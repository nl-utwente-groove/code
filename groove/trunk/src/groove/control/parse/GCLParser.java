// $ANTLR 3.0.1 GCL.g 2008-07-03 15:30:35

package groove.control.parse;
import groove.control.*;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


import org.antlr.runtime.tree.*;

public class GCLParser extends Parser {
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
    public static final int PROGRAM=4;
    public static final int PLUS=18;
    public static final int CALL=8;
    public static final int DOT=24;
    public static final int CHOICE=21;

        public GCLParser(TokenStream input) {
            super(input);
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "GCL.g"; }


    public static class program_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start program
    // GCL.g:29:1: program : ( procdef | statement )* -> ^( PROGRAM ( procdef )* ( statement )* ) ;
    public final program_return program() throws RecognitionException {
        program_return retval = new program_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        procdef_return procdef1 = null;

        statement_return statement2 = null;


        RewriteRuleSubtreeStream stream_statement=new RewriteRuleSubtreeStream(adaptor,"rule statement");
        RewriteRuleSubtreeStream stream_procdef=new RewriteRuleSubtreeStream(adaptor,"rule procdef");
        try {
            // GCL.g:29:9: ( ( procdef | statement )* -> ^( PROGRAM ( procdef )* ( statement )* ) )
            // GCL.g:29:11: ( procdef | statement )*
            {
            // GCL.g:29:11: ( procdef | statement )*
            loop1:
            do {
                int alt1=3;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==FUNCTION) ) {
                    alt1=1;
                }
                else if ( (LA1_0==IDENTIFIER||(LA1_0>=ALAP && LA1_0<=IF)||(LA1_0>=SHARP && LA1_0<=CHOICE)||LA1_0==29) ) {
                    alt1=2;
                }


                switch (alt1) {
            	case 1 :
            	    // GCL.g:29:12: procdef
            	    {
            	    pushFollow(FOLLOW_procdef_in_program71);
            	    procdef1=procdef();
            	    _fsp--;

            	    stream_procdef.add(procdef1.getTree());

            	    }
            	    break;
            	case 2 :
            	    // GCL.g:29:20: statement
            	    {
            	    pushFollow(FOLLOW_statement_in_program73);
            	    statement2=statement();
            	    _fsp--;

            	    stream_statement.add(statement2.getTree());

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            // AST REWRITE
            // elements: statement, procdef
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 29:32: -> ^( PROGRAM ( procdef )* ( statement )* )
            {
                // GCL.g:29:35: ^( PROGRAM ( procdef )* ( statement )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(adaptor.create(PROGRAM, "PROGRAM"), root_1);

                // GCL.g:29:45: ( procdef )*
                while ( stream_procdef.hasNext() ) {
                    adaptor.addChild(root_1, stream_procdef.next());

                }
                stream_procdef.reset();
                // GCL.g:29:54: ( statement )*
                while ( stream_statement.hasNext() ) {
                    adaptor.addChild(root_1, stream_statement.next());

                }
                stream_statement.reset();

                adaptor.addChild(root_0, root_1);
                }

            }



            }

            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

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

    public static class block_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start block
    // GCL.g:31:1: block : '{' ( statement )* '}' ;
    public final block_return block() throws RecognitionException {
        block_return retval = new block_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal3=null;
        Token char_literal5=null;
        statement_return statement4 = null;


        Object char_literal3_tree=null;
        Object char_literal5_tree=null;

        try {
            // GCL.g:31:7: ( '{' ( statement )* '}' )
            // GCL.g:31:9: '{' ( statement )* '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal3=(Token)input.LT(1);
            match(input,27,FOLLOW_27_in_block96); 
            // GCL.g:31:14: ( statement )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==IDENTIFIER||(LA2_0>=ALAP && LA2_0<=IF)||(LA2_0>=SHARP && LA2_0<=CHOICE)||LA2_0==29) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // GCL.g:31:14: statement
            	    {
            	    pushFollow(FOLLOW_statement_in_block99);
            	    statement4=statement();
            	    _fsp--;

            	    adaptor.addChild(root_0, statement4.getTree());

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            char_literal5=(Token)input.LT(1);
            match(input,28,FOLLOW_28_in_block103); 

            }

            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

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

    public static class procdef_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start procdef
    // GCL.g:33:1: procdef : FUNCTION IDENTIFIER '(' ')' block ;
    public final procdef_return procdef() throws RecognitionException {
        procdef_return retval = new procdef_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token FUNCTION6=null;
        Token IDENTIFIER7=null;
        Token char_literal8=null;
        Token char_literal9=null;
        block_return block10 = null;


        Object FUNCTION6_tree=null;
        Object IDENTIFIER7_tree=null;
        Object char_literal8_tree=null;
        Object char_literal9_tree=null;

        try {
            // GCL.g:33:9: ( FUNCTION IDENTIFIER '(' ')' block )
            // GCL.g:33:11: FUNCTION IDENTIFIER '(' ')' block
            {
            root_0 = (Object)adaptor.nil();

            FUNCTION6=(Token)input.LT(1);
            match(input,FUNCTION,FOLLOW_FUNCTION_in_procdef112); 
            FUNCTION6_tree = (Object)adaptor.create(FUNCTION6);
            adaptor.addChild(root_0, FUNCTION6_tree);

            IDENTIFIER7=(Token)input.LT(1);
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_procdef114); 
            IDENTIFIER7_tree = (Object)adaptor.create(IDENTIFIER7);
            adaptor.addChild(root_0, IDENTIFIER7_tree);

            char_literal8=(Token)input.LT(1);
            match(input,29,FOLLOW_29_in_procdef116); 
            char_literal9=(Token)input.LT(1);
            match(input,30,FOLLOW_30_in_procdef119); 
            pushFollow(FOLLOW_block_in_procdef122);
            block10=block();
            _fsp--;

            adaptor.addChild(root_0, block10.getTree());

            }

            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end procdef

    public static class condition_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start condition
    // GCL.g:35:1: condition : conditionliteral ( OR condition )? ;
    public final condition_return condition() throws RecognitionException {
        condition_return retval = new condition_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token OR12=null;
        conditionliteral_return conditionliteral11 = null;

        condition_return condition13 = null;


        Object OR12_tree=null;

        try {
            // GCL.g:36:2: ( conditionliteral ( OR condition )? )
            // GCL.g:36:4: conditionliteral ( OR condition )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_conditionliteral_in_condition131);
            conditionliteral11=conditionliteral();
            _fsp--;

            adaptor.addChild(root_0, conditionliteral11.getTree());
            // GCL.g:36:21: ( OR condition )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==OR) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // GCL.g:36:22: OR condition
                    {
                    OR12=(Token)input.LT(1);
                    match(input,OR,FOLLOW_OR_in_condition134); 
                    OR12_tree = (Object)adaptor.create(OR12);
                    root_0 = (Object)adaptor.becomeRoot(OR12_tree, root_0);

                    pushFollow(FOLLOW_condition_in_condition137);
                    condition13=condition();
                    _fsp--;

                    adaptor.addChild(root_0, condition13.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

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

    public static class statement_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start statement
    // GCL.g:38:1: statement : ( ALAP block | WHILE '(' condition ')' DO block | DO block WHILE '(' condition ')' | TRY block ( 'else' block )? | IF '(' condition ')' block ( ELSE block )? | 'choice' block ( CH_OR block )* | expression ';' );
    public final statement_return statement() throws RecognitionException {
        statement_return retval = new statement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ALAP14=null;
        Token WHILE16=null;
        Token char_literal17=null;
        Token char_literal19=null;
        Token DO20=null;
        Token DO22=null;
        Token WHILE24=null;
        Token char_literal25=null;
        Token char_literal27=null;
        Token TRY28=null;
        Token string_literal30=null;
        Token IF32=null;
        Token char_literal33=null;
        Token char_literal35=null;
        Token ELSE37=null;
        Token string_literal39=null;
        Token CH_OR41=null;
        Token char_literal44=null;
        block_return block15 = null;

        condition_return condition18 = null;

        block_return block21 = null;

        block_return block23 = null;

        condition_return condition26 = null;

        block_return block29 = null;

        block_return block31 = null;

        condition_return condition34 = null;

        block_return block36 = null;

        block_return block38 = null;

        block_return block40 = null;

        block_return block42 = null;

        expression_return expression43 = null;


        Object ALAP14_tree=null;
        Object WHILE16_tree=null;
        Object char_literal17_tree=null;
        Object char_literal19_tree=null;
        Object DO20_tree=null;
        Object DO22_tree=null;
        Object WHILE24_tree=null;
        Object char_literal25_tree=null;
        Object char_literal27_tree=null;
        Object TRY28_tree=null;
        Object string_literal30_tree=null;
        Object IF32_tree=null;
        Object char_literal33_tree=null;
        Object char_literal35_tree=null;
        Object ELSE37_tree=null;
        Object string_literal39_tree=null;
        Object CH_OR41_tree=null;
        Object char_literal44_tree=null;

        try {
            // GCL.g:39:2: ( ALAP block | WHILE '(' condition ')' DO block | DO block WHILE '(' condition ')' | TRY block ( 'else' block )? | IF '(' condition ')' block ( ELSE block )? | 'choice' block ( CH_OR block )* | expression ';' )
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
            case IDENTIFIER:
            case SHARP:
            case 29:
                {
                alt7=7;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("38:1: statement : ( ALAP block | WHILE '(' condition ')' DO block | DO block WHILE '(' condition ')' | TRY block ( 'else' block )? | IF '(' condition ')' block ( ELSE block )? | 'choice' block ( CH_OR block )* | expression ';' );", 7, 0, input);

                throw nvae;
            }

            switch (alt7) {
                case 1 :
                    // GCL.g:39:4: ALAP block
                    {
                    root_0 = (Object)adaptor.nil();

                    ALAP14=(Token)input.LT(1);
                    match(input,ALAP,FOLLOW_ALAP_in_statement150); 
                    ALAP14_tree = (Object)adaptor.create(ALAP14);
                    adaptor.addChild(root_0, ALAP14_tree);

                    pushFollow(FOLLOW_block_in_statement152);
                    block15=block();
                    _fsp--;

                    adaptor.addChild(root_0, block15.getTree());

                    }
                    break;
                case 2 :
                    // GCL.g:40:4: WHILE '(' condition ')' DO block
                    {
                    root_0 = (Object)adaptor.nil();

                    WHILE16=(Token)input.LT(1);
                    match(input,WHILE,FOLLOW_WHILE_in_statement157); 
                    WHILE16_tree = (Object)adaptor.create(WHILE16);
                    adaptor.addChild(root_0, WHILE16_tree);

                    char_literal17=(Token)input.LT(1);
                    match(input,29,FOLLOW_29_in_statement159); 
                    pushFollow(FOLLOW_condition_in_statement162);
                    condition18=condition();
                    _fsp--;

                    adaptor.addChild(root_0, condition18.getTree());
                    char_literal19=(Token)input.LT(1);
                    match(input,30,FOLLOW_30_in_statement164); 
                    DO20=(Token)input.LT(1);
                    match(input,DO,FOLLOW_DO_in_statement167); 
                    DO20_tree = (Object)adaptor.create(DO20);
                    adaptor.addChild(root_0, DO20_tree);

                    pushFollow(FOLLOW_block_in_statement169);
                    block21=block();
                    _fsp--;

                    adaptor.addChild(root_0, block21.getTree());

                    }
                    break;
                case 3 :
                    // GCL.g:41:4: DO block WHILE '(' condition ')'
                    {
                    root_0 = (Object)adaptor.nil();

                    DO22=(Token)input.LT(1);
                    match(input,DO,FOLLOW_DO_in_statement174); 
                    DO22_tree = (Object)adaptor.create(DO22);
                    adaptor.addChild(root_0, DO22_tree);

                    pushFollow(FOLLOW_block_in_statement176);
                    block23=block();
                    _fsp--;

                    adaptor.addChild(root_0, block23.getTree());
                    WHILE24=(Token)input.LT(1);
                    match(input,WHILE,FOLLOW_WHILE_in_statement178); 
                    WHILE24_tree = (Object)adaptor.create(WHILE24);
                    adaptor.addChild(root_0, WHILE24_tree);

                    char_literal25=(Token)input.LT(1);
                    match(input,29,FOLLOW_29_in_statement180); 
                    pushFollow(FOLLOW_condition_in_statement183);
                    condition26=condition();
                    _fsp--;

                    adaptor.addChild(root_0, condition26.getTree());
                    char_literal27=(Token)input.LT(1);
                    match(input,30,FOLLOW_30_in_statement185); 

                    }
                    break;
                case 4 :
                    // GCL.g:42:4: TRY block ( 'else' block )?
                    {
                    root_0 = (Object)adaptor.nil();

                    TRY28=(Token)input.LT(1);
                    match(input,TRY,FOLLOW_TRY_in_statement192); 
                    TRY28_tree = (Object)adaptor.create(TRY28);
                    adaptor.addChild(root_0, TRY28_tree);

                    pushFollow(FOLLOW_block_in_statement194);
                    block29=block();
                    _fsp--;

                    adaptor.addChild(root_0, block29.getTree());
                    // GCL.g:42:14: ( 'else' block )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==ELSE) ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // GCL.g:42:15: 'else' block
                            {
                            string_literal30=(Token)input.LT(1);
                            match(input,ELSE,FOLLOW_ELSE_in_statement197); 
                            string_literal30_tree = (Object)adaptor.create(string_literal30);
                            adaptor.addChild(root_0, string_literal30_tree);

                            pushFollow(FOLLOW_block_in_statement199);
                            block31=block();
                            _fsp--;

                            adaptor.addChild(root_0, block31.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 5 :
                    // GCL.g:43:4: IF '(' condition ')' block ( ELSE block )?
                    {
                    root_0 = (Object)adaptor.nil();

                    IF32=(Token)input.LT(1);
                    match(input,IF,FOLLOW_IF_in_statement206); 
                    IF32_tree = (Object)adaptor.create(IF32);
                    adaptor.addChild(root_0, IF32_tree);

                    char_literal33=(Token)input.LT(1);
                    match(input,29,FOLLOW_29_in_statement208); 
                    pushFollow(FOLLOW_condition_in_statement211);
                    condition34=condition();
                    _fsp--;

                    adaptor.addChild(root_0, condition34.getTree());
                    char_literal35=(Token)input.LT(1);
                    match(input,30,FOLLOW_30_in_statement213); 
                    pushFollow(FOLLOW_block_in_statement216);
                    block36=block();
                    _fsp--;

                    adaptor.addChild(root_0, block36.getTree());
                    // GCL.g:43:33: ( ELSE block )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0==ELSE) ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // GCL.g:43:34: ELSE block
                            {
                            ELSE37=(Token)input.LT(1);
                            match(input,ELSE,FOLLOW_ELSE_in_statement219); 
                            ELSE37_tree = (Object)adaptor.create(ELSE37);
                            adaptor.addChild(root_0, ELSE37_tree);

                            pushFollow(FOLLOW_block_in_statement221);
                            block38=block();
                            _fsp--;

                            adaptor.addChild(root_0, block38.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 6 :
                    // GCL.g:44:8: 'choice' block ( CH_OR block )*
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal39=(Token)input.LT(1);
                    match(input,CHOICE,FOLLOW_CHOICE_in_statement232); 
                    string_literal39_tree = (Object)adaptor.create(string_literal39);
                    adaptor.addChild(root_0, string_literal39_tree);

                    pushFollow(FOLLOW_block_in_statement234);
                    block40=block();
                    _fsp--;

                    adaptor.addChild(root_0, block40.getTree());
                    // GCL.g:44:23: ( CH_OR block )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==CH_OR) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // GCL.g:44:24: CH_OR block
                    	    {
                    	    CH_OR41=(Token)input.LT(1);
                    	    match(input,CH_OR,FOLLOW_CH_OR_in_statement237); 
                    	    pushFollow(FOLLOW_block_in_statement240);
                    	    block42=block();
                    	    _fsp--;

                    	    adaptor.addChild(root_0, block42.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop6;
                        }
                    } while (true);


                    }
                    break;
                case 7 :
                    // GCL.g:45:4: expression ';'
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_expression_in_statement247);
                    expression43=expression();
                    _fsp--;

                    adaptor.addChild(root_0, expression43.getTree());
                    char_literal44=(Token)input.LT(1);
                    match(input,31,FOLLOW_31_in_statement249); 

                    }
                    break;

            }
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

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

    public static class conditionliteral_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start conditionliteral
    // GCL.g:49:1: conditionliteral : ( 'true' | rule );
    public final conditionliteral_return conditionliteral() throws RecognitionException {
        conditionliteral_return retval = new conditionliteral_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal45=null;
        rule_return rule46 = null;


        Object string_literal45_tree=null;

        try {
            // GCL.g:50:2: ( 'true' | rule )
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==32) ) {
                alt8=1;
            }
            else if ( (LA8_0==IDENTIFIER) ) {
                alt8=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("49:1: conditionliteral : ( 'true' | rule );", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // GCL.g:50:4: 'true'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal45=(Token)input.LT(1);
                    match(input,32,FOLLOW_32_in_conditionliteral265); 
                    string_literal45_tree = (Object)adaptor.create(string_literal45);
                    adaptor.addChild(root_0, string_literal45_tree);


                    }
                    break;
                case 2 :
                    // GCL.g:50:13: rule
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_rule_in_conditionliteral269);
                    rule46=rule();
                    _fsp--;

                    adaptor.addChild(root_0, rule46.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end conditionliteral

    public static class expression_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start expression
    // GCL.g:52:1: expression : ( expression_atom ( ( OR expression ) | PLUS | STAR )? | SHARP expression_atom );
    public final expression_return expression() throws RecognitionException {
        expression_return retval = new expression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token OR48=null;
        Token PLUS50=null;
        Token STAR51=null;
        Token SHARP52=null;
        expression_atom_return expression_atom47 = null;

        expression_return expression49 = null;

        expression_atom_return expression_atom53 = null;


        Object OR48_tree=null;
        Object PLUS50_tree=null;
        Object STAR51_tree=null;
        Object SHARP52_tree=null;

        try {
            // GCL.g:53:2: ( expression_atom ( ( OR expression ) | PLUS | STAR )? | SHARP expression_atom )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==IDENTIFIER||LA10_0==29) ) {
                alt10=1;
            }
            else if ( (LA10_0==SHARP) ) {
                alt10=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("52:1: expression : ( expression_atom ( ( OR expression ) | PLUS | STAR )? | SHARP expression_atom );", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // GCL.g:53:4: expression_atom ( ( OR expression ) | PLUS | STAR )?
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_expression_atom_in_expression280);
                    expression_atom47=expression_atom();
                    _fsp--;

                    adaptor.addChild(root_0, expression_atom47.getTree());
                    // GCL.g:53:20: ( ( OR expression ) | PLUS | STAR )?
                    int alt9=4;
                    switch ( input.LA(1) ) {
                        case OR:
                            {
                            alt9=1;
                            }
                            break;
                        case PLUS:
                            {
                            alt9=2;
                            }
                            break;
                        case STAR:
                            {
                            alt9=3;
                            }
                            break;
                    }

                    switch (alt9) {
                        case 1 :
                            // GCL.g:53:22: ( OR expression )
                            {
                            // GCL.g:53:22: ( OR expression )
                            // GCL.g:53:23: OR expression
                            {
                            OR48=(Token)input.LT(1);
                            match(input,OR,FOLLOW_OR_in_expression285); 
                            OR48_tree = (Object)adaptor.create(OR48);
                            root_0 = (Object)adaptor.becomeRoot(OR48_tree, root_0);

                            pushFollow(FOLLOW_expression_in_expression288);
                            expression49=expression();
                            _fsp--;

                            adaptor.addChild(root_0, expression49.getTree());

                            }


                            }
                            break;
                        case 2 :
                            // GCL.g:53:41: PLUS
                            {
                            PLUS50=(Token)input.LT(1);
                            match(input,PLUS,FOLLOW_PLUS_in_expression293); 
                            PLUS50_tree = (Object)adaptor.create(PLUS50);
                            root_0 = (Object)adaptor.becomeRoot(PLUS50_tree, root_0);


                            }
                            break;
                        case 3 :
                            // GCL.g:53:49: STAR
                            {
                            STAR51=(Token)input.LT(1);
                            match(input,STAR,FOLLOW_STAR_in_expression298); 
                            STAR51_tree = (Object)adaptor.create(STAR51);
                            root_0 = (Object)adaptor.becomeRoot(STAR51_tree, root_0);


                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // GCL.g:54:4: SHARP expression_atom
                    {
                    root_0 = (Object)adaptor.nil();

                    SHARP52=(Token)input.LT(1);
                    match(input,SHARP,FOLLOW_SHARP_in_expression306); 
                    SHARP52_tree = (Object)adaptor.create(SHARP52);
                    adaptor.addChild(root_0, SHARP52_tree);

                    pushFollow(FOLLOW_expression_atom_in_expression308);
                    expression_atom53=expression_atom();
                    _fsp--;

                    adaptor.addChild(root_0, expression_atom53.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

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

    public static class expression_atom_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start expression_atom
    // GCL.g:57:1: expression_atom : ( rule | '(' expression ')' | procuse );
    public final expression_atom_return expression_atom() throws RecognitionException {
        expression_atom_return retval = new expression_atom_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal55=null;
        Token char_literal57=null;
        rule_return rule54 = null;

        expression_return expression56 = null;

        procuse_return procuse58 = null;


        Object char_literal55_tree=null;
        Object char_literal57_tree=null;

        try {
            // GCL.g:58:2: ( rule | '(' expression ')' | procuse )
            int alt11=3;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==IDENTIFIER) ) {
                int LA11_1 = input.LA(2);

                if ( (LA11_1==29) ) {
                    alt11=3;
                }
                else if ( (LA11_1==OR||(LA11_1>=PLUS && LA11_1<=STAR)||(LA11_1>=30 && LA11_1<=31)) ) {
                    alt11=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("57:1: expression_atom : ( rule | '(' expression ')' | procuse );", 11, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA11_0==29) ) {
                alt11=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("57:1: expression_atom : ( rule | '(' expression ')' | procuse );", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // GCL.g:58:4: rule
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_rule_in_expression_atom319);
                    rule54=rule();
                    _fsp--;

                    adaptor.addChild(root_0, rule54.getTree());

                    }
                    break;
                case 2 :
                    // GCL.g:59:4: '(' expression ')'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal55=(Token)input.LT(1);
                    match(input,29,FOLLOW_29_in_expression_atom324); 
                    pushFollow(FOLLOW_expression_in_expression_atom327);
                    expression56=expression();
                    _fsp--;

                    adaptor.addChild(root_0, expression56.getTree());
                    char_literal57=(Token)input.LT(1);
                    match(input,30,FOLLOW_30_in_expression_atom329); 

                    }
                    break;
                case 3 :
                    // GCL.g:60:4: procuse
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_procuse_in_expression_atom335);
                    procuse58=procuse();
                    _fsp--;

                    adaptor.addChild(root_0, procuse58.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end expression_atom

    public static class procuse_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start procuse
    // GCL.g:63:1: procuse : IDENTIFIER '(' ')' ;
    public final procuse_return procuse() throws RecognitionException {
        procuse_return retval = new procuse_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token IDENTIFIER59=null;
        Token char_literal60=null;
        Token char_literal61=null;

        Object IDENTIFIER59_tree=null;
        Object char_literal60_tree=null;
        Object char_literal61_tree=null;

        try {
            // GCL.g:64:2: ( IDENTIFIER '(' ')' )
            // GCL.g:64:4: IDENTIFIER '(' ')'
            {
            root_0 = (Object)adaptor.nil();

            IDENTIFIER59=(Token)input.LT(1);
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_procuse347); 
            IDENTIFIER59_tree = (Object)adaptor.create(IDENTIFIER59);
            adaptor.addChild(root_0, IDENTIFIER59_tree);

            char_literal60=(Token)input.LT(1);
            match(input,29,FOLLOW_29_in_procuse349); 
            char_literal60_tree = (Object)adaptor.create(char_literal60);
            adaptor.addChild(root_0, char_literal60_tree);

            char_literal61=(Token)input.LT(1);
            match(input,30,FOLLOW_30_in_procuse351); 
            char_literal61_tree = (Object)adaptor.create(char_literal61);
            adaptor.addChild(root_0, char_literal61_tree);


            }

            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end procuse

    public static class rule_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start rule
    // GCL.g:66:1: rule : IDENTIFIER ;
    public final rule_return rule() throws RecognitionException {
        rule_return retval = new rule_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token IDENTIFIER62=null;

        Object IDENTIFIER62_tree=null;

        try {
            // GCL.g:66:7: ( IDENTIFIER )
            // GCL.g:66:9: IDENTIFIER
            {
            root_0 = (Object)adaptor.nil();

            IDENTIFIER62=(Token)input.LT(1);
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_rule360); 
            IDENTIFIER62_tree = (Object)adaptor.create(IDENTIFIER62);
            adaptor.addChild(root_0, IDENTIFIER62_tree);


            }

            retval.stop = input.LT(-1);

                retval.tree = (Object)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

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


 

    public static final BitSet FOLLOW_procdef_in_program71 = new BitSet(new long[]{0x000000002030FA82L});
    public static final BitSet FOLLOW_statement_in_program73 = new BitSet(new long[]{0x000000002030FA82L});
    public static final BitSet FOLLOW_27_in_block96 = new BitSet(new long[]{0x000000003030FA00L});
    public static final BitSet FOLLOW_statement_in_block99 = new BitSet(new long[]{0x000000003030FA00L});
    public static final BitSet FOLLOW_28_in_block103 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_procdef112 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_IDENTIFIER_in_procdef114 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_procdef116 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_procdef119 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_block_in_procdef122 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionliteral_in_condition131 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_OR_in_condition134 = new BitSet(new long[]{0x0000000100000200L});
    public static final BitSet FOLLOW_condition_in_condition137 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALAP_in_statement150 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_block_in_statement152 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHILE_in_statement157 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_statement159 = new BitSet(new long[]{0x0000000100000200L});
    public static final BitSet FOLLOW_condition_in_statement162 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_statement164 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_DO_in_statement167 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_block_in_statement169 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DO_in_statement174 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_block_in_statement176 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_WHILE_in_statement178 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_statement180 = new BitSet(new long[]{0x0000000100000200L});
    public static final BitSet FOLLOW_condition_in_statement183 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_statement185 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRY_in_statement192 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_block_in_statement194 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_ELSE_in_statement197 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_block_in_statement199 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IF_in_statement206 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_statement208 = new BitSet(new long[]{0x0000000100000200L});
    public static final BitSet FOLLOW_condition_in_statement211 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_statement213 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_block_in_statement216 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_ELSE_in_statement219 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_block_in_statement221 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CHOICE_in_statement232 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_block_in_statement234 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_CH_OR_in_statement237 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_block_in_statement240 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_expression_in_statement247 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_31_in_statement249 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_32_in_conditionliteral265 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_conditionliteral269 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_atom_in_expression280 = new BitSet(new long[]{0x00000000000C0402L});
    public static final BitSet FOLLOW_OR_in_expression285 = new BitSet(new long[]{0x0000000020100200L});
    public static final BitSet FOLLOW_expression_in_expression288 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_expression293 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STAR_in_expression298 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SHARP_in_expression306 = new BitSet(new long[]{0x0000000020000200L});
    public static final BitSet FOLLOW_expression_atom_in_expression308 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_expression_atom319 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_expression_atom324 = new BitSet(new long[]{0x0000000020100200L});
    public static final BitSet FOLLOW_expression_in_expression_atom327 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_expression_atom329 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_procuse_in_expression_atom335 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_procuse347 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_procuse349 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_procuse351 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_rule360 = new BitSet(new long[]{0x0000000000000002L});

}