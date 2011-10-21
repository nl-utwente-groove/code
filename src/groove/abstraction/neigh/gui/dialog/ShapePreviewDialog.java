/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.abstraction.neigh.gui.dialog;

import groove.abstraction.neigh.Abstraction;
import groove.abstraction.neigh.gui.jgraph.ShapeJGraph;
import groove.abstraction.neigh.gui.jgraph.ShapeJModel;
import groove.abstraction.neigh.shape.Shape;
import groove.gui.Simulator;
import groove.gui.dialog.GraphPreviewDialog;
import groove.gui.layout.LayoutKind;
import groove.gui.layout.LayouterItem;
import groove.trans.DefaultHostGraph;
import groove.trans.HostGraph;
import groove.util.Groove;

import java.io.File;
import java.io.IOException;

import com.jgraph.layout.tree.JGraphTreeLayout;

/**
 * Dialog for displaying shapes.
 * 
 * @author Eduardo Zambon
 */
public final class ShapePreviewDialog extends GraphPreviewDialog {

    /** Constructs a new dialog, for a given shape. */
    private ShapePreviewDialog(Simulator simulator, Shape shape) {
        super(simulator, shape);
        this.setTitle(shape.getName());
    }

    /**
     * Creates a dialog for the given shape, and sets it to visible.
     */
    public static void showShape(Shape shape) {
        showShape(null, shape);
    }

    /**
     * Creates a dialog for the given shape and (possibly {@code null}) 
     * simulator, and sets it to visible.
     */
    public static void showShape(Simulator simulator, Shape shape) {
        new ShapePreviewDialog(simulator, shape).setVisible(true);
    }

    @Override
    protected ShapeJGraph createJGraph() {
        ShapeJGraph jGraph = new ShapeJGraph(this.simulator);
        ShapeJModel model = jGraph.newModel();
        model.loadGraph(((Shape) this.graph).downcast());
        jGraph.setModel(model);
        // EDUARDO says: this is some fine tuning of the layout algorithm
        // that is better suited for shapes. Should be moved to some other place.
        /*LayouterItem layouter =
            LayoutKind.getLayouterItemProto(LayoutKind.HIERARCHICAL);
        JGraphHierarchicalLayout layout =
            (JGraphHierarchicalLayout) layouter.getLayout();
        layout.setOrientation(SwingConstants.NORTH);
        layout.setCompactLayout(false);*/
        /*LayouterItem layouter =
            LayoutKind.getLayouterItemProto(LayoutKind.FAST_ORGANIC);
        JGraphFastOrganicLayout layout =
            (JGraphFastOrganicLayout) layouter.getLayout();
        layout.setInitialTemp(200.0);
        layout.setForceConstant(200.0);*/
        LayouterItem layouter =
            LayoutKind.getLayouterItemProto(LayoutKind.BASIC_TREE);
        JGraphTreeLayout layout = (JGraphTreeLayout) layouter.getLayout();
        layout.setNodeDistance(50);
        layout.setLevelDistance(100.0);
        jGraph.setLayouter(layouter);
        jGraph.doGraphLayout();
        return jGraph;
    }

    // Test -------------------------------------------------------------------

    private static Shape createShape(File file) {
        HostGraph graph = createHostGraph(file);
        return Shape.createShape(graph);
    }

    private static HostGraph createHostGraph(File file) {
        HostGraph result = null;
        try {
            result = new DefaultHostGraph(Groove.loadGraph(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /** Test method. */
    public static void main(String args[]) {
        Abstraction.initialise();
        String DIRECTORY = "junit/samples/abs-test.gps/";
        File file = new File(DIRECTORY + "shape-build-test-0.gst");
        Shape shape = createShape(file);
        showShape(shape);
    }
}
