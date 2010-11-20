tree grammar GCLNewChecker;

options {
	tokenVocab=GCLNew;
	output=AST;
	rewrite=true;
	ASTLabelType=MyTree;
}

@header {
package groove.control.parse;
import groove.control.*;
import groove.trans.Rule;
import groove.trans.SPORule;
import java.util.LinkedList;
import java.util.Stack;
import java.util.HashSet;
import java.util.HashMap;
}

@members{
    /** Helper class to convert AST trees to namespace. */
    private GCLHelper helper;
    
    public void displayRecognitionError(String[] tokenNames,
                                        RecognitionException e) {
        String hdr = getErrorHeader(e);
        String msg = getErrorMessage(e, tokenNames);
        helper.addError(hdr + " " + msg);
    }
    
    public List<String> getErrors() {
        return helper.getErrors();
    }

    /**
     * Runs the lexer and parser on a given input character stream,
     * with a (presumably empty) namespace.
     * @return the resulting syntax tree
     */
    public Tree run(MyTree tree, NamespaceNew namespace) throws RecognitionException {
        this.helper = new GCLHelper(this, namespace);
        MyTreeAdaptor treeAdaptor = new MyTreeAdaptor();
        setTreeAdaptor(treeAdaptor);
        setTreeNodeStream(treeAdaptor.createTreeNodeStream(tree));
        return (Tree) program().getTree();
    }
//    
//    private List<String> syntaxInit = new ArrayList<String>();
//    
//    int numParameters = 0;
//    SPORule currentRule;
//    HashSet<String> currentOutputParameters = new HashSet<String>();
//    
//    private void debug(String msg) {
//    	if (namespace.usesVariables()) {
//    		//System.err.println("Variables debug (GCLChecker): "+msg);
//    	}
//    }
}

program 
  :  ^(PROGRAM functions block) 
  ;

functions
  : ^(FUNCTIONS function*);

function
  : ^(FUNCTION ID block)
  ;
  
block
  : ^(BLOCK { helper.openScope(); } (stat)* { helper.closeScope(); })
  ;

stat
  : block
  | var_decl
  | ^(ALAP stat)
  | ^(WHILE stat stat)
  | ^(UNTIL stat stat)
  | ^(TRY stat (stat)?)
  | ^(IF stat stat (stat)?)
  | ^(CHOICE stat+)
  | ^(STAR stat)
  | rule
  | ANY
  | OTHER
  | TRUE
  ;

rule
  : ^(CALL ID arg*) 
    { helper.checkCall($CALL.tree); }
  ;

var_decl
	: ^( VAR type ID )
    { helper.declareVar($ID.tree, $type.tree); }
	;

type
@after{ helper.checkType($tree); }
  : NODE | BOOL | STRING | INT | REAL
  ;
  
arg
	: ^( ARG 
	     ( OUT? ID { helper.checkVarArg($ARG.tree); }
	     | DONT_CARE { helper.checkDontCareArg($ARG.tree); }
	     | literal { helper.checkConstArg($ARG.tree); }
	     )
	   )
	;

literal
  : TRUE
  | FALSE
  | STRING_LIT
  | INT_LIT
  | REAL_LIT
  ;
  