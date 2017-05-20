package com.mdg.droiders.floaters;

import android.animation.ValueAnimator;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
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

import static android.view.View.GONE;

public class FloatingViewService extends Service {

    Rect rc1, rc2;
    private WindowManager mWindowManager;
    private View mFloatingView;
    private View mClosingButtonView;

    public FloatingViewService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "On create called", Toast.LENGTH_SHORT).show();
        super.onCreate();
        //Inflate the floating view layout
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating, null);
        mClosingButtonView = LayoutInflater.from(this).inflate(R.layout.close_button, null);
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = mWindowManager.getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
        mClosingButtonView.setVisibility(GONE);

        //Add view to the window
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

        final WindowManager.LayoutParams closeButtonParams = new WindowManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;

        closeButtonParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
        /*closeButtonParams.x = (int) (size.x)/2;
        closeButtonParams.y = (int) size.y;*/

        //Add the view to the window
        mWindowManager.addView(mFloatingView, params);
        mWindowManager.addView(mClosingButtonView, closeButtonParams);


        /**
         * Setting up the views for the floating buttons
         */

        //The root element of the collapsed view layout
        final View collapsedView = mFloatingView.findViewById(R.id.collapse_view);
        //The root element of the expanded view layout
        final View expandedView = mFloatingView.findViewById(R.id.expanded_container);
        //the root element of the closing button
        final View closeWindowButton = mClosingButtonView.findViewById(R.id.close_window_button);

        //Set the close button
        ImageView closeServiceButton = (ImageView) mFloatingView.findViewById(R.id.close_btn);
        closeServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSelf();
            }
        });

        //Set the view while floating view is expanded.
        //Set the play button.
        ImageView playButton = (ImageView) mFloatingView.findViewById(R.id.play_btn);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FloatingViewService.this, "Playing the song.", Toast.LENGTH_LONG).show();
            }
        });


        //Set the next button.
        ImageView nextButton = (ImageView) mFloatingView.findViewById(R.id.next_btn);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FloatingViewService.this, "Playing next song.", Toast.LENGTH_LONG).show();
            }
        });


        //Set the pause button.
        ImageView prevButton = (ImageView) mFloatingView.findViewById(R.id.prev_btn);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FloatingViewService.this, "Playing previous song.", Toast.LENGTH_LONG).show();
            }
        });


        //Set the close button
        ImageView closeButton = (ImageView) mFloatingView.findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(GONE);
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

        /**
         * Making the floating widget responsible to the touch events by setting an onTouchListener
         */

        mFloatingView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX, initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //remember the initial position
                        initialX = params.x;
                        initialY = params.y;
                        mClosingButtonView.setVisibility(View.VISIBLE);

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //update the new parameters of the position
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        //update the layout with new X and Y coordinates
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        /*if(isOverlapping(mClosingButtonView,mFloatingView))
                            Toast.makeText(FloatingViewService.this, "Test successful", Toast.LENGTH_SHORT).show();*/
                        return true;
                    /**
                     * Since we have implemented the onTouchListener therefore we cannot implement onClickListener
                     * Therefor we make changes to the onTouchListener to handle touch events
                     */

                    case MotionEvent.ACTION_UP:

                        if (!isOverlapping(mFloatingView, mClosingButtonView)) {
                            int midX = (int) (size.x / 2);
                            if (params.x >= midX) {
                                animateToEnd(mFloatingView, params.x, size.x, params);
                            } else if (params.x < midX) {
                                animateToEnd(mFloatingView, params.x, 0, params);
                            }


                            int diffX = (int) (event.getRawX() - initialTouchX);
                            int diffY = (int) (event.getRawY() - initialTouchY);
                            if (diffX < 10 && diffY < 10) {
                                if (isViewCollapsed()) {
                                    //When user clicks on the image view of the collapsed layout,
                                    //visibility of the collapsed layout will be changed to "View.GONE"
                                    //and expanded view will become visible.
                                    collapsedView.setVisibility(GONE);
                                    expandedView.setVisibility(View.VISIBLE);
                                }
                            }
                            mClosingButtonView.setVisibility(GONE);
                        }
                        if (isOverlapping(mClosingButtonView, mFloatingView))
                            stopSelf();
                        return true;

                }
                return false;
            }
        });

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
        }
    }

    private boolean isOverlapping(View v1, View v2) {

        // Location holder
        int[] loc = new int[2];

        v1.getLocationOnScreen(loc);
        rc1 = new Rect(loc[0], loc[1], loc[0] + v1.getWidth(), loc[1] + v1.getHeight());

        v2.getLocationOnScreen(loc);
        rc2 = new Rect(loc[0], loc[1], loc[0] + v2.getWidth(), loc[1] + v2.getHeight());
        if (Rect.intersects(rc1, rc2)) {

            return true;
        }
        return false;

    }

    private void animateToEnd(final View viewToAnimate, int startX, int finalX, final WindowManager.LayoutParams params) {
        ValueAnimator translateLeft = ValueAnimator.ofInt(startX, finalX);
        translateLeft.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int updatedX = (Integer) animation.getAnimatedValue();
                params.x = updatedX;
                //update the layout with new X and Y coordinates
                mWindowManager.updateViewLayout(mFloatingView, params);

            }
        });
        translateLeft.setDuration(200);
        translateLeft.start();
    }
}
