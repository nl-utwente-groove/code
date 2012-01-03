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
 * $Id: DefaultNode.java,v 1.17 2008-02-19 10:35:31 fladder Exp $
 */
package groove.trans;

import groove.graph.AbstractNode;
import groove.graph.TypeGuard;
import groove.graph.TypeNode;
import groove.rel.LabelVar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Default implementation of a graph node. Default nodes have numbers, but node
 * equality is determined by object identity and not by node number.
 * @author Arend Rensink
 * @version $Revision: 2971 $
 */
public class DefaultRuleNode extends AbstractNode implements RuleNode {
    /**
     * Constructs a fresh node, with an explicitly given number and node type.
     * @param nr the number for this node
     * @param type the node type; may be {@code null}
     * @param sharp if {@code true}, the node is sharply typed
     */
    protected DefaultRuleNode(int nr, TypeNode type, boolean sharp,
            List<TypeGuard> typeGuards) {
        super(nr);
        assert type != null : "Can't instantiate untyped rule node";
        this.type = type;
        this.sharp = sharp;
        if (typeGuards == null) {
            this.typeGuards = Collections.emptyList();
            this.typeVars = Collections.emptyList();
            this.matchingTypes = type.getSubtypes();
        } else {
            this.typeGuards = new ArrayList<TypeGuard>(typeGuards);
            this.typeVars = new ArrayList<LabelVar>(typeGuards.size());
            this.matchingTypes = new HashSet<TypeNode>();
            if (sharp) {
                this.matchingTypes.add(type);
            } else {
                this.matchingTypes.addAll(type.getSubtypes());
            }
            // restrict the matching types to those that satisfy all label guards
            for (TypeGuard guard : typeGuards) {
                if (guard.getVar() != null) {
                    this.typeVars.add(guard.getVar());
                }
                Iterator<TypeNode> typeIter = this.matchingTypes.iterator();
                while (typeIter.hasNext()) {
                    boolean typeOk = false;
                    for (TypeNode superType : typeIter.next().getSupertypes()) {
                        if (guard.isSatisfied(superType.label())) {
                            typeOk = true;
                            break;
                        }
                    }
                    if (!typeOk) {
                        typeIter.remove();
                    }
                }
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        DefaultRuleNode other = (DefaultRuleNode) obj;
        if (getType() == null) {
            return other.getType() == null;
        } else {
            return getType().equals(other.getType());
        }
    }

    @Override
    protected int computeHashCode() {
        int result = super.computeHashCode();
        int prime = 31;
        if (getType() != null) {
            result = prime * result + getType().hashCode();
        }
        return result;
    }

    /**
     * Returns a string consisting of the letter <tt>'n'</tt>.
     */
    @Override
    public String getToStringPrefix() {
        return "n";
    }

    public TypeNode getType() {
        return this.type;
    }

    @Override
    public List<LabelVar> getTypeVars() {
        return this.typeVars;
    }

    /** 
     * Returns the set of type guards associated with this rule node.
     * @return the set of guards; not {@code null} but possibly empty
     */
    public List<TypeGuard> getTypeGuards() {
        return this.typeGuards;
    }

    public Set<TypeNode> getMatchingTypes() {
        return this.matchingTypes;
    }

    public boolean isSharp() {
        return this.sharp;
    }

    /** Flag indicating if this node is sharply typed. */
    private final boolean sharp;
    /** The (possibly {@code null}) type of this rule node. */
    private final TypeNode type;
    /** The list of type guards associated with this node. */
    private final List<TypeGuard> typeGuards;
    /** The list of label variables derived from {@link #typeGuards}. */
    private final List<LabelVar> typeVars;
    /** The set of matching node types. */
    private final Set<TypeNode> matchingTypes;
}
