// $ANTLR 3.4 E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g 2014-06-03 07:26:31

package groove.control.parse;
import groove.control.*;
import groove.algebra.AlgebraFamily;
import groove.grammar.model.FormatErrorSet;
import groove.util.antlr.ParseTreeAdaptor;
import groove.util.antlr.ParseInfo;
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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ALAP", "AMP", "ANY", "ARG", "ARGS", "ASTERISK", "ATOM", "BAR", "BLOCK", "BOOL", "BSLASH", "CALL", "CHOICE", "COMMA", "DO", "DONT_CARE", "DOT", "DO_UNTIL", "DO_WHILE", "ELSE", "EscapeSequence", "FALSE", "FUNCTION", "FUNCTIONS", "ID", "IF", "IMPORT", "IMPORTS", "INT", "INT_LIT", "IntegerNumber", "LANGLE", "LCURLY", "LPAR", "MINUS", "ML_COMMENT", "NODE", "NOT", "NonIntegerNumber", "OR", "OTHER", "OUT", "PACKAGE", "PAR", "PARS", "PLUS", "PRIORITY", "PROGRAM", "QUOTE", "RANGLE", "RCURLY", "REAL", "REAL_LIT", "RECIPE", "RECIPES", "RPAR", "SEMI", "SHARP", "SL_COMMENT", "STAR", "STRING", "STRING_LIT", "TRUE", "TRY", "UNTIL", "VAR", "WHILE", "WS"
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
    public static final int LANGLE=35;
    public static final int LCURLY=36;
    public static final int LPAR=37;
    public static final int MINUS=38;
    public static final int ML_COMMENT=39;
    public static final int NODE=40;
    public static final int NOT=41;
    public static final int NonIntegerNumber=42;
    public static final int OR=43;
    public static final int OTHER=44;
    public static final int OUT=45;
    public static final int PACKAGE=46;
    public static final int PAR=47;
    public static final int PARS=48;
    public static final int PLUS=49;
    public static final int PRIORITY=50;
    public static final int PROGRAM=51;
    public static final int QUOTE=52;
    public static final int RANGLE=53;
    public static final int RCURLY=54;
    public static final int REAL=55;
    public static final int REAL_LIT=56;
    public static final int RECIPE=57;
    public static final int RECIPES=58;
    public static final int RPAR=59;
    public static final int SEMI=60;
    public static final int SHARP=61;
    public static final int SL_COMMENT=62;
    public static final int STAR=63;
    public static final int STRING=64;
    public static final int STRING_LIT=65;
    public static final int TRUE=66;
    public static final int TRY=67;
    public static final int UNTIL=68;
    public static final int VAR=69;
    public static final int WHILE=70;
    public static final int WS=71;

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

        /** Constructs a helper class, based on the given name space and algebra. */
        public void initialise(ParseInfo namespace) {
            this.helper = new CtrlHelper((Namespace) namespace);
        }


    public static class program_return extends TreeRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "program"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:40:1: program : ^( PROGRAM package_decl imports functions recipes block ) ;
    public final CtrlChecker.program_return program() throws RecognitionException {
        CtrlChecker.program_return retval = new CtrlChecker.program_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree PROGRAM1=null;
        CtrlChecker.package_decl_return package_decl2 =null;

        CtrlChecker.imports_return imports3 =null;

        CtrlChecker.functions_return functions4 =null;

        CtrlChecker.recipes_return recipes5 =null;

        CtrlChecker.block_return block6 =null;


        CtrlTree PROGRAM1_tree=null;

         helper.clearErrors(); 
        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:42:3: ( ^( PROGRAM package_decl imports functions recipes block ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:42:5: ^( PROGRAM package_decl imports functions recipes block )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            PROGRAM1=(CtrlTree)match(input,PROGRAM,FOLLOW_PROGRAM_in_program61); 


            if ( _first_0==null ) _first_0 = PROGRAM1;
            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_package_decl_in_program63);
            package_decl2=package_decl();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = package_decl2.tree;


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_imports_in_program65);
            imports3=imports();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = imports3.tree;


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_functions_in_program67);
            functions4=functions();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = functions4.tree;


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_recipes_in_program69);
            recipes5=recipes();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = recipes5.tree;


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_block_in_program71);
            block6=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block6.tree;


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
    // $ANTLR end "program"


    public static class package_decl_return extends TreeRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "package_decl"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:45:1: package_decl : ^( PACKAGE ID SEMI ) ;
    public final CtrlChecker.package_decl_return package_decl() throws RecognitionException {
        CtrlChecker.package_decl_return retval = new CtrlChecker.package_decl_return();
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:46:3: ( ^( PACKAGE ID SEMI ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:46:5: ^( PACKAGE ID SEMI )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            PACKAGE7=(CtrlTree)match(input,PACKAGE,FOLLOW_PACKAGE_in_package_decl88); 


            if ( _first_0==null ) _first_0 = PACKAGE7;
            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            ID8=(CtrlTree)match(input,ID,FOLLOW_ID_in_package_decl90); 
             
            if ( _first_1==null ) _first_1 = ID8;


            _last = (CtrlTree)input.LT(1);
            SEMI9=(CtrlTree)match(input,SEMI,FOLLOW_SEMI_in_package_decl92); 
             
            if ( _first_1==null ) _first_1 = SEMI9;


             helper.checkPackage(ID8); 

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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:51:1: imports : ^( IMPORTS ( import_decl )* ) ;
    public final CtrlChecker.imports_return imports() throws RecognitionException {
        CtrlChecker.imports_return retval = new CtrlChecker.imports_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree IMPORTS10=null;
        CtrlChecker.import_decl_return import_decl11 =null;


        CtrlTree IMPORTS10_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:52:3: ( ^( IMPORTS ( import_decl )* ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:52:5: ^( IMPORTS ( import_decl )* )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            IMPORTS10=(CtrlTree)match(input,IMPORTS,FOLLOW_IMPORTS_in_imports122); 


            if ( _first_0==null ) _first_0 = IMPORTS10;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:52:15: ( import_decl )*
                loop1:
                do {
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( (LA1_0==IMPORT) ) {
                        alt1=1;
                    }


                    switch (alt1) {
                	case 1 :
                	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:52:15: import_decl
                	    {
                	    _last = (CtrlTree)input.LT(1);
                	    pushFollow(FOLLOW_import_decl_in_imports124);
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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:55:1: import_decl : ^( IMPORT ID SEMI ) ;
    public final CtrlChecker.import_decl_return import_decl() throws RecognitionException {
        CtrlChecker.import_decl_return retval = new CtrlChecker.import_decl_return();
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:56:3: ( ^( IMPORT ID SEMI ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:56:5: ^( IMPORT ID SEMI )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            IMPORT12=(CtrlTree)match(input,IMPORT,FOLLOW_IMPORT_in_import_decl141); 


            if ( _first_0==null ) _first_0 = IMPORT12;
            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            ID13=(CtrlTree)match(input,ID,FOLLOW_ID_in_import_decl143); 
             
            if ( _first_1==null ) _first_1 = ID13;


            _last = (CtrlTree)input.LT(1);
            SEMI14=(CtrlTree)match(input,SEMI,FOLLOW_SEMI_in_import_decl145); 
             
            if ( _first_1==null ) _first_1 = SEMI14;


             helper.checkImport(ID13); 

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


    public static class recipes_return extends TreeRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "recipes"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:61:1: recipes : ^( RECIPES ( recipe )* ) ;
    public final CtrlChecker.recipes_return recipes() throws RecognitionException {
        CtrlChecker.recipes_return retval = new CtrlChecker.recipes_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree RECIPES15=null;
        CtrlChecker.recipe_return recipe16 =null;


        CtrlTree RECIPES15_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:62:3: ( ^( RECIPES ( recipe )* ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:62:5: ^( RECIPES ( recipe )* )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            RECIPES15=(CtrlTree)match(input,RECIPES,FOLLOW_RECIPES_in_recipes175); 


            if ( _first_0==null ) _first_0 = RECIPES15;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:62:15: ( recipe )*
                loop2:
                do {
                    int alt2=2;
                    int LA2_0 = input.LA(1);

                    if ( (LA2_0==RECIPE) ) {
                        alt2=1;
                    }


                    switch (alt2) {
                	case 1 :
                	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:62:15: recipe
                	    {
                	    _last = (CtrlTree)input.LT(1);
                	    pushFollow(FOLLOW_recipe_in_recipes177);
                	    recipe16=recipe();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = recipe16.tree;


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


             helper.reorderFunctions(RECIPES15); 

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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:66:1: recipe : ^( RECIPE ID ^( PARS ( par_decl )* ) ( INT_LIT )? block ) ;
    public final CtrlChecker.recipe_return recipe() throws RecognitionException {
        CtrlChecker.recipe_return retval = new CtrlChecker.recipe_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree RECIPE17=null;
        CtrlTree ID18=null;
        CtrlTree PARS19=null;
        CtrlTree INT_LIT21=null;
        CtrlChecker.par_decl_return par_decl20 =null;

        CtrlChecker.block_return block22 =null;


        CtrlTree RECIPE17_tree=null;
        CtrlTree ID18_tree=null;
        CtrlTree PARS19_tree=null;
        CtrlTree INT_LIT21_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:67:3: ( ^( RECIPE ID ^( PARS ( par_decl )* ) ( INT_LIT )? block ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:67:5: ^( RECIPE ID ^( PARS ( par_decl )* ) ( INT_LIT )? block )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            RECIPE17=(CtrlTree)match(input,RECIPE,FOLLOW_RECIPE_in_recipe203); 


            if ( _first_0==null ) _first_0 = RECIPE17;
             helper.startBody(RECIPE17); 

            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            ID18=(CtrlTree)match(input,ID,FOLLOW_ID_in_recipe222); 
             
            if ( _first_1==null ) _first_1 = ID18;


            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_2 = _last;
            CtrlTree _first_2 = null;
            _last = (CtrlTree)input.LT(1);
            PARS19=(CtrlTree)match(input,PARS,FOLLOW_PARS_in_recipe225); 


            if ( _first_1==null ) _first_1 = PARS19;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:69:18: ( par_decl )*
                loop3:
                do {
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0==PAR) ) {
                        alt3=1;
                    }


                    switch (alt3) {
                	case 1 :
                	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:69:18: par_decl
                	    {
                	    _last = (CtrlTree)input.LT(1);
                	    pushFollow(FOLLOW_par_decl_in_recipe227);
                	    par_decl20=par_decl();

                	    state._fsp--;

                	     
                	    if ( _first_2==null ) _first_2 = par_decl20.tree;


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
            _last = _save_last_2;
            }


            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:69:29: ( INT_LIT )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==INT_LIT) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:69:29: INT_LIT
                    {
                    _last = (CtrlTree)input.LT(1);
                    INT_LIT21=(CtrlTree)match(input,INT_LIT,FOLLOW_INT_LIT_in_recipe231); 
                     
                    if ( _first_1==null ) _first_1 = INT_LIT21;


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;

            }


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_block_in_recipe241);
            block22=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block22.tree;


             helper.endBody((block22!=null?((CtrlTree)block22.tree):null)); 

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
    // $ANTLR end "recipe"


    public static class functions_return extends TreeRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "functions"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:75:1: functions : ^( FUNCTIONS ( function )* ) ;
    public final CtrlChecker.functions_return functions() throws RecognitionException {
        CtrlChecker.functions_return retval = new CtrlChecker.functions_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree FUNCTIONS23=null;
        CtrlChecker.function_return function24 =null;


        CtrlTree FUNCTIONS23_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:76:3: ( ^( FUNCTIONS ( function )* ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:76:5: ^( FUNCTIONS ( function )* )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            FUNCTIONS23=(CtrlTree)match(input,FUNCTIONS,FOLLOW_FUNCTIONS_in_functions273); 


            if ( _first_0==null ) _first_0 = FUNCTIONS23;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:76:18: ( function )*
                loop5:
                do {
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0==FUNCTION) ) {
                        alt5=1;
                    }


                    switch (alt5) {
                	case 1 :
                	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:76:18: function
                	    {
                	    _last = (CtrlTree)input.LT(1);
                	    pushFollow(FOLLOW_function_in_functions275);
                	    function24=function();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = function24.tree;


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


             helper.reorderFunctions(FUNCTIONS23); 

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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:80:1: function : ^( FUNCTION ID ^( PARS ( par_decl )* ) ( INT_LIT )? block ) ;
    public final CtrlChecker.function_return function() throws RecognitionException {
        CtrlChecker.function_return retval = new CtrlChecker.function_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree FUNCTION25=null;
        CtrlTree ID26=null;
        CtrlTree PARS27=null;
        CtrlTree INT_LIT29=null;
        CtrlChecker.par_decl_return par_decl28 =null;

        CtrlChecker.block_return block30 =null;


        CtrlTree FUNCTION25_tree=null;
        CtrlTree ID26_tree=null;
        CtrlTree PARS27_tree=null;
        CtrlTree INT_LIT29_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:81:3: ( ^( FUNCTION ID ^( PARS ( par_decl )* ) ( INT_LIT )? block ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:81:5: ^( FUNCTION ID ^( PARS ( par_decl )* ) ( INT_LIT )? block )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            FUNCTION25=(CtrlTree)match(input,FUNCTION,FOLLOW_FUNCTION_in_function301); 


            if ( _first_0==null ) _first_0 = FUNCTION25;
             helper.startBody(FUNCTION25); 

            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            ID26=(CtrlTree)match(input,ID,FOLLOW_ID_in_function319); 
             
            if ( _first_1==null ) _first_1 = ID26;


            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_2 = _last;
            CtrlTree _first_2 = null;
            _last = (CtrlTree)input.LT(1);
            PARS27=(CtrlTree)match(input,PARS,FOLLOW_PARS_in_function322); 


            if ( _first_1==null ) _first_1 = PARS27;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:83:18: ( par_decl )*
                loop6:
                do {
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0==PAR) ) {
                        alt6=1;
                    }


                    switch (alt6) {
                	case 1 :
                	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:83:18: par_decl
                	    {
                	    _last = (CtrlTree)input.LT(1);
                	    pushFollow(FOLLOW_par_decl_in_function324);
                	    par_decl28=par_decl();

                	    state._fsp--;

                	     
                	    if ( _first_2==null ) _first_2 = par_decl28.tree;


                	    retval.tree = (CtrlTree)_first_0;
                	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                	        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                	    }
                	    break;

                	default :
                	    break loop6;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }
            _last = _save_last_2;
            }


            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:83:29: ( INT_LIT )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==INT_LIT) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:83:29: INT_LIT
                    {
                    _last = (CtrlTree)input.LT(1);
                    INT_LIT29=(CtrlTree)match(input,INT_LIT,FOLLOW_INT_LIT_in_function328); 
                     
                    if ( _first_1==null ) _first_1 = INT_LIT29;


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;

            }


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_block_in_function338);
            block30=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block30.tree;


             helper.endBody((block30!=null?((CtrlTree)block30.tree):null)); 

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
    // $ANTLR end "function"


    public static class par_decl_return extends TreeRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "par_decl"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:89:1: par_decl : ^( PAR ( OUT )? type ID ) ;
    public final CtrlChecker.par_decl_return par_decl() throws RecognitionException {
        CtrlChecker.par_decl_return retval = new CtrlChecker.par_decl_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree PAR31=null;
        CtrlTree OUT32=null;
        CtrlTree ID34=null;
        CtrlChecker.type_return type33 =null;


        CtrlTree PAR31_tree=null;
        CtrlTree OUT32_tree=null;
        CtrlTree ID34_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:90:3: ( ^( PAR ( OUT )? type ID ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:90:5: ^( PAR ( OUT )? type ID )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            PAR31=(CtrlTree)match(input,PAR,FOLLOW_PAR_in_par_decl371); 


            if ( _first_0==null ) _first_0 = PAR31;
            match(input, Token.DOWN, null); 
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:90:11: ( OUT )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==OUT) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:90:11: OUT
                    {
                    _last = (CtrlTree)input.LT(1);
                    OUT32=(CtrlTree)match(input,OUT,FOLLOW_OUT_in_par_decl373); 
                     
                    if ( _first_1==null ) _first_1 = OUT32;


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;

            }


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_type_in_par_decl376);
            type33=type();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = type33.tree;


            _last = (CtrlTree)input.LT(1);
            ID34=(CtrlTree)match(input,ID,FOLLOW_ID_in_par_decl378); 
             
            if ( _first_1==null ) _first_1 = ID34;


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


             helper.declarePar(ID34, (type33!=null?((CtrlTree)type33.tree):null), OUT32); 

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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:94:1: block returns [ CtrlAut aut ] : ^( BLOCK ( stat )* ) ;
    public final CtrlChecker.block_return block() throws RecognitionException {
        CtrlChecker.block_return retval = new CtrlChecker.block_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree BLOCK35=null;
        CtrlChecker.stat_return stat36 =null;


        CtrlTree BLOCK35_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:95:3: ( ^( BLOCK ( stat )* ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:95:5: ^( BLOCK ( stat )* )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            BLOCK35=(CtrlTree)match(input,BLOCK,FOLLOW_BLOCK_in_block406); 


            if ( _first_0==null ) _first_0 = BLOCK35;
             helper.openScope(); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:97:8: ( stat )*
                loop9:
                do {
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0==ALAP||LA9_0==ANY||LA9_0==ATOM||LA9_0==BLOCK||(LA9_0 >= CALL && LA9_0 <= CHOICE)||LA9_0==IF||LA9_0==OTHER||LA9_0==SEMI||LA9_0==STAR||(LA9_0 >= TRUE && LA9_0 <= UNTIL)||LA9_0==WHILE) ) {
                        alt9=1;
                    }


                    switch (alt9) {
                	case 1 :
                	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:97:8: stat
                	    {
                	    _last = (CtrlTree)input.LT(1);
                	    pushFollow(FOLLOW_stat_in_block424);
                	    stat36=stat();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = stat36.tree;


                	    retval.tree = (CtrlTree)_first_0;
                	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                	        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                	    }
                	    break;

                	default :
                	    break loop9;
                    }
                } while (true);


                 helper.closeScope(); 

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
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "stat"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:102:1: stat : ( block | ^( SEMI var_decl ) | ^( SEMI stat ) | ^( ALAP stat ) | ^( ATOM stat ) | ^( WHILE stat stat ) | ^( UNTIL stat stat ) | ^( TRY stat ( stat )? ) | ^( IF stat stat ( stat )? ) | ^( CHOICE stat ( stat )* ) | ^( STAR stat ) | rule | ANY | OTHER | TRUE );
    public final CtrlChecker.stat_return stat() throws RecognitionException {
        CtrlChecker.stat_return retval = new CtrlChecker.stat_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree SEMI38=null;
        CtrlTree SEMI40=null;
        CtrlTree ALAP42=null;
        CtrlTree ATOM44=null;
        CtrlTree WHILE46=null;
        CtrlTree UNTIL49=null;
        CtrlTree TRY52=null;
        CtrlTree IF55=null;
        CtrlTree CHOICE59=null;
        CtrlTree STAR62=null;
        CtrlTree ANY65=null;
        CtrlTree OTHER66=null;
        CtrlTree TRUE67=null;
        CtrlChecker.block_return block37 =null;

        CtrlChecker.var_decl_return var_decl39 =null;

        CtrlChecker.stat_return stat41 =null;

        CtrlChecker.stat_return stat43 =null;

        CtrlChecker.stat_return stat45 =null;

        CtrlChecker.stat_return stat47 =null;

        CtrlChecker.stat_return stat48 =null;

        CtrlChecker.stat_return stat50 =null;

        CtrlChecker.stat_return stat51 =null;

        CtrlChecker.stat_return stat53 =null;

        CtrlChecker.stat_return stat54 =null;

        CtrlChecker.stat_return stat56 =null;

        CtrlChecker.stat_return stat57 =null;

        CtrlChecker.stat_return stat58 =null;

        CtrlChecker.stat_return stat60 =null;

        CtrlChecker.stat_return stat61 =null;

        CtrlChecker.stat_return stat63 =null;

        CtrlChecker.rule_return rule64 =null;


        CtrlTree SEMI38_tree=null;
        CtrlTree SEMI40_tree=null;
        CtrlTree ALAP42_tree=null;
        CtrlTree ATOM44_tree=null;
        CtrlTree WHILE46_tree=null;
        CtrlTree UNTIL49_tree=null;
        CtrlTree TRY52_tree=null;
        CtrlTree IF55_tree=null;
        CtrlTree CHOICE59_tree=null;
        CtrlTree STAR62_tree=null;
        CtrlTree ANY65_tree=null;
        CtrlTree OTHER66_tree=null;
        CtrlTree TRUE67_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:103:3: ( block | ^( SEMI var_decl ) | ^( SEMI stat ) | ^( ALAP stat ) | ^( ATOM stat ) | ^( WHILE stat stat ) | ^( UNTIL stat stat ) | ^( TRY stat ( stat )? ) | ^( IF stat stat ( stat )? ) | ^( CHOICE stat ( stat )* ) | ^( STAR stat ) | rule | ANY | OTHER | TRUE )
            int alt13=15;
            switch ( input.LA(1) ) {
            case BLOCK:
                {
                alt13=1;
                }
                break;
            case SEMI:
                {
                int LA13_2 = input.LA(2);

                if ( (LA13_2==DOWN) ) {
                    int LA13_15 = input.LA(3);

                    if ( (LA13_15==VAR) ) {
                        alt13=2;
                    }
                    else if ( (LA13_15==ALAP||LA13_15==ANY||LA13_15==ATOM||LA13_15==BLOCK||(LA13_15 >= CALL && LA13_15 <= CHOICE)||LA13_15==IF||LA13_15==OTHER||LA13_15==SEMI||LA13_15==STAR||(LA13_15 >= TRUE && LA13_15 <= UNTIL)||LA13_15==WHILE) ) {
                        alt13=3;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 13, 15, input);

                        throw nvae;

                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 13, 2, input);

                    throw nvae;

                }
                }
                break;
            case ALAP:
                {
                alt13=4;
                }
                break;
            case ATOM:
                {
                alt13=5;
                }
                break;
            case WHILE:
                {
                alt13=6;
                }
                break;
            case UNTIL:
                {
                alt13=7;
                }
                break;
            case TRY:
                {
                alt13=8;
                }
                break;
            case IF:
                {
                alt13=9;
                }
                break;
            case CHOICE:
                {
                alt13=10;
                }
                break;
            case STAR:
                {
                alt13=11;
                }
                break;
            case CALL:
                {
                alt13=12;
                }
                break;
            case ANY:
                {
                alt13=13;
                }
                break;
            case OTHER:
                {
                alt13=14;
                }
                break;
            case TRUE:
                {
                alt13=15;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;

            }

            switch (alt13) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:103:5: block
                    {
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_block_in_stat454);
                    block37=block();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = block37.tree;


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:104:5: ^( SEMI var_decl )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    SEMI38=(CtrlTree)match(input,SEMI,FOLLOW_SEMI_in_stat461); 


                    if ( _first_0==null ) _first_0 = SEMI38;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_var_decl_in_stat463);
                    var_decl39=var_decl();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = var_decl39.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 3 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:105:5: ^( SEMI stat )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    SEMI40=(CtrlTree)match(input,SEMI,FOLLOW_SEMI_in_stat471); 


                    if ( _first_0==null ) _first_0 = SEMI40;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat473);
                    stat41=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat41.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 4 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:106:5: ^( ALAP stat )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    ALAP42=(CtrlTree)match(input,ALAP,FOLLOW_ALAP_in_stat481); 


                    if ( _first_0==null ) _first_0 = ALAP42;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat483);
                    stat43=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat43.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 5 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:107:5: ^( ATOM stat )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    ATOM44=(CtrlTree)match(input,ATOM,FOLLOW_ATOM_in_stat491); 


                    if ( _first_0==null ) _first_0 = ATOM44;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat493);
                    stat45=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat45.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 6 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:108:5: ^( WHILE stat stat )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    WHILE46=(CtrlTree)match(input,WHILE,FOLLOW_WHILE_in_stat502); 


                    if ( _first_0==null ) _first_0 = WHILE46;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat511);
                    stat47=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat47.tree;


                     helper.startBranch(); 

                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat529);
                    stat48=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat48.tree;


                     helper.endBranch(); 

                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 7 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:114:5: ^( UNTIL stat stat )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    UNTIL49=(CtrlTree)match(input,UNTIL,FOLLOW_UNTIL_in_stat553); 


                    if ( _first_0==null ) _first_0 = UNTIL49;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat562);
                    stat50=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat50.tree;


                     helper.startBranch(); 

                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat580);
                    stat51=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat51.tree;


                     helper.endBranch(); 

                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 8 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:120:5: ^( TRY stat ( stat )? )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    TRY52=(CtrlTree)match(input,TRY,FOLLOW_TRY_in_stat604); 


                    if ( _first_0==null ) _first_0 = TRY52;
                     helper.startBranch(); 

                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat622);
                    stat53=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat53.tree;


                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:123:8: ( stat )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0==ALAP||LA10_0==ANY||LA10_0==ATOM||LA10_0==BLOCK||(LA10_0 >= CALL && LA10_0 <= CHOICE)||LA10_0==IF||LA10_0==OTHER||LA10_0==SEMI||LA10_0==STAR||(LA10_0 >= TRUE && LA10_0 <= UNTIL)||LA10_0==WHILE) ) {
                        alt10=1;
                    }
                    switch (alt10) {
                        case 1 :
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:123:10: stat
                            {
                             helper.nextBranch(); 

                            _last = (CtrlTree)input.LT(1);
                            pushFollow(FOLLOW_stat_in_stat644);
                            stat54=stat();

                            state._fsp--;

                             
                            if ( _first_1==null ) _first_1 = stat54.tree;


                            retval.tree = (CtrlTree)_first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                            }
                            break;

                    }


                     helper.endBranch(); 

                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 9 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:128:5: ^( IF stat stat ( stat )? )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    IF55=(CtrlTree)match(input,IF,FOLLOW_IF_in_stat678); 


                    if ( _first_0==null ) _first_0 = IF55;
                     helper.startBranch(); 

                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat697);
                    stat56=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat56.tree;


                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat707);
                    stat57=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat57.tree;


                     helper.nextBranch(); 

                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:133:8: ( stat )?
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0==ALAP||LA11_0==ANY||LA11_0==ATOM||LA11_0==BLOCK||(LA11_0 >= CALL && LA11_0 <= CHOICE)||LA11_0==IF||LA11_0==OTHER||LA11_0==SEMI||LA11_0==STAR||(LA11_0 >= TRUE && LA11_0 <= UNTIL)||LA11_0==WHILE) ) {
                        alt11=1;
                    }
                    switch (alt11) {
                        case 1 :
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:133:8: stat
                            {
                            _last = (CtrlTree)input.LT(1);
                            pushFollow(FOLLOW_stat_in_stat725);
                            stat58=stat();

                            state._fsp--;

                             
                            if ( _first_1==null ) _first_1 = stat58.tree;


                            retval.tree = (CtrlTree)_first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                            }
                            break;

                    }


                     helper.endBranch(); 

                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 10 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:136:5: ^( CHOICE stat ( stat )* )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    CHOICE59=(CtrlTree)match(input,CHOICE,FOLLOW_CHOICE_in_stat750); 


                    if ( _first_0==null ) _first_0 = CHOICE59;
                     helper.startBranch(); 

                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat768);
                    stat60=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat60.tree;


                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:139:8: ( stat )*
                    loop12:
                    do {
                        int alt12=2;
                        int LA12_0 = input.LA(1);

                        if ( (LA12_0==ALAP||LA12_0==ANY||LA12_0==ATOM||LA12_0==BLOCK||(LA12_0 >= CALL && LA12_0 <= CHOICE)||LA12_0==IF||LA12_0==OTHER||LA12_0==SEMI||LA12_0==STAR||(LA12_0 >= TRUE && LA12_0 <= UNTIL)||LA12_0==WHILE) ) {
                            alt12=1;
                        }


                        switch (alt12) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:139:10: stat
                    	    {
                    	     helper.nextBranch(); 

                    	    _last = (CtrlTree)input.LT(1);
                    	    pushFollow(FOLLOW_stat_in_stat791);
                    	    stat61=stat();

                    	    state._fsp--;

                    	     
                    	    if ( _first_1==null ) _first_1 = stat61.tree;


                    	    retval.tree = (CtrlTree)_first_0;
                    	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                    	        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    	    }
                    	    break;

                    	default :
                    	    break loop12;
                        }
                    } while (true);


                     helper.endBranch(); 

                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 11 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:144:5: ^( STAR stat )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    STAR62=(CtrlTree)match(input,STAR,FOLLOW_STAR_in_stat825); 


                    if ( _first_0==null ) _first_0 = STAR62;
                     helper.startBranch(); 

                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat843);
                    stat63=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat63.tree;


                     helper.endBranch(); 

                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 12 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:149:5: rule
                    {
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_rule_in_stat865);
                    rule64=rule();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = rule64.tree;


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 13 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:150:5: ANY
                    {
                    _last = (CtrlTree)input.LT(1);
                    ANY65=(CtrlTree)match(input,ANY,FOLLOW_ANY_in_stat871); 
                     
                    if ( _first_0==null ) _first_0 = ANY65;


                     helper.checkAny(ANY65); 

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 14 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:152:5: OTHER
                    {
                    _last = (CtrlTree)input.LT(1);
                    OTHER66=(CtrlTree)match(input,OTHER,FOLLOW_OTHER_in_stat883); 
                     
                    if ( _first_0==null ) _first_0 = OTHER66;


                     helper.checkOther(OTHER66); 

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 15 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:154:5: TRUE
                    {
                    _last = (CtrlTree)input.LT(1);
                    TRUE67=(CtrlTree)match(input,TRUE,FOLLOW_TRUE_in_stat895); 
                     
                    if ( _first_0==null ) _first_0 = TRUE67;


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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:157:1: rule : ^( CALL id= ID ( ^( ARGS ( arg )* RPAR ) )? ) ;
    public final CtrlChecker.rule_return rule() throws RecognitionException {
        CtrlChecker.rule_return retval = new CtrlChecker.rule_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree id=null;
        CtrlTree CALL68=null;
        CtrlTree ARGS69=null;
        CtrlTree RPAR71=null;
        CtrlChecker.arg_return arg70 =null;


        CtrlTree id_tree=null;
        CtrlTree CALL68_tree=null;
        CtrlTree ARGS69_tree=null;
        CtrlTree RPAR71_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:159:3: ( ^( CALL id= ID ( ^( ARGS ( arg )* RPAR ) )? ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:159:5: ^( CALL id= ID ( ^( ARGS ( arg )* RPAR ) )? )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            CALL68=(CtrlTree)match(input,CALL,FOLLOW_CALL_in_rule913); 


            if ( _first_0==null ) _first_0 = CALL68;
            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            id=(CtrlTree)match(input,ID,FOLLOW_ID_in_rule917); 
             
            if ( _first_1==null ) _first_1 = id;


            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:159:18: ( ^( ARGS ( arg )* RPAR ) )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==ARGS) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:159:19: ^( ARGS ( arg )* RPAR )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_2 = _last;
                    CtrlTree _first_2 = null;
                    _last = (CtrlTree)input.LT(1);
                    ARGS69=(CtrlTree)match(input,ARGS,FOLLOW_ARGS_in_rule921); 


                    if ( _first_1==null ) _first_1 = ARGS69;
                    match(input, Token.DOWN, null); 
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:159:26: ( arg )*
                    loop14:
                    do {
                        int alt14=2;
                        int LA14_0 = input.LA(1);

                        if ( (LA14_0==ARG) ) {
                            alt14=1;
                        }


                        switch (alt14) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:159:26: arg
                    	    {
                    	    _last = (CtrlTree)input.LT(1);
                    	    pushFollow(FOLLOW_arg_in_rule923);
                    	    arg70=arg();

                    	    state._fsp--;

                    	     
                    	    if ( _first_2==null ) _first_2 = arg70.tree;


                    	    retval.tree = (CtrlTree)_first_0;
                    	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                    	        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    	    }
                    	    break;

                    	default :
                    	    break loop14;
                        }
                    } while (true);


                    _last = (CtrlTree)input.LT(1);
                    RPAR71=(CtrlTree)match(input,RPAR,FOLLOW_RPAR_in_rule926); 
                     
                    if ( _first_2==null ) _first_2 = RPAR71;


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

             helper.checkCall(((CtrlTree)retval.tree)); 
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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:162:1: var_decl : ^( VAR type ( ID )+ ) ;
    public final CtrlChecker.var_decl_return var_decl() throws RecognitionException {
        CtrlChecker.var_decl_return retval = new CtrlChecker.var_decl_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree VAR72=null;
        CtrlTree ID74=null;
        CtrlChecker.type_return type73 =null;


        CtrlTree VAR72_tree=null;
        CtrlTree ID74_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:163:2: ( ^( VAR type ( ID )+ ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:163:4: ^( VAR type ( ID )+ )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            VAR72=(CtrlTree)match(input,VAR,FOLLOW_VAR_in_var_decl944); 


            if ( _first_0==null ) _first_0 = VAR72;
            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_type_in_var_decl946);
            type73=type();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = type73.tree;


            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:164:7: ( ID )+
            int cnt16=0;
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==ID) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:164:9: ID
            	    {
            	    _last = (CtrlTree)input.LT(1);
            	    ID74=(CtrlTree)match(input,ID,FOLLOW_ID_in_var_decl956); 
            	     
            	    if ( _first_1==null ) _first_1 = ID74;


            	     helper.declareVar(ID74, (type73!=null?((CtrlTree)type73.tree):null)); 

            	    retval.tree = (CtrlTree)_first_0;
            	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
            	        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

            	    }
            	    break;

            	default :
            	    if ( cnt16 >= 1 ) break loop16;
                        EarlyExitException eee =
                            new EarlyExitException(16, input);
                        throw eee;
                }
                cnt16++;
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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:170:1: type : ( NODE -> NODE | BOOL -> BOOL | STRING -> STRING | INT -> INT | REAL -> REAL );
    public final CtrlChecker.type_return type() throws RecognitionException {
        CtrlChecker.type_return retval = new CtrlChecker.type_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree NODE75=null;
        CtrlTree BOOL76=null;
        CtrlTree STRING77=null;
        CtrlTree INT78=null;
        CtrlTree REAL79=null;

        CtrlTree NODE75_tree=null;
        CtrlTree BOOL76_tree=null;
        CtrlTree STRING77_tree=null;
        CtrlTree INT78_tree=null;
        CtrlTree REAL79_tree=null;
        RewriteRuleNodeStream stream_REAL=new RewriteRuleNodeStream(adaptor,"token REAL");
        RewriteRuleNodeStream stream_INT=new RewriteRuleNodeStream(adaptor,"token INT");
        RewriteRuleNodeStream stream_NODE=new RewriteRuleNodeStream(adaptor,"token NODE");
        RewriteRuleNodeStream stream_STRING=new RewriteRuleNodeStream(adaptor,"token STRING");
        RewriteRuleNodeStream stream_BOOL=new RewriteRuleNodeStream(adaptor,"token BOOL");

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:173:3: ( NODE -> NODE | BOOL -> BOOL | STRING -> STRING | INT -> INT | REAL -> REAL )
            int alt17=5;
            switch ( input.LA(1) ) {
            case NODE:
                {
                alt17=1;
                }
                break;
            case BOOL:
                {
                alt17=2;
                }
                break;
            case STRING:
                {
                alt17=3;
                }
                break;
            case INT:
                {
                alt17=4;
                }
                break;
            case REAL:
                {
                alt17=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 17, 0, input);

                throw nvae;

            }

            switch (alt17) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:173:5: NODE
                    {
                    _last = (CtrlTree)input.LT(1);
                    NODE75=(CtrlTree)match(input,NODE,FOLLOW_NODE_in_type1001);  
                    stream_NODE.add(NODE75);


                    // AST REWRITE
                    // elements: NODE
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CtrlTree)adaptor.nil();
                    // 173:10: -> NODE
                    {
                        adaptor.addChild(root_0, 
                        stream_NODE.nextNode()
                        );

                    }


                    retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
                    input.replaceChildren(adaptor.getParent(retval.start),
                                          adaptor.getChildIndex(retval.start),
                                          adaptor.getChildIndex(_last),
                                          retval.tree);

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:174:5: BOOL
                    {
                    _last = (CtrlTree)input.LT(1);
                    BOOL76=(CtrlTree)match(input,BOOL,FOLLOW_BOOL_in_type1011);  
                    stream_BOOL.add(BOOL76);


                    // AST REWRITE
                    // elements: BOOL
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CtrlTree)adaptor.nil();
                    // 174:10: -> BOOL
                    {
                        adaptor.addChild(root_0, 
                        stream_BOOL.nextNode()
                        );

                    }


                    retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
                    input.replaceChildren(adaptor.getParent(retval.start),
                                          adaptor.getChildIndex(retval.start),
                                          adaptor.getChildIndex(_last),
                                          retval.tree);

                    }
                    break;
                case 3 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:175:5: STRING
                    {
                    _last = (CtrlTree)input.LT(1);
                    STRING77=(CtrlTree)match(input,STRING,FOLLOW_STRING_in_type1021);  
                    stream_STRING.add(STRING77);


                    // AST REWRITE
                    // elements: STRING
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CtrlTree)adaptor.nil();
                    // 175:12: -> STRING
                    {
                        adaptor.addChild(root_0, 
                        stream_STRING.nextNode()
                        );

                    }


                    retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
                    input.replaceChildren(adaptor.getParent(retval.start),
                                          adaptor.getChildIndex(retval.start),
                                          adaptor.getChildIndex(_last),
                                          retval.tree);

                    }
                    break;
                case 4 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:176:5: INT
                    {
                    _last = (CtrlTree)input.LT(1);
                    INT78=(CtrlTree)match(input,INT,FOLLOW_INT_in_type1031);  
                    stream_INT.add(INT78);


                    // AST REWRITE
                    // elements: INT
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CtrlTree)adaptor.nil();
                    // 176:9: -> INT
                    {
                        adaptor.addChild(root_0, 
                        stream_INT.nextNode()
                        );

                    }


                    retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
                    input.replaceChildren(adaptor.getParent(retval.start),
                                          adaptor.getChildIndex(retval.start),
                                          adaptor.getChildIndex(_last),
                                          retval.tree);

                    }
                    break;
                case 5 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:177:5: REAL
                    {
                    _last = (CtrlTree)input.LT(1);
                    REAL79=(CtrlTree)match(input,REAL,FOLLOW_REAL_in_type1041);  
                    stream_REAL.add(REAL79);


                    // AST REWRITE
                    // elements: REAL
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CtrlTree)adaptor.nil();
                    // 177:10: -> REAL
                    {
                        adaptor.addChild(root_0, 
                        stream_REAL.nextNode()
                        );

                    }


                    retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
                    input.replaceChildren(adaptor.getParent(retval.start),
                                          adaptor.getChildIndex(retval.start),
                                          adaptor.getChildIndex(_last),
                                          retval.tree);

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
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "arg"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:180:1: arg : ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) ) ;
    public final CtrlChecker.arg_return arg() throws RecognitionException {
        CtrlChecker.arg_return retval = new CtrlChecker.arg_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree ARG80=null;
        CtrlTree OUT81=null;
        CtrlTree ID82=null;
        CtrlTree DONT_CARE83=null;
        CtrlChecker.literal_return literal84 =null;


        CtrlTree ARG80_tree=null;
        CtrlTree OUT81_tree=null;
        CtrlTree ID82_tree=null;
        CtrlTree DONT_CARE83_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:181:2: ( ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:181:4: ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            ARG80=(CtrlTree)match(input,ARG,FOLLOW_ARG_in_arg1061); 


            if ( _first_0==null ) _first_0 = ARG80;
            match(input, Token.DOWN, null); 
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:182:7: ( ( OUT )? ID | DONT_CARE | literal )
            int alt19=3;
            switch ( input.LA(1) ) {
            case ID:
            case OUT:
                {
                alt19=1;
                }
                break;
            case DONT_CARE:
                {
                alt19=2;
                }
                break;
            case FALSE:
            case INT_LIT:
            case REAL_LIT:
            case STRING_LIT:
            case TRUE:
                {
                alt19=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 19, 0, input);

                throw nvae;

            }

            switch (alt19) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:182:9: ( OUT )? ID
                    {
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:182:9: ( OUT )?
                    int alt18=2;
                    int LA18_0 = input.LA(1);

                    if ( (LA18_0==OUT) ) {
                        alt18=1;
                    }
                    switch (alt18) {
                        case 1 :
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:182:9: OUT
                            {
                            _last = (CtrlTree)input.LT(1);
                            OUT81=(CtrlTree)match(input,OUT,FOLLOW_OUT_in_arg1072); 
                             
                            if ( _first_1==null ) _first_1 = OUT81;


                            retval.tree = (CtrlTree)_first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                            }
                            break;

                    }


                    _last = (CtrlTree)input.LT(1);
                    ID82=(CtrlTree)match(input,ID,FOLLOW_ID_in_arg1075); 
                     
                    if ( _first_1==null ) _first_1 = ID82;


                     helper.checkVarArg(ARG80); 

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:183:9: DONT_CARE
                    {
                    _last = (CtrlTree)input.LT(1);
                    DONT_CARE83=(CtrlTree)match(input,DONT_CARE,FOLLOW_DONT_CARE_in_arg1087); 
                     
                    if ( _first_1==null ) _first_1 = DONT_CARE83;


                     helper.checkDontCareArg(ARG80); 

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 3 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:184:9: literal
                    {
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_literal_in_arg1099);
                    literal84=literal();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = literal84.tree;


                     helper.checkConstArg(ARG80); 

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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:189:1: literal : ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT );
    public final CtrlChecker.literal_return literal() throws RecognitionException {
        CtrlChecker.literal_return retval = new CtrlChecker.literal_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree set85=null;

        CtrlTree set85_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:190:3: ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:
            {
            _last = (CtrlTree)input.LT(1);
            set85=(CtrlTree)input.LT(1);

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


 

    public static final BitSet FOLLOW_PROGRAM_in_program61 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_package_decl_in_program63 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_imports_in_program65 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_functions_in_program67 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_recipes_in_program69 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_block_in_program71 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PACKAGE_in_package_decl88 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_package_decl90 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_SEMI_in_package_decl92 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IMPORTS_in_imports122 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_import_decl_in_imports124 = new BitSet(new long[]{0x0000000040000008L});
    public static final BitSet FOLLOW_IMPORT_in_import_decl141 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_import_decl143 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_SEMI_in_import_decl145 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RECIPES_in_recipes175 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_recipe_in_recipes177 = new BitSet(new long[]{0x0200000000000008L});
    public static final BitSet FOLLOW_RECIPE_in_recipe203 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_recipe222 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_PARS_in_recipe225 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_par_decl_in_recipe227 = new BitSet(new long[]{0x0000800000000008L});
    public static final BitSet FOLLOW_INT_LIT_in_recipe231 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_block_in_recipe241 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FUNCTIONS_in_functions273 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_function_in_functions275 = new BitSet(new long[]{0x0000000004000008L});
    public static final BitSet FOLLOW_FUNCTION_in_function301 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_function319 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_PARS_in_function322 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_par_decl_in_function324 = new BitSet(new long[]{0x0000800000000008L});
    public static final BitSet FOLLOW_INT_LIT_in_function328 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_block_in_function338 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PAR_in_par_decl371 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_OUT_in_par_decl373 = new BitSet(new long[]{0x0080010100002000L,0x0000000000000001L});
    public static final BitSet FOLLOW_type_in_par_decl376 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_par_decl378 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BLOCK_in_block406 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_block424 = new BitSet(new long[]{0x9000100020019458L,0x000000000000005CL});
    public static final BitSet FOLLOW_block_in_stat454 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMI_in_stat461 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_var_decl_in_stat463 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SEMI_in_stat471 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat473 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ALAP_in_stat481 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat483 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ATOM_in_stat491 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat493 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WHILE_in_stat502 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat511 = new BitSet(new long[]{0x9000100020019450L,0x000000000000005CL});
    public static final BitSet FOLLOW_stat_in_stat529 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_UNTIL_in_stat553 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat562 = new BitSet(new long[]{0x9000100020019450L,0x000000000000005CL});
    public static final BitSet FOLLOW_stat_in_stat580 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TRY_in_stat604 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat622 = new BitSet(new long[]{0x9000100020019458L,0x000000000000005CL});
    public static final BitSet FOLLOW_stat_in_stat644 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IF_in_stat678 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat697 = new BitSet(new long[]{0x9000100020019450L,0x000000000000005CL});
    public static final BitSet FOLLOW_stat_in_stat707 = new BitSet(new long[]{0x9000100020019458L,0x000000000000005CL});
    public static final BitSet FOLLOW_stat_in_stat725 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CHOICE_in_stat750 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat768 = new BitSet(new long[]{0x9000100020019458L,0x000000000000005CL});
    public static final BitSet FOLLOW_stat_in_stat791 = new BitSet(new long[]{0x9000100020019458L,0x000000000000005CL});
    public static final BitSet FOLLOW_STAR_in_stat825 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat843 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_rule_in_stat865 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_stat871 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_stat883 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_stat895 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CALL_in_rule913 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rule917 = new BitSet(new long[]{0x0000000000000108L});
    public static final BitSet FOLLOW_ARGS_in_rule921 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_arg_in_rule923 = new BitSet(new long[]{0x0800000000000080L});
    public static final BitSet FOLLOW_RPAR_in_rule926 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VAR_in_var_decl944 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_type_in_var_decl946 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_var_decl956 = new BitSet(new long[]{0x0000000010000008L});
    public static final BitSet FOLLOW_NODE_in_type1001 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_type1011 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_type1021 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_type1031 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REAL_in_type1041 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ARG_in_arg1061 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_OUT_in_arg1072 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_arg1075 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DONT_CARE_in_arg1087 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_literal_in_arg1099 = new BitSet(new long[]{0x0000000000000008L});

}