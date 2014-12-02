package com.phone.kashyap.mathsolver;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Kashyap on 11/30/2014.
 */
public class HandleShareIntent extends Activity
{
	private static final String LOG_TAG = HandleShareIntent.class.getSimpleName();

	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.d(LOG_TAG, "onCreate");
        setContentView(R.layout.handle_intent_main);
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
		Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
		if (imageUri != null)
		{
			Intent mainActivityIntent = new Intent(this, MainActivity.class);
			mainActivityIntent.putExtra("fromShareIntent", true);
			mainActivityIntent.putExtra("imageUri", imageUri.toString());
			startActivity(mainActivityIntent);
			finish();
		}
	}
}
