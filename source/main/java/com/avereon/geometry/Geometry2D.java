package com.avereon.geometry;

import java.util.*;

public class Geometry2D {

	/**
	 * Determine if two line segments, a and b, intersect.
	 *
	 * @param a Line segment a
	 * @param b Line segment b
	 * @return True if the line segments intersect, false otherwise
	 */
	public static boolean areIntersecting( Line2D a, Line2D b ) {
		return areIntersecting( a.a, a.b, b.a, b.b );
	}

	/**
	 * Determine if two line segments, a and b, intersect given the respective end
	 * points.
	 *
	 * @param a1 First point of line segment a
	 * @param a2 Second point of line segment a
	 * @param b1 First point of line segment b
	 * @param b2 Second point of line segment b
	 * @return True if the line segments intersect, false otherwise
	 */
	public static boolean areIntersecting( Point2D a1, Point2D a2, Point2D b1, Point2D b2 ) {
		return (getSpin( a1, b1, b2 ) != getSpin( a2, b1, b2 )) & (getSpin( a1, a2, b1 ) != getSpin( a1, a2, b2 ));
	}

	/**
	 * Use the x and y coordinates to determine if the three points are in
	 * counter-clockwise(-1), straight(0), or clockwise(1) order.
	 *
	 * @param a The anchor point/vector to test
	 * @param b The direction point/vector to test
	 * @param c The point/vector to compare
	 * @return Minus one if CCW, zero if straight, and one if CW.
	 */
	public static int getSpin( Point2D a, Point2D b, Point2D c ) {
		Point2D ab = a.subtract( b );
		Point2D cb = c.subtract( b );
		double ccw = cb.crossProduct( ab ).z;

		if( ccw == 0.0 || ccw == -0.0 ) return 0;
		return ccw > 0 ? 1 : -1;
	}

	public static List<List<Point2D>> findPolygons( List<Point2D> fitPoints, List<Point2D> curvePoints ) {
		// It is assumed that both lists of points are ordered in the desired direction
		// There must be two or more intersections for this method to work

		List<List<Point2D>> polygons = new ArrayList<>();
		List<Line2D> fitLines = Point2D.toSegments( fitPoints );
		List<Line2D> curveLines = Point2D.toSegments( curvePoints );

		List<Point2D> a = new ArrayList<>();
		List<Point2D> b = new ArrayList<>();
		Point2D priorIntersection = null;
		for( Line2D fitLine : fitLines ) {
			for( Line2D curveLine : curveLines ) {
				List<Point2D> intersections = fitLine.intersections( curveLine );
				if( priorIntersection == null ) {
					if( intersections.size() == 0 ) {
						continue;
					} else {
						// Start both paths
						a.add( intersections.get( 0 ) );
						b.add( intersections.get( 0 ) );
						a.add( fitLine.b );
						b.add( curveLine.b );
					}
				} else {
					if( intersections.size() > 0 ) {
						// Add a polygon
						a.add( intersections.get( 0 ) );
						b.add( intersections.get( 0 ) );
						polygons.add( toCcwPolygon( a, b ) );
						a = new ArrayList<>();
						b = new ArrayList<>();
						a.add( intersections.get( 0 ) );
						b.add( intersections.get( 0 ) );
					}
					a.add( fitLine.b );
					b.add( curveLine.b );
				}

				if( intersections.size() > 0 ) priorIntersection = intersections.get( 0 );
			}
		}

		return polygons;
	}

	public static List<Point2D> toCcwPolygon( List<Point2D> a, List<Point2D> b ) {
		// It is assumed that a and b are ordered
		// and that there are two or more points in one list
		// and that there are two or more points in the other list
		// and that the first and last points are equal
		// and that the lists do not intersect

		a = new ArrayList<>( a );
		b = new ArrayList<>( b );

		if( b.size() < 3 && a.size() < 3 ) throw new IllegalArgumentException( "Not enough points in lists" );
		if( !Objects.equals( a.get( 0 ), b.get( 0 ) ) ) throw new IllegalArgumentException( "Starting point mismatch" );
		if( !Objects.equals( a.get( a.size() - 1 ), b.get( b.size() - 1 ) ) ) throw new IllegalArgumentException( "Ending point mismatch" );

		// One list will be CW or CCW from the other, just need to get them ordered correctly
		Point2D pA = a.get( 0 );
		Point2D pB = a.get( 1 );
		Point2D pC = b.get( 1 );

		// If pC is CCW then a is in order and b needs to be reversed
		// If pC is CW then b is in order and a needs to be reversed
		if( getSpin( pA, pB, pC ) > 0 ) {
			Collections.reverse( b );
		} else {
			Collections.reverse( a );
		}

		List<Point2D> polygon = new ArrayList<>();
		polygon.addAll( a.subList( 0, a.size() - 1 ) );
		polygon.addAll( b.subList( 0, b.size() - 1 ) );

		return polygon;
	}

	public static double calcPolygonArea( List<Point2D> points ) {
		// It is assumed that the points form a closed, non-intersecting polygon in counterclockwise order

		// This process uses Green's theorem to calculate the area
		// https://en.wikipedia.org/wiki/Green%27s_theorem#Area_calculation
		double result = 0;
		Point2D p1;
		Point2D p2;
		int count = points.size();
		for( int index1 = 0; index1 < count; index1++ ) {
			p1 = points.get( index1 );
			int index2 = index1 + 1;
			if( index2 >= count ) index2 = 0;
			p2 = points.get( index2 );

			result += 0.5 * (p2.x + p1.x ) * (p2.y - p1.y);
		}

		return result;
	}

	public static Bounds2D getBounds( Collection<Point2D> points ) {
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double maxX = Double.MIN_VALUE;
		double maxY = Double.MIN_VALUE;

		for( Point2D p : points ) {
			if( p.x < minX ) minX = p.x;
			if( p.y < minY ) minY = p.y;
			if( p.x > maxX ) maxX = p.x;
			if( p.y > maxY ) maxY = p.y;
		}

		return new Bounds2D( minX, minY, maxX, maxY );
	}


	public static List<Point2D> findIntersections( List<Point2D> fitPoints, List<Point2D> curvePoints ) {
		List<Line2D> fitLines = Point2D.toSegments( fitPoints );
		List<Line2D> curveLines = Point2D.toSegments( curvePoints );

		List<Point2D> intersections = new ArrayList<>();
		for( Line2D fitLine : fitLines ) {
			for( Line2D curveLine : curveLines ) {
				intersections.addAll( fitLine.intersections( curveLine ) );
			}
		}

		return intersections;
	}

}
