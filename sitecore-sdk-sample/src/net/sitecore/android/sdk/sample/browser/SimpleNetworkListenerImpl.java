package net.sitecore.android.sdk.sample.browser;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.VolleyError;

import net.sitecore.android.sdk.api.model.ItemsResponse;
import net.sitecore.android.sdk.sample.itemsmanager.Utils;
import net.sitecore.android.sdk.widget.ItemsBrowserFragment;

class SimpleNetworkListenerImpl implements ItemsBrowserFragment.NetworkEventsListener {
    private Context mContext;

    SimpleNetworkListenerImpl(Context context) {
        mContext = context;
    }

    @Override
    public void onUpdateRequestStarted() {

    }

    @Override
    public void onUpdateSuccess(ItemsResponse itemsResponse) {

    }

    @Override
    public void onUpdateError(VolleyError error) {
        Toast.makeText(mContext, Utils.getMessageFromError(error), Toast.LENGTH_LONG).show();
    }
}
