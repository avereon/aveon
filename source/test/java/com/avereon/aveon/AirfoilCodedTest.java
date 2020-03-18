package com.avereon.aveon;

import javafx.geometry.Point2D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class AirfoilCodedTest {

	private static final String HT05_IL_LED = "http://airfoiltools.com/airfoil/lednicerdatfile?airfoil=ht05-il";

	private static final String HT05_IL_SEL = "http://airfoiltools.com/airfoil/seligdatfile?airfoil=ht05-il";

	private static final String CLARKY_IL_LED = "http://airfoiltools.com/airfoil/lednicerdatfile?airfoil=clarky-il";

	private static final String CLARKY_IL_SEL = "http://airfoiltools.com/airfoil/seligdatfile?airfoil=clarky-il";

	private AirfoilCodec codec;

	@BeforeEach
	void setup() {
		codec = new AirfoilCodec();
	}

	@Test
	void testLoadLednicer() throws Exception {
		List<String> lines = new ArrayList<>();

		lines.add( "CLARK Y AIRFOIL" );
		lines.add( "       61.       61." );
		lines.add( "" );
		lines.add( "  0.000000  0.000000" );
		lines.add( "  0.360000  0.091627" );
		lines.add( "  1.000000  0.000599" );
		lines.add( "" );
		lines.add( "  0.000000  0.000000" );
		lines.add( "  0.160000 -0.030255" );
		lines.add( "  1.000000 -0.000599" );

		Airfoil foil = codec.loadLednicer( lines );

		assertThat( foil.getName(), is( "CLARK Y AIRFOIL" ) );
		int index = 0;
		assertThat( foil.getUpper().get( index++ ), is( new Point2D( 0, 0 ) ) );
		assertThat( foil.getUpper().get( index++ ), is( new Point2D( 0.36, 0.091627 ) ) );
		assertThat( foil.getUpper().get( index++ ), is( new Point2D( 1, 0.000599 ) ) );
		assertThat( index, is( 3 ) );
		index = 0;
		assertThat( foil.getLower().get( index++ ), is( new Point2D( 0, 0 ) ) );
		assertThat( foil.getLower().get( index++ ), is( new Point2D( 0.160000, -0.030255 ) ) );
		assertThat( foil.getLower().get( index++ ), is( new Point2D( 1, -0.000599 ) ) );
		assertThat( index, is( 3 ) );
	}

	@Test
	void testLoadSelig() throws Exception {
		List<String> lines = new ArrayList<>();

		lines.add( "CLARK Y AIRFOIL" );
		lines.add( "  1.000000  0.000599" );
		lines.add( "  0.360000  0.091627" );
		lines.add( "  0.000000  0.000000" );
		lines.add( "  0.160000 -0.030255" );
		lines.add( "  1.000000 -0.000599" );

		Airfoil foil = codec.loadSelig( lines );

		assertThat( foil.getName(), is( "CLARK Y AIRFOIL" ) );
		int index = 0;
		assertThat( foil.getUpper().get( index++ ), is( new Point2D( 0, 0 ) ) );
		assertThat( foil.getUpper().get( index++ ), is( new Point2D( 0.36, 0.091627 ) ) );
		assertThat( foil.getUpper().get( index++ ), is( new Point2D( 1, 0.000599 ) ) );
		assertThat( index, is( 3 ) );
		index = 0;
		assertThat( foil.getLower().get( index++ ), is( new Point2D( 0, 0 ) ) );
		assertThat( foil.getLower().get( index++ ), is( new Point2D( 0.160000, -0.030255 ) ) );
		assertThat( foil.getLower().get( index++ ), is( new Point2D( 1, -0.000599 ) ) );
		assertThat( index, is( 3 ) );
	}

	@Test
	void testParsePoint() {
		assertThat( codec.loadPoint( "0 0" ), is( Point2D.ZERO ) );
		assertThat( codec.loadPoint( "  0.25  0.01" ), is( new Point2D( 0.25, 0.01 ) ) );
	}

}
