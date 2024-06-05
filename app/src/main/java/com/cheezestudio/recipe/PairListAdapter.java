package com.cheezestudio.recipe;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

class PairListAdapter extends RecyclerView.Adapter<PairListAdapter.ViewHolder> {
    private Context context;

    private int type;

    private ArrayList<Pair<String, String>> data;

    private OnItemClickListener onItemClickListener;

    public PairListAdapter(Context context, int type, ArrayList<Pair<String, String>> data) {
        this.context = context;
        this.type = type;
        this.data = data;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_pair, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pair<String, String> item = data.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView keyTextView;
        private TextView valueTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            keyTextView = itemView.findViewById(R.id.key);
            valueTextView = itemView.findViewById(R.id.value);
            itemView.setOnClickListener(this);

            ImageButton more = itemView.findViewById(R.id.more);
            more.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(context, more);
                popupMenu.inflate(R.menu.popup_menu);
                popupMenu.setOnMenuItemClickListener(item -> {
                    int position = getAdapterPosition();

                    if (item.getItemId() == R.id.action_delete) {
                        String sType = context.getString(type);
                        String title = sType + " " + context.getString(R.string.delete);
                        int positive = R.string.delete;
                        int negative = R.string.cancel;
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(title);
                        builder.setMessage(String.format(context.getString(R.string.pair_remove_confirm_message), sType));
                        builder.setPositiveButton(positive, (dialog, which) -> {
                            data.remove(position);
                            notifyItemRemoved(position);
                            Snackbar.make(v, String.format(context.getString(R.string.pair_remove_message), sType), Snackbar.LENGTH_SHORT).show();
                        });
                        builder.setNegativeButton(negative, (dialog, which) -> {
                            dialog.dismiss();
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                    if (item.getItemId() == R.id.action_copy) {
                        Pair<String, String> dataItem = data.get(position);

                        String key = context.getString(R.string.key) + "\n" + dataItem.first;
                        String value = context.getString(R.string.value) + "\n" + dataItem.second;

                        String plainText = key + "\n\n" + value;
                        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText("clipBoard", plainText);
                        clipboard.setPrimaryClip(clipData);
                        Snackbar.make(v, R.string.clipboard, Snackbar.LENGTH_SHORT).show();
                    }

                    return true;
                });
                popupMenu.show();
            });
        }

        public void bind(Pair<String, String> pair) {
            keyTextView.setText(pair.first);
            valueTextView.setText(pair.second);
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}