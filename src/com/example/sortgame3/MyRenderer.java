package com.example.sortgame3;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

public class MyRenderer implements GLSurfaceView.Renderer
{	
	private Square square1;
	private Square square2;
	
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
    
    private Game game;

	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        
		GLES20.glClearColor(128.0f, 128.0f, 128.0f, 1f);
		
		square1 = new Square();
		square2 = new Square();
		
		blocks = new ArrayList<Block>();
		
		square1.setColor(Color.RED);
		square2.setColor(Color.BLUE);
    }

    public void onDrawFrame(GL10 unused) {

    	// update game
    	game.update();
    	
    	// Redraw background color
    	float[] scratch = new float[16];

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        
        // set look at and projection
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        
        blocks.clear();
        blocks = Game.getBlocks();
        PointF point;
        
        for (Block block: blocks)
        {        	
        	if (block.squareSet() == false)
        	{
        		Square s = new Square();
        		s.setColor(block.getColor());
        		block.setSquare(s);
        	}
        	
        	point = gamePointToGLPoint(block.getX(), block.getY());
        	//create model
            Matrix.setIdentityM(modelMatrix, 0);
            //apply with translation
            Matrix.translateM(modelMatrix, 0, point.x, point.y, 0f);
            //apply projection to translation
            Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, modelMatrix, 0);
            //draw
            block.getSquare().draw(scratch);
        }
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        
    	this.width = width;
    	this.height = height;
    	
    	GLES20.glViewport(0, 0, width, height);
    	
    	float ratio = width / (float)height;
    	
    	Matrix.orthoM(orthoMatrix, 0, -ratio, ratio, -1, 1, -1, 1);
    	Matrix.frustumM(projectionMatrix, 0, ratio, -ratio, 1, -1, 3, 7);
    }
    
    public static int loadShader(int type, String shaderCode)
    {
    	int shader = GLES20.glCreateShader(type);
    	
    	GLES20.glShaderSource(shader, shaderCode);
    	GLES20.glCompileShader(shader);
    	
    	return shader;
    	
    }
    
    public void setGame(Game game)
    {
    	this.game = game;
    }
    
    public void setCoordinateTranslator(CoordinateTranslator cT)
    {
    	this.coordTrans = cT;
    }
    
    public static float[] getColor(int color)
    {
    	float[] returnColor = new float[4];
    	
    	returnColor[0] = Color.red(color) / 255.0f;
    	returnColor[1] = Color.green(color) / 255.0f;
    	returnColor[2] = Color.blue(color) / 255.0f;
    	returnColor[3] = Color.alpha(color) / 255.0f;
    	
    	return returnColor;
    }  
    
    private PointF gamePointToGLPoint(int x, int y)
    {
    	float nuX = (float)(((1.0 / MainActivity.gameWidth) * x) - .5);
    	float nuY = (float)(((2.0 / MainActivity.gameHeight) * (MainActivity.gameHeight - y) - 1));
    	
    	return new PointF(nuX, nuY);
    }
    
	
}
