package com.example.scheduleapp;

import android.util.JsonWriter;

import org.json.JSONException;

import java.io.IOException;
import java.io.Writer;


public class WriteJSON {
      public static void writeJson(Writer output, ActivityDiary diaryInfos) throws IOException, JSONException {

        JsonWriter jsonWriter = new JsonWriter(output);

        jsonWriter.beginObject();

        jsonWriter.name("diary_info").beginArray();
        for (int i = 0; i < diaryInfos.getDiaryList().size(); i++)
        {
            jsonWriter.beginObject();
            jsonWriter.name("date").value(diaryInfos.getDiaryList().get(i).get_date());
            jsonWriter.name("content").value(diaryInfos.getDiaryList().get(i).get_content());
            jsonWriter.endObject();
        }
        jsonWriter.endArray();
        jsonWriter.endObject();
    }
}
