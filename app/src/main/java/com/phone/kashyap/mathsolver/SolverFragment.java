package com.phone.kashyap.mathsolver;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
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

import com.phone.kashyap.validator.Parser;
import com.phone.kashyap.validator.ParserException;
import com.phone.kashyap.validator.Token;
import com.phone.kashyap.validator.Tokenizer;

import java.util.HashMap;
import java.util.LinkedList;

import static android.view.View.OnClickListener;

public class SolverFragment extends Fragment
{
	private static final String LOG_TAG = SolverFragment.class.getSimpleName();
	private String _expression;
	private SolverTask _solverTask;
	private MainActivity _activity;
	private EditText _equation;
	private TableLayout _tableResultLayout;
	private String oldTitle = "";
	private Button _calcButton, _downloadButton;
	private String _txtFileString;
	private MakeTxt _makeTxtTask;

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
		if (_makeTxtTask != null)
		{
			_makeTxtTask.onAttach(activity);
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
		Log.d(LOG_TAG, "Old: " + _expression);
		_expression = verifyExpression(_expression);
		Log.d(LOG_TAG, "New: " + _expression);
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
			public void onClick(View v)
			{
				String equ = _equation.getText().toString();
				boolean flag = checkInternet();
				if (flag)
				{
					if (equ != null && equ.length() > 0)
					{
						_tableResultLayout.removeAllViews();
						_txtFileString = null;
						_solverTask = new SolverTask(_activity);
						_equation.setEnabled(false);
						_calcButton.setEnabled(false);
						String errorMsg;
						_txtFileString = null;
						errorMsg = validateExpression(equ);
						_downloadButton.setEnabled(false);
						if (errorMsg == null)
						{
							_solverTask.execute(equ);
						} else
						{
							addTitleToTable(errorMsg);
							_equation.setEnabled(true);
						}
					} else
					{
						addTitleToTable("No Equation Found !");
						_equation.setEnabled(true);
						_downloadButton.setEnabled(false);
					}
				}
			}
		});

		_downloadButton = (Button) getActivity().findViewById(R.id.button3);
		_downloadButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				if (_txtFileString != null && _txtFileString.length() > 0)
				{
					_makeTxtTask = new MakeTxt(_activity);
					_makeTxtTask.execute(_txtFileString);
					_downloadButton.setEnabled(false);
				} else
				{
					Toast.makeText(getActivity(), "No Solution Available to download!", Toast.LENGTH_LONG).show();
				}
			}
		});

		if (_solverTask != null && _solverTask.getStatus() == AsyncTask.Status.RUNNING)
		{
			_equation.setEnabled(false);
			_calcButton.setEnabled(false);
			_downloadButton.setEnabled(false);
		}
		if (_makeTxtTask != null && _makeTxtTask.getStatus() == AsyncTask.Status.RUNNING)
		{
			_downloadButton.setEnabled(false);
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

	public String verifyExpression(String expressionString)
	{
		expressionString = expressionString.replaceAll("A", "^");
		expressionString = expressionString.replaceAll("D", "p");
		expressionString = expressionString.replaceAll("B", "8");
		expressionString = expressionString.replaceAll("@", "0");
		expressionString = expressionString.replaceAll("\"", "^");
		expressionString = expressionString.replaceAll("Q", "9");
		expressionString = expressionString.replaceAll("\u20ac", "6");
		expressionString = expressionString.replaceAll("Z", "2");
		expressionString = expressionString.replaceAll("10g", "log");
		expressionString = expressionString.replaceAll("5in", "sin");
		return expressionString;
	}

	public String validateExpression(String equation)
	{
		Tokenizer tokenizer = new Tokenizer();
		tokenizer.add("sin|cos|exp|ln|sqrt|pow|acos|asin|atan|sec|cosec|cot|sinh|cosh|tanh|asinh|acosh|atanh|log|log10|log2", 1);
		tokenizer.add("\\(", 2);
		tokenizer.add("\\)", 3);
		tokenizer.add("\\+|-", 4);
		tokenizer.add("\\*|/", 5);
		//tokenizer.add("[0-9]+|pi", 6);
		tokenizer.add("[a-z][a-z0-9]*", 7);
		tokenizer.add("(?:\\d+\\.?|\\.\\d)\\d*(?:[Ee][-+]?\\d+)?", 6);
		tokenizer.add("\\=", 9);
		tokenizer.add("\\^", 8);

		try
		{
			tokenizer.tokenize(equation);
			Parser parser = new Parser();
			LinkedList<Token> token1;
			token1 = tokenizer.getTokens();
			if (tokenizer.flag)
			{
				if (tokenizer.variables.size() > 1)
				{
					if (tokenizer.variables.contains("pi"))
					{
						if ((tokenizer.variables.size() - 1) > 1)
						{
							throw new ParserException("More than one variable present. Only one variable expected!");
						} else
						{
							parser.parse(token1);
						}
					} else
					{
						throw new ParserException("More than one variable present. Only one variable expected!");
					}
				} else if (tokenizer.variables.size() < 1)
				{
					throw new ParserException("One variable expected!");
				} else
				{
					if (tokenizer.variables.contains("pi"))
					{
						throw new ParserException("One variable expected!");
					} else
					{
						parser.parse(token1);
					}
				}
			} else
			{
				if (tokenizer.variables.size() > 0)
				{
					if (tokenizer.variables.contains("pi"))
					{
						if ((tokenizer.variables.size() - 1) > 0)
						{
							throw new ParserException("Unexpected occurrence of variable(s)!");
						} else
						{
							parser.parse(token1);
						}
					} else
					{
						throw new ParserException("Unexpected occurrence of variable(s)!");
					}
				} else
				{
					parser.parse(token1);
				}
			}
		} catch (ParserException e)
		{
			return e.getMessage();
		}
		return null;
	}

	public void populatingTextView(HashMap<String, String> values)
	{

		final String title = "TITLE";
		final String subTitle = "SUBTITLE";
		final String para = "PARA";
		final String error = "ERROR";
		_equation.setEnabled(true);
		if (values != null)
		{
			if (values.containsKey(error))
			{
				addTitleToTable(values.get(error));
			} else
			{
				if (oldTitle.equals(values.get(title)))
				{
					if (values.containsKey(subTitle))
					{
						Log.d(LOG_TAG, values.get(subTitle));
						addSubTitleToTable(values.get(subTitle));
					}
					addParaToTable(values.get(para));
				} else
				{
					addTitleToTable(values.get(title));
					if (values.containsKey(subTitle))
					{
						Log.d(LOG_TAG, values.get(subTitle));
						addSubTitleToTable(values.get(subTitle));
					}
					addParaToTable(values.get(para));
					oldTitle = values.get(title);
				}
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
			titleTextView.setTextColor(Color.rgb(0x00, 0x69, 0x5C));
			titleTextView.setTypeface(null, Typeface.BOLD);
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
			subTitleTextView.setTextColor(Color.rgb(0x00, 0x69, 0x5C));
			subTitleTextView.setTypeface(null, Typeface.BOLD);
			subTitleTextView.setTextSize(15);
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
			paraTextView.setTextColor(Color.rgb(0xD3, 0x2F, 0x2F));
			paraTextView.setTextSize(15);
			paraTextView.setPadding(5, 5, 5, 5);
			tr.addView(paraTextView, new TableRow.LayoutParams());
			_tableResultLayout.addView(tr, new TableLayout.LayoutParams());
		}
	}

	public void afterReturnFromAsyncTask(String result)
	{
		_equation.setEnabled(true);
		if (_tableResultLayout.getChildCount() <= 1 && result.length() == 0)
		{
			//addTitleToTable("Invalid Equation!");
			_downloadButton.setEnabled(false);
		} else
		{
			_txtFileString = result;
			_downloadButton.setEnabled(true);
		}
	}

	public void afterSavingFile()
	{
		_downloadButton.setEnabled(true);
		Toast.makeText(_activity, "Download Completed!", Toast.LENGTH_LONG).show();
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
