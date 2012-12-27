package za.co.immedia.pinnedheaderlistview;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import za.co.immedia.pinnedheaderlistview.PinnedHeaderListView.PinnedSectionedHeaderAdapter;

public abstract class SectionedBaseAdapter extends BaseAdapter implements PinnedSectionedHeaderAdapter {

	private static int HEADER_VIEW_TYPE = 0;
	private static int ITEM_VIEW_TYPE = 0;

	/**
	 * Holds the calculated values of @{link getPositionInSectionForPosition}
	 */
	private SparseArray<Integer> mSectionPositionCache;
	/**
	 * Holds the calculated values of @{link getSectionForPosition}
	 */
	private SparseArray<Integer> mSectionCache;

	public SectionedBaseAdapter() {
		super();
		mSectionCache = new SparseArray<Integer>();
		mSectionPositionCache = new SparseArray<Integer>();
	}

	@Override
	public void notifyDataSetChanged() {
		mSectionCache.clear();
		mSectionPositionCache.clear();
		super.notifyDataSetChanged();
	}

	@Override
	public void notifyDataSetInvalidated() {
		mSectionCache.clear();
		mSectionPositionCache.clear();
		super.notifyDataSetInvalidated();
	}

	@Override
	public final int getCount() { // TODO cache value
		int count = 0;
		for (int i = 0; i < getSectionCount(); i++) {
			count += getCountForSection(i);
			count++; // for the header view
		}
		return count;
	}

	@Override
	public final Object getItem(int position) {
		return getItem(getSectionForPosition(position), getPositionInSectionForPosition(position));
	}

	@Override
	public final long getItemId(int position) {
		return getItemId(getSectionForPosition(position), getPositionInSectionForPosition(position));
	}

	@Override
	public final View getView(int position, View convertView, ViewGroup parent) {
		if (isSectionHeader(position)) {
			return getSectionHeaderView(getSectionForPosition(position), convertView, parent);
		}
		return getItemView(getSectionForPosition(position), getPositionInSectionForPosition(position), convertView, parent);
	}

	@Override
	public final int getItemViewType(int position) {
		if (isSectionHeader(position)) {
			return getItemViewTypeCount() + getSectionHeaderViewType(getSectionForPosition(position));
		}
		return getItemViewType(getSectionForPosition(position), getPositionInSectionForPosition(position));
	}

	@Override
	public final int getViewTypeCount() {
		return getItemViewTypeCount() + getSectionHeaderViewTypeCount();
	}

	public final int getSectionForPosition(int position) {
		// first try to retrieve values from cache
		Integer cachedSection = mSectionCache.get(position);
		if (cachedSection != null) {
			return cachedSection;
		}
		int sectionStart = 0;
		for (int i = 0; i < getSectionCount(); i++) {
			int sectionCount = getCountForSection(i);
			int sectionEnd = sectionStart + sectionCount + 1;
			if (position >= sectionStart && position < sectionEnd) {
				mSectionCache.put(position, i);
				return i;
			}
			sectionStart = sectionEnd;
		}
		return 0;
	}

	public int getPositionInSectionForPosition(int position) {
		// first try to retrieve values from cache
		Integer cachedPosition = mSectionPositionCache.get(position);
		if (cachedPosition != null) {
			return cachedPosition;
		}
		int sectionStart = 0;
		for (int i = 0; i < getSectionCount(); i++) {
			int sectionCount = getCountForSection(i);
			int sectionEnd = sectionStart + sectionCount + 1;
			if (position >= sectionStart && position < sectionEnd) {
				int positionInSection = position - sectionStart - 1;
				mSectionPositionCache.put(position, positionInSection);
				return positionInSection;
			}
			sectionStart = sectionEnd;
		}
		return 0;
	}

	public final boolean isSectionHeader(int position) {
		int sectionStart = 0;
		for (int i = 0; i < getSectionCount(); i++) {
			if (position == sectionStart) {
				return true;
			} else if (position < sectionStart) {
				return false;
			}
			sectionStart += getCountForSection(i) + 1;
		}
		return false;
	}

	public int getItemViewType(int section, int position) {
		return ITEM_VIEW_TYPE;
	}

	public int getItemViewTypeCount() {
		return 1;
	}

	public int getSectionHeaderViewType(int section) {
		return HEADER_VIEW_TYPE;
	}

	public int getSectionHeaderViewTypeCount() {
		return 1;
	}

	public abstract Object getItem(int section, int position);

	public abstract long getItemId(int section, int position);

	public abstract int getSectionCount();

	public abstract int getCountForSection(int section);

	public abstract View getItemView(int section, int position, View convertView, ViewGroup parent);

	public abstract View getSectionHeaderView(int section, View convertView, ViewGroup parent);

}
