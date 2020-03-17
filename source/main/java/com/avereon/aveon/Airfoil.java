package com.avereon.aveon;

import com.avereon.data.Node;
import javafx.geometry.Point2D;

import java.util.List;

public class Airfoil extends Node {

	private static final String ID = "id";

	private static final String NAME = "name";

	private static final String UPPER = "upper";

	private static final String LOWER = "lower";

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

}
