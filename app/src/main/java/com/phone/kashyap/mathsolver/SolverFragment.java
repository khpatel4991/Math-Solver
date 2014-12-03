package com.phone.kashyap.mathsolver;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import static android.view.View.OnClickListener;

/**
 * Created by Kashyap on 11/30/2014.
 */
public class SolverFragment extends Fragment
{
	private static final String LOG_TAG = SolverFragment.class.getSimpleName();
	private String _expression;
	private SolverTask _solverTask;
	private MainActivity _activity;
	private EditText _equation;
	private TableLayout _tableResultLayout;
	private String oldTitle = "";
	private Button _calcButton;

	public SolverFragment() {}

	@Override
	public void onAttach(Activity activity)
	{
		Log.i(LOG_TAG, "on Attach");
		super.onAttach(activity);
		_activity = (MainActivity) activity;
		if (_solverTask != null)
		{
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
		View view = inflater.inflate(R.layout.fragment_solver, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		setRetainInstance(true);

		_equation = (EditText) getActivity().findViewById(R.id.equEditText);

		_tableResultLayout = (TableLayout) getActivity().findViewById(R.id.table_result_layout);

		Bundle solverBundle = this.getArguments();
		_expression = solverBundle.getString("equation", "0");

		_equation.setText(_expression);

		_equation.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i2, int i3)
			{
				_calcButton.setEnabled(true);
			}

			@Override
			public void afterTextChanged(Editable editable) {}
		});

		_calcButton = (Button) getActivity().findViewById(R.id.button2);
		_calcButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String equ = _equation.getText().toString();
				boolean flag = checkInternet();
				if (flag)
				{
					if (equ != null && equ.length() > 0)
					{
						_tableResultLayout.removeAllViews();
						_solverTask = new SolverTask(_activity);
						_equation.setEnabled(false);
						_calcButton.setEnabled(false);
						_solverTask.execute(equ);
					}
					else addTitleToTable("No Equation Found !");
				}
			}
		});


		if (_solverTask != null && _solverTask.getStatus() == AsyncTask.Status.RUNNING)
		{
			_equation.setEnabled(false);
			_calcButton.setEnabled(false);
		}
	}

	public boolean checkInternet()
	{
		ConnectivityManager connectivityManager = (ConnectivityManager) _activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();
		if (activeInfo != null && activeInfo.isConnected())
		{
/*            Toast.makeText(_activity, "Active Connected Network Type : " + activeInfo.getTypeName(),
					Toast.LENGTH_LONG).show();
            Log.d("Internet", "Active Connected Network Type : " + activeInfo.getTypeName());*/
			return true;
		} else
		{
			Toast.makeText(_activity, "Network error. Please check your connection.", Toast.LENGTH_LONG).show();
			Log.d("Internet", "Network error. Please check your connection.");
			return false;
		}
	}

	public void populatingTextView(HashMap<String, String> values)
	{

		final String title = "TITLE";
		final String subTitle = "SUBTITLE";
		final String para = "PARA";
		_equation.setEnabled(true);
		if (values != null)
		{
			if (oldTitle.equals(values.get(title)))
			{
				if (values.containsKey(subTitle))
				{
					Log.d(LOG_TAG, values.get(subTitle));
					addSubTitleToTable(values.get(subTitle));
				}
				addParaToTable(values.get(para));
			}
			else
			{
				addTitleToTable(values.get(title));
				if (values.containsKey(subTitle))
				{
					Log.d(LOG_TAG, values.get(subTitle));
					addSubTitleToTable(values.get(subTitle));
				}
				addParaToTable(values.get(para));
			}
		}
	}

	public void addTitleToTable(String title)
	{
		if (!title.isEmpty())
		{
			TableRow tr = new TableRow(getActivity());
			TextView titleTextView = new TextView(getActivity());
			titleTextView.setText(title);
			titleTextView.setTextColor(Color.RED);
			titleTextView.setTextSize(20);
			titleTextView.setPadding(5, 5, 5, 5);
			tr.addView(titleTextView, new TableRow.LayoutParams());
			_tableResultLayout.addView(tr, new TableLayout.LayoutParams());
		}
	}

	public void addSubTitleToTable(String subTitle)
	{
		if (!subTitle.isEmpty())
		{
			TableRow tr = new TableRow(getActivity());
			TextView subTitleTextView = new TextView(getActivity());
			subTitleTextView.setText(subTitle);
			subTitleTextView.setTextColor(Color.GREEN);
			subTitleTextView.setPadding(5, 5, 5, 5);
			tr.addView(subTitleTextView, new TableRow.LayoutParams());
			_tableResultLayout.addView(tr, new TableLayout.LayoutParams());
		}
	}

	public void addParaToTable(String para)
	{
		if (!para.isEmpty())

		{
			TableRow tr = new TableRow(getActivity());
			TextView paraTextView = new TextView(getActivity());
			paraTextView.setText(para);
			paraTextView.setTextColor(Color.BLUE);
			paraTextView.setPadding(5, 5, 5, 5);
			tr.addView(paraTextView, new TableRow.LayoutParams());
			_tableResultLayout.addView(tr, new TableLayout.LayoutParams());
		}
	}

	public void afterReturnFromAsyncTask()
	{
		_equation.setEnabled(true);
	}

	@Override
	public void onDetach()
	{
		super.onDetach();
		if (_solverTask != null)
		{
			_solverTask.onDetach();
		}
	}
}
