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
 * $Id: DefaultNAC.java,v 1.10 2008-01-30 09:32:36 iovka Exp $
 */
package groove.trans;

import groove.graph.DefaultMorphism;
import groove.graph.Graph;
import groove.graph.Morphism;

/**
 * @version $Revision: 1.10 $ $Date: 2008-01-30 09:32:36 $
 */
@Deprecated
public class DefaultNAC extends NotCondition {
    /**
     * Creates a NAC based on a given (partial) morphism.
     */
    public DefaultNAC(Morphism partial, SystemProperties properties) {
        super(partial.cod(), partial.elementMap(), properties);
    }

    /**
     * Creates a NAC over a default context and an initially empty target pattern.
     */
    public DefaultNAC(Graph context, SystemProperties properties) {
        this(new DefaultMorphism(context, context.newGraph()), properties);
    }
}
