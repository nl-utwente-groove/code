/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.io.external;

import groove.graph.Graph;
import groove.gui.dialog.ErrorDialog;
import groove.gui.dialog.SaveDialog;
import groove.gui.jgraph.GraphJGraph;
import groove.io.ExtensionFilter;
import groove.io.GrooveFileChooser;
import groove.io.external.FormatPorter.Kind;
import groove.io.external.format.AutPorter;
import groove.io.external.format.FsmExporter;
import groove.io.external.format.KthExporter;
import groove.io.external.format.NativePorter;
import groove.io.external.format.RasterExporter;
import groove.io.external.format.TikzExporter;
import groove.io.external.format.VectorExporter;
import groove.trans.ResourceKind;
import groove.view.GraphBasedModel;
import groove.view.ResourceModel;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.jgraph.JGraph;

/**
 * Class used to export various resources and graphs to external formats.
 * Export gets initiated by ExportAction.
 * @author Harold Bruijntjes
 * @version $Revision $
 */
public class Exporter {
    /** Returns the singleton instance of this class. */
    public static Exporter instance() {
        return instance;
    }

    private static final Exporter instance = new Exporter();

    /**
     * Private constructor for singleton object.
     */
    private Exporter() {
        // empty
    }

    /**
     * Exports object contained in exportable. Parent is used as parent of save dialog
     * @param parent parent of save dialog; may be {@code null}
     * @param exportable Container with object to export
     */
    public void doExport(Component parent, Exportable exportable) {
        //JGraph first, then graph, then resource
        List<Format> formats = new ArrayList<Format>();
        for (FormatExporter rf : exporters) {
            if (exportable.containsKind(rf.getFormatKind())) {
                formats.addAll(rf.getSupportedFormats());
            }
        }

        // No exporter available, stop
        if (formats.size() == 0) {
            return;
        }

        GrooveFileChooser chooser = getChooser(formats);
        chooser.setSelectedFile(new File(exportable.getName()));
        File selectedFile = SaveDialog.show(chooser, parent, null);
        // now save, if so required
        if (selectedFile != null) {
            try {
                // Get exporter
                FormatFilter filter = (FormatFilter) chooser.getFileFilter();
                Format format = filter.getFormat();
                FormatPorter e = filter.getFormat().getFormatter();
                ((FormatExporter) e).doExport(selectedFile, format, exportable);
            } catch (PortException e) {
                showErrorDialog(parent, e, "Error while exporting to "
                    + selectedFile);
            }
        }
    }

    /** Creates chooser based on list of formats. */
    private GrooveFileChooser getChooser(List<Format> formats) {
        GrooveFileChooser result = null;
        List<ExtensionFilter> filters = new ArrayList<ExtensionFilter>();

        for (Format f : formats) {
            filters.add(new FormatFilter(f));
        }

        result = GrooveFileChooser.getFileChooser(filters);
        result.setFileFilter(filters.get(0));

        return result;
    }

    /**
     * Creates and shows an {@link ErrorDialog} for a given message and
     * exception.
     */
    private void showErrorDialog(Component parent, Throwable exc,
            String message, Object... args) {
        new ErrorDialog(parent, String.format(message, args), exc).setVisible(true);
    }

    /**
     * Get suitable export format for a given file.
      * Backwards compatibility function for now.
     */
    static public Format getAcceptingFormat(Graph<?,?> graph, File file) {
        Format result = null;
        outer: for (FormatExporter rf : exporters) {
            if (rf.getFormatKind() == Kind.GRAPH) {
                for (Format format : rf.getSupportedFormats()) {
                    for (String ext : format.getExtensions()) {
                        if (file.getName().endsWith(ext)) {
                            result = format;
                            break outer;
                        }
                    }
                }
            }
        }
        return result;
    }

    //TODO: ResourceFormatter too generic, use subtype for exporters
    /** List of available exporters. */
    private static final List<FormatExporter> exporters =
        new ArrayList<FormatExporter>();
    static {
        exporters.add(NativePorter.getInstance());
        exporters.add(RasterExporter.getInstance());
        exporters.add(VectorExporter.getInstance());
        exporters.add(AutPorter.getInstance());
        exporters.add(KthExporter.getInstance());
        exporters.add(FsmExporter.getInstance());
        exporters.add(TikzExporter.getInstance());
        //        exporters.add(EcorePorter.instance());
        //        exporters.add(GxlPorter.instance());
        //        exporters.add(DotPorter.getInstance());
    }

    /**
     * Wrapper class to transfer object to be exported form ExportAction to Exporter.
     * Can wrap either {@link Graph}s, {@link JGraph}s or {@link ResourceModel}s.
     * @author Harold
     * @version $Revision $
     */
    public static class Exportable {
        private final EnumSet<FormatPorter.Kind> porterKinds;
        private final String name;
        private final Graph<?,?> graph;
        private final GraphJGraph jGraph;
        private final ResourceModel<?> model;

        /** Constructs an exportable for a given {@link JGraph}. */
        public Exportable(Graph<?,?> graph) {
            this.porterKinds = EnumSet.of(Kind.GRAPH);
            this.name = graph.getName();
            this.jGraph = null;
            this.graph = graph;
            this.model = null;
        }

        /** Constructs an exportable for a given {@link JGraph}. */
        public Exportable(GraphJGraph jGraph) {
            this.porterKinds = EnumSet.of(Kind.GRAPH, Kind.JGRAPH);
            this.jGraph = jGraph;
            this.graph = jGraph.getModel().getGraph();
            this.model = null;
            this.name = this.graph.getName();
        }

        /** Constructs an exportable for a given {@link ResourceModel}. */
        public Exportable(ResourceModel<?> model) {
            this.porterKinds = EnumSet.of(Kind.RESOURCE);
            if (model.getKind().isGraphBased()) {
                this.porterKinds.add(Kind.GRAPH);
                this.graph = ((GraphBasedModel<?>) model).getSource();
            } else {
                this.graph = null;
            }

            this.name = model.getFullName();
            this.model = model;
            this.jGraph = null;
        }

        /** Constructs an exportable for a given {@link ResourceModel} that is displayed in a {@link JGraph}. */
        public Exportable(GraphJGraph jGraph, ResourceModel<?> model) {
            this.porterKinds =
                EnumSet.of(Kind.GRAPH, Kind.JGRAPH, Kind.RESOURCE);
            this.name = model.getFullName();
            this.jGraph = jGraph;
            this.graph = jGraph.getModel().getGraph();
            this.model = model;
        }

        /** Indicates if this exportable contains an object of a given porter kind. */
        public boolean containsKind(Kind kind) {
            return this.porterKinds.contains(kind);
        }

        /** Returns the name of the object wrapped by this exportable. */
        public String getName() {
            return this.name;
        }

        /** Returns the {@link Graph} wrapped by this exportable, if any. */
        public Graph<?,?> getGraph() {
            return this.graph;
        }

        /** Returns the {@link JGraph} wrapped by this exportable, if any. */
        public GraphJGraph getJGraph() {
            return this.jGraph;
        }

        /** Returns the resource kind of the model wrapped by this exportable, if any. */
        public ResourceKind getKind() {
            return getModel() == null ? null : getModel().getKind();
        }

        /** Returns the {@link ResourceModel} wrapped by this exportable, if any. */
        public ResourceModel<?> getModel() {
            return this.model;
        }
    }
}
