package com.avereon.geometry;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CfdGeometryTest {

	@Test
	public void testGetAngleWithTwoPoints() {
		assertThat( CfdGeometry.getAngle( Point2D.ZERO, Point2D.ZERO )).isNaN();

		assertThat( CfdGeometry.getAngle( new Point2D( 1, 0 ), new Point2D( 1, 0 ) )).isEqualTo( 0.0  );
		assertThat( CfdGeometry.getAngle( new Point2D( 0, 1 ), new Point2D( 0, 1 ) )).isEqualTo( 0.0  );
		assertThat( CfdGeometry.getAngle( new Point2D( -1, 0 ), new Point2D( -1, 0 ) )).isEqualTo( 0.0  );
		assertThat( CfdGeometry.getAngle( new Point2D( 0, -1 ), new Point2D( 0, -1 ) )).isEqualTo( 0.0  );

		assertThat( CfdGeometry.getAngle( new Point2D( 0, 1 ), new Point2D( 1, 0 ) )).isEqualTo( -Math.PI / 2 );
		assertThat( CfdGeometry.getAngle( new Point2D( 0, 1 ), new Point2D( -1, 0 ) )).isEqualTo( Math.PI / 2 );
		assertThat( CfdGeometry.getAngle( new Point2D( 0, -1 ), new Point2D( 1, 0 ) )).isEqualTo( Math.PI / 2 );
		assertThat( CfdGeometry.getAngle( new Point2D( 0, -1 ), new Point2D( -1, 0 ) )).isEqualTo( -Math.PI / 2 );
		assertThat( CfdGeometry.getAngle( new Point2D( 1, 0 ), new Point2D( 0, 1 ) )).isEqualTo( Math.PI / 2 );
		assertThat( CfdGeometry.getAngle( new Point2D( 1, 0 ), new Point2D( 0, -1 ) )).isEqualTo( -Math.PI / 2 );
		assertThat( CfdGeometry.getAngle( new Point2D( -1, 0 ), new Point2D( 0, 1 ) )).isEqualTo( -Math.PI / 2 );
		assertThat( CfdGeometry.getAngle( new Point2D( -1, 0 ), new Point2D( 0, -1 ) )).isEqualTo( Math.PI / 2 );

		assertThat( CfdGeometry.getAngle( new Point2D( 0, 1 ), new Point2D( 0, -1 ) )).isEqualTo( Math.PI );
	}

	@Test
	public void testGetPointLineBoundDistance() {
		Offset<Double> error = Offset.offset( 1e-15 );

		assertThat( CfdGeometry.getPointLineBoundOffset( Point2D.of( 0, 0 ), Point2D.of( 0, 1 ), Point2D.of( 1, 0 ) )).isCloseTo( -Math.sqrt( 0.5 ), error  );
		assertThat( CfdGeometry.getPointLineBoundOffset( Point2D.of( 1, 1 ), Point2D.of( 0, 1 ), Point2D.of( 1, 0 ) )).isCloseTo( Math.sqrt( 0.5 ), error  );

		assertThat( CfdGeometry.getPointLineBoundOffset( Point2D.of( 0, 2 ), Point2D.of( 0, 1 ), Point2D.of( 1, 0 ) )).isNaN();
		assertThat( CfdGeometry.getPointLineBoundOffset( Point2D.of( 2, 0 ), Point2D.of( 0, 1 ), Point2D.of( 1, 0 ) )).isNaN();

		assertThat( CfdGeometry.getPointLineBoundOffset( Point2D.of( -0.5, 0.5 ), Point2D.of( 0, 1 ), Point2D.of( 1, 0 ) )).isNaN();
		assertThat( CfdGeometry.getPointLineBoundOffset( Point2D.of( 0.5, -0.5 ), Point2D.of( 0, 1 ), Point2D.of( 1, 0 ) )).isNaN();
		assertThat( CfdGeometry.getPointLineBoundOffset( Point2D.of( 0.5, 1.5 ), Point2D.of( 0, 1 ), Point2D.of( 1, 0 ) )).isNaN();
		assertThat( CfdGeometry.getPointLineBoundOffset( Point2D.of( 1.5, 0.5 ), Point2D.of( 0, 1 ), Point2D.of( 1, 0 ) )).isNaN();
	}

	@Test
	public void testGetPointLineDistance() {
		assertThat( CfdGeometry.getPointLineDistance( new Point2D( -0.5, 1.0 ), new Point2D( 0, 0 ), new Point2D( 1, 0 ) )).isEqualTo( 1.0 );
		assertThat( CfdGeometry.getPointLineDistance( new Point2D( 0.0, 1.0 ), new Point2D( 0, 0 ), new Point2D( 1, 0 ) )).isEqualTo( 1.0 );
		assertThat( CfdGeometry.getPointLineDistance( new Point2D( 0.5, 1.0 ), new Point2D( 0, 0 ), new Point2D( 1, 0 ) )).isEqualTo( 1.0 );
		assertThat( CfdGeometry.getPointLineDistance( new Point2D( 1.0, 1.0 ), new Point2D( 0, 0 ), new Point2D( 1, 0 ) )).isEqualTo( 1.0 );
		assertThat( CfdGeometry.getPointLineDistance( new Point2D( 1.5, 1.0 ), new Point2D( 0, 0 ), new Point2D( 1, 0 ) )).isEqualTo( 1.0 );
	}

	@Test
	void testGetSpin() {
		assertThat( CfdGeometry.getSpin( new Point2D( 0, 0 ), new Point2D( 1, 0 ), new Point2D( 2, 0.5 ) )).isEqualTo( 1 );
		assertThat( CfdGeometry.getSpin( new Point2D( 0, 0 ), new Point2D( 1, 0 ), new Point2D( 2, 0.0 ) )).isEqualTo( 0 );
		assertThat( CfdGeometry.getSpin( new Point2D( 0, 0 ), new Point2D( 1, 0 ), new Point2D( 2, -0.5 ) )).isEqualTo( -1 );
	}

	@Test
	void testFindNearestSegment() {
		List<Point2D> points = List.of( new Point2D( 0, 0 ), Point2D.of( 1, 1 ), Point2D.of( 2, 1 ), Point2D.of( 3, 0 ) );
		assertThat( CfdGeometry.findDistanceToNearestSegment( Point2D.of( 1.5, 0 ), points )).isEqualTo( 1.0 );
	}

	@Test
	void testFindPolygonsWithSamePaths() {
		List<Point2D> a = new ArrayList<>();
		a.add( new Point2D( 1, 1 ) );
		a.add( new Point2D( 0, 1 ) );
		a.add( new Point2D( -1, 0 ) );
		a.add( new Point2D( -1, -1 ) );
		List<Point2D> b = new ArrayList<>( a );

		List<List<Point2D>> polygons = CfdGeometry.findPolygons( a, b );

		assertThat( polygons.size()).isEqualTo( 0 );
	}

	@Test
	void testFindPolygonsWithHarmonicPaths() {
		Cubic2D c = new Cubic2D( 0, 0, 0, 0.1, 0.2, 0.2, 0.4, 0.2 );
		List<Point2D> a = c.toPoints( 8 );
		List<Point2D> b = c.toPoints( 4 );

		List<List<Point2D>> polygons = CfdGeometry.findPolygons( a, b );

		assertThat( polygons.get( 0 ).size()).isEqualTo( 3 );
		assertThat( polygons.get( 1 ).size()).isEqualTo( 3 );
		assertThat( polygons.get( 2 ).size()).isEqualTo( 3 );
		assertThat( polygons.get( 3 ).size()).isEqualTo( 3 );

		assertThat( polygons.size()).isEqualTo( 4 );
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

		List<List<Point2D>> polygons = CfdGeometry.findPolygons( a, b );

		int index = 0;
		assertThat( polygons.get( 0 ).get( index++ )).isEqualTo( new Point2D( 1, 1 ) );
		assertThat( polygons.get( 0 ).get( index++ )).isEqualTo( new Point2D( 0, 1 ) );
		assertThat( polygons.get( 0 ).get( index++ )).isEqualTo( new Point2D( -1, 0 ) );
		assertThat( polygons.get( 0 ).get( index++ )).isEqualTo( new Point2D( -1, -1 ) );
		assertThat( polygons.get( 0 ).get( index++ )).isEqualTo( new Point2D( 0, -1 ) );
		assertThat( polygons.get( 0 ).get( index++ )).isEqualTo( new Point2D( 1, 0 ) );
		assertThat( index).isEqualTo( 6 );
		assertThat( polygons.size()).isEqualTo( 1 );
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

		List<List<Point2D>> polygons = CfdGeometry.findPolygons( a, b );

		int index = 0;
		assertThat( polygons.get( 0 ).get( index++ )).isEqualTo( new Point2D( 1.5, 1.5 ) );
		assertThat( polygons.get( 0 ).get( index++ )).isEqualTo( new Point2D( 2, 1 ) );
		assertThat( polygons.get( 0 ).get( index++ )).isEqualTo( new Point2D( 3, 1.5 ) );
		assertThat( polygons.get( 0 ).get( index++ )).isEqualTo( new Point2D( 2, 2 ) );
		assertThat( index).isEqualTo( 4 );
		assertThat( polygons.size()).isEqualTo( 1 );
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

		List<List<Point2D>> polygons = CfdGeometry.findPolygons( a, b );

		int index = 0;
		assertThat( polygons.get( 0 ).get( index++ )).isEqualTo( new Point2D( 1, 1.5 ) );
		assertThat( polygons.get( 0 ).get( index++ )).isEqualTo( new Point2D( 2, 1 ) );
		assertThat( polygons.get( 0 ).get( index++ )).isEqualTo( new Point2D( 2.5, 1.5 ) );
		assertThat( polygons.get( 0 ).get( index++ )).isEqualTo( new Point2D( 2, 2 ) );
		assertThat( index).isEqualTo( 4 );
		assertThat( polygons.size()).isEqualTo( 1 );
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

		List<List<Point2D>> polygons = CfdGeometry.findPolygons( a, b );

		int index = 0;
		assertThat( polygons.get( 0 ).get( index++ )).isEqualTo( new Point2D( 1.5, 1.5 ) );
		assertThat( polygons.get( 0 ).get( index++ )).isEqualTo( new Point2D( 2, 1 ) );
		assertThat( polygons.get( 0 ).get( index++ )).isEqualTo( new Point2D( 2.5, 1.5 ) );
		assertThat( polygons.get( 0 ).get( index++ )).isEqualTo( new Point2D( 2, 2 ) );
		assertThat( index).isEqualTo( 4 );
		assertThat( polygons.size()).isEqualTo( 1 );
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

		List<List<Point2D>> polygons = CfdGeometry.findPolygons( a, b );

		int index = 0;
		assertThat( polygons.get( 0 ).get( index++ )).isEqualTo( new Point2D( 1.5, 1.5 ) );
		assertThat( polygons.get( 0 ).get( index++ )).isEqualTo( new Point2D( 2, 1 ) );
		assertThat( polygons.get( 0 ).get( index++ )).isEqualTo( new Point2D( 2.5, 1.5 ) );
		assertThat( polygons.get( 0 ).get( index++ )).isEqualTo( new Point2D( 2, 2 ) );
		assertThat( index).isEqualTo( 4 );

		index = 0;
		assertThat( polygons.get( 1 ).get( index++ )).isEqualTo( new Point2D( 3.5, 1.5 ) );
		assertThat( polygons.get( 1 ).get( index++ )).isEqualTo( new Point2D( 3, 2 ) );
		assertThat( polygons.get( 1 ).get( index++ )).isEqualTo( new Point2D( 2.5, 1.5 ) );
		assertThat( polygons.get( 1 ).get( index++ )).isEqualTo( new Point2D( 3, 1 ) );
		assertThat( index).isEqualTo( 4 );

		index = 0;
		assertThat( polygons.get( 2 ).get( index++ )).isEqualTo( new Point2D( 3.5, 1.5 ) );
		assertThat( polygons.get( 2 ).get( index++ )).isEqualTo( new Point2D( 4, 1 ) );
		assertThat( polygons.get( 2 ).get( index++ )).isEqualTo( new Point2D( 4.5, 1.5 ) );
		assertThat( polygons.get( 2 ).get( index++ )).isEqualTo( new Point2D( 4, 2 ) );
		assertThat( index).isEqualTo( 4 );

		assertThat( polygons.size()).isEqualTo( 3 );
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

		List<Point2D> polygon = CfdGeometry.toCcwPolygon( a, b );

		int index = 0;
		assertThat( polygon.get( index++ )).isEqualTo( new Point2D( 1, 1 ) );
		assertThat( polygon.get( index++ )).isEqualTo( new Point2D( 0, 1 ) );
		assertThat( polygon.get( index++ )).isEqualTo( new Point2D( -1, 0 ) );
		assertThat( polygon.get( index++ )).isEqualTo( new Point2D( -1, -1 ) );
		assertThat( polygon.get( index++ )).isEqualTo( new Point2D( 0, -1 ) );
		assertThat( polygon.get( index++ )).isEqualTo( new Point2D( 1, 0 ) );
		assertThat( index).isEqualTo( 6 );

		polygon = CfdGeometry.toCcwPolygon( b, a );

		index = 0;
		assertThat( polygon.get( index++ )).isEqualTo( new Point2D( -1, -1 ) );
		assertThat( polygon.get( index++ )).isEqualTo( new Point2D( 0, -1 ) );
		assertThat( polygon.get( index++ )).isEqualTo( new Point2D( 1, 0 ) );
		assertThat( polygon.get( index++ )).isEqualTo( new Point2D( 1, 1 ) );
		assertThat( polygon.get( index++ )).isEqualTo( new Point2D( 0, 1 ) );
		assertThat( polygon.get( index++ )).isEqualTo( new Point2D( -1, 0 ) );
		assertThat( index).isEqualTo( 6 );
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

		List<Point2D> polygon = CfdGeometry.toCcwPolygon( a, b );

		int index = 0;
		assertThat( polygon.get( index++ )).isEqualTo( new Point2D( 1, 1 ) );
		assertThat( polygon.get( index++ )).isEqualTo( new Point2D( 2, 1 ) );
		assertThat( polygon.get( index++ )).isEqualTo( new Point2D( 1.5, 1.5 ) );
		assertThat( index).isEqualTo( 3 );

		polygon = CfdGeometry.toCcwPolygon( b, a );

		index = 0;
		assertThat( polygon.get( index++ )).isEqualTo( new Point2D( 2, 1 ) );
		assertThat( polygon.get( index++ )).isEqualTo( new Point2D( 1.5, 1.5 ) );
		assertThat( polygon.get( index++ )).isEqualTo( new Point2D( 1, 1 ) );
		assertThat( index).isEqualTo( 3 );
	}

	@Test
	void testCalcPathLength() {
		Cubic2D c = new Cubic2D( 0, 0, 0, 0.5, 0.5, 1, 1, 1 );
		assertThat( CfdGeometry.calcPathLength( c.toPoints( 8 ) )).isEqualTo( 1.5463566920835916 );
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

		assertThat( CfdGeometry.calcPolygonArea( p )).isEqualTo( 3.0 );
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

		assertThat( CfdGeometry.getBounds( p )).isEqualTo( new Bounds2D( -1, -1, 1, 1 ) );
	}

	@Test
	void testCalcQuadBasisEffect() {
		Offset<Double> error = Offset.offset( 1e-15 );

		assertThat( CfdGeometry.calcQuadBasisEffect( 0, 0 )).isEqualTo( 1.0 );
		assertThat( CfdGeometry.calcQuadBasisEffect( 0, 1.0 / 3.0 )).isCloseTo( 4.0 / 9.0, error );
		assertThat( CfdGeometry.calcQuadBasisEffect( 0, 0.5 )).isEqualTo( 0.25 );
		assertThat( CfdGeometry.calcQuadBasisEffect( 0, 2.0 / 3.0 )).isCloseTo( 1.0 / 9.0, error );
		assertThat( CfdGeometry.calcQuadBasisEffect( 0, 1 )).isEqualTo( 0.0 );

		assertThat( CfdGeometry.calcQuadBasisEffect( 1, 0 )).isEqualTo( 0.0 );
		assertThat( CfdGeometry.calcQuadBasisEffect( 1, 1.0 / 3.0 )).isCloseTo( 4.0 / 9.0, error );
		assertThat( CfdGeometry.calcQuadBasisEffect( 1, 0.5 )).isEqualTo( 0.5 );
		assertThat( CfdGeometry.calcQuadBasisEffect( 1, 2.0 / 3.0 )).isCloseTo( 4.0 / 9.0, error );
		assertThat( CfdGeometry.calcQuadBasisEffect( 1, 1 )).isEqualTo( 0.0 );

		assertThat( CfdGeometry.calcQuadBasisEffect( 2, 0 )).isEqualTo( 0.0 );
		assertThat( CfdGeometry.calcQuadBasisEffect( 2, 1.0 / 3.0 )).isCloseTo( 1.0 / 9.0, error );
		assertThat( CfdGeometry.calcQuadBasisEffect( 2, 0.5 )).isEqualTo( 0.25 );
		assertThat( CfdGeometry.calcQuadBasisEffect( 2, 2.0 / 3.0 )).isCloseTo( 4.0 / 9.0, error );
		assertThat( CfdGeometry.calcQuadBasisEffect( 2, 1 )).isEqualTo( 1.0 );
	}

	@Test
	void testCalcCubicBasisEffect() {
		Offset<Double> error = Offset.offset( 1e-15 );

		assertThat( CfdGeometry.calcCubicBasisEffect( 0, 0 )).isEqualTo( 1.0 );
		assertThat( CfdGeometry.calcCubicBasisEffect( 0, 1.0 / 3.0 )).isCloseTo( 8.0 / 27.0, error );
		assertThat( CfdGeometry.calcCubicBasisEffect( 0, 0.5 )).isEqualTo( 1.0 / 8.0 );
		assertThat( CfdGeometry.calcCubicBasisEffect( 0, 2.0 / 3.0 )).isCloseTo( 1.0 / 27.0, error );
		assertThat( CfdGeometry.calcCubicBasisEffect( 0, 1 )).isEqualTo( 0.0 );

		assertThat( CfdGeometry.calcCubicBasisEffect( 1, 0 )).isEqualTo( 0.0 );
		assertThat( CfdGeometry.calcCubicBasisEffect( 1, 1.0 / 3.0 )).isCloseTo( 4.0 / 9.0, error );
		assertThat( CfdGeometry.calcCubicBasisEffect( 1, 0.5 )).isEqualTo( 3.0 / 8.0 );
		assertThat( CfdGeometry.calcCubicBasisEffect( 1, 2.0 / 3.0 )).isCloseTo( 2.0 / 9.0, error );
		assertThat( CfdGeometry.calcCubicBasisEffect( 1, 1 )).isEqualTo( 0.0 );

		assertThat( CfdGeometry.calcCubicBasisEffect( 2, 0 )).isEqualTo( 0.0 );
		assertThat( CfdGeometry.calcCubicBasisEffect( 2, 1.0 / 3.0 )).isCloseTo( 2.0 / 9.0, error );
		assertThat( CfdGeometry.calcCubicBasisEffect( 2, 0.5 )).isEqualTo( 3.0 / 8.0 );
		assertThat( CfdGeometry.calcCubicBasisEffect( 2, 2.0 / 3.0 )).isCloseTo( 4.0 / 9.0, error );
		assertThat( CfdGeometry.calcCubicBasisEffect( 2, 1 )).isEqualTo( 0.0 );

		assertThat( CfdGeometry.calcCubicBasisEffect( 3, 0 )).isEqualTo( 0.0 );
		assertThat( CfdGeometry.calcCubicBasisEffect( 3, 1.0 / 3.0 )).isCloseTo( 1.0 / 27.0, error );
		assertThat( CfdGeometry.calcCubicBasisEffect( 3, 0.5 )).isEqualTo( 1.0 / 8.0 );
		assertThat( CfdGeometry.calcCubicBasisEffect( 3, 2.0 / 3.0 )).isCloseTo( 8.0 / 27.0, error );
		assertThat( CfdGeometry.calcCubicBasisEffect( 3, 1 )).isEqualTo( 1.0 );
	}

}
