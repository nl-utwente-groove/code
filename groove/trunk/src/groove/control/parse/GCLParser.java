// $ANTLR 3.1b1 GCL.g 2010-04-23 13:02:38

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "PROGRAM", "BLOCK", "FUNCTIONS", "FUNCTION", "CALL", "DO", "VAR", "PARAM", "IDENTIFIER", "OR", "ALAP", "WHILE", "UNTIL", "CHOICE", "CH_OR", "IF", "ELSE", "TRY", "TRUE", "PLUS", "STAR", "SHARP", "ANY", "OTHER", "NODE_TYPE", "BOOL_TYPE", "STRING_TYPE", "INT_TYPE", "REAL_TYPE", "COMMA", "OUT", "DONT_CARE", "FALSE", "STRING", "INT", "REAL", "QUOTE", "AND", "DOT", "NOT", "ML_COMMENT", "SL_COMMENT", "WS", "'{'", "'}'", "'('", "')'", "';'"
    };
    public static final int FUNCTION=7;
    public static final int STAR=24;
    public static final int FUNCTIONS=6;
    public static final int WHILE=15;
    public static final int BOOL_TYPE=29;
    public static final int NODE_TYPE=28;
    public static final int DO=9;
    public static final int PARAM=11;
    public static final int NOT=43;
    public static final int ALAP=14;
    public static final int AND=41;
    public static final int EOF=-1;
    public static final int IF=19;
    public static final int ML_COMMENT=44;
    public static final int QUOTE=40;
    public static final int T__51=51;
    public static final int COMMA=33;
    public static final int IDENTIFIER=12;
    public static final int CH_OR=18;
    public static final int PLUS=23;
    public static final int VAR=10;
    public static final int DOT=42;
    public static final int T__50=50;
    public static final int CHOICE=17;
    public static final int T__47=47;
    public static final int SHARP=25;
    public static final int OTHER=27;
    public static final int T__48=48;
    public static final int T__49=49;
    public static final int ELSE=20;
    public static final int INT=38;
    public static final int INT_TYPE=31;
    public static final int TRUE=22;
    public static final int TRY=21;
    public static final int REAL=39;
    public static final int REAL_TYPE=32;
    public static final int DONT_CARE=35;
    public static final int WS=46;
    public static final int ANY=26;
    public static final int OUT=34;
    public static final int UNTIL=16;
    public static final int BLOCK=5;
    public static final int STRING_TYPE=30;
    public static final int SL_COMMENT=45;
    public static final int OR=13;
    public static final int PROGRAM=4;
    public static final int CALL=8;
    public static final int FALSE=36;
    public static final int STRING=37;

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
            // elements: statement, function
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
        RewriteRuleTokenStream stream_48=new RewriteRuleTokenStream(adaptor,"token 48");
        RewriteRuleTokenStream stream_47=new RewriteRuleTokenStream(adaptor,"token 47");
        RewriteRuleSubtreeStream stream_statement=new RewriteRuleSubtreeStream(adaptor,"rule statement");
        try {
            // GCL.g:50:7: ( '{' ( statement )* '}' -> ^( BLOCK ( statement )* ) )
            // GCL.g:50:9: '{' ( statement )* '}'
            {
            char_literal3=(Token)match(input,47,FOLLOW_47_in_block121);  
            stream_47.add(char_literal3);

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

            char_literal5=(Token)match(input,48,FOLLOW_48_in_block127);  
            stream_48.add(char_literal5);



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
        RewriteRuleTokenStream stream_49=new RewriteRuleTokenStream(adaptor,"token 49");
        RewriteRuleTokenStream stream_FUNCTION=new RewriteRuleTokenStream(adaptor,"token FUNCTION");
        RewriteRuleTokenStream stream_IDENTIFIER=new RewriteRuleTokenStream(adaptor,"token IDENTIFIER");
        RewriteRuleTokenStream stream_50=new RewriteRuleTokenStream(adaptor,"token 50");
        RewriteRuleSubtreeStream stream_block=new RewriteRuleSubtreeStream(adaptor,"rule block");
        try {
            // GCL.g:52:10: ( FUNCTION IDENTIFIER '(' ')' block -> ^( FUNCTION IDENTIFIER block ) )
            // GCL.g:52:12: FUNCTION IDENTIFIER '(' ')' block
            {
            FUNCTION6=(Token)match(input,FUNCTION,FOLLOW_FUNCTION_in_function144);  
            stream_FUNCTION.add(FUNCTION6);

            IDENTIFIER7=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_function146);  
            stream_IDENTIFIER.add(IDENTIFIER7);

            char_literal8=(Token)match(input,49,FOLLOW_49_in_function148);  
            stream_49.add(char_literal8);

            char_literal9=(Token)match(input,50,FOLLOW_50_in_function150);  
            stream_50.add(char_literal9);

            pushFollow(FOLLOW_block_in_function152);
            block10=block();

            state._fsp--;

            stream_block.add(block10.getTree());


            // AST REWRITE
            // elements: IDENTIFIER, FUNCTION, block
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
    // GCL.g:58:1: statement : ( ALAP block -> ^( ALAP block ) | WHILE '(' condition ')' ( DO )? block -> ^( WHILE condition block ) | UNTIL '(' condition ')' ( DO )? block -> ^( UNTIL condition block ) | DO block WHILE '(' condition ')' -> ^( DO block condition ) | ifstatement | CHOICE block ( CH_OR block )* -> ^( CHOICE ( block )+ ) | expression ';' -> expression | var_declaration ';' -> var_declaration );
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
        Token CHOICE35=null;
        Token CH_OR37=null;
        Token char_literal40=null;
        Token char_literal42=null;
        GCLParser.block_return block15 = null;

        GCLParser.condition_return condition18 = null;

        GCLParser.block_return block21 = null;

        GCLParser.condition_return condition24 = null;

        GCLParser.block_return block27 = null;

        GCLParser.block_return block29 = null;

        GCLParser.condition_return condition32 = null;

        GCLParser.ifstatement_return ifstatement34 = null;

        GCLParser.block_return block36 = null;

        GCLParser.block_return block38 = null;

        GCLParser.expression_return expression39 = null;

        GCLParser.var_declaration_return var_declaration41 = null;


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
        Object CHOICE35_tree=null;
        Object CH_OR37_tree=null;
        Object char_literal40_tree=null;
        Object char_literal42_tree=null;
        RewriteRuleTokenStream stream_49=new RewriteRuleTokenStream(adaptor,"token 49");
        RewriteRuleTokenStream stream_DO=new RewriteRuleTokenStream(adaptor,"token DO");
        RewriteRuleTokenStream stream_WHILE=new RewriteRuleTokenStream(adaptor,"token WHILE");
        RewriteRuleTokenStream stream_ALAP=new RewriteRuleTokenStream(adaptor,"token ALAP");
        RewriteRuleTokenStream stream_51=new RewriteRuleTokenStream(adaptor,"token 51");
        RewriteRuleTokenStream stream_UNTIL=new RewriteRuleTokenStream(adaptor,"token UNTIL");
        RewriteRuleTokenStream stream_CHOICE=new RewriteRuleTokenStream(adaptor,"token CHOICE");
        RewriteRuleTokenStream stream_CH_OR=new RewriteRuleTokenStream(adaptor,"token CH_OR");
        RewriteRuleTokenStream stream_50=new RewriteRuleTokenStream(adaptor,"token 50");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        RewriteRuleSubtreeStream stream_condition=new RewriteRuleSubtreeStream(adaptor,"rule condition");
        RewriteRuleSubtreeStream stream_block=new RewriteRuleSubtreeStream(adaptor,"rule block");
        RewriteRuleSubtreeStream stream_var_declaration=new RewriteRuleSubtreeStream(adaptor,"rule var_declaration");
        try {
            // GCL.g:59:2: ( ALAP block -> ^( ALAP block ) | WHILE '(' condition ')' ( DO )? block -> ^( WHILE condition block ) | UNTIL '(' condition ')' ( DO )? block -> ^( UNTIL condition block ) | DO block WHILE '(' condition ')' -> ^( DO block condition ) | ifstatement | CHOICE block ( CH_OR block )* -> ^( CHOICE ( block )+ ) | expression ';' -> expression | var_declaration ';' -> var_declaration )
            int alt7=8;
            alt7 = dfa7.predict(input);
            switch (alt7) {
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
                    // elements: block, ALAP
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

                    char_literal17=(Token)match(input,49,FOLLOW_49_in_statement209);  
                    stream_49.add(char_literal17);

                    pushFollow(FOLLOW_condition_in_statement211);
                    condition18=condition();

                    state._fsp--;

                    stream_condition.add(condition18.getTree());
                    char_literal19=(Token)match(input,50,FOLLOW_50_in_statement213);  
                    stream_50.add(char_literal19);

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
                    // elements: condition, WHILE, block
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

                    char_literal23=(Token)match(input,49,FOLLOW_49_in_statement235);  
                    stream_49.add(char_literal23);

                    pushFollow(FOLLOW_condition_in_statement237);
                    condition24=condition();

                    state._fsp--;

                    stream_condition.add(condition24.getTree());
                    char_literal25=(Token)match(input,50,FOLLOW_50_in_statement239);  
                    stream_50.add(char_literal25);

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
                    // elements: block, condition, UNTIL
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

                    char_literal31=(Token)match(input,49,FOLLOW_49_in_statement265);  
                    stream_49.add(char_literal31);

                    pushFollow(FOLLOW_condition_in_statement267);
                    condition32=condition();

                    state._fsp--;

                    stream_condition.add(condition32.getTree());
                    char_literal33=(Token)match(input,50,FOLLOW_50_in_statement269);  
                    stream_50.add(char_literal33);



                    // AST REWRITE
                    // elements: DO, block, condition
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
                    // GCL.g:63:4: ifstatement
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_ifstatement_in_statement284);
                    ifstatement34=ifstatement();

                    state._fsp--;

                    adaptor.addChild(root_0, ifstatement34.getTree());

                    }
                    break;
                case 6 :
                    // GCL.g:64:7: CHOICE block ( CH_OR block )*
                    {
                    CHOICE35=(Token)match(input,CHOICE,FOLLOW_CHOICE_in_statement292);  
                    stream_CHOICE.add(CHOICE35);

                    pushFollow(FOLLOW_block_in_statement294);
                    block36=block();

                    state._fsp--;

                    stream_block.add(block36.getTree());
                    // GCL.g:64:20: ( CH_OR block )*
                    loop6:
                    do {
                        int alt6=2;
                        alt6 = dfa6.predict(input);
                        switch (alt6) {
                    	case 1 :
                    	    // GCL.g:64:21: CH_OR block
                    	    {
                    	    CH_OR37=(Token)match(input,CH_OR,FOLLOW_CH_OR_in_statement297);  
                    	    stream_CH_OR.add(CH_OR37);

                    	    pushFollow(FOLLOW_block_in_statement299);
                    	    block38=block();

                    	    state._fsp--;

                    	    stream_block.add(block38.getTree());

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
                    // 64:35: -> ^( CHOICE ( block )+ )
                    {
                        // GCL.g:64:38: ^( CHOICE ( block )+ )
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
                    // GCL.g:65:4: expression ';'
                    {
                    pushFollow(FOLLOW_expression_in_statement315);
                    expression39=expression();

                    state._fsp--;

                    stream_expression.add(expression39.getTree());
                    char_literal40=(Token)match(input,51,FOLLOW_51_in_statement317);  
                    stream_51.add(char_literal40);



                    // AST REWRITE
                    // elements: expression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 65:19: -> expression
                    {
                        adaptor.addChild(root_0, stream_expression.nextTree());

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 8 :
                    // GCL.g:66:4: var_declaration ';'
                    {
                    pushFollow(FOLLOW_var_declaration_in_statement326);
                    var_declaration41=var_declaration();

                    state._fsp--;

                    stream_var_declaration.add(var_declaration41.getTree());
                    char_literal42=(Token)match(input,51,FOLLOW_51_in_statement328);  
                    stream_51.add(char_literal42);



                    // AST REWRITE
                    // elements: var_declaration
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 66:24: -> var_declaration
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

    public static class ifstatement_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start ifstatement
    // GCL.g:69:1: ifstatement : ( IF '(' condition ')' block ( ELSE elseblock )? -> ^( IF condition block ( elseblock )? ) | TRY block ( ELSE elseblock )? -> ^( TRY block ( elseblock )? ) );
    public final GCLParser.ifstatement_return ifstatement() throws RecognitionException {
        GCLParser.ifstatement_return retval = new GCLParser.ifstatement_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token IF43=null;
        Token char_literal44=null;
        Token char_literal46=null;
        Token ELSE48=null;
        Token TRY50=null;
        Token ELSE52=null;
        GCLParser.condition_return condition45 = null;

        GCLParser.block_return block47 = null;

        GCLParser.elseblock_return elseblock49 = null;

        GCLParser.block_return block51 = null;

        GCLParser.elseblock_return elseblock53 = null;


        Object IF43_tree=null;
        Object char_literal44_tree=null;
        Object char_literal46_tree=null;
        Object ELSE48_tree=null;
        Object TRY50_tree=null;
        Object ELSE52_tree=null;
        RewriteRuleTokenStream stream_49=new RewriteRuleTokenStream(adaptor,"token 49");
        RewriteRuleTokenStream stream_TRY=new RewriteRuleTokenStream(adaptor,"token TRY");
        RewriteRuleTokenStream stream_IF=new RewriteRuleTokenStream(adaptor,"token IF");
        RewriteRuleTokenStream stream_ELSE=new RewriteRuleTokenStream(adaptor,"token ELSE");
        RewriteRuleTokenStream stream_50=new RewriteRuleTokenStream(adaptor,"token 50");
        RewriteRuleSubtreeStream stream_condition=new RewriteRuleSubtreeStream(adaptor,"rule condition");
        RewriteRuleSubtreeStream stream_block=new RewriteRuleSubtreeStream(adaptor,"rule block");
        RewriteRuleSubtreeStream stream_elseblock=new RewriteRuleSubtreeStream(adaptor,"rule elseblock");
        try {
            // GCL.g:70:5: ( IF '(' condition ')' block ( ELSE elseblock )? -> ^( IF condition block ( elseblock )? ) | TRY block ( ELSE elseblock )? -> ^( TRY block ( elseblock )? ) )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==IF) ) {
                alt10=1;
            }
            else if ( (LA10_0==TRY) ) {
                alt10=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // GCL.g:70:7: IF '(' condition ')' block ( ELSE elseblock )?
                    {
                    IF43=(Token)match(input,IF,FOLLOW_IF_in_ifstatement350);  
                    stream_IF.add(IF43);

                    char_literal44=(Token)match(input,49,FOLLOW_49_in_ifstatement352);  
                    stream_49.add(char_literal44);

                    pushFollow(FOLLOW_condition_in_ifstatement354);
                    condition45=condition();

                    state._fsp--;

                    stream_condition.add(condition45.getTree());
                    char_literal46=(Token)match(input,50,FOLLOW_50_in_ifstatement356);  
                    stream_50.add(char_literal46);

                    pushFollow(FOLLOW_block_in_ifstatement358);
                    block47=block();

                    state._fsp--;

                    stream_block.add(block47.getTree());
                    // GCL.g:70:34: ( ELSE elseblock )?
                    int alt8=2;
                    alt8 = dfa8.predict(input);
                    switch (alt8) {
                        case 1 :
                            // GCL.g:70:35: ELSE elseblock
                            {
                            ELSE48=(Token)match(input,ELSE,FOLLOW_ELSE_in_ifstatement361);  
                            stream_ELSE.add(ELSE48);

                            pushFollow(FOLLOW_elseblock_in_ifstatement363);
                            elseblock49=elseblock();

                            state._fsp--;

                            stream_elseblock.add(elseblock49.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: block, elseblock, IF, condition
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 70:52: -> ^( IF condition block ( elseblock )? )
                    {
                        // GCL.g:70:55: ^( IF condition block ( elseblock )? )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_IF.nextNode(), root_1);

                        adaptor.addChild(root_1, stream_condition.nextTree());
                        adaptor.addChild(root_1, stream_block.nextTree());
                        // GCL.g:70:76: ( elseblock )?
                        if ( stream_elseblock.hasNext() ) {
                            adaptor.addChild(root_1, stream_elseblock.nextTree());

                        }
                        stream_elseblock.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 2 :
                    // GCL.g:71:7: TRY block ( ELSE elseblock )?
                    {
                    TRY50=(Token)match(input,TRY,FOLLOW_TRY_in_ifstatement386);  
                    stream_TRY.add(TRY50);

                    pushFollow(FOLLOW_block_in_ifstatement388);
                    block51=block();

                    state._fsp--;

                    stream_block.add(block51.getTree());
                    // GCL.g:71:17: ( ELSE elseblock )?
                    int alt9=2;
                    alt9 = dfa9.predict(input);
                    switch (alt9) {
                        case 1 :
                            // GCL.g:71:18: ELSE elseblock
                            {
                            ELSE52=(Token)match(input,ELSE,FOLLOW_ELSE_in_ifstatement391);  
                            stream_ELSE.add(ELSE52);

                            pushFollow(FOLLOW_elseblock_in_ifstatement393);
                            elseblock53=elseblock();

                            state._fsp--;

                            stream_elseblock.add(elseblock53.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: block, TRY, elseblock
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 71:35: -> ^( TRY block ( elseblock )? )
                    {
                        // GCL.g:71:38: ^( TRY block ( elseblock )? )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_TRY.nextNode(), root_1);

                        adaptor.addChild(root_1, stream_block.nextTree());
                        // GCL.g:71:50: ( elseblock )?
                        if ( stream_elseblock.hasNext() ) {
                            adaptor.addChild(root_1, stream_elseblock.nextTree());

                        }
                        stream_elseblock.reset();

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
    // $ANTLR end ifstatement

    public static class elseblock_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start elseblock
    // GCL.g:74:1: elseblock : ( block | ifstatement -> ^( BLOCK ifstatement ) );
    public final GCLParser.elseblock_return elseblock() throws RecognitionException {
        GCLParser.elseblock_return retval = new GCLParser.elseblock_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        GCLParser.block_return block54 = null;

        GCLParser.ifstatement_return ifstatement55 = null;


        RewriteRuleSubtreeStream stream_ifstatement=new RewriteRuleSubtreeStream(adaptor,"rule ifstatement");
        try {
            // GCL.g:75:5: ( block | ifstatement -> ^( BLOCK ifstatement ) )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==47) ) {
                alt11=1;
            }
            else if ( (LA11_0==IF||LA11_0==TRY) ) {
                alt11=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // GCL.g:75:7: block
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_block_in_elseblock427);
                    block54=block();

                    state._fsp--;

                    adaptor.addChild(root_0, block54.getTree());

                    }
                    break;
                case 2 :
                    // GCL.g:76:7: ifstatement
                    {
                    pushFollow(FOLLOW_ifstatement_in_elseblock435);
                    ifstatement55=ifstatement();

                    state._fsp--;

                    stream_ifstatement.add(ifstatement55.getTree());


                    // AST REWRITE
                    // elements: ifstatement
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 76:19: -> ^( BLOCK ifstatement )
                    {
                        // GCL.g:76:22: ^( BLOCK ifstatement )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(BLOCK, "BLOCK"), root_1);

                        adaptor.addChild(root_1, stream_ifstatement.nextTree());

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
    // $ANTLR end elseblock

    public static class conditionliteral_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start conditionliteral
    // GCL.g:79:1: conditionliteral : ( TRUE | call | rule );
    public final GCLParser.conditionliteral_return conditionliteral() throws RecognitionException {
        GCLParser.conditionliteral_return retval = new GCLParser.conditionliteral_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token TRUE56=null;
        GCLParser.call_return call57 = null;

        GCLParser.rule_return rule58 = null;


        Object TRUE56_tree=null;

        try {
            // GCL.g:80:2: ( TRUE | call | rule )
            int alt12=3;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==TRUE) ) {
                alt12=1;
            }
            else if ( (LA12_0==IDENTIFIER) ) {
                int LA12_2 = input.LA(2);

                if ( (LA12_2==49) ) {
                    alt12=2;
                }
                else if ( (LA12_2==OR||LA12_2==50) ) {
                    alt12=3;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 12, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // GCL.g:80:4: TRUE
                    {
                    root_0 = (Object)adaptor.nil();

                    TRUE56=(Token)match(input,TRUE,FOLLOW_TRUE_in_conditionliteral462); 
                    TRUE56_tree = (Object)adaptor.create(TRUE56);
                    adaptor.addChild(root_0, TRUE56_tree);


                    }
                    break;
                case 2 :
                    // GCL.g:80:11: call
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_call_in_conditionliteral466);
                    call57=call();

                    state._fsp--;

                    adaptor.addChild(root_0, call57.getTree());

                    }
                    break;
                case 3 :
                    // GCL.g:80:18: rule
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_rule_in_conditionliteral470);
                    rule58=rule();

                    state._fsp--;

                    adaptor.addChild(root_0, rule58.getTree());

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
    // GCL.g:82:1: expression : expression2 ( OR expression )? ;
    public final GCLParser.expression_return expression() throws RecognitionException {
        GCLParser.expression_return retval = new GCLParser.expression_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token OR60=null;
        GCLParser.expression2_return expression259 = null;

        GCLParser.expression_return expression61 = null;


        Object OR60_tree=null;

        try {
            // GCL.g:83:2: ( expression2 ( OR expression )? )
            // GCL.g:83:4: expression2 ( OR expression )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_expression2_in_expression481);
            expression259=expression2();

            state._fsp--;

            adaptor.addChild(root_0, expression259.getTree());
            // GCL.g:83:16: ( OR expression )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==OR) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // GCL.g:83:17: OR expression
                    {
                    OR60=(Token)match(input,OR,FOLLOW_OR_in_expression484); 
                    OR60_tree = (Object)adaptor.create(OR60);
                    root_0 = (Object)adaptor.becomeRoot(OR60_tree, root_0);

                    pushFollow(FOLLOW_expression_in_expression487);
                    expression61=expression();

                    state._fsp--;

                    adaptor.addChild(root_0, expression61.getTree());

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
    // GCL.g:86:1: expression2 : ( expression_atom ( PLUS | STAR )? | SHARP expression_atom );
    public final GCLParser.expression2_return expression2() throws RecognitionException {
        GCLParser.expression2_return retval = new GCLParser.expression2_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token PLUS63=null;
        Token STAR64=null;
        Token SHARP65=null;
        GCLParser.expression_atom_return expression_atom62 = null;

        GCLParser.expression_atom_return expression_atom66 = null;


        Object PLUS63_tree=null;
        Object STAR64_tree=null;
        Object SHARP65_tree=null;

        try {
            // GCL.g:87:5: ( expression_atom ( PLUS | STAR )? | SHARP expression_atom )
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==IDENTIFIER||(LA15_0>=ANY && LA15_0<=OTHER)||LA15_0==49) ) {
                alt15=1;
            }
            else if ( (LA15_0==SHARP) ) {
                alt15=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;
            }
            switch (alt15) {
                case 1 :
                    // GCL.g:87:7: expression_atom ( PLUS | STAR )?
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_expression_atom_in_expression2503);
                    expression_atom62=expression_atom();

                    state._fsp--;

                    adaptor.addChild(root_0, expression_atom62.getTree());
                    // GCL.g:87:23: ( PLUS | STAR )?
                    int alt14=3;
                    int LA14_0 = input.LA(1);

                    if ( (LA14_0==PLUS) ) {
                        alt14=1;
                    }
                    else if ( (LA14_0==STAR) ) {
                        alt14=2;
                    }
                    switch (alt14) {
                        case 1 :
                            // GCL.g:87:24: PLUS
                            {
                            PLUS63=(Token)match(input,PLUS,FOLLOW_PLUS_in_expression2506); 
                            PLUS63_tree = (Object)adaptor.create(PLUS63);
                            root_0 = (Object)adaptor.becomeRoot(PLUS63_tree, root_0);


                            }
                            break;
                        case 2 :
                            // GCL.g:87:32: STAR
                            {
                            STAR64=(Token)match(input,STAR,FOLLOW_STAR_in_expression2511); 
                            STAR64_tree = (Object)adaptor.create(STAR64);
                            root_0 = (Object)adaptor.becomeRoot(STAR64_tree, root_0);


                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // GCL.g:88:7: SHARP expression_atom
                    {
                    root_0 = (Object)adaptor.nil();

                    SHARP65=(Token)match(input,SHARP,FOLLOW_SHARP_in_expression2522); 
                    SHARP65_tree = (Object)adaptor.create(SHARP65);
                    root_0 = (Object)adaptor.becomeRoot(SHARP65_tree, root_0);

                    pushFollow(FOLLOW_expression_atom_in_expression2525);
                    expression_atom66=expression_atom();

                    state._fsp--;

                    adaptor.addChild(root_0, expression_atom66.getTree());

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
    // GCL.g:91:1: expression_atom : ( ANY | OTHER | rule | '(' expression ')' | call );
    public final GCLParser.expression_atom_return expression_atom() throws RecognitionException {
        GCLParser.expression_atom_return retval = new GCLParser.expression_atom_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ANY67=null;
        Token OTHER68=null;
        Token char_literal70=null;
        Token char_literal72=null;
        GCLParser.rule_return rule69 = null;

        GCLParser.expression_return expression71 = null;

        GCLParser.call_return call73 = null;


        Object ANY67_tree=null;
        Object OTHER68_tree=null;
        Object char_literal70_tree=null;
        Object char_literal72_tree=null;

        try {
            // GCL.g:92:2: ( ANY | OTHER | rule | '(' expression ')' | call )
            int alt16=5;
            alt16 = dfa16.predict(input);
            switch (alt16) {
                case 1 :
                    // GCL.g:92:4: ANY
                    {
                    root_0 = (Object)adaptor.nil();

                    ANY67=(Token)match(input,ANY,FOLLOW_ANY_in_expression_atom539); 
                    ANY67_tree = (Object)adaptor.create(ANY67);
                    adaptor.addChild(root_0, ANY67_tree);


                    }
                    break;
                case 2 :
                    // GCL.g:93:4: OTHER
                    {
                    root_0 = (Object)adaptor.nil();

                    OTHER68=(Token)match(input,OTHER,FOLLOW_OTHER_in_expression_atom544); 
                    OTHER68_tree = (Object)adaptor.create(OTHER68);
                    adaptor.addChild(root_0, OTHER68_tree);


                    }
                    break;
                case 3 :
                    // GCL.g:94:4: rule
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_rule_in_expression_atom549);
                    rule69=rule();

                    state._fsp--;

                    adaptor.addChild(root_0, rule69.getTree());

                    }
                    break;
                case 4 :
                    // GCL.g:95:4: '(' expression ')'
                    {
                    root_0 = (Object)adaptor.nil();

                    char_literal70=(Token)match(input,49,FOLLOW_49_in_expression_atom554); 
                    pushFollow(FOLLOW_expression_in_expression_atom557);
                    expression71=expression();

                    state._fsp--;

                    adaptor.addChild(root_0, expression71.getTree());
                    char_literal72=(Token)match(input,50,FOLLOW_50_in_expression_atom559); 

                    }
                    break;
                case 5 :
                    // GCL.g:96:4: call
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_call_in_expression_atom565);
                    call73=call();

                    state._fsp--;

                    adaptor.addChild(root_0, call73.getTree());

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
    // GCL.g:99:1: call : IDENTIFIER '(' ( var_list )? ')' -> ^( CALL IDENTIFIER ( var_list )? ) ;
    public final GCLParser.call_return call() throws RecognitionException {
        GCLParser.call_return retval = new GCLParser.call_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token IDENTIFIER74=null;
        Token char_literal75=null;
        Token char_literal77=null;
        GCLParser.var_list_return var_list76 = null;


        Object IDENTIFIER74_tree=null;
        Object char_literal75_tree=null;
        Object char_literal77_tree=null;
        RewriteRuleTokenStream stream_49=new RewriteRuleTokenStream(adaptor,"token 49");
        RewriteRuleTokenStream stream_IDENTIFIER=new RewriteRuleTokenStream(adaptor,"token IDENTIFIER");
        RewriteRuleTokenStream stream_50=new RewriteRuleTokenStream(adaptor,"token 50");
        RewriteRuleSubtreeStream stream_var_list=new RewriteRuleSubtreeStream(adaptor,"rule var_list");
        try {
            // GCL.g:100:2: ( IDENTIFIER '(' ( var_list )? ')' -> ^( CALL IDENTIFIER ( var_list )? ) )
            // GCL.g:100:4: IDENTIFIER '(' ( var_list )? ')'
            {
            IDENTIFIER74=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_call577);  
            stream_IDENTIFIER.add(IDENTIFIER74);

            char_literal75=(Token)match(input,49,FOLLOW_49_in_call579);  
            stream_49.add(char_literal75);

            // GCL.g:100:19: ( var_list )?
            int alt17=2;
            alt17 = dfa17.predict(input);
            switch (alt17) {
                case 1 :
                    // GCL.g:100:19: var_list
                    {
                    pushFollow(FOLLOW_var_list_in_call581);
                    var_list76=var_list();

                    state._fsp--;

                    stream_var_list.add(var_list76.getTree());

                    }
                    break;

            }

            char_literal77=(Token)match(input,50,FOLLOW_50_in_call584);  
            stream_50.add(char_literal77);



            // AST REWRITE
            // elements: IDENTIFIER, var_list
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 100:33: -> ^( CALL IDENTIFIER ( var_list )? )
            {
                // GCL.g:100:36: ^( CALL IDENTIFIER ( var_list )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(CALL, "CALL"), root_1);

                adaptor.addChild(root_1, stream_IDENTIFIER.nextNode());
                // GCL.g:100:54: ( var_list )?
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
    // GCL.g:102:1: rule : IDENTIFIER -> ^( CALL IDENTIFIER ) ;
    public final GCLParser.rule_return rule() throws RecognitionException {
        GCLParser.rule_return retval = new GCLParser.rule_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token IDENTIFIER78=null;

        Object IDENTIFIER78_tree=null;
        RewriteRuleTokenStream stream_IDENTIFIER=new RewriteRuleTokenStream(adaptor,"token IDENTIFIER");

        try {
            // GCL.g:103:2: ( IDENTIFIER -> ^( CALL IDENTIFIER ) )
            // GCL.g:103:4: IDENTIFIER
            {
            IDENTIFIER78=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_rule606);  
            stream_IDENTIFIER.add(IDENTIFIER78);



            // AST REWRITE
            // elements: IDENTIFIER
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 103:15: -> ^( CALL IDENTIFIER )
            {
                // GCL.g:103:18: ^( CALL IDENTIFIER )
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
    // GCL.g:105:1: var_declaration : var_type IDENTIFIER ( ',' IDENTIFIER )* -> ( ^( VAR var_type IDENTIFIER ) )+ ;
    public final GCLParser.var_declaration_return var_declaration() throws RecognitionException {
        GCLParser.var_declaration_return retval = new GCLParser.var_declaration_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token IDENTIFIER80=null;
        Token char_literal81=null;
        Token IDENTIFIER82=null;
        GCLParser.var_type_return var_type79 = null;


        Object IDENTIFIER80_tree=null;
        Object char_literal81_tree=null;
        Object IDENTIFIER82_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_IDENTIFIER=new RewriteRuleTokenStream(adaptor,"token IDENTIFIER");
        RewriteRuleSubtreeStream stream_var_type=new RewriteRuleSubtreeStream(adaptor,"rule var_type");
        try {
            // GCL.g:106:2: ( var_type IDENTIFIER ( ',' IDENTIFIER )* -> ( ^( VAR var_type IDENTIFIER ) )+ )
            // GCL.g:106:4: var_type IDENTIFIER ( ',' IDENTIFIER )*
            {
            pushFollow(FOLLOW_var_type_in_var_declaration623);
            var_type79=var_type();

            state._fsp--;

            stream_var_type.add(var_type79.getTree());
            IDENTIFIER80=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_var_declaration625);  
            stream_IDENTIFIER.add(IDENTIFIER80);

            // GCL.g:106:24: ( ',' IDENTIFIER )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==COMMA) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // GCL.g:106:25: ',' IDENTIFIER
            	    {
            	    char_literal81=(Token)match(input,COMMA,FOLLOW_COMMA_in_var_declaration628);  
            	    stream_COMMA.add(char_literal81);

            	    IDENTIFIER82=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_var_declaration630);  
            	    stream_IDENTIFIER.add(IDENTIFIER82);


            	    }
            	    break;

            	default :
            	    break loop18;
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
            // 106:42: -> ( ^( VAR var_type IDENTIFIER ) )+
            {
                if ( !(stream_IDENTIFIER.hasNext()||stream_var_type.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_IDENTIFIER.hasNext()||stream_var_type.hasNext() ) {
                    // GCL.g:106:45: ^( VAR var_type IDENTIFIER )
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
    // GCL.g:109:1: var_type : ( NODE_TYPE | BOOL_TYPE | STRING_TYPE | INT_TYPE | REAL_TYPE );
    public final GCLParser.var_type_return var_type() throws RecognitionException {
        GCLParser.var_type_return retval = new GCLParser.var_type_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set83=null;

        Object set83_tree=null;

        try {
            // GCL.g:110:2: ( NODE_TYPE | BOOL_TYPE | STRING_TYPE | INT_TYPE | REAL_TYPE )
            // GCL.g:
            {
            root_0 = (Object)adaptor.nil();

            set83=(Token)input.LT(1);
            if ( (input.LA(1)>=NODE_TYPE && input.LA(1)<=REAL_TYPE) ) {
                input.consume();
                adaptor.addChild(root_0, (Object)adaptor.create(set83));
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
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
    // $ANTLR end var_type

    public static class var_list_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start var_list
    // GCL.g:117:1: var_list : variable ( COMMA var_list )? ;
    public final GCLParser.var_list_return var_list() throws RecognitionException {
        GCLParser.var_list_return retval = new GCLParser.var_list_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COMMA85=null;
        GCLParser.variable_return variable84 = null;

        GCLParser.var_list_return var_list86 = null;


        Object COMMA85_tree=null;

        try {
            // GCL.g:118:2: ( variable ( COMMA var_list )? )
            // GCL.g:118:4: variable ( COMMA var_list )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_variable_in_var_list686);
            variable84=variable();

            state._fsp--;

            adaptor.addChild(root_0, variable84.getTree());
            // GCL.g:118:13: ( COMMA var_list )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==COMMA) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // GCL.g:118:14: COMMA var_list
                    {
                    COMMA85=(Token)match(input,COMMA,FOLLOW_COMMA_in_var_list689); 
                    pushFollow(FOLLOW_var_list_in_var_list692);
                    var_list86=var_list();

                    state._fsp--;

                    adaptor.addChild(root_0, var_list86.getTree());

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
    // GCL.g:121:1: variable : ( OUT IDENTIFIER -> ^( PARAM OUT IDENTIFIER ) | IDENTIFIER -> ^( PARAM IDENTIFIER ) | DONT_CARE -> ^( PARAM DONT_CARE ) | literal -> ^( PARAM literal ) );
    public final GCLParser.variable_return variable() throws RecognitionException {
        GCLParser.variable_return retval = new GCLParser.variable_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token OUT87=null;
        Token IDENTIFIER88=null;
        Token IDENTIFIER89=null;
        Token DONT_CARE90=null;
        GCLParser.literal_return literal91 = null;


        Object OUT87_tree=null;
        Object IDENTIFIER88_tree=null;
        Object IDENTIFIER89_tree=null;
        Object DONT_CARE90_tree=null;
        RewriteRuleTokenStream stream_DONT_CARE=new RewriteRuleTokenStream(adaptor,"token DONT_CARE");
        RewriteRuleTokenStream stream_OUT=new RewriteRuleTokenStream(adaptor,"token OUT");
        RewriteRuleTokenStream stream_IDENTIFIER=new RewriteRuleTokenStream(adaptor,"token IDENTIFIER");
        RewriteRuleSubtreeStream stream_literal=new RewriteRuleSubtreeStream(adaptor,"rule literal");
        try {
            // GCL.g:122:2: ( OUT IDENTIFIER -> ^( PARAM OUT IDENTIFIER ) | IDENTIFIER -> ^( PARAM IDENTIFIER ) | DONT_CARE -> ^( PARAM DONT_CARE ) | literal -> ^( PARAM literal ) )
            int alt20=4;
            switch ( input.LA(1) ) {
            case OUT:
                {
                alt20=1;
                }
                break;
            case IDENTIFIER:
                {
                alt20=2;
                }
                break;
            case DONT_CARE:
                {
                alt20=3;
                }
                break;
            case TRUE:
            case FALSE:
            case STRING:
            case INT:
            case REAL:
                {
                alt20=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 20, 0, input);

                throw nvae;
            }

            switch (alt20) {
                case 1 :
                    // GCL.g:122:4: OUT IDENTIFIER
                    {
                    OUT87=(Token)match(input,OUT,FOLLOW_OUT_in_variable706);  
                    stream_OUT.add(OUT87);

                    IDENTIFIER88=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_variable708);  
                    stream_IDENTIFIER.add(IDENTIFIER88);



                    // AST REWRITE
                    // elements: OUT, IDENTIFIER
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 122:19: -> ^( PARAM OUT IDENTIFIER )
                    {
                        // GCL.g:122:22: ^( PARAM OUT IDENTIFIER )
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
                    // GCL.g:123:4: IDENTIFIER
                    {
                    IDENTIFIER89=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_variable723);  
                    stream_IDENTIFIER.add(IDENTIFIER89);



                    // AST REWRITE
                    // elements: IDENTIFIER
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 123:15: -> ^( PARAM IDENTIFIER )
                    {
                        // GCL.g:123:18: ^( PARAM IDENTIFIER )
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
                    // GCL.g:124:4: DONT_CARE
                    {
                    DONT_CARE90=(Token)match(input,DONT_CARE,FOLLOW_DONT_CARE_in_variable736);  
                    stream_DONT_CARE.add(DONT_CARE90);



                    // AST REWRITE
                    // elements: DONT_CARE
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 124:14: -> ^( PARAM DONT_CARE )
                    {
                        // GCL.g:124:17: ^( PARAM DONT_CARE )
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
                case 4 :
                    // GCL.g:125:4: literal
                    {
                    pushFollow(FOLLOW_literal_in_variable749);
                    literal91=literal();

                    state._fsp--;

                    stream_literal.add(literal91.getTree());


                    // AST REWRITE
                    // elements: literal
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 125:12: -> ^( PARAM literal )
                    {
                        // GCL.g:125:15: ^( PARAM literal )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PARAM, "PARAM"), root_1);

                        adaptor.addChild(root_1, stream_literal.nextTree());

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

    public static class literal_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start literal
    // GCL.g:128:1: literal : ( TRUE -> BOOL_TYPE TRUE | FALSE -> BOOL_TYPE FALSE | STRING -> STRING_TYPE STRING | INT -> INT_TYPE INT | REAL -> REAL_TYPE REAL );
    public final GCLParser.literal_return literal() throws RecognitionException {
        GCLParser.literal_return retval = new GCLParser.literal_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token TRUE92=null;
        Token FALSE93=null;
        Token STRING94=null;
        Token INT95=null;
        Token REAL96=null;

        Object TRUE92_tree=null;
        Object FALSE93_tree=null;
        Object STRING94_tree=null;
        Object INT95_tree=null;
        Object REAL96_tree=null;
        RewriteRuleTokenStream stream_REAL=new RewriteRuleTokenStream(adaptor,"token REAL");
        RewriteRuleTokenStream stream_INT=new RewriteRuleTokenStream(adaptor,"token INT");
        RewriteRuleTokenStream stream_FALSE=new RewriteRuleTokenStream(adaptor,"token FALSE");
        RewriteRuleTokenStream stream_TRUE=new RewriteRuleTokenStream(adaptor,"token TRUE");
        RewriteRuleTokenStream stream_STRING=new RewriteRuleTokenStream(adaptor,"token STRING");

        try {
            // GCL.g:129:2: ( TRUE -> BOOL_TYPE TRUE | FALSE -> BOOL_TYPE FALSE | STRING -> STRING_TYPE STRING | INT -> INT_TYPE INT | REAL -> REAL_TYPE REAL )
            int alt21=5;
            switch ( input.LA(1) ) {
            case TRUE:
                {
                alt21=1;
                }
                break;
            case FALSE:
                {
                alt21=2;
                }
                break;
            case STRING:
                {
                alt21=3;
                }
                break;
            case INT:
                {
                alt21=4;
                }
                break;
            case REAL:
                {
                alt21=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 21, 0, input);

                throw nvae;
            }

            switch (alt21) {
                case 1 :
                    // GCL.g:129:4: TRUE
                    {
                    TRUE92=(Token)match(input,TRUE,FOLLOW_TRUE_in_literal769);  
                    stream_TRUE.add(TRUE92);



                    // AST REWRITE
                    // elements: TRUE
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 129:9: -> BOOL_TYPE TRUE
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(BOOL_TYPE, "BOOL_TYPE"));
                        adaptor.addChild(root_0, stream_TRUE.nextNode());

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 2 :
                    // GCL.g:130:4: FALSE
                    {
                    FALSE93=(Token)match(input,FALSE,FOLLOW_FALSE_in_literal780);  
                    stream_FALSE.add(FALSE93);



                    // AST REWRITE
                    // elements: FALSE
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 130:10: -> BOOL_TYPE FALSE
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(BOOL_TYPE, "BOOL_TYPE"));
                        adaptor.addChild(root_0, stream_FALSE.nextNode());

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 3 :
                    // GCL.g:131:4: STRING
                    {
                    STRING94=(Token)match(input,STRING,FOLLOW_STRING_in_literal791);  
                    stream_STRING.add(STRING94);



                    // AST REWRITE
                    // elements: STRING
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 131:11: -> STRING_TYPE STRING
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(STRING_TYPE, "STRING_TYPE"));
                        adaptor.addChild(root_0, stream_STRING.nextNode());

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 4 :
                    // GCL.g:132:4: INT
                    {
                    INT95=(Token)match(input,INT,FOLLOW_INT_in_literal802);  
                    stream_INT.add(INT95);



                    // AST REWRITE
                    // elements: INT
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 132:8: -> INT_TYPE INT
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(INT_TYPE, "INT_TYPE"));
                        adaptor.addChild(root_0, stream_INT.nextNode());

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 5 :
                    // GCL.g:133:4: REAL
                    {
                    REAL96=(Token)match(input,REAL,FOLLOW_REAL_in_literal813);  
                    stream_REAL.add(REAL96);



                    // AST REWRITE
                    // elements: REAL
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 133:9: -> REAL_TYPE REAL
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(REAL_TYPE, "REAL_TYPE"));
                        adaptor.addChild(root_0, stream_REAL.nextNode());

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
    // $ANTLR end literal

    // Delegated rules


    protected DFA1 dfa1 = new DFA1(this);
    protected DFA2 dfa2 = new DFA2(this);
    protected DFA7 dfa7 = new DFA7(this);
    protected DFA6 dfa6 = new DFA6(this);
    protected DFA8 dfa8 = new DFA8(this);
    protected DFA9 dfa9 = new DFA9(this);
    protected DFA16 dfa16 = new DFA16(this);
    protected DFA17 dfa17 = new DFA17(this);
    static final String DFA1_eotS =
        "\20\uffff";
    static final String DFA1_eofS =
        "\1\1\17\uffff";
    static final String DFA1_minS =
        "\1\7\17\uffff";
    static final String DFA1_maxS =
        "\1\61\17\uffff";
    static final String DFA1_acceptS =
        "\1\uffff\1\3\1\1\1\2\14\uffff";
    static final String DFA1_specialS =
        "\20\uffff}>";
    static final String[] DFA1_transitionS = {
            "\1\2\1\uffff\1\3\2\uffff\1\3\1\uffff\4\3\1\uffff\1\3\1\uffff"+
            "\1\3\3\uffff\10\3\20\uffff\1\3",
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
        "\1\61\16\uffff";
    static final String DFA2_acceptS =
        "\1\uffff\1\2\1\1\14\uffff";
    static final String DFA2_specialS =
        "\17\uffff}>";
    static final String[] DFA2_transitionS = {
            "\1\2\2\uffff\1\2\1\uffff\4\2\1\uffff\1\2\1\uffff\1\2\3\uffff"+
            "\10\2\17\uffff\1\1\1\2",
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
    static final String DFA7_eotS =
        "\16\uffff";
    static final String DFA7_eofS =
        "\16\uffff";
    static final String DFA7_minS =
        "\1\11\15\uffff";
    static final String DFA7_maxS =
        "\1\61\15\uffff";
    static final String DFA7_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\uffff\1\6\1\7\4\uffff\1\10";
    static final String DFA7_specialS =
        "\16\uffff}>";
    static final String[] DFA7_transitionS = {
            "\1\4\2\uffff\1\10\1\uffff\1\1\1\2\1\3\1\7\1\uffff\1\5\1\uffff"+
            "\1\5\3\uffff\3\10\5\15\20\uffff\1\10",
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
            return "58:1: statement : ( ALAP block -> ^( ALAP block ) | WHILE '(' condition ')' ( DO )? block -> ^( WHILE condition block ) | UNTIL '(' condition ')' ( DO )? block -> ^( UNTIL condition block ) | DO block WHILE '(' condition ')' -> ^( DO block condition ) | ifstatement | CHOICE block ( CH_OR block )* -> ^( CHOICE ( block )+ ) | expression ';' -> expression | var_declaration ';' -> var_declaration );";
        }
    }
    static final String DFA6_eotS =
        "\22\uffff";
    static final String DFA6_eofS =
        "\1\1\21\uffff";
    static final String DFA6_minS =
        "\1\7\21\uffff";
    static final String DFA6_maxS =
        "\1\61\21\uffff";
    static final String DFA6_acceptS =
        "\1\uffff\1\2\17\uffff\1\1";
    static final String DFA6_specialS =
        "\22\uffff}>";
    static final String[] DFA6_transitionS = {
            "\1\1\1\uffff\1\1\2\uffff\1\1\1\uffff\4\1\1\21\1\1\1\uffff\1"+
            "\1\3\uffff\10\1\17\uffff\2\1",
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
            return "()* loopback of 64:20: ( CH_OR block )*";
        }
    }
    static final String DFA8_eotS =
        "\22\uffff";
    static final String DFA8_eofS =
        "\1\2\21\uffff";
    static final String DFA8_minS =
        "\1\7\21\uffff";
    static final String DFA8_maxS =
        "\1\61\21\uffff";
    static final String DFA8_acceptS =
        "\1\uffff\1\1\1\2\17\uffff";
    static final String DFA8_specialS =
        "\22\uffff}>";
    static final String[] DFA8_transitionS = {
            "\1\2\1\uffff\1\2\2\uffff\1\2\1\uffff\4\2\1\uffff\1\2\1\1\1\2"+
            "\3\uffff\10\2\17\uffff\2\2",
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
            return "70:34: ( ELSE elseblock )?";
        }
    }
    static final String DFA9_eotS =
        "\22\uffff";
    static final String DFA9_eofS =
        "\1\2\21\uffff";
    static final String DFA9_minS =
        "\1\7\21\uffff";
    static final String DFA9_maxS =
        "\1\61\21\uffff";
    static final String DFA9_acceptS =
        "\1\uffff\1\1\1\2\17\uffff";
    static final String DFA9_specialS =
        "\22\uffff}>";
    static final String[] DFA9_transitionS = {
            "\1\2\1\uffff\1\2\2\uffff\1\2\1\uffff\4\2\1\uffff\1\2\1\1\1\2"+
            "\3\uffff\10\2\17\uffff\2\2",
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
            return "71:17: ( ELSE elseblock )?";
        }
    }
    static final String DFA16_eotS =
        "\13\uffff";
    static final String DFA16_eofS =
        "\13\uffff";
    static final String DFA16_minS =
        "\1\14\2\uffff\1\15\7\uffff";
    static final String DFA16_maxS =
        "\1\61\2\uffff\1\63\7\uffff";
    static final String DFA16_acceptS =
        "\1\uffff\1\1\1\2\1\uffff\1\4\1\5\1\3\4\uffff";
    static final String DFA16_specialS =
        "\13\uffff}>";
    static final String[] DFA16_transitionS = {
            "\1\3\15\uffff\1\1\1\2\25\uffff\1\4",
            "",
            "",
            "\1\6\11\uffff\2\6\30\uffff\1\5\2\6",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA16_eot = DFA.unpackEncodedString(DFA16_eotS);
    static final short[] DFA16_eof = DFA.unpackEncodedString(DFA16_eofS);
    static final char[] DFA16_min = DFA.unpackEncodedStringToUnsignedChars(DFA16_minS);
    static final char[] DFA16_max = DFA.unpackEncodedStringToUnsignedChars(DFA16_maxS);
    static final short[] DFA16_accept = DFA.unpackEncodedString(DFA16_acceptS);
    static final short[] DFA16_special = DFA.unpackEncodedString(DFA16_specialS);
    static final short[][] DFA16_transition;

    static {
        int numStates = DFA16_transitionS.length;
        DFA16_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA16_transition[i] = DFA.unpackEncodedString(DFA16_transitionS[i]);
        }
    }

    class DFA16 extends DFA {

        public DFA16(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 16;
            this.eot = DFA16_eot;
            this.eof = DFA16_eof;
            this.min = DFA16_min;
            this.max = DFA16_max;
            this.accept = DFA16_accept;
            this.special = DFA16_special;
            this.transition = DFA16_transition;
        }
        public String getDescription() {
            return "91:1: expression_atom : ( ANY | OTHER | rule | '(' expression ')' | call );";
        }
    }
    static final String DFA17_eotS =
        "\12\uffff";
    static final String DFA17_eofS =
        "\12\uffff";
    static final String DFA17_minS =
        "\1\14\11\uffff";
    static final String DFA17_maxS =
        "\1\62\11\uffff";
    static final String DFA17_acceptS =
        "\1\uffff\1\1\7\uffff\1\2";
    static final String DFA17_specialS =
        "\12\uffff}>";
    static final String[] DFA17_transitionS = {
            "\1\1\11\uffff\1\1\13\uffff\6\1\12\uffff\1\11",
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

    static final short[] DFA17_eot = DFA.unpackEncodedString(DFA17_eotS);
    static final short[] DFA17_eof = DFA.unpackEncodedString(DFA17_eofS);
    static final char[] DFA17_min = DFA.unpackEncodedStringToUnsignedChars(DFA17_minS);
    static final char[] DFA17_max = DFA.unpackEncodedStringToUnsignedChars(DFA17_maxS);
    static final short[] DFA17_accept = DFA.unpackEncodedString(DFA17_acceptS);
    static final short[] DFA17_special = DFA.unpackEncodedString(DFA17_specialS);
    static final short[][] DFA17_transition;

    static {
        int numStates = DFA17_transitionS.length;
        DFA17_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA17_transition[i] = DFA.unpackEncodedString(DFA17_transitionS[i]);
        }
    }

    class DFA17 extends DFA {

        public DFA17(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 17;
            this.eot = DFA17_eot;
            this.eof = DFA17_eof;
            this.min = DFA17_min;
            this.max = DFA17_max;
            this.accept = DFA17_accept;
            this.special = DFA17_special;
            this.transition = DFA17_transition;
        }
        public String getDescription() {
            return "100:19: ( var_list )?";
        }
    }
 

    public static final BitSet FOLLOW_function_in_program89 = new BitSet(new long[]{0x00020001FE2BD282L});
    public static final BitSet FOLLOW_statement_in_program91 = new BitSet(new long[]{0x00020001FE2BD282L});
    public static final BitSet FOLLOW_47_in_block121 = new BitSet(new long[]{0x00030001FE2BD280L});
    public static final BitSet FOLLOW_statement_in_block123 = new BitSet(new long[]{0x00030001FE2BD280L});
    public static final BitSet FOLLOW_48_in_block127 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_function144 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_function146 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_function148 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_50_in_function150 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_block_in_function152 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionliteral_in_condition171 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_OR_in_condition174 = new BitSet(new long[]{0x000200000C401000L});
    public static final BitSet FOLLOW_condition_in_condition177 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALAP_in_statement192 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_block_in_statement194 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHILE_in_statement207 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_statement209 = new BitSet(new long[]{0x000200000C401000L});
    public static final BitSet FOLLOW_condition_in_statement211 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_50_in_statement213 = new BitSet(new long[]{0x0000800000000200L});
    public static final BitSet FOLLOW_DO_in_statement215 = new BitSet(new long[]{0x0000800000000200L});
    public static final BitSet FOLLOW_block_in_statement218 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNTIL_in_statement233 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_statement235 = new BitSet(new long[]{0x000200000C401000L});
    public static final BitSet FOLLOW_condition_in_statement237 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_50_in_statement239 = new BitSet(new long[]{0x0000800000000200L});
    public static final BitSet FOLLOW_DO_in_statement241 = new BitSet(new long[]{0x0000800000000200L});
    public static final BitSet FOLLOW_block_in_statement244 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DO_in_statement259 = new BitSet(new long[]{0x0000800000000200L});
    public static final BitSet FOLLOW_block_in_statement261 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_WHILE_in_statement263 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_statement265 = new BitSet(new long[]{0x000200000C401000L});
    public static final BitSet FOLLOW_condition_in_statement267 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_50_in_statement269 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ifstatement_in_statement284 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CHOICE_in_statement292 = new BitSet(new long[]{0x0000800000000200L});
    public static final BitSet FOLLOW_block_in_statement294 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_CH_OR_in_statement297 = new BitSet(new long[]{0x0000800000000200L});
    public static final BitSet FOLLOW_block_in_statement299 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_expression_in_statement315 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_statement317 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_declaration_in_statement326 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_statement328 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IF_in_ifstatement350 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_ifstatement352 = new BitSet(new long[]{0x000200000C401000L});
    public static final BitSet FOLLOW_condition_in_ifstatement354 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_50_in_ifstatement356 = new BitSet(new long[]{0x0000800000000200L});
    public static final BitSet FOLLOW_block_in_ifstatement358 = new BitSet(new long[]{0x0000000000100002L});
    public static final BitSet FOLLOW_ELSE_in_ifstatement361 = new BitSet(new long[]{0x0000800000280200L});
    public static final BitSet FOLLOW_elseblock_in_ifstatement363 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRY_in_ifstatement386 = new BitSet(new long[]{0x0000800000000200L});
    public static final BitSet FOLLOW_block_in_ifstatement388 = new BitSet(new long[]{0x0000000000100002L});
    public static final BitSet FOLLOW_ELSE_in_ifstatement391 = new BitSet(new long[]{0x0000800000280200L});
    public static final BitSet FOLLOW_elseblock_in_ifstatement393 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_elseblock427 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ifstatement_in_elseblock435 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_conditionliteral462 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_conditionliteral466 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_conditionliteral470 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression2_in_expression481 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_OR_in_expression484 = new BitSet(new long[]{0x000200000E001000L});
    public static final BitSet FOLLOW_expression_in_expression487 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_atom_in_expression2503 = new BitSet(new long[]{0x0000000001800002L});
    public static final BitSet FOLLOW_PLUS_in_expression2506 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STAR_in_expression2511 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SHARP_in_expression2522 = new BitSet(new long[]{0x000200000C001000L});
    public static final BitSet FOLLOW_expression_atom_in_expression2525 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_expression_atom539 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_expression_atom544 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_expression_atom549 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_49_in_expression_atom554 = new BitSet(new long[]{0x000200000E001000L});
    public static final BitSet FOLLOW_expression_in_expression_atom557 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_50_in_expression_atom559 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_expression_atom565 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_call577 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_call579 = new BitSet(new long[]{0x000400FC00401000L});
    public static final BitSet FOLLOW_var_list_in_call581 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_50_in_call584 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_rule606 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_type_in_var_declaration623 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_var_declaration625 = new BitSet(new long[]{0x0000000200000002L});
    public static final BitSet FOLLOW_COMMA_in_var_declaration628 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_var_declaration630 = new BitSet(new long[]{0x0000000200000002L});
    public static final BitSet FOLLOW_set_in_var_type0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variable_in_var_list686 = new BitSet(new long[]{0x0000000200000002L});
    public static final BitSet FOLLOW_COMMA_in_var_list689 = new BitSet(new long[]{0x000000FC00401000L});
    public static final BitSet FOLLOW_var_list_in_var_list692 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OUT_in_variable706 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_variable708 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_variable723 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DONT_CARE_in_variable736 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_variable749 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_literal769 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FALSE_in_literal780 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal791 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal802 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REAL_in_literal813 = new BitSet(new long[]{0x0000000000000002L});

}