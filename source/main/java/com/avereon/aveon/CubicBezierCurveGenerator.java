package com.avereon.aveon;

import javafx.geometry.Point2D;

import java.util.List;

public class CubicBezierCurveGenerator {

	public enum Hint {
		LEADING,
		TRAILING
	}

	public Curve2D generate( List<Point2D> points, Hint hint ) {
		// Determine initial control points based on the incoming hint
		Point2D a = new Point2D(0,0);
		Point2D b = new Point2D(0,0);
		Point2D c = new Point2D(0,0);
		Point2D d = new Point2D(0,0);

		a.add( 1,1 );

		return new Curve2D(a,b,c,d);
	}

}
