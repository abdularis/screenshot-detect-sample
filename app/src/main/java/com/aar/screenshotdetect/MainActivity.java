package com.aar.screenshotdetect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onDetectSsClick(View view) {
        Intent intent = new Intent(this, DetectSsActivity.class);
        startActivity(intent);
    }

    public void onSecDispClick(View view) {
        Intent intent = new Intent(this, SecureDisplayActivity.class);
        startActivity(intent);
    }
}
