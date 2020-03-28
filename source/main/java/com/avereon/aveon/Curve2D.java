package com.avereon.aveon;

import javafx.geometry.Point2D;

import java.util.List;

public class Curve2D {

	private Point2D a;

	private Point2D b;

	private Point2D c;

	private Point2D d;

	public Curve2D( Point2D a, Point2D b, Point2D c, Point2D d ) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;

		Curve2D cv = new Curve2D( a,b,c,d );
	}

	/**
	 * Get the list of points that estimate the curve with a specific number of
	 * line segments.
	 *
	 * @param count The number of line segments to produce
	 * @return The points that define the curve as line segements
	 */
	public List<Point2D> toPoints( int count ) {
		return List.of();
	}

	/**
	 * Get the list of points that estimate the curve as line segments no larger
	 * than size.
	 *
	 * @param size The largest permitted line segment length
	 * @return The points that estimate the curve as line segments
	 */
	public List<Point2D> toPoints( double size ) {
		return List.of();
	}

}
