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
 * $Id: ParserTester.java,v 1.1 2008-01-30 09:33:49 iovka Exp $
 */
package groove.control.test;

import groove.control.parse.AutomatonBuilder;
import groove.control.parse.GCLBuilder;
import groove.control.parse.GCLChecker;
import groove.control.parse.GCLLexer;
import groove.control.parse.GCLParser;

import java.io.File;

import antlr.collections.AST;
import antlr.debug.misc.ASTFrame;


/**
 * This should not be in the CVS
 * @author Tom Staijen
 * @version $Revision $
 */
public class ParserTester {
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		try
		{
			File file = new File("src/sgroove/control/parse/test.gcl");
			
			GCLLexer lexer = new GCLLexer(new java.io.FileInputStream(new File("src/groove/control/test/test.gcl")));
            
			GCLParser parser = new GCLParser(lexer);
            parser.program();
            GCLChecker checker = new GCLChecker();
            
            AutomatonBuilder b = new AutomatonBuilder();
           
            checker.setNamespace(b);
            checker.program(parser.getAST());
            
            GCLBuilder builder = new GCLBuilder();
            builder.setBuilder(b);
            
            builder.program(parser.getAST());
            
            AST ast = parser.getAST();
            ASTFrame frame = new ASTFrame("Control Program", ast);
            frame.setSize(400,800);
            frame.setVisible(true);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
}
