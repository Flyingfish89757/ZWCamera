package com.zwforandorid.zzl.zwcameraforandroid;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zwforandorid.zzl.zwcamera.ZWCameraActivity;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
private ImageView test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test = (ImageView) findViewById(R.id.test_im);
        TextView xx = (TextView) findViewById(R.id.tv_test);
        xx.setOnClickListener( this );
    }

        @Override public void onClick(final View v) {
                startActivity(v.getId());
        }

        @Override public void onRequestPermissionsResult(int requestCode, String[] permissions,
        int[] grantResults) {
            if (grantResults.length != 1 || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(requestCode);
            } else {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK){
            return;
        }
        if(requestCode == ZWCameraActivity.CAMERA_SELECT){
             Uri uri = Uri.parse(data.getStringExtra("img")) ;
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                test.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startActivity(int id) {
        Intent xxx =  new Intent(this, ZWCameraActivity.class);
        //target_path 照片保存目标路径
        xxx.putExtra("target_path","");
        startActivityForResult( xxx, ZWCameraActivity.CAMERA_SELECT);
        }
}
