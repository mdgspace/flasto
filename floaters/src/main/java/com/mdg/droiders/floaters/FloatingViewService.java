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
import android.widget.ImageView;
import android.widget.Toast;

public class FloatingViewService extends Service {

    private WindowManager mWindowManager;
    private FloatingViewContainer mFloatingContainer;
    private SheetLayoutContainer mSheetLayoutContainer;
    private WindowContainer mWindowContainer;
    private View mClosingButtonView;
    private FloatingViewBinder mBinder;
    private int expandedChoice = 0; //Default choice is of PLAYER
    Rect rc1, rc2;

    public enum FloatingViewExpanded {
        PLAYER,
        SHEET
    }

    // Contains public methhods that the Client app can call
    public class FloatingViewBinder extends Binder {
        public void setParentLayout(int layoutId) {
            mSheetLayoutContainer.setContent(layoutId);
        }

        public void specifyExpandedView(FloatingViewExpanded type) {
            if (type == FloatingViewExpanded.PLAYER) expandedChoice = 0;
            else if (type == FloatingViewExpanded.SHEET) expandedChoice = 1;
        }
        public void releaseService() {
            if (mFloatingContainer.getmFloatingView() != null) {
                mWindowManager.removeView(mClosingButtonView);
                mWindowManager.removeView(mFloatingContainer.getmFloatingView());
                mWindowManager.removeView(mSheetLayoutContainer.getmSheetLayout());
            }
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
        mFloatingContainer = new FloatingViewContainer(this);
        mSheetLayoutContainer = new SheetLayoutContainer(this);
        mWindowContainer = new WindowContainer(this, mSheetLayoutContainer, mFloatingContainer);
        mClosingButtonView = LayoutInflater.from(this).inflate(R.layout.close_button, null);
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingContainer.getmFloatingView() != null) {
            mWindowManager.removeView(mClosingButtonView);
            mWindowManager.removeView(mFloatingContainer.getmFloatingView());
            mWindowManager.removeView(mSheetLayoutContainer.getmSheetLayout());
        }
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

    private void initOnClickListeners(View mFloatingView) {
        //Set the close button
        /*ImageView closeServiceButton = (ImageView) mFloatingView.findViewById(R.id.close_btn);
        closeServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSelf();
            }
        });*/

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

        ImageView closeButton = (ImageView) mFloatingView.findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFloatingContainer.getCollapsedView().setVisibility(View.VISIBLE);
                mFloatingContainer.getExpandedView().setVisibility(View.GONE);
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
        params = mFloatingContainer.getDefaultFloatingViewParams();
        closeButtonParams = initLayoutParams();
        sheetLayoutParams = mSheetLayoutContainer.getDefaultSheetContainerLayoutParams();

        closeButtonParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
        /*closeButtonParams.x = (int) (size.x)/2;
        closeButtonParams.y = (int) size.y;*/

        //Add the view to the window
        mWindowManager.addView(mFloatingContainer.getmFloatingView(), params);
        mWindowManager.addView(mClosingButtonView, closeButtonParams);
        mWindowManager.addView(mSheetLayoutContainer.getmSheetLayout(), sheetLayoutParams);

        /*final View collapsedView = mFloatingView.findViewById(R.id.collapse_view);
        final View expandedView = mFloatingView.findViewById(R.id.expanded_container);*/
        //the root element of the closing button
        final View closeWindowButton = mClosingButtonView.findViewById(R.id.close_window_button);

        initOnClickListeners(mFloatingContainer.getmFloatingView());

        // Making the floating widget responsible to the touch events by setting an onTouchListener
        mFloatingContainer.getmFloatingView().findViewById(R.id.root_container).
                setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN: {
                                //remember the initial position
                                mWindowContainer.setInitialPos(params.x, params.y);
                                closeWindowButton.setVisibility(View.VISIBLE);

                                //get the touch location
                                mWindowContainer.setInitialTouchPos(event.getRawX(), event.getRawY());

                                return true;
                            }
                            case MotionEvent.ACTION_MOVE: {
                                //update the new parameters of the position
                                params.x = mWindowContainer.getInitialX() +
                                        (int) (event.getRawX() - mWindowContainer.getInitialTouchX());
                                params.y = mWindowContainer.getInitialY() +
                                        (int) (event.getRawY() - mWindowContainer.getInitialTouchY());

                                //update the layout with new X and Y coordinates
                                mWindowManager.updateViewLayout(mFloatingContainer.getmFloatingView(), params);
                                /*if(isOverlapping(mClosingButtonView,mFloatingView))
                                     Toast.makeText(FloatingViewService.this, "Test successful", Toast.LENGTH_SHORT).show();*/
                                return true;
                            }

                            case MotionEvent.ACTION_UP: {
                                // Since we have implemented the onTouchListener therefore we cannot implement onClickListener
                                // Therefor we make changes to the onTouchListener to handle touch events
                                mWindowContainer.setFloatingViewPos(size);

                                int diffX = (int) (event.getRawX() - mWindowContainer.getInitialTouchX());
                                int diffY = (int) (event.getRawY() - mWindowContainer.getInitialTouchY());
                                if (diffX < 10 && diffY < 10) {
                                    if (expandedChoice == 1) { // If the view expands in a sheet Layout like Messenger
                                        mWindowContainer.toggleSheetStatus(size);
                                    } else if (mFloatingContainer.isViewCollapsed()) {
                                        //When user clicks on the image view of the collapsed layout,
                                        //visibility of the collapsed layout will be changed to "View.GONE"
                                        //and expanded view will become visible.
                                        mFloatingContainer.getCollapsedView().setVisibility(View.GONE);
                                        mFloatingContainer.getExpandedView().setVisibility(View.VISIBLE);
                                    }
                                }

                                if (isOverlapping(mClosingButtonView, mFloatingContainer.getmFloatingView()))
                                    mBinder.releaseService();
                                return true;
                            }

                        }
                        return false;
                    }
                });
    }

}
