package com.vanpt.lunarcalendar.models;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.vanpt.lunarcalendar.activities.MainActivity;
import com.vanpt.lunarcalendar.R;
import com.vanpt.lunarcalendar.activities.ViewEventActivity;
import com.vanpt.lunarcalendar.adapters.EventRecyclerAdapter;
import com.vanpt.lunarcalendar.data.MyDbHandler;
import com.vanpt.lunarcalendar.interfaces.ICommand;
import com.vanpt.lunarcalendar.interfaces.OnItemClickListener;
import com.vanpt.lunarcalendar.utils.DateConverter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by vanpt on 11/25/2016.
 */

public class DisplayDayViewCommand implements ICommand, View.OnClickListener {

    private MainActivity mainActivity;
    private View dayView;
    private DateObject currentDate;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private EventRecyclerAdapter adapter;

    public DisplayDayViewCommand(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void execute(DateObject date) throws Exception {
        currentDate = date;
        ViewGroup contentMain = this.mainActivity.getContentMainView();
        contentMain.removeAllViews();
        dayView = View.inflate(this.mainActivity, R.layout.layout_day, contentMain);
        setInfo(date);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        try {
            Calendar cal = Calendar.getInstance();
            cal.set(currentDate.getYear(), currentDate.getMonth() - 1, currentDate.getDay());
            if (id == R.id.btnNextDay) {
                cal.add(Calendar.DAY_OF_YEAR, 1);
                DateObject newDate = new DateObject(
                        cal.get(Calendar.DAY_OF_MONTH),
                        cal.get(Calendar.MONTH) + 1,
                        cal.get(Calendar.YEAR),
                        0
                );
                this.mainActivity.setSelectedSolarDate(newDate);
            } else if (id == R.id.btnPreviousDay) {
                cal.add(Calendar.DAY_OF_YEAR, -1);
                DateObject newDate = new DateObject(
                        cal.get(Calendar.DAY_OF_MONTH),
                        cal.get(Calendar.MONTH) + 1,
                        cal.get(Calendar.YEAR),
                        0
                );
                this.mainActivity.setSelectedSolarDate(newDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setInfo(DateObject date) {
        try {
            final Button btnPrevious = (Button) this.dayView.findViewById(R.id.btnPreviousDay);
            btnPrevious.setOnClickListener(this);
            final Button btnNext = (Button) this.dayView.findViewById(R.id.btnNextDay);
            btnNext.setOnClickListener(this);
            DateObject lunarDate = DateConverter.convertSolar2Lunar(date, this.mainActivity.getTimeZone());
            Calendar cal = Calendar.getInstance();
            cal.set(currentDate.getYear(), currentDate.getMonth() - 1, currentDate.getDay());
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            final TextView textViewDay = (TextView) this.dayView.findViewById(R.id.textViewDay);
            textViewDay.setText(DateConverter.VN_DAYS[dayOfWeek - 1]);
            final TextView textViewSolarMonth = (TextView) this.dayView.findViewById(R.id.textViewSolarMonth);
            textViewSolarMonth.setText("Tháng " + date.getMonth() + ", " + date.getYear());
            final TextView textViewSolarDay = (TextView) this.dayView.findViewById(R.id.textViewSolarDay);
            textViewSolarDay.setText(String.valueOf(date.getDay()));
            final TextView textViewLunarMonth = (TextView) this.dayView.findViewById(R.id.textViewLunarMonth);
            textViewLunarMonth.setText("Tháng " + lunarDate.getMonth());
            final TextView textViewLunarDay = (TextView) this.dayView.findViewById(R.id.textViewLunarDay);
            textViewLunarDay.setText(String.valueOf(lunarDate.getDay()));
            final TextView textViewLunarYear = (TextView) this.dayView.findViewById(R.id.textViewLunarYear);
            textViewLunarYear.setText("Năm " + DateConverter.convertToLunarYear(lunarDate.getYear()));
            final TextView textViewNgayCanChi = (TextView) this.dayView.findViewById(R.id.textViewDayCanChi);
            String dayCanChi = DateConverter.convertToCanChiDay(
                    currentDate.getDay(), currentDate.getMonth(), currentDate.getYear()
            );
            textViewNgayCanChi.setText("Ngày " + dayCanChi);
            final TextView textViewMonthCanChi = (TextView) this.dayView.findViewById(R.id.textViewMonthCanChi);
            String monthCanChi = DateConverter.convertToCanChiMonth(
                    this.currentDate.getMonth(), this.currentDate.getYear());
            textViewMonthCanChi.setText("Tháng " + monthCanChi);
            final TextView textViewHourCanChi = (TextView) this.dayView.findViewById(R.id.textViewHourCanChi);
            String hourCanChi = DateConverter.convertToCanChiHour(
                    this.currentDate.getDay(),
                    this.currentDate.getMonth(),
                    this.currentDate.getYear(),
                    0
            );
            textViewHourCanChi.setText("Giờ " + hourCanChi);
            final TextView textViewTietAmLich = (TextView) this.dayView.findViewById(R.id.textViewTiet);
            String tietAmLich = DateConverter.getTietAmLichName(
                    this.currentDate.getDay(),
                    this.currentDate.getMonth());
            textViewTietAmLich.setText("Tiết " + tietAmLich);
            final TextView textViewGioHoangDao = (TextView) this.dayView.findViewById(R.id.textViewGioHoangDao);
            int[] gioHoangDao = DateConverter.getGioHoangDao(
                    this.currentDate.getDay(),
                    this.currentDate.getMonth(),
                    this.currentDate.getYear()
            );
            String strGioHoangDao = "Giờ hoàng đạo: ";
            for (int i = 0; i < gioHoangDao.length; i++) {
                int from = i * 2 - 1;
                int to = i * 2 + 1;
                if (from < 0) {
                    from = from + 24;
                }
                strGioHoangDao += DateConverter.CHI_AM_LICH[gioHoangDao[i]] +
                        " (" + from + "-" + to + "), ";
            }
            strGioHoangDao = strGioHoangDao.substring(0, strGioHoangDao.length() - 2);
            textViewGioHoangDao.setText(strGioHoangDao);
            mainActivity.getEvents().clear();
            MyDbHandler dbHandler = new MyDbHandler(mainActivity, null, null, 1);
            EventObject[] events = dbHandler.findEvent(lunarDate.getDay(), lunarDate.getMonth(), lunarDate.getYear());
            for (EventObject ev : events) {
                mainActivity.getEvents().add(ev);
            }
            recyclerView = (RecyclerView) dayView.findViewById(R.id.recycler_view);
            layoutManager = new LinearLayoutManager(this.mainActivity);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new EventRecyclerAdapter(this.mainActivity, this.mainActivity.getEvents(),
                    new OnItemClickListener() {
                @Override
                public void onItemClick(EventObject item) {
                    Intent intent = new Intent(mainActivity, ViewEventActivity.class);
                    intent.putExtra("id", item.getId());
                    mainActivity.startActivityForResult(intent, MainActivity.REQUEST_CODE);
                }
            });
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
