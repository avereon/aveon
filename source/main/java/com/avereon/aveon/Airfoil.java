package com.avereon.aveon;

import com.avereon.data.Node;
import com.avereon.geometry.Cubic2D;
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

	//	private static final String UPPER = "upper";
	//
	//	private static final String LOWER = "lower";

	private static final String UPPER_CURVES = "upper-curves";

	private static final String LOWER_CURVES = "lower-curves";

	private static final String UPPER_POINTS = "upper-points";

	private static final String LOWER_POINTS = "lower-points";

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

	public List<Point2D> getUpperStationPoints() {
		return getValue( UPPER_POINTS );
	}

	public Airfoil setUpperStationPoints( List<Point2D> coords ) {
		setValue( UPPER_POINTS, coords );
		return this;
	}

	public List<Point2D> getLowerStationPoints() {
		return getValue( LOWER_POINTS );
	}

	public Airfoil setLowerStationPoints( List<Point2D> coords ) {
		setValue( LOWER_POINTS, coords );
		return this;
	}

	public List<Point2D> getStationPoints() {
		List<Point2D> points = new ArrayList<>( getLowerStationPoints() );
		points.remove( 0 );
		Collections.reverse( points );
		points.addAll( getUpperStationPoints() );
		return points;
	}

	public List<Cubic2D> getUpperCurves() {
		return getValue( UPPER_CURVES );
	}

	public List<Cubic2D> getLowerCurves() {
		return getValue( LOWER_CURVES );
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

	public boolean isAnalyzed() {
		return getUpperCurves() != null && getLowerCurves() != null;
	}

	public void analyze() {
		// Min Y
		double minY = Double.MAX_VALUE;
		for( Point2D point : getLowerStationPoints() ) {
			if( point.getY() < minY ) minY = point.getY();
		}
		this.minY = minY;

		// Max Y
		double maxY = Double.MIN_VALUE;
		for( Point2D point : getUpperStationPoints() ) {
			if( point.getY() > maxY ) maxY = point.getY();
		}
		this.maxY = maxY;

		// Point groups
		List<List<Point2D>> upperPointGroups = getStationPointGroups( getUpperStationPoints() );
		List<List<Point2D>> lowerPointGroups = getStationPointGroups( getLowerStationPoints() );

		// Inflections
		//		upperInflections = Collections.unmodifiableList( findInflectionsY( getUpperStationPoints() ) );
		//		lowerInflections = Collections.unmodifiableList( findInflectionsY( getLowerStationPoints() ) );
		upperInflections = Collections.unmodifiableList( findInflections( upperPointGroups ) );
		lowerInflections = Collections.unmodifiableList( findInflections( lowerPointGroups ) );

		// NEXT Determine upper curves

		// NEXT Determine lower curves

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

	List<Point2D> findInflections( List<List<Point2D>> groups ) {
		List<Point2D> inflections = new ArrayList<>();

		int count = groups.size();
		for( int index = 1; index < count; index++ ) {
			inflections.add( groups.get( index ).get( 0 ) );
		}

		return inflections;
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

	List<List<Point2D>> getStationPointGroups( List<Point2D> points ) {
		List<List<Point2D>> groups = new ArrayList<>();

		int index = 1;
		int count = points.size();
		Point2D prior = points.get( 0 );
		double priordY = 0;
		List<Point2D> group = new ArrayList<>();
		while( index < count ) {
			group.add( prior );
			Point2D point = points.get( index );
			double dY = point.getY() - prior.getY();

			if( switched( priordY, dY ) ) {
				groups.add( group );
				group = new ArrayList<>();
				group.add( prior );
			}

			prior = point;
			priordY = dY;
			index++;
		}
		groups.add( group );

		return groups;
	}

	private boolean switched( double a, double b ) {
		return (a > 0 & b < 0) || (a < 0 & b > 0);
	}

}
