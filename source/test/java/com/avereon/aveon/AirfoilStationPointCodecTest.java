package com.avereon.aveon;

import javafx.geometry.Point2D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AirfoilStationPointCodecTest {

	private AirfoilStationPointCodec codec;

	@BeforeEach
	void setup() {
		codec = new AirfoilStationPointCodec();
	}

	@Test
	void testLoadWithNiceAirfoil() throws Exception {
		Airfoil airfoil = loadAirfoil( "clarky.led.txt" );
		assertNotNull( airfoil );
		assertThat( airfoil.getName(), is( "CLARK Y AIRFOIL" ) );

		assertThat( airfoil.getMaxY(), is( 0.091627 ) );
		assertThat( airfoil.getMinY(), is( -0.030255 ) );
	}

	@Test
	void testLoadWithMultiInflectionAirfoil() throws Exception {
		Airfoil airfoil = loadAirfoil( "ht05.led.txt" );
		assertNotNull( airfoil );
		assertThat( airfoil.getName(), is( "HT05" ) );
		assertThat( airfoil.getMaxY(), is( 0.024068 ) );
		assertThat( airfoil.getMinY(), is( -0.024047 ) );
	}

	@Test
	void testLoadWithAsymmetricStationAirfoil() throws Exception {
		Airfoil airfoil = loadAirfoil( "e376.led.txt" );
		assertNotNull( airfoil );
		assertThat( airfoil.getName(), is( "EPPLER 376 AIRFOIL" ) );
		assertThat( airfoil.getMaxY(), is( 0.091800 ) );
		assertThat( airfoil.getMinY(), is( -0.002940 ) );
	}

	@Test
	void testLoadLednicer() throws Exception {
		List<String> lines = new ArrayList<>();

		lines.add( "TEST AIRFOIL" );
		lines.add( "       3.       3." );
		lines.add( "  0.000000  0.000000" );
		lines.add( "  0.360000  0.091627" );
		lines.add( "  1.000000  0.000599" );
		lines.add( "  0.000000  0.000000" );
		lines.add( "  0.160000 -0.030255" );
		lines.add( "  1.000000 -0.000599" );

		Airfoil foil = codec.loadLednicer( lines );

		assertThat( foil.getName(), is( "TEST AIRFOIL" ) );
		int index = 0;
		assertThat( foil.getUpperStationPoints().get( index++ ), is( new Point2D( 0, 0 ) ) );
		assertThat( foil.getUpperStationPoints().get( index++ ), is( new Point2D( 0.36, 0.091627 ) ) );
		assertThat( foil.getUpperStationPoints().get( index++ ), is( new Point2D( 1, 0.000599 ) ) );
		assertThat( index, is( 3 ) );
		index = 0;
		assertThat( foil.getLowerStationPoints().get( index++ ), is( new Point2D( 0, 0 ) ) );
		assertThat( foil.getLowerStationPoints().get( index++ ), is( new Point2D( 0.160000, -0.030255 ) ) );
		assertThat( foil.getLowerStationPoints().get( index++ ), is( new Point2D( 1, -0.000599 ) ) );
		assertThat( index, is( 3 ) );
	}

	@Test
	void testLoadSelig() throws Exception {
		List<String> lines = new ArrayList<>();

		lines.add( "TEST AIRFOIL" );
		lines.add( "  1.000000  0.000599" );
		lines.add( "  0.360000  0.091627" );
		lines.add( "  0.000000  0.000000" );
		lines.add( "  0.160000 -0.030255" );
		lines.add( "  1.000000 -0.000599" );

		Airfoil foil = codec.loadSelig( lines );

		assertThat( foil.getName(), is( "TEST AIRFOIL" ) );
		int index = 0;
		assertThat( foil.getUpperStationPoints().get( index++ ), is( new Point2D( 0, 0 ) ) );
		assertThat( foil.getUpperStationPoints().get( index++ ), is( new Point2D( 0.36, 0.091627 ) ) );
		assertThat( foil.getUpperStationPoints().get( index++ ), is( new Point2D( 1, 0.000599 ) ) );
		assertThat( index, is( 3 ) );
		index = 0;
		assertThat( foil.getLowerStationPoints().get( index++ ), is( new Point2D( 0, 0 ) ) );
		assertThat( foil.getLowerStationPoints().get( index++ ), is( new Point2D( 0.160000, -0.030255 ) ) );
		assertThat( foil.getLowerStationPoints().get( index++ ), is( new Point2D( 1, -0.000599 ) ) );
		assertThat( index, is( 3 ) );
	}

	@Test
	void testParsePoint() {
		assertThat( codec.loadStationPoint( "0 0" ), is( Point2D.ZERO ) );
		assertThat( codec.loadStationPoint( "  0.25  0.01" ), is( new Point2D( 0.25, 0.01 ) ) );
	}

	private Airfoil loadAirfoil( String name ) throws Exception {
		try( InputStream input = getClass().getResource( name ).openStream() ) {
			return codec.loadStationPoints( input );
		}
	}

}
