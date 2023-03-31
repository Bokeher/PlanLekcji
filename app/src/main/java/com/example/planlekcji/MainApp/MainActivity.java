package com.example.planlekcji.MainApp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager2.widget.ViewPager2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.planlekcji.MainApp.Timetable.Adapter;
import com.example.planlekcji.MainApp.Replacements.GetReplacementsData;
import com.example.planlekcji.MainApp.Timetable.GetTimetableData;
import com.example.planlekcji.MainApp.Timetable.Lessons;
import com.example.planlekcji.R;
import com.example.planlekcji.MainApp.Replacements.ReplacementList;
import com.example.planlekcji.Settings.SettingsActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private HashMap<Integer, List<Integer>> replacementsToTimetableData = new HashMap<>();
    private static Context mContext;
    //    private String replacementsData;
    private ReplacementList replacementList;
    ViewPager2 viewPager;
    Lessons lessonsData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("state", "onCreate");

//        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
//        getActionBar().hide();

        mContext = this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        createNotificationChannel();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
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

        ImageButton button = findViewById(R.id.imageButton_goSettings);
        button.setOnClickListener(view -> {
            Intent settingsIntent = new Intent(view.getContext(), SettingsActivity.class);
            startActivity(settingsIntent);
        });

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

        setEventListenersToReplacements();
        setReplacements();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("state", "onStop");
        // TODO: notifications
//        System.out.println("onStop");
//        Log.e("ryhc", "dwad2");
//        startService(new Intent(this, NotificationService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("state", "onResume");
        // update all data for timetable and replacements
        getAllData();

        // set adapter to viewPager
        setAdapterToViewPager();

        // set current day (monday -> first tab, etc)
        setCurrentDay();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ChannelName";
            String description = "ChannelDesc";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notificationChannel", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setCurrentDay() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        int dayNumb = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if(dayNumb < 1 || dayNumb > 5) dayNumb = 1;

        viewPager.setCurrentItem(dayNumb-1);
    }

    private void setAdapterToViewPager() {
        Adapter adapter = new Adapter(getSupportFragmentManager(), getLifecycle(), lessonsData, replacementsToTimetableData);
        viewPager.setAdapter(adapter);
    }

    private void getAllData() {
        lessonsData = getDataForTimetable();
        replacementList = getDataForReplacements();
        replacementsToTimetableData = replacementList.getReplacementInfo();
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
                        changeVisibilityToReplacements(false);
                        break;
                    case 1:
                        changeVisibilityToReplacements(true);
                        break;
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }
    private void changeVisibilityToReplacements(boolean change) {
        int timetableVisibility = View.VISIBLE;
        int replacementsVisibility = View.INVISIBLE;

        if(change) {
            timetableVisibility = View.INVISIBLE;
            replacementsVisibility = View.VISIBLE;
        }

        findViewById(R.id.tabLayout).setVisibility(timetableVisibility);
        findViewById(R.id.pager).setVisibility(timetableVisibility);

        findViewById(R.id.textView_replacements).setVisibility(replacementsVisibility);
    }

    private void setReplacements() {
        TextView textFieldReplacements = findViewById(R.id.textView_replacements);
        if (replacementList.getReplacementList().size() == 0) textFieldReplacements.setText("Brak zastępstw");
        else textFieldReplacements.setText(replacementList.toString());
    }

    private ReplacementList getDataForReplacements() {
        GetReplacementsData getReplacementsData = new GetReplacementsData();
        Thread thread = new Thread(getReplacementsData);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return getReplacementsData.getReplacementList();
    }

    public static Context getContext() {
        return mContext;
    }
}