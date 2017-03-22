package com.zwforandorid.zzl.zwcamera;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.hardware.Camera;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zwforandorid.zzl.zwcamera.model.MagicParams;
import com.zwforandorid.zzl.zwcamera.utils.CameraEngine;
import com.zwforandorid.zzl.zwcamera.utils.CameraHelper;
import com.zwforandorid.zzl.zwcamera.utils.MagicCameraView;
import com.zwforandorid.zzl.zwcamera.utils.MagicEngine;
import com.zwforandorid.zzl.zwcamera.utils.SavePictureTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jp.co.cyberagent.android.gpuimage.GPUImage;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class ZWCameraActivity extends AppCompatActivity implements View.OnClickListener {

    private Uri mUri;
    private ImageView take_photo;
    private ImageView img_preview;
    private TextView cancel;
    private TextView select;
    private MagicEngine magicEngine;
    private ImageView im_front;
    private TextView beautyLevel;
    private RelativeLayout rl_photo;
    public static final int CAMERA_SELECT = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zwcamera);

        MagicEngine.Builder builder = new MagicEngine.Builder();
        magicEngine = builder
                .build((MagicCameraView) findViewById(R.id.surfaceView));
        initView();
        initCamera();
    }

    private void initView(){
        im_front = (ImageView) findViewById(R.id.im_front);
        im_front.setOnClickListener(this);
        take_photo = (ImageView)findViewById(R.id.take_photo);
        take_photo.setOnClickListener(this);
        select = (TextView) findViewById(R.id.select);
        select.setOnClickListener(this);
        cancel = (TextView) findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        img_preview = (ImageView)findViewById(R.id.img_preview);
        beautyLevel= (TextView) findViewById(R.id.beauty_level);
        beautyLevel.setOnClickListener(this);
        rl_photo = (RelativeLayout) findViewById(R.id.rl_photo);
    }
    @Override
    protected void onResume() {
        super.onResume();
        CameraEngine.resumeCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CameraEngine.releaseCamera();
    }

    private void SaveSuccess(String result){
        if(result != null){
            mUri = Uri.parse(result);
            cancel.setText("重拍");
            select.setVisibility(View.VISIBLE);
            beautyLevel.setVisibility(View.GONE);
            rl_photo.setVisibility(View.GONE);
        }else {
            Toast.makeText(ZWCameraActivity.this,"图片获取失败",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.take_photo) {
            magicEngine.savePicture(getOutputMediaFile(), new SavePictureTask.OnPictureSaveListener() {
                @Override
                public void onSaved(final String result) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            SaveSuccess(result);
                        }
                    }) ;
                }
            }, getContentResolver());

        }else if(i == R.id.cancel){
            if(cancel.getText().equals("返回")){
                finish();
            }else if (cancel.getText().equals("重拍")){
                select.setVisibility(View.INVISIBLE);
                cancel.setText("返回");
                CameraEngine.startPreview();
                img_preview.setVisibility(View.GONE);
                beautyLevel.setVisibility(View.VISIBLE);
                rl_photo.setVisibility(View.VISIBLE);
            }
        }else if(i == R.id.select){
            Intent intent = getIntent();
            intent.putExtra("img",mUri.toString());
            setResult(RESULT_OK, intent);
            finish();
        }else if(i == R.id.im_front){
            magicEngine.switchCamera();
    }
        else if(i == R.id.beauty_level){
            new AlertDialog.Builder(ZWCameraActivity.this)
                    .setSingleChoiceItems(new String[]{"关闭", "1", "2", "3", "4", "5"}, MagicParams.beautyLevel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    magicEngine.setBeautyLevel(which);
                                    dialog.dismiss();
                                }
                            })
                    .setNegativeButton("取消", null)
                    .show();
        }
    }

    private void initCamera(){
        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        MagicCameraView cameraView = (MagicCameraView) findViewById(R.id.surfaceView);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cameraView.getLayoutParams();
        params.width = screenSize.x;
        params.height = screenSize.x * 4 / 3;
        cameraView.setLayoutParams(params);
    }

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }
}
