package com.latizTech.jedit.tokenMarkers;

import com.latizTech.jedit.KeywordMap;
import com.latizTech.jedit.Token;

/**
 * Java token marker.
 *
 * @author Slava Pestov
 * @version $Id: JavaTokenMarker.java,v 1.1 2007/05/25 00:12:28 stjohnr Exp $
 */
public class JavaTokenMarker extends CTokenMarker {
  
  private static KeywordMap javaKeywords;
  /*
   * 
   */
  public JavaTokenMarker() {
    super(false,getKeywords());
  }

  public static KeywordMap getKeywords() {
    if(javaKeywords == null) {
      javaKeywords = new KeywordMap(false);
      javaKeywords.add("package",Token.ECLIPSE_PACKAGE);
      javaKeywords.add("import",Token.ECLIPSE_PACKAGE);
      javaKeywords.add("byte",Token.KEYWORD3);
      javaKeywords.add("char",Token.KEYWORD3);
      javaKeywords.add("short",Token.KEYWORD3);
      javaKeywords.add("int",Token.KEYWORD3);
      javaKeywords.add("long",Token.KEYWORD3);
      javaKeywords.add("float",Token.KEYWORD3);
      javaKeywords.add("double",Token.KEYWORD3);
      javaKeywords.add("boolean",Token.KEYWORD3);
      javaKeywords.add("void",Token.ECLIPSE_PACKAGE);
      javaKeywords.add("class",Token.ECLIPSE_PACKAGE);
      javaKeywords.add("interface",Token.ECLIPSE_PACKAGE);
      javaKeywords.add("abstract",Token.ECLIPSE_PACKAGE);
      javaKeywords.add("final",Token.ECLIPSE_PACKAGE);
      javaKeywords.add("private",Token.ECLIPSE_PACKAGE);
      javaKeywords.add("protected",Token.ECLIPSE_PACKAGE);
      javaKeywords.add("public",Token.ECLIPSE_PACKAGE);
      javaKeywords.add("static",Token.ECLIPSE_PACKAGE);
      javaKeywords.add("synchronized",Token.ECLIPSE_PACKAGE);
      javaKeywords.add("native",Token.ECLIPSE_PACKAGE);
      javaKeywords.add("volatile",Token.ECLIPSE_PACKAGE);
      javaKeywords.add("transient",Token.ECLIPSE_PACKAGE);
      javaKeywords.add("break",Token.KEYWORD1);
      javaKeywords.add("case",Token.KEYWORD1);
      javaKeywords.add("continue",Token.KEYWORD1);
      javaKeywords.add("default",Token.KEYWORD1);
      javaKeywords.add("do",Token.KEYWORD1);
      javaKeywords.add("else",Token.KEYWORD1);
      javaKeywords.add("for",Token.KEYWORD1);
      javaKeywords.add("if",Token.KEYWORD1);
      javaKeywords.add("instanceof",Token.KEYWORD1);
      javaKeywords.add("new",Token.ECLIPSE_PACKAGE);
      javaKeywords.add("return",Token.ECLIPSE_PACKAGE);
      javaKeywords.add("switch",Token.KEYWORD1);
      javaKeywords.add("while",Token.KEYWORD1);
      javaKeywords.add("throw",Token.KEYWORD1);
      javaKeywords.add("try",Token.ECLIPSE_PACKAGE);
      javaKeywords.add("catch",Token.ECLIPSE_PACKAGE);
      javaKeywords.add("extends",Token.ECLIPSE_PACKAGE);
      javaKeywords.add("finally",Token.ECLIPSE_PACKAGE);
      javaKeywords.add("implements",Token.KEYWORD1);
      javaKeywords.add("throws",Token.KEYWORD1);
      javaKeywords.add("this",Token.LITERAL2);
      javaKeywords.add("null",Token.LITERAL2);
      javaKeywords.add("super",Token.LITERAL2);
      javaKeywords.add("true",Token.LITERAL2);
      javaKeywords.add("false",Token.LITERAL2);
    }
    return javaKeywords;
  }
}
