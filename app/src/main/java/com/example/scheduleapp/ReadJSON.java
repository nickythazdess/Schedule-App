package com.example.scheduleapp;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ReadJSON {

    public static ArrayList<Diary> readDiaryJSONFile(Context context, String jsonText) throws IOException, JSONException {
        JSONObject jsonRoot = new JSONObject(readText(context, jsonText));
        ArrayList<Diary> diaries = new ArrayList<>();
        JSONArray jsonRootArray = jsonRoot.getJSONArray("diary_info");
        for (int j = 0; j < jsonRootArray.length(); j ++) {
            JSONObject jsonObject = jsonRootArray.getJSONObject(j);
            String _date = jsonObject.getString("date");
            String _content = jsonObject.getString("content");
            Diary diary = new Diary(_date, _content);
            diaries.add(diary);
        }
        return  diaries;
    }

    public static String readText(Context context, String file) throws IOException {
        InputStream is = context.openFileInput(file);
        BufferedReader br= new BufferedReader(new InputStreamReader(is));
        StringBuilder sb= new StringBuilder();
        String s= null;
        while((s = br.readLine())!=null) {
            sb.append(s);
            sb.append("\n");
        }
        return sb.toString();
    }

}

