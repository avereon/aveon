package com.avereon.aveon;

import com.avereon.product.Rb;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.Codec;
import lombok.CustomLog;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@CustomLog
public class FlowCodec extends Codec {

	private static final String MEDIA_TYPE = "application/vnd.avereon.aveon.flow";

	public FlowCodec() {
		setDefaultExtension( "flow" );
		addSupported( Pattern.MEDIATYPE, MEDIA_TYPE );
	}

	@Override
	public String getKey() {
		return MEDIA_TYPE;
	}

	@Override
	public String getName() {
		return Rb.text( "asset", "codec-flow-name" );
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
		log.atDebug().log( "Loading flow: %s", asset );
	}

	@Override
	public void save( Asset asset, OutputStream output ) throws IOException {
		log.atDebug().log( "Saving flow: %s", asset );
	}

}
