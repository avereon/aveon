package com.avereon.aveon;

import com.avereon.math.Arithmetic;
import com.avereon.util.Log;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.ProgramTool;
import com.avereon.xenon.UiFactory;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.task.Task;
import com.avereon.xenon.workpane.ToolException;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

public class FlowTool extends ProgramTool {

	private static final System.Logger log = Log.get();

	private static final double DEFAULT_SCALE = 0.5;

	private String url = "http://airfoiltools.com/airfoil/lednicerdatfile?airfoil=clarky-il";

	private Button foilButton;

	private Airfoil airfoil;

	private Group layers;

	private Group gridLayer;

	private Group foilShapeLayer;

	private Group referenceLayer;

	private Group foilOutlineLayer;

	private Group foilInflectionPointsLayer;

	private Path airfoilShape;

	private double scale = DEFAULT_SCALE;

	public FlowTool( ProgramProduct product, Asset asset ) {
		super( product, asset );

		setGraphic( getProgram().getIconLibrary().getIcon( "flow" ) );
		setTitle( "Flow" );

		foilButton = new Button( "Airfoil" );

		HBox buttonBox = new HBox( foilButton );
		buttonBox.setSpacing( UiFactory.PAD );

		BorderPane layout = new BorderPane();
		layout.setPadding( new Insets( UiFactory.PAD ) );
		layout.setTop( buttonBox );

		gridLayer = new Group();
		foilShapeLayer = new Group();
		referenceLayer = new Group();
		foilOutlineLayer = new Group();
		foilInflectionPointsLayer = new Group();

		layers = new Group( gridLayer, foilShapeLayer, referenceLayer, foilOutlineLayer, foilInflectionPointsLayer );
		scaleAndTranslate( layers );

		getChildren().addAll( layout, layers );

		//foilOutlineLayer.setVisible( false );

		foilButton.setOnAction( e -> requestAirfoilData() );
	}

	public String getUrl() {
		return url;
	}

	public void setUrl( String url ) {
		this.url = url;
		getProgram().getTaskManager().submit( Task.of( "Load airfoil", this::loadAirfoilData ) );
	}

	@Override
	protected void assetRefreshed() throws ToolException {
		// The asset has been refreshed...
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

		generateRuler();

		// Foil shape
		Path shape = generatePath( airfoil.getPoints(), true );
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

	private void generateRuler() {
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
			line.setStroke( Color.RED );
			setStrokeWidth( line );
			gridLayer.getChildren().add( line );
		}

		// Vertical lines
		for( double x = left; x <= right; x += horizontalInterval ) {
			Line line = new Line( x, top, x, bot );
			line.setStroke( Color.RED );
			setStrokeWidth( line );
			gridLayer.getChildren().add( line );
		}
	}

	private void setStrokeWidth( Shape shape ) {
		shape.strokeWidthProperty().bind( Bindings.divide( 1 / scale, widthProperty() ).divide( getScene().getWindow().getRenderScaleX() ) );
	}

	// THREAD FX Platform
	private void requestAirfoilData() {
		TextInputDialog dialog = new TextInputDialog( getUrl() );
		dialog.initOwner( getProgram().getWorkspaceManager().getActiveStage() );
		dialog.setGraphic( getProgram().getIconLibrary().getIcon( "flow" ) );
		dialog.setTitle( "Airfoil" );
		dialog.setHeaderText( "Choose an airfoil..." );
		dialog.setContentText( "URL:" );
		Optional<String> optional = dialog.showAndWait();
		if( optional.isEmpty() ) return;
		setUrl( optional.get() );
	}

	// THREAD Task
	private void loadAirfoilData() {
		try {
			Airfoil airfoil = new AirfoilCodec().load( new URL( getUrl() ).openStream() );
			Platform.runLater( () -> setAirfoil( airfoil ) );
		} catch( IOException exception ) {
			log.log( Log.ERROR, "Unable to load airfoil data", exception );
		}
	}

}
