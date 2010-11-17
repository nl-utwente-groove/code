// $ANTLR 3.2 Sep 23, 2009 12:02:23 GCLNew.g 2010-11-17 18:08:38

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "PROGRAM", "BLOCK", "FUNCTIONS", "FUNCTION", "CALL", "DO_WHILE", "VAR", "ARG", "LCURLY", "RCURLY", "ID", "LPAR", "RPAR", "ALAP", "WHILE", "UNTIL", "DO", "IF", "ELSE", "TRY", "CHOICE", "CH_OR", "SEMI", "OR", "TRUE", "PLUS", "STAR", "SHARP", "ANY", "OTHER", "DOT", "COMMA", "NODE_TYPE", "BOOL_TYPE", "STRING_TYPE", "INT_TYPE", "REAL_TYPE", "OUT", "DONT_CARE", "FALSE", "STRING_LIT", "INT_LIT", "REAL_LIT", "IntegerNumber", "NonIntegerNumber", "QUOTE", "EscapeSequence", "BSLASH", "CR", "NL", "AND", "NOT", "MINUS", "ML_COMMENT", "SL_COMMENT", "WS"
    };
    public static final int REAL_LIT=46;
    public static final int FUNCTION=7;
    public static final int STAR=30;
    public static final int INT_LIT=45;
    public static final int WHILE=18;
    public static final int FUNCTIONS=6;
    public static final int IntegerNumber=47;
    public static final int BOOL_TYPE=37;
    public static final int STRING_LIT=44;
    public static final int NODE_TYPE=36;
    public static final int DO=20;
    public static final int NOT=55;
    public static final int ALAP=17;
    public static final int AND=54;
    public static final int ID=14;
    public static final int EOF=-1;
    public static final int IF=21;
    public static final int ML_COMMENT=57;
    public static final int QUOTE=49;
    public static final int LPAR=15;
    public static final int ARG=11;
    public static final int COMMA=35;
    public static final int NonIntegerNumber=48;
    public static final int DO_WHILE=9;
    public static final int CH_OR=25;
    public static final int PLUS=29;
    public static final int VAR=10;
    public static final int NL=53;
    public static final int DOT=34;
    public static final int CHOICE=24;
    public static final int SHARP=31;
    public static final int OTHER=33;
    public static final int ELSE=22;
    public static final int LCURLY=12;
    public static final int MINUS=56;
    public static final int INT_TYPE=39;
    public static final int SEMI=26;
    public static final int TRUE=28;
    public static final int TRY=23;
    public static final int REAL_TYPE=40;
    public static final int DONT_CARE=42;
    public static final int WS=59;
    public static final int ANY=32;
    public static final int OUT=41;
    public static final int UNTIL=19;
    public static final int BLOCK=5;
    public static final int STRING_TYPE=38;
    public static final int SL_COMMENT=58;
    public static final int RCURLY=13;
    public static final int OR=27;
    public static final int RPAR=16;
    public static final int PROGRAM=4;
    public static final int CALL=8;
    public static final int CR=52;
    public static final int FALSE=43;
    public static final int BSLASH=51;
    public static final int EscapeSequence=50;

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
        public Tree run(CharStream input, NamespaceNew namespace) throws RecognitionException {
            this.helper = new GCLHelper(this, namespace);
            lexer.setCharStream(input);
            return (Tree) program().getTree();
        }


    public static class program_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "program"
    // GCLNew.g:64:1: program : ( function | stat )* -> ^( PROGRAM ^( FUNCTIONS ( function )* ) ^( BLOCK ( stat )* ) ) ;
    public final GCLNewParser.program_return program() throws RecognitionException {
        GCLNewParser.program_return retval = new GCLNewParser.program_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        GCLNewParser.function_return function1 = null;

        GCLNewParser.stat_return stat2 = null;


        RewriteRuleSubtreeStream stream_stat=new RewriteRuleSubtreeStream(adaptor,"rule stat");
        RewriteRuleSubtreeStream stream_function=new RewriteRuleSubtreeStream(adaptor,"rule function");
        try {
            // GCLNew.g:65:3: ( ( function | stat )* -> ^( PROGRAM ^( FUNCTIONS ( function )* ) ^( BLOCK ( stat )* ) ) )
            // GCLNew.g:65:5: ( function | stat )*
            {
            // GCLNew.g:65:5: ( function | stat )*
            loop1:
            do {
                int alt1=3;
                alt1 = dfa1.predict(input);
                switch (alt1) {
            	case 1 :
            	    // GCLNew.g:65:6: function
            	    {
            	    pushFollow(FOLLOW_function_in_program98);
            	    function1=function();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_function.add(function1.getTree());

            	    }
            	    break;
            	case 2 :
            	    // GCLNew.g:65:15: stat
            	    {
            	    pushFollow(FOLLOW_stat_in_program100);
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
            // 66:5: -> ^( PROGRAM ^( FUNCTIONS ( function )* ) ^( BLOCK ( stat )* ) )
            {
                // GCLNew.g:66:8: ^( PROGRAM ^( FUNCTIONS ( function )* ) ^( BLOCK ( stat )* ) )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(PROGRAM, "PROGRAM"), root_1);

                // GCLNew.g:66:18: ^( FUNCTIONS ( function )* )
                {
                CommonTree root_2 = (CommonTree)adaptor.nil();
                root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(FUNCTIONS, "FUNCTIONS"), root_2);

                // GCLNew.g:66:30: ( function )*
                while ( stream_function.hasNext() ) {
                    adaptor.addChild(root_2, stream_function.nextTree());

                }
                stream_function.reset();

                adaptor.addChild(root_1, root_2);
                }
                // GCLNew.g:66:41: ^( BLOCK ( stat )* )
                {
                CommonTree root_2 = (CommonTree)adaptor.nil();
                root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(BLOCK, "BLOCK"), root_2);

                // GCLNew.g:66:49: ( stat )*
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
    // GCLNew.g:69:1: block : LCURLY ( stat )* RCURLY -> ^( BLOCK ( stat )* ) ;
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
            // GCLNew.g:70:2: ( LCURLY ( stat )* RCURLY -> ^( BLOCK ( stat )* ) )
            // GCLNew.g:70:4: LCURLY ( stat )* RCURLY
            {
            LCURLY3=(Token)match(input,LCURLY,FOLLOW_LCURLY_in_block138); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LCURLY.add(LCURLY3);

            // GCLNew.g:70:11: ( stat )*
            loop2:
            do {
                int alt2=2;
                alt2 = dfa2.predict(input);
                switch (alt2) {
            	case 1 :
            	    // GCLNew.g:70:11: stat
            	    {
            	    pushFollow(FOLLOW_stat_in_block140);
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

            RCURLY5=(Token)match(input,RCURLY,FOLLOW_RCURLY_in_block143); if (state.failed) return retval; 
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
            // 70:24: -> ^( BLOCK ( stat )* )
            {
                // GCLNew.g:70:27: ^( BLOCK ( stat )* )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(BLOCK, "BLOCK"), root_1);

                // GCLNew.g:70:35: ( stat )*
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
    // GCLNew.g:72:1: function : FUNCTION ID LPAR RPAR block ;
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
            // GCLNew.g:73:3: ( FUNCTION ID LPAR RPAR block )
            // GCLNew.g:73:5: FUNCTION ID LPAR RPAR block
            {
            root_0 = (CommonTree)adaptor.nil();

            FUNCTION6=(Token)match(input,FUNCTION,FOLLOW_FUNCTION_in_function162); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            FUNCTION6_tree = (CommonTree)adaptor.create(FUNCTION6);
            root_0 = (CommonTree)adaptor.becomeRoot(FUNCTION6_tree, root_0);
            }
            ID7=(Token)match(input,ID,FOLLOW_ID_in_function165); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID7_tree = (CommonTree)adaptor.create(ID7);
            adaptor.addChild(root_0, ID7_tree);
            }
            LPAR8=(Token)match(input,LPAR,FOLLOW_LPAR_in_function167); if (state.failed) return retval;
            RPAR9=(Token)match(input,RPAR,FOLLOW_RPAR_in_function170); if (state.failed) return retval;
            pushFollow(FOLLOW_block_in_function173);
            block10=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, block10.getTree());

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
    // GCLNew.g:76:1: stat : ( block | ALAP stat -> ^( ALAP stat ) | WHILE LPAR cond RPAR stat | UNTIL LPAR cond RPAR stat | DO stat WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | IF LPAR cond RPAR stat ( ( ELSE )=> ELSE stat )? | TRY stat ( ( ELSE )=> ELSE stat )? | CHOICE stat ( ( CH_OR )=> CH_OR stat )+ | expr SEMI | var_decl SEMI );
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
        Token IF30=null;
        Token LPAR31=null;
        Token RPAR33=null;
        Token ELSE35=null;
        Token TRY37=null;
        Token ELSE39=null;
        Token CHOICE41=null;
        Token CH_OR43=null;
        Token SEMI46=null;
        Token SEMI48=null;
        GCLNewParser.block_return block11 = null;

        GCLNewParser.stat_return stat13 = null;

        GCLNewParser.cond_return cond16 = null;

        GCLNewParser.stat_return stat18 = null;

        GCLNewParser.cond_return cond21 = null;

        GCLNewParser.stat_return stat23 = null;

        GCLNewParser.stat_return stat25 = null;

        GCLNewParser.cond_return cond28 = null;

        GCLNewParser.cond_return cond32 = null;

        GCLNewParser.stat_return stat34 = null;

        GCLNewParser.stat_return stat36 = null;

        GCLNewParser.stat_return stat38 = null;

        GCLNewParser.stat_return stat40 = null;

        GCLNewParser.stat_return stat42 = null;

        GCLNewParser.stat_return stat44 = null;

        GCLNewParser.expr_return expr45 = null;

        GCLNewParser.var_decl_return var_decl47 = null;


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
        CommonTree IF30_tree=null;
        CommonTree LPAR31_tree=null;
        CommonTree RPAR33_tree=null;
        CommonTree ELSE35_tree=null;
        CommonTree TRY37_tree=null;
        CommonTree ELSE39_tree=null;
        CommonTree CHOICE41_tree=null;
        CommonTree CH_OR43_tree=null;
        CommonTree SEMI46_tree=null;
        CommonTree SEMI48_tree=null;
        RewriteRuleTokenStream stream_DO=new RewriteRuleTokenStream(adaptor,"token DO");
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleTokenStream stream_WHILE=new RewriteRuleTokenStream(adaptor,"token WHILE");
        RewriteRuleTokenStream stream_ALAP=new RewriteRuleTokenStream(adaptor,"token ALAP");
        RewriteRuleSubtreeStream stream_cond=new RewriteRuleSubtreeStream(adaptor,"rule cond");
        RewriteRuleSubtreeStream stream_stat=new RewriteRuleSubtreeStream(adaptor,"rule stat");
        try {
            // GCLNew.g:77:2: ( block | ALAP stat -> ^( ALAP stat ) | WHILE LPAR cond RPAR stat | UNTIL LPAR cond RPAR stat | DO stat WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | IF LPAR cond RPAR stat ( ( ELSE )=> ELSE stat )? | TRY stat ( ( ELSE )=> ELSE stat )? | CHOICE stat ( ( CH_OR )=> CH_OR stat )+ | expr SEMI | var_decl SEMI )
            int alt6=10;
            alt6 = dfa6.predict(input);
            switch (alt6) {
                case 1 :
                    // GCLNew.g:77:4: block
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_block_in_stat185);
                    block11=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block11.getTree());

                    }
                    break;
                case 2 :
                    // GCLNew.g:78:4: ALAP stat
                    {
                    ALAP12=(Token)match(input,ALAP,FOLLOW_ALAP_in_stat190); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ALAP.add(ALAP12);

                    pushFollow(FOLLOW_stat_in_stat192);
                    stat13=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_stat.add(stat13.getTree());


                    // AST REWRITE
                    // elements: ALAP, stat
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 78:14: -> ^( ALAP stat )
                    {
                        // GCLNew.g:78:17: ^( ALAP stat )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(stream_ALAP.nextNode(), root_1);

                        adaptor.addChild(root_1, stream_stat.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // GCLNew.g:79:4: WHILE LPAR cond RPAR stat
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    WHILE14=(Token)match(input,WHILE,FOLLOW_WHILE_in_stat205); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    WHILE14_tree = (CommonTree)adaptor.create(WHILE14);
                    root_0 = (CommonTree)adaptor.becomeRoot(WHILE14_tree, root_0);
                    }
                    LPAR15=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat208); if (state.failed) return retval;
                    pushFollow(FOLLOW_cond_in_stat211);
                    cond16=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond16.getTree());
                    RPAR17=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat213); if (state.failed) return retval;
                    pushFollow(FOLLOW_stat_in_stat216);
                    stat18=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat18.getTree());

                    }
                    break;
                case 4 :
                    // GCLNew.g:80:4: UNTIL LPAR cond RPAR stat
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    UNTIL19=(Token)match(input,UNTIL,FOLLOW_UNTIL_in_stat221); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    UNTIL19_tree = (CommonTree)adaptor.create(UNTIL19);
                    root_0 = (CommonTree)adaptor.becomeRoot(UNTIL19_tree, root_0);
                    }
                    LPAR20=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat224); if (state.failed) return retval;
                    pushFollow(FOLLOW_cond_in_stat227);
                    cond21=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond21.getTree());
                    RPAR22=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat229); if (state.failed) return retval;
                    pushFollow(FOLLOW_stat_in_stat232);
                    stat23=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat23.getTree());

                    }
                    break;
                case 5 :
                    // GCLNew.g:81:4: DO stat WHILE LPAR cond RPAR
                    {
                    DO24=(Token)match(input,DO,FOLLOW_DO_in_stat237); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DO.add(DO24);

                    pushFollow(FOLLOW_stat_in_stat239);
                    stat25=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_stat.add(stat25.getTree());
                    WHILE26=(Token)match(input,WHILE,FOLLOW_WHILE_in_stat241); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_WHILE.add(WHILE26);

                    LPAR27=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat243); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LPAR.add(LPAR27);

                    pushFollow(FOLLOW_cond_in_stat245);
                    cond28=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_cond.add(cond28.getTree());
                    RPAR29=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat247); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RPAR.add(RPAR29);



                    // AST REWRITE
                    // elements: stat, cond, stat, WHILE
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 81:33: -> ^( BLOCK stat ^( WHILE cond stat ) )
                    {
                        // GCLNew.g:81:36: ^( BLOCK stat ^( WHILE cond stat ) )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(BLOCK, "BLOCK"), root_1);

                        adaptor.addChild(root_1, stream_stat.nextTree());
                        // GCLNew.g:81:49: ^( WHILE cond stat )
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
                case 6 :
                    // GCLNew.g:82:5: IF LPAR cond RPAR stat ( ( ELSE )=> ELSE stat )?
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    IF30=(Token)match(input,IF,FOLLOW_IF_in_stat269); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IF30_tree = (CommonTree)adaptor.create(IF30);
                    root_0 = (CommonTree)adaptor.becomeRoot(IF30_tree, root_0);
                    }
                    LPAR31=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat272); if (state.failed) return retval;
                    pushFollow(FOLLOW_cond_in_stat275);
                    cond32=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond32.getTree());
                    RPAR33=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat277); if (state.failed) return retval;
                    pushFollow(FOLLOW_stat_in_stat280);
                    stat34=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat34.getTree());
                    // GCLNew.g:82:31: ( ( ELSE )=> ELSE stat )?
                    int alt3=2;
                    alt3 = dfa3.predict(input);
                    switch (alt3) {
                        case 1 :
                            // GCLNew.g:82:33: ( ELSE )=> ELSE stat
                            {
                            ELSE35=(Token)match(input,ELSE,FOLLOW_ELSE_in_stat290); if (state.failed) return retval;
                            pushFollow(FOLLOW_stat_in_stat293);
                            stat36=stat();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, stat36.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 7 :
                    // GCLNew.g:83:5: TRY stat ( ( ELSE )=> ELSE stat )?
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    TRY37=(Token)match(input,TRY,FOLLOW_TRY_in_stat302); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    TRY37_tree = (CommonTree)adaptor.create(TRY37);
                    root_0 = (CommonTree)adaptor.becomeRoot(TRY37_tree, root_0);
                    }
                    pushFollow(FOLLOW_stat_in_stat305);
                    stat38=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat38.getTree());
                    // GCLNew.g:83:15: ( ( ELSE )=> ELSE stat )?
                    int alt4=2;
                    alt4 = dfa4.predict(input);
                    switch (alt4) {
                        case 1 :
                            // GCLNew.g:83:17: ( ELSE )=> ELSE stat
                            {
                            ELSE39=(Token)match(input,ELSE,FOLLOW_ELSE_in_stat315); if (state.failed) return retval;
                            pushFollow(FOLLOW_stat_in_stat318);
                            stat40=stat();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, stat40.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 8 :
                    // GCLNew.g:84:5: CHOICE stat ( ( CH_OR )=> CH_OR stat )+
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    CHOICE41=(Token)match(input,CHOICE,FOLLOW_CHOICE_in_stat327); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CHOICE41_tree = (CommonTree)adaptor.create(CHOICE41);
                    root_0 = (CommonTree)adaptor.becomeRoot(CHOICE41_tree, root_0);
                    }
                    pushFollow(FOLLOW_stat_in_stat330);
                    stat42=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat42.getTree());
                    // GCLNew.g:84:18: ( ( CH_OR )=> CH_OR stat )+
                    int cnt5=0;
                    loop5:
                    do {
                        int alt5=2;
                        alt5 = dfa5.predict(input);
                        switch (alt5) {
                    	case 1 :
                    	    // GCLNew.g:84:19: ( CH_OR )=> CH_OR stat
                    	    {
                    	    CH_OR43=(Token)match(input,CH_OR,FOLLOW_CH_OR_in_stat339); if (state.failed) return retval;
                    	    pushFollow(FOLLOW_stat_in_stat342);
                    	    stat44=stat();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat44.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt5 >= 1 ) break loop5;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(5, input);
                                throw eee;
                        }
                        cnt5++;
                    } while (true);


                    }
                    break;
                case 9 :
                    // GCLNew.g:85:4: expr SEMI
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_expr_in_stat349);
                    expr45=expr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expr45.getTree());
                    SEMI46=(Token)match(input,SEMI,FOLLOW_SEMI_in_stat351); if (state.failed) return retval;

                    }
                    break;
                case 10 :
                    // GCLNew.g:86:4: var_decl SEMI
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_var_decl_in_stat357);
                    var_decl47=var_decl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, var_decl47.getTree());
                    SEMI48=(Token)match(input,SEMI,FOLLOW_SEMI_in_stat359); if (state.failed) return retval;

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
    // GCLNew.g:89:1: cond : cond_atom ( ( OR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom ) ;
    public final GCLNewParser.cond_return cond() throws RecognitionException {
        GCLNewParser.cond_return retval = new GCLNewParser.cond_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token OR50=null;
        GCLNewParser.cond_atom_return cond_atom49 = null;

        GCLNewParser.cond_atom_return cond_atom51 = null;


        CommonTree OR50_tree=null;
        RewriteRuleTokenStream stream_OR=new RewriteRuleTokenStream(adaptor,"token OR");
        RewriteRuleSubtreeStream stream_cond_atom=new RewriteRuleSubtreeStream(adaptor,"rule cond_atom");
        try {
            // GCLNew.g:90:2: ( cond_atom ( ( OR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom ) )
            // GCLNew.g:90:4: cond_atom ( ( OR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom )
            {
            pushFollow(FOLLOW_cond_atom_in_cond372);
            cond_atom49=cond_atom();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_cond_atom.add(cond_atom49.getTree());
            // GCLNew.g:91:4: ( ( OR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom )
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==OR) ) {
                alt8=1;
            }
            else if ( (LA8_0==RPAR) ) {
                alt8=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // GCLNew.g:91:6: ( OR cond_atom )+
                    {
                    // GCLNew.g:91:6: ( OR cond_atom )+
                    int cnt7=0;
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( (LA7_0==OR) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // GCLNew.g:91:7: OR cond_atom
                    	    {
                    	    OR50=(Token)match(input,OR,FOLLOW_OR_in_cond381); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_OR.add(OR50);

                    	    pushFollow(FOLLOW_cond_atom_in_cond383);
                    	    cond_atom51=cond_atom();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_cond_atom.add(cond_atom51.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt7 >= 1 ) break loop7;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(7, input);
                                throw eee;
                        }
                        cnt7++;
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
                    // 91:22: -> ^( CHOICE cond_atom ( cond_atom )+ )
                    {
                        // GCLNew.g:91:25: ^( CHOICE cond_atom ( cond_atom )+ )
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
                    // GCLNew.g:92:6: 
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
                    // 92:6: -> cond_atom
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
    // GCLNew.g:96:1: cond_atom : ( TRUE | call );
    public final GCLNewParser.cond_atom_return cond_atom() throws RecognitionException {
        GCLNewParser.cond_atom_return retval = new GCLNewParser.cond_atom_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token TRUE52=null;
        GCLNewParser.call_return call53 = null;


        CommonTree TRUE52_tree=null;

        try {
            // GCLNew.g:97:2: ( TRUE | call )
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==TRUE) ) {
                alt9=1;
            }
            else if ( (LA9_0==ID) ) {
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
                    // GCLNew.g:97:4: TRUE
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    TRUE52=(Token)match(input,TRUE,FOLLOW_TRUE_in_cond_atom421); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    TRUE52_tree = (CommonTree)adaptor.create(TRUE52);
                    adaptor.addChild(root_0, TRUE52_tree);
                    }

                    }
                    break;
                case 2 :
                    // GCLNew.g:97:11: call
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_call_in_cond_atom425);
                    call53=call();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, call53.getTree());

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
    // GCLNew.g:99:1: expr : expr2 ( ( OR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 ) ;
    public final GCLNewParser.expr_return expr() throws RecognitionException {
        GCLNewParser.expr_return retval = new GCLNewParser.expr_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token OR55=null;
        GCLNewParser.expr2_return expr254 = null;

        GCLNewParser.expr2_return expr256 = null;


        CommonTree OR55_tree=null;
        RewriteRuleTokenStream stream_OR=new RewriteRuleTokenStream(adaptor,"token OR");
        RewriteRuleSubtreeStream stream_expr2=new RewriteRuleSubtreeStream(adaptor,"rule expr2");
        try {
            // GCLNew.g:100:2: ( expr2 ( ( OR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 ) )
            // GCLNew.g:100:4: expr2 ( ( OR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 )
            {
            pushFollow(FOLLOW_expr2_in_expr436);
            expr254=expr2();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_expr2.add(expr254.getTree());
            // GCLNew.g:101:4: ( ( OR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==OR) ) {
                alt11=1;
            }
            else if ( (LA11_0==RPAR||LA11_0==SEMI) ) {
                alt11=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // GCLNew.g:101:6: ( OR expr2 )+
                    {
                    // GCLNew.g:101:6: ( OR expr2 )+
                    int cnt10=0;
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);

                        if ( (LA10_0==OR) ) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // GCLNew.g:101:7: OR expr2
                    	    {
                    	    OR55=(Token)match(input,OR,FOLLOW_OR_in_expr444); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_OR.add(OR55);

                    	    pushFollow(FOLLOW_expr2_in_expr446);
                    	    expr256=expr2();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_expr2.add(expr256.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt10 >= 1 ) break loop10;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(10, input);
                                throw eee;
                        }
                        cnt10++;
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
                    // 101:18: -> ^( CHOICE expr2 ( expr2 )+ )
                    {
                        // GCLNew.g:101:21: ^( CHOICE expr2 ( expr2 )+ )
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
                    // GCLNew.g:102:6: 
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
                    // 102:6: -> expr2
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
    // GCLNew.g:106:1: expr2 : (e= expr_atom ( PLUS -> ^( BLOCK $e ^( STAR $e) ) | STAR -> ^( STAR $e) | -> $e) | SHARP expr_atom -> ^( ALAP expr_atom ) );
    public final GCLNewParser.expr2_return expr2() throws RecognitionException {
        GCLNewParser.expr2_return retval = new GCLNewParser.expr2_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token PLUS57=null;
        Token STAR58=null;
        Token SHARP59=null;
        GCLNewParser.expr_atom_return e = null;

        GCLNewParser.expr_atom_return expr_atom60 = null;


        CommonTree PLUS57_tree=null;
        CommonTree STAR58_tree=null;
        CommonTree SHARP59_tree=null;
        RewriteRuleTokenStream stream_PLUS=new RewriteRuleTokenStream(adaptor,"token PLUS");
        RewriteRuleTokenStream stream_STAR=new RewriteRuleTokenStream(adaptor,"token STAR");
        RewriteRuleTokenStream stream_SHARP=new RewriteRuleTokenStream(adaptor,"token SHARP");
        RewriteRuleSubtreeStream stream_expr_atom=new RewriteRuleSubtreeStream(adaptor,"rule expr_atom");
        try {
            // GCLNew.g:107:3: (e= expr_atom ( PLUS -> ^( BLOCK $e ^( STAR $e) ) | STAR -> ^( STAR $e) | -> $e) | SHARP expr_atom -> ^( ALAP expr_atom ) )
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( ((LA13_0>=ID && LA13_0<=LPAR)||(LA13_0>=ANY && LA13_0<=OTHER)) ) {
                alt13=1;
            }
            else if ( (LA13_0==SHARP) ) {
                alt13=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // GCLNew.g:107:5: e= expr_atom ( PLUS -> ^( BLOCK $e ^( STAR $e) ) | STAR -> ^( STAR $e) | -> $e)
                    {
                    pushFollow(FOLLOW_expr_atom_in_expr2487);
                    e=expr_atom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expr_atom.add(e.getTree());
                    // GCLNew.g:108:5: ( PLUS -> ^( BLOCK $e ^( STAR $e) ) | STAR -> ^( STAR $e) | -> $e)
                    int alt12=3;
                    switch ( input.LA(1) ) {
                    case PLUS:
                        {
                        alt12=1;
                        }
                        break;
                    case STAR:
                        {
                        alt12=2;
                        }
                        break;
                    case RPAR:
                    case SEMI:
                    case OR:
                        {
                        alt12=3;
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 12, 0, input);

                        throw nvae;
                    }

                    switch (alt12) {
                        case 1 :
                            // GCLNew.g:108:7: PLUS
                            {
                            PLUS57=(Token)match(input,PLUS,FOLLOW_PLUS_in_expr2495); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_PLUS.add(PLUS57);



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
                            // 108:12: -> ^( BLOCK $e ^( STAR $e) )
                            {
                                // GCLNew.g:108:15: ^( BLOCK $e ^( STAR $e) )
                                {
                                CommonTree root_1 = (CommonTree)adaptor.nil();
                                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(BLOCK, "BLOCK"), root_1);

                                adaptor.addChild(root_1, stream_e.nextTree());
                                // GCLNew.g:108:26: ^( STAR $e)
                                {
                                CommonTree root_2 = (CommonTree)adaptor.nil();
                                root_2 = (CommonTree)adaptor.becomeRoot(stream_STAR.nextNode(), root_2);

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
                            // GCLNew.g:109:7: STAR
                            {
                            STAR58=(Token)match(input,STAR,FOLLOW_STAR_in_expr2519); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_STAR.add(STAR58);



                            // AST REWRITE
                            // elements: e, STAR
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
                            // 109:12: -> ^( STAR $e)
                            {
                                // GCLNew.g:109:15: ^( STAR $e)
                                {
                                CommonTree root_1 = (CommonTree)adaptor.nil();
                                root_1 = (CommonTree)adaptor.becomeRoot(stream_STAR.nextNode(), root_1);

                                adaptor.addChild(root_1, stream_e.nextTree());

                                adaptor.addChild(root_0, root_1);
                                }

                            }

                            retval.tree = root_0;}
                            }
                            break;
                        case 3 :
                            // GCLNew.g:110:7: 
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
                            // 110:7: -> $e
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
                    // GCLNew.g:112:5: SHARP expr_atom
                    {
                    SHARP59=(Token)match(input,SHARP,FOLLOW_SHARP_in_expr2551); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SHARP.add(SHARP59);

                    pushFollow(FOLLOW_expr_atom_in_expr2553);
                    expr_atom60=expr_atom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expr_atom.add(expr_atom60.getTree());


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
                    // 112:21: -> ^( ALAP expr_atom )
                    {
                        // GCLNew.g:112:24: ^( ALAP expr_atom )
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
    // GCLNew.g:115:1: expr_atom : ( ANY | OTHER | LPAR expr RPAR | call );
    public final GCLNewParser.expr_atom_return expr_atom() throws RecognitionException {
        GCLNewParser.expr_atom_return retval = new GCLNewParser.expr_atom_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token ANY61=null;
        Token OTHER62=null;
        Token LPAR63=null;
        Token RPAR65=null;
        GCLNewParser.expr_return expr64 = null;

        GCLNewParser.call_return call66 = null;


        CommonTree ANY61_tree=null;
        CommonTree OTHER62_tree=null;
        CommonTree LPAR63_tree=null;
        CommonTree RPAR65_tree=null;

        try {
            // GCLNew.g:116:2: ( ANY | OTHER | LPAR expr RPAR | call )
            int alt14=4;
            switch ( input.LA(1) ) {
            case ANY:
                {
                alt14=1;
                }
                break;
            case OTHER:
                {
                alt14=2;
                }
                break;
            case LPAR:
                {
                alt14=3;
                }
                break;
            case ID:
                {
                alt14=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 14, 0, input);

                throw nvae;
            }

            switch (alt14) {
                case 1 :
                    // GCLNew.g:116:4: ANY
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    ANY61=(Token)match(input,ANY,FOLLOW_ANY_in_expr_atom573); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ANY61_tree = (CommonTree)adaptor.create(ANY61);
                    adaptor.addChild(root_0, ANY61_tree);
                    }

                    }
                    break;
                case 2 :
                    // GCLNew.g:117:4: OTHER
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    OTHER62=(Token)match(input,OTHER,FOLLOW_OTHER_in_expr_atom578); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    OTHER62_tree = (CommonTree)adaptor.create(OTHER62);
                    adaptor.addChild(root_0, OTHER62_tree);
                    }

                    }
                    break;
                case 3 :
                    // GCLNew.g:118:4: LPAR expr RPAR
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    LPAR63=(Token)match(input,LPAR,FOLLOW_LPAR_in_expr_atom583); if (state.failed) return retval;
                    pushFollow(FOLLOW_expr_in_expr_atom586);
                    expr64=expr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expr64.getTree());
                    RPAR65=(Token)match(input,RPAR,FOLLOW_RPAR_in_expr_atom588); if (state.failed) return retval;

                    }
                    break;
                case 4 :
                    // GCLNew.g:119:4: call
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_call_in_expr_atom594);
                    call66=call();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, call66.getTree());

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
    // GCLNew.g:122:1: call : rule_name ( LPAR ( arg_list )? RPAR )? -> ^( CALL rule_name ( arg_list )? ) ;
    public final GCLNewParser.call_return call() throws RecognitionException {
        GCLNewParser.call_return retval = new GCLNewParser.call_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token LPAR68=null;
        Token RPAR70=null;
        GCLNewParser.rule_name_return rule_name67 = null;

        GCLNewParser.arg_list_return arg_list69 = null;


        CommonTree LPAR68_tree=null;
        CommonTree RPAR70_tree=null;
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleSubtreeStream stream_arg_list=new RewriteRuleSubtreeStream(adaptor,"rule arg_list");
        RewriteRuleSubtreeStream stream_rule_name=new RewriteRuleSubtreeStream(adaptor,"rule rule_name");
        try {
            // GCLNew.g:123:2: ( rule_name ( LPAR ( arg_list )? RPAR )? -> ^( CALL rule_name ( arg_list )? ) )
            // GCLNew.g:123:4: rule_name ( LPAR ( arg_list )? RPAR )?
            {
            pushFollow(FOLLOW_rule_name_in_call606);
            rule_name67=rule_name();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rule_name.add(rule_name67.getTree());
            // GCLNew.g:123:14: ( LPAR ( arg_list )? RPAR )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==LPAR) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // GCLNew.g:123:15: LPAR ( arg_list )? RPAR
                    {
                    LPAR68=(Token)match(input,LPAR,FOLLOW_LPAR_in_call609); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LPAR.add(LPAR68);

                    // GCLNew.g:123:20: ( arg_list )?
                    int alt15=2;
                    alt15 = dfa15.predict(input);
                    switch (alt15) {
                        case 1 :
                            // GCLNew.g:123:20: arg_list
                            {
                            pushFollow(FOLLOW_arg_list_in_call611);
                            arg_list69=arg_list();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_arg_list.add(arg_list69.getTree());

                            }
                            break;

                    }

                    RPAR70=(Token)match(input,RPAR,FOLLOW_RPAR_in_call614); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RPAR.add(RPAR70);


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
            // 124:4: -> ^( CALL rule_name ( arg_list )? )
            {
                // GCLNew.g:124:7: ^( CALL rule_name ( arg_list )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(CALL, "CALL"), root_1);

                adaptor.addChild(root_1, stream_rule_name.nextTree());
                // GCLNew.g:124:24: ( arg_list )?
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
    // GCLNew.g:127:1: rule_name : ids+= ID ( DOT ids+= ID )* ->;
    public final GCLNewParser.rule_name_return rule_name() throws RecognitionException {
        GCLNewParser.rule_name_return retval = new GCLNewParser.rule_name_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token DOT71=null;
        Token ids=null;
        List list_ids=null;

        CommonTree DOT71_tree=null;
        CommonTree ids_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");

        try {
            // GCLNew.g:129:3: (ids+= ID ( DOT ids+= ID )* ->)
            // GCLNew.g:129:5: ids+= ID ( DOT ids+= ID )*
            {
            ids=(Token)match(input,ID,FOLLOW_ID_in_rule_name646); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ids);

            if (list_ids==null) list_ids=new ArrayList();
            list_ids.add(ids);

            // GCLNew.g:129:13: ( DOT ids+= ID )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( (LA17_0==DOT) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // GCLNew.g:129:14: DOT ids+= ID
            	    {
            	    DOT71=(Token)match(input,DOT,FOLLOW_DOT_in_rule_name649); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(DOT71);

            	    ids=(Token)match(input,ID,FOLLOW_ID_in_rule_name653); if (state.failed) return retval; 
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
            // 130:5: ->
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
    // GCLNew.g:133:1: var_decl : var_type ID ( COMMA ID )* -> ^( VAR var_type ( ID )+ ) ;
    public final GCLNewParser.var_decl_return var_decl() throws RecognitionException {
        GCLNewParser.var_decl_return retval = new GCLNewParser.var_decl_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token ID73=null;
        Token COMMA74=null;
        Token ID75=null;
        GCLNewParser.var_type_return var_type72 = null;


        CommonTree ID73_tree=null;
        CommonTree COMMA74_tree=null;
        CommonTree ID75_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_var_type=new RewriteRuleSubtreeStream(adaptor,"rule var_type");
        try {
            // GCLNew.g:134:2: ( var_type ID ( COMMA ID )* -> ^( VAR var_type ( ID )+ ) )
            // GCLNew.g:134:4: var_type ID ( COMMA ID )*
            {
            pushFollow(FOLLOW_var_type_in_var_decl675);
            var_type72=var_type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_var_type.add(var_type72.getTree());
            ID73=(Token)match(input,ID,FOLLOW_ID_in_var_decl677); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID73);

            // GCLNew.g:134:16: ( COMMA ID )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==COMMA) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // GCLNew.g:134:17: COMMA ID
            	    {
            	    COMMA74=(Token)match(input,COMMA,FOLLOW_COMMA_in_var_decl680); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA74);

            	    ID75=(Token)match(input,ID,FOLLOW_ID_in_var_decl682); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_ID.add(ID75);


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
            // 134:28: -> ^( VAR var_type ( ID )+ )
            {
                // GCLNew.g:134:31: ^( VAR var_type ( ID )+ )
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
    // GCLNew.g:137:1: var_type : ( NODE_TYPE | BOOL_TYPE | STRING_TYPE | INT_TYPE | REAL_TYPE );
    public final GCLNewParser.var_type_return var_type() throws RecognitionException {
        GCLNewParser.var_type_return retval = new GCLNewParser.var_type_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token set76=null;

        CommonTree set76_tree=null;

        try {
            // GCLNew.g:138:2: ( NODE_TYPE | BOOL_TYPE | STRING_TYPE | INT_TYPE | REAL_TYPE )
            // GCLNew.g:
            {
            root_0 = (CommonTree)adaptor.nil();

            set76=(Token)input.LT(1);
            if ( (input.LA(1)>=NODE_TYPE && input.LA(1)<=REAL_TYPE) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (CommonTree)adaptor.create(set76));
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
    // GCLNew.g:145:1: arg_list : arg ( COMMA arg )* ;
    public final GCLNewParser.arg_list_return arg_list() throws RecognitionException {
        GCLNewParser.arg_list_return retval = new GCLNewParser.arg_list_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token COMMA78=null;
        GCLNewParser.arg_return arg77 = null;

        GCLNewParser.arg_return arg79 = null;


        CommonTree COMMA78_tree=null;

        try {
            // GCLNew.g:146:2: ( arg ( COMMA arg )* )
            // GCLNew.g:146:4: arg ( COMMA arg )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_arg_in_arg_list738);
            arg77=arg();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, arg77.getTree());
            // GCLNew.g:146:8: ( COMMA arg )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==COMMA) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // GCLNew.g:146:9: COMMA arg
            	    {
            	    COMMA78=(Token)match(input,COMMA,FOLLOW_COMMA_in_arg_list741); if (state.failed) return retval;
            	    pushFollow(FOLLOW_arg_in_arg_list744);
            	    arg79=arg();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, arg79.getTree());

            	    }
            	    break;

            	default :
            	    break loop19;
                }
            } while (true);


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
    // GCLNew.g:149:1: arg : ( OUT ID -> ^( ARG OUT ID ) | ID -> ^( ARG ID ) | DONT_CARE -> ^( ARG DONT_CARE ) | literal -> ^( ARG literal ) );
    public final GCLNewParser.arg_return arg() throws RecognitionException {
        GCLNewParser.arg_return retval = new GCLNewParser.arg_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token OUT80=null;
        Token ID81=null;
        Token ID82=null;
        Token DONT_CARE83=null;
        GCLNewParser.literal_return literal84 = null;


        CommonTree OUT80_tree=null;
        CommonTree ID81_tree=null;
        CommonTree ID82_tree=null;
        CommonTree DONT_CARE83_tree=null;
        RewriteRuleTokenStream stream_DONT_CARE=new RewriteRuleTokenStream(adaptor,"token DONT_CARE");
        RewriteRuleTokenStream stream_OUT=new RewriteRuleTokenStream(adaptor,"token OUT");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_literal=new RewriteRuleSubtreeStream(adaptor,"rule literal");
        try {
            // GCLNew.g:150:2: ( OUT ID -> ^( ARG OUT ID ) | ID -> ^( ARG ID ) | DONT_CARE -> ^( ARG DONT_CARE ) | literal -> ^( ARG literal ) )
            int alt20=4;
            switch ( input.LA(1) ) {
            case OUT:
                {
                alt20=1;
                }
                break;
            case ID:
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
            case STRING_LIT:
            case INT_LIT:
            case REAL_LIT:
                {
                alt20=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 20, 0, input);

                throw nvae;
            }

            switch (alt20) {
                case 1 :
                    // GCLNew.g:150:4: OUT ID
                    {
                    OUT80=(Token)match(input,OUT,FOLLOW_OUT_in_arg757); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_OUT.add(OUT80);

                    ID81=(Token)match(input,ID,FOLLOW_ID_in_arg759); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID81);



                    // AST REWRITE
                    // elements: OUT, ID
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 150:11: -> ^( ARG OUT ID )
                    {
                        // GCLNew.g:150:14: ^( ARG OUT ID )
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
                    // GCLNew.g:151:4: ID
                    {
                    ID82=(Token)match(input,ID,FOLLOW_ID_in_arg774); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID82);



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
                    // 151:7: -> ^( ARG ID )
                    {
                        // GCLNew.g:151:10: ^( ARG ID )
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
                    // GCLNew.g:152:4: DONT_CARE
                    {
                    DONT_CARE83=(Token)match(input,DONT_CARE,FOLLOW_DONT_CARE_in_arg787); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DONT_CARE.add(DONT_CARE83);



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
                    // 152:14: -> ^( ARG DONT_CARE )
                    {
                        // GCLNew.g:152:17: ^( ARG DONT_CARE )
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
                    // GCLNew.g:153:4: literal
                    {
                    pushFollow(FOLLOW_literal_in_arg800);
                    literal84=literal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_literal.add(literal84.getTree());


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
                    // 153:12: -> ^( ARG literal )
                    {
                        // GCLNew.g:153:15: ^( ARG literal )
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
    // GCLNew.g:156:1: literal : ( TRUE -> ^( BOOL_TYPE TRUE ) | FALSE -> ^( BOOL_TYPE FALSE ) | STRING_LIT -> ^( STRING_TYPE ) | INT_LIT -> ^( INT_TYPE INT_LIT ) | REAL_LIT -> ^( REAL_TYPE REAL_LIT ) );
    public final GCLNewParser.literal_return literal() throws RecognitionException {
        GCLNewParser.literal_return retval = new GCLNewParser.literal_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token TRUE85=null;
        Token FALSE86=null;
        Token STRING_LIT87=null;
        Token INT_LIT88=null;
        Token REAL_LIT89=null;

        CommonTree TRUE85_tree=null;
        CommonTree FALSE86_tree=null;
        CommonTree STRING_LIT87_tree=null;
        CommonTree INT_LIT88_tree=null;
        CommonTree REAL_LIT89_tree=null;
        RewriteRuleTokenStream stream_REAL_LIT=new RewriteRuleTokenStream(adaptor,"token REAL_LIT");
        RewriteRuleTokenStream stream_INT_LIT=new RewriteRuleTokenStream(adaptor,"token INT_LIT");
        RewriteRuleTokenStream stream_FALSE=new RewriteRuleTokenStream(adaptor,"token FALSE");
        RewriteRuleTokenStream stream_STRING_LIT=new RewriteRuleTokenStream(adaptor,"token STRING_LIT");
        RewriteRuleTokenStream stream_TRUE=new RewriteRuleTokenStream(adaptor,"token TRUE");

        try {
            // GCLNew.g:157:2: ( TRUE -> ^( BOOL_TYPE TRUE ) | FALSE -> ^( BOOL_TYPE FALSE ) | STRING_LIT -> ^( STRING_TYPE ) | INT_LIT -> ^( INT_TYPE INT_LIT ) | REAL_LIT -> ^( REAL_TYPE REAL_LIT ) )
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
            case STRING_LIT:
                {
                alt21=3;
                }
                break;
            case INT_LIT:
                {
                alt21=4;
                }
                break;
            case REAL_LIT:
                {
                alt21=5;
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
                    // GCLNew.g:157:4: TRUE
                    {
                    TRUE85=(Token)match(input,TRUE,FOLLOW_TRUE_in_literal819); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_TRUE.add(TRUE85);



                    // AST REWRITE
                    // elements: TRUE
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 157:9: -> ^( BOOL_TYPE TRUE )
                    {
                        // GCLNew.g:157:12: ^( BOOL_TYPE TRUE )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(BOOL_TYPE, "BOOL_TYPE"), root_1);

                        adaptor.addChild(root_1, stream_TRUE.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // GCLNew.g:158:4: FALSE
                    {
                    FALSE86=(Token)match(input,FALSE,FOLLOW_FALSE_in_literal832); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_FALSE.add(FALSE86);



                    // AST REWRITE
                    // elements: FALSE
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 158:10: -> ^( BOOL_TYPE FALSE )
                    {
                        // GCLNew.g:158:13: ^( BOOL_TYPE FALSE )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(BOOL_TYPE, "BOOL_TYPE"), root_1);

                        adaptor.addChild(root_1, stream_FALSE.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // GCLNew.g:159:4: STRING_LIT
                    {
                    STRING_LIT87=(Token)match(input,STRING_LIT,FOLLOW_STRING_LIT_in_literal845); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_STRING_LIT.add(STRING_LIT87);



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
                    // 159:15: -> ^( STRING_TYPE )
                    {
                        // GCLNew.g:159:18: ^( STRING_TYPE )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(STRING_TYPE, "STRING_TYPE"), root_1);

                        adaptor.addChild(root_1,  helper.toUnquoted((STRING_LIT87!=null?STRING_LIT87.getText():null)) );

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 4 :
                    // GCLNew.g:160:4: INT_LIT
                    {
                    INT_LIT88=(Token)match(input,INT_LIT,FOLLOW_INT_LIT_in_literal859); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_INT_LIT.add(INT_LIT88);



                    // AST REWRITE
                    // elements: INT_LIT
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 160:12: -> ^( INT_TYPE INT_LIT )
                    {
                        // GCLNew.g:160:15: ^( INT_TYPE INT_LIT )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(INT_TYPE, "INT_TYPE"), root_1);

                        adaptor.addChild(root_1, stream_INT_LIT.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 5 :
                    // GCLNew.g:161:4: REAL_LIT
                    {
                    REAL_LIT89=(Token)match(input,REAL_LIT,FOLLOW_REAL_LIT_in_literal872); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_REAL_LIT.add(REAL_LIT89);



                    // AST REWRITE
                    // elements: REAL_LIT
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 161:13: -> ^( REAL_TYPE REAL_LIT )
                    {
                        // GCLNew.g:161:16: ^( REAL_TYPE REAL_LIT )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(REAL_TYPE, "REAL_TYPE"), root_1);

                        adaptor.addChild(root_1, stream_REAL_LIT.nextNode());

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
    // $ANTLR end "literal"

    // $ANTLR start synpred1_GCLNew
    public final void synpred1_GCLNew_fragment() throws RecognitionException {   
        // GCLNew.g:82:33: ( ELSE )
        // GCLNew.g:82:34: ELSE
        {
        match(input,ELSE,FOLLOW_ELSE_in_synpred1_GCLNew285); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_GCLNew

    // $ANTLR start synpred2_GCLNew
    public final void synpred2_GCLNew_fragment() throws RecognitionException {   
        // GCLNew.g:83:17: ( ELSE )
        // GCLNew.g:83:18: ELSE
        {
        match(input,ELSE,FOLLOW_ELSE_in_synpred2_GCLNew310); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred2_GCLNew

    // $ANTLR start synpred3_GCLNew
    public final void synpred3_GCLNew_fragment() throws RecognitionException {   
        // GCLNew.g:84:19: ( CH_OR )
        // GCLNew.g:84:20: CH_OR
        {
        match(input,CH_OR,FOLLOW_CH_OR_in_synpred3_GCLNew334); if (state.failed) return ;

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
    protected DFA6 dfa6 = new DFA6(this);
    protected DFA3 dfa3 = new DFA3(this);
    protected DFA4 dfa4 = new DFA4(this);
    protected DFA5 dfa5 = new DFA5(this);
    protected DFA15 dfa15 = new DFA15(this);
    static final String DFA1_eotS =
        "\21\uffff";
    static final String DFA1_eofS =
        "\1\1\20\uffff";
    static final String DFA1_minS =
        "\1\7\20\uffff";
    static final String DFA1_maxS =
        "\1\50\20\uffff";
    static final String DFA1_acceptS =
        "\1\uffff\1\3\1\1\1\2\15\uffff";
    static final String DFA1_specialS =
        "\21\uffff}>";
    static final String[] DFA1_transitionS = {
            "\1\2\4\uffff\1\3\1\uffff\2\3\1\uffff\5\3\1\uffff\2\3\6\uffff"+
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
            return "()* loopback of 65:5: ( function | stat )*";
        }
    }
    static final String DFA2_eotS =
        "\20\uffff";
    static final String DFA2_eofS =
        "\20\uffff";
    static final String DFA2_minS =
        "\1\14\17\uffff";
    static final String DFA2_maxS =
        "\1\50\17\uffff";
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
            return "()* loopback of 70:11: ( stat )*";
        }
    }
    static final String DFA6_eotS =
        "\17\uffff";
    static final String DFA6_eofS =
        "\17\uffff";
    static final String DFA6_minS =
        "\1\14\16\uffff";
    static final String DFA6_maxS =
        "\1\50\16\uffff";
    static final String DFA6_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\4\uffff\1\12";
    static final String DFA6_specialS =
        "\17\uffff}>";
    static final String[] DFA6_transitionS = {
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
            return "76:1: stat : ( block | ALAP stat -> ^( ALAP stat ) | WHILE LPAR cond RPAR stat | UNTIL LPAR cond RPAR stat | DO stat WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | IF LPAR cond RPAR stat ( ( ELSE )=> ELSE stat )? | TRY stat ( ( ELSE )=> ELSE stat )? | CHOICE stat ( ( CH_OR )=> CH_OR stat )+ | expr SEMI | var_decl SEMI );";
        }
    }
    static final String DFA3_eotS =
        "\43\uffff";
    static final String DFA3_eofS =
        "\1\2\42\uffff";
    static final String DFA3_minS =
        "\1\7\1\0\41\uffff";
    static final String DFA3_maxS =
        "\1\50\1\0\41\uffff";
    static final String DFA3_acceptS =
        "\2\uffff\1\2\37\uffff\1\1";
    static final String DFA3_specialS =
        "\1\uffff\1\0\41\uffff}>";
    static final String[] DFA3_transitionS = {
            "\1\2\4\uffff\4\2\1\uffff\5\2\1\1\3\2\5\uffff\3\2\2\uffff\5"+
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

    static final short[] DFA3_eot = DFA.unpackEncodedString(DFA3_eotS);
    static final short[] DFA3_eof = DFA.unpackEncodedString(DFA3_eofS);
    static final char[] DFA3_min = DFA.unpackEncodedStringToUnsignedChars(DFA3_minS);
    static final char[] DFA3_max = DFA.unpackEncodedStringToUnsignedChars(DFA3_maxS);
    static final short[] DFA3_accept = DFA.unpackEncodedString(DFA3_acceptS);
    static final short[] DFA3_special = DFA.unpackEncodedString(DFA3_specialS);
    static final short[][] DFA3_transition;

    static {
        int numStates = DFA3_transitionS.length;
        DFA3_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA3_transition[i] = DFA.unpackEncodedString(DFA3_transitionS[i]);
        }
    }

    class DFA3 extends DFA {

        public DFA3(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 3;
            this.eot = DFA3_eot;
            this.eof = DFA3_eof;
            this.min = DFA3_min;
            this.max = DFA3_max;
            this.accept = DFA3_accept;
            this.special = DFA3_special;
            this.transition = DFA3_transition;
        }
        public String getDescription() {
            return "82:31: ( ( ELSE )=> ELSE stat )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA3_1 = input.LA(1);

                         
                        int index3_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_GCLNew()) ) {s = 34;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index3_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 3, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA4_eotS =
        "\43\uffff";
    static final String DFA4_eofS =
        "\1\2\42\uffff";
    static final String DFA4_minS =
        "\1\7\1\0\41\uffff";
    static final String DFA4_maxS =
        "\1\50\1\0\41\uffff";
    static final String DFA4_acceptS =
        "\2\uffff\1\2\37\uffff\1\1";
    static final String DFA4_specialS =
        "\1\uffff\1\0\41\uffff}>";
    static final String[] DFA4_transitionS = {
            "\1\2\4\uffff\4\2\1\uffff\5\2\1\1\3\2\5\uffff\3\2\2\uffff\5"+
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
            return "83:15: ( ( ELSE )=> ELSE stat )?";
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
                        if ( (synpred2_GCLNew()) ) {s = 34;}

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
        "\1\1\42\uffff";
    static final String DFA5_minS =
        "\1\7\22\uffff\1\0\17\uffff";
    static final String DFA5_maxS =
        "\1\50\22\uffff\1\0\17\uffff";
    static final String DFA5_acceptS =
        "\1\uffff\1\2\40\uffff\1\1";
    static final String DFA5_specialS =
        "\23\uffff\1\0\17\uffff}>";
    static final String[] DFA5_transitionS = {
            "\1\1\4\uffff\4\1\1\uffff\10\1\1\23\5\uffff\3\1\2\uffff\5\1",
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
            return "()+ loopback of 84:18: ( ( CH_OR )=> CH_OR stat )+";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA5_19 = input.LA(1);

                         
                        int index5_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred3_GCLNew()) ) {s = 34;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index5_19);
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
    static final String DFA15_eotS =
        "\12\uffff";
    static final String DFA15_eofS =
        "\12\uffff";
    static final String DFA15_minS =
        "\1\16\11\uffff";
    static final String DFA15_maxS =
        "\1\56\11\uffff";
    static final String DFA15_acceptS =
        "\1\uffff\1\1\7\uffff\1\2";
    static final String DFA15_specialS =
        "\12\uffff}>";
    static final String[] DFA15_transitionS = {
            "\1\1\1\uffff\1\11\13\uffff\1\1\14\uffff\6\1",
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

    static final short[] DFA15_eot = DFA.unpackEncodedString(DFA15_eotS);
    static final short[] DFA15_eof = DFA.unpackEncodedString(DFA15_eofS);
    static final char[] DFA15_min = DFA.unpackEncodedStringToUnsignedChars(DFA15_minS);
    static final char[] DFA15_max = DFA.unpackEncodedStringToUnsignedChars(DFA15_maxS);
    static final short[] DFA15_accept = DFA.unpackEncodedString(DFA15_acceptS);
    static final short[] DFA15_special = DFA.unpackEncodedString(DFA15_specialS);
    static final short[][] DFA15_transition;

    static {
        int numStates = DFA15_transitionS.length;
        DFA15_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA15_transition[i] = DFA.unpackEncodedString(DFA15_transitionS[i]);
        }
    }

    class DFA15 extends DFA {

        public DFA15(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 15;
            this.eot = DFA15_eot;
            this.eof = DFA15_eof;
            this.min = DFA15_min;
            this.max = DFA15_max;
            this.accept = DFA15_accept;
            this.special = DFA15_special;
            this.transition = DFA15_transition;
        }
        public String getDescription() {
            return "123:20: ( arg_list )?";
        }
    }
 

    public static final BitSet FOLLOW_function_in_program98 = new BitSet(new long[]{0x000001F381BED082L});
    public static final BitSet FOLLOW_stat_in_program100 = new BitSet(new long[]{0x000001F381BED082L});
    public static final BitSet FOLLOW_LCURLY_in_block138 = new BitSet(new long[]{0x000001F381BEF080L});
    public static final BitSet FOLLOW_stat_in_block140 = new BitSet(new long[]{0x000001F381BEF080L});
    public static final BitSet FOLLOW_RCURLY_in_block143 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_function162 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_ID_in_function165 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_LPAR_in_function167 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_RPAR_in_function170 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_block_in_function173 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_stat185 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALAP_in_stat190 = new BitSet(new long[]{0x000001F381BED080L});
    public static final BitSet FOLLOW_stat_in_stat192 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHILE_in_stat205 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_LPAR_in_stat208 = new BitSet(new long[]{0x000000031000C000L});
    public static final BitSet FOLLOW_cond_in_stat211 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_RPAR_in_stat213 = new BitSet(new long[]{0x000001F381BED080L});
    public static final BitSet FOLLOW_stat_in_stat216 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNTIL_in_stat221 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_LPAR_in_stat224 = new BitSet(new long[]{0x000000031000C000L});
    public static final BitSet FOLLOW_cond_in_stat227 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_RPAR_in_stat229 = new BitSet(new long[]{0x000001F381BED080L});
    public static final BitSet FOLLOW_stat_in_stat232 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DO_in_stat237 = new BitSet(new long[]{0x000001F381BED080L});
    public static final BitSet FOLLOW_stat_in_stat239 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_WHILE_in_stat241 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_LPAR_in_stat243 = new BitSet(new long[]{0x000000031000C000L});
    public static final BitSet FOLLOW_cond_in_stat245 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_RPAR_in_stat247 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IF_in_stat269 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_LPAR_in_stat272 = new BitSet(new long[]{0x000000031000C000L});
    public static final BitSet FOLLOW_cond_in_stat275 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_RPAR_in_stat277 = new BitSet(new long[]{0x000001F381FED080L});
    public static final BitSet FOLLOW_stat_in_stat280 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_ELSE_in_stat290 = new BitSet(new long[]{0x000001F381BED080L});
    public static final BitSet FOLLOW_stat_in_stat293 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRY_in_stat302 = new BitSet(new long[]{0x000001F381FED080L});
    public static final BitSet FOLLOW_stat_in_stat305 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_ELSE_in_stat315 = new BitSet(new long[]{0x000001F381BED080L});
    public static final BitSet FOLLOW_stat_in_stat318 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CHOICE_in_stat327 = new BitSet(new long[]{0x000001F383BED080L});
    public static final BitSet FOLLOW_stat_in_stat330 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_CH_OR_in_stat339 = new BitSet(new long[]{0x000001F383BED080L});
    public static final BitSet FOLLOW_stat_in_stat342 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_expr_in_stat349 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_SEMI_in_stat351 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_decl_in_stat357 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_SEMI_in_stat359 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_cond_atom_in_cond372 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_OR_in_cond381 = new BitSet(new long[]{0x000000031000C000L});
    public static final BitSet FOLLOW_cond_atom_in_cond383 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_TRUE_in_cond_atom421 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_cond_atom425 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr2_in_expr436 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_OR_in_expr444 = new BitSet(new long[]{0x000000038000C000L});
    public static final BitSet FOLLOW_expr2_in_expr446 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_expr_atom_in_expr2487 = new BitSet(new long[]{0x0000000060000002L});
    public static final BitSet FOLLOW_PLUS_in_expr2495 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STAR_in_expr2519 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SHARP_in_expr2551 = new BitSet(new long[]{0x000000030000C000L});
    public static final BitSet FOLLOW_expr_atom_in_expr2553 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_expr_atom573 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_expr_atom578 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_expr_atom583 = new BitSet(new long[]{0x000000038000C000L});
    public static final BitSet FOLLOW_expr_in_expr_atom586 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_RPAR_in_expr_atom588 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_expr_atom594 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_name_in_call606 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_LPAR_in_call609 = new BitSet(new long[]{0x00007E0010014000L});
    public static final BitSet FOLLOW_arg_list_in_call611 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_RPAR_in_call614 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_rule_name646 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_DOT_in_rule_name649 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_ID_in_rule_name653 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_var_type_in_var_decl675 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_ID_in_var_decl677 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_COMMA_in_var_decl680 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_ID_in_var_decl682 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_set_in_var_type0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arg_in_arg_list738 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_COMMA_in_arg_list741 = new BitSet(new long[]{0x00007E0010004000L});
    public static final BitSet FOLLOW_arg_in_arg_list744 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_OUT_in_arg757 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_ID_in_arg759 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_arg774 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DONT_CARE_in_arg787 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_arg800 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_literal819 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FALSE_in_literal832 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LIT_in_literal845 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_LIT_in_literal859 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REAL_LIT_in_literal872 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ELSE_in_synpred1_GCLNew285 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ELSE_in_synpred2_GCLNew310 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CH_OR_in_synpred3_GCLNew334 = new BitSet(new long[]{0x0000000000000002L});

}