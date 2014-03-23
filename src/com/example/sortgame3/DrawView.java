package com.example.sortgame3;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class DrawView {
	
	Paint paint = new Paint();
	Game game;
	
	int width, height;
	
	public DrawView(int width, int height)
	{
		this.width = width;
		this.height = height;
	}
	
	public void setGame(Game game)
	{
		this.game = game;
	}

	public void drawGame(Canvas canvas) {
		
		paint.setColor(Color.WHITE);
		canvas.drawRect(0, 0, width, height, paint);
		
		//draw sides
		paint.setColor(game.getLeftEdgeColor());
		canvas.drawRect(0, 0, 10, height, paint);
		paint.setColor(game.getRightEdgeColor());
		canvas.drawRect(width - 10, 0, width, height, paint);		
	
		ArrayList<Block> activeBlocks = game.getBlocks();
		
		for (Block block: activeBlocks)
		{
			paint.setColor(block.getColor());
			canvas.drawRect(block.getX(), block.getY(), block.getX() + block.getWidth(), block.getY() + block.getWidth(), paint);
		}
	}
}
