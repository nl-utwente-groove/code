/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.grammar;

import nl.utwente.groove.grammar.host.AnchorValue;
import nl.utwente.groove.grammar.host.HostEdge;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.grammar.rule.AnchorKey;
import nl.utwente.groove.grammar.rule.LabelVar;
import nl.utwente.groove.grammar.rule.RuleEdge;
import nl.utwente.groove.grammar.rule.RuleNode;
import nl.utwente.groove.grammar.type.TypeElement;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.graph.EdgeComparator;
import nl.utwente.groove.graph.NodeComparator;

/**
 * Kind of anchor keys and images.
 */
public enum AnchorKind implements Comparable<AnchorKind> {
    /** A node anchor, with {@link RuleNode} as key and {@link HostNode} as value. */
    NODE,
    /** An edge anchor, with {@link RuleEdge} as key and {@link HostEdge} as value. */
    EDGE,
    /** A label anchor, with {@link LabelVar} as key and {@link TypeLabel} as value. */
    LABEL;

    /**
     * Establishes a total ordering of anchor values.
     * For the meaning of the return values see {@link Comparable#compareTo(Object)}.
     */
    public static int compare(AnchorValue one, AnchorValue two) {
        int result = one.getAnchorKind()
            .compareTo(one.getAnchorKind());
        if (result == 0) {
            result = switch (one.getAnchorKind()) {
            case NODE -> NodeComparator.instance()
                .compare(AnchorKind.node(one), AnchorKind.node(two));
            case EDGE -> EdgeComparator.instance()
                .compare(AnchorKind.edge(one), AnchorKind.edge(two));
            case LABEL -> AnchorKind.label(one)
                .compareTo(AnchorKind.label(two));
            };
        }
        return result;
    }

    /** Casts a given anchor key to a node, if it is of node kind. */
    public static RuleNode node(AnchorKey key) {
        return key.getAnchorKind() == NODE ? (RuleNode) key : null;
    }

    /** Casts a given anchor key to an edge, if it is of edge kind. */
    public static RuleEdge edge(AnchorKey key) {
        return key.getAnchorKind() == EDGE ? (RuleEdge) key : null;
    }

    /** Casts a given anchor key to a label variable, if it is of label kind. */
    public static LabelVar label(AnchorKey key) {
        return key.getAnchorKind() == LABEL ? (LabelVar) key : null;
    }

    /** Casts a given anchor image to a node, if it is of node kind. */
    public static HostNode node(AnchorValue key) {
        return key.getAnchorKind() == NODE ? (HostNode) key : null;
    }

    /** Casts a given anchor image to an edge, if it is of edge kind. */
    public static HostEdge edge(AnchorValue key) {
        return key.getAnchorKind() == EDGE ? (HostEdge) key : null;
    }

    /** Casts a given anchor image to a type label, if it is of label kind. */
    public static TypeElement label(AnchorValue key) {
        return key.getAnchorKind() == LABEL ? (TypeElement) key : null;
    }
}