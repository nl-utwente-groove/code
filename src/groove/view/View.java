/* $Id: View.java,v 1.1 2007-04-29 09:22:35 rensink Exp $ */
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
