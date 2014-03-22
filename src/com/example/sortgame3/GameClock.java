package com.example.sortgame3;

public class GameClock {
	
	private long prevTime;
	private long recentTime;
	
	private boolean ticked;
	
	public GameClock()
	{
		recentTime = System.currentTimeMillis();
	}
	
	public void tick()
	{
		prevTime = recentTime;
		recentTime = System.currentTimeMillis();
		ticked = true;
	}
	
	public long getCurrTime()
	{
		return recentTime;
	}
	
	public double getDeltaInSeconds()
	{
		if (ticked)
		{
			return ((recentTime - prevTime) / 1000.0);
		}
		return 0;
	}
	
	public double getDeltaInMillis()
	{
		if (ticked)
		{
			if(Math.random() < .0001)
			{
			//System.out.println("--TIMER" + (recentTime - prevTime));
			}
			return (recentTime - prevTime);
		}
		return 0;
	}

}
