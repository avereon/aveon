package com.avereon.aveon;

import com.avereon.geometry.Cubic2D;
import com.avereon.geometry.Geometry2D;
import com.avereon.geometry.Point2D;
import com.avereon.util.Log;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CubicBezierCurveFitter {

	private static final System.Logger log = Log.get();

	public enum Hint {
		LEADING,
		MIDDLE,
		TRAILING
	}

	private String id;

	private List<Point2D> points;

	private Hint hint;

	public CubicBezierCurveFitter( String id, List<Point2D> points, Hint hint ) {
		this.id = id;
		this.points = points;
		this.hint = hint;
	}

	public Cubic2D generate() {
		Cubic2D curve = getInitial( points, hint );

		int iteration = 0;
		while( !closeEnough( points, curve, iteration++ ) ) {
			curve = adjustCurve( curve );
		}

		return curve;
	}

	private Cubic2D adjustCurve( Cubic2D curve ) {
		return new Cubic2D( curve );
	}

	/**
	 * Determine if the curve matches close enough to the curve
	 *
	 * @param points The points to match
	 * @param curve The curve to test
	 * @return True if the curve is "close enough"
	 */
	private boolean closeEnough( List<Point2D> points, Cubic2D curve, int iteration ) {
		double error = calcError( points, curve );
		log.log( Log.WARN, "id=" + id + " hint=" + hint + " iteration=" + iteration + " error=" + error );
		return error <= 0.1 || iteration >= 100;
	}

	private double calcError( List<Point2D> points, Cubic2D curve ) {
		Point2D pA = points.get( 0 );
		Point2D cA = curve.a;
		Point2D pD = points.get( points.size() - 1 );
		Point2D cD = curve.d;

		// The first and last points should match
		if( !Objects.equals( pA, cA ) || !Objects.equals( pD, cD ) ) return Double.NaN;

		// TODO There are several ways to generate the curve points
		// 1. Simple count
		// 2. Count based on number of points
		// 3. Flatness
		List<Point2D> curvePoints = curve.toPoints( 100 );

		return findAreas( points, curvePoints ).stream().mapToDouble( Double::doubleValue ).sum();
	}

	private List<Double> findAreas( List<Point2D> fitPoints, List<Point2D> curvePoints ) {
		List<List<Point2D>> polygons = Geometry2D.findPolygons( fitPoints, curvePoints );
		return polygons.stream().map( Geometry2D::calcPolygonArea ).collect( Collectors.toList() );
	}

	private Cubic2D getInitial( List<Point2D> points, Hint hint ) {
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
