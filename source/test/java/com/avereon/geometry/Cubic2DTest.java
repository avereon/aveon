package com.avereon.geometry;

import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class Cubic2DTest {

	@Test
	void testSubdivide() {
		Cubic2D g = new Cubic2D( 0, 0, 0.5, 0.5, 0.5, -0.5, 1, 0 );

		Cubic2D[] p = g.subdivide( 0.5 );
		Cubic2D[] q = Cubic2D.subdivide( g );
		Cubic2D[] r = Cubic2D.subdivide( g, 0.5 );

		assertThat( p[ 0 ], equalTo( new Cubic2D( 0, 0, 0.25, 0.25, 0.375, 0.125, 0.5, 0.0 ) ) );
		assertThat( p[ 1 ], equalTo( new Cubic2D( 0.5, 0, 0.625, -0.125, 0.75, -0.25, 1.0, 0.0 ) ) );

		assertThat( q[ 0 ], equalTo( new Cubic2D( 0, 0, 0.25, 0.25, 0.375, 0.125, 0.5, 0.0 ) ) );
		assertThat( q[ 1 ], equalTo( new Cubic2D( 0.5, 0, 0.625, -0.125, 0.75, -0.25, 1.0, 0.0 ) ) );

		assertThat( r[ 0 ], equalTo( new Cubic2D( 0, 0, 0.25, 0.25, 0.375, 0.125, 0.5, 0.0 ) ) );
		assertThat( r[ 1 ], equalTo( new Cubic2D( 0.5, 0, 0.625, -0.125, 0.75, -0.25, 1.0, 0.0 ) ) );
	}

	@Test
	void testSubdivideMore() {
		Cubic2D g = new Cubic2D( 0, 0, 0.5, 0.5, 0.5, -0.5, 1, 0 );
		for( double t = 0.1; t < 0.9; t += 0.1 ) {
			Cubic2D[] p = g.subdivide( t );
			Cubic2D[] q = Cubic2D.subdivide( g, t );
			assertThat( "t=" + t, p[ 0 ], equalTo( q[ 0 ] ) );
			assertThat( "t=" + t, p[ 1 ], equalTo( q[ 1 ] ) );
		}
	}

}
