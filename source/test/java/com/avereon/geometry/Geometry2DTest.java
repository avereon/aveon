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
	void testFindPolygonsWithOnePolygonSameHeadSameTail() {
		// Need a head crossing test
		// Need a tail crossing test
		// Need a both crossing test
		// Need a multi-polygon test
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

		List<List<Point2D>> polygons = Geometry2D.findPolygons( a, b );

		System.err.println();
		List<Point2D> polygon = polygons.get( 0 );
		for( Point2D point : polygon ) {
			System.err.println( "p=" + point );
		}

		int index = 0;
		assertThat( polygons.get( 0 ).get( index++ ), is( new Point2D( 1, 1 ) ) );
		assertThat( polygons.get( 0 ).get( index++ ), is( new Point2D( 0, 1 ) ) );
		assertThat( polygons.get( 0 ).get( index++ ), is( new Point2D( -1, 0 ) ) );
		assertThat( polygons.get( 0 ).get( index++ ), is( new Point2D( -1, -1 ) ) );
		assertThat( polygons.get( 0 ).get( index++ ), is( new Point2D( 0, -1 ) ) );
		assertThat( polygons.get( 0 ).get( index++ ), is( new Point2D( 1, 0 ) ) );
		assertThat( index, is( 6 ) );
		assertThat( polygons.size(), is( 1 ) );
	}

	@Test
	void testFindPolygonsWithOnePolygonCrossingHeadSameTail() {
		List<Point2D> a = new ArrayList<>();
		List<Point2D> b = new ArrayList<>();

		a.add( new Point2D( 1, 2 ) );
		a.add( new Point2D( 2, 1 ) );
		a.add( new Point2D( 3, 1.5 ) );

		b.add( new Point2D( 1, 1 ) );
		b.add( new Point2D( 2, 2 ) );
		b.add( new Point2D( 3, 1.5 ) );

		List<List<Point2D>> polygons = Geometry2D.findPolygons( a, b );

		int index = 0;
		assertThat( polygons.get( 0 ).get( index++ ), is( new Point2D( 1.5, 1.5 ) ) );
		assertThat( polygons.get( 0 ).get( index++ ), is( new Point2D( 2, 1 ) ) );
		assertThat( polygons.get( 0 ).get( index++ ), is( new Point2D( 3, 1.5 ) ) );
		assertThat( polygons.get( 0 ).get( index++ ), is( new Point2D( 2, 2 ) ) );
		assertThat( index, is( 4 ) );
		assertThat( polygons.size(), is( 1 ) );
	}

	@Test
	void testFindPolygonsWithOnePolygonSameHeadCrossingTail() {
		List<Point2D> a = new ArrayList<>();
		List<Point2D> b = new ArrayList<>();

		a.add( new Point2D( 1, 1.5 ) );
		a.add( new Point2D( 2, 1 ) );
		a.add( new Point2D( 3, 2 ) );

		b.add( new Point2D( 1, 1.5 ) );
		b.add( new Point2D( 2, 2 ) );
		b.add( new Point2D( 3, 1 ) );

		List<List<Point2D>> polygons = Geometry2D.findPolygons( a, b );

		int index = 0;
		assertThat( polygons.get( 0 ).get( index++ ), is( new Point2D( 1, 1.5 ) ) );
		assertThat( polygons.get( 0 ).get( index++ ), is( new Point2D( 2, 1 ) ) );
		assertThat( polygons.get( 0 ).get( index++ ), is( new Point2D( 2.5, 1.5 ) ) );
		assertThat( polygons.get( 0 ).get( index++ ), is( new Point2D( 2, 2 ) ) );
		assertThat( index, is( 4 ) );
		assertThat( polygons.size(), is( 1 ) );
	}

	@Test
	void testFindPolygonsWithOnePolygonCrossingHeadCrossingTail() {
		List<Point2D> a = new ArrayList<>();
		List<Point2D> b = new ArrayList<>();

		a.add( new Point2D( 1, 2 ) );
		a.add( new Point2D( 2, 1 ) );
		a.add( new Point2D( 3, 2 ) );

		b.add( new Point2D( 1, 1 ) );
		b.add( new Point2D( 2, 2 ) );
		b.add( new Point2D( 3, 1 ) );

		List<List<Point2D>> polygons = Geometry2D.findPolygons( a, b );

		int index = 0;
		assertThat( polygons.get( 0 ).get( index++ ), is( new Point2D( 1.5, 1.5 ) ) );
		assertThat( polygons.get( 0 ).get( index++ ), is( new Point2D( 2, 1 ) ) );
		assertThat( polygons.get( 0 ).get( index++ ), is( new Point2D( 2.5, 1.5 ) ) );
		assertThat( polygons.get( 0 ).get( index++ ), is( new Point2D( 2, 2 ) ) );
		assertThat( index, is( 4 ) );
		assertThat( polygons.size(), is( 1 ) );
	}

	@Test
	void testFindPolygonsWithMultiplePolygonsCrossingHeadCrossingTail() {
		List<Point2D> a = new ArrayList<>();
		List<Point2D> b = new ArrayList<>();

		a.add( new Point2D( 1, 2 ) );
		a.add( new Point2D( 2, 1 ) );
		a.add( new Point2D( 3, 2 ) );
		a.add( new Point2D( 4, 1 ) );
		a.add( new Point2D( 5, 2 ) );

		b.add( new Point2D( 1, 1 ) );
		b.add( new Point2D( 2, 2 ) );
		b.add( new Point2D( 3, 1 ) );
		b.add( new Point2D( 4, 2 ) );
		b.add( new Point2D( 5, 1 ) );

		List<List<Point2D>> polygons = Geometry2D.findPolygons( a, b );

		int index = 0;
		assertThat( polygons.get( 0 ).get( index++ ), is( new Point2D( 1.5, 1.5 ) ) );
		assertThat( polygons.get( 0 ).get( index++ ), is( new Point2D( 2, 1 ) ) );
		assertThat( polygons.get( 0 ).get( index++ ), is( new Point2D( 2.5, 1.5 ) ) );
		assertThat( polygons.get( 0 ).get( index++ ), is( new Point2D( 2, 2 ) ) );
		assertThat( index, is( 4 ) );

		index = 0;
		assertThat( polygons.get( 1 ).get( index++ ), is( new Point2D( 3.5, 1.5 ) ) );
		assertThat( polygons.get( 1 ).get( index++ ), is( new Point2D( 3, 2 ) ) );
		assertThat( polygons.get( 1 ).get( index++ ), is( new Point2D( 2.5, 1.5 ) ) );
		assertThat( polygons.get( 1 ).get( index++ ), is( new Point2D( 3, 1 ) ) );
		assertThat( index, is( 4 ) );

		index = 0;
		assertThat( polygons.get( 2 ).get( index++ ), is( new Point2D( 3.5, 1.5 ) ) );
		assertThat( polygons.get( 2 ).get( index++ ), is( new Point2D( 4, 1 ) ) );
		assertThat( polygons.get( 2 ).get( index++ ), is( new Point2D( 4.5, 1.5 ) ) );
		assertThat( polygons.get( 2 ).get( index++ ), is( new Point2D( 4, 2 ) ) );
		assertThat( index, is( 4 ) );

		assertThat( polygons.size(), is( 3 ) );
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

	@Test
	void testCalcPolygonArea() {
		List<Point2D> p = new ArrayList<>();

		p.add( new Point2D( 1, 1 ) );
		p.add( new Point2D( 0, 1 ) );
		p.add( new Point2D( -1, 0 ) );
		p.add( new Point2D( -1, -1 ) );
		p.add( new Point2D( 0, -1 ) );
		p.add( new Point2D( 1, 0 ) );

		assertThat( Geometry2D.calcPolygonArea( p ), is( 3.0 ) );
	}

	@Test
	void testGetBounds() {
		List<Point2D> p = new ArrayList<>();

		p.add( new Point2D( 1, 1 ) );
		p.add( new Point2D( 0, 1 ) );
		p.add( new Point2D( -1, 0 ) );
		p.add( new Point2D( -1, -1 ) );
		p.add( new Point2D( 0, -1 ) );
		p.add( new Point2D( 1, 0 ) );

		assertThat( Geometry2D.getBounds( p ), is( new Bounds2D( -1, -1, 1, 1 ) ) );
	}
}
