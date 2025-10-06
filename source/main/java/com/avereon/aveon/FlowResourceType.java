package com.avereon.aveon;

import com.avereon.xenon.Xenon;
import com.avereon.xenon.XenonProgramProduct;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.ResourceType;
import javafx.scene.control.TextInputDialog;
import lombok.CustomLog;

@CustomLog
public class FlowResourceType extends ResourceType {

	public FlowResourceType( XenonProgramProduct product ) {
		super( product, "flow2d" );
		setDefaultCodec( new FlowCodec() );
	}

	@Override
	public String getKey() {
		return getDefaultCodec().getKey();
	}

	@Override
	public boolean assetOpen( Xenon program, Asset asset ) {
		Flow2D flow = new Flow2D();
		asset.setModel( flow );
		return true;
	}

	@Override
	public boolean assetNew( Xenon program, Asset asset ) {
		String url = requestAirfoilData( "http://airfoiltools.com/airfoil/lednicerdatfile?airfoil=e376-il" );
		program.getSettingsManager().getAssetSettings( asset ).set( FlowTool.AIRFOIL_URL, url );
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
