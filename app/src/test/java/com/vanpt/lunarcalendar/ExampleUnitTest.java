package com.vanpt.lunarcalendar;

import com.vanpt.lunarcalendar.models.DateObject;
import com.vanpt.lunarcalendar.utils.DateConverter;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        Calendar calendar = Calendar.getInstance();
        int lastDate = calendar.getActualMaximum(Calendar.DATE);

        calendar.set(Calendar.DATE, lastDate);
        int lastDay = calendar.get(Calendar.DAY_OF_WEEK);

        System.out.println("Last Date: " + calendar.getTime());

        System.out.println("Last Day : " + lastDay);
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test_DateConverter() throws Exception {
        DateObject solarDate = new DateObject(
                23,
                11,
                2016,
                0);
        DateObject lunarDate = DateConverter.convertSolar2Lunar(solarDate, 7);
        assertEquals(24, lunarDate.getDay());
        assertEquals(10, lunarDate.getMonth());
        assertEquals(2016, lunarDate.getYear());
    }
}