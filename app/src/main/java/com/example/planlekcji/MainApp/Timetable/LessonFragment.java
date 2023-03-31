package com.example.planlekcji.MainApp.Timetable;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.planlekcji.MainApp.MainActivity;
import com.example.planlekcji.R;

import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LessonFragment extends Fragment {

    public static final String TITLE = "title";
    private Lessons timetableData;
    private HashMap<Integer, List<Integer>> replacementsData;
    private ArrayList<Integer> idsCards;
    private View view;
    private SharedPreferences sharedPreferences;

    public LessonFragment(Lessons timetableData, HashMap<Integer, List<Integer>> replacementsData) {
        this.timetableData = timetableData;
        this.replacementsData = replacementsData;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String argument = getArguments().getString(TITLE);
        this.view = view;
        int tabNumber = Character.getNumericValue(argument.charAt(3));

        sharedPreferences = MainActivity.getContext().getSharedPreferences("sharedPrefs",0);

        idsCards = new ArrayList<>(Arrays.asList(
                R.id.cardView1, R.id.cardView2, R.id.cardView3, R.id.cardView4,
                R.id.cardView5, R.id.cardView6, R.id.cardView7, R.id.cardView8,
                R.id.cardView9, R.id.cardView10, R.id.cardView11, R.id.cardView12
        ));

        ArrayList<Elements> dataList = timetableData.getMonday();
        switch (tabNumber) {
            case 2:
                dataList = timetableData.getTuesday();
                break;
            case 3:
                dataList = timetableData.getWednesday();
                break;
            case 4:
                dataList = timetableData.getThursday();
                break;
            case 5:
                dataList = timetableData.getFriday();
                break;
        }

        int currentLesson = getCurrentLessonIndex(tabNumber);
        String data = "";

        List<Integer> indexList = new ArrayList<>();
        if(replacementsData.containsKey(tabNumber)) {
            indexList = replacementsData.get(tabNumber);
        }

        Pattern pattern = Pattern.compile("[ABS]\\d+[\\w\\- /\\#.]+[ABS]\\d+");
        for (int i = 0; i < dataList.size(); i++) {

            String number = timetableData.getLessonNumbers().get(i).text();
            String hour = timetableData.getLessonHours().get(i).text();
            data = dataList.get(i).text();

//            if(data.contains("WP")) data = formatData(data);

            Matcher matcher = pattern.matcher(data);

            if(matcher.find()) {
                data = formatData(data);
            }

            
            LinearLayout linearLayout = view.findViewById(R.id.linearLayoutCards);

            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 0, 0, 10);

            int color = ContextCompat.getColor(getActivity(), R.color.lessonBackgroundColor);

            CardView cardView = new CardView(getActivity());
            cardView.setId(idsCards.get(i));
            cardView.setRadius(30);
            cardView.setCardBackgroundColor(color);

            ConstraintLayout.LayoutParams layoutParams_matchParent2 = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
            ConstraintLayout.LayoutParams layoutParams_matchParent3 = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);

            TextView lessonNumber = new TextView(getActivity());
            lessonNumber.setId(R.id.textViewLessonNumber);
            lessonNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36f);
            lessonNumber.setGravity(Gravity.CENTER);
            lessonNumber.setText(number);
            lessonNumber.setPadding(dpToPx(10), 0, 0, 0);

            TextView lessonHours = new TextView(getActivity());
            lessonHours.setId(R.id.textViewLessonHours);
            lessonHours.setLayoutParams(layoutParams_matchParent2);
            lessonHours.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            lessonHours.setText(hour);

            TextView lessonData = new TextView(getActivity());
            lessonData.setId(R.id.textViewLessonData);
            lessonData.setLayoutParams(layoutParams_matchParent3);
            lessonData.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            lessonData.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
            lessonData.setText(data);
            lessonData.setPadding(0, 0, 0, dpToPx(16));

            if(indexList.contains(i+1)) {
                boolean crossOut = sharedPreferences.getBoolean("crossOutReplacements", true);
                if(crossOut) strikeThroughText(lessonData);
                cardView.setCardBackgroundColor(ContextCompat.getColor(getActivity(), R.color.lessonBackgroundColorStrikedThrough));
            }
            // TODO: repair possible out of bound error

            if(currentLesson-1 == i) {
                int bgColor =  ContextCompat.getColor(getActivity(), R.color.primaryDark);
                int textColor = ContextCompat.getColor(getActivity(), R.color.black);

                cardView.setCardBackgroundColor(bgColor);
                lessonNumber.setTextColor(textColor);
                lessonHours.setTextColor(textColor);
                lessonData.setTextColor(textColor);
//                lessonNumber.setTypeface(null, Typeface.BOLD);
//                lessonHours.setTypeface(null, Typeface.BOLD);
//                lessonData.setTypeface(null, Typeface.BOLD);
            }

            ConstraintLayout constraintLayout = new ConstraintLayout(getActivity());
            constraintLayout.setId(R.id.constraintLayout);

            constraintLayout.addView(lessonNumber);
            constraintLayout.addView(lessonHours);
            constraintLayout.addView(lessonData);

            ConstraintSet constrSet = new ConstraintSet();
            constrSet.clone(constraintLayout);
            constrSet.connect(R.id.textViewLessonHours, ConstraintSet.TOP, R.id.constraintLayout, ConstraintSet.TOP, dpToPx(8));
            constrSet.connect(R.id.textViewLessonHours, ConstraintSet.LEFT, R.id.textViewLessonNumber, ConstraintSet.RIGHT, dpToPx(40));
            constrSet.connect(R.id.textViewLessonHours, ConstraintSet.RIGHT, R.id.constraintLayout, ConstraintSet.RIGHT, dpToPx(16));

            constrSet.connect(R.id.textViewLessonData, ConstraintSet.TOP, R.id.textViewLessonHours, ConstraintSet.BOTTOM, dpToPx(2));
            constrSet.connect(R.id.textViewLessonData, ConstraintSet.BOTTOM, R.id.constraintLayout, ConstraintSet.BOTTOM, dpToPx(2));
            constrSet.connect(R.id.textViewLessonData, ConstraintSet.RIGHT, R.id.constraintLayout, ConstraintSet.RIGHT, dpToPx(16));
            constrSet.connect(R.id.textViewLessonData, ConstraintSet.LEFT, R.id.textViewLessonNumber, ConstraintSet.LEFT, dpToPx(40));

            constrSet.connect(R.id.textViewLessonNumber, ConstraintSet.TOP, R.id.constraintLayout, ConstraintSet.TOP, 0);
            constrSet.connect(R.id.textViewLessonNumber, ConstraintSet.BOTTOM, R.id.constraintLayout, ConstraintSet.BOTTOM, 0);
            constrSet.applyTo(constraintLayout);

            cardView.addView(constraintLayout);
            cardView.setLayoutParams(layoutParams);
            linearLayout.addView(cardView);
        }
    }

    private void strikeThroughText(TextView textView) {
        textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    private String formatData(String data) {
        String[] arr = data.split("[ABS]\\d+");

        List<String> list = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            String elem = arr[i];
            list.add(elem.substring(0, elem.length()-1));
        }

        return String.join("\n", list);
    }

    private int getCurrentLessonIndex(int tabNumber) {
        Date date = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        int dayNumb = calendar.get(Calendar.DAY_OF_WEEK) - 1; // 1 - Monday etc
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);

//        hour = 12;
//        minutes = 12;

        if (dayNumb == tabNumber) {
            if (hour >= 6 && hour < 8) return 1;
            else if (hour == 8) {
                if (minutes < 45) return 1;
                else return 2;
            } else if (hour == 9) {
                if (minutes < 35) return 2;
                else return 3;
            } else if (hour == 10) {
                if (minutes < 30) return 3;
                else return 4;
            } else if (hour == 11) {
                if (minutes < 35) return 4;
                else return 5;
            } else if (hour == 12) {
                if (minutes < 30) return 5;
                else return 6;
            } else if (hour == 13) {
                if (minutes < 25) return 6;
                else return 7;
            } else if (hour == 14) {
                if (minutes < 20) return 7;
                else return 8;
            } else if (hour == 15) {
                if (minutes < 10) return 8;
                else return 9;
            } else if (hour == 16) {
                if (minutes < 50) return 10;
                else return 11;
            } else if (hour == 17) {
                return 11;
            }
        }
        return 0;
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }
}