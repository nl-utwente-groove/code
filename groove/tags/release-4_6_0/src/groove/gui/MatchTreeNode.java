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
package groove.gui;

import groove.io.HTMLConverter;
import groove.lts.GraphState;
import groove.lts.MatchResult;
import groove.lts.RuleTransition;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Tree node wrapping a graph transition.
 */
class MatchTreeNode extends DefaultMutableTreeNode {
    /**
     * Creates a new tree node based on a given graph transition. The node cannot have
     * children.
     */
    public MatchTreeNode(SimulatorModel model, GraphState source,
            MatchResult event, int nr, boolean anchored) {
        super(event, false);
        this.source = source;
        this.nr = nr;
        this.model = model;
        StringBuilder result = new StringBuilder();
        MatchResult match = getMatch();
        if (match instanceof RuleTransition) {
            String state = ((RuleTransition) match).target().toString();
            result.append(((RuleTransition) match).text(anchored));
            result.append(RIGHTARROW);
            result.append(HTMLConverter.ITALIC_TAG.on(state));
            if (this.model.getTrace().contains(match)) {
                result.append(TRACE_SUFFIX);
            }
            HTMLConverter.HTML_TAG.on(result);
        } else {
            result.append("Match ");
            result.append(this.nr);
        }
        this.label = result.toString();
    }

    /**
     * Convenience method to retrieve the user object as a graph transition.
     */
    public MatchResult getMatch() {
        return (MatchResult) getUserObject();
    }

    /**
     * Returns the graph state for which this is a match.
     */
    public GraphState getSource() {
        return this.source;
    }

    @Override
    public String toString() {
        return this.label;
    }

    private final SimulatorModel model;
    private final GraphState source;
    private final int nr;
    private final String label;
    /** HTML representation of the right arrow. */
    private static final String RIGHTARROW = "-->";
    /** The suffix for a match that is in the selected trace. */
    private static final String TRACE_SUFFIX = " "
        + HTMLConverter.STRONG_TAG.on("(*)");
}