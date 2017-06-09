package com.mdg.droiders.floaters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import static android.widget.RelativeLayout.ALIGN_PARENT_BOTTOM;

/**
 * Contains a sheet layout and relevant methods related to it.
 */
class SheetLayoutContainer {

    private View mSheetLayout;
    private View mSheetContainer;
    private View arrow;
    private Context ctx;
    private RelativeLayout.LayoutParams cachedSheetLayoutParams;
    private boolean isContentSet;

    /**
     * Constructor for {@link SheetLayoutContainer}
     *
     * @param ctx The {@link Context} is needed to inflate sheet layout from xml
     */
    SheetLayoutContainer(Context ctx) {
        this.ctx = ctx;
        mSheetLayout = LayoutInflater.from(ctx).inflate(R.layout.layout_floating_sheet, null);
        mSheetContainer = mSheetLayout.findViewById(R.id.sheet_container);
        arrow = mSheetContainer.findViewById(R.id.arrow);
        isContentSet = false;
    }

    /**
     * Generates default layout params for the SheetLayout if not previously generated
     *
     * @return The default layout params for the SheetLayout to use
     */
    RelativeLayout.LayoutParams getDefaultSheetContainerLayoutParams() {
        if (cachedSheetLayoutParams == null) {
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

    /**
     * Adds the layout specified by layoutId as a child view to the sheet layout.
     * The specified layout is shown in the sheet layout when the floating view expands into sheet layout.
     * <p><strong>Note:</strong>It is mandatory that you put all your content in a single xml
     * and add that xml alone to the SheetLayout. Further calls to this function will not add
     * the xml as a child to the sheet layout</p>
     *
     * @param layoutId The layout Id of the xml which is to be shown in expanded mode
     * @see #setContent(ViewGroup)
     */
    void setContent(int layoutId) {
        if (!isContentSet) {
            //Parent Layout's layout is taken from user
            View v = mSheetLayout.findViewById(R.id.parent_layout);
            LayoutInflater.from(ctx).inflate(layoutId, (ViewGroup) v);
            isContentSet = true;
        }
    }

    /**
     * Adds the specified layout as child to the sheet layout with default layout params.
     * <p><strong>Note: </strong>It is mandatory that you put all your content in a single ViewGroup
     * and add that ViewGroup alone to the SheetLayout. Further calls to this function will not add
     * the view as a child to the sheet layout</p>
     *
     * @param layout The ViewGroup which is to be shown in expanded mode
     * @see #setContent(int)
     */
    void setContent(ViewGroup layout) {
        if (!isContentSet) {
            ViewGroup v = (ViewGroup) mSheetLayout.findViewById(R.id.parent_layout);
            v.addView(layout);
            isContentSet = true;
        }
    }
}
