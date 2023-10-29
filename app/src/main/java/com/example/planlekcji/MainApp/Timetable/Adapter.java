package com.example.planlekcji.MainApp.Timetable;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.planlekcji.MainApp.Replacements.ReplacementToTimetable;

import java.util.List;

public class Adapter extends FragmentStateAdapter {
    private Lessons timetableData;
    private List<ReplacementToTimetable> replacementsForTimetable;

    public Adapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, Lessons timetableData, List<ReplacementToTimetable> replacementsForTimetable) {
        super(fragmentManager, lifecycle);
        this.timetableData = timetableData;
        this.replacementsForTimetable = replacementsForTimetable;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = new LessonFragment(timetableData, replacementsForTimetable);
        Bundle args = new Bundle();
        args.putString(LessonFragment.TITLE, "Tab"+(position+1));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
