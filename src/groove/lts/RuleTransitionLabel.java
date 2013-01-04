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

import groove.control.CtrlTransition;
import groove.graph.AbstractLabel;
import groove.trans.AbstractRuleEvent;
import groove.trans.HostNode;
import groove.trans.Rule;
import groove.trans.RuleEvent;
import groove.trans.GrammarRecord;

import java.util.Arrays;

/** Class of labels that can appear on rule transitions. */
public class RuleTransitionLabel extends AbstractLabel implements ActionLabel {
    /** 
     * Constructs a new label on the basis of a given rule event and list
     * of created nodes.
     */
    private RuleTransitionLabel(GraphState source, MatchResult match,
            HostNode[] addedNodes) {
        this.event = match.getEvent();
        this.ctrlTrans = match.getCtrlTransition();
        this.addedNodes = addedNodes;
    }

    @Override
    public Rule getAction() {
        return this.event.getRule();
    }

    /** Returns the event wrapped in this label. */
    public RuleEvent getEvent() {
        return this.event;
    }

    /** Returns the event wrapped in this label. */
    public CtrlTransition getCtrlTransition() {
        return this.ctrlTrans;
    }

    /** Returns the added nodes of the label. */
    public HostNode[] getAddedNodes() {
        return this.addedNodes;
    }

    @Override
    public String text() {
        return text(false);
    }

    /** Returns the label text, with optionally the rule parameters
     * replaced by anchor images.
     * @param anchored if {@code true}, the anchor images are used
     * instead of the rule parameters
     */
    public String text(boolean anchored) {
        StringBuilder result = new StringBuilder();
        boolean brackets =
            getAction().getSystemProperties().isShowTransitionBrackets();
        if (brackets) {
            result.append(BEGIN_CHAR);
        }
        if (getCtrlTransition().hasRecipe()) {
            result.append(getCtrlTransition().getRecipe());
            result.append('/');
        }
        result.append(((AbstractRuleEvent<?,?>) getEvent()).getLabelText(
            this.addedNodes, anchored));
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
        result = prime * result + this.ctrlTrans.hashCode();
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
        if (!this.ctrlTrans.equals(other.ctrlTrans)) {
            return false;
        }
        return true;
    }

    private final RuleEvent event;
    private final CtrlTransition ctrlTrans;
    private final HostNode[] addedNodes;

    /** 
     * Returns the label text for the rule label consisting of a given source state
     * and event. Optionally, the rule parameters are replaced by anchor images.
     */
    public static final String text(GraphState source, MatchResult match,
            boolean anchored) {
        return createLabel(source, match, null).text(anchored);
    }

    /** 
     * Creates a normalised rule label.
     * @see GrammarRecord#normaliseLabel(RuleTransitionLabel)
     */
    public static final RuleTransitionLabel createLabel(GraphState source,
            MatchResult match, HostNode[] addedNodes) {
        RuleTransitionLabel result =
            new RuleTransitionLabel(source, match, addedNodes);
        if (REUSE_LABELS) {
            GrammarRecord record = source.getGTS().getRecord();
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
