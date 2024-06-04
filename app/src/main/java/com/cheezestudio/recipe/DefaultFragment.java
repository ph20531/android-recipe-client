package com.cheezestudio.recipe;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DefaultFragment extends Fragment {
    private EditText url;

    private Spinner method;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_default, container, false);
        url = view.findViewById(R.id.url);
        url.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) saveUrlData();
        });

        method = view.findViewById(R.id.method);
        method.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                saveMethodData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                saveMethodData();
            }
        });

        loadData();
        return view;
    }

    private void saveUrlData() {
        SharedPreferences sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("url", url.getText().toString());
        editor.apply();
    }

    private void saveMethodData() {
        SharedPreferences sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("method", method.getSelectedItemPosition());
        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE);
        String prefURL = sharedPref.getString("url", Config.getDefaultUrl());
        int prefMethod = sharedPref.getInt("method", Config.getDefaultMethod());

        url.setText(prefURL);
        method.setSelection(prefMethod);
    }
}