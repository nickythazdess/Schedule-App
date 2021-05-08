package com.example.scheduleapp;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import static android.content.Context.MODE_APPEND;
import static android.content.Context.MODE_PRIVATE;

public class SaveLoad {
    ListDate listDate = ListDate.getInstance();
    public SaveLoad() {
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public void save(Context context){
        Gson gson = new Gson();
        String json = gson.toJson(listDate.getListDate());
        writeToFile(json, context);
    }
    @RequiresApi(api = Build.VERSION_CODES.R)
    private void writeToFile(String data, Context context) {
        try
        {
            FileOutputStream fOut = context.openFileOutput("data.json", MODE_PRIVATE);
            fOut.write(data.getBytes());
            fOut.close();
        }
        catch (IOException e)
        {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public String ReadFromFile(Context context) throws IOException {
        try {
            return readText(context, "data.json");
        } catch (FileNotFoundException e) {
            return "";
        }

    }

    public static String readText(Context context, String file) throws IOException {
        InputStream is = context.openFileInput(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String s = null;
        while ((s = br.readLine()) != null) {
            sb.append(s);
            sb.append("\n");
        }
        return sb.toString();
    }
}
