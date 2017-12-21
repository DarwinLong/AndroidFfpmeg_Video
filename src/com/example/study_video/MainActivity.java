package com.example.study_video;

import java.io.File;
import java.io.IOException;

import com.example.study_video.R;


import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.AsyncTask.Status;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	private static  String TAG = VideorRecordingTask.class.getSimpleName();
	SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private Camera mCamera;
	//记录是否在录制
	private boolean isRecording = false;
	
	VideorRecordingTask mVideoRecordingTask;
	private class VideorRecordingTask extends AsyncTask<byte[],Void, Void>{

		

		@Override
		protected Void doInBackground(byte[]... arg0) {
			// TODO Auto-generated method stub			
			VideoUtils.decode(arg0[0]);			
			return null;
		}
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		VideoUtils.init();
		
		mSurfaceView=(SurfaceView)findViewById(R.id.surfaceView1);
        SurfaceHolder holder=mSurfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        holder.addCallback(new SurfaceHolder.Callback() {
			
			private String TAG;

			@Override
			public void surfaceDestroyed(SurfaceHolder arg0) {
				// TODO Auto-generated method stub
				if(mCamera!=null)
				{
					mCamera.stopPreview();
					VideoUtils.close();
					mSurfaceView = null;  
					mSurfaceHolder = null;   
				}
			}
			
			@Override
			public void surfaceCreated(SurfaceHolder arg0) {
				// TODO Auto-generated method stub
				try{		
					if(mCamera!=null){
						mCamera.setPreviewDisplay(arg0);
						mSurfaceHolder=arg0;
					}
				}catch(IOException exception){
					Log.e(TAG, "Error setting up preview display", exception);
				}
			}
			
			@Override
			public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				if(mCamera==null) return;
				Camera.Parameters parameters=mCamera.getParameters();			
				parameters.setPreviewSize(640,480);
				parameters.setPictureSize(640,480);				
				mCamera.setParameters(parameters);
				try{
					mCamera.startPreview();
					mSurfaceHolder=arg0;
				}catch(Exception e){
					Log.e(TAG, "could not start preview", e);
					mCamera.release();
					mCamera=null;
				}
			}
		});
	}

	private PreviewCallback mPreviewCallback=new PreviewCallback(){
		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			if(mVideoRecordingTask!=null){
				//AsyncTask.Status.PENDING标志 任务还未执行
				if(mVideoRecordingTask.getStatus()==AsyncTask.Status.PENDING){
					mVideoRecordingTask.cancel(true);
				}else if(mVideoRecordingTask.getStatus()==AsyncTask.Status.RUNNING){
					return;
				}
			}
			mVideoRecordingTask=new VideorRecordingTask();
			mVideoRecordingTask.execute(data);
		}
	};
	
	public void mPlay(View btn){
		//String input = new File(Environment.getExternalStorageDirectory(),"input.mp4").getAbsolutePath();
		//String output = new File(Environment.getExternalStorageDirectory(),"output_1280x720_yuv420p.yuv").getAbsolutePath();
		//VideoUtils.decode(input, output);
		if(mCamera==null) return;
		if(isRecording){
			//停止
			isRecording=false;
			((Button)btn).setText("开始录制");
			mCamera.setPreviewCallback(null);
		}else{
			//开始
			isRecording=true;
			((Button)btn).setText("停止录制");
			mCamera.setPreviewCallback(mPreviewCallback);
			
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
    	if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.GINGERBREAD){
    		mCamera=Camera.open(0);
    	}else
    	{
    		mCamera=Camera.open();
    	}
	}
	
    @Override
    protected void onStop(){
    	super.onPause();
  
    	if(mCamera!=null){
    		mCamera.release();
    		mCamera=null;
    	}
    }
}
