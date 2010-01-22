tree grammar GCLDeterminismChecker;

options {
	tokenVocab=GCL;
	output=AST;
	rewrite=false;
	ASTLabelType=CommonTree;
}

@header {
package groove.control.parse;
import groove.control.*;
import groove.trans.Rule;
import java.util.LinkedList;
import java.util.HashMap;
}

@members{
    private Namespace namespace;
	public void setNamespace(Namespace namespace) {
		this.namespace = namespace;
	}

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
    
    private void debug(String msg) {
    	if (namespace.usesVariables()) {
    		System.err.println("Variables debug (GCLChecker): "+msg);
    	}
    }
    
    private HashMap<CommonTree,Boolean> it = new HashMap<CommonTree,Boolean>();
    private HashMap<CommonTree,ArrayList<CommonTree>> init = new HashMap<CommonTree,ArrayList<CommonTree>>();
    
    private ArrayList<CommonTree> getInit(CommonTree o) {
    	if (!init.containsKey(o)) {
    		init.put(o, new ArrayList<CommonTree>());
    	}
    	return init.get(o);
    }
    
    private void addInit(CommonTree o, ArrayList<CommonTree> otherInit) {
    	if (!init.containsKey(o)) init.put(o, new ArrayList<CommonTree>());
    	init.get(o).addAll(otherInit);
    }
    
    private void addInit(CommonTree o, CommonTree s) {
    	if (!init.containsKey(o)) init.put(o, new ArrayList<CommonTree>());
    	init.get(o).add(s);
    }
    
    private ArrayList<ArrayList<CommonTree>> checkInitDuplicates(CommonTree o) {
    	ArrayList<CommonTree> initialActions = getInit(o);
    	ArrayList<ArrayList<CommonTree>> ret = new ArrayList<ArrayList<CommonTree>>();
    	for (int i=0; i<initialActions.size(); i++) {
    		ArrayList<CommonTree> tmp = new ArrayList<CommonTree>();
    		for (int j=0; j<initialActions.size(); j++) {
    			if (i != j && initialActions.get(i).toString().equals(initialActions.get(j).toString()) || initialActions.get(j).toString().equals("any")) {
   					tmp.add(initialActions.get(j));
    			}
    		}
   			if (tmp.size() > 0) {
   				tmp.add(0, initialActions.get(i));
   				ret.add(tmp);
   			}
   			
    	}
    	return ret;
    }
}

program 
  :  ^(PROGRAM functions block) 
  ;

functions
  : ^(FUNCTIONS function*);

function
  : 
  ^(FUNCTION IDENTIFIER block);
  
block
  : ^(b=BLOCK { boolean firstStatement = true; ArrayList<CommonTree> statements = new ArrayList<CommonTree>(); } (s=statement { 
  		it.put($b.tree, it.get($s.tree));
  		if (firstStatement) {
  			addInit($b.tree, getInit($s.tree));
  			firstStatement = false; 
  		}
  		
    	if (it.get($s.tree)) {
  			statements.add($s.tree);
  		}
  		
  		for(CommonTree ct : statements) {
  			/**
  			 * First we add the init of this statement to all the
  			 * statements that are in the list already. These 
  			 * statements should all be able to instantly terminate.
  			 * 
  			 * We then check if any of these statements have non-
  			 * determinism.
  			 */
  			if (ct != $s.tree) {
  				addInit(ct, getInit($s.tree));
  			}
  			
	  		ArrayList<ArrayList<CommonTree>> nondet = checkInitDuplicates(ct);
	  		if (nondet.size() > 0) {
	  			ArrayList<CommonTree> alreadyReported = new ArrayList<CommonTree>();
	  			for (ArrayList<CommonTree> nondeterminism : nondet) {
		  			boolean found = alreadyReported.contains(nondeterminism.get(0));
		  			if (!nondeterminism.get(0).toString().equals("any")) {
			  			String errorstr = "Nondeterminism found for rule '"+nondeterminism.get(0) +"' on line "+nondeterminism.get(0).getLine()+":"+nondeterminism.get(0).getCharPositionInLine();
			  			for (int i=1; i<nondeterminism.size(); i++) {
			  				found = found && alreadyReported.contains(nondeterminism.get(i));
			  				errorstr += ", "+nondeterminism.get(i).getLine()+":"+nondeterminism.get(i).getCharPositionInLine();
			  			}
			  			if (!found) {
			  				errors.add(errorstr);
			  				alreadyReported.addAll(nondeterminism);
			  			}
			  		}
		  		}	
	  		}
  		}
  		
  		/**
  		 * If the current statement can instantly terminate
  		 * we need to add the inits of the next statement to it as well
  		 * (if the next one can terminate instantly too, add the
  		 *  init of the next one too, et cetera)
  		 *
  		 * however, if the current statement CANNOT instantly 
  		 * terminate, we can clear the statements list because
  		 * we must execute a rule at this point
  		 */
   		if (!it.get($s.tree)) {
	  		statements.clear(); 
  		}
  		
  		//System.err.println("IT("+$s.tree+"): "+it.get($s.tree));
  		//System.err.println("init("+$s.tree+"): "+getInit($s.tree));
  		ArrayList<ArrayList<CommonTree>> nondet = checkInitDuplicates($s.tree);
  		if (nondet.size() > 0) {
  			ArrayList<CommonTree> alreadyReported = new ArrayList<CommonTree>();
  			for (ArrayList<CommonTree> nondeterminism : nondet) {
	  			boolean found = alreadyReported.contains(nondeterminism.get(0));
	  			if (!nondeterminism.get(0).toString().equals("any")) {
		  			String errorstr = "Nondeterminism found for rule '"+nondeterminism.get(0) +"' on line "+nondeterminism.get(0).getLine()+":"+nondeterminism.get(0).getCharPositionInLine();
		  			for (int i=1; i<nondeterminism.size(); i++) {
		  				found = found && alreadyReported.contains(nondeterminism.get(i));
		  				errorstr += ", "+nondeterminism.get(i).getLine()+":"+nondeterminism.get(i).getCharPositionInLine();
		  			}
		  			if (!found) {
		  				errors.add(errorstr);
		  				alreadyReported.addAll(nondeterminism);
		  			}
		  			// stop, else we might keep reporting this error
		  			//break;
		  		}
	  		}	
  		}
  	})*)
  ;

statement
  : ^(alapnode=ALAP alapblock=block { 
  		it.put($alapnode.tree, true);
  		addInit($alapnode.tree, getInit($alapblock.tree));
  	})
  | ^(whilenode=WHILE whilecondition=condition whileblock=block { 
  		it.put($whilenode.tree, it.get($whilecondition.tree));
		addInit($whilenode.tree, getInit($whilecondition.tree)); 
  		if (it.get($whilecondition.tree)) {
  			addInit($whilenode.tree, getInit($whileblock.tree)); 
  		}
  	})
  | ^(untilnode=UNTIL untilcondition=condition untilblock=block { 
  		it.put($untilnode.tree, it.get($untilcondition.tree));
  		addInit($untilnode.tree, getInit($untilcondition.tree));
  		if (it.get($untilcondition.tree)) {
  			addInit($untilnode.tree, getInit($untilblock.tree)); 
  		}
  	})
  | ^(donode=DO doblock=block docondition=condition { 
  		it.put($donode.tree, it.get($doblock.tree) && it.get($docondition.tree));
  		addInit($donode.tree, getInit($doblock.tree));
  		if (it.get($doblock.tree)) {
  			addInit($donode.tree, getInit($docondition.tree));
  		}
  	})
  | ^(trynode=TRY tryblock1=block (tryblock2=block)? { 
  		addInit($trynode.tree, getInit($tryblock1.tree));
  		if (tryblock2 != null) {
  			addInit($trynode.tree, getInit($tryblock2.tree));
  			it.put($trynode.tree, it.get($tryblock2.tree));
  		} else {
  			it.put($trynode.tree, true);
  		}
  	})
  | ^(ifnode=IF ifcondition=condition ifblock=block (elseblock=block)? {
		addInit($ifnode.tree, getInit($ifcondition.tree));
  		if (elseblock == null) {
  			it.put($ifnode.tree, it.get($ifcondition.tree));
  			if (it.get($ifcondition.tree)) {
  				addInit($ifnode.tree, getInit($ifblock.tree));
  			}
  		} else {
  			it.put($ifnode.tree, it.get($ifcondition.tree) && (it.get($ifblock.tree) || it.get($elseblock.tree)));
  			if (it.get($ifcondition.tree)) {
  				addInit($ifnode.tree, getInit($elseblock.tree));
  			} else {
  				addInit($ifnode.tree, getInit($ifblock.tree));
  			}
  		}
  	})
  | ^(choicenode=CHOICE { boolean choiceIt = false; } (choiceblock=block { 
  		choiceIt = choiceIt || (it.get($choiceblock.tree) == null ? false : it.get($choiceblock.tree)); 
  		addInit($choicenode.tree, getInit($choiceblock.tree)); 
  	})+ {
  		it.put($choicenode.tree, choiceIt);
  	})
  | expression
  | var_declaration
  ;

expression	
	: ^(ornode=OR orexpr1=expression orexpr2=expression { 
		it.put($ornode.tree, it.get($orexpr1.tree) || it.get($orexpr2.tree)); 
		addInit($ornode.tree, getInit($orexpr1.tree));
		addInit($ornode.tree, getInit($orexpr2.tree));
	})
	| ^(plusnode=PLUS plusexpr=expression { 
		it.put($plusnode.tree, it.get($plusexpr.tree));
		addInit($plusnode.tree, getInit($plusexpr.tree));
	})
	| ^(starnode=STAR starexpr=expression { 
		it.put($starnode.tree, true);
		addInit($starnode.tree, getInit($starexpr.tree));
	})
	| ^(sharpnode=SHARP sharpexpr=expression { 
		it.put($sharpnode.tree, true);
		addInit($sharpnode.tree, getInit($sharpexpr.tree)); 
	})
	| rule
	| anynode=ANY { 
		it.put($anynode.tree, false);
		addInit($anynode.tree, $anynode.tree);
	}
	| othernode=OTHER { 
		it.put($othernode.tree, false); 
	}
	; 

condition
  : ^(ornode=OR orcondition1=condition orcondition2=condition {
  		it.put($ornode.tree, it.get($orcondition1.tree) || it.get($orcondition2.tree));
  		addInit($ornode.tree, getInit($orcondition1.tree));
  		addInit($ornode.tree, getInit($orcondition2.tree));
  	})
  | rule
  | truenode=TRUE { 
  		it.put($truenode.tree, true); 
  	}
  ;

rule
  : ^(callnode=CALL callidentifier=IDENTIFIER param* { 
		it.put($callnode.tree, false);
		addInit($callnode.tree, $callidentifier);  
	})
  ;

var_declaration
	: ^(varnode=VAR var_type IDENTIFIER { 
		it.put($varnode.tree, true); 
	})
	;

var_type
	: NODE_TYPE
	;

param
	: ^(PARAM IDENTIFIER)
	| ^(PARAM OUT IDENTIFIER)
	| ^(PARAM DONT_CARE)
	;