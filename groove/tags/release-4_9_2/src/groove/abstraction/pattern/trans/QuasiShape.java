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
package groove.abstraction.pattern.trans;

import groove.abstraction.Multiplicity;
import groove.abstraction.MyHashMap;
import groove.abstraction.MyHashSet;
import groove.abstraction.pattern.match.Match;
import groove.abstraction.pattern.shape.PatternEdge;
import groove.abstraction.pattern.shape.PatternGraph;
import groove.abstraction.pattern.shape.PatternNode;
import groove.abstraction.pattern.shape.PatternShape;
import groove.abstraction.pattern.shape.TypeEdge;
import groove.abstraction.pattern.shape.TypeNode;
import groove.util.Duo;
import groove.util.Pair;
import groove.util.collect.NestedIterator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of a quasi-shape.
 *  
 * @author Eduardo Zambon
 */
public final class QuasiShape extends PatternGraph {

    // ------------------------------------------------------------------------
    // Static methods
    // ------------------------------------------------------------------------

    /** Devolution of a pattern shape. */
    public static QuasiShape devolve(PatternShape pShape) {
        return new QuasiShape(pShape);
    }

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    /** Constraint sets. */
    private final Map<PatternNode,Set<Constraint>> nodeConstrMap;
    private final Map<PatternNode,Set<Constraint>> edgeConstrMap;
    /** Variable maps. */
    final Map<PatternNode,MultVar> nodeVarMap;
    final Map<PatternEdge,MultVar> edgeVarMap;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Basic constructor. */
    private QuasiShape(PatternGraph pGraph) {
        super(pGraph);
        this.nodeConstrMap = new MyHashMap<PatternNode,Set<Constraint>>();
        this.edgeConstrMap = new MyHashMap<PatternNode,Set<Constraint>>();
        this.nodeVarMap = new MyHashMap<PatternNode,MultVar>();
        this.edgeVarMap = new MyHashMap<PatternEdge,MultVar>();
    }

    /** Default constructor. */
    private QuasiShape(PatternShape pShape) {
        this((PatternGraph) pShape);
        // Constraints.
        for (PatternNode pNode : pShape.getLayerNodes(0)) {
            Multiplicity pMult = pShape.getMult(pNode);
            addNodeConstraint(pNode, pMult);
        }
        for (PatternEdge dEdge : pShape.edgeSet()) {
            Multiplicity dMult = pShape.getMult(dEdge);
            addEdgeConstraint(dEdge, dMult);
        }
    }

    /** Copying constructor. */
    private QuasiShape(QuasiShape qShape) {
        this((PatternGraph) qShape);
        for (Constraint constr : qShape.getConstraints()) {
            copyConstraint(constr);
        }
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public QuasiShape clone() {
        return new QuasiShape(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append("Node constraints: [\n");
        for (Constraint constr : getNodeConstraints()) {
            sb.append(constr.toString() + "\n");
        }
        sb.replace(sb.length() - 1, sb.length(), "]\n");
        sb.append("Edge constraints: [\n");
        for (Constraint constr : getEdgeConstraints()) {
            sb.append(constr.toString() + "\n");
        }
        sb.replace(sb.length() - 1, sb.length(), "]\n");
        return sb.toString();
    }

    @Override
    public boolean deletePattern(PatternNode node) {
        boolean modified = super.deletePattern(node);
        if (modified && node.isNodePattern()) {
            // We need to adjust the constraints.
            this.nodeConstrMap.remove(node);
            this.nodeVarMap.remove(node);
        }
        return modified;
    }

    @Override
    public PatternNode addNodePattern(TypeNode tNode) {
        PatternNode pNode = super.addNodePattern(tNode);
        addNodeConstraint(pNode, Multiplicity.ONE_NODE_MULT);
        return pNode;
    }

    @Override
    public Pair<PatternNode,Duo<PatternEdge>> addEdgePattern(TypeEdge m1,
            TypeEdge m2, PatternNode p1, PatternNode p2) {
        Pair<PatternNode,Duo<PatternEdge>> pair =
            super.addEdgePattern(m1, m2, p1, p2);
        PatternEdge d1 = pair.two().one();
        PatternEdge d2 = pair.two().two();
        addEdgeConstraint(d1, Multiplicity.ONE_EDGE_MULT);
        addEdgeConstraint(d2, Multiplicity.ONE_EDGE_MULT);
        return pair;
    }

    @Override
    public void prepareClosure(Match match) {
        PatternRule pRule = match.getRule();
        RuleNode rNode = pRule.getCreatorNodes()[0];
        Duo<RuleEdge> inEdges = pRule.rhs().getIncomingEdges(rNode);
        RuleEdge r1 = inEdges.one();
        RuleEdge r2 = inEdges.two();
        PatternNode p1 = match.getNode(r1.source());
        PatternNode p2 = match.getNode(r2.source());
        disambiguate(p1);
        disambiguate(p2);
    }

    @Override
    public Pair<PatternNode,Duo<PatternEdge>> closePattern(TypeEdge m1,
            TypeEdge m2, PatternNode p1, PatternNode p2) {
        Pair<PatternNode,Duo<PatternEdge>> pair =
            super.closePattern(m1, m2, p1, p2);
        PatternEdge d1 = pair.two().one();
        PatternEdge d2 = pair.two().two();
        addEdgeConstraint(d1, Multiplicity.ZERO_PLUS_EDGE_MULT);
        addEdgeConstraint(d2, Multiplicity.ZERO_PLUS_EDGE_MULT);
        return pair;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Disambiguation operation. */
    public void disambiguate(PatternNode p) {
        Duo<TypeEdge> duo = getDuoIncomingEdgeTypes(p);
        TypeEdge m1 = duo.one();
        TypeEdge m2 = duo.two();
        Collection<PatternNode> newNodes = disambiguate(p, m1);
        for (PatternNode newNode : newNodes) {
            // The new nodes are unambiguous w.r.t. m1, so now do m2.
            disambiguate(newNode, m2);
        }
    }

    private Collection<PatternNode> disambiguate(PatternNode p, TypeEdge m) {
        if (getInEdgesWithType(p, m).size() == 1) {
            // Nothing to do since m is already unambiguous in p.
            return Collections.singleton(p);
        }

        // Create the new nodes.
        Map<PatternEdge,PatternNode> origEdgeMap =
            new MyHashMap<PatternEdge,PatternNode>();
        for (PatternEdge inEdge : getInEdgesWithType(p, m)) {
            PatternNode newNode = createNode(p.getType());
            addNode(newNode);
            origEdgeMap.put(inEdge, newNode);
        }

        Collection<PatternEdge> newEdges =
            new ArrayList<PatternEdge>(origEdgeMap.values().size());

        // Create the new incoming edges.
        for (PatternEdge d : inEdgeSet(p)) {
            PatternNode src = d.source();
            TypeEdge type = d.getType();

            Collection<PatternNode> tgts;
            if (origEdgeMap.keySet().contains(d)) {
                // Only create a single new edge.
                tgts = Collections.singleton(origEdgeMap.get(d));
            } else {
                // Duplicate the d edge to all new nodes.
                tgts = origEdgeMap.values();
            }

            newEdges.clear();
            for (PatternNode tgt : tgts) {
                PatternEdge newEdge = createEdge(src, type, tgt);
                addEdgeContext(newEdge);
                newEdges.add(newEdge);
            }

            // Update the constraints.
            for (Constraint constr : this.edgeConstrMap.get(src)) {
                if (constr.contains(d)) {
                    constr.replaceVar(d, newEdges);
                }
            }
        }

        Set<Constraint> origOutConstrs = this.edgeConstrMap.get(p);
        if (origOutConstrs == null) {
            origOutConstrs = Collections.emptySet();
        }
        Map<PatternEdge,PatternEdge> replacingMap =
            new MyHashMap<PatternEdge,PatternEdge>();

        // Create the new outgoing edges.
        for (PatternNode newSrc : origEdgeMap.values()) {
            replacingMap.clear();
            for (PatternEdge d : outEdgeSet(p)) {
                // Duplicate the d edge to all new nodes.
                TypeEdge type = d.getType();
                PatternNode tgt = d.target();
                PatternEdge newEdge = createEdge(newSrc, type, tgt);
                addEdgeContext(newEdge);
                replacingMap.put(d, newEdge);
            }

            // Duplicate the constraints.
            for (Constraint origOutConstr : origOutConstrs) {
                Iterator<MultVar> varsIter = origOutConstr.vars.iterator();
                PatternEdge origEdge = varsIter.next().edge;
                PatternEdge newEdge = replacingMap.get(origEdge);
                Constraint newOutConstr =
                    addEdgeConstraint(newEdge, origOutConstr.mult);
                while (varsIter.hasNext()) {
                    origEdge = varsIter.next().edge;
                    newEdge = replacingMap.get(origEdge);
                    newOutConstr.addVar(newEdge);
                }
            }
        }

        // Remove p and all adjacent edges.
        removeNodeContext(p);
        this.edgeConstrMap.remove(p);

        return origEdgeMap.values();
    }

    private Iterable<Constraint> getConstraints() {
        Collection<Iterator<Constraint>> iters =
            new ArrayList<Iterator<Constraint>>();
        for (Set<Constraint> set : this.nodeConstrMap.values()) {
            iters.add(set.iterator());
        }
        for (Set<Constraint> set : this.edgeConstrMap.values()) {
            iters.add(set.iterator());
        }
        return new ConstraintIterable(iters);
    }

    private Iterable<Constraint> getNodeConstraints() {
        Collection<Iterator<Constraint>> iters =
            new ArrayList<Iterator<Constraint>>();
        for (Set<Constraint> set : this.nodeConstrMap.values()) {
            iters.add(set.iterator());
        }
        return new ConstraintIterable(iters);
    }

    private Iterable<Constraint> getEdgeConstraints() {
        Collection<Iterator<Constraint>> iters =
            new ArrayList<Iterator<Constraint>>();
        for (Set<Constraint> set : this.edgeConstrMap.values()) {
            iters.add(set.iterator());
        }
        return new ConstraintIterable(iters);
    }

    private void addNodeConstraint(PatternNode pNode, Multiplicity pMult) {
        Constraint constr = new Constraint(pNode, pMult);
        addToNodeConstraintMap(pNode, constr);
    }

    private Constraint addEdgeConstraint(PatternEdge dEdge, Multiplicity dMult) {
        Constraint constr = new Constraint(dEdge, dMult);
        addToEdgeConstraintMap(dEdge.source(), constr);
        return constr;
    }

    private void copyConstraint(Constraint constr) {
        Constraint newConstr = new Constraint(constr);
        if (newConstr.isNodeConstr()) {
            addToNodeConstraintMap(newConstr.getNode(), newConstr);
        } else {
            addToEdgeConstraintMap(newConstr.srcNode, constr);
        }
    }

    private void addToNodeConstraintMap(PatternNode pNode, Constraint constr) {
        Set<Constraint> constrs = this.nodeConstrMap.get(pNode);
        if (constrs == null) {
            constrs = new MyHashSet<Constraint>();
            this.nodeConstrMap.put(pNode, constrs);
        }
        constrs.add(constr);
    }

    private void addToEdgeConstraintMap(PatternNode srcNode, Constraint constr) {
        Set<Constraint> constrs = this.edgeConstrMap.get(srcNode);
        if (constrs == null) {
            constrs = new MyHashSet<Constraint>();
            this.edgeConstrMap.put(srcNode, constrs);
        }
        constrs.add(constr);
    }

    /** Compute the set of materialisations for this quasi-shape. */
    public void getMaterialisations(Collection<PatternShape> result) {
        for (Solution sol : computeSolutions()) {
            PatternShape mat = sol.createShape();
            result.add(mat);
        }
    }

    private Set<Solution> computeSolutions() {
        Set<Solution> result = new MyHashSet<Solution>();
        for (Constraint constr : getConstraints()) {
            constr.solve(result);
        }
        return result;
    }

    // ------------------------------------------------------------------------
    // Inner Classes
    // ------------------------------------------------------------------------

    // ---------
    // Iterables
    // ---------

    private static final class ConstraintIterable implements
            Iterable<Constraint> {

        Collection<Iterator<Constraint>> iters;

        ConstraintIterable(Collection<Iterator<Constraint>> iters) {
            this.iters = iters;
        }

        @Override
        public Iterator<Constraint> iterator() {
            return new NestedIterator<Constraint>(this.iters);
        }
    }

    // -------
    // MultVar
    // -------

    private static final class MultVar {

        final PatternNode node;
        final PatternEdge edge;

        MultVar(PatternNode node) {
            assert node.isNodePattern();
            this.node = node;
            this.edge = null;
        }

        MultVar(PatternEdge edge) {
            this.node = null;
            this.edge = edge;
        }

        @Override
        public String toString() {
            String suffix;
            if (isNodeVar()) {
                suffix = this.node.getIdStr();
            } else {
                suffix = this.edge.getIdStr();
            }
            return "x." + suffix;
        }

        boolean isNodeVar() {
            return this.node != null;
        }

    }

    // ----------
    // Constraint
    // ----------

    private final class Constraint {

        final PatternNode srcNode;
        final Set<MultVar> vars;
        final Multiplicity mult;

        Constraint(Multiplicity mult, PatternNode srcNode) {
            assert (srcNode == null && mult.isNodeKind())
                || (srcNode != null && mult.isEdgeKind());
            this.srcNode = srcNode;
            this.vars = new MyHashSet<MultVar>();
            this.mult = mult;
        }

        Constraint(Constraint constr) {
            this.srcNode = constr.srcNode;
            this.mult = constr.mult;
            this.vars = new MyHashSet<MultVar>();
            this.vars.addAll(constr.vars);
        }

        Constraint(PatternNode pNode, Multiplicity mult) {
            this(mult, null);
            addVar(pNode);
        }

        Constraint(PatternEdge pEdge, Multiplicity mult) {
            this(mult, pEdge.source());
            addVar(pEdge);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (MultVar var : this.vars) {
                sb.append(var.toString() + " + ");
            }
            int l = sb.length();
            sb.replace(l - 2, l - 1, "=");
            sb.append(this.mult.toString());
            return sb.toString();
        }

        boolean isNodeConstr() {
            return this.srcNode == null;
        }

        boolean isTrivial() {
            return this.vars.size() == 1;
        }

        PatternNode getNode() {
            return this.vars.iterator().next().node;
        }

        void addVar(PatternNode pNode) {
            assert isNodeConstr();
            MultVar var = new MultVar(pNode);
            QuasiShape.this.nodeVarMap.put(pNode, var);
            this.vars.add(var);
        }

        void addVar(PatternEdge pEdge) {
            assert pEdge.source().equals(this.srcNode);
            MultVar var = new MultVar(pEdge);
            QuasiShape.this.edgeVarMap.put(pEdge, var);
            this.vars.add(var);
        }

        void removeVar(PatternEdge pEdge) {
            assert pEdge.source().equals(this.srcNode);
            assert contains(pEdge);
            MultVar var = QuasiShape.this.edgeVarMap.get(pEdge);
            this.vars.remove(var);
        }

        void replaceVar(PatternEdge origEdge, Collection<PatternEdge> newEdges) {
            for (PatternEdge newEdge : newEdges) {
                addVar(newEdge);
            }
            removeVar(origEdge);
        }

        boolean contains(PatternEdge pEdge) {
            MultVar var = QuasiShape.this.edgeVarMap.get(pEdge);
            return this.vars.contains(var);
        }

        void solve(Set<Solution> partialSols) {
            Solution currSol = null;

            if (partialSols.isEmpty()) {
                currSol = new Solution();
                partialSols.add(currSol);
            } else if (partialSols.size() == 1) {
                currSol = partialSols.iterator().next();
            } else {
                assert false : "Implement proper solving!";
            }

            if (isTrivial()) {
                MultVar singleVar = this.vars.iterator().next();
                currSol.setValue(singleVar, this.mult);
            } else {
                assert false : "Implement proper solving!";
            }
        }

    }

    // --------
    // Solution
    // --------

    private final class Solution {

        final Map<MultVar,Multiplicity> varToValues;

        Solution() {
            this.varToValues = new MyHashMap<MultVar,Multiplicity>();
        }

        Solution(Solution sol) {
            this();
            this.varToValues.putAll(sol.varToValues);
        }

        @Override
        public Solution clone() {
            return new Solution(this);
        }

        @Override
        public String toString() {
            return this.varToValues.toString();
        }

        void setValue(MultVar var, Multiplicity value) {
            assert !isSet(var) : "Over-writting variable is solution!";
            this.varToValues.put(var, value);
        }

        boolean isSet(MultVar var) {
            return this.varToValues.get(var) != null;
        }

        MultVar getVar(PatternNode pNode) {
            return QuasiShape.this.nodeVarMap.get(pNode);
        }

        MultVar getVar(PatternEdge pEdge) {
            return QuasiShape.this.edgeVarMap.get(pEdge);
        }

        PatternShape createShape() {
            QuasiShape qShape = QuasiShape.this;
            PatternShape result = new PatternShape(qShape);
            for (PatternNode pNode : qShape.getLayerNodes(0)) {
                MultVar nodeVar = getVar(pNode);
                Multiplicity nodeMult = this.varToValues.get(nodeVar);
                assert nodeMult != null : String.format(
                    "Node %s has no image.", pNode);
                result.setMult(pNode, nodeMult);
            }
            for (PatternEdge pEdge : qShape.edgeSet()) {
                MultVar edgeVar = getVar(pEdge);
                Multiplicity edgeMult = this.varToValues.get(edgeVar);
                assert edgeMult != null : String.format(
                    "Edge %s has no image.", pEdge);
                result.setMult(pEdge, edgeMult);
            }
            result.propagateMults();
            assert result.isWellDefined();
            return result;
        }
    }

}
