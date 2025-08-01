package com.avereon.aveon.elementary;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class UniformFlowTest {

	@ParameterizedTest
	@MethodSource
	void stream( double speed, double x, double y, double expected) {
		// given
		UniformFlow flow = new UniformFlow( speed );

		// then
		assertThat( flow.getStream( x, y ) ).isEqualTo( expected );
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
	void potential( double speed, double fx, double fy, double expected) {
		// given
		UniformFlow flow = new UniformFlow( speed );

		// then
		assertThat( flow.getPotential( fx, fy ) ).isEqualTo( expected );
	}

	static Stream<Arguments> potential() {
		List<Arguments> arguments = new ArrayList<>();

		for( int fx = -2; fx <= 2; fx++ ) {
			for( int fy = -2; fy <= 2; fy++ ) {
				arguments.add( Arguments.of( 10, fx, fy, fx ));
			}
		}

		return arguments.stream();
	}

}
