package com.vanpt.lunarcalendar.models;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.vanpt.lunarcalendar.activities.MainActivity;
import com.vanpt.lunarcalendar.R;
import com.vanpt.lunarcalendar.activities.ViewEventActivity;
import com.vanpt.lunarcalendar.adapters.EventRecyclerAdapter;
import com.vanpt.lunarcalendar.data.MyDbHandler;
import com.vanpt.lunarcalendar.interfaces.ICommand;
import com.vanpt.lunarcalendar.utils.DateConverter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by vanpt on 11/23/2016.
 */

public class DisplayMonthViewCommand implements ICommand, View.OnClickListener {

    private final MainActivity mainActivity;
    private View monthView;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private EventRecyclerAdapter adapter;
    private DateObject currentDate;

    public DisplayMonthViewCommand(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void execute(DateObject date) throws Exception {
        this.currentDate = date;
        ViewGroup contentMain = this.mainActivity.getContentMainView();
        contentMain.removeAllViews();
        monthView = View.inflate(this.mainActivity, R.layout.layout_month, contentMain);
        genrateCalendar(date, monthView);
        setDateInfo(date, monthView);
    }

    private void setDateInfo(DateObject date, View monthView) {
        try {
            final TextView todaySolar = (TextView) monthView.findViewById(R.id.textViewSolarToday);
            todaySolar.setText(date.getDay() + " tháng " + date.getMonth() + ", " + date.getYear());
            final TextView todayLunar = (TextView) monthView.findViewById(R.id.textViewLunarToday);
            DateObject lunar = DateConverter.convertSolar2Lunar(date, this.mainActivity.getTimeZone());
            todayLunar.setText(
                    lunar.getDay() + " tháng " + lunar.getMonth() + ", năm " + DateConverter.convertToLunarYear(lunar.getYear()));
            mainActivity.getEvents().clear();
            MyDbHandler dbHandler = new MyDbHandler(mainActivity, null, null, 1);
            EventObject[] events = dbHandler.findEvent(lunar.getDay(), lunar.getMonth(), lunar.getYear());
            for (EventObject ev : events) {
                mainActivity.getEvents().add(ev);
            }
            recyclerView = (RecyclerView) monthView.findViewById(R.id.recycler_view);
            layoutManager = new LinearLayoutManager(this.mainActivity);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new EventRecyclerAdapter(this.mainActivity, this.mainActivity.getEvents(), this.mainActivity);
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void genrateCalendar(DateObject date, View monthView) throws Exception {
        final Button previousButton = (Button) monthView.findViewById(R.id.btnPreviousMonth);
        previousButton.setOnClickListener(this);
        final Button nextButton = (Button) monthView.findViewById(R.id.btnNextMonth);
        nextButton.setOnClickListener(this);
        final TextView textViewMonth = (TextView) monthView.findViewById(R.id.textViewMonth);
        textViewMonth.setText("Tháng " + date.getMonth() + ", " + date.getYear());
        DateObject lunarDate = DateConverter.convertSolar2Lunar(date, this.mainActivity.getTimeZone());
        final TextView textViewLunarMonth = (TextView) monthView.findViewById(R.id.textViewLunarMonth);
        textViewLunarMonth.setText("Tháng " + lunarDate.getMonth() + ", " + DateConverter.convertToLunarYear(lunarDate.getYear()));
        Calendar cal = Calendar.getInstance();
        cal.set(date.getYear(), date.getMonth() - 1, date.getDay());
        int lastDayOfMonth = cal.getActualMaximum(Calendar.DATE);
        TableLayout tl = (TableLayout) monthView.findViewById(R.id.tableMonth);
        TableLayout.LayoutParams tableRowParams =
                new TableLayout.LayoutParams
                        (TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT);

        final float scale = this.mainActivity.getResources().getDisplayMetrics().density;
        TableRow.LayoutParams textViewParams = new TableRow.LayoutParams(
                (int)(40 * scale + 0.5f), (int)(40 * scale + 0.5f));

        TableRow tr = new TableRow(this.mainActivity);
        tr.setLayoutParams(tableRowParams);
        cal.set(date.getYear(), date.getMonth() - 1, 1);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek > 1) {
            Date t = cal.getTime();
            cal.add(Calendar.MONTH, -1);
            t = cal.getTime();
            int lastDayOfPreviousMonth = cal.getActualMaximum(Calendar.DATE);
            for (int i = 0; i < dayOfWeek - 1; i++) {
                int yyyy = cal.get(Calendar.YEAR);
                int mm = cal.get(Calendar.MONTH) + 1;
                int dd = (lastDayOfPreviousMonth - dayOfWeek + i + 2);
                Button btn = createButton(dd, mm, yyyy);
                int id = yyyy * 10000 + mm * 100 + dd;
                DateObject d = new DateObject(dd, mm, yyyy, 0);
                DateObject lunar = DateConverter. convertSolar2Lunar(d, this.mainActivity.getTimeZone());
                btn.setTag(id);
                String lunarDay = lunar.getDay() + "";
                if (lunar.getDay() == 1) {
                    lunarDay += "/" + lunar.getMonth();
                }
                btn.setText(dd + "     \n    " + lunarDay);
                btn.setLayoutParams(textViewParams);
                btn.setTextColor(Color.GRAY);
                tr.addView(btn);
            }
        }
        cal = Calendar.getInstance();
        for (int i = 1; i <= lastDayOfMonth; i++) {
            int yyyy = date.getYear();
            int mm = date.getMonth();
            int dd = i;
            Button btn = createButton(dd, mm, yyyy);
            int id = yyyy * 10000 + mm * 100 + dd;
            DateObject d = new DateObject(dd, mm, yyyy, 0);
            DateObject lunar = DateConverter. convertSolar2Lunar(d, this.mainActivity.getTimeZone());
            btn.setTag(id);
            String lunarDay = lunar.getDay() + "";
            if (lunar.getDay() == 1) {
                lunarDay += "/" + lunar.getMonth();
            }
            btn.setText(i + "     \n     " + lunarDay);
            btn.setLayoutParams(textViewParams);
            if (dayOfWeek == 1) {
                btn.setTextColor(Color.RED);
            } else if (dayOfWeek == 7) {
                btn.setTextColor(Color.BLUE);
            }
            dayOfWeek += 1;
            tr.addView(btn);
            if (dayOfWeek > 7) {
                dayOfWeek = 1;
                tl.addView(tr, tableRowParams);
                tr = new TableRow(this.mainActivity);
                tr.setLayoutParams(tableRowParams);
            }
        }
        if (dayOfWeek <= 7) {
            tl.addView(tr, tableRowParams);
            for (int i = dayOfWeek; i <= 7; i++) {
                cal.set(date.getYear(), date.getMonth() - 1, date.getDay());
                cal.add(Calendar.MONTH, 1);
                int yyyy = cal.get(Calendar.YEAR);
                int mm = cal.get(Calendar.MONTH) + 1;
                int dd = (i - dayOfWeek + 1);
                Button btn = createButton(dd, mm, yyyy);
                int id = yyyy * 10000 + mm * 100 + dd;
                DateObject d = new DateObject(dd, mm, yyyy, 0);
                DateObject lunar = DateConverter. convertSolar2Lunar(d, this.mainActivity.getTimeZone());
                btn.setTag(id);
                String solarDay = dd + "";
                if (dd == 1) {
                    solarDay += "/" + mm;
                }
                String lunarDay = lunar.getDay() + "";
                if (lunar.getDay() == 1) {
                    lunarDay += "/" + lunar.getMonth();
                }
                btn.setTextColor(Color.GRAY);
                btn.setText(solarDay + "     \n     " + lunarDay);
                btn.setLayoutParams(textViewParams);
                tr.addView(btn);
            }
        }
    }

    @NonNull
    private Button createButton(int day, int month, int year) {
        Button btn = new Button(this.mainActivity, null, android.R.style.Widget_DeviceDefault_Button_Borderless_Small);
        btn.setGravity(Gravity.CENTER);
        GradientDrawable gd = new GradientDrawable();
        if (day != this.mainActivity.getSelectedSolarDate().getDay() ||
                month != this.mainActivity.getSelectedSolarDate().getMonth() ||
                year != this.mainActivity.getSelectedSolarDate().getYear()) {
            gd.setStroke(1, Color.GRAY);
        }
        else {
            gd.setCornerRadius(5);
            gd.setStroke(5, this.mainActivity.getResources().getColor(R.color.colorLightBlue));
        }
        if (day == this.mainActivity.getTodaySolarDate().getDay() &&
                month == this.mainActivity.getTodaySolarDate().getMonth() &&
                year == this.mainActivity.getTodaySolarDate().getYear()) {
            gd.setColor(this.mainActivity.getResources().getColor(R.color.colorToday));
        }
        btn.setBackgroundDrawable(gd);
        btn.setOnClickListener(this);
        return btn;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnPreviousMonth) {
            try {
                int selectedMonth = this.currentDate.getMonth();
                int previousMonth = selectedMonth - 1;
                int year = this.currentDate.getYear();
                if (previousMonth == 0) {
                    previousMonth = 12;
                    year -= 1;
                }
                DateObject newDate = new DateObject(
                        this.currentDate.getDay(),
                        previousMonth,
                        year,
                        0
                );
                this.mainActivity.setSelectedSolarDate(newDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (id == R.id.btnNextMonth) {
            try {
                int selectedMonth = this.currentDate.getMonth();
                int nextMonth = selectedMonth + 1;
                int year = this.currentDate.getYear();
                if (nextMonth == 13) {
                    nextMonth = 1;
                    year += 1;
                }
                DateObject newDate = new DateObject(
                        this.currentDate.getDay(),
                        nextMonth,
                        year,
                        0
                );
                this.mainActivity.setSelectedSolarDate(newDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            int tag = (int) view.getTag();
            try {
                int year = tag / 10000;
                int month = (tag - year * 10000) / 100;
                int day = tag - year * 10000 - month * 100;
                DateObject date = new DateObject(day, month, year, 0);
                this.mainActivity.setSelectedSolarDate(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
