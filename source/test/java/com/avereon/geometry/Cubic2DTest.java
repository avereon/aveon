package com.avereon.geometry;

import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class Cubic2DTest {

	@Test
	void testSubdivideWithValue() {
		Cubic2D g = new Cubic2D( 0, 0, 0.5, 0.5, 0.5, -0.5, 1, 0 );
		Cubic2D[] p = g.subdivide( 0.5 );

		assertThat( p[ 0 ], is( new Cubic2D( 0, 0, 0.25, 0.25, 0.375, 0.125, 0.5, 0.0 ) ) );
		assertThat( p[ 1 ], is( new Cubic2D( 0.5, 0, 0.625, -0.125, 0.75, -0.25, 1.0, 0.0 ) ) );
	}

	@Test
	void testSubdivide() {
		Cubic2D g = new Cubic2D( 0, 0, 0.5, 0.5, 0.5, -0.5, 1, 0 );
		Cubic2D[] q = Cubic2D.subdivide( g );

		assertThat( q[ 0 ], is( new Cubic2D( 0, 0, 0.25, 0.25, 0.375, 0.125, 0.5, 0.0 ) ) );
		assertThat( q[ 1 ], is( new Cubic2D( 0.5, 0, 0.625, -0.125, 0.75, -0.25, 1.0, 0.0 ) ) );
	}

}
