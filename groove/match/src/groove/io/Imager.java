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
 * $Id$
 */
package groove.io;

import static groove.explore.Verbosity.MEDIUM;
import static groove.io.FileType.GRAMMAR;
import static groove.io.FileType.GXL;
import static groove.io.FileType.RULE;
import static groove.io.FileType.STATE;
import static groove.io.FileType.TYPE;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import javax.swing.SwingUtilities;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.FileOptionHandler;
import org.kohsuke.args4j.spi.OneArgumentOptionHandler;
import org.kohsuke.args4j.spi.Setter;

import groove.explore.Verbosity;
import groove.grammar.QualName;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.GraphBasedModel;
import groove.grammar.model.ResourceKind;
import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.display.DisplayKind;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.AspectJModel;
import groove.io.external.Exportable;
import groove.io.external.Exporter;
import groove.io.external.Exporters;
import groove.io.external.PortException;
import groove.io.external.Porter;
import groove.io.external.Porter.Kind;
import groove.util.Groove;
import groove.util.Pair;
import groove.util.cli.ExistingFileHandler;
import groove.util.cli.GrooveCmdLineTool;
import groove.util.parse.FormatException;

/**
 * Application to create jpeg or gif files for a state or rule graph, or a
 * directory of them.
 * @author Arend Rensink
 * @version $Revision$
 */
public class Imager extends GrooveCmdLineTool<Object> {
    /**
     * Constructs the generator and processes the command-line arguments.
     * @throws CmdLineException if any error was found in the command-line arguments
     */
    public Imager(String... args) throws CmdLineException {
        this(false, args);
    }

    /**
     * Constructs a new imager, which may be GUI-based or command-line.
     * @param gui <tt>true</tt> if the imager should be GUI-based
     * @param args command-line arguments. If {@code gui} is {@code true},
     * the command-line arguments must be absent.
     */
    public Imager(boolean gui, String... args) {
        super("Imager", args);
        // force the LAF to be set
        groove.gui.Options.initLookAndFeel();
        if (gui) {
            if (args.length > 0) {
                throw new IllegalArgumentException(
                    "GUI-based imager is not compatible with arguments" + Arrays.toString(args));
            }
            setVerbosity(Verbosity.HIGH);
            this.imagerFrame = new ImagerFrame();
            this.imagerFrame.pack();
            this.imagerFrame.setVisible(true);
        } else {
            this.imagerFrame = null;
        }
    }

    @Override
    protected void emit(Verbosity min, String format, Object... args) {
        if (this.imagerFrame == null) {
            super.emit(min, format, args);
        } else if (getVerbosity().compareTo(min) >= 0) {
            this.imagerFrame.emit(format, args);
        }
    }

    /**
     * Runs the state space generation process.
     * @return {@code null} always
     * @throws Exception if anything goes wrong during generation.
     */
    @Override
    protected Object run() throws Exception {
        File inFile = getInFile();
        File outFile = getOutFile();
        makeImage(inFile, outFile == null ? inFile : outFile);
        return null;
    }

    /**
     * Makes an image file from the specified input file. If the input file is a
     * directory, the method descends recursively.
     * @param inFile the input file to be converted
     * @param outFile the intended output file. If {@code inFile} is a directory,
     * then {@code outFile} must be a directory as well.
     */
    public void makeImage(File inFile, File outFile) throws IOException {
        if (!inFile.exists()) {
            throw new IOException("Input file " + inFile + " does not exist");
        }
        File grammarFile = getGrammarFile(inFile);
        if (grammarFile == null) {
            throw new IOException("Input file " + inFile + " is not part of a grammar");
        }
        if (inFile.isDirectory() && !outFile.isDirectory()) {
            throw new IOException(
                "Can't image files in directory " + inFile + " to single file " + outFile);
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
     * @param outFile the intended output file. If {@code inFile} is a directory,
     * then {@code outFile} is guaranteed to be a directory as well.
     */
    private void makeImage(GrammarModel grammar, File inFile, File outFile) throws IOException {
        // if the given input-file is a directory, call this method recursively
        // for each file it contains but ensure:
        // --> output-file exists or can be created
        if (inFile.isDirectory()) {
            File[] files = inFile.listFiles();
            if (outFile.exists() || outFile.mkdir()) {
                for (File element : files) {
                    // see if we want to process this file
                    boolean process = element.isDirectory();
                    if (!process) {
                        Pair<ResourceKind,QualName> resource = parse(element);
                        process = resource != null && resource.one()
                            .isGraphBased();
                    }
                    if (process) {
                        makeImage(grammar, element, new File(outFile, element.getName()));
                    }
                }
            } else {
                throw new IOException("Output directory " + outFile + " cannot be created");
            }
        }
        // or the input-file is an ordinary Groove-file (state or rule)
        // here ensure:
        // --> output-file exists and will be overwritten or the directory in
        // which it will be placed exists or can be created
        else {
            Pair<ResourceKind,QualName> resource = parse(inFile);
            if (resource == null) {
                throw new IOException("Input file " + inFile + " is not a graph resource");
            }
            // Determine output file folder and filename
            String outFileName;
            File outParent;
            if (outFile.isDirectory()) {
                outParent = outFile;
                outFileName = inFile.getName();
            } else {
                outParent = outFile.getParentFile();
                outFileName = outFile.getName();
                if (outParent != null && !outParent.exists() && !outParent.mkdir()) {
                    throw new IOException("Output directory " + outParent + " cannot be created");
                }
            }
            // Determine output file format
            FileType outFileType = getFormatMap().get(getOutFormatExt());
            final FileType fileType = outFileType == null ? getFormatMap().values()
                .iterator()
                .next() : outFileType;
            final Exporter exporter = Exporters.getExporter(fileType);
            final File exportFile = new File(outParent, fileType.addExtension(outFileName));

            emit(MEDIUM, "Imaging %s as %s%n", inFile, outFile);
            GraphBasedModel<?> resourceModel =
                (GraphBasedModel<?>) grammar.getResource(resource.one(), resource.two());
            final Exportable exportable = toExportable(resourceModel, exporter.getFormatKinds());
            // make sure the export happens on the event thread
            Runnable export = new Runnable() {
                @Override
                public void run() {
                    try {
                        exporter.doExport(exportable, exportFile, fileType);
                    } catch (PortException e1) {
                        throw new RuntimeException(e1);
                    }
                }
            };
            if (SwingUtilities.isEventDispatchThread()) {
                try {
                    export.run();
                } catch (RuntimeException exc) {
                    throw new IOException(exc.getCause());
                }
            } else {
                try {
                    SwingUtilities.invokeAndWait(export);
                } catch (InterruptedException exc) {
                    // do nothing
                } catch (InvocationTargetException exc) {
                    throw new IOException(exc.getCause()
                        .getCause());
                }
            }
        }
    }

    /** Converts a resource model to an exportable object of the right kind. */
    private Exportable toExportable(GraphBasedModel<?> resourceModel, Set<Porter.Kind> outFormats) {
        Exportable result;
        AspectGraph aspectGraph = resourceModel.getSource();
        // find out what we have to export
        if (outFormats.contains(Porter.Kind.GRAPH)) {
            result = new Exportable(aspectGraph);
        } else if (outFormats.contains(Porter.Kind.RESOURCE)) {
            result = new Exportable(resourceModel);
        } else {
            assert outFormats.contains(Porter.Kind.JGRAPH);
            Options options = new Options();
            options.getItem(Options.SHOW_VALUE_NODES_OPTION)
                .setSelected(isEditorView());
            options.getItem(Options.SHOW_ASPECTS_OPTION)
                .setSelected(isEditorView());
            DisplayKind displayKind =
                DisplayKind.toDisplay(ResourceKind.toResource(aspectGraph.getRole()));
            AspectJGraph jGraph = new AspectJGraph(null, displayKind, false);
            jGraph.setGrammar(resourceModel.getGrammar());
            AspectJModel model = jGraph.newModel();
            model.loadGraph(aspectGraph);
            jGraph.setModel(model);
            // Ugly hack to prevent clipping of the image. We set the
            // jGraph size to twice its normal size. This does not
            // affect the final size of the exported figure, hence
            // it can be considered harmless... ;P
            Dimension oldPrefSize = jGraph.getPreferredSize();
            Dimension newPrefSize = new Dimension(oldPrefSize.width * 2, oldPrefSize.height * 2);
            jGraph.setSize(newPrefSize);
            result = new Exportable(jGraph);
        }
        return result;
    }

    /** Returns the location of the file(s) to be imaged. */
    private File getInFile() {
        return this.inFile;
    }

    /**
     * Returns the intended location for the image file(s).
     */
    private File getOutFile() {
        return this.outFile;
    }

    /**
     * Returns the image format to which the graphs will be converted.
     */
    private String getOutFormatExt() {
        return this.outFormatExt;
    }

    /**
     * Sets the output format extension to which the graphs will be converted.
     */
    private void setOutFormatExt(String outFormatExt) {
        this.outFormatExt = outFormatExt;
    }

    /** Indicates whether the image should show all label prefixes. */
    private boolean isEditorView() {
        return this.editorView;
    }

    /**
     * The imager frame if the invocation is gui-based; <tt>null</tt> if it is
     * command-line based.
     */
    private final ImagerFrame imagerFrame;

    /** The location of the file to be imaged. */
    @Argument(metaVar = "input", usage = "Input file or directory", required = true,
        handler = ExistingFileHandler.class)
    private File inFile;

    /** The  optional location of the output file to be imaged. */
    @Argument(metaVar = "output", index = 1,
        usage = "Output file name; if omitted, the input file name is used. "
            + "In the absence of the '-f' option, the "
            + "extension of <output> is taken to specify the output format",
        handler = FileOptionHandler.class)
    private File outFile;

    /** Name of the image format to which the imager converts. */
    @Option(name = "-f", metaVar = "ext", usage = FormatHandler.USAGE,
        handler = FormatHandler.class)
    private String outFormatExt;

    @Option(name = "-e", usage = "Enforces editor view export")
    private boolean editorView;

    /**
     * Starts the imager with a list of options and file names.
     * Always exits with {@link System#exit(int)}.
     * Call {@link #execute(String[])} for programmatic use instead
     * of command-line use.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            new Imager(true);
        } else {
            tryExecute(Imager.class, args);
        }
    }

    /** Starts the imager with a list of options and file names. */
    public static void execute(String[] args) throws Exception {
        new Imager(args).start();
    }

    /** Returns the parent file of a given file that corresponds
     * to a grammar directory, or {@code null} if the file is not
     * within a grammar directory.
     * @param file the file to be parsed
     * @return a parent file of {@code file}, or {@code null}
     */
    private static File getGrammarFile(File file) {
        while (file != null && !GRAMMAR.hasExtension(file)) {
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
            if (k.isGraphBased() && k.getFileType()
                .hasExtension(file)) {
                kind = k;
                break;
            }
        }
        QualName qualName = null;
        if (kind != null) {
            file = kind.getFileType()
                .stripExtension(file);
            // break the filename into fragments, up to the containing grammar
            List<String> fragments = new LinkedList<>();
            while (file != null && !GRAMMAR.hasExtension(file)) {
                fragments.add(0, file.getName());
                file = file.getParentFile();
            }
            // if file == null, there was no containing grammar
            if (file != null) {
                try {
                    qualName = new QualName(fragments).testValid();
                } catch (FormatException e) {
                    // do nothing
                }
            }
        }
        return qualName == null ? null : Pair.newPair(kind, qualName);
    }

    /** Collects a mapping from file extensions to formats. */
    public static Map<String,FileType> getFormatMap() {
        Map<String,FileType> result = formatMap;
        if (result == null) {
            result = formatMap = new HashMap<>();
            for (Exporter exporter : Exporters.getExporters()) {
                if (exporter.getFormatKinds()
                    .contains(Kind.RESOURCE)) {
                    continue;
                }
                for (FileType fileType : exporter.getSupportedFileTypes()) {
                    result.put(fileType.getExtensionName(), fileType); //strip dot
                }
            }
        }
        return result;
    }

    /** An array of all file types that can be imaged. */
    private static final Set<FileType> acceptedTypes;

    static {
        acceptedTypes = EnumSet.noneOf(FileType.class);
        acceptedTypes.add(GRAMMAR);
        acceptedTypes.add(TYPE);
        acceptedTypes.add(STATE);
        acceptedTypes.add(RULE);
        acceptedTypes.add(GXL);
    }

    private static Map<String,FileType> formatMap;

    /** Name of the imager application. */
    static public final String APPLICATION_NAME = "Imager";
    /** Label for the browse buttons. */
    static public final String BROWSE_LABEL = "Browse...";

    /** Option handler for output format extension. */
    public static class FormatHandler extends OneArgumentOptionHandler<String> {
        /** Required constructor. */
        public FormatHandler(CmdLineParser parser, OptionDef option,
            Setter<? super String> setter) {
            super(parser, option, setter);
        }

        @Override
        protected String parse(String argument) throws CmdLineException {
            // first check if parameter is a valid format name
            if (!getFormatMap().containsKey(argument)) {
                throw new CmdLineException(this.owner, "Unknown format: " + argument);
            }
            return argument;
        }

        /** Usage message for the -f option. */

        public static final String USAGE =
            "Specifies the output format extension. Supported formats are:";
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
                JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
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
                fileField.setText(this.browseChooser.getSelectedFile()
                    .getAbsolutePath());
            }
        }

        /**
         * Writes a formatted line to the logging area.
         * @param text the line to be written
         */
        public void emit(String text, Object... args) {
            this.logArea.append(String.format(text, args));
        }

        /**
         * Creates and returns a plain option pane on the basis of a given
         * message panel and row of buttons.
         * @param messagePane the central message pane
         * @param buttonRow the buttons to be displayed at the bottom of the
         *        pane
         */
        protected JOptionPane createOptionPane(JPanel messagePane, JButton[] buttonRow) {
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
                @Override
                public void actionPerformed(ActionEvent evt) {
                    setOutFormatExt((String) ImagerFrame.this.formatBox.getSelectedItem());
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
            return createOptionPane(central, new JButton[] {this.imageButton, this.closeButton});
        }

        /**
         * Creates an action that calls {@link #handleBrowseAction(JTextField)}
         * with a given text field.
         */
        protected Action createBrowseAction(final JTextField fileField) {
            return new AbstractAction(BROWSE_LABEL) {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    handleBrowseAction(fileField);
                    // set the out dir to the in file if it is not explciitly
                    // enabled
                    if (evt.getSource() == ImagerFrame.this.inFileBrowseButton
                        && !ImagerFrame.this.outFileEnabler.isSelected()) {
                        File file = new File(ImagerFrame.this.inFileField.getText());
                        File dir = file.isDirectory() ? file : file.getParentFile();
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
                @Override
                public void actionPerformed(ActionEvent evt) {
                    System.exit(0);
                }
            };
            Action imageAction = new AbstractAction(Options.IMAGE_ACTION_NAME) {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    handleImageAction();
                }
            };
            ItemListener enableItemListener = new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent evt) {
                    ImagerFrame.this.outFileField
                        .setEditable(ImagerFrame.this.outFileEnabler.isSelected());
                    ImagerFrame.this.outFileBrowseButton
                        .setEnabled(ImagerFrame.this.outFileEnabler.isSelected());
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
        private final JButton imageButton = new JButton(Options.IMAGE_ACTION_NAME);

        /** Button to close the imager. */
        private final JButton closeButton = new JButton(Options.CLOSE_ACTION_NAME);

        /** Checkbox to enable the out file. */
        final JCheckBox outFileEnabler = new JCheckBox();

        /** File chooser for the browse actions. */
        final JFileChooser browseChooser = GrooveFileChooser.getInstance(acceptedTypes);

        /** File chooser for the browse actions. */
        private final JTextArea logArea = new JTextArea();

        /** Combo box for the available image formats. */
        final JComboBox<String> formatBox = new JComboBox<>(Imager.getFormatMap()
            .keySet()
            .toArray(new String[] {}));
    }
}