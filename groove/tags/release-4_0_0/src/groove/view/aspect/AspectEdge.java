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
 * $Id: AspectEdge.java,v 1.10 2008-01-30 09:31:33 iovka Exp $
 */
package groove.view.aspect;

import groove.graph.AbstractBinaryEdge;
import groove.graph.DefaultLabel;
import groove.graph.Edge;
import groove.graph.Label;
import groove.view.FormatError;
import groove.view.FormatException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Edge enriched with aspect data. Aspect edge labels are interpreted as
 * {@link DefaultLabel}s.
 * @author Arend Rensink
 * @version $Revision$
 */
public class AspectEdge extends AbstractBinaryEdge<AspectNode,Label,AspectNode>
        implements AspectElement {
    /**
     * Constructs a new edge, with source and target node, label, and aspect
     * values as given.
     * @param source the source node for this edge
     * @param target the target node for this edge
     * @param parseData the aspect values for this edge.
     * @throws FormatException if the aspect values of <code>parseData</code>
     *         are inconsistent with those of the source or target nodes
     */
    AspectEdge(AspectNode source, AspectNode target, AspectMap parseData)
        throws FormatException {
        super(source, DefaultLabel.createLabel(parseData.getText()), target);
        this.parseData = parseData;
    }

    /** 
     * Initialises and checks all aspect value-related properties.
     * This method should always be called immediately after the constructor. 
     * @throws FormatException if there are aspect-related errors.
     */
    public void initAspects() throws FormatException {
        for (AspectValue value : this.parseData.getDeclaredValues()) {
            if (!value.isEdgeValue()) {
                throw new FormatException(
                    "Aspect value '%s' cannot be used on edges", value, this);
            }
        }
        addInferences();
        testLabel();
    }

    /**
     * Adds values to the aspect map of an edge that are inferred from source
     * and target nodes.
     * @throws FormatException if an explicitly declared aspect value is
     *         overruled
     */
    private void addInferences() throws FormatException {
        AspectMap sourceData = source().getAspectMap();
        AspectMap targetData = target().getAspectMap();
        for (Aspect aspect : Aspect.getAllAspects()) {
            try {
                AspectValue edgeValue = this.parseData.get(aspect);
                AspectValue sourceValue = sourceData.get(aspect);
                AspectValue sourceInference =
                    sourceValue == null ? null : sourceValue.sourceToEdge();
                AspectValue targetValue = targetData.get(aspect);
                AspectValue targetInference =
                    targetValue == null ? null : targetValue.targetToEdge();
                AspectValue result =
                    aspect.getMax(edgeValue, sourceInference, targetInference);
                if (result != null && !result.equals(edgeValue)) {
                    this.parseData.addInferredValue(result);
                }
            } catch (FormatException e) {
                throw e.extend(this);
            }
        }
    }

    /**
     * Tests if the parsed edge label is allowed by all inferred aspects.
     * @throws FormatException if there is an aspect whose value for this edge
     *         is incompatible with the edge label
     * @see Aspect#testLabel(Label, AspectValue, AspectValue)
     */
    private void testLabel() throws FormatException {
        List<FormatError> errors = new ArrayList<FormatError>();
        for (AspectValue declaredAspectValue : getDeclaredValues()) {
            Aspect aspect = declaredAspectValue.getAspect();
            AspectValue inferredValue = getAspectMap().get(aspect);
            try {
                aspect.testLabel(label(), declaredAspectValue, inferredValue);
            } catch (FormatException e) {
                for (FormatError error : e.getErrors()) {
                    errors.add(error.extend(this));
                }
            }
        }
        if (!errors.isEmpty()) {
            throw new FormatException(errors);
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
        return this.parseData.getDeclaredValues();
    }

    /**
     * Returns the map from aspects to aspect values for this edge, lazily
     * creating it first.
     */
    public AspectMap getAspectMap() {
        return this.parseData;
    }

    /**
     * Returns the plain text label for the aspect edge.
     */
    public String getPlainText() {
        return this.parseData.toString();
    }

    /**
     * Returns the label that this edge gets, when compiled to a model edge.
     * Convenience method for {@code getAspectMap().toModelLabel(regExpr)}
     * @throws FormatException if the label contains a format error
     */
    public Label getModelLabel() throws FormatException {
        try {
            return getAspectMap().toModelLabel();
        } catch (FormatException exc) {
            throw new FormatException(exc.getMessage(), this);
        }
    }

    /**
     * Includes the hash code of the associated aspect values.
     */
    @Override
    protected int computeHashCode() {
        int result = super.computeHashCode();
        for (Aspect aspect : Aspect.getAllAspects()) {
            AspectValue value = getValue(aspect);
            if (value != null) {
                result += value.hashCode();
            }
        }
        return result;
    }

    /**
     * Tests equality of type, ends and aspect values.
     */
    @Override
    public boolean equals(Object obj) {
        return isTypeEqual(obj) && isEndEqual((Edge) obj)
            && isLabelEqual((Edge) obj) && isAspectEqual((AspectEdge) obj);
    }

    /**
     * Tests if the object is an {@link AspectEdge}.
     */
    @Override
    protected boolean isTypeEqual(Object obj) {
        return obj instanceof AspectEdge;
    }

    /** Tests if the aspect map of this edge equals that of the other. */
    protected boolean isAspectEqual(AspectEdge other) {
        for (Aspect aspect : Aspect.getAllAspects()) {
            if (getValue(aspect) != other.getValue(aspect)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the edge can be displayed as a node label.
     * @return true, if the label is a node type label or a flag;
     *         false, otherwise.
     */
    public boolean isUnaryEdge() {
        return (this.isNodeType() == 0 || this.isFlag() == 0);
    }

    /**
     * This implementation makes sure that edges with node type labels are
     * ordered before other edges.
     */
    @Override
    protected int compareToEdge(Edge obj) {
        assert obj instanceof AspectEdge : String.format(
            "Can't compare aspect edge '%s' to non-aspect edge '%s'", this, obj);
        AspectEdge other = (AspectEdge) obj;
        int result;
        // first compare the source, then the aspects,
        // label, then the target
        result = source().compareTo(other.source());
        if (result == 0) {
            result = isNodeType() - other.isNodeType();
        }
        if (result == 0) {
            result = isFlag() - other.isFlag();
        }
        if (result == 0) {
            result = getAspectMap().compareTo(other.getAspectMap());
        }
        if (result == 0) {
            result = label().compareTo(other.label());
        }
        if (result == 0) {
            result = target().compareTo(other.target());
        }
        return result;
    }

    /** Tests if this aspect edge stands for a node type. */
    public int isNodeType() {
        return TypeAspect.isNodeType(this) ? 0 : 1;
    }

    /** Tests if this aspect edge stands for a flag. */
    public int isFlag() {
        return TypeAspect.isFlag(this) ? 0 : 1;
    }

    /**
     * This implementation defers to {@link #getPlainText()}
     */
    @Override
    protected String getLabelText() {
        return getPlainText();
    }

    /**
     * The aspect information of the label, set at construction time.
     */
    private final AspectMap parseData;
}
