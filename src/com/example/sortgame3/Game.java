package com.example.sortgame3;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;

public class Game 
{	
	private int gameWidth, gameHeight;
	
	private GameClock timer;
	
	private long nextSpawnTime;
	private long nextAddColorTime;
	private long nextSwitchColorTime;
	private long colorChangeInterval;
	
	private static long STD_SWITCH_COLOR_TIME = 6000;
	private static long STD_ADD_COLOR_TIME = 10000; 
	private static long STD_SPAWN_TIME = 1500;
	private static int numStartColors = 2;
	
	private int[] leftEdgeColors;
	private int leftEdgeNextColorIndex;
	private int[] rightEdgeColors;
	private int rightEdgeNextColorIndex;
	
	private boolean colorChanging;
	private int colorChangeIndex;
	
	public int score = 0;
	
	private static ArrayList<Block> blocks;
	ArrayList<Integer> activeColors;
	ArrayList<Integer> gameColors;
	
	public Game(int gameWidth, int gameHeight, Activity	 context)
	{
		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;
		
		reset();
	}
	
	public static ArrayList<Block> getBlocks()
	{
		return new ArrayList<Block>(blocks);
	}
	
	public int[] getLeftEdgeColor()
	{
		return leftEdgeColors;
	}
	
	public int[] getRightEdgeColor()
	{
		return rightEdgeColors;
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
		int startColor = (int)(Math.random() * activeColors.size());
		Block newBlock = new Block(gameWidth, gameHeight, activeColors, startColor);
		blocks.add(newBlock);
	}
	
	public boolean update()
	{
		timer.tick();
		
		nextSpawnTime -= timer.getDeltaInMillis();
		nextSwitchColorTime -= timer.getDeltaInMillis();
		nextAddColorTime -= timer.getDeltaInMillis();
		
		if (nextSpawnTime <= 0)
		{
			spawnBlock();
			
			nextSpawnTime = getModifiedRand(STD_SPAWN_TIME, .85, 1.15);
		}
		
		if (nextSwitchColorTime <= 0 && colorChanging == false)
		{
			leftEdgeNextColorIndex = leftEdgeColors[3];
			while (leftEdgeNextColorIndex == leftEdgeColors[3])
			{
				leftEdgeNextColorIndex = (int)(Math.random() * activeColors.size());
			}
			rightEdgeNextColorIndex = leftEdgeNextColorIndex;
			while (rightEdgeNextColorIndex == leftEdgeNextColorIndex)
			{
				rightEdgeNextColorIndex = (int)(Math.random() * activeColors.size());
			}
			
			colorChanging = true;
			colorChangeInterval = 250;
			colorChangeIndex = 0;
			
			leftEdgeColors[colorChangeIndex] = gameColors.get(leftEdgeNextColorIndex);
			rightEdgeColors[colorChangeIndex] = gameColors.get(rightEdgeNextColorIndex);
			//nextSwitchColorTime = getModifiedRand(STD_SWITCH_COLOR_TIME, .9, 1.2);
		}
		
		if (colorChanging)
		{
			colorChangeInterval -= timer.getDeltaInMillis();
			
			if (colorChangeInterval < 0)
			{
				leftEdgeColors[colorChangeIndex] = gameColors.get(leftEdgeNextColorIndex);
				rightEdgeColors[colorChangeIndex] = gameColors.get(rightEdgeNextColorIndex);
				
				colorChangeIndex++;
				
				if (colorChangeIndex >= leftEdgeColors.length)
				{
					colorChanging = false;
					nextSwitchColorTime = getModifiedRand(STD_SWITCH_COLOR_TIME, .9, 1.2);
				}
				
				else
				{
					colorChangeInterval = 250;
				}
			}
						
		}
		
		if (nextAddColorTime <= 0)
		{
			int nextColorIndex = activeColors.size();
			
			if (nextColorIndex < gameColors.size())
			{
				activeColors.add(gameColors.get(nextColorIndex));
			}
			
			nextAddColorTime = getModifiedRand(STD_ADD_COLOR_TIME, .9, 1.1);
		}
		
		ArrayList<Block> deadBlocks = new ArrayList<Block>();
		
		for (Block block: blocks)
		{
			block.update(timer.getDeltaInSeconds());
			if (block.hitBottom())
			{
				return false;
			}
			if (block.shouldDie())
			{
				if (block.getSwipeDirection() < 0)
				{
					if (block.getColor() != leftEdgeColors[3])
					{
						return false;
					}
				}
				else
				{
					if (block.getColor() != rightEdgeColors[3])
					{
						return false;
					}
				}
				
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
	
	public long getModifiedRand(long standard, double low, double high)
	{
		double lower = low;
		double upper = high;
		double spawn = ((Math.random() * (upper - lower)) + lower);
		
		return (long)(standard * spawn);
	}
	
	public void reset()
	{
		this.timer = new GameClock();
		this.blocks = new ArrayList<Block>();
		
		this.gameColors = new ArrayList<Integer>();
		this.gameColors.add(Color.RED);
		this.gameColors.add(Color.BLUE);
		this.gameColors.add(Color.GREEN);
		this.gameColors.add(Color.YELLOW);
		
		this.activeColors = new ArrayList<Integer>();
		for (int i = 0; i < numStartColors; i++)
		{
			this.activeColors.add(this.gameColors.get(i));
		}
		
		this.leftEdgeColors = new int[4];
		this.rightEdgeColors = new int[4];
		
		for (int i = 0; i < rightEdgeColors.length; i++)
		{
			leftEdgeColors[i] = Color.RED;
			rightEdgeColors[i] = Color.BLUE;
		}
		
		this.nextSpawnTime = 0;
		this.nextAddColorTime = STD_ADD_COLOR_TIME;
		this.nextSwitchColorTime = STD_SWITCH_COLOR_TIME;
		this.score = 0;
	}
	
}
