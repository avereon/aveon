package com.avereon.aveon;

import lombok.CustomLog;

import java.util.concurrent.ExecutorService;

@CustomLog
public class SimpleFlowSolver extends FlowSolver {

	public SimpleFlowSolver( Flow2D flow, ExecutorService executor ) {
		super( flow, executor );
	}

	@Override
	public void run() {
		log.atDebug().log( "Starting simple flow solver..." );

		// TODO Calculate the flow field

		// TODO Calculate the surface pressures (lift, lifting moment, induced drag)

		// TODO Analyze the boundary layer

		// TODO Calculate the surface shear (parasite drag)

		log.atInfo().log( "Simple flow solver started." );
	}

}
