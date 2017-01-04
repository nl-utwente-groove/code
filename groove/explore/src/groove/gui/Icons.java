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

import groove.grammar.model.ResourceKind;
import groove.io.store.EditType;
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
    /** Returns the icon for a certain edit on a grammar resource. */
    public static ImageIcon getEditIcon(EditType edit, ResourceKind resource) {
        switch (edit) {
        case COPY:
            return COPY_ICON;
        case CREATE:
            if (resource == null) {
                return NEW_ICON;
            }
            switch (resource) {
            case CONTROL:
                return NEW_CONTROL_ICON;
            case HOST:
                return NEW_GRAPH_ICON;
            case PROLOG:
                return NEW_PROLOG_ICON;
            case GROOVY:
                return NEW_ICON;
            case RULE:
                return NEW_RULE_ICON;
            case TYPE:
                return NEW_TYPE_ICON;
            case PROPERTIES:
            default:
                assert false;
                return null;
            }
        case DELETE:
            return DELETE_ICON;
        case MODIFY:
            if (resource == null) {
                return EDIT_ICON;
            }
            switch (resource) {
            case CONTROL:
                return EDIT_CONTROL_ICON;
            case HOST:
                return EDIT_GRAPH_ICON;
            case PROLOG:
                return EDIT_PROLOG_ICON;
            case GROOVY:
                return EDIT_ICON;
            case PROPERTIES:
                return EDIT_ICON;
            case RULE:
                return EDIT_RULE_ICON;
            case TYPE:
                return EDIT_TYPE_ICON;
            default:
                assert false;
                return null;
            }
        case RENAME:
            return RENAME_ICON;
        case ENABLE:
            return ENABLE_ICON;
        default:
            assert false;
            return null;
        }
    }

    /**
     * Returns the icon used for the main tab labels
     * in the display of a given resource kind.
     */
    public static ImageIcon getMainTabIcon(ResourceKind resource) {
        switch (resource) {
        case CONTROL:
            return CONTROL_FILE_ICON;
        case HOST:
            return GRAPH_MODE_ICON;
        case PROLOG:
            return PROLOG_FILE_ICON;
        case GROOVY:
            return GROOVY_FILE_ICON;
        case RULE:
            return RULE_MODE_ICON;
        case TYPE:
            return TYPE_MODE_ICON;
        default:
            assert false;
            return null;
        }
    }

    /**
     * Returns the icon used for the editor tab labels
     * in the display of a given resource kind.
     */
    public static ImageIcon getEditorTabIcon(ResourceKind resource) {
        switch (resource) {
        case CONTROL:
            return EDIT_CONTROL_ICON;
        case HOST:
            return EDIT_GRAPH_ICON;
        case PROLOG:
            return EDIT_PROLOG_ICON;
        case GROOVY:
            return EDIT_ICON;
        case RULE:
            return EDIT_RULE_ICON;
        case TYPE:
            return EDIT_TYPE_ICON;
        default:
            assert false;
            return null;
        }
    }

    /**
     * Returns the edit icon used for the label list
     * of a given resource kind.
     */
    public static ImageIcon getListEditIcon(ResourceKind resource) {
        switch (resource) {
        case CONTROL:
        case HOST:
        case PROLOG:
        case GROOVY:
        case TYPE:
            return EDIT_ICON;
        case RULE:
            return EDIT_WIDE_ICON;
        default:
            assert false;
            return null;
        }
    }

    /**
     * Returns the icon used for the label list
     * of a given resource kind.
     */
    public static ImageIcon getListIcon(ResourceKind resource) {
        switch (resource) {
        case CONTROL:
            return CONTROL_LIST_ICON;
        case HOST:
            return GRAPH_LIST_ICON;
        case PROLOG:
            return PROLOG_LIST_ICON;
        case GROOVY:
            return GROOVY_LIST_ICON;
        case RULE:
            return RULE_TREE_ICON;
        case TYPE:
            return TYPE_LIST_ICON;
        default:
            assert false;
            return null;
        }
    }

    /** Transparent open up-arrow icon. */
    public static final ImageIcon ARROW_OPEN_UP_ICON = createIcon("arrow-open-up.gif");
    /** Transparent open down-arrow icon. */
    public static final ImageIcon ARROW_OPEN_DOWN_ICON = createIcon("arrow-open-down.gif");
    /** Classic (simple) down-arrow icon. */
    public static final ImageIcon ARROW_SIMPLE_DOWN_ICON = createIcon("arrow-simple-down.gif");
    /** Classic (simple) down-arrow icon. */
    public static final ImageIcon ARROW_SIMPLE_LEFT_ICON = createIcon("arrow-wide-left.gif");
    /** Classic (simple) down-arrow icon. */
    public static final ImageIcon ARROW_SIMPLE_RIGHT_ICON = createIcon("arrow-wide-right.gif");
    /** Classic (simple) up-arrow icon. */
    public static final ImageIcon ARROW_SIMPLE_UP_ICON = createIcon("arrow-simple-up.gif");
    /** Cancel action icon. */
    public static final ImageIcon CANCEL_ICON = createIcon("cancel.gif");
    /** Compass icon. */
    public static final ImageIcon COMPASS_ICON = createIcon("compass.gif");
    /** Control automaton preview icon. */
    public static final ImageIcon CONTROL_MODE_ICON = createIcon("control-mode.gif");
    /** Icon for Control Panel. */
    public static final ImageIcon CONTROL_FRAME_ICON = createIcon("control-frame.gif");
    /** Icon for Control Files. */
    public static final ImageIcon CONTROL_FILE_ICON = createIcon("control-file.gif");
    /** Small icon for control programs, as shown in the control list. */
    public static final ImageIcon CONTROL_LIST_ICON = createIcon("control-file.gif");
    /** Copy action icon. */
    public static final ImageIcon COPY_ICON = createIcon("copy.gif");
    /** Cut action icon. */
    public static final ImageIcon CUT_ICON = createIcon("cut.gif");
    /** Delete action icon. */
    public static final ImageIcon DELETE_ICON = createIcon("delete.gif");
    /** Disable action icon. */
    public static final ImageIcon DISABLE_ICON = createIcon("disable.gif");
    /** Special icon denoting choice e/a. */
    public static final ImageIcon E_A_CHOICE_ICON = createIcon("e-a-choice.gif");
    /** Empty icon. */
    public static final ImageIcon EMPTY_ICON = new ImageIcon();
    /** Collapse all icon. */
    public static final ImageIcon COLLAPSE_ALL_ICON = createIcon("collapse-all.gif");
    /** Small icon for condition rules, as shown in the rule tree. */
    public static final ImageIcon CONDITION_TREE_ICON = createIcon("rule-condition.gif");
    /** Small icon for injective condition rules, as shown in the rule tree. */
    public static final ImageIcon CONDITION_I_TREE_ICON = createIcon("rule-condition-I.gif");
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
    /** State edit action icon. */
    public static final ImageIcon EDIT_STATE_ICON = createIcon("edit-S.gif");
    /** Wide edit action icon. */
    public static final ImageIcon EDIT_WIDE_ICON = createIcon("edit-wide.gif");
    /** Enable action icon. */
    public static final ImageIcon ENABLE_ICON = createIcon("enable.gif");
    /** Enable uniquely action icon. */
    public static final ImageIcon ENABLE_UNIQUE_ICON = createIcon("enable_unique.gif");
    /** Error icon. */
    public static final ImageIcon ERROR_ICON = createIcon("error.png");
    /** Export action icon. */
    public static final ImageIcon EXPORT_ICON = createIcon("export.gif");
    /** Small icon for forbidden condition rules, as shown in the rule tree. */
    public static final ImageIcon FORBIDDEN_TREE_ICON = createIcon("rule-forbidden.gif");
    /** Small icon for injective forbidden condition rules, as shown in the rule tree. */
    public static final ImageIcon FORBIDDEN_I_TREE_ICON = createIcon("rule-forbidden-I.gif");
    /** Icon for restart movement. */
    public static final ImageIcon GO_PREVIOUS_ICON = createIcon("go-previous.gif");
    /** Icon for fast-forward movement. */
    public static final ImageIcon GO_FORWARD_ICON = createIcon("go-forward.gif");
    /** Icon for single-step movement. */
    public static final ImageIcon GO_NEXT_ICON = createIcon("go-next.gif");
    /** Icon for normal forward movement. */
    public static final ImageIcon GO_START_ICON = createIcon("go-start.gif");
    /** Icon for stopping movement. */
    public static final ImageIcon GO_STOP_ICON = createIcon("go-stop.gif");
    /** Icon for fast-backward movement. */
    public static final ImageIcon GO_REWIND_ICON = createIcon("go-rewind.gif");
    /** Icon for GPS folders. */
    public static final ImageIcon GPS_FOLDER_ICON = createIcon("gps.gif");
    /** Icon for compressed GPS folders. */
    public static final ImageIcon GPS_COMPRESSED_FOLDER_ICON = createIcon("gps-compressed.png");
    /** GROOVE project icon in 16x16 format. */
    public static final ImageIcon GROOVE_ICON_16x16 = createIcon("groove-g-16x16.gif");
    /** Icon for graph (GXL or GST) files. */
    public static final ImageIcon GRAPH_FILE_ICON = createIcon("graph-file.gif");
    /** Icon for the state panel of the simulator. */
    public static final ImageIcon GRAPH_FRAME_ICON = createIcon("graph-frame.gif");
    /** Icon for graph with emphasised match. */
    public static final ImageIcon GRAPH_MATCH_ICON = createIcon("graph-match.gif");
    /** Icon for graph as shown in the host graph list. */
    public static final ImageIcon GRAPH_LIST_ICON = createIcon("graph-small.gif");
    /** Graph editing mode icon. */
    public static final ImageIcon GRAPH_MODE_ICON = createIcon("graph-mode.gif");
    /** Icon for snap to grid action. */
    public static final ImageIcon GRID_ICON = createIcon("grid.gif");
    /** Icon in the shape of an open hand, to be used as cursor. */
    public static final ImageIcon HAND_OPEN_CURSOR_ICON = createIcon("hand-open.gif");
    /** Icon in the shape of an open hand. */
    public static final ImageIcon HAND_OPEN_ICON = createIcon("hand-open-small.gif");
    /** Icon in the shape of a closed hand. */
    public static final ImageIcon HAND_CLOSED_ICON = createIcon("hand-closed.gif");
    /** Icon for hiding lts. */
    public static final ImageIcon HIDE_LTS_ICON = createIcon("hide-lts.png");
    /** Icon for filtering the LTS. */
    public static final ImageIcon FILTER_LTS_ICON = createIcon("filter-lts.png");
    /** Import action icon. */
    public static final ImageIcon IMPORT_ICON = createIcon("import.gif");
    /** Small icon for invariant condition rules, as shown in the rule tree. */
    public static final ImageIcon INVARIANT_TREE_ICON = createIcon("rule-invariant.gif");
    /** Small icon for injective invariant condition rules, as shown in the rule tree. */
    public static final ImageIcon INVARIANT_I_TREE_ICON = createIcon("rule-invariant-I.gif");
    /** Icon for the layout action. */
    public static final ImageIcon LAYOUT_ICON = createIcon("layout.gif");
    /** Icon for the LTS panel of the simulator. */
    public static final ImageIcon LTS_FRAME_ICON = createIcon("lts-frame.gif");
    /** LTS tab icon. */
    public static final ImageIcon LTS_MODE_ICON = createIcon("lts-mode.gif");
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
    /** Pin icon. */
    public static final ImageIcon PIN_ICON = createIcon("pin.gif");
    /** Preview action icon. */
    public static final ImageIcon PREVIEW_ICON = createIcon("preview.gif");
    /** Icon for Prolog Panel. */
    public static final ImageIcon PROLOG_FRAME_ICON = createIcon("prolog-frame.gif");
    /** Icon for Prolog Files. */
    public static final ImageIcon PROLOG_FILE_ICON = createIcon("prolog-file.gif");
    /** Small icon for production rules, as shown in the prolog list. */
    public static final ImageIcon PROLOG_LIST_ICON = createIcon("prolog-file.gif");
    /** Small icon for puzzle piece. */
    public static final ImageIcon PUZZLE_ICON = createIcon("puzzle.gif");
    /** Small icon for C-indexed puzzle piece. */
    public static final ImageIcon PUZZLE_C_ICON = createIcon("puzzle-C.gif");
    /** Small icon for R-indexed puzzle piece. */
    public static final ImageIcon PUZZLE_R_ICON = createIcon("puzzle-R.gif");
    /** Icon for Groovy Panel. */
    public static final ImageIcon GROOVY_FRAME_ICON = createIcon("groovy-frame.gif");
    /** Icon for Groovy Files. */
    public static final ImageIcon GROOVY_FILE_ICON = createIcon("groovy-file.gif");
    /** Small icon for scripts, as shown in the Groovy list. */
    public static final ImageIcon GROOVY_LIST_ICON = createIcon("groovy-file.gif");
    /** Icon for Properties Panel. */
    public static final ImageIcon PROPERTIES_FRAME_ICON = createIcon("properties-frame.gif");
    /** Redo action icon. */
    public static final ImageIcon REDO_ICON = createIcon("redo.gif");
    /** Small icon for injective production rules, as shown in the rule tree. */
    public static final ImageIcon RULE_I_TREE_ICON = createIcon("rule-standard-I.gif");
    /** Small icon for production rules, as shown in the rule tree. */
    public static final ImageIcon RULE_TREE_ICON = createIcon("rule-standard.gif");
    /** Small icon for transactional rules, as shown in the rule tree. */
    public static final ImageIcon RECIPE_TREE_ICON = createIcon("rule-recipe.gif");
    /** Icon for rule (GPR) files. */
    public static final ImageIcon RULE_FILE_ICON = createIcon("rule-file.gif");
    /** Icon for the rule panel of the simulator. */
    public static final ImageIcon RULE_FRAME_ICON = createIcon("rule-frame.gif");
    /** Rule editing mode icon. */
    public static final ImageIcon RULE_MODE_ICON = createIcon("rule-mode.gif");
    /** Save action icon. */
    public static final ImageIcon SAVE_ICON = createIcon("save.gif");
    /** Save-as action icon. */
    public static final ImageIcon SAVE_AS_ICON = createIcon("saveas.gif");
    /** Select action icon. */
    public static final ImageIcon SELECT_ICON = createIcon("select.gif");
    /** Search action icon. */
    public static final ImageIcon SEARCH_ICON = createIcon("search.gif");
    /** Rename action icon. */
    public static final ImageIcon RENAME_ICON = createIcon("rename.gif");
    /** Absent state icon. */
    public static final ImageIcon STATE_ABSENT_ICON = createIcon("state-absent.gif");
    /** Closed state icon. */
    public static final ImageIcon STATE_CLOSED_ICON = createIcon("state-closed.gif");
    /** Closed state icon. */
    public static final ImageIcon STATE_FINAL_ICON = createIcon("state-final.gif");
    /** Icon for the state panel of the simulator. */
    public static final ImageIcon STATE_FRAME_ICON = createIcon("state-frame.gif");
    /** Internal state icon. */
    public static final ImageIcon STATE_INTERNAL_ICON = createIcon("state-internal.gif");
    /** Absent internal state icon. */
    public static final ImageIcon STATE_INTERNAL_ABSENT_ICON =
        createIcon("state-internal-absent.gif");
    /** State display mode icon. */
    public static final ImageIcon STATE_MODE_ICON = createIcon("state-mode.gif");
    /** Open state icon. */
    public static final ImageIcon STATE_OPEN_ICON = createIcon("state-open.gif");
    /** Closed state icon. */
    public static final ImageIcon STATE_RESULT_ICON = createIcon("state-result.gif");
    /** Start state icon. */
    public static final ImageIcon STATE_START_ICON = createIcon("state-start.gif");
    /** Transient state icon. */
    public static final ImageIcon STATE_TRANSIENT_ICON = createIcon("state-transient.gif");
    /** Icon for type (GTY) files. */
    public static final ImageIcon TYPE_FILE_ICON = createIcon("type-file.gif");
    /** Icon for Type Panel. */
    public static final ImageIcon TYPE_FRAME_ICON = createIcon("type-frame.gif");
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
    public static final Cursor HAND_OPEN_CURSOR = createCursor("Open Hand", HAND_OPEN_CURSOR_ICON);
    /** Custom cursor in the shape of a closed hand. */
    public static final Cursor HAND_CLOSED_CURSOR = createCursor("Closed Hand", HAND_CLOSED_ICON);

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
