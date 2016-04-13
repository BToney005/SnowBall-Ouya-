package com.example.test2;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import android.content.Context;
import android.content.res.AssetManager;

public class Stage {
	int stageWidth, stageHeight;
	ArrayList<Platform> platforms = new ArrayList<Platform>();
	
	public Stage( String filename, Context context ){
		/*try{
			AssetManager am = context.getAssets();
			InputStream is = am.open(filename);
			int x, y, width, height;
			Scanner s = new Scanner(is);
			stageWidth = s.nextInt();
			stageHeight = s.nextInt();
			while( s.hasNext("Platform:")){
				x = s.nextInt();
				y = s.nextInt();
				if( (width = s.nextInt()) == -30000 )
					width = stageWidth;
				if( (height = s.nextInt()) == -30000 )
					height = stageHeight;
				platforms.add( new Platform ( x, y, width, height ));
			}
		}catch( Exception e){
			
		}*/
	}
	
	public Stage(int stageWidth, int stageHeight ){
		this.stageWidth = stageWidth;
		this.stageHeight = stageHeight;
		platforms = new ArrayList<Platform>();
		platforms.add( new Platform ( 0 , 850, stageWidth, 100));
		platforms.add( new Platform ( 0, 800, 500, 50 ));
		platforms.add( new Platform ( 1200, 800, 600, 50) );
		platforms.add( new Platform ( -50, 600, 200, 200) );
		platforms.add( new Platform ( 1580, 600, 200, 200));
		platforms.add( new Platform ( 350, 500, 300, 50 ));
		platforms.add( new Platform ( 750, 500, 250, 50 ));
		platforms.add( new Platform ( 1100, 500, 300, 50 ));
	}
}
