// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/* 
 * $Id: NAC.java,v 1.4 2007-10-03 23:10:53 rensink Exp $
 */
package groove.trans;

import groove.graph.Graph;
import groove.graph.NodeEdgeMap;

/**
 * A NAC, or <i>Negative Application Condition</i>, implements the standard concept
 * in graph transformations. That is, it defined conditions under which a transformation
 * rule may <i>not</i> be applied.
 * In retrospect, a NAC is just a first level {@link GraphCondition}. However, two
 * special subclasses, {@link MergeEmbargo} and {@link EdgeEmbargo}, are noteworthy
 * because they allow performance optimisations to be made during matching.
 * @author Arend Rensink
 * @version $Revision: 1.4 $
 */
@Deprecated
public interface NAC extends GraphCondition {
	/** Tests if the NAC forbids a certain match.
	 * @deprecated use {@link #matches(Graph, NodeEdgeMap)} instead. 
	 */
	@Deprecated
    public boolean forbids(groove.rel.VarMorphism match);
}
