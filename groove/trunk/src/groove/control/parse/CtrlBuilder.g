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
import groove.control.CtrlCall.Kind;
import groove.grammar.model.FormatErrorSet;
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

    /**
     * Runs the builder on a given, checked syntax tree.
     */
    public CtrlAut run(CtrlTree tree, Namespace namespace) throws RecognitionException {
        this.builder = CtrlFactory.instance();
        this.namespace = namespace;
        this.helper = new CtrlHelper(this, namespace, null);
        CtrlTreeAdaptor treeAdaptor = new CtrlTreeAdaptor();
        setTreeAdaptor(treeAdaptor);
        setTreeNodeStream(treeAdaptor.createTreeNodeStream(tree));
        CtrlAut result = program().aut;
        return result == null ? null : result.clone(namespace.getFullName());
    }
    
    public FormatErrorSet getErrors() {
        return this.helper.getErrors();
    }
}

program returns [ CtrlAut aut ]
  : ^(PROGRAM package_decl import_decl* functions recipes block)
    { if ($block.tree.getChildCount() == 0) {
          $aut = null;
      } else {
          $aut = $block.aut;
      }
    }
  ;

package_decl
  : ^(PACKAGE ID)
  ;
  
import_decl
  : ^(IMPORT ID)
  ;

functions
  : ^(FUNCTIONS function*)
  ;

function
  : ^(FUNCTION ID block)
    { namespace.addBody(helper.qualify($ID.text), $block.aut); }
  ;
  
recipes
  : ^(RECIPES recipe*)
  ;

recipe
  : ^(RECIPE ID INT_LIT? block)
    { 
      String recipeName = helper.qualify($ID.text);
      helper.checkRecipeBody($RECIPE, recipeName, $block.aut);
      namespace.addBody(recipeName, $block.aut);
    }
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
  : block
    { $aut = $block.aut; }
  | var_decl
    { $aut = builder.buildTrue(); }
  | ^(ALAP s=stat)
    { $aut = builder.buildAlap($s.aut); }
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
  : ^(CALL ID (^(ARGS arg*))?)
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
  