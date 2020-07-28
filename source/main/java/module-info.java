module com.avereon.aveon {

	requires com.avereon.xenon;
	requires com.avereon.rossa;
	requires com.avereon.zerra;
	requires com.avereon.zevra;
	requires javafx.controls;
	requires javafx.graphics;

	opens com.avereon.aveon.bundles;

	exports com.avereon.aveon to com.avereon.xenon, com.avereon.zerra;
	exports com.avereon.geometry;

	provides com.avereon.xenon.Mod with com.avereon.aveon.Aveon;

}
