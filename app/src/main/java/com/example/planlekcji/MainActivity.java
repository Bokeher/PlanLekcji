package com.example.planlekcji;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager2.widget.ViewPager2;

import com.example.planlekcji.fragments.model.ViewPagerAdapter;
import com.example.planlekcji.utils.ToastUtils;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class MainActivity extends AppCompatActivity {

    private static Context appContext;

    private ViewPager2 viewPager2_appContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the application context for other functions.
        appContext = this;

        // Check for internet connection; exit the app if not connected.
        if (!isOnline()) {
            ToastUtils.showToast(this, "Wymagane połączenie z internetem.", true);
        }

        // Lock the orientation of the screen to portrait mode.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Force night mode for the entire application.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        // Set the content view for the main activity.
        setContentView(R.layout.activity_main);

        viewPager2_appContent = findViewById(R.id.viewPager2_appContent);
        TabLayout tabLayout = findViewById(R.id.tabLayout_navigate);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager2_appContent.setAdapter(adapter);

        // Connect the TabLayout with the ViewPager2
        new TabLayoutMediator(tabLayout, viewPager2_appContent, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(R.string.navigate_home);
                    break;
                case 1:
                    tab.setText(R.string.navigate_timetable);
                    break;
                case 2:
                    tab.setText(R.string.navigate_replacements);
                    break;
                case 3:
                    tab.setText(R.string.navigate_settings);
                    break;
            }
        }).attach();
    }

    public static Context getContext() {
        return appContext;
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}