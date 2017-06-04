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

class CollapsedWindow {
    private RelativeLayout window;
    private FloatingViewContainer[] containerFloat;
    private int initialX, initialY;
    private float initialTouchX, initialTouchY;
    private boolean isVisible;
    private int previousParamsX, previousParamsY;
    private collapsedWindowListener mListener;

    interface collapsedWindowListener {
        void clickHappened();

        void overlapped();
    }

    CollapsedWindow(Context context, FloatingViewContainer... containerFloat) {
        this.containerFloat = containerFloat;
        window = new RelativeLayout(context);
    }

    void addChildViews() {
        for (FloatingViewContainer viewContainer : containerFloat)
            window.addView(viewContainer.getmFloatingView(),
                    viewContainer.getDefaultRelativeParams());
    }

    View getWindow() {
        return window;
    }

    /*WindowManager.LayoutParams getDefaultLayoutParams() {
        if (params == null) {
            params = new WindowManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
            params.gravity = Gravity.TOP | Gravity.START;        //Initially view will be added to top-left corner
            params.x = 0;
            params.y = 100;
        }
        return params;
    }*/

    private WindowManager.LayoutParams getParams() {
        return (WindowManager.LayoutParams) window.getLayoutParams();
    }

    private void setInitialPos(int initialX, int initialY) {
        this.initialX = initialX;
        this.initialY = initialY;
    }

    private void setInitialTouchPos(float initialTouchX, float initialTouchY) {
        this.initialTouchX = initialTouchX;
        this.initialTouchY = initialTouchY;
    }

    private int getInitialX() {
        return initialX;
    }

    private int getInitialY() {
        return initialY;
    }

    private float getInitialTouchX() {
        return initialTouchX;
    }

    private float getInitialTouchY() {
        return initialTouchY;
    }

    private void setFloatingViewPosAfterTouch(WindowManager mWindowManager, Point size) {
        for (FloatingViewContainer viewContainer : containerFloat) {
            WindowManager.LayoutParams params = getParams();
            if (isVisible) {
                params.x = previousParamsX;
                params.y = previousParamsY;
                mWindowManager.updateViewLayout(viewContainer.getmFloatingView(), params);
            } else {
                previousParamsX = params.x;
                previousParamsY = params.y;
                params.x = size.x;
                params.y = 0;
                mWindowManager.updateViewLayout(viewContainer.getmFloatingView(), params);
            }
        }
    }

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
                        //closeWindowButton.setVisibility(View.VISIBLE);

                        //get the touch location
                        setInitialTouchPos(event.getRawX(), event.getRawY());

                        return true;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        //update the new parameters of the position
                        WindowManager.LayoutParams params = getParams();
                        params.x = getInitialX() + (int) (event.getRawX() - getInitialTouchX());
                        params.y = getInitialY() + (int) (event.getRawY() - getInitialTouchY());

                        //update the layout with new X and Y coordinates
                        mWindowManager.updateViewLayout(window, params);
                                /*if(isOverlapping(mClosingButtonView,mFloatingView))
                                     Toast.makeText(FloatingViewService.this, "Test successful", Toast.LENGTH_SHORT).show();*/
                        return true;
                    }

                    case MotionEvent.ACTION_UP: {
                        // Since we have implemented the onTouchListener therefore we cannot implement onClickListener
                        // Therefor we make changes to the onTouchListener to handle touch events
                        setFloatingViewPos(mWindowManager, size);

                        int diffX = (int) (event.getRawX() - getInitialTouchX());
                        int diffY = (int) (event.getRawY() - getInitialTouchY());
                        if (diffX < 10 && diffY < 10) {
                            //CLICK HAPPENED
                                        /*if (expandedChoice == 1) { // If the view expands in a sheet Layout like Messenger
                                            mWindowContainer.toggleSheetStatus(size);
                                        } else if (mFloatingContainer.isViewCollapsed()) {
                                            //When user clicks on the image view of the collapsed layout,
                                            //visibility of the collapsed layout will be changed to "View.GONE"
                                            //and expanded view will become visible.
                                            mFloatingContainer.getCollapsedView().setVisibility(View.GONE);
                                            mFloatingContainer.getExpandedView().setVisibility(View.VISIBLE);
                                        }*/
                            // TODO NOTIFY VIA INTERFACE 2
                            mListener.clickHappened();
                        }

                        if (isOverlapping(mClosingButtonView, window))
                            mListener.overlapped();
                        // TODO NOTIFY VIA INTERFACE 1
                        return true;
                    }

                }
                return false;
            }
        });

    }


    void setmListener(collapsedWindowListener mListener) {
        this.mListener = mListener;
    }
}
