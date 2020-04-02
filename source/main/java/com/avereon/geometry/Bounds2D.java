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

	private int hash;

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals( Object object ) {
		if( object == this ) return true;
		if( !(object instanceof Bounds2D) ) return false;
		Bounds2D that = (Bounds2D)object;
		return this.minX == that.minX && this.minY == that.minY && this.maxX == that.maxX && this.maxY == that.maxY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		if( hash == 0 ) {
			long bits = 11L;
			bits = 73L * bits + Double.doubleToLongBits( minX );
			bits = 73L * bits + Double.doubleToLongBits( minY );
			bits = 73L * bits + Double.doubleToLongBits( maxX );
			bits = 73L * bits + Double.doubleToLongBits( maxY );
			hash = (int)(bits ^ (bits >> 32));
		}
		return hash;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "Line2D[minX=" + minX + ",minY=" + minY + ",maxX=" + maxX + ",maxY=" + maxY + "]";
	}

}
