package com.avereon.aveon;

import com.avereon.geometry.Cubic2D;
import com.avereon.geometry.Geometry2D;
import com.avereon.geometry.Point2D;
import com.avereon.geometry.SegmentedPath2D;
import lombok.CustomLog;

import java.util.List;

@CustomLog
public class CubicBezierCurveFitter2 implements CubicBezierCurveFitter {

	private final SegmentedPath2D path;

	private final Hint hint;

	private final Cubic2D bounds;

	private final Cubic2D initialCurve;

	private Cubic2D curve;

	public CubicBezierCurveFitter2( String id, List<Point2D> points, Hint hint ) {
		this.path = SegmentedPath2D.of( points );
		this.hint = hint;
		this.bounds = getInitial( path, 1.0, hint );
		this.initialCurve = getInitial( path, 0.5, hint );

		this.curve = getInitial( path, 0.5, hint );
	}

	@Override
	public Cubic2D generate() {
		adjustCurve1();
		return curve;
	}

	private void adjustCurve1() {
		int iteration = 0;

		// Get the error, scaled by the path length
		System.out.printf( "initial error=%f%n", calcError( curve ) );

		double bAnchor = 0.0;
		double cAnchor = 0.0;
		int segments = 10;
		for( int exp = 0; exp < 10; exp++ ) {
			double span = 1.0 / Math.pow( segments, exp );
			double bt = findClosestInterpOnHead( curve, bAnchor, bAnchor + span, segments );
			double ct = findClosestInterpOnTail( curve, cAnchor, cAnchor + span, segments );
			//System.out.printf( "bAnchor=%f cAnchor=%f bt=%f ct=%f%n", bAnchor, cAnchor, bt, ct );

			curve = new Cubic2D( bounds.a, bounds.a.interpolate( bounds.b, bt ), bounds.d.interpolate( bounds.c, ct ), bounds.d );

			bAnchor = bt - 0.5 / Math.pow( segments, exp+1 );
			cAnchor = ct - 0.5 / Math.pow( segments, exp+1 );
		}
	}

	private double findClosestInterpOnHead( Cubic2D curve, double m, double n, int segments ) {
		// If m is greater than n swap the values
		if( m > n ) {
			double t = m;
			m = n;
			n = t;
		}

		// The values m and n should always be between 0.0 and 1.0.
		if( m < 0.0 ) m = 0.0;
		if( n > 1.0 ) n = 1.0;

		double d = n - m;

		double t;
		double error;
		double lowestT = Double.MAX_VALUE;
		double lowestError = Double.MAX_VALUE;
		Point2D point;
		Cubic2D testCurve;
		for( int i = 0; i <= segments; i++ ) {
			t = m + ((double)i / (double)segments) * d;
			point = bounds.a.interpolate( bounds.b, t );
			testCurve = new Cubic2D( bounds.a, point, curve.c, curve.d );
			error = calcError( testCurve );
			//System.out.printf( "i=%d c=%f error=%f p=%s curve=%s %n", i, t, error, point, testCurve );

			if( error < lowestError ) {
				lowestError = error;
				lowestT = t;
			}
		}

		return lowestT;
	}

	private double findClosestInterpOnTail( Cubic2D curve, double m, double n, int segments ) {
		// If m is greater than n swap the values
		if( m > n ) {
			double t = m;
			m = n;
			n = t;
		}

		// The values m and n should always be between 0.0 and 1.0.
		if( m < 0.0 ) m = 0.0;
		if( n > 1.0 ) n = 1.0;

		double d = n - m;

		double t;
		double error;
		double lowestT = Double.MAX_VALUE;
		double lowestError = Double.MAX_VALUE;
		Point2D point;
		Cubic2D testCurve;
		for( int i = 0; i <= segments; i++ ) {
			t = m + ((double)i / (double)segments) * d;
			point = bounds.d.interpolate( bounds.c, t );
			testCurve = new Cubic2D( curve.a, curve.b, point, bounds.d );
			error = calcError( testCurve );
			//System.out.printf( "i=%d c=%f error=%f p=%s testcurve=%s %n", i, t, error, point, testCurve );

			if( error < lowestError ) {
				lowestError = error;
				lowestT = t;
			}
		}

		return lowestT;
	}

	private double calcError( Cubic2D curve ) {
		// NOTE Using calcErrorByArea will end up giving "shallow" results because
		// it is trying to reduce the area between the paths, not trying to match
		// up with the path points. It would be more appropriate to match up with
		// the path points.
		return calcErrorByDistance( curve.toPoints( 1000 ) ) / path.getLength();
	}

	/**
	 * Calculate the sum of the shortest lines between the fit points and the
	 * curve. This method should reduce near zero as the curve more accurately
	 * fits the fit points. However, it is possible that the error may never
	 * reach zero because the curve may not be able to accurately estimate the
	 * fit points.
	 *
	 * @param curvePoints The path to check
	 * @return The total error
	 */
	double calcErrorByDistance( List<Point2D> curvePoints ) {
		return Geometry2D.findPathSegmentDistances( curvePoints, path.points ).stream().mapToDouble( Double::doubleValue ).sum();
	}

	/**
	 * Calculate the error by finding the area between the fitPoints and the
	 * curve. This method, unfortunately, will never return zero because there
	 * will always be space between the segmented path defined by the fit points
	 * and the smooth curve. Therefore, iterating until there is no error is not
	 * a valid process.
	 *
	 * @param path The path to check
	 * @return The total error
	 */
	private double calcErrorByArea( List<Point2D> path ) {
		return Geometry2D.findArea( this.path.points, path );
	}

}
