/* $Id: RegExprEdgeSearchItem.java,v 1.15 2008-01-30 09:33:29 iovka Exp $ */
package groove.match;

import groove.graph.LabelStore;
import groove.graph.TypeLabel;
import groove.match.SearchPlanStrategy.Search;
import groove.rel.RegAut;
import groove.rel.LabelVar;
import groove.rel.RegExpr;
import groove.trans.HostNode;
import groove.trans.RuleEdge;
import groove.trans.RuleLabel;
import groove.trans.RuleNode;

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
class RegExprEdgeSearchItem extends AbstractSearchItem {
    /**
     * Constructs a new search item. The item will match according to the
     * regular expression on the edge label.
     * @param labelStore label store used to determine subtypes for 
     * node type labels in the regular expression
     */
    public RegExprEdgeSearchItem(RuleEdge edge, LabelStore labelStore) {
        this.source = edge.source();
        this.target = edge.target();
        this.selfEdge = this.source == this.target;
        this.boundNodes = new HashSet<RuleNode>();
        this.boundNodes.add(edge.source());
        this.boundNodes.add(edge.target());
        RuleLabel label = edge.label();
        assert label.getRegExpr() != null;
        this.labelAutomaton = label.getAutomaton(labelStore);
        this.edgeExpr = label.getRegExpr();
        this.boundVars = label.getRegExpr().boundVarSet();
        this.allVars = label.getRegExpr().allVarSet();
        this.neededVars = new HashSet<LabelVar>(this.allVars);
        this.neededVars.removeAll(this.boundVars);
    }

    final public Record getRecord(Search search) {
        if (isSingular(search)) {
            return createSingularRecord(search);
        } else {
            return createMultipleRecord(search);
        }
    }

    /**
     * The larger the automaton, the lower the rating.
     */
    @Override
    int getRating() {
        return -this.labelAutomaton.size();
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
    public Collection<RuleNode> bindsNodes() {
        return this.boundNodes;
    }

    @Override
    public void activate(SearchPlanStrategy strategy) {
        this.sourceFound = strategy.isNodeFound(this.source);
        this.sourceIx = strategy.getNodeIx(this.source);
        if (this.selfEdge) {
            this.targetFound = this.sourceFound;
            this.targetIx = this.sourceIx;
        } else {
            this.targetFound = strategy.isNodeFound(this.target);
            this.targetIx = strategy.getNodeIx(this.target);
        }
        this.varIxMap = new HashMap<LabelVar,Integer>();
        this.freshVars = new HashSet<LabelVar>();
        this.prematchedVars = new HashSet<LabelVar>();
        for (LabelVar var : this.allVars) {
            if (strategy.isVarFound(var)) {
                this.prematchedVars.add(var);
            } else {
                this.freshVars.add(var);
            }
            this.varIxMap.put(var, strategy.getVarIx(var));
        }
    }

    boolean isSingular(Search search) {
        boolean sourceSingular =
            this.sourceFound || search.getNodeAnchor(this.sourceIx) != null;
        boolean targetSingular =
            this.targetFound || search.getNodeAnchor(this.targetIx) != null;
        return sourceSingular && targetSingular && this.freshVars.isEmpty();
    }

    SingularRecord createSingularRecord(Search search) {
        return new RegExprEdgeSingularRecord(search);
    }

    MultipleRecord<RegAut.Result> createMultipleRecord(Search search) {
        return new RegExprEdgeMultipleRecord(search, this.sourceIx,
            this.targetIx, this.sourceFound, this.targetFound);
    }

    @Override
    public String toString() {
        return String.format("Find %s--%s->%s", this.source, this.edgeExpr,
            this.target);
    }

    /**
     * The source end of the regular edge, separately stored for efficiency.
     */
    final RuleNode source;
    /**
     * The target end of the regular edge, separately stored for efficiency.
     */
    final RuleNode target;
    /**
     * Flag indicating that the regular edge is a self-edge.
     */
    final boolean selfEdge;
    /** The set of end nodes of this edge. */
    private final Set<RuleNode> boundNodes;

    /** The index of the source in the search. */
    int sourceIx;
    /** The index of the target in the search. */
    int targetIx;
    /** Indicates if the source is found before this item is invoked. */
    boolean sourceFound;
    /** Indicates if the target is found before this item is invoked. */
    boolean targetFound;

    /**
     * The automaton that computes the matches for the underlying edge.
     */
    final RegAut labelAutomaton;
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
    /** The set of pre-matched variables. */
    Set<LabelVar> prematchedVars;
    /** The set of bound variables that are not yet pre-matched. */
    Set<LabelVar> freshVars;
    /** Mapping from variables to the corresponding indices in the result. */
    Map<LabelVar,Integer> varIxMap;

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
            Map<LabelVar,TypeLabel> valuation =
                new HashMap<LabelVar,TypeLabel>();
            for (LabelVar var : RegExprEdgeSearchItem.this.prematchedVars) {
                TypeLabel image =
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
        private Set<RegAut.Result> computeRelation(
                Map<LabelVar,TypeLabel> valuation) {
            HostNode sourceFind = this.sourcePreMatch;
            if (sourceFind == null && RegExprEdgeSearchItem.this.sourceFound) {
                sourceFind =
                    this.search.getNode(RegExprEdgeSearchItem.this.sourceIx);
            }
            Set<HostNode> imageSourceSet = Collections.singleton(sourceFind);
            HostNode targetFind = this.targetPreMatch;
            if (targetFind == null && RegExprEdgeSearchItem.this.targetFound) {
                targetFind =
                    this.search.getNode(RegExprEdgeSearchItem.this.targetIx);
            }
            Set<HostNode> imageTargetSet = Collections.singleton(targetFind);
            return RegExprEdgeSearchItem.this.labelAutomaton.getMatches(
                this.host, imageSourceSet, imageTargetSet, valuation);
        }

        /** Pre-matched source image, if any. */
        private final HostNode sourcePreMatch;
        /** Pre-matched target image, if any. */
        private final HostNode targetPreMatch;
    }

    class RegExprEdgeMultipleRecord extends MultipleRecord<RegAut.Result> {
        /** Constructs a new record, for a given matcher. */
        RegExprEdgeMultipleRecord(Search search, int sourceIx, int targetIx,
                boolean sourceFound, boolean targetFound) {
            super(search);
            this.sourceIx = sourceIx;
            this.targetIx = targetIx;
            this.sourceFound = sourceFound;
            this.targetFound = targetFound;
            this.sourcePreMatch = search.getNodeAnchor(sourceIx);
            this.targetPreMatch = search.getNodeAnchor(targetIx);
            assert RegExprEdgeSearchItem.this.varIxMap.keySet().containsAll(
                RegExprEdgeSearchItem.this.neededVars);
            this.valuation = new HashMap<LabelVar,TypeLabel>();
            for (LabelVar var : RegExprEdgeSearchItem.this.prematchedVars) {
                TypeLabel image =
                    this.search.getVar(RegExprEdgeSearchItem.this.varIxMap.get(var));
                assert image != null;
                this.valuation.put(var, image);
            }
        }

        /**
         * Computes the image set by querying the automaton derived for the edge
         * label.
         */
        @Override
        void init() {
            this.sourceFind = this.sourcePreMatch;
            if (this.sourceFind == null && this.sourceFound) {
                this.sourceFind = this.search.getNode(this.sourceIx);
                assert this.sourceFind != null : String.format("Source node not found");
            }
            this.targetFind = this.targetPreMatch;
            if (this.targetFind == null && this.targetFound) {
                this.targetFind = this.search.getNode(this.targetIx);
                assert this.targetFind != null : String.format("Target node not found");
            }
            Set<HostNode> imageSourceSet =
                this.sourceFind == null ? null
                        : Collections.singleton(this.sourceFind);
            Set<HostNode> imageTargetSet =
                this.targetFind == null ? null
                        : Collections.singleton(this.targetFind);
            Set<RegAut.Result> matches =
                RegExprEdgeSearchItem.this.labelAutomaton.getMatches(this.host,
                    imageSourceSet, imageTargetSet, this.valuation);
            this.imageIter = matches.iterator();
        }

        @Override
        boolean setImage(RegAut.Result image) {
            boolean result = true;
            HostNode source = image.one();
            if (this.sourceFind == null) {
                // maybe the prospective source image was used as
                // target image of this same edge in the previous attempt
                rollBackTargetImage();
                if (!this.search.putNode(this.sourceIx, source)) {
                    result = false;
                }
            }
            if (result) {
                HostNode target = image.two();
                if (RegExprEdgeSearchItem.this.selfEdge) {
                    if (target != source) {
                        return false;
                    }
                } else {
                    if (this.targetFind == null) {
                        if (!this.search.putNode(this.targetIx, target)) {
                            return false;
                        }
                    }
                }
                this.selected = image;
                if (result && !RegExprEdgeSearchItem.this.freshVars.isEmpty()) {
                    Map<LabelVar,TypeLabel> valuation = image.getValuation();
                    for (LabelVar var : RegExprEdgeSearchItem.this.freshVars) {
                        this.search.putVar(
                            RegExprEdgeSearchItem.this.varIxMap.get(var),
                            valuation.get(var));
                    }
                }
            }
            return result;
        }

        /** Rolls back the image set for the source. */
        private void rollBackTargetImage() {
            if (this.targetFind == null && !RegExprEdgeSearchItem.this.selfEdge) {
                this.search.putNode(this.targetIx, null);
            }
        }

        @Override
        public void reset() {
            super.reset();
            for (LabelVar var : RegExprEdgeSearchItem.this.freshVars) {
                this.search.putVar(
                    RegExprEdgeSearchItem.this.varIxMap.get(var), null);
            }
        }

        /** The index of the source in the search. */
        final int sourceIx;
        /** The index of the target in the search. */
        final int targetIx;
        /** Indicates if the source is found before this item is invoked. */
        final private boolean sourceFound;
        /** Indicates if the target is found before this item is invoked. */
        final private boolean targetFound;

        private final HostNode sourcePreMatch;
        private final HostNode targetPreMatch;
        /**
         * The pre-matched image for the edge source, if any. A value of
         * <code>null</code> means that no image is currently selected for the
         * source, or the source was pre-matched.
         */
        HostNode sourceFind;
        /**
         * The pre-matched image for the edge target, if any. A value of
         * <code>null</code> means that no image is currently selected for the
         * target, or the target was pre-matched.
         */
        HostNode targetFind;
        /** Image found by the latest call to {@link #find()}, if any. */
        RegAut.Result selected;
        private final Map<LabelVar,TypeLabel> valuation;
    }
}
