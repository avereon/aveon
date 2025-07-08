package com.avereon.aveon.elementary;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class SourceSheetFlowTest {

	@ParameterizedTest
	@MethodSource
	void velocity( double x1, double y1, double x2, double y2, double q, double fx, double fy, double p, double s, double[] expected ) {
		// given
		SourceSheetFlow flow = new SourceSheetFlow( x1, y1, x2, y2, q );

		// then
		assertThat( flow.getVelocity( fx, fy ) ).isEqualTo( expected );
	}

	static Stream<Arguments> velocity() {
		List<Arguments> arguments = new ArrayList<>();

		double x1 = -1;
		double y1 = 0;
		double x2 = 1;
		double y2 = 0;
		double q = 1;

		// This is the right idea, but most of the test values are incorrect
		//		arguments.add( Arguments.of( x1,y1,x2,y2, q, -2, -2,0,0,new double[]{ 0.25, -0.25 } ) );
		//		arguments.add( Arguments.of( x1,y1,x2,y2, q, -2, -1,0,0,new double[]{ 0.2, -0.4 } ) );
		//		arguments.add( Arguments.of( x1,y1,x2,y2, q, -2, 0,0,0,new double[]{ -0.0, -0.5 } ) );
		//		arguments.add( Arguments.of( x1,y1,x2,y2, q, -2, 1,0,0,new double[]{ -0.2, -0.4 } ) );
		//		arguments.add( Arguments.of( x1,y1,x2,y2, q, -2, 2,0,0,new double[]{ -0.25, -0.25 } ) );
		//
		//		arguments.add( Arguments.of( x1,y1,x2,y2, q, -1, -2,0,0,new double[]{ 0.4, -0.2 } ) );
		//		arguments.add( Arguments.of( x1,y1,x2,y2, q, -1, -1,0,0,new double[]{ 0.5, -0.5 } ) );
		//		arguments.add( Arguments.of( x1,y1,x2,y2, q, -1, 0,0,0,new double[]{ -0.0, -1.0 } ) );
		//		arguments.add( Arguments.of( x1,y1,x2,y2, q, -1, 1,0,0,new double[]{ -0.5, -0.5 } ) );
		//		arguments.add( Arguments.of( x1,y1,x2,y2, q, -1, 2,0,0,new double[]{ -0.4, -0.2 } ) );
		//
		//		arguments.add( Arguments.of( x1,y1,x2,y2, q, 0, -2,0,0,new double[]{ 0.5, 0.0 } ) );
		arguments.add( Arguments.of( x1, y1, x2, y2, q, -1, 0, 0, 0, new double[]{ Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY } ) );
		//		arguments.add( Arguments.of( x1,y1,x2,y2, q, 0, 0,0,0,new double[]{ Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY } ) );
		arguments.add( Arguments.of( x1, y1, x2, y2, q, 1, 0, 0, 0, new double[]{ Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY } ) );
		//		arguments.add( Arguments.of( x1,y1,x2,y2, q, 0, 2,0,0,new double[]{ -0.5, 0.0 } ) );
		//
		//		arguments.add( Arguments.of( x1,y1,x2,y2, q, 1, -2,0,0,new double[]{ 0.4, 0.2 } ) );
		//		arguments.add( Arguments.of( x1,y1,x2,y2, q, 1, -1,0,0,new double[]{ 0.5, 0.5 } ) );
		//		arguments.add( Arguments.of( x1,y1,x2,y2, q, 1, 0,0,0,new double[]{ -0.0, 1.0 } ) );
		//		arguments.add( Arguments.of( x1,y1,x2,y2, q, 1, 1,0,0,new double[]{ -0.5, 0.5 } ) );
		//		arguments.add( Arguments.of( x1,y1,x2,y2, q, 1, 2,0,0,new double[]{ -0.4, 0.2 } ) );
		//
		//		arguments.add( Arguments.of( x1,y1,x2,y2, q, 2, -2,0,0,new double[]{ 0.25, 0.25 } ) );
		//		arguments.add( Arguments.of( x1,y1,x2,y2, q, 2, -1,0,0,new double[]{ 0.2, 0.4 } ) );
		//		arguments.add( Arguments.of( x1,y1,x2,y2, q, 2, 0,0,0,new double[]{ -0.0, 0.5 } ) );
		//		arguments.add( Arguments.of( x1,y1,x2,y2, q, 2, 1,0,0,new double[]{ -0.2, 0.4 } ) );
		//		arguments.add( Arguments.of( x1,y1,x2,y2, q, 2, 2,0,0,new double[]{ -0.25, 0.25 } ) );

		return arguments.stream();
	}

}
