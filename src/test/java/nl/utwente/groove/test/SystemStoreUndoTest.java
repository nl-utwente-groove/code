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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nl.utwente.groove.grammar.GrammarKey;
import nl.utwente.groove.grammar.GrammarProperties;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.io.FileUtils;
import nl.utwente.groove.io.store.SystemStore;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;

/**
 * Smoke test for the undo channel of {@link SystemStore}: the edit-listener
 * contract that the GUI's {@code SimulatorUndoManager} relies on for its undo
 * history (see gh #887, phase 4).
 * <p>
 * The test bridges the edits posted by the store into a plain Swing
 * {@link UndoManager} in the same way as the GUI does, performs one store
 * mutation of every basic shape (text create, graph create, rename, delete,
 * properties change), and then walks the full undo and redo chain, checking
 * the store content against a snapshot at every step. It also pins the two
 * documented boundary semantics: a {@link SystemStore#reload()} reaches the
 * observers but never the undo channel, and a suspended store
 * ({@link SystemStore#setUndoSuspended(boolean)}) still mutates but notifies
 * neither channel.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
@NonNullByDefault
public class SystemStoreUndoTest {
    /** Location of the sample grammar copied for each test. */
    static private final String FIXTURE = "junit/samples/control.gps";

    /** Name of the control program created by the tests. */
    static private final QualName NEW_CTRL = QualName.parse("undoCtrl");
    /** Name of a host graph present in the fixture. */
    static private final QualName START = QualName.parse("start");
    /** Name of the host-graph copy created by the tests. */
    static private final QualName START_COPY = QualName.parse("startCopy");
    /** Name of a rule present in the fixture. */
    static private final QualName MOVE = QualName.parse("move");
    /** New name for the {@link #MOVE} rule. */
    static private final QualName MOVED = QualName.parse("moved");

    /**
     * Performs one edit of every basic shape, then undoes and redoes the
     * full chain, checking the store content at every step, on disk as well
     * as in memory. Also checks that the undo and redo traversals themselves
     * do not repost edits.
     */
    @Test
    public void testUndoRedoRoundTrip(@TempDir Path tmp) throws IOException {
        var fixture = new Fixture(tmp);
        var store = fixture.store;
        List<Snapshot> snapshots = new ArrayList<>();
        snapshots.add(Snapshot.take(store));

        // create a control program (text-based CREATE)
        store.putTexts(ResourceKind.CONTROL, Map.of(NEW_CTRL, "any;"));
        snapshots.add(Snapshot.take(store));

        // create a host graph, as a copy of the start graph (graph-based CREATE)
        var start = Objects.requireNonNull(store.getGraphs(ResourceKind.HOST).get(START));
        store.putGraphs(ResourceKind.HOST, List.of(start.rename(START_COPY)), false);
        snapshots.add(Snapshot.take(store));

        // rename a rule (graph-based RENAME)
        store.rename(ResourceKind.RULE, MOVE, MOVED);
        snapshots.add(Snapshot.take(store));

        // delete the host-graph copy again (graph-based DELETE)
        store.deleteGraphs(ResourceKind.HOST, List.of(START_COPY));
        snapshots.add(Snapshot.take(store));

        // change a property that does not require a grammar reload (MODIFY)
        var newProps = store.getProperties().clone();
        newProps.setProperty(GrammarKey.REMARK, "undo smoke test");
        store.putProperties(newProps);
        snapshots.add(Snapshot.take(store));

        int editCount = snapshots.size() - 1;
        assertEquals(editCount, fixture.postedEdits.size());

        // undo all the way back, checking every intermediate state
        for (int i = editCount; i > 0; i--) {
            assertTrue(fixture.undoHistory.canUndo());
            fixture.undoHistory.undo();
            snapshots.get(i - 1).assertState(store);
        }
        assertFalse(fixture.undoHistory.canUndo());
        snapshots.get(0).assertDiskState(store);

        // redo all the way forward again
        for (int i = 1; i <= editCount; i++) {
            assertTrue(fixture.undoHistory.canRedo());
            fixture.undoHistory.redo();
            snapshots.get(i).assertState(store);
        }
        assertFalse(fixture.undoHistory.canRedo());
        snapshots.get(editCount).assertDiskState(store);

        // undo and redo run through the do* methods, which must not repost
        assertEquals(editCount, fixture.postedEdits.size());
    }

    /**
     * A reload notifies the observers but must not reach the undo channel:
     * it would otherwise become an undoable edit.
     */
    @Test
    public void testReloadBypassesUndoChannel(@TempDir Path tmp) throws IOException {
        var fixture = new Fixture(tmp);
        // the initial reload in the fixture did not reach the edit listener
        assertTrue(fixture.postedEdits.isEmpty());
        assertFalse(fixture.undoHistory.canUndo());
        List<Object> observed = new ArrayList<>();
        fixture.store.addObserver(observed::add);
        fixture.store.reload();
        assertEquals(1, observed.size());
        assertTrue(fixture.postedEdits.isEmpty());
        assertFalse(fixture.undoHistory.canUndo());
    }

    /**
     * A suspended store still performs the mutation, but notifies neither
     * the edit listeners nor the observers.
     */
    @Test
    public void testUndoSuspendedSuppressesBothChannels(@TempDir Path tmp) throws IOException {
        var fixture = new Fixture(tmp);
        List<Object> observed = new ArrayList<>();
        fixture.store.addObserver(observed::add);
        fixture.store.setUndoSuspended(true);
        try {
            fixture.store.putTexts(ResourceKind.CONTROL, Map.of(NEW_CTRL, "any;"));
        } finally {
            fixture.store.setUndoSuspended(false);
        }
        // the store content did change ...
        assertEquals("any;", fixture.store.getTexts(ResourceKind.CONTROL).get(NEW_CTRL));
        // ... but neither channel was notified
        assertTrue(fixture.postedEdits.isEmpty());
        assertTrue(observed.isEmpty());
        assertFalse(fixture.undoHistory.canUndo());
    }

    /** A store loaded from a scratch copy of the fixture grammar, with the
     * posted edits bridged into an undo history in the same way as the GUI's
     * {@code SimulatorUndoManager} does. */
    static private class Fixture {
        Fixture(Path dir) throws IOException {
            File grammarDir = dir.resolve("control.gps").toFile();
            FileUtils.copyDirectory(new File(FIXTURE), grammarDir, false);
            this.store = new SystemStore(grammarDir, false);
            this.store.addEditListener(this::editPosted);
            this.store.reload();
        }

        /** Mirrors the GUI bridge: posted edits double as
         * {@link UndoableEdit}s and go into the undo history. */
        private void editPosted(SystemStore.Edit edit) {
            this.postedEdits.add(edit);
            if (edit instanceof UndoableEdit undoable) {
                this.undoHistory.addEdit(undoable);
            }
        }

        /** The store under test. */
        final SystemStore store;
        /** All edits posted to the edit listener, in order. */
        final List<SystemStore.Edit> postedEdits = new ArrayList<>();
        /** Undo history fed from the edit listener. */
        final UndoManager undoHistory = new UndoManager();
    }

    /** Snapshot of the content of a store, for comparison after undo/redo.
     * The graph maps are compared by identity of the graphs: undo and redo
     * must restore the very instances that the edits retained. */
    static private record Snapshot(Map<ResourceKind,Map<QualName,AspectGraph>> graphs,
        Map<ResourceKind,Map<QualName,String>> texts, GrammarProperties properties) {

        /** Captures the current content of a store. */
        static Snapshot take(SystemStore store) {
            Map<ResourceKind,Map<QualName,AspectGraph>> graphs = new EnumMap<>(ResourceKind.class);
            Map<ResourceKind,Map<QualName,String>> texts = new EnumMap<>(ResourceKind.class);
            for (var kind : ResourceKind.values()) {
                if (kind.isGraphBased()) {
                    graphs.put(kind, new LinkedHashMap<>(store.getGraphs(kind)));
                } else if (kind.isTextBased()) {
                    texts.put(kind, new LinkedHashMap<>(store.getTexts(kind)));
                }
            }
            return new Snapshot(graphs, texts, store.getProperties().clone());
        }

        /** Asserts that the in-memory content of the given store equals this snapshot. */
        void assertState(SystemStore store) {
            this.graphs.forEach((kind, expected) -> assertEquals(expected, store.getGraphs(kind)));
            this.texts.forEach((kind, expected) -> assertEquals(expected, store.getTexts(kind)));
            assertProperties(this.properties, store.getProperties());
        }

        /** Asserts that the on-disk content at the given store's location
         * equals this snapshot, by loading it into a fresh store. Graphs are
         * compared by name only, as loading creates fresh instances. */
        void assertDiskState(SystemStore store) throws IOException {
            var fresh = new SystemStore(store.getLocation(), false);
            fresh.reload();
            this.graphs
                .forEach((kind,
                          expected) -> assertEquals(expected.keySet(),
                                                    fresh.getGraphs(kind).keySet()));
            this.texts.forEach((kind, expected) -> assertEquals(expected, fresh.getTexts(kind)));
            assertProperties(this.properties, fresh.getProperties());
        }

        /** Asserts that two properties maps coincide on the effective value
         * of every non-system key. ({@link GrammarProperties} itself has no
         * content-based equals.) System keys are owned by the save machinery
         * and outside the undo contract: every save re-stamps the version
         * keys to the current version, so undo does not restore the version
         * stamp of a fixture created by an older GROOVE release. */
        static private void assertProperties(GrammarProperties expected,
                                             GrammarProperties actual) {
            for (var key : GrammarKey.values()) {
                if (!key.isSystem()) {
                    assertEquals(effectiveValue(expected, key), effectiveValue(actual, key),
                                 key.getName());
                }
            }
        }

        /** Returns the stored value for a key, normalised to {@code null}
         * if the value is absent or parses to the key's default. */
        static private @Nullable String effectiveValue(GrammarProperties props, GrammarKey key) {
            String value = props.getProperty(key);
            return value != null && key.parser().parsesToDefault(value)
                ? null
                : value;
        }
    }
}
