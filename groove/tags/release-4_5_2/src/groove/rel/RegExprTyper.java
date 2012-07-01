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
package groove.rel;

import groove.graph.TypeEdge;
import groove.graph.TypeElement;
import groove.graph.TypeGraph;
import groove.graph.TypeGuard;
import groove.graph.TypeLabel;
import groove.graph.TypeNode;
import groove.rel.RegExpr.Atom;
import groove.rel.RegExpr.Choice;
import groove.rel.RegExpr.Empty;
import groove.rel.RegExpr.Inv;
import groove.rel.RegExpr.Neg;
import groove.rel.RegExpr.Plus;
import groove.rel.RegExpr.Seq;
import groove.rel.RegExpr.Sharp;
import groove.rel.RegExpr.Star;
import groove.rel.RegExpr.Wildcard;
import groove.rel.RegExprTyper.Result;
import groove.view.FormatError;
import groove.view.FormatErrorSet;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Calculates the possible types of a regular expression. */
public class RegExprTyper implements RegExprCalculator<Result> {
    /** Constructs a typer for a given type graph. */
    public RegExprTyper(TypeGraph typeGraph,
            Map<LabelVar,Set<? extends TypeElement>> varTyping) {
        this.typeGraph = typeGraph;
        this.varTyping = varTyping;
    }

    @Override
    public Result computeNeg(Neg expr, Result arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Result computeStar(Star expr, Result arg) {
        Result result = new Result().getUnion(arg).getClosure();
        if (!arg.isEmpty() && result.isEmpty()) {
            result.addError("%s cannot be typed", expr);
        }
        return result;
    }

    @Override
    public Result computePlus(Plus expr, Result arg) {
        Result result = arg.getClosure();
        if (!arg.isEmpty() && result.isEmpty()) {
            result.addError("%s cannot be typed", expr);
        }
        return result;
    }

    @Override
    public Result computeInv(Inv expr, Result arg) {
        return arg.getInverse();
    }

    @Override
    public Result computeSeq(Seq expr, List<Result> argList) {
        Result result = argList.get(0);
        if (!result.isEmpty()) {
            for (int i = 1; i < argList.size(); i++) {
                result = result.getThen(argList.get(i));
            }
            if (result.isEmpty()) {
                result.addError("%s cannot be typed", expr);
            }
        }
        return result;
    }

    @Override
    public Result computeChoice(Choice expr, List<Result> argList) {
        Result result = argList.get(0);
        for (int i = 1; i < argList.size(); i++) {
            result = result.getUnion(argList.get(i));
        }
        return result;
    }

    @Override
    public Result computeAtom(Atom expr) {
        Result result = new Result();
        TypeLabel typeLabel = expr.toTypeLabel();
        if (this.typeGraph.isNodeType(typeLabel)) {
            for (TypeNode subType : this.typeGraph.getNode(typeLabel).getSubtypes()) {
                result.add(subType, subType);
            }
        } else {
            for (TypeEdge edgeType : this.typeGraph.labelEdgeSet(typeLabel)) {
                Set<TypeNode> targetTypes = edgeType.target().getSubtypes();
                for (TypeNode sourceType : edgeType.source().getSubtypes()) {
                    result.add(sourceType, targetTypes);
                }
            }
        }
        if (result.isEmpty()) {
            result.addError("%s cannot be typed", expr);
        }
        return result;
    }

    @Override
    public Result computeSharp(Sharp expr) {
        Result result = new Result();
        TypeLabel typeLabel = expr.getSharpLabel();
        if (this.typeGraph.isNodeType(typeLabel)) {
            for (TypeNode subType : this.typeGraph.getNode(typeLabel).getSubtypes()) {
                result.add(subType, subType);
            }
        } else {
            for (TypeEdge edgeType : this.typeGraph.labelEdgeSet(typeLabel)) {
                result.add(edgeType.source(), edgeType.target());
            }
        }
        if (result.isEmpty()) {
            result.addError("%s cannot be typed", expr);
        }
        return result;
    }

    @Override
    public Result computeWildcard(Wildcard expr) {
        Result result;
        if (this.typeGraph.isNodeType(expr.getKind())) {
            result = computeNodeWildcard(expr);
        } else {
            result = computeEdgeWildcard(expr);
        }
        if (result.isEmpty()) {
            result.addError("%s cannot be typed", expr);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private Result computeNodeWildcard(Wildcard expr) {
        Result result = new Result();
        LabelVar var = expr.getWildcardId();
        Set<TypeNode> candidates = new HashSet<TypeNode>();
        if (var.hasName()) {
            candidates.addAll((Collection<TypeNode>) this.varTyping.get(var));
        } else {
            candidates.addAll(this.typeGraph.nodeSet());
        }
        TypeGuard guard = expr.getWildcardGuard();
        for (TypeNode typeNode : guard.filter(candidates)) {
            result.add(typeNode, typeNode);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private Result computeEdgeWildcard(Wildcard expr) {
        Result result = new Result();
        LabelVar var = expr.getWildcardId();
        Set<TypeEdge> candidates = new HashSet<TypeEdge>();
        if (var.hasName()) {
            candidates.addAll((Collection<TypeEdge>) this.varTyping.get(var));
        } else {
            candidates.addAll(this.typeGraph.edgeSet());
        }
        TypeGuard guard = expr.getWildcardGuard();
        for (TypeEdge typeEdge : guard.filter(candidates)) {
            Set<TypeNode> targetTypes = typeEdge.target().getSubtypes();
            for (TypeNode sourceType : typeEdge.source().getSubtypes()) {
                result.add(sourceType, targetTypes);
            }
        }
        return result;
    }

    @Override
    public Result computeEmpty(Empty expr) {
        return new Result(this.typeGraph.nodeSet());
    }

    /** The Predefined typing of the label variables. */
    private final Map<LabelVar,Set<? extends TypeElement>> varTyping;

    /** The type graph with respect to which the typing is calculated. */
    private final TypeGraph typeGraph;

    /** 
     * Outcome of the typing of a regular expression,
     * consisting of a relation between node types.
     * @author Arend Rensink
     * @version $Revision $
     */
    public static class Result {
        /** Creates an empty relation. */
        public Result() {
            // empty
        }

        /** Creates a complete relation over a given set of nodes. */
        public Result(Collection<? extends TypeNode> universe) {
            for (TypeNode node : universe) {
                add(node, universe);
            }
        }

        /** Indicates if this is an empty relation. */
        public boolean isEmpty() {
            return this.map.isEmpty();
        }

        /** Returns all right hand elements related to a given left hand element.*/
        public Set<TypeNode> getAll(TypeNode left) {
            return this.map.get(left);
        }

        /** Adds a pair to the relation. */
        public void add(TypeNode left, TypeNode right) {
            Set<TypeNode> current = this.map.get(left);
            if (current == null) {
                this.map.put(left, current = new HashSet<TypeNode>());
            }
            current.add(right);
        }

        /** Adds a set of pairs to the relation. */
        public void add(TypeNode left, Collection<? extends TypeNode> right) {
            Set<TypeNode> current = this.map.get(left);
            if (current == null) {
                this.map.put(left, current = new HashSet<TypeNode>());
            }
            current.addAll(right);
        }

        /** Returns the inverse of this relation. */
        public Result getInverse() {
            Result result = new Result();
            for (Map.Entry<TypeNode,Set<TypeNode>> entry : this.map.entrySet()) {
                TypeNode left = entry.getKey();
                for (TypeNode right : entry.getValue()) {
                    result.add(right, left);
                }
            }
            result.addErrors(getErrors());
            return result;
        }

        /** Returns the composition of this relation followed by another. */
        public Result getThen(Result other) {
            Result result = new Result();
            for (Map.Entry<TypeNode,Set<TypeNode>> entry : this.map.entrySet()) {
                Set<TypeNode> allRight = new HashSet<TypeNode>();
                for (TypeNode right : entry.getValue()) {
                    Set<TypeNode> otherRight = other.getAll(right);
                    if (otherRight != null) {
                        allRight.addAll(otherRight);
                    }
                }
                if (!allRight.isEmpty()) {
                    result.add(entry.getKey(), allRight);
                }
            }
            result.addErrors(getErrors());
            result.addErrors(other.getErrors());
            return result;
        }

        /** Returns the union of this relation with another. */
        public Result getUnion(Result other) {
            Result result = new Result();
            copyTo(result);
            other.copyTo(result);
            return result;
        }

        /** Returns the intersection of this relation with another. */
        public Result getIntersection(Result other) {
            Result result = new Result();
            for (Map.Entry<TypeNode,Set<TypeNode>> entry : this.map.entrySet()) {
                TypeNode left = entry.getKey();
                Set<TypeNode> otherRight = other.getAll(left);
                if (otherRight != null) {
                    Set<TypeNode> right =
                        new HashSet<TypeNode>(entry.getValue());
                    right.retainAll(otherRight);
                    if (!right.isEmpty()) {
                        result.add(left, right);
                    }
                }
            }
            result.addErrors(getErrors());
            result.addErrors(other.getErrors());
            return result;
        }

        /** 
         * Intersects this relation with another.
         * @return {@code true} if this relation was changed as a result 
         */
        private boolean intersect(Result other) {
            boolean result = false;
            Iterator<Map.Entry<TypeNode,Set<TypeNode>>> entryIter =
                this.map.entrySet().iterator();
            while (entryIter.hasNext()) {
                Map.Entry<TypeNode,Set<TypeNode>> entry = entryIter.next();
                TypeNode left = entry.getKey();
                Set<TypeNode> otherRight = other.getAll(left);
                if (otherRight != null) {
                    Set<TypeNode> right = entry.getValue();
                    result |= right.retainAll(otherRight);
                    if (right.isEmpty()) {
                        entryIter.remove();
                    }
                }
            }
            return result;
        }

        /** Returns the limit of the intersection of this relation with itself. */
        public Result getClosure() {
            Result result = this;
            do {
                result = result.getThen(this);
            } while (result.intersect(this) && !result.isEmpty());
            return result;
        }

        @Override
        public int hashCode() {
            return this.map.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Result other = (Result) obj;
            return this.map.equals(other.map);
        }

        @Override
        public String toString() {
            return this.map.toString();
        }

        /** Adds a format error to the errors stored in this relation. */
        public void addError(String message, Object... args) {
            this.errors.add(message, args);
        }

        /** Adds a collection of format errors to the errors stored in this relation. */
        private void addErrors(Collection<FormatError> errors) {
            this.errors.addAll(errors);
        }

        /** Retrieves the errors stored in this relation. */
        public FormatErrorSet getErrors() {
            return this.errors;
        }

        /** Indicates if this relation has errors. */
        public boolean hasErrors() {
            return !getErrors().isEmpty();
        }

        /** Copies this relation (including the errors) into another. */
        private void copyTo(Result other) {
            for (Map.Entry<TypeNode,Set<TypeNode>> entry : this.map.entrySet()) {
                other.add(entry.getKey(), entry.getValue());
            }
            other.addErrors(getErrors());
        }

        private final Map<TypeNode,Set<TypeNode>> map =
            new HashMap<TypeNode,Set<TypeNode>>();
        private final FormatErrorSet errors = new FormatErrorSet();
    }
}