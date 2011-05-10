package groove.gui;

import groove.io.store.SystemStore;
import groove.lts.GTS;
import groove.lts.GTSAdapter;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.RuleEvent;
import groove.view.GraphView;
import groove.view.RuleView;
import groove.view.StoredGrammarView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.undo.UndoManager;

/**
 * Collection of values that make up the state of 
 * the {@link Simulator}.
 * GUI state changes (such as selections) should be made through
 * a transaction on this object.
 */
public class SimulatorModel implements Cloneable {
    /** Returns the active GTS, if any. */
    public final GTS getGts() {
        return this.gts;
    }

    /** 
     * Sets the active GTS and fires an update event when this results in a change.
     * If the new GTS is different from the old, this has the side effects of
     * <li> setting the state to the start state of the new GTS,
     * or to {@code null} if the new GTS is {@code null}
     * <li> setting the transition to {@code null}
     * <li> setting the event to {@code null}
     * @param gts the new GTS; may be {@code null}
     * @return if {@code true}, the GTS was really changed
     * @see #setState(GraphState)
     */
    public final boolean setGts(GTS gts) {
        start();
        doSetGts(gts, true);
        return finish();
    }

    /** 
     * Changes the active GTS. Optionally propagates the changes
     * to other model fields.
     * @param propagate if {@code true}, the change is propagated
     * and a notification event is fired
     * @see #setGts(GTS)
     */
    private final void doSetGts(GTS gts, boolean propagate) {
        if (this.gts != gts || this.ltsListener.isChanged()) {
            if (propagate && this.gts != gts) {
                doSetState(gts == null ? null : gts.startState(), true);
            }
            if (this.gts != null) {
                this.gts.removeLTSListener(this.ltsListener);
            }
            this.gts = gts;
            if (this.gts != null) {
                this.gts.addLTSListener(this.ltsListener);
            }
            this.ltsListener.clear();
            this.changes.add(Change.GTS);
        }
    }

    /** Returns the currently selected state, if any. */
    public final GraphState getState() {
        return this.state;
    }

    /** 
     * Sets the selected state and fires an update event if this results in a change.
     * If the new state is different from the old, the transition
     * and event are set to {@code null}.
     * @return if {@code true}, the state was really changed
     * @see #setTransition(GraphTransition)
     * @see #setEvent(RuleEvent)
     */
    public final boolean setState(GraphState state) {
        start();
        doSetState(state, true);
        return finish();
    }

    /** 
     * Does the work for {@link #setState(GraphState)}, except
     * for firing the update.
     * @param propagate if {@code false}, do not propagate the
     * change to the transition and event.
     */
    private final void doSetState(GraphState state, boolean propagate) {
        if (state != this.state) {
            this.state = state;
            this.changes.add(Change.STATE);
            if (propagate) {
                doSetTransition(null, false);
                doSetEvent(null);
            }
        }
    }

    /** Returns the currently selected transition, if any. */
    public final GraphTransition getTransition() {
        return this.transition;
    }

    /** 
     * Changes the selected transition.
     * If the new transition is different from the old, and not
     * {@code null}, also changes the state and the event 
     * to the source state and the event of the new transition.
     * @return if {@code true}, the transition was really changed
     * @see #setState(GraphState)
     * @see #setEvent(RuleEvent)
     */
    public final boolean setTransition(GraphTransition trans) {
        start();
        doSetTransition(trans, true);
        return finish();
    }

    /** 
     * Changes the selected transition.
     * If the new transition is different from the old, and not
     * {@code null}, also changes the state and the event 
     * to the source state and the event of the new transition.
     */
    private final void doSetTransition(GraphTransition trans, boolean propagate) {
        if (trans != this.transition) {
            this.transition = trans;
            this.changes.add(Change.TRANS);
            if (trans != null && propagate) {
                doSetState(trans.source(), false);
                doSetEvent(trans.getEvent());
            }
        }
    }

    /** Returns the currently selected event. */
    public final RuleEvent getEvent() {
        return this.event;
    }

    /** 
     * Changes the selected event, and fires an update event.
     * @return if {@code true}, the event was really changed
     */
    public final boolean setEvent(RuleEvent event) {
        start();
        doSetEvent(event);
        return finish();
    }

    /** 
     * Changes the selected event.
     */
    private final void doSetEvent(RuleEvent event) {
        if (event != this.event) {
            this.event = event;
            this.changes.add(Change.EVENT);
        }
    }

    /**
     * Returns the currently loaded graph grammar, if any.
     */
    public final StoredGrammarView getGrammar() {
        return this.grammar;
    }

    /** Convenience method to return the store of the currently loaded 
     * grammar view, if any.
     */
    public final SystemStore getStore() {
        return this.grammar == null ? null : this.grammar.getStore();
    }

    /** Updates the state according to a given grammar. */
    public final void setGrammar(StoredGrammarView grammar) {
        start();
        doSetGrammar(grammar, true);
        finish();
    }

    /** Updates the state according to a given grammar. */
    private final void doSetGrammar(StoredGrammarView grammar, boolean propagate) {
        boolean changed = (grammar != this.grammar);
        // transfer the undo manager to the new grammar
        if (changed && this.undoManager != null) {
            this.undoManager.discardAllEdits();
            if (this.grammar != null) {
                this.grammar.getStore().removeUndoableEditListener(
                    this.undoManager);
            }
            if (grammar != null) {
                grammar.getStore().addUndoableEditListener(this.undoManager);
            }
        }
        // if the grammar view is a different object,
        // do not attempt to keep the host graph and rule selections
        this.grammar = grammar;
        this.changes.add(Change.GRAMMAR);
        if (propagate) {
            doSetGts(null, false);
            if (changed) {
                doSetHostSet(Collections.<GraphView>emptySet());
                doSetRuleSet(Collections.<RuleView>emptySet());
            } else {
                // restrict the selected host graphs to those that are (still)
                // in the grammar
                Collection<GraphView> newHostSet =
                    new LinkedHashSet<GraphView>();
                for (String hostName : grammar.getGraphNames()) {
                    newHostSet.add(grammar.getGraphView(hostName));
                }
                newHostSet.retainAll(this.hostSet);
                doSetHostSet(newHostSet);
                // restrict the selected rules to those that are (still)
                // in the grammar
                Collection<RuleView> newRuleSet = new LinkedHashSet<RuleView>();
                for (String ruleName : grammar.getRuleNames()) {
                    newRuleSet.add(grammar.getRuleView(ruleName));
                }
                newRuleSet.retainAll(this.ruleSet);
                doSetRuleSet(newRuleSet);
            }
        }
    }

    /** Returns the currently selected host graph. */
    public final GraphView getHost() {
        return this.host;
    }

    /** Changes the currently selected host.
     * @return if {@code true}, the host was actually changed.
     */
    public final boolean setHost(GraphView host) {
        start();
        doSetHost(host);
        return finish();
    }

    /** Changes the currently selected host.
     */
    private final void doSetHost(GraphView host) {
        boolean result = (host != this.host);
        if (result) {
            this.host = host;
            this.changes.add(Change.HOST);
        }
    }

    /** 
     * Returns the currently selected host graph list.
     */
    public final Collection<GraphView> getHostSet() {
        return this.hostSet;
    }

    /** 
     * Changes the currently selected host graph list.
     * May also change the selected host graph.
     * @return if {@code true}, actually made the change.
     * @see #setHost(GraphView)
     */
    public final boolean setHostSet(Collection<GraphView> hostSet) {
        start();
        doSetHostSet(hostSet);
        return finish();
    }

    /** 
     * Changes the currently selected host graph list.
     * May also change the selected host graph.
     * @see #setHost(GraphView)
     */
    private final void doSetHostSet(Collection<GraphView> hostSet) {
        if (!hostSet.equals(this.hostSet)) {
            this.hostSet = hostSet;
            this.changes.add(Change.HOST_SET);
            if (!hostSet.contains(getHost())) {
                if (hostSet.isEmpty()) {
                    doSetHost(null);
                } else {
                    doSetHost(hostSet.iterator().next());
                }
            }
        }
    }

    /** Returns the currently selected rule. */
    public final RuleView getRule() {
        return this.rule;
    }

    /** Changes the currently selected rule, based on the rule name.
     * @return if {@code true}, the rule was actually changed.
     */
    public final boolean setRule(String ruleName) {
        if (ruleName == null) {
            return setRule((RuleView) null);
        } else {
            RuleView rule = getGrammar().getRuleView(ruleName);
            assert rule != null;
            return setRule(rule);
        }
    }

    /** Changes the currently selected rule.
     * @return if {@code true}, the rule was actually changed.
     */
    public final boolean setRule(RuleView rule) {
        start();
        doSetRule(rule);
        return finish();
    }

    /** Changes the currently selected rule.
     */
    private final void doSetRule(RuleView rule) {
        if (rule != this.rule) {
            this.rule = rule;
            this.changes.add(Change.RULE);
        }
    }

    /** 
     * Returns the currently selected rule set.
     */
    public final Collection<RuleView> getRuleSet() {
        return this.ruleSet;
    }

    /** 
     * Changes the currently selected rule set.
     * If the set has size 0 or 1, also changes the selected rule.
     * @return if {@code true}, actually made the change.
     * @see #setRule(RuleView)
     */
    public final boolean setRuleSet(Collection<RuleView> ruleSet) {
        start();
        doSetRuleSet(ruleSet);
        return finish();
    }

    /** 
     * Changes the currently selected rule set.
     * If the set has size 0 or 1, also changes the selected rule.
     * @see #setRule(RuleView)
     */
    private final void doSetRuleSet(Collection<RuleView> ruleSet) {
        if (!ruleSet.equals(this.ruleSet)) {
            this.ruleSet = ruleSet;
            this.changes.add(Change.RULE_SET);
            if (!ruleSet.contains(getRule())) {
                if (ruleSet.isEmpty()) {
                    doSetRule(null);
                } else {
                    doSetRule(ruleSet.iterator().next());
                }
            }
        }
    }

    @Override
    public String toString() {
        return "GuiState [gts=" + this.gts + ", state=" + this.state
            + ", transition=" + this.transition + ", event=" + this.event
            + ", grammar=" + this.grammar + ", host=" + this.host
            + ", hostSet=" + this.hostSet + ", rule=" + this.rule
            + ", ruleSet=" + this.ruleSet + ", changes=" + this.changes + "]";
    }

    //
    //    private void testActive() {
    //        if (this.owner == null) {
    //            throw new IllegalStateException("No transition is active");
    //        }
    //    }

    /** Adds a given simulation listener to the list. */
    public void addListener(SimulatorListener listener) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }

    /** Removes a given listener from the list. */
    public void removeListener(SimulatorListener listener) {
        this.listeners.remove(listener);
    }

    /** Sets an undo manager to listen to the changes in the grammar store. */
    public void setUndoManager(UndoManager undoManager) {
        this.undoManager = undoManager;
    }

    /** 
     * Starts a transaction.
     * This is only allowed if no transaction is currently underway.
     */
    private void start() {
        assert this.old == null;
        this.old = clone();
        this.changes = new HashSet<SimulatorModel.Change>();
    }

    /** 
     * Ends a transaction and notifies all listeners.
     * This is only allowed if there is a transaction underway,
     * by the same owner.
     */
    private boolean finish() {
        assert this.old != null;
        boolean result = !this.changes.isEmpty();
        if (result) {
            fireUpdate();
        }
        this.old = null;
        this.changes = null;
        return result;
    }

    /** 
     * Aborts a transaction and rolls back without notification.
     * This is only allowed if there is a transaction underway,
     * by the same owner.
     */
    private void abort() {
        assert this.old != null;
        // reset all values
        this.event = this.old.event;
        this.grammar = this.old.grammar;
        this.gts = this.old.gts;
        this.host = this.old.host;
        this.hostSet = this.old.hostSet;
        this.rule = this.old.rule;
        this.ruleSet = this.old.ruleSet;
        this.state = this.old.state;
        this.transition = this.old.transition;
        this.old = null;
        this.changes = null;
    }

    @Override
    protected SimulatorModel clone() {
        SimulatorModel result = null;
        try {
            result = (SimulatorModel) super.clone();
            result.hostSet = new ArrayList<GraphView>(this.hostSet);
            result.ruleSet = new ArrayList<RuleView>(this.ruleSet);
        } catch (CloneNotSupportedException e) {
            assert false;
        }
        return result;
    }

    /** 
     * Notifies all listeners of the changes involved in the current
     * transaction.
     */
    private void fireUpdate() {
        for (SimulatorListener listener : this.listeners) {
            listener.update(this, this.old, this.changes);
        }
    }

    /** Frozen GUI state, if there is a transaction underway. */
    private SimulatorModel old;
    /** Set of changes made in the current transaction. */
    private Set<SimulatorModel.Change> changes;
    /** Currently active GTS. */
    private GTS gts;
    /** Currently selected state. */
    private GraphState state;
    /** Currently selected transition. */
    private GraphTransition transition;
    /** Currently selected rule event. */
    private RuleEvent event;
    /** Currently loaded grammar. */
    private StoredGrammarView grammar;
    /** Currently selected host graph view. */
    private GraphView host;
    /** Multiple selection of host graph views. */
    private Collection<GraphView> hostSet = Collections.emptyList();
    /** Currently selected rule view. */
    private RuleView rule;
    /** Multiple selection of rule views. */
    private Collection<RuleView> ruleSet = Collections.emptyList();

    /** Array of listeners. */
    private final List<SimulatorListener> listeners =
        new ArrayList<SimulatorListener>();

    /** Undo manager listening to the grammar store. */
    private UndoManager undoManager;

    /** Change type. */
    public enum Change {
        /** 
         * The GTS has changed.
         * @see SimulatorModel#getGts()
         */
        GTS,
        /** 
         * The selected state has changed.
         * @see SimulatorModel#getState()
         */
        STATE,
        /** 
         * The selected transition has changed.
         * @see SimulatorModel#getTransition()
         */
        TRANS,
        /** 
         * The selected event has changed.
         * @see SimulatorModel#getEvent()
         */
        EVENT,
        /** 
         * The loaded grammar has changed.
         * @see SimulatorModel#getGrammar()
         */
        GRAMMAR,
        /** 
         * The selected host graph has changed.
         * @see SimulatorModel#getHost()
         */
        HOST,
        /** 
         * The selected set of host graphs has changed.
         * @see SimulatorModel#getHostSet()
         */
        HOST_SET,
        /** 
         * The selected rule has changed.
         * @see SimulatorModel#getRule()
         */
        RULE,
        /** 
         * The selected rule set has changed.
         * @see SimulatorModel#getRuleSet()
         */
        RULE_SET;
    }

    private final MyLTSListener ltsListener = new MyLTSListener();

    /**
     * LTS listener to observe changes to the GTS.
     */
    private class MyLTSListener extends GTSAdapter {
        /** Empty constructor with the correct visibility. */
        MyLTSListener() {
            // empty
        }

        /** Clears the changed flag. */
        public void clear() {
            this.changed = false;
        }

        /**
         * May only be called with the current lts as first parameter. Updates
         * the frame title by showing the number of nodes and edges.
         */
        @Override
        public void addUpdate(GTS gts, GraphState state) {
            this.changed = true;
        }

        @Override
        public void addUpdate(GTS gts, GraphTransition transition) {
            this.changed = true;
        }

        @Override
        public void closeUpdate(GTS graph, GraphState explored) {
            this.changed = true;
        }

        /** Indicates that a change has been registered. */
        public boolean isChanged() {
            return this.changed;
        }

        private boolean changed;
    }

}