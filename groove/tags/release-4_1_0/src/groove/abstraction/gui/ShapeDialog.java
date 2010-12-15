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
 * $Id$
 */
package groove.abstraction.gui;

import groove.abstraction.Shape;
import groove.abstraction.gui.jgraph.ShapeJGraph;
import groove.abstraction.gui.jgraph.ShapeJModel;
import groove.gui.Options;
import groove.util.Groove;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Class for debugging abstraction.
 * 
 * @author Eduardo Zambon
 */
public class ShapeDialog {

    /** Options for the dialog. */
    private static Options options = new Options();
    static {
        options.setValue(Options.SHOW_NODE_IDS_OPTION, 0);
        options.setValue(Options.SHOW_VERTEX_LABELS_OPTION, 0);
    }

    private final ShapeJModel jModel;
    private final ShapeJGraph jGraph;
    JFrame frame;
    String title;

    /** Creates and shows a shape in a pop-up window. */
    public ShapeDialog(Shape shape, String windowTitle) {
        this(shape, options, windowTitle);
    }

    /** Creates and shows a shape in a pop-up window. */
    public ShapeDialog(Shape shape, Options options, String windowTitle) {
        this.jModel = new ShapeJModel(shape, options);
        this.jGraph = new ShapeJGraph();
        this.jGraph.setJModel(this.jModel);
        this.title = windowTitle;

        this.jGraph.runLayout();

        this.jGraph.setEnabled(true);
        getFrame().pack();
        getFrame().setVisible(true);
    }

    private JFrame getFrame() {
        if (this.frame == null) {
            this.frame = new JFrame(this.title);
            this.frame.setIconImage(Groove.GROOVE_ICON_16x16.getImage());
            this.frame.setPreferredSize(new Dimension(600, 450));
            this.frame.setMinimumSize(this.frame.getPreferredSize());

            JScrollPane scrollPane = new JScrollPane(this.jGraph);

            JButton closeButton = new JButton("Close", null);
            closeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    ShapeDialog.this.frame.dispose();
                }
            });
            JPanel bottomPanel = new JPanel();
            bottomPanel.add(closeButton);
            bottomPanel.setPreferredSize(new Dimension(20, 35));
            bottomPanel.setMaximumSize(bottomPanel.getPreferredSize());
            bottomPanel.setMinimumSize(bottomPanel.getPreferredSize());

            this.frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
            this.frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        }
        return this.frame;
    }
}
