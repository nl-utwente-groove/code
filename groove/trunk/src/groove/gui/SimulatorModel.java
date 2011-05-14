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
     * Refreshes the GTS by firing an update event if any changes occurred 
     * since the GTS was last set or refreshed.
     */
    public final boolean refreshGts() {
        start();
        doRefreshGts();
        return finish();
    }

    /**
     * Refreshes the GTS.
     */
    private final boolean doRefreshGts() {
        boolean result = this.ltsListener.isChanged();
        if (result) {
            this.ltsListener.clear();
            this.changes.add(Change.GTS);
        }
        return result;
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
        if (doSetGts(gts)) {
            doSetState(gts == null ? null : gts.startState());
            doSetTransition(null);
            doSetEvent(null);
        } else if (this.ltsListener.isChanged()) {
            this.ltsListener.clear();
            this.changes.add(Change.GTS);
        }
        return finish();
    }

    /** 
     * Changes the active GTS.
     * @see #setGts(GTS)
     */
    private final boolean doSetGts(GTS gts) {
        boolean result = this.gts != gts;
        if (result) {
            if (this.gts != null) {
                this.gts.removeLTSListener(this.ltsListener);
            }
            if (gts != null) {
                gts.addLTSListener(this.ltsListener);
            }
            this.ltsListener.clear();
            this.gts = gts;
            this.changes.add(Change.GTS);
        }
        return result;
    }

    /** 
     * Returns the currently active state, if any.
     * The the GTS is set, there is always an active transition.
     */
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
        doRefreshGts();
        if (doSetState(state)) {
            doSetTransition(null);
            doSetEvent(null);
        }
        return finish();
    }

    /** 
     * Does the work for {@link #setState(GraphState)}, except
     * for firing the update.
     */
    private final boolean doSetState(GraphState state) {
        // never reset the active state as long as there is a GTS
        boolean result = state != this.state;
        if (result) {
            this.state = state;
            this.changes.add(Change.STATE);
        }
        return result;
    }

    /** Returns the currently active transition, if any. */
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
        if (doSetTransition(trans) && trans != null) {
            doSetState(trans.source());
            doSetEvent(trans.getEvent());
            doSetRule(this.grammar.getRuleView(trans.getEvent().getRule().getName()));
        }
        return finish();
    }

    /** 
     * Changes the selected transition.
     * If the new transition is different from the old, and not
     * {@code null}, also changes the state and the event 
     * to the source state and the event of the new transition.
     */
    private final boolean doSetTransition(GraphTransition trans) {
        boolean result = trans != this.transition;
        if (result) {
            this.transition = trans;
            this.changes.add(Change.TRANS);
        }
        return result;
    }

    /** Returns the currently selected event. */
    public final RuleEvent getEvent() {
        return this.event;
    }

    /** 
     * Changes the selected rule event, and fires an update event.
     * If the event is changed to a non-null event, also sets the rule.
     * @return if {@code true}, the event was really changed
     * @see #setRule(RuleView)
     */
    public final boolean setEvent(RuleEvent event) {
        start();
        if (doSetEvent(event) && event != null) {
            doSetRule(this.grammar.getRuleView(event.getRule().getName()));
        }
        doSetTransition(null);
        return finish();
    }

    /** 
     * Changes the selected event and, if the event is propagated,
     * possibly the rule.
     */
    private final boolean doSetEvent(RuleEvent event) {
        boolean result = event != this.event;
        if (result) {
            this.event = event;
            this.changes.add(Change.EVENT);
        }
        return result;
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

    /** 
     * Checks for changes in the currently loaded grammar view, 
     * and calls an update event if required.
     * Should be called after any change in the grammar view or
     * underlying store.
     */
    public final void refreshGrammar(boolean reset) {
        start();
        this.changes.add(Change.GRAMMAR);
        // restrict the selected host graphs to those that are (still)
        // in the grammar
        Collection<GraphView> newHostSet = new LinkedHashSet<GraphView>();
        for (String hostName : this.grammar.getGraphNames()) {
            newHostSet.add(this.grammar.getGraphView(hostName));
        }
        newHostSet.retainAll(this.hostSet);
        doSetHostSet(newHostSet);
        if (this.host != null && !newHostSet.contains(this.host)) {
            boolean hostValid =
                this.grammar.getGraphNames().contains(this.host.getName());
            doSetHost(hostValid ? this.host : null);
        }
        // restrict the selected rules to those that are (still)
        // in the grammar
        Collection<RuleView> newRuleSet = new LinkedHashSet<RuleView>();
        for (String ruleName : this.grammar.getRuleNames()) {
            newRuleSet.add(this.grammar.getRuleView(ruleName));
        }
        newRuleSet.retainAll(this.ruleSet);
        doSetRuleSet(newRuleSet);
        if (this.rule != null && !newRuleSet.contains(this.rule)) {
            boolean ruleValid =
                this.grammar.getRuleNames().contains(this.rule.getName());
            doSetRule(ruleValid ? this.rule : null);
        }
        if (reset) {
            doSetGts(null);
        }
        finish();
    }

    /** Updates the state according to a given grammar. */
    public final void setGrammar(StoredGrammarView grammar) {
        start();
        if (doSetGrammar(grammar)) {
            // reset the GTS in any case
            doSetGts(null);
            doSetHostSet(Collections.<GraphView>emptySet());
            doSetRuleSet(Collections.<RuleView>emptySet());
        }
        finish();
    }

    /** Updates the state according to a given grammar. */
    private final boolean doSetGrammar(StoredGrammarView grammar) {
        boolean result = (grammar != this.grammar);
        // transfer the undo manager to the new grammar
        if (result && this.undoManager != null) {
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
        return result;
    }

    /** 
     * Returns the currently selected host graph.
     */
    public final GraphView getHost() {
        return this.host;
    }

    /** Changes the currently selected host, based on the graph name.
     * @return if {@code true}, the host was actually changed.
     */
    public final boolean setHost(String name) {
        return setHost(this.grammar.getGraphView(name));
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
    private final boolean doSetHost(GraphView host) {
        boolean result = (host != this.host);
        if (result) {
            this.host = host;
            this.changes.add(Change.HOST);
            //            if (propagate) {
            //                doSetState(host == null ? this.activeState : null);
            //            }
        }
        return result;
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
    public final boolean setHostSet(Collection<String> hostNameSet) {
        start();
        List<GraphView> hostSet = new ArrayList<GraphView>();
        for (String hostName : hostNameSet) {
            hostSet.add(this.grammar.getGraphView(hostName));
        }
        if (doSetHostSet(hostSet) && !hostNameSet.contains(getHost())) {
            if (hostNameSet.isEmpty()) {
                doSetHost(null);
            } else {
                doSetHost(hostSet.iterator().next());
            }
        }
        return finish();
    }

    /** 
     * Changes the currently selected host graph list.
     * May also change the selected host graph.
     * @see #setHost(GraphView)
     */
    private final boolean doSetHostSet(Collection<GraphView> hostSet) {
        boolean result = !hostSet.equals(this.hostSet);
        if (result) {
            this.hostSet = hostSet;
            this.changes.add(Change.HOST_SET);
        }
        return result;
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
        if (doSetRule(rule)) {
            doSetTransition(null);
            doSetEvent(null);
        }
        return finish();
    }

    /**
     * Changes the currently selected rule and records the change,
     * if the new rule is different from the old.
     * @return {@code true} if a change was actually made
     */
    private final boolean doSetRule(RuleView rule) {
        boolean result = rule != this.rule;
        if (result) {
            this.rule = rule;
            this.changes.add(Change.RULE);
        }
        return result;
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
        if (doSetRuleSet(ruleSet) && !ruleSet.contains(getRule())) {
            if (ruleSet.isEmpty()) {
                doSetRule(null);
            } else {
                doSetRule(ruleSet.iterator().next());
            }
        }
        return finish();
    }

    /** 
     * Changes the currently selected rule set and records the change,
     * if the new rule set differs from the old.
     * @return {@code true} if a change was actually made
     * @see #setRule(RuleView)
     */
    private final boolean doSetRuleSet(Collection<RuleView> ruleSet) {
        boolean result = !ruleSet.equals(this.ruleSet);
        if (result) {
            this.ruleSet = ruleSet;
            this.changes.add(Change.RULE_SET);
        }
        return result;
    }

    @Override
    public String toString() {
        return "GuiState [gts=" + this.gts + ", state=" + this.state
            + ", transition=" + this.transition + ", event=" + this.event
            + ", grammar=" + this.grammar + ", host=" + this.host
            + ", hostSet=" + this.hostSet + ", rule=" + this.rule
            + ", ruleSet=" + this.ruleSet + ", changes=" + this.changes + "]";
    }

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
        for (SimulatorListener listener : new ArrayList<SimulatorListener>(
            this.listeners)) {
            listener.update(this, this.old, this.changes);
        }
    }

    /** Frozen GUI state, if there is a transaction underway. */
    private SimulatorModel old;
    /** Set of changes made in the current transaction. */
    private Set<SimulatorModel.Change> changes;
    /** Currently active GTS. */
    private GTS gts;
    /** Currently active state. */
    private GraphState state;
    /** Currently active transition. */
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
         * The selected and/or active state has changed.
         * @see SimulatorModel#getState()
         */
        STATE,
        /** 
         * The selected and/or active transition has changed.
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