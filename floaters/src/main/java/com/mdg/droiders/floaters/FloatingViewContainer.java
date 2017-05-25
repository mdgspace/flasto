package com.mdg.droiders.floaters;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

public class FloatingViewContainer {

    private View mFloatingView;
    private Context ctx;
    private WindowManager.LayoutParams floatingViewParams;

    public FloatingViewContainer(Context ctx) {
        this.ctx = ctx;
        this.mFloatingView = LayoutInflater.from(ctx).inflate(R.layout.layout_floating, null);
    }

    public View getmFloatingView() {
        return mFloatingView;
    }

    public WindowManager.LayoutParams getDefaultFloatingViewParams() {
        if (floatingViewParams == null) {
            floatingViewParams = new WindowManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
            floatingViewParams.gravity = Gravity.TOP | Gravity.START;        //Initially view will be added to top-left corner
            floatingViewParams.x = 0;
            floatingViewParams.y = 100;
        }
        return floatingViewParams;
    }

    public WindowManager.LayoutParams getFloatingViewParams() {
        return (WindowManager.LayoutParams) mFloatingView.getLayoutParams();
    }

}
