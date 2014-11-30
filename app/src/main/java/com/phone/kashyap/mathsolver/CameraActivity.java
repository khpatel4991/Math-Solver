package com.phone.kashyap.mathsolver;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;


/**
 * Created by Kashyap on 11/18/2014.
 */
public class CameraActivity extends Activity
{

	private static final String LOG_TAG = CameraActivity.class.getSimpleName();

	public static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/MathSolver/";

	private static final String lang = "eng";

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		if (savedInstanceState == null)
		{
			getFragmentManager().beginTransaction().add(R.id.container, new CameraFragment()).commit();
		}
	}
}
