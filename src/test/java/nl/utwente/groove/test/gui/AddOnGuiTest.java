/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2026
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */
package nl.utwente.groove.test.gui;

import static nl.utwente.groove.test.gui.SimulatorFixture.frame;
import static nl.utwente.groove.test.gui.SimulatorFixture.simulator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JMenu;
import javax.swing.SwingUtilities;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JFileChooserOperator;
import org.netbeans.jemmy.operators.JMenuBarOperator;

import nl.utwente.groove.gui.BackendChooser;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.view.GraphBackend;
import nl.utwente.groove.test.GuiTest;
import nl.utwente.groove.test.QuietLogging;
import nl.utwente.groove.test.SlowTest;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.AddOn;
import nl.utwente.groove.util.Extensions;
import nl.utwente.groove.util.Version;

/**
 * Drives the yFiles add-on submenu of the View menu: installs a stand-in add-on
 * zip through "Install from file..." and removes it again through "Remove", checking
 * the extension directory after each. In between, the graph backend submenu is
 * checked: it appears with the installation, marks the JGraph backend as the one in
 * use and the add-on's as the one selected for the next start, and follows the
 * choices made through it. The extension directory of the test JVM is
 * the empty one under {@code target} that the test configuration points to.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
@Tag(SlowTest.TAG)
@Tag(GuiTest.TAG)
@ExtendWith(SimulatorFixture.class)
public class AddOnGuiTest {
    @RegisterExtension
    static final QuietLogging QUIET_LOGGING = new QuietLogging();

    @Test
    void installsFromFileAndRemoves(@TempDir Path tmp) throws Exception {
        Path ext = Extensions.dir();
        AddOn addOn = AddOn.YFILES;
        addOn.uninstall(ext);
        Options.userPrefs.remove(Options.GRAPH_BACKEND_OPTION);
        Path zip = writeAddOnZip(tmp.resolve(addOn.getZipName(Version.NUMBER)));
        try {
            // only the JGraph backend is available, so there is nothing to choose
            assertNull(createChooserMenu());

            new JMenuBarOperator(frame())
                .pushMenuNoBlock(Options.DISPLAY_MENU_NAME + "|" + Options.YFILES_ADDON_MENU_NAME
                    + "|" + Options.INSTALL_ADDON_FILE_ACTION_NAME);
            new JFileChooserOperator().chooseFile(zip.toAbsolutePath().toString());
            JDialogOperator installed = new JDialogOperator(addOn.getDisplayName() + " installed");
            new JButtonOperator(installed, "OK").push();
            installed.waitClosed();
            assertTrue(addOn.isPresent(ext));
            assertTrue(Files.isRegularFile(addOn.getDir(ext).resolve("groove-yfiles.jar")));
            assertTrue(Files.isRegularFile(addOn.getDir(ext).resolve(addOn.getNoticeName())));

            // the installation selects the add-on's backend for the next start
            assertEquals(addOn.getName(), Options.userPrefs.get(Options.GRAPH_BACKEND_OPTION, null));
            String jgraph = GraphBackend.instance().getDisplayName();
            String yfiles = addOn.getDisplayName();
            assertChooserShows(jgraph + BackendChooser.THIS_SESSION_SUFFIX, false,
                               yfiles + BackendChooser.AFTER_RESTART_SUFFIX, true);
            // choosing the backend in use cancels the switch, without a dialog
            new JMenuBarOperator(frame())
                .pushMenu(Options.DISPLAY_MENU_NAME + "|" + Options.GRAPH_BACKEND_OPTION + "|"
                    + jgraph);
            assertEquals(GraphBackend.JGRAPH,
                         Options.userPrefs.get(Options.GRAPH_BACKEND_OPTION, null));
            assertChooserShows(jgraph, true, yfiles, false);
            // choosing the other backend again announces the switch
            new JMenuBarOperator(frame())
                .pushMenuNoBlock(Options.DISPLAY_MENU_NAME + "|" + Options.GRAPH_BACKEND_OPTION
                    + "|" + yfiles);
            JDialogOperator changed = new JDialogOperator(BackendChooser.CHANGED_TITLE);
            new JButtonOperator(changed, "OK").push();
            changed.waitClosed();
            assertEquals(addOn.getName(), Options.userPrefs.get(Options.GRAPH_BACKEND_OPTION, null));
            assertChooserShows(jgraph + BackendChooser.THIS_SESSION_SUFFIX, false,
                               yfiles + BackendChooser.AFTER_RESTART_SUFFIX, true);

            new JMenuBarOperator(frame())
                .pushMenuNoBlock(Options.DISPLAY_MENU_NAME + "|" + Options.YFILES_ADDON_MENU_NAME
                    + "|" + Options.REMOVE_ADDON_ACTION_NAME);
            JDialogOperator confirm = new JDialogOperator("Remove " + addOn.getDisplayName() + "?");
            new JButtonOperator(confirm, "Yes").push();
            JDialogOperator removed = new JDialogOperator(addOn.getDisplayName() + " removed");
            new JButtonOperator(removed, "OK").push();
            removed.waitClosed();
            assertFalse(addOn.isPresent(ext));
            // the choice is left as it is, but there is nothing to choose any more
            assertNull(createChooserMenu());
        } finally {
            addOn.uninstall(ext);
            Options.userPrefs.remove(Options.GRAPH_BACKEND_OPTION);
        }
    }

    /** Creates the backend chooser menu on the event thread, as the View menu does. */
    private static @Nullable JMenu createChooserMenu() throws Exception {
        var result = new AtomicReference<@Nullable JMenu>();
        SwingUtilities
            .invokeAndWait(() -> result
                .set(new BackendChooser(simulator().getFrame(), AddOn.YFILES).createMenu()));
        return result.get();
    }

    /** Asserts that the chooser menu shows two items with given texts and selection states. */
    private static void assertChooserShows(String text1, boolean selected1, String text2,
                                           boolean selected2) throws Exception {
        JMenu menu = createChooserMenu();
        assertNotNull(menu);
        assertEquals(2, menu.getItemCount());
        assertEquals(text1, menu.getItem(0).getText());
        assertEquals(selected1, menu.getItem(0).isSelected());
        assertEquals(text2, menu.getItem(1).getText());
        assertEquals(selected2, menu.getItem(1).isSelected());
    }

    /** Writes a stand-in add-on zip: a jar for the running version and a notice. */
    private static Path writeAddOnZip(Path path) throws IOException {
        var manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().put(Extensions.VERSION_ATTRIBUTE, Version.NUMBER);
        var jar = new ByteArrayOutputStream();
        try (var out = new JarOutputStream(jar, manifest)) {
            // only the manifest
        }
        try (var zip = new ZipOutputStream(Files.newOutputStream(path))) {
            zip.putNextEntry(new ZipEntry("yfiles/groove-yfiles.jar"));
            zip.write(jar.toByteArray());
            zip.closeEntry();
            zip.putNextEntry(new ZipEntry("yfiles/" + AddOn.YFILES.getNoticeName()));
            zip.write("notice".getBytes());
            zip.closeEntry();
        }
        return path;
    }
}
