/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id$
 */
package groove.gui.dialog;

import java.awt.Dimension;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
public class ProgressBarDialog extends JDialog {
    /**
     * Constructs a dialog for a given parent frame and title.
     */
    public ProgressBarDialog(JFrame parent, String title) {
        super(parent, title);
        setLocationRelativeTo(parent);
        getContentPane().add(getPanel());
        pack();
    }

    /**
     * Tells the dialog to make itself visible after a given delay, unless it is
     * deactivated before the delay expires.
     * @param millis the delay, measured in milliseconds
     */
    synchronized public void activate(long millis) {
        deactivate();
        this.activation = new Timer();
        this.activation.schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        setVisible(true);
                        // cancel the timer because it may otherwise go on running,
                        // keeping the entire program from terminating
                        synchronized (ProgressBarDialog.this) {
                            if (ProgressBarDialog.this.activation != null) {
                                ProgressBarDialog.this.activation.cancel();
                            }
                        }
                    }
                });
            }
        }, millis);
    }

    /**
     * Cancels the scheduled activation, if any, and sets the visibility of the
     * dialog to false.
     */
    synchronized public void deactivate() {
        if (this.activation != null) {
            this.activation.cancel();
            this.activation = null;
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setVisible(false);
            }
        });
    }

    /** Sets the text message in the label to a certain value. */
    public void setMessage(String text) {
        getLabel().setText(text);
    }

    /**
     * Sets the range of the progress bar.
     */
    public void setRange(int lower, int upper) {
        getBar().setMinimum(lower);
        getBar().setMaximum(upper);
        getBar().setValue(lower);
        getBar().setIndeterminate(false);
    }

    /**
     * Sets a progress value for the progress bar. This only has effect if
     * {@link #setRange(int, int)} was called first.
     * @param progress The progress value; should be in the range initialised in
     *        {@link #setRange(int, int)}
     */
    public void setProgress(int progress) {
        getBar().setValue(progress);
    }

    /**
     * Increments the progress value of the progress bar by <code>1</code>.
     * This only has effect if {@link #setRange(int, int)} was called first.
     */
    public void incProgress() {
        getBar().setValue(getBar().getValue() + 1);
    }

    private Box getPanel() {
        Box result = Box.createVerticalBox();
        result.setBorder(new EmptyBorder(BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH));
        result.add(getLabel());
        result.add(Box.createVerticalGlue());
        result.add(getBar());
        result.setPreferredSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
        return result;
    }

    /**
     * Lazily creates and returns the message label on the dialog.
     */
    private JLabel getLabel() {
        if (this.label == null) {
            this.label = new JLabel();
            this.label.setSize(LABEL_WIDTH, LABEL_HEIGHT);
        }
        return this.label;
    }

    /** Lazily creates and returns the progress bar on the dialog. */
    private JProgressBar getBar() {
        if (this.bar == null) {
            this.bar = new JProgressBar();
            this.bar.setSize(LABEL_WIDTH, LABEL_HEIGHT);
            this.bar.setIndeterminate(true);
            this.bar.setStringPainted(true);
        }
        return this.bar;
    }

    /** The message label of the dialog. */
    private JLabel label;
    /** The progress bar of the dialog. */
    private JProgressBar bar;
    /* 8 The activation timer. */
    private Timer activation;

    static private final int BORDER_WIDTH = 20;
    static private final int DIALOG_WIDTH = 200;
    static private final int DIALOG_HEIGHT = 100;
    static private final int LABEL_WIDTH = DIALOG_WIDTH - 2 * BORDER_WIDTH;
    static private final int LABEL_HEIGHT = 25;
}
