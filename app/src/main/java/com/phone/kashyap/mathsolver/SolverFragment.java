package com.phone.kashyap.mathsolver;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Kashyap on 11/30/2014.
 */
public class SolverFragment extends Fragment
{
	String arithExpression;

	public SolverFragment() {}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_solver,container, false);
		return view;
	}


}
