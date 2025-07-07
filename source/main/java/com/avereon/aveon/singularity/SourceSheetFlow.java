package com.avereon.aveon.singularity;

import com.avereon.aveon.ElementaryFlow;
import com.avereon.curve.math.Geometry;
import com.avereon.curve.math.Vector;

public class SourceSheetFlow implements ElementaryFlow {
	private final double ax;

	private final double ay;

	private final double bx;

	private final double by;

	private final double q;

	private final double cos;

	private final double sin;

	public SourceSheetFlow( double x1, double y1, double x2, double y2, double q ) {
		this.ax = x1;
		this.ay = y1;
		this.bx = x2;
		this.by = y2;
		this.q = q;

		double length = Geometry.length( Vector.of( x1, y1 ), Vector.of( x2, y2 ) );
		this.cos = (x2 - x1) / length;
		this.sin = (y2 - y1) / length;
	}

	@Override
	public double getStream( double x, double y ) {
		return 0;
	}

	@Override
	public double getPotential( double x, double y ) {
		return 0;
	}

	@Override
	public double[] getVelocity( double xf, double yf ) {
		// TODO This is the best implementation I'm aware of, but the tests don't show it working well
		double ar2, dax, day, br2, dbx, dby, alpha;
		double[] velocity = new double[]{ Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY };

		dax = xf - ax;
		day = yf - ay;
		ar2 = dax * dax + day * day;

		dbx = xf - bx;
		dby = yf - by;
		br2 = dbx * dbx + dby * dby;

		if( ar2 > 0.0 && br2 > 0.0 ) {
			alpha = Math.atan2( dax, day ) - Math.atan2( dbx, dby );
			if( alpha > Math.PI ) alpha -= 2.0 * Math.PI;
			if( alpha < -Math.PI ) alpha += 2.0 * Math.PI;
			velocity[ 0 ] = q * Math.log( ar2 / br2 ) / 2.0 * cos - q * alpha * sin;
			velocity[ 1 ] = q * alpha * cos + q * Math.log( ar2 / br2 ) / 2.0 * sin;
		}

		return velocity;
	}

}
