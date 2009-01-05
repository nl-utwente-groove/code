tree grammar GCLChecker;

options {
	tokenVocab=GCL;
	output=AST;
	rewrite=true;
	ASTLabelType=CommonTree;
}

@header {
package groove.control.parse;
import groove.control.*;
}

@members{
	private ControlAutomaton aut;
	
    private Namespace namespace;
	public void setNamespace(Namespace namespace) {
		this.namespace = namespace;
	}
}

program 
  :  ^(PROGRAM functions block) 
  ;

functions
  : ^(FUNCTIONS function*);

function
  : 
  ^(FUNCTION IDENTIFIER block { namespace.store( $IDENTIFIER.text , $block.tree); } )  -> ^(FUNCTION IDENTIFIER);
  
block
  : ^(BLOCK (statement)*)
  ;

statement
  : ^(ALAP block)
  | ^(WHILE condition block)
  | ^(UNTIL condition block)
  | ^(DO block condition)
  | ^(TRY block (block)?)
  | ^(IF condition block (block)?)
  | ^(CHOICE block+)
  | expression
  ;

expression	
	: ^(OR expression expression)
	| ^(PLUS e1=expression) -> ^(PLUS $e1 $e1)
	| ^(STAR expression)
	| ^(SHARP expression)
	| ^(CALL IDENTIFIER)
	| rule
	| ANY
	| OTHER
	; 

condition
  : ^(OR condition condition)
  | rule
  | TRUE
  ;

rule
  : IDENTIFIER
  ;
