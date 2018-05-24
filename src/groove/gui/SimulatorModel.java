package groove.gui;

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
import java.util.TreeSet;

import groove.explore.Exploration;
import groove.explore.ExplorationListener;
import groove.explore.ExploreResult;
import groove.explore.ExploreType;
import groove.explore.util.StatisticsReporter;
import groove.grammar.Grammar;
import groove.grammar.GrammarProperties;
import groove.grammar.QualName;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.GraphBasedModel;
import groove.grammar.model.NamedResourceModel;
import groove.grammar.model.ResourceKind;
import groove.grammar.model.TextBasedModel;
import groove.grammar.type.TypeLabel;
import groove.graph.GraphInfo;
import groove.gui.display.DisplayKind;
import groove.gui.list.SearchResult;
import groove.io.store.SystemStore;
import groove.lts.GTS;
import groove.lts.GTSChangeListener;
import groove.lts.GTSCounter;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.MatchResult;
import groove.lts.RuleTransition;
import groove.util.Exceptions;
import groove.util.parse.FormatException;

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
    public void doDelete(ResourceKind resource, Set<QualName> names) throws IOException {
        start();
        try {
            boolean change = new HashSet<>(names).removeAll(getGrammar().getActiveNames(resource));
            switch (resource) {
            case CONTROL:
            case PROLOG:
            case GROOVY:
                getStore().deleteTexts(resource, names);
                break;
            case HOST:
            case RULE:
            case TYPE:
                getStore().deleteGraphs(resource, names);
                break;
            case PROPERTIES:
            default:
                assert false;
            }
            changeGrammar(change);
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
    public boolean doRename(ResourceKind resource, QualName oldName, QualName newName)
        throws IOException {
        boolean result = false;
        start();
        try {
            result = getGrammar().getActiveNames(resource)
                .contains(oldName);
            getStore().rename(resource, oldName, newName);
            if (resource == ResourceKind.RULE) {
                // rename rules in control programs
                Map<QualName,String> renamedControl = getGrammar().getControlModel()
                    .getLoader()
                    .rename(oldName, newName);
                if (!renamedControl.isEmpty()) {
                    getStore().putTexts(ResourceKind.CONTROL, renamedControl);
                }
            }
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
    public boolean doEnable(ResourceKind resource, Set<QualName> names) throws IOException {
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
    private void setEnabled(ResourceKind kind, Set<QualName> names) throws IOException {
        switch (kind) {
        case RULE:
            Collection<AspectGraph> newRules = new ArrayList<>(names.size());
            for (QualName ruleName : names) {
                AspectGraph oldRule = getStore().getGraphs(ResourceKind.RULE)
                    .get(ruleName);
                AspectGraph newRule = oldRule.clone();
                GraphInfo.setEnabled(newRule, !GraphInfo.isEnabled(oldRule));
                newRule.setFixed();
                newRules.add(newRule);
            }
            getStore().putGraphs(ResourceKind.RULE, newRules, false);
            break;
        case HOST:
        case TYPE:
        case PROLOG:
        case CONTROL:
            GrammarProperties newProperties = getGrammar().getProperties()
                .clone();
            Set<QualName> actives = new TreeSet<>(newProperties.getActiveNames(kind));
            for (QualName typeName : names) {
                if (!actives.remove(typeName)) {
                    actives.add(typeName);
                }
            }
            newProperties.setActiveNames(kind, actives);
            getStore().putProperties(newProperties);
            break;
        case PROPERTIES:
        default:
            throw Exceptions.UNREACHABLE;
        }
    }

    /**
     * Enables a resource of a given kind, and disables all others.
     * @param name the name of the resource to be enabled uniquely
     * @return {@code true} if the GTS was invalidated as a result of the action
     * @throws IOException if the action failed due to an IO error
     */
    public boolean doEnableUniquely(ResourceKind kind, QualName name) throws IOException {
        start();
        boolean result = true;
        try {
            setEnabledUniquely(kind, name);
            changeDisplay(DisplayKind.toDisplay(kind));
            changeGrammar(result);
        } finally {
            finish();
        }
        return result;
    }

    /** Uniquely enables a named resource of a given kind. */
    private void setEnabledUniquely(ResourceKind kind, QualName name) throws IOException {
        switch (kind) {
        case HOST:
        case TYPE:
        case PROLOG:
        case CONTROL:
            GrammarProperties newProperties = getGrammar().getProperties()
                .clone();
            newProperties.setActiveNames(kind, Collections.singleton(name));
            getStore().putProperties(newProperties);
            break;
        case PROPERTIES:
        default:
            throw Exceptions.UNREACHABLE;
        }
    }

    /**
     * Adds a given graph-based resource to this grammar
     * @param newGraph the new resource
     * @param layout flag indicating that this is a layout change only,
     * which should not result in a complete refresh
     * @return {@code true} if the GTS was invalidated as a result of the action
     * @throws IOException if the add action failed
     */
    public boolean doAddGraph(ResourceKind kind, AspectGraph newGraph, boolean layout)
        throws IOException {
        assert newGraph.isFixed();
        start();
        try {
            QualName name = newGraph.getQualName();
            boolean oldEnabled = isEnabled(kind, name);
            getStore().putGraphs(kind, Collections.singleton(newGraph), layout);
            if (layout) {
                return false;
            } else {
                boolean result = isEnabled(kind, name) || oldEnabled;
                changeGrammar(result);
                changeSelected(kind, name);
                changeDisplay(DisplayKind.toDisplay(kind));
                return result;
            }
        } finally {
            finish();
        }
    }

    /** Tests if a given aspect graph corresponds to an enabled resource. */
    private boolean isEnabled(ResourceKind kind, QualName name) {
        return getGrammar().getActiveNames(kind)
            .contains(name);
    }

    /**
     * Adds a text-based resource to this grammar.
     * @param name the name of the resource
     * @param program the resource text
     * @return {@code true} if the GTS was invalidated as a result of the action
     * @throws IOException if the add action failed
     */
    public boolean doAddText(ResourceKind kind, QualName name, String program) throws IOException {
        start();
        try {
            GrammarModel grammar = getGrammar();
            boolean result = grammar.getActiveNames(kind)
                .contains(name);
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
     * Sets the priority of a set of rules and recipes.
     * @param priorityMap mapping from rule names to their new priorities
     * @return {@code true} if the GTS was invalidated as a result of the action
     * @throws IOException if the action failed due to an IO error
     */
    public boolean doSetPriority(Map<QualName,Integer> priorityMap) throws IOException {
        start();
        Set<AspectGraph> newGraphs = new HashSet<>();
        for (Map.Entry<QualName,Integer> entry : priorityMap.entrySet()) {
            AspectGraph oldGraph = getStore().getGraphs(ResourceKind.RULE)
                .get(entry.getKey());
            AspectGraph newGraph = oldGraph.clone();
            GraphInfo.setPriority(newGraph, entry.getValue());
            newGraph.setFixed();
            newGraphs.add(newGraph);
        }
        Map<QualName,String> newControl = getGrammar().getControlModel()
            .getLoader()
            .changePriority(priorityMap);
        try {
            if (!newGraphs.isEmpty()) {
                getStore().putGraphs(ResourceKind.RULE, newGraphs, false);
            }
            if (!newControl.isEmpty()) {
                getStore().putTexts(ResourceKind.CONTROL, newControl);
            }
            changeGrammar(true);
            return true;
        } finally {
            finish();
        }
    }

    /**
     * Changes the default exploration in the system properties.
     * @param exploreType the new default exploration
     * @return {@code true} if the GTS was invalidated as a result of the action
     * @throws IOException if the action failed
     */
    public boolean doSetDefaultExploreType(ExploreType exploreType) throws IOException {
        GrammarProperties properties = getGrammar().getProperties();
        GrammarProperties newProperties = properties.clone();
        newProperties.setExploreType(exploreType);
        return doSetProperties(newProperties);
    }

    /**
     * Changes the system properties.
     * @param newProperties the properties to be saved
     * @return {@code true} if the GTS was invalidated as a result of the action
     * @throws IOException if the action failed
     */
    public boolean doSetProperties(GrammarProperties newProperties) throws IOException {
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
    public boolean doRelabel(TypeLabel oldLabel, TypeLabel newLabel) throws IOException {
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
            getStore().renumber();
            changeGrammar(true);
            return true;
        } finally {
            finish();
        }
    }

    /**
     * Sets the selected state and optionally the incoming transition through which
     * this state was reached, as well as a randomly selected outgoing match.
     * @param state the new selected state; non-{@code null}
     * @param trans if not {@code null}, a transition that should be inserted post-hoc into the
     * history as the one that was selected before this change
     * @return if {@code true}, the transition or state was really changed
     * @see #setMatch(GraphState,MatchResult)
     */
    public final boolean doSetStateAndMatch(GraphState state, GraphTransition trans) {
        assert state != null;
        start();
        if (trans != null) {
            assert state == trans.target();
            // fake the history: the previously selected match is supposed
            // to have been this transition already
            this.old.trans = trans;
        }
        changeGTS();
        changeState(state);
        MatchResult match = getMatch(state);
        changeMatch(match);
        changeTransition(
            match != null && match.hasTransitionFrom(state) ? match.getTransition() : null);
        if (getDisplay() != DisplayKind.LTS) {
            changeDisplay(DisplayKind.STATE);
        }
        return finish();
    }

    /**
     * Returns the first unexplored match of the state; if there is none,
     * returns the first outgoing transition that is not a self-loop,
     * preferably one that also leads to an open state.
     * Returns {@code null} if there is no such match or transition.
     */
    private MatchResult getMatch(GraphState state) {
        MatchResult result = state.getMatch();
        if (result == null) {
            for (RuleTransition trans : state.getRuleTransitions()) {
                if (trans.target() != state) {
                    result = trans.getKey();
                    if (!trans.target()
                        .isClosed()) {
                        break;
                    }
                }
            }
        }
        return result;
    }

    /** Indicates if there is an active GTS. */
    public final boolean hasGTS() {
        return getGTS() != null;
    }

    /** Returns the active GTS, if any. */
    public final GTS getGTS() {
        return this.gts;
    }

    /**
     * Creates a fresh GTS from the grammar model and sets it using {@link #setGTS(GTS)}.
     * If the currently stored grammar has errors, sets the GTS to {@code null} instead.
     * @return {@code true} if the stored GTS was changed as a result of this call
     * @see #setGTS(GTS)
     */
    public final boolean resetGTS() {
        try {
            Grammar grammar = getGrammar().toGrammar();
            GTS gts = new GTS(grammar);
            gts.getRecord()
                .setRandomAccess(true);
            return setGTS(gts);
        } catch (FormatException e) {
            return setGTS(null);
        }
    }

    /**
     * Changes the active GTS.
     * @see #setGTS(GTS)
     */
    private final boolean changeGTS(GTS gts) {
        boolean result = this.gts != gts;
        if (result) {
            if (this.gts != null) {
                this.gts.removeLTSListener(this.ltsListener);
            }
            if (gts != null) {
                gts.addLTSListener(this.ltsListener);
            }
            getGTSCounter().setGTS(gts);
            this.ltsListener.clear();
            this.gts = gts;
            this.changes.add(Change.GTS);
        }
        return result;
    }

    /**
     * Refreshes the GTS.
     */
    private final boolean changeGTS() {
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
    private boolean setGTS(GTS gts) {
        start();
        if (changeGTS(gts)) {
            changeState(null);
            changeMatch(null);
            changeTransition(null);
            changeExploreResult(gts == null ? null : new ExploreResult(gts));
            this.trace.clear();
        }
        if (gts != null && getState() == null) {
            changeState(gts.startState());
        }
        return finish();
    }

    /** Currently active GTS. */
    private GTS gts;

    /**
     * Sets the internally stored exploration result and notifies all listeners.
     * The new result should have the currently set GTS.
     */
    public void setExploreResult(ExploreResult result) {
        start();
        changeExploreResult(result);
        finish();
    }

    /**
     * Changes the internally stored exploration result.
     * The new result should have the currently set GTS.
     */
    private void changeExploreResult(ExploreResult result) {
        assert result == null ? this.gts == null : result.getGTS() == this.gts;
        this.exploreResult = result;
        changeGTS();
    }

    /**
     * Tests if the current exploration has a non-{@code null}, non-empty result.
     */
    public boolean hasExploreResult() {
        return getExploreResult() != null && !getExploreResult().isEmpty();
    }

    /**
     * Convenience method to return the last exploration result.
     */
    public ExploreResult getExploreResult() {
        return this.exploreResult;
    }

    private ExploreResult exploreResult;

    private final MyGTSListener ltsListener = new MyGTSListener();

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
     * @see #setMatch(GraphState,MatchResult)
     */
    public final boolean setState(GraphState state) {
        start();
        changeGTS();
        if (changeState(state)) {
            changeMatch(null);
            changeTransition(null);
            if (state != null && getDisplay() != DisplayKind.LTS) {
                changeDisplay(DisplayKind.STATE);
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
        boolean result = state != this.state || this.changes.contains(Change.GTS);
        if (result) {
            this.state = state;
            this.changes.add(Change.STATE);
        }
        return result;
    }

    /** Currently active state. */
    private GraphState state;

    /** Indicates if there is a currently selected match result. */
    public final boolean hasMatch() {
        return getMatch() != null;
    }

    /** Returns the currently selected match result. */
    public final MatchResult getMatch() {
        return this.match;
    }

    /**
     * Changes the selected state and rule match, and fires an update event.
     * If the match is changed to a non-null event, also sets the rule.
     * If the match is changed to a non-null transition from the selected state,
     * also sets the transition.
     * @param state the new selected state; if {@code null}, the selected state
     * is unchanged
     * @param match the new selected match; if {@code null}, the match is
     * deselected
     * @return if {@code true}, the match was really changed
     */
    public final boolean setMatch(GraphState state, MatchResult match) {
        start();
        boolean stateChanged = changeState(state);
        boolean matchChanged = changeMatch(match);
        if (matchChanged || stateChanged) {
            if (match != null && match.hasTransitionFrom(state)) {
                changeTransition(match.getTransition());
            } else {
                changeTransition(null);
            }
            if (getDisplay() != DisplayKind.LTS) {
                changeDisplay(DisplayKind.STATE);
            }
        }
        if (matchChanged) {
            changeSelected(ResourceKind.RULE, match == null ? null : match.getAction()
                .getQualName());
        }
        return finish();
    }

    /**
     * Changes the currently selected trace.
     * @param trace the new trace to be selected
     * @return if {@code true}, the trace was really changed
     */
    public final boolean setTrace(Collection<GraphTransition> trace) {
        start();
        boolean result = !this.trace.equals(trace);
        if (result) {
            this.trace.clear();
            this.trace.addAll(trace);
            this.changes.add(Change.TRACE);
        }
        finish();
        return result;
    }

    /** Returns the currently selected trace. */
    public final Set<GraphTransition> getTrace() {
        return Collections.unmodifiableSet(this.trace);
    }

    /** Currently selected trace (set of transitions). */
    private final Set<GraphTransition> trace = new HashSet<>();

    /** Indicates if there is currently a transition selected.
     */
    public final boolean hasTransition() {
        return getTransition() != null;
    }

    /** Returns the currently selected transition, if the selected match is
     * a transition.
     * @see #getMatch()
     */
    public final GraphTransition getTransition() {
        return this.trans;
    }

    /** Changes the currently selected transition.
     * @param trans the newly selected transition, or {@code null}
     * if there is now no transition selected
     */
    public final boolean setTransition(GraphTransition trans) {
        start();
        if (changeTransition(trans) && trans != null) {
            RuleTransition ruleTrans = trans.getInitial();
            MatchResult match = ruleTrans.getKey();
            changeMatch(match);
            changeSelected(ResourceKind.RULE, match.getAction()
                .getQualName());
            changeState(trans.source());
            if (getDisplay() != DisplayKind.LTS) {
                changeDisplay(DisplayKind.STATE);
            }
        }
        return finish();
    }

    /**
     * Changes the selected rule match.
     */
    private final boolean changeMatch(MatchResult match) {
        boolean result = match == null ? this.match != null : !match.equals(this.match);
        if (result) {
            this.match = match;
            this.changes.add(Change.MATCH);
        }
        return result;
    }

    /** Currently selected match (event or transition). */
    private MatchResult match;

    /**
     * Changes the selected transition.
     */
    private final boolean changeTransition(GraphTransition trans) {
        boolean result = trans == null ? this.trans != null : !trans.equals(this.trans);
        if (result) {
            this.trans = trans;
            this.changes.add(Change.MATCH);
        }
        return result;
    }

    /** Currently selected transition, if any. */
    private GraphTransition trans;

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
            changeGTS(null);
            changeState(null);
            changeMatch(null);
            changeTransition(null);
            resetExploreType();
            clearExplorationStats();
            for (ResourceKind resource : ResourceKind.all(false)) {
                changeSelected(resource, null);
            }
            try {
                setExploreType(getExploreType());
            } catch (FormatException e) {
                // do nothing
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
            changeGTS(null);
            changeState(null);
            changeMatch(null);
            changeTransition(null);
            resetExploreType();
        }
        // restrict the selected resources to those that are (still)
        // in the grammar
        for (ResourceKind resource : ResourceKind.all(false)) {
            Set<QualName> newNames = new LinkedHashSet<>();
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

    /** Currently loaded grammar. */
    private GrammarModel grammar;

    /** Convenience method to return the store of the currently loaded
     * grammar view, if any.
     */
    public final SystemStore getStore() {
        return hasGrammar() ? getGrammar().getStore() : null;
    }

    /**
     * Returns the selected resource of a given kind, or {@code null}
     * if no resource is selected.
     * Convenience method for {@code getGrammar().getResource(resource,getSelected(name))}.
     */
    public final NamedResourceModel<?> getResource(ResourceKind resource) {
        QualName name = getSelected(resource);
        return name == null ? null : getGrammar().getResource(resource, name);
    }

    /**
     * Returns the selected graph-based resource of a given kind, or {@code null}
     * if no resource is selected.
     * Convenience method for {@code getGrammar().getGraphResource(resource,getSelected(name))}.
     * @param resource the resource kind for which the resource is retrieved; must be graph-based
     */
    public final GraphBasedModel<?> getGraphResource(ResourceKind resource) {
        QualName name = getSelected(resource);
        return name == null ? null : getGrammar().getGraphResource(resource, name);
    }

    /**
     * Returns the selected resource of a given kind, or {@code null}
     * if no resource is selected.
     * Convenience method for {@code getGrammar().getTextResource(resource,getSelected(name))}.
     * @param resource the resource kind for which the resource is retrieved; must be text-based
     */
    public final TextBasedModel<?> getTextResource(ResourceKind resource) {
        QualName name = getSelected(resource);
        return name == null ? null : getGrammar().getTextResource(resource, name);
    }

    /** Returns a list of search results for the given label. */
    public final List<SearchResult> searchLabel(TypeLabel label) {
        List<SearchResult> searchResults = new ArrayList<>();
        for (ResourceKind kind : ResourceKind.values()) {
            if (!kind.isGraphBased()) {
                continue;
            }
            for (QualName name : getGrammar().getNames(kind)) {
                AspectGraph graph = getGrammar().getGraphResource(kind, name)
                    .getSource();
                graph.getSearchResults(label, searchResults);
            }
        }
        return searchResults;
    }

    /**
     * Checks for changes in the currently loaded grammar view,
     * and calls an update event if required.
     * Should be called after any change in the grammar view or
     * underlying store.
     */
    public final void synchronize(boolean reset) {
        start();
        changeGrammar(reset);
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
    public final QualName getSelected(ResourceKind kind) {
        Set<QualName> resourceSet = this.resources.get(kind);
        return resourceSet.isEmpty() ? null : resourceSet.iterator()
            .next();
    }

    /**
     * Returns set of names of the currently selected resources of a given kind.
     * @param kind the resource kind
     * @return the names of the currently selected resource
     */
    public final Set<QualName> getSelectSet(ResourceKind kind) {
        return this.resources.get(kind);
    }

    /** Changes the selection of a given resource kind. */
    public final boolean doSelect(ResourceKind kind, QualName name) {
        start();
        changeSelected(kind, name);
        if (isSelected(kind) || kind == ResourceKind.HOST && hasState()) {
            changeDisplay(DisplayKind.toDisplay(kind));
        }
        return finish();
    }

    /**
     * Changes the selection of a given resource kind.
     */
    public final boolean doSelectSet(ResourceKind kind, Collection<QualName> names) {
        start();
        changeSelectedSet(kind, names);
        return finish();
    }

    /**
     * Changes the selected value of a given resource kind.
     */
    private boolean changeSelected(ResourceKind kind, QualName name) {
        return changeSelectedSet(kind,
            name == null ? Collections.<QualName>emptySet() : Collections.singleton(name));
    }

    /**
     * Changes the currently selected resource set and records the change,
     * if the new resource set differs from the old.
     * @return {@code true} if a change was actually made
     */
    private final boolean changeSelectedSet(ResourceKind resource, Collection<QualName> names) {
        boolean result = false;
        Set<QualName> newSelection = new LinkedHashSet<>(names);
        Set<QualName> allNames = getGrammar().getNames(resource);
        // try to select a name
        if (newSelection.isEmpty() && getGrammar() != null) {
            QualName name = null;
            // find the best choice of name
            Set<QualName> activeNames = getGrammar().getActiveNames(resource);
            if (!activeNames.isEmpty()) {
                // take the first active name (if there is one)
                newSelection.add(activeNames.iterator()
                    .next());
            }
            if (newSelection.isEmpty() && !allNames.isEmpty()) {
                // otherwise, just take the first existing name (if there is one)
                name = allNames.iterator()
                    .next();
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

    /**
     * Returns the internally stored default exploration.
     */
    public ExploreType getExploreType() {
        return this.exploreType;
    }

    /**
     * Sets the internally stored exploration to a given value.
     * If the given exploration is incompatible with the grammar,
     * the grammar's default exploration is used instead.
     * @param exploreType non-{@code null} exploration
     * @throws FormatException if the new exploration is not
     * compatible with the existing grammar
     */
    public void setExploreType(ExploreType exploreType) throws FormatException {
        assert exploreType != null;
        if (hasGrammar() && !getGrammar().hasErrors()) {
            try {
                exploreType.test(getGrammar().toGrammar());
            } catch (FormatException exc) {
                resetExploreType();
                throw exc;
            }
        }
        changeExploreType(exploreType);
    }

    /**
     * Sets the internally stored exploration type to the default for the
     * grammar.
     * If there is currently no grammar or it has no default exploration,
     * sets to {@link ExploreType#DEFAULT}
     */
    private void resetExploreType() {
        ExploreType exploreType = null;
        if (hasGrammar()) {
            exploreType = getGrammar().getDefaultExploreType();
        } else {
            exploreType = ExploreType.DEFAULT;
        }
        changeExploreType(exploreType);
    }

    /**
     * Changes the exploration field, and adds the ltsListener.
     * @param exploreType the new (non-{@code null}) exploration type
     */
    private void changeExploreType(ExploreType exploreType) {
        assert exploreType != null;
        if (exploreType != this.exploreType) {
            this.exploreType = exploreType;
        }
    }

    /**
     * The default exploration to be performed. This value is either the
     * previous exploration, or the default constructor of the Exploration class
     * (=breadth first). This value may never be null (and must be initialized
     * explicitly).
     */
    private ExploreType exploreType = ExploreType.DEFAULT;

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
        if (display != this.display) {
            this.display = display;
            this.changes.add(Change.DISPLAY);
        }
        return result;
    }

    /** Display currently showing in the simulator panel. */
    private DisplayKind display = DisplayKind.HOST;

    /**
     * Returns the exploration statistics object associated with the current
     * GTS.
     */
    public StatisticsReporter getExplorationStats() {
        if (this.explorationStats == null) {
            this.explorationStats = new StatisticsReporter();
        }
        return this.explorationStats;
    }

    /**
     * Unlinks the exploration stats object to force it to be reconstructed.
     * This is done, for instance, when a new grammar is loaded, to avoid
     * having the stats from a previously loaded grammar lingering around.
     */
    public void clearExplorationStats() {
        this.explorationStats = null;
    }

    /** Statistics for the last exploration performed. */
    private StatisticsReporter explorationStats;

    @Override
    public String toString() {
        return "GuiState [gts=" + this.gts + ", state=" + this.state + ", match=" + this.match
            + ", grammar=" + this.grammar + ", resources=" + this.resources + ", changes="
            + this.changes + "]";
    }

    /** Returns a counter registering all kinds of counts for the currently loaded GTS. */
    public GTSCounter getGTSCounter() {
        return this.gtsCounter;
    }

    private final GTSCounter gtsCounter = new GTSCounter();

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
            result.resources = new EnumMap<>(ResourceKind.class);
            for (ResourceKind resource : ResourceKind.all(false)) {
                result.resources.put(resource, new LinkedHashSet<>(this.resources.get(resource)));
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
        Set<SimulatorListener> notified = new HashSet<>();
        for (Change change : this.changes) {
            for (SimulatorListener listener : new ArrayList<>(this.listeners.get(change))) {
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
    /** Mapping from resource kinds to sets of selected resources of that kind. */
    private Map<ResourceKind,Set<QualName>> resources = new EnumMap<>(ResourceKind.class);

    {
        for (ResourceKind resource : ResourceKind.all(false)) {
            this.resources.put(resource, Collections.<QualName>emptySet());
        }
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
            changes = Change.values();
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
            changes = Change.values();
        }
        for (Change change : changes) {
            this.listeners.get(change)
                .remove(listener);
        }
    }

    /** Array of listeners. */
    private final Map<Change,List<SimulatorListener>> listeners = new EnumMap<>(Change.class);

    { // initialise the listener map to empty listener lists
        for (Change change : Change.values()) {
            this.listeners.put(change, new ArrayList<SimulatorListener>());
        }
    }

    private static class MyGTSListener extends GTSChangeListener implements ExplorationListener {
        @Override
        public void start(Exploration exploration, GTS gts) {
            setChanged();
        }

        @Override
        public void stop(GTS gts) {
            // do nothing
        }

        @Override
        public void abort(GTS gts) {
            // do nothing
        }
    }

    /** Change type. */
    public static enum Change {
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
         * @see SimulatorModel#getGTS()
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
         * The selected trace in the LTS has changed.
         */
        TRACE,
        /**
         * The selected prolog program has changed.
         */
        PROLOG(ResourceKind.PROLOG),
        /**
         * The selected config file has changed.
         */
        CONFIG(ResourceKind.CONFIG),
        /**
         * The selected Groovy script has changed.
         */
        GROOVY(ResourceKind.GROOVY),
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
            new EnumMap<>(ResourceKind.class);

        static {
            for (Change change : Change.values()) {
                ResourceKind resource = change.getResourceKind();
                if (resource != null) {
                    resourceToChangeMap.put(resource, change);
                }
            }
        }
    }

}
