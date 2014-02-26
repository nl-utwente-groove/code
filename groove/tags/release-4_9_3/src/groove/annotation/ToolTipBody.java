/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** Tool tip annotation for GROOVE-based Prolog predicates. */
@Retention(RetentionPolicy.RUNTIME)
public @interface ToolTipBody {
    /** 
     * Value of the tool tip.
     * The strings in the array are concatenated to form the main
     * body of the tool tip. 
     */
    String[] value();
}
