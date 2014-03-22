package com.example.sortgame3;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;

public class Game 
{
	private Activity context;
	
	private int gameWidth, gameHeight;
	
	private GameClock timer;
	
	public int leftColor, rightColor;
	
	private long nextSpawnTime;
	private static long STD_SPAWN_TIME = 6000;
	private static long SWIPE_DELAY = 100;
	private long tapTime;
	
	private boolean tapReady;
	private boolean swipeReady;
	private Block storedBlock;
	private int swipeDirection;
	
	private int score = 0;
	
	private static ArrayList<Block> blocks;
	int[] gameColors;
	
	public Game(int gameWidth, int gameHeight, Activity	 context)
	{
		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;
		
		this.context = context;
		
		reset();
	}
	
	public static ArrayList<Block> getBlocks()
	{
		return new ArrayList<Block>(blocks);
	}
	
	public void signalTap(int xLoc, int yLoc)
	{
		for (Block block: blocks)
		{
			if (block.touched(xLoc, yLoc))
			{
				block.nextColor();
			}
		}
	}
	
	public void signalSwipe(int xLoc, int yLoc, int direction)
	{
		for (Block block: blocks)
		{
			if (block.touched(xLoc, yLoc))
			{
				block.setSwiped(direction);
			}
		}
	}
	
	public void spawnBlock()
	{
		int startColor = (int)(Math.random() * gameColors.length);
		Block newBlock = new Block(gameWidth, gameHeight, gameColors, startColor);
		blocks.add(newBlock);
	}
	
	public boolean update()
	{
		timer.tick();
		
		nextSpawnTime -= timer.getDeltaInMillis();
		
		if (nextSpawnTime <= 0)
		{
			spawnBlock();
			float lower = 1.15f;
			float upper = .85f;
			double spawn = ((Math.random() * (upper - lower)) + lower);
			
			nextSpawnTime = (long)(STD_SPAWN_TIME * spawn);
		}
		
		ArrayList<Block> deadBlocks = new ArrayList<Block>();
		
		for (Block block: blocks)
		{
			block.update(timer.getDeltaInSeconds());
			if (block.gameOver())
			{
				context.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						AlertDialog.Builder alert = new AlertDialog.Builder(context);
						
						StringBuilder message = new StringBuilder();
						message.append("GAME OVER\n");
						message.append("Score: " + score + "\n");
						message.append("Play Again?\n");
						
						alert.setMessage(message.toString());
						
						alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								reset();
								
							}
						});
						
						alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								System.exit(0);	
							}
						});
						
						alert.show();
						
					}
				});
				
				return false;
			}
			if (block.shouldDie())
			{
				deadBlocks.add(block);
			}
		}
	
		for (Block block: deadBlocks)
		{
			blocks.remove(block);
			score++;
		}
		
		return true;
	}
	
	public void reset()
	{
		this.timer = new GameClock();
		this.blocks = new ArrayList<Block>();
		
		int[] colors = {Color.RED, Color.BLUE};
		this.gameColors = colors;
		
		this.leftColor = Color.RED;
		this.rightColor = Color.BLUE;
		
		this.tapReady = false;
		this.swipeReady = false;
		this.tapTime = Long.MAX_VALUE;
		
		this.nextSpawnTime = 0;
		this.score = 0;
	}
	
}
