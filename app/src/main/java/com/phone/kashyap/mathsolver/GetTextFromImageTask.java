package com.phone.kashyap.mathsolver;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Kashyap on 11/30/2014.
 */
public class GetTextFromImageTask extends AsyncTask<Bitmap, Void, String>
{
	private static final String LOG_TAG = GetTextFromImageTask.class.getSimpleName();
	private Context _context;
	private ProcessImage _processImage;

	public GetTextFromImageTask(Context context)
	{
		_context = context;
		_processImage = new ProcessImage(_context);
	}

	protected String doInBackground(Bitmap... croppedImage)
	{

		if (croppedImage != null)
		{
			Log.d(LOG_TAG, "Data YES");
			//get the returned data
			return _processImage.getTextFromTess(croppedImage[0]);
		} else Log.d(LOG_TAG, "No data");

		return "";
	}

	@Override
	protected void onPostExecute(String s)
	{
		super.onPostExecute(s);
		Toast.makeText(_context, s, Toast.LENGTH_SHORT).show();
	}
}
