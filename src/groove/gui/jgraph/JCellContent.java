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
 * $Id: JCellContent.java,v 1.3 2008-01-30 09:33:11 iovka Exp $
 */
package groove.gui.jgraph;

import java.util.Collection;
import java.util.TreeSet;

/**
 * User object underlying a {@link JVertex}or {@link JEdge}. The object behaves as a set that can
 * be loaded from a {@link String}.
 * 
 * @author Arend Rensink
 * @version $Revision$
 */
abstract public class JCellContent<T> extends TreeSet<T> {
    /**
     * Returns a collection of strings describing the objects contained in this user object.
     * @return the string descriptions of the objects contained in this collection
     */
    abstract public Collection<String> getLabelSet();
}
