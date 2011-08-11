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
package groove.abstraction.neigh.trans;

import static groove.abstraction.neigh.Multiplicity.EdgeMultDir.INCOMING;
import static groove.abstraction.neigh.Multiplicity.EdgeMultDir.OUTGOING;
import gnu.trove.THashMap;
import gnu.trove.THashSet;
import groove.abstraction.neigh.Multiplicity;
import groove.abstraction.neigh.Multiplicity.EdgeMultDir;
import groove.abstraction.neigh.Multiplicity.MultKind;
import groove.abstraction.neigh.shape.EdgeSignature;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.ShapeEdge;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.graph.TypeLabel;
import groove.trans.RuleEdge;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eduardo Zambon
 */
public final class EquationSystem {

    private final Materialisation mat;
    private final List<MultVar> vars;
    private final Set<Equation> eqs;
    private final Set<EdgeBundle> bundles;
    private final Map<ShapeEdge,EdgeMultVar> outEdgeMultVarMap;
    private final Map<ShapeEdge,EdgeMultVar> inEdgeMultVarMap;
    private final Set<Solution> solutions;
    /** The number of multiplicity variables of the system. */
    private int varCount;

    /** EDUARDO: Comment this... */
    public EquationSystem(Materialisation mat) {
        assert !mat.getPossibleNewEdgeSet().isEmpty();
        this.mat = mat;
        this.vars = new ArrayList<MultVar>();
        this.varCount = 0;
        this.eqs = new THashSet<Equation>();
        this.bundles = new THashSet<EdgeBundle>();
        this.outEdgeMultVarMap = new THashMap<ShapeEdge,EdgeMultVar>();
        this.inEdgeMultVarMap = new THashMap<ShapeEdge,EdgeMultVar>();
        this.solutions = new THashSet<Solution>();
        this.create();
    }

    @Override
    public String toString() {
        return "Equalities: " + this.eqs;
    }

    private void create() {
        Shape shape = this.mat.getShape();

        // BEGIN - Create opposition relations the edge bundles.
        for (ShapeEdge edge : this.mat.getPossibleNewEdgeSet()) {
            // Opposition relations.
            EdgeMultVar outMultVar = this.newEdgeMultVar(edge, OUTGOING);
            EdgeMultVar inMultVar = this.newEdgeMultVar(edge, INCOMING);
            outMultVar.opposite = inMultVar;
            inMultVar.opposite = outMultVar;
            // Edge bundles.
            TypeLabel label = edge.label();
            EdgeBundle outBundle =
                this.getEdgeBundle(edge.source(), label, OUTGOING);
            outBundle.addVar(outMultVar);
            EdgeBundle inBundle =
                this.getEdgeBundle(edge.target(), label, INCOMING);
            inBundle.addVar(inMultVar);
        } // END - Create opposition relations the edge bundles.

        // For each edge bundle, create an equality.
        // BEGIN - Create equalities.
        for (EdgeBundle bundle : this.bundles) {
            EdgeMultDir direction = bundle.direction;
            // Multiplicity of the bundle node.
            Multiplicity nodeMult = shape.getNodeMult(bundle.node);
            // Counter of bundle edges that were matched by the rule.
            Multiplicity matchedEdgesMult =
                Multiplicity.getMultiplicity(0, 0, MultKind.EDGE_MULT);
            // Edge signature of the bundle.
            EdgeSignature es = null;

            boolean hasUnmatchedEdges = false;
            // For all edges in the bundle.
            // BEGIN - Bundle edges loop.
            for (ShapeEdge edge : this.getEdgesForBundle(bundle)) {
                if (!this.hasVariable(edge, direction)) {
                    // There is no variable for the edge. This means that the
                    // edge is already in the shape.

                    // First check if we already have the signature.
                    if (es == null) {
                        // All edges of the bundle that are in the shape have
                        // the same edge signature. This code is only executed
                        // once.
                        es = shape.getEdgeSignature(edge, direction);
                    } // else do nothing, we already have the signature.

                    // Now, check if the shape edge is an image of a rule edge.
                    Set<RuleEdge> edgesR =
                        this.mat.getMatch().getPreImages(edge);
                    if (edgesR.size() > 0) {
                        assert edgesR.size() == 1;
                        // Yes, we have a rule edge. Increment the counter.
                        matchedEdgesMult = matchedEdgesMult.increment();
                    } else {
                        // No. There is no rule edge. Adjust the flag.
                        hasUnmatchedEdges = true;
                    }
                }
            } // END - Bundle edges loop.

            if (es == null) {
                // All edges in the bundle are possible edges and there are no
                // fixed edges in the shape. We need to retrieve the edge
                // signature from the original shape so we can get the proper
                // multiplicity.
                // EDUARDO: Implement this...
            }

            // Check if we need to create an extra variable for the signature.
            if (hasUnmatchedEdges) {
                // There are some edges of the shape in the bundle that were
                // not matched by rule edges. Create another multiplicity
                // variable to represent the value of the remainder
                // multiplicity for the edge signature.
                MultVar esVar = this.newEdgeSigMultVar(es, direction);
                bundle.vars.add(esVar);
            }

            // Multiplicity of the edge signature.
            Multiplicity esMult = shape.getEdgeSigMult(es, direction);
            // Create the equation.
            Equation eq = this.newEquation();
            // The constant = (nodeMult * esMult) - matchedEdgesMult .
            eq.setConstant(nodeMult.times(esMult).sub(matchedEdgesMult));
            // Add all variables of the bundle to the equation.
            for (MultVar var : bundle.vars) {
                eq.addVar(var);
            }
        } // END - Create equalities.
    }

    private EdgeMultVar newEdgeMultVar(ShapeEdge edge, EdgeMultDir direction) {
        EdgeMultVar result = new EdgeMultVar(this.varCount, direction, edge);
        this.getEdgeMultVarMap(direction).put(edge, result);
        this.vars.add(result);
        this.varCount++;
        return result;
    }

    private EdgeSigMultVar newEdgeSigMultVar(EdgeSignature es,
            EdgeMultDir direction) {
        EdgeSigMultVar result =
            new EdgeSigMultVar(this.varCount, direction, es);
        this.vars.add(result);
        this.varCount++;
        return result;
    }

    private Equation newEquation() {
        Equation result = new Equation();
        this.eqs.add(result);
        return result;
    }

    private EdgeBundle getEdgeBundle(ShapeNode node, TypeLabel label,
            EdgeMultDir direction) {
        EdgeBundle result = null;
        for (EdgeBundle bundle : this.bundles) {
            if (bundle.node.equals(node) && bundle.label.equals(label)
                && bundle.direction == direction) {
                result = bundle;
                break;
            }
        }
        if (result == null) {
            result = new EdgeBundle(node, label, direction);
            this.bundles.add(result);
        }
        return result;
    }

    private Set<ShapeEdge> getEdgesForBundle(EdgeBundle bundle) {
        Set<ShapeEdge> result = new THashSet<ShapeEdge>();
        for (ShapeEdge possibleEdge : this.mat.getPossibleNewEdgeSet()) {
            MultVar var = this.getEdgeVariable(possibleEdge, bundle.direction);
            if (bundle.vars.contains(var)) {
                result.add(possibleEdge);
            }
        }
        for (ShapeEdge edgeS : this.mat.getShape().binaryEdgeSet(bundle.node,
            bundle.direction)) {
            if (edgeS.label().equals(bundle.label)) {
                // EDUARDO: Fix this. This is wrong. If a node has more than
                // one bundle, all edges are mixed...
                result.add(edgeS);
            }
        }
        return result;
    }

    private boolean hasVariable(ShapeEdge edge, EdgeMultDir direction) {
        return this.getEdgeVariable(edge, direction) != null;
    }

    private EdgeMultVar getEdgeVariable(ShapeEdge edge, EdgeMultDir direction) {
        return this.getEdgeMultVarMap(direction).get(edge);
    }

    /** Returns the proper map accordingly to the given direction. */
    private Map<ShapeEdge,EdgeMultVar> getEdgeMultVarMap(EdgeMultDir direction) {
        Map<ShapeEdge,EdgeMultVar> result = null;
        switch (direction) {
        case OUTGOING:
            result = this.outEdgeMultVarMap;
            break;
        case INCOMING:
            result = this.inEdgeMultVarMap;
            break;
        default:
            assert false;
        }
        return result;
    }

    /** EDUARDO: Comment this... */
    public Set<Materialisation> solve() {
        // Compute all solutions.
        Solution initialSol = new Solution(this.varCount);
        Set<Solution> partialSols = new THashSet<Solution>();
        partialSols.add(initialSol);
        while (!partialSols.isEmpty()) {
            Solution sol = partialSols.iterator().next();
            partialSols.remove(sol);
            for (Solution newSol : this.iterateSolution(sol)) {
                if (newSol.isComplete()) {
                    this.solutions.add(newSol);
                } else {
                    partialSols.add(newSol);
                }
            }
        }
        // Create the return objects.
        Set<Materialisation> result = new THashSet<Materialisation>();
        if (this.solutions.size() == 1) {
            // Only one solution, no need to clone the materialisation object.
            this.updateMatFromSolution(this.mat,
                this.solutions.iterator().next());
            result.add(this.mat);
        } else {
            // OK, we need to clone.
            for (Solution sol : this.solutions) {
                Materialisation mat = this.mat.clone();
                this.updateMatFromSolution(mat, sol);
                result.add(mat);
            }
        }
        return result;
    }

    private Set<Solution> iterateSolution(Solution sol) {
        Set<Solution> result = new THashSet<Solution>();

        return result;
    }

    private void updateMatFromSolution(Materialisation mat, Solution sol) {

    }

    private static abstract class MultVar {

        final int number;
        final EdgeMultDir direction;

        MultVar(int number, EdgeMultDir direction) {
            this.number = number;
            this.direction = direction;
        }

        @Override
        public abstract String toString();
    }

    private static class EdgeMultVar extends MultVar {

        private final ShapeEdge edge;
        private EdgeMultVar opposite;

        private EdgeMultVar(int number, EdgeMultDir direction, ShapeEdge edge) {
            super(number, direction);
            this.edge = edge;
        }

        @Override
        public String toString() {
            return "x" + this.number;
        }
    }

    private static class EdgeSigMultVar extends MultVar {

        private final EdgeSignature es;

        private EdgeSigMultVar(int number, EdgeMultDir direction,
                EdgeSignature es) {
            super(number, direction);
            this.es = es;
        }

        @Override
        public String toString() {
            return "y" + this.number;
        }
    }

    private static class Equation {

        private final Set<MultVar> vars;
        private Multiplicity constant;

        private Equation() {
            this.vars = new THashSet<MultVar>();
        }

        private void setConstant(Multiplicity constant) {
            this.constant = constant;
        }

        private void addVar(MultVar var) {
            this.vars.add(var);
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            Iterator<MultVar> iter = this.vars.iterator();
            while (iter.hasNext()) {
                result.append(iter.next().toString());
                if (iter.hasNext()) {
                    result.append("+");
                }
            }
            result.append("=" + this.constant);
            return result.toString();
        }

        private void computeNewValues(Solution partialSol) {
            if (this.hasFullAssignment(partialSol)) {
                return;
            }
            Multiplicity zero =
                Multiplicity.getMultiplicity(0, 0, MultKind.EDGE_MULT);
            if (this.constant.isZero()) {
                // Since multiplicities can't be negative, this means that all
                // variables in the equation have to be zero.
                for (MultVar var : this.vars) {
                    partialSol.setValue(var, zero);
                    if (var instanceof EdgeMultVar) {
                        partialSol.setValue(((EdgeMultVar) var).opposite, zero);
                    }
                }
            }
        }

        private boolean hasFullAssignment(Solution partialSol) {
            boolean result = true;
            for (MultVar var : this.vars) {
                if (!partialSol.hasValue(var)) {
                    result = false;
                    break;
                }
            }
            return result;
        }
    }

    private static class EdgeBundle {

        private final ShapeNode node;
        private final TypeLabel label;
        private final EdgeMultDir direction;
        private final Set<MultVar> vars;

        private EdgeBundle(ShapeNode node, TypeLabel label,
                EdgeMultDir direction) {
            this.node = node;
            this.label = label;
            this.direction = direction;
            this.vars = new THashSet<MultVar>();
        }

        @Override
        public String toString() {
            return this.direction + ":" + this.node + "-" + this.label + "-"
                + this.vars;
        }

        private void addVar(MultVar var) {
            assert var.direction == this.direction;
            this.vars.add(var);
        }
    }

    private static class Solution {

        private final Multiplicity values[];
        private int valuesCount;

        private Solution(int varCount) {
            this.values = new Multiplicity[varCount];
            this.valuesCount = 0;
        }

        private boolean isComplete() {
            return this.valuesCount == this.values.length;
        }

        private boolean hasValue(MultVar var) {
            return this.getValue(var) != null;
        }

        private Multiplicity getValue(MultVar var) {
            return this.values[var.number];
        }

        private void setValue(MultVar var, Multiplicity mult) {
            assert !this.hasValue(var);
            assert mult != null;
            this.values[var.number] = mult;
            this.valuesCount++;
        }

        @Override
        public Solution clone() {
            Solution result = new Solution(this.values.length);
            for (int i = 0; i < this.values.length; i++) {
                result.values[i] = this.values[i];
            }
            result.valuesCount = this.valuesCount;
            return result;
        }
    }
}
