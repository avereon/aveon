package com.avereon.aveon;

import com.avereon.util.Log;
import com.avereon.xenon.Program;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.AssetException;
import com.avereon.xenon.asset.AssetType;
import javafx.scene.control.TextInputDialog;

import java.net.MalformedURLException;
import java.net.URL;

public class FlowAssetType extends AssetType {

	private static final String MEDIA_TYPE = "application/vnd.avereon.aveon.flow";

	private static final System.Logger log = Log.get();

	private TextInputDialog dialog = null;

	public FlowAssetType( ProgramProduct product ) {
		super( product, "flow2d" );
		setDefaultCodec( new FlowCodec( product ) );
	}

	@Override
	public boolean assetInit( Program program, Asset asset ) {
		Flow2D flow = new Flow2D();

		asset.setModel( flow );

		return true;
	}

	@Override
	public boolean assetUser( Program program, Asset asset ) throws AssetException {
		// FIXME So...interesting problem, this is called when restoring a "new" asset also

		URL url;
		try {
			url = new URL( requestAirfoilData( "http://airfoiltools.com/airfoil/lednicerdatfile?airfoil=e376-il" ) );
			((Flow2D)asset.getModel()).setAirfoilUrl( url );
		} catch( MalformedURLException exception ) {
			log.log( Log.ERROR, exception );
			return false;
		}

		return true;
	}

	// THREAD FX Platform
	private String requestAirfoilData( String url ) {
		TextInputDialog dialog = new TextInputDialog( url );
		dialog.initOwner( getProgram().getWorkspaceManager().getActiveStage() );
		dialog.setGraphic( getProgram().getIconLibrary().getIcon( "flow" ) );
		dialog.setTitle( "Airfoil" );
		dialog.setHeaderText( "Choose an airfoil..." );
		dialog.setContentText( "URL:" );
		return dialog.showAndWait().orElse( "" );
	}

}
