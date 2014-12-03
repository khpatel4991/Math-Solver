package com.phone.kashyap.mathsolver;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Kashyap on 11/29/2014.
 */
public class MainFragment extends Fragment
{
	private static final String LOG_TAG = MainFragment.class.getSimpleName();
	private static final int GET_CONTENT_INTENT = 5;
	private static final int CROP_INTENT = 2;
	private static final int CAMERA_INTENT = 1;
	public static final int MEDIA_TYPE_IMAGE = 1;
	private static final String CROP_ERROR = "Whoops - your device doesn't support the crop action!";
	private Uri _outputFileUri;
	private final ProcessImage _processImage = new ProcessImage(getActivity());

	public MainFragment(){}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		handleShareIntent(getArguments());
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		Button buttonCamera = (Button) rootView.findViewById(R.id.button_camera);
		Button buttonExisting = (Button) rootView.findViewById(R.id.button_existing);
		Button buttonCrop = (Button) rootView.findViewById(R.id.button_crop);

		buttonCamera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view)
			{
				//Using Camera
				Log.d(LOG_TAG, "camera button clicked");

				File file = getOutputMediaFile(MEDIA_TYPE_IMAGE);
				_outputFileUri = Uri.fromFile(file);
				final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, _outputFileUri);
				startActivityForResult(intent, CAMERA_INTENT);

				//startActivity(new Intent(getActivity(), CameraActivity.class));
			}
		});

		buttonExisting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view)
			{
				//Using Gallery
				Log.d(LOG_TAG, "existing Button Clicked");
				Intent intent = new Intent(Intent.ACTION_PICK);
				intent.setType("image/*");
				startActivityForResult(intent, GET_CONTENT_INTENT);
			}
		});

		buttonCrop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view)
			{
				startActivity(new Intent(getActivity(), TestActivity.class));
			}
		});

		return rootView;
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


	private void handleShareIntent(Bundle args)
	{
		if(args != null && args.containsKey("imageUri"))
		{
			Log.d(LOG_TAG, "Args not empty");
			_outputFileUri = Uri.parse(args.getString("imageUri"));
			args.remove("imageUri");
			Intent cropIntent = _processImage.getCropIntent(_outputFileUri);
			if (cropIntent != null) startActivityForResult(cropIntent, CROP_INTENT);
			else Toast.makeText(getActivity(), CROP_ERROR, Toast.LENGTH_SHORT).show();
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == CAMERA_INTENT)
		{
			Log.d(LOG_TAG, "Camera IntentResult");
			if (resultCode == Activity.RESULT_OK)
			{
				Log.d(LOG_TAG, "Picture taken, now cropping");
				Intent cropIntent = _processImage.getCropIntent(_outputFileUri);
				if (cropIntent != null) startActivityForResult(cropIntent, CROP_INTENT);
				else Toast.makeText(getActivity(), CROP_ERROR, Toast.LENGTH_SHORT).show();
			}
			else
				Log.d(LOG_TAG, "User cancelled from cam");
		}

		if (requestCode == GET_CONTENT_INTENT)
		{
			Log.d(LOG_TAG, "after crop, activity result");
			if(data != null)
			{
				try
				{
					_outputFileUri = data.getData();
					Intent cropIntent = _processImage.getCropIntent(_outputFileUri);
					if (cropIntent != null) startActivityForResult(cropIntent, CROP_INTENT);
					else Toast.makeText(getActivity(), CROP_ERROR, Toast.LENGTH_SHORT).show();
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		if (requestCode == CROP_INTENT)
		{
			Log.d(LOG_TAG, "onActivityResult, Result Code = " + String.valueOf(resultCode));
			if(data != null)
				new GetTextFromImageTask(getActivity()).execute((Bitmap) data.getExtras().getParcelable("data"));
		}
	}

}
