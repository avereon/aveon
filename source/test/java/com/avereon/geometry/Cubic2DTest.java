package com.avereon.geometry;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class Cubic2DTest {

	@Test
	void toPoints() {
		Cubic2D curve = new Cubic2D( 0, 0, 0, 0.5, 0.5, 1, 1, 1 );

		// Eight segments should produce nine points
		List<Point2D> curvePoints = curve.toPoints( 8 );
		//		for( Point2D point : curvePoints ) {
		//			System.out.println( "point=" + point );
		//		}

		assertThat( curvePoints.get( 0 ) ).isEqualTo( new Point2D( 0, 0 ) );
		//		point=Point2D[x=0.0224609375,y=0.1865234375]
		//		point=Point2D[x=0.0859375,y=0.3671875]
		//		point=Point2D[x=0.1845703125,y=0.5361328125]
		assertThat( curvePoints.get( 4 ) ).isEqualTo( new Point2D( 0.3125, 0.6875 ) );
		//		point=Point2D[x=0.4638671875,y=0.8154296875]
		//		point=Point2D[x=0.6328125,y=0.9140625]
		//		point=Point2D[x=0.8134765625,y=0.9775390625]
		assertThat( curvePoints.get( 8 ) ).isEqualTo( new Point2D( 1, 1 ) );

		assertThat( curvePoints.size() ).isEqualTo( 9 );
	}

	@Test
	void testSubdivideWithValue() {
		Cubic2D g = new Cubic2D( 0, 0, 0.5, 0.5, 0.5, -0.5, 1, 0 );
		Cubic2D[] p = g.subdivide( 0.5 );

		assertThat( p[ 0 ] ).isEqualTo( new Cubic2D( 0, 0, 0.25, 0.25, 0.375, 0.125, 0.5, 0.0 ) );
		assertThat( p[ 1 ] ).isEqualTo( new Cubic2D( 0.5, 0, 0.625, -0.125, 0.75, -0.25, 1.0, 0.0 ) );
	}

	@Test
	void testSubdivide() {
		Cubic2D g = new Cubic2D( 0, 0, 0.5, 0.5, 0.5, -0.5, 1, 0 );
		Cubic2D[] q = Cubic2D.subdivide( g );

		assertThat( q[ 0 ] ).isEqualTo( new Cubic2D( 0, 0, 0.25, 0.25, 0.375, 0.125, 0.5, 0.0 ) );
		assertThat( q[ 1 ] ).isEqualTo( new Cubic2D( 0.5, 0, 0.625, -0.125, 0.75, -0.25, 1.0, 0.0 ) );
	}

}
