package com.mdg.droiders.floaters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Contains a floating view instance and relevant methods related to it.
 */
class FloatingViewContainer {

    private View mFloatingView;
    private View collapsedView;
    private View expandedView;
    private RelativeLayout.LayoutParams cachedRelativeParams;

    /**
     * Constructor for {@link FloatingViewContainer} class
     *
     * @param ctx                   The {@link Context} is needed to inflate floating view layout from xml
     * @param isHardwareAccelerated Set false to set {@link View#LAYER_TYPE_SOFTWARE} on the floating view
     * @see View#getLayerType()
     * @see View#setLayerType(int, android.graphics.Paint)
     * @see View#LAYER_TYPE_SOFTWARE
     * @see View#LAYER_TYPE_HARDWARE
     */
    FloatingViewContainer(Context ctx, boolean isHardwareAccelerated) {
        this.mFloatingView = LayoutInflater.from(ctx).inflate(R.layout.layout_floating, null);
        collapsedView = mFloatingView.findViewById(R.id.collapse_view);
        expandedView = mFloatingView.findViewById(R.id.expanded_container);
        if (isHardwareAccelerated)
            mFloatingView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    View getmFloatingView() {
        return mFloatingView;
    }

    /**
     * Generates default layout params for the floating view if not previously generated
     *
     * @return The default layout params for the floating view to use
     */
    RelativeLayout.LayoutParams getDefaultRelativeParams() {
        if (cachedRelativeParams == null) {
            cachedRelativeParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        return cachedRelativeParams;
    }

    /**
     * @return Gets layout params from floating view and returns the same.
     */
    RelativeLayout.LayoutParams getRelativeParams() {
        return (RelativeLayout.LayoutParams) mFloatingView.getLayoutParams();
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
