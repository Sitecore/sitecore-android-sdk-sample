package net.sitecore.android.sdk.sample.browser;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

import com.android.volley.Response.Listener;

import net.sitecore.android.sdk.api.ScApiSession;
import net.sitecore.android.sdk.api.ScApiSessionFactory;
import net.sitecore.android.sdk.api.ScRequestQueue;
import net.sitecore.android.sdk.sample.itemsmanager.Prefs;
import net.sitecore.android.sdk.ui.ItemsBrowserFragment;
import net.sitecore.android.sdk.ui.ItemsListBrowserFragment;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ItemsBrowserDialogTestActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            final ItemsBrowserFragment itemsBrowserFragment = new ItemsListBrowserFragment();
            itemsBrowserFragment.show(getFragmentManager(), "tag");

            Listener<ScApiSession> onSuccess = new Listener<ScApiSession>() {
                @Override
                public void onResponse(ScApiSession scApiSession) {
                    itemsBrowserFragment.loadContent(scApiSession);
                    itemsBrowserFragment.getDialog().setTitle("Select item");
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

        }

    }
}