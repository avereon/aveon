package com.avereon.aveon;

import com.avereon.geometry.Cubic2D;
import com.avereon.geometry.Point2D;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class CubicBezierCurveFitterTest {

	@Test
	void testCalcErrorByOffset() {
		List<Point2D> a = List.of( new Point2D( 0, 0 ), new Point2D( 1, 0.5 ), new Point2D( 2, 0.5 ), new Point2D( 3, 0 ) );
		List<Point2D> b = List.of( new Point2D( 0, 0 ), new Point2D( 1, 1 ), new Point2D( 2, 1.0 ), new Point2D( 3, 0 ) );
		CubicBezierCurveFitter fitter = new CubicBezierCurveFitter( "TEST", a, CubicBezierCurveFitter.Hint.LEADING );
		assertThat( fitter.calcErrorByOffset( b ), closeTo( Math.sqrt( 0.5 ), 1e-15 ) );
	}

	@Test
	void testCalcErrorByOffset2() {
		Cubic2D goal = new Cubic2D( 0, 0, 0, 0.1, 0.2, 0.2, 0.4, 0.2 );
		List<Point2D> a = goal.toPoints( 8 );
		List<Point2D> b = goal.toPoints( 12 );
		CubicBezierCurveFitter fitter = new CubicBezierCurveFitter( "TEST", a, CubicBezierCurveFitter.Hint.LEADING );
		assertThat( fitter.calcErrorByOffset( b ), is( 0.002291233644431911 ) );
	}

	@Test
	void testCalcHeadError() {
		List<Point2D> a = List.of( new Point2D( 0, 0 ), new Point2D( 1, 0.5 ), new Point2D( 2, 0.5 ), new Point2D( 3, 0 ) );
		List<Point2D> b = List.of( new Point2D( 0, 0 ), new Point2D( 1, 1 ), new Point2D( 2, 1.1 ), new Point2D( 3, 0 ) );
		CubicBezierCurveFitter fitter = new CubicBezierCurveFitter( "TEST", a, CubicBezierCurveFitter.Hint.LEADING );
		assertThat( fitter.calcHeadError( b ), is( 0.25157457023622637 ) );
	}

	@Test
	void testCalcTailError() {
		List<Point2D> a = List.of( new Point2D( 0, 0 ), new Point2D( 1, 0.5 ), new Point2D( 2, 0.5 ), new Point2D( 3, 0 ) );
		List<Point2D> b = List.of( new Point2D( 0, 0 ), new Point2D( 1, 1 ), new Point2D( 2, 1.1 ), new Point2D( 3, 0 ) );
		CubicBezierCurveFitter fitter = new CubicBezierCurveFitter( "TEST", a, CubicBezierCurveFitter.Hint.LEADING );
		assertThat( fitter.calcTailError( b ), is( 0.2620666803629938 ) );
	}

	@Test
	void testGenerateLeading() {
		Cubic2D goal = new Cubic2D( 0, 0, 0, 0.15, 0.2, 0.2, 0.4, 0.2 );

		// These points will be the station points to fit
		List<Point2D> stationPoints = goal.toPoints( 8 );

		CubicBezierCurveFitter fitter = new CubicBezierCurveFitter( "TEST", stationPoints, CubicBezierCurveFitter.Hint.LEADING );
		Cubic2D curve = fitter.generate();

//		assertThat( fitter.calcError( curve ), lessThan( 1e-15 ) );

		assertThat( curve.a, is( new Point2D( 0.0, 0.0 ) ) );
		assertThat( curve.b.x, is( 0.0 ) );
		assertThat( curve.b.y, closeTo( 0.1, 0.0001 ) );
		assertThat( curve.c.x, closeTo( 0.2, 0.0001 ) );
		assertThat( curve.c.y, is( 0.2 ) );
		assertThat( curve.d, is( new Point2D( 0.4, 0.2 ) ) );
	}

}
