package com.mdg.droiders.floaters;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import static java.lang.Math.min;

public class CustomCircularFloatingView extends View {

    private int color;
    private float floatingViewRadius;
    private Paint paint;

    public CustomCircularFloatingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupAttributes(attrs);
        setupPaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        floatingViewRadius = ((float) min(canvas.getWidth(), canvas.getHeight())) / 2;
        float xcenter = ((float) getWidth()) / 2, ycenter = ((float) getHeight()) / 2;
        canvas.drawCircle(xcenter,ycenter,floatingViewRadius,paint);
    }

    private void setupAttributes(AttributeSet attrs) {
        // Obtain a typed array of attributes
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.CustomCircularFloatingView, 0, 0);
        // Extract custom attributes into member variables
        try {
            color = a.getColor(R.styleable.CustomCircularFloatingView_color, Color.BLACK);
        } finally {
            // TypedArray objects are shared and must be recycled.
            a.recycle();
        }
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        invalidate();
        requestLayout();
    }

    private void setupPaint() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
    }

}
