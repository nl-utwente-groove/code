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
 * $Id: AspectParseData.java,v 1.9 2008-01-30 09:31:33 iovka Exp $
 */
package groove.view.aspect;

import static groove.view.aspect.Aspect.VALUE_SEPARATOR;
import groove.graph.Label;
import groove.util.DefaultFixable;
import groove.view.FormatException;

import java.util.Collection;

/**
 * Combination of declared aspect values and actual label text, as derived from
 * a plain aspect label.
 * @author Arend Rensink
 * @version $Revision $
 * @deprecated all functionality is taken over by {@link AspectMap}
 */
@Deprecated
class AspectParseData {
    /**
     * Construct a label from given aspect value list and label.
     * @param values the list of aspect values to be used
     * @param label the existing label
     */
    @Deprecated
    AspectParseData(AspectMap values, Label label) {
        this(values);
    }

    /**
     * Construct a label from given aspect value list, end flag, and label text.
     * @param values the list of aspect values to be used
     */
    AspectParseData(AspectMap values) {
        this.allAspectMap = new AspectMap(values);
    }

    /**
     * Returns the aspect values, in the order given in the original label
     * prefix.
     */
    public Collection<AspectValue> getDeclaredValues() {
        return getAspectMap().getDeclaredValues();
    }

    /**
     * Returns the combined map of all aspect values, declared and inferred.
     */
    public AspectMap getAspectMap() {
        this.status.setFixed();
        return this.allAspectMap;
    }

    /**
     * Adds values to the aspect map that are inferred from source and target
     * nodes. This method should not be called after {@link #getAspectMap()} has
     * been invoked.
     * @param sourceMap map of aspect values for the source node
     * @param targetMap map of aspect values for the target node
     * @throws FormatException if an explicitly declared aspect value is
     *         overruled
     */
    @Deprecated
    void addInferences(AspectMap sourceMap, AspectMap targetMap)
        throws FormatException {
        this.status.testFixed(false);
        for (Aspect aspect : Aspect.allAspects) {
            AspectValue edgeValue = this.allAspectMap.get(aspect);
            AspectValue sourceValue = sourceMap.get(aspect);
            AspectValue sourceInference =
                sourceValue == null ? null : sourceValue.sourceToEdge();
            AspectValue targetValue = targetMap.get(aspect);
            AspectValue targetInference =
                targetValue == null ? null : targetValue.targetToEdge();
            AspectValue result =
                aspect.getMax(edgeValue, sourceInference, targetInference);
            if (result != null && !result.equals(edgeValue)) {
                this.allAspectMap.addInferredValue(result);
            }
        }
    }

    /**
     * Indicates if the original plain label contains an empty value explicitly
     * modelling the end of the aspect value list.
     */
    public boolean isHasEnd() {
        return this.allAspectMap.hasEnd();
    }

    /**
     * Returns the actual label text (which may be <code>null</code> if the
     * plain label was a node decorator).
     */
    public String getText() {
        return this.allAspectMap.getText();
    }

    /**
     * Indicates if there was an actual label text.
     * @return <code>true</code> if and only if <code>getText() != null</code>
     */
    public boolean hasText() {
        return getText() != null;
    }

    /**
     * Reconstructs the original plain label text from the list of aspect
     * values, the end flag, and the actual label text.
     */
    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        for (AspectValue value : getDeclaredValues()) {
            result.append(AspectParser.toString(value));
        }
        // append the end marking, if any
        if (isHasEnd()) {
            result.append(VALUE_SEPARATOR);
        }
        // append the label text, if any
        String label = getText();
        if (label != null) {
            result.append(label);
        }
        return result.toString();
    }

    /** The list of all (declared and inferred) aspect values. */
    private final AspectMap allAspectMap;
    /** Fixed status of the parse data. */
    private final DefaultFixable status = new DefaultFixable();
}
