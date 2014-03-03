package net.sitecore.android.sdk.sample.itemsmanager;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;

import net.sitecore.android.sdk.api.RequestBuilder;
import net.sitecore.android.sdk.api.RequestQueueProvider;
import net.sitecore.android.sdk.api.model.DeleteItemsResponse;
import net.sitecore.android.sdk.api.model.ScItem;

public class ItemsListActivity extends ListActivity implements AdapterView.OnItemLongClickListener {
    public static final String DATA_KEY = "items_data";

    private ItemsListAdapter mAdapter;

    private String[] mMenuTexts = new String[]{
            "Delete",
            "Create child item"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayList<ScItem> items = getIntent().getParcelableArrayListExtra(DATA_KEY);
        if (items == null) {
            items = new ArrayList<ScItem>();
        }

        mAdapter = new ItemsListAdapter(this, items);
        setListAdapter(mAdapter);

        getListView().setOnItemLongClickListener(this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(this, ItemActivity.class);
        intent.putExtra(ItemActivity.DATA_KEY, mAdapter.getItem(position));
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        showActionsDialog(position);
        return false;
    }


    private void showActionsDialog(int position) {
        final ScItem item = mAdapter.getItem(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setItems(mMenuTexts, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showDeleteDialog(item);
                } else {
                    Intent intent = new Intent(ItemsListActivity.this, CreateItemActivity.class);
                    intent.putExtra(CreateItemActivity.PARENT_ID_KEY, item.getId());
                    startActivity(intent);
                }
            }
        });
        builder.create().show();
    }

    private void showDeleteDialog(final ScItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete item")
                .setMessage(String.format("Are use sure you want to delete %s item?", item.getDisplayName()))
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteItem(item);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    private static class ItemsListAdapter extends ArrayAdapter<ScItem> {

        public ItemsListAdapter(Context context, List<ScItem> items) {
            super(context, android.R.layout.simple_list_item_2, items);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, null, false);
            }

            TextView name = (TextView) convertView.findViewById(android.R.id.text1);
            TextView path = (TextView) convertView.findViewById(android.R.id.text2);

            name.setText(getItem(position).getDisplayName());
            path.setText(getItem(position).getPath());

            return convertView;
        }
    }

    public void deleteItem(ScItem item) {
        Response.Listener<DeleteItemsResponse> success = new Response.Listener<DeleteItemsResponse>() {

            @Override
            public void onResponse(DeleteItemsResponse deleteItemsResponse) {
                Toast.makeText(ItemsListActivity.this,
                        deleteItemsResponse.getDeletedCount() + " item(s) deleted", Toast.LENGTH_SHORT)
                        .show();
                finish();
            }
        };

        ErrorListener onError = new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ItemsListActivity.this,
                        Utils.getMessageFromError(error), Toast.LENGTH_SHORT)
                        .show();
            }
        };

        RequestBuilder builder = ItemsApp.from(this).getSession().deleteItemsRequest(success, onError);
        builder.byItemId(item.getId());
        RequestQueueProvider.getRequestQueue(this).add(builder.build());
    }
}
