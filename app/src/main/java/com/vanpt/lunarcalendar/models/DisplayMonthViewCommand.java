package com.vanpt.lunarcalendar.models;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.vanpt.lunarcalendar.R;
import com.vanpt.lunarcalendar.activities.MainActivity;
import com.vanpt.lunarcalendar.adapters.EventRecyclerAdapter;
import com.vanpt.lunarcalendar.data.MyDbHandler;
import com.vanpt.lunarcalendar.fragments.MonthDayFragment;
import com.vanpt.lunarcalendar.interfaces.ICommand;
import com.vanpt.lunarcalendar.utils.DateConverter;

import java.util.Calendar;

/**
 * Created by vanpt on 11/23/2016.
 */

public class DisplayMonthViewCommand implements ICommand, View.OnClickListener {

    private final MainActivity mainActivity;
    private View monthView;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private EventRecyclerAdapter adapter;
    private DateObject previousDate;
    private DateObject currentDate;
    private MonthDayFragment selectedDay;

    public DisplayMonthViewCommand(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void execute(DateObject date) throws Exception {
        this.currentDate = date;
        ViewGroup contentMain = this.mainActivity.getContentMainView();
        if (previousDate == null ||
                (previousDate.getMonth() != this.currentDate.getMonth() ||
                previousDate.getYear() != this.currentDate.getYear())) {
            contentMain.removeAllViews();
            monthView = View.inflate(this.mainActivity, R.layout.layout_month, contentMain);
            genrateCalendar(date, monthView);
        }
        else {
            if (selectedDay != null) {
                selectedDay.setSelected(false);
            }
            FragmentManager fm = this.mainActivity.getFragmentManager();
            String tag = date.getYear() * 10000 + date.getMonth() * 100 + date.getDay() + "";
            MonthDayFragment dayView = (MonthDayFragment) fm.findFragmentByTag(tag);
            dayView.setSelected(true);
            this.selectedDay = dayView;
        }
        setDateInfo(date, monthView);
        this.previousDate = currentDate;
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
                new TableLayout.LayoutParams(0,TableLayout.LayoutParams.WRAP_CONTENT, 1);
        final float scale = this.mainActivity.getResources().getDisplayMetrics().density;
        TableRow.LayoutParams textViewParams = new TableRow.LayoutParams(
                (int)(40 * scale + 0.5f), (int)(40 * scale + 0.5f));

        TableRow tr = new TableRow(this.mainActivity);
        tr.setLayoutParams(tableRowParams);
        cal.set(date.getYear(), date.getMonth() - 1, 1);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek > 1) {
            cal.add(Calendar.MONTH, -1);
            int lastDayOfPreviousMonth = cal.getActualMaximum(Calendar.DATE);
            for (int i = 0; i < dayOfWeek - 1; i++) {
                int yyyy = cal.get(Calendar.YEAR);
                int mm = cal.get(Calendar.MONTH) + 1;
                int dd = (lastDayOfPreviousMonth - dayOfWeek + i + 2);
                View dayView = createDayView(dd, mm, yyyy, false);
                tr.addView(dayView);
            }
        }
        cal = Calendar.getInstance();
        for (int i = 1; i <= lastDayOfMonth; i++) {
            int yyyy = date.getYear();
            int mm = date.getMonth();
            int dd = i;
            View dayView = createDayView(dd, mm, yyyy, true);
            tr.addView(dayView);
            dayOfWeek += 1;
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
                View dayView = createDayView(dd, mm, yyyy, false);
                tr.addView(dayView);
            }
        }
    }

    private View createDayView(int day, int month, int year, boolean isCurrent) {
        int id = year * 10000 + month * 100 + day;
        MonthDayFragment frag = MonthDayFragment.newInstance(day, month, year, isCurrent);
        if (this.currentDate.getDay() == day && this.currentDate.getMonth() == month) {
            this.selectedDay = frag;
        }
        LinearLayout l = new LinearLayout(this.mainActivity);
        l.setId(id);
        android.app.FragmentManager fm = this.mainActivity.getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(l.getId(), frag, id + "");
        ft.commit();
        return l;
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
        }
    }
}
