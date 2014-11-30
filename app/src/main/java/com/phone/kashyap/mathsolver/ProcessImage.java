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
	public static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/MathSolver/";
	private static final String lang = "eng";
	private static final int CROP_INTENT = 2;
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
			//take care of exceptions
			//call the standard crop action intent (the user device may not support it)
			Intent cropIntent = new Intent("com.android.camera.action.CROP");
			cropIntent.setDataAndType(outputFileUri, "image/*");
			//set crop properties
			cropIntent.putExtra("crop", "true");
			/*cropIntent.putExtra("outputX", 256);
			cropIntent.putExtra("outputY", 256);
			*/cropIntent.putExtra("return-data", true);
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
				Log.d(LOG_TAG, "Data YES");
				//get the returned data
				Bundle extras = data.getExtras();
				//get the cropped bitmap
				_croppedImage = extras.getParcelable("data");
				Log.d(LOG_TAG, "Image Cropped now processing");
				return getTextFromTess(_croppedImage);
			}
			else Log.d(LOG_TAG, "No data");
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
		Log.i(LOG_TAG, baseApi.getInitLanguagesAsString());
		String recognizedText = baseApi.getUTF8Text();
		baseApi.end();

		// You now have the text in recognizedText var, you can do anything with it.
		// We will display a stripped out trimmed alpha-numeric version of it (if lang is eng)
		// so that garbage doesn't make it to the display.

		Log.d(LOG_TAG, "OCRed TEXT: " + recognizedText);
		recognizedText = recognizedText.trim();
		Log.d(LOG_TAG, "After Trim: " + recognizedText);
		return recognizedText;
	}
}
