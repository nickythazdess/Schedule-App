package com.example.scheduleapp;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class NextActivity extends Fragment {

    ListDate listDate = ListDate.getInstance();
    Activity yourNextActivity;
    private ViewGroup myViewGroup;
    private Context myContext;
    private TextView dateTextView;
    private TextView titleEditText;
    private TextView timeStartTextView;
    private TextView timeEndTextView;
    private TextView infoEditText;
    private TextView placeEditText;
    private TextView ggPlaceEditText;

    public NextActivity() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        if (yourNextActivity != null) {
            dateTextView = myViewGroup.findViewById(R.id.edit_date);
            titleEditText = myViewGroup.findViewById(R.id.edit_title);
            timeStartTextView = myViewGroup.findViewById(R.id.edit_time_start);
            timeEndTextView = myViewGroup.findViewById(R.id.edit_time_end);
            infoEditText = myViewGroup.findViewById(R.id.edit_info);
            placeEditText = myViewGroup.findViewById(R.id.edit_place);
            ggPlaceEditText = myViewGroup.findViewById(R.id.GGPlace);

            dateTextView.setText(yourNextActivity.get_date());
            titleEditText.setText(yourNextActivity.get_title());
            timeStartTextView.setText(yourNextActivity.get_timeStart());
            timeEndTextView.setText(yourNextActivity.get_timeEnd());
            if (!yourNextActivity.get_info().equals("")) infoEditText.setText(yourNextActivity.get_info());
            if (!yourNextActivity.get_place().equals("")) placeEditText.setText(yourNextActivity.get_place());
            if (!yourNextActivity.get_GGplace().equals("")) ggPlaceEditText.setText(yourNextActivity.get_GGplace());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.myContext = container.getContext();
        this.myViewGroup = container;
        this.yourNextActivity = listDate.getNextActivity();
        if (yourNextActivity != null) return inflater.inflate(R.layout.fragment_next_activity, container, false);
        else return inflater.inflate(R.layout.null_next_activity, container, false);
    }
}