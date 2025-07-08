package com.avereon.aveon.elementary;

import com.avereon.aveon.ElementaryFlow;
import com.avereon.curve.math.Geometry;
import com.avereon.curve.math.Vector;

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
	public double getPotential( double fx, double fy ) {
		double dx = fx - x;
		double dy = fx - y;
		double radius = Vector.magnitude( dx, dy );
		double theta = Geometry.theta( dx, dy ) + a;
		return q * Math.cos( theta ) / radius;
	}

	@Override
	public double getStream( double fx, double fy ) {
		double dx = fx - x;
		double dy = fx - y;
		double radius = Vector.magnitude( dx, dy );
		double theta = Geometry.theta( dx, dy ) + a;
		return -q * Math.sin( theta ) / radius;
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
