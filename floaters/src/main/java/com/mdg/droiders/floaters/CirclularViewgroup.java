package com.mdg.droiders.floaters;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class CirclularViewgroup extends ViewGroup {

    private int MAX_VISIBLE_CIRCLES = 4;
    private int MIN_CIRCLES = 3;
    private double PI = 3.14159;
    private Paint paint;
    boolean isAdded = false;
    private CustomCircularFloatingView centerView;

    public CirclularViewgroup(Context context) {
        super(context);
        setUpPaint();
        centerView = new CustomCircularFloatingView(context);
        centerView.setColorNormal(getResources().getColor(R.color.another_fake));
        centerView.setFloatingViewSize(getSmallCircleRadius() * 2);
        super.addView(centerView);
    }

    public CirclularViewgroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUpPaint();
        centerView = new CustomCircularFloatingView(context);
        centerView.setColorNormal(getResources().getColor(R.color.another_fake));
        centerView.setFloatingViewSize(getSmallCircleRadius() * 2);
        super.addView(centerView);
    }

    public CirclularViewgroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUpPaint();
        centerView = new CustomCircularFloatingView(context);
        centerView.setColorNormal(getResources().getColor(R.color.another_fake));
        centerView.setFloatingViewSize(getSmallCircleRadius() * 2);
        super.addView(centerView);
    }

    public CirclularViewgroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setUpPaint();
        centerView = new CustomCircularFloatingView(context);
        centerView.setColorNormal(getResources().getColor(R.color.another_fake));
        centerView.setFloatingViewSize(getSmallCircleRadius() * 2);
        super.addView(centerView);
    }

    /**
     * Any layout manager scrolls will want this.
     */
    @Override
    public boolean shouldDelayChildPressedState() {
        return true;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        float xCenter = 0;
        float yCenter = getCircleDrawerSize();
        canvas.drawCircle(xCenter, yCenter, getCircleDrawerSize(), paint);
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int viewRadius = getCircleDrawerSize();
        int height = 2 * viewRadius;
        int w = resolveSizeAndState(viewRadius, widthMeasureSpec, 0);
        int h = resolveSizeAndState(height, heightMeasureSpec, 0);
        /*for(int i=0; i<getChildCount(); i++){
            View v = getChildAt(i);
            measureChildWithMargins(v, widthMeasureSpec, 0, heightMeasureSpec, 0);
        }*/
        setMeasuredDimension(w, h);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        int actualCount = 0;
        int rad = getSmallCircleRadius(), R = getNormalCircleRadius();
        for (int i = 1; i < count; i++)
            if (getChildAt(i).getVisibility() == VISIBLE)
                actualCount++;
        if (actualCount < MIN_CIRCLES)
            return;

        // add center view to layout
        View centerView = getChildAt(0);
        centerView.layout(rad, getCircleDrawerSize() - rad,rad , getCircleDrawerSize() + rad);

        // actual count is greater than 2
        int iter = 1;
        int min = Math.min(MAX_VISIBLE_CIRCLES, actualCount);
        double degrees = 180 / (min + 1);
        double rads = degrees * PI / 180;
        for (int i = 1; i < count && iter <= 4; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                int d = rad + 2 * R;
                int y1 = (int) (d * Math.cos(rads * iter));
                int x = (int) (d * Math.sin(rads * iter));
                int y = getCircleDrawerSize() - y1;
                int top = y - R;
                int left = x - R;
                int bottom = top + 2 * R;
                int right = left + 2 * R;
                child.layout(left, top, right, bottom);
                iter++;
            }
        }
    }

    private int getCircleDrawerSize() {
        return getResources().getDimensionPixelSize(R.dimen.size_circle_drawer);
    }

    private int getNormalCircleRadius() {
        return (getResources().getDimensionPixelSize(R.dimen.size_normal)) / 2;
    }

    private int getSmallCircleRadius() {
        return (getResources().getDimensionPixelSize(R.dimen.size_small)) / 2;
    }

    private void setUpPaint() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.fake));
    }
}
