/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
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
 */
package groove.gui;

import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import groove.gui.display.DisplayKind;
import groove.gui.display.LTSDisplay;
import groove.io.store.SystemStore;

/**
 * Class that saves some basic information on the status of the Simulator.
 * @author Eduardo Zambon
 */
public class UserSettings {
    /** Reads and applies previously stored settings. */
    public static void applyUserSettings(Simulator simulator) {
        applyFrameSettings(simulator);
        applyLocationSettings(simulator);
        applyDisplaySettings(simulator);
    }

    /** Reads and applies previously stored settings. */
    private static void applyFrameSettings(Simulator simulator) {
        String simMax = userPrefs.get(SIM_MAX_KEY, "");
        String simWidth = userPrefs.get(SIM_WIDTH_KEY, "");
        String simHeight = userPrefs.get(SIM_HEIGHT_KEY, "");

        if (!simMax.isEmpty() && !simWidth.isEmpty() && !simHeight.isEmpty()) {
            JFrame frame = simulator.getFrame();
            if (Boolean.parseBoolean(simMax)) {
                frame.setExtendedState(Frame.MAXIMIZED_BOTH);
            } else {
                int w = Integer.parseInt(simWidth);
                int h = Integer.parseInt(simHeight);
                frame.setSize(w, h);
            }
        }

        String grammarPos = userPrefs.get(GRAMMAR_DIV_POS_KEY, "");
        if (!grammarPos.isEmpty()) {
            JSplitPane jsp = simulator.getGrammarPanel();
            jsp.setDividerLocation(Integer.parseInt(grammarPos));
        }

        String displaysInfoPos = userPrefs.get(DISPLAYS_INFO_DIV_POS_KEY, "");
        if (!displaysInfoPos.isEmpty()) {
            JSplitPane jsp = simulator.getDisplaysInfoPanel();
            jsp.setDividerLocation(Integer.parseInt(displaysInfoPos));
        }

        String listsPos = userPrefs.get(LISTS_DIV_POS_KEY, "");
        if (!listsPos.isEmpty()) {
            JSplitPane jsp = simulator.getListsPanel();
            jsp.setDividerLocation(Integer.parseInt(listsPos));
        }
    }

    private static boolean isFrameMaximized(JFrame frame) {
        return frame.getExtendedState() == Frame.MAXIMIZED_BOTH;
    }

    private static int getFrameWidth(JFrame frame) {
        return frame.getWidth();
    }

    private static int getFrameHeight(JFrame frame) {
        return frame.getHeight();
    }

    /** Restores the persisted user preferences into a given simulator. */
    private static void applyLocationSettings(final Simulator simulator) {
        String location = userPrefs.get(LOCATION_KEY, "");
        if (!location.isEmpty()) {
            // Set the value back so that an error in loading does not
            // reoccur forever from now on
            userPrefs.remove(LOCATION_KEY);
            try {
                final SystemStore store = SystemStore.newStore(new File(location), false);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            simulator.getActions()
                                .getLoadGrammarAction()
                                .load(store);
                        } catch (IOException e) {
                            // don't load if we're going to be difficult
                        }
                    }
                });
            } catch (IOException e) {
                // don't load if we're going to be difficult
            }
        }
    }

    private static void applyDisplaySettings(final Simulator simulator) {
        String display = userPrefs.get(DISPLAY_KEY, "");
        DisplayKind kindValue = null;
        if (!display.isEmpty()) {
            try {
                kindValue = DisplayKind.valueOf(display);
            } catch (IllegalArgumentException exc) {
                // do nothing
            }
        }
        int stateBoundValue;
        try {
            stateBoundValue = Integer.parseInt(userPrefs.get(STATE_BOUND_KEY, "1000"));
        } catch (NumberFormatException e) {
            stateBoundValue = 1000;
        }
        final DisplayKind kind = kindValue;
        final int stateBound = stateBoundValue;
        if (kind != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    simulator.getModel()
                        .setDisplay(kind);
                    ((LTSDisplay) simulator.getDisplaysPanel()
                        .getDisplay(DisplayKind.LTS)).setStateBound(stateBound);
                }
            });
        }
    }

    /** Synchronises saved settings with the current ones. */
    public static void syncSettings(Simulator simulator) {
        syncFrameSettings(simulator);
        syncLocationSettings(simulator);
        syncDisplaySettings(simulator);
    }

    /** Synchronises saved settings with the current ones. */
    private static void syncFrameSettings(Simulator simulator) {
        JFrame frame = simulator.getFrame();
        String simMax = Boolean.toString(isFrameMaximized(frame));
        String simWidth = Integer.toString(getFrameWidth(frame));
        String simHeight = Integer.toString(getFrameHeight(frame));

        userPrefs.put(SIM_MAX_KEY, simMax);
        userPrefs.put(SIM_WIDTH_KEY, simWidth);
        userPrefs.put(SIM_HEIGHT_KEY, simHeight);
        int grammarPos = simulator.getGrammarPanel()
            .getDividerLocation();
        userPrefs.put(GRAMMAR_DIV_POS_KEY, "" + grammarPos);
        int displaysInfoPos = simulator.getDisplaysInfoPanel()
            .getDividerLocation();
        userPrefs.put(DISPLAYS_INFO_DIV_POS_KEY, "" + displaysInfoPos);
        int listsPos = simulator.getListsPanel()
            .getDividerLocation();
        userPrefs.put(LISTS_DIV_POS_KEY, "" + listsPos);
    }

    /** Persists the state of the simulator into the user preferences. */
    private static void syncLocationSettings(Simulator simulator) {
        SimulatorModel model = simulator.getModel();
        if (model != null) {
            SystemStore store = model.getStore();
            if (store != null) {
                File location = store.getLocation();
                userPrefs.put(LOCATION_KEY, location.toString());
            }
        }
    }

    /** Persists the selected display. */
    private static void syncDisplaySettings(Simulator simulator) {
        Object display = simulator.getModel()
            .getDisplay()
            .name();
        userPrefs.put(DISPLAY_KEY, display.toString());
        Integer stateBound = ((LTSDisplay) simulator.getDisplaysPanel()
            .getDisplay(DisplayKind.LTS)).getStateBound();
        userPrefs.put(STATE_BOUND_KEY, stateBound.toString());
    }

    /** The persistently stored user preferences. */
    private static final Preferences userPrefs = Options.userPrefs;
    /** Key for the selected display. */
    private static final String DISPLAY_KEY = "Selected display";
    /** Key for the divider position in the grammar panel. */
    static private final String DISPLAYS_INFO_DIV_POS_KEY = "Displays+info panel divider position";
    /** Key for the divider position in the main panel. */
    static private final String GRAMMAR_DIV_POS_KEY = "Main panel divider position";
    /** Key for the divider position in the lists panel. */
    static private final String LISTS_DIV_POS_KEY = "Rule-Graph divider position";
    /** Key for the grammar location. */
    private static final String LOCATION_KEY = "Grammar location";
    static private final String SIM_HEIGHT_KEY = "Simulator height";
    static private final String SIM_MAX_KEY = "Simulator maximized";
    static private final String SIM_WIDTH_KEY = "Simulator width";
    static private final String STATE_BOUND_KEY = "Maximum state displayed";
}
