module com.avereon.aveon {

	// Compile-time only
	requires static lombok;

	// Both compile-time and run-time
	requires com.avereon.curve;
	requires com.avereon.xenon;
	requires com.avereon.zenna;
	requires com.avereon.zarra;
	requires com.avereon.zevra;
	requires javafx.controls;
	requires javafx.graphics;

	opens com.avereon.aveon.bundles;

	exports com.avereon.aveon to com.avereon.xenon, com.avereon.zarra;
	exports com.avereon.geometry;

	provides com.avereon.xenon.Mod with com.avereon.aveon.Aveon;

}
