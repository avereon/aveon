package com.avereon.aveon.elementary;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class VortexFlowTest {

	@ParameterizedTest
	@MethodSource( "flow" )
	void potential( double x, double y, double q, double fx, double fy, double p, double s, double[] expected ) {
		// given
		VortexFlow flow = new VortexFlow( x, y, q );

		// then
		assertThat( flow.getPotential( fx, fy ) ).isEqualTo( p );
	}

	@ParameterizedTest
	@MethodSource( "flow" )
	void stream( double x, double y, double q, double fx, double fy, double p, double s, double[] expected ) {
		// given
		VortexFlow flow = new VortexFlow( x, y, q );

		// then
		assertThat( flow.getStream( fx, fy ) ).isEqualTo( s );
	}

	@ParameterizedTest
	@MethodSource( "flow" )
	void velocity( double x, double y, double q, double fx, double fy, double p, double s, double[] expected ) {
		// given
		VortexFlow flow = new VortexFlow( x, y, q );

		// thenj
		assertThat( flow.getVelocity( fx, fy ) ).isEqualTo( expected );
	}

	static Stream<Arguments> flow() {
		List<Arguments> arguments = new ArrayList<>();

		double x = 0;
		double y = 0;
		double q = 1;

		arguments.add( Arguments.of( x, y, q, -2, -2, -0.625, 0.16547670011448873, new double[]{ 0.25, -0.25 } ) );
		arguments.add( Arguments.of( x, y, q, -2, -1, -0.5737918088252166, 0.12807499968169406, new double[]{ 0.2, -0.4 } ) );
		arguments.add( Arguments.of( x, y, q, -2, 0, -0.5, 0.1103178000763258, new double[]{ -0.0, -0.5 } ) );
		arguments.add( Arguments.of( x, y, q, -2, 1, -0.42620819117478337, 0.12807499968169406, new double[]{ -0.2, -0.4 } ) );
		arguments.add( Arguments.of( x, y, q, -2, 2, -0.375, 0.16547670011448873, new double[]{ -0.25, -0.25 } ) );

		arguments.add( Arguments.of( x, y, q, -1, -2, -0.6762081911747834, 0.12807499968169406, new double[]{ 0.4, -0.2 } ) );
		arguments.add( Arguments.of( x, y, q, -1, -1, -0.625, 0.05515890003816291, new double[]{ 0.5, -0.5 } ) );
		arguments.add( Arguments.of( x, y, q, -1, 0, -0.5, 0, new double[]{ -0.0, -1.0 } ) );
		arguments.add( Arguments.of( x, y, q, -1, 1, -0.375, 0.05515890003816291, new double[]{ -0.5, -0.5 } ) );
		arguments.add( Arguments.of( x, y, q, -1, 2, -0.32379180882521663, 0.12807499968169406, new double[]{ -0.4, -0.2 } ) );

		arguments.add( Arguments.of( x, y, q, 0, -2, -0.75, 0.1103178000763258, new double[]{ 0.5, 0.0 } ) );
		arguments.add( Arguments.of( x, y, q, 0, -1, -0.75, 0, new double[]{ 1.0, 0.0 } ) );
		arguments.add( Arguments.of( x, y, q, 0, 0, 0, Double.NEGATIVE_INFINITY, new double[]{ Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY } ) );
		arguments.add( Arguments.of( x, y, q, 0, 1, -0.25, 0, new double[]{ -1.0, 0.0 } ) );
		arguments.add( Arguments.of( x, y, q, 0, 2, -0.25, 0.1103178000763258, new double[]{ -0.5, 0.0 } ) );

		arguments.add( Arguments.of( x, y, q, 1, -2, -0.8237918088252166, 0.12807499968169406, new double[]{ 0.4, 0.2 } ) );
		arguments.add( Arguments.of( x, y, q, 1, -1, -0.875, 0.05515890003816291, new double[]{ 0.5, 0.5 } ) );
		arguments.add( Arguments.of( x, y, q, 1, 0, 0, 0, new double[]{ -0.0, 1.0 } ) );
		arguments.add( Arguments.of( x, y, q, 1, 1, -0.125, 0.05515890003816291, new double[]{ -0.5, 0.5 } ) );
		arguments.add( Arguments.of( x, y, q, 1, 2, -0.17620819117478337, 0.12807499968169406, new double[]{ -0.4, 0.2 } ) );

		arguments.add( Arguments.of( x, y, q, 2, -2, -0.875, 0.16547670011448873, new double[]{ 0.25, 0.25 } ) );
		arguments.add( Arguments.of( x, y, q, 2, -1, -0.9262081911747834, 0.12807499968169406, new double[]{ 0.2, 0.4 } ) );
		arguments.add( Arguments.of( x, y, q, 2, 0, 0, 0.1103178000763258, new double[]{ -0.0, 0.5 } ) );
		arguments.add( Arguments.of( x, y, q, 2, 1, -0.07379180882521663, 0.12807499968169406, new double[]{ -0.2, 0.4 } ) );
		arguments.add( Arguments.of( x, y, q, 2, 2, -0.125, 0.16547670011448873, new double[]{ -0.25, 0.25 } ) );

		return arguments.stream();
	}

}
