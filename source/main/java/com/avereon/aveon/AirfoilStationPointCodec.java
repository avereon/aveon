package com.avereon.aveon;

import com.avereon.geometry.Point2D;
import com.avereon.util.TextUtil;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.Codec;
import lombok.CustomLog;

import java.io.*;
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
	}

	@Override
	public void save( Asset asset, OutputStream output ) throws IOException {}

	Airfoil loadStationPoints( InputStream input ) throws IOException {
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

	Airfoil loadLednicer( List<String> lines ) {
		String name = lines.get( 0 ).trim();
		Point2D counts = loadStationPoint( lines.get( 1 ) );
		int upperCount = (int)counts.getX();
		int lowerCount = (int)counts.getY();
		List<Point2D> upper = loadStationPoints( lines, 2, upperCount );
		List<Point2D> lower = loadStationPoints( lines, 2 + upperCount, lowerCount );
		return createAirfoilFromStationPoints( name, upper, lower );
	}

	Airfoil loadSelig( List<String> lines ) {
		String name = lines.get( 0 ).trim();
		List<Point2D> upper = loadStationPoints( lines, 1, -1 );
		Collections.reverse( upper );
		List<Point2D> lower = loadStationPoints( lines, upper.size(), -1 );
		return createAirfoilFromStationPoints( name, upper, lower );
	}

	private Airfoil createAirfoilFromStationPoints( String name, List<Point2D> upper, List<Point2D> lower ) {
		Airfoil airfoil = new Airfoil();
		airfoil.setName( name );
		airfoil.setUpperStationPoints( upper );
		airfoil.setLowerStationPoints( lower );
		airfoil.analyzePoints();
		return airfoil;
	}

	private List<Point2D> loadStationPoints( List<String> lines, int start, int count ) {
		List<Point2D> points = new ArrayList<>();

		double x = 1;
		int index = start;
		int extent = start + count;
		while( index < lines.size() && (count < 0 || index < extent) ) {
			Point2D point = loadStationPoint( lines.get( index ) );
			if( start == 1 && point.getX() > x ) break;
			points.add( point );
			x = point.getX();
			index++;
		}

		return points;
	}

	Point2D loadStationPoint( String line ) {
		double tolerance = 0.001;
		String[] values = line.trim().split( "\\s+" );
		double x = Double.parseDouble( values[ 0 ] );
		double y = Double.parseDouble( values[ 1 ] );

		if( Math.abs( x ) < tolerance ) x = 0.0;
		if( x == 0.0 ) y = 0.0;

		return new Point2D( x, y );
	}

}
