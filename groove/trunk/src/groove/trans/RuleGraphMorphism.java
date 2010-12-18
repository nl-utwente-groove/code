/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: NodeEdgeHashMap.java,v 1.5 2008-01-30 09:32:52 iovka Exp $
 */
package groove.trans;

import groove.graph.Morphism;

/**
 * Default implementation of a generic node-edge-map. The implementation is
 * based on two internally stored hash maps, for the nodes and edges. Labels are
 * not translated.
 * @author Arend Rensink
 * @version $Revision: 2754 $
 */
public class RuleGraphMorphism extends Morphism<RuleNode,RuleLabel,RuleEdge> {
    /**
     * Creates a new, empty map.
     */
    public RuleGraphMorphism() {
        super(RuleFactory.instance());
    }

    @Override
    public RuleGraphMorphism clone() {
        return (RuleGraphMorphism) super.clone();
    }

    @Override
    public RuleGraphMorphism newMap() {
        return new RuleGraphMorphism();
    }
}
