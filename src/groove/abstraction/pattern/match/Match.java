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
package groove.abstraction.pattern.match;

import groove.abstraction.pattern.shape.PatternEdge;
import groove.abstraction.pattern.shape.PatternGraphMorphism;
import groove.abstraction.pattern.shape.PatternNode;
import groove.abstraction.pattern.trans.PatternRule;
import groove.graph.ElementFactory;

public final class Match extends PatternGraphMorphism {

    public Match(ElementFactory<PatternNode,PatternEdge> factory) {
        super(factory);
    }

    public PatternRule getRule() {
        return null;
    }

}
