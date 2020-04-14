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

	private static final NumberFormat iterationFormat = new DecimalFormat( "000" );

	private static final NumberFormat format = new DecimalFormat( "0.000000000" );

	public enum Hint {
		LEADING,
		MIDDLE,
		TRAILING
	}

	private static final double factor = 0.1;

	private final String id;

	private final SegmentedPath2D path;

	private final Cubic2D initialCurve;

	//private final double[][] fitPointWeights;

	private final double scale;

	private final Hint hint;

	private Cubic2D curve;

	private double error;

	private double errorPrior;

	private double dError;

	private double dErrorPrior;

	/**
	 * What direction to change the control point: shrink(negative) grow(positive)
	 */
	private double headError = Double.NaN;

	private double headErrorPrior = Double.NaN;

	private double headErrorDelta = Double.NaN;

	private double headPercent = 1.0;

	private double headPercentPrior = 1.0;

	private double headMovement = -0.1;

	/**
	 * What direction to change the control point: shrink(negative) grow(positive)
	 */
	private double tailError = Double.NaN;

	private double tailErrorPrior = Double.NaN;

	private double tailErrorDelta = Double.NaN;

	private double tailPercent = 1.0;

	private double tailPercentPrior = 1.0;

	private double tailMovement = -0.1;

	public CubicBezierCurveFitter( String id, List<Point2D> points, Hint hint ) {
		this.id = id;
		this.path = new SegmentedPath2D( points );
		this.initialCurve = getInitial( hint );
		this.scale = initialCurve.a.distance( initialCurve.d );
		this.hint = hint;

		// Setup initial values
		this.curve = initialCurve;

		//		// Calculate point weights
		//		int count = path.getPointCount();
		//		fitPointWeights = new double[ 3 ][ count ];
		//		for( int index = 0; index < count; index++ ) {
		//			fitPointWeights[ HEAD_WEIGHT_INDEX ][ index ] = Geometry2D.calcCubicBasisEffect( 1, path.getPercentDistance( index ) );
		//			fitPointWeights[ TAIL_WEIGHT_INDEX ][ index ] = Geometry2D.calcCubicBasisEffect( 2, path.getPercentDistance( index ) );
		//		}

		// What about CCW(left), CW(right)? Will need to know direction of control point also
	}

	public Cubic2D generate() {
		// This is the process brought from fitfoil
		//		adjustCurve1();
		//		adjustCurve2();
		//		adjustCurve3();
		adjustCurve4();
		return curve;
	}

	private void print( String id, int iteration ) {
		print( id, iteration, 0 );
	}

	private void print( String id, int parentIteration, int iteration ) {
		StringBuilder builder = new StringBuilder( id );
		builder.append( " itr=" ).append( iterationFormat.format( parentIteration ) ).append( "." ).append( iterationFormat.format( iteration ) );

		//		builder.append( " rE=" ).append( format( errorPrior ) );
		//		builder.append( " er=" ).append( format( error ) );
		//		builder.append( " dE=" ).append( format( dError, true ) );

		builder.append( " rH=" ).append( format( headErrorPrior ) );
		builder.append( " rT=" ).append( format( tailErrorPrior ) );
		builder.append( " eH=" ).append( format( headError ) );
		builder.append( " eT=" ).append( format( tailError ) );
		builder.append( " dH=" ).append( format( headErrorDelta ) );
		builder.append( " dT=" ).append( format( tailErrorDelta ) );

		builder.append( " dH=" ).append( format( headMovement ) );
		builder.append( " dT=" ).append( format( tailMovement ) );
		builder.append( " %H=" ).append( format.format( headPercent ) );
		builder.append( " %T=" ).append( format.format( tailPercent ) );
		//		builder.append( " curve=" ).append( curve );
		System.err.println( builder.toString() );
	}

	private String format( double value ) {
		return format( value, false );
	}

	private String format( double value, boolean showPlus ) {
		String text = format.format( value );
		String positive = showPlus ? "+" : " ";
		return text.startsWith( "-" ) ? text : positive + text;
	}

	/**
	 * Determine if the curve matches close enough to the curve
	 *
	 * @return True if the curve is "close enough"
	 */
	private boolean closeEnough( int iteration ) {
		int maxIterations = 1;
		double headMove = Math.abs( headMovement );
		double tailMove = Math.abs( tailMovement );
		return headMove < 1e-15 && tailMove < 1e-15;
		//return iteration != 0 && ((headMove < 1e-15 && tailMove < 1e-15) || iteration >= maxIterations);
	}

	private void adjustCurve4() {
		Cubic2D goal = new Cubic2D( 0, 0, 0, 0.05, 0.2, 0.2, 0.4, 0.2 );
		SegmentedPath2D stationPath = goal.toPath( 8 );

		Point2D headCtrl = new Point2D( goal.a.x, goal.d.y ).subtract( goal.a );
		Point2D tailCtrl = new Point2D( goal.a.x, goal.d.y ).subtract( goal.d );

		int tailCount = 10;
		int headCount = 10;

		for( int headIndex = 0; headIndex <= headCount; headIndex++ ) {
			double headT = headIndex / (double)headCount;
			System.out.print( headT );

			for( int tailIndex = 0; tailIndex <= tailCount; tailIndex++ ) {
				double tailT = tailIndex / (double)tailCount;
				Cubic2D curve = new Cubic2D( goal.a, goal.a.add( headCtrl.multiply( headT ) ), goal.d.add( tailCtrl.multiply( tailT ) ), goal.d );

				double e = calcErrorBySquareOffset( stationPath.getPoints(), curvePoints( curve ), -1 );

				System.out.print( "," + e );
				//System.out.println( "t=" + t + " e=" + e );
			}
			System.out.println();
		}
	}

	private void adjustCurve3() {
		int iteration = 0;

		// Set the initial prior error to the current error
		errorPrior = calcErrorBySquareOffset( curve, 1 );
		dErrorPrior = Double.NaN;

		// Start with moving the control vectors 1/10 their original size
		headMovement = -0.5;
		tailMovement = -0.5;

		while( !closeEnough( iteration ) && iteration < 1 ) {

			adjust3( iteration, true );
			//			adjust3( iteration, false );
			//			adjust3( iteration, false );
			//			adjust3( iteration, true );

			iteration++;
		}
		print( "rt", iteration );
	}

	private void adjust3( int parentIteration, boolean head ) {
		int iteration = 0;

		//		this.headMovement = -Math.pow( 10, -(parentIteration + 1) );
		//		this.tailMovement = -Math.pow( 10, -(parentIteration + 1) );

		Point2D initHeadVector = initialCurve.b.subtract( initialCurve.a );
		Point2D initTailVector = initialCurve.c.subtract( initialCurve.d );
		Point2D newHeadVector;
		Point2D newTailVector;
		double headMovement = this.headMovement;
		double tailMovement = this.tailMovement;
		double headPercent = this.headPercent;
		double tailPercent = this.tailPercent;

		double headError = this.headError;
		double tailError = this.tailError;
		double headErrorDelta = this.headErrorDelta;
		double tailErrorDelta = this.tailErrorDelta;
		double headErrorPrior = this.headErrorPrior;
		double tailErrorPrior = this.tailErrorPrior;

		//		double errorPrior = this.errorPrior;
		//		double dErrorPrior = this.dErrorPrior;
		//		double error;
		//		double dError;

		Cubic2D curve = this.curve;

		while( iteration < 20 ) {
			if( iteration == 0 ) print( "v3" + (head ? "h" : "t") + "a", parentIteration, iteration );

			if( head ) {
				headErrorPrior = headError;
				headPercent = headPercent + headMovement;
				newHeadVector = initHeadVector.multiply( headPercent );
				curve = new Cubic2D( curve.a, curve.a.add( newHeadVector ), curve.c, curve.d );
				headError = calcErrorBySquareOffset( curve, -1 );
				headErrorDelta = headError - headErrorPrior;
			} else {
				tailErrorPrior = tailError;
				tailPercent = tailPercent + tailMovement;
				newTailVector = initTailVector.multiply( tailPercent );
				curve = new Cubic2D( curve.a, curve.b, curve.d.add( newTailVector ), curve.d );
				tailError = calcErrorBySquareOffset( curve, -1 );
				tailErrorDelta = tailError - tailErrorPrior;
			}

			//error = calcErrorBySquareOffset( curve, 1 );
			//dError = error - errorPrior;
			//errorPrior = error;
			//dErrorPrior = dError;

			if( head && headErrorDelta > 0 ) headMovement *= -0.1;
			if( !head && tailErrorDelta > 0 ) tailMovement *= -0.1;
			//			if( dError > 0 ) {
			//				if( head ) {
			//					headMovement *= -1;
			//				} else {
			//					tailMovement *= -1;
			//				}
			//			}

			this.curve = curve;
			//			this.error = error;
			//			this.dError = dError;
			//			this.errorPrior = errorPrior;
			//			this.dErrorPrior = dErrorPrior;
			this.headError = headError;
			this.headErrorDelta = headErrorDelta;
			this.headErrorPrior = headErrorPrior;
			this.tailError = tailError;
			this.tailErrorDelta = tailErrorDelta;
			this.tailErrorPrior = tailErrorPrior;
			this.headMovement = headMovement;
			this.tailMovement = tailMovement;
			this.headPercent = headPercent;
			this.tailPercent = tailPercent;

			print( "v3" + (head ? "h" : "t") + "b", parentIteration, iteration );
			iteration++;
		}
	}

	/**
	 * This is the process brought from fitfoil.
	 */
	private void adjustCurve1() {
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
			headError = -calcErrorByOffset( curve, 1 );
			tailError = -calcErrorByOffset( curve, 2 );
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

			print( "v1", iteration );

			iteration++;
			if( iteration % iterationsPerSide == 0 ) tail = !tail;
		}
	}

	private void adjustCurve2() {
		int iteration = 0;
		boolean tail = false;
		int iterationsPerSide = 4;
		Point2D initHeadVector = initialCurve.b.subtract( initialCurve.a );
		Point2D initTailVector = initialCurve.c.subtract( initialCurve.d );

		headError = calcErrorByDistance( curve, 1 );
		tailError = calcErrorByDistance( curve, 2 );
		error = calcError( curve );

		double percent;
		double testError;
		double testErrorDelta;
		double testErrorPrior = headError;

		//		headMovement = -0.1;
		//		tailMovement = -0.1;

		//print( "initial" );

		Cubic2D result = curve;
		while( !closeEnough( iteration ) ) {
			if( !tail ) {
				// The test percent
				percent = headPercent + headMovement;

				// Adjust the head
				result = new Cubic2D( result.a, result.a.add( initHeadVector.multiply( percent ) ), result.c, result.d );
				testError = calcErrorByDistance( result, 1 );
				testErrorDelta = testError - testErrorPrior;

				//System.err.println( "  head err=" + testError + " dE=" + testErrorDelta );

				// At this point we know if we are improving or not
				if( testErrorDelta < 0 ) {
					// Accept this result
					this.curve = result;
					this.headError = testError;
					this.headErrorDelta = testErrorDelta;
					this.headPercent = percent;
				} else {
					// We did not improve
					headMovement *= -0.8;
					//break;
				}
			}

			if( tail ) {
				// The test percent
				percent = tailPercent + tailMovement;

				// Adjust the tail
				result = new Cubic2D( result.a, result.b, result.d.add( initTailVector.multiply( percent ) ), result.d );
				testError = calcErrorByDistance( result, 1 );
				testErrorDelta = testError - testErrorPrior;

				//System.err.println( "  tail err=" + testError + " dE=" + testErrorDelta );

				// At this point we know if we are improving or not
				if( testErrorDelta < 0 ) {
					// Accept this result
					this.curve = result;
					//this.error = error;
					//this.dError = dError;
					this.tailError = testError;
					this.tailErrorDelta = testErrorDelta;
					this.tailPercent = percent;
				} else {
					// We did not improve
					tailMovement *= -0.8;
					//break;
				}
			}

			print( "v2", iteration );

			iteration++;
			if( iteration % iterationsPerSide == 0 ) tail = !tail;
		}
		//System.err.println();
		print( "rt", iteration );
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

		// FIXME Switch these to vertical, horizontal and tangent for the head and tail
		switch( hint ) {
			case LEADING: {
				a = new Point2D( p.x, p.y );
				b = new Point2D( p.x, 0.5 * r.y );
				c = new Point2D( 0.5 * p.x, r.y );
				d = new Point2D( r );
				break;
			}
			case TRAILING: {
				a = new Point2D( p );
				b = new Point2D( 0.5 * r.x, p.y );
				c = r.add( q.subtract( r ).normalize().multiply( Math.abs( r.x - p.x ) ).multiply( 0.5 ) );
				d = new Point2D( r );
				break;
			}
			default: {
				a = new Point2D( p );
				b = new Point2D( 0.5 * r.x, p.y );
				c = new Point2D( 0.5 * p.x, r.y );
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
			points = curve.toPoints( 10 );
			//return curve.toSizePoints( 0.001 * scale );
			//return curve.toFlatPoints( 0.0001 * scale );

			pointCache.clear();
			pointCache.put( curve, points );
		}

		return points;
	}

	double calcError( Cubic2D curve ) {
		return calcErrorByDistance( curvePoints( curve ) ) / scale;
	}

	private double calcErrorBySquareOffset( Cubic2D curve, int basisIndex ) {
		return calcErrorBySquareOffset( curvePoints( curve ), path.points, basisIndex ) / scale;
	}

	double calcErrorBySquareOffset( List<Point2D> curvePoints, List<Point2D> fitPoints, int basisIndex ) {
		//validateEndPoints( curvePoints );
		final List<Double> offsets = Geometry2D.findPathOffsets( curvePoints, fitPoints );
		int count = offsets.size() - 1;
		return IntStream
			.range( 0, count )
			.mapToDouble( i -> offsets.get( i ) * offsets.get( i ) * Geometry2D.calcCubicBasisEffect( basisIndex, ((double)i / (double)count) ) )
			.sum();
	}

	private double calcErrorByOffset( Cubic2D curve, int basisIndex ) {
		return calcErrorByOffset( curvePoints( curve ), path.points, basisIndex ) / scale;
	}

	double calcErrorByOffset( List<Point2D> curvePoints, List<Point2D> fitPoints, int basisIndex ) {
		//validateEndPoints( curvePoints );
		final List<Double> offsets = Geometry2D.findPathOffsets( curvePoints, fitPoints );
		int count = offsets.size() - 1;
		return IntStream.range( 0, count ).mapToDouble( i -> offsets.get( i ) * Geometry2D.calcCubicBasisEffect( basisIndex, ((double)i / (double)count) ) ).sum();
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
	double calcErrorByDistance( List<Point2D> curvePoints ) {
		//validateEndPoints( curvePoints );
		return Geometry2D.findPathSegmentDistances( curvePoints, path.points ).stream().mapToDouble( Double::doubleValue ).sum();
	}

	private double calcErrorByDistance( Cubic2D curve, int basisIndex ) {
		return calcErrorByDistance( curvePoints( curve ), path.points, basisIndex ) / scale;
	}

	/**
	 * Calculate the absolute error between the curve points and the fit points
	 * weighted toward the head of the curve.
	 *
	 * @param curvePoints The curve points to test
	 * @param fitPoints The points to compare with
	 * @return The absolute error weighted toward the head of the curve
	 */
	double calcErrorByDistance( List<Point2D> curvePoints, List<Point2D> fitPoints, int basisIndex ) {
		//validateEndPoints( curvePoints );
		final List<Double> offsets = Geometry2D.findPathSegmentDistances( curvePoints, fitPoints );
		int count = offsets.size() - 1;
		return IntStream.range( 0, count ).mapToDouble( i -> offsets.get( i ) * Geometry2D.calcCubicBasisEffect( basisIndex, ((double)i / (double)count) ) ).sum();
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
