package com.avereon.aveon;

import com.avereon.data.Node;
import com.avereon.geometry.Cubic2D;
import com.avereon.geometry.Point2D;
import lombok.CustomLog;
import org.tinyspline.BSpline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;

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

	/**
	 * Get the airfoil definition points starting from the trailing edge, going
	 * across the lower surface to the leading edge, then across the upper surface
	 * to the trailing edge again. The first and last point should be equal.
	 *
	 * @return The list of definition points for the airfoil
	 */
	public List<Point2D> getDefinitionPoints() {
		List<Point2D> points = new ArrayList<>( getLowerDefinitionPoints() );
		points.remove( 0 );
		Collections.reverse( points );
		points.addAll( getUpperDefinitionPoints() );
		return points;
	}

	/**
	 * Set the airfoil definition points.
	 *
	 * @param upper The upper points from leading edge to trailing edge
	 * @param lower The lower points from leading edge to trailing edge
	 * @return This airfoil
	 */
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

		// NEXT Generate cubic curve surfaces
		List<Cubic2D> surface = fitSurface( getDefinitionPoints() );
		// TODO Split the list of surface cubics into upper and lower groups
		// TODO Use the cubics to determine the panel points
		//fitPoints( panelCount + 1, UPPER_PANEL_POINTS, LOWER_PANEL_POINTS, Airfoil::cosineSpacing );

		List<Point2D> uppers = getValue( UPPER_ANALYSIS_POINTS, List.of() );
		List<Point2D> lowers = getValue( LOWER_ANALYSIS_POINTS, List.of() );
		int count = lowers.size();
		List<Point2D> camber = new ArrayList<>();
		for( int index = 0; index < count; index++ ) {
			Point2D upper = uppers.get( index );
			Point2D lower = lowers.get( index );

			// Find the max camber
			// Camber is the midpoint between the upper and lower surfaces at each station
			Point2D camberPoint = upper.midpoint( lower );
			camber.add( camberPoint );
			if( camberPoint.getY() > maxCamber.getY() ) maxCamber = camberPoint;

			// Find the max thickness
			// Thickness is measured at each station
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

	List<Cubic2D> fitSurface( List<Point2D> surface ) {
		// Points go across the bottom then across the top

		List<Double> points = surface.stream().flatMap( p -> Stream.of( p.getX(), p.getY() ) ).toList();
		BSpline spline = BSpline.interpolateCubicNatural( points, 2 );
		//log.atWarn().log( "Spline degree={0} points={1}", spline.getDegree(), spline.getNumControlPoints() );

		//DeBoorNet bisect = spline.bisect( 0, 0 );
		//log.atWarn().log( "Value knot={0}", bisect.getKnot() );
		//log.atWarn().log("Bezier degree={0}",bezier.getDegree());

		List<Double> controlPoints = spline.getControlPoints();
		int size = (int)(spline.getOrder() * spline.getDimension());
		int surfaceCount = controlPoints.size() / size;
		List<Cubic2D> panelCurves = new ArrayList<>( surfaceCount );
		for( int index = 0; index < surfaceCount; index++ ) {
			int offset = index * size;
			panelCurves.add( new Cubic2D(
				controlPoints.get( offset ),
				controlPoints.get( offset + 1 ),
				controlPoints.get( offset + 2 ),
				controlPoints.get( offset + 3 ),
				controlPoints.get( offset + 4 ),
				controlPoints.get( offset + 5 ),
				controlPoints.get( offset + 6 ),
				controlPoints.get( offset + 7 )
			) );
		}

		List<Point2D> lowerPanelPoints = new ArrayList<>();
		List<Point2D> upperPanelPoints = new ArrayList<>();
		boolean isUpperSurface = false;
		lowerPanelPoints.add( Point2D.of( 1, 0 ) );
		upperPanelPoints.add( Point2D.of( 0, 0 ) );
		for( Cubic2D s : panelCurves ) {
			Point2D p2 = Point2D.of( s.bx, s.by );
			Point2D p3 = Point2D.of( s.cx, s.cy );
			Point2D p4 = Point2D.of( s.dx, s.dy );
			if( isUpperSurface ) {
				upperPanelPoints.add( p2 );
				upperPanelPoints.add( p3 );
				upperPanelPoints.add( p4 );
			} else {
				lowerPanelPoints.add( p2 );
				lowerPanelPoints.add( p3 );
				lowerPanelPoints.add( p4 );
			}
			if( p4.x == 0.0 && p4.y == 0.0 ) isUpperSurface = true;
		}
		Collections.reverse( lowerPanelPoints );
		setValue( UPPER_PANEL_POINTS, upperPanelPoints );
		setValue( LOWER_PANEL_POINTS, lowerPanelPoints );

		// NEXT TODO Find intersections along the upper and lower surfaces

		return panelCurves;
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
