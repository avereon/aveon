package com.avereon.aveon;

import com.avereon.xenon.Mod;
import com.avereon.xenon.ToolRegistration;
import lombok.CustomLog;

@CustomLog
public class Aveon extends Mod {

	private final FlowAssetType flowAssetType;

	public Aveon() {
		flowAssetType = new FlowAssetType( this );
	}

	@Override
	public void startup() {
		registerIcon( "aveon", new FlowIcon() );
		registerIcon( "flow", new FlowIcon() );

		registerAction( this, "toggle-grid" );
		registerAction( this, "toggle-airfoil" );
//		registerAction( this, "toggle-reference-points" );
//		registerAction( this, "toggle-reference-lines" );
//		registerAction( this, "toggle-pressure-field" );
//		registerAction( this, "toggle-velocity-field" );
//		registerAction( this, "toggle-stream-field" );

		registerAssetType( flowAssetType );
		registerTool( flowAssetType, new ToolRegistration( this, FlowTool.class ) );
	}

	@Override
	public void shutdown() {
		unregisterTool( flowAssetType, FlowTool.class );
		unregisterAssetType( flowAssetType );

//		unregisterAction( "toggle-stream-field" );
//		unregisterAction( "toggle-velocity-field" );
//		unregisterAction( "toggle-pressure-field" );
//		unregisterAction( "toggle-reference-lines" );
//		unregisterAction( "toggle-reference-points" );
		unregisterAction( "toggle-airfoil" );
		unregisterAction( "toggle-grid" );

		unregisterIcon( "flow", new FlowIcon() );
		unregisterIcon( "aveon", new FlowIcon() );
	}

}
