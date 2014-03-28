package com.example.sortgame3;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class SoundPlayer {
	
	public static final int start = R.raw.start;
	public static final int changeColor = R.raw.switchcolor;
	public static final int addColor = R.raw.addcolor;
	public static final int awesome = R.raw.awesome;
	public static final int gameover = R.raw.gameover;
	public static final int faster = R.raw.faster;
	public static final int music = R.raw.cephalopod;
	
	public static final int numSounds = 7; // the number of sounds above
	
	private static SoundPool soundPool;
	private static HashMap<Integer, Integer> soundPoolMap;
	
	public static MediaPlayer player;
	
	public static void initSounds(Context context)
	{
		soundPool = new SoundPool(numSounds, AudioManager.STREAM_MUSIC, 100);
		soundPoolMap = new HashMap<Integer, Integer>(numSounds);
		
		soundPoolMap.put(start, soundPool.load(context, start, 1));
		soundPoolMap.put(changeColor, soundPool.load(context, changeColor, 1));
		soundPoolMap.put(addColor, soundPool.load(context, addColor, 1));
		soundPoolMap.put(awesome, soundPool.load(context, awesome, 1));
		soundPoolMap.put(gameover, soundPool.load(context, gameover, 1));
		soundPoolMap.put(faster, soundPool.load(context, faster, 1));
		soundPoolMap.put(music, soundPool.load(context, music, 1));
	}

	public static void playSound(int soundID)
	{
		float volume = 1.0f;
		
		soundPool.play(soundPoolMap.get(soundID), volume, volume, 1, 0, 1f);
	}
	
	public static void playSoundMedia(Context context, int soundID)
	{
		player = MediaPlayer.create(context, soundID);
		player.setLooping(true);
		player.start();
	}
	
}
