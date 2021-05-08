package com.example.scheduleapp;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;

public class ScheduleItemActivity extends AppCompatActivity {
    TextView textViewTime;
    private final int[] saveWhich = {0};
    private int lastSelectedHour = -1;
    private int lastSelectedMinute = -1;
    private String _date,_title,_timeStart,_timeEnd,_info,_place,_GGplace, _child;
    private String _repeat = "Today";
    private LatLng _location;
    private Integer _position;
    private String[] data= {"Today", "Every day in this month", "Every week in this year", "Every month in this year", "Every next 10 year"};
    private TextView dateTextView;
    private EditText titleEditText;
    private TextView timeStartTextView;
    private TextView timeEndTextView;
    private EditText infoEditText;
    private EditText placeEditText;
    private EditText ggPlaceEditText;
    private CheckedTextView repeatEditText;
    ListDate listDate = ListDate.getInstance();
    private ArrayList<Activity> activities = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_item);
        initComponents();
        loadSearchBar(this);
    }

    private void loadSearchBar(Context context) {
        Places.initialize(getApplicationContext(), getString(R.string.google_api_key));
        PlacesClient placesClient = Places.createClient(context);

        AutocompleteSupportFragment autocompleteFragmentDest = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.GGPlace);


        autocompleteFragmentDest.setCountries("VN");
        autocompleteFragmentDest.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG, Place.Field.ID, Place.Field.NAME));

        autocompleteFragmentDest.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                _location = place.getLatLng();
                _GGplace = place.getName();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error
            }
        });
    }

    @SuppressLint("WrongViewCast")
    private void initComponents() {
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        _date = bundle.getString("date", "");
        _title = bundle.getString("title","");
        _timeStart=bundle.getString("time-start","");
        _timeEnd = bundle.getString("time-end","");
        _info = bundle.getString("info","");
        _place = bundle.getString("place","");
        _position = bundle.getInt("position",0);
        _GGplace = bundle.getString("GGPlace", "");
        _repeat = bundle.getString("repeat", "Today");
        String _longti, _lati;
        _longti = bundle.getString("longitude", "");
        _lati = bundle.getString("latitude", "");
        if (!_longti.equals("") && !_lati.equals(""))_location = new LatLng(Double.parseDouble(_lati), Double.parseDouble(_longti));

        dateTextView = findViewById(R.id.edit_date);
        titleEditText = findViewById(R.id.edit_title);
        timeStartTextView = findViewById(R.id.edit_time_start);
        timeEndTextView = findViewById(R.id.edit_time_end);
        infoEditText = findViewById(R.id.edit_info);
        placeEditText = findViewById(R.id.edit_place);
        repeatEditText = findViewById(R.id.edit_repeat);
        //ggPlaceEditText = findViewById(R.id.GGPlace);

        dateTextView.setText(_date);
        titleEditText.setText(_title);
        timeStartTextView.setText(_timeStart);
        timeEndTextView.setText(_timeEnd);
        infoEditText.setText(_info);
        placeEditText.setText(_place);
        repeatEditText.setText(_repeat);
        //ggPlaceEditText.setText(_GGplace);

        for (int i = 0; i < data.length; i++)
            if (_repeat.compareTo(data[i]) == 0) {
                saveWhich[0] = i;
            }
    }

    private void buttonSelectTime()  {
        if(this.lastSelectedHour == -1)  {
            // Get Current Time
            final Calendar c = Calendar.getInstance();
            this.lastSelectedHour = c.get(Calendar.HOUR_OF_DAY);
            this.lastSelectedMinute = c.get(Calendar.MINUTE);
        }

        // Time Set Listener.
        TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minute) -> {
            String _hour = String.valueOf(hourOfDay);
            String _minute = String.valueOf(minute);
            if (hourOfDay < 10) _hour = "0" + _hour;
            if (minute < 10) _minute = "0" + _minute;
            textViewTime.setText(_hour + ":" +  _minute);
            lastSelectedHour = hourOfDay;
            lastSelectedMinute = minute;
        };

        // Create TimePickerDialog:
        TimePickerDialog timePickerDialog = null;

        // TimePicker in Spinner Mode:
        timePickerDialog = new TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, timeSetListener, lastSelectedHour, lastSelectedMinute, true);

        // Show
        timePickerDialog.show();
    }

    public void set_time(View view) {
        textViewTime = findViewById(view.getId());
        buttonSelectTime();
    }

    public void set_repeat(View view) throws ParseException {
        final TextView textView = findViewById(R.id.edit_repeat);
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setSingleChoiceItems(data, saveWhich[0], (dialog, which) -> {
            _repeat = data[which];
            textView.setText(data[which]);
            saveWhich[0] = which;
        });
        b.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void set_everyday() throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormat.parse(_date);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        String _dateParent =_date;
        boolean check = true;
        int t = 30;
        switch (c.getTime().getMonth()+1){
            case 1: case 3: case 5: case 7: case 8: case 10: case 12:
                t = 30; break;
            case 2: t = 27; break;
            default:
                t = 29; break;
        }
        for (int i = 0; i<t; ++i){
            c.roll(Calendar.DATE,true);
            Date __Date = c.getTime();
            @SuppressLint("DefaultLocale") String res = String.format("%d-%02d-%02d", __Date.getYear() + 1900, __Date.getMonth() + 1, __Date.getDate());
            if (!listDate.haveDate(res)) {
                listDate.addDate(res);
                if (listDate.getTodoList(res) == null) {
                    listDate.addTodoList(res, new ArrayList<>());
                }

            }

            if (res.compareTo(_date) > 0 && checkRepeat(res, _timeStart)) {
                Activity activity;
                activity = new Activity(res, _title, _timeStart, _timeEnd, _place);
                activity.set_info(_info);
                activity.set_timeEnd(_timeEnd);
                activity.set_GGplace(_GGplace);
                activity.set_location(_location);
                activity.set_repeat(_repeat);

                listDate.getTodoList(res).add(activity);

                if (check) {
                    _child = res;
                    check = false;
                } else {
                    listDate.getTodoList(_dateParent).get(_position).set_childDate(res);
                }
                _dateParent = res;
                _position = listDate.getTodoList(_dateParent).size() - 1;
                //listDate.getTodoList(_dateParent).get(_position).set_childDate(_date);
            }
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void set_everyweek() throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormat.parse(_date);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        ArrayList<String> day = new ArrayList<>();

        String _dateParent = _date;
        boolean check = true;
        for (int i = 0; i<53; ++i){
            c.roll(Calendar.WEEK_OF_YEAR,true );
            Date __Date = c.getTime();
            @SuppressLint("DefaultLocale") String res = String.format("%d-%02d-%02d", __Date.getYear() + 1900, __Date.getMonth() + 1, __Date.getDate());
            day.add(res);
        }
        LinkedHashSet<String> set = new LinkedHashSet<>(day);
        day.clear();
        day.addAll(set);
        for (int i = 0; i<day.size()-1; i++){
            if (!listDate.haveDate(day.get(i))) {
                listDate.addDate(day.get(i));
                if (listDate.getTodoList(day.get(i)) == null) {
                    listDate.addTodoList(day.get(i), new ArrayList<>());
                }
            }

            if (day.get(i).compareTo(_date) > 0 && checkRepeat(day.get(i), _timeStart)) {
                Activity activity;
                activity = new Activity(day.get(i), _title, _timeStart, _timeEnd, _place);
                activity.set_info(_info);
                activity.set_timeEnd(_timeEnd);
                activity.set_GGplace(_GGplace);
                activity.set_location(_location);
                activity.set_repeat(_repeat);

                listDate.getTodoList(day.get(i)).add(activity);
                if (check) {
                    _child = day.get(i);
                    check = false;
                } else listDate.getTodoList(_dateParent).get(_position).set_childDate(day.get(i));
                _dateParent = day.get(i);
                _position = listDate.getTodoList(_dateParent).size() - 1;
                //listDate.getTodoList(_dateParent).get(_position).set_childDate(_date);
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void set_everymonth() throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormat.parse(_date);
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        String _dateParent = _date;
        boolean check = true;
        for (int i = 0; i < 11; ++i) {
            c.roll(Calendar.MONTH, true);
            Date __Date = c.getTime();
            @SuppressLint("DefaultLocale") String res = String.format("%d-%02d-%02d", __Date.getYear() + 1900, __Date.getMonth() + 1, __Date.getDate());
            if (!listDate.haveDate(res)) {
                listDate.addDate(res);
                if (listDate.getTodoList(res) == null) {
                    listDate.addTodoList(res, new ArrayList<>());
                }

            }
            if (res.compareTo(_date) > 0 && checkRepeat(res, _timeStart)) {
                Activity activity;
                activity = new Activity(res, _title, _timeStart, _timeEnd, _place);
                activity.set_info(_info);
                activity.set_timeEnd(_timeEnd);
                activity.set_GGplace(_GGplace);
                activity.set_location(_location);
                activity.set_repeat(_repeat);

                listDate.getTodoList(res).add(activity);

                if (check) {
                    _child = res;
                    check = false;
                } else {
                    listDate.getTodoList(_dateParent).get(_position).set_childDate(res);
                }
                _dateParent = res;
                _position = listDate.getTodoList(_dateParent).size() - 1;
                //listDate.getTodoList(_dateParent).get(_position).set_childDate(_date);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void set_everyyear() throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormat.parse(_date);
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        String _dateParent = _date;
        boolean check = true;
        for (int i = 0; i<9; ++i){
            c.roll(Calendar.YEAR,true);
            Date __Date = c.getTime();
            @SuppressLint("DefaultLocale") String res = String.format("%d-%02d-%02d", __Date.getYear() + 1900, __Date.getMonth() + 1, __Date.getDate());
            if (!listDate.haveDate(res)) {
                listDate.addDate(res);
                if (listDate.getTodoList(res) == null) {
                    listDate.addTodoList(res, new ArrayList<>());
                }

            }
            if (res.compareTo(_date) > 0 && checkRepeat(res, _timeStart)) {
                Activity activity;
                activity = new Activity(res, _title, _timeStart, _timeEnd, _place);
                activity.set_info(_info);
                activity.set_timeEnd(_timeEnd);
                activity.set_GGplace(_GGplace);
                activity.set_location(_location);
                activity.set_repeat(_repeat);

                listDate.getTodoList(res).add(activity);

                if (check) {
                    _child = res;
                    check = false;
                } else {
                    listDate.getTodoList(_dateParent).get(_position).set_childDate(res);
                }
                _dateParent = res;
                _position = listDate.getTodoList(_dateParent).size() - 1;
                //listDate.getTodoList(_dateParent).get(_position).set_childDate(_date);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void save_info(View view) {
        _timeStart = (String) timeStartTextView.getText();
        _timeEnd = (String) timeEndTextView.getText();
        _title = String.valueOf(titleEditText.getText());
        _info = String.valueOf(infoEditText.getText());
        _place = String.valueOf(placeEditText.getText());

        if (checkInput(_title, _timeStart, _timeEnd)) {
            Intent intent = new Intent(ScheduleItemActivity.this, ScheduleActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("date", _date);
            bundle.putString("title", _title);
            bundle.putString("time-start", _timeStart);
            bundle.putString("time-end", _timeEnd);
            bundle.putString("info", _info);
            bundle.putString("place", _place);
            bundle.putInt("position",_position);
            bundle.putString("GGPlace", _GGplace);
            if (_location != null) {
                bundle.putString("longitude", String.valueOf(_location.longitude));
                bundle.putString("latitude", String.valueOf(_location.latitude));
            } else {
                bundle.putString("longitude", "");
                bundle.putString("latitude", "");
            }
            switch (saveWhich[0]){
                default:
                    break;
                case 1:
                    try {
                        set_everyday();
                        bundle.putString("childDate", _child);
                        bundle.putString("repeat", data[1]);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    try {
                        set_everyweek();
                        bundle.putString("childDate", _child);
                        bundle.putString("repeat", data[2]);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    try {
                        set_everymonth();
                        bundle.putString("childDate", _child);
                        bundle.putString("repeat", data[3]);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    try {
                        set_everyyear();
                        bundle.putString("childDate", _child);
                        bundle.putString("repeat", data[4]);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            finish();
            bundle.clear();
        }
    }

    private boolean checkRepeat(String _date, String _timeStart) {
        ArrayList<Activity> list = listDate.getTodoList(_date);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).get_timeStart().compareTo(_timeStart) == 0) return false;
            if (list.get(i).get_timeEnd().compareTo("") != 0 && _timeStart.compareTo(list.get(i).get_timeEnd()) < 0) return false;
        }
        return true;
    }

    private boolean checkInput(String _title, String _timeStart, String _timeEnd) {
        String noti = "";
        if (_title == null) noti = noti + "title ";
        if (_timeStart.equals("")) noti = noti + "time-start ";
        if (_timeEnd.equals("")) noti = noti + "time-end ";
        if (noti.equals("")) {
            ListDate listDate = ListDate.getInstance();
            for (Activity key: listDate.getTodoList(_date) ) {
                if (key.get_timeStart().equals(_timeStart))
                {
                    Toast toast=Toast.makeText(this, "Cannot have 2 activities with the same starting time!", Toast.LENGTH_SHORT);
                    toast.show();
                    return false;
                }
                if (key.get_timeEnd().compareTo(_timeStart) > 0 && key.get_timeStart().compareTo(_timeStart) < 0) {
                    Toast toast=Toast.makeText(this, "Cannot have 2 activities in the same time!", Toast.LENGTH_SHORT);
                    toast.show();
                    return false;
                }
                if (key.get_timeStart().compareTo(_timeEnd) < 0 && key.get_timeStart().compareTo(_timeStart) > 0) {
                    Toast toast=Toast.makeText(this, "Cannot have 2 activities in the same time!", Toast.LENGTH_SHORT);
                    toast.show();
                    return false;
                }
            }
            if (_timeStart.compareTo(_timeEnd) >= 0 && _timeEnd.compareTo("") != 0) {
                Toast toast=Toast.makeText(this, "Starting time must be before ending time", Toast.LENGTH_SHORT);
                toast.show();
                return false;
            }
            return true;
        }
        Toast toast=Toast.makeText(this, "Please input " + noti, Toast.LENGTH_SHORT);
        toast.show();
        return false;

    }

    @Override
    public void onBackPressed() {

    }
}