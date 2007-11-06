/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id: ProgressBarDialog.java,v 1.1 2007-11-06 16:07:31 rensink Exp $
 */
package groove.gui;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
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
    }

    /** Sets the text message in the label to a certain value. */
    public void setMessage(String text) {
        getLabel().setText(text);
    }
    
    /**
     * Sets the range of the progress bar. 
     * @param lower
     * @param upper
     */
    public void setRange(int lower, int upper) {
        getBar().setMinimum(lower);
        getBar().setMaximum(upper);
        getBar().setValue(lower);
        getBar().setIndeterminate(false);
    }
    
     
    /** 
     * Sets a progress value for the progress bar.
     * This only has effect if {@link #setRange(int, int)} was called first. 
     * @param progress The progress value; should be in the range initialised in {@link #setRange(int, int)}
     */
    public void setProgress(int progress) {
        getBar().setValue(progress);
    }
    
    /** 
     * Increments the progress value of the progress bar by <code>1</code>.
     * This only has effect if {@link #setRange(int, int)} was called first. 
     */
    public void incProgress() {
        getBar().setValue(getBar().getValue()+1);
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
        if (label == null) {
            label = new JLabel();
            label.setSize(LABEL_WIDTH, LABEL_HEIGHT);
        }
        return label;
    }
    
    /** Lazily creates and returns the progress bar on the dialog. */
    private JProgressBar getBar() {
        if (bar == null) {
            bar = new JProgressBar();
            bar.setSize(LABEL_WIDTH, LABEL_HEIGHT);
            bar.setIndeterminate(true);
            bar.setStringPainted(true);
        }
        return bar;
    }
    
    /** The message label of the dialog. */
    private JLabel label;
    /** The progress bar of the dialog. */
    private JProgressBar bar;
    
    static private final int BORDER_WIDTH = 20;
    static private final int DIALOG_WIDTH = 200;
    static private final int DIALOG_HEIGHT = 100;
    static private final int LABEL_WIDTH = DIALOG_WIDTH - 2*BORDER_WIDTH;
    static private final int LABEL_HEIGHT = 25;
}
