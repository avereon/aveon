package com.avereon.aveon;

import com.avereon.geometry.Point2D;
import com.avereon.marea.Pen;
import com.avereon.marea.Renderer2d;
import com.avereon.marea.fx.FxRenderer2d;
import com.avereon.marea.geom.Ellipse;
import com.avereon.marea.geom.Path;
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
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import lombok.CustomLog;

@CustomLog
public class AirfoilTool extends ProgramTool implements RunPauseResettable {

	static final String AIRFOIL_URL = "airfoil-url";

	private final ProgramAction runPauseAction;

	private final ProgramAction resetAction;

	private final Renderer2d renderer;

	public AirfoilTool( ProgramProduct product, Asset asset ) {
		super( product, asset );

		Screen screen = Screen.getPrimary();
		double dpi = screen.getDpi();
		this.renderer = new FxRenderer2d( 960, 540 );
		this.renderer.setDpi( dpi, dpi );
		this.renderer.setZoom( 10, 10 );
		this.renderer.setViewpoint( 0.5, 0.1 );
		((FxRenderer2d)this.renderer).widthProperty().bind( this.widthProperty() );
		((FxRenderer2d)this.renderer).heightProperty().bind( this.heightProperty() );
		((FxRenderer2d)this.renderer).widthProperty().addListener( ( p, o, n ) -> repaint() );
		((FxRenderer2d)this.renderer).heightProperty().addListener( ( p, o, n ) -> repaint() );

		getChildren().add( new BorderPane( (Node)renderer ) );

		runPauseAction = new RunPauseAction( getProgram(), this );
		resetAction = new ResetAction( getProgram(), this );
	}

	@Override
	protected void ready( OpenAssetRequest request ) throws ToolException {
		super.ready( request );
		setGraphic( getProgram().getIconLibrary().getIcon( "flow" ) );
		setTitle( Rb.textOr( getProduct(), "asset", "airfoil2d-name", "Flow" ) );

		// Register airfoil (asset model) event handlers...
		//getAirfoil().register( Flow2D.AIRFOIL, ( e ) -> getAirfoil().getPathSolver().reset() );
	}

	@Override
	protected void open( OpenAssetRequest request ) {
		repaint();
	}

	private void repaint() {
		this.renderer.clear();
		if( !getAsset().isLoaded() ) return;

		Airfoil airfoil = getAssetModel();

		Path airfoilLines = new Path( 1, 0 );
		airfoil.getStationPoints().forEach( p -> airfoilLines.line( p.getX(), p.getY() ) );
		renderer.draw( airfoilLines, new Pen( Color.CYAN, 0.001 ) );

		airfoil.getUpperInflections().forEach( this::dot );
		airfoil.getLowerInflections().forEach( this::dot );
	}

	private void dot( Point2D point ) {
		double r = 0.005;
		renderer.fill( new Ellipse( point.x, point.y, r, r ), new Pen( Color.YELLOW, 0.001 ) );
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
