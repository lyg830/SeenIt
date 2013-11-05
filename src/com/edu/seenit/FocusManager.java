package com.edu.seenit;

import java.util.List;

import android.hardware.Camera.Area;
import android.hardware.Camera.Parameters;


public class FocusManager{
	
	private Parameters mParameters;
	private Focus focus;
	Callback mCallback;
	
	public interface Callback {
		public void capture();
		//public void autoFocus();
	}
	
	public FocusManager(Parameters mPara, Focus focusView, Callback callback){
		focus = focusView;
		mCallback = callback;
		mParameters = mPara;
	}
	public void onAutoFocus(boolean success){
		if(success){
			focus.focusFinish();
		}
		else
			focus.focusFail();
		mCallback.capture();
	}
	public void updateFocus(){
		if(mParameters.getMaxNumFocusAreas() > 0){
			List<Area> focusAreas = mParameters.getFocusAreas();
			System.out.println(focusAreas.size());
		}
	}
	
	
}
