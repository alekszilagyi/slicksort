package com.example.sortgame3;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

public class MyRenderer implements GLSurfaceView.Renderer {

	private CoordinateTranslator coordTrans;

	private final float[] mMVPMatrix = new float[16];
	private final float[] orthoMatrix = new float[16];
	private final float[] projectionMatrix = new float[16];
	private final float[] viewMatrix = new float[16];
	private final float[] rotationMatrix = new float[16];
	private final float[] translationMatrix = new float[16];

	private final float[] modelMatrix = new float[16];
	private float[] tempMatrix = new float[16];

	private int height, width;
	private ArrayList<Block> blocks;
	
	private ArrayList<TallRectangle> leftEdges;
	private ArrayList<TallRectangle> rightEdges;

	private Game game;
	private Activity context;
	private boolean alertShown;

	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		// Set the background frame color

		GLES20.glClearColor(128.0f, 128.0f, 128.0f, 1f);

		leftEdges = new ArrayList<TallRectangle>();
		rightEdges = new ArrayList<TallRectangle>();
		
		for (int i = 0; i < 4; i++)
		{
			leftEdges.add(new TallRectangle());
			rightEdges.add(new TallRectangle());
		}

		blocks = new ArrayList<Block>();

	}

	public void onDrawFrame(GL10 unused) {

		// update game
		boolean gameContinue = game.update();

		if (gameContinue) {

			// Redraw background color
			float[] scratch = new float[16];			

			GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT
					| GLES20.GL_DEPTH_BUFFER_BIT);

			// set look at and projection
			Matrix.setLookAtM(viewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f,
					0.0f);
			Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
			
			blocks.clear();
			blocks = Game.getBlocks();
			PointF point;
			
			
			int[] leftColors = game.getLeftEdgeColor();
			int[] rightColors = game.getRightEdgeColor();
			
			for (int i = 0; i < leftColors.length; i++)
			{	
				leftEdges.get(i).setColor(leftColors[i]);
				point = gamePointToGLPoint(0, MainActivity.gameHeight - (i * (MainActivity.gameHeight / 4)));
				// create model
				Matrix.setIdentityM(modelMatrix, 0);
				// apply with translation
				Matrix.translateM(modelMatrix, 0, point.x, point.y, 0f);
				// apply projection to translation
				Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, modelMatrix, 0);
				// draw
				leftEdges.get(i).draw(scratch);
			}
			
			for (int i = 0; i < leftColors.length; i++)
			{	
				rightEdges.get(i).setColor(rightColors[i]);
				point = gamePointToGLPoint(MainActivity.gameWidth, MainActivity.gameHeight - (i * (MainActivity.gameHeight / 4)));
				// create model
				Matrix.setIdentityM(modelMatrix, 0);
				// apply with translation
				Matrix.translateM(modelMatrix, 0, point.x, point.y, 0f);
				// apply projection to translation
				Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, modelMatrix, 0);
				// draw
				rightEdges.get(i).draw(scratch);
			}

			for (Block block : blocks) {
				if (block.squareSet() == false) {
					Square s = new Square();
					s.setColor(block.getColor());
					block.setSquare(s);
				}

				point = gamePointToGLPoint(block.getX(), block.getY());
				// create model
				Matrix.setIdentityM(modelMatrix, 0);
				// apply with translation
				Matrix.translateM(modelMatrix, 0, point.x, point.y, 0f);
				// apply projection to translation
				Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, modelMatrix, 0);
				// draw
				block.getSquare().draw(scratch);
			}
		}
		
		else if (gameContinue == false && alertShown == false)
		{
			alertShown = true;
			showAlert();
		}
	}

	public void onSurfaceChanged(GL10 unused, int width, int height) {

		this.width = width;
		this.height = height;

		GLES20.glViewport(0, 0, width, height);

		float ratio = width / (float) height;

		Matrix.orthoM(orthoMatrix, 0, -ratio, ratio, -1, 1, -1, 1);
		Matrix.frustumM(projectionMatrix, 0, ratio, -ratio, 1, -1, 3, 7);
	}

	public static int loadShader(int type, String shaderCode) {
		int shader = GLES20.glCreateShader(type);

		GLES20.glShaderSource(shader, shaderCode);
		GLES20.glCompileShader(shader);

		return shader;

	}

	public void setRefs(Activity context, Game game) {
		this.game = game;
		this.context = context;
	}

	public void setCoordinateTranslator(CoordinateTranslator cT) {
		this.coordTrans = cT;
	}

	public static float[] getColor(int color) {
		float[] returnColor = new float[4];

		returnColor[0] = Color.red(color) / 255.0f;
		returnColor[1] = Color.green(color) / 255.0f;
		returnColor[2] = Color.blue(color) / 255.0f;
		returnColor[3] = Color.alpha(color) / 255.0f;

		return returnColor;
	}

	private PointF gamePointToGLPoint(int x, int y) {
		float nuX = (float) (((1.0 / MainActivity.gameWidth) * x) - .5);
		float nuY = (float) (((2.0 / MainActivity.gameHeight)
				* (MainActivity.gameHeight - y) - 1));

		return new PointF(nuX, nuY);
	}

	public void showAlert() {
		

		context.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				AlertDialog.Builder alert = new AlertDialog.Builder(context);

				StringBuilder message = new StringBuilder();
				message.append("GAME OVER\n");
				message.append("Score: " + game.score + "\n");
				message.append("Play Again?\n");

				alert.setMessage(message.toString());

				alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						game.reset();
						alertShown = false;
					}
				});

				alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						alertShown = false;
						System.exit(0);
					}
				});

				alert.show();
				
			}
		});		
	}
}
