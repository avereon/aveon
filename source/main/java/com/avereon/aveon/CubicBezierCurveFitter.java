package com.avereon.aveon;

import com.avereon.geometry.Cubic2D;
import com.avereon.geometry.Geometry2D;
import com.avereon.geometry.Point2D;
import com.avereon.geometry.SegmentedPath2D;
import com.avereon.util.Log;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

public class CubicBezierCurveFitter {

	private static final int HEAD_WEIGHT_INDEX = 1;

	private static final int TAIL_WEIGHT_INDEX = 2;

	private static final System.Logger log = Log.get();

	private static NumberFormat iterationFormat = new DecimalFormat( "000" );

	private static NumberFormat format = new DecimalFormat( "0.000000000" );

	public enum Hint {
		LEADING,
		MIDDLE,
		TRAILING
	}

	private static final double factor = 0.05;

	private final String id;

	private final SegmentedPath2D path;

	private final Cubic2D initialCurve;

	private final double[][] weights;

	private final double scale;

	private final Hint hint;

	private Cubic2D curve;

	private double error;

	private double dError;

	/**
	 * What direction to change the control point: shrink(negative) grow(positive)
	 */
	private double headError = 0;

	private double headPercent = 1.0;

	private double headMovement = -factor;

	/**
	 * What direction to change the control point: shrink(negative) grow(positive)
	 */
	private double tailError = 0;

	private double tailPercent = 1.0;

	private double tailMovement = -factor;

	public CubicBezierCurveFitter( String id, List<Point2D> points, Hint hint ) {
		this.id = id;
		this.path = new SegmentedPath2D( points );
		this.initialCurve = getInitial( hint );
		this.scale = initialCurve.a.distance( initialCurve.d );
		this.hint = hint;

		// Setup initial values
		this.curve = initialCurve;

		// Calculate point weights
		int count = path.getPointCount();
		weights = new double[ 3 ][ count ];
		for( int index = 0; index < count; index++ ) {
			weights[ HEAD_WEIGHT_INDEX ][ index ] = Geometry2D.calcCubicBasisEffect( 1, path.getPercentDistance( index ) );
			weights[ TAIL_WEIGHT_INDEX ][ index ] = Geometry2D.calcCubicBasisEffect( 2, path.getPercentDistance( index ) );
		}

		// What about CCW(left), CW(right)? Will need to know direction of control point also
	}

	public Cubic2D generate() {
		error = calcError( curve );
		headError = calcHeadError( curve );
		tailError = calcTailError( curve );

		//print( "initial" );

		int iteration = 0;
		while( !closeEnough( iteration ) ) {
			print( "itr-" + iterationFormat.format( iteration ) );
			adjustCurve();
			iteration++;
		}
		System.err.println();
		print( "result " );

		return curve;
	}

	private void print( String id ) {
		StringBuilder builder = new StringBuilder( id );
		builder.append( " err=" ).append( format( error ) );
		builder.append( " dE=" ).append( format( dError ) );
		builder.append( " pH=" ).append( format.format( headPercent ) );
		builder.append( " pT=" ).append( format.format( tailPercent ) );
		builder.append( " dH=" ).append( format( headMovement ) );
		builder.append( " dT=" ).append( format( tailMovement ) );
		builder.append( " curve=" ).append( curve );
		System.err.println( builder.toString() );
	}

	private String format( double value ) {
		return format.format( value );
	}

	/**
	 * Determine if the curve matches close enough to the curve
	 *
	 * @return True if the curve is "close enough"
	 */
	private boolean closeEnough( int iteration ) {
		int maxIterations = 100;

		double headDir = Math.abs( headMovement );
		double tailDir = Math.abs( tailMovement );

		return iteration >= maxIterations;

		//return iteration != 0 && ((headDir < 0.0001 && tailDir < 0.0001) || error < 0.00001 || iteration >= maxIterations);
	}

	private void adjustCurve() {
		double priorError = this.error;
		Cubic2D result = curve;
		double error;
		double dError;
		int steps = 1;

		// Adjust the head
		for( int index = 0; index < steps; index++ ) {
			// The initial head magnitude
			// TODO Cache this value
			Point2D m = initialCurve.b.subtract( initialCurve.a );

			// The test percent
			double percent = headPercent += headMovement;

			result = new Cubic2D( result.a, result.a.add( m.multiply( percent ) ), result.c, result.d );
			error = calcError( result );
			dError = error - priorError;
			//error = calcHeadError( test );
			//dError = error - this.headError;
			//error = calcTailError( test );
			//dError = error - this.tailError;

			// At this point we know if we are improving or not
			if( dError < 0 ) {
				// Accept this result
				this.curve = result;
				this.error = error;
				this.dError = dError;
				this.headPercent = percent;
				priorError = error;
			} else {
				// We did not improve
				headMovement *= -0.1;
				break;
			}
		}

		// Adjust the tail
		for( int index = 0; index < steps; index++ ) {
			// The initial tail magnitude
			// TODO Cache this value
			Point2D m = initialCurve.c.subtract( initialCurve.d );

			// The test percent
			double percent = tailPercent += tailMovement;

			result = new Cubic2D( result.a, result.b, result.d.add( m.multiply( percent ) ), result.d );
			error = calcError( result );
			dError = error - priorError;
			//error = calcHeadError( test );
			//dError = error - this.headError;
			//error = calcTailError( test );
			//dError = error - this.tailError;

			// At this point we know if we are improving or not
			if( dError < 0 ) {
				// Accept this result
				this.curve = result;
				this.error = error;
				this.dError = dError;
				this.tailPercent = percent;
				priorError = error;
			} else {
				// We did not improve
				tailMovement *= -0.1;
				break;
			}
		}
	}

	private Cubic2D tweakHead( Cubic2D curve ) {
		// Calculate a new percentage of the initial control point

		// The initial magnitude
		Point2D m = initialCurve.b.subtract( initialCurve.a );

		// The new percent
		double percent = headPercent += headMovement;

		return new Cubic2D( curve.a, curve.a.add( m.multiply( percent ) ), curve.c, curve.d );
	}

	private Cubic2D tweakTail( Cubic2D curve ) {
		// The current vector to the control point
		Point2D u = curve.c.subtract( curve.d );
		// Using the initial curve avoids additive rounding errors
		Point2D d = initialCurve.c.subtract( initialCurve.d ).normalize();
		double factor = 1 + headMovement;
		//System.err.println( "    tail factor=" + factor );
		Point2D v = d.multiply( u.magnitude() * (1 + tailMovement) );
		return new Cubic2D( curve.a, curve.b, curve.d.add( v ), curve.d );
	}

	private Cubic2D getInitial( Hint hint ) {
		Point2D p = path.points.get( 0 );
		Point2D q = path.points.get( path.pointCount - 2 );
		Point2D r = path.points.get( path.pointCount - 1 );

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

	private Map<Cubic2D, List<Point2D>> pointCache = new HashMap<>();

	private List<Point2D> curvePoints( Cubic2D curve ) {
		List<Point2D> points = pointCache.get( curve );

		if( points == null ) {
			// TODO There are several ways to generate the curve points
			// 1. Segment count
			// 2. Sizeness
			// 3. Flatness
			points = curve.toPoints( 36 );
			//return curve.toSizePoints( 0.001 * scale );
			//return curve.toFlatPoints( 0.0001 * scale );

			pointCache.clear();
			pointCache.put( curve, points );
		}

		return points;
	}

	double calcError( Cubic2D curve ) {
		return calcErrorByOffset( curvePoints( curve ) ) / scale;
	}

	private double calcHeadError( Cubic2D curve ) {
		return calcHeadError( curvePoints( curve ) ) / scale;
	}

	private double calcTailError( Cubic2D curve ) {
		return calcTailError( curvePoints( curve ) ) / scale;
	}

	/**
	 * Calculate the sum of the shortest lines between the fit points and the
	 * curve. This method should reduce near zero as the curve more accurately
	 * fits the fitPoints. However, it is possible that the error may never
	 * reach zero because the curve may not be able to accurately estimate the
	 * fitPoints.
	 *
	 * @param curvePoints The path to check
	 * @return The total error
	 */
	double calcErrorByOffset( List<Point2D> curvePoints ) {
		validateEndPoints( curvePoints );
		return Geometry2D.findDistances( path.points, curvePoints ).stream().mapToDouble( Double::doubleValue ).sum();
	}

	double calcHeadError( List<Point2D> curvePoints ) {
		validateEndPoints( curvePoints );
		final List<Double> offsets = Geometry2D.findDistances( path.points, curvePoints );
		return IntStream.range( 0, offsets.size() ).mapToDouble( i -> offsets.get( i ) * weights[ HEAD_WEIGHT_INDEX ][ i ] ).sum();
	}

	double calcTailError( List<Point2D> curvePoints ) {
		validateEndPoints( curvePoints );
		final List<Double> offsets = Geometry2D.findDistances( path.points, curvePoints );
		return IntStream.range( 0, offsets.size() ).mapToDouble( i -> offsets.get( i ) * weights[ TAIL_WEIGHT_INDEX ][ i ] ).sum();
	}

	/**
	 * Calculate the error by finding the area between the fitPoints and the
	 * curve. This method, unfortunately, will never return zero because there
	 * will always be space between the segmented path defined by the fit points
	 * and the smooth curve. Therefore iterating until there is no error is not
	 * a valid process.
	 *
	 * @param curve
	 * @return
	 */
	double calcErrorByArea( Cubic2D curve ) {
		validateEndPoints( curve );

		// TODO There are several ways to generate the curve points
		// 1. Simple count
		// 2. Count based on number of points
		// 3. Flatness
		List<Point2D> curvePoints = curve.toPoints( 16 );

		double area = Geometry2D.findAreas( path.points, curvePoints ).stream().mapToDouble( Double::doubleValue ).sum();

		// Scale the error by the curve distance
		return area / (curve.a.distance( curve.d ));
	}

	private void validateEndPoints( Cubic2D curve ) {
		validateEndPoints( curve.a, curve.d );
	}

	private void validateEndPoints( List<Point2D> path ) {
		validateEndPoints( path.get( 0 ), path.get( path.size() - 1 ) );
	}

	private void validateEndPoints( Point2D head, Point2D tail ) {
		boolean headPointsMatch = Objects.equals( head, path.points.get( 0 ) );
		boolean tailPointsMatch = Objects.equals( tail, path.points.get( path.pointCount - 1 ) );
		if( !headPointsMatch ) throw new IllegalArgumentException( "Head points don't match" );
		if( !tailPointsMatch ) throw new IllegalArgumentException( "Tail points don't match" );
	}

}
