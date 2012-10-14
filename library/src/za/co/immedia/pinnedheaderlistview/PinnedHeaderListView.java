package za.co.immedia.pinnedheaderlistview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class PinnedHeaderListView extends ListView implements OnScrollListener {

	public static interface PinnedSectionedHeaderAdapter {
		public boolean isSectionHeader(int position);

		public int getSectionForPosition(int position);

		public View getSectionHeaderView(int section, View convertView, ViewGroup parent);

		public int getCount();

	}

	private PinnedSectionedHeaderAdapter mAdapter;
	private View mCurrentHeader;
	private float mHeaderOffset;
	private boolean mShouldPin = true;
	private int mCurrentSection = 0;

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
		if (mAdapter == null || mAdapter.getCount() == 0 || !mShouldPin)
			return;

		int section = mAdapter.getSectionForPosition(firstVisibleItem);
		mCurrentHeader = getHeaderView(section, mCurrentHeader);
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
	}

	private View getHeaderView(int section, View oldView) {
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
			int widthSpec = MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY);
			int heightSpec;
			ViewGroup.LayoutParams layoutParams = header.getLayoutParams();
			if (layoutParams != null && layoutParams.height > 0) {
				heightSpec = MeasureSpec.makeMeasureSpec(layoutParams.height, MeasureSpec.EXACTLY);
			} else {
				heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
			}
			header.measure(widthSpec, heightSpec);
			int height = header.getMeasuredHeight();
			header.layout(0, 0, getWidth(), height);
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (mAdapter == null || !mShouldPin || mCurrentHeader == null)
			return;
		int saveCount = canvas.save();
		canvas.translate(0, mHeaderOffset);
		canvas.clipRect(0, 0, getWidth(), mCurrentHeader.getMeasuredHeight()); // needed for < HONEYCOMB
		mCurrentHeader.draw(canvas);
		canvas.restoreToCount(saveCount);
	}

	public void setOnItemClickListener(PinnedHeaderListView.OnItemClickListener listener) {
		super.setOnItemClickListener(listener);
	}

	public static abstract class OnItemClickListener implements AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			SectionedBaseAdapter adaper = (SectionedBaseAdapter) arg0.getAdapter();
			int section = adaper.getSectionForPosition(arg2);
			int position = adaper.getPositionInSectionForPosition(arg2);
			onItemClick(adaper, arg0, section, position, arg3);
		}

		public abstract void onItemClick(SectionedBaseAdapter adapter, View view, int section, int position, long id);

	}
}
