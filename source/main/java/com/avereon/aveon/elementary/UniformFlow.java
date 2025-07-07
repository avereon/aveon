package com.avereon.aveon.elementary;

import com.avereon.aveon.ElementaryFlow;

public class UniformFlow implements ElementaryFlow {

	private final double speed;

	public UniformFlow() {
		this( 1.0 );
	}

	public UniformFlow( double speed ) {
		this.speed = speed;
	}

	@Override
	public double getStream( double x, double y ) {
		return y;
	}

	@Override
	public double getPotential( double x, double y ) {
		return x;
	}

	@Override
	public double[] getVelocity( double x, double y ) {
		return new double[]{ speed, 0 };
	}

}
