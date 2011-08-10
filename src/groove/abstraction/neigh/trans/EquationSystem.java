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
import gnu.trove.THashSet;
import groove.abstraction.neigh.Multiplicity;
import groove.abstraction.neigh.Multiplicity.EdgeMultDir;
import groove.abstraction.neigh.shape.EdgeSignature;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.ShapeEdge;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.graph.TypeLabel;

import java.util.Iterator;
import java.util.Set;

/**
 * @author Eduardo Zambon
 */
public final class EquationSystem {

    private final Materialisation mat;
    private final Set<MultVar> vars;
    private final Set<Equation> eqs;
    private final Set<EdgeBundle> bundles;
    /** The number of multiplicity variables of the system. */
    private int varCount;
    private Multiplicity values[];

    /** EDUARDO: Comment this... */
    public EquationSystem(Materialisation mat) {
        assert !mat.getPossibleNewEdgeSet().isEmpty();
        this.mat = mat;
        this.vars = new THashSet<MultVar>();
        this.varCount = 0;
        this.eqs = new THashSet<Equation>();
        this.bundles = new THashSet<EdgeBundle>();
        this.create();
    }

    private void create() {
        // Create the opposition relations and the edge bundles.
        for (ShapeEdge edge : this.mat.getPossibleNewEdgeSet()) {
            // Opposition relations.
            Equation eq = this.newEquation(Relation.OPPOSITION);
            MultVar outMultVar = this.newEdgeMultVar(edge, OUTGOING);
            MultVar inMultVar = this.newEdgeMultVar(edge, INCOMING);
            eq.addVar(outMultVar);
            eq.addVar(inMultVar);
            // Edge bundles.
            TypeLabel label = edge.label();
            EdgeBundle outBundle =
                this.getEdgeBundle(edge.source(), label, OUTGOING);
            outBundle.addVar(outMultVar);
            EdgeBundle inBundle =
                this.getEdgeBundle(edge.target(), label, INCOMING);
            inBundle.addVar(inMultVar);
        }
        // For each edge bundle, create an equality.
        Shape shape = this.mat.getShape();
        for (EdgeBundle bundle : this.bundles) {
            Multiplicity nodeMult = shape.getNodeMult(bundle.node);
            EdgeSignature es = this.getEdgeSigForBundle(bundle);
            Multiplicity esMult = shape.getEdgeSigMult(es, bundle.direction);
            Multiplicity matchedEdgesMult = this.getMatchedEdgesMult(bundle);
            if (matchedEdgesMult.isZero()) {
                // There are no shape edges that were matched by rule edges.
                // Create another multiplicity variable to represent the value
                // of the remainder multiplicity for the edge signature.
                MultVar var = this.newEdgeSigMultVar(es, bundle.direction);
                bundle.vars.add(var);
            }
            Equation eq = this.newEquation(Relation.EQUALITY);
            eq.setConstant(nodeMult.times(esMult).sub(matchedEdgesMult));
            for (MultVar var : bundle.vars) {
                eq.addVar(var);
            }
        }
        // Create the array of values for the multiplicity variables.
        this.values = new Multiplicity[this.varCount];
    }

    private MultVar newEdgeMultVar(ShapeEdge edge, EdgeMultDir direction) {
        MultVar result = new EdgeMultVar(this.varCount, direction, edge);
        this.vars.add(result);
        this.varCount++;
        return result;
    }

    private MultVar newEdgeSigMultVar(EdgeSignature es, EdgeMultDir direction) {
        MultVar result = new EdgeSigMultVar(this.varCount, direction, es);
        this.vars.add(result);
        this.varCount++;
        return result;
    }

    private Equation newEquation(Relation rel) {
        Equation result = new Equation(rel);
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

    private EdgeSignature getEdgeSigForBundle(EdgeBundle bundle) {
        /*ShapeNode matNode = null;
        ShapeEdge edge = ((EdgeMultVar) bundle.vars.iterator().next()).edge;
        switch (bundle.direction) {
        case OUTGOING:
            matNode = edge.target();
            break;
        case INCOMING:
            matNode = edge.source();
            break;
        default:
            assert false;
        }
        ShapeNode origNode = this.mat.getOriginalCollectorNode(matNode);
        Shape shape = this.mat.getShape();
        return shape.getEdgeSignature(bundle.node, bundle.label,
            shape.getEquivClassOf(origNode));*/
        return null;
    }

    private Multiplicity getMatchedEdgesMult(EdgeBundle bundle) {
        return null;
    }

    /** EDUARDO: Comment this... */
    public Set<Materialisation> solve() {
        Set<Materialisation> result = new THashSet<Materialisation>();
        result.add(this.mat);
        return result;
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
        private final Relation rel;
        private Multiplicity constant;

        private Equation(Relation rel) {
            this.vars = new THashSet<MultVar>();
            this.rel = rel;
        }

        private void setConstant(Multiplicity constant) {
            this.constant = constant;
        }

        private void addVar(MultVar var) {
            assert this.vars.size() < this.rel.varCount;
            this.vars.add(var);
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            Iterator<MultVar> iter = this.vars.iterator();
            switch (this.rel) {
            case OPPOSITION:
                result.append(iter.next());
                result.append("<->");
                result.append(iter.next());
                break;
            case EQUALITY:
                break;
            case GT:
                result.append(iter.next());
                result.append(">" + this.constant);
                break;
            default:
                assert false;
            }
            return result.toString();
        }
    }

    private enum Relation {
        OPPOSITION(2), EQUALITY(Integer.MAX_VALUE), GT(1);

        private final int varCount;

        private Relation(int varCount) {
            this.varCount = varCount;
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
}
