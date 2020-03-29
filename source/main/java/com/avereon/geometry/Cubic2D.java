package com.avereon.geometry;

import javafx.geometry.Point2D;

import java.util.List;

/**
 * The <code>Cubic2D</code> class defines a cubic Bezier parametric curve
 * segment in 2D coordinate space.
 */
public class Cubic2D extends Shape {

	// TODO Change the names of the variables to ax, ay, bx, by, cx, cy, dx, dy

	public final double x1;

	public final double y1;

	public final double ctrlx1;

	public final double ctrly1;

	public final double ctrlx2;

	public final double ctrly2;

	public final double x2;

	public final double y2;

	private int hash;

	/**
	 * Create a new {@code Cubic2D} curve.
	 *
	 * @param x1 the X coordinate of the first control point
	 * @param y1 the Y coordinate of the first control point
	 * @param ctrlx1 the X coordinate of the second control point
	 * @param ctrly1 the Y coordinate of the second control point
	 * @param ctrlx2 the X coordinate of the third control point
	 * @param ctrly2 the Y coordinate of the third control point
	 * @param x2 the X coordinate of the last control point
	 * @param y2 the Y coordinate of the last control point
	 */
	public Cubic2D( double x1, double y1, double ctrlx1, double ctrly1, double ctrlx2, double ctrly2, double x2, double y2 ) {
		this.x1 = x1;
		this.y1 = y1;
		this.ctrlx1 = ctrlx1;
		this.ctrly1 = ctrly1;
		this.ctrlx2 = ctrlx2;
		this.ctrly2 = ctrly2;
		this.x2 = x2;
		this.y2 = y2;
	}

	/**
	 * Create a new {@code Cubic2D} curve.
	 *
	 * @param a control point a
	 * @param b control point b
	 * @param c control point c
	 * @param d control point d
	 */
	public Cubic2D( Point2D a, Point2D b, Point2D c, Point2D d ) {
		this( a.getX(), a.getY(), b.getX(), b.getY(), c.getX(), c.getY(), d.getX(), d.getY() );
	}

	/**
	 * Compute bounding box of the curve.
	 *
	 * @return the bounding box of the curve
	 */
	public Bounds2D getBounds() {
		double left = Math.min( Math.min( x1, x2 ), Math.min( ctrlx1, ctrlx2 ) );
		double top = Math.min( Math.min( y1, y2 ), Math.min( ctrly1, ctrly2 ) );
		double right = Math.max( Math.max( x1, x2 ), Math.max( ctrlx1, ctrlx2 ) );
		double bottom = Math.max( Math.max( y1, y2 ), Math.max( ctrly1, ctrly2 ) );
		return new Bounds2D( left, top, right, bottom );
	}

	/**
	 * Evaluates the curve at the given value. The value 0 corresponds to the
	 * start point of the curve and the value 1 corresponds to the end point
	 * of the curve. Values outside the range 0 to 1 are undefined.
	 *
	 * @param t the value at which to evaluate the curve
	 * @return a new point that is the location on the curve at that value
	 */
	public Point2D eval( float t ) {
		return new Point2D( calcX( t ), calcY( t ) );
	}

	/**
	 * Calculate the derivative of the curve at the given value. The value 0
	 * corresponds to the derivative at the start point of the curve and the
	 * value 1 corresponds to the derivative at the end point of the curve.
	 * Values outside the range 0 to 1 are undefined.
	 *
	 * @param t value at which to compute the derivative of the curve
	 */
	public Point2D evalDt( double t ) {
		double u = 1 - t;
		double x = 3 * ((ctrlx1 - x1) * u * u + 2 * (ctrlx2 - ctrlx1) * u * t + (x2 - ctrlx2) * t * t);
		double y = 3 * ((ctrly1 - y1) * u * u + 2 * (ctrly2 - ctrly1) * u * t + (y2 - ctrly2) * t * t);
		return new Point2D( x, y );
	}

	/**
	 * Calculate the square of the flatness of the curve with given coordinates.
	 * The flatness is the maximum distance of a control point from the line
	 * connecting the end points.
	 *
	 * @param x1 the X coordinate of the start point
	 * @param y1 the Y coordinate of the start point
	 * @param ctrlx1 the X coordinate of the first control point
	 * @param ctrly1 the Y coordinate of the first control point
	 * @param ctrlx2 the X coordinate of the second control point
	 * @param ctrly2 the Y coordinate of the second control point
	 * @param x2 the X coordinate of the end point
	 * @param y2 the Y coordinate of the end point
	 * @return the square of the flatness of the curve
	 */
	public static double getFlatnessSq( double x1, double y1, double ctrlx1, double ctrly1, double ctrlx2, double ctrly2, double x2, double y2 ) {
		return Math.max( Line2D.ptSegDistSq( x1, y1, x2, y2, ctrlx1, ctrly1 ), Line2D.ptSegDistSq( x1, y1, x2, y2, ctrlx2, ctrly2 ) );
	}

	/**
	 * Returns the flatness of the cubic curve specified
	 * by the indicated control points. The flatness is the maximum distance
	 * of a control point from the line connecting the end points.
	 *
	 * @param x1 the X coordinate of the start point
	 * @param y1 the Y coordinate of the start point
	 * @param ctrlx1 the X coordinate of the first control point
	 * @param ctrly1 the Y coordinate of the first control point
	 * @param ctrlx2 the X coordinate of the second control point
	 * @param ctrly2 the Y coordinate of the second control point
	 * @param x2 the X coordinate of the end point
	 * @param y2 the Y coordinate of the end point
	 * @return the flatness of the curve
	 */
	public static float getFlatness( double x1, double y1, double ctrlx1, double ctrly1, double ctrlx2, double ctrly2, double x2, double y2 ) {
		return (float)Math.sqrt( getFlatnessSq( x1, y1, ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2 ) );
	}

	/**
	 * Calculate the square of the flatness of this curve. The flatness is the
	 * maximum distance of a control point from the line connecting the
	 * end points.
	 *
	 * @return the square of the flatness of this curve
	 */
	public double getFlatnessSq() {
		return getFlatnessSq( x1, y1, ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2 );
	}

	/**
	 * Calculate the flatness of this curve.  The flatness is the maximum distance
	 * of a control point from the line connecting the end points.
	 *
	 * @return the flatness of this curve.
	 */
	public float getFlatness() {
		return getFlatness( x1, y1, ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2 );
	}

	/**
	 * Subdivide this curve at the given value. The value 0 corresponds to the
	 * start point of the curve and the value 1 corresponds to the end point
	 * of the curve. Values outside the range 0 to 1 are undefined.
	 *
	 * @param t the value at which to subdivide the curve
	 * @return a two element array containing the two halves of the curve
	 */
	public Cubic2D[] subdivide( double t ) {
		double npx = calcX( t );
		double npy = calcY( t );

		double x1 = this.x1;
		double y1 = this.y1;
		double c1x = this.ctrlx1;
		double c1y = this.ctrly1;
		double c2x = this.ctrlx2;
		double c2y = this.ctrly2;
		double x2 = this.x2;
		double y2 = this.y2;
		double u = 1 - t;
		double hx = u * c1x + t * c2x;
		double hy = u * c1y + t * c2y;

		double lc1x = u * x1 + t * c1x;
		double lc1y = u * y1 + t * c1y;
		double lc2x = u * lc1x + t * hx;
		double lc2y = u * lc1y + t * hy;
		Cubic2D left = new Cubic2D( x1, y1, lc1x, lc1y, lc2x, lc2y, npx, npy );

		double rc2x = u * c2x + t * x2;
		double rc2y = u * c2y + t * y2;
		double rc1x = u * hx + t * rc2x;
		double rc1y = u * hy + t * rc2y;
		Cubic2D right = new Cubic2D( npx, npy, rc1x, rc1y, rc2x, rc2y, x2, y2 );

		return new Cubic2D[]{ left, right };
	}

	/**
	 * Subdivide the given curve.
	 *
	 * @param src the curve to be subdivided
	 */
	public static Cubic2D[] subdivide( Cubic2D src ) {
		double x1 = src.x1;
		double y1 = src.y1;
		double ctrlx1 = src.ctrlx1;
		double ctrly1 = src.ctrly1;
		double ctrlx2 = src.ctrlx2;
		double ctrly2 = src.ctrly2;
		double x2 = src.x2;
		double y2 = src.y2;
		double centerx = 0.5 * (ctrlx1 + ctrlx2);
		double centery = 0.5 * (ctrly1 + ctrly2);
		ctrlx1 = 0.5 * (x1 + ctrlx1);
		ctrly1 = 0.5 * (y1 + ctrly1);
		ctrlx2 = 0.5 * (x2 + ctrlx2);
		ctrly2 = 0.5 * (y2 + ctrly2);
		double ctrlx12 = 0.5 * (ctrlx1 + centerx);
		double ctrly12 = 0.5 * (ctrly1 + centery);
		double ctrlx21 = 0.5 * (ctrlx2 + centerx);
		double ctrly21 = 0.5 * (ctrly2 + centery);
		centerx = 0.5 * (ctrlx12 + ctrlx21);
		centery = 0.5 * (ctrly12 + ctrly21);

		Cubic2D left = new Cubic2D( x1, y1, ctrlx1, ctrly1, ctrlx12, ctrly12, centerx, centery );
		Cubic2D right = new Cubic2D( centerx, centery, ctrlx21, ctrly21, ctrlx2, ctrly2, x2, y2 );

		return new Cubic2D[]{ left, right };
	}

	/**
	 * Subdivide the given curve.
	 *
	 * @param src the curve to be subdivided
	 */
	public static Cubic2D[] subdivide( Cubic2D src, double t ) {
		double x1 = src.x1;
		double y1 = src.y1;
		double ctrlx1 = src.ctrlx1;
		double ctrly1 = src.ctrly1;
		double ctrlx2 = src.ctrlx2;
		double ctrly2 = src.ctrly2;
		double x2 = src.x2;
		double y2 = src.y2;
		double centerx = t * (ctrlx1 + ctrlx2);
		double centery = t * (ctrly1 + ctrly2);
		ctrlx1 = t * (x1 + ctrlx1);
		ctrly1 = t * (y1 + ctrly1);
		ctrlx2 = t * (x2 + ctrlx2);
		ctrly2 = t * (y2 + ctrly2);
		double ctrlx12 = t * (ctrlx1 + centerx);
		double ctrly12 = t * (ctrly1 + centery);
		double ctrlx21 = t * (ctrlx2 + centerx);
		double ctrly21 = t * (ctrly2 + centery);
		centerx = t * (ctrlx12 + ctrlx21);
		centery = t * (ctrly12 + ctrly21);

		Cubic2D left = new Cubic2D( x1, y1, ctrlx1, ctrly1, ctrlx12, ctrly12, centerx, centery );
		Cubic2D right = new Cubic2D( centerx, centery, ctrlx21, ctrly21, ctrlx2, ctrly2, x2, y2 );

		return new Cubic2D[]{ left, right };
	}

	// TODO Future methods

	/**
	 * Get the list of points that estimate the curve with a specific number of
	 * line segments.
	 *
	 * @param count The number of line segments to produce
	 * @return The points that define the curve as line segements
	 */
	public List<Point2D> toPoints( int count ) {
		return List.of();
	}

	/**
	 * Get the list of points that estimate the curve as line segments no larger
	 * than size.
	 *
	 * @param size The largest permitted line segment length
	 * @return The points that estimate the curve as line segments
	 */
	public List<Point2D> toPoints( double size ) {
		return List.of();
	}

	private double calcX( final double t ) {
		final double u = 1 - t;
		return (u * u * u * x1 + 3 * (t * u * u * ctrlx1 + t * t * u * ctrlx2) + t * t * t * x2);
	}

	private double calcY( final double t ) {
		final double u = 1 - t;
		return (u * u * u * y1 + 3 * (t * u * u * ctrly1 + t * t * u * ctrly2) + t * t * t * y2);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals( Object object ) {
		if( object == this ) return true;
		if( !(object instanceof Cubic2D) ) return false;
		Cubic2D that = (Cubic2D)object;
		return this.x1 == that.x1 && this.y1 == that.y1 && this.ctrlx1 == that.ctrlx1 && this.ctrly1 == that.ctrly1 && this.ctrlx2 == that.ctrlx2 && this.ctrly2 == that.ctrly2 && this.x2 == that.x2 && this.y2 == that.y2;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		if( hash == 0 ) {
			long bits = 11L;
			bits = 73L * bits + Double.doubleToLongBits( x1 );
			bits = 73L * bits + Double.doubleToLongBits( y1 );
			bits = 73L * bits + Double.doubleToLongBits( ctrlx1 );
			bits = 73L * bits + Double.doubleToLongBits( ctrly1 );
			bits = 73L * bits + Double.doubleToLongBits( ctrlx2 );
			bits = 73L * bits + Double.doubleToLongBits( ctrly2 );
			bits = 73L * bits + Double.doubleToLongBits( x2 );
			bits = 73L * bits + Double.doubleToLongBits( y2 );
			hash = (int)(bits ^ (bits >> 32));
		}
		return hash;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "Cubic2D[x1=" + x1 + ",y1=" + y1 + ",ctrlx1=" + ctrlx1 + ",ctrly1=" + ctrly1 + ",ctrlx2=" + ctrlx2 + ",ctrly2=" + ctrly2 + ",x2=" + x2 + ",y2=" + y2 + "]";
	}

}
