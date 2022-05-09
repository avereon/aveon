package com.avereon.geometry;

import java.util.Arrays;

/**
 * The Polynomial class represents a mathematical polynomial.
 *
 * @author mvsoder
 */
public class Polynomial {

	public static final double TOLERANCE = 1e-12;

	public static final double ACCURACY = 9;

	public static final double LN2 = Math.log( 2 );

	public static final double LN10 = Math.log( 10 );

	private static final double ONE_THIRD = 1.0 / 3.0;

	private static final double PI_OVER_THREE = Math.PI / 3;

	private double[] coefficients;

	private Polynomial() {}

	/**
	 * The Polynomial class represents a mathematical polynomial by specifying the
	 * coefficients in standard order. One or more coefficients initialize the
	 * polynomial. The coefficients are in order by highest degree monomial first.
	 * <p>
	 * For example, the following example initializes a polynomial object for:
	 * 3x^4 + 2x^2 + 5:
	 * <p>
	 * <code>Polynomial p = new Polynomial(3, 0, 2, 0, 5);</code>
	 * <p>
	 * All coefficients from highest degree to degree 0 must be provided. A zero
	 * is used for monomials that are not present in the polynomial.
	 * <p>
	 * NOTE: The polynomial coefficients are stored in an array in the reverse
	 * order to how they were specified. This has the benefit that the
	 * coefficient's position in the array corresponds to the degree of the
	 * monomial to which it belongs.
	 *
	 * @param coefficients The list of coefficients
	 */
	public Polynomial( double... coefficients ) {
		if( coefficients == null ) throw new NullPointerException( "Coefficients array cannot be null." );
		if( coefficients.length == 0 ) throw new IllegalArgumentException( "Must have at least one coefficient." );

		// Store the coefficients in the array in reverse order.
		this.coefficients = new double[coefficients.length];
		for( int index = 0; index < coefficients.length; index++ ) {
			this.coefficients[coefficients.length - index - 1] = coefficients[index];
		}

		simplify();
	}

	/**
	 * Get the degree of the polynomial.
	 *
	 * @return
	 */
	public final int getDegree() {
		return coefficients.length - 1;
	}

	/**
	 * Get the polynomial coefficients. The returned array stores the coefficients
	 * from highest (index 0) degree to degree 0 (index n).
	 *
	 * @return The coefficients array.
	 */
	public final double[] getCoefficients() {
		double[] coefficients = new double[this.coefficients.length];
		for( int index = 0; index < this.coefficients.length; index++ ) {
			coefficients[this.coefficients.length - 1 - index] = this.coefficients[index];
		}
		return coefficients;
	}

	/**
	 * Evaluate the polynomial at the specified value.
	 *
	 * @param value
	 * @return
	 */
	public final double evaluate( double value ) {
		double result = 0;

		for( int index = this.coefficients.length - 1; index >= 0; index-- ) {
			result = result * value + this.coefficients[index];
		}

		return result;
	}

	/**
	 * Create a new polynomial that is the sum of this polynomial and the
	 * specified polynomial.
	 *
	 * @param that
	 * @return
	 */
	public final Polynomial add( Polynomial that ) {
		Polynomial result = new Polynomial();
		int d1 = this.getDegree();
		int d2 = that.getDegree();
		int size = Math.max( d1, d2 );

		result.coefficients = new double[size + 1];
		for( int i = 0; i <= size; i++ ) {
			double v1 = ( i <= d1 ) ? this.coefficients[i] : 0;
			double v2 = ( i <= d2 ) ? that.coefficients[i] : 0;

			result.coefficients[i] = v1 + v2;
		}

		return result;
	}

	public final Polynomial multiply( Polynomial that ) {
		Polynomial result = new Polynomial();

		int size = this.getDegree() + that.getDegree();

		result.coefficients = new double[size + 1];
		for( int i = 0; i <= size; i++ ) {
			result.coefficients[i] = 0;
		}

		for( int i = 0; i <= this.getDegree(); i++ ) {
			for( int j = 0; j <= that.getDegree(); j++ ) {
				result.coefficients[i + j] += this.coefficients[i] * that.coefficients[j];
			}
		}

		return result;
	}

	/**
	 * Divide this polynomial by a scalar.
	 *
	 * @param scalar
	 */
	public final void divide( double scalar ) {
		for( int i = 0; i < this.coefficients.length; i++ ) {
			this.coefficients[i] /= scalar;
		}
	}

	/**
	 * Get the derivative polynomial of this polynomial.
	 *
	 * @return
	 */
	public final Polynomial getDerivative() {
		Polynomial derivative = new Polynomial();
		derivative.coefficients = new double[this.coefficients.length - 1];

		for( int i = 1; i < this.coefficients.length; i++ ) {
			derivative.coefficients[i - 1] = i * this.coefficients[i];
		}

		return derivative;
	}

	public final double bisection( double min, double max ) {
		double result = Double.NaN;
		double minValue = this.evaluate( min );
		double maxValue = this.evaluate( max );

		if( Math.abs( minValue ) <= Polynomial.TOLERANCE ) {
			result = min;
		} else if( Math.abs( maxValue ) <= Polynomial.TOLERANCE ) {
			result = max;
		} else if( minValue * maxValue <= 0 ) {
			double tmp1 = Math.log( max - min );
			double tmp2 = LN10 * Polynomial.ACCURACY;
			double iters = Math.ceil( ( tmp1 + tmp2 ) / LN2 );

			for( int i = 0; i < iters; i++ ) {
				result = 0.5 * ( min + max );
				double value = this.evaluate( result );

				if( Math.abs( value ) <= Polynomial.TOLERANCE ) break;

				if( value * minValue < 0 ) {
					max = result;
					maxValue = value;
				} else {
					min = result;
					minValue = value;
				}
			}
		}

		return result;
	}

	public final double[] getRoots() {
		double[] result = new double[0];

		switch( getDegree() ) {
			case 0: {
				return result;
			}
			case 1: {
				return getLinearRoot();
			}
			case 2: {
				return getQuadricRoots();
			}
			case 3: {
				return getCubicRoots();
			}
			case 4: {
				return getQuarticRoots();
			}
		}
		return result;
	}

	public final double[] getRootsInInterval( double min, double max ) {
		int count = 0;
		double value = 0;
		double[] roots = new double[getDegree()];

		if( this.getDegree() == 1 ) {
			value = this.bisection( min, max );
			if( value != Double.NaN ) roots[count++] = value;
		} else {
			// Get the roots of the derivative.
			Polynomial derivitive = this.getDerivative();
			double[] derivitiveRoots = derivitive.getRootsInInterval( min, max );

			if( derivitiveRoots.length > 0 ) {
				// Find the roots on [min, derivitiveRoots[0]]
				value = this.bisection( min, derivitiveRoots[0] );
				if( value != Double.NaN ) roots[count++] = value;

				// Find the roots on [derivitiveRoots[i],derivitiveRoots[i+1]] for 0 <= i <= count-2
				for( int i = 0; i <= derivitiveRoots.length - 2; i++ ) {
					value = this.bisection( derivitiveRoots[i], derivitiveRoots[i + 1] );
					if( value != Double.NaN ) roots[count++] = value;
				}

				// Find the roots on [derivitiveRoots[count-1],xmax]
				value = this.bisection( derivitiveRoots[derivitiveRoots.length - 1], max );
				if( value != Double.NaN ) roots[count++] = value;
			} else {
				// The polynomial is monotone on [min,max]. Has at most one root.
				value = this.bisection( min, max );
				if( value != Double.NaN ) roots[count++] = value;
			}
		}

		if( count == roots.length ) return roots;

		double[] result = new double[count];
		System.arraycopy( roots, 0, result, 0, count );
		return result;
	}

	@Override
	public boolean equals( Object object ) {
		if( !( object instanceof Polynomial ) ) return false;
		return Arrays.equals( coefficients, ( (Polynomial)object ).coefficients );
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		boolean start = true;
		for( int index = coefficients.length - 1; index >= 0; index-- ) {
			double coefficient = coefficients[index];

			// Add the sign of the coefficient.
			if( coefficient == 0 ) {
				continue;
			} else if( coefficient < 0 ) {
				if( !start ) builder.append( " " );
				builder.append( "-" );
			} else if( !start && coefficient > 0 ) {
				builder.append( " " );
				builder.append( "+" );
			}

			// Add the coefficient.
			if( !start ) builder.append( " " );
			builder.append( Math.abs( coefficient ) );

			// Add the power.
			if( index > 0 ) {
				if( index == 1 ) {
					builder.append( "x" );
				} else {
					builder.append( "x^" );
					builder.append( index );
				}
			}

			start = false;
		}

		return builder.toString();
	}

	/**
	 * Remove unnecessary zero coefficients.
	 *
	 * @return
	 */
	private void simplify() {
		int length = 0;

		// Start checking at the highest degree coefficient and go backwards.
		for( int index = coefficients.length; index > 0; index-- ) {
			if( Math.abs( coefficients[index - 1] ) > TOLERANCE ) {
				length = index;
				break;
			}
		}

		if( length == coefficients.length ) return;
		if( length == 0 ) length = 1;

		// Make a new array of the correct length and copy the coefficients.
		double[] simple = new double[length];
		System.arraycopy( coefficients, 0, simple, 0, length );
		this.coefficients = simple;
	}

	private double[] getLinearRoot() {
		double a = this.coefficients[1];
		return a == 0 ? new double[0] : new double[] { -this.coefficients[0] / a };
	}

	/**
	 * Determine quadric roots.
	 *
	 * @return
	 */
	// Derived from: http://read.pudn.com/downloads21/sourcecode/graph/71499/gems/Roots3And4.c__.htm
	private double[] getQuadricRoots() {
		// Normal form: x^2 + px + q = 0
		double p = coefficients[1] / ( 2 * coefficients[2] );
		double q = coefficients[0] / coefficients[2];
		double d = p * p - q;

		if( isSmallEnough( d ) ) {
			return new double[] { -p };
		} else if( d < 0 ) {
			return new double[0];
		} else if( d > 0 ) {
			double sqrtD = Math.sqrt( d );
			return new double[] { -sqrtD - p, sqrtD + p };
		}

		return new double[0];
	}

	/**
	 * Determine cubic roots.
	 *
	 * @return
	 */
	// Derived from: http://read.pudn.com/downloads21/sourcecode/graph/71499/gems/Roots3And4.c__.htm
	private double[] getCubicRoots() {
		// Normal form: x^3 + ax^2 + bx + c = 0
		double a = coefficients[2] / coefficients[3];
		double b = coefficients[1] / coefficients[3];
		double c = coefficients[0] / coefficients[3];

		// Substitute x = y - a/3 to eliminate quadric term: x^3 +px + q = 0
		double sq_A = a * a;
		double p = ONE_THIRD * ( -ONE_THIRD * sq_A + b );
		double q = 0.5 * ( 2.0 / 27.0 * a * sq_A - ONE_THIRD * a * b + c );

		// Use Cardano's formula
		double ppp = p * p * p;
		double d = q * q + ppp;

		double[] result = new double[0];

		if( isSmallEnough( d ) ) {
			if( isSmallEnough( q ) ) {
				// One triple solution
				result = new double[] { 0 };
			} else {
				// One single and one double solution
				double u = Math.cbrt( -q );
				result = new double[] { 2 * u, -u };
			}
		} else if( d < 0 ) {
			// Three real solutions
			double phi = ONE_THIRD * Math.acos( -q / Math.sqrt( -ppp ) );
			double t = 2 * Math.sqrt( -p );
			result = new double[] { -t * Math.cos( phi - PI_OVER_THREE ), -t * Math.cos( phi + PI_OVER_THREE ), t * Math.cos( phi ) };
		} else {
			// One real solution
			double sqrt_D = Math.sqrt( d );
			double u = Math.cbrt( sqrt_D - q );
			double v = -Math.cbrt( sqrt_D + q );
			result = new double[] { u + v };
		}

		// Resubstitute
		double sub = ONE_THIRD * a;
		for( int i = 0; i < result.length; ++i ) {
			result[i] -= sub;
		}

		return result;
	}

	/**
	 * Determine quartic roots.
	 *
	 * @return
	 */
	// Derived from: http://read.pudn.com/downloads21/sourcecode/graph/71499/gems/Roots3And4.c__.htm
	private double[] getQuarticRoots() {
		// Normal form: x^4 + ax^3 + bx^2 + cx + d = 0

		double a = coefficients[3] / coefficients[4];
		double b = coefficients[2] / coefficients[4];
		double c = coefficients[1] / coefficients[4];
		double d = coefficients[0] / coefficients[4];

		// Substitute x = y - a/4 to eliminate cubic term: x^4 + px^2 + qx + r = 0

		double aa = a * a;
		double p = -0.375 * aa + b;
		double q = 0.125 * aa * a - 0.5 * a * b + c;
		double r = -0.01171875 * aa * aa + 0.0625 * aa * b - 0.25 * a * c + d;

		double[] result = new double[0];

		if( isSmallEnough( r ) ) {
			// No absolute term: y(y^3 + py + q) = 0
			result = new Polynomial( 1, 0, p, q ).getCubicRoots();
		} else {
			// Solve the resolvent cubic ...
			double[] roots = new Polynomial( 1, -0.5 * p, -r, 0.5 * r * p - 0.125 * q * q ).getCubicRoots();

			// ... and take the one real solution ...
			double z = roots[0];

			// ... to build two quadric equations
			double u = z * z - r;
			double v = 2 * z - p;

			if( isSmallEnough( u ) ) {
				u = 0;
			} else if( u > 0 ) {
				u = Math.sqrt( u );
			} else {
				return result;
			}

			if( isSmallEnough( v ) ) {
				v = 0;
			} else if( v > 0 ) {
				v = Math.sqrt( v );
			} else {
				return result;
			}

			double[] aroots = new Polynomial( 1, q < 0 ? -v : v, z - u ).getQuadricRoots();
			double[] broots = new Polynomial( 1, q < 0 ? v : -v, z + u ).getQuadricRoots();

			result = new double[aroots.length + broots.length];
			System.arraycopy( aroots, 0, result, 0, aroots.length );
			System.arraycopy( broots, 0, result, aroots.length, broots.length );
		}

		// Resubstitute
		double sub = 1.0 / 4 * a;
		for( int i = 0; i < result.length; ++i ) {
			result[i] -= sub;
		}

		return result;
	}

	private final boolean isSmallEnough( double value ) {
		return Math.abs( value ) < TOLERANCE;
	}

}
