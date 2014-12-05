package com.phone.kashyap.mathsolver;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class MakeTxt extends AsyncTask<String, Void, Void>
{
	private Activity _activity;

	public MakeTxt(Activity _activity) {
		onAttach(_activity);
	}

	public void onAttach(Activity activity) {
		_activity = activity;
	}

	public void onDetach() {
		_activity = null;
	}

	@Override
	protected Void doInBackground(String... params)
	{
		PrintWriter fOutput = null;
		FileWriter file = null;
		File fileInstance = MainFragment.getOutputTextFile(MainFragment.MEDIA_TYPE_TEXT);

		try
		{
			file = new FileWriter(fileInstance, true);
			fOutput = new PrintWriter(file);
			fOutput.println(params[0]);

		} catch (Exception e) {
			Log.e("Error: ", e.getMessage());
		} finally {
			if (fOutput != null) {
				fOutput.close();
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void aVoid) {
		if (_activity != null)
		{

			((MainActivity) _activity)._solverFrag.afterSavingFile();
		}
	}
}