package com.github.drxaos.modelviewer;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class TextAreaOutputStream extends OutputStream {

    private JTextArea textArea;
    private final StringBuilder sb = new StringBuilder();
    private final List<String> log = new ArrayList<>();

    public TextAreaOutputStream() {
    }

    public void setTextArea(JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }

    @Override
    public void write(int b) throws IOException {

        if (b == '\r')
            return;

        if (b == '\n') {
            final String text = sb.toString() + "\n";
            if (textArea != null) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        textArea.append(text);
                    }
                });
            }
            sb.setLength(0);
            return;
        }

        sb.append((char) b);
    }
}