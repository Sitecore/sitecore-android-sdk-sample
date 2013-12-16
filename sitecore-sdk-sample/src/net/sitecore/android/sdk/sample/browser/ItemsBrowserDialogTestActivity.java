package net.sitecore.android.sdk.sample.browser;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

import com.android.volley.RequestQueue;

import net.sitecore.android.sdk.api.RequestQueueProvider;
import net.sitecore.android.sdk.sample.itemsmanager.ItemsApp;
import net.sitecore.android.sdk.widget.ItemsBrowserFragment;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ItemsBrowserDialogTestActivity extends Activity {

    private ItemsBrowserFragment itemsBrowserFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            itemsBrowserFragment = new ItemsBrowserFragment();
            itemsBrowserFragment.show(getFragmentManager(), "tag");

            final RequestQueue requestQueue = RequestQueueProvider.getRequestQueue(ItemsBrowserDialogTestActivity.this);
            itemsBrowserFragment.setApiProperties(requestQueue, ItemsApp.from(this).getSession());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        itemsBrowserFragment.getDialog().setTitle("Select item");
    }
}