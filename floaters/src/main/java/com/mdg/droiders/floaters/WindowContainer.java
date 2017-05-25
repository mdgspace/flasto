package com.mdg.droiders.floaters;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import static android.content.Context.WINDOW_SERVICE;

public class WindowContainer {

    private Context ctx;
    private SheetLayoutContainer sheetLayoutContainer;
    private FloatingViewContainer floatingView;
    private int initialX, initialY;
    private float initialTouchX, initialTouchY;
    private boolean isVisible;
    private boolean isLayoutSet;
    private int statusBarHeight;
    private WindowManager mWindowManager;

    public WindowContainer(Context ctx,
                           SheetLayoutContainer sheetLayoutContainer,
                           FloatingViewContainer floatingView) {
        this.ctx = ctx;
        this.sheetLayoutContainer = sheetLayoutContainer;
        this.floatingView = floatingView;
        isVisible = false;
        statusBarHeight = (int) Math.ceil(25 * ctx.getResources().getDisplayMetrics().density);
        mWindowManager = (WindowManager) ctx.getSystemService(WINDOW_SERVICE);
        isLayoutSet = false;
    }

    public boolean isOverlapping(View v1, View v2) {
        // Location holder
        Rect rc1, rc2;
        int[] loc = new int[2];

        v1.getLocationOnScreen(loc);
        rc1 = new Rect(loc[0], loc[1], loc[0] + v1.getWidth(), loc[1] + v1.getHeight());

        v2.getLocationOnScreen(loc);
        rc2 = new Rect(loc[0], loc[1], loc[0] + v2.getWidth(), loc[1] + v2.getHeight());
        return Rect.intersects(rc1, rc2);
    }

    private WindowManager.LayoutParams initLayoutParams() {
        return new WindowManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
    }

    public void setInitialPos(int initialX, int initialY) {
        this.initialX = initialX;
        this.initialY = initialY;
    }

    public void setInitialTouchPos(int initialTouchX, int initialTouchY) {
        this.initialTouchX = initialTouchX;
        this.initialTouchY = initialTouchY;
    }

    public int getInitialX() {
        return initialX;
    }

    public int getInitialY() {
        return initialY;
    }

    public float getInitialTouchX() {
        return initialTouchX;
    }

    public float getInitialTouchY() {
        return initialTouchY;
    }

    public void toggleSheetStatus(Point size) {
        WindowManager.LayoutParams mSheetLayoutLayoutParams = sheetLayoutContainer.getSheetLayoutParams();
        if (isVisible) {
            sheetLayoutContainer.getmSheetLayout().setVisibility(View.GONE);
            mSheetLayoutLayoutParams.dimAmount = 0;
            mWindowManager.updateViewLayout(sheetLayoutContainer.getmSheetLayout(), mSheetLayoutLayoutParams);
        } else {
            if(!isLayoutSet){
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
}
