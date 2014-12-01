package com.phone.kashyap.mathsolver;

import android.app.Activity;
import android.os.AsyncTask;

import com.wolfram.alpha.WAEngine;
import com.wolfram.alpha.WAException;
import com.wolfram.alpha.WAPlainText;
import com.wolfram.alpha.WAPod;
import com.wolfram.alpha.WAQuery;
import com.wolfram.alpha.WAQueryResult;
import com.wolfram.alpha.WASubpod;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by Priya on 11/30/2014.
 */
public class SolverTask extends AsyncTask<String, Void, String> {

    private static String appid = "VYQ8QE-Y7AEURHGHE";
    private static HashSet<String> podTitleStr = new HashSet<String>();

    private Activity _activity;

    public SolverTask(MainActivity _activity) {
        onAttach(_activity);
    }

    public void onAttach(Activity activity) {
        _activity = activity;
    }

    public void onDetach() {
        _activity = null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (_activity == null) {
        }
        else {
            ((MainActivity) _activity)._solverFrag.showProgressBarWhileRetrieving();
        }
    }

    @Override
    protected String doInBackground(String... params) {
        StringBuilder queryResultStr = new StringBuilder("");
        String[] podTitleArray = {"Input", "Exact result","Results", "Result", "Solutions", "Decimal approximation"};

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
                for (WAPod pod : queryResult.getPods()) {
                    if (!pod.isError()) {
                        if(podTitleStr.contains(pod.getTitle())){
                            queryResultStr.append(pod.getTitle()+"\n------------\n");

                            for (WASubpod subpod : pod.getSubpods()) {
                                if(!subpod.getTitle().equals("")){
                                    queryResultStr.append("\n"+subpod.getTitle()+"\n------------\n");
                                }
                                for (Object element : subpod.getContents()) {
                                    if (element instanceof WAPlainText) {
                                        if(subpod.getTitle().equals("Possible intermediate steps")){
                                            StringBuilder temp = new StringBuilder(((WAPlainText) element).getText());

                                            String[] stepsInQuery = (temp.toString()).split("\n");

                                            for(int i =0; i<stepsInQuery.length; i++){
                                                int j = stepsInQuery[i].indexOf("|");
                                                int k = stepsInQuery[i].indexOf("Answer");
                                                if(stepsInQuery[i].contains(":"))
                                                {
                                                    stepsInQuery[i] = "\n"+stepsInQuery[i];
                                                }

                                                if(j == -1 && !stepsInQuery[i].equals("")){
                                                    queryResultStr.append(stepsInQuery[i]+"\n");
                                                }else if(k != -1){
                                                    String remPipe = stepsInQuery[i].replace("|", "");
                                                    queryResultStr.append(remPipe);
                                                    i++;
                                                    remPipe = stepsInQuery[i].replace("|", "");
                                                    queryResultStr.append(remPipe);
                                                }
                                            }
                                            queryResultStr.append("\n");
                                        }else{
                                            if(pod.getTitle().equals("Decimal approximation")){
                                                String resFloatStr = ((WAPlainText) element).getText();
                                                resFloatStr = (resFloatStr.replace("...","")).trim();
                                                float resFloat = Float.parseFloat(resFloatStr);
                                                queryResultStr.append(resFloat+"\n");
                                            }else{
                                                queryResultStr.append(((WAPlainText) element).getText()+"\n");
                                            }
                                        }
                                    }
                                }
                            }
                            queryResultStr.append("\n");
                        }
                    }
                }
            }
        } catch (WAException e) {
            e.printStackTrace();
        }

        return queryResultStr.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        if(_activity == null) {
        }else{
            ((MainActivity)_activity)._solverFrag.hideProgressBarAfterRetrieving(result);
        }
    }
}
