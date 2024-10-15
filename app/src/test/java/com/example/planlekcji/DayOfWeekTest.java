package com.example.planlekcji;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import com.example.planlekcji.timetable.model.DayOfWeek;

import org.junit.Test;

public class DayOfWeekTest {

    @Test
    public void shouldGetDayOfWeekAsNumber() {

        int mondayAsNumber = DayOfWeek.MONDAY.getDayOfWeekAsNumber();
        int fridayAsNumber = DayOfWeek.FRIDAY.getDayOfWeekAsNumber();

        assertEquals(1, mondayAsNumber);
        assertEquals(5, fridayAsNumber);
    }

    @Test
    public void shouldGetDaysOfWeekAsNumber() {
        Integer[] daysOfWeekAsNumbers = DayOfWeek.getDaysOfWeekAsNumbers();

        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5}, daysOfWeekAsNumbers);
    }

    @Test
    public void shouldGetDayOfWeekByDayNumber() {
        DayOfWeek dayOfWeek = DayOfWeek.getDayOfWeek(1);

        assertEquals(DayOfWeek.MONDAY, dayOfWeek);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenDayNumberIsZero() {
        DayOfWeek.getDayOfWeek(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenDayNumberIsGreaterThanFive() {
        DayOfWeek.getDayOfWeek(6);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenDayNumberIsSmallerThanZero() {
        DayOfWeek.getDayOfWeek(-1);
    }
}
