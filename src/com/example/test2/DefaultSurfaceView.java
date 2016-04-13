package com.example.test2;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Build;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import tv.ouya.console.api.OuyaController;

public class DefaultSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
	private SurfaceHolder surfaceHolder;
	private final Paint paint = new Paint( Paint.ANTI_ALIAS_FLAG );
	private Context ctx;
	private Handler handler;
	DefaultThread thread;
	Bitmap bmp, bg;
	
	int numPlayers = 2;
	
	public DefaultSurfaceView(Context context) {
		super(context);
		this.surfaceHolder = getHolder();
		this.surfaceHolder.addCallback(this);
		this.paint.setColor(Color.BLUE);
		this.paint.setStyle(Style.FILL);
		ctx = context;
		handler = new Handler();
		setFocusable(true);
	}
	
	@Override
	public boolean onKeyDown(final int keyCode, KeyEvent event){
	    //Get the player #
	    int player = OuyaController.getPlayerNumByDeviceId(event.getDeviceId());       

	    if( player > numPlayers )
	    	return false;
	    //Handle the input
	    return (thread.handleButtonPress(keyCode, player) || super.onKeyDown(keyCode, event));
	}
	
	@Override
	public boolean onKeyUp(final int keyCode, KeyEvent event){
		//Get the player #
	    int player = OuyaController.getPlayerNumByDeviceId(event.getDeviceId());       
	    if( player > numPlayers )
	    	return false;
	    //Handle the input
	    return (thread.handleButtonRelease(keyCode, player) || super.onKeyUp(keyCode, event))
	    ;
	}

	@Override
	public boolean onGenericMotionEvent(final MotionEvent event) {
	    //Get the player #
	    int player = OuyaController.getPlayerNumByDeviceId(event.getDeviceId());    

	    //Get all the axis for the event
	    /*float LS_X = event.getAxisValue(OuyaController.AXIS_LS_X);
	    float LS_Y = event.getAxisValue(OuyaController.AXIS_LS_Y);
	    float RS_X = event.getAxisValue(OuyaController.AXIS_RS_X);
	    float RS_Y = event.getAxisValue(OuyaController.AXIS_RS_Y);
	    float L2 = event.getAxisValue(OuyaController.AXIS_L2);
	    float R2 = event.getAxisValue(OuyaController.AXIS_R2);*/

	    //Do something with the input
	    //updatePlayerInput(player, LS_X, LS_Y, RS_X, RS_Y, L2, R2);

	    return true;
	}
	
	public DefaultThread getThread(){
		return thread;
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		thread.setSurfaceSize(width, height);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Canvas canvas = surfaceHolder.lockCanvas();
		canvas.drawColor(Color.BLACK);
		//canvas.drawRect(100, 200, 150, 250, this.paint);
		this.surfaceHolder.unlockCanvasAndPost(canvas);
		thread = new DefaultThread( surfaceHolder, ctx, new Handler() );
		thread.init(numPlayers);
		thread.setRunning(true);
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		thread.setRunning(false);
		while( retry ){
			try{
				thread.join();
				retry = false;
			} catch( InterruptedException e ){};
		}
	}
	
	public void reset(){
		try{
			thread.sleep(1000);
		}catch(Exception e ){
			
		}
		thread.init(numPlayers);
	}
	

	boolean debugOutput = false;
	public void toggleDebugOutput(){
		if(debugOutput)
			debugOutput = false;
		else 
			debugOutput = true;
	}
	
	public void selectNumPlayers( int num ){
		numPlayers = num;
	}
	
	class DefaultThread extends Thread{
		private int canvasWidth = DefaultSurfaceView.this.getRight() - DefaultSurfaceView.this.getLeft();
		private int canvasHeight = DefaultSurfaceView.this.getBottom() - DefaultSurfaceView.this.getTop();
		
		private boolean run = false;
		boolean gameOver = false;
		int numPlayers;
		
		ArrayList<Player> players;
		ArrayList<Platform> platforms;
		ArrayList<Projectile> projectiles;
		ArrayList<Projectile> queueProjectiles;
		
		public DefaultThread( SurfaceHolder surfaceHolder, Context context, Handler handler ){
			DefaultSurfaceView.this.surfaceHolder = surfaceHolder;
			DefaultSurfaceView.this.handler = handler;
			ctx = context;
			init(3);
		}
		
		public void init( int numPlayers ){
			this.numPlayers = numPlayers;
			players = new ArrayList<Player>();
			players.add( new Player( canvasWidth/2 - 150, canvasHeight/2, 0, bmp));
			players.add( new Player( canvasWidth/2 + 100, canvasHeight/2, 1, bmp));
			if(numPlayers == 3)
				players.add( new Player( canvasWidth/2 - 25, 800, 2, bmp));
			else if(numPlayers == 4){
				players.add( new Player( canvasWidth/2 - 150, 800, 2, bmp));
				players.add( new Player( canvasWidth/2 + 100, 800, 3, bmp));
			}
			
			for( Player p : players )
				p.bmp = bmp;

			platforms = new ArrayList<Platform>();
			
			Stage stage = new Stage( 1750, 900);
			//Stage stage = new Stage("stage0.txt", ctx);
			platforms = stage.platforms;
			projectiles = new ArrayList<Projectile>();
			queueProjectiles = new ArrayList<Projectile>();
			gameOver = false;
		}
		
		public void resetRound(){
			players.get(0).reset( canvasWidth/2 - 150, canvasHeight/2 );
			players.get(1).reset( canvasWidth/2 + 100, canvasHeight/2 );
			if(numPlayers == 3)
				players.get(2).reset( canvasWidth/2 - 25, 800 );
			else if(numPlayers == 4){
				players.get(2).reset(canvasWidth/2 - 150, 800 );
				players.get(3).reset( canvasWidth/2 + 100, 800 );
			}
			
			projectiles = new ArrayList<Projectile>();
			queueProjectiles = new ArrayList<Projectile>();
			gameOver = false;
			
		}
		
		public void doStart(){
			synchronized ( surfaceHolder ){

			}
		}
		
		public void run(){
			while( run ){
				Canvas c = null;
		
				if(!gameOver)
					update();// Do Game Logic
				else
					gameMenu(); // Not implemented
				
				try{
					c = surfaceHolder.lockCanvas(null);
					synchronized(surfaceHolder){
						doDraw(c); // Draw to Canvas
					}
				} finally{
					if( c != null ){
						surfaceHolder.unlockCanvasAndPost(c);
					}
				}
			}
		}
		
		public void setRunning( boolean run ){
			this.run = run;
		}
		
		public void setSurfaceSize( int width, int height ){
			synchronized ( surfaceHolder ){
				canvasWidth = width;
				canvasHeight = height;
				doStart();
			}
		}
		
		public boolean handleButtonPress( final int keyCode, int player ){
			boolean retval = false;
			if(player < numPlayers){
			    switch(keyCode){
		        case OuyaController.BUTTON_O:
		        	players.get(player).buttons[Button.O.code] = true;
		            retval =  true;
		            break;
		            
		        case OuyaController.BUTTON_U:
					if(players.get(player).shoot()){
						queueProjectiles.add(players.get(player).nextProjectile());
					}
					retval = true;
					break;
		    	          
		        /*case OuyaController.BUTTON_Y:
		        	//buttons[Button.Y.code] = false;
		        	return true;*/
	
				case OuyaController.BUTTON_A:
					//KEEPS GAME FROM QUITTING WHEN A IS PRESSED
					//buttons[Button.A.code] = false;
					retval = true;
					break;
			    }
				
		            
				if( keyCode == OuyaController.BUTTON_DPAD_LEFT){
					players.get(player).buttons[Button.D_LEFT.code] = true;
					
					retval = true;
				}else if( keyCode == OuyaController.BUTTON_DPAD_RIGHT){
					players.get(player).buttons[Button.D_RIGHT.code] = true;
					retval = true;
			    }
				
				if( keyCode == OuyaController.BUTTON_DPAD_DOWN){
					players.get(player).buttons[Button.D__DOWN.code] = true;
					retval = true;
				}
			}
		    
			return retval;
		}
		
		public boolean handleButtonRelease( final int keyCode, int player ){
			boolean retval = false;
			if(player < numPlayers){
			    switch(keyCode){
		        case OuyaController.BUTTON_O: //Player Jumps
		        	players.get(player).buttons[Button.O.code] = false;
		            retval =  true;
		            
		        /*case OuyaController.BUTTON_U:
					players.get(player).buttons[Button.U.code] = false;
					return true;
		    	          
		        /*case OuyaController.BUTTON_Y:
		        	//buttons[Button.Y.code] = false;
		        	return true;*/
	
				case OuyaController.BUTTON_A:
					//KEEPS GAME FROM QUITTING WHEN A IS PRESSED
					//buttons[Button.A.code] = false;
					retval = true;
				
			    }
				if(keyCode == OuyaController.BUTTON_DPAD_LEFT){
					players.get(player).buttons[Button.D_LEFT.code] = false;
					retval = true;
				}
				if( keyCode ==  OuyaController.BUTTON_DPAD_RIGHT ){
					players.get(player).buttons[Button.D_RIGHT.code] = false;
					retval = true;
				}
				if( keyCode == OuyaController.BUTTON_DPAD_DOWN){
					players.get(player).buttons[Button.D__DOWN.code] = false ;
					retval = true;
				}
			}
			return retval;
		}
		
		public void checkRoundOver(){
			int playersAlive = 0;
			for(Player p : players){
				if( p.score == 10 )
					gameOver = true;				
				if(p.alive)
					playersAlive++;
			}
			if(playersAlive <=1){//TEMP
				try{
					this.sleep(500);
				}catch( Exception e ){
				
				}
				this.resetRound();
			}
		}
		
		public void gameMenu(){
			//TODO
		}
		
		private void update(){
			checkRoundOver();
			if(gameOver)
				return;
			projectiles.addAll(queueProjectiles);
			queueProjectiles.clear(); // = new ArrayList<Projectile>();
			PROJECTILE_LOOP:
			for( int i = projectiles.size() - 1; i >= 0; i--){
				for( Player plyr : players ){
					if(projectiles.get(i).checkCollision(plyr)){
						if(projectiles.get(i).active){
							plyr.alive = false;
							projectiles.get(i).deactivate();
							if( plyr.getSlot() != projectiles.get(i).slot )
								players.get(projectiles.get(i).slot).score++;
							else
								players.get(projectiles.get(i).slot).score--;
						}
						else if(plyr.alive){
								plyr.numProjectiles++;
								projectiles.remove(i);
								continue PROJECTILE_LOOP;
						}
					}
				}
				for( Platform plat : platforms )
					if(projectiles.get(i).checkCollision(plat)){
						projectiles.get(i).deactivate();
						projectiles.get(i).grounded = true;
						continue PROJECTILE_LOOP;
					}else{
						projectiles.get(i).grounded = false;
					}
				for( Projectile proj : projectiles ){
					if( !projectiles.get(i).equals(proj) )
						if(projectiles.get(i).checkCollision(proj)){
							projectiles.get(i).deactivate();
							proj.deactivate();
							continue PROJECTILE_LOOP;
						}
				}
				
				/*if(remove)
					projectiles.remove(i);
				else*/
				projectiles.get(i).update();
				
			}

			for( Player p : players ){	

				
				boolean playerFalls = true; //determines whether player will fall off of a platform
				
				p.moveLeft = true;
				p.moveRight = true;
				
				for( Platform plat : platforms ){
					if(p.checkCollision(plat)){
						playerFalls = false;
						//break;
					}
				}
				if( playerFalls ){
					p.jumping = false;
					p.falling = true;
					p.onGround = false;
				}
				
				for( Player pp : players ){
					if(!p.equals(pp))
						p.checkCollision(pp);
				}
				
				p.update();
			}

		}

		private void doDraw( Canvas canvas ){
			canvas.restore();
			canvas.drawColor(Color.BLACK);
			//canvas.drawBitmap(bg, 0, 0, null); 
			for( Player p : players ){
				if(gameOver && p.score == 10){
					Paint paint = new Paint();
					paint.setColor(Color.WHITE);
					paint.setTextSize(50);
					canvas.drawText(String.format("Player %d Wins!", p.getSlot() + 1 ), 700, 200, paint);
				}
				p.debugOutput = debugOutput;
				p.doDraw(canvas);
			}
			for( Platform p : platforms ){
				p.doDraw(canvas);
			}
			for( Projectile p : projectiles ){
				p.doDraw(canvas);
			}
			
			if( debugOutput ){
			
				Paint p = new Paint();
				p.setColor(Color.WHITE);
				p.setTextSize(20);
				Integer n = 0;
				for( int i = 0; i < 18; i++ ){
					n = i*100;
					canvas.drawText(n.toString(), n, 880, p);
				}
	
				for( int i = 0; i < 10; i++ ){
					n = i*100;
					canvas.drawText(n.toString(), 1650, n, p);
				}
			}
		}
	}
	

}
