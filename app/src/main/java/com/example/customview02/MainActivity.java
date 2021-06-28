package com.example.customview02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private HealthProgressBarView mProgressView;
    private TextView mProgressTv;
    private float progressRatio = 300f;
    private float totalSize = 360f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressView = findViewById(R.id.health_view);
        mProgressTv = findViewById(R.id.progress_tv);
        mProgressView.setHealthProgressBar(progressRatio,totalSize);
        mProgressView.setOnProgressChangedListener(progress -> mProgressTv.setText("进度" + progress + "%"));
    }

}