package com.avereon.geometry;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

public class Geometry2DTest {

	@Test
	public void testGetAngleWithTwoPoints() {
		assertThat( Geometry2D.getAngle( Point2D.ZERO, Point2D.ZERO ), is( Double.NaN ) );

		assertThat( Geometry2D.getAngle( new Point2D( 1, 0 ), new Point2D( 1, 0 ) ), is( 0.0 ) );
		assertThat( Geometry2D.getAngle( new Point2D( 0, 1 ), new Point2D( 0, 1 ) ), is( 0.0 ) );
		assertThat( Geometry2D.getAngle( new Point2D( -1, 0 ), new Point2D( -1, 0 ) ), is( 0.0 ) );
		assertThat( Geometry2D.getAngle( new Point2D( 0, -1 ), new Point2D( 0, -1 ) ), is( 0.0 ) );

		assertThat( Geometry2D.getAngle( new Point2D( 0, 1 ), new Point2D( 1, 0 ) ), is( Math.PI / 2 ) );
		assertThat( Geometry2D.getAngle( new Point2D( 0, 1 ), new Point2D( -1, 0 ) ), is( Math.PI / 2 ) );
		assertThat( Geometry2D.getAngle( new Point2D( 0, -1 ), new Point2D( 1, 0 ) ), is( Math.PI / 2 ) );
		assertThat( Geometry2D.getAngle( new Point2D( 0, -1 ), new Point2D( -1, 0 ) ), is( Math.PI / 2 ) );
		assertThat( Geometry2D.getAngle( new Point2D( 1, 0 ), new Point2D( 0, 1 ) ), is( Math.PI / 2 ) );
		assertThat( Geometry2D.getAngle( new Point2D( 1, 0 ), new Point2D( 0, -1 ) ), is( Math.PI / 2 ) );
		assertThat( Geometry2D.getAngle( new Point2D( -1, 0 ), new Point2D( 0, 1 ) ), is( Math.PI / 2 ) );
		assertThat( Geometry2D.getAngle( new Point2D( -1, 0 ), new Point2D( 0, -1 ) ), is( Math.PI / 2 ) );

		assertThat( Geometry2D.getAngle( new Point2D( 0, 1 ), new Point2D( 0, -1 ) ), is( Math.PI ) );
	}

	@Test
	public void testGetPointLineBoundDistance() {
		assertThat( Geometry2D.getPointLineBoundOffset( Point2D.of( 0, 0 ), Point2D.of( 0, 1 ), Point2D.of( 1, 0 ) ), closeTo( -Math.sqrt( 0.5 ), 1e-15 ) );
		assertThat( Geometry2D.getPointLineBoundOffset( Point2D.of( 1, 1 ), Point2D.of( 0, 1 ), Point2D.of( 1, 0 ) ), closeTo( Math.sqrt( 0.5 ), 1e-15 ) );

		assertThat( Geometry2D.getPointLineBoundOffset( Point2D.of( 0, 2 ), Point2D.of( 0, 1 ), Point2D.of( 1, 0 ) ), is( Double.NaN ) );
		assertThat( Geometry2D.getPointLineBoundOffset( Point2D.of( 2, 0 ), Point2D.of( 0, 1 ), Point2D.of( 1, 0 ) ), is( Double.NaN ) );

		assertThat( Geometry2D.getPointLineBoundOffset( Point2D.of( -0.5, 0.5 ), Point2D.of( 0, 1 ), Point2D.of( 1, 0 ) ), is( Double.NaN ) );
		assertThat( Geometry2D.getPointLineBoundOffset( Point2D.of( 0.5, -0.5 ), Point2D.of( 0, 1 ), Point2D.of( 1, 0 ) ), is( Double.NaN ) );
		assertThat( Geometry2D.getPointLineBoundOffset( Point2D.of( 0.5, 1.5 ), Point2D.of( 0, 1 ), Point2D.of( 1, 0 ) ), is( Double.NaN ) );
		assertThat( Geometry2D.getPointLineBoundOffset( Point2D.of( 1.5, 0.5 ), Point2D.of( 0, 1 ), Point2D.of( 1, 0 ) ), is( Double.NaN ) );
	}

	@Test
	public void testGetPointLineDistance() {
		assertThat( Geometry2D.getPointLineDistance( new Point2D( -0.5, 1.0 ), new Point2D( 0, 0 ), new Point2D( 1, 0 ) ), is( 1.0 ) );
		assertThat( Geometry2D.getPointLineDistance( new Point2D( 0.0, 1.0 ), new Point2D( 0, 0 ), new Point2D( 1, 0 ) ), is( 1.0 ) );
		assertThat( Geometry2D.getPointLineDistance( new Point2D( 0.5, 1.0 ), new Point2D( 0, 0 ), new Point2D( 1, 0 ) ), is( 1.0 ) );
		assertThat( Geometry2D.getPointLineDistance( new Point2D( 1.0, 1.0 ), new Point2D( 0, 0 ), new Point2D( 1, 0 ) ), is( 1.0 ) );
		assertThat( Geometry2D.getPointLineDistance( new Point2D( 1.5, 1.0 ), new Point2D( 0, 0 ), new Point2D( 1, 0 ) ), is( 1.0 ) );
	}

	@Test
	void testGetSpin() {
		assertThat( Geometry2D.getSpin( new Point2D( 0, 0 ), new Point2D( 1, 0 ), new Point2D( 2, 0.5 ) ), is( 1 ) );
		assertThat( Geometry2D.getSpin( new Point2D( 0, 0 ), new Point2D( 1, 0 ), new Point2D( 2, 0.0 ) ), is( 0 ) );
		assertThat( Geometry2D.getSpin( new Point2D( 0, 0 ), new Point2D( 1, 0 ), new Point2D( 2, -0.5 ) ), is( -1 ) );
	}

	@Test
	void testFindNearestSegment() {
		List<Point2D> points = List.of( new Point2D( 0, 0 ), Point2D.of( 1, 1 ), Point2D.of( 2, 1 ), Point2D.of( 3, 0 ) );
		assertThat( Geometry2D.findDistanceToNearestSegment( Point2D.of( 1.5, 0 ), points ), is( 1.0 ) );
	}

	@Test
	void testFindPolygonsWithSamePaths() {
		List<Point2D> a = new ArrayList<>();
		a.add( new Point2D( 1, 1 ) );
		a.add( new Point2D( 0, 1 ) );
		a.add( new Point2D( -1, 0 ) );
		a.add( new Point2D( -1, -1 ) );
		List<Point2D> b = new ArrayList<>( a );

		List<List<Point2D>> polygons = Geometry2D.findPolygons( a, b );

		assertThat( polygons.size(), is( 0 ) );
	}

	@Test
	void testFindPolygonsWithHarmonicPaths() {
		Cubic2D c = new Cubic2D( 0, 0, 0, 0.1, 0.2, 0.2, 0.4, 0.2 );
		List<Point2D> a = c.toPoints( 8 );
		List<Point2D> b = c.toPoints( 4 );

		List<List<Point2D>> polygons = Geometry2D.findPolygons( a, b );

		assertThat( polygons.get( 0 ).size(), is( 3 ) );
		assertThat( polygons.get( 1 ).size(), is( 3 ) );
		assertThat( polygons.get( 2 ).size(), is( 3 ) );
		assertThat( polygons.get( 3 ).size(), is( 3 ) );

		assertThat( polygons.size(), is( 4 ) );
	}

	@Test
	void testFindPolygonsWithOnePolygonSameHeadSameTail() {
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
	void testToTrianglePolygon() {
		List<Point2D> a = new ArrayList<>();
		List<Point2D> b = new ArrayList<>();

		a.add( new Point2D( 1, 1 ) );
		a.add( new Point2D( 2, 1 ) );

		b.add( new Point2D( 1, 1 ) );
		b.add( new Point2D( 1.5, 1.5 ) );
		b.add( new Point2D( 2, 1 ) );

		List<Point2D> polygon = Geometry2D.toCcwPolygon( a, b );

		int index = 0;
		assertThat( polygon.get( index++ ), is( new Point2D( 1, 1 ) ) );
		assertThat( polygon.get( index++ ), is( new Point2D( 2, 1 ) ) );
		assertThat( polygon.get( index++ ), is( new Point2D( 1.5, 1.5 ) ) );
		assertThat( index, is( 3 ) );

		polygon = Geometry2D.toCcwPolygon( b, a );

		index = 0;
		assertThat( polygon.get( index++ ), is( new Point2D( 2, 1 ) ) );
		assertThat( polygon.get( index++ ), is( new Point2D( 1.5, 1.5 ) ) );
		assertThat( polygon.get( index++ ), is( new Point2D( 1, 1 ) ) );
		assertThat( index, is( 3 ) );
	}

	@Test
	void testCalcPathLength() {
		Cubic2D c = new Cubic2D( 0, 0, 0, 0.5, 0.5, 1, 1, 1 );
		assertThat( Geometry2D.calcPathLength( c.toPoints( 8 ) ), is( 1.5463566920835916 ) );
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

	@Test
	void testCalcQuadBasisEffect() {
		double error = 1e-15;

		assertThat( Geometry2D.calcQuadBasisEffect( 0, 0 ), is( 1.0 ) );
		assertThat( Geometry2D.calcQuadBasisEffect( 0, 1.0 / 3.0 ), closeTo( 4.0 / 9.0, error ) );
		assertThat( Geometry2D.calcQuadBasisEffect( 0, 0.5 ), is( 0.25 ) );
		assertThat( Geometry2D.calcQuadBasisEffect( 0, 2.0 / 3.0 ), closeTo( 1.0 / 9.0, error ) );
		assertThat( Geometry2D.calcQuadBasisEffect( 0, 1 ), is( 0.0 ) );

		assertThat( Geometry2D.calcQuadBasisEffect( 1, 0 ), is( 0.0 ) );
		assertThat( Geometry2D.calcQuadBasisEffect( 1, 1.0 / 3.0 ), closeTo( 4.0 / 9.0, error ) );
		assertThat( Geometry2D.calcQuadBasisEffect( 1, 0.5 ), is( 0.5 ) );
		assertThat( Geometry2D.calcQuadBasisEffect( 1, 2.0 / 3.0 ), closeTo( 4.0 / 9.0, error ) );
		assertThat( Geometry2D.calcQuadBasisEffect( 1, 1 ), is( 0.0 ) );

		assertThat( Geometry2D.calcQuadBasisEffect( 2, 0 ), is( 0.0 ) );
		assertThat( Geometry2D.calcQuadBasisEffect( 2, 1.0 / 3.0 ), closeTo( 1.0 / 9.0, error ) );
		assertThat( Geometry2D.calcQuadBasisEffect( 2, 0.5 ), is( 0.25 ) );
		assertThat( Geometry2D.calcQuadBasisEffect( 2, 2.0 / 3.0 ), closeTo( 4.0 / 9.0, error ) );
		assertThat( Geometry2D.calcQuadBasisEffect( 2, 1 ), is( 1.0 ) );
	}

	@Test
	void testCalcCubicBasisEffect() {
		double error = 1e-15;

		assertThat( Geometry2D.calcCubicBasisEffect( 0, 0 ), is( 1.0 ) );
		assertThat( Geometry2D.calcCubicBasisEffect( 0, 1.0 / 3.0 ), closeTo( 8.0 / 27.0, error ) );
		assertThat( Geometry2D.calcCubicBasisEffect( 0, 0.5 ), is( 1.0 / 8.0 ) );
		assertThat( Geometry2D.calcCubicBasisEffect( 0, 2.0 / 3.0 ), closeTo( 1.0 / 27.0, error ) );
		assertThat( Geometry2D.calcCubicBasisEffect( 0, 1 ), is( 0.0 ) );

		assertThat( Geometry2D.calcCubicBasisEffect( 1, 0 ), is( 0.0 ) );
		assertThat( Geometry2D.calcCubicBasisEffect( 1, 1.0 / 3.0 ), closeTo( 4.0 / 9.0, error ) );
		assertThat( Geometry2D.calcCubicBasisEffect( 1, 0.5 ), is( 3.0 / 8.0 ) );
		assertThat( Geometry2D.calcCubicBasisEffect( 1, 2.0 / 3.0 ), closeTo( 2.0 / 9.0, error ) );
		assertThat( Geometry2D.calcCubicBasisEffect( 1, 1 ), is( 0.0 ) );

		assertThat( Geometry2D.calcCubicBasisEffect( 2, 0 ), is( 0.0 ) );
		assertThat( Geometry2D.calcCubicBasisEffect( 2, 1.0 / 3.0 ), closeTo( 2.0 / 9.0, error ) );
		assertThat( Geometry2D.calcCubicBasisEffect( 2, 0.5 ), is( 3.0 / 8.0 ) );
		assertThat( Geometry2D.calcCubicBasisEffect( 2, 2.0 / 3.0 ), closeTo( 4.0 / 9.0, error ) );
		assertThat( Geometry2D.calcCubicBasisEffect( 2, 1 ), is( 0.0 ) );

		assertThat( Geometry2D.calcCubicBasisEffect( 3, 0 ), is( 0.0 ) );
		assertThat( Geometry2D.calcCubicBasisEffect( 3, 1.0 / 3.0 ), closeTo( 1.0 / 27.0, error ) );
		assertThat( Geometry2D.calcCubicBasisEffect( 3, 0.5 ), is( 1.0 / 8.0 ) );
		assertThat( Geometry2D.calcCubicBasisEffect( 3, 2.0 / 3.0 ), closeTo( 8.0 / 27.0, error ) );
		assertThat( Geometry2D.calcCubicBasisEffect( 3, 1 ), is( 1.0 ) );
	}

}
