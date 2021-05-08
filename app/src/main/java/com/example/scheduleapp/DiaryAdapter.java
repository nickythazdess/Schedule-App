package com.example.scheduleapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DiaryAdapter extends ArrayAdapter<Diary> {

    public DiaryAdapter(@NonNull Context context, ArrayList<Diary> activities) {
        super(context, 0, activities);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) convertView = createRow(position, (ListView)parent);
        return  convertView;
    }

    private View createRow(int position, ListView parent) {
        Diary activity = getItem(position);
        View itemView = createANewRow(position);
        DisplayInfo(itemView, activity);
        return itemView;
    }

    private void DisplayInfo(View itemView, final Diary activity) {
        TextView dateView = (TextView) itemView.findViewById(R.id.dateOfDiary);
        dateView.setText(activity.get_date());

        TextView contentView = (TextView) itemView.findViewById(R.id.contentOfDiary);
        contentView.setText(activity.get_content());
    }

    private View createANewRow(int position) {
        return LayoutInflater.from(this.getContext()).inflate(R.layout.diary_listview_item, null);
    }
}
