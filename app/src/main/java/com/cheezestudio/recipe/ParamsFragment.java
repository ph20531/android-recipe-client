package com.cheezestudio.recipe;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class ParamsFragment extends Fragment {

    private RecyclerView Params;

    private PairListAdapter adapter;

    private FloatingActionButton addParam;

    private ArrayList<Pair<String, String>> data = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_params, container, false);

        data = loadData();

        Params = view.findViewById(R.id.params);
        Params.setOnDataChangedListener(() -> saveData(data));
        Params.setEmptyView(view.findViewById(R.id.empty));
        Params.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new PairListAdapter(requireContext(), R.string.settings_params, data);
        adapter.setOnItemClickListener((position) -> {
            Pair<String, String> item = data.get(position);

            PairDialogFragment dialogFragment = new PairDialogFragment(R.string.settings_params);
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

        Params.setAdapter(adapter);

        ItemMoveCallback callback = new ItemMoveCallback(adapter, data);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(Params);

        addParam = view.findViewById(R.id.add_param);
        addParam.setOnClickListener(v -> {
            PairDialogFragment dialogFragment = new PairDialogFragment(R.string.settings_params);
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
        editor.putString("params", json);
        editor.apply();
    }

    private ArrayList<Pair<String, String>> loadData() {
        SharedPreferences sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("params", Config.getDefaultParams());
        Type type = new TypeToken<ArrayList<Pair<String, String>>>() {
        }.getType();
        return gson.fromJson(json, type);
    }
}