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
package groove.explore;

import groove.explore.encode.Serialized;
import groove.explore.encode.Template;
import groove.explore.encode.TemplateList;
import groove.explore.result.Acceptor;
import groove.grammar.Grammar;
import groove.util.parse.FormatException;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * <!=========================================================================>
 * AcceptorEnumerator enumerates all acceptors that are available in GROOVE.
 * With this enumeration, it is possible to create an editor for acceptors
 * (inherited method createEditor, stored results as a Serialized) and to
 * parse an acceptor from a Serialized (inherited method parse).
 * <!=========================================================================>
 * @author Maarten de Mol
 */
public class AcceptorEnumerator extends TemplateList<Acceptor> {

    /**
     * Enumerates the available acceptors one by one. An acceptor is defined
     * by means of a Template<Acceptor> instance.
     */
    private AcceptorEnumerator(EnumSet<AcceptorValue> acceptors) {
        super("acceptor", ACCEPTOR_TOOLTIP);
        for (AcceptorValue value : acceptors) {
            Template<Acceptor> template = value.getTemplate();
            addTemplate(template);
        }
    }

    /**
     * Parses a command line argument into a <code>Serialized</code> that
     * represents an acceptor.
     * @throws FormatException if the argument cannot be parsed
     */
    public static Serialized parseCommandLineAcceptor(String text) throws FormatException {
        Serialized result = instance().parseCommandline(text);
        if (result == null) {
            throw new FormatException("No such acceptor '%s'", text);
        }
        return result;
    }

    /** Inverse to {@link #parseCommandLineAcceptor(String)}. */
    public static String toParsableAcceptor(Serialized source) {
        return instance().toParsableString(source);
    }

    /**
     * Creates an {@link Acceptor} out of a {@link Serialized}
     * by finding the template that starts
     * with the given keyword and then using its parse method.
     */
    public static Acceptor parseAcceptor(Grammar rules, Serialized source) throws FormatException {
        return instance().parse(rules, source);
    }

    /** Returns the instance of this class that enumerates all acceptors. */
    public static AcceptorEnumerator instance() {
        return instance(EnumSet.allOf(AcceptorValue.class));
    }

    /** Returns an instance of this class enumerating a given (sub)set of acceptors. */
    public static AcceptorEnumerator instance(EnumSet<AcceptorValue> acceptors) {
        AcceptorEnumerator result = instanceMap.get(acceptors);
        if (result == null) {
            result = new AcceptorEnumerator(acceptors);
            instanceMap.put(acceptors, result);
        }
        return result;
    }

    /** Map from parsable strategies to the corresponding instance of this class. */
    private final static Map<EnumSet<AcceptorValue>,AcceptorEnumerator> instanceMap =
        new HashMap<>();

    private static final String ACCEPTOR_TOOLTIP = "<HTML>"
        + "An acceptor is a predicate that is applied each time the LTS is "
        + "updated<I>*</I>.<BR>"
        + "Information about each acceptor success is added to the result "
        + "set of the exploration.<BR>"
        + "This result set can be used to interrupt exploration.<BR>"
        + "<I>(*)<I>The LTS is updated when a transition is applied, or "
        + "when a new state is reached." + "</HTML>";
}