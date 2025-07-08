package com.avereon.aveon.elementary;

import com.avereon.curve.math.Constants;
import com.avereon.curve.math.Geometry;
import com.avereon.curve.math.Point;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class SourceFlowTest {

	@ParameterizedTest
	@MethodSource( "flow" )
	void potential( double x, double y, double q, double fx, double fy, double p, double s, double[] v ) {
		// given
		SourceFlow flow = new SourceFlow( x, y, q );

		// then
		assertThat( flow.getPotential( fx, fy ) ).isEqualTo( p );
	}

	@ParameterizedTest
	@MethodSource( "flow" )
	void stream( double x, double y, double q, double fx, double fy, double p, double s, double[] v ) {
		// given
		SourceFlow flow = new SourceFlow( x, y, q );

		// then
		assertThat( flow.getStream( fx, fy ) ).isEqualTo( s );
	}

	@ParameterizedTest
	@MethodSource( "flow" )
	void velocity( double x, double y, double q, double fx, double fy, double p, double s, double[] v ) {
		// given
		SourceFlow flow = new SourceFlow( x, y, q );

		// then
		assertThat( flow.getVelocity( fx, fy ) ).isEqualTo( v );
	}

	static Stream<Arguments> flow() {
		List<Arguments> arguments = new ArrayList<>();

		double x = 0;
		double y = 0;
		double q = 1;

		arguments.add( Arguments.of( x, y, q, -2, -2, 0.16547670011448873, 0.625, new double[]{ -0.25, -0.25 } ) );
		arguments.add( Arguments.of( x, y, q, -2, -1, 0.12807499968169406, 0.5737918088252166, new double[]{ -0.4, -0.2 } ) );
		arguments.add( Arguments.of( x, y, q, -2, 0, 0.1103178000763258, 0.5, new double[]{ -0.5, 0.0 } ) );
		arguments.add( Arguments.of( x, y, q, -2, 1, 0.12807499968169406, 0.42620819117478337, new double[]{ -0.4, 0.2 } ) );
		arguments.add( Arguments.of( x, y, q, -2, 2, 0.16547670011448873, 0.375, new double[]{ -0.25, 0.25 } ) );

		arguments.add( Arguments.of( x, y, q, -1, -2, 0.12807499968169406, 0.6762081911747834, new double[]{ -0.2, -0.4 } ) );
		arguments.add( Arguments.of( x, y, q, -1, -1, 0.05515890003816291, 0.625, new double[]{ -0.5, -0.5 } ) );
		arguments.add( Arguments.of( x, y, q, -1, 0, 0, 0.5, new double[]{ -1.0, 0.0 } ) );
		arguments.add( Arguments.of( x, y, q, -1, 1, 0.05515890003816291, 0.375, new double[]{ -0.5, 0.5 } ) );
		arguments.add( Arguments.of( x, y, q, -1, 2, 0.12807499968169406, 0.32379180882521663, new double[]{ -0.2, 0.4 } ) );

		arguments.add( Arguments.of( x, y, q, 0, -2, 0.1103178000763258, 0.75, new double[]{ 0.0, -0.5 } ) );
		arguments.add( Arguments.of( x, y, q, 0, -1, 0, 0.75, new double[]{ 0.0, -1.0 } ) );
		arguments.add( Arguments.of( x, y, q, 0, 0, Double.NEGATIVE_INFINITY, 0, new double[]{ Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY } ) );
		arguments.add( Arguments.of( x, y, q, 0, 1, 0, 0.25, new double[]{ 0.0, 1.0 } ) );
		arguments.add( Arguments.of( x, y, q, 0, 2, 0.1103178000763258, 0.25, new double[]{ 0.0, 0.5 } ) );

		arguments.add( Arguments.of( x, y, q, 1, -2, 0.12807499968169406, 0.8237918088252166, new double[]{ 0.2, -0.4 } ) );
		arguments.add( Arguments.of( x, y, q, 1, -1, 0.05515890003816291, 0.875, new double[]{ 0.5, -0.5 } ) );
		arguments.add( Arguments.of( x, y, q, 1, 0, 0, 0, new double[]{ 1.0, 0.0 } ) );
		arguments.add( Arguments.of( x, y, q, 1, 1, 0.05515890003816291, 0.125, new double[]{ 0.5, 0.5 } ) );
		arguments.add( Arguments.of( x, y, q, 1, 2, 0.12807499968169406, 0.17620819117478337, new double[]{ 0.2, 0.4 } ) );

		arguments.add( Arguments.of( x, y, q, 2, -2, 0.16547670011448873, 0.875, new double[]{ 0.25, -0.25 } ) );
		arguments.add( Arguments.of( x, y, q, 2, -1, 0.12807499968169406, 0.9262081911747834, new double[]{ 0.4, -0.2 } ) );
		arguments.add( Arguments.of( x, y, q, 2, 0, 0.1103178000763258, 0, new double[]{ 0.5, 0.0 } ) );
		arguments.add( Arguments.of( x, y, q, 2, 1, 0.12807499968169406, 0.07379180882521663, new double[]{ 0.4, 0.2 } ) );
		arguments.add( Arguments.of( x, y, q, 2, 2, 0.16547670011448873, 0.125, new double[]{ 0.25, 0.25 } ) );

		return arguments.stream();
	}

	static double p( double x, double y, double q, double fx, double fy ) {
		double radius = Geometry.length( Point.of(x,y), Point.of(fx,fy) );
		return q * Math.log( radius ) / Constants.TWO_PI;
	}

}
