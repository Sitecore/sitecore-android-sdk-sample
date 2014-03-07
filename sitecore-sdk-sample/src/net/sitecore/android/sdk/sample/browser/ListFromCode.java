package net.sitecore.android.sdk.sample.browser;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

import com.android.volley.Response.Listener;

import net.sitecore.android.sdk.api.ScApiSession;
import net.sitecore.android.sdk.api.ScApiSessionFactory;
import net.sitecore.android.sdk.api.ScRequestQueue;
import net.sitecore.android.sdk.api.internal.LogUtils;
import net.sitecore.android.sdk.sample.itemsmanager.Prefs;
import net.sitecore.android.sdk.ui.ItemsListBrowserFragment;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ListFromCode extends Activity {

    private ItemsListBrowserFragment mItemsListBrowserFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.setLogEnabled(true);

        if (savedInstanceState == null) {
            mItemsListBrowserFragment = new ItemsListBrowserFragment();
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, mItemsListBrowserFragment, "tag")
                    .commit();

            Listener<ScApiSession> onSuccess = new Listener<ScApiSession>() {
                @Override
                public void onResponse(ScApiSession scApiSession) {
                    mItemsListBrowserFragment.loadContent(scApiSession);
                }
            };

            Prefs prefs = Prefs.from(this);
            if (prefs.isAuth()) {
                ScApiSessionFactory.getSession(
                        new ScRequestQueue(getContentResolver()),
                        prefs.getUrl(), prefs.getLogin(), prefs.getPassword(), onSuccess);
            } else {
                ScApiSessionFactory.getAnonymousSession(prefs.getUrl(), onSuccess);
            }
        } else {
            mItemsListBrowserFragment = (ItemsListBrowserFragment) getFragmentManager().findFragmentByTag("tag");
        }

    }
}