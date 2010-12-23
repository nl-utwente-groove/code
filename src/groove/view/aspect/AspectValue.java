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
 * $Id: AspectValue.java,v 1.11 2008-02-29 11:02:22 fladder Exp $
 */
package groove.view.aspect;

import static groove.view.aspect.Aspect.CONTENT_ASSIGN;
import static groove.view.aspect.Aspect.VALUE_SEPARATOR;
import groove.view.FormatException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Class implementing values of a given aspect. Aspect values are distinguished
 * by name, which should therefore be globally distinct. This is checked at
 * construction time. The class has functionality to statically retrieve aspect
 * values by name.
 * @author Arend Rensink
 * @version $Revision $
 */
public class AspectValue implements Comparable<AspectValue> {
    /**
     * Creates a new aspect value, for a given aspect and with a given name.
     * Throws an exception if an aspect value with the same name exists already.
     * @param aspect the aspect for which this is a value
     * @param name the name of the aspect value.
     * @param allowsContent if {@code true}, the value may be used
     * as a prototype for copies with a string content object
     * @throws groove.view.FormatException if the value name is already used
     */
    public AspectValue(Aspect aspect, String name, boolean allowsContent)
        throws FormatException {
        this.aspect = aspect;
        this.name = name;
        this.prototype = allowsContent;
        this.content = null;
    }

    /**
     * Copies a given aspect value. This is a local constructor, not to be
     * invoked directly.
     * @param original the aspect value that we copy.
     */
    AspectValue(AspectValue original, String content) throws FormatException {
        if (!original.isPrototype()) {
            throw new FormatException("Can't create aspect %s with value %s",
                original, content);
        }
        this.aspect = original.getAspect();
        this.name = original.getName();
        this.last = original.isLast();
        this.content = content;
        this.prototype = false;
    }

    /** 
     * Creates a new aspect value with a given content string.
     * @throws FormatException if some constraint of the content string
     * is not fulfilled
     */
    public AspectValue newValue(String content) throws FormatException {
        assert getContent() == null : String.format(
            "Can't use named aspect value %s as prototype", this);
        return new AspectValue(this, content);
    }

    /** Returns the (possibly {@code null}) content of this aspect value. */
    public String getContent() {
        return this.content;
    }

    /** Indicates if this value can be used as a prototype for
     * values with content.
     * @return {@code false} always; should be overridden by a subclass
     * that does allow content.
     */
    public boolean isPrototype() {
        return this.prototype;
    }

    /**
     * Returns the current value of aspect.
     */
    public Aspect getAspect() {
        return this.aspect;
    }

    /**
     * Returns the name of the aspect value. The name uniquely identifies not
     * just the value itself, but also the aspect.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the prefix of the aspect value. The prefix consists of the name
     * followed by the separator.
     * @see #getName()
     * @see Aspect#VALUE_SEPARATOR
     */
    public String getPrefix() {
        return this.name + VALUE_SEPARATOR;
    }

    /**
     * Indicates if this aspect value must be the last in a sequence. 
     */
    public final boolean isLast() {
        return this.last;
    }

    /**
     * Assigns a label parser to this aspect value.
     */
    final void setLast(boolean last) {
        this.last = last;
    }

    /** Indicates if this aspect value may occur on nodes. */
    public boolean isNodeValue() {
        return this.aspect.getNodeValues().contains(this);
    }

    /** Indicates if this aspect value may occur on edges. */
    public boolean isEdgeValue() {
        return this.aspect.getEdgeValues().contains(this);
    }

    /**
     * Tests for equality by comparing the names.
     * @see #getName()
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof AspectValue
            && ((AspectValue) obj).getName().equals(this.name);
    }

    /**
     * Returns the hash code of the name.
     * @see #getName()
     */
    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    /**
     * Returns the name of the aspect.
     * @see #getName()
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(getName());
        if (this.content != null && this.content.length() != 0) {
            result.append(CONTENT_ASSIGN);
            result.append(this.content);
        }
        result.append(Aspect.VALUE_SEPARATOR);
        return result.toString();
    }

    /** Aspect values are compared by name. */
    @Override
    public int compareTo(AspectValue o) {
        return getName().compareTo(o.getName());
    }

    /**
     * The aspect that this value belongs to.
     */
    private final Aspect aspect;
    /**
     * The name of this value.
     */
    private final String name;
    /**
     * The content of this aspect value, if any.
     */
    private final String content;
    /** Flag indicating if this value may be used as a prototype. */
    private final boolean prototype;
    /** Flag indicatign that this aspect is always the last in a label. */
    private boolean last;

    /**
     * Returns the aspect value associated with a given name, if any. Returns
     * <code>null</code> if there is no value associated.
     * @param name the name for which we want the corresponding aspect value.
     */
    public static AspectValue getValue(String name) {
        Aspect.getAllAspects();
        return getValueMap().get(name);
    }

    /** Returns an unmodifiable view on the registered value names. */
    public static Set<String> getValueNames() {
        Aspect.getAllAspects();
        return getValueMap().keySet();
    }

    /** Returns the register of aspect value names. */
    private static Map<String,AspectValue> getValueMap() {
        // initialise the value map, if necessary
        if (valueMap == null) {
            valueMap = new HashMap<String,AspectValue>();
            for (Aspect aspect : Aspect.getAllAspects()) {
                for (AspectValue value : aspect.getValues()) {
                    String name = value.getName();
                    AspectValue previous = valueMap.put(value.getName(), value);
                    assert previous == null : String.format("Aspect value name "
                        + name + " already used for " + previous.getAspect());
                }
            }
        }
        return valueMap;
    }

    /** The internally kept register of aspect value names. */
    private static Map<String,AspectValue> valueMap;
}
