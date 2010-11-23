tree grammar GCLNewBuilder;

options {
	tokenVocab=GCLNew;
	output=AST;
	rewrite=true;
	ASTLabelType=MyTree;
}

@header {
package groove.control.parse;
import groove.control.*;
import groove.trans.SPORule;
import java.util.Set;
}

@members{
    /** Builder for control automata. */
    private CtrlFactory builder;
    /** Set of all rules (needed for building the ANY automaton). */
    private Set<SPORule> allRules;
    /** Set of non-invoked rules (needed for building the OTHER automaton). */
    private Set<SPORule> uncontrolledRules;

    /**
     * Runs the builder on a given, checked syntax tree.
     */
    public Tree run(MyTree tree, Set<SPORule> allRules, Set<SPORule> uncontrolledRules) throws RecognitionException {
        this.builder = new CtrlFactory();
        this.uncontrolledRules = uncontrolledRules;
        this.allRules = allRules;
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
    { $aut = builder.buildCall($rule.tree.getCtrlCall()); }
  | ANY
    { $aut = builder.buildCallChoice(allRules); }
  | OTHER
    { $aut = builder.buildCallChoice(uncontrolledRules); }
  | TRUE
    { $aut = builder.buildTrue(); }
  ;

rule
  : ^(CALL ID arg*) 
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
  