package com.example.sortgame3;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;

public class Game {
	private int gameWidth, gameHeight;

	private GameClock timer;

	private long nextSpawnTime;
	private long nextAddColorTime;
	private long nextSwitchColorTime;
	private long colorChangeInterval;
	private long nextSpeedUpTime;

	private static final long BASE_SWITCH_COLOR_TIME = 6000;
	private static final long BASE_SPAWN_TIME = 1500;
	private static final int BASE_BLOCK_SPEED = 250;
	
	private static final int BLOCK_SPEED_INCREASE = 75;
	private static final int SPAWN_DECREASE = 200;
	private static final int COLOR_CHANGE_DECREASE = 750;
	
	
	private static long STD_SWITCH_COLOR_TIME = 6000;
	private static long STD_ADD_COLOR_TIME = 25000;
	private static long STD_SPEED_UP_TIME = 15000;
	private static long STD_SPAWN_TIME = 1500;
	private static int numStartColors = 2;
	private static int numSpeedUps = 0;
	private static int maxSpeedUps = 4;

	private int[] leftEdgeColors;
	private int leftEdgeNextColorIndex;
	private int[] rightEdgeColors;
	private int rightEdgeNextColorIndex;

	private boolean colorChanging;
	private int colorChangeIndex;

	public int score = 0;
	public boolean paused;
	private boolean firstLoad;

	private static ArrayList<Block> blocks;
	ArrayList<Integer> activeColors;
	ArrayList<Integer> gameColors;
	
	public enum GameStatus {PLAY, PAUSE, GAMEOVER};

	public Game(int gameWidth, int gameHeight, Activity context) {
		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;
		this.firstLoad = true;

		reset();
	}

	public void pause() {
		paused = !paused;
	}

	public static ArrayList<Block> getBlocks() {
		return new ArrayList<Block>(blocks);
	}

	public int[] getLeftEdgeColor() {
		return leftEdgeColors;
	}

	public int[] getRightEdgeColor() {
		return rightEdgeColors;
	}

	public void signalTap(int xLoc, int yLoc) {
		for (Block block : blocks) {
			if (block.touched(xLoc, yLoc)) {
				block.nextColor();
			}
		}
	}

	public void signalSwipe(int xLoc, int yLoc, int direction) {
		for (Block block : blocks) {
			if (block.touched(xLoc, yLoc)) {
				block.setSwiped(direction);
			}
		}
	}

	public void spawnBlock() {
		int startColor = (int) (Math.random() * activeColors.size());
		Block newBlock = new Block(gameWidth, gameHeight, activeColors,
				startColor);
		blocks.add(newBlock);
	}

	public GameStatus update() {
		timer.tick();
		
		if (paused)
		{
			return GameStatus.PAUSE;
		}

		nextSpawnTime -= timer.getDeltaInMillis();
		nextSwitchColorTime -= timer.getDeltaInMillis();
		nextAddColorTime -= timer.getDeltaInMillis();
		nextSpeedUpTime -= timer.getDeltaInMillis();

		if (nextSpawnTime <= 0) {
			spawnBlock();

			nextSpawnTime = getModifiedRand(STD_SPAWN_TIME, .85, 1.15);
		}

		if (nextSwitchColorTime <= 0 && colorChanging == false) {
			SoundPlayer.playSound(SoundPlayer.changeColor);
			leftEdgeNextColorIndex = activeColors.indexOf(leftEdgeColors[3]);
			while (activeColors.get(leftEdgeNextColorIndex) == leftEdgeColors[3]) {
				leftEdgeNextColorIndex = (int) (Math.random() * activeColors
						.size());
			}
			rightEdgeNextColorIndex = leftEdgeNextColorIndex;
			while (rightEdgeNextColorIndex == leftEdgeNextColorIndex) {
				rightEdgeNextColorIndex = (int) (Math.random() * activeColors
						.size());
			}

			colorChanging = true;
			colorChangeInterval = 250;
			colorChangeIndex = 0;

			leftEdgeColors[colorChangeIndex] = gameColors
					.get(leftEdgeNextColorIndex);
			rightEdgeColors[colorChangeIndex] = gameColors
					.get(rightEdgeNextColorIndex);
			// nextSwitchColorTime = getModifiedRand(STD_SWITCH_COLOR_TIME,
			// .9, 1.2);
		}

		if (colorChanging) {
			colorChangeInterval -= timer.getDeltaInMillis();

			if (colorChangeInterval < 0) {
				leftEdgeColors[colorChangeIndex] = gameColors
						.get(leftEdgeNextColorIndex);
				rightEdgeColors[colorChangeIndex] = gameColors
						.get(rightEdgeNextColorIndex);

				colorChangeIndex++;

				if (colorChangeIndex >= leftEdgeColors.length) {
					colorChanging = false;
					nextSwitchColorTime = getModifiedRand(
							STD_SWITCH_COLOR_TIME, .9, 1.2);
				}

				else {
					colorChangeInterval = 250;
				}
			}

		}

		if (nextAddColorTime <= 0) {
			int nextColorIndex = activeColors.size();

			if (nextColorIndex < gameColors.size()) {
				SoundPlayer.playSound(SoundPlayer.addColor);
				activeColors.add(gameColors.get(nextColorIndex));
			}

			nextAddColorTime = getModifiedRand(STD_ADD_COLOR_TIME, .9, 1.1);
		}
		
		if (nextSpeedUpTime <= 0)
		{
			SoundPlayer.playSound(SoundPlayer.faster);
			numSpeedUps++;
			
			if (numSpeedUps < maxSpeedUps)
			{
				Block.VERT_MOVE_CONST = (int)(Block.VERT_MOVE_CONST + BLOCK_SPEED_INCREASE);
				this.STD_SPAWN_TIME = (int)(this.STD_SPAWN_TIME - SPAWN_DECREASE);
				this.STD_SWITCH_COLOR_TIME = (int)(this.STD_SWITCH_COLOR_TIME - COLOR_CHANGE_DECREASE);
			}			
			
			nextSpeedUpTime = STD_SPEED_UP_TIME;
		}

		ArrayList<Block> deadBlocks = new ArrayList<Block>();

		for (Block block : blocks) {
			block.update(timer.getDeltaInSeconds());
			if (block.hitBottom()) {
				return GameStatus.GAMEOVER;
			}
			if (block.shouldDie()) {
				if (block.getSwipeDirection() < 0) {
					if (block.getColor() != leftEdgeColors[3]) {
						return GameStatus.GAMEOVER;
					}
				} else {
					if (block.getColor() != rightEdgeColors[3]) {
						return GameStatus.GAMEOVER;
					}
				}

				deadBlocks.add(block);
			}
		}

		for (Block block : deadBlocks) {
			blocks.remove(block);
			score++;

			if (score % 20 == 0) {
				SoundPlayer.playSound(SoundPlayer.awesome);
			}

		}

		return GameStatus.PLAY;
	}

	public long getModifiedRand(long standard, double low, double high) {
		double lower = low;
		double upper = high;
		double spawn = ((Math.random() * (upper - lower)) + lower);

		return (long) (standard * spawn);
	}

	public void reset() {
		if (firstLoad == false) {
			SoundPlayer.playSound(R.raw.start);
		} else {
			firstLoad = false;
		}

		this.paused = true;
		this.timer = new GameClock();
		this.blocks = new ArrayList<Block>();
		
		Block.VERT_MOVE_CONST = BASE_BLOCK_SPEED;

		this.gameColors = new ArrayList<Integer>();
		this.gameColors.add(Color.RED);
		this.gameColors.add(Color.BLUE);
		this.gameColors.add(Color.GREEN);
		this.gameColors.add(Color.YELLOW);

		this.activeColors = new ArrayList<Integer>();
		for (int i = 0; i < numStartColors; i++) {
			this.activeColors.add(this.gameColors.get(i));
		}

		this.leftEdgeColors = new int[4];
		this.rightEdgeColors = new int[4];

		for (int i = 0; i < rightEdgeColors.length; i++) {
			leftEdgeColors[i] = Color.RED;
			rightEdgeColors[i] = Color.BLUE;
		}
		
		this.STD_SPAWN_TIME = this.BASE_SPAWN_TIME;
		this.STD_SWITCH_COLOR_TIME = this.BASE_SWITCH_COLOR_TIME;

		this.nextSpawnTime = 0;
		this.nextSpeedUpTime = STD_SPEED_UP_TIME;
		this.nextAddColorTime = STD_ADD_COLOR_TIME;
		this.nextSwitchColorTime = STD_SWITCH_COLOR_TIME;
		this.numSpeedUps = 0;
		this.score = 0;
	}

}
