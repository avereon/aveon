package com.avereon.aveon;

import com.avereon.xenon.Module;
import com.avereon.xenon.ToolRegistration;
import lombok.CustomLog;

@CustomLog
public class Aveon extends Module {

	private final AirfoilResourceType airfoilAssetType;

	private final FlowResourceType flowAssetType;

	public Aveon() {
		airfoilAssetType = new AirfoilResourceType( this );
		flowAssetType = new FlowResourceType( this );
	}

	@Override
	public void startup() {
		registerIcon( "airfoil", new FlowIcon() );
		registerIcon( "aveon", new FlowIcon() );
		registerIcon( "flow", new FlowIcon() );

		registerAction( this, "toggle-grid" );
		registerAction( this, "toggle-airfoil" );
//		registerAction( this, "toggle-reference-points" );
//		registerAction( this, "toggle-reference-lines" );
//		registerAction( this, "toggle-pressure-field" );
//		registerAction( this, "toggle-velocity-field" );
//		registerAction( this, "toggle-stream-field" );

		registerAssetType( airfoilAssetType );
		registerTool( airfoilAssetType, new ToolRegistration( this, AirfoilTool.class ) );

		registerAssetType( flowAssetType );
		registerTool( flowAssetType, new ToolRegistration( this, FlowTool.class ) );
	}

	@Override
	public void shutdown() {
		unregisterTool( flowAssetType, FlowTool.class );
		unregisterAssetType( flowAssetType );

		unregisterTool( airfoilAssetType, AirfoilTool.class );
		unregisterAssetType( airfoilAssetType );

//		unregisterAction( "toggle-stream-field" );
//		unregisterAction( "toggle-velocity-field" );
//		unregisterAction( "toggle-pressure-field" );
//		unregisterAction( "toggle-reference-lines" );
//		unregisterAction( "toggle-reference-points" );
		unregisterAction( "toggle-airfoil" );
		unregisterAction( "toggle-grid" );

		unregisterIcon( "flow", new FlowIcon() );
		unregisterIcon( "aveon", new FlowIcon() );
		unregisterIcon( "airfoil", new FlowIcon() );
	}

}
