package za.co.immedia.pinnedheaderlistviewexample;

import android.app.Activity;
import android.os.Bundle;
import za.co.immedia.pinnedheaderlistview.PinnedHeaderListView;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PinnedHeaderListView listView = (PinnedHeaderListView) findViewById(R.id.pinnedListView);
        TestSectionedAdapter sectionedAdapter = new TestSectionedAdapter();
        listView.setAdapter(sectionedAdapter);
    }
}
