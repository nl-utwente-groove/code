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
package groove.grammar.rule;

import groove.grammar.AnchorKind;
import groove.graph.EdgeComparator;
import groove.graph.NodeComparator;

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
public class Anchor extends ArrayList<AnchorKey> implements Comparable<Anchor> {
    /** Constructs an empty anchor. */
    public Anchor() {
        super();
    }

    /** 
     * Constructs an anchor initialised to a given collection of keys.
     * @param keys the collection of keys
     */
    public Anchor(Collection<? extends AnchorKey> keys) {
        super(keys.size());
        for (AnchorKey k : keys) {
            add(k);
        }
    }

    /** Constructs an anchor initialised to the elements of a rule graph. */
    public Anchor(RuleGraph graph) {
        addAll(graph.nodeSet());
        addAll(graph.edgeSet());
        addAll(graph.varSet());
    }

    @Override
    public boolean add(AnchorKey e) {
        // make sure there are no duplicates
        boolean result = !contains(e);
        if (result) {
            super.add(e);
            if (e.getAnchorKind() == AnchorKind.NODE) {
                addAll(AnchorKind.node(e).getVars());
            } else if (e.getAnchorKind() == AnchorKind.EDGE) {
                addAll(AnchorKind.edge(e).getVars());
                add(AnchorKind.edge(e).source());
                add(AnchorKind.edge(e).target());
            }
        }
        return result;
    }

    /** Returns the set of node keys in this anchor. */
    public Set<RuleNode> nodeSet() {
        if (this.nodeSet == null) {
            initSets();
        }
        return this.nodeSet;
    }

    /** Returns the set of edge keys in this anchor. */
    public Set<RuleEdge> edgeSet() {
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
        this.nodeSet = new HashSet<>();
        this.edgeSet = new HashSet<>();
        this.varSet = new HashSet<>();
        for (AnchorKey key : this) {
            switch (key.getAnchorKind()) {
            case NODE:
                this.nodeSet.add(AnchorKind.node(key));
                assert containsAll(AnchorKind.node(key).getVars());
                break;
            case EDGE:
                this.edgeSet.add(AnchorKind.edge(key));
                break;
            case LABEL:
                this.varSet.add(AnchorKind.label(key));
            }
        }
    }

    @Override
    public int compareTo(Anchor other) {
        int result = size() - other.size();
        if (result != 0) {
            return result;
        }
        for (int i = 0; i < size(); i++) {
            result = compare(get(i), other.get(i));
            if (result != 0) {
                return result;
            }
        }
        return result;
    }

    private int compare(AnchorKey one, AnchorKey two) {
        int result = one.getAnchorKind().compareTo(two.getAnchorKind());
        if (result != 0) {
            return result;
        }
        switch (one.getAnchorKind()) {
        case EDGE:
            result = EdgeComparator.instance().compare(AnchorKind.edge(one), AnchorKind.edge(two));
            break;
        case LABEL:
            result = AnchorKind.label(one).compareTo(AnchorKind.label(two));
            break;
        case NODE:
            result = NodeComparator.instance().compare(AnchorKind.node(one), AnchorKind.node(two));
        }
        return result;
    }

    private Set<RuleNode> nodeSet;
    private Set<RuleEdge> edgeSet;
    private Set<LabelVar> varSet;
}
