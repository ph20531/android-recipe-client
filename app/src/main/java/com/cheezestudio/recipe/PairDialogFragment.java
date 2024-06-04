package com.cheezestudio.recipe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class PairDialogFragment extends DialogFragment {
    private int type;

    private EditText key;

    private EditText value;

    private PairListener listener;

    public interface PairListener {
        void onEdit(String key, String value);
    }

    public PairDialogFragment(int type){
        this.type = type;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_pair, null);

        this.key = dialogView.findViewById(R.id.key);
        this.value = dialogView.findViewById(R.id.value);

        Bundle args = getArguments();
        String savedKey, savedValue;

        if (args != null) {
            savedKey = getArguments().getString("key", "");
            savedValue = getArguments().getString("value", "");

            this.key.setText(savedKey);
            this.value.setText(savedValue);
        }

        String type = getString(this.type);
        String title = type + " " + getString(args != null ? R.string.update : R.string.create);
        int positive = args != null ? R.string.update : R.string.create;
        int negative = R.string.cancel;

        AlertDialog pairDialog = builder.setView(dialogView).setTitle(title).setPositiveButton(positive, (dialog, which) -> {
            String key = this.key.getText().toString();
            String value = this.value.getText().toString();
            listener.onEdit(key, value);
        }).setNegativeButton(negative, (dialog, which) -> dialog.dismiss()).create();

        pairDialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = pairDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            positiveButton.setEnabled(!TextUtils.isEmpty(key.getText()) && !TextUtils.isEmpty(value.getText()));

            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    positiveButton.setEnabled(!TextUtils.isEmpty(key.getText()) && !TextUtils.isEmpty(value.getText()));
                }
            };

            key.addTextChangedListener(textWatcher);
            value.addTextChangedListener(textWatcher);
        });

        return pairDialog;
    }

    public void setOnPairListener(PairListener listener) {
        this.listener = listener;
    }
}
