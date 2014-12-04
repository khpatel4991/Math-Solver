package com.phone.kashyap.mathsolver;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

/**
 * Created by Kashyap on 11/29/2014.
 */
public class ProcessImage
{
	private static final String LOG_TAG = ProcessImage.class.getSimpleName();
	private Context _context;
	private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/MathSolver/";

	private static final String lang = "eng";
	private static final int CROP_INTENT = 2;
	private static final int ACCEPTABLE_CONFIDENCE_LEVEL = 68;
	private Bitmap _croppedImage;

	public ProcessImage(Context context)
	{
		_context = context;
	}


	public Intent getCropIntent(Uri outputFileUri)
	{
		Log.d(LOG_TAG, "getCropIntent");
		try
		{

			Intent cropIntent = new Intent("com.android.camera.action.CROP");
			cropIntent.setDataAndType(outputFileUri, "image/*");
			//set crop properties
			cropIntent.putExtra("crop", "false");
			cropIntent.putExtra("aspectX", 0);
			cropIntent.putExtra("aspectY", 0);
			cropIntent.putExtra("return-data", true);
			return cropIntent;
		}
		//respond to users whose devices do not support the crop action
		catch (ActivityNotFoundException anfe)
		{
			return null;
		}
	}

	public String getTextFromImage(int requestCode ,Intent data)
	{
		if (requestCode == CROP_INTENT)
		{
			if (data != null)
			{
				Log.d(LOG_TAG, "Cropped Image Received");
				//get the returned data
				Bundle extras = data.getExtras();
				//get the cropped bitmap
				_croppedImage = extras.getParcelable("data");
				return getTextFromTess(_croppedImage);
			}
			else Log.d(LOG_TAG, "Cropped Picture Not received");
		}
		return "";
	}


	public String getTextFromTess(Bitmap croppedImage)
	{
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;

		Log.v(LOG_TAG, "Before baseApi");
		TessBaseAPI baseApi = new TessBaseAPI();
		baseApi.setDebug(true);
		baseApi.init(DATA_PATH, lang);
		baseApi.setImage(croppedImage);
		String recognizedText = baseApi.getUTF8Text();
		Log.d("Mean Confidence", String.valueOf(baseApi.meanConfidence()));
		int meanConfidence = baseApi.meanConfidence();
		baseApi.end();
		recognizedText = recognizedText.trim();
		return meanConfidence > ACCEPTABLE_CONFIDENCE_LEVEL ? recognizedText : "";
	}
}
