package com.example.planlekcji.fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.planlekcji.fragments.ui.ArticlesFragment;
import com.example.planlekcji.fragments.ui.CalendarFragment;
import com.example.planlekcji.fragments.ui.ReplacementsFragment;
import com.example.planlekcji.fragments.ui.SettingsFragment;
import com.example.planlekcji.fragments.ui.TimetableFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public static final int TIMETABLE_TAB_ID = 0;
    public static final int REPLACEMENTS_TAB_ID = 1;
    public static final int ARTICLES_TAB_ID = 2;
    public static final int CALENDAR_TAB_ID = 3;
    public static final int SETTINGS_TAB_ID = 4;

    public static final int NUMBER_OF_TABS = 5;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return switch (position) {
            case TIMETABLE_TAB_ID -> new TimetableFragment();
            case REPLACEMENTS_TAB_ID -> new ReplacementsFragment();
            case ARTICLES_TAB_ID -> new ArticlesFragment();
            case CALENDAR_TAB_ID -> new CalendarFragment();
            case SETTINGS_TAB_ID -> new SettingsFragment();
            default -> throw new IllegalStateException("Unexpected value: " + position);
        };
    }

    @Override
    public int getItemCount() {
        return NUMBER_OF_TABS;
    }
}