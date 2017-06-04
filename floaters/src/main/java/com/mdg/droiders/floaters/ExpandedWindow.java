package com.mdg.droiders.floaters;


import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

class ExpandedWindow {

    private RelativeLayout window;
    private WindowContainer container;
    //private View closeButtonLayout, closeWindowButton;
    private boolean isSheetVisible, isLayoutSet,
            isWindowVisible, isLayoutAddedToWindow;
    private Integer expandedChoice;
    private expandedWindowListener mListener;
    //private int statusBarHeight;
    //private int previousParamsX, previousParamsY;
    //private CollapsedWindow collapsedWindow;
    //private View.OnTouchListener listener;

    interface expandedWindowListener {
        void clickHappened();

        void overlapped();
    }

    ExpandedWindow(Context context, WindowContainer container) {
        this.container = container;
        window = new RelativeLayout(context);
        //isSheetVisible = false;
        isLayoutSet = false;
        //isLayoutAddedToWindow = false;
        //isWindowVisible = false;
        //FloatingViewContainer floatingViewContainer = new FloatingViewContainer(context, false);
        //collapsedWindow = new CollapsedWindow(floatingViewContainer);
        //statusBarHeight = (int) Math.ceil(25 * context.getResources().getDisplayMetrics().density);
    }

    /*void setDummyOnClickListener() {
        collapsedWindow.collapsedWindowFloatingView.
                setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        swapWindow(false, null);
                        event.setAction(MotionEvent.ACTION_DOWN);
                        listener.onTouch(container.getFloatingContainer().getmFloatingView(), event);
                        return true;
                    }
                });
    }*/

    //moves floating view inside the relative layout
    void setTouchListenerOnWindow(final WindowManager windowManager, final View closeButtonLayout) {
        final FloatingViewContainer containerFloat = container.getFloatingContainer();
        final View mfloatingView = containerFloat.getmFloatingView();
        container.getFloatingContainer().getmFloatingView()
                .setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN: {
                                RelativeLayout.LayoutParams lp = containerFloat.getRelativeParams();
                                //remember the initial position
                                container.setInitialPos(lp.leftMargin, lp.topMargin);
                                //get the touch location
                                container.setInitialTouchPos(event.getRawX(), event.getRawY());
                                //closeWindowButton.setVisibility(View.VISIBLE);
                                return true;
                            }
                            case MotionEvent.ACTION_MOVE: {
                                RelativeLayout.LayoutParams lp = containerFloat.getRelativeParams();
                                int x, y;
                                x = (int) (container.getInitialX() +
                                        event.getRawX() - container.getInitialTouchX());
                                y = (int) (container.getInitialY() +
                                        event.getRawY() - container.getInitialTouchY());
                                if (x <= window.getWidth() - mfloatingView.getWidth())
                                    lp.leftMargin = x;
                                else
                                    lp.leftMargin = window.getWidth() - mfloatingView.getWidth();

                                if (y <= window.getHeight() - mfloatingView.getHeight())
                                    lp.topMargin = y;
                                else lp.topMargin = window.getHeight() - mfloatingView.getHeight();
                                window.updateViewLayout(mfloatingView, lp);
                                return true;
                            }
                            case MotionEvent.ACTION_UP: {
                                // Since we have implemented the onTouchListener therefore we cannot implement onClickListener
                                // Therefor we make changes to the onTouchListener to handle touch events
                                container.setFloatingViewPos(window.getWidth());
                                int diffX = (int) (event.getRawX() - container.getInitialTouchX());
                                int diffY = (int) (event.getRawY() - container.getInitialTouchY());
                                if (diffX < 10 && diffY < 10) {
                                    if (expandedChoice == 1) { // If the view expands in a sheet Layout like Messenger
                                        toggleVisibiltyStatus(windowManager);
                                    } else if (containerFloat.isViewCollapsed()) {
                                        //When user clicks on the image view of the collapsed layout,
                                        //visibility of the collapsed layout will be changed to "View.GONE"
                                        //and expanded view will become visible.
                                        containerFloat.getCollapsedView().setVisibility(View.GONE);
                                        containerFloat.getExpandedView().setVisibility(View.VISIBLE);
                                    }
                                    mListener.clickHappened();
                                }
                                //closeWindowButton.setVisibility(View.GONE);
                                if (isOverlapping(closeButtonLayout, containerFloat.getmFloatingView()))
                                    mListener.overlapped();
                                //releaseService(windowManager);
                                //else swapWindow(true, windowManager);
                                return true;
                            }
                        }
                        return false;
                    }
                });
    }

    void addChildViews() {
        window.addView(container.getSheetLayoutContainer().getmSheetLayout()
                , container.getSheetLayoutContainer().getDefaultSheetContainerLayoutParams());
        window.addView(container.getFloatingContainer().getmFloatingView(),
                container.getFloatingContainer().getDefaultRelativeParams());
    }

    /*void addCloseButton(View view, RelativeLayout.LayoutParams layoutParams) {
        if (closeButtonLayout == null) {
            closeButtonLayout = view;
            window.addView(view, layoutParams);
            closeWindowButton = closeButtonLayout.findViewById(R.id.close_window_button);
        }
    }*/

    void removeChildViews() {
        window.removeAllViews();
    }

    void addToWindow(WindowManager wm) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                PixelFormat.TRANSLUCENT);
        layoutParams.dimAmount = 0;
        wm.addView(window, layoutParams);
        //addCollapsedViewToWindow(wm);
        window.setVisibility(View.GONE);
        //isLayoutAddedToWindow = true;
    }

    /*private void addCollapsedViewToWindow(WindowManager wm) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        wm.addView(collapsedWindow.collapsedWindowFloatingView, layoutParams);
    }*/

    void toggleVisibiltyStatus(WindowManager wm) {
        //setFloatingViewPosAfterTouch();
        WindowManager.LayoutParams lp = getWindowLayoutParams();
        /*if (isSheetVisible) {
            container.getSheetLayoutContainer().
                    getmSheetLayout().setVisibility(View.GONE);
            lp.dimAmount = 0;
        } else {*/
        if (!isLayoutSet) {
            setSheetHeight();
            setArrowPos();
            isLayoutSet = true;
        }
        lp.dimAmount = 0.4f;
        container.getSheetLayoutContainer().
                getmSheetLayout().setVisibility(View.VISIBLE);
        //}
        // isSheetVisible = !isSheetVisible;
        wm.updateViewLayout(window, lp);
    }

    private void setArrowPos() {
        RelativeLayout.LayoutParams lp =
                (RelativeLayout.LayoutParams) container.getSheetLayoutContainer()
                        .getArrow().getLayoutParams();
        int arrowWidth = lp.width;
        int widthOfFLoatingView = container.getFloatingContainer().getmFloatingView().getWidth();
        lp.setMarginStart(window.getWidth() - widthOfFLoatingView / 2 - arrowWidth / 2);
        container.getSheetLayoutContainer().getArrow().setLayoutParams(lp);
    }

    private void setSheetHeight() {
        int heightOfFloatingView = container.getFloatingContainer().
                getmFloatingView().getHeight();
        FrameLayout.LayoutParams layoutParams =
                (FrameLayout.LayoutParams) container.getSheetLayoutContainer()
                        .getmSheetContainer().getLayoutParams();
        layoutParams.height = window.getHeight() - heightOfFloatingView - 30;
        // 30 is to give extra space between floatingView and sheetLayout
        container.getSheetLayoutContainer()
                .getmSheetLayout().getLayoutParams().height
                = layoutParams.height;
        container.getSheetLayoutContainer()
                .getmSheetContainer().requestLayout();
        container.getSheetLayoutContainer()
                .getmSheetLayout().requestLayout();
    }

    private WindowManager.LayoutParams getWindowLayoutParams() {
        return (WindowManager.LayoutParams) window.getLayoutParams();
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

    /*void releaseService(WindowManager mWindowManager) {
        if (isLayoutAddedToWindow) {
            mWindowManager.removeView(getWindow());
            //mWindowManager.removeView(collapsedWindow.
            //        collapsedWindowFloatingView);
            isLayoutAddedToWindow = false;
        }
    }*/

    /*private void toggleWindowVisibility() {
        if (isWindowVisible) window.setVisibility(View.GONE);
        else window.setVisibility(View.VISIBLE);
    }*/

    /*private void swapWindow(boolean setCollapsedViewPos, WindowManager windowManager) {
        if (!isSheetVisible) {
            if (setCollapsedViewPos) {
                RelativeLayout.LayoutParams layoutParams
                        = container.getFloatingContainer().getRelativeParams();
                int x = layoutParams.leftMargin;
                int y = layoutParams.topMargin + statusBarHeight;
                WindowManager.LayoutParams params = collapsedWindow.getLayoutParams();
                params.x = x;
                params.y = y;
                windowManager.updateViewLayout(collapsedWindow.
                        collapsedWindowFloatingView, params);
            }
            collapsedWindow.toggleVisibilty();
            toggleWindowVisibility();
            isWindowVisible = !isWindowVisible;
        }
    }*/

    void setFloatingView() {
        RelativeLayout.LayoutParams params = container.getFloatingContainer().getRelativeParams();
            params.leftMargin = window.getWidth() -
                    container.getFloatingContainer().getmFloatingView().getWidth();
            params.topMargin = 0;
            window.updateViewLayout(container.getFloatingContainer().
                    getmFloatingView(), params);
    }
    /*private void setFloatingViewPosAfterTouch() {
        RelativeLayout.LayoutParams params = container.getFloatingContainer().getRelativeParams();
        if (isSheetVisible) {
            params.leftMargin = previousParamsX;
            params.topMargin = previousParamsY;
            window.updateViewLayout(container.getFloatingContainer().
                    getmFloatingView(), params);
        } else {
            previousParamsX = params.leftMargin;
            previousParamsY = params.topMargin;
            params.leftMargin = window.getWidth() -
                    container.getFloatingContainer().getmFloatingView().getWidth();
            params.topMargin = 0;
            window.updateViewLayout(container.getFloatingContainer().
                    getmFloatingView(), params);
        }
    }*/

    RelativeLayout getWindow() {
        return window;
    }

    void changeDimVal(int dim, WindowManager windowManager) {
        WindowManager.LayoutParams lp = getWindowLayoutParams();
        lp.dimAmount = 0.4f;
        windowManager.updateViewLayout(window, lp);
    }

    public void setExpandedChoice(Integer expandedChoice) {
        this.expandedChoice = expandedChoice;
    }

    public void setmListener(expandedWindowListener mListener) {
        this.mListener = mListener;
    }

    /*private class CollapsedWindow {
        private View collapsedWindowFloatingView;

        private CollapsedWindow(FloatingViewContainer floatingViewContainer) {
            collapsedWindowFloatingView = floatingViewContainer.getmFloatingView();
        }

        private void toggleVisibilty() {
            boolean isCollapsedWindowVisible = !isWindowVisible;
            if (isCollapsedWindowVisible)
                collapsedWindowFloatingView.setVisibility(View.GONE);
            else
                collapsedWindowFloatingView.setVisibility(View.VISIBLE);
        }

        private WindowManager.LayoutParams getLayoutParams() {
            return (WindowManager.LayoutParams) collapsedWindowFloatingView.getLayoutParams();
        }
    }*/
}