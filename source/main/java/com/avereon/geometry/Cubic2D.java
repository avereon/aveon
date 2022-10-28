package com.avereon.geometry;

import java.util.ArrayList;
import java.util.List;

/**
 * The <code>Cubic2D</code> class defines a cubic Bezier parametric curve
 * segment in 2D coordinate space.
 */
public class Cubic2D extends Shape {

	public final double ax;

	public final double ay;

	public final double bx;

	public final double by;

	public final double cx;

	public final double cy;

	public final double dx;

	public final double dy;

	public final Point2D a;

	public final Point2D b;

	public final Point2D c;

	public final Point2D d;

	private int hash;

	/**
	 * Create a new {@code Cubic2D} curve.
	 *
	 * @param ax the X coordinate of the first control point
	 * @param ay the Y coordinate of the first control point
	 * @param bx the X coordinate of the second control point
	 * @param by the Y coordinate of the second control point
	 * @param cx the X coordinate of the third control point
	 * @param cy the Y coordinate of the third control point
	 * @param dx the X coordinate of the last control point
	 * @param dy the Y coordinate of the last control point
	 */
	public Cubic2D( double ax, double ay, double bx, double by, double cx, double cy, double dx, double dy ) {
		this.ax = ax;
		this.ay = ay;
		this.bx = bx;
		this.by = by;
		this.cx = cx;
		this.cy = cy;
		this.dx = dx;
		this.dy = dy;

		this.a = new Point2D( ax, ay );
		this.b = new Point2D( bx, by );
		this.c = new Point2D( cx, cy );
		this.d = new Point2D( dx, dy );
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

	public Cubic2D( Cubic2D curve ) {
		this( curve.ax, curve.ay, curve.bx, curve.by, curve.cx, curve.cy, curve.dx, curve.dy );
	}

	/**
	 * Compute bounding box of the curve.
	 *
	 * @return the bounding box of the curve
	 */
	public Bounds2D getBounds() {
		double left = Math.min( Math.min( ax, dx ), Math.min( bx, cx ) );
		double top = Math.min( Math.min( ay, dy ), Math.min( by, cy ) );
		double right = Math.max( Math.max( ax, dx ), Math.max( bx, cx ) );
		double bottom = Math.max( Math.max( ay, dy ), Math.max( by, cy ) );
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
	public Point2D eval( double t ) {
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
		double x = 3 * ((bx - ax) * u * u + 2 * (cx - bx) * u * t + (dx - cx) * t * t);
		double y = 3 * ((by - ay) * u * u + 2 * (cy - by) * u * t + (dy - cy) * t * t);
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
		return getFlatnessSq( ax, ay, bx, by, cx, cy, dx, dy );
	}

	/**
	 * Calculate the flatness of this curve.  The flatness is the maximum distance
	 * of a control point from the line connecting the end points.
	 *
	 * @return the flatness of this curve.
	 */
	public float getFlatness() {
		return getFlatness( ax, ay, bx, by, cx, cy, dx, dy );
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

		double x1 = this.ax;
		double y1 = this.ay;
		double c1x = this.bx;
		double c1y = this.by;
		double c2x = this.cx;
		double c2y = this.cy;
		double x2 = this.dx;
		double y2 = this.dy;
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
	 * Subdivide the given curve. This is an optimized implementation that
	 * subdivides the curve with a value of 0.5.
	 *
	 * @param src the curve to be subdivided
	 */
	public static Cubic2D[] subdivide( Cubic2D src ) {
		double x1 = src.ax;
		double y1 = src.ay;
		double ctrlx1 = src.bx;
		double ctrly1 = src.by;
		double ctrlx2 = src.cx;
		double ctrly2 = src.cy;
		double x2 = src.dx;
		double y2 = src.dy;
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
	 * Get the list of points that estimate the curve with a specific number of
	 * line segments.
	 *
	 * @param count The number of line segments to produce
	 * @return The points that define the curve as line segements
	 */
	public List<Point2D> toPoints( int count ) {
		return toPath( count ).getPoints();
	}

	public SegmentedPath2D toPath( int count ) {
		double fraction = 1.0 / count;
		count++;
		List<Point2D> points = new ArrayList<>( count );
		for( int index = 0; index < count; index++ ) {
			points.add( eval( index * fraction ) );
		}
		return SegmentedPath2D.of( points );
	}

	// TODO Future methods

	/**
	 * Get the list of points that estimate the curve as line segments no larger
	 * than size.
	 *
	 * @param size The largest permitted line segment length
	 * @return The points that estimate the curve as line segments
	 */
	public List<Point2D> toSizePoints( double size ) {
		return List.of( a, d );
	}

	/**
	 * Get the list of points that estimate the curve as line segments with
	 * flatness no larger than flatness.
	 *
	 * @param flatness The largest permitted line segment flatness
	 * @return The points that estimate the curve as line segments
	 */
	public List<Point2D> toFlatPoints( double flatness ) {
		return List.of( a, d );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals( Object object ) {
		if( object == this ) return true;
		if( !(object instanceof Cubic2D) ) return false;
		Cubic2D that = (Cubic2D)object;
		return this.ax == that.ax && this.ay == that.ay && this.bx == that.bx && this.by == that.by && this.cx == that.cx && this.cy == that.cy && this.dx == that.dx && this.dy == that.dy;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		if( hash == 0 ) {
			long bits = 11L;
			bits = 73L * bits + Double.doubleToLongBits( ax );
			bits = 73L * bits + Double.doubleToLongBits( ay );
			bits = 73L * bits + Double.doubleToLongBits( bx );
			bits = 73L * bits + Double.doubleToLongBits( by );
			bits = 73L * bits + Double.doubleToLongBits( cx );
			bits = 73L * bits + Double.doubleToLongBits( cy );
			bits = 73L * bits + Double.doubleToLongBits( dx );
			bits = 73L * bits + Double.doubleToLongBits( dy );
			hash = (int)(bits ^ (bits >> 32));
		}
		return hash;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "Cubic2D[ax=" + ax + ",ay=" + ay + ",bx=" + bx + ",by=" + by + ",cx=" + cx + ",cy=" + cy + ",dx=" + dx + ",dy=" + dy + "]";
	}

	private double calcX( final double t ) {
		final double u = 1 - t;
		return (u * u * u * ax + 3 * (t * u * u * bx + t * t * u * cx) + t * t * t * dx);
	}

	private double calcY( final double t ) {
		final double u = 1 - t;
		return (u * u * u * ay + 3 * (t * u * u * by + t * t * u * cy) + t * t * t * dy);
	}

}
