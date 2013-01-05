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
import groove.grammar.Rule;
import groove.graph.EdgeComparator;
import groove.transform.RuleEvent;

import java.util.Comparator;

/** 
 * Class encoding the result of matching the rule in a control transition.
 * This essentially consists of a rule event and the control transition.
 */
public class MatchResult implements GraphTransitionKey {
    /** Constructs a result from a given event and control transition. */
    public MatchResult(RuleTransition ruleTrans) {
        this.ruleTrans = ruleTrans;
        this.event = ruleTrans.getEvent();
        this.ctrlTrans = ruleTrans.getCtrlTransition();
    }

    /** Constructs a result from a given event and control transition. */
    public MatchResult(RuleEvent event, CtrlTransition ctrlTrans) {
        this.ruleTrans = null;
        this.event = event;
        this.ctrlTrans = ctrlTrans;
    }

    /** Indicates if this match result is based on an already explored rule transition. */
    public boolean hasRuleTransition() {
        return this.ruleTrans != null;
    }

    /** Returns the rule transition wrapped in this match result, if any. */
    public RuleTransition getRuleTransition() {
        return this.ruleTrans;
    }

    /** Returns the event wrapped by this transition key. */
    public RuleEvent getEvent() {
        return this.event;
    }

    /** Returns the control transition wrapped by this transition key. */
    public CtrlTransition getCtrlTransition() {
        return this.ctrlTrans;
    }

    /** Returns the underlying rule of this match. */
    public Rule getRule() {
        return this.event.getRule();
    }

    @Override
    public int hashCode() {
        int hashcode = this.hashcode;
        if (hashcode == 0) {
            hashcode = computeHashCode();
            if (hashcode == 0) {
                hashcode++;
            }
            this.hashcode = hashcode;
        }
        return hashcode;
    }

    private int computeHashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getCtrlTransition().hashCode();
        result = prime * result + getEvent().hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof MatchResult)) {
            return false;
        }
        MatchResult other = (MatchResult) obj;
        if (!getCtrlTransition().equals(other.getCtrlTransition())) {
            return false;
        }
        if (!getEvent().equals(other.getEvent())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.ctrlTrans.label().text();
    }

    /** The precomputed hashcode; 0 if it has not yet been not initialised. */
    private int hashcode;

    private final RuleTransition ruleTrans;
    private final RuleEvent event;
    private final CtrlTransition ctrlTrans;
    /** Fixed comparator for match results, which compares results for their 
     * rule events and then their control transitions. 
     */
    public static final Comparator<MatchResult> COMPARATOR =
        new Comparator<MatchResult>() {
            @Override
            public int compare(MatchResult o1, MatchResult o2) {
                int result = o1.getEvent().compareTo(o2.getEvent());
                if (result == 0) {
                    result =
                        edgeComparator.compare(o1.getCtrlTransition(),
                            o2.getCtrlTransition());
                }
                return result;
            }
        };
    private static final EdgeComparator edgeComparator =
        EdgeComparator.instance();
}
