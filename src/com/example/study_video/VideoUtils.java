package com.example.study_video;

public class VideoUtils {

	/**初始化*/
	public native static void init();
	/**转码、压缩并储存为mp4文件*/
	public native static void decode(byte[] input);
	/**结束*/
	public native static void close();
	
	static{
	    	System.loadLibrary("avutil-54");
	    	System.loadLibrary("swresample-1");
	    	System.loadLibrary("avcodec-56");
	    	System.loadLibrary("avformat-56");
	    	System.loadLibrary("swscale-3");
	    	System.loadLibrary("postproc-53");
	    	System.loadLibrary("avfilter-5");
	    	System.loadLibrary("avdevice-56");
	    	System.loadLibrary("Study_Video");
	}
}
