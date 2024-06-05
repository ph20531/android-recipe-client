package com.cheezestudio.recipe;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.text.HtmlCompat;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;

public class JsonLogListAdapter extends BaseAdapter {
    private static final int TYPE_TITLE_TEXT = 0;
    private static final int TYPE_CONTENT_TEXT = 1;
    private Context context;
    private ArrayList<HashMap<String, String>> data;
    private LayoutInflater inflater;

    public JsonLogListAdapter(Context context, ArrayList<HashMap<String, String>> data) {
        this.context = context;
        this.data = data;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        HashMap<String, String> item = data.get(position);
        return item.containsKey("log") ? TYPE_TITLE_TEXT : TYPE_CONTENT_TEXT;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);
        if (convertView == null) {
            if (viewType == TYPE_TITLE_TEXT) {
                convertView = inflater.inflate(R.layout.list_item_title, parent, false);
            } else {
                convertView = inflater.inflate(R.layout.list_item_content, parent, false);
            }
        }

        if (viewType == TYPE_TITLE_TEXT) {
            TextView textView = convertView.findViewById(R.id.log);
            Button button = convertView.findViewById(R.id.copy_all);
            HashMap<String, String> item = data.get(position);
            textView.setText(item.get("log"));
            button.setOnClickListener(view -> {
                String plainText = "";
                if (item.containsKey("log"))
                    if (item.containsKey("response"))
                        plainText = item.getOrDefault("response", "");
                    else
                        plainText = item.getOrDefault("errors", "");

                plainText = Utils.removeHtmlTags(plainText);

                ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("clipBoard", plainText);
                clipboardManager.setPrimaryClip(clipData);

                Snackbar.make(view, R.string.clipboard, Snackbar.LENGTH_SHORT).show();
            });
        } else {
            TextView textView1 = convertView.findViewById(R.id.line_number);
            TextView textView2 = convertView.findViewById(R.id.line_data);
            HashMap<String, String> item = data.get(position);
            textView1.setText(item.get("line_number"));
            textView2.setText(HtmlCompat.fromHtml(item.get("line_data"), HtmlCompat.FROM_HTML_MODE_LEGACY));
        }

        return convertView;
    }
}