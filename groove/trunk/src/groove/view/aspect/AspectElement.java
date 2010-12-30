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
 * $Id: AspectElement.java,v 1.2 2008-01-30 09:31:33 iovka Exp $
 */
package groove.view.aspect;

import groove.graph.Element;
import groove.util.Fixable;

/**
 * Extension of the {@link Element} interface with support for {@link Aspect}s.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface AspectElement extends Element, Fixable {
    /** 
     * Returns the main aspect of this element, if any.
     * At all times, the return value is guaranteed to be valid for the kind of graph.
     * When the graph is fixed, the return value is guaranteed to be non-{@code null}. 
     */
    Aspect getAspect();

    /** 
     * Returns the main aspect kind of this element, if any.
     * At all times, the return value is guaranteed to be valid for the kind of graph.
     * The return value is guaranteed to be non-{@code null}. 
     * Convenience method for {@code getType().getKind()}.
     * @see #getAspect()
     */
    AspectKind getKind();

    /** 
     * Indicates if this element has an attribute-related aspect.
     * @see #getAttrAspect()
     */
    boolean hasAttrAspect();

    /** 
     * Returns the attribute-related aspect of this element, if any. 
     */
    Aspect getAttrAspect();

    /** 
     * Returns the kind of attribute-related aspect for this element, or {@link AspectKind#NONE}.
     * The return value is guaranteed to be valid for the kind of graph,
     * and if not {@link AspectKind#NONE}, to satisfy {@link AspectKind#isAttrKind()}
     * @see #getAttrAspect()
     */
    AspectKind getAttrKind();
}
