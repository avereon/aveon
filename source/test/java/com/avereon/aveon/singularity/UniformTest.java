package com.avereon.aveon.singularity;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class UniformTest {

	@ParameterizedTest
	@MethodSource
	void stream( double speed, double x, double y, double expected) {
		// given
		Uniform uniform = new Uniform( speed );

		// then
		assertThat( uniform.getStream( x, y ) ).isEqualTo( expected );
	}

	static Stream<Arguments> stream() {
		List<Arguments> arguments = new ArrayList<>();

		for( int x = -2; x <= 2; x++ ) {
			for( int y = -2; y <= 2; y++ ) {
				arguments.add( Arguments.of( 10, x, y, y ));
			}
		}

		return arguments.stream();
	}

	@ParameterizedTest
	@MethodSource
	void potential(double speed, double x, double y, double expected) {
		// given
		Uniform uniform = new Uniform( speed );

		// then
		assertThat( uniform.getPotential( x, y ) ).isEqualTo( expected );
	}

	static Stream<Arguments> potential() {
		List<Arguments> arguments = new ArrayList<>();

		for( int x = -2; x <= 2; x++ ) {
			for( int y = -2; y <= 2; y++ ) {
				arguments.add( Arguments.of( 10, x, y, x ));
			}
		}

		return arguments.stream();
	}

}
