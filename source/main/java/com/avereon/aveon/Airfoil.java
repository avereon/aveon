package com.avereon.aveon;

import com.avereon.curve.math.*;
import com.avereon.data.Node;
import com.avereon.geometry.Cubic2D;
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
	private static final String UPPER_PANEL_CURVES = "upper-construction-curves";

	/**
	 * The points used to define the airfoil after it is analyzed and to be used for panels.
	 */
	private static final String LOWER_PANEL_CURVES = "lower-construction-curves";

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

	/**
	 * Set the airfoil definition points.
	 *
	 * @param upper The upper points from leading edge to trailing edge
	 * @param lower The lower points from leading edge to trailing edge
	 * @return This airfoil
	 */
	public Airfoil setDefinitionPoints( List<Point2D> upper, List<Point2D> lower ) {
		adjustLeadingEdgePoints( upper, lower );
		adjustLeadingEdgePoints( lower, upper );

		setUpperDefinitionPoints( normalize( upper ) );
		setLowerDefinitionPoints( normalize( lower ) );
		analyze();
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

	private static void adjustLeadingEdgePoints( List<Point2D> check, List<Point2D> compliment ) {
		Point2D prior = check.get( 0 );
		for( Point2D p : new ArrayList<>( check ) ) {
			if( p.x < prior.x ) {
				// Move prior
				check.remove( prior );
				if( !compliment.contains( prior ) ) compliment.add( 0, prior );
				prior = p;
			}
		}
		Point2D first = check.get(0);
		if( !compliment.contains( first ) ) compliment.add(0, first );
	}

	private static List<Point2D> normalize( List<Point2D> points ) {
		Point2D first = points.get( 0 );
		Point2D last = points.get( points.size() - 1 );

		double angle = Geometry.getAngle( Point.of( 1, 0 ), last.subtract( first ).toArray() );
		double length = Geometry.distance( first.toArray(), last.toArray() );

		//log.atWarn().log("first={0} last={1} length={2} angle={3}", first, last, length, angle );

		// Move to the origin
		Transform a = Transform.scale( 1.0 / length, 1, 1 );

		// Rotate the trailing edge
		Transform b = a.combine( Transform.rotation( Point.of( 0, 0, 1 ), -angle ) );

		// Scale to unit chord
		Transform c = b.combine( Transform.translation( -first.x, -first.y, 0 ) );

		List<Point2D> cleaned = new ArrayList<>( points.stream().map( p -> {
			double[] t = c.apply( Point.of( p.x, p.y ) );
			return Point2D.of( t[ 0 ], t[ 1 ] );
		} ).toList() );

		cleaned.set( 0, new Point2D( 0, 0 ) );
		cleaned.set( cleaned.size() - 1, new Point2D( 1, 0 ) );

		return cleaned;
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

		// Generate cubic curve surfaces
		fitSurface( getDefinitionPoints() );

		// Generate analysis points
		fitPoints( 1000, UPPER_ANALYSIS_POINTS, LOWER_ANALYSIS_POINTS, Airfoil::linearSpacing );

		// Generate panel points
		int panelCount = 60;
		fitPoints( panelCount + 1, UPPER_PANEL_POINTS, LOWER_PANEL_POINTS, Airfoil::cosineSpacing );

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

		// Prepare the points for interpolation
		int count = surface.size();
		double[][] points = new double[ count ][ 3 ];
		for( int index = 0; index < count; index++ ) {
			Point2D p = surface.get( index );
			points[ index ][ 0 ] = p.getX();
			points[ index ][ 1 ] = p.getY();
			points[ index ][ 2 ] = 0;
		}

		// Interpolate to cubic bezier curves
		double[][][] curves = Geometry.interpolateCubicNatural( points );

		// Convert curves from array to objects
		int surfaceCount = curves.length;
		List<Cubic2D> panelCurves = new ArrayList<>( surfaceCount );
		for( double[][] curve : curves ) {
			panelCurves.add( new Cubic2D( curve[ 0 ][ 0 ], curve[ 0 ][ 1 ], curve[ 1 ][ 0 ], curve[ 1 ][ 1 ], curve[ 2 ][ 0 ], curve[ 2 ][ 1 ], curve[ 3 ][ 0 ], curve[ 3 ][ 1 ] ) );
		}

		// Split the surface curves into upper and lower curve lists
		boolean isUpperSurface = false;
		List<Cubic2D> lowerPanelCurves = new ArrayList<>();
		List<Cubic2D> upperPanelCurves = new ArrayList<>();
		for( Cubic2D s : panelCurves ) {
			Point2D p1 = Point2D.of( s.ax, s.ay );
			Point2D p2 = Point2D.of( s.bx, s.by );
			Point2D p3 = Point2D.of( s.cx, s.cy );
			Point2D p4 = Point2D.of( s.dx, s.dy );
			if( isUpperSurface ) {
				upperPanelCurves.add( new Cubic2D( p1, p2, p3, p4 ) );
			} else {
				lowerPanelCurves.add( new Cubic2D( p4, p3, p2, p1 ) );
			}

			if( Geometry.distance( Point.of( p4.x, p4.y ) ) < 1e-12 ) {
				//log.atWarn().log( "Should be close: {0},{1}", p4.x, p4.y );
				isUpperSurface = true;
			}
		}
		if( !isUpperSurface ) log.atWarn().log( "Did not switch to upper surface" );
		Collections.reverse( lowerPanelCurves );
		setValue( UPPER_PANEL_CURVES, upperPanelCurves );
		setValue( LOWER_PANEL_CURVES, lowerPanelCurves );

		return panelCurves;
	}

	void fitPoints( int stationCount, String upperKey, String lowerKey, BiFunction<Double, Double, Double> spacing ) {
		List<Point2D> upperPoints = new ArrayList<>();
		List<Point2D> lowerPoints = new ArrayList<>();

		upperPoints.add( Point2D.of( 0, 0 ) );
		lowerPoints.add( Point2D.of( 0, 0 ) );
		for( int index = 1; index < stationCount; index++ ) {
			double station = spacing.apply( (double)stationCount, (double)index );
			upperPoints.add( findStationPoint( getValue( UPPER_PANEL_CURVES ), station ) );
			lowerPoints.add( findStationPoint( getValue( LOWER_PANEL_CURVES ), station ) );
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

	private Point2D findStationPoint( List<Cubic2D> panelCurves, double station ) {
		// Compute the intersection of a line at the station and a definition curve
		for( Cubic2D curve : panelCurves ) {
			if( station < curve.ax || station > curve.dx ) continue;
			double[] p1 = Point.of( station, 1.0 );
			double[] p2 = Point.of( station, -1.0 );
			double[] a = Point.of( curve.ax, curve.ay );
			double[] b = Point.of( curve.bx, curve.by );
			double[] c = Point.of( curve.cx, curve.cy );
			double[] d = Point.of( curve.dx, curve.dy );
			Intersection2D intersection = Intersection2D.intersectLineBezier3( p1, p2, a, b, c, d );
			if( intersection.getType() == Intersection.Type.INTERSECTION ) {
				return Point2D.of( intersection.getPoints()[ 0 ] );
			}
		}

		//log.atWarn().log( "No intersection found at " + station );

		// Add the point for the station
		return Point2D.ZERO;
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

		return groups;
	}

	private boolean switched( double a, double b ) {
		return (a > 0 & b < 0) || (a < 0 & b > 0);
	}

}
