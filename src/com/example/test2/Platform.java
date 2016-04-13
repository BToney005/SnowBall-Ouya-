package com.example.test2;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Platform extends GameObject {
	public Platform( int x, int y, int width, int height ){
		super(x,y,width,height);
	}
	
	@Override
	public void doDraw( Canvas canvas ){
		Paint p = new Paint();
		p.setColor( Color.GRAY );
		canvas.drawRect( x, y, x + width, y + height, p ); 
	}
}
