tree grammar CtrlChecker;

options {
	tokenVocab=Ctrl;
	output=AST;
	rewrite=true;
	ASTLabelType=CtrlTree;
}

@header {
package groove.control.parse;
import groove.control.*;
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
}

program 
@init { helper.clearErrors(); }
  : ^(PROGRAM package_decl imports functions recipes block) 
  ;

package_decl
  : ^( PACKAGE qual_id SEMI
       { helper.checkPackage($qual_id.tree); }
     )
  ;

imports
  : ^(IMPORTS import_decl*)
  ;

import_decl
  : ^( IMPORT qual_id SEMI
       { helper.checkImport($qual_id.tree); }
     )
  ;

recipes
  : ^(RECIPES recipe*)
  ;

recipe
  : ^( RECIPE
       { helper.startBody($RECIPE); } 
       ID ^(PARS par_decl*) INT_LIT?
       block
       { helper.endBody($block.tree); } 
     )
  ;

functions
  : ^( FUNCTIONS function*)
  ;

function
  : ^( FUNCTION
       { helper.startBody($FUNCTION); }
       ID ^(PARS par_decl*)
       block
       { helper.endBody($block.tree); } 
     )
  ;
  
par_decl
  : ^(PAR OUT? type ID)
    { helper.declarePar($ID, $type.tree, $OUT); }
  ;
  
block
  : ^( BLOCK
       { helper.openScope(); }
       stat*
       { helper.closeScope(); }
     )
  ;

stat
  : block
  | ^(SEMI var_decl)
  | ^(SEMI stat)
  | ^(ALAP stat)
  | ^(ATOM stat)
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
       { helper.startBranch(); }
       stat 
       stat
       { helper.nextBranch(); }
       stat?
       { helper.endBranch(); }
     )
  | ^( CHOICE
       { helper.startBranch(); }
       stat 
       ( { helper.nextBranch(); }
         stat
       )*
       { helper.endBranch(); }
     )
  | ^( STAR
       { helper.startBranch(); }
       stat
       { helper.endBranch(); }
     )
  | rule
  | TRUE
  ;

rule
@after{ helper.checkGroupCall($tree); }
  : ^(CALL qual_id (^(ARGS arg* RPAR))?)
  ;

var_decl
	: ^( VAR type
	     ( ID 
         { helper.declareVar($ID, $type.tree); }
	     )+
	   )
	;

qual_id
  : ^((ID|ANY|OTHER) ID)
  // the second ID is a traceability token to keep track of the original
  // last token of the qualified ID
  ;

type
  // no idea why, but for some reason without the rewriting
  // the result tree is empty
  : NODE -> NODE
  | BOOL -> BOOL
  | STRING -> STRING
  | INT -> INT
  | REAL -> REAL
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
  