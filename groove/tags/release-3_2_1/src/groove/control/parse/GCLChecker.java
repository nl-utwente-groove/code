// $ANTLR 3.1b1 GCLChecker.g 2008-11-28 11:20:19

package groove.control.parse;
import groove.control.*;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


@SuppressWarnings("all")              
public class GCLChecker extends TreeParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "PROGRAM", "BLOCK", "FUNCTIONS", "FUNCTION", "CALL", "DO", "IDENTIFIER", "OR", "ALAP", "WHILE", "UNTIL", "TRY", "ELSE", "IF", "CHOICE", "CH_OR", "TRUE", "PLUS", "STAR", "SHARP", "ANY", "OTHER", "AND", "COMMA", "DOT", "NOT", "WS", "'{'", "'}'", "'('", "')'", "';'"
    };
    public static final int FUNCTION=7;
    public static final int STAR=22;
    public static final int SHARP=23;
    public static final int OTHER=25;
    public static final int FUNCTIONS=6;
    public static final int WHILE=13;
    public static final int ELSE=16;
    public static final int DO=9;
    public static final int NOT=29;
    public static final int ALAP=12;
    public static final int AND=26;
    public static final int EOF=-1;
    public static final int TRUE=20;
    public static final int TRY=15;
    public static final int IF=17;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int T__33=33;
    public static final int ANY=24;
    public static final int WS=30;
    public static final int T__34=34;
    public static final int T__35=35;
    public static final int COMMA=27;
    public static final int UNTIL=14;
    public static final int IDENTIFIER=10;
    public static final int BLOCK=5;
    public static final int OR=11;
    public static final int CH_OR=19;
    public static final int PROGRAM=4;
    public static final int PLUS=21;
    public static final int CALL=8;
    public static final int DOT=28;
    public static final int CHOICE=18;

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


    public static class program_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start program
    // GCLChecker.g:24:1: program : ^( PROGRAM functions block ) ;
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
            // GCLChecker.g:25:3: ( ^( PROGRAM functions block ) )
            // GCLChecker.g:25:6: ^( PROGRAM functions block )
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
    // GCLChecker.g:28:1: functions : ^( FUNCTIONS ( function )* ) ;
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
            // GCLChecker.g:29:3: ( ^( FUNCTIONS ( function )* ) )
            // GCLChecker.g:29:5: ^( FUNCTIONS ( function )* )
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
                // GCLChecker.g:29:17: ( function )*
                loop1:
                do {
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( (LA1_0==FUNCTION) ) {
                        alt1=1;
                    }


                    switch (alt1) {
                	case 1 :
                	    // GCLChecker.g:29:17: function
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
    // GCLChecker.g:31:1: function : ^( FUNCTION IDENTIFIER block ) -> ^( FUNCTION IDENTIFIER ) ;
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
            // GCLChecker.g:32:3: ( ^( FUNCTION IDENTIFIER block ) -> ^( FUNCTION IDENTIFIER ) )
            // GCLChecker.g:33:3: ^( FUNCTION IDENTIFIER block )
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
             namespace.store( (IDENTIFIER7!=null?IDENTIFIER7.getText():null) , (block8!=null?((CommonTree)block8.tree):null)); 

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
            // 33:88: -> ^( FUNCTION IDENTIFIER )
            {
                // GCLChecker.g:33:91: ^( FUNCTION IDENTIFIER )
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
    // GCLChecker.g:35:1: block : ^( BLOCK ( statement )* ) ;
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
            // GCLChecker.g:36:3: ( ^( BLOCK ( statement )* ) )
            // GCLChecker.g:36:5: ^( BLOCK ( statement )* )
            {
            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            _last = (CommonTree)input.LT(1);
            BLOCK9=(CommonTree)match(input,BLOCK,FOLLOW_BLOCK_in_block125); 


            if ( _first_0==null ) _first_0 = BLOCK9;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // GCLChecker.g:36:13: ( statement )*
                loop2:
                do {
                    int alt2=2;
                    int LA2_0 = input.LA(1);

                    if ( ((LA2_0>=CALL && LA2_0<=TRY)||(LA2_0>=IF && LA2_0<=CHOICE)||(LA2_0>=PLUS && LA2_0<=OTHER)) ) {
                        alt2=1;
                    }


                    switch (alt2) {
                	case 1 :
                	    // GCLChecker.g:36:14: statement
                	    {
                	    _last = (CommonTree)input.LT(1);
                	    pushFollow(FOLLOW_statement_in_block128);
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
    // GCLChecker.g:39:1: statement : ( ^( ALAP block ) | ^( WHILE condition block ) | ^( UNTIL condition block ) | ^( DO block condition ) | ^( TRY block ( block )? ) | ^( IF condition block ( block )? ) | ^( CHOICE ( block )+ ) | expression );
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


        CommonTree ALAP11_tree=null;
        CommonTree WHILE13_tree=null;
        CommonTree UNTIL16_tree=null;
        CommonTree DO19_tree=null;
        CommonTree TRY22_tree=null;
        CommonTree IF25_tree=null;
        CommonTree CHOICE29_tree=null;

        try {
            // GCLChecker.g:40:3: ( ^( ALAP block ) | ^( WHILE condition block ) | ^( UNTIL condition block ) | ^( DO block condition ) | ^( TRY block ( block )? ) | ^( IF condition block ( block )? ) | ^( CHOICE ( block )+ ) | expression )
            int alt6=8;
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
            case IDENTIFIER:
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
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }

            switch (alt6) {
                case 1 :
                    // GCLChecker.g:40:5: ^( ALAP block )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    ALAP11=(CommonTree)match(input,ALAP,FOLLOW_ALAP_in_statement145); 


                    if ( _first_0==null ) _first_0 = ALAP11;
                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_block_in_statement147);
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
                    // GCLChecker.g:41:5: ^( WHILE condition block )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    WHILE13=(CommonTree)match(input,WHILE,FOLLOW_WHILE_in_statement155); 


                    if ( _first_0==null ) _first_0 = WHILE13;
                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_condition_in_statement157);
                    condition14=condition();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = condition14.tree;
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_block_in_statement159);
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
                    // GCLChecker.g:42:5: ^( UNTIL condition block )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    UNTIL16=(CommonTree)match(input,UNTIL,FOLLOW_UNTIL_in_statement167); 


                    if ( _first_0==null ) _first_0 = UNTIL16;
                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_condition_in_statement169);
                    condition17=condition();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = condition17.tree;
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_block_in_statement171);
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
                    // GCLChecker.g:43:5: ^( DO block condition )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    DO19=(CommonTree)match(input,DO,FOLLOW_DO_in_statement179); 


                    if ( _first_0==null ) _first_0 = DO19;
                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_block_in_statement181);
                    block20=block();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = block20.tree;
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_condition_in_statement183);
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
                    // GCLChecker.g:44:5: ^( TRY block ( block )? )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    TRY22=(CommonTree)match(input,TRY,FOLLOW_TRY_in_statement191); 


                    if ( _first_0==null ) _first_0 = TRY22;
                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_block_in_statement193);
                    block23=block();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = block23.tree;
                    // GCLChecker.g:44:17: ( block )?
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0==BLOCK) ) {
                        alt3=1;
                    }
                    switch (alt3) {
                        case 1 :
                            // GCLChecker.g:44:18: block
                            {
                            _last = (CommonTree)input.LT(1);
                            pushFollow(FOLLOW_block_in_statement196);
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
                    // GCLChecker.g:45:5: ^( IF condition block ( block )? )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    IF25=(CommonTree)match(input,IF,FOLLOW_IF_in_statement206); 


                    if ( _first_0==null ) _first_0 = IF25;
                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_condition_in_statement208);
                    condition26=condition();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = condition26.tree;
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_block_in_statement210);
                    block27=block();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = block27.tree;
                    // GCLChecker.g:45:26: ( block )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==BLOCK) ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // GCLChecker.g:45:27: block
                            {
                            _last = (CommonTree)input.LT(1);
                            pushFollow(FOLLOW_block_in_statement213);
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
                    // GCLChecker.g:46:5: ^( CHOICE ( block )+ )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    CHOICE29=(CommonTree)match(input,CHOICE,FOLLOW_CHOICE_in_statement223); 


                    if ( _first_0==null ) _first_0 = CHOICE29;
                    match(input, Token.DOWN, null); 
                    // GCLChecker.g:46:14: ( block )+
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
                    	    // GCLChecker.g:46:14: block
                    	    {
                    	    _last = (CommonTree)input.LT(1);
                    	    pushFollow(FOLLOW_block_in_statement225);
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
                    // GCLChecker.g:47:5: expression
                    {
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_expression_in_statement233);
                    expression31=expression();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = expression31.tree;

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
    // GCLChecker.g:50:1: expression : ( ^( OR expression expression ) | ^( PLUS e1= expression ) -> ^( PLUS $e1 $e1) | ^( STAR expression ) | ^( SHARP expression ) | ^( CALL IDENTIFIER ) | rule | ANY | OTHER );
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
        CommonTree CALL40=null;
        CommonTree IDENTIFIER41=null;
        CommonTree ANY43=null;
        CommonTree OTHER44=null;
        GCLChecker.expression_return e1 = null;

        GCLChecker.expression_return expression33 = null;

        GCLChecker.expression_return expression34 = null;

        GCLChecker.expression_return expression37 = null;

        GCLChecker.expression_return expression39 = null;

        GCLChecker.rule_return rule42 = null;


        CommonTree OR32_tree=null;
        CommonTree PLUS35_tree=null;
        CommonTree STAR36_tree=null;
        CommonTree SHARP38_tree=null;
        CommonTree CALL40_tree=null;
        CommonTree IDENTIFIER41_tree=null;
        CommonTree ANY43_tree=null;
        CommonTree OTHER44_tree=null;
        RewriteRuleNodeStream stream_PLUS=new RewriteRuleNodeStream(adaptor,"token PLUS");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        try {
            // GCLChecker.g:51:2: ( ^( OR expression expression ) | ^( PLUS e1= expression ) -> ^( PLUS $e1 $e1) | ^( STAR expression ) | ^( SHARP expression ) | ^( CALL IDENTIFIER ) | rule | ANY | OTHER )
            int alt7=8;
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
            case IDENTIFIER:
                {
                alt7=6;
                }
                break;
            case ANY:
                {
                alt7=7;
                }
                break;
            case OTHER:
                {
                alt7=8;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;
            }

            switch (alt7) {
                case 1 :
                    // GCLChecker.g:51:4: ^( OR expression expression )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    OR32=(CommonTree)match(input,OR,FOLLOW_OR_in_expression247); 


                    if ( _first_0==null ) _first_0 = OR32;
                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_expression_in_expression249);
                    expression33=expression();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = expression33.tree;
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_expression_in_expression251);
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
                    // GCLChecker.g:52:4: ^( PLUS e1= expression )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    PLUS35=(CommonTree)match(input,PLUS,FOLLOW_PLUS_in_expression258);  
                    stream_PLUS.add(PLUS35);


                    if ( _first_0==null ) _first_0 = PLUS35;
                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression262);
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
                    // 52:26: -> ^( PLUS $e1 $e1)
                    {
                        // GCLChecker.g:52:29: ^( PLUS $e1 $e1)
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
                    // GCLChecker.g:53:4: ^( STAR expression )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    STAR36=(CommonTree)match(input,STAR,FOLLOW_STAR_in_expression281); 


                    if ( _first_0==null ) _first_0 = STAR36;
                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_expression_in_expression283);
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
                    // GCLChecker.g:54:4: ^( SHARP expression )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    SHARP38=(CommonTree)match(input,SHARP,FOLLOW_SHARP_in_expression290); 


                    if ( _first_0==null ) _first_0 = SHARP38;
                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_expression_in_expression292);
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
                    // GCLChecker.g:55:4: ^( CALL IDENTIFIER )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    CALL40=(CommonTree)match(input,CALL,FOLLOW_CALL_in_expression299); 


                    if ( _first_0==null ) _first_0 = CALL40;
                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    IDENTIFIER41=(CommonTree)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_expression301); 
                     
                    if ( _first_1==null ) _first_1 = IDENTIFIER41;

                    match(input, Token.UP, null); _last = _save_last_1;
                    }


                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 6 :
                    // GCLChecker.g:56:4: rule
                    {
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_rule_in_expression307);
                    rule42=rule();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = rule42.tree;

                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 7 :
                    // GCLChecker.g:57:4: ANY
                    {
                    _last = (CommonTree)input.LT(1);
                    ANY43=(CommonTree)match(input,ANY,FOLLOW_ANY_in_expression312); 
                     
                    if ( _first_0==null ) _first_0 = ANY43;

                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 8 :
                    // GCLChecker.g:58:4: OTHER
                    {
                    _last = (CommonTree)input.LT(1);
                    OTHER44=(CommonTree)match(input,OTHER,FOLLOW_OTHER_in_expression317); 
                     
                    if ( _first_0==null ) _first_0 = OTHER44;

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
    // GCLChecker.g:61:1: condition : ( ^( OR condition condition ) | rule | TRUE );
    public final GCLChecker.condition_return condition() throws RecognitionException {
        GCLChecker.condition_return retval = new GCLChecker.condition_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree OR45=null;
        CommonTree TRUE49=null;
        GCLChecker.condition_return condition46 = null;

        GCLChecker.condition_return condition47 = null;

        GCLChecker.rule_return rule48 = null;


        CommonTree OR45_tree=null;
        CommonTree TRUE49_tree=null;

        try {
            // GCLChecker.g:62:3: ( ^( OR condition condition ) | rule | TRUE )
            int alt8=3;
            switch ( input.LA(1) ) {
            case OR:
                {
                alt8=1;
                }
                break;
            case IDENTIFIER:
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
                    // GCLChecker.g:62:5: ^( OR condition condition )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    OR45=(CommonTree)match(input,OR,FOLLOW_OR_in_condition331); 


                    if ( _first_0==null ) _first_0 = OR45;
                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_condition_in_condition333);
                    condition46=condition();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = condition46.tree;
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_condition_in_condition335);
                    condition47=condition();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = condition47.tree;

                    match(input, Token.UP, null); _last = _save_last_1;
                    }


                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 2 :
                    // GCLChecker.g:63:5: rule
                    {
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_rule_in_condition342);
                    rule48=rule();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = rule48.tree;

                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 3 :
                    // GCLChecker.g:64:5: TRUE
                    {
                    _last = (CommonTree)input.LT(1);
                    TRUE49=(CommonTree)match(input,TRUE,FOLLOW_TRUE_in_condition348); 
                     
                    if ( _first_0==null ) _first_0 = TRUE49;

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
    // GCLChecker.g:67:1: rule : IDENTIFIER ;
    public final GCLChecker.rule_return rule() throws RecognitionException {
        GCLChecker.rule_return retval = new GCLChecker.rule_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree IDENTIFIER50=null;

        CommonTree IDENTIFIER50_tree=null;

        try {
            // GCLChecker.g:68:3: ( IDENTIFIER )
            // GCLChecker.g:68:5: IDENTIFIER
            {
            _last = (CommonTree)input.LT(1);
            IDENTIFIER50=(CommonTree)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_rule361); 
             
            if ( _first_0==null ) _first_0 = IDENTIFIER50;

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
    public static final BitSet FOLLOW_statement_in_block128 = new BitSet(new long[]{0x0000000003E6FF08L});
    public static final BitSet FOLLOW_ALAP_in_statement145 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_statement147 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WHILE_in_statement155 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_statement157 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_statement159 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_UNTIL_in_statement167 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_statement169 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_statement171 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DO_in_statement179 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_statement181 = new BitSet(new long[]{0x0000000000100C00L});
    public static final BitSet FOLLOW_condition_in_statement183 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TRY_in_statement191 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_statement193 = new BitSet(new long[]{0x0000000000000028L});
    public static final BitSet FOLLOW_block_in_statement196 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IF_in_statement206 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_statement208 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_statement210 = new BitSet(new long[]{0x0000000000000028L});
    public static final BitSet FOLLOW_block_in_statement213 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CHOICE_in_statement223 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_statement225 = new BitSet(new long[]{0x0000000000000028L});
    public static final BitSet FOLLOW_expression_in_statement233 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_expression247 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression249 = new BitSet(new long[]{0x0000000003E6FF08L});
    public static final BitSet FOLLOW_expression_in_expression251 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PLUS_in_expression258 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression262 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STAR_in_expression281 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression283 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SHARP_in_expression290 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression292 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CALL_in_expression299 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENTIFIER_in_expression301 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_rule_in_expression307 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_expression312 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_expression317 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_condition331 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_condition333 = new BitSet(new long[]{0x0000000000100C00L});
    public static final BitSet FOLLOW_condition_in_condition335 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_rule_in_condition342 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_condition348 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_rule361 = new BitSet(new long[]{0x0000000000000002L});

}