/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.trans;

import groove.graph.Morphism;
import groove.graph.TypeLabel;

/**
 * Mappings from elements of one host graph to those of another.
 * @author Arend Rensink
 * @version $Revision $
 */
public class HostGraphMorphism extends Morphism<HostNode,TypeLabel,HostEdge> {
    /**
     * Creates a new, empty map.
     */
    public HostGraphMorphism(HostFactory factory) {
        super(factory);
    }

    @Override
    public HostFactory getFactory() {
        return (HostFactory) super.getFactory();
    }

    @Override
    public HostGraphMorphism clone() {
        return (HostGraphMorphism) super.clone();
    }

    @Override
    public HostGraphMorphism newMap() {
        return new HostGraphMorphism(getFactory());
    }

    @Override
    public HostGraphMorphism then(Morphism<HostNode,TypeLabel,HostEdge> other) {
        return (HostGraphMorphism) super.then(other);
    }

    @Override
    public HostGraphMorphism inverseThen(
            Morphism<HostNode,TypeLabel,HostEdge> other) {
        return (HostGraphMorphism) super.inverseThen(other);
    }
}
