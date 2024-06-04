package com.cheezestudio.recipe;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class HeadersFragment extends Fragment {

    private RecyclerView headers;

    private PairListAdapter adapter;

    private FloatingActionButton addHeader;

    private ArrayList<Pair<String, String>> data = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_headers, container, false);

        data = loadData();

        headers = view.findViewById(R.id.headers);
        headers.setOnDataChangedListener(() -> saveData(data));
        headers.setEmptyView(view.findViewById(R.id.empty));
        headers.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new PairListAdapter(requireContext(), R.string.settings_headers, data);
        adapter.setOnItemClickListener((position) -> {
            Pair<String, String> item = data.get(position);

            PairDialogFragment dialogFragment = new PairDialogFragment(R.string.settings_headers);
            Bundle args = new Bundle();
            args.putString("key", item.first);
            args.putString("value", item.second);
            dialogFragment.setArguments(args);
            dialogFragment.setOnPairListener((key, value) -> {
                data.set(position, new Pair<>(key, value));
                adapter.notifyDataSetChanged();
            });
            dialogFragment.show(requireActivity().getSupportFragmentManager(), "dialogFragment");
        });

        headers.setAdapter(adapter);

        ItemMoveCallback callback = new ItemMoveCallback(adapter, data);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(headers);

        addHeader = view.findViewById(R.id.add_header);
        addHeader.setOnClickListener(v -> {
            PairDialogFragment dialogFragment = new PairDialogFragment(R.string.settings_headers);
            dialogFragment.setOnPairListener((key, value) -> {
                data.add(new Pair<>(key, value));
                adapter.notifyDataSetChanged();
            });
            dialogFragment.show(requireActivity().getSupportFragmentManager(), "dialogFragment");
        });

        return view;
    }

    private void saveData(ArrayList<Pair<String, String>> data) {
        SharedPreferences sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(data);
        editor.putString("headers", json);
        editor.apply();
    }

    private ArrayList<Pair<String, String>> loadData() {
        SharedPreferences sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("headers", Config.getDefaultHeaders());
        Type type = new TypeToken<ArrayList<Pair<String, String>>>() {
        }.getType();
        return gson.fromJson(json, type);
    }
}