package com.avereon.geometry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SegmentedPath2DTest {

	private SegmentedPath2D path;

	@BeforeEach
	void setup() {
		path = new SegmentedPath2D( List.of( new Point2D( 0, 0 ), new Point2D( 1, 0 ), new Point2D( 1, 2 ), new Point2D( -2, 2 ), new Point2D( -2, -2 ) ) );
	}

	@Test
	void testGetCount() {
		assertThat( path.getPointCount(), is( 5 ) );
	}

	@Test
	void testGetLength() {
		assertThat( path.getLength(), is( 10.0 ) );
	}

	@Test
	void testGetPercent() {
		assertThat( path.getPercentDistance( 0 ), is( 0.0 ) );
		assertThat( path.getPercentDistance( 1 ), is( 0.1 ) );
		assertThat( path.getPercentDistance( 2 ), is( 0.3 ) );
		assertThat( path.getPercentDistance( 3 ), is( 0.6 ) );
		assertThat( path.getPercentDistance( 4 ), is( 1.0 ) );
	}

}
