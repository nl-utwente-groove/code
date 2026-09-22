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

import java.awt.Desktop;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.gui.dialog.ErrorDialog;
import nl.utwente.groove.gui.dialog.ProgressBarDialog;
import nl.utwente.groove.gui.dialog.SwingExtensionFilter;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.AddOn;
import nl.utwente.groove.util.AddOn.Outcome;
import nl.utwente.groove.util.AddOn.Pending;
import nl.utwente.groove.util.AddOn.Status;
import nl.utwente.groove.util.Extensions;
import nl.utwente.groove.util.FileType;
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
     * Creates the menu of this installer, for the options part of the View menu. The items
     * are put in whenever the menu is opened, since the installer changes what applies.
     * The first item is a disabled line stating the status of the add-on, so that the
     * actions below it can be read: an add-on that is installed can only be removed, one
     * with a pending removal only reactivated, one that is absent only installed; only a
     * stale add-on can be both updated and removed. A development version has no release
     * to download from, so its menu offers only the installation from a file.
     */
    public JMenu createMenu() {
        JMenu result = new JMenu(Options.YFILES_ADDON_MENU_NAME);
        JMenuItem statusItem = new JMenuItem();
        statusItem.setEnabled(false);
        JMenuItem downloadItem = new JMenuItem(Options.DOWNLOAD_ADDON_ACTION_NAME);
        downloadItem.addActionListener(e -> download());
        downloadItem.setToolTipText("From " + this.addOn.getDownloadUri(Version.NUMBER));
        JMenuItem fileItem = new JMenuItem(Options.INSTALL_ADDON_FILE_ACTION_NAME);
        fileItem.addActionListener(e -> installFromFile());
        JMenuItem removeItem = new JMenuItem(Options.REMOVE_ADDON_ACTION_NAME);
        removeItem.addActionListener(e -> remove());
        JMenuItem reactivateItem = new JMenuItem(Options.REACTIVATE_ADDON_ACTION_NAME);
        reactivateItem.addActionListener(e -> reactivate());
        result.addMenuListener(new MenuListener() {
            // the listener's parameters are unconstrained, hence nullable here
            @Override
            public void menuSelected(@Nullable MenuEvent e) {
                result.removeAll();
                Path ext = Extensions.dir();
                AddOn addOn = AddOnInstaller.this.addOn;
                Status status = getStatus();
                boolean present = addOn.isPresent(ext);
                statusItem.setText(describeStatus(status, present));
                statusItem
                    .setToolTipText(present
                        ? "In " + addOn.getDir(ext)
                        : null);
                result.add(statusItem);
                result.addSeparator();
                if (addOn.getPending(ext) == Pending.REMOVE) {
                    result.add(reactivateItem);
                    return;
                }
                if (status == Status.STALE || !present) {
                    if (!Version.isDevelopmentVersion()) {
                        downloadItem
                            .setText(status == Status.STALE
                                ? Options.UPDATE_ADDON_ACTION_NAME
                                : Options.DOWNLOAD_ADDON_ACTION_NAME);
                        result.add(downloadItem);
                    }
                    result.add(fileItem);
                }
                if (present) {
                    result.add(removeItem);
                }
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
        if (status == Status.INSTALLED
            || this.addOn.getPending(Extensions.dir()) == Pending.INSTALL) {
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
        String name = this.addOn.getDisplayName();
        String situation = status == Status.STALE
            ? STALE_SITUATION.formatted(name)
            : ABSENT_SITUATION;
        Path dir = this.addOn.getDir(Extensions.dir());
        String message = INSTALL_QUESTION
            .formatted(situation, name, this.addOn.getDownloadUri(Version.NUMBER), dir,
                       Options.DISPLAY_MENU_NAME, Options.YFILES_ADDON_MENU_NAME, dir.toUri());
        int answer = JOptionPane
            .showConfirmDialog(this.frame, createMessagePane(message), "Install " + name + "?",
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

    /** Installs the add-on from a zip file chosen by the user; reports the outcome in a dialog. */
    public void installFromFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Install " + this.addOn.getDisplayName() + " from file");
        chooser.setFileFilter(SwingExtensionFilter.getFilter(FileType.ZIP));
        chooser
            .setSelectedFile(new File(chooser.getCurrentDirectory(),
                this.addOn.getZipName(Version.NUMBER)));
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
                               "Remove the " + this.addOn.getDisplayName() + " by deleting " + dir
                                   + "?",
                               "Remove " + this.addOn.getDisplayName() + "?",
                               JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }
        String name = this.addOn.getDisplayName();
        try {
            String message = switch (this.addOn.uninstall(Extensions.dir())) {
            case DONE -> "The " + name
                + " is removed; the change takes effect at the next start of GROOVE.";
            case DEFERRED -> "The files of the " + name
                + " are in use, by this or another running GROOVE, and cannot be deleted now;"
                + " the " + name + " is removed at the next start of GROOVE."
                + " Until then, it can be reactivated from the menu.";
            };
            JOptionPane
                .showMessageDialog(this.frame, message, name + " removed",
                                   JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException exc) {
            reportError("Removal of the " + name + " failed", exc);
        }
    }

    /** Cancels a pending removal of the add-on; reports the outcome in a dialog. */
    public void reactivate() {
        String name = this.addOn.getDisplayName();
        try {
            this.addOn.reactivate(Extensions.dir());
            JOptionPane
                .showMessageDialog(this.frame, "The " + name + " stays installed.",
                                   name + " reactivated", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException exc) {
            reportError("Reactivation of the " + name + " failed", exc);
        }
    }

    /**
     * Reports a successful installation, having selected the add-on's backend for the
     * next start: installing the add-on is what a user does to use it, whatever an
     * earlier choice was.
     */
    private void reportInstalled(Outcome outcome) {
        Options.userPrefs.put(Options.GRAPH_BACKEND_OPTION, this.addOn.getName());
        String name = this.addOn.getDisplayName();
        Path ext = Extensions.dir();
        String message = switch (outcome) {
        case DONE -> INSTALLED_REPORT
            .formatted(name, this.addOn.getDir(ext).toUri(), this.addOn.getNoticeName(),
                       Options.DISPLAY_MENU_NAME, Options.GRAPH_BACKEND_OPTION);
        case DEFERRED -> DEFERRED_REPORT
            .formatted(name, Extensions.pendingInstallDir(ext, this.addOn.getName()).toUri(),
                       this.addOn.getNoticeName(), Options.DISPLAY_MENU_NAME,
                       Options.GRAPH_BACKEND_OPTION);
        };
        JOptionPane
            .showMessageDialog(this.frame, createMessagePane(message), name + " installed",
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

    /**
     * Describes the status of the add-on in this run, and what changes at the next start,
     * in one line for the status item of the menu.
     * @param status the status in the scan of this run
     * @param present whether the add-on has files in the extension directory, which it
     * may have without being loaded in this run, e.g. when installed during the run
     */
    private String describeStatus(Status status, boolean present) {
        String result = switch (status) {
        case INSTALLED -> "Installed and loaded";
        case STALE -> "Installed for GROOVE "
            + String.join(", ", this.addOn.getVersions(Extensions.instance())) + ", not loaded";
        case ABSENT -> present
            ? "Installed, not loaded in this run"
            : "Not installed";
        };
        return switch (this.addOn.getPending(Extensions.dir())) {
        case NONE -> result;
        case INSTALL -> result + "; new version installed at the next start";
        case REMOVE -> result + "; removed at the next start unless reactivated";
        };
    }

    /**
     * Creates a read-only pane showing an HTML message in the option pane's font, with
     * clickable links. {@link JOptionPane} gets the pane rather than the string, since it
     * would show a string's links as inert text and break the string into separate labels
     * at every line break, of which only the first is rendered as HTML.
     * Hovering over a link shows its target as a tooltip, since the link text does not.
     */
    private static JEditorPane createMessagePane(String html) {
        JEditorPane result = new JEditorPane("text/html", html);
        result.setEditable(false);
        result.setOpaque(false);
        result.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        Font font = UIManager.getFont("OptionPane.messageFont");
        if (font != null) {
            result.setFont(font);
        }
        result.addHyperlinkListener(e -> {
            var type = e.getEventType();
            if (type == HyperlinkEvent.EventType.ACTIVATED) {
                followLink(e.getDescription());
            } else if (type == HyperlinkEvent.EventType.ENTERED) {
                result.setToolTipText(describeLink(e.getDescription()));
            } else if (type == HyperlinkEvent.EventType.EXITED) {
                result.setToolTipText(null);
            }
        });
        return result;
    }

    /**
     * Returns the target of a link as a tooltip text: a local directory or file as a
     * path, any other target as it is written in the link.
     */
    private static @Nullable String describeLink(@Nullable String href) {
        if (href == null) {
            return null;
        }
        try {
            URI uri = new URI(href);
            if ("file".equals(uri.getScheme())) {
                return Path.of(uri).toString();
            }
        } catch (URISyntaxException | RuntimeException exc) {
            // fall through to the raw link
        }
        return href;
    }

    /**
     * Opens the target of a link in a message: a web address in the browser, and a local
     * directory in the file manager, or its nearest existing ancestor if it does not exist,
     * as the installation directory does not before the add-on is installed. Failures are
     * ignored, since the link text shows the target anyway.
     */
    private static void followLink(@Nullable String href) {
        if (href == null || !Desktop.isDesktopSupported()) {
            return;
        }
        try {
            URI uri = new URI(href);
            if ("file".equals(uri.getScheme())) {
                @Nullable
                Path path = Path.of(uri);
                while (path != null && !Files.exists(path)) {
                    path = path.getParent();
                }
                if (path != null) {
                    Desktop.getDesktop().open(path.toFile());
                }
            } else {
                Desktop.getDesktop().browse(uri);
            }
        } catch (IOException | URISyntaxException | RuntimeException exc) {
            // nothing to be done; the user can still copy the target from the link text
        }
    }

    /**
     * Opening of the installation question if an add-on for another version is installed;
     * the parameter is the add-on's display name.
     */
    private static final String STALE_SITUATION
        = "The currently installed %s add-on was built for another GROOVE version and is not loaded.";
    /** Opening of the installation question if no add-on is installed. */
    private static final String ABSENT_SITUATION
        = """
            GROOVE can optionally show and edit graphs using the commercial library "yFiles for Java (Swing)"
            by yWorks GmbH, with better rendering and layouting.
            """;
    /**
     * HTML template of the installation question. The parameters are, in order: the
     * opening situation, the add-on's display name, its download URI, its installation
     * directory, the names of the menu and submenu where the choice stays available, and
     * the installation directory as a URI, for its link. Line breaks in the template are
     * white space to the HTML pane of {@link #createMessagePane}.
     */
    private static final String INSTALL_QUESTION = """
        <html><body style='width: 400px'>%1$s<br><br>
        The %2$s comes as an add-on of about 9 MB, downloaded from
        <a href="%3$s">github</a>
        and added to
        <a href="%7$s">GROOVE's extension folder</a>.<br><br>
        The library is licensed to the University of Twente for <i>non-commercial use</i>
        (research, teaching and study), hence GROOVE with the add-on installed may be used
        for such purposes only. The library may not be extracted from the add-on,
        de-obfuscated or reverse engineered. If in doubt, do not install it.<br><br>
        Install the %2$s now? (The choice stays available under %5$s &gt; %6$s.)
        </body></html>
        """;
    /**
     * HTML template of the report of a successful installation. The parameters are, in
     * order: the add-on's display name, its installation directory as a URI (a plain path
     * is no valid link target), the file name of its notice, and the names of the menu
     * and submenu where the backend choice can be changed. Line breaks in the
     * template are white space to the HTML pane of
     * {@link #createMessagePane}.
     */
    private static final String INSTALLED_REPORT
        = """
            <html><body style='width: 400px'>
            The %1$s is installed in <a href="%2$s">GROOVE's extension folder</a>
            and selected as graph backend from the next start of the Simulator on
            (the choice can be changed under %4$s &gt; %5$s).<br><br>
            The use of this backend is restricted to non-commercial purposes; see <a href="%2$s%3$s">%3$s</a>
            (in the extension folder) for more information.
            </body></html>
            """;
    /**
     * HTML template of the report of an installation deferred to the next start, with
     * the same parameters as {@link #INSTALLED_REPORT}, the directory being that of the
     * pending installation.
     */
    private static final String DEFERRED_REPORT
        = """
            <html><body style='width: 400px'>
            The %1$s is unpacked into <a href="%2$s">GROOVE's extension folder</a>,
            but the previously installed version is in use, by this or another running GROOVE,
            and cannot be replaced now; the new version is installed at the next start of GROOVE
            and selected as graph backend from then on
            (the choice can be changed under %4$s &gt; %5$s).<br><br>
            The use of this backend is restricted to non-commercial purposes; see <a href="%2$s%3$s">%3$s</a>
            (in the extension folder) for more information.
            </body></html>
            """;

    /**
     * System property that suppresses the first-run question when set to {@code false}.
     * The test configurations set it, so that the question cannot block a test.
     */
    public static final String PROMPT_PROPERTY = "groove.addon.prompt";

    /**
     * Background download and installation of the add-on for a GROOVE version,
     * reporting its progress in a dialog and its outcome afterwards.
     * The worker's inherited methods are unconstrained, hence the default is off.
     */
    @NonNullByDefault({})
    private class Download extends SwingWorker<Outcome,Long> {
        Download(String version, ProgressBarDialog progress) {
            this.version = version;
            this.progress = progress;
        }

        private final String version;
        private final ProgressBarDialog progress;
        private long size = -1;

        @Override
        protected Outcome doInBackground() throws IOException {
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
                    + " failed",
                            cause == null
                                ? exc
                                : cause);
            }
        }
    }
}
