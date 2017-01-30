/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.gui.dialog.config;

import javax.swing.JPanel;

import groove.explore.config.BoundKind;
import groove.explore.config.CountKind;
import groove.explore.config.ExploreKey;
import groove.explore.config.FrontierSizeKind;
import groove.explore.config.GoalKind;
import groove.explore.config.HeuristicKind;
import groove.explore.config.MatchKind;
import groove.explore.config.SettingKey;
import groove.explore.config.SettingKind;
import groove.gui.dialog.ExploreConfigDialog;
import groove.util.Exceptions;

/**
 * Class that can create editor for a given exploration key,
 * or for a given key/value pair.
 * @author Arend Rensink
 * @version $Revision $
 */
public class EditorFactory {
    /** Constructs a factory for a given dialog. */
    public EditorFactory(ExploreConfigDialog dialog) {
        this.dialog = dialog;
    }

    /** Creates a settings editor for a given exploration key. */
    public SettingEditor createEditor(ExploreKey key) {
        switch (key) {
        case ALGEBRA:
            return new ButtonEditor(getDialog(), key, "Algebra for data values");
        case BOUND:
            return new ButtonEditor(getDialog(), key, "Exploration bound");
        case COST:
            return new ButtonEditor(getDialog(), key, "Transition cost");
        case EQUATE:
            return new ButtonEditor(getDialog(), key, "Condition for state collapse");
        case FRONTIER_SIZE:
            return new ButtonEditor(getDialog(), key, "Algebra for data values");
        case GOAL:
            return new ButtonEditor(getDialog(), key, "Acceptor");
        case HEURISTIC:
            return new ButtonEditor(getDialog(), key, "Heuristic");
        case MATCHER:
            return new ButtonEditor(getDialog(), key, "Match strategy");
        case PERSISTENCE:
            return new CheckBoxEditor(getDialog(), key, "Remember visited states");
        case RESULT_COUNT:
            return new ButtonEditor(getDialog(), key, "Result count");
        case RESULT_TYPE:
            return new ButtonEditor(getDialog(), key, "Result type");
        case SUCCESSOR:
            return new ButtonEditor(getDialog(), key, "Choice of successor");
        case TRAVERSE:
            return new ButtonEditor(getDialog(), key, "Traversal strategy");
        default:
            throw Exceptions.UNREACHABLE; // all cases covered
        }
    }

    /**
     * Creates a settings editor for a given combination of exploration key and
     * setting kind.
     */
    public SettingEditor createEditor(JPanel holder, ExploreKey key, SettingKey kind) {
        SettingEditor result;
        if (kind instanceof SettingKind) {
            // these keys do not have content, hence no holder
            result = new NullEditor(getDialog(), null, key, kind);
        } else {
            switch (key) {
            case BOUND:
                result = createBoundEditor(holder, (BoundKind) kind);
                break;
            case FRONTIER_SIZE:
                result = createFrontierSizeEditor(holder, (FrontierSizeKind) kind);
                break;
            case GOAL:
                result = createGoalEditor(holder, (GoalKind) kind);
                break;
            case HEURISTIC:
                result = createHeuristicEditor(holder, (HeuristicKind) kind);
                break;
            case RESULT_COUNT:
                result = createResultCountEditor(holder, (CountKind) kind);
                break;
            case MATCHER:
                result = createMatcherEditor(holder, (MatchKind) kind);
                break;
            //        case CHECKING:
            //            switch ((CheckingKind) kind) {
            //            case LTL_CHECK:
            //            case CTL_CHECK:
            //                result = new TextFieldEditor(getDialog(), holder, key, kind);
            //                break;
            //            default:
            //                result = null;
            //            }
            //            break;
            default:
                throw Exceptions.UNREACHABLE; // all cases covered
            }
            if (result == null) {
                result = new NullEditor(getDialog(), holder, key, kind);
            }
        }
        return result;
    }

    /**
     * Returns an editor for the {@link ExploreKey#BOUND} key and a certain setting key.
     */
    private SettingEditor createBoundEditor(JPanel holder, BoundKind kind) {
        SettingEditor result;
        switch (kind) {
        case COST:
        case COUNTS:
        case SIZE:
            result = new TextFieldEditor(getDialog(), holder, ExploreKey.BOUND, kind);
            break;
        case NONE:
            result = null;
            break;
        default:
            throw Exceptions.UNREACHABLE; // all cases covered
        }
        return result;
    }

    /**
     * Returns an editor for the {@link ExploreKey#FRONTIER_SIZE} key and a certain setting key.
     */
    private SettingEditor createFrontierSizeEditor(JPanel holder, FrontierSizeKind kind) {
        SettingEditor result;
        switch (kind) {
        case BEAM:
            result = new TextFieldEditor(getDialog(), holder, ExploreKey.FRONTIER_SIZE, kind);
            break;
        default:
            result = null;
        }
        return result;
    }

    /**
     * Returns an editor for the {@link ExploreKey#GOAL} key and a certain setting key.
     */
    private SettingEditor createGoalEditor(JPanel holder, GoalKind kind) {
        SettingEditor result;
        switch (kind) {
        case CONDITION:
        case CTL:
        case FORMULA:
        case LTL:
            result = new TextFieldEditor(getDialog(), holder, ExploreKey.GOAL, kind);
            break;
        case FINAL:
        case NONE:
            result = null;
            break;
        default:
            throw Exceptions.UNREACHABLE;
        }
        return result;
    }

    /**
     * Returns an editor for the {@link ExploreKey#HEURISTIC} key and a certain setting key.
     */
    private SettingEditor createHeuristicEditor(JPanel holder, HeuristicKind kind) {
        SettingEditor result;
        switch (kind) {
        case NEN:
        case NONE:
            result = null;
            break;
        default:
            throw Exceptions.UNREACHABLE;
        }
        return result;
    }

    /**
     * Returns an editor for the {@link ExploreKey#MATCHER} key and a certain setting key.
     */
    private SettingEditor createMatcherEditor(JPanel holder, MatchKind kind) {
        SettingEditor result;
        switch (kind) {
        case PLAN:
            result = new TextFieldEditor(getDialog(), holder, ExploreKey.MATCHER, kind);
            break;
        default:
            result = null;
        }
        return result;
    }

    /**
     * Returns an editor for the {@link ExploreKey#RESULT_COUNT} key and a certain setting key.
     */
    private SettingEditor createResultCountEditor(JPanel holder, CountKind kind) {
        SettingEditor result;
        switch (kind) {
        case COUNT:
            result = new TextFieldEditor(getDialog(), holder, ExploreKey.RESULT_COUNT, kind);
            break;
        default:
            result = null;
        }
        return result;
    }

    private ExploreConfigDialog getDialog() {
        return this.dialog;
    }

    private final ExploreConfigDialog dialog;
}
