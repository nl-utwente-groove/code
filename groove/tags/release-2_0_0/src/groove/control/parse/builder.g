///////////////////////////////////////////////////////////////////////////
// Automaton builder
///////////////////////////////////////////////////////////////////////////

header {
package groove.control.parse;
import groove.control.*;
}

class GCLBuilder extends TreeParser;
options {
	buildAST = false;
	importVocab = GCLParser;
}

{
	AutomatonBuilder builder;
    
    public void setBuilder(AutomatonBuilder ab) {
    	this.builder = ab;
    }
    
}

program returns [ControlShape shape=null] { ControlState start; ControlState end; }
  :#(PROGRAM 
  { builder.startProgram(); shape = builder.currentShape(); start = builder.getStart(); end = builder.getEnd(); }
  	(proc)
  { builder.endProgram(); }
	)
  ;

proc
  : #(p:PROC o:IDENTIFIER
    { builder.openScope(o.getText()); }
    block)
	{ builder.closeScope(); }
  ;
  
block { ControlState start = builder.getStart(); ControlState end = builder.getEnd(); }
  : #(BLOCK {ControlState newState = builder.newState(); builder.restore(start, newState);} 
  	(
  		statement
		{ builder.restore(newState, newState = builder.newState());}
   	)*
  ) { builder.rmState(newState); builder.restore(builder.getStart(), end); builder.merge(); }
  ;

statement { ControlState start = builder.getStart(); ControlState end = builder.getEnd(); ControlState newState;}
  : #(ALAP { builder.addElse(); newState = builder.newState(); builder.restore(newState, start); builder.addLambda(); builder.restore(start, newState); }    block)
  | #(WHILE { builder.addElse(); newState = builder.newState(); builder.restore(start, newState); } condition {builder.restore(newState, start);} block )
  | #(DO { newState = builder.newState(); builder.restore(newState, end); builder.addElse(); builder.restore(start, newState); } block { builder.restore(newState, start); } condition)
  | #(TRY { newState = builder.newState(); builder.restore(start, newState); builder.addElse(); builder.restore(start, end);} block {builder.restore(newState, end); } (b:block {if( b == null) builder.merge();} )? )
  | #(IF { newState = builder.newState(); builder.restore(start, newState); } condition {builder.restore(newState, end);} block {newState = builder.newState(); builder.restore(start, newState); builder.addElse(); builder.restore(newState,end); } (e:block {if( e == null) builder.merge();})?)
  | #(CHOICE ( { newState = builder.newState(); builder.restore(start, newState); builder.addLambda(); builder.restore(newState, end); } block)+)
  | expression
  ;

expression { ControlState start = builder.getStart(); ControlState end = builder.getEnd(); ControlState newState; }
	: #(OR expression {builder.restore(start, end);} expression)
	| #(PLUS e:expression {builder.restore(end,end); expression(e); })
	| #(STAR {builder.merge() ;} expression)
	| #(SHARP { builder.addElse(); builder.restore(start, start); } expression)
	| #(PROCUSE i:IDENTIFIER) { proc(builder.getProc(i.getText())); }
	| TRUE { builder.addLambda(); }
	| rule
	; 

condition 
  : expression
  ;

rule
  : i:IDENTIFIER
  { builder.addTransition(i.getText()); }
  ;