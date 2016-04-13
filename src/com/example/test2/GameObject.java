package com.example.test2;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class GameObject {
	int x, y;
	int width, height;
	Rect rect;
	
	public GameObject( int x, int y, int width, int height ){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.rect = new Rect( x, y, x + width, y + height );
	}
	
	public boolean checkCollision( GameObject obj){
		if( obj instanceof Player )
			if(!((Player)obj).alive)
				return false;
		return x <= obj.x + obj.width && x + width >= obj.x &&
		      y <= obj.y + obj.height && y + height >= obj.y;
	}
	
	public void doDraw( Canvas canvas ){
		Paint p = new Paint();
		p.setColor( Color.WHITE );
		canvas.drawRect( x, y, x + width, y + height, p);
	}

}
