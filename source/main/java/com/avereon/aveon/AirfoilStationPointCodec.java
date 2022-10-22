package com.avereon.aveon;

import com.avereon.geometry.Point2D;
import com.avereon.util.TextUtil;
import com.avereon.util.UriUtil;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.Codec;
import lombok.CustomLog;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@CustomLog
public class AirfoilStationPointCodec extends Codec {

	@Override
	public String getKey() {
		return "com.avereon.aveon.codec.airfoil.station.points";
	}

	@Override
	public String getName() {
		return "Airfoil Station Points";
	}

	@Override
	public boolean canLoad() {
		return true;
	}

	@Override
	public boolean canSave() {
		return false;
	}

	@Override
	public void load( Asset asset, InputStream input ) throws IOException {
		asset.setModel( loadStationPoints( input ) );

		URI uri = asset.getUri();
		if( uri.toString().startsWith( "http://airfoiltools.com/airfoil/lednicerdatfile" ) ) {
			asset.setName( UriUtil.parseQuery( uri.getQuery() ).get( "airfoil" ) );
		}
	}

	@Override
	public void save( Asset asset, OutputStream output ) throws IOException {}

	public static Airfoil loadStationPoints( InputStream input ) throws IOException {
		// Load the data into lines skipping any blank lines
		BufferedReader reader = new BufferedReader( new InputStreamReader( input, StandardCharsets.UTF_8 ) );
		List<String> result = new ArrayList<>();
		for( ; ; ) {
			String line = reader.readLine();
			if( line == null ) break;
			if( !TextUtil.isEmpty( line ) ) result.add( line );
		}

		// If the first point has values greater than one then
		// it contains the point counts and
		// the file is assumed to be in Lednicer format
		Point2D point = loadStationPoint( result.get( 1 ) );
		boolean lednicer = point.getX() > 1;

		return lednicer ? loadLednicer( result ) : loadSelig( result );
	}

	static Airfoil loadLednicer( List<String> lines ) {
		String name = lines.get( 0 ).trim();
		Point2D counts = loadStationPoint( lines.get( 1 ) );
		int upperCount = (int)counts.getX();
		int lowerCount = (int)counts.getY();
		List<Point2D> upper = loadStationPoints( lines, 2, upperCount );
		List<Point2D> lower = loadStationPoints( lines, 2 + upperCount, lowerCount );
		return createAirfoilFromStationPoints( name, upper, lower );
	}

	static Airfoil loadSelig( List<String> lines ) {
		String name = lines.get( 0 ).trim();
		List<Point2D> upper = loadStationPoints( lines, 1, -1 );
		Collections.reverse( upper );
		List<Point2D> lower = loadStationPoints( lines, upper.size(), -1 );
		return createAirfoilFromStationPoints( name, upper, lower );
	}

	/**
	 * Create an airfoil from station point data.
	 *
	 * @param name The name of the airfoil
	 * @param upper The upper points from leading edge to trailing edge
	 * @param lower The lower points from leading edge to trailing edge
	 * @return The airfoil
	 */
	private static Airfoil createAirfoilFromStationPoints( String name, List<Point2D> upper, List<Point2D> lower ) {
		Airfoil airfoil = new Airfoil();
		airfoil.setName( name );
		airfoil.setDefinitionPoints( upper, lower );
		return airfoil;
	}

	private static List<Point2D> loadStationPoints( List<String> lines, int start, int count ) {
		List<Point2D> points = new ArrayList<>();

		double x = 1;
		int index = start;
		int extent = start + count;
		while( index < lines.size() && (count < 0 || index < extent) ) {
			Point2D point = loadStationPoint( lines.get( index ) );

			// If the points started at the TE and have now wrapped the LE...break
			if( start == 1 && point.getX() > x ) break;

			// Avoid repeat points
			if( points.size() == 0 || !points.get( points.size() - 1 ).equals( point ) ) points.add( point );

			x = point.getX();
			index++;
		}

		return points;
	}

	static Point2D loadStationPoint( String line ) {
		String[] values = line.trim().split( "\\s+" );
		double x = Double.parseDouble( values[ 0 ] );
		double y = Double.parseDouble( values[ 1 ] );
		return new Point2D( x, y );
	}

}
