package com.avereon.aveon;

import com.avereon.geometry.Cubic2D;
import com.avereon.geometry.Point2D;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class CubicBezierCurveFitterTest {

	@Test
	void testGenerateLeading() {
		// These points will be the station points to fit
		List<Point2D> stationPoints = new Cubic2D( 0, 0, 0, 0.1, 0.2, 0.2, 0.4, 0.2 ).toPoints( 8 );

		Cubic2D curve = new CubicBezierCurveFitter( "TEST", stationPoints, CubicBezierCurveFitter.Hint.LEADING ).generate();

		//assertThat( CubicBezierCurveFitter.calcError( stationPoints, curve ), lessThanOrEqualTo( 0.0) );

		assertThat( curve.a, is( new Point2D( 0.0, 0.0 ) ) );
		assertThat( curve.b.x, is( 0.0 ) );
		assertThat( Math.abs( 0.1 - curve.b.y ), lessThanOrEqualTo( 0.01 ) );
		assertThat( Math.abs( 0.2 - curve.c.x ), lessThanOrEqualTo( 0.01 ) );
		assertThat( curve.c.y, is( 0.2 ) );
		assertThat( curve.d, is( new Point2D( 0.4, 0.2 ) ) );
	}

}
