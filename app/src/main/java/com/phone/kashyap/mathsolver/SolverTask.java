package com.phone.kashyap.mathsolver;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.wolfram.alpha.WAEngine;
import com.wolfram.alpha.WAException;
import com.wolfram.alpha.WAPlainText;
import com.wolfram.alpha.WAPod;
import com.wolfram.alpha.WAQuery;
import com.wolfram.alpha.WAQueryResult;
import com.wolfram.alpha.WASubpod;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Priya on 11/30/2014.
 */
public class SolverTask extends AsyncTask<String, HashMap<String, String>, String>
{
	private static final String LOG_TAG = SolverTask.class.getSimpleName();
	private static String appid = "VYQ8QE-Y7AEURHGHE";
	private static HashSet<String> podTitleStr = new HashSet<String>();

	private Activity _activity;

	private ProgressDialog _progressDialog;

	public SolverTask(MainActivity _activity)
	{
		onAttach(_activity);
	}

	public void onAttach(Activity activity)
	{
		_activity = activity;
	}

	public void onDetach()
	{
		_activity = null;
	}

	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		if (_activity != null)
		{
			_progressDialog = new ProgressDialog(_activity);
			_progressDialog.setMessage("Working some Magic...");
			_progressDialog.setCancelable(false);
			_progressDialog.show();
		}
	}

	@Override
	protected String doInBackground(String... params)
	{
		StringBuilder queryResultStr = new StringBuilder("");
		String[] podTitleArray = {"Input", "Exact result", "Results", "Result", "Solutions", "Decimal approximation"};

		podTitleStr.addAll(Arrays.asList(podTitleArray));

		String input = params[0];
		WAEngine engine = new WAEngine();

		// These properties will be set in all the WAQuery objects created from this WAEngine.
		engine.setAppID(appid);
		engine.addFormat("plaintext");
		engine.addPodState("Solution__Step-by-step solution");
		engine.addPodState("Result__Step-by-step solution");
		engine.addPodState("Solution__Approximate form"); // For single variable equation it gives decimal result

		// Create the query.
		WAQuery query = engine.createQuery();

		// Set properties of the query.
		query.setInput(input);

		try {
			WAQueryResult queryResult = engine.performQuery(query);

			if (queryResult.isError()) {
				queryResultStr.append("Query error\n\terror code: " + queryResult.getErrorCode()+"\n\terror message: " + queryResult.getErrorMessage());
			} else if (!queryResult.isSuccess()) {
				queryResultStr.append("Query was not understood; no results available.");
			} else {
				final String title = "TITLE";
				final String subTitle = "SUBTITLE";
				final String para = "PARA";
				HashMap<String, String> resultMap;
				for (WAPod pod : queryResult.getPods()) {
					resultMap = new HashMap<>();
					if (!pod.isError()) {
						if(podTitleStr.contains(pod.getTitle())){
							queryResultStr.append(pod.getTitle()+"\n------------\n");
							resultMap.put(title, pod.getTitle());

							for (WASubpod subpod : pod.getSubpods()) {
								if(!subpod.getTitle().equals("")){
									queryResultStr.append("\n"+subpod.getTitle()+"\n------------\n");
									resultMap.put(subTitle, subpod.getTitle());
								}
								for (Object element : subpod.getContents()) {
									if (element instanceof WAPlainText) {
										if(subpod.getTitle().equals("Possible intermediate steps")){
											StringBuilder temp = new StringBuilder(((WAPlainText) element).getText());

											StringBuilder paraMatter = new StringBuilder("");

											String[] stepsInQuery = (temp.toString()).split("\n");

											for(int i =0; i<stepsInQuery.length; i++){
												int j = stepsInQuery[i].indexOf("|");
												int k = stepsInQuery[i].indexOf("Answer");
												if(stepsInQuery[i].contains(":"))
												{
													stepsInQuery[i] = "\n"+stepsInQuery[i];
												}

												if(j == -1 && !stepsInQuery[i].equals("")){
													paraMatter.append(stepsInQuery[i]+"\n");
												}else if(k != -1){
													paraMatter.append("\n");
													for(int l=i; l<stepsInQuery.length; l++) {
														String remPipe = stepsInQuery[l].replace("|", "");
														paraMatter.append(remPipe.trim()+"\n");
													}
												}
											}
											resultMap.put(para, paraMatter.toString());
											queryResultStr.append(paraMatter);
											queryResultStr.append("\n");
										}else{
											if(pod.getTitle().equals("Decimal approximation")){
												String resFloatStr = ((WAPlainText) element).getText();
												resFloatStr = (resFloatStr.replace("...","")).trim();
												float resFloat = Float.parseFloat(resFloatStr);
												queryResultStr.append(resFloat+"\n");
												resultMap.put(para, Float.toString(resFloat));
											}else{
												queryResultStr.append(((WAPlainText) element).getText());
												resultMap.put(para, ((WAPlainText) element).getText());
											}
										}
									}
								}
							}
							publishProgress(resultMap);
							queryResultStr.append("\n");
						}
					}
				}
			}
		} catch (WAException e) {
			Log.d(LOG_TAG, e.getMessage());
		}

		return queryResultStr.toString();
	}

	@Override
	protected void onProgressUpdate(HashMap<String, String>... values)
	{
		super.onProgressUpdate(values);

		if (_activity != null)
		{
			if (values != null && values.length > 0)
			{
				Object[] objects = values[0].values().toArray();
				for(int i =0; i < objects.length;i++)
				{
					Log.d("QueryMap", objects[i].toString());
				}
				((MainActivity) _activity)._solverFrag.populatingTextView(values[0]);
			}
		}
	}

	@Override
	protected void onPostExecute(String result)
	{
		if (_activity != null)
		{
			if (_progressDialog != null) _progressDialog.dismiss();

			((MainActivity) _activity)._solverFrag.afterReturnFromAsyncTask();
		}
	}
}
