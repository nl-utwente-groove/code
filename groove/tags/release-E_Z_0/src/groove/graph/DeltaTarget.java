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
 * $Id: DeltaTarget.java,v 1.3 2008-01-30 09:32:51 iovka Exp $
 */
package groove.graph;

/**
 * Command interface to deal with graph changes.
 */
public interface DeltaTarget {
    /** Callback method invoked to indicate that a node is to be added. */
    public boolean addNode(Node elem);

    /** Callback method invoked to indicate that a node is to be removed. */
    public boolean removeNode(Node elem);

    /** Callback method invoked to indicate that an edge is to be added. */
    public boolean addEdge(Edge elem);

    /** Callback method invoked to indicate that an edge is to be removed. */
    public boolean removeEdge(Edge elem);
}