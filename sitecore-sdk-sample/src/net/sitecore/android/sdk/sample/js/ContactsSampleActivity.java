package net.sitecore.android.sdk.sample.js;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import net.sitecore.android.sdk.web.LogUtils;
import net.sitecore.android.sdk.sample.R;
import net.sitecore.android.sdk.web.ScWebViewSupportFragment;

public class ContactsSampleActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogUtils.setLogEnabled(true);
        setContentView(R.layout.main_support_fragment);
        ScWebViewSupportFragment fragment = (ScWebViewSupportFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_scmobile);
        fragment.getWebView().loadUrl("file:///android_asset/www/contacts.html");
    }

}