package com.example.scheduleapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import static android.content.Context.ALARM_SERVICE;

public class ListDate {
    private TreeMap<String, ArrayList<Activity>> listDate;
    private static volatile ListDate INSTANCE ;
    private ListDate(){
        listDate = new TreeMap<>();
    }
    private Context scheduleActivityContext;

    public static synchronized ListDate getInstance(){
        if (INSTANCE == null) {
            INSTANCE = new ListDate();
        }
        return INSTANCE;
    }

    public boolean haveDate(String date) {
        return listDate.containsKey(date);
    }

    public void addDate(String newDate) {
        listDate.put(newDate,null);
    }

    public void addTodoList(String Date, ArrayList<Activity> list){
        listDate.put(Date,list);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<String> getActivityDays() {
        ArrayList<String> list = new ArrayList<String>();
        for (String key: listDate.keySet() ) {
            list.add(key);
        }
        Collections.sort(list);
        return list;
    }

    public ArrayList<Activity> getTodoList(String date){
        return listDate.get(date);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void Sort(){
        Set<String> keySet = listDate.keySet();
        for(String key : keySet){
            Collections.sort(Objects.requireNonNull(listDate.get(key)), Comparator.comparing(Activity::get_timeStart));
        }
    }

    public void loadFromData(TreeMap<String,ArrayList<Activity>> map){
        listDate.putAll(map);
    }

    public TreeMap<String,ArrayList<Activity>> getListDate(){
        return listDate;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public void updateDataFromFile(Context context) throws IOException {
        SaveLoad saveLoad = new SaveLoad();
        Gson gson = new Gson();
        String json = saveLoad.ReadFromFile(context);
        TreeMap<String, ArrayList<Activity>> map = gson.fromJson(
                json, new TypeToken<TreeMap<String, ArrayList<Activity>>>(){}.getType()
        );
        if (map != null) loadFromData(map);
    }

    public void setMyContext(Context context) {
        this.scheduleActivityContext = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setAlarmForNextActivity() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        PendingIntent pendingIntent = compose(cal);
        if (pendingIntent != null) {
            Date date = cal.getTime();
            AlarmManager alarmManager = (AlarmManager) scheduleActivityContext.getSystemService(ALARM_SERVICE);
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
        } else
        {
            AlarmManager alarmManager = (AlarmManager) scheduleActivityContext.getSystemService(ALARM_SERVICE);
            Intent myIntent = new Intent(scheduleActivityContext, ReminderBroadcast.class);
            PendingIntent pendingIntentt = PendingIntent.getBroadcast(
                    scheduleActivityContext, 0, myIntent, 0);

            alarmManager.cancel(pendingIntentt);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public PendingIntent compose(Calendar calendar) {
        Activity act = getUpComingActivity();
        if (act != null) {
            String date = act.get_date();
            String time = act.get_timeStart();
            int year = Integer.parseInt(date.substring(0, 4));
            int month = Integer.parseInt(date.substring(5, 7)) - 1;
            int day = Integer.parseInt(date.substring(8, date.length()));
            int hour = Integer.parseInt(time.substring(0, 2));
            int minute = Integer.parseInt(time.substring(3, 5));

            calendar.set(year, month, day, hour, minute, 0);

            Intent alarmIntent = new Intent(scheduleActivityContext, ReminderBroadcast.class);
            return PendingIntent.getBroadcast(scheduleActivityContext, 0, alarmIntent, 0);
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Activity getNextActivity() {
        ArrayList<String> list = getActivityDays();
        ArrayList<Activity> toDoList;
        if (list.size() == 0) return null;
        Sort();

        Calendar calendar = java.util.Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = dateFormat.format(calendar.getTime());
        dateFormat = new SimpleDateFormat("HH:mm");
        String datetime = date + " " + dateFormat.format(calendar.getTime());

        for (int i = 0; i < list.size(); i++) {
            if (date.compareTo(list.get(i)) <= 0) {
                toDoList = getTodoList(list.get(i));
                for (int j = 0; j < toDoList.size(); j++)
                {
                    String endNext = toDoList.get(j).get_date() + " " + toDoList.get(j).get_timeEnd();
                    if (datetime.compareTo(endNext) <= 0) return toDoList.get(j);
                }
            }
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Activity getUpComingActivity() {
        ArrayList<String> list = getActivityDays();
        ArrayList<Activity> toDoList;
        if (list.size() == 0) return null;
        Sort();

        Calendar calendar = java.util.Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = dateFormat.format(calendar.getTime());
        dateFormat = new SimpleDateFormat("HH:mm");
        String datetime = date + " " + dateFormat.format(calendar.getTime());

        for (int i = 0; i < list.size(); i++) {
            if (date.compareTo(list.get(i)) <= 0) {
                toDoList = getTodoList(list.get(i));
                for (int j = 0; j < toDoList.size(); j++)
                {
                    String beginNext = toDoList.get(j).get_date() + " " + toDoList.get(j).get_timeStart();
                    if (datetime.compareTo(beginNext) < 0) return toDoList.get(j);
                }
            }
        }
        return null;
    }
}
