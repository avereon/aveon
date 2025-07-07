package com.avereon.aveon;

public interface ElementaryFlow {

	double getStream(double x, double y);

	double getPotential( double x, double y);

	double[] getVelocity(double x, double y);

//	double getLift(double x, double y);
//
//	double getLiftingMoment(double x, double y);

}
