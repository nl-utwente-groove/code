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
 * $Id: Label.java,v 1.3 2007-04-29 09:22:27 rensink Exp $
 */
package groove.graph;

import groove.view.FormatException;

/**
 * Interface for edge labels.
 * @author Arend Rensink
 * @version $Revision: 1.3 $ $Date: 2007-04-29 09:22:27 $
 */
public interface Label extends Comparable<Label>, java.io.Serializable {
    /**
     * Returns a label corresponding to a given string.
     */
	@Deprecated
    Label parse(String text) throws FormatException;

    /**
     * Returns the text that this Label carries.
     * As a rule, <code>text()</code> should equal <code>toString()</code>.
     * The <tt>compareTo</tt> method of <tt>Comparable</tt> will, in a standard
     * implementation, be delegated to <code>text()</code>.
     * @ensure result != null
     */
    String text();
}
