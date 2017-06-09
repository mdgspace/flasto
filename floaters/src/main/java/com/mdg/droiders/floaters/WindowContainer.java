package com.mdg.droiders.floaters;

import android.graphics.Point;
import android.view.WindowManager;
import android.widget.RelativeLayout;

/**
 * Container class that is an extension of {@link ExpandedWindow} class that
 * contains reference to a {@link SheetLayoutContainer} and a {@link FloatingViewContainer}
 * and contains methods that help set the two views in the Expanded Window Relative layout
 */
class WindowContainer {

    private SheetLayoutContainer sheetLayoutContainer;
    private FloatingViewContainer floatingView;
    /**
     * x coordinate of the floating view at the beginning of motion event
     */
    private float initialX;
    /**
     * y coordinate of the floating view at the beginning of motion event
     */
    private float initialY;
    /**
     * x coordinate of initial touch event
     */
    private float initialTouchX;
    /**
     * y coordinate of initial touch event
     */
    private float initialTouchY;

    /**
     * Constructor for class {@link WindowContainer}
     *
     * @param sheetLayoutContainer {@link SheetLayoutContainer} instance to use
     * @param floatingView         {@link FloatingViewContainer} instance to use
     */
    WindowContainer(SheetLayoutContainer sheetLayoutContainer,
                    FloatingViewContainer floatingView) {
        this.sheetLayoutContainer = sheetLayoutContainer;
        this.floatingView = floatingView;
    }

    /**
     * Stores initial position {@link #initialX} and {@link #initialY} of floating view
     *
     * @param initialX x coordinate of the floating view
     * @param initialY y coordinate of floating view
     */
    void setInitialPos(float initialX, float initialY) {
        this.initialX = initialX;
        this.initialY = initialY;
    }

    /**
     * Stores coordinates {@link #initialTouchX} and {@link #initialTouchY} at which initial touch event occurred
     *
     * @param initialTouchX x coordinate of initial touch event
     * @param initialTouchY y coordinate of initial touch event
     */
    void setInitialTouchPos(float initialTouchX, float initialTouchY) {
        this.initialTouchX = initialTouchX;
        this.initialTouchY = initialTouchY;
    }

    /**
     * @return {@link #initialX}
     */
    float getInitialX() {
        return initialX;
    }

    /**
     * @return {@link #initialY}
     */
    float getInitialY() {
        return initialY;
    }

    /**
     * @return {@link #initialTouchX}
     */
    float getInitialTouchX() {
        return initialTouchX;
    }

    /**
     * @return {@link #initialTouchY}
     */
    float getInitialTouchY() {
        return initialTouchY;
    }

    /**
     * Positions floating view at left edge of phone if its x coordinate is
     * less than half of the size of device screen else positions it at the right edge.
     * <p>Similar to {@link CollapsedWindow#setFloatingViewPos(WindowManager, Point)}
     * , except the floating view's parent is a Relative Layout</p>
     *
     * @param parentWidth Width of {@link ExpandedWindow#window}
     */
    void setFloatingViewPos(int parentWidth) {
        int midX = parentWidth / 2;
        int finalX = 0;
        if (floatingView.getmFloatingView().getX() >= midX)
            finalX = parentWidth - floatingView.getmFloatingView().getWidth();
        else if (floatingView.getmFloatingView().getX() < midX)
            finalX = 0;
        RelativeLayout.LayoutParams lp = floatingView.getRelativeParams();
        lp.leftMargin = finalX;
        floatingView.getmFloatingView().setLayoutParams(lp);
    }

    SheetLayoutContainer getSheetLayoutContainer() {
        return sheetLayoutContainer;
    }

    FloatingViewContainer getFloatingContainer() {
        return floatingView;
    }
}
