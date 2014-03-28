package com.example.sortgame3;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

	// private final Semaphore available = new Semaphore(1, true);

	// Vizualization visual;
	// FrameLayout frame;

	private GLSurfaceView mGLView;

	GestureDetectorCompat detector;

	private int screenWidth, screenHeight;
	private MainActivity mainAct;

	public static final int gameHeight = 800;
	public static final int gameWidth = 600;
	
	public boolean start;

	private static CoordinateTranslator coordTrans;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mainAct = this;
		start = false;
		SoundPlayer.initSounds(this);
		SoundPlayer.playSoundMedia(this, R.raw.cephalopod);
		showWelcome();		

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		Point screenSize = new Point();
		getWindowManager().getDefaultDisplay().getSize(screenSize);
		screenWidth = screenSize.x;
		screenHeight = screenSize.y;
		game = new Game(gameWidth, gameHeight, this);

		coordTrans = new CoordinateTranslator(screenWidth, screenHeight,
				gameWidth, gameHeight, new Point(0, 0));
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
		if (SoundPlayer.player != null)
		{
			SoundPlayer.player.pause();
		}
		// thread.stopThread();

	}

	@Override
	protected void onResume() {
		super.onResume();
		mGLView.onResume();
		if (SoundPlayer.player != null)
		{
			SoundPlayer.player.start();
		}
		// thread.run();

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

	public void showWelcome() {

		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				AlertDialog.Builder alert = new AlertDialog.Builder(mainAct);

				StringBuilder message = new StringBuilder();
				message.append("Welcome to Slick Sort\n");
				message.append("Swipe to Sort to the Correct Color\n");
				message.append("Double Tap to Change Colors\n");
				message.append("How Slick is Your Sort?\n");

				alert.setMessage(message.toString());

				alert.setPositiveButton("Let's Go",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								SoundPlayer.playSound(SoundPlayer.start);
								game.pause();
							}
						});
				
				alert.setNeutralButton("Music Info",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								showLicense();
							}
						});

				alert.show();

			}
		});
	}
	
	public void showLicense() {

		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				AlertDialog.Builder alert = new AlertDialog.Builder(mainAct);

				StringBuilder message = new StringBuilder();
				message.append("Music Used in Application\n");
				message.append("Cephelopod:Kevin MacLeod:(incompetech.com)\n");
				message.append("Licensed under Creative Commons: By Attribution 3.0\n");
				message.append("http://creativecommons.org/licenses/by/3.0/\n");

				alert.setMessage(message.toString());

				alert.setPositiveButton("Thanks, Kevin!",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								showWelcome();
							}
						});

				alert.show();

			}
		});
	}
}
