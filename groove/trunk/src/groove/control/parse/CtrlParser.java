// $ANTLR 3.4 Ctrl.g 2011-07-31 09:50:03

package groove.control.parse;
import groove.control.*;
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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ALAP", "AMP", "ANY", "ARG", "ARGS", "ASTERISK", "BAR", "BLOCK", "BOOL", "BSLASH", "CALL", "CHOICE", "COMMA", "DO", "DONT_CARE", "DOT", "DO_UNTIL", "DO_WHILE", "ELSE", "EscapeSequence", "FALSE", "FUNCTION", "FUNCTIONS", "ID", "IF", "INT", "INT_LIT", "IntegerNumber", "LCURLY", "LPAR", "MINUS", "ML_COMMENT", "NODE", "NOT", "NonIntegerNumber", "OR", "OTHER", "OUT", "PLUS", "PROGRAM", "QUOTE", "RCURLY", "REAL", "REAL_LIT", "RPAR", "SEMI", "SHARP", "SL_COMMENT", "STAR", "STRING", "STRING_LIT", "TRUE", "TRY", "UNTIL", "VAR", "WHILE", "WS"
    };

    public static final int EOF=-1;
    public static final int ALAP=4;
    public static final int AMP=5;
    public static final int ANY=6;
    public static final int ARG=7;
    public static final int ARGS=8;
    public static final int ASTERISK=9;
    public static final int BAR=10;
    public static final int BLOCK=11;
    public static final int BOOL=12;
    public static final int BSLASH=13;
    public static final int CALL=14;
    public static final int CHOICE=15;
    public static final int COMMA=16;
    public static final int DO=17;
    public static final int DONT_CARE=18;
    public static final int DOT=19;
    public static final int DO_UNTIL=20;
    public static final int DO_WHILE=21;
    public static final int ELSE=22;
    public static final int EscapeSequence=23;
    public static final int FALSE=24;
    public static final int FUNCTION=25;
    public static final int FUNCTIONS=26;
    public static final int ID=27;
    public static final int IF=28;
    public static final int INT=29;
    public static final int INT_LIT=30;
    public static final int IntegerNumber=31;
    public static final int LCURLY=32;
    public static final int LPAR=33;
    public static final int MINUS=34;
    public static final int ML_COMMENT=35;
    public static final int NODE=36;
    public static final int NOT=37;
    public static final int NonIntegerNumber=38;
    public static final int OR=39;
    public static final int OTHER=40;
    public static final int OUT=41;
    public static final int PLUS=42;
    public static final int PROGRAM=43;
    public static final int QUOTE=44;
    public static final int RCURLY=45;
    public static final int REAL=46;
    public static final int REAL_LIT=47;
    public static final int RPAR=48;
    public static final int SEMI=49;
    public static final int SHARP=50;
    public static final int SL_COMMENT=51;
    public static final int STAR=52;
    public static final int STRING=53;
    public static final int STRING_LIT=54;
    public static final int TRUE=55;
    public static final int TRY=56;
    public static final int UNTIL=57;
    public static final int VAR=58;
    public static final int WHILE=59;
    public static final int WS=60;

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
    public String getGrammarFileName() { return "Ctrl.g"; }


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
    // Ctrl.g:70:1: program : ( function | stat )* -> ^( PROGRAM ^( FUNCTIONS ( function )* ) ^( BLOCK ( stat )* ) ) ;
    public final CtrlParser.program_return program() throws RecognitionException {
        CtrlParser.program_return retval = new CtrlParser.program_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        CtrlParser.function_return function1 =null;

        CtrlParser.stat_return stat2 =null;


        RewriteRuleSubtreeStream stream_stat=new RewriteRuleSubtreeStream(adaptor,"rule stat");
        RewriteRuleSubtreeStream stream_function=new RewriteRuleSubtreeStream(adaptor,"rule function");
        try {
            // Ctrl.g:71:3: ( ( function | stat )* -> ^( PROGRAM ^( FUNCTIONS ( function )* ) ^( BLOCK ( stat )* ) ) )
            // Ctrl.g:74:5: ( function | stat )*
            {
            // Ctrl.g:74:5: ( function | stat )*
            loop1:
            do {
                int alt1=3;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==FUNCTION) ) {
                    alt1=1;
                }
                else if ( (LA1_0==ALAP||LA1_0==ANY||LA1_0==BOOL||LA1_0==CHOICE||LA1_0==DO||(LA1_0 >= ID && LA1_0 <= INT)||(LA1_0 >= LCURLY && LA1_0 <= LPAR)||LA1_0==NODE||LA1_0==OTHER||LA1_0==REAL||LA1_0==SHARP||LA1_0==STRING||(LA1_0 >= TRY && LA1_0 <= UNTIL)||LA1_0==WHILE) ) {
                    alt1=2;
                }


                switch (alt1) {
            	case 1 :
            	    // Ctrl.g:74:6: function
            	    {
            	    pushFollow(FOLLOW_function_in_program127);
            	    function1=function();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_function.add(function1.getTree());

            	    }
            	    break;
            	case 2 :
            	    // Ctrl.g:74:15: stat
            	    {
            	    pushFollow(FOLLOW_stat_in_program129);
            	    stat2=stat();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_stat.add(stat2.getTree());

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            // AST REWRITE
            // elements: stat, function
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 75:5: -> ^( PROGRAM ^( FUNCTIONS ( function )* ) ^( BLOCK ( stat )* ) )
            {
                // Ctrl.g:75:8: ^( PROGRAM ^( FUNCTIONS ( function )* ) ^( BLOCK ( stat )* ) )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(PROGRAM, "PROGRAM")
                , root_1);

                // Ctrl.g:75:18: ^( FUNCTIONS ( function )* )
                {
                CommonTree root_2 = (CommonTree)adaptor.nil();
                root_2 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(FUNCTIONS, "FUNCTIONS")
                , root_2);

                // Ctrl.g:75:30: ( function )*
                while ( stream_function.hasNext() ) {
                    adaptor.addChild(root_2, stream_function.nextTree());

                }
                stream_function.reset();

                adaptor.addChild(root_1, root_2);
                }

                // Ctrl.g:75:41: ^( BLOCK ( stat )* )
                {
                CommonTree root_2 = (CommonTree)adaptor.nil();
                root_2 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(BLOCK, "BLOCK")
                , root_2);

                // Ctrl.g:75:49: ( stat )*
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


    public static class function_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "function"
    // Ctrl.g:82:1: function : FUNCTION ^ ID LPAR ! RPAR ! block ;
    public final CtrlParser.function_return function() throws RecognitionException {
        CtrlParser.function_return retval = new CtrlParser.function_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token FUNCTION3=null;
        Token ID4=null;
        Token LPAR5=null;
        Token RPAR6=null;
        CtrlParser.block_return block7 =null;


        CommonTree FUNCTION3_tree=null;
        CommonTree ID4_tree=null;
        CommonTree LPAR5_tree=null;
        CommonTree RPAR6_tree=null;

        try {
            // Ctrl.g:83:3: ( FUNCTION ^ ID LPAR ! RPAR ! block )
            // Ctrl.g:85:5: FUNCTION ^ ID LPAR ! RPAR ! block
            {
            root_0 = (CommonTree)adaptor.nil();


            FUNCTION3=(Token)match(input,FUNCTION,FOLLOW_FUNCTION_in_function180); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            FUNCTION3_tree = 
            (CommonTree)adaptor.create(FUNCTION3)
            ;
            root_0 = (CommonTree)adaptor.becomeRoot(FUNCTION3_tree, root_0);
            }

            ID4=(Token)match(input,ID,FOLLOW_ID_in_function183); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID4_tree = 
            (CommonTree)adaptor.create(ID4)
            ;
            adaptor.addChild(root_0, ID4_tree);
            }

            LPAR5=(Token)match(input,LPAR,FOLLOW_LPAR_in_function185); if (state.failed) return retval;

            RPAR6=(Token)match(input,RPAR,FOLLOW_RPAR_in_function188); if (state.failed) return retval;

            pushFollow(FOLLOW_block_in_function191);
            block7=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, block7.getTree());

            if ( state.backtracking==0 ) { helper.declareFunction(FUNCTION3_tree); }

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
    // Ctrl.g:90:1: block : LCURLY ( stat )* RCURLY -> ^( BLOCK ( stat )* ) ;
    public final CtrlParser.block_return block() throws RecognitionException {
        CtrlParser.block_return retval = new CtrlParser.block_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token LCURLY8=null;
        Token RCURLY10=null;
        CtrlParser.stat_return stat9 =null;


        CommonTree LCURLY8_tree=null;
        CommonTree RCURLY10_tree=null;
        RewriteRuleTokenStream stream_LCURLY=new RewriteRuleTokenStream(adaptor,"token LCURLY");
        RewriteRuleTokenStream stream_RCURLY=new RewriteRuleTokenStream(adaptor,"token RCURLY");
        RewriteRuleSubtreeStream stream_stat=new RewriteRuleSubtreeStream(adaptor,"rule stat");
        try {
            // Ctrl.g:91:3: ( LCURLY ( stat )* RCURLY -> ^( BLOCK ( stat )* ) )
            // Ctrl.g:93:5: LCURLY ( stat )* RCURLY
            {
            LCURLY8=(Token)match(input,LCURLY,FOLLOW_LCURLY_in_block222); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LCURLY.add(LCURLY8);


            // Ctrl.g:93:12: ( stat )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==ALAP||LA2_0==ANY||LA2_0==BOOL||LA2_0==CHOICE||LA2_0==DO||(LA2_0 >= ID && LA2_0 <= INT)||(LA2_0 >= LCURLY && LA2_0 <= LPAR)||LA2_0==NODE||LA2_0==OTHER||LA2_0==REAL||LA2_0==SHARP||LA2_0==STRING||(LA2_0 >= TRY && LA2_0 <= UNTIL)||LA2_0==WHILE) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // Ctrl.g:93:12: stat
            	    {
            	    pushFollow(FOLLOW_stat_in_block224);
            	    stat9=stat();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_stat.add(stat9.getTree());

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            RCURLY10=(Token)match(input,RCURLY,FOLLOW_RCURLY_in_block227); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RCURLY.add(RCURLY10);


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
            // 93:25: -> ^( BLOCK ( stat )* )
            {
                // Ctrl.g:93:28: ^( BLOCK ( stat )* )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(BLOCK, "BLOCK")
                , root_1);

                // Ctrl.g:93:36: ( stat )*
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
    // Ctrl.g:96:1: stat : ( block | ALAP ^ stat | WHILE ^ LPAR ! cond RPAR ! stat | UNTIL ^ LPAR ! cond RPAR ! stat | DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) ) | IF ^ LPAR ! cond RPAR ! stat ( ( ELSE )=> ELSE ! stat )? | TRY ^ stat ( ( ELSE )=> ELSE ! stat )? | CHOICE ^ stat ( ( OR )=> OR ! stat )+ | expr SEMI !| var_decl SEMI !);
    public final CtrlParser.stat_return stat() throws RecognitionException {
        CtrlParser.stat_return retval = new CtrlParser.stat_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token ALAP12=null;
        Token WHILE14=null;
        Token LPAR15=null;
        Token RPAR17=null;
        Token UNTIL19=null;
        Token LPAR20=null;
        Token RPAR22=null;
        Token DO24=null;
        Token WHILE26=null;
        Token LPAR27=null;
        Token RPAR29=null;
        Token UNTIL30=null;
        Token LPAR31=null;
        Token RPAR33=null;
        Token IF34=null;
        Token LPAR35=null;
        Token RPAR37=null;
        Token ELSE39=null;
        Token TRY41=null;
        Token ELSE43=null;
        Token CHOICE45=null;
        Token OR47=null;
        Token SEMI50=null;
        Token SEMI52=null;
        CtrlParser.block_return block11 =null;

        CtrlParser.stat_return stat13 =null;

        CtrlParser.cond_return cond16 =null;

        CtrlParser.stat_return stat18 =null;

        CtrlParser.cond_return cond21 =null;

        CtrlParser.stat_return stat23 =null;

        CtrlParser.stat_return stat25 =null;

        CtrlParser.cond_return cond28 =null;

        CtrlParser.cond_return cond32 =null;

        CtrlParser.cond_return cond36 =null;

        CtrlParser.stat_return stat38 =null;

        CtrlParser.stat_return stat40 =null;

        CtrlParser.stat_return stat42 =null;

        CtrlParser.stat_return stat44 =null;

        CtrlParser.stat_return stat46 =null;

        CtrlParser.stat_return stat48 =null;

        CtrlParser.expr_return expr49 =null;

        CtrlParser.var_decl_return var_decl51 =null;


        CommonTree ALAP12_tree=null;
        CommonTree WHILE14_tree=null;
        CommonTree LPAR15_tree=null;
        CommonTree RPAR17_tree=null;
        CommonTree UNTIL19_tree=null;
        CommonTree LPAR20_tree=null;
        CommonTree RPAR22_tree=null;
        CommonTree DO24_tree=null;
        CommonTree WHILE26_tree=null;
        CommonTree LPAR27_tree=null;
        CommonTree RPAR29_tree=null;
        CommonTree UNTIL30_tree=null;
        CommonTree LPAR31_tree=null;
        CommonTree RPAR33_tree=null;
        CommonTree IF34_tree=null;
        CommonTree LPAR35_tree=null;
        CommonTree RPAR37_tree=null;
        CommonTree ELSE39_tree=null;
        CommonTree TRY41_tree=null;
        CommonTree ELSE43_tree=null;
        CommonTree CHOICE45_tree=null;
        CommonTree OR47_tree=null;
        CommonTree SEMI50_tree=null;
        CommonTree SEMI52_tree=null;
        RewriteRuleTokenStream stream_DO=new RewriteRuleTokenStream(adaptor,"token DO");
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleTokenStream stream_WHILE=new RewriteRuleTokenStream(adaptor,"token WHILE");
        RewriteRuleTokenStream stream_UNTIL=new RewriteRuleTokenStream(adaptor,"token UNTIL");
        RewriteRuleSubtreeStream stream_cond=new RewriteRuleSubtreeStream(adaptor,"rule cond");
        RewriteRuleSubtreeStream stream_stat=new RewriteRuleSubtreeStream(adaptor,"rule stat");
        try {
            // Ctrl.g:97:2: ( block | ALAP ^ stat | WHILE ^ LPAR ! cond RPAR ! stat | UNTIL ^ LPAR ! cond RPAR ! stat | DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) ) | IF ^ LPAR ! cond RPAR ! stat ( ( ELSE )=> ELSE ! stat )? | TRY ^ stat ( ( ELSE )=> ELSE ! stat )? | CHOICE ^ stat ( ( OR )=> OR ! stat )+ | expr SEMI !| var_decl SEMI !)
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
                    // Ctrl.g:98:4: block
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_block_in_stat251);
                    block11=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block11.getTree());

                    }
                    break;
                case 2 :
                    // Ctrl.g:102:4: ALAP ^ stat
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    ALAP12=(Token)match(input,ALAP,FOLLOW_ALAP_in_stat268); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ALAP12_tree = 
                    (CommonTree)adaptor.create(ALAP12)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(ALAP12_tree, root_0);
                    }

                    pushFollow(FOLLOW_stat_in_stat271);
                    stat13=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat13.getTree());

                    }
                    break;
                case 3 :
                    // Ctrl.g:107:4: WHILE ^ LPAR ! cond RPAR ! stat
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    WHILE14=(Token)match(input,WHILE,FOLLOW_WHILE_in_stat292); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    WHILE14_tree = 
                    (CommonTree)adaptor.create(WHILE14)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(WHILE14_tree, root_0);
                    }

                    LPAR15=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat295); if (state.failed) return retval;

                    pushFollow(FOLLOW_cond_in_stat298);
                    cond16=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond16.getTree());

                    RPAR17=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat300); if (state.failed) return retval;

                    pushFollow(FOLLOW_stat_in_stat303);
                    stat18=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat18.getTree());

                    }
                    break;
                case 4 :
                    // Ctrl.g:111:5: UNTIL ^ LPAR ! cond RPAR ! stat
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    UNTIL19=(Token)match(input,UNTIL,FOLLOW_UNTIL_in_stat323); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    UNTIL19_tree = 
                    (CommonTree)adaptor.create(UNTIL19)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(UNTIL19_tree, root_0);
                    }

                    LPAR20=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat326); if (state.failed) return retval;

                    pushFollow(FOLLOW_cond_in_stat329);
                    cond21=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond21.getTree());

                    RPAR22=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat331); if (state.failed) return retval;

                    pushFollow(FOLLOW_stat_in_stat334);
                    stat23=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat23.getTree());

                    }
                    break;
                case 5 :
                    // Ctrl.g:112:4: DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) )
                    {
                    DO24=(Token)match(input,DO,FOLLOW_DO_in_stat339); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DO.add(DO24);


                    pushFollow(FOLLOW_stat_in_stat341);
                    stat25=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_stat.add(stat25.getTree());

                    // Ctrl.g:113:4: ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) )
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
                            // Ctrl.g:118:7: WHILE LPAR cond RPAR
                            {
                            WHILE26=(Token)match(input,WHILE,FOLLOW_WHILE_in_stat384); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_WHILE.add(WHILE26);


                            LPAR27=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat386); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_LPAR.add(LPAR27);


                            pushFollow(FOLLOW_cond_in_stat388);
                            cond28=cond();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_cond.add(cond28.getTree());

                            RPAR29=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat390); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_RPAR.add(RPAR29);


                            // AST REWRITE
                            // elements: WHILE, stat, stat, cond
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {

                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (CommonTree)adaptor.nil();
                            // 118:28: -> ^( BLOCK stat ^( WHILE cond stat ) )
                            {
                                // Ctrl.g:118:31: ^( BLOCK stat ^( WHILE cond stat ) )
                                {
                                CommonTree root_1 = (CommonTree)adaptor.nil();
                                root_1 = (CommonTree)adaptor.becomeRoot(
                                (CommonTree)adaptor.create(BLOCK, "BLOCK")
                                , root_1);

                                adaptor.addChild(root_1, stream_stat.nextTree());

                                // Ctrl.g:118:44: ^( WHILE cond stat )
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
                            // Ctrl.g:125:5: UNTIL LPAR cond RPAR
                            {
                            UNTIL30=(Token)match(input,UNTIL,FOLLOW_UNTIL_in_stat453); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_UNTIL.add(UNTIL30);


                            LPAR31=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat455); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_LPAR.add(LPAR31);


                            pushFollow(FOLLOW_cond_in_stat457);
                            cond32=cond();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_cond.add(cond32.getTree());

                            RPAR33=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat459); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_RPAR.add(RPAR33);


                            // AST REWRITE
                            // elements: stat, cond, UNTIL, stat
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {

                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (CommonTree)adaptor.nil();
                            // 125:26: -> ^( BLOCK stat ^( UNTIL cond stat ) )
                            {
                                // Ctrl.g:125:29: ^( BLOCK stat ^( UNTIL cond stat ) )
                                {
                                CommonTree root_1 = (CommonTree)adaptor.nil();
                                root_1 = (CommonTree)adaptor.becomeRoot(
                                (CommonTree)adaptor.create(BLOCK, "BLOCK")
                                , root_1);

                                adaptor.addChild(root_1, stream_stat.nextTree());

                                // Ctrl.g:125:42: ^( UNTIL cond stat )
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
                    // Ctrl.g:131:5: IF ^ LPAR ! cond RPAR ! stat ( ( ELSE )=> ELSE ! stat )?
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    IF34=(Token)match(input,IF,FOLLOW_IF_in_stat506); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IF34_tree = 
                    (CommonTree)adaptor.create(IF34)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(IF34_tree, root_0);
                    }

                    LPAR35=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat509); if (state.failed) return retval;

                    pushFollow(FOLLOW_cond_in_stat512);
                    cond36=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond36.getTree());

                    RPAR37=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat514); if (state.failed) return retval;

                    pushFollow(FOLLOW_stat_in_stat517);
                    stat38=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat38.getTree());

                    // Ctrl.g:131:31: ( ( ELSE )=> ELSE ! stat )?
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
                            // Ctrl.g:131:33: ( ELSE )=> ELSE ! stat
                            {
                            ELSE39=(Token)match(input,ELSE,FOLLOW_ELSE_in_stat527); if (state.failed) return retval;

                            pushFollow(FOLLOW_stat_in_stat530);
                            stat40=stat();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, stat40.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 7 :
                    // Ctrl.g:135:5: TRY ^ stat ( ( ELSE )=> ELSE ! stat )?
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    TRY41=(Token)match(input,TRY,FOLLOW_TRY_in_stat554); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    TRY41_tree = 
                    (CommonTree)adaptor.create(TRY41)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(TRY41_tree, root_0);
                    }

                    pushFollow(FOLLOW_stat_in_stat557);
                    stat42=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat42.getTree());

                    // Ctrl.g:135:15: ( ( ELSE )=> ELSE ! stat )?
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
                            // Ctrl.g:135:17: ( ELSE )=> ELSE ! stat
                            {
                            ELSE43=(Token)match(input,ELSE,FOLLOW_ELSE_in_stat567); if (state.failed) return retval;

                            pushFollow(FOLLOW_stat_in_stat570);
                            stat44=stat();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, stat44.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 8 :
                    // Ctrl.g:138:5: CHOICE ^ stat ( ( OR )=> OR ! stat )+
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    CHOICE45=(Token)match(input,CHOICE,FOLLOW_CHOICE_in_stat589); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CHOICE45_tree = 
                    (CommonTree)adaptor.create(CHOICE45)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(CHOICE45_tree, root_0);
                    }

                    pushFollow(FOLLOW_stat_in_stat592);
                    stat46=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat46.getTree());

                    // Ctrl.g:138:18: ( ( OR )=> OR ! stat )+
                    int cnt6=0;
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==OR) ) {
                            int LA6_19 = input.LA(2);

                            if ( (synpred3_Ctrl()) ) {
                                alt6=1;
                            }


                        }


                        switch (alt6) {
                    	case 1 :
                    	    // Ctrl.g:138:20: ( OR )=> OR ! stat
                    	    {
                    	    OR47=(Token)match(input,OR,FOLLOW_OR_in_stat602); if (state.failed) return retval;

                    	    pushFollow(FOLLOW_stat_in_stat605);
                    	    stat48=stat();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat48.getTree());

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
                    // Ctrl.g:141:4: expr SEMI !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_expr_in_stat620);
                    expr49=expr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expr49.getTree());

                    SEMI50=(Token)match(input,SEMI,FOLLOW_SEMI_in_stat622); if (state.failed) return retval;

                    }
                    break;
                case 10 :
                    // Ctrl.g:144:4: var_decl SEMI !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_var_decl_in_stat636);
                    var_decl51=var_decl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, var_decl51.getTree());

                    SEMI52=(Token)match(input,SEMI,FOLLOW_SEMI_in_stat638); if (state.failed) return retval;

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
    // Ctrl.g:148:1: cond : cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom ) ;
    public final CtrlParser.cond_return cond() throws RecognitionException {
        CtrlParser.cond_return retval = new CtrlParser.cond_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token BAR54=null;
        CtrlParser.cond_atom_return cond_atom53 =null;

        CtrlParser.cond_atom_return cond_atom55 =null;


        CommonTree BAR54_tree=null;
        RewriteRuleTokenStream stream_BAR=new RewriteRuleTokenStream(adaptor,"token BAR");
        RewriteRuleSubtreeStream stream_cond_atom=new RewriteRuleSubtreeStream(adaptor,"rule cond_atom");
        try {
            // Ctrl.g:149:2: ( cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom ) )
            // Ctrl.g:151:4: cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom )
            {
            pushFollow(FOLLOW_cond_atom_in_cond662);
            cond_atom53=cond_atom();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_cond_atom.add(cond_atom53.getTree());

            // Ctrl.g:152:4: ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom )
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
                    // Ctrl.g:152:6: ( BAR cond_atom )+
                    {
                    // Ctrl.g:152:6: ( BAR cond_atom )+
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
                    	    // Ctrl.g:152:7: BAR cond_atom
                    	    {
                    	    BAR54=(Token)match(input,BAR,FOLLOW_BAR_in_cond671); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_BAR.add(BAR54);


                    	    pushFollow(FOLLOW_cond_atom_in_cond673);
                    	    cond_atom55=cond_atom();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_cond_atom.add(cond_atom55.getTree());

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
                    // 152:23: -> ^( CHOICE cond_atom ( cond_atom )+ )
                    {
                        // Ctrl.g:152:26: ^( CHOICE cond_atom ( cond_atom )+ )
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
                    // Ctrl.g:153:6: 
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
                    // 153:6: -> cond_atom
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
    // Ctrl.g:157:1: cond_atom : ( TRUE | call );
    public final CtrlParser.cond_atom_return cond_atom() throws RecognitionException {
        CtrlParser.cond_atom_return retval = new CtrlParser.cond_atom_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token TRUE56=null;
        CtrlParser.call_return call57 =null;


        CommonTree TRUE56_tree=null;

        try {
            // Ctrl.g:158:2: ( TRUE | call )
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
                    // Ctrl.g:160:4: TRUE
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    TRUE56=(Token)match(input,TRUE,FOLLOW_TRUE_in_cond_atom719); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    TRUE56_tree = 
                    (CommonTree)adaptor.create(TRUE56)
                    ;
                    adaptor.addChild(root_0, TRUE56_tree);
                    }

                    }
                    break;
                case 2 :
                    // Ctrl.g:164:5: call
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_call_in_cond_atom740);
                    call57=call();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, call57.getTree());

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
    // Ctrl.g:167:1: expr : expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 ) ;
    public final CtrlParser.expr_return expr() throws RecognitionException {
        CtrlParser.expr_return retval = new CtrlParser.expr_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token BAR59=null;
        CtrlParser.expr2_return expr258 =null;

        CtrlParser.expr2_return expr260 =null;


        CommonTree BAR59_tree=null;
        RewriteRuleTokenStream stream_BAR=new RewriteRuleTokenStream(adaptor,"token BAR");
        RewriteRuleSubtreeStream stream_expr2=new RewriteRuleSubtreeStream(adaptor,"rule expr2");
        try {
            // Ctrl.g:168:2: ( expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 ) )
            // Ctrl.g:172:4: expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 )
            {
            pushFollow(FOLLOW_expr2_in_expr770);
            expr258=expr2();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_expr2.add(expr258.getTree());

            // Ctrl.g:173:4: ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 )
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==BAR) ) {
                alt12=1;
            }
            else if ( ((LA12_0 >= RPAR && LA12_0 <= SEMI)) ) {
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
                    // Ctrl.g:173:6: ( BAR expr2 )+
                    {
                    // Ctrl.g:173:6: ( BAR expr2 )+
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
                    	    // Ctrl.g:173:7: BAR expr2
                    	    {
                    	    BAR59=(Token)match(input,BAR,FOLLOW_BAR_in_expr778); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_BAR.add(BAR59);


                    	    pushFollow(FOLLOW_expr2_in_expr780);
                    	    expr260=expr2();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_expr2.add(expr260.getTree());

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
                    // 173:19: -> ^( CHOICE expr2 ( expr2 )+ )
                    {
                        // Ctrl.g:173:22: ^( CHOICE expr2 ( expr2 )+ )
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
                    // Ctrl.g:174:6: 
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
                    // 174:6: -> expr2
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
    // Ctrl.g:178:1: expr2 : (e= expr_atom ( PLUS -> ^( BLOCK $e ^( STAR $e) ) | ASTERISK -> ^( STAR $e) | -> $e) | SHARP expr_atom -> ^( ALAP expr_atom ) );
    public final CtrlParser.expr2_return expr2() throws RecognitionException {
        CtrlParser.expr2_return retval = new CtrlParser.expr2_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token PLUS61=null;
        Token ASTERISK62=null;
        Token SHARP63=null;
        CtrlParser.expr_atom_return e =null;

        CtrlParser.expr_atom_return expr_atom64 =null;


        CommonTree PLUS61_tree=null;
        CommonTree ASTERISK62_tree=null;
        CommonTree SHARP63_tree=null;
        RewriteRuleTokenStream stream_PLUS=new RewriteRuleTokenStream(adaptor,"token PLUS");
        RewriteRuleTokenStream stream_SHARP=new RewriteRuleTokenStream(adaptor,"token SHARP");
        RewriteRuleTokenStream stream_ASTERISK=new RewriteRuleTokenStream(adaptor,"token ASTERISK");
        RewriteRuleSubtreeStream stream_expr_atom=new RewriteRuleSubtreeStream(adaptor,"rule expr_atom");
        try {
            // Ctrl.g:179:3: (e= expr_atom ( PLUS -> ^( BLOCK $e ^( STAR $e) ) | ASTERISK -> ^( STAR $e) | -> $e) | SHARP expr_atom -> ^( ALAP expr_atom ) )
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
                    // Ctrl.g:187:5: e= expr_atom ( PLUS -> ^( BLOCK $e ^( STAR $e) ) | ASTERISK -> ^( STAR $e) | -> $e)
                    {
                    pushFollow(FOLLOW_expr_atom_in_expr2861);
                    e=expr_atom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expr_atom.add(e.getTree());

                    // Ctrl.g:188:5: ( PLUS -> ^( BLOCK $e ^( STAR $e) ) | ASTERISK -> ^( STAR $e) | -> $e)
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
                            // Ctrl.g:188:7: PLUS
                            {
                            PLUS61=(Token)match(input,PLUS,FOLLOW_PLUS_in_expr2869); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_PLUS.add(PLUS61);


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
                            // 188:12: -> ^( BLOCK $e ^( STAR $e) )
                            {
                                // Ctrl.g:188:15: ^( BLOCK $e ^( STAR $e) )
                                {
                                CommonTree root_1 = (CommonTree)adaptor.nil();
                                root_1 = (CommonTree)adaptor.becomeRoot(
                                (CommonTree)adaptor.create(BLOCK, "BLOCK")
                                , root_1);

                                adaptor.addChild(root_1, stream_e.nextTree());

                                // Ctrl.g:188:26: ^( STAR $e)
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
                            // Ctrl.g:189:7: ASTERISK
                            {
                            ASTERISK62=(Token)match(input,ASTERISK,FOLLOW_ASTERISK_in_expr2893); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_ASTERISK.add(ASTERISK62);


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
                            // 189:16: -> ^( STAR $e)
                            {
                                // Ctrl.g:189:19: ^( STAR $e)
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
                            // Ctrl.g:190:7: 
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
                            // 190:7: -> $e
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
                    // Ctrl.g:196:5: SHARP expr_atom
                    {
                    SHARP63=(Token)match(input,SHARP,FOLLOW_SHARP_in_expr2945); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SHARP.add(SHARP63);


                    pushFollow(FOLLOW_expr_atom_in_expr2947);
                    expr_atom64=expr_atom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expr_atom.add(expr_atom64.getTree());

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
                    // 196:21: -> ^( ALAP expr_atom )
                    {
                        // Ctrl.g:196:24: ^( ALAP expr_atom )
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
    // Ctrl.g:199:1: expr_atom : ( ANY | OTHER | LPAR ! expr RPAR !| call );
    public final CtrlParser.expr_atom_return expr_atom() throws RecognitionException {
        CtrlParser.expr_atom_return retval = new CtrlParser.expr_atom_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token ANY65=null;
        Token OTHER66=null;
        Token LPAR67=null;
        Token RPAR69=null;
        CtrlParser.expr_return expr68 =null;

        CtrlParser.call_return call70 =null;


        CommonTree ANY65_tree=null;
        CommonTree OTHER66_tree=null;
        CommonTree LPAR67_tree=null;
        CommonTree RPAR69_tree=null;

        try {
            // Ctrl.g:200:2: ( ANY | OTHER | LPAR ! expr RPAR !| call )
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
                    // Ctrl.g:202:4: ANY
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    ANY65=(Token)match(input,ANY,FOLLOW_ANY_in_expr_atom975); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ANY65_tree = 
                    (CommonTree)adaptor.create(ANY65)
                    ;
                    adaptor.addChild(root_0, ANY65_tree);
                    }

                    }
                    break;
                case 2 :
                    // Ctrl.g:206:4: OTHER
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    OTHER66=(Token)match(input,OTHER,FOLLOW_OTHER_in_expr_atom992); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    OTHER66_tree = 
                    (CommonTree)adaptor.create(OTHER66)
                    ;
                    adaptor.addChild(root_0, OTHER66_tree);
                    }

                    }
                    break;
                case 3 :
                    // Ctrl.g:209:4: LPAR ! expr RPAR !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    LPAR67=(Token)match(input,LPAR,FOLLOW_LPAR_in_expr_atom1005); if (state.failed) return retval;

                    pushFollow(FOLLOW_expr_in_expr_atom1008);
                    expr68=expr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expr68.getTree());

                    RPAR69=(Token)match(input,RPAR,FOLLOW_RPAR_in_expr_atom1010); if (state.failed) return retval;

                    }
                    break;
                case 4 :
                    // Ctrl.g:212:4: call
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_call_in_expr_atom1024);
                    call70=call();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, call70.getTree());

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
    // Ctrl.g:216:1: call : rule_name ( arg_list )? -> ^( CALL[$rule_name.text] rule_name ( arg_list )? ) ;
    public final CtrlParser.call_return call() throws RecognitionException {
        CtrlParser.call_return retval = new CtrlParser.call_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        CtrlParser.rule_name_return rule_name71 =null;

        CtrlParser.arg_list_return arg_list72 =null;


        RewriteRuleSubtreeStream stream_arg_list=new RewriteRuleSubtreeStream(adaptor,"rule arg_list");
        RewriteRuleSubtreeStream stream_rule_name=new RewriteRuleSubtreeStream(adaptor,"rule rule_name");
        try {
            // Ctrl.g:217:2: ( rule_name ( arg_list )? -> ^( CALL[$rule_name.text] rule_name ( arg_list )? ) )
            // Ctrl.g:221:4: rule_name ( arg_list )?
            {
            pushFollow(FOLLOW_rule_name_in_call1054);
            rule_name71=rule_name();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rule_name.add(rule_name71.getTree());

            // Ctrl.g:221:14: ( arg_list )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==LPAR) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // Ctrl.g:221:14: arg_list
                    {
                    pushFollow(FOLLOW_arg_list_in_call1056);
                    arg_list72=arg_list();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_arg_list.add(arg_list72.getTree());

                    }
                    break;

            }


            // AST REWRITE
            // elements: rule_name, arg_list
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 222:4: -> ^( CALL[$rule_name.text] rule_name ( arg_list )? )
            {
                // Ctrl.g:222:7: ^( CALL[$rule_name.text] rule_name ( arg_list )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(CALL, (rule_name71!=null?input.toString(rule_name71.start,rule_name71.stop):null))
                , root_1);

                adaptor.addChild(root_1, stream_rule_name.nextTree());

                // Ctrl.g:222:41: ( arg_list )?
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
    // Ctrl.g:228:1: arg_list : LPAR ( arg ( COMMA arg )* )? RPAR -> ^( ARGS ( arg )* ) ;
    public final CtrlParser.arg_list_return arg_list() throws RecognitionException {
        CtrlParser.arg_list_return retval = new CtrlParser.arg_list_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token LPAR73=null;
        Token COMMA75=null;
        Token RPAR77=null;
        CtrlParser.arg_return arg74 =null;

        CtrlParser.arg_return arg76 =null;


        CommonTree LPAR73_tree=null;
        CommonTree COMMA75_tree=null;
        CommonTree RPAR77_tree=null;
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_arg=new RewriteRuleSubtreeStream(adaptor,"rule arg");
        try {
            // Ctrl.g:229:3: ( LPAR ( arg ( COMMA arg )* )? RPAR -> ^( ARGS ( arg )* ) )
            // Ctrl.g:231:5: LPAR ( arg ( COMMA arg )* )? RPAR
            {
            LPAR73=(Token)match(input,LPAR,FOLLOW_LPAR_in_arg_list1096); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAR.add(LPAR73);


            // Ctrl.g:231:10: ( arg ( COMMA arg )* )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==DONT_CARE||LA18_0==FALSE||LA18_0==ID||LA18_0==INT_LIT||LA18_0==OUT||LA18_0==REAL_LIT||(LA18_0 >= STRING_LIT && LA18_0 <= TRUE)) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // Ctrl.g:231:11: arg ( COMMA arg )*
                    {
                    pushFollow(FOLLOW_arg_in_arg_list1099);
                    arg74=arg();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_arg.add(arg74.getTree());

                    // Ctrl.g:231:15: ( COMMA arg )*
                    loop17:
                    do {
                        int alt17=2;
                        int LA17_0 = input.LA(1);

                        if ( (LA17_0==COMMA) ) {
                            alt17=1;
                        }


                        switch (alt17) {
                    	case 1 :
                    	    // Ctrl.g:231:16: COMMA arg
                    	    {
                    	    COMMA75=(Token)match(input,COMMA,FOLLOW_COMMA_in_arg_list1102); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA75);


                    	    pushFollow(FOLLOW_arg_in_arg_list1104);
                    	    arg76=arg();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_arg.add(arg76.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop17;
                        }
                    } while (true);


                    }
                    break;

            }


            RPAR77=(Token)match(input,RPAR,FOLLOW_RPAR_in_arg_list1110); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAR.add(RPAR77);


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
            // 232:5: -> ^( ARGS ( arg )* )
            {
                // Ctrl.g:232:8: ^( ARGS ( arg )* )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(ARGS, "ARGS")
                , root_1);

                // Ctrl.g:232:15: ( arg )*
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
    // Ctrl.g:238:1: arg : ( OUT ID -> ^( ARG OUT ID ) | ID -> ^( ARG ID ) | DONT_CARE -> ^( ARG DONT_CARE ) | literal -> ^( ARG literal ) );
    public final CtrlParser.arg_return arg() throws RecognitionException {
        CtrlParser.arg_return retval = new CtrlParser.arg_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token OUT78=null;
        Token ID79=null;
        Token ID80=null;
        Token DONT_CARE81=null;
        CtrlParser.literal_return literal82 =null;


        CommonTree OUT78_tree=null;
        CommonTree ID79_tree=null;
        CommonTree ID80_tree=null;
        CommonTree DONT_CARE81_tree=null;
        RewriteRuleTokenStream stream_DONT_CARE=new RewriteRuleTokenStream(adaptor,"token DONT_CARE");
        RewriteRuleTokenStream stream_OUT=new RewriteRuleTokenStream(adaptor,"token OUT");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_literal=new RewriteRuleSubtreeStream(adaptor,"rule literal");
        try {
            // Ctrl.g:239:3: ( OUT ID -> ^( ARG OUT ID ) | ID -> ^( ARG ID ) | DONT_CARE -> ^( ARG DONT_CARE ) | literal -> ^( ARG literal ) )
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
                    // Ctrl.g:242:5: OUT ID
                    {
                    OUT78=(Token)match(input,OUT,FOLLOW_OUT_in_arg1153); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_OUT.add(OUT78);


                    ID79=(Token)match(input,ID,FOLLOW_ID_in_arg1155); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID79);


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
                    // 242:12: -> ^( ARG OUT ID )
                    {
                        // Ctrl.g:242:15: ^( ARG OUT ID )
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
                    // Ctrl.g:246:5: ID
                    {
                    ID80=(Token)match(input,ID,FOLLOW_ID_in_arg1186); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID80);


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
                    // 246:8: -> ^( ARG ID )
                    {
                        // Ctrl.g:246:11: ^( ARG ID )
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
                    // Ctrl.g:250:5: DONT_CARE
                    {
                    DONT_CARE81=(Token)match(input,DONT_CARE,FOLLOW_DONT_CARE_in_arg1215); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DONT_CARE.add(DONT_CARE81);


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
                    // 250:15: -> ^( ARG DONT_CARE )
                    {
                        // Ctrl.g:250:18: ^( ARG DONT_CARE )
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
                    // Ctrl.g:251:5: literal
                    {
                    pushFollow(FOLLOW_literal_in_arg1232);
                    literal82=literal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_literal.add(literal82.getTree());

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
                    // 251:13: -> ^( ARG literal )
                    {
                        // Ctrl.g:251:16: ^( ARG literal )
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
    // Ctrl.g:254:1: literal : ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT );
    public final CtrlParser.literal_return literal() throws RecognitionException {
        CtrlParser.literal_return retval = new CtrlParser.literal_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token set83=null;

        CommonTree set83_tree=null;

        try {
            // Ctrl.g:255:3: ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT )
            // Ctrl.g:
            {
            root_0 = (CommonTree)adaptor.nil();


            set83=(Token)input.LT(1);

            if ( input.LA(1)==FALSE||input.LA(1)==INT_LIT||input.LA(1)==REAL_LIT||(input.LA(1) >= STRING_LIT && input.LA(1) <= TRUE) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, 
                (CommonTree)adaptor.create(set83)
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
    // Ctrl.g:273:1: rule_name :ids+= ID ( DOT ids+= ID )* ->;
    public final CtrlParser.rule_name_return rule_name() throws RecognitionException {
        CtrlParser.rule_name_return retval = new CtrlParser.rule_name_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token DOT84=null;
        Token ids=null;
        List list_ids=null;

        CommonTree DOT84_tree=null;
        CommonTree ids_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");

        try {
            // Ctrl.g:274:3: (ids+= ID ( DOT ids+= ID )* ->)
            // Ctrl.g:274:5: ids+= ID ( DOT ids+= ID )*
            {
            ids=(Token)match(input,ID,FOLLOW_ID_in_rule_name1344); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ids);

            if (list_ids==null) list_ids=new ArrayList();
            list_ids.add(ids);


            // Ctrl.g:274:13: ( DOT ids+= ID )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==DOT) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // Ctrl.g:274:14: DOT ids+= ID
            	    {
            	    DOT84=(Token)match(input,DOT,FOLLOW_DOT_in_rule_name1347); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(DOT84);


            	    ids=(Token)match(input,ID,FOLLOW_ID_in_rule_name1351); if (state.failed) return retval; 
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
            // 275:5: ->
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
    // Ctrl.g:279:1: var_decl : var_type ID ( COMMA ID )* -> ^( VAR var_type ( ID )+ ) ;
    public final CtrlParser.var_decl_return var_decl() throws RecognitionException {
        CtrlParser.var_decl_return retval = new CtrlParser.var_decl_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token ID86=null;
        Token COMMA87=null;
        Token ID88=null;
        CtrlParser.var_type_return var_type85 =null;


        CommonTree ID86_tree=null;
        CommonTree COMMA87_tree=null;
        CommonTree ID88_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_var_type=new RewriteRuleSubtreeStream(adaptor,"rule var_type");
        try {
            // Ctrl.g:280:2: ( var_type ID ( COMMA ID )* -> ^( VAR var_type ( ID )+ ) )
            // Ctrl.g:282:4: var_type ID ( COMMA ID )*
            {
            pushFollow(FOLLOW_var_type_in_var_decl1383);
            var_type85=var_type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_var_type.add(var_type85.getTree());

            ID86=(Token)match(input,ID,FOLLOW_ID_in_var_decl1385); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID86);


            // Ctrl.g:282:16: ( COMMA ID )*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);

                if ( (LA21_0==COMMA) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // Ctrl.g:282:17: COMMA ID
            	    {
            	    COMMA87=(Token)match(input,COMMA,FOLLOW_COMMA_in_var_decl1388); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA87);


            	    ID88=(Token)match(input,ID,FOLLOW_ID_in_var_decl1390); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_ID.add(ID88);


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
            // 282:28: -> ^( VAR var_type ( ID )+ )
            {
                // Ctrl.g:282:31: ^( VAR var_type ( ID )+ )
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
    // Ctrl.g:286:1: var_type : ( NODE | BOOL | STRING | INT | REAL );
    public final CtrlParser.var_type_return var_type() throws RecognitionException {
        CtrlParser.var_type_return retval = new CtrlParser.var_type_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token set89=null;

        CommonTree set89_tree=null;

        try {
            // Ctrl.g:287:2: ( NODE | BOOL | STRING | INT | REAL )
            // Ctrl.g:
            {
            root_0 = (CommonTree)adaptor.nil();


            set89=(Token)input.LT(1);

            if ( input.LA(1)==BOOL||input.LA(1)==INT||input.LA(1)==NODE||input.LA(1)==REAL||input.LA(1)==STRING ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, 
                (CommonTree)adaptor.create(set89)
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
        // Ctrl.g:131:33: ( ELSE )
        // Ctrl.g:131:34: ELSE
        {
        match(input,ELSE,FOLLOW_ELSE_in_synpred1_Ctrl522); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred1_Ctrl

    // $ANTLR start synpred2_Ctrl
    public final void synpred2_Ctrl_fragment() throws RecognitionException {
        // Ctrl.g:135:17: ( ELSE )
        // Ctrl.g:135:18: ELSE
        {
        match(input,ELSE,FOLLOW_ELSE_in_synpred2_Ctrl562); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred2_Ctrl

    // $ANTLR start synpred3_Ctrl
    public final void synpred3_Ctrl_fragment() throws RecognitionException {
        // Ctrl.g:138:20: ( OR )
        // Ctrl.g:138:21: OR
        {
        match(input,OR,FOLLOW_OR_in_synpred3_Ctrl597); if (state.failed) return ;

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


 

    public static final BitSet FOLLOW_function_in_program127 = new BitSet(new long[]{0x0B2441133A029052L});
    public static final BitSet FOLLOW_stat_in_program129 = new BitSet(new long[]{0x0B2441133A029052L});
    public static final BitSet FOLLOW_FUNCTION_in_function180 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_function183 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_LPAR_in_function185 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_RPAR_in_function188 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_block_in_function191 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LCURLY_in_block222 = new BitSet(new long[]{0x0B24611338029050L});
    public static final BitSet FOLLOW_stat_in_block224 = new BitSet(new long[]{0x0B24611338029050L});
    public static final BitSet FOLLOW_RCURLY_in_block227 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_stat251 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALAP_in_stat268 = new BitSet(new long[]{0x0B24411338029050L});
    public static final BitSet FOLLOW_stat_in_stat271 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHILE_in_stat292 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_LPAR_in_stat295 = new BitSet(new long[]{0x0080000008000000L});
    public static final BitSet FOLLOW_cond_in_stat298 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat300 = new BitSet(new long[]{0x0B24411338029050L});
    public static final BitSet FOLLOW_stat_in_stat303 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNTIL_in_stat323 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_LPAR_in_stat326 = new BitSet(new long[]{0x0080000008000000L});
    public static final BitSet FOLLOW_cond_in_stat329 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat331 = new BitSet(new long[]{0x0B24411338029050L});
    public static final BitSet FOLLOW_stat_in_stat334 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DO_in_stat339 = new BitSet(new long[]{0x0B24411338029050L});
    public static final BitSet FOLLOW_stat_in_stat341 = new BitSet(new long[]{0x0A00000000000000L});
    public static final BitSet FOLLOW_WHILE_in_stat384 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_LPAR_in_stat386 = new BitSet(new long[]{0x0080000008000000L});
    public static final BitSet FOLLOW_cond_in_stat388 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat390 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNTIL_in_stat453 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_LPAR_in_stat455 = new BitSet(new long[]{0x0080000008000000L});
    public static final BitSet FOLLOW_cond_in_stat457 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat459 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IF_in_stat506 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_LPAR_in_stat509 = new BitSet(new long[]{0x0080000008000000L});
    public static final BitSet FOLLOW_cond_in_stat512 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat514 = new BitSet(new long[]{0x0B24411338029050L});
    public static final BitSet FOLLOW_stat_in_stat517 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_ELSE_in_stat527 = new BitSet(new long[]{0x0B24411338029050L});
    public static final BitSet FOLLOW_stat_in_stat530 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRY_in_stat554 = new BitSet(new long[]{0x0B24411338029050L});
    public static final BitSet FOLLOW_stat_in_stat557 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_ELSE_in_stat567 = new BitSet(new long[]{0x0B24411338029050L});
    public static final BitSet FOLLOW_stat_in_stat570 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CHOICE_in_stat589 = new BitSet(new long[]{0x0B24411338029050L});
    public static final BitSet FOLLOW_stat_in_stat592 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_OR_in_stat602 = new BitSet(new long[]{0x0B24411338029050L});
    public static final BitSet FOLLOW_stat_in_stat605 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_expr_in_stat620 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_SEMI_in_stat622 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_decl_in_stat636 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_SEMI_in_stat638 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_cond_atom_in_cond662 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_BAR_in_cond671 = new BitSet(new long[]{0x0080000008000000L});
    public static final BitSet FOLLOW_cond_atom_in_cond673 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_TRUE_in_cond_atom719 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_cond_atom740 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr2_in_expr770 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_BAR_in_expr778 = new BitSet(new long[]{0x0004010208000040L});
    public static final BitSet FOLLOW_expr2_in_expr780 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_expr_atom_in_expr2861 = new BitSet(new long[]{0x0000040000000202L});
    public static final BitSet FOLLOW_PLUS_in_expr2869 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASTERISK_in_expr2893 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SHARP_in_expr2945 = new BitSet(new long[]{0x0000010208000040L});
    public static final BitSet FOLLOW_expr_atom_in_expr2947 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_expr_atom975 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_expr_atom992 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_expr_atom1005 = new BitSet(new long[]{0x0004010208000040L});
    public static final BitSet FOLLOW_expr_in_expr_atom1008 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_RPAR_in_expr_atom1010 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_expr_atom1024 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_name_in_call1054 = new BitSet(new long[]{0x0000000200000002L});
    public static final BitSet FOLLOW_arg_list_in_call1056 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_arg_list1096 = new BitSet(new long[]{0x00C1820049040000L});
    public static final BitSet FOLLOW_arg_in_arg_list1099 = new BitSet(new long[]{0x0001000000010000L});
    public static final BitSet FOLLOW_COMMA_in_arg_list1102 = new BitSet(new long[]{0x00C0820049040000L});
    public static final BitSet FOLLOW_arg_in_arg_list1104 = new BitSet(new long[]{0x0001000000010000L});
    public static final BitSet FOLLOW_RPAR_in_arg_list1110 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OUT_in_arg1153 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_arg1155 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_arg1186 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DONT_CARE_in_arg1215 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_arg1232 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_rule_name1344 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_DOT_in_rule_name1347 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_rule_name1351 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_var_type_in_var_decl1383 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_var_decl1385 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_COMMA_in_var_decl1388 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_var_decl1390 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_ELSE_in_synpred1_Ctrl522 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ELSE_in_synpred2_Ctrl562 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_synpred3_Ctrl597 = new BitSet(new long[]{0x0000000000000002L});

}