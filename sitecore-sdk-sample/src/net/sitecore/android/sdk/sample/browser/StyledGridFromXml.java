package net.sitecore.android.sdk.sample.browser;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;

import net.sitecore.android.sdk.api.LogUtils;
import net.sitecore.android.sdk.api.RequestQueueProvider;
import net.sitecore.android.sdk.api.model.ItemsResponse;
import net.sitecore.android.sdk.api.model.ScItem;
import net.sitecore.android.sdk.sample.R;
import net.sitecore.android.sdk.sample.itemsmanager.ItemsApp;
import net.sitecore.android.sdk.widget.ItemViewBinder;
import net.sitecore.android.sdk.widget.ItemsBrowserFragment;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class StyledGridFromXml extends Activity implements ItemsBrowserFragment.ContentTreePositionListener,
        ItemsBrowserFragment.NetworkEventsListener {

    TextView mTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.browser_styled);
        LogUtils.setLogEnabled(true);

        mTitle = (TextView) findViewById(R.id.text_title);

        final StyledFragment fragment = (StyledFragment) getFragmentManager().findFragmentById(R.id.fragment_styled);
        fragment.setContentTreePositionListener(this);
        fragment.setNetworkEventsListener(this);

        final RequestQueue requestQueue = RequestQueueProvider.getRequestQueue(StyledGridFromXml.this);
        fragment.setApiProperties(requestQueue, ItemsApp.from(this).getSession());
        fragment.setNetworkEventsListener(new SimpleNetworkListenerImpl(getApplicationContext()));
    }

    @Override
    public void onGoUp(ScItem item) {
        mTitle.setText(item.getPath());
    }

    @Override
    public void onGoInside(ScItem item) {
        mTitle.setText(item.getPath());
    }

    @Override
    public void onInitialized(ScItem item) {
        mTitle.setText(item.getPath());
    }

    @Override
    public void onUpdateRequestStarted() {
        setProgressBarIndeterminateVisibility(true);
    }

    @Override
    public void onUpdateSuccess(ItemsResponse itemsResponse) {
        setProgressBarIndeterminateVisibility(false);
    }

    @Override
    public void onUpdateError(VolleyError error) {
        setProgressBarIndeterminateVisibility(false);
    }

    public static class StyledFragment extends ItemsBrowserFragment {

        @Override
        public void onScItemClick(ScItem item) {
            if (item.hasChildren()) {
                super.onScItemClick(item);
            } else {
                Toast.makeText(getActivity(), item.getDisplayName() + " has no children", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onScItemLongClick(ScItem item) {
            Toast.makeText(getActivity(), "long click: " + item.getDisplayName(), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected View onCreateFooterView(LayoutInflater inflater) {
            return inflater.inflate(R.layout.browser_footer_styled, null);
        }

        @Override
        protected View onCreateUpButtonView(LayoutInflater inflater) {
            return inflater.inflate(R.layout.browser_up_styled, null);
        }

        @Override
        protected ItemViewBinder onCreateItemViewBinder() {
            return new StyledItemViewBinder();
        }

        private class StyledItemViewBinder implements ItemViewBinder {
            @Override
            public void bindView(Context context, View v, ScItem item) {
                ViewHolder holder = (ViewHolder) v.getTag();

                final String title = item.getDisplayName() + " (" + item.getFields().size() + " fields)";
                holder.text1.setText(title);
                holder.text2.setText(item.getTemplate());

                int textColor = item.hasChildren()
                        ? getResources().getColor(android.R.color.holo_green_dark)
                        : getResources().getColor(android.R.color.holo_red_dark);
                holder.text2.setTextColor(textColor);
            }

            @Override
            public View newView(Context context, ViewGroup parent, LayoutInflater inflater, ScItem item) {
                final View v = inflater.inflate(R.layout.browser_item_styled, parent, false);
                final ViewHolder holder = new ViewHolder();
                holder.text1 = (TextView) v.findViewById(android.R.id.text1);
                holder.text2 = (TextView) v.findViewById(android.R.id.text2);
                v.setTag(holder);

                return v;
            }
        }

        private static class ViewHolder {
            TextView text1;
            TextView text2;
        }
    }

}