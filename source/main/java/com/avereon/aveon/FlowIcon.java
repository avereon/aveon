package com.avereon.aveon;

import com.avereon.zarra.image.RenderedIcon;

public class FlowIcon extends RenderedIcon {

	@Override
	protected void render() {
		double r = 5;
		startPath();
		addOval( g( 16 ), g( 16 ), g( r ), g( r ) );
		closePath();
		fill();

		double aa = 3;
		double a = 9;
		double b = 6;
		startPath();
		moveTo( g( aa ), g( 13 ) );
		getGraphicsContext2D().bezierCurveTo( g( a ), g( 12 ), g( 16 - b ), g( 7 ), g( 16 ), g( 7 ) );
		getGraphicsContext2D().bezierCurveTo( g( 16 + b ), g( 7 ), g( 32 - a ), g( 12 ), g( 32 - aa ), g( 13 ) );

		moveTo( g( aa ), g( 19 ) );
		getGraphicsContext2D().bezierCurveTo( g( a ), g( 20 ), g( 16 - b ), g( 25 ), g( 16 ), g( 25 ) );
		getGraphicsContext2D().bezierCurveTo( g( 16 + b ), g( 25 ), g( 32 - a ), g( 20 ), g( 32 - aa ), g( 19 ) );

		setDrawWidth( g( 2 ) );
		draw( getPrimaryPaint() );
	}

	public static void main( String[] args ) {
		proof( new FlowIcon() );
	}

}
