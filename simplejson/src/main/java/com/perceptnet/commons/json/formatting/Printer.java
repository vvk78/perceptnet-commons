package com.perceptnet.commons.json.formatting;

import java.io.PrintStream;
import java.util.Deque;
import java.util.LinkedList;


/**
 * created by vkorovkin (vkorovkin@gmail.com) on 05.12.2017
 */
public class Printer {
    private PrintStream out = System.out;

    private boolean startWithIndentation;
    private Deque<String> indentStack = new LinkedList<>();

    public void setOut(PrintStream out) {
        this.out = out;
    }

    public void print(int i) {
        print("" + i);
    }

    public void print(String s) {
        s = processIndentation(s);
        out.print(s);
        out.flush();
    }

    public void print(Object obj) {
        print("" + obj);
    }

    public void println() {
        out.println();
        startWithIndentation = getCurIndentation() != null;
    }

    public void println(int x) {
        String s = processIndentation("" + x);
        out.println(s);
        startWithIndentation = true;
    }

    public void println(String s) {
        s = processIndentation(s);
        out.println(s);
        startWithIndentation = true;
    }

    private String processIndentation(String s) {
        String curIndentation = getCurIndentation();
        s = indent(s, curIndentation);
        if (startWithIndentation) {
            startWithIndentation = false;
            if (curIndentation != null) {
                s = curIndentation + s;
            }
        }
        if (s.endsWith("\n")) {
            startWithIndentation = true;
        }
        return s;
    }

    public String getCurIndentation() {
        if (indentStack.isEmpty()) {
            return null;
        }
        return indentStack.peek();
    }

    public void pushIndentation(String addition) {
        String cur = getCurIndentation();
        if (cur != null && !cur.isEmpty()) {
            indentStack.push(cur + addition);
        } else {
            indentStack.push(addition);
        }
    }

    public void popIndentation() {
        if (indentStack.isEmpty()) {
            return;
        }
        indentStack.pop();
    }




    public void clearIndentation() {
        indentStack.clear();
    }

    private String indent(String str, String indentation) {
        if (indentation == null || indentation.isEmpty() || str == null || str.isEmpty()) {
            return str;
        }

        if (str.endsWith("\r\n")) {
            return str.substring(0, str.length() - 2).replace("\n", "\n" + indentation) + "\r\n";
        } else if (str.endsWith("\n")) {
            return str.substring(0, str.length() - 1).replace("\n", "\n" + indentation) + "\n";
        } else {
            return str.replace("\n", "\n" + indentation);
        }
    }
}


