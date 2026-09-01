package com.example.planlekcji.timetable.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.planlekcji.MainActivity;
import com.example.planlekcji.R;
import com.example.planlekcji.ckziu_elektryk.client.timetable.SchoolEntryType;
import com.example.planlekcji.ckziu_elektryk.client.timetable.lesson.GroupLesson;
import com.example.planlekcji.ckziu_elektryk.client.timetable.lesson.Lesson;
import com.example.planlekcji.ckziu_elektryk.client.timetable.lesson.LessonDetails;
import com.example.planlekcji.ckziu_elektryk.client.timetable.lesson.LessonDuration;
import com.example.planlekcji.ckziu_elektryk.client.timetable.lesson.SchoolClass;
import com.example.planlekcji.ckziu_elektryk.client.timetable.lesson.SingleLesson;
import com.example.planlekcji.settings.GroupPreferenceManager;
import com.example.planlekcji.utils.DayOfWeek;
import com.example.planlekcji.utils.EmptyStateHelper;
import com.example.planlekcji.utils.EmptyStateType;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class LessonFragment extends Fragment {
    public static final String TITLE = "title";
    private Map<DayOfWeek, List<Lesson>> timetableMap;

    public LessonFragment() {
    }

    public LessonFragment(Map<DayOfWeek, List<Lesson>> timetableMap) {
        this.timetableMap = timetableMap;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lesson, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateCards();
    }

    @Override
    public void onResume() {
        super.onResume();
        populateCards();
    }

    private void populateCards() {
        if (!isAdded() || getView() == null) return;

        Context context = requireContext();
        LinearLayout layout = getView().findViewById(R.id.linearLayoutCards);
        if (layout == null) return;
        layout.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(context);

        if (getArguments() == null) return;
        String argument = getArguments().getString(TITLE);
        if (argument == null || argument.length() < 4) return;
        int tabNumber = Character.getNumericValue(argument.charAt(3));

        DayOfWeek thisDayNumber = DayOfWeek.getDayOfWeek(tabNumber);

        if (timetableMap == null || timetableMap.get(DayOfWeek.MONDAY) == null) {
            return;
        }

        List<Lesson> lessonList = timetableMap.get(thisDayNumber);
        if (lessonList == null) {
            Log.e("TimetableLessonFragment", "lessonList is empty");
            return;
        }

        SharedPreferences sharedPref = MainActivity.getContext().getSharedPreferences("sharedPrefs", 0);
        SchoolEntryType timetableType = MainActivity.getTimetableType();
        String classToken = (timetableType == SchoolEntryType.CLASSES) ? sharedPref.getString(getString(R.string.classTokenKey), "") : "";
        boolean hideUnselected = GroupPreferenceManager.isHideUnselected(sharedPref);
        boolean isClassesTimetable = (timetableType == SchoolEntryType.CLASSES) && !classToken.isEmpty();

        Map<String, TimetableCardItemBuilder> cardMap = new LinkedHashMap<>();
        Set<Integer> coveredNumbers = new HashSet<>();
        int minLessonNumber = Integer.MAX_VALUE;
        int maxLessonNumber = Integer.MIN_VALUE;

        for (Lesson lesson : lessonList) {
            List<Integer> nums = lesson.getLessonsNumbers();
            if (nums == null || nums.isEmpty()) continue;

            List<LessonDetails> rawDetailsList;
            if (lesson instanceof SingleLesson) {
                rawDetailsList = Collections.singletonList(((SingleLesson) lesson).getDetails());
            } else if (lesson instanceof GroupLesson) {
                rawDetailsList = ((GroupLesson) lesson).getLessonsDetails();
            } else {
                rawDetailsList = Collections.emptyList();
            }

            for (int num : nums) {
                coveredNumbers.add(num);
                if (num < minLessonNumber) minLessonNumber = num;
                if (num > maxLessonNumber) maxLessonNumber = num;
            }

            int firstNum = Collections.min(nums);
            int lastNum = Collections.max(nums);
            boolean isMerged = (firstNum != lastNum);
            String slotKey = isMerged ? (firstNum + "_" + lastNum) : String.valueOf(firstNum);

            TimetableCardItemBuilder builder = cardMap.get(slotKey);
            if (builder == null) {
                builder = new TimetableCardItemBuilder(isMerged, firstNum, lastNum);
                cardMap.put(slotKey, builder);
            }

            if (lesson.getDuration() != null) {
                builder.updateDuration(lesson.getDuration(), isMerged);
            }

            if (lesson.getDuration() != null && isLessonCurrentlyActive(tabNumber, lesson.getDuration(), isMerged)) {
                builder.isCurrent = true;
            }

            for (LessonDetails details : rawDetailsList) {
                if (details != null && !builder.containsDetails(details)) {
                    builder.detailsList.add(details);
                }
            }
        }

        List<TimetableCardItem> cardItems = new ArrayList<>();
        for (TimetableCardItemBuilder builder : cardMap.values()) {
            cardItems.add(builder.build());
        }

        // No lessons scheduled for this day
        if (cardItems.isEmpty() || coveredNumbers.isEmpty()) {
            layout.addView(EmptyStateHelper.create(inflater, layout, EmptyStateType.DAY_OFF));
            return;
        }

        // Add empty data entries for gaps (okienka) between lessons
        for (int i = minLessonNumber; i <= maxLessonNumber; i++) {
            if (!coveredNumbers.contains(i)) {
                cardItems.add(new TimetableCardItem(false, i, i, String.valueOf(i), "", Collections.emptyList(), false, i));
            }
        }

        cardItems.sort(Comparator.comparingInt(item -> item.sortOrder));

        for (TimetableCardItem item : cardItems) {
            MaterialCardView cardView = (MaterialCardView) inflater.inflate(R.layout.lesson_card, layout, false);

            TextView lessonHoursText = cardView.findViewById(R.id.textViewLessonHours);
            TextView lessonNumber = cardView.findViewById(R.id.textViewLessonNumber);
            LinearLayout layoutMergedLessonNumber = cardView.findViewById(R.id.layout_mergedLessonNumber);
            TextView lessonNumberStart = cardView.findViewById(R.id.textViewLessonNumberStart);
            TextView lessonNumberEnd = cardView.findViewById(R.id.textViewLessonNumberEnd);
            View mergedLessonDivider = cardView.findViewById(R.id.view_mergedLessonDivider);
            View divider = cardView.findViewById(R.id.lessonCard_divider);
            LinearLayout lessonsContainer = cardView.findViewById(R.id.layout_lessonsContainer);
            TextView viewLessonData = cardView.findViewById(R.id.textViewLessonData);

            lessonHoursText.setText(item.timeRange);

            if (item.isMerged) {
                lessonNumber.setVisibility(View.GONE);
                layoutMergedLessonNumber.setVisibility(View.VISIBLE);
                lessonNumberStart.setText(String.valueOf(item.startNumber));
                lessonNumberEnd.setText(String.valueOf(item.endNumber));
                lessonNumberStart.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
                lessonNumberEnd.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
            } else {
                lessonNumber.setVisibility(View.VISIBLE);
                layoutMergedLessonNumber.setVisibility(View.GONE);
                lessonNumber.setText(item.singleNumberText);
                lessonNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
            }

            List<LessonDetails> detailsList = item.detailsList;
            boolean isCurrentLesson = item.isCurrent;

            if (detailsList == null || detailsList.isEmpty()) {
                viewLessonData.setVisibility(View.VISIBLE);
                viewLessonData.setText("");
            } else if (hideUnselected) {
                // Hide unselected mode: filter out hidden lessons
                List<LessonDetails> visibleDetails = new ArrayList<>();
                for (LessonDetails d : detailsList) {
                    if (!isClassesTimetable || !GroupPreferenceManager.isLessonHidden(sharedPref, classToken, thisDayNumber, item.startNumber, d)) {
                        visibleDetails.add(d);
                    }
                }

                if (visibleDetails.isEmpty()) {
                    // All lessons in this card were hidden by the user -> show empty gap
                    viewLessonData.setVisibility(View.VISIBLE);
                    viewLessonData.setText("");

                    if (isClassesTimetable) {
                        cardView.setOnLongClickListener(v -> {
                            if (GroupPreferenceManager.isLockSelection(sharedPref)) {
                                Toast.makeText(context, R.string.settings_groups_editing_locked, Toast.LENGTH_SHORT).show();
                                return true;
                            }
                            v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                            for (LessonDetails d : detailsList) {
                                GroupPreferenceManager.setLessonHidden(sharedPref, classToken, thisDayNumber, item.startNumber, d, false);
                            }
                            populateCards();
                            return true;
                        });
                    }
                } else {
                    viewLessonData.setVisibility(View.GONE);

                    for (int idx = 0; idx < visibleDetails.size(); idx++) {
                        LessonDetails details = visibleDetails.get(idx);

                        if (idx > 0) {
                            View itemDivider = new View(context);
                            LinearLayout.LayoutParams divParams = new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    (int) (1 * getResources().getDisplayMetrics().density)
                            );
                            int marginV = (int) (3 * getResources().getDisplayMetrics().density);
                            divParams.setMargins(0, marginV, 0, marginV);
                            itemDivider.setLayoutParams(divParams);
                            itemDivider.setBackgroundColor(isCurrentLesson ? Color.parseColor("#44FFC107") : Color.parseColor("#1FFFFFFF"));
                            lessonsContainer.addView(itemDivider);
                        }

                        View rowView = inflater.inflate(R.layout.item_lesson_entry, lessonsContainer, false);
                        TextView textSubject = rowView.findViewById(R.id.textView_lessonSubject);
                        TextView textMeta = rowView.findViewById(R.id.textView_lessonMeta);

                        textSubject.setText(getSubjectText(details));
                        textMeta.setText(getMetaText(details));

                        if (isCurrentLesson) {
                            textSubject.setTextColor(Color.WHITE);
                            textSubject.setTypeface(null, Typeface.BOLD);
                            textMeta.setTextColor(Color.parseColor("#FFD54F"));
                        }

                        if (isClassesTimetable) {
                            rowView.setOnLongClickListener(v -> {
                                if (GroupPreferenceManager.isLockSelection(sharedPref)) {
                                    Toast.makeText(context, R.string.settings_groups_editing_locked, Toast.LENGTH_SHORT).show();
                                    return true;
                                }
                                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

                                if (detailsList.size() == 1) {
                                    GroupPreferenceManager.setLessonHidden(sharedPref, classToken, thisDayNumber, item.startNumber, details, true);
                                } else {
                                    if (visibleDetails.size() == 1) {
                                        // Only this one was visible -> reset all in card to visible
                                        for (LessonDetails d : detailsList) {
                                            GroupPreferenceManager.setLessonHidden(sharedPref, classToken, thisDayNumber, item.startNumber, d, false);
                                        }
                                    } else {
                                        // Select this one -> keep this, hide other details in this card
                                        for (LessonDetails d : detailsList) {
                                            boolean hide = (d != details);
                                            GroupPreferenceManager.setLessonHidden(sharedPref, classToken, thisDayNumber, item.startNumber, d, hide);
                                        }
                                    }
                                }
                                populateCards();
                                return true;
                            });
                        }

                        lessonsContainer.addView(rowView);
                    }
                }
            } else {
                // Dim unselected mode: render all lessons, dim hidden ones
                viewLessonData.setVisibility(View.GONE);

                for (int idx = 0; idx < detailsList.size(); idx++) {
                    LessonDetails details = detailsList.get(idx);

                    if (idx > 0) {
                        View itemDivider = new View(context);
                        LinearLayout.LayoutParams divParams = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                (int) (1 * getResources().getDisplayMetrics().density)
                        );
                        int marginV = (int) (3 * getResources().getDisplayMetrics().density);
                        divParams.setMargins(0, marginV, 0, marginV);
                        itemDivider.setLayoutParams(divParams);
                        itemDivider.setBackgroundColor(isCurrentLesson ? Color.parseColor("#44FFC107") : Color.parseColor("#1FFFFFFF"));
                        lessonsContainer.addView(itemDivider);
                    }

                    View rowView = inflater.inflate(R.layout.item_lesson_entry, lessonsContainer, false);
                    TextView textSubject = rowView.findViewById(R.id.textView_lessonSubject);
                    TextView textMeta = rowView.findViewById(R.id.textView_lessonMeta);

                    textSubject.setText(getSubjectText(details));
                    textMeta.setText(getMetaText(details));

                    boolean isHidden = isClassesTimetable && GroupPreferenceManager.isLessonHidden(sharedPref, classToken, thisDayNumber, item.startNumber, details);

                    if (isHidden) {
                        rowView.setAlpha(0.35f);
                        textSubject.setTextColor(Color.parseColor("#9E9E9E"));
                        textMeta.setTextColor(Color.parseColor("#757575"));
                    } else if (isCurrentLesson) {
                        textSubject.setTextColor(Color.WHITE);
                        textSubject.setTypeface(null, Typeface.BOLD);
                        textMeta.setTextColor(Color.parseColor("#FFD54F"));
                    }

                    if (isClassesTimetable) {
                        rowView.setOnLongClickListener(v -> {
                            if (GroupPreferenceManager.isLockSelection(sharedPref)) {
                                Toast.makeText(context, R.string.settings_groups_editing_locked, Toast.LENGTH_SHORT).show();
                                return true;
                            }
                            v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

                            if (detailsList.size() == 1) {
                                GroupPreferenceManager.toggleLessonHidden(sharedPref, classToken, thisDayNumber, item.startNumber, details);
                            } else {
                                boolean isThisCurrentlyActive = !GroupPreferenceManager.isLessonHidden(sharedPref, classToken, thisDayNumber, item.startNumber, details);
                                boolean areAllOthersHidden = true;
                                for (LessonDetails d : detailsList) {
                                    if (d != details && !GroupPreferenceManager.isLessonHidden(sharedPref, classToken, thisDayNumber, item.startNumber, d)) {
                                        areAllOthersHidden = false;
                                        break;
                                    }
                                }

                                if (isThisCurrentlyActive && areAllOthersHidden) {
                                    // Deselect: unhide all lessons in this card
                                    for (LessonDetails d : detailsList) {
                                        GroupPreferenceManager.setLessonHidden(sharedPref, classToken, thisDayNumber, item.startNumber, d, false);
                                    }
                                } else {
                                    // Select this lesson: keep this active, hide/dim all others in this card
                                    for (LessonDetails d : detailsList) {
                                        boolean hide = (d != details);
                                        GroupPreferenceManager.setLessonHidden(sharedPref, classToken, thisDayNumber, item.startNumber, d, hide);
                                    }
                                }
                            }
                            populateCards();
                            return true;
                        });
                    }

                    lessonsContainer.addView(rowView);
                }
            }

            // Highlight current lesson
            if (isCurrentLesson) {
                int goldColor = ContextCompat.getColor(requireContext(), R.color.primaryDark);
                int darkGoldBg = Color.parseColor("#2E250A");
                int strokeColor = Color.parseColor("#99FFC107");
                int strokeWidthPx = (int) (1.5f * getResources().getDisplayMetrics().density);

                cardView.setCardBackgroundColor(darkGoldBg);
                cardView.setStrokeColor(strokeColor);
                cardView.setStrokeWidth(strokeWidthPx);

                lessonNumber.setTextColor(goldColor);
                lessonNumber.setTypeface(null, Typeface.BOLD);

                if (item.isMerged) {
                    lessonNumberStart.setTextColor(goldColor);
                    lessonNumberStart.setTypeface(null, Typeface.BOLD);
                    lessonNumberEnd.setTextColor(goldColor);
                    lessonNumberEnd.setTypeface(null, Typeface.BOLD);
                    if (mergedLessonDivider != null) {
                        mergedLessonDivider.setBackgroundColor(strokeColor);
                    }
                }

                lessonHoursText.setTextColor(Color.parseColor("#FFD54F"));
                lessonHoursText.setAlpha(1.0f);

                if (divider != null) {
                    divider.setBackgroundColor(strokeColor);
                }
            }

            layout.addView(cardView);
        }
    }

    private String getSubjectText(LessonDetails lessonDetails) {
        return lessonDetails.getSubject().name();
    }

    private String getMetaText(LessonDetails lessonDetails) {
        SchoolEntryType timetableType = MainActivity.getTimetableType();
        String classroomName = lessonDetails.getClassroom();
        String teacherName = lessonDetails.getTeacher();
        String schoolClassName = "";
        if (lessonDetails.getSchoolClasses() != null && !lessonDetails.getSchoolClasses().isEmpty()) {
            schoolClassName = lessonDetails.getSchoolClasses().stream()
                    .map(SchoolClass::shortcut)
                    .collect(Collectors.joining(", "));
        } else if (lessonDetails.getSchoolClass().isPresent()) {
            schoolClassName = lessonDetails.getSchoolClass().get().shortcut();
        }

        if (timetableType == SchoolEntryType.CLASSES) {
            StringBuilder sb = new StringBuilder();
            if (!classroomName.isEmpty()) sb.append(classroomName);
            if (!teacherName.isEmpty()) {
                if (sb.length() > 0) sb.append(" · ");
                sb.append(teacherName);
            }
            return sb.toString();
        } else if (timetableType == SchoolEntryType.TEACHERS) {
            StringBuilder sb = new StringBuilder();
            if (!schoolClassName.isEmpty()) sb.append(schoolClassName);
            if (!classroomName.isEmpty()) {
                if (sb.length() > 0) sb.append(" · ");
                sb.append(classroomName);
            }
            return sb.toString();
        } else {
            StringBuilder sb = new StringBuilder();
            if (!schoolClassName.isEmpty()) sb.append(schoolClassName);
            if (!teacherName.isEmpty()) {
                if (sb.length() > 0) sb.append(" · ");
                sb.append(teacherName);
            }
            return sb.toString();
        }
    }

    private static int getLessonStartMinutes(LessonDuration duration, boolean isMerged) {
        if (duration == null || duration.startTime() == null || duration.endTime() == null) {
            return 0;
        }
        int startMins = duration.startTime().getHours() * 60 + duration.startTime().getMinutes();
        int endMins = duration.endTime().getHours() * 60 + duration.endTime().getMinutes();
        if (!isMerged && (endMins - startMins > 55)) {
            return endMins - 45;
        }
        return startMins;
    }

    private static int getLessonEndMinutes(LessonDuration duration) {
        if (duration == null || duration.endTime() == null) {
            return 0;
        }
        return duration.endTime().getHours() * 60 + duration.endTime().getMinutes();
    }

    private static String formatTimeRange(LessonDuration duration, boolean isMerged) {
        if (duration == null || duration.startTime() == null || duration.endTime() == null) {
            return "";
        }
        int startMins = getLessonStartMinutes(duration, isMerged);
        int endMins = getLessonEndMinutes(duration);

        int sH = startMins / 60;
        int sM = startMins % 60;
        int eH = endMins / 60;
        int eM = endMins % 60;

        return String.format(Locale.US, "%02d:%02d - %02d:%02d", sH, sM, eH, eM);
    }

    /**
     * Determines whether the given lesson is currently active based on system time and lesson duration.
     *
     * @param tabNumber Tab day index (1 = Monday, 2 = Tuesday, etc.)
     * @param duration  The lesson duration
     * @param isMerged  Whether this is a merged multi-hour lesson
     * @return True if the lesson is currently active.
     */
    private boolean isLessonCurrentlyActive(int tabNumber, LessonDuration duration, boolean isMerged) {
        if (duration == null || duration.startTime() == null || duration.endTime() == null) {
            return false;
        }

        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; // Monday == 1, Tuesday == 2, etc

        if (dayOfWeek != tabNumber) return false;

        int currentMinutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
        int startMinutes = getLessonStartMinutes(duration, isMerged);
        int endMinutes = getLessonEndMinutes(duration);

        return currentMinutes >= startMinutes && currentMinutes <= endMinutes;
    }

    private static class TimetableCardItemBuilder {
        final boolean isMerged;
        final int startNumber;
        final int endNumber;
        final List<LessonDetails> detailsList = new ArrayList<>();
        LessonDuration bestDuration;
        boolean isCurrent = false;

        TimetableCardItemBuilder(boolean isMerged, int startNumber, int endNumber) {
            this.isMerged = isMerged;
            this.startNumber = startNumber;
            this.endNumber = endNumber;
        }

        void updateDuration(LessonDuration duration, boolean isMerged) {
            if (duration == null || duration.startTime() == null || duration.endTime() == null) return;
            if (bestDuration == null) {
                bestDuration = duration;
                return;
            }
            int currentSpan = (bestDuration.endTime().getHours() * 60 + bestDuration.endTime().getMinutes())
                    - (bestDuration.startTime().getHours() * 60 + bestDuration.startTime().getMinutes());
            int newSpan = (duration.endTime().getHours() * 60 + duration.endTime().getMinutes())
                    - (duration.startTime().getHours() * 60 + duration.startTime().getMinutes());

            if (!isMerged && newSpan <= 55 && currentSpan > 55) {
                bestDuration = duration;
            }
        }

        boolean containsDetails(LessonDetails newDetails) {
            String newSub = GroupPreferenceManager.getSubjectName(newDetails);
            String newTeacher = GroupPreferenceManager.getTeacher(newDetails);
            String newRoom = GroupPreferenceManager.getClassroom(newDetails);
            for (LessonDetails d : detailsList) {
                if (GroupPreferenceManager.getSubjectName(d).equals(newSub)
                        && GroupPreferenceManager.getTeacher(d).equals(newTeacher)
                        && GroupPreferenceManager.getClassroom(d).equals(newRoom)) {
                    return true;
                }
            }
            return false;
        }

        TimetableCardItem build() {
            String singleNumberText = isMerged ? "" : String.valueOf(startNumber);
            String timeRange = formatTimeRange(bestDuration, isMerged);
            return new TimetableCardItem(isMerged, startNumber, endNumber, singleNumberText, timeRange, detailsList, isCurrent, startNumber);
        }
    }

    private record TimetableCardItem(
            boolean isMerged,
            int startNumber,
            int endNumber,
            String singleNumberText,
            String timeRange,

            List<LessonDetails> detailsList,
            boolean isCurrent,

            int sortOrder
    ) { }
}