/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
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
package groove.io;

import groove.graph.GraphFactory;
import groove.trans.RuleNameLabel;
import groove.trans.SystemProperties;
import groove.view.aspect.AspectGraph;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

/**
 * Implementation based on {@link AspectGraph} representations of the rules and
 * graphs, and using an {@link AspectualViewGps} as persistent storage.
 * @author Arend Rensink
 * @version $Revision $
 */
public class AspectualSource extends Observable implements
        RuleSystemSource<AspectGraph,AspectGraph> {

    /**
     * Constructs a source object.
     * @param layouted if <code>true</code>, loads and saves graph layout.
     */
    public AspectualSource(boolean layouted) {
        this.marshaller =
            createGraphMarshaller(GraphFactory.getInstance(), true);
    }

    @Override
    public AspectGraph deleteGraph(String name)
        throws UnsupportedOperationException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AspectGraph deleteRule(String name)
        throws UnsupportedOperationException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String,String> getControls() {
        return Collections.unmodifiableMap(this.controlMap);
    }

    @Override
    public Map<String,AspectGraph> getGraphs() {
        return Collections.unmodifiableMap(this.graphMap);
    }

    @Override
    public SystemProperties getProperties() {
        return this.properties;
    }

    @Override
    public Map<RuleNameLabel,AspectGraph> getRules() {
        return Collections.unmodifiableMap(this.ruleMap);
    }

    @Override
    public String putControl(String name, String control)
        throws UnsupportedOperationException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AspectGraph putGraph(AspectGraph graph)
        throws UnsupportedOperationException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void putProperties(SystemProperties properties)
        throws UnsupportedOperationException {
        // TODO Auto-generated method stub

    }

    @Override
    public AspectGraph putRule(AspectGraph rule)
        throws UnsupportedOperationException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AspectGraph renameGraph(String oldName, String newName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AspectGraph renameRule(String oldName, String newName) {
        // TODO Auto-generated method stub
        return null;
    }

    /** Callback factory method for creating a graph marshaller. */
    protected Xml<AspectGraph> createGraphMarshaller(GraphFactory graphFactory,
            boolean layouted) {
        if (layouted) {
            return new AspectGxl(new LayedOutXml(graphFactory));
        } else {
            return new AspectGxl(new DefaultGxl(graphFactory));
        }
    }

    /** The name-to-rule map of the source. */
    private final Map<RuleNameLabel,AspectGraph> ruleMap =
        new HashMap<RuleNameLabel,AspectGraph>();
    /** The name-to-graph map of the source. */
    private final Map<String,AspectGraph> graphMap =
        new HashMap<String,AspectGraph>();
    /** The name-to-control-program map of the source. */
    private final Map<String,String> controlMap = new HashMap<String,String>();
    /** The system properties object of the source. */
    private SystemProperties properties;
    /** The graph marshaller used for retrieving rule and graph files. */
    private final Xml<AspectGraph> marshaller;
}
