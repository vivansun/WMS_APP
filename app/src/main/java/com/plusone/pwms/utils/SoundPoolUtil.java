/*
 * Copyright 2016 licensed to mbpsoft Co, Ltd;
 * You may not use this file except in compliance with the 'License' from mbpsoft company
 * Please contact at info@mbpsoft.com
 */
package com.plusone.pwms.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;


/**
 * 提示音与振动 - 工具类.
 *
 * @author MBP.Limeng
 */
public class SoundPoolUtil {

	private static final int MAX_STREAMS = 2;
	private static final int DEFAULT_QUALITY = 0;
	private static final int DEFAULT_PRIORITY = 1;
	private static final float LEFT_VOLUME = 1.0f;
	private static final float RIGHT_VOLUME = 1.0f;
	private static final int LOOP = 0;
	private static final float RATE = 1.0f;

	private static SoundPoolUtil sSoundPoolUtil;

	/**
	 * 音频的相关类
	 */
	private static SoundPool mSoundPool;
	private static Context mContext;
	private static Vibrator mVibrator;


	private SoundPoolUtil(Context context) {
		mContext = context;
		//初始化行营的音频类
		intSoundPool();
		initVibrator();
	}

	private static void intSoundPool() {
		//根据不同的版本进行相应的创建
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			mSoundPool = new SoundPool.Builder()
					.setMaxStreams(MAX_STREAMS)
					.build();
		} else {
			mSoundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, DEFAULT_QUALITY);
		}
	}

	private static void initVibrator() {
		mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
	}

	public static SoundPoolUtil getInstance(Context context) {
		if (sSoundPoolUtil == null) {
			synchronized (SoundPoolUtil.class) {
				if (sSoundPoolUtil == null) {
					sSoundPoolUtil = new SoundPoolUtil(context);
				}
			}
		}
		return sSoundPoolUtil;
	}

	/**
	 * @param resId 音频的资源ID
	 * 方法描述: 开始播放音频
	 */
	public static void playVideo(int resId) {
		if (mSoundPool == null) {
			intSoundPool();
		}
		final int load = mSoundPool.load(mContext, resId, DEFAULT_PRIORITY);

		mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool mSoundPool, int sampleId, int status) {
				if (status == 0) {
					//第一个参数soundID
					//第二个参数leftVolume为左侧音量值（范围= 0.0到1.0）
					//第三个参数rightVolume为右的音量值（范围= 0.0到1.0）
					//第四个参数priority 为流的优先级，值越大优先级高，影响当同时播放数量超出了最大支持数时SoundPool对该流的处理
					//第五个参数loop 为音频重复播放次数，0为值播放一次，-1为无限循环，其他值为播放loop+1次
					//第六个参数 rate为播放的速率，范围0.5-2.0(0.5为一半速率，1.0为正常速率，2.0为两倍速率)
					mSoundPool.play(load, LEFT_VOLUME, RIGHT_VOLUME, DEFAULT_PRIORITY, LOOP, RATE);
				}
			}
		});

//		mSoundPool.play(load, LEFT_VOLUME, RIGHT_VOLUME, DEFAULT_PRIORITY, LOOP, RATE);

	}

	/**
	 * @param milliseconds 震动时间
	 * 方法描述: 开启相应的震动
	 */
	public static void startVibrator(long milliseconds) {
		if (mVibrator == null) {
			initVibrator();
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			VibrationEffect vibrationEffect = VibrationEffect.createOneShot(milliseconds, 100);
			mVibrator.vibrate(vibrationEffect);
		} else {
			mVibrator.vibrate(1000);
		}
	}

	/**
	 * @param resId        资源id
	 * @param milliseconds 震动时间
	 * 方法描述: 同时开始音乐和震动
	 */
	public static void startVideoAndVibrator(int resId, long milliseconds) {
		playVideo(resId);
		startVibrator(milliseconds);
	}

	/**
	 * 方法描述:  释放相应的资源
	 */
	public static void release() {
		//释放所有的资源
		if (mSoundPool != null) {
			mSoundPool.release();
			mSoundPool = null;
		}

		if (mVibrator != null) {
			mVibrator.cancel();
			mVibrator = null;
		}
	}
}