package com.avereon.geometry;

/**
 * An immutable 2D bounding box that represents the minimum and maximum x and y
 * coordinates of a bounding rectangle.
 */
public class Bounds2D {

	public static final Bounds2D ZERO = new Bounds2D( 0.0, 0.0, 0.0, 0.0 );

	private double minX;

	private double minY;

	private double maxX;

	private double maxY;

	/**
	 * Create a new {@code Bounds2D}.
	 *
	 * @param minX the minimum X coordinate
	 * @param minY the minimum Y coordinate
	 * @param maxX the maximum X coordinate
	 * @param maxY the maximum Y coordinate
	 */
	public Bounds2D( double minX, double minY, double maxX, double maxY ) {
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}

	public double getMinX() {
		return minX;
	}

	public double getMinY() {
		return minY;
	}

	public double getMaxX() {
		return maxX;
	}

	public double getMaxY() {
		return maxY;
	}
}
