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
    private ImageView m_take_photo;
    private ImageView m_img_preview;
    private TextView mCancel;
    private TextView mSelect;
    private MagicEngine mMagicEngine;
    private ImageView m_im_front;
    private TextView mBeautyLevel;
    private RelativeLayout rl_photo;
    private File m_photo_file;
    public static final int CAMERA_SELECT = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zwcamera);
        MagicEngine.Builder builder = new MagicEngine.Builder();
        mMagicEngine = builder
                .build((MagicCameraView) findViewById(R.id.surfaceView));
        getTargetPath();
        initView();
        initCamera();
    }

    private void initView(){
        m_im_front = (ImageView) findViewById(R.id.im_front);
        m_im_front.setOnClickListener(this);
        m_take_photo = (ImageView)findViewById(R.id.take_photo);
        m_take_photo.setOnClickListener(this);
        mSelect = (TextView) findViewById(R.id.select);
        mSelect.setOnClickListener(this);
        mCancel = (TextView) findViewById(R.id.cancel);
        mCancel.setOnClickListener(this);
        m_img_preview = (ImageView)findViewById(R.id.img_preview);
        mBeautyLevel= (TextView) findViewById(R.id.beauty_level);
        mBeautyLevel.setOnClickListener(this);
        rl_photo = (RelativeLayout) findViewById(R.id.rl_photo);
    }
    private void getTargetPath(){
        String mTarget = getIntent().getStringExtra("target_path");
        if(mTarget.isEmpty() && !mTarget.equals("")){
            m_photo_file = new File(mTarget);
        }else {
            m_photo_file = getOutputMediaFile();
        }
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
            mCancel.setText("重拍");
            mSelect.setVisibility(View.VISIBLE);
            mBeautyLevel.setVisibility(View.GONE);
            rl_photo.setVisibility(View.GONE);
        }else {
            Toast.makeText(ZWCameraActivity.this,"图片获取失败",Toast.LENGTH_SHORT).show();
        }
    }
    boolean b = false; // 拍照状态判断
    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.take_photo) {
            if(b){
                return;
            }
            b = true;
            mMagicEngine.savePicture(m_photo_file, new SavePictureTask.OnPictureSaveListener() {
                @Override
                public void onSaved(final String result) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            SaveSuccess(result);
                            b = false;
                        }
                    }) ;
                }
            }, getContentResolver());

        }else if(i == R.id.cancel){
            if(mCancel.getText().equals("返回")){
                finish();
            }else if (mCancel.getText().equals("重拍")){
                mSelect.setVisibility(View.INVISIBLE);
                mCancel.setText("返回");
                CameraEngine.startPreview();
                m_img_preview.setVisibility(View.GONE);
                mBeautyLevel.setVisibility(View.VISIBLE);
                rl_photo.setVisibility(View.VISIBLE);
            }
        }else if(i == R.id.select){
            Intent intent = getIntent();
            intent.putExtra("img",mUri.toString());
            setResult(RESULT_OK, intent);
            finish();
        }else if(i == R.id.im_front){
            mMagicEngine.switchCamera();
    }
        else if(i == R.id.beauty_level){
            new AlertDialog.Builder(ZWCameraActivity.this)
                    .setSingleChoiceItems(new String[]{"关闭", "1", "2", "3", "4", "5"}, MagicParams.beautyLevel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    mMagicEngine.setBeautyLevel(which);
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
        //设置图片显示比例
        params.width = screenSize.x;
        params.height = screenSize.x * 4 / 3;
        cameraView.setLayoutParams(params);
    }

    private  File getOutputMediaFile() {
        File mediaStorageDir = getExternalCacheDir();
        // Create a media file name
        String timeStamp = "ZWCamera";
        File mediaFile;
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }
}
