package com.example.planlekcji.fragments.model;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.planlekcji.fragments.ui.ReplacementsFragment;
import com.example.planlekcji.fragments.ui.SettingsFragment;
import com.example.planlekcji.fragments.ui.TimetableFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return switch (position) {
            case 1 -> new ReplacementsFragment();
            case 2 -> new SettingsFragment();
            default -> new TimetableFragment();
        };
    }

    @Override
    public int getItemCount() {
        return 3; // Number of tabs
    }
}