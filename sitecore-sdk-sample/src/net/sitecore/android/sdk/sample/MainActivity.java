package net.sitecore.android.sdk.sample;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import net.sitecore.android.sdk.sample.browser.BrowserSamplesChooserActivity;
import net.sitecore.android.sdk.sample.itemsmanager.ManagerActivity;
import net.sitecore.android.sdk.sample.itemsmanager.SettingsActivity;
import net.sitecore.android.sdk.sample.js.TestsListActivity;

public class MainActivity extends ListActivity {

    private static final String[] PLUGINS = {
            "Javascript sample",
            "Web API sample",
            "Items browser component"
    };

    private static final Class[] PLUGIN_TEST = new Class[]{
            TestsListActivity.class,
            ManagerActivity.class,
            BrowserSamplesChooserActivity.class
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == R.id.menu_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else {
            return super.onMenuItemSelected(featureId, item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, PLUGINS);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if (position == 2 && (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH)) {
            Toast.makeText(this, "Items browser component is supported in Android 4.0 or later", Toast.LENGTH_LONG).
                    show();
            return;
        }
        startActivity(new Intent(this, PLUGIN_TEST[position]));
    }
}
