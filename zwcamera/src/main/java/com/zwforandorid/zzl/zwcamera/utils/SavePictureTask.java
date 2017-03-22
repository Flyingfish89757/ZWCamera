package com.zwforandorid.zzl.zwcamera.utils;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.zwforandorid.zzl.zwcamera.model.MagicParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SavePictureTask extends AsyncTask<Bitmap, Integer, String>{
	
	private OnPictureSaveListener onPictureSaveListener;
	private File file;
	private ContentResolver contentResolver;

	public SavePictureTask(File file, OnPictureSaveListener listener, ContentResolver contentResolver){
		this.onPictureSaveListener = listener;
		this.file = file;
		this.contentResolver = contentResolver;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(final String result) {
		if(result != null)
			MediaScannerConnection.scanFile(MagicParams.context,
	                new String[] {result}, null,
	                new MediaScannerConnection.OnScanCompletedListener() {
	                    @Override
	                    public void onScanCompleted(final String path, final Uri uri) {
	                        if (onPictureSaveListener != null)
                                onPictureSaveListener.onSaved(result);
	                    }
            	});
	}

	@Override
	protected String doInBackground(Bitmap... params) {
		if(file == null)
			return null;
		return saveBitmap(params[0]);
	}
	
	private String saveBitmap(Bitmap bitmap) {
		if (file.exists()) {
			file.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(contentResolver, bitmap, null,null));
			out.flush();
			out.close();
			bitmap.recycle();
			return uri.toString();
		} catch (FileNotFoundException e) {
		   e.printStackTrace();
		} catch (IOException e) {
		   e.printStackTrace();
		}
		return null;
	}
	
	public interface OnPictureSaveListener{
		void onSaved(String result);
	}
}
