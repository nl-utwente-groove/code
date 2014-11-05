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
import groove.gui.SimulatorModel;
import groove.io.conceptual.Timer;
import groove.io.conceptual.configuration.Config;
import groove.io.conceptual.graph.AbsGraph;
import groove.io.conceptual.lang.ExportException;
import groove.io.conceptual.lang.ExportableResource;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** Resource ready to be exported to GROOVE native format, in the form
 * of a set of graphs (possibly including type graph, host graph and constraints).
 */
public class GrooveResource extends ExportableResource {
    /**
     * Constructs a new, initially empty resource, for a given configuration and name space.
     * @param cfg export configuration
     * @param simModel optional simulator model, if the export is GUI-based
     * @param namespace name space for the exported resource (prepended to all graph names)
     */
    public GrooveResource(Config cfg, SimulatorModel simModel, String namespace) {
        this.m_cfg = cfg;
        this.m_simModel = simModel;
        this.m_namespace = namespace;

        for (GraphRole role : GraphRole.values()) {
            this.m_graphs.put(role, new HashMap<String,GrammarGraph>());
        }
    }

    /** Export configuration for this resource. */
    private final Config m_cfg;

    /** Returns the optional simulator model to which this resource is to be exported. */
    public SimulatorModel getSimulatorModel() {
        return this.m_simModel;
    }

    /** Optional simulator model to which the generated sources should be added. */
    private final SimulatorModel m_simModel;

    /** Returns the name space of this resource.
     * The name space is prepended to all names of graphs in this resource.
     */
    public String getNamespace() {
        return this.m_namespace;
    }

    /** Name space of this resource. */
    private final String m_namespace;

    @Override
    public boolean export() throws ExportException {
        int timer = Timer.start("Groove save");
        for (GraphRole role : this.m_graphs.keySet()) {
            for (GrammarGraph graph : this.m_graphs.get(role).values()) {
                AbsGraph absGraph = graph.getGraph();
                String safeName =
                    GrooveUtil.getSafeResource(this.m_namespace + QualName.SEPARATOR
                        + graph.getGraphName());
                AspectGraph aspectGraph = absGraph.toAspectGraph(safeName, graph.getGraphRole());

                try {
                    this.m_simModel.getGrammar()
                        .getStore()
                        .put(aspectGraph.getKind(), Collections.singleton(aspectGraph), false);
                    this.m_simModel.doRefreshGrammar();
                } catch (IOException e) {
                    throw new ExportException(e);
                }
            }
        }
        Timer.stop(timer);
        return true;
    }

    /** Returns the configuration for this resource. */
    public Config getConfig() {
        return this.m_cfg;
    }

    /** Tests if a graph with a given name and role is in this resource. */
    public boolean hasGraph(String name, GraphRole graphRole) {
        return this.m_graphs.get(graphRole).containsKey(name);
    }

    /**
     * Returns a graph container for a given name and role,
     * creating it first if it does not yet exist.
     * @param name name of the desired graph
     * @param graphRole role of the desired graph
     * @return previously or freshly generated graph with these characteristics
     */
    public GrammarGraph getGraph(String name, GraphRole graphRole) {
        GrammarGraph result = this.m_graphs.get(graphRole).get(name);
        if (result == null) {
            result = new GrammarGraph(name, graphRole);
            this.m_graphs.get(graphRole).put(name, result);
        }
        assert result.getGraphRole() == graphRole;
        //            if (resultGraph.getGraphRole() != graphRole) {
        //                return null;
        //            }
        return result;
    }

    /** Returns a mapping from graph roles to a name-graph map of graphs with that role. */
    public Map<GraphRole,HashMap<String,GrammarGraph>> getGraphs() {
        return this.m_graphs;
    }

    /** Set of graphs of which this resource consists. */
    private final Map<GraphRole,HashMap<String,GrammarGraph>> m_graphs =
        new HashMap<GraphRole,HashMap<String,GrammarGraph>>();

}
