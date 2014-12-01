package com.phone.kashyap.mathsolver;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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

	private static final String CROP_ERROR = "Whoops - your device doesn't support the crop action!";

	private static final int CROP_INTENT = 2;

	private final ProcessImage _processImage = new ProcessImage(this);

    SolverFragment _solverFrag;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.d(LOG_TAG, "onCreate");
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

		/*// Get intent, action and MIME type
		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();

		if (Intent.ACTION_SEND.equals(action) && type != null)
		{
			if (type.startsWith("image/"))
			{
				Log.d(LOG_TAG, "intent present");
				handleSendImage(intent); // Handle single image being sent
			}
		}
*/
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null)
		{
			getFragmentManager().beginTransaction().add(R.id.container, new MainFragment()).commit();
		}
	}


    public void startSolverFragmentMethod(String finalText)
    {
        _solverFrag = new SolverFragment();
        Bundle solverBundle = new Bundle();
        solverBundle.putString("equation", finalText);
        _solverFrag.setArguments(solverBundle);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, _solverFrag);
        fragmentTransaction.commit();
    }

	/*void handleSendImage(Intent intent)
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
	}*/

	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == CROP_INTENT)
		{
			Log.d(LOG_TAG, "onActivityResult, Result Code = " + String.valueOf(resultCode));
			if(data != null)
			{
				String finalText = _processImage.getTextFromImage(requestCode, data);

				Toast.makeText(this, finalText, Toast.LENGTH_SHORT).show();
			}
		}


		Log.d(LOG_TAG, "onActivityResult");

	}
*/
}
