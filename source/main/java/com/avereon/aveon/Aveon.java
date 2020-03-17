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

		registerAction( this.rb(), "reset" );
		registerAction( this.rb(), "runpause" );

		registerAssetType( flowAssetType );
		registerTool( flowAssetType, new ToolRegistration( this, FlowTool.class ) );
	}

	@Override
	public void shutdown() {
		unregisterTool( flowAssetType, FlowTool.class );
		unregisterAssetType( flowAssetType );

		unregisterAction( "runpause" );
		unregisterAction( "reset" );

		unregisterIcon( "flow", FlowIcon.class );
		unregisterIcon( "aveon", FlowIcon.class );
	}

}
