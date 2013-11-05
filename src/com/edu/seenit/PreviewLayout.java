package com.edu.seenit;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class PreviewLayout extends FrameLayout {
	double ratio = 3.0/4.0;

	public PreviewLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	public void setRatio(double nRatio){
		ratio = 1/nRatio;
		requestLayout();
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		if (width > height * ratio) {
				width = (int) (height * ratio + .5);
		} else {
			height = (int) (width / ratio + .5);
		}
		super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
	}
	
}
