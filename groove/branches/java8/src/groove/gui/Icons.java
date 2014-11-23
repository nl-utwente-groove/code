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
import groove.util.Exceptions;
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
    public static Icon getEditIcon(EditType edit, ResourceKind resource) {
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
            case FORMAT:
            case GROOVY:
                return NEW_ICON;
            case RULE:
                return NEW_RULE_ICON;
            case TYPE:
                return NEW_TYPE_ICON;
            case PROPERTIES:
            default:
                throw Exceptions.UNREACHABLE;
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
            case FORMAT:
            case GROOVY:
                return EDIT_ICON;
            case PROPERTIES:
                return EDIT_ICON;
            case RULE:
                return EDIT_RULE_ICON;
            case TYPE:
                return EDIT_TYPE_ICON;
            default:
                throw Exceptions.UNREACHABLE;
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
    public static Icon getMainTabIcon(ResourceKind resource) {
        switch (resource) {
        case CONTROL:
            return CONTROL_FILE_ICON;
        case HOST:
            return GRAPH_MODE_ICON;
        case PROLOG:
            return PROLOG_FILE_ICON;
        case GROOVY:
            return GROOVY_FILE_ICON;
        case FORMAT:
            return FORMAT_FILE_ICON;
        case RULE:
            return RULE_MODE_ICON;
        case TYPE:
            return TYPE_MODE_ICON;
        default:
            throw Exceptions.UNREACHABLE;
        }
    }

    /**
     * Returns the icon used for the editor tab labels
     * in the display of a given resource kind.
     */
    public static Icon getEditorTabIcon(ResourceKind resource) {
        switch (resource) {
        case CONTROL:
            return EDIT_CONTROL_ICON;
        case HOST:
            return EDIT_GRAPH_ICON;
        case PROLOG:
            return EDIT_PROLOG_ICON;
        case FORMAT:
        case GROOVY:
            return EDIT_ICON;
        case RULE:
            return EDIT_RULE_ICON;
        case TYPE:
            return EDIT_TYPE_ICON;
        default:
            throw Exceptions.UNREACHABLE;
        }
    }

    /**
     * Returns the edit icon used for the label list
     * of a given resource kind.
     */
    public static Icon getListEditIcon(ResourceKind resource) {
        switch (resource) {
        case CONTROL:
        case HOST:
        case PROLOG:
        case GROOVY:
        case TYPE:
        case FORMAT:
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
    public static Icon getListIcon(ResourceKind resource) {
        switch (resource) {
        case CONTROL:
            return CONTROL_LIST_ICON;
        case HOST:
            return GRAPH_LIST_ICON;
        case PROLOG:
            return PROLOG_LIST_ICON;
        case FORMAT:
            return FORMAT_LIST_ICON;
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
    public static final Icon ARROW_OPEN_UP_ICON = createIcon("arrow-open-up.gif");
    /** Transparent open down-arrow icon. */
    public static final Icon ARROW_OPEN_DOWN_ICON = createIcon("arrow-open-down.gif");
    /** Classic (simple) down-arrow icon. */
    public static final Icon ARROW_SIMPLE_DOWN_ICON = createIcon("arrow-simple-down.gif");
    /** Classic (simple) down-arrow icon. */
    public static final Icon ARROW_SIMPLE_LEFT_ICON = createIcon("arrow-wide-left.gif");
    /** Classic (simple) down-arrow icon. */
    public static final Icon ARROW_SIMPLE_RIGHT_ICON = createIcon("arrow-wide-right.gif");
    /** Classic (simple) up-arrow icon. */
    public static final Icon ARROW_SIMPLE_UP_ICON = createIcon("arrow-simple-up.gif");
    /** Cancel action icon. */
    public static final Icon CANCEL_ICON = createIcon("cancel.gif");
    /** Compass icon. */
    public static final Icon COMPASS_ICON = createIcon("compass.gif");
    /** Control automaton preview icon. */
    public static final Icon CONTROL_MODE_ICON = createIcon("control-mode.gif");
    /** Icon for Control Panel. */
    public static final Icon CONTROL_FRAME_ICON = createIcon("control-frame.gif");
    /** Icon for Control Files. */
    public static final Icon CONTROL_FILE_ICON = createIcon("control-file.gif");
    /** Small icon for control programs, as shown in the control list. */
    public static final Icon CONTROL_LIST_ICON = createIcon("control-file.gif");
    /** Copy action icon. */
    public static final Icon COPY_ICON = createIcon("copy.gif");
    /** Cut action icon. */
    public static final Icon CUT_ICON = createIcon("cut.gif");
    /** Delete action icon. */
    public static final Icon DELETE_ICON = createIcon("delete.gif");
    /** Disable action icon. */
    public static final Icon DISABLE_ICON = createIcon("disable.gif");
    /** Special icon denoting choice e/a. */
    public static final Icon E_A_CHOICE_ICON = createIcon("e-a-choice.gif");
    /** Empty icon. */
    public static final Icon EMPTY_ICON = createIcon("");
    /** Collapse all icon. */
    public static final Icon COLLAPSE_ALL_ICON = createIcon("collapse-all.gif");
    /** Small icon for condition rules, as shown in the rule tree. */
    public static final Icon CONDITION_TREE_ICON = createIcon("rule-condition.gif");
    /** Small icon for injective condition rules, as shown in the rule tree. */
    public static final Icon CONDITION_I_TREE_ICON = createIcon("rule-condition-I.gif");
    /** Edge action icon. */
    public static final Icon EDGE_ICON = createIcon("edge.gif");
    /** Edit action icon. */
    public static final Icon EDIT_ICON = createIcon("edit.gif");
    /** Control edit action icon. */
    public static final Icon EDIT_CONTROL_ICON = createIcon("edit-C.gif");
    /** Graph edit action icon. */
    public static final Icon EDIT_GRAPH_ICON = createIcon("edit-G.gif");
    /** Rule edit action icon. */
    public static final Icon EDIT_RULE_ICON = createIcon("edit-R.gif");
    /** Type edit action icon. */
    public static final Icon EDIT_TYPE_ICON = createIcon("edit-T.gif");
    /** Prolog edit action icon. */
    public static final Icon EDIT_PROLOG_ICON = createIcon("edit-P.gif");
    /** State edit action icon. */
    public static final Icon EDIT_STATE_ICON = createIcon("edit-S.gif");
    /** Wide edit action icon. */
    public static final Icon EDIT_WIDE_ICON = createIcon("edit-wide.gif");
    /** Enable action icon. */
    public static final Icon ENABLE_ICON = createIcon("enable.gif");
    /** Enable uniquely action icon. */
    public static final Icon ENABLE_UNIQUE_ICON = createIcon("enable_unique.gif");
    /** Error icon. */
    public static final Icon ERROR_ICON = createIcon("error.png");
    /** Export action icon. */
    public static final Icon EXPORT_ICON = createIcon("export.gif");
    /** Small icon for forbidden condition rules, as shown in the rule tree. */
    public static final Icon FORBIDDEN_TREE_ICON = createIcon("rule-forbidden.gif");
    /** Small icon for injective forbidden condition rules, as shown in the rule tree. */
    public static final Icon FORBIDDEN_I_TREE_ICON = createIcon("rule-forbidden-I.gif");
    /** Icon for Format Panel. */
    public static final Icon FORMAT_FRAME_ICON = createIcon("format-frame.gif");
    /** Icon for Format Files. */
    public static final Icon FORMAT_FILE_ICON = createIcon("format-file.gif");
    /** Icon for to appear in the format list. */
    public static final Icon FORMAT_LIST_ICON = createIcon("format-file.gif");
    /** Icon for restart movement. */
    public static final Icon GO_PREVIOUS_ICON = createIcon("go-previous.gif");
    /** Icon for fast-forward movement. */
    public static final Icon GO_FORWARD_ICON = createIcon("go-forward.gif");
    /** Icon for single-step movement. */
    public static final Icon GO_NEXT_ICON = createIcon("go-next.gif");
    /** Icon for normal forward movement. */
    public static final Icon GO_START_ICON = createIcon("go-start.gif");
    /** Icon for stopping movement. */
    public static final Icon GO_STOP_ICON = createIcon("go-stop.gif");
    /** Icon for fast-backward movement. */
    public static final Icon GO_REWIND_ICON = createIcon("go-rewind.gif");
    /** Icon for GPS folders. */
    public static final Icon GPS_FOLDER_ICON = createIcon("gps.gif");
    /** Icon for compressed GPS folders. */
    public static final Icon GPS_COMPRESSED_FOLDER_ICON = createIcon("gps-compressed.png");
    /** GROOVE project icon in 16x16 format. */
    public static final Icon GROOVE_ICON_16x16 = createIcon("groove-g-16x16.gif");
    /** Icon for graph (GXL or GST) files. */
    public static final Icon GRAPH_FILE_ICON = createIcon("graph-file.gif");
    /** Icon for the state panel of the simulator. */
    public static final Icon GRAPH_FRAME_ICON = createIcon("graph-frame.gif");
    /** Icon for graph with emphasised match. */
    public static final Icon GRAPH_MATCH_ICON = createIcon("graph-match.gif");
    /** Icon for graph as shown in the host graph list. */
    public static final Icon GRAPH_LIST_ICON = createIcon("graph-small.gif");
    /** Graph editing mode icon. */
    public static final Icon GRAPH_MODE_ICON = createIcon("graph-mode.gif");
    /** Icon for snap to grid action. */
    public static final Icon GRID_ICON = createIcon("grid.gif");
    /** Icon for Groovy Panel. */
    public static final Icon GROOVY_FRAME_ICON = createIcon("groovy-frame.gif");
    /** Icon for Groovy Files. */
    public static final Icon GROOVY_FILE_ICON = createIcon("groovy-file.gif");
    /** Small icon for scripts, as shown in the Groovy list. */
    public static final Icon GROOVY_LIST_ICON = createIcon("groovy-file.gif");
    /** Icon in the shape of an open hand, to be used as cursor. */
    public static final Icon HAND_OPEN_CURSOR_ICON = createIcon("hand-open.gif");
    /** Icon in the shape of an open hand. */
    public static final Icon HAND_OPEN_ICON = createIcon("hand-open-small.gif");
    /** Icon in the shape of a closed hand. */
    public static final Icon HAND_CLOSED_ICON = createIcon("hand-closed.gif");
    /** Icon for hiding lts. */
    public static final Icon HIDE_LTS_ICON = createIcon("hide-lts.png");
    /** Icon for filtering the LTS. */
    public static final Icon FILTER_LTS_ICON = createIcon("filter-lts.png");
    /** Import action icon. */
    public static final Icon IMPORT_ICON = createIcon("import.gif");
    /** Small icon for invariant condition rules, as shown in the rule tree. */
    public static final Icon INVARIANT_TREE_ICON = createIcon("rule-invariant.gif");
    /** Small icon for injective invariant condition rules, as shown in the rule tree. */
    public static final Icon INVARIANT_I_TREE_ICON = createIcon("rule-invariant-I.gif");
    /** Icon for the layout action. */
    public static final Icon LAYOUT_ICON = createIcon("layout.gif");
    /** Icon for the LTS panel of the simulator. */
    public static final Icon LTS_FRAME_ICON = createIcon("lts-frame.gif");
    /** LTS tab icon. */
    public static final Icon LTS_MODE_ICON = createIcon("lts-mode.gif");
    /** Icon for a New action. */
    public static final Icon NEW_ICON = createIcon("new.gif");
    /** Icon for a New Graph action. */
    public static final Icon NEW_GRAPH_ICON = createIcon("new-G.gif");
    /** Icon for a New Rule action. */
    public static final Icon NEW_RULE_ICON = createIcon("new-R.gif");
    /** Icon for a New Type action. */
    public static final Icon NEW_TYPE_ICON = createIcon("new-T.gif");
    /** Icon for a New Control action. */
    public static final Icon NEW_CONTROL_ICON = createIcon("new-C.gif");
    /** Icon for a New Prolog action. */
    public static final Icon NEW_PROLOG_ICON = createIcon("new-P.gif");
    /** Open action icon. */
    public static final Icon OPEN_ICON = createIcon("open.gif");
    /** Paste action icon. */
    public static final Icon PASTE_ICON = createIcon("paste.gif");
    /** Pin icon. */
    public static final Icon PIN_ICON = createIcon("pin.gif");
    /** Preview action icon. */
    public static final Icon PREVIEW_ICON = createIcon("preview.gif");
    /** Icon for Prolog Panel. */
    public static final Icon PROLOG_FRAME_ICON = createIcon("prolog-frame.gif");
    /** Icon for Prolog Files. */
    public static final Icon PROLOG_FILE_ICON = createIcon("prolog-file.gif");
    /** Small icon for production rules, as shown in the prolog list. */
    public static final Icon PROLOG_LIST_ICON = createIcon("prolog-file.gif");
    /** Small icon for puzzle piece. */
    public static final Icon PUZZLE_ICON = createIcon("puzzle.gif");
    /** Small icon for C-indexed puzzle piece. */
    public static final Icon PUZZLE_C_ICON = createIcon("puzzle-C.gif");
    /** Small icon for R-indexed puzzle piece. */
    public static final Icon PUZZLE_R_ICON = createIcon("puzzle-R.gif");
    /** Icon for Properties Panel. */
    public static final Icon PROPERTIES_FRAME_ICON = createIcon("properties-frame.gif");
    /** Redo action icon. */
    public static final Icon REDO_ICON = createIcon("redo.gif");
    /** Small icon for injective production rules, as shown in the rule tree. */
    public static final Icon RULE_I_TREE_ICON = createIcon("rule-standard-I.gif");
    /** Small icon for production rules, as shown in the rule tree. */
    public static final Icon RULE_TREE_ICON = createIcon("rule-standard.gif");
    /** Small icon for transactional rules, as shown in the rule tree. */
    public static final Icon RECIPE_TREE_ICON = createIcon("rule-recipe.gif");
    /** Icon for rule (GPR) files. */
    public static final Icon RULE_FILE_ICON = createIcon("rule-file.gif");
    /** Icon for the rule panel of the simulator. */
    public static final Icon RULE_FRAME_ICON = createIcon("rule-frame.gif");
    /** Rule editing mode icon. */
    public static final Icon RULE_MODE_ICON = createIcon("rule-mode.gif");
    /** Save action icon. */
    public static final Icon SAVE_ICON = createIcon("save.gif");
    /** Save-as action icon. */
    public static final Icon SAVE_AS_ICON = createIcon("saveas.gif");
    /** Select action icon. */
    public static final Icon SELECT_ICON = createIcon("select.gif");
    /** Search action icon. */
    public static final Icon SEARCH_ICON = createIcon("search.gif");
    /** Rename action icon. */
    public static final Icon RENAME_ICON = createIcon("rename.gif");
    /** Absent state icon. */
    public static final Icon STATE_ABSENT_ICON = createIcon("state-absent.gif");
    /** Closed state icon. */
    public static final Icon STATE_CLOSED_ICON = createIcon("state-closed.gif");
    /** Closed state icon. */
    public static final Icon STATE_FINAL_ICON = createIcon("state-final.gif");
    /** Icon for the state panel of the simulator. */
    public static final Icon STATE_FRAME_ICON = createIcon("state-frame.gif");
    /** Internal state icon. */
    public static final Icon STATE_INTERNAL_ICON = createIcon("state-internal.gif");
    /** Absent internal state icon. */
    public static final Icon STATE_INTERNAL_ABSENT_ICON = createIcon("state-internal-absent.gif");
    /** State display mode icon. */
    public static final Icon STATE_MODE_ICON = createIcon("state-mode.gif");
    /** Open state icon. */
    public static final Icon STATE_OPEN_ICON = createIcon("state-open.gif");
    /** Closed state icon. */
    public static final Icon STATE_RESULT_ICON = createIcon("state-result.gif");
    /** Start state icon. */
    public static final Icon STATE_START_ICON = createIcon("state-start.gif");
    /** Transient state icon. */
    public static final Icon STATE_TRANSIENT_ICON = createIcon("state-transient.gif");
    /** Icon for type (GTY) files. */
    public static final Icon TYPE_FILE_ICON = createIcon("type-file.gif");
    /** Icon for Type Panel. */
    public static final Icon TYPE_FRAME_ICON = createIcon("type-frame.gif");
    /** Type editing mode icon. */
    public static final Icon TYPE_LIST_ICON = createIcon("type-small.gif");
    /** Type editing mode icon. */
    public static final Icon TYPE_MODE_ICON = createIcon("type-mode.gif");
    /** Undo action icon. */
    public static final Icon UNDO_ICON = createIcon("undo.gif");
    /** Icon in the shape of a + magnifying class. */
    public static final Icon ZOOM_IN_ICON = createIcon("zoomin.gif");
    /** Icon in the shape of a - magnifying class. */
    public static final Icon ZOOM_OUT_ICON = createIcon("zoomout.gif");

    /** Custom cursor in the shape of an open hand. */
    public static final Cursor HAND_OPEN_CURSOR = createCursor("Open Hand", HAND_OPEN_CURSOR_ICON);
    /** Custom cursor in the shape of a closed hand. */
    public static final Cursor HAND_CLOSED_CURSOR = createCursor("Closed Hand", HAND_CLOSED_ICON);

    /** Creates a named cursor from a given file. */
    static private Icon createIcon(String filename) {
        return new Icon(filename);
    }

    /** Creates a named cursor from a given file. */
    static private Cursor createCursor(String name, Icon icon) {
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

    /** Class wrapping an icon that enables lazy loading, improving startup time. */
    public static class Icon {
        /** Creates an instance with a given resource name. */
        public Icon(String name) {
            this.name = name;
        }

        /** Returns the resource name of this icon. */
        public String getName() {
            return this.name;
        }

        private final String name;

        /** Lazily loads and returns the image icon wrapped in this object. */
        public ImageIcon getIcon() {
            if (this.imageIcon == null) {
                this.imageIcon =
                    this.name.isEmpty() ? new ImageIcon() : new ImageIcon(
                        Groove.getResource(this.name));
            }
            return this.imageIcon;
        }

        /** Lazily loads and returns the image of this icon. */
        public Image getImage() {
            return getIcon().getImage();
        }

        private ImageIcon imageIcon;
    }
}
