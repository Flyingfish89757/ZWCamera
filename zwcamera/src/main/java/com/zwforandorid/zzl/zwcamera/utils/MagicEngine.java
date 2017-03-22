package com.zwforandorid.zzl.zwcamera.utils;

import android.content.ContentResolver;

import com.zwforandorid.zzl.zwcamera.model.MagicParams;

import java.io.File;

/**
 * Created by why8222 on 2016/2/25.
 */
public class MagicEngine {
    private static MagicEngine magicEngine;

    public static MagicEngine getInstance(){
        if(magicEngine == null)
            throw new NullPointerException("MagicEngine must be built first");
        else
            return magicEngine;
    }

    private MagicEngine(Builder builder){

    }

    public void setFilter(MagicFilterType type){
        MagicParams.magicBaseView.setFilter(type);
    }

    public void savePicture(File file, SavePictureTask.OnPictureSaveListener listener, ContentResolver contentResolver){
        SavePictureTask savePictureTask = new SavePictureTask(file, listener,contentResolver);
        MagicParams.magicBaseView.savePicture(savePictureTask,contentResolver);
    }

    public void startRecord(){
        if(MagicParams.magicBaseView instanceof MagicCameraView)
            ((MagicCameraView)MagicParams.magicBaseView).changeRecordingState(true);
    }

    public void stopRecord(){
        if(MagicParams.magicBaseView instanceof MagicCameraView)
            ((MagicCameraView)MagicParams.magicBaseView).changeRecordingState(false);
    }

    public void setBeautyLevel(int level){
        if(MagicParams.magicBaseView instanceof MagicCameraView && MagicParams.beautyLevel != level) {
            MagicParams.beautyLevel = level;
            ((MagicCameraView) MagicParams.magicBaseView).onBeautyLevelChanged();
        }
    }

    public void switchCamera(){
        CameraEngine.switchCamera();
    }
    public static class Builder{

        public MagicEngine build(MagicBaseView magicBaseView) {
            MagicParams.context = magicBaseView.getContext();
            MagicParams.magicBaseView = magicBaseView;
            return new MagicEngine(this);
        }
    }
}
