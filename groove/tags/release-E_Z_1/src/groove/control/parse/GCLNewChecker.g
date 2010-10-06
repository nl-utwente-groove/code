tree grammar GCLNewChecker;

options {
	tokenVocab=GCLNew;
	output=AST;
	rewrite=true;
	ASTLabelType=CommonTree;
}

@header {
package groove.control.parse;
import groove.control.*;
import groove.trans.Rule;
import groove.trans.SPORule;
import java.util.LinkedList;
import java.util.Stack;
import java.util.HashSet;
import java.util.HashMap;
}

@members{
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
  : ^(FUNCTION ID block {
  		debug("proc: "+$ID.text);
  		if (namespace.hasRule($ID.text)) {
  			errors.add("There already exists a rule with the name: "+$ID.text);
  		}
  	} );
  
block
  : ^(BLOCK { st.openScope(); } (stat)* { st.closeScope(); })
  ;

stat
  : block
  | var_decl
  | ^(ALAP stat)
  | ^(WHILE stat stat)
  | ^(UNTIL stat stat)
  | ^(TRY stat (stat)?)
  | ^(IF stat stat (stat)?)
  | ^(CHOICE stat+)
  | ^(STAR stat)
  | rule
  | ANY
  | OTHER
  | TRUE
  ;

rule
  : ^(CALL r=ID { currentRule = namespace.getRule(r.getText()); } arg* {
		debug("checking if "+$r.text+" exists");
		if (!namespace.hasRule($r.text) && !namespace.hasProc($r.text)) {
			errors.add("No such rule: "+$r.text+" on line "+$r.line);
		} else if (numParameters != 0 && numParameters != currentRule.getVisibleParCount()) {
			errors.add("The number of parameters used in this call of "+currentRule.getName().toString()+" ("+numParameters+") does not match the number of parameters defined in the rule ("+currentRule.getVisibleParCount()+") on line "+$ID.line);
		}
		if (numParameters == 0 && currentRule != null && currentRule.hasRequiredInputs()) {
			errors.add("The rule "+currentRule.getName().toString()+" has required input parameters on line "+$ID.line);
		}
		numParameters = 0;
		currentOutputParameters.clear();
	}) 
  ;

var_decl
	: ^(VAR t=(NODE_TYPE | BOOL_TYPE | STRING_TYPE | INT_TYPE | REAL_TYPE) ID { 
		if (!namespace.hasVariable($ID.text)) {
			namespace.addVariable($ID.text); 
			st.declareSymbol($ID.text, t.getText());
		} else {
			errors.add("Double declaration of variable '"+$ID.text+"' on line "+$ID.line);
		}
	} )
	;

arg
	: ^(ARG ID {
			numParameters++;
			if (st.isDeclared($ID.text)) {
				if (!st.isInitialized($ID.text)) {
					errors.add("The variable "+$ID.text+" might not have been initialized on line "+$ID.line);
				}
			} else {
				errors.add("No such variable: "+$ID.text);
			}
			if (currentRule != null && currentRule.getNumberOfParameters() < numParameters) {
				errors.add("Rule "+currentRule.getName().toString()+" does not have this many parameters on line "+$ID.line); 
			} else {
				if (currentRule != null && !currentRule.isInputParameter(numParameters)) {
					errors.add("Parameter number "+(numParameters)+" cannot be an input parameter in rule "+currentRule.getName().toString()+" on line "+$ID.line);
				}
				if (currentRule != null && !currentRule.getAttributeParameterType(numParameters).equals(st.getType($ID.text))) {
					errors.add("Type mismatch between parameter "+numParameters+" of "+currentRule.getName().toString()+" and variable "+$ID.text+" ("+currentRule.getAttributeParameterType(numParameters)+" is not "+st.getType($ID.text)+")");
				}
			}
		} )
	| ^(ARG OUT ID {
			numParameters++;
			
			if (st.isDeclared($ID.text)) {
				if (!st.canInitialize($ID.text) || syntaxInit.contains($ID.text)) {
					errors.add("Variable already initialized: "+$ID.text+" on line "+$ID.line);
				} else {
					st.initializeSymbol($ID.text);
					syntaxInit.add($ID.text);
				}
			} else {
				errors.add("No such variable: "+$ID.text+" on line "+$ID.line);
			}
			if (currentRule != null && currentRule.getNumberOfParameters() < numParameters) {
				errors.add("Rule "+currentRule.getName().toString()+" does not have this many parameters on line "+$ID.line); 
			} else {
				if (currentRule != null && !currentRule.isOutputParameter(numParameters)) {
					errors.add("Parameter number "+(numParameters)+" cannot be an output parameter in rule "+currentRule.getName().toString()+" on line "+$ID.line);
				}
				if (currentOutputParameters.contains($ID.text)) {
					errors.add("You can not use the same parameter as output more than once per call: "+$ID.text+" on line "+$ID.line);			
				}
				if (currentRule != null && !currentRule.getAttributeParameterType(numParameters).equals(st.getType($ID.text))) {
					errors.add("Type mismatch between parameter "+numParameters+" of "+currentRule.getName().toString()+" and variable "+$ID.text+" ("+currentRule.getAttributeParameterType(numParameters)+" is not "+st.getType($ID.text)+")");
				}
				if (currentRule != null && currentRule.isRequiredInput(numParameters)) {
					errors.add("Parameter "+numParameters+" of rule "+currentRule.getName().toString()+" must be an input parameter.");
				}
			} 
			currentOutputParameters.add($ID.text);
		} )
	| ^(ARG DONT_CARE { 
		numParameters++;
		if (currentRule != null && currentRule.isRequiredInput(numParameters)) {
			errors.add("Parameter "+numParameters+" of rule "+currentRule.getName().toString()+" must be an input parameter.");
		} 
	})
	| ^(ARG BOOL_TYPE bool=(TRUE|FALSE) {
		numParameters++;
		if (currentRule != null && !currentRule.getAttributeParameterType(numParameters).equals("bool")) {
			errors.add("Type mismatch between parameter "+numParameters+" of "+currentRule.getName().toString()+" and '"+bool.getText()+"' on line "+bool.getLine()+" ("+currentRule.getAttributeParameterType(numParameters)+" is not bool)");
		}
	})
	| ^(ARG STRING_TYPE str=ID {
		numParameters++;
		if (currentRule != null && !currentRule.getAttributeParameterType(numParameters).equals("string")) {
			errors.add("Type mismatch between parameter "+numParameters+" of "+currentRule.getName().toString()+" and "+str.getText()+" on line "+str.getLine()+" ("+currentRule.getAttributeParameterType(numParameters)+" is not string)");
		}
	})
	| ^(ARG INT_TYPE in=ID {
		numParameters++;
		if (currentRule != null && !currentRule.getAttributeParameterType(numParameters).equals("int")) {
			errors.add("Type mismatch between parameter "+numParameters+" of "+currentRule.getName().toString()+" and '"+in.getText()+"' on line "+in.getLine()+" ("+currentRule.getAttributeParameterType(numParameters)+" is not int)");
		}
	})
	| ^(ARG REAL_TYPE r=ID {
		numParameters++;
		if (r.getText().equals(".")) {
			errors.add("'.' is not a valid real value on line "+r.getLine());
		}
		if (currentRule != null && !currentRule.getAttributeParameterType(numParameters).equals("real")) {
			errors.add("Type mismatch between parameter "+numParameters+" of "+currentRule.getName().toString()+" and '"+r.getText()+"' on line "+r.getLine()+" ("+currentRule.getAttributeParameterType(numParameters)+" is not real)");
		}
	})
	;