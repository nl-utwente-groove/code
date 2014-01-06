tree grammar CtrlChecker;

options {
	tokenVocab=Ctrl;
	output=AST;
	rewrite=true;
	ASTLabelType=NewCtrlTree;
}

@header {
package groove.control.parse;
import groove.control.*;
import groove.control.CtrlEdge.Kind;
import groove.algebra.AlgebraFamily;
import groove.grammar.model.FormatErrorSet;
import groove.util.antlr.ParseTreeAdaptor;
import groove.util.antlr.ParseInfo;
import java.util.LinkedList;
import java.util.Stack;
import java.util.HashSet;
import java.util.HashMap;
}

@members{
    /** Helper class to convert AST trees to namespace. */
    private CtrlHelper helper;
    
    public void displayRecognitionError(String[] tokenNames,
            RecognitionException e) {
        String hdr = getErrorHeader(e);
        String msg = getErrorMessage(e, tokenNames);
        this.helper.addError(hdr + " " + msg, e.line, e.charPositionInLine);
    }

    /** Constructs a helper class, based on the given name space and algebra. */
    public void initialise(ParseInfo namespace) {
        this.helper = new CtrlHelper((Namespace) namespace);
    }
    
    /**
     * Runs the lexer and parser on a given input character stream,
     * with a (presumably empty) namespace.
     * @return the resulting syntax tree
     */
    public NewCtrlTree run(NewCtrlTree tree, Namespace namespace) throws RecognitionException {
        this.helper = new CtrlHelper(namespace);
        ParseTreeAdaptor treeAdaptor = new ParseTreeAdaptor(new NewCtrlTree());
        setTreeAdaptor(treeAdaptor);
        setTreeNodeStream(treeAdaptor.createTreeNodeStream(tree));
        return (NewCtrlTree) program().getTree();
    }
}

program 
  : ^(PROGRAM package_decl import_decl* functions recipes block) 
    { if ($block.tree.getChildCount() == 0) {
          helper.checkAny($PROGRAM);
      }
    }
  ;

package_decl
  : ^( PACKAGE ID SEMI
       { helper.checkPackage($ID); }
     )
  ;
  
import_decl
  : ^( IMPORT ID SEMI
       { helper.checkImport($ID); }
     )
  ;

recipes
  : ^(RECIPES recipe*)
  ;

recipe
  : ^( RECIPE ID INT_LIT?
       { helper.startBody($ID, Kind.RECIPE); } 
       block
       { helper.endBody(); } 
     )
  ;

functions
  : ^(FUNCTIONS function*)
    { helper.reorderFunctions($FUNCTIONS); }
  ;

function
  : ^( FUNCTION ID
       { helper.startBody($ID, Kind.FUNCTION); } 
       block
       { helper.endBody(); } 
     )
  ;
  
block returns [ CtrlAut aut ]
  : ^( BLOCK
       { helper.openScope(); }
       stat*
       { helper.closeScope(); }
       RCURLY
     )
  ;

stat
  : block
  | ^(SEMI var_decl)
  | ^(SEMI stat)
  | ^(ALAP stat)
  | ^( WHILE
       stat
       { helper.startBranch(); }
       stat
       { helper.endBranch(); }
     )
  | ^( UNTIL
       stat
       { helper.startBranch(); }
       stat
       { helper.endBranch(); }
     )
  | ^( TRY
       { helper.startBranch(); }
       stat
       ( { helper.nextBranch(); }
         stat
       )?
       { helper.endBranch(); }
     )
  | ^( IF 
       stat 
       { helper.startBranch(); }
       stat 
       ( { helper.nextBranch(); }
         stat
       )?
       { helper.endBranch(); }
     )
  | ^( CHOICE
       { helper.startBranch(); }
       stat 
       ( { helper.nextBranch(); }
         stat
       )*
       )
       { helper.endBranch(); }
  | ^( STAR
       { helper.startBranch(); }
       stat
       { helper.endBranch(); }
     )
  | rule
  | ANY
    { helper.checkAny($ANY); }
  | OTHER
    { helper.checkOther($OTHER); }
  | TRUE
  ;

rule
@after{ helper.checkCall($tree); }
  : ^(CALL id=ID (^(ARGS arg* RPAR))?)
  ;

var_decl
	: ^( VAR type
	     //{ helper.checkType($type.tree); }
	     ( ID 
         { helper.declareVar($ID, $type.tree); }
	     )+
	   )
	;

type
  : NODE   { helper.checkType($NODE); }
  | BOOL   { helper.checkType($BOOL); }
  | STRING { helper.checkType($STRING); }
  | INT    { helper.checkType($INT); }
  | REAL   { helper.checkType($REAL); }
  ;
  
arg
	: ^( ARG 
	     ( OUT? ID { helper.checkVarArg($ARG); }
	     | DONT_CARE { helper.checkDontCareArg($ARG); }
	     | literal { helper.checkConstArg($ARG); }
	     )
	   )
	;

literal
  : TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT
  ;
  