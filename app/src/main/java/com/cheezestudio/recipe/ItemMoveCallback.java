package com.cheezestudio.recipe;

import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;

import java.util.ArrayList;
import java.util.Collections;

public class ItemMoveCallback extends ItemTouchHelper.Callback {
    private ArrayList<Pair<String, String>> data;

    private RecyclerView.Adapter adapter;

    public ItemMoveCallback(RecyclerView.Adapter adapter, ArrayList<Pair<String, String>> data) {
        this.adapter = adapter;
        this.data = data;
    }

    @Override
    public int getMovementFlags(@NonNull androidx.recyclerview.widget.RecyclerView recyclerView, @NonNull androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(@NonNull androidx.recyclerview.widget.RecyclerView recyclerView, @NonNull androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder, @NonNull androidx.recyclerview.widget.RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAdapterPosition();
        int toPosition = target.getAdapterPosition();
        Collections.swap(data, fromPosition, toPosition);
        adapter.notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }
}