// $ANTLR 3.1b1 GCLBuilder.g 2008-07-14 13:51:45

package groove.control.parse;
import groove.control.*;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class GCLBuilder extends TreeParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "PROGRAM", "BLOCK", "FUNCTIONS", "FUNCTION", "CALL", "IDENTIFIER", "OR", "ALAP", "WHILE", "DO", "UNTIL", "TRY", "ELSE", "IF", "CHOICE", "CH_OR", "PLUS", "STAR", "SHARP", "AND", "COMMA", "DOT", "NOT", "WS", "'{'", "'}'", "'('", "')'", "';'", "'true'"
    };
    public static final int FUNCTION=7;
    public static final int T__29=29;
    public static final int STAR=21;
    public static final int T__28=28;
    public static final int SHARP=22;
    public static final int FUNCTIONS=6;
    public static final int WHILE=12;
    public static final int ELSE=16;
    public static final int DO=13;
    public static final int NOT=26;
    public static final int ALAP=11;
    public static final int AND=23;
    public static final int EOF=-1;
    public static final int TRY=15;
    public static final int IF=17;
    public static final int T__30=30;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int T__33=33;
    public static final int WS=27;
    public static final int COMMA=24;
    public static final int UNTIL=14;
    public static final int IDENTIFIER=9;
    public static final int BLOCK=5;
    public static final int OR=10;
    public static final int CH_OR=19;
    public static final int PROGRAM=4;
    public static final int PLUS=20;
    public static final int CALL=8;
    public static final int DOT=25;
    public static final int CHOICE=18;

    // delegates
    // delegators


        public GCLBuilder(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public GCLBuilder(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
        }
        

    public String[] getTokenNames() { return GCLBuilder.tokenNames; }
    public String getGrammarFileName() { return "GCLBuilder.g"; }

    
    	AutomatonBuilder builder;
        
        public void setBuilder(AutomatonBuilder ab) {
        	this.builder = ab;
        }
        
        private void proc(CommonTree block) throws RecognitionException {
        	TreeNodeStream restore = input;
        	input = new CommonTreeNodeStream(block);
        	block();
        	this.input = restore;
        }
    
        



    // $ANTLR start program
    // GCLBuilder.g:30:1: program returns [ControlShape shape=null] : ^( PROGRAM functions block ) ;
    public final ControlShape program() throws RecognitionException {
        ControlShape shape = null;

         ControlState start; ControlState end; 
        try {
            // GCLBuilder.g:32:3: ( ^( PROGRAM functions block ) )
            // GCLBuilder.g:32:5: ^( PROGRAM functions block )
            {
            match(input,PROGRAM,FOLLOW_PROGRAM_in_program52); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_functions_in_program54);
            functions();

            state._fsp--;

             builder.startProgram(); shape = builder.currentShape(); start = builder.getStart(); end = builder.getEnd(); 
            pushFollow(FOLLOW_block_in_program64);
            block();

            state._fsp--;

             builder.endProgram(); 

            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return shape;
    }
    // $ANTLR end program


    // $ANTLR start functions
    // GCLBuilder.g:38:1: functions : ^( FUNCTIONS ( function )* ) ;
    public final void functions() throws RecognitionException {
        try {
            // GCLBuilder.g:39:3: ( ^( FUNCTIONS ( function )* ) )
            // GCLBuilder.g:39:5: ^( FUNCTIONS ( function )* )
            {
            match(input,FUNCTIONS,FOLLOW_FUNCTIONS_in_functions82); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // GCLBuilder.g:39:17: ( function )*
                loop1:
                do {
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( (LA1_0==FUNCTION) ) {
                        alt1=1;
                    }


                    switch (alt1) {
                	case 1 :
                	    // GCLBuilder.g:39:17: function
                	    {
                	    pushFollow(FOLLOW_function_in_functions84);
                	    function();

                	    state._fsp--;


                	    }
                	    break;

                	default :
                	    break loop1;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end functions


    // $ANTLR start function
    // GCLBuilder.g:41:1: function : ^( FUNCTION IDENTIFIER ) ;
    public final void function() throws RecognitionException {
        try {
            // GCLBuilder.g:42:3: ( ^( FUNCTION IDENTIFIER ) )
            // GCLBuilder.g:42:5: ^( FUNCTION IDENTIFIER )
            {
            match(input,FUNCTION,FOLLOW_FUNCTION_in_function97); 

            match(input, Token.DOWN, null); 
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_function99); 

            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end function


    // $ANTLR start block
    // GCLBuilder.g:44:1: block : ^( BLOCK ( statement )* ) ;
    public final void block() throws RecognitionException {
         
        		ControlState start = builder.getStart(); 
        		ControlState end = builder.getEnd(); 
        		boolean empty = true;
        		boolean first = true;
        		ControlState newState = builder.newState();
          		builder.restore(start, newState);
          		ControlState tmpStart = start;

        try {
            // GCLBuilder.g:54:1: ( ^( BLOCK ( statement )* ) )
            // GCLBuilder.g:54:4: ^( BLOCK ( statement )* )
            {
            match(input,BLOCK,FOLLOW_BLOCK_in_block115); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // GCLBuilder.g:54:12: ( statement )*
                loop2:
                do {
                    int alt2=2;
                    int LA2_0 = input.LA(1);

                    if ( ((LA2_0>=CALL && LA2_0<=TRY)||(LA2_0>=IF && LA2_0<=CHOICE)||(LA2_0>=PLUS && LA2_0<=SHARP)||LA2_0==33) ) {
                        alt2=1;
                    }


                    switch (alt2) {
                	case 1 :
                	    // GCLBuilder.g:54:13: statement
                	    {
                	    pushFollow(FOLLOW_statement_in_block118);
                	    statement();

                	    state._fsp--;

                	     	if( !first ) { 
                	    					builder.deltaInitCopy(tmpStart, start); 
                	    				} else {
                	    					first = false;
                	    				}
                	    				tmpStart = newState;
                	    				builder.restore(newState, newState = builder.newState());
                	    				empty = false;
                	    			

                	    }
                	    break;

                	default :
                	    break loop2;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }
            
            	builder.rmState(newState);
            	builder.restore(builder.getStart(), end);
            	builder.merge();
            	if( empty ) { builder.tagDelta(start); }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end block


    // $ANTLR start statement
    // GCLBuilder.g:74:1: statement : ( ^( ALAP block ) | ^( WHILE condition block ) | ^( DO block condition ) | ^( UNTIL condition block ) | ^( TRY block ( block )? ) | ^( IF condition block ( block )? ) | ^( CHOICE ( block )+ ) | expression );
    public final void statement() throws RecognitionException {
        ControlState start = builder.getStart(); ControlState end = builder.getEnd(); ControlState newState; ControlTransition fail;
        try {
            // GCLBuilder.g:75:133: ( ^( ALAP block ) | ^( WHILE condition block ) | ^( DO block condition ) | ^( UNTIL condition block ) | ^( TRY block ( block )? ) | ^( IF condition block ( block )? ) | ^( CHOICE ( block )+ ) | expression )
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
            case DO:
                {
                alt6=3;
                }
                break;
            case UNTIL:
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
            case 33:
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
                    // GCLBuilder.g:76:2: ^( ALAP block )
                    {
                    match(input,ALAP,FOLLOW_ALAP_in_statement155); 

                     fail = builder.addElse(); newState = builder.newState(); builder.restore(newState, start); builder.addLambda(); builder.restore(start, newState); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_statement169);
                    block();

                    state._fsp--;

                     builder.fail(start,fail); builder.tagDelta(start); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // GCLBuilder.g:84:4: ^( WHILE condition block )
                    {
                    match(input,WHILE,FOLLOW_WHILE_in_statement198); 

                     fail = builder.addElse(); newState = builder.newState(); builder.restore(start, newState); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_condition_in_statement211);
                    condition();

                    state._fsp--;

                     builder.fail(start, fail); builder.restore(newState, start);
                    pushFollow(FOLLOW_block_in_statement224);
                    block();

                    state._fsp--;

                     builder.deltaInitCopy(newState, start); builder.tagDelta(start); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // GCLBuilder.g:91:5: ^( DO block condition )
                    {
                    match(input,DO,FOLLOW_DO_in_statement244); 

                     newState = builder.newState(); builder.restore(newState, end); fail = builder.addElse(); builder.restore(start, newState); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_statement255);
                    block();

                    state._fsp--;

                     builder.restore(newState, start); 
                    pushFollow(FOLLOW_condition_in_statement266);
                    condition();

                    state._fsp--;

                     builder.fail(newState,fail); builder.tagDelta(newState); builder.deltaInitCopy(newState, start); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // GCLBuilder.g:99:5: ^( UNTIL condition block )
                    {
                    match(input,UNTIL,FOLLOW_UNTIL_in_statement285); 

                     newState = builder.newState(); builder.restore(start, end); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_condition_in_statement296);
                    condition();

                    state._fsp--;

                     builder.restore(start, newState); fail = builder.addElse(); builder.fail(start, fail); builder.restore(newState,start); 
                    pushFollow(FOLLOW_block_in_statement307);
                    block();

                    state._fsp--;

                     builder.tagDelta(newState); builder.deltaInitCopy(newState, start); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 5 :
                    // GCLBuilder.g:111:3: ^( TRY block ( block )? )
                    {
                    match(input,TRY,FOLLOW_TRY_in_statement334); 

                     builder.debug("try,enter");
                     newState = builder.newState(); builder.restore(start, newState); fail = builder.addElse(); builder.restore(start, end);

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_statement349);
                    block();

                    state._fsp--;

                    builder.fail(start, fail); builder.restore(newState, end); boolean block = false; 
                    // GCLBuilder.g:115:5: ( block )?
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0==BLOCK) ) {
                        alt3=1;
                    }
                    switch (alt3) {
                        case 1 :
                            // GCLBuilder.g:115:6: block
                            {
                            pushFollow(FOLLOW_block_in_statement363);
                            block();

                            state._fsp--;

                            block = true;

                            }
                            break;

                    }

                     if(!block){builder.merge(); builder.tagDelta(start);}else{builder.initCopy(newState, start);}
                     builder.debug("try,exit");

                    match(input, Token.UP, null); 

                    }
                    break;
                case 6 :
                    // GCLBuilder.g:122:4: ^( IF condition block ( block )? )
                    {
                    match(input,IF,FOLLOW_IF_in_statement400); 

                     newState = builder.newState(); builder.restore(start, newState); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_condition_in_statement411);
                    condition();

                    state._fsp--;

                    builder.restore(newState, end);
                    pushFollow(FOLLOW_block_in_statement422);
                    block();

                    state._fsp--;

                      newState = builder.newState(); builder.restore(start, newState); fail = builder.addElse(); builder.fail(start,fail); builder.restore(newState,end); boolean block = false; 
                    // GCLBuilder.g:128:4: ( block )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==BLOCK) ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // GCLBuilder.g:128:5: block
                            {
                            pushFollow(FOLLOW_block_in_statement434);
                            block();

                            state._fsp--;

                            block = true;

                            }
                            break;

                    }

                    if(!block){builder.merge();builder.tagDelta(start);}else{builder.initCopy(newState, start);}

                    match(input, Token.UP, null); 

                    }
                    break;
                case 7 :
                    // GCLBuilder.g:133:5: ^( CHOICE ( block )+ )
                    {
                    match(input,CHOICE,FOLLOW_CHOICE_in_statement466); 

                    match(input, Token.DOWN, null); 
                    // GCLBuilder.g:134:4: ( block )+
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
                    	    // GCLBuilder.g:134:6: block
                    	    {
                    	     newState = builder.newState(); builder.restore(start, newState); builder.addLambda(); builder.restore(newState, end); 
                    	    pushFollow(FOLLOW_block_in_statement476);
                    	    block();

                    	    state._fsp--;

                    	     start.addInit(newState); 

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


                    match(input, Token.UP, null); 

                    }
                    break;
                case 8 :
                    // GCLBuilder.g:135:5: expression
                    {
                    pushFollow(FOLLOW_expression_in_statement488);
                    expression();

                    state._fsp--;


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
        return ;
    }
    // $ANTLR end statement


    // $ANTLR start expression
    // GCLBuilder.g:137:1: expression : ( ^( OR expression expression ) | ^( PLUS expression expression ) | ^( STAR expression ) | ^( SHARP expression ) | ^( CALL IDENTIFIER ) | 'true' | rule );
    public final void expression() throws RecognitionException {
        CommonTree IDENTIFIER1=null;

         ControlState start = builder.getStart(); ControlState end = builder.getEnd(); ControlState newState; ControlTransition fail; 
        try {
            // GCLBuilder.g:139:2: ( ^( OR expression expression ) | ^( PLUS expression expression ) | ^( STAR expression ) | ^( SHARP expression ) | ^( CALL IDENTIFIER ) | 'true' | rule )
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
            case 33:
                {
                alt7=6;
                }
                break;
            case IDENTIFIER:
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
                    // GCLBuilder.g:139:4: ^( OR expression expression )
                    {
                    match(input,OR,FOLLOW_OR_in_expression502); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression504);
                    expression();

                    state._fsp--;

                    builder.restore(start, end);
                    pushFollow(FOLLOW_expression_in_expression508);
                    expression();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // GCLBuilder.g:140:4: ^( PLUS expression expression )
                    {
                    match(input,PLUS,FOLLOW_PLUS_in_expression515); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression517);
                    expression();

                    state._fsp--;

                     builder.restore(end,end); 
                    pushFollow(FOLLOW_expression_in_expression521);
                    expression();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // GCLBuilder.g:141:4: ^( STAR expression )
                    {
                    match(input,STAR,FOLLOW_STAR_in_expression528); 

                     newState = builder.newState(); builder.restore(start,newState); builder.addLambda(); builder.restore(newState,newState); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression532);
                    expression();

                    state._fsp--;

                     builder.restore(newState,end); builder.addLambda(); builder.tagDelta(start);

                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // GCLBuilder.g:142:4: ^( SHARP expression )
                    {
                    match(input,SHARP,FOLLOW_SHARP_in_expression542); 

                     fail = builder.addElse(); builder.restore(start, start); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression546);
                    expression();

                    state._fsp--;

                     builder.fail(start,fail); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 5 :
                    // GCLBuilder.g:143:4: ^( CALL IDENTIFIER )
                    {
                    match(input,CALL,FOLLOW_CALL_in_expression555); 

                    match(input, Token.DOWN, null); 
                    IDENTIFIER1=(CommonTree)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_expression557); 

                    match(input, Token.UP, null); 
                     proc(builder.getProc((IDENTIFIER1!=null?IDENTIFIER1.getText():null))); 

                    }
                    break;
                case 6 :
                    // GCLBuilder.g:144:4: 'true'
                    {
                    match(input,33,FOLLOW_33_in_expression565); 
                     builder.addLambda(); builder.tagDelta(start); 

                    }
                    break;
                case 7 :
                    // GCLBuilder.g:145:4: rule
                    {
                    pushFollow(FOLLOW_rule_in_expression572);
                    rule();

                    state._fsp--;


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
        return ;
    }
    // $ANTLR end expression


    // $ANTLR start condition
    // GCLBuilder.g:148:1: condition : expression ;
    public final void condition() throws RecognitionException {
        try {
            // GCLBuilder.g:149:3: ( expression )
            // GCLBuilder.g:149:5: expression
            {
            pushFollow(FOLLOW_expression_in_condition586);
            expression();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end condition


    // $ANTLR start rule
    // GCLBuilder.g:152:1: rule : IDENTIFIER ;
    public final void rule() throws RecognitionException {
        CommonTree IDENTIFIER2=null;

        try {
            // GCLBuilder.g:153:3: ( IDENTIFIER )
            // GCLBuilder.g:153:5: IDENTIFIER
            {
            IDENTIFIER2=(CommonTree)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_rule599); 
             builder.addTransition((IDENTIFIER2!=null?IDENTIFIER2.getText():null)); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end rule

    // Delegated rules


 

    public static final BitSet FOLLOW_PROGRAM_in_program52 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_functions_in_program54 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_program64 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FUNCTIONS_in_functions82 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_function_in_functions84 = new BitSet(new long[]{0x0000000000000088L});
    public static final BitSet FOLLOW_FUNCTION_in_function97 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENTIFIER_in_function99 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BLOCK_in_block115 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_statement_in_block118 = new BitSet(new long[]{0x000000020076FF08L});
    public static final BitSet FOLLOW_ALAP_in_statement155 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_statement169 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WHILE_in_statement198 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_statement211 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_statement224 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DO_in_statement244 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_statement255 = new BitSet(new long[]{0x000000020076FF08L});
    public static final BitSet FOLLOW_condition_in_statement266 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_UNTIL_in_statement285 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_statement296 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_statement307 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TRY_in_statement334 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_statement349 = new BitSet(new long[]{0x0000000000000028L});
    public static final BitSet FOLLOW_block_in_statement363 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IF_in_statement400 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_statement411 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_statement422 = new BitSet(new long[]{0x0000000000000028L});
    public static final BitSet FOLLOW_block_in_statement434 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CHOICE_in_statement466 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_statement476 = new BitSet(new long[]{0x0000000000000028L});
    public static final BitSet FOLLOW_expression_in_statement488 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_expression502 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression504 = new BitSet(new long[]{0x000000020076FF08L});
    public static final BitSet FOLLOW_expression_in_expression508 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PLUS_in_expression515 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression517 = new BitSet(new long[]{0x000000020076FF08L});
    public static final BitSet FOLLOW_expression_in_expression521 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STAR_in_expression528 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression532 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SHARP_in_expression542 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression546 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CALL_in_expression555 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENTIFIER_in_expression557 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_33_in_expression565 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_expression572 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_condition586 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_rule599 = new BitSet(new long[]{0x0000000000000002L});

}