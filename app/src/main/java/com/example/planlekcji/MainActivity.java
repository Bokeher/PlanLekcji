package com.example.planlekcji;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static Context appContext;
    private String replacementData;
    private List<String> replacementsForSearch;
    Lessons lessonsData;
    ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize context for other function
        appContext = this;

        // exit app if not connected to internet
        if(!isOnline()) {
            Toast.makeText(this, "Wymagane połączenie z internetem.", Toast.LENGTH_LONG).show();
            MainActivity.this.finish();
            System.exit(0);
        }
        
        // lock orientation of screen
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // always use night mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        setContentView(R.layout.activity_main);

        // init ViewPager2
        viewPager = findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(6);

        // get all data for timetable and replacements from website
        getAllData();

        setAdapterToViewPager();

        // set current day (monday -> first tab, etc)
        setCurrentDay();
        setHeadersToTabLayout();

        // make tab 'replacement' work
        setEventListenerToSettingsButton();
        setEventListenersToReplacements();
        setEventListenerToSearchBar();

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

        // update search bar
        updateSearchBar();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EditText searchBar = findViewById(R.id.editText_searchBar);

        SharedPreferences sharedPref = this.getSharedPreferences("sharedPrefs", 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.searchKey), String.valueOf(searchBar.getText()));
        editor.apply();
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
    private void setEventListenerToSettingsButton() {
        ImageButton button = findViewById(R.id.imageButton_goSettings);
        button.setOnClickListener(view -> {
            Intent settingsIntent = new Intent(view.getContext(), SettingsActivity.class);
            startActivity(settingsIntent);
        });
    }

    private void setEventListenerToSearchBar() {
        EditText searchBar = findViewById(R.id.editText_searchBar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String foundData = searchReplacements(String.valueOf(charSequence));
                if(foundData.isEmpty() || charSequence.equals("")) setReplacements();
                else setReplacements(foundData);
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void setHeadersToTabLayout() {
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
    private void setReplacements(String data) {
        TextView textFieldReplacements = findViewById(R.id.textView_replacements);
        textFieldReplacements.setText(Html.fromHtml(data, Html.FROM_HTML_MODE_LEGACY));
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
        replacementsForSearch = getReplacementsData.getReplacementsForSearch();
        return getReplacementsData.getAllReplacements();
    }

    public static Context getContext() {
        return appContext;
    }

    private String searchReplacements(String searchingKey) {
        if(searchingKey.equals("")) return "";
        String foundResults = "";
        for (String singleReplacement : replacementsForSearch) {
            if(singleReplacement.toLowerCase().contains(searchingKey.toLowerCase())) {
                if(foundResults.isEmpty()) foundResults += singleReplacement;
                else foundResults += "<br><br>"+singleReplacement;
            }
        }
        return foundResults;
    }

    private void updateSearchBar() {
        SharedPreferences sharedPref = this.getSharedPreferences("sharedPrefs", 0);
        String searchKey = sharedPref.getString(getString(R.string.searchKey), "");

        EditText searchBar = findViewById(R.id.editText_searchBar);
        searchBar.setText(searchKey);
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}