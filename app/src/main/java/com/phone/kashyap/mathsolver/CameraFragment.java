package com.phone.kashyap.mathsolver;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraFragment extends Fragment
{

	private static final String LOG_TAG = CameraFragment.class.getSimpleName();
	private static final int CROP_INTENT = 2;
	public static final int MEDIA_TYPE_IMAGE = 1;
	private static final String CROP_ERROR = "Whoops - your device doesn't support the crop action!";
	private static Camera _camera;
	private static CameraPreview _cameraPreview;
	private ProcessImage _processImage = new ProcessImage(getActivity());

	private Camera.PictureCallback _pictureCallback = new Camera.PictureCallback()
	{
		@Override
		public void onPictureTaken(byte[] data, Camera camera)
		{
			Log.d(LOG_TAG, "Image Captured");
			File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
			Uri outputFileUri = Uri.fromFile(pictureFile);

			if (pictureFile == null) Log.d(LOG_TAG, "File Permissions messed up!");

			try
			{
				FileOutputStream fos = new FileOutputStream(pictureFile);
				fos.write(data);
				fos.close();
			} catch (FileNotFoundException e)
			{
				Log.d(LOG_TAG, "File not found: " + e.getMessage());
			} catch (IOException e)
			{
				Log.d(LOG_TAG, "Error accessing file: " + e.getMessage());
			}

			Intent cropIntent = _processImage.getCropIntent(outputFileUri);
			if(cropIntent != null)
				startActivityForResult(cropIntent, CROP_INTENT);
			else
				Toast.makeText(getActivity(), CROP_ERROR, Toast.LENGTH_SHORT).show();
		}
	};

	public CameraFragment() {}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Log.d(LOG_TAG, "onCreateView");
		View rootView = inflater.inflate(R.layout.fragment_camera, container, false);
		RelativeLayout preview = (RelativeLayout) rootView.findViewById(R.id.c_surface);

		_camera = getCameraInstance();
		_cameraPreview = new CameraPreview(getActivity(), _camera);
		setCameraDisplayOrientation(getActivity(), Camera.CameraInfo.CAMERA_FACING_BACK, _camera);
		preview.addView(_cameraPreview);

		Button buttonSnap = (Button) rootView.findViewById(R.id.button_capture);
		buttonSnap.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				_camera.takePicture(null, null, _pictureCallback);
			}
		});
		return rootView;
	}


	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		Log.d(LOG_TAG, "onDestroyView");
		releaseCamera();
	}

	private static Camera getCameraInstance()
	{
		Camera camera = null;
		try
		{
			camera = Camera.open();
			camera.setDisplayOrientation(90);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return camera;
	}

	/* Create a File for saving an image or video */
	private static File getOutputMediaFile(int type)
	{
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MathSolver");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists())
		{
			if (!mediaStorageDir.mkdirs())
			{
				Log.d("MathSolver", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE)
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
		else return null;

		return mediaFile;
	}

	private void releaseCamera()
	{
		if (_camera != null)
		{
			_camera.release();
			_camera = null;
		}
	}

	public static void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera)
	{

		Camera.Parameters parameters = camera.getParameters();
		Log.d(CameraPreview.class.getSimpleName(), "setCameraOri");
		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
		int degrees = 0;
		switch (rotation)
		{
			case Surface.ROTATION_0:
				degrees = 0;
				parameters.setRotation(90);
				break;
			case Surface.ROTATION_90:
				degrees = 90;
				break;
			case Surface.ROTATION_180:
				degrees = 180;
				break;
			case Surface.ROTATION_270:
				degrees = 270;
				parameters.setRotation(180);
				break;
		}
		camera.setParameters(parameters);

		int result = (info.orientation - degrees + 360) % 360;
		camera.setDisplayOrientation(result);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		Log.d(LOG_TAG, "onActivityResult, Result Code = " + String.valueOf(resultCode));
		String finalText = _processImage.getTextFromImage(requestCode, data);
		Toast.makeText(getActivity(), finalText, Toast.LENGTH_SHORT).show();
	}
}