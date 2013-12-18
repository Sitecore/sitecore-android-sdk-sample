package net.sitecore.android.sdk.sample.js;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;

import net.sitecore.android.sdk.sample.R;
import net.sitecore.android.sdk.web.ScWebViewSupportFragment;

public class CustomWebsiteActivity extends FragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_custom_website);
        final ScWebViewSupportFragment fragment = (ScWebViewSupportFragment) getSupportFragmentManager().
                findFragmentById(R.id.fragment_scmobile);

        findViewById(R.id.button_go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText urlField = (EditText) findViewById(R.id.field_url);
                fragment.getWebView().loadUrl(urlField.getText().toString());
            }
        });
    }
}
