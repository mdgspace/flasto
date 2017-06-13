package com.mdg.droiders.floaters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * Contains a floating view instance and relevant methods related to it.
 */
class FloatingViewContainer {

    private View mFloatingView;
    private View collapsedView;
    private View expandedView;
    private RelativeLayout.LayoutParams cachedRelativeParams;

    /**
     * Constructor for {@link FloatingViewContainer} class
     *
     * @param ctx                   The {@link Context} is needed to inflate floating view layout
     *                              from xml
     * @param isHardwareAccelerated Set false to set {@link View#LAYER_TYPE_SOFTWARE} on the
     *                              floating view
     * @param isExpandable          Set true to imply that the floating view may convert into music
     *                              window on click
     * @see View#getLayerType()
     * @see View#setLayerType(int, android.graphics.Paint)
     * @see View#LAYER_TYPE_SOFTWARE
     * @see View#LAYER_TYPE_HARDWARE
     */
    FloatingViewContainer(Context ctx, boolean isHardwareAccelerated, boolean isExpandable) {
        this.mFloatingView = LayoutInflater.from(ctx).inflate(R.layout.layout_floating, null);
        collapsedView = mFloatingView.findViewById(R.id.collapse_view);
        expandedView = mFloatingView.findViewById(R.id.expanded_container);
        if (isHardwareAccelerated) {
            mFloatingView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        if (isExpandable) {
            initOnClickListeners(ctx);
        }
    }

    View getmFloatingView() {
        return mFloatingView;
    }

    /**
     * Generates default layout params for the floating view if not previously generated
     *
     * @return The default layout params for the floating view to use
     */
    RelativeLayout.LayoutParams getDefaultRelativeParams() {
        if (cachedRelativeParams == null) {
            cachedRelativeParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        return cachedRelativeParams;
    }

    /**
     * @return Gets layout params from floating view and returns the same.
     */
    RelativeLayout.LayoutParams getRelativeParams() {
        return (RelativeLayout.LayoutParams) mFloatingView.getLayoutParams();
    }

    /**
     * Detect if the floating view is collapsed or expanded.
     *
     * @return true if the floating view is collapsed.
     */
    boolean isViewCollapsed() {
        return mFloatingView == null || mFloatingView.findViewById(
                R.id.collapse_view).getVisibility() == View.VISIBLE;
    }

    View getCollapsedView() {
        return collapsedView;
    }

    View getExpandedView() {
        return expandedView;
    }

    /**
     * Init on click listener in the MUSIC PLAYER expanded version of the floating view.
     *
     * @param context The @{@link Context} to use for displaying toast on view click
     */
    private void initOnClickListeners(final Context context) {

        ImageView playButton;
        ImageView nextButton;
        ImageView prevButton;
        ImageView openButton;
        ImageView closeButton;

        playButton = (ImageView) mFloatingView.findViewById(R.id.play_btn);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Playing the song.", Toast.LENGTH_LONG).show();
            }
        });

        nextButton = (ImageView) mFloatingView.findViewById(R.id.next_btn);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Playing next song.", Toast.LENGTH_LONG).show();
            }
        });

        prevButton = (ImageView) mFloatingView.findViewById(R.id.prev_btn);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Playing previous song.", Toast.LENGTH_LONG).show();
            }
        });

        closeButton = (ImageView) mFloatingView.findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCollapsedView().setVisibility(View.VISIBLE);
                getExpandedView().setVisibility(View.GONE);
            }
        });

        //Open the application on thi button click
        openButton = (ImageView) mFloatingView.findViewById(R.id.open_button);
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open the application  click.
                /*Intent intent = new Intent(context, mContext);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*/
            }
        });
    }
}
