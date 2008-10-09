tree grammar GCLBuilder;

options {
	tokenVocab=GCL;
	ASTLabelType=CommonTree;
}

@header {
package groove.control.parse;
import groove.control.*;
}

@members{
	AutomatonBuilder builder;
    
    public void setBuilder(AutomatonBuilder ab) {
    	this.builder = ab;
    }
    
    private void proc(CommonTree block) throws RecognitionException {
    	TreeNodeStream restore = input;
    	input = new CommonTreeNodeStream(block);
    	block();
    	this.input = restore;
    }

    
}

program returns [ControlShape shape=null] 
@init{ ControlState start; ControlState end; }
  : ^(PROGRAM functions {
		builder.startProgram(); 
  		shape = builder.currentShape(); 
  		start = builder.getStart(); 
  		end = builder.getEnd(); 
	}
  	block {
  		builder.endProgram(); 
  	}
);

functions
  : ^(FUNCTIONS function*);

function
  : ^(FUNCTION IDENTIFIER);

block	
@init { 
	ControlState start = builder.getStart(); 
	ControlState end = builder.getEnd(); 
	boolean empty = true;
	boolean first = true;
	ControlState newState = builder.newState();
  	builder.restore(start, newState);
  	ControlState tmpStart = start;
} : ^(BLOCK (statement {
  				if( !first ) { 
					builder.deltaInitCopy(tmpStart, start); 
				} else {
					first = false;
				}
				tmpStart = newState;
				builder.restore(newState, newState = builder.newState());
				empty = false;
			}
   		)*
	)
{
	builder.rmState(newState);
	builder.restore(builder.getStart(), end);
	builder.merge();
	if( empty ) { builder.tagDelta(start); }
};

statement
@init {
	ControlState start = builder.getStart();
	ControlState end = builder.getEnd();
	ControlState newState;
	ControlTransition fail;
} :	^(ALAP {
		fail = builder.addElse(); 
		newState = builder.newState(); 
		builder.restore(newState, start); 
		builder.addLambda(); 
		builder.restore(start, newState); 
	}
    block {
    	builder.fail(start,fail);
    	builder.tagDelta(start); 
    }
) |	^(WHILE {
		fail = builder.addElse(); 
		newState = builder.newState(); 
		builder.restore(start, newState); 
	} condition	{
		builder.fail(start, fail); 
		builder.restore(newState, start);
	} block {
		builder.deltaInitCopy(newState, start); 
		builder.tagDelta(start);
	}
) | ^(DO {
		newState = builder.newState(); 
		builder.restore(newState, end); 
		fail = builder.addElse(); 
		builder.restore(start, newState);
	} block	{
		builder.restore(newState, start);
	} condition {
		builder.fail(newState,fail);
		builder.tagDelta(newState);
		builder.deltaInitCopy(newState, start);
	}
) | ^(UNTIL {
		newState = builder.newState(); 
		builder.restore(start, end);
	} condition	{
		builder.restore(start, newState); 
		fail = builder.addElse(); 
		builder.fail(start, fail); 
		builder.restore(newState,start);
	} block	{
		builder.tagDelta(newState); 
		builder.deltaInitCopy(newState, start); 
	}
) | ^(TRY { 
		builder.debug("try,enter");
	} { 
		newState = builder.newState(); 
		builder.restore(start, newState); 
		fail = builder.addElse(); 
		builder.restore(start, end);
	} block {
		builder.fail(start, fail); 
		builder.restore(newState, end); 
		boolean block = false;
	} ( block {
		block = true;
	} )? {
		if (!block) {
			builder.merge(); 
			builder.tagDelta(start);
		} else {
			builder.initCopy(newState, start);
		}
	} { 
		builder.debug("try,exit");
	}
) |	^(IF { 
		newState = builder.newState(); 
		builder.restore(start, newState);
	} condition {
		builder.restore(newState, end);
	} block {
		newState = builder.newState(); 
		builder.restore(start, newState); 
		fail = builder.addElse(); 
		builder.fail(start,fail); 
		builder.restore(newState,end); 
		boolean block = false;
	} ( block {
		block = true;
	} )? {
		if (!block) { 
			builder.merge();
			builder.tagDelta(start);
		} else {
			builder.initCopy(newState, start);
		}
	}
) | ^(CHOICE ( { 
		newState = builder.newState(); 
		builder.restore(start, newState); 
		builder.addLambda(); 
		builder.restore(newState, end); 
	} block {
		start.addInit(newState); 
	}
)+) | expression;

expression
@init{
	ControlState start = builder.getStart();
	ControlState end = builder.getEnd();
	ControlState newState;
	ControlTransition fail;
} : ^( OR expression {
		builder.restore(start, end);
	} expression 
) | ^(PLUS expression { 
		builder.restore(end,end);
	} expression
) | ^(STAR {
		newState = builder.newState(); 
		builder.restore(start,newState); 
		builder.addLambda(); 
		builder.restore(newState,newState); 
	} expression {
		builder.restore(newState,end);
		builder.addLambda();
		builder.tagDelta(start);
	}
) | ^(SHARP { 
		fail = builder.addElse();
		builder.restore(start, start); 
	} expression { 
		builder.fail(start,fail); 
	}
) | ^(CALL IDENTIFIER) {
		if (builder.hasProc($IDENTIFIER.text)) {
			proc(builder.getProc($IDENTIFIER.text)); 
		} else {
			builder.addTransition($IDENTIFIER.text);
		}
	}
  | TRUE { 
  		builder.addLambda();
  		builder.tagDelta(start); 
  	}
  | OTHER { 
  		builder.addOther(); 
  	}
  | ANY { 
  		builder.addAny(); 
  	}
  | rule
	; 

condition 
  : expression
  ;

rule
  : IDENTIFIER
  { builder.addTransition($IDENTIFIER.text); }
  ;
