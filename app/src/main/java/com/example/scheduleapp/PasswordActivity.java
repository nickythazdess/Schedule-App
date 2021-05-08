package com.example.scheduleapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.io.IOException;
import java.util.List;

import io.paperdb.Paper;

public class PasswordActivity extends AppCompatActivity {
    String save_pattern_key = "pattern_code";
    PatternLockView mPatternLockView;
    String final_pattern;
    private Context context = this;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            this.requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,}, 201);
        }

        Paper.init(this);
        final String save_pattern = Paper.book().read(save_pattern_key);
        if(save_pattern != null && !save_pattern.equals("null")){
            setContentView(R.layout.lockscreen);
            mPatternLockView = findViewById(R.id.lockview_havePass);
            mPatternLockView.addPatternLockListener(new PatternLockViewListener() {
                @Override
                public void onStarted() {

                }
                @Override
                public void onProgress(List<PatternLockView.Dot> progressPattern) {

                }
                @RequiresApi(api = Build.VERSION_CODES.R)
                @Override
                public void onComplete(List<PatternLockView.Dot> pattern)  {
                    final_pattern = PatternLockUtils.patternToString(mPatternLockView,pattern);
                    if(final_pattern.equals(save_pattern)){
                        Intent intent = new Intent(PasswordActivity.this, MainActivity.class);
                        mPatternLockView.clearPattern();
                        ListDate listDate = ListDate.getInstance();
                        try {
                            listDate.updateDataFromFile(context);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.d("Load file", "Loaded");
                        startActivity(intent);
                    }
                    else {
                        mPatternLockView.clearPattern();
                    }
                }

                @Override
                public void onCleared() {
                    mPatternLockView.clearPattern();
                }
            });
        }
        else {
            setContentView(R.layout.activity_password);
            mPatternLockView = findViewById(R.id.lockview);
            mPatternLockView.addPatternLockListener(new PatternLockViewListener() {
                @Override
                public void onStarted() {

                }

                @Override
                public void onProgress(List<PatternLockView.Dot> progressPattern) {

                }

                @Override
                public void onComplete(List<PatternLockView.Dot> pattern) {
                    final_pattern = PatternLockUtils.patternToString(mPatternLockView, pattern);
                }

                @Override
                public void onCleared() {

                }
            });

            ImageButton btnSetup = (ImageButton) findViewById(R.id.set_password_button);
            btnSetup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Paper.book().write(save_pattern_key, final_pattern);
                    Intent intent = new Intent(PasswordActivity.this, MainActivity.class);
                    mPatternLockView.clearPattern();
                    startActivity(intent);
                }
            });

        }
    }
    @Override
    public void onBackPressed() {

    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}