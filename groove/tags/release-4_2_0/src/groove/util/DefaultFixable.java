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
 * $Id$
 */
package groove.util;

/**
 * Default implementation of the {@link Fixable} interface.
 * @author Arend
 * @version $Revision $
 */
public class DefaultFixable implements Fixable {
    /**
     * Freezes the properties object, after which changing any properties
     * becomes illegal.
     */
    public void setFixed() {
        this.fixed = true;
    }

    /**
     * Indicates if the properties are fixed. If so, any attempt to modify any
     * of the properties will result in an {@link IllegalStateException}.
     * @return <code>true</code> if the properties are fixed.
     */
    public boolean isFixed() {
        return this.fixed;
    }

    public void testFixed(boolean fixed) throws IllegalStateException {
        if (this.fixed != fixed) {
            throw new IllegalStateException(String.format(
                "Expected fixed = %b", fixed));
        }
    }

    /** Flag indicating if the object is currently fixed. */
    private boolean fixed;
}
