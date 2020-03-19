package com.avereon.aveon;

import com.avereon.util.Log;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.ProgramTool;
import com.avereon.xenon.UiFactory;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.task.Task;
import com.avereon.xenon.workpane.ToolException;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
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
		getChildren().addAll( layout );

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

	private void setAirfoil( Airfoil airfoil ) {
		this.airfoil = airfoil;

		if( airfoilShape != null ) getChildren().remove( airfoilShape );

		boolean first = true;
		Path path = new Path();
		for( Point2D point : airfoil.getPoints() ) {
			System.err.println( "point=" + point );
			if( first ) {
				path.getElements().add( new MoveTo( point.getX(), point.getY() ) );
				first = false;
			} else {
				path.getElements().add( new LineTo( point.getX(), point.getY() ) );
			}
		}
		path.getElements().add( new ClosePath() );

		path.setFill( Color.BLACK );
		path.setStrokeWidth( 0 );
		path.setStroke( Color.RED );
		//path.setStrokeType( StrokeType.INSIDE );

		path.translateXProperty().bind( widthProperty().multiply( 0.5 ) );
		path.translateYProperty().bind( heightProperty().multiply( 0.5 ) );
		path.scaleXProperty().bind( widthProperty().multiply( 0.5 ) );
		path.scaleYProperty().bind( widthProperty().multiply( -0.5 ) );
		//path.strokeWidthProperty().bind( widthProperty().multiply( 0.5 ) );

		getChildren().addAll( this.airfoilShape = path );
	}

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
