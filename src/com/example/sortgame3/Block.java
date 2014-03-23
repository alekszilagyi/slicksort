package com.example.sortgame3;

import java.util.ArrayList;

import android.graphics.Rect;


public class Block {
	
	private int xCenter;
	private int yCenter;
	private int blockWidthHalf;
	
	private Rect bounds;
	private static Rect gameBounds;
	
	private ArrayList<Integer> colors;
	private int colorIndex;
	
	private static int VERT_MOVE_CONST = 250;
	private static int HORIZ_MOVE_CONST = 3000;
	
	private Square mySquare;
	private boolean squareSet;
	
	private boolean swiped;
	private boolean hitBottom;
	private int swipeDirection; //negative left, positive right
	
	public Block(int gameWidth, int gameHeight, ArrayList<Integer> colors, int startColor)
	{
		
		blockWidthHalf = gameWidth / 5 / 2;
		
		yCenter = gameHeight - blockWidthHalf;
		xCenter = (int)((gameWidth - blockWidthHalf) * Math.random());
		
		bounds = new Rect(xCenter - blockWidthHalf, yCenter + blockWidthHalf, xCenter + blockWidthHalf, yCenter - blockWidthHalf);
		gameBounds = new Rect(0, gameHeight, gameWidth, 0);
		this.colors = colors;
		if (startColor >= colors.size())
		{
			startColor = 0;
		}
		this.colorIndex = startColor;
		
		this.swiped = false;
		this.hitBottom = false;
		
		squareSet = false;
	}
	
	public boolean touched(int xLoc, int yLoc)
	{		
		bounds = new Rect(xCenter - blockWidthHalf, yCenter + blockWidthHalf, xCenter + blockWidthHalf, yCenter - blockWidthHalf);
		
		if (xLoc <= bounds.left || xLoc >= bounds.right)
		{
			return false;
		}
		if (yLoc <= bounds.bottom || yLoc >= bounds.top)
		{
			return false;
		}
		
		return true;	
	}
	
	public boolean squareSet()
	{
		return squareSet;
	}
	
	public void setSquare(Square square)
	{
		squareSet = true;
		this.mySquare = square;
	}
	
	public int getX()
	{
		return xCenter;
	}
	
	public int getY()
	{
		return yCenter;	
	}
	
	public int getWidth()
	{
		return blockWidthHalf * 2;
	}
	
	public void nextColor()
	{
		if (swiped == false)
		{
			colorIndex++;
			if (colorIndex >= colors.size())
			{
				colorIndex = 0;
			}
			mySquare.setColor(colors.get(colorIndex));
		}
	}
	
	public int getColor()
	{
		return colors.get(colorIndex);
	}
	
	public Square getSquare()
	{
		return mySquare;
	}
	
	public void setSwiped(int direction)
	{
		if (swiped == false)
		{			
			swiped = !swiped;
			if (direction < 0)
			{
				System.out.println("Swipe Left");
				swipeDirection = -1;
			}
			else
			{
				System.out.println("Swipe Right");
				swipeDirection = 1;
			}
		}
	}
	
	public int getSwipeDirection()
	{
		return swipeDirection;
	}
	
	public boolean shouldDie()
	{		
		bounds = new Rect(xCenter - blockWidthHalf, yCenter + blockWidthHalf, xCenter + blockWidthHalf, yCenter - blockWidthHalf);
		
		if (bounds.right <= gameBounds.left || bounds.left >= gameBounds.right)
		{
			return true;
		}
		if (bounds.top <= gameBounds.bottom || bounds.bottom >= gameBounds.top)
		{
			return true;
		}
		
		return false;
		
	}
	
	public boolean hitBottom()
	{
		return hitBottom;
	}
	
	public void update(double delta)
	{
		if (swiped == false)
		{
			yCenter -= (delta * VERT_MOVE_CONST);
			
			if (yCenter < 0)
			{
				hitBottom = true;
			}
		}
		else
		{
			xCenter += (delta * HORIZ_MOVE_CONST * swipeDirection); 
		}
	}
}
