package com.edu.seenit;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Rect;
import android.hardware.Camera.Area;
import android.hardware.Camera.Parameters;
import android.view.MotionEvent;
import android.widget.FrameLayout;


public class FocusManager{
	final int tab_to_focus = 0;
	final int default_focus = 1;
	
	private Parameters mParameters;
	private Focus focus;
	Callback mCallback;
	
	public interface Callback {
		public void capture();
		public void setFocus(List<Area> focusArea, List<Area> meteringArea);
		public void autoFocus();
	}
	
	public FocusManager(Parameters mPara, Focus focusView, Callback callback){
		focus = focusView;
		mCallback = callback;
		mParameters = mPara;
	}
	public void onAutoFocus(boolean success, int state){
		if(success){
			focus.focusFinish();
		}
		else
			focus.focusFail();
		
		if(state == default_focus)
			mCallback.capture();
	}
	public void updateFocus(){
		if(mParameters.getMaxNumFocusAreas() > 0){
			List<Area> focusAreas = mParameters.getFocusAreas();
			System.out.println(focusAreas.size());
		}
	}
	public boolean onTouch(int preWidth,int preHeight, MotionEvent me){
		int x = Math.round(me.getAxisValue(0));
		int y = Math.round(me.getAxisValue(1));
		int focusWidth = focus.getWidth();
		int focusHeight = focus.getHeight();
		int previewWidth = preWidth;
		int previewHeight = preHeight;
		List<Area> mFocusAreas = new ArrayList<Area>();
		List<Area> mMeteringAreas = new ArrayList<Area>();
		mFocusAreas.add(new Area(new Rect(), 1));
		mMeteringAreas.add(new Area(new Rect(), 1));
		
		calculateArea(x,y,focusWidth,focusHeight,previewWidth,previewHeight,mFocusAreas.get(0),1.0f);
		calculateArea(x,y,focusWidth,focusHeight,previewWidth,previewHeight,mMeteringAreas.get(0),1.5f);
		FrameLayout.LayoutParams p = (FrameLayout.LayoutParams)focus.getLayoutParams();
		p.setMargins(mFocusAreas.get(0).rect.left, mFocusAreas.get(0).rect.top, 0, 0);
		p.gravity = -1;
		focus.requestLayout();
		mCallback.setFocus(mFocusAreas,mMeteringAreas);
		mCallback.autoFocus();
		return true;
	}
	
	private void calculateArea(int x, int y, int focusWidth, int focusHeight, int previewWidth,
			int previewHeight, Area area, float multiple){
		int width = (int)(focusWidth * multiple);
		int height = (int)(focusHeight * multiple);
		int left, top;
		if(x-width/2<0)
			left = 0;
		else if(x-width/2>(previewWidth-width))
			left = previewWidth-width;
		else
			left = x-width/2;
		
		if(y-height/2<0)
			top = 0;
		else if(y-height/2>(previewHeight-height))
			top = previewHeight-height;
		else
			top = y-height/2;
		area.rect = new Rect(left, top, left + width, top + height);
	}
	
}
