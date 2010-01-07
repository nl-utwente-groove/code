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
package groove.view.aspect;

import groove.graph.DefaultLabel;
import groove.graph.Label;
import groove.util.ExprParser;
import groove.view.FormatException;
import groove.view.LabelParser;

/**
 * Graph aspect dealing with node types.
 * @author Arend
 * @version $Revision $
 */
public class TypeAspect extends AbstractAspect {
    /** Private constructor to create the singleton instance. */
    private TypeAspect() {
        super(NODE_TYPE_ASPECT_NAME);
    }

    @Override
    public void checkEdge(AspectEdge edge, AspectGraph graph)
        throws FormatException {
        if (isNodeType(edge) && !edge.source().equals(edge.opposite())) {
            throw new FormatException(
                "Node type label '%s' only allowed on self-edges", edge.label());
        }
    }

    /** Indicates if a given aspect edge stands for a node type. */
    public static boolean isNodeType(AspectEdge edge) {
        AspectValue value = edge.getValue(getInstance());
        return NODE_TYPE.equals(value);
    }

    /**
     * Returns the singleton instance of this aspect.
     */
    public static TypeAspect getInstance() {
        return instance;
    }

    /**
     * The singleton instance of this class.
     */
    static private final TypeAspect instance = new TypeAspect();
    /** Name of this aspect. */
    static public final String NODE_TYPE_ASPECT_NAME = "node type";
    /** Name of the node type aspect value. */
    static public final String NODE_TYPE_NAME = "type";
    /** Name of the node type aspect value. */
    static public final AspectValue NODE_TYPE;

    static {
        try {
            NODE_TYPE = instance.addEdgeValue(NODE_TYPE_NAME);
            NODE_TYPE.setLabelParser(NodeTypeLabelParser.getInstance());
            // incompatibilities
            instance.setIncompatible(NestingAspect.getInstance());
        } catch (FormatException exc) {
            throw new Error("Aspect '" + NODE_TYPE_ASPECT_NAME
                + "' cannot be initialised due to name conflict", exc);
        }
    }

    /** Label used for subtype edges in type graphs. */
    public static final String SUB_LABEL = "sub";

    /**
     * Parser constructing node type labels.
     */
    static private class NodeTypeLabelParser implements LabelParser {
        /** Private constructor for this singleton class. */
        private NodeTypeLabelParser() {
            // empty
        }

        @Override
        public Label parse(Label label) throws FormatException {
            String labelText = label.text();
            if (ExprParser.isIdentifier(labelText)) {
                return DefaultLabel.createLabel(labelText, true);
            } else {
                throw new FormatException(
                    "Node type label '%s' is not a valid identifier", labelText);
            }
        }

        @Override
        public DefaultLabel unparse(Label label) {
            return DefaultLabel.createLabel(label.text(), label.isNodeType());
        }

        /** Returns the singleton instance of this class. */
        static public NodeTypeLabelParser getInstance() {
            return instance;
        }

        /** Singleton instance of this class. */
        static private NodeTypeLabelParser instance = new NodeTypeLabelParser();
    }
}
