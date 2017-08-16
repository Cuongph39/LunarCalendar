package com.vanpt.lunarcalendar.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vanpt.lunarcalendar.R;
import com.vanpt.lunarcalendar.activities.MainActivity;
import com.vanpt.lunarcalendar.fragments.GoToLunarDateFragment;
import com.vanpt.lunarcalendar.models.DateObject;
import com.vanpt.lunarcalendar.models.EventObject;
import com.vanpt.lunarcalendar.models.RepetitionTypeEnum;
import com.vanpt.lunarcalendar.utils.DateConverter;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by vanpt on 12/1/2016.
 */

public class MyDbHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "eventsDb.db";
    private static final String TABLE_EVENTS = "events";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_COLOR = "color";
    private static final String COLUMN_START_DATE = "start_date";
    private static final String COLUMN_END_DATE = "end_date";
    private static final String COLUMN_REPETITION = "repetition";
    private static final String COLUMN_ALL_DAY = "all_day";
    private static final String COLUMN_LOCATION = "location";

    public MyDbHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlCreateEventsTable =
                "CREATE TABLE IF NOT EXISTS " + TABLE_EVENTS + " " +
                        " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NAME + " TEXT NOT NULL DEFAULT ('Sự kiện mới'), " +
                        COLUMN_COLOR + " INTEGER NOT NULL DEFAULT (" + R.color.colorRed + "), " +
                        COLUMN_START_DATE + " BIGINT NOT NULL, " +
                        COLUMN_END_DATE + " BIGINT, " +
                        COLUMN_REPETITION + " INTEGER NOT NULL DEFAULT (0), " +
                        COLUMN_ALL_DAY + " INTEGER NOT NULL DEFAULT (0), " +
                        COLUMN_LOCATION + " TEXT " +
                        ")";
        db.execSQL(sqlCreateEventsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        onCreate(db);
    }

    public void addEvent(EventObject event) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, event.getName());
        values.put(COLUMN_COLOR, event.getColor());
        long startDate = event.getFromDate().getTime();
        long endDate = event.getToDate().getTime();
        if (event.isAllDayEvent()) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(event.getFromDate());
            cal.set(
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH),
                    8,0
            );
            startDate = cal.getTime().getTime();
            cal.setTime(event.getToDate());
            cal.set(
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH),
                    8,0
            );
            endDate = cal.getTime().getTime();
        }
        values.put(COLUMN_START_DATE, startDate);
        values.put(COLUMN_END_DATE, endDate);
        values.put(COLUMN_REPETITION, event.getRepetitionType().getValue());
        int allDay = event.isAllDayEvent()? 1 : 0;
        values.put(COLUMN_ALL_DAY, allDay);
        values.put(COLUMN_LOCATION, event.getLocation());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_EVENTS, null, values);
        db.close();
    }

    public EventObject findEvent(int id) {
        String query = "SELECT * FROM " + TABLE_EVENTS +
                " WHERE " + COLUMN_ID + "=" + id;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        EventObject event = new EventObject("");
        if (cursor.moveToFirst()) {
            event.setId(cursor.getInt(0));
            event.setName(cursor.getString(1));
            event.setColor(cursor.getInt(2));
            event.setLocation(cursor.getString(7));
            event.setFromDate(new Date(cursor.getLong(3)));
            event.setToDate(new Date(cursor.getLong(4)));
            event.setRepetitionType(cursor.getInt(5));
            event.setAllDayEvent(cursor.getInt(6) != 0);
        } else {
            event = null;
        }
        cursor.close();
        db.close();
        return event;
    }

    public EventObject[] findEvent(int day, int month, int year) {
        ArrayList<EventObject> events = new ArrayList<EventObject>();
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day);
        String query = "SELECT * FROM " + TABLE_EVENTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Date theDate = cal.getTime();
        while (cursor.moveToNext()) {
            Date startDate = new Date(cursor.getLong(3));
            Date endDate = new Date(cursor.getLong(4));
            int repetitionType = cursor.getInt(5);

            try {
                if (includes(startDate, endDate, repetitionType, theDate)) {
                    EventObject event = new EventObject(cursor.getString(1));
                    event.setId(cursor.getInt(0));
                    event.setColor(cursor.getInt(2));
                    event.setRepetitionType(cursor.getInt(5));
                    event.setAllDayEvent(cursor.getInt(6) != 0);
                    event.setLocation(cursor.getString(7));
                    event.setFromDate(startDate);
                    event.setToDate(endDate);
                    events.add(event);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        db.close();
        return events.toArray(new EventObject[0]);
    }

    public int findEventCount(int day, int month, int year) {
        int count = 0;
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day);
        String query = "SELECT * FROM " + TABLE_EVENTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Date theDate = cal.getTime();
        while (cursor.moveToNext()) {
            Date startDate = new Date(cursor.getLong(3));
            Date endDate = new Date(cursor.getLong(4));
            int repetitionType = cursor.getInt(5);
            try {
                if (includes(startDate, endDate, repetitionType, theDate)) {
                    count += 1;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        db.close();
        return count;
    }

    public void deleteEvent(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EVENTS, COLUMN_ID + "= ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    public void updateEvent(EventObject event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, event.getName());
        contentValues.put(COLUMN_COLOR, event.getColor());
        contentValues.put(COLUMN_START_DATE, event.getFromDate().getTime());
        contentValues.put(COLUMN_END_DATE, event.getToDate().getTime());
        contentValues.put(COLUMN_REPETITION, event.getRepetitionType().getValue());
        contentValues.put(COLUMN_ALL_DAY, event.isAllDayEvent() ? 1 : 0);
        contentValues.put(COLUMN_LOCATION, event.getLocation());
        db.update(TABLE_EVENTS, contentValues, COLUMN_ID + " = ?",
                new String[]{String.valueOf(event.getId())});
        db.close();
    }

    private boolean includes(Date startDate, Date endDate, int repetitionType, Date theDate) throws Exception {
        Calendar calStart = Calendar.getInstance();
        calStart.setTime(startDate);
        calStart.set(calStart.get(Calendar.YEAR), calStart.get(Calendar.MONTH), calStart.get(Calendar.DAY_OF_MONTH), 0, 0);
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(endDate);
        calEnd.set(calEnd.get(Calendar.YEAR), calEnd.get(Calendar.MONTH), calEnd.get(Calendar.DAY_OF_MONTH), 23, 59);
        if (!(calStart.getTime().getTime() > theDate.getTime() ||
                calEnd.getTime().getTime() < theDate.getTime())) {
            return true;
        }
        if (repetitionType == 0) {
            return !(startDate.getTime() > theDate.getTime() ||
                    endDate.getTime() < theDate.getTime());
        } else if (repetitionType == 1) {
            return true;
        } else if (repetitionType == 2) {
            long dateDiff = DateConverter.getDateDiff(startDate, theDate);
            return dateDiff % 7 == 0;
        } else if (repetitionType == 3) {
            calStart.setTime(startDate);
            DateObject tempDate = new DateObject(
                    calStart.get(Calendar.DAY_OF_MONTH),
                    calStart.get(Calendar.MONTH) + 1,
                    calStart.get(Calendar.YEAR));
            DateObject lunarStartDate = DateConverter.convertSolar2Lunar(tempDate, MainActivity.timeZone);
            Calendar cal = Calendar.getInstance();
            cal.setTime(theDate);
            tempDate = new DateObject(
                    cal.get(Calendar.DAY_OF_MONTH),
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.YEAR));
            DateObject theLunarDate = DateConverter.convertSolar2Lunar(tempDate, MainActivity.timeZone);
            return lunarStartDate.getDay() == theLunarDate.getDay();
        } else if (repetitionType == 4) {
            calStart.setTime(startDate);
            DateObject tempDate = new DateObject(
                    calStart.get(Calendar.DAY_OF_MONTH),
                    calStart.get(Calendar.MONTH) + 1,
                    calStart.get(Calendar.YEAR));
            DateObject lunarStartDate = DateConverter.convertSolar2Lunar(tempDate, MainActivity.timeZone);
            Calendar cal = Calendar.getInstance();
            cal.setTime(theDate);
            tempDate = new DateObject(
                    cal.get(Calendar.DAY_OF_MONTH),
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.YEAR));
            DateObject theLunarDate = DateConverter.convertSolar2Lunar(tempDate, MainActivity.timeZone);
            return lunarStartDate.getMonth() == theLunarDate.getMonth() &&
                    lunarStartDate.getDay() == theLunarDate.getDay();
        } else {
            return false;
        }
    }

    public void addCommonEvents(InputStream stream) {
        try {
            InputStreamReader sr = new InputStreamReader(stream);
            BufferedReader br = new BufferedReader(sr);
            String line = br.readLine();
            while (line != null) {
                String[] parts = line.split(";");
                Calendar cal = Calendar.getInstance();
                EventObject event = new EventObject(parts[0]);
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.FRENCH);
                Date result = df.parse(parts[1]);
                cal.setTime(result);
                DateObject lunarDate = new DateObject(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, 2000);
                result = df.parse(parts[2]);
                cal.setTime(result);
                DateObject toLunarDate = new DateObject(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, 2000);
                DateObject solarDate = DateConverter.convertLunar2Solar(lunarDate, MainActivity.timeZone);
                DateObject toSolarDate = DateConverter.convertLunar2Solar(toLunarDate, MainActivity.timeZone);
                cal.set(solarDate.getYear(), solarDate.getMonth() - 1, solarDate.getDay());
                event.setFromDate(cal.getTime());
                cal.set(toSolarDate.getYear(), toSolarDate.getMonth() - 1, toSolarDate.getDay());
                event.setToDate(cal.getTime());
                boolean allDayEvent = Boolean.parseBoolean(parts[3]);
                event.setAllDayEvent(allDayEvent);
                int repetition = Integer.parseInt(parts[4]);
                event.setRepetitionType(repetition);
                event.setLocation(parts[5]);
                if (!parts[6].equals("1")) {
                    event.setColor(R.color.colorBlue);
                }
                addEvent(event);

                line = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
