package com.phone.kashyap.validator;

/*
 * This software and all files contained in it are distrubted under the MIT license.
 *
 * Copyright (c) 2013 Cogito Learning Ltd
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
public class Token {
	public static final int EPSILON = 0;
	public static final int PLUSMINUS = 4;
	public static final int MULTDIV = 5;
	public static final int RAISED = 8;
	public static final int FUNCTION = 1;
	public static final int OPEN_BRACKET = 2;
	public static final int CLOSE_BRACKET = 3;
	public static final int NUMBER = 6;
	public static final int VARIABLE = 7;
	public static final int EQUAL = 9;

	public final int token;
	public final String sequence;
	public final int position;

	public Token(int token, String sequence, int position)
	{
		super();
		this.token = token;
		this.sequence = sequence;
		this.position = position;
		
	}

}
