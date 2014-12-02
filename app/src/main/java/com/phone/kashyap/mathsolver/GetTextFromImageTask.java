package com.phone.kashyap.mathsolver;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by Kashyap on 11/30/2014.
 */
public class GetTextFromImageTask extends AsyncTask<Bitmap, Void, String>
{
	private static final String LOG_TAG = GetTextFromImageTask.class.getSimpleName();
	private Activity _activity;
	private ProcessImage _processImage;
	private ProgressDialog _progressDialog;
    private StartSolverFragment _startSolverFragment;

	public GetTextFromImageTask(Activity activity)
	{
		_activity = activity;
		_progressDialog = new ProgressDialog(_activity);
		_processImage = new ProcessImage(activity);
        _startSolverFragment = (StartSolverFragment) _activity;
	}

	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		_progressDialog.setMessage("Recognizing Text...");
		_progressDialog.setCancelable(false);
		_progressDialog.show();
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

		if(_progressDialog != null)
			_progressDialog.dismiss();
		_startSolverFragment.startSolverFragmentMethod(s);
	}
}