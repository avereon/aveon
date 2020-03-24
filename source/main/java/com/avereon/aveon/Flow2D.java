package com.avereon.aveon;

import com.avereon.data.Node;

import java.net.URL;

public class Flow2D extends Node {

	private static final String AIRFOIL_URL = "airfoil-url";

	public URL getAirfoilUrl() {
		return getValue( AIRFOIL_URL );
	}

	public Flow2D setAirfoilUrl( URL url ) {
		setValue( AIRFOIL_URL, url );
		return this;
	}

}
