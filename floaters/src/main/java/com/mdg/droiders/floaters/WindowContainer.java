package com.mdg.droiders.floaters;

import android.content.Context;
import android.graphics.Point;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

class WindowContainer {

    private SheetLayoutContainer sheetLayoutContainer;
    private FloatingViewContainer floatingView;
    private float initialX;
    private float initialY;
    private float initialTouchX, initialTouchY;
    private boolean isVisible;
    private boolean isLayoutSet;
    private int statusBarHeight;
    private int previousParamsX, previousParamsY;

    WindowContainer(Context ctx,
                    SheetLayoutContainer sheetLayoutContainer,
                    FloatingViewContainer floatingView) {
        this.sheetLayoutContainer = sheetLayoutContainer;
        this.floatingView = floatingView;
        isVisible = false;
        statusBarHeight = (int) Math.ceil(25 * ctx.getResources().getDisplayMetrics().density);
        isLayoutSet = false;
    }

    void setInitialPos(float initialX, float initialY) {
        this.initialX = initialX;
        this.initialY = initialY;
    }

    void setInitialTouchPos(float initialTouchX, float initialTouchY) {
        this.initialTouchX = initialTouchX;
        this.initialTouchY = initialTouchY;
    }

    float getInitialX() {
        return initialX;
    }

    float getInitialY() {
        return initialY;
    }

    float getInitialTouchX() {
        return initialTouchX;
    }

    float getInitialTouchY() {
        return initialTouchY;
    }

    void toggleSheetStatus(Point size, WindowManager mWindowManager) {
        WindowManager.LayoutParams mSheetLayoutLayoutParams = sheetLayoutContainer.getSheetLayoutParams();
        //setFloatingViewPosAfterTouch(size, mWindowManager);
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
        // Set X coordinates of sheet Layout (specifically arrow view)
        RelativeLayout.LayoutParams lp =
                (RelativeLayout.LayoutParams) sheetLayoutContainer.getArrow().getLayoutParams();
        int arrowWidth = lp.width;
        int widthOfFLoatingView = floatingView.getmFloatingView().getWidth();
        lp.setMarginStart(size.x - widthOfFLoatingView / 2 - arrowWidth / 2);
        sheetLayoutContainer.getArrow().setLayoutParams(lp);
        isLayoutSet = true;
    }

    /*private void setFloatingViewPosAfterTouch(Point size, WindowManager mWindowManager) {
        WindowManager.LayoutParams params = floatingView.getRelativeParams();
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
