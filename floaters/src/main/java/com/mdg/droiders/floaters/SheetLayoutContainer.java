package com.mdg.droiders.floaters;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import static android.widget.RelativeLayout.ALIGN_PARENT_BOTTOM;

class SheetLayoutContainer {

    private View mSheetLayout;
    private View mSheetContainer;
    private View arrow;
    private Context ctx;
    private RelativeLayout.LayoutParams cachedSheetLayoutParams;

    SheetLayoutContainer(Context ctx) {
        this.ctx = ctx;
        mSheetLayout = LayoutInflater.from(ctx).inflate(R.layout.layout_floating_sheet, null);
        mSheetContainer = mSheetLayout.findViewById(R.id.sheet_container);
        arrow = mSheetContainer.findViewById(R.id.arrow);
    }

    /*WindowManager.LayoutParams getDefaultSheetContainerLayoutParams() {
        if (sheetLayoutParams == null) {
            sheetLayoutParams = new WindowManager.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                            WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                    PixelFormat.TRANSLUCENT);
            sheetLayoutParams.gravity = Gravity.BOTTOM;
            sheetLayoutParams.dimAmount = 0.4f;
        }
        return sheetLayoutParams;
    }*/

    RelativeLayout.LayoutParams getDefaultSheetContainerLayoutParams() {
        if(cachedSheetLayoutParams == null){
            cachedSheetLayoutParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            cachedSheetLayoutParams.addRule(ALIGN_PARENT_BOTTOM);
        }
        return cachedSheetLayoutParams;
    }

    WindowManager.LayoutParams getSheetLayoutParams() {
        return (WindowManager.LayoutParams) mSheetLayout.getLayoutParams();
    }

    View getmSheetLayout() {
        return mSheetLayout;
    }

    View getmSheetContainer() {
        return mSheetContainer;
    }

    View getArrow() {
        return arrow;
    }

    void setContent(int layoutId) {
        //Parent Layout's layout is taken from user
        View v = mSheetLayout.findViewById(R.id.parent_layout);
        LayoutInflater.from(ctx).inflate(layoutId, (ViewGroup) v);
    }
}
