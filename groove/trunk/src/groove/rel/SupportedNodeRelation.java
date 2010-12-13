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
 * $Id: SupportedNodeRelation.java,v 1.4 2008-02-05 13:43:00 rensink Exp $
 */
package groove.rel;

import groove.graph.Element;

import java.util.Collection;

/**
 * Binary relation over nodes which for each pair of related nodes contains a
 * <i>support</i>, which is a set of graph elements that justifies the
 * relation.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface SupportedNodeRelation extends NodeRelation {
    /**
     * Yields the set of all graph elements supporting this relation.
     */
    public Collection<Element> getSupport();
}
