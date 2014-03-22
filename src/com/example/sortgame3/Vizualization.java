package com.example.sortgame3;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.PointF;
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
		renderer.setGame(game);
		
		setRenderer(renderer);
				
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	}	
}
