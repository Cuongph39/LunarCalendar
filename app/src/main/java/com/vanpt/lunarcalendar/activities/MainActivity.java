package com.vanpt.lunarcalendar.activities;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.vanpt.lunarcalendar.R;
import com.vanpt.lunarcalendar.data.MyDbHandler;
import com.vanpt.lunarcalendar.fragments.EventPropertiesFragment;
import com.vanpt.lunarcalendar.fragments.GoToLunarDateFragment;
import com.vanpt.lunarcalendar.interfaces.IDialogEventListener;
import com.vanpt.lunarcalendar.interfaces.OnItemClickListener;
import com.vanpt.lunarcalendar.models.BackgroundWorker;
import com.vanpt.lunarcalendar.models.CommandInvoker;
import com.vanpt.lunarcalendar.models.DateObject;
import com.vanpt.lunarcalendar.models.DisplayAgendaViewCommand;
import com.vanpt.lunarcalendar.models.DisplayDayViewCommand;
import com.vanpt.lunarcalendar.models.DisplayMonthViewCommand;
import com.vanpt.lunarcalendar.models.EventObject;
import com.vanpt.lunarcalendar.utils.DateConverter;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        Runnable,
        OnItemClickListener,
        IDialogEventListener {

    private DateObject todaySolarDate;
    private DateObject selectedSolarDate;
    private Thread mainThread = null;
    private BackgroundWorker backgroundWorker;
    private CommandInvoker commandInvoker = new CommandInvoker();
    private int selectedNavMenuItem;
    private int previousSelectedNavMenuItem;
    private boolean selectedNavMenuItemChanged = false;
    private ArrayList<EventObject> events = new ArrayList<>();
    private static final int UNIQUE_ID = 461984;
    private static final String NUMBER_OF_LAUNCHES = "numberOfLaunches";

    NotificationCompat.Builder notification;

    public static int timeZone = 7;
    public static final int REQUEST_CODE = 5;

    public MainActivity() {
        backgroundWorker = new BackgroundWorker(this);
        Calendar cal = Calendar.getInstance();
        TimeZone timeZone = cal.getTimeZone();
        this.timeZone = (int) TimeUnit.HOURS.convert(timeZone.getRawOffset(), TimeUnit.MILLISECONDS);
        commandInvoker.setCommand(R.id.nav_month, new DisplayMonthViewCommand(this));
        commandInvoker.setCommand(R.id.nav_day, new DisplayDayViewCommand(this));
        commandInvoker.setCommand(R.id.nav_agenda, new DisplayAgendaViewCommand(this));
    }

    public ArrayList<EventObject> getEvents() {
        return events;
    }

    public int getTimeZone() {
        return timeZone;
    }

    public DateObject getTodaySolarDate() {
        return todaySolarDate;
    }

    public void setTodaySolarDate(DateObject todaySolarDate) {
        this.todaySolarDate = todaySolarDate;
        if (this.selectedSolarDate == null) {
            this.selectedSolarDate = this.todaySolarDate;
        }
    }

    public DateObject getSelectedSolarDate() {
        return selectedSolarDate;
    }

    public void setSelectedSolarDate(DateObject selectedSolarDate) {
        if (this.selectedSolarDate != selectedSolarDate) {
            this.selectedSolarDate = selectedSolarDate;
        }
        try {
            selectedNavMenuItemChanged = this.previousSelectedNavMenuItem != this.selectedNavMenuItem;
            commandInvoker.executeCommand(this.selectedNavMenuItem, this.selectedSolarDate);
            this.previousSelectedNavMenuItem = this.selectedNavMenuItem;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isSelectedNavMenuItemChanged() {
        return selectedNavMenuItemChanged;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        backgroundWorker.setNavigationViewInfo();

        Menu navMenu = navigationView.getMenu();
        MenuItem month = navMenu.getItem(0);
        onNavigationItemSelected(month);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        int numberOfLaunches = preferences.getInt(NUMBER_OF_LAUNCHES, 1);
//        editor.putInt(NUMBER_OF_LAUNCHES, 0);
//        editor.commit();
        if (numberOfLaunches < 2) {
            InputStream stream = getResources().openRawResource(
                    getResources().getIdentifier("holidays", "raw", getPackageName()));
            MyDbHandler dbHandler = new MyDbHandler(this, null, null, 1);
            dbHandler.addCommonEvents(stream);
            numberOfLaunches += 1;
            editor.putInt(NUMBER_OF_LAUNCHES, numberOfLaunches);
            editor.commit();
        }
        notification = new NotificationCompat.Builder(this);
        notification.setAutoCancel(true);
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_event) {
            try {
                EventPropertiesFragment eventProperties = new EventPropertiesFragment();
                EventObject eventObject = new EventObject("Sự kiện mới");
                DateObject lunar = DateConverter.convertSolar2Lunar(this.selectedSolarDate, this.timeZone);
                Calendar cal = Calendar.getInstance();
                cal.set(
                        lunar.getYear(),
                        lunar.getMonth() - 1,
                        lunar.getDay(), 8, 0);

                eventObject.setFromDate(cal.getTime());
                cal.set(
                        lunar.getYear(),
                        lunar.getMonth() - 1,
                        lunar.getDay(), 20, 0);
                eventObject.setToDate(cal.getTime());
                eventProperties.setEvent(eventObject);
                eventProperties.show(getFragmentManager(), "EventPropertiesFragment");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        } else if (id == R.id.action_today) {
            try {
                setSelectedSolarDate(todaySolarDate);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else if (id == R.id.go_to_solar_day) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    try {
                        MainActivity.this.selectedSolarDate.setYear(year);
                        MainActivity.this.selectedSolarDate.setMonth(month + 1);
                        MainActivity.this.selectedSolarDate.setDay(day);
                        MainActivity.this.selectedNavMenuItemChanged =
                                MainActivity.this.selectedNavMenuItem != MainActivity.this.previousSelectedNavMenuItem;
                        MainActivity.this.commandInvoker.executeCommand(MainActivity.this.selectedNavMenuItem,
                                MainActivity.this.selectedSolarDate);
                        MainActivity.this.previousSelectedNavMenuItem = MainActivity.this.selectedNavMenuItem;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, this.selectedSolarDate.getYear(), this.selectedSolarDate.getMonth() - 1, this.selectedSolarDate.getDay());
            datePickerDialog.show();
        } else if (id == R.id.go_to_lunar_day) {
            GoToLunarDateFragment goToLunarDateFragment = new GoToLunarDateFragment();
            goToLunarDateFragment.show(getFragmentManager(), "GoToLunarDateFragment");
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        selectedNavMenuItem = id;

        try {
            this.selectedNavMenuItemChanged =
                    this.selectedNavMenuItem != this.previousSelectedNavMenuItem;
            commandInvoker.executeCommand(id, selectedSolarDate);
            this.previousSelectedNavMenuItem = this.selectedNavMenuItem;
        } catch (Exception e) {
            e.printStackTrace();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mainThread = new Thread(this);
        //mainThread.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            mainThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            runOnUiThread(backgroundWorker);
            try {
                mainThread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public ViewGroup getContentMainView() {
        View appBarMain = findViewById(R.id.app_bar_main);
        ViewGroup contentMain = (ViewGroup) appBarMain.findViewById(R.id.include);
        return contentMain;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == REQUEST_CODE) && (resultCode == RESULT_OK)) {
            int selectedColor = data.getExtras().getInt("selectedColor");
            int eventId = data.getExtras().getInt("id");
            for (EventObject ev : events) {
                if (ev.getId() == eventId) {
                    ev.setColor(selectedColor);
                    break;
                }
            }
            try {
                this.selectedNavMenuItemChanged =
                        this.selectedNavMenuItem != this.previousSelectedNavMenuItem;
                this.commandInvoker.executeCommand(this.selectedNavMenuItem, this.selectedSolarDate);
                this.previousSelectedNavMenuItem = this.selectedNavMenuItem;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onItemClick(EventObject item) {
        Intent intent = new Intent(this, ViewEventActivity.class);
        intent.putExtra("id", item.getId());
        this.startActivityForResult(intent, MainActivity.REQUEST_CODE);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if (dialog instanceof EventPropertiesFragment) {
            EventPropertiesFragment eventProperties = (EventPropertiesFragment) dialog;
            MyDbHandler dbHandler = new MyDbHandler(this, null, null, 1);
            dbHandler.addEvent(eventProperties.getEvent());
        } else if (dialog instanceof GoToLunarDateFragment) {
            try {
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
                Date result = df.parse(((GoToLunarDateFragment)dialog).getLunarDateString());
                Calendar cal = Calendar.getInstance();
                cal.setTime(result);
                DateObject lunar = new DateObject(
                        cal.get(Calendar.DAY_OF_MONTH),
                        cal.get(Calendar.MONTH) + 1,
                        cal.get(Calendar.YEAR)
                );
                DateObject solar = DateConverter.convertLunar2Solar(lunar, timeZone);
                this.setSelectedSolarDate(solar);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.getDialog().cancel();
    }
}
