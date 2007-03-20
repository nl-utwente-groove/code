// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/*
 * $Id: HintedIndegreeScheduleFactory.java,v 1.1.1.1 2007-03-20 10:05:20 kastenberg Exp $
 */
package groove.trans;

import groove.graph.DefaultLabel;
import groove.graph.Edge;
import groove.graph.Label;
import groove.graph.Node;
import groove.util.Bag;
import groove.util.ExprFormatException;
import groove.util.ExprParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class that uses a hint as to the sequence in which edge labels can
 * be matched best. 
 * @author Arend Rensink
 * @version $Revision: 1.1.1.1 $
 */
public class HintedIndegreeScheduleFactory extends IndegreeScheduleFactory {
    /**
     * Converts a list of strings into a list of {@link DefaultLabel}s.
     */
    static protected List<Label> toLabelList(List<String> labelTexts) {
        List<Label> result = new ArrayList<Label>();
        for (String labelText: labelTexts) {
            result.add(DefaultLabel.createLabel(labelText));
        }
        return result;
    }

    /**
     * Initializes the factory with a hint.
     * The hint is list of {@link groove.graph.DefaultLabel}s.
     * These should be attempted in the order of their occurrence in the list.
     */
    public HintedIndegreeScheduleFactory(List<Label> hintList) {
        initHint(hintList);
    }

    /**
     * Initializes the factory with a string.
     * The hint is list of strings that will be treated as {@link groove.graph.DefaultLabel}s.
     * These should be attempted in the order of their occurrence in the list.
     */
    public HintedIndegreeScheduleFactory(String hintText) throws ExprFormatException {
        this(toLabelList(Arrays.asList(ExprParser.splitExpr(hintText, " "))));
    }

    /**
     * Initializes the state of this factory on the basis of the given hint.
     * @param hint
     */
    protected void initHint(List<Label> hint) {
        this.hint = hint;
        this.priorities = new HashMap<Label,Integer>();
        for (int hintIndex = 0; hintIndex < hint.size(); hintIndex++) {
            priorities.put(hint.get(hintIndex), new Integer(hintIndex));
        }
    }
    
    /**
     * Returns an unmodifiable view upon the hint.
     */
    protected List<Label> getHint() {
        return Collections.unmodifiableList(hint);
    }

    /**
     * This implementation first tests if <code>first</code> has a 
     * higher priority label than <code>second</code>, and only delegates to
     * <code>super</code> if the priorities are the same.
     */
    protected int compareTo(Bag<Node> indegrees, Set<Node> remainingNodes, Edge first, Edge second) {
        // first compare edge priorities (lower = better)
        int result = getEdgePriority(second) - getEdgePriority(first);
        if (result != 0) {
            return result;
        }
        // now delegate to super
        return super.compareTo(indegrees, remainingNodes, first, second);
    }
    
    /**
     * Returns the priority of an edge, judged by its label.
     * @see #getPriority(Label)
     */
    private int getEdgePriority(Edge edge) {
        return getPriority(edge.label());
    }

    /**
     * Returns the priority of a given label according to the position
     * of the label in the internally stored hint.
     * Priorities are ordered from low to higt, i.e., the lower the
     * number the higher the priority.
     * If the label does not occur in the hint, it gets the lowest
     * priority.
     */
    protected int getPriority(Label label) {
        Integer priority = priorities.get(label);
        if (priority == null) {
            return hint.size();
        } else {
            return priority.intValue();
        }
    }
    /**
     * The hint contained in this factory.
     */
    private List<Label> hint;
    /**
     * A mapping from {@link groove.graph.DefaultLabel} to {@link Integer}
     * indicating the position of a given label in {@link #hint}.
     */
    private Map<Label,Integer> priorities;
}