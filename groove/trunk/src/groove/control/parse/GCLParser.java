// $ANTLR 3.1b1 GCL.g 2008-10-08 09:36:11

package groove.control.parse;
import groove.control.*;
import java.util.LinkedList;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


import org.antlr.runtime.tree.*;

public class GCLParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "PROGRAM", "BLOCK", "FUNCTIONS", "FUNCTION", "CALL", "IDENTIFIER", "OR", "ALAP", "WHILE", "DO", "UNTIL", "TRY", "ELSE", "IF", "CHOICE", "CH_OR", "TRUE", "PLUS", "STAR", "SHARP", "ANY", "OTHER", "AND", "COMMA", "DOT", "NOT", "WS", "'{'", "'}'", "'('", "')'", "';'"
    };
    public static final int FUNCTION=7;
    public static final int STAR=22;
    public static final int OTHER=25;
    public static final int SHARP=23;
    public static final int FUNCTIONS=6;
    public static final int WHILE=12;
    public static final int ELSE=16;
    public static final int DO=13;
    public static final int NOT=29;
    public static final int ALAP=11;
    public static final int AND=26;
    public static final int EOF=-1;
    public static final int TRUE=20;
    public static final int TRY=15;
    public static final int IF=17;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int WS=30;
    public static final int ANY=24;
    public static final int T__33=33;
    public static final int T__34=34;
    public static final int T__35=35;
    public static final int COMMA=27;
    public static final int UNTIL=14;
    public static final int IDENTIFIER=9;
    public static final int BLOCK=5;
    public static final int OR=10;
    public static final int CH_OR=19;
    public static final int PLUS=21;
    public static final int PROGRAM=4;
    public static final int CALL=8;
    public static final int DOT=28;
    public static final int CHOICE=18;

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


    public static class program_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start program
    // GCL.g:44:1: program : ( function | statement )* -> ^( PROGRAM ^( FUNCTIONS ( function )* ) ^( BLOCK ( statement )* ) ) ;
    public final GCLParser.program_return program() throws RecognitionException {
        GCLParser.program_return retval = new GCLParser.program_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        GCLParser.function_return function1 = null;

        GCLParser.statement_return statement2 = null;


        RewriteRuleSubtreeStream stream_statement=new RewriteRuleSubtreeStream(adaptor,"rule statement");
        RewriteRuleSubtreeStream stream_function=new RewriteRuleSubtreeStream(adaptor,"rule function");
        try {
            // GCL.g:44:9: ( ( function | statement )* -> ^( PROGRAM ^( FUNCTIONS ( function )* ) ^( BLOCK ( statement )* ) ) )
            // GCL.g:44:11: ( function | statement )*
            {
            // GCL.g:44:11: ( function | statement )*
            loop1:
            do {
                int alt1=3;
                alt1 = dfa1.predict(input);
                switch (alt1) {
            	case 1 :
            	    // GCL.g:44:12: function
            	    {
            	    pushFollow(FOLLOW_function_in_program77);
            	    function1=function();

            	    state._fsp--;

            	    stream_function.add(function1.getTree());

            	    }
            	    break;
            	case 2 :
            	    // GCL.g:44:21: statement
            	    {
            	    pushFollow(FOLLOW_statement_in_program79);
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
            // elements: function, statement
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 44:33: -> ^( PROGRAM ^( FUNCTIONS ( function )* ) ^( BLOCK ( statement )* ) )
            {
                // GCL.g:44:36: ^( PROGRAM ^( FUNCTIONS ( function )* ) ^( BLOCK ( statement )* ) )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PROGRAM, "PROGRAM"), root_1);

                // GCL.g:44:46: ^( FUNCTIONS ( function )* )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(FUNCTIONS, "FUNCTIONS"), root_2);

                // GCL.g:44:58: ( function )*
                while ( stream_function.hasNext() ) {
                    adaptor.addChild(root_2, stream_function.nextTree());

                }
                stream_function.reset();

                adaptor.addChild(root_1, root_2);
                }
                // GCL.g:44:69: ^( BLOCK ( statement )* )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(BLOCK, "BLOCK"), root_2);

                // GCL.g:44:77: ( statement )*
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
    // GCL.g:46:1: block : '{' ( statement )* '}' -> ^( BLOCK ( statement )* ) ;
    public final GCLParser.block_return block() throws RecognitionException {
        GCLParser.block_return retval = new GCLParser.block_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal3=null;
        Token char_literal5=null;
        GCLParser.statement_return statement4 = null;


        Object char_literal3_tree=null;
        Object char_literal5_tree=null;
        RewriteRuleTokenStream stream_32=new RewriteRuleTokenStream(adaptor,"token 32");
        RewriteRuleTokenStream stream_31=new RewriteRuleTokenStream(adaptor,"token 31");
        RewriteRuleSubtreeStream stream_statement=new RewriteRuleSubtreeStream(adaptor,"rule statement");
        try {
            // GCL.g:46:7: ( '{' ( statement )* '}' -> ^( BLOCK ( statement )* ) )
            // GCL.g:46:9: '{' ( statement )* '}'
            {
            char_literal3=(Token)match(input,31,FOLLOW_31_in_block109);  
            stream_31.add(char_literal3);

            // GCL.g:46:13: ( statement )*
            loop2:
            do {
                int alt2=2;
                alt2 = dfa2.predict(input);
                switch (alt2) {
            	case 1 :
            	    // GCL.g:46:13: statement
            	    {
            	    pushFollow(FOLLOW_statement_in_block111);
            	    statement4=statement();

            	    state._fsp--;

            	    stream_statement.add(statement4.getTree());

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            char_literal5=(Token)match(input,32,FOLLOW_32_in_block115);  
            stream_32.add(char_literal5);



            // AST REWRITE
            // elements: statement
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 46:29: -> ^( BLOCK ( statement )* )
            {
                // GCL.g:46:32: ^( BLOCK ( statement )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(BLOCK, "BLOCK"), root_1);

                // GCL.g:46:40: ( statement )*
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
    // GCL.g:48:1: function : FUNCTION IDENTIFIER '(' ')' block -> ^( FUNCTION IDENTIFIER block ) ;
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
        RewriteRuleTokenStream stream_33=new RewriteRuleTokenStream(adaptor,"token 33");
        RewriteRuleTokenStream stream_IDENTIFIER=new RewriteRuleTokenStream(adaptor,"token IDENTIFIER");
        RewriteRuleTokenStream stream_34=new RewriteRuleTokenStream(adaptor,"token 34");
        RewriteRuleSubtreeStream stream_block=new RewriteRuleSubtreeStream(adaptor,"rule block");
        try {
            // GCL.g:48:10: ( FUNCTION IDENTIFIER '(' ')' block -> ^( FUNCTION IDENTIFIER block ) )
            // GCL.g:48:12: FUNCTION IDENTIFIER '(' ')' block
            {
            FUNCTION6=(Token)match(input,FUNCTION,FOLLOW_FUNCTION_in_function132);  
            stream_FUNCTION.add(FUNCTION6);

            IDENTIFIER7=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_function134);  
            stream_IDENTIFIER.add(IDENTIFIER7);

            char_literal8=(Token)match(input,33,FOLLOW_33_in_function136);  
            stream_33.add(char_literal8);

            char_literal9=(Token)match(input,34,FOLLOW_34_in_function138);  
            stream_34.add(char_literal9);

            pushFollow(FOLLOW_block_in_function140);
            block10=block();

            state._fsp--;

            stream_block.add(block10.getTree());


            // AST REWRITE
            // elements: FUNCTION, IDENTIFIER, block
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 48:46: -> ^( FUNCTION IDENTIFIER block )
            {
                // GCL.g:48:49: ^( FUNCTION IDENTIFIER block )
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
    // GCL.g:50:1: condition : conditionliteral ( OR condition )? ;
    public final GCLParser.condition_return condition() throws RecognitionException {
        GCLParser.condition_return retval = new GCLParser.condition_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token OR12=null;
        GCLParser.conditionliteral_return conditionliteral11 = null;

        GCLParser.condition_return condition13 = null;


        Object OR12_tree=null;

        try {
            // GCL.g:51:2: ( conditionliteral ( OR condition )? )
            // GCL.g:51:4: conditionliteral ( OR condition )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_conditionliteral_in_condition159);
            conditionliteral11=conditionliteral();

            state._fsp--;

            adaptor.addChild(root_0, conditionliteral11.getTree());
            // GCL.g:51:21: ( OR condition )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==OR) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // GCL.g:51:22: OR condition
                    {
                    OR12=(Token)match(input,OR,FOLLOW_OR_in_condition162); 
                    OR12_tree = (Object)adaptor.create(OR12);
                    root_0 = (Object)adaptor.becomeRoot(OR12_tree, root_0);

                    pushFollow(FOLLOW_condition_in_condition165);
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
    // GCL.g:54:1: statement : ( ALAP block -> ^( ALAP block ) | WHILE '(' condition ')' DO block -> ^( WHILE condition block ) | DO block WHILE '(' condition ')' -> ^( DO block condition ) | UNTIL '(' condition ')' DO block -> ^( UNTIL condition block ) | TRY block ( ELSE block )? -> ^( TRY ( block )+ ) | IF '(' condition ')' block ( ELSE block )? -> ^( IF condition ( block )+ ) | CHOICE block ( CH_OR block )* -> ^( CHOICE ( block )+ ) | expression ';' -> expression );
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
        Token UNTIL28=null;
        Token char_literal29=null;
        Token char_literal31=null;
        Token DO32=null;
        Token TRY34=null;
        Token ELSE36=null;
        Token IF38=null;
        Token char_literal39=null;
        Token char_literal41=null;
        Token ELSE43=null;
        Token CHOICE45=null;
        Token CH_OR47=null;
        Token char_literal50=null;
        GCLParser.block_return block15 = null;

        GCLParser.condition_return condition18 = null;

        GCLParser.block_return block21 = null;

        GCLParser.block_return block23 = null;

        GCLParser.condition_return condition26 = null;

        GCLParser.condition_return condition30 = null;

        GCLParser.block_return block33 = null;

        GCLParser.block_return block35 = null;

        GCLParser.block_return block37 = null;

        GCLParser.condition_return condition40 = null;

        GCLParser.block_return block42 = null;

        GCLParser.block_return block44 = null;

        GCLParser.block_return block46 = null;

        GCLParser.block_return block48 = null;

        GCLParser.expression_return expression49 = null;


        Object ALAP14_tree=null;
        Object WHILE16_tree=null;
        Object char_literal17_tree=null;
        Object char_literal19_tree=null;
        Object DO20_tree=null;
        Object DO22_tree=null;
        Object WHILE24_tree=null;
        Object char_literal25_tree=null;
        Object char_literal27_tree=null;
        Object UNTIL28_tree=null;
        Object char_literal29_tree=null;
        Object char_literal31_tree=null;
        Object DO32_tree=null;
        Object TRY34_tree=null;
        Object ELSE36_tree=null;
        Object IF38_tree=null;
        Object char_literal39_tree=null;
        Object char_literal41_tree=null;
        Object ELSE43_tree=null;
        Object CHOICE45_tree=null;
        Object CH_OR47_tree=null;
        Object char_literal50_tree=null;
        RewriteRuleTokenStream stream_DO=new RewriteRuleTokenStream(adaptor,"token DO");
        RewriteRuleTokenStream stream_WHILE=new RewriteRuleTokenStream(adaptor,"token WHILE");
        RewriteRuleTokenStream stream_ALAP=new RewriteRuleTokenStream(adaptor,"token ALAP");
        RewriteRuleTokenStream stream_35=new RewriteRuleTokenStream(adaptor,"token 35");
        RewriteRuleTokenStream stream_UNTIL=new RewriteRuleTokenStream(adaptor,"token UNTIL");
        RewriteRuleTokenStream stream_33=new RewriteRuleTokenStream(adaptor,"token 33");
        RewriteRuleTokenStream stream_34=new RewriteRuleTokenStream(adaptor,"token 34");
        RewriteRuleTokenStream stream_CHOICE=new RewriteRuleTokenStream(adaptor,"token CHOICE");
        RewriteRuleTokenStream stream_TRY=new RewriteRuleTokenStream(adaptor,"token TRY");
        RewriteRuleTokenStream stream_IF=new RewriteRuleTokenStream(adaptor,"token IF");
        RewriteRuleTokenStream stream_ELSE=new RewriteRuleTokenStream(adaptor,"token ELSE");
        RewriteRuleTokenStream stream_CH_OR=new RewriteRuleTokenStream(adaptor,"token CH_OR");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        RewriteRuleSubtreeStream stream_condition=new RewriteRuleSubtreeStream(adaptor,"rule condition");
        RewriteRuleSubtreeStream stream_block=new RewriteRuleSubtreeStream(adaptor,"rule block");
        try {
            // GCL.g:55:2: ( ALAP block -> ^( ALAP block ) | WHILE '(' condition ')' DO block -> ^( WHILE condition block ) | DO block WHILE '(' condition ')' -> ^( DO block condition ) | UNTIL '(' condition ')' DO block -> ^( UNTIL condition block ) | TRY block ( ELSE block )? -> ^( TRY ( block )+ ) | IF '(' condition ')' block ( ELSE block )? -> ^( IF condition ( block )+ ) | CHOICE block ( CH_OR block )* -> ^( CHOICE ( block )+ ) | expression ';' -> expression )
            int alt7=8;
            alt7 = dfa7.predict(input);
            switch (alt7) {
                case 1 :
                    // GCL.g:55:4: ALAP block
                    {
                    ALAP14=(Token)match(input,ALAP,FOLLOW_ALAP_in_statement180);  
                    stream_ALAP.add(ALAP14);

                    pushFollow(FOLLOW_block_in_statement182);
                    block15=block();

                    state._fsp--;

                    stream_block.add(block15.getTree());


                    // AST REWRITE
                    // elements: ALAP, block
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 55:15: -> ^( ALAP block )
                    {
                        // GCL.g:55:18: ^( ALAP block )
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
                    // GCL.g:56:4: WHILE '(' condition ')' DO block
                    {
                    WHILE16=(Token)match(input,WHILE,FOLLOW_WHILE_in_statement195);  
                    stream_WHILE.add(WHILE16);

                    char_literal17=(Token)match(input,33,FOLLOW_33_in_statement197);  
                    stream_33.add(char_literal17);

                    pushFollow(FOLLOW_condition_in_statement199);
                    condition18=condition();

                    state._fsp--;

                    stream_condition.add(condition18.getTree());
                    char_literal19=(Token)match(input,34,FOLLOW_34_in_statement201);  
                    stream_34.add(char_literal19);

                    DO20=(Token)match(input,DO,FOLLOW_DO_in_statement203);  
                    stream_DO.add(DO20);

                    pushFollow(FOLLOW_block_in_statement205);
                    block21=block();

                    state._fsp--;

                    stream_block.add(block21.getTree());


                    // AST REWRITE
                    // elements: condition, block, WHILE
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 56:37: -> ^( WHILE condition block )
                    {
                        // GCL.g:56:40: ^( WHILE condition block )
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
                    // GCL.g:57:4: DO block WHILE '(' condition ')'
                    {
                    DO22=(Token)match(input,DO,FOLLOW_DO_in_statement220);  
                    stream_DO.add(DO22);

                    pushFollow(FOLLOW_block_in_statement222);
                    block23=block();

                    state._fsp--;

                    stream_block.add(block23.getTree());
                    WHILE24=(Token)match(input,WHILE,FOLLOW_WHILE_in_statement224);  
                    stream_WHILE.add(WHILE24);

                    char_literal25=(Token)match(input,33,FOLLOW_33_in_statement226);  
                    stream_33.add(char_literal25);

                    pushFollow(FOLLOW_condition_in_statement228);
                    condition26=condition();

                    state._fsp--;

                    stream_condition.add(condition26.getTree());
                    char_literal27=(Token)match(input,34,FOLLOW_34_in_statement230);  
                    stream_34.add(char_literal27);



                    // AST REWRITE
                    // elements: condition, block, DO
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 57:37: -> ^( DO block condition )
                    {
                        // GCL.g:57:40: ^( DO block condition )
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
                    // GCL.g:58:4: UNTIL '(' condition ')' DO block
                    {
                    UNTIL28=(Token)match(input,UNTIL,FOLLOW_UNTIL_in_statement245);  
                    stream_UNTIL.add(UNTIL28);

                    char_literal29=(Token)match(input,33,FOLLOW_33_in_statement247);  
                    stream_33.add(char_literal29);

                    pushFollow(FOLLOW_condition_in_statement249);
                    condition30=condition();

                    state._fsp--;

                    stream_condition.add(condition30.getTree());
                    char_literal31=(Token)match(input,34,FOLLOW_34_in_statement251);  
                    stream_34.add(char_literal31);

                    DO32=(Token)match(input,DO,FOLLOW_DO_in_statement253);  
                    stream_DO.add(DO32);

                    pushFollow(FOLLOW_block_in_statement255);
                    block33=block();

                    state._fsp--;

                    stream_block.add(block33.getTree());


                    // AST REWRITE
                    // elements: block, UNTIL, condition
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 58:37: -> ^( UNTIL condition block )
                    {
                        // GCL.g:58:40: ^( UNTIL condition block )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_UNTIL.nextNode(), root_1);

                        adaptor.addChild(root_1, stream_condition.nextTree());
                        adaptor.addChild(root_1, stream_block.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 5 :
                    // GCL.g:59:4: TRY block ( ELSE block )?
                    {
                    TRY34=(Token)match(input,TRY,FOLLOW_TRY_in_statement270);  
                    stream_TRY.add(TRY34);

                    pushFollow(FOLLOW_block_in_statement272);
                    block35=block();

                    state._fsp--;

                    stream_block.add(block35.getTree());
                    // GCL.g:59:14: ( ELSE block )?
                    int alt4=2;
                    alt4 = dfa4.predict(input);
                    switch (alt4) {
                        case 1 :
                            // GCL.g:59:15: ELSE block
                            {
                            ELSE36=(Token)match(input,ELSE,FOLLOW_ELSE_in_statement275);  
                            stream_ELSE.add(ELSE36);

                            pushFollow(FOLLOW_block_in_statement277);
                            block37=block();

                            state._fsp--;

                            stream_block.add(block37.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: TRY, block
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 59:28: -> ^( TRY ( block )+ )
                    {
                        // GCL.g:59:31: ^( TRY ( block )+ )
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
                case 6 :
                    // GCL.g:60:4: IF '(' condition ')' block ( ELSE block )?
                    {
                    IF38=(Token)match(input,IF,FOLLOW_IF_in_statement293);  
                    stream_IF.add(IF38);

                    char_literal39=(Token)match(input,33,FOLLOW_33_in_statement295);  
                    stream_33.add(char_literal39);

                    pushFollow(FOLLOW_condition_in_statement297);
                    condition40=condition();

                    state._fsp--;

                    stream_condition.add(condition40.getTree());
                    char_literal41=(Token)match(input,34,FOLLOW_34_in_statement299);  
                    stream_34.add(char_literal41);

                    pushFollow(FOLLOW_block_in_statement301);
                    block42=block();

                    state._fsp--;

                    stream_block.add(block42.getTree());
                    // GCL.g:60:31: ( ELSE block )?
                    int alt5=2;
                    alt5 = dfa5.predict(input);
                    switch (alt5) {
                        case 1 :
                            // GCL.g:60:32: ELSE block
                            {
                            ELSE43=(Token)match(input,ELSE,FOLLOW_ELSE_in_statement304);  
                            stream_ELSE.add(ELSE43);

                            pushFollow(FOLLOW_block_in_statement306);
                            block44=block();

                            state._fsp--;

                            stream_block.add(block44.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: IF, block, condition
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 60:45: -> ^( IF condition ( block )+ )
                    {
                        // GCL.g:60:48: ^( IF condition ( block )+ )
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
                case 7 :
                    // GCL.g:61:7: CHOICE block ( CH_OR block )*
                    {
                    CHOICE45=(Token)match(input,CHOICE,FOLLOW_CHOICE_in_statement327);  
                    stream_CHOICE.add(CHOICE45);

                    pushFollow(FOLLOW_block_in_statement329);
                    block46=block();

                    state._fsp--;

                    stream_block.add(block46.getTree());
                    // GCL.g:61:20: ( CH_OR block )*
                    loop6:
                    do {
                        int alt6=2;
                        alt6 = dfa6.predict(input);
                        switch (alt6) {
                    	case 1 :
                    	    // GCL.g:61:21: CH_OR block
                    	    {
                    	    CH_OR47=(Token)match(input,CH_OR,FOLLOW_CH_OR_in_statement332);  
                    	    stream_CH_OR.add(CH_OR47);

                    	    pushFollow(FOLLOW_block_in_statement334);
                    	    block48=block();

                    	    state._fsp--;

                    	    stream_block.add(block48.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop6;
                        }
                    } while (true);



                    // AST REWRITE
                    // elements: CHOICE, block
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 61:35: -> ^( CHOICE ( block )+ )
                    {
                        // GCL.g:61:38: ^( CHOICE ( block )+ )
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
                case 8 :
                    // GCL.g:62:4: expression ';'
                    {
                    pushFollow(FOLLOW_expression_in_statement350);
                    expression49=expression();

                    state._fsp--;

                    stream_expression.add(expression49.getTree());
                    char_literal50=(Token)match(input,35,FOLLOW_35_in_statement352);  
                    stream_35.add(char_literal50);



                    // AST REWRITE
                    // elements: expression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 62:19: -> expression
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
    // GCL.g:66:1: conditionliteral : ( TRUE | rule );
    public final GCLParser.conditionliteral_return conditionliteral() throws RecognitionException {
        GCLParser.conditionliteral_return retval = new GCLParser.conditionliteral_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token TRUE51=null;
        GCLParser.rule_return rule52 = null;


        Object TRUE51_tree=null;

        try {
            // GCL.g:67:2: ( TRUE | rule )
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==TRUE) ) {
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
                    // GCL.g:67:4: TRUE
                    {
                    root_0 = (Object)adaptor.nil();

                    TRUE51=(Token)match(input,TRUE,FOLLOW_TRUE_in_conditionliteral371); 
                    TRUE51_tree = (Object)adaptor.create(TRUE51);
                    adaptor.addChild(root_0, TRUE51_tree);


                    }
                    break;
                case 2 :
                    // GCL.g:67:11: rule
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_rule_in_conditionliteral375);
                    rule52=rule();

                    state._fsp--;

                    adaptor.addChild(root_0, rule52.getTree());

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
    // GCL.g:69:1: expression : expression2 ( OR expression )? ;
    public final GCLParser.expression_return expression() throws RecognitionException {
        GCLParser.expression_return retval = new GCLParser.expression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token OR54=null;
        GCLParser.expression2_return expression253 = null;

        GCLParser.expression_return expression55 = null;


        Object OR54_tree=null;

        try {
            // GCL.g:70:2: ( expression2 ( OR expression )? )
            // GCL.g:70:4: expression2 ( OR expression )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_expression2_in_expression386);
            expression253=expression2();

            state._fsp--;

            adaptor.addChild(root_0, expression253.getTree());
            // GCL.g:70:16: ( OR expression )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==OR) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // GCL.g:70:17: OR expression
                    {
                    OR54=(Token)match(input,OR,FOLLOW_OR_in_expression389); 
                    OR54_tree = (Object)adaptor.create(OR54);
                    root_0 = (Object)adaptor.becomeRoot(OR54_tree, root_0);

                    pushFollow(FOLLOW_expression_in_expression392);
                    expression55=expression();

                    state._fsp--;

                    adaptor.addChild(root_0, expression55.getTree());

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
    // GCL.g:73:1: expression2 : ( expression_atom ( PLUS | STAR )? | SHARP expression_atom );
    public final GCLParser.expression2_return expression2() throws RecognitionException {
        GCLParser.expression2_return retval = new GCLParser.expression2_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token PLUS57=null;
        Token STAR58=null;
        Token SHARP59=null;
        GCLParser.expression_atom_return expression_atom56 = null;

        GCLParser.expression_atom_return expression_atom60 = null;


        Object PLUS57_tree=null;
        Object STAR58_tree=null;
        Object SHARP59_tree=null;

        try {
            // GCL.g:74:5: ( expression_atom ( PLUS | STAR )? | SHARP expression_atom )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==IDENTIFIER||(LA11_0>=ANY && LA11_0<=OTHER)||LA11_0==33) ) {
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
                    // GCL.g:74:7: expression_atom ( PLUS | STAR )?
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_expression_atom_in_expression2408);
                    expression_atom56=expression_atom();

                    state._fsp--;

                    adaptor.addChild(root_0, expression_atom56.getTree());
                    // GCL.g:74:23: ( PLUS | STAR )?
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
                            // GCL.g:74:24: PLUS
                            {
                            PLUS57=(Token)match(input,PLUS,FOLLOW_PLUS_in_expression2411); 
                            PLUS57_tree = (Object)adaptor.create(PLUS57);
                            root_0 = (Object)adaptor.becomeRoot(PLUS57_tree, root_0);


                            }
                            break;
                        case 2 :
                            // GCL.g:74:32: STAR
                            {
                            STAR58=(Token)match(input,STAR,FOLLOW_STAR_in_expression2416); 
                            STAR58_tree = (Object)adaptor.create(STAR58);
                            root_0 = (Object)adaptor.becomeRoot(STAR58_tree, root_0);


                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // GCL.g:75:7: SHARP expression_atom
                    {
                    root_0 = (Object)adaptor.nil();

                    SHARP59=(Token)match(input,SHARP,FOLLOW_SHARP_in_expression2427); 
                    SHARP59_tree = (Object)adaptor.create(SHARP59);
                    root_0 = (Object)adaptor.becomeRoot(SHARP59_tree, root_0);

                    pushFollow(FOLLOW_expression_atom_in_expression2430);
                    expression_atom60=expression_atom();

                    state._fsp--;

                    adaptor.addChild(root_0, expression_atom60.getTree());

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
    // GCL.g:78:1: expression_atom : ( rule | ANY | OTHER | '(' expression ')' | call );
    public final GCLParser.expression_atom_return expression_atom() throws RecognitionException {
        GCLParser.expression_atom_return retval = new GCLParser.expression_atom_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ANY62=null;
        Token OTHER63=null;
        Token char_literal64=null;
        Token char_literal66=null;
        GCLParser.rule_return rule61 = null;

        GCLParser.expression_return expression65 = null;

        GCLParser.call_return call67 = null;


        Object ANY62_tree=null;
        Object OTHER63_tree=null;
        Object char_literal64_tree=null;
        Object char_literal66_tree=null;

        try {
            // GCL.g:79:2: ( rule | ANY | OTHER | '(' expression ')' | call )
            int alt12=5;
            alt12 = dfa12.predict(input);
            switch (alt12) {
                case 1 :
                    // GCL.g:79:4: rule
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_rule_in_expression_atom444);
                    rule61=rule();

                    state._fsp--;

                    adaptor.addChild(root_0, rule61.getTree());

                    }
                    break;
                case 2 :
                    // GCL.g:80:4: ANY
                    {
                    root_0 = (Object)adaptor.nil();

                    ANY62=(Token)match(input,ANY,FOLLOW_ANY_in_expression_atom449); 
                    ANY62_tree = (Object)adaptor.create(ANY62);
                    adaptor.addChild(root_0, ANY62_tree);


                    }
                    break;
                case 3 :
                    // GCL.g:81:4: OTHER
                    {
                    root_0 = (Object)adaptor.nil();

                    OTHER63=(Token)match(input,OTHER,FOLLOW_OTHER_in_expression_atom454); 
                    OTHER63_tree = (Object)adaptor.create(OTHER63);
                    adaptor.addChild(root_0, OTHER63_tree);


                    }
                    break;
                case 4 :
                    // GCL.g:82:4: '(' expression ')'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal64=(Token)match(input,33,FOLLOW_33_in_expression_atom459); 
                    pushFollow(FOLLOW_expression_in_expression_atom462);
                    expression65=expression();

                    state._fsp--;

                    adaptor.addChild(root_0, expression65.getTree());
                    char_literal66=(Token)match(input,34,FOLLOW_34_in_expression_atom464); 

                    }
                    break;
                case 5 :
                    // GCL.g:83:4: call
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_call_in_expression_atom470);
                    call67=call();

                    state._fsp--;

                    adaptor.addChild(root_0, call67.getTree());

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
    // GCL.g:86:1: call : IDENTIFIER '(' ')' -> ^( CALL IDENTIFIER ) ;
    public final GCLParser.call_return call() throws RecognitionException {
        GCLParser.call_return retval = new GCLParser.call_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token IDENTIFIER68=null;
        Token char_literal69=null;
        Token char_literal70=null;

        Object IDENTIFIER68_tree=null;
        Object char_literal69_tree=null;
        Object char_literal70_tree=null;
        RewriteRuleTokenStream stream_33=new RewriteRuleTokenStream(adaptor,"token 33");
        RewriteRuleTokenStream stream_IDENTIFIER=new RewriteRuleTokenStream(adaptor,"token IDENTIFIER");
        RewriteRuleTokenStream stream_34=new RewriteRuleTokenStream(adaptor,"token 34");

        try {
            // GCL.g:87:2: ( IDENTIFIER '(' ')' -> ^( CALL IDENTIFIER ) )
            // GCL.g:87:4: IDENTIFIER '(' ')'
            {
            IDENTIFIER68=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_call482);  
            stream_IDENTIFIER.add(IDENTIFIER68);

            char_literal69=(Token)match(input,33,FOLLOW_33_in_call484);  
            stream_33.add(char_literal69);

            char_literal70=(Token)match(input,34,FOLLOW_34_in_call486);  
            stream_34.add(char_literal70);



            // AST REWRITE
            // elements: IDENTIFIER
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 87:23: -> ^( CALL IDENTIFIER )
            {
                // GCL.g:87:26: ^( CALL IDENTIFIER )
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
    // GCL.g:89:1: rule : IDENTIFIER ;
    public final GCLParser.rule_return rule() throws RecognitionException {
        GCLParser.rule_return retval = new GCLParser.rule_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token IDENTIFIER71=null;

        Object IDENTIFIER71_tree=null;

        try {
            // GCL.g:89:7: ( IDENTIFIER )
            // GCL.g:89:9: IDENTIFIER
            {
            root_0 = (Object)adaptor.nil();

            IDENTIFIER71=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_rule503); 
            IDENTIFIER71_tree = (Object)adaptor.create(IDENTIFIER71);
            adaptor.addChild(root_0, IDENTIFIER71_tree);


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
    protected DFA12 dfa12 = new DFA12(this);
    static final String DFA1_eotS =
        "\17\uffff";
    static final String DFA1_eofS =
        "\1\1\16\uffff";
    static final String DFA1_minS =
        "\1\7\16\uffff";
    static final String DFA1_maxS =
        "\1\41\16\uffff";
    static final String DFA1_acceptS =
        "\1\uffff\1\3\1\1\1\2\13\uffff";
    static final String DFA1_specialS =
        "\17\uffff}>";
    static final String[] DFA1_transitionS = {
            "\1\2\1\uffff\1\3\1\uffff\5\3\1\uffff\2\3\4\uffff\3\3\7\uffff"+
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
            return "()* loopback of 44:11: ( function | statement )*";
        }
    }
    static final String DFA2_eotS =
        "\16\uffff";
    static final String DFA2_eofS =
        "\16\uffff";
    static final String DFA2_minS =
        "\1\11\15\uffff";
    static final String DFA2_maxS =
        "\1\41\15\uffff";
    static final String DFA2_acceptS =
        "\1\uffff\1\2\1\1\13\uffff";
    static final String DFA2_specialS =
        "\16\uffff}>";
    static final String[] DFA2_transitionS = {
            "\1\2\1\uffff\5\2\1\uffff\2\2\4\uffff\3\2\6\uffff\1\1\1\2",
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
            return "()* loopback of 46:13: ( statement )*";
        }
    }
    static final String DFA7_eotS =
        "\15\uffff";
    static final String DFA7_eofS =
        "\15\uffff";
    static final String DFA7_minS =
        "\1\11\14\uffff";
    static final String DFA7_maxS =
        "\1\41\14\uffff";
    static final String DFA7_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\4\uffff";
    static final String DFA7_specialS =
        "\15\uffff}>";
    static final String[] DFA7_transitionS = {
            "\1\10\1\uffff\1\1\1\2\1\3\1\4\1\5\1\uffff\1\6\1\7\4\uffff\3"+
            "\10\7\uffff\1\10",
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
            return "54:1: statement : ( ALAP block -> ^( ALAP block ) | WHILE '(' condition ')' DO block -> ^( WHILE condition block ) | DO block WHILE '(' condition ')' -> ^( DO block condition ) | UNTIL '(' condition ')' DO block -> ^( UNTIL condition block ) | TRY block ( ELSE block )? -> ^( TRY ( block )+ ) | IF '(' condition ')' block ( ELSE block )? -> ^( IF condition ( block )+ ) | CHOICE block ( CH_OR block )* -> ^( CHOICE ( block )+ ) | expression ';' -> expression );";
        }
    }
    static final String DFA4_eotS =
        "\21\uffff";
    static final String DFA4_eofS =
        "\1\2\20\uffff";
    static final String DFA4_minS =
        "\1\7\20\uffff";
    static final String DFA4_maxS =
        "\1\41\20\uffff";
    static final String DFA4_acceptS =
        "\1\uffff\1\1\1\2\16\uffff";
    static final String DFA4_specialS =
        "\21\uffff}>";
    static final String[] DFA4_transitionS = {
            "\1\2\1\uffff\1\2\1\uffff\5\2\1\1\2\2\4\uffff\3\2\6\uffff\2\2",
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
            return "59:14: ( ELSE block )?";
        }
    }
    static final String DFA5_eotS =
        "\21\uffff";
    static final String DFA5_eofS =
        "\1\2\20\uffff";
    static final String DFA5_minS =
        "\1\7\20\uffff";
    static final String DFA5_maxS =
        "\1\41\20\uffff";
    static final String DFA5_acceptS =
        "\1\uffff\1\1\1\2\16\uffff";
    static final String DFA5_specialS =
        "\21\uffff}>";
    static final String[] DFA5_transitionS = {
            "\1\2\1\uffff\1\2\1\uffff\5\2\1\1\2\2\4\uffff\3\2\6\uffff\2\2",
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
            return "60:31: ( ELSE block )?";
        }
    }
    static final String DFA6_eotS =
        "\21\uffff";
    static final String DFA6_eofS =
        "\1\1\20\uffff";
    static final String DFA6_minS =
        "\1\7\20\uffff";
    static final String DFA6_maxS =
        "\1\41\20\uffff";
    static final String DFA6_acceptS =
        "\1\uffff\1\2\16\uffff\1\1";
    static final String DFA6_specialS =
        "\21\uffff}>";
    static final String[] DFA6_transitionS = {
            "\1\1\1\uffff\1\1\1\uffff\5\1\1\uffff\2\1\1\20\3\uffff\3\1\6"+
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
            return "()* loopback of 61:20: ( CH_OR block )*";
        }
    }
    static final String DFA12_eotS =
        "\13\uffff";
    static final String DFA12_eofS =
        "\13\uffff";
    static final String DFA12_minS =
        "\1\11\1\12\11\uffff";
    static final String DFA12_maxS =
        "\1\41\1\43\11\uffff";
    static final String DFA12_acceptS =
        "\2\uffff\1\2\1\3\1\4\1\5\1\1\4\uffff";
    static final String DFA12_specialS =
        "\13\uffff}>";
    static final String[] DFA12_transitionS = {
            "\1\1\16\uffff\1\2\1\3\7\uffff\1\4",
            "\1\6\12\uffff\2\6\12\uffff\1\5\2\6",
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

    static final short[] DFA12_eot = DFA.unpackEncodedString(DFA12_eotS);
    static final short[] DFA12_eof = DFA.unpackEncodedString(DFA12_eofS);
    static final char[] DFA12_min = DFA.unpackEncodedStringToUnsignedChars(DFA12_minS);
    static final char[] DFA12_max = DFA.unpackEncodedStringToUnsignedChars(DFA12_maxS);
    static final short[] DFA12_accept = DFA.unpackEncodedString(DFA12_acceptS);
    static final short[] DFA12_special = DFA.unpackEncodedString(DFA12_specialS);
    static final short[][] DFA12_transition;

    static {
        int numStates = DFA12_transitionS.length;
        DFA12_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA12_transition[i] = DFA.unpackEncodedString(DFA12_transitionS[i]);
        }
    }

    class DFA12 extends DFA {

        public DFA12(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 12;
            this.eot = DFA12_eot;
            this.eof = DFA12_eof;
            this.min = DFA12_min;
            this.max = DFA12_max;
            this.accept = DFA12_accept;
            this.special = DFA12_special;
            this.transition = DFA12_transition;
        }
        public String getDescription() {
            return "78:1: expression_atom : ( rule | ANY | OTHER | '(' expression ')' | call );";
        }
    }
 

    public static final BitSet FOLLOW_function_in_program77 = new BitSet(new long[]{0x000000020386FA82L});
    public static final BitSet FOLLOW_statement_in_program79 = new BitSet(new long[]{0x000000020386FA82L});
    public static final BitSet FOLLOW_31_in_block109 = new BitSet(new long[]{0x000000030386FA80L});
    public static final BitSet FOLLOW_statement_in_block111 = new BitSet(new long[]{0x000000030386FA80L});
    public static final BitSet FOLLOW_32_in_block115 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_function132 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_IDENTIFIER_in_function134 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_33_in_function136 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_34_in_function138 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_block_in_function140 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionliteral_in_condition159 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_OR_in_condition162 = new BitSet(new long[]{0x0000000000100200L});
    public static final BitSet FOLLOW_condition_in_condition165 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALAP_in_statement180 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_block_in_statement182 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHILE_in_statement195 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_33_in_statement197 = new BitSet(new long[]{0x0000000000100200L});
    public static final BitSet FOLLOW_condition_in_statement199 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_34_in_statement201 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_DO_in_statement203 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_block_in_statement205 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DO_in_statement220 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_block_in_statement222 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_WHILE_in_statement224 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_33_in_statement226 = new BitSet(new long[]{0x0000000000100200L});
    public static final BitSet FOLLOW_condition_in_statement228 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_34_in_statement230 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNTIL_in_statement245 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_33_in_statement247 = new BitSet(new long[]{0x0000000000100200L});
    public static final BitSet FOLLOW_condition_in_statement249 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_34_in_statement251 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_DO_in_statement253 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_block_in_statement255 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRY_in_statement270 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_block_in_statement272 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_ELSE_in_statement275 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_block_in_statement277 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IF_in_statement293 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_33_in_statement295 = new BitSet(new long[]{0x0000000000100200L});
    public static final BitSet FOLLOW_condition_in_statement297 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_34_in_statement299 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_block_in_statement301 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_ELSE_in_statement304 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_block_in_statement306 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CHOICE_in_statement327 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_block_in_statement329 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_CH_OR_in_statement332 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_block_in_statement334 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_expression_in_statement350 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_statement352 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_conditionliteral371 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_conditionliteral375 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression2_in_expression386 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_OR_in_expression389 = new BitSet(new long[]{0x000000020386FA80L});
    public static final BitSet FOLLOW_expression_in_expression392 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_atom_in_expression2408 = new BitSet(new long[]{0x0000000000600002L});
    public static final BitSet FOLLOW_PLUS_in_expression2411 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STAR_in_expression2416 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SHARP_in_expression2427 = new BitSet(new long[]{0x0000000203000200L});
    public static final BitSet FOLLOW_expression_atom_in_expression2430 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_expression_atom444 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_expression_atom449 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_expression_atom454 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_expression_atom459 = new BitSet(new long[]{0x000000060386FA80L});
    public static final BitSet FOLLOW_expression_in_expression_atom462 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_34_in_expression_atom464 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_expression_atom470 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_call482 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_33_in_call484 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_34_in_call486 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_rule503 = new BitSet(new long[]{0x0000000000000002L});

}