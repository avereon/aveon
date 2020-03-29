package com.avereon.aveon;

import com.avereon.geometry.Cubic2D;
import com.avereon.geometry.Point2D;

import java.util.List;

public class CubicBezierCurveGenerator {

	public enum Hint {
		LEADING,
		TRAILING
	}

	public Cubic2D generate( List<Point2D> points, Hint hint ) {
		Point2D start = points.get( 0 );
		Point2D end = points.get( points.size() - 1 );

		// Determine initial control points based on the incoming hint
		Point2D a;
		Point2D b;
		Point2D c;
		Point2D d;

		switch( hint ) {
			case LEADING: {
				a = new Point2D( 0.0, 0.0 );
				b = new Point2D( 0.0, end.y );
				c = new Point2D( 0.0, end.y );
				d = new Point2D( end );
				break;
			}
			case TRAILING: {
				a = new Point2D( start );
				b = new Point2D( end.x, start.y );
				// FIXME This point should have the angle of the trailing edge
				c = new Point2D( start.x, end.y );
				d = new Point2D( end );
				break;
			}
			default: {
				a = new Point2D( start );
				b = new Point2D( end.x, start.y );
				c = new Point2D( start.x, end.y );
				d = new Point2D( end );
				break;
			}
		}

		// Determine initial control points based on the incoming hint
		return new Cubic2D( a, b, c, d );
	}

}
