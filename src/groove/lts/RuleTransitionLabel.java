/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.lts;

import groove.graph.AbstractLabel;
import groove.trans.AbstractEvent;
import groove.trans.HostNode;
import groove.trans.Recipe;
import groove.trans.Rule;
import groove.trans.RuleEvent;
import groove.trans.SystemRecord;

import java.util.Arrays;

/** Class of labels that can appear on rule transitions. */
public class RuleTransitionLabel extends AbstractLabel implements ActionLabel {
    /** 
     * Constructs a new label on the basis of a given rule event and list
     * of created nodes.
     */
    private RuleTransitionLabel(GraphState source, RuleEvent event,
            HostNode[] addedNodes) {
        this.event = event;
        this.addedNodes = addedNodes;
        this.recipe =
            source.getCtrlState().getTransition(event.getRule()).getRecipe();
    }

    @Override
    public Rule getAction() {
        return this.event.getRule();
    }

    /** Returns the event wrapped in this label. */
    public RuleEvent getEvent() {
        return this.event;
    }

    /** Returns the added nodes of the label. */
    public HostNode[] getAddedNodes() {
        return this.addedNodes;
    }

    @Override
    public String text() {
        StringBuilder result = new StringBuilder();
        boolean brackets =
            getAction().getSystemProperties().isShowTransitionBrackets();
        if (brackets) {
            result.append(BEGIN_CHAR);
        }
        if (this.recipe != null) {
            result.append(this.recipe);
            result.append('/');
        }
        result.append(((AbstractEvent<?,?>) this.event).getLabelText(this.addedNodes));
        if (brackets) {
            result.append(END_CHAR);
        }
        return result.toString();
    }

    @Override
    protected int computeHashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(this.addedNodes);
        result = prime * result + this.event.hashCode();
        result =
            prime * result
                + ((this.recipe == null) ? 0 : this.recipe.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RuleTransitionLabel other = (RuleTransitionLabel) obj;
        if (!Arrays.equals(this.addedNodes, other.addedNodes)) {
            return false;
        }
        if (!this.event.equals(other.event)) {
            return false;
        }
        if (this.recipe == null) {
            if (other.recipe != null) {
                return false;
            }
        } else if (!this.recipe.equals(other.recipe)) {
            return false;
        }
        return true;
    }

    private final RuleEvent event;
    private final HostNode[] addedNodes;
    private final Recipe recipe;

    /** 
     * Returns a label text consisting of the anchors, rather than
     * the rule parameters.
     */
    public static final String getAnchorText(RuleEvent event) {
        StringBuilder result = new StringBuilder();
        Rule rule = event.getRule();
        boolean brackets =
            rule.getSystemProperties().isShowTransitionBrackets();
        if (brackets) {
            result.append(BEGIN_CHAR);
        }
        result.append(rule.getTransitionLabel());
        result.append(event.getAnchorImageString());
        if (brackets) {
            result.append(END_CHAR);
        }
        return result.toString();
    }

    /** 
     * Creates a normalised rule label.
     * @see SystemRecord#normaliseLabel(RuleTransitionLabel)
     */
    public static final RuleTransitionLabel createLabel(GraphState source,
            RuleEvent event, HostNode[] addedNodes) {
        RuleTransitionLabel result =
            new RuleTransitionLabel(source, event, addedNodes);
        if (REUSE_LABELS) {
            SystemRecord record = source.getGTS().getRecord();
            result = record.normaliseLabel(result);
        }
        return result;
    }

    /** The obligatory first character of a rule name. */
    private static final char BEGIN_CHAR = '<';
    /** The obligatory last character of a rule name. */
    private static final char END_CHAR = '>';
    /** Flag controlling whether transition labels are normalised. */
    private static final boolean REUSE_LABELS = false;
}
