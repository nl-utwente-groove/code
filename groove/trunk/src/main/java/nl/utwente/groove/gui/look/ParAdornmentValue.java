/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package nl.utwente.groove.gui.look;

import nl.utwente.groove.grammar.aspect.Aspect;
import nl.utwente.groove.grammar.aspect.AspectNode;
import nl.utwente.groove.gui.jgraph.AspectJEdge;
import nl.utwente.groove.gui.jgraph.AspectJVertex;

/**
 * Strategy for computing the parameter adornment for a given JVertex
 * @author Arend Rensink
 * @version $Revision $
 */
public class ParAdornmentValue extends AspectValue<String> {
    @Override
    protected String getForJVertex(AspectJVertex jVertex) {
        StringBuilder result = null;
        AspectNode node = jVertex.getNode();
        if (node.hasParam()) {
            Aspect param = node.getParam();
            result = new StringBuilder(param.getContentString());
            switch (param.getKind()) {
            case PARAM_IN:
                result.insert(0, '?');
                break;
            case PARAM_OUT:
                result.insert(0, '!');
                break;
            case PARAM_ASK:
                result.insert(0, '*');
                break;
            default:
                // no special decoration
            }
        }
        return result == null
            ? null
            : result.toString();
    }

    @Override
    protected String getForJEdge(AspectJEdge jEdge) {
        return null;
    }
}
