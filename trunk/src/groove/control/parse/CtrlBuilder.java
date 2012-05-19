// $ANTLR 3.4 E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g 2012-01-16 17:57:04

package groove.control.parse;
import groove.control.*;
import groove.control.CtrlCall.Kind;
import groove.trans.Rule;
import groove.view.FormatError;
import java.util.Set;
import java.util.HashSet;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


@SuppressWarnings({"all", "warnings", "unchecked"})
public class CtrlBuilder extends TreeParser {
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


    public CtrlBuilder(TreeNodeStream input) {
        this(input, new RecognizerSharedState());
    }
    public CtrlBuilder(TreeNodeStream input, RecognizerSharedState state) {
        super(input, state);
    }

protected TreeAdaptor adaptor = new CommonTreeAdaptor();

public void setTreeAdaptor(TreeAdaptor adaptor) {
    this.adaptor = adaptor;
}
public TreeAdaptor getTreeAdaptor() {
    return adaptor;
}
    public String[] getTokenNames() { return CtrlBuilder.tokenNames; }
    public String getGrammarFileName() { return "E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g"; }


        /** Builder for control automata. */
        private CtrlFactory builder;
        /** Namespace used for building the automaton. */
        private Namespace namespace;
        /** Helper class for some final static semantic checks. */
        private CtrlHelper helper;

        /**
         * Runs the builder on a given, checked syntax tree.
         */
        public CtrlAut run(MyTree tree, Namespace namespace) throws RecognitionException {
            this.builder = CtrlFactory.instance();
            this.namespace = namespace;
            this.helper = new CtrlHelper(this, namespace, null);
            MyTreeAdaptor treeAdaptor = new MyTreeAdaptor();
            setTreeAdaptor(treeAdaptor);
            setTreeNodeStream(treeAdaptor.createTreeNodeStream(tree));
            return program().aut;
        }
        
        public List<FormatError> getErrors() {
            return this.helper.getErrors();
        }


    public static class program_return extends TreeRuleReturnScope {
        public CtrlAut aut;
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "program"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:46:1: program returns [ CtrlAut aut ] : ^( PROGRAM recipes functions block ) ;
    public final CtrlBuilder.program_return program() throws RecognitionException {
        CtrlBuilder.program_return retval = new CtrlBuilder.program_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree PROGRAM1=null;
        CtrlBuilder.recipes_return recipes2 =null;

        CtrlBuilder.functions_return functions3 =null;

        CtrlBuilder.block_return block4 =null;


        MyTree PROGRAM1_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:47:3: ( ^( PROGRAM recipes functions block ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:47:5: ^( PROGRAM recipes functions block )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            PROGRAM1=(MyTree)match(input,PROGRAM,FOLLOW_PROGRAM_in_program59); 


            if ( _first_0==null ) _first_0 = PROGRAM1;
            match(input, Token.DOWN, null); 
            _last = (MyTree)input.LT(1);
            pushFollow(FOLLOW_recipes_in_program61);
            recipes2=recipes();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = recipes2.tree;


            _last = (MyTree)input.LT(1);
            pushFollow(FOLLOW_functions_in_program63);
            functions3=functions();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = functions3.tree;


            _last = (MyTree)input.LT(1);
            pushFollow(FOLLOW_block_in_program65);
            block4=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block4.tree;


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


             if ((block4!=null?((MyTree)block4.tree):null).getChildCount() == 0) {
                      retval.aut = null;
                  } else {
                      retval.aut = (block4!=null?block4.aut:null);
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


    public static class functions_return extends TreeRuleReturnScope {
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "functions"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:56:1: functions : ^( FUNCTIONS ( function )* ) ;
    public final CtrlBuilder.functions_return functions() throws RecognitionException {
        CtrlBuilder.functions_return retval = new CtrlBuilder.functions_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree FUNCTIONS5=null;
        CtrlBuilder.function_return function6 =null;


        MyTree FUNCTIONS5_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:57:3: ( ^( FUNCTIONS ( function )* ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:57:5: ^( FUNCTIONS ( function )* )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            FUNCTIONS5=(MyTree)match(input,FUNCTIONS,FOLLOW_FUNCTIONS_in_functions86); 


            if ( _first_0==null ) _first_0 = FUNCTIONS5;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:57:17: ( function )*
                loop1:
                do {
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( (LA1_0==FUNCTION) ) {
                        alt1=1;
                    }


                    switch (alt1) {
                	case 1 :
                	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:57:17: function
                	    {
                	    _last = (MyTree)input.LT(1);
                	    pushFollow(FOLLOW_function_in_functions88);
                	    function6=function();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = function6.tree;


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
    // $ANTLR end "functions"


    public static class function_return extends TreeRuleReturnScope {
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "function"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:59:1: function : ^( FUNCTION ID block ) ;
    public final CtrlBuilder.function_return function() throws RecognitionException {
        CtrlBuilder.function_return retval = new CtrlBuilder.function_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree FUNCTION7=null;
        MyTree ID8=null;
        CtrlBuilder.block_return block9 =null;


        MyTree FUNCTION7_tree=null;
        MyTree ID8_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:60:3: ( ^( FUNCTION ID block ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:60:5: ^( FUNCTION ID block )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            FUNCTION7=(MyTree)match(input,FUNCTION,FOLLOW_FUNCTION_in_function101); 


            if ( _first_0==null ) _first_0 = FUNCTION7;
            match(input, Token.DOWN, null); 
            _last = (MyTree)input.LT(1);
            ID8=(MyTree)match(input,ID,FOLLOW_ID_in_function103); 
             
            if ( _first_1==null ) _first_1 = ID8;


            _last = (MyTree)input.LT(1);
            pushFollow(FOLLOW_block_in_function105);
            block9=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block9.tree;


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


             namespace.addBody((ID8!=null?ID8.getText():null), (block9!=null?block9.aut:null)); 

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


    public static class recipes_return extends TreeRuleReturnScope {
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "recipes"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:64:1: recipes : ^( RECIPES ( recipe )* ) ;
    public final CtrlBuilder.recipes_return recipes() throws RecognitionException {
        CtrlBuilder.recipes_return retval = new CtrlBuilder.recipes_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree RECIPES10=null;
        CtrlBuilder.recipe_return recipe11 =null;


        MyTree RECIPES10_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:65:3: ( ^( RECIPES ( recipe )* ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:65:5: ^( RECIPES ( recipe )* )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            RECIPES10=(MyTree)match(input,RECIPES,FOLLOW_RECIPES_in_recipes128); 


            if ( _first_0==null ) _first_0 = RECIPES10;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:65:15: ( recipe )*
                loop2:
                do {
                    int alt2=2;
                    int LA2_0 = input.LA(1);

                    if ( (LA2_0==RECIPE) ) {
                        alt2=1;
                    }


                    switch (alt2) {
                	case 1 :
                	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:65:15: recipe
                	    {
                	    _last = (MyTree)input.LT(1);
                	    pushFollow(FOLLOW_recipe_in_recipes130);
                	    recipe11=recipe();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = recipe11.tree;


                	    retval.tree = (MyTree)_first_0;
                	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                	        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                	    }
                	    break;

                	default :
                	    break loop2;
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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:67:1: recipe : ^( RECIPE ID ( INT_LIT )? block ) ;
    public final CtrlBuilder.recipe_return recipe() throws RecognitionException {
        CtrlBuilder.recipe_return retval = new CtrlBuilder.recipe_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree RECIPE12=null;
        MyTree ID13=null;
        MyTree INT_LIT14=null;
        CtrlBuilder.block_return block15 =null;


        MyTree RECIPE12_tree=null;
        MyTree ID13_tree=null;
        MyTree INT_LIT14_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:68:3: ( ^( RECIPE ID ( INT_LIT )? block ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:68:5: ^( RECIPE ID ( INT_LIT )? block )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            RECIPE12=(MyTree)match(input,RECIPE,FOLLOW_RECIPE_in_recipe143); 


            if ( _first_0==null ) _first_0 = RECIPE12;
            match(input, Token.DOWN, null); 
            _last = (MyTree)input.LT(1);
            ID13=(MyTree)match(input,ID,FOLLOW_ID_in_recipe145); 
             
            if ( _first_1==null ) _first_1 = ID13;


            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:68:17: ( INT_LIT )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==INT_LIT) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:68:17: INT_LIT
                    {
                    _last = (MyTree)input.LT(1);
                    INT_LIT14=(MyTree)match(input,INT_LIT,FOLLOW_INT_LIT_in_recipe147); 
                     
                    if ( _first_1==null ) _first_1 = INT_LIT14;


                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;

            }


            _last = (MyTree)input.LT(1);
            pushFollow(FOLLOW_block_in_recipe150);
            block15=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block15.tree;


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


             helper.checkRecipeBody(RECIPE12, (ID13!=null?ID13.getText():null), (block15!=null?block15.aut:null));
                  namespace.addBody((ID13!=null?ID13.getText():null), (block15!=null?block15.aut:null));
                

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


    public static class block_return extends TreeRuleReturnScope {
        public CtrlAut aut;
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "block"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:74:1: block returns [ CtrlAut aut ] : ^( BLOCK ( stat )* ) ;
    public final CtrlBuilder.block_return block() throws RecognitionException {
        CtrlBuilder.block_return retval = new CtrlBuilder.block_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree BLOCK16=null;
        CtrlBuilder.stat_return stat17 =null;


        MyTree BLOCK16_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:75:3: ( ^( BLOCK ( stat )* ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:75:5: ^( BLOCK ( stat )* )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            BLOCK16=(MyTree)match(input,BLOCK,FOLLOW_BLOCK_in_block176); 


            if ( _first_0==null ) _first_0 = BLOCK16;
             retval.aut = builder.buildTrue(); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:77:8: ( stat )*
                loop4:
                do {
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==ALAP||LA4_0==ANY||LA4_0==BLOCK||(LA4_0 >= CALL && LA4_0 <= CHOICE)||LA4_0==IF||LA4_0==OTHER||LA4_0==STAR||(LA4_0 >= TRUE && LA4_0 <= WHILE)) ) {
                        alt4=1;
                    }


                    switch (alt4) {
                	case 1 :
                	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:77:10: stat
                	    {
                	    _last = (MyTree)input.LT(1);
                	    pushFollow(FOLLOW_stat_in_block196);
                	    stat17=stat();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = stat17.tree;


                	     retval.aut = builder.buildSeq(retval.aut, (stat17!=null?stat17.aut:null)); 

                	    retval.tree = (MyTree)_first_0;
                	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                	        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                	    }
                	    break;

                	default :
                	    break loop4;
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
    // $ANTLR end "block"


    public static class stat_return extends TreeRuleReturnScope {
        public CtrlAut aut;
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "stat"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:83:1: stat returns [ CtrlAut aut ] : ( block | var_decl | ^( ALAP s= stat ) | ^( WHILE c= stat s= stat ) | ^( UNTIL c= stat s= stat ) | ^( TRY s1= stat (s2= stat )? ) | ^( IF c= stat s1= stat (s2= stat )? ) | ^( CHOICE s1= stat (s2= stat )* ) | ^( STAR s= stat ) | rule | ANY | OTHER | TRUE );
    public final CtrlBuilder.stat_return stat() throws RecognitionException {
        CtrlBuilder.stat_return retval = new CtrlBuilder.stat_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree ALAP20=null;
        MyTree WHILE21=null;
        MyTree UNTIL22=null;
        MyTree TRY23=null;
        MyTree IF24=null;
        MyTree CHOICE25=null;
        MyTree STAR26=null;
        MyTree ANY28=null;
        MyTree OTHER29=null;
        MyTree TRUE30=null;
        CtrlBuilder.stat_return s =null;

        CtrlBuilder.stat_return c =null;

        CtrlBuilder.stat_return s1 =null;

        CtrlBuilder.stat_return s2 =null;

        CtrlBuilder.block_return block18 =null;

        CtrlBuilder.var_decl_return var_decl19 =null;

        CtrlBuilder.rule_return rule27 =null;


        MyTree ALAP20_tree=null;
        MyTree WHILE21_tree=null;
        MyTree UNTIL22_tree=null;
        MyTree TRY23_tree=null;
        MyTree IF24_tree=null;
        MyTree CHOICE25_tree=null;
        MyTree STAR26_tree=null;
        MyTree ANY28_tree=null;
        MyTree OTHER29_tree=null;
        MyTree TRUE30_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:84:3: ( block | var_decl | ^( ALAP s= stat ) | ^( WHILE c= stat s= stat ) | ^( UNTIL c= stat s= stat ) | ^( TRY s1= stat (s2= stat )? ) | ^( IF c= stat s1= stat (s2= stat )? ) | ^( CHOICE s1= stat (s2= stat )* ) | ^( STAR s= stat ) | rule | ANY | OTHER | TRUE )
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:84:5: block
                    {
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_block_in_stat241);
                    block18=block();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = block18.tree;


                     retval.aut = (block18!=null?block18.aut:null); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:86:5: var_decl
                    {
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_var_decl_in_stat253);
                    var_decl19=var_decl();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = var_decl19.tree;


                     retval.aut = builder.buildTrue(); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 3 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:88:5: ^( ALAP s= stat )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    ALAP20=(MyTree)match(input,ALAP,FOLLOW_ALAP_in_stat266); 


                    if ( _first_0==null ) _first_0 = ALAP20;
                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat270);
                    s=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                     retval.aut = builder.buildAlap((s!=null?s.aut:null)); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 4 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:90:5: ^( WHILE c= stat s= stat )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    WHILE21=(MyTree)match(input,WHILE,FOLLOW_WHILE_in_stat284); 


                    if ( _first_0==null ) _first_0 = WHILE21;
                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat288);
                    c=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = c.tree;


                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat292);
                    s=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                     retval.aut = builder.buildWhileDo((c!=null?c.aut:null), (s!=null?s.aut:null)); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 5 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:92:5: ^( UNTIL c= stat s= stat )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    UNTIL22=(MyTree)match(input,UNTIL,FOLLOW_UNTIL_in_stat306); 


                    if ( _first_0==null ) _first_0 = UNTIL22;
                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat310);
                    c=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = c.tree;


                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat314);
                    s=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                     retval.aut = builder.buildUntilDo((c!=null?c.aut:null), (s!=null?s.aut:null)); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 6 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:94:5: ^( TRY s1= stat (s2= stat )? )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    TRY23=(MyTree)match(input,TRY,FOLLOW_TRY_in_stat328); 


                    if ( _first_0==null ) _first_0 = TRY23;
                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat332);
                    s1=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s1.tree;


                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:94:19: (s2= stat )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0==ALAP||LA5_0==ANY||LA5_0==BLOCK||(LA5_0 >= CALL && LA5_0 <= CHOICE)||LA5_0==IF||LA5_0==OTHER||LA5_0==STAR||(LA5_0 >= TRUE && LA5_0 <= WHILE)) ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:94:20: s2= stat
                            {
                            _last = (MyTree)input.LT(1);
                            pushFollow(FOLLOW_stat_in_stat337);
                            s2=stat();

                            state._fsp--;

                             
                            if ( _first_1==null ) _first_1 = s2.tree;


                            retval.tree = (MyTree)_first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (MyTree)adaptor.getParent(retval.tree);

                            }
                            break;

                    }


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                     retval.aut = builder.buildTryElse((s1!=null?s1.aut:null), (s2!=null?s2.aut:null)); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 7 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:96:5: ^( IF c= stat s1= stat (s2= stat )? )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    IF24=(MyTree)match(input,IF,FOLLOW_IF_in_stat353); 


                    if ( _first_0==null ) _first_0 = IF24;
                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat357);
                    c=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = c.tree;


                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat361);
                    s1=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s1.tree;


                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:96:25: (s2= stat )?
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0==ALAP||LA6_0==ANY||LA6_0==BLOCK||(LA6_0 >= CALL && LA6_0 <= CHOICE)||LA6_0==IF||LA6_0==OTHER||LA6_0==STAR||(LA6_0 >= TRUE && LA6_0 <= WHILE)) ) {
                        alt6=1;
                    }
                    switch (alt6) {
                        case 1 :
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:96:26: s2= stat
                            {
                            _last = (MyTree)input.LT(1);
                            pushFollow(FOLLOW_stat_in_stat366);
                            s2=stat();

                            state._fsp--;

                             
                            if ( _first_1==null ) _first_1 = s2.tree;


                            retval.tree = (MyTree)_first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (MyTree)adaptor.getParent(retval.tree);

                            }
                            break;

                    }


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                     retval.aut = builder.buildIfThenElse((c!=null?c.aut:null), (s1!=null?s1.aut:null), (s2!=null?s2.aut:null)); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 8 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:98:5: ^( CHOICE s1= stat (s2= stat )* )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    CHOICE25=(MyTree)match(input,CHOICE,FOLLOW_CHOICE_in_stat383); 


                    if ( _first_0==null ) _first_0 = CHOICE25;
                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat395);
                    s1=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s1.tree;


                     retval.aut = (s1!=null?s1.aut:null); 

                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:101:8: (s2= stat )*
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( (LA7_0==ALAP||LA7_0==ANY||LA7_0==BLOCK||(LA7_0 >= CALL && LA7_0 <= CHOICE)||LA7_0==IF||LA7_0==OTHER||LA7_0==STAR||(LA7_0 >= TRUE && LA7_0 <= WHILE)) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:101:10: s2= stat
                    	    {
                    	    _last = (MyTree)input.LT(1);
                    	    pushFollow(FOLLOW_stat_in_stat417);
                    	    s2=stat();

                    	    state._fsp--;

                    	     
                    	    if ( _first_1==null ) _first_1 = s2.tree;


                    	     retval.aut = builder.buildOr(retval.aut, (s2!=null?s2.aut:null)); 

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


                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 9 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:105:5: ^( STAR s= stat )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    STAR26=(MyTree)match(input,STAR,FOLLOW_STAR_in_stat452); 


                    if ( _first_0==null ) _first_0 = STAR26;
                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat456);
                    s=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                     retval.aut = builder.buildStar((s!=null?s.aut:null)); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 10 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:107:5: rule
                    {
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_rule_in_stat469);
                    rule27=rule();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = rule27.tree;


                     retval.aut = builder.buildCall((rule27!=null?((MyTree)rule27.tree):null).getCtrlCall(), namespace); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 11 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:109:5: ANY
                    {
                    _last = (MyTree)input.LT(1);
                    ANY28=(MyTree)match(input,ANY,FOLLOW_ANY_in_stat481); 
                     
                    if ( _first_0==null ) _first_0 = ANY28;


                     retval.aut = builder.buildAny(namespace); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 12 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:111:5: OTHER
                    {
                    _last = (MyTree)input.LT(1);
                    OTHER29=(MyTree)match(input,OTHER,FOLLOW_OTHER_in_stat493); 
                     
                    if ( _first_0==null ) _first_0 = OTHER29;


                     retval.aut = builder.buildOther(namespace); 
                        

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 13 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:114:5: TRUE
                    {
                    _last = (MyTree)input.LT(1);
                    TRUE30=(MyTree)match(input,TRUE,FOLLOW_TRUE_in_stat505); 
                     
                    if ( _first_0==null ) _first_0 = TRUE30;


                     retval.aut = builder.buildTrue(); 

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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:118:1: rule : ^( CALL ID ( ^( ARGS ( arg )* ) )? ) ;
    public final CtrlBuilder.rule_return rule() throws RecognitionException {
        CtrlBuilder.rule_return retval = new CtrlBuilder.rule_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree CALL31=null;
        MyTree ID32=null;
        MyTree ARGS33=null;
        CtrlBuilder.arg_return arg34 =null;


        MyTree CALL31_tree=null;
        MyTree ID32_tree=null;
        MyTree ARGS33_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:119:3: ( ^( CALL ID ( ^( ARGS ( arg )* ) )? ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:119:5: ^( CALL ID ( ^( ARGS ( arg )* ) )? )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            CALL31=(MyTree)match(input,CALL,FOLLOW_CALL_in_rule525); 


            if ( _first_0==null ) _first_0 = CALL31;
            match(input, Token.DOWN, null); 
            _last = (MyTree)input.LT(1);
            ID32=(MyTree)match(input,ID,FOLLOW_ID_in_rule527); 
             
            if ( _first_1==null ) _first_1 = ID32;


            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:119:15: ( ^( ARGS ( arg )* ) )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==ARGS) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:119:16: ^( ARGS ( arg )* )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_2 = _last;
                    MyTree _first_2 = null;
                    _last = (MyTree)input.LT(1);
                    ARGS33=(MyTree)match(input,ARGS,FOLLOW_ARGS_in_rule531); 


                    if ( _first_1==null ) _first_1 = ARGS33;
                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:119:23: ( arg )*
                        loop9:
                        do {
                            int alt9=2;
                            int LA9_0 = input.LA(1);

                            if ( (LA9_0==ARG) ) {
                                alt9=1;
                            }


                            switch (alt9) {
                        	case 1 :
                        	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:119:23: arg
                        	    {
                        	    _last = (MyTree)input.LT(1);
                        	    pushFollow(FOLLOW_arg_in_rule533);
                        	    arg34=arg();

                        	    state._fsp--;

                        	     
                        	    if ( _first_2==null ) _first_2 = arg34.tree;


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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:122:1: var_decl : ^( VAR type ( ID )+ ) ;
    public final CtrlBuilder.var_decl_return var_decl() throws RecognitionException {
        CtrlBuilder.var_decl_return retval = new CtrlBuilder.var_decl_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree VAR35=null;
        MyTree ID37=null;
        CtrlBuilder.type_return type36 =null;


        MyTree VAR35_tree=null;
        MyTree ID37_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:123:2: ( ^( VAR type ( ID )+ ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:123:4: ^( VAR type ( ID )+ )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            VAR35=(MyTree)match(input,VAR,FOLLOW_VAR_in_var_decl552); 


            if ( _first_0==null ) _first_0 = VAR35;
            match(input, Token.DOWN, null); 
            _last = (MyTree)input.LT(1);
            pushFollow(FOLLOW_type_in_var_decl554);
            type36=type();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = type36.tree;


            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:123:16: ( ID )+
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
            	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:123:16: ID
            	    {
            	    _last = (MyTree)input.LT(1);
            	    ID37=(MyTree)match(input,ID,FOLLOW_ID_in_var_decl556); 
            	     
            	    if ( _first_1==null ) _first_1 = ID37;


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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:126:1: type : ( NODE | BOOL | STRING | INT | REAL );
    public final CtrlBuilder.type_return type() throws RecognitionException {
        CtrlBuilder.type_return retval = new CtrlBuilder.type_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree set38=null;

        MyTree set38_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:127:3: ( NODE | BOOL | STRING | INT | REAL )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:
            {
            _last = (MyTree)input.LT(1);
            set38=(MyTree)input.LT(1);

            if ( input.LA(1)==BOOL||input.LA(1)==INT||input.LA(1)==NODE||input.LA(1)==REAL||input.LA(1)==STRING ) {
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
    // $ANTLR end "type"


    public static class arg_return extends TreeRuleReturnScope {
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "arg"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:130:1: arg : ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) ) ;
    public final CtrlBuilder.arg_return arg() throws RecognitionException {
        CtrlBuilder.arg_return retval = new CtrlBuilder.arg_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree ARG39=null;
        MyTree OUT40=null;
        MyTree ID41=null;
        MyTree DONT_CARE42=null;
        CtrlBuilder.literal_return literal43 =null;


        MyTree ARG39_tree=null;
        MyTree OUT40_tree=null;
        MyTree ID41_tree=null;
        MyTree DONT_CARE42_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:131:2: ( ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:131:4: ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            ARG39=(MyTree)match(input,ARG,FOLLOW_ARG_in_arg603); 


            if ( _first_0==null ) _first_0 = ARG39;
            match(input, Token.DOWN, null); 
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:131:11: ( ( OUT )? ID | DONT_CARE | literal )
            int alt13=3;
            switch ( input.LA(1) ) {
            case ID:
            case OUT:
                {
                alt13=1;
                }
                break;
            case DONT_CARE:
                {
                alt13=2;
                }
                break;
            case FALSE:
            case INT_LIT:
            case REAL_LIT:
            case STRING_LIT:
            case TRUE:
                {
                alt13=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;

            }

            switch (alt13) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:131:13: ( OUT )? ID
                    {
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:131:13: ( OUT )?
                    int alt12=2;
                    int LA12_0 = input.LA(1);

                    if ( (LA12_0==OUT) ) {
                        alt12=1;
                    }
                    switch (alt12) {
                        case 1 :
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:131:13: OUT
                            {
                            _last = (MyTree)input.LT(1);
                            OUT40=(MyTree)match(input,OUT,FOLLOW_OUT_in_arg607); 
                             
                            if ( _first_1==null ) _first_1 = OUT40;


                            retval.tree = (MyTree)_first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (MyTree)adaptor.getParent(retval.tree);

                            }
                            break;

                    }


                    _last = (MyTree)input.LT(1);
                    ID41=(MyTree)match(input,ID,FOLLOW_ID_in_arg610); 
                     
                    if ( _first_1==null ) _first_1 = ID41;


                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:131:23: DONT_CARE
                    {
                    _last = (MyTree)input.LT(1);
                    DONT_CARE42=(MyTree)match(input,DONT_CARE,FOLLOW_DONT_CARE_in_arg614); 
                     
                    if ( _first_1==null ) _first_1 = DONT_CARE42;


                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 3 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:131:35: literal
                    {
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_literal_in_arg618);
                    literal43=literal();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = literal43.tree;


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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:134:1: literal : ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT );
    public final CtrlBuilder.literal_return literal() throws RecognitionException {
        CtrlBuilder.literal_return retval = new CtrlBuilder.literal_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree set44=null;

        MyTree set44_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:135:3: ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:
            {
            _last = (MyTree)input.LT(1);
            set44=(MyTree)input.LT(1);

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


 

    public static final BitSet FOLLOW_PROGRAM_in_program59 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_recipes_in_program61 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_functions_in_program63 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_block_in_program65 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FUNCTIONS_in_functions86 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_function_in_functions88 = new BitSet(new long[]{0x0000000002000008L});
    public static final BitSet FOLLOW_FUNCTION_in_function101 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_function103 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_block_in_function105 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RECIPES_in_recipes128 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_recipe_in_recipes130 = new BitSet(new long[]{0x0002000000000008L});
    public static final BitSet FOLLOW_RECIPE_in_recipe143 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_recipe145 = new BitSet(new long[]{0x0000000040000800L});
    public static final BitSet FOLLOW_INT_LIT_in_recipe147 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_block_in_recipe150 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BLOCK_in_block176 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_block196 = new BitSet(new long[]{0x7C8001001000C858L});
    public static final BitSet FOLLOW_block_in_stat241 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_decl_in_stat253 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALAP_in_stat266 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat270 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WHILE_in_stat284 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat288 = new BitSet(new long[]{0x7C8001001000C850L});
    public static final BitSet FOLLOW_stat_in_stat292 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_UNTIL_in_stat306 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat310 = new BitSet(new long[]{0x7C8001001000C850L});
    public static final BitSet FOLLOW_stat_in_stat314 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TRY_in_stat328 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat332 = new BitSet(new long[]{0x7C8001001000C858L});
    public static final BitSet FOLLOW_stat_in_stat337 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IF_in_stat353 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat357 = new BitSet(new long[]{0x7C8001001000C850L});
    public static final BitSet FOLLOW_stat_in_stat361 = new BitSet(new long[]{0x7C8001001000C858L});
    public static final BitSet FOLLOW_stat_in_stat366 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CHOICE_in_stat383 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat395 = new BitSet(new long[]{0x7C8001001000C858L});
    public static final BitSet FOLLOW_stat_in_stat417 = new BitSet(new long[]{0x7C8001001000C858L});
    public static final BitSet FOLLOW_STAR_in_stat452 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat456 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_rule_in_stat469 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_stat481 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_stat493 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_stat505 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CALL_in_rule525 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rule527 = new BitSet(new long[]{0x0000000000000108L});
    public static final BitSet FOLLOW_ARGS_in_rule531 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_arg_in_rule533 = new BitSet(new long[]{0x0000000000000088L});
    public static final BitSet FOLLOW_VAR_in_var_decl552 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_type_in_var_decl554 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_var_decl556 = new BitSet(new long[]{0x0000000008000008L});
    public static final BitSet FOLLOW_ARG_in_arg603 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_OUT_in_arg607 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_arg610 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DONT_CARE_in_arg614 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_literal_in_arg618 = new BitSet(new long[]{0x0000000000000008L});

}