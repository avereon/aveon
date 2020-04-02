package com.avereon.geometry;

import java.util.List;
import java.util.Objects;

/**
 * An immutable 2D line segment that contains the start and end point coordinates.
 */
public class Line2D extends Shape {

	// TODO Change the names of the variables to ax, ay, bx, by

	public final double x1;

	public final double y1;

	public final double x2;

	public final double y2;

	public final Point2D a;

	public final Point2D b;

	private int hash;

	/**
	 * Create a <code>Line2D</code> from the given coordinates.
	 *
	 * @param x1 the X coordinate of the start point
	 * @param y1 the Y coordinate of the start point
	 * @param x2 the X coordinate of the end point
	 * @param y2 the Y coordinate of the end point
	 */
	public Line2D( double x1, double y1, double x2, double y2 ) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;

		this.a = new Point2D( x1, y1 );
		this.b = new Point2D( x2, y2 );
	}

	/**
	 * Constructs and initializes a <code>Line2D</code> from the
	 * given start and end <code>Point2D</code>.
	 *
	 * @param p1 the start <code>Point2D</code> of this line segment
	 * @param p2 the end <code>Point2D</code> of this line segment
	 */
	public Line2D( Point2D p1, Point2D p2 ) {
		this( p1.getX(), p1.getY(), p2.getX(), p2.getY() );
	}

	/**
	 * Compute bounding box of the line.
	 *
	 * @return the bounding box of the line
	 */
	public Bounds2D getBounds() {
		return new Bounds2D( x1, y1, x2, y2 );
	}

	/**
	 * Returns the square of the distance from a point to a line segment.
	 * The distance measured is the distance between the specified
	 * point and the closest point between the end points.
	 * If the point intersects the line segment in between the
	 * end points, this method returns 0.0.
	 *
	 * @param x1 the X coordinate of the start point of the line segment
	 * @param y1 the Y coordinate of the start point of the line segment
	 * @param x2 the X coordinate of the end point of the line segment
	 * @param y2 the Y coordinate of the end point of the line segment
	 * @param px the X coordinate of the point being measured against the line segment
	 * @param py the Y coordinate of the point being measured against the line segment
	 * @return the square of the distance from the point to the line segment
	 * //@see #ptLineDistSq(double, double, double, double, double, double)
	 */
	public static double ptSegDistSq( double x1, double y1, double x2, double y2, double px, double py ) {
		// Adjust vectors relative to x1,y1
		// x2,y2 becomes relative vector
		// from x1,y1 to end of segment
		x2 -= x1;
		y2 -= y1;

		// px,py becomes relative vector from x1,y1 to test point
		px -= x1;
		py -= y1;
		double dotprod = px * x2 + py * y2;
		double projlenSq;
		if( dotprod <= 0f ) {
			// px,py is on the side of x1,y1 away from x2,y2
			// distance to segment is length of px,py vector
			// "length of its (clipped) projection" is now 0.0
			projlenSq = 0f;
		} else {
			// switch to backwards vectors relative to x2,y2
			// x2,y2 are already the negative of x1,y1=>x2,y2
			// to get px,py to be the negative of px,py=>x2,y2
			// the dot product of two negated vectors is the same
			// as the dot product of the two normal vectors
			px = x2 - px;
			py = y2 - py;
			dotprod = px * x2 + py * y2;
			if( dotprod <= 0f ) {
				// px,py is on the side of x2,y2 away from x1,y1
				// distance to segment is length of (backwards) px,py vector
				// "length of its (clipped) projection" is now 0.0
				projlenSq = 0f;
			} else {
				// px,py is between x1,y1 and x2,y2
				// dotprod is the length of the px,py vector
				// projected on the x2,y2=>x1,y1 vector times the
				// length of the x2,y2=>x1,y1 vector
				projlenSq = dotprod * dotprod / (x2 * x2 + y2 * y2);
			}
		}
		// Distance to line is now the length of the relative point
		// vector minus the length of its projection onto the line
		// (which is zero if the projection falls outside the range
		//  of the line segment).
		double lenSq = px * px + py * py - projlenSq;
		if( lenSq < 0f ) {
			lenSq = 0f;
		}
		return lenSq;
	}

	/**
	 * Returns the distance from a point to a line segment.
	 * The distance measured is the distance between the specified
	 * point and the closest point between the specified end points.
	 * If the specified point intersects the line segment in between the
	 * end points, this method returns 0.0.
	 *
	 * @param x1 the X coordinate of the start point of the
	 * specified line segment
	 * @param y1 the Y coordinate of the start point of the
	 * specified line segment
	 * @param x2 the X coordinate of the end point of the
	 * specified line segment
	 * @param y2 the Y coordinate of the end point of the
	 * specified line segment
	 * @param px the X coordinate of the specified point being
	 * measured against the specified line segment
	 * @param py the Y coordinate of the specified point being
	 * measured against the specified line segment
	 * @return a double value that is the distance from the specified point
	 * to the specified line segment.
	 * @see #ptLineDist(double, double, double, double, double, double)
	 */
	public static float ptSegDist(
		float x1, float y1, float x2, float y2, float px, float py
	) {
		return (float)Math.sqrt( ptSegDistSq( x1, y1, x2, y2, px, py ) );
	}

	public List<Point2D> intersections( Line2D line ) {
		return segmentIntersections( this, line );
	}

	public static List<Point2D> segmentIntersections( Line2D a, Line2D b ) {
		if( Objects.equals( a.a, b.a ) ) return List.of( new Point2D( a.a ) );
		if( Objects.equals( a.a, b.b ) ) return List.of( new Point2D( a.a ) );
		if( Objects.equals( a.b, b.a ) ) return List.of( new Point2D( a.b ) );
		if( Objects.equals( a.b, b.b ) ) return List.of( new Point2D( a.b ) );
		return Intersection2D.intersectLineLine( a.a, a.b, b.a, b.b ).getPoints();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals( Object object ) {
		if( object == this ) return true;
		if( !(object instanceof Line2D) ) return false;
		Line2D that = (Line2D)object;
		return this.x1 == that.x1 && this.y1 == that.y1 && this.x2 == that.x2 && this.y2 == that.y2;
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
		return "Line2D[x1=" + x1 + ",y1=" + y1 + ",x2=" + x2 + ",y2=" + y2 + "]";
	}

}
