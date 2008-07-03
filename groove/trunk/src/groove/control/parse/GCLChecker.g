tree grammar GCLChecker;

options {
	tokenVocab=GCL;
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
  :  ^(PROGRAM proc* statement*) 
  ;

proc
  : 
  ^(FUNCTION IDENTIFIER block)  { namespace.store( $IDENTIFIER.text , $FUNCTION); }
  ;
  
block
  : ^(BLOCK (statement)*)
  ;

statement
  : ^(ALAP block)
  | ^(WHILE condition DO block)
  | ^(DO block WHILE condition)
  | ^(TRY block (block)?)
  | ^(IF condition block (ELSE block)?)
  | ^(CHOICE block (OR block)*  )
  | expression
  ;

expression	
	: ^(OR expression expression)
	| ^(PLUS expression)
	| ^(STAR expression)
	| ^(SHARP expression)
	| ^(CALL IDENTIFIER)
	| rule
	; 

condition
  : ^(OR condition condition)
  | rule
  | 'true'
  ;

rule
  : IDENTIFIER
  ;
