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
 * $Id: CAPanel.java,v 1.18 2008-03-18 12:18:19 fladder Exp $
 */
package groove.gui;

import groove.control.ControlAutomaton;
import groove.control.ControlView;
import groove.control.parse.GCLTokenMaker;
import groove.gui.jgraph.ControlJGraph;
import groove.gui.jgraph.ControlJModel;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.NameLabel;
import groove.trans.RuleMatch;
import groove.util.Groove;
import groove.view.DefaultGrammarView;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

/**
 * The Simulator panel that shows the control program, with a buttton that shows
 * the corresponding control automaton.
 * 
 * @author Tom Staijen
 * @version $0.9$
 */
public class CAPanel extends JPanel implements SimulationListener {

    Simulator simulator;
    // AutomatonPanel autPanel;
    RSyntaxTextArea textPanel;
    RSyntaxDocument document;
    DefaultGrammarView grammar;

    JButton editButton, doneButton, viewButton; // , saveButton;

    /**
     * @param simulator The Simulator the panel is added to.
     */
    public CAPanel(Simulator simulator) {
        super();
        this.simulator = simulator;

        // create the layout for this JPanel
        this.setLayout(new BorderLayout());
        JToolBar toolBar = new JToolBar();

        this.editButton = new JButton("Edit");
        toolBar.add(this.editButton);
        this.editButton.setEnabled(false);
        this.editButton.addActionListener(new EditButtonListener());

        this.doneButton = new JButton("Done");
        toolBar.add(this.doneButton);
        this.doneButton.addActionListener(new DoneButtonListener());
        this.doneButton.setEnabled(false);

        this.viewButton = new JButton(Groove.GRAPH_ICON);
        toolBar.add(this.viewButton);
        this.viewButton.addActionListener(new ViewButtonListener());
        this.viewButton.setEnabled(false);


        RSyntaxDocument document = new RSyntaxDocument("gcl");
        document.setSyntaxStyle(new GCLTokenMaker());
        
        this.textPanel = new RSyntaxTextArea(document);
        
        RTextScrollPane scroller = new RTextScrollPane(500,400, this.textPanel, true);
        
        // create editor and surrounding scrollpane
//        this.textPanel = new JEditorPane();
//        JScrollPane scroller = new JScrollPane(this.textPanel);
        
//        this.textPanel.setFont(this.textPanel.getFont().deriveFont((float) 16));
        this.textPanel.setText("");
        this.textPanel.setEditable(false);
        this.textPanel.setEnabled(false);

        this.add(toolBar, BorderLayout.NORTH);
        this.add(scroller, BorderLayout.CENTER);
        
        simulator.addSimulationListener(this);
    }

    /**
     * We do nothing when a transition is applied
     */
    public void applyTransitionUpdate(GraphTransition transition) {
        // // do nothing
    }

    public void setGrammarUpdate(DefaultGrammarView grammar) {
        this.grammar = grammar;

        // autPanel.getJGraph().setModel(ControlJModel.EMPTY_CONTROL_JMODEL);
        // autPanel.getJGraph().setEnabled(false);
        // autPanel.setEnabled(false);
        this.textPanel.setText("");

        if (grammar.getControl() != null) {
            ControlView cv = grammar.getControl();
            // in any case display the program
            this.textPanel.setText(cv.getProgram());
            this.editButton.setEnabled(true);
            // cant view automaton while grammar has errors!
            if (this.simulator.getCurrentGrammar().getErrors().size() == 0) {
                this.viewButton.setEnabled(true);
            }
        } else {
            this.viewButton.setEnabled(false);
            this.editButton.setEnabled(false);
        }
    }

    public void setRuleUpdate(NameLabel name) {
        // nothing happens
    }

    public void setStateUpdate(GraphState state) {
        // nothing happens
    }

    public void setMatchUpdate(RuleMatch match) {
        // nothing happens
    }

    public void setTransitionUpdate(GraphTransition transition) {
        // nothing happens
    }

    public void startSimulationUpdate(GTS gts) {
        // nothing happens
    }

    class DoneButtonListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            ControlView cv = CAPanel.this.grammar.getControl();

            String program = CAPanel.this.textPanel.getText();
            if (program == null || program.length() == 0) {
                return;
            }
            if (cv == null) {
                // return;
                // we had no CV yet.
            }
            // cv.setProgram(CAPanel.this.textPanel.getText());

            CAPanel.this.simulator.handleSaveControl(program);
            CAPanel.this.simulator.doRefreshGrammar();

            // if( CAPanel.this.grammar.getControl().toAutomaton() != null ) {
            CAPanel.this.textPanel.setEditable(false);
            CAPanel.this.textPanel.setEnabled(false);
            CAPanel.this.editButton.setEnabled(true);
            CAPanel.this.doneButton.setEnabled(false);
            // CAPanel.this.saveButton.setEnabled(true);
            // }
        }
    }

    class EditButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            CAPanel.this.textPanel.setEditable(true);
            CAPanel.this.textPanel.setEnabled(true);
            CAPanel.this.editButton.setEnabled(false);
            CAPanel.this.doneButton.setEnabled(true);
            // CAPanel.this.saveButton.setEnabled(false);
        }
    }

    /**
     * Creates a dialog showing the control automaton
     * @author Tom Staijen
     * @version $Revision $
     */
    class ViewButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            assert CAPanel.this.simulator.getCurrentGrammar().getErrors().size() == 0 : "View Button should be disabled if grammar has errors.";

            ControlAutomaton caut =
                CAPanel.this.simulator.getCurrentGrammar().getControl().getAutomaton();
            ControlJGraph cjg =
                new ControlJGraph(new ControlJModel(caut, CAPanel.this.simulator.getOptions()));

            AutomatonPanel autPanel =
                new AutomatonPanel(CAPanel.this.simulator, cjg);

            JDialog jf =
                new JDialog(CAPanel.this.simulator.getFrame(),
                    "Control Automaton");
            jf.add(autPanel);
            jf.setSize(600, 700);
            Point p = CAPanel.this.simulator.getFrame().getLocation();
            jf.setLocation(new Point(p.x + 50, p.y + 50));
            jf.setVisible(true);
            
            cjg.getLayouter().start(true);
            
        }
    }

}

class AutomatonPanel extends JGraphPanel<ControlJGraph> {
    /**
     * The constructor of this panel creates a panel with the Control Automaton
     * of the current grammar.
     * @param simulator
     */
    public AutomatonPanel(Simulator simulator, ControlJGraph graph) {
        super(graph, true, simulator.getOptions());
        this.getJGraph().setConnectable(false);
        this.getJGraph().setDisconnectable(false);
        this.getJGraph().setEnabled(true);
//        this.layouter = new MyForestLayouter().newInstance(this.getJGraph());
//        this.getJGraph().setLayouter(this.layouter);
        getJGraph().setToolTipEnabled(true);
    }

    @Override
    public ControlJModel getJModel() {
        return (ControlJModel) super.getJModel();
    }

    /**
     * A specialization of the forest layouter that takes the LTS start graph as
     * its suggested root.
     */
}

