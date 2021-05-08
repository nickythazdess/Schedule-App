package com.example.scheduleapp;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainCalendar extends Fragment {

    private DatePicker datePicker;
    private EditText editTextDate;
    private Button buttonDate;
    private Calendar calendar;
    private Context myContext;
    private ViewGroup myViewGroup;
    ListDate listDate = ListDate.getInstance();
    private boolean check = false;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        editTextDate = myViewGroup.findViewById(R.id.editText_date);
        buttonDate = myViewGroup.findViewById(R.id.button_date);
        datePicker = myViewGroup.findViewById(R.id.datePicker);

        calendar = java.util.Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int year = calendar.get(java.util.Calendar.YEAR);
        int month  = calendar.get(java.util.Calendar.MONTH);
        int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
        setDate(year, month, day);
        datePicker.init( year, month , day , new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                datePickerChange(datePicker, year, month,   dayOfMonth);
            }
        });

        buttonDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = getDate();
                if (check) {
                    Intent intent = new Intent(myContext, ScheduleActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("date", date);
                    intent.putExtras(bundle);
                    if (!listDate.haveDate(date)) {
                        listDate.addDate(date);
                    }
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.myViewGroup = container;
        this.myContext = container.getContext();
        return inflater.inflate(R.layout.fragment_main_calendar, container, false);
    }

    private void datePickerChange(DatePicker datePicker, int year, int month, int dayOfMonth) {
        Log.d("Date", "Year=" + year + " Month=" + (month + 1) + " day=" + dayOfMonth);
        setDate(year, month, dayOfMonth);
    }

    private void setDate(int year, int month, int dayOfMonth) {
        String _year = String.valueOf(year);
        String _month = String.valueOf(month + 1); // 0 - 11
        String _day = String.valueOf(dayOfMonth);

        if (_month.length() == 1) _month = "0" + _month;
        if (_day.length() == 1) _day = "0" + _day;
        this.editTextDate.setText(_day + "-" + _month + "-" + _year);
    }

    private String getDate() {
        String _getDate = String.valueOf(this.editTextDate.getText());
        if (_getDate.length() > 0) {
            if (checkFormat(_getDate)) {
                String _day = _getDate.substring(0, 2);
                String _month = _getDate.substring(3, 5);
                String _year = _getDate.substring(6);
                check = true;
                return _year + "-" + _month + "-" + _day;
            } else
            {
                Toast toast= Toast.makeText(myContext, "WRONG FORMAT! Please input again!", Toast.LENGTH_SHORT);
                toast.show();
                check = false;
            }
        } return _getDate;
    }

    private boolean checkFormat(String s) {
        for (int i = 0; i < s.length(); i++) {
            switch (i) {
                case 2: case 5:
                    if (s.charAt(i) != '-') return false;
                    break;
                default:
                    if ((int) s.charAt(i) > 57 || (int) s.charAt(i) < 48) return false;
                    break;
            }
        }
        String _day = s.substring(0, 2);
        Integer day = Integer.valueOf(_day);
        String _month = s.substring(3, 5);
        Integer month = Integer.valueOf(_month);
        String _year = s.substring(6, s.length());
        Integer year = Integer.valueOf(_year);
        if (month <= 0 || month >= 13) return false;
        if (year % 4 == 0) {
            if (month == 2 && day > 29) return false;
        } else if (month == 2 && day > 28) return false;
        if (day <= 0 || day >= 31) return false;
        return true;
    }
}