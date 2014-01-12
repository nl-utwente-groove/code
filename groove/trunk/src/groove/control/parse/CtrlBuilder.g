tree grammar CtrlBuilder;

options {
	tokenVocab=Ctrl;
	output=AST;
	rewrite=true;
	ASTLabelType=CtrlTree;
}

@header {
package groove.control.parse;
import groove.control.*;
import groove.grammar.model.FormatErrorSet;
import groove.util.antlr.ParseTreeAdaptor;
import groove.util.antlr.ParseInfo;
import java.util.Set;
import java.util.HashSet;
}

@members{
    /** Builder for control automata. */
    private CtrlFactory builder;
    /** Namespace used for building the automaton. */
    private Namespace namespace;
    /** Helper class for some final static semantic checks. */
    private CtrlHelper helper;

    /** Initialises the internal variables, based on the given name space. */
    public void initialise(ParseInfo namespace) {
        this.builder = CtrlFactory.instance();
        this.namespace = (Namespace) namespace;
        this.helper = new CtrlHelper(this.namespace);
    }
}

program returns [ CtrlAut aut ]
  : ^(PROGRAM package_decl import_decl* functions recipes block)
    { // at least one child due to closing TRUE
      if ($block.tree.getChildCount() == 1) {
          $aut = null;
      } else {
          $aut = $block.aut;
      }
    }
  ;

package_decl
  : ^(PACKAGE ID SEMI)
  ;
  
import_decl
  : ^(IMPORT ID SEMI)
  ;

functions
  : ^(FUNCTIONS function*)
  ;

function
  : ^(FUNCTION ID par_list priority block)
    { 
      namespace.addBody(helper.qualify($ID.text), $block.aut);
    }
  ;
  
recipes
  : ^(RECIPES recipe*)
  ;

recipe
  : ^(RECIPE ID par_list priority block)
    { 
      String recipeName = helper.qualify($ID.text);
      helper.checkRecipeBody($RECIPE, recipeName, $block.aut);
      namespace.addBody(recipeName, $block.aut);
    }
  ;

priority
  : ( INT_LIT )?
  ;

par_list
  : ^(PARS par_decl* )
  ;

par_decl
  : ^(PAR OUT? type ID)
  ;

block returns [ CtrlAut aut ]
  : ^( BLOCK
       { $aut = builder.buildTrue(); }
       ( stat
         { $aut = builder.buildSeq($aut, $stat.aut); }
       )*
     )
  ;

stat returns [ CtrlAut aut ]
  : ^(SEMI s=stat)
    { $aut = $s.aut; }
  | block
    { $aut = $block.aut; }
  | var_decl
    { $aut = builder.buildTrue(); }
  | ^(ALAP s=stat)
    { $aut = builder.buildAlap($s.aut); }
  | ^(ATOM s=stat)
    { helper.emitErrorMessage($s.tree, "Atomic blocks are not supported in this version");
      $aut = $s.aut;
    }
  | ^(WHILE c=stat s=stat)
    { $aut = builder.buildWhileDo($c.aut, $s.aut); }
  | ^(UNTIL c=stat s=stat)
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
    { $aut = builder.buildCall($rule.tree.getCtrlCall(), namespace); }
  | ANY
    { $aut = builder.buildAny(namespace); }
  | OTHER
    { $aut = builder.buildOther(namespace); 
    }
  | TRUE
    { $aut = builder.buildTrue(); }
  ;

rule
  : ^(CALL ID (^(ARGS arg* RPAR))?)
  ;

var_decl
	: ^( VAR type ID+ )
	;

type
  : NODE | BOOL | STRING | INT | REAL
  ;
  
arg
	: ^( ARG ( OUT? ID | DONT_CARE | literal ) )
	;

literal
  : TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT
  ;
  