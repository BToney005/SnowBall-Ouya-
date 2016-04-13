package com.example.test2;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Projectile extends GameObject {
	int dir;
	int speed = 10;
	int velY = 0, maxVelY = 5;
	int counter = 0;
	int dropRate = 30;
	
	int slot = -1; //Denotes who shot the projectile
	boolean active = true, grounded = false;
	public Projectile(int x, int y, int width, int height, int dir) {
		super(x, y, width, height);
		this.dir = dir;

		// TODO Auto-generated constructor stub
	}
	
	public Projectile(){
		this(0,0,10,10,0);
	}
	
	public void deactivate(){
		if(active){
			active = false;
			if(dir == 0)
				x += 5;
			else
				x -= 5;
		}
	}
	
	public boolean checkCollision( GameObject obj){
		if( obj instanceof Player )
			if(!((Player)obj).alive)
				return false;
		return x <= obj.x + obj.width && x + width >= obj.x &&
		      y <= obj.y + obj.height && y + height >= obj.y;
	}
	
	public void update(){
		if( active ){
			switch(dir){
				case 0: x-=speed; break;
				case 1: x+=speed; break;
			}
		}
		if( ! grounded ){
			if( x > 1750 )
				x = 0;
			else if( x + width < 0 )
				x = 1750 - width;
			if(active){
				if(counter == dropRate){
					if(velY < maxVelY )
						velY++;
					counter = 0;
				}
			}else{
				if(velY < maxVelY)
					velY++;
			}
			y+=velY;
			counter++;
		}
		
	}
	
	public void doDraw( Canvas canvas ){
		Paint p = new Paint();
		p.setColor(Color.WHITE);
		canvas.drawCircle( x+5, y+5, 5, p);
	}
	//TODO Create subclasses and update function

}
