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
 * $Id: GraphPatternPopupWindow.java,v 1.1 2007-11-28 16:07:41 iovka Exp $
 */
package groove.gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicDesktopPaneUI;

import org.jgraph.graph.AttributeMap;

import groove.abs.GraphPattern;
import groove.graph.GraphShape;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.JAttr;
import groove.gui.jgraph.JGraph;
import groove.gui.jgraph.PatternGraphJModel;
import groove.gui.layout.SpringLayouter;
import groove.util.Groove;

/** A window for displaying a graph pattern */
public class GraphPatternPopupWindow {

	// -----------------------------------------------------------
	// FIELDS, CONSTRUCTORS, STANDARD METHODS
	// -----------------------------------------------------------
	public GraphPatternPopupWindow (GraphPattern graph, Options options) {
		this.jgraph = new JGraph(PatternGraphJModel.getInstance(graph, options), false); 
		this.jgraph.setPreferredSize(new Dimension(250, 250));
		
		this.jgraph.setEnabled(true);
		getFrame().pack();
		getFrame().setVisible(true);
	}
	
	private JFrame getFrame() {
		if (this.frame == null) {
			this.frame = new JFrame("Graph pattern");
			this.frame.setIconImage(Groove.GROOVE_ICON_16x16.getImage());
			
			this.frame.getContentPane().setLayout(new BorderLayout());
			this.frame.getContentPane().add(this.jgraph, BorderLayout.CENTER);
			JButton closeButton = new JButton("Close", null);
			closeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					GraphPatternPopupWindow.this.frame.dispose();
				}
			});
			JPanel bottomPanel = new JPanel();
			bottomPanel.add(closeButton);
			this.frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH); 
		}
		return this.frame;
	}
	
	private JGraph jgraph;
	JFrame frame;
	
}
