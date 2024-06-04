package com.cheezestudio.recipe;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MainViewPagerAdapter extends FragmentStateAdapter {

    public MainViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new MainConsoleFragment();
            case 1:
                return new MainSettingsFragment();
            default:
                return new MainConsoleFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}