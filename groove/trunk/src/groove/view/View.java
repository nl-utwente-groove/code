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
 * $Id: View.java,v 1.3 2007-08-26 07:24:10 rensink Exp $
 */
package groove.view;

import java.util.List;

/**
 * General interface for classes that provide a view upon some other object
 * (the model). This is not a view in the MVC sense; here, 
 * the view is more a kind a syntax for the model. This syntax may still
 * contain errors which prevent it from being translated to a model.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface View<Model> {
	/** 
	 * Returns the (non-<code>null</code>) name of the underlying model.
	 */
	String getName();
	
	/** 
	 * Returns the underlying model. 
	 * This can only be successful if there are no syntax errors reported by 
	 * {@link #getErrors()}.
	 * @throws FormatException if there are syntax errors in the view that
	 * prevent it from being translated to a model
	 */
	Model toModel() throws FormatException;
	
	/** 
	 * Retrieves the list of syntax errors in this view.
	 * Conversion to a model can only be successful if this list is empty.
	 * @return a non-<code>null</code>, possibly empty list of syntax errors
	 */
	List<String> getErrors();
}
