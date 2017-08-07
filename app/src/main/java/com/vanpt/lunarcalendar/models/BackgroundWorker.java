package com.vanpt.lunarcalendar.models;

import android.support.design.widget.NavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.vanpt.lunarcalendar.activities.MainActivity;
import com.vanpt.lunarcalendar.R;
import com.vanpt.lunarcalendar.utils.Constants;
import com.vanpt.lunarcalendar.utils.DateConverter;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by vanpt on 11/23/2016.
 */

public class BackgroundWorker implements Runnable {
    private MainActivity mainActivity;

    public BackgroundWorker(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void run() {
        setNavigationViewInfo();
    }

    public void setNavigationViewInfo() {
        try {
            NavigationView navigationView = (NavigationView) this.mainActivity.findViewById(R.id.nav_view);
            Calendar cal = Calendar.getInstance();
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
            int month = cal.get(Calendar.MONTH) + 1;
            int year = cal.get(Calendar.YEAR);
            DateObject today = new DateObject(dayOfMonth, month, year, 0);
            this.mainActivity.setTodaySolarDate(today);
            DateObject lunarDate = DateConverter.convertSolar2Lunar(today, this.mainActivity.getTimeZone());
            View header = navigationView.getHeaderView(0);
            final TextView textViewDay = (TextView) header.findViewById(R.id.textViewDay);
            textViewDay.setText(Constants.DAYS[dayOfWeek]);
            final TextView textViewDate = (TextView) header.findViewById(R.id.textViewDate);
            textViewDate.setText(dayOfMonth + " tháng " + month + ", " + year);
            //String time = new SimpleDateFormat("hh:mm:ss a").format(cal.getTime());
            //final TextView textViewTime = (TextView) header.findViewById(R.id.textViewTime);
            //textViewTime.setText(time);

            final TextView textViewLunarDate = (TextView) header.findViewById(R.id.textViewLunarDate);
            textViewLunarDate.setText(
                    lunarDate.getDay() + " tháng " + lunarDate.getMonth() + ", năm " + DateConverter.convertToLunarYear(lunarDate.getYear()));

            Menu menu = navigationView.getMenu();
            MenuItem mnuMonth = menu.getItem(0);
            mnuMonth.setTitle("Tháng (tháng " + month + ")");
            MenuItem mnuDay = menu.getItem(1);
            mnuDay.setTitle("Ngày (" + dayOfMonth + " tháng " + month + ")");
            MenuItem mnuAgenda = menu.getItem(2);
            mnuAgenda.setTitle("Sự kiện (" + dayOfMonth + " tháng " + month + ")");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
