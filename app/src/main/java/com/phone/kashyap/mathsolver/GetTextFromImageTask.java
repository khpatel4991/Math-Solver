package com.phone.kashyap.mathsolver;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * Created by Kashyap on 11/30/2014.
 */
public class GetTextFromImageTask extends AsyncTask<Bitmap, Void, String>
{
	private static final String LOG_TAG = GetTextFromImageTask.class.getSimpleName();
	private Context _context;
	private ProcessImage _processImage;
	private ProgressBar _progressBar;

	public GetTextFromImageTask(Context context, ProgressBar progressBar)
	{
		_context = context;
		_progressBar = progressBar;
		_processImage = new ProcessImage(_context);
	}

	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		if(_progressBar != null)
			_progressBar.setVisibility(View.VISIBLE);
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
		if(_progressBar != null)
			_progressBar.setVisibility(View.INVISIBLE);
		Toast.makeText(_context, s, Toast.LENGTH_SHORT).show();
	}
}
