// $ANTLR 3.1b1 GCL.g 2010-01-25 11:27:39

package groove.control.parse;
import groove.control.*;
import java.util.LinkedList;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


import org.antlr.runtime.tree.*;

@SuppressWarnings("all")              
public class GCLParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "PROGRAM", "BLOCK", "FUNCTIONS", "FUNCTION", "CALL", "DO", "VAR", "PARAM", "IDENTIFIER", "OR", "ALAP", "WHILE", "UNTIL", "TRY", "ELSE", "IF", "CHOICE", "CH_OR", "TRUE", "PLUS", "STAR", "SHARP", "ANY", "OTHER", "NODE_TYPE", "COMMA", "OUT", "DONT_CARE", "AND", "DOT", "NOT", "ML_COMMENT", "SL_COMMENT", "WS", "'{'", "'}'", "'('", "')'", "';'"
    };
    public static final int T__42=42;
    public static final int FUNCTION=7;
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
    public static final int ML_COMMENT=35;
    public static final int DONT_CARE=31;
    public static final int WS=37;
    public static final int ANY=26;
    public static final int OUT=30;
    public static final int COMMA=29;
    public static final int T__38=38;
    public static final int UNTIL=16;
    public static final int T__39=39;
    public static final int IDENTIFIER=12;
    public static final int BLOCK=5;
    public static final int SL_COMMENT=36;
    public static final int OR=13;
    public static final int CH_OR=21;
    public static final int PLUS=23;
    public static final int PROGRAM=4;
    public static final int VAR=10;
    public static final int CALL=8;
    public static final int DOT=33;
    public static final int CHOICE=20;

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
    // GCL.g:48:1: program : ( function | statement )* -> ^( PROGRAM ^( FUNCTIONS ( function )* ) ^( BLOCK ( statement )* ) ) ;
    public final GCLParser.program_return program() throws RecognitionException {
        GCLParser.program_return retval = new GCLParser.program_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        GCLParser.function_return function1 = null;

        GCLParser.statement_return statement2 = null;


        RewriteRuleSubtreeStream stream_statement=new RewriteRuleSubtreeStream(adaptor,"rule statement");
        RewriteRuleSubtreeStream stream_function=new RewriteRuleSubtreeStream(adaptor,"rule function");
        try {
            // GCL.g:48:9: ( ( function | statement )* -> ^( PROGRAM ^( FUNCTIONS ( function )* ) ^( BLOCK ( statement )* ) ) )
            // GCL.g:48:11: ( function | statement )*
            {
            // GCL.g:48:11: ( function | statement )*
            loop1:
            do {
                int alt1=3;
                alt1 = dfa1.predict(input);
                switch (alt1) {
            	case 1 :
            	    // GCL.g:48:12: function
            	    {
            	    pushFollow(FOLLOW_function_in_program89);
            	    function1=function();

            	    state._fsp--;

            	    stream_function.add(function1.getTree());

            	    }
            	    break;
            	case 2 :
            	    // GCL.g:48:21: statement
            	    {
            	    pushFollow(FOLLOW_statement_in_program91);
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
            // 48:33: -> ^( PROGRAM ^( FUNCTIONS ( function )* ) ^( BLOCK ( statement )* ) )
            {
                // GCL.g:48:36: ^( PROGRAM ^( FUNCTIONS ( function )* ) ^( BLOCK ( statement )* ) )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PROGRAM, "PROGRAM"), root_1);

                // GCL.g:48:46: ^( FUNCTIONS ( function )* )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(FUNCTIONS, "FUNCTIONS"), root_2);

                // GCL.g:48:58: ( function )*
                while ( stream_function.hasNext() ) {
                    adaptor.addChild(root_2, stream_function.nextTree());

                }
                stream_function.reset();

                adaptor.addChild(root_1, root_2);
                }
                // GCL.g:48:69: ^( BLOCK ( statement )* )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(BLOCK, "BLOCK"), root_2);

                // GCL.g:48:77: ( statement )*
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
    // GCL.g:50:1: block : '{' ( statement )* '}' -> ^( BLOCK ( statement )* ) ;
    public final GCLParser.block_return block() throws RecognitionException {
        GCLParser.block_return retval = new GCLParser.block_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal3=null;
        Token char_literal5=null;
        GCLParser.statement_return statement4 = null;


        Object char_literal3_tree=null;
        Object char_literal5_tree=null;
        RewriteRuleTokenStream stream_39=new RewriteRuleTokenStream(adaptor,"token 39");
        RewriteRuleTokenStream stream_38=new RewriteRuleTokenStream(adaptor,"token 38");
        RewriteRuleSubtreeStream stream_statement=new RewriteRuleSubtreeStream(adaptor,"rule statement");
        try {
            // GCL.g:50:7: ( '{' ( statement )* '}' -> ^( BLOCK ( statement )* ) )
            // GCL.g:50:9: '{' ( statement )* '}'
            {
            char_literal3=(Token)match(input,38,FOLLOW_38_in_block121);  
            stream_38.add(char_literal3);

            // GCL.g:50:13: ( statement )*
            loop2:
            do {
                int alt2=2;
                alt2 = dfa2.predict(input);
                switch (alt2) {
            	case 1 :
            	    // GCL.g:50:13: statement
            	    {
            	    pushFollow(FOLLOW_statement_in_block123);
            	    statement4=statement();

            	    state._fsp--;

            	    stream_statement.add(statement4.getTree());

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            char_literal5=(Token)match(input,39,FOLLOW_39_in_block127);  
            stream_39.add(char_literal5);



            // AST REWRITE
            // elements: statement
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 50:29: -> ^( BLOCK ( statement )* )
            {
                // GCL.g:50:32: ^( BLOCK ( statement )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(BLOCK, "BLOCK"), root_1);

                // GCL.g:50:40: ( statement )*
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
    // GCL.g:52:1: function : FUNCTION IDENTIFIER '(' ')' block -> ^( FUNCTION IDENTIFIER block ) ;
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
        RewriteRuleTokenStream stream_41=new RewriteRuleTokenStream(adaptor,"token 41");
        RewriteRuleTokenStream stream_40=new RewriteRuleTokenStream(adaptor,"token 40");
        RewriteRuleTokenStream stream_IDENTIFIER=new RewriteRuleTokenStream(adaptor,"token IDENTIFIER");
        RewriteRuleSubtreeStream stream_block=new RewriteRuleSubtreeStream(adaptor,"rule block");
        try {
            // GCL.g:52:10: ( FUNCTION IDENTIFIER '(' ')' block -> ^( FUNCTION IDENTIFIER block ) )
            // GCL.g:52:12: FUNCTION IDENTIFIER '(' ')' block
            {
            FUNCTION6=(Token)match(input,FUNCTION,FOLLOW_FUNCTION_in_function144);  
            stream_FUNCTION.add(FUNCTION6);

            IDENTIFIER7=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_function146);  
            stream_IDENTIFIER.add(IDENTIFIER7);

            char_literal8=(Token)match(input,40,FOLLOW_40_in_function148);  
            stream_40.add(char_literal8);

            char_literal9=(Token)match(input,41,FOLLOW_41_in_function150);  
            stream_41.add(char_literal9);

            pushFollow(FOLLOW_block_in_function152);
            block10=block();

            state._fsp--;

            stream_block.add(block10.getTree());


            // AST REWRITE
            // elements: FUNCTION, block, IDENTIFIER
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 52:46: -> ^( FUNCTION IDENTIFIER block )
            {
                // GCL.g:52:49: ^( FUNCTION IDENTIFIER block )
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
    // GCL.g:54:1: condition : conditionliteral ( OR condition )? ;
    public final GCLParser.condition_return condition() throws RecognitionException {
        GCLParser.condition_return retval = new GCLParser.condition_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token OR12=null;
        GCLParser.conditionliteral_return conditionliteral11 = null;

        GCLParser.condition_return condition13 = null;


        Object OR12_tree=null;

        try {
            // GCL.g:55:2: ( conditionliteral ( OR condition )? )
            // GCL.g:55:4: conditionliteral ( OR condition )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_conditionliteral_in_condition171);
            conditionliteral11=conditionliteral();

            state._fsp--;

            adaptor.addChild(root_0, conditionliteral11.getTree());
            // GCL.g:55:21: ( OR condition )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==OR) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // GCL.g:55:22: OR condition
                    {
                    OR12=(Token)match(input,OR,FOLLOW_OR_in_condition174); 
                    OR12_tree = (Object)adaptor.create(OR12);
                    root_0 = (Object)adaptor.becomeRoot(OR12_tree, root_0);

                    pushFollow(FOLLOW_condition_in_condition177);
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
    // GCL.g:58:1: statement : ( ALAP block -> ^( ALAP block ) | WHILE '(' condition ')' ( DO )? block -> ^( WHILE condition block ) | UNTIL '(' condition ')' ( DO )? block -> ^( UNTIL condition block ) | DO block WHILE '(' condition ')' -> ^( DO block condition ) | TRY block ( ELSE block )? -> ^( TRY ( block )+ ) | IF '(' condition ')' block ( ELSE block )? -> ^( IF condition ( block )+ ) | CHOICE block ( CH_OR block )* -> ^( CHOICE ( block )+ ) | expression ';' -> expression | var_declaration ';' -> var_declaration );
    public final GCLParser.statement_return statement() throws RecognitionException {
        GCLParser.statement_return retval = new GCLParser.statement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ALAP14=null;
        Token WHILE16=null;
        Token char_literal17=null;
        Token char_literal19=null;
        Token DO20=null;
        Token UNTIL22=null;
        Token char_literal23=null;
        Token char_literal25=null;
        Token DO26=null;
        Token DO28=null;
        Token WHILE30=null;
        Token char_literal31=null;
        Token char_literal33=null;
        Token TRY34=null;
        Token ELSE36=null;
        Token IF38=null;
        Token char_literal39=null;
        Token char_literal41=null;
        Token ELSE43=null;
        Token CHOICE45=null;
        Token CH_OR47=null;
        Token char_literal50=null;
        Token char_literal52=null;
        GCLParser.block_return block15 = null;

        GCLParser.condition_return condition18 = null;

        GCLParser.block_return block21 = null;

        GCLParser.condition_return condition24 = null;

        GCLParser.block_return block27 = null;

        GCLParser.block_return block29 = null;

        GCLParser.condition_return condition32 = null;

        GCLParser.block_return block35 = null;

        GCLParser.block_return block37 = null;

        GCLParser.condition_return condition40 = null;

        GCLParser.block_return block42 = null;

        GCLParser.block_return block44 = null;

        GCLParser.block_return block46 = null;

        GCLParser.block_return block48 = null;

        GCLParser.expression_return expression49 = null;

        GCLParser.var_declaration_return var_declaration51 = null;


        Object ALAP14_tree=null;
        Object WHILE16_tree=null;
        Object char_literal17_tree=null;
        Object char_literal19_tree=null;
        Object DO20_tree=null;
        Object UNTIL22_tree=null;
        Object char_literal23_tree=null;
        Object char_literal25_tree=null;
        Object DO26_tree=null;
        Object DO28_tree=null;
        Object WHILE30_tree=null;
        Object char_literal31_tree=null;
        Object char_literal33_tree=null;
        Object TRY34_tree=null;
        Object ELSE36_tree=null;
        Object IF38_tree=null;
        Object char_literal39_tree=null;
        Object char_literal41_tree=null;
        Object ELSE43_tree=null;
        Object CHOICE45_tree=null;
        Object CH_OR47_tree=null;
        Object char_literal50_tree=null;
        Object char_literal52_tree=null;
        RewriteRuleTokenStream stream_DO=new RewriteRuleTokenStream(adaptor,"token DO");
        RewriteRuleTokenStream stream_42=new RewriteRuleTokenStream(adaptor,"token 42");
        RewriteRuleTokenStream stream_41=new RewriteRuleTokenStream(adaptor,"token 41");
        RewriteRuleTokenStream stream_WHILE=new RewriteRuleTokenStream(adaptor,"token WHILE");
        RewriteRuleTokenStream stream_40=new RewriteRuleTokenStream(adaptor,"token 40");
        RewriteRuleTokenStream stream_ALAP=new RewriteRuleTokenStream(adaptor,"token ALAP");
        RewriteRuleTokenStream stream_UNTIL=new RewriteRuleTokenStream(adaptor,"token UNTIL");
        RewriteRuleTokenStream stream_CHOICE=new RewriteRuleTokenStream(adaptor,"token CHOICE");
        RewriteRuleTokenStream stream_TRY=new RewriteRuleTokenStream(adaptor,"token TRY");
        RewriteRuleTokenStream stream_IF=new RewriteRuleTokenStream(adaptor,"token IF");
        RewriteRuleTokenStream stream_ELSE=new RewriteRuleTokenStream(adaptor,"token ELSE");
        RewriteRuleTokenStream stream_CH_OR=new RewriteRuleTokenStream(adaptor,"token CH_OR");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        RewriteRuleSubtreeStream stream_condition=new RewriteRuleSubtreeStream(adaptor,"rule condition");
        RewriteRuleSubtreeStream stream_block=new RewriteRuleSubtreeStream(adaptor,"rule block");
        RewriteRuleSubtreeStream stream_var_declaration=new RewriteRuleSubtreeStream(adaptor,"rule var_declaration");
        try {
            // GCL.g:59:2: ( ALAP block -> ^( ALAP block ) | WHILE '(' condition ')' ( DO )? block -> ^( WHILE condition block ) | UNTIL '(' condition ')' ( DO )? block -> ^( UNTIL condition block ) | DO block WHILE '(' condition ')' -> ^( DO block condition ) | TRY block ( ELSE block )? -> ^( TRY ( block )+ ) | IF '(' condition ')' block ( ELSE block )? -> ^( IF condition ( block )+ ) | CHOICE block ( CH_OR block )* -> ^( CHOICE ( block )+ ) | expression ';' -> expression | var_declaration ';' -> var_declaration )
            int alt9=9;
            alt9 = dfa9.predict(input);
            switch (alt9) {
                case 1 :
                    // GCL.g:59:4: ALAP block
                    {
                    ALAP14=(Token)match(input,ALAP,FOLLOW_ALAP_in_statement192);  
                    stream_ALAP.add(ALAP14);

                    pushFollow(FOLLOW_block_in_statement194);
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
                    // 59:15: -> ^( ALAP block )
                    {
                        // GCL.g:59:18: ^( ALAP block )
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
                    // GCL.g:60:4: WHILE '(' condition ')' ( DO )? block
                    {
                    WHILE16=(Token)match(input,WHILE,FOLLOW_WHILE_in_statement207);  
                    stream_WHILE.add(WHILE16);

                    char_literal17=(Token)match(input,40,FOLLOW_40_in_statement209);  
                    stream_40.add(char_literal17);

                    pushFollow(FOLLOW_condition_in_statement211);
                    condition18=condition();

                    state._fsp--;

                    stream_condition.add(condition18.getTree());
                    char_literal19=(Token)match(input,41,FOLLOW_41_in_statement213);  
                    stream_41.add(char_literal19);

                    // GCL.g:60:28: ( DO )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==DO) ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // GCL.g:60:28: DO
                            {
                            DO20=(Token)match(input,DO,FOLLOW_DO_in_statement215);  
                            stream_DO.add(DO20);


                            }
                            break;

                    }

                    pushFollow(FOLLOW_block_in_statement218);
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
                    // 60:38: -> ^( WHILE condition block )
                    {
                        // GCL.g:60:41: ^( WHILE condition block )
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
                    // GCL.g:61:4: UNTIL '(' condition ')' ( DO )? block
                    {
                    UNTIL22=(Token)match(input,UNTIL,FOLLOW_UNTIL_in_statement233);  
                    stream_UNTIL.add(UNTIL22);

                    char_literal23=(Token)match(input,40,FOLLOW_40_in_statement235);  
                    stream_40.add(char_literal23);

                    pushFollow(FOLLOW_condition_in_statement237);
                    condition24=condition();

                    state._fsp--;

                    stream_condition.add(condition24.getTree());
                    char_literal25=(Token)match(input,41,FOLLOW_41_in_statement239);  
                    stream_41.add(char_literal25);

                    // GCL.g:61:28: ( DO )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0==DO) ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // GCL.g:61:28: DO
                            {
                            DO26=(Token)match(input,DO,FOLLOW_DO_in_statement241);  
                            stream_DO.add(DO26);


                            }
                            break;

                    }

                    pushFollow(FOLLOW_block_in_statement244);
                    block27=block();

                    state._fsp--;

                    stream_block.add(block27.getTree());


                    // AST REWRITE
                    // elements: UNTIL, condition, block
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 61:38: -> ^( UNTIL condition block )
                    {
                        // GCL.g:61:41: ^( UNTIL condition block )
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
                case 4 :
                    // GCL.g:62:4: DO block WHILE '(' condition ')'
                    {
                    DO28=(Token)match(input,DO,FOLLOW_DO_in_statement259);  
                    stream_DO.add(DO28);

                    pushFollow(FOLLOW_block_in_statement261);
                    block29=block();

                    state._fsp--;

                    stream_block.add(block29.getTree());
                    WHILE30=(Token)match(input,WHILE,FOLLOW_WHILE_in_statement263);  
                    stream_WHILE.add(WHILE30);

                    char_literal31=(Token)match(input,40,FOLLOW_40_in_statement265);  
                    stream_40.add(char_literal31);

                    pushFollow(FOLLOW_condition_in_statement267);
                    condition32=condition();

                    state._fsp--;

                    stream_condition.add(condition32.getTree());
                    char_literal33=(Token)match(input,41,FOLLOW_41_in_statement269);  
                    stream_41.add(char_literal33);



                    // AST REWRITE
                    // elements: condition, block, DO
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 62:37: -> ^( DO block condition )
                    {
                        // GCL.g:62:40: ^( DO block condition )
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
                case 5 :
                    // GCL.g:63:4: TRY block ( ELSE block )?
                    {
                    TRY34=(Token)match(input,TRY,FOLLOW_TRY_in_statement284);  
                    stream_TRY.add(TRY34);

                    pushFollow(FOLLOW_block_in_statement286);
                    block35=block();

                    state._fsp--;

                    stream_block.add(block35.getTree());
                    // GCL.g:63:14: ( ELSE block )?
                    int alt6=2;
                    alt6 = dfa6.predict(input);
                    switch (alt6) {
                        case 1 :
                            // GCL.g:63:15: ELSE block
                            {
                            ELSE36=(Token)match(input,ELSE,FOLLOW_ELSE_in_statement289);  
                            stream_ELSE.add(ELSE36);

                            pushFollow(FOLLOW_block_in_statement291);
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
                    // 63:28: -> ^( TRY ( block )+ )
                    {
                        // GCL.g:63:31: ^( TRY ( block )+ )
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
                    // GCL.g:64:4: IF '(' condition ')' block ( ELSE block )?
                    {
                    IF38=(Token)match(input,IF,FOLLOW_IF_in_statement307);  
                    stream_IF.add(IF38);

                    char_literal39=(Token)match(input,40,FOLLOW_40_in_statement309);  
                    stream_40.add(char_literal39);

                    pushFollow(FOLLOW_condition_in_statement311);
                    condition40=condition();

                    state._fsp--;

                    stream_condition.add(condition40.getTree());
                    char_literal41=(Token)match(input,41,FOLLOW_41_in_statement313);  
                    stream_41.add(char_literal41);

                    pushFollow(FOLLOW_block_in_statement315);
                    block42=block();

                    state._fsp--;

                    stream_block.add(block42.getTree());
                    // GCL.g:64:31: ( ELSE block )?
                    int alt7=2;
                    alt7 = dfa7.predict(input);
                    switch (alt7) {
                        case 1 :
                            // GCL.g:64:32: ELSE block
                            {
                            ELSE43=(Token)match(input,ELSE,FOLLOW_ELSE_in_statement318);  
                            stream_ELSE.add(ELSE43);

                            pushFollow(FOLLOW_block_in_statement320);
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
                    // 64:45: -> ^( IF condition ( block )+ )
                    {
                        // GCL.g:64:48: ^( IF condition ( block )+ )
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
                    // GCL.g:65:7: CHOICE block ( CH_OR block )*
                    {
                    CHOICE45=(Token)match(input,CHOICE,FOLLOW_CHOICE_in_statement341);  
                    stream_CHOICE.add(CHOICE45);

                    pushFollow(FOLLOW_block_in_statement343);
                    block46=block();

                    state._fsp--;

                    stream_block.add(block46.getTree());
                    // GCL.g:65:20: ( CH_OR block )*
                    loop8:
                    do {
                        int alt8=2;
                        alt8 = dfa8.predict(input);
                        switch (alt8) {
                    	case 1 :
                    	    // GCL.g:65:21: CH_OR block
                    	    {
                    	    CH_OR47=(Token)match(input,CH_OR,FOLLOW_CH_OR_in_statement346);  
                    	    stream_CH_OR.add(CH_OR47);

                    	    pushFollow(FOLLOW_block_in_statement348);
                    	    block48=block();

                    	    state._fsp--;

                    	    stream_block.add(block48.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop8;
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
                    // 65:35: -> ^( CHOICE ( block )+ )
                    {
                        // GCL.g:65:38: ^( CHOICE ( block )+ )
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
                    // GCL.g:66:4: expression ';'
                    {
                    pushFollow(FOLLOW_expression_in_statement364);
                    expression49=expression();

                    state._fsp--;

                    stream_expression.add(expression49.getTree());
                    char_literal50=(Token)match(input,42,FOLLOW_42_in_statement366);  
                    stream_42.add(char_literal50);



                    // AST REWRITE
                    // elements: expression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 66:19: -> expression
                    {
                        adaptor.addChild(root_0, stream_expression.nextTree());

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 9 :
                    // GCL.g:67:4: var_declaration ';'
                    {
                    pushFollow(FOLLOW_var_declaration_in_statement375);
                    var_declaration51=var_declaration();

                    state._fsp--;

                    stream_var_declaration.add(var_declaration51.getTree());
                    char_literal52=(Token)match(input,42,FOLLOW_42_in_statement377);  
                    stream_42.add(char_literal52);



                    // AST REWRITE
                    // elements: var_declaration
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 67:24: -> var_declaration
                    {
                        adaptor.addChild(root_0, stream_var_declaration.nextTree());

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
    // GCL.g:70:1: conditionliteral : ( TRUE | call | rule );
    public final GCLParser.conditionliteral_return conditionliteral() throws RecognitionException {
        GCLParser.conditionliteral_return retval = new GCLParser.conditionliteral_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token TRUE53=null;
        GCLParser.call_return call54 = null;

        GCLParser.rule_return rule55 = null;


        Object TRUE53_tree=null;

        try {
            // GCL.g:71:2: ( TRUE | call | rule )
            int alt10=3;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==TRUE) ) {
                alt10=1;
            }
            else if ( (LA10_0==IDENTIFIER) ) {
                int LA10_2 = input.LA(2);

                if ( (LA10_2==40) ) {
                    alt10=2;
                }
                else if ( (LA10_2==OR||LA10_2==41) ) {
                    alt10=3;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 10, 2, input);

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
                    // GCL.g:71:4: TRUE
                    {
                    root_0 = (Object)adaptor.nil();

                    TRUE53=(Token)match(input,TRUE,FOLLOW_TRUE_in_conditionliteral396); 
                    TRUE53_tree = (Object)adaptor.create(TRUE53);
                    adaptor.addChild(root_0, TRUE53_tree);


                    }
                    break;
                case 2 :
                    // GCL.g:71:11: call
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_call_in_conditionliteral400);
                    call54=call();

                    state._fsp--;

                    adaptor.addChild(root_0, call54.getTree());

                    }
                    break;
                case 3 :
                    // GCL.g:71:18: rule
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_rule_in_conditionliteral404);
                    rule55=rule();

                    state._fsp--;

                    adaptor.addChild(root_0, rule55.getTree());

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
    // GCL.g:73:1: expression : expression2 ( OR expression )? ;
    public final GCLParser.expression_return expression() throws RecognitionException {
        GCLParser.expression_return retval = new GCLParser.expression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token OR57=null;
        GCLParser.expression2_return expression256 = null;

        GCLParser.expression_return expression58 = null;


        Object OR57_tree=null;

        try {
            // GCL.g:74:2: ( expression2 ( OR expression )? )
            // GCL.g:74:4: expression2 ( OR expression )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_expression2_in_expression415);
            expression256=expression2();

            state._fsp--;

            adaptor.addChild(root_0, expression256.getTree());
            // GCL.g:74:16: ( OR expression )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==OR) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // GCL.g:74:17: OR expression
                    {
                    OR57=(Token)match(input,OR,FOLLOW_OR_in_expression418); 
                    OR57_tree = (Object)adaptor.create(OR57);
                    root_0 = (Object)adaptor.becomeRoot(OR57_tree, root_0);

                    pushFollow(FOLLOW_expression_in_expression421);
                    expression58=expression();

                    state._fsp--;

                    adaptor.addChild(root_0, expression58.getTree());

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
    // GCL.g:77:1: expression2 : ( expression_atom ( PLUS | STAR )? | SHARP expression_atom );
    public final GCLParser.expression2_return expression2() throws RecognitionException {
        GCLParser.expression2_return retval = new GCLParser.expression2_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token PLUS60=null;
        Token STAR61=null;
        Token SHARP62=null;
        GCLParser.expression_atom_return expression_atom59 = null;

        GCLParser.expression_atom_return expression_atom63 = null;


        Object PLUS60_tree=null;
        Object STAR61_tree=null;
        Object SHARP62_tree=null;

        try {
            // GCL.g:78:5: ( expression_atom ( PLUS | STAR )? | SHARP expression_atom )
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==IDENTIFIER||(LA13_0>=ANY && LA13_0<=OTHER)||LA13_0==40) ) {
                alt13=1;
            }
            else if ( (LA13_0==SHARP) ) {
                alt13=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // GCL.g:78:7: expression_atom ( PLUS | STAR )?
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_expression_atom_in_expression2437);
                    expression_atom59=expression_atom();

                    state._fsp--;

                    adaptor.addChild(root_0, expression_atom59.getTree());
                    // GCL.g:78:23: ( PLUS | STAR )?
                    int alt12=3;
                    int LA12_0 = input.LA(1);

                    if ( (LA12_0==PLUS) ) {
                        alt12=1;
                    }
                    else if ( (LA12_0==STAR) ) {
                        alt12=2;
                    }
                    switch (alt12) {
                        case 1 :
                            // GCL.g:78:24: PLUS
                            {
                            PLUS60=(Token)match(input,PLUS,FOLLOW_PLUS_in_expression2440); 
                            PLUS60_tree = (Object)adaptor.create(PLUS60);
                            root_0 = (Object)adaptor.becomeRoot(PLUS60_tree, root_0);


                            }
                            break;
                        case 2 :
                            // GCL.g:78:32: STAR
                            {
                            STAR61=(Token)match(input,STAR,FOLLOW_STAR_in_expression2445); 
                            STAR61_tree = (Object)adaptor.create(STAR61);
                            root_0 = (Object)adaptor.becomeRoot(STAR61_tree, root_0);


                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // GCL.g:79:7: SHARP expression_atom
                    {
                    root_0 = (Object)adaptor.nil();

                    SHARP62=(Token)match(input,SHARP,FOLLOW_SHARP_in_expression2456); 
                    SHARP62_tree = (Object)adaptor.create(SHARP62);
                    root_0 = (Object)adaptor.becomeRoot(SHARP62_tree, root_0);

                    pushFollow(FOLLOW_expression_atom_in_expression2459);
                    expression_atom63=expression_atom();

                    state._fsp--;

                    adaptor.addChild(root_0, expression_atom63.getTree());

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
    // GCL.g:82:1: expression_atom : ( ANY | OTHER | rule | '(' expression ')' | call );
    public final GCLParser.expression_atom_return expression_atom() throws RecognitionException {
        GCLParser.expression_atom_return retval = new GCLParser.expression_atom_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ANY64=null;
        Token OTHER65=null;
        Token char_literal67=null;
        Token char_literal69=null;
        GCLParser.rule_return rule66 = null;

        GCLParser.expression_return expression68 = null;

        GCLParser.call_return call70 = null;


        Object ANY64_tree=null;
        Object OTHER65_tree=null;
        Object char_literal67_tree=null;
        Object char_literal69_tree=null;

        try {
            // GCL.g:83:2: ( ANY | OTHER | rule | '(' expression ')' | call )
            int alt14=5;
            alt14 = dfa14.predict(input);
            switch (alt14) {
                case 1 :
                    // GCL.g:83:4: ANY
                    {
                    root_0 = (Object)adaptor.nil();

                    ANY64=(Token)match(input,ANY,FOLLOW_ANY_in_expression_atom473); 
                    ANY64_tree = (Object)adaptor.create(ANY64);
                    adaptor.addChild(root_0, ANY64_tree);


                    }
                    break;
                case 2 :
                    // GCL.g:84:4: OTHER
                    {
                    root_0 = (Object)adaptor.nil();

                    OTHER65=(Token)match(input,OTHER,FOLLOW_OTHER_in_expression_atom478); 
                    OTHER65_tree = (Object)adaptor.create(OTHER65);
                    adaptor.addChild(root_0, OTHER65_tree);


                    }
                    break;
                case 3 :
                    // GCL.g:85:4: rule
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_rule_in_expression_atom483);
                    rule66=rule();

                    state._fsp--;

                    adaptor.addChild(root_0, rule66.getTree());

                    }
                    break;
                case 4 :
                    // GCL.g:86:4: '(' expression ')'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal67=(Token)match(input,40,FOLLOW_40_in_expression_atom488); 
                    pushFollow(FOLLOW_expression_in_expression_atom491);
                    expression68=expression();

                    state._fsp--;

                    adaptor.addChild(root_0, expression68.getTree());
                    char_literal69=(Token)match(input,41,FOLLOW_41_in_expression_atom493); 

                    }
                    break;
                case 5 :
                    // GCL.g:87:4: call
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_call_in_expression_atom499);
                    call70=call();

                    state._fsp--;

                    adaptor.addChild(root_0, call70.getTree());

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
    // GCL.g:90:1: call : IDENTIFIER '(' ( var_list )? ')' -> ^( CALL IDENTIFIER ( var_list )? ) ;
    public final GCLParser.call_return call() throws RecognitionException {
        GCLParser.call_return retval = new GCLParser.call_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token IDENTIFIER71=null;
        Token char_literal72=null;
        Token char_literal74=null;
        GCLParser.var_list_return var_list73 = null;


        Object IDENTIFIER71_tree=null;
        Object char_literal72_tree=null;
        Object char_literal74_tree=null;
        RewriteRuleTokenStream stream_41=new RewriteRuleTokenStream(adaptor,"token 41");
        RewriteRuleTokenStream stream_40=new RewriteRuleTokenStream(adaptor,"token 40");
        RewriteRuleTokenStream stream_IDENTIFIER=new RewriteRuleTokenStream(adaptor,"token IDENTIFIER");
        RewriteRuleSubtreeStream stream_var_list=new RewriteRuleSubtreeStream(adaptor,"rule var_list");
        try {
            // GCL.g:91:2: ( IDENTIFIER '(' ( var_list )? ')' -> ^( CALL IDENTIFIER ( var_list )? ) )
            // GCL.g:91:4: IDENTIFIER '(' ( var_list )? ')'
            {
            IDENTIFIER71=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_call511);  
            stream_IDENTIFIER.add(IDENTIFIER71);

            char_literal72=(Token)match(input,40,FOLLOW_40_in_call513);  
            stream_40.add(char_literal72);

            // GCL.g:91:19: ( var_list )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==IDENTIFIER||(LA15_0>=OUT && LA15_0<=DONT_CARE)) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // GCL.g:91:19: var_list
                    {
                    pushFollow(FOLLOW_var_list_in_call515);
                    var_list73=var_list();

                    state._fsp--;

                    stream_var_list.add(var_list73.getTree());

                    }
                    break;

            }

            char_literal74=(Token)match(input,41,FOLLOW_41_in_call518);  
            stream_41.add(char_literal74);



            // AST REWRITE
            // elements: IDENTIFIER, var_list
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 91:33: -> ^( CALL IDENTIFIER ( var_list )? )
            {
                // GCL.g:91:36: ^( CALL IDENTIFIER ( var_list )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(CALL, "CALL"), root_1);

                adaptor.addChild(root_1, stream_IDENTIFIER.nextNode());
                // GCL.g:91:54: ( var_list )?
                if ( stream_var_list.hasNext() ) {
                    adaptor.addChild(root_1, stream_var_list.nextTree());

                }
                stream_var_list.reset();

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
    // GCL.g:93:1: rule : IDENTIFIER -> ^( CALL IDENTIFIER ) ;
    public final GCLParser.rule_return rule() throws RecognitionException {
        GCLParser.rule_return retval = new GCLParser.rule_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token IDENTIFIER75=null;

        Object IDENTIFIER75_tree=null;
        RewriteRuleTokenStream stream_IDENTIFIER=new RewriteRuleTokenStream(adaptor,"token IDENTIFIER");

        try {
            // GCL.g:93:7: ( IDENTIFIER -> ^( CALL IDENTIFIER ) )
            // GCL.g:93:9: IDENTIFIER
            {
            IDENTIFIER75=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_rule538);  
            stream_IDENTIFIER.add(IDENTIFIER75);



            // AST REWRITE
            // elements: IDENTIFIER
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 93:20: -> ^( CALL IDENTIFIER )
            {
                // GCL.g:93:23: ^( CALL IDENTIFIER )
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
    // $ANTLR end rule

    public static class var_declaration_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start var_declaration
    // GCL.g:95:1: var_declaration : var_type IDENTIFIER ( ',' IDENTIFIER )* -> ( ^( VAR var_type IDENTIFIER ) )+ ;
    public final GCLParser.var_declaration_return var_declaration() throws RecognitionException {
        GCLParser.var_declaration_return retval = new GCLParser.var_declaration_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token IDENTIFIER77=null;
        Token char_literal78=null;
        Token IDENTIFIER79=null;
        GCLParser.var_type_return var_type76 = null;


        Object IDENTIFIER77_tree=null;
        Object char_literal78_tree=null;
        Object IDENTIFIER79_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_IDENTIFIER=new RewriteRuleTokenStream(adaptor,"token IDENTIFIER");
        RewriteRuleSubtreeStream stream_var_type=new RewriteRuleSubtreeStream(adaptor,"rule var_type");
        try {
            // GCL.g:96:2: ( var_type IDENTIFIER ( ',' IDENTIFIER )* -> ( ^( VAR var_type IDENTIFIER ) )+ )
            // GCL.g:96:4: var_type IDENTIFIER ( ',' IDENTIFIER )*
            {
            pushFollow(FOLLOW_var_type_in_var_declaration555);
            var_type76=var_type();

            state._fsp--;

            stream_var_type.add(var_type76.getTree());
            IDENTIFIER77=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_var_declaration557);  
            stream_IDENTIFIER.add(IDENTIFIER77);

            // GCL.g:96:24: ( ',' IDENTIFIER )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==COMMA) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // GCL.g:96:25: ',' IDENTIFIER
            	    {
            	    char_literal78=(Token)match(input,COMMA,FOLLOW_COMMA_in_var_declaration560);  
            	    stream_COMMA.add(char_literal78);

            	    IDENTIFIER79=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_var_declaration562);  
            	    stream_IDENTIFIER.add(IDENTIFIER79);


            	    }
            	    break;

            	default :
            	    break loop16;
                }
            } while (true);



            // AST REWRITE
            // elements: IDENTIFIER, var_type
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 96:42: -> ( ^( VAR var_type IDENTIFIER ) )+
            {
                if ( !(stream_IDENTIFIER.hasNext()||stream_var_type.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_IDENTIFIER.hasNext()||stream_var_type.hasNext() ) {
                    // GCL.g:96:45: ^( VAR var_type IDENTIFIER )
                    {
                    Object root_1 = (Object)adaptor.nil();
                    root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(VAR, "VAR"), root_1);

                    adaptor.addChild(root_1, stream_var_type.nextTree());
                    adaptor.addChild(root_1, stream_IDENTIFIER.nextNode());

                    adaptor.addChild(root_0, root_1);
                    }

                }
                stream_IDENTIFIER.reset();
                stream_var_type.reset();

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
    // $ANTLR end var_declaration

    public static class var_type_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start var_type
    // GCL.g:99:1: var_type : NODE_TYPE ;
    public final GCLParser.var_type_return var_type() throws RecognitionException {
        GCLParser.var_type_return retval = new GCLParser.var_type_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token NODE_TYPE80=null;

        Object NODE_TYPE80_tree=null;

        try {
            // GCL.g:100:2: ( NODE_TYPE )
            // GCL.g:100:4: NODE_TYPE
            {
            root_0 = (Object)adaptor.nil();

            NODE_TYPE80=(Token)match(input,NODE_TYPE,FOLLOW_NODE_TYPE_in_var_type586); 
            NODE_TYPE80_tree = (Object)adaptor.create(NODE_TYPE80);
            adaptor.addChild(root_0, NODE_TYPE80_tree);


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
    // $ANTLR end var_type

    public static class var_list_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start var_list
    // GCL.g:103:1: var_list : variable ( COMMA var_list )? ;
    public final GCLParser.var_list_return var_list() throws RecognitionException {
        GCLParser.var_list_return retval = new GCLParser.var_list_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COMMA82=null;
        GCLParser.variable_return variable81 = null;

        GCLParser.var_list_return var_list83 = null;


        Object COMMA82_tree=null;

        try {
            // GCL.g:104:2: ( variable ( COMMA var_list )? )
            // GCL.g:104:4: variable ( COMMA var_list )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_variable_in_var_list598);
            variable81=variable();

            state._fsp--;

            adaptor.addChild(root_0, variable81.getTree());
            // GCL.g:104:13: ( COMMA var_list )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==COMMA) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // GCL.g:104:14: COMMA var_list
                    {
                    COMMA82=(Token)match(input,COMMA,FOLLOW_COMMA_in_var_list601); 
                    pushFollow(FOLLOW_var_list_in_var_list604);
                    var_list83=var_list();

                    state._fsp--;

                    adaptor.addChild(root_0, var_list83.getTree());

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
    // $ANTLR end var_list

    public static class variable_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start variable
    // GCL.g:107:1: variable : ( OUT IDENTIFIER -> ^( PARAM OUT IDENTIFIER ) | IDENTIFIER -> ^( PARAM IDENTIFIER ) | DONT_CARE -> ^( PARAM DONT_CARE ) );
    public final GCLParser.variable_return variable() throws RecognitionException {
        GCLParser.variable_return retval = new GCLParser.variable_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token OUT84=null;
        Token IDENTIFIER85=null;
        Token IDENTIFIER86=null;
        Token DONT_CARE87=null;

        Object OUT84_tree=null;
        Object IDENTIFIER85_tree=null;
        Object IDENTIFIER86_tree=null;
        Object DONT_CARE87_tree=null;
        RewriteRuleTokenStream stream_DONT_CARE=new RewriteRuleTokenStream(adaptor,"token DONT_CARE");
        RewriteRuleTokenStream stream_OUT=new RewriteRuleTokenStream(adaptor,"token OUT");
        RewriteRuleTokenStream stream_IDENTIFIER=new RewriteRuleTokenStream(adaptor,"token IDENTIFIER");

        try {
            // GCL.g:108:2: ( OUT IDENTIFIER -> ^( PARAM OUT IDENTIFIER ) | IDENTIFIER -> ^( PARAM IDENTIFIER ) | DONT_CARE -> ^( PARAM DONT_CARE ) )
            int alt18=3;
            switch ( input.LA(1) ) {
            case OUT:
                {
                alt18=1;
                }
                break;
            case IDENTIFIER:
                {
                alt18=2;
                }
                break;
            case DONT_CARE:
                {
                alt18=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 18, 0, input);

                throw nvae;
            }

            switch (alt18) {
                case 1 :
                    // GCL.g:108:4: OUT IDENTIFIER
                    {
                    OUT84=(Token)match(input,OUT,FOLLOW_OUT_in_variable618);  
                    stream_OUT.add(OUT84);

                    IDENTIFIER85=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_variable620);  
                    stream_IDENTIFIER.add(IDENTIFIER85);



                    // AST REWRITE
                    // elements: OUT, IDENTIFIER
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 108:19: -> ^( PARAM OUT IDENTIFIER )
                    {
                        // GCL.g:108:22: ^( PARAM OUT IDENTIFIER )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PARAM, "PARAM"), root_1);

                        adaptor.addChild(root_1, stream_OUT.nextNode());
                        adaptor.addChild(root_1, stream_IDENTIFIER.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 2 :
                    // GCL.g:109:4: IDENTIFIER
                    {
                    IDENTIFIER86=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_variable635);  
                    stream_IDENTIFIER.add(IDENTIFIER86);



                    // AST REWRITE
                    // elements: IDENTIFIER
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 109:15: -> ^( PARAM IDENTIFIER )
                    {
                        // GCL.g:109:18: ^( PARAM IDENTIFIER )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PARAM, "PARAM"), root_1);

                        adaptor.addChild(root_1, stream_IDENTIFIER.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 3 :
                    // GCL.g:110:4: DONT_CARE
                    {
                    DONT_CARE87=(Token)match(input,DONT_CARE,FOLLOW_DONT_CARE_in_variable648);  
                    stream_DONT_CARE.add(DONT_CARE87);



                    // AST REWRITE
                    // elements: DONT_CARE
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 110:14: -> ^( PARAM DONT_CARE )
                    {
                        // GCL.g:110:17: ^( PARAM DONT_CARE )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PARAM, "PARAM"), root_1);

                        adaptor.addChild(root_1, stream_DONT_CARE.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

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
    // $ANTLR end variable

    // Delegated rules


    protected DFA1 dfa1 = new DFA1(this);
    protected DFA2 dfa2 = new DFA2(this);
    protected DFA9 dfa9 = new DFA9(this);
    protected DFA6 dfa6 = new DFA6(this);
    protected DFA7 dfa7 = new DFA7(this);
    protected DFA8 dfa8 = new DFA8(this);
    protected DFA14 dfa14 = new DFA14(this);
    static final String DFA1_eotS =
        "\20\uffff";
    static final String DFA1_eofS =
        "\1\1\17\uffff";
    static final String DFA1_minS =
        "\1\7\17\uffff";
    static final String DFA1_maxS =
        "\1\50\17\uffff";
    static final String DFA1_acceptS =
        "\1\uffff\1\3\1\1\1\2\14\uffff";
    static final String DFA1_specialS =
        "\20\uffff}>";
    static final String[] DFA1_transitionS = {
            "\1\2\1\uffff\1\3\2\uffff\1\3\1\uffff\4\3\1\uffff\2\3\4\uffff"+
            "\4\3\13\uffff\1\3",
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
            return "()* loopback of 48:11: ( function | statement )*";
        }
    }
    static final String DFA2_eotS =
        "\17\uffff";
    static final String DFA2_eofS =
        "\17\uffff";
    static final String DFA2_minS =
        "\1\11\16\uffff";
    static final String DFA2_maxS =
        "\1\50\16\uffff";
    static final String DFA2_acceptS =
        "\1\uffff\1\2\1\1\14\uffff";
    static final String DFA2_specialS =
        "\17\uffff}>";
    static final String[] DFA2_transitionS = {
            "\1\2\2\uffff\1\2\1\uffff\4\2\1\uffff\2\2\4\uffff\4\2\12\uffff"+
            "\1\1\1\2",
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
            return "()* loopback of 50:13: ( statement )*";
        }
    }
    static final String DFA9_eotS =
        "\16\uffff";
    static final String DFA9_eofS =
        "\16\uffff";
    static final String DFA9_minS =
        "\1\11\15\uffff";
    static final String DFA9_maxS =
        "\1\50\15\uffff";
    static final String DFA9_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\4\uffff\1\11";
    static final String DFA9_specialS =
        "\16\uffff}>";
    static final String[] DFA9_transitionS = {
            "\1\4\2\uffff\1\10\1\uffff\1\1\1\2\1\3\1\5\1\uffff\1\6\1\7\4"+
            "\uffff\3\10\1\15\13\uffff\1\10",
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

    static final short[] DFA9_eot = DFA.unpackEncodedString(DFA9_eotS);
    static final short[] DFA9_eof = DFA.unpackEncodedString(DFA9_eofS);
    static final char[] DFA9_min = DFA.unpackEncodedStringToUnsignedChars(DFA9_minS);
    static final char[] DFA9_max = DFA.unpackEncodedStringToUnsignedChars(DFA9_maxS);
    static final short[] DFA9_accept = DFA.unpackEncodedString(DFA9_acceptS);
    static final short[] DFA9_special = DFA.unpackEncodedString(DFA9_specialS);
    static final short[][] DFA9_transition;

    static {
        int numStates = DFA9_transitionS.length;
        DFA9_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA9_transition[i] = DFA.unpackEncodedString(DFA9_transitionS[i]);
        }
    }

    class DFA9 extends DFA {

        public DFA9(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 9;
            this.eot = DFA9_eot;
            this.eof = DFA9_eof;
            this.min = DFA9_min;
            this.max = DFA9_max;
            this.accept = DFA9_accept;
            this.special = DFA9_special;
            this.transition = DFA9_transition;
        }
        public String getDescription() {
            return "58:1: statement : ( ALAP block -> ^( ALAP block ) | WHILE '(' condition ')' ( DO )? block -> ^( WHILE condition block ) | UNTIL '(' condition ')' ( DO )? block -> ^( UNTIL condition block ) | DO block WHILE '(' condition ')' -> ^( DO block condition ) | TRY block ( ELSE block )? -> ^( TRY ( block )+ ) | IF '(' condition ')' block ( ELSE block )? -> ^( IF condition ( block )+ ) | CHOICE block ( CH_OR block )* -> ^( CHOICE ( block )+ ) | expression ';' -> expression | var_declaration ';' -> var_declaration );";
        }
    }
    static final String DFA6_eotS =
        "\22\uffff";
    static final String DFA6_eofS =
        "\1\2\21\uffff";
    static final String DFA6_minS =
        "\1\7\21\uffff";
    static final String DFA6_maxS =
        "\1\50\21\uffff";
    static final String DFA6_acceptS =
        "\1\uffff\1\1\1\2\17\uffff";
    static final String DFA6_specialS =
        "\22\uffff}>";
    static final String[] DFA6_transitionS = {
            "\1\2\1\uffff\1\2\2\uffff\1\2\1\uffff\4\2\1\1\2\2\4\uffff\4\2"+
            "\12\uffff\2\2",
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
            return "63:14: ( ELSE block )?";
        }
    }
    static final String DFA7_eotS =
        "\22\uffff";
    static final String DFA7_eofS =
        "\1\2\21\uffff";
    static final String DFA7_minS =
        "\1\7\21\uffff";
    static final String DFA7_maxS =
        "\1\50\21\uffff";
    static final String DFA7_acceptS =
        "\1\uffff\1\1\1\2\17\uffff";
    static final String DFA7_specialS =
        "\22\uffff}>";
    static final String[] DFA7_transitionS = {
            "\1\2\1\uffff\1\2\2\uffff\1\2\1\uffff\4\2\1\1\2\2\4\uffff\4\2"+
            "\12\uffff\2\2",
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
            return "64:31: ( ELSE block )?";
        }
    }
    static final String DFA8_eotS =
        "\22\uffff";
    static final String DFA8_eofS =
        "\1\1\21\uffff";
    static final String DFA8_minS =
        "\1\7\21\uffff";
    static final String DFA8_maxS =
        "\1\50\21\uffff";
    static final String DFA8_acceptS =
        "\1\uffff\1\2\17\uffff\1\1";
    static final String DFA8_specialS =
        "\22\uffff}>";
    static final String[] DFA8_transitionS = {
            "\1\1\1\uffff\1\1\2\uffff\1\1\1\uffff\4\1\1\uffff\2\1\1\21\3"+
            "\uffff\4\1\12\uffff\2\1",
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
            "",
            ""
    };

    static final short[] DFA8_eot = DFA.unpackEncodedString(DFA8_eotS);
    static final short[] DFA8_eof = DFA.unpackEncodedString(DFA8_eofS);
    static final char[] DFA8_min = DFA.unpackEncodedStringToUnsignedChars(DFA8_minS);
    static final char[] DFA8_max = DFA.unpackEncodedStringToUnsignedChars(DFA8_maxS);
    static final short[] DFA8_accept = DFA.unpackEncodedString(DFA8_acceptS);
    static final short[] DFA8_special = DFA.unpackEncodedString(DFA8_specialS);
    static final short[][] DFA8_transition;

    static {
        int numStates = DFA8_transitionS.length;
        DFA8_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA8_transition[i] = DFA.unpackEncodedString(DFA8_transitionS[i]);
        }
    }

    class DFA8 extends DFA {

        public DFA8(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 8;
            this.eot = DFA8_eot;
            this.eof = DFA8_eof;
            this.min = DFA8_min;
            this.max = DFA8_max;
            this.accept = DFA8_accept;
            this.special = DFA8_special;
            this.transition = DFA8_transition;
        }
        public String getDescription() {
            return "()* loopback of 65:20: ( CH_OR block )*";
        }
    }
    static final String DFA14_eotS =
        "\13\uffff";
    static final String DFA14_eofS =
        "\13\uffff";
    static final String DFA14_minS =
        "\1\14\2\uffff\1\15\7\uffff";
    static final String DFA14_maxS =
        "\1\50\2\uffff\1\52\7\uffff";
    static final String DFA14_acceptS =
        "\1\uffff\1\1\1\2\1\uffff\1\4\1\5\1\3\4\uffff";
    static final String DFA14_specialS =
        "\13\uffff}>";
    static final String[] DFA14_transitionS = {
            "\1\3\15\uffff\1\1\1\2\14\uffff\1\4",
            "",
            "",
            "\1\6\11\uffff\2\6\17\uffff\1\5\2\6",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA14_eot = DFA.unpackEncodedString(DFA14_eotS);
    static final short[] DFA14_eof = DFA.unpackEncodedString(DFA14_eofS);
    static final char[] DFA14_min = DFA.unpackEncodedStringToUnsignedChars(DFA14_minS);
    static final char[] DFA14_max = DFA.unpackEncodedStringToUnsignedChars(DFA14_maxS);
    static final short[] DFA14_accept = DFA.unpackEncodedString(DFA14_acceptS);
    static final short[] DFA14_special = DFA.unpackEncodedString(DFA14_specialS);
    static final short[][] DFA14_transition;

    static {
        int numStates = DFA14_transitionS.length;
        DFA14_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA14_transition[i] = DFA.unpackEncodedString(DFA14_transitionS[i]);
        }
    }

    class DFA14 extends DFA {

        public DFA14(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 14;
            this.eot = DFA14_eot;
            this.eof = DFA14_eof;
            this.min = DFA14_min;
            this.max = DFA14_max;
            this.accept = DFA14_accept;
            this.special = DFA14_special;
            this.transition = DFA14_transition;
        }
        public String getDescription() {
            return "82:1: expression_atom : ( ANY | OTHER | rule | '(' expression ')' | call );";
        }
    }
 

    public static final BitSet FOLLOW_function_in_program89 = new BitSet(new long[]{0x000001001E1BD282L});
    public static final BitSet FOLLOW_statement_in_program91 = new BitSet(new long[]{0x000001001E1BD282L});
    public static final BitSet FOLLOW_38_in_block121 = new BitSet(new long[]{0x000001801E1BD280L});
    public static final BitSet FOLLOW_statement_in_block123 = new BitSet(new long[]{0x000001801E1BD280L});
    public static final BitSet FOLLOW_39_in_block127 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_function144 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_function146 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_function148 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_41_in_function150 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_block_in_function152 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionliteral_in_condition171 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_OR_in_condition174 = new BitSet(new long[]{0x000001000C401000L});
    public static final BitSet FOLLOW_condition_in_condition177 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALAP_in_statement192 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_block_in_statement194 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHILE_in_statement207 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_statement209 = new BitSet(new long[]{0x000001000C401000L});
    public static final BitSet FOLLOW_condition_in_statement211 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_41_in_statement213 = new BitSet(new long[]{0x0000004000000200L});
    public static final BitSet FOLLOW_DO_in_statement215 = new BitSet(new long[]{0x0000004000000200L});
    public static final BitSet FOLLOW_block_in_statement218 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNTIL_in_statement233 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_statement235 = new BitSet(new long[]{0x000001000C401000L});
    public static final BitSet FOLLOW_condition_in_statement237 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_41_in_statement239 = new BitSet(new long[]{0x0000004000000200L});
    public static final BitSet FOLLOW_DO_in_statement241 = new BitSet(new long[]{0x0000004000000200L});
    public static final BitSet FOLLOW_block_in_statement244 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DO_in_statement259 = new BitSet(new long[]{0x0000004000000200L});
    public static final BitSet FOLLOW_block_in_statement261 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_WHILE_in_statement263 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_statement265 = new BitSet(new long[]{0x000001000C401000L});
    public static final BitSet FOLLOW_condition_in_statement267 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_41_in_statement269 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRY_in_statement284 = new BitSet(new long[]{0x0000004000000200L});
    public static final BitSet FOLLOW_block_in_statement286 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_ELSE_in_statement289 = new BitSet(new long[]{0x0000004000000200L});
    public static final BitSet FOLLOW_block_in_statement291 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IF_in_statement307 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_statement309 = new BitSet(new long[]{0x000001000C401000L});
    public static final BitSet FOLLOW_condition_in_statement311 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_41_in_statement313 = new BitSet(new long[]{0x0000004000000200L});
    public static final BitSet FOLLOW_block_in_statement315 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_ELSE_in_statement318 = new BitSet(new long[]{0x0000004000000200L});
    public static final BitSet FOLLOW_block_in_statement320 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CHOICE_in_statement341 = new BitSet(new long[]{0x0000004000000200L});
    public static final BitSet FOLLOW_block_in_statement343 = new BitSet(new long[]{0x0000000000200002L});
    public static final BitSet FOLLOW_CH_OR_in_statement346 = new BitSet(new long[]{0x0000004000000200L});
    public static final BitSet FOLLOW_block_in_statement348 = new BitSet(new long[]{0x0000000000200002L});
    public static final BitSet FOLLOW_expression_in_statement364 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_statement366 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_declaration_in_statement375 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_statement377 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_conditionliteral396 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_conditionliteral400 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_conditionliteral404 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression2_in_expression415 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_OR_in_expression418 = new BitSet(new long[]{0x000001000E001000L});
    public static final BitSet FOLLOW_expression_in_expression421 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_atom_in_expression2437 = new BitSet(new long[]{0x0000000001800002L});
    public static final BitSet FOLLOW_PLUS_in_expression2440 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STAR_in_expression2445 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SHARP_in_expression2456 = new BitSet(new long[]{0x000001000C001000L});
    public static final BitSet FOLLOW_expression_atom_in_expression2459 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_expression_atom473 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_expression_atom478 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_expression_atom483 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_expression_atom488 = new BitSet(new long[]{0x000001000E001000L});
    public static final BitSet FOLLOW_expression_in_expression_atom491 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_41_in_expression_atom493 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_expression_atom499 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_call511 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_call513 = new BitSet(new long[]{0x00000200C0001000L});
    public static final BitSet FOLLOW_var_list_in_call515 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_41_in_call518 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_rule538 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_type_in_var_declaration555 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_var_declaration557 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_COMMA_in_var_declaration560 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_var_declaration562 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_NODE_TYPE_in_var_type586 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variable_in_var_list598 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_COMMA_in_var_list601 = new BitSet(new long[]{0x00000000C0001000L});
    public static final BitSet FOLLOW_var_list_in_var_list604 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OUT_in_variable618 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_variable620 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_variable635 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DONT_CARE_in_variable648 = new BitSet(new long[]{0x0000000000000002L});

}