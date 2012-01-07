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
package groove.trans;

import groove.rel.LabelVar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Collection of rule elements that together completely determine the
 * relevant part of a rule match.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Anchor extends ArrayList<AnchorKey> {
    /** Constructs an empty anchor. */
    public Anchor() {
        super();
    }

    /** 
     * Constructs an anchor initialised to a given collection of keys.
     * @param c the collection of keys; these are assumed to be of type {@link AnchorKey}
     */
    public Anchor(Collection<? extends Object> c) {
        addKeys(c);
    }

    /** Constructs an anchor initialised to the elements of a rule graph. */
    public Anchor(RuleGraph graph) {
        addKeys(graph.nodeSet());
        addKeys(graph.edgeSet());
        addAll(graph.varSet());
    }

    /**
     * Adds a given object to this anchor,
     * after casting it to an {@link AnchorKey}.
     */
    public void addKey(Object key) {
        add((AnchorKey) key);
    }

    @Override
    public boolean add(AnchorKey e) {
        boolean result = !contains(e);
        if (result) {
            super.add(e);
            if (e.getAnchorKind() == AnchorKind.NODE) {
                addAll(AnchorKind.node(e).getVars());
            } else if (e.getAnchorKind() == AnchorKind.EDGE) {
                addAll(AnchorKind.edge(e).getVars());
                addKey(AnchorKind.edge(e).source());
                addKey(AnchorKind.edge(e).target());
            }
        }
        return result;
    }

    /**
     * Adds all elements of a given collection to this anchor,
     * assuming all of them to be {@link AnchorKey}s.
     */
    public void addKeys(Collection<? extends Object> keys) {
        for (Object key : keys) {
            add((AnchorKey) key);
        }
    }

    /** Returns the set of node keys in this anchor. */
    public Set<RuleNode> nodeSet() {
        if (this.nodeSet == null) {
            initSets();
        }
        return this.nodeSet;
    }

    /** Returns the set of edge keys in this anchor. */
    public Set<DefaultRuleEdge> edgeSet() {
        if (this.edgeSet == null) {
            initSets();
        }
        return this.edgeSet;
    }

    /** Returns the set of label variable keys in this anchor. */
    public Set<LabelVar> varSet() {
        if (this.varSet == null) {
            initSets();
        }
        return this.varSet;
    }

    /** Initialises the node, edge and label sets. */
    private void initSets() {
        this.nodeSet = new HashSet<RuleNode>();
        this.edgeSet = new HashSet<DefaultRuleEdge>();
        this.varSet = new HashSet<LabelVar>();
        for (AnchorKey key : this) {
            switch (key.getAnchorKind()) {
            case NODE:
                this.nodeSet.add(AnchorKind.node(key));
                break;
            case EDGE:
                this.edgeSet.add(AnchorKind.edge(key));
                break;
            case LABEL:
                this.varSet.add(AnchorKind.label(key));
            }
        }
    }

    private Set<RuleNode> nodeSet;
    private Set<DefaultRuleEdge> edgeSet;
    private Set<LabelVar> varSet;
}
