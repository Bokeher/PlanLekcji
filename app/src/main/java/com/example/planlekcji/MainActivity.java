package com.example.planlekcji;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.planlekcji.MainApp.Timetable.Adapter;
import com.example.planlekcji.MainApp.Replacements.GetReplacementsData;
import com.example.planlekcji.MainApp.Timetable.GetTimetableData;
import com.example.planlekcji.MainApp.Timetable.Lessons;
import com.example.planlekcji.Settings.SettingsActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {
    private static Context appContext;
    private String replacementData;
    Lessons lessonsData;
    ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize context for other function
        appContext = this;

        // lock orientation screen
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // always use night mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        // set app content
        setContentView(R.layout.activity_main);

        // init ViewPager2
        viewPager = findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(6);

        // get all data for timetable and replacements from website
        getAllData();

        // set adapter to viewPager
        setAdapterToViewPager();

        // set current day (monday -> first tab, etc)
        setCurrentDay();

        // make settings button work
        ImageButton button = findViewById(R.id.imageButton_goSettings);
        button.setOnClickListener(view -> {
            Intent settingsIntent = new Intent(view.getContext(), SettingsActivity.class);
            startActivity(settingsIntent);
        });

        // set headers to tabLayout
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            String data = "Pon";
            position++;
            switch (position) {
                case 2:
                    data = "Wto";
                    break;
                case 3:
                    data = "Śrd";
                    break;
                case 4:
                    data = "Czw";
                    break;
                case 5:
                    data = "Ptk";
                    break;
            }
            tab.setText(data);
        }).attach();

        // make tab 'replacement' work
        setEventListenersToReplacements();

        // set text of replacements
        setReplacements();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // update all data for timetable and replacements
        getAllData();

        // set adapter to viewPager (update timetable data)
        setAdapterToViewPager();

        // update replacement data
        setReplacements();

        // set current day (monday -> first tab, etc)
        setCurrentDay();
    }

    /**
     * get current day, convert into number and set current item of viewPager
     * monday -> first tab, etc
     */
    private void setCurrentDay() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        int dayNumb = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if(dayNumb < 1 || dayNumb > 5) dayNumb = 1;

        viewPager.setCurrentItem(dayNumb - 1);
    }

    private void setAdapterToViewPager() {
        Adapter adapter = new Adapter(getSupportFragmentManager(), getLifecycle(), lessonsData);
        viewPager.setAdapter(adapter);
    }

    private void getAllData() {
        lessonsData = getDataForTimetable();
        replacementData = getDataForReplacements();
    }

    private Lessons getDataForTimetable() {
        GetTimetableData getTimetableData = new GetTimetableData();
        Thread thread = new Thread(getTimetableData);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return getTimetableData.getLessons();
    }

    private void setEventListenersToReplacements() {
        TabLayout guiTabs;
        guiTabs = findViewById(R.id.tabLayoutChangeView);
        guiTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()) {
                    case 0:
                        changeVisibilityOfReplacements(false);
                        break;
                    case 1:
                        changeVisibilityOfReplacements(true);
                        break;
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }
    private void changeVisibilityOfReplacements(boolean change) {
        int timetableVisibility = View.VISIBLE;
        int replacementsVisibility = View.GONE;

        if(change) {
            timetableVisibility = View.GONE;
            replacementsVisibility = View.VISIBLE;
        }

        findViewById(R.id.tabLayout).setVisibility(timetableVisibility);
        findViewById(R.id.pager).setVisibility(timetableVisibility);

        findViewById(R.id.scrollView_replacements).setVisibility(replacementsVisibility);
    }

    private void setReplacements() {
        TextView textFieldReplacements = findViewById(R.id.textView_replacements);
        textFieldReplacements.setText(Html.fromHtml(replacementData, Html.FROM_HTML_MODE_LEGACY));
    }

    private String getDataForReplacements() {
        GetReplacementsData getReplacementsData = new GetReplacementsData();
        Thread thread = new Thread(getReplacementsData);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return getReplacementsData.getAllReplacements();
    }

    public static Context getContext() {
        return appContext;
    }
}