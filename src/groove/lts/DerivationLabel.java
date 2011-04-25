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
import groove.trans.RuleEvent;
import groove.trans.Rule;

/** Class of labels that can appear on graph transitions. */
public class DerivationLabel extends AbstractLabel {
    /** 
     * Constructs a new label on the basis of a given rule event and list
     * of created nodes.
     */
    public DerivationLabel(RuleEvent event, HostNode[] addedNodes) {
        this.text = getLabelText(event, addedNodes);
    }

    /** Constructs the label text. */
    private String getLabelText(RuleEvent event, HostNode[] addedNodes) {
        StringBuilder result = new StringBuilder();
        Rule rule = event.getRule();
        boolean brackets =
            rule.getSystemProperties().isShowTransitionBrackets();
        if (brackets) {
            result.append(BEGIN_CHAR);
        }
        result.append(((AbstractEvent<?,?>) event).getLabelText(addedNodes));
        if (brackets) {
            result.append(END_CHAR);
        }
        return result.toString();
    }

    @Override
    public String text() {
        return this.text;
    }

    private final String text;

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

    /** The obligatory first character of a rule name. */
    private static final char BEGIN_CHAR = '<';
    /** The obligatory last character of a rule name. */
    private static final char END_CHAR = '>';
}
