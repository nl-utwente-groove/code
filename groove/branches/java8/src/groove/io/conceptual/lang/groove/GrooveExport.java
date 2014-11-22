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
import groove.gui.SimulatorModel;
import groove.io.conceptual.Timer;
import groove.io.conceptual.configuration.Config;
import groove.io.conceptual.lang.Export;
import groove.io.external.PortException;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/** Resource ready to be exported to GROOVE native format, in the form
 * of a set of graphs (possibly including type graph, host graph and constraints).
 */
public class GrooveExport extends Export {
    /**
     * Constructs a new, initially empty export, for a given configuration and name space.
     * @param cfg export configuration
     * @param simModel optional simulator model, if the export is GUI-based
     * @param namespace name space for the exported resource (prepended to all graph names)
     */
    public GrooveExport(Config cfg, SimulatorModel simModel, String namespace) {
        this.m_cfg = cfg;
        this.m_simModel = simModel;
        this.m_namespace = namespace;

        for (GraphRole role : GraphRole.values()) {
            if (ResourceKind.isResource(role)) {
                this.m_graphs.put(role, new HashMap<>());
            }
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

    /** Indicates if this export has a non-empty name space.
     */
    public boolean hasNamespace() {
        return getNamespace() != null && !getNamespace().isEmpty();
    }

    /** Returns the name space of this resource.
     * The name space is prepended to all names of graphs in this resource.
     */
    public String getNamespace() {
        return this.m_namespace;
    }

    /** Name space of this resource. */
    private final String m_namespace;

    /** Returns the namespace-prefixed version of a name. */
    protected String prefix(String name) {
        return GrooveUtil.getSafeResource(hasNamespace() ? getNamespace() + QualName.SEPARATOR
            + name : name);
    }

    @Override
    public boolean export() throws PortException {
        int timer = Timer.start("Groove save");
        for (GraphRole role : this.m_graphs.keySet()) {
            Collection<AspectGraph> graphs =
                this.m_graphs.get(role)
                    .values()
                    .stream()
                    .map(g -> g.toAspectGraph())
                    .collect(Collectors.toList());
            try {
                this.m_simModel.getGrammar().getStore().put(ResourceKind.toResource(role), graphs);
                this.m_simModel.doRefreshGrammar();
            } catch (IOException e) {
                throw new PortException(e);
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
        return this.m_graphs.get(graphRole).containsKey(prefix(name));
    }

    /**
     * Returns a graph container for a given name and role,
     * creating it first if it does not yet exist.
     * @param name name of the desired graph
     * @param graphRole role of the desired graph
     * @return previously or freshly generated graph with these characteristics
     */
    public PreGraph getGraph(String name, GraphRole graphRole) {
        String nsName = prefix(name);
        PreGraph result = this.m_graphs.get(graphRole).get(nsName);
        if (result == null) {
            result = new PreGraph(nsName, graphRole);
            this.m_graphs.get(graphRole).put(nsName, result);
        }
        assert result.getGraphRole() == graphRole;
        return result;
    }

    /** Returns a mapping from graph roles to a name-graph map of graphs with that role. */
    public Map<GraphRole,HashMap<String,PreGraph>> getGraphs() {
        return this.m_graphs;
    }

    /** Set of graphs of which this resource consists. */
    private final Map<GraphRole,HashMap<String,PreGraph>> m_graphs = new HashMap<>();

}
