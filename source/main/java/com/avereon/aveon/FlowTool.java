package com.avereon.aveon;

import com.avereon.math.Arithmetic;
import com.avereon.skill.RunPauseResettable;
import com.avereon.util.Log;
import com.avereon.xenon.Action;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.ProgramTool;
import com.avereon.xenon.action.common.ResetAction;
import com.avereon.xenon.action.common.RunPauseAction;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.OpenAssetRequest;
import com.avereon.xenon.task.Task;
import com.avereon.xenon.task.TaskEvent;
import com.avereon.xenon.util.ActionUtil;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class FlowTool extends ProgramTool implements RunPauseResettable {

	static final String AIRFOIL_URL = "airfoil-url";

	private static final System.Logger log = Log.get();

	private static final double DEFAULT_SCALE = 0.5;

	private Airfoil airfoil;

	private Group gridLayer;

	private Group foilShapeLayer;

	private Group referenceLayer;

	private Group foilOutlineLayer;

	private Group foilInflectionPointsLayer;

	private double scale = DEFAULT_SCALE;

	private Paint gridPaint = Color.web( "#80808080" );

	private Action runPauseAction;

	private Action resetAction;

	public FlowTool( ProgramProduct product, Asset asset ) {
		super( product, asset );

		setGraphic( getProgram().getIconLibrary().getIcon( "flow" ) );
		setTitle( "Flow" );

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
	protected void assetReady( OpenAssetRequest request ) {
		Flow2D flow = getAssetModel();

		// Load the initial state from the flow (asset model)
		if( flow.getAirfoil() == null || !flow.getAirfoil().isAnalyzed() ) {
			String airfoilUrl = getAsset().getSettings().get( "airfoil-url" );
			if( airfoilUrl == null ) airfoilUrl = getSettings().get( AIRFOIL_URL );
			loadAirfoilPoints( airfoilUrl );
		}

		// Register flow (asset model) event handlers...
		flow.register( Flow2D.AIRFOIL, ( e ) -> flow.reset() );
		//flow.register( Flow2D.PRESSURE_FIELD, ( e ) -> redrawPressureField() );
		//flow.register( Flow2D.VELOCITY_FIELD, ( e ) -> redrawVelocityField() );
		//flow.register( Flow2D.STREAM_FIELD, ( e ) -> redrawStreamField() );
	}

	@Override
	protected void assetRefreshed() {
		// The asset has been refreshed...
	}

	@Override
	protected void display() {
		pushAction( "runpause", runPauseAction );
		pushAction( "reset", resetAction );

		pushToolActions( "toggle-grid", "toggle-airfoil", ActionUtil.SEPARATOR, "reset", "runpause" );

		// Set the current action state
		if( getAsset().isOpen() ) {
			Flow2D flow = getAssetModel();
			FlowSolver solver = flow.getFlowSolver();
			runPauseAction.setState( solver != null && solver.isRunning() ? "pause" : "run" );
		}
	}

	@Override
	protected void conceal() {
		pullToolActions();

		pullAction( "runpause", runPauseAction );
		pullAction( "reset", resetAction );
	}

	public void run() {
		Flow2D flow = getAssetModel();
		final FlowSolver solver = new SimpleFlowSolver( flow, getProgram().getTaskManager().getExecutor() );

		Task<?> t = Task.of( "Start flow solver", solver );
		t.register( TaskEvent.FINISH, ( e ) -> {
			log.log( Log.INFO, "Flow solver complete!" );
			runPauseAction.setState( "run" );
		} );
		getProgram().getTaskManager().submit( t );
	}

	public void pause() {
		((Flow2D)getAssetModel()).getFlowSolver().pause();
	}

	public void reset() {
		((Flow2D)getAssetModel()).getFlowSolver().reset();
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

	private void setAirfoil( Airfoil airfoil ) {
		this.airfoil = airfoil;
		if( airfoil == null ) return;

		generateGrid();

		// Foil shape
		Path shape = generatePath( airfoil.getStationPoints(), true );
		shape.setFill( Color.web( "#00000080" ) );
		foilShapeLayer.getChildren().clear();
		foilShapeLayer.getChildren().add( shape );

		// Foil outline
		Path outline = new Path( shape.getElements() );
		outline.setStroke( Color.YELLOW );
		outline.setStrokeType( StrokeType.INSIDE );
		setStrokeWidth( outline );
		foilOutlineLayer.getChildren().clear();
		foilOutlineLayer.getChildren().add( outline );

		// Thickness
		Point2D thicknessUpper = airfoil.getThicknessUpper();
		Point2D thicknessLower = airfoil.getThicknessLower();
		Line thickness = new Line( thicknessUpper.getX(), thicknessUpper.getY(), thicknessLower.getX(), thicknessLower.getY() );
		thickness.setStroke( Color.MAGENTA );
		setStrokeWidth( thickness );
		referenceLayer.getChildren().clear();
		referenceLayer.getChildren().add( thickness );

		// Camber
		Path camber = generatePath( airfoil.getCamber(), false );
		camber.setStroke( Color.MAGENTA );
		setStrokeWidth( camber );
		referenceLayer.getChildren().add( camber );

		// Max camber
		referenceLayer.getChildren().add( generateDot( airfoil.getMaxCamber(), Color.MAGENTA ) );

		// Inflections
		foilInflectionPointsLayer.getChildren().clear();
		for( Point2D i : airfoil.getUpperInflections() ) {
			foilInflectionPointsLayer.getChildren().add( generateDot( i, Color.YELLOW ) );
		}
		for( Point2D i : airfoil.getLowerInflections() ) {
			foilInflectionPointsLayer.getChildren().add( generateDot( i, Color.YELLOW ) );
		}
	}

	private Circle generateDot( Point2D point, Paint fill ) {
		return new Circle( point.getX(), point.getY(), 0.002, fill );
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
			Airfoil airfoil = new AirfoilStationPointCodec().loadStationPoints( new URL( url.trim() ).openStream() );
			Platform.runLater( () -> setAirfoil( airfoil ) );
		} catch( IOException exception ) {
			log.log( Log.ERROR, "Unable to load airfoil data", exception );
		}
	}

}
