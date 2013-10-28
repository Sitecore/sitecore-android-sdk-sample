package net.sitecore.android.sdk.sample.itemsmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import net.sitecore.android.sdk.api.model.ScItem;

public class ItemsListFragment extends ListFragment implements AdapterView.OnItemLongClickListener {

    interface OnDeleteItemListener {
        public void onDeleteItem(ScItem item);
    }

    private OnDeleteItemListener mDeleteItemListener;
    private ItemsListAdapter mAdapter;

    private String[] mMenuTexts = new String[]{
            "Delete",
            "Create child item"
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mDeleteItemListener = (OnDeleteItemListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnDeleteItemListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText("No items");

        mAdapter = new ItemsListAdapter(getActivity(), new ArrayList<ScItem>());
        setListAdapter(mAdapter);
        getListView().setOnItemLongClickListener(this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(getActivity(), ItemActivity.class);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setItems(mMenuTexts, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showDeleteDialog(item);
                } else {
                    Intent intent = new Intent(getActivity(), CreateItemActivity.class);
                    intent.putExtra(CreateItemActivity.PARENT_ID_KEY, item.getId());
                    startActivity(intent);
                }
            }
        });
        builder.create().show();
    }

    public void setItems(List<ScItem> items) {
        mAdapter.clear();
        for (ScItem item : items) {
            mAdapter.add(item);
        }
    }

    public void deleteItemFromList(ScItem item) {
        mAdapter.remove(item);
        mAdapter.notifyDataSetChanged();
    }

    private void showDeleteDialog(final ScItem item) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Delete item")
                .setMessage(String.format("Are use sure you want to delete %s item?", item.getDisplayName()))
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mDeleteItemListener.onDeleteItem(item);
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

}
