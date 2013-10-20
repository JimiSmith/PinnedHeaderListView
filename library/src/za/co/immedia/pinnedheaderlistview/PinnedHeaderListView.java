package za.co.immedia.pinnedheaderlistview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class PinnedHeaderListView extends ListView implements OnScrollListener, View.OnTouchListener {

    private OnScrollListener mOnScrollListener;
    private Rect mClipRect = new Rect();

    public static interface PinnedSectionedHeaderAdapter {
        public boolean isSectionHeader(int position);

        public int getSectionForPosition(int position);

        public View getSectionHeaderView(int section, View convertView, ViewGroup parent);

        public int getSectionHeaderViewType(int section);

        public int getCount();
    }

    private PinnedSectionedHeaderAdapter mAdapter;
    private OnTouchListener mForwardingTouchListener = null;
    private float mLastUpEventY = -1;
    private View mCurrentHeader;
    private int mCurrentHeaderViewType = 0;
    private float mHeaderOffset;
    private boolean mShouldPin = true;
    private int mCurrentSection = 0;

    public PinnedHeaderListView(Context context) {
        super(context);
        super.setOnScrollListener(this);
        super.setOnTouchListener(this);
    }

    public PinnedHeaderListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setOnScrollListener(this);
        super.setOnTouchListener(this);
    }

    public PinnedHeaderListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        super.setOnScrollListener(this);
        super.setOnTouchListener(this);
    }

    public void setPinHeaders(boolean shouldPin) {
        mShouldPin = shouldPin;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        mCurrentHeader = null;
        mAdapter = (PinnedSectionedHeaderAdapter) adapter;
        super.setAdapter(adapter);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }

        if (mAdapter == null || mAdapter.getCount() == 0 || !mShouldPin || (firstVisibleItem < getHeaderViewsCount())) {
            mCurrentHeader = null;
            mHeaderOffset = 0.0f;
            for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++) {
                View header = getChildAt(i);
                if (header != null) {
                    header.setVisibility(VISIBLE);
                }
            }
            return;
        }

        firstVisibleItem -= getHeaderViewsCount();

        int section = mAdapter.getSectionForPosition(firstVisibleItem);
        int viewType = mAdapter.getSectionHeaderViewType(section);
        mCurrentHeader = getSectionHeaderView(section, mCurrentHeaderViewType != viewType ? null : mCurrentHeader);
        ensurePinnedHeaderLayout(mCurrentHeader);
        mCurrentHeaderViewType = viewType;

        mHeaderOffset = 0.0f;

        for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++) {
            if (mAdapter.isSectionHeader(i)) {
                View header = getChildAt(i - firstVisibleItem);
                float headerTop = header.getTop();
                float pinnedHeaderHeight = mCurrentHeader.getMeasuredHeight();
                header.setVisibility(VISIBLE);
                if (pinnedHeaderHeight >= headerTop && headerTop > 0) {
                    mHeaderOffset = headerTop - header.getHeight();
                } else if (headerTop <= 0) {
                    header.setVisibility(INVISIBLE);
                }
            }
        }
        invalidate();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    private View getSectionHeaderView(int section, View oldView) {
        boolean shouldLayout = section != mCurrentSection || oldView == null;

        View view = mAdapter.getSectionHeaderView(section, oldView, this);
        if (shouldLayout) {
            // a new section, thus a new header. We should lay it out again
            ensurePinnedHeaderLayout(view);
            mCurrentSection = section;
        }
        return view;
    }

    private void ensurePinnedHeaderLayout(View header) {
        if (header.isLayoutRequested()) {
            measureHeader(header);
            header.layout(0, 0, header.getMeasuredWidth(), header.getMeasuredHeight());
        }
    }

    private void measureHeader(View header) {
        int widthSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY);

        int heightSpec;
        ViewGroup.LayoutParams layoutParams = header.getLayoutParams();
        if (layoutParams != null && layoutParams.height > 0) {
            heightSpec = MeasureSpec.makeMeasureSpec(layoutParams.height, MeasureSpec.EXACTLY);
        } else {
            heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        header.measure(widthSpec, heightSpec);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mCurrentHeader != null) {
            measureHeader(mCurrentHeader);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mCurrentHeader != null) {
            ensurePinnedHeaderLayout(mCurrentHeader);
        }
    }

    @Override
    public void invalidateViews() {
        super.invalidateViews();
        if (mCurrentHeader != null) {
            mCurrentHeader.invalidate();
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (mCurrentHeader != null) {
            mCurrentHeader.invalidate();
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (!(mAdapter == null || !mShouldPin || mCurrentHeader == null)) {
            canvas.save();
            mClipRect.set(0, (int) (mCurrentHeader.getMeasuredHeight() + mHeaderOffset), getWidth(), getBottom());
            canvas.clipRect(mClipRect);
        }

        super.dispatchDraw(canvas);
        if (mAdapter == null || !mShouldPin || mCurrentHeader == null) {
            return;
        }
        canvas.restore();
        int saveCount = canvas.save();
        canvas.translate(0, mHeaderOffset);
        mClipRect.set(0, 0, getWidth(), mCurrentHeader.getMeasuredHeight());
        canvas.clipRect(mClipRect);
        mCurrentHeader.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    @Override
    public boolean performItemClick(View view, int position, long id) {
        if (mLastUpEventY > 0 && mCurrentHeader != null && mLastUpEventY < mCurrentHeader.getBottom()) {
            mCurrentHeader.performClick();
            mLastUpEventY = -1;
            return true;
        }
        return super.performItemClick(view, position, id);
    }

    /**
     * Register a callback to be invoked when a touch event is sent to this view.
     *
     * @param l the touch listener to attach to this view
     */
    @Override
    public void setOnTouchListener(OnTouchListener l) {
        mForwardingTouchListener = l;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mForwardingTouchListener != null) {
            mForwardingTouchListener.onTouch(v, event);
        }

        // Save the last y up event. We use this in performItemClick to determine whether
        // to forward the event to the section header or to the list item
        if (mCurrentHeader != null && event.getY() < mCurrentHeader.getHeight()) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                mLastUpEventY = event.getY();
            }
        }

        return false;
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mOnScrollListener = l;
    }

    public void setOnItemClickListener(PinnedHeaderListView.OnItemClickListener listener) {
        super.setOnItemClickListener(listener);
    }

    public static abstract class OnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int rawPosition, long id) {
            SectionedBaseAdapter adapter;
            if (adapterView.getAdapter().getClass().equals(HeaderViewListAdapter.class)) {
                HeaderViewListAdapter wrapperAdapter = (HeaderViewListAdapter) adapterView.getAdapter();
                adapter = (SectionedBaseAdapter) wrapperAdapter.getWrappedAdapter();
            } else {
                adapter = (SectionedBaseAdapter) adapterView.getAdapter();
            }
            int section = adapter.getSectionForPosition(rawPosition);
            int position = adapter.getPositionInSectionForPosition(rawPosition);

            if (position == -1) {
                onSectionClick(adapterView, view, section, id);
            } else {
                onItemClick(adapterView, view, section, position, id);
            }
        }

        public abstract void onItemClick(AdapterView<?> adapterView, View view, int section, int position, long id);

        public abstract void onSectionClick(AdapterView<?> adapterView, View view, int section, long id);

    }
}
