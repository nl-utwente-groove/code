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
 * $Id: VarMap.java,v 1.4 2008-01-30 09:32:28 iovka Exp $
 */
package groove.rel;

import groove.graph.TypeLabel;

import java.util.Map;

/**
 * Add-on interface that specifies support for mapping variables (given by
 * strings) to labels.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface VarMap {
    /**
     * Returns a mapping from variables to labels.
     */
    Map<LabelVar,TypeLabel> getValuation();

    /**
     * Returns the value stored for a given variable. Returns <code>null</code>
     * if the variable does not occur in the source or has no value in the
     * morphism.
     */
    TypeLabel getVar(LabelVar var);

    /**
     * Inserts a value at a given variable. Returns the old value for the
     * variable, if any.
     */
    TypeLabel putVar(LabelVar var, TypeLabel value);

    /**
     * Copies a given valuation mapping into this one.
     */
    void putAllVar(Map<LabelVar,TypeLabel> valuation);
}
