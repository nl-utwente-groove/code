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
import groove.graph.GraphInfo;
import groove.graph.Label;
import groove.view.FormatException;

/**
 * Graph aspect dealing with node types.
 * @author Arend
 * @version $Revision $
 */
public class TypeAspect extends AbstractAspect {
    /** Private constructor to create the singleton instance. */
    private TypeAspect() {
        super(TYPE_ASPECT_NAME);
    }

    @Override
    public void checkEdge(AspectEdge edge, AspectGraph graph)
        throws FormatException {
        if (!GraphInfo.hasTypeRole(graph)) {
            if (isSubtype(edge)) {
                throw new FormatException(
                    "%s-prefixed edges only allowed in type graphs", SUB_NAME,
                    edge);
            } else if (isAbstract(edge)) {
                throw new FormatException(
                    "%s-prefixed edges only allowed in type graphs", ABS_NAME,
                    edge);
            }
        }
    }

    /** Indicates if a given aspect edge is an abstract edge. */
    public static boolean isAbstract(AspectElement elem) {
        AspectValue value = elem.getValue(getInstance());
        return ABS.equals(value);
    }

    /** Indicates if a given aspect edge is a subtype edge. */
    public static boolean isSubtype(AspectEdge edge) {
        AspectValue value = edge.getValue(getInstance());
        return SUB.equals(value);
    }

    /** Indicates if a given aspect edge stands for a path. */
    public static boolean isPath(AspectEdge edge) {
        AspectValue value = edge.getValue(getInstance());
        return PATH.equals(value);
    }

    /** Indicates if a given aspect edge has an empty aspect value. */
    public static boolean isEmpty(AspectEdge edge) {
        AspectValue value = edge.getValue(getInstance());
        return EMPTY.equals(value);
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
    static public final String TYPE_ASPECT_NAME = "type";
    //    /** The node type aspect value. */
    //    static public final AspectValue NODE_TYPE;
    //    /** Name of the flag aspect value. */
    //    static public final AspectValue FLAG;
    /** Name of the path aspect value. */
    static public final String PATH_NAME = "path";
    /** The path aspect value. */
    static public final AspectValue PATH;
    /** Name of the abstract aspect value. */
    static public final String ABS_NAME = "abs";
    /** The abstract aspect value. */
    static public final AspectValue ABS;
    /** Name of the subtype aspect value. */
    static public final String SUB_NAME = "sub";
    /** The subtype aspect value. */
    static public final AspectValue SUB;
    /** Name of the empty aspect value. */
    static public final String EMPTY_NAME = "";
    /** The empty aspect value. */
    static public final AspectValue EMPTY;

    static {
        try {
            PATH = instance.addEdgeValue(PATH_NAME);
            PATH.setLabelParser(RegExprLabelParser.getInstance(true));
            SUB = instance.addEdgeValue(SUB_NAME);
            SUB.setLabelParser(EmptyLabelParser.getInstance());
            SUB.setIncompatible(RuleAspect.getInstance());
            ABS = instance.addEdgeValue(ABS_NAME);
            ABS.setIncompatible(RuleAspect.getInstance());
            EMPTY = instance.addEdgeValue(EMPTY_NAME);
            EMPTY.setLabelParser(FreeLabelParser.getInstance());
            // incompatibilities
            instance.setIncompatible(NestingAspect.getInstance());
        } catch (FormatException exc) {
            throw new Error("Aspect '" + TYPE_ASPECT_NAME
                + "' cannot be initialised due to name conflict", exc);
        }
    }

    /** Parser that only accepts empty labels. */
    static private class EmptyLabelParser implements LabelParser {
        /** Private constructor for this singleton class. */
        private EmptyLabelParser() {
            // empty constructor
        }

        @Override
        public Label parse(String text) throws FormatException {
            if (text.length() > 0) {
                throw new FormatException(
                    "Only empty label text allowed for '%s'-label", SUB);
            }
            return DefaultLabel.createLabel(text);
        }

        @Override
        public DefaultLabel unparse(Label label) {
            return DefaultLabel.createLabel("");
        }

        /** Returns the singleton instance of this class. */
        static public EmptyLabelParser getInstance() {
            return instance;
        }

        /** Singleton instance of this class. */
        static private EmptyLabelParser instance = new EmptyLabelParser();
    }
}
