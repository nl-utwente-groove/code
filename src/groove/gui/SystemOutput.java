/*
 * Groove Prolog Interface
 * Copyright (C) 2009 Michiel Hendriks, University of Twente
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package groove.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

/**
 * A panel that captures the data from the System.out and System.err.
 * 
 * @author Michiel Hendriks
 */
public class SystemOutput extends JPanel {
    private static final long serialVersionUID = 6148032699629502139L;

    /**
     * TODO
     */
    public SystemOutput() {
        super();
        setLayout(new BorderLayout());
        Font editFont = new Font("Monospaced", Font.PLAIN, 12);
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        JTextArea output = new JTextArea();
        output.setFont(editFont);
        output.setText("");
        output.setEditable(false);
        output.setEnabled(true);
        output.setBackground(null);
        System.setOut(new PrintStream(new UIOutputStream(output), true));
        splitPane.setTopComponent(new JScrollPane(output));
        final JTextArea stdout = output;
        toolbar.add(new AbstractAction("Clear stdout") {
            private static final long serialVersionUID = 1663612667198925970L;

            public void actionPerformed(ActionEvent e) {
                stdout.setText("");
            }
        });

        // std error
        output = new JTextArea();
        output.setFont(editFont);
        output.setForeground(Color.RED);
        output.setText("");
        output.setEditable(false);
        output.setEnabled(true);
        output.setBackground(null);
        System.setErr(new PrintStream(new UIOutputStream(output), true));

        splitPane.setBottomComponent(new JScrollPane(output));
        splitPane.setDividerLocation(100);

        final JTextArea stderr = output;
        toolbar.add(new AbstractAction("Clear stderr") {
            private static final long serialVersionUID = 1663612667198925970L;

            public void actionPerformed(ActionEvent e) {
                stderr.setText("");
            }
        });

        add(toolbar, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    /**
     * An output stream which appends the data to a {@link JTextArea}
     * 
     * @author Michiel Hendriks
     */
    class UIOutputStream extends OutputStream {
        JTextArea dest;

        static final int BUFFER_SIZE = 512;
        int[] buffer = new int[BUFFER_SIZE];
        int pos = 0;

        public UIOutputStream(JTextArea toArea) {
            this.dest = toArea;
        }

        /*
         * (non-Javadoc)
         * @see java.io.OutputStream#write(int)
         */
        @Override
        public void write(int b) throws IOException {
            this.buffer[this.pos++] = b;
            if (this.pos >= this.buffer.length) {
                flush();
            }
        }

        /*
         * (non-Javadoc)
         * @see java.io.OutputStream#flush()
         */
        @Override
        public void flush() throws IOException {
            super.flush();
            if (this.pos == 0) {
                return;
            }
            this.dest.append(new String(this.buffer, 0, this.pos));
            this.buffer = new int[BUFFER_SIZE];
            this.pos = 0;
        }
    }
}
