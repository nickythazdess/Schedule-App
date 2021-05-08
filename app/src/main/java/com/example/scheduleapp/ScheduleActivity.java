package com.example.scheduleapp;


import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ScheduleActivity extends AppCompatActivity {

    private ArrayList<Activity> activities = new ArrayList<>();
    ArrayAdapter<Activity> scheduleAdapter;
    ListDate listDate = ListDate.getInstance();
    ListView listView;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        listDate.Sort();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        init();
        listDate.setMyContext(this);
    }

    public void addActivity(View view) {
        Intent intent = new Intent(ScheduleActivity.this, ScheduleItemActivity.class);
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        String _date = bundle.getString("date", "");
        bundle.clear();
        Bundle bundle2 = new Bundle();
        bundle2.putString("date", _date);
        bundle2.putString("title","");
        bundle2.putString("time-start","");
        bundle2.putString("time-end:","");
        bundle2.putString("info","");
        bundle2.putString("place","");
        bundle2.putInt("position",0);
        bundle2.putString("GGPlace", "");
        bundle2.putString("longitude", "");
        bundle2.putString("latitude", "");
        bundle2.putString("childDate", "");
        bundle2.putString("repeat", "Today");
        intent.putExtras(bundle2);
        startActivityForResult(intent, 2402);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void init() {
        listView = findViewById(R.id.schedule);
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        String _date = bundle.getString("date", "");
        if (listDate.getTodoList(_date) == null) {
            listDate.addTodoList(_date, activities);
        }
        scheduleAdapter = new ScheduleAdapter(this, 0, listDate.getTodoList(_date));
        scheduleAdapter.notifyDataSetChanged();
        listView.setAdapter(scheduleAdapter);

        updateAnItem(_date);
        deleteAnItem(_date);
    }

    private void updateAnItem(String _date) {
        listView.setOnItemClickListener((parent, view, position, id) -> {
            ArrayList<Activity> todoList = listDate.getTodoList(_date);
            Bundle bundle = new Bundle();
            Intent intent = new Intent(ScheduleActivity.this,ScheduleItemActivity.class);
            bundle.putString("date", todoList.get(position).get_date());
            bundle.putString("title",todoList.get(position).get_title());
            bundle.putString("time-start",todoList.get(position).get_timeStart());
            bundle.putString("time-end",todoList.get(position).get_timeEnd());
            bundle.putString("info",todoList.get(position).get_info());
            bundle.putString("place",todoList.get(position).get_place());
            bundle.putInt("position",position);
            bundle.putString("GGPlace", todoList.get(position).get_GGplace());
            bundle.putString("longitude", todoList.get(position).get_longi());
            bundle.putString("latitude", todoList.get(position).get_lati());
            bundle.putString("childDate", todoList.get(position).get_childDate());
            bundle.putString("repeat", todoList.get(position).get_repeat());
            intent.putExtras(bundle);
            String _oldTimeStart = todoList.get(position).get_timeStart();
            String _oldTitle = todoList.get(position).get_title();
            String _childDate = todoList.get(position).get_childDate();
            todoList.remove(position);
            while (_childDate != null && !_childDate.equals(_date) && !_childDate.equals("")) {
                ArrayList<Activity> list = listDate.getTodoList(_childDate);
                _childDate = null;
                for (int i = 0; i<list.size(); i++) {
                    if (list.get(i).get_timeStart().compareTo(_oldTimeStart) == 0 && list.get(i).get_title().compareTo(_oldTitle) == 0) {
                        _childDate = list.get(i).get_childDate();
                        list.remove(i);
                        break;
                    }
                }
            }
            startActivityForResult(intent, 908);
        });
        scheduleAdapter.notifyDataSetChanged();
        listView.setAdapter(scheduleAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void deleteAnItem(String _date) {
        listView.setOnItemLongClickListener((arg0, arg1, pos, id) -> {
            AlertDialog.Builder b = new AlertDialog.Builder(ScheduleActivity.this);
            b.setTitle("CONFIRM");
            b.setMessage("Do you want to delete this activity?");
            //OK
            b.setPositiveButton("YES", (dialog, id1) -> {
                ArrayList<Activity> todoList = listDate.getTodoList(_date);
                String _childDate = todoList.get(pos).get_childDate();
                String _title = todoList.get(pos).get_title();
                String _timeStart = todoList.get(pos).get_timeStart();
                todoList.remove(pos);
                while (_childDate != null && !_childDate.equals("")) {
                    ArrayList<Activity> list = listDate.getTodoList(_childDate);
                    _childDate = null;
                    for (int i = 0; i<list.size(); i++) {
                        if (list.get(i).get_timeStart().compareTo(_timeStart) == 0 && list.get(i).get_title().compareTo(_title) == 0) {
                            _childDate = list.get(i).get_childDate();
                            list.remove(i);
                            listDate.setAlarmForNextActivity();
                            break;
                        }
                    }
                }
                scheduleAdapter.notifyDataSetChanged();
                listView.setAdapter(scheduleAdapter);
            });
            //Cancel
            b.setNegativeButton("Cancel", (dialog, id12) -> dialog.cancel());

            AlertDialog al = b.create();

            al.show();
            return true;
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            assert data != null;
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                String _date = bundle.getString("date", "");
                String _title = bundle.getString("title", "");
                String _timeStart = bundle.getString("time-start", "");
                String _timeEnd = bundle.getString("time-end", "");
                String _info = bundle.getString("info", "");
                String _place = bundle.getString("place", "");
                String _GGplace = bundle.getString("GGPlace", "");
                String _childDate = bundle.getString("childDate", "");
                String _repeat = bundle.getString("repeat", "Today");
                String _longti, _lati;
                _longti = bundle.getString("longitude", "");
                _lati = bundle.getString("latitude", "");
                LatLng _location;
                if (!_lati.equals("") && !_longti.equals("")) _location = new LatLng(Double.parseDouble(_lati), Double.parseDouble(_longti));
                else _location = null;
                bundle.clear();

                if (!_timeStart.equals("")) {
                    Activity activity = null;
                    activity = new Activity(_date, _title, _timeStart, _timeEnd, _place);
                    activity.set_info(_info);
                    activity.set_timeEnd(_timeEnd);
                    activity.set_GGplace(_GGplace);
                    activity.set_longi(_longti);
                    activity.set_lati(_lati);
                    activity.set_location(_location);
                    activity.set_childDate(_childDate);
                    activity.set_repeat(_repeat);

                    listDate.getTodoList(_date).add(activity);
                    listDate.Sort();

                    listView = findViewById(R.id.schedule);

                    scheduleAdapter = new ScheduleAdapter(this, 0, listDate.getTodoList(_date));
                    scheduleAdapter.notifyDataSetChanged();
                    listView.setAdapter(scheduleAdapter);
                }
            }
        }
    }


    @Override
    public void onBackPressed() {

    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public void backToMain(View view) {
        listDate.Sort();
        SaveLoad saveApp = new SaveLoad();
        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            this.requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, 200);
        saveApp.save(this);
        listDate.setAlarmForNextActivity();
        finish();
    }
}