package com.avereon.aveon;

import com.avereon.util.TextUtil;
import com.avereon.xenon.Xenon;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.AssetException;
import com.avereon.xenon.asset.AssetType;
import javafx.scene.control.TextInputDialog;
import lombok.CustomLog;

import java.net.URI;
import java.net.URISyntaxException;

@CustomLog
public class AirfoilAssetType extends AssetType {

	public AirfoilAssetType( ProgramProduct product ) {
		super( product, "airfoil2d" );
		setDefaultCodec( new AirfoilStationPointCodec() );
	}

	@Override
	public String getKey() {
		return getDefaultCodec().getKey();
	}

	@Override
	public boolean assetOpen( Xenon program, Asset asset ) throws AssetException {
		Airfoil airfoil = new Airfoil();
		asset.setModel( airfoil );
		return true;
	}

	@Override
	public boolean assetNew( Xenon program, Asset asset ) throws AssetException {
		try {
			String url = requestAirfoilData( "http://airfoiltools.com/airfoil/lednicerdatfile?airfoil=e376-il" );
			if( TextUtil.isEmpty( url ) ) return false;
			asset.setUri( new URI( url ) );
		} catch( URISyntaxException exception ) {
			throw new AssetException( asset, exception );
		}
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
