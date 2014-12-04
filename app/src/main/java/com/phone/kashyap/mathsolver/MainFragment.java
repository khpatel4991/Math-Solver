package com.phone.kashyap.mathsolver;

import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import eu.janmuller.android.simplecropimage.CropImage;

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
	public static final String MY_DIRECTORY = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/MathSolver/";
	//"file:///storage/emulated/0/Pictures/MathSolver/"
	private File _imageFile;
	private Uri _imageFileUri;
	public MainFragment(){}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		handleShareIntent(getArguments());

		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		Button buttonCamera = (Button) rootView.findViewById(R.id.button_camera);
		Button buttonExisting = (Button) rootView.findViewById(R.id.button_existing);
		buttonCamera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view)
			{
				//Using Camera
				Log.d(LOG_TAG, "Camera Button Clicked");
				takePicture();
			}
		});

		buttonExisting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view)
			{
				//Using Gallery
				Log.d(LOG_TAG, "Gallery Button Clicked");
				openGallery();
			}
		});
		_imageFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
		_imageFileUri = Uri.fromFile(_imageFile);

		return rootView;
	}

	private void takePicture()
	{
		try
		{
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, _imageFileUri);
			intent.putExtra("return-data", true);
			startActivityForResult(intent, CAMERA_INTENT);
		} catch (ActivityNotFoundException e)
		{
			Log.d(LOG_TAG, "No Camera App installed in your phone!");
		}
	}

	private void openGallery()
	{
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		startActivityForResult(intent, GET_CONTENT_INTENT);
	}


	/* Create a File for saving an image or video */
	private static File getOutputMediaFile(int type)
	{
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		File mediaStorageDir = new File(MY_DIRECTORY);
		if (!mediaStorageDir.exists())
		{
			if (!mediaStorageDir.mkdirs())
			{
				Log.d("MathSolver", "Failed to create directory");
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
			Log.d(LOG_TAG, "Image Shared with MathSolver, now cropping");
			copyAndCrop(Uri.parse(args.getString("imageUri")));
			args.remove("imageUri");
		}
	}

	private void startCropImage()
	{
		Intent intent = new Intent(getActivity(), CropImage.class);
		intent.putExtra(CropImage.IMAGE_PATH, _imageFile.getPath());
		intent.putExtra(CropImage.SCALE, true);

		intent.putExtra(CropImage.ASPECT_X, 3);
		intent.putExtra(CropImage.ASPECT_Y, 1);

		startActivityForResult(intent, CROP_INTENT);
	}

	private void copyAndCrop(Uri uri)
	{
		if (!uri.getPath().startsWith(MY_DIRECTORY))
		{
			Log.d(LOG_TAG, "Not from MathSolver Directory");
			_imageFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
			_imageFileUri = uri;
		} else
		{
			Log.d(LOG_TAG, "From MathSolver Directory, Cropped Image will be over-written");
			_imageFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
			_imageFileUri = uri;
		}
		try
		{
			InputStream inputStream = getActivity().getContentResolver().openInputStream(_imageFileUri);
			FileOutputStream fileOutputStream = new FileOutputStream(_imageFile);
			copyStream(inputStream, fileOutputStream);
			fileOutputStream.close();
			inputStream.close();

			startCropImage();
		} catch (IOException e)
		{
			Log.e(LOG_TAG, e.getMessage());
			Log.d(LOG_TAG, "Can't create temp file, so can't crop");
		}
	}

	private static void copyStream(InputStream input, OutputStream output) throws IOException
	{

		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = input.read(buffer)) != -1)
		{
			output.write(buffer, 0, bytesRead);
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == CAMERA_INTENT)
		{
			if (resultCode == Activity.RESULT_OK)
			{
				Log.d(LOG_TAG, "Picture taken, now cropping");
				startCropImage();
			}
			else
				Log.d(LOG_TAG, "User cancelled from cam");
		}

		if (requestCode == GET_CONTENT_INTENT)
		{
			if(resultCode == Activity.RESULT_OK)
			{
				Log.d(LOG_TAG, "Picture Got from Gallery, now cropping");
				copyAndCrop(data.getData());
			} else Log.d(LOG_TAG, "User cancelled from gallery");
		}

		if (requestCode == CROP_INTENT)
		{
			if (data != null)
			{
				Log.d(LOG_TAG, "Image Cropped, Now Processing to get Equation");
				String path = data.getStringExtra(CropImage.IMAGE_PATH);
				if (path == null) return;
				MediaScannerConnection.scanFile(getActivity(), new String[]{_imageFile.getAbsolutePath()}, null, new MediaScannerConnectionClient()
				{
					@Override
					public void onMediaScannerConnected() {}

					@Override
					public void onScanCompleted(String s, Uri uri) {}
				});
				new GetTextFromImageTask(getActivity()).execute(BitmapFactory.decodeFile(_imageFile.getPath()));
			}
		}
	}
}
