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
 * $Id: AbstractLabel.java,v 1.3 2007-06-27 11:55:16 rensink Exp $
 */
package groove.graph;

/**
 * Provides a partial implementation of the Label interface,
 * consisting only of a label text.
 * @author Arend Rensink
 * @version $Revision: 1.3 $ $Date: 2007-06-27 11:55:16 $
 */
public abstract class AbstractLabel implements Cloneable, Label {
	/** This implementation delegates to {@link #text()}. */
    public String plainText() {
		return text();
	}

    /** This implementation compares this label's {@link #text()} with that of <code>obj</code>. */
	public int compareTo(Label obj) {
        return text().compareTo(obj.text());
    }

    /** This implementation compares this label's {@link #text()} with that of <code>obj</code>. */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Label) && text().equals(((Label) obj).text());
    }

    /** This implementation delegates to to {@link #text()}. */
    @Override
    public int hashCode() {
        return text().hashCode();
    }

    /** This implementation delegates to to {@link #text()}. */
    @Override
    public String toString() {
        return text();
    }
}
