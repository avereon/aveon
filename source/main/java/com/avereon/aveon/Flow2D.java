package com.avereon.aveon;

import com.avereon.data.Node;

public class Flow2D extends Node {

	private static final String AIRFOIL_URL = "airfoil-url";

	public String getAirfoilUrl() {
		return getValue( AIRFOIL_URL );
	}

	public Flow2D setAirfoilUrl( String url ) {
		setValue( AIRFOIL_URL, url );
		return this;
	}

}
