package com.avereon.aveon;

import com.avereon.geometry.Cubic2D;
import com.avereon.geometry.Point2D;

import java.util.List;

public class CubicBezierCurveFitter {

	public enum Hint {
		LEADING,
		INTERMEDIATE,
		TRAILING
	}

	public Cubic2D generate( List<Point2D> points, Hint hint ) {
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
				c = new Point2D( p.x, r.y );
				//c = q.subtract( r ).normalize().multiply( Math.abs( r.x - p.x ) );
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
