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
 * $Id: Match.java,v 1.2 2008-01-30 09:32:35 iovka Exp $
 */
package groove.trans;

import groove.graph.Edge;
import groove.graph.Node;

import java.util.Collection;

/**
 * Interface wrapping the match of a condition or rule. This is specialised
 * depending on the type of condition or rule.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface Match {
    /** Returns the (host graph) nodes used as images in the match. */
    Collection<? extends Node> getNodeValues();

    /** Returns the (host graph) edges used as images in the match. */
    Collection<? extends Edge> getEdgeValues();
}
