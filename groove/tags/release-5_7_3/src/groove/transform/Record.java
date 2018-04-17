/* $Id$ */
package groove.transform;

import static groove.transform.RuleEvent.Reuse.EVENT;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;

import groove.algebra.AlgebraFamily;
import groove.grammar.Grammar;
import groove.grammar.Rule;
import groove.grammar.RuleDependencies;
import groove.grammar.host.HostFactory;
import groove.grammar.rule.RuleToHostMap;
import groove.lts.RuleTransitionLabel;
import groove.transform.RuleEvent.Reuse;
import groove.util.collect.Pool;

/**
 * Usage instance of a given graph grammar. Stores information gathered during
 * rule application. An instance has an associated transition system, for which it
 * creates fresh node identities (to ensure deterministic and consecutive node
 * numbers) and maintains a map of rule events (to save space and time).
 * @author Arend Rensink
 * @version $Revision $
 */
public class Record {
    /**
     * The total number of events (over all rules) created in
     * {@link #getEvent(Proof)}.
     */
    private static int eventCount;

    /**
     * Returns the number of events created in the course of rule application.
     */
    static public int getEventCount() {
        return eventCount;
    }

    /**
     * Constructs a derivation record from a given (fixed) graph grammar. The
     * initial (fresh) node number is set to one higher than the highest node
     * number occurring in the start graph.
     * @throws IllegalStateException if the grammar is not fixed according to
     *         {@link Grammar#testFixed(boolean)}.
     */
    public Record(Grammar grammar, HostFactory hostFactory) throws IllegalStateException {
        this.grammar = grammar;
        this.hostFactory = hostFactory;
        grammar.testFixed(true);
        this.checkIso = grammar.getProperties()
            .isCheckIsomorphism();
        this.family = grammar.getProperties()
            .getAlgebraFamily();
    }

    /** Returns the stored rule system on which the derivations are based. */
    public Grammar getGrammar() {
        return this.grammar;
    }

    /**
     * The associated graph grammar.
     */
    private final Grammar grammar;

    /** Returns the host factory associated with the GTS. */
    public HostFactory getFactory() {
        return this.hostFactory;
    }

    /**
     * The associated host factory.
     */
    private final HostFactory hostFactory;

    /** Returns the algebra family associated with the GTS. */
    public AlgebraFamily getFamily() {
        return this.family;
    }

    private final AlgebraFamily family;

    /**
     * Returns an event for a given rule match. If {@link #getReuse()} is
     * set, events are stored internally and reused.
     */
    public RuleEvent getEvent(Proof proof) {
        return proof.newEvent(this);
    }

    /**
     * Returns a "normal" event representing a given event. If
     * {@link #getReuse()} is set, events are stored internally and reused.
     */
    public RuleEvent normaliseEvent(RuleEvent event) {
        RuleEvent result;
        if (getReuse() == EVENT && event instanceof AbstractRuleEvent<?,?>) {
            result = this.eventMap.put((AbstractRuleEvent<?,?>) event);
            if (result == null) {
                // the event is new.
                result = event;
                eventCount++;
            }
        } else {
            result = event;
        }
        return result;
    }

    /**
     * Factory method for a composite event. If {@link #getReuse()} is set,
     * the event is normalised w.r.t. a global store.
     * @param rule the rule of the composite event
     * @param eventSet the set of sub-events for the composite event
     */
    public RuleEvent createCompositeEvent(Rule rule, Collection<BasicEvent> eventSet) {
        return normaliseEvent(new CompositeEvent(this, rule, eventSet, getReuse()));
    }

    /**
     * Factory method for a simple event. If {@link #getReuse()} is set,
     * the event is normalised w.r.t. a global store.
     * @param rule the rule of the composite event
     * @param elementMap the element map for the simple event
     */
    public BasicEvent createSimpleEvent(Rule rule, RuleToHostMap elementMap) {
        return (BasicEvent) normaliseEvent(new BasicEvent(rule, elementMap, getReuse()));
    }

    /**
     * Normalises a given transition label.
     */
    public @NonNull RuleTransitionLabel normaliseLabel(@NonNull RuleTransitionLabel prototype) {
        return this.labelPool.canonical(prototype);
    }

    /**
     * Returns the set of rules that may be enabled by a given rule, according
     * to the currently calculated dependencies.
     * @param enabler the (potential) enabler rule
     * @return the set of rules that may be enabled by <code>enabler</code>
     */
    public Set<Rule> getEnabledRules(Rule enabler) {
        return getDependencies().getEnableds(enabler);
    }

    /**
     * Returns the set of rules that may be disabled by a given rule, according
     * to the currently calculated dependencies.
     * @param disabler the (potential) disabler rule
     * @return the set of rules that may be disabled by <code>disabler</code>
     */
    public Set<Rule> getDisabledRules(Rule disabler) {
        Set<Rule> result = getDependencies().getDisableds(disabler);
        assert result != null : String.format("Null rule dependencies for %s",
            disabler.getQualName());
        return result;
    }

    /**
     * Initialises the rule dependencies.
     */
    protected RuleDependencies getDependencies() {
        if (this.dependencies == null) {
            this.dependencies = new RuleDependencies(getGrammar());
        }
        return this.dependencies;
    }

    /**
     * Rule dependencies of the rule system.
     */
    private RuleDependencies dependencies;
    /**
     * Identity map for events that have been encountered during exploration.
     * Events are stored only if {@link #getReuse()} is set.
     */
    private final RuleEventSet eventMap = new RuleEventSet();
    /** Identity map of normal transition labels. */
    private final Pool<RuleTransitionLabel> labelPool = new Pool<>();

    /**
     * Sets the policy of the GTS in determining state equivalence. This is only
     * relevant if {@link #isCollapse()} is set to <code>true</code>.
     * @param check if <code>true</code>, states with isomorphic graph
     *        structure are considered equivalent; otherwise, only equal graphs
     *        (with the same set of nodes and edges) are considered equivalent.
     */
    public void setCheckIso(boolean check) {
        this.checkIso = check;
    }

    /**
     * Returns the current value of the isomorphism checking policy.
     * @see #setCheckIso(boolean)
     */
    public boolean isCheckIso() {
        return this.checkIso;
    }

    /**
     * Flag indicating if states with isomorphic graph structure are to be
     * considered equivalent. If <code>true</code>, new states are compared
     * with old ones modulo isomorphism; otherwise, they are compared modulo
     * equality of node and edge sets. Default value is <code>true</code>.
     */
    private boolean checkIso = true;

    /**
     * Sets the policy of the GTS in collapsing equivalent states. Which states
     * are equivalent is partially determined by #isCheckIso. Not collapsing
     * states only makes sense in linear exploration strategies.
     * @param collapse if <code>true</code>, equivalent states are collapsed;
     *        otherwise, new states are always added to the GTS, without
     *        comparing them to existing states.
     */
    public void setCollapse(boolean collapse) {
        this.collapseStates = collapse;
    }

    /**
     * Returns the current value of the state collapsing policy.
     * @see #setCollapse(boolean)
     */
    public boolean isCollapse() {
        return this.collapseStates;
    }

    /**
     * Flag indicating if equivalent states are to be collapsed in the GTS. If
     * <code>false</code>, new states are not compared with old ones, and are
     * added to the state set straight away. Default value is <code>true</code>.
     */
    private boolean collapseStates = true;

    /**
     * Changes the behaviour of the GTS in reusing previous rule events.
     * Unpredictable behaviour will ensue if this method is called while an
     * existing GTS is being explored. Initially the property is set to
     * <code>true</code>
     * @param reuse if <code>true</code>, events are stored and reused
     */
    public void setReuseEvents(Reuse reuse) {
        if (this.eventMap.isEmpty()) {
            // only change the reuse policy if there are not yet any events
            // generated, otherwise their equals test will break down
            this.reuse = reuse;
        }
    }

    /**
     * Returns the current value of the reuse property.
     * @return if <code>true</code>, previously found results are reused
     */
    public Reuse getReuse() {
        return this.reuse;
    }

    /**
     * Flag indicating if events are to be reused, meaning that there is a
     * global store {@link #eventMap} of "normal" event representatives. Default
     * value: <code>true</code>.
     */
    private Reuse reuse = EVENT;

    /**
     * Changes the state of the copyGraphs property.
     * @see #isCopyGraphs()
     * @see #isRandomAccess()
     */
    public void setCopyGraphs(boolean copy) {
        this.copyGraphs = copy;
    }

    /**
     * Indicates if new graphs are obtained by copying the content of their
     * parents.
     * @return <code>true</code> if new graphs are to be obtained by copying;
     *         <code>false</code> if the parent's data structure is
     *         "borrowed". The latter runs the risk of
     *         {@link ConcurrentModificationException}s if iterators over the
     *         parent's data structures are still alive.
     */
    public boolean isCopyGraphs() {
        return this.copyGraphs || this.randomAccess;
    }

    /**
     * Flag indicating if new graphs are obtained by copying the content of
     * their parents; if <code>false</code>, the parent's data structure is
     * "borrowed". The latter runs the risk of
     * {@link ConcurrentModificationException}s if iterators over the parent's
     * data structures are still alive.
     */
    // EZ says: Switched to false to see if the performance is improved...
    private boolean copyGraphs = false;

    /**
     * Changes the random access property.
     * @see #isRandomAccess()
     */
    public void setRandomAccess(boolean access) {
        this.randomAccess = access;
    }

    /**
     * Indicates that the GTS may be accessed randomly, as in the
     * Simulator. This implies that graphs should be copied, not shared.
     * @return <code>true</code> if the data structures should allow for
     * random access; <code>false</code> if access is determined entirely
     * by the exploration strategy.
     */
    public boolean isRandomAccess() {
        return this.randomAccess;
    }

    /**
     * Flag indicating that the generated states may be randomly accessed,
     * as in the simulator. This means the copyGraphs should never be disabled.
     */
    private boolean randomAccess = false;
}
