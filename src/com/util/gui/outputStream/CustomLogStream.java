package com.util.gui.outputStream;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by rajendv3 on 3/07/2017.
 */
public class CustomLogStream extends OutputStream {

    private JTextArea textArea;

    public CustomLogStream(JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void write(int b) throws IOException {
        textArea.append(String.valueOf((char) b));
        textArea.setCaretPosition(textArea.getDocument().getLength());
        textArea.getCaret().setBlinkRate(0);
        textArea.update(textArea.getGraphics());
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        textArea.append(new String(b,off,len));
        textArea.setCaretPosition(textArea.getDocument().getLength());
        textArea.getCaret().setBlinkRate(0);
        textArea.update(textArea.getGraphics());
    }
}
