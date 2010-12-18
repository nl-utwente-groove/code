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
package groove.rel;

import groove.graph.ElementFactory;
import groove.trans.RuleLabel;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
public class RegFactory implements ElementFactory<RegNode,RuleLabel,RegEdge> {
    @Override
    public RegNode createNode(int nr) {
        return new RegNode(nr);
    }

    @Override
    public RuleLabel createLabel(String text) {
        return new RuleLabel(text);
    }

    @Override
    public RegEdge createEdge(RegNode source, String text, RegNode target) {
        return createEdge(source, createLabel(text), target);
    }

    @Override
    public RegEdge createEdge(RegNode source, RuleLabel label, RegNode target) {
        return new RegEdge(source, label, target);
    }

    /** Returns the singleton instance of this factory. */
    public static RegFactory instance() {
        return INSTANCE;
    }

    /** The singleton instance of this factory. */
    private static final RegFactory INSTANCE = new RegFactory();
}
