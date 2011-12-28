// $ANTLR 3.4 E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g 2011-12-28 22:53:46

package groove.control.parse;
import groove.control.*;
import groove.control.parse.Namespace.Kind;
import groove.algebra.AlgebraFamily;
import groove.view.FormatError;
import java.util.LinkedList;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.antlr.runtime.tree.*;


@SuppressWarnings({"all", "warnings", "unchecked"})
public class CtrlParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ACTIONS", "ALAP", "AMP", "ANY", "ARG", "ARGS", "ASTERISK", "BAR", "BLOCK", "BOOL", "BSLASH", "CALL", "CHOICE", "COMMA", "DO", "DONT_CARE", "DOT", "DO_UNTIL", "DO_WHILE", "ELSE", "EscapeSequence", "FALSE", "FUNCTION", "FUNCTIONS", "ID", "IF", "INT", "INT_LIT", "IntegerNumber", "LCURLY", "LPAR", "MINUS", "ML_COMMENT", "NODE", "NOT", "NonIntegerNumber", "OR", "OTHER", "OUT", "PLUS", "PROGRAM", "QUOTE", "RCURLY", "REAL", "REAL_LIT", "RPAR", "RULE", "SEMI", "SHARP", "SL_COMMENT", "STAR", "STRING", "STRING_LIT", "TRUE", "TRY", "UNTIL", "VAR", "WHILE", "WS"
    };

    public static final int EOF=-1;
    public static final int ACTIONS=4;
    public static final int ALAP=5;
    public static final int AMP=6;
    public static final int ANY=7;
    public static final int ARG=8;
    public static final int ARGS=9;
    public static final int ASTERISK=10;
    public static final int BAR=11;
    public static final int BLOCK=12;
    public static final int BOOL=13;
    public static final int BSLASH=14;
    public static final int CALL=15;
    public static final int CHOICE=16;
    public static final int COMMA=17;
    public static final int DO=18;
    public static final int DONT_CARE=19;
    public static final int DOT=20;
    public static final int DO_UNTIL=21;
    public static final int DO_WHILE=22;
    public static final int ELSE=23;
    public static final int EscapeSequence=24;
    public static final int FALSE=25;
    public static final int FUNCTION=26;
    public static final int FUNCTIONS=27;
    public static final int ID=28;
    public static final int IF=29;
    public static final int INT=30;
    public static final int INT_LIT=31;
    public static final int IntegerNumber=32;
    public static final int LCURLY=33;
    public static final int LPAR=34;
    public static final int MINUS=35;
    public static final int ML_COMMENT=36;
    public static final int NODE=37;
    public static final int NOT=38;
    public static final int NonIntegerNumber=39;
    public static final int OR=40;
    public static final int OTHER=41;
    public static final int OUT=42;
    public static final int PLUS=43;
    public static final int PROGRAM=44;
    public static final int QUOTE=45;
    public static final int RCURLY=46;
    public static final int REAL=47;
    public static final int REAL_LIT=48;
    public static final int RPAR=49;
    public static final int RULE=50;
    public static final int SEMI=51;
    public static final int SHARP=52;
    public static final int SL_COMMENT=53;
    public static final int STAR=54;
    public static final int STRING=55;
    public static final int STRING_LIT=56;
    public static final int TRUE=57;
    public static final int TRY=58;
    public static final int UNTIL=59;
    public static final int VAR=60;
    public static final int WHILE=61;
    public static final int WS=62;

    // delegates
    public Parser[] getDelegates() {
        return new Parser[] {};
    }

    // delegators


    public CtrlParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }
    public CtrlParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

protected TreeAdaptor adaptor = new CommonTreeAdaptor();

public void setTreeAdaptor(TreeAdaptor adaptor) {
    this.adaptor = adaptor;
}
public TreeAdaptor getTreeAdaptor() {
    return adaptor;
}
    public String[] getTokenNames() { return CtrlParser.tokenNames; }
    public String getGrammarFileName() { return "E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g"; }


        /** Lexer for the GCL language. */
        private static CtrlLexer lexer = new CtrlLexer(null);
        /** Helper class to convert AST trees to namespace. */
        private CtrlHelper helper;
        
        public void displayRecognitionError(String[] tokenNames,
                RecognitionException e) {
            String hdr = getErrorHeader(e);
            String msg = getErrorMessage(e, tokenNames);
            this.helper.addError(hdr + " " + msg, e.line, e.charPositionInLine);
        }

        public List<FormatError> getErrors() {
            return this.helper.getErrors();
        }

        /**
         * Runs the lexer and parser on a given input character stream,
         * with a (presumably empty) namespace.
         * @return the resulting syntax tree
         */
        public MyTree run(CharStream input, Namespace namespace, AlgebraFamily family) throws RecognitionException {
            this.helper = new CtrlHelper(this, namespace, family);
            lexer.setCharStream(input);
            setTokenStream(new CommonTokenStream(lexer));
            setTreeAdaptor(new MyTreeAdaptor());
            return (MyTree) program().getTree();
        }


    public static class program_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "program"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:71:1: program : ( function | action | stat )* EOF -> ^( PROGRAM ^( ACTIONS ( action )* ) ^( FUNCTIONS ( function )* ) ^( BLOCK ( stat )* ) ) ;
    public final CtrlParser.program_return program() throws RecognitionException {
        CtrlParser.program_return retval = new CtrlParser.program_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token EOF4=null;
        CtrlParser.function_return function1 =null;

        CtrlParser.action_return action2 =null;

        CtrlParser.stat_return stat3 =null;


        CommonTree EOF4_tree=null;
        RewriteRuleTokenStream stream_EOF=new RewriteRuleTokenStream(adaptor,"token EOF");
        RewriteRuleSubtreeStream stream_action=new RewriteRuleSubtreeStream(adaptor,"rule action");
        RewriteRuleSubtreeStream stream_stat=new RewriteRuleSubtreeStream(adaptor,"rule stat");
        RewriteRuleSubtreeStream stream_function=new RewriteRuleSubtreeStream(adaptor,"rule function");
        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:72:3: ( ( function | action | stat )* EOF -> ^( PROGRAM ^( ACTIONS ( action )* ) ^( FUNCTIONS ( function )* ) ^( BLOCK ( stat )* ) ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:75:5: ( function | action | stat )* EOF
            {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:75:5: ( function | action | stat )*
            loop1:
            do {
                int alt1=4;
                switch ( input.LA(1) ) {
                case FUNCTION:
                    {
                    alt1=1;
                    }
                    break;
                case RULE:
                    {
                    alt1=2;
                    }
                    break;
                case ALAP:
                case ANY:
                case BOOL:
                case CHOICE:
                case DO:
                case ID:
                case IF:
                case INT:
                case LCURLY:
                case LPAR:
                case NODE:
                case OTHER:
                case REAL:
                case SHARP:
                case STRING:
                case TRY:
                case UNTIL:
                case WHILE:
                    {
                    alt1=3;
                    }
                    break;

                }

                switch (alt1) {
            	case 1 :
            	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:75:6: function
            	    {
            	    pushFollow(FOLLOW_function_in_program128);
            	    function1=function();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_function.add(function1.getTree());

            	    }
            	    break;
            	case 2 :
            	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:75:15: action
            	    {
            	    pushFollow(FOLLOW_action_in_program130);
            	    action2=action();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_action.add(action2.getTree());

            	    }
            	    break;
            	case 3 :
            	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:75:22: stat
            	    {
            	    pushFollow(FOLLOW_stat_in_program132);
            	    stat3=stat();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_stat.add(stat3.getTree());

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            EOF4=(Token)match(input,EOF,FOLLOW_EOF_in_program136); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_EOF.add(EOF4);


            // AST REWRITE
            // elements: action, function, stat
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 76:5: -> ^( PROGRAM ^( ACTIONS ( action )* ) ^( FUNCTIONS ( function )* ) ^( BLOCK ( stat )* ) )
            {
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:76:8: ^( PROGRAM ^( ACTIONS ( action )* ) ^( FUNCTIONS ( function )* ) ^( BLOCK ( stat )* ) )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(PROGRAM, "PROGRAM")
                , root_1);

                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:76:18: ^( ACTIONS ( action )* )
                {
                CommonTree root_2 = (CommonTree)adaptor.nil();
                root_2 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(ACTIONS, "ACTIONS")
                , root_2);

                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:76:28: ( action )*
                while ( stream_action.hasNext() ) {
                    adaptor.addChild(root_2, stream_action.nextTree());

                }
                stream_action.reset();

                adaptor.addChild(root_1, root_2);
                }

                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:76:37: ^( FUNCTIONS ( function )* )
                {
                CommonTree root_2 = (CommonTree)adaptor.nil();
                root_2 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(FUNCTIONS, "FUNCTIONS")
                , root_2);

                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:76:49: ( function )*
                while ( stream_function.hasNext() ) {
                    adaptor.addChild(root_2, stream_function.nextTree());

                }
                stream_function.reset();

                adaptor.addChild(root_1, root_2);
                }

                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:76:60: ^( BLOCK ( stat )* )
                {
                CommonTree root_2 = (CommonTree)adaptor.nil();
                root_2 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(BLOCK, "BLOCK")
                , root_2);

                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:76:68: ( stat )*
                while ( stream_stat.hasNext() ) {
                    adaptor.addChild(root_2, stream_stat.nextTree());

                }
                stream_stat.reset();

                adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "program"


    public static class action_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "action"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:82:1: action : RULE ^ ID LPAR ! RPAR ! block ;
    public final CtrlParser.action_return action() throws RecognitionException {
        CtrlParser.action_return retval = new CtrlParser.action_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token RULE5=null;
        Token ID6=null;
        Token LPAR7=null;
        Token RPAR8=null;
        CtrlParser.block_return block9 =null;


        CommonTree RULE5_tree=null;
        CommonTree ID6_tree=null;
        CommonTree LPAR7_tree=null;
        CommonTree RPAR8_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:83:3: ( RULE ^ ID LPAR ! RPAR ! block )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:85:5: RULE ^ ID LPAR ! RPAR ! block
            {
            root_0 = (CommonTree)adaptor.nil();


            RULE5=(Token)match(input,RULE,FOLLOW_RULE_in_action192); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            RULE5_tree = 
            (CommonTree)adaptor.create(RULE5)
            ;
            root_0 = (CommonTree)adaptor.becomeRoot(RULE5_tree, root_0);
            }

            ID6=(Token)match(input,ID,FOLLOW_ID_in_action195); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID6_tree = 
            (CommonTree)adaptor.create(ID6)
            ;
            adaptor.addChild(root_0, ID6_tree);
            }

            LPAR7=(Token)match(input,LPAR,FOLLOW_LPAR_in_action197); if (state.failed) return retval;

            RPAR8=(Token)match(input,RPAR,FOLLOW_RPAR_in_action200); if (state.failed) return retval;

            pushFollow(FOLLOW_block_in_action203);
            block9=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, block9.getTree());

            if ( state.backtracking==0 ) { helper.declareName(RULE5_tree); }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "action"


    public static class function_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "function"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:93:1: function : FUNCTION ^ ID LPAR ! RPAR ! block ;
    public final CtrlParser.function_return function() throws RecognitionException {
        CtrlParser.function_return retval = new CtrlParser.function_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token FUNCTION10=null;
        Token ID11=null;
        Token LPAR12=null;
        Token RPAR13=null;
        CtrlParser.block_return block14 =null;


        CommonTree FUNCTION10_tree=null;
        CommonTree ID11_tree=null;
        CommonTree LPAR12_tree=null;
        CommonTree RPAR13_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:94:3: ( FUNCTION ^ ID LPAR ! RPAR ! block )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:96:5: FUNCTION ^ ID LPAR ! RPAR ! block
            {
            root_0 = (CommonTree)adaptor.nil();


            FUNCTION10=(Token)match(input,FUNCTION,FOLLOW_FUNCTION_in_function234); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            FUNCTION10_tree = 
            (CommonTree)adaptor.create(FUNCTION10)
            ;
            root_0 = (CommonTree)adaptor.becomeRoot(FUNCTION10_tree, root_0);
            }

            ID11=(Token)match(input,ID,FOLLOW_ID_in_function237); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID11_tree = 
            (CommonTree)adaptor.create(ID11)
            ;
            adaptor.addChild(root_0, ID11_tree);
            }

            LPAR12=(Token)match(input,LPAR,FOLLOW_LPAR_in_function239); if (state.failed) return retval;

            RPAR13=(Token)match(input,RPAR,FOLLOW_RPAR_in_function242); if (state.failed) return retval;

            pushFollow(FOLLOW_block_in_function245);
            block14=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, block14.getTree());

            if ( state.backtracking==0 ) { helper.declareName(FUNCTION10_tree); }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "function"


    public static class block_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "block"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:101:1: block : LCURLY ( stat )* RCURLY -> ^( BLOCK ( stat )* ) ;
    public final CtrlParser.block_return block() throws RecognitionException {
        CtrlParser.block_return retval = new CtrlParser.block_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token LCURLY15=null;
        Token RCURLY17=null;
        CtrlParser.stat_return stat16 =null;


        CommonTree LCURLY15_tree=null;
        CommonTree RCURLY17_tree=null;
        RewriteRuleTokenStream stream_LCURLY=new RewriteRuleTokenStream(adaptor,"token LCURLY");
        RewriteRuleTokenStream stream_RCURLY=new RewriteRuleTokenStream(adaptor,"token RCURLY");
        RewriteRuleSubtreeStream stream_stat=new RewriteRuleSubtreeStream(adaptor,"rule stat");
        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:102:3: ( LCURLY ( stat )* RCURLY -> ^( BLOCK ( stat )* ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:104:5: LCURLY ( stat )* RCURLY
            {
            LCURLY15=(Token)match(input,LCURLY,FOLLOW_LCURLY_in_block276); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LCURLY.add(LCURLY15);


            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:104:12: ( stat )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==ALAP||LA2_0==ANY||LA2_0==BOOL||LA2_0==CHOICE||LA2_0==DO||(LA2_0 >= ID && LA2_0 <= INT)||(LA2_0 >= LCURLY && LA2_0 <= LPAR)||LA2_0==NODE||LA2_0==OTHER||LA2_0==REAL||LA2_0==SHARP||LA2_0==STRING||(LA2_0 >= TRY && LA2_0 <= UNTIL)||LA2_0==WHILE) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:104:12: stat
            	    {
            	    pushFollow(FOLLOW_stat_in_block278);
            	    stat16=stat();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_stat.add(stat16.getTree());

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            RCURLY17=(Token)match(input,RCURLY,FOLLOW_RCURLY_in_block281); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RCURLY.add(RCURLY17);


            // AST REWRITE
            // elements: stat
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 104:25: -> ^( BLOCK ( stat )* )
            {
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:104:28: ^( BLOCK ( stat )* )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(BLOCK, "BLOCK")
                , root_1);

                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:104:36: ( stat )*
                while ( stream_stat.hasNext() ) {
                    adaptor.addChild(root_1, stream_stat.nextTree());

                }
                stream_stat.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "block"


    public static class stat_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "stat"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:107:1: stat : ( block | ALAP ^ stat | WHILE ^ LPAR ! cond RPAR ! stat | UNTIL ^ LPAR ! cond RPAR ! stat | DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) ) | IF ^ LPAR ! cond RPAR ! stat ( ( ELSE )=> ELSE ! stat )? | TRY ^ stat ( ( ELSE )=> ELSE ! stat )? | CHOICE ^ stat ( ( OR )=> OR ! stat )+ | expr SEMI !| var_decl SEMI !);
    public final CtrlParser.stat_return stat() throws RecognitionException {
        CtrlParser.stat_return retval = new CtrlParser.stat_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token ALAP19=null;
        Token WHILE21=null;
        Token LPAR22=null;
        Token RPAR24=null;
        Token UNTIL26=null;
        Token LPAR27=null;
        Token RPAR29=null;
        Token DO31=null;
        Token WHILE33=null;
        Token LPAR34=null;
        Token RPAR36=null;
        Token UNTIL37=null;
        Token LPAR38=null;
        Token RPAR40=null;
        Token IF41=null;
        Token LPAR42=null;
        Token RPAR44=null;
        Token ELSE46=null;
        Token TRY48=null;
        Token ELSE50=null;
        Token CHOICE52=null;
        Token OR54=null;
        Token SEMI57=null;
        Token SEMI59=null;
        CtrlParser.block_return block18 =null;

        CtrlParser.stat_return stat20 =null;

        CtrlParser.cond_return cond23 =null;

        CtrlParser.stat_return stat25 =null;

        CtrlParser.cond_return cond28 =null;

        CtrlParser.stat_return stat30 =null;

        CtrlParser.stat_return stat32 =null;

        CtrlParser.cond_return cond35 =null;

        CtrlParser.cond_return cond39 =null;

        CtrlParser.cond_return cond43 =null;

        CtrlParser.stat_return stat45 =null;

        CtrlParser.stat_return stat47 =null;

        CtrlParser.stat_return stat49 =null;

        CtrlParser.stat_return stat51 =null;

        CtrlParser.stat_return stat53 =null;

        CtrlParser.stat_return stat55 =null;

        CtrlParser.expr_return expr56 =null;

        CtrlParser.var_decl_return var_decl58 =null;


        CommonTree ALAP19_tree=null;
        CommonTree WHILE21_tree=null;
        CommonTree LPAR22_tree=null;
        CommonTree RPAR24_tree=null;
        CommonTree UNTIL26_tree=null;
        CommonTree LPAR27_tree=null;
        CommonTree RPAR29_tree=null;
        CommonTree DO31_tree=null;
        CommonTree WHILE33_tree=null;
        CommonTree LPAR34_tree=null;
        CommonTree RPAR36_tree=null;
        CommonTree UNTIL37_tree=null;
        CommonTree LPAR38_tree=null;
        CommonTree RPAR40_tree=null;
        CommonTree IF41_tree=null;
        CommonTree LPAR42_tree=null;
        CommonTree RPAR44_tree=null;
        CommonTree ELSE46_tree=null;
        CommonTree TRY48_tree=null;
        CommonTree ELSE50_tree=null;
        CommonTree CHOICE52_tree=null;
        CommonTree OR54_tree=null;
        CommonTree SEMI57_tree=null;
        CommonTree SEMI59_tree=null;
        RewriteRuleTokenStream stream_DO=new RewriteRuleTokenStream(adaptor,"token DO");
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleTokenStream stream_WHILE=new RewriteRuleTokenStream(adaptor,"token WHILE");
        RewriteRuleTokenStream stream_UNTIL=new RewriteRuleTokenStream(adaptor,"token UNTIL");
        RewriteRuleSubtreeStream stream_cond=new RewriteRuleSubtreeStream(adaptor,"rule cond");
        RewriteRuleSubtreeStream stream_stat=new RewriteRuleSubtreeStream(adaptor,"rule stat");
        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:108:2: ( block | ALAP ^ stat | WHILE ^ LPAR ! cond RPAR ! stat | UNTIL ^ LPAR ! cond RPAR ! stat | DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) ) | IF ^ LPAR ! cond RPAR ! stat ( ( ELSE )=> ELSE ! stat )? | TRY ^ stat ( ( ELSE )=> ELSE ! stat )? | CHOICE ^ stat ( ( OR )=> OR ! stat )+ | expr SEMI !| var_decl SEMI !)
            int alt7=10;
            switch ( input.LA(1) ) {
            case LCURLY:
                {
                alt7=1;
                }
                break;
            case ALAP:
                {
                alt7=2;
                }
                break;
            case WHILE:
                {
                alt7=3;
                }
                break;
            case UNTIL:
                {
                alt7=4;
                }
                break;
            case DO:
                {
                alt7=5;
                }
                break;
            case IF:
                {
                alt7=6;
                }
                break;
            case TRY:
                {
                alt7=7;
                }
                break;
            case CHOICE:
                {
                alt7=8;
                }
                break;
            case ANY:
            case ID:
            case LPAR:
            case OTHER:
            case SHARP:
                {
                alt7=9;
                }
                break;
            case BOOL:
            case INT:
            case NODE:
            case REAL:
            case STRING:
                {
                alt7=10;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;

            }

            switch (alt7) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:109:4: block
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_block_in_stat305);
                    block18=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block18.getTree());

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:113:4: ALAP ^ stat
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    ALAP19=(Token)match(input,ALAP,FOLLOW_ALAP_in_stat322); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ALAP19_tree = 
                    (CommonTree)adaptor.create(ALAP19)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(ALAP19_tree, root_0);
                    }

                    pushFollow(FOLLOW_stat_in_stat325);
                    stat20=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat20.getTree());

                    }
                    break;
                case 3 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:118:4: WHILE ^ LPAR ! cond RPAR ! stat
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    WHILE21=(Token)match(input,WHILE,FOLLOW_WHILE_in_stat346); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    WHILE21_tree = 
                    (CommonTree)adaptor.create(WHILE21)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(WHILE21_tree, root_0);
                    }

                    LPAR22=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat349); if (state.failed) return retval;

                    pushFollow(FOLLOW_cond_in_stat352);
                    cond23=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond23.getTree());

                    RPAR24=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat354); if (state.failed) return retval;

                    pushFollow(FOLLOW_stat_in_stat357);
                    stat25=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat25.getTree());

                    }
                    break;
                case 4 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:122:5: UNTIL ^ LPAR ! cond RPAR ! stat
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    UNTIL26=(Token)match(input,UNTIL,FOLLOW_UNTIL_in_stat377); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    UNTIL26_tree = 
                    (CommonTree)adaptor.create(UNTIL26)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(UNTIL26_tree, root_0);
                    }

                    LPAR27=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat380); if (state.failed) return retval;

                    pushFollow(FOLLOW_cond_in_stat383);
                    cond28=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond28.getTree());

                    RPAR29=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat385); if (state.failed) return retval;

                    pushFollow(FOLLOW_stat_in_stat388);
                    stat30=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat30.getTree());

                    }
                    break;
                case 5 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:123:4: DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) )
                    {
                    DO31=(Token)match(input,DO,FOLLOW_DO_in_stat393); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DO.add(DO31);


                    pushFollow(FOLLOW_stat_in_stat395);
                    stat32=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_stat.add(stat32.getTree());

                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:124:4: ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) )
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0==WHILE) ) {
                        alt3=1;
                    }
                    else if ( (LA3_0==UNTIL) ) {
                        alt3=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 3, 0, input);

                        throw nvae;

                    }
                    switch (alt3) {
                        case 1 :
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:129:7: WHILE LPAR cond RPAR
                            {
                            WHILE33=(Token)match(input,WHILE,FOLLOW_WHILE_in_stat438); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_WHILE.add(WHILE33);


                            LPAR34=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat440); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_LPAR.add(LPAR34);


                            pushFollow(FOLLOW_cond_in_stat442);
                            cond35=cond();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_cond.add(cond35.getTree());

                            RPAR36=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat444); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_RPAR.add(RPAR36);


                            // AST REWRITE
                            // elements: stat, WHILE, cond, stat
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {

                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (CommonTree)adaptor.nil();
                            // 129:28: -> ^( BLOCK stat ^( WHILE cond stat ) )
                            {
                                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:129:31: ^( BLOCK stat ^( WHILE cond stat ) )
                                {
                                CommonTree root_1 = (CommonTree)adaptor.nil();
                                root_1 = (CommonTree)adaptor.becomeRoot(
                                (CommonTree)adaptor.create(BLOCK, "BLOCK")
                                , root_1);

                                adaptor.addChild(root_1, stream_stat.nextTree());

                                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:129:44: ^( WHILE cond stat )
                                {
                                CommonTree root_2 = (CommonTree)adaptor.nil();
                                root_2 = (CommonTree)adaptor.becomeRoot(
                                stream_WHILE.nextNode()
                                , root_2);

                                adaptor.addChild(root_2, stream_cond.nextTree());

                                adaptor.addChild(root_2, stream_stat.nextTree());

                                adaptor.addChild(root_1, root_2);
                                }

                                adaptor.addChild(root_0, root_1);
                                }

                            }


                            retval.tree = root_0;
                            }

                            }
                            break;
                        case 2 :
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:136:5: UNTIL LPAR cond RPAR
                            {
                            UNTIL37=(Token)match(input,UNTIL,FOLLOW_UNTIL_in_stat507); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_UNTIL.add(UNTIL37);


                            LPAR38=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat509); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_LPAR.add(LPAR38);


                            pushFollow(FOLLOW_cond_in_stat511);
                            cond39=cond();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_cond.add(cond39.getTree());

                            RPAR40=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat513); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_RPAR.add(RPAR40);


                            // AST REWRITE
                            // elements: cond, UNTIL, stat, stat
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {

                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (CommonTree)adaptor.nil();
                            // 136:26: -> ^( BLOCK stat ^( UNTIL cond stat ) )
                            {
                                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:136:29: ^( BLOCK stat ^( UNTIL cond stat ) )
                                {
                                CommonTree root_1 = (CommonTree)adaptor.nil();
                                root_1 = (CommonTree)adaptor.becomeRoot(
                                (CommonTree)adaptor.create(BLOCK, "BLOCK")
                                , root_1);

                                adaptor.addChild(root_1, stream_stat.nextTree());

                                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:136:42: ^( UNTIL cond stat )
                                {
                                CommonTree root_2 = (CommonTree)adaptor.nil();
                                root_2 = (CommonTree)adaptor.becomeRoot(
                                stream_UNTIL.nextNode()
                                , root_2);

                                adaptor.addChild(root_2, stream_cond.nextTree());

                                adaptor.addChild(root_2, stream_stat.nextTree());

                                adaptor.addChild(root_1, root_2);
                                }

                                adaptor.addChild(root_0, root_1);
                                }

                            }


                            retval.tree = root_0;
                            }

                            }
                            break;

                    }


                    }
                    break;
                case 6 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:142:5: IF ^ LPAR ! cond RPAR ! stat ( ( ELSE )=> ELSE ! stat )?
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    IF41=(Token)match(input,IF,FOLLOW_IF_in_stat560); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IF41_tree = 
                    (CommonTree)adaptor.create(IF41)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(IF41_tree, root_0);
                    }

                    LPAR42=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat563); if (state.failed) return retval;

                    pushFollow(FOLLOW_cond_in_stat566);
                    cond43=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond43.getTree());

                    RPAR44=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat568); if (state.failed) return retval;

                    pushFollow(FOLLOW_stat_in_stat571);
                    stat45=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat45.getTree());

                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:142:31: ( ( ELSE )=> ELSE ! stat )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==ELSE) ) {
                        int LA4_1 = input.LA(2);

                        if ( (synpred1_Ctrl()) ) {
                            alt4=1;
                        }
                    }
                    switch (alt4) {
                        case 1 :
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:142:33: ( ELSE )=> ELSE ! stat
                            {
                            ELSE46=(Token)match(input,ELSE,FOLLOW_ELSE_in_stat581); if (state.failed) return retval;

                            pushFollow(FOLLOW_stat_in_stat584);
                            stat47=stat();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, stat47.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 7 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:146:5: TRY ^ stat ( ( ELSE )=> ELSE ! stat )?
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    TRY48=(Token)match(input,TRY,FOLLOW_TRY_in_stat608); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    TRY48_tree = 
                    (CommonTree)adaptor.create(TRY48)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(TRY48_tree, root_0);
                    }

                    pushFollow(FOLLOW_stat_in_stat611);
                    stat49=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat49.getTree());

                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:146:15: ( ( ELSE )=> ELSE ! stat )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0==ELSE) ) {
                        int LA5_1 = input.LA(2);

                        if ( (synpred2_Ctrl()) ) {
                            alt5=1;
                        }
                    }
                    switch (alt5) {
                        case 1 :
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:146:17: ( ELSE )=> ELSE ! stat
                            {
                            ELSE50=(Token)match(input,ELSE,FOLLOW_ELSE_in_stat621); if (state.failed) return retval;

                            pushFollow(FOLLOW_stat_in_stat624);
                            stat51=stat();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, stat51.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 8 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:149:5: CHOICE ^ stat ( ( OR )=> OR ! stat )+
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    CHOICE52=(Token)match(input,CHOICE,FOLLOW_CHOICE_in_stat643); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CHOICE52_tree = 
                    (CommonTree)adaptor.create(CHOICE52)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(CHOICE52_tree, root_0);
                    }

                    pushFollow(FOLLOW_stat_in_stat646);
                    stat53=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat53.getTree());

                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:149:18: ( ( OR )=> OR ! stat )+
                    int cnt6=0;
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==OR) ) {
                            int LA6_20 = input.LA(2);

                            if ( (synpred3_Ctrl()) ) {
                                alt6=1;
                            }


                        }


                        switch (alt6) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:149:20: ( OR )=> OR ! stat
                    	    {
                    	    OR54=(Token)match(input,OR,FOLLOW_OR_in_stat656); if (state.failed) return retval;

                    	    pushFollow(FOLLOW_stat_in_stat659);
                    	    stat55=stat();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat55.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt6 >= 1 ) break loop6;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(6, input);
                                throw eee;
                        }
                        cnt6++;
                    } while (true);


                    }
                    break;
                case 9 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:152:4: expr SEMI !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_expr_in_stat674);
                    expr56=expr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expr56.getTree());

                    SEMI57=(Token)match(input,SEMI,FOLLOW_SEMI_in_stat676); if (state.failed) return retval;

                    }
                    break;
                case 10 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:155:4: var_decl SEMI !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_var_decl_in_stat690);
                    var_decl58=var_decl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, var_decl58.getTree());

                    SEMI59=(Token)match(input,SEMI,FOLLOW_SEMI_in_stat692); if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "stat"


    public static class cond_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "cond"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:159:1: cond : cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom ) ;
    public final CtrlParser.cond_return cond() throws RecognitionException {
        CtrlParser.cond_return retval = new CtrlParser.cond_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token BAR61=null;
        CtrlParser.cond_atom_return cond_atom60 =null;

        CtrlParser.cond_atom_return cond_atom62 =null;


        CommonTree BAR61_tree=null;
        RewriteRuleTokenStream stream_BAR=new RewriteRuleTokenStream(adaptor,"token BAR");
        RewriteRuleSubtreeStream stream_cond_atom=new RewriteRuleSubtreeStream(adaptor,"rule cond_atom");
        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:160:2: ( cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:162:4: cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom )
            {
            pushFollow(FOLLOW_cond_atom_in_cond716);
            cond_atom60=cond_atom();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_cond_atom.add(cond_atom60.getTree());

            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:163:4: ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom )
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==BAR) ) {
                alt9=1;
            }
            else if ( (LA9_0==RPAR) ) {
                alt9=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                throw nvae;

            }
            switch (alt9) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:163:6: ( BAR cond_atom )+
                    {
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:163:6: ( BAR cond_atom )+
                    int cnt8=0;
                    loop8:
                    do {
                        int alt8=2;
                        int LA8_0 = input.LA(1);

                        if ( (LA8_0==BAR) ) {
                            alt8=1;
                        }


                        switch (alt8) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:163:7: BAR cond_atom
                    	    {
                    	    BAR61=(Token)match(input,BAR,FOLLOW_BAR_in_cond725); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_BAR.add(BAR61);


                    	    pushFollow(FOLLOW_cond_atom_in_cond727);
                    	    cond_atom62=cond_atom();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_cond_atom.add(cond_atom62.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt8 >= 1 ) break loop8;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(8, input);
                                throw eee;
                        }
                        cnt8++;
                    } while (true);


                    // AST REWRITE
                    // elements: cond_atom, cond_atom
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 163:23: -> ^( CHOICE cond_atom ( cond_atom )+ )
                    {
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:163:26: ^( CHOICE cond_atom ( cond_atom )+ )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(CHOICE, "CHOICE")
                        , root_1);

                        adaptor.addChild(root_1, stream_cond_atom.nextTree());

                        if ( !(stream_cond_atom.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_cond_atom.hasNext() ) {
                            adaptor.addChild(root_1, stream_cond_atom.nextTree());

                        }
                        stream_cond_atom.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:164:6: 
                    {
                    // AST REWRITE
                    // elements: cond_atom
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 164:6: -> cond_atom
                    {
                        adaptor.addChild(root_0, stream_cond_atom.nextTree());

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "cond"


    public static class cond_atom_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "cond_atom"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:168:1: cond_atom : ( TRUE | call );
    public final CtrlParser.cond_atom_return cond_atom() throws RecognitionException {
        CtrlParser.cond_atom_return retval = new CtrlParser.cond_atom_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token TRUE63=null;
        CtrlParser.call_return call64 =null;


        CommonTree TRUE63_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:169:2: ( TRUE | call )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==TRUE) ) {
                alt10=1;
            }
            else if ( (LA10_0==ID) ) {
                alt10=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;

            }
            switch (alt10) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:171:4: TRUE
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    TRUE63=(Token)match(input,TRUE,FOLLOW_TRUE_in_cond_atom773); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    TRUE63_tree = 
                    (CommonTree)adaptor.create(TRUE63)
                    ;
                    adaptor.addChild(root_0, TRUE63_tree);
                    }

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:175:5: call
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_call_in_cond_atom794);
                    call64=call();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, call64.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "cond_atom"


    public static class expr_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "expr"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:178:1: expr : expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 ) ;
    public final CtrlParser.expr_return expr() throws RecognitionException {
        CtrlParser.expr_return retval = new CtrlParser.expr_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token BAR66=null;
        CtrlParser.expr2_return expr265 =null;

        CtrlParser.expr2_return expr267 =null;


        CommonTree BAR66_tree=null;
        RewriteRuleTokenStream stream_BAR=new RewriteRuleTokenStream(adaptor,"token BAR");
        RewriteRuleSubtreeStream stream_expr2=new RewriteRuleSubtreeStream(adaptor,"rule expr2");
        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:179:2: ( expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:183:4: expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 )
            {
            pushFollow(FOLLOW_expr2_in_expr824);
            expr265=expr2();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_expr2.add(expr265.getTree());

            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:184:4: ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 )
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==BAR) ) {
                alt12=1;
            }
            else if ( (LA12_0==RPAR||LA12_0==SEMI) ) {
                alt12=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;

            }
            switch (alt12) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:184:6: ( BAR expr2 )+
                    {
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:184:6: ( BAR expr2 )+
                    int cnt11=0;
                    loop11:
                    do {
                        int alt11=2;
                        int LA11_0 = input.LA(1);

                        if ( (LA11_0==BAR) ) {
                            alt11=1;
                        }


                        switch (alt11) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:184:7: BAR expr2
                    	    {
                    	    BAR66=(Token)match(input,BAR,FOLLOW_BAR_in_expr832); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_BAR.add(BAR66);


                    	    pushFollow(FOLLOW_expr2_in_expr834);
                    	    expr267=expr2();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_expr2.add(expr267.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt11 >= 1 ) break loop11;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(11, input);
                                throw eee;
                        }
                        cnt11++;
                    } while (true);


                    // AST REWRITE
                    // elements: expr2, expr2
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 184:19: -> ^( CHOICE expr2 ( expr2 )+ )
                    {
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:184:22: ^( CHOICE expr2 ( expr2 )+ )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(CHOICE, "CHOICE")
                        , root_1);

                        adaptor.addChild(root_1, stream_expr2.nextTree());

                        if ( !(stream_expr2.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_expr2.hasNext() ) {
                            adaptor.addChild(root_1, stream_expr2.nextTree());

                        }
                        stream_expr2.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:185:6: 
                    {
                    // AST REWRITE
                    // elements: expr2
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 185:6: -> expr2
                    {
                        adaptor.addChild(root_0, stream_expr2.nextTree());

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "expr"


    public static class expr2_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "expr2"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:189:1: expr2 : (e= expr_atom ( PLUS -> ^( BLOCK $e ^( STAR $e) ) | ASTERISK -> ^( STAR $e) | -> $e) | SHARP expr_atom -> ^( ALAP expr_atom ) );
    public final CtrlParser.expr2_return expr2() throws RecognitionException {
        CtrlParser.expr2_return retval = new CtrlParser.expr2_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token PLUS68=null;
        Token ASTERISK69=null;
        Token SHARP70=null;
        CtrlParser.expr_atom_return e =null;

        CtrlParser.expr_atom_return expr_atom71 =null;


        CommonTree PLUS68_tree=null;
        CommonTree ASTERISK69_tree=null;
        CommonTree SHARP70_tree=null;
        RewriteRuleTokenStream stream_PLUS=new RewriteRuleTokenStream(adaptor,"token PLUS");
        RewriteRuleTokenStream stream_SHARP=new RewriteRuleTokenStream(adaptor,"token SHARP");
        RewriteRuleTokenStream stream_ASTERISK=new RewriteRuleTokenStream(adaptor,"token ASTERISK");
        RewriteRuleSubtreeStream stream_expr_atom=new RewriteRuleSubtreeStream(adaptor,"rule expr_atom");
        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:190:3: (e= expr_atom ( PLUS -> ^( BLOCK $e ^( STAR $e) ) | ASTERISK -> ^( STAR $e) | -> $e) | SHARP expr_atom -> ^( ALAP expr_atom ) )
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==ANY||LA14_0==ID||LA14_0==LPAR||LA14_0==OTHER) ) {
                alt14=1;
            }
            else if ( (LA14_0==SHARP) ) {
                alt14=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 14, 0, input);

                throw nvae;

            }
            switch (alt14) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:198:5: e= expr_atom ( PLUS -> ^( BLOCK $e ^( STAR $e) ) | ASTERISK -> ^( STAR $e) | -> $e)
                    {
                    pushFollow(FOLLOW_expr_atom_in_expr2915);
                    e=expr_atom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expr_atom.add(e.getTree());

                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:199:5: ( PLUS -> ^( BLOCK $e ^( STAR $e) ) | ASTERISK -> ^( STAR $e) | -> $e)
                    int alt13=3;
                    switch ( input.LA(1) ) {
                    case PLUS:
                        {
                        alt13=1;
                        }
                        break;
                    case ASTERISK:
                        {
                        alt13=2;
                        }
                        break;
                    case BAR:
                    case RPAR:
                    case SEMI:
                        {
                        alt13=3;
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 13, 0, input);

                        throw nvae;

                    }

                    switch (alt13) {
                        case 1 :
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:199:7: PLUS
                            {
                            PLUS68=(Token)match(input,PLUS,FOLLOW_PLUS_in_expr2923); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_PLUS.add(PLUS68);


                            // AST REWRITE
                            // elements: e, e
                            // token labels: 
                            // rule labels: retval, e
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {

                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
                            RewriteRuleSubtreeStream stream_e=new RewriteRuleSubtreeStream(adaptor,"rule e",e!=null?e.tree:null);

                            root_0 = (CommonTree)adaptor.nil();
                            // 199:12: -> ^( BLOCK $e ^( STAR $e) )
                            {
                                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:199:15: ^( BLOCK $e ^( STAR $e) )
                                {
                                CommonTree root_1 = (CommonTree)adaptor.nil();
                                root_1 = (CommonTree)adaptor.becomeRoot(
                                (CommonTree)adaptor.create(BLOCK, "BLOCK")
                                , root_1);

                                adaptor.addChild(root_1, stream_e.nextTree());

                                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:199:26: ^( STAR $e)
                                {
                                CommonTree root_2 = (CommonTree)adaptor.nil();
                                root_2 = (CommonTree)adaptor.becomeRoot(
                                (CommonTree)adaptor.create(STAR, "STAR")
                                , root_2);

                                adaptor.addChild(root_2, stream_e.nextTree());

                                adaptor.addChild(root_1, root_2);
                                }

                                adaptor.addChild(root_0, root_1);
                                }

                            }


                            retval.tree = root_0;
                            }

                            }
                            break;
                        case 2 :
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:200:7: ASTERISK
                            {
                            ASTERISK69=(Token)match(input,ASTERISK,FOLLOW_ASTERISK_in_expr2947); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_ASTERISK.add(ASTERISK69);


                            // AST REWRITE
                            // elements: e
                            // token labels: 
                            // rule labels: retval, e
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {

                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
                            RewriteRuleSubtreeStream stream_e=new RewriteRuleSubtreeStream(adaptor,"rule e",e!=null?e.tree:null);

                            root_0 = (CommonTree)adaptor.nil();
                            // 200:16: -> ^( STAR $e)
                            {
                                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:200:19: ^( STAR $e)
                                {
                                CommonTree root_1 = (CommonTree)adaptor.nil();
                                root_1 = (CommonTree)adaptor.becomeRoot(
                                (CommonTree)adaptor.create(STAR, "STAR")
                                , root_1);

                                adaptor.addChild(root_1, stream_e.nextTree());

                                adaptor.addChild(root_0, root_1);
                                }

                            }


                            retval.tree = root_0;
                            }

                            }
                            break;
                        case 3 :
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:201:7: 
                            {
                            // AST REWRITE
                            // elements: e
                            // token labels: 
                            // rule labels: retval, e
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {

                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
                            RewriteRuleSubtreeStream stream_e=new RewriteRuleSubtreeStream(adaptor,"rule e",e!=null?e.tree:null);

                            root_0 = (CommonTree)adaptor.nil();
                            // 201:7: -> $e
                            {
                                adaptor.addChild(root_0, stream_e.nextTree());

                            }


                            retval.tree = root_0;
                            }

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:207:5: SHARP expr_atom
                    {
                    SHARP70=(Token)match(input,SHARP,FOLLOW_SHARP_in_expr2999); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SHARP.add(SHARP70);


                    pushFollow(FOLLOW_expr_atom_in_expr21001);
                    expr_atom71=expr_atom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expr_atom.add(expr_atom71.getTree());

                    // AST REWRITE
                    // elements: expr_atom
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 207:21: -> ^( ALAP expr_atom )
                    {
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:207:24: ^( ALAP expr_atom )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(ALAP, "ALAP")
                        , root_1);

                        adaptor.addChild(root_1, stream_expr_atom.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "expr2"


    public static class expr_atom_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "expr_atom"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:210:1: expr_atom : ( ANY | OTHER | LPAR ! expr RPAR !| call );
    public final CtrlParser.expr_atom_return expr_atom() throws RecognitionException {
        CtrlParser.expr_atom_return retval = new CtrlParser.expr_atom_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token ANY72=null;
        Token OTHER73=null;
        Token LPAR74=null;
        Token RPAR76=null;
        CtrlParser.expr_return expr75 =null;

        CtrlParser.call_return call77 =null;


        CommonTree ANY72_tree=null;
        CommonTree OTHER73_tree=null;
        CommonTree LPAR74_tree=null;
        CommonTree RPAR76_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:211:2: ( ANY | OTHER | LPAR ! expr RPAR !| call )
            int alt15=4;
            switch ( input.LA(1) ) {
            case ANY:
                {
                alt15=1;
                }
                break;
            case OTHER:
                {
                alt15=2;
                }
                break;
            case LPAR:
                {
                alt15=3;
                }
                break;
            case ID:
                {
                alt15=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;

            }

            switch (alt15) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:213:4: ANY
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    ANY72=(Token)match(input,ANY,FOLLOW_ANY_in_expr_atom1029); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ANY72_tree = 
                    (CommonTree)adaptor.create(ANY72)
                    ;
                    adaptor.addChild(root_0, ANY72_tree);
                    }

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:217:4: OTHER
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    OTHER73=(Token)match(input,OTHER,FOLLOW_OTHER_in_expr_atom1046); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    OTHER73_tree = 
                    (CommonTree)adaptor.create(OTHER73)
                    ;
                    adaptor.addChild(root_0, OTHER73_tree);
                    }

                    }
                    break;
                case 3 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:220:4: LPAR ! expr RPAR !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    LPAR74=(Token)match(input,LPAR,FOLLOW_LPAR_in_expr_atom1059); if (state.failed) return retval;

                    pushFollow(FOLLOW_expr_in_expr_atom1062);
                    expr75=expr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expr75.getTree());

                    RPAR76=(Token)match(input,RPAR,FOLLOW_RPAR_in_expr_atom1064); if (state.failed) return retval;

                    }
                    break;
                case 4 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:223:4: call
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_call_in_expr_atom1078);
                    call77=call();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, call77.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "expr_atom"


    public static class call_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "call"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:227:1: call : rule_name ( arg_list )? -> ^( CALL[$rule_name.start] rule_name ( arg_list )? ) ;
    public final CtrlParser.call_return call() throws RecognitionException {
        CtrlParser.call_return retval = new CtrlParser.call_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        CtrlParser.rule_name_return rule_name78 =null;

        CtrlParser.arg_list_return arg_list79 =null;


        RewriteRuleSubtreeStream stream_arg_list=new RewriteRuleSubtreeStream(adaptor,"rule arg_list");
        RewriteRuleSubtreeStream stream_rule_name=new RewriteRuleSubtreeStream(adaptor,"rule rule_name");
        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:228:2: ( rule_name ( arg_list )? -> ^( CALL[$rule_name.start] rule_name ( arg_list )? ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:232:4: rule_name ( arg_list )?
            {
            pushFollow(FOLLOW_rule_name_in_call1108);
            rule_name78=rule_name();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rule_name.add(rule_name78.getTree());

            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:232:14: ( arg_list )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==LPAR) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:232:14: arg_list
                    {
                    pushFollow(FOLLOW_arg_list_in_call1110);
                    arg_list79=arg_list();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_arg_list.add(arg_list79.getTree());

                    }
                    break;

            }


            // AST REWRITE
            // elements: arg_list, rule_name
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 233:4: -> ^( CALL[$rule_name.start] rule_name ( arg_list )? )
            {
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:233:7: ^( CALL[$rule_name.start] rule_name ( arg_list )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(CALL, (rule_name78!=null?((Token)rule_name78.start):null))
                , root_1);

                adaptor.addChild(root_1, stream_rule_name.nextTree());

                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:233:42: ( arg_list )?
                if ( stream_arg_list.hasNext() ) {
                    adaptor.addChild(root_1, stream_arg_list.nextTree());

                }
                stream_arg_list.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "call"


    public static class arg_list_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "arg_list"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:239:1: arg_list : LPAR ( arg ( COMMA arg )* )? RPAR -> ^( ARGS ( arg )* ) ;
    public final CtrlParser.arg_list_return arg_list() throws RecognitionException {
        CtrlParser.arg_list_return retval = new CtrlParser.arg_list_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token LPAR80=null;
        Token COMMA82=null;
        Token RPAR84=null;
        CtrlParser.arg_return arg81 =null;

        CtrlParser.arg_return arg83 =null;


        CommonTree LPAR80_tree=null;
        CommonTree COMMA82_tree=null;
        CommonTree RPAR84_tree=null;
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_arg=new RewriteRuleSubtreeStream(adaptor,"rule arg");
        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:240:3: ( LPAR ( arg ( COMMA arg )* )? RPAR -> ^( ARGS ( arg )* ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:242:5: LPAR ( arg ( COMMA arg )* )? RPAR
            {
            LPAR80=(Token)match(input,LPAR,FOLLOW_LPAR_in_arg_list1150); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAR.add(LPAR80);


            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:242:10: ( arg ( COMMA arg )* )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==DONT_CARE||LA18_0==FALSE||LA18_0==ID||LA18_0==INT_LIT||LA18_0==OUT||LA18_0==REAL_LIT||(LA18_0 >= STRING_LIT && LA18_0 <= TRUE)) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:242:11: arg ( COMMA arg )*
                    {
                    pushFollow(FOLLOW_arg_in_arg_list1153);
                    arg81=arg();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_arg.add(arg81.getTree());

                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:242:15: ( COMMA arg )*
                    loop17:
                    do {
                        int alt17=2;
                        int LA17_0 = input.LA(1);

                        if ( (LA17_0==COMMA) ) {
                            alt17=1;
                        }


                        switch (alt17) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:242:16: COMMA arg
                    	    {
                    	    COMMA82=(Token)match(input,COMMA,FOLLOW_COMMA_in_arg_list1156); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA82);


                    	    pushFollow(FOLLOW_arg_in_arg_list1158);
                    	    arg83=arg();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_arg.add(arg83.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop17;
                        }
                    } while (true);


                    }
                    break;

            }


            RPAR84=(Token)match(input,RPAR,FOLLOW_RPAR_in_arg_list1164); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAR.add(RPAR84);


            // AST REWRITE
            // elements: arg
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 243:5: -> ^( ARGS ( arg )* )
            {
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:243:8: ^( ARGS ( arg )* )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(ARGS, "ARGS")
                , root_1);

                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:243:15: ( arg )*
                while ( stream_arg.hasNext() ) {
                    adaptor.addChild(root_1, stream_arg.nextTree());

                }
                stream_arg.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "arg_list"


    public static class arg_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "arg"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:249:1: arg : ( OUT ID -> ^( ARG OUT ID ) | ID -> ^( ARG ID ) | DONT_CARE -> ^( ARG DONT_CARE ) | literal -> ^( ARG literal ) );
    public final CtrlParser.arg_return arg() throws RecognitionException {
        CtrlParser.arg_return retval = new CtrlParser.arg_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token OUT85=null;
        Token ID86=null;
        Token ID87=null;
        Token DONT_CARE88=null;
        CtrlParser.literal_return literal89 =null;


        CommonTree OUT85_tree=null;
        CommonTree ID86_tree=null;
        CommonTree ID87_tree=null;
        CommonTree DONT_CARE88_tree=null;
        RewriteRuleTokenStream stream_DONT_CARE=new RewriteRuleTokenStream(adaptor,"token DONT_CARE");
        RewriteRuleTokenStream stream_OUT=new RewriteRuleTokenStream(adaptor,"token OUT");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_literal=new RewriteRuleSubtreeStream(adaptor,"rule literal");
        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:250:3: ( OUT ID -> ^( ARG OUT ID ) | ID -> ^( ARG ID ) | DONT_CARE -> ^( ARG DONT_CARE ) | literal -> ^( ARG literal ) )
            int alt19=4;
            switch ( input.LA(1) ) {
            case OUT:
                {
                alt19=1;
                }
                break;
            case ID:
                {
                alt19=2;
                }
                break;
            case DONT_CARE:
                {
                alt19=3;
                }
                break;
            case FALSE:
            case INT_LIT:
            case REAL_LIT:
            case STRING_LIT:
            case TRUE:
                {
                alt19=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 19, 0, input);

                throw nvae;

            }

            switch (alt19) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:253:5: OUT ID
                    {
                    OUT85=(Token)match(input,OUT,FOLLOW_OUT_in_arg1207); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_OUT.add(OUT85);


                    ID86=(Token)match(input,ID,FOLLOW_ID_in_arg1209); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID86);


                    // AST REWRITE
                    // elements: ID, OUT
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 253:12: -> ^( ARG OUT ID )
                    {
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:253:15: ^( ARG OUT ID )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(ARG, "ARG")
                        , root_1);

                        adaptor.addChild(root_1, 
                        stream_OUT.nextNode()
                        );

                        adaptor.addChild(root_1, 
                        stream_ID.nextNode()
                        );

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:257:5: ID
                    {
                    ID87=(Token)match(input,ID,FOLLOW_ID_in_arg1240); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID87);


                    // AST REWRITE
                    // elements: ID
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 257:8: -> ^( ARG ID )
                    {
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:257:11: ^( ARG ID )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(ARG, "ARG")
                        , root_1);

                        adaptor.addChild(root_1, 
                        stream_ID.nextNode()
                        );

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 3 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:261:5: DONT_CARE
                    {
                    DONT_CARE88=(Token)match(input,DONT_CARE,FOLLOW_DONT_CARE_in_arg1269); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DONT_CARE.add(DONT_CARE88);


                    // AST REWRITE
                    // elements: DONT_CARE
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 261:15: -> ^( ARG DONT_CARE )
                    {
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:261:18: ^( ARG DONT_CARE )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(ARG, "ARG")
                        , root_1);

                        adaptor.addChild(root_1, 
                        stream_DONT_CARE.nextNode()
                        );

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 4 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:262:5: literal
                    {
                    pushFollow(FOLLOW_literal_in_arg1286);
                    literal89=literal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_literal.add(literal89.getTree());

                    // AST REWRITE
                    // elements: literal
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 262:13: -> ^( ARG literal )
                    {
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:262:16: ^( ARG literal )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(ARG, "ARG")
                        , root_1);

                        adaptor.addChild(root_1, stream_literal.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "arg"


    public static class literal_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "literal"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:265:1: literal : ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT );
    public final CtrlParser.literal_return literal() throws RecognitionException {
        CtrlParser.literal_return retval = new CtrlParser.literal_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token set90=null;

        CommonTree set90_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:266:3: ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:
            {
            root_0 = (CommonTree)adaptor.nil();


            set90=(Token)input.LT(1);

            if ( input.LA(1)==FALSE||input.LA(1)==INT_LIT||input.LA(1)==REAL_LIT||(input.LA(1) >= STRING_LIT && input.LA(1) <= TRUE) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, 
                (CommonTree)adaptor.create(set90)
                );
                state.errorRecovery=false;
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "literal"


    public static class rule_name_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "rule_name"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:284:1: rule_name :ids+= ID ( DOT ids+= ID )* ->;
    public final CtrlParser.rule_name_return rule_name() throws RecognitionException {
        CtrlParser.rule_name_return retval = new CtrlParser.rule_name_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token DOT91=null;
        Token ids=null;
        List list_ids=null;

        CommonTree DOT91_tree=null;
        CommonTree ids_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:285:3: (ids+= ID ( DOT ids+= ID )* ->)
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:285:5: ids+= ID ( DOT ids+= ID )*
            {
            ids=(Token)match(input,ID,FOLLOW_ID_in_rule_name1398); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ids);

            if (list_ids==null) list_ids=new ArrayList();
            list_ids.add(ids);


            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:285:13: ( DOT ids+= ID )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==DOT) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:285:14: DOT ids+= ID
            	    {
            	    DOT91=(Token)match(input,DOT,FOLLOW_DOT_in_rule_name1401); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(DOT91);


            	    ids=(Token)match(input,ID,FOLLOW_ID_in_rule_name1405); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_ID.add(ids);

            	    if (list_ids==null) list_ids=new ArrayList();
            	    list_ids.add(ids);


            	    }
            	    break;

            	default :
            	    break loop20;
                }
            } while (true);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 286:5: ->
            {
                adaptor.addChild(root_0,  helper.toRuleName(list_ids) );

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "rule_name"


    public static class var_decl_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "var_decl"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:290:1: var_decl : var_type ID ( COMMA ID )* -> ^( VAR var_type ( ID )+ ) ;
    public final CtrlParser.var_decl_return var_decl() throws RecognitionException {
        CtrlParser.var_decl_return retval = new CtrlParser.var_decl_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token ID93=null;
        Token COMMA94=null;
        Token ID95=null;
        CtrlParser.var_type_return var_type92 =null;


        CommonTree ID93_tree=null;
        CommonTree COMMA94_tree=null;
        CommonTree ID95_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_var_type=new RewriteRuleSubtreeStream(adaptor,"rule var_type");
        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:291:2: ( var_type ID ( COMMA ID )* -> ^( VAR var_type ( ID )+ ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:293:4: var_type ID ( COMMA ID )*
            {
            pushFollow(FOLLOW_var_type_in_var_decl1437);
            var_type92=var_type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_var_type.add(var_type92.getTree());

            ID93=(Token)match(input,ID,FOLLOW_ID_in_var_decl1439); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID93);


            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:293:16: ( COMMA ID )*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);

                if ( (LA21_0==COMMA) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:293:17: COMMA ID
            	    {
            	    COMMA94=(Token)match(input,COMMA,FOLLOW_COMMA_in_var_decl1442); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA94);


            	    ID95=(Token)match(input,ID,FOLLOW_ID_in_var_decl1444); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_ID.add(ID95);


            	    }
            	    break;

            	default :
            	    break loop21;
                }
            } while (true);


            // AST REWRITE
            // elements: var_type, ID
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 293:28: -> ^( VAR var_type ( ID )+ )
            {
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:293:31: ^( VAR var_type ( ID )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(VAR, "VAR")
                , root_1);

                adaptor.addChild(root_1, stream_var_type.nextTree());

                if ( !(stream_ID.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_ID.hasNext() ) {
                    adaptor.addChild(root_1, 
                    stream_ID.nextNode()
                    );

                }
                stream_ID.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "var_decl"


    public static class var_type_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "var_type"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:297:1: var_type : ( NODE | BOOL | STRING | INT | REAL );
    public final CtrlParser.var_type_return var_type() throws RecognitionException {
        CtrlParser.var_type_return retval = new CtrlParser.var_type_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token set96=null;

        CommonTree set96_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:298:2: ( NODE | BOOL | STRING | INT | REAL )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:
            {
            root_0 = (CommonTree)adaptor.nil();


            set96=(Token)input.LT(1);

            if ( input.LA(1)==BOOL||input.LA(1)==INT||input.LA(1)==NODE||input.LA(1)==REAL||input.LA(1)==STRING ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, 
                (CommonTree)adaptor.create(set96)
                );
                state.errorRecovery=false;
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "var_type"

    // $ANTLR start synpred1_Ctrl
    public final void synpred1_Ctrl_fragment() throws RecognitionException {
        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:142:33: ( ELSE )
        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:142:34: ELSE
        {
        match(input,ELSE,FOLLOW_ELSE_in_synpred1_Ctrl576); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred1_Ctrl

    // $ANTLR start synpred2_Ctrl
    public final void synpred2_Ctrl_fragment() throws RecognitionException {
        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:146:17: ( ELSE )
        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:146:18: ELSE
        {
        match(input,ELSE,FOLLOW_ELSE_in_synpred2_Ctrl616); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred2_Ctrl

    // $ANTLR start synpred3_Ctrl
    public final void synpred3_Ctrl_fragment() throws RecognitionException {
        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:149:20: ( OR )
        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:149:21: OR
        {
        match(input,OR,FOLLOW_OR_in_synpred3_Ctrl651); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred3_Ctrl

    // Delegated rules

    public final boolean synpred1_Ctrl() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_Ctrl_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred3_Ctrl() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred3_Ctrl_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred2_Ctrl() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred2_Ctrl_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


 

    public static final BitSet FOLLOW_function_in_program128 = new BitSet(new long[]{0x2C948226740520A0L});
    public static final BitSet FOLLOW_action_in_program130 = new BitSet(new long[]{0x2C948226740520A0L});
    public static final BitSet FOLLOW_stat_in_program132 = new BitSet(new long[]{0x2C948226740520A0L});
    public static final BitSet FOLLOW_EOF_in_program136 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_in_action192 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_action195 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_LPAR_in_action197 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_RPAR_in_action200 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_block_in_action203 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_function234 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_function237 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_LPAR_in_function239 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_RPAR_in_function242 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_block_in_function245 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LCURLY_in_block276 = new BitSet(new long[]{0x2C90C226700520A0L});
    public static final BitSet FOLLOW_stat_in_block278 = new BitSet(new long[]{0x2C90C226700520A0L});
    public static final BitSet FOLLOW_RCURLY_in_block281 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_stat305 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALAP_in_stat322 = new BitSet(new long[]{0x2C908226700520A0L});
    public static final BitSet FOLLOW_stat_in_stat325 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHILE_in_stat346 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_LPAR_in_stat349 = new BitSet(new long[]{0x0200000010000000L});
    public static final BitSet FOLLOW_cond_in_stat352 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat354 = new BitSet(new long[]{0x2C908226700520A0L});
    public static final BitSet FOLLOW_stat_in_stat357 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNTIL_in_stat377 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_LPAR_in_stat380 = new BitSet(new long[]{0x0200000010000000L});
    public static final BitSet FOLLOW_cond_in_stat383 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat385 = new BitSet(new long[]{0x2C908226700520A0L});
    public static final BitSet FOLLOW_stat_in_stat388 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DO_in_stat393 = new BitSet(new long[]{0x2C908226700520A0L});
    public static final BitSet FOLLOW_stat_in_stat395 = new BitSet(new long[]{0x2800000000000000L});
    public static final BitSet FOLLOW_WHILE_in_stat438 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_LPAR_in_stat440 = new BitSet(new long[]{0x0200000010000000L});
    public static final BitSet FOLLOW_cond_in_stat442 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat444 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNTIL_in_stat507 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_LPAR_in_stat509 = new BitSet(new long[]{0x0200000010000000L});
    public static final BitSet FOLLOW_cond_in_stat511 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat513 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IF_in_stat560 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_LPAR_in_stat563 = new BitSet(new long[]{0x0200000010000000L});
    public static final BitSet FOLLOW_cond_in_stat566 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat568 = new BitSet(new long[]{0x2C908226700520A0L});
    public static final BitSet FOLLOW_stat_in_stat571 = new BitSet(new long[]{0x0000000000800002L});
    public static final BitSet FOLLOW_ELSE_in_stat581 = new BitSet(new long[]{0x2C908226700520A0L});
    public static final BitSet FOLLOW_stat_in_stat584 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRY_in_stat608 = new BitSet(new long[]{0x2C908226700520A0L});
    public static final BitSet FOLLOW_stat_in_stat611 = new BitSet(new long[]{0x0000000000800002L});
    public static final BitSet FOLLOW_ELSE_in_stat621 = new BitSet(new long[]{0x2C908226700520A0L});
    public static final BitSet FOLLOW_stat_in_stat624 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CHOICE_in_stat643 = new BitSet(new long[]{0x2C908226700520A0L});
    public static final BitSet FOLLOW_stat_in_stat646 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_OR_in_stat656 = new BitSet(new long[]{0x2C908226700520A0L});
    public static final BitSet FOLLOW_stat_in_stat659 = new BitSet(new long[]{0x0000010000000002L});
    public static final BitSet FOLLOW_expr_in_stat674 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_SEMI_in_stat676 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_decl_in_stat690 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_SEMI_in_stat692 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_cond_atom_in_cond716 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_BAR_in_cond725 = new BitSet(new long[]{0x0200000010000000L});
    public static final BitSet FOLLOW_cond_atom_in_cond727 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_TRUE_in_cond_atom773 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_cond_atom794 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr2_in_expr824 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_BAR_in_expr832 = new BitSet(new long[]{0x0010020410000080L});
    public static final BitSet FOLLOW_expr2_in_expr834 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_expr_atom_in_expr2915 = new BitSet(new long[]{0x0000080000000402L});
    public static final BitSet FOLLOW_PLUS_in_expr2923 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASTERISK_in_expr2947 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SHARP_in_expr2999 = new BitSet(new long[]{0x0000020410000080L});
    public static final BitSet FOLLOW_expr_atom_in_expr21001 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_expr_atom1029 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_expr_atom1046 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_expr_atom1059 = new BitSet(new long[]{0x0010020410000080L});
    public static final BitSet FOLLOW_expr_in_expr_atom1062 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_RPAR_in_expr_atom1064 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_expr_atom1078 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_name_in_call1108 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_arg_list_in_call1110 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_arg_list1150 = new BitSet(new long[]{0x0303040092080000L});
    public static final BitSet FOLLOW_arg_in_arg_list1153 = new BitSet(new long[]{0x0002000000020000L});
    public static final BitSet FOLLOW_COMMA_in_arg_list1156 = new BitSet(new long[]{0x0301040092080000L});
    public static final BitSet FOLLOW_arg_in_arg_list1158 = new BitSet(new long[]{0x0002000000020000L});
    public static final BitSet FOLLOW_RPAR_in_arg_list1164 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OUT_in_arg1207 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_arg1209 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_arg1240 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DONT_CARE_in_arg1269 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_arg1286 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_rule_name1398 = new BitSet(new long[]{0x0000000000100002L});
    public static final BitSet FOLLOW_DOT_in_rule_name1401 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_rule_name1405 = new BitSet(new long[]{0x0000000000100002L});
    public static final BitSet FOLLOW_var_type_in_var_decl1437 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_var_decl1439 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_COMMA_in_var_decl1442 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_var_decl1444 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_ELSE_in_synpred1_Ctrl576 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ELSE_in_synpred2_Ctrl616 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_synpred3_Ctrl651 = new BitSet(new long[]{0x0000000000000002L});

}