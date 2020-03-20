package com.avereon.aveon;

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
import javafx.scene.shape.*;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

public class FlowTool extends ProgramTool {

	private static final System.Logger log = Log.get();

	private String url = "http://airfoiltools.com/airfoil/lednicerdatfile?airfoil=clarky-il";

	private Button foilButton;

	private Airfoil airfoil;

	private Group layers;

	private Group gridLayer;

	private Group foilShapeLayer;

	private Group foilOutlineLayer;

	private Group foilInflectionPointsLayer;

	private Path airfoilShape;

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
		foilOutlineLayer = new Group();
		foilInflectionPointsLayer = new Group();

		layers = new Group( gridLayer, foilShapeLayer, foilOutlineLayer, foilInflectionPointsLayer );
		scaleAndTranslate( layers );

		getChildren().addAll( layout, layers );

		foilOutlineLayer.setVisible( false );

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
		parent.scaleXProperty().bind( widthProperty().multiply( 0.5 ) );
		parent.scaleYProperty().bind( widthProperty().multiply( -0.5 ) );
		parent.translateXProperty().bind( widthProperty().multiply( 0.5 ) );
		parent.translateYProperty().bind( heightProperty().multiply( 0.5 ) );
	}

	private void setAirfoil( Airfoil airfoil ) {
		this.airfoil = airfoil;

		foilShapeLayer.getChildren().clear();
		if( airfoil == null ) return;

		generateRuler();

		boolean first = true;
		Path path = new Path();
		for( Point2D point : airfoil.getPoints() ) {
			if( first ) {
				path.getElements().add( new MoveTo( point.getX(), point.getY() ) );
				first = false;
			} else {
				path.getElements().add( new LineTo( point.getX(), point.getY() ) );
			}
		}
		path.getElements().add( new ClosePath() );
		path.setStrokeWidth( 0 );
		path.setFill( Color.web( "#00000080" ) );
		foilShapeLayer.getChildren().add( path );

		// Foil Outline
		Path outline = new Path( path.getElements() );
		outline.setStroke( Color.YELLOW );
		outline.setStrokeType( StrokeType.INSIDE );
		setStrokeWidth( outline );
		foilOutlineLayer.getChildren().add( outline );

		// Inflections
		for( Point2D i : airfoil.findInflectionsY( airfoil.getUpper() ) ) {
			Circle dot = new Circle( i.getX(), i.getY(), 0.002, Color.YELLOW );
			foilInflectionPointsLayer.getChildren().add( dot );
		}
		for( Point2D i : airfoil.findInflectionsY( airfoil.getLower() ) ) {
			Circle dot = new Circle( i.getX(), i.getY(), 0.002, Color.YELLOW );
			foilInflectionPointsLayer.getChildren().add( dot );
		}
	}

	private double nextUp( double anchor, double step ) {
		// NEXT Implement nextUp()
		return anchor;
	}

	private double nextDown( double anchor, double step ) {
		// NEXT Implement nextDown()
		return anchor;
	}

	private void generateRuler() {
		gridLayer.getChildren().clear();

		double horizontalInterval = 0.1;
		double verticalInterval = 0.02;

		double left = 0;
		double right = 1;
		double top = nextUp( airfoil.getMaxY(), verticalInterval );
		double bot = nextDown( airfoil.getMinY(), verticalInterval );

		// Horizontal lines
		for( double y = bot; y <= top; y += verticalInterval ) {
			Line l = new Line( left, y, right, y );
			l.setStroke( Color.RED );
			setStrokeWidth( l );
			gridLayer.getChildren().add( l );
		}

		// Vertical lines
		for( double x = left; x <= right; x += horizontalInterval ) {
			Line l = new Line( x, top, x, bot );
			l.setStroke( Color.RED );
			setStrokeWidth( l );
			gridLayer.getChildren().add( l );
		}
	}

	private void setStrokeWidth( Shape shape ) {
		shape.strokeWidthProperty().bind( Bindings.divide( 2, widthProperty() ) );
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
