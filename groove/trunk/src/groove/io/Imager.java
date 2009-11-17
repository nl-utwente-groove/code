// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id: Imager.java,v 1.17 2008-01-09 16:21:36 rensink Exp $
 */
package groove.io;

import groove.graph.GraphShape;
import groove.gui.Exporter;
import groove.gui.Options;
import groove.gui.jgraph.AspectJModel;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.JGraph;
import groove.gui.jgraph.JModel;
import groove.util.CommandLineOption;
import groove.util.CommandLineTool;
import groove.util.Groove;
import groove.view.AspectualRuleView;
import groove.view.aspect.AspectGraph;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

/**
 * Application to create jpeg or gif files for a state or rule graph, or a
 * directory of them.
 * @author Arend Rensink
 * @version $Revision$
 */
public class Imager extends CommandLineTool {
    /**
     * Constructs a new, command-line Imager.
     */
    public Imager(List<String> args) {
        this(args, false);
    }

    /**
     * Constructs a new imager, which may be GUI-based or command-line.
     * @param gui <tt>true</tt> if the imager should be GUI-based.
     */
    public Imager(List<String> args, boolean gui) {
        super(args);
        // force the LAF to be set
        groove.gui.Options.initLookAndFeel();
        if (gui) {
            this.imagerFrame = new ImagerFrame();
            this.imagerFrame.pack();
            this.imagerFrame.setVisible(true);
        } else {
            this.imagerFrame = null;
            addOption(new FormatOption(this));
        }
    }

    /**
     * Does the actual conversion work.
     * @require <tt>getLocation() != null</tt>
     */
    public void start() {
        File inFile = getInFile();
        File outFile = getOutFile();
        if (!inFile.exists()) {
            println("Input file " + inFile + " does not exist");
        } else if (outFile == null) {
            makeImage(inFile, inFile);
        } else {
            makeImage(inFile, outFile);
        }
        // jframe.dispose();
    }

    /**
     * Makes an image file from the specified input file. If the input file is a
     * directory, the method descends recursively. The types of input files
     * recognized are: gxl, gps and gst
     * @param inFile the input file to be converted
     * @param outFile the intended output file
     */
    public void makeImage(File inFile, File outFile) {
        // if the given input-file is a directory, call this method recursively
        // for each file it contains
        // but ensure:
        // --> output-file exists or can be created
        if (inFile.isDirectory()) {
            File[] files = inFile.listFiles();
            if (outFile.exists() || outFile.mkdir()) {
                for (File element : files) {
                    makeImage(element, new File(outFile, element.getName()));
                }
            } else {
                println("Directory " + outFile + " could not be created");
            }
        }
        // or the input-file is an ordinary Groove-file (state or rule)
        // here ensure:
        // --> output-file exists and will be overwritten or the directory in
        // which
        // it will be placed exists or can be created
        else {
            if (outFile.getParentFile() != null
                && !outFile.getParentFile().exists()
                && !outFile.getParentFile().mkdir()) {
                JOptionPane.showMessageDialog(null,
                    "Output file does not exist and directory can not be created.");
                return;
            }

            ExtensionFilter acceptingFilter = accept(inFile);
            if (acceptingFilter != null) {
                try {
                    String outFileName =
                        acceptingFilter.stripExtension(outFile.getPath());
                    outFile =
                        new File(
                            new ExtensionFilter(this.imageFormat).addExtension(outFileName));
                    GraphShape graph = graphLoader.unmarshalGraph(inFile);

                    if (graph.size() == 0) {
                        // fix to skip empty graphs and rules, since
                        // they cause a nullpointer
                        printlnMedium("Skpping empty graph " + inFile);
                        return;
                    }

                    JModel model;
                    if (acceptingFilter == ruleFilter) {
                        AspectualRuleView rule =
                            AspectGraph.newInstance(graph).toRuleView(null);
                        model = AspectJModel.newInstance(rule, new Options());
                    } else {
                        model = GraphJModel.newInstance(graph, new Options());
                    }

                    JGraph jGraph = new JGraph(model, false, null);
                    jGraph.setModel(model);
                    // Ugly hack to prevent clipping of the image. We set the jGraph size
                    // to twice its normal size. This does not affect the final size of
                    // the exported figure, hence it can be considered harmless... ;P
                    Dimension oldPrefSize = jGraph.getPreferredSize();
                    Dimension newPrefSize = new Dimension(oldPrefSize.width * 2,
                                                          oldPrefSize.height * 2);
                    jGraph.setSize(newPrefSize);
                    printlnMedium("Imaging " + inFile + " as " + outFile);

                    this.exporter.export(jGraph, outFile);
                    Thread.yield();
                } catch (FileNotFoundException fnfe) {
                    println("File " + outFile + "does not exist.");
                } catch (IOException e) {
                    println("Problem reading " + inFile);
                    return;
                }
            }
        }
    }

    /**
     * Determines if a given file is recognized by any of the filters regocnized
     * by this <tt>Imager</tt>. In this implementation, these are the
     * {@link Groove}gxl filter, state filter or rule filter.
     * @param file the file to be tested for acceptance
     * @return a filter that accepts <tt>file</tt>, or <tt>null</tt>.
     * @see Groove#createGxlFilter()
     * @see Groove#createStateFilter()
     * @see Groove#createRuleFilter()
     */
    public ExtensionFilter accept(File file) {
        for (ExtensionFilter element : acceptFilters) {
            if (element.accept(file)) {
                return element;
            }
        }
        return null;
    }

    /** Returns the location of the file(s) to be imaged. */
    public File getInFile() {
        return this.inFile;
    }

    /**
     * Returns the intended location for the image file(s).
     */
    public File getOutFile() {
        return this.outFile;
    }

    /**
     * Sets the location where to look for the files to be imaged. No check is
     * done if the location actually exists.
     * @param fileName the name of the files to be imaged
     * @ensure <tt>getOutFile().getName().equals(fileName)</tt>
     */
    public void setInFile(String fileName) {
        this.inFile = new File(fileName);
    }

    /**
     * Sets the location where to store the image file. No check is done if the
     * location actually exists.
     * @param outFileName The name of the outFile to set
     */
    public void setOutFile(String outFileName) {
        this.outFile = new File(outFileName);
    }

    /**
     * Returns the image format to which the graphs will be converted.
     */
    public String getImageFormat() {
        return this.imageFormat;
    }

    /**
     * Sets the image format to which the graphs will be converted.
     */
    public void setImageFormat(String imageFormat) {
        this.imageFormat = imageFormat;
    }

    /**
     * Processes a list of arguments (which are <tt>String</tt>s) by setting the
     * attributes of the imager accordingly.
     * @require <tt>argsList instanceof List of String</tt>
     */
    @Override
    protected void processArguments() {
        super.processArguments();
        List<String> argsList = getArgs();
        if (argsList.size() > 0) {
            setInFile(argsList.get(0));
            argsList.remove(0);
        }
        if (argsList.size() > 0) {
            setOutFile(argsList.get(0));
            argsList.remove(0);
        }
        if (argsList.size() > 0) {
            printError("Invalid number of arguments");
        }
    }

    @Override
    protected String getUsageMessage() {
        return "Usage: Imager [options] filename [outlocation]";
    }

    /**
     * This tool does not support logging.
     */
    @Override
    protected boolean supportsLogOption() {
        return false;
    }

    /**
     * This tool does not support output file specification through an option.
     */
    @Override
    protected boolean supportsOutputOption() {
        return false;
    }

    /** Overwrites the method to write to the system output or to the GUI. */
    @Override
    protected void print(String text) {
        if (this.imagerFrame == null) {
            super.print(text);
        } else {
            this.imagerFrame.print(text);
        }
    }

    /** Overwrites the method to write to the system output or to the GUI. */
    @Override
    protected void println() {
        if (this.imagerFrame == null) {
            super.println();
        } else {
            this.imagerFrame.println("");
        }
    }

    /** Overwrites the method to write to the system output or to the GUI. */
    @Override
    protected void println(String text) {
        if (this.imagerFrame == null) {
            super.println(text);
        } else {
            this.imagerFrame.println(text);
        }
    }

    /** The image exporter used. */
    final Exporter exporter = new Exporter();
    /** Name of the image format to which the imager converts. */
    private String imageFormat =
        this.exporter.getDefaultFormat().getFilter().getExtension();
    /**
     * The imager frame if the invocation is gui-based; <tt>null</tt> if it is
     * command-line based.
     */
    private final ImagerFrame imagerFrame;
    /** The location of the file(s) to be imaged. */
    private File inFile;
    /** The intended location of the image file(s). */
    private File outFile;

    /** Starts the imager with a list of options and file names. */
    public static void main(String[] args) {
        Imager imager;
        if (args.length == 0) {
            new Imager(Collections.<String>emptyList(), true);
        } else {
            imager = new Imager(new LinkedList<String>(Arrays.asList(args)));
            imager.processArguments();
            imager.start();
        }
    }

    /** Name of the imager application. */
    static public final String APPLICATION_NAME = "Imager";
    // /** Name of the png (Portable Network Graphic) image format. */
    // static public final String PNG_FORMAT = "png";
    // /** Name of the jpeg image format. */
    // static public final String JPG_FORMAT = "jpg";
    // /** Name of the eps image format. */
    // static public final String EPS_FORMAT = "eps";
    //
    // /** The default format of the imager. */
    // static public final String DEFAULT_FORMAT = PNG_FORMAT;
    // /** List of all supported image formats. */
    // static public final String[] FORMATS = new String[] { JPG_FORMAT,
    // PNG_FORMAT, EPS_FORMAT} ;
    /** Label for the browse buttons. */
    static public final String BROWSE_LABEL = "Browse...";

    /** The loader used for the xml files. */
    static final Xml<?> graphLoader = new LayedOutXml();

    /** The rule filter. */
    static final ExtensionFilter ruleFilter = Groove.createRuleFilter();

    /** The state filter. */
    static final ExtensionFilter stateFilter = Groove.createStateFilter();

    /** The gxl filter. */
    static final ExtensionFilter gxlFilter = Groove.createGxlFilter();

    /** The production system filter. */
    static final ExtensionFilter gpsFilter = Groove.createRuleSystemFilter();

    /** An array of all filters identifying files that can be imaged. */
    static final ExtensionFilter[] acceptFilters =
        new ExtensionFilter[] {gpsFilter, ruleFilter, stateFilter, gxlFilter};

    /**
     * Option to set the output format for the imager.
     */
    static public class FormatOption implements CommandLineOption {
        /** Abbreviation of the format option. */
        static public final String NAME = "f";
        /** Short description of the format option. */
        static public final String DESCRIPTION =
            "Output format extension. Supported formats are:";
        /** File suffix for the default format. */
        static public final String DEFAULT_SUFFIX = " (default)";
        /** Option parameter name. */
        static public final String PARAMETER_NAME = "name";

        /** Constructs a command-line format option working on a given imager. */
        public FormatOption(Imager imager) {
            this.imager = imager;
            this.exporter = imager.exporter;
        }

        public String getName() {
            return NAME;
        }

        public String[] getDescription() {
            List<String> result = new LinkedList<String>();
            result.add(DESCRIPTION);
            for (String formatName : this.exporter.getExtensions()) {
                String format = "* " + formatName;
                if (format.equals(this.exporter.getDefaultFormat().getFilter().getExtension())) {
                    format += DEFAULT_SUFFIX;
                }
                result.add(format);
            }
            return result.toArray(new String[0]);
        }

        public String getParameterName() {
            return PARAMETER_NAME;
        }

        public boolean hasParameter() {
            return true;
        }

        /**
         * Changes the current output format of the imager, if the parameter is
         * a valid format name.
         */
        public void parse(String parameter) {
            String extension = ExtensionFilter.SEPARATOR + parameter;
            // first check if parameter is a valid format name
            if (!this.exporter.getExtensions().contains(extension)) {
                throw new IllegalArgumentException("Unknown format: "
                    + parameter);
            }
            this.imager.setImageFormat(extension);
        }

        private final Imager imager;
        /** The exporter used by {@link #imager}. */
        private final Exporter exporter;
    }

    /**
     * Frame with fields for selecting input and output files and starting the
     * imager.
     */
    public class ImagerFrame extends JFrame {
        /** Constructs an instanceof the frame, with GUI components set. */
        public ImagerFrame() {
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
            this.inFileField.setText(fileName);
        }

        /**
         * Sets the name of the output file for the next imaging action.
         * @param fileName the new output file name
         */
        public void setOutFile(String fileName) {
            this.outFileField.setText(fileName);
        }

        /**
         * Images the file named in {@link #inFileField}, and saves the result
         * to the file named in {@link #outFileField}.
         */
        public void handleImageAction() {
            File inFile = new File(this.inFileField.getText());
            File outFile;
            if (this.outFileField.isEditable()) {
                outFile = new File(this.outFileField.getText());
            } else {
                outFile = inFile;
            }
            if (inFile.exists()) {
                makeImage(inFile, outFile);
            } else {
                JOptionPane.showMessageDialog(this, "File " + inFile
                    + " does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        /**
         * Starts a file chooser and sets the selected file name in a given text
         * field.
         */
        public void handleBrowseAction(JTextField fileField) {
            this.browseChooser.setSelectedFile(new File(fileField.getText()));
            int answer = this.browseChooser.showOpenDialog(this);
            if (answer == JFileChooser.APPROVE_OPTION) {
                fileField.setText(this.browseChooser.getSelectedFile().getAbsolutePath());
            }
        }

        /**
         * Writes a text to the logging area, followed by a new line.
         * @param text the line to be written
         */
        public void println(String text) {
            this.logArea.append(text + "\n");
            validate();
        }

        /**
         * Writes a text to the logging area.
         * @param text the text to be written
         */
        public void print(String text) {
            this.logArea.append(text);
            validate();
        }

        /**
         * Creates and returns a plain option pane on the basis of a given
         * message panel and row of buttons.
         * @param messagePane the central message pane
         * @param buttonRow the buttons to be displayed at the bottom of the
         *        pane
         */
        protected JOptionPane createOptionPane(JPanel messagePane,
                JButton[] buttonRow) {
            return new JOptionPane(messagePane, JOptionPane.PLAIN_MESSAGE,
                JOptionPane.DEFAULT_OPTION, null, buttonRow);
        }

        /**
         * Creates and returns a content pane containing all GUI elements.
         */
        protected JComponent createContentPane() {
            // make format chooser panel
            this.formatBox.setSelectedIndex(1);
            this.formatBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    setImageFormat((String) ImagerFrame.this.formatBox.getSelectedItem());
                }
            });
            // make central panel
            JPanel central = new JPanel(new GridBagLayout(), false);
            GridBagConstraints constraint = new GridBagConstraints();
            constraint.ipadx = 1;
            constraint.ipady = 2;

            // first line: input file
            constraint.gridwidth = 2;
            constraint.anchor = GridBagConstraints.LINE_START;
            central.add(new JLabel("Input filename"), constraint);

            constraint.fill = GridBagConstraints.BOTH;
            constraint.weightx = 1;
            constraint.anchor = GridBagConstraints.CENTER;
            constraint.gridwidth = 1;
            central.add(this.inFileField, constraint);

            constraint.fill = GridBagConstraints.NONE;
            constraint.weightx = 0;
            central.add(this.inFileBrowseButton, constraint);

            // second line: output file name
            constraint.gridwidth = 1;
            constraint.weighty = 0;
            constraint.gridy = 1;
            constraint.anchor = GridBagConstraints.LINE_START;
            constraint.fill = GridBagConstraints.NONE;
            central.add(new JLabel("Output filename"), constraint);

            central.add(this.outFileEnabler, constraint);

            constraint.fill = GridBagConstraints.BOTH;
            constraint.weightx = 1;
            constraint.anchor = GridBagConstraints.CENTER;
            central.add(this.outFileField, constraint);

            constraint.gridwidth = GridBagConstraints.REMAINDER;
            constraint.fill = GridBagConstraints.NONE;
            constraint.weightx = 0;
            central.add(this.outFileBrowseButton, constraint);

            // third line: image format
            constraint.anchor = GridBagConstraints.LINE_START;
            constraint.gridx = 0;
            constraint.gridy = 2;
            constraint.gridwidth = 2;
            central.add(new JLabel("Output format"), constraint);

            constraint.gridx = GridBagConstraints.RELATIVE;
            constraint.gridwidth = 1;
            constraint.fill = GridBagConstraints.HORIZONTAL;
            central.add(this.formatBox, constraint);

            // log area
            constraint.gridy = 3;
            constraint.anchor = GridBagConstraints.LINE_START;
            constraint.gridheight = 1;
            constraint.ipady = 9;
            constraint.gridwidth = GridBagConstraints.REMAINDER;
            constraint.weightx = 0;
            central.add(new JLabel("Imaging log"), constraint);

            constraint.gridy = GridBagConstraints.RELATIVE;
            constraint.weightx = 1;
            constraint.weighty = 1;
            constraint.fill = GridBagConstraints.BOTH;
            JScrollPane logPane = new JScrollPane(this.logArea);
            logPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            central.add(logPane, constraint);
            return createOptionPane(central, new JButton[] {this.imageButton,
                this.closeButton});
        }

        /**
         * Creates an action that calls {@link #handleBrowseAction(JTextField)}
         * with a given text field.
         */
        protected Action createBrowseAction(final JTextField fileField) {
            return new AbstractAction(BROWSE_LABEL) {
                public void actionPerformed(ActionEvent evt) {
                    handleBrowseAction(fileField);
                    // set the out dir to the in file if it is not explciitly
                    // enabled
                    if (evt.getSource() == ImagerFrame.this.inFileBrowseButton
                        && !ImagerFrame.this.outFileEnabler.isSelected()) {
                        File file =
                            new File(ImagerFrame.this.inFileField.getText());
                        File dir =
                            file.isDirectory() ? file : file.getParentFile();
                        ImagerFrame.this.outFileField.setText(dir.getPath());
                    }
                }
            };
        }

        /** Initialises the GUI components. */
        protected void initComponents() {
            // outFileField.setBorder(BorderFactory.createEtchedBorder());
            setInFile(Groove.WORKING_DIR);
            setOutFile(Groove.WORKING_DIR);
            this.inFileField.setPreferredSize(new Dimension(300, 0));
            this.outFileField.setEditable(false);
            this.logArea.setEditable(false);
            this.logArea.setRows(10);
            // outFileField.setEnabled(false);
            this.browseChooser.setCurrentDirectory(new File(Groove.WORKING_DIR));
            // browseChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            for (ExtensionFilter filter : acceptFilters) {
                this.browseChooser.addChoosableFileFilter(filter);
            }
            this.browseChooser.setFileFilter(gpsFilter);
        }

        /** Initialises the actions of the imager. */
        protected void initActions() {
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            Action closeAction = new AbstractAction(Options.CLOSE_ACTION_NAME) {
                public void actionPerformed(ActionEvent evt) {
                    System.exit(0);
                }
            };
            Action imageAction = new AbstractAction(Options.IMAGE_ACTION_NAME) {
                public void actionPerformed(ActionEvent evt) {
                    // imageButton.setEnabled(false);
                    // new Thread() {
                    // public void run() {
                    handleImageAction();
                    // imageButton.setEnabled(true);
                    // }
                    // }.start();
                }
            };
            ItemListener enableItemListener = new ItemListener() {
                public void itemStateChanged(ItemEvent evt) {
                    ImagerFrame.this.outFileField.setEditable(ImagerFrame.this.outFileEnabler.isSelected());
                    // outFileField.setEnabled(outFileEnabler.isSelected());
                    // if (outFileEnabler.isSelected()) {
                    // outFileField.setBorder(BorderFactory.createEtchedBorder());
                    // } else {
                    // outFileField.setBorder(null);
                    // }
                    ImagerFrame.this.outFileBrowseButton.setEnabled(ImagerFrame.this.outFileEnabler.isSelected());
                }
            };
            this.closeButton.setAction(closeAction);
            this.imageButton.setAction(imageAction);
            this.outFileEnabler.addItemListener(enableItemListener);
            this.inFileBrowseButton.setAction(createBrowseAction(this.inFileField));
            this.outFileBrowseButton.setAction(createBrowseAction(this.outFileField));
            this.outFileBrowseButton.setEnabled(false);
        }

        /** Textfield to contain the name of the input file. */
        final JTextField inFileField = new JTextField();

        /** Textfield to contain the name of the output file. */
        final JTextField outFileField = new JTextField();

        /** Button to browse for the input file. */
        final JButton inFileBrowseButton = new JButton(BROWSE_LABEL);

        /** Button to browse for the output file. */
        final JButton outFileBrowseButton = new JButton(BROWSE_LABEL);

        /** Button to start the imaging. */
        private final JButton imageButton =
            new JButton(Options.IMAGE_ACTION_NAME);

        /** Button to close the imager. */
        private final JButton closeButton =
            new JButton(Options.CLOSE_ACTION_NAME);

        /** Checkbox to enable the out file. */
        final JCheckBox outFileEnabler = new JCheckBox();

        /** File chooser for the browse actions. */
        final JFileChooser browseChooser = new GrooveFileChooser();

        /** File chooser for the browse actions. */
        private final JTextArea logArea = new JTextArea();

        /** Combo box for the available image formats. */
        final JComboBox formatBox =
            new JComboBox(Imager.this.exporter.getExtensions().toArray());
    }
}