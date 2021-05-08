package com.example.scheduleapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DiaryFragment extends Fragment {
    private ActivityDiary note = ActivityDiary.getInstance();
    private ArrayList<Diary> list = note.getDiaryList();;

    private Context myContext;
    private ViewGroup myViewGroup;
    ArrayAdapter<Diary> diaryAdapter;
    ListView listView;
    ImageButton btnAdd;
    private Calendar calendar;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        readJSONFile();
        listView = (ListView) myViewGroup.findViewById(R.id.DiaryContainer);
        diaryAdapter = new DiaryAdapter(myContext, list);
        btnAdd = (ImageButton) myViewGroup.findViewById(R.id.addDiaryBtn);
        btnAdd.setOnClickListener(view1 -> {
            Intent intent = new Intent(myContext, DiaryItemActivity.class);
            calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            dateFormat.setLenient(false);
            String date = dateFormat.format(calendar.getTime());

            Bundle bundle2 = new Bundle();
            bundle2.putString("date", date);
            bundle2.putInt("code", 0);

            intent.putExtras(bundle2);
            startActivityForResult(intent, 1111);
        });
        updateADiary();
        deleteADiary();
    }

    private void readJSONFile() {
        try {
            list = ReadJSON.readDiaryJSONFile(myContext, "diary.json");
            note.setListDiary(list);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        note.Sort();
        listView.setAdapter(diaryAdapter);
        diaryAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.myViewGroup = container;
        this.myContext = container.getContext();
        return inflater.inflate(R.layout.fragment_diary, container, false);
    }

    private void updateADiary() {
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(myContext,DiaryItemActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("date", list.get(position).get_date());
            bundle.putInt("code", 1);
            intent.putExtras(bundle);
            startActivityForResult(intent, 2222);
        });
    }

    private void deleteADiary() {
        listView.setOnItemLongClickListener((arg0, arg1, pos, id) -> {
            AlertDialog.Builder b = new AlertDialog.Builder(myContext);
            b.setTitle("CONFIRM");
            b.setMessage("Do you want to delete this diary?");
            //OK
            b.setPositiveButton("YES", (dialog, id1) -> {
                list.remove(pos);
                listView.setAdapter(diaryAdapter);
                diaryAdapter.notifyDataSetChanged();
            });
            //Cancel
            b.setNegativeButton("Cancel", (dialog, id12) -> dialog.cancel());
            AlertDialog al = b.create();
            al.show();
            return true;
        });
    }
}