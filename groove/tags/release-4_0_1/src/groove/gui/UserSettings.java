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

import javax.swing.JFrame;
import javax.swing.JSplitPane;

/**
 * Class that saves some basic information on the status of the Simulator.
 * @author Eduardo Zambon
 */

/** Class saving a previous user settings. */
public class UserSettings {

    /** Reads and applies previously stored settings. */
    public static void applyUserSettings(JFrame frame) {
        String simMax = Options.userPrefs.get(SIM_MAX, "");
        String simWidth = Options.userPrefs.get(SIM_WIDTH, "");
        String simHeight = Options.userPrefs.get(SIM_HEIGHT, "");
        String rulePos = Options.userPrefs.get(RULE_GRAPH_DIV_POS, "");
        String mainPos = Options.userPrefs.get(MAIN_DIV_POS, "");

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

    /** Synchronizes saved settings with the current ones. */
    public static void synchSettings(JFrame frame) {
        String simMax = new Boolean(isFrameMaximized(frame)).toString();
        String simWidth = new Integer(getFrameWidth(frame)).toString();
        String simHeight = new Integer(getFrameHeight(frame)).toString();
        String rulePos = new Integer(getRuleDivPos(frame)).toString();
        String mainPos = new Integer(getMainDivPos(frame)).toString();

        Options.userPrefs.put(SIM_MAX, simMax);
        Options.userPrefs.put(SIM_WIDTH, simWidth);
        Options.userPrefs.put(SIM_HEIGHT, simHeight);
        Options.userPrefs.put(RULE_GRAPH_DIV_POS, rulePos);
        Options.userPrefs.put(MAIN_DIV_POS, mainPos);
    }

    static private final String SIM_MAX = "Simulator maximized";
    static private final String SIM_WIDTH = "Simulator width";
    static private final String SIM_HEIGHT = "Simulator height";
    static private final String RULE_GRAPH_DIV_POS =
        "Rule-Graph divider position";
    static private final String MAIN_DIV_POS = "Main panel divider position";

}
