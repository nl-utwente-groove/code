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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JFileChooserOperator;
import org.netbeans.jemmy.operators.JMenuBarOperator;

import nl.utwente.groove.gui.Options;
import nl.utwente.groove.test.GuiTest;
import nl.utwente.groove.test.SlowTest;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.AddOn;
import nl.utwente.groove.util.Extensions;
import nl.utwente.groove.util.Version;

/**
 * Drives the yFiles add-on submenu of the View menu: installs a stand-in add-on
 * zip through "Install from file..." and removes it again through "Remove", checking
 * the extension directory after each. The extension directory of the test JVM is
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
    @Test
    void installsFromFileAndRemoves(@TempDir Path tmp) throws Exception {
        Path ext = Extensions.dir();
        AddOn addOn = AddOn.YFILES;
        addOn.uninstall(ext);
        Path zip = writeAddOnZip(tmp.resolve(addOn.getZipName(Version.NUMBER)));
        try {
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

            new JMenuBarOperator(frame())
                .pushMenuNoBlock(Options.DISPLAY_MENU_NAME + "|" + Options.YFILES_ADDON_MENU_NAME
                    + "|" + Options.REMOVE_ADDON_ACTION_NAME);
            JDialogOperator confirm = new JDialogOperator("Remove " + addOn.getDisplayName() + "?");
            new JButtonOperator(confirm, "Yes").push();
            JDialogOperator removed = new JDialogOperator(addOn.getDisplayName() + " removed");
            new JButtonOperator(removed, "OK").push();
            removed.waitClosed();
            assertFalse(addOn.isPresent(ext));
        } finally {
            addOn.uninstall(ext);
        }
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
