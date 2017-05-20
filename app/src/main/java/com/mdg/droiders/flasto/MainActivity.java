package com.mdg.droiders.flasto;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.mdg.droiders.floaters.FloatingViewService;

public class MainActivity extends AppCompatActivity {
    private boolean isReturnedFromSettings = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Set and initialize the view elements.
     */
    private void initializeView() {
        findViewById(R.id.checkButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serviceIntent = new Intent(MainActivity.this, FloatingViewService.class);
                startService(serviceIntent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isReturnedFromSettings) {
            isReturnedFromSettings = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                //If the draw over permission is not available open the settings screen
                //to grant the permission.
                isReturnedFromSettings = true;
                Toast.makeText(this, "System overlay permission denied, closing app", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                initializeView();
            }

        } else {
            //Check if the application has draw over other apps permission or not?
            //This permission is by default available for API<23. But for API > 23
            //you have to ask for the permission in runtime.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {


                //If the draw over permission is not available open the settings screen
                //to grant the permission.
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                isReturnedFromSettings = true;
                Toast.makeText(this, "Flasto needs permission to draw over other apps", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            } else {
                initializeView();
            }
        }
    }
}


