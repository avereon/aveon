package com.avereon.aveon;

import com.avereon.geometry.Bounds2D;
import com.avereon.geometry.Cubic2D;
import com.avereon.geometry.Point2D;

import java.util.Collection;
import java.util.List;

public class CubicBezierCurveFitter {

	public enum Hint {
		LEADING,
		INTERMEDIATE,
		TRAILING
	}

	public Cubic2D generate( List<Point2D> points, Hint hint ) {
		Cubic2D curve = getInitial( points, hint );

		boolean closeEnough = closeEnough( points, curve );
		while( !closeEnough ) {
			// try again
			curve = adjustCurve( curve );

			closeEnough = closeEnough( points, curve );
		}

		return curve;
	}

	private Cubic2D adjustCurve( Cubic2D curve ) {
		return curve;
	}

	/**
	 * Determine if the curve matches close enough to the curve
	 *
	 * @param points The points to match
	 * @param curve The curve to test
	 * @return True if the curve is "close enough"
	 */
	private boolean closeEnough( List<Point2D> points, Cubic2D curve ) {
		//

		return true;
	}

	private double calcError( List<Point2D> points, Cubic2D curve ) {
		// The first and last points should match
		if( !points.get( 0 ).equals( curve.a ) || points.get( points.size() - 1 ).equals( curve.d ) ) return Double.NaN;

		double pointArea = getArea( points );
		double curveArea = getArea( curve.toPoints( 100 ) );

		return Math.abs( pointArea - curveArea );
	}

	private double getArea( Collection<Point2D> points ) {
		Bounds2D bounds = getBounds( points );

		//

		return 0.0;
	}

	private Bounds2D getBounds( Collection<Point2D> points ) {
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

	private double getMinY( Collection<Point2D> points ) {
		return points.stream().mapToDouble( Point2D::getY ).min().getAsDouble();
	}

	public Cubic2D getInitial( List<Point2D> points, Hint hint ) {
		Point2D p = points.get( 0 );
		Point2D q = points.get( points.size() - 2 );
		Point2D r = points.get( points.size() - 1 );

		// Determine initial control points based on the incoming hint
		Point2D a;
		Point2D b;
		Point2D c;
		Point2D d;

		switch( hint ) {
			case LEADING: {
				a = new Point2D( p.x, p.y );
				b = new Point2D( p.x, r.y );
				c = new Point2D( p.x, r.y );
				d = new Point2D( r );
				break;
			}
			case TRAILING: {
				a = new Point2D( p );
				b = new Point2D( r.x, p.y );
				c = r.add( q.subtract( r ).normalize().multiply( Math.abs( r.x - p.x ) ) );
				d = new Point2D( r );
				break;
			}
			default: {
				a = new Point2D( p );
				b = new Point2D( r.x, p.y );
				c = new Point2D( p.x, r.y );
				d = new Point2D( r );
				break;
			}
		}

		// Determine initial control points based on the incoming hint
		return new Cubic2D( a, b, c, d );
	}

}
