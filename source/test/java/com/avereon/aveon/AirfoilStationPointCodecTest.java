package com.avereon.aveon;

import com.avereon.geometry.Point2D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AirfoilStationPointCodecTest {

	private AirfoilStationPointCodec codec;

	@BeforeEach
	void setup() {
		codec = new AirfoilStationPointCodec();
	}

	@Test
	void testLoadWithNiceAirfoil() throws Exception {
		Airfoil airfoil = loadAirfoil( "clarky.led.txt" );
		assertThat( airfoil ).isNotNull();
		assertThat( airfoil.getName() ).isEqualTo( "CLARK Y AIRFOIL" );

		assertThat( airfoil.getMaxY() ).isEqualTo( 0.091627 );
		assertThat( airfoil.getMinY() ).isEqualTo( -0.030255 );
	}

	@Test
	void testLoadWithMultiInflectionAirfoil() throws Exception {
		Airfoil airfoil = loadAirfoil( "ht05.led.txt" );
		assertThat( airfoil ).isNotNull();
		assertThat( airfoil.getName() ).isEqualTo( "HT05" );
		assertThat( airfoil.getMaxY() ).isEqualTo( 0.024068 );
		assertThat( airfoil.getMinY() ).isEqualTo( -0.024047 );
	}

	@Test
	void testLoadWithAsymmetricStationAirfoil() throws Exception {
		Airfoil airfoil = loadAirfoil( "e376.led.txt" );
		assertThat( airfoil ).isNotNull();
		assertThat( airfoil.getName() ).isEqualTo( "EPPLER 376 AIRFOIL" );
		assertThat( airfoil.getMaxY() ).isEqualTo( 0.091800 );
		assertThat( airfoil.getMinY() ).isEqualTo( -0.002940 );
	}

	@Test
	void testLoadLednicer() {
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

		assertThat( foil.getName() ).isEqualTo( "TEST AIRFOIL" );
		int index = 0;
		assertThat( foil.getUpperStationPoints().get( index++ ) ).isEqualTo( new Point2D( 0, 0 ) );
		assertThat( foil.getUpperStationPoints().get( index++ ) ).isEqualTo( new Point2D( 0.36, 0.091627 ) );
		assertThat( foil.getUpperStationPoints().get( index++ ) ).isEqualTo( new Point2D( 1, 0.000599 ) );
		assertThat( index ).isEqualTo( 3 );
		index = 0;
		assertThat( foil.getLowerStationPoints().get( index++ ) ).isEqualTo( new Point2D( 0, 0 ) );
		assertThat( foil.getLowerStationPoints().get( index++ ) ).isEqualTo( new Point2D( 0.160000, -0.030255 ) );
		assertThat( foil.getLowerStationPoints().get( index++ ) ).isEqualTo( new Point2D( 1, -0.000599 ) );
		assertThat( index ).isEqualTo( 3 );
	}

	@Test
	void testLoadSelig() {
		List<String> lines = new ArrayList<>();

		lines.add( "TEST AIRFOIL" );
		lines.add( "  1.000000  0.000599" );
		lines.add( "  0.360000  0.091627" );
		lines.add( "  0.000000  0.000000" );
		lines.add( "  0.160000 -0.030255" );
		lines.add( "  1.000000 -0.000599" );

		Airfoil foil = codec.loadSelig( lines );

		assertThat( foil.getName() ).isEqualTo( "TEST AIRFOIL" );
		int index = 0;
		assertThat( foil.getUpperStationPoints().get( index++ ) ).isEqualTo( new Point2D( 0, 0 ) );
		assertThat( foil.getUpperStationPoints().get( index++ ) ).isEqualTo( new Point2D( 0.36, 0.091627 ) );
		assertThat( foil.getUpperStationPoints().get( index++ ) ).isEqualTo( new Point2D( 1, 0.000599 ) );
		assertThat( index ).isEqualTo( 3 );
		index = 0;
		assertThat( foil.getLowerStationPoints().get( index++ ) ).isEqualTo( new Point2D( 0, 0 ) );
		assertThat( foil.getLowerStationPoints().get( index++ ) ).isEqualTo( new Point2D( 0.160000, -0.030255 ) );
		assertThat( foil.getLowerStationPoints().get( index++ ) ).isEqualTo( new Point2D( 1, -0.000599 ) );
		assertThat( index ).isEqualTo( 3 );
	}

	@Test
	void testParsePoint() {
		assertThat( codec.loadStationPoint( "0 0" ) ).isEqualTo( Point2D.ZERO );
		assertThat( codec.loadStationPoint( "  0.25  0.01  " ) ).isEqualTo( new Point2D( 0.25, 0.01 ) );
	}

	@Test
	void testParsePointCleanup() {
		assertThat( codec.loadStationPoint( "  0.0001  0.01  " ) ).isEqualTo( Point2D.ZERO );
	}

	private Airfoil loadAirfoil( String name ) throws Exception {
		try( InputStream input = getClass().getResource( name ).openStream() ) {
			return codec.loadStationPoints( input );
		}
	}

}
