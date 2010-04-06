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
import groove.trans.Rule;
import java.util.LinkedList;
import java.util.Stack;
import java.util.HashSet;
import java.util.HashMap;
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
    
    private List<String> syntaxInit = new ArrayList<String>();
    
    int numParameters = 0;
    String currentRule;
    HashSet<String> currentOutputParameters = new HashSet<String>();
    
    private void debug(String msg) {
    	if (namespace.usesVariables()) {
    		//System.err.println("Variables debug (GCLChecker): "+msg);
    	}
    }
}

program 
  :  ^(PROGRAM functions block) 
  ;

functions
  : ^(f=FUNCTIONS { namespace.storeProcs(f); } function*);

function
  : ^(FUNCTION IDENTIFIER block {
  		debug("proc: "+$IDENTIFIER.text);
  		if (namespace.hasRule($IDENTIFIER.text)) {
  			errors.add("There already exists a rule with the name: "+$IDENTIFIER.text);
  		}
  	} );
  
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
  : ^(CALL r=IDENTIFIER { currentRule = r.getText(); } param* {
		debug("currentRule: "+currentRule);
		debug("checking if "+$r.text+" exists");
		if (!namespace.hasRule($r.text) && !namespace.hasProc($r.text)) {
			errors.add("No such rule: "+$r.text+" on line "+$r.line);
		} else if (numParameters != 0 && numParameters != namespace.getRule(currentRule).getVisibleParCount()) {
			errors.add("The number of parameters used in this call of "+currentRule+" ("+numParameters+") does not match the number of parameters defined in the rule ("+namespace.getRule(currentRule).getVisibleParCount()+") on line "+$IDENTIFIER.line);
		}
		numParameters = 0;
		currentOutputParameters.clear();
	}) 
  ;

var_declaration
	: ^(VAR var_type IDENTIFIER { 
		if (!namespace.hasVariable($IDENTIFIER.text)) {
			namespace.addVariable($IDENTIFIER.text); st.declareSymbol($IDENTIFIER.text);
		} else {
			errors.add("Double declaration of variable '"+$IDENTIFIER.text+"' on line "+$IDENTIFIER.line);
		}
	} )
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
			if (namespace.getRule(currentRule) != null && !namespace.getRule(currentRule).isInputParameter(numParameters)) {
				errors.add("Parameter number "+(numParameters)+" cannot be an input parameter in rule "+currentRule+" on line "+$IDENTIFIER.line);
			}
		} )
	| ^(PARAM OUT IDENTIFIER {
			numParameters++;
			
			if (st.isDeclared($IDENTIFIER.text)) {
				if (!st.canInitialize($IDENTIFIER.text) || syntaxInit.contains($IDENTIFIER.text)) {
					errors.add("Variable already initialized: "+$IDENTIFIER.text+" on line "+$IDENTIFIER.line);
				} else {
					st.initializeSymbol($IDENTIFIER.text);
					syntaxInit.add($IDENTIFIER.text);
				}
			} else {
				errors.add("No such variable: "+$IDENTIFIER.text+" on line "+$IDENTIFIER.line);
			}
			if (namespace.getRule(currentRule) != null && !namespace.getRule(currentRule).isOutputParameter(numParameters)) {
				errors.add("Parameter number "+(numParameters)+" cannot be an output parameter in rule "+currentRule+" on line "+$IDENTIFIER.line);
			}
			if (currentOutputParameters.contains($IDENTIFIER.text)) {
				errors.add("You can not use the same parameter as output more than once per call: "+$IDENTIFIER.text+" on line "+$IDENTIFIER.line);			
			}
			currentOutputParameters.add($IDENTIFIER.text);
		} )
	| ^(PARAM DONT_CARE { numParameters++; })
	;