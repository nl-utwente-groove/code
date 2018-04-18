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
package groove.lts;

import groove.grammar.Action;
import groove.transform.Event;

import java.util.Comparator;

/** 
 * Type encoding the essential ingredients for a graph transition.
 * The implementation differs for rule and recipe transitions.
 */
public interface GraphTransitionKey {
    /** Returns the action for which this is a key. */
    public Action getAction();

    /** Returns the rule or recipe event of this key. */
    public Event getEvent();

    /** Fixed comparator for graph transition keys.
     */
    public static final Comparator<GraphTransitionKey> COMPARATOR =
        new groove.util.collect.Comparator<GraphTransitionKey>() {
            @Override
            public int compare(GraphTransitionKey o1, GraphTransitionKey o2) {
                // Recipe transitions come before match results
                int result = compare(o1 instanceof RecipeEvent, o2 instanceof RecipeEvent);
                if (result != 0) {
                    return result;
                }
                if (o1 instanceof RecipeEvent) {
                    result = compare((RecipeEvent) o1, (RecipeEvent) o2);
                } else {
                    result = compare((MatchResult) o1, (MatchResult) o2);
                }
                return result;
            }

            private int compare(RecipeEvent o1, RecipeEvent o2) {
                int result = o1.getAction().compareTo(o2.getAction());
                if (result != 0) {
                    return result;
                }
                result = o1.getInitial().getEvent().compareTo(o2.getInitial().getEvent());
                if (result != 0) {
                    return result;
                }
                result = o1.getTarget().getNumber() - o2.getTarget().getNumber();
                return result;
            }

            private int compare(MatchResult o1, MatchResult o2) {
                int result = o1.getEvent().compareTo(o2.getEvent());
                if (result != 0) {
                    return result;
                }
                return o1.getStep().compareTo(o2.getStep());
            }
        };
}
