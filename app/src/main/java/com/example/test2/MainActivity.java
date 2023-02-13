package com.example.test2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager2.widget.ViewPager2;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> replacementDataForTimetable = new ArrayList<String>();
    private String replacementsData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_main);

        replacementsData = getDataForReplacements();
        getDataNeededFromReplacementsToTimetable();

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager2 = findViewById(R.id.pager);
        viewPager2.setOffscreenPageLimit(6);

        Lessons timetableData = getDataForTimetable();

        Adapter adapter = new Adapter(getSupportFragmentManager(), getLifecycle(), timetableData, replacementDataForTimetable);
        viewPager2.setAdapter(adapter);

        Date date = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
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

        findViewById(R.id.tabLayout).setVisibility(timetableVisibility);
        findViewById(R.id.textView_replacements).setVisibility(replacementsVisibility);
    }

    private void setReplacements() {
        TextView textFieldReplacements = findViewById(R.id.textView_replacements);
        if (replacementsData == null || replacementsData.isEmpty() || replacementsData.equals("")) textFieldReplacements.setText("Brak zastępstw");
        else {
            //set replacementsData to editText
            textFieldReplacements.setText(replacementsData);

//            get replacementsData needed for updating timetable

        }
    }
    private void getDataNeededFromReplacementsToTimetable() {
        final String[] dayNames = {"poniedziałek", "wtorek", "środa", "czwartek", "piątek"};
        List<String> arrayData = new ArrayList<>();
        if(replacementsData.startsWith("Zastępstwa w dniu")) {
            arrayData.add(replacementsData);
        } else arrayData = Arrays.asList(replacementsData.split("\n\n"));

        for (String replacement : arrayData) {
            for (String dayName : dayNames) {
                if(replacement.contains(dayName)) {
                    int dayNumb = dayNameToIntValue(dayName);
                    String lessonNumbers = getLessonNumberFromReplacement(replacement);
                    replacementDataForTimetable.add(lessonNumbers+";"+dayNumb);
                }
            }
        }
    }

    private int dayNameToIntValue(String dayName) {
        final String[] dayNames = {"poniedziałek", "wtorek", "środa", "czwartek", "piątek"};

        for(int i = 0; i < dayNames.length; i++) {
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
        String res = String.join(",", lessonNumbers);

        return res;
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

        return getReplacementsData.getData();
    }
}