package com.cheezestudio.recipe;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RecyclerView extends androidx.recyclerview.widget.RecyclerView {
    private View emptyView;

    private AdapterDataObserver emptyObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            Adapter<?> adapter = getAdapter();
            if (adapter != null) {
                if (listener != null)
                    listener.onDataChanged();
                if (emptyView != null) {
                    if (adapter.getItemCount() == 0) {
                        emptyView.setVisibility(View.VISIBLE);
                        RecyclerView.this.setVisibility(View.GONE);
                    } else {
                        emptyView.setVisibility(View.GONE);
                        RecyclerView.this.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    };

    private OnDataChangedListener listener;

    public interface OnDataChangedListener {
        void onDataChanged();
    }

    public void setOnDataChangedListener(OnDataChangedListener listener) {
        this.listener = listener;
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);

        if (adapter != null) {
            adapter.registerAdapterDataObserver(emptyObserver);
        }

        emptyObserver.onChanged();
    }

    @Override
    public void onChildAttachedToWindow(@NonNull View child) {
        super.onChildAttachedToWindow(child);
        emptyObserver.onChanged();
    }

    @Override
    public void onChildDetachedFromWindow(@NonNull View child) {
        super.onChildDetachedFromWindow(child);
        emptyObserver.onChanged();
    }

    public RecyclerView(@NonNull Context context) {
        super(context);
    }

    public RecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}