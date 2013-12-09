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
 * $Id: Element.java,v 1.7 2008-01-30 09:32:53 iovka Exp $
 */
package groove.graph;

/**
 * Common interface for graph elements. The direct subinterfaces are:
 * {@link Node} and {@link Edge}. {@link Edge}s are essentially labelled
 * hyper-edges consisting of a number of <i>end points</i> (at least one),
 * which are {@link Node}s.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface Element extends java.io.Serializable {
    // empty
}