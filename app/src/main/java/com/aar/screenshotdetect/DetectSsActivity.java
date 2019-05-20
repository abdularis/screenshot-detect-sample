package com.aar.screenshotdetect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.aar.screenshotdetect.ssdetect.SsDetect;

public class DetectSsActivity extends AppCompatActivity {

    private TextView mTextFile;

    private SsDetect mSsDetect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detech_ss);

        checkReadExternalStorage();

        mTextFile = findViewById(R.id.text_filename);

        mSsDetect = new SsDetect(getContentResolver(), new SsDetect.OnScreenshotTakenListener() {
            @Override
            public void onScreenshotTaken(String path) {
                mTextFile.setText(path);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSsDetect.startDetecting();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSsDetect.stopDetecting();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "This screenshot detect technique needs read external storage permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void checkReadExternalStorage() {
        boolean granted = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;

        if (!granted) {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE }, 100);
        }
    }
}
