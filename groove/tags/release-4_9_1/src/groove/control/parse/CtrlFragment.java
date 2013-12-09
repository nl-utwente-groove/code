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
package groove.control.parse;

/** Fragment of a control program, corresponding to a recipe declaration. */
public class CtrlFragment {
    /**
     * Creates a control fragment.
     * @param name control program name
     * @param startLine start line
     */
    public CtrlFragment(String name, int startLine) {
        super();
        this.name = name;
        this.startLine = startLine;
    }

    /** Returns the full name of the enclosing control program. */
    public String getName() {
        return this.name;
    }

    /** Returns the start line of the fragment within the control program. */
    public int getStartLine() {
        return this.startLine;
    }

    private final String name;
    private final int startLine;
}
