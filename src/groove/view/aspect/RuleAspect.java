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
 * $Id: RuleAspect.java,v 1.17 2008-01-31 13:12:08 rensink Exp $
 */
package groove.view.aspect;

import groove.util.Groove;
import groove.view.FormatException;

/**
 * Graph aspect dealing with transformation rules. Values are: <i>eraser</i>,
 * <i>reader</i> or <i>creator</i>.
 * @author Arend Rensink
 * @version $Revision$
 */
public class RuleAspect extends AbstractAspect {

    /** Private constructor to create the singleton instance. */
    private RuleAspect() {
        super(RULE_ASPECT_NAME);
    }

    /**
     * Returns a {@link NamedAspectValue} with the given name.
     * @param name the name content
     * @throws FormatException if <code>name</code> is not correctly formatted
     *         as an identifier
     */
    @Override
    protected AspectValue createValue(String name) throws FormatException {
        if (name.equals(REMARK_NAME)) {
            return super.createValue(name);
        } else {
            return new NamedAspectValue(getInstance(), name);
        }
    }

    /**
     * Returns the singleton instance of this aspect.
     */
    public static RuleAspect getInstance() {
        return instance;
    }

    /**
     * Tests if a given aspect element contains a {@link RuleAspect} value that
     * indicates presence in the left hand side. This is the case if there is an
     * aspect value in the element which equals either {@link #READER} or
     * {@link #ERASER}.
     * @param element the element to be tested
     * @return <code>true</code> if <code>element</code> contains a
     *         {@link RuleAspect} value that equals either {@link #READER} or
     *         {@link #ERASER}.
     */
    public static boolean inLHS(AspectElement element) {
        AspectValue type = element.getRole();
        return READER.equals(type) || ERASER.equals(type);
    }

    /**
     * Tests if a given aspect element contains a {@link RuleAspect} value that
     * indicates presence in the right hand side. This is the case if there is
     * an aspect value in the element which equals either {@link #READER} or
     * {@link #CREATOR}.
     * @param element the element to be tested
     * @return <code>true</code> if <code>element</code> contains a
     *         {@link RuleAspect} value that equals either {@link #READER} or
     *         {@link #CREATOR}.
     */
    public static boolean inRHS(AspectElement element) {
        AspectValue role = element.getRole();
        return READER.equals(role) || CREATOR.equals(role) || CNEW.equals(role);
    }

    /**
     * Tests if a given aspect element contains a {@link RuleAspect} value that
     * indicates presence a negative application condition. This is the case if
     * there is an aspect value in the element which equals {@link #EMBARGO}.
     * @param element the element to be tested
     * @return <code>true</code> if <code>element</code> contains a
     *         {@link RuleAspect} value that equals {@link #EMBARGO}.
     */
    public static boolean inNAC(AspectElement element) {
        return EMBARGO.equals(element.getRole()) || isCNEW(element);
    }

    /**
     * Tests if a given aspect element is a creator. This is the case if there
     * is an aspect value in the element which equals {@link #CREATOR}.
     * @param element the element to be tested
     * @return <code>true</code> if <code>element</code> contains a
     *         {@link RuleAspect} value that equals {@link #CREATOR}.
     */
    public static boolean isCreator(AspectElement element) {
        return CREATOR.equals(element.getRole()) || isCNEW(element);
    }

    /**
     * Tests if a given aspect element is an embargo creator. This is the case
     * if there is an aspect value in the element which equals EMBARGOCREATOR.
     */
    public static boolean isCNEW(AspectElement element) {
        return CNEW.equals(element.getRole());
    }

    /**
     * Tests if a given aspect element is an eraser. This is the case if there
     * is an aspect value in the element which equals {@link #ERASER}.
     * @param element the element to be tested
     * @return <code>true</code> if <code>element</code> contains a
     *         {@link RuleAspect} value that equals {@link #ERASER}.
     */
    public static boolean isEraser(AspectElement element) {
        return ERASER.equals(element.getRole());
    }

    /**
     * The name of the rule aspect.
     */
    public static final String RULE_ASPECT_NAME = "role";
    /** Name of the eraser aspect value. */
    public static final String ERASER_NAME =
        Groove.getXMLProperty("label.eraser.prefix");
    /** The eraser aspect value. */
    public static final AspectValue ERASER;
    /** Name of the reader aspect value. */
    public static final String READER_NAME =
        Groove.getXMLProperty("label.reader.prefix");
    /** The reader aspect value. */
    public static final AspectValue READER;
    /** Name of the creator aspect value. */
    public static final String CREATOR_NAME =
        Groove.getXMLProperty("label.creator.prefix");
    /** The creator aspect value. */
    public static final AspectValue CREATOR;
    /** Name of the embargo aspect value. */
    public static final String EMBARGO_NAME =
        Groove.getXMLProperty("label.embargo.prefix");
    /** The embargo aspect value. */
    public static final AspectValue EMBARGO;
    /** Name of the embargo creator aspect value. */
    public static final String CNEW_NAME =
        Groove.getXMLProperty("label.embargocreator.prefix");
    /** The embargo creator aspect value. */
    public static final AspectValue CNEW;
    /** Name of the remark aspect value. */
    public static final String REMARK_NAME =
        Groove.getXMLProperty("label.remark.prefix");
    /** The remark aspect value. */
    public static final AspectValue REMARK;
    /**
     * The singleton instance of this class.
     */
    private static final RuleAspect instance = new RuleAspect();

    static {
        try {
            ERASER = instance.addValue(ERASER_NAME);
            CREATOR = instance.addValue(CREATOR_NAME);
            EMBARGO = instance.addValue(EMBARGO_NAME);
            CNEW = instance.addValue(CNEW_NAME);
            READER = instance.addValue(READER_NAME);
            REMARK = instance.addValue(REMARK_NAME);
            REMARK.setLast(true);
        } catch (FormatException exc) {
            throw new Error("Aspect '" + RULE_ASPECT_NAME
                + "' cannot be initialised due to name conflict", exc);
        }
    }
}
