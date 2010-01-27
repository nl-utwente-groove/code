// $ANTLR 3.1b1 GCLChecker.g 2010-01-27 13:20:30

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
    // GCLChecker.g:55:1: functions : ^(f= FUNCTIONS ( function )* ) ;
    public final GCLChecker.functions_return functions() throws RecognitionException {
        GCLChecker.functions_return retval = new GCLChecker.functions_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree f=null;
        GCLChecker.function_return function4 = null;


        CommonTree f_tree=null;

        try {
            // GCLChecker.g:56:3: ( ^(f= FUNCTIONS ( function )* ) )
            // GCLChecker.g:56:5: ^(f= FUNCTIONS ( function )* )
            {
            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            _last = (CommonTree)input.LT(1);
            f=(CommonTree)match(input,FUNCTIONS,FOLLOW_FUNCTIONS_in_functions79); 


            if ( _first_0==null ) _first_0 = f; namespace.storeProcs(f); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // GCLChecker.g:56:48: ( function )*
                loop1:
                do {
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( (LA1_0==FUNCTION) ) {
                        alt1=1;
                    }


                    switch (alt1) {
                	case 1 :
                	    // GCLChecker.g:56:48: function
                	    {
                	    _last = (CommonTree)input.LT(1);
                	    pushFollow(FOLLOW_function_in_functions83);
                	    function4=function();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = function4.tree;

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
    // GCLChecker.g:58:1: function : ^( FUNCTION IDENTIFIER block ) ;
    public final GCLChecker.function_return function() throws RecognitionException {
        GCLChecker.function_return retval = new GCLChecker.function_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree FUNCTION5=null;
        CommonTree IDENTIFIER6=null;
        GCLChecker.block_return block7 = null;


        CommonTree FUNCTION5_tree=null;
        CommonTree IDENTIFIER6_tree=null;

        try {
            // GCLChecker.g:59:3: ( ^( FUNCTION IDENTIFIER block ) )
            // GCLChecker.g:59:5: ^( FUNCTION IDENTIFIER block )
            {
            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            _last = (CommonTree)input.LT(1);
            FUNCTION5=(CommonTree)match(input,FUNCTION,FOLLOW_FUNCTION_in_function96); 


            if ( _first_0==null ) _first_0 = FUNCTION5;
            match(input, Token.DOWN, null); 
            _last = (CommonTree)input.LT(1);
            IDENTIFIER6=(CommonTree)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_function98); 
             
            if ( _first_1==null ) _first_1 = IDENTIFIER6;
            _last = (CommonTree)input.LT(1);
            pushFollow(FOLLOW_block_in_function100);
            block7=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block7.tree;
            
              		debug("proc: "+(IDENTIFIER6!=null?IDENTIFIER6.getText():null));
              		if (namespace.hasRule((IDENTIFIER6!=null?IDENTIFIER6.getText():null))) {
              			errors.add("There already exists a rule with the name: "+(IDENTIFIER6!=null?IDENTIFIER6.getText():null));
              		}
              	

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
    // $ANTLR end function

    public static class block_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start block
    // GCLChecker.g:66:1: block : ^( BLOCK ( statement )* ) ;
    public final GCLChecker.block_return block() throws RecognitionException {
        GCLChecker.block_return retval = new GCLChecker.block_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree BLOCK8=null;
        GCLChecker.statement_return statement9 = null;


        CommonTree BLOCK8_tree=null;

        try {
            // GCLChecker.g:67:3: ( ^( BLOCK ( statement )* ) )
            // GCLChecker.g:67:5: ^( BLOCK ( statement )* )
            {
            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            _last = (CommonTree)input.LT(1);
            BLOCK8=(CommonTree)match(input,BLOCK,FOLLOW_BLOCK_in_block117); 


            if ( _first_0==null ) _first_0 = BLOCK8; st.openScope(); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // GCLChecker.g:67:33: ( statement )*
                loop2:
                do {
                    int alt2=2;
                    int LA2_0 = input.LA(1);

                    if ( ((LA2_0>=CALL && LA2_0<=VAR)||(LA2_0>=OR && LA2_0<=TRY)||(LA2_0>=IF && LA2_0<=CHOICE)||(LA2_0>=PLUS && LA2_0<=OTHER)) ) {
                        alt2=1;
                    }


                    switch (alt2) {
                	case 1 :
                	    // GCLChecker.g:67:34: statement
                	    {
                	    _last = (CommonTree)input.LT(1);
                	    pushFollow(FOLLOW_statement_in_block122);
                	    statement9=statement();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = statement9.tree;

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
    // GCLChecker.g:70:1: statement : ( ^( ALAP block ) | ^( WHILE condition block ) | ^( UNTIL condition block ) | ^( DO block condition ) | ^( TRY block ( block )? ) | ^( IF condition block ( block )? ) | ^( CHOICE ( block )+ ) | expression | var_declaration );
    public final GCLChecker.statement_return statement() throws RecognitionException {
        GCLChecker.statement_return retval = new GCLChecker.statement_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree ALAP10=null;
        CommonTree WHILE12=null;
        CommonTree UNTIL15=null;
        CommonTree DO18=null;
        CommonTree TRY21=null;
        CommonTree IF24=null;
        CommonTree CHOICE28=null;
        GCLChecker.block_return block11 = null;

        GCLChecker.condition_return condition13 = null;

        GCLChecker.block_return block14 = null;

        GCLChecker.condition_return condition16 = null;

        GCLChecker.block_return block17 = null;

        GCLChecker.block_return block19 = null;

        GCLChecker.condition_return condition20 = null;

        GCLChecker.block_return block22 = null;

        GCLChecker.block_return block23 = null;

        GCLChecker.condition_return condition25 = null;

        GCLChecker.block_return block26 = null;

        GCLChecker.block_return block27 = null;

        GCLChecker.block_return block29 = null;

        GCLChecker.expression_return expression30 = null;

        GCLChecker.var_declaration_return var_declaration31 = null;


        CommonTree ALAP10_tree=null;
        CommonTree WHILE12_tree=null;
        CommonTree UNTIL15_tree=null;
        CommonTree DO18_tree=null;
        CommonTree TRY21_tree=null;
        CommonTree IF24_tree=null;
        CommonTree CHOICE28_tree=null;

        try {
            // GCLChecker.g:71:3: ( ^( ALAP block ) | ^( WHILE condition block ) | ^( UNTIL condition block ) | ^( DO block condition ) | ^( TRY block ( block )? ) | ^( IF condition block ( block )? ) | ^( CHOICE ( block )+ ) | expression | var_declaration )
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
                    // GCLChecker.g:71:5: ^( ALAP block )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    ALAP10=(CommonTree)match(input,ALAP,FOLLOW_ALAP_in_statement141); 


                    if ( _first_0==null ) _first_0 = ALAP10;
                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_block_in_statement143);
                    block11=block();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = block11.tree;

                    match(input, Token.UP, null); _last = _save_last_1;
                    }


                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 2 :
                    // GCLChecker.g:72:5: ^( WHILE condition block )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    WHILE12=(CommonTree)match(input,WHILE,FOLLOW_WHILE_in_statement151); 


                    if ( _first_0==null ) _first_0 = WHILE12;
                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_condition_in_statement153);
                    condition13=condition();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = condition13.tree;
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_block_in_statement155);
                    block14=block();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = block14.tree;

                    match(input, Token.UP, null); _last = _save_last_1;
                    }


                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 3 :
                    // GCLChecker.g:73:5: ^( UNTIL condition block )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    UNTIL15=(CommonTree)match(input,UNTIL,FOLLOW_UNTIL_in_statement163); 


                    if ( _first_0==null ) _first_0 = UNTIL15;
                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_condition_in_statement165);
                    condition16=condition();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = condition16.tree;
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_block_in_statement167);
                    block17=block();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = block17.tree;

                    match(input, Token.UP, null); _last = _save_last_1;
                    }


                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 4 :
                    // GCLChecker.g:74:5: ^( DO block condition )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    DO18=(CommonTree)match(input,DO,FOLLOW_DO_in_statement175); 


                    if ( _first_0==null ) _first_0 = DO18;
                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_block_in_statement177);
                    block19=block();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = block19.tree;
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_condition_in_statement179);
                    condition20=condition();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = condition20.tree;

                    match(input, Token.UP, null); _last = _save_last_1;
                    }


                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 5 :
                    // GCLChecker.g:75:5: ^( TRY block ( block )? )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    TRY21=(CommonTree)match(input,TRY,FOLLOW_TRY_in_statement187); 


                    if ( _first_0==null ) _first_0 = TRY21;
                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_block_in_statement189);
                    block22=block();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = block22.tree;
                    // GCLChecker.g:75:17: ( block )?
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0==BLOCK) ) {
                        alt3=1;
                    }
                    switch (alt3) {
                        case 1 :
                            // GCLChecker.g:75:18: block
                            {
                            _last = (CommonTree)input.LT(1);
                            pushFollow(FOLLOW_block_in_statement192);
                            block23=block();

                            state._fsp--;

                             
                            if ( _first_1==null ) _first_1 = block23.tree;

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
                    // GCLChecker.g:76:5: ^( IF condition block ( block )? )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    IF24=(CommonTree)match(input,IF,FOLLOW_IF_in_statement202); 


                    if ( _first_0==null ) _first_0 = IF24;
                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_condition_in_statement204);
                    condition25=condition();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = condition25.tree;
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_block_in_statement206);
                    block26=block();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = block26.tree;
                    // GCLChecker.g:76:26: ( block )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==BLOCK) ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // GCLChecker.g:76:27: block
                            {
                            _last = (CommonTree)input.LT(1);
                            pushFollow(FOLLOW_block_in_statement209);
                            block27=block();

                            state._fsp--;

                             
                            if ( _first_1==null ) _first_1 = block27.tree;

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
                    // GCLChecker.g:77:5: ^( CHOICE ( block )+ )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    CHOICE28=(CommonTree)match(input,CHOICE,FOLLOW_CHOICE_in_statement219); 


                    if ( _first_0==null ) _first_0 = CHOICE28;
                    match(input, Token.DOWN, null); 
                    // GCLChecker.g:77:14: ( block )+
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
                    	    // GCLChecker.g:77:14: block
                    	    {
                    	    _last = (CommonTree)input.LT(1);
                    	    pushFollow(FOLLOW_block_in_statement221);
                    	    block29=block();

                    	    state._fsp--;

                    	     
                    	    if ( _first_1==null ) _first_1 = block29.tree;

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
                    // GCLChecker.g:78:5: expression
                    {
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_expression_in_statement229);
                    expression30=expression();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = expression30.tree;

                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 9 :
                    // GCLChecker.g:79:5: var_declaration
                    {
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_var_declaration_in_statement235);
                    var_declaration31=var_declaration();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = var_declaration31.tree;

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
    // GCLChecker.g:82:1: expression : ( ^( OR expression expression ) | ^( PLUS e1= expression ) -> ^( PLUS $e1 $e1) | ^( STAR expression ) | ^( SHARP expression ) | rule | ANY | OTHER );
    public final GCLChecker.expression_return expression() throws RecognitionException {
        GCLChecker.expression_return retval = new GCLChecker.expression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree OR32=null;
        CommonTree PLUS35=null;
        CommonTree STAR36=null;
        CommonTree SHARP38=null;
        CommonTree ANY41=null;
        CommonTree OTHER42=null;
        GCLChecker.expression_return e1 = null;

        GCLChecker.expression_return expression33 = null;

        GCLChecker.expression_return expression34 = null;

        GCLChecker.expression_return expression37 = null;

        GCLChecker.expression_return expression39 = null;

        GCLChecker.rule_return rule40 = null;


        CommonTree OR32_tree=null;
        CommonTree PLUS35_tree=null;
        CommonTree STAR36_tree=null;
        CommonTree SHARP38_tree=null;
        CommonTree ANY41_tree=null;
        CommonTree OTHER42_tree=null;
        RewriteRuleNodeStream stream_PLUS=new RewriteRuleNodeStream(adaptor,"token PLUS");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        try {
            // GCLChecker.g:83:2: ( ^( OR expression expression ) | ^( PLUS e1= expression ) -> ^( PLUS $e1 $e1) | ^( STAR expression ) | ^( SHARP expression ) | rule | ANY | OTHER )
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
                    // GCLChecker.g:83:4: ^( OR expression expression )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    OR32=(CommonTree)match(input,OR,FOLLOW_OR_in_expression249); 


                    if ( _first_0==null ) _first_0 = OR32;
                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_expression_in_expression251);
                    expression33=expression();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = expression33.tree;
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_expression_in_expression253);
                    expression34=expression();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = expression34.tree;

                    match(input, Token.UP, null); _last = _save_last_1;
                    }


                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 2 :
                    // GCLChecker.g:84:4: ^( PLUS e1= expression )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    PLUS35=(CommonTree)match(input,PLUS,FOLLOW_PLUS_in_expression260);  
                    stream_PLUS.add(PLUS35);


                    if ( _first_0==null ) _first_0 = PLUS35;
                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression264);
                    e1=expression();

                    state._fsp--;

                    stream_expression.add(e1.getTree());

                    match(input, Token.UP, null); _last = _save_last_1;
                    }



                    // AST REWRITE
                    // elements: PLUS, e1, e1
                    // token labels: 
                    // rule labels: retval, e1
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);
                    RewriteRuleSubtreeStream stream_e1=new RewriteRuleSubtreeStream(adaptor,"token e1",e1!=null?e1.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 84:26: -> ^( PLUS $e1 $e1)
                    {
                        // GCLChecker.g:84:29: ^( PLUS $e1 $e1)
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
                    // GCLChecker.g:85:4: ^( STAR expression )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    STAR36=(CommonTree)match(input,STAR,FOLLOW_STAR_in_expression283); 


                    if ( _first_0==null ) _first_0 = STAR36;
                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_expression_in_expression285);
                    expression37=expression();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = expression37.tree;

                    match(input, Token.UP, null); _last = _save_last_1;
                    }


                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 4 :
                    // GCLChecker.g:86:4: ^( SHARP expression )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    SHARP38=(CommonTree)match(input,SHARP,FOLLOW_SHARP_in_expression292); 


                    if ( _first_0==null ) _first_0 = SHARP38;
                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_expression_in_expression294);
                    expression39=expression();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = expression39.tree;

                    match(input, Token.UP, null); _last = _save_last_1;
                    }


                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 5 :
                    // GCLChecker.g:87:4: rule
                    {
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_rule_in_expression300);
                    rule40=rule();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = rule40.tree;

                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 6 :
                    // GCLChecker.g:88:4: ANY
                    {
                    _last = (CommonTree)input.LT(1);
                    ANY41=(CommonTree)match(input,ANY,FOLLOW_ANY_in_expression305); 
                     
                    if ( _first_0==null ) _first_0 = ANY41;

                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 7 :
                    // GCLChecker.g:89:4: OTHER
                    {
                    _last = (CommonTree)input.LT(1);
                    OTHER42=(CommonTree)match(input,OTHER,FOLLOW_OTHER_in_expression310); 
                     
                    if ( _first_0==null ) _first_0 = OTHER42;

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
    // GCLChecker.g:92:1: condition : ( ^( OR condition condition ) | rule | TRUE );
    public final GCLChecker.condition_return condition() throws RecognitionException {
        GCLChecker.condition_return retval = new GCLChecker.condition_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree OR43=null;
        CommonTree TRUE47=null;
        GCLChecker.condition_return condition44 = null;

        GCLChecker.condition_return condition45 = null;

        GCLChecker.rule_return rule46 = null;


        CommonTree OR43_tree=null;
        CommonTree TRUE47_tree=null;

        try {
            // GCLChecker.g:93:3: ( ^( OR condition condition ) | rule | TRUE )
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
                    // GCLChecker.g:93:5: ^( OR condition condition )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    OR43=(CommonTree)match(input,OR,FOLLOW_OR_in_condition324); 


                    if ( _first_0==null ) _first_0 = OR43;
                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_condition_in_condition326);
                    condition44=condition();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = condition44.tree;
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_condition_in_condition328);
                    condition45=condition();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = condition45.tree;

                    match(input, Token.UP, null); _last = _save_last_1;
                    }


                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 2 :
                    // GCLChecker.g:94:5: rule
                    {
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_rule_in_condition335);
                    rule46=rule();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = rule46.tree;

                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 3 :
                    // GCLChecker.g:95:5: TRUE
                    {
                    _last = (CommonTree)input.LT(1);
                    TRUE47=(CommonTree)match(input,TRUE,FOLLOW_TRUE_in_condition341); 
                     
                    if ( _first_0==null ) _first_0 = TRUE47;

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
    // GCLChecker.g:98:1: rule : ^( CALL r= IDENTIFIER ( param )* ) ;
    public final GCLChecker.rule_return rule() throws RecognitionException {
        GCLChecker.rule_return retval = new GCLChecker.rule_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree r=null;
        CommonTree CALL48=null;
        GCLChecker.param_return param49 = null;


        CommonTree r_tree=null;
        CommonTree CALL48_tree=null;

        try {
            // GCLChecker.g:99:3: ( ^( CALL r= IDENTIFIER ( param )* ) )
            // GCLChecker.g:99:5: ^( CALL r= IDENTIFIER ( param )* )
            {
            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            _last = (CommonTree)input.LT(1);
            CALL48=(CommonTree)match(input,CALL,FOLLOW_CALL_in_rule355); 


            if ( _first_0==null ) _first_0 = CALL48;
            match(input, Token.DOWN, null); 
            _last = (CommonTree)input.LT(1);
            r=(CommonTree)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_rule359); 
             
            if ( _first_1==null ) _first_1 = r;
             currentRule = r.getText(); 
            // GCLChecker.g:99:56: ( param )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==PARAM) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // GCLChecker.g:99:56: param
            	    {
            	    _last = (CommonTree)input.LT(1);
            	    pushFollow(FOLLOW_param_in_rule363);
            	    param49=param();

            	    state._fsp--;

            	     
            	    if ( _first_1==null ) _first_1 = param49.tree;

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
            		debug("checking if "+(r!=null?r.getText():null)+" exists");
            		if (!namespace.hasRule((r!=null?r.getText():null)) && !namespace.hasProc((r!=null?r.getText():null))) {
            			debug("ERROR!");
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
    // GCLChecker.g:114:1: var_declaration : ^( VAR var_type IDENTIFIER ) ;
    public final GCLChecker.var_declaration_return var_declaration() throws RecognitionException {
        GCLChecker.var_declaration_return retval = new GCLChecker.var_declaration_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree VAR50=null;
        CommonTree IDENTIFIER52=null;
        GCLChecker.var_type_return var_type51 = null;


        CommonTree VAR50_tree=null;
        CommonTree IDENTIFIER52_tree=null;

        try {
            // GCLChecker.g:115:2: ( ^( VAR var_type IDENTIFIER ) )
            // GCLChecker.g:115:4: ^( VAR var_type IDENTIFIER )
            {
            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            _last = (CommonTree)input.LT(1);
            VAR50=(CommonTree)match(input,VAR,FOLLOW_VAR_in_var_declaration381); 


            if ( _first_0==null ) _first_0 = VAR50;
            match(input, Token.DOWN, null); 
            _last = (CommonTree)input.LT(1);
            pushFollow(FOLLOW_var_type_in_var_declaration383);
            var_type51=var_type();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = var_type51.tree;
            _last = (CommonTree)input.LT(1);
            IDENTIFIER52=(CommonTree)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_var_declaration385); 
             
            if ( _first_1==null ) _first_1 = IDENTIFIER52;
             namespace.addVariable((IDENTIFIER52!=null?IDENTIFIER52.getText():null)); st.declareSymbol((IDENTIFIER52!=null?IDENTIFIER52.getText():null)); 

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
    // GCLChecker.g:118:1: var_type : NODE_TYPE ;
    public final GCLChecker.var_type_return var_type() throws RecognitionException {
        GCLChecker.var_type_return retval = new GCLChecker.var_type_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree NODE_TYPE53=null;

        CommonTree NODE_TYPE53_tree=null;

        try {
            // GCLChecker.g:119:2: ( NODE_TYPE )
            // GCLChecker.g:119:4: NODE_TYPE
            {
            _last = (CommonTree)input.LT(1);
            NODE_TYPE53=(CommonTree)match(input,NODE_TYPE,FOLLOW_NODE_TYPE_in_var_type400); 
             
            if ( _first_0==null ) _first_0 = NODE_TYPE53;

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
    // GCLChecker.g:122:1: param : ( ^( PARAM IDENTIFIER ) | ^( PARAM OUT IDENTIFIER ) | ^( PARAM DONT_CARE ) );
    public final GCLChecker.param_return param() throws RecognitionException {
        GCLChecker.param_return retval = new GCLChecker.param_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree PARAM54=null;
        CommonTree IDENTIFIER55=null;
        CommonTree PARAM56=null;
        CommonTree OUT57=null;
        CommonTree IDENTIFIER58=null;
        CommonTree PARAM59=null;
        CommonTree DONT_CARE60=null;

        CommonTree PARAM54_tree=null;
        CommonTree IDENTIFIER55_tree=null;
        CommonTree PARAM56_tree=null;
        CommonTree OUT57_tree=null;
        CommonTree IDENTIFIER58_tree=null;
        CommonTree PARAM59_tree=null;
        CommonTree DONT_CARE60_tree=null;

        try {
            // GCLChecker.g:123:2: ( ^( PARAM IDENTIFIER ) | ^( PARAM OUT IDENTIFIER ) | ^( PARAM DONT_CARE ) )
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
                    // GCLChecker.g:123:4: ^( PARAM IDENTIFIER )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    PARAM54=(CommonTree)match(input,PARAM,FOLLOW_PARAM_in_param412); 


                    if ( _first_0==null ) _first_0 = PARAM54;
                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    IDENTIFIER55=(CommonTree)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_param414); 
                     
                    if ( _first_1==null ) _first_1 = IDENTIFIER55;
                    
                    			numParameters++;
                    			if (st.isDeclared((IDENTIFIER55!=null?IDENTIFIER55.getText():null))) {
                    				if (!st.isInitialized((IDENTIFIER55!=null?IDENTIFIER55.getText():null))) {
                    					errors.add("The variable "+(IDENTIFIER55!=null?IDENTIFIER55.getText():null)+" might not have been initialized on line "+(IDENTIFIER55!=null?IDENTIFIER55.getLine():0));
                    				}
                    			} else {
                    				errors.add("No such variable: "+(IDENTIFIER55!=null?IDENTIFIER55.getText():null));
                    			}
                    			if (!namespace.getRule(currentRule).isInputParameter(numParameters)) {
                    				errors.add("Parameter number "+(numParameters)+" cannot be an input parameter in rule "+currentRule+" on line "+(IDENTIFIER55!=null?IDENTIFIER55.getLine():0));
                    			}
                    		

                    match(input, Token.UP, null); _last = _save_last_1;
                    }


                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 2 :
                    // GCLChecker.g:136:4: ^( PARAM OUT IDENTIFIER )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    PARAM56=(CommonTree)match(input,PARAM,FOLLOW_PARAM_in_param424); 


                    if ( _first_0==null ) _first_0 = PARAM56;
                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    OUT57=(CommonTree)match(input,OUT,FOLLOW_OUT_in_param426); 
                     
                    if ( _first_1==null ) _first_1 = OUT57;
                    _last = (CommonTree)input.LT(1);
                    IDENTIFIER58=(CommonTree)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_param428); 
                     
                    if ( _first_1==null ) _first_1 = IDENTIFIER58;
                    
                    			numParameters++;
                    			
                    			if (st.isDeclared((IDENTIFIER58!=null?IDENTIFIER58.getText():null))) {
                    				if (!st.canInitialize((IDENTIFIER58!=null?IDENTIFIER58.getText():null))) {
                    					errors.add("Variable already initialized: "+(IDENTIFIER58!=null?IDENTIFIER58.getText():null)+" on line "+(IDENTIFIER58!=null?IDENTIFIER58.getLine():0));
                    				} else {
                    					st.initializeSymbol((IDENTIFIER58!=null?IDENTIFIER58.getText():null));
                    				}
                    			} else {
                    				errors.add("No such variable: "+(IDENTIFIER58!=null?IDENTIFIER58.getText():null)+" on line "+(IDENTIFIER58!=null?IDENTIFIER58.getLine():0));
                    			}
                    			if (!namespace.getRule(currentRule).isOutputParameter(numParameters)) {
                    				errors.add("Parameter number "+(numParameters)+" cannot be an output parameter in rule "+currentRule+" on line "+(IDENTIFIER58!=null?IDENTIFIER58.getLine():0));
                    			}
                    			if (currentOutputParameters.contains((IDENTIFIER58!=null?IDENTIFIER58.getText():null))) {
                    				errors.add("You can not use the same parameter as output more than once per call: "+(IDENTIFIER58!=null?IDENTIFIER58.getText():null)+" on line "+(IDENTIFIER58!=null?IDENTIFIER58.getLine():0));			
                    			}
                    			currentOutputParameters.add((IDENTIFIER58!=null?IDENTIFIER58.getText():null));
                    		

                    match(input, Token.UP, null); _last = _save_last_1;
                    }


                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 3 :
                    // GCLChecker.g:156:4: ^( PARAM DONT_CARE )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    PARAM59=(CommonTree)match(input,PARAM,FOLLOW_PARAM_in_param438); 


                    if ( _first_0==null ) _first_0 = PARAM59;
                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    DONT_CARE60=(CommonTree)match(input,DONT_CARE,FOLLOW_DONT_CARE_in_param440); 
                     
                    if ( _first_1==null ) _first_1 = DONT_CARE60;
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
    public static final BitSet FOLLOW_FUNCTIONS_in_functions79 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_function_in_functions83 = new BitSet(new long[]{0x0000000000000088L});
    public static final BitSet FOLLOW_FUNCTION_in_function96 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENTIFIER_in_function98 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_function100 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BLOCK_in_block117 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_statement_in_block122 = new BitSet(new long[]{0x000000000F9BE708L});
    public static final BitSet FOLLOW_ALAP_in_statement141 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_statement143 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WHILE_in_statement151 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_statement153 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_statement155 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_UNTIL_in_statement163 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_statement165 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_statement167 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DO_in_statement175 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_statement177 = new BitSet(new long[]{0x0000000000402100L});
    public static final BitSet FOLLOW_condition_in_statement179 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TRY_in_statement187 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_statement189 = new BitSet(new long[]{0x0000000000000028L});
    public static final BitSet FOLLOW_block_in_statement192 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IF_in_statement202 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_statement204 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_statement206 = new BitSet(new long[]{0x0000000000000028L});
    public static final BitSet FOLLOW_block_in_statement209 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CHOICE_in_statement219 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_statement221 = new BitSet(new long[]{0x0000000000000028L});
    public static final BitSet FOLLOW_expression_in_statement229 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_declaration_in_statement235 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_expression249 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression251 = new BitSet(new long[]{0x000000000F802100L});
    public static final BitSet FOLLOW_expression_in_expression253 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PLUS_in_expression260 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression264 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STAR_in_expression283 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression285 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SHARP_in_expression292 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression294 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_rule_in_expression300 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_expression305 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_expression310 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_condition324 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_condition326 = new BitSet(new long[]{0x0000000000402100L});
    public static final BitSet FOLLOW_condition_in_condition328 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_rule_in_condition335 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_condition341 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CALL_in_rule355 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENTIFIER_in_rule359 = new BitSet(new long[]{0x0000000000000808L});
    public static final BitSet FOLLOW_param_in_rule363 = new BitSet(new long[]{0x0000000000000808L});
    public static final BitSet FOLLOW_VAR_in_var_declaration381 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_var_type_in_var_declaration383 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_var_declaration385 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NODE_TYPE_in_var_type400 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PARAM_in_param412 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENTIFIER_in_param414 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PARAM_in_param424 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_OUT_in_param426 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_param428 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PARAM_in_param438 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DONT_CARE_in_param440 = new BitSet(new long[]{0x0000000000000008L});

}