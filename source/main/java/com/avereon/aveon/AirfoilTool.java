package com.avereon.aveon;

import com.avereon.geometry.Point2D;
import com.avereon.marea.RenderUnit;
import com.avereon.marea.Pen;
import com.avereon.marea.ShapeRenderer2d;
import com.avereon.marea.fx.FxRenderer2d;
import com.avereon.marea.geom.Ellipse;
import com.avereon.marea.geom.Line;
import com.avereon.marea.geom.Path;
import com.avereon.product.Rb;
import com.avereon.skill.RunPauseResettable;
import com.avereon.xenon.ProgramAction;
import com.avereon.xenon.ProgramTool;
import com.avereon.xenon.XenonProgramProduct;
import com.avereon.xenon.action.common.ResetAction;
import com.avereon.xenon.action.common.RunPauseAction;
import com.avereon.xenon.resource.Resource;
import com.avereon.xenon.resource.OpenAssetRequest;
import com.avereon.xenon.task.Task;
import com.avereon.xenon.task.TaskEvent;
import com.avereon.xenon.workpane.ToolException;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Screen;
import lombok.CustomLog;

@CustomLog
public class AirfoilTool extends ProgramTool implements RunPauseResettable {

	private final ProgramAction runPauseAction;

	private final ProgramAction resetAction;

	private final ShapeRenderer2d renderer;

	private AirfoilPathSolver solver;

	public AirfoilTool( XenonProgramProduct product, Resource resource ) {
		super( product, resource );
		setGraphic( getProgram().getIconLibrary().getIcon( "airfoil" ) );
		setTitle( Rb.textOr( getProduct(), "asset", "airfoil2d-name", "Flow" ) );

		Screen screen = Screen.getPrimary();
		double dpi = screen.getDpi();
		this.renderer = new FxRenderer2d( 960, 540 );
		this.renderer.setLengthUnit( RenderUnit.CM );
		this.renderer.setZoomStep( 0.2 );
		this.renderer.setDpi( dpi, dpi );
		this.renderer.setZoom( 10, 10 );
		this.renderer.setViewpoint( 0.5, 0.0 );

		// Force the renderer to always be the tool size
		this.renderer.widthProperty().bind( widthProperty() );
		this.renderer.heightProperty().bind( heightProperty() );

		// Repaint when certain events happen
		this.renderer.widthProperty().addListener( ( p, o, n ) -> render() );
		this.renderer.heightProperty().addListener( ( p, o, n ) -> render() );
		this.renderer.zoomXProperty().addListener( ( p, o, n ) -> render() );
		this.renderer.zoomYProperty().addListener( ( p, o, n ) -> render() );
		this.renderer.viewpointXProperty().addListener( ( p, o, n ) -> render() );
		this.renderer.viewpointYProperty().addListener( ( p, o, n ) -> render() );

		// Add the renderer to the tool
		getChildren().add( (Node)renderer );

		runPauseAction = new RunPauseAction( getProgram(), this );
		resetAction = new ResetAction( getProgram(), this );
	}

	public Airfoil getAirfoil() {
		return getAssetModel();
	}

	@Override
	protected void ready( OpenAssetRequest request ) throws ToolException {
		super.ready( request );
		setGraphic( getProgram().getIconLibrary().getIcon( "airfoil" ) );
		setTitle( request.getResource().getName() );

		this.solver = new AirfoilPathSolver( getAirfoil() );

		// Register airfoil (asset model) event handlers...
		//getAirfoil().register( Flow2D.AIRFOIL, ( e ) -> this.solver.reset() );
	}

	@Override
	protected void open( OpenAssetRequest request ) {
		render();
	}

	private void render() {
		this.renderer.clear();
		if( !getResource().isLoaded() ) return;

		Airfoil airfoil = getAssetModel();

		// Airfoil chord
		renderer.draw( new Line( 0, 0, 1, 0 ), new Pen( Color.GREEN, 0.001 ) );

		// Airfoil definition surface
		Path airfoilDefinitionLines = new Path( 1, 0 );
		airfoil.getDefinitionPoints().forEach( p -> airfoilDefinitionLines.line( p.getX(), p.getY() ) );
		renderer.draw( airfoilDefinitionLines, new Pen( Color.GRAY, 0.001 ) );
		//airfoil.getDefinitionPoints().forEach( p -> dot( p, Color.RED ));

		// Airfoil inflection points
		airfoil.getUpperInflections().forEach( this::dot );
		airfoil.getLowerInflections().forEach( this::dot );

		// Airfoil camber path
		Path camberLines = new Path( 0, 0 );
		airfoil.getCamber().forEach( p -> camberLines.line( p.getX(), p.getY() ) );
		renderer.draw( camberLines, new Pen( Color.GRAY, 0.001 ) );

		// Airfoil camber station
		Point2D mc = airfoil.getMaxCamber();
		renderer.draw( new Line( mc.x, mc.y, mc.x, 0 ), new Pen( Color.CYAN, 0.001 ) );

		// Airfoil thickness stations
		Point2D tu = airfoil.getThicknessUpper();
		Point2D tl = airfoil.getThicknessLower();
		renderer.draw( new Line( tu.x, tu.y, tl.x, tl.y ), new Pen( Color.MEDIUMPURPLE, 0.001 ) );
		Point2D ut = airfoil.getUpperThickness();
		renderer.draw( new Line( ut.x, ut.y, ut.x, 0 ), new Pen( Color.GREEN, 0.001 ) );
		Point2D lt = airfoil.getLowerThickness();
		renderer.draw( new Line( lt.x, lt.y, lt.x, 0 ), new Pen( Color.RED, 0.001 ) );

		// Airfoil analysis surface
		Path airfoilAnalysisLines = new Path( 1, 0 );
		airfoil.getAnalysisPoints().forEach( p -> airfoilAnalysisLines.line( p.getX(), p.getY() ) );
		renderer.draw( airfoilAnalysisLines, new Pen( Color.YELLOW, 0.001 ) );
		//airfoil.getDefinitionPoints().forEach( p -> dot( p, Color.LAVENDER ));

		// Airfoil panel points
		//		airfoil.getUpperPoints().forEach( p -> this.dot( p, Color.LAVENDER ) );
		//		airfoil.getLowerPoints().forEach( p -> this.dot( p, Color.LAVENDER ) );

		//		airfoil.getUpperPoints().forEach( c -> {
		//			renderer.draw( new Curve( c.ax, c.ay, c.bx, c.by, c.cx, c.cy, c.dx, c.dy ), new Pen( Color.RED, 0.001 ) );
		//			dot( Point2D.of( c.ax, c.ay ));
		//			dot( Point2D.of( c.bx, c.by ));
		//			dot( Point2D.of( c.cx, c.cy ));
		//			dot( Point2D.of( c.dx, c.dy ));
		//		} );
		//		airfoil.getLowerPoints().forEach( c -> {
		//			renderer.draw( new Curve( c.ax, c.ay, c.bx, c.by, c.cx, c.cy, c.dx, c.dy ), new Pen( Color.RED, 0.001 ) );
		//			dot( Point2D.of( c.ax, c.ay ));
		//			dot( Point2D.of( c.bx, c.by ));
		//			dot( Point2D.of( c.cx, c.cy ));
		//			dot( Point2D.of( c.dx, c.dy ));
		//		} );
	}

	private void dot( Point2D point ) {
		dot( point, Color.YELLOW );
	}

	private void dot( Point2D point, Paint paint ) {
		dot( point, paint, 0.002 );
	}

	private void dot( Point2D point, Paint paint, double size ) {
		renderer.fill( new Ellipse( point.x, point.y, size, size ), new Pen( paint, 0.0 ) );
	}

	@Override
	protected void activate() {
		pushAction( "runpause", runPauseAction );
		pushAction( "reset", resetAction );
		pushTools( "reset runpause" );

		// Set the current action state
		if( getResource().isLoaded() ) {
			//AirfoilPathSolver solver = this.solver;
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
		Task<?> t = Task.of( "Airfoil path solver", solver );
		t.register( TaskEvent.FINISH, ( e ) -> {
			log.atInfo().log( "Airfoil path solver complete." );
			runPauseAction.setState( "run" );
			render();
		} );
		getProgram().getTaskManager().submit( t );
	}

	public void pause() {
		this.solver.pause();
	}

	public void reset() {
		this.solver.reset();
	}

}
