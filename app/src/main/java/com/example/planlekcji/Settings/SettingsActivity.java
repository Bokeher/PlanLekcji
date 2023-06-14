package com.example.planlekcji.Settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.planlekcji.R;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    private SharedPreferences sharedPref;
    private List<TimetableInfo> classInfoList = new ArrayList<>();
    private List<TimetableInfo> teachersInfoList = new ArrayList<>();
    private List<TimetableInfo> classroomsInfoList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        sharedPref = this.getSharedPreferences("sharedPrefs", 0);

        getData();

        initSpinners();
    }

    private void initSpinners() {
        Spinner spinnerClassTokens = findViewById(R.id.spinnerClassTokens);
        Spinner spinnerTeacherTokens = findViewById(R.id.spinnerTeacherTokens);
        Spinner spinnerClassroomTokens = findViewById(R.id.spinnerClassroomTokens);

        // moze byc tu blad w przyszlosci jak nie bedzie nauczyciela 'AB' lub klasy '1 ALN' lub sali 'A29'
        setSpinner(spinnerClassTokens, classInfoList, getString(R.string.classTokenKey), getString(R.string.classTimetableUrlKey), "1 ALN");
        setSpinner(spinnerTeacherTokens, teachersInfoList, getString(R.string.teacherTokenKey), getString(R.string.teacherTimetableUrlKey), "AB");
        setSpinner(spinnerClassroomTokens, classroomsInfoList, getString(R.string.teacherTokenKey), getString(R.string.classroomTimetableUrlKey), "A29");
        setTypeSpinner();
    }

    private void setSpinner(Spinner spinner, List<TimetableInfo> timetableInfoList, String sharedPreferencesToken, String sharedPreferencesUrl, String sharedPreferencesDefaultToken) {
        List<String> tokenList = new ArrayList<>();
        for (TimetableInfo timetableInfo : timetableInfoList) {
            tokenList.add(timetableInfo.getToken());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, tokenList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        String token = sharedPref.getString(sharedPreferencesToken, sharedPreferencesDefaultToken);
        spinner.setSelection(adapter.getPosition(token));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TimetableInfo timetableInfo = timetableInfoList.get(i);

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(sharedPreferencesToken, timetableInfo.getToken());
                editor.putString(sharedPreferencesUrl, timetableInfo.getUrl());
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setTypeSpinner() {
        Spinner spinnerUserType = findViewById(R.id.spinnerUserType);

        int typeIndex = sharedPref.getInt(getString(R.string.selectedTypeOfTimetableKey), 0);
        spinnerUserType.setSelection(typeIndex);

        spinnerUserType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(getString(R.string.selectedTypeOfTimetableKey), i);
                editor.apply();

                changeVisibility();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void changeVisibility() {
        TextView textView_selectClass = findViewById(R.id.textView_selectClass);
        TextView textView_selectTeacher = findViewById(R.id.textView_selectTeacher);
        TextView textView_selectClassroom = findViewById(R.id.textView_selectClassroom);

        Spinner spinnerClassTokens = findViewById(R.id.spinnerClassTokens);
        Spinner spinnerTeacherTokens = findViewById(R.id.spinnerTeacherTokens);
        Spinner spinnerClassroomTokens = findViewById(R.id.spinnerClassroomTokens);

        int visible = View.VISIBLE;
        int gone = View.GONE;

        int timetableType = sharedPref.getInt(getString(R.string.selectedTypeOfTimetableKey), 0);
        if(timetableType == 0) {
            spinnerClassTokens.setVisibility(visible);
            spinnerTeacherTokens.setVisibility(gone);
            spinnerClassroomTokens.setVisibility(gone);

            textView_selectClass.setVisibility(visible);
            textView_selectTeacher.setVisibility(gone);
            textView_selectClassroom.setVisibility(gone);
        } else if(timetableType == 1) {
            spinnerClassTokens.setVisibility(gone);
            spinnerTeacherTokens.setVisibility(visible);
            spinnerClassroomTokens.setVisibility(gone);

            textView_selectClass.setVisibility(gone);
            textView_selectTeacher.setVisibility(visible);
            textView_selectClassroom.setVisibility(gone);
        } else {
            spinnerClassTokens.setVisibility(gone);
            spinnerTeacherTokens.setVisibility(gone);
            spinnerClassroomTokens.setVisibility(visible);

            textView_selectClass.setVisibility(gone);
            textView_selectTeacher.setVisibility(gone);
            textView_selectClassroom.setVisibility(visible);
        }
    }

    private void getData() {
        GetDataForSpinners getDataForSpinners = new GetDataForSpinners();
        Thread thread = new Thread(getDataForSpinners);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        classInfoList = getDataForSpinners.getClassInfoList();
        teachersInfoList = getDataForSpinners.getTeachersInfoList();
        classroomsInfoList = getDataForSpinners.getClassroomsInfoList();
    }
}
