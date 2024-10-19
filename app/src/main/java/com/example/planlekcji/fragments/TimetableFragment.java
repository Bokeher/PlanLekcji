package com.example.planlekcji.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.planlekcji.MainViewModel;
import com.example.planlekcji.R;
import com.example.planlekcji.timetable.model.LessonRow;
import com.example.planlekcji.timetable.ui.Adapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class TimetableFragment extends Fragment {
    private ViewPager2 viewp;
    private MainViewModel mainViewModel;

    private List<LessonRow> lessonRows;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_timetable, container, false);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.fetchData();

        viewp = view.findViewById(R.id.viewPager_timetable);
        viewp.setOffscreenPageLimit(5);

        observeAndHandleLiveDataChanges();

        setAdapterToViewPager();

        return view;
    }

    private void setAdapterToViewPager() {
        Adapter adapter = new Adapter(getChildFragmentManager(), getLifecycle(), lessonRows, null);
        viewp.setAdapter(adapter);
    }

    private void observeAndHandleLiveDataChanges() {
        mainViewModel.getCombinedLiveData().observe(getViewLifecycleOwner(), bool -> {
            if(bool)  {
                lessonRows = mainViewModel.getLessonRows();

                setAdapterToViewPager();

                setHeadersToTabLayout();
                setCurrentDay();
            }
        });
    }

    private void setHeadersToTabLayout() {
        TabLayout tabLayout = view.findViewById(R.id.tabLayout_dayNames);
        new TabLayoutMediator(tabLayout, viewp, (tab, position) -> {
            String data = "";

            switch (position + 1){
                case 1:
                    data = getResources().getString(R.string.mondayShortcut);
                    break;
                case 2:
                    data = getResources().getString(R.string.tuesdayShortcut);
                    break;
                case 3:
                    data = getResources().getString(R.string.wednesdayShortcut);
                    break;
                case 4:
                    data = getResources().getString(R.string.thursdayShortcut);
                    break;
                case 5:
                    data = getResources().getString(R.string.fridayShortcut);
                    break;
            }
            tab.setText(data);
        }).attach();
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

        viewp.setCurrentItem(dayNumb - 1);
    }
}