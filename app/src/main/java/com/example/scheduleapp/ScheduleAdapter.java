package com.example.scheduleapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ScheduleAdapter extends ArrayAdapter<Activity> {

    public ScheduleAdapter(@NonNull Context context, int resource, ArrayList<Activity> activities) {
        super(context, resource, activities);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null)
        {
            convertView = createRow(position, (ListView)parent);
        }
        return  convertView;
    }

    private View createRow(int position, ListView parent) {
        Activity activity = getItem(position);
        View itemView;
        @SuppressLint("SimpleDateFormat") DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        dateFormatter.setLenient(false);
        Date today = new Date();
        String s = dateFormatter.format(today);
        String time = "";
        if (activity.get_timeEnd().compareTo("") == 0) {
            time = activity.get_date() + " " + activity.get_timeStart();
        } else {
            time = activity.get_date() + " " + activity.get_timeEnd();
        }
        if (time.compareTo(s) > 0) itemView = createANewRow(position);
        else itemView = createANewRow2(position);
        DisplayInfo(itemView, activity);
        return itemView;
    }

    private void DisplayInfo(View itemView, final Activity activity) {
        TextView timeView = (TextView) itemView.findViewById(R.id.time_start);
        timeView.setText(activity.get_timeStart());

        TextView titleView = (TextView) itemView.findViewById(R.id.title);
        titleView.setText(activity.get_title());

        TextView placeView = (TextView) itemView.findViewById(R.id.place);
        placeView.setText(activity.get_place());
    }

    private View createANewRow(int position) {
        return LayoutInflater.from(this.getContext()).inflate(R.layout.schedule_listview_item, null);
    }

    private View createANewRow2(int position) {
        return LayoutInflater.from(this.getContext()).inflate(R.layout.schedule_listview_item2, null);
    }
}
