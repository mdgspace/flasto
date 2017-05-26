package com.mdg.droiders.floaters;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

public class CustomCircularFloatingView extends android.support.v7.widget.AppCompatImageView {

    public static final int SIZE_NORMAL = 0, TYPE_IMAGE = 0;
    public static final int SIZE_SMALL = 1, TYPE_IMAGE_BUTTON = 1;

    private int colorNormal;
    private int colorPressed;
    private int colorRipple;
    private Paint bMapPaint;
    private Rect rect;
    private int floatingViewSize;
    private float floatingViewRadius;
    private Paint paint;
    private int type;
    private PorterDuffXfermode porterDuffXfermode;

    public CustomCircularFloatingView(Context context) {
        super(context);
        setupPaint();
    }

    public CustomCircularFloatingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupAttributes(attrs);
        setupPaint();
    }

    public CustomCircularFloatingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupAttributes(attrs);
        setupPaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float xCenter = canvas.getWidth() / 2.0f;
        float yCenter = canvas.getHeight() / 2.0f;
        if (type == TYPE_IMAGE_BUTTON) {// INCOMPLETE CODE
            canvas.drawCircle(xCenter, yCenter, floatingViewRadius, paint);
        } else {
            Drawable drawable = getDrawable();
            if (drawable == null)
                drawable = getResources().getDrawable(R.drawable.default_pic, null);
            Bitmap b = ((BitmapDrawable) drawable).getBitmap();
            Bitmap scaledBitmap = createCenterCroppedBMap(b, getCircleSize());
            Bitmap bitmap = scaledBitmap.copy(Bitmap.Config.ARGB_8888, true);
            canvas.drawARGB(0, 0, 0, 0);
            canvas.drawCircle(xCenter, yCenter, floatingViewRadius, bMapPaint);
            bMapPaint.setXfermode(porterDuffXfermode);
            canvas.drawBitmap(bitmap, rect, rect, bMapPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int contentWidth = getCircleSize();

        // Resolve the width based on our minimum and the measure spec
        int minw = contentWidth + getPaddingLeft() + getPaddingRight();
        int w = resolveSizeAndState(minw, widthMeasureSpec, 0);

        // Ask for a height that would let the view get as big as it can
        int minh = contentWidth + getPaddingBottom() + getPaddingTop();
        int h = resolveSizeAndState(minh, heightMeasureSpec, 0);

        // Calling this method determines the measured width and height
        // Retrieve with getMeasuredWidth or getMeasuredHeight methods later
        setMeasuredDimension(w, h);
    }

    private void setupAttributes(AttributeSet attrs) {
        // Obtain a typed array of attributes
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.CustomCircularFloatingView, 0, 0);
        floatingViewRadius = ((float) getCircleSize()) / 2;

        // Extract custom attributes into member variables
        try {
            colorNormal = a.getColor(R.styleable.CustomCircularFloatingView_colorNormal, Color.BLACK);
            colorPressed = a.getColor(R.styleable.CustomCircularFloatingView_colorPressed, Color.BLACK);
            colorRipple = a.getColor(R.styleable.CustomCircularFloatingView_colorRipple, Color.BLACK);
            floatingViewSize = a.getInt(R.styleable.CustomCircularFloatingView_floatingViewSize, SIZE_NORMAL);
            type = a.getInt(R.styleable.CustomCircularFloatingView_floatingViewType, TYPE_IMAGE);
        } finally {
            // TypedArray objects are shared and must be recycled.
            a.recycle();
        }
    }

    public int getColorNormal() {
        return colorNormal;
    }

    public void setColorNormal(int colorNormal) {
        this.colorNormal = colorNormal;
        invalidate();
        requestLayout();
    }

    public int getColorPressed() {
        return colorPressed;
    }

    public void setColorPressed(int colorPressed) {
        this.colorPressed = colorPressed;
        invalidate();
        requestLayout();
    }

    public int getColorRipple() {
        return colorRipple;
    }

    public void setColorRipple(int colorRipple) {
        this.colorRipple = colorRipple;
        invalidate();
        requestLayout();
    }

    public int getFloatingViewSize() {
        return floatingViewSize;
    }

    public void setFloatingViewSize(int floatingViewSize) {
        this.floatingViewSize = floatingViewSize;
        invalidate();
        requestLayout();
    }

    private void setupPaint() {
        if (type == TYPE_IMAGE_BUTTON) {
            paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(colorNormal);
        } else {
            int tmpColor = 0xff424242;
            bMapPaint = new Paint();
            bMapPaint.setAntiAlias(true);
            bMapPaint.setColor(tmpColor);
            rect = new Rect(0, 0, getCircleSize(), getCircleSize());
            porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        }
    }

    private int getCircleSize() {
        return getResources().getDimensionPixelSize(floatingViewSize == SIZE_NORMAL
                ? R.dimen.size_normal : R.dimen.size_small);
    }

    private Bitmap createCenterCroppedBMap(Bitmap srcBmp, int scaleVal) {
        Bitmap dstBmp;
        if (srcBmp.getWidth() >= srcBmp.getHeight()) {

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    srcBmp.getWidth() / 2 - srcBmp.getHeight() / 2,
                    0,
                    srcBmp.getHeight(),
                    srcBmp.getHeight()
            );

        } else {

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    0,
                    srcBmp.getHeight() / 2 - srcBmp.getWidth() / 2,
                    srcBmp.getWidth(),
                    srcBmp.getWidth()
            );
        }
        return Bitmap.createScaledBitmap(dstBmp, scaleVal, scaleVal, false);
    }

}
