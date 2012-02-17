package com.latizTech.jedit.tokenMarkers;
/*
 * CTokenMarker.java - C token marker
 * Copyright (C) 1998, 1999 Slava Pestov
 *
 * You may use and modify this package for any purpose. Redistribution is
 * permitted, in both source and binary form, provided that this notice
 * remains intact in all source distributions of this package.
 */

import com.latizTech.jedit.KeywordMap;
import com.latizTech.jedit.Token;
import javax.swing.text.Segment;

/**
 * C token marker.
 *
 * @author Slava Pestov
 * @version $Id: CTokenMarker.java,v 1.1 2007/05/25 00:12:27 stjohnr Exp $
 */
public class PropertiesTokenMarker extends TokenMarker {

    public PropertiesTokenMarker() {
        this(getKeywords());
    }

    public PropertiesTokenMarker(KeywordMap keywords) {
        this.keywords = keywords;
    }

    public byte markTokensImpl(byte token, Segment line, int lineIndex) {
        char[] array = line.array;
        int offset = line.offset;
        lastOffset = offset;
        lastKeyword = offset;
        int len = line.count + offset;

        loop:
        for (int i = offset; i < len; i++) {
            int i1 = (i + 1);

            char c = array[i];

            switch (token) {
                case Token.NULL:
                    switch (c) {

                        // <editor-fold defaultstate="collapsed" desc="Unused cases">
//                        case '#':
//                            break;
//                        case '"':
//                            doKeyword(line, i, c);
//                            addToken(i - lastOffset, token);
//                            token = Token.LITERAL1;
//                            lastOffset = lastKeyword = i;
//                            break;
//                        case ':':
//                            if (lastKeyword == offset) {
//                                if (doKeyword(line, i, c)) {
//                                    break;
//                                }
//                                addToken(i1 - lastOffset, Token.LABEL);
//                                lastOffset = lastKeyword = i1;
//                            } else if (doKeyword(line, i, c)) {
//                                break;
//                            }
//                            break;
//                        case '/':
//                            doKeyword(line, i, c);
//                            if (len - i > 1) {
//                                switch (array[i1]) {
//                                    case '*':
//                                        addToken(i - lastOffset, token);
//                                        lastOffset = lastKeyword = i;
//                                        if (len - i > 2 && array[i + 2] == '*') {
//                                            token = Token.COMMENT2;
//                                        } else {
//                                            token = Token.COMMENT1;
//                                        }
//                                        break;
//                                    case '/':
//                                        addToken(i - lastOffset, token);
//                                        addToken(len - i, Token.COMMENT1);
//                                        lastOffset = lastKeyword = len;
//                                        break loop;
//                                }
//                            }
//                            break;
// </editor-fold>
                        
                        case '#':
                            doKeyword(line, i, c);
                            addToken(i - lastOffset, token);
                            addToken(len - i, Token.COMMENT5);
                            lastOffset = lastKeyword = len;
                            break loop;
                        case '=':
                            int j=i+1;
                            doKeyword(line, j, c);
                            addToken(j - lastOffset, token);
                            addToken(len - j, Token.KEYWORD4);
                            lastOffset = lastKeyword = len;
                            break loop;
                        default:
                            if (!Character.isLetterOrDigit(c) && c != '_') {
                                doKeyword(line, i, c);
                            }
                            break;
                    }
                    break;
                case Token.COMMENT1:
//                case Token.COMMENT2:
//                    if (c == '*' && len - i > 1) {
//                        if (array[i1] == '/') {
//                            i++;
//                            addToken((i + 1) - lastOffset, token);
//                            token = Token.NULL;
//                            lastOffset = lastKeyword = i + 1;
//                        }
//                    }
//                    break;
//                case Token.LITERAL1:
//                    if (c == '"') {
//                        addToken(i1 - lastOffset, token);
//                        token = Token.NULL;
//                        lastOffset = lastKeyword = i1;
//                    }
//                    break;
                case Token.LITERAL2:
                case Token.LITERAL3:
                    if (c == '\'') {
                        addToken(i1 - lastOffset, Token.LITERAL3);
                        token = Token.NULL;
                        lastOffset = lastKeyword = i1;
                    }
                    break;
                default:
                    throw new InternalError("Invalid state: " + token);
            }
        }

        if (token == Token.NULL) {
            doKeyword(line, len, '\0');
        }

        switch (token) {
            case Token.LITERAL1:
            case Token.LITERAL3:
            case Token.LITERAL2:
                addToken(len - lastOffset, Token.INVALID);
                token = Token.NULL;
                break;
            case Token.KEYWORD2:
                addToken(len - lastOffset, token);
                token = Token.NULL;
                break;
            default:
                addToken(len - lastOffset, token);
                break;
        }

        return token;
    }

    public static KeywordMap getKeywords() {
        if (cKeywords == null) {
            cKeywords = new KeywordMap(false);
        }
        return cKeywords;
    }
    // private members
    private static KeywordMap cKeywords;
    private KeywordMap keywords;
    private int lastOffset;
    private int lastKeyword;

    private boolean doKeyword(Segment line, int i, char c) {
        int i1 = i + 1;

        int len = i - lastKeyword;
        byte id = keywords.lookup(line, lastKeyword, len);
        if (id != Token.NULL) {
            if (lastKeyword != lastOffset) {
                addToken(lastKeyword - lastOffset, Token.NULL);
            }
            addToken(len, id);
            lastOffset = i;
        }
        lastKeyword = i1;
        return false;
    }
}
