package com.avereon.aveon;

import com.avereon.data.Node;
import com.avereon.skill.Resettable;

public class Flow2D extends Node implements Resettable {

	static final String SOLVER = "solver";

	static final String AIRFOIL = "airfoil";

	static final String FLOW_FIELD = "flow-field";

	private static final String SURFACE_PRESSURES = "surface-pressures";

	private static final String SURFACE_MOMENTS = "surface-moments";

	public Flow2D() {
		setValue( FLOW_FIELD, new FlowField() );
	}

	public Airfoil getAirfoil() {
		return getValue( AIRFOIL );
	}

	public Flow2D setAirfoil( Airfoil airfoil ) {
		setValue( AIRFOIL, airfoil );
		return this;
	}

	public FlowField getFlowField() {
		return getValue( FLOW_FIELD );
	}

	public Flow2D setFlowSolver( FlowSolver solver ) {
		setValue( SOLVER, solver );
		return this;
	}

	public FlowSolver getFlowSolver() {
		return getValue( SOLVER );
	}

	/**
	 * Reset all the derived state
	 */
	public void reset() {
		getFlowField().invalidate();
	}

}
