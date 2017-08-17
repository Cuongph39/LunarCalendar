package com.vanpt.lunarcalendar.models;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
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
    private DateObject currentDate;
    private MonthDayFragment selectedDay;
    private MonthDayFragment[] dayFragments = new MonthDayFragment[42];

    public DisplayMonthViewCommand(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void execute(DateObject date) throws Exception {
        ViewGroup contentMain = this.mainActivity.getContentMainView();
        this.currentDate = date;
        if (this.mainActivity.isSelectedNavMenuItemChanged()) {
            contentMain.removeAllViews();
            monthView = View.inflate(this.mainActivity, R.layout.layout_month, contentMain);
            final Button previousButton = (Button) monthView.findViewById(R.id.btnPreviousMonth);
            previousButton.setOnClickListener(this);
            final Button nextButton = (Button) monthView.findViewById(R.id.btnNextMonth);
            nextButton.setOnClickListener(this);
            genrateCalendar(date, monthView);
        }
        updateCalendar(date);
        final TextView textViewMonth = (TextView) monthView.findViewById(R.id.textViewMonth);
        textViewMonth.setText("Tháng " + date.getMonth() + ", " + date.getYear());
        DateObject lunarDate = DateConverter.convertSolar2Lunar(date, this.mainActivity.getTimeZone());
        final TextView textViewLunarMonth = (TextView) monthView.findViewById(R.id.textViewLunarMonth);
        textViewLunarMonth.setText("Tháng " + lunarDate.getMonth() + ", " + DateConverter.convertToLunarYear(lunarDate.getYear()));
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
            EventObject[] events = dbHandler.findEvent(date.getDay(), date.getMonth(), date.getYear());
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
        android.app.FragmentManager fm = this.mainActivity.getFragmentManager();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                dayFragments[i * 7 + j] = MonthDayFragment.newInstance(1, 1, 2000, false);
                String strId = "md" + (i + 1) + "" + (j + 1);
                int resId = this.mainActivity.getResources().getIdentifier(strId, "id", this.mainActivity.getPackageName());
                LinearLayout ll = (LinearLayout) monthView.findViewById(resId);
                FragmentTransaction ft = fm.beginTransaction();
                ft.add(ll.getId(), dayFragments[i * 7 + j], strId);
                ft.commit();
             }
        }
    }

    private void updateCalendar(DateObject date) throws Exception {
        if (selectedDay != null) {
            selectedDay.setSelected(false);
        }
        Calendar cal = Calendar.getInstance();
        cal.set(date.getYear(), date.getMonth() - 1, date.getDay());
        int lastDayOfMonth = cal.getActualMaximum(Calendar.DATE);
        cal.set(date.getYear(), date.getMonth() - 1, 1);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int index = 0;
        if (dayOfWeek > 1) {
            cal.add(Calendar.MONTH, -1);
            int lastDayOfPreviousMonth = cal.getActualMaximum(Calendar.DATE);
            for (int i = 0; i < dayOfWeek - 1; i++) {
                int yyyy = cal.get(Calendar.YEAR);
                int mm = cal.get(Calendar.MONTH) + 1;
                int dd = (lastDayOfPreviousMonth - dayOfWeek + i + 2);
                MonthDayFragment dayView = dayFragments[0 + i];
                index += 1;
                if (dayView != null) {
                    dayView.update(dd, mm, yyyy, false);
                }
            }
        }
        for (int i = 1; i <= lastDayOfMonth; i++) {
            int yyyy = date.getYear();
            int mm = date.getMonth();
            int dd = i;
            MonthDayFragment dayView = dayFragments[index];
            index += 1;
            if (dayView != null) {
                dayView.update(dd, mm, yyyy, true);
            }
            if (dd == date.getDay()) {
                this.selectedDay  = dayView;
            }
        }
        for (int i = index; i < dayFragments.length; i++) {
            int yyyy = date.getYear();
            int mm = date.getMonth() + 1;
            int dd = i - index + 1;
            MonthDayFragment dayView = dayFragments[i];
            if (dayView != null) {
                dayView.update(dd, mm, yyyy, false);
            }
        }
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
