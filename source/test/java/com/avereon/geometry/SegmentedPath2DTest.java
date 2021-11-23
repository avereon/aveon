package com.avereon.geometry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SegmentedPath2DTest {

	private SegmentedPath2D path;

	@BeforeEach
	void setup() {
		path = new SegmentedPath2D( List.of( new Point2D( 0, 0 ), new Point2D( 1, 0 ), new Point2D( 1, 2 ), new Point2D( -2, 2 ), new Point2D( -2, -2 ) ) );
	}

	@Test
	void testGetCount() {
		assertThat( path.getPointCount() ).isEqualTo( 5 );
	}

	@Test
	void testGetLength() {
		assertThat( path.getLength() ).isEqualTo( 10.0 );
	}

	@Test
	void testGetPercent() {
		assertThat( path.getPercentDistance( 0 ) ).isEqualTo( 0.0 );
		assertThat( path.getPercentDistance( 1 ) ).isEqualTo( 0.1 );
		assertThat( path.getPercentDistance( 2 ) ).isEqualTo( 0.3 );
		assertThat( path.getPercentDistance( 3 ) ).isEqualTo( 0.6 );
		assertThat( path.getPercentDistance( 4 ) ).isEqualTo( 1.0 );
	}

}
