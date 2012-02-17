package com.latizTech.jedit.tokenMarkers;
/*
 * PatchTokenMarker.java - DIFF patch token marker
 * Copyright (C) 1999 Slava Pestov
 *
 * You may use and modify this package for any purpose. Redistribution is
 * permitted, in both source and binary form, provided that this notice
 * remains intact in all source distributions of this package.
 */

import com.latizTech.jedit.Token;
import javax.swing.text.Segment;

/**
 * Patch/diff token marker.
 *
 * @author Slava Pestov
 * @version $Id: PatchTokenMarker.java,v 1.1 2007/05/25 00:12:28 stjohnr Exp $
 */
public class PatchTokenMarker extends TokenMarker
{
	public byte markTokensImpl(byte token, Segment line, int lineIndex)
	{
		if(line.count == 0)
			return Token.NULL;
		switch(line.array[line.offset])
		{
		case '+': case '>':
			addToken(line.count,Token.KEYWORD1);
			break;
		case '-': case '<':
			addToken(line.count,Token.KEYWORD2);
			break;
		case '@': case '*':
			addToken(line.count,Token.KEYWORD3);
			break;
	        default:
			addToken(line.count,Token.NULL);
			break;
		}
		return Token.NULL;
	}

	public boolean supportsMultilineTokens()
	{
		return false;
	}
}
