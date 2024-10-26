package com.example.planlekcji.fragments.ui;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.planlekcji.MainActivity;
import com.example.planlekcji.MainViewModel;
import com.example.planlekcji.R;
import com.example.planlekcji.settings.SchoolEntriesDownloader;
import com.example.planlekcji.settings.model.TimetableInfo;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {
    private SharedPreferences sharedPref;
    private List<TimetableInfo> classInfoList = new ArrayList<>();
    private List<TimetableInfo> teachersInfoList = new ArrayList<>();
    private List<TimetableInfo> classroomsInfoList = new ArrayList<>();
    private View view;
    private MainViewModel mainViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Obtain the MainViewModel instance to update data on settings changes
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // Initialize SharedPreferences for storing application settings.
        sharedPref = MainActivity.getContext().getSharedPreferences("sharedPrefs", 0);

        // Fetch data relevant to settings.
        getData();

        // Initialize spinners and populate them with data.
        initSpinners();

        return view;
    }

    private void initSpinners() {
        Spinner spinnerClassTokens = view.findViewById(R.id.spinnerClassTokens);
        Spinner spinnerTeacherTokens = view.findViewById(R.id.spinnerTeacherTokens);
        Spinner spinnerClassroomTokens = view.findViewById(R.id.spinnerClassroomTokens);

        setSpinner(spinnerClassTokens, classInfoList, getString(R.string.classTokenKey), getString(R.string.classTimetableUrlKey));
        setSpinner(spinnerTeacherTokens, teachersInfoList, getString(R.string.teacherTokenKey), getString(R.string.teacherTimetableUrlKey));
        setSpinner(spinnerClassroomTokens, classroomsInfoList, getString(R.string.classroomTokenKey), getString(R.string.classroomTimetableUrlKey));

        setTypeOfTimetableSpinner();
    }

    private void setSpinner(Spinner spinner, List<TimetableInfo> timetableInfoList, String sharedPreferencesToken, String sharedPreferencesUrl) {
        List<String> tokenList = new ArrayList<>();
        for (TimetableInfo timetableInfo : timetableInfoList) {
            tokenList.add(timetableInfo.getToken());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.getContext(), android.R.layout.simple_list_item_1, tokenList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        String token = sharedPref.getString(sharedPreferencesToken, "");
        spinner.setSelection(adapter.getPosition(token));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TimetableInfo timetableInfo = timetableInfoList.get(i);

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(sharedPreferencesToken, timetableInfo.getToken());
                editor.putString(sharedPreferencesUrl, timetableInfo.getUrl());
                editor.apply();

                // Update timetable and replacements
                mainViewModel.fetchData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void setTypeOfTimetableSpinner() {
        Spinner spinnerUserType = view.findViewById(R.id.spinnerUserType);

        int typeIndex = sharedPref.getInt(getString(R.string.selectedTypeOfTimetableKey), 0);
        spinnerUserType.setSelection(typeIndex);

        spinnerUserType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(getString(R.string.selectedTypeOfTimetableKey), i);
                editor.apply();

                changeVisibility();

                // Update timetable and replacements
                mainViewModel.fetchData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    /**
     * Changes the visibility of three components: class, teacher, and classroom.
     */
    private void changeVisibility() {
        int whichIsVisible = sharedPref.getInt(getString(R.string.selectedTypeOfTimetableKey), 0) + 1;

        int classVisibility = View.GONE;
        int teacherVisibility = View.GONE;
        int classroomVisibility = View.GONE;

        if(whichIsVisible == 1) classVisibility = View.VISIBLE;
        else if(whichIsVisible == 2) teacherVisibility = View.VISIBLE;
        else if(whichIsVisible == 3) classroomVisibility = View.VISIBLE;

        view.findViewById(R.id.textView_selectClass).setVisibility(classVisibility);
        view.findViewById(R.id.textView_selectTeacher).setVisibility(teacherVisibility);
        view.findViewById(R.id.textView_selectClassroom).setVisibility(classroomVisibility);

        view.findViewById(R.id.spinnerClassTokens).setVisibility(classVisibility);
        view.findViewById(R.id.spinnerTeacherTokens).setVisibility(teacherVisibility);
        view.findViewById(R.id.spinnerClassroomTokens).setVisibility(classroomVisibility);
    }

    private void getData() {
        SchoolEntriesDownloader spinnersDataDownloader = new SchoolEntriesDownloader();
        Thread thread = new Thread(spinnersDataDownloader);
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        classInfoList = spinnersDataDownloader.getClassInfoList();
        teachersInfoList = spinnersDataDownloader.getTeachersInfoList();
        classroomsInfoList = spinnersDataDownloader.getClassroomsInfoList();
    }
}