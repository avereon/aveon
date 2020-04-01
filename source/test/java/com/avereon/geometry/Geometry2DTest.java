package com.avereon.geometry;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class Geometry2DTest {

	@Test
	void testGetSpin() {
		assertThat( Geometry2D.getSpin( new Point2D( 0, 0 ), new Point2D( 1, 0 ), new Point2D( 2, 0.5 ) ), is( 1 ) );
		assertThat( Geometry2D.getSpin( new Point2D( 0, 0 ), new Point2D( 1, 0 ), new Point2D( 2, 0.0 ) ), is( 0 ) );
		assertThat( Geometry2D.getSpin( new Point2D( 0, 0 ), new Point2D( 1, 0 ), new Point2D( 2, -0.5 ) ), is( -1 ) );
	}

	@Test
	void testToPolygon() {
		List<Point2D> a = new ArrayList<>();
		List<Point2D> b = new ArrayList<>();

		a.add( new Point2D( 1, 1 ) );
		a.add( new Point2D( 0, 1 ) );
		a.add( new Point2D( -1, 0 ) );
		a.add( new Point2D( -1, -1 ) );

		b.add( new Point2D( 1, 1 ) );
		b.add( new Point2D( 1, 0 ) );
		b.add( new Point2D( 0, -1 ) );
		b.add( new Point2D( -1, -1 ) );

		List<Point2D> polygon = Geometry2D.toCcwPolygon( a, b );

		int index = 0;
		assertThat( polygon.get( index++ ), is( new Point2D( 1, 1 ) ) );
		assertThat( polygon.get( index++ ), is( new Point2D( 0, 1 ) ) );
		assertThat( polygon.get( index++ ), is( new Point2D( -1, 0 ) ) );
		assertThat( polygon.get( index++ ), is( new Point2D( -1, -1 ) ) );
		assertThat( polygon.get( index++ ), is( new Point2D( 0, -1 ) ) );
		assertThat( polygon.get( index++ ), is( new Point2D( 1, 0 ) ) );
		assertThat( index, is( 6 ) );

		polygon = Geometry2D.toCcwPolygon( b, a );

		index = 0;
		assertThat( polygon.get( index++ ), is( new Point2D( -1, -1 ) ) );
		assertThat( polygon.get( index++ ), is( new Point2D( 0, -1 ) ) );
		assertThat( polygon.get( index++ ), is( new Point2D( 1, 0 ) ) );
		assertThat( polygon.get( index++ ), is( new Point2D( 1, 1 ) ) );
		assertThat( polygon.get( index++ ), is( new Point2D( 0, 1 ) ) );
		assertThat( polygon.get( index++ ), is( new Point2D( -1, 0 ) ) );
		assertThat( index, is( 6 ) );
	}

}
