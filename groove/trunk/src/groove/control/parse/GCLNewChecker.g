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
    /** Builder for control automata. */
    private CtrlFactory builder;
    
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
        this.builder = new CtrlFactory();
        MyTreeAdaptor treeAdaptor = new MyTreeAdaptor();
        setTreeAdaptor(treeAdaptor);
        setTreeNodeStream(treeAdaptor.createTreeNodeStream(tree));
        return (Tree) program().getTree();
    }
}

program 
  :  ^(PROGRAM functions block) 
  ;

functions
  : ^(FUNCTIONS function*);

function
  : ^(FUNCTION ID block)
  ;
  
block returns [ CtrlAut aut ]
  : ^( BLOCK
       { $aut = builder.buildTrue();
         helper.openScope(); 
       }
       ( stat
         { $aut = builder.buildSeq($aut, $stat.aut); }
       )*
       { helper.closeScope(); }
     )
  ;

stat returns [ CtrlAut aut ]
  : block
    { $aut = $block.aut; }
  | var_decl
    { $aut = builder.buildTrue(); }
  | ^(ALAP s=stat)
    { $aut = builder.buildAlap($s.aut); }
  | ^(WHILE c=stat s=stat)
    { $aut = builder.buildWhileDo($c.aut, $s.aut); }
  | ^(UNTIL stat stat)
    { $aut = builder.buildUntilDo($c.aut, $s.aut); }
  | ^(TRY s1=stat (s2=stat)?)
    { $aut = builder.buildTryElse($s1.aut, $s2.aut); }
  | ^(IF c=stat s1=stat (s2=stat)?)
    { $aut = builder.buildIfThenElse($c.aut, $s1.aut, $s2.aut); }
  | ^( CHOICE 
       s1=stat
       { $aut = $s1.aut; }
       ( s2=stat
         { $aut = builder.buildOr($aut, $s2.aut); }
       )*
     )
  | ^(STAR s=stat)
    { $aut = builder.buildStar($s.aut); }
  | rule
  | ANY
  | OTHER
  | TRUE
    { $aut = builder.buildTrue(); }
  ;

rule
  : ^(CALL ID arg*) 
    { helper.checkCall($CALL.tree); }
  ;

var_decl
	: ^( VAR type
	     ( ID 
         { helper.declareVar($ID.tree, $type.tree); }
	     )+
	   )
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
  