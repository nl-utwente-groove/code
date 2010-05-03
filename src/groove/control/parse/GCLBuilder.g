tree grammar GCLBuilder;

options {
	tokenVocab=GCL;
	ASTLabelType=CommonTree;
}

@header {
package groove.control.parse;
import groove.control.*;
import groove.util.Pair;
import groove.trans.Rule;
import groove.graph.GraphInfo;
}

@init {
@SuppressWarnings("all")
}

@members{
	AutomatonBuilder builder;
  String name;
  
    public void setBuilder(AutomatonBuilder ab) {
    	this.builder = ab;
    }
    
    public void setName(String name) {
      this.name = name;
    }
    
    public String getName() {
      return name;
    }
    
    private void proc(CommonTree block) throws RecognitionException {
    	TreeNodeStream restore = input;
    	input = new CommonTreeNodeStream(block);
    	block();
    	this.input = restore;
    }
    
    private ArrayList<Pair<String,Integer>> parameters = new ArrayList<Pair<String,Integer>>();
    
    private void debug(String msg) {
    	if (builder.usesVariables()) {
    		System.err.println("Variables debug (GCLBuilder): "+msg);
    	}
    }
    
    ControlTransition currentTransition;
}

program returns [ControlAutomaton aut=null] 
@init{ ControlState start; ControlState end; }
  : ^(PROGRAM functions {
		aut = builder.startProgram(); 
		GraphInfo.setName(aut, getName());
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
) | ^(TRY
    { 
    	debug("TRY STARTS HERE");
		newState = builder.newState(); 
		builder.copyInitializedVariables(start, newState);
		builder.copyInitializedVariables(start, end);
		builder.restore(start, newState); 
		fail = builder.addElse(); 
		builder.restore(start, end);
		debug("TRY PART ENDS HERE");
	} block {
		debug("BLOCK STARTS HERE ");
		builder.fail(start, fail);
		builder.restore(newState, end); 
		boolean block = false;
		debug("BLOCK ENDS HERE");
	} ( block {
		debug("BLOCK2 STARTS HERE");
		block = true;
		debug("BLOCK2 ENDS HERE");
	} )? {
		debug("TRY PART 2 STARTS HERE");
		if (!block) {
			builder.merge(); 
			builder.tagDelta(start);
		} else {
			builder.initCopy(newState, start);
		}
		debug("TRY ENDS HERE");
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
)+) | expression
	| var_declaration;

expression
@init{
	ControlState start = builder.getStart();
	ControlState end = builder.getEnd();
	ControlState newState;
	ControlTransition fail;
	parameters.clear();
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
) | ^(CALL IDENTIFIER parameter*) {
		if (builder.hasProc($IDENTIFIER.text)) {
			debug("adding proc:"+$IDENTIFIER.text);
			proc(builder.getProc($IDENTIFIER.text)); 
		} else {
			ControlTransition ct = builder.addTransition($IDENTIFIER.text);
			for(Pair<String,Integer> parameter : parameters) {
				debug("adding a parameter: "+$IDENTIFIER.text);
				ct.addParameter(parameter.first(), parameter.second()); 
			}
			ct.setRule(builder.getRule($IDENTIFIER.text));
			currentTransition = ct;
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

var_declaration
  : ^(VAR var_type IDENTIFIER) { builder.addLambda(); }
  ;
  
var_type
  : NODE_TYPE
  | BOOL_TYPE
  | STRING_TYPE
  | INT_TYPE
  | REAL_TYPE
  ;
  
parameter
  : ^(PARAM OUT IDENTIFIER {
  		builder.getEnd().initializeVariable($IDENTIFIER.text); 
  		parameters.add(new Pair<String,Integer>($IDENTIFIER.text, Rule.PARAMETER_OUTPUT));
  	})
  | ^(PARAM IDENTIFIER {
  		parameters.add(new Pair<String,Integer>($IDENTIFIER.text, Rule.PARAMETER_INPUT));
  	})
  | ^(PARAM DONT_CARE {
  		parameters.add(new Pair<String,Integer>("", Rule.PARAMETER_DONT_CARE));
  	})
  | ^(PARAM BOOL_TYPE TRUE)
  | ^(PARAM BOOL_TYPE FALSE)
  | ^(PARAM STRING_TYPE str=STRING {
  		debug("Adding integer parameter: "+str.getText());
  		parameters.add(new Pair<String,Integer>(str.getText(), Rule.PARAMETER_INPUT));
  	})
  | ^(PARAM INT_TYPE in=INT {
  		debug("Adding integer parameter: "+in.getText());
  		parameters.add(new Pair<String,Integer>(in.getText(), Rule.PARAMETER_INPUT));
  })
  | ^(PARAM REAL_TYPE REAL)
  ;