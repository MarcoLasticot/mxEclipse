package org.mxeclipse.utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.Hashtable;
import java.util.Vector;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

public class JavaLineStyler
  implements LineStyleListener
{
  JavaScanner scanner = new JavaScanner();
  int[] tokenColors;
  Color[] colors;
  Vector blockComments = new Vector();
  public static final int EOF = -1;
  public static final int EOL = 10;
  public static final int WORD = 0;
  public static final int WHITE = 1;
  public static final int KEY = 2;
  public static final int COMMENT = 3;
  public static final int STRING = 5;
  public static final int OTHER = 6;
  public static final int NUMBER = 7;
  public static final int MAXIMUM_TOKEN = 8;

  public JavaLineStyler()
  {
    initializeColors();
    this.scanner = new JavaScanner();
  }

  Color getColor(int type) {
    if ((type < 0) || (type >= this.tokenColors.length)) {
      return null;
    }
    return this.colors[this.tokenColors[type]];
  }

  boolean inBlockComment(int start, int end) {
    for (int i = 0; i < this.blockComments.size(); i++) {
      int[] offsets = (int[])this.blockComments.elementAt(i);

      if ((offsets[0] >= start) && (offsets[0] <= end)) return true;

      if ((offsets[1] >= start) && (offsets[1] <= end)) return true;
      if ((offsets[0] <= start) && (offsets[1] >= end)) return true;
    }
    return false;
  }

  void initializeColors() {
    Display display = Display.getDefault();
    this.colors = new Color[] { 
      new Color(display, new RGB(0, 0, 0)), 
      new Color(display, new RGB(255, 0, 0)), 
      new Color(display, new RGB(0, 255, 0)), 
      new Color(display, new RGB(0, 0, 255)) };

    this.tokenColors = new int[8];
    this.tokenColors[0] = 0;
    this.tokenColors[1] = 0;
    this.tokenColors[2] = 3;
    this.tokenColors[3] = 1;
    this.tokenColors[5] = 2;
    this.tokenColors[6] = 0;
    this.tokenColors[7] = 0;
  }

  void disposeColors() {
    for (int i = 0; i < this.colors.length; i++)
      this.colors[i].dispose();
  }

  public void lineGetStyle(LineStyleEvent event)
  {
    Vector styles = new Vector();

    if (inBlockComment(event.lineOffset, event.lineOffset + event.lineText.length())) {
      styles.addElement(new StyleRange(event.lineOffset, event.lineText.length(), getColor(3), null));
      event.styles = new StyleRange[styles.size()];
      styles.copyInto(event.styles);
      return;
    }
    Color defaultFgColor = ((Control)event.widget).getForeground();
    this.scanner.setRange(event.lineText);
    int token = this.scanner.nextToken();
    while (token != -1) {
      if (token != 6)
      {
        if (token != 1) {
          Color color = getColor(token);

          if ((!color.equals(defaultFgColor)) || (token == 2)) {
            StyleRange style = new StyleRange(this.scanner.getStartOffset() + event.lineOffset, this.scanner.getLength(), color, null);
            if (token == 2) {
              style.fontStyle = 1;
            }
            if (styles.isEmpty()) {
              styles.addElement(style);
            }
            else {
              StyleRange lastStyle = (StyleRange)styles.lastElement();
              if ((lastStyle.similarTo(style)) && (lastStyle.start + lastStyle.length == style.start))
                lastStyle.length += style.length;
              else
                styles.addElement(style);
            }
          }
        }
        else
        {
          StyleRange lastStyle;
          if ((!styles.isEmpty()) && ((lastStyle = (StyleRange)styles.lastElement()).fontStyle == 1)) {
            int start = this.scanner.getStartOffset() + event.lineOffset;
            lastStyle = (StyleRange)styles.lastElement();

            if (lastStyle.start + lastStyle.length == start)
            {
              lastStyle.length += this.scanner.getLength();
            }
          }
        }
      }
      token = this.scanner.nextToken();
    }
    event.styles = new StyleRange[styles.size()];
    styles.copyInto(event.styles);
  }
  public void parseBlockComments(String text) {
    this.blockComments = new Vector();
    StringReader buffer = new StringReader(text);

    boolean blkComment = false;
    int cnt = 0;
    int[] offsets = new int[2];
    boolean done = false;
    try
    {
      while (!done)
      {
        int ch;
        switch (ch = buffer.read()) {
        case -1:
          if (blkComment) {
            offsets[1] = cnt;
            this.blockComments.addElement(offsets);
          }
          done = true;
          break;
        case 47:
          ch = buffer.read();
          if ((ch == 42) && (!blkComment)) {
            offsets = new int[2];
            offsets[0] = cnt;
            blkComment = true;
            cnt++;
          } else {
            cnt++;
          }
          cnt++;
          break;
        case 42:
          if (blkComment) {
            ch = buffer.read();
            cnt++;
            if (ch == 47) {
              blkComment = false;
              offsets[1] = cnt;
              this.blockComments.addElement(offsets);
            }
          }
          cnt++;
          break;
        default:
          cnt++;
        }
      }
    }
    catch (IOException localIOException)
    {
    }
  }

  public class JavaScanner
  {
    protected Hashtable fgKeys = null;
    protected StringBuffer fBuffer = new StringBuffer();
    protected String fDoc;
    protected int fPos;
    protected int fEnd;
    protected int fStartToken;
    protected boolean fEofSeen = false;

    private String[] fgKeywords = { 
      "abstract", 
      "boolean", "break", "byte", 
      "case", "catch", "char", "class", "continue", 
      "default", "do", "double", 
      "else", "extends", 
      "false", "final", "finally", "float", "for", 
      "if", "implements", "import", "instanceof", "int", "interface", 
      "long", 
      "native", "new", "null", 
      "package", "private", "protected", "public", 
      "return", 
      "short", "static", "super", "switch", "synchronized", 
      "this", "throw", "throws", "transient", "true", "try", 
      "void", "volatile", 
      "while" };

    public JavaScanner()
    {
      initialize();
    }

    public final int getLength()
    {
      return this.fPos - this.fStartToken;
    }

    void initialize()
    {
      this.fgKeys = new Hashtable();
      Integer k = new Integer(2);
      for (int i = 0; i < this.fgKeywords.length; i++)
        this.fgKeys.put(this.fgKeywords[i], k);
    }

    public final int getStartOffset()
    {
      return this.fStartToken;
    }

    public int nextToken()
    {
      this.fStartToken = this.fPos;
      int c;
      switch (c = read()) {
      case -1:
        return -1;
      case 47:
        c = read();
        if (c == 47) {
          do
            c = read();
          while ((c != -1) && (c != 10));
          unread(c);
          return 3;
        }

        unread(c);

        return 6;
      case 39:
        while (true) {
          c = read();
          switch (c) {
          case 39:
            return 5;
          case -1:
            unread(c);
            return 5;
          case 92:
            c = read();
          }
        }

      case 34:
        while (true)
        {
          c = read();
          switch (c) {
          case 34:
            return 5;
          case -1:
            unread(c);
            return 5;
          case 92:
            c = read(); }  } case 48:
      case 49:
      case 50:
      case 51:
      case 52:
      case 53:
      case 54:
      case 55:
      case 56:
      case 57:
        do c = read();
        while (
          Character.isDigit((char)c));
        unread(c);
        return 7;
      }
      if (Character.isWhitespace((char)c)) {
        do
          c = read();
        while (
          Character.isWhitespace((char)c));
        unread(c);
        return 1;
      }
      if (Character.isJavaIdentifierStart((char)c)) {
        this.fBuffer.setLength(0);
        do {
          this.fBuffer.append((char)c);
          c = read();
        }
        while (
          Character.isJavaIdentifierPart((char)c));
        unread(c);
        Integer i = (Integer)this.fgKeys.get(this.fBuffer.toString());
        if (i != null)
          return i.intValue();
        return 0;
      }
      return 6;
    }

    protected int read()
    {
      if (this.fPos <= this.fEnd) {
        return this.fDoc.charAt(this.fPos++);
      }
      return -1;
    }

    public void setRange(String text) {
      this.fDoc = text;
      this.fPos = 0;
      this.fEnd = (this.fDoc.length() - 1);
    }

    protected void unread(int c) {
      if (c != -1)
        this.fPos -= 1;
    }
  }
}