package com.example.planlekcji.timetable.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.planlekcji.replacements.model.ReplacementToTimetable;
import com.example.planlekcji.timetable.model.DayOfWeek;

import java.util.List;
import java.util.Map;

public class Adapter extends FragmentStateAdapter {
    private final Map<DayOfWeek, List<String>> timetableMap;
    private final List<ReplacementToTimetable> replacementsForTimetable;

    public Adapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, Map<DayOfWeek, List<String>> timetableMap, List<ReplacementToTimetable> replacementsForTimetable) {
        super(fragmentManager, lifecycle);
        this.timetableMap = timetableMap;
        this.replacementsForTimetable = replacementsForTimetable;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (timetableMap == null || timetableMap.get(DayOfWeek.MONDAY) == null) {
            return new Fragment();
        }

        Fragment fragment = new LessonFragment(timetableMap, replacementsForTimetable);
        Bundle args = new Bundle();
        args.putString(LessonFragment.TITLE, "Tab" + (position + 1));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
