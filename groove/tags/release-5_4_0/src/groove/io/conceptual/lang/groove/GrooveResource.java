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
import groove.grammar.model.ResourceKind;
import groove.graph.GraphRole;
import groove.gui.Simulator;
import groove.gui.SimulatorModel;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.AspectJModel;
import groove.gui.layout.Layouter;
import groove.io.conceptual.Timer;
import groove.io.conceptual.configuration.Config;
import groove.io.conceptual.graph.AbsGraph;
import groove.io.conceptual.lang.ExportException;
import groove.io.conceptual.lang.ExportableResource;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GrooveResource extends ExportableResource {
    protected Config m_cfg;
    protected SimulatorModel m_simModel;

    protected Simulator m_sim;
    protected Layouter m_layouter;

    protected String m_namespace;

    protected Map<GraphRole,HashMap<String,GrammarGraph>> m_graphs =
        new HashMap<GraphRole,HashMap<String,GrammarGraph>>();

    public GrooveResource(Config cfg, SimulatorModel simModel, String namespace) {
        this.m_cfg = cfg;
        this.m_simModel = simModel;

        this.m_namespace = namespace;

        for (GraphRole role : GraphRole.values()) {
            this.m_graphs.put(role, new HashMap<String,GrammarGraph>());
        }
    }

    public void setLayouter(Simulator sim, Layouter layouter) {
        this.m_sim = sim;
        this.m_layouter = layouter;
    }

    public void count() {
        int constraintCount = 0;
        for (GraphRole role : this.m_graphs.keySet()) {
            for (GrammarGraph graph : this.m_graphs.get(role).values()) {
                AbsGraph absGraph = graph.getGraph();
                int nodes = absGraph.getNodes().size();
                int edges = absGraph.getEdges().size();
                if (graph.getGraphName().startsWith("constraint")
                    || graph.getGraphName().startsWith("Default")) {
                    constraintCount++;
                } else {
                    System.out.println("Graph " + graph.getGraphName()
                        + ", nodes: " + nodes + ", edges: " + edges);
                }
            }
        }
        System.out.println("#constraintCount: " + constraintCount);
    }

    public Map<GraphRole,HashMap<String,GrammarGraph>> getGraphs() {
        return this.m_graphs;
    }

    @Override
    public boolean export() throws ExportException {
        int timer = Timer.start("Groove save");
        for (GraphRole role : this.m_graphs.keySet()) {
            for (GrammarGraph graph : this.m_graphs.get(role).values()) {
                AbsGraph absGraph = graph.getGraph();
                String safeName =
                    GrooveUtil.getSafeResource(this.m_namespace
                        + QualName.SEPARATOR + graph.getGraphName());
                AspectGraph aspectGraph =
                    absGraph.toAspectGraph(safeName, graph.getGraphRole());

                try {
                    switch (graph.getGraphRole()) {
                    case TYPE:
                        //m_simModel.doAddGraph(ResourceKind.TYPE, aspectGraph, false);
                        this.m_simModel.getGrammar().getStore().putGraphs(
                            ResourceKind.TYPE,
                            Collections.singleton(aspectGraph), false);
                        break;
                    case HOST:
                        //m_simModel.doAddGraph(ResourceKind.HOST, aspectGraph, false);
                        this.m_simModel.getGrammar().getStore().putGraphs(
                            ResourceKind.HOST,
                            Collections.singleton(aspectGraph), false);
                        break;
                    case RULE:
                        //m_simModel.doAddGraph(ResourceKind.RULE, aspectGraph, false);
                        this.m_simModel.getGrammar().getStore().putGraphs(
                            ResourceKind.RULE,
                            Collections.singleton(aspectGraph), false);
                        break;
                    }

                    //Timer.stop(timer);
                    this.m_simModel.doRefreshGrammar();
                    //Timer.cont(timer);

                    if (this.m_layouter != null) {
                        AspectJGraph jGraph =
                            new AspectJGraph(this.m_sim,
                                groove.gui.display.DisplayKind.TYPE, false);
                        AspectJModel model = jGraph.newModel();
                        model.loadGraph(aspectGraph);
                        try {
                            jGraph.setModel(model);
                            this.m_layouter.newInstance(jGraph).start();
                            //m_simModel.synchronize();
                        } catch (Exception e) {
                            // For some reason NullPointerException when filtering and some label keys are null
                            // TODO: figure out what goes wrong here
                            // If crash occurs here simulator seems to chug along just fine
                        }
                    }

                } catch (IOException e) {
                    throw new ExportException(e);
                }
            }
        }
        Timer.stop(timer);
        return true;
    }

    // Silly helper function to delete resources that would have been generated by export
    public void delete() throws ExportException {
        for (GraphRole role : this.m_graphs.keySet()) {
            for (GrammarGraph graph : this.m_graphs.get(role).values()) {
                String safeName =
                    GrooveUtil.getSafeResource(this.m_namespace
                        + QualName.SEPARATOR + graph.getGraphName());
                try {
                    switch (graph.getGraphRole()) {
                    case TYPE:
                        this.m_simModel.doDelete(ResourceKind.TYPE,
                            Collections.singleton(safeName));
                        break;
                    case HOST:
                        this.m_simModel.doDelete(ResourceKind.HOST,
                            Collections.singleton(safeName));
                        break;
                    case RULE:
                        this.m_simModel.doDelete(ResourceKind.RULE,
                            Collections.singleton(safeName));
                        break;
                    }
                } catch (IOException e) {
                    throw new ExportException(e);
                }
            }
        }

    }

    public Config getConfig() {
        return this.m_cfg;
    }

    public boolean hasGraph(String name, GraphRole graphRole) {
        return this.m_graphs.get(graphRole).containsKey(name);
    }

    public GrammarGraph getGraph(String name, GraphRole graphRole) {
        if (this.m_graphs.containsKey(name)) {
            GrammarGraph resultGraph = this.m_graphs.get(graphRole).get(name);
            if (resultGraph.getGraphRole() != graphRole) {
                return null;
            }
            return resultGraph;
        }

        GrammarGraph newGraph = new GrammarGraph(name, graphRole);
        this.m_graphs.get(graphRole).put(name, newGraph);

        return newGraph;
    }
}
