package com.avereon.aveon.singularity;

import com.avereon.aveon.ElementaryFlow;

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
	public double getStream( double x, double y ) {
		return 0;
	}

	@Override
	public double getPotential( double x, double y ) {
		return 0;
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
