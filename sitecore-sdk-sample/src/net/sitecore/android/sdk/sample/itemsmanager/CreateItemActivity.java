package net.sitecore.android.sdk.sample.itemsmanager;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import net.sitecore.android.sdk.api.RequestBuilder;
import net.sitecore.android.sdk.api.RequestQueueProvider;
import net.sitecore.android.sdk.api.model.ItemsResponse;
import net.sitecore.android.sdk.sample.R;

public class CreateItemActivity extends Activity implements Response.Listener<ItemsResponse>, Response.ErrorListener {

    public static final String PARENT_ID_KEY = "parent_item_id";
    private static final String DEFAULT_TEMPLATE_PATH = "Sample/Sample Item";

    private EditText mItemNameText;
    private EditText mItemDatabaseText;
    private EditText mItemParentIdText;
    private EditText mItemTemplate;

    private Button mCreateItemButton;

    private String mParentItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_item);

        mParentItemId = getIntent().getStringExtra(PARENT_ID_KEY);
        if (mParentItemId == null) {
            showMessage("No parent item specified for this activity");
            finish();
        }

        mItemNameText = (EditText) findViewById(R.id.item_name);
        mItemDatabaseText = (EditText) findViewById(R.id.item_database);
        mItemParentIdText = (EditText) findViewById(R.id.parent_item_id);
        mItemTemplate = (EditText) findViewById(R.id.item_template);

        mItemParentIdText.setText(mParentItemId);
        mItemTemplate.setText(DEFAULT_TEMPLATE_PATH);

        mCreateItemButton = (Button) findViewById(R.id.button_create_item);

        mCreateItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createItem();
            }
        });
    }

    private void createItem() {
        String itemName = mItemNameText.getText().toString();

        if (TextUtils.isEmpty(itemName)) {
            Toast.makeText(this, "You should specify item name", Toast.LENGTH_LONG).show();
            return;
        }

        RequestBuilder builder = ItemsApp.from(this).getSession().createItem(itemName,
                DEFAULT_TEMPLATE_PATH, this, this);

        if (!TextUtils.isEmpty(mItemDatabaseText.getText())) {
            builder.database(mItemDatabaseText.getText().toString());
        }
        builder.byItemId(mParentItemId);

        RequestQueueProvider.getRequestQueue(this).add(builder.build());
    }

    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        showMessage(Utils.getMessageFromError(error));
    }

    @Override
    public void onResponse(ItemsResponse response) {
        showMessage(String.format("Successfully created %d items", response.getResultCount()));
        finish();
    }
}
