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

	private double error;

	private boolean head = true;

	/**
	 * What direction to change the control point: shrink(-1) grow(1)
	 */
	private double headDirection = -1;

	/**
	 * What direction to change the control point: shrink(-1) grow(1)
	 */
	private double tailDirection = -1;

	public CubicBezierCurveFitter( String id, List<Point2D> points, Hint hint ) {
		this.id = id;
		this.points = points;
		this.hint = hint;
	}

	public Cubic2D generate() {
		Cubic2D curve = getInitial( points, hint );
		error = calcError( points, curve );

		System.err.println( "initalerror=" + error );

		int iteration = 0;
		while( !closeEnough( points, curve, iteration ) ) {
			curve = adjustCurve( curve, iteration );
			iteration++;
		}

		return curve;
	}

	private Cubic2D adjustCurve( Cubic2D curve, int iteration ) {
		// Need to adjust the magnitude of either control point
		if( head ) {
			// Adjust the head
			Point2D v = curve.b.subtract( curve.a );
			double m = v.magnitude();
			double mo = m;

			m = m + (m * 0.1 * headDirection);
			Point2D u = v.normalize().multiply( m );
			System.err.println( "  mag=" + mo + " newmag=" + m + " u=" + u + " newb=" + curve.a.add( u ) );

			return new Cubic2D( curve.a, curve.a.add( u ), curve.c, curve.d );
		} else {
			// Adjust the tail
			Point2D v = curve.c.subtract( curve.d );
			double m = v.magnitude();
			m = m + (m * 0.1 * tailDirection);
			Point2D u = v.normalize().multiply( m );
			return new Cubic2D( curve.a, curve.b, curve.d.add( u ), curve.d );
		}
	}

	/**
	 * Determine if the curve matches close enough to the curve
	 *
	 * @param points The points to match
	 * @param curve The curve to test
	 * @return True if the curve is "close enough"
	 */
	private boolean closeEnough( List<Point2D> points, Cubic2D curve, int iteration ) {
		System.err.println( "\niteration=" + iteration + " curve=" + curve );
		double error = calcError( points, curve );
		System.err.println( "  id=" + id + " hint=" + hint + " iteration=" + iteration + " error=" + error );
		if( Double.isNaN( error ) ) throw new RuntimeException( "Error returned NaN" );
		return error <= 0.1 || iteration >= 100;
	}

	private double calcError( List<Point2D> points, Cubic2D curve ) {
		boolean headPointsMatch = Objects.equals( points.get( 0 ), curve.a );
		boolean tailPointsMatch = Objects.equals( points.get( points.size() - 1 ), curve.d );

		// The first and last points should match
		if( !headPointsMatch ) {
			log.log( Log.WARN, "Head points don't match" );
			return Double.NaN;
		}
		else if( !tailPointsMatch ) {
			log.log( Log.WARN, "Tail points don't match" );
			return Double.NaN;
		}
		//if( !headPointsMatch || !tailPointsMatch ) return Double.NaN;

		// TODO There are several ways to generate the curve points
		// 1. Simple count
		// 2. Count based on number of points
		// 3. Flatness
		List<Point2D> curvePoints = curve.toPoints( 8 );
		System.err.println( "  statnpoints=" + points );
		System.err.println( "  curvepoints=" + curvePoints );

		return findAreas( points, curvePoints ).stream().mapToDouble( Double::doubleValue ).sum();
	}

	private List<Double> findAreas( List<Point2D> fitPoints, List<Point2D> curvePoints ) {
		List<List<Point2D>> polygons = Geometry2D.findPolygons( fitPoints, curvePoints );
		if( polygons.size() == 0 ) {
			//log.log( Log.WARN, "No polygons defined" );
			//log.log( Log.WARN, "  fitPoints=" + fitPoints );
			//log.log( Log.WARN, "  curvePoints=" + curvePoints );
			return List.of( Double.NaN );
		}
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
