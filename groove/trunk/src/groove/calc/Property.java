/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
 * $Id: Property.java,v 1.5 2007-08-26 07:24:21 rensink Exp $
 */
package groove.calc;


/**
 * Interface to wrap a simple condition on a subject type. 
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class Property<S> {
	/** Creates an instance with <code>null</code> comment and description. */
	public Property() {
		this(null, null);
	}
	
	/** Creates an instance with a given description and <code>null</code> comment. */
	public Property(String description) {
		this(description, null);
	}
	
	/** 
	 * Creates an instance with a given description and comment.
	 * @param description the description of the property
	 * @param comment the properyt comment 
	 */
	public Property(String description, String comment) {
		this.description = description;
		this.comment = comment;
	}
	
	/** Indicates if this property is satisfied by a given object of type <code>S</code>. */
	abstract public boolean isSatisfied(S value);
	
	/** 
	 * Provides a description of the value(s) that satisfy this property.
	 * This implementation returns <code>null</code>. 
	 */
	public String getDescription() {
		return description;
	}
	
	/** 
	 * Provides a comment on this property.
	 * This can be a description of the thing the property is testing.
	 * This implementation returns <code>null</code>. 
	 */
	public String getComment() {
		return comment;
	}
	
	/** Comment for this property. */
	private final String comment;
	/** Description of th is proeprty. */
	private final String description;
	
	/** 
	 * Creates and returns a property that returns <code>true</code> on all 
	 * objects of a generic type.
	 */
	static public <T> Property<T> createTrue() {
		return new True<T>();
	}
	
	/** Property subclass that always returns true. */
	static public class True<S> extends Property<S> {
		/** Constructs an instance with <code>null</code> description and comment. */
		public True() {
			this(null);
		}
		
		/** Constructs an instance with <code>null</code> description and a given comment. */
		public True(String comment) {
			super(comment);
		}
		
		@Override
		public boolean isSatisfied(S state) {
			return true;
		}
	}
	
	/** 
	 * Property subclass that tests if a given string represents a boolean value.
	 * This is considered to be the case if the string equals <code>true</code>,
	 * <code>false</code>, or optionally the empty string.
	 * @author Arend Rensink
	 * @version $Revision $
	 */
	static public class IsBoolean extends Property<String> {
		/** 
		 * Constructs an instance with a flag to indicate if the empty
		 * string should be approved.
		 * @param emptyOk if <code>true</code>, the empty string is approved.
		 */
		public IsBoolean(String comment, boolean emptyOk) {
			super(description, comment);
			this.emptyOk = emptyOk;
		}
		
		/** A value is only correct if it is empty, or equals <code>true</code> or <code>false</code>. */
		@Override
		public boolean isSatisfied(String value) {
			return (emptyOk && value.equals("")) || value.equals(trueString) || value.equals(falseString);
		}
		
		/** Flag indicating if the empty string is approved. */
		private final boolean emptyOk;
		
		/** Representation of <code>true</code>. */
		static private final String trueString = Boolean.toString(true);
		/** Representation of <code>false</code>. */
		static private final String falseString = Boolean.toString(false);
		/** The property description. */
		static private final String description = String.format("%s or %s", trueString, falseString);
	}
}
