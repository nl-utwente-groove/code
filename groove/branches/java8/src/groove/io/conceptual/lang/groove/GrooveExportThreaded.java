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
package groove.io.conceptual.lang.groove;

import groove.grammar.QualName;
import groove.grammar.aspect.AspectGraph;
import groove.graph.GraphRole;
import groove.gui.Simulator;
import groove.io.conceptual.configuration.Config;
import groove.io.conceptual.graph.AbsGraph;
import groove.io.conceptual.lang.ExportException;

import java.io.IOException;
import java.util.List;

import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

/**
 * Just like {@link GrooveExport}, but with a half-baked attempt to get some progress dialog showing.
 * It's slow and not very safe, but if the lack of feedback annoys too much this class may be
 * an alternative to the silent GrooveResource.
 * Class instance can only be used once.
 * @author Harold Bruijntjes
 * @version $Revision $
 */
public class GrooveExportThreaded extends GrooveExport {
    // Keep track of progress so far.
    private int progress = 0;
    private final Simulator m_sim;

    /** Constructs an instance of this class, for a given simulator. */
    public GrooveExportThreaded(Config cfg, Simulator sim, String namespace) {
        super(cfg, sim.getModel(), namespace);
        this.m_sim = sim;
    }

    @Override
    public boolean export() throws ExportException {
        worker();
        return false;
    }

    private void worker() {
        int max = 0;
        this.progress = 0;
        for (GraphRole role : GraphRole.values()) {
            max += getGraphs().get(role).size();
        }
        final ProgressMonitor progressMonitor =
            new ProgressMonitor(this.m_sim.getFrame(), "Importing graphs", "", 0, max);

        SwingWorker<Boolean,AspectGraph> sw = new SwingWorker<Boolean,AspectGraph>() {
            private ProgressMonitor dlg = progressMonitor;

            @Override
            protected Boolean doInBackground() throws Exception {
                for (GraphRole role : GraphRole.values()) {
                    for (GrammarGraph graph : getGraphs().get(role).values()) {
                        if (this.dlg.isCanceled() || this.isCancelled()) {
                            return false;
                        }
                        AbsGraph absGraph = graph.getGraph();
                        String safeName =
                            GrooveUtil.getSafeResource(getNamespace() + QualName.SEPARATOR
                                + graph.getGraphName());
                        AspectGraph aspectGraph =
                            absGraph.toAspectGraph(safeName, graph.getGraphRole());

                        // importGraph here puts work on worker thread and updates GUI better.
                        // Alas, it's not thread safe, so putting it in process() instead
                        //importGraph(new Pair<AspectGraph,ResourceKind>(aspectGraph, kind));
                        publish(aspectGraph);

                        // Yes, this is ugly, but it gets the progress dialog to redraw a little
                        // Put importGraph on worker thread and this is not needed
                        Thread.sleep(100);
                    }
                }
                return true;
            }

            @Override
            protected void done() {
                this.dlg.close();
            }

            @Override
            protected void process(List<AspectGraph> graphs) {
                for (AspectGraph chunk : graphs) {
                    // Do the actual work here in GUI thread for a bit more safety
                    importGraph(chunk);

                    GrooveExportThreaded.this.progress++;

                    int percent = GrooveExportThreaded.this.progress * 100 / this.dlg.getMaximum();

                    String message =
                        String.format("Graph %d of %d (%d%%).\n",
                            GrooveExportThreaded.this.progress,
                            this.dlg.getMaximum(),
                            percent);
                    this.dlg.setNote(message);
                    this.dlg.setProgress(GrooveExportThreaded.this.progress);

                    GrooveExportThreaded.this.m_sim.getFrame().validate();
                    GrooveExportThreaded.this.m_sim.getFrame().repaint();
                }
            }

            private void importGraph(AspectGraph graph) {
                try {
                    getSimulatorModel().doAdd(graph, false);
                } catch (IOException e) {
                    //throw new ExportException(e);
                }
            }
        };

        sw.execute();
    }
}
