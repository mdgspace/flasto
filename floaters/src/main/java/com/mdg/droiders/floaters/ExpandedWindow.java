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

/**
 * Contains a Relative Layout that has floating view and the sheet layout as its child views
 * and various methods to implement "floatation" of the floating view and set the position of sheet layout
 */
class ExpandedWindow {

    /**
     * It is the Relative Layout that is used as a container
     * for all elements in the {@link ExpandedWindow}
     */
    private RelativeLayout window;
    /**
     * {@link WindowContainer} instance
     */
    private WindowContainer container;
    /**
     * {@link Boolean} value to indicate whether the expanded layout
     * has been set or laid out once so that the floating view is above
     * sheet layout.
     */
    private boolean isLayoutSet;
    private Integer expandedChoice;
    /**
     * {@link expandedWindowListener} instance
     */
    private expandedWindowListener mListener;

    /**
     * an interface to notify service about click event or overlapping with the
     * {@link FloatingViewService#mClosingButtonView} and the {@link #window)}
     * <p>Similar to {@link com.mdg.droiders.floaters.CollapsedWindow.collapsedWindowListener}</p>
     */
    interface expandedWindowListener {
        void clickHappened();

        void overlapped();
    }

    /**
     * Constructor for {@link ExpandedWindow} class.
     *
     * @param context   The {@link Context} to use
     * @param container The {@link WindowContainer} instance to use
     */
    ExpandedWindow(Context context, WindowContainer container) {
        this.container = container;
        window = new RelativeLayout(context);
        isLayoutSet = false;
    }

    //moves floating view inside the relative layout

    /**
     * Sets Touch listener on the floating view in the expanded {@link #window}
     *
     * @param windowManager     The {@link WindowManager} instance to use.
     * @param closeButtonLayout The closeButtonView to see if the floating view overlaps with it.
     */
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
                                    // TODO: Set MUSIC PLAYER functionality
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
                                if (isOverlapping(closeButtonLayout, containerFloat.getmFloatingView()))
                                    mListener.overlapped();
                                return true;
                            }
                        }
                        return false;
                    }
                });
    }

    /**
     * Adds the sheetLayout and floating view to the {@link #window}
     */
    void addChildViews() {
        window.addView(container.getSheetLayoutContainer().getmSheetLayout()
                , container.getSheetLayoutContainer().getDefaultSheetContainerLayoutParams());
        window.addView(container.getFloatingContainer().getmFloatingView(),
                container.getFloatingContainer().getDefaultRelativeParams());
    }

    void removeChildViews() {
        window.removeAllViews();
    }

    /**
     * {@link #window} will be added to the screen window covering the whole screen.
     *
     * @param wm {@link WindowManager} instance
     */
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
        window.setVisibility(View.GONE);
    }

    /**
     * Dims the background and sets the layout depending on the value of  {@link #isLayoutSet}
     *
     * @param wm {@link WindowManager} instance to use
     */
    void toggleVisibiltyStatus(WindowManager wm) {
        if (!isLayoutSet) {
            WindowManager.LayoutParams lp = getWindowLayoutParams();
            setSheetHeight();
            setArrowPos();
            lp.dimAmount = 0.4f;
            wm.updateViewLayout(window, lp);
            isLayoutSet = true;
        }
    }

    /**
     * Arrow is a drawable that appears just above the parent_layout in the sheet layout.
     * This method sets the arrow such that it always points at the middle of floating view above it.
     */
    private void setArrowPos() {
        RelativeLayout.LayoutParams lp =
                (RelativeLayout.LayoutParams) container.getSheetLayoutContainer()
                        .getArrow().getLayoutParams();
        int arrowWidth = lp.width;
        int widthOfFLoatingView = container.getFloatingContainer().getmFloatingView().getWidth();
        lp.setMarginStart(window.getWidth() - widthOfFLoatingView / 2 - arrowWidth / 2);
        container.getSheetLayoutContainer().getArrow().setLayoutParams(lp);
    }

    /**
     * This method adjusts the height of sheet layout such that it starts from the bottom
     * of the window and extends up to where the floating view is.
     */
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

    /**
     * positions the floating view in expanded layout at the top right corner.
     */
    void setFloatingView() {
        RelativeLayout.LayoutParams params = container.getFloatingContainer().getRelativeParams();
        params.leftMargin = window.getWidth() -
                container.getFloatingContainer().getmFloatingView().getWidth();
        params.topMargin = 0;
        window.updateViewLayout(container.getFloatingContainer().
                getmFloatingView(), params);
    }

    /**
     * @return {@link #window}
     */
    RelativeLayout getWindow() {
        return window;
    }

    void changeDimVal(int dim, WindowManager windowManager) {
        WindowManager.LayoutParams lp = getWindowLayoutParams();
        lp.dimAmount = 0.4f;
        windowManager.updateViewLayout(window, lp);
    }

    /**
     * Sets whether the floating view should expand into a sheet layout or a music player layout
     * <p><strong>Note:</strong>Don't set the expandedChoice value yourself. It is to be set by
     * the user who integrates this library into his project</p>
     *
     * @param expandedChoice is 0 for music player layout and 1 for sheetLayout
     */
    void setExpandedChoice(Integer expandedChoice) {
        this.expandedChoice = expandedChoice;
    }

    /**
     * Set a {@link expandedWindowListener} after implementing its abstract methods
     *
     * @param mListener Your listener instance with implemented methods
     */
    void setmListener(expandedWindowListener mListener) {
        this.mListener = mListener;
    }

}
