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
package groove.control.instance;

import groove.control.template.Stage;
import groove.control.template.Template;
import groove.graph.AGraph;
import groove.graph.GGraph;
import groove.graph.GraphRole;
import groove.graph.Node;
import groove.graph.plain.NodeSetOutEdgeMapGraph;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
public class Instance extends NodeSetOutEdgeMapGraph<Frame,Step> {
    /**
     * Instantiates a given template.
     */
    public Instance(Template template) {
        super(template.getName(), GraphRole.CTRL);
        this.template = template;
        this.normalFrameMap = new HashMap<Frame,Frame>();
    }

    /** Returns the template from which this control instance has been created. */
    public Template getTemplate() {
        return this.template;
    }

    private final Template template;

    /** Returns the next available frame number. */
    int getNextFrameNr() {
        return nodeCount();
    }

    @Override
    public Set<? extends Frame> nodeSet() {
        return this.normalFrameMap.keySet();
    }

    @Override
    public Set<Step> outEdgeSet(Node node) {
        return Collections.singleton(((Frame) node).getAttempt());
    }

    /** 
     * Adds a normalised frame with the given parameters to this instance,
     * and returns the frame.
     */
    public Frame addFrame(Stage stage, Frame nextFrame, Frame alsoFrame,
            Frame elseFrame) {
        Frame result =
            Frame.newInstance(this, stage, nextFrame, alsoFrame, elseFrame);
        Frame oldResult = this.normalFrameMap.put(result, result);
        if (oldResult != null) {
            result = oldResult;
        }
        return result;
    }

    public GGraph<Frame,Step> newGraph(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addNode(Frame node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addEdge(Step edge) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeEdge(Step edge) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeNode(Frame node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AGraph<Frame,Step> clone() {
        return new Instance(getTemplate());
    }

    private Map<Frame,Frame> normalFrameMap;
}
