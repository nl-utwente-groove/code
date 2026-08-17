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

import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.gui.SimulatorModel;
import nl.utwente.groove.gui.SimulatorModel.Change;
import nl.utwente.groove.gui.display.DisplayKind;
import nl.utwente.groove.io.store.SystemStore;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.io.FileUtils;

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
}
