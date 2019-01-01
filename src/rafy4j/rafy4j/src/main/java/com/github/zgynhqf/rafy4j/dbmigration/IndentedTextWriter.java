package com.github.zgynhqf.rafy4j.dbmigration;

import com.github.zgynhqf.rafy4j.Consts;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * @author: huqingfang
 * @date: 2018-12-26 00:53
 **/
public class IndentedTextWriter extends Writer {
    public static final String DEFAULT_TAB_STRING = "    ";

    private int indent = 0;
    private boolean isNewLine = false;
    private Writer writer;

    public IndentedTextWriter() {
        this.writer = new StringWriter();
    }

    public IndentedTextWriter(StringWriter writer) {
        this.writer = writer;
    }

    public int getIndent() {
        return indent;
    }

    public void setIndent(int indent) {
        this.indent = indent;
    }

    public void plusIndent() {
        indent++;
    }

    public void minusIndent() {
        indent--;
    }

    public void writeLine(String str)  {
        this.write(str);
        this.writeLine();
    }

    @Override
    public void write(String str)  {
        try {
            super.write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        if (isNewLine) {
            for (int i = 0; i < indent; i++) {
                writer.write(DEFAULT_TAB_STRING);
            }
            isNewLine = false;
        }
        writer.write(cbuf, off, len);
    }

    @Override
    public void flush()  {
        try {
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeLine() {
        try {
            writer.write(Consts.NEW_LINE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        isNewLine = true;
    }

    @Override
    public String toString() {
        return writer.toString();
    }
}