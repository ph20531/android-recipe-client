package com.cheezestudio.recipe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainSettingsFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager2 viewPager2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_main, container, false);
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager2 = view.findViewById(R.id.view_pager);

        viewPager2.setAdapter(new ViewPagerAdapter(requireActivity()));

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(R.string.settings_default);
                    break;
                case 1:
                    tab.setText(R.string.settings_headers);
                    break;
                case 2:
                    tab.setText(R.string.settings_params);
                    break;
                case 3:
                    tab.setText(R.string.settings_body);
                    break;
            }
        }).attach();
        return view;
    }
}