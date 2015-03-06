package com.DreamTV;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class TransparentPanel extends LinearLayout {

	
	private Paint	innerPaint/*, borderPaint */;
    
	public TransparentPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public TransparentPanel(Context context) {
		super(context);
		init();
	}

	private void init() 
	{
		setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, Gravity.BOTTOM ));
		
		innerPaint = new Paint();
		innerPaint.setARGB(100, 0x44, 0x9D, 0xEF); //gray
		innerPaint.setAntiAlias(true);

//		borderPaint = new Paint();
//		borderPaint.setARGB(255, 255, 255, 255);
//		borderPaint.setAntiAlias(true);
//		borderPaint.setStyle(Style.STROKE);
//		borderPaint.setStrokeWidth(2);
	}
	
    @Override
    protected void dispatchDraw(Canvas canvas) {
    	
    	RectF drawRect = new RectF();
    	drawRect.set(0,0, getMeasuredWidth(), getMeasuredHeight());
    	
    	canvas.drawRoundRect(drawRect, 5, 5, innerPaint);
		//canvas.drawRoundRect(drawRect, 5, 5, borderPaint);
		
		super.dispatchDraw(canvas);
    }

}
