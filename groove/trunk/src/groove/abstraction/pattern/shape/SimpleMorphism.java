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
package groove.abstraction.pattern.shape;

import groove.abstraction.MyHashMap;
import groove.trans.HostNode;
import groove.util.Fixable;

import java.util.Map;

/**
 * Class representing morphisms between simple graphs. These morphisms are used
 * as labels of pattern edges. The map is injective and not surjective on the
 * target.
 * 
 * @author Eduardo Zambon
 */
public class SimpleMorphism implements Fixable {

    private final String name;
    private final TypeNode source;
    private final TypeNode target;
    private final Map<HostNode,HostNode> nodeMap;
    private final Map<HostNode,HostNode> inverseNodeMap;
    private boolean fixed;

    /** Default constructor. */
    public SimpleMorphism(String name, TypeNode source, TypeNode target) {
        this.name = name;
        this.source = source;
        this.target = target;
        this.nodeMap = new MyHashMap<HostNode,HostNode>();
        this.inverseNodeMap = new MyHashMap<HostNode,HostNode>();
        this.fixed = false;
    }

    @Override
    public void setFixed() {
        this.fixed = true;
    }

    @Override
    public boolean isFixed() {
        return this.fixed;
    }

    @Override
    public void testFixed(boolean fixed) {
        if (isFixed() != fixed) {
            throw new IllegalStateException();
        }
    }

    @Override
    public String toString() {
        return this.getName() + ": " + this.nodeMap;
    }

    /** Basic getter method. */
    public String getName() {
        return this.name;
    }

    /** Basic getter method. */
    public TypeNode getSource() {
        return this.source;
    }

    /** Basic getter method. */
    public TypeNode getTarget() {
        return this.target;
    }

    /** Updates the morphism. */
    public void putNode(HostNode source, HostNode target) {
        assert !isFixed();
        this.nodeMap.put(source, target);
        this.inverseNodeMap.put(target, source);
    }

    /** Returns the non-null image of the given node in the morphism. */
    public HostNode getImage(HostNode source) {
        assert isFixed();
        HostNode target = this.nodeMap.get(source);
        assert target != null;
        return target;
    }

    /**
     * Returns the pre-image of the given node in the morphism. The returned
     * result is a single element instead of a set because the morphism is
     * injective. May return null if the node has no pre-image. 
     */
    public HostNode getPreImage(HostNode target) {
        assert isFixed();
        return this.inverseNodeMap.get(target);
    }

    /** Returns true if the given node is the domain of the morphism. */
    public boolean isDom(HostNode node) {
        return this.nodeMap.keySet().contains(node);
    }

    /** Returns true if the given node is the co-domain of the morphism. */
    public boolean isCod(HostNode node) {
        return this.nodeMap.values().contains(node);
    }
}
