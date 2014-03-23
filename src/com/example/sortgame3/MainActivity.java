package com.example.sortgame3;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

	public Game game;
	DrawView view;
	
	//private final Semaphore available = new Semaphore(1, true);

	//Vizualization visual;
	//FrameLayout frame;
	
	private GLSurfaceView mGLView;

	GestureDetectorCompat detector;

	private int screenWidth, screenHeight;
	
	public static final int gameHeight = 800;
	public static final int gameWidth = 600;
	
	private static CoordinateTranslator coordTrans;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		Point screenSize = new Point();
		getWindowManager().getDefaultDisplay().getSize(screenSize);
		screenWidth = screenSize.x;
		screenHeight = screenSize.y;
		game = new Game(gameWidth, gameHeight, this);
		
		coordTrans = new CoordinateTranslator(screenWidth, screenHeight, gameWidth, gameHeight, new Point(0, 0));
		mGLView = new Vizualization(this, game);
		setContentView(mGLView);

		detector = new GestureDetectorCompat(this, new SimpleListener());
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		this.detector.onTouchEvent(event);
		return false;
	}

	@Override
	protected void onPause() {
		super.onPause();
		mGLView.onPause();
		//thread.stopThread();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGLView.onResume();
		//thread.run();

	}

	private class SimpleListener extends SimpleOnGestureListener {
		private int SWIPE_MIN_DISTANCE = 30;
		private int SWIPE_MAX_OFF_PATH = 250;
		private float SWIPE_THRESHOLD_VELOCITY = 150;

		@Override
		public boolean onDoubleTap(MotionEvent event) {
			int x = (int) event.getX();
			int y = (int) event.getY();

			Point pt = coordTrans.convertScreenToWorld(new Point(x, y));
			game.signalTap(pt.x, pt.y);
			
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {
				int x1 = (int) e1.getX();
				int y1 = (int) e1.getY();
				int x2 = (int) e2.getX();
				int y2 = (int) e2.getY();
				
				if (Math.abs(y1 - y2) > SWIPE_MAX_OFF_PATH) {
					return false;
				}

				Point pt = coordTrans.convertScreenToWorld(new Point(x1, y1));
				if (x1 - x2 > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					
					game.signalSwipe(pt.x, pt.y, -1);

				} else if (x2 - x1 > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					
					game.signalSwipe(pt.x, pt.y, 1);

				}

			} catch (Exception e) {

			}
			return false;
		}
	}
}
