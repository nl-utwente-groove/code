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
 * $Id: Exporter.java,v 1.10 2008-03-04 22:03:36 rensink Exp $
 */
package groove.gui;

import groove.graph.Graph;
import groove.gui.jgraph.EditorJModel;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.JGraph;
import groove.gui.jgraph.StateJGraph;
import groove.io.ExtensionFilter;
import groove.io.GrooveFileChooser;
import groove.util.Converter;
import groove.util.Groove;
import groove.view.aspect.AspectGraph;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

import net.sf.epsgraphics.ColorMode;
import net.sf.epsgraphics.EpsGraphics;

/**
 * Class providing functionality to export a {@link JGraph} to a file in
 * different formats.
 * @author Arend Rensink
 * @version $Revision$
 */
public class Exporter {
    /**
     * Returns a file chooser for exporting, lazily creating it first.
     */
    public JFileChooser getFileChooser() {
        if (this.fileChooser == null) {
            this.fileChooser = new GrooveFileChooser();
            this.fileChooser.setAcceptAllFileFilterUsed(false);
            for (Format format : getFormatList()) {
                this.fileChooser.addChoosableFileFilter(format.getFilter());
            }
            this.fileChooser.setFileFilter(PngFormat.getInstance().getFilter());
        }
        return this.fileChooser;
    }

    /**
     * Exports the current state to a given format. The format is deduced from
     * the file name, using known file filters.
     */
    public void export(JGraph jGraph, File file) throws IOException {
        for (Format format : getFormatList()) {
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

    /**
     * Returns an unmodifiable view on the list of currently supported
     * structural formats. A structural format is one that does not save layout.
     */
    public List<StructuralFormat> getStructuralFormats() {
        return Collections.unmodifiableList(getStructuralFormatList());
    }

    /** Returns the list of file extensions of the supported formats. */
    public List<String> getExtensions() {
        List<String> result = new ArrayList<String>();
        for (Format format : getFormatList()) {
            result.add(format.getFilter().getExtension());
        }
        return result;
    }

    /** Returns the default format. */
    public Format getDefaultFormat() {
        return PngFormat.getInstance();
    }

    /**
     * Checks if the given file is accepted by at least one of the image
     * exporters.
     */
    public boolean acceptsImageFormat(File file) {
        if (JpgFormat.getInstance().getFilter().accept(file)
            || PngFormat.getInstance().getFilter().accept(file)
            || EpsFormat.getInstance().getFilter().accept(file)) {
            return true;
        } else {
            return false;
        }
    }

    /** Returns the (modifiable) list of currently supported formats. */
    private List<Format> getFormatList() {
        if (this.formats == null) {
            this.formats = new ArrayList<Format>();
            this.formats.add(JpgFormat.getInstance());
            this.formats.add(PngFormat.getInstance());
            this.formats.add(EpsFormat.getInstance());
            this.formats.add(AutFormat.getInstance());
            this.formats.add(TikzFormat.getInstance());
            this.formats.add(KthFormat.getInstance());
            this.formats.add(FsmFormat.getInstance());
        }
        return this.formats;
    }

    /** Returns the (modifiable) list of currently supported formats. */
    private List<StructuralFormat> getStructuralFormatList() {
        if (this.structuralFormats == null) {
            this.structuralFormats = new ArrayList<StructuralFormat>();
            for (Format format : getFormatList()) {
                if (format instanceof StructuralFormat) {
                    this.structuralFormats.add((StructuralFormat) format);
                }
            }
        }
        return this.structuralFormats;
    }

    /** The file chooser of this exporter. */
    private GrooveFileChooser fileChooser;
    /** List of the supported export formats. */
    private List<Format> formats;
    /** List of the supported structural export formats. */
    private List<StructuralFormat> structuralFormats;

    /** Singleton class implementing the FSM export format. */
    private static class FsmFormat implements StructuralFormat {
        /** Empty constructor to ensure singleton usage of the class. */
        private FsmFormat() {
            // empty
        }

        public ExtensionFilter getFilter() {
            return this.fsmFilter;
        }

        public void export(JGraph jGraph, File file) throws IOException {
            Graph<?,?> graph;
            if (jGraph.getModel() instanceof GraphJModel) {
                graph = ((GraphJModel<?,?>) jGraph.getModel()).getGraph();
            } else {
                graph = ((EditorJModel) jGraph.getModel()).toPlainGraph();
            }
            export(graph, file);
        }

        public void export(Graph<?,?> graph, File file) throws IOException {
            PrintWriter writer = new PrintWriter(new FileWriter(file));
            Converter.graphToFsm(graph, writer);
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

    /** Class implementing the JPG export format. */
    private static class JpgFormat implements Format {
        /** Empty constructor to ensure singleton usage of the class. */
        private JpgFormat() {
            // empty
        }

        public ExtensionFilter getFilter() {
            return this.jpgFilter;
        }

        public void export(JGraph jGraph, File file) throws IOException {
            BufferedImage image = jGraph.toImage();
            if (image == null) {
                throw new IOException("Cannot export blank image");
            }
            ImageIO.write(image, this.jpgFilter.getExtension().substring(1),
                file);
        }

        /**
         * Extension filter used for exporting graphs in jpeg format.
         */
        private final ExtensionFilter jpgFilter = new ExtensionFilter(
            "JPEG image files", Groove.JPG_EXTENSION);

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
            return this.pngFilter;
        }

        public void export(JGraph jGraph, File file) throws IOException {
            BufferedImage image = jGraph.toImage();
            String format = this.pngFilter.getExtension().substring(1);
            if (image == null) {
                throw new IOException("Cannot export blank image");
            }
            ImageIO.write(image, format, file);
        }

        /**
         * Extension filter used for exporting graphs in png format.
         */
        private final ExtensionFilter pngFilter = new ExtensionFilter(
            "PNG image files", Groove.PNG_EXTENSION);

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
            return this.epsFilter;
        }

        public void export(JGraph jGraph, File file) throws IOException {
            // Create a graphics contents on the buffered image
            BufferedImage image = jGraph.toImage();
            if (image == null) {
                throw new IOException("Cannot export blank image");
            }
            // Create an output stream
            OutputStream out = new FileOutputStream(file);
            // minX,minY,maxX,maxY
            EpsGraphics g2d =
                new EpsGraphics("Title", out, 0, 0, image.getWidth(),
                    image.getHeight(), ColorMode.COLOR_RGB);
            g2d.drawImage(jGraph.toImage(), new AffineTransform(), null);
            g2d.close();
        }

        /**
         * Extension filter used for exporting graphs in png format.
         */
        private final ExtensionFilter epsFilter = new ExtensionFilter(
            "EPS image files", Groove.EPS_EXTENSION);

        /** Returns the singleton instance of this class. */
        public static Format getInstance() {
            return instance;
        }

        /** The singleton instance of this class. */
        private static final Format instance = new EpsFormat();
    }

    /** Class implementing the <code>.aut</code> export format. */
    private static class AutFormat implements StructuralFormat {
        /** Empty constructor to ensure singleton usage of the class. */
        private AutFormat() {
            // empty
        }

        public ExtensionFilter getFilter() {
            return this.autFilter;
        }

        /**
         * Exports the jgraph by calling
         * {@link Converter#graphToAut(Graph, PrintWriter)} on
         * the graph contained therein.
         */
        public void export(JGraph jGraph, File file) throws IOException {
            Graph<?,?> graph;
            if (jGraph.getModel() instanceof GraphJModel) {
                graph = ((GraphJModel<?,?>) jGraph.getModel()).getGraph();
            } else {
                graph = ((EditorJModel) jGraph.getModel()).toPlainGraph();
            }
            export(graph, file);
        }

        /**
         * Exports the graph by calling
         * {@link Converter#graphToAut(Graph, PrintWriter)}.
         */
        public void export(Graph<?,?> graph, File file) throws IOException {
            PrintWriter writer = new PrintWriter(new FileWriter(file));
            Converter.graphToAut(graph, writer);
            writer.close();
        }

        /**
         * Extension filter used for exporting graphs in .aut format.
         */
        private final ExtensionFilter autFilter = new ExtensionFilter(
            "CADP .aut files", Groove.AUT_EXTENSION);

        /** Returns the singleton instance of this class. */
        public static Format getInstance() {
            return instance;
        }

        /** The singleton instance of this class. */
        private static final Format instance = new AutFormat();
    }

    /** Class implementing the LaTeX <code>Tikz</code> export format. */
    private static class TikzFormat implements Format {
        /** Empty constructor to ensure singleton usage of the class. */
        private TikzFormat() {
            // empty
        }

        public ExtensionFilter getFilter() {
            return this.tikzFilter;
        }

        /**
         * Exports the graph by calling
         * {@link Converter#graphToTikz(JGraph, PrintWriter)}.
         */
        public void export(JGraph jGraph, File file) throws IOException {
            PrintWriter writer = new PrintWriter(new FileWriter(file));
            Converter.graphToTikz(jGraph, writer);
            writer.close();
        }

        /**
         * Extension filter used for exporting graphs in Tikz format.
         */
        private final ExtensionFilter tikzFilter = new ExtensionFilter(
            "LaTeX tikz files", ".tikz");

        /** Returns the singleton instance of this class. */
        public static Format getInstance() {
            return instance;
        }

        /** The singleton instance of this class. */
        private static final Format instance = new TikzFormat();
    }

    /** Class implementing the <code>.kth</code> export format. */
    private static class KthFormat implements StructuralFormat {
        /** Empty constructor to ensure singleton usage of the class. */
        private KthFormat() {
            // empty
        }

        public ExtensionFilter getFilter() {
            return this.kthFilter;
        }

        /**
         * Exports the jgraph by calling
         * {@link Converter#graphToKth(AspectGraph, PrintWriter)} on
         * the graph contained therein.
         */
        public void export(JGraph jGraph, File file) throws IOException {
            if (jGraph instanceof StateJGraph) {
                Graph<?,?> graph = ((StateJGraph) jGraph).getModel().getGraph();
                export(graph, file);
            } else {
                throw new IOException(
                    "This exporter can only be used with state graphs");
            }

        }

        /**
         * Exports the graph by calling
         * {@link Converter#graphToAut(Graph, PrintWriter)}.
         */
        public void export(Graph<?,?> graph, File file) throws IOException {
            PrintWriter writer = new PrintWriter(new FileWriter(file));
            Converter.graphToKth((AspectGraph) graph, writer);
            writer.close();
        }

        /**
         * Extension filter used for exporting graphs in .kth format.
         */
        private final ExtensionFilter kthFilter = new ExtensionFilter(
            "Simple .kth files", Groove.KTH_EXTENSION);

        /** Returns the singleton instance of this class. */
        public static Format getInstance() {
            return instance;
        }

        /** The singleton instance of this class. */
        private static final Format instance = new KthFormat();
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

    /**
     * Interface for structural formats; that is, formats that do not save
     * layout information.
     */
    public static interface StructuralFormat extends Format {
        /** Exports a GraphShape into this format. */
        void export(Graph<?,?> graph, File file) throws IOException;
    }
}
