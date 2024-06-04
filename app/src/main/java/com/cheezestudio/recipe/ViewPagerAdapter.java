package com.cheezestudio.recipe;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new DefaultFragment();
            case 1:
                return new HeadersFragment();
            case 2:
                return new ParamsFragment();
            case 3:
                return new BodyFragment();
            default:
                return new DefaultFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}