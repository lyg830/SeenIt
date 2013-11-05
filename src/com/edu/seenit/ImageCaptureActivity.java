package com.edu.seenit;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

public class ImageCaptureActivity extends Activity implements FocusManager.Callback, OnClickListener,Camera.AutoFocusCallback, SurfaceHolder.Callback{
	SurfaceView mSurface;
	SurfaceHolder holder;
	ImageButton mButton;
	Camera mCamera;
	Focus focus;
	Bitmap mBitmap; 
	FocusManager mFManager;
	PreviewLayout preLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_image_capture);
		mSurface = (SurfaceView)findViewById(R.id.mySurface);	
		mButton = (ImageButton)findViewById(R.id.myBtn);
		mButton.setOnClickListener(this);
		holder = mSurface.getHolder();
		holder.addCallback(this);
		focus = (Focus)findViewById(R.id.focus);
		focus.showFocus();
		preLayout = (PreviewLayout)findViewById(R.id.CameraLayout);
	
	}
	

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (mCamera != null){
            //mCamera.setPreviewCallback(null);
           	holder.removeCallback(this);
            mCamera.release();
            mCamera = null;
        }
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		holder = mSurface.getHolder();
		holder.addCallback(this);
		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View arg0) {
		 mCamera.autoFocus(this);  
     
	}
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int width, int height) {
		// TODO Auto-generated method stub
		
		Camera.Parameters para = mCamera.getParameters();
		mCamera.setDisplayOrientation(90);
		para.setPictureFormat(ImageFormat.JPEG);
		para.setFocusMode("auto");
		Camera.Size picSize = para.getPictureSize();
		double nRatio = (double)picSize.width/picSize.height;
		preLayout.setRatio(nRatio);
		Log.i("pic size: ", "width:" + picSize.width + "height:" + picSize.height + "ratio: " + nRatio);
		Camera.Size preSize = getPreSize(para);
		para.setPreviewSize(preSize.height, preSize.width);
		/*
		 * These cannot work on my Samsung device.
		 * You guys can try on yours.
		 * 
		 * Camera.Size picSize = getPicSize(para);
		 * para.setPictureSize(picSize.height, picSize.width);
		*/
		
		mCamera.setParameters(para);
		mFManager = new FocusManager(para,focus,this);
		mCamera.startPreview();

		Log.i("My best presize: ", "width:" + preSize.height + " height: " + preSize.width);
	}
	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		if(mCamera==null){
			mCamera = Camera.open();
			try{
				mCamera.setPreviewDisplay(arg0);
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		focus.showFocus();
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
	}
	
	@Override
	public void onAutoFocus(boolean success, Camera camera) {
		// TODO Auto-generated method stub
		mFManager.onAutoFocus(success);
		//mFManager.updateFocus();
	}
	
	
    
    private Camera.Size getPreSize(Camera.Parameters para){
    	
    	List<Camera.Size> previewSizes = para.getSupportedPreviewSizes();
    	Camera.Size optimal = getOptimal(previewSizes, (double) 4.0 / 3.0);
    	if(optimal == null){
    		optimal = previewSizes.get(0);
        	for(int i=0; i<previewSizes.size();i++){
        		if(previewSizes.get(i).width*previewSizes.get(i).height>optimal.width*optimal.height){
        			optimal = previewSizes.get(i);
        		}
        	}
    	}
    	
		return optimal;
    	
    }
    private Camera.Size getOptimal(List<Camera.Size> sizes, double ratio){
    	Camera.Size optimal = null;
    	if(sizes.isEmpty())
    		return optimal;
    	int targetHeight = preLayout.getWidth();
    	double diff = Double.MAX_VALUE;
    	for(Camera.Size size : sizes){
    		double supportedRatio = (double)size.width/size.height;
    		if(Math.abs(supportedRatio-ratio)>0.001)continue;
    		else{
    			if(Math.abs(targetHeight-size.height)<diff){
    				optimal = size;
    				diff = Math.abs(targetHeight-size.height);
    			}
    		}
    	}
		return optimal;
    	
    }
    @Override
	public void capture() {
		// TODO Auto-generated method stub
    	mCamera.takePicture(null, null, new myPictureCallback());
	}

    /*
     * cannot work on my device.
     * 
    private Camera.Size getPicSize(Camera.Parameters para){
    	
    	Camera.Size myPicSize;
    	List<Camera.Size> pictureSizes = para.getSupportedPictureSizes();
    	myPicSize = pictureSizes.get(0);
    	for(int i=0; i<pictureSizes.size();i++){
    		if(pictureSizes.get(i).width*pictureSizes.get(i).height>myPicSize.width*myPicSize.height){
    			myPicSize = pictureSizes.get(i);
    		}
    	}
		return myPicSize; 	
    }
    */

    private final class myPictureCallback implements PictureCallback{

    	@Override
    	public void onPictureTaken(byte[] data, Camera camera) {
    		// TODO Auto-generated method stub
    		mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            Bitmap modifiedbMap;
            int orientation;
            if(mBitmap.getHeight()<mBitmap.getWidth()){
            	orientation = 90;
            }
            else
            	orientation = 0;
            if(orientation!=0){
            	Matrix mMatrix = new Matrix();
            	mMatrix.postRotate(orientation);
            	modifiedbMap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), mMatrix, true);
            }
            else
            	modifiedbMap = Bitmap.createScaledBitmap(mBitmap, mBitmap.getWidth(), mBitmap.getHeight(), true);
            final CharSequence myDate = DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance());
            File f = new File(Environment.getExternalStorageDirectory() + "/SeenIt/");
            if(!f.exists()) {
            	if(!f.mkdir()){
            		Log.e("Error", "Problem creating a folder");
            	}
            }
            File file = new File(Environment.getExternalStorageDirectory().getPath()+"/SeenIt/" + myDate +".jpg");
            try {
            	file.createNewFile();
            	BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
            	modifiedbMap.compress(Bitmap.CompressFormat.JPEG, 100, bufferedOutputStream);
            	bufferedOutputStream.flush();
            	bufferedOutputStream.close();
            	Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            	mediaScanIntent.setData(Uri.fromFile(file));
            	sendBroadcast(mediaScanIntent);
            	camera.stopPreview();
            	camera.startPreview();
            	focus.showFocus();
            	mBitmap.recycle();
            } catch(IOException e){
            	e.printStackTrace();
            }
    	}
    	
    }
	

}

