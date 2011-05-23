/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.gui;

import groove.util.Groove;

import java.awt.Cursor;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.ImageIcon;

/**
 * List of all icons used in the GUI.
 * 
 * @author Eduardo Zambon
 */
public final class Icons {

    /** Open action icon. */
    public static final ImageIcon OPEN_ICON = createIcon("open.gif");
    /** Cancel action icon. */
    public static final ImageIcon CANCEL_ICON =
        createIcon("cancel-smaller.gif");
    /** Classic (simple) down-arrow icon. */
    public static final ImageIcon CLASSIC_DOWN_ARROW_ICON =
        createIcon("classic-down-arrow.gif");
    /** Classic (simple) down-arrow icon. */
    public static final ImageIcon CLASSIC_LEFT_ARROW_ICON =
        createIcon("classic-left-arrow.gif");
    /** Classic (simple) down-arrow icon. */
    public static final ImageIcon CLASSIC_RIGHT_ARROW_ICON =
        createIcon("classic-right-arrow.gif");
    /** Classic (simple) up-arrow icon. */
    public static final ImageIcon CLASSIC_UP_ARROW_ICON =
        createIcon("classic-up-arrow.gif");
    /** Copy action icon. */
    public static final ImageIcon COPY_ICON = createIcon("copy.gif");
    /** Cut action icon. */
    public static final ImageIcon CUT_ICON = createIcon("cut.gif");
    /** Delete action icon. */
    public static final ImageIcon DELETE_ICON = createIcon("delete.gif");
    /** Disable action icon. */
    public static final ImageIcon DISABLE_ICON =
        createIcon("disable-smaller.gif");
    /** Edit action icon. */
    public static final ImageIcon EDIT_ICON = createIcon("edit.gif");
    /** Enable action icon. */
    public static final ImageIcon ENABLE_ICON = createIcon("enable.gif");
    /** Icon for a New action. */
    public static final ImageIcon NEW_ICON = createIcon("new.gif");
    /** Icon for a New Graph action. */
    public static final ImageIcon NEW_GRAPH_ICON = createIcon("new-G.gif");
    /** Icon for a Start Simulation action. */
    public static final ImageIcon NEW_LTS_ICON = createIcon("new-LTS.gif");
    /** Icon for a New Rule action. */
    public static final ImageIcon NEW_RULE_ICON = createIcon("new-R.gif");
    /** Icon for a New Type action. */
    public static final ImageIcon NEW_TYPE_ICON = createIcon("new-T.gif");
    /** Rename action icon. */
    public static final ImageIcon RENAME_ICON = createIcon("rename.gif");
    /** Redo action icon. */
    public static final ImageIcon REDO_ICON = createIcon("redo.gif");
    /** Undo action icon. */
    public static final ImageIcon UNDO_ICON = createIcon("undo.gif");
    /** Icon for GPS folders. */
    public static final ImageIcon GPS_FOLDER_ICON = createIcon("gps.gif");
    /** Control automaton preview icon. */
    public static final ImageIcon CONTROL_MODE_ICON =
        createIcon("graph-mode.gif");
    /** Icon for Control Panel. */
    public static final ImageIcon CONTROL_FRAME_ICON =
        createIcon("cp-frame.gif");
    /** Icon for Control Files. */
    public static final ImageIcon CONTROL_FILE_ICON =
        createIcon("control-file.gif");
    /** Icon starting automatic simulation. */
    public static final ImageIcon FORWARD_ICON = createIcon("forward.gif");
    /** Icon for graph (GXL or GST) files. */
    public static final ImageIcon GRAPH_FILE_ICON =
        createIcon("graph-file.gif");
    /** Icon for the state panel of the simulator. */
    public static final ImageIcon GRAPH_FRAME_ICON =
        createIcon("graph-frame.gif");
    /** Icon for graph with emphasised match. */
    public static final ImageIcon GRAPH_MATCH_ICON =
        createIcon("graph-match.gif");
    /** Graph editing mode icon. */
    public static final ImageIcon GRAPH_MODE_ICON =
        createIcon("graph-mode.gif");
    /** Icon for snap to grid action. */
    public static final ImageIcon GRID_ICON = createIcon("grid.gif");
    /** Icon for the LTS panel of the simulator. */
    public static final ImageIcon LTS_FRAME_ICON = createIcon("lts-frame.gif");
    /** Small icon for production rules. */
    public static final ImageIcon RULE_SMALL_ICON =
        createIcon("rule-small.gif");
    /** Icon for rule (GPR) files. */
    public static final ImageIcon RULE_FILE_ICON = createIcon("rule-file.gif");
    /** Icon for the rule panel of the simulator. */
    public static final ImageIcon RULE_FRAME_ICON =
        createIcon("rule-frame.gif");
    /** Rule editing mode icon. */
    public static final ImageIcon RULE_MODE_ICON = createIcon("rule-mode.gif");
    /** Save action icon. */
    public static final ImageIcon SAVE_ICON = createIcon("save.gif");
    /** Save-as action icon. */
    public static final ImageIcon SAVE_AS_ICON = createIcon("saveas.gif");
    /** Start action icon. */
    public static final ImageIcon START_ICON = createIcon("start.gif");
    /** Select action icon. */
    public static final ImageIcon SELECT_ICON = createIcon("select.gif");
    /** Icon for type (GTY) files. */
    public static final ImageIcon TYPE_FILE_ICON = createIcon("type-file.gif");
    /** Icon for Type Panel. */
    public static final ImageIcon TYPE_FRAME_ICON =
        createIcon("type-frame.gif");
    /** Type editing mode icon. */
    public static final ImageIcon TYPE_MODE_ICON = createIcon("type-mode.gif");
    /** Icon for Prolog Panel. */
    public static final ImageIcon PROLOG_FRAME_ICON =
        createIcon("prolog-frame.gif");
    /** GROOVE project icon in 16x16 format. */
    public static final ImageIcon GROOVE_ICON_16x16 =
        createIcon("groove-g-16x16.gif");
    /** Transparent open up-arrow icon. */
    public static final ImageIcon OPEN_UP_ARROW_ICON =
        createIcon("open-up-arrow.gif");
    /** Transparent open down-arrow icon. */
    public static final ImageIcon OPEN_DOWN_ARROW_ICON =
        createIcon("open-down-arrow.gif");
    /** Paste action icon. */
    public static final ImageIcon PASTE_ICON = createIcon("paste.gif");
    /** Special icon denoting choice e/a. */
    public static final ImageIcon E_A_CHOICE_ICON =
        createIcon("e-a-choice.gif");
    /** Edge action icon. */
    public static final ImageIcon EDGE_ICON = createIcon("edge.gif");
    /** Preview action icon. */
    public static final ImageIcon PREVIEW_ICON = createIcon("preview.gif");

    /** Icon in the shape of an open hand, to be used as cursor. */
    public static final ImageIcon OPEN_HAND_CURSOR_ICON =
        createIcon("openhand.gif");
    /** Icon in the shape of an open hand. */
    public static final ImageIcon OPEN_HAND_ICON =
        createIcon("openhand-small.gif");
    /** Icon in the shape of a closed hand. */
    public static final ImageIcon CLOSED_HAND_ICON =
        createIcon("closedhand.gif");
    /** Icon in the shape of a + magnifying class. */
    public static final ImageIcon ZOOM_IN_ICON = createIcon("zoomin.gif");
    /** Icon in the shape of a - magnifying class. */
    public static final ImageIcon ZOOM_OUT_ICON = createIcon("zoomout.gif");

    /** Custom cursor in the shape of an open hand. */
    public static final Cursor OPEN_HAND_CURSOR = createCursor("Open Hand",
        OPEN_HAND_CURSOR_ICON);
    /** Custom cursor in the shape of a closed hand. */
    public static final Cursor CLOSED_HAND_CURSOR = createCursor("Closed Hand",
        CLOSED_HAND_ICON);

    /** Creates a named cursor from a given file. */
    static private ImageIcon createIcon(String filename) {
        return new ImageIcon(Groove.getResource(filename));
    }

    /** Creates a named cursor from a given file. */
    static private Cursor createCursor(String name, ImageIcon icon) {
        if (GraphicsEnvironment.isHeadless()) {
            // The environtment variable DISPLAY is not set. We can't call
            // createCustomCursor from the awt toolkit because this causes
            // a java.awt.HeadlessException. In any case we don't need the
            // cursor because we are running without GUI, so we just abort.
            return null;
        } else {
            Toolkit tk = Toolkit.getDefaultToolkit();
            Image cursorImage = icon.getImage();
            return tk.createCustomCursor(cursorImage, new Point(0, 0), name);
        }
    }

}
