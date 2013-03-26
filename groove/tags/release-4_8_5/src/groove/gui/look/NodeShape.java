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
package groove.gui.look;

/**
 * Node shapes.
 * @author Arend Rensink
 * @version $Revision $
 */
public enum NodeShape {
    /** Rounded rectangle shape. */
    ROUNDED,
    /** Ellipse (or circle) shape. */
    ELLIPSE,
    /** Diamond shape. */
    DIAMOND,
    /** Sharp-cornered rectangle shape. */
    RECTANGLE,
    /** Oval shape (rounded rectangle with larger rounding arc). */
    OVAL;
}
