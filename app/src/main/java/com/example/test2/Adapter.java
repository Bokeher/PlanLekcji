package com.example.test2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class Adapter extends FragmentStateAdapter {
    private Lessons timetableData;
    private ArrayList<String> replacementsData;
    public Adapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, Lessons timetableData, ArrayList<String> replacementData) {
        super(fragmentManager, lifecycle);
        this.timetableData = timetableData;
        this.replacementsData = replacementData;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = new Fragm(timetableData, replacementsData);
        Bundle args = new Bundle();
        args.putString(Fragm.TITLE, "Tab"+(position+1));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
