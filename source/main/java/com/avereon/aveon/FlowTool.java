package com.avereon.aveon;

import com.avereon.curve.math.Arithmetic;
import com.avereon.geometry.Point2D;
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
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import lombok.CustomLog;

import java.io.IOException;
import java.net.URL;
import java.util.List;

@CustomLog
public class FlowTool extends ProgramTool implements RunPauseResettable {

	static final String AIRFOIL_URL = "airfoil-url";

	private static final double DEFAULT_SCALE = 0.8;

	private final double scale = DEFAULT_SCALE;

	private Airfoil airfoil;

	private final Group gridLayer;

	private final Group foilShapeLayer;

	private final Group referenceLayer;

	private final Group foilOutlineLayer;

	private final Group foilInflectionPointsLayer;

	private final Paint gridPaint = Color.web( "#80808080" );

	private final ProgramAction runPauseAction;

	private final ProgramAction resetAction;

	public FlowTool( XenonProgramProduct product, Resource resource ) {
		super( product, resource );

		gridLayer = new Group();
		foilShapeLayer = new Group();
		referenceLayer = new Group();
		foilOutlineLayer = new Group();
		foilInflectionPointsLayer = new Group();

		Group layers = new Group( gridLayer, foilShapeLayer, referenceLayer, foilOutlineLayer, foilInflectionPointsLayer );
		scaleAndTranslate( layers );

		getChildren().addAll( layers );

		//foilOutlineLayer.setVisible( false );

		runPauseAction = new RunPauseAction( getProgram(), this );
		resetAction = new ResetAction( getProgram(), this );
	}

	@Override
	protected void ready( OpenAssetRequest request ) {
		setGraphic( getProgram().getIconLibrary().getIcon( "flow" ) );
		setTitle( Rb.textOr( getProduct(), "asset", "flow2d-name", "Flow" ) );
	}

	@Override
	protected void open( OpenAssetRequest request ) {
		// Load the initial state from the flow (asset model)
		if( getFlow().getAirfoil() == null || !getFlow().getAirfoil().isAnalyzed() ) {
			String airfoilUrl = getAssetSettings().get( "airfoil-url" );
			if( airfoilUrl == null ) airfoilUrl = getSettings().get( AIRFOIL_URL );
			loadAirfoilPoints( airfoilUrl );
		}

		// Register flow (asset model) event handlers...
		getFlow().register( Flow2D.AIRFOIL, ( e ) -> getFlow().reset() );
		//flow.register( Flow2D.PRESSURE_FIELD, ( e ) -> redrawPressureField() );
		//flow.register( Flow2D.VELOCITY_FIELD, ( e ) -> redrawVelocityField() );
		//flow.register( Flow2D.STREAM_FIELD, ( e ) -> redrawStreamField() );
	}

	@Override
	protected void activate() {
		pushAction( "runpause", runPauseAction );
		pushAction( "reset", resetAction );
		pushTools( "toggle-grid toggle-airfoil | reset runpause" );

		// Set the current action state
		if( getResource().isLoaded() ) {
			FlowSolver solver = getFlow().getFlowSolver();
			runPauseAction.setState( solver != null && solver.isRunning() ? "pause" : "run" );
		}
	}

	@Override
	protected void conceal() {
		pullTools();
		pullAction( "runpause", runPauseAction );
		pullAction( "reset", resetAction );
	}

	public void run() {
		final FlowSolver solver = new SimpleFlowSolver( getFlow(), getProgram().getTaskManager().getExecutor() );

		Task<?> t = Task.of( "Start flow solver", solver );
		t.register( TaskEvent.FINISH, ( e ) -> {
			log.atInfo().log( "Flow solver complete!" );
			runPauseAction.setState( "run" );
		} );
		getProgram().getTaskManager().submit( t );
	}

	public void pause() {
		getFlow().getFlowSolver().pause();
	}

	public void reset() {
		getFlow().getFlowSolver().reset();
	}

	private Flow2D getFlow() {
		return (Flow2D)getAssetModel();
	}

	private void scaleAndTranslate( Parent parent ) {
		parent.scaleXProperty().bind( widthProperty().multiply( scale ) );
		parent.scaleYProperty().bind( widthProperty().multiply( -scale ) );
		parent.translateXProperty().bind( widthProperty().multiply( 0.5 ) );
		parent.translateYProperty().bind( heightProperty().multiply( 0.5 ) );
	}

	private Path generatePath( List<Point2D> points, boolean close ) {
		boolean first = true;
		Path path = new Path();
		for( Point2D point : points ) {
			if( first ) {
				path.getElements().add( new MoveTo( point.getX(), point.getY() ) );
				first = false;
			} else {
				path.getElements().add( new LineTo( point.getX(), point.getY() ) );
			}
		}
		if( close ) path.getElements().add( new ClosePath() );
		path.setStroke( null );
		path.setFill( null );
		return path;
	}

//	private Path generatePath( List<Cubic2D> curves ) {
//		Path path = new Path();
//		Cubic2D g = curves.get( 0 );
//		path.getElements().add( new MoveTo( g.ax, g.ay ) );
//		for( Cubic2D c : curves ) {
//			path.getElements().add( new CubicCurveTo( c.bx, c.by, c.cx, c.cy, c.dx, c.dy ) );
//		}
//		return path;
//	}

	private void setAirfoil( Airfoil airfoil ) {
		this.airfoil = airfoil;
		if( airfoil == null ) return;

		generateGrid();

		// Foil shape
		Path stationPointShape = generatePath( airfoil.getDefinitionPoints(), true );
		stationPointShape.setFill( Color.web( "#00000080" ) );
		foilShapeLayer.getChildren().clear();
		foilShapeLayer.getChildren().add( stationPointShape );

		// Foil outline
		Path stationPointOutline = new Path( stationPointShape.getElements() );
		stationPointOutline.setStroke( Color.GRAY );
		//stationPointOutline.setStrokeType( StrokeType.INSIDE );
		setStrokeWidth( stationPointOutline );
		foilOutlineLayer.getChildren().clear();
		foilOutlineLayer.getChildren().add( stationPointOutline );

		// Foil shape
		//Path upperCurveShape = generatePath( airfoil.getUpperPoints() );
		//Path lowerCurveShape = generatePath( airfoil.getLowerPoints() );

		//		// Foil outline
		//		Path upperCurveOutline = new Path( upperCurveShape.getElements() );
		//		Path lowerCurveOutline = new Path( lowerCurveShape.getElements() );
		//		upperCurveOutline.setStroke( Color.ORANGE );
		//		lowerCurveOutline.setStroke( Color.ORANGE );
		//		setStrokeWidth( upperCurveOutline );
		//		setStrokeWidth( lowerCurveOutline );
		//		foilOutlineLayer.getChildren().add( upperCurveOutline );
		//		foilOutlineLayer.getChildren().add( lowerCurveOutline );

		CubicCurve c1 = new CubicCurve( 0, 0, 0, 0.1, 0.1, 0.2, 0.4, 0.2 );
		CubicCurve c2 = new CubicCurve( 0, 0, 0, 0.04, 0.02, 0.2, 0.4, 0.2 );
		CubicCurve c3 = new CubicCurve( 0, 0, 0, 0.19, 0.32, 0.2, 0.4, 0.2 );
		CubicCurve c4 = new CubicCurve( 0, 0, 0, 0.15, 0.2, 0.2, 0.4, 0.2 );
		c1.setStroke( Color.RED );
		c2.setStroke( Color.ORANGE );
		c3.setStroke( Color.MAGENTA );
		c4.setStroke( Color.GREENYELLOW );
		c1.setFill( null );
		c2.setFill( null );
		c3.setFill( null );
		c4.setFill( null );
		setStrokeWidth( c1 );
		setStrokeWidth( c2 );
		setStrokeWidth( c3 );
		setStrokeWidth( c4 );
		foilOutlineLayer.getChildren().addAll( c1, c2, c3, c4 );

		// Thickness
		Point2D thicknessUpper = airfoil.getUpperThickness();
		Point2D thicknessLower = airfoil.getLowerThickness();
		Line thickness = new Line( thicknessUpper.getX(), thicknessUpper.getY(), thicknessLower.getX(), thicknessLower.getY() );
		thickness.setStroke( Color.MAGENTA );
		setStrokeWidth( thickness );
		referenceLayer.getChildren().clear();
		//referenceLayer.getChildren().add( thickness );

		// Camber
		Path camber = generatePath( airfoil.getCamber(), false );
		camber.setStroke( Color.MAGENTA );
		setStrokeWidth( camber );
		//referenceLayer.getChildren().add( camber );

		// Max camber
		//referenceLayer.getChildren().add( generateDot( airfoil.getMaxCamber(), Color.MAGENTA ) );

		// Inflections
		double r = 0.001;
		foilInflectionPointsLayer.getChildren().clear();
		for( Point2D i : airfoil.getUpperInflections() ) {
			foilInflectionPointsLayer.getChildren().add( generateDot( i, r, Color.YELLOW ) );
		}
		for( Point2D i : airfoil.getLowerInflections() ) {
			foilInflectionPointsLayer.getChildren().add( generateDot( i, r, Color.YELLOW ) );
		}
	}

	private Circle generateDot( Point2D point, double r, Paint fill ) {
		return new Circle( point.getX(), point.getY(), r, fill );
	}

	private void generateGrid() {
		gridLayer.getChildren().clear();

		double horizontalInterval = 0.1;
		double verticalInterval = 0.05;

		double left = 0;
		double right = 1;
		double top = Arithmetic.nearestAbove( airfoil.getMaxY(), verticalInterval );
		double bot = Arithmetic.nearestBelow( airfoil.getMinY(), verticalInterval );

		// Horizontal lines
		for( double y = bot; y <= top; y += verticalInterval ) {
			Line line = new Line( left, y, right, y );
			line.setStroke( gridPaint );
			setStrokeWidth( line );
			gridLayer.getChildren().add( line );
		}

		// Vertical lines
		for( double x = left; x <= right; x += horizontalInterval ) {
			Line line = new Line( x, top, x, bot );
			line.setStroke( gridPaint );
			setStrokeWidth( line );
			gridLayer.getChildren().add( line );
		}
	}

	private void setStrokeWidth( Shape shape ) {
		if( shape == null ) return;
		shape.setStrokeLineCap( StrokeLineCap.ROUND );
		shape.setStrokeLineJoin( StrokeLineJoin.ROUND );
		shape.strokeWidthProperty().bind( Bindings.divide( 1 / scale, widthProperty() ).divide( getScene().getWindow().getRenderScaleX() ) );
	}

	// THREAD Task
	private void loadAirfoilPoints( String url ) {
		if( url == null ) return;
		try {
			getSettings().set( AIRFOIL_URL, url );
			Airfoil airfoil = AirfoilStationPointCodec.loadStationPoints( new URL( url.trim() ).openStream() );
			Platform.runLater( () -> setAirfoil( airfoil ) );
		} catch( IOException exception ) {
			log.atError( exception ).log( "Unable to load airfoil data" );
		}
	}

}
