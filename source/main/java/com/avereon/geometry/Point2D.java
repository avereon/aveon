package com.avereon.geometry;

import java.util.ArrayList;
import java.util.List;

/**
 * An immutable 2D point that contains the x and y components of a point or vector.
 */
public class Point2D {

	/**
	 * Point or vector with all coordinates set to zero.
	 */
	public static final Point2D ZERO = new Point2D( 0.0, 0.0 );

	public final double x;

	public final double y;

	private int hash = 0;

	/**
	 * Create a new {@code Point2D}.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public Point2D( double x, double y ) {
		this.x = x;
		this.y = y;
	}

	public Point2D( Point2D p ) {
		this( p.x, p.y );
		this.hash = p.hash;
	}

	public static Point2D of( double[] p ) {
		return new Point2D( p[ 0 ], p[ 1 ] );
	}

	public static Point2D of( double x, double y ) {
		return new Point2D( x, y );
	}

	public final double getX() {
		return x;
	}

	public final double getY() {
		return y;
	}

	/**
	 * Computes the distance between this point and the coordinates.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return the distance between this point and the coordinates
	 */
	public double distance( double x, double y ) {
		double a = getX() - x;
		double b = getY() - y;
		return Math.sqrt( a * a + b * b );
	}

	/**
	 * Computes the distance between this point and another {@code point}.
	 *
	 * @param point the other point
	 * @return the distance between this point and another {@code point}
	 * @throws NullPointerException if the {@code point} is null
	 */
	public double distance( Point2D point ) {
		return distance( point.getX(), point.getY() );
	}

	/**
	 * Create a new point with the coordinates added to the coordinates of this
	 * point.
	 *
	 * @param x the X coordinate
	 * @param y the Y coordinate
	 * @return a new point with the coordinates added
	 */
	public Point2D add( double x, double y ) {
		return new Point2D( getX() + x, getY() + y );
	}

	/**
	 * Create a new point with the coordinates of another point added to the
	 * coordinates of this point.
	 *
	 * @param point the other point whose coordinates are to be added
	 * @return a new point with coordinates added
	 * @throws NullPointerException if the {@code point} is null
	 */
	public Point2D add( Point2D point ) {
		return add( point.getX(), point.getY() );
	}

	/**
	 * Create a new point with the coordinates subtracted from the coordinates
	 * of this point.
	 *
	 * @param x the X coordinate
	 * @param y the Y coordinate
	 * @return the point with coordinates subtracted
	 */
	public Point2D subtract( double x, double y ) {
		return new Point2D( getX() - x, getY() - y );
	}

	/**
	 * Create a new point with the coordinates of another point subtracted
	 * from the coordinates of this point.
	 *
	 * @param point the other point whose coordinates are to be subtracted
	 * @return the point with coordinates subtracted
	 * @throws NullPointerException if the {@code point} is null
	 */
	public Point2D subtract( Point2D point ) {
		return subtract( point.getX(), point.getY() );
	}

	/**
	 * Create a new point with the coordinates of this point multiplied
	 * by a factor.
	 *
	 * @param factor the factor with which to multiply the coordinates
	 * @return the point with coordinates multiplied
	 */
	public Point2D multiply( double factor ) {
		return new Point2D( getX() * factor, getY() * factor );
	}

	/**
	 * Create a new point that is the normalized coordinates of this point.
	 * That is point with the same direction and magnitude equal to one.
	 * If the magnitude of this point is zero, a zero point is returned.
	 *
	 * @return the normalized {@code point2D}
	 */
	public Point2D normalize() {
		final double mag = magnitude();
		if( mag == 0.0 ) return new Point2D( Double.NaN, Double.NaN );
		return new Point2D( getX() / mag, getY() / mag );
	}

	/**
	 * Create a new point that is the midpoint between this point and the
	 * coordinates.
	 *
	 * @param x the X coordinate
	 * @param y the Y coordinate
	 * @return the midpoint between this point and the coordinates
	 */
	public Point2D midpoint( double x, double y ) {
		return new Point2D( x + 0.5 * (getX() - x), y + 0.5 * (getY() - y) );
	}

	/**
	 * Create a new point that is the midpoint between this point and another
	 * point.
	 *
	 * @param point the other point
	 * @return the midpoint between this point and the other point
	 * @throws NullPointerException if the {@code point} is null
	 */
	public Point2D midpoint( Point2D point ) {
		return midpoint( point.getX(), point.getY() );
	}

	/**
	 * Compute the angle (in degrees) between this point/vector coordinates and
	 * another point/vector coordinates. This returns {@code Double.NaN} if any
	 * of the two points/vectors is a zero point/vector.
	 *
	 * @param x the X coordinate
	 * @param y the Y coordinate
	 * @return the angle between the two points/vectors in degrees
	 */
	public double angle( double x, double y ) {
		return angle( getX(), getY(), x, y );
	}

	/**
	 * Compute the angle (in degrees) between this point/vector and another
	 * point/vector. This returns {@code Double.NaN} if any of the two
	 * points/vectors is a zero point/vector.
	 *
	 * @param point the other point/vector
	 * @return the angle between the two points/vectors in degrees
	 * @throws NullPointerException if the {@code point} is null
	 */
	public double angle( Point2D point ) {
		return angle( point.getX(), point.getY() );
	}

	/**
	 * Compute the angle (in degrees) between three points with this point
	 * as the vertex. This return {@code Double.NaN} if the three points/vectors
	 * are not different from one another.
	 *
	 * @param p1 start point/vector
	 * @param p2 end point/vector
	 * @return the angle between the points/vectors (p1, this and p2) in degrees
	 * @throws NullPointerException if {@code p1} or {@code p2} is null
	 */
	public double angle( Point2D p1, Point2D p2 ) {
		final double x = getX();
		final double y = getY();
		final double ax = p1.getX() - x;
		final double ay = p1.getY() - y;
		final double bx = p2.getX() - x;
		final double by = p2.getY() - y;
		return angle( ax, ay, bx, by );
	}

	/**
	 * Calculate the angle (in degrees) between two points/vectors. This returns
	 * {@see Double.NaN} if any of the two points/vectors is a zero point/vector.
	 *
	 * @param ax the point a X coordinate
	 * @param ay the point a Y coordinate
	 * @param bx the point b X coordinate
	 * @param by the point b Y coordinate
	 * @return the angle between the two points/vectors in degrees
	 */
	public double angle( double ax, double ay, double bx, double by ) {
		final double delta = (ax * bx + ay * by) / Math.sqrt( (ax * ax + ay * ay) * (bx * bx + by * by) );
		if( delta > 1.0 ) return 0.0;
		if( delta < -1.0 ) return 180.0;
		return Math.toDegrees( Math.acos( delta ) );
	}

	/**
	 * Compute the magnitude of this point/vector.
	 *
	 * @return the magnitude of this point/vector
	 */
	public double magnitude() {
		final double x = getX();
		final double y = getY();
		return Math.sqrt( x * x + y * y );
	}

	/**
	 * Compute the dot product of the coordinates and this point/vector.
	 *
	 * @param x the X coordinate
	 * @param y the Y coordinate
	 * @return the dot product of the coordinates and this point/vector
	 */
	public double dotProduct( double x, double y ) {
		return getX() * x + getY() * y;
	}

	/**
	 * Compute the dot product of this point/vector and another point/vector.
	 *
	 * @param point the other point/vector
	 * @return the dot product of the two vectors
	 * @throws NullPointerException if the {@code point} is null
	 */
	public double dotProduct( Point2D point ) {
		return dotProduct( point.getX(), point.getY() );
	}

	/**
	 * Compute the cross product of the coordinates and this point/vector.
	 *
	 * @param x the X coordinate
	 * @param y the Y coordinate
	 * @return the cross product of the coordinates and this point/vector
	 */
	public double crossProduct( double x, double y ) {
		return this.x * y - this.y * x;
	}

	/**
	 * Compute the cross product of this point/vector and another point/vector.
	 *
	 * @param point the other point/vector
	 * @return the cross product of the two vectors
	 * @throws NullPointerException if the {@code point} is null
	 */
	public double crossProduct( Point2D point ) {
		return crossProduct( point.getX(), point.getY() );
	}

	/**
	 * Calculate a new point interpolated between this point and another point.
	 *
	 * @param point the other point
	 * @param t the interpolation ratio between 0.0 and 1.0 inclusive
	 */
	public Point2D interpolate( Point2D point, double t ) {
		if( t <= 0.0 ) return this;
		if( t >= 1.0 ) return point;
		double x = getX() + (point.x - this.x) * t;
		double y = getY() + (point.y - this.y) * t;
		return new Point2D( x, y );
	}

	public static List<Line2D> toSegments( List<Point2D> points ) {
		List<Line2D> segments = new ArrayList<>( points.size() - 1 );

		Point2D prior = points.get( 0 );
		for( Point2D point : points ) {
			if( point == prior ) continue;
			segments.add( new Line2D( prior, point ) );
			prior = point;
		}

		return segments;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals( Object object ) {
		if( object == this ) return true;
		if( !(object instanceof Point2D that) ) return false;
		return this.x == that.x && this.y == that.y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		if( hash == 0 ) {
			long bits = 11L;
			bits = 73L * bits + Double.doubleToLongBits( x );
			bits = 73L * bits + Double.doubleToLongBits( y );
			hash = (int)(bits ^ (bits >> 32));
		}
		return hash;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "Point2D[x=" + x + ",y=" + y + "]";
	}

}
