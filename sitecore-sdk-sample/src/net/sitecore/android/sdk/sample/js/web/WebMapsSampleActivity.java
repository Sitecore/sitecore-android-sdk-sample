package net.sitecore.android.sdk.sample.js.web;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import net.sitecore.android.sdk.sample.R;
import net.sitecore.android.sdk.web.ScWebViewSupportFragment;

public class WebMapsSampleActivity extends FragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_support_fragment);

        ScWebViewSupportFragment fragment = (ScWebViewSupportFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_scmobile);

        fragment.getWebView().loadUrl("http://ws-alr1.dk.sitecore.net:8096/en/Company/Contact_us.aspx");
    }
}