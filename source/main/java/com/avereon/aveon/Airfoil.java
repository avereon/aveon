package com.avereon.aveon;

import com.avereon.data.Node;
import com.avereon.geometry.Point2D;
import lombok.CustomLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Some examples:
 * <ul>
 *   <li>http://airfoiltools.com/airfoil/lednicerdatfile?airfoil=clarky-il</li>
 *   <li>http://airfoiltools.com/airfoil/lednicerdatfile?airfoil=ht05-il</li>
 *   <li>http://airfoiltools.com/airfoil/lednicerdatfile?airfoil=e376-il</li>
 *   <li>http://airfoiltools.com/airfoil/lednicerdatfile?airfoil=e176-il</li>
 * </ul>
 */
@CustomLog
public class Airfoil extends Node {

	private static final String ID = "id";

	private static final String NAME = "name";

	/**
	 * The points used to define the airfoil before it is analyzed.
	 */
	private static final String UPPER_DEFINITION_POINTS = "upper-definition-points";

	/**
	 * The points used to define the airfoil before it is analyzed.
	 */
	private static final String LOWER_DEFINITION_POINTS = "lower-definition-points";

	/**
	 * The points used to define the airfoil after it is analyzed and to be used for calculations.
	 */
	private static final String UPPER_ANALYSIS_POINTS = "upper-analysis-points";

	/**
	 * The points used to define the airfoil after it is analyzed and to be used for calculations.
	 */
	private static final String LOWER_ANALYSIS_POINTS = "lower-analysis-points";

	/**
	 * The points used to define the airfoil after it is analyzed and to be used for panels.
	 */
	private static final String UPPER_PANEL_POINTS = "upper-construction-points";

	/**
	 * The points used to define the airfoil after it is analyzed and to be used for panels.
	 */
	private static final String LOWER_PANEL_POINTS = "lower-construction-points";

	// The highest point on the upper surface
	private double maxY;

	// The lowest point on the lower surface
	private double minY;

	// The thickness at maxY
	private Point2D upperThickness = Point2D.ZERO;

	// The thickness at minY
	private Point2D lowerThickness = Point2D.ZERO;

	private Point2D maxThickness = Point2D.ZERO;

	private Point2D thicknessUpper = Point2D.ZERO;

	private Point2D thicknessLower = Point2D.ZERO;

	private List<Point2D> camber = List.of();

	private Point2D maxCamber = Point2D.ZERO;

	private List<List<Point2D>> upperPointGroups;

	private List<List<Point2D>> lowerPointGroups;

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

	public List<Point2D> getUpperDefinitionPoints() {
		return getValue( UPPER_DEFINITION_POINTS );
	}

	private Airfoil setUpperDefinitionPoints( List<Point2D> coords ) {
		setValue( UPPER_DEFINITION_POINTS, coords );
		return this;
	}

	public List<Point2D> getLowerDefinitionPoints() {
		return getValue( LOWER_DEFINITION_POINTS );
	}

	private Airfoil setLowerDefinitionPoints( List<Point2D> coords ) {
		setValue( LOWER_DEFINITION_POINTS, coords );
		return this;
	}

	public List<Point2D> getDefinitionPoints() {
		List<Point2D> points = new ArrayList<>( getLowerDefinitionPoints() );
		points.remove( 0 );
		Collections.reverse( points );
		points.addAll( getUpperDefinitionPoints() );
		return points;
	}

	public Airfoil setDefinitionPoints( List<Point2D> upper, List<Point2D> lower ) {
		setUpperDefinitionPoints( upper );
		setLowerDefinitionPoints( lower );
		analyze();
		return this;
	}

	private List<Point2D> getUpperAnalysisPoints() {
		return getValue( UPPER_ANALYSIS_POINTS, List.of() );
	}

	private List<Point2D> getLowerAnalysisPoints() {
		return getValue( LOWER_ANALYSIS_POINTS, List.of() );
	}

	public List<Point2D> getUpperPoints() {
		return getValue( UPPER_PANEL_POINTS, List.of() );
	}

	public List<Point2D> getLowerPoints() {
		return getValue( LOWER_PANEL_POINTS, List.of() );
	}

	public double getMinY() {
		return minY;
	}

	public double getMaxY() {
		return maxY;
	}


	public double getThickness() {
		return maxThickness.getY();
	}

	public double getThicknessStation() {
		return maxThickness.getX();
	}

	public Point2D getThicknessUpper() {
		return thicknessUpper;
	}

	public Point2D getThicknessLower() {
		return thicknessLower;
	}

	public Point2D getUpperThickness() {
		return upperThickness;
	}

	public Point2D getLowerThickness() {
		return lowerThickness;
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

	public List<List<Point2D>> getUpperPointGroups() {
		return upperPointGroups;
	}

	public List<List<Point2D>> getLowerPointGroups() {
		return lowerPointGroups;
	}

	public boolean isAnalyzed() {
		return getUpperPoints() != null && getLowerPoints() != null;
	}

	public void analyze() {
		// Min Y
		double minY = Double.MAX_VALUE;
		double lowerThicknessStation = Double.NaN;
		for( Point2D point : getLowerDefinitionPoints() ) {
			if( point.y < minY ) {
				minY = point.y;
				lowerThicknessStation = point.x;
			}
		}
		this.minY = minY;

		// Max Y
		double maxY = Double.MIN_VALUE;
		double upperThicknessStation = Double.NaN;
		for( Point2D point : getUpperDefinitionPoints() ) {
			if( point.y > maxY ) {
				maxY = point.y;
				upperThicknessStation = point.x;
			}
		}
		this.maxY = maxY;

		// Point groups
		upperPointGroups = getStationPointGroups( getUpperDefinitionPoints() );
		lowerPointGroups = getStationPointGroups( getLowerDefinitionPoints() );

		// Inflections
		upperInflections = Collections.unmodifiableList( findInflections( upperPointGroups ) );
		lowerInflections = Collections.unmodifiableList( findInflections( lowerPointGroups ) );

		upperThickness = new Point2D( upperThicknessStation, maxY );
		lowerThickness = new Point2D( lowerThicknessStation, minY );

		// Build analysis points from the definition points
		fitPoints( 1000, UPPER_ANALYSIS_POINTS, LOWER_ANALYSIS_POINTS, Airfoil::linearSpacing );

		// Build panel points from the definition points
		int panelCount = 60;
		fitPoints( panelCount + 1, UPPER_PANEL_POINTS, LOWER_PANEL_POINTS, Airfoil::cosineSpacing );

		List<Point2D> uppers = getValue( UPPER_ANALYSIS_POINTS, List.of() );
		List<Point2D> lowers = getValue( LOWER_ANALYSIS_POINTS, List.of() );
		int count = lowers.size();
		List<Point2D> camber = new ArrayList<>();
		for( int index = 0; index < count; index++ ) {
			Point2D upper = uppers.get( index );
			Point2D lower = lowers.get( index );

			// FIXME Camber should be calculated after the b-spline is derived
			// Camber is the midpoint between the upper and lower surfaces at each station; find the max-Y value
			// Camber
			Point2D camberPoint = upper.midpoint( lower );
			camber.add( camberPoint );
			if( camberPoint.getY() > maxCamber.getY() ) maxCamber = camberPoint;

			// FIXME Thickness should be calculated after the b-spline is derived
			// Thickness is measured at each station; find the max thickness
			// Thickness
			double thickness = upper.distance( lower );
			if( thickness > getThickness() ) {
				thicknessUpper = upper;
				thicknessLower = lower;
				maxThickness = Point2D.of( upper.getX(), thickness );
			}
		}
		this.camber = Collections.unmodifiableList( camber );
		//		if( maxCamber.getX() == 0 ) maxCamber = new Point2D( getThicknessMoment(), 0 );
	}

	void fitPoints( int stationCount, String upperKey, String lowerKey, BiFunction<Double, Double, Double> spacing ) {
		List<Point2D> upperPoints = new ArrayList<>();
		List<Point2D> lowerPoints = new ArrayList<>();

		upperPoints.add( Point2D.of( 0, 0 ) );
		lowerPoints.add( Point2D.of( 0, 0 ) );
		for( int index = 1; index < stationCount; index++ ) {
			double analysisStation = spacing.apply( (double)stationCount, (double)index );
			upperPoints.add( findStationPoint( getUpperDefinitionPoints(), analysisStation ) );
			lowerPoints.add( findStationPoint( getLowerDefinitionPoints(), analysisStation ) );
		}
		upperPoints.add( Point2D.of( 1, 0 ) );
		lowerPoints.add( Point2D.of( 1, 0 ) );

		setValue( upperKey, upperPoints );
		setValue( lowerKey, lowerPoints );
	}

	private static double linearSpacing( double stationCount, double index ) {
		return index / stationCount;
	}

	private static double cosineSpacing( double stationCount, double index ) {
		double alpha = Math.PI * linearSpacing( stationCount, index );
		return 1.0 - (0.5 * Math.cos( alpha ) + 0.5);
	}

	private Point2D findStationPoint( List<Point2D> definitionPoints, double station ) {
		// NOTE A minimum of three points is required for this to work

		// Find the points to use for the Lagrange polynomial
		Point2D a = null;
		Point2D b = null;
		Point2D c = null;
		int index = 0;
		int count = definitionPoints.size() - 1;
		while( index < count && a == null ) {
			if( station > definitionPoints.get( index ).getX() && station < definitionPoints.get( index + 1 ).getX() ) {
				if( index == 0 ) {
					a = definitionPoints.get( index );
					b = definitionPoints.get( index + 1 );
					c = definitionPoints.get( index + 2 );
				} else {
					a = definitionPoints.get( index - 1 );
					b = definitionPoints.get( index );
					c = definitionPoints.get( index + 1 );
				}
			}
			index++;
		}

		// Find the point for the station using a Lagrange polynomial
		return Point2D.of( station, lagrange( a, b, c, station ) );
	}

	private double lagrange( Point2D a, Point2D b, Point2D c, double x ) {
		double x0 = a.getX();
		double y0 = a.getY();
		double x1 = b.getX();
		double y1 = b.getY();
		double x2 = c.getX();
		double y2 = c.getY();

		double t0 = y0 * ((x - x1) / (x0 - x1)) * ((x - x2) / (x0 - x2));
		double t1 = y1 * ((x - x0) / (x1 - x0)) * ((x - x2) / (x1 - x2));
		double t2 = y2 * ((x - x0) / (x2 - x0)) * ((x - x1) / (x2 - x1));

		return t0 + t1 + t2;
	}

	List<Point2D> findInflections( List<List<Point2D>> groups ) {
		List<Point2D> inflections = new ArrayList<>();
		groups.forEach( g -> inflections.add( g.get( 0 ) ) );

		List<Point2D> last = groups.get( groups.size() - 1 );
		inflections.add( last.get( last.size() - 1 ) );

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

	private List<List<Point2D>> getStationPointGroups( List<Point2D> points ) {
		List<List<Point2D>> groups = new ArrayList<>();

		int index = 1;
		int count = points.size();
		Point2D point = points.get( 0 );
		Point2D next = null;
		double priordY = 0;
		List<Point2D> group = new ArrayList<>();
		while( index < count ) {
			group.add( point );

			next = points.get( index );
			double dY = next.getY() - point.getY();

			if( switched( priordY, dY ) ) {
				groups.add( group );
				group = new ArrayList<>();
				group.add( point );
			}

			point = next;
			priordY = dY;
			index++;
		}
		group.add( next );
		groups.add( group );

		int groupPointCount = groups.stream().mapToInt( List::size ).sum();

		return groups;
	}

	private boolean switched( double a, double b ) {
		return (a > 0 & b < 0) || (a < 0 & b > 0);
	}

}
