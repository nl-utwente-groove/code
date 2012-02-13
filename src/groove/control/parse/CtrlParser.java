// $ANTLR 3.4 E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g 2012-02-13 21:37:31

package groove.control.parse;
import groove.control.*;
import groove.control.CtrlCall.Kind;
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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ALAP", "AMP", "ANY", "ARG", "ARGS", "ASTERISK", "BAR", "BLOCK", "BOOL", "BSLASH", "CALL", "CHOICE", "COMMA", "DO", "DONT_CARE", "DOT", "DO_UNTIL", "DO_WHILE", "ELSE", "EscapeSequence", "FALSE", "FUNCTION", "FUNCTIONS", "ID", "IF", "INT", "INT_LIT", "IntegerNumber", "LCURLY", "LPAR", "MINUS", "ML_COMMENT", "NODE", "NOT", "NonIntegerNumber", "OR", "OTHER", "OUT", "PLUS", "PRIORITY", "PROGRAM", "QUOTE", "RCURLY", "REAL", "REAL_LIT", "RECIPE", "RECIPES", "RPAR", "SEMI", "SHARP", "SL_COMMENT", "STAR", "STRING", "STRING_LIT", "TRUE", "TRY", "UNTIL", "VAR", "WHILE", "WS"
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
    public static final int PRIORITY=43;
    public static final int PROGRAM=44;
    public static final int QUOTE=45;
    public static final int RCURLY=46;
    public static final int REAL=47;
    public static final int REAL_LIT=48;
    public static final int RECIPE=49;
    public static final int RECIPES=50;
    public static final int RPAR=51;
    public static final int SEMI=52;
    public static final int SHARP=53;
    public static final int SL_COMMENT=54;
    public static final int STAR=55;
    public static final int STRING=56;
    public static final int STRING_LIT=57;
    public static final int TRUE=58;
    public static final int TRY=59;
    public static final int UNTIL=60;
    public static final int VAR=61;
    public static final int WHILE=62;
    public static final int WS=63;

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
    public String getGrammarFileName() { return "E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g"; }


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
            lexer.setHelper(this.helper);
            setTokenStream(new CommonTokenStream(lexer));
            setTreeAdaptor(new MyTreeAdaptor());
            return (MyTree) program().getTree();
        }


    public static class program_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "program"
    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:106:1: program : ( function | recipe | stat )* EOF -> ^( PROGRAM ^( RECIPES ( recipe )* ) ^( FUNCTIONS ( function )* ) ^( BLOCK ( stat )* ) ) ;
    public final CtrlParser.program_return program() throws RecognitionException {
        CtrlParser.program_return retval = new CtrlParser.program_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token EOF4=null;
        CtrlParser.function_return function1 =null;

        CtrlParser.recipe_return recipe2 =null;

        CtrlParser.stat_return stat3 =null;


        CommonTree EOF4_tree=null;
        RewriteRuleTokenStream stream_EOF=new RewriteRuleTokenStream(adaptor,"token EOF");
        RewriteRuleSubtreeStream stream_recipe=new RewriteRuleSubtreeStream(adaptor,"rule recipe");
        RewriteRuleSubtreeStream stream_stat=new RewriteRuleSubtreeStream(adaptor,"rule stat");
        RewriteRuleSubtreeStream stream_function=new RewriteRuleSubtreeStream(adaptor,"rule function");
        try {
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:107:3: ( ( function | recipe | stat )* EOF -> ^( PROGRAM ^( RECIPES ( recipe )* ) ^( FUNCTIONS ( function )* ) ^( BLOCK ( stat )* ) ) )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:110:5: ( function | recipe | stat )* EOF
            {
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:110:5: ( function | recipe | stat )*
            loop1:
            do {
                int alt1=4;
                switch ( input.LA(1) ) {
                case FUNCTION:
                    {
                    alt1=1;
                    }
                    break;
                case RECIPE:
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
            	    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:110:6: function
            	    {
            	    pushFollow(FOLLOW_function_in_program137);
            	    function1=function();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_function.add(function1.getTree());

            	    }
            	    break;
            	case 2 :
            	    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:110:15: recipe
            	    {
            	    pushFollow(FOLLOW_recipe_in_program139);
            	    recipe2=recipe();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_recipe.add(recipe2.getTree());

            	    }
            	    break;
            	case 3 :
            	    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:110:22: stat
            	    {
            	    pushFollow(FOLLOW_stat_in_program141);
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


            EOF4=(Token)match(input,EOF,FOLLOW_EOF_in_program145); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_EOF.add(EOF4);


            // AST REWRITE
            // elements: function, recipe, stat
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 111:5: -> ^( PROGRAM ^( RECIPES ( recipe )* ) ^( FUNCTIONS ( function )* ) ^( BLOCK ( stat )* ) )
            {
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:111:8: ^( PROGRAM ^( RECIPES ( recipe )* ) ^( FUNCTIONS ( function )* ) ^( BLOCK ( stat )* ) )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(PROGRAM, "PROGRAM")
                , root_1);

                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:111:18: ^( RECIPES ( recipe )* )
                {
                CommonTree root_2 = (CommonTree)adaptor.nil();
                root_2 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(RECIPES, "RECIPES")
                , root_2);

                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:111:28: ( recipe )*
                while ( stream_recipe.hasNext() ) {
                    adaptor.addChild(root_2, stream_recipe.nextTree());

                }
                stream_recipe.reset();

                adaptor.addChild(root_1, root_2);
                }

                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:111:37: ^( FUNCTIONS ( function )* )
                {
                CommonTree root_2 = (CommonTree)adaptor.nil();
                root_2 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(FUNCTIONS, "FUNCTIONS")
                , root_2);

                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:111:49: ( function )*
                while ( stream_function.hasNext() ) {
                    adaptor.addChild(root_2, stream_function.nextTree());

                }
                stream_function.reset();

                adaptor.addChild(root_1, root_2);
                }

                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:111:60: ^( BLOCK ( stat )* )
                {
                CommonTree root_2 = (CommonTree)adaptor.nil();
                root_2 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(BLOCK, "BLOCK")
                , root_2);

                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:111:68: ( stat )*
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


    public static class recipe_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "recipe"
    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:117:1: recipe : RECIPE ^ ID LPAR ! RPAR ! ( PRIORITY ! INT_LIT )? block ;
    public final CtrlParser.recipe_return recipe() throws RecognitionException {
        CtrlParser.recipe_return retval = new CtrlParser.recipe_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token RECIPE5=null;
        Token ID6=null;
        Token LPAR7=null;
        Token RPAR8=null;
        Token PRIORITY9=null;
        Token INT_LIT10=null;
        CtrlParser.block_return block11 =null;


        CommonTree RECIPE5_tree=null;
        CommonTree ID6_tree=null;
        CommonTree LPAR7_tree=null;
        CommonTree RPAR8_tree=null;
        CommonTree PRIORITY9_tree=null;
        CommonTree INT_LIT10_tree=null;

        try {
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:118:3: ( RECIPE ^ ID LPAR ! RPAR ! ( PRIORITY ! INT_LIT )? block )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:120:5: RECIPE ^ ID LPAR ! RPAR ! ( PRIORITY ! INT_LIT )? block
            {
            root_0 = (CommonTree)adaptor.nil();


            if ( state.backtracking==0 ) { lexer.startRecord(); }

            RECIPE5=(Token)match(input,RECIPE,FOLLOW_RECIPE_in_recipe207); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            RECIPE5_tree = 
            (CommonTree)adaptor.create(RECIPE5)
            ;
            root_0 = (CommonTree)adaptor.becomeRoot(RECIPE5_tree, root_0);
            }

            ID6=(Token)match(input,ID,FOLLOW_ID_in_recipe210); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID6_tree = 
            (CommonTree)adaptor.create(ID6)
            ;
            adaptor.addChild(root_0, ID6_tree);
            }

            LPAR7=(Token)match(input,LPAR,FOLLOW_LPAR_in_recipe212); if (state.failed) return retval;

            RPAR8=(Token)match(input,RPAR,FOLLOW_RPAR_in_recipe215); if (state.failed) return retval;

            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:121:28: ( PRIORITY ! INT_LIT )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==PRIORITY) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:121:29: PRIORITY ! INT_LIT
                    {
                    PRIORITY9=(Token)match(input,PRIORITY,FOLLOW_PRIORITY_in_recipe219); if (state.failed) return retval;

                    INT_LIT10=(Token)match(input,INT_LIT,FOLLOW_INT_LIT_in_recipe222); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    INT_LIT10_tree = 
                    (CommonTree)adaptor.create(INT_LIT10)
                    ;
                    adaptor.addChild(root_0, INT_LIT10_tree);
                    }

                    }
                    break;

            }


            pushFollow(FOLLOW_block_in_recipe226);
            block11=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, block11.getTree());

            if ( state.backtracking==0 ) { helper.declareName(RECIPE5_tree, lexer.getRecord()); }

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
    // $ANTLR end "recipe"


    public static class function_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "function"
    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:129:1: function : FUNCTION ^ ID LPAR ! RPAR ! block ;
    public final CtrlParser.function_return function() throws RecognitionException {
        CtrlParser.function_return retval = new CtrlParser.function_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token FUNCTION12=null;
        Token ID13=null;
        Token LPAR14=null;
        Token RPAR15=null;
        CtrlParser.block_return block16 =null;


        CommonTree FUNCTION12_tree=null;
        CommonTree ID13_tree=null;
        CommonTree LPAR14_tree=null;
        CommonTree RPAR15_tree=null;

        try {
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:130:3: ( FUNCTION ^ ID LPAR ! RPAR ! block )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:132:5: FUNCTION ^ ID LPAR ! RPAR ! block
            {
            root_0 = (CommonTree)adaptor.nil();


            FUNCTION12=(Token)match(input,FUNCTION,FOLLOW_FUNCTION_in_function257); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            FUNCTION12_tree = 
            (CommonTree)adaptor.create(FUNCTION12)
            ;
            root_0 = (CommonTree)adaptor.becomeRoot(FUNCTION12_tree, root_0);
            }

            ID13=(Token)match(input,ID,FOLLOW_ID_in_function260); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID13_tree = 
            (CommonTree)adaptor.create(ID13)
            ;
            adaptor.addChild(root_0, ID13_tree);
            }

            LPAR14=(Token)match(input,LPAR,FOLLOW_LPAR_in_function262); if (state.failed) return retval;

            RPAR15=(Token)match(input,RPAR,FOLLOW_RPAR_in_function265); if (state.failed) return retval;

            pushFollow(FOLLOW_block_in_function268);
            block16=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, block16.getTree());

            if ( state.backtracking==0 ) { helper.declareName(FUNCTION12_tree, null); }

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
    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:137:1: block : LCURLY ( stat )* RCURLY -> ^( BLOCK ( stat )* ) ;
    public final CtrlParser.block_return block() throws RecognitionException {
        CtrlParser.block_return retval = new CtrlParser.block_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token LCURLY17=null;
        Token RCURLY19=null;
        CtrlParser.stat_return stat18 =null;


        CommonTree LCURLY17_tree=null;
        CommonTree RCURLY19_tree=null;
        RewriteRuleTokenStream stream_LCURLY=new RewriteRuleTokenStream(adaptor,"token LCURLY");
        RewriteRuleTokenStream stream_RCURLY=new RewriteRuleTokenStream(adaptor,"token RCURLY");
        RewriteRuleSubtreeStream stream_stat=new RewriteRuleSubtreeStream(adaptor,"rule stat");
        try {
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:138:3: ( LCURLY ( stat )* RCURLY -> ^( BLOCK ( stat )* ) )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:140:5: LCURLY ( stat )* RCURLY
            {
            LCURLY17=(Token)match(input,LCURLY,FOLLOW_LCURLY_in_block299); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LCURLY.add(LCURLY17);


            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:140:12: ( stat )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==ALAP||LA3_0==ANY||LA3_0==BOOL||LA3_0==CHOICE||LA3_0==DO||(LA3_0 >= ID && LA3_0 <= INT)||(LA3_0 >= LCURLY && LA3_0 <= LPAR)||LA3_0==NODE||LA3_0==OTHER||LA3_0==REAL||LA3_0==SHARP||LA3_0==STRING||(LA3_0 >= TRY && LA3_0 <= UNTIL)||LA3_0==WHILE) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:140:12: stat
            	    {
            	    pushFollow(FOLLOW_stat_in_block301);
            	    stat18=stat();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_stat.add(stat18.getTree());

            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);


            RCURLY19=(Token)match(input,RCURLY,FOLLOW_RCURLY_in_block304); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RCURLY.add(RCURLY19);


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
            // 140:25: -> ^( BLOCK ( stat )* )
            {
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:140:28: ^( BLOCK ( stat )* )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(BLOCK, "BLOCK")
                , root_1);

                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:140:36: ( stat )*
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
    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:143:1: stat : ( block | ALAP ^ stat | WHILE ^ LPAR ! cond RPAR ! stat | UNTIL ^ LPAR ! cond RPAR ! stat | DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) ) | IF ^ LPAR ! cond RPAR ! stat ( ( ELSE )=> ELSE ! stat )? | TRY ^ stat ( ( ELSE )=> ELSE ! stat )? | CHOICE ^ stat ( ( OR )=> OR ! stat )+ | expr SEMI !| var_decl SEMI !);
    public final CtrlParser.stat_return stat() throws RecognitionException {
        CtrlParser.stat_return retval = new CtrlParser.stat_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token ALAP21=null;
        Token WHILE23=null;
        Token LPAR24=null;
        Token RPAR26=null;
        Token UNTIL28=null;
        Token LPAR29=null;
        Token RPAR31=null;
        Token DO33=null;
        Token WHILE35=null;
        Token LPAR36=null;
        Token RPAR38=null;
        Token UNTIL39=null;
        Token LPAR40=null;
        Token RPAR42=null;
        Token IF43=null;
        Token LPAR44=null;
        Token RPAR46=null;
        Token ELSE48=null;
        Token TRY50=null;
        Token ELSE52=null;
        Token CHOICE54=null;
        Token OR56=null;
        Token SEMI59=null;
        Token SEMI61=null;
        CtrlParser.block_return block20 =null;

        CtrlParser.stat_return stat22 =null;

        CtrlParser.cond_return cond25 =null;

        CtrlParser.stat_return stat27 =null;

        CtrlParser.cond_return cond30 =null;

        CtrlParser.stat_return stat32 =null;

        CtrlParser.stat_return stat34 =null;

        CtrlParser.cond_return cond37 =null;

        CtrlParser.cond_return cond41 =null;

        CtrlParser.cond_return cond45 =null;

        CtrlParser.stat_return stat47 =null;

        CtrlParser.stat_return stat49 =null;

        CtrlParser.stat_return stat51 =null;

        CtrlParser.stat_return stat53 =null;

        CtrlParser.stat_return stat55 =null;

        CtrlParser.stat_return stat57 =null;

        CtrlParser.expr_return expr58 =null;

        CtrlParser.var_decl_return var_decl60 =null;


        CommonTree ALAP21_tree=null;
        CommonTree WHILE23_tree=null;
        CommonTree LPAR24_tree=null;
        CommonTree RPAR26_tree=null;
        CommonTree UNTIL28_tree=null;
        CommonTree LPAR29_tree=null;
        CommonTree RPAR31_tree=null;
        CommonTree DO33_tree=null;
        CommonTree WHILE35_tree=null;
        CommonTree LPAR36_tree=null;
        CommonTree RPAR38_tree=null;
        CommonTree UNTIL39_tree=null;
        CommonTree LPAR40_tree=null;
        CommonTree RPAR42_tree=null;
        CommonTree IF43_tree=null;
        CommonTree LPAR44_tree=null;
        CommonTree RPAR46_tree=null;
        CommonTree ELSE48_tree=null;
        CommonTree TRY50_tree=null;
        CommonTree ELSE52_tree=null;
        CommonTree CHOICE54_tree=null;
        CommonTree OR56_tree=null;
        CommonTree SEMI59_tree=null;
        CommonTree SEMI61_tree=null;
        RewriteRuleTokenStream stream_DO=new RewriteRuleTokenStream(adaptor,"token DO");
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleTokenStream stream_WHILE=new RewriteRuleTokenStream(adaptor,"token WHILE");
        RewriteRuleTokenStream stream_UNTIL=new RewriteRuleTokenStream(adaptor,"token UNTIL");
        RewriteRuleSubtreeStream stream_cond=new RewriteRuleSubtreeStream(adaptor,"rule cond");
        RewriteRuleSubtreeStream stream_stat=new RewriteRuleSubtreeStream(adaptor,"rule stat");
        try {
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:144:2: ( block | ALAP ^ stat | WHILE ^ LPAR ! cond RPAR ! stat | UNTIL ^ LPAR ! cond RPAR ! stat | DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) ) | IF ^ LPAR ! cond RPAR ! stat ( ( ELSE )=> ELSE ! stat )? | TRY ^ stat ( ( ELSE )=> ELSE ! stat )? | CHOICE ^ stat ( ( OR )=> OR ! stat )+ | expr SEMI !| var_decl SEMI !)
            int alt8=10;
            switch ( input.LA(1) ) {
            case LCURLY:
                {
                alt8=1;
                }
                break;
            case ALAP:
                {
                alt8=2;
                }
                break;
            case WHILE:
                {
                alt8=3;
                }
                break;
            case UNTIL:
                {
                alt8=4;
                }
                break;
            case DO:
                {
                alt8=5;
                }
                break;
            case IF:
                {
                alt8=6;
                }
                break;
            case TRY:
                {
                alt8=7;
                }
                break;
            case CHOICE:
                {
                alt8=8;
                }
                break;
            case ANY:
            case ID:
            case LPAR:
            case OTHER:
            case SHARP:
                {
                alt8=9;
                }
                break;
            case BOOL:
            case INT:
            case NODE:
            case REAL:
            case STRING:
                {
                alt8=10;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;

            }

            switch (alt8) {
                case 1 :
                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:145:4: block
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_block_in_stat328);
                    block20=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block20.getTree());

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:149:4: ALAP ^ stat
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    ALAP21=(Token)match(input,ALAP,FOLLOW_ALAP_in_stat345); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ALAP21_tree = 
                    (CommonTree)adaptor.create(ALAP21)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(ALAP21_tree, root_0);
                    }

                    pushFollow(FOLLOW_stat_in_stat348);
                    stat22=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat22.getTree());

                    }
                    break;
                case 3 :
                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:154:4: WHILE ^ LPAR ! cond RPAR ! stat
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    WHILE23=(Token)match(input,WHILE,FOLLOW_WHILE_in_stat369); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    WHILE23_tree = 
                    (CommonTree)adaptor.create(WHILE23)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(WHILE23_tree, root_0);
                    }

                    LPAR24=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat372); if (state.failed) return retval;

                    pushFollow(FOLLOW_cond_in_stat375);
                    cond25=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond25.getTree());

                    RPAR26=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat377); if (state.failed) return retval;

                    pushFollow(FOLLOW_stat_in_stat380);
                    stat27=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat27.getTree());

                    }
                    break;
                case 4 :
                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:158:5: UNTIL ^ LPAR ! cond RPAR ! stat
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    UNTIL28=(Token)match(input,UNTIL,FOLLOW_UNTIL_in_stat400); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    UNTIL28_tree = 
                    (CommonTree)adaptor.create(UNTIL28)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(UNTIL28_tree, root_0);
                    }

                    LPAR29=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat403); if (state.failed) return retval;

                    pushFollow(FOLLOW_cond_in_stat406);
                    cond30=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond30.getTree());

                    RPAR31=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat408); if (state.failed) return retval;

                    pushFollow(FOLLOW_stat_in_stat411);
                    stat32=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat32.getTree());

                    }
                    break;
                case 5 :
                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:159:4: DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) )
                    {
                    DO33=(Token)match(input,DO,FOLLOW_DO_in_stat416); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DO.add(DO33);


                    pushFollow(FOLLOW_stat_in_stat418);
                    stat34=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_stat.add(stat34.getTree());

                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:160:4: ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) )
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==WHILE) ) {
                        alt4=1;
                    }
                    else if ( (LA4_0==UNTIL) ) {
                        alt4=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 4, 0, input);

                        throw nvae;

                    }
                    switch (alt4) {
                        case 1 :
                            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:165:7: WHILE LPAR cond RPAR
                            {
                            WHILE35=(Token)match(input,WHILE,FOLLOW_WHILE_in_stat461); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_WHILE.add(WHILE35);


                            LPAR36=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat463); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_LPAR.add(LPAR36);


                            pushFollow(FOLLOW_cond_in_stat465);
                            cond37=cond();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_cond.add(cond37.getTree());

                            RPAR38=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat467); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_RPAR.add(RPAR38);


                            // AST REWRITE
                            // elements: stat, stat, WHILE, cond
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {

                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (CommonTree)adaptor.nil();
                            // 165:28: -> ^( BLOCK stat ^( WHILE cond stat ) )
                            {
                                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:165:31: ^( BLOCK stat ^( WHILE cond stat ) )
                                {
                                CommonTree root_1 = (CommonTree)adaptor.nil();
                                root_1 = (CommonTree)adaptor.becomeRoot(
                                (CommonTree)adaptor.create(BLOCK, "BLOCK")
                                , root_1);

                                adaptor.addChild(root_1, stream_stat.nextTree());

                                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:165:44: ^( WHILE cond stat )
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
                            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:172:5: UNTIL LPAR cond RPAR
                            {
                            UNTIL39=(Token)match(input,UNTIL,FOLLOW_UNTIL_in_stat530); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_UNTIL.add(UNTIL39);


                            LPAR40=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat532); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_LPAR.add(LPAR40);


                            pushFollow(FOLLOW_cond_in_stat534);
                            cond41=cond();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_cond.add(cond41.getTree());

                            RPAR42=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat536); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_RPAR.add(RPAR42);


                            // AST REWRITE
                            // elements: UNTIL, stat, stat, cond
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {

                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (CommonTree)adaptor.nil();
                            // 172:26: -> ^( BLOCK stat ^( UNTIL cond stat ) )
                            {
                                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:172:29: ^( BLOCK stat ^( UNTIL cond stat ) )
                                {
                                CommonTree root_1 = (CommonTree)adaptor.nil();
                                root_1 = (CommonTree)adaptor.becomeRoot(
                                (CommonTree)adaptor.create(BLOCK, "BLOCK")
                                , root_1);

                                adaptor.addChild(root_1, stream_stat.nextTree());

                                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:172:42: ^( UNTIL cond stat )
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
                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:178:5: IF ^ LPAR ! cond RPAR ! stat ( ( ELSE )=> ELSE ! stat )?
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    IF43=(Token)match(input,IF,FOLLOW_IF_in_stat583); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IF43_tree = 
                    (CommonTree)adaptor.create(IF43)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(IF43_tree, root_0);
                    }

                    LPAR44=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat586); if (state.failed) return retval;

                    pushFollow(FOLLOW_cond_in_stat589);
                    cond45=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond45.getTree());

                    RPAR46=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat591); if (state.failed) return retval;

                    pushFollow(FOLLOW_stat_in_stat594);
                    stat47=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat47.getTree());

                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:178:31: ( ( ELSE )=> ELSE ! stat )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0==ELSE) ) {
                        int LA5_1 = input.LA(2);

                        if ( (synpred1_Ctrl()) ) {
                            alt5=1;
                        }
                    }
                    switch (alt5) {
                        case 1 :
                            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:178:33: ( ELSE )=> ELSE ! stat
                            {
                            ELSE48=(Token)match(input,ELSE,FOLLOW_ELSE_in_stat604); if (state.failed) return retval;

                            pushFollow(FOLLOW_stat_in_stat607);
                            stat49=stat();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, stat49.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 7 :
                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:182:5: TRY ^ stat ( ( ELSE )=> ELSE ! stat )?
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    TRY50=(Token)match(input,TRY,FOLLOW_TRY_in_stat631); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    TRY50_tree = 
                    (CommonTree)adaptor.create(TRY50)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(TRY50_tree, root_0);
                    }

                    pushFollow(FOLLOW_stat_in_stat634);
                    stat51=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat51.getTree());

                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:182:15: ( ( ELSE )=> ELSE ! stat )?
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0==ELSE) ) {
                        int LA6_1 = input.LA(2);

                        if ( (synpred2_Ctrl()) ) {
                            alt6=1;
                        }
                    }
                    switch (alt6) {
                        case 1 :
                            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:182:17: ( ELSE )=> ELSE ! stat
                            {
                            ELSE52=(Token)match(input,ELSE,FOLLOW_ELSE_in_stat644); if (state.failed) return retval;

                            pushFollow(FOLLOW_stat_in_stat647);
                            stat53=stat();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, stat53.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 8 :
                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:185:5: CHOICE ^ stat ( ( OR )=> OR ! stat )+
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    CHOICE54=(Token)match(input,CHOICE,FOLLOW_CHOICE_in_stat666); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CHOICE54_tree = 
                    (CommonTree)adaptor.create(CHOICE54)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(CHOICE54_tree, root_0);
                    }

                    pushFollow(FOLLOW_stat_in_stat669);
                    stat55=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat55.getTree());

                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:185:18: ( ( OR )=> OR ! stat )+
                    int cnt7=0;
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( (LA7_0==OR) ) {
                            int LA7_20 = input.LA(2);

                            if ( (synpred3_Ctrl()) ) {
                                alt7=1;
                            }


                        }


                        switch (alt7) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:185:20: ( OR )=> OR ! stat
                    	    {
                    	    OR56=(Token)match(input,OR,FOLLOW_OR_in_stat679); if (state.failed) return retval;

                    	    pushFollow(FOLLOW_stat_in_stat682);
                    	    stat57=stat();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat57.getTree());

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


                    }
                    break;
                case 9 :
                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:188:4: expr SEMI !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_expr_in_stat697);
                    expr58=expr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expr58.getTree());

                    SEMI59=(Token)match(input,SEMI,FOLLOW_SEMI_in_stat699); if (state.failed) return retval;

                    }
                    break;
                case 10 :
                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:191:4: var_decl SEMI !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_var_decl_in_stat713);
                    var_decl60=var_decl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, var_decl60.getTree());

                    SEMI61=(Token)match(input,SEMI,FOLLOW_SEMI_in_stat715); if (state.failed) return retval;

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
    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:195:1: cond : cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom ) ;
    public final CtrlParser.cond_return cond() throws RecognitionException {
        CtrlParser.cond_return retval = new CtrlParser.cond_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token BAR63=null;
        CtrlParser.cond_atom_return cond_atom62 =null;

        CtrlParser.cond_atom_return cond_atom64 =null;


        CommonTree BAR63_tree=null;
        RewriteRuleTokenStream stream_BAR=new RewriteRuleTokenStream(adaptor,"token BAR");
        RewriteRuleSubtreeStream stream_cond_atom=new RewriteRuleSubtreeStream(adaptor,"rule cond_atom");
        try {
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:196:2: ( cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom ) )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:198:4: cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom )
            {
            pushFollow(FOLLOW_cond_atom_in_cond739);
            cond_atom62=cond_atom();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_cond_atom.add(cond_atom62.getTree());

            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:199:4: ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==BAR) ) {
                alt10=1;
            }
            else if ( (LA10_0==RPAR) ) {
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
                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:199:6: ( BAR cond_atom )+
                    {
                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:199:6: ( BAR cond_atom )+
                    int cnt9=0;
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( (LA9_0==BAR) ) {
                            alt9=1;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:199:7: BAR cond_atom
                    	    {
                    	    BAR63=(Token)match(input,BAR,FOLLOW_BAR_in_cond748); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_BAR.add(BAR63);


                    	    pushFollow(FOLLOW_cond_atom_in_cond750);
                    	    cond_atom64=cond_atom();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_cond_atom.add(cond_atom64.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt9 >= 1 ) break loop9;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(9, input);
                                throw eee;
                        }
                        cnt9++;
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
                    // 199:23: -> ^( CHOICE cond_atom ( cond_atom )+ )
                    {
                        // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:199:26: ^( CHOICE cond_atom ( cond_atom )+ )
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
                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:200:6: 
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
                    // 200:6: -> cond_atom
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
    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:204:1: cond_atom : ( TRUE | call );
    public final CtrlParser.cond_atom_return cond_atom() throws RecognitionException {
        CtrlParser.cond_atom_return retval = new CtrlParser.cond_atom_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token TRUE65=null;
        CtrlParser.call_return call66 =null;


        CommonTree TRUE65_tree=null;

        try {
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:205:2: ( TRUE | call )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==TRUE) ) {
                alt11=1;
            }
            else if ( (LA11_0==ID) ) {
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
                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:207:4: TRUE
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    TRUE65=(Token)match(input,TRUE,FOLLOW_TRUE_in_cond_atom796); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    TRUE65_tree = 
                    (CommonTree)adaptor.create(TRUE65)
                    ;
                    adaptor.addChild(root_0, TRUE65_tree);
                    }

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:211:5: call
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_call_in_cond_atom817);
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
    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:214:1: expr : expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 ) ;
    public final CtrlParser.expr_return expr() throws RecognitionException {
        CtrlParser.expr_return retval = new CtrlParser.expr_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token BAR68=null;
        CtrlParser.expr2_return expr267 =null;

        CtrlParser.expr2_return expr269 =null;


        CommonTree BAR68_tree=null;
        RewriteRuleTokenStream stream_BAR=new RewriteRuleTokenStream(adaptor,"token BAR");
        RewriteRuleSubtreeStream stream_expr2=new RewriteRuleSubtreeStream(adaptor,"rule expr2");
        try {
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:215:2: ( expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 ) )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:219:4: expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 )
            {
            pushFollow(FOLLOW_expr2_in_expr847);
            expr267=expr2();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_expr2.add(expr267.getTree());

            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:220:4: ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 )
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==BAR) ) {
                alt13=1;
            }
            else if ( ((LA13_0 >= RPAR && LA13_0 <= SEMI)) ) {
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
                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:220:6: ( BAR expr2 )+
                    {
                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:220:6: ( BAR expr2 )+
                    int cnt12=0;
                    loop12:
                    do {
                        int alt12=2;
                        int LA12_0 = input.LA(1);

                        if ( (LA12_0==BAR) ) {
                            alt12=1;
                        }


                        switch (alt12) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:220:7: BAR expr2
                    	    {
                    	    BAR68=(Token)match(input,BAR,FOLLOW_BAR_in_expr855); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_BAR.add(BAR68);


                    	    pushFollow(FOLLOW_expr2_in_expr857);
                    	    expr269=expr2();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_expr2.add(expr269.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt12 >= 1 ) break loop12;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(12, input);
                                throw eee;
                        }
                        cnt12++;
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
                    // 220:19: -> ^( CHOICE expr2 ( expr2 )+ )
                    {
                        // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:220:22: ^( CHOICE expr2 ( expr2 )+ )
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
                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:221:6: 
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
                    // 221:6: -> expr2
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
    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:225:1: expr2 : (e= expr_atom ( PLUS -> ^( BLOCK $e ^( STAR $e) ) | ASTERISK -> ^( STAR $e) | -> $e) | SHARP expr_atom -> ^( ALAP expr_atom ) );
    public final CtrlParser.expr2_return expr2() throws RecognitionException {
        CtrlParser.expr2_return retval = new CtrlParser.expr2_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token PLUS70=null;
        Token ASTERISK71=null;
        Token SHARP72=null;
        CtrlParser.expr_atom_return e =null;

        CtrlParser.expr_atom_return expr_atom73 =null;


        CommonTree PLUS70_tree=null;
        CommonTree ASTERISK71_tree=null;
        CommonTree SHARP72_tree=null;
        RewriteRuleTokenStream stream_PLUS=new RewriteRuleTokenStream(adaptor,"token PLUS");
        RewriteRuleTokenStream stream_SHARP=new RewriteRuleTokenStream(adaptor,"token SHARP");
        RewriteRuleTokenStream stream_ASTERISK=new RewriteRuleTokenStream(adaptor,"token ASTERISK");
        RewriteRuleSubtreeStream stream_expr_atom=new RewriteRuleSubtreeStream(adaptor,"rule expr_atom");
        try {
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:226:3: (e= expr_atom ( PLUS -> ^( BLOCK $e ^( STAR $e) ) | ASTERISK -> ^( STAR $e) | -> $e) | SHARP expr_atom -> ^( ALAP expr_atom ) )
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==ANY||LA15_0==ID||LA15_0==LPAR||LA15_0==OTHER) ) {
                alt15=1;
            }
            else if ( (LA15_0==SHARP) ) {
                alt15=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;

            }
            switch (alt15) {
                case 1 :
                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:234:5: e= expr_atom ( PLUS -> ^( BLOCK $e ^( STAR $e) ) | ASTERISK -> ^( STAR $e) | -> $e)
                    {
                    pushFollow(FOLLOW_expr_atom_in_expr2938);
                    e=expr_atom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expr_atom.add(e.getTree());

                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:235:5: ( PLUS -> ^( BLOCK $e ^( STAR $e) ) | ASTERISK -> ^( STAR $e) | -> $e)
                    int alt14=3;
                    switch ( input.LA(1) ) {
                    case PLUS:
                        {
                        alt14=1;
                        }
                        break;
                    case ASTERISK:
                        {
                        alt14=2;
                        }
                        break;
                    case BAR:
                    case RPAR:
                    case SEMI:
                        {
                        alt14=3;
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
                            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:235:7: PLUS
                            {
                            PLUS70=(Token)match(input,PLUS,FOLLOW_PLUS_in_expr2946); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_PLUS.add(PLUS70);


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
                            // 235:12: -> ^( BLOCK $e ^( STAR $e) )
                            {
                                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:235:15: ^( BLOCK $e ^( STAR $e) )
                                {
                                CommonTree root_1 = (CommonTree)adaptor.nil();
                                root_1 = (CommonTree)adaptor.becomeRoot(
                                (CommonTree)adaptor.create(BLOCK, "BLOCK")
                                , root_1);

                                adaptor.addChild(root_1, stream_e.nextTree());

                                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:235:26: ^( STAR $e)
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
                            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:236:7: ASTERISK
                            {
                            ASTERISK71=(Token)match(input,ASTERISK,FOLLOW_ASTERISK_in_expr2970); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_ASTERISK.add(ASTERISK71);


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
                            // 236:16: -> ^( STAR $e)
                            {
                                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:236:19: ^( STAR $e)
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
                            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:237:7: 
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
                            // 237:7: -> $e
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
                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:243:5: SHARP expr_atom
                    {
                    SHARP72=(Token)match(input,SHARP,FOLLOW_SHARP_in_expr21022); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SHARP.add(SHARP72);


                    pushFollow(FOLLOW_expr_atom_in_expr21024);
                    expr_atom73=expr_atom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expr_atom.add(expr_atom73.getTree());

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
                    // 243:21: -> ^( ALAP expr_atom )
                    {
                        // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:243:24: ^( ALAP expr_atom )
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
    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:246:1: expr_atom : ( ANY | OTHER | LPAR ! expr RPAR !| call );
    public final CtrlParser.expr_atom_return expr_atom() throws RecognitionException {
        CtrlParser.expr_atom_return retval = new CtrlParser.expr_atom_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token ANY74=null;
        Token OTHER75=null;
        Token LPAR76=null;
        Token RPAR78=null;
        CtrlParser.expr_return expr77 =null;

        CtrlParser.call_return call79 =null;


        CommonTree ANY74_tree=null;
        CommonTree OTHER75_tree=null;
        CommonTree LPAR76_tree=null;
        CommonTree RPAR78_tree=null;

        try {
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:247:2: ( ANY | OTHER | LPAR ! expr RPAR !| call )
            int alt16=4;
            switch ( input.LA(1) ) {
            case ANY:
                {
                alt16=1;
                }
                break;
            case OTHER:
                {
                alt16=2;
                }
                break;
            case LPAR:
                {
                alt16=3;
                }
                break;
            case ID:
                {
                alt16=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 16, 0, input);

                throw nvae;

            }

            switch (alt16) {
                case 1 :
                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:249:4: ANY
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    ANY74=(Token)match(input,ANY,FOLLOW_ANY_in_expr_atom1052); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ANY74_tree = 
                    (CommonTree)adaptor.create(ANY74)
                    ;
                    adaptor.addChild(root_0, ANY74_tree);
                    }

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:253:4: OTHER
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    OTHER75=(Token)match(input,OTHER,FOLLOW_OTHER_in_expr_atom1069); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    OTHER75_tree = 
                    (CommonTree)adaptor.create(OTHER75)
                    ;
                    adaptor.addChild(root_0, OTHER75_tree);
                    }

                    }
                    break;
                case 3 :
                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:256:4: LPAR ! expr RPAR !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    LPAR76=(Token)match(input,LPAR,FOLLOW_LPAR_in_expr_atom1082); if (state.failed) return retval;

                    pushFollow(FOLLOW_expr_in_expr_atom1085);
                    expr77=expr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expr77.getTree());

                    RPAR78=(Token)match(input,RPAR,FOLLOW_RPAR_in_expr_atom1087); if (state.failed) return retval;

                    }
                    break;
                case 4 :
                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:259:4: call
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_call_in_expr_atom1101);
                    call79=call();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, call79.getTree());

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
    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:263:1: call : rule_name ( arg_list )? -> ^( CALL[$rule_name.start] rule_name ( arg_list )? ) ;
    public final CtrlParser.call_return call() throws RecognitionException {
        CtrlParser.call_return retval = new CtrlParser.call_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        CtrlParser.rule_name_return rule_name80 =null;

        CtrlParser.arg_list_return arg_list81 =null;


        RewriteRuleSubtreeStream stream_arg_list=new RewriteRuleSubtreeStream(adaptor,"rule arg_list");
        RewriteRuleSubtreeStream stream_rule_name=new RewriteRuleSubtreeStream(adaptor,"rule rule_name");
        try {
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:264:2: ( rule_name ( arg_list )? -> ^( CALL[$rule_name.start] rule_name ( arg_list )? ) )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:268:4: rule_name ( arg_list )?
            {
            pushFollow(FOLLOW_rule_name_in_call1131);
            rule_name80=rule_name();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rule_name.add(rule_name80.getTree());

            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:268:14: ( arg_list )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==LPAR) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:268:14: arg_list
                    {
                    pushFollow(FOLLOW_arg_list_in_call1133);
                    arg_list81=arg_list();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_arg_list.add(arg_list81.getTree());

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
            // 269:4: -> ^( CALL[$rule_name.start] rule_name ( arg_list )? )
            {
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:269:7: ^( CALL[$rule_name.start] rule_name ( arg_list )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(CALL, (rule_name80!=null?((Token)rule_name80.start):null))
                , root_1);

                adaptor.addChild(root_1, stream_rule_name.nextTree());

                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:269:42: ( arg_list )?
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
    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:275:1: arg_list : LPAR ( arg ( COMMA arg )* )? RPAR -> ^( ARGS ( arg )* ) ;
    public final CtrlParser.arg_list_return arg_list() throws RecognitionException {
        CtrlParser.arg_list_return retval = new CtrlParser.arg_list_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token LPAR82=null;
        Token COMMA84=null;
        Token RPAR86=null;
        CtrlParser.arg_return arg83 =null;

        CtrlParser.arg_return arg85 =null;


        CommonTree LPAR82_tree=null;
        CommonTree COMMA84_tree=null;
        CommonTree RPAR86_tree=null;
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_arg=new RewriteRuleSubtreeStream(adaptor,"rule arg");
        try {
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:276:3: ( LPAR ( arg ( COMMA arg )* )? RPAR -> ^( ARGS ( arg )* ) )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:278:5: LPAR ( arg ( COMMA arg )* )? RPAR
            {
            LPAR82=(Token)match(input,LPAR,FOLLOW_LPAR_in_arg_list1173); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAR.add(LPAR82);


            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:278:10: ( arg ( COMMA arg )* )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==DONT_CARE||LA19_0==FALSE||LA19_0==ID||LA19_0==INT_LIT||LA19_0==OUT||LA19_0==REAL_LIT||(LA19_0 >= STRING_LIT && LA19_0 <= TRUE)) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:278:11: arg ( COMMA arg )*
                    {
                    pushFollow(FOLLOW_arg_in_arg_list1176);
                    arg83=arg();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_arg.add(arg83.getTree());

                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:278:15: ( COMMA arg )*
                    loop18:
                    do {
                        int alt18=2;
                        int LA18_0 = input.LA(1);

                        if ( (LA18_0==COMMA) ) {
                            alt18=1;
                        }


                        switch (alt18) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:278:16: COMMA arg
                    	    {
                    	    COMMA84=(Token)match(input,COMMA,FOLLOW_COMMA_in_arg_list1179); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA84);


                    	    pushFollow(FOLLOW_arg_in_arg_list1181);
                    	    arg85=arg();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_arg.add(arg85.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop18;
                        }
                    } while (true);


                    }
                    break;

            }


            RPAR86=(Token)match(input,RPAR,FOLLOW_RPAR_in_arg_list1187); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAR.add(RPAR86);


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
            // 279:5: -> ^( ARGS ( arg )* )
            {
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:279:8: ^( ARGS ( arg )* )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(ARGS, "ARGS")
                , root_1);

                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:279:15: ( arg )*
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
    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:285:1: arg : ( OUT ID -> ^( ARG OUT ID ) | ID -> ^( ARG ID ) | DONT_CARE -> ^( ARG DONT_CARE ) | literal -> ^( ARG literal ) );
    public final CtrlParser.arg_return arg() throws RecognitionException {
        CtrlParser.arg_return retval = new CtrlParser.arg_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token OUT87=null;
        Token ID88=null;
        Token ID89=null;
        Token DONT_CARE90=null;
        CtrlParser.literal_return literal91 =null;


        CommonTree OUT87_tree=null;
        CommonTree ID88_tree=null;
        CommonTree ID89_tree=null;
        CommonTree DONT_CARE90_tree=null;
        RewriteRuleTokenStream stream_DONT_CARE=new RewriteRuleTokenStream(adaptor,"token DONT_CARE");
        RewriteRuleTokenStream stream_OUT=new RewriteRuleTokenStream(adaptor,"token OUT");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_literal=new RewriteRuleSubtreeStream(adaptor,"rule literal");
        try {
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:286:3: ( OUT ID -> ^( ARG OUT ID ) | ID -> ^( ARG ID ) | DONT_CARE -> ^( ARG DONT_CARE ) | literal -> ^( ARG literal ) )
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
            case FALSE:
            case INT_LIT:
            case REAL_LIT:
            case STRING_LIT:
            case TRUE:
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
                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:289:5: OUT ID
                    {
                    OUT87=(Token)match(input,OUT,FOLLOW_OUT_in_arg1230); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_OUT.add(OUT87);


                    ID88=(Token)match(input,ID,FOLLOW_ID_in_arg1232); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID88);


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
                    // 289:12: -> ^( ARG OUT ID )
                    {
                        // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:289:15: ^( ARG OUT ID )
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
                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:293:5: ID
                    {
                    ID89=(Token)match(input,ID,FOLLOW_ID_in_arg1263); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID89);


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
                    // 293:8: -> ^( ARG ID )
                    {
                        // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:293:11: ^( ARG ID )
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
                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:297:5: DONT_CARE
                    {
                    DONT_CARE90=(Token)match(input,DONT_CARE,FOLLOW_DONT_CARE_in_arg1292); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DONT_CARE.add(DONT_CARE90);


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
                    // 297:15: -> ^( ARG DONT_CARE )
                    {
                        // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:297:18: ^( ARG DONT_CARE )
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
                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:298:5: literal
                    {
                    pushFollow(FOLLOW_literal_in_arg1309);
                    literal91=literal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_literal.add(literal91.getTree());

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
                    // 298:13: -> ^( ARG literal )
                    {
                        // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:298:16: ^( ARG literal )
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
    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:301:1: literal : ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT );
    public final CtrlParser.literal_return literal() throws RecognitionException {
        CtrlParser.literal_return retval = new CtrlParser.literal_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token set92=null;

        CommonTree set92_tree=null;

        try {
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:302:3: ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:
            {
            root_0 = (CommonTree)adaptor.nil();


            set92=(Token)input.LT(1);

            if ( input.LA(1)==FALSE||input.LA(1)==INT_LIT||input.LA(1)==REAL_LIT||(input.LA(1) >= STRING_LIT && input.LA(1) <= TRUE) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, 
                (CommonTree)adaptor.create(set92)
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
    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:320:1: rule_name :ids+= ID ( DOT ids+= ID )* ->;
    public final CtrlParser.rule_name_return rule_name() throws RecognitionException {
        CtrlParser.rule_name_return retval = new CtrlParser.rule_name_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token DOT93=null;
        Token ids=null;
        List list_ids=null;

        CommonTree DOT93_tree=null;
        CommonTree ids_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");

        try {
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:321:3: (ids+= ID ( DOT ids+= ID )* ->)
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:321:5: ids+= ID ( DOT ids+= ID )*
            {
            ids=(Token)match(input,ID,FOLLOW_ID_in_rule_name1421); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ids);

            if (list_ids==null) list_ids=new ArrayList();
            list_ids.add(ids);


            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:321:13: ( DOT ids+= ID )*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);

                if ( (LA21_0==DOT) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:321:14: DOT ids+= ID
            	    {
            	    DOT93=(Token)match(input,DOT,FOLLOW_DOT_in_rule_name1424); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(DOT93);


            	    ids=(Token)match(input,ID,FOLLOW_ID_in_rule_name1428); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_ID.add(ids);

            	    if (list_ids==null) list_ids=new ArrayList();
            	    list_ids.add(ids);


            	    }
            	    break;

            	default :
            	    break loop21;
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
            // 322:5: ->
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
    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:326:1: var_decl : var_type ID ( COMMA ID )* -> ^( VAR var_type ( ID )+ ) ;
    public final CtrlParser.var_decl_return var_decl() throws RecognitionException {
        CtrlParser.var_decl_return retval = new CtrlParser.var_decl_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token ID95=null;
        Token COMMA96=null;
        Token ID97=null;
        CtrlParser.var_type_return var_type94 =null;


        CommonTree ID95_tree=null;
        CommonTree COMMA96_tree=null;
        CommonTree ID97_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_var_type=new RewriteRuleSubtreeStream(adaptor,"rule var_type");
        try {
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:327:2: ( var_type ID ( COMMA ID )* -> ^( VAR var_type ( ID )+ ) )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:329:4: var_type ID ( COMMA ID )*
            {
            pushFollow(FOLLOW_var_type_in_var_decl1460);
            var_type94=var_type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_var_type.add(var_type94.getTree());

            ID95=(Token)match(input,ID,FOLLOW_ID_in_var_decl1462); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID95);


            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:329:16: ( COMMA ID )*
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( (LA22_0==COMMA) ) {
                    alt22=1;
                }


                switch (alt22) {
            	case 1 :
            	    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:329:17: COMMA ID
            	    {
            	    COMMA96=(Token)match(input,COMMA,FOLLOW_COMMA_in_var_decl1465); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA96);


            	    ID97=(Token)match(input,ID,FOLLOW_ID_in_var_decl1467); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_ID.add(ID97);


            	    }
            	    break;

            	default :
            	    break loop22;
                }
            } while (true);


            // AST REWRITE
            // elements: ID, var_type
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 329:28: -> ^( VAR var_type ( ID )+ )
            {
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:329:31: ^( VAR var_type ( ID )+ )
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
    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:333:1: var_type : ( NODE | BOOL | STRING | INT | REAL );
    public final CtrlParser.var_type_return var_type() throws RecognitionException {
        CtrlParser.var_type_return retval = new CtrlParser.var_type_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token set98=null;

        CommonTree set98_tree=null;

        try {
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:334:2: ( NODE | BOOL | STRING | INT | REAL )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:
            {
            root_0 = (CommonTree)adaptor.nil();


            set98=(Token)input.LT(1);

            if ( input.LA(1)==BOOL||input.LA(1)==INT||input.LA(1)==NODE||input.LA(1)==REAL||input.LA(1)==STRING ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, 
                (CommonTree)adaptor.create(set98)
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
        // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:178:33: ( ELSE )
        // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:178:34: ELSE
        {
        match(input,ELSE,FOLLOW_ELSE_in_synpred1_Ctrl599); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred1_Ctrl

    // $ANTLR start synpred2_Ctrl
    public final void synpred2_Ctrl_fragment() throws RecognitionException {
        // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:182:17: ( ELSE )
        // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:182:18: ELSE
        {
        match(input,ELSE,FOLLOW_ELSE_in_synpred2_Ctrl639); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred2_Ctrl

    // $ANTLR start synpred3_Ctrl
    public final void synpred3_Ctrl_fragment() throws RecognitionException {
        // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:185:20: ( OR )
        // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:185:21: OR
        {
        match(input,OR,FOLLOW_OR_in_synpred3_Ctrl674); if (state.failed) return ;

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


 

    public static final BitSet FOLLOW_function_in_program137 = new BitSet(new long[]{0x592281133A029050L});
    public static final BitSet FOLLOW_recipe_in_program139 = new BitSet(new long[]{0x592281133A029050L});
    public static final BitSet FOLLOW_stat_in_program141 = new BitSet(new long[]{0x592281133A029050L});
    public static final BitSet FOLLOW_EOF_in_program145 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RECIPE_in_recipe207 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_recipe210 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_LPAR_in_recipe212 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_RPAR_in_recipe215 = new BitSet(new long[]{0x0000080100000000L});
    public static final BitSet FOLLOW_PRIORITY_in_recipe219 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_INT_LIT_in_recipe222 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_block_in_recipe226 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_function257 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_function260 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_LPAR_in_function262 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_RPAR_in_function265 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_block_in_function268 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LCURLY_in_block299 = new BitSet(new long[]{0x5920C11338029050L});
    public static final BitSet FOLLOW_stat_in_block301 = new BitSet(new long[]{0x5920C11338029050L});
    public static final BitSet FOLLOW_RCURLY_in_block304 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_stat328 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALAP_in_stat345 = new BitSet(new long[]{0x5920811338029050L});
    public static final BitSet FOLLOW_stat_in_stat348 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHILE_in_stat369 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_LPAR_in_stat372 = new BitSet(new long[]{0x0400000008000000L});
    public static final BitSet FOLLOW_cond_in_stat375 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat377 = new BitSet(new long[]{0x5920811338029050L});
    public static final BitSet FOLLOW_stat_in_stat380 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNTIL_in_stat400 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_LPAR_in_stat403 = new BitSet(new long[]{0x0400000008000000L});
    public static final BitSet FOLLOW_cond_in_stat406 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat408 = new BitSet(new long[]{0x5920811338029050L});
    public static final BitSet FOLLOW_stat_in_stat411 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DO_in_stat416 = new BitSet(new long[]{0x5920811338029050L});
    public static final BitSet FOLLOW_stat_in_stat418 = new BitSet(new long[]{0x5000000000000000L});
    public static final BitSet FOLLOW_WHILE_in_stat461 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_LPAR_in_stat463 = new BitSet(new long[]{0x0400000008000000L});
    public static final BitSet FOLLOW_cond_in_stat465 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat467 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNTIL_in_stat530 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_LPAR_in_stat532 = new BitSet(new long[]{0x0400000008000000L});
    public static final BitSet FOLLOW_cond_in_stat534 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat536 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IF_in_stat583 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_LPAR_in_stat586 = new BitSet(new long[]{0x0400000008000000L});
    public static final BitSet FOLLOW_cond_in_stat589 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat591 = new BitSet(new long[]{0x5920811338029050L});
    public static final BitSet FOLLOW_stat_in_stat594 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_ELSE_in_stat604 = new BitSet(new long[]{0x5920811338029050L});
    public static final BitSet FOLLOW_stat_in_stat607 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRY_in_stat631 = new BitSet(new long[]{0x5920811338029050L});
    public static final BitSet FOLLOW_stat_in_stat634 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_ELSE_in_stat644 = new BitSet(new long[]{0x5920811338029050L});
    public static final BitSet FOLLOW_stat_in_stat647 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CHOICE_in_stat666 = new BitSet(new long[]{0x5920811338029050L});
    public static final BitSet FOLLOW_stat_in_stat669 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_OR_in_stat679 = new BitSet(new long[]{0x5920811338029050L});
    public static final BitSet FOLLOW_stat_in_stat682 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_expr_in_stat697 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_SEMI_in_stat699 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_decl_in_stat713 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_SEMI_in_stat715 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_cond_atom_in_cond739 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_BAR_in_cond748 = new BitSet(new long[]{0x0400000008000000L});
    public static final BitSet FOLLOW_cond_atom_in_cond750 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_TRUE_in_cond_atom796 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_cond_atom817 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr2_in_expr847 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_BAR_in_expr855 = new BitSet(new long[]{0x0020010208000040L});
    public static final BitSet FOLLOW_expr2_in_expr857 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_expr_atom_in_expr2938 = new BitSet(new long[]{0x0000040000000202L});
    public static final BitSet FOLLOW_PLUS_in_expr2946 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASTERISK_in_expr2970 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SHARP_in_expr21022 = new BitSet(new long[]{0x0000010208000040L});
    public static final BitSet FOLLOW_expr_atom_in_expr21024 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_expr_atom1052 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_expr_atom1069 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_expr_atom1082 = new BitSet(new long[]{0x0020010208000040L});
    public static final BitSet FOLLOW_expr_in_expr_atom1085 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_RPAR_in_expr_atom1087 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_expr_atom1101 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_name_in_call1131 = new BitSet(new long[]{0x0000000200000002L});
    public static final BitSet FOLLOW_arg_list_in_call1133 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_arg_list1173 = new BitSet(new long[]{0x0609020049040000L});
    public static final BitSet FOLLOW_arg_in_arg_list1176 = new BitSet(new long[]{0x0008000000010000L});
    public static final BitSet FOLLOW_COMMA_in_arg_list1179 = new BitSet(new long[]{0x0601020049040000L});
    public static final BitSet FOLLOW_arg_in_arg_list1181 = new BitSet(new long[]{0x0008000000010000L});
    public static final BitSet FOLLOW_RPAR_in_arg_list1187 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OUT_in_arg1230 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_arg1232 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_arg1263 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DONT_CARE_in_arg1292 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_arg1309 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_rule_name1421 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_DOT_in_rule_name1424 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_rule_name1428 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_var_type_in_var_decl1460 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_var_decl1462 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_COMMA_in_var_decl1465 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_var_decl1467 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_ELSE_in_synpred1_Ctrl599 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ELSE_in_synpred2_Ctrl639 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_synpred3_Ctrl674 = new BitSet(new long[]{0x0000000000000002L});

}