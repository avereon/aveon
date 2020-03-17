package com.avereon.aveon;

import javafx.geometry.Point2D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class AirfoilCodedTest {

	private AirfoilCodec codec;

	@BeforeEach
	void setup() {
		codec = new AirfoilCodec();
	}

	@Test
	void testLoadSelig() {
		String content = "";

		//codec.loadSelig( asset, lines);
	}

	@Test
	void testParsePoint() {
		assertThat( codec.parsePoint( "0 0" ), is( Point2D.ZERO ) );
		assertThat( codec.parsePoint( "0.25 0.01" ), is( new Point2D( 0.25, 0.01 ) ) );
	}

}
