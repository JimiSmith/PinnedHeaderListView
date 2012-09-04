package za.co.immedia.pinnedheaderlistview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;

public class PinnedHeaderListView extends ListView implements OnScrollListener {

	public static interface PinnedSectionedHeaderAdapter {
		public boolean isSectionHeader(int position);

		public int getSectionForPosition(int position);

		public View getSectionHeaderView(int section, View convertView, ViewGroup parent);
	}

	private PinnedSectionedHeaderAdapter mAdapter;
	private View mCurrentHeader;
	private float mHeaderOffset;
	private boolean mShouldPin = true;

	public PinnedHeaderListView(Context context) {
		super(context);
		super.setOnScrollListener(this);
	}

	public PinnedHeaderListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		super.setOnScrollListener(this);
	}

	public PinnedHeaderListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		super.setOnScrollListener(this);
	}

	public void setPinHeaders(boolean shouldPin) {
		mShouldPin = shouldPin;
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		mAdapter = (PinnedSectionedHeaderAdapter) adapter;
		super.setAdapter(adapter);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (mAdapter == null || !mShouldPin) return;
		int section = mAdapter.getSectionForPosition(firstVisibleItem);
		mCurrentHeader = mAdapter.getSectionHeaderView(section, null, this);
		if (mAdapter.isSectionHeader(firstVisibleItem + 1)) {
			mHeaderOffset = getChildAt(1).getTop() - getChildAt(1).getMeasuredHeight();
		} else {
			mHeaderOffset = 0;
		}
		ensurePinnedHeaderLayout();
		invalidate();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	private void ensurePinnedHeaderLayout() {
		if (mCurrentHeader.isLayoutRequested()) {
			int widthSpec = MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY);
			int heightSpec;
			ViewGroup.LayoutParams layoutParams = mCurrentHeader.getLayoutParams();
			if (layoutParams != null && layoutParams.height > 0) {
				heightSpec = MeasureSpec.makeMeasureSpec(layoutParams.height, MeasureSpec.EXACTLY);
			} else {
				heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
			}
			mCurrentHeader.measure(widthSpec, heightSpec);
			int height = mCurrentHeader.getMeasuredHeight();
			mCurrentHeader.layout(0, 0, getWidth(), height);
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (mAdapter == null || !mShouldPin) return;
		int saveCount = canvas.save();
		canvas.translate(0, mHeaderOffset);
		mCurrentHeader.draw(canvas);
		canvas.restoreToCount(saveCount);
	}
}
