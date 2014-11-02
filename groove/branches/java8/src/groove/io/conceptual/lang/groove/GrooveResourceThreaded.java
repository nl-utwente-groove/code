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
import groove.gui.display.DisplayKind;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.AspectJModel;
import groove.io.conceptual.configuration.Config;
import groove.io.conceptual.graph.AbsGraph;
import groove.io.conceptual.lang.ExportException;

import java.io.IOException;
import java.util.List;

import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

/**
 * Just like GrooveResource, but with a half-baked attempt to get some progress dialog showing.
 * It's slow and not very safe, but if the lack of feedback annoys too much this class may be
 * an alternative to the silent GrooveResource.
 * Class instance can only be used once.
 * @author s0141844
 * @version $Revision $
 */
public class GrooveResourceThreaded extends GrooveResource {
    // Keep track of progress so far.
    private int progress = 0;

    public GrooveResourceThreaded(Config cfg, Simulator sim, String namespace) {
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
        for (GraphRole role : this.m_graphs.keySet()) {
            max += this.m_graphs.get(role).size();
        }
        final ProgressMonitor progressMonitor =
            new ProgressMonitor(this.m_sim.getFrame(), "Importing graphs", "", 0, max);

        SwingWorker<Boolean,AspectGraph> sw = new SwingWorker<Boolean,AspectGraph>() {
            private ProgressMonitor dlg = progressMonitor;

            @SuppressWarnings("unchecked")
            @Override
            protected Boolean doInBackground() throws Exception {
                for (GraphRole role : GrooveResourceThreaded.this.m_graphs.keySet()) {
                    for (GrammarGraph graph : GrooveResourceThreaded.this.m_graphs.get(role)
                        .values()) {
                        if (this.dlg.isCanceled() || this.isCancelled()) {
                            return false;
                        }
                        AbsGraph absGraph = graph.getGraph();
                        String safeName =
                            GrooveUtil.getSafeResource(GrooveResourceThreaded.this.m_namespace
                                + QualName.SEPARATOR + graph.getGraphName());
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
            protected void process(List<AspectGraph> chunks) {
                for (AspectGraph chunk : chunks) {
                    // Do the actual work here in GUI thread for a bit more safety
                    importGraph(chunk);

                    GrooveResourceThreaded.this.progress++;

                    int percent =
                        GrooveResourceThreaded.this.progress * 100 / this.dlg.getMaximum();

                    String message =
                        String.format("Graph %d of %d (%d%%).\n",
                            GrooveResourceThreaded.this.progress,
                            this.dlg.getMaximum(),
                            percent);
                    this.dlg.setNote(message);
                    this.dlg.setProgress(GrooveResourceThreaded.this.progress);

                    GrooveResourceThreaded.this.m_sim.getFrame().validate();
                    GrooveResourceThreaded.this.m_sim.getFrame().repaint();
                }
            }

            private void importGraph(AspectGraph chunk) {
                try {
                    GrooveResourceThreaded.this.m_simModel.doAdd(chunk, false);

                    if (GrooveResourceThreaded.this.m_layouter != null) {
                        AspectJGraph jGraph =
                            new AspectJGraph(GrooveResourceThreaded.this.m_sim, DisplayKind.TYPE,
                                false);
                        AspectJModel model = jGraph.newModel();
                        model.loadGraph(chunk);
                        try {
                            jGraph.setModel(model);
                            GrooveResourceThreaded.this.m_layouter.newInstance(jGraph).start();
                            //m_simModel.synchronize();
                        } catch (Exception e) {
                            // For some reason NullPointerException when filtering and some label keys are null
                            // TODO: figure out what goes wrong here
                            // If crash occurs here simulator seems to chug along just fine
                        }
                    }
                } catch (IOException e) {
                    //throw new ExportException(e);
                }
            }
        };

        sw.execute();
    }
}
