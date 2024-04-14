package com.example.planlekcji.MainApp.Timetable;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
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

import java.util.Calendar;
import java.util.List;

public class LessonFragment extends Fragment {

    public static final String TITLE = "title";
    private View view;
    private SharedPreferences sharedPreferences;
    List<LessonRow> lessonRows;
    List<ReplacementToTimetable> replacementsForTimetable;

    public LessonFragment() {}

    public LessonFragment(List<LessonRow> lessonRows, List<ReplacementToTimetable> replacementsForTimetable) {
        this.lessonRows = lessonRows;
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
        int tabNumber = Character.getNumericValue(argument.charAt(3));

        this.view = view;

        sharedPreferences = MainActivity.getContext().getSharedPreferences("sharedPrefs", 0);
        int timetableType = sharedPreferences.getInt("selectedTypeOfTimetable", 0);

        int dayNumber = 0;
        switch (tabNumber) {
            case 1:
                dayNumber = LessonRow.MONDAY;
                break;
            case 2:
                dayNumber = LessonRow.TUESDAY;
                break;
            case 3:
                dayNumber = LessonRow.WEDNESDAY;
                break;
            case 4:
                dayNumber = LessonRow.THURSDAY;
                break;
            case 5:
                dayNumber = LessonRow.FRIDAY;
                break;
        }

        int currentLesson = getCurrentLessonIndex(tabNumber);

        if (lessonRows != null) {
            for (int i = 0; i < lessonRows.size(); i++) {
                // get number of lesson and hour
                String number = lessonRows.get(i).getLessonNumbers();
                String hour = lessonRows.get(i).getLessonHours();

                // get html to change <br> tag into \n
                String html = lessonRows.get(i).getDayData().get(dayNumber);

                // for some reason when timetable is for classrooms or teachers there is br at the end of cell
                // this is to prevent that from making new lines
                if (timetableType != 0) {
                    html = html.replace("<br>", "");
                }
                // create pointer for line breaks (\n cant be used here)
                html = html.replace("<br>", "|nLine|");

                // create html document to remove unnecessary html tags
                Document doc = Jsoup.parse(html);

                // replace pointer for \n
                String data = doc.text().replace("|nLine|", "\n");

                SpannableStringBuilder str = new SpannableStringBuilder(data);

                if (shouldShowReplacementsOnTimetable()) {
                    for (ReplacementToTimetable replacement : replacementsForTimetable) {
                        if (isLessonWithReplacement(replacement, tabNumber, i)) {
                            // 0 means there is no group division
                            if (replacement.getGroupNumber() == 0) {
                                str.setSpan(new StrikethroughSpan(), 0, str.length(), 0); // apply strikethrough to entire lesson
                                appendExtraInfoIfNeeded(str, replacement.getExtraInfo());
                            } else {
                                applyStrikethroughToCorrectPartsOfSpan(str, replacement);
                            }
                        }
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
                lessonNumber.setText(number);
                lessonNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36f);
                lessonNumber.setGravity(Gravity.CENTER);
                lessonNumber.setPadding(dpToPx(10), 0, 0, 0);

                TextView lessonHours = new TextView(getActivity());
                lessonHours.setId(R.id.textViewLessonHours);
                lessonHours.setText(hour);
                lessonHours.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                lessonHours.setLayoutParams(layoutParams_matchParent2);

                TextView lessonData = new TextView(getActivity());
                lessonData.setId(R.id.textViewLessonData);
                lessonData.setText(str);
                lessonData.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
                lessonData.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                lessonData.setLayoutParams(layoutParams_matchParent3);


                lessonData.setPadding(0, 0, 0, dpToPx(16));

                if (i == currentLesson - 1) {
                    int bgColor = ContextCompat.getColor(getActivity(), R.color.primaryDark);
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
    }

    private boolean shouldShowReplacementsOnTimetable() {
        boolean visibility = sharedPreferences.getBoolean(getString(R.string.replacementVisibilityOnTimetable), false);
        int timetableType = sharedPreferences.getInt("selectedTypeOfTimetable", 0);
        return visibility && timetableType == 0;
    }

    private boolean isLessonWithReplacement(ReplacementToTimetable replacement, int tabNumber, int i) {
        return replacement.getDayNumber() == tabNumber && replacement.getLessonNumber() == i + 1;
    }

    private void applyStrikethroughToCorrectPartsOfSpan(SpannableStringBuilder str, ReplacementToTimetable replacement) {
        int beginIndex = 0;
        int foundIndex = 0;

        for (int k = 0; foundIndex != -1 && beginIndex < str.length(); k++) {
            foundIndex = str.toString().indexOf("\n", beginIndex);

            if (foundIndex == -1) {
                foundIndex = str.length();
            }

            if (k == replacement.getGroupNumber() - 1) {
                str.setSpan(new StrikethroughSpan(), beginIndex, foundIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                appendExtraInfoIfNeeded(str, replacement.getExtraInfo());
            }

            beginIndex = foundIndex + 1;
        }
    }

    private void appendExtraInfoIfNeeded(SpannableStringBuilder str, String extraInfo) {
        if(extraInfo.equals("")) return;

        str.append("\n"+extraInfo);
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
            "21:50",
            "22:40",
            "23:30",
            "00:20"
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