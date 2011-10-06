package groove.gui;

import groove.explore.Exploration;
import groove.explore.strategy.ExploreStateStrategy;
import groove.explore.util.ExplorationStatistics;
import groove.explore.util.MatchApplier;
import groove.explore.util.RuleEventApplier;
import groove.graph.GraphInfo;
import groove.graph.GraphProperties;
import groove.graph.TypeLabel;
import groove.io.store.DefaultFileSystemStore;
import groove.io.store.SystemStore;
import groove.lts.GTS;
import groove.lts.GTSAdapter;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.MatchResult;
import groove.trans.GraphGrammar;
import groove.trans.ResourceKind;
import groove.trans.SystemProperties;
import groove.view.FormatException;
import groove.view.GrammarModel;
import groove.view.GraphBasedModel;
import groove.view.ResourceModel;
import groove.view.TextBasedModel;
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
     * Deletes a set of named resources from the grammar.
     * @param resource the kind of the resources
     * @param names the names of the resources to be deleted
     * @throws IOException if the action failed due to an IO error
     */
    public void doDelete(ResourceKind resource, Set<String> names)
        throws IOException {
        boolean result = false;
        start();
        try {
            GrammarModel grammar = getGrammar();
            switch (resource) {
            case CONTROL:
                result = names.contains(grammar.getControlName());
                getStore().deleteTexts(ResourceKind.CONTROL, names);
                break;
            case HOST:
                // test now if this is the start state, before it is deleted from the
                // grammar
                result = names.contains(grammar.getStartGraphName());
                getStore().deleteGraphs(ResourceKind.HOST, names);
                if (result) {
                    // reset the start graph to null
                    grammar.removeStartGraph();
                }
                break;
            case PROLOG:
                getStore().deleteTexts(ResourceKind.PROLOG, names);
                break;
            case RULE:
            case TYPE:
                for (AspectGraph oldGraph : getStore().deleteGraphs(resource,
                    names)) {
                    result |= isEnabled(oldGraph);
                }
                break;
            case PROPERTIES:
            default:
                assert false;
            }
            changeGrammar(result);
        } finally {
            finish();
        }
    }

    /**
     * Renames a resource of a given kind.
     * @param resource the kind of the resource to be renamed
     * @param oldName the name of the resource to be renamed
     * @param newName the new name for the rule
     * @return {@code true} if the GTS was invalidated as a result of the action
     * @throws IOException if the action failed due to an IO error
     */
    public boolean doRename(ResourceKind resource, String oldName,
            String newName) throws IOException {
        boolean result = false;
        start();
        try {
            switch (resource) {
            case CONTROL:
                result = oldName.equals(getGrammar().getControlName());
                break;
            case HOST:
                GrammarModel grammar = getGrammar();
                String startGraphName = grammar.getStartGraphName();
                result =
                    oldName.equals(startGraphName)
                        || newName.equals(startGraphName);
                if (result) {
                    // reset the start graph to the renamed graph
                    grammar.setStartGraph(newName);
                }
                break;
            case RULE:
            case TYPE:
                result =
                    getGrammar().getResource(resource, oldName).isEnabled();
                break;
            }
            getStore().rename(resource, oldName, newName);
            changeSelected(resource, newName);
            changeGrammar(result);
            changeDisplay(DisplayKind.toDisplay(resource));
        } finally {
            finish();
        }
        return result;
    }

    /**
     * Changes the enabling of a set of named resources from the grammar.
     * @param resource the kind of the resources
     * @param names the names of the resources to be changed
     * @return {@code true} if the GTS was invalidated as a result of the action
     * @throws IOException if the action failed due to an IO error
     */
    public boolean doEnable(ResourceKind resource, Set<String> names)
        throws IOException {
        start();
        boolean result = true;
        try {
            setEnabled(resource, names);
            changeDisplay(DisplayKind.toDisplay(resource));
            changeGrammar(result);
        } finally {
            finish();
        }
        return result;
    }

    /** Enables a collection of named resources of a given kind. */
    private void setEnabled(ResourceKind kind, Set<String> names)
        throws IOException {
        String name = names.iterator().next();
        SystemProperties oldProperties = getGrammar().getProperties();
        SystemProperties newProperties = oldProperties.clone();
        switch (kind) {
        case CONTROL:
            if (name.equals(oldProperties.getControlName())) {
                name = "";
            }
            newProperties.setControlName(name);
            getStore().putProperties(newProperties);
            break;
        case HOST:
            getGrammar().setStartGraph(name);
            break;
        case RULE:
            Collection<AspectGraph> newRules =
                new ArrayList<AspectGraph>(names.size());
            for (String ruleName : names) {
                AspectGraph oldRule =
                    getStore().getGraphs(ResourceKind.RULE).get(ruleName);
                GraphProperties properties =
                    GraphInfo.getProperties(oldRule, true).clone();
                properties.setEnabled(!properties.isEnabled());
                AspectGraph newRule = oldRule.clone();
                GraphInfo.setProperties(newRule, properties);
                newRule.setFixed();
                newRules.add(newRule);
            }
            getStore().putGraphs(ResourceKind.RULE, newRules);
            break;
        case TYPE:
        case PROLOG:
            List<String> actives = new ArrayList<String>();
            if (kind == ResourceKind.TYPE) {
                actives.addAll(newProperties.getTypeNames());
            } else {
                actives.addAll(newProperties.getPrologNames());
            }
            for (String typeName : names) {
                if (!actives.remove(typeName)) {
                    actives.add(typeName);
                }
            }
            if (kind == ResourceKind.TYPE) {
                newProperties.setTypeNames(actives);
            } else {
                newProperties.setPrologNames(actives);
            }
            getStore().putProperties(newProperties);
            break;
        case PROPERTIES:
        default:
            assert false;
        }
    }

    /**
     * Adds a given graph-based resource resources in this grammar
     * @param newGraph the new host graph
     * @return {@code true} if the GTS was invalidated as a result of the action
     * @throws IOException if the add action failed
     */
    public boolean doAddGraph(ResourceKind kind, AspectGraph newGraph)
        throws IOException {
        start();
        try {
            boolean result = isEnabled(newGraph);
            Collection<AspectGraph> oldGraphs =
                getStore().putGraphs(kind, Collections.singleton(newGraph));
            for (AspectGraph oldGraph : oldGraphs) {
                result |= isEnabled(oldGraph);
            }
            changeGrammar(result);
            changeSelected(kind, newGraph.getName());
            changeDisplay(DisplayKind.toDisplay(kind));
            return result;
        } finally {
            finish();
        }
    }

    /** Tests if a given aspect graph corresponds to an enabled resource. */
    private boolean isEnabled(AspectGraph graph) {
        boolean result = false;
        String name = graph.getName();
        SystemProperties properties = getGrammar().getProperties();
        switch (graph.getRole()) {
        case HOST:
            result = name.equals(getGrammar().getStartGraphName());
            break;
        case RULE:
            result = GraphProperties.isEnabled(graph);
            break;
        case TYPE:
            result = properties.getTypeNames().contains(name);
            break;
        default:
            assert false;
        }
        return result;
    }

    /**
     * Adds a text-based resource to this grammar.
     * @param name the name of the resource
     * @param program the resource text
     * @return {@code true} if the GTS was invalidated as a result of the action
     * @throws IOException if the add action failed
     */
    public boolean doAddText(ResourceKind kind, String name, String program)
        throws IOException {
        start();
        try {
            GrammarModel grammar = getGrammar();
            boolean result =
                kind != ResourceKind.CONTROL
                    || name.equals(grammar.getControlName());
            getStore().putTexts(kind, Collections.singletonMap(name, program));
            changeGrammar(result);
            changeSelected(kind, name);
            changeDisplay(DisplayKind.toDisplay(kind));
            return result;
        } finally {
            finish();
        }
    }

    /**
     * Sets the priority of a set of rules.
     * @param priorityMap mapping from rule names to their new priorities
     * @return {@code true} if the GTS was invalidated as a result of the action
     * @throws IOException if the action failed due to an IO error
     */
    public boolean doSetPriority(Map<String,Integer> priorityMap)
        throws IOException {
        start();
        ResourceKind resource = ResourceKind.RULE;
        Set<AspectGraph> newGraphs = new HashSet<AspectGraph>();
        for (Map.Entry<String,Integer> entry : priorityMap.entrySet()) {
            AspectGraph oldGraph =
                getStore().getGraphs(resource).get(entry.getKey());
            AspectGraph newGraph = oldGraph.clone();
            GraphInfo.getProperties(newGraph, true).setPriority(
                entry.getValue());
            newGraph.setFixed();
            newGraphs.add(newGraph);
        }
        try {
            getStore().putGraphs(resource, newGraphs);
            changeGrammar(true);
            changeDisplay(DisplayKind.toDisplay(resource));
            return true;
        } finally {
            finish();
        }
    }

    /**
     * Changes the default exploration in the system properties.
     * @param exploration the new default exploration
     * @return {@code true} if the GTS was invalidated as a result of the action
     * @throws IOException if the action failed
     */
    public boolean doSetDefaultExploration(Exploration exploration)
        throws IOException {
        SystemProperties properties = getGrammar().getProperties();
        SystemProperties newProperties = properties.clone();
        newProperties.setExploration(exploration.toParsableString());
        return doSetProperties(newProperties);
    }

    /**
     * Changes the system properties.
     * @param newProperties the properties to be saved
     * @return {@code true} if the GTS was invalidated as a result of the action
     * @throws IOException if the action failed
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
            changeDisplay(DisplayKind.HOST);
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
        GrammarModel grammar = GrammarModel.newInstance(grammarFile, true);
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
    public void doExploreState() {
        start();
        getExploreStateStrategy().prepare(getGts(), getState());
        getExploreStateStrategy().next();
        changeGts(getGts(), true);
        GraphTransition outTrans = getOutTransition(getState());
        if (outTrans != null) {
            changeMatch(outTrans);
            changeDisplay(DisplayKind.LTS);
        }
        finish();
    }

    /**
     * Applies a match to the current state. The current state is set to the
     * derivation's target, and the current derivation to null.
     */
    public void doApplyMatch() {
        start();
        GraphTransition trans = getTransition();
        if (trans == null) {
            trans = getEventApplier().apply(getState(), getMatch().getEvent());
        }
        if (trans != null) {
            changeGts();
            // fake it by pretending the old match was the
            // transition that has just been applied
            this.old.match = trans;
            GraphState state = this.state = trans.target();
            this.changes.add(Change.STATE);
            GraphTransition outTrans = null;
            // set the match to an outgoing transition
            if (state.isClosed()) {
                outTrans = getOutTransition(state);
            }
            changeMatch(outTrans);
            changeDisplay(DisplayKind.LTS);
        }
        finish();
    }

    /** Returns the first outgoing transition that is not a self-loop,
     * preferably one that also leads to an open state.
     * Returns {@code null} if there is no such outgoing transition. */
    private GraphTransition getOutTransition(GraphState state) {
        GraphTransition result = null;
        for (GraphTransition outTrans : getState().getTransitionSet()) {
            if (outTrans.target() != getState()) {
                result = outTrans;
                if (!outTrans.target().isClosed()) {
                    break;
                }
            }
        }
        return result;
    }

    /** Indicates if there is an active GTS. */
    public final boolean hasGts() {
        return getGts() != null;
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
     * @param switchTab if {@code true}, the simulator tab will be switched to the LTS
     * @return if {@code true}, the GTS was really changed
     * @see #setState(GraphState)
     */
    public final boolean setGts(GTS gts, boolean switchTab) {
        start();
        if (changeGts(gts, false)) {
            changeState(gts == null ? null : gts.startState());
            changeMatch(null);
        } else if (this.ltsListener.isChanged()) {
            this.ltsListener.clear();
            this.changes.add(Change.GTS);
        }
        if (gts != null && getState() == null) {
            changeState(gts.startState());
        }
        if (switchTab) {
            changeDisplay(DisplayKind.LTS);
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
            GTS gts = new GTS(grammar);
            gts.getRecord().setRandomAccess(true);
            return setGts(gts, false);
        } catch (FormatException e) {
            return setGts(null, false);
        }
    }

    /** 
     * Changes the active GTS.
     * @param always also fire an event if the GTS actually is the same object.
     * @see #setGts(GTS, boolean)
     */
    private final boolean changeGts(GTS gts, boolean always) {
        boolean result = always || this.gts != gts;
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
     * Indicates if there is an active state.
     */
    public final boolean hasState() {
        return getState() != null;
    }

    /** 
     * Returns the currently active state, if any.
     * If the GTS is set, there is always an active state.
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
            if (state != null) {
                changeDisplay(DisplayKind.LTS);
            }
        }
        return finish();
    }

    /** 
     * Does the work for {@link #setState(GraphState)}, except
     * for firing the update.
     */
    private final boolean changeState(GraphState state) {
        // never reset the active state as long as there is a GTS
        boolean result =
            state != this.state || this.changes.contains(Change.GTS);
        if (result) {
            this.state = state;
            this.changes.add(Change.STATE);
        }
        return result;
    }

    /** Indicates if there is a currently selected match result. */
    public final boolean hasMatch() {
        return getMatch() != null;
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
            changeSelected(ResourceKind.RULE,
                match.getEvent().getRule().getName());
            if (match instanceof GraphTransition) {
                changeState(((GraphTransition) match).source());
            }
            changeDisplay(DisplayKind.LTS);
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
     * Indicates if there is a loaded grammar.
     */
    public final boolean hasGrammar() {
        return getGrammar() != null;
    }

    /**
     * Returns the currently loaded graph grammar, if any.
     */
    public final GrammarModel getGrammar() {
        return this.grammar;
    }

    /** Updates the model according to a given grammar. */
    public final void setGrammar(GrammarModel grammar) {
        start();
        if (changeGrammar(grammar)) {
            // reset the GTS in any case
            changeGts(null, false);
            changeState(null);
            changeMatch(null);
            changeExploration();
            for (ResourceKind resource : ResourceKind.all(false)) {
                changeSelected(resource, null);
            }
            if (getExploration() != null && grammar != null
                && !grammar.hasErrors()) {
                try {
                    getExploration().test(grammar.toGrammar());
                } catch (FormatException e) {
                    // the exploration strategy is not compatible with the 
                    // grammar;
                    // reset to default exploration
                    changeExploration(new Exploration());
                }
            }
        }
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
        GrammarModel grammar = this.grammar;
        changeGrammar(grammar);
        if (reset) {
            changeGts(null, false);
            changeState(null);
            changeMatch(null);
            changeExploration();
        }
        // restrict the selected resources to those that are (still)
        // in the grammar
        for (ResourceKind resource : ResourceKind.all(false)) {
            Set<String> newNames = new LinkedHashSet<String>();
            newNames.addAll(getSelectSet(resource));
            newNames.retainAll(grammar.getNames(resource));
            changeSelectedSet(resource, newNames);
        }
    }

    /** Updates the state according to a given grammar. */
    private final boolean changeGrammar(GrammarModel grammar) {
        boolean result = (grammar != this.grammar);
        // if the grammar view is a different object,
        // do not attempt to keep the host graph and rule selections
        this.grammar = grammar;
        this.changes.add(Change.GRAMMAR);
        return result;
    }

    /** Convenience method to return the store of the currently loaded 
     * grammar view, if any.
     */
    public final SystemStore getStore() {
        return this.grammar == null ? null : this.grammar.getStore();
    }

    /** 
     * Returns the selected resource of a given kind, or {@code null}
     * if no resource is selected.
     * Convenience method for {@code getGrammar().getResource(resource,getSelected(name))}. 
     */
    public final ResourceModel<?> getResource(ResourceKind resource) {
        String name = getSelected(resource);
        return name == null ? null : getGrammar().getResource(resource, name);
    }

    /** 
     * Returns the selected graph-based resource of a given kind, or {@code null}
     * if no resource is selected.
     * Convenience method for {@code getGrammar().getGraphResource(resource,getSelected(name))}. 
     * @param resource the resource kind for which the resource is retrieved; must be graph-based
     */
    public final GraphBasedModel<?> getGraphResource(ResourceKind resource) {
        String name = getSelected(resource);
        return name == null ? null : getGrammar().getGraphResource(resource,
            name);
    }

    /** 
     * Returns the selected resource of a given kind, or {@code null}
     * if no resource is selected.
     * Convenience method for {@code getGrammar().getTextResource(resource,getSelected(name))}. 
     * @param resource the resource kind for which the resource is retrieved; must be text-based
     */
    public final TextBasedModel<?> getTextResource(ResourceKind resource) {
        String name = getSelected(resource);
        return name == null ? null : getGrammar().getTextResource(resource,
            name);
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
     * Tests if there is a selected resource of a given kind.
     * Convenience method for {@code getResource(ResourceKind) != null}.
     */
    public final boolean isSelected(ResourceKind kind) {
        return getSelected(kind) != null;
    }

    /** 
     * Returns the currently selected resource name of a given kind.
     * @param kind the resource kind
     * @return the currently selected resource, or {@code null} if
     * none is selected
     */
    public final String getSelected(ResourceKind kind) {
        Set<String> resourceSet = this.resources.get(kind);
        return resourceSet.isEmpty() ? null : resourceSet.iterator().next();
    }

    /** 
     * Returns set of names of the currently selected resources of a given kind.
     * @param kind the resource kind
     * @return the names of the currently selected resource
     */
    public final Set<String> getSelectSet(ResourceKind kind) {
        return this.resources.get(kind);
    }

    /** Changes the selection of a given resource kind. */
    public final boolean doSelect(ResourceKind kind, String name) {
        start();
        changeSelected(kind, name);
        if (isSelected(kind) || kind == ResourceKind.HOST && hasState()) {
            changeDisplay(DisplayKind.toDisplay(kind));
        }
        return finish();
    }

    /** Changes the selection of a given resource kind. */
    public final boolean doSelectSet(ResourceKind kind, Collection<String> names) {
        start();
        changeSelectedSet(kind, names);
        return finish();
    }

    /** Changes the selected value of a given resource kind. */
    private boolean changeSelected(ResourceKind kind, String name) {
        return changeSelectedSet(
            kind,
            name == null ? Collections.<String>emptySet()
                    : Collections.singleton(name));
    }

    /** 
     * Changes the currently selected resource set and records the change,
     * if the new resource set differs from the old.
     * @return {@code true} if a change was actually made
     */
    private final boolean changeSelectedSet(ResourceKind resource,
            Collection<String> names) {
        boolean result = false;
        Set<String> newSelection = new LinkedHashSet<String>(names);
        Set<String> allNames = getGrammar().getNames(resource);
        // try to select a name
        if (newSelection.isEmpty() && getGrammar() != null) {
            String name = null;
            // find the best choice of name
            if (resource == ResourceKind.HOST) {
                // for a host graph, the best choice is the current state
                // to select that, set the name to a non-existent one;
                // this will be filtered out later
                if (hasState()) {
                    name = "";
                } else {
                    // the next best choice is the start graph name
                    name = getGrammar().getStartGraphName();
                }
            } else if (resource == ResourceKind.CONTROL) {
                // for control, the best choice is the active control program
                name = getGrammar().getControlName();
            }
            if (name == null && !allNames.isEmpty()) {
                // otherwise, just take the first existing name (if there is one)
                name = allNames.iterator().next();
            }
            if (name != null) {
                newSelection.add(name);
            }
        }
        newSelection.retainAll(allNames);
        if (!newSelection.equals(getSelectSet(resource))) {
            this.resources.put(resource, newSelection);
            this.changes.add(Change.toChange(resource));
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
     * @throws FormatException if the new exploration is not
     * compatible with the existing grammar
     */
    public void setExploration(Exploration exploration) throws FormatException {
        if (hasGrammar() && !getGrammar().hasErrors()) {
            exploration.test(getGrammar().toGrammar());
        }
        changeExploration(exploration);
    }

    /**
     * If the grammar has a default exploration,
     * sets the exploration to that exploration.
     */
    public void changeExploration() {
        Exploration exploration = getGrammar().getDefaultExploration();
        if (exploration != null) {
            changeExploration(exploration);
        }
    }

    /**
     * Sets the internally stored default exploration.
     * @param exploration may not be null
     */
    public void changeExploration(Exploration exploration) {
        this.exploration = exploration;
    }

    /** Returns the display currently showing in the simulator panel. */
    public final DisplayKind getDisplay() {
        return this.display;
    }

    /** Changes the display showing in the simulator panel. */
    public final boolean setDisplay(DisplayKind display) {
        start();
        changeDisplay(display);
        return finish();
    }

    /** Changes the display showing in the simulator panel. */
    private boolean changeDisplay(DisplayKind display) {
        boolean result = false;
        //        if (display != this.display) {
        this.display = display;
        this.changes.add(Change.DISPLAY);
        //        }
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
            + ", resources=" + this.resources + ", changes=" + this.changes
            + "]";
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
            result.resources =
                new EnumMap<ResourceKind,Set<String>>(ResourceKind.class);
            for (ResourceKind resource : ResourceKind.all(false)) {
                result.resources.put(resource, new LinkedHashSet<String>(
                    this.resources.get(resource)));
            }
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
    /** Mapping from resource kinds to sets of selected resources of that kind. */
    private Map<ResourceKind,Set<String>> resources =
        new EnumMap<ResourceKind,Set<String>>(ResourceKind.class);
    {
        for (ResourceKind resource : ResourceKind.all(false)) {
            this.resources.put(resource, Collections.<String>emptySet());
        }
    }
    /** Currently loaded grammar. */
    private GrammarModel grammar;

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
    /** Display currently showing in the simulator panel. */
    private DisplayKind display = DisplayKind.HOST;
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
    public static enum Change {
        /** The abstraction mode has changed. */
        ABSTRACT,
        /**
         * The selected control program has changed.
         */
        CONTROL(ResourceKind.CONTROL),
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
         */
        HOST(ResourceKind.HOST),
        /** 
         * The selected match (i.e., a rule event or a transition) has changed.
         * @see SimulatorModel#getMatch()
         * @see SimulatorModel#getTransition()
         */
        MATCH,
        /**
         * The selected prolog program has changed.
         */
        PROLOG(ResourceKind.PROLOG),
        /** 
         * The selected rule set has changed.
         */
        RULE(ResourceKind.RULE),
        /** 
         * The selected and/or active state has changed.
         * @see SimulatorModel#getState()
         */
        STATE,
        /** The selected tab in the simulator panel has changed. */
        DISPLAY,
        /**
         * The selected type graph has changed.
         */
        TYPE(ResourceKind.TYPE);

        private Change() {
            this(null);
        }

        private Change(ResourceKind resource) {
            this.resource = resource;
        }

        /** Returns the (possibly {@code null}) resource kind changed by this change. */
        public ResourceKind getResourceKind() {
            return this.resource;
        }

        private final ResourceKind resource;

        /** Returns the change type for a given resource kind, if any. */
        public static Change toChange(ResourceKind resource) {
            return resourceToChangeMap.get(resource);
        }

        private static Map<ResourceKind,Change> resourceToChangeMap =
            new EnumMap<ResourceKind,Change>(ResourceKind.class);

        static {
            for (Change change : EnumSet.allOf(Change.class)) {
                ResourceKind resource = change.getResourceKind();
                if (resource != null) {
                    resourceToChangeMap.put(resource, change);
                }
            }
        }
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