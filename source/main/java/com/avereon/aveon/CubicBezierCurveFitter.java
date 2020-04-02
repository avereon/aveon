package com.avereon.aveon;

import com.avereon.geometry.Cubic2D;
import com.avereon.geometry.Geometry2D;
import com.avereon.geometry.Point2D;
import com.avereon.util.Log;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Objects;

public class CubicBezierCurveFitter {

	private static final System.Logger log = Log.get();

	private static NumberFormat format = new DecimalFormat( "0.0000000000000000" );

	public enum Hint {
		LEADING,
		MIDDLE,
		TRAILING
	}

	private static final double factor = 0.5;

	private final String id;

	private final List<Point2D> points;

	private final Hint hint;

	private final Cubic2D initialCurve;

	private double error;

	private double dError;

	/**
	 * What direction to change the control point: shrink(negative) grow(positive)
	 */
	private double headDirection = -factor;

	/**
	 * What direction to change the control point: shrink(negative) grow(positive)
	 */
	private double tailDirection = -factor;

	public CubicBezierCurveFitter( String id, List<Point2D> points, Hint hint ) {
		this.id = id;
		this.points = points;
		this.hint = hint;
		this.initialCurve = getInitial( hint );
	}

	public Cubic2D generate() {
		//System.err.println( "initalstate" );

		Cubic2D curve = initialCurve;
		error = calcError( points, curve );
		System.err.println( "  init curve=" + curve );
		System.err.println( "  init error=" + format.format( error ) + " dError=" + format.format( dError ) );

		int iteration = 0;
		while( !closeEnough( curve, iteration ) ) {
			//System.err.println( "iteration=" + iteration );
			curve = adjustCurve( curve );
			double oldError = error;
			error = calcError( points, curve );
			dError = error - oldError;
			//System.err.println( "  error=" + format.format( error ) + " dError=" + format.format( dError ) );

			System.err.println( id + " " + hint + " iteration=" + iteration + " headDir=" + headDirection + " tailDir=" + tailDirection + " error=" + error + " dError=" + dError );
			iteration++;
		}

		return curve;
	}

	/**
	 * Determine if the curve matches close enough to the curve
	 *
	 * @param curve The curve to test
	 * @return True if the curve is "close enough"
	 */
	private boolean closeEnough( Cubic2D curve, int iteration ) {
		double headDir = Math.abs( headDirection );
		double tailDir = Math.abs( tailDirection );
		double dErr = Math.abs( dError );

		return iteration != 0 && ((headDir < 0.0001 && tailDir < 0.0001) || error < 0.00001 || iteration >= 5);
	}

	private Cubic2D adjustCurve( Cubic2D curve ) {
		Cubic2D test = curve;
		Cubic2D result = curve;
		double priorError = error;
		double newError;
		double dError;
		int steps = 1;

		// Adjust the head
		for( int index = 0; index < steps; index++ ) {
			test = tweakHead( test );
			System.err.println( "  head curve=" + test );
			newError = calcError( points, test );
			dError = newError - priorError;
			if( dError < 0 ) {
				// Less error
				priorError = newError;
				result = test;
				System.err.println( "  head error=" + format.format( calcError( points, test ) ) + " dError=" + format.format( dError ) );
			} else {
				// More error
				headDirection *= -factor;
				System.err.println( "  head error increased, switching direction: " + headDirection );
				break;
			}
		}

		// Adjust the tail
		for( int index = 0; index < steps; index++ ) {
			test = tweakTail( test );
			System.err.println( "  tail curve=" + test );
			newError = calcError( points, test );
			dError = newError - priorError;
			if( dError < 0 ) {
				priorError = newError;
				result = test;
				System.err.println( "  tail error=" + format.format( calcError( points, test ) ) + " dError=" + format.format( dError ) );
			} else {
				tailDirection *= -factor;
				System.err.println( "  tail error increased, switching direction: " + tailDirection );
				break;
			}
		}

		return result;
	}

	private Cubic2D tweakHead( Cubic2D curve ) {
		// The current vector to the control point
		Point2D u = curve.b.subtract( curve.a );
		// Using the initial curve avoids additive rounding errors
		Point2D d = initialCurve.b.subtract( initialCurve.a ).normalize();
		Point2D v = d.multiply( u.magnitude() * (1 + headDirection) );
		return new Cubic2D( curve.a, curve.a.add( v ), curve.c, curve.d );
	}

	private Cubic2D tweakTail( Cubic2D curve ) {
		// The current vector to the control point
		Point2D u = curve.c.subtract( curve.d );
		// Using the initial curve avoids additive rounding errors
		Point2D d = initialCurve.c.subtract( initialCurve.d ).normalize();
		Point2D v = d.multiply( u.magnitude() * ( 1 + tailDirection ) );
		return new Cubic2D( curve.a, curve.b, curve.d.add( v ), curve.d );
	}

	private Cubic2D getInitial( Hint hint ) {
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

	static double calcError( List<Point2D> fitPoints, Cubic2D curve ) {
		boolean headPointsMatch = Objects.equals( fitPoints.get( 0 ), curve.a );
		boolean tailPointsMatch = Objects.equals( fitPoints.get( fitPoints.size() - 1 ), curve.d );

		// The first and last points should match
		if( !headPointsMatch ) {
			log.log( Log.WARN, "Head points don't match" );
			return Double.NaN;
		} else if( !tailPointsMatch ) {
			log.log( Log.WARN, "Tail points don't match" );
			return Double.NaN;
		}

		// TODO There are several ways to generate the curve points
		// 1. Simple count
		// 2. Count based on number of points
		// 3. Flatness
		List<Point2D> curvePoints = curve.toPoints( 16 );

		double area = Geometry2D.findAreas( fitPoints, curvePoints ).stream().mapToDouble( Double::doubleValue ).sum();

		// Scale the error by the curve distance
		return area / (curve.a.distance( curve.d ));
	}

}
