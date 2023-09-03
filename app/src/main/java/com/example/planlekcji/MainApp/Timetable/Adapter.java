package com.example.planlekcji.MainApp.Timetable;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.HashMap;
import java.util.List;

public class Adapter extends FragmentStateAdapter {
    private Lessons timetableData;
    public Adapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, Lessons timetableData) {
        super(fragmentManager, lifecycle);
        this.timetableData = timetableData;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = new LessonFragment(timetableData);
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
