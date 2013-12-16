package net.sitecore.android.sdk.sample.browser;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import net.sitecore.android.sdk.api.RequestQueueProvider;
import net.sitecore.android.sdk.sample.R;
import net.sitecore.android.sdk.sample.itemsmanager.ItemsApp;
import net.sitecore.android.sdk.widget.ItemsBrowserFragment;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class StyledListDialogFromCode extends Activity {

    private ItemsBrowserFragment itemsBrowserFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            itemsBrowserFragment = new ItemsBrowserFragment() {
                @Override
                protected View onCreateFooterView(LayoutInflater inflater) {
                    View v = inflater.inflate(R.layout.browser_footer_styled, null);
                    v.findViewById(R.id.button_ok).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(StyledListDialogFromCode.this, "Selected!", Toast.LENGTH_SHORT).show();
                            //itemsBrowserFragment.dismiss();
                        }
                    });
                    return v;
                }
            };
            itemsBrowserFragment.show(getFragmentManager(), "tag");
            itemsBrowserFragment.setRootFolder("/sitecore");

            final RequestQueue requestQueue = RequestQueueProvider.getRequestQueue(StyledListDialogFromCode.this);
            itemsBrowserFragment.setApiProperties(requestQueue, ItemsApp.from(this).getSession());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        itemsBrowserFragment.getDialog().setTitle("Select item");
    }
}