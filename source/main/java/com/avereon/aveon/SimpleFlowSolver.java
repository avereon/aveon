package com.avereon.aveon;

import com.avereon.util.Log;

import java.util.concurrent.ExecutorService;

public class SimpleFlowSolver extends FlowSolver {

	public SimpleFlowSolver( Flow2D flow, ExecutorService executor ) {
		super( flow, executor );
	}

	@Override
	public void run() {
		log.log( Log.DEBUG, "Starting simple flow solver..." );

		// TODO Calculate the flow field

		// TODO Calculate the surface pressures (lift, lifting moment, induced drag)

		// TODO Analyze the boundary layer

		// TODO Calculate the surface shear (parasite drag)

		log.log( Log.INFO, "Simple flow solver started." );
	}

}
