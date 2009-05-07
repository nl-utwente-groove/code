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
package groove.gui.chscenar;

import groove.explore.Scenario;
import groove.explore.result.Result;
import groove.io.ExtensionFilter;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.trans.GraphGrammar;

import groove.util.Groove;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import groove.lts.LTSGraph;

import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/** Proposes a main frame which proposes Generator
 * functionality after choosing a scenario.
 * @author Iovka Boneva
 * @version $Revision $
 */
public class ScenarioChooser {

    // -------------------------------------------------------
    // FUNTIONALITY
    // -------------------------------------------------------
    /** Generates the LTS and stores the results.
     * @return <code>true</code> if everything went well, false otherwise
     * In this implementation, if an I/O error is met when storing the results,
     * then all the computations are forgotten.
     */
    private boolean doGenerate() {
        // Collect all the necessary components
        GraphGrammar grammar = getScPanel().getSelectedGrammar();
        if (grammar == null) {
            showErrorDialog("Please load a grammar first");
            return false;
        }

        Scenario scenario = getScPanel().getSelectedScenario();
        if (scenario == null) {
            showErrorDialog("Please construct a scenario first");
            return false;
        }

        File resultDir = new File(getScPanel().getResultsFolderName());
        if (!resultDir.exists() || !resultDir.isDirectory()) {
            showErrorDialog("Result directory does not exist: " + resultDir);
            return false;
        }


        // Configure the scenario
        GTS gts = new GTS(grammar);
        scenario.prepare(gts, gts.startState());
        scenario.play();
        

        // Store the results
        ExtensionFilter gxlFilter = Groove.createGxlFilter();
        String ltsFileName = resultDir.getAbsolutePath() + File.separator + getScPanel().getLtsFileNamePrefix();
        ltsFileName = gxlFilter.addExtension(ltsFileName);

        try {
            Groove.saveGraph(new LTSGraph(gts), ltsFileName);
        } catch (IOException ex) {
            showErrorDialog("Result could not be saved due to I/O error.\n" + ex.getMessage());
            return false;
        }
        
        Result computedResult = (Result) scenario.getResult();
        // For now, all results are composed of graph states
        ExtensionFilter gstFilter = Groove.createStateFilter();
        String outFileNameBase = resultDir.getPath() + File.separator+ getScPanel().getResultFileNamePrefix() + "-";
        for (Object o : computedResult.getValue()) {
            try {
                GraphState r = (GraphState) o;
                String outFileName = outFileNameBase + r.toString();
                outFileName = gstFilter.addExtension(outFileName);
                Groove.saveGraph(r.getGraph(), outFileName);
            } catch (IOException ex) {
                showErrorDialog("Result could not be saved due to I/O error.\n" + ex.getMessage());
                return false;
            } 
        }
        return true;

    }

	// -------------------------------------------------------
	// GUI RELATED METHODS
	// -------------------------------------------------------
	
	private final static String APPLICATION_NAME = "Scenario chooser";
	private final static String GENERATE_ACTION_NAME = "Generate";

	private JFrame getMainFrame () {
		createMainFrame();
		return mainFrame;
	}
	
	/** Creates and initialises the main frame. */
	private void createMainFrame() {
		if (this.mainFrame != null) {
			return;
		}
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new FlowLayout());
		buttonsPanel.add(getGenerateButton());
		buttonsPanel.add(getCloseButton());
		
		mainFrame = new JFrame(APPLICATION_NAME);
		mainFrame.setIconImage(Groove.GROOVE_ICON_16x16.getImage());
		
		mainFrame.getContentPane().setLayout(new BorderLayout());
		mainFrame.getContentPane().add(getScPanel(), BorderLayout.CENTER);
		mainFrame.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
		
	}
	
	/** Displays the frame of the program. */
	private void showGui () {
		getMainFrame().pack();
		getMainFrame().setVisible(true);
	}
	
	/** Lazily creates the scenario chooser panel. */
	private ScenarioChooserPanel getScPanel () {
		if (this.scPanel == null) {
			this.scPanel = new ScenarioChooserPanel(getMainFrame());
		}
		return scPanel;
	}
	
	/** Lazily creates the generate button. */
	private JButton getGenerateButton () {
		if (this.generateButton == null) {
			generateButton = new JButton(GENERATE_ACTION_NAME);
			generateButton.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e) {
					if (doGenerate()) {
                                            showInfoDialog("Results saved.");
                                        }
				}

			});
		}
		return generateButton;
	}
	
	private JButton getCloseButton () {
		if (this.closeButton == null) {
			closeButton = new JButton("Close");
			closeButton.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e) {
					// Is this what I have to do ?
					getMainFrame().dispose();
					System.exit(0);
				}

			});
		}
		return closeButton;
	}
	
	private void showErrorDialog (String message) {
        JOptionPane.showMessageDialog(getMainFrame(),
                    message,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
    }
	private void showInfoDialog (String message) {
            JOptionPane.showMessageDialog(getMainFrame(),
                    message,
                    "Information",
                    JOptionPane.PLAIN_MESSAGE);
        }
        
        
	// -------------------------------------------------------
	// FIELDS
	// -------------------------------------------------------
	private JFrame mainFrame;
	private ScenarioChooserPanel scPanel;
	private JButton generateButton;
	private JButton closeButton;
	
	// -------------------------------------------------------
	// MAIN METHOD
	// -------------------------------------------------------
	public static void main (String args[]) {
		ScenarioChooser sc = new ScenarioChooser();
		sc.showGui();
	}
}
