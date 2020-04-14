package com.avereon.geometry;

import java.util.List;

public class SegmentedPath2D {

	private static final int PERCENT_INDEX = 0;

	private static final int HEAD_WEIGHT_INDEX = 1;

	private static final int TAIL_WEIGHT_INDEX = 2;

	public final List<Point2D> points;

	public final int pointCount;

	public final int segmentCount;

	public final double length;

	public final double[] percent;

	public SegmentedPath2D( List<Point2D> points ) {
		this.points = points;
		this.pointCount = points.size();
		this.segmentCount = pointCount - 1;

		// Path length
		length = Geometry2D.calcPathLength( points );

		// Calculate path curve
		percent = new double[ pointCount ];
		for( int index = 0; index < pointCount; index++ ) {
			percent[ index ] = Geometry2D.calcPathLength( points.subList( 0, index + 1 ) ) / length;
		}
	}

	public static SegmentedPath2D of( List<Point2D> points ) {
		return new SegmentedPath2D( points );
	}

	public final List<Point2D> getPoints() {
		return points;
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
