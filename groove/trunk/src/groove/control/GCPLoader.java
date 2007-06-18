package groove.control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringReader;

import antlr.collections.AST;

/**
 * @author Staijen
 * Loads a control program into a given ControlAutomaton
 */
public class GCPLoader {

	/**
	 * @param args
	 */
	public static void loadProgram(ControlAutomaton ca) throws FileNotFoundException {
		try
        {
			ca.clear();
			
			GCLLexer lexer = new GCLLexer(new StringReader(ca.getProgram()));
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

	public static void loadFile(File controlFile, ControlAutomaton ca) {
		StringBuilder contents = new StringBuilder();
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(controlFile));
			
		
			String line;
			while( ((line = br.readLine()) != null) )
			{
				contents.append(line);
			}
			
			ca.setProgram(contents.toString());

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void saveFile(File controlFile, ControlAutomaton ca) throws FileNotFoundException {
		try
		{
			if( controlFile.canWrite() ) {
				PrintWriter pw = new PrintWriter(controlFile);
				pw.write(ca.getProgram());
			}
		} catch(Exception e) {
			// 
		}
	}
	
}
