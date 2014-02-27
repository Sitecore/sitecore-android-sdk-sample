package net.sitecore.android.sdk.sample.browser;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

import net.sitecore.android.sdk.widget.ItemsBrowserFragment;
import net.sitecore.android.sdk.widget.ItemsListBrowserFragment;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class CachedList extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            final ItemsBrowserFragment fragment = new ItemsListBrowserFragment();
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, fragment, "tag")
                    .commit();

            fragment.setLoadContentWithoutConnection(true);
        }
    }
}