package com.example.sortgame3;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;

public class Vizualization extends GLSurfaceView {
	
	private MyRenderer renderer;
	private static Game game;
	private static Activity context;
	
	public Vizualization(Activity context, Game game) {
		super(context);

		setEGLContextClientVersion(2);
		setPreserveEGLContextOnPause(true);
				
		renderer = new MyRenderer();
		renderer.setRefs(context, game);
		
		setRenderer(renderer);
				
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	}	
}
