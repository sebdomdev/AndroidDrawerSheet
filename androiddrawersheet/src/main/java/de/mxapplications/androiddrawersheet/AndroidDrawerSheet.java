package de.mxapplications.androiddrawersheet;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Google calls it navigation drawer or bottom sheet: This drawer sheet can be opened from the left, right, top, and bottom and is displayed on top of the rest of the content.
 *
 * Created by Sebastian Dombrowski on 10/19/2015.
 */
public class AndroidDrawerSheet extends FrameLayout {
    public static final int ALIGNMENT_TOP = 1;
    public static final int ALIGNMENT_BOTTOM = 2;
    public static final int ALIGNMENT_LEFT = 3;
    public static final int ALIGNMENT_RIGHT = 4;

    private static final int DRAWER_STATE_CLOSED = 1;
    private static final int DRAWER_STATE_OPEN = 2;

    private FrameLayout mInnerContainer;

    private int mAlignment = ALIGNMENT_BOTTOM;
    private boolean mInvisibleOffset = false;
    private int mOffset = 0;
    private DrawerListener mDrawerListener;
    private boolean mDragging = false;
    private int mFullSize;
    private int mDrawerState=DRAWER_STATE_CLOSED;
    private Context mContext;
    private int mMinimumOpeningSize = 0;
    private int mMinimumClosingSize = 0;
    private boolean mStickyDrag = true;

    private AlignmentStrategy mAlignmentStrategy;

    private List<OnInteractionListener> onInteractionListenerList = new ArrayList<>();
    private List<OnResizeListener> onResizeListenerList = new ArrayList<>();

    /***
     * Constructs the CheckableGridView .
     *
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc.
     */
    public AndroidDrawerSheet(Context context) {
        super(context);
        init(context, null, 0, 0);
    }
    /***
     * Constructs the CheckableGridView.
     *
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc.
     * @param attrs The attributes of the XML tag that is inflating the view.
     */
    public AndroidDrawerSheet(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }
    /***
     * Constructs the CheckableGridView.
     *
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc.
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a
     *        reference to a style resource that supplies default values for
     *        the view. Can be 0 to not look for defaults.
     */
    public AndroidDrawerSheet(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }
    /***
     * Constructs the CheckableGridView.
     *
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc.
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a
     *        reference to a style resource that supplies default values for
     *        the view. Can be 0 to not look for defaults.
     * @param defStyleRes A resource identifier of a style resource that
     *        supplies default values for the view, used only if
     *        defStyleAttr is 0 or can not be found in the theme. Can be 0
     *        to not look for defaults.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AndroidDrawerSheet(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context ,attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mFullSize = mAlignmentStrategy.getSize(this);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /***
     * Initializes member variables and assignes the appropriate {@link de.mxapplications.androiddrawersheet.AndroidDrawerSheet.AlignmentStrategy} based on the alignment set by the user.
     * It also initializes the inner ViewGroup (mInnerContainer) that holds all the children of the view.
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc.
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a
     *        reference to a style resource that supplies default values for
     *        the view. Can be 0 to not look for defaults.
     * @param defStyleRes A resource identifier of a style resource that
     *        supplies default values for the view, used only if
     *        defStyleAttr is 0 or can not be found in the theme. Can be 0
     *        to not look for defaults.
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
        mContext = context;

        setSaveEnabled(true);

        TypedArray styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.AndroidDrawerSheet);
        mOffset = styledAttributes.getDimensionPixelSize(R.styleable.AndroidDrawerSheet_drawerOffset, mOffset);
        mAlignment = styledAttributes.getInt(R.styleable.AndroidDrawerSheet_drawerAlignment, mAlignment);
        mInvisibleOffset = styledAttributes.getBoolean(R.styleable.AndroidDrawerSheet_drawerOffsetInvisible, mInvisibleOffset);
        mMinimumClosingSize = styledAttributes.getDimensionPixelSize(R.styleable.AndroidDrawerSheet_minimumClosingSize, mMinimumClosingSize);
        mMinimumOpeningSize = styledAttributes.getDimensionPixelSize(R.styleable.AndroidDrawerSheet_minimumOpeningSize, mMinimumOpeningSize);
        mStickyDrag = styledAttributes.getBoolean(R.styleable.AndroidDrawerSheet_stickyDrag, mStickyDrag);

        styledAttributes.recycle();

        switch (mAlignment){
            case ALIGNMENT_TOP:
                mAlignmentStrategy = new TopAlignmentStrategy();
                break;
            case ALIGNMENT_BOTTOM:
                mAlignmentStrategy = new BottomAlignmentStrategy();
                break;
            case ALIGNMENT_RIGHT:
                mAlignmentStrategy = new RightAlignmentStrategy();
                break;
            case ALIGNMENT_LEFT:
            default:
                mAlignmentStrategy = new LeftAlignmentStrategy();
                break;
        }

        final RelativeLayout outerContainer = new RelativeLayout(context);
        ViewGroup.LayoutParams outerContainerLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        outerContainer.setLayoutParams(outerContainerLayoutParams);

        ViewTreeObserver observer = outerContainer.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                outerContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                mAlignmentStrategy.setOuterAlignment();
                if(mDrawerState == DRAWER_STATE_CLOSED){
                    closeDrawer(false);
                }
                return true;
            }
        });
        super.addView(outerContainer);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mAlignmentStrategy.setSize(layoutParams, mOffset);
        layoutParams.addRule(mAlignmentStrategy.getAlignment());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mInnerContainer = new FrameLayout(getContext(), attrs, defStyleAttr, defStyleRes);
            mInnerContainer.setElevation(16);
            mInnerContainer.setOutlineProvider(ViewOutlineProvider.PADDED_BOUNDS);
            if(!mInvisibleOffset) {
                mAlignmentStrategy.setShadowMargins(layoutParams);
            }
        }else{
            mInnerContainer = new FrameLayout(getContext(), attrs, defStyleAttr);
        }

        mInnerContainer.setLayoutParams(layoutParams);
        if(mInvisibleOffset){
            mAlignmentStrategy.setPadding(mInnerContainer, mOffset);
        }

        mDrawerListener = new DrawerListener();
        mInnerContainer.setOnTouchListener(mDrawerListener);

        outerContainer.addView(mInnerContainer);
    }

    /***
     * Returns true if the drawer is currently open, false otherwise.
     * @return true if the drawer is currently open, false otherwise.
     */
    public boolean isDrawerOpen(){
        return mDrawerState == DRAWER_STATE_OPEN;
    }

    /***
     * Adds an {@link de.mxapplications.androiddrawersheet.AndroidDrawerSheet.OnInteractionListener} which will receive event notifications when the drawer is opened or closed.
     * @param listener The listener that will receive events notifications.
     */
    public void addOnInteractionListener(OnInteractionListener listener){
        onInteractionListenerList.add(listener);
    }

    /***
     * Removes an {@link de.mxapplications.androiddrawersheet.AndroidDrawerSheet.OnInteractionListener} which will not receive event notifications when the drawer is opened or closed anymore.
     * @param listener The listener that won't receive event notifications anymore.
     */
    public void removeOnInteractionListener(OnInteractionListener listener){
        onInteractionListenerList.remove(listener);
    }

    /***
     * Adds a {@link de.mxapplications.androiddrawersheet.AndroidDrawerSheet.OnResizeListener} which will receive event notifications when the drawer is resized.
     * @param listener The listener that will receive event notifications.
     */
    public void addOnResizeListener(OnResizeListener listener){
        onResizeListenerList.add(listener);
    }
    /***
     * Removes a {@link de.mxapplications.androiddrawersheet.AndroidDrawerSheet.OnResizeListener} which will not receive event notifications when the drawer is resized anymore.
     * @param listener The listener that will receive event notifications.
     */
    public void removeOnResizeListener(OnResizeListener listener){
        onResizeListenerList.remove(listener);
    }
    private void notifyOnInteractionListeners(boolean open, boolean beforeDrawerStateChanged){
        for(OnInteractionListener listener:onInteractionListenerList){
            if(open && beforeDrawerStateChanged){
                listener.beforeDrawerOpened();
            }else if(open && !beforeDrawerStateChanged){
                listener.afterDrawerOpened();
            }else if(!open && beforeDrawerStateChanged){
                listener.beforeDrawerClosed();
            }else if(!open && !beforeDrawerStateChanged){
                listener.afterDrawerClosed();
            }
        }
    }
    private void notifyOnResizeListeners(int size){
        for(OnResizeListener listener:onResizeListenerList){
            listener.drawerResized(size);
        }
    }

    /***
     * Opens the drawer and notifies all listeners.
     */
    public void openDrawer(){
        if(mDrawerState != DRAWER_STATE_OPEN){
            notifyOnInteractionListeners(true, true);
        }
        ViewGroup.LayoutParams layoutParams= mInnerContainer.getLayoutParams();
        mAlignmentStrategy.setSize(layoutParams, mFullSize);
        mInnerContainer.setLayoutParams(layoutParams);
        if(mDrawerState != DRAWER_STATE_OPEN) {
            mDrawerState = DRAWER_STATE_OPEN;
            notifyOnResizeListeners(mFullSize);
            notifyOnInteractionListeners(true, false);
        }
    }

    /***
     * Opens the drawer if it is closed and closes the drawer if it is open.
     */
    public void toggleDrawer(){
        if(mDrawerState == DRAWER_STATE_CLOSED){
            openDrawer();
        }else{
            closeDrawer();
        }
    }

    /***
     * Closes the drawer and notifies the listeners if the argument is true.
     * @param notify If true, listeners will be notified, if false, they won't be.
     */
    private void closeDrawer(boolean notify){
        if(notify && mDrawerState != DRAWER_STATE_CLOSED){
            notifyOnInteractionListeners(false, true);
        }
        ViewGroup.LayoutParams layoutParams= mInnerContainer.getLayoutParams();
        mAlignmentStrategy.setSize(layoutParams, mOffset);
        mInnerContainer.setLayoutParams(layoutParams);
        if(notify && mDrawerState != DRAWER_STATE_CLOSED) {
            mDrawerState = DRAWER_STATE_CLOSED;
            notifyOnResizeListeners(mOffset);
            notifyOnInteractionListeners(false, false);
        }else{
            mDrawerState=DRAWER_STATE_CLOSED;
        }
    }

    /***
     * Closes the drawer and notifies all listeners.
     */
    public void closeDrawer(){
        closeDrawer(true);
    }

    @Override
    public void addView(View child) {
        if(mInnerContainer == null){
            super.addView(child);
            return;
        }
        mInnerContainer.addView(child);
    }

    @Override
    public void addView(View child, int index) {
        if(mInnerContainer == null){
            super.addView(child, index);
        }else {
            mInnerContainer.addView(child, index);
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if(mInnerContainer == null){
            super.addView(child, index, params);
        }else {
            mInnerContainer.addView(child, index, params);
        }
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        if(mInnerContainer == null){
            super.addView(child, params);
        }else {
            mInnerContainer.addView(child, params);
        }
    }

    @Override
    public void addView(View child, int width, int height) {
        if(mInnerContainer == null){
            super.addView(child, width, height);
        }else {
            mInnerContainer.addView(child, width, height);
        }
    }

    @Override
    protected boolean addViewInLayout(View child, int index, ViewGroup.LayoutParams params) {
        return false;
    }

    @Override
    protected boolean addViewInLayout(View child, int index, ViewGroup.LayoutParams params, boolean preventRequestLayout) {
        return false;
    }

    /***
     * Returns the alignment of the drawer which is on of ALIGNMENT_TOP, ALIGNMENT_LEFT, ALIGNMENT_RIGHT, or ALIGNMENT_BOTTOM.
     * @return The alignment of the drawer which is on of ALIGNMENT_TOP, ALIGNMENT_LEFT, ALIGNMENT_RIGHT, or ALIGNMENT_BOTTOM.
     */
    public int getAlignment() {
        return mAlignment;
    }

    /***
     * Sets the alignment of the drawer which is on of ALIGNMENT_TOP, ALIGNMENT_LEFT, ALIGNMENT_RIGHT, or ALIGNMENT_BOTTOM.
     * @param mAlignment The alignment of the drawer which is on of ALIGNMENT_TOP, ALIGNMENT_LEFT, ALIGNMENT_RIGHT, or ALIGNMENT_BOTTOM.
     * @return The AndroidDrawerSheet-object.
     */
    public AndroidDrawerSheet setAlignment(int mAlignment) {
        this.mAlignment = mAlignment;
        return this;
    }

    /***
     * Returns true if the offset is invisible. If the is offset is visible, the drawer is partially visible even when closed.
     * How much the drawer is visible depends on the offset (see {@link #setOffset(int)}).
     * If the offset is invisible, the drawer can still be dragged to open but it is not visible when closed.
     *
     * @return True, if the offset is invisible, false otherwise.
     */
    public boolean isInvisibleOffset() {
        return mInvisibleOffset;
    }

    /***
     * Set if the is offset is visible or not. If it is visible, the drawer is partially visible even when closed.
     * How much the drawer is visible depends on the offset (see {@link #setOffset(int)}).
     * If the offset is invisible, the drawer can still be dragged to open but it is not visible when closed.
     *
     * @param mInvisibleOffset True, if the offset is invisible, false otherwise.
     * @return The AndroidDrawerSheet-object.
     */
    public AndroidDrawerSheet setInvisibleOffset(boolean mInvisibleOffset) {
        this.mInvisibleOffset = mInvisibleOffset;
        return this;
    }

    /***
     * Returns the offset in pixels. The offset determines how much of the drawer is visible if the drawer is closed.
     * @return The offset in pixels.
     */
    public int getOffset() {
        return mOffset;
    }

    /***
     * Set the offset in pixels. The offset determines how much of the drawer is visible if the drawer is closed.
     * @param mOffset The offset in pixels.
     * @return
     */
    public AndroidDrawerSheet setOffset(int mOffset) {
        this.mOffset = mOffset;
        return this;
    }

    /***
     * This listener interface provides methods that are being called when the drawer is opened or closed.
     */
    public interface OnInteractionListener{
        /***
         * This method is called before the drawer is closed.
         */
        void beforeDrawerClosed();

        /***
         * This method is called before the drawer is opened.
         */
        void beforeDrawerOpened();

        /***
         * This method is called after the drawer is closed.
         */
        void afterDrawerClosed();

        /***
         * This method is called after the drawer is opened.
         */
        void afterDrawerOpened();
    }

    /***
     * This listener interface provides a method that is called when the drawer is resized.
     */
    public interface OnResizeListener{
        /***
         * This method is called when the drawer is resized.
         * @param size The new size of the drawer is pixels.
         */
        void drawerResized(int size);
    }

    /***
     * Returns the minimum opening size in pixels. This size determines how far the drawer has to be opened by the user, before it completely opens when the user releases it.
     * @return The minimum opening size in pixels.
     */
    public int getMinimumOpeningSize() {
        return mMinimumOpeningSize;
    }

    /***
     * Sets the minimum opening size in pixels. This size determines how far the drawer has to be opened by the user, before it completely opens when the user releases it.
     * @param minimumOpeningSize The minimum opening size in pixels.
     */
    public AndroidDrawerSheet setMinimumOpeningSize(int minimumOpeningSize) {
        this.mMinimumOpeningSize = minimumOpeningSize;
        return this;
    }
    /***
     * Returns the minimum closing size in pixels. This size determines how far the drawer has to be closed by the user, before it completely closes when the user releases it.
     * @return The minimum closing size in pixels.
     */
    public int getMinimumClosingSize() {
        return mMinimumClosingSize;
    }
    /***
     * Sets the minimum closing size in pixels. This size determines how far the drawer has to be closed by the user, before it completely closes when the user releases it.
     * @param minimumClosingSize The minimum closing size in pixels.
     */
    public AndroidDrawerSheet setMinimumClosingSize(int minimumClosingSize) {
        this.mMinimumClosingSize = minimumClosingSize;
        return this;
    }

    /***
     * Returns true if sticky drag is enabled. If sticky drag is enabled, the drawer will close completely, if the user releases the drawer after dragging it and the drawer is less then half open.
     * The drawer will open completely, if the user releases the drawer after dragging it and the drawer is more then half open.
     * If stick drag is enabled, minimum closing size and minimum opening size are ignored.
     * @return True if sticky drag is enabled, false otherwise.
     */
    public boolean isStickyDrag() {
        return mStickyDrag;
    }
    /***
     * Set sticky drag enabled or disabled. If sticky drag is enabled, the drawer will close completely, if the user releases the drawer after dragging it and the drawer is less then half open.
     * The drawer will open completely, if the user releases the drawer after dragging it and the drawer is more then half open.
     * If stick drag is enabled, minimum closing size and minimum opening size are ignored.
     * @param stickyDrag True to enabled sticky drag and false to disable it.
     */
    public AndroidDrawerSheet setStickyDrag(boolean stickyDrag) {
        this.mStickyDrag = stickyDrag;
        return this;
    }

    private class DrawerListener implements OnTouchListener{
        int mTouchPositionDifference = 0;

        android.view.GestureDetector gestureDetector = new android.view.GestureDetector(mContext, new android.view.GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }
            @Override
            public void onShowPress(MotionEvent e) {
            }
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }
            @Override
            public void onLongPress(MotionEvent e) {
            }
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                int size = 0;
                int oldDrawerState = mDrawerState;
                if(mAlignmentStrategy.isSwipeClose(e1, e2, velocityX, velocityY)){
                    size = mOffset;
                    mDrawerState = DRAWER_STATE_CLOSED;
                }else if(mAlignmentStrategy.isSwipeOpen(e1, e2, velocityX, velocityY)){
                    size = mFullSize;
                    mDrawerState = DRAWER_STATE_OPEN;
                }else{
                    return false;
                }
                ViewGroup.LayoutParams layoutParams= mInnerContainer.getLayoutParams();
                mAlignmentStrategy.setSize(layoutParams, size);
                notifyOnResizeListeners(size + mOffset);
                if(oldDrawerState!=mDrawerState){
                    notifyOnInteractionListeners(mDrawerState == DRAWER_STATE_OPEN, true);
                }
                mInnerContainer.setLayoutParams(layoutParams);
                if(oldDrawerState!=mDrawerState) {
                    notifyOnInteractionListeners(mDrawerState == DRAWER_STATE_OPEN, false);
                }
                return true;
            }
        });

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(gestureDetector.onTouchEvent(event)){
                return true;
            }

            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                mDragging = true;
                mTouchPositionDifference = mAlignmentStrategy.calculateTouchPositionDifference(v, event);
            }else if(event.getAction() == MotionEvent.ACTION_MOVE) {
                if(mDragging) {
                    ViewGroup.LayoutParams layoutParams=v.getLayoutParams();
                    int size = mAlignmentStrategy.calculateRawSize(v.getRootView(), event);
                    size+= mTouchPositionDifference;
                    size=Math.max(size, mOffset);
                    size=Math.min(size, mFullSize);
                    mAlignmentStrategy.setSize(layoutParams, size);
                    v.setLayoutParams(layoutParams);
                    notifyOnResizeListeners(size + mOffset);
                }
            }else if(event.getAction() == MotionEvent.ACTION_UP && mDragging) {
                mDragging =false;

                ViewGroup.LayoutParams layoutParams=v.getLayoutParams();
                int size = mAlignmentStrategy.calculateRawSize(v.getRootView(), event);
                size = size + mTouchPositionDifference;
                int oldDrawerState=mDrawerState;
                if((size <= mMinimumClosingSize)||(mStickyDrag && size < mFullSize / 2)){
                    size=mOffset;
                    mDrawerState = DRAWER_STATE_CLOSED;
                }
                if((size >= mFullSize - mMinimumOpeningSize)||(mStickyDrag && size >= mFullSize / 2)){
                    size = mFullSize;
                    mDrawerState = DRAWER_STATE_OPEN;
                }
                mAlignmentStrategy.setSize(layoutParams, size);
                if(oldDrawerState != mDrawerState){
                    notifyOnInteractionListeners(mDrawerState == DRAWER_STATE_OPEN, true);
                }
                v.setLayoutParams(layoutParams);
                notifyOnResizeListeners(size+mOffset);
                if(oldDrawerState != mDrawerState) {
                    notifyOnInteractionListeners(mDrawerState == DRAWER_STATE_OPEN, false);
                }
            }
            return true;
        }


    }


    private interface AlignmentStrategy{
        int SWIPE_THRESHOLD_VELOCITY = 200;
        int SWIPE_MIN_DISTANCE = 20;
        int ELEVATION_SHADOW=6;
        int getAlignment();
        boolean isSwipeClose(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY);
        boolean isSwipeOpen(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY);
        void setSize(ViewGroup.LayoutParams layoutParams, int size);
        int getSize(View view);
        int getSize(ViewGroup.LayoutParams layoutParams);
        void setPadding(View view, int padding);
        int calculateRawSize(View v, MotionEvent event);
        int calculateTouchPositionDifference(View v, MotionEvent event);
        void setOuterAlignment();
        void setShadowMargins(RelativeLayout.LayoutParams layoutParams);
    }
    private class TopAlignmentStrategy implements AlignmentStrategy{
        @Override
        public int getAlignment() {
            return RelativeLayout.ALIGN_PARENT_TOP;
        }
        @Override
        public boolean isSwipeClose(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return (velocityY < -1 *SWIPE_THRESHOLD_VELOCITY && (e1.getRawY() - e2.getRawY()) > SWIPE_MIN_DISTANCE);
        }
        @Override
        public boolean isSwipeOpen(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return (velocityY > SWIPE_THRESHOLD_VELOCITY && (e2.getRawY() - e1.getRawY()) > SWIPE_MIN_DISTANCE);
        }
        @Override
        public void setSize(ViewGroup.LayoutParams layoutParams, int size) {
            layoutParams.height=size;
        }
        @Override
        public int getSize(View view) {
            return view.getMeasuredHeight();
        }

        @Override
        public int getSize(ViewGroup.LayoutParams layoutParams) {
            return layoutParams.height;
        }

        @Override
        public void setPadding(View view, int padding) {
            view.setPadding(0, 0, 0, padding);
        }
        @Override
        public int calculateRawSize(View v, MotionEvent event) {
            return (int)event.getRawY();
        }

        @Override
        public int calculateTouchPositionDifference(View v, MotionEvent event) {
            return getSize(v.getLayoutParams()) - calculateRawSize(v, event);
        }

        @Override
        public void setOuterAlignment() {
            if(getLayoutParams() instanceof RelativeLayout.LayoutParams){
                ((RelativeLayout.LayoutParams) getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_TOP);
            }else if(getLayoutParams() instanceof LayoutParams){
                ((LayoutParams) getLayoutParams()).gravity=Gravity.TOP;
            }
        }

        @Override
        public void setShadowMargins(RelativeLayout.LayoutParams layoutParams) {
            layoutParams.setMargins(0, 0, 0, ELEVATION_SHADOW);
        }
    }
    private class BottomAlignmentStrategy implements AlignmentStrategy{
        @Override
        public int getAlignment() {
            return RelativeLayout.ALIGN_PARENT_BOTTOM;
        }

        @Override
        public boolean isSwipeClose(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return (velocityY > SWIPE_THRESHOLD_VELOCITY && (e2.getRawY()-e1.getRawY()) > SWIPE_MIN_DISTANCE);
        }

        @Override
        public boolean isSwipeOpen(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return (velocityY < -1 * SWIPE_THRESHOLD_VELOCITY && (e1.getRawY() - e2.getRawY()) > SWIPE_MIN_DISTANCE);
        }

        @Override
        public void setSize(ViewGroup.LayoutParams layoutParams, int size) {
            layoutParams.height=size;
        }

        @Override
        public int getSize(View view) {
            return view.getMeasuredHeight();
        }

        @Override
        public int getSize(ViewGroup.LayoutParams layoutParams) {
            return layoutParams.height;
        }

        @Override
        public void setPadding(View view, int padding) {
            view.setPadding(0, padding, 0, 0);
        }

        @Override
        public int calculateRawSize(View v, MotionEvent event) {
            return v.getRootView().getHeight()-(int)event.getRawY();
        }

        @Override
        public int calculateTouchPositionDifference(View v, MotionEvent event) {
            return getSize(v.getLayoutParams()) - calculateRawSize(v, event);
        }

        @Override
        public void setOuterAlignment() {
            if(getLayoutParams() instanceof RelativeLayout.LayoutParams){
                ((RelativeLayout.LayoutParams) getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            }else if(getLayoutParams() instanceof LayoutParams){
                ((LayoutParams) getLayoutParams()).gravity= Gravity.BOTTOM;
            }
        }
        @Override
        public void setShadowMargins(RelativeLayout.LayoutParams layoutParams) {
            layoutParams.setMargins(0, ELEVATION_SHADOW, 0, 0);
        }
    }
    private class LeftAlignmentStrategy implements AlignmentStrategy{
        @Override
        public int getAlignment() {
            return RelativeLayout.ALIGN_PARENT_LEFT;
        }

        @Override
        public boolean isSwipeClose(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return (velocityX < -1*SWIPE_THRESHOLD_VELOCITY && (e1.getRawX() - e2.getRawX()) > SWIPE_MIN_DISTANCE);
        }

        @Override
        public boolean isSwipeOpen(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return (velocityX > SWIPE_THRESHOLD_VELOCITY && (e2.getRawX() - e1.getRawX()) > SWIPE_MIN_DISTANCE);
        }

        @Override
        public void setSize(ViewGroup.LayoutParams layoutParams, int size) {
            layoutParams.width=size;
        }

        @Override
        public int getSize(View view) {
            return view.getMeasuredWidth();
        }

        @Override
        public int getSize(ViewGroup.LayoutParams layoutParams) {
            return layoutParams.width;
        }

        @Override
        public void setPadding(View view, int padding) {
            view.setPadding(0, 0, padding, 0);
        }

        @Override
        public int calculateRawSize(View v, MotionEvent event) {
            return (int)event.getRawX();
        }

        @Override
        public int calculateTouchPositionDifference(View v, MotionEvent event) {
            return getSize(v.getLayoutParams()) - calculateRawSize(v, event);
        }

        @Override
        public void setOuterAlignment() {
            if(getLayoutParams() instanceof RelativeLayout.LayoutParams){
                ((RelativeLayout.LayoutParams) getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            }else if(getLayoutParams() instanceof LayoutParams){
                ((LayoutParams) getLayoutParams()).gravity= Gravity.LEFT;
            }
        }
        @Override
        public void setShadowMargins(RelativeLayout.LayoutParams layoutParams) {
            layoutParams.setMargins(0, 0, ELEVATION_SHADOW, 0);
        }
    }
    private class RightAlignmentStrategy implements AlignmentStrategy{
        @Override
        public int getAlignment() {
            return RelativeLayout.ALIGN_PARENT_RIGHT;
        }

        @Override
        public boolean isSwipeClose(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return (velocityX > SWIPE_THRESHOLD_VELOCITY && (e2.getRawX() - e1.getRawX()) > SWIPE_MIN_DISTANCE);
        }

        @Override
        public boolean isSwipeOpen(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return (velocityX < -1 * SWIPE_THRESHOLD_VELOCITY && (e1.getRawX() - e2.getRawX()) > SWIPE_MIN_DISTANCE);
        }

        @Override
        public void setSize(ViewGroup.LayoutParams layoutParams, int size) {
            layoutParams.width = size;
        }

        @Override
        public int getSize(View view) {
            return view.getMeasuredWidth();
        }

        @Override
        public int getSize(ViewGroup.LayoutParams layoutParams) {
            return layoutParams.width;
        }

        @Override
        public void setPadding(View view, int padding) {
            view.setPadding(padding, 0, 0, 0);
        }

        @Override
        public int calculateRawSize(View v, MotionEvent event) {
            return v.getRootView().getWidth()-(int)event.getRawX();
        }

        @Override
        public int calculateTouchPositionDifference(View v, MotionEvent event) {
            return getSize(v.getLayoutParams()) - calculateRawSize(v, event);
        }

        @Override
        public void setOuterAlignment() {
            if(getLayoutParams() instanceof RelativeLayout.LayoutParams){
                ((RelativeLayout.LayoutParams) getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            }else if(getLayoutParams() instanceof LayoutParams){
                ((LayoutParams) getLayoutParams()).gravity= Gravity.RIGHT;
            }
        }
        @Override
        public void setShadowMargins(RelativeLayout.LayoutParams layoutParams) {
            layoutParams.setMargins(ELEVATION_SHADOW, 0, 0, 0);
        }
    }



    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable state = super.onSaveInstanceState();
        SavedState savedState = new SavedState(state);

        savedState.drawerState = this.mDrawerState;
        savedState.alignment = this.mAlignment;
        savedState.invisibleOffset = this.mInvisibleOffset;
        savedState.offset = this.mOffset;
        savedState.minClose = this.mMinimumClosingSize;
        savedState.minOpen = this.mMinimumOpeningSize;
        savedState.stickyDrag = this.mStickyDrag;

        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(!(state instanceof SavedState)){
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState savedState = (SavedState)state;

        super.onRestoreInstanceState(savedState.getSuperState());

        this.mDrawerState = savedState.drawerState;
        this.mAlignment = savedState.alignment;
        this.mInvisibleOffset = savedState.invisibleOffset;
        this.mOffset = savedState.offset;
        this.mMinimumClosingSize = savedState.minClose;
        this.mMinimumOpeningSize = savedState.minOpen;
        this.mStickyDrag = savedState.stickyDrag;
    }
    static class SavedState extends BaseSavedState{
        int drawerState;
        int alignment;
        boolean invisibleOffset;
        int offset;
        int minClose;
        int minOpen;
        boolean stickyDrag;

        public SavedState(Parcel source){
            super(source);
            this.drawerState = source.readInt();
            this.alignment = source.readInt();
            this.invisibleOffset = source.readByte()!=0;
            this.offset = source.readInt();
            this.minClose = source.readInt();
            this.minOpen = source.readInt();
            this.stickyDrag = source.readByte()!=0;
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.drawerState);
            out.writeInt(this.alignment);
            out.writeByte((byte) (this.invisibleOffset ? 1 : 0));
            out.writeInt(this.offset);
            out.writeInt(this.minClose);
            out.writeInt(this.minOpen);
            out.writeByte((byte)(this.stickyDrag?1:0));
        }
        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

}
