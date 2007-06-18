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
            //System.out.println("lexed, now parsing");
            GCLParser parser = new GCLParser(lexer);
            parser.program();
            AST ast = parser.getAST();
            //System.out.println("parsing completed");
            
            /*
            ASTFrame frame = new ASTFrame("Parse Tree", ast);
            frame.setSize(500,500);
            frame.setVisible(true);
			*/
            
            GCLBuilder builder = new GCLBuilder(ca);
            builder.program(parser.getAST());
            //System.out.println("Done loading " + controlFile.getName());
        }
		catch(Exception e)
		{
			System.err.println(e.getMessage());
			System.err.println("Error loading " + controlFile.getAbsolutePath());
		}
	}

}
