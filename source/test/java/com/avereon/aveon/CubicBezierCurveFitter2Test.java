package com.avereon.aveon;

import com.avereon.geometry.Cubic2D;
import com.avereon.geometry.Point2D;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CubicBezierCurveFitter2Test {

	@Test
	void testGenerateLeading() {
		Cubic2D goal = new Cubic2D( 0, 0, 0, 0.1, 0.2, 0.2, 0.4, 0.2 );

		// These points will be the station points to fit
		List<Point2D> stationPoints = goal.toPoints( 8 );

		CubicBezierCurveFitter fitter = new CubicBezierCurveFitter2( "TEST", stationPoints, CubicBezierCurveFitter.Hint.LEADING );
		Cubic2D curve = fitter.generate();

		//		System.err.println( "result: " + curve.b.y + " " + curve.c.x );

		// By percent
		assertThat( Math.abs( curve.b.y / 0.1 - 1 ) ).isCloseTo( 0, Offset.offset( 0.01 ) );
		assertThat( Math.abs( curve.c.x / 0.2 - 1 ) ).isCloseTo( 0, Offset.offset( 0.01 ) );

		//		assertThat( curve.a).isEqualTo( new Point2D( 0.0, 0.0 ) ) );
		//		assertThat( curve.b.x).isEqualTo( 0.0 ) );
		//		assertThat( curve.b.y).isCloseTo( 0.1, 0.01 ) );
		//		assertThat( curve.c.x).isCloseTo( 0.2, 0.02 ) );
		//		assertThat( curve.c.y).isEqualTo( 0.2 ) );
		//		assertThat( curve.d).isEqualTo( new Point2D( 0.4, 0.2 ) ) );

		assertThat( fitter.calcError( curve ) ).isLessThan( 1e-4 );
	}

	@Test
	void testGenerateLeadingClarkY() throws Exception {
		//Cubic2D goal = new Cubic2D( 0, 0, 0, 0.1, 0.2, 0.2, 0.4, 0.2 );
		InputStream input = getClass().getResourceAsStream( "clarky.led.txt" );
		Airfoil airfoil = AirfoilStationPointCodec.loadStationPoints( input );

		List<Point2D> le = airfoil.getUpperPointGroups().get(0);
		CubicBezierCurveFitter fitter = new CubicBezierCurveFitter2( "TEST", le, CubicBezierCurveFitter.Hint.LEADING );
		Cubic2D curve = fitter.generate();

		// By percent
//		assertThat( Math.abs( curve.b.y / (0.5 * 0.091627) - 1 ) ).isCloseTo( 0, Offset.offset( 0.01 ) );
//		assertThat( Math.abs( curve.c.x / (0.5 * 0.36) - 1 ) ).isCloseTo( 0, Offset.offset( 0.01 ) );
//
//		assertThat( fitter.calcError( curve ) ).isLessThan( 1e-4 );
	}

}
