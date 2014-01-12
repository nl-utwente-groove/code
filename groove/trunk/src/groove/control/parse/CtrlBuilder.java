// $ANTLR 3.4 D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g 2014-01-12 16:19:09

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ALAP", "AMP", "ANY", "ARG", "ARGS", "ASTERISK", "ATOM", "BAR", "BLOCK", "BOOL", "BSLASH", "CALL", "CHOICE", "COMMA", "DO", "DONT_CARE", "DOT", "DO_UNTIL", "DO_WHILE", "ELSE", "EscapeSequence", "FALSE", "FUNCTION", "FUNCTIONS", "ID", "IF", "IMPORT", "INT", "INT_LIT", "IntegerNumber", "LCURLY", "LPAR", "MINUS", "ML_COMMENT", "NODE", "NOT", "NonIntegerNumber", "OR", "OTHER", "OUT", "PACKAGE", "PAR", "PARS", "PLUS", "PRIORITY", "PROGRAM", "QUOTE", "RCURLY", "REAL", "REAL_LIT", "RECIPE", "RECIPES", "RPAR", "SEMI", "SHARP", "SL_COMMENT", "STAR", "STRING", "STRING_LIT", "TRUE", "TRY", "UNTIL", "VAR", "WHILE", "WS"
    };

    public static final int EOF=-1;
    public static final int ALAP=4;
    public static final int AMP=5;
    public static final int ANY=6;
    public static final int ARG=7;
    public static final int ARGS=8;
    public static final int ASTERISK=9;
    public static final int ATOM=10;
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
    public static final int IMPORT=30;
    public static final int INT=31;
    public static final int INT_LIT=32;
    public static final int IntegerNumber=33;
    public static final int LCURLY=34;
    public static final int LPAR=35;
    public static final int MINUS=36;
    public static final int ML_COMMENT=37;
    public static final int NODE=38;
    public static final int NOT=39;
    public static final int NonIntegerNumber=40;
    public static final int OR=41;
    public static final int OTHER=42;
    public static final int OUT=43;
    public static final int PACKAGE=44;
    public static final int PAR=45;
    public static final int PARS=46;
    public static final int PLUS=47;
    public static final int PRIORITY=48;
    public static final int PROGRAM=49;
    public static final int QUOTE=50;
    public static final int RCURLY=51;
    public static final int REAL=52;
    public static final int REAL_LIT=53;
    public static final int RECIPE=54;
    public static final int RECIPES=55;
    public static final int RPAR=56;
    public static final int SEMI=57;
    public static final int SHARP=58;
    public static final int SL_COMMENT=59;
    public static final int STAR=60;
    public static final int STRING=61;
    public static final int STRING_LIT=62;
    public static final int TRUE=63;
    public static final int TRY=64;
    public static final int UNTIL=65;
    public static final int VAR=66;
    public static final int WHILE=67;
    public static final int WS=68;

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


    public static class program_return extends TreeRuleReturnScope {
        public CtrlAut aut;
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "program"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:36:1: program returns [ CtrlAut aut ] : ^( PROGRAM package_decl ( import_decl )* functions recipes block ) ;
    public final CtrlBuilder.program_return program() throws RecognitionException {
        CtrlBuilder.program_return retval = new CtrlBuilder.program_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree PROGRAM1=null;
        CtrlBuilder.package_decl_return package_decl2 =null;

        CtrlBuilder.import_decl_return import_decl3 =null;

        CtrlBuilder.functions_return functions4 =null;

        CtrlBuilder.recipes_return recipes5 =null;

        CtrlBuilder.block_return block6 =null;


        CtrlTree PROGRAM1_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:37:3: ( ^( PROGRAM package_decl ( import_decl )* functions recipes block ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:37:5: ^( PROGRAM package_decl ( import_decl )* functions recipes block )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            PROGRAM1=(CtrlTree)match(input,PROGRAM,FOLLOW_PROGRAM_in_program59); 


            if ( _first_0==null ) _first_0 = PROGRAM1;
            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_package_decl_in_program61);
            package_decl2=package_decl();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = package_decl2.tree;


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:37:28: ( import_decl )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==IMPORT) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:37:28: import_decl
            	    {
            	    _last = (CtrlTree)input.LT(1);
            	    pushFollow(FOLLOW_import_decl_in_program63);
            	    import_decl3=import_decl();

            	    state._fsp--;

            	     
            	    if ( _first_1==null ) _first_1 = import_decl3.tree;


            	    retval.tree = (CtrlTree)_first_0;
            	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
            	        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_functions_in_program66);
            functions4=functions();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = functions4.tree;


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_recipes_in_program68);
            recipes5=recipes();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = recipes5.tree;


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_block_in_program70);
            block6=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block6.tree;


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


             // at least one child due to closing TRUE
                  if ((block6!=null?((CtrlTree)block6.tree):null).getChildCount() == 1) {
                      retval.aut = null;
                  } else {
                      retval.aut = (block6!=null?block6.aut:null);
                  }
                

            retval.tree = (CtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

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
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "package_decl"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:47:1: package_decl : ^( PACKAGE ID SEMI ) ;
    public final CtrlBuilder.package_decl_return package_decl() throws RecognitionException {
        CtrlBuilder.package_decl_return retval = new CtrlBuilder.package_decl_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree PACKAGE7=null;
        CtrlTree ID8=null;
        CtrlTree SEMI9=null;

        CtrlTree PACKAGE7_tree=null;
        CtrlTree ID8_tree=null;
        CtrlTree SEMI9_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:48:3: ( ^( PACKAGE ID SEMI ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:48:5: ^( PACKAGE ID SEMI )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            PACKAGE7=(CtrlTree)match(input,PACKAGE,FOLLOW_PACKAGE_in_package_decl91); 


            if ( _first_0==null ) _first_0 = PACKAGE7;
            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            ID8=(CtrlTree)match(input,ID,FOLLOW_ID_in_package_decl93); 
             
            if ( _first_1==null ) _first_1 = ID8;


            _last = (CtrlTree)input.LT(1);
            SEMI9=(CtrlTree)match(input,SEMI,FOLLOW_SEMI_in_package_decl95); 
             
            if ( _first_1==null ) _first_1 = SEMI9;


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = (CtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

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
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "import_decl"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:51:1: import_decl : ^( IMPORT ID SEMI ) ;
    public final CtrlBuilder.import_decl_return import_decl() throws RecognitionException {
        CtrlBuilder.import_decl_return retval = new CtrlBuilder.import_decl_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree IMPORT10=null;
        CtrlTree ID11=null;
        CtrlTree SEMI12=null;

        CtrlTree IMPORT10_tree=null;
        CtrlTree ID11_tree=null;
        CtrlTree SEMI12_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:52:3: ( ^( IMPORT ID SEMI ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:52:5: ^( IMPORT ID SEMI )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            IMPORT10=(CtrlTree)match(input,IMPORT,FOLLOW_IMPORT_in_import_decl112); 


            if ( _first_0==null ) _first_0 = IMPORT10;
            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            ID11=(CtrlTree)match(input,ID,FOLLOW_ID_in_import_decl114); 
             
            if ( _first_1==null ) _first_1 = ID11;


            _last = (CtrlTree)input.LT(1);
            SEMI12=(CtrlTree)match(input,SEMI,FOLLOW_SEMI_in_import_decl116); 
             
            if ( _first_1==null ) _first_1 = SEMI12;


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = (CtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

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
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "functions"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:55:1: functions : ^( FUNCTIONS ( function )* ) ;
    public final CtrlBuilder.functions_return functions() throws RecognitionException {
        CtrlBuilder.functions_return retval = new CtrlBuilder.functions_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree FUNCTIONS13=null;
        CtrlBuilder.function_return function14 =null;


        CtrlTree FUNCTIONS13_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:56:3: ( ^( FUNCTIONS ( function )* ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:56:5: ^( FUNCTIONS ( function )* )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            FUNCTIONS13=(CtrlTree)match(input,FUNCTIONS,FOLLOW_FUNCTIONS_in_functions131); 


            if ( _first_0==null ) _first_0 = FUNCTIONS13;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:56:17: ( function )*
                loop2:
                do {
                    int alt2=2;
                    int LA2_0 = input.LA(1);

                    if ( (LA2_0==FUNCTION) ) {
                        alt2=1;
                    }


                    switch (alt2) {
                	case 1 :
                	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:56:17: function
                	    {
                	    _last = (CtrlTree)input.LT(1);
                	    pushFollow(FOLLOW_function_in_functions133);
                	    function14=function();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = function14.tree;


                	    retval.tree = (CtrlTree)_first_0;
                	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                	        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

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


            retval.tree = (CtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

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
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "function"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:59:1: function : ^( FUNCTION ID par_list priority block ) ;
    public final CtrlBuilder.function_return function() throws RecognitionException {
        CtrlBuilder.function_return retval = new CtrlBuilder.function_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree FUNCTION15=null;
        CtrlTree ID16=null;
        CtrlBuilder.par_list_return par_list17 =null;

        CtrlBuilder.priority_return priority18 =null;

        CtrlBuilder.block_return block19 =null;


        CtrlTree FUNCTION15_tree=null;
        CtrlTree ID16_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:60:3: ( ^( FUNCTION ID par_list priority block ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:60:5: ^( FUNCTION ID par_list priority block )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            FUNCTION15=(CtrlTree)match(input,FUNCTION,FOLLOW_FUNCTION_in_function149); 


            if ( _first_0==null ) _first_0 = FUNCTION15;
            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            ID16=(CtrlTree)match(input,ID,FOLLOW_ID_in_function151); 
             
            if ( _first_1==null ) _first_1 = ID16;


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_par_list_in_function153);
            par_list17=par_list();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = par_list17.tree;


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_priority_in_function155);
            priority18=priority();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = priority18.tree;


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_block_in_function157);
            block19=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block19.tree;


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


             
                  namespace.addBody(helper.qualify((ID16!=null?ID16.getText():null)), (block19!=null?block19.aut:null));
                

            retval.tree = (CtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

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
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "recipes"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:66:1: recipes : ^( RECIPES ( recipe )* ) ;
    public final CtrlBuilder.recipes_return recipes() throws RecognitionException {
        CtrlBuilder.recipes_return retval = new CtrlBuilder.recipes_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree RECIPES20=null;
        CtrlBuilder.recipe_return recipe21 =null;


        CtrlTree RECIPES20_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:67:3: ( ^( RECIPES ( recipe )* ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:67:5: ^( RECIPES ( recipe )* )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            RECIPES20=(CtrlTree)match(input,RECIPES,FOLLOW_RECIPES_in_recipes180); 


            if ( _first_0==null ) _first_0 = RECIPES20;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:67:15: ( recipe )*
                loop3:
                do {
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0==RECIPE) ) {
                        alt3=1;
                    }


                    switch (alt3) {
                	case 1 :
                	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:67:15: recipe
                	    {
                	    _last = (CtrlTree)input.LT(1);
                	    pushFollow(FOLLOW_recipe_in_recipes182);
                	    recipe21=recipe();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = recipe21.tree;


                	    retval.tree = (CtrlTree)_first_0;
                	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                	        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

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


            retval.tree = (CtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

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
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "recipe"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:70:1: recipe : ^( RECIPE ID par_list priority block ) ;
    public final CtrlBuilder.recipe_return recipe() throws RecognitionException {
        CtrlBuilder.recipe_return retval = new CtrlBuilder.recipe_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree RECIPE22=null;
        CtrlTree ID23=null;
        CtrlBuilder.par_list_return par_list24 =null;

        CtrlBuilder.priority_return priority25 =null;

        CtrlBuilder.block_return block26 =null;


        CtrlTree RECIPE22_tree=null;
        CtrlTree ID23_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:71:3: ( ^( RECIPE ID par_list priority block ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:71:5: ^( RECIPE ID par_list priority block )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            RECIPE22=(CtrlTree)match(input,RECIPE,FOLLOW_RECIPE_in_recipe198); 


            if ( _first_0==null ) _first_0 = RECIPE22;
            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            ID23=(CtrlTree)match(input,ID,FOLLOW_ID_in_recipe200); 
             
            if ( _first_1==null ) _first_1 = ID23;


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_par_list_in_recipe202);
            par_list24=par_list();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = par_list24.tree;


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_priority_in_recipe204);
            priority25=priority();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = priority25.tree;


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_block_in_recipe206);
            block26=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block26.tree;


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


             
                  String recipeName = helper.qualify((ID23!=null?ID23.getText():null));
                  helper.checkRecipeBody(RECIPE22, recipeName, (block26!=null?block26.aut:null));
                  namespace.addBody(recipeName, (block26!=null?block26.aut:null));
                

            retval.tree = (CtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

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


    public static class priority_return extends TreeRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "priority"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:79:1: priority : ( INT_LIT )? ;
    public final CtrlBuilder.priority_return priority() throws RecognitionException {
        CtrlBuilder.priority_return retval = new CtrlBuilder.priority_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree INT_LIT27=null;

        CtrlTree INT_LIT27_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:80:3: ( ( INT_LIT )? )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:80:5: ( INT_LIT )?
            {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:80:5: ( INT_LIT )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==INT_LIT) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:80:7: INT_LIT
                    {
                    _last = (CtrlTree)input.LT(1);
                    INT_LIT27=(CtrlTree)match(input,INT_LIT,FOLLOW_INT_LIT_in_priority228); 
                     
                    if ( _first_0==null ) _first_0 = INT_LIT27;


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;

            }


            retval.tree = (CtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

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
    // $ANTLR end "priority"


    public static class par_list_return extends TreeRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "par_list"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:83:1: par_list : ^( PARS ( par_decl )* ) ;
    public final CtrlBuilder.par_list_return par_list() throws RecognitionException {
        CtrlBuilder.par_list_return retval = new CtrlBuilder.par_list_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree PARS28=null;
        CtrlBuilder.par_decl_return par_decl29 =null;


        CtrlTree PARS28_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:84:3: ( ^( PARS ( par_decl )* ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:84:5: ^( PARS ( par_decl )* )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            PARS28=(CtrlTree)match(input,PARS,FOLLOW_PARS_in_par_list245); 


            if ( _first_0==null ) _first_0 = PARS28;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:84:12: ( par_decl )*
                loop5:
                do {
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0==PAR) ) {
                        alt5=1;
                    }


                    switch (alt5) {
                	case 1 :
                	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:84:12: par_decl
                	    {
                	    _last = (CtrlTree)input.LT(1);
                	    pushFollow(FOLLOW_par_decl_in_par_list247);
                	    par_decl29=par_decl();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = par_decl29.tree;


                	    retval.tree = (CtrlTree)_first_0;
                	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                	        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                	    }
                	    break;

                	default :
                	    break loop5;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }
            _last = _save_last_1;
            }


            retval.tree = (CtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

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
    // $ANTLR end "par_list"


    public static class par_decl_return extends TreeRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "par_decl"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:87:1: par_decl : ^( PAR ( OUT )? type ID ) ;
    public final CtrlBuilder.par_decl_return par_decl() throws RecognitionException {
        CtrlBuilder.par_decl_return retval = new CtrlBuilder.par_decl_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree PAR30=null;
        CtrlTree OUT31=null;
        CtrlTree ID33=null;
        CtrlBuilder.type_return type32 =null;


        CtrlTree PAR30_tree=null;
        CtrlTree OUT31_tree=null;
        CtrlTree ID33_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:88:3: ( ^( PAR ( OUT )? type ID ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:88:5: ^( PAR ( OUT )? type ID )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            PAR30=(CtrlTree)match(input,PAR,FOLLOW_PAR_in_par_decl264); 


            if ( _first_0==null ) _first_0 = PAR30;
            match(input, Token.DOWN, null); 
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:88:11: ( OUT )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==OUT) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:88:11: OUT
                    {
                    _last = (CtrlTree)input.LT(1);
                    OUT31=(CtrlTree)match(input,OUT,FOLLOW_OUT_in_par_decl266); 
                     
                    if ( _first_1==null ) _first_1 = OUT31;


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;

            }


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_type_in_par_decl269);
            type32=type();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = type32.tree;


            _last = (CtrlTree)input.LT(1);
            ID33=(CtrlTree)match(input,ID,FOLLOW_ID_in_par_decl271); 
             
            if ( _first_1==null ) _first_1 = ID33;


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = (CtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

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
    // $ANTLR end "par_decl"


    public static class block_return extends TreeRuleReturnScope {
        public CtrlAut aut;
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "block"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:91:1: block returns [ CtrlAut aut ] : ^( BLOCK ( stat )* ) ;
    public final CtrlBuilder.block_return block() throws RecognitionException {
        CtrlBuilder.block_return retval = new CtrlBuilder.block_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree BLOCK34=null;
        CtrlBuilder.stat_return stat35 =null;


        CtrlTree BLOCK34_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:92:3: ( ^( BLOCK ( stat )* ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:92:5: ^( BLOCK ( stat )* )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            BLOCK34=(CtrlTree)match(input,BLOCK,FOLLOW_BLOCK_in_block291); 


            if ( _first_0==null ) _first_0 = BLOCK34;
             retval.aut = builder.buildTrue(); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:94:8: ( stat )*
                loop7:
                do {
                    int alt7=2;
                    int LA7_0 = input.LA(1);

                    if ( (LA7_0==ALAP||LA7_0==ANY||LA7_0==ATOM||LA7_0==BLOCK||(LA7_0 >= CALL && LA7_0 <= CHOICE)||LA7_0==IF||LA7_0==OTHER||LA7_0==SEMI||LA7_0==STAR||(LA7_0 >= TRUE && LA7_0 <= WHILE)) ) {
                        alt7=1;
                    }


                    switch (alt7) {
                	case 1 :
                	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:94:10: stat
                	    {
                	    _last = (CtrlTree)input.LT(1);
                	    pushFollow(FOLLOW_stat_in_block311);
                	    stat35=stat();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = stat35.tree;


                	     retval.aut = builder.buildSeq(retval.aut, (stat35!=null?stat35.aut:null)); 

                	    retval.tree = (CtrlTree)_first_0;
                	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                	        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                	    }
                	    break;

                	default :
                	    break loop7;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }
            _last = _save_last_1;
            }


            retval.tree = (CtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

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
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "stat"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:100:1: stat returns [ CtrlAut aut ] : ( ^( SEMI s= stat ) | block | var_decl | ^( ALAP s= stat ) | ^( ATOM s= stat ) | ^( WHILE c= stat s= stat ) | ^( UNTIL c= stat s= stat ) | ^( TRY s1= stat (s2= stat )? ) | ^( IF c= stat s1= stat (s2= stat )? ) | ^( CHOICE s1= stat (s2= stat )* ) | ^( STAR s= stat ) | rule | ANY | OTHER | TRUE );
    public final CtrlBuilder.stat_return stat() throws RecognitionException {
        CtrlBuilder.stat_return retval = new CtrlBuilder.stat_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree SEMI36=null;
        CtrlTree ALAP39=null;
        CtrlTree ATOM40=null;
        CtrlTree WHILE41=null;
        CtrlTree UNTIL42=null;
        CtrlTree TRY43=null;
        CtrlTree IF44=null;
        CtrlTree CHOICE45=null;
        CtrlTree STAR46=null;
        CtrlTree ANY48=null;
        CtrlTree OTHER49=null;
        CtrlTree TRUE50=null;
        CtrlBuilder.stat_return s =null;

        CtrlBuilder.stat_return c =null;

        CtrlBuilder.stat_return s1 =null;

        CtrlBuilder.stat_return s2 =null;

        CtrlBuilder.block_return block37 =null;

        CtrlBuilder.var_decl_return var_decl38 =null;

        CtrlBuilder.rule_return rule47 =null;


        CtrlTree SEMI36_tree=null;
        CtrlTree ALAP39_tree=null;
        CtrlTree ATOM40_tree=null;
        CtrlTree WHILE41_tree=null;
        CtrlTree UNTIL42_tree=null;
        CtrlTree TRY43_tree=null;
        CtrlTree IF44_tree=null;
        CtrlTree CHOICE45_tree=null;
        CtrlTree STAR46_tree=null;
        CtrlTree ANY48_tree=null;
        CtrlTree OTHER49_tree=null;
        CtrlTree TRUE50_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:101:3: ( ^( SEMI s= stat ) | block | var_decl | ^( ALAP s= stat ) | ^( ATOM s= stat ) | ^( WHILE c= stat s= stat ) | ^( UNTIL c= stat s= stat ) | ^( TRY s1= stat (s2= stat )? ) | ^( IF c= stat s1= stat (s2= stat )? ) | ^( CHOICE s1= stat (s2= stat )* ) | ^( STAR s= stat ) | rule | ANY | OTHER | TRUE )
            int alt11=15;
            switch ( input.LA(1) ) {
            case SEMI:
                {
                alt11=1;
                }
                break;
            case BLOCK:
                {
                alt11=2;
                }
                break;
            case VAR:
                {
                alt11=3;
                }
                break;
            case ALAP:
                {
                alt11=4;
                }
                break;
            case ATOM:
                {
                alt11=5;
                }
                break;
            case WHILE:
                {
                alt11=6;
                }
                break;
            case UNTIL:
                {
                alt11=7;
                }
                break;
            case TRY:
                {
                alt11=8;
                }
                break;
            case IF:
                {
                alt11=9;
                }
                break;
            case CHOICE:
                {
                alt11=10;
                }
                break;
            case STAR:
                {
                alt11=11;
                }
                break;
            case CALL:
                {
                alt11=12;
                }
                break;
            case ANY:
                {
                alt11=13;
                }
                break;
            case OTHER:
                {
                alt11=14;
                }
                break;
            case TRUE:
                {
                alt11=15;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;

            }

            switch (alt11) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:101:5: ^( SEMI s= stat )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    SEMI36=(CtrlTree)match(input,SEMI,FOLLOW_SEMI_in_stat357); 


                    if ( _first_0==null ) _first_0 = SEMI36;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat361);
                    s=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                     retval.aut = (s!=null?s.aut:null); 

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 2 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:103:5: block
                    {
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_block_in_stat374);
                    block37=block();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = block37.tree;


                     retval.aut = (block37!=null?block37.aut:null); 

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 3 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:105:5: var_decl
                    {
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_var_decl_in_stat386);
                    var_decl38=var_decl();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = var_decl38.tree;


                     retval.aut = builder.buildTrue(); 

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 4 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:107:5: ^( ALAP s= stat )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    ALAP39=(CtrlTree)match(input,ALAP,FOLLOW_ALAP_in_stat399); 


                    if ( _first_0==null ) _first_0 = ALAP39;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat403);
                    s=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                     retval.aut = builder.buildAlap((s!=null?s.aut:null)); 

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 5 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:109:5: ^( ATOM s= stat )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    ATOM40=(CtrlTree)match(input,ATOM,FOLLOW_ATOM_in_stat417); 


                    if ( _first_0==null ) _first_0 = ATOM40;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat421);
                    s=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                     helper.emitErrorMessage((s!=null?((CtrlTree)s.tree):null), "Atomic blocks are not supported in this version");
                          retval.aut = (s!=null?s.aut:null);
                        

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 6 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:113:5: ^( WHILE c= stat s= stat )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    WHILE41=(CtrlTree)match(input,WHILE,FOLLOW_WHILE_in_stat435); 


                    if ( _first_0==null ) _first_0 = WHILE41;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat439);
                    c=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = c.tree;


                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat443);
                    s=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                     retval.aut = builder.buildWhileDo((c!=null?c.aut:null), (s!=null?s.aut:null)); 

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 7 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:115:5: ^( UNTIL c= stat s= stat )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    UNTIL42=(CtrlTree)match(input,UNTIL,FOLLOW_UNTIL_in_stat457); 


                    if ( _first_0==null ) _first_0 = UNTIL42;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat461);
                    c=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = c.tree;


                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat465);
                    s=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                     retval.aut = builder.buildUntilDo((c!=null?c.aut:null), (s!=null?s.aut:null)); 

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 8 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:117:5: ^( TRY s1= stat (s2= stat )? )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    TRY43=(CtrlTree)match(input,TRY,FOLLOW_TRY_in_stat479); 


                    if ( _first_0==null ) _first_0 = TRY43;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat483);
                    s1=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s1.tree;


                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:117:19: (s2= stat )?
                    int alt8=2;
                    int LA8_0 = input.LA(1);

                    if ( (LA8_0==ALAP||LA8_0==ANY||LA8_0==ATOM||LA8_0==BLOCK||(LA8_0 >= CALL && LA8_0 <= CHOICE)||LA8_0==IF||LA8_0==OTHER||LA8_0==SEMI||LA8_0==STAR||(LA8_0 >= TRUE && LA8_0 <= WHILE)) ) {
                        alt8=1;
                    }
                    switch (alt8) {
                        case 1 :
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:117:20: s2= stat
                            {
                            _last = (CtrlTree)input.LT(1);
                            pushFollow(FOLLOW_stat_in_stat488);
                            s2=stat();

                            state._fsp--;

                             
                            if ( _first_1==null ) _first_1 = s2.tree;


                            retval.tree = (CtrlTree)_first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                            }
                            break;

                    }


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                     retval.aut = builder.buildTryElse((s1!=null?s1.aut:null), (s2!=null?s2.aut:null)); 

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 9 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:119:5: ^( IF c= stat s1= stat (s2= stat )? )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    IF44=(CtrlTree)match(input,IF,FOLLOW_IF_in_stat504); 


                    if ( _first_0==null ) _first_0 = IF44;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat508);
                    c=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = c.tree;


                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat512);
                    s1=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s1.tree;


                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:119:25: (s2= stat )?
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0==ALAP||LA9_0==ANY||LA9_0==ATOM||LA9_0==BLOCK||(LA9_0 >= CALL && LA9_0 <= CHOICE)||LA9_0==IF||LA9_0==OTHER||LA9_0==SEMI||LA9_0==STAR||(LA9_0 >= TRUE && LA9_0 <= WHILE)) ) {
                        alt9=1;
                    }
                    switch (alt9) {
                        case 1 :
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:119:26: s2= stat
                            {
                            _last = (CtrlTree)input.LT(1);
                            pushFollow(FOLLOW_stat_in_stat517);
                            s2=stat();

                            state._fsp--;

                             
                            if ( _first_1==null ) _first_1 = s2.tree;


                            retval.tree = (CtrlTree)_first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                            }
                            break;

                    }


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                     retval.aut = builder.buildIfThenElse((c!=null?c.aut:null), (s1!=null?s1.aut:null), (s2!=null?s2.aut:null)); 

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 10 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:121:5: ^( CHOICE s1= stat (s2= stat )* )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    CHOICE45=(CtrlTree)match(input,CHOICE,FOLLOW_CHOICE_in_stat534); 


                    if ( _first_0==null ) _first_0 = CHOICE45;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat546);
                    s1=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s1.tree;


                     retval.aut = (s1!=null?s1.aut:null); 

                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:124:8: (s2= stat )*
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);

                        if ( (LA10_0==ALAP||LA10_0==ANY||LA10_0==ATOM||LA10_0==BLOCK||(LA10_0 >= CALL && LA10_0 <= CHOICE)||LA10_0==IF||LA10_0==OTHER||LA10_0==SEMI||LA10_0==STAR||(LA10_0 >= TRUE && LA10_0 <= WHILE)) ) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:124:10: s2= stat
                    	    {
                    	    _last = (CtrlTree)input.LT(1);
                    	    pushFollow(FOLLOW_stat_in_stat568);
                    	    s2=stat();

                    	    state._fsp--;

                    	     
                    	    if ( _first_1==null ) _first_1 = s2.tree;


                    	     retval.aut = builder.buildOr(retval.aut, (s2!=null?s2.aut:null)); 

                    	    retval.tree = (CtrlTree)_first_0;
                    	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                    	        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    	    }
                    	    break;

                    	default :
                    	    break loop10;
                        }
                    } while (true);


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 11 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:128:5: ^( STAR s= stat )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    STAR46=(CtrlTree)match(input,STAR,FOLLOW_STAR_in_stat603); 


                    if ( _first_0==null ) _first_0 = STAR46;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat607);
                    s=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                     retval.aut = builder.buildStar((s!=null?s.aut:null)); 

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 12 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:130:5: rule
                    {
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_rule_in_stat620);
                    rule47=rule();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = rule47.tree;


                     retval.aut = builder.buildCall((rule47!=null?((CtrlTree)rule47.tree):null).getCtrlCall(), namespace); 

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 13 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:132:5: ANY
                    {
                    _last = (CtrlTree)input.LT(1);
                    ANY48=(CtrlTree)match(input,ANY,FOLLOW_ANY_in_stat632); 
                     
                    if ( _first_0==null ) _first_0 = ANY48;


                     retval.aut = builder.buildAny(namespace); 

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 14 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:134:5: OTHER
                    {
                    _last = (CtrlTree)input.LT(1);
                    OTHER49=(CtrlTree)match(input,OTHER,FOLLOW_OTHER_in_stat644); 
                     
                    if ( _first_0==null ) _first_0 = OTHER49;


                     retval.aut = builder.buildOther(namespace); 
                        

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 15 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:137:5: TRUE
                    {
                    _last = (CtrlTree)input.LT(1);
                    TRUE50=(CtrlTree)match(input,TRUE,FOLLOW_TRUE_in_stat656); 
                     
                    if ( _first_0==null ) _first_0 = TRUE50;


                     retval.aut = builder.buildTrue(); 

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

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
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "rule"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:141:1: rule : ^( CALL ID ( ^( ARGS ( arg )* RPAR ) )? ) ;
    public final CtrlBuilder.rule_return rule() throws RecognitionException {
        CtrlBuilder.rule_return retval = new CtrlBuilder.rule_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree CALL51=null;
        CtrlTree ID52=null;
        CtrlTree ARGS53=null;
        CtrlTree RPAR55=null;
        CtrlBuilder.arg_return arg54 =null;


        CtrlTree CALL51_tree=null;
        CtrlTree ID52_tree=null;
        CtrlTree ARGS53_tree=null;
        CtrlTree RPAR55_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:142:3: ( ^( CALL ID ( ^( ARGS ( arg )* RPAR ) )? ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:142:5: ^( CALL ID ( ^( ARGS ( arg )* RPAR ) )? )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            CALL51=(CtrlTree)match(input,CALL,FOLLOW_CALL_in_rule676); 


            if ( _first_0==null ) _first_0 = CALL51;
            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            ID52=(CtrlTree)match(input,ID,FOLLOW_ID_in_rule678); 
             
            if ( _first_1==null ) _first_1 = ID52;


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:142:15: ( ^( ARGS ( arg )* RPAR ) )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==ARGS) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:142:16: ^( ARGS ( arg )* RPAR )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_2 = _last;
                    CtrlTree _first_2 = null;
                    _last = (CtrlTree)input.LT(1);
                    ARGS53=(CtrlTree)match(input,ARGS,FOLLOW_ARGS_in_rule682); 


                    if ( _first_1==null ) _first_1 = ARGS53;
                    match(input, Token.DOWN, null); 
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:142:23: ( arg )*
                    loop12:
                    do {
                        int alt12=2;
                        int LA12_0 = input.LA(1);

                        if ( (LA12_0==ARG) ) {
                            alt12=1;
                        }


                        switch (alt12) {
                    	case 1 :
                    	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:142:23: arg
                    	    {
                    	    _last = (CtrlTree)input.LT(1);
                    	    pushFollow(FOLLOW_arg_in_rule684);
                    	    arg54=arg();

                    	    state._fsp--;

                    	     
                    	    if ( _first_2==null ) _first_2 = arg54.tree;


                    	    retval.tree = (CtrlTree)_first_0;
                    	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                    	        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    	    }
                    	    break;

                    	default :
                    	    break loop12;
                        }
                    } while (true);


                    _last = (CtrlTree)input.LT(1);
                    RPAR55=(CtrlTree)match(input,RPAR,FOLLOW_RPAR_in_rule687); 
                     
                    if ( _first_2==null ) _first_2 = RPAR55;


                    match(input, Token.UP, null); 
                    _last = _save_last_2;
                    }


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;

            }


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = (CtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

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
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "var_decl"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:145:1: var_decl : ^( VAR type ( ID )+ ) ;
    public final CtrlBuilder.var_decl_return var_decl() throws RecognitionException {
        CtrlBuilder.var_decl_return retval = new CtrlBuilder.var_decl_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree VAR56=null;
        CtrlTree ID58=null;
        CtrlBuilder.type_return type57 =null;


        CtrlTree VAR56_tree=null;
        CtrlTree ID58_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:146:2: ( ^( VAR type ( ID )+ ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:146:4: ^( VAR type ( ID )+ )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            VAR56=(CtrlTree)match(input,VAR,FOLLOW_VAR_in_var_decl705); 


            if ( _first_0==null ) _first_0 = VAR56;
            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_type_in_var_decl707);
            type57=type();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = type57.tree;


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:146:16: ( ID )+
            int cnt14=0;
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( (LA14_0==ID) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:146:16: ID
            	    {
            	    _last = (CtrlTree)input.LT(1);
            	    ID58=(CtrlTree)match(input,ID,FOLLOW_ID_in_var_decl709); 
            	     
            	    if ( _first_1==null ) _first_1 = ID58;


            	    retval.tree = (CtrlTree)_first_0;
            	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
            	        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

            	    }
            	    break;

            	default :
            	    if ( cnt14 >= 1 ) break loop14;
                        EarlyExitException eee =
                            new EarlyExitException(14, input);
                        throw eee;
                }
                cnt14++;
            } while (true);


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = (CtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

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
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "type"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:149:1: type : ( NODE | BOOL | STRING | INT | REAL );
    public final CtrlBuilder.type_return type() throws RecognitionException {
        CtrlBuilder.type_return retval = new CtrlBuilder.type_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree set59=null;

        CtrlTree set59_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:150:3: ( NODE | BOOL | STRING | INT | REAL )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:
            {
            _last = (CtrlTree)input.LT(1);
            set59=(CtrlTree)input.LT(1);

            if ( input.LA(1)==BOOL||input.LA(1)==INT||input.LA(1)==NODE||input.LA(1)==REAL||input.LA(1)==STRING ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }

            retval.tree = (CtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);
             

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
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "arg"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:153:1: arg : ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) ) ;
    public final CtrlBuilder.arg_return arg() throws RecognitionException {
        CtrlBuilder.arg_return retval = new CtrlBuilder.arg_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree ARG60=null;
        CtrlTree OUT61=null;
        CtrlTree ID62=null;
        CtrlTree DONT_CARE63=null;
        CtrlBuilder.literal_return literal64 =null;


        CtrlTree ARG60_tree=null;
        CtrlTree OUT61_tree=null;
        CtrlTree ID62_tree=null;
        CtrlTree DONT_CARE63_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:154:2: ( ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:154:4: ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            ARG60=(CtrlTree)match(input,ARG,FOLLOW_ARG_in_arg756); 


            if ( _first_0==null ) _first_0 = ARG60;
            match(input, Token.DOWN, null); 
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:154:11: ( ( OUT )? ID | DONT_CARE | literal )
            int alt16=3;
            switch ( input.LA(1) ) {
            case ID:
            case OUT:
                {
                alt16=1;
                }
                break;
            case DONT_CARE:
                {
                alt16=2;
                }
                break;
            case FALSE:
            case INT_LIT:
            case REAL_LIT:
            case STRING_LIT:
            case TRUE:
                {
                alt16=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 16, 0, input);

                throw nvae;

            }

            switch (alt16) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:154:13: ( OUT )? ID
                    {
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:154:13: ( OUT )?
                    int alt15=2;
                    int LA15_0 = input.LA(1);

                    if ( (LA15_0==OUT) ) {
                        alt15=1;
                    }
                    switch (alt15) {
                        case 1 :
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:154:13: OUT
                            {
                            _last = (CtrlTree)input.LT(1);
                            OUT61=(CtrlTree)match(input,OUT,FOLLOW_OUT_in_arg760); 
                             
                            if ( _first_1==null ) _first_1 = OUT61;


                            retval.tree = (CtrlTree)_first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                            }
                            break;

                    }


                    _last = (CtrlTree)input.LT(1);
                    ID62=(CtrlTree)match(input,ID,FOLLOW_ID_in_arg763); 
                     
                    if ( _first_1==null ) _first_1 = ID62;


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 2 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:154:23: DONT_CARE
                    {
                    _last = (CtrlTree)input.LT(1);
                    DONT_CARE63=(CtrlTree)match(input,DONT_CARE,FOLLOW_DONT_CARE_in_arg767); 
                     
                    if ( _first_1==null ) _first_1 = DONT_CARE63;


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 3 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:154:35: literal
                    {
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_literal_in_arg771);
                    literal64=literal();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = literal64.tree;


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;

            }


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = (CtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

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
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "literal"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:157:1: literal : ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT );
    public final CtrlBuilder.literal_return literal() throws RecognitionException {
        CtrlBuilder.literal_return retval = new CtrlBuilder.literal_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree set65=null;

        CtrlTree set65_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:158:3: ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:
            {
            _last = (CtrlTree)input.LT(1);
            set65=(CtrlTree)input.LT(1);

            if ( input.LA(1)==FALSE||input.LA(1)==INT_LIT||input.LA(1)==REAL_LIT||(input.LA(1) >= STRING_LIT && input.LA(1) <= TRUE) ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }

            retval.tree = (CtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);
             

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
    public static final BitSet FOLLOW_package_decl_in_program61 = new BitSet(new long[]{0x0000000048000000L});
    public static final BitSet FOLLOW_import_decl_in_program63 = new BitSet(new long[]{0x0000000048000000L});
    public static final BitSet FOLLOW_functions_in_program66 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_recipes_in_program68 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_block_in_program70 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PACKAGE_in_package_decl91 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_package_decl93 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_SEMI_in_package_decl95 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IMPORT_in_import_decl112 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_import_decl114 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_SEMI_in_import_decl116 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FUNCTIONS_in_functions131 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_function_in_functions133 = new BitSet(new long[]{0x0000000004000008L});
    public static final BitSet FOLLOW_FUNCTION_in_function149 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_function151 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_par_list_in_function153 = new BitSet(new long[]{0x0000000100001000L});
    public static final BitSet FOLLOW_priority_in_function155 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_block_in_function157 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RECIPES_in_recipes180 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_recipe_in_recipes182 = new BitSet(new long[]{0x0040000000000008L});
    public static final BitSet FOLLOW_RECIPE_in_recipe198 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_recipe200 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_par_list_in_recipe202 = new BitSet(new long[]{0x0000000100001000L});
    public static final BitSet FOLLOW_priority_in_recipe204 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_block_in_recipe206 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_INT_LIT_in_priority228 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PARS_in_par_list245 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_par_decl_in_par_list247 = new BitSet(new long[]{0x0000200000000008L});
    public static final BitSet FOLLOW_PAR_in_par_decl264 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_OUT_in_par_decl266 = new BitSet(new long[]{0x2010004080002000L});
    public static final BitSet FOLLOW_type_in_par_decl269 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_par_decl271 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BLOCK_in_block291 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_block311 = new BitSet(new long[]{0x9200040020019458L,0x000000000000000FL});
    public static final BitSet FOLLOW_SEMI_in_stat357 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat361 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_block_in_stat374 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_decl_in_stat386 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALAP_in_stat399 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat403 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ATOM_in_stat417 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat421 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WHILE_in_stat435 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat439 = new BitSet(new long[]{0x9200040020019450L,0x000000000000000FL});
    public static final BitSet FOLLOW_stat_in_stat443 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_UNTIL_in_stat457 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat461 = new BitSet(new long[]{0x9200040020019450L,0x000000000000000FL});
    public static final BitSet FOLLOW_stat_in_stat465 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TRY_in_stat479 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat483 = new BitSet(new long[]{0x9200040020019458L,0x000000000000000FL});
    public static final BitSet FOLLOW_stat_in_stat488 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IF_in_stat504 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat508 = new BitSet(new long[]{0x9200040020019450L,0x000000000000000FL});
    public static final BitSet FOLLOW_stat_in_stat512 = new BitSet(new long[]{0x9200040020019458L,0x000000000000000FL});
    public static final BitSet FOLLOW_stat_in_stat517 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CHOICE_in_stat534 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat546 = new BitSet(new long[]{0x9200040020019458L,0x000000000000000FL});
    public static final BitSet FOLLOW_stat_in_stat568 = new BitSet(new long[]{0x9200040020019458L,0x000000000000000FL});
    public static final BitSet FOLLOW_STAR_in_stat603 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat607 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_rule_in_stat620 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_stat632 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_stat644 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_stat656 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CALL_in_rule676 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rule678 = new BitSet(new long[]{0x0000000000000108L});
    public static final BitSet FOLLOW_ARGS_in_rule682 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_arg_in_rule684 = new BitSet(new long[]{0x0100000000000080L});
    public static final BitSet FOLLOW_RPAR_in_rule687 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VAR_in_var_decl705 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_type_in_var_decl707 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_var_decl709 = new BitSet(new long[]{0x0000000010000008L});
    public static final BitSet FOLLOW_ARG_in_arg756 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_OUT_in_arg760 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_arg763 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DONT_CARE_in_arg767 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_literal_in_arg771 = new BitSet(new long[]{0x0000000000000008L});

}