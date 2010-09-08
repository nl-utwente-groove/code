// $ANTLR 3.2 Sep 23, 2009 12:02:23 GCLNew.g 2010-09-08 15:26:58

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "PROGRAM", "BLOCK", "FUNCTIONS", "FUNCTION", "CALL", "DO_WHILE", "VAR", "ARG", "LCURLY", "RCURLY", "ID", "LPAR", "RPAR", "ALAP", "WHILE", "UNTIL", "DO", "IF", "ELSE", "TRY", "CHOICE", "CH_OR", "SEMI", "OR", "TRUE", "PLUS", "STAR", "SHARP", "ANY", "OTHER", "DOT", "COMMA", "NODE_TYPE", "BOOL_TYPE", "STRING_TYPE", "INT_TYPE", "REAL_TYPE", "OUT", "DONT_CARE", "FALSE", "QUOTE", "BSLASH", "MINUS", "NUMBER", "AND", "NOT", "ML_COMMENT", "SL_COMMENT", "WS"
    };
    public static final int FUNCTION=7;
    public static final int STAR=30;
    public static final int WHILE=18;
    public static final int FUNCTIONS=6;
    public static final int BOOL_TYPE=37;
    public static final int NODE_TYPE=36;
    public static final int DO=20;
    public static final int NOT=49;
    public static final int ALAP=17;
    public static final int AND=48;
    public static final int ID=14;
    public static final int EOF=-1;
    public static final int IF=21;
    public static final int ML_COMMENT=50;
    public static final int QUOTE=44;
    public static final int LPAR=15;
    public static final int ARG=11;
    public static final int COMMA=35;
    public static final int DO_WHILE=9;
    public static final int CH_OR=25;
    public static final int PLUS=29;
    public static final int VAR=10;
    public static final int DOT=34;
    public static final int CHOICE=24;
    public static final int SHARP=31;
    public static final int OTHER=33;
    public static final int ELSE=22;
    public static final int NUMBER=47;
    public static final int LCURLY=12;
    public static final int MINUS=46;
    public static final int INT_TYPE=39;
    public static final int SEMI=26;
    public static final int TRUE=28;
    public static final int TRY=23;
    public static final int REAL_TYPE=40;
    public static final int DONT_CARE=42;
    public static final int WS=52;
    public static final int ANY=32;
    public static final int OUT=41;
    public static final int UNTIL=19;
    public static final int BLOCK=5;
    public static final int STRING_TYPE=38;
    public static final int SL_COMMENT=51;
    public static final int RCURLY=13;
    public static final int OR=27;
    public static final int RPAR=16;
    public static final int PROGRAM=4;
    public static final int CALL=8;
    public static final int FALSE=43;
    public static final int BSLASH=45;

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

    	CommonTree concat(CommonTree seq) {
            String result;
            List children = seq.getChildren();
            if (children == null) {
                result = seq.getText();
            } else {
                StringBuilder builder = new StringBuilder();
                for (Object token: seq.getChildren()) {
                    builder.append(((CommonTree) token).getText());
                }
                result = builder.toString();
            }
            return new CommonTree(new CommonToken(ID, result));
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
            	    pushFollow(FOLLOW_function_in_program99);
            	    function1=function();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_function.add(function1.getTree());

            	    }
            	    break;
            	case 2 :
            	    // GCLNew.g:65:15: stat
            	    {
            	    pushFollow(FOLLOW_stat_in_program101);
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
            LCURLY3=(Token)match(input,LCURLY,FOLLOW_LCURLY_in_block139); if (state.failed) return retval; 
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
            	    pushFollow(FOLLOW_stat_in_block141);
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

            RCURLY5=(Token)match(input,RCURLY,FOLLOW_RCURLY_in_block144); if (state.failed) return retval; 
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

            FUNCTION6=(Token)match(input,FUNCTION,FOLLOW_FUNCTION_in_function163); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            FUNCTION6_tree = (CommonTree)adaptor.create(FUNCTION6);
            root_0 = (CommonTree)adaptor.becomeRoot(FUNCTION6_tree, root_0);
            }
            ID7=(Token)match(input,ID,FOLLOW_ID_in_function166); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID7_tree = (CommonTree)adaptor.create(ID7);
            adaptor.addChild(root_0, ID7_tree);
            }
            LPAR8=(Token)match(input,LPAR,FOLLOW_LPAR_in_function168); if (state.failed) return retval;
            RPAR9=(Token)match(input,RPAR,FOLLOW_RPAR_in_function171); if (state.failed) return retval;
            pushFollow(FOLLOW_block_in_function174);
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

                    pushFollow(FOLLOW_block_in_stat186);
                    block11=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block11.getTree());

                    }
                    break;
                case 2 :
                    // GCLNew.g:78:4: ALAP stat
                    {
                    ALAP12=(Token)match(input,ALAP,FOLLOW_ALAP_in_stat191); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ALAP.add(ALAP12);

                    pushFollow(FOLLOW_stat_in_stat193);
                    stat13=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_stat.add(stat13.getTree());


                    // AST REWRITE
                    // elements: stat, ALAP
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

                    WHILE14=(Token)match(input,WHILE,FOLLOW_WHILE_in_stat206); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    WHILE14_tree = (CommonTree)adaptor.create(WHILE14);
                    root_0 = (CommonTree)adaptor.becomeRoot(WHILE14_tree, root_0);
                    }
                    LPAR15=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat209); if (state.failed) return retval;
                    pushFollow(FOLLOW_cond_in_stat212);
                    cond16=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond16.getTree());
                    RPAR17=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat214); if (state.failed) return retval;
                    pushFollow(FOLLOW_stat_in_stat217);
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

                    UNTIL19=(Token)match(input,UNTIL,FOLLOW_UNTIL_in_stat222); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    UNTIL19_tree = (CommonTree)adaptor.create(UNTIL19);
                    root_0 = (CommonTree)adaptor.becomeRoot(UNTIL19_tree, root_0);
                    }
                    LPAR20=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat225); if (state.failed) return retval;
                    pushFollow(FOLLOW_cond_in_stat228);
                    cond21=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond21.getTree());
                    RPAR22=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat230); if (state.failed) return retval;
                    pushFollow(FOLLOW_stat_in_stat233);
                    stat23=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat23.getTree());

                    }
                    break;
                case 5 :
                    // GCLNew.g:81:4: DO stat WHILE LPAR cond RPAR
                    {
                    DO24=(Token)match(input,DO,FOLLOW_DO_in_stat238); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DO.add(DO24);

                    pushFollow(FOLLOW_stat_in_stat240);
                    stat25=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_stat.add(stat25.getTree());
                    WHILE26=(Token)match(input,WHILE,FOLLOW_WHILE_in_stat242); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_WHILE.add(WHILE26);

                    LPAR27=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat244); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LPAR.add(LPAR27);

                    pushFollow(FOLLOW_cond_in_stat246);
                    cond28=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_cond.add(cond28.getTree());
                    RPAR29=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat248); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RPAR.add(RPAR29);



                    // AST REWRITE
                    // elements: stat, cond, WHILE, stat
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

                    IF30=(Token)match(input,IF,FOLLOW_IF_in_stat270); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IF30_tree = (CommonTree)adaptor.create(IF30);
                    root_0 = (CommonTree)adaptor.becomeRoot(IF30_tree, root_0);
                    }
                    LPAR31=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat273); if (state.failed) return retval;
                    pushFollow(FOLLOW_cond_in_stat276);
                    cond32=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond32.getTree());
                    RPAR33=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat278); if (state.failed) return retval;
                    pushFollow(FOLLOW_stat_in_stat281);
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
                            ELSE35=(Token)match(input,ELSE,FOLLOW_ELSE_in_stat291); if (state.failed) return retval;
                            pushFollow(FOLLOW_stat_in_stat294);
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

                    TRY37=(Token)match(input,TRY,FOLLOW_TRY_in_stat303); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    TRY37_tree = (CommonTree)adaptor.create(TRY37);
                    root_0 = (CommonTree)adaptor.becomeRoot(TRY37_tree, root_0);
                    }
                    pushFollow(FOLLOW_stat_in_stat306);
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
                            ELSE39=(Token)match(input,ELSE,FOLLOW_ELSE_in_stat316); if (state.failed) return retval;
                            pushFollow(FOLLOW_stat_in_stat319);
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

                    CHOICE41=(Token)match(input,CHOICE,FOLLOW_CHOICE_in_stat328); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CHOICE41_tree = (CommonTree)adaptor.create(CHOICE41);
                    root_0 = (CommonTree)adaptor.becomeRoot(CHOICE41_tree, root_0);
                    }
                    pushFollow(FOLLOW_stat_in_stat331);
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
                    	    CH_OR43=(Token)match(input,CH_OR,FOLLOW_CH_OR_in_stat340); if (state.failed) return retval;
                    	    pushFollow(FOLLOW_stat_in_stat343);
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

                    pushFollow(FOLLOW_expr_in_stat350);
                    expr45=expr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expr45.getTree());
                    SEMI46=(Token)match(input,SEMI,FOLLOW_SEMI_in_stat352); if (state.failed) return retval;

                    }
                    break;
                case 10 :
                    // GCLNew.g:86:4: var_decl SEMI
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_var_decl_in_stat358);
                    var_decl47=var_decl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, var_decl47.getTree());
                    SEMI48=(Token)match(input,SEMI,FOLLOW_SEMI_in_stat360); if (state.failed) return retval;

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
            pushFollow(FOLLOW_cond_atom_in_cond373);
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
                    	    OR50=(Token)match(input,OR,FOLLOW_OR_in_cond382); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_OR.add(OR50);

                    	    pushFollow(FOLLOW_cond_atom_in_cond384);
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

                    TRUE52=(Token)match(input,TRUE,FOLLOW_TRUE_in_cond_atom422); if (state.failed) return retval;
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

                    pushFollow(FOLLOW_call_in_cond_atom426);
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
            pushFollow(FOLLOW_expr2_in_expr437);
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
                    	    OR55=(Token)match(input,OR,FOLLOW_OR_in_expr445); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_OR.add(OR55);

                    	    pushFollow(FOLLOW_expr2_in_expr447);
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
                    pushFollow(FOLLOW_expr_atom_in_expr2488);
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
                            PLUS57=(Token)match(input,PLUS,FOLLOW_PLUS_in_expr2496); if (state.failed) return retval; 
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
                            STAR58=(Token)match(input,STAR,FOLLOW_STAR_in_expr2520); if (state.failed) return retval; 
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
                    SHARP59=(Token)match(input,SHARP,FOLLOW_SHARP_in_expr2552); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SHARP.add(SHARP59);

                    pushFollow(FOLLOW_expr_atom_in_expr2554);
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

                    ANY61=(Token)match(input,ANY,FOLLOW_ANY_in_expr_atom574); if (state.failed) return retval;
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

                    OTHER62=(Token)match(input,OTHER,FOLLOW_OTHER_in_expr_atom579); if (state.failed) return retval;
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

                    LPAR63=(Token)match(input,LPAR,FOLLOW_LPAR_in_expr_atom584); if (state.failed) return retval;
                    pushFollow(FOLLOW_expr_in_expr_atom587);
                    expr64=expr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expr64.getTree());
                    RPAR65=(Token)match(input,RPAR,FOLLOW_RPAR_in_expr_atom589); if (state.failed) return retval;

                    }
                    break;
                case 4 :
                    // GCLNew.g:119:4: call
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_call_in_expr_atom595);
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
    // GCLNew.g:122:1: call : rule_name ( LPAR ( arg_list )? RPAR )? -> ^( CALL ( arg_list )? ) ;
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
            // GCLNew.g:123:2: ( rule_name ( LPAR ( arg_list )? RPAR )? -> ^( CALL ( arg_list )? ) )
            // GCLNew.g:123:4: rule_name ( LPAR ( arg_list )? RPAR )?
            {
            pushFollow(FOLLOW_rule_name_in_call607);
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
                    LPAR68=(Token)match(input,LPAR,FOLLOW_LPAR_in_call610); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LPAR.add(LPAR68);

                    // GCLNew.g:123:20: ( arg_list )?
                    int alt15=2;
                    alt15 = dfa15.predict(input);
                    switch (alt15) {
                        case 1 :
                            // GCLNew.g:123:20: arg_list
                            {
                            pushFollow(FOLLOW_arg_list_in_call612);
                            arg_list69=arg_list();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_arg_list.add(arg_list69.getTree());

                            }
                            break;

                    }

                    RPAR70=(Token)match(input,RPAR,FOLLOW_RPAR_in_call615); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RPAR.add(RPAR70);


                    }
                    break;

            }



            // AST REWRITE
            // elements: arg_list
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 124:4: -> ^( CALL ( arg_list )? )
            {
                // GCLNew.g:124:7: ^( CALL ( arg_list )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(CALL, "CALL"), root_1);

                adaptor.addChild(root_1,  concat((rule_name67!=null?((CommonTree)rule_name67.tree):null)) );
                // GCLNew.g:124:42: ( arg_list )?
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
    // GCLNew.g:127:1: rule_name : ID ( DOT ID )* ;
    public final GCLNewParser.rule_name_return rule_name() throws RecognitionException {
        GCLNewParser.rule_name_return retval = new GCLNewParser.rule_name_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token ID71=null;
        Token DOT72=null;
        Token ID73=null;

        CommonTree ID71_tree=null;
        CommonTree DOT72_tree=null;
        CommonTree ID73_tree=null;

        try {
            // GCLNew.g:128:3: ( ID ( DOT ID )* )
            // GCLNew.g:128:5: ID ( DOT ID )*
            {
            root_0 = (CommonTree)adaptor.nil();

            ID71=(Token)match(input,ID,FOLLOW_ID_in_rule_name643); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID71_tree = (CommonTree)adaptor.create(ID71);
            adaptor.addChild(root_0, ID71_tree);
            }
            // GCLNew.g:128:8: ( DOT ID )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( (LA17_0==DOT) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // GCLNew.g:128:9: DOT ID
            	    {
            	    DOT72=(Token)match(input,DOT,FOLLOW_DOT_in_rule_name646); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    DOT72_tree = (CommonTree)adaptor.create(DOT72);
            	    adaptor.addChild(root_0, DOT72_tree);
            	    }
            	    ID73=(Token)match(input,ID,FOLLOW_ID_in_rule_name648); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    ID73_tree = (CommonTree)adaptor.create(ID73);
            	    adaptor.addChild(root_0, ID73_tree);
            	    }

            	    }
            	    break;

            	default :
            	    break loop17;
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
    // $ANTLR end "rule_name"

    public static class var_decl_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "var_decl"
    // GCLNew.g:131:1: var_decl : var_type ID ( COMMA ID )* -> ^( VAR var_type ( ID )+ ) ;
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
            // GCLNew.g:132:2: ( var_type ID ( COMMA ID )* -> ^( VAR var_type ( ID )+ ) )
            // GCLNew.g:132:4: var_type ID ( COMMA ID )*
            {
            pushFollow(FOLLOW_var_type_in_var_decl662);
            var_type74=var_type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_var_type.add(var_type74.getTree());
            ID75=(Token)match(input,ID,FOLLOW_ID_in_var_decl664); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID75);

            // GCLNew.g:132:16: ( COMMA ID )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==COMMA) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // GCLNew.g:132:17: COMMA ID
            	    {
            	    COMMA76=(Token)match(input,COMMA,FOLLOW_COMMA_in_var_decl667); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA76);

            	    ID77=(Token)match(input,ID,FOLLOW_ID_in_var_decl669); if (state.failed) return retval; 
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
            // 132:28: -> ^( VAR var_type ( ID )+ )
            {
                // GCLNew.g:132:31: ^( VAR var_type ( ID )+ )
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
    // GCLNew.g:135:1: var_type : ( NODE_TYPE | BOOL_TYPE | STRING_TYPE | INT_TYPE | REAL_TYPE );
    public final GCLNewParser.var_type_return var_type() throws RecognitionException {
        GCLNewParser.var_type_return retval = new GCLNewParser.var_type_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token set78=null;

        CommonTree set78_tree=null;

        try {
            // GCLNew.g:136:2: ( NODE_TYPE | BOOL_TYPE | STRING_TYPE | INT_TYPE | REAL_TYPE )
            // GCLNew.g:
            {
            root_0 = (CommonTree)adaptor.nil();

            set78=(Token)input.LT(1);
            if ( (input.LA(1)>=NODE_TYPE && input.LA(1)<=REAL_TYPE) ) {
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
    // GCLNew.g:143:1: arg_list : arg ( COMMA arg )* ;
    public final GCLNewParser.arg_list_return arg_list() throws RecognitionException {
        GCLNewParser.arg_list_return retval = new GCLNewParser.arg_list_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token COMMA80=null;
        GCLNewParser.arg_return arg79 = null;

        GCLNewParser.arg_return arg81 = null;


        CommonTree COMMA80_tree=null;

        try {
            // GCLNew.g:144:2: ( arg ( COMMA arg )* )
            // GCLNew.g:144:4: arg ( COMMA arg )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_arg_in_arg_list725);
            arg79=arg();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, arg79.getTree());
            // GCLNew.g:144:8: ( COMMA arg )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==COMMA) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // GCLNew.g:144:9: COMMA arg
            	    {
            	    COMMA80=(Token)match(input,COMMA,FOLLOW_COMMA_in_arg_list728); if (state.failed) return retval;
            	    pushFollow(FOLLOW_arg_in_arg_list731);
            	    arg81=arg();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, arg81.getTree());

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
    // GCLNew.g:147:1: arg : ( OUT ID -> ^( ARG OUT ID ) | ID -> ^( ARG ID ) | DONT_CARE -> ^( ARG DONT_CARE ) | literal -> ^( ARG literal ) );
    public final GCLNewParser.arg_return arg() throws RecognitionException {
        GCLNewParser.arg_return retval = new GCLNewParser.arg_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token OUT82=null;
        Token ID83=null;
        Token ID84=null;
        Token DONT_CARE85=null;
        GCLNewParser.literal_return literal86 = null;


        CommonTree OUT82_tree=null;
        CommonTree ID83_tree=null;
        CommonTree ID84_tree=null;
        CommonTree DONT_CARE85_tree=null;
        RewriteRuleTokenStream stream_DONT_CARE=new RewriteRuleTokenStream(adaptor,"token DONT_CARE");
        RewriteRuleTokenStream stream_OUT=new RewriteRuleTokenStream(adaptor,"token OUT");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_literal=new RewriteRuleSubtreeStream(adaptor,"rule literal");
        try {
            // GCLNew.g:148:2: ( OUT ID -> ^( ARG OUT ID ) | ID -> ^( ARG ID ) | DONT_CARE -> ^( ARG DONT_CARE ) | literal -> ^( ARG literal ) )
            int alt20=4;
            alt20 = dfa20.predict(input);
            switch (alt20) {
                case 1 :
                    // GCLNew.g:148:4: OUT ID
                    {
                    OUT82=(Token)match(input,OUT,FOLLOW_OUT_in_arg744); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_OUT.add(OUT82);

                    ID83=(Token)match(input,ID,FOLLOW_ID_in_arg746); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID83);



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
                    // 148:11: -> ^( ARG OUT ID )
                    {
                        // GCLNew.g:148:14: ^( ARG OUT ID )
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
                    // GCLNew.g:149:4: ID
                    {
                    ID84=(Token)match(input,ID,FOLLOW_ID_in_arg761); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID84);



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
                    // 149:7: -> ^( ARG ID )
                    {
                        // GCLNew.g:149:10: ^( ARG ID )
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
                    // GCLNew.g:150:4: DONT_CARE
                    {
                    DONT_CARE85=(Token)match(input,DONT_CARE,FOLLOW_DONT_CARE_in_arg774); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DONT_CARE.add(DONT_CARE85);



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
                    // 150:14: -> ^( ARG DONT_CARE )
                    {
                        // GCLNew.g:150:17: ^( ARG DONT_CARE )
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
                    // GCLNew.g:151:4: literal
                    {
                    pushFollow(FOLLOW_literal_in_arg787);
                    literal86=literal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_literal.add(literal86.getTree());


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
                    // 151:12: -> ^( ARG literal )
                    {
                        // GCLNew.g:151:15: ^( ARG literal )
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
    // GCLNew.g:154:1: literal : ( TRUE -> BOOL_TYPE TRUE | FALSE -> BOOL_TYPE FALSE | dqText -> STRING_TYPE dqText | integer -> INT_TYPE | real -> REAL_TYPE );
    public final GCLNewParser.literal_return literal() throws RecognitionException {
        GCLNewParser.literal_return retval = new GCLNewParser.literal_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token TRUE87=null;
        Token FALSE88=null;
        GCLNewParser.dqText_return dqText89 = null;

        GCLNewParser.integer_return integer90 = null;

        GCLNewParser.real_return real91 = null;


        CommonTree TRUE87_tree=null;
        CommonTree FALSE88_tree=null;
        RewriteRuleTokenStream stream_FALSE=new RewriteRuleTokenStream(adaptor,"token FALSE");
        RewriteRuleTokenStream stream_TRUE=new RewriteRuleTokenStream(adaptor,"token TRUE");
        RewriteRuleSubtreeStream stream_real=new RewriteRuleSubtreeStream(adaptor,"rule real");
        RewriteRuleSubtreeStream stream_integer=new RewriteRuleSubtreeStream(adaptor,"rule integer");
        RewriteRuleSubtreeStream stream_dqText=new RewriteRuleSubtreeStream(adaptor,"rule dqText");
        try {
            // GCLNew.g:155:2: ( TRUE -> BOOL_TYPE TRUE | FALSE -> BOOL_TYPE FALSE | dqText -> STRING_TYPE dqText | integer -> INT_TYPE | real -> REAL_TYPE )
            int alt21=5;
            alt21 = dfa21.predict(input);
            switch (alt21) {
                case 1 :
                    // GCLNew.g:155:4: TRUE
                    {
                    TRUE87=(Token)match(input,TRUE,FOLLOW_TRUE_in_literal806); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_TRUE.add(TRUE87);



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
                    // 155:9: -> BOOL_TYPE TRUE
                    {
                        adaptor.addChild(root_0, (CommonTree)adaptor.create(BOOL_TYPE, "BOOL_TYPE"));
                        adaptor.addChild(root_0, stream_TRUE.nextNode());

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // GCLNew.g:156:4: FALSE
                    {
                    FALSE88=(Token)match(input,FALSE,FOLLOW_FALSE_in_literal817); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_FALSE.add(FALSE88);



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
                    // 156:10: -> BOOL_TYPE FALSE
                    {
                        adaptor.addChild(root_0, (CommonTree)adaptor.create(BOOL_TYPE, "BOOL_TYPE"));
                        adaptor.addChild(root_0, stream_FALSE.nextNode());

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // GCLNew.g:157:4: dqText
                    {
                    pushFollow(FOLLOW_dqText_in_literal828);
                    dqText89=dqText();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_dqText.add(dqText89.getTree());


                    // AST REWRITE
                    // elements: dqText
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 157:11: -> STRING_TYPE dqText
                    {
                        adaptor.addChild(root_0, (CommonTree)adaptor.create(STRING_TYPE, "STRING_TYPE"));
                        adaptor.addChild(root_0, stream_dqText.nextTree());

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 4 :
                    // GCLNew.g:158:4: integer
                    {
                    pushFollow(FOLLOW_integer_in_literal839);
                    integer90=integer();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_integer.add(integer90.getTree());


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
                    // 158:12: -> INT_TYPE
                    {
                        adaptor.addChild(root_0, (CommonTree)adaptor.create(INT_TYPE, "INT_TYPE"));
                        adaptor.addChild(root_0,  concat((integer90!=null?((CommonTree)integer90.tree):null)) );

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 5 :
                    // GCLNew.g:159:4: real
                    {
                    pushFollow(FOLLOW_real_in_literal850);
                    real91=real();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_real.add(real91.getTree());


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
                    // 159:9: -> REAL_TYPE
                    {
                        adaptor.addChild(root_0, (CommonTree)adaptor.create(REAL_TYPE, "REAL_TYPE"));
                        adaptor.addChild(root_0,  concat((real91!=null?((CommonTree)real91.tree):null)) );

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

    public static class dqText_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "dqText"
    // GCLNew.g:162:1: dqText : QUOTE dqContent QUOTE ->;
    public final GCLNewParser.dqText_return dqText() throws RecognitionException {
        GCLNewParser.dqText_return retval = new GCLNewParser.dqText_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token QUOTE92=null;
        Token QUOTE94=null;
        GCLNewParser.dqContent_return dqContent93 = null;


        CommonTree QUOTE92_tree=null;
        CommonTree QUOTE94_tree=null;
        RewriteRuleTokenStream stream_QUOTE=new RewriteRuleTokenStream(adaptor,"token QUOTE");
        RewriteRuleSubtreeStream stream_dqContent=new RewriteRuleSubtreeStream(adaptor,"rule dqContent");
        try {
            // GCLNew.g:163:4: ( QUOTE dqContent QUOTE ->)
            // GCLNew.g:163:6: QUOTE dqContent QUOTE
            {
            QUOTE92=(Token)match(input,QUOTE,FOLLOW_QUOTE_in_dqText869); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_QUOTE.add(QUOTE92);

            pushFollow(FOLLOW_dqContent_in_dqText871);
            dqContent93=dqContent();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_dqContent.add(dqContent93.getTree());
            QUOTE94=(Token)match(input,QUOTE,FOLLOW_QUOTE_in_dqText873); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_QUOTE.add(QUOTE94);



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
            // 163:28: ->
            {
                adaptor.addChild(root_0,  concat((dqContent93!=null?((CommonTree)dqContent93.tree):null)) );

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
    // $ANTLR end "dqText"

    public static class dqContent_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "dqContent"
    // GCLNew.g:166:1: dqContent : ( dqTextChar )* ;
    public final GCLNewParser.dqContent_return dqContent() throws RecognitionException {
        GCLNewParser.dqContent_return retval = new GCLNewParser.dqContent_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        GCLNewParser.dqTextChar_return dqTextChar95 = null;



        try {
            // GCLNew.g:167:4: ( ( dqTextChar )* )
            // GCLNew.g:167:6: ( dqTextChar )*
            {
            root_0 = (CommonTree)adaptor.nil();

            // GCLNew.g:167:6: ( dqTextChar )*
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( ((LA22_0>=PROGRAM && LA22_0<=FALSE)||(LA22_0>=BSLASH && LA22_0<=WS)) ) {
                    alt22=1;
                }


                switch (alt22) {
            	case 1 :
            	    // GCLNew.g:167:6: dqTextChar
            	    {
            	    pushFollow(FOLLOW_dqTextChar_in_dqContent892);
            	    dqTextChar95=dqTextChar();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, dqTextChar95.getTree());

            	    }
            	    break;

            	default :
            	    break loop22;
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
    // $ANTLR end "dqContent"

    public static class dqTextChar_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "dqTextChar"
    // GCLNew.g:170:1: dqTextChar : (~ ( QUOTE | BSLASH ) | BSLASH ( BSLASH | QUOTE ) );
    public final GCLNewParser.dqTextChar_return dqTextChar() throws RecognitionException {
        GCLNewParser.dqTextChar_return retval = new GCLNewParser.dqTextChar_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token set96=null;
        Token BSLASH97=null;
        Token set98=null;

        CommonTree set96_tree=null;
        CommonTree BSLASH97_tree=null;
        CommonTree set98_tree=null;

        try {
            // GCLNew.g:171:4: (~ ( QUOTE | BSLASH ) | BSLASH ( BSLASH | QUOTE ) )
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( ((LA23_0>=PROGRAM && LA23_0<=FALSE)||(LA23_0>=MINUS && LA23_0<=WS)) ) {
                alt23=1;
            }
            else if ( (LA23_0==BSLASH) ) {
                alt23=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 23, 0, input);

                throw nvae;
            }
            switch (alt23) {
                case 1 :
                    // GCLNew.g:171:6: ~ ( QUOTE | BSLASH )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    set96=(Token)input.LT(1);
                    if ( (input.LA(1)>=PROGRAM && input.LA(1)<=FALSE)||(input.LA(1)>=MINUS && input.LA(1)<=WS) ) {
                        input.consume();
                        if ( state.backtracking==0 ) adaptor.addChild(root_0, (CommonTree)adaptor.create(set96));
                        state.errorRecovery=false;state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    }
                    break;
                case 2 :
                    // GCLNew.g:172:6: BSLASH ( BSLASH | QUOTE )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    BSLASH97=(Token)match(input,BSLASH,FOLLOW_BSLASH_in_dqTextChar920); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    BSLASH97_tree = (CommonTree)adaptor.create(BSLASH97);
                    adaptor.addChild(root_0, BSLASH97_tree);
                    }
                    set98=(Token)input.LT(1);
                    if ( (input.LA(1)>=QUOTE && input.LA(1)<=BSLASH) ) {
                        input.consume();
                        if ( state.backtracking==0 ) adaptor.addChild(root_0, (CommonTree)adaptor.create(set98));
                        state.errorRecovery=false;state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
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
        }
        return retval;
    }
    // $ANTLR end "dqTextChar"

    public static class real_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "real"
    // GCLNew.g:175:1: real : ( MINUS )? ( NUMBER )? DOT ( NUMBER )? ;
    public final GCLNewParser.real_return real() throws RecognitionException {
        GCLNewParser.real_return retval = new GCLNewParser.real_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token MINUS99=null;
        Token NUMBER100=null;
        Token DOT101=null;
        Token NUMBER102=null;

        CommonTree MINUS99_tree=null;
        CommonTree NUMBER100_tree=null;
        CommonTree DOT101_tree=null;
        CommonTree NUMBER102_tree=null;

        try {
            // GCLNew.g:176:2: ( ( MINUS )? ( NUMBER )? DOT ( NUMBER )? )
            // GCLNew.g:176:4: ( MINUS )? ( NUMBER )? DOT ( NUMBER )?
            {
            root_0 = (CommonTree)adaptor.nil();

            // GCLNew.g:176:4: ( MINUS )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==MINUS) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // GCLNew.g:176:4: MINUS
                    {
                    MINUS99=(Token)match(input,MINUS,FOLLOW_MINUS_in_real939); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    MINUS99_tree = (CommonTree)adaptor.create(MINUS99);
                    adaptor.addChild(root_0, MINUS99_tree);
                    }

                    }
                    break;

            }

            // GCLNew.g:176:11: ( NUMBER )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==NUMBER) ) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // GCLNew.g:176:11: NUMBER
                    {
                    NUMBER100=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_real942); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    NUMBER100_tree = (CommonTree)adaptor.create(NUMBER100);
                    adaptor.addChild(root_0, NUMBER100_tree);
                    }

                    }
                    break;

            }

            DOT101=(Token)match(input,DOT,FOLLOW_DOT_in_real945); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            DOT101_tree = (CommonTree)adaptor.create(DOT101);
            adaptor.addChild(root_0, DOT101_tree);
            }
            // GCLNew.g:176:23: ( NUMBER )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==NUMBER) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // GCLNew.g:176:23: NUMBER
                    {
                    NUMBER102=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_real947); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    NUMBER102_tree = (CommonTree)adaptor.create(NUMBER102);
                    adaptor.addChild(root_0, NUMBER102_tree);
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
        }
        return retval;
    }
    // $ANTLR end "real"

    public static class integer_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "integer"
    // GCLNew.g:178:1: integer : ( MINUS )? NUMBER ;
    public final GCLNewParser.integer_return integer() throws RecognitionException {
        GCLNewParser.integer_return retval = new GCLNewParser.integer_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token MINUS103=null;
        Token NUMBER104=null;

        CommonTree MINUS103_tree=null;
        CommonTree NUMBER104_tree=null;

        try {
            // GCLNew.g:179:2: ( ( MINUS )? NUMBER )
            // GCLNew.g:179:4: ( MINUS )? NUMBER
            {
            root_0 = (CommonTree)adaptor.nil();

            // GCLNew.g:179:4: ( MINUS )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==MINUS) ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // GCLNew.g:179:4: MINUS
                    {
                    MINUS103=(Token)match(input,MINUS,FOLLOW_MINUS_in_integer958); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    MINUS103_tree = (CommonTree)adaptor.create(MINUS103);
                    adaptor.addChild(root_0, MINUS103_tree);
                    }

                    }
                    break;

            }

            NUMBER104=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_integer961); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            NUMBER104_tree = (CommonTree)adaptor.create(NUMBER104);
            adaptor.addChild(root_0, NUMBER104_tree);
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
    // $ANTLR end "integer"

    // $ANTLR start synpred1_GCLNew
    public final void synpred1_GCLNew_fragment() throws RecognitionException {   
        // GCLNew.g:82:33: ( ELSE )
        // GCLNew.g:82:34: ELSE
        {
        match(input,ELSE,FOLLOW_ELSE_in_synpred1_GCLNew286); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_GCLNew

    // $ANTLR start synpred2_GCLNew
    public final void synpred2_GCLNew_fragment() throws RecognitionException {   
        // GCLNew.g:83:17: ( ELSE )
        // GCLNew.g:83:18: ELSE
        {
        match(input,ELSE,FOLLOW_ELSE_in_synpred2_GCLNew311); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred2_GCLNew

    // $ANTLR start synpred3_GCLNew
    public final void synpred3_GCLNew_fragment() throws RecognitionException {   
        // GCLNew.g:84:19: ( CH_OR )
        // GCLNew.g:84:20: CH_OR
        {
        match(input,CH_OR,FOLLOW_CH_OR_in_synpred3_GCLNew335); if (state.failed) return ;

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
    protected DFA20 dfa20 = new DFA20(this);
    protected DFA21 dfa21 = new DFA21(this);
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
        "\13\uffff";
    static final String DFA15_eofS =
        "\13\uffff";
    static final String DFA15_minS =
        "\1\16\12\uffff";
    static final String DFA15_maxS =
        "\1\57\12\uffff";
    static final String DFA15_acceptS =
        "\1\uffff\1\1\10\uffff\1\2";
    static final String DFA15_specialS =
        "\13\uffff}>";
    static final String[] DFA15_transitionS = {
            "\1\1\1\uffff\1\12\13\uffff\1\1\5\uffff\1\1\6\uffff\4\1\1\uffff"+
            "\2\1",
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
    static final String DFA20_eotS =
        "\12\uffff";
    static final String DFA20_eofS =
        "\12\uffff";
    static final String DFA20_minS =
        "\1\16\11\uffff";
    static final String DFA20_maxS =
        "\1\57\11\uffff";
    static final String DFA20_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\5\uffff";
    static final String DFA20_specialS =
        "\12\uffff}>";
    static final String[] DFA20_transitionS = {
            "\1\2\15\uffff\1\4\5\uffff\1\4\6\uffff\1\1\1\3\2\4\1\uffff\2"+
            "\4",
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

    static final short[] DFA20_eot = DFA.unpackEncodedString(DFA20_eotS);
    static final short[] DFA20_eof = DFA.unpackEncodedString(DFA20_eofS);
    static final char[] DFA20_min = DFA.unpackEncodedStringToUnsignedChars(DFA20_minS);
    static final char[] DFA20_max = DFA.unpackEncodedStringToUnsignedChars(DFA20_maxS);
    static final short[] DFA20_accept = DFA.unpackEncodedString(DFA20_acceptS);
    static final short[] DFA20_special = DFA.unpackEncodedString(DFA20_specialS);
    static final short[][] DFA20_transition;

    static {
        int numStates = DFA20_transitionS.length;
        DFA20_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA20_transition[i] = DFA.unpackEncodedString(DFA20_transitionS[i]);
        }
    }

    class DFA20 extends DFA {

        public DFA20(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 20;
            this.eot = DFA20_eot;
            this.eof = DFA20_eof;
            this.min = DFA20_min;
            this.max = DFA20_max;
            this.accept = DFA20_accept;
            this.special = DFA20_special;
            this.transition = DFA20_transition;
        }
        public String getDescription() {
            return "147:1: arg : ( OUT ID -> ^( ARG OUT ID ) | ID -> ^( ARG ID ) | DONT_CARE -> ^( ARG DONT_CARE ) | literal -> ^( ARG literal ) );";
        }
    }
    static final String DFA21_eotS =
        "\17\uffff";
    static final String DFA21_eofS =
        "\17\uffff";
    static final String DFA21_minS =
        "\1\34\3\uffff\1\42\1\20\1\uffff\1\20\7\uffff";
    static final String DFA21_maxS =
        "\1\57\3\uffff\1\57\1\43\1\uffff\1\43\7\uffff";
    static final String DFA21_acceptS =
        "\1\uffff\1\1\1\2\1\3\2\uffff\1\5\3\uffff\1\4\4\uffff";
    static final String DFA21_specialS =
        "\17\uffff}>";
    static final String[] DFA21_transitionS = {
            "\1\1\5\uffff\1\6\10\uffff\1\2\1\3\1\uffff\1\4\1\5",
            "",
            "",
            "",
            "\1\6\14\uffff\1\7",
            "\1\12\21\uffff\1\6\1\12",
            "",
            "\1\12\21\uffff\1\6\1\12",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA21_eot = DFA.unpackEncodedString(DFA21_eotS);
    static final short[] DFA21_eof = DFA.unpackEncodedString(DFA21_eofS);
    static final char[] DFA21_min = DFA.unpackEncodedStringToUnsignedChars(DFA21_minS);
    static final char[] DFA21_max = DFA.unpackEncodedStringToUnsignedChars(DFA21_maxS);
    static final short[] DFA21_accept = DFA.unpackEncodedString(DFA21_acceptS);
    static final short[] DFA21_special = DFA.unpackEncodedString(DFA21_specialS);
    static final short[][] DFA21_transition;

    static {
        int numStates = DFA21_transitionS.length;
        DFA21_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA21_transition[i] = DFA.unpackEncodedString(DFA21_transitionS[i]);
        }
    }

    class DFA21 extends DFA {

        public DFA21(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 21;
            this.eot = DFA21_eot;
            this.eof = DFA21_eof;
            this.min = DFA21_min;
            this.max = DFA21_max;
            this.accept = DFA21_accept;
            this.special = DFA21_special;
            this.transition = DFA21_transition;
        }
        public String getDescription() {
            return "154:1: literal : ( TRUE -> BOOL_TYPE TRUE | FALSE -> BOOL_TYPE FALSE | dqText -> STRING_TYPE dqText | integer -> INT_TYPE | real -> REAL_TYPE );";
        }
    }
 

    public static final BitSet FOLLOW_function_in_program99 = new BitSet(new long[]{0x000001F381BED082L});
    public static final BitSet FOLLOW_stat_in_program101 = new BitSet(new long[]{0x000001F381BED082L});
    public static final BitSet FOLLOW_LCURLY_in_block139 = new BitSet(new long[]{0x000001F381BEF080L});
    public static final BitSet FOLLOW_stat_in_block141 = new BitSet(new long[]{0x000001F381BEF080L});
    public static final BitSet FOLLOW_RCURLY_in_block144 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_function163 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_ID_in_function166 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_LPAR_in_function168 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_RPAR_in_function171 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_block_in_function174 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_stat186 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALAP_in_stat191 = new BitSet(new long[]{0x000001F381BED080L});
    public static final BitSet FOLLOW_stat_in_stat193 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHILE_in_stat206 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_LPAR_in_stat209 = new BitSet(new long[]{0x000000031000C000L});
    public static final BitSet FOLLOW_cond_in_stat212 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_RPAR_in_stat214 = new BitSet(new long[]{0x000001F381BED080L});
    public static final BitSet FOLLOW_stat_in_stat217 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNTIL_in_stat222 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_LPAR_in_stat225 = new BitSet(new long[]{0x000000031000C000L});
    public static final BitSet FOLLOW_cond_in_stat228 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_RPAR_in_stat230 = new BitSet(new long[]{0x000001F381BED080L});
    public static final BitSet FOLLOW_stat_in_stat233 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DO_in_stat238 = new BitSet(new long[]{0x000001F381BED080L});
    public static final BitSet FOLLOW_stat_in_stat240 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_WHILE_in_stat242 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_LPAR_in_stat244 = new BitSet(new long[]{0x000000031000C000L});
    public static final BitSet FOLLOW_cond_in_stat246 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_RPAR_in_stat248 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IF_in_stat270 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_LPAR_in_stat273 = new BitSet(new long[]{0x000000031000C000L});
    public static final BitSet FOLLOW_cond_in_stat276 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_RPAR_in_stat278 = new BitSet(new long[]{0x000001F381FED080L});
    public static final BitSet FOLLOW_stat_in_stat281 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_ELSE_in_stat291 = new BitSet(new long[]{0x000001F381BED080L});
    public static final BitSet FOLLOW_stat_in_stat294 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRY_in_stat303 = new BitSet(new long[]{0x000001F381FED080L});
    public static final BitSet FOLLOW_stat_in_stat306 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_ELSE_in_stat316 = new BitSet(new long[]{0x000001F381BED080L});
    public static final BitSet FOLLOW_stat_in_stat319 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CHOICE_in_stat328 = new BitSet(new long[]{0x000001F383BED080L});
    public static final BitSet FOLLOW_stat_in_stat331 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_CH_OR_in_stat340 = new BitSet(new long[]{0x000001F383BED080L});
    public static final BitSet FOLLOW_stat_in_stat343 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_expr_in_stat350 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_SEMI_in_stat352 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_decl_in_stat358 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_SEMI_in_stat360 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_cond_atom_in_cond373 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_OR_in_cond382 = new BitSet(new long[]{0x000000031000C000L});
    public static final BitSet FOLLOW_cond_atom_in_cond384 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_TRUE_in_cond_atom422 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_cond_atom426 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr2_in_expr437 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_OR_in_expr445 = new BitSet(new long[]{0x000000038000C000L});
    public static final BitSet FOLLOW_expr2_in_expr447 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_expr_atom_in_expr2488 = new BitSet(new long[]{0x0000000060000002L});
    public static final BitSet FOLLOW_PLUS_in_expr2496 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STAR_in_expr2520 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SHARP_in_expr2552 = new BitSet(new long[]{0x000000030000C000L});
    public static final BitSet FOLLOW_expr_atom_in_expr2554 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_expr_atom574 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_expr_atom579 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_expr_atom584 = new BitSet(new long[]{0x000000038000C000L});
    public static final BitSet FOLLOW_expr_in_expr_atom587 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_RPAR_in_expr_atom589 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_expr_atom595 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_name_in_call607 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_LPAR_in_call610 = new BitSet(new long[]{0x0000DE0410014000L});
    public static final BitSet FOLLOW_arg_list_in_call612 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_RPAR_in_call615 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_rule_name643 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_DOT_in_rule_name646 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_ID_in_rule_name648 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_var_type_in_var_decl662 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_ID_in_var_decl664 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_COMMA_in_var_decl667 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_ID_in_var_decl669 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_set_in_var_type0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arg_in_arg_list725 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_COMMA_in_arg_list728 = new BitSet(new long[]{0x0000DE0410004000L});
    public static final BitSet FOLLOW_arg_in_arg_list731 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_OUT_in_arg744 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_ID_in_arg746 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_arg761 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DONT_CARE_in_arg774 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_arg787 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_literal806 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FALSE_in_literal817 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dqText_in_literal828 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_integer_in_literal839 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_real_in_literal850 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUOTE_in_dqText869 = new BitSet(new long[]{0x001FFFFFFFFFFFF0L});
    public static final BitSet FOLLOW_dqContent_in_dqText871 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_QUOTE_in_dqText873 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dqTextChar_in_dqContent892 = new BitSet(new long[]{0x001FEFFFFFFFFFF2L});
    public static final BitSet FOLLOW_set_in_dqTextChar908 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BSLASH_in_dqTextChar920 = new BitSet(new long[]{0x0000300000000000L});
    public static final BitSet FOLLOW_set_in_dqTextChar922 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_real939 = new BitSet(new long[]{0x0000800400000000L});
    public static final BitSet FOLLOW_NUMBER_in_real942 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_DOT_in_real945 = new BitSet(new long[]{0x0000800000000002L});
    public static final BitSet FOLLOW_NUMBER_in_real947 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_integer958 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_NUMBER_in_integer961 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ELSE_in_synpred1_GCLNew286 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ELSE_in_synpred2_GCLNew311 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CH_OR_in_synpred3_GCLNew335 = new BitSet(new long[]{0x0000000000000002L});

}