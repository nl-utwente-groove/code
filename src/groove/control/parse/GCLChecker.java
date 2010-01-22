// $ANTLR 3.1b1 GCLChecker.g 2010-01-22 12:45:14

package groove.control.parse;
import groove.control.*;
import groove.trans.Rule;
import java.util.LinkedList;
import java.util.Stack;
import java.util.HashSet;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


@SuppressWarnings("all")              
public class GCLChecker extends TreeParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "PROGRAM", "BLOCK", "FUNCTIONS", "FUNCTION", "CALL", "DO", "VAR", "PARAM", "IDENTIFIER", "OR", "ALAP", "WHILE", "UNTIL", "TRY", "ELSE", "IF", "CHOICE", "CH_OR", "TRUE", "PLUS", "STAR", "SHARP", "ANY", "OTHER", "NODE_TYPE", "COMMA", "OUT", "DONT_CARE", "AND", "DOT", "NOT", "ML_COMMENT", "SL_COMMENT", "WS", "'{'", "'}'", "'('", "')'", "';'"
    };
    public static final int FUNCTION=7;
    public static final int T__42=42;
    public static final int T__40=40;
    public static final int T__41=41;
    public static final int STAR=24;
    public static final int OTHER=27;
    public static final int SHARP=25;
    public static final int WHILE=15;
    public static final int FUNCTIONS=6;
    public static final int NODE_TYPE=28;
    public static final int ELSE=18;
    public static final int DO=9;
    public static final int PARAM=11;
    public static final int NOT=34;
    public static final int ALAP=14;
    public static final int AND=32;
    public static final int EOF=-1;
    public static final int TRUE=22;
    public static final int TRY=17;
    public static final int IF=19;
    public static final int DONT_CARE=31;
    public static final int ML_COMMENT=35;
    public static final int ANY=26;
    public static final int WS=37;
    public static final int OUT=30;
    public static final int T__38=38;
    public static final int COMMA=29;
    public static final int T__39=39;
    public static final int UNTIL=16;
    public static final int IDENTIFIER=12;
    public static final int BLOCK=5;
    public static final int OR=13;
    public static final int SL_COMMENT=36;
    public static final int CH_OR=21;
    public static final int PROGRAM=4;
    public static final int PLUS=23;
    public static final int VAR=10;
    public static final int CALL=8;
    public static final int DOT=33;
    public static final int CHOICE=20;

    // delegates
    // delegators


        public GCLChecker(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public GCLChecker(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return GCLChecker.tokenNames; }
    public String getGrammarFileName() { return "GCLChecker.g"; }

    
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
        		//System.err.println("Variables debug (GCLChecker): "+msg);
        	}
        }


    public static class program_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start program
    // GCLChecker.g:51:1: program : ^( PROGRAM functions block ) ;
    public final GCLChecker.program_return program() throws RecognitionException {
        GCLChecker.program_return retval = new GCLChecker.program_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree PROGRAM1=null;
        GCLChecker.functions_return functions2 = null;

        GCLChecker.block_return block3 = null;


        CommonTree PROGRAM1_tree=null;

        try {
            // GCLChecker.g:52:3: ( ^( PROGRAM functions block ) )
            // GCLChecker.g:52:6: ^( PROGRAM functions block )
            {
            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            _last = (CommonTree)input.LT(1);
            PROGRAM1=(CommonTree)match(input,PROGRAM,FOLLOW_PROGRAM_in_program57); 


            if ( _first_0==null ) _first_0 = PROGRAM1;
            match(input, Token.DOWN, null); 
            _last = (CommonTree)input.LT(1);
            pushFollow(FOLLOW_functions_in_program59);
            functions2=functions();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = functions2.tree;
            _last = (CommonTree)input.LT(1);
            pushFollow(FOLLOW_block_in_program61);
            block3=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block3.tree;

            match(input, Token.UP, null); _last = _save_last_1;
            }


            retval.tree = (CommonTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (CommonTree)adaptor.getParent(retval.tree);
            }

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
    // GCLChecker.g:55:1: functions : ^( FUNCTIONS ( function )* ) ;
    public final GCLChecker.functions_return functions() throws RecognitionException {
        GCLChecker.functions_return retval = new GCLChecker.functions_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree FUNCTIONS4=null;
        GCLChecker.function_return function5 = null;


        CommonTree FUNCTIONS4_tree=null;

        try {
            // GCLChecker.g:56:3: ( ^( FUNCTIONS ( function )* ) )
            // GCLChecker.g:56:5: ^( FUNCTIONS ( function )* )
            {
            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            _last = (CommonTree)input.LT(1);
            FUNCTIONS4=(CommonTree)match(input,FUNCTIONS,FOLLOW_FUNCTIONS_in_functions77); 


            if ( _first_0==null ) _first_0 = FUNCTIONS4;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // GCLChecker.g:56:17: ( function )*
                loop1:
                do {
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( (LA1_0==FUNCTION) ) {
                        alt1=1;
                    }


                    switch (alt1) {
                	case 1 :
                	    // GCLChecker.g:56:17: function
                	    {
                	    _last = (CommonTree)input.LT(1);
                	    pushFollow(FOLLOW_function_in_functions79);
                	    function5=function();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = function5.tree;

                	    retval.tree = (CommonTree)_first_0;
                	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                	        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                	    }
                	    break;

                	default :
                	    break loop1;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }_last = _save_last_1;
            }


            retval.tree = (CommonTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (CommonTree)adaptor.getParent(retval.tree);
            }

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
    // GCLChecker.g:58:1: function : ^( FUNCTION IDENTIFIER block ) -> ^( FUNCTION IDENTIFIER ) ;
    public final GCLChecker.function_return function() throws RecognitionException {
        GCLChecker.function_return retval = new GCLChecker.function_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree FUNCTION6=null;
        CommonTree IDENTIFIER7=null;
        GCLChecker.block_return block8 = null;


        CommonTree FUNCTION6_tree=null;
        CommonTree IDENTIFIER7_tree=null;
        RewriteRuleNodeStream stream_FUNCTION=new RewriteRuleNodeStream(adaptor,"token FUNCTION");
        RewriteRuleNodeStream stream_IDENTIFIER=new RewriteRuleNodeStream(adaptor,"token IDENTIFIER");
        RewriteRuleSubtreeStream stream_block=new RewriteRuleSubtreeStream(adaptor,"rule block");
        try {
            // GCLChecker.g:59:3: ( ^( FUNCTION IDENTIFIER block ) -> ^( FUNCTION IDENTIFIER ) )
            // GCLChecker.g:60:3: ^( FUNCTION IDENTIFIER block )
            {
            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            _last = (CommonTree)input.LT(1);
            FUNCTION6=(CommonTree)match(input,FUNCTION,FOLLOW_FUNCTION_in_function95);  
            stream_FUNCTION.add(FUNCTION6);


            if ( _first_0==null ) _first_0 = FUNCTION6;
            match(input, Token.DOWN, null); 
            _last = (CommonTree)input.LT(1);
            IDENTIFIER7=(CommonTree)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_function97);  
            stream_IDENTIFIER.add(IDENTIFIER7);

            pushFollow(FOLLOW_block_in_function99);
            block8=block();

            state._fsp--;

            stream_block.add(block8.getTree());
            
              		if (namespace.hasRule((IDENTIFIER7!=null?IDENTIFIER7.getText():null))) {
              			errors.add("There already exists a rule with the name: "+(IDENTIFIER7!=null?IDENTIFIER7.getText():null));
              		} else if (namespace.hasProc((IDENTIFIER7!=null?IDENTIFIER7.getText():null))) {
              			errors.add("Multiple definitions of the function: "+(IDENTIFIER7!=null?IDENTIFIER7.getText():null));
              		} else {
              			namespace.store( (IDENTIFIER7!=null?IDENTIFIER7.getText():null) , (block8!=null?((CommonTree)block8.tree):null));
              		} 
              	

            match(input, Token.UP, null); _last = _save_last_1;
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
            // 68:9: -> ^( FUNCTION IDENTIFIER )
            {
                // GCLChecker.g:68:12: ^( FUNCTION IDENTIFIER )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(stream_FUNCTION.nextNode(), root_1);

                adaptor.addChild(root_1, stream_IDENTIFIER.nextNode());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            input.replaceChildren(adaptor.getParent(retval.start),
                                  adaptor.getChildIndex(retval.start),
                                  adaptor.getChildIndex(_last),
                                  retval.tree);
            }

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
    // GCLChecker.g:70:1: block : ^( BLOCK ( statement )* ) ;
    public final GCLChecker.block_return block() throws RecognitionException {
        GCLChecker.block_return retval = new GCLChecker.block_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree BLOCK9=null;
        GCLChecker.statement_return statement10 = null;


        CommonTree BLOCK9_tree=null;

        try {
            // GCLChecker.g:71:3: ( ^( BLOCK ( statement )* ) )
            // GCLChecker.g:71:5: ^( BLOCK ( statement )* )
            {
            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            _last = (CommonTree)input.LT(1);
            BLOCK9=(CommonTree)match(input,BLOCK,FOLLOW_BLOCK_in_block125); 


            if ( _first_0==null ) _first_0 = BLOCK9; st.openScope(); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // GCLChecker.g:71:33: ( statement )*
                loop2:
                do {
                    int alt2=2;
                    int LA2_0 = input.LA(1);

                    if ( ((LA2_0>=CALL && LA2_0<=VAR)||(LA2_0>=OR && LA2_0<=TRY)||(LA2_0>=IF && LA2_0<=CHOICE)||(LA2_0>=PLUS && LA2_0<=OTHER)) ) {
                        alt2=1;
                    }


                    switch (alt2) {
                	case 1 :
                	    // GCLChecker.g:71:34: statement
                	    {
                	    _last = (CommonTree)input.LT(1);
                	    pushFollow(FOLLOW_statement_in_block130);
                	    statement10=statement();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = statement10.tree;

                	    retval.tree = (CommonTree)_first_0;
                	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                	        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                	    }
                	    break;

                	default :
                	    break loop2;
                    }
                } while (true);

                 st.closeScope(); 

                match(input, Token.UP, null); 
            }_last = _save_last_1;
            }


            retval.tree = (CommonTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (CommonTree)adaptor.getParent(retval.tree);
            }

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
    // GCLChecker.g:74:1: statement : ( ^( ALAP block ) | ^( WHILE condition block ) | ^( UNTIL condition block ) | ^( DO block condition ) | ^( TRY block ( block )? ) | ^( IF condition block ( block )? ) | ^( CHOICE ( block )+ ) | expression | var_declaration );
    public final GCLChecker.statement_return statement() throws RecognitionException {
        GCLChecker.statement_return retval = new GCLChecker.statement_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree ALAP11=null;
        CommonTree WHILE13=null;
        CommonTree UNTIL16=null;
        CommonTree DO19=null;
        CommonTree TRY22=null;
        CommonTree IF25=null;
        CommonTree CHOICE29=null;
        GCLChecker.block_return block12 = null;

        GCLChecker.condition_return condition14 = null;

        GCLChecker.block_return block15 = null;

        GCLChecker.condition_return condition17 = null;

        GCLChecker.block_return block18 = null;

        GCLChecker.block_return block20 = null;

        GCLChecker.condition_return condition21 = null;

        GCLChecker.block_return block23 = null;

        GCLChecker.block_return block24 = null;

        GCLChecker.condition_return condition26 = null;

        GCLChecker.block_return block27 = null;

        GCLChecker.block_return block28 = null;

        GCLChecker.block_return block30 = null;

        GCLChecker.expression_return expression31 = null;

        GCLChecker.var_declaration_return var_declaration32 = null;


        CommonTree ALAP11_tree=null;
        CommonTree WHILE13_tree=null;
        CommonTree UNTIL16_tree=null;
        CommonTree DO19_tree=null;
        CommonTree TRY22_tree=null;
        CommonTree IF25_tree=null;
        CommonTree CHOICE29_tree=null;

        try {
            // GCLChecker.g:75:3: ( ^( ALAP block ) | ^( WHILE condition block ) | ^( UNTIL condition block ) | ^( DO block condition ) | ^( TRY block ( block )? ) | ^( IF condition block ( block )? ) | ^( CHOICE ( block )+ ) | expression | var_declaration )
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
                    // GCLChecker.g:75:5: ^( ALAP block )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    ALAP11=(CommonTree)match(input,ALAP,FOLLOW_ALAP_in_statement149); 


                    if ( _first_0==null ) _first_0 = ALAP11;
                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_block_in_statement151);
                    block12=block();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = block12.tree;

                    match(input, Token.UP, null); _last = _save_last_1;
                    }


                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 2 :
                    // GCLChecker.g:76:5: ^( WHILE condition block )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    WHILE13=(CommonTree)match(input,WHILE,FOLLOW_WHILE_in_statement159); 


                    if ( _first_0==null ) _first_0 = WHILE13;
                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_condition_in_statement161);
                    condition14=condition();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = condition14.tree;
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_block_in_statement163);
                    block15=block();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = block15.tree;

                    match(input, Token.UP, null); _last = _save_last_1;
                    }


                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 3 :
                    // GCLChecker.g:77:5: ^( UNTIL condition block )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    UNTIL16=(CommonTree)match(input,UNTIL,FOLLOW_UNTIL_in_statement171); 


                    if ( _first_0==null ) _first_0 = UNTIL16;
                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_condition_in_statement173);
                    condition17=condition();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = condition17.tree;
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_block_in_statement175);
                    block18=block();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = block18.tree;

                    match(input, Token.UP, null); _last = _save_last_1;
                    }


                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 4 :
                    // GCLChecker.g:78:5: ^( DO block condition )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    DO19=(CommonTree)match(input,DO,FOLLOW_DO_in_statement183); 


                    if ( _first_0==null ) _first_0 = DO19;
                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_block_in_statement185);
                    block20=block();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = block20.tree;
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_condition_in_statement187);
                    condition21=condition();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = condition21.tree;

                    match(input, Token.UP, null); _last = _save_last_1;
                    }


                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 5 :
                    // GCLChecker.g:79:5: ^( TRY block ( block )? )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    TRY22=(CommonTree)match(input,TRY,FOLLOW_TRY_in_statement195); 


                    if ( _first_0==null ) _first_0 = TRY22;
                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_block_in_statement197);
                    block23=block();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = block23.tree;
                    // GCLChecker.g:79:17: ( block )?
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0==BLOCK) ) {
                        alt3=1;
                    }
                    switch (alt3) {
                        case 1 :
                            // GCLChecker.g:79:18: block
                            {
                            _last = (CommonTree)input.LT(1);
                            pushFollow(FOLLOW_block_in_statement200);
                            block24=block();

                            state._fsp--;

                             
                            if ( _first_1==null ) _first_1 = block24.tree;

                            retval.tree = (CommonTree)_first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                            }
                            break;

                    }


                    match(input, Token.UP, null); _last = _save_last_1;
                    }


                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 6 :
                    // GCLChecker.g:80:5: ^( IF condition block ( block )? )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    IF25=(CommonTree)match(input,IF,FOLLOW_IF_in_statement210); 


                    if ( _first_0==null ) _first_0 = IF25;
                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_condition_in_statement212);
                    condition26=condition();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = condition26.tree;
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_block_in_statement214);
                    block27=block();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = block27.tree;
                    // GCLChecker.g:80:26: ( block )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==BLOCK) ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // GCLChecker.g:80:27: block
                            {
                            _last = (CommonTree)input.LT(1);
                            pushFollow(FOLLOW_block_in_statement217);
                            block28=block();

                            state._fsp--;

                             
                            if ( _first_1==null ) _first_1 = block28.tree;

                            retval.tree = (CommonTree)_first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                            }
                            break;

                    }


                    match(input, Token.UP, null); _last = _save_last_1;
                    }


                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 7 :
                    // GCLChecker.g:81:5: ^( CHOICE ( block )+ )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    CHOICE29=(CommonTree)match(input,CHOICE,FOLLOW_CHOICE_in_statement227); 


                    if ( _first_0==null ) _first_0 = CHOICE29;
                    match(input, Token.DOWN, null); 
                    // GCLChecker.g:81:14: ( block )+
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
                    	    // GCLChecker.g:81:14: block
                    	    {
                    	    _last = (CommonTree)input.LT(1);
                    	    pushFollow(FOLLOW_block_in_statement229);
                    	    block30=block();

                    	    state._fsp--;

                    	     
                    	    if ( _first_1==null ) _first_1 = block30.tree;

                    	    retval.tree = (CommonTree)_first_0;
                    	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                    	        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
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


                    match(input, Token.UP, null); _last = _save_last_1;
                    }


                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 8 :
                    // GCLChecker.g:82:5: expression
                    {
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_expression_in_statement237);
                    expression31=expression();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = expression31.tree;

                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 9 :
                    // GCLChecker.g:83:5: var_declaration
                    {
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_var_declaration_in_statement243);
                    var_declaration32=var_declaration();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = var_declaration32.tree;

                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                    }
                    break;

            }
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
    // GCLChecker.g:86:1: expression : ( ^( OR expression expression ) | ^( PLUS e1= expression ) -> ^( PLUS $e1 $e1) | ^( STAR expression ) | ^( SHARP expression ) | rule | ANY | OTHER );
    public final GCLChecker.expression_return expression() throws RecognitionException {
        GCLChecker.expression_return retval = new GCLChecker.expression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree OR33=null;
        CommonTree PLUS36=null;
        CommonTree STAR37=null;
        CommonTree SHARP39=null;
        CommonTree ANY42=null;
        CommonTree OTHER43=null;
        GCLChecker.expression_return e1 = null;

        GCLChecker.expression_return expression34 = null;

        GCLChecker.expression_return expression35 = null;

        GCLChecker.expression_return expression38 = null;

        GCLChecker.expression_return expression40 = null;

        GCLChecker.rule_return rule41 = null;


        CommonTree OR33_tree=null;
        CommonTree PLUS36_tree=null;
        CommonTree STAR37_tree=null;
        CommonTree SHARP39_tree=null;
        CommonTree ANY42_tree=null;
        CommonTree OTHER43_tree=null;
        RewriteRuleNodeStream stream_PLUS=new RewriteRuleNodeStream(adaptor,"token PLUS");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        try {
            // GCLChecker.g:87:2: ( ^( OR expression expression ) | ^( PLUS e1= expression ) -> ^( PLUS $e1 $e1) | ^( STAR expression ) | ^( SHARP expression ) | rule | ANY | OTHER )
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
                    // GCLChecker.g:87:4: ^( OR expression expression )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    OR33=(CommonTree)match(input,OR,FOLLOW_OR_in_expression257); 


                    if ( _first_0==null ) _first_0 = OR33;
                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_expression_in_expression259);
                    expression34=expression();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = expression34.tree;
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_expression_in_expression261);
                    expression35=expression();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = expression35.tree;

                    match(input, Token.UP, null); _last = _save_last_1;
                    }


                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 2 :
                    // GCLChecker.g:88:4: ^( PLUS e1= expression )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    PLUS36=(CommonTree)match(input,PLUS,FOLLOW_PLUS_in_expression268);  
                    stream_PLUS.add(PLUS36);


                    if ( _first_0==null ) _first_0 = PLUS36;
                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression272);
                    e1=expression();

                    state._fsp--;

                    stream_expression.add(e1.getTree());

                    match(input, Token.UP, null); _last = _save_last_1;
                    }



                    // AST REWRITE
                    // elements: e1, e1, PLUS
                    // token labels: 
                    // rule labels: retval, e1
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);
                    RewriteRuleSubtreeStream stream_e1=new RewriteRuleSubtreeStream(adaptor,"token e1",e1!=null?e1.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 88:26: -> ^( PLUS $e1 $e1)
                    {
                        // GCLChecker.g:88:29: ^( PLUS $e1 $e1)
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(stream_PLUS.nextNode(), root_1);

                        adaptor.addChild(root_1, stream_e1.nextTree());
                        adaptor.addChild(root_1, stream_e1.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                    input.replaceChildren(adaptor.getParent(retval.start),
                                          adaptor.getChildIndex(retval.start),
                                          adaptor.getChildIndex(_last),
                                          retval.tree);
                    }
                    break;
                case 3 :
                    // GCLChecker.g:89:4: ^( STAR expression )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    STAR37=(CommonTree)match(input,STAR,FOLLOW_STAR_in_expression291); 


                    if ( _first_0==null ) _first_0 = STAR37;
                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_expression_in_expression293);
                    expression38=expression();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = expression38.tree;

                    match(input, Token.UP, null); _last = _save_last_1;
                    }


                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 4 :
                    // GCLChecker.g:90:4: ^( SHARP expression )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    SHARP39=(CommonTree)match(input,SHARP,FOLLOW_SHARP_in_expression300); 


                    if ( _first_0==null ) _first_0 = SHARP39;
                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_expression_in_expression302);
                    expression40=expression();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = expression40.tree;

                    match(input, Token.UP, null); _last = _save_last_1;
                    }


                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 5 :
                    // GCLChecker.g:91:4: rule
                    {
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_rule_in_expression308);
                    rule41=rule();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = rule41.tree;

                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 6 :
                    // GCLChecker.g:92:4: ANY
                    {
                    _last = (CommonTree)input.LT(1);
                    ANY42=(CommonTree)match(input,ANY,FOLLOW_ANY_in_expression313); 
                     
                    if ( _first_0==null ) _first_0 = ANY42;

                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 7 :
                    // GCLChecker.g:93:4: OTHER
                    {
                    _last = (CommonTree)input.LT(1);
                    OTHER43=(CommonTree)match(input,OTHER,FOLLOW_OTHER_in_expression318); 
                     
                    if ( _first_0==null ) _first_0 = OTHER43;

                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                    }
                    break;

            }
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
    // GCLChecker.g:96:1: condition : ( ^( OR condition condition ) | rule | TRUE );
    public final GCLChecker.condition_return condition() throws RecognitionException {
        GCLChecker.condition_return retval = new GCLChecker.condition_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree OR44=null;
        CommonTree TRUE48=null;
        GCLChecker.condition_return condition45 = null;

        GCLChecker.condition_return condition46 = null;

        GCLChecker.rule_return rule47 = null;


        CommonTree OR44_tree=null;
        CommonTree TRUE48_tree=null;

        try {
            // GCLChecker.g:97:3: ( ^( OR condition condition ) | rule | TRUE )
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
                    // GCLChecker.g:97:5: ^( OR condition condition )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    OR44=(CommonTree)match(input,OR,FOLLOW_OR_in_condition332); 


                    if ( _first_0==null ) _first_0 = OR44;
                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_condition_in_condition334);
                    condition45=condition();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = condition45.tree;
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_condition_in_condition336);
                    condition46=condition();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = condition46.tree;

                    match(input, Token.UP, null); _last = _save_last_1;
                    }


                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 2 :
                    // GCLChecker.g:98:5: rule
                    {
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_rule_in_condition343);
                    rule47=rule();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = rule47.tree;

                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 3 :
                    // GCLChecker.g:99:5: TRUE
                    {
                    _last = (CommonTree)input.LT(1);
                    TRUE48=(CommonTree)match(input,TRUE,FOLLOW_TRUE_in_condition349); 
                     
                    if ( _first_0==null ) _first_0 = TRUE48;

                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                    }
                    break;

            }
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
    // GCLChecker.g:102:1: rule : ^( CALL r= IDENTIFIER ( param )* ) ;
    public final GCLChecker.rule_return rule() throws RecognitionException {
        GCLChecker.rule_return retval = new GCLChecker.rule_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree r=null;
        CommonTree CALL49=null;
        GCLChecker.param_return param50 = null;


        CommonTree r_tree=null;
        CommonTree CALL49_tree=null;

        try {
            // GCLChecker.g:103:3: ( ^( CALL r= IDENTIFIER ( param )* ) )
            // GCLChecker.g:103:5: ^( CALL r= IDENTIFIER ( param )* )
            {
            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            _last = (CommonTree)input.LT(1);
            CALL49=(CommonTree)match(input,CALL,FOLLOW_CALL_in_rule363); 


            if ( _first_0==null ) _first_0 = CALL49;
            match(input, Token.DOWN, null); 
            _last = (CommonTree)input.LT(1);
            r=(CommonTree)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_rule367); 
             
            if ( _first_1==null ) _first_1 = r;
             currentRule = r.getText(); 
            // GCLChecker.g:103:56: ( param )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==PARAM) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // GCLChecker.g:103:56: param
            	    {
            	    _last = (CommonTree)input.LT(1);
            	    pushFollow(FOLLOW_param_in_rule371);
            	    param50=param();

            	    state._fsp--;

            	     
            	    if ( _first_1==null ) _first_1 = param50.tree;

            	    retval.tree = (CommonTree)_first_0;
            	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
            	        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);

            
            		debug("currentRule: "+currentRule);
            		if (!namespace.hasRule((r!=null?r.getText():null))) {
            			errors.add("No such rule: "+(r!=null?r.getText():null)+" on line "+(r!=null?r.getLine():0));
            		}
            		if (numParameters != 0 && numParameters != namespace.getRule(currentRule).getVisibleParCount()) {
            			errors.add("The number of parameters used in this call of "+currentRule+" ("+numParameters+") does not match the number of parameters defined in the rule ("+namespace.getRule(currentRule).getVisibleParCount()+") on line "+(r!=null?r.getLine():0));
            		}
            		numParameters = 0;
            		currentOutputParameters.clear();
            	

            match(input, Token.UP, null); _last = _save_last_1;
            }


            retval.tree = (CommonTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (CommonTree)adaptor.getParent(retval.tree);
            }

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
    // GCLChecker.g:116:1: var_declaration : ^( VAR var_type IDENTIFIER ) ;
    public final GCLChecker.var_declaration_return var_declaration() throws RecognitionException {
        GCLChecker.var_declaration_return retval = new GCLChecker.var_declaration_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree VAR51=null;
        CommonTree IDENTIFIER53=null;
        GCLChecker.var_type_return var_type52 = null;


        CommonTree VAR51_tree=null;
        CommonTree IDENTIFIER53_tree=null;

        try {
            // GCLChecker.g:117:2: ( ^( VAR var_type IDENTIFIER ) )
            // GCLChecker.g:117:4: ^( VAR var_type IDENTIFIER )
            {
            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            _last = (CommonTree)input.LT(1);
            VAR51=(CommonTree)match(input,VAR,FOLLOW_VAR_in_var_declaration389); 


            if ( _first_0==null ) _first_0 = VAR51;
            match(input, Token.DOWN, null); 
            _last = (CommonTree)input.LT(1);
            pushFollow(FOLLOW_var_type_in_var_declaration391);
            var_type52=var_type();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = var_type52.tree;
            _last = (CommonTree)input.LT(1);
            IDENTIFIER53=(CommonTree)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_var_declaration393); 
             
            if ( _first_1==null ) _first_1 = IDENTIFIER53;
             namespace.addVariable((IDENTIFIER53!=null?IDENTIFIER53.getText():null)); st.declareSymbol((IDENTIFIER53!=null?IDENTIFIER53.getText():null)); 

            match(input, Token.UP, null); _last = _save_last_1;
            }


            retval.tree = (CommonTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (CommonTree)adaptor.getParent(retval.tree);
            }

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
    // GCLChecker.g:120:1: var_type : NODE_TYPE ;
    public final GCLChecker.var_type_return var_type() throws RecognitionException {
        GCLChecker.var_type_return retval = new GCLChecker.var_type_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree NODE_TYPE54=null;

        CommonTree NODE_TYPE54_tree=null;

        try {
            // GCLChecker.g:121:2: ( NODE_TYPE )
            // GCLChecker.g:121:4: NODE_TYPE
            {
            _last = (CommonTree)input.LT(1);
            NODE_TYPE54=(CommonTree)match(input,NODE_TYPE,FOLLOW_NODE_TYPE_in_var_type408); 
             
            if ( _first_0==null ) _first_0 = NODE_TYPE54;

            retval.tree = (CommonTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (CommonTree)adaptor.getParent(retval.tree);
            }

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
    // GCLChecker.g:124:1: param : ( ^( PARAM IDENTIFIER ) | ^( PARAM OUT IDENTIFIER ) | ^( PARAM DONT_CARE ) );
    public final GCLChecker.param_return param() throws RecognitionException {
        GCLChecker.param_return retval = new GCLChecker.param_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree PARAM55=null;
        CommonTree IDENTIFIER56=null;
        CommonTree PARAM57=null;
        CommonTree OUT58=null;
        CommonTree IDENTIFIER59=null;
        CommonTree PARAM60=null;
        CommonTree DONT_CARE61=null;

        CommonTree PARAM55_tree=null;
        CommonTree IDENTIFIER56_tree=null;
        CommonTree PARAM57_tree=null;
        CommonTree OUT58_tree=null;
        CommonTree IDENTIFIER59_tree=null;
        CommonTree PARAM60_tree=null;
        CommonTree DONT_CARE61_tree=null;

        try {
            // GCLChecker.g:125:2: ( ^( PARAM IDENTIFIER ) | ^( PARAM OUT IDENTIFIER ) | ^( PARAM DONT_CARE ) )
            int alt10=3;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==PARAM) ) {
                int LA10_1 = input.LA(2);

                if ( (LA10_1==DOWN) ) {
                    switch ( input.LA(3) ) {
                    case IDENTIFIER:
                        {
                        alt10=1;
                        }
                        break;
                    case OUT:
                        {
                        alt10=2;
                        }
                        break;
                    case DONT_CARE:
                        {
                        alt10=3;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 10, 2, input);

                        throw nvae;
                    }

                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 10, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // GCLChecker.g:125:4: ^( PARAM IDENTIFIER )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    PARAM55=(CommonTree)match(input,PARAM,FOLLOW_PARAM_in_param420); 


                    if ( _first_0==null ) _first_0 = PARAM55;
                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    IDENTIFIER56=(CommonTree)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_param422); 
                     
                    if ( _first_1==null ) _first_1 = IDENTIFIER56;
                    
                    			numParameters++;
                    			if (st.isDeclared((IDENTIFIER56!=null?IDENTIFIER56.getText():null))) {
                    				if (!st.isInitialized((IDENTIFIER56!=null?IDENTIFIER56.getText():null))) {
                    					errors.add("The variable "+(IDENTIFIER56!=null?IDENTIFIER56.getText():null)+" might not have been initialized on line "+(IDENTIFIER56!=null?IDENTIFIER56.getLine():0));
                    				}
                    			} else {
                    				errors.add("No such variable: "+(IDENTIFIER56!=null?IDENTIFIER56.getText():null));
                    			}
                    			if (!namespace.getRule(currentRule).isInputParameter(numParameters)) {
                    				errors.add("Parameter number "+(numParameters)+" cannot be an input parameter in rule "+currentRule+" on line "+(IDENTIFIER56!=null?IDENTIFIER56.getLine():0));
                    			}
                    		

                    match(input, Token.UP, null); _last = _save_last_1;
                    }


                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 2 :
                    // GCLChecker.g:138:4: ^( PARAM OUT IDENTIFIER )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    PARAM57=(CommonTree)match(input,PARAM,FOLLOW_PARAM_in_param432); 


                    if ( _first_0==null ) _first_0 = PARAM57;
                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    OUT58=(CommonTree)match(input,OUT,FOLLOW_OUT_in_param434); 
                     
                    if ( _first_1==null ) _first_1 = OUT58;
                    _last = (CommonTree)input.LT(1);
                    IDENTIFIER59=(CommonTree)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_param436); 
                     
                    if ( _first_1==null ) _first_1 = IDENTIFIER59;
                    
                    			numParameters++;
                    			
                    			if (st.isDeclared((IDENTIFIER59!=null?IDENTIFIER59.getText():null))) {
                    				if (!st.canInitialize((IDENTIFIER59!=null?IDENTIFIER59.getText():null))) {
                    					errors.add("Variable already initialized: "+(IDENTIFIER59!=null?IDENTIFIER59.getText():null)+" on line "+(IDENTIFIER59!=null?IDENTIFIER59.getLine():0));
                    				} else {
                    					st.initializeSymbol((IDENTIFIER59!=null?IDENTIFIER59.getText():null));
                    				}
                    			} else {
                    				errors.add("No such variable: "+(IDENTIFIER59!=null?IDENTIFIER59.getText():null)+" on line "+(IDENTIFIER59!=null?IDENTIFIER59.getLine():0));
                    			}
                    			if (!namespace.getRule(currentRule).isOutputParameter(numParameters)) {
                    				errors.add("Parameter number "+(numParameters)+" cannot be an output parameter in rule "+currentRule+" on line "+(IDENTIFIER59!=null?IDENTIFIER59.getLine():0));
                    			}
                    			if (currentOutputParameters.contains((IDENTIFIER59!=null?IDENTIFIER59.getText():null))) {
                    				errors.add("You can not use the same parameter as output more than once per call: "+(IDENTIFIER59!=null?IDENTIFIER59.getText():null)+" on line "+(IDENTIFIER59!=null?IDENTIFIER59.getLine():0));			
                    			}
                    			currentOutputParameters.add((IDENTIFIER59!=null?IDENTIFIER59.getText():null));
                    		

                    match(input, Token.UP, null); _last = _save_last_1;
                    }


                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 3 :
                    // GCLChecker.g:158:4: ^( PARAM DONT_CARE )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    PARAM60=(CommonTree)match(input,PARAM,FOLLOW_PARAM_in_param446); 


                    if ( _first_0==null ) _first_0 = PARAM60;
                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    DONT_CARE61=(CommonTree)match(input,DONT_CARE,FOLLOW_DONT_CARE_in_param448); 
                     
                    if ( _first_1==null ) _first_1 = DONT_CARE61;
                     numParameters++; 

                    match(input, Token.UP, null); _last = _save_last_1;
                    }


                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                    }
                    break;

            }
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


 

    public static final BitSet FOLLOW_PROGRAM_in_program57 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_functions_in_program59 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_program61 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FUNCTIONS_in_functions77 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_function_in_functions79 = new BitSet(new long[]{0x0000000000000088L});
    public static final BitSet FOLLOW_FUNCTION_in_function95 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENTIFIER_in_function97 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_function99 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BLOCK_in_block125 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_statement_in_block130 = new BitSet(new long[]{0x000000000F9BE708L});
    public static final BitSet FOLLOW_ALAP_in_statement149 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_statement151 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WHILE_in_statement159 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_statement161 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_statement163 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_UNTIL_in_statement171 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_statement173 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_statement175 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DO_in_statement183 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_statement185 = new BitSet(new long[]{0x0000000000402100L});
    public static final BitSet FOLLOW_condition_in_statement187 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TRY_in_statement195 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_statement197 = new BitSet(new long[]{0x0000000000000028L});
    public static final BitSet FOLLOW_block_in_statement200 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IF_in_statement210 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_statement212 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_statement214 = new BitSet(new long[]{0x0000000000000028L});
    public static final BitSet FOLLOW_block_in_statement217 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CHOICE_in_statement227 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_statement229 = new BitSet(new long[]{0x0000000000000028L});
    public static final BitSet FOLLOW_expression_in_statement237 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_declaration_in_statement243 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_expression257 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression259 = new BitSet(new long[]{0x000000000F802100L});
    public static final BitSet FOLLOW_expression_in_expression261 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PLUS_in_expression268 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression272 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STAR_in_expression291 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression293 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SHARP_in_expression300 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression302 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_rule_in_expression308 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_expression313 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_expression318 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_condition332 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_condition334 = new BitSet(new long[]{0x0000000000402100L});
    public static final BitSet FOLLOW_condition_in_condition336 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_rule_in_condition343 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_condition349 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CALL_in_rule363 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENTIFIER_in_rule367 = new BitSet(new long[]{0x0000000000000808L});
    public static final BitSet FOLLOW_param_in_rule371 = new BitSet(new long[]{0x0000000000000808L});
    public static final BitSet FOLLOW_VAR_in_var_declaration389 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_var_type_in_var_declaration391 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_var_declaration393 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NODE_TYPE_in_var_type408 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PARAM_in_param420 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENTIFIER_in_param422 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PARAM_in_param432 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_OUT_in_param434 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_param436 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PARAM_in_param446 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DONT_CARE_in_param448 = new BitSet(new long[]{0x0000000000000008L});

}