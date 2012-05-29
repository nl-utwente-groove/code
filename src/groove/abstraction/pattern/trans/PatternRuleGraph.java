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
package groove.abstraction.pattern.trans;

import groove.abstraction.pattern.shape.AbstractPatternGraph;

/**
 * Pattern graphs that compose rules.
 * 
 * @author Eduardo Zambon
 */
public final class PatternRuleGraph extends
        AbstractPatternGraph<RuleNode,RuleEdge> {

    /** Default constructor. */
    public PatternRuleGraph(String name) {
        super(name);
    }

    @Override
    public boolean addNode(RuleNode node) {
        boolean result = super.addNode(node);
        if (result) {
            addToLayer(node);
        }
        return result;
    }

}
