package com.avereon.aveon;

import com.avereon.product.Rb;
import com.avereon.xenon.resource.Resource;
import com.avereon.xenon.resource.Codec;
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
	public void load( Resource resource, InputStream input ) throws IOException {
		log.atDebug().log( "Loading flow: %s", resource );
	}

	@Override
	public void save( Resource resource, OutputStream output ) throws IOException {
		log.atDebug().log( "Saving flow: %s", resource );
	}

}
