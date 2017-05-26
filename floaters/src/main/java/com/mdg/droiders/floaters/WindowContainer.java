package com.mdg.droiders.floaters;

import android.content.Context;
import android.graphics.Point;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import static android.content.Context.WINDOW_SERVICE;

class WindowContainer {

    private SheetLayoutContainer sheetLayoutContainer;
    private FloatingViewContainer floatingView;
    private int initialX, initialY;
    private float initialTouchX, initialTouchY;
    private boolean isVisible;
    private boolean isLayoutSet;
    private int statusBarHeight;
    private WindowManager mWindowManager;
    private int previousParamsX, previousParamsY;

    WindowContainer(Context ctx,
                    SheetLayoutContainer sheetLayoutContainer,
                    FloatingViewContainer floatingView) {
        this.sheetLayoutContainer = sheetLayoutContainer;
        this.floatingView = floatingView;
        isVisible = false;
        statusBarHeight = (int) Math.ceil(25 * ctx.getResources().getDisplayMetrics().density);
        mWindowManager = (WindowManager) ctx.getSystemService(WINDOW_SERVICE);
        isLayoutSet = false;
    }

    void setInitialPos(int initialX, int initialY) {
        this.initialX = initialX;
        this.initialY = initialY;
    }

    void setInitialTouchPos(float initialTouchX, float initialTouchY) {
        this.initialTouchX = initialTouchX;
        this.initialTouchY = initialTouchY;
    }

    int getInitialX() {
        return initialX;
    }

    int getInitialY() {
        return initialY;
    }

    float getInitialTouchX() {
        return initialTouchX;
    }

    float getInitialTouchY() {
        return initialTouchY;
    }

    void toggleSheetStatus(Point size) {
        WindowManager.LayoutParams mSheetLayoutLayoutParams = sheetLayoutContainer.getSheetLayoutParams();
        setFloatingViewPosAfterTouch(size);
        if (isVisible) {
            sheetLayoutContainer.getmSheetLayout().setVisibility(View.GONE);
            mSheetLayoutLayoutParams.dimAmount = 0;
            mWindowManager.updateViewLayout(sheetLayoutContainer.getmSheetLayout(), mSheetLayoutLayoutParams);
        } else {
            if (!isLayoutSet) {
                setLayout(size);
            }
            mSheetLayoutLayoutParams.dimAmount = 0.4f;
            mWindowManager.updateViewLayout(sheetLayoutContainer.getmSheetLayout(),
                    mSheetLayoutLayoutParams);

            sheetLayoutContainer.getmSheetLayout().setVisibility(View.VISIBLE);
        }
        this.isVisible = !isVisible;
    }

    private void setLayout(Point size) {
        // Set Y coordinates of sheet Layout
        int heightOfFloatingView = floatingView.getmFloatingView().getHeight();
        FrameLayout.LayoutParams layoutParams =
                (FrameLayout.LayoutParams) sheetLayoutContainer.getmSheetContainer().getLayoutParams();
        layoutParams.height = size.y - heightOfFloatingView - statusBarHeight - 30;
        // 30 is to give extra space between floatingView and sheetLayout
        sheetLayoutContainer.getmSheetLayout().getLayoutParams().height
                = layoutParams.height;
        sheetLayoutContainer.getmSheetContainer().requestLayout();
        sheetLayoutContainer.getmSheetLayout().requestLayout();
        // Set X coordinates of sheet Layout (specially arrow view)
        RelativeLayout.LayoutParams lp =
                (RelativeLayout.LayoutParams) sheetLayoutContainer.getArrow().getLayoutParams();
        int arrowWidth = lp.width;
        int widthOfFLoatingView = floatingView.getmFloatingView().getWidth();
        lp.setMarginStart(size.x - widthOfFLoatingView / 2 - arrowWidth / 2);
        sheetLayoutContainer.getArrow().setLayoutParams(lp);
        isLayoutSet = true;
    }

    private void setFloatingViewPosAfterTouch(Point size) {
        WindowManager.LayoutParams params = floatingView.getFloatingViewParams();
        if (isVisible) {
            params.x = previousParamsX;
            params.y = previousParamsY;
            mWindowManager.updateViewLayout(floatingView.getmFloatingView(), params);
        } else {
            previousParamsX = params.x;
            previousParamsY = params.y;
            params.x = size.x;
            params.y = 0;
            mWindowManager.updateViewLayout(floatingView.getmFloatingView(), params);
        }
    }

    void setFloatingViewPos(Point size) {
        WindowManager.LayoutParams params = floatingView.getFloatingViewParams();
        int midX = size.x / 2;
        if (params.x >= midX)
            params.x = size.x;
        else if (params.x < midX)
            params.x = 0;
        //update the layout with new X and Y coordinates
        mWindowManager.updateViewLayout(floatingView.getmFloatingView(), params);

    }
}
