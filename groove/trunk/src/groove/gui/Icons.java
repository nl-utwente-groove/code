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

    /** Transparent open up-arrow icon. */
    public static final ImageIcon ARROW_OPEN_UP_ICON =
        createIcon("arrow-open-up.gif");
    /** Transparent open down-arrow icon. */
    public static final ImageIcon ARROW_OPEN_DOWN_ICON =
        createIcon("arrow-open-down.gif");
    /** Classic (simple) down-arrow icon. */
    public static final ImageIcon ARROW_SIMPLE_DOWN_ICON =
        createIcon("arrow-simple-down.gif");
    /** Classic (simple) down-arrow icon. */
    public static final ImageIcon ARROW_SIMPLE_LEFT_ICON =
        createIcon("arrow-wide-left.gif");
    /** Classic (simple) down-arrow icon. */
    public static final ImageIcon ARROW_SIMPLE_RIGHT_ICON =
        createIcon("arrow-wide-right.gif");
    /** Classic (simple) up-arrow icon. */
    public static final ImageIcon ARROW_SIMPLE_UP_ICON =
        createIcon("arrow-simple-up.gif");
    /** Cancel action icon. */
    public static final ImageIcon CANCEL_ICON = createIcon("cancel.gif");
    /** Control automaton preview icon. */
    public static final ImageIcon CONTROL_MODE_ICON =
        createIcon("control-mode.gif");
    /** Icon for Control Panel. */
    public static final ImageIcon CONTROL_FRAME_ICON =
        createIcon("control-frame.gif");
    /** Icon for Control Files. */
    public static final ImageIcon CONTROL_FILE_ICON =
        createIcon("control-file.gif");
    /** Small icon for control programs, as shown in the control list. */
    public static final ImageIcon CONTROL_LIST_ICON =
        createIcon("control-file.gif");
    /** Copy action icon. */
    public static final ImageIcon COPY_ICON = createIcon("copy.gif");
    /** Cut action icon. */
    public static final ImageIcon CUT_ICON = createIcon("cut.gif");
    /** Delete action icon. */
    public static final ImageIcon DELETE_ICON = createIcon("delete.gif");
    /** Disable action icon. */
    public static final ImageIcon DISABLE_ICON = createIcon("disable.gif");
    /** Special icon denoting choice e/a. */
    public static final ImageIcon E_A_CHOICE_ICON =
        createIcon("e-a-choice.gif");
    /** Edge action icon. */
    public static final ImageIcon EDGE_ICON = createIcon("edge.gif");
    /** Edit action icon. */
    public static final ImageIcon EDIT_ICON = createIcon("edit.gif");
    /** Control edit action icon. */
    public static final ImageIcon EDIT_CONTROL_ICON = createIcon("edit-C.gif");
    /** Graph edit action icon. */
    public static final ImageIcon EDIT_GRAPH_ICON = createIcon("edit-G.gif");
    /** Rule edit action icon. */
    public static final ImageIcon EDIT_RULE_ICON = createIcon("edit-R.gif");
    /** Type edit action icon. */
    public static final ImageIcon EDIT_TYPE_ICON = createIcon("edit-T.gif");
    /** Prolog edit action icon. */
    public static final ImageIcon EDIT_PROLOG_ICON = createIcon("edit-P.gif");
    /** Enable action icon. */
    public static final ImageIcon ENABLE_ICON = createIcon("enable.gif");
    /** Export action icon. */
    public static final ImageIcon EXPORT_ICON = createIcon("export.gif");
    /** Icon for restart movement. */
    public static final ImageIcon GO_PREVIOUS_ICON =
        createIcon("go-previous.gif");
    /** Icon for fast-forward movement. */
    public static final ImageIcon GO_FORWARD_ICON =
        createIcon("go-forward.gif");
    /** Icon for single-step movement. */
    public static final ImageIcon GO_NEXT_ICON = createIcon("go-next.gif");
    /** Icon for normal forward movement. */
    public static final ImageIcon GO_START_ICON = createIcon("go-start.gif");
    /** Icon for fast-backward movement. */
    public static final ImageIcon GO_REWIND_ICON = createIcon("go-rewind.gif");
    /** Icon for GPS folders. */
    public static final ImageIcon GPS_FOLDER_ICON = createIcon("gps.gif");
    /** Icon for compressed GPS folders. */
    public static final ImageIcon GPS_COMPRESSED_FOLDER_ICON =
        createIcon("gps-compressed.png");
    /** GROOVE project icon in 16x16 format. */
    public static final ImageIcon GROOVE_ICON_16x16 =
        createIcon("groove-g-16x16.gif");
    /** Icon for graph (GXL or GST) files. */
    public static final ImageIcon GRAPH_FILE_ICON =
        createIcon("graph-file.gif");
    /** Icon for the state panel of the simulator. */
    public static final ImageIcon GRAPH_FRAME_ICON =
        createIcon("graph-frame.gif");
    /** Icon for graph with emphasised match. */
    public static final ImageIcon GRAPH_MATCH_ICON =
        createIcon("graph-match.gif");
    /** Icon for graph as shown in the host graph list. */
    public static final ImageIcon GRAPH_LIST_ICON =
        createIcon("graph-small.gif");
    /** Graph editing mode icon. */
    public static final ImageIcon GRAPH_MODE_ICON =
        createIcon("graph-mode.gif");
    /** Icon for snap to grid action. */
    public static final ImageIcon GRID_ICON = createIcon("grid.gif");
    /** Icon in the shape of an open hand, to be used as cursor. */
    public static final ImageIcon HAND_OPEN_CURSOR_ICON =
        createIcon("hand-open.gif");
    /** Icon in the shape of an open hand. */
    public static final ImageIcon HAND_OPEN_ICON =
        createIcon("hand-open-small.gif");
    /** Icon in the shape of a closed hand. */
    public static final ImageIcon HAND_CLOSED_ICON =
        createIcon("hand-closed.gif");
    /** Import action icon. */
    public static final ImageIcon IMPORT_ICON = createIcon("import.gif");
    /** Icon for the layout action. */
    public static final ImageIcon LAYOUT_ICON = createIcon("layout.gif");
    /** Icon for the LTS panel of the simulator. */
    public static final ImageIcon LTS_FRAME_ICON = createIcon("lts-frame.gif");
    /** Icon for a New action. */
    public static final ImageIcon NEW_ICON = createIcon("new.gif");
    /** Icon for a New Graph action. */
    public static final ImageIcon NEW_GRAPH_ICON = createIcon("new-G.gif");
    /** Icon for a New Rule action. */
    public static final ImageIcon NEW_RULE_ICON = createIcon("new-R.gif");
    /** Icon for a New Type action. */
    public static final ImageIcon NEW_TYPE_ICON = createIcon("new-T.gif");
    /** Icon for a New Control action. */
    public static final ImageIcon NEW_CONTROL_ICON = createIcon("new-C.gif");
    /** Icon for a New Prolog action. */
    public static final ImageIcon NEW_PROLOG_ICON = createIcon("new-P.gif");
    /** Open action icon. */
    public static final ImageIcon OPEN_ICON = createIcon("open.gif");
    /** Paste action icon. */
    public static final ImageIcon PASTE_ICON = createIcon("paste.gif");
    /** Preview action icon. */
    public static final ImageIcon PREVIEW_ICON = createIcon("preview.gif");
    /** Icon for Prolog Panel. */
    public static final ImageIcon PROLOG_FRAME_ICON =
        createIcon("prolog-frame.gif");
    /** Icon for Prolog Files. */
    public static final ImageIcon PROLOG_FILE_ICON =
        createIcon("prolog-file.gif");
    /** Small icon for production rules, as shown in the prolog list. */
    public static final ImageIcon PROLOG_LIST_ICON =
        createIcon("prolog-file.gif");
    /** Redo action icon. */
    public static final ImageIcon REDO_ICON = createIcon("redo.gif");
    /** Rename action icon. */
    public static final ImageIcon RENAME_ICON = createIcon("rename.gif");
    /** Small icon for production rules, as shown in the rule tree. */
    public static final ImageIcon RULE_LIST_ICON = createIcon("rule-small.gif");
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
    /** Select action icon. */
    public static final ImageIcon SELECT_ICON = createIcon("select.gif");
    /** State display mode icon. */
    public static final ImageIcon STATE_MODE_ICON =
        createIcon("state-mode.gif");
    /** Icon for type (GTY) files. */
    public static final ImageIcon TYPE_FILE_ICON = createIcon("type-file.gif");
    /** Icon for Type Panel. */
    public static final ImageIcon TYPE_FRAME_ICON =
        createIcon("type-frame.gif");
    /** Type editing mode icon. */
    public static final ImageIcon TYPE_LIST_ICON = createIcon("type-small.gif");
    /** Type editing mode icon. */
    public static final ImageIcon TYPE_MODE_ICON = createIcon("type-mode.gif");
    /** Undo action icon. */
    public static final ImageIcon UNDO_ICON = createIcon("undo.gif");
    /** Icon in the shape of a + magnifying class. */
    public static final ImageIcon ZOOM_IN_ICON = createIcon("zoomin.gif");
    /** Icon in the shape of a - magnifying class. */
    public static final ImageIcon ZOOM_OUT_ICON = createIcon("zoomout.gif");

    /** Custom cursor in the shape of an open hand. */
    public static final Cursor HAND_OPEN_CURSOR = createCursor("Open Hand",
        HAND_OPEN_CURSOR_ICON);
    /** Custom cursor in the shape of a closed hand. */
    public static final Cursor HAND_CLOSED_CURSOR = createCursor("Closed Hand",
        HAND_CLOSED_ICON);

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
