package net.sitecore.android.sdk.sample.itemsmanager;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.android.volley.toolbox.ImageLoader;

import net.sitecore.android.sdk.api.RequestQueueProvider;
import net.sitecore.android.sdk.api.ScApiSession;
import net.sitecore.android.sdk.api.model.CheckBoxField;
import net.sitecore.android.sdk.api.model.ImageField;
import net.sitecore.android.sdk.api.model.RichTextField;
import net.sitecore.android.sdk.api.model.ScBaselistField;
import net.sitecore.android.sdk.api.model.ScDateField;
import net.sitecore.android.sdk.api.model.ScField;
import net.sitecore.android.sdk.api.model.ScItem;
import net.sitecore.android.sdk.sample.R;

public class ItemActivity extends ListActivity {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static final String DATA_KEY = "item";

    private ImageLoader mImageLoader;
    private ScItem mItem;
    private ScApiSession mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mItem = getIntent().getParcelableExtra(DATA_KEY);
        if (mItem == null) {
            Toast.makeText(this, "No item specified for this activity", Toast.LENGTH_SHORT).show();
            finish();
        }

        mSession = ItemsApp.from(getApplicationContext()).getSession();
        if (mSession == null) {
            Toast.makeText(this, "No Session available", Toast.LENGTH_SHORT).show();
            finish();
        }

        mImageLoader = new ImageLoader(RequestQueueProvider.getRequestQueue(this), new BitmapLruCache());
        initViews();
    }

    private void initViews() {
        View header = getLayoutInflater().inflate(R.layout.item_header, null);

        TextView itemNameText = (TextView) header.findViewById(R.id.item_display_name);
        EditText itemVersionText = (EditText) header.findViewById(R.id.item_version);
        CheckBox itemHasChildrenCheckBox = (CheckBox) header.findViewById(R.id.item_has_children);
        EditText itemLanguageText = (EditText) header.findViewById(R.id.item_language);
        EditText itemIdText = (EditText) header.findViewById(R.id.item_id);
        EditText itemLongIdText = (EditText) header.findViewById(R.id.item_long_id);
        EditText itemPathText = (EditText) header.findViewById(R.id.item_path);
        EditText itemTemplateText = (EditText) header.findViewById(R.id.item_template);
        EditText itemDatabaseText = (EditText) header.findViewById(R.id.item_database);

        itemNameText.setText(mItem.getDisplayName());
        itemVersionText.setText(String.valueOf(mItem.getVersion()));
        itemHasChildrenCheckBox.setChecked(mItem.hasChildren());
        itemLanguageText.setText(mItem.getLanguage());
        itemIdText.setText(mItem.getId());
        itemLongIdText.setText(mItem.getLongId());
        itemPathText.setText(mItem.getPath());
        itemTemplateText.setText(mItem.getTemplate());
        itemDatabaseText.setText(mItem.getDatabase());

        getListView().addHeaderView(header);

        FieldsAdapter adapter = new FieldsAdapter(this, mItem.getFields());
        getListView().setAdapter(adapter);
    }

    private void showWebviewDialog(String text) {
        WebView webView = new WebView(this);
        webView.loadData(text, "text/html", "UTF-8");

        new AlertDialog.Builder(this)
                .setView(webView)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    class FieldsAdapter extends ArrayAdapter<ScField> {

        public FieldsAdapter(Context context, List<ScField> objects) {
            super(context, R.layout.item_field_layout, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_field_layout, null);
                ViewHolder viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }

            ScField field = getItem(position);
            ViewHolder holder = (ViewHolder) convertView.getTag();

            holder.name.setText(field.getName());
            holder.id.setText(field.getId());
            holder.type.setText(field.getType().toString());
            holder.rawValue.setText(field.getRawValue());

            if (holder.container.getChildCount() > 6) {
                holder.container.removeViewAt(holder.container.getChildCount() - 1);
            }
            holder.container.addView(loadFieldView(field));

            return convertView;
        }

        private View loadFieldView(final ScField field) {
            if (field instanceof ScBaselistField) {
                Spinner spinner = new Spinner(getContext());
                ArrayList<String> itemsIds = ((ScBaselistField) field).getItemsIds();
                spinner.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, itemsIds));
                return spinner;
            } else if (field instanceof CheckBoxField) {
                CheckBox checkBox = new CheckBox(getContext());
                checkBox.setChecked(((CheckBoxField) field).isChecked());
                return checkBox;
            } else if (field instanceof ScDateField) {
                EditText text = new EditText(getContext());
                DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
                String date = DATE_FORMAT.format(new Date(((ScDateField) field).getDate()));
                text.setText(date);
                return text;
            } else if (field instanceof ImageField) {
                ImageView imageView = new ImageView(getContext());
                String imageUrl = mSession.getBaseUrl() + ((ImageField) field).getImageSrcUrl();
                mImageLoader.get(imageUrl, ImageLoader.getImageListener(imageView, 0, 0));
                return imageView;
            } else if (field instanceof RichTextField) {
                Button showButton = new Button(getContext());
                showButton.setText("show in webview");

                final String text = ((RichTextField) field).getHtmlText(mSession.getBaseUrl());
                if (TextUtils.isEmpty(text)) {
                    showButton.setEnabled(false);
                } else {
                    showButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showWebviewDialog(text);
                        }
                    });
                }
                return showButton;
            } else {
                EditText text = new EditText(getContext());
                text.setText(field.getRawValue());
                return text;
            }
        }

        class ViewHolder {
            public TextView name;
            public EditText type;
            public EditText id;
            public EditText rawValue;
            public LinearLayout container;

            ViewHolder(View root) {
                name = (TextView) root.findViewById(R.id.field_name);
                type = (EditText) root.findViewById(R.id.field_type);
                rawValue = (EditText) root.findViewById(R.id.field_raw_value);
                id = (EditText) root.findViewById(R.id.field_id);
                container = (LinearLayout) root.findViewById(R.id.container);
            }
        }
    }

}
