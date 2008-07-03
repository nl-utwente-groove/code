// $ANTLR 3.0.1 GCLChecker.g 2008-07-03 15:49:43

package groove.control.parse;
import groove.control.*;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class GCLChecker extends TreeParser {
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

        public GCLChecker(TreeNodeStream input) {
            super(input);
        }
        

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "GCLChecker.g"; }

    
    	private ControlAutomaton aut;
    	
        private Namespace namespace;
    	public void setNamespace(Namespace namespace) {
    		this.namespace = namespace;
    	}



    // $ANTLR start program
    // GCLChecker.g:22:1: program : ^( PROGRAM ( proc )* ( statement )* ) ;
    public final void program() throws RecognitionException {
        try {
            // GCLChecker.g:23:3: ( ^( PROGRAM ( proc )* ( statement )* ) )
            // GCLChecker.g:23:6: ^( PROGRAM ( proc )* ( statement )* )
            {
            match(input,PROGRAM,FOLLOW_PROGRAM_in_program45); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // GCLChecker.g:23:16: ( proc )*
                loop1:
                do {
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( (LA1_0==FUNCTION) ) {
                        alt1=1;
                    }


                    switch (alt1) {
                	case 1 :
                	    // GCLChecker.g:23:16: proc
                	    {
                	    pushFollow(FOLLOW_proc_in_program47);
                	    proc();
                	    _fsp--;


                	    }
                	    break;

                	default :
                	    break loop1;
                    }
                } while (true);

                // GCLChecker.g:23:22: ( statement )*
                loop2:
                do {
                    int alt2=2;
                    int LA2_0 = input.LA(1);

                    if ( ((LA2_0>=CALL && LA2_0<=IF)||(LA2_0>=PLUS && LA2_0<=CHOICE)) ) {
                        alt2=1;
                    }


                    switch (alt2) {
                	case 1 :
                	    // GCLChecker.g:23:22: statement
                	    {
                	    pushFollow(FOLLOW_statement_in_program50);
                	    statement();
                	    _fsp--;


                	    }
                	    break;

                	default :
                	    break loop2;
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
    // $ANTLR end program


    // $ANTLR start proc
    // GCLChecker.g:26:1: proc : ^( FUNCTION IDENTIFIER block ) ;
    public final void proc() throws RecognitionException {
        CommonTree IDENTIFIER1=null;
        CommonTree FUNCTION2=null;

        try {
            // GCLChecker.g:27:3: ( ^( FUNCTION IDENTIFIER block ) )
            // GCLChecker.g:28:3: ^( FUNCTION IDENTIFIER block )
            {
            FUNCTION2=(CommonTree)input.LT(1);
            match(input,FUNCTION,FOLLOW_FUNCTION_in_proc70); 

            match(input, Token.DOWN, null); 
            IDENTIFIER1=(CommonTree)input.LT(1);
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_proc72); 
            pushFollow(FOLLOW_block_in_proc74);
            block();
            _fsp--;


            match(input, Token.UP, null); 
             namespace.store( IDENTIFIER1.getText() , FUNCTION2); 

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
    // $ANTLR end proc


    // $ANTLR start block
    // GCLChecker.g:31:1: block : ^( BLOCK ( statement )* ) ;
    public final void block() throws RecognitionException {
        try {
            // GCLChecker.g:32:3: ( ^( BLOCK ( statement )* ) )
            // GCLChecker.g:32:5: ^( BLOCK ( statement )* )
            {
            match(input,BLOCK,FOLLOW_BLOCK_in_block94); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // GCLChecker.g:32:13: ( statement )*
                loop3:
                do {
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( ((LA3_0>=CALL && LA3_0<=IF)||(LA3_0>=PLUS && LA3_0<=CHOICE)) ) {
                        alt3=1;
                    }


                    switch (alt3) {
                	case 1 :
                	    // GCLChecker.g:32:14: statement
                	    {
                	    pushFollow(FOLLOW_statement_in_block97);
                	    statement();
                	    _fsp--;


                	    }
                	    break;

                	default :
                	    break loop3;
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
    // $ANTLR end block


    // $ANTLR start statement
    // GCLChecker.g:35:1: statement : ( ^( ALAP block ) | ^( WHILE condition DO block ) | ^( DO block WHILE condition ) | ^( TRY block ( block )? ) | ^( IF condition block ( ELSE block )? ) | ^( CHOICE block ( OR block )* ) | expression );
    public final void statement() throws RecognitionException {
        try {
            // GCLChecker.g:36:3: ( ^( ALAP block ) | ^( WHILE condition DO block ) | ^( DO block WHILE condition ) | ^( TRY block ( block )? ) | ^( IF condition block ( ELSE block )? ) | ^( CHOICE block ( OR block )* ) | expression )
            int alt7=7;
            switch ( input.LA(1) ) {
            case ALAP:
                {
                alt7=1;
                }
                break;
            case WHILE:
                {
                alt7=2;
                }
                break;
            case DO:
                {
                alt7=3;
                }
                break;
            case TRY:
                {
                alt7=4;
                }
                break;
            case IF:
                {
                alt7=5;
                }
                break;
            case CHOICE:
                {
                alt7=6;
                }
                break;
            case CALL:
            case IDENTIFIER:
            case OR:
            case PLUS:
            case STAR:
            case SHARP:
                {
                alt7=7;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("35:1: statement : ( ^( ALAP block ) | ^( WHILE condition DO block ) | ^( DO block WHILE condition ) | ^( TRY block ( block )? ) | ^( IF condition block ( ELSE block )? ) | ^( CHOICE block ( OR block )* ) | expression );", 7, 0, input);

                throw nvae;
            }

            switch (alt7) {
                case 1 :
                    // GCLChecker.g:36:5: ^( ALAP block )
                    {
                    match(input,ALAP,FOLLOW_ALAP_in_statement114); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_statement116);
                    block();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // GCLChecker.g:37:5: ^( WHILE condition DO block )
                    {
                    match(input,WHILE,FOLLOW_WHILE_in_statement124); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_condition_in_statement126);
                    condition();
                    _fsp--;

                    match(input,DO,FOLLOW_DO_in_statement128); 
                    pushFollow(FOLLOW_block_in_statement130);
                    block();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // GCLChecker.g:38:5: ^( DO block WHILE condition )
                    {
                    match(input,DO,FOLLOW_DO_in_statement138); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_statement140);
                    block();
                    _fsp--;

                    match(input,WHILE,FOLLOW_WHILE_in_statement142); 
                    pushFollow(FOLLOW_condition_in_statement144);
                    condition();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // GCLChecker.g:39:5: ^( TRY block ( block )? )
                    {
                    match(input,TRY,FOLLOW_TRY_in_statement152); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_statement154);
                    block();
                    _fsp--;

                    // GCLChecker.g:39:17: ( block )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==BLOCK) ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // GCLChecker.g:39:18: block
                            {
                            pushFollow(FOLLOW_block_in_statement157);
                            block();
                            _fsp--;


                            }
                            break;

                    }


                    match(input, Token.UP, null); 

                    }
                    break;
                case 5 :
                    // GCLChecker.g:40:5: ^( IF condition block ( ELSE block )? )
                    {
                    match(input,IF,FOLLOW_IF_in_statement167); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_condition_in_statement169);
                    condition();
                    _fsp--;

                    pushFollow(FOLLOW_block_in_statement171);
                    block();
                    _fsp--;

                    // GCLChecker.g:40:26: ( ELSE block )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0==ELSE) ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // GCLChecker.g:40:27: ELSE block
                            {
                            match(input,ELSE,FOLLOW_ELSE_in_statement174); 
                            pushFollow(FOLLOW_block_in_statement176);
                            block();
                            _fsp--;


                            }
                            break;

                    }


                    match(input, Token.UP, null); 

                    }
                    break;
                case 6 :
                    // GCLChecker.g:41:5: ^( CHOICE block ( OR block )* )
                    {
                    match(input,CHOICE,FOLLOW_CHOICE_in_statement186); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_statement188);
                    block();
                    _fsp--;

                    // GCLChecker.g:41:20: ( OR block )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==OR) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // GCLChecker.g:41:21: OR block
                    	    {
                    	    match(input,OR,FOLLOW_OR_in_statement191); 
                    	    pushFollow(FOLLOW_block_in_statement193);
                    	    block();
                    	    _fsp--;


                    	    }
                    	    break;

                    	default :
                    	    break loop6;
                        }
                    } while (true);


                    match(input, Token.UP, null); 

                    }
                    break;
                case 7 :
                    // GCLChecker.g:42:5: expression
                    {
                    pushFollow(FOLLOW_expression_in_statement204);
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
    // GCLChecker.g:45:1: expression : ( ^( OR expression expression ) | ^( PLUS expression ) | ^( STAR expression ) | ^( SHARP expression ) | ^( CALL IDENTIFIER ) | rule );
    public final void expression() throws RecognitionException {
        try {
            // GCLChecker.g:46:2: ( ^( OR expression expression ) | ^( PLUS expression ) | ^( STAR expression ) | ^( SHARP expression ) | ^( CALL IDENTIFIER ) | rule )
            int alt8=6;
            switch ( input.LA(1) ) {
            case OR:
                {
                alt8=1;
                }
                break;
            case PLUS:
                {
                alt8=2;
                }
                break;
            case STAR:
                {
                alt8=3;
                }
                break;
            case SHARP:
                {
                alt8=4;
                }
                break;
            case CALL:
                {
                alt8=5;
                }
                break;
            case IDENTIFIER:
                {
                alt8=6;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("45:1: expression : ( ^( OR expression expression ) | ^( PLUS expression ) | ^( STAR expression ) | ^( SHARP expression ) | ^( CALL IDENTIFIER ) | rule );", 8, 0, input);

                throw nvae;
            }

            switch (alt8) {
                case 1 :
                    // GCLChecker.g:46:4: ^( OR expression expression )
                    {
                    match(input,OR,FOLLOW_OR_in_expression218); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression220);
                    expression();
                    _fsp--;

                    pushFollow(FOLLOW_expression_in_expression222);
                    expression();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // GCLChecker.g:47:4: ^( PLUS expression )
                    {
                    match(input,PLUS,FOLLOW_PLUS_in_expression229); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression231);
                    expression();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // GCLChecker.g:48:4: ^( STAR expression )
                    {
                    match(input,STAR,FOLLOW_STAR_in_expression238); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression240);
                    expression();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // GCLChecker.g:49:4: ^( SHARP expression )
                    {
                    match(input,SHARP,FOLLOW_SHARP_in_expression247); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression249);
                    expression();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 5 :
                    // GCLChecker.g:50:4: ^( CALL IDENTIFIER )
                    {
                    match(input,CALL,FOLLOW_CALL_in_expression256); 

                    match(input, Token.DOWN, null); 
                    match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_expression258); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 6 :
                    // GCLChecker.g:51:4: rule
                    {
                    pushFollow(FOLLOW_rule_in_expression264);
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
    // GCLChecker.g:54:1: condition : ( ^( OR condition condition ) | rule | 'true' );
    public final void condition() throws RecognitionException {
        try {
            // GCLChecker.g:55:3: ( ^( OR condition condition ) | rule | 'true' )
            int alt9=3;
            switch ( input.LA(1) ) {
            case OR:
                {
                alt9=1;
                }
                break;
            case IDENTIFIER:
                {
                alt9=2;
                }
                break;
            case 32:
                {
                alt9=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("54:1: condition : ( ^( OR condition condition ) | rule | 'true' );", 9, 0, input);

                throw nvae;
            }

            switch (alt9) {
                case 1 :
                    // GCLChecker.g:55:5: ^( OR condition condition )
                    {
                    match(input,OR,FOLLOW_OR_in_condition278); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_condition_in_condition280);
                    condition();
                    _fsp--;

                    pushFollow(FOLLOW_condition_in_condition282);
                    condition();
                    _fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // GCLChecker.g:56:5: rule
                    {
                    pushFollow(FOLLOW_rule_in_condition289);
                    rule();
                    _fsp--;


                    }
                    break;
                case 3 :
                    // GCLChecker.g:57:5: 'true'
                    {
                    match(input,32,FOLLOW_32_in_condition295); 

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
    // $ANTLR end condition


    // $ANTLR start rule
    // GCLChecker.g:60:1: rule : IDENTIFIER ;
    public final void rule() throws RecognitionException {
        try {
            // GCLChecker.g:61:3: ( IDENTIFIER )
            // GCLChecker.g:61:5: IDENTIFIER
            {
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_rule308); 

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


 

    public static final BitSet FOLLOW_PROGRAM_in_program45 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_proc_in_program47 = new BitSet(new long[]{0x00000000003CFF88L});
    public static final BitSet FOLLOW_statement_in_program50 = new BitSet(new long[]{0x00000000003CFF08L});
    public static final BitSet FOLLOW_FUNCTION_in_proc70 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENTIFIER_in_proc72 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_proc74 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BLOCK_in_block94 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_statement_in_block97 = new BitSet(new long[]{0x00000000003CFF08L});
    public static final BitSet FOLLOW_ALAP_in_statement114 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_statement116 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WHILE_in_statement124 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_statement126 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_DO_in_statement128 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_statement130 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DO_in_statement138 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_statement140 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_WHILE_in_statement142 = new BitSet(new long[]{0x0000000100000600L});
    public static final BitSet FOLLOW_condition_in_statement144 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TRY_in_statement152 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_statement154 = new BitSet(new long[]{0x0000000000000028L});
    public static final BitSet FOLLOW_block_in_statement157 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IF_in_statement167 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_statement169 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_statement171 = new BitSet(new long[]{0x0000000000010008L});
    public static final BitSet FOLLOW_ELSE_in_statement174 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_statement176 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CHOICE_in_statement186 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_statement188 = new BitSet(new long[]{0x0000000000000408L});
    public static final BitSet FOLLOW_OR_in_statement191 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_statement193 = new BitSet(new long[]{0x0000000000000408L});
    public static final BitSet FOLLOW_expression_in_statement204 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_expression218 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression220 = new BitSet(new long[]{0x00000000001C0700L});
    public static final BitSet FOLLOW_expression_in_expression222 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PLUS_in_expression229 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression231 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STAR_in_expression238 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression240 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SHARP_in_expression247 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression249 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CALL_in_expression256 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENTIFIER_in_expression258 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_rule_in_expression264 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_condition278 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_condition280 = new BitSet(new long[]{0x0000000100000600L});
    public static final BitSet FOLLOW_condition_in_condition282 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_rule_in_condition289 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_32_in_condition295 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_rule308 = new BitSet(new long[]{0x0000000000000002L});

}