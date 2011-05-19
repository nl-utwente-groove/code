package groove.gui;

import groove.abstraction.lts.AGTS;
import groove.explore.Exploration;
import groove.explore.strategy.ExploreStateStrategy;
import groove.explore.util.ExplorationStatistics;
import groove.explore.util.MatchApplier;
import groove.explore.util.RuleEventApplier;
import groove.graph.GraphInfo;
import groove.graph.GraphProperties;
import groove.graph.TypeLabel;
import groove.gui.SimulatorPanel.TabKind;
import groove.io.store.DefaultFileSystemStore;
import groove.io.store.SystemStore;
import groove.lts.GTS;
import groove.lts.GTSAdapter;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.MatchResult;
import groove.trans.GraphGrammar;
import groove.trans.SystemProperties;
import groove.view.CtrlView;
import groove.view.FormatException;
import groove.view.GraphView;
import groove.view.PrologView;
import groove.view.RuleView;
import groove.view.StoredGrammarView;
import groove.view.TypeView;
import groove.view.aspect.AspectGraph;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Collection of values that make up the state of 
 * the {@link Simulator}.
 * GUI state changes (such as selections) should be made through
 * a transaction on this object.
 */
public class SimulatorModel implements Cloneable {
    /**
     * Adds a given host graph to the host graphs in this grammar
     * @param hostGraph the new host graph
     * @return {@code true} if the GTS was invalidated as a result of the action
     * @throws IOException if the add action failed
     */
    public boolean doAddHost(AspectGraph hostGraph) throws IOException {
        start();
        try {
            StoredGrammarView grammar = getGrammar();
            grammar.getStore().putGraph(hostGraph);
            String hostName = hostGraph.getName();
            boolean result = hostName.equals(grammar.getStartGraphName());
            changeGrammar(result);
            changeHost(grammar.getGraphView(hostName));
            return result;
        } finally {
            finish();
        }
    }

    /**
     * Deletes a host graph from the grammar view.
     * @param hostName name of the host graph to be deleted
     * @return {@code true} if the GTS was invalidated as a result of the action
     * @throws IOException if the delete action failed
     */
    public boolean doDeleteHost(String hostName) throws IOException {
        start();
        try {
            StoredGrammarView grammar = getGrammar();
            // test now if this is the start state, before it is deleted from the
            // grammar
            boolean result = hostName.equals(grammar.getStartGraphName());
            grammar.getStore().deleteGraph(hostName);
            if (result) {
                // reset the start graph to null
                grammar.removeStartGraph();
            }
            changeGrammar(result);
            return result;
        } finally {
            finish();
        }
    }

    /**
     * Renames one of the graphs in the graph list. If the graph was the start
     * graph, uses the renamed graph again as start graph.
     * @param graph the old host graph
     * @param newName new name for the host graph
     * @return {@code true} if the GTS was invalidated as a result of the action
     * @throws IOException if the rename action failed
     */
    public boolean doRenameHost(AspectGraph graph, String newName)
        throws IOException {
        start();
        try {
            String oldName = graph.getName();
            // test now if this is the start state, before it is deleted from the
            // grammar
            StoredGrammarView grammar = getGrammar();
            String startGraphName = grammar.getStartGraphName();
            boolean result =
                oldName.equals(startGraphName)
                    || newName.equals(startGraphName);
            getStore().renameGraph(oldName, newName);
            if (result) {
                // reset the start graph to the renamed graph
                grammar.setStartGraph(newName);
            }
            changeGrammar(result);
            return result;
        } finally {
            finish();
        }
    }

    /**
     * Saves an aspect graph as a rule under a given name, and puts the rule
     * into the current grammar view.
     * @param ruleGraph the new rule, given as an aspect graph
     * @return {@code true} if the GTS was invalidated as a result of the action
     * @throws IOException if the add action failed
     */
    public boolean doAddRule(AspectGraph ruleGraph) throws IOException {
        start();
        try {
            AspectGraph oldGraph = getStore().putRule(ruleGraph);
            boolean result =
                GraphProperties.isEnabled(ruleGraph)
                    || GraphProperties.isEnabled(oldGraph);
            changeRule(ruleGraph.getName());
            changeGrammar(result);
            return result;
        } finally {
            finish();
        }
    }

    /**
     * Deletes a rule from the grammar and the file system, and resets the
     * grammar view.
     * @param name name of the rule to be deleted
     * @return {@code true} if the GTS was invalidated as a result of the action
     * @throws IOException if the delete action failed
     */
    public boolean doDeleteRule(String name) throws IOException {
        start();
        try {
            StoredGrammarView grammar = getGrammar();
            boolean result = grammar.getRuleView(name).isEnabled();
            AspectGraph rule = getStore().deleteRule(name);
            result |= GraphProperties.isEnabled(rule);
            changeGrammar(result);
            return result;
        } finally {
            finish();
        }
    }

    /** Inverts the enabledness of the current rule, and stores the result.
     * @param ruleGraph rule whose enabledness should be changed
     * @return {@code true} if the GTS was invalidated as a result of the action
     * @throws IOException if the delete action failed
     */
    public boolean doEnableRule(AspectGraph ruleGraph) throws IOException {
        GraphProperties properties =
            GraphInfo.getProperties(ruleGraph, true).clone();
        properties.setEnabled(!properties.isEnabled());
        AspectGraph newRuleGraph = ruleGraph.clone();
        GraphInfo.setProperties(newRuleGraph, properties);
        newRuleGraph.setFixed();
        return doAddRule(newRuleGraph);
    }

    /**
     * Renames one of the rules in the grammar.
     * @param ruleGraph rule to be renamed
     * @param newName new name for the rule
     * @return {@code true} if the GTS was invalidated as a result of the action
     * @throws IOException if the rename action failed
     */
    public boolean doRenameRule(AspectGraph ruleGraph, String newName)
        throws IOException {
        start();
        try {
            boolean result = GraphProperties.isEnabled(ruleGraph);
            String oldName = ruleGraph.getName();
            getStore().renameRule(oldName, newName);
            changeGrammar(result);
            changeRule(newName);
            return result;
        } finally {
            finish();
        }
    }

    /**
     * Adds a control program to this grammar.
     * @param name the name of the control program
     * @param program the control program text
     * @return {@code true} if the GTS was invalidated as a result of the action
     * @throws IOException if the add action failed
     */
    public boolean doAddControl(String name, String program) throws IOException {
        start();
        try {
            StoredGrammarView grammar = getGrammar();
            boolean result = name.equals(grammar.getControlName());
            getStore().putControl(name, program);
            changeGrammar(result);
            changeControl(name);
            return result;
        } finally {
            finish();
        }
    }

    /** 
     * Removes a control program from this grammar. 
     * @param name name of the prolog program
     * @return {@code true} if the GTS was invalidated as a result of the action
     * @throws IOException if the delete action failed
     */
    public boolean doDeleteControl(String name) throws IOException {
        start();
        try {
            StoredGrammarView grammar = getGrammar();
            boolean result = name.equals(grammar.getControlName());
            grammar.getStore().deleteControl(name);
            changeGrammar(result);
            return result;
        } finally {
            finish();
        }
    }

    /**
     * Renames one of the control programs in the grammar.
     * @param oldName old name of the program
     * @param newName new name for the program
     * @return {@code true} if the GTS was invalidated as a result of the action
     * @throws IOException if the rename action failed
     */
    public boolean doRenameControl(String oldName, String newName)
        throws IOException {
        start();
        try {
            boolean result = oldName.equals(getGrammar().getControlName());
            getStore().renameControl(oldName, newName);
            changeGrammar(result);
            return result;
        } finally {
            finish();
        }
    }

    /**
     * Adds a prolog program to this grammar.
     * @param name the name of the prolog program
     * @param program the prolog program text
     * @return {@code true} if the GTS was invalidated as a result of the action
     * @throws IOException if the add action failed
     */
    public boolean doAddProlog(String name, String program) throws IOException {
        start();
        try {
            getStore().putProlog(name, program);
            return false;
        } finally {
            finish();
        }
    }

    /** 
     * Removes a prolog program from this grammar.
     * @param name name of the prolog program
     * @return {@code true} if the GTS was invalidated as a result of the action
     * @throws IOException if the delete action failed
     */
    public boolean doDeleteProlog(String name) throws IOException {
        start();
        try {
            getStore().deleteProlog(name);
            return false;
        } finally {
            finish();
        }
    }

    /**
     * Saves an aspect graph as a type graph under a given name, and puts the
     * type graph into the current grammar view.
     * @param typeGraph the new type, given as an aspect graph
     * @return {@code true} if the GTS was invalidated as a result of the action
     * @throws IOException if the add action failed
     */
    public boolean doAddType(AspectGraph typeGraph) throws IOException {
        start();
        try {
            StoredGrammarView grammar = getGrammar();
            boolean result =
                grammar.getActiveTypeNames().contains(typeGraph.getName());
            grammar.getStore().putType(typeGraph);
            changeGrammar(result);
            changeType(typeGraph.getName());
            return result;
        } finally {
            finish();
        }
    }

    /** 
     * Removes a type graph from this grammar. 
     * @param name name of the type graph to be deleted
     * @return {@code true} if the GTS was invalidated as a result of the action
     * @throws IOException if the delete action failed
     */
    public boolean doDeleteType(String name) throws IOException {
        start();
        try {
            StoredGrammarView grammar = getGrammar();
            boolean result = grammar.getActiveTypeNames().contains(name);
            grammar.getStore().deleteType(name);
            changeGrammar(result);
            // we only need to refresh the grammar if the deleted
            // type graph was the currently active one
            return result;
        } finally {
            finish();
        }
    }

    /**
     * Renames a given type graph.
     * @param graph the type graph to be renamed
     * @param newName new name for the type graph
     * @return {@code true} if the GTS was invalidated as a result of the action
     * @throws IOException if the rename action failed
     */
    public boolean doRenameType(AspectGraph graph, String newName)
        throws IOException {
        start();
        try {
            String oldName = graph.getName();
            // test now if this is the type graph, before it is deleted from the
            // grammar
            boolean result =
                getGrammar().getActiveTypeNames().contains(oldName);
            getStore().renameType(oldName, newName);
            changeGrammar(result);
            if (oldName.equals(getType().getName())) {
                changeType(newName);
            }
            return result;
        } finally {
            finish();
        }
    }

    /**
     * Saves a changed list of active types in the system properties.
     */
    public boolean doSetActiveTypes(List<String> activeTypes)
        throws IOException {
        SystemProperties oldProperties = getGrammar().getProperties();
        SystemProperties newProperties = oldProperties.clone();
        newProperties.setTypeNames(activeTypes);
        return doSetProperties(newProperties);
    }

    /**
     * Changes the system properties.
     * @param newProperties the properties to be saved
     * @return {@code true} if the GTS was invalidated as a result of the action
     * @throws IOException if the save action failed
     */
    public boolean doSetProperties(SystemProperties newProperties)
        throws IOException {
        start();
        try {
            getStore().putProperties(newProperties);
            changeGrammar(true);
            return true;
        } finally {
            finish();
        }
    }

    /**
     * Sets a given host graph as start state. This results in a reset of
     * the LTS.
     * @param graph the new start graph
     * @return {@code true} if the GTS was invalidated as a result of the action
     */
    public boolean doSetStartGraph(AspectGraph graph) {
        start();
        try {
            getGrammar().setStartGraph(graph);
            changeGrammar(true);
            return true;
        } finally {
            finish();
        }
    }

    /**
     * Sets a graph with given name as start state. This results in a reset of
     * the LTS.
     * @param name name of the new start graph
     * @return {@code true} if the GTS was invalidated as a result of the action
     */
    public boolean doSetStartGraph(String name) {
        start();
        try {
            getGrammar().setStartGraph(name);
            changeGrammar(true);
            return true;
        } finally {
            finish();
        }
    }

    /**
     * Creates an empty grammar and an empty directory, and sets it in the
     * simulator.
     * @param grammarFile the grammar file to be used
     * @return {@code true} if the GTS was invalidated as a result of the action
     * @throws IOException if the create action failed
     */
    public boolean doNewGrammar(File grammarFile) throws IOException {
        StoredGrammarView grammar =
            StoredGrammarView.newInstance(grammarFile, true);
        setGrammar(grammar);
        return true;
    }

    /**
     * Refreshes the currently loaded grammar, if any. Does not ask for
     * confirmation. Has no effect if no grammar is currently loaded.
     * @return {@code true} if the GTS was invalidated as a result of the action
     * @throws IOException if the create action failed
     */
    public boolean doRefreshGrammar() throws IOException {
        start();
        try {
            boolean result = false;
            if (getStore() != null) {
                getStore().reload();
                changeGrammar(true);
                result = true;
            }
            return result;
        } finally {
            finish();
        }
    }

    /** 
     * Replaces all occurrences of a given label into another label, throughout
     * the grammar.
     * @param oldLabel the label to be renamed
     * @param newLabel the replacement label
     * @return {@code true} if the GTS was invalidated as a result of the action
     * @throws IOException if the relabel action failed 
     */
    public boolean doRelabel(TypeLabel oldLabel, TypeLabel newLabel)
        throws IOException {
        start();
        try {
            getStore().relabel(oldLabel, newLabel);
            changeGrammar(true);
            return true;
        } finally {
            finish();
        }
    }

    /** 
     * Renumbers the nodes in all graphs from {@code 0} upwards.
     * @return {@code true} if the GTS was invalidated as a result of the action
     * @throws IOException if the add action failed
     */
    public boolean doRenumber() throws IOException {
        start();
        try {
            boolean result = false;
            if (getStore() instanceof DefaultFileSystemStore) {
                ((DefaultFileSystemStore) getStore()).renumber();
                changeGrammar(true);
                result = true;
            }
            return result;
        } finally {
            finish();
        }
    }

    /** Fully explores a given state of the GTS. */
    public void doExploreState(GraphState state) {
        start();
        changeState(state);
        getExploreStateStrategy().prepare(getGts(), state);
        getExploreStateStrategy().next();
        changeGts(getGts());
        finish();
    }

    /**
     * Applies a match to the current state. The current state is set to the
     * derivation's target, and the current derivation to null.
     */
    public void applyMatch() {
        GraphTransition trans = getTransition();
        if (trans == null) {
            trans = getEventApplier().apply(getState(), getMatch().getEvent());
        }
        if (trans != null) {
            setState(trans.target());
        }
    }

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
        changeGts();
        return finish();
    }

    /**
     * Refreshes the GTS.
     */
    private final boolean changeGts() {
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
     * @param switchTab TODO
     * @return if {@code true}, the GTS was really changed
     * @see #setState(GraphState)
     */
    public final boolean setGts(GTS gts, boolean switchTab) {
        start();
        if (changeGts(gts)) {
            changeState(gts == null ? null : gts.startState());
            changeMatch(null);
        } else if (this.ltsListener.isChanged()) {
            this.ltsListener.clear();
            this.changes.add(Change.GTS);
        }
        if (switchTab) {
            changeTabKind(TabKind.LTS);
        }
        return finish();
    }

    /** 
     * Creates a fresh GTS and fires an update event.
     * This has the side effects of
     * <li> setting the state to the start state of the new GTS,
     * or to {@code null} if the new GTS is {@code null}
     * <li> setting the transition to {@code null}
     * <li> setting the event to {@code null}
     * @return {@code true} if the GTS could be created and set
     * @see #setGts(GTS, boolean)
     */
    public final boolean setGts() {
        try {
            GraphGrammar grammar = getGrammar().toGrammar();
            GTS gts;
            if (isAbstractionMode()) {
                gts = new AGTS(grammar);
            } else {
                gts = new GTS(grammar);
            }
            gts.getRecord().setRandomAccess(true);
            return setGts(gts, false);
        } catch (FormatException e) {
            return setGts(null, false);
        }
    }

    /** 
     * Changes the active GTS.
     * @see #setGts(GTS, boolean)
     */
    private final boolean changeGts(GTS gts) {
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
     * @see #setMatch(MatchResult)
     */
    public final boolean setState(GraphState state) {
        start();
        changeGts();
        if (changeState(state)) {
            changeMatch(null);
        }
        return finish();
    }

    /** 
     * Does the work for {@link #setState(GraphState)}, except
     * for firing the update.
     */
    private final boolean changeState(GraphState state) {
        // never reset the active state as long as there is a GTS
        boolean result = state != this.state;
        if (result) {
            this.state = state;
            this.changes.add(Change.STATE);
        }
        return result;
    }

    /** Returns the currently selected match result. */
    public final MatchResult getMatch() {
        return this.match;
    }

    /** Returns the currently selected transition, if the selected match is
     * a transition.
     * @see #getMatch()
     */
    public final GraphTransition getTransition() {
        return this.match instanceof GraphTransition
                ? (GraphTransition) this.match : null;
    }

    /** 
     * Changes the selected rule match, and fires an update event.
     * If the match is changed to a non-null event, also sets the rule.
     * If the match is changed to a non-null transition, also sets the state.
     * @return if {@code true}, the match was really changed
     */
    public final boolean setMatch(MatchResult match) {
        start();
        if (changeMatch(match) && match != null) {
            changeRule(match.getEvent().getRule().getName());
            if (match instanceof GraphTransition) {
                changeState(((GraphTransition) match).source());
            }
        }
        return finish();
    }

    /** 
     * Changes the selected event and, if the event is propagated,
     * possibly the rule.
     */
    private final boolean changeMatch(MatchResult match) {
        boolean result = match != this.match;
        if (result) {
            this.match = match;
            this.changes.add(Change.MATCH);
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
    public final void synchronize() {
        start();
        changeGrammar(true);
        finish();
    }

    /** 
     * Checks for changes in the currently loaded grammar view, 
     * but does not yet fire an update.
     * Should be called after any change in the grammar view or
     * underlying store.
     */
    private final void changeGrammar(boolean reset) {
        this.changes.add(Change.GRAMMAR);
        StoredGrammarView grammar = this.grammar;
        changeGrammar(grammar);
        // restrict the selected host graphs to those that are (still)
        // in the grammar
        Set<GraphView> newHostSet = new LinkedHashSet<GraphView>(this.hostSet);
        Set<GraphView> grammarHostSet = new HashSet<GraphView>();
        for (String hostName : grammar.getGraphNames()) {
            grammarHostSet.add(grammar.getGraphView(hostName));
        }
        newHostSet.retainAll(grammarHostSet);
        changeHostSet(newHostSet);
        // restrict the selected rules to those that are (still)
        // in the grammar
        Collection<RuleView> newRuleSet =
            new LinkedHashSet<RuleView>(this.ruleSet);
        Set<RuleView> grammarRuleSet = new HashSet<RuleView>();
        for (String ruleName : grammar.getRuleNames()) {
            grammarRuleSet.add(grammar.getRuleView(ruleName));
        }
        newRuleSet.retainAll(grammarRuleSet);
        changeRuleSet(newRuleSet);
        changeControl();
        changeType();
        if (reset) {
            changeGts(null);
            changeState(null);
            changeMatch(null);
        }
    }

    /** Updates the state according to a given grammar. */
    public final void setGrammar(StoredGrammarView grammar) {
        start();
        if (changeGrammar(grammar)) {
            // reset the GTS in any case
            changeGts(null);
            changeState(null);
            changeMatch(null);
            changeHostSet(Collections.<GraphView>emptySet());
            changeRuleSet(Collections.<RuleView>emptySet());
        }
        changeControl();
        changeType();
        finish();
    }

    /** Updates the state according to a given grammar. */
    private final boolean changeGrammar(StoredGrammarView grammar) {
        boolean result = (grammar != this.grammar);
        // if the grammar view is a different object,
        // do not attempt to keep the host graph and rule selections
        this.grammar = grammar;
        this.changes.add(Change.GRAMMAR);
        return result;
    }

    /** 
     * Returns the first of the currently selected host graphs.
     */
    public final GraphView getHost() {
        return this.hostSet.isEmpty() ? null : this.hostSet.iterator().next();
    }

    /** 
     * Returns the currently selected host graph list.
     */
    public final Collection<GraphView> getHostSet() {
        return this.hostSet;
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
        changeHostSet(Collections.singleton(host));
        return finish();
    }

    /** 
     * Changes the currently selected host graph list.
     * Changes the simulator tab to the graph view.
     * @return if {@code true}, actually made the change.
     * @see #setHost(GraphView)
     */
    public final boolean setHostSet(Collection<String> hostNameSet) {
        start();
        List<GraphView> hostSet = new ArrayList<GraphView>();
        for (String hostName : hostNameSet) {
            hostSet.add(this.grammar.getGraphView(hostName));
        }
        changeHostSet(hostSet);
        changeTabKind(TabKind.GRAPH);
        return finish();
    }

    /** 
     * Changes the currently selected host graph.
     * @see #setHost(GraphView)
     */
    private final boolean changeHost(GraphView hostView) {
        return changeHostSet(Collections.singleton(hostView));
    }

    /** 
     * Changes the currently selected host graph list.
     * May also change the selected host graph.
     * @see #setHost(GraphView)
     */
    private final boolean changeHostSet(Collection<GraphView> hostSet) {
        boolean result = !hostSet.equals(this.hostSet);
        if (result) {
            // keep the current set in the same order
            this.hostSet = new LinkedHashSet<GraphView>(this.hostSet);
            this.hostSet.retainAll(hostSet);
            this.hostSet.addAll(hostSet);
            this.changes.add(Change.HOST);
        }
        return result;
    }

    /** Returns the currently selected rule. */
    public final RuleView getRule() {
        return this.ruleSet.isEmpty() ? null : this.ruleSet.iterator().next();
    }

    /** 
     * Returns the currently selected rule set.
     */
    public final Collection<RuleView> getRuleSet() {
        return this.ruleSet;
    }

    /**
     * Changes the currently selected rule, based on the rule name.
     * Also changes the simulator tab to the rule panel.
     * @return if {@code true}, the rule was actually changed.
     */
    public final boolean setRule(String ruleName) {
        start();
        if (ruleName == null) {
            changeRule(null);
        } else {
            changeRule(ruleName);
            changeTabKind(TabKind.RULE);
        }
        return finish();
    }

    /** 
     * Changes the currently selected rule set.
     * If the set has size 0 or 1, also changes the selected rule.
     * @return if {@code true}, actually made the change.
     * @see #setRule(String)
     */
    public final boolean setRuleSet(Collection<RuleView> ruleSet) {
        start();
        changeRuleSet(ruleSet);
        return finish();
    }

    /** Changes the rule view to a given named rule. */
    private final boolean changeRule(String ruleName) {
        RuleView rule = getGrammar().getRuleView(ruleName);
        assert rule != null;
        return changeRuleSet(Collections.singleton(rule));
    }

    /** 
     * Changes the currently selected rule set and records the change,
     * if the new rule set differs from the old.
     * @return {@code true} if a change was actually made
     * @see #setRule(String)
     */
    private final boolean changeRuleSet(Collection<RuleView> ruleSet) {
        boolean result = !ruleSet.equals(this.ruleSet);
        if (result) {
            this.ruleSet = new LinkedHashSet<RuleView>(this.ruleSet);
            this.ruleSet.retainAll(ruleSet);
            this.ruleSet.addAll(ruleSet);
            this.changes.add(Change.RULE);
        }
        return result;
    }

    /** Returns the currently selected type graph. */
    public final TypeView getType() {
        return this.type;
    }

    /** Changes the currently selected type graph, based on the type name.
     * @return if {@code true}, the rule was actually changed.
     */
    public final boolean setType(String typeName) {
        start();
        changeType(typeName);
        return finish();
    }

    /** 
     * Sets the selected type graph, by using the current value if
     * that exists in the current grammar, or choosing the best value from the
     * type graphs available in the grammar otherwise. Only sets {@code null} 
     * if the grammar has no type graphs.
     */
    private final boolean changeType() {
        boolean result = false;
        StoredGrammarView grammar = this.grammar;
        TypeView type = this.type;
        if (type == null || !type.equals(grammar.getTypeView(type.getName()))) {
            String newTypeName = null;
            if (!grammar.getActiveTypeNames().isEmpty()) {
                // preferably get the type view from the active types
                newTypeName = grammar.getActiveTypeNames().iterator().next();
            } else if (!grammar.getTypeNames().isEmpty()) {
                // if there are none, take any type
                newTypeName = grammar.getTypeNames().iterator().next();
            }
            result = changeType(newTypeName);
        }
        return result;
    }

    /**
     * Changes the currently selected type graph and records the change,
     * if the new type graph is different from the old.
     * @return {@code true} if a change was actually made
     */
    private final boolean changeType(String typeName) {
        TypeView type =
            typeName == null ? null : getGrammar().getTypeView(typeName);
        boolean result = type != this.type;
        if (result) {
            this.type = type;
            this.changes.add(Change.TYPE);
        }
        return result;
    }

    /** Returns the currently selected control program. */
    public final CtrlView getControl() {
        return this.control;
    }

    /** Changes the currently selected control program, based on the program name.
     * @return if {@code true}, the program was actually changed.
     */
    public final boolean setControl(String controlName) {
        start();
        changeControl(controlName);
        return finish();
    }

    /** 
     * Sets the selected control field, by using the current value if
     * that exists in the current grammar, or choosing the best value from the
     * control programs available in the grammar otherwise. Only sets {@code null} 
     * if the grammar has no control programs.
     */
    private final boolean changeControl() {
        boolean result = false;
        // check the selected control view
        StoredGrammarView grammar = this.grammar;
        CtrlView control = this.control;
        if (control == null
            || !control.equals(grammar.getControlView(control.getName()))) {
            String newControlName = grammar.getControlName();
            if (newControlName == null && !grammar.getControlNames().isEmpty()) {
                newControlName = grammar.getControlNames().iterator().next();
            }
            result = changeControl(newControlName);
        }
        return result;
    }

    /**
     * Changes the currently selected control program and records the change,
     * if the new type program is different from the old.
     * @return {@code true} if a change was actually made
     */
    private final boolean changeControl(String controlName) {
        CtrlView control =
            controlName == null ? null : getGrammar().getControlView(
                controlName);
        boolean result = control != this.control;
        if (result) {
            this.control = control;
            this.changes.add(Change.CONTROL);
        }
        return result;
    }

    /** Returns the currently selected type graph. */
    public final TypeView getProlog() {
        return this.type;
    }

    /** Changes the currently selected prolog program, based on the program name.
     * @return if {@code true}, the program was actually changed.
     */
    public final boolean setProlog(String prologName) {
        if (prologName == null) {
            return setProlog((PrologView) null);
        } else {
            PrologView prolog = getGrammar().getPrologView(prologName);
            assert prolog != null;
            return setProlog(prolog);
        }
    }

    /**
     * Changes the currently selected prolog program.
     * @return if {@code true}, the program was actually changed.
     */
    public final boolean setProlog(PrologView prolog) {
        start();
        changeProlog(prolog);
        return finish();
    }

    /**
     * Changes the currently selected prolog program and records the change,
     * if the new program is different from the old.
     * @return {@code true} if a change was actually made
     */
    private final boolean changeProlog(PrologView prolog) {
        boolean result = prolog != this.prolog;
        if (result) {
            this.prolog = prolog;
            this.changes.add(Change.PROLOG);
        }
        return result;
    }

    /** Returns true if the simulator is in abstraction mode. */
    public boolean isAbstractionMode() {
        return this.abstractionMode;
    }

    /** 
     * Sets the abstraction mode.
     * @param value if {@code true}, the simulator is set to abstract.
     */
    public boolean setAbstractionMode(boolean value) {
        start();
        changeAbstractionMode(value);
        return finish();
    }

    /** 
     * Sets the abstraction mode.
     * @param value if {@code true}, the simulator is set to abstract.
     */
    private boolean changeAbstractionMode(boolean value) {
        boolean result = false;
        if (value != this.abstractionMode) {
            this.abstractionMode = value;
            this.changes.add(Change.ABSTRACT);
        }
        return result;
    }

    /**
     * Returns the internally stored default exploration.
     */
    public Exploration getExploration() {
        return this.exploration;
    }

    /**
     * Sets the internally stored default exploration.
     * @param exploration may not be null
     */
    public void setExploration(Exploration exploration) {
        this.exploration = exploration;
    }

    /** Returns the kind of tab currently showing in the simulator panel. */
    public final SimulatorPanel.TabKind getTabKind() {
        return this.tabKind;
    }

    /** Changes the kind of tab showing in the simulator panel. */
    public final boolean setTabKind(SimulatorPanel.TabKind tabKind) {
        start();
        changeTabKind(tabKind);
        return finish();
    }

    /** Changes the kind of tab showing in the simulator panel. */
    private boolean changeTabKind(SimulatorPanel.TabKind tabKind) {
        boolean result = false;
        if (tabKind != this.tabKind) {
            this.tabKind = tabKind;
            this.changes.add(Change.TAB);
        }
        return result;
    }

    /**
     * Returns the exploration statistics object associated with the current
     * GTS.
     */
    public ExplorationStatistics getExplorationStats() {
        if (this.explorationStats == null
            || this.explorationStats.getGts() != getGts()) {
            this.explorationStats = new ExplorationStatistics(getGts());
            this.explorationStats.configureForSimulator();
        }
        return this.explorationStats;
    }

    @Override
    public String toString() {
        return "GuiState [gts=" + this.gts + ", state=" + this.state
            + ", match=" + this.match + ", grammar=" + this.grammar
            + ", hostSet=" + this.hostSet + ", ruleSet=" + this.ruleSet
            + ", changes=" + this.changes + "]";
    }

    /** 
     * Adds a given simulation listener to the list.
     * An optional parameter indicates which kinds of changes should be
     * notified.
     * @param listener the listener to be registered
     * @param changes the set of change events the listener should be notified of;
     * if empty, the listener is notified of all types of events
     */
    public void addListener(SimulatorListener listener, Change... changes) {
        if (changes.length == 0) {
            changes = EnumSet.allOf(Change.class).toArray(new Change[0]);
        }
        for (Change change : changes) {
            List<SimulatorListener> listeners = this.listeners.get(change);
            if (!listeners.contains(listener)) {
                listeners.add(listener);
            }
        }
    }

    /**
     * Removes a given listener from the list.
     * @param listener the listener to be removed
     * @param changes the set of change events the listener should be notified of;
     * if empty, the listener is notified of all types of events 
     */
    public void removeListener(SimulatorListener listener, Change... changes) {
        if (changes.length == 0) {
            changes = EnumSet.allOf(Change.class).toArray(new Change[0]);
        }
        for (Change change : changes) {
            this.listeners.get(change).remove(listener);
        }
    }

    /** 
     * Starts a transaction.
     * This is only allowed if no transaction is currently underway.
     */
    private void start() {
        assert this.old == null;
        this.old = clone();
        this.changes.clear();
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
        this.changes.clear();
        return result;
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
     * Notifies all registered listeners of the changes involved in the current
     * transaction.
     */
    private void fireUpdate() {
        Set<SimulatorListener> notified = new HashSet<SimulatorListener>();
        for (Change change : this.changes) {
            for (SimulatorListener listener : new ArrayList<SimulatorListener>(
                this.listeners.get(change))) {
                if (notified.add(listener)) {
                    listener.update(this, this.old, this.changes);
                }
            }
        }
    }

    /** Frozen GUI state, if there is a transaction underway. */
    private SimulatorModel old;
    /** Set of changes made in the current transaction. */
    private Set<Change> changes = EnumSet.noneOf(Change.class);
    /** Currently active GTS. */
    private GTS gts;
    /** Currently active state. */
    private GraphState state;
    /** Currently selected match (event or transition). */
    private MatchResult match;
    /** Currently loaded grammar. */
    private StoredGrammarView grammar;
    /** Multiple selection of host graph views. */
    private Collection<GraphView> hostSet = Collections.emptyList();
    /** Multiple selection of rule views. */
    private Collection<RuleView> ruleSet = Collections.emptyList();
    /** Currently selected type view. */
    private TypeView type;
    /** Currently selected control view. */
    private CtrlView control;
    /** Currently selected prolog view. */
    private PrologView prolog;

    /**
     * The default exploration to be performed. This value is either the
     * previous exploration, or the default constructor of the Exploration class
     * (=breadth first). This value may never be null (and must be initialized
     * explicitly).
     */
    private Exploration exploration = new Exploration();
    /** Statistics for the last exploration performed. */
    private ExplorationStatistics explorationStats;

    /** Flag to indicate that the Simulator is in abstraction mode. */
    private boolean abstractionMode = false;
    /** Kind of tab that currently showing in the simulator panel. */
    private SimulatorPanel.TabKind tabKind;
    /** Array of listeners. */
    private final Map<Change,List<SimulatorListener>> listeners =
        new EnumMap<Change,List<SimulatorListener>>(Change.class);
    { // initialise the listener map to empty listener lists
        for (Change change : EnumSet.allOf(Change.class)) {
            this.listeners.put(change, new ArrayList<SimulatorListener>());
        }
    }
    private final MyLTSListener ltsListener = new MyLTSListener();

    /**
     * Returns the state generator for the current GTS, if any.
     */
    private RuleEventApplier getEventApplier() {
        GTS gts = getGts();
        if (this.eventApplier == null || this.eventApplier.getGTS() != gts) {
            if (gts != null) {
                this.eventApplier = new MatchApplier(gts);
            }
        }
        return this.eventApplier;
    }

    /** The rule event applier for the current GTS. */
    private RuleEventApplier eventApplier;

    /**
     * @return the explore-strategy for exploring a single state
     */
    private ExploreStateStrategy getExploreStateStrategy() {
        if (this.exploreStateStrategy == null) {
            this.exploreStateStrategy = new ExploreStateStrategy();
        }
        return this.exploreStateStrategy;
    }

    private ExploreStateStrategy exploreStateStrategy;

    /** Change type. */
    public enum Change {
        /** The abstraction mode has changed. */
        ABSTRACT,
        /**
         * The selected control program has changed.
         */
        CONTROL,
        /** 
         * The loaded grammar has changed.
         * @see SimulatorModel#getGrammar()
         */
        GRAMMAR,
        /** 
         * The GTS has changed.
         * @see SimulatorModel#getGts()
         */
        GTS,
        /** 
         * The selected set of host graphs has changed.
         * @see SimulatorModel#getHost()
         * @see SimulatorModel#getHostSet()
         */
        HOST,
        /** 
         * The selected match (i.e., a rule event or a transition) has changed.
         * @see SimulatorModel#getMatch()
         * @see SimulatorModel#getTransition()
         */
        MATCH,
        /**
         * The selected prolog program has changed.
         */
        PROLOG,
        /** 
         * The selected rule set has changed.
         * @see SimulatorModel#getRule()
         * @see SimulatorModel#getRuleSet()
         */
        RULE,
        /** 
         * The selected and/or active state has changed.
         * @see SimulatorModel#getState()
         */
        STATE,
        /** The selected tab in the simulator panel has changed. */
        TAB,
        /**
         * The selected type graph has changed.
         */
        TYPE,
    }

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