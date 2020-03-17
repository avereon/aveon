package com.avereon.aveon;

import com.avereon.util.TextUtil;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.Codec;
import javafx.geometry.Point2D;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class AirfoilCodec extends Codec {

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
		if( TextUtil.isEmpty( result.get( 2 ) ) ) {
			loadLednicer( asset, result );
		} else {
			loadSelig( asset, result );
		}
	}

	@Override
	public void save( Asset asset, OutputStream output ) throws IOException {}

	Airfoil loadLednicer( Asset asset, List<String> lines ) {
		// First line is name
		String name = lines.get( 0 ).trim();

		// Second line is upper point count and lower point count
		// blank line

		// nose to tail upper coords x,y
		String line;
		int index = 0;
		List<Point2D> upper = new ArrayList<>();
		while( !TextUtil.isEmpty( line = lines.get( index++ ) ) ) {
			Point2D point = parsePoint( line );
		}

		// blank line
		index++;

		// nose to tail lower coords x,y
		List<Point2D> lower = new ArrayList<>();
		while( !TextUtil.isEmpty( line = lines.get( index++ ) ) ) {
			Point2D point = parsePoint( line );
		}

		Airfoil airfoil = new Airfoil();
		airfoil.setName( name );
		airfoil.setUpper( upper );
		airfoil.setLower( lower );
		return airfoil;
	}

	Airfoil loadSelig( Asset asset, List<String> lines ) {
		// First line is name
		// tail to nose upper coords x,y
		// there should be one point at 0,0 (but not exactly at 0,0)
		// nose to tail lower coords x,y

		Airfoil airfoil = new Airfoil();
		return airfoil;
	}

	Point2D parsePoint( String line ) {
		String[] values = line.split( " " );
		return new Point2D( Double.parseDouble( values[ 0 ] ), Double.parseDouble( values[ 1 ] ) );
	}

}
