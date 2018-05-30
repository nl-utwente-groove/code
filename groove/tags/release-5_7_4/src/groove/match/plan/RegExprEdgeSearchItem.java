/* $Id$ */
package groove.match.plan;

import groove.automaton.RegAut;
import groove.automaton.RegExpr;
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostNode;
import groove.grammar.rule.LabelVar;
import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleLabel;
import groove.grammar.rule.RuleNode;
import groove.grammar.rule.Valuation;
import groove.grammar.type.TypeElement;
import groove.grammar.type.TypeGraph;
import groove.graph.EdgeComparator;
import groove.match.plan.PlanSearchStrategy.Search;

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
     * @param typeGraph label store used to determine subtypes for 
     * node type labels in the regular expression
     */
    public RegExprEdgeSearchItem(RuleEdge edge, TypeGraph typeGraph) {
        this.edge = edge;
        this.source = edge.source();
        this.target = edge.target();
        this.selfEdge = this.source == this.target;
        this.boundEdges = Collections.singleton(edge);
        this.boundNodes = new HashSet<>();
        this.boundNodes.add(edge.source());
        this.boundNodes.add(edge.target());
        RuleLabel label = edge.label();
        this.labelAutomaton = label.getAutomaton(typeGraph);
        this.edgeExpr = label.getMatchExpr();
        this.boundVars = label.getMatchExpr().boundVarSet();
        this.allVars = label.getMatchExpr().allVarSet();
        this.neededVars = new HashSet<>(this.allVars);
        this.neededVars.removeAll(this.boundVars);
    }

    @Override
    final public Record createRecord(Search search) {
        if (isSingular(search)) {
            return createSingularRecord(search);
        } else {
            return createMultipleRecord(search);
        }
    }

    @Override
    public int compareTo(SearchItem item) {
        int result = super.compareTo(item);
        if (result != 0) {
            return result;
        }
        RegExprEdgeSearchItem other = (RegExprEdgeSearchItem) item;
        return EdgeComparator.instance().compare(this.edge, other.edge);
    }

    @Override
    int computeHashCode() {
        return super.computeHashCode() + 31 * this.edge.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        RegExprEdgeSearchItem other = (RegExprEdgeSearchItem) obj;
        return this.edge.equals(other.edge);
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
    public Collection<? extends RuleEdge> bindsEdges() {
        return this.boundEdges;
    }

    /** This implementation returns the empty set. */
    @Override
    public Collection<? extends RuleNode> bindsNodes() {
        return this.boundNodes;
    }

    @Override
    public void activate(PlanSearchStrategy strategy) {
        this.sourceFound = strategy.isNodeFound(this.source);
        this.sourceIx = strategy.getNodeIx(this.source);
        if (this.selfEdge) {
            this.targetFound = this.sourceFound;
            this.targetIx = this.sourceIx;
        } else {
            this.targetFound = strategy.isNodeFound(this.target);
            this.targetIx = strategy.getNodeIx(this.target);
        }
        this.varIxMap = new HashMap<>();
        this.prematchedVars = new HashSet<>();
        for (LabelVar var : this.allVars) {
            assert strategy.isVarFound(var);
            this.prematchedVars.add(var);
            this.varIxMap.put(var, strategy.getVarIx(var));
        }
    }

    boolean isSingular(Search search) {
        boolean sourceSingular = this.sourceFound || search.getNodeSeed(this.sourceIx) != null;
        boolean targetSingular = this.targetFound || search.getNodeSeed(this.targetIx) != null;
        return sourceSingular && targetSingular;
    }

    SingularRecord createSingularRecord(Search search) {
        return new RegExprEdgeSingularRecord(search);
    }

    MultipleRecord<RegAut.Result> createMultipleRecord(Search search) {
        return new RegExprEdgeMultipleRecord(search, this.sourceIx, this.targetIx,
            this.sourceFound, this.targetFound);
    }

    @Override
    public String toString() {
        return String.format("Find %s--%s->%s", this.source, this.edgeExpr, this.target);
    }

    private final RuleEdge edge;
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
    /** The matched edge. */
    private final Set<RuleEdge> boundEdges;
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
    /** Mapping from variables to the corresponding indices in the result. */
    Map<LabelVar,Integer> varIxMap;

    private class RegExprEdgeSingularRecord extends SingularRecord {
        /** Constructs a new record, for a given matcher. */
        RegExprEdgeSingularRecord(Search search) {
            super(search);
            assert RegExprEdgeSearchItem.this.varIxMap.keySet().containsAll(needsVars());
        }

        @Override
        public void initialise(HostGraph host) {
            super.initialise(host);
            this.sourcePreMatch = this.search.getNodeSeed(RegExprEdgeSearchItem.this.sourceIx);
            this.targetPreMatch = this.search.getNodeSeed(RegExprEdgeSearchItem.this.targetIx);
        }

        @Override
        boolean find() {
            Valuation valuation = new Valuation();
            for (LabelVar var : RegExprEdgeSearchItem.this.prematchedVars) {
                TypeElement image =
                    this.search.getVar(RegExprEdgeSearchItem.this.varIxMap.get(var));
                assert image != null;
                valuation.put(var, image);
            }
            return !computeRelation(valuation).isEmpty();
        }

        @Override
        void erase() {
            // There is nothing to erase
        }

        @Override
        boolean write() {
            // There is nothing to write
            return true;
        }

        /**
         * Computes the image set by querying the automaton derived for the edge
         * label.
         */
        private Set<RegAut.Result> computeRelation(Valuation valuation) {
            HostNode sourceFind = this.sourcePreMatch;
            if (sourceFind == null && RegExprEdgeSearchItem.this.sourceFound) {
                sourceFind = this.search.getNode(RegExprEdgeSearchItem.this.sourceIx);
            }
            HostNode targetFind = this.targetPreMatch;
            if (targetFind == null && RegExprEdgeSearchItem.this.targetFound) {
                targetFind = this.search.getNode(RegExprEdgeSearchItem.this.targetIx);
            }
            return RegExprEdgeSearchItem.this.labelAutomaton.getMatches(this.host, sourceFind,
                targetFind, valuation);
        }

        @Override
        public String toString() {
            return RegExprEdgeSearchItem.this.toString() + ": " + this.state.isWritten();
        }

        /** Pre-matched source image, if any. */
        private HostNode sourcePreMatch;
        /** Pre-matched target image, if any. */
        private HostNode targetPreMatch;
    }

    private class RegExprEdgeMultipleRecord extends MultipleRecord<RegAut.Result> {
        /** Constructs a new record, for a given matcher. */
        RegExprEdgeMultipleRecord(Search search, int sourceIx, int targetIx, boolean sourceFound,
                boolean targetFound) {
            super(search);
            this.sourceIx = sourceIx;
            this.targetIx = targetIx;
            this.sourceFound = sourceFound;
            this.targetFound = targetFound;
            assert RegExprEdgeSearchItem.this.varIxMap.keySet().containsAll(
                RegExprEdgeSearchItem.this.neededVars);
        }

        @Override
        public void initialise(HostGraph host) {
            super.initialise(host);
            this.sourcePreMatch = this.search.getNodeSeed(this.sourceIx);
            this.targetPreMatch = this.search.getNodeSeed(this.targetIx);
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
            Valuation valuation = new Valuation();
            for (LabelVar var : RegExprEdgeSearchItem.this.prematchedVars) {
                TypeElement image =
                    this.search.getVar(RegExprEdgeSearchItem.this.varIxMap.get(var));
                assert image != null;
                valuation.put(var, image);
            }
            Set<RegAut.Result> matches =
                RegExprEdgeSearchItem.this.labelAutomaton.getMatches(this.host, this.sourceFind,
                    this.targetFind, valuation);
            this.imageIter = matches.iterator();
        }

        @Override
        boolean write(RegAut.Result image) {
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
            }
            return result;
        }

        @Override
        void erase() {
            if (this.sourceFind == null) {
                this.search.putNode(this.sourceIx, null);
            }
            if (this.targetFind == null) {
                this.search.putNode(this.targetIx, null);
            }
        }

        /** Rolls back the image set for the source. */
        private void rollBackTargetImage() {
            if (this.targetFind == null && !RegExprEdgeSearchItem.this.selfEdge) {
                this.search.putNode(this.targetIx, null);
            }
        }

        @Override
        public String toString() {
            return RegExprEdgeSearchItem.this.toString() + " = [" + this.sourceFind + ", "
                + this.targetFind + "]";
        }

        /** The index of the source in the search. */
        private final int sourceIx;
        /** The index of the target in the search. */
        private final int targetIx;
        /** Indicates if the source is found before this item is invoked. */
        final private boolean sourceFound;
        /** Indicates if the target is found before this item is invoked. */
        final private boolean targetFound;

        private HostNode sourcePreMatch;
        private HostNode targetPreMatch;
        /**
         * The pre-matched image for the edge source, if any. A value of
         * <code>null</code> means that no image is currently selected for the
         * source, or the source was pre-matched.
         */
        private HostNode sourceFind;
        /**
         * The pre-matched image for the edge target, if any. A value of
         * <code>null</code> means that no image is currently selected for the
         * target, or the target was pre-matched.
         */
        private HostNode targetFind;
    }
}
