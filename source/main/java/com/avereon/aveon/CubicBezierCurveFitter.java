package com.avereon.aveon;

import com.avereon.geometry.Cubic2D;
import com.avereon.geometry.Point2D;
import com.avereon.geometry.SegmentedPath2D;

public interface CubicBezierCurveFitter {

	enum Hint {
		LEADING,
		MIDDLE,
		TRAILING;
	}

	Cubic2D generate();

	default Cubic2D getInitial( SegmentedPath2D path, double t, Hint hint ) {
		Point2D p = path.points.get( 0 );
		Point2D q = path.points.get( path.pointCount - 2 );
		Point2D r = path.points.get( path.pointCount - 1 );
		double by = p.y + t * (r.y - p.y);
		double bx = p.x + t * (r.x - r.y);
		double cx = r.x + t * (p.x - r.x);

		// Determine initial control points based on the incoming hint
		Point2D a;
		Point2D b;
		Point2D c;
		Point2D d;

		switch( hint ) {
			case LEADING -> {
				a = new Point2D( p );
				b = new Point2D( p.x, by );
				c = new Point2D( cx, r.y );
				d = new Point2D( r );
			}
			case TRAILING -> {
				a = new Point2D( p );
				b = new Point2D( bx, p.y );
				c = r.add( q.subtract( r ).normalize().multiply( Math.abs( r.x - p.x ) ).multiply( t ) );
				d = new Point2D( r );
			}
			default -> {
				a = new Point2D( p );
				b = new Point2D( bx, p.y );
				c = new Point2D( cx, r.y );
				d = new Point2D( r );
			}
		}

		// Determine initial control points based on the incoming hint
		return new Cubic2D( a, b, c, d );
	}

}

