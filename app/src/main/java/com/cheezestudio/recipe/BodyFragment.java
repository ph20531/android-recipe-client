package com.cheezestudio.recipe;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BodyFragment extends Fragment {
    private EditText body;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_body, container, false);
        body = view.findViewById(R.id.body);
        body.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) saveData();
        });

        loadData();
        return view;
    }

    private void saveData() {
        SharedPreferences sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("body", body.getText().toString());
        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE);
        String prefBody = sharedPref.getString("body", Config.getDefaultBody());

        body.setText(prefBody);
    }
}