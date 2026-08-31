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
package nl.utwente.groove.test.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.undo.UndoableEdit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nl.utwente.groove.grammar.Semantics;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.io.store.EditType;
import nl.utwente.groove.io.store.SystemStore;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.io.FileUtils;

/**
 * Tests for the mutating side of {@link SystemStore}: putting, deleting and
 * renaming resources, property changes, relabelling, saving, the undoable
 * edits these operations post, and the various store construction paths
 * (fresh, bare, zipped, by URL). The read-only loading side is exercised
 * throughout the suite already; the edit/undo machinery was previously only
 * reachable through the GUI.
 * All tests work on a copy of the fixture grammar in a temporary directory,
 * so the fixture itself is never modified.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
@NonNullByDefault
public class SystemStoreTest {
    /** Location of the fixture grammar. */
    private static final String GRAMMAR_DIR = "junit/samples/mc.gps";

    /** Copies the fixture grammar into the temporary directory and opens
     * the copy as a loaded, writable store, with an edit listener that
     * records all posted edits in {@link #edits}. */
    private SystemStore openCopy(Path tmp) throws IOException {
        File dir = tmp.resolve("mc.gps").toFile();
        FileUtils.copyDirectory(new File(GRAMMAR_DIR), dir, true);
        SystemStore result = SystemStore.newStore(dir, false, true);
        result.addEditListener(this.edits::add);
        return result;
    }

    /** The edits posted by the store under test, in order. */
    private final List<SystemStore.Edit> edits = new ArrayList<>();

    /** Returns the last posted edit, which must exist. */
    private SystemStore.Edit lastEdit() {
        assertFalse(this.edits.isEmpty());
        return this.edits.get(this.edits.size() - 1);
    }

    /** Constructor and factory error paths: missing file, plain file,
     * wrong extension, and creating a zipped grammar. */
    @Test
    public void testConstructionErrors(@TempDir Path tmp) throws IOException {
        assertThrows(IOException.class,
                     () -> new SystemStore(tmp.resolve("absent.gps").toFile(), false));
        File plainFile = tmp.resolve("plain.gps").toFile();
        assertTrue(plainFile.createNewFile());
        assertThrows(IOException.class, () -> new SystemStore(plainFile, false));
        File wrongExt = tmp.resolve("noext").toFile();
        assertTrue(wrongExt.mkdir());
        assertThrows(IOException.class, () -> new SystemStore(wrongExt, true));
        assertThrows(IOException.class,
                     () -> SystemStore.newStore(tmp.resolve("fresh.zip").toFile(), true, false));
    }

    /** A freshly created store carries version properties (so is not empty);
     * a bare directory without a properties file is empty and pins the
     * pre-properties simple-graph semantics. */
    @Test
    public void testFreshAndBareStore(@TempDir Path tmp) throws IOException {
        File freshDir = tmp.resolve("fresh.gps").toFile();
        SystemStore fresh = SystemStore.newStore(freshDir, true, true);
        assertEquals("fresh", fresh.getName());
        assertEquals(freshDir, fresh.getLocation());
        assertFalse(fresh.isEmpty());
        assertTrue(fresh.toString().contains("fresh"));
        // location determines equality
        SystemStore other = SystemStore.newStore(freshDir, false, false);
        assertEquals(fresh, other);
        assertEquals(fresh.hashCode(), other.hashCode());
        assertFalse(fresh.equals(null));
        // the grammar model is created once and then cached
        assertSame(fresh.toGrammarModel(), fresh.toGrammarModel());

        File bareDir = tmp.resolve("bare.gps").toFile();
        assertTrue(bareDir.mkdir());
        SystemStore bare = SystemStore.newStore(bareDir, false, true);
        assertTrue(bare.isEmpty());
        assertEquals(Semantics.SPO_SIMPLE, bare.getProperties().getSemantics());
    }

    /** Resource access requires the store to have been loaded. */
    @Test
    public void testInitGuard(@TempDir Path tmp) throws IOException {
        SystemStore store = SystemStore.newStore(new File(GRAMMAR_DIR), false, false);
        assertThrows(IllegalStateException.class, () -> store.getGraphs(ResourceKind.HOST));
        // unused; keeps the temp-dir parameter pattern uniform
        assertTrue(Files.isDirectory(tmp));
    }

    /** Put, modify, rename, delete of graph-based resources, including
     * subdirectory creation and cleanup for qualified names, and the
     * undo/redo round trip of the posted edits. */
    @Test
    public void testGraphEdits(@TempDir Path tmp) throws IOException {
        SystemStore store = openCopy(tmp);
        AspectGraph start = store.getGraphs(ResourceKind.HOST).get(QualName.name("start"));
        assert start != null;
        // create under a qualified name: the subdirectory is created on demand
        QualName subName = QualName.name("sub", "copy");
        AspectGraph copy = start.rename(subName);
        assertTrue(store.putGraphs(ResourceKind.HOST, List.of(copy), false).isEmpty());
        assertEquals(EditType.CREATE, lastEdit().getType());
        File subFile = tmp.resolve("mc.gps").resolve("sub").resolve("copy.gst").toFile();
        assertTrue(subFile.isFile());
        // putting the same name again is a modification, and returns the old graph
        assertEquals(1, store.putGraphs(ResourceKind.HOST, List.of(copy), false).size());
        assertEquals(EditType.MODIFY, lastEdit().getType());
        // a layout-only put is propagated as such
        store.putGraphs(ResourceKind.HOST, List.of(copy), true);
        assertEquals(EditType.LAYOUT, lastEdit().getType());
        // rename moves the file and the map entry
        QualName newName = QualName.name("sub2", "copy2");
        store.rename(ResourceKind.HOST, subName, newName);
        assertEquals(EditType.RENAME, lastEdit().getType());
        assertFalse(subFile.exists());
        File newFile = tmp.resolve("mc.gps").resolve("sub2").resolve("copy2.gst").toFile();
        assertTrue(newFile.isFile());
        assertFalse(store.getGraphs(ResourceKind.HOST).containsKey(subName));
        assertTrue(store.getGraphs(ResourceKind.HOST).containsKey(newName));
        // delete removes the file and cleans up the now-empty subdirectory
        assertEquals(1, store.deleteGraphs(ResourceKind.HOST, List.of(newName)).size());
        assertEquals(EditType.DELETE, lastEdit().getType());
        assertFalse(newFile.exists());
        assertFalse(tmp.resolve("mc.gps").resolve("sub2").toFile().exists());
        // undo the deletion, then redo it
        UndoableEdit deleteEdit = (UndoableEdit) lastEdit();
        deleteEdit.undo();
        assertTrue(store.getGraphs(ResourceKind.HOST).containsKey(newName));
        assertTrue(newFile.isFile());
        deleteEdit.redo();
        assertFalse(store.getGraphs(ResourceKind.HOST).containsKey(newName));
        assertFalse(newFile.exists());
    }

    /** Put, modify, rename, delete of text-based resources, and the
     * undo/redo round trip of the posted edits. */
    @Test
    public void testTextEdits(@TempDir Path tmp) throws IOException {
        SystemStore store = openCopy(tmp);
        QualName name = QualName.name("ctrl");
        assertNotNull(store.putTexts(ResourceKind.CONTROL, Map.of(name, "p;")));
        assertEquals(EditType.CREATE, lastEdit().getType());
        File file = tmp.resolve("mc.gps").resolve("ctrl.gcp").toFile();
        assertTrue(file.isFile());
        assertEquals("p;", store.getTexts(ResourceKind.CONTROL).get(name));
        // modification returns the old text
        Map<QualName,String> old = store.putTexts(ResourceKind.CONTROL, Map.of(name, "q;"));
        assert old != null;
        assertEquals("p;", old.get(name));
        assertEquals(EditType.MODIFY, lastEdit().getType());
        // rename moves the file and the map entry
        QualName newName = QualName.name("ctrl2");
        store.rename(ResourceKind.CONTROL, name, newName);
        assertEquals(EditType.RENAME, lastEdit().getType());
        assertFalse(file.exists());
        assertEquals("q;", store.getTexts(ResourceKind.CONTROL).get(newName));
        // delete returns the old text
        old = store.deleteTexts(ResourceKind.CONTROL, List.of(newName));
        assert old != null;
        assertEquals("q;", old.get(newName));
        assertEquals(EditType.DELETE, lastEdit().getType());
        // undo the deletion, then redo it
        UndoableEdit deleteEdit = (UndoableEdit) lastEdit();
        deleteEdit.undo();
        assertEquals("q;", store.getTexts(ResourceKind.CONTROL).get(newName));
        deleteEdit.redo();
        assertFalse(store.getTexts(ResourceKind.CONTROL).containsKey(newName));
    }

    /** While undo registration is suspended, edits are applied but not posted. */
    @Test
    public void testUndoSuspended(@TempDir Path tmp) throws IOException {
        SystemStore store = openCopy(tmp);
        assertFalse(store.isUndoSuspended());
        store.setUndoSuspended(true);
        store.putTexts(ResourceKind.CONTROL, Map.of(QualName.name("ctrl"), "p;"));
        assertTrue(this.edits.isEmpty());
        assertTrue(store.getTexts(ResourceKind.CONTROL).containsKey(QualName.name("ctrl")));
        store.setUndoSuspended(false);
    }

    /** Property changes: a display-only key changes just the properties,
     * a reload key (the algebra family) triggers a full reload and an edit
     * spanning all resource kinds; both undo back to the original value. */
    @Test
    public void testPutProperties(@TempDir Path tmp) throws IOException {
        SystemStore store = openCopy(tmp);
        var props = store.getProperties().clone();
        boolean oldLoops = props.isShowLoopsAsLabels();
        props.setShowLoopsAsLabels(!oldLoops);
        store.putProperties(props);
        assertEquals(!oldLoops, store.getProperties().isShowLoopsAsLabels());
        assertEquals(EditType.MODIFY, lastEdit().getType());
        assertEquals(EnumSet.of(ResourceKind.PROPERTIES), lastEdit().getChange());
        // the stored-node-ids key is a reload key: the resulting edit spans
        // all resource kinds
        props = store.getProperties().clone();
        boolean oldIds = props.isUseStoredNodeIds();
        props.setUseStoredNodeIds(!oldIds);
        store.putProperties(props);
        assertEquals(!oldIds, store.getProperties().isUseStoredNodeIds());
        UndoableEdit reloadEdit = (UndoableEdit) lastEdit();
        assertEquals(EnumSet.allOf(ResourceKind.class), lastEdit().getChange());
        reloadEdit.undo();
        assertEquals(oldIds, store.getProperties().isUseStoredNodeIds());
        reloadEdit.redo();
        assertEquals(!oldIds, store.getProperties().isUseStoredNodeIds());
    }

    /** Relabelling changes all resources using the label, posts a compound
     * edit, and undoes back to the original label. */
    @Test
    public void testRelabel(@TempDir Path tmp) throws IOException {
        SystemStore store = openCopy(tmp);
        // the start-with-final fixture graph contains an empty-labelled
        // self-loop, which AspectGraph.relabel rejects by assertion even
        // though the loader accepts it; keep it out of the relabelling
        store.deleteGraphs(ResourceKind.HOST, List.of(QualName.name("start-with-final")));
        Path ruleFile = tmp.resolve("mc.gps").resolve("p.gpr");
        assertTrue(Files.readString(ruleFile).contains("<string>p</string>"));
        store.relabel(TypeLabel.createLabel("p"), TypeLabel.createLabel("pp"));
        SystemStore.Edit edit = lastEdit();
        assertTrue(edit.getChange().contains(ResourceKind.RULE));
        assertTrue(Files.readString(ruleFile).contains("<string>pp</string>"));
        // relabelling a label that occurs nowhere changes no file (an edit is
        // still posted, since even an empty per-kind sub-edit carries its
        // resource kind in its change set)
        store.relabel(TypeLabel.createLabel("absent"), TypeLabel.createLabel("gone"));
        assertTrue(Files.readString(ruleFile).contains("<string>pp</string>"));
        // the compound edit undoes and redoes as a whole
        UndoableEdit undoable = (UndoableEdit) edit;
        assertTrue(undoable.getUndoPresentationName().contains(EditType.REPLACE_ACTION_NAME));
        undoable.undo();
        assertTrue(Files.readString(ruleFile).contains("<string>p</string>"));
        undoable.redo();
        assertTrue(Files.readString(ruleFile).contains("<string>pp</string>"));
    }

    /** Node renumbering runs over all graph-based resources. Node numbers are
     * dispensed globally across the loaded graphs, so renumbering each graph
     * from 0 is a real change here: it posts a single compound edit and keeps
     * the resource names intact. */
    @Test
    public void testRenumber(@TempDir Path tmp) throws IOException {
        SystemStore store = openCopy(tmp);
        var oldNames = Set.copyOf(store.getGraphs(ResourceKind.RULE).keySet());
        store.renumber();
        assertEquals(1, this.edits.size());
        UndoableEdit edit = (UndoableEdit) lastEdit();
        assertTrue(edit.getPresentationName().contains(EditType.RENUMBER_ACTION_NAME));
        assertEquals(oldNames, store.getGraphs(ResourceKind.RULE).keySet());
    }

    /** Saving to a new location, over an existing store (both keeping and
     * clearing the target), and the invalid-extension error path. */
    @Test
    public void testSave(@TempDir Path tmp) throws IOException {
        SystemStore store = openCopy(tmp);
        File target = tmp.resolve("saved.gps").toFile();
        SystemStore saved = store.save(target, true);
        assertEquals(store.getGraphs(ResourceKind.RULE).keySet(),
                     saved.getGraphs(ResourceKind.RULE).keySet());
        assertEquals(store.getGraphs(ResourceKind.HOST).keySet(),
                     saved.getGraphs(ResourceKind.HOST).keySet());
        // saving onto the existing target exercises the backup-and-restore
        // logic; the backup directory is cleaned up on success
        for (boolean clearDir : new boolean[] {true, false}) {
            SystemStore resaved = store.save(target, clearDir);
            assertEquals(store.getGraphs(ResourceKind.RULE).keySet(),
                         resaved.getGraphs(ResourceKind.RULE).keySet());
            assertFalse(tmp.resolve("Copy of saved.gps").toFile().exists());
        }
        assertThrows(IOException.class, () -> store.save(tmp.resolve("saved.txt").toFile(), true));
    }

    /** Loading a zipped grammar, a grammar URL, and a string location. */
    @Test
    public void testZipAndUrlStores(@TempDir Path tmp) throws IOException {
        // build a zip whose single root entry is the grammar directory
        File zip = tmp.resolve("mczip.zip").toFile();
        File src = new File(GRAMMAR_DIR);
        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zip))) {
            // the extractor does not create parent directories on its own,
            // so the grammar directory needs an explicit entry
            out.putNextEntry(new ZipEntry("mczip.gps/"));
            out.closeEntry();
            File[] files = src.listFiles();
            assert files != null;
            for (File file : files) {
                out.putNextEntry(new ZipEntry("mczip.gps/" + file.getName()));
                Files.copy(file.toPath(), out);
                out.closeEntry();
            }
        }
        SystemStore zipStore = SystemStore.newStore(zip, false, true);
        assertEquals("mczip", zipStore.getName());
        assertEquals(2, zipStore.getGraphs(ResourceKind.HOST).size());
        assertEquals(3, zipStore.getGraphs(ResourceKind.RULE).size());
        // a zip whose root holds more than one entry is rejected
        File badZip = tmp.resolve("bad.zip").toFile();
        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(badZip))) {
            for (String entry : new String[] {"one.gps/system.properties", "two.txt"}) {
                out.putNextEntry(new ZipEntry(entry));
                out.closeEntry();
            }
        }
        assertThrows(IOException.class, () -> SystemStore.newStore(badZip, false, true));
        // URL and string-location loading of the plain grammar directory
        assertNotNull(SystemStore.newGrammar(FileUtils.toURL(new File(GRAMMAR_DIR))));
        assertNotNull(SystemStore.newGrammar(new File(GRAMMAR_DIR)));
        try {
            assertEquals("mc", SystemStore.newStore(GRAMMAR_DIR).getName());
        } catch (java.net.URISyntaxException exc) {
            throw new IOException(exc);
        }
    }
}
