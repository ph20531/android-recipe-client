package com.cheezestudio.recipe;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private View toolbarDivider;
    private ProgressBar circularProgress;
    private TextView elapsedTime;
    public int currentTabIndex = 0;
    public int lastCircularProgressVisibility = View.GONE;
    public int lastElapsedTimeVisibility = View.GONE;
    private TabLayout mainTabLayout;
    private ViewPager2 mainViewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        hideSystemBar();

        toolbar = findViewById(R.id.toolbar);
        toolbarDivider = findViewById(R.id.toolbar_divider);
        circularProgress = findViewById(R.id.circular_progress);
        elapsedTime = findViewById(R.id.elapsed_time);
        mainTabLayout = findViewById(R.id.main_tab_layout);
        mainViewPager2 = findViewById(R.id.main_view_pager);
        mainViewPager2.setAdapter(new MainViewPagerAdapter(this));
        mainViewPager2.setUserInputEnabled(false);
        new TabLayoutMediator(mainTabLayout, mainViewPager2, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(R.string.console);
                    break;
                case 1:
                    tab.setText(R.string.settings);
                    break;
            }
        }).attach();

        mainViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentTabIndex = position;
                switch (position) {
                    case 0:
                        toolbar.setTitle(R.string.console);
                        toolbarDivider.setVisibility(View.VISIBLE);

                        circularProgress.setVisibility(lastCircularProgressVisibility);
                        elapsedTime.setVisibility(lastElapsedTimeVisibility);

                        break;
                    case 1:
                        toolbar.setTitle(R.string.settings);
                        toolbarDivider.setVisibility(View.GONE);

                        lastCircularProgressVisibility = circularProgress.getVisibility();
                        lastElapsedTimeVisibility = elapsedTime.getVisibility();

                        circularProgress.setVisibility(View.GONE);
                        elapsedTime.setVisibility(View.GONE);
                        break;
                }
            }
        });

        mainTabLayout.getTabAt(0).setIcon(R.drawable.outline_developer_board_24);
        mainTabLayout.getTabAt(1).setIcon(R.drawable.outline_settings_input_component_24);
    }

    private void hideSystemBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11 이상
            WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.navigationBars());
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Android 4.4 (KitKat) 이상, Android 11 미만
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}