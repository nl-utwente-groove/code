// $ANTLR 3.4 D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g 2014-01-16 09:30:01

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ALAP", "AMP", "ANY", "ARG", "ARGS", "ASTERISK", "ATOM", "BAR", "BLOCK", "BOOL", "BSLASH", "CALL", "CHOICE", "COMMA", "DO", "DONT_CARE", "DOT", "DO_UNTIL", "DO_WHILE", "ELSE", "EscapeSequence", "FALSE", "FUNCTION", "FUNCTIONS", "ID", "IF", "IMPORT", "IMPORTS", "INT", "INT_LIT", "IntegerNumber", "LCURLY", "LPAR", "MINUS", "ML_COMMENT", "NODE", "NOT", "NonIntegerNumber", "OR", "OTHER", "OUT", "PACKAGE", "PAR", "PARS", "PLUS", "PRIORITY", "PROGRAM", "QUOTE", "RCURLY", "REAL", "REAL_LIT", "RECIPE", "RECIPES", "RPAR", "SEMI", "SHARP", "SL_COMMENT", "STAR", "STRING", "STRING_LIT", "TRUE", "TRY", "UNTIL", "VAR", "WHILE", "WS"
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
    public static final int IMPORTS=31;
    public static final int INT=32;
    public static final int INT_LIT=33;
    public static final int IntegerNumber=34;
    public static final int LCURLY=35;
    public static final int LPAR=36;
    public static final int MINUS=37;
    public static final int ML_COMMENT=38;
    public static final int NODE=39;
    public static final int NOT=40;
    public static final int NonIntegerNumber=41;
    public static final int OR=42;
    public static final int OTHER=43;
    public static final int OUT=44;
    public static final int PACKAGE=45;
    public static final int PAR=46;
    public static final int PARS=47;
    public static final int PLUS=48;
    public static final int PRIORITY=49;
    public static final int PROGRAM=50;
    public static final int QUOTE=51;
    public static final int RCURLY=52;
    public static final int REAL=53;
    public static final int REAL_LIT=54;
    public static final int RECIPE=55;
    public static final int RECIPES=56;
    public static final int RPAR=57;
    public static final int SEMI=58;
    public static final int SHARP=59;
    public static final int SL_COMMENT=60;
    public static final int STAR=61;
    public static final int STRING=62;
    public static final int STRING_LIT=63;
    public static final int TRUE=64;
    public static final int TRY=65;
    public static final int UNTIL=66;
    public static final int VAR=67;
    public static final int WHILE=68;
    public static final int WS=69;

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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:36:1: program returns [ CtrlAut aut ] : ^( PROGRAM package_decl imports functions recipes block ) ;
    public final CtrlBuilder.program_return program() throws RecognitionException {
        CtrlBuilder.program_return retval = new CtrlBuilder.program_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree PROGRAM1=null;
        CtrlBuilder.package_decl_return package_decl2 =null;

        CtrlBuilder.imports_return imports3 =null;

        CtrlBuilder.functions_return functions4 =null;

        CtrlBuilder.recipes_return recipes5 =null;

        CtrlBuilder.block_return block6 =null;


        CtrlTree PROGRAM1_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:37:3: ( ^( PROGRAM package_decl imports functions recipes block ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:37:5: ^( PROGRAM package_decl imports functions recipes block )
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


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_imports_in_program63);
            imports3=imports();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = imports3.tree;


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_functions_in_program65);
            functions4=functions();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = functions4.tree;


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_recipes_in_program67);
            recipes5=recipes();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = recipes5.tree;


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_block_in_program69);
            block6=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block6.tree;


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


             if ((block6!=null?((CtrlTree)block6.tree):null).getChildCount() == 0) {
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:46:1: package_decl : ^( PACKAGE ID SEMI ) ;
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:47:3: ( ^( PACKAGE ID SEMI ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:47:5: ^( PACKAGE ID SEMI )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            PACKAGE7=(CtrlTree)match(input,PACKAGE,FOLLOW_PACKAGE_in_package_decl90); 


            if ( _first_0==null ) _first_0 = PACKAGE7;
            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            ID8=(CtrlTree)match(input,ID,FOLLOW_ID_in_package_decl92); 
             
            if ( _first_1==null ) _first_1 = ID8;


            _last = (CtrlTree)input.LT(1);
            SEMI9=(CtrlTree)match(input,SEMI,FOLLOW_SEMI_in_package_decl94); 
             
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


    public static class imports_return extends TreeRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "imports"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:50:1: imports : ^( IMPORTS ( import_decl )* ) ;
    public final CtrlBuilder.imports_return imports() throws RecognitionException {
        CtrlBuilder.imports_return retval = new CtrlBuilder.imports_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree IMPORTS10=null;
        CtrlBuilder.import_decl_return import_decl11 =null;


        CtrlTree IMPORTS10_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:51:3: ( ^( IMPORTS ( import_decl )* ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:51:5: ^( IMPORTS ( import_decl )* )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            IMPORTS10=(CtrlTree)match(input,IMPORTS,FOLLOW_IMPORTS_in_imports110); 


            if ( _first_0==null ) _first_0 = IMPORTS10;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:51:15: ( import_decl )*
                loop1:
                do {
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( (LA1_0==IMPORT) ) {
                        alt1=1;
                    }


                    switch (alt1) {
                	case 1 :
                	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:51:15: import_decl
                	    {
                	    _last = (CtrlTree)input.LT(1);
                	    pushFollow(FOLLOW_import_decl_in_imports112);
                	    import_decl11=import_decl();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = import_decl11.tree;


                	    retval.tree = (CtrlTree)_first_0;
                	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                	        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

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
    // $ANTLR end "imports"


    public static class import_decl_return extends TreeRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "import_decl"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:54:1: import_decl : ^( IMPORT ID SEMI ) ;
    public final CtrlBuilder.import_decl_return import_decl() throws RecognitionException {
        CtrlBuilder.import_decl_return retval = new CtrlBuilder.import_decl_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree IMPORT12=null;
        CtrlTree ID13=null;
        CtrlTree SEMI14=null;

        CtrlTree IMPORT12_tree=null;
        CtrlTree ID13_tree=null;
        CtrlTree SEMI14_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:55:3: ( ^( IMPORT ID SEMI ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:55:5: ^( IMPORT ID SEMI )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            IMPORT12=(CtrlTree)match(input,IMPORT,FOLLOW_IMPORT_in_import_decl130); 


            if ( _first_0==null ) _first_0 = IMPORT12;
            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            ID13=(CtrlTree)match(input,ID,FOLLOW_ID_in_import_decl132); 
             
            if ( _first_1==null ) _first_1 = ID13;


            _last = (CtrlTree)input.LT(1);
            SEMI14=(CtrlTree)match(input,SEMI,FOLLOW_SEMI_in_import_decl134); 
             
            if ( _first_1==null ) _first_1 = SEMI14;


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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:58:1: functions : ^( FUNCTIONS ( function )* ) ;
    public final CtrlBuilder.functions_return functions() throws RecognitionException {
        CtrlBuilder.functions_return retval = new CtrlBuilder.functions_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree FUNCTIONS15=null;
        CtrlBuilder.function_return function16 =null;


        CtrlTree FUNCTIONS15_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:59:3: ( ^( FUNCTIONS ( function )* ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:59:5: ^( FUNCTIONS ( function )* )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            FUNCTIONS15=(CtrlTree)match(input,FUNCTIONS,FOLLOW_FUNCTIONS_in_functions149); 


            if ( _first_0==null ) _first_0 = FUNCTIONS15;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:59:17: ( function )*
                loop2:
                do {
                    int alt2=2;
                    int LA2_0 = input.LA(1);

                    if ( (LA2_0==FUNCTION) ) {
                        alt2=1;
                    }


                    switch (alt2) {
                	case 1 :
                	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:59:17: function
                	    {
                	    _last = (CtrlTree)input.LT(1);
                	    pushFollow(FOLLOW_function_in_functions151);
                	    function16=function();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = function16.tree;


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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:62:1: function : ^( FUNCTION ID par_list priority block ) ;
    public final CtrlBuilder.function_return function() throws RecognitionException {
        CtrlBuilder.function_return retval = new CtrlBuilder.function_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree FUNCTION17=null;
        CtrlTree ID18=null;
        CtrlBuilder.par_list_return par_list19 =null;

        CtrlBuilder.priority_return priority20 =null;

        CtrlBuilder.block_return block21 =null;


        CtrlTree FUNCTION17_tree=null;
        CtrlTree ID18_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:63:3: ( ^( FUNCTION ID par_list priority block ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:63:5: ^( FUNCTION ID par_list priority block )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            FUNCTION17=(CtrlTree)match(input,FUNCTION,FOLLOW_FUNCTION_in_function167); 


            if ( _first_0==null ) _first_0 = FUNCTION17;
            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            ID18=(CtrlTree)match(input,ID,FOLLOW_ID_in_function169); 
             
            if ( _first_1==null ) _first_1 = ID18;


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_par_list_in_function171);
            par_list19=par_list();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = par_list19.tree;


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_priority_in_function173);
            priority20=priority();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = priority20.tree;


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_block_in_function175);
            block21=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block21.tree;


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


             
                  namespace.addBody(helper.qualify((ID18!=null?ID18.getText():null)), (block21!=null?block21.aut:null));
                

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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:69:1: recipes : ^( RECIPES ( recipe )* ) ;
    public final CtrlBuilder.recipes_return recipes() throws RecognitionException {
        CtrlBuilder.recipes_return retval = new CtrlBuilder.recipes_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree RECIPES22=null;
        CtrlBuilder.recipe_return recipe23 =null;


        CtrlTree RECIPES22_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:70:3: ( ^( RECIPES ( recipe )* ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:70:5: ^( RECIPES ( recipe )* )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            RECIPES22=(CtrlTree)match(input,RECIPES,FOLLOW_RECIPES_in_recipes198); 


            if ( _first_0==null ) _first_0 = RECIPES22;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:70:15: ( recipe )*
                loop3:
                do {
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0==RECIPE) ) {
                        alt3=1;
                    }


                    switch (alt3) {
                	case 1 :
                	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:70:15: recipe
                	    {
                	    _last = (CtrlTree)input.LT(1);
                	    pushFollow(FOLLOW_recipe_in_recipes200);
                	    recipe23=recipe();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = recipe23.tree;


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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:73:1: recipe : ^( RECIPE ID par_list priority block ) ;
    public final CtrlBuilder.recipe_return recipe() throws RecognitionException {
        CtrlBuilder.recipe_return retval = new CtrlBuilder.recipe_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree RECIPE24=null;
        CtrlTree ID25=null;
        CtrlBuilder.par_list_return par_list26 =null;

        CtrlBuilder.priority_return priority27 =null;

        CtrlBuilder.block_return block28 =null;


        CtrlTree RECIPE24_tree=null;
        CtrlTree ID25_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:74:3: ( ^( RECIPE ID par_list priority block ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:74:5: ^( RECIPE ID par_list priority block )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            RECIPE24=(CtrlTree)match(input,RECIPE,FOLLOW_RECIPE_in_recipe216); 


            if ( _first_0==null ) _first_0 = RECIPE24;
            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            ID25=(CtrlTree)match(input,ID,FOLLOW_ID_in_recipe218); 
             
            if ( _first_1==null ) _first_1 = ID25;


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_par_list_in_recipe220);
            par_list26=par_list();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = par_list26.tree;


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_priority_in_recipe222);
            priority27=priority();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = priority27.tree;


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_block_in_recipe224);
            block28=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block28.tree;


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


             
                  String recipeName = helper.qualify((ID25!=null?ID25.getText():null));
                  helper.checkRecipeBody(RECIPE24, recipeName, (block28!=null?block28.aut:null));
                  namespace.addBody(recipeName, (block28!=null?block28.aut:null));
                

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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:82:1: priority : ( INT_LIT )? ;
    public final CtrlBuilder.priority_return priority() throws RecognitionException {
        CtrlBuilder.priority_return retval = new CtrlBuilder.priority_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree INT_LIT29=null;

        CtrlTree INT_LIT29_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:83:3: ( ( INT_LIT )? )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:83:5: ( INT_LIT )?
            {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:83:5: ( INT_LIT )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==INT_LIT) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:83:7: INT_LIT
                    {
                    _last = (CtrlTree)input.LT(1);
                    INT_LIT29=(CtrlTree)match(input,INT_LIT,FOLLOW_INT_LIT_in_priority246); 
                     
                    if ( _first_0==null ) _first_0 = INT_LIT29;


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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:86:1: par_list : ^( PARS ( par_decl )* ) ;
    public final CtrlBuilder.par_list_return par_list() throws RecognitionException {
        CtrlBuilder.par_list_return retval = new CtrlBuilder.par_list_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree PARS30=null;
        CtrlBuilder.par_decl_return par_decl31 =null;


        CtrlTree PARS30_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:87:3: ( ^( PARS ( par_decl )* ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:87:5: ^( PARS ( par_decl )* )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            PARS30=(CtrlTree)match(input,PARS,FOLLOW_PARS_in_par_list263); 


            if ( _first_0==null ) _first_0 = PARS30;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:87:12: ( par_decl )*
                loop5:
                do {
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0==PAR) ) {
                        alt5=1;
                    }


                    switch (alt5) {
                	case 1 :
                	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:87:12: par_decl
                	    {
                	    _last = (CtrlTree)input.LT(1);
                	    pushFollow(FOLLOW_par_decl_in_par_list265);
                	    par_decl31=par_decl();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = par_decl31.tree;


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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:90:1: par_decl : ^( PAR ( OUT )? type ID ) ;
    public final CtrlBuilder.par_decl_return par_decl() throws RecognitionException {
        CtrlBuilder.par_decl_return retval = new CtrlBuilder.par_decl_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree PAR32=null;
        CtrlTree OUT33=null;
        CtrlTree ID35=null;
        CtrlBuilder.type_return type34 =null;


        CtrlTree PAR32_tree=null;
        CtrlTree OUT33_tree=null;
        CtrlTree ID35_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:91:3: ( ^( PAR ( OUT )? type ID ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:91:5: ^( PAR ( OUT )? type ID )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            PAR32=(CtrlTree)match(input,PAR,FOLLOW_PAR_in_par_decl282); 


            if ( _first_0==null ) _first_0 = PAR32;
            match(input, Token.DOWN, null); 
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:91:11: ( OUT )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==OUT) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:91:11: OUT
                    {
                    _last = (CtrlTree)input.LT(1);
                    OUT33=(CtrlTree)match(input,OUT,FOLLOW_OUT_in_par_decl284); 
                     
                    if ( _first_1==null ) _first_1 = OUT33;


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;

            }


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_type_in_par_decl287);
            type34=type();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = type34.tree;


            _last = (CtrlTree)input.LT(1);
            ID35=(CtrlTree)match(input,ID,FOLLOW_ID_in_par_decl289); 
             
            if ( _first_1==null ) _first_1 = ID35;


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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:94:1: block returns [ CtrlAut aut ] : ^( BLOCK ( stat )* ) ;
    public final CtrlBuilder.block_return block() throws RecognitionException {
        CtrlBuilder.block_return retval = new CtrlBuilder.block_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree BLOCK36=null;
        CtrlBuilder.stat_return stat37 =null;


        CtrlTree BLOCK36_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:95:3: ( ^( BLOCK ( stat )* ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:95:5: ^( BLOCK ( stat )* )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            BLOCK36=(CtrlTree)match(input,BLOCK,FOLLOW_BLOCK_in_block309); 


            if ( _first_0==null ) _first_0 = BLOCK36;
             retval.aut = builder.buildTrue(); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:97:8: ( stat )*
                loop7:
                do {
                    int alt7=2;
                    int LA7_0 = input.LA(1);

                    if ( (LA7_0==ALAP||LA7_0==ANY||LA7_0==ATOM||LA7_0==BLOCK||(LA7_0 >= CALL && LA7_0 <= CHOICE)||LA7_0==IF||LA7_0==OTHER||LA7_0==SEMI||LA7_0==STAR||(LA7_0 >= TRUE && LA7_0 <= WHILE)) ) {
                        alt7=1;
                    }


                    switch (alt7) {
                	case 1 :
                	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:97:10: stat
                	    {
                	    _last = (CtrlTree)input.LT(1);
                	    pushFollow(FOLLOW_stat_in_block329);
                	    stat37=stat();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = stat37.tree;


                	     retval.aut = builder.buildSeq(retval.aut, (stat37!=null?stat37.aut:null)); 

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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:103:1: stat returns [ CtrlAut aut ] : ( ^( SEMI s= stat ) | block | var_decl | ^( ALAP s= stat ) | ^( ATOM s= stat ) | ^( WHILE c= stat s= stat ) | ^( UNTIL c= stat s= stat ) | ^( TRY s1= stat (s2= stat )? ) | ^( IF c= stat s1= stat (s2= stat )? ) | ^( CHOICE s1= stat (s2= stat )* ) | ^( STAR s= stat ) | rule | ANY | OTHER | TRUE );
    public final CtrlBuilder.stat_return stat() throws RecognitionException {
        CtrlBuilder.stat_return retval = new CtrlBuilder.stat_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree SEMI38=null;
        CtrlTree ALAP41=null;
        CtrlTree ATOM42=null;
        CtrlTree WHILE43=null;
        CtrlTree UNTIL44=null;
        CtrlTree TRY45=null;
        CtrlTree IF46=null;
        CtrlTree CHOICE47=null;
        CtrlTree STAR48=null;
        CtrlTree ANY50=null;
        CtrlTree OTHER51=null;
        CtrlTree TRUE52=null;
        CtrlBuilder.stat_return s =null;

        CtrlBuilder.stat_return c =null;

        CtrlBuilder.stat_return s1 =null;

        CtrlBuilder.stat_return s2 =null;

        CtrlBuilder.block_return block39 =null;

        CtrlBuilder.var_decl_return var_decl40 =null;

        CtrlBuilder.rule_return rule49 =null;


        CtrlTree SEMI38_tree=null;
        CtrlTree ALAP41_tree=null;
        CtrlTree ATOM42_tree=null;
        CtrlTree WHILE43_tree=null;
        CtrlTree UNTIL44_tree=null;
        CtrlTree TRY45_tree=null;
        CtrlTree IF46_tree=null;
        CtrlTree CHOICE47_tree=null;
        CtrlTree STAR48_tree=null;
        CtrlTree ANY50_tree=null;
        CtrlTree OTHER51_tree=null;
        CtrlTree TRUE52_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:104:3: ( ^( SEMI s= stat ) | block | var_decl | ^( ALAP s= stat ) | ^( ATOM s= stat ) | ^( WHILE c= stat s= stat ) | ^( UNTIL c= stat s= stat ) | ^( TRY s1= stat (s2= stat )? ) | ^( IF c= stat s1= stat (s2= stat )? ) | ^( CHOICE s1= stat (s2= stat )* ) | ^( STAR s= stat ) | rule | ANY | OTHER | TRUE )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:104:5: ^( SEMI s= stat )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    SEMI38=(CtrlTree)match(input,SEMI,FOLLOW_SEMI_in_stat375); 


                    if ( _first_0==null ) _first_0 = SEMI38;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat379);
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:106:5: block
                    {
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_block_in_stat392);
                    block39=block();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = block39.tree;


                     retval.aut = (block39!=null?block39.aut:null); 

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 3 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:108:5: var_decl
                    {
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_var_decl_in_stat404);
                    var_decl40=var_decl();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = var_decl40.tree;


                     retval.aut = builder.buildTrue(); 

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 4 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:110:5: ^( ALAP s= stat )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    ALAP41=(CtrlTree)match(input,ALAP,FOLLOW_ALAP_in_stat417); 


                    if ( _first_0==null ) _first_0 = ALAP41;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat421);
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:112:5: ^( ATOM s= stat )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    ATOM42=(CtrlTree)match(input,ATOM,FOLLOW_ATOM_in_stat435); 


                    if ( _first_0==null ) _first_0 = ATOM42;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat439);
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:116:5: ^( WHILE c= stat s= stat )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    WHILE43=(CtrlTree)match(input,WHILE,FOLLOW_WHILE_in_stat453); 


                    if ( _first_0==null ) _first_0 = WHILE43;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat457);
                    c=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = c.tree;


                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat461);
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:118:5: ^( UNTIL c= stat s= stat )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    UNTIL44=(CtrlTree)match(input,UNTIL,FOLLOW_UNTIL_in_stat475); 


                    if ( _first_0==null ) _first_0 = UNTIL44;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat479);
                    c=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = c.tree;


                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat483);
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:120:5: ^( TRY s1= stat (s2= stat )? )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    TRY45=(CtrlTree)match(input,TRY,FOLLOW_TRY_in_stat497); 


                    if ( _first_0==null ) _first_0 = TRY45;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat501);
                    s1=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s1.tree;


                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:120:19: (s2= stat )?
                    int alt8=2;
                    int LA8_0 = input.LA(1);

                    if ( (LA8_0==ALAP||LA8_0==ANY||LA8_0==ATOM||LA8_0==BLOCK||(LA8_0 >= CALL && LA8_0 <= CHOICE)||LA8_0==IF||LA8_0==OTHER||LA8_0==SEMI||LA8_0==STAR||(LA8_0 >= TRUE && LA8_0 <= WHILE)) ) {
                        alt8=1;
                    }
                    switch (alt8) {
                        case 1 :
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:120:20: s2= stat
                            {
                            _last = (CtrlTree)input.LT(1);
                            pushFollow(FOLLOW_stat_in_stat506);
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:122:5: ^( IF c= stat s1= stat (s2= stat )? )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    IF46=(CtrlTree)match(input,IF,FOLLOW_IF_in_stat522); 


                    if ( _first_0==null ) _first_0 = IF46;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat526);
                    c=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = c.tree;


                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat530);
                    s1=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s1.tree;


                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:122:25: (s2= stat )?
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0==ALAP||LA9_0==ANY||LA9_0==ATOM||LA9_0==BLOCK||(LA9_0 >= CALL && LA9_0 <= CHOICE)||LA9_0==IF||LA9_0==OTHER||LA9_0==SEMI||LA9_0==STAR||(LA9_0 >= TRUE && LA9_0 <= WHILE)) ) {
                        alt9=1;
                    }
                    switch (alt9) {
                        case 1 :
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:122:26: s2= stat
                            {
                            _last = (CtrlTree)input.LT(1);
                            pushFollow(FOLLOW_stat_in_stat535);
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:124:5: ^( CHOICE s1= stat (s2= stat )* )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    CHOICE47=(CtrlTree)match(input,CHOICE,FOLLOW_CHOICE_in_stat552); 


                    if ( _first_0==null ) _first_0 = CHOICE47;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat564);
                    s1=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s1.tree;


                     retval.aut = (s1!=null?s1.aut:null); 

                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:127:8: (s2= stat )*
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);

                        if ( (LA10_0==ALAP||LA10_0==ANY||LA10_0==ATOM||LA10_0==BLOCK||(LA10_0 >= CALL && LA10_0 <= CHOICE)||LA10_0==IF||LA10_0==OTHER||LA10_0==SEMI||LA10_0==STAR||(LA10_0 >= TRUE && LA10_0 <= WHILE)) ) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:127:10: s2= stat
                    	    {
                    	    _last = (CtrlTree)input.LT(1);
                    	    pushFollow(FOLLOW_stat_in_stat586);
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:131:5: ^( STAR s= stat )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    STAR48=(CtrlTree)match(input,STAR,FOLLOW_STAR_in_stat621); 


                    if ( _first_0==null ) _first_0 = STAR48;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat625);
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:133:5: rule
                    {
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_rule_in_stat638);
                    rule49=rule();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = rule49.tree;


                     retval.aut = builder.buildCall((rule49!=null?((CtrlTree)rule49.tree):null).getCtrlCall(), namespace); 

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 13 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:135:5: ANY
                    {
                    _last = (CtrlTree)input.LT(1);
                    ANY50=(CtrlTree)match(input,ANY,FOLLOW_ANY_in_stat650); 
                     
                    if ( _first_0==null ) _first_0 = ANY50;


                     retval.aut = builder.buildAny(namespace); 

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 14 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:137:5: OTHER
                    {
                    _last = (CtrlTree)input.LT(1);
                    OTHER51=(CtrlTree)match(input,OTHER,FOLLOW_OTHER_in_stat662); 
                     
                    if ( _first_0==null ) _first_0 = OTHER51;


                     retval.aut = builder.buildOther(namespace); 
                        

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 15 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:140:5: TRUE
                    {
                    _last = (CtrlTree)input.LT(1);
                    TRUE52=(CtrlTree)match(input,TRUE,FOLLOW_TRUE_in_stat674); 
                     
                    if ( _first_0==null ) _first_0 = TRUE52;


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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:144:1: rule : ^( CALL ID ( ^( ARGS ( arg )* RPAR ) )? ) ;
    public final CtrlBuilder.rule_return rule() throws RecognitionException {
        CtrlBuilder.rule_return retval = new CtrlBuilder.rule_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree CALL53=null;
        CtrlTree ID54=null;
        CtrlTree ARGS55=null;
        CtrlTree RPAR57=null;
        CtrlBuilder.arg_return arg56 =null;


        CtrlTree CALL53_tree=null;
        CtrlTree ID54_tree=null;
        CtrlTree ARGS55_tree=null;
        CtrlTree RPAR57_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:145:3: ( ^( CALL ID ( ^( ARGS ( arg )* RPAR ) )? ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:145:5: ^( CALL ID ( ^( ARGS ( arg )* RPAR ) )? )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            CALL53=(CtrlTree)match(input,CALL,FOLLOW_CALL_in_rule694); 


            if ( _first_0==null ) _first_0 = CALL53;
            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            ID54=(CtrlTree)match(input,ID,FOLLOW_ID_in_rule696); 
             
            if ( _first_1==null ) _first_1 = ID54;


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:145:15: ( ^( ARGS ( arg )* RPAR ) )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==ARGS) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:145:16: ^( ARGS ( arg )* RPAR )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_2 = _last;
                    CtrlTree _first_2 = null;
                    _last = (CtrlTree)input.LT(1);
                    ARGS55=(CtrlTree)match(input,ARGS,FOLLOW_ARGS_in_rule700); 


                    if ( _first_1==null ) _first_1 = ARGS55;
                    match(input, Token.DOWN, null); 
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:145:23: ( arg )*
                    loop12:
                    do {
                        int alt12=2;
                        int LA12_0 = input.LA(1);

                        if ( (LA12_0==ARG) ) {
                            alt12=1;
                        }


                        switch (alt12) {
                    	case 1 :
                    	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:145:23: arg
                    	    {
                    	    _last = (CtrlTree)input.LT(1);
                    	    pushFollow(FOLLOW_arg_in_rule702);
                    	    arg56=arg();

                    	    state._fsp--;

                    	     
                    	    if ( _first_2==null ) _first_2 = arg56.tree;


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
                    RPAR57=(CtrlTree)match(input,RPAR,FOLLOW_RPAR_in_rule705); 
                     
                    if ( _first_2==null ) _first_2 = RPAR57;


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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:148:1: var_decl : ^( VAR type ( ID )+ ) ;
    public final CtrlBuilder.var_decl_return var_decl() throws RecognitionException {
        CtrlBuilder.var_decl_return retval = new CtrlBuilder.var_decl_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree VAR58=null;
        CtrlTree ID60=null;
        CtrlBuilder.type_return type59 =null;


        CtrlTree VAR58_tree=null;
        CtrlTree ID60_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:149:2: ( ^( VAR type ( ID )+ ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:149:4: ^( VAR type ( ID )+ )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            VAR58=(CtrlTree)match(input,VAR,FOLLOW_VAR_in_var_decl723); 


            if ( _first_0==null ) _first_0 = VAR58;
            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_type_in_var_decl725);
            type59=type();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = type59.tree;


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:149:16: ( ID )+
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
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:149:16: ID
            	    {
            	    _last = (CtrlTree)input.LT(1);
            	    ID60=(CtrlTree)match(input,ID,FOLLOW_ID_in_var_decl727); 
            	     
            	    if ( _first_1==null ) _first_1 = ID60;


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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:152:1: type : ( NODE | BOOL | STRING | INT | REAL );
    public final CtrlBuilder.type_return type() throws RecognitionException {
        CtrlBuilder.type_return retval = new CtrlBuilder.type_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree set61=null;

        CtrlTree set61_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:153:3: ( NODE | BOOL | STRING | INT | REAL )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:
            {
            _last = (CtrlTree)input.LT(1);
            set61=(CtrlTree)input.LT(1);

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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:156:1: arg : ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) ) ;
    public final CtrlBuilder.arg_return arg() throws RecognitionException {
        CtrlBuilder.arg_return retval = new CtrlBuilder.arg_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree ARG62=null;
        CtrlTree OUT63=null;
        CtrlTree ID64=null;
        CtrlTree DONT_CARE65=null;
        CtrlBuilder.literal_return literal66 =null;


        CtrlTree ARG62_tree=null;
        CtrlTree OUT63_tree=null;
        CtrlTree ID64_tree=null;
        CtrlTree DONT_CARE65_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:157:2: ( ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:157:4: ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            ARG62=(CtrlTree)match(input,ARG,FOLLOW_ARG_in_arg774); 


            if ( _first_0==null ) _first_0 = ARG62;
            match(input, Token.DOWN, null); 
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:157:11: ( ( OUT )? ID | DONT_CARE | literal )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:157:13: ( OUT )? ID
                    {
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:157:13: ( OUT )?
                    int alt15=2;
                    int LA15_0 = input.LA(1);

                    if ( (LA15_0==OUT) ) {
                        alt15=1;
                    }
                    switch (alt15) {
                        case 1 :
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:157:13: OUT
                            {
                            _last = (CtrlTree)input.LT(1);
                            OUT63=(CtrlTree)match(input,OUT,FOLLOW_OUT_in_arg778); 
                             
                            if ( _first_1==null ) _first_1 = OUT63;


                            retval.tree = (CtrlTree)_first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                            }
                            break;

                    }


                    _last = (CtrlTree)input.LT(1);
                    ID64=(CtrlTree)match(input,ID,FOLLOW_ID_in_arg781); 
                     
                    if ( _first_1==null ) _first_1 = ID64;


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 2 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:157:23: DONT_CARE
                    {
                    _last = (CtrlTree)input.LT(1);
                    DONT_CARE65=(CtrlTree)match(input,DONT_CARE,FOLLOW_DONT_CARE_in_arg785); 
                     
                    if ( _first_1==null ) _first_1 = DONT_CARE65;


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 3 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:157:35: literal
                    {
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_literal_in_arg789);
                    literal66=literal();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = literal66.tree;


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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:160:1: literal : ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT );
    public final CtrlBuilder.literal_return literal() throws RecognitionException {
        CtrlBuilder.literal_return retval = new CtrlBuilder.literal_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree set67=null;

        CtrlTree set67_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:161:3: ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:
            {
            _last = (CtrlTree)input.LT(1);
            set67=(CtrlTree)input.LT(1);

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
    public static final BitSet FOLLOW_package_decl_in_program61 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_imports_in_program63 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_functions_in_program65 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_recipes_in_program67 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_block_in_program69 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PACKAGE_in_package_decl90 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_package_decl92 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_SEMI_in_package_decl94 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IMPORTS_in_imports110 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_import_decl_in_imports112 = new BitSet(new long[]{0x0000000040000008L});
    public static final BitSet FOLLOW_IMPORT_in_import_decl130 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_import_decl132 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_SEMI_in_import_decl134 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FUNCTIONS_in_functions149 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_function_in_functions151 = new BitSet(new long[]{0x0000000004000008L});
    public static final BitSet FOLLOW_FUNCTION_in_function167 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_function169 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_par_list_in_function171 = new BitSet(new long[]{0x0000000200001000L});
    public static final BitSet FOLLOW_priority_in_function173 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_block_in_function175 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RECIPES_in_recipes198 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_recipe_in_recipes200 = new BitSet(new long[]{0x0080000000000008L});
    public static final BitSet FOLLOW_RECIPE_in_recipe216 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_recipe218 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_par_list_in_recipe220 = new BitSet(new long[]{0x0000000200001000L});
    public static final BitSet FOLLOW_priority_in_recipe222 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_block_in_recipe224 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_INT_LIT_in_priority246 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PARS_in_par_list263 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_par_decl_in_par_list265 = new BitSet(new long[]{0x0000400000000008L});
    public static final BitSet FOLLOW_PAR_in_par_decl282 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_OUT_in_par_decl284 = new BitSet(new long[]{0x4020008100002000L});
    public static final BitSet FOLLOW_type_in_par_decl287 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_par_decl289 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BLOCK_in_block309 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_block329 = new BitSet(new long[]{0x2400080020019458L,0x000000000000001FL});
    public static final BitSet FOLLOW_SEMI_in_stat375 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat379 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_block_in_stat392 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_decl_in_stat404 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALAP_in_stat417 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat421 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ATOM_in_stat435 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat439 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WHILE_in_stat453 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat457 = new BitSet(new long[]{0x2400080020019450L,0x000000000000001FL});
    public static final BitSet FOLLOW_stat_in_stat461 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_UNTIL_in_stat475 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat479 = new BitSet(new long[]{0x2400080020019450L,0x000000000000001FL});
    public static final BitSet FOLLOW_stat_in_stat483 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TRY_in_stat497 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat501 = new BitSet(new long[]{0x2400080020019458L,0x000000000000001FL});
    public static final BitSet FOLLOW_stat_in_stat506 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IF_in_stat522 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat526 = new BitSet(new long[]{0x2400080020019450L,0x000000000000001FL});
    public static final BitSet FOLLOW_stat_in_stat530 = new BitSet(new long[]{0x2400080020019458L,0x000000000000001FL});
    public static final BitSet FOLLOW_stat_in_stat535 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CHOICE_in_stat552 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat564 = new BitSet(new long[]{0x2400080020019458L,0x000000000000001FL});
    public static final BitSet FOLLOW_stat_in_stat586 = new BitSet(new long[]{0x2400080020019458L,0x000000000000001FL});
    public static final BitSet FOLLOW_STAR_in_stat621 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat625 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_rule_in_stat638 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_stat650 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_stat662 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_stat674 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CALL_in_rule694 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rule696 = new BitSet(new long[]{0x0000000000000108L});
    public static final BitSet FOLLOW_ARGS_in_rule700 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_arg_in_rule702 = new BitSet(new long[]{0x0200000000000080L});
    public static final BitSet FOLLOW_RPAR_in_rule705 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VAR_in_var_decl723 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_type_in_var_decl725 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_var_decl727 = new BitSet(new long[]{0x0000000010000008L});
    public static final BitSet FOLLOW_ARG_in_arg774 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_OUT_in_arg778 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_arg781 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DONT_CARE_in_arg785 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_literal_in_arg789 = new BitSet(new long[]{0x0000000000000008L});

}