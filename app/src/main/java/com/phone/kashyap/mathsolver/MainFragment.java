package com.phone.kashyap.mathsolver;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Kashyap on 11/29/2014.
 */
public class MainFragment extends Fragment
{
	private static final String LOG_TAG = MainFragment.class.getSimpleName();
	private static final int GET_CONTENT_INTENT = 5;
	private static final int CROP_INTENT = 2;
	private static final String CROP_ERROR = "Whoops - your device doesn't support the crop action!";
	private final ProcessImage _processImage = new ProcessImage(getActivity());

	public MainFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);

		Button buttonCamera = (Button) rootView.findViewById(R.id.button_camera);
		Button buttonExisting = (Button) rootView.findViewById(R.id.button_existing);

		buttonCamera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view)
			{
				//Using Camera
				startActivity(new Intent(getActivity(), CameraActivity.class));
			}
		});

		buttonExisting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view)
			{
				//Using Gallery
				Log.d(LOG_TAG, "existing Button Clicked");
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
				startActivityForResult(intent, GET_CONTENT_INTENT);
			}
		});

		return rootView;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == GET_CONTENT_INTENT)
		{
			Uri fileUri = data.getData();
			Intent cropIntent = _processImage.getCropIntent(fileUri);
			if(cropIntent != null)
				startActivityForResult(cropIntent, CROP_INTENT);
			else
				Toast.makeText(getActivity(), CROP_ERROR, Toast.LENGTH_SHORT).show();
		}
		if(requestCode == CROP_INTENT)
		{
			Log.d(LOG_TAG, "onActivityResult, Result Code = " + String.valueOf(resultCode));
			String finalText = _processImage.getTextFromImage(requestCode, data);
			Toast.makeText(getActivity(), finalText, Toast.LENGTH_SHORT).show();
		}
	}
}
