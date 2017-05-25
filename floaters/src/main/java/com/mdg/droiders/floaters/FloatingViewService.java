package com.mdg.droiders.floaters;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Binder;
import android.os.IBinder;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class FloatingViewService extends Service {

    private WindowManager mWindowManager;
    private View mFloatingView;
    private View mClosingButtonView;
    private View mSheetLayout;
    private View mSheetContainer;
    private View arrow;
    private FloatingViewBinder mBinder;
    private int previousParamsX, previousParamsY;
    private int expandedChoice = 0; //Default choice is of PLAYER
    private int statusBarHeight = 0, arrowWidth;
    Rect rc1, rc2;

    public enum FloatingViewExpanded {
        PLAYER(0),
        SHEET(1);

        private int view_id;

        FloatingViewExpanded(int view_id) {
            this.view_id = view_id;
        }
    }

    // Contains public methhods that the Client app can call
    public class FloatingViewBinder extends Binder {
        public void setParentLayout(int layoutId) {
            //Parent Layout's layout is taken from user
            View v = mSheetLayout.findViewById(R.id.parent_layout);
            LayoutInflater.from(FloatingViewService.this).inflate(layoutId, (ViewGroup) v);
        }

        public void specifyExpandedView(FloatingViewExpanded type) {
            if (type == FloatingViewExpanded.PLAYER) expandedChoice = 0;
            else if (type == FloatingViewExpanded.SHEET) expandedChoice = 1;
        }
    }

    public FloatingViewService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        mBinder = new FloatingViewBinder();
        createFloatingHead();
        return mBinder;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "On create called", Toast.LENGTH_SHORT).show();
        super.onCreate();
        //Inflate the floating view layout
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating, null);
        mClosingButtonView = LayoutInflater.from(this).inflate(R.layout.close_button, null);
        mSheetLayout = LayoutInflater.from(this).inflate(R.layout.layout_floating_sheet, null);
        mSheetContainer = mSheetLayout.findViewById(R.id.sheet_container);
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        arrow = mSheetContainer.findViewById(R.id.arrow);
        statusBarHeight = getStatusBarHeight();
    }

    /**
     * Detect if the floating view is collapsed or expanded.
     *
     * @return true if the floating view is collapsed.
     */
    private boolean isViewCollapsed() {
        return mFloatingView == null || mFloatingView.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) {
            mWindowManager.removeView(mClosingButtonView);
            mWindowManager.removeView(mFloatingView);
            mWindowManager.removeView(mSheetLayout);
        }
    }

    private int getStatusBarHeight() {
        return (int) Math.ceil(25 * this.getResources().getDisplayMetrics().density);
    }

    private boolean isOverlapping(View v1, View v2) {
        // Location holder
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

    private void initOnClickListeners() {
        ImageView closeServiceButton = (ImageView) mFloatingView.findViewById(R.id.close_btn);
        closeServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSelf();
            }
        });

        ImageView playButton = (ImageView) mFloatingView.findViewById(R.id.play_btn);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FloatingViewService.this, "Playing the song.", Toast.LENGTH_LONG).show();
            }
        });

        ImageView nextButton = (ImageView) mFloatingView.findViewById(R.id.next_btn);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FloatingViewService.this, "Playing next song.", Toast.LENGTH_LONG).show();
            }
        });

        ImageView prevButton = (ImageView) mFloatingView.findViewById(R.id.prev_btn);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FloatingViewService.this, "Playing previous song.", Toast.LENGTH_LONG).show();
            }
        });

        //Open the application on thi button click
        ImageView openButton = (ImageView) mFloatingView.findViewById(R.id.open_button);
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open the application  click.
                /*Intent intent = new Intent(FloatingViewService.this, mContext);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*/
                //close the service and remove view from the view hierarchy
                stopSelf();
            }
        });
    }

    private void createFloatingHead() {
        //Get Display Size
        Display display = mWindowManager.getDefaultDisplay();
        final Point size;
        size = new Point();
        display.getSize(size);

        final WindowManager.LayoutParams params, closeButtonParams, sheetLayoutParams;
        params = initLayoutParams();
        closeButtonParams = initLayoutParams();
        sheetLayoutParams = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                PixelFormat.TRANSLUCENT);

        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.START;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;

        closeButtonParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
        /*closeButtonParams.x = (int) (size.x)/2;
        closeButtonParams.y = (int) size.y;*/

        sheetLayoutParams.gravity = Gravity.BOTTOM;
        sheetLayoutParams.dimAmount = 0.4f;

        //Add the view to the window
        mWindowManager.addView(mFloatingView, params);
        mWindowManager.addView(mClosingButtonView, closeButtonParams);
        mWindowManager.addView(mSheetLayout, sheetLayoutParams);

        // Initialise root elements of layouts
        final View collapsedView = mFloatingView.findViewById(R.id.collapse_view);
        final View expandedView = mFloatingView.findViewById(R.id.expanded_container);
        final View closeWindowButton = mClosingButtonView.findViewById(R.id.close_window_button);

        initOnClickListeners();
        ImageView closeButton = (ImageView) mFloatingView.findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);
            }
        });

        // Making the floating widget responsible to the touch events by setting an onTouchListener
        mFloatingView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX, initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        //remember the initial position
                        initialX = params.x;
                        initialY = params.y;
                        closeWindowButton.setVisibility(View.VISIBLE);

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        //update the new parameters of the position
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        //update the layout with new X and Y coordinates
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        /*if(isOverlapping(mClosingButtonView,mFloatingView))
                            Toast.makeText(FloatingViewService.this, "Test successful", Toast.LENGTH_SHORT).show();*/
                        return true;
                    }

                    case MotionEvent.ACTION_UP: {
                        // Since we have implemented the onTouchListener therefore we cannot implement onClickListener
                        // Therefor we make changes to the onTouchListener to handle touch events

                        int midX = (int) (size.x / 2);
                        if (params.x >= midX)
                            params.x = size.x;
                        else if (params.x < midX)
                            params.x = 0;
                        //update the layout with new X and Y coordinates
                        mWindowManager.updateViewLayout(mFloatingView, params);

                        int diffX = (int) (event.getRawX() - initialTouchX);
                        int diffY = (int) (event.getRawY() - initialTouchY);
                        if (diffX < 10 && diffY < 10) {
                            if (expandedChoice == 1) { // If the view expands in a sheet Layout like Messenger
                                WindowManager.LayoutParams mSheetLayoutLayoutParams =
                                        (WindowManager.LayoutParams) mSheetLayout.getLayoutParams();
                                if (mSheetLayout.getVisibility() == View.VISIBLE) {
                                    mSheetLayout.setVisibility(View.GONE);
                                    params.x = previousParamsX;
                                    params.y = previousParamsY;
                                    mWindowManager.updateViewLayout(mFloatingView, params);
                                    mSheetLayoutLayoutParams.dimAmount = 0;
                                    mWindowManager.updateViewLayout(mSheetLayout, mSheetLayoutLayoutParams);
                                } else {
                                    // Set Y coordinates of sheet Layout
                                    int heightOfFloatingView = mFloatingView.getHeight();
                                    FrameLayout.LayoutParams layoutParams =
                                            (FrameLayout.LayoutParams) mSheetContainer.getLayoutParams();
                                    layoutParams.height = size.y - heightOfFloatingView - statusBarHeight - 30;
                                    // 30 is to give extra space between floatingView and sheetLayout
                                    mSheetLayout.getLayoutParams().height = layoutParams.height;
                                    mSheetContainer.requestLayout();
                                    mSheetLayout.requestLayout();
                                    mSheetLayoutLayoutParams.dimAmount = 0.4f;
                                    mWindowManager.updateViewLayout(mSheetLayout, mSheetLayoutLayoutParams);
                                    //----------
                                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) arrow.getLayoutParams();
                                    int arrowWidth = lp.width;
                                    int widthOfFLoatingView = mFloatingView.getWidth();
                                    lp.setMarginStart(size.x - widthOfFLoatingView / 2 - arrowWidth / 2);
                                    arrow.setLayoutParams(lp);
                                    mSheetLayout.setVisibility(View.VISIBLE);
                                    previousParamsX = params.x;
                                    previousParamsY = params.y;
                                    params.x = size.x;
                                    params.y = 0;
                                    mWindowManager.updateViewLayout(mFloatingView, params);
                                }
                            } else if (isViewCollapsed()) {
                                //When user clicks on the image view of the collapsed layout,
                                //visibility of the collapsed layout will be changed to "View.GONE"
                                //and expanded view will become visible.
                                collapsedView.setVisibility(View.GONE);
                                expandedView.setVisibility(View.VISIBLE);
                            }
                        }

                        if (isOverlapping(mClosingButtonView, mFloatingView))
                            stopSelf();
                        return true;
                    }

                }
                return false;
            }
        });
    }

}
