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
 * $Id: RelationCalculator.java,v 1.3 2008-01-30 09:32:25 iovka Exp $
 */
package groove.rel;

import groove.graph.GraphShape;

import java.util.Iterator;
import java.util.List;

/**
 * Calculator yielding a {@link groove.rel.NodeRelation}.
 * @author Arend Rensink
 * @version $Revision$
 */
public class RelationCalculator implements RegExprCalculator<NodeRelation> {
    /**
     * Creates a relation calculator based on a given relation factory.
     */
    public RelationCalculator(NodeRelation factory) {
        this.factory = factory;
    }

    /**
     * Creates a relation calculator based on a given graph. The relation
     * factory will be set to a {@link SetNodeRelation} over that graph.
     */
    public RelationCalculator(GraphShape graph) {
        this(new SetNodeRelation(graph));
    }

    /**
     * Copies the relation associated with the atom in <code>expr</code> and
     * stores it in the underlying mapping.
     */
    public NodeRelation computeAtom(RegExpr.Atom expr) {
        return this.factory.newInstance(expr.toLabel());
    }

    /**
     * Computes the union of the relations associated with the operands of
     * <code>expr</code>, and stores it in the underlying mapping.
     */
    public NodeRelation computeChoice(RegExpr.Choice expr,
            List<NodeRelation> args) {
        Iterator<NodeRelation> argsIter = args.iterator();
        NodeRelation result = argsIter.next();
        while (argsIter.hasNext()) {
            NodeRelation operand = argsIter.next();
            result.doOr(operand);
        }
        return result;
    }

    /**
     * Computes the identity relation and stores it in the underlying mapping.
     */
    public NodeRelation computeEmpty(RegExpr.Empty expr) {
        return this.factory.createIdentityRelation();
    }

    /**
     * Computes the inverse of the relation associated with the operand of
     * <code>expr</code> and stores it in the underlying mapping.
     */
    public NodeRelation computeInv(RegExpr.Inv expr, NodeRelation arg) {
        return arg.getInverse();
    }

    /**
     * Negation of operations is not supported.
     * @throws UnsupportedOperationException always
     */
    public NodeRelation computeNeg(RegExpr.Neg expr, NodeRelation arg) {
        throw new UnsupportedOperationException();
    }

    /**
     * Computes the transitive closure over the relation associated with the
     * operand of <code>expr</code>, and stores it in the underlying mapping.
     */
    public NodeRelation computePlus(RegExpr.Plus expr, NodeRelation arg) {
        arg.doTransitiveClosure();
        return arg;
    }

    /**
     * Computes the sequential composition of the relations associated with the
     * operands of <code>expr</code>, and stores it in the underlying mapping.
     */
    public NodeRelation computeSeq(RegExpr.Seq expr, List<NodeRelation> argList) {
        Iterator<NodeRelation> argsIter = argList.iterator();
        NodeRelation result = argsIter.next();
        while (argsIter.hasNext()) {
            NodeRelation operand = argsIter.next();
            result.doThen(operand);
        }
        return result;
    }

    /**
     * Computes the transitive and reflexive closure over the relation
     * associated with the operand of <code>expr</code>, and stores it in the
     * underlying mapping.
     */
    public NodeRelation computeStar(RegExpr.Star expr, NodeRelation arg) {
        arg.doTransitiveClosure();
        arg.doReflexiveClosure();
        return arg;
    }

    /**
     * Creates a fresh relation on the basis of the set of all pairs.
     */
    public NodeRelation computeWildcard(RegExpr.Wildcard expr) {
        return this.factory.createMaximalRelation();
    }

    /**
     * Returns the current relation factory.
     */
    public NodeRelation getFactory() {
        return this.factory;
    }

    /** Factory for creating relations. */
    private final NodeRelation factory;
}