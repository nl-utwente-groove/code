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
import java.util.LinkedList;
import java.util.Stack;
import java.util.HashSet;
}

@members{
	private ControlAutomaton aut;
	
    private Namespace namespace;
	public void setNamespace(Namespace namespace) {
		this.namespace = namespace;
	}
	
	private SymbolTable st = new SymbolTable();

    private List<String> errors = new LinkedList<String>();
    public void displayRecognitionError(String[] tokenNames,
                                        RecognitionException e) {
        String hdr = getErrorHeader(e);
        String msg = getErrorMessage(e, tokenNames);
        errors.add(hdr + " " + msg);
    }
    public List<String> getErrors() {
        return errors;
    }
    
    int numParameters = 0;
    String currentRule;
    HashSet<String> currentOutputParameters = new HashSet<String>();
    
    private void debug(String msg) {
    	if (namespace.usesVariables()) {
    		System.err.println("Variables debug (GCLChecker): "+msg);
    	}
    }
}

program 
  :  ^(PROGRAM functions block) 
  ;

functions
  : ^(FUNCTIONS function*);

function
  : 
  ^(FUNCTION IDENTIFIER block {
  		if (namespace.hasRule($IDENTIFIER.text)) {
  			errors.add("There already exists a rule with the name: "+$IDENTIFIER.text);
  		} else if (namespace.hasProc($IDENTIFIER.text)) {
  			errors.add("Multiple definitions of the function: "+$IDENTIFIER.text);
  		} else {
  			namespace.store( $IDENTIFIER.text , $block.tree);
  		} 
  	} )  -> ^(FUNCTION IDENTIFIER);
  
block
  : ^(BLOCK { st.openScope(); } (statement)* { st.closeScope(); })
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
  | var_declaration
  ;

expression	
	: ^(OR expression expression)
	| ^(PLUS e1=expression) -> ^(PLUS $e1 $e1)
	| ^(STAR expression)
	| ^(SHARP expression)
	| ^(CALL r=IDENTIFIER { currentRule = r.getText(); } param* {
		debug("currentRule: "+currentRule);
		if (numParameters != 0 && numParameters != namespace.getRule(currentRule).getNumParameters()) {
			errors.add("The number of parameters used in this call of "+currentRule+" ("+numParameters+") does not match the number of parameters defined in the rule ("+namespace.getRule(currentRule).getNumParameters()+") on line "+$IDENTIFIER.line);
		}
		numParameters = 0;
		currentOutputParameters.clear();
	}) 
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

var_declaration
	: ^(VAR var_type IDENTIFIER { namespace.addVariable($IDENTIFIER.text); st.declareSymbol($IDENTIFIER.text); } )
	;

var_type
	: NODE_TYPE
	;

param
	: ^(PARAM IDENTIFIER {
			numParameters++;
			if (st.isDeclared($IDENTIFIER.text)) {
				if (!st.isInitialized($IDENTIFIER.text)) {
					errors.add("The variable "+$IDENTIFIER.text+" might not have been initialized on line "+$IDENTIFIER.line);
				}
			} else {
				errors.add("No such variable: "+$IDENTIFIER.text);
			}
			if (!namespace.getRule(currentRule).isInputParameter(numParameters)) {
				errors.add("Parameter number "+(numParameters)+" cannot be an input parameter in rule "+currentRule+" on line "+$IDENTIFIER.line);
			}
		} )
	| ^(PARAM OUT IDENTIFIER {
			numParameters++;
			
			if (st.isDeclared($IDENTIFIER.text)) {
				if (!st.canInitialize($IDENTIFIER.text)) {
					errors.add("You are not allowed to initialize a variable in more than one location. "+$IDENTIFIER.text+" on line "+$IDENTIFIER.line);
				} else {
					st.initializeSymbol($IDENTIFIER.text);
				}
			} else {
				errors.add("No such variable: "+$IDENTIFIER.text+" on line "+$IDENTIFIER.line);
			}
			if (!namespace.getRule(currentRule).isOutputParameter(numParameters)) {
				errors.add("Parameter number "+(numParameters)+" cannot be an output parameter in rule "+currentRule+" on line "+$IDENTIFIER.line);
			}
			if (currentOutputParameters.contains($IDENTIFIER.text)) {
				errors.add("You can not use the same parameter as output more than once per call: "+$IDENTIFIER.text+" on line "+$IDENTIFIER.line);			
			}
			currentOutputParameters.add($IDENTIFIER.text);
		} )
	| ^(PARAM DONT_CARE { numParameters++; })
	;