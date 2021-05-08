package com.example.scheduleapp;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

public class DiaryItemActivity extends AppCompatActivity {

    private String _date,_content;
    private Integer code;
    private EditText dateView;
    private EditText contentView;
    private ActivityDiary note = ActivityDiary.getInstance();
    private ArrayList<Diary> list = note.getDiaryList();
    private DiaryFragment diaryFrag;
    boolean check = true;
    private jsonText jsonText= new jsonText();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_item);
        initComponents();
    }

    private void initComponents() {
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        _date = bundle.getString("date", "");
        code = bundle.getInt("code", 0);
        bundle.clear();

        dateView = findViewById(R.id.diary_date);
        contentView = findViewById(R.id.diary_content);

        if (code == 1) {
            Diary act = note.getDiary(_date);
            dateView.setText(act.get_date());
            contentView.setText(act.get_content());
        } else dateView.setText(_date);
    }

    public void save_diary(View view) {
        String newDate = "";
        _content = contentView.getText().toString();

        if (checkInput(newDate, _content)) {
            newDate = dateView.getText().toString();
            if (code == 0) note.addDiary(newDate, _content);
            else if (code == 1) {
                Diary temp = note.getDiary(newDate);
                temp.set_date(newDate);
                temp.set_content(_content);
            }
            StringWriter output = new StringWriter();
            try {
                WriteJSON.writeJson(output, note);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            onWrite(output.toString());
            finish();
        }
    }

    private boolean checkInput(String newDate, String content) {
        String noti = "";
        newDate = getDate();
        if (!check) return false;
        if (content.equals("")) noti = noti + "content!";
        if (newDate.equals("")) noti = noti + "date!";
        if (noti.equals("")) {
            if ((code == 0 && note.checkExist(newDate)) || (code == 1 && !newDate.equals(_date) && note.checkExist(newDate)))
            {
                Toast toast=Toast.makeText(this, "The diary of this date is already exist!", Toast.LENGTH_SHORT);
                toast.show();
                return false;
            }
            return true;
        }
        Toast toast=Toast.makeText(this, "Please input " + noti, Toast.LENGTH_SHORT);
        toast.show();
        return false;
    }

    private String getDate() {
        String _getDate = dateView.getText().toString();
        if (_getDate.length() > 0) {
            if (checkFormat(_getDate)) {
                String _day = _getDate.substring(0, 2);
                String _month = _getDate.substring(3, 5);
                String _year = _getDate.substring(6, _getDate.length());
                check = true;
                return _day + "-" + _month + "-"+ _year;
            } else
            {
                Toast toast= Toast.makeText(this, "WRONG DATE! Please input again as 'dd-mm-yyyy'!", Toast.LENGTH_SHORT);
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

    private void onWrite(String string) {
        try {
            FileOutputStream out = openFileOutput("diary.json", Context.MODE_PRIVATE);
            out.write(string.getBytes());
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}