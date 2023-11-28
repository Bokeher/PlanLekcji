package com.example.planlekcji;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.planlekcji.MainApp.Replacements.ReplacementToTimetable;
import com.example.planlekcji.MainApp.Timetable.Adapter;
import com.example.planlekcji.MainApp.Timetable.LessonRow;
import com.example.planlekcji.Settings.SettingsActivity;
import com.example.planlekcji.ViewModels.MainViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private static Context appContext;

    // downloaded data
    private List<String> replacements;
    private List<ReplacementToTimetable> replacementsForTimetable;
    private List<LessonRow> lessonRows;

    private ViewPager2 viewPager;
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the application context for other functions.
        appContext = this;

        // Initialize the ViewModel
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Check for internet connection; exit the app if not connected.
        if (!isOnline()) {
            ToastUtils.showToast(this, "Wymagane połączenie z internetem.", true);
        }

        // Lock the orientation of the screen to portrait mode.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Enable night mode for the entire application.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        // Set the content view for the main activity.
        setContentView(R.layout.activity_main);

        // Initialize ViewPager2.
        viewPager = findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(5);

        observeAndHandleLiveDataChanges();

        // Set event listeners for various UI elements.
        setEventListenerToSettingsButton();
        setEventListenersToReplacements();
    }

    @Override
    protected void onResume() {
        super.onResume();

        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        mainViewModel.fetchData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EditText searchBar = findViewById(R.id.editText_searchBar);

        SharedPreferences sharedPref = this.getSharedPreferences("sharedPrefs", 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.searchKey), String.valueOf(searchBar.getText()));
        editor.apply();

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        mainViewModel.setSelectedTabNumber(tabLayout.getSelectedTabPosition()+1);
    }

    private void observeAndHandleLiveDataChanges() {
        mainViewModel.getCombinedLiveData().observe(this, bool -> {
            if(bool)  {
                lessonRows = mainViewModel.getLessonRows();
                replacementsForTimetable = mainViewModel.getReplacementsForTimetableValue();
                replacements = mainViewModel.getReplacementsValue();

                if(lessonRows != null && replacementsForTimetable != null && replacements != null) {

                    setReplacements();
                    setEventListenerToSearchBar();
                    searchReplacements(updateSearchBar());

                    setAdapterToViewPager();

                    if(mainViewModel.getSelectedTabNumber() == 0) setCurrentDay();
                    else {
                        viewPager.setCurrentItem(mainViewModel.getSelectedTabNumber()-1, false);
                    }

                    setHeadersToTabLayout();
                    findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    /**
     * Sets the current item of the ViewPager based on the current day of the week.
     * Monday corresponds to the first tab, Tuesday to the second tab, and so on.
     */
    private void setCurrentDay() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        int dayNumb = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if(dayNumb < 1 || dayNumb > 5) dayNumb = 1;

        viewPager.setCurrentItem(dayNumb - 1);
    }

    private void setAdapterToViewPager() {
        Adapter adapter = new Adapter(getSupportFragmentManager(), getLifecycle(), lessonRows, replacementsForTimetable);
        viewPager.setAdapter(adapter);
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
        TextView textView_resultsNumber = findViewById(R.id.textView_noResults);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() == 0) {
                    setReplacements();
                } else {
                    String foundData = searchReplacements(String.valueOf(charSequence));
                    if(foundData.isEmpty() || charSequence.equals("")) {
                        textView_resultsNumber.setVisibility(View.VISIBLE);
                        setReplacements();
                    } else {
                        textView_resultsNumber.setVisibility(View.GONE);
                        setReplacements(foundData);
                    }
                }
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
            String data = "";
            position++;

            switch (position) {
                case 1:
                    data = "Pon";
                    break;
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
        textFieldReplacements.setText(Html.fromHtml(String.join("<br><br>", replacements), Html.FROM_HTML_MODE_LEGACY));
    }
    private void setReplacements(String data) {
        if (data != null) {
            TextView textFieldReplacements = findViewById(R.id.textView_replacements);
            textFieldReplacements.setText(Html.fromHtml(data, Html.FROM_HTML_MODE_LEGACY));
        }
    }

    public static Context getContext() {
        return appContext;
    }

    private String searchReplacements(String searchingKey) {
        if(searchingKey.isEmpty()) return "";

        // all this in case user types '4ptn' instead of '4 ptn'
        searchingKey = handleOtherUserInput(searchingKey);

        // searching
        boolean firstIteration = true;
        StringBuilder foundResults = new StringBuilder();
        for (String singleReplacement : replacements) {
            if (firstIteration) {
                firstIteration = false;
                continue;
            }
            if(singleReplacement.toLowerCase().contains(searchingKey.toLowerCase())) {
                if(foundResults.length() == 0) foundResults.append(singleReplacement);
                else foundResults.append("<br><br>").append(singleReplacement);
            }
        }
        return foundResults.toString();
    }

    private String handleOtherUserInput(String searchingKey) {
        Pattern pattern = Pattern.compile("\\d");
        Matcher matcher = pattern.matcher(searchingKey.substring(0, 1));

        if(!searchingKey.contains(" ") && searchingKey.length() > 1 && matcher.find()) {
            searchingKey = searchingKey.charAt(0)+" "+searchingKey.substring(1);
        }

        return searchingKey;
    }

    private String updateSearchBar() {
        SharedPreferences sharedPref = this.getSharedPreferences("sharedPrefs", 0);
        String searchKey = sharedPref.getString(getString(R.string.searchKey), "");

        EditText searchBar = findViewById(R.id.editText_searchBar);
        searchBar.setText(searchKey);

        return searchKey;
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}