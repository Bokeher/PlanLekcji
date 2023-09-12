package com.example.planlekcji.MainApp.Timetable;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
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

import com.example.planlekcji.MainActivity;
import com.example.planlekcji.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

public class LessonFragment extends Fragment {

    public static final String TITLE = "title";
    private Lessons timetableData;
    private View view;
    private SharedPreferences sharedPreferences;

    public LessonFragment(Lessons timetableData) {
        this.timetableData = timetableData;
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
        int timetableType = sharedPreferences.getInt("selectedTypeOfTimetable", 0);

        List<Elements> dataList = timetableData.getMonday();
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

        for (int i = 0; i < dataList.size(); i++) {
            // get number of lesson and hour
            String number = timetableData.getLessonNumbers().get(i).text();
            String hour = timetableData.getLessonHours().get(i).text();

            // get html to change <br> tag into \n
            String html = dataList.get(i).html();

            // for some reason when timetable is for classrooms and teacher there is br at the end of cell
            // this is to prevent that from making new lines
            if(timetableType == 1 || timetableType == 2) {
                html = html.replace("<br>", "");
//                html = html.replace(",", "<br>");
            }
            // create pointer for \n (\n cant be used here)
            html = html.replace("<br>", "|nLine|");


            // create html document to later change it into text
            Document doc = Jsoup.parse(html);

            // replace pointer for \n
            String data = doc.text().replace("|nLine|", "\n");

            LinearLayout linearLayout = view.findViewById(R.id.linearLayoutCards);

            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 0, 0, 10);

            int color = ContextCompat.getColor(getActivity(), R.color.lessonBackgroundColor);

            CardView cardView = new CardView(getActivity());
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

            // might be some out of bound exception but works for now
            if(currentLesson-1 == i) {
                int bgColor =  ContextCompat.getColor(getActivity(), R.color.primaryDark);
                int textColor = ContextCompat.getColor(getActivity(), R.color.black);

                cardView.setCardBackgroundColor(bgColor);
                lessonNumber.setTextColor(textColor);
                lessonHours.setTextColor(textColor);
                lessonData.setTextColor(textColor);
                lessonNumber.setTypeface(null, Typeface.BOLD);
                lessonHours.setTypeface(null, Typeface.BOLD);
                lessonData.setTypeface(null, Typeface.BOLD);
            }

            ConstraintLayout constraintLayout = new ConstraintLayout(getActivity());
            constraintLayout.setId(R.id.constraintLayout);

            constraintLayout.addView(lessonNumber);
            constraintLayout.addView(lessonHours);
            constraintLayout.addView(lessonData);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.connect(R.id.textViewLessonHours, ConstraintSet.TOP, R.id.constraintLayout, ConstraintSet.TOP, dpToPx(8));
            constraintSet.connect(R.id.textViewLessonHours, ConstraintSet.LEFT, R.id.textViewLessonNumber, ConstraintSet.RIGHT, dpToPx(40));
            constraintSet.connect(R.id.textViewLessonHours, ConstraintSet.RIGHT, R.id.constraintLayout, ConstraintSet.RIGHT, dpToPx(16));

            constraintSet.connect(R.id.textViewLessonData, ConstraintSet.TOP, R.id.textViewLessonHours, ConstraintSet.BOTTOM, dpToPx(2));
            constraintSet.connect(R.id.textViewLessonData, ConstraintSet.BOTTOM, R.id.constraintLayout, ConstraintSet.BOTTOM, dpToPx(2));
            constraintSet.connect(R.id.textViewLessonData, ConstraintSet.RIGHT, R.id.constraintLayout, ConstraintSet.RIGHT, dpToPx(16));
            constraintSet.connect(R.id.textViewLessonData, ConstraintSet.LEFT, R.id.textViewLessonNumber, ConstraintSet.LEFT, dpToPx(40));

            constraintSet.connect(R.id.textViewLessonNumber, ConstraintSet.TOP, R.id.constraintLayout, ConstraintSet.TOP, 0);
            constraintSet.connect(R.id.textViewLessonNumber, ConstraintSet.BOTTOM, R.id.constraintLayout, ConstraintSet.BOTTOM, 0);
            constraintSet.applyTo(constraintLayout);

            cardView.addView(constraintLayout);
            cardView.setLayoutParams(layoutParams);
            linearLayout.addView(cardView);
        }
    }

    /**
     * Calculates the current lesson index for a given tab number (day of the week).
     * @param tabNumber The tab number representing the day of the week (e.g., 1 for Monday).
     * @return The index of the current lesson (1 to 11) or 0 if there is no ongoing lesson.
     */
    private int getCurrentLessonIndex(int tabNumber) {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; // 1 - Monday etc
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);

        // TODO: make this automatic, because there are classes with more than 11 lessons
        if (dayOfWeek == tabNumber) {
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