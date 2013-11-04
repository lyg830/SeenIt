package com.edu.seenit;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
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

public class ImageCaptureActivity extends Activity implements OnClickListener,Camera.AutoFocusCallback, SurfaceHolder.Callback{
	SurfaceView mSurface;
	SurfaceHolder holder;
	ImageButton mButton;
	Camera mCamera;
	Focus focus;
	private Bitmap mBitmap;
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
		
	}
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.camera, menu);
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
		mCamera.startPreview();
		
		Log.i("My best presize: ", "width:" + preSize.height + " height: " + preSize.width);
	}
	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		
		//TODO hardcoding to 0. Need to check num of cameras
		//and select back-camera if appropriate. Also need
		//To check that device has camera before this point
		if(mCamera==null){
			mCamera = Camera.open(0);
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
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
	}
	
	@Override
	public void onAutoFocus(boolean success, Camera camera) {
		// TODO Auto-generated method stub
		mCamera.takePicture(null, null, callback);
        
	}
	
	private Camera.PictureCallback callback = new Camera.PictureCallback() {
    
    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
    
    // TODO Auto-generated method stub
    //
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
            File file = new File(Environment.getExternalStorageDirectory().getPath()+"/DCIM/Camera/" + myDate +".jpg");
            try {
            	file.createNewFile();
            	BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
            	modifiedbMap.compress(Bitmap.CompressFormat.JPEG, 100, bufferedOutputStream);
            	bufferedOutputStream.flush();
            	bufferedOutputStream.close();
            	mCamera.stopPreview();
            	mCamera.startPreview();
            	mBitmap.recycle();
            } catch(IOException e){
            	e.printStackTrace();
            }
}
};

private Camera.Size getPreSize(Camera.Parameters para){

Camera.Size myPreSize;
List<Camera.Size> previewSizes = para.getSupportedPreviewSizes();
myPreSize = previewSizes.get(0);
for(int i=0; i<previewSizes.size();i++){
if(previewSizes.get(i).width*previewSizes.get(i).height>myPreSize.width*myPreSize.height){
myPreSize = previewSizes.get(i);
}
}
return myPreSize;

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





}
