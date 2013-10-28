package net.sitecore.android.sdk.sample.js.web;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class WebPagesListActivity extends ListActivity {

    private static final String[] PLUGINS = {
            "Toast",
            "Native alert",
            "Send email",
            "Accelerometer",
            "Maps",
            "IUFeatures tests site"
    };

    private static final Class[] PLUGIN_TEST = new Class[]{
            WebToastSampleActivity.class,
            WebAlertSampleActivity.class,
            WebEmailSampleActivity.class,
            WebAccelerometerSampleActivity.class,
            WebMapsSampleActivity.class,
            WebTestSiteSampleActivity.class
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, PLUGINS);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        startActivity(new Intent(this, PLUGIN_TEST[position]));
    }
}