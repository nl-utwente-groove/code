// $ANTLR 3.1b1 GCL.g 2008-07-04 16:18:56

package groove.control.parse;
import groove.control.*;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


import org.antlr.runtime.tree.*;

public class GCLParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "PROGRAM", "BLOCK", "FUNCTIONS", "FUNCTION", "CALL", "IDENTIFIER", "OR", "ALAP", "WHILE", "DO", "TRY", "ELSE", "IF", "CHOICE", "CH_OR", "PLUS", "STAR", "SHARP", "AND", "COMMA", "DOT", "NOT", "WS", "'{'", "'}'", "'('", "')'", "';'", "'true'"
    };
    public static final int FUNCTION=7;
    public static final int STAR=20;
    public static final int T__29=29;
    public static final int T__28=28;
    public static final int SHARP=21;
    public static final int T__27=27;
    public static final int FUNCTIONS=6;
    public static final int WHILE=12;
    public static final int ELSE=15;
    public static final int DO=13;
    public static final int NOT=25;
    public static final int ALAP=11;
    public static final int AND=22;
    public static final int EOF=-1;
    public static final int TRY=14;
    public static final int IF=16;
    public static final int T__30=30;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int WS=26;
    public static final int COMMA=23;
    public static final int IDENTIFIER=9;
    public static final int BLOCK=5;
    public static final int OR=10;
    public static final int CH_OR=18;
    public static final int PLUS=19;
    public static final int PROGRAM=4;
    public static final int CALL=8;
    public static final int DOT=24;
    public static final int CHOICE=17;

    // delegates
    // delegators


        public GCLParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public GCLParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return GCLParser.tokenNames; }
    public String getGrammarFileName() { return "GCL.g"; }


    public static class program_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start program
    // GCL.g:29:1: program : ( function | statement )* -> ^( PROGRAM ^( FUNCTIONS ( function )* ) ^( BLOCK ( statement )* ) ) ;
    public final GCLParser.program_return program() throws RecognitionException {
        GCLParser.program_return retval = new GCLParser.program_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        GCLParser.function_return function1 = null;

        GCLParser.statement_return statement2 = null;


        RewriteRuleSubtreeStream stream_statement=new RewriteRuleSubtreeStream(adaptor,"rule statement");
        RewriteRuleSubtreeStream stream_function=new RewriteRuleSubtreeStream(adaptor,"rule function");
        try {
            // GCL.g:29:9: ( ( function | statement )* -> ^( PROGRAM ^( FUNCTIONS ( function )* ) ^( BLOCK ( statement )* ) ) )
            // GCL.g:29:11: ( function | statement )*
            {
            // GCL.g:29:11: ( function | statement )*
            loop1:
            do {
                int alt1=3;
                alt1 = dfa1.predict(input);
                switch (alt1) {
            	case 1 :
            	    // GCL.g:29:12: function
            	    {
            	    pushFollow(FOLLOW_function_in_program71);
            	    function1=function();

            	    state._fsp--;

            	    stream_function.add(function1.getTree());

            	    }
            	    break;
            	case 2 :
            	    // GCL.g:29:21: statement
            	    {
            	    pushFollow(FOLLOW_statement_in_program73);
            	    statement2=statement();

            	    state._fsp--;

            	    stream_statement.add(statement2.getTree());

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);



            // AST REWRITE
            // elements: statement, function
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 29:33: -> ^( PROGRAM ^( FUNCTIONS ( function )* ) ^( BLOCK ( statement )* ) )
            {
                // GCL.g:29:36: ^( PROGRAM ^( FUNCTIONS ( function )* ) ^( BLOCK ( statement )* ) )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PROGRAM, "PROGRAM"), root_1);

                // GCL.g:29:46: ^( FUNCTIONS ( function )* )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(FUNCTIONS, "FUNCTIONS"), root_2);

                // GCL.g:29:58: ( function )*
                while ( stream_function.hasNext() ) {
                    adaptor.addChild(root_2, stream_function.nextTree());

                }
                stream_function.reset();

                adaptor.addChild(root_1, root_2);
                }
                // GCL.g:29:69: ^( BLOCK ( statement )* )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(BLOCK, "BLOCK"), root_2);

                // GCL.g:29:77: ( statement )*
                while ( stream_statement.hasNext() ) {
                    adaptor.addChild(root_2, stream_statement.nextTree());

                }
                stream_statement.reset();

                adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

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
    // GCL.g:31:1: block : '{' ( statement )* '}' -> ^( BLOCK ( statement )* ) ;
    public final GCLParser.block_return block() throws RecognitionException {
        GCLParser.block_return retval = new GCLParser.block_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal3=null;
        Token char_literal5=null;
        GCLParser.statement_return statement4 = null;


        Object char_literal3_tree=null;
        Object char_literal5_tree=null;
        RewriteRuleTokenStream stream_27=new RewriteRuleTokenStream(adaptor,"token 27");
        RewriteRuleTokenStream stream_28=new RewriteRuleTokenStream(adaptor,"token 28");
        RewriteRuleSubtreeStream stream_statement=new RewriteRuleSubtreeStream(adaptor,"rule statement");
        try {
            // GCL.g:31:7: ( '{' ( statement )* '}' -> ^( BLOCK ( statement )* ) )
            // GCL.g:31:9: '{' ( statement )* '}'
            {
            char_literal3=(Token)match(input,27,FOLLOW_27_in_block103);  
            stream_27.add(char_literal3);

            // GCL.g:31:13: ( statement )*
            loop2:
            do {
                int alt2=2;
                alt2 = dfa2.predict(input);
                switch (alt2) {
            	case 1 :
            	    // GCL.g:31:13: statement
            	    {
            	    pushFollow(FOLLOW_statement_in_block105);
            	    statement4=statement();

            	    state._fsp--;

            	    stream_statement.add(statement4.getTree());

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            char_literal5=(Token)match(input,28,FOLLOW_28_in_block109);  
            stream_28.add(char_literal5);



            // AST REWRITE
            // elements: statement
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 31:29: -> ^( BLOCK ( statement )* )
            {
                // GCL.g:31:32: ^( BLOCK ( statement )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(BLOCK, "BLOCK"), root_1);

                // GCL.g:31:40: ( statement )*
                while ( stream_statement.hasNext() ) {
                    adaptor.addChild(root_1, stream_statement.nextTree());

                }
                stream_statement.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end block

    public static class function_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start function
    // GCL.g:33:1: function : FUNCTION IDENTIFIER '(' ')' block -> ^( FUNCTION IDENTIFIER block ) ;
    public final GCLParser.function_return function() throws RecognitionException {
        GCLParser.function_return retval = new GCLParser.function_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token FUNCTION6=null;
        Token IDENTIFIER7=null;
        Token char_literal8=null;
        Token char_literal9=null;
        GCLParser.block_return block10 = null;


        Object FUNCTION6_tree=null;
        Object IDENTIFIER7_tree=null;
        Object char_literal8_tree=null;
        Object char_literal9_tree=null;
        RewriteRuleTokenStream stream_FUNCTION=new RewriteRuleTokenStream(adaptor,"token FUNCTION");
        RewriteRuleTokenStream stream_30=new RewriteRuleTokenStream(adaptor,"token 30");
        RewriteRuleTokenStream stream_IDENTIFIER=new RewriteRuleTokenStream(adaptor,"token IDENTIFIER");
        RewriteRuleTokenStream stream_29=new RewriteRuleTokenStream(adaptor,"token 29");
        RewriteRuleSubtreeStream stream_block=new RewriteRuleSubtreeStream(adaptor,"rule block");
        try {
            // GCL.g:33:10: ( FUNCTION IDENTIFIER '(' ')' block -> ^( FUNCTION IDENTIFIER block ) )
            // GCL.g:33:12: FUNCTION IDENTIFIER '(' ')' block
            {
            FUNCTION6=(Token)match(input,FUNCTION,FOLLOW_FUNCTION_in_function126);  
            stream_FUNCTION.add(FUNCTION6);

            IDENTIFIER7=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_function128);  
            stream_IDENTIFIER.add(IDENTIFIER7);

            char_literal8=(Token)match(input,29,FOLLOW_29_in_function130);  
            stream_29.add(char_literal8);

            char_literal9=(Token)match(input,30,FOLLOW_30_in_function132);  
            stream_30.add(char_literal9);

            pushFollow(FOLLOW_block_in_function134);
            block10=block();

            state._fsp--;

            stream_block.add(block10.getTree());


            // AST REWRITE
            // elements: IDENTIFIER, block, FUNCTION
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 33:46: -> ^( FUNCTION IDENTIFIER block )
            {
                // GCL.g:33:49: ^( FUNCTION IDENTIFIER block )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_FUNCTION.nextNode(), root_1);

                adaptor.addChild(root_1, stream_IDENTIFIER.nextNode());
                adaptor.addChild(root_1, stream_block.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end function

    public static class condition_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start condition
    // GCL.g:35:1: condition : conditionliteral ( OR condition )? ;
    public final GCLParser.condition_return condition() throws RecognitionException {
        GCLParser.condition_return retval = new GCLParser.condition_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token OR12=null;
        GCLParser.conditionliteral_return conditionliteral11 = null;

        GCLParser.condition_return condition13 = null;


        Object OR12_tree=null;

        try {
            // GCL.g:36:2: ( conditionliteral ( OR condition )? )
            // GCL.g:36:4: conditionliteral ( OR condition )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_conditionliteral_in_condition153);
            conditionliteral11=conditionliteral();

            state._fsp--;

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
                    OR12=(Token)match(input,OR,FOLLOW_OR_in_condition156); 
                    OR12_tree = (Object)adaptor.create(OR12);
                    root_0 = (Object)adaptor.becomeRoot(OR12_tree, root_0);

                    pushFollow(FOLLOW_condition_in_condition159);
                    condition13=condition();

                    state._fsp--;

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
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

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
    // GCL.g:39:1: statement : ( ALAP block -> ^( ALAP block ) | WHILE '(' condition ')' DO block -> ^( WHILE condition block ) | DO block WHILE '(' condition ')' -> ^( DO block condition ) | TRY block ( ELSE block )? -> ^( TRY ( block )+ ) | IF '(' condition ')' block ( ELSE block )? -> ^( IF condition ( block )+ ) | CHOICE block ( CH_OR block )* -> ^( CHOICE ( block )+ ) | expression ';' -> expression );
    public final GCLParser.statement_return statement() throws RecognitionException {
        GCLParser.statement_return retval = new GCLParser.statement_return();
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
        Token ELSE30=null;
        Token IF32=null;
        Token char_literal33=null;
        Token char_literal35=null;
        Token ELSE37=null;
        Token CHOICE39=null;
        Token CH_OR41=null;
        Token char_literal44=null;
        GCLParser.block_return block15 = null;

        GCLParser.condition_return condition18 = null;

        GCLParser.block_return block21 = null;

        GCLParser.block_return block23 = null;

        GCLParser.condition_return condition26 = null;

        GCLParser.block_return block29 = null;

        GCLParser.block_return block31 = null;

        GCLParser.condition_return condition34 = null;

        GCLParser.block_return block36 = null;

        GCLParser.block_return block38 = null;

        GCLParser.block_return block40 = null;

        GCLParser.block_return block42 = null;

        GCLParser.expression_return expression43 = null;


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
        Object ELSE30_tree=null;
        Object IF32_tree=null;
        Object char_literal33_tree=null;
        Object char_literal35_tree=null;
        Object ELSE37_tree=null;
        Object CHOICE39_tree=null;
        Object CH_OR41_tree=null;
        Object char_literal44_tree=null;
        RewriteRuleTokenStream stream_DO=new RewriteRuleTokenStream(adaptor,"token DO");
        RewriteRuleTokenStream stream_30=new RewriteRuleTokenStream(adaptor,"token 30");
        RewriteRuleTokenStream stream_31=new RewriteRuleTokenStream(adaptor,"token 31");
        RewriteRuleTokenStream stream_WHILE=new RewriteRuleTokenStream(adaptor,"token WHILE");
        RewriteRuleTokenStream stream_ALAP=new RewriteRuleTokenStream(adaptor,"token ALAP");
        RewriteRuleTokenStream stream_CHOICE=new RewriteRuleTokenStream(adaptor,"token CHOICE");
        RewriteRuleTokenStream stream_TRY=new RewriteRuleTokenStream(adaptor,"token TRY");
        RewriteRuleTokenStream stream_IF=new RewriteRuleTokenStream(adaptor,"token IF");
        RewriteRuleTokenStream stream_ELSE=new RewriteRuleTokenStream(adaptor,"token ELSE");
        RewriteRuleTokenStream stream_CH_OR=new RewriteRuleTokenStream(adaptor,"token CH_OR");
        RewriteRuleTokenStream stream_29=new RewriteRuleTokenStream(adaptor,"token 29");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        RewriteRuleSubtreeStream stream_condition=new RewriteRuleSubtreeStream(adaptor,"rule condition");
        RewriteRuleSubtreeStream stream_block=new RewriteRuleSubtreeStream(adaptor,"rule block");
        try {
            // GCL.g:40:2: ( ALAP block -> ^( ALAP block ) | WHILE '(' condition ')' DO block -> ^( WHILE condition block ) | DO block WHILE '(' condition ')' -> ^( DO block condition ) | TRY block ( ELSE block )? -> ^( TRY ( block )+ ) | IF '(' condition ')' block ( ELSE block )? -> ^( IF condition ( block )+ ) | CHOICE block ( CH_OR block )* -> ^( CHOICE ( block )+ ) | expression ';' -> expression )
            int alt7=7;
            alt7 = dfa7.predict(input);
            switch (alt7) {
                case 1 :
                    // GCL.g:40:4: ALAP block
                    {
                    ALAP14=(Token)match(input,ALAP,FOLLOW_ALAP_in_statement174);  
                    stream_ALAP.add(ALAP14);

                    pushFollow(FOLLOW_block_in_statement176);
                    block15=block();

                    state._fsp--;

                    stream_block.add(block15.getTree());


                    // AST REWRITE
                    // elements: block, ALAP
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 40:15: -> ^( ALAP block )
                    {
                        // GCL.g:40:18: ^( ALAP block )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_ALAP.nextNode(), root_1);

                        adaptor.addChild(root_1, stream_block.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 2 :
                    // GCL.g:41:4: WHILE '(' condition ')' DO block
                    {
                    WHILE16=(Token)match(input,WHILE,FOLLOW_WHILE_in_statement189);  
                    stream_WHILE.add(WHILE16);

                    char_literal17=(Token)match(input,29,FOLLOW_29_in_statement191);  
                    stream_29.add(char_literal17);

                    pushFollow(FOLLOW_condition_in_statement193);
                    condition18=condition();

                    state._fsp--;

                    stream_condition.add(condition18.getTree());
                    char_literal19=(Token)match(input,30,FOLLOW_30_in_statement195);  
                    stream_30.add(char_literal19);

                    DO20=(Token)match(input,DO,FOLLOW_DO_in_statement197);  
                    stream_DO.add(DO20);

                    pushFollow(FOLLOW_block_in_statement199);
                    block21=block();

                    state._fsp--;

                    stream_block.add(block21.getTree());


                    // AST REWRITE
                    // elements: WHILE, block, condition
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 41:37: -> ^( WHILE condition block )
                    {
                        // GCL.g:41:40: ^( WHILE condition block )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_WHILE.nextNode(), root_1);

                        adaptor.addChild(root_1, stream_condition.nextTree());
                        adaptor.addChild(root_1, stream_block.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 3 :
                    // GCL.g:42:4: DO block WHILE '(' condition ')'
                    {
                    DO22=(Token)match(input,DO,FOLLOW_DO_in_statement214);  
                    stream_DO.add(DO22);

                    pushFollow(FOLLOW_block_in_statement216);
                    block23=block();

                    state._fsp--;

                    stream_block.add(block23.getTree());
                    WHILE24=(Token)match(input,WHILE,FOLLOW_WHILE_in_statement218);  
                    stream_WHILE.add(WHILE24);

                    char_literal25=(Token)match(input,29,FOLLOW_29_in_statement220);  
                    stream_29.add(char_literal25);

                    pushFollow(FOLLOW_condition_in_statement222);
                    condition26=condition();

                    state._fsp--;

                    stream_condition.add(condition26.getTree());
                    char_literal27=(Token)match(input,30,FOLLOW_30_in_statement224);  
                    stream_30.add(char_literal27);



                    // AST REWRITE
                    // elements: block, condition, DO
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 42:37: -> ^( DO block condition )
                    {
                        // GCL.g:42:40: ^( DO block condition )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_DO.nextNode(), root_1);

                        adaptor.addChild(root_1, stream_block.nextTree());
                        adaptor.addChild(root_1, stream_condition.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 4 :
                    // GCL.g:43:4: TRY block ( ELSE block )?
                    {
                    TRY28=(Token)match(input,TRY,FOLLOW_TRY_in_statement241);  
                    stream_TRY.add(TRY28);

                    pushFollow(FOLLOW_block_in_statement243);
                    block29=block();

                    state._fsp--;

                    stream_block.add(block29.getTree());
                    // GCL.g:43:14: ( ELSE block )?
                    int alt4=2;
                    alt4 = dfa4.predict(input);
                    switch (alt4) {
                        case 1 :
                            // GCL.g:43:15: ELSE block
                            {
                            ELSE30=(Token)match(input,ELSE,FOLLOW_ELSE_in_statement246);  
                            stream_ELSE.add(ELSE30);

                            pushFollow(FOLLOW_block_in_statement248);
                            block31=block();

                            state._fsp--;

                            stream_block.add(block31.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: block, TRY
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 43:28: -> ^( TRY ( block )+ )
                    {
                        // GCL.g:43:31: ^( TRY ( block )+ )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_TRY.nextNode(), root_1);

                        if ( !(stream_block.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_block.hasNext() ) {
                            adaptor.addChild(root_1, stream_block.nextTree());

                        }
                        stream_block.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 5 :
                    // GCL.g:44:4: IF '(' condition ')' block ( ELSE block )?
                    {
                    IF32=(Token)match(input,IF,FOLLOW_IF_in_statement264);  
                    stream_IF.add(IF32);

                    char_literal33=(Token)match(input,29,FOLLOW_29_in_statement266);  
                    stream_29.add(char_literal33);

                    pushFollow(FOLLOW_condition_in_statement268);
                    condition34=condition();

                    state._fsp--;

                    stream_condition.add(condition34.getTree());
                    char_literal35=(Token)match(input,30,FOLLOW_30_in_statement270);  
                    stream_30.add(char_literal35);

                    pushFollow(FOLLOW_block_in_statement272);
                    block36=block();

                    state._fsp--;

                    stream_block.add(block36.getTree());
                    // GCL.g:44:31: ( ELSE block )?
                    int alt5=2;
                    alt5 = dfa5.predict(input);
                    switch (alt5) {
                        case 1 :
                            // GCL.g:44:32: ELSE block
                            {
                            ELSE37=(Token)match(input,ELSE,FOLLOW_ELSE_in_statement275);  
                            stream_ELSE.add(ELSE37);

                            pushFollow(FOLLOW_block_in_statement277);
                            block38=block();

                            state._fsp--;

                            stream_block.add(block38.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: condition, block, IF
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 44:45: -> ^( IF condition ( block )+ )
                    {
                        // GCL.g:44:48: ^( IF condition ( block )+ )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_IF.nextNode(), root_1);

                        adaptor.addChild(root_1, stream_condition.nextTree());
                        if ( !(stream_block.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_block.hasNext() ) {
                            adaptor.addChild(root_1, stream_block.nextTree());

                        }
                        stream_block.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 6 :
                    // GCL.g:45:7: CHOICE block ( CH_OR block )*
                    {
                    CHOICE39=(Token)match(input,CHOICE,FOLLOW_CHOICE_in_statement298);  
                    stream_CHOICE.add(CHOICE39);

                    pushFollow(FOLLOW_block_in_statement300);
                    block40=block();

                    state._fsp--;

                    stream_block.add(block40.getTree());
                    // GCL.g:45:20: ( CH_OR block )*
                    loop6:
                    do {
                        int alt6=2;
                        alt6 = dfa6.predict(input);
                        switch (alt6) {
                    	case 1 :
                    	    // GCL.g:45:21: CH_OR block
                    	    {
                    	    CH_OR41=(Token)match(input,CH_OR,FOLLOW_CH_OR_in_statement303);  
                    	    stream_CH_OR.add(CH_OR41);

                    	    pushFollow(FOLLOW_block_in_statement305);
                    	    block42=block();

                    	    state._fsp--;

                    	    stream_block.add(block42.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop6;
                        }
                    } while (true);



                    // AST REWRITE
                    // elements: block, CHOICE
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 45:35: -> ^( CHOICE ( block )+ )
                    {
                        // GCL.g:45:38: ^( CHOICE ( block )+ )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_CHOICE.nextNode(), root_1);

                        if ( !(stream_block.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_block.hasNext() ) {
                            adaptor.addChild(root_1, stream_block.nextTree());

                        }
                        stream_block.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 7 :
                    // GCL.g:46:4: expression ';'
                    {
                    pushFollow(FOLLOW_expression_in_statement321);
                    expression43=expression();

                    state._fsp--;

                    stream_expression.add(expression43.getTree());
                    char_literal44=(Token)match(input,31,FOLLOW_31_in_statement323);  
                    stream_31.add(char_literal44);



                    // AST REWRITE
                    // elements: expression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 46:19: -> expression
                    {
                        adaptor.addChild(root_0, stream_expression.nextTree());

                    }

                    retval.tree = root_0;retval.tree = root_0;
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
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

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
    // GCL.g:50:1: conditionliteral : ( 'true' | rule );
    public final GCLParser.conditionliteral_return conditionliteral() throws RecognitionException {
        GCLParser.conditionliteral_return retval = new GCLParser.conditionliteral_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal45=null;
        GCLParser.rule_return rule46 = null;


        Object string_literal45_tree=null;

        try {
            // GCL.g:51:2: ( 'true' | rule )
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
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // GCL.g:51:4: 'true'
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal45=(Token)match(input,32,FOLLOW_32_in_conditionliteral342); 
                    string_literal45_tree = (Object)adaptor.create(string_literal45);
                    adaptor.addChild(root_0, string_literal45_tree);


                    }
                    break;
                case 2 :
                    // GCL.g:51:13: rule
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_rule_in_conditionliteral346);
                    rule46=rule();

                    state._fsp--;

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
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

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
    // GCL.g:53:1: expression : expression2 ( OR expression )? ;
    public final GCLParser.expression_return expression() throws RecognitionException {
        GCLParser.expression_return retval = new GCLParser.expression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token OR48=null;
        GCLParser.expression2_return expression247 = null;

        GCLParser.expression_return expression49 = null;


        Object OR48_tree=null;

        try {
            // GCL.g:54:2: ( expression2 ( OR expression )? )
            // GCL.g:54:4: expression2 ( OR expression )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_expression2_in_expression357);
            expression247=expression2();

            state._fsp--;

            adaptor.addChild(root_0, expression247.getTree());
            // GCL.g:54:16: ( OR expression )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==OR) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // GCL.g:54:17: OR expression
                    {
                    OR48=(Token)match(input,OR,FOLLOW_OR_in_expression360); 
                    OR48_tree = (Object)adaptor.create(OR48);
                    root_0 = (Object)adaptor.becomeRoot(OR48_tree, root_0);

                    pushFollow(FOLLOW_expression_in_expression363);
                    expression49=expression();

                    state._fsp--;

                    adaptor.addChild(root_0, expression49.getTree());

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
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end expression

    public static class expression2_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start expression2
    // GCL.g:57:1: expression2 : ( expression_atom ( PLUS | STAR )? | SHARP expression_atom );
    public final GCLParser.expression2_return expression2() throws RecognitionException {
        GCLParser.expression2_return retval = new GCLParser.expression2_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token PLUS51=null;
        Token STAR52=null;
        Token SHARP53=null;
        GCLParser.expression_atom_return expression_atom50 = null;

        GCLParser.expression_atom_return expression_atom54 = null;


        Object PLUS51_tree=null;
        Object STAR52_tree=null;
        Object SHARP53_tree=null;

        try {
            // GCL.g:58:5: ( expression_atom ( PLUS | STAR )? | SHARP expression_atom )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==IDENTIFIER||LA11_0==29) ) {
                alt11=1;
            }
            else if ( (LA11_0==SHARP) ) {
                alt11=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // GCL.g:58:7: expression_atom ( PLUS | STAR )?
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_expression_atom_in_expression2379);
                    expression_atom50=expression_atom();

                    state._fsp--;

                    adaptor.addChild(root_0, expression_atom50.getTree());
                    // GCL.g:58:23: ( PLUS | STAR )?
                    int alt10=3;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0==PLUS) ) {
                        alt10=1;
                    }
                    else if ( (LA10_0==STAR) ) {
                        alt10=2;
                    }
                    switch (alt10) {
                        case 1 :
                            // GCL.g:58:24: PLUS
                            {
                            PLUS51=(Token)match(input,PLUS,FOLLOW_PLUS_in_expression2382); 
                            PLUS51_tree = (Object)adaptor.create(PLUS51);
                            root_0 = (Object)adaptor.becomeRoot(PLUS51_tree, root_0);


                            }
                            break;
                        case 2 :
                            // GCL.g:58:32: STAR
                            {
                            STAR52=(Token)match(input,STAR,FOLLOW_STAR_in_expression2387); 
                            STAR52_tree = (Object)adaptor.create(STAR52);
                            root_0 = (Object)adaptor.becomeRoot(STAR52_tree, root_0);


                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // GCL.g:59:7: SHARP expression_atom
                    {
                    root_0 = (Object)adaptor.nil();

                    SHARP53=(Token)match(input,SHARP,FOLLOW_SHARP_in_expression2398); 
                    SHARP53_tree = (Object)adaptor.create(SHARP53);
                    root_0 = (Object)adaptor.becomeRoot(SHARP53_tree, root_0);

                    pushFollow(FOLLOW_expression_atom_in_expression2401);
                    expression_atom54=expression_atom();

                    state._fsp--;

                    adaptor.addChild(root_0, expression_atom54.getTree());

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
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end expression2

    public static class expression_atom_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start expression_atom
    // GCL.g:62:1: expression_atom : ( rule | '(' expression ')' | call );
    public final GCLParser.expression_atom_return expression_atom() throws RecognitionException {
        GCLParser.expression_atom_return retval = new GCLParser.expression_atom_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal56=null;
        Token char_literal58=null;
        GCLParser.rule_return rule55 = null;

        GCLParser.expression_return expression57 = null;

        GCLParser.call_return call59 = null;


        Object char_literal56_tree=null;
        Object char_literal58_tree=null;

        try {
            // GCL.g:63:2: ( rule | '(' expression ')' | call )
            int alt12=3;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==IDENTIFIER) ) {
                int LA12_1 = input.LA(2);

                if ( (LA12_1==29) ) {
                    alt12=3;
                }
                else if ( (LA12_1==OR||(LA12_1>=PLUS && LA12_1<=STAR)||(LA12_1>=30 && LA12_1<=31)) ) {
                    alt12=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 12, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA12_0==29) ) {
                alt12=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // GCL.g:63:4: rule
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_rule_in_expression_atom415);
                    rule55=rule();

                    state._fsp--;

                    adaptor.addChild(root_0, rule55.getTree());

                    }
                    break;
                case 2 :
                    // GCL.g:64:4: '(' expression ')'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal56=(Token)match(input,29,FOLLOW_29_in_expression_atom420); 
                    pushFollow(FOLLOW_expression_in_expression_atom423);
                    expression57=expression();

                    state._fsp--;

                    adaptor.addChild(root_0, expression57.getTree());
                    char_literal58=(Token)match(input,30,FOLLOW_30_in_expression_atom425); 

                    }
                    break;
                case 3 :
                    // GCL.g:65:4: call
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_call_in_expression_atom431);
                    call59=call();

                    state._fsp--;

                    adaptor.addChild(root_0, call59.getTree());

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
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end expression_atom

    public static class call_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start call
    // GCL.g:68:1: call : IDENTIFIER '(' ')' -> ^( CALL IDENTIFIER ) ;
    public final GCLParser.call_return call() throws RecognitionException {
        GCLParser.call_return retval = new GCLParser.call_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token IDENTIFIER60=null;
        Token char_literal61=null;
        Token char_literal62=null;

        Object IDENTIFIER60_tree=null;
        Object char_literal61_tree=null;
        Object char_literal62_tree=null;
        RewriteRuleTokenStream stream_30=new RewriteRuleTokenStream(adaptor,"token 30");
        RewriteRuleTokenStream stream_IDENTIFIER=new RewriteRuleTokenStream(adaptor,"token IDENTIFIER");
        RewriteRuleTokenStream stream_29=new RewriteRuleTokenStream(adaptor,"token 29");

        try {
            // GCL.g:69:2: ( IDENTIFIER '(' ')' -> ^( CALL IDENTIFIER ) )
            // GCL.g:69:4: IDENTIFIER '(' ')'
            {
            IDENTIFIER60=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_call443);  
            stream_IDENTIFIER.add(IDENTIFIER60);

            char_literal61=(Token)match(input,29,FOLLOW_29_in_call445);  
            stream_29.add(char_literal61);

            char_literal62=(Token)match(input,30,FOLLOW_30_in_call447);  
            stream_30.add(char_literal62);



            // AST REWRITE
            // elements: IDENTIFIER
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 69:23: -> ^( CALL IDENTIFIER )
            {
                // GCL.g:69:26: ^( CALL IDENTIFIER )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(CALL, "CALL"), root_1);

                adaptor.addChild(root_1, stream_IDENTIFIER.nextNode());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end call

    public static class rule_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start rule
    // GCL.g:71:1: rule : IDENTIFIER ;
    public final GCLParser.rule_return rule() throws RecognitionException {
        GCLParser.rule_return retval = new GCLParser.rule_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token IDENTIFIER63=null;

        Object IDENTIFIER63_tree=null;

        try {
            // GCL.g:71:7: ( IDENTIFIER )
            // GCL.g:71:9: IDENTIFIER
            {
            root_0 = (Object)adaptor.nil();

            IDENTIFIER63=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_rule464); 
            IDENTIFIER63_tree = (Object)adaptor.create(IDENTIFIER63);
            adaptor.addChild(root_0, IDENTIFIER63_tree);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end rule

    // Delegated rules


    protected DFA1 dfa1 = new DFA1(this);
    protected DFA2 dfa2 = new DFA2(this);
    protected DFA7 dfa7 = new DFA7(this);
    protected DFA4 dfa4 = new DFA4(this);
    protected DFA5 dfa5 = new DFA5(this);
    protected DFA6 dfa6 = new DFA6(this);
    static final String DFA1_eotS =
        "\14\uffff";
    static final String DFA1_eofS =
        "\1\1\13\uffff";
    static final String DFA1_minS =
        "\1\7\13\uffff";
    static final String DFA1_maxS =
        "\1\35\13\uffff";
    static final String DFA1_acceptS =
        "\1\uffff\1\3\1\1\1\2\10\uffff";
    static final String DFA1_specialS =
        "\14\uffff}>";
    static final String[] DFA1_transitionS = {
            "\1\2\1\uffff\1\3\1\uffff\4\3\1\uffff\2\3\3\uffff\1\3\7\uffff"+
            "\1\3",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA1_eot = DFA.unpackEncodedString(DFA1_eotS);
    static final short[] DFA1_eof = DFA.unpackEncodedString(DFA1_eofS);
    static final char[] DFA1_min = DFA.unpackEncodedStringToUnsignedChars(DFA1_minS);
    static final char[] DFA1_max = DFA.unpackEncodedStringToUnsignedChars(DFA1_maxS);
    static final short[] DFA1_accept = DFA.unpackEncodedString(DFA1_acceptS);
    static final short[] DFA1_special = DFA.unpackEncodedString(DFA1_specialS);
    static final short[][] DFA1_transition;

    static {
        int numStates = DFA1_transitionS.length;
        DFA1_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA1_transition[i] = DFA.unpackEncodedString(DFA1_transitionS[i]);
        }
    }

    class DFA1 extends DFA {

        public DFA1(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 1;
            this.eot = DFA1_eot;
            this.eof = DFA1_eof;
            this.min = DFA1_min;
            this.max = DFA1_max;
            this.accept = DFA1_accept;
            this.special = DFA1_special;
            this.transition = DFA1_transition;
        }
        public String getDescription() {
            return "()* loopback of 29:11: ( function | statement )*";
        }
    }
    static final String DFA2_eotS =
        "\13\uffff";
    static final String DFA2_eofS =
        "\13\uffff";
    static final String DFA2_minS =
        "\1\11\12\uffff";
    static final String DFA2_maxS =
        "\1\35\12\uffff";
    static final String DFA2_acceptS =
        "\1\uffff\1\2\1\1\10\uffff";
    static final String DFA2_specialS =
        "\13\uffff}>";
    static final String[] DFA2_transitionS = {
            "\1\2\1\uffff\4\2\1\uffff\2\2\3\uffff\1\2\6\uffff\1\1\1\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA2_eot = DFA.unpackEncodedString(DFA2_eotS);
    static final short[] DFA2_eof = DFA.unpackEncodedString(DFA2_eofS);
    static final char[] DFA2_min = DFA.unpackEncodedStringToUnsignedChars(DFA2_minS);
    static final char[] DFA2_max = DFA.unpackEncodedStringToUnsignedChars(DFA2_maxS);
    static final short[] DFA2_accept = DFA.unpackEncodedString(DFA2_acceptS);
    static final short[] DFA2_special = DFA.unpackEncodedString(DFA2_specialS);
    static final short[][] DFA2_transition;

    static {
        int numStates = DFA2_transitionS.length;
        DFA2_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA2_transition[i] = DFA.unpackEncodedString(DFA2_transitionS[i]);
        }
    }

    class DFA2 extends DFA {

        public DFA2(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 2;
            this.eot = DFA2_eot;
            this.eof = DFA2_eof;
            this.min = DFA2_min;
            this.max = DFA2_max;
            this.accept = DFA2_accept;
            this.special = DFA2_special;
            this.transition = DFA2_transition;
        }
        public String getDescription() {
            return "()* loopback of 31:13: ( statement )*";
        }
    }
    static final String DFA7_eotS =
        "\12\uffff";
    static final String DFA7_eofS =
        "\12\uffff";
    static final String DFA7_minS =
        "\1\11\11\uffff";
    static final String DFA7_maxS =
        "\1\35\11\uffff";
    static final String DFA7_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\2\uffff";
    static final String DFA7_specialS =
        "\12\uffff}>";
    static final String[] DFA7_transitionS = {
            "\1\7\1\uffff\1\1\1\2\1\3\1\4\1\uffff\1\5\1\6\3\uffff\1\7\7\uffff"+
            "\1\7",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA7_eot = DFA.unpackEncodedString(DFA7_eotS);
    static final short[] DFA7_eof = DFA.unpackEncodedString(DFA7_eofS);
    static final char[] DFA7_min = DFA.unpackEncodedStringToUnsignedChars(DFA7_minS);
    static final char[] DFA7_max = DFA.unpackEncodedStringToUnsignedChars(DFA7_maxS);
    static final short[] DFA7_accept = DFA.unpackEncodedString(DFA7_acceptS);
    static final short[] DFA7_special = DFA.unpackEncodedString(DFA7_specialS);
    static final short[][] DFA7_transition;

    static {
        int numStates = DFA7_transitionS.length;
        DFA7_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA7_transition[i] = DFA.unpackEncodedString(DFA7_transitionS[i]);
        }
    }

    class DFA7 extends DFA {

        public DFA7(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 7;
            this.eot = DFA7_eot;
            this.eof = DFA7_eof;
            this.min = DFA7_min;
            this.max = DFA7_max;
            this.accept = DFA7_accept;
            this.special = DFA7_special;
            this.transition = DFA7_transition;
        }
        public String getDescription() {
            return "39:1: statement : ( ALAP block -> ^( ALAP block ) | WHILE '(' condition ')' DO block -> ^( WHILE condition block ) | DO block WHILE '(' condition ')' -> ^( DO block condition ) | TRY block ( ELSE block )? -> ^( TRY ( block )+ ) | IF '(' condition ')' block ( ELSE block )? -> ^( IF condition ( block )+ ) | CHOICE block ( CH_OR block )* -> ^( CHOICE ( block )+ ) | expression ';' -> expression );";
        }
    }
    static final String DFA4_eotS =
        "\16\uffff";
    static final String DFA4_eofS =
        "\1\2\15\uffff";
    static final String DFA4_minS =
        "\1\7\15\uffff";
    static final String DFA4_maxS =
        "\1\35\15\uffff";
    static final String DFA4_acceptS =
        "\1\uffff\1\1\1\2\13\uffff";
    static final String DFA4_specialS =
        "\16\uffff}>";
    static final String[] DFA4_transitionS = {
            "\1\2\1\uffff\1\2\1\uffff\4\2\1\1\2\2\3\uffff\1\2\6\uffff\2\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA4_eot = DFA.unpackEncodedString(DFA4_eotS);
    static final short[] DFA4_eof = DFA.unpackEncodedString(DFA4_eofS);
    static final char[] DFA4_min = DFA.unpackEncodedStringToUnsignedChars(DFA4_minS);
    static final char[] DFA4_max = DFA.unpackEncodedStringToUnsignedChars(DFA4_maxS);
    static final short[] DFA4_accept = DFA.unpackEncodedString(DFA4_acceptS);
    static final short[] DFA4_special = DFA.unpackEncodedString(DFA4_specialS);
    static final short[][] DFA4_transition;

    static {
        int numStates = DFA4_transitionS.length;
        DFA4_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA4_transition[i] = DFA.unpackEncodedString(DFA4_transitionS[i]);
        }
    }

    class DFA4 extends DFA {

        public DFA4(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 4;
            this.eot = DFA4_eot;
            this.eof = DFA4_eof;
            this.min = DFA4_min;
            this.max = DFA4_max;
            this.accept = DFA4_accept;
            this.special = DFA4_special;
            this.transition = DFA4_transition;
        }
        public String getDescription() {
            return "43:14: ( ELSE block )?";
        }
    }
    static final String DFA5_eotS =
        "\16\uffff";
    static final String DFA5_eofS =
        "\1\2\15\uffff";
    static final String DFA5_minS =
        "\1\7\15\uffff";
    static final String DFA5_maxS =
        "\1\35\15\uffff";
    static final String DFA5_acceptS =
        "\1\uffff\1\1\1\2\13\uffff";
    static final String DFA5_specialS =
        "\16\uffff}>";
    static final String[] DFA5_transitionS = {
            "\1\2\1\uffff\1\2\1\uffff\4\2\1\1\2\2\3\uffff\1\2\6\uffff\2\2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA5_eot = DFA.unpackEncodedString(DFA5_eotS);
    static final short[] DFA5_eof = DFA.unpackEncodedString(DFA5_eofS);
    static final char[] DFA5_min = DFA.unpackEncodedStringToUnsignedChars(DFA5_minS);
    static final char[] DFA5_max = DFA.unpackEncodedStringToUnsignedChars(DFA5_maxS);
    static final short[] DFA5_accept = DFA.unpackEncodedString(DFA5_acceptS);
    static final short[] DFA5_special = DFA.unpackEncodedString(DFA5_specialS);
    static final short[][] DFA5_transition;

    static {
        int numStates = DFA5_transitionS.length;
        DFA5_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA5_transition[i] = DFA.unpackEncodedString(DFA5_transitionS[i]);
        }
    }

    class DFA5 extends DFA {

        public DFA5(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 5;
            this.eot = DFA5_eot;
            this.eof = DFA5_eof;
            this.min = DFA5_min;
            this.max = DFA5_max;
            this.accept = DFA5_accept;
            this.special = DFA5_special;
            this.transition = DFA5_transition;
        }
        public String getDescription() {
            return "44:31: ( ELSE block )?";
        }
    }
    static final String DFA6_eotS =
        "\16\uffff";
    static final String DFA6_eofS =
        "\1\1\15\uffff";
    static final String DFA6_minS =
        "\1\7\15\uffff";
    static final String DFA6_maxS =
        "\1\35\15\uffff";
    static final String DFA6_acceptS =
        "\1\uffff\1\2\13\uffff\1\1";
    static final String DFA6_specialS =
        "\16\uffff}>";
    static final String[] DFA6_transitionS = {
            "\1\1\1\uffff\1\1\1\uffff\4\1\1\uffff\2\1\1\15\2\uffff\1\1\6"+
            "\uffff\2\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA6_eot = DFA.unpackEncodedString(DFA6_eotS);
    static final short[] DFA6_eof = DFA.unpackEncodedString(DFA6_eofS);
    static final char[] DFA6_min = DFA.unpackEncodedStringToUnsignedChars(DFA6_minS);
    static final char[] DFA6_max = DFA.unpackEncodedStringToUnsignedChars(DFA6_maxS);
    static final short[] DFA6_accept = DFA.unpackEncodedString(DFA6_acceptS);
    static final short[] DFA6_special = DFA.unpackEncodedString(DFA6_specialS);
    static final short[][] DFA6_transition;

    static {
        int numStates = DFA6_transitionS.length;
        DFA6_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA6_transition[i] = DFA.unpackEncodedString(DFA6_transitionS[i]);
        }
    }

    class DFA6 extends DFA {

        public DFA6(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 6;
            this.eot = DFA6_eot;
            this.eof = DFA6_eof;
            this.min = DFA6_min;
            this.max = DFA6_max;
            this.accept = DFA6_accept;
            this.special = DFA6_special;
            this.transition = DFA6_transition;
        }
        public String getDescription() {
            return "()* loopback of 45:20: ( CH_OR block )*";
        }
    }
 

    public static final BitSet FOLLOW_function_in_program71 = new BitSet(new long[]{0x0000000020237A82L});
    public static final BitSet FOLLOW_statement_in_program73 = new BitSet(new long[]{0x0000000020237A82L});
    public static final BitSet FOLLOW_27_in_block103 = new BitSet(new long[]{0x0000000030237A80L});
    public static final BitSet FOLLOW_statement_in_block105 = new BitSet(new long[]{0x0000000030237A80L});
    public static final BitSet FOLLOW_28_in_block109 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_function126 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_IDENTIFIER_in_function128 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_function130 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_function132 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_block_in_function134 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionliteral_in_condition153 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_OR_in_condition156 = new BitSet(new long[]{0x0000000100000200L});
    public static final BitSet FOLLOW_condition_in_condition159 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALAP_in_statement174 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_block_in_statement176 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHILE_in_statement189 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_statement191 = new BitSet(new long[]{0x0000000100000200L});
    public static final BitSet FOLLOW_condition_in_statement193 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_statement195 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_DO_in_statement197 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_block_in_statement199 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DO_in_statement214 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_block_in_statement216 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_WHILE_in_statement218 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_statement220 = new BitSet(new long[]{0x0000000100000200L});
    public static final BitSet FOLLOW_condition_in_statement222 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_statement224 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRY_in_statement241 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_block_in_statement243 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_ELSE_in_statement246 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_block_in_statement248 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IF_in_statement264 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_statement266 = new BitSet(new long[]{0x0000000100000200L});
    public static final BitSet FOLLOW_condition_in_statement268 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_statement270 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_block_in_statement272 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_ELSE_in_statement275 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_block_in_statement277 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CHOICE_in_statement298 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_block_in_statement300 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_CH_OR_in_statement303 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_block_in_statement305 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_expression_in_statement321 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_31_in_statement323 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_32_in_conditionliteral342 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_conditionliteral346 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression2_in_expression357 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_OR_in_expression360 = new BitSet(new long[]{0x0000000020237A80L});
    public static final BitSet FOLLOW_expression_in_expression363 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_atom_in_expression2379 = new BitSet(new long[]{0x0000000000180002L});
    public static final BitSet FOLLOW_PLUS_in_expression2382 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STAR_in_expression2387 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SHARP_in_expression2398 = new BitSet(new long[]{0x0000000020000200L});
    public static final BitSet FOLLOW_expression_atom_in_expression2401 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_expression_atom415 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_expression_atom420 = new BitSet(new long[]{0x0000000060237A80L});
    public static final BitSet FOLLOW_expression_in_expression_atom423 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_expression_atom425 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_expression_atom431 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_call443 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_call445 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_call447 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_rule464 = new BitSet(new long[]{0x0000000000000002L});

}