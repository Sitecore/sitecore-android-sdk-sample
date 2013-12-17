package net.sitecore.android.sdk.sample.js;

import android.R;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class TestsListActivity extends ListActivity {

    private static final String[] PLUGINS = {
            "Toast",
            "Native alert",
            "Device info",
            "Events",
            "Send email",
            "Send to social network",
            "Accelerometer",
            "Get image",
            "Contacts",
            "QR Reader",
            "Google Maps",
            "Custom website"
    };

    private static final Class[] PLUGIN_TEST = new Class[]{
            ToastSampleActivity.class,
            AlertSampleActivity.class,
            DeviceSampleActivity.class,
            EventsSampleActivity.class,
            EmailSampleActivity.class,
            SocialSampleActivity.class,
            AccelerometerSampleActivity.class,
            CameraSampleActivity.class,
            ContactsSampleActivity.class,
            QrReaderSample.class,
            MapsSampleActivity.class,
            CustomWebsiteActivity.class,
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ListAdapter adapter = new ArrayAdapter<String>(this, R.layout.simple_list_item_1, PLUGINS);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        startActivity(new Intent(this, PLUGIN_TEST[position]));
    }
}
