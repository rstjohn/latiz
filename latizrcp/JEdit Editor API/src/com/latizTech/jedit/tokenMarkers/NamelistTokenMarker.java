package com.latizTech.jedit.tokenMarkers;

/*
 * FortranTokenMarker.java - Fortran token marker
 * by Carl Smotricz
 * carl@smotricz.com
 * www.smotricz.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import com.latizTech.jedit.KeywordMap;
import com.latizTech.jedit.Token;
import javax.swing.text.Segment;

/**
 * Custom TokenMarker for UNISYS's <cite>ASCII FORTRAN 77</cite>.
 * Characteristics of this dialect are:
 * <ul>
 *  <li>Fortran Namelist.
 *  <ul>
 *   <li>comment character ! </li>
 *  </ul>
 *  </li>
 * </ul>
 * It should be easy enough to adapt this class for minor variations
 * in the dialect so long as the format is the classic fixed column format.
 * As this scanner is highly optimized for the fixed column format, it
 * is probably not readily adaptable for freeform FORTRAN code.
 */
@SuppressWarnings("unchecked")
public class NamelistTokenMarker extends TokenMarker {

    private static KeywordMap fortranKeywords;
    private KeywordMap keywords;
    private int lastOffset;
    private int lastKeyword;

    /**
     * Constructor, with a wee bit of initialization.
     */
    public NamelistTokenMarker() {
        super();
        this.keywords = getKeywords();
    }

    /**
     * Implementation of code to mark tokens.
     */
    @SuppressWarnings("fallthrough")
    public byte markTokensImpl(byte token, Segment line, int lineIndex) {
        char[] array = line.array;
        int offset = line.offset;
        lastOffset = offset;
        lastKeyword = offset;
        int len = line.count + offset;
        boolean isOperatorHighlighted = true;

        loop:
        for (int i = offset; i < len; i++) {
            int i1 = (i + 1);
            char c = array[i];
            switch (token) {
                case Token.NULL:
                    switch (c) {
                        case '!':
                            addToken(i - lastOffset, token);
                            addToken(len - i, Token.COMMENT1);
                            lastOffset = lastKeyword = len;
                            break loop;
                        case '\"':
                            doKeyword(line, i, c);
                            addToken(i - lastOffset, token);
                            token = Token.LITERAL1;
                            lastOffset = lastKeyword = i;
                            break;
                        case '\'':
                            doKeyword(line, i, c);
                            addToken(i - lastOffset, token);
                            token = Token.LITERAL2;
                            lastOffset = lastKeyword = i;
                            break;
                        case '*':
                        case '=':
                            if (isOperatorHighlighted) {
                                guardedAddToken(i - lastOffset, token);
                                addToken(1, Token.OPERATOR);
                                lastOffset = lastKeyword = i1;
                                break;
                            }
                        default:
                            if (!Character.isLetterOrDigit(c) && c != '_') {
                                doKeyword(line, i, c);
                            }
                            break;
                    }
                    break;

                case Token.COMMENT1:
                case Token.COMMENT2:
                case Token.LITERAL1:
                    if (c == '\"') {
                        addToken(i1 - lastOffset, token);
                        token = Token.NULL;
                        lastOffset = lastKeyword = i1;
                    }
                    break;
                case Token.LITERAL2:
                    if (c == '\'') {
                        addToken(i1 - lastOffset, Token.LITERAL1);
                        token = Token.NULL;
                        lastOffset = lastKeyword = i1;
                    }
                    break;
                default:
                    throw new InternalError("Invalid state: " + token);
            }
        }

        if (token == Token.NULL) {
            doKeyword(line, len, '\000');
        }

        switch (token) {
            case Token.LITERAL1:
            case Token.LITERAL2:
                addToken(len - lastOffset, Token.INVALID);
                token = Token.NULL;
                break;
            case Token.KEYWORD2:
            default:
                addToken(len - lastOffset, token);
                break;
        }
        return token;
    }

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

    /**
     * Call addToken only if the length of the token is not 0.
     */
    private void guardedAddToken(int len, byte token) {
        if (len > 0) {
            addToken(len, token);
        }
    }

    /**
     * Return the keyword map.
     * It's lazily initialized on the first call.
     */
    public static KeywordMap getKeywords() {
        if (fortranKeywords == null) {
            fortranKeywords = new KeywordMap(true);

            // === Commands ===
            fortranKeywords.add("u", Token.KEYWORD1);
            fortranKeywords.add("l", Token.KEYWORD1);
            fortranKeywords.add("ap", Token.KEYWORD1);
            fortranKeywords.add("ip", Token.KEYWORD1);
            fortranKeywords.add("iw", Token.KEYWORD1);
            fortranKeywords.add("rw", Token.KEYWORD1);
            fortranKeywords.add("zp", Token.KEYWORD1);

            fortranKeywords.add("da", Token.KEYWORD1);
            fortranKeywords.add("ida", Token.KEYWORD1);
            fortranKeywords.add("id", Token.KEYWORD1);
            fortranKeywords.add("dp", Token.KEYWORD1);
            fortranKeywords.add("ro", Token.KEYWORD1);
            fortranKeywords.add("ri", Token.KEYWORD1);
            fortranKeywords.add("ri", Token.KEYWORD1);

            fortranKeywords.add("c", Token.COMMENT1);
            fortranKeywords.add("*", Token.OPERATOR);
            fortranKeywords.add("=", Token.OPERATOR);

            fortranKeywords.add("&end", Token.LITERAL1);
            fortranKeywords.add("&acs0", Token.LITERAL1);

            // === Compiler directives ===
            fortranKeywords.add("fn", Token.KEYWORD2);

            // === Data types (etc.) ===
        }
        return fortranKeywords;
    }

    @Override
    public boolean supportsMultilineTokens() {
        return false;
    }
}
//End of FortranTokenMarker.java

