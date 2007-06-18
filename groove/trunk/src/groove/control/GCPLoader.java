package groove.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import antlr.collections.AST;
import antlr.debug.misc.ASTFrame;

/**
 * @author Staijen
 * Loads a control program into a given ControlAutomaton
 */
public class GCPLoader {

	/**
	 * @param args
	 */
	public static void loadFile(File controlFile, ControlAutomaton ca) throws FileNotFoundException {
		try
        {
			GCLLexer lexer = new GCLLexer(new FileInputStream(controlFile));
            GCLParser parser = new GCLParser(lexer);
            parser.program();
            AST ast = parser.getAST();
            
            GCLBuilder builder = new GCLBuilder(ca);
            builder.program(parser.getAST());
        }
		catch(Exception e)
		{
			//System.err.println(e.getMessage());
			//System.err.println("Error loading " + controlFile.getAbsolutePath());
		}
	}

}
