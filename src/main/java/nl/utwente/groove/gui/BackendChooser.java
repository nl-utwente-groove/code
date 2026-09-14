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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.gui.view.GraphBackend;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.AddOn;
import nl.utwente.groove.util.Extensions;

/**
 * The Simulator's chooser of the graph-visualisation backend: a menu with an item for
 * every backend that can be chosen for the next start, which are the backends
 * available in this run (see {@link GraphBackend#available()}) and the backend of
 * the add-on if that is installed but not loaded, as after an installation during
 * this run. The choice is stored as the user preference
 * {@link Options#GRAPH_BACKEND_OPTION} and takes effect at the next start, since the
 * backend is selected at start-up (see {@link GraphBackend#instance()}); a dialog
 * says so whenever a backend other than the one in use is chosen.
 * <p>
 * The menu shows two things: the backend in use in this run, by its check mark, and
 * the backend expected to be in use at the next start, by the suffix
 * {@link #NEXT_START_SUFFIX} to its name if it is not the one in use. The expectation
 * applies the selection of the start-up to the backends of the next start: the
 * preferred one if it is among them, otherwise the first in the ranking.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class BackendChooser {
    /**
     * Constructs a chooser for a given frame and add-on.
     * @param frame the Simulator frame, parent of the chooser's dialogs
     * @param addOn the add-on whose backend is offered if it is installed, even if
     * it is not loaded in this run
     */
    public BackendChooser(JFrame frame, AddOn addOn) {
        this.frame = frame;
        this.addOn = addOn;
    }

    private final JFrame frame;
    private final AddOn addOn;

    /**
     * Creates a fresh menu with the backends that can be chosen for the next start,
     * marked as described in the class comment; the menu reflects the state at the
     * time of the call, so it should be created afresh whenever it is shown.
     * @return the menu, or {@code null} if there is nothing to choose, i.e., if
     * only one backend can be chosen
     */
    public @Nullable JMenu createMenu() {
        Path ext = Extensions.dir();
        // the backends that can be chosen, by name and display name
        Map<String,String> choices = new LinkedHashMap<>();
        for (GraphBackend backend : GraphBackend.available()) {
            choices.put(backend.getName(), backend.getDisplayName());
        }
        boolean addOnLoads = this.addOn.loadsAtNextStart(ext);
        if (addOnLoads) {
            choices.putIfAbsent(this.addOn.getName(), this.addOn.getDisplayName());
        }
        if (choices.size() < 2) {
            return null;
        }
        // the backends expected at the next start: the choices, minus the add-on's
        // if that is present but will not load, e.g. because its removal is pending
        List<String> nextAvailable = new ArrayList<>(choices.keySet());
        if (!addOnLoads && this.addOn.isPresent(ext)) {
            nextAvailable.remove(this.addOn.getName());
        }
        String running = GraphBackend.instance().getName();
        String next = nextAvailable.isEmpty()
            ? null
            : GraphBackend
                .selectName(nextAvailable, Options.userPrefs.get(Options.GRAPH_BACKEND_OPTION, null));
        JMenu result = new JMenu(Options.GRAPH_BACKEND_OPTION);
        result.setToolTipText("A change takes effect at the next start of the Simulator");
        for (var choice : choices.entrySet()) {
            String name = choice.getKey();
            String displayName = choice.getValue();
            boolean isRunning = name.equals(running);
            boolean isNext = name.equals(next);
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(isNext && !isRunning
                ? displayName + NEXT_START_SUFFIX
                : displayName);
            item.setSelected(isRunning);
            item
                .setToolTipText(isRunning
                    ? (isNext
                        ? "In use, also from the next start"
                        : "In use in this run; click to keep it from the next start")
                    : (isNext
                        ? "Selected for the next start"
                        : "Click to select for the next start"));
            item.addActionListener(e -> choose(name, displayName, isRunning));
            result.add(item);
        }
        return result;
    }

    /** Records a backend as the choice for the next start, and says so if it is not the one in use. */
    private void choose(String name, String displayName, boolean isRunning) {
        Options.userPrefs.put(Options.GRAPH_BACKEND_OPTION, name);
        if (!isRunning) {
            // postponed until the menu has closed
            SwingUtilities.invokeLater(() -> JOptionPane
                .showMessageDialog(this.frame,
                                   "The graph backend is set to " + displayName
                                       + "; the change takes effect at the next start of the Simulator.",
                                   CHANGED_TITLE, JOptionPane.INFORMATION_MESSAGE));
        }
    }

    /** Suffix to the name of the backend expected at the next start, if that is not the one in use. */
    public static final String NEXT_START_SUFFIX = " (from next start)";
    /** Title of the dialog reporting a changed choice. */
    public static final String CHANGED_TITLE = "Graph backend changed";
}
