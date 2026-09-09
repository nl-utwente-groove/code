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
package nl.utwente.groove.gui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.gui.dialog.ErrorDialog;
import nl.utwente.groove.gui.dialog.ProgressBarDialog;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.AddOn;
import nl.utwente.groove.util.AddOn.Status;
import nl.utwente.groove.util.Extensions;
import nl.utwente.groove.util.Version;

/**
 * The Simulator's front for installing an {@link AddOn}: a menu with the download,
 * the installation from a file and the removal, and the one-time question at the
 * first start of a GROOVE version whose add-on is not installed.
 * <p>
 * The question is asked at most once per GROOVE version, recorded in the user
 * preferences, and never for a development version (which has no release to
 * download from) or when the system property {@link #PROMPT_PROPERTY} is
 * {@code false}, as it is for the tests. Since backends are selected at start-up
 * (see {@link nl.utwente.groove.gui.view.GraphBackend#instance()}), an installed
 * add-on is used from the next start on, which the messages say.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class AddOnInstaller {
    /** Constructs an installer for a given add-on, with the Simulator frame as parent of its dialogs. */
    public AddOnInstaller(JFrame frame, AddOn addOn) {
        this.frame = frame;
        this.addOn = addOn;
    }

    private final JFrame frame;
    private final AddOn addOn;

    /**
     * Creates the menu of this installer, for the options part of the View menu. The items are refreshed
     * whenever the menu is opened, since the installer changes what they act on.
     */
    public JMenu createMenu() {
        JMenu result = new JMenu(Options.YFILES_ADDON_MENU_NAME);
        JMenuItem downloadItem = new JMenuItem(Options.DOWNLOAD_ADDON_ACTION_NAME);
        downloadItem.addActionListener(e -> download());
        JMenuItem fileItem = new JMenuItem(Options.INSTALL_ADDON_FILE_ACTION_NAME);
        fileItem.addActionListener(e -> installFromFile());
        JMenuItem removeItem = new JMenuItem(Options.REMOVE_ADDON_ACTION_NAME);
        removeItem.addActionListener(e -> remove());
        result.add(downloadItem);
        result.add(fileItem);
        result.add(removeItem);
        result.addMenuListener(new MenuListener() {
            // the listener's parameters are unconstrained, hence nullable here
            @Override
            public void menuSelected(@Nullable MenuEvent e) {
                Status status = getStatus();
                downloadItem
                    .setText(status == Status.STALE
                        ? Options.UPDATE_ADDON_ACTION_NAME
                        : Options.DOWNLOAD_ADDON_ACTION_NAME);
                downloadItem.setToolTipText("From " + AddOnInstaller.this.addOn.getDownloadUri(Version.NUMBER));
                removeItem.setEnabled(AddOnInstaller.this.addOn.isPresent(Extensions.dir()));
                removeItem.setToolTipText(describeStatus(status));
            }

            @Override
            public void menuDeselected(@Nullable MenuEvent e) {
                // nothing to do
            }

            @Override
            public void menuCanceled(@Nullable MenuEvent e) {
                // nothing to do
            }
        });
        return result;
    }

    /**
     * Asks once per GROOVE version whether to install the add-on, if it is not
     * installed for this version, and starts the download if the answer is yes.
     * To be called after the frame is visible.
     */
    public void promptOnFirstRun() {
        if (!Boolean.parseBoolean(System.getProperty(PROMPT_PROPERTY, "true"))) {
            return;
        }
        if (Version.isDevelopmentVersion()) {
            return;
        }
        Status status = getStatus();
        if (status == Status.INSTALLED) {
            return;
        }
        if (Version.NUMBER.equals(Options.userPrefs.get(Options.YFILES_ADDON_ASKED_OPTION, null))) {
            return;
        }
        Options.userPrefs.put(Options.YFILES_ADDON_ASKED_OPTION, Version.NUMBER);
        if (confirmInstall(status)) {
            download();
        }
    }

    /** Asks whether the add-on should be downloaded and installed, showing the license restriction. */
    private boolean confirmInstall(Status status) {
        String situation = status == Status.STALE
            ? "The installed " + this.addOn.getDisplayName()
                + " add-on was built for another GROOVE version and is not loaded."
            : "GROOVE can show graphs with the commercial library yFiles for Java (Swing)"
                + " by yWorks GmbH, which adds its layout algorithms to the layout menu.";
        String message = "<html><body style='width: 480px'>" + situation + "<br><br>The "
            + this.addOn.getDisplayName() + " comes as an add-on of about 9 MB, downloaded from<br><i>"
            + this.addOn.getDownloadUri(Version.NUMBER) + "</i><br>and installed in<br><i>"
            + this.addOn.getDir(Extensions.dir())
            + "</i><br><br>The library is licensed to the University of Twente for"
            + " <b>non-commercial use only</b> (research, teaching and study), and GROOVE with the"
            + " add-on installed may be used for such purposes only. The library may not be extracted"
            + " from the add-on, de-obfuscated or reverse engineered. If in doubt, do not install"
            + " it; the full notice comes with the add-on.<br><br>Install the "
            + this.addOn.getDisplayName() + " now? (The choice stays available under "
            + Options.DISPLAY_MENU_NAME + " &gt; " + Options.YFILES_ADDON_MENU_NAME
            + ".)</body></html>";
        int answer = JOptionPane
            .showConfirmDialog(this.frame, message, "Install " + this.addOn.getDisplayName() + "?",
                               JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return answer == JOptionPane.YES_OPTION;
    }

    /**
     * Downloads the add-on for the running GROOVE version and installs it, in the
     * background, with a progress dialog; reports the outcome in a dialog.
     */
    public void download() {
        String version = Version.NUMBER;
        ProgressBarDialog progress
            = new ProgressBarDialog(this.frame, "Downloading " + this.addOn.getDisplayName());
        progress.setMessage("Downloading " + this.addOn.getZipName(version) + "...");
        progress.activate(0);
        new Download(version, progress).execute();
    }

    /**
     * Background download and installation of the add-on for a GROOVE version,
     * reporting its progress in a dialog and its outcome afterwards.
     * The worker's inherited methods are unconstrained, hence the default is off.
     */
    @NonNullByDefault({})
    private class Download extends SwingWorker<Path,Long> {
        Download(String version, ProgressBarDialog progress) {
            this.version = version;
            this.progress = progress;
        }

        private final String version;
        private final ProgressBarDialog progress;
        private long size = -1;

        @Override
        protected Path doInBackground() throws IOException {
            AddOn addOn = AddOnInstaller.this.addOn;
            this.size = addOn.getSize(this.version);
            Path zip = addOn.download(this.version, this::publish);
            try {
                return addOn.install(zip, Extensions.dir());
            } finally {
                Files.deleteIfExists(zip);
            }
        }

        @Override
        protected void process(List<Long> chunks) {
            long received = chunks.get(chunks.size() - 1);
            if (this.size > 0) {
                this.progress.setRange(0, (int) (this.size / 1024));
                this.progress.setProgress((int) (received / 1024));
            }
            this.progress.setMessage("Downloaded " + received / 1024 + " kB");
        }

        @Override
        protected void done() {
            this.progress.deactivate();
            this.progress.dispose();
            try {
                reportInstalled(get());
            } catch (InterruptedException exc) {
                Thread.currentThread().interrupt();
            } catch (ExecutionException exc) {
                Throwable cause = exc.getCause();
                reportError("Installation of the " + AddOnInstaller.this.addOn.getDisplayName()
                    + " failed", cause == null
                        ? exc
                        : cause);
            }
        }
    }

    /** Installs the add-on from a zip file chosen by the user; reports the outcome in a dialog. */
    public void installFromFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Install " + this.addOn.getDisplayName() + " from file");
        chooser
            .setFileFilter(new FileNameExtensionFilter(this.addOn.getDisplayName() + " add-on ("
                + this.addOn.getZipName(Version.NUMBER) + ")", "zip"));
        if (chooser.showOpenDialog(this.frame) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        if (file == null) {
            return;
        }
        try {
            reportInstalled(this.addOn.install(file.toPath(), Extensions.dir()));
        } catch (IOException exc) {
            reportError("Installation of the " + this.addOn.getDisplayName() + " failed", exc);
        }
    }

    /** Removes the installed add-on after confirmation; reports the outcome in a dialog. */
    public void remove() {
        Path dir = this.addOn.getDir(Extensions.dir());
        int answer = JOptionPane
            .showConfirmDialog(this.frame,
                               "Remove the " + this.addOn.getDisplayName() + " by deleting " + dir + "?",
                               "Remove " + this.addOn.getDisplayName() + "?", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            if (this.addOn.uninstall(Extensions.dir())) {
                JOptionPane
                    .showMessageDialog(this.frame,
                                       "The " + this.addOn.getDisplayName()
                                           + " is removed; the change takes effect at the next start of GROOVE.",
                                       this.addOn.getDisplayName() + " removed",
                                       JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException exc) {
            reportError("Removal of the " + this.addOn.getDisplayName() + " failed", exc);
        }
    }

    private void reportInstalled(Path dir) {
        String message = "<html><body style='width: 400px'>The " + this.addOn.getDisplayName()
            + " is installed in<br><i>" + dir + "</i><br>and is used from the next start of GROOVE on."
            + "<br><br>Its use is restricted to non-commercial purposes; see <i>"
            + this.addOn.getNoticeName() + "</i> in that directory.</body></html>";
        JOptionPane
            .showMessageDialog(this.frame, message, this.addOn.getDisplayName() + " installed",
                               JOptionPane.INFORMATION_MESSAGE);
    }

    private void reportError(String message, Throwable cause) {
        String detail = cause.getMessage();
        new ErrorDialog(this.frame, detail == null
            ? message
            : message + ": " + detail, cause).setVisible(true);
    }

    private Status getStatus() {
        return this.addOn.getStatus(Extensions.instance());
    }

    private String describeStatus(Status status) {
        return switch (status) {
        case INSTALLED -> "Installed in " + this.addOn.getDir(Extensions.dir());
        case STALE -> "Installed in " + this.addOn.getDir(Extensions.dir())
            + ", but built for another GROOVE version";
        case ABSENT -> "Not installed";
        };
    }

    /**
     * System property that suppresses the first-run question when set to {@code false}.
     * The test configurations set it, so that the question cannot block a test.
     */
    public static final String PROMPT_PROPERTY = "groove.addon.prompt";
}
