package com.mdg.droiders.floaters;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

class FloatingViewContainer {

    private View mFloatingView;
    private View collapsedView;
    private View expandedView;
    private RelativeLayout.LayoutParams cachedRelativeParams;
    private WindowManager.LayoutParams cachedWindowParams;

    FloatingViewContainer(Context ctx, boolean isHardwareAccelerated) {
        this.mFloatingView = LayoutInflater.from(ctx).inflate(R.layout.layout_floating, null);
        collapsedView = mFloatingView.findViewById(R.id.collapse_view);
        expandedView = mFloatingView.findViewById(R.id.expanded_container);
        if(isHardwareAccelerated)
            mFloatingView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    /*FloatingViewContainer(Context ctx, boolean b) {
        this.mFloatingView = LayoutInflater.from(ctx).inflate(R.layout.layout_floating, null);
    }*/

    View getmFloatingView() {
        return mFloatingView;
    }

    /*WindowManager.LayoutParams getDefaultRelativeParams() {
        if (floatingViewParams == null) {
            floatingViewParams = new WindowManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
            floatingViewParams.gravity = Gravity.TOP | Gravity.START;        //Initially view will be added to top-left corner
            floatingViewParams.x = 0;
            floatingViewParams.y = 100;
        }
        return floatingViewParams;
    }*/
    RelativeLayout.LayoutParams getDefaultRelativeParams() {
        if (cachedRelativeParams == null) {
            cachedRelativeParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        return cachedRelativeParams;
    }

    /*WindowManager.LayoutParams getDefaultWindowParams() {
        if (cachedWindowParams == null) {
            cachedWindowParams = new WindowManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
            cachedWindowParams.gravity = Gravity.TOP | Gravity.START;        //Initially view will be added to top-left corner
            cachedWindowParams.x = 0;
            cachedWindowParams.y = 100;
        }
        return cachedWindowParams;
    }*/

    /*WindowManager.LayoutParams getRelativeParams() {
        return (WindowManager.LayoutParams) mFloatingView.getLayoutParams();
    }*/

    RelativeLayout.LayoutParams getRelativeParams() {
        return (RelativeLayout.LayoutParams) mFloatingView.getLayoutParams();
    }

    /*WindowManager.LayoutParams getWindowParams() {
        return (WindowManager.LayoutParams) mFloatingView.getLayoutParams();
    }*/

    /**
     * Detect if the floating view is collapsed or expanded.
     *
     * @return true if the floating view is collapsed.
     */
    boolean isViewCollapsed() {
        return mFloatingView == null || mFloatingView.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
    }

    View getCollapsedView() {
        return collapsedView;
    }

    View getExpandedView() {
        return expandedView;
    }
}
