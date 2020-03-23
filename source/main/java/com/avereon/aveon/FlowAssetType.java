package com.avereon.aveon;

import com.avereon.product.Product;
import com.avereon.xenon.asset.AssetType;

public class FlowAssetType extends AssetType {

	private static final String MEDIA_TYPE = "application/vnd.avereon.aveon.flow";

	public FlowAssetType( Product product ) {
		super( product, "flow2d" );
		setDefaultCodec( new FlowCodec( product ) );
	}

}
