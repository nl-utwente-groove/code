/*
 * GROOVE: GRaphs for Object Oriented VErification
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
 * $Id: ModelChecker.java,v 1.1.1.1 2007-03-20 10:05:17 kastenberg Exp $
 */
package groove.verify;

import groove.gui.Options;
import groove.io.ExtensionFilter;
import groove.io.GrooveFileChooser;
import groove.trans.GraphGrammar;
import groove.util.CommandLineTool;
import groove.util.ExprFormatException;
import groove.util.Groove;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Graphical application to direct the model checking functionality.
 * @author Harmen Kastenberg
 * @version $Revision: 1.1.1.1 $ $Date: 2007-03-20 10:05:17 $
 */
public class ModelChecker extends CommandLineTool {
    /** Name of the imager application. */
    static public final String APPLICATION_NAME = "Model Checker";

    /** Label for the browse buttons. */
    static public final String BROWSE_LABEL = "Browse...";

    /** Label for the browse buttons. */
    static public final String PARSE_LABEL = "Parse";

    /** The production system filter. */
    static private final ExtensionFilter gpsFilter = Groove.createRuleSystemFilter();

    /** The state filter. */
    static private final ExtensionFilter stateFilter = Groove.createStateFilter();

    /** An array of all filters identifying files that can be imaged. */
    static private final ExtensionFilter[] acceptFilters = new ExtensionFilter[] {
            gpsFilter, stateFilter};

    
    /**
     * Main method.
     * @param args the list of command-line arguments
     */
    public static void main(String[] args) {
        ModelChecker modelChecker;
        if (args.length == 0) {
            new ModelChecker(Collections.<String>emptyList(), true);
        } else {
            modelChecker = new ModelChecker(new LinkedList<String>(Arrays.asList(args)));
            modelChecker.processArguments();
            modelChecker.start();
        }
    }
    
    /**
     * Frame with fields for selecting input and output files and starting the imager.
     */
    public class ModelCheckerFrame extends JFrame {
        public ModelCheckerFrame() {
            super(APPLICATION_NAME);
            setIconImage(Groove.GROOVE_ICON_16x16.getImage());
            initComponents();
            initActions();
            setContentPane(createContentPane());
            setVerbosity(HIGH_VERBOSITY);
        }

        /**
         * Sets the name of the input file for the next imaging action.
         * @param fileName the new input file name
         */
        public void setInFile(String fileName) {
            grammarField.setText(fileName);
        }

        /**
         * Sets the name of the output file for the next imaging action.
         * @param fileName the new output file name
         */
        public void setOutFile(String fileName) {
            startStateField.setText(fileName);
        }

        public void handleListAtomicPropositions() {
        	assert (grammarLocation != null) : "The location of the GPS for which to list the atom. prop. should have been specified." ;
        	initGPS();
        }

        public void handleModelCheckAction() {
            File grammarLocation = new File(grammarField.getText());
            File startStateFile = null;
            if (startStateField.isEditable()) {
                startStateFile = new File(startStateField.getText());
            }
            if (grammarLocation.exists()) {
            	modelCheck(grammarLocation, startStateFile);
            }
            else {
                JOptionPane.showMessageDialog(this, "File " + grammarLocation + " does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        public void handleGrammarBrowseAction(JTextField fileField) {
            grammarBrowseChooser.setSelectedFile(new File(fileField.getText()));
            int answer = grammarBrowseChooser.showOpenDialog(this);
            if (answer == JFileChooser.APPROVE_OPTION) {
                fileField.setText(grammarBrowseChooser.getSelectedFile().getAbsolutePath());
            }
        }

        public void handleStartStateBrowseAction(JTextField fileField) {
            startStateBrowseChooser.setSelectedFile(new File(fileField.getText()));
            int answer = startStateBrowseChooser.showOpenDialog(this);
            if (answer == JFileChooser.APPROVE_OPTION) {
                fileField.setText(startStateBrowseChooser.getSelectedFile().getAbsolutePath());
            }
        }

        protected void initGPS() {
        	try {
        	if (startStateLocation == null) {
        		graphGrammar = Groove.loadGrammar(grammarLocation);
        	}
        	} catch (IOException ie) {
        		println(ie.getMessage());
        	}
        }

        /**
         * Writes a text to the logging area, followed by a new line.
         * @param text the line to be written
         */
        public void println(String text) {
            logArea.append(text + "\n");
            validate();
        }

        /**
         * Writes a text to the logging area.
         * @param text the text to be written
         */
        public void print(String text) {
            logArea.append(text);
            validate();
        }

        /**
         * Creates and returns a plain option pane on the basis of a given message panel and row of
         * buttons.
         * @param messagePane the central message pane
         * @param buttonRow the buttons to be displayed at the bottom of the pane
         */
        protected JOptionPane createOptionPane(JPanel messagePane, JButton[] buttonRow) {
            return new JOptionPane(messagePane, JOptionPane.PLAIN_MESSAGE,
                    JOptionPane.DEFAULT_OPTION, null, buttonRow);
        }

        /**
         * Creates and returns a content pane containing all GUI elements.
         */
        protected JComponent createContentPane() {
            // make central panel
            JPanel central = new JPanel();
            GridBagLayout layout = new GridBagLayout();
            central.setLayout(layout);
            GridBagConstraints constraint = new GridBagConstraints();
            constraint.ipadx = 1;
            constraint.ipady = 2;

            // first line: gps location
            constraint.gridwidth = 2;
            constraint.anchor = GridBagConstraints.LINE_START;
            central.add(new JLabel("GPS location"), constraint);

            constraint.fill = GridBagConstraints.BOTH;
            constraint.weightx = 1;
            constraint.anchor = GridBagConstraints.CENTER;
            constraint.gridwidth = 1;
            central.add(grammarField, constraint);

            constraint.fill = GridBagConstraints.NONE;
            constraint.weightx = 0;
            central.add(grammarBrowseButton, constraint);

            // second line: start state
            constraint.gridwidth = 1;
            constraint.weighty = 0;
            constraint.gridy = 1;
            constraint.anchor = GridBagConstraints.LINE_START;
            constraint.fill = GridBagConstraints.NONE;
            central.add(new JLabel("Start state"), constraint);

            central.add(startStateEnabler, constraint);

            constraint.fill = GridBagConstraints.BOTH;
            constraint.weightx = 1;
            constraint.anchor = GridBagConstraints.CENTER;
            central.add(startStateField, constraint);

            constraint.gridwidth = GridBagConstraints.REMAINDER;
            constraint.fill = GridBagConstraints.NONE;
            constraint.weightx = 0;
            central.add(startStateBrowseButton, constraint);

            // third line: start state
            constraint.gridy = 2;
            constraint.gridwidth = 1;
            constraint.weighty = 0;
            constraint.anchor = GridBagConstraints.LINE_START;
            constraint.fill = GridBagConstraints.NONE;
            central.add(new JLabel("Property"), constraint);

            logicGroup.add(ctlChooser);
            logicGroup.add(ltlChooser);

            constraint.gridy = 3;
            constraint.gridwidth = 1;
            constraint.weightx = 1;
            constraint.anchor = GridBagConstraints.LINE_START;
            central.add(ctlChooser, constraint);
            constraint.fill = GridBagConstraints.BOTH;
            constraint.weightx = 1;
            constraint.anchor = GridBagConstraints.CENTER;
            constraint.gridwidth = 2;
            central.add(ctlPropertyField, constraint);
            constraint.gridwidth = GridBagConstraints.REMAINDER;
            constraint.fill = GridBagConstraints.NONE;
            constraint.weightx = 0;
            central.add(ctlParseButton, constraint);

            constraint.gridy = 4;
            constraint.gridwidth = 1;
            constraint.weightx = 1;
            constraint.anchor = GridBagConstraints.LINE_START;
            central.add(ltlChooser, constraint);
            constraint.fill = GridBagConstraints.BOTH;
            constraint.weightx = 1;
            constraint.anchor = GridBagConstraints.CENTER;
            constraint.gridwidth = 2;
            central.add(ltlPropertyField, constraint);
            constraint.gridwidth = GridBagConstraints.REMAINDER;
            constraint.fill = GridBagConstraints.NONE;
            constraint.weightx = 0;
            central.add(ltlParseButton, constraint);

            
            constraint.gridx = GridBagConstraints.RELATIVE;
            constraint.gridwidth = 1;
            constraint.fill = GridBagConstraints.HORIZONTAL;
//            central.add(formatBox, constraint);

            // log area
            constraint.gridy = 5;
            constraint.anchor = GridBagConstraints.LINE_START;
            constraint.gridheight = 1;
            constraint.ipady = 9;
            constraint.gridwidth = GridBagConstraints.REMAINDER;
            constraint.weightx = 0;
            central.add(new JLabel("Messages"), constraint);

            constraint.gridy = GridBagConstraints.RELATIVE;
            constraint.weightx = 1;
            constraint.weighty = 1;
            constraint.fill = GridBagConstraints.BOTH;
            JScrollPane logPane = new JScrollPane(logArea);
            logPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            central.add(logPane, constraint);
            return createOptionPane(central, new JButton[] { showPropositionsButton, verifyButton, closeButton });
        }

        /**
         * Creates an action that calls {@link #handleGrammarBrowseAction(JTextField)} with a given text
         * field.
         */
        protected Action createGrammarBrowseAction(final JTextField fileField) {
            return new AbstractAction(BROWSE_LABEL) {
                public void actionPerformed(ActionEvent evt) {
                    handleGrammarBrowseAction(fileField);
                }
            };
        }

        /**
         * Creates an action that calls {@link #handleGrammarBrowseAction(JTextField)} with a given text
         * field.
         */
        protected Action createStartStateBrowseAction(final JTextField fileField) {
            return new AbstractAction(BROWSE_LABEL) {
                public void actionPerformed(ActionEvent evt) {
                    handleStartStateBrowseAction(fileField);
                }
            };
        }

        protected void initComponents() {
            // outFileField.setBorder(BorderFactory.createEtchedBorder());
            setInFile(Groove.WORKING_DIR);
            setOutFile(Groove.WORKING_DIR);
            grammarField.setPreferredSize(new Dimension(300, 0));
            startStateField.setEditable(false);
            ctlChooser.setSelected(true);
            ltlPropertyField.setEditable(false);
            ltlParseButton.setEnabled(false);
            logArea.setEditable(false);
            logArea.setRows(10);
            // outFileField.setEnabled(false);
            grammarBrowseChooser.setCurrentDirectory(new File(Groove.WORKING_DIR));
            grammarBrowseChooser.addChoosableFileFilter(gpsFilter);
            grammarBrowseChooser.setFileFilter(gpsFilter);
            startStateBrowseChooser.setCurrentDirectory(new File(Groove.WORKING_DIR));
            startStateBrowseChooser.addChoosableFileFilter(stateFilter);
            startStateBrowseChooser.setFileFilter(stateFilter);
        }

        protected void initActions() {
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            Action closeAction = new AbstractAction(Options.CLOSE_ACTION_NAME) {
                public void actionPerformed(ActionEvent evt) {
                    System.exit(0);
                }
            };
            Action listPropositionsAction = new AbstractAction(Options.LIST_ATOMIC_PROPOSITIONS_ACTION_NAME) {
                public void actionPerformed(ActionEvent evt) {
                	if (grammarLocation == null) {
                		println("You first have to select a graph production system.");
                	}
                	handleListAtomicPropositions();
                }
            };

            Action modelCheckAction = new AbstractAction(Options.MODEL_CHECK_ACTION_NAME) {
                public void actionPerformed(ActionEvent evt) {
                    // imageButton.setEnabled(false);
                    // new Thread() {
                    // public void run() {
                	handleModelCheckAction();
//                    handleImageAction();
                    // imageButton.setEnabled(true);
                    // }
                    // }.start();
                }
            };

            Action parseAction = new AbstractAction(Options.PARSE_ACTION_NAME) {
            	public void actionPerformed(ActionEvent evt) {
            		if ((evt.getSource() == ctlParseButton) && !(ctlPropertyField.getText().equals(""))) {
            			setProperty(ctlPropertyField.getText());
            		} else if ((evt.getSource() == ltlParseButton) && !(ltlPropertyField.getText().equals(""))) {
            			setProperty(ltlPropertyField.getText());
            		}
            		if (property != null) {
            			println("Property set to: " + property);
            		}
            	}
            };

            ItemListener enableItemListener = new ItemListener() {
                public void itemStateChanged(ItemEvent evt) {
                    startStateField.setEditable(startStateEnabler.isSelected());
                    startStateBrowseButton.setEnabled(startStateEnabler.isSelected());
                }
            };

            ActionListener logicChooserListener = new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                	if (evt.getSource() == ctlChooser) {
                		LOGIC = CTL;
                		ctlPropertyField.setEditable(true);
                		ctlParseButton.setEnabled(true);
                		ltlPropertyField.setEditable(false);
                		ltlParseButton.setEnabled(false);
                		
                	} else if (evt.getSource() == ltlChooser) {
                		LOGIC = LTL;
                		ltlPropertyField.setEditable(true);
                		ltlParseButton.setEnabled(true);
                		ctlPropertyField.setEditable(false);
                		ctlParseButton.setEnabled(false);
                	}
                }
            };

            closeButton.setAction(closeAction);
            showPropositionsButton.setAction(listPropositionsAction);
            verifyButton.setAction(modelCheckAction);
            startStateEnabler.addItemListener(enableItemListener);
            ctlChooser.addActionListener(logicChooserListener);
            ctlParseButton.addActionListener(parseAction);
            ltlChooser.addActionListener(logicChooserListener);
            ltlParseButton.addActionListener(parseAction);
            grammarBrowseButton.setAction(createGrammarBrowseAction(grammarField));
            startStateBrowseButton.setAction(createStartStateBrowseAction(startStateField));
            startStateBrowseButton.setEnabled(false);
        }

        /** Textfield to contain the name of the input file. */
        private final JTextField grammarField = new JTextField();

        /** Textfield to contain the name of the output file. */
        private final JTextField startStateField = new JTextField();

        /** Textfield to contain the CTL-property to be checked for. */
        private final JTextField ctlPropertyField = new JTextField();

        /** Textfield to contain the LTL-property to be checked for. */
        private final JTextField ltlPropertyField = new JTextField();

        /** Button to browse for the input file. */
        private final JButton grammarBrowseButton = new JButton(BROWSE_LABEL);

        /** Button to browse for the output file. */
        private final JButton startStateBrowseButton = new JButton(BROWSE_LABEL);

        /** Button to parse the ctl formula. */
        private final JButton ctlParseButton = new JButton(PARSE_LABEL);

        /** Button to parse for ltl formula. */
        private final JButton ltlParseButton = new JButton(PARSE_LABEL);

        /** Button to start the imaging. */
        private final JButton showPropositionsButton = new JButton(Options.LIST_ATOMIC_PROPOSITIONS_ACTION_NAME);

        /** Button to start the imaging. */
        private final JButton verifyButton = new JButton(Options.MODEL_CHECK_ACTION_NAME);

        /** Button to close the imager. */
        private final JButton closeButton = new JButton(Options.CLOSE_ACTION_NAME);

        /** Checkbox to enable the out file. */
        private final JCheckBox startStateEnabler = new JCheckBox();

        /** Group of two radio buttons to choose the logic. */
        private final ButtonGroup logicGroup = new ButtonGroup();

        /** RadioButton to choose the CTL logic. */
        private final JRadioButton ctlChooser = new JRadioButton("CTL");

        /** RadioButton to choose the LTL logic. */
        private final JRadioButton ltlChooser = new JRadioButton("LTL");

        /** File chooser for the grammar browse actions. */
        private final JFileChooser grammarBrowseChooser = new GrooveFileChooser();

        /** File chooser for the grammar browse actions. */
        private final JFileChooser startStateBrowseChooser = new GrooveFileChooser();

        /** File chooser for the browse actions. */
        private final JTextArea logArea = new JTextArea();

        /** Combo box for the available image formats. */
//        private final JComboBox formatBox = new JComboBox(FORMATS);
    }

    /**
     * Constructs a new, command-line ModelChecker.
     */
    public ModelChecker(List<String> args) {
        this(args, false);
    }
    
    /**
     * Constructs a new modelChecker, which may be GUI-based or command-line.
     * @param gui <tt>true</tt> if the imager should be GUI-based.
     */
    public ModelChecker(List<String> args, boolean gui) {
        super(args);
        if (gui) {
            imagerFrame = new ModelCheckerFrame();
            imagerFrame.pack();
            imagerFrame.setVisible(true);
        } else {
            imagerFrame = null;
//            addOption(new FormatOption(this));
        }
    }
    
    /**
     * Does the actual conversion work.
     * @require <tt>getLocation() != null</tt>
     */
    public void start() {
    	File grammarLocation = new File(getGrammarLocation());
//        File inFile = getInFile();
//        File outFile = getOutFile();
        if (!grammarLocation.exists()) {
            println("Graph production system " + grammarLocation + " does not exist");
        } else {
        	try {
        	graphGrammar = Groove.loadGrammar(getGrammarLocation());
        	} catch (IOException ie) {
        		println(ie.getMessage());
        	}
//        	property.mark(marker, marking, gts);
//            makeImage(inFile, outFile);
        }
//        jframe.dispose();
    }

    /**
     * Makes an image file from the specified input file. If the input file is a directory, the
     * method descends recursively. The types of input files recognized are: gxl, gps and gst
     * @param inFile the input file to be converted
     * @param outFile the intended output file
     */
//    public void makeImage(File inFile, File outFile) {
//        // if the given input-file is a directory, call this method recursively for each file it contains
//        // but ensure:
//        // --> output-file exists or can be created
//        if (inFile.isDirectory()) {
//            if (outFile.exists() || outFile.mkdir()) {
//                File[] files = inFile.listFiles();
//                for (int i = 0; i < files.length; i++) {
//                    makeImage(files[i], new File(outFile, files[i].getName()));
//                }
//            } else {
//                println("Directory " + outFile + " could not be created");                
//            }
//        }
//        // or the input-file is an ordinary Groove-file (state or rule)
//        // here ensure:
//        // --> output-file exists and will be overwritten or the directory in which
//        //     it will be placed exists or can be created
//        else {
//            if (!outFile.getParentFile().exists() && !outFile.getParentFile().mkdir()) {
//                JOptionPane.showMessageDialog(null, "Output file does not exist and directory can not be created.");
//                return;
//            }
//
//            ExtensionFilter acceptingFilter = accept(inFile);
//            if (acceptingFilter != null) {
//                try {
//                    String outFileName = acceptingFilter.stripExtension(outFile.getPath());
//                    outFile = new File(new ExtensionFilter("."+imageFormat).addExtension(outFileName));
//                    groove.graph.Graph graph = graphLoader.unmarshal(inFile);
//                    JModel model;
//                    if (acceptingFilter == ruleFilter) {
//                        String ruleName = ruleFilter.stripExtension(inFile.getName());
//                        RuleGraph rule = new RuleGraph(graph, new NameLabel(ruleName));
//                        model = new RuleJModel(rule);
//                    } else {
//                        model = new GraphJModel(graph);
//                    }
//                    JGraph jGraph = new JGraph(model);
//                    jGraph.setModel(model);
//                    jGraph.setSize(jGraph.getPreferredSize());
////                    jframe.pack();
//                    printlnMedium("Imaging "+inFile+" as "+outFile);
//                    if (! ImageIO.write(jGraph.toImage(), imageFormat, outFile)) {
//                        println("No writer found for "+outFile);
//                        outFile.delete();
//                    }
//                    Thread.yield();
//                } catch (GraphFormatException e1) {
//                    println("Problem in rule format of " + inFile);
//                    return;
//                } catch (XmlException e) {
//                    println("Problem in XML format of " + inFile);
//                    return;
//                } catch (FileNotFoundException fnfe){
//                    println("File " + outFile + "does not exist.");
//                } catch (IOException e) {
//                    println("Problem reading " + inFile);
//                    return;
//                }
//            }
//        }
//    }

    public void modelCheck(File grammarLocation, File startStateFile) {
    	println("Model Checking " + grammarLocation + " for property " + property);
    }

    /**
     * Determines if a given file is recognized by any of the filters regocnized by this
     * <tt>Imager</tt>. In this implementation, these are the {@link Groove}gxl filter, state filter
     * or rule filter.
     * @param file the file to be tested for acceptance
     * @return a filter that accepts <tt>file</tt>, or <tt>null</tt>.
     * @see Groove#createGxlFilter()
     * @see Groove#createStateFilter()
     * @see Groove#createRuleFilter()
     */
    public ExtensionFilter accept(File file) {
        for (int i = 0; i < acceptFilters.length; i++) {
            if (acceptFilters[i].accept(file)) {
                return acceptFilters[i];
            }
        }
        return null;
    }

//    /**
//     * Logs a line of text, either to the standard output
//     * or (if the invocation is gui-based) to the {@link ImagerFrame}.
//     */
//    public void log(String text) {
//        if (imagerFrame == null) {
//            System.out.println(text);
//        } else {
//            imagerFrame.println(text);
//        }
//    }

    /** Returns the location of the file(s) to be imaged. */
//    public File getInFile() {
//		return inFile;
//    }

    /**
     * Returns the intended location for the image file(s).
     */
//    public File getOutFile() {
//		return outFile;
//    }

    public CTLStarFormula getFormulaFactory() {
    	return formulaFactory;
    }

    /**
     * Returns the File of the graph production system to be model checked.
     * @return the {@link #grammarLocation}-value
     */
    public String getGrammarLocation() {
    	return grammarLocation;
    }

    public void setProperty(String property) {
    	try {
    		if (LOGIC == CTL) {
    			this.property = CTLFormula.parseFormula(property);
    		} else if (LOGIC == LTL) {
    			this.property = LTLFormula.parseFormula(property);
    		} else {
    			println("Select one of the available logics.");
    		}
    	} catch (ExprFormatException efe) {
    		print("Error in property format:" + efe.getMessage());
    	}
    }

    /**
     * Sets the location of the graph production system to be model checked.
     * @param fileName the new value for {@link #grammarLocation}
     */
    public void setGrammarLocation(String fileName) {
    	this.grammarLocation = fileName;
    }

    /**
     * Sets the location where to look for the files to be imaged. No check is done if the location
     * actually exists.
     * @param fileName the name of the files to be imaged
     * @ensure <tt>getOutFile().getName().equals(fileName)</tt>
     */
//    public void setInFile(String fileName) {
//        inFile = new File(fileName);
//    }

    /**
     * Sets the location where to store the image file. No check is done if the location actually
     * exists.
     * @param outFileName The name of the outFile to set
     */
//    public void setOutFile(String outFileName) {
//        this.outFile = new File(outFileName);
//    }
    
    /**
     * Returns the image format to which the graphs will be converted. 
     */
//    public String getImageFormat() {
//        return imageFormat;
//    }
    
    /**
     * Sets the image format to which the graphs will be converted. 
     */
//    public void setImageFormat(String imageFormat) {
//        this.imageFormat = imageFormat;
//    }
    
    /**
     * Processes a list of arguments (which are <tt>String</tt>s) by setting the attributes of
     * the imager accordingly.
     * @param argsList the list of arguents
     * @require <tt>argsList instanceof List of String</tt>
     */
    protected void processArguments() {
        super.processArguments();
        List<String> argsList = getArgs();
        if (argsList.size() > 0) {
        	setGrammarLocation(argsList.get(0));
            argsList.remove(0);
        }
        if (argsList.size() > 0) {
        	setProperty(argsList.get(0));
            argsList.remove(0);
        }
        if (argsList.size() > 0) {
            printError("Invalid number of arguments");
        }
    }

    protected String getUsageMessage() {
        return "Usage: ModelChecker [options] grammar-location property";
    }

    /**
     * This tool does not support logging.
     */
    protected boolean supportsLogOption() {
        return false;
    }
    
    /**
     * This tool does not support output file specification through an option.
     */
    protected boolean supportsOutputOption() {
        return false;
    }
    
    /** Overwrites the method to write to the system output or to the GUI. */
    protected void print(String text) {
        if (imagerFrame == null) {
            super.print(text);
        } else {
            imagerFrame.print(text);
        }
    }
    
    /** Overwrites the method to write to the system output or to the GUI. */
    protected void println() {
        if (imagerFrame == null) {
            super.println();
        } else {
            imagerFrame.println("");
        }
    }
    
    /** Overwrites the method to write to the system output or to the GUI. */
    protected void println(String text) {
        if (imagerFrame == null) {
            super.println(text);
        } else {
            imagerFrame.println(text);
        }
    }

    /** The imager frame if the invocation is gui-based; <tt>null</tt> if it is command-line based. */
    private final ModelCheckerFrame imagerFrame;
    /** The graph grammar to be model checked. */
    private GraphGrammar graphGrammar;
    /** The location of the graph grammar to be model checked. */
    private String grammarLocation;
    /** The location of the start state. */
    private String startStateLocation;
    /** The factory for creating a temporal formula from a string input. */
    private CTLStarFormula formulaFactory;
    /** The property to be checked for. */
    private TemporalFormula property;
    /** The id for the CTL logic. */
    private int CTL = 1;
    /** The id for the LTL logic. */
    private int LTL = 2;
    /** The logic to use for model checking (default {@link #CTL}). */
    private int LOGIC = CTL;
}
