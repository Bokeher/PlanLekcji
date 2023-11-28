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
    private List<LessonRow> lessonRows;
    private List<ReplacementToTimetable> replacementsForTimetable;

    public Adapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, List<LessonRow> lessonRows, List<ReplacementToTimetable> replacementsForTimetable) {
        super(fragmentManager, lifecycle);
        this.lessonRows = lessonRows;
        this.replacementsForTimetable = replacementsForTimetable;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (lessonRows == null || lessonRows.get(0).getLessonNumbers() == null || lessonRows.get(0) == null) {
            return new Fragment();
        }

        Fragment fragment = new LessonFragment(lessonRows, replacementsForTimetable);
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
