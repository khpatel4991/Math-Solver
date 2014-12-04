package com.phone.kashyap.mathsolver;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class MainActivity extends Activity implements StartSolverFragment
{

	private static final String LOG_TAG = MainActivity.class.getSimpleName();

	public static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/MathSolver/";

	private static final String lang = "eng";

	public SolverFragment _solverFrag;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.d(LOG_TAG, "onCreate");
		super.onCreate(savedInstanceState);
		copyAssetData();

		setContentView(R.layout.activity_main);
		MainFragment mainFragment = new MainFragment();
		if (isComingFromShareIntentHandler())
		{
			Log.d(LOG_TAG, "Setting up bundle with uri string = " + this.getIntent().getExtras().getString("imageUri"));
			Bundle args = new Bundle();
			args.putString("imageUri", this.getIntent().getExtras().getString("imageUri"));
			mainFragment.setArguments(args);
			//getFragmentManager().beginTransaction().add(R.id.container, mainFragment).commit();
		}
		if(savedInstanceState == null)
			getFragmentManager().beginTransaction().add(R.id.container, mainFragment).commit();
	}

	private void copyAssetData()
	{
		String[] paths = new String[]{DATA_PATH, DATA_PATH + "tessdata/"};

		for (String path : paths)
		{
			File dir = new File(path);
			if (!dir.exists())
			{
				if (!dir.mkdirs())
				{
					Log.v(LOG_TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
					return;
				} else
				{
					Log.v(LOG_TAG, "Created directory " + path + " on sdcard");
				}
			}

		}

		// lang.traineddata file with the app (in assets folder)
		// You can get them at:
		// http://code.google.com/p/tesseract-ocr/downloads/list
		// This area needs work and optimization
		if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists())
		{
			try
			{

				AssetManager assetManager = getAssets();
				InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
				OutputStream out = new FileOutputStream(DATA_PATH + "tessdata/" + lang + ".traineddata");

				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0)
				{
					out.write(buf, 0, len);
				}
				in.close();
				//gin.close();
				out.close();

				Log.d(LOG_TAG, "Copied " + lang + " traineddata");
			} catch (IOException e)
			{
				Log.e(LOG_TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
			}
		}
	}


	private boolean isComingFromShareIntentHandler()
	{
		Intent shareIntent = this.getIntent();
		if(shareIntent != null)
			if(shareIntent.getExtras() != null)
					if(shareIntent.getExtras().containsKey("fromShareIntent"))
						return true;
		return false;
	}

    public void startSolverFragmentMethod(String finalText)
    {
        _solverFrag = new SolverFragment();
        Bundle solverBundle = new Bundle();
        solverBundle.putString("equation", finalText);
        _solverFrag.setArguments(solverBundle);
		getFragmentManager().beginTransaction().setCustomAnimations(
				R.animator.card_flip_right_in, R.animator.card_flip_right_out,
				R.animator.card_flip_left_in, R.animator.card_flip_left_out).replace(R.id.container, _solverFrag).addToBackStack(null).commit();
    }
}
