package groove.gui;

import groove.graph.GraphShape;
import groove.gui.jgraph.JGraph;
import groove.io.ExtensionFilter;
import groove.io.GrooveFileChooser;
import groove.util.Converter;
import groove.util.Groove;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

import net.sf.epsgraphics.ColorMode;
import net.sf.epsgraphics.EpsGraphics;

/**
 * Class providing functionality to export a {@link JGraph} to a file in different formats.
 * @author Arend Rensink
 * @version $Revision: 1.1 $
 */
public class Exporter {
    /**
     * Returns a file chooser for exporting, lazily creating it first.
     */
    public JFileChooser getFileChooser() {
        if (fileChooser== null) {
            fileChooser = new GrooveFileChooser();
            fileChooser.setAcceptAllFileFilterUsed(false);
            for (Format format: getFormatList()) {
                fileChooser.addChoosableFileFilter(format.getFilter());
            }
            fileChooser.setFileFilter(PngFormat.getInstance().getFilter());
        }
        return fileChooser;
    }
    
    /**
     * Exports the current state to a given format. The format is deduced from the file name, using
     * known file filters.
     */
    public void export(JGraph jGraph, File file) throws IOException {
        for (Format format: getFormatList()) {
            if (format.getFilter().accept(file)) {
                format.export(jGraph, file);
                return;
            }
        }
    }

    /** Returns an unmodifiable view on the list of currently supported formats. */
    public List<Format> getFormats() {
        return Collections.unmodifiableList(getFormatList());
    }

    /** Returns the list of file extensions of the supported formats. */
    public List<String> getExtensions() {
        List<String> result = new ArrayList<String>();
        for (Format format: getFormatList()) {
            result.add(format.getFilter().getExtension());
        }
        return result;
    }
    
    /** Returns the default format. */
    public Format getDefaultFormat() {
        return PngFormat.getInstance();
    }
    
    /** Returns the (modifiable) list of currently supported formats. */
    private List<Format> getFormatList() {
        if (formats == null) {
            formats = new ArrayList<Format>();
            formats.add(LispFormat.getInstance());
            formats.add(FsmFormat.getInstance());
            formats.add(JpgFormat.getInstance());
            formats.add(PngFormat.getInstance());
            formats.add(EpsFormat.getInstance());
        }
        return formats;
    }
    
    /** The file chooser of this exporter. */
    private GrooveFileChooser fileChooser;
    /** List of the supported export formats. */
    private List<Format> formats;
    
    /** Singleton class implementing the FSM export format. */
    private static class FsmFormat implements Format {
        /** Empty constructor to ensure singleton usage of the class. */
        private FsmFormat() {
            // empty
        }
        
        public ExtensionFilter getFilter() {
            return fsmFilter;
        }

        public void export(JGraph jGraph, File file) throws IOException {
            PrintWriter writer = new PrintWriter(new FileWriter(file));
            Converter.graphToFsm(jGraph.getModel().toPlainGraph(), writer);
            writer.close();
        }

        /**
         * Extension filter used for exporting graphs in fsm format.
         */
        private final ExtensionFilter fsmFilter = Groove.createFsmFilter();
        
        /** Returns the singleton instance of this class. */
        public static Format getInstance() {
            return instance;
        }
        
        /** The singleton instance of this class. */
        private static final Format instance = new FsmFormat();
    }

    
    /** Singleton class implementing the Lisp export format. */
    private static class LispFormat implements Format {
        /** Empty constructor to ensure singleton usage of the class. */
        private LispFormat() {
            // empty
        }

        public ExtensionFilter getFilter() {
            return lispFilter;
        }

        public void export(JGraph jGraph, File file) throws IOException {
            PrintWriter writer = new PrintWriter(new FileWriter(file));
            convert(jGraph.getModel().toPlainGraph(), writer);
            writer.close();
        }
        
        /** Writes a graph to a writer in the requried format. */
        private void convert(GraphShape graph, PrintWriter writer) {
            this.writer = writer;
            this.indent = 0;
            println("(%s", GRAPH_KEYWORD);
            println(")");
            assert this.indent == 0 : String.format("Conversion ended at indentation level %d", indent);
        }
        
        /** Prints a line to a writer, taking care of indentation. */
        private void println(String line, Object... args) {
            char[] spaces = new char[INDENT_COUNT * indent];
            Arrays.fill(spaces, ' ');
            writer.print(spaces);
            String text = String.format(line, args);
            writer.println(text);
            int opens = 0;
            int closes = 0;
            for (char c: text.toCharArray()) {
                if (c=='(') opens++;
                if (c==')') closes++;
            }
            indent += opens - closes;
        }

        /** The writer used in the current {@link #convert(GraphShape, PrintWriter)} invocation. */
        private PrintWriter writer;
        /** The indentation level of the current {@link #convert(GraphShape, PrintWriter)} invocation. */
        private int indent;
        /**
         * Extension filter used for exporting graphs in lisp format.
         */
        private final ExtensionFilter lispFilter = Groove.getFilter("Lisp layout files", Groove.LISP_EXTENSION, true);

        /** Returns the singleton instance of this class. */
        public static Format getInstance() {
            return instance;
        }
        
        /** The singleton instance of this class. */
        private static final Format instance = new LispFormat();
        
        /** Number of indentation positions per indent level. */
        private static int INDENT_COUNT = 4;
        /** Lisp function for graphs. */
        private static final String GRAPH_KEYWORD = "graph";
    }

    
    /** Class implementing the JPG export format. */
    private static class JpgFormat implements Format {
        /** Empty constructor to ensure singleton usage of the class. */
        private JpgFormat() {
            // empty
        }
        
        public ExtensionFilter getFilter() {
            return jpgFilter;
        }

        public void export(JGraph jGraph, File file) throws IOException {
            ImageIO.write(jGraph.toImage(), jpgFilter.getExtension().substring(1), file);
        }

        /**
         * Extension filter used for exporting graphs in jpeg format.
         */
        private final ExtensionFilter jpgFilter = new ExtensionFilter("JPEG image files", Groove.JPG_EXTENSION);

        /** Returns the singleton instance of this class. */
        public static Format getInstance() {
            return instance;
        }
        
        /** The singleton instance of this class. */
        private static final Format instance = new JpgFormat();
    }

    
    /** Class implementing the PNG export format. */
    private static class PngFormat implements Format {
        /** Empty constructor to ensure singleton usage of the class. */
        private PngFormat() {
            // empty
        }
        
        public ExtensionFilter getFilter() {
            return pngFilter;
        }

        public void export(JGraph jGraph, File file) throws IOException {
            ImageIO.write(jGraph.toImage(), pngFilter.getExtension().substring(1), file);
        }

        /**
         * Extension filter used for exporting graphs in png format.
         */
        private final ExtensionFilter pngFilter = new ExtensionFilter("PNG image files",
                Groove.PNG_EXTENSION);

        /** Returns the singleton instance of this class. */
        public static Format getInstance() {
            return instance;
        }
        
        /** The singleton instance of this class. */
        private static final Format instance = new PngFormat();
    }

    
    /** Class implementing the EPS export format. */
    private static class EpsFormat implements Format {
        /** Empty constructor to ensure singleton usage of the class. */
        private EpsFormat() {
            // empty
        }

        public ExtensionFilter getFilter() {
            return epsFilter;
        }

        public void export(JGraph jGraph, File file) throws IOException {
            // Create a graphics contents on the buffered image
            BufferedImage image = jGraph.toImage();
            // Create an output stream
            OutputStream out = new FileOutputStream(file);
            // minX,minY,maxX,maxY
            EpsGraphics g2d = new EpsGraphics("Title", out, 0, 0, image.getWidth(), image
                    .getHeight(), ColorMode.COLOR_RGB);
            g2d.drawImage(jGraph.toImage(), new AffineTransform(), null);
            g2d.close();
        }

        /**
         * Extension filter used for exporting graphs in png format.
         */
        private final ExtensionFilter epsFilter = new ExtensionFilter("EPS image files",
                Groove.EPS_EXTENSION);

        /** Returns the singleton instance of this class. */
        public static Format getInstance() {
            return instance;
        }
        
        /** The singleton instance of this class. */
        private static final Format instance = new EpsFormat();
    }

    /**
     * Interface for export formats.
     */
    public static interface Format {
        /** Returns the extension filter for this format. */
        ExtensionFilter getFilter();
        /** Exports a JGraph into this format. */
        void export(JGraph jGraph, File file) throws IOException;
    }
}
