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

import groove.abstraction.EdgeSignature;
import groove.abstraction.EquivClass;
import groove.abstraction.Multiplicity;
import groove.abstraction.Shape;
import groove.abstraction.ShapeNode;
import groove.gui.Options;
import groove.gui.jgraph.JGraph;
import groove.util.Groove;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * EDUARDO
 * @author Eduardo Zambon
 * @version $Revision $
 */
public class ShapeDialog {

    private final JGraph jgraph;
    JFrame frame;
    Shape shape;
    String title;

    /** Creates and shows a shape in a pop-up window. */
    public ShapeDialog(Shape shape, Options options, String windowTitle) {
        this.jgraph =
            new JGraph(ShapeJModel.getInstance(shape, options), false, null);
        this.jgraph.setPreferredSize(new Dimension(800, 350));
        this.title = windowTitle;
        this.shape = shape;

        this.jgraph.getLayouter().start(false);

        this.jgraph.setEnabled(true);
        getFrame().pack();
        getFrame().setVisible(true);
    }

    private JFrame getFrame() {
        if (this.frame == null) {
            this.frame = new JFrame(this.title);
            this.frame.setIconImage(Groove.GROOVE_ICON_16x16.getImage());

            this.frame.getContentPane().setLayout(new BorderLayout());
            this.frame.getContentPane().add(this.jgraph, BorderLayout.NORTH);

            String columnNames[] = {"EquivRel", "OutMult", "InMult"};
            Object data[][] = new Object[5][3];
            int i = 0;
            int j = 0;
            for (EquivClass<ShapeNode> ec : this.shape.getEquivRelation()) {
                data[i][j] = ec.toString();
                i++;
            }
            i = 0;
            j = 1;
            for (Entry<EdgeSignature,Multiplicity> entry : this.shape.getOutEdgeMultMap().entrySet()) {
                data[i][j] = entry.toString();
                i++;
            }
            i = 0;
            j = 2;
            for (Entry<EdgeSignature,Multiplicity> entry : this.shape.getInEdgeMultMap().entrySet()) {
                data[i][j] = entry.toString();
                i++;
            }

            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            this.frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

            JButton closeButton = new JButton("Close", null);
            closeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    ShapeDialog.this.frame.dispose();
                }
            });
            JPanel bottomPanel = new JPanel();
            bottomPanel.add(closeButton);
            this.frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        }
        return this.frame;
    }
}
