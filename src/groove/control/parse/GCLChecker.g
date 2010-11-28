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
import groove.trans.SPORule;
import groove.control.CtrlPar;
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
    SPORule currentRule;
    List<CtrlPar.Var> currentSig;
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
  : ^(CALL r=IDENTIFIER
    { currentRule = namespace.getRule(r.getText());
      if (currentRule != null) {
        currentSig = currentRule.getSignature();
      } 
    } 
    param* 
    {
		//debug("currentRule: "+currentRule.getName().toString());
		debug("checking if "+$r.text+" exists");
		if (!namespace.hasRule($r.text) && !namespace.hasProc($r.text)) {
			errors.add("No such rule: "+$r.text+" on line "+$r.line);
		} else if (numParameters != 0 && numParameters != currentSig.size()) {
			errors.add("The number of parameters used in this call of "+currentRule.getName().toString()+" ("+numParameters+") does not match the number of parameters defined in the rule ("+currentSig.size()+") on line "+$IDENTIFIER.line);
		}
		if (numParameters == 0 && currentRule != null && currentRule.hasRequiredInputs()) {
			errors.add("The rule "+currentRule.getName().toString()+" has required input parameters on line "+$IDENTIFIER.line);
		}
		numParameters = 0;
		currentOutputParameters.clear();
	}) 
  ;

var_declaration
	: ^(VAR t=(NODE_TYPE | BOOL_TYPE | STRING_TYPE | INT_TYPE | REAL_TYPE) IDENTIFIER { 
		if (!namespace.hasVariable($IDENTIFIER.text)) {
			namespace.addVariable($IDENTIFIER.text); 
			st.declareSymbol($IDENTIFIER.text, t.getText());
		} else {
			errors.add("Double declaration of variable '"+$IDENTIFIER.text+"' on line "+$IDENTIFIER.line);
		}
	} )
	;

param
@init{
  numParameters++;
  CtrlPar.Var currentPar = null;
  String currentParType = null;
  if (numParameters <= currentSig.size()) {
    currentPar = currentSig.get(numParameters-1);
    currentParType = currentPar.getType().toString();
  }
}
	: ^(PARAM IDENTIFIER {
			if (st.isDeclared($IDENTIFIER.text)) {
				if (!st.isInitialized($IDENTIFIER.text)) {
					errors.add("The variable "+$IDENTIFIER.text+" might not have been initialized on line "+$IDENTIFIER.line);
				}
			} else {
				errors.add("No such variable: "+$IDENTIFIER.text);
			}
			if (currentRule != null && currentSig.size() < numParameters) {
				errors.add("Rule "+currentRule.getName().toString()+" does not have this many parameters on line "+$IDENTIFIER.line); 
			} else {
				if (currentRule != null && currentPar.isOutOnly()) {
					errors.add("Parameter number "+(numParameters)+" cannot be an input parameter in rule "+currentRule.getName().toString()+" on line "+$IDENTIFIER.line);
				}
				if (currentRule != null && !currentPar.getType().toString().equals(st.getType($IDENTIFIER.text))) {
					errors.add("Type mismatch between parameter "+numParameters+" of "+currentRule.getName().toString()+" and variable "+$IDENTIFIER.text+" ("+currentPar.getType().toString()+" is not "+st.getType($IDENTIFIER.text)+")");
				}
			}
		} )
	| ^(PARAM OUT IDENTIFIER {
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
			if (currentRule != null && currentSig.size() < numParameters) {
				errors.add("Rule "+currentRule.getName().toString()+" does not have this many parameters on line "+$IDENTIFIER.line); 
			} else {
				if (currentOutputParameters.contains($IDENTIFIER.text)) {
					errors.add("You can not use the same parameter as output more than once per call: "+$IDENTIFIER.text+" on line "+$IDENTIFIER.line);			
				}
				if (currentRule != null && !currentParType.equals(st.getType($IDENTIFIER.text))) {
					errors.add("Type mismatch between parameter "+numParameters+" of "+currentRule.getName().toString()+" and variable "+$IDENTIFIER.text+" ("+currentParType+" is not "+st.getType($IDENTIFIER.text)+")");
				}
				if (currentRule != null && currentPar.isInOnly()) {
					errors.add("Parameter "+numParameters+" of rule "+currentRule.getName().toString()+" must be an input parameter.");
				}
			} 
			currentOutputParameters.add($IDENTIFIER.text);
		} )
	| ^(PARAM DONT_CARE { 
		if (currentRule != null && currentPar.isInOnly()) {
			errors.add("Parameter "+numParameters+" of rule "+currentRule.getName().toString()+" must be an input parameter.");
		} 
	})
	| ^(PARAM BOOL_TYPE bool=(TRUE|FALSE) {
		if (currentRule != null && !currentPar.getType().toString().equals("bool")) {
			errors.add("Type mismatch between parameter "+numParameters+" of "+currentRule.getName().toString()+" and '"+bool.getText()+"' on line "+bool.getLine()+" ("+currentParType+" is not bool)");
		}
	})
	| ^(PARAM STRING_TYPE str=IDENTIFIER {
		if (currentRule != null && !currentPar.getType().toString().equals("string")) {
			errors.add("Type mismatch between parameter "+numParameters+" of "+currentRule.getName().toString()+" and "+str.getText()+" on line "+str.getLine()+" ("+currentParType+" is not string)");
		}
	})
	| ^(PARAM INT_TYPE in=IDENTIFIER {
		if (currentRule != null && !currentPar.getType().toString().equals("int")) {
			errors.add("Type mismatch between parameter "+numParameters+" of "+currentRule.getName().toString()+" and '"+in.getText()+"' on line "+in.getLine()+" ("+currentParType+" is not int)");
		}
	})
	| ^(PARAM REAL_TYPE r=IDENTIFIER {
		if (r.getText().equals(".")) {
			errors.add("'.' is not a valid real value on line "+r.getLine());
		}
		if (currentRule != null && !currentPar.getType().toString().equals("real")) {
			errors.add("Type mismatch between parameter "+numParameters+" of "+currentRule.getName().toString()+" and '"+r.getText()+"' on line "+r.getLine()+" ("+currentParType+" is not real)");
		}
	})
	;