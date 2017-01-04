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
package groove.transform;

import groove.grammar.aspect.AspectGraph;
import groove.grammar.aspect.GraphConverter;
import groove.grammar.host.HostGraph;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.HostModel;
import groove.util.parse.FormatException;

/**
 * Object wrapping a host graph,
 * with conversion functionality.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Model {
    /**
     * Constructs a result object from an aspect graph.
     */
    public Model(GrammarModel grammarModel, AspectGraph aspectGraph)
        throws FormatException {
        this(new HostModel(grammarModel, aspectGraph));
    }

    /**
     * Constructs a result object from a host graph.
     */
    public Model(GrammarModel grammarModel, HostGraph hostGraph) {
        this.grammarModel = grammarModel;
        this.hostGraph = hostGraph;
    }

    /**
     * Constructs a result object from a host graph.
     */
    public Model(HostModel hostModel) throws FormatException {
        this.grammarModel = hostModel.getGrammar();
        this.hostGraph = hostModel.toHost();
        this.hostModel = hostModel;
    }

    /** Returns the grammar model to which this model belong. */
    public GrammarModel getGrammar() {
        return this.grammarModel;
    }

    /** Returns the host graph wrapped in this model. */
    public HostGraph getHostGraph() {
        return this.hostGraph;
    }

    /** Returns the host graph model wrapped in this model. */
    public HostModel toHostModel() {
        if (this.hostModel == null) {
            return new HostModel(getGrammar(), toAspectGraph());
        }
        return this.hostModel;
    }

    /** Returns the aspect graph wrapped in this model. */
    public AspectGraph toAspectGraph() {
        if (this.aspectGraph == null) {
            if (this.hostModel == null) {
                this.aspectGraph =
                    GraphConverter.toAspectMap(getHostGraph()).getAspectGraph();
            } else {
                this.aspectGraph = this.hostModel.getSource();
            }
        }
        return this.aspectGraph;
    }

    private final GrammarModel grammarModel;
    private final HostGraph hostGraph;
    private AspectGraph aspectGraph;
    private HostModel hostModel;
}
