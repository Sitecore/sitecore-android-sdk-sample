package net.sitecore.android.sdk.sample.js.web;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import net.sitecore.android.sdk.sample.R;
import net.sitecore.android.sdk.web.ScWebViewSupportFragment;

public class WebAlertSampleActivity extends FragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_support_fragment);
        ScWebViewSupportFragment fragment = (ScWebViewSupportFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_scmobile);

        fragment.getWebView().loadUrl("https://googledrive.com/host/0B2zVYPZ3fO8kTkVQc0JzM21RSkE/alert.html");
    }
}