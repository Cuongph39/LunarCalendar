package com.vanpt.lunarcalendar.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vanpt.lunarcalendar.R;
import com.vanpt.lunarcalendar.activities.MainActivity;
import com.vanpt.lunarcalendar.data.MyDbHandler;
import com.vanpt.lunarcalendar.models.DateObject;
import com.vanpt.lunarcalendar.models.EventObject;
import com.vanpt.lunarcalendar.utils.DateConverter;

import java.util.Calendar;

public class MonthDayFragment extends Fragment implements View.OnClickListener{
    private static final String ARG_DAY = "day";
    private static final String ARG_MONTH = "month";
    private static final String ARG_YEAR = "year";
    private static final String ARG_ISCURRENT = "isCurrent";

    private int mDay = 0;
    private int mMonth = 0;
    private int mYear = 0;
    private boolean mIsCurrent;
    private EventObject[] events;
    private boolean isSelected = false;
    private View view;

    public MonthDayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param day Day.
     * @param month Month.
     * @param year Year.
     * @param isCurrent Is current month
     * @return A new instance of fragment MonthDayFragment.
     */
    public static MonthDayFragment newInstance(int day, int month, int year, boolean isCurrent) {
        MonthDayFragment fragment = new MonthDayFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_DAY, day);
        args.putInt(ARG_MONTH, month);
        args.putInt(ARG_YEAR, year);
        args.putBoolean(ARG_ISCURRENT, isCurrent);
        fragment.setArguments(args);
        return fragment;
    }

    public void update(int dd, int mm, int yyyy, boolean isCurrent) {
        mDay = dd;
        mMonth = mm;
        mYear = yyyy;
        mIsCurrent = isCurrent;
        if (view == null) {
            return;
        }
        View rootView = view.findViewById(R.id.rootLayoutMonthDayFrag);
        LinearLayout gridLayout = (LinearLayout) view.findViewById(R.id.gridLayoutMonthDayFrag);
        gridLayout.setOnClickListener(this);
        TextView textViewSolarDay = (TextView)view.findViewById(R.id.textViewMonthSolarDay);
        textViewSolarDay.setText(mDay + "");
        TextView textViewLunarDay = (TextView)view.findViewById(R.id.textViewMonthLunarDay);
        MainActivity activity = (MainActivity) this.getActivity();
        try {
            DateObject date = new DateObject(mDay, mMonth, mYear);
            MyDbHandler dbHandler = new MyDbHandler(activity, null, null, 1);
            events = dbHandler.findEvent(date.getDay(), date.getMonth(), date.getYear());
            LinearLayout eventView = (LinearLayout) view.findViewById(R.id.layoutMonthDayEvent);
            eventView.removeAllViews();
            int min = Math.min(3, events.length);
            for (int i = 0; i < min; i++) {
                TextView tv = new TextView(activity);
                LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                llp.setMargins(1, 1, 1, 1);
                tv.setLayoutParams(llp);
                tv.setHeight(10);
                tv.setWidth(20);
                tv.setBackgroundColor(getResources().getColor(events[i].getColor()));
                eventView.addView(tv);
            }
            if (mDay == activity.getTodaySolarDate().getDay() &&
                    mMonth == activity.getTodaySolarDate().getMonth() &&
                    mYear == activity.getTodaySolarDate().getYear()) {
                rootView.setBackgroundColor(activity.getResources().getColor(R.color.colorToday));
                gridLayout.setBackgroundColor(activity.getResources().getColor(R.color.colorToday));
            }
            else {
                rootView.setBackgroundColor(Color.WHITE);
                gridLayout.setBackgroundColor(Color.WHITE);
            }

            DateObject selected = activity.getSelectedSolarDate();
            if (selected.getDay() == mDay &&
                    selected.getMonth() == mMonth &&
                    selected.getYear() == mYear) {
                setSelected(true);
            }
            DateObject d = new DateObject(mDay, mMonth, mYear, 0);
            DateObject lunar = DateConverter. convertSolar2Lunar(d, activity.getTimeZone());
            String lunarDay = lunar.getDay() + "";
            if (lunar.getDay() == 1) {
                lunarDay += "/" + lunar.getMonth();
            }
            textViewLunarDay.setText(lunarDay);

            if (mIsCurrent) {
                Calendar cal = Calendar.getInstance();
                cal.set(mYear, mMonth - 1, mDay);
                int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
                textViewSolarDay.setTextColor(Color.BLACK);
                textViewLunarDay.setTextColor(Color.BLACK);
                if (dayOfWeek == 1) {
                    textViewSolarDay.setTextColor(Color.RED);
                    textViewLunarDay.setTextColor(Color.RED);
                } else if (dayOfWeek == 7) {
                    textViewSolarDay.setTextColor(Color.BLUE);
                    textViewLunarDay.setTextColor(Color.BLUE);
                }
            }
            else {
                textViewSolarDay.setTextColor(Color.GRAY);
                textViewLunarDay.setTextColor(Color.GRAY);
                String solarDay = mDay + "";
                if (mDay == 1) {
                    solarDay += "/" + mMonth;
                    textViewSolarDay.setText(solarDay);
                }
            }
        } catch (Exception e) {
            Log.e("ERROR", e.toString());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null) {
            return;
        }
        if (mDay == 0) {
            mDay = getArguments().getInt(ARG_DAY);
        }
        if (mMonth == 0) {
            mMonth = getArguments().getInt(ARG_MONTH);
        }
        if (mYear == 0) {
            mYear = getArguments().getInt(ARG_YEAR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View  view = inflater.inflate(R.layout.fragment_month_day, container, false);
        this.view = view;
        update(mDay, mMonth, mYear, mIsCurrent);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        try {
            MainActivity activity = (MainActivity) this.getActivity();
            DateObject date = new DateObject(mDay, mMonth, mYear, 0);
            activity.setSelectedSolarDate(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
        View rootView = this.view.findViewById(R.id.rootLayoutMonthDayFrag);
        MainActivity activity = (MainActivity) this.getActivity();
        if (isSelected) {
            rootView.setBackgroundColor(activity.getResources().getColor(R.color.colorYellow));
        } else {
            rootView.setBackgroundColor(Color.WHITE);
        }
    }
}
