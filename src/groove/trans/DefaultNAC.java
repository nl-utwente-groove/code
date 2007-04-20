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
 * $Id: DefaultNAC.java,v 1.4 2007-04-20 15:12:27 rensink Exp $
 */
package groove.trans;

import groove.graph.DefaultMorphism;
import groove.graph.Morphism;
import groove.rel.VarGraph;
import groove.rel.VarMorphism;

/**
 * @version $Revision: 1.4 $ $Date: 2007-04-20 15:12:27 $
 */
public class DefaultNAC extends DefaultGraphCondition implements NAC {
    /**
     * Creates a NAC based on a given (partial) morphism.
     */
    public DefaultNAC(Morphism partial, SystemProperties properties) {
        super(partial, properties);
    }

    /**
     * Creates a NAC over a default context and an initially empty target pattern.
     */
    public DefaultNAC(VarGraph context, SystemProperties properties) {
        this(new DefaultMorphism(context, context.newGraph()), properties);
    }

    /**
     * Delegates to {@link #matches(VarMorphism)}
     * @require this.dom().equals(match.dom())
     */
    @Deprecated
    public boolean forbids(VarMorphism match) {
        return matches(match);
    }
}
