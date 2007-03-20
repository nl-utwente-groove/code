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
/* $Id: WrapperLabel.java,v 1.1.1.1 2007-03-20 10:05:36 kastenberg Exp $ */
package groove.graph;

/**
 * Label class that wraps an object of a given (generic) type.
 * @author Arend Rensink
 * @version $Revision $
 */
public class WrapperLabel<Type extends Comparable<Type>> implements Label {
	/** Constructs a label wrapping a given object. */
	public WrapperLabel(Type content) {
		this.content = content;
	}
	
	@Deprecated
	public Label parse(String text) throws GraphFormatException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns a string representation of the wrapped object.
	 */
	public String text() {
		return convertToText(getContent());
	}
	
	/**
	 * Callback method from {@link #toString()}, to
	 * convert the wrapped object to a string.
	 */
	protected String convertToText(Type object) {
		return object.toString();
	}

	/**
	 * If the other is also a {@link WrapperLabel}, attempts to compare the
	 * contents; otherwise, compares the text of both labels.
	 */
	public int compareTo(Label other) {
		if (other instanceof WrapperLabel) {
			return getContent().compareTo(((WrapperLabel<Type>) other).getContent());
		} else {
			return text().compareTo(other.text());
		}
	}

	/**
	 * Tests if the other is also a {@link WrapperLabel} with the same content.
	 */
    public boolean equals(Object obj) {
    	if (obj instanceof WrapperLabel) {
    		return getContent().equals(((WrapperLabel<?>) obj).getContent());
    	} else {
    		return false;
    	}
    }

    /**
     * Returns the hash code of the wrapped object.
     */
    public int hashCode() {
        return getContent().hashCode();
    }

    /**
     * Returns the text of this label.
     * @see #text()
     */
    public String toString() {
        return text();
    }

	/**
	 * Returns the wrapped object of this label.
	 */
	public Type getContent() {
		return content;
	}

	private final Type content;
}
