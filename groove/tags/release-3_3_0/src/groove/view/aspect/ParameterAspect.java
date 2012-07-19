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
 * $Id: ParameterAspect.java,v 1.2 2008-03-04 10:10:19 fladder Exp $
 */
package groove.view.aspect;

import groove.view.FormatException;

/**
 * Graph Aspect dealing with rule nesting. It essentially allows a complete rule
 * tree to be stored in a flat format.
 * 
 * @author Tom Staijen
 * @version $Revision$
 */
public class ParameterAspect extends AbstractAspect {
    /**
     * Creates a new instance of this Aspect
     */
    public ParameterAspect() {
        super(PARAMETER_ASPECT_NAME);
    }

    @Override
    protected AspectValue createValue(String name) throws FormatException {
        return new ParameterAspectValue();
    }

    /**
     * Returns the singleton instance of this aspect.
     */
    public static ParameterAspect getInstance() {
        return instance;
    }

    /**
     * Helper function to get the parameter number of an aspect node.
     * Returns <code>0</code> if the node has an anonymous parameter,
     * and <code>null</code> if the node has no parameter aspect value.
     */
    public static Integer getParNumber(AspectNode node) {
        ParameterAspectValue value =
            (ParameterAspectValue) node.getValue(getInstance());
        if (value == null) {
            return null;
        } else {
            Integer id = value.getContent();
            return id == null ? 0 : id;
        }
    }

    /**
     * Helper function to get the string representation of the parameter number of an aspect node.
     * Returns <code>null</code> if the node has no parameter aspect value, or the
     * aspect value has no content.
     */
    public static String getParString(AspectNode node) {
        ParameterAspectValue value =
            (ParameterAspectValue) node.getValue(getInstance());
        if (value == null) {
            return null;
        } else {
            return value.getContentString();
        }
    }

    /** The name of the aspect. */
    private static final String PARAMETER_ASPECT_NAME = "parameter";

    /** The label for the parameter aspect value * */
    public static final String PAR_NAME = "par";

    /** The singleton instance of this aspect. */
    private static final ParameterAspect instance = new ParameterAspect();

    static {
        try {
            instance.addValue(PAR_NAME);
        } catch (FormatException exc) {
            throw new Error("Aspect '" + PARAMETER_ASPECT_NAME
                + "' cannot be initialised due to name conflict", exc);
        }
    }
    

    /**
     * Aspect value encoding wrapping a number value.
     * @author Arend Rensink
     * @version $Revision $
     */
    public class ParameterAspectValue extends ContentAspectValue<Integer> {
        /** 
         * Constructs a new aspect value, for the {@link ParameterAspect}. 
         */
        public ParameterAspectValue()
            throws FormatException {
            super(ParameterAspect.getInstance(), ParameterAspect.PAR_NAME);
        }

        /** Creates an instance of a given nesting aspect value, with a given level. */
        private ParameterAspectValue(ParameterAspectValue original, Integer number) {
            super(original, number);
        }

        @Override
        public ContentAspectValue<Integer> newValue(String value)
            throws FormatException {
            return new ParameterAspectValue(this, this.parser.toContent(value));
        }

        /**
         * This implementation returns a parser that attempts to parse the value as a
         * parameter, consisting of {@value #PARAMETER_START_CHAR} followed by a number.
         */
        @Override
        ContentParser<Integer> createParser() {
            return new ParameterParser();
        }

        /** ContentParser used for this AspectValue */
        private final ContentParser<Integer> parser = new ParameterParser();

        /** Start character of parameter strings. */
        static private final char PARAMETER_START_CHAR = '$';
        
        /** Content parser which acts as the identity function on strings. */
        private class ParameterParser implements ContentParser<Integer> {
            /** Empty constructor with the correct visibility. */
            ParameterParser() {
                // empty
            }

            public Integer toContent(String value) throws FormatException {
                if (value.length() == 0) {
                    return null;
                }
                if (value.charAt(0) != PARAMETER_START_CHAR) {
                    throw new FormatException("Parameter '%s' should start with '%c'", value, PARAMETER_START_CHAR);
                }
                try {
                    return Integer.parseInt(value.substring(1));
                } catch (NumberFormatException exc) {
                    throw new FormatException(
                        "Invalid parameter number", value);
                }
            }

            public String toString(Integer content) {
                return PARAMETER_START_CHAR+content.toString();
            }
        }
    }
}