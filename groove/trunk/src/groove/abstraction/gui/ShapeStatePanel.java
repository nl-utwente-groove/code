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
package groove.abstraction.gui;

import groove.abstraction.Shape;
import groove.abstraction.gui.jgraph.ShapeJGraph;
import groove.abstraction.gui.jgraph.ShapeJModel;
import groove.abstraction.lts.ShapeState;
import groove.gui.Simulator;
import groove.gui.StatePanel;
import groove.lts.GraphState;
import groove.util.Converter;

/**
 * EDUARDO: Comment this...
 * @author Eduardo Zambon
 */
public class ShapeStatePanel extends StatePanel {

    private ShapeJModel realJModel;
    private ShapeJGraph realJGraph;

    /** EDUARDO: Comment this... */
    public ShapeStatePanel(Simulator simulator) {
        super(simulator);
    }

    @Override
    protected String getStatusText() {
        StringBuilder result = new StringBuilder(super.getStatusText());
        Converter.HTML_TAG.off(result);
        result.append(" [Abstract mode view]");
        return Converter.HTML_TAG.on(result).toString();
    }

    @Override
    protected void refresh() {
        ShapeJModel jModel = this.getRealJModel();
        this.getRealJGraph().setEnabled(jModel != null);
        this.getRealJGraph().clearSelection();
        this.refreshStatus();
    }

    @Override
    public synchronized void setStateUpdate(GraphState state) {
        assert state instanceof ShapeState;
        Shape shape = ((ShapeState) state).getGraph();
        ShapeJModel model = new ShapeJModel(shape);
        ShapeJGraph jGraph = new ShapeJGraph(model);
        this.setRealJModel(model);
        this.setRealJGraph(jGraph);
        this.refreshStatus();
    }

    /** EDUARDO: Comment this... */
    public ShapeJModel getRealJModel() {
        return this.realJModel;
    }

    /** EDUARDO: Comment this... */
    public void setRealJModel(ShapeJModel model) {
        this.realJModel = model;
    }

    /** EDUARDO: Comment this... */
    public ShapeJGraph getRealJGraph() {
        return this.realJGraph;
    }

    /** EDUARDO: Comment this... */
    public void setRealJGraph(ShapeJGraph jGraph) {
        this.realJGraph = jGraph;
    }

}
