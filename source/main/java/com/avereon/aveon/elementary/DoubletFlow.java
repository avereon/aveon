package com.avereon.aveon.elementary;

import com.avereon.aveon.ElementaryFlow;

public class DoubletFlow implements ElementaryFlow {

	private final double x;

	private final double y;

	private final double a;

	private final double q;

	public DoubletFlow( double x, double y, double a, double q ) {
		this.x = x;
		this.y = y;
		this.a = a;
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
			velocity[ 0 ] = -q / r2 / r2 * ((xr * xr - yr * yr) * Math.cos( a ) + 2 * xr * yr * Math.sin( a ));
			velocity[ 1 ] = q / r2 / r2 * (-(xr * xr - yr * yr) * Math.sin( a ) + 2 * xr * yr * Math.cos( a ));
		}

		return velocity;
	}

}
