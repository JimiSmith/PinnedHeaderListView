package za.co.immedia.pinnedheaderlistviewexample;

import za.co.immedia.pinnedheaderlistview.SectionedBaseAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TestSectionedAdapter extends SectionedBaseAdapter {

	@Override
	public Object getItem(int section, int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int section, int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSectionCount() {
		return 7;
	}

	@Override
	public int getCountForSection(int section) {
		return 15;
	}

	@Override
	public View getItemView(int section, int position, View convertView, ViewGroup parent) {
		RelativeLayout layout = null;
		if (convertView == null) {
			LayoutInflater inflator = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			layout = (RelativeLayout) inflator.inflate(R.layout.list_item, null);
		} else {
			layout = (RelativeLayout) convertView;
		}
		((TextView) layout.findViewById(R.id.textItem)).setText("Section " + section + " Item " + position);
		return layout;
	}

	@Override
	public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
		RelativeLayout layout = null;
		if (convertView == null) {
			LayoutInflater inflator = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			layout = (RelativeLayout) inflator.inflate(R.layout.list_item, parent, false);
		} else {
			layout = (RelativeLayout) convertView;
		}
		((TextView) layout.findViewById(R.id.textItem)).setText("Header for section " + section);
		return layout;
	}

	@Override
  public boolean shouldPinHeaders() {
	  return true;
  }

}
