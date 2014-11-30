package com.phone.kashyap.mathsolver;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

/**
 * Created by Kashyap on 11/18/2014.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback
{
	private static final String LOG_TAG = CameraPreview.class.getSimpleName();
	private Context _context;
	private SurfaceHolder _holder;
	private Camera _camera;
	private Camera.Parameters _cameraParameters;

	public CameraPreview(Context context, Camera camera)
	{
		super(context);
		_context = context;
		_camera = camera;
		_cameraParameters = _camera.getParameters();
		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		_holder = getHolder();
		_holder.addCallback(this);
	}

	public void surfaceCreated(SurfaceHolder surfaceHolder)
	{
		int orientation = getResources().getConfiguration().orientation;
		Log.d(LOG_TAG, "In surfaceCreated");
		// The Surface has been created, now tell the camera where to draw the preview.

		/*if(orientation == 1)
			_cameraParameters.setRotation(90);

		if(orientation == 2)
			_cameraParameters.setRotation(0);
		*/


		try
		{
			List<Camera.Size> sizes = _cameraParameters.getSupportedPreviewSizes();
			_cameraParameters.setPreviewSize(sizes.get(0).width, sizes.get(0).height);
			_cameraParameters.setPictureFormat(ImageFormat.JPEG);
			_cameraParameters.setFocusMode(_cameraParameters.FOCUS_MODE_CONTINUOUS_PICTURE);
			_camera.setParameters(_cameraParameters);
			_camera.setPreviewDisplay(_holder);
			_camera.startPreview();
		} catch (IOException e)
		{
			Log.d(LOG_TAG, "Error setting camera preview: " + e.getMessage());
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int w, int h)
	{
		Log.d(LOG_TAG, "in SurfaceChanged");
		if (_holder.getSurface() == null) return;
		try
		{
			_camera.stopPreview();
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		// set preview size and make any resize, rotate or
		// reformatting changes here

		// start preview with new settings

		try
		{
			_camera.setPreviewDisplay(_holder);
			_camera.startPreview();
		} catch (IOException e)
		{
			Log.d(LOG_TAG, "Error showing image preview" + e.toString());
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder surfaceHolder)
	{
		// empty. Take care of releasing the Camera preview in fragment.
	}
}
