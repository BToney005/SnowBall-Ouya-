package com.example.test2;

import java.io.InputStream;
import java.util.LinkedList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class Player extends GameObject {
	private final int slot;
	int dir; // 0 = west, 1 = east
	boolean onGround = false, jumping = false, falling = true; //HANDLES VERTICAL MOVEMENT
	boolean moveLeft = true, moveRight = true; //HANDLES HORIZONTAL MOVEMENT
	boolean[] buttons = new boolean[23];
	boolean ducking = false;
	boolean alive = true;
	int speed = 5; //HORIZONTAL MOVEMENT FACTOR
	int velocityY = 0; //VERTICAL MOVEMENT FACTOR
	int jumpHeight = -20; //MUST BE < 0
	int velocityYMax = 10; //MAX FALLING SPEED : JUST A COUNTER AND HAS NOTHING TO DO WOTH GRAVITY
	
	Bitmap bmp;
	Rect src = new Rect();
	
	int numProjectiles = 5;
	
	int score = 0;
	
	public Player( int x, int y, final int slot, Bitmap bmp ){
		super( x, y, 40, 50 );
		this.slot = slot;
		switch (slot){
			case 0: dir = 1; break; //right
			case 1: dir = 0; break; //left
			case 2: dir = 1; break; //right
			case 3: dir = 0; break; //left
		}
		for (int i = 0; i < buttons.length; i++) {
			buttons[i] = false;
		}
		this.bmp = bmp;

	}
	
	public int getSlot(){
		return slot;
	}
	
	void reset( int x, int y ){
		this.x = x;
		this.y = y;
		alive = true;
		numProjectiles = 5;
		for (int i = 0; i < buttons.length; i++) 
			buttons[i] = false;
		switch (slot){
			case 0: dir = 1; break; //right
			case 1: dir = 0; break; //left
			case 2: dir = 1; break; //right
			case 3: dir = 0; break; //left
		}
		
	}
	
	//public Player(){
	//	this(0,0,0);
	//}
	
	public boolean equals( Player other ){
		return this.slot == other.slot;
	}
	
	public boolean shoot(){
		if(!ducking && alive){
			if( numProjectiles >0){
				numProjectiles--;
				return true;
			}
		}
			return false;
	}
	
	Projectile nextProjectile(){
		Projectile p = new Projectile();
		if( dir == 0 )
			p.x = x - p.width - 1;
		else
			p.x = x + width + 1;
		p.slot = slot;
		p.y = y + height/2;
		p.dir = dir;
		return p;
	}
	
	@Override
	public boolean checkCollision( GameObject obj ){

		
		if(alive){
			if( obj.y < this.y + this.height && obj.y + obj.height >= this.y  ){
				if(this.x + this.width + speed >= obj.x  && this.x + this.width <= obj.x ){
					if( obj instanceof Player ){
						if( ((Player)obj).alive )
							moveRight = false;
					}else
						moveRight = false;
					
				}
				if(this.x - speed <= obj.x + obj.width && this.x + this.width >= obj.x + obj.width ){
					if( obj instanceof Player ){
						if( ((Player)obj).alive )
							moveLeft = false;
					}else
						moveLeft = false;
				}
			}
			
			if( this.x + this.width > obj.x && this.x < obj.x + obj.width )	
				if( this.y + this.height >= obj.y && this.y + this.height <= obj.y + velocityYMax*2){
					
					if( obj instanceof Platform ){
						this.y = obj.y - this.height;
						this.velocityY = 0;
						this.jumping = false;
						this.onGround = true;
						this.falling = false;
					} else if( obj instanceof Player ){
						if( ((Player)obj).alive ){
							((Player)obj).alive = false;
							this.score++;
						}
					}
					return true;
				}
			
			if( this.x + this.width > obj.x && this.x < obj.x + obj.width )
				if( this.y > obj.y + obj.height + jumpHeight && this.y < obj.y + obj.height){
					if( obj instanceof Platform ){
						this.y = obj.y + obj.height;
						this.velocityY = 0;
						this.jumping = false;
						this.onGround = false;
						this.falling = true;
					}
					return true;
				}
			
			//return false;
			
			//TODO Implement Horizontal Wall Collision
			//TODO Implement Wall Jumping
		
		}
		return false;
	}
		
	void update(){
		if(alive){
			// If a user is on the ground and presses O, the user jumps
			if(buttons[Button.O.code] && onGround){
				jumping = true;
				falling = false;
				onGround = false;
				velocityY = jumpHeight;
			}
			
			// If a user holds down on d-pad while not on the ground, the user will fall faster
			if(buttons[Button.D__DOWN.code]){
				if(!onGround){
					velocityYMax = 20;
					if(ducking){
						y-=20;
						height = 50;
						ducking = false;
					}
				}else{
					if(!ducking){
						height = 30;
						y+=20;
					}
					ducking = true;
				}
			}else{
				if(ducking){
					y-=20;
					height = 50;
				}
				ducking = false;
				velocityYMax = 10;
			}
			
			// Handles y - movement in the air
			if(jumping){
				
				y+=velocityY;
				
				if(velocityY < 0)
					velocityY++;
				else{
					jumping = false;
					falling = true;
					onGround = false;
				}
				
			}else if(falling){
				
				y+=velocityY;
				
				if(velocityY < velocityYMax)
					velocityY++;
			}
			
			// Handles x - movement
			if(!ducking){
				if(buttons[Button.D_LEFT.code] && moveLeft){
					dir = 0;
					this.x-=speed;
				}
				if(buttons[Button.D_RIGHT.code] && moveRight){
					dir = 1;
					this.x+=speed;
				}
			}
			
			// Implements torus
			if( x > 1750 )
				x = 0;
			else if( x + width < 0 )
				x = 1750 - width;
		}
		
	}
	
	@Override
	public void doDraw( Canvas canvas ){

        
		Paint p = new Paint();
		if(alive){
			switch (slot){
				case 0: p.setColor( Color.BLUE ); break;
				case 1: p.setColor( Color.RED ); break;
				case 2: p.setColor( Color.GREEN ); break;
				case 3: p.setColor( Color.YELLOW ); break;
			}
			if(!alive)
				p.setColor( Color.WHITE );

			
			if(dir == 0)
				canvas.drawBitmap(bmp, new Rect( 440, 50 + slot*50, 480, 100 + slot * 50), new Rect( x, y, x+width, y+height), null);
			if(dir == 1)
				canvas.drawBitmap(bmp, new Rect( 120, 50 + slot*50, 160, 100 + slot * 50), new Rect( x, y, x+width, y+height), null);
		
			p.setColor( Color.WHITE );
			
			for( int i = 0; i < numProjectiles; i++ ){
				canvas.drawCircle(x+5*i, y - 5, 2, p);
			}
        }
		
		p.setStyle(Style.FILL);   
		p.setTextSize(20);
		if(slot == 0)
			p.setColor(Color.CYAN);
		else if( slot == 1)
			p.setColor(Color.RED);
		else if( slot == 2)
			p.setColor(Color.GREEN);
		else if( slot == 3 )
			p.setColor(Color.YELLOW);
		canvas.drawText(String.format("Player: %d", slot+1), slot*200, 25, p);
		canvas.drawText(String.format("Score: %d", score), slot*200, 55, p);
		//DEUBUGGING OUTPUT
		if(debugOutput){
			p.setStyle(Style.STROKE);
			canvas.drawRect( x, y, x + width, y + height, p );
 
			/*if( dir == 0 ){
				canvas.drawText("<-", x+width/2, y + height/2, p);
			}
			else
				canvas.drawText("->", x+width/2, y + height/2, p);*/

			//canvas.drawText("Jumping: " + jumping, slot*200, 85, p); 
			canvas.drawText("In Air: " + falling, slot*200, 115, p); 
			canvas.drawText("onGround: " + onGround, slot*200, 145, p);
			canvas.drawText( String.format("x: %d, y: %d", x, y), slot*200, 175, p);
			String status;
			if(alive)
				status = "alive";
			else
				status = "dead";
			canvas.drawText(String.format("Status: %s", status), slot*200, 205, p);
			
		}
	}
	
	boolean debugOutput = false;
		
}
