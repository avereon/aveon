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

	private static final double factor = 0.1;

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

	private double headErrorDelta = 0;

	private double headPercent = 1.0;

	private double headPercentPrior = 1.0;

	private double headMovement = 1;

	/**
	 * What direction to change the control point: shrink(negative) grow(positive)
	 */
	private double tailError = 0;

	private double tailErrorDelta = 0;

	private double tailPercent = 1.0;

	private double tailPercentPrior = 1.0;

	private double tailMovement = 1;

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
		//adjustCurve();
		adjustCurve2();
		return curve;
	}

	private void print( String id ) {
		StringBuilder builder = new StringBuilder( id );
		builder.append( " eH=" ).append( format( headError ) );
		builder.append( " eT=" ).append( format( tailError ) );
		builder.append( " dH=" ).append( format( headErrorDelta ) );
		builder.append( " dT=" ).append( format( tailErrorDelta ) );
		builder.append( " pH=" ).append( format.format( headPercent ) );
		builder.append( " pT=" ).append( format.format( tailPercent ) );
		builder.append( " dH=" ).append( format( headMovement ) );
		builder.append( " dT=" ).append( format( tailMovement ) );
		builder.append( " curve=" ).append( curve );
		System.err.println( builder.toString() );
	}

	private String format( double value ) {
		String text = format.format( value );

		return text.startsWith( "-" ) ? text : " " + text;
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
		return iteration != 0 && ((headDir < 1e-15 && tailDir < 1e-15) || iteration >= maxIterations);
	}

	private void adjustCurve2() {
		int iteration = 0;
		boolean tail = false;
		int iterationsPerSide = 2;
		Point2D initHeadVector = initialCurve.b.subtract( initialCurve.a );
		Point2D initTailVector = initialCurve.c.subtract( initialCurve.d );
		Point2D newHeadVector;
		Point2D newTailVector;

		//double error = 0;
		double errorPrior = 0;
		double errorDelta = 0;
		double errorDeltaPrior = 0;

		double headErrorPrior = 0;
		double headErrorDeltaPrior = 0;

		double tailErrorPrior = 0;
		double tailErrorDeltaPrior = 0;

		double headDirection = 1;
		double tailDirection = 1;

		while( !closeEnough( iteration ) ) {
			headError = -calcHeadError2( curve );
			tailError = -calcTailError2( curve );
			// This value is for information purpose only
			error = Math.abs( headError ) + Math.abs( tailError );

			headErrorDelta = headError - headErrorPrior;
			tailErrorDelta = tailError - tailErrorPrior;
			// This value is for information purpose only
			errorDelta = error - errorPrior;

			if( headErrorDelta > headErrorDeltaPrior ) headDirection *= -1.0;
			if( tailErrorDelta > tailErrorDeltaPrior ) tailDirection *= -1.0;

			// NOTE When the movements near zero, the process is near finished
			headMovement = headError * headDirection;
			tailMovement = tailError * tailDirection;

			headPercent = headPercentPrior * (1 + headMovement);
			tailPercent = tailPercentPrior * (1 + tailMovement);

			newHeadVector = initHeadVector.multiply( headPercent );
			newTailVector = initTailVector.multiply( tailPercent );

			if( !tail ) curve = new Cubic2D( curve.a, curve.a.add( newHeadVector ), curve.c, curve.d );
			if( tail ) curve = new Cubic2D( curve.a, curve.b, curve.d.add( newTailVector ), curve.d );

			headPercentPrior = headPercent;
			tailPercentPrior = tailPercent;
			headErrorDeltaPrior = headErrorDelta;
			tailErrorDeltaPrior = tailErrorDelta;
			headErrorPrior = headError;
			tailErrorPrior = tailError;
			errorPrior = error;

			print( "itr-" + iterationFormat.format( iteration ) );

			iteration++;
			if( iteration % iterationsPerSide == 0 ) tail = !tail;
		}
	}

	private void adjustCurve() {
		int iteration = 0;
		boolean tail = false;
		int iterationsPerSide = 2;
		Point2D initHeadVector = initialCurve.b.subtract( initialCurve.a );
		Point2D initTailVector = initialCurve.c.subtract( initialCurve.d );

		headError = calcHeadError( curve );
		tailError = calcTailError( curve );
		error = calcError( curve );

		// NEXT Finish reworking this

		//			double error;
		//			double dError;
		double headErrorPrior = this.headError;
		double tailErrorPrior = this.tailError;

		//print( "initial" );

		Cubic2D result = curve;
		while( !closeEnough( iteration ) ) {

			// Adjust the head

			// The test percent
			double percent = headPercent += headMovement;

			result = new Cubic2D( result.a, result.a.add( initHeadVector.multiply( percent ) ), result.c, result.d );
			//error = calcError( result );
			//dError = error - priorError;
			//error = calcHeadError( result );
			//dError = error - headPriorError;
			headError = calcTailError( result );
			dError = error - headErrorPrior;

			//System.err.println( "  head err=" + error + " dE=" + dError );

			// At this point we know if we are improving or not
			if( dError < 0 ) {
				// Accept this result
				this.curve = result;
				//this.error = error;
				//this.dError = dError;
				this.headError = error;
				this.headErrorDelta = dError;
				this.headPercent = percent;
				headErrorPrior = error;
			} else {
				// We did not improve
				headMovement *= -0.1;
				break;
			}

			// Adjust the tail

			// The test percent
			percent = tailPercent += tailMovement;

			result = new Cubic2D( result.a, result.b, result.d.add( initTailVector.multiply( percent ) ), result.d );
			//error = calcError( result );
			//dError = error - priorError;
			error = calcHeadError( result );
			dError = error - headErrorPrior;
			//error = calcTailError( result );
			//dError = error - tailPriorError;

			//System.err.println( "  tail err=" + error + " dE=" + dError );

			// At this point we know if we are improving or not
			if( dError < 0 ) {
				// Accept this result
				this.curve = result;
				//this.error = error;
				//this.dError = dError;
				this.tailError = error;
				this.tailErrorDelta = dError;
				this.tailPercent = percent;
				tailErrorPrior = error;
			} else {
				// We did not improve
				tailMovement *= -0.1;
				break;
			}

			print( "itr-" + iterationFormat.format( iteration ) );

			iteration++;
			if( iteration % iterationsPerSide == 0 ) tail = !tail;
		}
		System.err.println();
		print( "result " );
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
			points = curve.toPoints( 100 );
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

	private double calcHeadError2( Cubic2D curve ) {
		return calcHeadError2( curvePoints( curve ) );
	}

	private double calcTailError2( Cubic2D curve ) {
		return calcTailError2( curvePoints( curve ) );
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
		//validateEndPoints( curvePoints );
		return Geometry2D.findDistances( path.points, curvePoints ).stream().mapToDouble( Double::doubleValue ).sum();
	}

	double calcHeadError( List<Point2D> curvePoints ) {
		//validateEndPoints( curvePoints );
		final List<Double> offsets = Geometry2D.findDistances( path.points, curvePoints );
		return IntStream.range( 0, offsets.size() ).mapToDouble( i -> offsets.get( i ) * weights[ HEAD_WEIGHT_INDEX ][ i ] ).sum();
	}

	double calcTailError( List<Point2D> curvePoints ) {
		//validateEndPoints( curvePoints );
		final List<Double> offsets = Geometry2D.findDistances( path.points, curvePoints );
		return IntStream.range( 0, offsets.size() ).mapToDouble( i -> offsets.get( i ) * weights[ TAIL_WEIGHT_INDEX ][ i ] ).sum();
	}

	double calcHeadError2( List<Point2D> curvePoints ) {
		//validateEndPoints( curvePoints );
		final List<Double> offsets = Geometry2D.findCcwDistances( path.points, curvePoints );
		return IntStream.range( 0, offsets.size() ).mapToDouble( i -> offsets.get( i ) * weights[ HEAD_WEIGHT_INDEX ][ i ] ).sum();
	}

	double calcTailError2( List<Point2D> curvePoints ) {
		//validateEndPoints( curvePoints );
		final List<Double> offsets = Geometry2D.findCcwDistances( path.points, curvePoints );
		return IntStream.range( 0, offsets.size() ).mapToDouble( i -> offsets.get( i ) * weights[ TAIL_WEIGHT_INDEX ][ i ] ).sum();
	}

	/**
	 * Calculate the error by finding the area between the fitPoints and the
	 * curve. This method, unfortunately, will never return zero because there
	 * will always be space between the segmented path defined by the fit points
	 * and the smooth curve. Therefore iterating until there is no error is not
	 * a valid process.
	 *
	 * @param path The path to check
	 * @return The total error
	 */
	double calcErrorByArea( List<Point2D> path ) {
		validateEndPoints( path );
		return Geometry2D.findAreas( this.path.points, path ).stream().mapToDouble( Double::doubleValue ).sum();
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
