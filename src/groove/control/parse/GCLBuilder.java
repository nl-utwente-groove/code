// $ANTLR 3.0.1 GCLBuilder.g 2008-07-03 16:07:19

package groove.control.parse;
import groove.control.*;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class GCLBuilder extends TreeParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "PROGRAM", "BLOCK", "FUNCTIONS", "FUNCTION", "CALL", "IDENTIFIER", "OR", "ALAP", "WHILE", "DO", "TRY", "IF", "ELSE", "CH_OR", "PLUS", "STAR", "SHARP", "CHOICE", "AND", "COMMA", "DOT", "NOT", "WS", "'{'", "'}'", "'('", "')'", "';'", "'true'"
    };
    public static final int FUNCTION=7;
    public static final int STAR=19;
    public static final int SHARP=20;
    public static final int FUNCTIONS=6;
    public static final int WHILE=12;
    public static final int ELSE=16;
    public static final int DO=13;
    public static final int NOT=25;
    public static final int ALAP=11;
    public static final int AND=22;
    public static final int EOF=-1;
    public static final int TRY=14;
    public static final int IF=15;
    public static final int WS=26;
    public static final int COMMA=23;
    public static final int IDENTIFIER=9;
    public static final int BLOCK=5;
    public static final int OR=10;
    public static final int CH_OR=17;
    public static final int PLUS=18;
    public static final int PROGRAM=4;
    public static final int CALL=8;
    public static final int DOT=24;
    public static final int CHOICE=21;

        public GCLBuilder(TreeNodeStream input) {
            super(input);
        }
        

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "GCLBuilder.g"; }

    
    	AutomatonBuilder builder;
        
        public void setBuilder(AutomatonBuilder ab) {
        	this.builder = ab;
        }
        
        private void proc(CommonTree proc) throws RecognitionException {
        	TreeNodeStream restore = input;
        	input = new CommonTreeNodeStream(proc.getChild(1));
        	block();
        	this.input = restore;
        }
    
        



    // $ANTLR start program
    // GCLBuilder.g:30:1: program returns [ControlShape shape=null] : ^( PROGRAM ( statement )* ) ;
    public final ControlShape program() throws RecognitionException {
        ControlShape shape = null;

         ControlState start; ControlState end; 
        try {
            // GCLBuilder.g:32:3: ( ^( PROGRAM ( statement )* ) )
            // GCLBuilder.g:32:5: ^( PROGRAM ( statement )* )
            {
            match(input,PROGRAM,FOLLOW_PROGRAM_in_program52); 

             builder.startProgram(); shape = builder.currentShape(); start = builder.getStart(); end = builder.getEnd(); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // GCLBuilder.g:34:4: ( statement )*
                loop1:
                do {
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( ((LA1_0>=CALL && LA1_0<=IF)||(LA1_0>=PLUS && LA1_0<=CHOICE)||LA1_0==32) ) {
                        alt1=1;
                    }


                    switch (alt1) {
                	case 1 :
                	    // GCLBuilder.g:34:5: statement
                	    {
                	    pushFollow(FOLLOW_statement_in_program63);
                	    statement();
                	    _fsp--;


                	    }
                	    break;

                	default :
                	    break loop1;
                    }
                } while (true);

                 builder.endProgram(); 

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
        return shape;
    }
    // $ANTLR end program


    // $ANTLR start block
    // GCLBuilder.g:38:1: block : ^( BLOCK ( statement )* ) ;
    public final void block() throws RecognitionException {
         
        		ControlState start = builder.getStart(); 
        		ControlState end = builder.getEnd(); 
        		boolean empty = true;
        		boolean first = true;
        		ControlState newState = builder.newState();
          		builder.restore(start, newState);
          		ControlState tmpStart = start;

        try {
            // GCLBuilder.g:48:1: ( ^( BLOCK ( statement )* ) )
            // GCLBuilder.g:48:4: ^( BLOCK ( statement )* )
            {
            match(input,BLOCK,FOLLOW_BLOCK_in_block87); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // GCLBuilder.g:48:12: ( statement )*
                loop2:
                do {
                    int alt2=2;
                    int LA2_0 = input.LA(1);

                    if ( ((LA2_0>=CALL && LA2_0<=IF)||(LA2_0>=PLUS && LA2_0<=CHOICE)||LA2_0==32) ) {
                        alt2=1;
                    }


                    switch (alt2) {
                	case 1 :
                	    // GCLBuilder.g:48:13: statement
                	    {
                	    pushFollow(FOLLOW_statement_in_block90);
                	    statement();
                	    _fsp--;

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
    // GCLBuilder.g:68:1: statement : ( ^( ALAP block ) | ^( WHILE condition block ) | ^( DO block condition ) | ^( TRY block ( ELSE block )? ) | ^( IF condition block ( ELSE block )? ) | ^( CHOICE ( block )+ ) | expression );
    public final void statement() throws RecognitionException {
        ControlState start = builder.getStart(); ControlState end = builder.getEnd(); ControlState newState; ControlTransition fail;
        try {
            // GCLBuilder.g:70:1: ( ^( ALAP block ) | ^( WHILE condition block ) | ^( DO block condition ) | ^( TRY block ( ELSE block )? ) | ^( IF condition block ( ELSE block )? ) | ^( CHOICE ( block )+ ) | expression )
            int alt6=7;
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
            case TRY:
                {
                alt6=4;
                }
                break;
            case IF:
                {
                alt6=5;
                }
                break;
            case CHOICE:
                {
                alt6=6;
                }
                break;
            case CALL:
            case IDENTIFIER:
            case OR:
            case PLUS:
            case STAR:
            case SHARP:
            case 32:
                {
                alt6=7;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("68:1: statement : ( ^( ALAP block ) | ^( WHILE condition block ) | ^( DO block condition ) | ^( TRY block ( ELSE block )? ) | ^( IF condition block ( ELSE block )? ) | ^( CHOICE ( block )+ ) | expression );", 6, 0, input);

                throw nvae;
            }

            switch (alt6) {
                case 1 :
                    // GCLBuilder.g:70:4: ^( ALAP block )
                    {
                    match(input,ALAP,FOLLOW_ALAP_in_statement127); 

                     fail = builder.addElse(); newState = builder.newState(); builder.restore(newState, start); builder.addLambda(); builder.restore(start, newState); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_statement141);
                    block();
                    _fsp--;

                     builder.fail(start,fail); builder.tagDelta(start); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // GCLBuilder.g:75:5: ^( WHILE condition block )
                    {
                    match(input,WHILE,FOLLOW_WHILE_in_statement161); 

                     fail = builder.addElse(); newState = builder.newState(); builder.restore(start, newState); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_condition_in_statement174);
                    condition();
                    _fsp--;

                     builder.fail(start, fail); builder.restore(newState, start);
                    pushFollow(FOLLOW_block_in_statement187);
                    block();
                    _fsp--;

                     builder.deltaInitCopy(newState, start); builder.tagDelta(start); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // GCLBuilder.g:82:5: ^( DO block condition )
                    {
                    match(input,DO,FOLLOW_DO_in_statement207); 

                     newState = builder.newState(); builder.restore(newState, end); fail = builder.addElse(); builder.restore(start, newState); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_statement218);
                    block();
                    _fsp--;

                     builder.restore(newState, start); 
                    pushFollow(FOLLOW_condition_in_statement229);
                    condition();
                    _fsp--;

                     builder.fail(newState,fail); builder.tagDelta(newState); builder.deltaInitCopy(newState, start); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // GCLBuilder.g:89:5: ^( TRY block ( ELSE block )? )
                    {
                    match(input,TRY,FOLLOW_TRY_in_statement247); 

                     builder.debug("try,enter");
                     newState = builder.newState(); builder.restore(start, newState); fail = builder.addElse(); builder.restore(start, end);

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_statement262);
                    block();
                    _fsp--;

                    builder.fail(start, fail); builder.restore(newState, end); boolean block = false; 
                    // GCLBuilder.g:93:5: ( ELSE block )?
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0==ELSE) ) {
                        alt3=1;
                    }
                    switch (alt3) {
                        case 1 :
                            // GCLBuilder.g:93:6: ELSE block
                            {
                            match(input,ELSE,FOLLOW_ELSE_in_statement276); 
                            pushFollow(FOLLOW_block_in_statement278);
                            block();
                            _fsp--;

                            block = true;

                            }
                            break;

                    }

                     if(block){builder.merge(); builder.tagDelta(start);}else{builder.initCopy(newState, start);}
                     builder.debug("try,exit");

                    match(input, Token.UP, null); 

                    }
                    break;
                case 5 :
                    // GCLBuilder.g:96:5: ^( IF condition block ( ELSE block )? )
                    {
                    match(input,IF,FOLLOW_IF_in_statement303); 

                     newState = builder.newState(); builder.restore(start, newState); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_condition_in_statement314);
                    condition();
                    _fsp--;

                    builder.restore(newState, end);
                    pushFollow(FOLLOW_block_in_statement325);
                    block();
                    _fsp--;

                      newState = builder.newState(); builder.restore(start, newState); fail = builder.addElse(); builder.fail(start,fail); builder.restore(newState,end); boolean block = false; 
                    // GCLBuilder.g:102:4: ( ELSE block )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==ELSE) ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // GCLBuilder.g:102:5: ELSE block
                            {
                            match(input,ELSE,FOLLOW_ELSE_in_statement337); 
                            pushFollow(FOLLOW_block_in_statement339);
                            block();
                            _fsp--;

                            block = true;

                            }
                            break;

                    }

                    if(block){builder.merge();}else{builder.initCopy(newState, start);}

                    match(input, Token.UP, null); 

                    }
                    break;
                case 6 :
                    // GCLBuilder.g:105:5: ^( CHOICE ( block )+ )
                    {
                    match(input,CHOICE,FOLLOW_CHOICE_in_statement363); 

                    match(input, Token.DOWN, null); 
                    // GCLBuilder.g:106:4: ( block )+
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
                    	    // GCLBuilder.g:106:6: block
                    	    {
                    	     newState = builder.newState(); builder.restore(start, newState); builder.addLambda(); builder.restore(newState, end); 
                    	    pushFollow(FOLLOW_block_in_statement373);
                    	    block();
                    	    _fsp--;

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
                case 7 :
                    // GCLBuilder.g:107:5: expression
                    {
                    pushFollow(FOLLOW_expression_in_statement385);
                    expression();
                    _fsp--;


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
    // GCLBuilder.g:109:1: expression : ( ^( OR expression expression ) | ^( PLUS expression expression ) | ^( STAR expression ) | ^( SHARP expression ) | ^( CALL IDENTIFIER ) | 'true' | rule );
    public final void expression() throws RecognitionException {
        CommonTree IDENTIFIER1=null;

         ControlState start = builder.getStart(); ControlState end = builder.getEnd(); ControlState newState; ControlTransition fail; 
        try {
            // GCLBuilder.g:111:2: ( ^( OR expression expression ) | ^( PLUS expression expression ) | ^( STAR expression ) | ^( SHARP expression ) | ^( CALL IDENTIFIER ) | 'true' | rule )
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
            case 32:
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
                    new NoViableAltException("109:1: expression : ( ^( OR expression expression ) | ^( PLUS expression expression ) | ^( STAR expression ) | ^( SHARP expression ) | ^( CALL IDENTIFIER ) | 'true' | rule );", 7, 0, input);

                throw nvae;
            }

            switch (alt7) {
                case 1 :
                    // GCLBuilder.g:111:4: ^( OR expression expression )
                    {
                    match(input,OR,FOLLOW_OR_in_expression399); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression401);
                    expression();
                    _fsp--;

                    builder.restore(start, end);
                    pushFollow(FOLLOW_expression_in_expression405);
                    expression();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // GCLBuilder.g:112:4: ^( PLUS expression expression )
                    {
                    match(input,PLUS,FOLLOW_PLUS_in_expression412); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression414);
                    expression();
                    _fsp--;

                     builder.restore(end,end); 
                    pushFollow(FOLLOW_expression_in_expression418);
                    expression();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // GCLBuilder.g:113:4: ^( STAR expression )
                    {
                    match(input,STAR,FOLLOW_STAR_in_expression425); 

                     newState = builder.newState(); builder.restore(start,newState); builder.addLambda(); builder.restore(newState,newState); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression429);
                    expression();
                    _fsp--;

                     builder.restore(newState,end); builder.addLambda(); builder.tagDelta(start);

                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // GCLBuilder.g:114:4: ^( SHARP expression )
                    {
                    match(input,SHARP,FOLLOW_SHARP_in_expression439); 

                     fail = builder.addElse(); builder.restore(start, start); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression443);
                    expression();
                    _fsp--;

                     builder.fail(start,fail); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 5 :
                    // GCLBuilder.g:115:4: ^( CALL IDENTIFIER )
                    {
                    match(input,CALL,FOLLOW_CALL_in_expression452); 

                    match(input, Token.DOWN, null); 
                    IDENTIFIER1=(CommonTree)input.LT(1);
                    match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_expression454); 

                    match(input, Token.UP, null); 
                     proc(builder.getProc(IDENTIFIER1.getText())); 

                    }
                    break;
                case 6 :
                    // GCLBuilder.g:116:4: 'true'
                    {
                    match(input,32,FOLLOW_32_in_expression462); 
                     builder.addLambda(); builder.tagDelta(start); 

                    }
                    break;
                case 7 :
                    // GCLBuilder.g:117:4: rule
                    {
                    pushFollow(FOLLOW_rule_in_expression469);
                    rule();
                    _fsp--;


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
    // GCLBuilder.g:120:1: condition : expression ;
    public final void condition() throws RecognitionException {
        try {
            // GCLBuilder.g:121:3: ( expression )
            // GCLBuilder.g:121:5: expression
            {
            pushFollow(FOLLOW_expression_in_condition483);
            expression();
            _fsp--;


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
    // GCLBuilder.g:124:1: rule : IDENTIFIER ;
    public final void rule() throws RecognitionException {
        CommonTree IDENTIFIER2=null;

        try {
            // GCLBuilder.g:125:3: ( IDENTIFIER )
            // GCLBuilder.g:125:5: IDENTIFIER
            {
            IDENTIFIER2=(CommonTree)input.LT(1);
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_rule496); 
             builder.addTransition(IDENTIFIER2.getText()); 

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


 

    public static final BitSet FOLLOW_PROGRAM_in_program52 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_statement_in_program63 = new BitSet(new long[]{0x00000001003CFF08L});
    public static final BitSet FOLLOW_BLOCK_in_block87 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_statement_in_block90 = new BitSet(new long[]{0x00000001003CFF08L});
    public static final BitSet FOLLOW_ALAP_in_statement127 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_statement141 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WHILE_in_statement161 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_statement174 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_statement187 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DO_in_statement207 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_statement218 = new BitSet(new long[]{0x00000001001C0700L});
    public static final BitSet FOLLOW_condition_in_statement229 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TRY_in_statement247 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_statement262 = new BitSet(new long[]{0x0000000000010008L});
    public static final BitSet FOLLOW_ELSE_in_statement276 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_statement278 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IF_in_statement303 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_statement314 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_statement325 = new BitSet(new long[]{0x0000000000010008L});
    public static final BitSet FOLLOW_ELSE_in_statement337 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_statement339 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CHOICE_in_statement363 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_statement373 = new BitSet(new long[]{0x0000000000000028L});
    public static final BitSet FOLLOW_expression_in_statement385 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_expression399 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression401 = new BitSet(new long[]{0x00000001001C0700L});
    public static final BitSet FOLLOW_expression_in_expression405 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PLUS_in_expression412 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression414 = new BitSet(new long[]{0x00000001001C0700L});
    public static final BitSet FOLLOW_expression_in_expression418 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STAR_in_expression425 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression429 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SHARP_in_expression439 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression443 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CALL_in_expression452 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENTIFIER_in_expression454 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_32_in_expression462 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_expression469 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_condition483 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_rule496 = new BitSet(new long[]{0x0000000000000002L});

}