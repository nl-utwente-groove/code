/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.gui.tree;

import static nl.utwente.groove.io.HTMLConverter.HTML_TAG;
import static nl.utwente.groove.io.HTMLConverter.STRONG_TAG;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.tree.DefaultMutableTreeNode;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.graph.Label;
import nl.utwente.groove.gui.jgraph.JAttr;
import nl.utwente.groove.gui.jgraph.LTSJGraph;
import nl.utwente.groove.gui.tree.LTSEntry.Type;
import nl.utwente.groove.lts.GTS;

/**
 * Label tree for LTS-based labels.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class LTSTree extends LabelTree<GTS> {
    /** Constructs a tree for a given graph. */
    public LTSTree(LTSJGraph jGraph) {
        super(jGraph, true);
        this.headerNodes = new EnumMap<>(LTSEntry.Type.class);
        for (var entryType : LTSEntry.Type.values()) {
            this.headerNodes.put(entryType, new HeaderNode(entryType));
        }
    }

    @Override
    LTSFilter getFilter() {
        var result = this.filter;
        if (result == null) {
            this.filter = result = new LTSFilter();
        }
        return result;
    }

    private @Nullable LTSFilter filter;

    /** Mapping from entry types to (fixed) header nodes. */
    private final Map<LTSEntry.Type,HeaderNode> headerNodes;

    @Override
    Collection<? extends TreeNode> fillTree() {
        boolean headers = false;
        var typedEntries = new EnumMap<LTSEntry.Type,Set<LTSEntry>>(LTSEntry.Type.class);
        for (var entryType : LTSEntry.Type.values()) {
            this.headerNodes.get(entryType).removeAllChildren();
            typedEntries.put(entryType, new TreeSet<>());
        }
        for (var entry : getFilter().getEntries()) {
            if (getFilter().hasJCells(entry)) {
                headers |= entry.getType() != Type.GRAPH_CONDITION;
                typedEntries.get(entry.getType()).add(entry);
            }
        }
        if (headers) {
            var result = new ArrayList<HeaderNode>();
            for (var e : typedEntries.entrySet()) {
                if (!e.getValue().isEmpty()) {
                    var header = this.headerNodes.get(e.getKey());
                    getTopNode().add(header);
                    result.add(header);
                    fillTree(header, e.getValue());
                }
            }
            return result;
        } else {
            return fillTree(getTopNode(), typedEntries.get(Type.GRAPH_CONDITION));
        }
    }

    private Collection<TreeNode> fillTree(DefaultMutableTreeNode top, Set<LTSEntry> entries) {
        List<TreeNode> result = new ArrayList<>();
        for (LabelEntry entry : entries) {
            LabelTreeNode labelNode = new LabelTreeNode(this, entry, true);
            top.add(labelNode);
        }
        return result;
    }

    /**
     * If the object to be displayed is a {@link Label}, this implementation
     * returns an HTML-formatted string with the text of the label.
     */
    @Override
    public String convertValueToText(@Nullable Object value, boolean selected, boolean expanded,
                                     boolean leaf, int row, boolean hasFocus) {
        if (value instanceof LabelTreeNode labelNode) {
            LabelEntry entry = labelNode.getEntry();
            return HTML_TAG.on(getText(entry)).toString();
        } else if (value instanceof HeaderNode header) {
            return HTML_TAG.on(STRONG_TAG.on(header.getText()));
        } else {
            return super.convertValueToText(value, selected, expanded, leaf, row, hasFocus);
        }
    }

    @Override
    protected void paintComponent(@Nullable Graphics g) {
        super.paintComponent(g);
        if (getJGraph().getSimulatorModel().hasAbsentState()) {
            JAttr.paintHatch(this, g);
        }
    }

    /** Heads the class of displayed labels. */
    class HeaderNode extends TreeNode {
        HeaderNode(LTSEntry.Type entryType) {
            this.entryType = entryType;
        }

        private final LTSEntry.Type entryType;

        /**
         * Returns the text of this header node.
         */
        public String getText() {
            return this.entryType.getName();
        }

        @Override
        public boolean hasCheckbox() {
            return false;
        }

        @Override
        public boolean isSelected() {
            return false;
        }

        @Override
        public void setSelected(boolean selected) {
            // cannot be invoked; does nothing
        }

    }
}
