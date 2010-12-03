/* $Id: RegExprEdgeSearchItem.java,v 1.15 2008-01-30 09:33:29 iovka Exp $ */
package groove.match;

import groove.graph.Edge;
import groove.graph.Label;
import groove.graph.LabelStore;
import groove.graph.Node;
import groove.match.SearchPlanStrategy.Search;
import groove.rel.Automaton;
import groove.rel.LabelVar;
import groove.rel.NodeRelation;
import groove.rel.RegExpr;
import groove.rel.RegExprLabel;
import groove.rel.RelationEdge;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A search item that searches an image for an edge.
 * @author Arend Rensink
 * @version $Revision $
 */
class RegExprEdgeSearchItem extends Edge2SearchItem {
    /**
     * Constructs a new search item. The item will match according to the
     * regular expression on the edge label.
     * @param labelStore label store used to determine subtypes for 
     * node type labels in the regular expression
     */
    public RegExprEdgeSearchItem(Edge edge, LabelStore labelStore) {
        super(edge);
        RegExprLabel label = (RegExprLabel) edge.label();
        this.labelAutomaton = label.getAutomaton(labelStore);
        this.edgeExpr = label.getRegExpr();
        this.boundVars = label.getRegExpr().boundVarSet();
        this.allVars = label.getRegExpr().allVarSet();
        this.neededVars = new HashSet<LabelVar>(this.allVars);
        this.neededVars.removeAll(this.boundVars);
    }

    /**
     * Returns the set of variables used but not bound in the regular
     * expression.
     */
    @Override
    public Collection<LabelVar> needsVars() {
        return this.neededVars;
    }

    /**
     * Returns the set of variables bound in the regular expression.
     */
    @Override
    public Collection<LabelVar> bindsVars() {
        return this.boundVars;
    }

    /** Returns the regular expression on the edge. */
    public RegExpr getEdgeExpr() {
        return this.edgeExpr;
    }

    /** This implementation returns the empty set. */
    @Override
    public Collection<? extends Edge> bindsEdges() {
        return Collections.emptySet();
    }

    @Override
    public void activate(SearchPlanStrategy strategy) {
        super.activate(strategy);
        this.allVarsFound = true;
        this.varIxMap = new HashMap<LabelVar,Integer>();
        for (LabelVar var : this.allVars) {
            this.allVarsFound = this.allVarsFound && strategy.isVarFound(var);
            this.varIxMap.put(var, strategy.getVarIx(var));
        }
    }

    /** This implementation returns <code>false</code>. */
    @Override
    boolean isPreMatched(Search search) {
        return false;
    }

    @Override
    boolean isSingular(Search search) {
        return super.isSingular(search) && this.allVarsFound;
    }

    @Override
    SingularRecord createSingularRecord(Search search) {
        return new RegExprEdgeSingularRecord(search);
    }

    @Override
    MultipleRecord<Edge> createMultipleRecord(Search search) {
        return new RegExprEdgeMultipleRecord(search, this.edgeIx,
            this.sourceIx, this.targetIx, this.sourceFound, this.targetFound);
    }

    /**
     * The automaton that computes the matches for the underlying edge.
     */
    final Automaton labelAutomaton;
    /** The regular expression on the edge. */
    final RegExpr edgeExpr;
    /** Collection of all variables occurring in the regular expression. */
    final Set<LabelVar> allVars;
    /** Collection of variables bound by the regular expression. */
    final Set<LabelVar> boundVars;
    /**
     * Collection of variables used in the regular expression but not bound by
     * it.
     */
    final Set<LabelVar> neededVars;
    /** Mapping from variables to the corresponding indices in the result. */
    Map<LabelVar,Integer> varIxMap;
    /**
     * Mapping indicating is all variables in the regular expression have been
     * found before the search item is invoked.
     */
    private boolean allVarsFound;

    class RegExprEdgeSingularRecord extends SingularRecord {
        /** Constructs a new record, for a given matcher. */
        RegExprEdgeSingularRecord(Search search) {
            super(search);
            this.sourcePreMatch =
                search.getNodeAnchor(RegExprEdgeSearchItem.this.sourceIx);
            this.targetPreMatch =
                search.getNodeAnchor(RegExprEdgeSearchItem.this.targetIx);
            assert RegExprEdgeSearchItem.this.varIxMap.keySet().containsAll(
                needsVars());
        }

        @Override
        boolean set() {
            Map<LabelVar,Label> valuation = new HashMap<LabelVar,Label>();
            for (LabelVar var : RegExprEdgeSearchItem.this.allVars) {
                Label image =
                    this.search.getVar(RegExprEdgeSearchItem.this.varIxMap.get(var));
                assert image != null;
                valuation.put(var, image);
            }
            return !computeRelation(valuation).isEmpty();
        }

        /**
         * Computes the image set by querying the automaton derived for the edge
         * label.
         */
        private NodeRelation computeRelation(Map<LabelVar,Label> valuation) {
            NodeRelation result;
            Node sourceFind = this.sourcePreMatch;
            if (sourceFind == null && RegExprEdgeSearchItem.this.sourceFound) {
                sourceFind =
                    this.search.getNode(RegExprEdgeSearchItem.this.sourceIx);
            }
            Set<Node> imageSourceSet = Collections.singleton(sourceFind);
            Node targetFind = this.targetPreMatch;
            if (targetFind == null && RegExprEdgeSearchItem.this.targetFound) {
                targetFind =
                    this.search.getNode(RegExprEdgeSearchItem.this.targetIx);
            }
            Set<Node> imageTargetSet = Collections.singleton(targetFind);
            result =
                RegExprEdgeSearchItem.this.labelAutomaton.getMatches(this.host,
                    imageSourceSet, imageTargetSet);
            return result;
        }

        /** Pre-matched source image, if any. */
        private final Node sourcePreMatch;
        /** Pre-matched target image, if any. */
        private final Node targetPreMatch;
    }

    class RegExprEdgeMultipleRecord extends Edge2MultipleRecord {
        /** Constructs a new record, for a given matcher. */
        RegExprEdgeMultipleRecord(Search search, int edgeIx, int sourceIx,
                int targetIx, boolean sourceFound, boolean targetFound) {
            super(search, edgeIx, sourceIx, targetIx, sourceFound, targetFound);
            assert RegExprEdgeSearchItem.this.varIxMap.keySet().containsAll(
                RegExprEdgeSearchItem.this.neededVars);
            this.freshVars = new HashSet<LabelVar>();
            for (LabelVar var : RegExprEdgeSearchItem.this.boundVars) {
                if (search.getVar(RegExprEdgeSearchItem.this.varIxMap.get(var)) == null) {
                    this.freshVars.add(var);
                }
            }
        }

        /**
         * Computes the image set by querying the automaton derived for the edge
         * label.
         */
        @Override
        void initImages() {
            Set<Node> imageSourceSet =
                this.sourceFind == null ? null
                        : Collections.singleton(this.sourceFind);
            Set<Node> imageTargetSet =
                this.targetFind == null ? null
                        : Collections.singleton(this.targetFind);
            NodeRelation matches;
            matches =
                RegExprEdgeSearchItem.this.labelAutomaton.getMatches(this.host,
                    imageSourceSet, imageTargetSet);
            initImages(matches.getAllRelated(), false, false, false, false);
        }

        @Override
        boolean setImage(Edge image) {
            boolean result = super.setImage(image);
            if (result && !this.freshVars.isEmpty()) {
                Map<LabelVar,Label> valuation =
                    ((RelationEdge) image).getValue();
                for (LabelVar var : this.freshVars) {
                    this.search.putVar(
                        RegExprEdgeSearchItem.this.varIxMap.get(var),
                        valuation.get(var));
                }
            }
            return result;
        }

        @Override
        public void reset() {
            super.reset();
            for (LabelVar var : this.freshVars) {
                this.search.putVar(
                    RegExprEdgeSearchItem.this.varIxMap.get(var), null);
            }
        }

        /** The set of bound variables that are not yet pre-matched. */
        private final Set<LabelVar> freshVars;
    }
}
