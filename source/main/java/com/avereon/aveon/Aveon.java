package com.avereon.aveon;

import com.avereon.util.Log;
import com.avereon.xenon.Mod;
import com.avereon.xenon.ToolRegistration;

public class Aveon extends Mod {

	private static final System.Logger log = Log.get();

	private FlowAssetType flowAssetType;

	public Aveon() {
		flowAssetType = new FlowAssetType( this );
	}

	@Override
	public void startup() {
		registerIcon( "aveon", FlowIcon.class );
		registerIcon( "flow", FlowIcon.class );

		registerAction( this.rb(), "toggle-grid" );
		registerAction( this.rb(), "toggle-airfoil" );
//		registerAction( this.rb(), "toggle-reference-points" );
//		registerAction( this.rb(), "toggle-reference-lines" );
//		registerAction( this.rb(), "toggle-pressure-field" );
//		registerAction( this.rb(), "toggle-velocity-field" );
//		registerAction( this.rb(), "toggle-stream-field" );

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

		unregisterIcon( "flow", FlowIcon.class );
		unregisterIcon( "aveon", FlowIcon.class );
	}

}
