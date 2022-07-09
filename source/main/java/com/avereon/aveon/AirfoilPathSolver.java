package com.avereon.aveon;

import com.avereon.skill.RunPauseResettable;
import lombok.CustomLog;

@CustomLog
public class AirfoilPathSolver implements RunPauseResettable {

	private Airfoil airfoil;

	private boolean halt = false;

	public AirfoilPathSolver( Airfoil airfoil ) {
		this.airfoil = airfoil;
	}

	@Override
	public void run() {
		log.atDebug().log( "Starting airfoil path solver..." );
		log.atInfo().log( "Airfoil path solver started." );

		halt = false;
		while( !halt ) {
			// Find curves that fit the airfoil points
			airfoil.fitCurves();
		}
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

}
