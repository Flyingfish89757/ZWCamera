package com.zwforandorid.zzl.zwcameraforandroid;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zwforandorid.zzl.zwcamera.ZWCameraActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView xx = (TextView) findViewById(R.id.tv_test);
        xx.setOnClickListener( this );
    }

        @Override public void onClick(final View v) {
            if (PermissionChecker.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA },
                        v.getId());
            } else {
                startActivity(v.getId());
            }
        }

        @Override public void onRequestPermissionsResult(int requestCode, String[] permissions,
        int[] grantResults) {
            if (grantResults.length != 1 || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(requestCode);
            } else {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }

    private void startActivity(int id) {

        Intent xxx =  new Intent(this, ZWCameraActivity.class);
        startActivity( xxx );
        }
}
