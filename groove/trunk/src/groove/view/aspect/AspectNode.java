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
 * $Id: AspectNode.java,v 1.4 2008-01-30 09:31:33 iovka Exp $
 */
package groove.view.aspect;

import groove.graph.AbstractNode;
import groove.view.FormatException;

import java.util.Collection;

/**
 * Graph node implementation that supports aspects.
 * @author Arend Rensink
 * @version $Revision$
 */
public class AspectNode extends AbstractNode implements AspectElement {
    /** Constructs an aspect node with a given number. */
    AspectNode(int nr) {
        super(nr);
        this.aspectMap = new AspectMap(true);
    }

    /**
     * This class does not guarantee unique representatives for the same number,
     * so we need to override {@link #hashCode()} and {@link #equals(Object)}.
     */
    @Override
    protected int computeHashCode() {
        return getNumber() ^ getClass().hashCode();
    }

    /** 
     * Use the same prefix as for default nodes, so the error messages
     * remain understandable.
     */
    @Override
    protected String getToStringPrefix() {
        return "n";
    }

    /**
     * This class does not guarantee unique representatives for the same number,
     * so we need to override {@link #hashCode()} and {@link #equals(Object)}.
     */
    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass().equals(getClass())
            && ((AspectNode) obj).getNumber() == getNumber();
    }

    /**
     * Adds an aspect value to the node, or updates an existing value.
     * @param value the aspect value to be added
     * @throws FormatException if the node already has a value for
     *         <code>value.getAspect()</code>
     */
    public void addInferredValue(AspectValue value) throws FormatException {
        try {
            getAspectMap().addInferredValue(value);
        } catch (FormatException e) {
            throw e.extend(this);
        }
    }

    /**
     * Adds an aspect value to the node.
     * @param value the aspect value to be added
     * @throws FormatException if the node already has a value for
     *         <code>value.getAspect()</code>
     */
    public void addDeclaredValue(AspectValue value) throws FormatException {
        try {
            getAspectMap().addDeclaredValue(value);
        } catch (FormatException e) {
            throw e.extend(this);
        }
    }

    public AspectValue getValue(Aspect aspect) {
        AspectValue result = getAspectMap().get(aspect);
        if (result == null) {
            result = aspect.getDefaultValue();
        }
        return result;
    }

    public Collection<AspectValue> getDeclaredValues() {
        return getAspectMap().getDeclaredValues();
    }

    /**
     * Returns the value for a given aspect for an edge that has this node as
     * its source, in case the node's own value gives a way to predict this.
     * @param aspect the aspect for which a value is to be inferred
     * @return an aspect value for an edge that has this node as its source, or
     *         <code>null</code> if no value can be inferred.
     */
    public AspectValue getSourceToEdgeValue(Aspect aspect) {
        AspectValue ownValue = getValue(aspect);
        if (ownValue == null) {
            return null;
        } else {
            return ownValue.sourceToEdge();
        }
    }

    /**
     * Returns the value for a given aspect for an edge that has this node as
     * its target, in case the node's own value gives a way to predict this.
     * @param aspect the aspect for which a value is to be inferred
     * @return an aspect value for an edge that has this node as its target, or
     *         <code>null</code> if no value can be inferred.
     */
    public AspectValue getTargetToEdgeValue(Aspect aspect) {
        AspectValue ownValue = getValue(aspect);
        if (ownValue == null) {
            return null;
        } else {
            return ownValue.targetToEdge();
        }
    }

    /**
     * Returns the map from aspects to aspect values for this edge.
     */
    public AspectMap getAspectMap() {
        return this.aspectMap;
    }

    /**
     * The internal map from aspects to corresponding values.
     */
    private final AspectMap aspectMap;
}
