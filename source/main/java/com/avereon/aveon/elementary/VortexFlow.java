package com.avereon.aveon.elementary;

import com.avereon.aveon.ElementaryFlow;
import com.avereon.curve.math.Constants;
import com.avereon.curve.math.Vector;

public class VortexFlow implements ElementaryFlow {

	private final double x;

	private final double y;

	private final double q;

	public VortexFlow( double x, double y, double q ) {
		this.x = x;
		this.y = y;
		this.q = q;
	}

	@Override
	public double getPotential( double fx, double fy ) {
		double theta = Math.atan2( fy - y, fx - x );
		if( theta < 0 ) theta += Constants.TWO_PI;
		return -q * theta / (Constants.TWO_PI);
	}

	@Override
	public double getStream( double fx, double fy ) {
		double radius = Vector.distance( x, y, fx, fy );
		return q * Math.log( radius ) / (Constants.TWO_PI);
	}

	@Override
	public double[] getVelocity( double xf, double yf ) {
		double r2, xr, yr;
		double[] velocity = new double[]{ Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY };

		xr = xf - x;
		yr = yf - y;
		r2 = xr * xr + yr * yr;

		if( r2 > 0.0 ) {
			velocity[ 0 ] = -q / r2 * yr;
			velocity[ 1 ] = q / r2 * xr;
		}

		return velocity;
	}
}
