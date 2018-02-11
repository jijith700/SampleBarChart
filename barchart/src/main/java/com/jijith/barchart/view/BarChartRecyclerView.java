package com.jijith.barchart.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.jijith.barchart.R;
import com.jijith.barchart.adapter.BarChartViewAdapter;
import com.jijith.barchart.utils.ObservableScrollViewCallbacks;
import com.jijith.barchart.utils.RecyclerViewPositionHelper;
import com.jijith.barchart.utils.SavedStateScrolling;
import com.jijith.barchart.utils.Scrollable;


/**
 * BarChartRecyclerView class is used to display a bar chart by using a Recycler View
 */
public class BarChartRecyclerView extends FrameLayout implements Scrollable {

    public RecyclerView mRecyclerView;
    protected RecyclerView.OnScrollListener mOnScrollListener;
    protected LAYOUT_MANAGER_TYPE layoutManagerType;
    private boolean automaticLoadMoreEnabled = false;
    protected int mPadding;
    protected int mPaddingTop;
    protected int mPaddingBottom;
    protected int mPaddingLeft;
    protected int mPaddingRight;
    protected boolean mClipToPadding;
    private BarChartViewAdapter mAdapter;
    // Fields that should be saved onSaveInstanceState
    private int mPrevFirstVisiblePosition;
    private int mPrevFirstVisibleChildHeight = -1;
    private int mPrevScrolledChildrenHeight;
    private int mPrevScrollY;
    private int mScrollY;
    private SparseIntArray mChildrenHeights = new SparseIntArray();

    // Fields that don't need to be saved onSaveInstanceState
    private ObservableScrollViewCallbacks mCallbacks;

    private boolean mIntercepted;
    private MotionEvent mPrevMoveEvent;
    private ViewGroup mTouchInterceptionViewGroup;
    /**
     * empty view group
     */
    protected ViewStub mEmpty;
    protected View mEmptyView;
    protected int mEmptyId;


    private RecyclerViewPositionHelper mRecyclerViewHelper;
    private CustomRelativeWrapper mHeader;
    private int mTotalYScrolled;

    private static boolean isParallaxHeader = false;
    private LayoutInflater inflater;

    /**
     * control to show the loading view first when list is initiated at the beginning
     * true - assume there is a buffer to load things before and the adapter suppose zero data at the beignning
     * false - assume there is data to show at the beginning level
     */
    private boolean isFirstLoadingOnlineAdapter = false;
    // added by Sevan Joe to support scrollbars
    private static final int SCROLLBARS_NONE = 0;
    private static final int SCROLLBARS_VERTICAL = 1;
    private static final int SCROLLBARS_HORIZONTAL = 2;
    private int mScrollbarsStyle;
    private int mVisibleItemCount = 0;
    private int mTotalItemCount = 0;
    private int previousTotal = 0;
    private int mFirstVisibleItem;

    public BarChartRecyclerView(Context context) {
        super(context);
        initViews();
    }

    public BarChartRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initViews();
    }

    public BarChartRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        initViews();
    }

    public void setRecylerViewBackgroundColor(@ColorInt int color) {
        mRecyclerView.setBackgroundColor(color);
    }

    protected void initViews() {
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_bar_chart_recycler_view, this);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.lv_bar_chart);
        setScrollbars();

        if (mRecyclerView != null) {
            mRecyclerView.setClipToPadding(mClipToPadding);
            if (mPadding != -1.1f) {
                mRecyclerView.setPadding(mPadding, mPadding, mPadding, mPadding);
            } else {
                mRecyclerView.setPadding(mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom);
            }
        }

        setDefaultScrollListener();

        /**
         * empty view setup
         */
        mEmpty = (ViewStub) view.findViewById(R.id.empty_view);
        if (mEmptyId != 0) {
            mEmpty.setLayoutResource(mEmptyId);
            mEmptyView = mEmpty.inflate();
            mEmpty.setVisibility(View.GONE);
        }
    }

    /**
     * Show the custom or default empty view
     * You can customize it as loading view
     *
     * @return is the empty shown
     */
    public boolean showEmptyView() {
        if (mEmpty != null && mEmptyView != null && mAdapter != null) {
//            if (mAdapter.getEmptyViewPolicy() == EMPTY_CLEAR_ALL || mAdapter.getEmptyViewPolicy() == EMPTY_KEEP_HEADER) {
//                mEmpty.setVisibility(View.VISIBLE);
//                if (mEmptyViewListener != null) {
//                    mEmptyViewListener.onEmptyViewShow(mEmptyView);
//                }
//            }
            return true;
        } else {
            Log.d(VIEW_LOG_TAG, "it is unable to show empty view");
            return false;
        }
    }

    /**
     * Hide the custom or default empty view
     */
    public void hideEmptyView() {
        if (mEmpty != null && mEmptyView != null) {
            mEmpty.setVisibility(View.GONE);
        } else {
            Log.d(VIEW_LOG_TAG, "there is no such empty view");
        }
    }

    /**
     * Add ScrollBar of Recyclerview
     */
    protected void setScrollbars() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        switch (mScrollbarsStyle) {
            case SCROLLBARS_VERTICAL:
//                View verticalView = inflater.inflate(R.layout.vertical_recycler_view, mSwipeRefreshLayout, true);
//                mRecyclerView = (RecyclerView) verticalView.findViewById(R.id.ultimate_list);
                break;
            case SCROLLBARS_HORIZONTAL:
                View horizontalView = inflater.inflate(R.layout.horizontal_recycler_view, null, true);
                mRecyclerView = (RecyclerView) horizontalView.findViewById(R.id.ultimate_list);
                break;
            default:
                break;
        }
    }

    protected void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.BarChartRecyclerview);

        try {
            mPadding = (int) typedArray.getDimension(R.styleable.BarChartRecyclerview_recyclerviewPadding, -1.1f);
            mPaddingTop = (int) typedArray.getDimension(R.styleable.BarChartRecyclerview_recyclerviewPaddingTop, 0.0f);
            mPaddingBottom = (int) typedArray.getDimension(R.styleable.BarChartRecyclerview_recyclerviewPaddingBottom, 0.0f);
            mPaddingLeft = (int) typedArray.getDimension(R.styleable.BarChartRecyclerview_recyclerviewPaddingLeft, 0.0f);
            mPaddingRight = (int) typedArray.getDimension(R.styleable.BarChartRecyclerview_recyclerviewPaddingRight, 0.0f);
            mClipToPadding = typedArray.getBoolean(R.styleable.BarChartRecyclerview_recyclerviewClipToPadding, false);
            mEmptyId = typedArray.getResourceId(R.styleable.BarChartRecyclerview_recyclerviewEmptyView, 0);

        } finally {
            typedArray.recycle();
        }
    }


    private void setObserableScrollListener() {
        mRecyclerView.removeOnScrollListener(mOnScrollListener);
        mOnScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        };
        mRecyclerView.addOnScrollListener(mOnScrollListener);
    }

    private int[] mlastPositionsStaggeredGridLayout;

    private void scroll_load_more_detection(RecyclerView recyclerView) {

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

        if (layoutManagerType == null) {
            if (layoutManager instanceof GridLayoutManager) {
                layoutManagerType = LAYOUT_MANAGER_TYPE.GRID;
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                layoutManagerType = LAYOUT_MANAGER_TYPE.STAGGERED_GRID;
            } else if (layoutManager instanceof LinearLayoutManager) {
                layoutManagerType = LAYOUT_MANAGER_TYPE.LINEAR;
            } else {
                throw new RuntimeException("Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
            }
        }

        mTotalItemCount = layoutManager.getItemCount();
        mVisibleItemCount = layoutManager.getChildCount();

        switch (layoutManagerType) {
            case LINEAR:
                mFirstVisibleItem = mRecyclerViewHelper.findFirstVisibleItemPosition();
                break;
            case GRID:
                if (layoutManager instanceof GridLayoutManager) {
                    GridLayoutManager ly = (GridLayoutManager) layoutManager;
                    mFirstVisibleItem = ly.findFirstVisibleItemPosition();
                }
                break;
            case STAGGERED_GRID:
                if (layoutManager instanceof StaggeredGridLayoutManager) {
                    StaggeredGridLayoutManager sy = (StaggeredGridLayoutManager) layoutManager;

                    if (mlastPositionsStaggeredGridLayout == null)
                        mlastPositionsStaggeredGridLayout = new int[sy.getSpanCount()];

                    sy.findLastVisibleItemPositions(mlastPositionsStaggeredGridLayout);

                    sy.findFirstVisibleItemPositions(mlastPositionsStaggeredGridLayout);
                    mFirstVisibleItem = findMin(mlastPositionsStaggeredGridLayout);
                }
                break;
        }

        if (automaticLoadMoreEnabled) {

            if (mTotalItemCount > previousTotal) {
                automaticLoadMoreEnabled = false;
                previousTotal = mTotalItemCount;
            }
        }

        boolean bottomEdgeHit = (mTotalItemCount - mVisibleItemCount) <= mFirstVisibleItem;

        if (bottomEdgeHit) {
            previousTotal = mTotalItemCount;
        }
    }

    protected void setDefaultScrollListener() {
        mRecyclerView.removeOnScrollListener(mOnScrollListener);
        mOnScrollListener = new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mHeader != null) {
                    mTotalYScrolled += dy;
                    if (isParallaxHeader)
                        translateHeader(mTotalYScrolled);
                }

                scroll_load_more_detection(recyclerView);

            }
        };

        mRecyclerView.addOnScrollListener(mOnScrollListener);
    }

    /**
     * Set a listener that will be notified of any changes in scroll state or position.
     *
     * @param customOnScrollListener to set or null to clear
     * @deprecated Use {@link #addOnScrollListener(RecyclerView.OnScrollListener)} and
     * {@link #removeOnScrollListener(RecyclerView.OnScrollListener)}
     */
    public void setOnScrollListener(RecyclerView.OnScrollListener customOnScrollListener) {
        mRecyclerView.setOnScrollListener(customOnScrollListener);
    }

    public void addOnScrollListener(RecyclerView.OnScrollListener customOnScrollListener) {
        mRecyclerView.addOnScrollListener(customOnScrollListener);
    }

    public void removeOnScrollListener(RecyclerView.OnScrollListener customOnScrollListener) {
        mRecyclerView.removeOnScrollListener(customOnScrollListener);
    }

    public void addItemDividerDecoration(Context context) {
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecoration);
    }


    /**
     * Swaps the current adapter with the provided one. It is similar to
     * {@link #setAdapter(BarChartViewAdapter)} but assumes existing adapter and the new adapter uses the same
     * ViewHolder and does not clear the RecycledViewPool.
     * Note that it still calls onAdapterChanged callbacks.
     *
     * @param adapter                       The new adapter to set, or null to set no adapter.
     * @param removeAndRecycleExistingViews If set to true, RecyclerView will recycle all existing Views. If adapters have stable ids and/or you want to animate the disappearing views, you may prefer to set this to false.
     */
    public void swapAdapter(BarChartViewAdapter adapter, boolean removeAndRecycleExistingViews) {
        mRecyclerView.swapAdapter(adapter, removeAndRecycleExistingViews);
        setAdapterInternal(adapter);
    }

    public void setAdapter(BarChartViewAdapter adapter) {
        mRecyclerView.setAdapter(adapter);
        setAdapterInternal(adapter);
    }

    /**
     * Add an {@link RecyclerView.ItemDecoration} to this RecyclerView. Item decorations can affect both measurement and drawing of individual item views. Item decorations are ordered. Decorations placed earlier in the list will be run/queried/drawn first for their effects on item views. Padding added to views will be nested; a padding added by an earlier decoration will mean further item decorations in the list will be asked to draw/pad within the previous decoration's given area.
     *
     * @param itemDecoration Decoration to add
     */
    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        mRecyclerView.addItemDecoration(itemDecoration);
    }

    /**
     * Add an {@link RecyclerView.ItemDecoration} to this RecyclerView. Item decorations can affect both measurement and drawing of individual item views.
     * <p>Item decorations are ordered. Decorations placed earlier in the list will be run/queried/drawn first for their effects on item views. Padding added to views will be nested; a padding added by an earlier decoration will mean further item decorations in the list will be asked to draw/pad within the previous decoration's given area.</p>
     *
     * @param itemDecoration Decoration to add
     * @param index          Position in the decoration chain to insert this decoration at. If this value is negative the decoration will be added at the end.
     */
    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration, int index) {
        mRecyclerView.addItemDecoration(itemDecoration, index);
    }

    /**
     * Sets the {@link RecyclerView.ItemAnimator} that will handle animations involving changes
     * to the items in this RecyclerView. By default, RecyclerView instantiates and
     * uses an instance of {@link android.support.v7.widget.DefaultItemAnimator}. Whether item animations are enabled for the RecyclerView depends on the ItemAnimator and whether
     * the LayoutManager {@link android.support.v7.widget.RecyclerView.LayoutManager#supportsPredictiveItemAnimations()
     * supports item animations}.
     *
     * @param animator The ItemAnimator being set. If null, no animations will occur
     *                 when changes occur to the items in this RecyclerView.
     */
    public void setItemAnimator(RecyclerView.ItemAnimator animator) {
        mRecyclerView.setItemAnimator(animator);
    }

    /**
     * Gets the current ItemAnimator for this RecyclerView. A null return value
     * indicates that there is no animator and that item changes will happen without
     * any animations. By default, RecyclerView instantiates and
     * uses an instance of {@link android.support.v7.widget.DefaultItemAnimator}.
     *
     * @return ItemAnimator The current ItemAnimator. If null, no animations will occur
     * when changes occur to the items in this RecyclerView.
     */
    public RecyclerView.ItemAnimator getItemAnimator() {
        return mRecyclerView.getItemAnimator();
    }

    /**
     * Set the layout manager to the recycler
     *
     * @param manager lm
     */
    public void setLayoutManager(RecyclerView.LayoutManager manager) {
        mRecyclerView.setLayoutManager(manager);
    }

    /**
     * Get the adapter of UltimateRecyclerview
     *
     * @return ad
     */
    public RecyclerView.Adapter getAdapter() {
        return mRecyclerView.getAdapter();
    }


    /**
     * Set a BarChartViewAdapter or the subclass of BarChartViewAdapter to the recyclerview
     *
     * @param adapter the adapter in normal
     */
    private void setAdapterInternal(BarChartViewAdapter adapter) {
        mAdapter = adapter;
        if (mAdapter != null)
            mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeChanged(int positionStart, int itemCount) {
                    super.onItemRangeChanged(positionStart, itemCount);
                    updateHelperDisplays();
                }

                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    updateHelperDisplays();
                }

                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {
                    super.onItemRangeRemoved(positionStart, itemCount);
                    updateHelperDisplays();
                }

                @Override
                public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                    super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                    updateHelperDisplays();
                }

                @Override
                public void onChanged() {
                    super.onChanged();
                    updateHelperDisplays();
                }
            });

        mRecyclerViewHelper = RecyclerViewPositionHelper.createHelper(mRecyclerView);
    }


    private void updateHelperDisplays() {
        automaticLoadMoreEnabled = false;
        if (mAdapter == null)
            return;

        if (!isFirstLoadingOnlineAdapter) {
            isFirstLoadingOnlineAdapter = true;
            if (mAdapter.getAdapterItemCount() == 0) {

                mEmpty.setVisibility(mEmptyView == null ? View.VISIBLE : View.GONE);


            } else if (mEmptyId != 0) {
                mEmpty.setVisibility(View.GONE);
            }
        }

    }

    public void setHasFixedSize(boolean hasFixedSize) {
        mRecyclerView.setHasFixedSize(hasFixedSize);
    }


    public interface OnLoadMoreListener {
        void loadMore(int itemsCount, final int maxLastVisiblePosition);
    }

    public enum LAYOUT_MANAGER_TYPE {
        LINEAR,
        GRID,
        STAGGERED_GRID,
        PUZZLE,
    }

    private int findMax(int[] lastPositions) {
        int max = Integer.MIN_VALUE;
        for (int value : lastPositions) {
            if (value > max)
                max = value;
        }
        return max;
    }

    private int findMin(int[] lastPositions) {
        int min = Integer.MAX_VALUE;
        for (int value : lastPositions) {
            if (value != RecyclerView.NO_POSITION && value < min)
                min = value;
        }
        return min;
    }

    /**
     * Set the parallax header of the recyclerview
     *
     * @param header the view
     */
    public void setParallaxHeader(View header) {
        mHeader = new CustomRelativeWrapper(header.getContext());
        mHeader.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mHeader.addView(header, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        isParallaxHeader = true;
    }

    private float mScrollMultiplier = 0.5f;

    /**
     * Translates the adapter in Y
     *
     * @param of offset in px
     */
    public void translateHeader(float of) {
        float ofCalculated = of * mScrollMultiplier;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && of < mHeader.getHeight()) {
            mHeader.setTranslationY(ofCalculated);
        } else if (of < mHeader.getHeight()) {
            TranslateAnimation anim = new TranslateAnimation(0, 0, ofCalculated, ofCalculated);
            anim.setFillAfter(true);
            anim.setDuration(0);
            mHeader.startAnimation(anim);
        }
        mHeader.setClipY(Math.round(ofCalculated));
    }

    public interface OnParallaxScroll {
        void onParallaxScroll(float percentage, float offset, View parallax);
    }

    /**
     * Custom layout for the Parallax Header.
     */
    public static class CustomRelativeWrapper extends RelativeLayout {

        private int mOffset;

        public CustomRelativeWrapper(Context context) {
            super(context);
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            if (isParallaxHeader)
                canvas.clipRect(new Rect(getLeft(), getTop(), getRight(), getBottom() + mOffset));
            super.dispatchDraw(canvas);
        }

        public void setClipY(int offset) {
            mOffset = offset;
            invalidate();
        }

    }

    /**
     * the observable scroll view call backs
     *
     * @param listener listener to set
     */
    public void setScrollViewCallbacks(ObservableScrollViewCallbacks listener) {
        mCallbacks = listener;
    }

    public void setItemViewCacheSize(final int off_screen_items) {
        mRecyclerView.setItemViewCacheSize(off_screen_items);
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedStateScrolling ss = (SavedStateScrolling) state;
        mPrevFirstVisiblePosition = ss.prevFirstVisiblePosition;
        mPrevFirstVisibleChildHeight = ss.prevFirstVisibleChildHeight;
        mPrevScrolledChildrenHeight = ss.prevScrolledChildrenHeight;
        mPrevScrollY = ss.prevScrollY;
        mScrollY = ss.scrollY;
        mChildrenHeights = ss.childrenHeights;
        RecyclerView.LayoutManager layoutManager = getLayoutManager();

        /**
         * enhanced and store the previous scroll position
         */
        if (layoutManager != null) {
            int count = layoutManager.getChildCount();
            if (mPrevScrollY != RecyclerView.NO_POSITION && mPrevScrollY < count) {
                layoutManager.scrollToPosition(mPrevScrollY);
            }
        }

        super.onRestoreInstanceState(ss.getSuperState());
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedStateScrolling ss = new SavedStateScrolling(superState);
        ss.prevFirstVisiblePosition = mPrevFirstVisiblePosition;
        ss.prevFirstVisibleChildHeight = mPrevFirstVisibleChildHeight;
        ss.prevScrolledChildrenHeight = mPrevScrolledChildrenHeight;
        ss.prevScrollY = mPrevScrollY;
        ss.scrollY = mScrollY;
        ss.childrenHeights = mChildrenHeights;
        return ss;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mCallbacks != null) {
            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    mCallbacks.onDownMotionEvent();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mIntercepted = false;
                    break;
            }
        }
        return super.onInterceptTouchEvent(ev);

    }


    @Override
    public void setTouchInterceptionViewGroup(ViewGroup viewGroup) {
        mTouchInterceptionViewGroup = viewGroup;
        setObserableScrollListener();
    }

    @Override
    public void scrollVerticallyTo(int y) {
//        URLogs.d("vertically");
        View firstVisibleChild = getChildAt(0);
        if (firstVisibleChild != null) {
            int baseHeight = firstVisibleChild.getHeight();
            int position = y / baseHeight;
            scrollVerticallyToPosition(position);
        }
    }

    public void scrollVerticallyToPosition(int position) {
        RecyclerView.LayoutManager lm = getLayoutManager();

        if (lm != null && lm instanceof LinearLayoutManager) {
            ((LinearLayoutManager) lm).scrollToPositionWithOffset(position, 0);
        } else {
            lm.scrollToPosition(position);
        }
    }

    @Override
    public int getCurrentScrollY() {
        return mScrollY;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
//        URLogs.d("ev---" + ev);
        if (mCallbacks != null) {

            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mIntercepted = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mPrevMoveEvent == null) {
                        mPrevMoveEvent = ev;
                    }
                    float diffY = ev.getY() - mPrevMoveEvent.getY();
                    mPrevMoveEvent = MotionEvent.obtainNoHistory(ev);
                    if (getCurrentScrollY() - diffY <= 0) {
                        // Can't scroll anymore.

                        if (mIntercepted) {
                            // Already dispatched ACTION_DOWN event to parents, so stop here.
                            return false;
                        }

                        // Apps can set the interception target other than the direct parent.
                        final ViewGroup parent;
                        if (mTouchInterceptionViewGroup == null) {
                            parent = (ViewGroup) getParent();
                        } else {
                            parent = mTouchInterceptionViewGroup;
                        }

                        // Get offset to parents. If the parent is not the direct parent,
                        // we should aggregate offsets from all of the parents.
                        float offsetX = 0;
                        float offsetY = 0;
                        for (View v = this; v != null && v != parent; v = (View) v.getParent()) {
                            offsetX += v.getLeft() - v.getScrollX();
                            offsetY += v.getTop() - v.getScrollY();
                        }
                        final MotionEvent event = MotionEvent.obtainNoHistory(ev);
                        event.offsetLocation(offsetX, offsetY);

                        if (parent.onInterceptTouchEvent(event)) {
                            mIntercepted = true;

                            // If the parent wants to intercept ACTION_MOVE events,
                            // we pass ACTION_DOWN event to the parent
                            // as if these touch events just have began now.
                            event.setAction(MotionEvent.ACTION_DOWN);

                            // Return this onTouchEvent() first and set ACTION_DOWN event for parent
                            // to the queue, to keep events sequence.
                            post(new Runnable() {
                                @Override
                                public void run() {
                                    parent.dispatchTouchEvent(event);
                                }
                            });
                            return false;
                        }
                        // Even when this can't be scrolled anymore,
                        // simply returning false here may cause subView's click,
                        // so delegate it to super.
                        return super.onTouchEvent(ev);
                    }
                    break;
            }
        }
        return super.onTouchEvent(ev);
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return mRecyclerView.getLayoutManager();
    }
}
