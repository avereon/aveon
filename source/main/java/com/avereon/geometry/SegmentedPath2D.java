package com.avereon.geometry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SegmentedPath2D {

	private static final int PERCENT_INDEX = 0;

	private static final int HEAD_WEIGHT_INDEX = 1;

	private static final int TAIL_WEIGHT_INDEX = 2;

	public final List<Point2D> points;

	public final List<Line2D> segments;

	public final int pointCount;

	public final int segmentCount;

	public final double length;

	public final double[] percent;

	public SegmentedPath2D( List<Point2D> points ) {
		this.points = Collections.unmodifiableList( points );
		this.pointCount = points.size();

		// Path length
		length = CfdGeometry.calcPathLength( points );

		// Analyze path curve
		Point2D prior = null;
		Point2D point;
		List<Line2D> lines = new ArrayList<>();
		percent = new double[ pointCount ];
		for( int index = 0; index < pointCount; index++ ) {
			percent[ index ] = CfdGeometry.calcPathLength( points.subList( 0, index + 1 ) ) / length;
			point = points.get( index );
			if( index > 0 ) lines.add( new Line2D( prior, point ));
			prior = point;
		}
		segments = Collections.unmodifiableList( lines );
		this.segmentCount = segments.size();
	}

	public static SegmentedPath2D of() {
		return new SegmentedPath2D( List.of() );
	}

	public static SegmentedPath2D of( List<Point2D> points ) {
		return new SegmentedPath2D( points );
	}

	public final List<Point2D> getPoints() {
		return points;
	}

	public final List<Line2D> getSegments() {
		return segments;
	}

	public final double getLength() {
		return length;
	}

	public final int getPointCount() {
		return pointCount;
	}

	public final int getSegmentCount() {
		return segmentCount;
	}

	public final double getPercentDistance( int index ) {
		return percent[ index ];
	}

	public Point2D getPoint( int index ) {
		return points.get( index );
	}

	public Line2D getSegment( int index ) {
		return new Line2D( points.get( index ), points.get( index + 1 ) );
	}

}
