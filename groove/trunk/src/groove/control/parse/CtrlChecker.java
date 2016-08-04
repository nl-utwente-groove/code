// $ANTLR 3.4 E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g 2016-06-29 23:28:20

package groove.control.parse;
import groove.util.antlr.ParseInfo;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;


@SuppressWarnings({"all", "warnings", "unchecked"})
public class CtrlChecker extends TreeParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ALAP", "AMP", "ANY", "ARG", "ARGS", "ASTERISK", "ATOM", "BAR", "BECOMES", "BLOCK", "BOOL", "BQUOTE", "BSLASH", "CALL", "CHOICE", "COMMA", "DO", "DONT_CARE", "DOT", "DO_UNTIL", "DO_WHILE", "ELSE", "EscapeSequence", "FALSE", "FUNCTION", "FUNCTIONS", "ID", "IF", "IMPORT", "IMPORTS", "INT", "INT_LIT", "IntegerNumber", "LANGLE", "LCURLY", "LPAR", "MINUS", "ML_COMMENT", "NODE", "NOT", "NonIntegerNumber", "OR", "OTHER", "OUT", "PACKAGE", "PAR", "PARS", "PLUS", "PRIORITY", "PROGRAM", "QUOTE", "RANGLE", "RCURLY", "REAL", "REAL_LIT", "RECIPE", "RECIPES", "RPAR", "SEMI", "SHARP", "SL_COMMENT", "STAR", "STRING", "STRING_LIT", "TRUE", "TRY", "UNTIL", "VAR", "WHILE", "WS"
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
    public static final int BECOMES=12;
    public static final int BLOCK=13;
    public static final int BOOL=14;
    public static final int BQUOTE=15;
    public static final int BSLASH=16;
    public static final int CALL=17;
    public static final int CHOICE=18;
    public static final int COMMA=19;
    public static final int DO=20;
    public static final int DONT_CARE=21;
    public static final int DOT=22;
    public static final int DO_UNTIL=23;
    public static final int DO_WHILE=24;
    public static final int ELSE=25;
    public static final int EscapeSequence=26;
    public static final int FALSE=27;
    public static final int FUNCTION=28;
    public static final int FUNCTIONS=29;
    public static final int ID=30;
    public static final int IF=31;
    public static final int IMPORT=32;
    public static final int IMPORTS=33;
    public static final int INT=34;
    public static final int INT_LIT=35;
    public static final int IntegerNumber=36;
    public static final int LANGLE=37;
    public static final int LCURLY=38;
    public static final int LPAR=39;
    public static final int MINUS=40;
    public static final int ML_COMMENT=41;
    public static final int NODE=42;
    public static final int NOT=43;
    public static final int NonIntegerNumber=44;
    public static final int OR=45;
    public static final int OTHER=46;
    public static final int OUT=47;
    public static final int PACKAGE=48;
    public static final int PAR=49;
    public static final int PARS=50;
    public static final int PLUS=51;
    public static final int PRIORITY=52;
    public static final int PROGRAM=53;
    public static final int QUOTE=54;
    public static final int RANGLE=55;
    public static final int RCURLY=56;
    public static final int REAL=57;
    public static final int REAL_LIT=58;
    public static final int RECIPE=59;
    public static final int RECIPES=60;
    public static final int RPAR=61;
    public static final int SEMI=62;
    public static final int SHARP=63;
    public static final int SL_COMMENT=64;
    public static final int STAR=65;
    public static final int STRING=66;
    public static final int STRING_LIT=67;
    public static final int TRUE=68;
    public static final int TRY=69;
    public static final int UNTIL=70;
    public static final int VAR=71;
    public static final int WHILE=72;
    public static final int WS=73;

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
    @Override
    public String[] getTokenNames() { return CtrlChecker.tokenNames; }
    @Override
    public String getGrammarFileName() { return "E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g"; }


        /** Helper class to convert AST trees to namespace. */
        private CtrlHelper helper;
        
        @Override
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
        @Override
        public Object getTree() { return tree; }
    };


    // $ANTLR start "program"
    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:39:1: program : ^( PROGRAM package_decl imports functions recipes block ) ;
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
            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:41:3: ( ^( PROGRAM package_decl imports functions recipes block ) )
            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:41:5: ^( PROGRAM package_decl imports functions recipes block )
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


            retval.tree = _first_0;
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
        @Override
        public Object getTree() { return tree; }
    };


    // $ANTLR start "package_decl"
    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:44:1: package_decl : ^( PACKAGE qual_id SEMI ) ;
    public final CtrlChecker.package_decl_return package_decl() throws RecognitionException {
        CtrlChecker.package_decl_return retval = new CtrlChecker.package_decl_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree PACKAGE7=null;
        CtrlTree SEMI9=null;
        CtrlChecker.qual_id_return qual_id8 =null;


        CtrlTree PACKAGE7_tree=null;
        CtrlTree SEMI9_tree=null;

        try {
            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:45:3: ( ^( PACKAGE qual_id SEMI ) )
            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:45:5: ^( PACKAGE qual_id SEMI )
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
            pushFollow(FOLLOW_qual_id_in_package_decl90);
            qual_id8=qual_id();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = qual_id8.tree;


            _last = (CtrlTree)input.LT(1);
            SEMI9=(CtrlTree)match(input,SEMI,FOLLOW_SEMI_in_package_decl92); 
             
            if ( _first_1==null ) _first_1 = SEMI9;


             helper.checkPackage((qual_id8!=null?((CtrlTree)qual_id8.tree):null)); 

            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = _first_0;
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
        @Override
        public Object getTree() { return tree; }
    };


    // $ANTLR start "imports"
    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:50:1: imports : ^( IMPORTS ( import_decl )* ) ;
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
            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:51:3: ( ^( IMPORTS ( import_decl )* ) )
            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:51:5: ^( IMPORTS ( import_decl )* )
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
                // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:51:15: ( import_decl )*
                loop1:
                do {
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( (LA1_0==IMPORT) ) {
                        alt1=1;
                    }


                    switch (alt1) {
                	case 1 :
                	    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:51:15: import_decl
                	    {
                	    _last = (CtrlTree)input.LT(1);
                	    pushFollow(FOLLOW_import_decl_in_imports124);
                	    import_decl11=import_decl();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = import_decl11.tree;


                	    retval.tree = _first_0;
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


            retval.tree = _first_0;
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
        @Override
        public Object getTree() { return tree; }
    };


    // $ANTLR start "import_decl"
    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:54:1: import_decl : ^( IMPORT qual_id SEMI ) ;
    public final CtrlChecker.import_decl_return import_decl() throws RecognitionException {
        CtrlChecker.import_decl_return retval = new CtrlChecker.import_decl_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree IMPORT12=null;
        CtrlTree SEMI14=null;
        CtrlChecker.qual_id_return qual_id13 =null;


        CtrlTree IMPORT12_tree=null;
        CtrlTree SEMI14_tree=null;

        try {
            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:55:3: ( ^( IMPORT qual_id SEMI ) )
            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:55:5: ^( IMPORT qual_id SEMI )
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
            pushFollow(FOLLOW_qual_id_in_import_decl143);
            qual_id13=qual_id();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = qual_id13.tree;


            _last = (CtrlTree)input.LT(1);
            SEMI14=(CtrlTree)match(input,SEMI,FOLLOW_SEMI_in_import_decl145); 
             
            if ( _first_1==null ) _first_1 = SEMI14;


             helper.checkImport((qual_id13!=null?((CtrlTree)qual_id13.tree):null)); 

            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = _first_0;
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
        @Override
        public Object getTree() { return tree; }
    };


    // $ANTLR start "recipes"
    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:60:1: recipes : ^( RECIPES ( recipe )* ) ;
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
            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:61:3: ( ^( RECIPES ( recipe )* ) )
            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:61:5: ^( RECIPES ( recipe )* )
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
                // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:61:15: ( recipe )*
                loop2:
                do {
                    int alt2=2;
                    int LA2_0 = input.LA(1);

                    if ( (LA2_0==RECIPE) ) {
                        alt2=1;
                    }


                    switch (alt2) {
                	case 1 :
                	    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:61:15: recipe
                	    {
                	    _last = (CtrlTree)input.LT(1);
                	    pushFollow(FOLLOW_recipe_in_recipes177);
                	    recipe16=recipe();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = recipe16.tree;


                	    retval.tree = _first_0;
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


            retval.tree = _first_0;
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
        @Override
        public Object getTree() { return tree; }
    };


    // $ANTLR start "recipe"
    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:64:1: recipe : ^( RECIPE ID ^( PARS ( par_decl )* ) ( INT_LIT )? block ) ;
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
            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:65:3: ( ^( RECIPE ID ^( PARS ( par_decl )* ) ( INT_LIT )? block ) )
            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:65:5: ^( RECIPE ID ^( PARS ( par_decl )* ) ( INT_LIT )? block )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            RECIPE17=(CtrlTree)match(input,RECIPE,FOLLOW_RECIPE_in_recipe194); 


            if ( _first_0==null ) _first_0 = RECIPE17;
             helper.startBody(RECIPE17); 

            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            ID18=(CtrlTree)match(input,ID,FOLLOW_ID_in_recipe213); 
             
            if ( _first_1==null ) _first_1 = ID18;


            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_2 = _last;
            CtrlTree _first_2 = null;
            _last = (CtrlTree)input.LT(1);
            PARS19=(CtrlTree)match(input,PARS,FOLLOW_PARS_in_recipe216); 


            if ( _first_1==null ) _first_1 = PARS19;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:67:18: ( par_decl )*
                loop3:
                do {
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0==PAR) ) {
                        alt3=1;
                    }


                    switch (alt3) {
                	case 1 :
                	    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:67:18: par_decl
                	    {
                	    _last = (CtrlTree)input.LT(1);
                	    pushFollow(FOLLOW_par_decl_in_recipe218);
                	    par_decl20=par_decl();

                	    state._fsp--;

                	     
                	    if ( _first_2==null ) _first_2 = par_decl20.tree;


                	    retval.tree = _first_0;
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


            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:67:29: ( INT_LIT )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==INT_LIT) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:67:29: INT_LIT
                    {
                    _last = (CtrlTree)input.LT(1);
                    INT_LIT21=(CtrlTree)match(input,INT_LIT,FOLLOW_INT_LIT_in_recipe222); 
                     
                    if ( _first_1==null ) _first_1 = INT_LIT21;


                    retval.tree = _first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;

            }


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_block_in_recipe232);
            block22=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block22.tree;


             helper.endBody((block22!=null?((CtrlTree)block22.tree):null)); 

            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = _first_0;
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
        @Override
        public Object getTree() { return tree; }
    };


    // $ANTLR start "functions"
    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:73:1: functions : ^( FUNCTIONS ( function )* ) ;
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
            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:74:3: ( ^( FUNCTIONS ( function )* ) )
            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:74:5: ^( FUNCTIONS ( function )* )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            FUNCTIONS23=(CtrlTree)match(input,FUNCTIONS,FOLLOW_FUNCTIONS_in_functions264); 


            if ( _first_0==null ) _first_0 = FUNCTIONS23;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:74:18: ( function )*
                loop5:
                do {
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0==FUNCTION) ) {
                        alt5=1;
                    }


                    switch (alt5) {
                	case 1 :
                	    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:74:18: function
                	    {
                	    _last = (CtrlTree)input.LT(1);
                	    pushFollow(FOLLOW_function_in_functions266);
                	    function24=function();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = function24.tree;


                	    retval.tree = _first_0;
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


            retval.tree = _first_0;
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
        @Override
        public Object getTree() { return tree; }
    };


    // $ANTLR start "function"
    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:77:1: function : ^( FUNCTION ID ^( PARS ( par_decl )* ) block ) ;
    public final CtrlChecker.function_return function() throws RecognitionException {
        CtrlChecker.function_return retval = new CtrlChecker.function_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree FUNCTION25=null;
        CtrlTree ID26=null;
        CtrlTree PARS27=null;
        CtrlChecker.par_decl_return par_decl28 =null;

        CtrlChecker.block_return block29 =null;


        CtrlTree FUNCTION25_tree=null;
        CtrlTree ID26_tree=null;
        CtrlTree PARS27_tree=null;

        try {
            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:78:3: ( ^( FUNCTION ID ^( PARS ( par_decl )* ) block ) )
            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:78:5: ^( FUNCTION ID ^( PARS ( par_decl )* ) block )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            FUNCTION25=(CtrlTree)match(input,FUNCTION,FOLLOW_FUNCTION_in_function283); 


            if ( _first_0==null ) _first_0 = FUNCTION25;
             helper.startBody(FUNCTION25); 

            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            ID26=(CtrlTree)match(input,ID,FOLLOW_ID_in_function301); 
             
            if ( _first_1==null ) _first_1 = ID26;


            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_2 = _last;
            CtrlTree _first_2 = null;
            _last = (CtrlTree)input.LT(1);
            PARS27=(CtrlTree)match(input,PARS,FOLLOW_PARS_in_function304); 


            if ( _first_1==null ) _first_1 = PARS27;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:80:18: ( par_decl )*
                loop6:
                do {
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0==PAR) ) {
                        alt6=1;
                    }


                    switch (alt6) {
                	case 1 :
                	    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:80:18: par_decl
                	    {
                	    _last = (CtrlTree)input.LT(1);
                	    pushFollow(FOLLOW_par_decl_in_function306);
                	    par_decl28=par_decl();

                	    state._fsp--;

                	     
                	    if ( _first_2==null ) _first_2 = par_decl28.tree;


                	    retval.tree = _first_0;
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


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_block_in_function317);
            block29=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block29.tree;


             helper.endBody((block29!=null?((CtrlTree)block29.tree):null)); 

            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = _first_0;
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
        @Override
        public Object getTree() { return tree; }
    };


    // $ANTLR start "par_decl"
    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:86:1: par_decl : ^( PAR ( OUT )? type ID ) ;
    public final CtrlChecker.par_decl_return par_decl() throws RecognitionException {
        CtrlChecker.par_decl_return retval = new CtrlChecker.par_decl_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree PAR30=null;
        CtrlTree OUT31=null;
        CtrlTree ID33=null;
        CtrlChecker.type_return type32 =null;


        CtrlTree PAR30_tree=null;
        CtrlTree OUT31_tree=null;
        CtrlTree ID33_tree=null;

        try {
            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:87:3: ( ^( PAR ( OUT )? type ID ) )
            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:87:5: ^( PAR ( OUT )? type ID )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            PAR30=(CtrlTree)match(input,PAR,FOLLOW_PAR_in_par_decl350); 


            if ( _first_0==null ) _first_0 = PAR30;
            match(input, Token.DOWN, null); 
            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:87:11: ( OUT )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==OUT) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:87:11: OUT
                    {
                    _last = (CtrlTree)input.LT(1);
                    OUT31=(CtrlTree)match(input,OUT,FOLLOW_OUT_in_par_decl352); 
                     
                    if ( _first_1==null ) _first_1 = OUT31;


                    retval.tree = _first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;

            }


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_type_in_par_decl355);
            type32=type();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = type32.tree;


            _last = (CtrlTree)input.LT(1);
            ID33=(CtrlTree)match(input,ID,FOLLOW_ID_in_par_decl357); 
             
            if ( _first_1==null ) _first_1 = ID33;


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


             helper.declarePar(ID33, (type32!=null?((CtrlTree)type32.tree):null), OUT31); 

            retval.tree = _first_0;
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
        CtrlTree tree;
        @Override
        public Object getTree() { return tree; }
    };


    // $ANTLR start "block"
    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:91:1: block : ^( BLOCK ( stat )* ) ;
    public final CtrlChecker.block_return block() throws RecognitionException {
        CtrlChecker.block_return retval = new CtrlChecker.block_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree BLOCK34=null;
        CtrlChecker.stat_return stat35 =null;


        CtrlTree BLOCK34_tree=null;

        try {
            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:92:3: ( ^( BLOCK ( stat )* ) )
            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:92:5: ^( BLOCK ( stat )* )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            BLOCK34=(CtrlTree)match(input,BLOCK,FOLLOW_BLOCK_in_block381); 


            if ( _first_0==null ) _first_0 = BLOCK34;
             helper.openScope(); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:94:8: ( stat )*
                loop8:
                do {
                    int alt8=2;
                    int LA8_0 = input.LA(1);

                    if ( (LA8_0==ALAP||LA8_0==ATOM||(LA8_0 >= BECOMES && LA8_0 <= BLOCK)||(LA8_0 >= CALL && LA8_0 <= CHOICE)||LA8_0==IF||LA8_0==SEMI||LA8_0==STAR||(LA8_0 >= TRUE && LA8_0 <= UNTIL)||LA8_0==WHILE) ) {
                        alt8=1;
                    }


                    switch (alt8) {
                	case 1 :
                	    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:94:8: stat
                	    {
                	    _last = (CtrlTree)input.LT(1);
                	    pushFollow(FOLLOW_stat_in_block399);
                	    stat35=stat();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = stat35.tree;


                	    retval.tree = _first_0;
                	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                	        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                	    }
                	    break;

                	default :
                	    break loop8;
                    }
                } while (true);


                 helper.closeScope(); 

                match(input, Token.UP, null); 
            }
            _last = _save_last_1;
            }


            retval.tree = _first_0;
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
        @Override
        public Object getTree() { return tree; }
    };


    // $ANTLR start "stat"
    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:99:1: stat : ( block | ^( SEMI var_decl ) | ^( SEMI stat ) | ^( ALAP stat ) | ^( ATOM stat ) | ^( WHILE stat stat ) | ^( UNTIL stat stat ) | ^( TRY stat ( stat )? ) | ^( IF stat stat ( stat )? ) | ^( CHOICE stat ( stat )* ) | ^( STAR stat ) | call | assign | TRUE );
    public final CtrlChecker.stat_return stat() throws RecognitionException {
        CtrlChecker.stat_return retval = new CtrlChecker.stat_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree SEMI37=null;
        CtrlTree SEMI39=null;
        CtrlTree ALAP41=null;
        CtrlTree ATOM43=null;
        CtrlTree WHILE45=null;
        CtrlTree UNTIL48=null;
        CtrlTree TRY51=null;
        CtrlTree IF54=null;
        CtrlTree CHOICE58=null;
        CtrlTree STAR61=null;
        CtrlTree TRUE65=null;
        CtrlChecker.block_return block36 =null;

        CtrlChecker.var_decl_return var_decl38 =null;

        CtrlChecker.stat_return stat40 =null;

        CtrlChecker.stat_return stat42 =null;

        CtrlChecker.stat_return stat44 =null;

        CtrlChecker.stat_return stat46 =null;

        CtrlChecker.stat_return stat47 =null;

        CtrlChecker.stat_return stat49 =null;

        CtrlChecker.stat_return stat50 =null;

        CtrlChecker.stat_return stat52 =null;

        CtrlChecker.stat_return stat53 =null;

        CtrlChecker.stat_return stat55 =null;

        CtrlChecker.stat_return stat56 =null;

        CtrlChecker.stat_return stat57 =null;

        CtrlChecker.stat_return stat59 =null;

        CtrlChecker.stat_return stat60 =null;

        CtrlChecker.stat_return stat62 =null;

        CtrlChecker.call_return call63 =null;

        CtrlChecker.assign_return assign64 =null;


        CtrlTree SEMI37_tree=null;
        CtrlTree SEMI39_tree=null;
        CtrlTree ALAP41_tree=null;
        CtrlTree ATOM43_tree=null;
        CtrlTree WHILE45_tree=null;
        CtrlTree UNTIL48_tree=null;
        CtrlTree TRY51_tree=null;
        CtrlTree IF54_tree=null;
        CtrlTree CHOICE58_tree=null;
        CtrlTree STAR61_tree=null;
        CtrlTree TRUE65_tree=null;

        try {
            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:100:3: ( block | ^( SEMI var_decl ) | ^( SEMI stat ) | ^( ALAP stat ) | ^( ATOM stat ) | ^( WHILE stat stat ) | ^( UNTIL stat stat ) | ^( TRY stat ( stat )? ) | ^( IF stat stat ( stat )? ) | ^( CHOICE stat ( stat )* ) | ^( STAR stat ) | call | assign | TRUE )
            int alt12=14;
            switch ( input.LA(1) ) {
            case BLOCK:
                {
                alt12=1;
                }
                break;
            case SEMI:
                {
                int LA12_2 = input.LA(2);

                if ( (LA12_2==DOWN) ) {
                    int LA12_14 = input.LA(3);

                    if ( (LA12_14==VAR) ) {
                        alt12=2;
                    }
                    else if ( (LA12_14==ALAP||LA12_14==ATOM||(LA12_14 >= BECOMES && LA12_14 <= BLOCK)||(LA12_14 >= CALL && LA12_14 <= CHOICE)||LA12_14==IF||LA12_14==SEMI||LA12_14==STAR||(LA12_14 >= TRUE && LA12_14 <= UNTIL)||LA12_14==WHILE) ) {
                        alt12=3;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 12, 14, input);

                        throw nvae;

                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 12, 2, input);

                    throw nvae;

                }
                }
                break;
            case ALAP:
                {
                alt12=4;
                }
                break;
            case ATOM:
                {
                alt12=5;
                }
                break;
            case WHILE:
                {
                alt12=6;
                }
                break;
            case UNTIL:
                {
                alt12=7;
                }
                break;
            case TRY:
                {
                alt12=8;
                }
                break;
            case IF:
                {
                alt12=9;
                }
                break;
            case CHOICE:
                {
                alt12=10;
                }
                break;
            case STAR:
                {
                alt12=11;
                }
                break;
            case CALL:
                {
                alt12=12;
                }
                break;
            case BECOMES:
                {
                alt12=13;
                }
                break;
            case TRUE:
                {
                alt12=14;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;

            }

            switch (alt12) {
                case 1 :
                    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:100:5: block
                    {
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_block_in_stat429);
                    block36=block();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = block36.tree;


                    retval.tree = _first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:101:5: ^( SEMI var_decl )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    SEMI37=(CtrlTree)match(input,SEMI,FOLLOW_SEMI_in_stat436); 


                    if ( _first_0==null ) _first_0 = SEMI37;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_var_decl_in_stat438);
                    var_decl38=var_decl();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = var_decl38.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = _first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 3 :
                    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:102:5: ^( SEMI stat )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    SEMI39=(CtrlTree)match(input,SEMI,FOLLOW_SEMI_in_stat446); 


                    if ( _first_0==null ) _first_0 = SEMI39;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat448);
                    stat40=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat40.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = _first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 4 :
                    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:103:5: ^( ALAP stat )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    ALAP41=(CtrlTree)match(input,ALAP,FOLLOW_ALAP_in_stat456); 


                    if ( _first_0==null ) _first_0 = ALAP41;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat458);
                    stat42=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat42.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = _first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 5 :
                    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:104:5: ^( ATOM stat )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    ATOM43=(CtrlTree)match(input,ATOM,FOLLOW_ATOM_in_stat466); 


                    if ( _first_0==null ) _first_0 = ATOM43;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat468);
                    stat44=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat44.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = _first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 6 :
                    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:105:5: ^( WHILE stat stat )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    WHILE45=(CtrlTree)match(input,WHILE,FOLLOW_WHILE_in_stat477); 


                    if ( _first_0==null ) _first_0 = WHILE45;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat486);
                    stat46=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat46.tree;


                     helper.startBranch(); 

                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat504);
                    stat47=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat47.tree;


                     helper.nextBranch(); 

                     helper.endBranch(); 

                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = _first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 7 :
                    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:114:5: ^( UNTIL stat stat )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    UNTIL48=(CtrlTree)match(input,UNTIL,FOLLOW_UNTIL_in_stat553); 


                    if ( _first_0==null ) _first_0 = UNTIL48;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat562);
                    stat49=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat49.tree;


                     helper.startBranch(); 

                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat580);
                    stat50=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat50.tree;


                     helper.nextBranch(); 

                     helper.endBranch(); 

                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = _first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 8 :
                    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:123:5: ^( TRY stat ( stat )? )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    TRY51=(CtrlTree)match(input,TRY,FOLLOW_TRY_in_stat629); 


                    if ( _first_0==null ) _first_0 = TRY51;
                     helper.startBranch(); 

                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat647);
                    stat52=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat52.tree;


                    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:126:8: ( stat )?
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0==ALAP||LA9_0==ATOM||(LA9_0 >= BECOMES && LA9_0 <= BLOCK)||(LA9_0 >= CALL && LA9_0 <= CHOICE)||LA9_0==IF||LA9_0==SEMI||LA9_0==STAR||(LA9_0 >= TRUE && LA9_0 <= UNTIL)||LA9_0==WHILE) ) {
                        alt9=1;
                    }
                    switch (alt9) {
                        case 1 :
                            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:126:10: stat
                            {
                             helper.nextBranch(); 

                            _last = (CtrlTree)input.LT(1);
                            pushFollow(FOLLOW_stat_in_stat669);
                            stat53=stat();

                            state._fsp--;

                             
                            if ( _first_1==null ) _first_1 = stat53.tree;


                            retval.tree = _first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                            }
                            break;

                    }


                     helper.endBranch(); 

                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = _first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 9 :
                    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:131:5: ^( IF stat stat ( stat )? )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    IF54=(CtrlTree)match(input,IF,FOLLOW_IF_in_stat703); 


                    if ( _first_0==null ) _first_0 = IF54;
                     helper.startBranch(); 

                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat722);
                    stat55=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat55.tree;


                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat732);
                    stat56=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat56.tree;


                     helper.nextBranch(); 

                    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:136:8: ( stat )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0==ALAP||LA10_0==ATOM||(LA10_0 >= BECOMES && LA10_0 <= BLOCK)||(LA10_0 >= CALL && LA10_0 <= CHOICE)||LA10_0==IF||LA10_0==SEMI||LA10_0==STAR||(LA10_0 >= TRUE && LA10_0 <= UNTIL)||LA10_0==WHILE) ) {
                        alt10=1;
                    }
                    switch (alt10) {
                        case 1 :
                            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:136:8: stat
                            {
                            _last = (CtrlTree)input.LT(1);
                            pushFollow(FOLLOW_stat_in_stat750);
                            stat57=stat();

                            state._fsp--;

                             
                            if ( _first_1==null ) _first_1 = stat57.tree;


                            retval.tree = _first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                            }
                            break;

                    }


                     helper.endBranch(); 

                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = _first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 10 :
                    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:139:5: ^( CHOICE stat ( stat )* )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    CHOICE58=(CtrlTree)match(input,CHOICE,FOLLOW_CHOICE_in_stat775); 


                    if ( _first_0==null ) _first_0 = CHOICE58;
                     helper.startBranch(); 

                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat793);
                    stat59=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat59.tree;


                    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:142:8: ( stat )*
                    loop11:
                    do {
                        int alt11=2;
                        int LA11_0 = input.LA(1);

                        if ( (LA11_0==ALAP||LA11_0==ATOM||(LA11_0 >= BECOMES && LA11_0 <= BLOCK)||(LA11_0 >= CALL && LA11_0 <= CHOICE)||LA11_0==IF||LA11_0==SEMI||LA11_0==STAR||(LA11_0 >= TRUE && LA11_0 <= UNTIL)||LA11_0==WHILE) ) {
                            alt11=1;
                        }


                        switch (alt11) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:142:10: stat
                    	    {
                    	     helper.nextBranch(); 

                    	    _last = (CtrlTree)input.LT(1);
                    	    pushFollow(FOLLOW_stat_in_stat816);
                    	    stat60=stat();

                    	    state._fsp--;

                    	     
                    	    if ( _first_1==null ) _first_1 = stat60.tree;


                    	    retval.tree = _first_0;
                    	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                    	        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    	    }
                    	    break;

                    	default :
                    	    break loop11;
                        }
                    } while (true);


                     helper.endBranch(); 

                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = _first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 11 :
                    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:147:5: ^( STAR stat )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    STAR61=(CtrlTree)match(input,STAR,FOLLOW_STAR_in_stat850); 


                    if ( _first_0==null ) _first_0 = STAR61;
                     helper.startBranch(); 

                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat868);
                    stat62=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat62.tree;


                     helper.endBranch(); 

                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = _first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 12 :
                    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:152:5: call
                    {
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_call_in_stat890);
                    call63=call();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = call63.tree;


                    retval.tree = _first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 13 :
                    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:153:5: assign
                    {
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_assign_in_stat896);
                    assign64=assign();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = assign64.tree;


                    retval.tree = _first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 14 :
                    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:154:5: TRUE
                    {
                    _last = (CtrlTree)input.LT(1);
                    TRUE65=(CtrlTree)match(input,TRUE,FOLLOW_TRUE_in_stat902); 
                     
                    if ( _first_0==null ) _first_0 = TRUE65;


                    retval.tree = _first_0;
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


    public static class call_return extends TreeRuleReturnScope {
        CtrlTree tree;
        @Override
        public Object getTree() { return tree; }
    };


    // $ANTLR start "call"
    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:157:1: call : ^( CALL qual_id ( arg_list )? ) ;
    public final CtrlChecker.call_return call() throws RecognitionException {
        CtrlChecker.call_return retval = new CtrlChecker.call_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree CALL66=null;
        CtrlChecker.qual_id_return qual_id67 =null;

        CtrlChecker.arg_list_return arg_list68 =null;


        CtrlTree CALL66_tree=null;

        try {
            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:159:3: ( ^( CALL qual_id ( arg_list )? ) )
            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:159:5: ^( CALL qual_id ( arg_list )? )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            CALL66=(CtrlTree)match(input,CALL,FOLLOW_CALL_in_call920); 


            if ( _first_0==null ) _first_0 = CALL66;
            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_qual_id_in_call922);
            qual_id67=qual_id();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = qual_id67.tree;


            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:159:20: ( arg_list )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==ARGS) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:159:20: arg_list
                    {
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_arg_list_in_call924);
                    arg_list68=arg_list();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = arg_list68.tree;


                    retval.tree = _first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;

            }


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = _first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

            }

             helper.checkGroupCall((retval.tree)); 
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
    // $ANTLR end "call"


    public static class assign_return extends TreeRuleReturnScope {
        CtrlTree tree;
        @Override
        public Object getTree() { return tree; }
    };


    // $ANTLR start "assign"
    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:162:1: assign : ^( BECOMES ( var_decl | arg_list ) ^( CALL qual_id ( arg_list )? ) ) ;
    public final CtrlChecker.assign_return assign() throws RecognitionException {
        CtrlChecker.assign_return retval = new CtrlChecker.assign_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree BECOMES69=null;
        CtrlTree CALL72=null;
        CtrlChecker.var_decl_return var_decl70 =null;

        CtrlChecker.arg_list_return arg_list71 =null;

        CtrlChecker.qual_id_return qual_id73 =null;

        CtrlChecker.arg_list_return arg_list74 =null;


        CtrlTree BECOMES69_tree=null;
        CtrlTree CALL72_tree=null;

        try {
            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:164:3: ( ^( BECOMES ( var_decl | arg_list ) ^( CALL qual_id ( arg_list )? ) ) )
            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:164:5: ^( BECOMES ( var_decl | arg_list ) ^( CALL qual_id ( arg_list )? ) )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            BECOMES69=(CtrlTree)match(input,BECOMES,FOLLOW_BECOMES_in_assign944); 


            if ( _first_0==null ) _first_0 = BECOMES69;
            match(input, Token.DOWN, null); 
            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:164:15: ( var_decl | arg_list )
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==VAR) ) {
                alt14=1;
            }
            else if ( (LA14_0==ARGS) ) {
                alt14=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 14, 0, input);

                throw nvae;

            }
            switch (alt14) {
                case 1 :
                    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:164:16: var_decl
                    {
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_var_decl_in_assign947);
                    var_decl70=var_decl();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = var_decl70.tree;


                    retval.tree = _first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:164:27: arg_list
                    {
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_arg_list_in_assign951);
                    arg_list71=arg_list();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = arg_list71.tree;


                    retval.tree = _first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;

            }


            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_2 = _last;
            CtrlTree _first_2 = null;
            _last = (CtrlTree)input.LT(1);
            CALL72=(CtrlTree)match(input,CALL,FOLLOW_CALL_in_assign955); 


            if ( _first_1==null ) _first_1 = CALL72;
            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_qual_id_in_assign957);
            qual_id73=qual_id();

            state._fsp--;

             
            if ( _first_2==null ) _first_2 = qual_id73.tree;


            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:164:52: ( arg_list )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==ARGS) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:164:52: arg_list
                    {
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_arg_list_in_assign959);
                    arg_list74=arg_list();

                    state._fsp--;

                     
                    if ( _first_2==null ) _first_2 = arg_list74.tree;


                    retval.tree = _first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;

            }


            match(input, Token.UP, null); 
            _last = _save_last_2;
            }


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = _first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

            }

             helper.checkAssign((retval.tree)); 
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
    // $ANTLR end "assign"


    public static class var_decl_return extends TreeRuleReturnScope {
        CtrlTree tree;
        @Override
        public Object getTree() { return tree; }
    };


    // $ANTLR start "var_decl"
    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:167:1: var_decl : ^( VAR type ( ID )+ ) ;
    public final CtrlChecker.var_decl_return var_decl() throws RecognitionException {
        CtrlChecker.var_decl_return retval = new CtrlChecker.var_decl_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree VAR75=null;
        CtrlTree ID77=null;
        CtrlChecker.type_return type76 =null;


        CtrlTree VAR75_tree=null;
        CtrlTree ID77_tree=null;

        try {
            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:168:2: ( ^( VAR type ( ID )+ ) )
            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:168:4: ^( VAR type ( ID )+ )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            VAR75=(CtrlTree)match(input,VAR,FOLLOW_VAR_in_var_decl976); 


            if ( _first_0==null ) _first_0 = VAR75;
            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_type_in_var_decl978);
            type76=type();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = type76.tree;


            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:169:7: ( ID )+
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
            	    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:169:9: ID
            	    {
            	    _last = (CtrlTree)input.LT(1);
            	    ID77=(CtrlTree)match(input,ID,FOLLOW_ID_in_var_decl988); 
            	     
            	    if ( _first_1==null ) _first_1 = ID77;


            	     helper.declareVar(ID77, (type76!=null?((CtrlTree)type76.tree):null)); 

            	    retval.tree = _first_0;
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


            retval.tree = _first_0;
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


    public static class qual_id_return extends TreeRuleReturnScope {
        CtrlTree tree;
        @Override
        public Object getTree() { return tree; }
    };


    // $ANTLR start "qual_id"
    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:175:1: qual_id : ^( ( ID | ANY | OTHER ) ID ) ;
    public final CtrlChecker.qual_id_return qual_id() throws RecognitionException {
        CtrlChecker.qual_id_return retval = new CtrlChecker.qual_id_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree set78=null;
        CtrlTree ID79=null;

        CtrlTree set78_tree=null;
        CtrlTree ID79_tree=null;

        try {
            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:176:3: ( ^( ( ID | ANY | OTHER ) ID ) )
            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:176:5: ^( ( ID | ANY | OTHER ) ID )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            set78=(CtrlTree)input.LT(1);

            if ( input.LA(1)==ANY||input.LA(1)==ID||input.LA(1)==OTHER ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            if ( _first_0==null ) _first_0 = set78;
            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            ID79=(CtrlTree)match(input,ID,FOLLOW_ID_in_qual_id1036); 
             
            if ( _first_1==null ) _first_1 = ID79;


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = _first_0;
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
    // $ANTLR end "qual_id"


    public static class type_return extends TreeRuleReturnScope {
        CtrlTree tree;
        @Override
        public Object getTree() { return tree; }
    };


    // $ANTLR start "type"
    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:181:1: type : ( NODE -> NODE | BOOL -> BOOL | STRING -> STRING | INT -> INT | REAL -> REAL );
    public final CtrlChecker.type_return type() throws RecognitionException {
        CtrlChecker.type_return retval = new CtrlChecker.type_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree NODE80=null;
        CtrlTree BOOL81=null;
        CtrlTree STRING82=null;
        CtrlTree INT83=null;
        CtrlTree REAL84=null;

        CtrlTree NODE80_tree=null;
        CtrlTree BOOL81_tree=null;
        CtrlTree STRING82_tree=null;
        CtrlTree INT83_tree=null;
        CtrlTree REAL84_tree=null;
        RewriteRuleNodeStream stream_BOOL=new RewriteRuleNodeStream(adaptor,"token BOOL");
        RewriteRuleNodeStream stream_NODE=new RewriteRuleNodeStream(adaptor,"token NODE");
        RewriteRuleNodeStream stream_REAL=new RewriteRuleNodeStream(adaptor,"token REAL");
        RewriteRuleNodeStream stream_STRING=new RewriteRuleNodeStream(adaptor,"token STRING");
        RewriteRuleNodeStream stream_INT=new RewriteRuleNodeStream(adaptor,"token INT");

        try {
            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:184:3: ( NODE -> NODE | BOOL -> BOOL | STRING -> STRING | INT -> INT | REAL -> REAL )
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
                    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:184:5: NODE
                    {
                    _last = (CtrlTree)input.LT(1);
                    NODE80=(CtrlTree)match(input,NODE,FOLLOW_NODE_in_type1062);  
                    stream_NODE.add(NODE80);


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
                    // 184:10: -> NODE
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
                    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:185:5: BOOL
                    {
                    _last = (CtrlTree)input.LT(1);
                    BOOL81=(CtrlTree)match(input,BOOL,FOLLOW_BOOL_in_type1072);  
                    stream_BOOL.add(BOOL81);


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
                    // 185:10: -> BOOL
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
                    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:186:5: STRING
                    {
                    _last = (CtrlTree)input.LT(1);
                    STRING82=(CtrlTree)match(input,STRING,FOLLOW_STRING_in_type1082);  
                    stream_STRING.add(STRING82);


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
                    // 186:12: -> STRING
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
                    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:187:5: INT
                    {
                    _last = (CtrlTree)input.LT(1);
                    INT83=(CtrlTree)match(input,INT,FOLLOW_INT_in_type1092);  
                    stream_INT.add(INT83);


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
                    // 187:9: -> INT
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
                    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:188:5: REAL
                    {
                    _last = (CtrlTree)input.LT(1);
                    REAL84=(CtrlTree)match(input,REAL,FOLLOW_REAL_in_type1102);  
                    stream_REAL.add(REAL84);


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
                    // 188:10: -> REAL
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


    public static class arg_list_return extends TreeRuleReturnScope {
        CtrlTree tree;
        @Override
        public Object getTree() { return tree; }
    };


    // $ANTLR start "arg_list"
    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:191:1: arg_list : ^( ARGS ( arg )* RPAR ) ;
    public final CtrlChecker.arg_list_return arg_list() throws RecognitionException {
        CtrlChecker.arg_list_return retval = new CtrlChecker.arg_list_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree ARGS85=null;
        CtrlTree RPAR87=null;
        CtrlChecker.arg_return arg86 =null;


        CtrlTree ARGS85_tree=null;
        CtrlTree RPAR87_tree=null;

        try {
            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:192:3: ( ^( ARGS ( arg )* RPAR ) )
            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:192:5: ^( ARGS ( arg )* RPAR )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            ARGS85=(CtrlTree)match(input,ARGS,FOLLOW_ARGS_in_arg_list1120); 


            if ( _first_0==null ) _first_0 = ARGS85;
            match(input, Token.DOWN, null); 
            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:192:12: ( arg )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==ARG) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:192:12: arg
            	    {
            	    _last = (CtrlTree)input.LT(1);
            	    pushFollow(FOLLOW_arg_in_arg_list1122);
            	    arg86=arg();

            	    state._fsp--;

            	     
            	    if ( _first_1==null ) _first_1 = arg86.tree;


            	    retval.tree = _first_0;
            	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
            	        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

            	    }
            	    break;

            	default :
            	    break loop18;
                }
            } while (true);


            _last = (CtrlTree)input.LT(1);
            RPAR87=(CtrlTree)match(input,RPAR,FOLLOW_RPAR_in_arg_list1125); 
             
            if ( _first_1==null ) _first_1 = RPAR87;


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = _first_0;
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
    // $ANTLR end "arg_list"


    public static class arg_return extends TreeRuleReturnScope {
        CtrlTree tree;
        @Override
        public Object getTree() { return tree; }
    };


    // $ANTLR start "arg"
    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:195:1: arg : ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) ) ;
    public final CtrlChecker.arg_return arg() throws RecognitionException {
        CtrlChecker.arg_return retval = new CtrlChecker.arg_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree ARG88=null;
        CtrlTree OUT89=null;
        CtrlTree ID90=null;
        CtrlTree DONT_CARE91=null;
        CtrlChecker.literal_return literal92 =null;


        CtrlTree ARG88_tree=null;
        CtrlTree OUT89_tree=null;
        CtrlTree ID90_tree=null;
        CtrlTree DONT_CARE91_tree=null;

        try {
            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:196:2: ( ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) ) )
            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:196:4: ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            ARG88=(CtrlTree)match(input,ARG,FOLLOW_ARG_in_arg1140); 


            if ( _first_0==null ) _first_0 = ARG88;
            match(input, Token.DOWN, null); 
            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:197:7: ( ( OUT )? ID | DONT_CARE | literal )
            int alt20=3;
            switch ( input.LA(1) ) {
            case ID:
            case OUT:
                {
                alt20=1;
                }
                break;
            case DONT_CARE:
                {
                alt20=2;
                }
                break;
            case FALSE:
            case INT_LIT:
            case REAL_LIT:
            case STRING_LIT:
            case TRUE:
                {
                alt20=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 20, 0, input);

                throw nvae;

            }

            switch (alt20) {
                case 1 :
                    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:197:9: ( OUT )? ID
                    {
                    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:197:9: ( OUT )?
                    int alt19=2;
                    int LA19_0 = input.LA(1);

                    if ( (LA19_0==OUT) ) {
                        alt19=1;
                    }
                    switch (alt19) {
                        case 1 :
                            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:197:9: OUT
                            {
                            _last = (CtrlTree)input.LT(1);
                            OUT89=(CtrlTree)match(input,OUT,FOLLOW_OUT_in_arg1151); 
                             
                            if ( _first_1==null ) _first_1 = OUT89;


                            retval.tree = _first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                            }
                            break;

                    }


                    _last = (CtrlTree)input.LT(1);
                    ID90=(CtrlTree)match(input,ID,FOLLOW_ID_in_arg1154); 
                     
                    if ( _first_1==null ) _first_1 = ID90;


                     helper.checkVarArg(ARG88); 

                    retval.tree = _first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:198:9: DONT_CARE
                    {
                    _last = (CtrlTree)input.LT(1);
                    DONT_CARE91=(CtrlTree)match(input,DONT_CARE,FOLLOW_DONT_CARE_in_arg1166); 
                     
                    if ( _first_1==null ) _first_1 = DONT_CARE91;


                     helper.checkDontCareArg(ARG88); 

                    retval.tree = _first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 3 :
                    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:199:9: literal
                    {
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_literal_in_arg1178);
                    literal92=literal();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = literal92.tree;


                     helper.checkConstArg(ARG88); 

                    retval.tree = _first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;

            }


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = _first_0;
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
        @Override
        public Object getTree() { return tree; }
    };


    // $ANTLR start "literal"
    // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:204:1: literal : ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT );
    public final CtrlChecker.literal_return literal() throws RecognitionException {
        CtrlChecker.literal_return retval = new CtrlChecker.literal_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree set93=null;

        CtrlTree set93_tree=null;

        try {
            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:205:3: ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT )
            // E:\\Eclipse\\groove-formula\\src\\groove\\control\\parse\\CtrlChecker.g:
            {
            _last = (CtrlTree)input.LT(1);
            set93=(CtrlTree)input.LT(1);

            if ( input.LA(1)==FALSE||input.LA(1)==INT_LIT||input.LA(1)==REAL_LIT||(input.LA(1) >= STRING_LIT && input.LA(1) <= TRUE) ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }

            retval.tree = _first_0;
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
    public static final BitSet FOLLOW_package_decl_in_program63 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_imports_in_program65 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_functions_in_program67 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_recipes_in_program69 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_block_in_program71 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PACKAGE_in_package_decl88 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_qual_id_in_package_decl90 = new BitSet(new long[]{0x4000000000000000L});
    public static final BitSet FOLLOW_SEMI_in_package_decl92 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IMPORTS_in_imports122 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_import_decl_in_imports124 = new BitSet(new long[]{0x0000000100000008L});
    public static final BitSet FOLLOW_IMPORT_in_import_decl141 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_qual_id_in_import_decl143 = new BitSet(new long[]{0x4000000000000000L});
    public static final BitSet FOLLOW_SEMI_in_import_decl145 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RECIPES_in_recipes175 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_recipe_in_recipes177 = new BitSet(new long[]{0x0800000000000008L});
    public static final BitSet FOLLOW_RECIPE_in_recipe194 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_recipe213 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_PARS_in_recipe216 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_par_decl_in_recipe218 = new BitSet(new long[]{0x0002000000000008L});
    public static final BitSet FOLLOW_INT_LIT_in_recipe222 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_block_in_recipe232 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FUNCTIONS_in_functions264 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_function_in_functions266 = new BitSet(new long[]{0x0000000010000008L});
    public static final BitSet FOLLOW_FUNCTION_in_function283 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_function301 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_PARS_in_function304 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_par_decl_in_function306 = new BitSet(new long[]{0x0002000000000008L});
    public static final BitSet FOLLOW_block_in_function317 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PAR_in_par_decl350 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_OUT_in_par_decl352 = new BitSet(new long[]{0x0200040400004000L,0x0000000000000004L});
    public static final BitSet FOLLOW_type_in_par_decl355 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_ID_in_par_decl357 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BLOCK_in_block381 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_block399 = new BitSet(new long[]{0x4000000080063418L,0x0000000000000172L});
    public static final BitSet FOLLOW_block_in_stat429 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMI_in_stat436 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_var_decl_in_stat438 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SEMI_in_stat446 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat448 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ALAP_in_stat456 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat458 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ATOM_in_stat466 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat468 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WHILE_in_stat477 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat486 = new BitSet(new long[]{0x4000000080063410L,0x0000000000000172L});
    public static final BitSet FOLLOW_stat_in_stat504 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_UNTIL_in_stat553 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat562 = new BitSet(new long[]{0x4000000080063410L,0x0000000000000172L});
    public static final BitSet FOLLOW_stat_in_stat580 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TRY_in_stat629 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat647 = new BitSet(new long[]{0x4000000080063418L,0x0000000000000172L});
    public static final BitSet FOLLOW_stat_in_stat669 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IF_in_stat703 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat722 = new BitSet(new long[]{0x4000000080063410L,0x0000000000000172L});
    public static final BitSet FOLLOW_stat_in_stat732 = new BitSet(new long[]{0x4000000080063418L,0x0000000000000172L});
    public static final BitSet FOLLOW_stat_in_stat750 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CHOICE_in_stat775 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat793 = new BitSet(new long[]{0x4000000080063418L,0x0000000000000172L});
    public static final BitSet FOLLOW_stat_in_stat816 = new BitSet(new long[]{0x4000000080063418L,0x0000000000000172L});
    public static final BitSet FOLLOW_STAR_in_stat850 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat868 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_call_in_stat890 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_assign_in_stat896 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_stat902 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CALL_in_call920 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_qual_id_in_call922 = new BitSet(new long[]{0x0000000000000108L});
    public static final BitSet FOLLOW_arg_list_in_call924 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BECOMES_in_assign944 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_var_decl_in_assign947 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_arg_list_in_assign951 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_CALL_in_assign955 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_qual_id_in_assign957 = new BitSet(new long[]{0x0000000000000108L});
    public static final BitSet FOLLOW_arg_list_in_assign959 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VAR_in_var_decl976 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_type_in_var_decl978 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_ID_in_var_decl988 = new BitSet(new long[]{0x0000000040000008L});
    public static final BitSet FOLLOW_set_in_qual_id1028 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_qual_id1036 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NODE_in_type1062 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_type1072 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_type1082 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_type1092 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REAL_in_type1102 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ARGS_in_arg_list1120 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_arg_in_arg_list1122 = new BitSet(new long[]{0x2000000000000080L});
    public static final BitSet FOLLOW_RPAR_in_arg_list1125 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ARG_in_arg1140 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_OUT_in_arg1151 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_ID_in_arg1154 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DONT_CARE_in_arg1166 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_literal_in_arg1178 = new BitSet(new long[]{0x0000000000000008L});

}