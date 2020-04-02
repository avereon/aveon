package com.avereon.geometry;

import java.util.*;

public class Intersection2D {

	public enum Type {
		NONE,
		PARALLEL,
		COINCIDENT,
		SAME,
		INTERSECTION
	}

	private Type type;

	private List<Point2D> points;

	public Intersection2D( Type status, Point2D... points ) {
		this.type = status;
		this.points = Arrays.asList( points );
	}

	public Type getType() {
		return type;
	}

	public List<Point2D> getPoints() {
		return points;
	}

	@Override
	public boolean equals( Object object ) {
		if( !(object instanceof Intersection2D) ) return false;
		Intersection2D that = (Intersection2D)object;

		if( this.type != that.type ) return false;
		if( this.points.size() != that.points.size() ) return false;

		for( Point2D point : this.points ) {
			if( !that.points.contains( point ) ) return false;
		}

		return true;
	}

	/**
	 * Find the Intersection2D of two lines. An Intersection2D object is returned with
	 * the following values:
	 * <ul>
	 * <li>None, no points: the lines do not intersect
	 * <li>Coincident, no points: the lines overlap
	 * <li>Parallel, no points: the lines are parallel, but do not overlap
	 * <li>Intersection, 1 Intersection2D point: the lines intersect
	 * </ul>
	 *
	 * @param a1 First point of line segment a
	 * @param a2 Second point of line segment a
	 * @param b1 First point of line segment b
	 * @param b2 Second point of line segment b
	 * @return The intersection of the lines
	 */
	public static Intersection2D intersectLineLine( Point2D a1, Point2D a2, Point2D b1, Point2D b2 ) {
		Intersection2D result;

		double ua_t = (b2.x - b1.x) * (a1.y - b1.y) - (b2.y - b1.y) * (a1.x - b1.x);
		double ub_t = (a2.x - a1.x) * (a1.y - b1.y) - (a2.y - a1.y) * (a1.x - b1.x);
		double u_b = (b2.y - b1.y) * (a2.x - a1.x) - (b2.x - b1.x) * (a2.y - a1.y);

		if( u_b != 0 ) {
			double ua = ua_t / u_b;
			double ub = ub_t / u_b;

			if( 0 <= ua && ua <= 1 && 0 <= ub && ub <= 1 ) {
				result = new Intersection2D( Type.INTERSECTION, new Point2D( a1.x + ua * (a2.x - a1.x), a1.y + ua * (a2.y - a1.y) ) );
			} else {
				result = new Intersection2D( Type.NONE );
			}
		} else {
			if( ua_t == 0 || ub_t == 0 ) {
				result = new Intersection2D( Type.COINCIDENT );
			} else {
				result = new Intersection2D( Type.PARALLEL );
			}
		}

		return result;
	}

	/**
	 * Find the Intersection2D of two ellipses. An Intersection2D object is returned
	 * with the following values:
	 * <ul>
	 * <li>No Intersection, no points: the ellipses do not intersect
	 * <li>Coincident, no points: the ellipses overlap
	 * <li>Intersection, 1-4 points: the ellipses intersect
	 * </ul>
	 * This code is based on MgcIntr2DElpElp.cpp written by David Eberly. His code
	 * along with many other excellent examples are available at his site:
	 * http://www.magic-software.com
	 *
	 * @param oc1
	 * @param rx1
	 * @param ry1
	 * @param oc2
	 * @param rx2
	 * @param ry2
	 * @return
	 */
	public static Intersection2D intersectEllipseEllipse( Point2D oc1, double rx1, double ry1, Point2D oc2, double rx2, double ry2 ) {
		if( oc1.equals( oc2 ) && rx1 == rx2 && ry1 == ry2 ) return new Intersection2D( Type.SAME );

		Point2D c1 = new Point2D( 0, 0 );
		Point2D c2 = new Point2D( oc2.x - oc1.x, oc2.y - oc1.y );

		// Each array has six values.
		double[] a = new double[]{ ry1 * ry1, 0, rx1 * rx1, -2 * ry1 * ry1 * c1.x, -2 * rx1 * rx1 * c1.y, ry1 * ry1 * c1.x * c1.x + rx1 * rx1 * c1.y * c1.y - rx1 * rx1 * ry1 * ry1 };
		double[] b = new double[]{ ry2 * ry2, 0, rx2 * rx2, -2 * ry2 * ry2 * c2.x, -2 * rx2 * rx2 * c2.y, ry2 * ry2 * c2.x * c2.x + rx2 * rx2 * c2.y * c2.y - rx2 * rx2 * ry2 * ry2 };

		Polynomial yPoly = bezout( a, b );
		double[] yRoots = yPoly.getRoots();

		double norm0 = 1e-3 * (a[ 0 ] * a[ 0 ] + 2 * a[ 1 ] * a[ 1 ] + a[ 2 ] * a[ 2 ]);
		double norm1 = 1e-3 * (b[ 0 ] * b[ 0 ] + 2 * b[ 1 ] * b[ 1 ] + b[ 2 ] * b[ 2 ]);

		Set<Point2D> intersections = new HashSet<>();

		for( double yRoot : yRoots ) {
			Polynomial xPoly = new Polynomial( a[ 0 ], a[ 3 ] + yRoot * a[ 1 ], a[ 5 ] + yRoot * (a[ 4 ] + yRoot * a[ 2 ]) );
			double[] xRoots = xPoly.getRoots();
			for( double xRoot : xRoots ) {
				double test0 = (a[ 0 ] * xRoot + a[ 1 ] * yRoot + a[ 3 ]) * xRoot + (a[ 2 ] * yRoot + a[ 4 ]) * yRoot + a[ 5 ];
				if( Math.abs( test0 ) < norm0 ) {
					double test1 = (b[ 0 ] * xRoot + b[ 1 ] * yRoot + b[ 3 ]) * xRoot + (b[ 2 ] * yRoot + b[ 4 ]) * yRoot + b[ 5 ];
					if( Math.abs( test1 ) < norm1 ) intersections.add( new Point2D( xRoot + oc1.x, yRoot + oc1.y ) );
				}
			}
		}

		return intersections.size() == 0 ? new Intersection2D( Type.NONE ) : new Intersection2D( Type.INTERSECTION,
			intersections.toArray( new Point2D[ intersections.size() ] )
		);
	}

	public static Intersection2D intersectBezier3Bezier3( Point2D a1, Point2D a2, Point2D a3, Point2D a4, Point2D b1, Point2D b2, Point2D b3, Point2D b4 ) {
		if( a1.equals( b1 ) && a2.equals( b2 ) && a3.equals( b3 ) && a4.equals( b4 ) || a1.equals( b4 ) && a2.equals( b3 ) && a3.equals( b2 ) && a4.equals( b1 ) ) {
			return new Intersection2D( Type.SAME );
		}

		Point2D a, b, c, d; // temporary variables
		Point2D c13, c12, c11, c10; // coefficients of cubic
		Point2D c23, c22, c21, c20; // coefficients of cubic

		// Calculate the coefficients of cubic polynomial
		a = a1.multiply( -1 );
		b = a2.multiply( 3 );
		c = a3.multiply( -3 );
		d = a.add( b.add( c.add( a4 ) ) );
		c13 = new Point2D( d.x, d.y );

		a = a1.multiply( 3 );
		b = a2.multiply( -6 );
		c = a3.multiply( 3 );
		d = a.add( b.add( c ) );
		c12 = new Point2D( d.x, d.y );

		a = a1.multiply( -3 );
		b = a2.multiply( 3 );
		c = a.add( b );
		c11 = new Point2D( c.x, c.y );

		c10 = new Point2D( a1.x, a1.y );

		a = b1.multiply( -1 );
		b = b2.multiply( 3 );
		c = b3.multiply( -3 );
		d = a.add( b.add( c.add( b4 ) ) );
		c23 = new Point2D( d.x, d.y );

		a = b1.multiply( 3 );
		b = b2.multiply( -6 );
		c = b3.multiply( 3 );
		d = a.add( b.add( c ) );
		c22 = new Point2D( d.x, d.y );

		a = b1.multiply( -3 );
		b = b2.multiply( 3 );
		c = a.add( b );
		c21 = new Point2D( c.x, c.y );

		c20 = new Point2D( b1.x, b1.y );

		double c10x2 = c10.x * c10.x;
		double c10x3 = c10.x * c10.x * c10.x;
		double c10y2 = c10.y * c10.y;
		double c10y3 = c10.y * c10.y * c10.y;
		double c11x2 = c11.x * c11.x;
		double c11x3 = c11.x * c11.x * c11.x;
		double c11y2 = c11.y * c11.y;
		double c11y3 = c11.y * c11.y * c11.y;
		double c12x2 = c12.x * c12.x;
		double c12x3 = c12.x * c12.x * c12.x;
		double c12y2 = c12.y * c12.y;
		double c12y3 = c12.y * c12.y * c12.y;
		double c13x2 = c13.x * c13.x;
		double c13x3 = c13.x * c13.x * c13.x;
		double c13y2 = c13.y * c13.y;
		double c13y3 = c13.y * c13.y * c13.y;
		double c20x2 = c20.x * c20.x;
		double c20x3 = c20.x * c20.x * c20.x;
		double c20y2 = c20.y * c20.y;
		double c20y3 = c20.y * c20.y * c20.y;
		double c21x2 = c21.x * c21.x;
		double c21x3 = c21.x * c21.x * c21.x;
		double c21y2 = c21.y * c21.y;
		double c22x2 = c22.x * c22.x;
		double c22x3 = c22.x * c22.x * c22.x;
		double c22y2 = c22.y * c22.y;
		double c23x2 = c23.x * c23.x;
		double c23x3 = c23.x * c23.x * c23.x;
		double c23y2 = c23.y * c23.y;
		double c23y3 = c23.y * c23.y * c23.y;
		Polynomial poly = new Polynomial( -c13x3 * c23y3 + c13y3 * c23x3 - 3 * c13.x * c13y2 * c23x2 * c23.y + 3 * c13x2 * c13.y * c23.x * c23y2,
			-6 * c13.x * c22.x * c13y2 * c23.x * c23.y + 6 * c13x2 * c13.y * c22.y * c23.x * c23.y + 3 * c22.x * c13y3 * c23x2 - 3 * c13x3 * c22.y * c23y2 - 3 * c13.x * c13y2 * c22.y * c23x2 + 3 * c13x2 * c22.x * c13.y * c23y2,
			-6 * c21.x * c13.x * c13y2 * c23.x * c23.y - 6 * c13.x * c22.x * c13y2 * c22.y * c23.x + 6 * c13x2 * c22.x * c13.y * c22.y * c23.y + 3 * c21.x * c13y3 * c23x2 + 3 * c22x2 * c13y3 * c23.x + 3 * c21.x * c13x2 * c13.y * c23y2 - 3 * c13.x * c21.y * c13y2 * c23x2 - 3 * c13.x * c22x2 * c13y2 * c23.y + c13x2 * c13.y * c23.x * (6 * c21.y * c23.y + 3 * c22y2) + c13x3 * (-c21.y * c23y2 - 2 * c22y2 * c23.y - c23.y * (2 * c21.y * c23.y + c22y2)),
			c11.x * c12.y * c13.x * c13.y * c23.x * c23.y - c11.y * c12.x * c13.x * c13.y * c23.x * c23.y + 6 * c21.x * c22.x * c13y3 * c23.x + 3 * c11.x * c12.x * c13.x * c13.y * c23y2 + 6 * c10.x * c13.x * c13y2 * c23.x * c23.y - 3 * c11.x * c12.x * c13y2 * c23.x * c23.y - 3 * c11.y * c12.y * c13.x * c13.y * c23x2 - 6 * c10.y * c13x2 * c13.y * c23.x * c23.y - 6 * c20.x * c13.x * c13y2 * c23.x * c23.y + 3 * c11.y * c12.y * c13x2 * c23.x * c23.y - 2 * c12.x * c12y2 * c13.x * c23.x * c23.y - 6 * c21.x * c13.x * c22.x * c13y2 * c23.y - 6 * c21.x * c13.x * c13y2 * c22.y * c23.x - 6 * c13.x * c21.y * c22.x * c13y2 * c23.x + 6 * c21.x * c13x2 * c13.y * c22.y * c23.y + 2 * c12x2 * c12.y * c13.y * c23.x * c23.y + c22x3 * c13y3 - 3 * c10.x * c13y3 * c23x2 + 3 * c10.y * c13x3 * c23y2 + 3 * c20.x * c13y3 * c23x2 + c12y3 * c13.x * c23x2 - c12x3 * c13.y * c23y2 - 3 * c10.x * c13x2 * c13.y * c23y2 + 3 * c10.y * c13.x * c13y2 * c23x2 - 2 * c11.x * c12.y * c13x2 * c23y2 + c11.x * c12.y * c13y2 * c23x2 - c11.y * c12.x * c13x2 * c23y2 + 2 * c11.y * c12.x * c13y2 * c23x2 + 3 * c20.x * c13x2 * c13.y * c23y2 - c12.x * c12y2 * c13.y * c23x2 - 3 * c20.y * c13.x * c13y2 * c23x2 + c12x2 * c12.y * c13.x * c23y2 - 3 * c13.x * c22x2 * c13y2 * c22.y + c13x2 * c13.y * c23.x * (6 * c20.y * c23.y + 6 * c21.y * c22.y) + c13x2 * c22.x * c13.y * (6 * c21.y * c23.y + 3 * c22y2) + c13x3 * (-2 * c21.y * c22.y * c23.y - c20.y * c23y2 - c22.y * (2 * c21.y * c23.y + c22y2) - c23.y * (2 * c20.y * c23.y + 2 * c21.y * c22.y)),
			6 * c11.x * c12.x * c13.x * c13.y * c22.y * c23.y + c11.x * c12.y * c13.x * c22.x * c13.y * c23.y + c11.x * c12.y * c13.x * c13.y * c22.y * c23.x - c11.y * c12.x * c13.x * c22.x * c13.y * c23.y - c11.y * c12.x * c13.x * c13.y * c22.y * c23.x - 6 * c11.y * c12.y * c13.x * c22.x * c13.y * c23.x - 6 * c10.x * c22.x * c13y3 * c23.x + 6 * c20.x * c22.x * c13y3 * c23.x + 6 * c10.y * c13x3 * c22.y * c23.y + 2 * c12y3 * c13.x * c22.x * c23.x - 2 * c12x3 * c13.y * c22.y * c23.y + 6 * c10.x * c13.x * c22.x * c13y2 * c23.y + 6 * c10.x * c13.x * c13y2 * c22.y * c23.x + 6 * c10.y * c13.x * c22.x * c13y2 * c23.x - 3 * c11.x * c12.x * c22.x * c13y2 * c23.y - 3 * c11.x * c12.x * c13y2 * c22.y * c23.x + 2 * c11.x * c12.y * c22.x * c13y2 * c23.x + 4 * c11.y * c12.x * c22.x * c13y2 * c23.x - 6 * c10.x * c13x2 * c13.y * c22.y * c23.y - 6 * c10.y * c13x2 * c22.x * c13.y * c23.y - 6 * c10.y * c13x2 * c13.y * c22.y * c23.x - 4 * c11.x * c12.y * c13x2 * c22.y * c23.y - 6 * c20.x * c13.x * c22.x * c13y2 * c23.y - 6 * c20.x * c13.x * c13y2 * c22.y * c23.x - 2 * c11.y * c12.x * c13x2 * c22.y * c23.y + 3 * c11.y * c12.y * c13x2 * c22.x * c23.y + 3 * c11.y * c12.y * c13x2 * c22.y * c23.x - 2 * c12.x * c12y2 * c13.x * c22.x * c23.y - 2 * c12.x * c12y2 * c13.x * c22.y * c23.x - 2 * c12.x * c12y2 * c22.x * c13.y * c23.x - 6 * c20.y * c13.x * c22.x * c13y2 * c23.x - 6 * c21.x * c13.x * c21.y * c13y2 * c23.x - 6 * c21.x * c13.x * c22.x * c13y2 * c22.y + 6 * c20.x * c13x2 * c13.y * c22.y * c23.y + 2 * c12x2 * c12.y * c13.x * c22.y * c23.y + 2 * c12x2 * c12.y * c22.x * c13.y * c23.y + 2 * c12x2 * c12.y * c13.y * c22.y * c23.x + 3 * c21.x * c22x2 * c13y3 + 3 * c21x2 * c13y3 * c23.x - 3 * c13.x * c21.y * c22x2 * c13y2 - 3 * c21x2 * c13.x * c13y2 * c23.y + c13x2 * c22.x * c13.y * (6 * c20.y * c23.y + 6 * c21.y * c22.y) + c13x2 * c13.y * c23.x * (6 * c20.y * c22.y + 3 * c21y2) + c21.x * c13x2 * c13.y * (6 * c21.y * c23.y + 3 * c22y2) + c13x3 * (-2 * c20.y * c22.y * c23.y - c23.y * (2 * c20.y * c22.y + c21y2) - c21.y * (2 * c21.y * c23.y + c22y2) - c22.y * (2 * c20.y * c23.y + 2 * c21.y * c22.y)),
			c11.x * c21.x * c12.y * c13.x * c13.y * c23.y + c11.x * c12.y * c13.x * c21.y * c13.y * c23.x + c11.x * c12.y * c13.x * c22.x * c13.y * c22.y - c11.y * c12.x * c21.x * c13.x * c13.y * c23.y - c11.y * c12.x * c13.x * c21.y * c13.y * c23.x - c11.y * c12.x * c13.x * c22.x * c13.y * c22.y - 6 * c11.y * c21.x * c12.y * c13.x * c13.y * c23.x - 6 * c10.x * c21.x * c13y3 * c23.x + 6 * c20.x * c21.x * c13y3 * c23.x + 2 * c21.x * c12y3 * c13.x * c23.x + 6 * c10.x * c21.x * c13.x * c13y2 * c23.y + 6 * c10.x * c13.x * c21.y * c13y2 * c23.x + 6 * c10.x * c13.x * c22.x * c13y2 * c22.y + 6 * c10.y * c21.x * c13.x * c13y2 * c23.x - 3 * c11.x * c12.x * c21.x * c13y2 * c23.y - 3 * c11.x * c12.x * c21.y * c13y2 * c23.x - 3 * c11.x * c12.x * c22.x * c13y2 * c22.y + 2 * c11.x * c21.x * c12.y * c13y2 * c23.x + 4 * c11.y * c12.x * c21.x * c13y2 * c23.x - 6 * c10.y * c21.x * c13x2 * c13.y * c23.y - 6 * c10.y * c13x2 * c21.y * c13.y * c23.x - 6 * c10.y * c13x2 * c22.x * c13.y * c22.y - 6 * c20.x * c21.x * c13.x * c13y2 * c23.y - 6 * c20.x * c13.x * c21.y * c13y2 * c23.x - 6 * c20.x * c13.x * c22.x * c13y2 * c22.y + 3 * c11.y * c21.x * c12.y * c13x2 * c23.y - 3 * c11.y * c12.y * c13.x * c22x2 * c13.y + 3 * c11.y * c12.y * c13x2 * c21.y * c23.x + 3 * c11.y * c12.y * c13x2 * c22.x * c22.y - 2 * c12.x * c21.x * c12y2 * c13.x * c23.y - 2 * c12.x * c21.x * c12y2 * c13.y * c23.x - 2 * c12.x * c12y2 * c13.x * c21.y * c23.x - 2 * c12.x * c12y2 * c13.x * c22.x * c22.y - 6 * c20.y * c21.x * c13.x * c13y2 * c23.x - 6 * c21.x * c13.x * c21.y * c22.x * c13y2 + 6 * c20.y * c13x2 * c21.y * c13.y * c23.x + 2 * c12x2 * c21.x * c12.y * c13.y * c23.y + 2 * c12x2 * c12.y * c21.y * c13.y * c23.x + 2 * c12x2 * c12.y * c22.x * c13.y * c22.y - 3 * c10.x * c22x2 * c13y3 + 3 * c20.x * c22x2 * c13y3 + 3 * c21x2 * c22.x * c13y3 + c12y3 * c13.x * c22x2 + 3 * c10.y * c13.x * c22x2 * c13y2 + c11.x * c12.y * c22x2 * c13y2 + 2 * c11.y * c12.x * c22x2 * c13y2 - c12.x * c12y2 * c22x2 * c13.y - 3 * c20.y * c13.x * c22x2 * c13y2 - 3 * c21x2 * c13.x * c13y2 * c22.y + c12x2 * c12.y * c13.x * (2 * c21.y * c23.y + c22y2) + c11.x * c12.x * c13.x * c13.y * (6 * c21.y * c23.y + 3 * c22y2) + c21.x * c13x2 * c13.y * (6 * c20.y * c23.y + 6 * c21.y * c22.y) + c12x3 * c13.y * (-2 * c21.y * c23.y - c22y2) + c10.y * c13x3 * (6 * c21.y * c23.y + 3 * c22y2) + c11.y * c12.x * c13x2 * (-2 * c21.y * c23.y - c22y2) + c11.x * c12.y * c13x2 * (-4 * c21.y * c23.y - 2 * c22y2) + c10.x * c13x2 * c13.y * (-6 * c21.y * c23.y - 3 * c22y2) + c13x2 * c22.x * c13.y * (6 * c20.y * c22.y + 3 * c21y2) + c20.x * c13x2 * c13.y * (6 * c21.y * c23.y + 3 * c22y2) + c13x3 * (-2 * c20.y * c21.y * c23.y - c22.y * (2 * c20.y * c22.y + c21y2) - c20.y * (2 * c21.y * c23.y + c22y2) - c21.y * (2 * c20.y * c23.y + 2 * c21.y * c22.y)),
			-c10.x * c11.x * c12.y * c13.x * c13.y * c23.y + c10.x * c11.y * c12.x * c13.x * c13.y * c23.y + 6 * c10.x * c11.y * c12.y * c13.x * c13.y * c23.x - 6 * c10.y * c11.x * c12.x * c13.x * c13.y * c23.y - c10.y * c11.x * c12.y * c13.x * c13.y * c23.x + c10.y * c11.y * c12.x * c13.x * c13.y * c23.x + c11.x * c11.y * c12.x * c12.y * c13.x * c23.y - c11.x * c11.y * c12.x * c12.y * c13.y * c23.x + c11.x * c20.x * c12.y * c13.x * c13.y * c23.y + c11.x * c20.y * c12.y * c13.x * c13.y * c23.x + c11.x * c21.x * c12.y * c13.x * c13.y * c22.y + c11.x * c12.y * c13.x * c21.y * c22.x * c13.y - c20.x * c11.y * c12.x * c13.x * c13.y * c23.y - 6 * c20.x * c11.y * c12.y * c13.x * c13.y * c23.x - c11.y * c12.x * c20.y * c13.x * c13.y * c23.x - c11.y * c12.x * c21.x * c13.x * c13.y * c22.y - c11.y * c12.x * c13.x * c21.y * c22.x * c13.y - 6 * c11.y * c21.x * c12.y * c13.x * c22.x * c13.y - 6 * c10.x * c20.x * c13y3 * c23.x - 6 * c10.x * c21.x * c22.x * c13y3 - 2 * c10.x * c12y3 * c13.x * c23.x + 6 * c20.x * c21.x * c22.x * c13y3 + 2 * c20.x * c12y3 * c13.x * c23.x + 2 * c21.x * c12y3 * c13.x * c22.x + 2 * c10.y * c12x3 * c13.y * c23.y - 6 * c10.x * c10.y * c13.x * c13y2 * c23.x + 3 * c10.x * c11.x * c12.x * c13y2 * c23.y - 2 * c10.x * c11.x * c12.y * c13y2 * c23.x - 4 * c10.x * c11.y * c12.x * c13y2 * c23.x + 3 * c10.y * c11.x * c12.x * c13y2 * c23.x + 6 * c10.x * c10.y * c13x2 * c13.y * c23.y + 6 * c10.x * c20.x * c13.x * c13y2 * c23.y - 3 * c10.x * c11.y * c12.y * c13x2 * c23.y + 2 * c10.x * c12.x * c12y2 * c13.x * c23.y + 2 * c10.x * c12.x * c12y2 * c13.y * c23.x + 6 * c10.x * c20.y * c13.x * c13y2 * c23.x + 6 * c10.x * c21.x * c13.x * c13y2 * c22.y + 6 * c10.x * c13.x * c21.y * c22.x * c13y2 + 4 * c10.y * c11.x * c12.y * c13x2 * c23.y + 6 * c10.y * c20.x * c13.x * c13y2 * c23.x + 2 * c10.y * c11.y * c12.x * c13x2 * c23.y - 3 * c10.y * c11.y * c12.y * c13x2 * c23.x + 2 * c10.y * c12.x * c12y2 * c13.x * c23.x + 6 * c10.y * c21.x * c13.x * c22.x * c13y2 - 3 * c11.x * c20.x * c12.x * c13y2 * c23.y + 2 * c11.x * c20.x * c12.y * c13y2 * c23.x + c11.x * c11.y * c12y2 * c13.x * c23.x - 3 * c11.x * c12.x * c20.y * c13y2 * c23.x - 3 * c11.x * c12.x * c21.x * c13y2 * c22.y - 3 * c11.x * c12.x * c21.y * c22.x * c13y2 + 2 * c11.x * c21.x * c12.y * c22.x * c13y2 + 4 * c20.x * c11.y * c12.x * c13y2 * c23.x + 4 * c11.y * c12.x * c21.x * c22.x * c13y2 - 2 * c10.x * c12x2 * c12.y * c13.y * c23.y - 6 * c10.y * c20.x * c13x2 * c13.y * c23.y - 6 * c10.y * c20.y * c13x2 * c13.y * c23.x - 6 * c10.y * c21.x * c13x2 * c13.y * c22.y - 2 * c10.y * c12x2 * c12.y * c13.x * c23.y - 2 * c10.y * c12x2 * c12.y * c13.y * c23.x - 6 * c10.y * c13x2 * c21.y * c22.x * c13.y - c11.x * c11.y * c12x2 * c13.y * c23.y - 2 * c11.x * c11y2 * c13.x * c13.y * c23.x + 3 * c20.x * c11.y * c12.y * c13x2 * c23.y - 2 * c20.x * c12.x * c12y2 * c13.x * c23.y - 2 * c20.x * c12.x * c12y2 * c13.y * c23.x - 6 * c20.x * c20.y * c13.x * c13y2 * c23.x - 6 * c20.x * c21.x * c13.x * c13y2 * c22.y - 6 * c20.x * c13.x * c21.y * c22.x * c13y2 + 3 * c11.y * c20.y * c12.y * c13x2 * c23.x + 3 * c11.y * c21.x * c12.y * c13x2 * c22.y + 3 * c11.y * c12.y * c13x2 * c21.y * c22.x - 2 * c12.x * c20.y * c12y2 * c13.x * c23.x - 2 * c12.x * c21.x * c12y2 * c13.x * c22.y - 2 * c12.x * c21.x * c12y2 * c22.x * c13.y - 2 * c12.x * c12y2 * c13.x * c21.y * c22.x - 6 * c20.y * c21.x * c13.x * c22.x * c13y2 - c11y2 * c12.x * c12.y * c13.x * c23.x + 2 * c20.x * c12x2 * c12.y * c13.y * c23.y + 6 * c20.y * c13x2 * c21.y * c22.x * c13.y + 2 * c11x2 * c11.y * c13.x * c13.y * c23.y + c11x2 * c12.x * c12.y * c13.y * c23.y + 2 * c12x2 * c20.y * c12.y * c13.y * c23.x + 2 * c12x2 * c21.x * c12.y * c13.y * c22.y + 2 * c12x2 * c12.y * c21.y * c22.x * c13.y + c21x3 * c13y3 + 3 * c10x2 * c13y3 * c23.x - 3 * c10y2 * c13x3 * c23.y + 3 * c20x2 * c13y3 * c23.x + c11y3 * c13x2 * c23.x - c11x3 * c13y2 * c23.y - c11.x * c11y2 * c13x2 * c23.y + c11x2 * c11.y * c13y2 * c23.x - 3 * c10x2 * c13.x * c13y2 * c23.y + 3 * c10y2 * c13x2 * c13.y * c23.x - c11x2 * c12y2 * c13.x * c23.y + c11y2 * c12x2 * c13.y * c23.x - 3 * c21x2 * c13.x * c21.y * c13y2 - 3 * c20x2 * c13.x * c13y2 * c23.y + 3 * c20y2 * c13x2 * c13.y * c23.x + c11.x * c12.x * c13.x * c13.y * (6 * c20.y * c23.y + 6 * c21.y * c22.y) + c12x3 * c13.y * (-2 * c20.y * c23.y - 2 * c21.y * c22.y) + c10.y * c13x3 * (6 * c20.y * c23.y + 6 * c21.y * c22.y) + c11.y * c12.x * c13x2 * (-2 * c20.y * c23.y - 2 * c21.y * c22.y) + c12x2 * c12.y * c13.x * (2 * c20.y * c23.y + 2 * c21.y * c22.y) + c11.x * c12.y * c13x2 * (-4 * c20.y * c23.y - 4 * c21.y * c22.y) + c10.x * c13x2 * c13.y * (-6 * c20.y * c23.y - 6 * c21.y * c22.y) + c20.x * c13x2 * c13.y * (6 * c20.y * c23.y + 6 * c21.y * c22.y) + c21.x * c13x2 * c13.y * (6 * c20.y * c22.y + 3 * c21y2) + c13x3 * (-2 * c20.y * c21.y * c22.y - c20y2 * c23.y - c21.y * (2 * c20.y * c22.y + c21y2) - c20.y * (2 * c20.y * c23.y + 2 * c21.y * c22.y)),
			-c10.x * c11.x * c12.y * c13.x * c13.y * c22.y + c10.x * c11.y * c12.x * c13.x * c13.y * c22.y + 6 * c10.x * c11.y * c12.y * c13.x * c22.x * c13.y - 6 * c10.y * c11.x * c12.x * c13.x * c13.y * c22.y - c10.y * c11.x * c12.y * c13.x * c22.x * c13.y + c10.y * c11.y * c12.x * c13.x * c22.x * c13.y + c11.x * c11.y * c12.x * c12.y * c13.x * c22.y - c11.x * c11.y * c12.x * c12.y * c22.x * c13.y + c11.x * c20.x * c12.y * c13.x * c13.y * c22.y + c11.x * c20.y * c12.y * c13.x * c22.x * c13.y + c11.x * c21.x * c12.y * c13.x * c21.y * c13.y - c20.x * c11.y * c12.x * c13.x * c13.y * c22.y - 6 * c20.x * c11.y * c12.y * c13.x * c22.x * c13.y - c11.y * c12.x * c20.y * c13.x * c22.x * c13.y - c11.y * c12.x * c21.x * c13.x * c21.y * c13.y - 6 * c10.x * c20.x * c22.x * c13y3 - 2 * c10.x * c12y3 * c13.x * c22.x + 2 * c20.x * c12y3 * c13.x * c22.x + 2 * c10.y * c12x3 * c13.y * c22.y - 6 * c10.x * c10.y * c13.x * c22.x * c13y2 + 3 * c10.x * c11.x * c12.x * c13y2 * c22.y - 2 * c10.x * c11.x * c12.y * c22.x * c13y2 - 4 * c10.x * c11.y * c12.x * c22.x * c13y2 + 3 * c10.y * c11.x * c12.x * c22.x * c13y2 + 6 * c10.x * c10.y * c13x2 * c13.y * c22.y + 6 * c10.x * c20.x * c13.x * c13y2 * c22.y - 3 * c10.x * c11.y * c12.y * c13x2 * c22.y + 2 * c10.x * c12.x * c12y2 * c13.x * c22.y + 2 * c10.x * c12.x * c12y2 * c22.x * c13.y + 6 * c10.x * c20.y * c13.x * c22.x * c13y2 + 6 * c10.x * c21.x * c13.x * c21.y * c13y2 + 4 * c10.y * c11.x * c12.y * c13x2 * c22.y + 6 * c10.y * c20.x * c13.x * c22.x * c13y2 + 2 * c10.y * c11.y * c12.x * c13x2 * c22.y - 3 * c10.y * c11.y * c12.y * c13x2 * c22.x + 2 * c10.y * c12.x * c12y2 * c13.x * c22.x - 3 * c11.x * c20.x * c12.x * c13y2 * c22.y + 2 * c11.x * c20.x * c12.y * c22.x * c13y2 + c11.x * c11.y * c12y2 * c13.x * c22.x - 3 * c11.x * c12.x * c20.y * c22.x * c13y2 - 3 * c11.x * c12.x * c21.x * c21.y * c13y2 + 4 * c20.x * c11.y * c12.x * c22.x * c13y2 - 2 * c10.x * c12x2 * c12.y * c13.y * c22.y - 6 * c10.y * c20.x * c13x2 * c13.y * c22.y - 6 * c10.y * c20.y * c13x2 * c22.x * c13.y - 6 * c10.y * c21.x * c13x2 * c21.y * c13.y - 2 * c10.y * c12x2 * c12.y * c13.x * c22.y - 2 * c10.y * c12x2 * c12.y * c22.x * c13.y - c11.x * c11.y * c12x2 * c13.y * c22.y - 2 * c11.x * c11y2 * c13.x * c22.x * c13.y + 3 * c20.x * c11.y * c12.y * c13x2 * c22.y - 2 * c20.x * c12.x * c12y2 * c13.x * c22.y - 2 * c20.x * c12.x * c12y2 * c22.x * c13.y - 6 * c20.x * c20.y * c13.x * c22.x * c13y2 - 6 * c20.x * c21.x * c13.x * c21.y * c13y2 + 3 * c11.y * c20.y * c12.y * c13x2 * c22.x + 3 * c11.y * c21.x * c12.y * c13x2 * c21.y - 2 * c12.x * c20.y * c12y2 * c13.x * c22.x - 2 * c12.x * c21.x * c12y2 * c13.x * c21.y - c11y2 * c12.x * c12.y * c13.x * c22.x + 2 * c20.x * c12x2 * c12.y * c13.y * c22.y - 3 * c11.y * c21x2 * c12.y * c13.x * c13.y + 6 * c20.y * c21.x * c13x2 * c21.y * c13.y + 2 * c11x2 * c11.y * c13.x * c13.y * c22.y + c11x2 * c12.x * c12.y * c13.y * c22.y + 2 * c12x2 * c20.y * c12.y * c22.x * c13.y + 2 * c12x2 * c21.x * c12.y * c21.y * c13.y - 3 * c10.x * c21x2 * c13y3 + 3 * c20.x * c21x2 * c13y3 + 3 * c10x2 * c22.x * c13y3 - 3 * c10y2 * c13x3 * c22.y + 3 * c20x2 * c22.x * c13y3 + c21x2 * c12y3 * c13.x + c11y3 * c13x2 * c22.x - c11x3 * c13y2 * c22.y + 3 * c10.y * c21x2 * c13.x * c13y2 - c11.x * c11y2 * c13x2 * c22.y + c11.x * c21x2 * c12.y * c13y2 + 2 * c11.y * c12.x * c21x2 * c13y2 + c11x2 * c11.y * c22.x * c13y2 - c12.x * c21x2 * c12y2 * c13.y - 3 * c20.y * c21x2 * c13.x * c13y2 - 3 * c10x2 * c13.x * c13y2 * c22.y + 3 * c10y2 * c13x2 * c22.x * c13.y - c11x2 * c12y2 * c13.x * c22.y + c11y2 * c12x2 * c22.x * c13.y - 3 * c20x2 * c13.x * c13y2 * c22.y + 3 * c20y2 * c13x2 * c22.x * c13.y + c12x2 * c12.y * c13.x * (2 * c20.y * c22.y + c21y2) + c11.x * c12.x * c13.x * c13.y * (6 * c20.y * c22.y + 3 * c21y2) + c12x3 * c13.y * (-2 * c20.y * c22.y - c21y2) + c10.y * c13x3 * (6 * c20.y * c22.y + 3 * c21y2) + c11.y * c12.x * c13x2 * (-2 * c20.y * c22.y - c21y2) + c11.x * c12.y * c13x2 * (-4 * c20.y * c22.y - 2 * c21y2) + c10.x * c13x2 * c13.y * (-6 * c20.y * c22.y - 3 * c21y2) + c20.x * c13x2 * c13.y * (6 * c20.y * c22.y + 3 * c21y2) + c13x3 * (-2 * c20.y * c21y2 - c20y2 * c22.y - c20.y * (2 * c20.y * c22.y + c21y2)),
			-c10.x * c11.x * c12.y * c13.x * c21.y * c13.y + c10.x * c11.y * c12.x * c13.x * c21.y * c13.y + 6 * c10.x * c11.y * c21.x * c12.y * c13.x * c13.y - 6 * c10.y * c11.x * c12.x * c13.x * c21.y * c13.y - c10.y * c11.x * c21.x * c12.y * c13.x * c13.y + c10.y * c11.y * c12.x * c21.x * c13.x * c13.y - c11.x * c11.y * c12.x * c21.x * c12.y * c13.y + c11.x * c11.y * c12.x * c12.y * c13.x * c21.y + c11.x * c20.x * c12.y * c13.x * c21.y * c13.y + 6 * c11.x * c12.x * c20.y * c13.x * c21.y * c13.y + c11.x * c20.y * c21.x * c12.y * c13.x * c13.y - c20.x * c11.y * c12.x * c13.x * c21.y * c13.y - 6 * c20.x * c11.y * c21.x * c12.y * c13.x * c13.y - c11.y * c12.x * c20.y * c21.x * c13.x * c13.y - 6 * c10.x * c20.x * c21.x * c13y3 - 2 * c10.x * c21.x * c12y3 * c13.x + 6 * c10.y * c20.y * c13x3 * c21.y + 2 * c20.x * c21.x * c12y3 * c13.x + 2 * c10.y * c12x3 * c21.y * c13.y - 2 * c12x3 * c20.y * c21.y * c13.y - 6 * c10.x * c10.y * c21.x * c13.x * c13y2 + 3 * c10.x * c11.x * c12.x * c21.y * c13y2 - 2 * c10.x * c11.x * c21.x * c12.y * c13y2 - 4 * c10.x * c11.y * c12.x * c21.x * c13y2 + 3 * c10.y * c11.x * c12.x * c21.x * c13y2 + 6 * c10.x * c10.y * c13x2 * c21.y * c13.y + 6 * c10.x * c20.x * c13.x * c21.y * c13y2 - 3 * c10.x * c11.y * c12.y * c13x2 * c21.y + 2 * c10.x * c12.x * c21.x * c12y2 * c13.y + 2 * c10.x * c12.x * c12y2 * c13.x * c21.y + 6 * c10.x * c20.y * c21.x * c13.x * c13y2 + 4 * c10.y * c11.x * c12.y * c13x2 * c21.y + 6 * c10.y * c20.x * c21.x * c13.x * c13y2 + 2 * c10.y * c11.y * c12.x * c13x2 * c21.y - 3 * c10.y * c11.y * c21.x * c12.y * c13x2 + 2 * c10.y * c12.x * c21.x * c12y2 * c13.x - 3 * c11.x * c20.x * c12.x * c21.y * c13y2 + 2 * c11.x * c20.x * c21.x * c12.y * c13y2 + c11.x * c11.y * c21.x * c12y2 * c13.x - 3 * c11.x * c12.x * c20.y * c21.x * c13y2 + 4 * c20.x * c11.y * c12.x * c21.x * c13y2 - 6 * c10.x * c20.y * c13x2 * c21.y * c13.y - 2 * c10.x * c12x2 * c12.y * c21.y * c13.y - 6 * c10.y * c20.x * c13x2 * c21.y * c13.y - 6 * c10.y * c20.y * c21.x * c13x2 * c13.y - 2 * c10.y * c12x2 * c21.x * c12.y * c13.y - 2 * c10.y * c12x2 * c12.y * c13.x * c21.y - c11.x * c11.y * c12x2 * c21.y * c13.y - 4 * c11.x * c20.y * c12.y * c13x2 * c21.y - 2 * c11.x * c11y2 * c21.x * c13.x * c13.y + 3 * c20.x * c11.y * c12.y * c13x2 * c21.y - 2 * c20.x * c12.x * c21.x * c12y2 * c13.y - 2 * c20.x * c12.x * c12y2 * c13.x * c21.y - 6 * c20.x * c20.y * c21.x * c13.x * c13y2 - 2 * c11.y * c12.x * c20.y * c13x2 * c21.y + 3 * c11.y * c20.y * c21.x * c12.y * c13x2 - 2 * c12.x * c20.y * c21.x * c12y2 * c13.x - c11y2 * c12.x * c21.x * c12.y * c13.x + 6 * c20.x * c20.y * c13x2 * c21.y * c13.y + 2 * c20.x * c12x2 * c12.y * c21.y * c13.y + 2 * c11x2 * c11.y * c13.x * c21.y * c13.y + c11x2 * c12.x * c12.y * c21.y * c13.y + 2 * c12x2 * c20.y * c21.x * c12.y * c13.y + 2 * c12x2 * c20.y * c12.y * c13.x * c21.y + 3 * c10x2 * c21.x * c13y3 - 3 * c10y2 * c13x3 * c21.y + 3 * c20x2 * c21.x * c13y3 + c11y3 * c21.x * c13x2 - c11x3 * c21.y * c13y2 - 3 * c20y2 * c13x3 * c21.y - c11.x * c11y2 * c13x2 * c21.y + c11x2 * c11.y * c21.x * c13y2 - 3 * c10x2 * c13.x * c21.y * c13y2 + 3 * c10y2 * c21.x * c13x2 * c13.y - c11x2 * c12y2 * c13.x * c21.y + c11y2 * c12x2 * c21.x * c13.y - 3 * c20x2 * c13.x * c21.y * c13y2 + 3 * c20y2 * c21.x * c13x2 * c13.y,
			c10.x * c10.y * c11.x * c12.y * c13.x * c13.y - c10.x * c10.y * c11.y * c12.x * c13.x * c13.y + c10.x * c11.x * c11.y * c12.x * c12.y * c13.y - c10.y * c11.x * c11.y * c12.x * c12.y * c13.x - c10.x * c11.x * c20.y * c12.y * c13.x * c13.y + 6 * c10.x * c20.x * c11.y * c12.y * c13.x * c13.y + c10.x * c11.y * c12.x * c20.y * c13.x * c13.y - c10.y * c11.x * c20.x * c12.y * c13.x * c13.y - 6 * c10.y * c11.x * c12.x * c20.y * c13.x * c13.y + c10.y * c20.x * c11.y * c12.x * c13.x * c13.y - c11.x * c20.x * c11.y * c12.x * c12.y * c13.y + c11.x * c11.y * c12.x * c20.y * c12.y * c13.x + c11.x * c20.x * c20.y * c12.y * c13.x * c13.y - c20.x * c11.y * c12.x * c20.y * c13.x * c13.y - 2 * c10.x * c20.x * c12y3 * c13.x + 2 * c10.y * c12x3 * c20.y * c13.y - 3 * c10.x * c10.y * c11.x * c12.x * c13y2 - 6 * c10.x * c10.y * c20.x * c13.x * c13y2 + 3 * c10.x * c10.y * c11.y * c12.y * c13x2 - 2 * c10.x * c10.y * c12.x * c12y2 * c13.x - 2 * c10.x * c11.x * c20.x * c12.y * c13y2 - c10.x * c11.x * c11.y * c12y2 * c13.x + 3 * c10.x * c11.x * c12.x * c20.y * c13y2 - 4 * c10.x * c20.x * c11.y * c12.x * c13y2 + 3 * c10.y * c11.x * c20.x * c12.x * c13y2 + 6 * c10.x * c10.y * c20.y * c13x2 * c13.y + 2 * c10.x * c10.y * c12x2 * c12.y * c13.y + 2 * c10.x * c11.x * c11y2 * c13.x * c13.y + 2 * c10.x * c20.x * c12.x * c12y2 * c13.y + 6 * c10.x * c20.x * c20.y * c13.x * c13y2 - 3 * c10.x * c11.y * c20.y * c12.y * c13x2 + 2 * c10.x * c12.x * c20.y * c12y2 * c13.x + c10.x * c11y2 * c12.x * c12.y * c13.x + c10.y * c11.x * c11.y * c12x2 * c13.y + 4 * c10.y * c11.x * c20.y * c12.y * c13x2 - 3 * c10.y * c20.x * c11.y * c12.y * c13x2 + 2 * c10.y * c20.x * c12.x * c12y2 * c13.x + 2 * c10.y * c11.y * c12.x * c20.y * c13x2 + c11.x * c20.x * c11.y * c12y2 * c13.x - 3 * c11.x * c20.x * c12.x * c20.y * c13y2 - 2 * c10.x * c12x2 * c20.y * c12.y * c13.y - 6 * c10.y * c20.x * c20.y * c13x2 * c13.y - 2 * c10.y * c20.x * c12x2 * c12.y * c13.y - 2 * c10.y * c11x2 * c11.y * c13.x * c13.y - c10.y * c11x2 * c12.x * c12.y * c13.y - 2 * c10.y * c12x2 * c20.y * c12.y * c13.x - 2 * c11.x * c20.x * c11y2 * c13.x * c13.y - c11.x * c11.y * c12x2 * c20.y * c13.y + 3 * c20.x * c11.y * c20.y * c12.y * c13x2 - 2 * c20.x * c12.x * c20.y * c12y2 * c13.x - c20.x * c11y2 * c12.x * c12.y * c13.x + 3 * c10y2 * c11.x * c12.x * c13.x * c13.y + 3 * c11.x * c12.x * c20y2 * c13.x * c13.y + 2 * c20.x * c12x2 * c20.y * c12.y * c13.y - 3 * c10x2 * c11.y * c12.y * c13.x * c13.y + 2 * c11x2 * c11.y * c20.y * c13.x * c13.y + c11x2 * c12.x * c20.y * c12.y * c13.y - 3 * c20x2 * c11.y * c12.y * c13.x * c13.y - c10x3 * c13y3 + c10y3 * c13x3 + c20x3 * c13y3 - c20y3 * c13x3 - 3 * c10.x * c20x2 * c13y3 - c10.x * c11y3 * c13x2 + 3 * c10x2 * c20.x * c13y3 + c10.y * c11x3 * c13y2 + 3 * c10.y * c20y2 * c13x3 + c20.x * c11y3 * c13x2 + c10x2 * c12y3 * c13.x - 3 * c10y2 * c20.y * c13x3 - c10y2 * c12x3 * c13.y + c20x2 * c12y3 * c13.x - c11x3 * c20.y * c13y2 - c12x3 * c20y2 * c13.y - c10.x * c11x2 * c11.y * c13y2 + c10.y * c11.x * c11y2 * c13x2 - 3 * c10.x * c10y2 * c13x2 * c13.y - c10.x * c11y2 * c12x2 * c13.y + c10.y * c11x2 * c12y2 * c13.x - c11.x * c11y2 * c20.y * c13x2 + 3 * c10x2 * c10.y * c13.x * c13y2 + c10x2 * c11.x * c12.y * c13y2 + 2 * c10x2 * c11.y * c12.x * c13y2 - 2 * c10y2 * c11.x * c12.y * c13x2 - c10y2 * c11.y * c12.x * c13x2 + c11x2 * c20.x * c11.y * c13y2 - 3 * c10.x * c20y2 * c13x2 * c13.y + 3 * c10.y * c20x2 * c13.x * c13y2 + c11.x * c20x2 * c12.y * c13y2 - 2 * c11.x * c20y2 * c12.y * c13x2 + c20.x * c11y2 * c12x2 * c13.y - c11.y * c12.x * c20y2 * c13x2 - c10x2 * c12.x * c12y2 * c13.y - 3 * c10x2 * c20.y * c13.x * c13y2 + 3 * c10y2 * c20.x * c13x2 * c13.y + c10y2 * c12x2 * c12.y * c13.x - c11x2 * c20.y * c12y2 * c13.x + 2 * c20x2 * c11.y * c12.x * c13y2 + 3 * c20.x * c20y2 * c13x2 * c13.y - c20x2 * c12.x * c12y2 * c13.y - 3 * c20x2 * c20.y * c13.x * c13y2 + c12x2 * c20y2 * c12.y * c13.x
		);
		double[] roots = poly.getRootsInInterval( 0, 1 );

		List<Point2D> intersections = new ArrayList<>();

		for( double s : roots ) {
			double[] xRoots = new Polynomial( c13.x, c12.x, c11.x, c10.x - c20.x - s * c21.x - s * s * c22.x - s * s * s * c23.x ).getRoots();
			double[] yRoots = new Polynomial( c13.y, c12.y, c11.y, c10.y - c20.y - s * c21.y - s * s * c22.y - s * s * s * c23.y ).getRoots();

			if( xRoots.length > 0 && yRoots.length > 0 ) {
				double TOLERANCE = 1e-4;

				checkRoots:
				for( double xRoot : xRoots ) {
					if( 0 <= xRoot && xRoot <= 1 ) {
						for( double yRoot : yRoots ) {
							if( Math.abs( xRoot - yRoot ) < TOLERANCE ) {
								intersections.add( new Point2D( c23.multiply( s * s * s ).add( c22.multiply( s * s ).add( c21.multiply( s ).add( c20 ) ) ) ) );
								break checkRoots;
							}
						}
					}
				}
			}
		}

		return intersections.size() == 0 ? new Intersection2D( Type.NONE ) : new Intersection2D( Type.INTERSECTION, intersections.toArray( new Point2D[ 0 ] ) );
	}

	private static Polynomial bezout( double[] e1, double[] e2 ) {
		double AB = e1[ 0 ] * e2[ 1 ] - e2[ 0 ] * e1[ 1 ];
		double AC = e1[ 0 ] * e2[ 2 ] - e2[ 0 ] * e1[ 2 ];
		double AD = e1[ 0 ] * e2[ 3 ] - e2[ 0 ] * e1[ 3 ];
		double AE = e1[ 0 ] * e2[ 4 ] - e2[ 0 ] * e1[ 4 ];
		double AF = e1[ 0 ] * e2[ 5 ] - e2[ 0 ] * e1[ 5 ];

		double BC = e1[ 1 ] * e2[ 2 ] - e2[ 1 ] * e1[ 2 ];
		double BE = e1[ 1 ] * e2[ 4 ] - e2[ 1 ] * e1[ 4 ];
		double BF = e1[ 1 ] * e2[ 5 ] - e2[ 1 ] * e1[ 5 ];

		double CD = e1[ 2 ] * e2[ 3 ] - e2[ 2 ] * e1[ 3 ];

		double DE = e1[ 3 ] * e2[ 4 ] - e2[ 3 ] * e1[ 4 ];
		double DF = e1[ 3 ] * e2[ 5 ] - e2[ 3 ] * e1[ 5 ];

		double BFpDE = BF + DE;
		double BEmCD = BE - CD;

		return new Polynomial( AB * BC - AC * AC,
			AB * BEmCD + AD * BC - 2 * AC * AE,
			AB * BFpDE + AD * BEmCD - AE * AE - 2 * AC * AF,
			AB * DF + AD * BFpDE - 2 * AE * AF,
			AD * DF - AF * AF
		);
	}

}
