package com.avereon.aveon;

import com.avereon.data.Node;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Airfoil extends Node {

	private static final String ID = "id";

	private static final String NAME = "name";

	private static final String UPPER = "upper";

	private static final String LOWER = "lower";

	private double thickness;

	private double thicknessMoment;

	private List<Point2D> upperInflections;

	private List<Point2D> lowerInflections;

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

	public double getThickness() {
		return thickness;
	}

	public void analyze() {
		//		double minX = Double.MAX_VALUE;
		//		double maxX = Double.MIN_VALUE;
		//		double minY = Double.MAX_VALUE;
		//		double maxY = Double.MIN_VALUE;

		upperInflections = findInflectionsY( getUpper() );
		lowerInflections = findInflectionsY( getLower() );

		// Find thickness
		// Find thickness x-position

		// Find camber
	}

	List<Point2D> findInflectionsY( List<Point2D> points ) {
		List<Point2D> inflections = new ArrayList<>();

		int index = 1;
		int count = points.size();
		Point2D prior = points.get(0);
		double priordY = 0;
		while( index < count ) {
			Point2D point = points.get( index );
			double dY = point.getY() - prior.getY();

			if( switched( priordY, dY )) inflections.add( point );

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
