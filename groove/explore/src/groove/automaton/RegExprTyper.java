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
package groove.automaton;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;

import groove.automaton.RegExpr.Atom;
import groove.automaton.RegExpr.Choice;
import groove.automaton.RegExpr.Empty;
import groove.automaton.RegExpr.Inv;
import groove.automaton.RegExpr.Neg;
import groove.automaton.RegExpr.Plus;
import groove.automaton.RegExpr.Seq;
import groove.automaton.RegExpr.Sharp;
import groove.automaton.RegExpr.Star;
import groove.automaton.RegExpr.Wildcard;
import groove.automaton.RegExprTyper.Result;
import groove.grammar.rule.LabelVar;
import groove.grammar.type.TypeEdge;
import groove.grammar.type.TypeElement;
import groove.grammar.type.TypeGraph;
import groove.grammar.type.TypeGuard;
import groove.grammar.type.TypeLabel;
import groove.grammar.type.TypeNode;
import groove.graph.EdgeRole;
import groove.util.parse.FormatError;
import groove.util.parse.FormatErrorSet;

/** Calculates the possible types of a regular expression. */
public class RegExprTyper implements RegExprCalculator<Result> {
    /**
     * Constructs a typer for a given type graph and variable typing.
     * @param typeGraph the (non-{@code null}) type graph over which results are computed
     * @param varTyping the (non-{@code null}) mapping from type variables to type graph elements
     */
    public RegExprTyper(TypeGraph typeGraph, Map<LabelVar,Set<? extends TypeElement>> varTyping) {
        this.typeGraph = typeGraph;
        this.varTyping = varTyping;
    }

    @Override
    public Result computeNeg(Neg expr, Result arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Result computeStar(Star expr, Result arg) {
        Result result = new Result().getUnion(arg)
            .getClosure();
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
            TypeNode typeNode = this.typeGraph.getNode(typeLabel);
            assert typeNode != null; // due to isNodeType(typeLabel)
            for (TypeNode subType : typeNode.getSubtypes()) {
                if (!subType.isAbstract()) {
                    result.add(subType, subType);
                }
            }
        } else {
            for (TypeEdge edgeType : this.typeGraph.edgeSet(typeLabel)) {
                if (!edgeType.isAbstract()) {
                    Set<TypeNode> targetTypes = edgeType.target()
                        .getSubtypes();
                    for (TypeNode sourceType : edgeType.source()
                        .getSubtypes()) {
                        result.add(sourceType, targetTypes);
                    }
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
            TypeNode typeNode = this.typeGraph.getNode(typeLabel);
            assert typeNode != null; // due to isNodeType(typeLabel)
            for (TypeNode subType : typeNode.getSubtypes()) {
                result.add(subType, subType);
            }
        } else {
            for (TypeEdge edgeType : this.typeGraph.edgeSet(typeLabel)) {
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
        Set<@NonNull TypeNode> candidates = new HashSet<>();
        if (var.hasName()) {
            candidates.addAll((Collection<@NonNull TypeNode>) this.varTyping.get(var));
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
        Set<@NonNull TypeEdge> candidates = new HashSet<>();
        if (var.hasName()) {
            candidates.addAll((Collection<@NonNull TypeEdge>) this.varTyping.get(var));
        } else {
            candidates.addAll(this.typeGraph.edgeSet());
        }
        TypeGuard guard = expr.getWildcardGuard();
        for (TypeEdge typeEdge : guard.filter(candidates)) {
            Set<TypeNode> targetTypes = typeEdge.target()
                .getSubtypes();
            for (TypeNode sourceType : typeEdge.source()
                .getSubtypes()) {
                if (expr.getKind() == EdgeRole.BINARY) {
                    result.add(sourceType, targetTypes);
                } else {
                    result.add(sourceType, sourceType);
                }
            }
        }
        return result;
    }

    @Override
    public Result computeEmpty(Empty expr) {
        Result result = new Result();
        // Nodes can be unified with sub- or supertypes
        for (TypeNode node : this.typeGraph.nodeSet()) {
            for (TypeNode node1 : node.getSubtypes()) {
                result.add(node1, node);
                result.add(node, node1);
            }
        }
        return result;
    }

    /** The Predefined typing of the label variables. */
    private final Map<LabelVar,Set<? extends TypeElement>> varTyping;

    /** The type graph with respect to which the typing is calculated. */
    private final TypeGraph typeGraph;

    /**
     * Outcome of the typing of a regular expression,
     * consisting of a relation between type nodes.
     * Each pair in the relation consists of a potential source and target
     * node of a path through the type graph of which the label sequence
     * is accepted by the regular expression.
     * @author Arend Rensink
     * @version $Revision $
     */
    public static class Result {
        /** Creates an empty relation. */
        public Result() {
            this.size = 0;
        }

        /** Returns the number of pairs in the relation. */
        public int getSize() {
            return this.size;
        }

        /** Indicates if this is an empty relation. */
        public boolean isEmpty() {
            return this.map.isEmpty();
        }

        /** Returns all target nodes related to a given source node.*/
        public Set<TypeNode> getAll(TypeNode left) {
            return this.map.get(left);
        }

        /** Returns the mapping from source nodes to sets of target nodes.*/
        public Map<TypeNode,Set<TypeNode>> getMap() {
            return this.map;
        }

        /** Adds a pair to the relation. */
        public boolean add(TypeNode left, TypeNode right) {
            Set<TypeNode> current = this.map.get(left);
            if (current == null) {
                this.map.put(left, current = new HashSet<>());
            }
            boolean result = current.add(right);
            if (result) {
                this.size++;
            }
            return result;
        }

        /** Adds a set of pairs to the relation. */
        public boolean add(TypeNode left, Collection<? extends TypeNode> right) {
            Set<TypeNode> current = this.map.get(left);
            if (current == null) {
                this.map.put(left, current = new HashSet<>());
            }
            int currentSize = current.size();
            current.addAll(right);
            this.size += current.size() - currentSize;
            return current.size() > currentSize;
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
                Set<TypeNode> allRight = new HashSet<>();
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

        /** Returns the limit of the intersection of this relation with itself. */
        public Result getClosure() {
            Result result = this;
            int oldSize = 0;
            int newSize = result.getSize();
            while (newSize > oldSize) {
                result = result.getThen(this);
                result.union(this);
                oldSize = newSize;
                newSize = result.getSize();
            }
            return result;
        }

        /**
         * Intersects this relation with another.
         * @return {@code true} if this relation was changed as a result
         */
        private boolean union(Result other) {
            return other.copyTo(this);
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
        private boolean copyTo(Result other) {
            boolean result = false;
            for (Map.Entry<TypeNode,Set<TypeNode>> entry : this.map.entrySet()) {
                result |= other.add(entry.getKey(), entry.getValue());
            }
            other.addErrors(getErrors());
            return result;
        }

        private int size;
        private final Map<TypeNode,Set<TypeNode>> map = new HashMap<>();
        private final FormatErrorSet errors = new FormatErrorSet();
    }
}
