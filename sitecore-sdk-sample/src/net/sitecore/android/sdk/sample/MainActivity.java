package net.sitecore.android.sdk.sample;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import net.sitecore.android.sdk.sample.itemsmanager.ManagerActivity;
import net.sitecore.android.sdk.sample.js.TestsListActivity;

public class MainActivity extends ListActivity {

    private static final String[] PLUGINS = {
            "Javascript sample",
            "Web API sample"
    };

    private static final Class[] PLUGIN_TEST = new Class[]{
            TestsListActivity.class,
            ManagerActivity.class
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
