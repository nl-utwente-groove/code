// $ANTLR 3.4 D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g 2014-01-05 18:48:12

package groove.control.parse;
import groove.control.*;
import groove.grammar.model.FormatErrorSet;
import groove.util.antlr.ParseTreeAdaptor;
import groove.util.antlr.ParseInfo;
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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ALAP", "AMP", "ANY", "ARG", "ARGS", "ASTERISK", "BAR", "BLOCK", "BOOL", "BSLASH", "CALL", "CHOICE", "COMMA", "DO", "DONT_CARE", "DOT", "DO_UNTIL", "DO_WHILE", "ELSE", "EscapeSequence", "FALSE", "FUNCTION", "FUNCTIONS", "ID", "IF", "IMPORT", "INT", "INT_LIT", "IntegerNumber", "LCURLY", "LPAR", "MINUS", "ML_COMMENT", "NODE", "NOT", "NonIntegerNumber", "OR", "OTHER", "OUT", "PACKAGE", "PLUS", "PRIORITY", "PROGRAM", "QUOTE", "RCURLY", "REAL", "REAL_LIT", "RECIPE", "RECIPES", "RPAR", "SEMI", "SHARP", "SL_COMMENT", "STAR", "STRING", "STRING_LIT", "TRUE", "TRY", "UNTIL", "VAR", "WHILE", "WS"
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
    public static final int IMPORT=29;
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
    public static final int PACKAGE=43;
    public static final int PLUS=44;
    public static final int PRIORITY=45;
    public static final int PROGRAM=46;
    public static final int QUOTE=47;
    public static final int RCURLY=48;
    public static final int REAL=49;
    public static final int REAL_LIT=50;
    public static final int RECIPE=51;
    public static final int RECIPES=52;
    public static final int RPAR=53;
    public static final int SEMI=54;
    public static final int SHARP=55;
    public static final int SL_COMMENT=56;
    public static final int STAR=57;
    public static final int STRING=58;
    public static final int STRING_LIT=59;
    public static final int TRUE=60;
    public static final int TRY=61;
    public static final int UNTIL=62;
    public static final int VAR=63;
    public static final int WHILE=64;
    public static final int WS=65;

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
    public String getGrammarFileName() { return "D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g"; }


        /** Builder for control automata. */
        private CtrlFactory builder;
        /** Namespace used for building the automaton. */
        private Namespace namespace;
        /** Helper class for some final static semantic checks. */
        private CtrlHelper helper;

        /** Initialises the internal variables, based on the given name space. */
        public void initialise(ParseInfo namespace) {
            this.builder = CtrlFactory.instance();
            this.namespace = (Namespace) namespace;
            this.helper = new CtrlHelper(this.namespace);
        }
        
        /**
         * Runs the builder on a given, checked syntax tree.
         */
        public CtrlAut run(NewCtrlTree tree, Namespace namespace) throws RecognitionException {
            this.builder = CtrlFactory.instance();
            this.namespace = namespace;
            this.helper = new CtrlHelper(namespace);
            ParseTreeAdaptor treeAdaptor = new ParseTreeAdaptor(new NewCtrlTree());
            setTreeAdaptor(treeAdaptor);
            setTreeNodeStream(treeAdaptor.createTreeNodeStream(tree));
            CtrlAut result = program().aut;
            return result == null ? null : result.clone(namespace.getFullName());
        }


    public static class program_return extends TreeRuleReturnScope {
        public CtrlAut aut;
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "program"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:50:1: program returns [ CtrlAut aut ] : ^( PROGRAM package_decl ( import_decl )* functions recipes block ) ;
    public final CtrlBuilder.program_return program() throws RecognitionException {
        CtrlBuilder.program_return retval = new CtrlBuilder.program_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        NewCtrlTree _first_0 = null;
        NewCtrlTree _last = null;

        NewCtrlTree PROGRAM1=null;
        CtrlBuilder.package_decl_return package_decl2 =null;

        CtrlBuilder.import_decl_return import_decl3 =null;

        CtrlBuilder.functions_return functions4 =null;

        CtrlBuilder.recipes_return recipes5 =null;

        CtrlBuilder.block_return block6 =null;


        NewCtrlTree PROGRAM1_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:51:3: ( ^( PROGRAM package_decl ( import_decl )* functions recipes block ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:51:5: ^( PROGRAM package_decl ( import_decl )* functions recipes block )
            {
            _last = (NewCtrlTree)input.LT(1);
            {
            NewCtrlTree _save_last_1 = _last;
            NewCtrlTree _first_1 = null;
            _last = (NewCtrlTree)input.LT(1);
            PROGRAM1=(NewCtrlTree)match(input,PROGRAM,FOLLOW_PROGRAM_in_program59); 


            if ( _first_0==null ) _first_0 = PROGRAM1;
            match(input, Token.DOWN, null); 
            _last = (NewCtrlTree)input.LT(1);
            pushFollow(FOLLOW_package_decl_in_program61);
            package_decl2=package_decl();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = package_decl2.tree;


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:51:28: ( import_decl )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==IMPORT) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:51:28: import_decl
            	    {
            	    _last = (NewCtrlTree)input.LT(1);
            	    pushFollow(FOLLOW_import_decl_in_program63);
            	    import_decl3=import_decl();

            	    state._fsp--;

            	     
            	    if ( _first_1==null ) _first_1 = import_decl3.tree;


            	    retval.tree = (NewCtrlTree)_first_0;
            	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
            	        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            _last = (NewCtrlTree)input.LT(1);
            pushFollow(FOLLOW_functions_in_program66);
            functions4=functions();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = functions4.tree;


            _last = (NewCtrlTree)input.LT(1);
            pushFollow(FOLLOW_recipes_in_program68);
            recipes5=recipes();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = recipes5.tree;


            _last = (NewCtrlTree)input.LT(1);
            pushFollow(FOLLOW_block_in_program70);
            block6=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block6.tree;


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


             // at least one child due to closing RCURLY
                  if ((block6!=null?((NewCtrlTree)block6.tree):null).getChildCount() == 1) {
                      retval.aut = null;
                  } else {
                      retval.aut = (block6!=null?block6.aut:null);
                  }
                

            retval.tree = (NewCtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

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


    public static class package_decl_return extends TreeRuleReturnScope {
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "package_decl"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:61:1: package_decl : ^( PACKAGE ID SEMI ) ;
    public final CtrlBuilder.package_decl_return package_decl() throws RecognitionException {
        CtrlBuilder.package_decl_return retval = new CtrlBuilder.package_decl_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        NewCtrlTree _first_0 = null;
        NewCtrlTree _last = null;

        NewCtrlTree PACKAGE7=null;
        NewCtrlTree ID8=null;
        NewCtrlTree SEMI9=null;

        NewCtrlTree PACKAGE7_tree=null;
        NewCtrlTree ID8_tree=null;
        NewCtrlTree SEMI9_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:62:3: ( ^( PACKAGE ID SEMI ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:62:5: ^( PACKAGE ID SEMI )
            {
            _last = (NewCtrlTree)input.LT(1);
            {
            NewCtrlTree _save_last_1 = _last;
            NewCtrlTree _first_1 = null;
            _last = (NewCtrlTree)input.LT(1);
            PACKAGE7=(NewCtrlTree)match(input,PACKAGE,FOLLOW_PACKAGE_in_package_decl91); 


            if ( _first_0==null ) _first_0 = PACKAGE7;
            match(input, Token.DOWN, null); 
            _last = (NewCtrlTree)input.LT(1);
            ID8=(NewCtrlTree)match(input,ID,FOLLOW_ID_in_package_decl93); 
             
            if ( _first_1==null ) _first_1 = ID8;


            _last = (NewCtrlTree)input.LT(1);
            SEMI9=(NewCtrlTree)match(input,SEMI,FOLLOW_SEMI_in_package_decl95); 
             
            if ( _first_1==null ) _first_1 = SEMI9;


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = (NewCtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

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
    // $ANTLR end "package_decl"


    public static class import_decl_return extends TreeRuleReturnScope {
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "import_decl"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:65:1: import_decl : ^( IMPORT ID ) ;
    public final CtrlBuilder.import_decl_return import_decl() throws RecognitionException {
        CtrlBuilder.import_decl_return retval = new CtrlBuilder.import_decl_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        NewCtrlTree _first_0 = null;
        NewCtrlTree _last = null;

        NewCtrlTree IMPORT10=null;
        NewCtrlTree ID11=null;

        NewCtrlTree IMPORT10_tree=null;
        NewCtrlTree ID11_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:66:3: ( ^( IMPORT ID ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:66:5: ^( IMPORT ID )
            {
            _last = (NewCtrlTree)input.LT(1);
            {
            NewCtrlTree _save_last_1 = _last;
            NewCtrlTree _first_1 = null;
            _last = (NewCtrlTree)input.LT(1);
            IMPORT10=(NewCtrlTree)match(input,IMPORT,FOLLOW_IMPORT_in_import_decl112); 


            if ( _first_0==null ) _first_0 = IMPORT10;
            match(input, Token.DOWN, null); 
            _last = (NewCtrlTree)input.LT(1);
            ID11=(NewCtrlTree)match(input,ID,FOLLOW_ID_in_import_decl114); 
             
            if ( _first_1==null ) _first_1 = ID11;


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = (NewCtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

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
    // $ANTLR end "import_decl"


    public static class functions_return extends TreeRuleReturnScope {
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "functions"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:69:1: functions : ^( FUNCTIONS ( function )* ) ;
    public final CtrlBuilder.functions_return functions() throws RecognitionException {
        CtrlBuilder.functions_return retval = new CtrlBuilder.functions_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        NewCtrlTree _first_0 = null;
        NewCtrlTree _last = null;

        NewCtrlTree FUNCTIONS12=null;
        CtrlBuilder.function_return function13 =null;


        NewCtrlTree FUNCTIONS12_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:70:3: ( ^( FUNCTIONS ( function )* ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:70:5: ^( FUNCTIONS ( function )* )
            {
            _last = (NewCtrlTree)input.LT(1);
            {
            NewCtrlTree _save_last_1 = _last;
            NewCtrlTree _first_1 = null;
            _last = (NewCtrlTree)input.LT(1);
            FUNCTIONS12=(NewCtrlTree)match(input,FUNCTIONS,FOLLOW_FUNCTIONS_in_functions129); 


            if ( _first_0==null ) _first_0 = FUNCTIONS12;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:70:17: ( function )*
                loop2:
                do {
                    int alt2=2;
                    int LA2_0 = input.LA(1);

                    if ( (LA2_0==FUNCTION) ) {
                        alt2=1;
                    }


                    switch (alt2) {
                	case 1 :
                	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:70:17: function
                	    {
                	    _last = (NewCtrlTree)input.LT(1);
                	    pushFollow(FOLLOW_function_in_functions131);
                	    function13=function();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = function13.tree;


                	    retval.tree = (NewCtrlTree)_first_0;
                	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                	        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

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


            retval.tree = (NewCtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

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
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "function"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:73:1: function : ^( FUNCTION ID block ) ;
    public final CtrlBuilder.function_return function() throws RecognitionException {
        CtrlBuilder.function_return retval = new CtrlBuilder.function_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        NewCtrlTree _first_0 = null;
        NewCtrlTree _last = null;

        NewCtrlTree FUNCTION14=null;
        NewCtrlTree ID15=null;
        CtrlBuilder.block_return block16 =null;


        NewCtrlTree FUNCTION14_tree=null;
        NewCtrlTree ID15_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:74:3: ( ^( FUNCTION ID block ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:74:5: ^( FUNCTION ID block )
            {
            _last = (NewCtrlTree)input.LT(1);
            {
            NewCtrlTree _save_last_1 = _last;
            NewCtrlTree _first_1 = null;
            _last = (NewCtrlTree)input.LT(1);
            FUNCTION14=(NewCtrlTree)match(input,FUNCTION,FOLLOW_FUNCTION_in_function147); 


            if ( _first_0==null ) _first_0 = FUNCTION14;
            match(input, Token.DOWN, null); 
            _last = (NewCtrlTree)input.LT(1);
            ID15=(NewCtrlTree)match(input,ID,FOLLOW_ID_in_function149); 
             
            if ( _first_1==null ) _first_1 = ID15;


            _last = (NewCtrlTree)input.LT(1);
            pushFollow(FOLLOW_block_in_function151);
            block16=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block16.tree;


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


             namespace.addBody(helper.qualify((ID15!=null?ID15.getText():null)), (block16!=null?block16.aut:null)); 

            retval.tree = (NewCtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

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
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "recipes"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:78:1: recipes : ^( RECIPES ( recipe )* ) ;
    public final CtrlBuilder.recipes_return recipes() throws RecognitionException {
        CtrlBuilder.recipes_return retval = new CtrlBuilder.recipes_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        NewCtrlTree _first_0 = null;
        NewCtrlTree _last = null;

        NewCtrlTree RECIPES17=null;
        CtrlBuilder.recipe_return recipe18 =null;


        NewCtrlTree RECIPES17_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:79:3: ( ^( RECIPES ( recipe )* ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:79:5: ^( RECIPES ( recipe )* )
            {
            _last = (NewCtrlTree)input.LT(1);
            {
            NewCtrlTree _save_last_1 = _last;
            NewCtrlTree _first_1 = null;
            _last = (NewCtrlTree)input.LT(1);
            RECIPES17=(NewCtrlTree)match(input,RECIPES,FOLLOW_RECIPES_in_recipes174); 


            if ( _first_0==null ) _first_0 = RECIPES17;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:79:15: ( recipe )*
                loop3:
                do {
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0==RECIPE) ) {
                        alt3=1;
                    }


                    switch (alt3) {
                	case 1 :
                	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:79:15: recipe
                	    {
                	    _last = (NewCtrlTree)input.LT(1);
                	    pushFollow(FOLLOW_recipe_in_recipes176);
                	    recipe18=recipe();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = recipe18.tree;


                	    retval.tree = (NewCtrlTree)_first_0;
                	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                	        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

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


            retval.tree = (NewCtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

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
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "recipe"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:82:1: recipe : ^( RECIPE ID ( INT_LIT )? block ) ;
    public final CtrlBuilder.recipe_return recipe() throws RecognitionException {
        CtrlBuilder.recipe_return retval = new CtrlBuilder.recipe_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        NewCtrlTree _first_0 = null;
        NewCtrlTree _last = null;

        NewCtrlTree RECIPE19=null;
        NewCtrlTree ID20=null;
        NewCtrlTree INT_LIT21=null;
        CtrlBuilder.block_return block22 =null;


        NewCtrlTree RECIPE19_tree=null;
        NewCtrlTree ID20_tree=null;
        NewCtrlTree INT_LIT21_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:83:3: ( ^( RECIPE ID ( INT_LIT )? block ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:83:5: ^( RECIPE ID ( INT_LIT )? block )
            {
            _last = (NewCtrlTree)input.LT(1);
            {
            NewCtrlTree _save_last_1 = _last;
            NewCtrlTree _first_1 = null;
            _last = (NewCtrlTree)input.LT(1);
            RECIPE19=(NewCtrlTree)match(input,RECIPE,FOLLOW_RECIPE_in_recipe192); 


            if ( _first_0==null ) _first_0 = RECIPE19;
            match(input, Token.DOWN, null); 
            _last = (NewCtrlTree)input.LT(1);
            ID20=(NewCtrlTree)match(input,ID,FOLLOW_ID_in_recipe194); 
             
            if ( _first_1==null ) _first_1 = ID20;


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:83:17: ( INT_LIT )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==INT_LIT) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:83:17: INT_LIT
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    INT_LIT21=(NewCtrlTree)match(input,INT_LIT,FOLLOW_INT_LIT_in_recipe196); 
                     
                    if ( _first_1==null ) _first_1 = INT_LIT21;


                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;

            }


            _last = (NewCtrlTree)input.LT(1);
            pushFollow(FOLLOW_block_in_recipe199);
            block22=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block22.tree;


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


             
                  String recipeName = helper.qualify((ID20!=null?ID20.getText():null));
                  helper.checkRecipeBody(RECIPE19, recipeName, (block22!=null?block22.aut:null));
                  namespace.addBody(recipeName, (block22!=null?block22.aut:null));
                

            retval.tree = (NewCtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

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
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "block"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:91:1: block returns [ CtrlAut aut ] : ^( BLOCK ( stat )* RCURLY ) ;
    public final CtrlBuilder.block_return block() throws RecognitionException {
        CtrlBuilder.block_return retval = new CtrlBuilder.block_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        NewCtrlTree _first_0 = null;
        NewCtrlTree _last = null;

        NewCtrlTree BLOCK23=null;
        NewCtrlTree RCURLY25=null;
        CtrlBuilder.stat_return stat24 =null;


        NewCtrlTree BLOCK23_tree=null;
        NewCtrlTree RCURLY25_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:92:3: ( ^( BLOCK ( stat )* RCURLY ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:92:5: ^( BLOCK ( stat )* RCURLY )
            {
            _last = (NewCtrlTree)input.LT(1);
            {
            NewCtrlTree _save_last_1 = _last;
            NewCtrlTree _first_1 = null;
            _last = (NewCtrlTree)input.LT(1);
            BLOCK23=(NewCtrlTree)match(input,BLOCK,FOLLOW_BLOCK_in_block225); 


            if ( _first_0==null ) _first_0 = BLOCK23;
             retval.aut = builder.buildTrue(); 

            match(input, Token.DOWN, null); 
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:94:8: ( stat )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==ALAP||LA5_0==ANY||LA5_0==BLOCK||(LA5_0 >= CALL && LA5_0 <= CHOICE)||LA5_0==IF||LA5_0==OTHER||LA5_0==SEMI||LA5_0==STAR||(LA5_0 >= TRUE && LA5_0 <= WHILE)) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:94:10: stat
            	    {
            	    _last = (NewCtrlTree)input.LT(1);
            	    pushFollow(FOLLOW_stat_in_block245);
            	    stat24=stat();

            	    state._fsp--;

            	     
            	    if ( _first_1==null ) _first_1 = stat24.tree;


            	     retval.aut = builder.buildSeq(retval.aut, (stat24!=null?stat24.aut:null)); 

            	    retval.tree = (NewCtrlTree)_first_0;
            	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
            	        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);


            _last = (NewCtrlTree)input.LT(1);
            RCURLY25=(NewCtrlTree)match(input,RCURLY,FOLLOW_RCURLY_in_block275); 
             
            if ( _first_1==null ) _first_1 = RCURLY25;


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = (NewCtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

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
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "stat"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:101:1: stat returns [ CtrlAut aut ] : ( ^( SEMI s= stat ) | block | var_decl | ^( ALAP s= stat ) | ^( WHILE c= stat s= stat ) | ^( UNTIL c= stat s= stat ) | ^( TRY s1= stat (s2= stat )? ) | ^( IF c= stat s1= stat (s2= stat )? ) | ^( CHOICE s1= stat (s2= stat )* ) | ^( STAR s= stat ) | rule | ANY | OTHER | TRUE );
    public final CtrlBuilder.stat_return stat() throws RecognitionException {
        CtrlBuilder.stat_return retval = new CtrlBuilder.stat_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        NewCtrlTree _first_0 = null;
        NewCtrlTree _last = null;

        NewCtrlTree SEMI26=null;
        NewCtrlTree ALAP29=null;
        NewCtrlTree WHILE30=null;
        NewCtrlTree UNTIL31=null;
        NewCtrlTree TRY32=null;
        NewCtrlTree IF33=null;
        NewCtrlTree CHOICE34=null;
        NewCtrlTree STAR35=null;
        NewCtrlTree ANY37=null;
        NewCtrlTree OTHER38=null;
        NewCtrlTree TRUE39=null;
        CtrlBuilder.stat_return s =null;

        CtrlBuilder.stat_return c =null;

        CtrlBuilder.stat_return s1 =null;

        CtrlBuilder.stat_return s2 =null;

        CtrlBuilder.block_return block27 =null;

        CtrlBuilder.var_decl_return var_decl28 =null;

        CtrlBuilder.rule_return rule36 =null;


        NewCtrlTree SEMI26_tree=null;
        NewCtrlTree ALAP29_tree=null;
        NewCtrlTree WHILE30_tree=null;
        NewCtrlTree UNTIL31_tree=null;
        NewCtrlTree TRY32_tree=null;
        NewCtrlTree IF33_tree=null;
        NewCtrlTree CHOICE34_tree=null;
        NewCtrlTree STAR35_tree=null;
        NewCtrlTree ANY37_tree=null;
        NewCtrlTree OTHER38_tree=null;
        NewCtrlTree TRUE39_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:102:3: ( ^( SEMI s= stat ) | block | var_decl | ^( ALAP s= stat ) | ^( WHILE c= stat s= stat ) | ^( UNTIL c= stat s= stat ) | ^( TRY s1= stat (s2= stat )? ) | ^( IF c= stat s1= stat (s2= stat )? ) | ^( CHOICE s1= stat (s2= stat )* ) | ^( STAR s= stat ) | rule | ANY | OTHER | TRUE )
            int alt9=14;
            switch ( input.LA(1) ) {
            case SEMI:
                {
                alt9=1;
                }
                break;
            case BLOCK:
                {
                alt9=2;
                }
                break;
            case VAR:
                {
                alt9=3;
                }
                break;
            case ALAP:
                {
                alt9=4;
                }
                break;
            case WHILE:
                {
                alt9=5;
                }
                break;
            case UNTIL:
                {
                alt9=6;
                }
                break;
            case TRY:
                {
                alt9=7;
                }
                break;
            case IF:
                {
                alt9=8;
                }
                break;
            case CHOICE:
                {
                alt9=9;
                }
                break;
            case STAR:
                {
                alt9=10;
                }
                break;
            case CALL:
                {
                alt9=11;
                }
                break;
            case ANY:
                {
                alt9=12;
                }
                break;
            case OTHER:
                {
                alt9=13;
                }
                break;
            case TRUE:
                {
                alt9=14;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                throw nvae;

            }

            switch (alt9) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:102:5: ^( SEMI s= stat )
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    {
                    NewCtrlTree _save_last_1 = _last;
                    NewCtrlTree _first_1 = null;
                    _last = (NewCtrlTree)input.LT(1);
                    SEMI26=(NewCtrlTree)match(input,SEMI,FOLLOW_SEMI_in_stat300); 


                    if ( _first_0==null ) _first_0 = SEMI26;
                    match(input, Token.DOWN, null); 
                    _last = (NewCtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat304);
                    s=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                     retval.aut = (s!=null?s.aut:null); 

                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 2 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:104:5: block
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    pushFollow(FOLLOW_block_in_stat317);
                    block27=block();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = block27.tree;


                     retval.aut = (block27!=null?block27.aut:null); 

                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 3 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:106:5: var_decl
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    pushFollow(FOLLOW_var_decl_in_stat329);
                    var_decl28=var_decl();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = var_decl28.tree;


                     retval.aut = builder.buildTrue(); 

                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 4 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:108:5: ^( ALAP s= stat )
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    {
                    NewCtrlTree _save_last_1 = _last;
                    NewCtrlTree _first_1 = null;
                    _last = (NewCtrlTree)input.LT(1);
                    ALAP29=(NewCtrlTree)match(input,ALAP,FOLLOW_ALAP_in_stat342); 


                    if ( _first_0==null ) _first_0 = ALAP29;
                    match(input, Token.DOWN, null); 
                    _last = (NewCtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat346);
                    s=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                     retval.aut = builder.buildAlap((s!=null?s.aut:null)); 

                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 5 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:110:5: ^( WHILE c= stat s= stat )
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    {
                    NewCtrlTree _save_last_1 = _last;
                    NewCtrlTree _first_1 = null;
                    _last = (NewCtrlTree)input.LT(1);
                    WHILE30=(NewCtrlTree)match(input,WHILE,FOLLOW_WHILE_in_stat360); 


                    if ( _first_0==null ) _first_0 = WHILE30;
                    match(input, Token.DOWN, null); 
                    _last = (NewCtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat364);
                    c=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = c.tree;


                    _last = (NewCtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat368);
                    s=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                     retval.aut = builder.buildWhileDo((c!=null?c.aut:null), (s!=null?s.aut:null)); 

                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 6 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:112:5: ^( UNTIL c= stat s= stat )
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    {
                    NewCtrlTree _save_last_1 = _last;
                    NewCtrlTree _first_1 = null;
                    _last = (NewCtrlTree)input.LT(1);
                    UNTIL31=(NewCtrlTree)match(input,UNTIL,FOLLOW_UNTIL_in_stat382); 


                    if ( _first_0==null ) _first_0 = UNTIL31;
                    match(input, Token.DOWN, null); 
                    _last = (NewCtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat386);
                    c=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = c.tree;


                    _last = (NewCtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat390);
                    s=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                     retval.aut = builder.buildUntilDo((c!=null?c.aut:null), (s!=null?s.aut:null)); 

                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 7 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:114:5: ^( TRY s1= stat (s2= stat )? )
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    {
                    NewCtrlTree _save_last_1 = _last;
                    NewCtrlTree _first_1 = null;
                    _last = (NewCtrlTree)input.LT(1);
                    TRY32=(NewCtrlTree)match(input,TRY,FOLLOW_TRY_in_stat404); 


                    if ( _first_0==null ) _first_0 = TRY32;
                    match(input, Token.DOWN, null); 
                    _last = (NewCtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat408);
                    s1=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s1.tree;


                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:114:19: (s2= stat )?
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0==ALAP||LA6_0==ANY||LA6_0==BLOCK||(LA6_0 >= CALL && LA6_0 <= CHOICE)||LA6_0==IF||LA6_0==OTHER||LA6_0==SEMI||LA6_0==STAR||(LA6_0 >= TRUE && LA6_0 <= WHILE)) ) {
                        alt6=1;
                    }
                    switch (alt6) {
                        case 1 :
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:114:20: s2= stat
                            {
                            _last = (NewCtrlTree)input.LT(1);
                            pushFollow(FOLLOW_stat_in_stat413);
                            s2=stat();

                            state._fsp--;

                             
                            if ( _first_1==null ) _first_1 = s2.tree;


                            retval.tree = (NewCtrlTree)_first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                            }
                            break;

                    }


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                     retval.aut = builder.buildTryElse((s1!=null?s1.aut:null), (s2!=null?s2.aut:null)); 

                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 8 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:116:5: ^( IF c= stat s1= stat (s2= stat )? )
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    {
                    NewCtrlTree _save_last_1 = _last;
                    NewCtrlTree _first_1 = null;
                    _last = (NewCtrlTree)input.LT(1);
                    IF33=(NewCtrlTree)match(input,IF,FOLLOW_IF_in_stat429); 


                    if ( _first_0==null ) _first_0 = IF33;
                    match(input, Token.DOWN, null); 
                    _last = (NewCtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat433);
                    c=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = c.tree;


                    _last = (NewCtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat437);
                    s1=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s1.tree;


                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:116:25: (s2= stat )?
                    int alt7=2;
                    int LA7_0 = input.LA(1);

                    if ( (LA7_0==ALAP||LA7_0==ANY||LA7_0==BLOCK||(LA7_0 >= CALL && LA7_0 <= CHOICE)||LA7_0==IF||LA7_0==OTHER||LA7_0==SEMI||LA7_0==STAR||(LA7_0 >= TRUE && LA7_0 <= WHILE)) ) {
                        alt7=1;
                    }
                    switch (alt7) {
                        case 1 :
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:116:26: s2= stat
                            {
                            _last = (NewCtrlTree)input.LT(1);
                            pushFollow(FOLLOW_stat_in_stat442);
                            s2=stat();

                            state._fsp--;

                             
                            if ( _first_1==null ) _first_1 = s2.tree;


                            retval.tree = (NewCtrlTree)_first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                            }
                            break;

                    }


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                     retval.aut = builder.buildIfThenElse((c!=null?c.aut:null), (s1!=null?s1.aut:null), (s2!=null?s2.aut:null)); 

                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 9 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:118:5: ^( CHOICE s1= stat (s2= stat )* )
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    {
                    NewCtrlTree _save_last_1 = _last;
                    NewCtrlTree _first_1 = null;
                    _last = (NewCtrlTree)input.LT(1);
                    CHOICE34=(NewCtrlTree)match(input,CHOICE,FOLLOW_CHOICE_in_stat459); 


                    if ( _first_0==null ) _first_0 = CHOICE34;
                    match(input, Token.DOWN, null); 
                    _last = (NewCtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat471);
                    s1=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s1.tree;


                     retval.aut = (s1!=null?s1.aut:null); 

                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:121:8: (s2= stat )*
                    loop8:
                    do {
                        int alt8=2;
                        int LA8_0 = input.LA(1);

                        if ( (LA8_0==ALAP||LA8_0==ANY||LA8_0==BLOCK||(LA8_0 >= CALL && LA8_0 <= CHOICE)||LA8_0==IF||LA8_0==OTHER||LA8_0==SEMI||LA8_0==STAR||(LA8_0 >= TRUE && LA8_0 <= WHILE)) ) {
                            alt8=1;
                        }


                        switch (alt8) {
                    	case 1 :
                    	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:121:10: s2= stat
                    	    {
                    	    _last = (NewCtrlTree)input.LT(1);
                    	    pushFollow(FOLLOW_stat_in_stat493);
                    	    s2=stat();

                    	    state._fsp--;

                    	     
                    	    if ( _first_1==null ) _first_1 = s2.tree;


                    	     retval.aut = builder.buildOr(retval.aut, (s2!=null?s2.aut:null)); 

                    	    retval.tree = (NewCtrlTree)_first_0;
                    	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                    	        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    	    }
                    	    break;

                    	default :
                    	    break loop8;
                        }
                    } while (true);


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 10 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:125:5: ^( STAR s= stat )
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    {
                    NewCtrlTree _save_last_1 = _last;
                    NewCtrlTree _first_1 = null;
                    _last = (NewCtrlTree)input.LT(1);
                    STAR35=(NewCtrlTree)match(input,STAR,FOLLOW_STAR_in_stat528); 


                    if ( _first_0==null ) _first_0 = STAR35;
                    match(input, Token.DOWN, null); 
                    _last = (NewCtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat532);
                    s=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                     retval.aut = builder.buildStar((s!=null?s.aut:null)); 

                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 11 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:127:5: rule
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    pushFollow(FOLLOW_rule_in_stat545);
                    rule36=rule();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = rule36.tree;


                     retval.aut = builder.buildCall((rule36!=null?((NewCtrlTree)rule36.tree):null).getCtrlCall(), namespace); 

                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 12 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:129:5: ANY
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    ANY37=(NewCtrlTree)match(input,ANY,FOLLOW_ANY_in_stat557); 
                     
                    if ( _first_0==null ) _first_0 = ANY37;


                     retval.aut = builder.buildAny(namespace); 

                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 13 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:131:5: OTHER
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    OTHER38=(NewCtrlTree)match(input,OTHER,FOLLOW_OTHER_in_stat569); 
                     
                    if ( _first_0==null ) _first_0 = OTHER38;


                     retval.aut = builder.buildOther(namespace); 
                        

                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 14 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:134:5: TRUE
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    TRUE39=(NewCtrlTree)match(input,TRUE,FOLLOW_TRUE_in_stat581); 
                     
                    if ( _first_0==null ) _first_0 = TRUE39;


                     retval.aut = builder.buildTrue(); 

                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

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
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "rule"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:138:1: rule : ^( CALL ID ( ^( ARGS ( arg )* RPAR ) )? ) ;
    public final CtrlBuilder.rule_return rule() throws RecognitionException {
        CtrlBuilder.rule_return retval = new CtrlBuilder.rule_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        NewCtrlTree _first_0 = null;
        NewCtrlTree _last = null;

        NewCtrlTree CALL40=null;
        NewCtrlTree ID41=null;
        NewCtrlTree ARGS42=null;
        NewCtrlTree RPAR44=null;
        CtrlBuilder.arg_return arg43 =null;


        NewCtrlTree CALL40_tree=null;
        NewCtrlTree ID41_tree=null;
        NewCtrlTree ARGS42_tree=null;
        NewCtrlTree RPAR44_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:139:3: ( ^( CALL ID ( ^( ARGS ( arg )* RPAR ) )? ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:139:5: ^( CALL ID ( ^( ARGS ( arg )* RPAR ) )? )
            {
            _last = (NewCtrlTree)input.LT(1);
            {
            NewCtrlTree _save_last_1 = _last;
            NewCtrlTree _first_1 = null;
            _last = (NewCtrlTree)input.LT(1);
            CALL40=(NewCtrlTree)match(input,CALL,FOLLOW_CALL_in_rule601); 


            if ( _first_0==null ) _first_0 = CALL40;
            match(input, Token.DOWN, null); 
            _last = (NewCtrlTree)input.LT(1);
            ID41=(NewCtrlTree)match(input,ID,FOLLOW_ID_in_rule603); 
             
            if ( _first_1==null ) _first_1 = ID41;


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:139:15: ( ^( ARGS ( arg )* RPAR ) )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==ARGS) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:139:16: ^( ARGS ( arg )* RPAR )
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    {
                    NewCtrlTree _save_last_2 = _last;
                    NewCtrlTree _first_2 = null;
                    _last = (NewCtrlTree)input.LT(1);
                    ARGS42=(NewCtrlTree)match(input,ARGS,FOLLOW_ARGS_in_rule607); 


                    if ( _first_1==null ) _first_1 = ARGS42;
                    match(input, Token.DOWN, null); 
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:139:23: ( arg )*
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);

                        if ( (LA10_0==ARG) ) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:139:23: arg
                    	    {
                    	    _last = (NewCtrlTree)input.LT(1);
                    	    pushFollow(FOLLOW_arg_in_rule609);
                    	    arg43=arg();

                    	    state._fsp--;

                    	     
                    	    if ( _first_2==null ) _first_2 = arg43.tree;


                    	    retval.tree = (NewCtrlTree)_first_0;
                    	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                    	        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    	    }
                    	    break;

                    	default :
                    	    break loop10;
                        }
                    } while (true);


                    _last = (NewCtrlTree)input.LT(1);
                    RPAR44=(NewCtrlTree)match(input,RPAR,FOLLOW_RPAR_in_rule612); 
                     
                    if ( _first_2==null ) _first_2 = RPAR44;


                    match(input, Token.UP, null); 
                    _last = _save_last_2;
                    }


                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;

            }


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = (NewCtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

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
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "var_decl"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:142:1: var_decl : ^( VAR type ( ID )+ ) ;
    public final CtrlBuilder.var_decl_return var_decl() throws RecognitionException {
        CtrlBuilder.var_decl_return retval = new CtrlBuilder.var_decl_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        NewCtrlTree _first_0 = null;
        NewCtrlTree _last = null;

        NewCtrlTree VAR45=null;
        NewCtrlTree ID47=null;
        CtrlBuilder.type_return type46 =null;


        NewCtrlTree VAR45_tree=null;
        NewCtrlTree ID47_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:143:2: ( ^( VAR type ( ID )+ ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:143:4: ^( VAR type ( ID )+ )
            {
            _last = (NewCtrlTree)input.LT(1);
            {
            NewCtrlTree _save_last_1 = _last;
            NewCtrlTree _first_1 = null;
            _last = (NewCtrlTree)input.LT(1);
            VAR45=(NewCtrlTree)match(input,VAR,FOLLOW_VAR_in_var_decl630); 


            if ( _first_0==null ) _first_0 = VAR45;
            match(input, Token.DOWN, null); 
            _last = (NewCtrlTree)input.LT(1);
            pushFollow(FOLLOW_type_in_var_decl632);
            type46=type();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = type46.tree;


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:143:16: ( ID )+
            int cnt12=0;
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==ID) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:143:16: ID
            	    {
            	    _last = (NewCtrlTree)input.LT(1);
            	    ID47=(NewCtrlTree)match(input,ID,FOLLOW_ID_in_var_decl634); 
            	     
            	    if ( _first_1==null ) _first_1 = ID47;


            	    retval.tree = (NewCtrlTree)_first_0;
            	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
            	        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

            	    }
            	    break;

            	default :
            	    if ( cnt12 >= 1 ) break loop12;
                        EarlyExitException eee =
                            new EarlyExitException(12, input);
                        throw eee;
                }
                cnt12++;
            } while (true);


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = (NewCtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

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
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "type"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:146:1: type : ( NODE | BOOL | STRING | INT | REAL );
    public final CtrlBuilder.type_return type() throws RecognitionException {
        CtrlBuilder.type_return retval = new CtrlBuilder.type_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        NewCtrlTree _first_0 = null;
        NewCtrlTree _last = null;

        NewCtrlTree set48=null;

        NewCtrlTree set48_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:147:3: ( NODE | BOOL | STRING | INT | REAL )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:
            {
            _last = (NewCtrlTree)input.LT(1);
            set48=(NewCtrlTree)input.LT(1);

            if ( input.LA(1)==BOOL||input.LA(1)==INT||input.LA(1)==NODE||input.LA(1)==REAL||input.LA(1)==STRING ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }

            retval.tree = (NewCtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);
             

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
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "arg"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:150:1: arg : ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) ) ;
    public final CtrlBuilder.arg_return arg() throws RecognitionException {
        CtrlBuilder.arg_return retval = new CtrlBuilder.arg_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        NewCtrlTree _first_0 = null;
        NewCtrlTree _last = null;

        NewCtrlTree ARG49=null;
        NewCtrlTree OUT50=null;
        NewCtrlTree ID51=null;
        NewCtrlTree DONT_CARE52=null;
        CtrlBuilder.literal_return literal53 =null;


        NewCtrlTree ARG49_tree=null;
        NewCtrlTree OUT50_tree=null;
        NewCtrlTree ID51_tree=null;
        NewCtrlTree DONT_CARE52_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:151:2: ( ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:151:4: ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) )
            {
            _last = (NewCtrlTree)input.LT(1);
            {
            NewCtrlTree _save_last_1 = _last;
            NewCtrlTree _first_1 = null;
            _last = (NewCtrlTree)input.LT(1);
            ARG49=(NewCtrlTree)match(input,ARG,FOLLOW_ARG_in_arg681); 


            if ( _first_0==null ) _first_0 = ARG49;
            match(input, Token.DOWN, null); 
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:151:11: ( ( OUT )? ID | DONT_CARE | literal )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:151:13: ( OUT )? ID
                    {
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:151:13: ( OUT )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);

                    if ( (LA13_0==OUT) ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:151:13: OUT
                            {
                            _last = (NewCtrlTree)input.LT(1);
                            OUT50=(NewCtrlTree)match(input,OUT,FOLLOW_OUT_in_arg685); 
                             
                            if ( _first_1==null ) _first_1 = OUT50;


                            retval.tree = (NewCtrlTree)_first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                            }
                            break;

                    }


                    _last = (NewCtrlTree)input.LT(1);
                    ID51=(NewCtrlTree)match(input,ID,FOLLOW_ID_in_arg688); 
                     
                    if ( _first_1==null ) _first_1 = ID51;


                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 2 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:151:23: DONT_CARE
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    DONT_CARE52=(NewCtrlTree)match(input,DONT_CARE,FOLLOW_DONT_CARE_in_arg692); 
                     
                    if ( _first_1==null ) _first_1 = DONT_CARE52;


                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 3 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:151:35: literal
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    pushFollow(FOLLOW_literal_in_arg696);
                    literal53=literal();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = literal53.tree;


                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;

            }


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = (NewCtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

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
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "literal"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:154:1: literal : ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT );
    public final CtrlBuilder.literal_return literal() throws RecognitionException {
        CtrlBuilder.literal_return retval = new CtrlBuilder.literal_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        NewCtrlTree _first_0 = null;
        NewCtrlTree _last = null;

        NewCtrlTree set54=null;

        NewCtrlTree set54_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:155:3: ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:
            {
            _last = (NewCtrlTree)input.LT(1);
            set54=(NewCtrlTree)input.LT(1);

            if ( input.LA(1)==FALSE||input.LA(1)==INT_LIT||input.LA(1)==REAL_LIT||(input.LA(1) >= STRING_LIT && input.LA(1) <= TRUE) ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }

            retval.tree = (NewCtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);
             

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
    public static final BitSet FOLLOW_package_decl_in_program61 = new BitSet(new long[]{0x0000000024000000L});
    public static final BitSet FOLLOW_import_decl_in_program63 = new BitSet(new long[]{0x0000000024000000L});
    public static final BitSet FOLLOW_functions_in_program66 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_recipes_in_program68 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_block_in_program70 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PACKAGE_in_package_decl91 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_package_decl93 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_SEMI_in_package_decl95 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IMPORT_in_import_decl112 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_import_decl114 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FUNCTIONS_in_functions129 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_function_in_functions131 = new BitSet(new long[]{0x0000000002000008L});
    public static final BitSet FOLLOW_FUNCTION_in_function147 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_function149 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_block_in_function151 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RECIPES_in_recipes174 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_recipe_in_recipes176 = new BitSet(new long[]{0x0008000000000008L});
    public static final BitSet FOLLOW_RECIPE_in_recipe192 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_recipe194 = new BitSet(new long[]{0x0000000080000800L});
    public static final BitSet FOLLOW_INT_LIT_in_recipe196 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_block_in_recipe199 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BLOCK_in_block225 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_block245 = new BitSet(new long[]{0xF24102001000C850L,0x0000000000000001L});
    public static final BitSet FOLLOW_RCURLY_in_block275 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SEMI_in_stat300 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat304 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_block_in_stat317 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_decl_in_stat329 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALAP_in_stat342 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat346 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WHILE_in_stat360 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat364 = new BitSet(new long[]{0xF24002001000C850L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat368 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_UNTIL_in_stat382 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat386 = new BitSet(new long[]{0xF24002001000C850L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat390 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TRY_in_stat404 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat408 = new BitSet(new long[]{0xF24002001000C858L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat413 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IF_in_stat429 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat433 = new BitSet(new long[]{0xF24002001000C850L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat437 = new BitSet(new long[]{0xF24002001000C858L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat442 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CHOICE_in_stat459 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat471 = new BitSet(new long[]{0xF24002001000C858L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat493 = new BitSet(new long[]{0xF24002001000C858L,0x0000000000000001L});
    public static final BitSet FOLLOW_STAR_in_stat528 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat532 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_rule_in_stat545 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_stat557 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_stat569 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_stat581 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CALL_in_rule601 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rule603 = new BitSet(new long[]{0x0000000000000108L});
    public static final BitSet FOLLOW_ARGS_in_rule607 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_arg_in_rule609 = new BitSet(new long[]{0x0020000000000080L});
    public static final BitSet FOLLOW_RPAR_in_rule612 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VAR_in_var_decl630 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_type_in_var_decl632 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_var_decl634 = new BitSet(new long[]{0x0000000008000008L});
    public static final BitSet FOLLOW_ARG_in_arg681 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_OUT_in_arg685 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_arg688 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DONT_CARE_in_arg692 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_literal_in_arg696 = new BitSet(new long[]{0x0000000000000008L});

}