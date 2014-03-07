package net.sitecore.android.sdk.sample.itemsmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import net.sitecore.android.sdk.api.RequestBuilder;
import net.sitecore.android.sdk.api.ScRequest;
import net.sitecore.android.sdk.api.ScRequestQueue;
import net.sitecore.android.sdk.api.model.ItemsResponse;
import net.sitecore.android.sdk.api.model.RequestScope;
import net.sitecore.android.sdk.sample.R;

public class ManagerActivity extends Activity implements Response.ErrorListener {
    private EditText mItemPathText;
    private EditText mItemIDText;
    private EditText mItemVersionText;
    private EditText mDatabase;
    private EditText mLanguage;
    private EditText mQuery;
    private EditText mPage;
    private EditText mPageSize;

    private CheckBox mSelf;
    private CheckBox mParent;
    private CheckBox mChildren;

    private Response.Listener<ItemsResponse> mItemsResponseListener = new Response.Listener<ItemsResponse>() {
        @Override
        public void onResponse(ItemsResponse response) {
            String message = String.format("%d of %d items loaded.", response.getResultCount(),
                    response.getTotalCount());

            Toast.makeText(ManagerActivity.this, message, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(ManagerActivity.this, ItemsListActivity.class);
            intent.putParcelableArrayListExtra(ItemsListActivity.DATA_KEY,
                    (ArrayList<? extends Parcelable>) response.getItems());
            startActivity(intent);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manager_activity_main);

        findViewById(R.id.button_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendRequest();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        mItemPathText = (EditText) findViewById(R.id.item_path);
        mItemIDText = (EditText) findViewById(R.id.item_id);
        mItemVersionText = (EditText) findViewById(R.id.item_version);
        mDatabase = (EditText) findViewById(R.id.item_database);
        mLanguage = (EditText) findViewById(R.id.item_language);
        mQuery = (EditText) findViewById(R.id.query);

        mSelf = (CheckBox) findViewById(R.id.check_scope_self);
        mParent = (CheckBox) findViewById(R.id.check_scope_parent);
        mChildren = (CheckBox) findViewById(R.id.check_scope_children);

        mPage = (EditText) findViewById(R.id.page);
        mPageSize = (EditText) findViewById(R.id.page_size);
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
        ScRequest request = createRequest(mItemsResponseListener, this);
        if (request == null) return;

        new ScRequestQueue(getContentResolver()).add(request);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(ManagerActivity.this, Utils.getMessageFromError(error), Toast.LENGTH_SHORT).show();
    }

    public ScRequest<?> createRequest(Response.Listener<ItemsResponse> successListener,
            Response.ErrorListener errorListener) {
        RequestBuilder builder = ItemsApp.from(this).getSession().
                readItemsRequest(successListener, errorListener);

        if (!TextUtils.isEmpty(mItemIDText.getText())) {
            builder.byItemId(mItemIDText.getText().toString());
        }

        if (!TextUtils.isEmpty(mItemVersionText.getText())) {
            int version = Integer.parseInt(mItemVersionText.getText().toString());
            builder.itemVersion(version);
        }

        if (!TextUtils.isEmpty(mItemPathText.getText())) {
            String path = mItemPathText.getText().toString();
            if (!path.startsWith("/")) {
                Toast.makeText(this, "Path should start with '/'", Toast.LENGTH_LONG).show();
                return null;
            }
            builder.byItemPath(path);
        }

        if (!TextUtils.isEmpty(mDatabase.getText())) {
            builder.database(mDatabase.getText().toString());
        }

        if (!TextUtils.isEmpty(mLanguage.getText())) {
            builder.setLanguage(mLanguage.getText().toString());
        }

        if (!TextUtils.isEmpty(mQuery.getText())) {
            builder.bySitecoreQuery(mQuery.getText().toString());
        }

        if (mSelf.isChecked()) {
            builder.withScope(RequestScope.SELF);
        }
        if (mParent.isChecked()) {
            builder.withScope(RequestScope.PARENT);
        }
        if (mChildren.isChecked()) {
            builder.withScope(RequestScope.CHILDREN);
        }

        boolean isPageSet = !TextUtils.isEmpty(mPage.getText());
        boolean isPageSizeSet = !TextUtils.isEmpty(mPageSize.getText());

        if (isPageSet && isPageSizeSet) {
            int page = Integer.parseInt(mPage.getText().toString());
            int pageSize = Integer.parseInt(mPageSize.getText().toString());
            builder.setPage(page, pageSize);
        } else {
            if (isPageSet || isPageSizeSet) {
                Toast.makeText(this, "You have to specify both page and page size", Toast.LENGTH_LONG).show();
                return null;
            }
        }

        return builder.build();
    }

}