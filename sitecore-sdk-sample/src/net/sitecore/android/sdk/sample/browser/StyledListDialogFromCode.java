package net.sitecore.android.sdk.sample.browser;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response.Listener;

import net.sitecore.android.sdk.api.ScApiSession;
import net.sitecore.android.sdk.api.ScApiSessionFactory;
import net.sitecore.android.sdk.api.ScRequestQueue;
import net.sitecore.android.sdk.sample.R;
import net.sitecore.android.sdk.sample.itemsmanager.Prefs;
import net.sitecore.android.sdk.ui.ItemsListBrowserFragment;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class StyledListDialogFromCode extends Activity {

    private ItemsListBrowserFragment mItemsListBrowserFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            mItemsListBrowserFragment = new ItemsListBrowserFragment() {
                @Override
                protected View onCreateFooterView(LayoutInflater inflater) {
                    View v = inflater.inflate(R.layout.browser_footer_styled, null);
                    v.findViewById(R.id.button_ok).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(StyledListDialogFromCode.this, "Selected!", Toast.LENGTH_SHORT).show();
                            //mItemsListBrowserFragment.dismiss();
                        }
                    });
                    return v;
                }
            };
            mItemsListBrowserFragment.show(getFragmentManager(), "tag");
            mItemsListBrowserFragment.setRootFolder("/sitecore");

            Listener<ScApiSession> onSuccess = new Listener<ScApiSession>() {
                @Override
                public void onResponse(ScApiSession scApiSession) {
                    mItemsListBrowserFragment.loadContent(scApiSession);
                    mItemsListBrowserFragment.getDialog().setTitle("Select item");
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