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
import groove.explore.encode.Template.Visibility;
import groove.explore.encode.TemplateList;
import groove.explore.result.Acceptor;
import groove.trans.RuleSystem;
import groove.view.FormatException;

import java.util.EnumSet;

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
    private AcceptorEnumerator() {
        super("acceptor", ACCEPTOR_TOOLTIP);
        for (AcceptorValue value : EnumSet.allOf(AcceptorValue.class)) {
            Template<Acceptor> template = value.getTemplate();
            int mask;
            switch (value) {
            case FINAL:
            case ANY:
            case NONE:
                mask = MASK_ALL;
                if ((mask & MASK_DEVELOPMENT_ONLY) == MASK_DEVELOPMENT_ONLY) {
                    template.setVisibility(Visibility.DEVELOPMENT_ONLY);
                    mask = mask - MASK_DEVELOPMENT_ONLY;
                }
                break;
            default:
                mask = MASK_DEFAULT;
            }
            template.setMask(mask);
            addTemplate(template);
        }
    }

    /** Returns the singleton instance of this class. */
    public static AcceptorEnumerator getInstance() {
        return INSTANCE;
    }

    /**
     * Creates an {@link Acceptor} out of a {@link Serialized}
     * by finding the template that starts
     * with the given keyword and then using its parse method.
     */
    public static Acceptor parseAcceptor(RuleSystem rules, Serialized source)
        throws FormatException {
        return getInstance().parse(rules, source);
    }

    private static final AcceptorEnumerator INSTANCE = new AcceptorEnumerator();

    /** Mask for strategies that are only enabled in 'concrete' mode. */
    public final static int MASK_CONCRETE = 1;
    /** Mask for strategies that are only enabled in 'abstraction' mode. */
    public final static int MASK_ABSTRACT = 2;
    /** Special mask for development strategies only. Treated specially. */
    public final static int MASK_DEVELOPMENT_ONLY = 4;
    /** Mask for strategies that are enabled in all modes. */
    public final static int MASK_ALL = MASK_CONCRETE | MASK_ABSTRACT;
    /** Mask that is used by default. */
    public final static int MASK_DEFAULT = MASK_CONCRETE;
    private static final String ACCEPTOR_TOOLTIP = "<HTML>"
        + "An acceptor is a predicate that is applied each time the LTS is "
        + "updated<I>*</I>.<BR>"
        + "Information about each acceptor success is added to the result "
        + "set of the exploration.<BR>"
        + "This result set can be used to interrupt exploration.<BR>"
        + "<I>(*)<I>The LTS is updated when a transition is applied, or "
        + "when a new state is reached." + "</HTML>";
}