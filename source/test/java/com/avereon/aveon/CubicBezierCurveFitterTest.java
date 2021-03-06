package com.avereon.aveon;

import com.avereon.geometry.Cubic2D;
import com.avereon.geometry.Geometry2D;
import com.avereon.geometry.Point2D;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

class CubicBezierCurveFitterTest {

	@Test
	void testGenerateLeading() {
		Cubic2D goal = new Cubic2D( 0, 0, 0, 0.1, 0.1, 0.2, 0.4, 0.2 );

		// These points will be the station points to fit
		List<Point2D> stationPoints = goal.toPoints( 8 );

		CubicBezierCurveFitter fitter = new CubicBezierCurveFitter( "TEST", stationPoints, CubicBezierCurveFitter.Hint.LEADING );
		Cubic2D curve = fitter.generate();

		//		System.err.println( "result: " + curve.b.y + " " + curve.c.x );

		// By percent
		//assertThat( Math.abs( curve.b.y / 0.1 - 1 ), closeTo( 0, 0.0001 ) );
		//assertThat( Math.abs( curve.c.x / 0.2 - 1 ), closeTo( 0, 0.0001 ) );

//		assertThat( curve.a, is( new Point2D( 0.0, 0.0 ) ) );
//		assertThat( curve.b.x, is( 0.0 ) );
//		assertThat( curve.b.y, closeTo( 0.1, 0.01 ) );
//		assertThat( curve.c.x, closeTo( 0.2, 0.02 ) );
//		assertThat( curve.c.y, is( 0.2 ) );
//		assertThat( curve.d, is( new Point2D( 0.4, 0.2 ) ) );

		//		assertThat( fitter.calcError( curve ), lessThan( 1e-15 ) );
	}

	@Test
	void testCalcErrorByDistance() {
		List<Point2D> a = List.of( new Point2D( 0, 0 ), new Point2D( 1, 0.5 ), new Point2D( 2, 0.5 ), new Point2D( 3, 0 ) );
		List<Point2D> b = List.of( new Point2D( 0, 0 ), new Point2D( 1, 1 ), new Point2D( 2, 1.0 ), new Point2D( 3, 0 ) );
		CubicBezierCurveFitter fitterA = new CubicBezierCurveFitter( "TEST", a, CubicBezierCurveFitter.Hint.LEADING );
		assertThat( fitterA.calcErrorByDistance( b ), closeTo( 0.0, 1e-15 ) );
		CubicBezierCurveFitter fitterB = new CubicBezierCurveFitter( "TEST", b, CubicBezierCurveFitter.Hint.LEADING );
		assertThat( fitterB.calcErrorByDistance( a ), closeTo( Math.sqrt( 0.5 ), 1e-15 ) );

		Cubic2D goal = new Cubic2D( 0, 0, 0, 0.1, 0.2, 0.2, 0.4, 0.2 );
		List<Point2D> c = goal.toPoints( 8 );
		List<Point2D> d = goal.toPoints( 12 );
		CubicBezierCurveFitter fitterC = new CubicBezierCurveFitter( "TEST", c, CubicBezierCurveFitter.Hint.LEADING );
		assertThat( fitterC.calcErrorByDistance( d ), is( 0.00921630634219396 ) );
		CubicBezierCurveFitter fitterD = new CubicBezierCurveFitter( "TEST", d, CubicBezierCurveFitter.Hint.LEADING );
		assertThat( fitterD.calcErrorByDistance( c ), is( 0.002291233644431911 ) );
	}

	@Test
	void testCalcErrorByOffsetForHead() {
		List<Point2D> a = List.of( new Point2D( 0, 0 ), new Point2D( 1, 0.5 ), new Point2D( 2, 0.5 ), new Point2D( 3, 0 ) );
		List<Point2D> b = List.of( new Point2D( 0, 0 ), new Point2D( 1, 1 ), new Point2D( 2, 1 ), new Point2D( 3, 0 ) );

		CubicBezierCurveFitter fitter = new CubicBezierCurveFitter( "TEST", a, CubicBezierCurveFitter.Hint.LEADING );

		// This test intentionally uses getPointLineDistance instead of getPointLineOffset
		double aDistance1 = Geometry2D.getPointLineDistance( Point2D.of( 1, 0.5 ), Point2D.of( 0, 0 ), Point2D.of( 1, 1 ) );
		double aDistance2 = Geometry2D.getPointLineDistance( Point2D.of( 2, 0.5 ), Point2D.of( 2, 1 ), Point2D.of( 3, 0 ) );
		double aWeight1 = Geometry2D.calcCubicBasisEffect( 1, 1.0 / 3.0 );
		double aWeight2 = Geometry2D.calcCubicBasisEffect( 1, 2.0 / 3.0 );
		double aError = aDistance1 * aWeight1 + aDistance2 * aWeight2;
		// The error should be negative comparing a to b
		assertThat( fitter.calcErrorByOffset( a, b, 1 ), is( -aError ) );

		// This test intentionally used just point distance
		double bDistance1 = Point2D.of( 1, 1 ).distance( Point2D.of( 1, 0.5 ) );
		double bDistance2 = Point2D.of( 2, 1 ).distance( Point2D.of( 2, 0.5 ) );
		double bWeight1 = Geometry2D.calcCubicBasisEffect( 1, 1.0 / 3.0 );
		double bWeight2 = Geometry2D.calcCubicBasisEffect( 1, 2.0 / 3.0 );
		double bError = bDistance1 * bWeight1 + bDistance2 * bWeight2;
		// The error should be positive comparing b to a
		assertThat( fitter.calcErrorByOffset( b, a, 1 ), is( bError ) );
	}

	@Test
	void testCalcErrorByOffsetForTail() {
		List<Point2D> a = List.of( new Point2D( 0, 0 ), new Point2D( 1, 0.5 ), new Point2D( 2, 0.5 ), new Point2D( 3, 0 ) );
		List<Point2D> b = List.of( new Point2D( 0, 0 ), new Point2D( 1, 1 ), new Point2D( 2, 1 ), new Point2D( 3, 0 ) );
		CubicBezierCurveFitter fitter = new CubicBezierCurveFitter( "TEST", a, CubicBezierCurveFitter.Hint.LEADING );

		// This test intentionally uses getPointLineDistance instead of getPointLineOffset
		double aDistance1 = Geometry2D.getPointLineDistance( Point2D.of( 1, 0.5 ), Point2D.of( 0, 0 ), Point2D.of( 1, 1 ) );
		double aDistance2 = Geometry2D.getPointLineDistance( Point2D.of( 2, 0.5 ), Point2D.of( 2, 1 ), Point2D.of( 3, 0 ) );
		double aWeight1 = Geometry2D.calcCubicBasisEffect( 2, 1.0 / 3.0 );
		double aWeight2 = Geometry2D.calcCubicBasisEffect( 2, 2.0 / 3.0 );
		double aError = aDistance1 * aWeight1 + aDistance2 * aWeight2;
		// The error should be negative comparing a to b
		assertThat( fitter.calcErrorByOffset( a, b, 2 ), is( -aError - 0.00000000000000003 ) );

		// This test intentionally used just point distance
		double bDistance1 = Point2D.of( 1, 1 ).distance( Point2D.of( 1, 0.5 ) );
		double bDistance2 = Point2D.of( 2, 1 ).distance( Point2D.of( 2, 0.5 ) );
		double bWeight1 = Geometry2D.calcCubicBasisEffect( 2, 1.0 / 3.0 );
		double bWeight2 = Geometry2D.calcCubicBasisEffect( 2, 2.0 / 3.0 );
		double bError = bDistance1 * bWeight1 + bDistance2 * bWeight2;
		// The error should be positive comparing b to a
		assertThat( fitter.calcErrorByOffset( b, a, 2 ), is( bError ) );
	}

}
