package net.sitecore.android.sdk.sample.itemsmanager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;

import net.sitecore.android.sdk.sample.R;
import net.sitecore.android.sdk.api.RequestBuilder;
import net.sitecore.android.sdk.api.ScRequest;
import net.sitecore.android.sdk.api.model.ItemsResponse;
import net.sitecore.android.sdk.api.model.RequestScope;

public class QueryFragment extends Fragment {

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_query, container, false);

        mItemPathText = (EditText) rootView.findViewById(R.id.item_path);
        mItemIDText = (EditText) rootView.findViewById(R.id.item_id);
        mItemVersionText = (EditText) rootView.findViewById(R.id.item_version);
        mDatabase = (EditText) rootView.findViewById(R.id.item_database);
        mLanguage = (EditText) rootView.findViewById(R.id.item_language);
        mQuery = (EditText) rootView.findViewById(R.id.query);

        mSelf = (CheckBox) rootView.findViewById(R.id.check_scope_self);
        mParent = (CheckBox) rootView.findViewById(R.id.check_scope_parent);
        mChildren = (CheckBox) rootView.findViewById(R.id.check_scope_children);

        mPage = (EditText) rootView.findViewById(R.id.page);
        mPageSize = (EditText) rootView.findViewById(R.id.page_size);

        return rootView;
    }

    public ScRequest<?> createRequest(Response.Listener<ItemsResponse> successListener, Response.ErrorListener errorListener) {
        RequestBuilder builder = ItemsApp.from(getActivity()).getSession().getItems(successListener, errorListener);

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
                Toast.makeText(getActivity(), "Path should start with '/'", Toast.LENGTH_LONG).show();
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
                Toast.makeText(getActivity(), "You have to specify both page and page size", Toast.LENGTH_LONG).show();
                return null;
            }
        }

        return builder.build();
    }
}
