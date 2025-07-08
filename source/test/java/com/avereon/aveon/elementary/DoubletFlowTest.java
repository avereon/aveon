package com.avereon.aveon.elementary;

import org.assertj.core.data.Offset;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class DoubletFlowTest {

	@ParameterizedTest
	@MethodSource( "flow" )
	void potential( double x, double y, double a, double q, double fx, double fy, double p, double s, double[] expected ) {
		// given
		DoubletFlow flow = new DoubletFlow( x, y, a, q );

		// then
		assertThat( flow.getPotential( fx, fy ) ).isEqualTo( p, Offset.offset( 1e-10) );
	}

	@ParameterizedTest
	@MethodSource( "flow" )
	void stream( double x, double y, double a, double q, double fx, double fy, double p, double s, double[] expected ) {
		// given
		DoubletFlow flow = new DoubletFlow( x, y, a, q );

		// then
		assertThat( flow.getStream( fx, fy ) ).isEqualTo( s, Offset.offset( 1e-10) );
	}

	@ParameterizedTest
	@MethodSource( "flow" )
	void velocity( double x, double y, double a, double q, double fx, double fy, double p, double s, double[] expected ) {
		// given
		DoubletFlow flow = new DoubletFlow( x, y, a, q );

		// then
		assertThat( flow.getVelocity( fx, fy ) ).isEqualTo( expected );
	}

	static Stream<Arguments> flow() {
		List<Arguments> arguments = new ArrayList<>();

		double x = 0;
		double y = 0;
		double a = 0;
		double q = 1;

		arguments.add( Arguments.of( x, y, a, q, -2, -2, -0.25, 0.25, new double[]{ -0.0, 0.125 } ) );
		arguments.add( Arguments.of( x, y, a, q, -2, -1, -0.25, 0.25, new double[]{ -0.12, 0.16 } ) );
		arguments.add( Arguments.of( x, y, a, q, -2, 0, -0.25, 0.25, new double[]{ -0.25, -0.0 } ) );
		arguments.add( Arguments.of( x, y, a, q, -2, 1, -0.25, 0.25, new double[]{ -0.12, -0.16 } ) );
		arguments.add( Arguments.of( x, y, a, q, -2, 2, -0.25, 0.25, new double[]{ -0.0, -0.125 } ) );

		arguments.add( Arguments.of( x, y, a, q, -1, -2, -0.5, 0.5, new double[]{ 0.12, 0.16 } ) );
		arguments.add( Arguments.of( x, y, a, q, -1, -1, -0.5, 0.5, new double[]{ -0.0, 0.5 } ) );
		arguments.add( Arguments.of( x, y, a, q, -1, 0, -0.5, 0.5, new double[]{ -1.0, -0.0 } ) );
		arguments.add( Arguments.of( x, y, a, q, -1, 1, -0.5, 0.5, new double[]{ -0.0, -0.5 } ) );
		arguments.add( Arguments.of( x, y, a, q, -1, 2, -0.5, 0.5, new double[]{ 0.12, -0.16 } ) );

		arguments.add( Arguments.of( x, y, a, q, 0, -2, Double.POSITIVE_INFINITY, Double.NaN, new double[]{ 0.25, 0.0 } ) );
		arguments.add( Arguments.of( x, y, a, q, 0, -1, Double.POSITIVE_INFINITY, Double.NaN, new double[]{ 1.0, 0.0 } ) );
		arguments.add( Arguments.of( x, y, a, q, 0, 0, Double.POSITIVE_INFINITY, Double.NaN, new double[]{ Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY } ) );
		arguments.add( Arguments.of( x, y, a, q, 0, 1, Double.POSITIVE_INFINITY, Double.NaN, new double[]{ 1.0, 0.0 } ) );
		arguments.add( Arguments.of( x, y, a, q, 0, 2, Double.POSITIVE_INFINITY, Double.NaN, new double[]{ 0.25, 0.0 } ) );

		arguments.add( Arguments.of( x, y, a, q, 1, -2, 0.5, -0.5, new double[]{ 0.12, -0.16 } ) );
		arguments.add( Arguments.of( x, y, a, q, 1, -1, 0.5, -0.5, new double[]{ -0.0, -0.5 } ) );
		arguments.add( Arguments.of( x, y, a, q, 1, 0, 0.5, -0.5, new double[]{ -1.0, 0.0 } ) );
		arguments.add( Arguments.of( x, y, a, q, 1, 1, 0.5, -0.5, new double[]{ -0.0, 0.5 } ) );
		arguments.add( Arguments.of( x, y, a, q, 1, 2, 0.5, -0.5, new double[]{ 0.12, 0.16 } ) );

		arguments.add( Arguments.of( x, y, a, q, 2, -2, 0.25, -0.25, new double[]{ -0.0, -0.125 } ) );
		arguments.add( Arguments.of( x, y, a, q, 2, -1, 0.25, -0.25, new double[]{ -0.12, -0.16 } ) );
		arguments.add( Arguments.of( x, y, a, q, 2, 0, 0.25, -0.25, new double[]{ -0.25, 0.0 } ) );
		arguments.add( Arguments.of( x, y, a, q, 2, 1, 0.25, -0.25, new double[]{ -0.12, 0.16 } ) );
		arguments.add( Arguments.of( x, y, a, q, 2, 2, 0.25, -0.25, new double[]{ -0.0, 0.125 } ) );

		return arguments.stream();
	}

}
