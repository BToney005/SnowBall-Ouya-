package com.example.test2;

import tv.ouya.console.api.OuyaController;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;

public class MainActivity extends Activity {
	DefaultSurfaceView game;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		OuyaController.init(this);

		Bitmap bmp = BitmapFactory.decodeResource(this.getResources(), R.drawable.sprites);
		//Bitmap bg = BitmapFactory.decodeResource(this.getResources(), R.drawable.bg);
	
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		game = new DefaultSurfaceView(this);
		//game.bg = bg;
		game.bmp = bmp;
		setContentView(game);
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected( MenuItem item ){
		switch (item.getItemId()){
		case R.id.new_game:
			game.reset();
			return true;
		case R.id.toggle_debug:
			game.toggleDebugOutput();
			return true;
		case R.id.players_two:
			game.selectNumPlayers(2);
			game.reset();
			return true;
		case R.id.players_three:
			game.selectNumPlayers(3);
			game.reset();
			return true;
		case R.id.players_four:
			game.selectNumPlayers(4);
			game.reset();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
