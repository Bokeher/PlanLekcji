package com.example.planlekcji.MainApp.Timetable;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StrikethroughSpan;
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

import com.example.planlekcji.MainActivity;
import com.example.planlekcji.MainApp.Replacements.ReplacementToTimetable;
import com.example.planlekcji.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LessonFragment extends Fragment {

    public static final String TITLE = "title";
    private Lessons timetableData;
    private View view;
    private SharedPreferences sharedPreferences;
    private List<ReplacementToTimetable> replacementsForTimetable;

    public LessonFragment(Lessons timetableData, List<ReplacementToTimetable> replacementsForTimetable) {
        this.timetableData = timetableData;
        this.replacementsForTimetable = replacementsForTimetable;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

        List<Elements> dataList = null;
        switch (tabNumber) {
            case 1:
                dataList = timetableData.getMonday();
                break;
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

            // for some reason when timetable is for classrooms or teachers there is br at the end of cell
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

            // showing replacements
            boolean visibility = sharedPreferences.getBoolean(getString(R.string.replacementVisibilityOnTimetable), false);

            SpannableStringBuilder str = null;
            if(visibility) {
                List<ReplacementToTimetable> currTabReplacementList = new ArrayList<>();
                for (int j = 0; j < replacementsForTimetable.size(); j++) {
                    ReplacementToTimetable replacement = replacementsForTimetable.get(j);
                    if(replacement.getDayNumber() == tabNumber && replacement.getLessonNumber() == i+1) {
                        if(replacement.getGroupNumber() == 0) {
                            str = new SpannableStringBuilder(data);
                            str.setSpan(new StrikethroughSpan(), 0, str.length(), 0);
                        } else {
                            str = new SpannableStringBuilder();
                            String[] lines = data.split("\n");

                            for (int k = 0; k < lines.length; k++) {
                                if (k == replacement.getGroupNumber() - 1) {
                                    SpannableString spannableString = new SpannableString(lines[k]);
                                    StrikethroughSpan strikethroughSpan = new StrikethroughSpan();
                                    spannableString.setSpan(strikethroughSpan, 0, spannableString.length(), 0);
                                    str.append(spannableString);

                                    if(!replacement.getExtraInfo().equals("")) {
                                        str.append("\n"+replacement.getExtraInfo());
                                    }
                                } else {
                                    str.append(lines[k]);
                                }

                                if (k < lines.length - 1) {
                                    str.append("\n");
                                }
                            }
                        }
                    }
                }
                for (int j = 0; j < currTabReplacementList.size(); j++) {
                    Log.e("test", ""+currTabReplacementList.get(j).getLessonNumber());
                }
            }

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

            if(str != null) {
                lessonData.setText(str);
            }
            else lessonData.setText(data);
            lessonData.setPadding(0, 0, 0, dpToPx(16));

            if(currentLesson - 1 == i) {
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
     * Determines the index of the current lesson based on the current time.
     *
     * This method calculates the current lesson index by comparing the current hour
     * and minutes with the predefined ending times of lessons.
     *
     * @return The index of the current lesson or 0 if there is no active lesson.
     */
    private int getCurrentLessonIndex(int tabNumber) {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; // Monday == 1, Tuesday == 2, etc

        if (dayOfWeek != tabNumber) return 0;

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);

        String[] lessonEndTimers = {
            "8:45", // time of 1st lesson ending
            "9:35",
            "10:30",
            "11:35",
            "12:30",
            "13:25",
            "14:20",
            "15:10",
            "16:00",
            "16:50",
            "17:40",
            "18:30",
            "19:20",
            "20:10",
            "21:00",
            "21:50"
        };

        for (int i = 0; i < lessonEndTimers.length; i++) {
            String[] args = lessonEndTimers[i].split(":");
            int lessonHour = Integer.parseInt(args[0]);
            int lessonMinutes = Integer.parseInt(args[1]);

            if(hour == lessonHour) {
                if(minutes < lessonMinutes) return i + 1;
                return i + 2;
            }
        }

        return 0;
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }
}