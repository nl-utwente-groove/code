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
package groove.abstraction.pattern.match;

import groove.abstraction.pattern.match.Matcher.Search;
import groove.abstraction.pattern.shape.TypeNode;
import groove.abstraction.pattern.trans.RuleNode;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * A search item that searches an image for pattern node.
 * @author Eduardo Zambon
 */
public final class PatternNodeSearchItem extends SearchItem {

    /** The node to be matched. */
    private final RuleNode node;
    /** The type node to be matched. */
    private final TypeNode type;
    /** Singleton set of nodes. */
    private final Set<RuleNode> boundNodes;
    /** The index of the source in the search. */
    private int nodeIx;

    /**
     * Creates a search item for a given typed node.
     * @param node the node to be matched
     */
    public PatternNodeSearchItem(RuleNode node) {
        this.node = node;
        this.type = node.getType();
        this.boundNodes = Collections.singleton(node);
    }

    /**
     * Returns the node for which this item tests.
     */
    @Override
    public Collection<RuleNode> bindsNodes() {
        return this.boundNodes;
    }

    /**
     * This implementation first attempts to compare node type labels, if
     * the other search item is also an {@link PatternNodeSearchItem}; otherwise,
     * it delegates to super.
     */
    @Override
    public int compareTo(SearchItem other) {
        int result = 0;
        if (other instanceof PatternNodeSearchItem) {
            result = this.type.compareTo(((PatternNodeSearchItem) other).type);
        }
        if (result == 0) {
            return super.compareTo(other);
        } else {
            return result;
        }
    }

    @Override
    Record createRecord(Search search) {
        return null;
    }

    /** This method returns the hash code of the node type as rating. */
    @Override
    int getRating() {
        return this.type.hashCode();
    }

    @Override
    void activate(Matcher matcher) {
        this.nodeIx = matcher.getNodeIx(this.node);
    }

    /** Returns the node for which this item tests. */
    public RuleNode getNode() {
        return this.node;
    }

}
