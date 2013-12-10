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

/** 
 * Tool tip header annotation.
 * The header is set on the first line, in boldface.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ToolTipHeader {
    /** 
     * Value of the tool tip header.
     */
    String value();
}
