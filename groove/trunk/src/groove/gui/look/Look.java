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
package groove.gui.look;

import groove.grammar.aspect.AspectKind;
import groove.gui.look.VisualKey.Nature;
import groove.util.Colors;
import groove.util.NodeShape;

import java.awt.Color;
import java.awt.Font;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Graph element look values.
 * @author Arend Rensink
 * @version $Revision $
 */
public enum Look {
    /** Basic node and edge look. */
    BASIC(true) {
        @Override
        void init() {
            add(VisualKey.OPAQUE, true);
            add(VisualKey.NODE_SHAPE, NodeShape.ROUNDED);
            add(VisualKey.EDGE_TARGET_SHAPE, EdgeEnd.ARROW);
            add(VisualKey.FONT, Font.PLAIN);
        }
    },
    /** Bidirectional edge look change. */
    BIDIRECTIONAL {
        @Override
        void init() {
            add(VisualKey.EDGE_SOURCE_SHAPE, EdgeEnd.ARROW);
            // also add the target shape (again) to overrule the NODIFIED look
            add(VisualKey.EDGE_TARGET_SHAPE, EdgeEnd.ARROW);
        }
    },
    /** Look change for nodes displayed as edge points and their incoming edges. */
    NODIFIED(true) {
        @Override
        void init() {
            add(VisualKey.OPAQUE, true);
            add(VisualKey.NODE_SHAPE, NodeShape.ELLIPSE);
            add(VisualKey.EDGE_TARGET_SHAPE, EdgeEnd.NONE);
        }
    },
    /** Look change for edges without target decoration. */
    NO_ARROW() {
        @Override
        void init() {
            add(VisualKey.EDGE_TARGET_SHAPE, EdgeEnd.NONE);
        }
    },
    /** Look change for regular expression edges. */
    REGULAR() {
        @Override
        void init() {
            add(VisualKey.FONT, Font.ITALIC);
        }
    },
    /** Type node and edge look. */
    TYPE(true, BASIC) {
        @Override
        void init() {
            add(VisualKey.NODE_SHAPE, NodeShape.RECTANGLE);
        }
    },
    /** Subtype edge look. */
    SUBTYPE(true, TYPE) {
        @Override
        void init() {
            add(VisualKey.EDGE_TARGET_SHAPE, EdgeEnd.SUBTYPE);
        }

    },
    /** Composite edge look. */
    COMPOSITE(TYPE) {
        @Override
        void init() {
            add(VisualKey.EDGE_SOURCE_SHAPE, EdgeEnd.COMPOSITE);
        }

    },
    /** Abstract type node and edge look. */
    ABSTRACT(true, TYPE) {
        @Override
        void init() {
            add(VisualKey.DASH, Values.ABSTRACT_DASH);
            add(VisualKey.FONT, Font.ITALIC);
        }
    },
    /** Remark node and edge look. */
    REMARK(true, BASIC) {
        @Override
        void init() {
            add(VisualKey.FOREGROUND, Values.REMARK_FOREGROUND);
            add(VisualKey.BACKGROUND, Values.REMARK_BACKGROUND);
        }

    },
    /** Embargo node and edge look. */
    EMBARGO(true, BASIC) {
        @Override
        void init() {
            add(VisualKey.FOREGROUND, Values.EMBARGO_FOREGROUND);
            add(VisualKey.BACKGROUND, Values.EMBARGO_BACKGROUND);
            add(VisualKey.LINE_WIDTH, 5f);
            add(VisualKey.DASH, Values.EMBARGO_DASH);
            add(VisualKey.EDGE_TARGET_SHAPE, EdgeEnd.UNFILLED);
        }

    },
    /** Artificial connecting edges between disjoint NAC patterns. */
    CONNECT(true, EMBARGO) {
        @Override
        void init() {
            add(VisualKey.LINE_WIDTH, 4f);
            add(VisualKey.DASH, Values.CONNECT_DASH);
            add(VisualKey.EDGE_TARGET_SHAPE, EdgeEnd.NONE);
        }
    },
    /** Eraser node and edge look. */
    ERASER(true, BASIC) {
        @Override
        void init() {
            add(VisualKey.FOREGROUND, Values.ERASER_FOREGROUND);
            add(VisualKey.BACKGROUND, Values.ERASER_BACKGROUND);
            add(VisualKey.DASH, Values.ERASER_DASH);
        }

    },
    /** Creator node and edge look. */
    CREATOR(true, BASIC) {
        @Override
        void init() {
            add(VisualKey.FOREGROUND, Values.CREATOR_FOREGROUND);
            add(VisualKey.BACKGROUND, Values.CREATOR_BACKGROUND);
            add(VisualKey.LINE_WIDTH, 3f);
        }
    },
    /** Adder node and edge look. */
    ADDER(true, CREATOR, EMBARGO) {
        @Override
        void init() {
            add(VisualKey.LINE_WIDTH, 6f);
            add(VisualKey.INNER_LINE, Values.CREATOR_FOREGROUND);
        }
    },
    /** Quantifier nesting node and edge look. */
    NESTING(true, BASIC) {
        @Override
        void init() {
            add(VisualKey.DASH, Values.NESTED_DASH);
            add(VisualKey.EDGE_TARGET_SHAPE, EdgeEnd.SIMPLE);
        }

    },
    /** Product node look. */
    PRODUCT(false, BASIC) {
        @Override
        void init() {
            add(VisualKey.NODE_SHAPE, NodeShape.DIAMOND);
        }
    },
    /** Data node look. */
    DATA(false, BASIC) {
        @Override
        void init() {
            add(VisualKey.NODE_SHAPE, NodeShape.ELLIPSE);
        }
    },
    /** Transient control state look. */
    CTRL_TRANSIENT_STATE(true, BASIC) {
        @Override
        void init() {
            add(VisualKey.NODE_SHAPE, NodeShape.DIAMOND);
        }
    },
    /** Omega control transition. */
    CTRL_OMEGA_TRANS(true, BASIC) {
        @Override
        void init() {
            add(VisualKey.FOREGROUND, Color.RED);
        }
    },
    /** Control transition exiting a transient state. */
    CTRL_EXIT_TRANS(true, BASIC) {
        @Override
        void init() {
            add(VisualKey.EDGE_SOURCE_SHAPE, EdgeEnd.DOUBLE_LINE);
        }
    },
    /** Omega control transition exiting a transient state. */
    CTRL_OMEGA_EXIT_TRANS(true, CTRL_OMEGA_TRANS, CTRL_EXIT_TRANS),
    /** Closed state look. */
    STATE(true, BASIC) {
        @Override
        void init() {
            add(VisualKey.NODE_SHAPE, NodeShape.OVAL);
        }
    },
    /** Start state look. */
    START(true, STATE) {
        @Override
        void init() {
            add(VisualKey.FOREGROUND, Values.START_FOREGROUND);
            add(VisualKey.BACKGROUND, Values.START_BACKGROUND);
        }
    },
    /** Transition look. */
    TRANS(true, BASIC) {
        @Override
        void init() {
            add(VisualKey.EDGE_TARGET_SHAPE, EdgeEnd.SIMPLE);
        }
    },
    /** Change in look due to open state status. */
    OPEN() {
        @Override
        void init() {
            add(VisualKey.BACKGROUND, Values.OPEN_BACKGROUND);
        }

        @Override
        public void apply(VisualMap map) {
            boolean isStart = (map.getBackground() == Values.START_BACKGROUND);
            super.apply(map);
            if (isStart) {
                map.put(VisualKey.BACKGROUND, Values.START_OPEN_BACKGROUND,
                    false);
            }
        }
    },
    /** Change in look due to final state status. */
    FINAL() {
        @Override
        void init() {
            add(VisualKey.BACKGROUND, Values.FINAL_BACKGROUND);
        }
    },
    /** Change in look due to result state status. */
    RESULT() {
        @Override
        void init() {
            add(VisualKey.BACKGROUND, Values.RESULT_BACKGROUND);
        }
    },
    /** Change in look due to transient state/transition status. */
    TRANSIENT() {
        @Override
        void init() {
            add(VisualKey.NODE_SHAPE, NodeShape.DIAMOND);
            add(VisualKey.FOREGROUND, Values.TRANSIENT_COLOR);
        }
    },
    /** Change in look due to absent state/transition status. */
    ABSENT() {
        @Override
        void init() {
            add(VisualKey.DASH, Values.ABSENT_DASH);
        }
    },
    /** Change in look due to active state/transition status. */
    ACTIVE() {
        @Override
        void init() {
            add(VisualKey.FOREGROUND, Values.ACTIVE_COLOR);
            add(VisualKey.LINE_WIDTH, 3f);
        }

        @Override
        public void apply(VisualMap map) {
            boolean isTransient =
                (map.getForeground() == Values.TRANSIENT_COLOR);
            boolean isStart = (map.getForeground() == Values.START_FOREGROUND);
            super.apply(map);
            if (isTransient || isStart) {
                map.put(VisualKey.FOREGROUND, isTransient
                        ? Values.TRANSIENT_ACTIVE_COLOR
                        : Values.ACTIVE_START_COLOR, false);
            }
        }
    },
    /** Node and edges in a pattern graph. */
    PATTERN(true, BASIC) {
        @Override
        void init() {
            add(VisualKey.BACKGROUND, Values.NORMAL_BACKGROUND);
            add(VisualKey.DASH, Values.NESTED_DASH);
            add(VisualKey.INSET, 8);
            add(VisualKey.FONT, Font.PLAIN);
            add(VisualKey.NODE_SHAPE, NodeShape.RECTANGLE);
        }
    },
    /** Equivalence class of a neighbourhood shape. */
    EQUIV_CLASS(true, BASIC) {
        @Override
        void init() {
            add(VisualKey.OPAQUE, false);
            add(VisualKey.DASH, Values.NESTED_DASH);
            add(VisualKey.INSET, 8);
            add(VisualKey.NODE_SHAPE, NodeShape.RECTANGLE);
            add(VisualKey.FOREGROUND, Values.DEFAULT_FOREGROUND);
        }
    },
    /**
     * Grayed-out node or edge.
     * Graying out is visualised by transparency.
     */
    GRAYED_OUT {
        @Override
        void init() {
            add(VisualKey.FOREGROUND, Colors.findColor("200 200 200 100"));
            add(VisualKey.OPAQUE, false);
        }
    };

    private Look(Look... templates) {
        this(false, templates);
    }

    private Look(boolean structural, Look... templates) {
        this.structural = structural;
        this.visuals = new VisualMap();
        for (Look template : templates) {
            template.apply(this.visuals);
        }
        init();
        this.visuals.setFixed();
    }

    /** Callback method to initialise the attributes of this look. */
    void init() {
        // does nothing
    }

    /** Adds a derived or refreshable key-value pair to the attribute map of this look. */
    void add(VisualKey key, Object value) {
        assert key.getNature() == Nature.DERIVED
            || key.getNature() == Nature.REFRESHABLE;
        this.visuals.put(key, value, false);
    }

    /** Returns the attribute map associated with this look. */
    public VisualMap getVisuals() {
        return this.visuals;
    }

    /**
     * Apply this look to a given visual map.
     * Usually this means adding the look's visuals, but some
     * looks have a more subtle modifying effect. 
     * @param map the visual map to be modified
     */
    public void apply(VisualMap map) {
        map.putAll(getVisuals());
    }

    /**
     * Indicates if this is a structural look,
     * or an update on an existing look.
     */
    public boolean isStructural() {
        return this.structural;
    }

    private final VisualMap visuals;
    private final boolean structural;

    /** Returns the combined attributes for a sequence of looks. */
    public static VisualMap getVisualsFor(Set<Look> looks) {
        VisualMap result = looksMap.get(looks);
        if (result == null) {
            looksMap.put(looks, result = new VisualMap());
            for (Look look : looks) {
                look.apply(result);
            }
            result.setFixed();
        }
        return result;
    }

    /** Returns the look for a given aspect. */
    public static Look getLookFor(AspectKind aspect) {
        Look result = aspectLookMap.get(aspect);
        if (result == null) {
            result = BASIC;
        }
        return result;
    }

    private final static Map<Set<Look>,VisualMap> looksMap =
        new HashMap<Set<Look>,VisualMap>();

    private final static Map<AspectKind,Look> aspectLookMap =
        new EnumMap<AspectKind,Look>(AspectKind.class);

    static {
        aspectLookMap.put(AspectKind.REMARK, REMARK);
        aspectLookMap.put(AspectKind.CREATOR, CREATOR);
        aspectLookMap.put(AspectKind.EMBARGO, EMBARGO);
        aspectLookMap.put(AspectKind.ERASER, ERASER);
        aspectLookMap.put(AspectKind.CONNECT, CONNECT);
        aspectLookMap.put(AspectKind.CREATOR, CREATOR);
        aspectLookMap.put(AspectKind.ADDER, ADDER);
        aspectLookMap.put(AspectKind.FORALL, NESTING);
        aspectLookMap.put(AspectKind.FORALL_POS, NESTING);
        aspectLookMap.put(AspectKind.EXISTS, NESTING);
        aspectLookMap.put(AspectKind.EXISTS_OPT, NESTING);
        aspectLookMap.put(AspectKind.NESTED, NESTING);
        aspectLookMap.put(AspectKind.COMPOSITE, COMPOSITE);
        aspectLookMap.put(AspectKind.SUBTYPE, SUBTYPE);
        aspectLookMap.put(AspectKind.ABSTRACT, ABSTRACT);
        aspectLookMap.put(AspectKind.BOOL, DATA);
        aspectLookMap.put(AspectKind.INT, DATA);
        aspectLookMap.put(AspectKind.STRING, DATA);
        aspectLookMap.put(AspectKind.REAL, DATA);
        aspectLookMap.put(AspectKind.PRODUCT, PRODUCT);
    }
}
