package com.avereon.geometry;

/**
 * An immutable 3D point that contains the x, y and z components of a point or vector.
 */
public class Point3D {

	/**
	 * Point or vector with all coordinates set to zero.
	 */
	public static final Point3D ZERO = new Point3D( 0.0, 0.0, 0.0 );

	public final double x;

	public final double y;

	public final double z;

	private int hash = 0;

	/**
	 * Create a new {@code Point3D}.
	 *
	 * @param x The X coordinate
	 * @param y The Y coordinate
	 * @param z The Z coordinate
	 */
	public Point3D( double x, double y, double z ) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public final double getX() {
		return x;
	}

	public final double getY() {
		return y;
	}

	public final double getZ() {
		return z;
	}

	/**
	 * Compute the distance between this point and the coordinates.
	 *
	 * @param x1 the x coordinate
	 * @param y1 the y coordinate
	 * @param z1 the z coordinate
	 * @return the distance between this point and the coordinates
	 */
	public double distance( double x1, double y1, double z1 ) {
		double a = getX() - x1;
		double b = getY() - y1;
		double c = getZ() - z1;
		return Math.sqrt( a * a + b * b + c * c );
	}

	/**
	 * Compute the distance between this point and another point.
	 *
	 * @param point the other point
	 * @return the distance between this point and another point
	 * @throws NullPointerException if the {@code point} is null
	 */
	public double distance( Point3D point ) {
		return distance( point.getX(), point.getY(), point.getZ() );
	}

	/**
	 * Create a new point with the coordinates added to the coordinates
	 * of this point.
	 *
	 * @param x the X coordinate
	 * @param y the Y coordinate
	 * @param z the Z coordinate
	 * @return a new point with the coordinates added
	 */
	public Point3D add( double x, double y, double z ) {
		return new Point3D( getX() + x, getY() + y, getZ() + z );
	}

	/**
	 * Create a new point with the coordinates of another point added to the
	 * coordinates of this point.
	 *
	 * @param point the other point whose coordinates are to be added
	 * @return a new point with coordinates added
	 * @throws NullPointerException if the {@code point} is null
	 */
	public Point3D add( Point3D point ) {
		return add( point.getX(), point.getY(), point.getZ() );
	}

	/**
	 * Create a new point with the coordinates subtracted from the coordinates
	 * of this point.
	 *
	 * @param x the X coordinate
	 * @param y the Y coordinate
	 * @param z the Z coordinate
	 * @return the point with coordinates subtracted
	 */
	public Point3D subtract( double x, double y, double z ) {
		return new Point3D( getX() - x, getY() - y, getZ() - z );
	}

	/**
	 * Create a new point with the coordinates of another point subtracted
	 * from the coordinates of this point.
	 *
	 * @param point the other point whose coordinates are to be subtracted
	 * @return the point with coordinates subtracted
	 * @throws NullPointerException if the {@code point} is null
	 */
	public Point3D subtract( Point3D point ) {
		return subtract( point.getX(), point.getY(), point.getZ() );
	}

	/**
	 * Create a new point with the coordinates of this point multiplied
	 * by a factor.
	 *
	 * @param factor the factor with which to multiply the coordinates
	 * @return the point with coordinates multiplied
	 */
	public Point3D multiply( double factor ) {
		return new Point3D( getX() * factor, getY() * factor, getZ() * factor );
	}

	/**
	 * Create a new point that is the normalized coordinates of this point.
	 * That is point with the same direction and magnitude equal to one.
	 * If the magnitude of this point is zero, a zero point is returned.
	 *
	 * @return the normalized {@code point2D}
	 */
	public Point3D normalize() {
		final double mag = magnitude();
		if( mag == 0.0 ) return new Point3D( 0.0, 0.0, 0.0 );
		return new Point3D( getX() / mag, getY() / mag, getZ() / mag );
	}

	/**
	 * Create a new point that is the midpoint between this point and the
	 * coordinates.
	 *
	 * @param x the X coordinate
	 * @param y the Y coordinate
	 * @param z the Z coordinate
	 * @return the midpoint between this point and the coordinates
	 */
	public Point3D midpoint( double x, double y, double z ) {
		return new Point3D( x + 0.5 * (getX() - x), y + 0.5 * (getY() - y), z + 0.5 * (getZ() - z) );
	}

	/**
	 * Create a new point that is the midpoint between this point and another
	 * point.
	 *
	 * @param point the other point
	 * @return the midpoint between this point and the other point
	 * @throws NullPointerException if the {@code point} is null
	 */
	public Point3D midpoint( Point3D point ) {
		return midpoint( point.getX(), point.getY(), point.getZ() );
	}

	/**
	 * Compute the angle (in degrees) between this point/vector coordinates and
	 * another point/vector coordinates. This returns {@code Double.NaN} if any
	 * of the two points/vectors is a zero point/vector.
	 *
	 * @param x the X coordinate
	 * @param y the Y coordinate
	 * @param z the Z coordinate
	 * @return the angle between the two points/vectors in degrees
	 */
	public double angle( double x, double y, double z ) {
		return angle( this.x, this.y, this.z, x, y, z );
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
	public double angle( Point3D point ) {
		return angle( point.getX(), point.getY(), point.getZ() );
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
	public double angle( Point3D p1, Point3D p2 ) {
		final double ax = p1.getX() - this.x;
		final double ay = p1.getY() - this.y;
		final double az = p1.getZ() - this.z;
		final double bx = p2.getX() - this.x;
		final double by = p2.getY() - this.y;
		final double bz = p2.getZ() - this.z;
		return angle( ax, ay, az, bx, by, bz );
	}

	/**
	 * Calculate the angle (in degrees) between two points/vectors. This returns
	 * {@see Double.NaN} if any of the two points/vectors is a zero point/vector.
	 *
	 * @param ax the point a X coordinate
	 * @param ay the point a Y coordinate
	 * @param az the point a Z coordinate
	 * @param bx the point b X coordinate
	 * @param by the point b Y coordinate
	 * @param bz the point b Z coordinate
	 * @return the angle between the two points/vectors in degrees
	 */
	public double angle( double ax, double ay, double az, double bx, double by, double bz ) {
		final double delta = (ax * bx + ay * by + az * bz) / Math.sqrt( (ax * ax + ay * ay + az * az) * (bx * bx + by * by + bz * bz) );
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
		return Math.sqrt( this.x * this.x + this.y * this.y + this.z * this.z );
	}

	/**
	 * Compute the dot product of the coordinates and this point/vector.
	 *
	 * @param x the X coordinate
	 * @param y the Y coordinate
	 * @param z the Z coordinate
	 * @return the dot product of the coordinates and this point/vector
	 */
	public double dotProduct( double x, double y, double z ) {
		return this.x * x + this.y * y + this.z * z;
	}

	/**
	 * Compute the dot product of this point/vector and another point/vector.
	 *
	 * @param point the other point/vector
	 * @return the dot product of the two vectors
	 * @throws NullPointerException if the {@code point} is null
	 */
	public double dotProduct( Point3D point ) {
		return dotProduct( point.getX(), point.getY(), point.z );
	}

	/**
	 * Compute the cross product of the coordinates and this point/vector.
	 *
	 * @param x the X coordinate
	 * @param y the Y coordinate
	 * @param z the Z coordinate
	 * @return the cross product of the coordinates and this point/vector
	 */
	public Point3D crossProduct( double x, double y, double z ) {
		return new Point3D( this.y * z - this.z * y, this.z * x - this.x * z, this.x * y - this.y * x );
	}

	/**
	 * Compute the cross product of this point/vector and another point/vector.
	 *
	 * @param point the other point/vector
	 * @return the cross product of the two vectors
	 * @throws NullPointerException if the {@code point} is null
	 */
	public Point3D crossProduct( Point3D point ) {
		return crossProduct( point.x, point.y, point.z );
	}

	/**
	 * Calculate a new point interpolated between this point and another point.
	 *
	 * @param point the other point
	 * @param t the interpolation ratio between 0.0 and 1.0 inclusive
	 */
	public Point3D interpolate( Point3D point, double t ) {
		if( t <= 0.0 ) return this;
		if( t >= 1.0 ) return point;
		double x = getX() + (point.x - this.x) * t;
		double y = getY() + (point.y - this.y) * t;
		double z = getZ() + (point.z - this.z) * t;
		return new Point3D( x, y, z );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals( Object object ) {
		if( object == this ) return true;
		if( !(object instanceof Point3D) ) return false;
		Point3D that = (Point3D)object;
		return this.x == that.x && this.y == that.y && this.z == that.z;
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
			bits = 73L * bits + Double.doubleToLongBits( z );
			hash = (int)(bits ^ (bits >> 32));
		}
		return hash;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "Point3D[x=" + x + ",y=" + y + ",z=" + z + "]";
	}

}
