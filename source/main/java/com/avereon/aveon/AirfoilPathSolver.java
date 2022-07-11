package com.avereon.aveon;

import com.avereon.geometry.Cubic2D;
import com.avereon.geometry.Point2D;
import com.avereon.skill.RunPauseResettable;
import lombok.CustomLog;

import java.util.ArrayList;
import java.util.List;

@CustomLog
public class AirfoilPathSolver implements RunPauseResettable {

	private final Airfoil airfoil;

	private boolean halt = false;

	public AirfoilPathSolver( Airfoil airfoil ) {
		this.airfoil = airfoil;
	}

	@Override
	public void run() {
		//log.atDebug().log( "Starting airfoil path solver..." );
		//log.atInfo().log( "Airfoil path solver started." );

		halt = false;
		//while( !halt ) {
		// Find curves that fit the airfoil points
		fitCurves();
		//}
	}

	@Override
	public void pause() {
		log.atDebug().log( "Pausing airfoil path solver..." );

		halt = false;

		log.atInfo().log( "Airfoil path solver paused." );
	}

	@Override
	public void reset() {
		pause();

		log.atDebug().log( "Resetting airfoil path solver..." );

		log.atInfo().log( "Airfoil path solver reset." );
	}

	private void fitCurves() {
		String name = airfoil.getName();

		List<List<Point2D>> upperPointGroups = airfoil.getUpperPointGroups();
		int upperCount = upperPointGroups.size();
		List<Cubic2D> upperCurves = new ArrayList<>();

		List<List<Point2D>> lowerPointGroups = airfoil.getLowerPointGroups();
		int lowerCount = lowerPointGroups.size();
		List<Cubic2D> lowerCurves = new ArrayList<>();

		// TODO Determine upper curves
		upperCurves.add( new CubicBezierCurveFitter2( name, upperPointGroups.get( 0 ), CubicBezierCurveFitter.Hint.LEADING ).generate() );
		for( int index = 1; index < upperCount - 1; index++ ) {
			upperCurves.add( new CubicBezierCurveFitter2( name, upperPointGroups.get( index ), CubicBezierCurveFitter.Hint.MIDDLE ).generate() );
		}
		upperCurves.add( new CubicBezierCurveFitter2( name, upperPointGroups.get( upperCount - 1 ), CubicBezierCurveFitter.Hint.TRAILING ).generate() );

		// TODO Determine lower curves
		lowerCurves.add( new CubicBezierCurveFitter2( name, lowerPointGroups.get( 0 ), CubicBezierCurveFitter.Hint.LEADING ).generate() );
		for( int index = 1; index < lowerCount - 1; index++ ) {
			lowerCurves.add( new CubicBezierCurveFitter2( name, lowerPointGroups.get( index ), CubicBezierCurveFitter.Hint.MIDDLE ).generate() );
		}
		lowerCurves.add( new CubicBezierCurveFitter2( name, lowerPointGroups.get( lowerCount - 1 ), CubicBezierCurveFitter.Hint.TRAILING ).generate() );

		airfoil.setUpperCurves( upperCurves );
		airfoil.setLowerCurves( lowerCurves );
	}

	List<Point2D> findInflections( List<List<Point2D>> groups ) {
		List<Point2D> inflections = new ArrayList<>();

		int count = groups.size();
		for( int index = 0; index < count; index++ ) {
			inflections.add( groups.get( index ).get( 0 ) );
		}

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

	List<List<Point2D>> getStationPointGroups( List<Point2D> points ) {
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
