package com.avereon.aveon;

import com.avereon.skill.RunPauseResettable;
import lombok.CustomLog;

import java.util.concurrent.ExecutorService;

@CustomLog
public class AirfoilPathSolver implements RunPauseResettable {

	public AirfoilPathSolver( Airfoil airfoil, ExecutorService executor ) {
		//super( flow, executor );
	}

	@Override
	public void run() {
		log.atDebug().log( "Starting airfoil path solver..." );

		log.atInfo().log( "Simple airfoil path started." );
	}

	@Override
	public void pause() {

	}

	@Override
	public void reset() {

	}

}
