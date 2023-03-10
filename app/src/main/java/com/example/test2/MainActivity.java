package com.example.test2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager2.widget.ViewPager2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private HashMap<Integer, List<Integer>> replacementsToTimetableData = new HashMap<>();
//    private String replacementsData;
    private ReplacementList replacementList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        createNotificationChannel();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_main);

        Lessons lessonsData = getDataForTimetable();
        replacementList = getDataForReplacements();
        replacementsToTimetableData = replacementList.getReplacementInfo();

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager2 = findViewById(R.id.pager);
        viewPager2.setOffscreenPageLimit(6);

        Adapter adapter = new Adapter(getSupportFragmentManager(), getLifecycle(), lessonsData, replacementsToTimetableData);
        viewPager2.setAdapter(adapter);

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        int dayNumb = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if(dayNumb < 1 || dayNumb > 5) dayNumb = 1;

        viewPager2.setCurrentItem(dayNumb-1);

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            String data = "Pon";
            position++;
            switch (position) {
                case 2:
                    data = "Wto";
                    break;
                case 3:
                    data = "??rd";
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
        // TODO: notifications
//        System.out.println("onStop");
//        Log.e("ryhc", "dwad2");
//        startService(new Intent(this, NotificationService.class));
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
        if (replacementList.getReplacementList().size() == 0) textFieldReplacements.setText("Brak zast??pstw");
        else textFieldReplacements.setText(replacementList.toString());
    }
//    private void getDataNeededFromReplacementsToTimetable() {
//        final String[] dayNames = {"poniedzia??ek", "wtorek", "??roda", "czwartek", "pi??tek"};
//        List<String> arrayData = new ArrayList<>();
//
//        if (replacementsData.startsWith("Zast??pstwa w dniu")) arrayData.add(replacementsData);
//        else arrayData = Arrays.asList(replacementsData.split("\n\n"));
//
//        for (String replacement : arrayData) {
//            for (String dayName : dayNames) {
//                if (replacement.contains(dayName)) {
//                    int dayNumb = dayNameToIntValue(dayName);
//                    String lessonNumbers = getLessonNumberFromReplacement(replacement);
//                    replacementDataForTimetable.add(lessonNumbers+";"+dayNumb);
//                }
//            }
//        }
//    }

    private int dayNameToIntValue(String dayName) {
        final String[] dayNames = {"poniedzia??ek", "wtorek", "??roda", "czwartek", "pi??tek"};

        for (int i = 0; i < dayNames.length; i++) {
            if(dayName.equals(dayNames[i])) return i+1;
        }
        return 0;
    }

    private String getLessonNumberFromReplacement(String text) {
        Pattern pattern = Pattern.compile("([0-9]|[0-9]) \\| ");
        Matcher matcher = pattern.matcher(text);

        ArrayList<String> lessonNumbers = new ArrayList<String>();
        while (matcher.find()) {
            int startingIndex = matcher.start();
            char ch = text.charAt(startingIndex);
            String number = String.valueOf(ch);
            lessonNumbers.add(number);
        }

        return String.join(",", lessonNumbers);
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
}