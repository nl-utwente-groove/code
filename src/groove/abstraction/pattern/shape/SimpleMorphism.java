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
package groove.abstraction.pattern.shape;

import groove.util.Fixable;
import groove.view.FormatException;

/**
 * EDUARDO: Comment this...
 * 
 * @author Eduardo Zambon
 */
public class SimpleMorphism implements Fixable {

    private final String name;
    private final TypeNode source;
    private final TypeNode target;
    private boolean fixed;

    /** EDUARDO: Comment this... */
    public SimpleMorphism(String name, TypeNode source, TypeNode target) {
        this.name = name;
        this.source = source;
        this.target = target;
        this.fixed = false;
    }

    @Override
    public void setFixed() throws FormatException {
        this.fixed = true;
    }

    @Override
    public boolean isFixed() {
        return this.fixed;
    }

    @Override
    public void testFixed(boolean fixed) {
        if (isFixed() != fixed) {
            throw new IllegalStateException();
        }
    }

    /** EDUARDO: Comment this... */
    public String getName() {
        return this.name;
    }

    /** EDUARDO: Comment this... */
    public TypeNode getSource() {
        return this.source;
    }

    /** EDUARDO: Comment this... */
    public TypeNode getTarget() {
        return this.target;
    }
}
