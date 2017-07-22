package com.mdg.droiders.floaters;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

/**
 * Contains a Relative Layout that has all the floating views as its child view
 * and various methods to implement "floatation" of the chat heads.
 */
class CollapsedWindow {
    /**
     * It is the Relative Layout that is used as a container
     * for all elements in the {@link CollapsedWindow}
     */
    private RelativeLayout window;
    /**
     * {@link FloatingViewContainer} array
     */
    private FloatingViewContainer[] containerFloat;
    /**
     * x coordinate of the floating view at the beginning of motion event
     */
    private int initialX;
    /**
     * y coordinate of the floating view at the beginning of motion event
     */
    private int initialY;
    /**
     * x coordinate of initial touch event
     */
    private float initialTouchX;
    /**
     * y coordinate of initial touch event
     */
    private float initialTouchY;
    private collapsedWindowListener mListener;

    /**
     * an interface to notify service about click event or overlapping with the
     * {@link FloatingViewService#mClosingButtonView} and the {@link #window)}
     */
    interface collapsedWindowListener {
        void clickHappened();

        void overlapped();
    }

    /**
     * Constructor for {@link CollapsedWindow}
     *
     * @param context        The {@link Context} to use.
     * @param containerFloat Takes indefinite {@link FloatingViewContainer} instances
     *                       and for adding them to the {@link #window}
     */
    CollapsedWindow(Context context, FloatingViewContainer... containerFloat) {
        this.containerFloat = containerFloat;
        window = new RelativeLayout(context);
    }

    /**
     * Adds all the floating views contained in {@link #containerFloat} to {@link #window}
     */
    void addChildViews() {
        for (FloatingViewContainer viewContainer : containerFloat)
            window.addView(viewContainer.getmFloatingView(),
                    viewContainer.getDefaultRelativeParams());
    }

    /**
     * @return {@link #window}
     */
    View getWindow() {
        return window;
    }

    private WindowManager.LayoutParams getParams() {
        return (WindowManager.LayoutParams) window.getLayoutParams();
    }

    /**
     * Stores initial position {@link #initialX} and {@link #initialY} of floating view
     *
     * @param initialX x coordinate of the floating view
     * @param initialY y coordinate of floating view
     */
    private void setInitialPos(int initialX, int initialY) {
        this.initialX = initialX;
        this.initialY = initialY;
    }

    /**
     * Stores coordinates {@link #initialTouchX} and {@link #initialTouchY} at which initial touch event occurred
     *
     * @param initialTouchX x coordinate of initial touch event
     * @param initialTouchY y coordinate of initial touch event
     */
    private void setInitialTouchPos(float initialTouchX, float initialTouchY) {
        this.initialTouchX = initialTouchX;
        this.initialTouchY = initialTouchY;
    }

    /**
     * @return {@link #initialX}
     */
    private int getInitialX() {
        return initialX;
    }

    /**
     * @return {@link #initialY}
     */
    private int getInitialY() {
        return initialY;
    }

    /**
     * @return {@link #initialTouchX}
     */
    private float getInitialTouchX() {
        return initialTouchX;
    }

    /**
     * @return {@link #initialTouchY}
     */
    private float getInitialTouchY() {
        return initialTouchY;
    }

    /**
     * Positions floating view at left edge of phone if its x coordinate is
     * less than half of the size of device screen else positions it at the right edge.
     *
     * @param mWindowManager {@link WindowManager} instance
     * @param size           size of screen
     */
    private void setFloatingViewPos(WindowManager mWindowManager, Point size) {
        for (FloatingViewContainer viewContainer : containerFloat) {
            WindowManager.LayoutParams params = getParams();
            int midX = size.x / 2;
            if (params.x >= midX)
                params.x = size.x;
            else if (params.x < midX)
                params.x = 0;
            //update the layout with new X and Y coordinates
            mWindowManager.updateViewLayout(window, params);
        }
    }

    /**
     * {@link #window} will be added to the screen window at the top left position
     *
     * @param wm {@link WindowManager} instance
     */
    void addToWindow(WindowManager wm) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        layoutParams.gravity = Gravity.TOP | Gravity.START;
        layoutParams.x = 0;
        layoutParams.y = 100;
        wm.addView(window, layoutParams);
        window.setVisibility(View.VISIBLE);
    }

    private boolean isOverlapping(View v1, View v2) {
        Rect rc1, rc2;
        // Location holder
        int[] loc = new int[2];

        v1.getLocationOnScreen(loc);
        rc1 = new Rect(loc[0], loc[1], loc[0] + v1.getWidth(), loc[1] + v1.getHeight());

        v2.getLocationOnScreen(loc);
        rc2 = new Rect(loc[0], loc[1], loc[0] + v2.getWidth(), loc[1] + v2.getHeight());
        return Rect.intersects(rc1, rc2);
    }

    /**
     * Sets touch listener on the collapsed {@link #window}
     *
     * @param mWindowManager     The {@link WindowManager} instance to use
     * @param size               {@link Point} instance containing screen size information.
     * @param mClosingButtonView The closeButtonView to see if the collapsed {@link #window} overlaps with it.
     */
    void setOnTouchListenerOnWindow(final WindowManager mWindowManager, final Point size, final View mClosingButtonView) {
        // Making the floating widget responsible to the touch events by setting an onTouchListener

        window.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        WindowManager.LayoutParams params = getParams();
                        //remember the initial position
                        setInitialPos(params.x, params.y);
                        //get the touch location
                        setInitialTouchPos(event.getRawX(), event.getRawY());
                        mClosingButtonView.setVisibility(View.VISIBLE);
                        return true;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        //update the new parameters of the position
                        WindowManager.LayoutParams params = getParams();
                        params.x = getInitialX() + (int) (event.getRawX() - getInitialTouchX());
                        params.y = getInitialY() + (int) (event.getRawY() - getInitialTouchY());

                        //update the layout with new X and Y coordinates
                        mWindowManager.updateViewLayout(window, params);
                        return true;
                    }
                    case MotionEvent.ACTION_UP: {
                        // Since we have implemented the onTouchListener therefore we cannot implement onClickListener
                        // Therefor we make changes to the onTouchListener to handle touch events
                        setFloatingViewPos(mWindowManager, size);

                        int diffX = (int) (event.getRawX() - getInitialTouchX());
                        int diffY = (int) (event.getRawY() - getInitialTouchY());
                        if (diffX < 10 && diffY < 10) {
                            mListener.clickHappened();
                        }
                        mClosingButtonView.setVisibility(View.GONE);
                        if (isOverlapping(mClosingButtonView, window))
                            mListener.overlapped();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    /**
     * Set a {@link collapsedWindowListener} after implementing its abstract methods
     *
     * @param mListener Your listener instance with implemented methods
     */
    void setmListener(collapsedWindowListener mListener) {
        this.mListener = mListener;
    }
}
