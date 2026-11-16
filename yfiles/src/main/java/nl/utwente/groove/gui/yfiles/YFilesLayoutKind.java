/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2026
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
 *
 * $Id$
 */
package nl.utwente.groove.gui.yfiles;

import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;

import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.LayoutOrientation;
import com.yworks.yfiles.layout.MultiStageLayout;
import com.yworks.yfiles.layout.circular.CircularLayout;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.layout.organic.OrganicLayout;
import com.yworks.yfiles.layout.orthogonal.OrthogonalLayout;
import com.yworks.yfiles.layout.radial.RadialLayout;
import com.yworks.yfiles.layout.tree.BalloonLayout;
import com.yworks.yfiles.layout.tree.RootNodePolicy;
import com.yworks.yfiles.layout.tree.TreeLayout;
import com.yworks.yfiles.layout.tree.TreeReductionStage;

import nl.utwente.groove.util.AIGenerated;

/**
 * The yFiles layout algorithms offered in the layout palette, each with the
 * settings shown for it in the layout dialog. A kind creates a fresh, configured
 * algorithm per layouter instance; the settings panel works on that instance.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
enum YFilesLayoutKind {
    /** Layered layout, with most edges pointing in the main direction. */
    HIERARCHIC("Hierarchic") {
        @Override
        HierarchicLayout newAlgorithm() {
            return new HierarchicLayout();
        }

        @Override
        void addSettings(LayoutSettingsPanel panel, ILayoutAlgorithm algorithm) {
            var layout = (HierarchicLayout) algorithm;
            addOrientation(panel, layout);
            panel
                .addSpinner("Layer distance", layout.getMinimumLayerDistance(), 0, 500, 5,
                            layout::setMinimumLayerDistance);
            panel
                .addSpinner("Node distance", layout.getNodeToNodeDistance(), 0, 500, 5,
                            layout::setNodeToNodeDistance);
            panel
                .addCheckBox("Orthogonal edges", layout.isOrthogonalRoutingEnabled(),
                             layout::setOrthogonalRoutingEnabled);
        }
    },
    /** Force-directed layout with straight edges of uniform length. */
    ORGANIC("Organic") {
        @Override
        OrganicLayout newAlgorithm() {
            var result = new OrganicLayout();
            result.setDeterministicModeEnabled(true);
            result.setNodeSizeConsiderationEnabled(true);
            result.setNodeOverlapsAllowed(false);
            result.setPreferredEdgeLength(PREFERRED_EDGE_LENGTH);
            result.setMinimumNodeDistance(MINIMUM_NODE_DISTANCE);
            return result;
        }

        @Override
        void addSettings(LayoutSettingsPanel panel, ILayoutAlgorithm algorithm) {
            var layout = (OrganicLayout) algorithm;
            panel
                .addSpinner("Preferred edge length", layout.getPreferredEdgeLength(), 1, 1000,
                            5, layout::setPreferredEdgeLength);
            panel
                .addSpinner("Minimum node distance", layout.getMinimumNodeDistance(), 0, 500,
                            5, layout::setMinimumNodeDistance);
            panel
                .addSpinner("Compactness", layout.getCompactnessFactor(), 0, 1, 0.1,
                            layout::setCompactnessFactor);
            panel
                .addCheckBox("Deterministic", layout.isDeterministicModeEnabled(),
                             layout::setDeterministicModeEnabled);
        }
    },
    /** Layout with horizontal and vertical edge segments only. */
    ORTHOGONAL("Orthogonal") {
        @Override
        OrthogonalLayout newAlgorithm() {
            return new OrthogonalLayout();
        }

        @Override
        void addSettings(LayoutSettingsPanel panel, ILayoutAlgorithm algorithm) {
            var layout = (OrthogonalLayout) algorithm;
            panel
                .addSpinner("Grid spacing", layout.getGridSpacing(), 1, 200, 1,
                            value -> layout.setGridSpacing((int) value));
            // only the styles that keep the node sizes: the canvas derives them from the text
            var styles = List
                .of(com.yworks.yfiles.layout.orthogonal.LayoutStyle.NORMAL,
                    com.yworks.yfiles.layout.orthogonal.LayoutStyle.FIXED_BOX,
                    com.yworks.yfiles.layout.orthogonal.LayoutStyle.FIXED_MIXED);
            panel
                .addChoice("Style", List.of("Normal", "Box", "Mixed"),
                           Math.max(0, styles.indexOf(layout.getLayoutStyle())),
                           index -> layout.setLayoutStyle(styles.get(index)));
        }
    },
    /** Tree layout; edges outside a spanning tree are drawn straight. */
    TREE("Tree") {
        @Override
        ILayoutAlgorithm newAlgorithm() {
            return reduceToTree(new TreeLayout());
        }

        @Override
        void addSettings(LayoutSettingsPanel panel, ILayoutAlgorithm algorithm) {
            addOrientation(panel, (MultiStageLayout) core(algorithm));
        }
    },
    /** Tree layout with subtrees placed radially around their roots. */
    BALLOON("Balloon") {
        @Override
        ILayoutAlgorithm newAlgorithm() {
            return reduceToTree(new BalloonLayout());
        }

        @Override
        void addSettings(LayoutSettingsPanel panel, ILayoutAlgorithm algorithm) {
            var layout = (BalloonLayout) core(algorithm);
            var policies = List
                .of(RootNodePolicy.DIRECTED_ROOT, RootNodePolicy.CENTER_ROOT,
                    RootNodePolicy.WEIGHTED_CENTER_ROOT);
            panel
                .addChoice("Root", List.of("Source", "Centre", "Weighted centre"),
                           Math.max(0, policies.indexOf(layout.getRootNodePolicy())),
                           index -> layout.setRootNodePolicy(policies.get(index)));
            panel
                .addSpinner("Minimum edge length", layout.getMinimumEdgeLength(), 1, 500, 5,
                            value -> layout.setMinimumEdgeLength((int) value));
            panel
                .addSpinner("Compactness", layout.getCompactnessFactor(), 0.1, 0.9, 0.1,
                            layout::setCompactnessFactor);
        }
    },
    /** Nodes on circles, emphasising the biconnected components. */
    CIRCULAR("Circular") {
        @Override
        CircularLayout newAlgorithm() {
            return new CircularLayout();
        }

        @Override
        void addSettings(LayoutSettingsPanel panel, ILayoutAlgorithm algorithm) {
            var layout = (CircularLayout) algorithm;
            var styles = List
                .of(com.yworks.yfiles.layout.circular.LayoutStyle.BCC_COMPACT,
                    com.yworks.yfiles.layout.circular.LayoutStyle.BCC_ISOLATED,
                    com.yworks.yfiles.layout.circular.LayoutStyle.SINGLE_CYCLE);
            panel
                .addChoice("Style", List.of("Compact", "Isolated", "Single cycle"),
                           Math.max(0, styles.indexOf(layout.getLayoutStyle())),
                           index -> layout.setLayoutStyle(styles.get(index)));
        }
    },
    /** Nodes on concentric circles around a centre. */
    RADIAL("Radial") {
        @Override
        RadialLayout newAlgorithm() {
            return new RadialLayout();
        }

        @Override
        void addSettings(LayoutSettingsPanel panel, ILayoutAlgorithm algorithm) {
            var layout = (RadialLayout) algorithm;
            panel
                .addSpinner("Layer spacing", layout.getLayerSpacing(), 0, 500, 5,
                            layout::setLayerSpacing);
            panel
                .addSpinner("Node distance", layout.getMinimumNodeToNodeDistance(), 0, 500, 5,
                            layout::setMinimumNodeToNodeDistance);
        }
    };

    /**
     * Constructs a layout kind with a given name, to which {@link #getDisplayName} appends
     * the word "Layout".
     */
    private YFilesLayoutKind(String name) {
        this.name = name;
    }

    /** The name of this kind, to which {@link #getDisplayName} appends the word "Layout". */
    private final String name;

    /** Returns the name under which the layouter appears in the palette. */
    String getDisplayName() {
        return this.name + " Layout";
    }

    /** Creates a fresh algorithm of this kind, with GROOVE's configuration. */
    abstract ILayoutAlgorithm newAlgorithm();

    /**
     * Adds the settings of an algorithm of this kind to a panel.
     * @param algorithm an algorithm created by {@link #newAlgorithm()}
     */
    abstract void addSettings(LayoutSettingsPanel panel, ILayoutAlgorithm algorithm);

    /** Adds the choice of the main layout direction of a multi-stage layout. */
    static void addOrientation(LayoutSettingsPanel panel, MultiStageLayout layout) {
        var orientations = List
            .of(LayoutOrientation.TOP_TO_BOTTOM, LayoutOrientation.BOTTOM_TO_TOP,
                LayoutOrientation.LEFT_TO_RIGHT, LayoutOrientation.RIGHT_TO_LEFT);
        panel
            .addChoice("Orientation",
                       List.of("Top to bottom", "Bottom to top", "Left to right", "Right to left"),
                       Math.max(0, orientations.indexOf(layout.getLayoutOrientation())),
                       index -> layout.setLayoutOrientation(orientations.get(index)));
    }

    /**
     * Wraps a tree layout in the stage that reduces a general graph to a tree,
     * drawing the edges outside the tree as straight lines.
     */
    static ILayoutAlgorithm reduceToTree(MultiStageLayout tree) {
        var result = new TreeReductionStage(tree);
        result.setNonTreeEdgeRouter(result.createStraightLineRouter());
        return result;
    }

    /** Returns the core layout of a tree reduction stage. */
    static ILayoutAlgorithm core(ILayoutAlgorithm algorithm) {
        return ((TreeReductionStage) algorithm).getCoreLayout();
    }

    /** Preferred edge length of the organic layout, as found suitable in the spike. */
    private static final double PREFERRED_EDGE_LENGTH = 80;
    /** Minimum node distance of the organic layout, as found suitable in the spike. */
    private static final double MINIMUM_NODE_DISTANCE = 30;
}
