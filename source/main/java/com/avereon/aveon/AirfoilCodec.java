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
		// Load the data into lines
		BufferedReader reader = new BufferedReader( new InputStreamReader( input, StandardCharsets.UTF_8 ) );
		List<String> result = new ArrayList<>();
		for( ; ; ) {
			String line = reader.readLine();
			if( line == null ) break;
			result.add( line );
		}

		// If line 3 is blank then it is Lednicer format otherwise it is Selig format
		boolean lednicer = TextUtil.isEmpty( result.get( 2 ) );
		Airfoil foil = lednicer ? loadLednicer( result ) : loadSelig( result );

		asset.setModel( foil );
	}

	@Override
	public void save( Asset asset, OutputStream output ) throws IOException {}

	Airfoil loadLednicer( List<String> lines ) {
		int index = 0;

		// First line is name
		String name = lines.get( index++ ).trim();

		// Second line is upper point count and lower point count
		index++;

		List<Point2D> upper = new ArrayList<>();
		index = loadPoints( index, lines, upper );

		List<Point2D> lower = new ArrayList<>();
		index = loadPoints( index, lines, lower );

		Airfoil airfoil = new Airfoil();
		airfoil.setName( name );
		airfoil.setUpper( upper );
		airfoil.setLower( lower );
		return airfoil;
	}

	Airfoil loadSelig( List<String> lines ) {
		int index = 0;

		// First line is name
		String name = lines.get( index++ ).trim();

		// tail to nose upper coords x,y
		List<Point2D> upper = new ArrayList<>();
		index = loadPoints( index, lines, upper, true );
		Collections.reverse( upper );

		// nose to tail lower coords x,y
		List<Point2D> lower = new ArrayList<>();
		index = loadPoints( index, lines, lower );

		Airfoil airfoil = new Airfoil();
		airfoil.setName( name );
		airfoil.setUpper( upper );
		airfoil.setLower( lower );
		return airfoil;
	}

	private int loadPoints( int index, List<String> lines, List<Point2D> upper ) {
		return loadPoints( index, lines, upper, false );
	}

	private int loadPoints( int index, List<String> lines, List<Point2D> upper, boolean stopOnTurn ) {
		// blank lines
		while( index < lines.size() && TextUtil.isEmpty( lines.get( index ) ) ) {
			index++;
		}

		// points
		String line;
		double x = 1;
		while( index < lines.size() && !TextUtil.isEmpty( line = lines.get( index ) ) ) {
			Point2D point = loadPoint( line );
			if( stopOnTurn && point.getX() > x ) {
				index--;
				break;
			}
			upper.add( point );
			x = point.getX();
			index++;
		}
		return index;
	}

	Point2D loadPoint( String line ) {
		String[] values = line.trim().split( "\\s+" );
		return new Point2D( Double.parseDouble( values[ 0 ] ), Double.parseDouble( values[ 1 ] ) );
	}

}
