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

import static groove.io.FileType.GRAMMAR_FILTER;
import static groove.io.FileType.GXL_FILTER;
import static groove.io.FileType.RULE_FILTER;
import static groove.io.FileType.STATE_FILTER;
import static groove.io.FileType.TYPE_FILTER;
import groove.explore.Verbosity;
import groove.grammar.QualName;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.model.FormatException;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.GraphBasedModel;
import groove.grammar.model.ResourceKind;
import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.display.DisplayKind;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.AspectJModel;
import groove.io.external.Exporter;
import groove.io.external.Exporter.Exportable;
import groove.io.external.Format;
import groove.io.external.FormatExporter;
import groove.io.external.PortException;
import groove.io.external.format.NativePorter;
import groove.util.CommandLineOption;
import groove.util.CommandLineTool;
import groove.util.Groove;
import groove.util.Pair;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
    public Imager(String... args) {
        this(false, args);
    }

    /**
     * Constructs a new imager, which may be GUI-based or command-line.
     * @param gui <tt>true</tt> if the imager should be GUI-based.
     */
    public Imager(boolean gui, String... args) {
        super(args);
        // force the LAF to be set
        groove.gui.Options.initLookAndFeel();
        if (gui) {
            this.imagerFrame = new ImagerFrame();
            this.imagerFrame.pack();
            this.imagerFrame.setVisible(true);
        } else {
            this.imagerFrame = null;
            addOption(getEditorViewOption());
            addOption(getFormatOption());
        }
    }

    /**
     * Does the actual conversion work.
     * @require <tt>getLocation() != null</tt>
     */
    public void start() {
        try {
            File inFile = getInFile();
            File outFile = getOutFile();
            if (inFile == null) {
                println("No input file specified");
            } else if (!inFile.exists()) {
                println("Input file " + inFile + " does not exist");
            } else if (outFile == null) {
                makeImage(inFile, inFile);
            } else {
                makeImage(inFile, outFile);
            }
        } catch (IOException e) {
            println(e.getMessage());
        }
    }

    /**
     * Makes an image file from the specified input file. If the input file is a
     * directory, the method descends recursively. The types of input files
     * recognized are: gxl, gps and gst
     * @param inFile the input file to be converted
     * @param outFile the intended output file
     */
    public void makeImage(File inFile, File outFile) throws IOException {
        if (!inFile.exists()) {
            throw new IOException("Input file " + inFile + " does not exist");
        }
        File grammarFile = getGrammarFile(inFile);
        if (grammarFile == null) {
            throw new IOException("Input file " + inFile
                + " is not part of a grammar");
        }
        try {
            GrammarModel grammar = GrammarModel.newInstance(grammarFile, false);
            makeImage(grammar, inFile, outFile);
        } catch (IllegalArgumentException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Makes an image file from the specified input file. If the input file is a
     * directory, the method descends recursively. The types of input files
     * recognized are: gxl, gps and gst
     * @param inFile the input file to be converted
     * @param outFile the intended output file
     */
    private void makeImage(GrammarModel grammar, File inFile, File outFile)
        throws IOException {
        // if the given input-file is a directory, call this method recursively
        // for each file it contains but ensure:
        // --> output-file exists or can be created
        if (inFile.isDirectory()) {
            File[] files = inFile.listFiles();
            if (outFile.exists() || outFile.mkdir()) {
                for (File element : files) {
                    if (element.isDirectory() || parse(element) != null) {
                        // only process c
                        makeImage(grammar, element,
                            new File(outFile, element.getName()));
                    }
                }
            } else {
                throw new IOException("Directory " + outFile
                    + " could not be created");
            }
        }
        // or the input-file is an ordinary Groove-file (state or rule)
        // here ensure:
        // --> output-file exists and will be overwritten or the directory in
        // which it will be placed exists or can be created
        else {
            File outParent = outFile.getParentFile();
            if (outParent == null) {
                outParent = inFile.getParentFile();
            } else if (!outParent.exists() && !outParent.mkdir()) {
                JOptionPane.showMessageDialog(null, "Output directory "
                    + outParent + " cannot be created");
                return;
            }

            Pair<ResourceKind,QualName> resource = parse(inFile);
            if (resource == null) {
                throw new IOException("Input file " + inFile
                    + " is not a resource file");
            }
            String imageFormat = getImageFormat();
            if (imageFormat == null) {
                imageFormat = outFile.toString();
            }
            Map<String,Format> formats = getFormatMap();
            Format outputFormat = null;
            String extension = null;
            for (Entry<String,Format> e : formats.entrySet()) {
                if (imageFormat.endsWith(e.getKey())) {
                    extension = e.getKey();
                    outputFormat = e.getValue();
                    break;
                }
            }
            String outFileName;
            if (outputFormat == null) {
                // Pick first format as default
                Entry<String,Format> e =
                    getFormatMap().entrySet().iterator().next();
                extension = e.getKey();
                outputFormat = e.getValue();
                // maybe the output file was set to equal the input file;
                // try stripping the input extension
                outFileName = outFile.getName();
            } else {
                outFileName = outputFormat.stripExtension(outFile.getName());
            }
            outFile =
                new File(outParent, outFileName + ExtensionFilter.SEPARATOR
                    + extension);

            GraphBasedModel<?> resourceModel =
                (GraphBasedModel<?>) grammar.getResource(resource.one(),
                    resource.two().toString());
            AspectGraph aspectGraph = resourceModel.getSource();
            Options options = new Options();
            options.getItem(Options.SHOW_VALUE_NODES_OPTION).setSelected(
                isEditorView());
            options.getItem(Options.SHOW_ASPECTS_OPTION).setSelected(
                isEditorView());
            DisplayKind displayKind =
                DisplayKind.toDisplay(ResourceKind.toResource(aspectGraph.getRole()));
            AspectJGraph jGraph = new AspectJGraph(null, displayKind, false);
            AspectJModel model = jGraph.newModel();
            model.setGrammar(grammar);
            model.loadGraph(aspectGraph);
            jGraph.setModel(model);
            // Ugly hack to prevent clipping of the image. We set the
            // jGraph size to twice its normal size. This does not
            // affect the final size of the exported figure, hence
            // it can be considered harmless... ;P
            Dimension oldPrefSize = jGraph.getPreferredSize();
            Dimension newPrefSize =
                new Dimension(oldPrefSize.width * 2, oldPrefSize.height * 2);
            jGraph.setSize(newPrefSize);
            printlnMedium("Imaging " + inFile + " as " + outFile);
            try {
                Exportable exportable = new Exportable(jGraph);
                ((FormatExporter) outputFormat.getFormatter()).doExport(
                    outFile, outputFormat, exportable);
            } catch (PortException e1) {
                println("Error exporting graph: " + e1.getMessage());
            }
        }
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
     * Sets the location of the file to be imaged. No check is
     * done if the location actually exists.
     * @param fileName the name of the files to be imaged
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

    /** The location of the file(s) to be imaged. */
    private File inFile;

    /** The  optional location of the output file(s) to be imaged. */
    private File outFile;

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
            printError("Invalid number of arguments", true);
        }
    }

    @Override
    protected String getUsageMessage() {
        return "Usage: Imager [options] inputfile";
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
        return true;
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

    /**
     * Returns the image format to which the graphs will be converted.
     */
    private String getImageFormat() {
        return this.imageFormat;
    }

    /**
     * Sets the image format to which the graphs will be converted.
     */
    private void setImageFormat(String imageFormat) {
        this.imageFormat = imageFormat;
    }

    /** Name of the image format to which the imager converts. */
    private String imageFormat;

    /** Indicates whether the image should show all label prefixes. */
    public final boolean isEditorView() {
        return this.editorView;
    }

    /** Makes sure the image shows all label prefixes. */
    public final void setEditorView(boolean editorView) {
        this.editorView = editorView;
    }

    private boolean editorView;

    /**
     * The imager frame if the invocation is gui-based; <tt>null</tt> if it is
     * command-line based.
     */
    private final ImagerFrame imagerFrame;

    /** Lazily creates and returns the format option associated with this Imager. */
    private EditorViewOption getEditorViewOption() {
        if (this.editorViewOption == null) {
            this.editorViewOption = new EditorViewOption();
        }
        return this.editorViewOption;
    }

    /** 
     * The option that makes prefixes visible in the imaged graph.
     * Lazily created by {@link #getEditorViewOption()}.
     */
    private EditorViewOption editorViewOption;

    /** Lazily creates and returns the format option associated with this Imager. */
    private FormatOption getFormatOption() {
        if (this.formatOption == null) {
            this.formatOption = new FormatOption();
        }
        return this.formatOption;
    }

    /** 
     * The format option associated with this Imager.
     * Lazily created by {@link #getFormatOption()}.
     */
    private FormatOption formatOption;

    /** Starts the imager with a list of options and file names. */
    public static void main(String[] args) {
        Imager imager;
        if (args.length == 0) {
            new Imager(true);
        } else {
            imager = new Imager(args);
            imager.processArguments();
            imager.start();
        }
    }

    /** Returns the parent file of a given file that corresponds
     * to a grammar directory, or {@code null} if the file is not
     * within a grammar directory.
     * @param file the file to be parsed
     * @return a parent file of {@code file}, or {@code null}
     */
    private static File getGrammarFile(File file) {
        while (file != null && !GRAMMAR_FILTER.accept(file)) {
            file = file.getParentFile();
        }
        return file;
    }

    /**
     * Decomposes the name of a file, supposedly within a grammar,
     * into a pair consisting of its resource kind and the qualified name
     * of the resource within the grammar.
     */
    private static Pair<ResourceKind,QualName> parse(File file) {
        ResourceKind kind = null;
        // find out the resource kind
        for (ResourceKind k : ResourceKind.values()) {
            if (k.isGraphBased() && k.getFilter().accept(file)) {
                kind = k;
                break;
            }
        }
        QualName qualName = null;
        if (kind != null) {
            file = new File(kind.getFilter().stripExtension(file.toString()));
            // break the filename into fragments, up to the containing grammar
            List<String> fragments = new LinkedList<String>();
            while (file != null && !GRAMMAR_FILTER.accept(file)) {
                fragments.add(0, file.getName());
                file = file.getParentFile();
            }
            // if file == null, there was no containing grammar
            if (file != null) {
                try {
                    qualName = new QualName(fragments);
                } catch (FormatException e) {
                    // do nothing
                }
            }
        }
        return qualName == null ? null : Pair.newPair(kind, qualName);
    }

    /** Collects a mapping from file extensions to formats. */
    public static Map<String,Format> getFormatMap() {
        Map<String,Format> result = formatMap;
        if (result == null) {
            result = formatMap = new HashMap<String,Format>();
            for (FormatExporter exporter : Exporter.getExporters()) {
                if (exporter instanceof NativePorter) {
                    continue;
                }
                for (Format format : exporter.getSupportedFormats()) {
                    for (String ext : format.getExtensions()) {
                        result.put(ext.substring(1), format); //strip dot
                    }
                }
            }
        }
        return result;
    }

    private static Map<String,Format> formatMap;

    /** Name of the imager application. */
    static public final String APPLICATION_NAME = "Imager";
    /** Label for the browse buttons. */
    static public final String BROWSE_LABEL = "Browse...";

    /** An array of all filters identifying files that can be imaged. */
    private static final List<ExtensionFilter> acceptFilters;
    static {
        acceptFilters = new ArrayList<ExtensionFilter>(4);
        acceptFilters.add(GRAMMAR_FILTER);
        acceptFilters.add(TYPE_FILTER);
        acceptFilters.add(STATE_FILTER);
        acceptFilters.add(RULE_FILTER);
        acceptFilters.add(GXL_FILTER);
    }

    private class EditorViewOption implements CommandLineOption {
        @Override
        public String[] getDescription() {
            return new String[] {DESCRIPTION};
        }

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public String getParameterName() {
            return null;
        }

        @Override
        public boolean hasParameter() {
            return false;
        }

        @Override
        public void parse(String parameter) throws IllegalArgumentException {
            setEditorView(true);
        }

        /** Abbreviation of the editor view option. */
        static public final String NAME = "e";
        /** Short description of the editor view option. */
        static public final String DESCRIPTION = "Enforces editor view export";
    }

    /**
     * Option to set the output format for the imager.
     */
    private class FormatOption implements CommandLineOption {
        /** Abbreviation of the format option. */
        static public final String NAME = "f";
        /** Short description of the format option. */
        static public final String DESCRIPTION =
            "Output format extension. Supported formats are:";
        /** Option parameter name. */
        static public final String PARAMETER_NAME = "name";

        public String getName() {
            return NAME;
        }

        public String[] getDescription() {
            List<String> result = new LinkedList<String>();
            result.add(DESCRIPTION);
            Map<String,Format> exts = getFormatMap();
            for (String formatName : exts.keySet()) {
                String format = "* " + formatName;
                result.add(format);
            }
            return result.toArray(new String[result.size()]);
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
            String extension = parameter;

            Map<String,Format> exts = getFormatMap();

            // first check if parameter is a valid format name
            if (!exts.containsKey(extension)) {
                throw new IllegalArgumentException("Unknown format: "
                    + parameter);
            }
            setImageFormat(extension);
        }
    }

    /**
     * Frame with fields for selecting input and output files and starting the
     * imager.
     */
    private class ImagerFrame extends JFrame {
        /** Constructs an instanceof the frame, with GUI components set. */
        public ImagerFrame() {
            super(APPLICATION_NAME);
            setIconImage(Icons.GROOVE_ICON_16x16.getImage());
            initComponents();
            initActions();
            setContentPane(createContentPane());
            setVerbosity(Verbosity.HIGH);
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
            String error = null;
            try {
                File inFile = new File(this.inFileField.getText());
                File outFile;
                if (this.outFileField.isEditable()) {
                    outFile = new File(this.outFileField.getText());
                } else {
                    outFile = inFile;
                }
                makeImage(inFile, outFile);
            } catch (IOException e) {
                error = e.getMessage();
            }
            if (error != null) {
                JOptionPane.showMessageDialog(this, error, "Error",
                    JOptionPane.ERROR_MESSAGE);
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
            setInFile(Groove.WORKING_DIR);
            setOutFile(Groove.WORKING_DIR);
            this.inFileField.setPreferredSize(new Dimension(300, 0));
            this.outFileField.setEditable(false);
            this.logArea.setEditable(false);
            this.logArea.setRows(10);
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
                    handleImageAction();
                }
            };
            ItemListener enableItemListener = new ItemListener() {
                public void itemStateChanged(ItemEvent evt) {
                    ImagerFrame.this.outFileField.setEditable(ImagerFrame.this.outFileEnabler.isSelected());
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
        private final JButton imageButton = new JButton(
            Options.IMAGE_ACTION_NAME);

        /** Button to close the imager. */
        private final JButton closeButton = new JButton(
            Options.CLOSE_ACTION_NAME);

        /** Checkbox to enable the out file. */
        final JCheckBox outFileEnabler = new JCheckBox();

        /** File chooser for the browse actions. */
        final JFileChooser browseChooser =
            GrooveFileChooser.getFileChooser(acceptFilters);

        /** File chooser for the browse actions. */
        private final JTextArea logArea = new JTextArea();

        /** Combo box for the available image formats. */
        final JComboBox formatBox = new JComboBox(
            Imager.getFormatMap().keySet().toArray());
    }
}