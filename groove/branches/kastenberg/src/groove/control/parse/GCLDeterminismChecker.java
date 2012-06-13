// $ANTLR 3.1b1 GCLDeterminismChecker.g 2010-05-04 16:27:34

package groove.control.parse;
import groove.control.*;
import groove.trans.Rule;
import java.util.LinkedList;
import java.util.HashMap;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


@SuppressWarnings("all")              
public class GCLDeterminismChecker extends TreeParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "PROGRAM", "BLOCK", "FUNCTIONS", "FUNCTION", "CALL", "DO", "VAR", "PARAM", "IDENTIFIER", "OR", "ALAP", "WHILE", "UNTIL", "CHOICE", "CH_OR", "IF", "ELSE", "TRY", "TRUE", "PLUS", "STAR", "SHARP", "ANY", "OTHER", "DOT", "NODE_TYPE", "BOOL_TYPE", "STRING_TYPE", "INT_TYPE", "REAL_TYPE", "COMMA", "OUT", "DONT_CARE", "FALSE", "QUOTE", "BSLASH", "MINUS", "NUMBER", "AND", "NOT", "ML_COMMENT", "SL_COMMENT", "WS", "'{'", "'}'", "'('", "')'", "';'"
    };
    public static final int FUNCTION=7;
    public static final int STAR=24;
    public static final int FUNCTIONS=6;
    public static final int WHILE=15;
    public static final int BOOL_TYPE=30;
    public static final int NODE_TYPE=29;
    public static final int DO=9;
    public static final int PARAM=11;
    public static final int NOT=43;
    public static final int ALAP=14;
    public static final int AND=42;
    public static final int EOF=-1;
    public static final int IF=19;
    public static final int ML_COMMENT=44;
    public static final int QUOTE=38;
    public static final int T__51=51;
    public static final int COMMA=34;
    public static final int IDENTIFIER=12;
    public static final int CH_OR=18;
    public static final int PLUS=23;
    public static final int VAR=10;
    public static final int DOT=28;
    public static final int T__50=50;
    public static final int CHOICE=17;
    public static final int T__47=47;
    public static final int SHARP=25;
    public static final int OTHER=27;
    public static final int T__48=48;
    public static final int T__49=49;
    public static final int ELSE=20;
    public static final int NUMBER=41;
    public static final int MINUS=40;
    public static final int INT_TYPE=32;
    public static final int TRUE=22;
    public static final int TRY=21;
    public static final int REAL_TYPE=33;
    public static final int DONT_CARE=36;
    public static final int WS=46;
    public static final int ANY=26;
    public static final int OUT=35;
    public static final int UNTIL=16;
    public static final int STRING_TYPE=31;
    public static final int BLOCK=5;
    public static final int OR=13;
    public static final int SL_COMMENT=45;
    public static final int PROGRAM=4;
    public static final int CALL=8;
    public static final int FALSE=37;
    public static final int BSLASH=39;

    // delegates
    // delegators


        public GCLDeterminismChecker(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public GCLDeterminismChecker(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return GCLDeterminismChecker.tokenNames; }
    public String getGrammarFileName() { return "GCLDeterminismChecker.g"; }

    
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
        
        /**
         * marks whether a given CommonTree can instantly terminate
         */
        private HashMap<CommonTree,Boolean> it = new HashMap<CommonTree,Boolean>();
        
        /**
         * The initial action for a given CommonTree
         */
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


    public static class program_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start program
    // GCLDeterminismChecker.g:88:1: program : ^( PROGRAM functions block ) ;
    public final GCLDeterminismChecker.program_return program() throws RecognitionException {
        GCLDeterminismChecker.program_return retval = new GCLDeterminismChecker.program_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree PROGRAM1=null;
        GCLDeterminismChecker.functions_return functions2 = null;

        GCLDeterminismChecker.block_return block3 = null;


        CommonTree PROGRAM1_tree=null;

        try {
            // GCLDeterminismChecker.g:89:3: ( ^( PROGRAM functions block ) )
            // GCLDeterminismChecker.g:89:6: ^( PROGRAM functions block )
            {
            root_0 = (CommonTree)adaptor.nil();

            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
            PROGRAM1=(CommonTree)match(input,PROGRAM,FOLLOW_PROGRAM_in_program57); 
            PROGRAM1_tree = (CommonTree)adaptor.dupNode(PROGRAM1);

            root_1 = (CommonTree)adaptor.becomeRoot(PROGRAM1_tree, root_1);



            match(input, Token.DOWN, null); 
            _last = (CommonTree)input.LT(1);
            pushFollow(FOLLOW_functions_in_program59);
            functions2=functions();

            state._fsp--;

            adaptor.addChild(root_1, functions2.getTree());
            _last = (CommonTree)input.LT(1);
            pushFollow(FOLLOW_block_in_program61);
            block3=block();

            state._fsp--;

            adaptor.addChild(root_1, block3.getTree());

            match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
            }


            }

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end program

    public static class functions_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start functions
    // GCLDeterminismChecker.g:92:1: functions : ^( FUNCTIONS ( function )* ) ;
    public final GCLDeterminismChecker.functions_return functions() throws RecognitionException {
        GCLDeterminismChecker.functions_return retval = new GCLDeterminismChecker.functions_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree FUNCTIONS4=null;
        GCLDeterminismChecker.function_return function5 = null;


        CommonTree FUNCTIONS4_tree=null;

        try {
            // GCLDeterminismChecker.g:93:3: ( ^( FUNCTIONS ( function )* ) )
            // GCLDeterminismChecker.g:93:5: ^( FUNCTIONS ( function )* )
            {
            root_0 = (CommonTree)adaptor.nil();

            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
            FUNCTIONS4=(CommonTree)match(input,FUNCTIONS,FOLLOW_FUNCTIONS_in_functions77); 
            FUNCTIONS4_tree = (CommonTree)adaptor.dupNode(FUNCTIONS4);

            root_1 = (CommonTree)adaptor.becomeRoot(FUNCTIONS4_tree, root_1);



            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // GCLDeterminismChecker.g:93:17: ( function )*
                loop1:
                do {
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( (LA1_0==FUNCTION) ) {
                        alt1=1;
                    }


                    switch (alt1) {
                	case 1 :
                	    // GCLDeterminismChecker.g:93:17: function
                	    {
                	    _last = (CommonTree)input.LT(1);
                	    pushFollow(FOLLOW_function_in_functions79);
                	    function5=function();

                	    state._fsp--;

                	    adaptor.addChild(root_1, function5.getTree());

                	    }
                	    break;

                	default :
                	    break loop1;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }adaptor.addChild(root_0, root_1);_last = _save_last_1;
            }


            }

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end functions

    public static class function_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start function
    // GCLDeterminismChecker.g:95:1: function : ^( FUNCTION IDENTIFIER block ) -> ^( FUNCTION IDENTIFIER ) ;
    public final GCLDeterminismChecker.function_return function() throws RecognitionException {
        GCLDeterminismChecker.function_return retval = new GCLDeterminismChecker.function_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree FUNCTION6=null;
        CommonTree IDENTIFIER7=null;
        GCLDeterminismChecker.block_return block8 = null;


        CommonTree FUNCTION6_tree=null;
        CommonTree IDENTIFIER7_tree=null;
        RewriteRuleNodeStream stream_FUNCTION=new RewriteRuleNodeStream(adaptor,"token FUNCTION");
        RewriteRuleNodeStream stream_IDENTIFIER=new RewriteRuleNodeStream(adaptor,"token IDENTIFIER");
        RewriteRuleSubtreeStream stream_block=new RewriteRuleSubtreeStream(adaptor,"rule block");
        try {
            // GCLDeterminismChecker.g:96:3: ( ^( FUNCTION IDENTIFIER block ) -> ^( FUNCTION IDENTIFIER ) )
            // GCLDeterminismChecker.g:97:3: ^( FUNCTION IDENTIFIER block )
            {
            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
            FUNCTION6=(CommonTree)match(input,FUNCTION,FOLLOW_FUNCTION_in_function95);  
            stream_FUNCTION.add(FUNCTION6);



            match(input, Token.DOWN, null); 
            _last = (CommonTree)input.LT(1);
            IDENTIFIER7=(CommonTree)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_function97);  
            stream_IDENTIFIER.add(IDENTIFIER7);

            pushFollow(FOLLOW_block_in_function99);
            block8=block();

            state._fsp--;

            stream_block.add(block8.getTree());

            match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
            }



            // AST REWRITE
            // elements: IDENTIFIER, FUNCTION
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 97:32: -> ^( FUNCTION IDENTIFIER )
            {
                // GCLDeterminismChecker.g:97:35: ^( FUNCTION IDENTIFIER )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(stream_FUNCTION.nextNode(), root_1);

                adaptor.addChild(root_1, stream_IDENTIFIER.nextNode());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end function

    public static class block_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start block
    // GCLDeterminismChecker.g:99:1: block : ^(b= BLOCK (s= statement )* ) ;
    public final GCLDeterminismChecker.block_return block() throws RecognitionException {
        GCLDeterminismChecker.block_return retval = new GCLDeterminismChecker.block_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree b=null;
        GCLDeterminismChecker.statement_return s = null;


        CommonTree b_tree=null;

        try {
            // GCLDeterminismChecker.g:100:3: ( ^(b= BLOCK (s= statement )* ) )
            // GCLDeterminismChecker.g:100:5: ^(b= BLOCK (s= statement )* )
            {
            root_0 = (CommonTree)adaptor.nil();

            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
            b=(CommonTree)match(input,BLOCK,FOLLOW_BLOCK_in_block123); 
            b_tree = (CommonTree)adaptor.dupNode(b);

            root_1 = (CommonTree)adaptor.becomeRoot(b_tree, root_1);


             boolean firstStatement = true; ArrayList<CommonTree> statements = new ArrayList<CommonTree>(); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // GCLDeterminismChecker.g:100:114: (s= statement )*
                loop2:
                do {
                    int alt2=2;
                    int LA2_0 = input.LA(1);

                    if ( ((LA2_0>=CALL && LA2_0<=VAR)||(LA2_0>=OR && LA2_0<=CHOICE)||LA2_0==IF||LA2_0==TRY||(LA2_0>=PLUS && LA2_0<=OTHER)) ) {
                        alt2=1;
                    }


                    switch (alt2) {
                	case 1 :
                	    // GCLDeterminismChecker.g:100:115: s= statement
                	    {
                	    _last = (CommonTree)input.LT(1);
                	    pushFollow(FOLLOW_statement_in_block130);
                	    s=statement();

                	    state._fsp--;

                	    adaptor.addChild(root_1, s.getTree());
                	     
                	      		it.put(b_tree, it.get((s!=null?((CommonTree)s.tree):null)));
                	      		if (firstStatement) {
                	      			addInit(b_tree, getInit((s!=null?((CommonTree)s.tree):null)));
                	      			firstStatement = false; 
                	      		}
                	      		
                	        	if (it.get((s!=null?((CommonTree)s.tree):null))) {
                	      			statements.add((s!=null?((CommonTree)s.tree):null));
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
                	      			if (ct != (s!=null?((CommonTree)s.tree):null)) {
                	      				addInit(ct, getInit((s!=null?((CommonTree)s.tree):null)));
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
                	       		if (!it.get((s!=null?((CommonTree)s.tree):null))) {
                	    	  		statements.clear(); 
                	      		}
                	      		
                	      		//System.err.println("IT("+(s!=null?((CommonTree)s.tree):null)+"): "+it.get((s!=null?((CommonTree)s.tree):null)));
                	      		//System.err.println("init("+(s!=null?((CommonTree)s.tree):null)+"): "+getInit((s!=null?((CommonTree)s.tree):null)));
                	      		ArrayList<ArrayList<CommonTree>> nondet = checkInitDuplicates((s!=null?((CommonTree)s.tree):null));
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
                	      	

                	    }
                	    break;

                	default :
                	    break loop2;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }adaptor.addChild(root_0, root_1);_last = _save_last_1;
            }


            }

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end block

    public static class statement_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start statement
    // GCLDeterminismChecker.g:183:1: statement : ( ^(alapnode= ALAP alapblock= block ) | ^(whilenode= WHILE whilecondition= condition whileblock= block ) | ^(untilnode= UNTIL untilcondition= condition untilblock= block ) | ^(donode= DO doblock= block docondition= condition ) | ^(trynode= TRY tryblock1= block (tryblock2= block )? ) | ^(ifnode= IF ifcondition= condition ifblock= block (elseblock= block )? ) | ^(choicenode= CHOICE (choiceblock= block )+ ) | expression | var_declaration );
    public final GCLDeterminismChecker.statement_return statement() throws RecognitionException {
        GCLDeterminismChecker.statement_return retval = new GCLDeterminismChecker.statement_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree alapnode=null;
        CommonTree whilenode=null;
        CommonTree untilnode=null;
        CommonTree donode=null;
        CommonTree trynode=null;
        CommonTree ifnode=null;
        CommonTree choicenode=null;
        GCLDeterminismChecker.block_return alapblock = null;

        GCLDeterminismChecker.condition_return whilecondition = null;

        GCLDeterminismChecker.block_return whileblock = null;

        GCLDeterminismChecker.condition_return untilcondition = null;

        GCLDeterminismChecker.block_return untilblock = null;

        GCLDeterminismChecker.block_return doblock = null;

        GCLDeterminismChecker.condition_return docondition = null;

        GCLDeterminismChecker.block_return tryblock1 = null;

        GCLDeterminismChecker.block_return tryblock2 = null;

        GCLDeterminismChecker.condition_return ifcondition = null;

        GCLDeterminismChecker.block_return ifblock = null;

        GCLDeterminismChecker.block_return elseblock = null;

        GCLDeterminismChecker.block_return choiceblock = null;

        GCLDeterminismChecker.expression_return expression9 = null;

        GCLDeterminismChecker.var_declaration_return var_declaration10 = null;


        CommonTree alapnode_tree=null;
        CommonTree whilenode_tree=null;
        CommonTree untilnode_tree=null;
        CommonTree donode_tree=null;
        CommonTree trynode_tree=null;
        CommonTree ifnode_tree=null;
        CommonTree choicenode_tree=null;

        try {
            // GCLDeterminismChecker.g:184:3: ( ^(alapnode= ALAP alapblock= block ) | ^(whilenode= WHILE whilecondition= condition whileblock= block ) | ^(untilnode= UNTIL untilcondition= condition untilblock= block ) | ^(donode= DO doblock= block docondition= condition ) | ^(trynode= TRY tryblock1= block (tryblock2= block )? ) | ^(ifnode= IF ifcondition= condition ifblock= block (elseblock= block )? ) | ^(choicenode= CHOICE (choiceblock= block )+ ) | expression | var_declaration )
            int alt6=9;
            switch ( input.LA(1) ) {
            case ALAP:
                {
                alt6=1;
                }
                break;
            case WHILE:
                {
                alt6=2;
                }
                break;
            case UNTIL:
                {
                alt6=3;
                }
                break;
            case DO:
                {
                alt6=4;
                }
                break;
            case TRY:
                {
                alt6=5;
                }
                break;
            case IF:
                {
                alt6=6;
                }
                break;
            case CHOICE:
                {
                alt6=7;
                }
                break;
            case CALL:
            case OR:
            case PLUS:
            case STAR:
            case SHARP:
            case ANY:
            case OTHER:
                {
                alt6=8;
                }
                break;
            case VAR:
                {
                alt6=9;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }

            switch (alt6) {
                case 1 :
                    // GCLDeterminismChecker.g:184:5: ^(alapnode= ALAP alapblock= block )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    alapnode=(CommonTree)match(input,ALAP,FOLLOW_ALAP_in_statement151); 
                    alapnode_tree = (CommonTree)adaptor.dupNode(alapnode);

                    root_1 = (CommonTree)adaptor.becomeRoot(alapnode_tree, root_1);



                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_block_in_statement155);
                    alapblock=block();

                    state._fsp--;

                    adaptor.addChild(root_1, alapblock.getTree());
                     
                      		it.put(alapnode_tree, true);
                      		addInit(alapnode_tree, getInit((alapblock!=null?((CommonTree)alapblock.tree):null)));
                      	

                    match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 2 :
                    // GCLDeterminismChecker.g:188:5: ^(whilenode= WHILE whilecondition= condition whileblock= block )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    whilenode=(CommonTree)match(input,WHILE,FOLLOW_WHILE_in_statement167); 
                    whilenode_tree = (CommonTree)adaptor.dupNode(whilenode);

                    root_1 = (CommonTree)adaptor.becomeRoot(whilenode_tree, root_1);



                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_condition_in_statement171);
                    whilecondition=condition();

                    state._fsp--;

                    adaptor.addChild(root_1, whilecondition.getTree());
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_block_in_statement175);
                    whileblock=block();

                    state._fsp--;

                    adaptor.addChild(root_1, whileblock.getTree());
                     
                      		it.put(whilenode_tree, it.get((whilecondition!=null?((CommonTree)whilecondition.tree):null)));
                    		addInit(whilenode_tree, getInit((whilecondition!=null?((CommonTree)whilecondition.tree):null))); 
                      		if (it.get((whilecondition!=null?((CommonTree)whilecondition.tree):null))) {
                      			addInit(whilenode_tree, getInit((whileblock!=null?((CommonTree)whileblock.tree):null))); 
                      		}
                      	

                    match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 3 :
                    // GCLDeterminismChecker.g:195:5: ^(untilnode= UNTIL untilcondition= condition untilblock= block )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    untilnode=(CommonTree)match(input,UNTIL,FOLLOW_UNTIL_in_statement187); 
                    untilnode_tree = (CommonTree)adaptor.dupNode(untilnode);

                    root_1 = (CommonTree)adaptor.becomeRoot(untilnode_tree, root_1);



                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_condition_in_statement191);
                    untilcondition=condition();

                    state._fsp--;

                    adaptor.addChild(root_1, untilcondition.getTree());
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_block_in_statement195);
                    untilblock=block();

                    state._fsp--;

                    adaptor.addChild(root_1, untilblock.getTree());
                     
                      		it.put(untilnode_tree, it.get((untilcondition!=null?((CommonTree)untilcondition.tree):null)));
                      		addInit(untilnode_tree, getInit((untilcondition!=null?((CommonTree)untilcondition.tree):null)));
                      		if (it.get((untilcondition!=null?((CommonTree)untilcondition.tree):null))) {
                      			addInit(untilnode_tree, getInit((untilblock!=null?((CommonTree)untilblock.tree):null))); 
                      		}
                      	

                    match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 4 :
                    // GCLDeterminismChecker.g:202:5: ^(donode= DO doblock= block docondition= condition )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    donode=(CommonTree)match(input,DO,FOLLOW_DO_in_statement207); 
                    donode_tree = (CommonTree)adaptor.dupNode(donode);

                    root_1 = (CommonTree)adaptor.becomeRoot(donode_tree, root_1);



                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_block_in_statement211);
                    doblock=block();

                    state._fsp--;

                    adaptor.addChild(root_1, doblock.getTree());
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_condition_in_statement215);
                    docondition=condition();

                    state._fsp--;

                    adaptor.addChild(root_1, docondition.getTree());
                     
                      		it.put(donode_tree, it.get((doblock!=null?((CommonTree)doblock.tree):null)) && it.get((docondition!=null?((CommonTree)docondition.tree):null)));
                      		addInit(donode_tree, getInit((doblock!=null?((CommonTree)doblock.tree):null)));
                      		if (it.get((doblock!=null?((CommonTree)doblock.tree):null))) {
                      			addInit(donode_tree, getInit((docondition!=null?((CommonTree)docondition.tree):null)));
                      		}
                      	

                    match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 5 :
                    // GCLDeterminismChecker.g:209:5: ^(trynode= TRY tryblock1= block (tryblock2= block )? )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    trynode=(CommonTree)match(input,TRY,FOLLOW_TRY_in_statement227); 
                    trynode_tree = (CommonTree)adaptor.dupNode(trynode);

                    root_1 = (CommonTree)adaptor.becomeRoot(trynode_tree, root_1);



                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_block_in_statement231);
                    tryblock1=block();

                    state._fsp--;

                    adaptor.addChild(root_1, tryblock1.getTree());
                    // GCLDeterminismChecker.g:209:35: (tryblock2= block )?
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0==BLOCK) ) {
                        alt3=1;
                    }
                    switch (alt3) {
                        case 1 :
                            // GCLDeterminismChecker.g:209:36: tryblock2= block
                            {
                            _last = (CommonTree)input.LT(1);
                            pushFollow(FOLLOW_block_in_statement236);
                            tryblock2=block();

                            state._fsp--;

                            adaptor.addChild(root_1, tryblock2.getTree());

                            }
                            break;

                    }

                     
                      		addInit(trynode_tree, getInit((tryblock1!=null?((CommonTree)tryblock1.tree):null)));
                      		if (tryblock2 != null) {
                      			addInit(trynode_tree, getInit((tryblock2!=null?((CommonTree)tryblock2.tree):null)));
                      			it.put(trynode_tree, it.get((tryblock2!=null?((CommonTree)tryblock2.tree):null)));
                      		} else {
                      			it.put(trynode_tree, true);
                      		}
                      	

                    match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 6 :
                    // GCLDeterminismChecker.g:218:5: ^(ifnode= IF ifcondition= condition ifblock= block (elseblock= block )? )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    ifnode=(CommonTree)match(input,IF,FOLLOW_IF_in_statement250); 
                    ifnode_tree = (CommonTree)adaptor.dupNode(ifnode);

                    root_1 = (CommonTree)adaptor.becomeRoot(ifnode_tree, root_1);



                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_condition_in_statement254);
                    ifcondition=condition();

                    state._fsp--;

                    adaptor.addChild(root_1, ifcondition.getTree());
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_block_in_statement258);
                    ifblock=block();

                    state._fsp--;

                    adaptor.addChild(root_1, ifblock.getTree());
                    // GCLDeterminismChecker.g:218:53: (elseblock= block )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==BLOCK) ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // GCLDeterminismChecker.g:218:54: elseblock= block
                            {
                            _last = (CommonTree)input.LT(1);
                            pushFollow(FOLLOW_block_in_statement263);
                            elseblock=block();

                            state._fsp--;

                            adaptor.addChild(root_1, elseblock.getTree());

                            }
                            break;

                    }

                    
                    		addInit(ifnode_tree, getInit((ifcondition!=null?((CommonTree)ifcondition.tree):null)));
                      		if (elseblock == null) {
                      			it.put(ifnode_tree, it.get((ifcondition!=null?((CommonTree)ifcondition.tree):null)));
                      			if (it.get((ifcondition!=null?((CommonTree)ifcondition.tree):null))) {
                      				addInit(ifnode_tree, getInit((ifblock!=null?((CommonTree)ifblock.tree):null)));
                      			}
                      		} else {
                      			it.put(ifnode_tree, it.get((ifcondition!=null?((CommonTree)ifcondition.tree):null)) && (it.get((ifblock!=null?((CommonTree)ifblock.tree):null)) || it.get((elseblock!=null?((CommonTree)elseblock.tree):null))));
                      			if (it.get((ifcondition!=null?((CommonTree)ifcondition.tree):null))) {
                      				addInit(ifnode_tree, getInit((elseblock!=null?((CommonTree)elseblock.tree):null)));
                      			} else {
                      				addInit(ifnode_tree, getInit((ifblock!=null?((CommonTree)ifblock.tree):null)));
                      			}
                      		}
                      	

                    match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 7 :
                    // GCLDeterminismChecker.g:234:5: ^(choicenode= CHOICE (choiceblock= block )+ )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    choicenode=(CommonTree)match(input,CHOICE,FOLLOW_CHOICE_in_statement277); 
                    choicenode_tree = (CommonTree)adaptor.dupNode(choicenode);

                    root_1 = (CommonTree)adaptor.becomeRoot(choicenode_tree, root_1);


                     boolean choiceIt = false; 

                    match(input, Token.DOWN, null); 
                    // GCLDeterminismChecker.g:234:55: (choiceblock= block )+
                    int cnt5=0;
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( (LA5_0==BLOCK) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // GCLDeterminismChecker.g:234:56: choiceblock= block
                    	    {
                    	    _last = (CommonTree)input.LT(1);
                    	    pushFollow(FOLLOW_block_in_statement284);
                    	    choiceblock=block();

                    	    state._fsp--;

                    	    adaptor.addChild(root_1, choiceblock.getTree());
                    	     
                    	      		choiceIt = choiceIt || (it.get((choiceblock!=null?((CommonTree)choiceblock.tree):null)) == null ? false : it.get((choiceblock!=null?((CommonTree)choiceblock.tree):null))); 
                    	      		addInit(choicenode_tree, getInit((choiceblock!=null?((CommonTree)choiceblock.tree):null))); 
                    	      	

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt5 >= 1 ) break loop5;
                                EarlyExitException eee =
                                    new EarlyExitException(5, input);
                                throw eee;
                        }
                        cnt5++;
                    } while (true);

                    
                      		it.put(choicenode_tree, choiceIt);
                      	

                    match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 8 :
                    // GCLDeterminismChecker.g:240:5: expression
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_expression_in_statement297);
                    expression9=expression();

                    state._fsp--;

                    adaptor.addChild(root_0, expression9.getTree());

                    }
                    break;
                case 9 :
                    // GCLDeterminismChecker.g:241:5: var_declaration
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_var_declaration_in_statement303);
                    var_declaration10=var_declaration();

                    state._fsp--;

                    adaptor.addChild(root_0, var_declaration10.getTree());

                    }
                    break;

            }
            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end statement

    public static class expression_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start expression
    // GCLDeterminismChecker.g:244:1: expression : ( ^(ornode= OR orexpr1= expression orexpr2= expression ) | ^(plusnode= PLUS plusexpr= expression expression ) | ^(starnode= STAR starexpr= expression ) | ^(sharpnode= SHARP sharpexpr= expression ) | rule | anynode= ANY | othernode= OTHER );
    public final GCLDeterminismChecker.expression_return expression() throws RecognitionException {
        GCLDeterminismChecker.expression_return retval = new GCLDeterminismChecker.expression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree ornode=null;
        CommonTree plusnode=null;
        CommonTree starnode=null;
        CommonTree sharpnode=null;
        CommonTree anynode=null;
        CommonTree othernode=null;
        GCLDeterminismChecker.expression_return orexpr1 = null;

        GCLDeterminismChecker.expression_return orexpr2 = null;

        GCLDeterminismChecker.expression_return plusexpr = null;

        GCLDeterminismChecker.expression_return starexpr = null;

        GCLDeterminismChecker.expression_return sharpexpr = null;

        GCLDeterminismChecker.expression_return expression11 = null;

        GCLDeterminismChecker.rule_return rule12 = null;


        CommonTree ornode_tree=null;
        CommonTree plusnode_tree=null;
        CommonTree starnode_tree=null;
        CommonTree sharpnode_tree=null;
        CommonTree anynode_tree=null;
        CommonTree othernode_tree=null;

        try {
            // GCLDeterminismChecker.g:245:2: ( ^(ornode= OR orexpr1= expression orexpr2= expression ) | ^(plusnode= PLUS plusexpr= expression expression ) | ^(starnode= STAR starexpr= expression ) | ^(sharpnode= SHARP sharpexpr= expression ) | rule | anynode= ANY | othernode= OTHER )
            int alt7=7;
            switch ( input.LA(1) ) {
            case OR:
                {
                alt7=1;
                }
                break;
            case PLUS:
                {
                alt7=2;
                }
                break;
            case STAR:
                {
                alt7=3;
                }
                break;
            case SHARP:
                {
                alt7=4;
                }
                break;
            case CALL:
                {
                alt7=5;
                }
                break;
            case ANY:
                {
                alt7=6;
                }
                break;
            case OTHER:
                {
                alt7=7;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;
            }

            switch (alt7) {
                case 1 :
                    // GCLDeterminismChecker.g:245:4: ^(ornode= OR orexpr1= expression orexpr2= expression )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    ornode=(CommonTree)match(input,OR,FOLLOW_OR_in_expression319); 
                    ornode_tree = (CommonTree)adaptor.dupNode(ornode);

                    root_1 = (CommonTree)adaptor.becomeRoot(ornode_tree, root_1);



                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_expression_in_expression323);
                    orexpr1=expression();

                    state._fsp--;

                    adaptor.addChild(root_1, orexpr1.getTree());
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_expression_in_expression327);
                    orexpr2=expression();

                    state._fsp--;

                    adaptor.addChild(root_1, orexpr2.getTree());
                     
                    		it.put(ornode_tree, it.get((orexpr1!=null?((CommonTree)orexpr1.tree):null)) || it.get((orexpr2!=null?((CommonTree)orexpr2.tree):null))); 
                    		addInit(ornode_tree, getInit((orexpr1!=null?((CommonTree)orexpr1.tree):null)));
                    		addInit(ornode_tree, getInit((orexpr2!=null?((CommonTree)orexpr2.tree):null)));
                    	

                    match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 2 :
                    // GCLDeterminismChecker.g:250:4: ^(plusnode= PLUS plusexpr= expression expression )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    plusnode=(CommonTree)match(input,PLUS,FOLLOW_PLUS_in_expression338); 
                    plusnode_tree = (CommonTree)adaptor.dupNode(plusnode);

                    root_1 = (CommonTree)adaptor.becomeRoot(plusnode_tree, root_1);



                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_expression_in_expression342);
                    plusexpr=expression();

                    state._fsp--;

                    adaptor.addChild(root_1, plusexpr.getTree());
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_expression_in_expression344);
                    expression11=expression();

                    state._fsp--;

                    adaptor.addChild(root_1, expression11.getTree());
                    
                    		// XXX: done this to make sure "a+; a;" was not allowed
                    		//it.put(plusnode_tree, it.get((plusexpr!=null?((CommonTree)plusexpr.tree):null)));
                    		it.put(plusnode_tree, true);
                    		addInit(plusnode_tree, getInit((plusexpr!=null?((CommonTree)plusexpr.tree):null)));
                    	

                    match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 3 :
                    // GCLDeterminismChecker.g:256:4: ^(starnode= STAR starexpr= expression )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    starnode=(CommonTree)match(input,STAR,FOLLOW_STAR_in_expression355); 
                    starnode_tree = (CommonTree)adaptor.dupNode(starnode);

                    root_1 = (CommonTree)adaptor.becomeRoot(starnode_tree, root_1);



                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_expression_in_expression359);
                    starexpr=expression();

                    state._fsp--;

                    adaptor.addChild(root_1, starexpr.getTree());
                     
                    		it.put(starnode_tree, true);
                    		addInit(starnode_tree, getInit((starexpr!=null?((CommonTree)starexpr.tree):null)));
                    	

                    match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 4 :
                    // GCLDeterminismChecker.g:260:4: ^(sharpnode= SHARP sharpexpr= expression )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    sharpnode=(CommonTree)match(input,SHARP,FOLLOW_SHARP_in_expression370); 
                    sharpnode_tree = (CommonTree)adaptor.dupNode(sharpnode);

                    root_1 = (CommonTree)adaptor.becomeRoot(sharpnode_tree, root_1);



                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_expression_in_expression374);
                    sharpexpr=expression();

                    state._fsp--;

                    adaptor.addChild(root_1, sharpexpr.getTree());
                     
                    		it.put(sharpnode_tree, true);
                    		addInit(sharpnode_tree, getInit((sharpexpr!=null?((CommonTree)sharpexpr.tree):null))); 
                    	

                    match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 5 :
                    // GCLDeterminismChecker.g:264:4: rule
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_rule_in_expression382);
                    rule12=rule();

                    state._fsp--;

                    adaptor.addChild(root_0, rule12.getTree());

                    }
                    break;
                case 6 :
                    // GCLDeterminismChecker.g:265:4: anynode= ANY
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    anynode=(CommonTree)match(input,ANY,FOLLOW_ANY_in_expression389); 
                    anynode_tree = (CommonTree)adaptor.dupNode(anynode);

                    adaptor.addChild(root_0, anynode_tree);

                     
                    		it.put(anynode_tree, false);
                    		addInit(anynode_tree, anynode_tree);
                    	

                    }
                    break;
                case 7 :
                    // GCLDeterminismChecker.g:269:4: othernode= OTHER
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    othernode=(CommonTree)match(input,OTHER,FOLLOW_OTHER_in_expression398); 
                    othernode_tree = (CommonTree)adaptor.dupNode(othernode);

                    adaptor.addChild(root_0, othernode_tree);

                     
                    		it.put(othernode_tree, false); 
                    	

                    }
                    break;

            }
            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end expression

    public static class condition_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start condition
    // GCLDeterminismChecker.g:274:1: condition : ( ^(ornode= OR orcondition1= condition orcondition2= condition ) | rule | truenode= TRUE );
    public final GCLDeterminismChecker.condition_return condition() throws RecognitionException {
        GCLDeterminismChecker.condition_return retval = new GCLDeterminismChecker.condition_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree ornode=null;
        CommonTree truenode=null;
        GCLDeterminismChecker.condition_return orcondition1 = null;

        GCLDeterminismChecker.condition_return orcondition2 = null;

        GCLDeterminismChecker.rule_return rule13 = null;


        CommonTree ornode_tree=null;
        CommonTree truenode_tree=null;

        try {
            // GCLDeterminismChecker.g:275:3: ( ^(ornode= OR orcondition1= condition orcondition2= condition ) | rule | truenode= TRUE )
            int alt8=3;
            switch ( input.LA(1) ) {
            case OR:
                {
                alt8=1;
                }
                break;
            case CALL:
                {
                alt8=2;
                }
                break;
            case TRUE:
                {
                alt8=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }

            switch (alt8) {
                case 1 :
                    // GCLDeterminismChecker.g:275:5: ^(ornode= OR orcondition1= condition orcondition2= condition )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    ornode=(CommonTree)match(input,OR,FOLLOW_OR_in_condition416); 
                    ornode_tree = (CommonTree)adaptor.dupNode(ornode);

                    root_1 = (CommonTree)adaptor.becomeRoot(ornode_tree, root_1);



                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_condition_in_condition420);
                    orcondition1=condition();

                    state._fsp--;

                    adaptor.addChild(root_1, orcondition1.getTree());
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_condition_in_condition424);
                    orcondition2=condition();

                    state._fsp--;

                    adaptor.addChild(root_1, orcondition2.getTree());
                    
                      		it.put(ornode_tree, it.get((orcondition1!=null?((CommonTree)orcondition1.tree):null)) || it.get((orcondition2!=null?((CommonTree)orcondition2.tree):null)));
                      		addInit(ornode_tree, getInit((orcondition1!=null?((CommonTree)orcondition1.tree):null)));
                      		addInit(ornode_tree, getInit((orcondition2!=null?((CommonTree)orcondition2.tree):null)));
                      	

                    match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 2 :
                    // GCLDeterminismChecker.g:280:5: rule
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_rule_in_condition433);
                    rule13=rule();

                    state._fsp--;

                    adaptor.addChild(root_0, rule13.getTree());

                    }
                    break;
                case 3 :
                    // GCLDeterminismChecker.g:281:5: truenode= TRUE
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    truenode=(CommonTree)match(input,TRUE,FOLLOW_TRUE_in_condition441); 
                    truenode_tree = (CommonTree)adaptor.dupNode(truenode);

                    adaptor.addChild(root_0, truenode_tree);

                     
                      		it.put(truenode_tree, true); 
                      	

                    }
                    break;

            }
            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end condition

    public static class rule_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start rule
    // GCLDeterminismChecker.g:286:1: rule : ^(callnode= CALL callidentifier= IDENTIFIER ( param )* ) ;
    public final GCLDeterminismChecker.rule_return rule() throws RecognitionException {
        GCLDeterminismChecker.rule_return retval = new GCLDeterminismChecker.rule_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree callnode=null;
        CommonTree callidentifier=null;
        GCLDeterminismChecker.param_return param14 = null;


        CommonTree callnode_tree=null;
        CommonTree callidentifier_tree=null;

        try {
            // GCLDeterminismChecker.g:287:3: ( ^(callnode= CALL callidentifier= IDENTIFIER ( param )* ) )
            // GCLDeterminismChecker.g:287:5: ^(callnode= CALL callidentifier= IDENTIFIER ( param )* )
            {
            root_0 = (CommonTree)adaptor.nil();

            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
            callnode=(CommonTree)match(input,CALL,FOLLOW_CALL_in_rule459); 
            callnode_tree = (CommonTree)adaptor.dupNode(callnode);

            root_1 = (CommonTree)adaptor.becomeRoot(callnode_tree, root_1);



            match(input, Token.DOWN, null); 
            _last = (CommonTree)input.LT(1);
            callidentifier=(CommonTree)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_rule463); 
            callidentifier_tree = (CommonTree)adaptor.dupNode(callidentifier);

            adaptor.addChild(root_1, callidentifier_tree);

            // GCLDeterminismChecker.g:287:47: ( param )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==PARAM) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // GCLDeterminismChecker.g:287:47: param
            	    {
            	    _last = (CommonTree)input.LT(1);
            	    pushFollow(FOLLOW_param_in_rule465);
            	    param14=param();

            	    state._fsp--;

            	    adaptor.addChild(root_1, param14.getTree());

            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);

             
            		it.put(callnode_tree, false);
            		addInit(callnode_tree, callidentifier);  
            	

            match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
            }


            }

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end rule

    public static class var_declaration_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start var_declaration
    // GCLDeterminismChecker.g:293:1: var_declaration : ^(varnode= VAR var_type IDENTIFIER ) ;
    public final GCLDeterminismChecker.var_declaration_return var_declaration() throws RecognitionException {
        GCLDeterminismChecker.var_declaration_return retval = new GCLDeterminismChecker.var_declaration_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree varnode=null;
        CommonTree IDENTIFIER16=null;
        GCLDeterminismChecker.var_type_return var_type15 = null;


        CommonTree varnode_tree=null;
        CommonTree IDENTIFIER16_tree=null;

        try {
            // GCLDeterminismChecker.g:294:2: ( ^(varnode= VAR var_type IDENTIFIER ) )
            // GCLDeterminismChecker.g:294:4: ^(varnode= VAR var_type IDENTIFIER )
            {
            root_0 = (CommonTree)adaptor.nil();

            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
            varnode=(CommonTree)match(input,VAR,FOLLOW_VAR_in_var_declaration484); 
            varnode_tree = (CommonTree)adaptor.dupNode(varnode);

            root_1 = (CommonTree)adaptor.becomeRoot(varnode_tree, root_1);



            match(input, Token.DOWN, null); 
            _last = (CommonTree)input.LT(1);
            pushFollow(FOLLOW_var_type_in_var_declaration486);
            var_type15=var_type();

            state._fsp--;

            adaptor.addChild(root_1, var_type15.getTree());
            _last = (CommonTree)input.LT(1);
            IDENTIFIER16=(CommonTree)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_var_declaration488); 
            IDENTIFIER16_tree = (CommonTree)adaptor.dupNode(IDENTIFIER16);

            adaptor.addChild(root_1, IDENTIFIER16_tree);

             
            		it.put(varnode_tree, true); 
            	

            match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
            }


            }

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end var_declaration

    public static class var_type_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start var_type
    // GCLDeterminismChecker.g:299:1: var_type : ( NODE_TYPE | BOOL_TYPE | STRING_TYPE | INT_TYPE | REAL_TYPE );
    public final GCLDeterminismChecker.var_type_return var_type() throws RecognitionException {
        GCLDeterminismChecker.var_type_return retval = new GCLDeterminismChecker.var_type_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree set17=null;

        CommonTree set17_tree=null;

        try {
            // GCLDeterminismChecker.g:300:2: ( NODE_TYPE | BOOL_TYPE | STRING_TYPE | INT_TYPE | REAL_TYPE )
            // GCLDeterminismChecker.g:
            {
            root_0 = (CommonTree)adaptor.nil();

            _last = (CommonTree)input.LT(1);
            set17=(CommonTree)input.LT(1);
            if ( (input.LA(1)>=NODE_TYPE && input.LA(1)<=REAL_TYPE) ) {
                input.consume();

                set17_tree = (CommonTree)adaptor.dupNode(set17);

                adaptor.addChild(root_0, set17_tree);

                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }

             

            }

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end var_type

    public static class param_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start param
    // GCLDeterminismChecker.g:307:1: param : ( ^( PARAM IDENTIFIER ) | ^( PARAM OUT IDENTIFIER ) | ^( PARAM DONT_CARE ) | ^( PARAM BOOL_TYPE TRUE ) | ^( PARAM BOOL_TYPE FALSE ) | ^( PARAM STRING_TYPE IDENTIFIER ) | ^( PARAM INT_TYPE IDENTIFIER ) | ^( PARAM REAL_TYPE IDENTIFIER ) );
    public final GCLDeterminismChecker.param_return param() throws RecognitionException {
        GCLDeterminismChecker.param_return retval = new GCLDeterminismChecker.param_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree PARAM18=null;
        CommonTree IDENTIFIER19=null;
        CommonTree PARAM20=null;
        CommonTree OUT21=null;
        CommonTree IDENTIFIER22=null;
        CommonTree PARAM23=null;
        CommonTree DONT_CARE24=null;
        CommonTree PARAM25=null;
        CommonTree BOOL_TYPE26=null;
        CommonTree TRUE27=null;
        CommonTree PARAM28=null;
        CommonTree BOOL_TYPE29=null;
        CommonTree FALSE30=null;
        CommonTree PARAM31=null;
        CommonTree STRING_TYPE32=null;
        CommonTree IDENTIFIER33=null;
        CommonTree PARAM34=null;
        CommonTree INT_TYPE35=null;
        CommonTree IDENTIFIER36=null;
        CommonTree PARAM37=null;
        CommonTree REAL_TYPE38=null;
        CommonTree IDENTIFIER39=null;

        CommonTree PARAM18_tree=null;
        CommonTree IDENTIFIER19_tree=null;
        CommonTree PARAM20_tree=null;
        CommonTree OUT21_tree=null;
        CommonTree IDENTIFIER22_tree=null;
        CommonTree PARAM23_tree=null;
        CommonTree DONT_CARE24_tree=null;
        CommonTree PARAM25_tree=null;
        CommonTree BOOL_TYPE26_tree=null;
        CommonTree TRUE27_tree=null;
        CommonTree PARAM28_tree=null;
        CommonTree BOOL_TYPE29_tree=null;
        CommonTree FALSE30_tree=null;
        CommonTree PARAM31_tree=null;
        CommonTree STRING_TYPE32_tree=null;
        CommonTree IDENTIFIER33_tree=null;
        CommonTree PARAM34_tree=null;
        CommonTree INT_TYPE35_tree=null;
        CommonTree IDENTIFIER36_tree=null;
        CommonTree PARAM37_tree=null;
        CommonTree REAL_TYPE38_tree=null;
        CommonTree IDENTIFIER39_tree=null;

        try {
            // GCLDeterminismChecker.g:308:2: ( ^( PARAM IDENTIFIER ) | ^( PARAM OUT IDENTIFIER ) | ^( PARAM DONT_CARE ) | ^( PARAM BOOL_TYPE TRUE ) | ^( PARAM BOOL_TYPE FALSE ) | ^( PARAM STRING_TYPE IDENTIFIER ) | ^( PARAM INT_TYPE IDENTIFIER ) | ^( PARAM REAL_TYPE IDENTIFIER ) )
            int alt10=8;
            alt10 = dfa10.predict(input);
            switch (alt10) {
                case 1 :
                    // GCLDeterminismChecker.g:308:4: ^( PARAM IDENTIFIER )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    PARAM18=(CommonTree)match(input,PARAM,FOLLOW_PARAM_in_param534); 
                    PARAM18_tree = (CommonTree)adaptor.dupNode(PARAM18);

                    root_1 = (CommonTree)adaptor.becomeRoot(PARAM18_tree, root_1);



                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    IDENTIFIER19=(CommonTree)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_param536); 
                    IDENTIFIER19_tree = (CommonTree)adaptor.dupNode(IDENTIFIER19);

                    adaptor.addChild(root_1, IDENTIFIER19_tree);


                    match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 2 :
                    // GCLDeterminismChecker.g:309:4: ^( PARAM OUT IDENTIFIER )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    PARAM20=(CommonTree)match(input,PARAM,FOLLOW_PARAM_in_param543); 
                    PARAM20_tree = (CommonTree)adaptor.dupNode(PARAM20);

                    root_1 = (CommonTree)adaptor.becomeRoot(PARAM20_tree, root_1);



                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    OUT21=(CommonTree)match(input,OUT,FOLLOW_OUT_in_param545); 
                    OUT21_tree = (CommonTree)adaptor.dupNode(OUT21);

                    adaptor.addChild(root_1, OUT21_tree);

                    _last = (CommonTree)input.LT(1);
                    IDENTIFIER22=(CommonTree)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_param547); 
                    IDENTIFIER22_tree = (CommonTree)adaptor.dupNode(IDENTIFIER22);

                    adaptor.addChild(root_1, IDENTIFIER22_tree);


                    match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 3 :
                    // GCLDeterminismChecker.g:310:4: ^( PARAM DONT_CARE )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    PARAM23=(CommonTree)match(input,PARAM,FOLLOW_PARAM_in_param554); 
                    PARAM23_tree = (CommonTree)adaptor.dupNode(PARAM23);

                    root_1 = (CommonTree)adaptor.becomeRoot(PARAM23_tree, root_1);



                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    DONT_CARE24=(CommonTree)match(input,DONT_CARE,FOLLOW_DONT_CARE_in_param556); 
                    DONT_CARE24_tree = (CommonTree)adaptor.dupNode(DONT_CARE24);

                    adaptor.addChild(root_1, DONT_CARE24_tree);


                    match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 4 :
                    // GCLDeterminismChecker.g:311:4: ^( PARAM BOOL_TYPE TRUE )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    PARAM25=(CommonTree)match(input,PARAM,FOLLOW_PARAM_in_param563); 
                    PARAM25_tree = (CommonTree)adaptor.dupNode(PARAM25);

                    root_1 = (CommonTree)adaptor.becomeRoot(PARAM25_tree, root_1);



                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    BOOL_TYPE26=(CommonTree)match(input,BOOL_TYPE,FOLLOW_BOOL_TYPE_in_param565); 
                    BOOL_TYPE26_tree = (CommonTree)adaptor.dupNode(BOOL_TYPE26);

                    adaptor.addChild(root_1, BOOL_TYPE26_tree);

                    _last = (CommonTree)input.LT(1);
                    TRUE27=(CommonTree)match(input,TRUE,FOLLOW_TRUE_in_param567); 
                    TRUE27_tree = (CommonTree)adaptor.dupNode(TRUE27);

                    adaptor.addChild(root_1, TRUE27_tree);


                    match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 5 :
                    // GCLDeterminismChecker.g:312:4: ^( PARAM BOOL_TYPE FALSE )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    PARAM28=(CommonTree)match(input,PARAM,FOLLOW_PARAM_in_param574); 
                    PARAM28_tree = (CommonTree)adaptor.dupNode(PARAM28);

                    root_1 = (CommonTree)adaptor.becomeRoot(PARAM28_tree, root_1);



                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    BOOL_TYPE29=(CommonTree)match(input,BOOL_TYPE,FOLLOW_BOOL_TYPE_in_param576); 
                    BOOL_TYPE29_tree = (CommonTree)adaptor.dupNode(BOOL_TYPE29);

                    adaptor.addChild(root_1, BOOL_TYPE29_tree);

                    _last = (CommonTree)input.LT(1);
                    FALSE30=(CommonTree)match(input,FALSE,FOLLOW_FALSE_in_param578); 
                    FALSE30_tree = (CommonTree)adaptor.dupNode(FALSE30);

                    adaptor.addChild(root_1, FALSE30_tree);


                    match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 6 :
                    // GCLDeterminismChecker.g:313:4: ^( PARAM STRING_TYPE IDENTIFIER )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    PARAM31=(CommonTree)match(input,PARAM,FOLLOW_PARAM_in_param585); 
                    PARAM31_tree = (CommonTree)adaptor.dupNode(PARAM31);

                    root_1 = (CommonTree)adaptor.becomeRoot(PARAM31_tree, root_1);



                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    STRING_TYPE32=(CommonTree)match(input,STRING_TYPE,FOLLOW_STRING_TYPE_in_param587); 
                    STRING_TYPE32_tree = (CommonTree)adaptor.dupNode(STRING_TYPE32);

                    adaptor.addChild(root_1, STRING_TYPE32_tree);

                    _last = (CommonTree)input.LT(1);
                    IDENTIFIER33=(CommonTree)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_param589); 
                    IDENTIFIER33_tree = (CommonTree)adaptor.dupNode(IDENTIFIER33);

                    adaptor.addChild(root_1, IDENTIFIER33_tree);


                    match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 7 :
                    // GCLDeterminismChecker.g:314:4: ^( PARAM INT_TYPE IDENTIFIER )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    PARAM34=(CommonTree)match(input,PARAM,FOLLOW_PARAM_in_param596); 
                    PARAM34_tree = (CommonTree)adaptor.dupNode(PARAM34);

                    root_1 = (CommonTree)adaptor.becomeRoot(PARAM34_tree, root_1);



                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    INT_TYPE35=(CommonTree)match(input,INT_TYPE,FOLLOW_INT_TYPE_in_param598); 
                    INT_TYPE35_tree = (CommonTree)adaptor.dupNode(INT_TYPE35);

                    adaptor.addChild(root_1, INT_TYPE35_tree);

                    _last = (CommonTree)input.LT(1);
                    IDENTIFIER36=(CommonTree)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_param600); 
                    IDENTIFIER36_tree = (CommonTree)adaptor.dupNode(IDENTIFIER36);

                    adaptor.addChild(root_1, IDENTIFIER36_tree);


                    match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 8 :
                    // GCLDeterminismChecker.g:315:4: ^( PARAM REAL_TYPE IDENTIFIER )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    PARAM37=(CommonTree)match(input,PARAM,FOLLOW_PARAM_in_param607); 
                    PARAM37_tree = (CommonTree)adaptor.dupNode(PARAM37);

                    root_1 = (CommonTree)adaptor.becomeRoot(PARAM37_tree, root_1);



                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    REAL_TYPE38=(CommonTree)match(input,REAL_TYPE,FOLLOW_REAL_TYPE_in_param609); 
                    REAL_TYPE38_tree = (CommonTree)adaptor.dupNode(REAL_TYPE38);

                    adaptor.addChild(root_1, REAL_TYPE38_tree);

                    _last = (CommonTree)input.LT(1);
                    IDENTIFIER39=(CommonTree)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_param611); 
                    IDENTIFIER39_tree = (CommonTree)adaptor.dupNode(IDENTIFIER39);

                    adaptor.addChild(root_1, IDENTIFIER39_tree);


                    match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;

            }
            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end param

    // Delegated rules


    protected DFA10 dfa10 = new DFA10(this);
    static final String DFA10_eotS =
        "\14\uffff";
    static final String DFA10_eofS =
        "\14\uffff";
    static final String DFA10_minS =
        "\1\13\1\2\1\14\3\uffff\1\26\5\uffff";
    static final String DFA10_maxS =
        "\1\13\1\2\1\44\3\uffff\1\45\5\uffff";
    static final String DFA10_acceptS =
        "\3\uffff\1\1\1\2\1\3\1\uffff\1\6\1\7\1\10\1\4\1\5";
    static final String DFA10_specialS =
        "\14\uffff}>";
    static final String[] DFA10_transitionS = {
            "\1\1",
            "\1\2",
            "\1\3\21\uffff\1\6\1\7\1\10\1\11\1\uffff\1\4\1\5",
            "",
            "",
            "",
            "\1\12\16\uffff\1\13",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA10_eot = DFA.unpackEncodedString(DFA10_eotS);
    static final short[] DFA10_eof = DFA.unpackEncodedString(DFA10_eofS);
    static final char[] DFA10_min = DFA.unpackEncodedStringToUnsignedChars(DFA10_minS);
    static final char[] DFA10_max = DFA.unpackEncodedStringToUnsignedChars(DFA10_maxS);
    static final short[] DFA10_accept = DFA.unpackEncodedString(DFA10_acceptS);
    static final short[] DFA10_special = DFA.unpackEncodedString(DFA10_specialS);
    static final short[][] DFA10_transition;

    static {
        int numStates = DFA10_transitionS.length;
        DFA10_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA10_transition[i] = DFA.unpackEncodedString(DFA10_transitionS[i]);
        }
    }

    class DFA10 extends DFA {

        public DFA10(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 10;
            this.eot = DFA10_eot;
            this.eof = DFA10_eof;
            this.min = DFA10_min;
            this.max = DFA10_max;
            this.accept = DFA10_accept;
            this.special = DFA10_special;
            this.transition = DFA10_transition;
        }
        public String getDescription() {
            return "307:1: param : ( ^( PARAM IDENTIFIER ) | ^( PARAM OUT IDENTIFIER ) | ^( PARAM DONT_CARE ) | ^( PARAM BOOL_TYPE TRUE ) | ^( PARAM BOOL_TYPE FALSE ) | ^( PARAM STRING_TYPE IDENTIFIER ) | ^( PARAM INT_TYPE IDENTIFIER ) | ^( PARAM REAL_TYPE IDENTIFIER ) );";
        }
    }
 

    public static final BitSet FOLLOW_PROGRAM_in_program57 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_functions_in_program59 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_program61 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FUNCTIONS_in_functions77 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_function_in_functions79 = new BitSet(new long[]{0x0000000000000088L});
    public static final BitSet FOLLOW_FUNCTION_in_function95 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENTIFIER_in_function97 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_function99 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BLOCK_in_block123 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_statement_in_block130 = new BitSet(new long[]{0x000000000FABE708L});
    public static final BitSet FOLLOW_ALAP_in_statement151 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_statement155 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WHILE_in_statement167 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_statement171 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_statement175 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_UNTIL_in_statement187 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_statement191 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_statement195 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DO_in_statement207 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_statement211 = new BitSet(new long[]{0x0000000000402100L});
    public static final BitSet FOLLOW_condition_in_statement215 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TRY_in_statement227 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_statement231 = new BitSet(new long[]{0x0000000000000028L});
    public static final BitSet FOLLOW_block_in_statement236 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IF_in_statement250 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_statement254 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_statement258 = new BitSet(new long[]{0x0000000000000028L});
    public static final BitSet FOLLOW_block_in_statement263 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CHOICE_in_statement277 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_statement284 = new BitSet(new long[]{0x0000000000000028L});
    public static final BitSet FOLLOW_expression_in_statement297 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_declaration_in_statement303 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_expression319 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression323 = new BitSet(new long[]{0x000000000F802100L});
    public static final BitSet FOLLOW_expression_in_expression327 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PLUS_in_expression338 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression342 = new BitSet(new long[]{0x000000000F802100L});
    public static final BitSet FOLLOW_expression_in_expression344 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STAR_in_expression355 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression359 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SHARP_in_expression370 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression374 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_rule_in_expression382 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_expression389 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_expression398 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_condition416 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_condition420 = new BitSet(new long[]{0x0000000000402100L});
    public static final BitSet FOLLOW_condition_in_condition424 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_rule_in_condition433 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_condition441 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CALL_in_rule459 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENTIFIER_in_rule463 = new BitSet(new long[]{0x0000000000000808L});
    public static final BitSet FOLLOW_param_in_rule465 = new BitSet(new long[]{0x0000000000000808L});
    public static final BitSet FOLLOW_VAR_in_var_declaration484 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_var_type_in_var_declaration486 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_var_declaration488 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_set_in_var_type0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PARAM_in_param534 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENTIFIER_in_param536 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PARAM_in_param543 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_OUT_in_param545 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_param547 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PARAM_in_param554 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DONT_CARE_in_param556 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PARAM_in_param563 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_BOOL_TYPE_in_param565 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_TRUE_in_param567 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PARAM_in_param574 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_BOOL_TYPE_in_param576 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_FALSE_in_param578 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PARAM_in_param585 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_TYPE_in_param587 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_param589 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PARAM_in_param596 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_INT_TYPE_in_param598 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_param600 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PARAM_in_param607 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_REAL_TYPE_in_param609 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_param611 = new BitSet(new long[]{0x0000000000000008L});

}