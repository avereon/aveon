package com.avereon.geometry;

import java.util.*;
import java.util.stream.Collectors;

public class Geometry2D {

	public static final double FULL_CIRCLE = Math.PI * 2.0;

	public static final double HALF_CIRCLE = Math.PI;

	public static final double QUARTER_CIRCLE = Math.PI * 0.5;

	public static final double RADIANS_PER_DEGREE = Math.PI / 180.0;

	public static final double DEGREES_PER_RADIAN = 180.0 / Math.PI;

	//	public static final double RESOLUTION_LENGTH = 1e-6;
	//
	//	public static final double RESOLUTION_NORMAL = 1e-10;
	//
	//	public static final double RESOLUTION_ANGLE = Math.atan( RESOLUTION_NORMAL );
	//
	//	public static final double RESOLUTION_SMOOTH = 1e-3;

	/**
	 * Get the distance between a point and a line.
	 *
	 * @param p The point from which to determine the distance.
	 * @param a The first point on the line.
	 * @param b The second point on the line.
	 * @return The distance between the point and line
	 */
	public static double getPointLineDistance( Point2D p, Point2D a, Point2D b ) {
		Point2D t = b.subtract( a );
		double s = t.crossProduct( b.subtract( p ) );
		return Math.abs( s ) / t.magnitude();
	}

	/**
	 * Given a ray starting at p1 and extending to p2, return the distance from the line. Positive values
	 * are to the left of the ray direction. Negative values are to the right of the ray direction. A value
	 * of zero means the point lies on the ray.
	 *
	 * @param a The first point on the line.
	 * @param b The second point on the line.
	 * @param p
	 * @return
	 */
	public static double getRayPointDistance( Point2D a, Point2D b, Point2D p ) {
		Point2D s = b.subtract( p );
		Point2D t = b.subtract( a );
		return -t.crossProduct( s ) / t.magnitude();
	}

	/**
	 * Get the distance between a plane and a point.
	 *
	 * @param origin The origin of the plane.
	 * @param normal The normal of the plane.
	 * @param p The point to which to determine the distance.
	 * @return
	 */
	public static double getPointPlaneDistance( Point2D origin, Point2D normal, Point2D p ) {
		return Math.abs( normal.dotProduct( new Point2D( p.x - origin.x, p.y - origin.y ) ) ) / normal.magnitude();
	}

	/**
	 * Get the angle of a vector in the X-Y plane by computing Math.atan2(
	 * vector.y, vector.x ).
	 *
	 * @param p
	 * @return
	 */
	public static double getAngle( final Point2D p ) {
		return Math.atan2( p.y, p.x );
	}

	public static final double getAngle( final Point2D v1, final Point2D v2 ) {
		return Math.acos( v1.normalize().dotProduct( v2.normalize() ) );
	}

	/**
	 * Get the distance between a point and a line. If the point is outside of the
	 * line segment then Double.NaN is returned.
	 *
	 * @param p The point from which to determine the distance.
	 * @param p The first point on the line.
	 * @param a The second point on the line.
	 * @return
	 */
	public static final double getPointLineBoundDistance( Point2D p, Point2D a, Point2D b ) {
		Point2D pb = p.subtract( b );
		Point2D pa = p.subtract( a );
		Point2D ba = b.subtract( a );
		Point2D ab = a.subtract( b );
		double anglea = Geometry2D.getAngle( ba, pa );
		double angleb = Geometry2D.getAngle( ab, pb );
		if( anglea > Geometry2D.QUARTER_CIRCLE || angleb > Geometry2D.QUARTER_CIRCLE ) return Double.NaN;
		return Math.abs( ba.crossProduct( b.subtract( p ) ) ) / ba.magnitude();
	}

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
	 * @param a The anchor point/Point2D to test
	 * @param b The direction point/Point2D to test
	 * @param c The point/Point2D to compare
	 * @return Minus one if CCW, zero if straight, and one if CW.
	 */
	public static int getSpin( Point2D a, Point2D b, Point2D c ) {
		Point2D ab = a.subtract( b );
		Point2D cb = c.subtract( b );
		double ccw = cb.crossProduct( ab );

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

	public static List<Double> findDistances( List<Point2D> points, List<Point2D> curvePoints ) {
		return points.stream().map( p -> findDistanceToNearestSegment( p, curvePoints ) ).collect( Collectors.toList() );
	}

	public static double findDistanceToNearestSegment( Point2D anchor, List<Point2D> path ) {
		double result = Double.MAX_VALUE;

		Point2D prior = path.get( 0 );
		for( Point2D point : path ) {
			if( point != prior ) {
				double lineDistance = getPointLineDistance( anchor, prior, point );
				if( lineDistance < result ) result = lineDistance;
			}
			prior = point;
		}

		return result;
	}

	public static List<Double> findCcwDistances( List<Point2D> points, List<Point2D> curvePoints ) {
		return points.stream().map( p -> findCcwSegment( p, curvePoints ) ).collect( Collectors.toList() );
	}

	public static double findCcwSegment( Point2D anchor, List<Point2D> path ) {
		double result = Double.MAX_VALUE;

		Point2D prior = path.get( 0 );
		for( Point2D point : path ) {
			if( point != prior ) {
				double lineDistance = getDistanceInNormalArea( prior, point, anchor );
				if( lineDistance < result ) result = lineDistance;
			}
			prior = point;
		}

		return result;
	}

	public static double getDistanceInNormalArea( Point2D p1, Point2D p2, Point2D p ) {
		Point2D rayVector = p2.subtract( p1 );
		Point2D normalVector = new Point2D( -rayVector.getY(), rayVector.getX() );
		Point2D p3 = new Point2D( p1.getX() + normalVector.getX(), p1.getY() + normalVector.getY() );
		Point2D p4 = new Point2D( p2.getX() + normalVector.getX(), p2.getY() + normalVector.getY() );

		double dl = getRayPointDistance( p1, p3, p );
		double dr = getRayPointDistance( p2, p4, p );

		return (dl < 0 && dr > 0) ? getRayPointDistance( p1, p2, p ) : Double.NaN;
	}

	public static List<Line2D> findLines( List<Point2D> points, List<Point2D> curvePoints ) {
		return points.stream().map( p -> new Line2D( p, findNearestPoint( p, curvePoints ) ) ).collect( Collectors.toList() );
	}

	public static Point2D findNearestPoint( Point2D anchor, List<Point2D> path ) {
		Point2D result = null;
		double minDistance = Double.MAX_VALUE;

		for( Point2D point : path ) {
			double distance = anchor.distance( point );
			if( distance < minDistance ) {
				minDistance = distance;
				result = point;
			}
		}

		return result;
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
	 * Calculate the path length of a segmented path.
	 *
	 * @param points The path
	 * @return The length of the path
	 */
	public static double calcPathLength( List<Point2D> points ) {
		if( points.isEmpty() ) return Double.NaN;

		double result = 0;

		Point2D point;
		Point2D prior = points.get( 0 );
		int count = points.size();
		for( int index = 1; index < count; index++ ) {
			point = points.get( index );
			result += prior.distance( point );
			prior = point;
		}

		return result;
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

	/**
	 * Calculate the quadradic <a href="https://mathworld.wolfram.com/BernsteinPolynomial.html">Bernstein polynomial coefficient</a>.
	 *
	 * @param index The polynomial index
	 * @param t The parameter
	 * @return The quadradic Bernstein polynomial coefficient
	 */
	public static double calcQuadBasisEffect( int index, double t ) {
		double s = 1 - t;
		switch( index ) {
			case 0:
				return s * s;
			case 1:
				return 2 * s * t;
			case 2:
				return t * t;
		}
		return Double.NaN;
	}

	/**
	 * Calculate the cubic <a href="https://mathworld.wolfram.com/BernsteinPolynomial.html">Bernstein polynomial coefficient</a>.
	 *
	 * @param index The polynomial index
	 * @param t The parameter
	 * @return The cubic Bernstein polynomial coefficient
	 */
	public static double calcCubicBasisEffect( int index, double t ) {
		double s = 1 - t;
		switch( index ) {
			case 0:
				return s * s * s;
			case 1:
				return 3 * t * s * s;
			case 2:
				return 3 * s * t * t;
			case 3:
				return t * t * t;
		}
		return Double.NaN;
	}

}
