package com.phone.kashyap.mathsolver;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Kashyap on 11/30/2014.
 */
public class HandleShareIntent extends Activity
{
	private static final String LOG_TAG = HandleShareIntent.class.getSimpleName();

	private static final String CROP_ERROR = "Whoops - your device doesn't support the crop action!";

	private static final int CROP_INTENT = 2;

	private final ProcessImage _processImage = new ProcessImage(this);

	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.d(LOG_TAG, "onCreate");
		// Get intent, action and MIME type
		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();

		if (Intent.ACTION_SEND.equals(action) && type != null)
		{
			if (type.startsWith("image/"))
			{
				Log.d(LOG_TAG, "calling handleSendIntent");
				handleSendImage(intent); // Handle single image being sent
			}
		}
	}

	void handleSendImage(Intent intent)
	{
		Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
		if (imageUri != null)
		{
			Intent cropIntent = _processImage.getCropIntent(imageUri);
			if (cropIntent != null) startActivityForResult(cropIntent, CROP_INTENT);
			else Toast.makeText(this, CROP_ERROR, Toast.LENGTH_SHORT).show();

			Log.d(LOG_TAG, imageUri.getPath());
			// Update UI to reflect image being shared
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		Log.d(LOG_TAG, "oonActivityResult");

		if (requestCode == CROP_INTENT)
		{
			Log.d(LOG_TAG, "onActivityResult, Result Code = " + String.valueOf(resultCode));
			if(data != null)
			{
				new GetTextFromImageTask(this, null).execute((Bitmap) data.getExtras().getParcelable("data"));

				Intent m = new Intent(this, MainActivity.class);
				//m.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

				finish();
				Log.d(LOG_TAG, "created intent");
				startActivity(m);
			}
		}


		Log.d(LOG_TAG, "onAcivityResult");

	}
}
