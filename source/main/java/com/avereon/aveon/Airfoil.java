package com.avereon.aveon;

import com.avereon.data.Node;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Some examples:
 * <ul>
 *   <li>http://airfoiltools.com/airfoil/lednicerdatfile?airfoil=clarky-il</li>
 *   <li>http://airfoiltools.com/airfoil/lednicerdatfile?airfoil=ht05-il</li>
 *   <li>http://airfoiltools.com/airfoil/lednicerdatfile?airfoil=e376-il</li>
 *   <li>http://airfoiltools.com/airfoil/lednicerdatfile?airfoil=e176-il</li>
 * </ul>
 */
public class Airfoil extends Node {

	private static final String ID = "id";

	private static final String NAME = "name";

	private static final String UPPER = "upper";

	private static final String LOWER = "lower";

	private double minY;

	private double maxY;

	private Point2D thicknessUpper = Point2D.ZERO;

	private Point2D thicknessLower = Point2D.ZERO;

	private List<Point2D> camber = List.of();

	private Point2D maxCamber = Point2D.ZERO;

	private List<Point2D> upperInflections = List.of();

	private List<Point2D> lowerInflections = List.of();

	public Airfoil() {
		definePrimaryKey( ID );
		defineNaturalKey( NAME );
	}

	public String getId() {
		return getValue( ID );
	}

	public Airfoil setId( String id ) {
		setValue( ID, id );
		return this;
	}

	public String getName() {
		return getValue( NAME );
	}

	public Airfoil setName( String name ) {
		setValue( NAME, name );
		return this;
	}

	public List<Point2D> getUpper() {
		return getValue( UPPER );
	}

	public Airfoil setUpper( List<Point2D> coords ) {
		setValue( UPPER, coords );
		return this;
	}

	public List<Point2D> getLower() {
		return getValue( LOWER );
	}

	public Airfoil setLower( List<Point2D> coords ) {
		setValue( LOWER, coords );
		return this;
	}

	public List<Point2D> getPoints() {
		List<Point2D> points = new ArrayList<>( getLower() );
		points.remove( 0 );
		Collections.reverse( points );
		points.addAll( getUpper() );
		return points;
	}

	public double getMinY() {
		return minY;
	}

	public double getMaxY() {
		return maxY;
	}

	public double getThickness() {
		return thicknessLower.distance( thicknessUpper );
	}

	public double getThicknessMoment() {
		return thicknessLower.getX();
	}

	public Point2D getThicknessUpper() {
		return thicknessUpper;
	}

	public Point2D getThicknessLower() {
		return thicknessLower;
	}

	public List<Point2D> getCamber() {
		return camber;
	}

	public Point2D getMaxCamber() {
		return maxCamber;
	}

	public List<Point2D> getUpperInflections() {
		return upperInflections;
	}

	public List<Point2D> getLowerInflections() {
		return lowerInflections;
	}

	public void analyze() {
		// Min Y
		double minY = Double.MAX_VALUE;
		for( Point2D point : getLower() ) {
			if( point.getY() < minY ) minY = point.getY();
		}
		this.minY = minY;

		// Max Y
		double maxY = Double.MIN_VALUE;
		for( Point2D point : getUpper() ) {
			if( point.getY() > maxY ) maxY = point.getY();
		}
		this.maxY = maxY;

		// Upper inflections
		upperInflections = Collections.unmodifiableList( findInflectionsY( getUpper() ) );

		// Lower inflections
		lowerInflections = Collections.unmodifiableList( findInflectionsY( getLower() ) );

		//		int count = getLower().size();
		//		List<Point2D> camber = new ArrayList<>();
		//		for( int index = 0; index < count; index++ ) {
		//			Point2D upper = getUpper().get( index );
		//			Point2D lower = getLower().get( index );
		//
		//			// FIXME Camber should be calculated after the b-spline is derived
		//			// Camber
		//			Point2D camberPoint = upper.midpoint( lower );
		//			camber.add( camberPoint );
		//			if( camberPoint.getY() > maxCamber.getY() ) maxCamber = camberPoint;
		//
		//			// FIXME Thickness should be calculated after the b-spline is derived
		//			// Thickness
		//			double thickness = upper.distance( lower );
		//			if( thickness > getThickness() ) {
		//				thicknessUpper = upper;
		//				thicknessLower = lower;
		//			}
		//		}
		//		this.camber = Collections.unmodifiableList( camber );
		//		if( maxCamber.getX() == 0 ) maxCamber = new Point2D( getThicknessMoment(), 0 );
	}

	List<Point2D> findInflectionsY( List<Point2D> points ) {
		List<Point2D> inflections = new ArrayList<>();

		int index = 1;
		int count = points.size();
		Point2D prior = points.get( 0 );
		double priordY = 0;
		while( index < count ) {
			Point2D point = points.get( index );
			double dY = point.getY() - prior.getY();

			if( switched( priordY, dY ) ) inflections.add( prior );

			prior = point;
			priordY = dY;
			index++;
		}

		return inflections;
	}

	private boolean switched( double a, double b ) {
		return (a > 0 & b < 0) || (a < 0 & b > 0);
	}

}
