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

import groove.trans.Rule;
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

    /**
     * Creates a new instance of this aspect with a given name
     * @param name the name to give to this aspect
     */
    public ParameterAspect(String name) {
        super(name);
    }

    @Override
    protected AspectValue createValue(String name) throws FormatException {
        return new ParameterAspectValue(name);
    }

    /**
     * Returns the singleton instance of this aspect.
     */
    public static ParameterAspect getInstance() {
        return instance;
    }

    /**
     * Gets the type of a given parameter, i.e. whether it may be used as 
     * input, output or both in the control language.
     * @param elem the AspectElement to check the type for 
     * @return Rule.PARAMETER_INPUT || Rule.PARAMETER_OUTPUT || Rule.PARAMETER_BOTH
     */
    public static int getParameterType(AspectNode elem) {
        AspectValue param = elem.getParameter();
        if (param != null) {
            String paramString = param.getName();
            if (paramString.startsWith(PAR_IN_NAME)) {
                return Rule.PARAMETER_INPUT;
            } else if (paramString.startsWith(PAR_OUT_NAME)) {
                return Rule.PARAMETER_OUTPUT;
            } else if (paramString.startsWith(PAR_NAME)) {
                return Rule.PARAMETER_BOTH;
            }
        }
        return Rule.PARAMETER_DOES_NOT_EXIST;
    }

    /** The name of the aspect. */
    private static final String PARAMETER_ASPECT_NAME = "parameter";

    /** The label for the parameter aspect value */
    public static final String PAR_NAME = "par";

    /** The label for the parameter-in value */
    public static final String PAR_IN_NAME = "parin";

    /** The label for the parameter-out value */
    public static final String PAR_OUT_NAME = "parout";

    /** The singleton instance of this aspect. */
    private static final ParameterAspect instance = new ParameterAspect();

    static {
        try {
            instance.addValue(PAR_NAME);
            instance.addValue(PAR_IN_NAME);
            instance.addValue(PAR_OUT_NAME);
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
    public class ParameterAspectValue extends AspectValue {
        /** 
         * Constructs a new aspect value, for the {@link ParameterAspect}. 
         */
        public ParameterAspectValue(String name) throws FormatException {
            super(ParameterAspect.getInstance(), name, true);
        }

        /** Constructs a value wrapping a given number (encoded as a string). */
        private ParameterAspectValue(ParameterAspectValue original,
                String number) throws FormatException {
            super(original, number);
        }

        @Override
        public ParameterAspectValue newValue(String value)
            throws FormatException {
            if (value.length() == 0) {
                return null;
            }
            if (value.charAt(0) != PARAMETER_START_CHAR) {
                throw new FormatException(
                    "Parameter '%s' should start with '%c'", value,
                    PARAMETER_START_CHAR);
            }
            try {
                Integer.parseInt(value.substring(1));
                return new ParameterAspectValue(this, value);
            } catch (NumberFormatException exc) {
                throw new FormatException("Invalid parameter number", value);
            }
        }

        /** Returns the parameter number wrapped by this aspect value. */
        public Integer getNumber() {
            String content = getContent();
            return content == null ? null
                    : Integer.parseInt(content.substring(1));
        }

        /** Start character of parameter strings. */
        static private final char PARAMETER_START_CHAR = '$';
    }
}
