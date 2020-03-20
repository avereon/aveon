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
import javafx.scene.Node;
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

	private Group group;

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

		group = new Group();
		scaleAndTranslate( group );

		getChildren().addAll( layout, group );

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

		if( airfoilShape != null ) group.getChildren().clear();
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

		// Fill
		path.setFill( Color.web( "#00000080" ) );

		// Stroke
		path.setStrokeWidth( 0 );
		path.setStroke( Color.YELLOW );
		path.setStrokeType( StrokeType.INSIDE );
		setStrokeWidth( path );
		group.getChildren().addAll( this.airfoilShape = path );

		// Inflections
		for( Point2D i : airfoil.findInflectionsY( airfoil.getUpper() ) ) {
			Circle dot = new Circle( i.getX(), i.getY(), 0.002, Color.YELLOW );
			group.getChildren().add( dot );
		}
		for( Point2D i : airfoil.findInflectionsY( airfoil.getLower() ) ) {
			Circle dot = new Circle( i.getX(), i.getY(), 0.002, Color.YELLOW );
			group.getChildren().add( dot );
		}
	}

	private void generateRuler() {
		double top = 0.1;
		double bot = -0.05;
		for( double y = top; y >= bot; y+= -0.05 ) {
			Line l = new Line( 0, y, 1, y );
			l.setStroke( Color.RED );
			group.getChildren().add( l );
		}

		for( int index = 0; index < 11; index++ ) {
			Line l = new Line( 0.1 * index, top, 0.1 * index, bot );
			l.setStroke( Color.RED );
			group.getChildren().add( l );
		}

		for( Node node : group.getChildrenUnmodifiable() ) {
			setStrokeWidth( (Shape)node );
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
