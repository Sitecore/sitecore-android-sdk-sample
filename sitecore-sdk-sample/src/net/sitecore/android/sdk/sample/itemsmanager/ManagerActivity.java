package net.sitecore.android.sdk.sample.itemsmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import net.sitecore.android.sdk.api.RequestBuilder;
import net.sitecore.android.sdk.api.RequestQueueProvider;
import net.sitecore.android.sdk.api.ScRequest;
import net.sitecore.android.sdk.api.model.DeleteItemsResponse;
import net.sitecore.android.sdk.api.model.ItemsResponse;
import net.sitecore.android.sdk.api.model.ScItem;
import net.sitecore.android.sdk.sample.R;

public class ManagerActivity extends FragmentActivity implements Response.ErrorListener, ItemsListFragment.OnDeleteItemListener {

    private ItemsListFragment mItemsFragment;
    private QueryFragment mQueryFragment;
    private ViewPager mPager;

    private Response.Listener<ItemsResponse> mItemsResponseListener = new Response.Listener<ItemsResponse>() {
        @Override
        public void onResponse(ItemsResponse response) {
            showMessage(String.format("%d of %d items loaded.", response.getResultCount(), response.getTotalCount()));
            mItemsFragment.setItems(response.getItems());
            mPager.setCurrentItem(1);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        findViewById(R.id.button_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendRequest();
            }
        });

        mPager = (ViewPager) findViewById(R.id.viewpager);

        mItemsFragment = new ItemsListFragment();
        mQueryFragment = new QueryFragment();
        HomePagerAdapter adapter = new HomePagerAdapter(getSupportFragmentManager(), mQueryFragment, mItemsFragment);
        mPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == R.id.menu_settings) {
            startActivity(new Intent(ManagerActivity.this, SettingsActivity.class));
            return true;
        } else {
            return super.onMenuItemSelected(featureId, item);
        }
    }

    private void onSendRequest() {
        ScRequest request = mQueryFragment.createRequest(mItemsResponseListener, this);
        if (request == null) return;

        RequestQueueProvider.getRequestQueue(this).add(request);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        showMessage(Utils.getMessageFromError(error));
    }

    private void showMessage(String message) {
        Toast.makeText(ManagerActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteItem(final ScItem item) {
        Response.Listener<DeleteItemsResponse> success = new Response.Listener<DeleteItemsResponse>() {

            @Override
            public void onResponse(DeleteItemsResponse deleteItemsResponse) {
                showMessage(deleteItemsResponse.getDeletedCount() + " item(s) deleted");
                mItemsFragment.deleteItemFromList(item);
                mPager.setCurrentItem(0);

            }
        };

        RequestBuilder builder = ItemsApp.from(this).getSession().deleteItems(success, this);
        builder.byItemId(item.getId());
        RequestQueueProvider.getRequestQueue(this).add(builder.build());
    }

}