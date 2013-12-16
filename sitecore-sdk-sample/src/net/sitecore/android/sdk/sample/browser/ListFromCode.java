package net.sitecore.android.sdk.sample.browser;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

import com.android.volley.RequestQueue;

import net.sitecore.android.sdk.api.LogUtils;
import net.sitecore.android.sdk.api.RequestQueueProvider;
import net.sitecore.android.sdk.sample.itemsmanager.ItemsApp;
import net.sitecore.android.sdk.widget.ItemsBrowserFragment;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ListFromCode extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.setLogEnabled(true);

        final ItemsBrowserFragment itemsBrowserFragment;
        if (savedInstanceState == null) {
            LogUtils.LOGD("1st start");
            itemsBrowserFragment = new ItemsBrowserFragment();
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, itemsBrowserFragment, "tag")
                    .commit();

            final RequestQueue requestQueue = RequestQueueProvider.getRequestQueue(ListFromCode.this);
            itemsBrowserFragment.setApiProperties(requestQueue, ItemsApp.from(this).getSession());
        } else {
            LogUtils.LOGD("re start");
            itemsBrowserFragment = (ItemsBrowserFragment) getFragmentManager().findFragmentByTag("tag");
        }

    }
}