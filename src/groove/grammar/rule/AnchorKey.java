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
package groove.grammar.rule;

import org.eclipse.jdt.annotation.NonNullByDefault;

import groove.grammar.AnchorKind;

/**
 * Type of element that can occur in an anchor.
 * Anchors are used to fix a match of a rule graph in a host graph.
 * @author Arend Rensink
 * @version $Revision $
 */
@NonNullByDefault
public interface AnchorKey {
    /** Returns the kind of anchor key of this object. */
    AnchorKind getAnchorKind();
}
