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

import groove.grammar.host.HostGraph;
import groove.grammar.host.HostGraphMorphism;

/**
 * 
 * @author Ruud
 * A HostGraph with a HostGraphMorphism
 * Used to keep track of states when analysing whether a critical pair is strictly locally confluent
 */
class HostGraphWithMorphism {

    //the target of the morphism
    private final HostGraph hostGraph;
    private final HostGraphMorphism morphism;

    HostGraphWithMorphism(HostGraph hostGraph, HostGraphMorphism morphism) {
        this.hostGraph = hostGraph;
        this.morphism = morphism;
    }

    public HostGraph getHostGraph() {
        return this.hostGraph;
    }

    public HostGraphMorphism getMorphism() {
        return this.morphism;
    }
}