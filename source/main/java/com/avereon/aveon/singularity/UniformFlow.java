package com.avereon.aveon.singularity;

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
	public double getStream(double x, double y) {
		return y;
	}

	@Override
	public double getPotential( double x, double y) {
		return x;
	}

	@Override
	public double[] getVelocity(double x, double y) {
		double[] velocity = new double[2];
		velocity[0] = speed;
		velocity[1] = 0;
		return velocity;
	}

}
