// $ANTLR 3.4 D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g 2014-01-10 13:55:06

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ALAP", "AMP", "ANY", "ARG", "ARGS", "ASTERISK", "BAR", "BLOCK", "BOOL", "BSLASH", "CALL", "CHOICE", "COMMA", "DO", "DONT_CARE", "DOT", "DO_UNTIL", "DO_WHILE", "ELSE", "EscapeSequence", "FALSE", "FUNCTION", "FUNCTIONS", "ID", "IF", "IMPORT", "INT", "INT_LIT", "IntegerNumber", "LCURLY", "LPAR", "MINUS", "ML_COMMENT", "NODE", "NOT", "NonIntegerNumber", "OR", "OTHER", "OUT", "PACKAGE", "PAR", "PARS", "PLUS", "PRIORITY", "PROGRAM", "QUOTE", "RCURLY", "REAL", "REAL_LIT", "RECIPE", "RECIPES", "RPAR", "SEMI", "SHARP", "SL_COMMENT", "STAR", "STRING", "STRING_LIT", "TRUE", "TRY", "UNTIL", "VAR", "WHILE", "WS"
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
    public static final int PAR=44;
    public static final int PARS=45;
    public static final int PLUS=46;
    public static final int PRIORITY=47;
    public static final int PROGRAM=48;
    public static final int QUOTE=49;
    public static final int RCURLY=50;
    public static final int REAL=51;
    public static final int REAL_LIT=52;
    public static final int RECIPE=53;
    public static final int RECIPES=54;
    public static final int RPAR=55;
    public static final int SEMI=56;
    public static final int SHARP=57;
    public static final int SL_COMMENT=58;
    public static final int STAR=59;
    public static final int STRING=60;
    public static final int STRING_LIT=61;
    public static final int TRUE=62;
    public static final int TRY=63;
    public static final int UNTIL=64;
    public static final int VAR=65;
    public static final int WHILE=66;
    public static final int WS=67;

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


             // at least one child due to closing RCURLY
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:59:1: function : ^( FUNCTION ID PARS ( INT_LIT )? block ) ;
    public final CtrlBuilder.function_return function() throws RecognitionException {
        CtrlBuilder.function_return retval = new CtrlBuilder.function_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree FUNCTION15=null;
        CtrlTree ID16=null;
        CtrlTree PARS17=null;
        CtrlTree INT_LIT18=null;
        CtrlBuilder.block_return block19 =null;


        CtrlTree FUNCTION15_tree=null;
        CtrlTree ID16_tree=null;
        CtrlTree PARS17_tree=null;
        CtrlTree INT_LIT18_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:60:3: ( ^( FUNCTION ID PARS ( INT_LIT )? block ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:60:5: ^( FUNCTION ID PARS ( INT_LIT )? block )
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
            PARS17=(CtrlTree)match(input,PARS,FOLLOW_PARS_in_function153); 
             
            if ( _first_1==null ) _first_1 = PARS17;


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:60:24: ( INT_LIT )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==INT_LIT) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:60:24: INT_LIT
                    {
                    _last = (CtrlTree)input.LT(1);
                    INT_LIT18=(CtrlTree)match(input,INT_LIT,FOLLOW_INT_LIT_in_function155); 
                     
                    if ( _first_1==null ) _first_1 = INT_LIT18;


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;

            }


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_block_in_function158);
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:64:1: recipes : ^( RECIPES ( recipe )* ) ;
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:65:3: ( ^( RECIPES ( recipe )* ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:65:5: ^( RECIPES ( recipe )* )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            RECIPES20=(CtrlTree)match(input,RECIPES,FOLLOW_RECIPES_in_recipes181); 


            if ( _first_0==null ) _first_0 = RECIPES20;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:65:15: ( recipe )*
                loop4:
                do {
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==RECIPE) ) {
                        alt4=1;
                    }


                    switch (alt4) {
                	case 1 :
                	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:65:15: recipe
                	    {
                	    _last = (CtrlTree)input.LT(1);
                	    pushFollow(FOLLOW_recipe_in_recipes183);
                	    recipe21=recipe();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = recipe21.tree;


                	    retval.tree = (CtrlTree)_first_0;
                	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                	        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:68:1: recipe : ^( RECIPE ID PARS ( INT_LIT )? block ) ;
    public final CtrlBuilder.recipe_return recipe() throws RecognitionException {
        CtrlBuilder.recipe_return retval = new CtrlBuilder.recipe_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree RECIPE22=null;
        CtrlTree ID23=null;
        CtrlTree PARS24=null;
        CtrlTree INT_LIT25=null;
        CtrlBuilder.block_return block26 =null;


        CtrlTree RECIPE22_tree=null;
        CtrlTree ID23_tree=null;
        CtrlTree PARS24_tree=null;
        CtrlTree INT_LIT25_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:69:3: ( ^( RECIPE ID PARS ( INT_LIT )? block ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:69:5: ^( RECIPE ID PARS ( INT_LIT )? block )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            RECIPE22=(CtrlTree)match(input,RECIPE,FOLLOW_RECIPE_in_recipe199); 


            if ( _first_0==null ) _first_0 = RECIPE22;
            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            ID23=(CtrlTree)match(input,ID,FOLLOW_ID_in_recipe201); 
             
            if ( _first_1==null ) _first_1 = ID23;


            _last = (CtrlTree)input.LT(1);
            PARS24=(CtrlTree)match(input,PARS,FOLLOW_PARS_in_recipe203); 
             
            if ( _first_1==null ) _first_1 = PARS24;


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:69:22: ( INT_LIT )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==INT_LIT) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:69:22: INT_LIT
                    {
                    _last = (CtrlTree)input.LT(1);
                    INT_LIT25=(CtrlTree)match(input,INT_LIT,FOLLOW_INT_LIT_in_recipe205); 
                     
                    if ( _first_1==null ) _first_1 = INT_LIT25;


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;

            }


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_block_in_recipe208);
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


    public static class block_return extends TreeRuleReturnScope {
        public CtrlAut aut;
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "block"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:77:1: block returns [ CtrlAut aut ] : ^( BLOCK ( stat )* RCURLY ) ;
    public final CtrlBuilder.block_return block() throws RecognitionException {
        CtrlBuilder.block_return retval = new CtrlBuilder.block_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree BLOCK27=null;
        CtrlTree RCURLY29=null;
        CtrlBuilder.stat_return stat28 =null;


        CtrlTree BLOCK27_tree=null;
        CtrlTree RCURLY29_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:78:3: ( ^( BLOCK ( stat )* RCURLY ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:78:5: ^( BLOCK ( stat )* RCURLY )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            BLOCK27=(CtrlTree)match(input,BLOCK,FOLLOW_BLOCK_in_block234); 


            if ( _first_0==null ) _first_0 = BLOCK27;
             retval.aut = builder.buildTrue(); 

            match(input, Token.DOWN, null); 
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:80:8: ( stat )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==ALAP||LA6_0==ANY||LA6_0==BLOCK||(LA6_0 >= CALL && LA6_0 <= CHOICE)||LA6_0==IF||LA6_0==OTHER||LA6_0==SEMI||LA6_0==STAR||(LA6_0 >= TRUE && LA6_0 <= WHILE)) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:80:10: stat
            	    {
            	    _last = (CtrlTree)input.LT(1);
            	    pushFollow(FOLLOW_stat_in_block254);
            	    stat28=stat();

            	    state._fsp--;

            	     
            	    if ( _first_1==null ) _first_1 = stat28.tree;


            	     retval.aut = builder.buildSeq(retval.aut, (stat28!=null?stat28.aut:null)); 

            	    retval.tree = (CtrlTree)_first_0;
            	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
            	        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);


            _last = (CtrlTree)input.LT(1);
            RCURLY29=(CtrlTree)match(input,RCURLY,FOLLOW_RCURLY_in_block284); 
             
            if ( _first_1==null ) _first_1 = RCURLY29;


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
    // $ANTLR end "block"


    public static class stat_return extends TreeRuleReturnScope {
        public CtrlAut aut;
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "stat"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:87:1: stat returns [ CtrlAut aut ] : ( ^( SEMI s= stat ) | block | var_decl | ^( ALAP s= stat ) | ^( WHILE c= stat s= stat ) | ^( UNTIL c= stat s= stat ) | ^( TRY s1= stat (s2= stat )? ) | ^( IF c= stat s1= stat (s2= stat )? ) | ^( CHOICE s1= stat (s2= stat )* ) | ^( STAR s= stat ) | rule | ANY | OTHER | TRUE );
    public final CtrlBuilder.stat_return stat() throws RecognitionException {
        CtrlBuilder.stat_return retval = new CtrlBuilder.stat_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree SEMI30=null;
        CtrlTree ALAP33=null;
        CtrlTree WHILE34=null;
        CtrlTree UNTIL35=null;
        CtrlTree TRY36=null;
        CtrlTree IF37=null;
        CtrlTree CHOICE38=null;
        CtrlTree STAR39=null;
        CtrlTree ANY41=null;
        CtrlTree OTHER42=null;
        CtrlTree TRUE43=null;
        CtrlBuilder.stat_return s =null;

        CtrlBuilder.stat_return c =null;

        CtrlBuilder.stat_return s1 =null;

        CtrlBuilder.stat_return s2 =null;

        CtrlBuilder.block_return block31 =null;

        CtrlBuilder.var_decl_return var_decl32 =null;

        CtrlBuilder.rule_return rule40 =null;


        CtrlTree SEMI30_tree=null;
        CtrlTree ALAP33_tree=null;
        CtrlTree WHILE34_tree=null;
        CtrlTree UNTIL35_tree=null;
        CtrlTree TRY36_tree=null;
        CtrlTree IF37_tree=null;
        CtrlTree CHOICE38_tree=null;
        CtrlTree STAR39_tree=null;
        CtrlTree ANY41_tree=null;
        CtrlTree OTHER42_tree=null;
        CtrlTree TRUE43_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:88:3: ( ^( SEMI s= stat ) | block | var_decl | ^( ALAP s= stat ) | ^( WHILE c= stat s= stat ) | ^( UNTIL c= stat s= stat ) | ^( TRY s1= stat (s2= stat )? ) | ^( IF c= stat s1= stat (s2= stat )? ) | ^( CHOICE s1= stat (s2= stat )* ) | ^( STAR s= stat ) | rule | ANY | OTHER | TRUE )
            int alt10=14;
            switch ( input.LA(1) ) {
            case SEMI:
                {
                alt10=1;
                }
                break;
            case BLOCK:
                {
                alt10=2;
                }
                break;
            case VAR:
                {
                alt10=3;
                }
                break;
            case ALAP:
                {
                alt10=4;
                }
                break;
            case WHILE:
                {
                alt10=5;
                }
                break;
            case UNTIL:
                {
                alt10=6;
                }
                break;
            case TRY:
                {
                alt10=7;
                }
                break;
            case IF:
                {
                alt10=8;
                }
                break;
            case CHOICE:
                {
                alt10=9;
                }
                break;
            case STAR:
                {
                alt10=10;
                }
                break;
            case CALL:
                {
                alt10=11;
                }
                break;
            case ANY:
                {
                alt10=12;
                }
                break;
            case OTHER:
                {
                alt10=13;
                }
                break;
            case TRUE:
                {
                alt10=14;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;

            }

            switch (alt10) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:88:5: ^( SEMI s= stat )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    SEMI30=(CtrlTree)match(input,SEMI,FOLLOW_SEMI_in_stat309); 


                    if ( _first_0==null ) _first_0 = SEMI30;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat313);
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:90:5: block
                    {
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_block_in_stat326);
                    block31=block();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = block31.tree;


                     retval.aut = (block31!=null?block31.aut:null); 

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 3 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:92:5: var_decl
                    {
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_var_decl_in_stat338);
                    var_decl32=var_decl();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = var_decl32.tree;


                     retval.aut = builder.buildTrue(); 

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 4 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:94:5: ^( ALAP s= stat )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    ALAP33=(CtrlTree)match(input,ALAP,FOLLOW_ALAP_in_stat351); 


                    if ( _first_0==null ) _first_0 = ALAP33;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat355);
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:96:5: ^( WHILE c= stat s= stat )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    WHILE34=(CtrlTree)match(input,WHILE,FOLLOW_WHILE_in_stat369); 


                    if ( _first_0==null ) _first_0 = WHILE34;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat373);
                    c=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = c.tree;


                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat377);
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
                case 6 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:98:5: ^( UNTIL c= stat s= stat )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    UNTIL35=(CtrlTree)match(input,UNTIL,FOLLOW_UNTIL_in_stat391); 


                    if ( _first_0==null ) _first_0 = UNTIL35;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat395);
                    c=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = c.tree;


                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat399);
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
                case 7 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:100:5: ^( TRY s1= stat (s2= stat )? )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    TRY36=(CtrlTree)match(input,TRY,FOLLOW_TRY_in_stat413); 


                    if ( _first_0==null ) _first_0 = TRY36;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat417);
                    s1=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s1.tree;


                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:100:19: (s2= stat )?
                    int alt7=2;
                    int LA7_0 = input.LA(1);

                    if ( (LA7_0==ALAP||LA7_0==ANY||LA7_0==BLOCK||(LA7_0 >= CALL && LA7_0 <= CHOICE)||LA7_0==IF||LA7_0==OTHER||LA7_0==SEMI||LA7_0==STAR||(LA7_0 >= TRUE && LA7_0 <= WHILE)) ) {
                        alt7=1;
                    }
                    switch (alt7) {
                        case 1 :
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:100:20: s2= stat
                            {
                            _last = (CtrlTree)input.LT(1);
                            pushFollow(FOLLOW_stat_in_stat422);
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
                case 8 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:102:5: ^( IF c= stat s1= stat (s2= stat )? )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    IF37=(CtrlTree)match(input,IF,FOLLOW_IF_in_stat438); 


                    if ( _first_0==null ) _first_0 = IF37;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat442);
                    c=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = c.tree;


                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat446);
                    s1=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s1.tree;


                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:102:25: (s2= stat )?
                    int alt8=2;
                    int LA8_0 = input.LA(1);

                    if ( (LA8_0==ALAP||LA8_0==ANY||LA8_0==BLOCK||(LA8_0 >= CALL && LA8_0 <= CHOICE)||LA8_0==IF||LA8_0==OTHER||LA8_0==SEMI||LA8_0==STAR||(LA8_0 >= TRUE && LA8_0 <= WHILE)) ) {
                        alt8=1;
                    }
                    switch (alt8) {
                        case 1 :
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:102:26: s2= stat
                            {
                            _last = (CtrlTree)input.LT(1);
                            pushFollow(FOLLOW_stat_in_stat451);
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
                case 9 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:104:5: ^( CHOICE s1= stat (s2= stat )* )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    CHOICE38=(CtrlTree)match(input,CHOICE,FOLLOW_CHOICE_in_stat468); 


                    if ( _first_0==null ) _first_0 = CHOICE38;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat480);
                    s1=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s1.tree;


                     retval.aut = (s1!=null?s1.aut:null); 

                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:107:8: (s2= stat )*
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( (LA9_0==ALAP||LA9_0==ANY||LA9_0==BLOCK||(LA9_0 >= CALL && LA9_0 <= CHOICE)||LA9_0==IF||LA9_0==OTHER||LA9_0==SEMI||LA9_0==STAR||(LA9_0 >= TRUE && LA9_0 <= WHILE)) ) {
                            alt9=1;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:107:10: s2= stat
                    	    {
                    	    _last = (CtrlTree)input.LT(1);
                    	    pushFollow(FOLLOW_stat_in_stat502);
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
                    	    break loop9;
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
                case 10 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:111:5: ^( STAR s= stat )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    STAR39=(CtrlTree)match(input,STAR,FOLLOW_STAR_in_stat537); 


                    if ( _first_0==null ) _first_0 = STAR39;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat541);
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
                case 11 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:113:5: rule
                    {
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_rule_in_stat554);
                    rule40=rule();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = rule40.tree;


                     retval.aut = builder.buildCall((rule40!=null?((CtrlTree)rule40.tree):null).getCtrlCall(), namespace); 

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 12 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:115:5: ANY
                    {
                    _last = (CtrlTree)input.LT(1);
                    ANY41=(CtrlTree)match(input,ANY,FOLLOW_ANY_in_stat566); 
                     
                    if ( _first_0==null ) _first_0 = ANY41;


                     retval.aut = builder.buildAny(namespace); 

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 13 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:117:5: OTHER
                    {
                    _last = (CtrlTree)input.LT(1);
                    OTHER42=(CtrlTree)match(input,OTHER,FOLLOW_OTHER_in_stat578); 
                     
                    if ( _first_0==null ) _first_0 = OTHER42;


                     retval.aut = builder.buildOther(namespace); 
                        

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 14 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:120:5: TRUE
                    {
                    _last = (CtrlTree)input.LT(1);
                    TRUE43=(CtrlTree)match(input,TRUE,FOLLOW_TRUE_in_stat590); 
                     
                    if ( _first_0==null ) _first_0 = TRUE43;


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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:124:1: rule : ^( CALL ID ( ^( ARGS ( arg )* RPAR ) )? ) ;
    public final CtrlBuilder.rule_return rule() throws RecognitionException {
        CtrlBuilder.rule_return retval = new CtrlBuilder.rule_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree CALL44=null;
        CtrlTree ID45=null;
        CtrlTree ARGS46=null;
        CtrlTree RPAR48=null;
        CtrlBuilder.arg_return arg47 =null;


        CtrlTree CALL44_tree=null;
        CtrlTree ID45_tree=null;
        CtrlTree ARGS46_tree=null;
        CtrlTree RPAR48_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:125:3: ( ^( CALL ID ( ^( ARGS ( arg )* RPAR ) )? ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:125:5: ^( CALL ID ( ^( ARGS ( arg )* RPAR ) )? )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            CALL44=(CtrlTree)match(input,CALL,FOLLOW_CALL_in_rule610); 


            if ( _first_0==null ) _first_0 = CALL44;
            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            ID45=(CtrlTree)match(input,ID,FOLLOW_ID_in_rule612); 
             
            if ( _first_1==null ) _first_1 = ID45;


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:125:15: ( ^( ARGS ( arg )* RPAR ) )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==ARGS) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:125:16: ^( ARGS ( arg )* RPAR )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_2 = _last;
                    CtrlTree _first_2 = null;
                    _last = (CtrlTree)input.LT(1);
                    ARGS46=(CtrlTree)match(input,ARGS,FOLLOW_ARGS_in_rule616); 


                    if ( _first_1==null ) _first_1 = ARGS46;
                    match(input, Token.DOWN, null); 
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:125:23: ( arg )*
                    loop11:
                    do {
                        int alt11=2;
                        int LA11_0 = input.LA(1);

                        if ( (LA11_0==ARG) ) {
                            alt11=1;
                        }


                        switch (alt11) {
                    	case 1 :
                    	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:125:23: arg
                    	    {
                    	    _last = (CtrlTree)input.LT(1);
                    	    pushFollow(FOLLOW_arg_in_rule618);
                    	    arg47=arg();

                    	    state._fsp--;

                    	     
                    	    if ( _first_2==null ) _first_2 = arg47.tree;


                    	    retval.tree = (CtrlTree)_first_0;
                    	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                    	        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    	    }
                    	    break;

                    	default :
                    	    break loop11;
                        }
                    } while (true);


                    _last = (CtrlTree)input.LT(1);
                    RPAR48=(CtrlTree)match(input,RPAR,FOLLOW_RPAR_in_rule621); 
                     
                    if ( _first_2==null ) _first_2 = RPAR48;


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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:128:1: var_decl : ^( VAR type ( ID )+ ) ;
    public final CtrlBuilder.var_decl_return var_decl() throws RecognitionException {
        CtrlBuilder.var_decl_return retval = new CtrlBuilder.var_decl_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree VAR49=null;
        CtrlTree ID51=null;
        CtrlBuilder.type_return type50 =null;


        CtrlTree VAR49_tree=null;
        CtrlTree ID51_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:129:2: ( ^( VAR type ( ID )+ ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:129:4: ^( VAR type ( ID )+ )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            VAR49=(CtrlTree)match(input,VAR,FOLLOW_VAR_in_var_decl639); 


            if ( _first_0==null ) _first_0 = VAR49;
            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_type_in_var_decl641);
            type50=type();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = type50.tree;


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:129:16: ( ID )+
            int cnt13=0;
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( (LA13_0==ID) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:129:16: ID
            	    {
            	    _last = (CtrlTree)input.LT(1);
            	    ID51=(CtrlTree)match(input,ID,FOLLOW_ID_in_var_decl643); 
            	     
            	    if ( _first_1==null ) _first_1 = ID51;


            	    retval.tree = (CtrlTree)_first_0;
            	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
            	        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

            	    }
            	    break;

            	default :
            	    if ( cnt13 >= 1 ) break loop13;
                        EarlyExitException eee =
                            new EarlyExitException(13, input);
                        throw eee;
                }
                cnt13++;
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:132:1: type : ( NODE | BOOL | STRING | INT | REAL );
    public final CtrlBuilder.type_return type() throws RecognitionException {
        CtrlBuilder.type_return retval = new CtrlBuilder.type_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree set52=null;

        CtrlTree set52_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:133:3: ( NODE | BOOL | STRING | INT | REAL )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:
            {
            _last = (CtrlTree)input.LT(1);
            set52=(CtrlTree)input.LT(1);

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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:136:1: arg : ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) ) ;
    public final CtrlBuilder.arg_return arg() throws RecognitionException {
        CtrlBuilder.arg_return retval = new CtrlBuilder.arg_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree ARG53=null;
        CtrlTree OUT54=null;
        CtrlTree ID55=null;
        CtrlTree DONT_CARE56=null;
        CtrlBuilder.literal_return literal57 =null;


        CtrlTree ARG53_tree=null;
        CtrlTree OUT54_tree=null;
        CtrlTree ID55_tree=null;
        CtrlTree DONT_CARE56_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:137:2: ( ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:137:4: ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            ARG53=(CtrlTree)match(input,ARG,FOLLOW_ARG_in_arg690); 


            if ( _first_0==null ) _first_0 = ARG53;
            match(input, Token.DOWN, null); 
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:137:11: ( ( OUT )? ID | DONT_CARE | literal )
            int alt15=3;
            switch ( input.LA(1) ) {
            case ID:
            case OUT:
                {
                alt15=1;
                }
                break;
            case DONT_CARE:
                {
                alt15=2;
                }
                break;
            case FALSE:
            case INT_LIT:
            case REAL_LIT:
            case STRING_LIT:
            case TRUE:
                {
                alt15=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;

            }

            switch (alt15) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:137:13: ( OUT )? ID
                    {
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:137:13: ( OUT )?
                    int alt14=2;
                    int LA14_0 = input.LA(1);

                    if ( (LA14_0==OUT) ) {
                        alt14=1;
                    }
                    switch (alt14) {
                        case 1 :
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:137:13: OUT
                            {
                            _last = (CtrlTree)input.LT(1);
                            OUT54=(CtrlTree)match(input,OUT,FOLLOW_OUT_in_arg694); 
                             
                            if ( _first_1==null ) _first_1 = OUT54;


                            retval.tree = (CtrlTree)_first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                            }
                            break;

                    }


                    _last = (CtrlTree)input.LT(1);
                    ID55=(CtrlTree)match(input,ID,FOLLOW_ID_in_arg697); 
                     
                    if ( _first_1==null ) _first_1 = ID55;


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 2 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:137:23: DONT_CARE
                    {
                    _last = (CtrlTree)input.LT(1);
                    DONT_CARE56=(CtrlTree)match(input,DONT_CARE,FOLLOW_DONT_CARE_in_arg701); 
                     
                    if ( _first_1==null ) _first_1 = DONT_CARE56;


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 3 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:137:35: literal
                    {
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_literal_in_arg705);
                    literal57=literal();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = literal57.tree;


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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:140:1: literal : ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT );
    public final CtrlBuilder.literal_return literal() throws RecognitionException {
        CtrlBuilder.literal_return retval = new CtrlBuilder.literal_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree set58=null;

        CtrlTree set58_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:141:3: ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:
            {
            _last = (CtrlTree)input.LT(1);
            set58=(CtrlTree)input.LT(1);

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
    public static final BitSet FOLLOW_package_decl_in_program61 = new BitSet(new long[]{0x0000000024000000L});
    public static final BitSet FOLLOW_import_decl_in_program63 = new BitSet(new long[]{0x0000000024000000L});
    public static final BitSet FOLLOW_functions_in_program66 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_recipes_in_program68 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_block_in_program70 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PACKAGE_in_package_decl91 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_package_decl93 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_SEMI_in_package_decl95 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IMPORT_in_import_decl112 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_import_decl114 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_SEMI_in_import_decl116 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FUNCTIONS_in_functions131 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_function_in_functions133 = new BitSet(new long[]{0x0000000002000008L});
    public static final BitSet FOLLOW_FUNCTION_in_function149 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_function151 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_PARS_in_function153 = new BitSet(new long[]{0x0000000080000800L});
    public static final BitSet FOLLOW_INT_LIT_in_function155 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_block_in_function158 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RECIPES_in_recipes181 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_recipe_in_recipes183 = new BitSet(new long[]{0x0020000000000008L});
    public static final BitSet FOLLOW_RECIPE_in_recipe199 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_recipe201 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_PARS_in_recipe203 = new BitSet(new long[]{0x0000000080000800L});
    public static final BitSet FOLLOW_INT_LIT_in_recipe205 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_block_in_recipe208 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BLOCK_in_block234 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_block254 = new BitSet(new long[]{0xC90402001000C850L,0x0000000000000007L});
    public static final BitSet FOLLOW_RCURLY_in_block284 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SEMI_in_stat309 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat313 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_block_in_stat326 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_decl_in_stat338 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALAP_in_stat351 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat355 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WHILE_in_stat369 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat373 = new BitSet(new long[]{0xC90002001000C850L,0x0000000000000007L});
    public static final BitSet FOLLOW_stat_in_stat377 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_UNTIL_in_stat391 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat395 = new BitSet(new long[]{0xC90002001000C850L,0x0000000000000007L});
    public static final BitSet FOLLOW_stat_in_stat399 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TRY_in_stat413 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat417 = new BitSet(new long[]{0xC90002001000C858L,0x0000000000000007L});
    public static final BitSet FOLLOW_stat_in_stat422 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IF_in_stat438 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat442 = new BitSet(new long[]{0xC90002001000C850L,0x0000000000000007L});
    public static final BitSet FOLLOW_stat_in_stat446 = new BitSet(new long[]{0xC90002001000C858L,0x0000000000000007L});
    public static final BitSet FOLLOW_stat_in_stat451 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CHOICE_in_stat468 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat480 = new BitSet(new long[]{0xC90002001000C858L,0x0000000000000007L});
    public static final BitSet FOLLOW_stat_in_stat502 = new BitSet(new long[]{0xC90002001000C858L,0x0000000000000007L});
    public static final BitSet FOLLOW_STAR_in_stat537 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat541 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_rule_in_stat554 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_stat566 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_stat578 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_stat590 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CALL_in_rule610 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rule612 = new BitSet(new long[]{0x0000000000000108L});
    public static final BitSet FOLLOW_ARGS_in_rule616 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_arg_in_rule618 = new BitSet(new long[]{0x0080000000000080L});
    public static final BitSet FOLLOW_RPAR_in_rule621 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VAR_in_var_decl639 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_type_in_var_decl641 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_var_decl643 = new BitSet(new long[]{0x0000000008000008L});
    public static final BitSet FOLLOW_ARG_in_arg690 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_OUT_in_arg694 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_arg697 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DONT_CARE_in_arg701 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_literal_in_arg705 = new BitSet(new long[]{0x0000000000000008L});

}