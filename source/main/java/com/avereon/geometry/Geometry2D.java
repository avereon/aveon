package com.avereon.geometry;

import java.util.*;
import java.util.stream.Collectors;

public class Geometry2D {

	/**
	 * Determine if two line segments intersect.
	 *
	 * @param a Line segment a
	 * @param b Line segment b
	 * @return True if the line segments intersect, false otherwise
	 */
	public static boolean areIntersecting( Line2D a, Line2D b ) {
		return areIntersecting( a.a, a.b, b.a, b.b );
	}

	/**
	 * Determine if two line segments intersect given the respective end points.
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
	 * Determine if the three points are in counter-clockwise(-1), straight(0),
	 * or clockwise(1) order.
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

	/**
	 * Find the intersections between two segmented paths.
	 *
	 * @param a A segmented path
	 * @param b Another segmented path
	 * @return The list of intersection points
	 */
	public static List<Point2D> findIntersections( List<Point2D> a, List<Point2D> b ) {
		List<Line2D> aSegments = Point2D.toSegments( a );
		List<Line2D> bSegments = Point2D.toSegments( b );

		List<Point2D> intersections = new ArrayList<>();
		for( Line2D aSegment : aSegments ) {
			for( Line2D bSegment : bSegments ) {
				Intersection2D intersection = aSegment.intersection( bSegment );
				if( intersection.getType() == Intersection2D.Type.INTERSECTION ) {
					intersections.addAll( intersection.getPoints() );
				} else {
					return List.of();
				}
			}
		}

		return intersections;
	}

	public static List<Double> findAreas( List<Point2D> fitPoints, List<Point2D> curvePoints ) {
		List<List<Point2D>> polygons = findPolygons( fitPoints, curvePoints );
		if( polygons.size() == 0 ) throw new RuntimeException( "No polygons found to calculate area" );
		return polygons.stream().map( Geometry2D::calcPolygonArea ).collect( Collectors.toList() );
	}

	/**
	 * Find the polygons (areas of intersection) between two segmented paths. It
	 * is assumed that both paths are ordered. For any polygons to be returned
	 * there must be two or more intersections. If there are not enough
	 * intersection points then an empty list is returned. The generated polygons
	 * are in closed counterclockwise form as returned from {@link #toCcwPolygon}.
	 *
	 * @param a Segmented path a
	 * @param b Segmented path b
	 * @return The list of polygons (areas of intersection)
	 */
	public static List<List<Point2D>> findPolygons( List<Point2D> a, List<Point2D> b ) {
		List<List<Point2D>> polygons = new ArrayList<>();

		// Performance improvement to use the smallest count on the outside loop
		if( a.size() > b.size() ) {
			List<Point2D> c = b;
			b = a;
			a = c;
		}

		List<Line2D> aSegments = Point2D.toSegments( a );
		List<Line2D> bSegments = Point2D.toSegments( b );

		int aOffset = 0;
		int bOffset = 0;
		int aCount = aSegments.size();
		int bCount = bSegments.size();
		List<Point2D> c = new ArrayList<>();
		List<Point2D> d = new ArrayList<>();
		Point2D priorIntersection = null;

		for( int aIndex = 0; aIndex < aCount; aIndex++ ) {
			for( int bIndex = bOffset; bIndex < bCount; bIndex++ ) {
				Line2D aSegment = aSegments.get( aIndex );
				Line2D bSegment = bSegments.get( bIndex );
				Intersection2D intersection = aSegment.intersection( bSegment );

				// If segments are the same jump to the next segments
				if( intersection.getType() == Intersection2D.Type.SAME ) aIndex++;
				if( intersection.getType() != Intersection2D.Type.INTERSECTION ) continue;

				Point2D intersectionPoint = intersection.getPoints().get( 0 );

				if( !Objects.equals( intersectionPoint, priorIntersection ) ) {
					if( priorIntersection != null ) {
						// Add points up to intersecting segments
						c.addAll( a.subList( aOffset, aIndex + 1 ) );
						d.addAll( b.subList( bOffset, bIndex + 1 ) );

						// Add closing intersection
						c.add( intersectionPoint );
						d.add( intersectionPoint );

						// Add polygon
						List<Point2D> e = c.stream().distinct().collect( Collectors.toList() );
						List<Point2D> f = d.stream().distinct().collect( Collectors.toList() );
						polygons.add( toCcwPolygon( e, f ) );

						// Start new paths
						c = new ArrayList<>();
						d = new ArrayList<>();
					}

					// Add starting intersection
					c.add( intersectionPoint );
					d.add( intersectionPoint );
				}

				// Store state
				priorIntersection = intersectionPoint;
				aOffset = aIndex + 1;
				bOffset = bIndex + 1;
			}
		}

		return polygons;
	}

	/**
	 * Convert two segmented paths that start and end at the same point into a
	 * closed counterclockwise segmented path. It is assumed that the two
	 * segmented paths are already ordered, that there are two or more points in
	 * one of the paths, that there are three or more points in the other path,
	 * that the first and last points are equal and that the paths do not
	 * intersect.
	 *
	 * @param a Segmented path a
	 * @param b Segmented path b
	 * @return A counterclockwise polygon path
	 */
	public static List<Point2D> toCcwPolygon( List<Point2D> a, List<Point2D> b ) {
		// It is assumed that a and b are ordered
		// and that there are two or more points in one list
		// and that there are three or more points in the other list
		// and that the first and last points are equal
		// and that the paths do not intersect

		if( b.size() < 3 && a.size() < 3 ) throw new IllegalArgumentException( "Not enough points in lists" );
		if( !Objects.equals( a.get( 0 ), b.get( 0 ) ) ) throw new IllegalArgumentException( "Starting point mismatch" );
		if( !Objects.equals( a.get( a.size() - 1 ), b.get( b.size() - 1 ) ) ) throw new IllegalArgumentException( "Ending point mismatch" );

		// One list will be CW or CCW from the other, just need to get them ordered correctly
		Point2D pA = a.get( 0 );
		Point2D pB = a.get( 1 );
		Point2D pC = b.get( 1 );

		a = new ArrayList<>( a );
		b = new ArrayList<>( b );

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

	/**
	 * Calculate the area of a polygon using <a href="https://en.wikipedia.org/wiki/Green%27s_theorem#Area_calculation">Green's theorem</a>.
	 * It is assumed that the points form a closed, counterclockwise, non-intersecting segmented path.
	 *
	 * @param points The polygon
	 * @return The area of the polygon
	 */
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

			result += 0.5 * (p2.x + p1.x) * (p2.y - p1.y);
		}

		return result;
	}

	/**
	 * Determine the bounds of the collection of points.
	 *
	 * @param points A collection of points
	 * @return The bounds of the points
	 */
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

}
