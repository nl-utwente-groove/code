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
 * $Id: Namespace.java,v 1.1 2007-11-22 15:39:13 fladder Exp $
 */
package groove.control.parse;

import java.util.HashMap;

import antlr.SemanticException;
import antlr.collections.AST;

public class Namespace {

	private HashMap<String, AST> procs = new HashMap<String, AST>();

	/**
	 * Stores the AST of a procedure.
	 * @param name
	 * @param ast
	 */
	public void store(String name, AST ast) {
		this.procs.put(name, ast);
	}
	
	/**
	 * Returns the AST for a procedure
	 * @param name
	 * @return
	 * @throws SemanticException
	 */
	public AST getProc(String name) throws SemanticException {
		AST ast = procs.get(name);
		if( ast == null )
			throw new SemanticException("Procedure not found: \""+ name + "\"");
		return ast;
	}
	
}
