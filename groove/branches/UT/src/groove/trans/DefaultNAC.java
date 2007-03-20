// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/* 
 * $Id: DefaultNAC.java,v 1.1.1.1 2007-03-20 10:05:19 kastenberg Exp $
 */
package groove.trans;

import groove.graph.Morphism;
import groove.rel.VarGraph;
import groove.rel.VarMorphism;

/**
 * @version $Revision: 1.1.1.1 $ $Date: 2007-03-20 10:05:19 $
 */
public class DefaultNAC extends DefaultGraphCondition implements NAC {
    /**
     * Creates a NAC based on a given (partial) morphism.
     */
    public DefaultNAC(Morphism partial, RuleFactory ruleFactory) {
        super(partial, ruleFactory);
    }

    /**
     * Creates a NAC over a default context and an initially empty target pattern.
     */
    public DefaultNAC(VarGraph context, RuleFactory ruleFactory) {
        super(context, (VarGraph) context.newGraph(), ruleFactory);
    }

    /**
     * Delegates to {@link #hasMatching(VarMorphism)}
     * @require this.dom().equals(match.dom())
     */
    public boolean forbids(VarMorphism match) {
        return hasMatching(match);
    }
}
