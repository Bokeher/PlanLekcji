package com.example.planlekcji.Settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.planlekcji.R;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    SharedPreferences sharedPref;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        sharedPref = this.getSharedPreferences("sharedPrefs", 0);

        classInfoSpinnerInit();
        crossOutReplacementsSwitchInit();
    }

    private void classInfoSpinnerInit() {
        List<ClassInfo> classInfoList = getData();

        Spinner spinnerClassTokens = findViewById(R.id.spinnerClassTokens);

        List<String> tokenList = new ArrayList<>();
        for (ClassInfo classInfo : classInfoList) {
            tokenList.add(classInfo.getToken());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, tokenList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClassTokens.setAdapter(adapter);

        String token = sharedPref.getString("classToken", "4 PTN");
        spinnerClassTokens.setSelection(adapter.getPosition(token));

        spinnerClassTokens.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ClassInfo classInfo = classInfoList.get(i);

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("classToken", classInfo.getToken());
                editor.putString("classTimetableUrl", classInfo.getTimetableUrl());
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void crossOutReplacementsSwitchInit() {
        Switch crossSwitch = findViewById(R.id.switch_crossOutReplacements);

        boolean bool = sharedPref.getBoolean("crossOutReplacements", true);
        crossSwitch.setChecked(bool);

        crossSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("crossOutReplacements", b);
            editor.apply();
        });
    }
    private List<ClassInfo> getData() {
        Log.e("set", "test1");
        GetClassInfo getClassInfo = new GetClassInfo();
        Thread thread = new Thread(getClassInfo);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return getClassInfo.getClassInfoList();
    }
}
