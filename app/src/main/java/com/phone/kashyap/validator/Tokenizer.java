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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer
{
    private class TokenInfo
    {
        public final Pattern regex;
        public final int token;


        public TokenInfo(Pattern regex, int token)
        {
            super();
            this.regex = regex;
            this.token = token;
        }
    }

    public HashSet<String> variables;
    private LinkedList<TokenInfo> tokenInfos;
    private LinkedList<Token> tokens;
    public boolean flag;

    public Tokenizer()
    {
        tokenInfos = new LinkedList<TokenInfo>();
        tokens = new LinkedList<Token>();
        variables = new HashSet<String>();
        this.flag = false;
    }

    public void add(String regex, int token)
    {
        tokenInfos.add(new TokenInfo(Pattern.compile("^("+regex+")"), token));
    }

    public void tokenize(String str)
    {
        String s = str.trim();
        int totalLength = s.length();
        tokens.clear();
        while (!s.equals(""))
        {
            int remaining = s.length();
            boolean match = false;
            for (TokenInfo info : tokenInfos)
            {
                Matcher m = info.regex.matcher(s);
                if (m.find())
                {
                    match = true;
                    String tok = m.group().trim();
                    s = m.replaceFirst("").trim();
                    Token t = new Token(info.token, tok, totalLength-remaining);
                    tokens.add(t);
                    if(info.token == 9){
                        if(!flag){
                            flag = true;
                        }else{
                            throw new ParserException("Equation with two '=' signs cannot be evaluated!");
                        }
                    }
                    if(info.token == 7)
                    {
                        variables.add(t.sequence);
                    }
                    break;
                }
            }
            if (!match) throw new ParserException("Unexpected character '"+s.charAt(0)+"' in input at position "+str.indexOf(s)+".");
        }
    }

    public LinkedList<Token> getTokens()
    {
        return tokens;
    }

}

