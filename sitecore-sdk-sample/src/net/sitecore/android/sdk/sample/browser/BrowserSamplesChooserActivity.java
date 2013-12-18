package net.sitecore.android.sdk.sample.browser;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import net.sitecore.android.sdk.sample.R;
import net.sitecore.android.sdk.sample.itemsmanager.ManagerActivity;
import net.sitecore.android.sdk.sample.itemsmanager.SettingsActivity;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class BrowserSamplesChooserActivity extends ListActivity {

    String[] titles = {
            "Default",
            "Default in dialog",
            "Styled list",
            "Styled dialog",
            "List with cache only"
    };

    Class[] activities = new Class[]{
            ListFromCode.class,
            ItemsBrowserDialogTestActivity.class,
            StyledGridFromXml.class,
            StyledListDialogFromCode.class,
            CachedList.class
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoaderManager.enableDebugLogging(true);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1, titles);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final Intent intent = new Intent(this, activities[position]);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == R.id.menu_settings) {
            startActivity(new Intent(BrowserSamplesChooserActivity.this, SettingsActivity.class));
            return true;
        } else {
            return super.onMenuItemSelected(featureId, item);
        }
    }
}