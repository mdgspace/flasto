package com.mdg.droiders.floaters;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

class FloatingViewContainer {

    private View mFloatingView;
    private View collapsedView;
    private View expandedView;
    private WindowManager.LayoutParams floatingViewParams;

    FloatingViewContainer(Context ctx) {
        this.mFloatingView = LayoutInflater.from(ctx).inflate(R.layout.layout_floating, null);
        collapsedView = mFloatingView.findViewById(R.id.collapse_view);
        expandedView = mFloatingView.findViewById(R.id.expanded_container);
    }

    View getmFloatingView() {
        return mFloatingView;
    }

    WindowManager.LayoutParams getDefaultFloatingViewParams() {
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

    WindowManager.LayoutParams getFloatingViewParams() {
        return (WindowManager.LayoutParams) mFloatingView.getLayoutParams();
    }

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
