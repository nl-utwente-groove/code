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

import static groove.abstraction.neigh.Multiplicity.OMEGA;
import static groove.abstraction.neigh.Multiplicity.EdgeMultDir.INCOMING;
import static groove.abstraction.neigh.Multiplicity.EdgeMultDir.OUTGOING;
import gnu.trove.THashMap;
import gnu.trove.THashSet;
import gnu.trove.TIntArrayList;
import groove.abstraction.neigh.Multiplicity;
import groove.abstraction.neigh.Multiplicity.EdgeMultDir;
import groove.abstraction.neigh.Multiplicity.MultKind;
import groove.abstraction.neigh.shape.EdgeSignature;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.ShapeEdge;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.graph.TypeLabel;
import groove.util.Duo;
import groove.util.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Eduardo Zambon
 */
public final class EquationSystem {

    /** EDUARDO: Comment this... */
    public final static EquationSystem newEqSys(Materialisation mat) {
        return new EquationSystem(mat, 1);
    }

    private final Materialisation mat;
    private final int stage;
    private final Set<Equation> trivialEqs;
    private final Set<Equation> nonTrivialEqs;
    private final Map<ShapeEdge,Duo<BoundVar>> edgeVarsMap;
    private final TIntArrayList lowerBoundRange;
    private final TIntArrayList upperBoundRange;
    private int varsCount;

    private EquationSystem(Materialisation mat, int stage) {
        assert mat != null;
        assert stage >= 1 && stage <= 3;
        this.mat = mat;
        this.stage = stage;
        this.trivialEqs = new THashSet<Equation>();
        this.nonTrivialEqs = new THashSet<Equation>();
        this.edgeVarsMap = new THashMap<ShapeEdge,Duo<BoundVar>>();
        this.lowerBoundRange = new TIntArrayList();
        this.upperBoundRange = new TIntArrayList();
        this.varsCount = 0;
        this.create();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Equation System:\n");
        sb.append("Trivial Equations:\n");
        for (Equation eq : this.trivialEqs) {
            sb.append(eq + "\n");
        }
        sb.append("Non-trivial Equations:\n");
        for (Equation eq : this.nonTrivialEqs) {
            sb.append(eq + "\n");
        }
        return sb.toString();
    }

    private void create() {
        this.fillBoundRanges();
        switch (this.stage) {
        case 1:
            this.createFirstStage();
            break;
        case 2:
            this.createSecondStage();
            break;
        case 3:
            this.createThirdStage();
            break;
        default:
            assert false;
        }
    }

    private void fillBoundRanges() {
        MultKind kind = null;
        switch (this.stage) {
        case 1:
            kind = MultKind.EQSYS_MULT;
            break;
        case 2:
            kind = MultKind.EDGE_MULT;
            break;
        case 3:
            kind = MultKind.NODE_MULT;
            break;
        default:
            assert false;
        }
        int b = Multiplicity.getBound(kind);
        for (int i = 0; i <= b + 1; i++) {
            this.lowerBoundRange.add(i);
            if (i == b + 1) {
                this.upperBoundRange.add(OMEGA);
            } else {
                this.upperBoundRange.add(i);
            }
        }
    }

    private void createFirstStage() {
        assert this.stage == 1;
        Shape shape = this.mat.getShape();

        // Create the edge bundles.
        Set<EdgeBundle> bundles = new THashSet<EdgeBundle>();
        for (ShapeEdge edge : this.mat.getAffectedEdges()) {
            this.addToEdgeBundle(bundles, edge.source(), edge, OUTGOING);
            this.addToEdgeBundle(bundles, edge.target(), edge, INCOMING);
        }

        // For each bundle...
        for (EdgeBundle bundle : bundles) {
            bundle.computeAdditionalEdges(this.mat);
            int varsCount = bundle.edges.size();
            Multiplicity nodeMult = shape.getNodeMult(bundle.node);
            Multiplicity edgeMult = bundle.origEsMult;
            Multiplicity constMult = nodeMult.times(edgeMult);
            // ... create one lower bound and one upper bound equation.
            Equation lbEq =
                new Equation(BoundType.LB, Relation.GE,
                    constMult.getLowerBound(), varsCount);
            Equation ubEq =
                new Equation(BoundType.UB, Relation.LE,
                    constMult.getUpperBound(), varsCount);

            // For each edge in the bundle...
            for (ShapeEdge edge : bundle.edges) {
                // ... create two bound variables.
                Pair<BoundVar,BoundVar> varPair = retrieveBoundVars(edge);
                BoundVar lbVar = varPair.one();
                BoundVar ubVar = varPair.two();
                lbEq.addVar(lbVar);
                ubEq.addVar(ubVar);

                // Create one additional equations for the fixed edges.
                if (bundle.direction == OUTGOING && this.mat.isFixed(edge)) {
                    Equation trivialLbEq =
                        new Equation(BoundType.LB, Relation.GE, 1, 1);
                    Equation trivialUbEq =
                        new Equation(BoundType.UB, Relation.LE, 1, 1);
                    trivialLbEq.addVar(lbVar);
                    trivialUbEq.addVar(ubVar);
                    this.storeEquations(trivialLbEq, trivialUbEq);
                }
            }
            this.storeEquations(lbEq, ubEq);
        }
    }

    private void addToEdgeBundle(Set<EdgeBundle> bundles, ShapeNode node,
            ShapeEdge edge, EdgeMultDir direction) {
        EdgeBundle result = null;
        TypeLabel label = edge.label();

        Shape origShape = this.mat.getOriginalShape();
        EdgeSignature origEs =
            this.mat.getShapeMorphism().getEdgeSignature(origShape,
                this.mat.getShape().getEdgeSignature(edge, direction));

        for (EdgeBundle bundle : bundles) {
            if (bundle.node.equals(node) && bundle.label.equals(label)
                && bundle.direction == direction
                && bundle.origEs.equals(origEs)) {
                result = bundle;
                break;
            }
        }

        if (result == null) {
            Multiplicity origEsMult =
                origShape.getEdgeSigMult(origEs, direction);
            result = new EdgeBundle(origEs, origEsMult, node, label, direction);
            bundles.add(result);
        }

        result.edges.add(edge);
    }

    private Duo<BoundVar> retrieveBoundVars(ShapeEdge edge) {
        Duo<BoundVar> vars = this.edgeVarsMap.get(edge);
        if (vars == null) {
            BoundVar lbVar = new BoundVar(this.varsCount, BoundType.LB);
            BoundVar ubVar = new BoundVar(this.varsCount, BoundType.UB);
            vars = new Duo<BoundVar>(lbVar, ubVar);
            this.edgeVarsMap.put(edge, vars);
            this.varsCount++;
        }
        return vars;
    }

    private void storeEquations(Equation lbEq, Equation ubEq) {
        // Fill the duality relation first, before we store the equations.
        lbEq.setDual(ubEq);
        ubEq.setDual(lbEq);
        // Now store.
        if (lbEq.isUseful()) {
            if (lbEq.isTrivial()) {
                this.trivialEqs.add(lbEq);
            } else {
                this.nonTrivialEqs.add(lbEq);
            }
        }
        if (ubEq.isUseful()) {
            if (ubEq.isTrivial()) {
                this.trivialEqs.add(ubEq);
            } else {
                this.nonTrivialEqs.add(ubEq);
            }
        }
    }

    private void createSecondStage() {
        // todo
    }

    private void createThirdStage() {
        // todo
    }

    /** EDUARDO: Comment this... */
    public void solve(Set<Materialisation> result) {
        // todo
    }

    private static class EdgeBundle {

        final EdgeSignature origEs;
        final Multiplicity origEsMult;
        final ShapeNode node;
        final TypeLabel label;
        final EdgeMultDir direction;
        final Set<ShapeEdge> edges;

        EdgeBundle(EdgeSignature origEs, Multiplicity origEsMult,
                ShapeNode node, TypeLabel label, EdgeMultDir direction) {
            this.origEs = origEs;
            this.origEsMult = origEsMult;
            this.node = node;
            this.label = label;
            this.direction = direction;
            this.edges = new THashSet<ShapeEdge>();
        }

        @Override
        public String toString() {
            return this.direction + ":" + this.node + "-" + this.label + "-"
                + this.edges;
        }

        void computeAdditionalEdges(Materialisation mat) {
            Shape shape = mat.getShape();
            for (ShapeEdge edgeS : shape.binaryEdgeSet(this.node,
                this.direction)) {
                if (edgeS.label().equals(this.label)) {
                    EdgeSignature es =
                        shape.getEdgeSignature(edgeS, this.direction);
                    EdgeSignature otherOrigEs =
                        mat.getShapeMorphism().getEdgeSignature(
                            mat.getOriginalShape(), es);
                    if (otherOrigEs.equals(this.origEs)) {
                        this.edges.add(edgeS);
                    }
                }
            }
        }

    }

    private enum BoundType {
        LB, UB
    }

    private static class BoundVar {

        final int number;
        final BoundType type;

        BoundVar(int number, BoundType type) {
            this.number = number;
            this.type = type;
        }

        @Override
        public String toString() {
            String prefix = "";
            switch (this.type) {
            case LB:
                prefix = "x_";
                break;
            case UB:
                prefix = "x^";
                break;
            default:
                assert false;
            }
            return prefix + this.number;
        }
    }

    private enum Relation {
        LE, GE
    }

    private class Equation {

        final BoundType type;
        final ArrayList<BoundVar> vars;
        final Relation relation;
        final int constant;
        Equation dual;

        Equation(BoundType type, Relation relation, int constant, int varsCount) {
            this.type = type;
            this.vars = new ArrayList<BoundVar>(varsCount);
            this.relation = relation;
            this.constant = constant;
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            Iterator<BoundVar> iter = this.vars.iterator();
            while (iter.hasNext()) {
                result.append(iter.next().toString());
                if (iter.hasNext()) {
                    result.append(" + ");
                }
            }
            switch (this.relation) {
            case LE:
                result.append(" <= ");
                break;
            case GE:
                result.append(" >= ");
                break;
            default:
                assert false;
            }
            if (this.constant == OMEGA) {
                result.append("w");
            } else {
                result.append(this.constant);
            }
            return result.toString();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + this.constant;
            result = prime * result + this.relation.hashCode();
            result = prime * result + this.type.hashCode();
            for (BoundVar var : this.vars) {
                result = prime * result + var.number;
            }
            return result;
        }

        @Override
        public boolean equals(Object o) {
            boolean result;
            if (!(o instanceof Equation)) {
                result = false;
            } else {
                Equation eq = (Equation) o;
                result =
                    this.constant == eq.constant && this.type == eq.type
                        && this.relation == eq.relation
                        && this.vars.containsAll(eq.vars)
                        && eq.vars.containsAll(this.vars);
            }
            return result;
        }

        boolean isTrivial() {
            return this.vars.size() == 1;
        }

        void addVar(BoundVar var) {
            assert var.type == this.type;
            this.vars.add(var);
        }

        void setDual(Equation dual) {
            assert (this.type == BoundType.LB && dual.type == BoundType.UB)
                || (this.type == BoundType.UB && dual.type == BoundType.LB);
            if (dual.isUseful()) {
                this.dual = dual;
            }
        }

        boolean isUseful() {
            boolean result = false;
            switch (this.relation) {
            case LE:
                result = this.constant < this.getMaxRangeValue();
                break;
            case GE:
                result = this.constant > this.getMinRangeValue();
                break;
            default:
                assert false;
            }
            return result;
        }

        TIntArrayList getBoundRange() {
            TIntArrayList result = null;
            switch (this.type) {
            case LB:
                result = EquationSystem.this.lowerBoundRange;
                break;
            case UB:
                result = EquationSystem.this.upperBoundRange;
                break;
            default:
                assert false;
            }
            return result;
        }

        int getMaxRangeValue() {
            TIntArrayList boundRange = this.getBoundRange();
            return boundRange.get(boundRange.size() - 1);
        }

        int getMinRangeValue() {
            return this.getBoundRange().get(0);
        }
    }

}
