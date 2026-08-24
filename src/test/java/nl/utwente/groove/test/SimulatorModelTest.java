/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2026 University of Twente
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
package nl.utwente.groove.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nl.utwente.groove.explore.Exploration;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.gui.SimulatorListener;
import nl.utwente.groove.gui.SimulatorModel;
import nl.utwente.groove.gui.SimulatorModel.Change;
import nl.utwente.groove.gui.display.DisplayKind;
import nl.utwente.groove.io.store.SystemStore;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.RuleTransition;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.io.FileUtils;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Smoke test for the headless use of {@link SimulatorModel}: the model can be
 * constructed without a Simulator (only a display-option selector is needed)
 * and its resource-editing layer can be driven programmatically, with the
 * listener notifications observable through the ordinary listener interface.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
@NonNullByDefault
public class SimulatorModelTest {
    /** Location of the sample grammar copied for each test. */
    static private final String FIXTURE = "junit/samples/control.gps";

    /** Name of the control program created by the test. */
    static private final QualName NEW_CTRL = QualName.parse("headlessCtrl");
    /** Name of a rule present in the fixture. */
    static private final QualName MOVE = QualName.parse("move");
    /** New name for the {@link #MOVE} rule. */
    static private final QualName MOVED = QualName.parse("moved");

    /**
     * Constructs a model headlessly, loads a grammar into it, and drives a
     * resource addition, rename and deletion through the {@code do*} layer,
     * checking the resulting grammar content, selection state and listener
     * notifications at every step.
     */
    @Test
    public void testHeadlessGrammarEditing(@TempDir Path tmp) throws IOException {
        SimulatorModel model = new SimulatorModel(option -> false);
        List<Set<Change>> updates = new ArrayList<>();
        model.addListener((source, oldModel, changes) -> updates.add(EnumSet.copyOf(changes)));

        // load the grammar
        File grammarDir = tmp.resolve("control.gps").toFile();
        FileUtils.copyDirectory(new File(FIXTURE), grammarDir, false);
        var store = new SystemStore(grammarDir, false);
        store.reload();
        model.setGrammar(store);
        assertTrue(model.hasGrammar());
        assertEquals(1, updates.size());
        assertTrue(updates.get(0).contains(Change.GRAMMAR));

        // add a control program
        model.doAddText(ResourceKind.CONTROL, NEW_CTRL, "any;");
        assertTrue(model.getGrammar().getNames(ResourceKind.CONTROL).contains(NEW_CTRL));
        assertEquals(NEW_CTRL, model.getSelected(ResourceKind.CONTROL));
        assertEquals(DisplayKind.CONTROL, model.getDisplay());
        assertEquals(2, updates.size());
        assertTrue(updates.get(1).containsAll(Set.of(Change.GRAMMAR, Change.CONTROL)));

        // rename a rule
        model.doRename(ResourceKind.RULE, MOVE, MOVED);
        var ruleNames = model.getGrammar().getNames(ResourceKind.RULE);
        assertTrue(ruleNames.contains(MOVED));
        assertFalse(ruleNames.contains(MOVE));
        assertEquals(3, updates.size());
        assertTrue(updates.get(2).contains(Change.GRAMMAR));

        // delete the control program again; the selection must move
        // to a remaining resource (the model auto-selects on an empty selection)
        model.doDelete(ResourceKind.CONTROL, Set.of(NEW_CTRL));
        assertFalse(model.getGrammar().getNames(ResourceKind.CONTROL).contains(NEW_CTRL));
        assertNotEquals(NEW_CTRL, model.getSelected(ResourceKind.CONTROL));
        assertTrue(model
            .getGrammar()
            .getNames(ResourceKind.CONTROL)
            .contains(model.getSelected(ResourceKind.CONTROL)));
        assertEquals(4, updates.size());
        assertTrue(updates.get(3).contains(Change.GRAMMAR));
    }

    /** Location of the grammar used for the GTS-layer test (loaded read-only). */
    static private final String GTS_FIXTURE = "junit/samples/ferryman.gps";
    /** Known state space size of {@link #GTS_FIXTURE}. */
    static private final int FERRYMAN_STATES = 114;
    /** Known transition count of {@link #GTS_FIXTURE}. */
    static private final int FERRYMAN_TRANSITIONS = 198;

    /**
     * Drives the GTS/state/match selection layer headlessly: creates a GTS,
     * explores it, and selects transitions, states and matches, checking the
     * selection invariants (transition selection implies match, rule and source
     * state selection; state selection clears the match; rule selection clears
     * the match) and the listener contract (per-change registration, at most
     * one notification per transaction, deregistration).
     */
    @Test
    public void testGtsStateAndMatchSelection() throws IOException, FormatException {
        SimulatorModel model = new SimulatorModel(option -> false);
        var store = new SystemStore(new File(GTS_FIXTURE), false);
        store.reload();
        model.setGrammar(store);

        // register one listener for all changes and one for state changes only
        List<Set<Change>> updates = new ArrayList<>();
        model.addListener((source, oldModel, changes) -> updates.add(EnumSet.copyOf(changes)));
        List<Set<Change>> stateUpdates = new ArrayList<>();
        model
            .addListener((source, oldModel,
                          changes) -> stateUpdates.add(EnumSet.copyOf(changes)),
                         Change.STATE);

        // creating the GTS selects its start state
        assertTrue(model.resetGTS());
        GTS gts = model.getGTS();
        assertNotNull(gts);
        assertEquals(gts.startState(), model.getState());
        assertEquals(1, updates.size());
        assertTrue(updates.get(0).containsAll(Set.of(Change.GTS, Change.STATE)));
        assertEquals(1, stateUpdates.size());

        // explore fully (headlessly) and publish the result, as ExploreAction does
        var exploration = new Exploration(model.getExploreType(), model.getState());
        exploration.play();
        model.setExploreResult(exploration.getResult(), model.getExploreType());
        assertEquals(FERRYMAN_STATES, gts.nodeCount());
        assertEquals(FERRYMAN_TRANSITIONS, gts.edgeCount());
        assertEquals(2, updates.size());
        assertTrue(updates.get(1).contains(Change.GTS));

        // selecting a transition selects its match, rule and source state
        RuleTransition trans = gts
            .startState()
            .getRuleTransitions()
            .stream()
            .filter(t -> t.target() != t.source())
            .findFirst()
            .orElseThrow();
        assertTrue(model.setTransition(trans));
        assertEquals(trans, model.getTransition());
        assertEquals(trans.getKey(), model.getMatch());
        assertEquals(trans.source(), model.getState());
        assertEquals(trans.getAction().getQualName(), model.getSelected(ResourceKind.RULE));
        // the all-changes listener was notified exactly once for this
        // transaction, even though it is registered for every fired change
        assertEquals(3, updates.size());
        assertTrue(updates.get(2).containsAll(Set.of(Change.MATCH, Change.RULE)));
        // the state did not change (the transition leaves the selected state),
        // so the state-only listener was not notified
        assertEquals(1, stateUpdates.size());

        // selecting the target state clears match and transition
        assertTrue(model.setState(trans.target()));
        assertEquals(trans.target(), model.getState());
        assertFalse(model.hasMatch());
        assertFalse(model.hasTransition());
        assertEquals(4, updates.size());
        assertTrue(updates.get(3).contains(Change.STATE));
        assertEquals(2, stateUpdates.size());

        // doSetStateAndMatch auto-selects an outgoing match of the new state
        assertTrue(model.doSetStateAndMatch(gts.startState(), null));
        assertEquals(gts.startState(), model.getState());
        assertTrue(model.hasMatch());
        assertEquals(5, updates.size());

        // selecting another rule clears the match
        QualName selectedRule = model.getSelected(ResourceKind.RULE);
        QualName otherRule = model
            .getGrammar()
            .getNames(ResourceKind.RULE)
            .stream()
            .filter(n -> !n.equals(selectedRule))
            .findFirst()
            .orElseThrow();
        assertTrue(model.doSelect(ResourceKind.RULE, otherRule));
        assertEquals(otherRule, model.getSelected(ResourceKind.RULE));
        assertFalse(model.hasMatch());
        assertEquals(6, updates.size());
        assertTrue(updates.get(5).containsAll(Set.of(Change.RULE, Change.MATCH)));

        // after deregistration, listeners are no longer notified
        SimulatorListener late = (source, oldModel, changes) -> updates.add(EnumSet.copyOf(changes));
        model.addListener(late);
        model.removeListener(late);
        assertTrue(model.setState(trans.target()));
        assertEquals(7, updates.size());
    }
}
