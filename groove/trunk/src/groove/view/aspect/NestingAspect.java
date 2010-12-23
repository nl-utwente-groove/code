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
 * $Id: NestingAspect.java,v 1.16 2008-02-29 11:02:22 fladder Exp $
 */
package groove.view.aspect;

import groove.view.FormatException;

import java.util.HashSet;
import java.util.Set;

/**
 * Graph Aspect dealing with rule nesting. It essentially allows a complete rule
 * tree to be stored in a flat format.
 * 
 * @author kramor
 * @version 0.1 $Revision$ $Date: 2008-02-29 11:02:22 $
 */
public class NestingAspect extends AbstractAspect {
    /**
     * Creates a new instance of this Aspect
     */
    private NestingAspect() {
        super(NESTING_ASPECT_NAME);
    }

    @Override
    protected AspectValue createValue(String name) throws FormatException {
        AspectValue result;
        if (contentValues.contains(name)) {
            result = new NamedAspectValue(getInstance(), name);
        } else {
            result = super.createValue(name);
        }
        return result;
    }

    /**
     * Returns the singleton instance of this aspect.
     * @return the singleton instance of this aspect
     */
    public static final NestingAspect getInstance() {
        return instance;
    }

    /**
     * Determine whether an aspect edge carries the {@link #FORALL} or
     * {@link #FORALL_POS} nesting value.
     */
    public static boolean isForall(AspectElement element) {
        return FORALL.equals(element.getType())
            || FORALL_POS.equals(element.getType());
    }

    /**
     * Determine whether an aspect edge carries the {@link #FORALL_POS} nesting
     * value.
     */
    public static boolean isPositive(AspectElement element) {
        return FORALL_POS.equals(element.getType());
    }

    /**
     * Determine whether an aspect edge carries the {@link #EXISTS} nesting
     * value.
     */
    public static boolean isExists(AspectElement element) {
        return EXISTS.equals(element.getType());
    }

    /** The name of the nesting aspect */
    public static final String NESTING_ASPECT_NAME = "nesting";
    // /** Name of the NAC aspect value */
    // public static final String NAC_NAME = "nac";
    /** Name of the exists aspect value */
    public static final String EXISTS_NAME = "exists";
    /** Name of the forall aspect value */
    public static final String FORALL_NAME = "forall";
    /** Name of the positive forall aspect value */
    public static final String FORALL_POS_NAME = "forallx";
    /** Name of the generic nesting edge aspect value. */
    public static final String NESTED_NAME = "nested";
    /** The set of aspect value names that are content values. */
    private static final Set<String> contentValues;

    static {
        contentValues = new HashSet<String>();
        contentValues.add(EXISTS_NAME);
        contentValues.add(FORALL_NAME);
        contentValues.add(FORALL_POS_NAME);
    }

    /** The exists aspect value */
    public static final AspectValue EXISTS;
    /** The forall aspect value */
    public static final AspectValue FORALL;
    /** The positive forall aspect value */
    public static final AspectValue FORALL_POS;
    /** Nested edge aspect value. */
    public static final AspectValue NESTED;

    /** Singleton instance of this class */
    private static final NestingAspect instance = new NestingAspect();

    static {
        try {
            EXISTS = instance.addValue(EXISTS_NAME);
            FORALL = instance.addValue(FORALL_NAME);
            FORALL_POS = instance.addNodeValue(FORALL_POS_NAME);
            NESTED = instance.addEdgeValue(NESTED_NAME);
        } catch (FormatException exc) {
            throw new Error("Aspect '" + NESTING_ASPECT_NAME
                + "' cannot be initialised due to name conflict", exc);
        }
    }

    /** Label used for parent edges (between meta-nodes). */
    public static final String IN_LABEL = "in";
    /** Label used for level edges (from rule nodes to meta-nodes). */
    public static final String AT_LABEL = "at";
    /** Label used for the top-level meta-node. */
    public static final String TOP_LABEL = "top";
    /** The set of all allowed nesting labels. */
    static final Set<String> ALLOWED_LABELS = new HashSet<String>();

    static {
        ALLOWED_LABELS.add(IN_LABEL);
        ALLOWED_LABELS.add(AT_LABEL);
        ALLOWED_LABELS.add(TOP_LABEL);
        NESTED.setLast(true);
    }
}
