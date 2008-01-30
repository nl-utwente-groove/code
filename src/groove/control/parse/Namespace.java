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
 * $Id: Namespace.java,v 1.3 2008-01-30 09:33:33 iovka Exp $
 */
package groove.control.parse;

import groove.trans.GraphGrammar;
import groove.trans.Rule;
import groove.trans.RuleNameLabel;
import groove.view.DefaultGrammarView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import antlr.SemanticException;
import antlr.collections.AST;

/**
 * Namespace class for the AutomatonBuilder (for checking).
 * Can be used to store names with an optional referenced Object.
 * 
 * @author Tom Staijen
 * @version $Revision $
 */
public class Namespace {

	private Set<String> ruleNames = new HashSet();
	
	private GraphGrammar grammar;
	private DefaultGrammarView view;

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
	 * @return AST
	 * @throws SemanticException
	 */
	public AST getProc(String name) throws SemanticException {
		AST ast = procs.get(name);
		if( ast == null )
			throw new SemanticException("Procedure not found: \""+ name + "\"");
		return ast;
	}
	
	public boolean ruleExists(String name) throws SemanticException {
		 return ruleNames.contains(name);
	}
	
	public void setRuleNames(DefaultGrammarView grammarView) {
		for( RuleNameLabel rule : grammarView.getRuleMap().keySet() ) {
			this.ruleNames.add(rule.text());
		}
	}
	
}
