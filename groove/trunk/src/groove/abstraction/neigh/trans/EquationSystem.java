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
import groove.abstraction.neigh.shape.ShapeMorphism;
import groove.abstraction.neigh.shape.ShapeNode;
import groove.graph.TypeLabel;
import groove.trans.RuleEdge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An equation system is the mechanism used to resolve non-determinism during
 * the materialisation phase.
 * The system is composed of multiplicity variables, that are associated with
 * edges or edge signatures of the shape under materialisation.
 * The variables are used to form equations over multiplicities values. The
 * process of solving the equation system entails finding all possible values
 * for the multiplicity variables that satisfy all equations.
 * When a solution is found the materialisation object is adjusted accordingly.
 * 
 * @author Eduardo Zambon
 */
public final class EquationSystem {

    /**
     * The initial materialisation object for which the equation system should
     * be built. If the system has only one solution this object is modified
     * directly. If there are multiple solutions, this object is cloned.
     */
    private final Materialisation mat;

    /**
     * List of all variables of the system. The object is final but the list
     * is modified during the creation of the system. After that it no longer
     * changes. 
     */
    private final List<MultVar> vars;

    /** The number of multiplicity variables of the system. */
    private int varCount;

    /**
     * Set of all equations that compose the system. The object is final but 
     * the set is modified during the creation of the system. After that it
     * no longer changes. 
     */
    private final THashSet<Equation> eqs;

    /**
     * Maps from possible new edges that can be in the materialised shape to
     * their corresponding multiplicity variables.  The object are final but 
     * are modified during the creation of the system. After that they no
     * longer change. 
     */
    private final Map<ShapeEdge,EdgeMultVar> outEdgeMultVarMap;
    private final Map<ShapeEdge,EdgeMultVar> inEdgeMultVarMap;

    /** 
     * Constructs the equation system based on the given materialisation.
     * After the constructor finishes, a call to solve() yields all possible
     * resulting materialisations, with the proper shapes.
     * 
     * Passing a materialisation that has an empty set of possible new edges
     * gives an assertion error.
     */
    public EquationSystem(Materialisation mat) {
        assert !mat.getPossibleNewEdgeSet().isEmpty();
        this.mat = mat;
        this.vars = new ArrayList<MultVar>();
        this.varCount = 0;
        this.eqs = new THashSet<Equation>();
        this.outEdgeMultVarMap = new THashMap<ShapeEdge,EdgeMultVar>();
        this.inEdgeMultVarMap = new THashMap<ShapeEdge,EdgeMultVar>();
        this.create();
    }

    @Override
    public String toString() {
        return "Equalities: " + this.eqs;
    }

    /**
     * Creates the equation system.
     * See the comments in the code for more details.
     */
    private void create() {
        Shape shape = this.mat.getShape();
        // BEGIN - Create opposition relations and edge bundles.
        Set<EdgeBundle> bundles = new THashSet<EdgeBundle>();
        // For all possible new edges.
        for (ShapeEdge edge : this.mat.getPossibleNewEdgeSet()) {
            // Create one outgoing and one incoming multiplicity variable.
            EdgeMultVar outMultVar = this.newEdgeMultVar(edge, OUTGOING);
            EdgeMultVar inMultVar = this.newEdgeMultVar(edge, INCOMING);
            // The variables are opposite of one another.
            outMultVar.opposite = inMultVar;
            inMultVar.opposite = outMultVar;
            // Edge bundles.
            TypeLabel label = edge.label();
            EdgeBundle outBundle =
                this.getEdgeBundle(bundles, edge.source(), label, OUTGOING);
            outBundle.addVar(outMultVar);
            EdgeBundle inBundle =
                this.getEdgeBundle(bundles, edge.target(), label, INCOMING);
            inBundle.addVar(inMultVar);
        } // END - Create opposition relations and edge bundles.

        // For each edge bundle, create an equality.
        // BEGIN - Create equalities.
        for (EdgeBundle bundle : bundles) {
            EdgeMultDir direction = bundle.direction;

            // Counter of bundle edges that were matched by the rule.
            Multiplicity matchedEdgesMult =
                Multiplicity.getMultiplicity(0, 0, MultKind.EDGE_MULT);
            boolean hasUnmatchedEdges = false;
            // For all edges in the bundle.
            // BEGIN - Bundle edges loop.
            Set<ShapeEdge> bundleEdges = this.getEdgesForBundle(bundle);
            for (ShapeEdge edge : bundleEdges) {
                if (!this.hasVariable(edge, direction)) {
                    // There is no variable for the edge. This means that the
                    // edge is already in the shape.
                    // Check if the shape edge is an image of a rule edge.
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

            // Get the edge signature from the original shape.
            // Take any edge from the bundle.
            ShapeEdge bundleEdge = bundleEdges.iterator().next();
            // Find the original edge from the shaping morphism.
            ShapeEdge origEdge =
                this.mat.getShapeMorphism().getEdge(bundleEdge);
            EdgeSignature origEs = shape.getEdgeSignature(origEdge, direction);
            // Multiplicity of the edge signature.
            Multiplicity origEsMult = shape.getEdgeSigMult(origEs, direction);

            // Check if we need to create an extra variable for the signature.
            if (hasUnmatchedEdges) {
                // There are some edges of the shape in the bundle that were
                // not matched by rule edges. Create another multiplicity
                // variable to represent the value of the remainder
                // multiplicity for the edge signature.
                MultVar esVar = this.newEdgeSigMultVar(origEs, direction);
                bundle.vars.add(esVar);
            }

            // Create the equation with constant = esMult - matchedEdgesMult .
            Equation eq =
                this.newEquation(bundle.vars.size(),
                    origEsMult.sub(matchedEdgesMult),
                    this.mat.getShape().getNodeMult(bundle.node).isCollector());
            // Add all variables in the bundle to the equation.
            for (MultVar var : bundle.vars) {
                eq.addVar(var);
            }
        } // END - Create equalities.
    }

    /**
     * Creates and returns a new edge multiplicity variable for the given
     * edge and direction.
     * The map from edges to variables is properly adjusted along with the
     * variable set and count. 
     */
    private EdgeMultVar newEdgeMultVar(ShapeEdge edge, EdgeMultDir direction) {
        EdgeMultVar result = new EdgeMultVar(this.varCount, direction, edge);
        this.getEdgeMultVarMap(direction).put(edge, result);
        this.vars.add(result);
        this.varCount++;
        return result;
    }

    /**
     * Creates and returns a new edge signature multiplicity variable for
     * the given signature and direction.
     * The variable set and count is properly adjusted.
     */
    private EdgeSigMultVar newEdgeSigMultVar(EdgeSignature es,
            EdgeMultDir direction) {
        EdgeSigMultVar result =
            new EdgeSigMultVar(this.varCount, direction, es);
        this.vars.add(result);
        this.varCount++;
        return result;
    }

    /**
     * Creates and returns a new equation,
     * while storing it in the equation set.
     */
    private Equation newEquation(int varsCount, Multiplicity constant,
            boolean isCollector) {
        Equation result = new Equation(varsCount, constant, isCollector);
        this.eqs.add(result);
        return result;
    }

    /**
     * Searches in the given bundle set for a bundle with corresponding node,
     * label and direction. If a proper bundle is not found, a new one is
     * created and added to the set.  
     */
    private EdgeBundle getEdgeBundle(Set<EdgeBundle> bundles, ShapeNode node,
            TypeLabel label, EdgeMultDir direction) {
        EdgeBundle result = null;
        for (EdgeBundle bundle : bundles) {
            if (bundle.node.equals(node) && bundle.label.equals(label)
                && bundle.direction == direction) {
                result = bundle;
                break;
            }
        }
        if (result == null) {
            result = new EdgeBundle(node, label, direction);
            bundles.add(result);
        }
        return result;
    }

    /**
     * Returns the set of edges that are associated with given bundle. This
     * includes the possible new edges of the materialisation that are 
     * associated with bundle variables and also all edges that are already in
     * the shape and have the same edge signature that is associated with the
     * bundle.
     */
    private Set<ShapeEdge> getEdgesForBundle(EdgeBundle bundle) {
        Set<ShapeEdge> result = new THashSet<ShapeEdge>();
        // Include the possible edges.
        for (ShapeEdge possibleEdge : this.mat.getPossibleNewEdgeSet()) {
            MultVar var = this.getEdgeVariable(possibleEdge, bundle.direction);
            if (bundle.vars.contains(var)) {
                result.add(possibleEdge);
            }
        }
        for (ShapeEdge edgeS : this.mat.getShape().binaryEdgeSet(bundle.node,
            bundle.direction)) {
            if (edgeS.label().equals(bundle.label)) {
                // EDUARDO: Fix this. This is wrong. If a node is in more than
                // one bundle, all edges are mixed...
                result.add(edgeS);
            }
        }
        return result;
    }

    /**
     * Returns true is the given edge has a variable associated in the given
     * direction. 
     */
    private boolean hasVariable(ShapeEdge edge, EdgeMultDir direction) {
        return this.getEdgeVariable(edge, direction) != null;
    }

    /**
     * Returns the (possible null) variable associated with the given edge
     * and direction.
     */
    private EdgeMultVar getEdgeVariable(ShapeEdge edge, EdgeMultDir direction) {
        return this.getEdgeMultVarMap(direction).get(edge);
    }

    /** Returns the proper (non-null) map accordingly to the given direction. */
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

    /**
     * Finds all solutions of this equation system and return all
     * materialisation objects created from the valid solutions.
     * This method resolves all non-determinism of the materialisation phase. 
     */
    public Set<Materialisation> solve() {
        // Compute all solutions.
        Set<Solution> solutions = new THashSet<Solution>();
        Solution initialSol =
            new Solution(this.varCount, (THashSet<Equation>) this.eqs.clone());
        Set<Solution> partialSols = new THashSet<Solution>();
        partialSols.add(initialSol);
        while (!partialSols.isEmpty()) {
            Solution sol = partialSols.iterator().next();
            partialSols.remove(sol);
            for (Solution newSol : this.iterateSolution(sol)) {
                if (newSol.isComplete()) {
                    solutions.add(newSol);
                } else {
                    partialSols.add(newSol);
                }
            }
        }
        // Create the return objects.
        Set<Materialisation> result = new THashSet<Materialisation>();
        if (solutions.size() == 1) {
            // Only one solution, no need to clone the materialisation object.
            this.updateMatFromSolution(this.mat, solutions.iterator().next());
            result.add(this.mat);
        } else {
            // OK, we need to clone.
            for (Solution sol : solutions) {
                Materialisation mat = this.mat.clone();
                this.updateMatFromSolution(mat, sol);
                result.add(mat);
            }
        }
        return result;
    }

    /**
     * Iterate the given solution over all equations of the system and try to
     * fix more variables. This may lead to branching. In this case, an
     * heuristic is used to decide on which equation to use that is likely
     * to produce less branching. Returns the set of new (partial) solutions.
     */
    @SuppressWarnings("unchecked")
    private Set<Solution> iterateSolution(Solution sol) {
        // For all equations, try to fix as many variables as possible.
        Set<Equation> currEqsToUse = (Set<Equation>) sol.eqsToUse.clone();
        int currEqsToUseSize = currEqsToUse.size();
        int prevEqsToUseSize = 0;
        while (currEqsToUseSize != prevEqsToUseSize && currEqsToUseSize > 0) {
            for (Equation eq : currEqsToUse) {
                eq.computeNewValues(sol);
            }
            prevEqsToUseSize = currEqsToUseSize;
            currEqsToUse = (Set<Equation>) sol.eqsToUse.clone();
            currEqsToUseSize = currEqsToUse.size();
        }

        Set<Solution> result;
        Equation branchingEq = sol.getBestBranchingEquation();
        if (branchingEq == null) {
            // We don't need to branch in the solution search.
            result = new THashSet<Solution>();
            result.add(sol);
        } else {
            result = branchingEq.getNewSolutions(sol);
        }
        return result;
    }

    /**
     * Adjust the given materialisation object using the given solution.
     * After this the materialisation is complete. 
     */
    private void updateMatFromSolution(Materialisation mat, Solution sol) {
        assert mat != null;
        assert sol != null;
        assert sol.isComplete();
        Shape shape = mat.getShape();
        ShapeMorphism morph = mat.getShapeMorphism();
        // Each variable is associated with either an edge or a signature.
        // Iterate over the variables and modify the shape accordingly.
        for (MultVar var : this.vars) {
            if (sol.impreciseVars.contains(var)) {
                // We'll deal with the imprecise variables in the end.
                continue;
            }
            if (var instanceof EdgeMultVar) {
                // We have a variable associated with a possible edge.
                EdgeMultVar edgeMultVar = (EdgeMultVar) var;
                Multiplicity value = sol.getValue(edgeMultVar);
                ShapeEdge edgeS = edgeMultVar.edge;
                if (!value.isZero()) {
                    // We have to include the edge in the shape.
                    // But first, check if it's not included, because of an
                    // opposite variable.
                    if (!shape.edgeSet().contains(edgeS)) {
                        shape.addEdgeWithoutCheck(edgeS);
                    }
                    // Set the multiplicity accordingly.
                    shape.setEdgeMult(edgeS, edgeMultVar.direction, value);
                } else {
                    // The possible edge is not inserted in the shape.
                    // Remove it from the shape morphism.
                    morph.removeEdge(edgeS);
                }
            } else if (var instanceof EdgeSigMultVar) {
                // We have a variable associated with the remainder of an edge
                // signature multiplicity;
                EdgeSigMultVar edgeSigMultVar = (EdgeSigMultVar) var;
                Multiplicity value = sol.getValue(edgeSigMultVar);
                // Setting the edge signature multiplicity.
                shape.setEdgeSigMult(edgeSigMultVar.es,
                    edgeSigMultVar.direction, value);
            }
        }
        // If we have imprecise variables we may have to pull nodes.
        for (MultVar var : sol.impreciseVars) {
            if (var instanceof EdgeMultVar) {
                // We have an imprecise variable associated with a possible edge.
                shape.pullNode(mat, ((EdgeMultVar) var).edge, var.direction);
            }
        }
        assert mat.isShapeMorphConsistent();
        assert shape.isInvariantOK();
    }

    /**
     * Abstract class to represent all types of multiplicity variables. 
     */
    private static abstract class MultVar {

        /** The unique variable number. */
        final int number;
        /** The direction this variable is associated with. */
        final EdgeMultDir direction;

        /** Basic constructor. */
        MultVar(int number, EdgeMultDir direction) {
            this.number = number;
            this.direction = direction;
        }

        @Override
        public abstract String toString();
    }

    /**
     * Multiplicity variable associated with a possible edge.
     * All such variables have an opposite one, representing the other end
     * of the edge. These two opposite variables have an existential relation,
     * i.e., if a variable is zero then its opposite must also be zero, and if
     * a variable is positive its opposite must also be positive (but not
     * necessarily must have the same value).
     */
    private static class EdgeMultVar extends MultVar {

        /** The edge associated with this variable. */
        final ShapeEdge edge;
        /** The opposite variable. */
        EdgeMultVar opposite;

        /** Basic constructor. */
        EdgeMultVar(int number, EdgeMultDir direction, ShapeEdge edge) {
            super(number, direction);
            this.edge = edge;
        }

        /** Variables of this type a prefixed with an 'x'. */
        @Override
        public String toString() {
            return "x" + this.number;
        }
    }

    /**
     * Multiplicity variable associated with an edge signature, representing
     * the remaining value of the signature multiplicity after the
     * materialisation is finished.
     */
    private static class EdgeSigMultVar extends MultVar {

        /** The edge signature associated with this variable. */
        final EdgeSignature es;

        /** Basic constructor. */
        EdgeSigMultVar(int number, EdgeMultDir direction, EdgeSignature es) {
            super(number, direction);
            this.es = es;
        }

        /** Variables of this type a prefixed with an 'y'. */
        @Override
        public String toString() {
            return "y" + this.number;
        }
    }

    /**
     * Class representing equations of the form: x0 + x1 + ... xn = const.
     * The variables can be of either type and there is no ordering of variables
     * numbers. The constant is an edge multiplicity. 
     */
    private static class Equation {

        /** The set of variables composing this equation. */
        final Set<MultVar> vars;
        /** The multiplicity constant. */
        final Multiplicity constant;
        /** Flag to indicate if the node from the edge bundle is a collector. */
        final boolean isCollector;

        /** Basic constructor. */
        Equation(int varsCount, Multiplicity constant, boolean isCollector) {
            this.vars = new THashSet<MultVar>(varsCount);
            this.constant = constant;
            this.isCollector = isCollector;
        }

        /** Basic setter method. */
        void addVar(MultVar var) {
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
            if (this.isCollector) {
                result.append("C");
            }
            return result.toString();
        }

        /**
         * Given a partial solution, tries to assign additional values to the
         * variables when possible. (See comments on code for additional
         * information). If no new values can be obtained from this equation
         * the given solution remains unchanged. 
         */
        void computeNewValues(Solution partialSol) {
            // EZ says: sorry for the multiple return points, but otherwise
            // the code becomes less readable...

            if (!partialSol.eqsToUse.contains(this)) {
                // This equation can't contribute in finding new values for
                // the solution. Nothing to do.
                return;
            }

            int openVarsCount = this.openVarsCount(partialSol);
            if (openVarsCount == 0) {
                // All variables in this equation have a value assigned.
                // Nothing to do.
                partialSol.eqsToUse.remove(this);
                return;
            }

            if (this.constant.isZero()) {
                // The equation constant is zero.
                // Since multiplicities can't be negative, this means that all
                // variables in the equation have to be zero.
                for (MultVar var : this.getOpenVars(partialSol, openVarsCount)) {
                    partialSol.setValue(var, this.constant);
                }
                // This equation is no longer useful for finding the remaining
                // values of this solution.
                partialSol.eqsToUse.remove(this);
                return;
            }

            if (openVarsCount == 1) {
                // We have only one variable without a value so we assign the
                // equation constant minus the sum of all other variables values
                // to the open variable.
                Multiplicity newConst = this.constant;
                for (Multiplicity mult : this.getFixedValues(partialSol,
                    openVarsCount)) {
                    newConst = newConst.sub(mult);
                }
                for (MultVar var : this.getOpenVars(partialSol, openVarsCount)) {
                    partialSol.setValue(var, newConst);
                }
                // This equation is no longer useful for finding the remaining
                // values of this solution.
                partialSol.eqsToUse.remove(this);
                return;
            }

            MultVar impreciseVars[] = this.getImpreciseVars(partialSol);
            if (impreciseVars.length == 1) {
                if (this.constant.isOne() && !this.isCollector) {
                    // In this case we know that the value for the imprecise
                    // variable must be one and that all other open variables must
                    // be zero.
                    Multiplicity zero =
                        Multiplicity.getMultiplicity(0, 0, MultKind.EDGE_MULT);
                    for (MultVar var : this.getOpenVars(partialSol,
                        openVarsCount)) {
                        if (var.equals(impreciseVars[0])) {
                            partialSol.setValue(var, this.constant);
                        } else {
                            partialSol.setValue(var, zero);
                        }
                    }
                    // This equation is no longer useful for finding the remaining
                    // values of this solution.
                    partialSol.eqsToUse.remove(this);
                    return;
                }

                // If we reached this point it means that the constant is
                // a positive collector multiplicity.
                assert this.constant.isCollector() || this.isCollector;
                // At this point we can't determine anything about the open
                // variables except that they are positive. This means that
                // all open variables become imprecise variables.
                // Since all variables need a value for the solution to become
                // complete, we set these imprecise variables to the equation
                // constant. This value is not used anywhere, a proper value
                // is produced when we pull out nodes at the last stage of
                // materialisation.
                // Also, we don't want this equation laying around because it
                // will just cause unnecessary branching.
                for (MultVar var : this.getOpenVars(partialSol, openVarsCount)) {
                    partialSol.setValue(var, this.constant, false);
                    partialSol.impreciseVars.add(var);
                }
                partialSol.eqsToUse.remove(this);
            }
        }

        /**
         * Counts the number of variables of this equation that open under
         * the given partial solution.
         */
        int openVarsCount(Solution partialSol) {
            int openVars = this.vars.size();
            for (MultVar var : this.vars) {
                if (partialSol.hasValue(var)) {
                    openVars--;
                }
            }
            return openVars;
        }

        /**
         * Returns true if the number of open variables for the given solution
         * is zero; otherwise returns false. 
         */
        boolean isFullyAssigned(Solution sol) {
            return this.openVarsCount(sol) == 0;
        }

        /**
         * Returns a newly created array of open variables for the given
         * partial solution. The second parameter is used to avoid counting
         * the number of variables again, when the number is already known.
         */
        MultVar[] getOpenVars(Solution partialSol, int openVarsCount) {
            assert openVarsCount > 0;
            MultVar result[] = new MultVar[openVarsCount];
            int i = 0;
            for (MultVar var : this.vars) {
                if (!partialSol.hasValue(var)) {
                    result[i] = var;
                    i++;
                }
            }
            assert i == openVarsCount;
            return result;
        }

        /**
         * Returns a newly created array of open variables for the given
         * partial solution.
         */
        MultVar[] getOpenVars(Solution partialSol) {
            return this.getOpenVars(partialSol, this.openVarsCount(partialSol));
        }

        MultVar[] getImpreciseVars(Solution partialSol) {
            ArrayList<MultVar> result =
                new ArrayList<MultVar>(partialSol.impreciseVars.size());
            for (MultVar var : partialSol.impreciseVars) {
                if (this.vars.contains(var)) {
                    result.add(var);
                }
            }
            return result.toArray(new MultVar[result.size()]);
        }

        Multiplicity[] getFixedValues(Solution partialSol, int openVarsCount) {
            Multiplicity result[] =
                new Multiplicity[this.vars.size() - openVarsCount];
            int i = 0;
            for (MultVar var : this.vars) {
                if (partialSol.hasValue(var)) {
                    result[i] = partialSol.getValue(var);
                    i++;
                }
            }
            return result;
        }

        /**
         * Branches the search for valid solutions by assigning all possible
         * valid values to the open variables of this equation.
         * The blow-up on the materialisation phase is now due to this method.
         */
        Set<Solution> getNewSolutions(Solution partialSol) {
            assert !partialSol.isComplete();
            assert partialSol.eqsToUse.contains(this);

            Set<Solution> result = new THashSet<Solution>();

            // Array of possible multiplicity values.
            Multiplicity mults[] = Multiplicity.getAllEdgeMultiplicities();
            int multsSize = mults.length;
            // Array of variables that need values.
            MultVar vars[] = this.getOpenVars(partialSol);
            int varsSize = vars.length;
            // Array of current values for the variables.
            // Hold indexes of positions in the mults array.
            int currVals[] = new int[varsSize];

            // Start iterating.
            int curr = varsSize - 1;
            int prev;
            while (currVals[0] != multsSize) {
                // Test and store valid solution.
                Solution newSol = partialSol.clone();
                // Store the current values in the new solution.
                for (int i = 0; i < vars.length; i++) {
                    newSol.setValue(vars[i], mults[currVals[i]], false);
                }
                if (this.isValidSolution(newSol)) {
                    /*newSol.purgeImpreciseVars();*/
                    result.add(newSol);
                }

                // Update pointers and compute next solution.
                currVals[curr]++;
                if (currVals[curr] == multsSize) {
                    prev = curr - 1;
                    while (prev >= 0 && currVals[prev]++ == multsSize) {
                        prev--;
                    }
                    if (prev >= 0) {
                        // Zero all indices between prev + 1 and curr .
                        for (int i = prev + 1; i <= curr; i++) {
                            currVals[i] = 0;
                        }
                    }
                }
            }

            // This equation is no longer useful for finding the remaining
            // values of this solution.
            partialSol.eqsToUse.remove(this);

            return result;
        }

        /**
         * Calculates the bounded sum of the values provided by the given
         * solution for the equation values and compares the sum with the
         * equation constant.
         * @return true if the sum is equal to the constant, false otherwise. 
         */
        boolean isValidSolution(Solution sol) {
            Multiplicity sum =
                Multiplicity.getMultiplicity(0, 0, MultKind.EDGE_MULT);
            for (MultVar var : this.vars) {
                Multiplicity mult = sol.getValue(var);
                if (mult.isZero() && sol.impreciseVars.contains(var)) {
                    return false;
                }
                sum = sum.add(mult);
            }
            return this.constant.equals(sum);
        }
    }

    /**
     * Class that represent a collection of edges that are bundled by a single
     * edge multiplicity. This comprises the new possible edges of the
     * materialisation and the edge signatures already in the shape. We can't
     * use a simple edge signature here because nodes on the opposite direction
     * of the edges are already in distinct equivalence classes. 
     */
    private static class EdgeBundle {

        /**
         * The node associated with this bundle. It can be either a source or
         * target node, depending on the direction.
         */
        final ShapeNode node;
        /** The label for all edges of the bundle. */
        final TypeLabel label;
        /** Defines the direction of the edges. */
        final EdgeMultDir direction;
        /** The variables for the possible edges that are part of the bundle. */
        final Set<MultVar> vars;

        /** Basic constructor. */
        EdgeBundle(ShapeNode node, TypeLabel label, EdgeMultDir direction) {
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

        /** Basic setter method. */
        void addVar(MultVar var) {
            assert var.direction == this.direction;
            this.vars.add(var);
        }
    }

    /**
     * Class representing a possible solution for the equation system.
     * This is the only dynamic part of the system, the remaining structures
     * are all fixed when trying to solve the system. Objects of this class
     * are cloned during the solution search, hence we try to keep the objects
     * as small as possible. 
     */
    private static class Solution {

        /**
         * The array of values corresponding to an assignment to the variables
         * in the system. The array is indexed by the variable number, thus,
         * for example, the value for variable 'x3' is in value[3]. If a
         * position in the array is null then the corresponding variable is
         * still open in the solution.
         */
        final Multiplicity values[];
        /** Number of non-null values of this solution. */
        int valuesCount;
        /**
         * Set of equations that can still provide new values for variables.
         * When an equation has no open variables it can no longer contribute
         * to the solution search and therefore the equation is removed from
         * this set.
         */
        final THashSet<Equation> eqsToUse;

        final THashSet<MultVar> impreciseVars;

        /** Basic constructor. */
        Solution(int varCount, THashSet<Equation> eqsToUse) {
            this.values = new Multiplicity[varCount];
            this.valuesCount = 0;
            this.eqsToUse = eqsToUse;
            this.impreciseVars = new THashSet<MultVar>();
        }

        /** Returns true if all values are non-null. */
        boolean isComplete() {
            return this.valuesCount == this.values.length;
        }

        /** Returns true if the given variable has a value in this solution. */
        boolean hasValue(MultVar var) {
            return this.values[var.number] != null;
        }

        /**
         * Returns the non-null value associated with the given variable in
         * this solution. Fails in an assertion if the variable has no value.
         */
        Multiplicity getValue(MultVar var) {
            assert this.hasValue(var);
            return this.values[var.number];
        }

        /**
         * Sets the value of the given variable to the given multiplicity.
         * Fails in an assertion if the variable already has a value.
         * This method may have two side-effects:
         * 1) If the variable being assigned a value is of type EdgeMultVar
         * then its opposite value also receives a value.
         * 2) Equations that become fully assigned are removed from the set
         * of equations to be used by this solution.
         */
        void setValue(MultVar var, Multiplicity mult, boolean removeImprecise) {
            assert !this.hasValue(var);
            assert mult != null;
            this.values[var.number] = mult;
            this.valuesCount++;
            if (removeImprecise) {
                this.impreciseVars.remove(var);
            }
            // Check if the variable has an opposite one. And if yes, also
            // set its value.
            if (var instanceof EdgeMultVar) {
                MultVar opposite = ((EdgeMultVar) var).opposite;
                if (mult.isZero()) {
                    // We are setting this variable to zero so its opposite
                    // must also be zero.
                    assert !this.hasValue(opposite);
                    assert !this.impreciseVars.contains(opposite);
                    this.values[opposite.number] = mult;
                    this.valuesCount++;
                } else {
                    // The multiplicity is positive, we know that the opposite
                    // must be positive as well, but we don't know its precise
                    // value for now.
                    if (!this.hasValue(opposite)) {
                        this.impreciseVars.add(opposite);
                    }
                }
            }
            // Check if any equation has become fully assigned.
            Iterator<Equation> iter = this.eqsToUse.iterator();
            while (iter.hasNext()) {
                Equation eq = iter.next();
                if (eq.isFullyAssigned(this)) {
                    // Remove the equation.
                    iter.remove();
                }
            }
        }

        /*void purgeImpreciseVars() {
            Iterator<MultVar> iter = this.impreciseVars.iterator();
            while (iter.hasNext()) {
                MultVar var = iter.next();
                if (this.hasValue(var)) {
                    iter.remove();
                }
            }
        }*/

        void setValue(MultVar var, Multiplicity mult) {
            this.setValue(var, mult, true);
        }

        @Override
        public String toString() {
            return Arrays.toString(this.values);
        }

        @Override
        public Solution clone() {
            Solution result =
                new Solution(this.values.length,
                    (THashSet<Equation>) this.eqsToUse.clone());
            for (int i = 0; i < this.values.length; i++) {
                result.values[i] = this.values[i];
            }
            result.valuesCount = this.valuesCount;
            return result;
        }

        /**
         * Returns an equation from the set of equations to be used that is
         * likely to cause the least branching during the solution search. The
         * method uses the following heuristic:
         * - Equations with a concrete constant are preferred.
         * - Try to pick the equation with the smaller number of open variables.
         */
        Equation getBestBranchingEquation() {
            Equation result = null;
            for (Equation eq : this.eqsToUse) {
                if (!eq.isCollector
                    && (result == null
                        || (result.constant.isCollector() && !eq.constant.isCollector()) || (eq.openVarsCount(this) < result.openVarsCount(this)))) {
                    result = eq;
                }
            }
            return result;
        }
    }
}
