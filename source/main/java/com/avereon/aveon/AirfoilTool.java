package com.avereon.aveon;

import com.avereon.product.Rb;
import com.avereon.skill.RunPauseResettable;
import com.avereon.xenon.ProgramAction;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.ProgramTool;
import com.avereon.xenon.action.common.ResetAction;
import com.avereon.xenon.action.common.RunPauseAction;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.OpenAssetRequest;
import com.avereon.xenon.workpane.ToolException;
import lombok.CustomLog;

@CustomLog
public class AirfoilTool extends ProgramTool implements RunPauseResettable {

	static final String AIRFOIL_URL = "airfoil-url";

	private final ProgramAction runPauseAction;

	private final ProgramAction resetAction;

	public AirfoilTool( ProgramProduct product, Asset asset ) {
		super( product, asset );

		runPauseAction = new RunPauseAction( getProgram(), this );
		resetAction = new ResetAction( getProgram(), this );
	}

	@Override
	protected void ready( OpenAssetRequest request ) throws ToolException {
		super.ready( request );
		setGraphic( getProgram().getIconLibrary().getIcon( "flow" ) );
		setTitle( Rb.textOr( getProduct(), "asset", "airfoil2d-name", "Flow" ) );

		log.atWarn().log( "Airfoil handed to tool: lower=%d upper=%d", getAirfoil().getLowerStationPoints().size(), getAirfoil().getUpperStationPoints().size() );
	}

	@Override
	protected void open( OpenAssetRequest request ) {
		// Load the initial state from the flow (asset model)
		if( getAirfoil() == null || !getAirfoil().isAnalyzed() ) {
			String airfoilUrl = getAssetSettings().get( "airfoil-url" );
			if( airfoilUrl == null ) airfoilUrl = getSettings().get( AIRFOIL_URL );
			//loadAirfoilPoints( airfoilUrl );
			// TODO Load the airfoil points using the codec
		}

		// Register airfoil (asset model) event handlers...
		//getAirfoil().register( Flow2D.AIRFOIL, ( e ) -> getAirfoil().getPathSolver().reset() );
	}

	@Override
	protected void activate() {
		pushAction( "runpause", runPauseAction );
		pushAction( "reset", resetAction );
		pushTools( "reset runpause" );

		// Set the current action state
		if( getAsset().isLoaded() ) {
			//AirfoilPathSolver solver = getAirfoil().getPathSolver();
			//runPauseAction.setState( solver != null && solver.isRunning() ? "pause" : "run" );
		}
	}

	@Override
	protected void conceal() {
		pullTools();
		pullAction( "runpause", runPauseAction );
		pullAction( "reset", resetAction );
	}

	public void run() {
		//		final FlowSolver solver = new SimpleFlowSolver( getAirfoil(), getProgram().getTaskManager().getExecutor() );
		//
		//		Task<?> t = Task.of( "Start flow solver", solver );
		//		t.register( TaskEvent.FINISH, ( e ) -> {
		//			log.atInfo().log( "Flow solver complete!" );
		//			runPauseAction.setState( "run" );
		//		} );
		//		getProgram().getTaskManager().submit( t );
	}

	public void pause() {
		//getAirfoil().getPathSolver().pause();
	}

	public void reset() {
		//getAirfoil().getPathSolver().reset();
	}

	private Airfoil getAirfoil() {
		return getAssetModel();
	}

}
