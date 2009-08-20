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
 * $Id: AspectualView.java,v 1.8 2008-01-30 09:33:25 iovka Exp $
 */
package groove.view;

import groove.graph.DefaultLabel;
import groove.graph.Edge;
import groove.graph.Label;
import groove.graph.NodeEdgeMap;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectValue;

/**
 * View specialisation based on aspect graphs. Apart from the aspect graph
 * itself, an instance of this view maintains a map from the nodes of the aspect
 * graph to nodes in the model. This can be useful for traceability. The model
 * may for instance be a transformation rule, an attributed graph, or a graph
 * condition.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class AspectualView<Model> implements View<Model> {
    /**
     * Returns the aspect graph representation of this view.
     */
    abstract public AspectGraph getAspectGraph();

    /**
     * Returns a mapping from the nodes in the aspect graph view to the
     * corresponding nodes in the model that is being viewed.
     */
    abstract public NodeEdgeMap getMap();

    /**
     * Returns a parsed label for the aspect edge. The aspect edge has a
     * {@link DefaultLabel}, which can be turned into a special label depending
     * on the aspect values associated with the edge. If the aspect values do
     * not determine a label parser (through
     * {@link AspectValue#getLabelParser()}) then the default parser for this
     * view is used (see {@link #getDefaultLabelParser()}).
     * @param aspectEdge the edge for which we want the parsed label; not
     *        <code>null</code>
     * @return the parsed label for <code>aspectEdge</code>; not
     *         <code>null</code>
     * @throws FormatException if the aspect values yield conflicting parsers,
     *         or the parser throws an exception
     */
    protected Label parse(AspectEdge aspectEdge) throws FormatException {
        AspectValue parsingValue = null;
        LabelParser parser = null;
        for (AspectValue value : aspectEdge.getAspectMap().values()) {
            // find the parser for this aspect value
            LabelParser valueParser = value.getLabelParser();
            // set it as the label parser, or compare it with the previously
            // found parser
            if (parser == null) {
                parser = valueParser;
                parsingValue = value;
            } else if (valueParser != null && !valueParser.equals(parser)) {
                throw new FormatException(
                    "Conflicting aspect values '%s' and '%s' on edge %s",
                    parsingValue, value, aspectEdge);
            }
        }
        // use the default parser if none is found
        if (parser == null) {
            parser = getDefaultLabelParser();
        }
        // parse the label
        return parser.parse(aspectEdge.label());
    }

    /**
     * Returns a default label that is the unparsed version of a given label,
     * according to this view's default parser.
     */
    public DefaultLabel unparse(Label label) {
        return getDefaultLabelParser().unparse(label);
    }

    /**
     * Returns a default label that is the unparsed version of a given edge's
     * label, according to this view's default parser. Convenience method for
     * <code>unparseLabel(edge.label())</code>.
     */
    protected DefaultLabel unparse(Edge edge) {
        return unparse(edge.label());
    }

    /** Returns the default label parser for this particular view. */
    abstract protected LabelParser getDefaultLabelParser();
    //
    // /**
    // * Creates a view from a given aspect graph. Depending on the role of the
    // * graph, the result is an {@link AspectualRuleView} or an
    // * {@link AspectualGraphView}.
    // * @param aspectGraph the graph to create the view from
    // * @return a graph or rule view based on <code>aspectGraph</code>
    // * @see GraphInfo#getRole(GraphShape)
    // */
    // static public AspectualView<?> createView(AspectGraph aspectGraph) {
    // if (GraphInfo.hasRuleRole(aspectGraph)) {
    // return new AspectualRuleView(aspectGraph);
    // } else {
    // return new AspectualGraphView(aspectGraph, null);
    // }
    // }
}
