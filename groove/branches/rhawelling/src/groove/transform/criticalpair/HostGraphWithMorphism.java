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
package groove.transform.criticalpair;

import groove.grammar.host.HostEdge;
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostGraphMorphism;
import groove.grammar.host.HostNode;
import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleGraph;
import groove.grammar.rule.RuleNode;
import groove.graph.AElementBiMap;

class HostGraphWithMorphism {

    //the target of the morphism
    private final HostGraph hostGraph;
    private final HostGraphMorphism morphism;

    HostGraphWithMorphism(HostGraph hostGraph, HostGraphMorphism morphism) {
        this.hostGraph = hostGraph;
        this.morphism = morphism;
    }

    private RuleGraph ruleGraph = null;
    private AElementBiMap<HostNode,HostEdge,RuleNode,RuleEdge> sourceToRuleMorphism =
        null;

    public HostGraph getHostGraph() {
        return this.hostGraph;
    }

    public HostGraphMorphism getMorphism() {
        return this.morphism;
    }
}