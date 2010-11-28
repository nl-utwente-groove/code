// $ANTLR 3.2 Sep 23, 2009 12:02:23 GCLNew.g 2010-11-28 11:13:35

package groove.control.parse;
import groove.control.*;
import java.util.LinkedList;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.antlr.runtime.tree.*;

public class GCLNewParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ARG", "ARGS", "BLOCK", "CALL", "DO_WHILE", "DO_UNTIL", "FUNCTION", "FUNCTIONS", "PROGRAM", "VAR", "LCURLY", "RCURLY", "ID", "LPAR", "RPAR", "ALAP", "WHILE", "UNTIL", "DO", "IF", "ELSE", "TRY", "CHOICE", "OR", "SEMI", "BAR", "TRUE", "PLUS", "ASTERISK", "SHARP", "ANY", "OTHER", "DOT", "COMMA", "NODE", "BOOL", "STRING", "INT", "REAL", "OUT", "DONT_CARE", "FALSE", "STRING_LIT", "INT_LIT", "REAL_LIT", "STAR", "IntegerNumber", "NonIntegerNumber", "QUOTE", "EscapeSequence", "BSLASH", "AMP", "NOT", "MINUS", "ML_COMMENT", "SL_COMMENT", "WS"
    };
    public static final int REAL_LIT=48;
    public static final int FUNCTION=10;
    public static final int DO_UNTIL=9;
    public static final int STAR=49;
    public static final int INT_LIT=47;
    public static final int FUNCTIONS=11;
    public static final int WHILE=20;
    public static final int IntegerNumber=50;
    public static final int AMP=55;
    public static final int STRING_LIT=46;
    public static final int DO=22;
    public static final int NOT=56;
    public static final int ALAP=19;
    public static final int ID=16;
    public static final int EOF=-1;
    public static final int ASTERISK=32;
    public static final int IF=23;
    public static final int ML_COMMENT=58;
    public static final int QUOTE=52;
    public static final int LPAR=17;
    public static final int ARG=4;
    public static final int COMMA=37;
    public static final int NonIntegerNumber=51;
    public static final int DO_WHILE=8;
    public static final int ARGS=5;
    public static final int PLUS=31;
    public static final int VAR=13;
    public static final int DOT=36;
    public static final int CHOICE=26;
    public static final int SHARP=33;
    public static final int OTHER=35;
    public static final int NODE=38;
    public static final int ELSE=24;
    public static final int BOOL=39;
    public static final int LCURLY=14;
    public static final int INT=41;
    public static final int MINUS=57;
    public static final int SEMI=28;
    public static final int TRUE=30;
    public static final int TRY=25;
    public static final int DONT_CARE=44;
    public static final int REAL=42;
    public static final int WS=60;
    public static final int ANY=34;
    public static final int OUT=43;
    public static final int UNTIL=21;
    public static final int BLOCK=6;
    public static final int SL_COMMENT=59;
    public static final int RCURLY=15;
    public static final int OR=27;
    public static final int RPAR=18;
    public static final int PROGRAM=12;
    public static final int CALL=7;
    public static final int FALSE=45;
    public static final int BSLASH=54;
    public static final int EscapeSequence=53;
    public static final int BAR=29;
    public static final int STRING=40;

    // delegates
    // delegators


        public GCLNewParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public GCLNewParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return GCLNewParser.tokenNames; }
    public String getGrammarFileName() { return "GCLNew.g"; }


        /** Lexer for the GCL language. */
        private static GCLNewLexer lexer = new GCLNewLexer(null);
        /** Helper class to convert AST trees to namespace. */
        private GCLHelper helper;
        
        public void displayRecognitionError(String[] tokenNames,
                                            RecognitionException e) {
            String hdr = getErrorHeader(e);
            String msg = getErrorMessage(e, tokenNames);
            helper.addError(hdr + " " + msg);
        }
        
        public List<String> getErrors() {
            return helper.getErrors();
        }

        /**
         * Runs the lexer and parser on a given input character stream,
         * with a (presumably empty) namespace.
         * @return the resulting syntax tree
         */
        public MyTree run(CharStream input, NamespaceNew namespace) throws RecognitionException {
            this.helper = new GCLHelper(this, namespace);
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
    // GCLNew.g:67:1: program : ( function | stat )* -> ^( PROGRAM ^( FUNCTIONS ( function )* ) ^( BLOCK ( stat )* ) ) ;
    public final GCLNewParser.program_return program() throws RecognitionException {
        GCLNewParser.program_return retval = new GCLNewParser.program_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        GCLNewParser.function_return function1 = null;

        GCLNewParser.stat_return stat2 = null;


        RewriteRuleSubtreeStream stream_stat=new RewriteRuleSubtreeStream(adaptor,"rule stat");
        RewriteRuleSubtreeStream stream_function=new RewriteRuleSubtreeStream(adaptor,"rule function");
        try {
            // GCLNew.g:68:3: ( ( function | stat )* -> ^( PROGRAM ^( FUNCTIONS ( function )* ) ^( BLOCK ( stat )* ) ) )
            // GCLNew.g:68:5: ( function | stat )*
            {
            // GCLNew.g:68:5: ( function | stat )*
            loop1:
            do {
                int alt1=3;
                alt1 = dfa1.predict(input);
                switch (alt1) {
            	case 1 :
            	    // GCLNew.g:68:6: function
            	    {
            	    pushFollow(FOLLOW_function_in_program110);
            	    function1=function();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_function.add(function1.getTree());

            	    }
            	    break;
            	case 2 :
            	    // GCLNew.g:68:15: stat
            	    {
            	    pushFollow(FOLLOW_stat_in_program112);
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
            // elements: function, stat
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 69:5: -> ^( PROGRAM ^( FUNCTIONS ( function )* ) ^( BLOCK ( stat )* ) )
            {
                // GCLNew.g:69:8: ^( PROGRAM ^( FUNCTIONS ( function )* ) ^( BLOCK ( stat )* ) )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(PROGRAM, "PROGRAM"), root_1);

                // GCLNew.g:69:18: ^( FUNCTIONS ( function )* )
                {
                CommonTree root_2 = (CommonTree)adaptor.nil();
                root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(FUNCTIONS, "FUNCTIONS"), root_2);

                // GCLNew.g:69:30: ( function )*
                while ( stream_function.hasNext() ) {
                    adaptor.addChild(root_2, stream_function.nextTree());

                }
                stream_function.reset();

                adaptor.addChild(root_1, root_2);
                }
                // GCLNew.g:69:41: ^( BLOCK ( stat )* )
                {
                CommonTree root_2 = (CommonTree)adaptor.nil();
                root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(BLOCK, "BLOCK"), root_2);

                // GCLNew.g:69:49: ( stat )*
                while ( stream_stat.hasNext() ) {
                    adaptor.addChild(root_2, stream_stat.nextTree());

                }
                stream_stat.reset();

                adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
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
        }
        return retval;
    }
    // $ANTLR end "program"

    public static class block_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "block"
    // GCLNew.g:72:1: block : LCURLY ( stat )* RCURLY -> ^( BLOCK ( stat )* ) ;
    public final GCLNewParser.block_return block() throws RecognitionException {
        GCLNewParser.block_return retval = new GCLNewParser.block_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token LCURLY3=null;
        Token RCURLY5=null;
        GCLNewParser.stat_return stat4 = null;


        CommonTree LCURLY3_tree=null;
        CommonTree RCURLY5_tree=null;
        RewriteRuleTokenStream stream_LCURLY=new RewriteRuleTokenStream(adaptor,"token LCURLY");
        RewriteRuleTokenStream stream_RCURLY=new RewriteRuleTokenStream(adaptor,"token RCURLY");
        RewriteRuleSubtreeStream stream_stat=new RewriteRuleSubtreeStream(adaptor,"rule stat");
        try {
            // GCLNew.g:73:2: ( LCURLY ( stat )* RCURLY -> ^( BLOCK ( stat )* ) )
            // GCLNew.g:73:4: LCURLY ( stat )* RCURLY
            {
            LCURLY3=(Token)match(input,LCURLY,FOLLOW_LCURLY_in_block150); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LCURLY.add(LCURLY3);

            // GCLNew.g:73:11: ( stat )*
            loop2:
            do {
                int alt2=2;
                alt2 = dfa2.predict(input);
                switch (alt2) {
            	case 1 :
            	    // GCLNew.g:73:11: stat
            	    {
            	    pushFollow(FOLLOW_stat_in_block152);
            	    stat4=stat();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_stat.add(stat4.getTree());

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            RCURLY5=(Token)match(input,RCURLY,FOLLOW_RCURLY_in_block155); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RCURLY.add(RCURLY5);



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
            // 73:24: -> ^( BLOCK ( stat )* )
            {
                // GCLNew.g:73:27: ^( BLOCK ( stat )* )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(BLOCK, "BLOCK"), root_1);

                // GCLNew.g:73:35: ( stat )*
                while ( stream_stat.hasNext() ) {
                    adaptor.addChild(root_1, stream_stat.nextTree());

                }
                stream_stat.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
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
        }
        return retval;
    }
    // $ANTLR end "block"

    public static class function_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "function"
    // GCLNew.g:75:1: function : FUNCTION ID LPAR RPAR block ;
    public final GCLNewParser.function_return function() throws RecognitionException {
        GCLNewParser.function_return retval = new GCLNewParser.function_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token FUNCTION6=null;
        Token ID7=null;
        Token LPAR8=null;
        Token RPAR9=null;
        GCLNewParser.block_return block10 = null;


        CommonTree FUNCTION6_tree=null;
        CommonTree ID7_tree=null;
        CommonTree LPAR8_tree=null;
        CommonTree RPAR9_tree=null;

        try {
            // GCLNew.g:76:3: ( FUNCTION ID LPAR RPAR block )
            // GCLNew.g:76:5: FUNCTION ID LPAR RPAR block
            {
            root_0 = (CommonTree)adaptor.nil();

            FUNCTION6=(Token)match(input,FUNCTION,FOLLOW_FUNCTION_in_function174); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            FUNCTION6_tree = (CommonTree)adaptor.create(FUNCTION6);
            root_0 = (CommonTree)adaptor.becomeRoot(FUNCTION6_tree, root_0);
            }
            ID7=(Token)match(input,ID,FOLLOW_ID_in_function177); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID7_tree = (CommonTree)adaptor.create(ID7);
            adaptor.addChild(root_0, ID7_tree);
            }
            LPAR8=(Token)match(input,LPAR,FOLLOW_LPAR_in_function179); if (state.failed) return retval;
            RPAR9=(Token)match(input,RPAR,FOLLOW_RPAR_in_function182); if (state.failed) return retval;
            pushFollow(FOLLOW_block_in_function185);
            block10=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, block10.getTree());
            if ( state.backtracking==0 ) {
               helper.declareFunction(FUNCTION6_tree); 
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
        }
        return retval;
    }
    // $ANTLR end "function"

    public static class stat_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "stat"
    // GCLNew.g:80:1: stat : ( block | ALAP stat | WHILE LPAR cond RPAR stat | UNTIL LPAR cond RPAR stat | DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) ) | IF LPAR cond RPAR stat ( ( ELSE )=> ELSE stat )? | TRY stat ( ( ELSE )=> ELSE stat )? | CHOICE stat ( ( OR )=> OR stat )+ | expr SEMI | var_decl SEMI );
    public final GCLNewParser.stat_return stat() throws RecognitionException {
        GCLNewParser.stat_return retval = new GCLNewParser.stat_return();
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
        GCLNewParser.block_return block11 = null;

        GCLNewParser.stat_return stat13 = null;

        GCLNewParser.cond_return cond16 = null;

        GCLNewParser.stat_return stat18 = null;

        GCLNewParser.cond_return cond21 = null;

        GCLNewParser.stat_return stat23 = null;

        GCLNewParser.stat_return stat25 = null;

        GCLNewParser.cond_return cond28 = null;

        GCLNewParser.cond_return cond32 = null;

        GCLNewParser.cond_return cond36 = null;

        GCLNewParser.stat_return stat38 = null;

        GCLNewParser.stat_return stat40 = null;

        GCLNewParser.stat_return stat42 = null;

        GCLNewParser.stat_return stat44 = null;

        GCLNewParser.stat_return stat46 = null;

        GCLNewParser.stat_return stat48 = null;

        GCLNewParser.expr_return expr49 = null;

        GCLNewParser.var_decl_return var_decl51 = null;


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
            // GCLNew.g:81:2: ( block | ALAP stat | WHILE LPAR cond RPAR stat | UNTIL LPAR cond RPAR stat | DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) ) | IF LPAR cond RPAR stat ( ( ELSE )=> ELSE stat )? | TRY stat ( ( ELSE )=> ELSE stat )? | CHOICE stat ( ( OR )=> OR stat )+ | expr SEMI | var_decl SEMI )
            int alt7=10;
            alt7 = dfa7.predict(input);
            switch (alt7) {
                case 1 :
                    // GCLNew.g:81:4: block
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_block_in_stat203);
                    block11=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block11.getTree());

                    }
                    break;
                case 2 :
                    // GCLNew.g:82:4: ALAP stat
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    ALAP12=(Token)match(input,ALAP,FOLLOW_ALAP_in_stat208); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ALAP12_tree = (CommonTree)adaptor.create(ALAP12);
                    root_0 = (CommonTree)adaptor.becomeRoot(ALAP12_tree, root_0);
                    }
                    pushFollow(FOLLOW_stat_in_stat211);
                    stat13=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat13.getTree());

                    }
                    break;
                case 3 :
                    // GCLNew.g:83:4: WHILE LPAR cond RPAR stat
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    WHILE14=(Token)match(input,WHILE,FOLLOW_WHILE_in_stat216); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    WHILE14_tree = (CommonTree)adaptor.create(WHILE14);
                    root_0 = (CommonTree)adaptor.becomeRoot(WHILE14_tree, root_0);
                    }
                    LPAR15=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat219); if (state.failed) return retval;
                    pushFollow(FOLLOW_cond_in_stat222);
                    cond16=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond16.getTree());
                    RPAR17=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat224); if (state.failed) return retval;
                    pushFollow(FOLLOW_stat_in_stat227);
                    stat18=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat18.getTree());

                    }
                    break;
                case 4 :
                    // GCLNew.g:84:4: UNTIL LPAR cond RPAR stat
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    UNTIL19=(Token)match(input,UNTIL,FOLLOW_UNTIL_in_stat232); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    UNTIL19_tree = (CommonTree)adaptor.create(UNTIL19);
                    root_0 = (CommonTree)adaptor.becomeRoot(UNTIL19_tree, root_0);
                    }
                    LPAR20=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat235); if (state.failed) return retval;
                    pushFollow(FOLLOW_cond_in_stat238);
                    cond21=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond21.getTree());
                    RPAR22=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat240); if (state.failed) return retval;
                    pushFollow(FOLLOW_stat_in_stat243);
                    stat23=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat23.getTree());

                    }
                    break;
                case 5 :
                    // GCLNew.g:85:4: DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) )
                    {
                    DO24=(Token)match(input,DO,FOLLOW_DO_in_stat248); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DO.add(DO24);

                    pushFollow(FOLLOW_stat_in_stat250);
                    stat25=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_stat.add(stat25.getTree());
                    // GCLNew.g:86:4: ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) )
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
                            // GCLNew.g:86:6: WHILE LPAR cond RPAR
                            {
                            WHILE26=(Token)match(input,WHILE,FOLLOW_WHILE_in_stat258); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_WHILE.add(WHILE26);

                            LPAR27=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat260); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_LPAR.add(LPAR27);

                            pushFollow(FOLLOW_cond_in_stat262);
                            cond28=cond();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_cond.add(cond28.getTree());
                            RPAR29=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat264); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_RPAR.add(RPAR29);



                            // AST REWRITE
                            // elements: WHILE, stat, cond, stat
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (CommonTree)adaptor.nil();
                            // 86:27: -> ^( BLOCK stat ^( WHILE cond stat ) )
                            {
                                // GCLNew.g:86:30: ^( BLOCK stat ^( WHILE cond stat ) )
                                {
                                CommonTree root_1 = (CommonTree)adaptor.nil();
                                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(BLOCK, "BLOCK"), root_1);

                                adaptor.addChild(root_1, stream_stat.nextTree());
                                // GCLNew.g:86:43: ^( WHILE cond stat )
                                {
                                CommonTree root_2 = (CommonTree)adaptor.nil();
                                root_2 = (CommonTree)adaptor.becomeRoot(stream_WHILE.nextNode(), root_2);

                                adaptor.addChild(root_2, stream_cond.nextTree());
                                adaptor.addChild(root_2, stream_stat.nextTree());

                                adaptor.addChild(root_1, root_2);
                                }

                                adaptor.addChild(root_0, root_1);
                                }

                            }

                            retval.tree = root_0;}
                            }
                            break;
                        case 2 :
                            // GCLNew.g:87:6: UNTIL LPAR cond RPAR
                            {
                            UNTIL30=(Token)match(input,UNTIL,FOLLOW_UNTIL_in_stat287); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_UNTIL.add(UNTIL30);

                            LPAR31=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat289); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_LPAR.add(LPAR31);

                            pushFollow(FOLLOW_cond_in_stat291);
                            cond32=cond();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_cond.add(cond32.getTree());
                            RPAR33=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat293); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_RPAR.add(RPAR33);



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
                            // 87:27: -> ^( BLOCK stat ^( UNTIL cond stat ) )
                            {
                                // GCLNew.g:87:30: ^( BLOCK stat ^( UNTIL cond stat ) )
                                {
                                CommonTree root_1 = (CommonTree)adaptor.nil();
                                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(BLOCK, "BLOCK"), root_1);

                                adaptor.addChild(root_1, stream_stat.nextTree());
                                // GCLNew.g:87:43: ^( UNTIL cond stat )
                                {
                                CommonTree root_2 = (CommonTree)adaptor.nil();
                                root_2 = (CommonTree)adaptor.becomeRoot(stream_UNTIL.nextNode(), root_2);

                                adaptor.addChild(root_2, stream_cond.nextTree());
                                adaptor.addChild(root_2, stream_stat.nextTree());

                                adaptor.addChild(root_1, root_2);
                                }

                                adaptor.addChild(root_0, root_1);
                                }

                            }

                            retval.tree = root_0;}
                            }
                            break;

                    }


                    }
                    break;
                case 6 :
                    // GCLNew.g:89:5: IF LPAR cond RPAR stat ( ( ELSE )=> ELSE stat )?
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    IF34=(Token)match(input,IF,FOLLOW_IF_in_stat320); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IF34_tree = (CommonTree)adaptor.create(IF34);
                    root_0 = (CommonTree)adaptor.becomeRoot(IF34_tree, root_0);
                    }
                    LPAR35=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat323); if (state.failed) return retval;
                    pushFollow(FOLLOW_cond_in_stat326);
                    cond36=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond36.getTree());
                    RPAR37=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat328); if (state.failed) return retval;
                    pushFollow(FOLLOW_stat_in_stat331);
                    stat38=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat38.getTree());
                    // GCLNew.g:89:31: ( ( ELSE )=> ELSE stat )?
                    int alt4=2;
                    alt4 = dfa4.predict(input);
                    switch (alt4) {
                        case 1 :
                            // GCLNew.g:89:33: ( ELSE )=> ELSE stat
                            {
                            ELSE39=(Token)match(input,ELSE,FOLLOW_ELSE_in_stat341); if (state.failed) return retval;
                            pushFollow(FOLLOW_stat_in_stat344);
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
                    // GCLNew.g:90:5: TRY stat ( ( ELSE )=> ELSE stat )?
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    TRY41=(Token)match(input,TRY,FOLLOW_TRY_in_stat353); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    TRY41_tree = (CommonTree)adaptor.create(TRY41);
                    root_0 = (CommonTree)adaptor.becomeRoot(TRY41_tree, root_0);
                    }
                    pushFollow(FOLLOW_stat_in_stat356);
                    stat42=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat42.getTree());
                    // GCLNew.g:90:15: ( ( ELSE )=> ELSE stat )?
                    int alt5=2;
                    alt5 = dfa5.predict(input);
                    switch (alt5) {
                        case 1 :
                            // GCLNew.g:90:17: ( ELSE )=> ELSE stat
                            {
                            ELSE43=(Token)match(input,ELSE,FOLLOW_ELSE_in_stat366); if (state.failed) return retval;
                            pushFollow(FOLLOW_stat_in_stat369);
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
                    // GCLNew.g:91:5: CHOICE stat ( ( OR )=> OR stat )+
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    CHOICE45=(Token)match(input,CHOICE,FOLLOW_CHOICE_in_stat378); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CHOICE45_tree = (CommonTree)adaptor.create(CHOICE45);
                    root_0 = (CommonTree)adaptor.becomeRoot(CHOICE45_tree, root_0);
                    }
                    pushFollow(FOLLOW_stat_in_stat381);
                    stat46=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat46.getTree());
                    // GCLNew.g:91:18: ( ( OR )=> OR stat )+
                    int cnt6=0;
                    loop6:
                    do {
                        int alt6=2;
                        alt6 = dfa6.predict(input);
                        switch (alt6) {
                    	case 1 :
                    	    // GCLNew.g:91:20: ( OR )=> OR stat
                    	    {
                    	    OR47=(Token)match(input,OR,FOLLOW_OR_in_stat391); if (state.failed) return retval;
                    	    pushFollow(FOLLOW_stat_in_stat394);
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
                    // GCLNew.g:92:4: expr SEMI
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_expr_in_stat401);
                    expr49=expr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expr49.getTree());
                    SEMI50=(Token)match(input,SEMI,FOLLOW_SEMI_in_stat403); if (state.failed) return retval;

                    }
                    break;
                case 10 :
                    // GCLNew.g:93:4: var_decl SEMI
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_var_decl_in_stat409);
                    var_decl51=var_decl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, var_decl51.getTree());
                    SEMI52=(Token)match(input,SEMI,FOLLOW_SEMI_in_stat411); if (state.failed) return retval;

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
        }
        return retval;
    }
    // $ANTLR end "stat"

    public static class cond_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "cond"
    // GCLNew.g:96:1: cond : cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom ) ;
    public final GCLNewParser.cond_return cond() throws RecognitionException {
        GCLNewParser.cond_return retval = new GCLNewParser.cond_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token BAR54=null;
        GCLNewParser.cond_atom_return cond_atom53 = null;

        GCLNewParser.cond_atom_return cond_atom55 = null;


        CommonTree BAR54_tree=null;
        RewriteRuleTokenStream stream_BAR=new RewriteRuleTokenStream(adaptor,"token BAR");
        RewriteRuleSubtreeStream stream_cond_atom=new RewriteRuleSubtreeStream(adaptor,"rule cond_atom");
        try {
            // GCLNew.g:97:2: ( cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom ) )
            // GCLNew.g:97:4: cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom )
            {
            pushFollow(FOLLOW_cond_atom_in_cond424);
            cond_atom53=cond_atom();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_cond_atom.add(cond_atom53.getTree());
            // GCLNew.g:98:4: ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom )
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
                    // GCLNew.g:98:6: ( BAR cond_atom )+
                    {
                    // GCLNew.g:98:6: ( BAR cond_atom )+
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
                    	    // GCLNew.g:98:7: BAR cond_atom
                    	    {
                    	    BAR54=(Token)match(input,BAR,FOLLOW_BAR_in_cond433); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_BAR.add(BAR54);

                    	    pushFollow(FOLLOW_cond_atom_in_cond435);
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
                    // 98:23: -> ^( CHOICE cond_atom ( cond_atom )+ )
                    {
                        // GCLNew.g:98:26: ^( CHOICE cond_atom ( cond_atom )+ )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(CHOICE, "CHOICE"), root_1);

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

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // GCLNew.g:99:6: 
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
                    // 99:6: -> cond_atom
                    {
                        adaptor.addChild(root_0, stream_cond_atom.nextTree());

                    }

                    retval.tree = root_0;}
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
        }
        return retval;
    }
    // $ANTLR end "cond"

    public static class cond_atom_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "cond_atom"
    // GCLNew.g:103:1: cond_atom : ( TRUE | call );
    public final GCLNewParser.cond_atom_return cond_atom() throws RecognitionException {
        GCLNewParser.cond_atom_return retval = new GCLNewParser.cond_atom_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token TRUE56=null;
        GCLNewParser.call_return call57 = null;


        CommonTree TRUE56_tree=null;

        try {
            // GCLNew.g:104:2: ( TRUE | call )
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
                    // GCLNew.g:104:4: TRUE
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    TRUE56=(Token)match(input,TRUE,FOLLOW_TRUE_in_cond_atom473); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    TRUE56_tree = (CommonTree)adaptor.create(TRUE56);
                    adaptor.addChild(root_0, TRUE56_tree);
                    }

                    }
                    break;
                case 2 :
                    // GCLNew.g:104:11: call
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_call_in_cond_atom477);
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
        }
        return retval;
    }
    // $ANTLR end "cond_atom"

    public static class expr_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "expr"
    // GCLNew.g:106:1: expr : expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 ) ;
    public final GCLNewParser.expr_return expr() throws RecognitionException {
        GCLNewParser.expr_return retval = new GCLNewParser.expr_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token BAR59=null;
        GCLNewParser.expr2_return expr258 = null;

        GCLNewParser.expr2_return expr260 = null;


        CommonTree BAR59_tree=null;
        RewriteRuleTokenStream stream_BAR=new RewriteRuleTokenStream(adaptor,"token BAR");
        RewriteRuleSubtreeStream stream_expr2=new RewriteRuleSubtreeStream(adaptor,"rule expr2");
        try {
            // GCLNew.g:107:2: ( expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 ) )
            // GCLNew.g:107:4: expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 )
            {
            pushFollow(FOLLOW_expr2_in_expr488);
            expr258=expr2();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_expr2.add(expr258.getTree());
            // GCLNew.g:108:4: ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 )
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
                    // GCLNew.g:108:6: ( BAR expr2 )+
                    {
                    // GCLNew.g:108:6: ( BAR expr2 )+
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
                    	    // GCLNew.g:108:7: BAR expr2
                    	    {
                    	    BAR59=(Token)match(input,BAR,FOLLOW_BAR_in_expr496); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_BAR.add(BAR59);

                    	    pushFollow(FOLLOW_expr2_in_expr498);
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
                    // 108:19: -> ^( CHOICE expr2 ( expr2 )+ )
                    {
                        // GCLNew.g:108:22: ^( CHOICE expr2 ( expr2 )+ )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(CHOICE, "CHOICE"), root_1);

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

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // GCLNew.g:109:6: 
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
                    // 109:6: -> expr2
                    {
                        adaptor.addChild(root_0, stream_expr2.nextTree());

                    }

                    retval.tree = root_0;}
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
        }
        return retval;
    }
    // $ANTLR end "expr"

    public static class expr2_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "expr2"
    // GCLNew.g:113:1: expr2 : (e= expr_atom ( PLUS -> ^( BLOCK $e ^( STAR $e) ) | ASTERISK -> ^( STAR $e) | -> $e) | SHARP expr_atom -> ^( ALAP expr_atom ) );
    public final GCLNewParser.expr2_return expr2() throws RecognitionException {
        GCLNewParser.expr2_return retval = new GCLNewParser.expr2_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token PLUS61=null;
        Token ASTERISK62=null;
        Token SHARP63=null;
        GCLNewParser.expr_atom_return e = null;

        GCLNewParser.expr_atom_return expr_atom64 = null;


        CommonTree PLUS61_tree=null;
        CommonTree ASTERISK62_tree=null;
        CommonTree SHARP63_tree=null;
        RewriteRuleTokenStream stream_PLUS=new RewriteRuleTokenStream(adaptor,"token PLUS");
        RewriteRuleTokenStream stream_SHARP=new RewriteRuleTokenStream(adaptor,"token SHARP");
        RewriteRuleTokenStream stream_ASTERISK=new RewriteRuleTokenStream(adaptor,"token ASTERISK");
        RewriteRuleSubtreeStream stream_expr_atom=new RewriteRuleSubtreeStream(adaptor,"rule expr_atom");
        try {
            // GCLNew.g:114:3: (e= expr_atom ( PLUS -> ^( BLOCK $e ^( STAR $e) ) | ASTERISK -> ^( STAR $e) | -> $e) | SHARP expr_atom -> ^( ALAP expr_atom ) )
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( ((LA14_0>=ID && LA14_0<=LPAR)||(LA14_0>=ANY && LA14_0<=OTHER)) ) {
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
                    // GCLNew.g:114:5: e= expr_atom ( PLUS -> ^( BLOCK $e ^( STAR $e) ) | ASTERISK -> ^( STAR $e) | -> $e)
                    {
                    pushFollow(FOLLOW_expr_atom_in_expr2539);
                    e=expr_atom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expr_atom.add(e.getTree());
                    // GCLNew.g:115:5: ( PLUS -> ^( BLOCK $e ^( STAR $e) ) | ASTERISK -> ^( STAR $e) | -> $e)
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
                    case RPAR:
                    case SEMI:
                    case BAR:
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
                            // GCLNew.g:115:7: PLUS
                            {
                            PLUS61=(Token)match(input,PLUS,FOLLOW_PLUS_in_expr2547); if (state.failed) return retval; 
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
                            // 115:12: -> ^( BLOCK $e ^( STAR $e) )
                            {
                                // GCLNew.g:115:15: ^( BLOCK $e ^( STAR $e) )
                                {
                                CommonTree root_1 = (CommonTree)adaptor.nil();
                                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(BLOCK, "BLOCK"), root_1);

                                adaptor.addChild(root_1, stream_e.nextTree());
                                // GCLNew.g:115:26: ^( STAR $e)
                                {
                                CommonTree root_2 = (CommonTree)adaptor.nil();
                                root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(STAR, "STAR"), root_2);

                                adaptor.addChild(root_2, stream_e.nextTree());

                                adaptor.addChild(root_1, root_2);
                                }

                                adaptor.addChild(root_0, root_1);
                                }

                            }

                            retval.tree = root_0;}
                            }
                            break;
                        case 2 :
                            // GCLNew.g:116:7: ASTERISK
                            {
                            ASTERISK62=(Token)match(input,ASTERISK,FOLLOW_ASTERISK_in_expr2571); if (state.failed) return retval; 
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
                            // 116:16: -> ^( STAR $e)
                            {
                                // GCLNew.g:116:19: ^( STAR $e)
                                {
                                CommonTree root_1 = (CommonTree)adaptor.nil();
                                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(STAR, "STAR"), root_1);

                                adaptor.addChild(root_1, stream_e.nextTree());

                                adaptor.addChild(root_0, root_1);
                                }

                            }

                            retval.tree = root_0;}
                            }
                            break;
                        case 3 :
                            // GCLNew.g:117:7: 
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
                            // 117:7: -> $e
                            {
                                adaptor.addChild(root_0, stream_e.nextTree());

                            }

                            retval.tree = root_0;}
                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // GCLNew.g:119:5: SHARP expr_atom
                    {
                    SHARP63=(Token)match(input,SHARP,FOLLOW_SHARP_in_expr2603); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SHARP.add(SHARP63);

                    pushFollow(FOLLOW_expr_atom_in_expr2605);
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
                    // 119:21: -> ^( ALAP expr_atom )
                    {
                        // GCLNew.g:119:24: ^( ALAP expr_atom )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(ALAP, "ALAP"), root_1);

                        adaptor.addChild(root_1, stream_expr_atom.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
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
        }
        return retval;
    }
    // $ANTLR end "expr2"

    public static class expr_atom_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "expr_atom"
    // GCLNew.g:122:1: expr_atom : ( ANY | OTHER | LPAR expr RPAR | call );
    public final GCLNewParser.expr_atom_return expr_atom() throws RecognitionException {
        GCLNewParser.expr_atom_return retval = new GCLNewParser.expr_atom_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token ANY65=null;
        Token OTHER66=null;
        Token LPAR67=null;
        Token RPAR69=null;
        GCLNewParser.expr_return expr68 = null;

        GCLNewParser.call_return call70 = null;


        CommonTree ANY65_tree=null;
        CommonTree OTHER66_tree=null;
        CommonTree LPAR67_tree=null;
        CommonTree RPAR69_tree=null;

        try {
            // GCLNew.g:123:2: ( ANY | OTHER | LPAR expr RPAR | call )
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
                    // GCLNew.g:123:4: ANY
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    ANY65=(Token)match(input,ANY,FOLLOW_ANY_in_expr_atom625); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ANY65_tree = (CommonTree)adaptor.create(ANY65);
                    adaptor.addChild(root_0, ANY65_tree);
                    }

                    }
                    break;
                case 2 :
                    // GCLNew.g:124:4: OTHER
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    OTHER66=(Token)match(input,OTHER,FOLLOW_OTHER_in_expr_atom630); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    OTHER66_tree = (CommonTree)adaptor.create(OTHER66);
                    adaptor.addChild(root_0, OTHER66_tree);
                    }

                    }
                    break;
                case 3 :
                    // GCLNew.g:125:4: LPAR expr RPAR
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    LPAR67=(Token)match(input,LPAR,FOLLOW_LPAR_in_expr_atom635); if (state.failed) return retval;
                    pushFollow(FOLLOW_expr_in_expr_atom638);
                    expr68=expr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expr68.getTree());
                    RPAR69=(Token)match(input,RPAR,FOLLOW_RPAR_in_expr_atom640); if (state.failed) return retval;

                    }
                    break;
                case 4 :
                    // GCLNew.g:126:4: call
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_call_in_expr_atom646);
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
        }
        return retval;
    }
    // $ANTLR end "expr_atom"

    public static class call_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "call"
    // GCLNew.g:129:1: call : rule_name ( arg_list )? -> ^( CALL rule_name ( arg_list )? ) ;
    public final GCLNewParser.call_return call() throws RecognitionException {
        GCLNewParser.call_return retval = new GCLNewParser.call_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        GCLNewParser.rule_name_return rule_name71 = null;

        GCLNewParser.arg_list_return arg_list72 = null;


        RewriteRuleSubtreeStream stream_arg_list=new RewriteRuleSubtreeStream(adaptor,"rule arg_list");
        RewriteRuleSubtreeStream stream_rule_name=new RewriteRuleSubtreeStream(adaptor,"rule rule_name");
        try {
            // GCLNew.g:130:2: ( rule_name ( arg_list )? -> ^( CALL rule_name ( arg_list )? ) )
            // GCLNew.g:130:4: rule_name ( arg_list )?
            {
            pushFollow(FOLLOW_rule_name_in_call658);
            rule_name71=rule_name();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rule_name.add(rule_name71.getTree());
            // GCLNew.g:130:14: ( arg_list )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==LPAR) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // GCLNew.g:130:14: arg_list
                    {
                    pushFollow(FOLLOW_arg_list_in_call660);
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
            // 131:4: -> ^( CALL rule_name ( arg_list )? )
            {
                // GCLNew.g:131:7: ^( CALL rule_name ( arg_list )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(CALL, "CALL"), root_1);

                adaptor.addChild(root_1, stream_rule_name.nextTree());
                // GCLNew.g:131:24: ( arg_list )?
                if ( stream_arg_list.hasNext() ) {
                    adaptor.addChild(root_1, stream_arg_list.nextTree());

                }
                stream_arg_list.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
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
        }
        return retval;
    }
    // $ANTLR end "call"

    public static class rule_name_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rule_name"
    // GCLNew.g:134:1: rule_name : ids+= ID ( DOT ids+= ID )* ->;
    public final GCLNewParser.rule_name_return rule_name() throws RecognitionException {
        GCLNewParser.rule_name_return retval = new GCLNewParser.rule_name_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token DOT73=null;
        Token ids=null;
        List list_ids=null;

        CommonTree DOT73_tree=null;
        CommonTree ids_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");

        try {
            // GCLNew.g:136:3: (ids+= ID ( DOT ids+= ID )* ->)
            // GCLNew.g:136:5: ids+= ID ( DOT ids+= ID )*
            {
            ids=(Token)match(input,ID,FOLLOW_ID_in_rule_name691); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ids);

            if (list_ids==null) list_ids=new ArrayList();
            list_ids.add(ids);

            // GCLNew.g:136:13: ( DOT ids+= ID )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( (LA17_0==DOT) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // GCLNew.g:136:14: DOT ids+= ID
            	    {
            	    DOT73=(Token)match(input,DOT,FOLLOW_DOT_in_rule_name694); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(DOT73);

            	    ids=(Token)match(input,ID,FOLLOW_ID_in_rule_name698); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_ID.add(ids);

            	    if (list_ids==null) list_ids=new ArrayList();
            	    list_ids.add(ids);


            	    }
            	    break;

            	default :
            	    break loop17;
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
            // 137:5: ->
            {
                adaptor.addChild(root_0,  helper.toRuleName(list_ids) );

            }

            retval.tree = root_0;}
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
        }
        return retval;
    }
    // $ANTLR end "rule_name"

    public static class var_decl_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "var_decl"
    // GCLNew.g:140:1: var_decl : var_type ID ( COMMA ID )* -> ^( VAR var_type ( ID )+ ) ;
    public final GCLNewParser.var_decl_return var_decl() throws RecognitionException {
        GCLNewParser.var_decl_return retval = new GCLNewParser.var_decl_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token ID75=null;
        Token COMMA76=null;
        Token ID77=null;
        GCLNewParser.var_type_return var_type74 = null;


        CommonTree ID75_tree=null;
        CommonTree COMMA76_tree=null;
        CommonTree ID77_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_var_type=new RewriteRuleSubtreeStream(adaptor,"rule var_type");
        try {
            // GCLNew.g:141:2: ( var_type ID ( COMMA ID )* -> ^( VAR var_type ( ID )+ ) )
            // GCLNew.g:141:4: var_type ID ( COMMA ID )*
            {
            pushFollow(FOLLOW_var_type_in_var_decl720);
            var_type74=var_type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_var_type.add(var_type74.getTree());
            ID75=(Token)match(input,ID,FOLLOW_ID_in_var_decl722); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID75);

            // GCLNew.g:141:16: ( COMMA ID )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==COMMA) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // GCLNew.g:141:17: COMMA ID
            	    {
            	    COMMA76=(Token)match(input,COMMA,FOLLOW_COMMA_in_var_decl725); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA76);

            	    ID77=(Token)match(input,ID,FOLLOW_ID_in_var_decl727); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_ID.add(ID77);


            	    }
            	    break;

            	default :
            	    break loop18;
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
            // 141:28: -> ^( VAR var_type ( ID )+ )
            {
                // GCLNew.g:141:31: ^( VAR var_type ( ID )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(VAR, "VAR"), root_1);

                adaptor.addChild(root_1, stream_var_type.nextTree());
                if ( !(stream_ID.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_ID.hasNext() ) {
                    adaptor.addChild(root_1, stream_ID.nextNode());

                }
                stream_ID.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
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
        }
        return retval;
    }
    // $ANTLR end "var_decl"

    public static class var_type_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "var_type"
    // GCLNew.g:144:1: var_type : ( NODE | BOOL | STRING | INT | REAL );
    public final GCLNewParser.var_type_return var_type() throws RecognitionException {
        GCLNewParser.var_type_return retval = new GCLNewParser.var_type_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token set78=null;

        CommonTree set78_tree=null;

        try {
            // GCLNew.g:145:2: ( NODE | BOOL | STRING | INT | REAL )
            // GCLNew.g:
            {
            root_0 = (CommonTree)adaptor.nil();

            set78=(Token)input.LT(1);
            if ( (input.LA(1)>=NODE && input.LA(1)<=REAL) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (CommonTree)adaptor.create(set78));
                state.errorRecovery=false;state.failed=false;
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
        }
        return retval;
    }
    // $ANTLR end "var_type"

    public static class arg_list_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "arg_list"
    // GCLNew.g:152:1: arg_list : LPAR ( arg ( COMMA arg )* )? RPAR -> ^( ARGS ( arg )* ) ;
    public final GCLNewParser.arg_list_return arg_list() throws RecognitionException {
        GCLNewParser.arg_list_return retval = new GCLNewParser.arg_list_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token LPAR79=null;
        Token COMMA81=null;
        Token RPAR83=null;
        GCLNewParser.arg_return arg80 = null;

        GCLNewParser.arg_return arg82 = null;


        CommonTree LPAR79_tree=null;
        CommonTree COMMA81_tree=null;
        CommonTree RPAR83_tree=null;
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_arg=new RewriteRuleSubtreeStream(adaptor,"rule arg");
        try {
            // GCLNew.g:153:2: ( LPAR ( arg ( COMMA arg )* )? RPAR -> ^( ARGS ( arg )* ) )
            // GCLNew.g:153:4: LPAR ( arg ( COMMA arg )* )? RPAR
            {
            LPAR79=(Token)match(input,LPAR,FOLLOW_LPAR_in_arg_list783); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAR.add(LPAR79);

            // GCLNew.g:153:9: ( arg ( COMMA arg )* )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==ID||LA20_0==TRUE||(LA20_0>=OUT && LA20_0<=REAL_LIT)) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // GCLNew.g:153:10: arg ( COMMA arg )*
                    {
                    pushFollow(FOLLOW_arg_in_arg_list786);
                    arg80=arg();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_arg.add(arg80.getTree());
                    // GCLNew.g:153:14: ( COMMA arg )*
                    loop19:
                    do {
                        int alt19=2;
                        int LA19_0 = input.LA(1);

                        if ( (LA19_0==COMMA) ) {
                            alt19=1;
                        }


                        switch (alt19) {
                    	case 1 :
                    	    // GCLNew.g:153:15: COMMA arg
                    	    {
                    	    COMMA81=(Token)match(input,COMMA,FOLLOW_COMMA_in_arg_list789); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA81);

                    	    pushFollow(FOLLOW_arg_in_arg_list791);
                    	    arg82=arg();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_arg.add(arg82.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop19;
                        }
                    } while (true);


                    }
                    break;

            }

            RPAR83=(Token)match(input,RPAR,FOLLOW_RPAR_in_arg_list797); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAR.add(RPAR83);



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
            // 154:4: -> ^( ARGS ( arg )* )
            {
                // GCLNew.g:154:7: ^( ARGS ( arg )* )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(ARGS, "ARGS"), root_1);

                // GCLNew.g:154:14: ( arg )*
                while ( stream_arg.hasNext() ) {
                    adaptor.addChild(root_1, stream_arg.nextTree());

                }
                stream_arg.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
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
        }
        return retval;
    }
    // $ANTLR end "arg_list"

    public static class arg_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "arg"
    // GCLNew.g:157:1: arg : ( OUT ID -> ^( ARG OUT ID ) | ID -> ^( ARG ID ) | DONT_CARE -> ^( ARG DONT_CARE ) | literal -> ^( ARG literal ) );
    public final GCLNewParser.arg_return arg() throws RecognitionException {
        GCLNewParser.arg_return retval = new GCLNewParser.arg_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token OUT84=null;
        Token ID85=null;
        Token ID86=null;
        Token DONT_CARE87=null;
        GCLNewParser.literal_return literal88 = null;


        CommonTree OUT84_tree=null;
        CommonTree ID85_tree=null;
        CommonTree ID86_tree=null;
        CommonTree DONT_CARE87_tree=null;
        RewriteRuleTokenStream stream_DONT_CARE=new RewriteRuleTokenStream(adaptor,"token DONT_CARE");
        RewriteRuleTokenStream stream_OUT=new RewriteRuleTokenStream(adaptor,"token OUT");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_literal=new RewriteRuleSubtreeStream(adaptor,"rule literal");
        try {
            // GCLNew.g:158:2: ( OUT ID -> ^( ARG OUT ID ) | ID -> ^( ARG ID ) | DONT_CARE -> ^( ARG DONT_CARE ) | literal -> ^( ARG literal ) )
            int alt21=4;
            switch ( input.LA(1) ) {
            case OUT:
                {
                alt21=1;
                }
                break;
            case ID:
                {
                alt21=2;
                }
                break;
            case DONT_CARE:
                {
                alt21=3;
                }
                break;
            case TRUE:
            case FALSE:
            case STRING_LIT:
            case INT_LIT:
            case REAL_LIT:
                {
                alt21=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 21, 0, input);

                throw nvae;
            }

            switch (alt21) {
                case 1 :
                    // GCLNew.g:158:4: OUT ID
                    {
                    OUT84=(Token)match(input,OUT,FOLLOW_OUT_in_arg820); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_OUT.add(OUT84);

                    ID85=(Token)match(input,ID,FOLLOW_ID_in_arg822); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID85);



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
                    // 158:11: -> ^( ARG OUT ID )
                    {
                        // GCLNew.g:158:14: ^( ARG OUT ID )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(ARG, "ARG"), root_1);

                        adaptor.addChild(root_1, stream_OUT.nextNode());
                        adaptor.addChild(root_1, stream_ID.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // GCLNew.g:159:4: ID
                    {
                    ID86=(Token)match(input,ID,FOLLOW_ID_in_arg837); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID86);



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
                    // 159:7: -> ^( ARG ID )
                    {
                        // GCLNew.g:159:10: ^( ARG ID )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(ARG, "ARG"), root_1);

                        adaptor.addChild(root_1, stream_ID.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // GCLNew.g:160:4: DONT_CARE
                    {
                    DONT_CARE87=(Token)match(input,DONT_CARE,FOLLOW_DONT_CARE_in_arg850); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DONT_CARE.add(DONT_CARE87);



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
                    // 160:14: -> ^( ARG DONT_CARE )
                    {
                        // GCLNew.g:160:17: ^( ARG DONT_CARE )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(ARG, "ARG"), root_1);

                        adaptor.addChild(root_1, stream_DONT_CARE.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 4 :
                    // GCLNew.g:161:4: literal
                    {
                    pushFollow(FOLLOW_literal_in_arg863);
                    literal88=literal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_literal.add(literal88.getTree());


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
                    // 161:12: -> ^( ARG literal )
                    {
                        // GCLNew.g:161:15: ^( ARG literal )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(ARG, "ARG"), root_1);

                        adaptor.addChild(root_1, stream_literal.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
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
        }
        return retval;
    }
    // $ANTLR end "arg"

    public static class literal_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "literal"
    // GCLNew.g:164:1: literal : ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT );
    public final GCLNewParser.literal_return literal() throws RecognitionException {
        GCLNewParser.literal_return retval = new GCLNewParser.literal_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token set89=null;

        CommonTree set89_tree=null;

        try {
            // GCLNew.g:165:2: ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT )
            // GCLNew.g:
            {
            root_0 = (CommonTree)adaptor.nil();

            set89=(Token)input.LT(1);
            if ( input.LA(1)==TRUE||(input.LA(1)>=FALSE && input.LA(1)<=REAL_LIT) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (CommonTree)adaptor.create(set89));
                state.errorRecovery=false;state.failed=false;
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
        }
        return retval;
    }
    // $ANTLR end "literal"

    // $ANTLR start synpred1_GCLNew
    public final void synpred1_GCLNew_fragment() throws RecognitionException {   
        // GCLNew.g:89:33: ( ELSE )
        // GCLNew.g:89:34: ELSE
        {
        match(input,ELSE,FOLLOW_ELSE_in_synpred1_GCLNew336); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_GCLNew

    // $ANTLR start synpred2_GCLNew
    public final void synpred2_GCLNew_fragment() throws RecognitionException {   
        // GCLNew.g:90:17: ( ELSE )
        // GCLNew.g:90:18: ELSE
        {
        match(input,ELSE,FOLLOW_ELSE_in_synpred2_GCLNew361); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred2_GCLNew

    // $ANTLR start synpred3_GCLNew
    public final void synpred3_GCLNew_fragment() throws RecognitionException {   
        // GCLNew.g:91:20: ( OR )
        // GCLNew.g:91:21: OR
        {
        match(input,OR,FOLLOW_OR_in_synpred3_GCLNew386); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred3_GCLNew

    // Delegated rules

    public final boolean synpred1_GCLNew() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_GCLNew_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred3_GCLNew() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred3_GCLNew_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred2_GCLNew() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred2_GCLNew_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA1 dfa1 = new DFA1(this);
    protected DFA2 dfa2 = new DFA2(this);
    protected DFA7 dfa7 = new DFA7(this);
    protected DFA4 dfa4 = new DFA4(this);
    protected DFA5 dfa5 = new DFA5(this);
    protected DFA6 dfa6 = new DFA6(this);
    static final String DFA1_eotS =
        "\21\uffff";
    static final String DFA1_eofS =
        "\1\1\20\uffff";
    static final String DFA1_minS =
        "\1\12\20\uffff";
    static final String DFA1_maxS =
        "\1\52\20\uffff";
    static final String DFA1_acceptS =
        "\1\uffff\1\3\1\1\1\2\15\uffff";
    static final String DFA1_specialS =
        "\21\uffff}>";
    static final String[] DFA1_transitionS = {
            "\1\2\3\uffff\1\3\1\uffff\2\3\1\uffff\5\3\1\uffff\2\3\6\uffff"+
            "\3\3\2\uffff\5\3",
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
            return "()* loopback of 68:5: ( function | stat )*";
        }
    }
    static final String DFA2_eotS =
        "\20\uffff";
    static final String DFA2_eofS =
        "\20\uffff";
    static final String DFA2_minS =
        "\1\16\17\uffff";
    static final String DFA2_maxS =
        "\1\52\17\uffff";
    static final String DFA2_acceptS =
        "\1\uffff\1\2\1\1\15\uffff";
    static final String DFA2_specialS =
        "\20\uffff}>";
    static final String[] DFA2_transitionS = {
            "\1\2\1\1\2\2\1\uffff\5\2\1\uffff\2\2\6\uffff\3\2\2\uffff\5"+
            "\2",
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
            return "()* loopback of 73:11: ( stat )*";
        }
    }
    static final String DFA7_eotS =
        "\17\uffff";
    static final String DFA7_eofS =
        "\17\uffff";
    static final String DFA7_minS =
        "\1\16\16\uffff";
    static final String DFA7_maxS =
        "\1\52\16\uffff";
    static final String DFA7_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\4\uffff\1\12";
    static final String DFA7_specialS =
        "\17\uffff}>";
    static final String[] DFA7_transitionS = {
            "\1\1\1\uffff\2\11\1\uffff\1\2\1\3\1\4\1\5\1\6\1\uffff\1\7\1"+
            "\10\6\uffff\3\11\2\uffff\5\16",
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
            return "80:1: stat : ( block | ALAP stat | WHILE LPAR cond RPAR stat | UNTIL LPAR cond RPAR stat | DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) ) | IF LPAR cond RPAR stat ( ( ELSE )=> ELSE stat )? | TRY stat ( ( ELSE )=> ELSE stat )? | CHOICE stat ( ( OR )=> OR stat )+ | expr SEMI | var_decl SEMI );";
        }
    }
    static final String DFA4_eotS =
        "\43\uffff";
    static final String DFA4_eofS =
        "\1\2\42\uffff";
    static final String DFA4_minS =
        "\1\12\1\0\41\uffff";
    static final String DFA4_maxS =
        "\1\52\1\0\41\uffff";
    static final String DFA4_acceptS =
        "\2\uffff\1\2\37\uffff\1\1";
    static final String DFA4_specialS =
        "\1\uffff\1\0\41\uffff}>";
    static final String[] DFA4_transitionS = {
            "\1\2\3\uffff\4\2\1\uffff\5\2\1\1\3\2\5\uffff\3\2\2\uffff\5"+
            "\2",
            "\1\uffff",
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
            return "89:31: ( ( ELSE )=> ELSE stat )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA4_1 = input.LA(1);

                         
                        int index4_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_GCLNew()) ) {s = 34;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index4_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 4, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA5_eotS =
        "\43\uffff";
    static final String DFA5_eofS =
        "\1\2\42\uffff";
    static final String DFA5_minS =
        "\1\12\1\0\41\uffff";
    static final String DFA5_maxS =
        "\1\52\1\0\41\uffff";
    static final String DFA5_acceptS =
        "\2\uffff\1\2\37\uffff\1\1";
    static final String DFA5_specialS =
        "\1\uffff\1\0\41\uffff}>";
    static final String[] DFA5_transitionS = {
            "\1\2\3\uffff\4\2\1\uffff\5\2\1\1\3\2\5\uffff\3\2\2\uffff\5"+
            "\2",
            "\1\uffff",
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
            return "90:15: ( ( ELSE )=> ELSE stat )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA5_1 = input.LA(1);

                         
                        int index5_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2_GCLNew()) ) {s = 34;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index5_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 5, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA6_eotS =
        "\43\uffff";
    static final String DFA6_eofS =
        "\1\1\42\uffff";
    static final String DFA6_minS =
        "\1\12\22\uffff\1\0\17\uffff";
    static final String DFA6_maxS =
        "\1\52\22\uffff\1\0\17\uffff";
    static final String DFA6_acceptS =
        "\1\uffff\1\2\40\uffff\1\1";
    static final String DFA6_specialS =
        "\23\uffff\1\0\17\uffff}>";
    static final String[] DFA6_transitionS = {
            "\1\1\3\uffff\4\1\1\uffff\10\1\1\23\5\uffff\3\1\2\uffff\5\1",
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
            "",
            "",
            "\1\uffff",
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
            return "()+ loopback of 91:18: ( ( OR )=> OR stat )+";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA6_19 = input.LA(1);

                         
                        int index6_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred3_GCLNew()) ) {s = 34;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index6_19);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 6, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

    public static final BitSet FOLLOW_function_in_program110 = new BitSet(new long[]{0x000007CE06FB4402L});
    public static final BitSet FOLLOW_stat_in_program112 = new BitSet(new long[]{0x000007CE06FB4402L});
    public static final BitSet FOLLOW_LCURLY_in_block150 = new BitSet(new long[]{0x000007CE06FBC400L});
    public static final BitSet FOLLOW_stat_in_block152 = new BitSet(new long[]{0x000007CE06FBC400L});
    public static final BitSet FOLLOW_RCURLY_in_block155 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_function174 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ID_in_function177 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_LPAR_in_function179 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_RPAR_in_function182 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_block_in_function185 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_stat203 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALAP_in_stat208 = new BitSet(new long[]{0x000007CE06FB4400L});
    public static final BitSet FOLLOW_stat_in_stat211 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHILE_in_stat216 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_LPAR_in_stat219 = new BitSet(new long[]{0x0000000C40030000L});
    public static final BitSet FOLLOW_cond_in_stat222 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_RPAR_in_stat224 = new BitSet(new long[]{0x000007CE06FB4400L});
    public static final BitSet FOLLOW_stat_in_stat227 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNTIL_in_stat232 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_LPAR_in_stat235 = new BitSet(new long[]{0x0000000C40030000L});
    public static final BitSet FOLLOW_cond_in_stat238 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_RPAR_in_stat240 = new BitSet(new long[]{0x000007CE06FB4400L});
    public static final BitSet FOLLOW_stat_in_stat243 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DO_in_stat248 = new BitSet(new long[]{0x000007CE06FB4400L});
    public static final BitSet FOLLOW_stat_in_stat250 = new BitSet(new long[]{0x0000000000300000L});
    public static final BitSet FOLLOW_WHILE_in_stat258 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_LPAR_in_stat260 = new BitSet(new long[]{0x0000000C40030000L});
    public static final BitSet FOLLOW_cond_in_stat262 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_RPAR_in_stat264 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNTIL_in_stat287 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_LPAR_in_stat289 = new BitSet(new long[]{0x0000000C40030000L});
    public static final BitSet FOLLOW_cond_in_stat291 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_RPAR_in_stat293 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IF_in_stat320 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_LPAR_in_stat323 = new BitSet(new long[]{0x0000000C40030000L});
    public static final BitSet FOLLOW_cond_in_stat326 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_RPAR_in_stat328 = new BitSet(new long[]{0x000007CE07FB4400L});
    public static final BitSet FOLLOW_stat_in_stat331 = new BitSet(new long[]{0x0000000001000002L});
    public static final BitSet FOLLOW_ELSE_in_stat341 = new BitSet(new long[]{0x000007CE06FB4400L});
    public static final BitSet FOLLOW_stat_in_stat344 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRY_in_stat353 = new BitSet(new long[]{0x000007CE07FB4400L});
    public static final BitSet FOLLOW_stat_in_stat356 = new BitSet(new long[]{0x0000000001000002L});
    public static final BitSet FOLLOW_ELSE_in_stat366 = new BitSet(new long[]{0x000007CE06FB4400L});
    public static final BitSet FOLLOW_stat_in_stat369 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CHOICE_in_stat378 = new BitSet(new long[]{0x000007CE0EFB4400L});
    public static final BitSet FOLLOW_stat_in_stat381 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_OR_in_stat391 = new BitSet(new long[]{0x000007CE0EFB4400L});
    public static final BitSet FOLLOW_stat_in_stat394 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_expr_in_stat401 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_SEMI_in_stat403 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_decl_in_stat409 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_SEMI_in_stat411 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_cond_atom_in_cond424 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_BAR_in_cond433 = new BitSet(new long[]{0x0000000C40030000L});
    public static final BitSet FOLLOW_cond_atom_in_cond435 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_TRUE_in_cond_atom473 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_cond_atom477 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr2_in_expr488 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_BAR_in_expr496 = new BitSet(new long[]{0x0000000E00030000L});
    public static final BitSet FOLLOW_expr2_in_expr498 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_expr_atom_in_expr2539 = new BitSet(new long[]{0x0000000180000002L});
    public static final BitSet FOLLOW_PLUS_in_expr2547 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASTERISK_in_expr2571 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SHARP_in_expr2603 = new BitSet(new long[]{0x0000000C00030000L});
    public static final BitSet FOLLOW_expr_atom_in_expr2605 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_expr_atom625 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_expr_atom630 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_expr_atom635 = new BitSet(new long[]{0x0000000E00030000L});
    public static final BitSet FOLLOW_expr_in_expr_atom638 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_RPAR_in_expr_atom640 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_expr_atom646 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_name_in_call658 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_arg_list_in_call660 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_rule_name691 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_DOT_in_rule_name694 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ID_in_rule_name698 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_var_type_in_var_decl720 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ID_in_var_decl722 = new BitSet(new long[]{0x0000002000000002L});
    public static final BitSet FOLLOW_COMMA_in_var_decl725 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ID_in_var_decl727 = new BitSet(new long[]{0x0000002000000002L});
    public static final BitSet FOLLOW_set_in_var_type0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_arg_list783 = new BitSet(new long[]{0x0001F80040050000L});
    public static final BitSet FOLLOW_arg_in_arg_list786 = new BitSet(new long[]{0x0000002000040000L});
    public static final BitSet FOLLOW_COMMA_in_arg_list789 = new BitSet(new long[]{0x0001F80040010000L});
    public static final BitSet FOLLOW_arg_in_arg_list791 = new BitSet(new long[]{0x0000002000040000L});
    public static final BitSet FOLLOW_RPAR_in_arg_list797 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OUT_in_arg820 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ID_in_arg822 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_arg837 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DONT_CARE_in_arg850 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_arg863 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_literal0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ELSE_in_synpred1_GCLNew336 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ELSE_in_synpred2_GCLNew361 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_synpred3_GCLNew386 = new BitSet(new long[]{0x0000000000000002L});

}