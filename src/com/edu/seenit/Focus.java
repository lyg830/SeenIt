package com.edu.seenit;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class Focus extends View {


	public Focus(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	private void setDrawable(int resid) {
        this.setBackgroundResource(resid);
    }

    public void showFocus() {
        setDrawable(R.drawable.focus);
    }
    public void focusFinish() {
    	setDrawable(R.drawable.focus1);	
    }
    public void focusFail() {
    	setDrawable(R.drawable.focus2);	
    }
}
