package com.phone.kashyap.mathsolver;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static android.view.View.OnClickListener;

/**
 * Created by Kashyap on 11/30/2014.
 */
public class SolverFragment extends Fragment
{
	private String _expression;
    private SolverTask _solverTask;
    private MainActivity _activity;
    private EditText _equation;
    private TextView _resultView;
    private Button _calcButton;

	public SolverFragment() {}

    @Override
    public void onAttach(Activity activity) {
       Log.i("PP-DF", "on Attach");
        super.onAttach(activity);
        _activity = (MainActivity)activity;
        if (_solverTask != null) {
            _solverTask.onAttach(activity);
        }
    }

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_solver,container, false);
		return view;
	}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);

        _equation = (EditText) getActivity().findViewById(R.id.equEditText);
        _resultView = (TextView) getActivity().findViewById(R.id.resultView);

        Bundle solverBundle = this.getArguments();
        _expression = solverBundle.getString("equation", "0");

        _equation.setText(_expression);

        _calcButton = (Button) getActivity().findViewById(R.id.button2);
        _calcButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String equ =_equation.getText().toString();
                boolean flag = checkInternet();
                if(flag) {
                    if (equ != null && equ.length() > 0) {
                        _solverTask = new SolverTask(_activity);
                        _equation.setEnabled(false);
                        _calcButton.setEnabled(false);
                        _solverTask.execute(equ);
                    } else {
                        _resultView.setText("No equation found from the image.");
                    }
                }
            }
        });

        if(_solverTask != null && _solverTask.getStatus() == AsyncTask.Status.RUNNING){
            _equation.setEnabled(false);
            _calcButton.setEnabled(false);
        }
    }

    public boolean checkInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager) _activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
/*            Toast.makeText(_activity, "Active Connected Network Type : " + activeInfo.getTypeName(),
                    Toast.LENGTH_LONG).show();
            Log.d("Internet", "Active Connected Network Type : " + activeInfo.getTypeName());*/
            return true;
        }else{
            Toast.makeText(_activity, "Network error. Please check your connection.",
                    Toast.LENGTH_LONG).show();
            Log.d("Internet", "Network error. Please check your connection.");
            return false;
        }
    }



	public void showResult(String result)
	{
		if (_solverTask != null)
		{
			_resultView.setText(result);
			_calcButton.setEnabled(false);
			_equation.setEnabled(true);
		}
	}

    @Override
    public void onDetach() {
        super.onDetach();
        if (_solverTask != null) {
            _solverTask.onDetach();
        }
    }

}
