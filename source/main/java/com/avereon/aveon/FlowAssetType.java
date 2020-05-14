package com.avereon.aveon;

import com.avereon.util.Log;
import com.avereon.xenon.Program;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.AssetType;
import javafx.scene.control.TextInputDialog;

public class FlowAssetType extends AssetType {

	private static final System.Logger log = Log.get();

	public FlowAssetType( ProgramProduct product ) {
		super( product, "flow2d" );
		setDefaultCodec( new FlowCodec( product ) );
	}

	@Override
	public String getKey() {
		return getDefaultCodec().getKey();
	}

	@Override
	public boolean assetInit( Program program, Asset asset ) {
		Flow2D flow = new Flow2D();
		asset.setModel( flow );
		return true;
	}

	@Override
	public boolean assetUser( Program program, Asset asset ) {
		String url = requestAirfoilData( "http://airfoiltools.com/airfoil/lednicerdatfile?airfoil=e376-il" );
		asset.getSettings().set( FlowTool.AIRFOIL_URL, url );
		return true;
	}

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
