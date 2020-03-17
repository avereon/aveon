package com.avereon.aveon;

import com.avereon.product.Product;
import com.avereon.util.Log;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.Codec;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FlowCodec extends Codec {

	private static final System.Logger log = Log.get();

	private Product product;

	public FlowCodec( Product product ) {
		this.product = product;
		setDefaultExtension( "flow" );
	}

	@Override
	public String getKey() {
		return "com.avereon.aveon.codec.flow";
	}

	@Override
	public String getName() {
		return product.rb().text( "asset", "codec-flow-name" );
	}

	@Override
	public boolean canLoad() {
		return true;
	}

	@Override
	public boolean canSave() {
		return false;
	}

	@Override
	public void load( Asset asset, InputStream input ) throws IOException {
		log.log( Log.DEBUG, "Loading flow: " + asset );
	}

	@Override
	public void save( Asset asset, OutputStream output ) throws IOException {
		log.log( Log.DEBUG, "Saving flow: " + asset );
	}

}
