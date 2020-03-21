package com.avereon.aveon;

import com.avereon.util.Log;
import com.avereon.util.TextUtil;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.Codec;
import javafx.geometry.Point2D;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AirfoilCodec extends Codec {

	private static final System.Logger log = Log.get();

	@Override
	public String getKey() {
		return "com.avereon.aveon.codec.airfoil";
	}

	@Override
	public String getName() {
		return "Airfoil";
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
		asset.setModel( load( input ) );
	}

	@Override
	public void save( Asset asset, OutputStream output ) throws IOException {}

	Airfoil load( InputStream input ) throws IOException {
		// Load the data into lines
		BufferedReader reader = new BufferedReader( new InputStreamReader( input, StandardCharsets.UTF_8 ) );
		List<String> result = new ArrayList<>();
		for( ; ; ) {
			String line = reader.readLine();
			if( line == null ) break;
			if( !TextUtil.isEmpty( line ) ) result.add( line );
		}

		Point2D point = loadPoint( result.get( 1 ) );
		boolean lednicer = point.getX() > 1;

		Airfoil foil = lednicer ? loadLednicer( result, (int)point.getX(), (int)point.getY() ) : loadSelig( result );
		foil.analyze();

		return foil;
	}

	Airfoil loadLednicer( List<String> lines, int upperCount, int lowerCount ) {
		String name = lines.get( 0 ).trim();
		List<Point2D> upper = loadPoints( lines, 2, upperCount );
		List<Point2D> lower = loadPoints( lines, upperCount + 2, lowerCount );
		return createAirfoil( name, upper, lower );
	}

	Airfoil loadSelig( List<String> lines ) {
		String name = lines.get( 0 ).trim();
		List<Point2D> upper = loadPoints( lines, 1, -1 );
		Collections.reverse( upper );
		List<Point2D> lower = loadPoints( lines, upper.size(), -1 );
		return createAirfoil( name, upper, lower );
	}

	private Airfoil createAirfoil( String name, List<Point2D> upper, List<Point2D> lower ) {
		Airfoil airfoil = new Airfoil();
		airfoil.setName( name );
		airfoil.setUpper( upper );
		airfoil.setLower( lower );
		return airfoil;
	}

	private List<Point2D> loadPoints( List<String> lines, int start, int count ) {
		List<Point2D> points = new ArrayList<>();

		double x = 1;
		int index = start;
		int extent = start + count;
		while( index < lines.size() && (count < 0 || index < extent) ) {
			Point2D point = loadPoint( lines.get( index ) );
			if( start == 1 && point.getX() > x ) break;
			points.add( point );
			x = point.getX();
			index++;
		}

		return points;
	}

	Point2D loadPoint( String line ) {
		String[] values = line.trim().split( "\\s+" );
		return new Point2D( Double.parseDouble( values[ 0 ] ), Double.parseDouble( values[ 1 ] ) );
	}

}
