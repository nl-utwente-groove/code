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

import groove.io.store.SystemStore;
import groove.io.store.SystemStoreFactory;

import java.io.IOException;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

/**
 * Class that saves some basic information on the status of the Simulator.
 * @author Eduardo Zambon
 */
public class UserSettings {
    /** Reads and applies previously stored settings. */
    public static void applyUserSettings(Simulator simulator) {
        applyFrameSettings(simulator.getFrame());
        applyLocationSettings(simulator);
        applyDisplaySettings(simulator);
    }

    /** Reads and applies previously stored settings. */
    private static void applyFrameSettings(JFrame frame) {
        String simMax = userPrefs.get(SIM_MAX_KEY, "");
        String simWidth = userPrefs.get(SIM_WIDTH_KEY, "");
        String simHeight = userPrefs.get(SIM_HEIGHT_KEY, "");
        String rulePos = userPrefs.get(RULE_GRAPH_DIV_POS_KEY, "");
        String mainPos = userPrefs.get(MAIN_DIV_POS_KEY, "");

        if (simMax.isEmpty() || simWidth.isEmpty() || simHeight.isEmpty()
            || rulePos.isEmpty() || mainPos.isEmpty()) {
            return;
        }

        if (Boolean.parseBoolean(simMax)) {
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            int w = Integer.parseInt(simWidth);
            int h = Integer.parseInt(simHeight);
            frame.setSize(w, h);
        }

        JSplitPane jsp = (JSplitPane) frame.getContentPane().getComponent(1);
        jsp.setDividerLocation(Integer.parseInt(mainPos));

        jsp = (JSplitPane) jsp.getLeftComponent();
        jsp.setDividerLocation(Integer.parseInt(rulePos));
    }

    private static boolean isFrameMaximized(JFrame frame) {
        return frame.getExtendedState() == JFrame.MAXIMIZED_BOTH;
    }

    private static int getFrameWidth(JFrame frame) {
        return frame.getWidth();
    }

    private static int getFrameHeight(JFrame frame) {
        return frame.getHeight();
    }

    private static int getRuleDivPos(JFrame frame) {
        JSplitPane jsp = (JSplitPane) frame.getContentPane().getComponent(1);
        jsp = (JSplitPane) jsp.getLeftComponent();
        return jsp.getDividerLocation();
    }

    private static int getMainDivPos(JFrame frame) {
        JSplitPane jsp = (JSplitPane) frame.getContentPane().getComponent(1);
        return jsp.getDividerLocation();
    }

    /** Restores the persisted user preferences into a given simulator. */
    private static void applyLocationSettings(final Simulator simulator) {
        String location = userPrefs.get(LOCATION_KEY, "");
        if (!location.isEmpty()) {
            // Set the value back so that an error in loading does not
            // reoccur forever from now on
            userPrefs.remove(LOCATION_KEY);
            try {
                final SystemStore store = SystemStoreFactory.newStore(location);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            simulator.getActions().getLoadGrammarAction().load(
                                store);
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
        final DisplayKind kind =
            display.isEmpty() ? null : DisplayKind.valueOf(display);
        String ltsTab = userPrefs.get(SIMULATION_TAB_KEY, "");
        final Integer tabIndex =
            ltsTab.isEmpty() ? null : Integer.parseInt(ltsTab);
        if (kind != null || tabIndex != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (kind != null) {
                        simulator.getModel().setDisplay(kind);
                    }
                    if (tabIndex != null) {
                        simulator.getDisplaysPanel().getLtsDisplay().getTabPane().setSelectedIndex(
                            tabIndex);
                    }
                }
            });
        }
    }

    /** Synchronises saved settings with the current ones. */
    public static void syncSettings(Simulator simulator) {
        syncFrameSettings(simulator.getFrame());
        syncLocationSettings(simulator);
        syncDisplaySettings(simulator);
    }

    /** Synchronises saved settings with the current ones. */
    private static void syncFrameSettings(JFrame frame) {
        String simMax = Boolean.toString(isFrameMaximized(frame));
        String simWidth = Integer.toString(getFrameWidth(frame));
        String simHeight = Integer.toString(getFrameHeight(frame));
        String rulePos = Integer.toString(getRuleDivPos(frame));
        String mainPos = Integer.toString(getMainDivPos(frame));

        userPrefs.put(SIM_MAX_KEY, simMax);
        userPrefs.put(SIM_WIDTH_KEY, simWidth);
        userPrefs.put(SIM_HEIGHT_KEY, simHeight);
        userPrefs.put(RULE_GRAPH_DIV_POS_KEY, rulePos);
        userPrefs.put(MAIN_DIV_POS_KEY, mainPos);
    }

    /** Persists the state of the simulator into the user preferences. */
    private static void syncLocationSettings(Simulator simulator) {
        SimulatorModel model = simulator.getModel();
        if (model != null) {
            SystemStore store = model.getStore();
            if (store != null) {
                Object location = store.getLocation();
                userPrefs.put(LOCATION_KEY, location.toString());
            }
        }
    }

    /** Persists the selected display. */
    private static void syncDisplaySettings(Simulator simulator) {
        Object display = simulator.getModel().getDisplay().name();
        userPrefs.put(DISPLAY_KEY, display.toString());
        int ltsTabIndex =
            simulator.getDisplaysPanel().getLtsDisplay().getTabPane().getSelectedIndex();
        if (ltsTabIndex >= 0) {
            userPrefs.put(SIMULATION_TAB_KEY, "" + ltsTabIndex);
        } else {
            userPrefs.remove(SIMULATION_TAB_KEY);
        }
    }

    /** The persistently stored user preferences. */
    private static final Preferences userPrefs = Options.userPrefs;
    /** Key for the grammar location. */
    private static final String LOCATION_KEY = "Grammar location";
    /** Key for the selected display. */
    private static final String DISPLAY_KEY = "Selected display";
    /** Key for the selected simulation tab. */
    private static final String SIMULATION_TAB_KEY = "Selected simulation tab";
    static private final String SIM_MAX_KEY = "Simulator maximized";
    static private final String SIM_WIDTH_KEY = "Simulator width";
    static private final String SIM_HEIGHT_KEY = "Simulator height";
    static private final String RULE_GRAPH_DIV_POS_KEY =
        "Rule-Graph divider position";
    static private final String MAIN_DIV_POS_KEY =
        "Main panel divider position";
}