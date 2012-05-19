// $ANTLR 3.4 E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g 2012-01-16 17:57:03

package groove.control.parse;
import groove.control.*;
import groove.control.CtrlCall.Kind;
import groove.trans.Rule;
import groove.algebra.AlgebraFamily;
import groove.view.FormatError;
import java.util.LinkedList;
import java.util.Stack;
import java.util.HashSet;
import java.util.HashMap;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


@SuppressWarnings({"all", "warnings", "unchecked"})
public class CtrlChecker extends TreeParser {
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
    public TreeParser[] getDelegates() {
        return new TreeParser[] {};
    }

    // delegators


    public CtrlChecker(TreeNodeStream input) {
        this(input, new RecognizerSharedState());
    }
    public CtrlChecker(TreeNodeStream input, RecognizerSharedState state) {
        super(input, state);
    }

protected TreeAdaptor adaptor = new CommonTreeAdaptor();

public void setTreeAdaptor(TreeAdaptor adaptor) {
    this.adaptor = adaptor;
}
public TreeAdaptor getTreeAdaptor() {
    return adaptor;
}
    public String[] getTokenNames() { return CtrlChecker.tokenNames; }
    public String getGrammarFileName() { return "E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g"; }


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
        public MyTree run(MyTree tree, Namespace namespace, AlgebraFamily family) throws RecognitionException {
            this.helper = new CtrlHelper(this, namespace, family);
            MyTreeAdaptor treeAdaptor = new MyTreeAdaptor();
            setTreeAdaptor(treeAdaptor);
            setTreeNodeStream(treeAdaptor.createTreeNodeStream(tree));
            return (MyTree) program().getTree();
        }


    public static class program_return extends TreeRuleReturnScope {
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "program"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:52:1: program : ^( PROGRAM recipes functions block ) ;
    public final CtrlChecker.program_return program() throws RecognitionException {
        CtrlChecker.program_return retval = new CtrlChecker.program_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree PROGRAM1=null;
        CtrlChecker.recipes_return recipes2 =null;

        CtrlChecker.functions_return functions3 =null;

        CtrlChecker.block_return block4 =null;


        MyTree PROGRAM1_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:53:3: ( ^( PROGRAM recipes functions block ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:53:5: ^( PROGRAM recipes functions block )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            PROGRAM1=(MyTree)match(input,PROGRAM,FOLLOW_PROGRAM_in_program56); 


            if ( _first_0==null ) _first_0 = PROGRAM1;
            match(input, Token.DOWN, null); 
            _last = (MyTree)input.LT(1);
            pushFollow(FOLLOW_recipes_in_program58);
            recipes2=recipes();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = recipes2.tree;


            _last = (MyTree)input.LT(1);
            pushFollow(FOLLOW_functions_in_program60);
            functions3=functions();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = functions3.tree;


            _last = (MyTree)input.LT(1);
            pushFollow(FOLLOW_block_in_program62);
            block4=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block4.tree;


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


             if ((block4!=null?((MyTree)block4.tree):null).getChildCount() == 0) {
                      helper.checkAny(PROGRAM1);
                  }
                

            retval.tree = (MyTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (MyTree)adaptor.getParent(retval.tree);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "program"


    public static class recipes_return extends TreeRuleReturnScope {
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "recipes"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:60:1: recipes : ^( RECIPES ( recipe )* ) ;
    public final CtrlChecker.recipes_return recipes() throws RecognitionException {
        CtrlChecker.recipes_return retval = new CtrlChecker.recipes_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree RECIPES5=null;
        CtrlChecker.recipe_return recipe6 =null;


        MyTree RECIPES5_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:61:3: ( ^( RECIPES ( recipe )* ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:61:5: ^( RECIPES ( recipe )* )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            RECIPES5=(MyTree)match(input,RECIPES,FOLLOW_RECIPES_in_recipes84); 


            if ( _first_0==null ) _first_0 = RECIPES5;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:61:15: ( recipe )*
                loop1:
                do {
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( (LA1_0==RECIPE) ) {
                        alt1=1;
                    }


                    switch (alt1) {
                	case 1 :
                	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:61:15: recipe
                	    {
                	    _last = (MyTree)input.LT(1);
                	    pushFollow(FOLLOW_recipe_in_recipes86);
                	    recipe6=recipe();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = recipe6.tree;


                	    retval.tree = (MyTree)_first_0;
                	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                	        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                	    }
                	    break;

                	default :
                	    break loop1;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }
            _last = _save_last_1;
            }


            retval.tree = (MyTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (MyTree)adaptor.getParent(retval.tree);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "recipes"


    public static class recipe_return extends TreeRuleReturnScope {
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "recipe"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:64:1: recipe : ^( RECIPE ID ( INT_LIT )? block ) ;
    public final CtrlChecker.recipe_return recipe() throws RecognitionException {
        CtrlChecker.recipe_return retval = new CtrlChecker.recipe_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree RECIPE7=null;
        MyTree ID8=null;
        MyTree INT_LIT9=null;
        CtrlChecker.block_return block10 =null;


        MyTree RECIPE7_tree=null;
        MyTree ID8_tree=null;
        MyTree INT_LIT9_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:65:3: ( ^( RECIPE ID ( INT_LIT )? block ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:65:5: ^( RECIPE ID ( INT_LIT )? block )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            RECIPE7=(MyTree)match(input,RECIPE,FOLLOW_RECIPE_in_recipe103); 


            if ( _first_0==null ) _first_0 = RECIPE7;
            match(input, Token.DOWN, null); 
            _last = (MyTree)input.LT(1);
            ID8=(MyTree)match(input,ID,FOLLOW_ID_in_recipe105); 
             
            if ( _first_1==null ) _first_1 = ID8;


            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:65:18: ( INT_LIT )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==INT_LIT) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:65:18: INT_LIT
                    {
                    _last = (MyTree)input.LT(1);
                    INT_LIT9=(MyTree)match(input,INT_LIT,FOLLOW_INT_LIT_in_recipe107); 
                     
                    if ( _first_1==null ) _first_1 = INT_LIT9;


                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;

            }


             helper.startBody(ID8, Kind.RECIPE); 

            _last = (MyTree)input.LT(1);
            pushFollow(FOLLOW_block_in_recipe127);
            block10=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block10.tree;


             helper.endBody(); 

            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = (MyTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (MyTree)adaptor.getParent(retval.tree);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "recipe"


    public static class functions_return extends TreeRuleReturnScope {
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "functions"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:72:1: functions : ^( FUNCTIONS ( function )* ) ;
    public final CtrlChecker.functions_return functions() throws RecognitionException {
        CtrlChecker.functions_return retval = new CtrlChecker.functions_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree FUNCTIONS11=null;
        CtrlChecker.function_return function12 =null;


        MyTree FUNCTIONS11_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:73:3: ( ^( FUNCTIONS ( function )* ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:73:5: ^( FUNCTIONS ( function )* )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            FUNCTIONS11=(MyTree)match(input,FUNCTIONS,FOLLOW_FUNCTIONS_in_functions158); 


            if ( _first_0==null ) _first_0 = FUNCTIONS11;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:73:17: ( function )*
                loop3:
                do {
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0==FUNCTION) ) {
                        alt3=1;
                    }


                    switch (alt3) {
                	case 1 :
                	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:73:17: function
                	    {
                	    _last = (MyTree)input.LT(1);
                	    pushFollow(FOLLOW_function_in_functions160);
                	    function12=function();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = function12.tree;


                	    retval.tree = (MyTree)_first_0;
                	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                	        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                	    }
                	    break;

                	default :
                	    break loop3;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }
            _last = _save_last_1;
            }


             helper.reorderFunctions(FUNCTIONS11); 

            retval.tree = (MyTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (MyTree)adaptor.getParent(retval.tree);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "functions"


    public static class function_return extends TreeRuleReturnScope {
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "function"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:77:1: function : ^( FUNCTION ID block ) ;
    public final CtrlChecker.function_return function() throws RecognitionException {
        CtrlChecker.function_return retval = new CtrlChecker.function_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree FUNCTION13=null;
        MyTree ID14=null;
        CtrlChecker.block_return block15 =null;


        MyTree FUNCTION13_tree=null;
        MyTree ID14_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:78:3: ( ^( FUNCTION ID block ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:78:5: ^( FUNCTION ID block )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            FUNCTION13=(MyTree)match(input,FUNCTION,FOLLOW_FUNCTION_in_function183); 


            if ( _first_0==null ) _first_0 = FUNCTION13;
            match(input, Token.DOWN, null); 
            _last = (MyTree)input.LT(1);
            ID14=(MyTree)match(input,ID,FOLLOW_ID_in_function185); 
             
            if ( _first_1==null ) _first_1 = ID14;


             helper.startBody(ID14, Kind.FUNCTION); 

            _last = (MyTree)input.LT(1);
            pushFollow(FOLLOW_block_in_function204);
            block15=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block15.tree;


             helper.endBody(); 

            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = (MyTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (MyTree)adaptor.getParent(retval.tree);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "function"


    public static class block_return extends TreeRuleReturnScope {
        public CtrlAut aut;
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "block"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:85:1: block returns [ CtrlAut aut ] : ^( BLOCK ( stat )* ) ;
    public final CtrlChecker.block_return block() throws RecognitionException {
        CtrlChecker.block_return retval = new CtrlChecker.block_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree BLOCK16=null;
        CtrlChecker.stat_return stat17 =null;


        MyTree BLOCK16_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:86:3: ( ^( BLOCK ( stat )* ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:86:5: ^( BLOCK ( stat )* )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            BLOCK16=(MyTree)match(input,BLOCK,FOLLOW_BLOCK_in_block242); 


            if ( _first_0==null ) _first_0 = BLOCK16;
             helper.openScope(); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:88:8: ( stat )*
                loop4:
                do {
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==ALAP||LA4_0==ANY||LA4_0==BLOCK||(LA4_0 >= CALL && LA4_0 <= CHOICE)||LA4_0==IF||LA4_0==OTHER||LA4_0==STAR||(LA4_0 >= TRUE && LA4_0 <= WHILE)) ) {
                        alt4=1;
                    }


                    switch (alt4) {
                	case 1 :
                	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:88:8: stat
                	    {
                	    _last = (MyTree)input.LT(1);
                	    pushFollow(FOLLOW_stat_in_block260);
                	    stat17=stat();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = stat17.tree;


                	    retval.tree = (MyTree)_first_0;
                	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                	        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                	    }
                	    break;

                	default :
                	    break loop4;
                    }
                } while (true);


                 helper.closeScope(); 

                match(input, Token.UP, null); 
            }
            _last = _save_last_1;
            }


            retval.tree = (MyTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (MyTree)adaptor.getParent(retval.tree);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "block"


    public static class stat_return extends TreeRuleReturnScope {
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "stat"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:93:1: stat : ( block | var_decl | ^( ALAP stat ) | ^( WHILE stat stat ) | ^( UNTIL stat stat ) | ^( TRY stat ( stat )? ) | ^( IF stat stat ( stat )? ) | ^( CHOICE stat ( stat )* ) | ^( STAR stat ) | rule | ANY | OTHER | TRUE );
    public final CtrlChecker.stat_return stat() throws RecognitionException {
        CtrlChecker.stat_return retval = new CtrlChecker.stat_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree ALAP20=null;
        MyTree WHILE22=null;
        MyTree UNTIL25=null;
        MyTree TRY28=null;
        MyTree IF31=null;
        MyTree CHOICE35=null;
        MyTree STAR38=null;
        MyTree ANY41=null;
        MyTree OTHER42=null;
        MyTree TRUE43=null;
        CtrlChecker.block_return block18 =null;

        CtrlChecker.var_decl_return var_decl19 =null;

        CtrlChecker.stat_return stat21 =null;

        CtrlChecker.stat_return stat23 =null;

        CtrlChecker.stat_return stat24 =null;

        CtrlChecker.stat_return stat26 =null;

        CtrlChecker.stat_return stat27 =null;

        CtrlChecker.stat_return stat29 =null;

        CtrlChecker.stat_return stat30 =null;

        CtrlChecker.stat_return stat32 =null;

        CtrlChecker.stat_return stat33 =null;

        CtrlChecker.stat_return stat34 =null;

        CtrlChecker.stat_return stat36 =null;

        CtrlChecker.stat_return stat37 =null;

        CtrlChecker.stat_return stat39 =null;

        CtrlChecker.rule_return rule40 =null;


        MyTree ALAP20_tree=null;
        MyTree WHILE22_tree=null;
        MyTree UNTIL25_tree=null;
        MyTree TRY28_tree=null;
        MyTree IF31_tree=null;
        MyTree CHOICE35_tree=null;
        MyTree STAR38_tree=null;
        MyTree ANY41_tree=null;
        MyTree OTHER42_tree=null;
        MyTree TRUE43_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:94:3: ( block | var_decl | ^( ALAP stat ) | ^( WHILE stat stat ) | ^( UNTIL stat stat ) | ^( TRY stat ( stat )? ) | ^( IF stat stat ( stat )? ) | ^( CHOICE stat ( stat )* ) | ^( STAR stat ) | rule | ANY | OTHER | TRUE )
            int alt8=13;
            switch ( input.LA(1) ) {
            case BLOCK:
                {
                alt8=1;
                }
                break;
            case VAR:
                {
                alt8=2;
                }
                break;
            case ALAP:
                {
                alt8=3;
                }
                break;
            case WHILE:
                {
                alt8=4;
                }
                break;
            case UNTIL:
                {
                alt8=5;
                }
                break;
            case TRY:
                {
                alt8=6;
                }
                break;
            case IF:
                {
                alt8=7;
                }
                break;
            case CHOICE:
                {
                alt8=8;
                }
                break;
            case STAR:
                {
                alt8=9;
                }
                break;
            case CALL:
                {
                alt8=10;
                }
                break;
            case ANY:
                {
                alt8=11;
                }
                break;
            case OTHER:
                {
                alt8=12;
                }
                break;
            case TRUE:
                {
                alt8=13;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;

            }

            switch (alt8) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:94:5: block
                    {
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_block_in_stat290);
                    block18=block();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = block18.tree;


                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:95:5: var_decl
                    {
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_var_decl_in_stat296);
                    var_decl19=var_decl();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = var_decl19.tree;


                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 3 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:96:5: ^( ALAP stat )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    ALAP20=(MyTree)match(input,ALAP,FOLLOW_ALAP_in_stat303); 


                    if ( _first_0==null ) _first_0 = ALAP20;
                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat305);
                    stat21=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat21.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 4 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:97:5: ^( WHILE stat stat )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    WHILE22=(MyTree)match(input,WHILE,FOLLOW_WHILE_in_stat314); 


                    if ( _first_0==null ) _first_0 = WHILE22;
                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat323);
                    stat23=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat23.tree;


                     helper.startBranch(); 

                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat341);
                    stat24=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat24.tree;


                     helper.endBranch(); 

                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 5 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:103:5: ^( UNTIL stat stat )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    UNTIL25=(MyTree)match(input,UNTIL,FOLLOW_UNTIL_in_stat365); 


                    if ( _first_0==null ) _first_0 = UNTIL25;
                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat374);
                    stat26=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat26.tree;


                     helper.startBranch(); 

                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat392);
                    stat27=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat27.tree;


                     helper.endBranch(); 

                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 6 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:109:5: ^( TRY stat ( stat )? )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    TRY28=(MyTree)match(input,TRY,FOLLOW_TRY_in_stat416); 


                    if ( _first_0==null ) _first_0 = TRY28;
                     helper.startBranch(); 

                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat434);
                    stat29=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat29.tree;


                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:112:8: ( stat )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0==ALAP||LA5_0==ANY||LA5_0==BLOCK||(LA5_0 >= CALL && LA5_0 <= CHOICE)||LA5_0==IF||LA5_0==OTHER||LA5_0==STAR||(LA5_0 >= TRUE && LA5_0 <= WHILE)) ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:112:10: stat
                            {
                             helper.nextBranch(); 

                            _last = (MyTree)input.LT(1);
                            pushFollow(FOLLOW_stat_in_stat456);
                            stat30=stat();

                            state._fsp--;

                             
                            if ( _first_1==null ) _first_1 = stat30.tree;


                            retval.tree = (MyTree)_first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (MyTree)adaptor.getParent(retval.tree);

                            }
                            break;

                    }


                     helper.endBranch(); 

                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 7 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:117:5: ^( IF stat stat ( stat )? )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    IF31=(MyTree)match(input,IF,FOLLOW_IF_in_stat490); 


                    if ( _first_0==null ) _first_0 = IF31;
                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat500);
                    stat32=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat32.tree;


                     helper.startBranch(); 

                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat519);
                    stat33=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat33.tree;


                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:121:8: ( stat )?
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0==ALAP||LA6_0==ANY||LA6_0==BLOCK||(LA6_0 >= CALL && LA6_0 <= CHOICE)||LA6_0==IF||LA6_0==OTHER||LA6_0==STAR||(LA6_0 >= TRUE && LA6_0 <= WHILE)) ) {
                        alt6=1;
                    }
                    switch (alt6) {
                        case 1 :
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:121:10: stat
                            {
                             helper.nextBranch(); 

                            _last = (MyTree)input.LT(1);
                            pushFollow(FOLLOW_stat_in_stat542);
                            stat34=stat();

                            state._fsp--;

                             
                            if ( _first_1==null ) _first_1 = stat34.tree;


                            retval.tree = (MyTree)_first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (MyTree)adaptor.getParent(retval.tree);

                            }
                            break;

                    }


                     helper.endBranch(); 

                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 8 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:126:5: ^( CHOICE stat ( stat )* )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    CHOICE35=(MyTree)match(input,CHOICE,FOLLOW_CHOICE_in_stat576); 


                    if ( _first_0==null ) _first_0 = CHOICE35;
                     helper.startBranch(); 

                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat594);
                    stat36=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat36.tree;


                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:129:8: ( stat )*
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( (LA7_0==ALAP||LA7_0==ANY||LA7_0==BLOCK||(LA7_0 >= CALL && LA7_0 <= CHOICE)||LA7_0==IF||LA7_0==OTHER||LA7_0==STAR||(LA7_0 >= TRUE && LA7_0 <= WHILE)) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:129:10: stat
                    	    {
                    	     helper.nextBranch(); 

                    	    _last = (MyTree)input.LT(1);
                    	    pushFollow(FOLLOW_stat_in_stat617);
                    	    stat37=stat();

                    	    state._fsp--;

                    	     
                    	    if ( _first_1==null ) _first_1 = stat37.tree;


                    	    retval.tree = (MyTree)_first_0;
                    	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                    	        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    	    }
                    	    break;

                    	default :
                    	    break loop7;
                        }
                    } while (true);


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                     helper.endBranch(); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 9 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:134:5: ^( STAR stat )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    STAR38=(MyTree)match(input,STAR,FOLLOW_STAR_in_stat653); 


                    if ( _first_0==null ) _first_0 = STAR38;
                     helper.startBranch(); 

                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat671);
                    stat39=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat39.tree;


                     helper.endBranch(); 

                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 10 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:139:5: rule
                    {
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_rule_in_stat693);
                    rule40=rule();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = rule40.tree;


                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 11 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:140:5: ANY
                    {
                    _last = (MyTree)input.LT(1);
                    ANY41=(MyTree)match(input,ANY,FOLLOW_ANY_in_stat699); 
                     
                    if ( _first_0==null ) _first_0 = ANY41;


                     helper.checkAny(ANY41); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 12 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:142:5: OTHER
                    {
                    _last = (MyTree)input.LT(1);
                    OTHER42=(MyTree)match(input,OTHER,FOLLOW_OTHER_in_stat711); 
                     
                    if ( _first_0==null ) _first_0 = OTHER42;


                     helper.checkOther(OTHER42); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 13 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:144:5: TRUE
                    {
                    _last = (MyTree)input.LT(1);
                    TRUE43=(MyTree)match(input,TRUE,FOLLOW_TRUE_in_stat723); 
                     
                    if ( _first_0==null ) _first_0 = TRUE43;


                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "stat"


    public static class rule_return extends TreeRuleReturnScope {
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "rule"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:147:1: rule : ^( CALL id= ID ( ^( ARGS ( arg )* ) )? ) ;
    public final CtrlChecker.rule_return rule() throws RecognitionException {
        CtrlChecker.rule_return retval = new CtrlChecker.rule_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree id=null;
        MyTree CALL44=null;
        MyTree ARGS45=null;
        CtrlChecker.arg_return arg46 =null;


        MyTree id_tree=null;
        MyTree CALL44_tree=null;
        MyTree ARGS45_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:149:3: ( ^( CALL id= ID ( ^( ARGS ( arg )* ) )? ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:149:5: ^( CALL id= ID ( ^( ARGS ( arg )* ) )? )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            CALL44=(MyTree)match(input,CALL,FOLLOW_CALL_in_rule741); 


            if ( _first_0==null ) _first_0 = CALL44;
            match(input, Token.DOWN, null); 
            _last = (MyTree)input.LT(1);
            id=(MyTree)match(input,ID,FOLLOW_ID_in_rule745); 
             
            if ( _first_1==null ) _first_1 = id;


            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:149:18: ( ^( ARGS ( arg )* ) )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==ARGS) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:149:19: ^( ARGS ( arg )* )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_2 = _last;
                    MyTree _first_2 = null;
                    _last = (MyTree)input.LT(1);
                    ARGS45=(MyTree)match(input,ARGS,FOLLOW_ARGS_in_rule749); 


                    if ( _first_1==null ) _first_1 = ARGS45;
                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:149:26: ( arg )*
                        loop9:
                        do {
                            int alt9=2;
                            int LA9_0 = input.LA(1);

                            if ( (LA9_0==ARG) ) {
                                alt9=1;
                            }


                            switch (alt9) {
                        	case 1 :
                        	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:149:26: arg
                        	    {
                        	    _last = (MyTree)input.LT(1);
                        	    pushFollow(FOLLOW_arg_in_rule751);
                        	    arg46=arg();

                        	    state._fsp--;

                        	     
                        	    if ( _first_2==null ) _first_2 = arg46.tree;


                        	    retval.tree = (MyTree)_first_0;
                        	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        	        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                        	    }
                        	    break;

                        	default :
                        	    break loop9;
                            }
                        } while (true);


                        match(input, Token.UP, null); 
                    }
                    _last = _save_last_2;
                    }


                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;

            }


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = (MyTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (MyTree)adaptor.getParent(retval.tree);

            }

             helper.checkCall(((MyTree)retval.tree)); 
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "rule"


    public static class var_decl_return extends TreeRuleReturnScope {
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "var_decl"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:152:1: var_decl : ^( VAR type ( ID )+ ) ;
    public final CtrlChecker.var_decl_return var_decl() throws RecognitionException {
        CtrlChecker.var_decl_return retval = new CtrlChecker.var_decl_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree VAR47=null;
        MyTree ID49=null;
        CtrlChecker.type_return type48 =null;


        MyTree VAR47_tree=null;
        MyTree ID49_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:153:2: ( ^( VAR type ( ID )+ ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:153:4: ^( VAR type ( ID )+ )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            VAR47=(MyTree)match(input,VAR,FOLLOW_VAR_in_var_decl770); 


            if ( _first_0==null ) _first_0 = VAR47;
            match(input, Token.DOWN, null); 
            _last = (MyTree)input.LT(1);
            pushFollow(FOLLOW_type_in_var_decl772);
            type48=type();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = type48.tree;


            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:155:7: ( ID )+
            int cnt11=0;
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( (LA11_0==ID) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:155:9: ID
            	    {
            	    _last = (MyTree)input.LT(1);
            	    ID49=(MyTree)match(input,ID,FOLLOW_ID_in_var_decl789); 
            	     
            	    if ( _first_1==null ) _first_1 = ID49;


            	     helper.declareVar(ID49, (type48!=null?((MyTree)type48.tree):null)); 

            	    retval.tree = (MyTree)_first_0;
            	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
            	        retval.tree = (MyTree)adaptor.getParent(retval.tree);

            	    }
            	    break;

            	default :
            	    if ( cnt11 >= 1 ) break loop11;
                        EarlyExitException eee =
                            new EarlyExitException(11, input);
                        throw eee;
                }
                cnt11++;
            } while (true);


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = (MyTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (MyTree)adaptor.getParent(retval.tree);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "var_decl"


    public static class type_return extends TreeRuleReturnScope {
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "type"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:161:1: type : ( NODE | BOOL | STRING | INT | REAL );
    public final CtrlChecker.type_return type() throws RecognitionException {
        CtrlChecker.type_return retval = new CtrlChecker.type_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree NODE50=null;
        MyTree BOOL51=null;
        MyTree STRING52=null;
        MyTree INT53=null;
        MyTree REAL54=null;

        MyTree NODE50_tree=null;
        MyTree BOOL51_tree=null;
        MyTree STRING52_tree=null;
        MyTree INT53_tree=null;
        MyTree REAL54_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:162:3: ( NODE | BOOL | STRING | INT | REAL )
            int alt12=5;
            switch ( input.LA(1) ) {
            case NODE:
                {
                alt12=1;
                }
                break;
            case BOOL:
                {
                alt12=2;
                }
                break;
            case STRING:
                {
                alt12=3;
                }
                break;
            case INT:
                {
                alt12=4;
                }
                break;
            case REAL:
                {
                alt12=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;

            }

            switch (alt12) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:162:5: NODE
                    {
                    _last = (MyTree)input.LT(1);
                    NODE50=(MyTree)match(input,NODE,FOLLOW_NODE_in_type828); 
                     
                    if ( _first_0==null ) _first_0 = NODE50;


                     helper.checkType(NODE50); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:163:5: BOOL
                    {
                    _last = (MyTree)input.LT(1);
                    BOOL51=(MyTree)match(input,BOOL,FOLLOW_BOOL_in_type838); 
                     
                    if ( _first_0==null ) _first_0 = BOOL51;


                     helper.checkType(BOOL51); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 3 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:164:5: STRING
                    {
                    _last = (MyTree)input.LT(1);
                    STRING52=(MyTree)match(input,STRING,FOLLOW_STRING_in_type848); 
                     
                    if ( _first_0==null ) _first_0 = STRING52;


                     helper.checkType(STRING52); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 4 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:165:5: INT
                    {
                    _last = (MyTree)input.LT(1);
                    INT53=(MyTree)match(input,INT,FOLLOW_INT_in_type856); 
                     
                    if ( _first_0==null ) _first_0 = INT53;


                     helper.checkType(INT53); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 5 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:166:5: REAL
                    {
                    _last = (MyTree)input.LT(1);
                    REAL54=(MyTree)match(input,REAL,FOLLOW_REAL_in_type867); 
                     
                    if ( _first_0==null ) _first_0 = REAL54;


                     helper.checkType(REAL54); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "type"


    public static class arg_return extends TreeRuleReturnScope {
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "arg"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:169:1: arg : ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) ) ;
    public final CtrlChecker.arg_return arg() throws RecognitionException {
        CtrlChecker.arg_return retval = new CtrlChecker.arg_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree ARG55=null;
        MyTree OUT56=null;
        MyTree ID57=null;
        MyTree DONT_CARE58=null;
        CtrlChecker.literal_return literal59 =null;


        MyTree ARG55_tree=null;
        MyTree OUT56_tree=null;
        MyTree ID57_tree=null;
        MyTree DONT_CARE58_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:170:2: ( ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:170:4: ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            ARG55=(MyTree)match(input,ARG,FOLLOW_ARG_in_arg887); 


            if ( _first_0==null ) _first_0 = ARG55;
            match(input, Token.DOWN, null); 
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:171:7: ( ( OUT )? ID | DONT_CARE | literal )
            int alt14=3;
            switch ( input.LA(1) ) {
            case ID:
            case OUT:
                {
                alt14=1;
                }
                break;
            case DONT_CARE:
                {
                alt14=2;
                }
                break;
            case FALSE:
            case INT_LIT:
            case REAL_LIT:
            case STRING_LIT:
            case TRUE:
                {
                alt14=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 14, 0, input);

                throw nvae;

            }

            switch (alt14) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:171:9: ( OUT )? ID
                    {
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:171:9: ( OUT )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);

                    if ( (LA13_0==OUT) ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:171:9: OUT
                            {
                            _last = (MyTree)input.LT(1);
                            OUT56=(MyTree)match(input,OUT,FOLLOW_OUT_in_arg898); 
                             
                            if ( _first_1==null ) _first_1 = OUT56;


                            retval.tree = (MyTree)_first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (MyTree)adaptor.getParent(retval.tree);

                            }
                            break;

                    }


                    _last = (MyTree)input.LT(1);
                    ID57=(MyTree)match(input,ID,FOLLOW_ID_in_arg901); 
                     
                    if ( _first_1==null ) _first_1 = ID57;


                     helper.checkVarArg(ARG55); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:172:9: DONT_CARE
                    {
                    _last = (MyTree)input.LT(1);
                    DONT_CARE58=(MyTree)match(input,DONT_CARE,FOLLOW_DONT_CARE_in_arg913); 
                     
                    if ( _first_1==null ) _first_1 = DONT_CARE58;


                     helper.checkDontCareArg(ARG55); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 3 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:173:9: literal
                    {
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_literal_in_arg925);
                    literal59=literal();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = literal59.tree;


                     helper.checkConstArg(ARG55); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;

            }


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = (MyTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (MyTree)adaptor.getParent(retval.tree);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "arg"


    public static class literal_return extends TreeRuleReturnScope {
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "literal"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:178:1: literal : ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT );
    public final CtrlChecker.literal_return literal() throws RecognitionException {
        CtrlChecker.literal_return retval = new CtrlChecker.literal_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree set60=null;

        MyTree set60_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:179:3: ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:
            {
            _last = (MyTree)input.LT(1);
            set60=(MyTree)input.LT(1);

            if ( input.LA(1)==FALSE||input.LA(1)==INT_LIT||input.LA(1)==REAL_LIT||(input.LA(1) >= STRING_LIT && input.LA(1) <= TRUE) ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }

            retval.tree = (MyTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (MyTree)adaptor.getParent(retval.tree);
             

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "literal"

    // Delegated rules


 

    public static final BitSet FOLLOW_PROGRAM_in_program56 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_recipes_in_program58 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_functions_in_program60 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_block_in_program62 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RECIPES_in_recipes84 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_recipe_in_recipes86 = new BitSet(new long[]{0x0002000000000008L});
    public static final BitSet FOLLOW_RECIPE_in_recipe103 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_recipe105 = new BitSet(new long[]{0x0000000040000800L});
    public static final BitSet FOLLOW_INT_LIT_in_recipe107 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_block_in_recipe127 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FUNCTIONS_in_functions158 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_function_in_functions160 = new BitSet(new long[]{0x0000000002000008L});
    public static final BitSet FOLLOW_FUNCTION_in_function183 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_function185 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_block_in_function204 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BLOCK_in_block242 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_block260 = new BitSet(new long[]{0x7C8001001000C858L});
    public static final BitSet FOLLOW_block_in_stat290 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_decl_in_stat296 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALAP_in_stat303 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat305 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WHILE_in_stat314 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat323 = new BitSet(new long[]{0x7C8001001000C850L});
    public static final BitSet FOLLOW_stat_in_stat341 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_UNTIL_in_stat365 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat374 = new BitSet(new long[]{0x7C8001001000C850L});
    public static final BitSet FOLLOW_stat_in_stat392 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TRY_in_stat416 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat434 = new BitSet(new long[]{0x7C8001001000C858L});
    public static final BitSet FOLLOW_stat_in_stat456 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IF_in_stat490 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat500 = new BitSet(new long[]{0x7C8001001000C850L});
    public static final BitSet FOLLOW_stat_in_stat519 = new BitSet(new long[]{0x7C8001001000C858L});
    public static final BitSet FOLLOW_stat_in_stat542 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CHOICE_in_stat576 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat594 = new BitSet(new long[]{0x7C8001001000C858L});
    public static final BitSet FOLLOW_stat_in_stat617 = new BitSet(new long[]{0x7C8001001000C858L});
    public static final BitSet FOLLOW_STAR_in_stat653 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat671 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_rule_in_stat693 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_stat699 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_stat711 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_stat723 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CALL_in_rule741 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rule745 = new BitSet(new long[]{0x0000000000000108L});
    public static final BitSet FOLLOW_ARGS_in_rule749 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_arg_in_rule751 = new BitSet(new long[]{0x0000000000000088L});
    public static final BitSet FOLLOW_VAR_in_var_decl770 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_type_in_var_decl772 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_var_decl789 = new BitSet(new long[]{0x0000000008000008L});
    public static final BitSet FOLLOW_NODE_in_type828 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_type838 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_type848 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_type856 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REAL_in_type867 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ARG_in_arg887 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_OUT_in_arg898 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_arg901 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DONT_CARE_in_arg913 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_literal_in_arg925 = new BitSet(new long[]{0x0000000000000008L});

}