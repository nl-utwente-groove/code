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
package groove.abstraction;

import groove.graph.Morphism;
import groove.graph.TypeLabel;
import groove.trans.HostEdge;
import groove.trans.HostGraphMorphism;
import groove.trans.HostNode;

/**
 * Morphism between shapes.
 * @author Arend Rensink
 * @version $Revision $
 */
public class ShapeMorphism extends HostGraphMorphism {

    /**
     * Creates a shape morphism with a given element factory.
     */
    public ShapeMorphism(ShapeFactory factory) {
        super(factory);
    }

    @Override
    public ShapeMorphism clone() {
        return (ShapeMorphism) super.clone();
    }

    @Override
    public ShapeMorphism newMap() {
        return new ShapeMorphism(getFactory());
    }

    @Override
    public ShapeMorphism then(Morphism<HostNode,TypeLabel,HostEdge> other) {
        return (ShapeMorphism) super.then(other);
    }

    @Override
    public ShapeMorphism inverseThen(Morphism<HostNode,TypeLabel,HostEdge> other) {
        return (ShapeMorphism) super.inverseThen(other);
    }

    @Override
    public ShapeNode getNode(HostNode key) {
        return (ShapeNode) super.getNode(key);
    }

    @Override
    public ShapeEdge getEdge(HostEdge key) {
        return (ShapeEdge) super.getEdge(key);
    }

    @Override
    public ShapeNode putNode(HostNode key, HostNode layout) {
        return (ShapeNode) super.putNode(key, layout);
    }

    @Override
    public ShapeEdge putEdge(HostEdge key, HostEdge layout) {
        return (ShapeEdge) super.putEdge(key, layout);
    }

    @Override
    public ShapeNode removeNode(HostNode key) {
        return (ShapeNode) super.removeNode(key);
    }

    @Override
    public ShapeEdge removeEdge(HostEdge key) {
        return (ShapeEdge) super.removeEdge(key);
    }

    @Override
    public ShapeFactory getFactory() {
        return (ShapeFactory) super.getFactory();
    }
}
