package com.mdg.droiders.floaters;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Binder;
import android.os.IBinder;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class FloatingViewService extends Service {

    private WindowManager mWindowManager;
    private FloatingViewContainer floatingExpandedContainer, floatingCollapsedContainer;
    private SheetLayoutContainer mSheetLayoutContainer;
    private ExpandedWindow expandedWindow;
    private CollapsedWindow collapsedWindow;
    private FloatingViewBinder mBinder;
    // TODO: Make the Closing button disappear when finger is lifted off the screen and visible again when the finger touches the screen
    private View mClosingButtonView;
    private Integer expandedChoice = 0;  //Default choice is of MUSIC PLAYER

    /**
     * Contains the types of Expanded Views the service can offer.
     */
    public enum FloatingViewExpanded {
        /**
         * Floating view expands into a mini music player type view
         * which contains play/pause, next song & previous song options.
         */
        PLAYER,
        /**
         * Floating view expands into a sheet just like messenger
         * chat heads does.
         */
        SHEET
    }

    /**
     * Binder class that contains public method which the user can call
     * to interact with the service.
     */
    public class FloatingViewBinder extends Binder {
        /**
         * Adds the layout specified by layoutId as a child view to the sheet layout.
         * The specified layout is shown in the sheet layout when the floating view expands into sheet layout.
         * <p><strong>Note:</strong>It is mandatory that you put all your content in a single xml
         * and add that xml alone to the SheetLayout. Further calls to this function will not add
         * the xml as a child to the sheet layout</p>
         *
         * @param layoutId The layout Id of the xml which is to be shown in expanded mode
         * @see #setParentLayout(ViewGroup)
         */
        public void setParentLayout(int layoutId) {
            mSheetLayoutContainer.setContent(layoutId);
        }

        /**
         * Adds the specified layout as child to the sheet layout with default layout params.
         * <p><strong>Note: </strong>It is mandatory that you put all your content in a single ViewGroup
         * and add that ViewGroup alone to the SheetLayout. Further calls to this function will not add
         * the view as a child to the sheet layout</p>
         *
         * @param layout The ViewGroup which is to be shown in expanded mode
         * @see #setParentLayout(int)
         */
        public void setParentLayout(ViewGroup layout) {
            mSheetLayoutContainer.setContent(layout);
        }

        /**
         * Specify the type of expanded view you want when the floating view expands. Currently
         * available choices are {@link FloatingViewExpanded#SHEET} and {@link FloatingViewExpanded#PLAYER}.
         *
         * @param type The type of expanded view.
         */
        public void specifyExpandedView(FloatingViewExpanded type) {
            if (type == FloatingViewExpanded.PLAYER) expandedChoice = 0;
            else if (type == FloatingViewExpanded.SHEET) expandedChoice = 1;
        }

        /**
         * Removes all the views from the current window.
         */
        public void releaseService() {
            if (expandedWindow != null && mWindowManager != null && collapsedWindow != null) {
                mWindowManager.removeView(expandedWindow.getWindow());
                mWindowManager.removeView(collapsedWindow.getWindow());
            }
        }
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
        // Init expanded view related classes
        floatingExpandedContainer = new FloatingViewContainer(this, true);
        mSheetLayoutContainer = new SheetLayoutContainer(this);
        WindowContainer mWindowContainer = new WindowContainer(mSheetLayoutContainer, floatingExpandedContainer);
        expandedWindow = new ExpandedWindow(this, mWindowContainer);
        // Init collapsed view related classes
        floatingCollapsedContainer = new FloatingViewContainer(this, true);
        collapsedWindow = new CollapsedWindow(this, floatingCollapsedContainer);
        // Init close button at which will be position at the bottom of the screen
        mClosingButtonView = LayoutInflater.from(this).inflate(R.layout.close_button, null);

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mBinder.releaseService();
    }

    /**
     * @return Default {@link android.view.WindowManager.LayoutParams} for the closing button
     */
    private WindowManager.LayoutParams initLayoutParams() {
        return new WindowManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
    }

    // TODO: Move the initialisation of onCLickListeners to the Floating View container class.

    /**
     * Init on click listener in the MUSIC PLAYER expanded version of the floating view.
     *
     * @param mFloatingView The floating view that of {@link ExpandedWindow}
     */
    private void initOnClickListeners(View mFloatingView) {

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
                floatingExpandedContainer.getCollapsedView().setVisibility(View.VISIBLE);
                floatingExpandedContainer.getExpandedView().setVisibility(View.GONE);
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

        final WindowManager.LayoutParams closeButtonParams;
        closeButtonParams = initLayoutParams();

        closeButtonParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
        /*closeButtonParams.x = (int) (size.x)/2;
        closeButtonParams.y = (int) size.y;*/

        expandedWindow.addChildViews();
        collapsedWindow.addChildViews();

        //Add the view to the window
        mWindowManager.addView(mClosingButtonView, closeButtonParams);
        expandedWindow.addToWindow(mWindowManager);
        collapsedWindow.addToWindow(mWindowManager);

        //the root element of the closing button
        final View closeWindowButton = mClosingButtonView.findViewById(R.id.close_window_button);

        initOnClickListeners(floatingExpandedContainer.getmFloatingView());

        expandedWindow.setExpandedChoice(expandedChoice);
        expandedWindow.setTouchListenerOnWindow(mWindowManager, closeWindowButton);
        collapsedWindow.setOnTouchListenerOnWindow(mWindowManager, size, closeWindowButton);
        expandedWindow.setmListener(new ExpandedWindow.expandedWindowListener() {
            @Override
            public void clickHappened() {
                expandedWindow.getWindow().setVisibility(View.GONE);
                collapsedWindow.getWindow().setVisibility(View.VISIBLE);
            }

            @Override
            public void overlapped() {
                mBinder.releaseService();
            }
        });
        collapsedWindow.setmListener(new CollapsedWindow.collapsedWindowListener() {
            @Override
            public void clickHappened() {
                collapsedWindow.getWindow().setVisibility(View.GONE);
                expandedWindow.toggleVisibiltyStatus(mWindowManager);
                expandedWindow.setFloatingView();
                expandedWindow.setExpandedChoice(expandedChoice);
                expandedWindow.getWindow().setVisibility(View.VISIBLE);
            }

            @Override
            public void overlapped() {
                mBinder.releaseService();
            }
        });
    }

}
