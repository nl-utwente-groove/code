// $ANTLR 3.4 E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g 2012-10-24 22:07:01

package groove.control.parse;
import groove.control.*;
import groove.control.CtrlCall.Kind;
import groove.trans.Rule;
import groove.algebra.AlgebraFamily;
import groove.view.FormatErrorSet;
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
    public String getGrammarFileName() { return "E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g"; }


        /** Helper class to convert AST trees to namespace. */
        private CtrlHelper helper;
        
        public void displayRecognitionError(String[] tokenNames,
                RecognitionException e) {
            String hdr = getErrorHeader(e);
            String msg = getErrorMessage(e, tokenNames);
            this.helper.addError(hdr + " " + msg, e.line, e.charPositionInLine);
        }

        public FormatErrorSet getErrors() {
            return this.helper.getErrors();
        }

        /**
         * Runs the lexer and parser on a given input character stream,
         * with a (presumably empty) namespace.
         * @return the resulting syntax tree
         */
        public CtrlTree run(CtrlTree tree, Namespace namespace, AlgebraFamily family) throws RecognitionException {
            this.helper = new CtrlHelper(this, namespace, family);
            CtrlTreeAdaptor treeAdaptor = new CtrlTreeAdaptor();
            setTreeAdaptor(treeAdaptor);
            setTreeNodeStream(treeAdaptor.createTreeNodeStream(tree));
            return (CtrlTree) program().getTree();
        }


    public static class program_return extends TreeRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "program"
    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:52:1: program : ^( PROGRAM package_decl ( import_decl )* recipes functions block ) ;
    public final CtrlChecker.program_return program() throws RecognitionException {
        CtrlChecker.program_return retval = new CtrlChecker.program_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree PROGRAM1=null;
        CtrlChecker.package_decl_return package_decl2 =null;

        CtrlChecker.import_decl_return import_decl3 =null;

        CtrlChecker.recipes_return recipes4 =null;

        CtrlChecker.functions_return functions5 =null;

        CtrlChecker.block_return block6 =null;


        CtrlTree PROGRAM1_tree=null;

        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:53:3: ( ^( PROGRAM package_decl ( import_decl )* recipes functions block ) )
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:53:5: ^( PROGRAM package_decl ( import_decl )* recipes functions block )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            PROGRAM1=(CtrlTree)match(input,PROGRAM,FOLLOW_PROGRAM_in_program56); 


            if ( _first_0==null ) _first_0 = PROGRAM1;
            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_package_decl_in_program58);
            package_decl2=package_decl();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = package_decl2.tree;


            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:53:28: ( import_decl )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==IMPORT) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:53:28: import_decl
            	    {
            	    _last = (CtrlTree)input.LT(1);
            	    pushFollow(FOLLOW_import_decl_in_program60);
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
            pushFollow(FOLLOW_recipes_in_program63);
            recipes4=recipes();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = recipes4.tree;


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_functions_in_program65);
            functions5=functions();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = functions5.tree;


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_block_in_program67);
            block6=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block6.tree;


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


             if ((block6!=null?((CtrlTree)block6.tree):null).getChildCount() == 0) {
                      helper.checkAny(PROGRAM1);
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
    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:60:1: package_decl : ^( PACKAGE ID ) ;
    public final CtrlChecker.package_decl_return package_decl() throws RecognitionException {
        CtrlChecker.package_decl_return retval = new CtrlChecker.package_decl_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree PACKAGE7=null;
        CtrlTree ID8=null;

        CtrlTree PACKAGE7_tree=null;
        CtrlTree ID8_tree=null;

        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:61:3: ( ^( PACKAGE ID ) )
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:61:5: ^( PACKAGE ID )
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


    public static class import_decl_return extends TreeRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "import_decl"
    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:66:1: import_decl : ^( IMPORT ID ) ;
    public final CtrlChecker.import_decl_return import_decl() throws RecognitionException {
        CtrlChecker.import_decl_return retval = new CtrlChecker.import_decl_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree IMPORT9=null;
        CtrlTree ID10=null;

        CtrlTree IMPORT9_tree=null;
        CtrlTree ID10_tree=null;

        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:67:3: ( ^( IMPORT ID ) )
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:67:5: ^( IMPORT ID )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            IMPORT9=(CtrlTree)match(input,IMPORT,FOLLOW_IMPORT_in_import_decl125); 


            if ( _first_0==null ) _first_0 = IMPORT9;
            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            ID10=(CtrlTree)match(input,ID,FOLLOW_ID_in_import_decl127); 
             
            if ( _first_1==null ) _first_1 = ID10;


             helper.checkImport(ID10); 

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
    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:72:1: recipes : ^( RECIPES ( recipe )* ) ;
    public final CtrlChecker.recipes_return recipes() throws RecognitionException {
        CtrlChecker.recipes_return retval = new CtrlChecker.recipes_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree RECIPES11=null;
        CtrlChecker.recipe_return recipe12 =null;


        CtrlTree RECIPES11_tree=null;

        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:73:3: ( ^( RECIPES ( recipe )* ) )
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:73:5: ^( RECIPES ( recipe )* )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            RECIPES11=(CtrlTree)match(input,RECIPES,FOLLOW_RECIPES_in_recipes157); 


            if ( _first_0==null ) _first_0 = RECIPES11;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:73:15: ( recipe )*
                loop2:
                do {
                    int alt2=2;
                    int LA2_0 = input.LA(1);

                    if ( (LA2_0==RECIPE) ) {
                        alt2=1;
                    }


                    switch (alt2) {
                	case 1 :
                	    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:73:15: recipe
                	    {
                	    _last = (CtrlTree)input.LT(1);
                	    pushFollow(FOLLOW_recipe_in_recipes159);
                	    recipe12=recipe();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = recipe12.tree;


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
    // $ANTLR end "recipes"


    public static class recipe_return extends TreeRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "recipe"
    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:76:1: recipe : ^( RECIPE ID ( INT_LIT )? block ) ;
    public final CtrlChecker.recipe_return recipe() throws RecognitionException {
        CtrlChecker.recipe_return retval = new CtrlChecker.recipe_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree RECIPE13=null;
        CtrlTree ID14=null;
        CtrlTree INT_LIT15=null;
        CtrlChecker.block_return block16 =null;


        CtrlTree RECIPE13_tree=null;
        CtrlTree ID14_tree=null;
        CtrlTree INT_LIT15_tree=null;

        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:77:3: ( ^( RECIPE ID ( INT_LIT )? block ) )
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:77:5: ^( RECIPE ID ( INT_LIT )? block )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            RECIPE13=(CtrlTree)match(input,RECIPE,FOLLOW_RECIPE_in_recipe176); 


            if ( _first_0==null ) _first_0 = RECIPE13;
            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            ID14=(CtrlTree)match(input,ID,FOLLOW_ID_in_recipe178); 
             
            if ( _first_1==null ) _first_1 = ID14;


            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:77:18: ( INT_LIT )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==INT_LIT) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:77:18: INT_LIT
                    {
                    _last = (CtrlTree)input.LT(1);
                    INT_LIT15=(CtrlTree)match(input,INT_LIT,FOLLOW_INT_LIT_in_recipe180); 
                     
                    if ( _first_1==null ) _first_1 = INT_LIT15;


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;

            }


             helper.startBody(ID14, Kind.RECIPE); 

            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_block_in_recipe200);
            block16=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block16.tree;


             helper.endBody(); 

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
    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:84:1: functions : ^( FUNCTIONS ( function )* ) ;
    public final CtrlChecker.functions_return functions() throws RecognitionException {
        CtrlChecker.functions_return retval = new CtrlChecker.functions_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree FUNCTIONS17=null;
        CtrlChecker.function_return function18 =null;


        CtrlTree FUNCTIONS17_tree=null;

        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:85:3: ( ^( FUNCTIONS ( function )* ) )
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:85:5: ^( FUNCTIONS ( function )* )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            FUNCTIONS17=(CtrlTree)match(input,FUNCTIONS,FOLLOW_FUNCTIONS_in_functions231); 


            if ( _first_0==null ) _first_0 = FUNCTIONS17;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:85:17: ( function )*
                loop4:
                do {
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==FUNCTION) ) {
                        alt4=1;
                    }


                    switch (alt4) {
                	case 1 :
                	    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:85:17: function
                	    {
                	    _last = (CtrlTree)input.LT(1);
                	    pushFollow(FOLLOW_function_in_functions233);
                	    function18=function();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = function18.tree;


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


             helper.reorderFunctions(FUNCTIONS17); 

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
    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:89:1: function : ^( FUNCTION ID block ) ;
    public final CtrlChecker.function_return function() throws RecognitionException {
        CtrlChecker.function_return retval = new CtrlChecker.function_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree FUNCTION19=null;
        CtrlTree ID20=null;
        CtrlChecker.block_return block21 =null;


        CtrlTree FUNCTION19_tree=null;
        CtrlTree ID20_tree=null;

        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:90:3: ( ^( FUNCTION ID block ) )
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:90:5: ^( FUNCTION ID block )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            FUNCTION19=(CtrlTree)match(input,FUNCTION,FOLLOW_FUNCTION_in_function256); 


            if ( _first_0==null ) _first_0 = FUNCTION19;
            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            ID20=(CtrlTree)match(input,ID,FOLLOW_ID_in_function258); 
             
            if ( _first_1==null ) _first_1 = ID20;


             helper.startBody(ID20, Kind.FUNCTION); 

            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_block_in_function277);
            block21=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block21.tree;


             helper.endBody(); 

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


    public static class block_return extends TreeRuleReturnScope {
        public CtrlAut aut;
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "block"
    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:97:1: block returns [ CtrlAut aut ] : ^( BLOCK ( stat )* ) ;
    public final CtrlChecker.block_return block() throws RecognitionException {
        CtrlChecker.block_return retval = new CtrlChecker.block_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree BLOCK22=null;
        CtrlChecker.stat_return stat23 =null;


        CtrlTree BLOCK22_tree=null;

        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:98:3: ( ^( BLOCK ( stat )* ) )
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:98:5: ^( BLOCK ( stat )* )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            BLOCK22=(CtrlTree)match(input,BLOCK,FOLLOW_BLOCK_in_block315); 


            if ( _first_0==null ) _first_0 = BLOCK22;
             helper.openScope(); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:100:8: ( stat )*
                loop5:
                do {
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0==ALAP||LA5_0==ANY||LA5_0==BLOCK||(LA5_0 >= CALL && LA5_0 <= CHOICE)||LA5_0==IF||LA5_0==OTHER||LA5_0==STAR||(LA5_0 >= TRUE && LA5_0 <= WHILE)) ) {
                        alt5=1;
                    }


                    switch (alt5) {
                	case 1 :
                	    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:100:8: stat
                	    {
                	    _last = (CtrlTree)input.LT(1);
                	    pushFollow(FOLLOW_stat_in_block333);
                	    stat23=stat();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = stat23.tree;


                	    retval.tree = (CtrlTree)_first_0;
                	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                	        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                	    }
                	    break;

                	default :
                	    break loop5;
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
    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:105:1: stat : ( block | var_decl | ^( ALAP stat ) | ^( WHILE stat stat ) | ^( UNTIL stat stat ) | ^( TRY stat ( stat )? ) | ^( IF stat stat ( stat )? ) | ^( CHOICE stat ( stat )* ) | ^( STAR stat ) | rule | ANY | OTHER | TRUE );
    public final CtrlChecker.stat_return stat() throws RecognitionException {
        CtrlChecker.stat_return retval = new CtrlChecker.stat_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree ALAP26=null;
        CtrlTree WHILE28=null;
        CtrlTree UNTIL31=null;
        CtrlTree TRY34=null;
        CtrlTree IF37=null;
        CtrlTree CHOICE41=null;
        CtrlTree STAR44=null;
        CtrlTree ANY47=null;
        CtrlTree OTHER48=null;
        CtrlTree TRUE49=null;
        CtrlChecker.block_return block24 =null;

        CtrlChecker.var_decl_return var_decl25 =null;

        CtrlChecker.stat_return stat27 =null;

        CtrlChecker.stat_return stat29 =null;

        CtrlChecker.stat_return stat30 =null;

        CtrlChecker.stat_return stat32 =null;

        CtrlChecker.stat_return stat33 =null;

        CtrlChecker.stat_return stat35 =null;

        CtrlChecker.stat_return stat36 =null;

        CtrlChecker.stat_return stat38 =null;

        CtrlChecker.stat_return stat39 =null;

        CtrlChecker.stat_return stat40 =null;

        CtrlChecker.stat_return stat42 =null;

        CtrlChecker.stat_return stat43 =null;

        CtrlChecker.stat_return stat45 =null;

        CtrlChecker.rule_return rule46 =null;


        CtrlTree ALAP26_tree=null;
        CtrlTree WHILE28_tree=null;
        CtrlTree UNTIL31_tree=null;
        CtrlTree TRY34_tree=null;
        CtrlTree IF37_tree=null;
        CtrlTree CHOICE41_tree=null;
        CtrlTree STAR44_tree=null;
        CtrlTree ANY47_tree=null;
        CtrlTree OTHER48_tree=null;
        CtrlTree TRUE49_tree=null;

        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:106:3: ( block | var_decl | ^( ALAP stat ) | ^( WHILE stat stat ) | ^( UNTIL stat stat ) | ^( TRY stat ( stat )? ) | ^( IF stat stat ( stat )? ) | ^( CHOICE stat ( stat )* ) | ^( STAR stat ) | rule | ANY | OTHER | TRUE )
            int alt9=13;
            switch ( input.LA(1) ) {
            case BLOCK:
                {
                alt9=1;
                }
                break;
            case VAR:
                {
                alt9=2;
                }
                break;
            case ALAP:
                {
                alt9=3;
                }
                break;
            case WHILE:
                {
                alt9=4;
                }
                break;
            case UNTIL:
                {
                alt9=5;
                }
                break;
            case TRY:
                {
                alt9=6;
                }
                break;
            case IF:
                {
                alt9=7;
                }
                break;
            case CHOICE:
                {
                alt9=8;
                }
                break;
            case STAR:
                {
                alt9=9;
                }
                break;
            case CALL:
                {
                alt9=10;
                }
                break;
            case ANY:
                {
                alt9=11;
                }
                break;
            case OTHER:
                {
                alt9=12;
                }
                break;
            case TRUE:
                {
                alt9=13;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                throw nvae;

            }

            switch (alt9) {
                case 1 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:106:5: block
                    {
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_block_in_stat363);
                    block24=block();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = block24.tree;


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:107:5: var_decl
                    {
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_var_decl_in_stat369);
                    var_decl25=var_decl();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = var_decl25.tree;


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 3 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:108:5: ^( ALAP stat )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    ALAP26=(CtrlTree)match(input,ALAP,FOLLOW_ALAP_in_stat376); 


                    if ( _first_0==null ) _first_0 = ALAP26;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat378);
                    stat27=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat27.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 4 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:109:5: ^( WHILE stat stat )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    WHILE28=(CtrlTree)match(input,WHILE,FOLLOW_WHILE_in_stat387); 


                    if ( _first_0==null ) _first_0 = WHILE28;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat396);
                    stat29=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat29.tree;


                     helper.startBranch(); 

                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat414);
                    stat30=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat30.tree;


                     helper.endBranch(); 

                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 5 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:115:5: ^( UNTIL stat stat )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    UNTIL31=(CtrlTree)match(input,UNTIL,FOLLOW_UNTIL_in_stat438); 


                    if ( _first_0==null ) _first_0 = UNTIL31;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat447);
                    stat32=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat32.tree;


                     helper.startBranch(); 

                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat465);
                    stat33=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat33.tree;


                     helper.endBranch(); 

                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 6 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:121:5: ^( TRY stat ( stat )? )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    TRY34=(CtrlTree)match(input,TRY,FOLLOW_TRY_in_stat489); 


                    if ( _first_0==null ) _first_0 = TRY34;
                     helper.startBranch(); 

                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat507);
                    stat35=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat35.tree;


                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:124:8: ( stat )?
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0==ALAP||LA6_0==ANY||LA6_0==BLOCK||(LA6_0 >= CALL && LA6_0 <= CHOICE)||LA6_0==IF||LA6_0==OTHER||LA6_0==STAR||(LA6_0 >= TRUE && LA6_0 <= WHILE)) ) {
                        alt6=1;
                    }
                    switch (alt6) {
                        case 1 :
                            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:124:10: stat
                            {
                             helper.nextBranch(); 

                            _last = (CtrlTree)input.LT(1);
                            pushFollow(FOLLOW_stat_in_stat529);
                            stat36=stat();

                            state._fsp--;

                             
                            if ( _first_1==null ) _first_1 = stat36.tree;


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
                case 7 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:129:5: ^( IF stat stat ( stat )? )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    IF37=(CtrlTree)match(input,IF,FOLLOW_IF_in_stat563); 


                    if ( _first_0==null ) _first_0 = IF37;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat573);
                    stat38=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat38.tree;


                     helper.startBranch(); 

                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat592);
                    stat39=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat39.tree;


                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:133:8: ( stat )?
                    int alt7=2;
                    int LA7_0 = input.LA(1);

                    if ( (LA7_0==ALAP||LA7_0==ANY||LA7_0==BLOCK||(LA7_0 >= CALL && LA7_0 <= CHOICE)||LA7_0==IF||LA7_0==OTHER||LA7_0==STAR||(LA7_0 >= TRUE && LA7_0 <= WHILE)) ) {
                        alt7=1;
                    }
                    switch (alt7) {
                        case 1 :
                            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:133:10: stat
                            {
                             helper.nextBranch(); 

                            _last = (CtrlTree)input.LT(1);
                            pushFollow(FOLLOW_stat_in_stat615);
                            stat40=stat();

                            state._fsp--;

                             
                            if ( _first_1==null ) _first_1 = stat40.tree;


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
                case 8 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:138:5: ^( CHOICE stat ( stat )* )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    CHOICE41=(CtrlTree)match(input,CHOICE,FOLLOW_CHOICE_in_stat649); 


                    if ( _first_0==null ) _first_0 = CHOICE41;
                     helper.startBranch(); 

                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat667);
                    stat42=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat42.tree;


                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:141:8: ( stat )*
                    loop8:
                    do {
                        int alt8=2;
                        int LA8_0 = input.LA(1);

                        if ( (LA8_0==ALAP||LA8_0==ANY||LA8_0==BLOCK||(LA8_0 >= CALL && LA8_0 <= CHOICE)||LA8_0==IF||LA8_0==OTHER||LA8_0==STAR||(LA8_0 >= TRUE && LA8_0 <= WHILE)) ) {
                            alt8=1;
                        }


                        switch (alt8) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:141:10: stat
                    	    {
                    	     helper.nextBranch(); 

                    	    _last = (CtrlTree)input.LT(1);
                    	    pushFollow(FOLLOW_stat_in_stat690);
                    	    stat43=stat();

                    	    state._fsp--;

                    	     
                    	    if ( _first_1==null ) _first_1 = stat43.tree;


                    	    retval.tree = (CtrlTree)_first_0;
                    	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                    	        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    	    }
                    	    break;

                    	default :
                    	    break loop8;
                        }
                    } while (true);


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                     helper.endBranch(); 

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 9 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:146:5: ^( STAR stat )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    STAR44=(CtrlTree)match(input,STAR,FOLLOW_STAR_in_stat726); 


                    if ( _first_0==null ) _first_0 = STAR44;
                     helper.startBranch(); 

                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat744);
                    stat45=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat45.tree;


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
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:151:5: rule
                    {
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_rule_in_stat766);
                    rule46=rule();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = rule46.tree;


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 11 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:152:5: ANY
                    {
                    _last = (CtrlTree)input.LT(1);
                    ANY47=(CtrlTree)match(input,ANY,FOLLOW_ANY_in_stat772); 
                     
                    if ( _first_0==null ) _first_0 = ANY47;


                     helper.checkAny(ANY47); 

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 12 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:154:5: OTHER
                    {
                    _last = (CtrlTree)input.LT(1);
                    OTHER48=(CtrlTree)match(input,OTHER,FOLLOW_OTHER_in_stat784); 
                     
                    if ( _first_0==null ) _first_0 = OTHER48;


                     helper.checkOther(OTHER48); 

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 13 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:156:5: TRUE
                    {
                    _last = (CtrlTree)input.LT(1);
                    TRUE49=(CtrlTree)match(input,TRUE,FOLLOW_TRUE_in_stat796); 
                     
                    if ( _first_0==null ) _first_0 = TRUE49;


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
    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:159:1: rule : ^( CALL id= ID ( ^( ARGS ( arg )* ) )? ) ;
    public final CtrlChecker.rule_return rule() throws RecognitionException {
        CtrlChecker.rule_return retval = new CtrlChecker.rule_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree id=null;
        CtrlTree CALL50=null;
        CtrlTree ARGS51=null;
        CtrlChecker.arg_return arg52 =null;


        CtrlTree id_tree=null;
        CtrlTree CALL50_tree=null;
        CtrlTree ARGS51_tree=null;

        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:161:3: ( ^( CALL id= ID ( ^( ARGS ( arg )* ) )? ) )
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:161:5: ^( CALL id= ID ( ^( ARGS ( arg )* ) )? )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            CALL50=(CtrlTree)match(input,CALL,FOLLOW_CALL_in_rule814); 


            if ( _first_0==null ) _first_0 = CALL50;
            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            id=(CtrlTree)match(input,ID,FOLLOW_ID_in_rule818); 
             
            if ( _first_1==null ) _first_1 = id;


            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:161:18: ( ^( ARGS ( arg )* ) )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==ARGS) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:161:19: ^( ARGS ( arg )* )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_2 = _last;
                    CtrlTree _first_2 = null;
                    _last = (CtrlTree)input.LT(1);
                    ARGS51=(CtrlTree)match(input,ARGS,FOLLOW_ARGS_in_rule822); 


                    if ( _first_1==null ) _first_1 = ARGS51;
                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:161:26: ( arg )*
                        loop10:
                        do {
                            int alt10=2;
                            int LA10_0 = input.LA(1);

                            if ( (LA10_0==ARG) ) {
                                alt10=1;
                            }


                            switch (alt10) {
                        	case 1 :
                        	    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:161:26: arg
                        	    {
                        	    _last = (CtrlTree)input.LT(1);
                        	    pushFollow(FOLLOW_arg_in_rule824);
                        	    arg52=arg();

                        	    state._fsp--;

                        	     
                        	    if ( _first_2==null ) _first_2 = arg52.tree;


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
                    }
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
    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:164:1: var_decl : ^( VAR type ( ID )+ ) ;
    public final CtrlChecker.var_decl_return var_decl() throws RecognitionException {
        CtrlChecker.var_decl_return retval = new CtrlChecker.var_decl_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree VAR53=null;
        CtrlTree ID55=null;
        CtrlChecker.type_return type54 =null;


        CtrlTree VAR53_tree=null;
        CtrlTree ID55_tree=null;

        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:165:2: ( ^( VAR type ( ID )+ ) )
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:165:4: ^( VAR type ( ID )+ )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            VAR53=(CtrlTree)match(input,VAR,FOLLOW_VAR_in_var_decl843); 


            if ( _first_0==null ) _first_0 = VAR53;
            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_type_in_var_decl845);
            type54=type();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = type54.tree;


            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:167:7: ( ID )+
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
            	    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:167:9: ID
            	    {
            	    _last = (CtrlTree)input.LT(1);
            	    ID55=(CtrlTree)match(input,ID,FOLLOW_ID_in_var_decl862); 
            	     
            	    if ( _first_1==null ) _first_1 = ID55;


            	     helper.declareVar(ID55, (type54!=null?((CtrlTree)type54.tree):null)); 

            	    retval.tree = (CtrlTree)_first_0;
            	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
            	        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

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
    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:173:1: type : ( NODE | BOOL | STRING | INT | REAL );
    public final CtrlChecker.type_return type() throws RecognitionException {
        CtrlChecker.type_return retval = new CtrlChecker.type_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree NODE56=null;
        CtrlTree BOOL57=null;
        CtrlTree STRING58=null;
        CtrlTree INT59=null;
        CtrlTree REAL60=null;

        CtrlTree NODE56_tree=null;
        CtrlTree BOOL57_tree=null;
        CtrlTree STRING58_tree=null;
        CtrlTree INT59_tree=null;
        CtrlTree REAL60_tree=null;

        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:174:3: ( NODE | BOOL | STRING | INT | REAL )
            int alt13=5;
            switch ( input.LA(1) ) {
            case NODE:
                {
                alt13=1;
                }
                break;
            case BOOL:
                {
                alt13=2;
                }
                break;
            case STRING:
                {
                alt13=3;
                }
                break;
            case INT:
                {
                alt13=4;
                }
                break;
            case REAL:
                {
                alt13=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;

            }

            switch (alt13) {
                case 1 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:174:5: NODE
                    {
                    _last = (CtrlTree)input.LT(1);
                    NODE56=(CtrlTree)match(input,NODE,FOLLOW_NODE_in_type901); 
                     
                    if ( _first_0==null ) _first_0 = NODE56;


                     helper.checkType(NODE56); 

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:175:5: BOOL
                    {
                    _last = (CtrlTree)input.LT(1);
                    BOOL57=(CtrlTree)match(input,BOOL,FOLLOW_BOOL_in_type911); 
                     
                    if ( _first_0==null ) _first_0 = BOOL57;


                     helper.checkType(BOOL57); 

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 3 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:176:5: STRING
                    {
                    _last = (CtrlTree)input.LT(1);
                    STRING58=(CtrlTree)match(input,STRING,FOLLOW_STRING_in_type921); 
                     
                    if ( _first_0==null ) _first_0 = STRING58;


                     helper.checkType(STRING58); 

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 4 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:177:5: INT
                    {
                    _last = (CtrlTree)input.LT(1);
                    INT59=(CtrlTree)match(input,INT,FOLLOW_INT_in_type929); 
                     
                    if ( _first_0==null ) _first_0 = INT59;


                     helper.checkType(INT59); 

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 5 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:178:5: REAL
                    {
                    _last = (CtrlTree)input.LT(1);
                    REAL60=(CtrlTree)match(input,REAL,FOLLOW_REAL_in_type940); 
                     
                    if ( _first_0==null ) _first_0 = REAL60;


                     helper.checkType(REAL60); 

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
    // $ANTLR end "type"


    public static class arg_return extends TreeRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "arg"
    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:181:1: arg : ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) ) ;
    public final CtrlChecker.arg_return arg() throws RecognitionException {
        CtrlChecker.arg_return retval = new CtrlChecker.arg_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree ARG61=null;
        CtrlTree OUT62=null;
        CtrlTree ID63=null;
        CtrlTree DONT_CARE64=null;
        CtrlChecker.literal_return literal65 =null;


        CtrlTree ARG61_tree=null;
        CtrlTree OUT62_tree=null;
        CtrlTree ID63_tree=null;
        CtrlTree DONT_CARE64_tree=null;

        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:182:2: ( ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) ) )
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:182:4: ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            ARG61=(CtrlTree)match(input,ARG,FOLLOW_ARG_in_arg960); 


            if ( _first_0==null ) _first_0 = ARG61;
            match(input, Token.DOWN, null); 
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:183:7: ( ( OUT )? ID | DONT_CARE | literal )
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
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:183:9: ( OUT )? ID
                    {
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:183:9: ( OUT )?
                    int alt14=2;
                    int LA14_0 = input.LA(1);

                    if ( (LA14_0==OUT) ) {
                        alt14=1;
                    }
                    switch (alt14) {
                        case 1 :
                            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:183:9: OUT
                            {
                            _last = (CtrlTree)input.LT(1);
                            OUT62=(CtrlTree)match(input,OUT,FOLLOW_OUT_in_arg971); 
                             
                            if ( _first_1==null ) _first_1 = OUT62;


                            retval.tree = (CtrlTree)_first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                            }
                            break;

                    }


                    _last = (CtrlTree)input.LT(1);
                    ID63=(CtrlTree)match(input,ID,FOLLOW_ID_in_arg974); 
                     
                    if ( _first_1==null ) _first_1 = ID63;


                     helper.checkVarArg(ARG61); 

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:184:9: DONT_CARE
                    {
                    _last = (CtrlTree)input.LT(1);
                    DONT_CARE64=(CtrlTree)match(input,DONT_CARE,FOLLOW_DONT_CARE_in_arg986); 
                     
                    if ( _first_1==null ) _first_1 = DONT_CARE64;


                     helper.checkDontCareArg(ARG61); 

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 3 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:185:9: literal
                    {
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_literal_in_arg998);
                    literal65=literal();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = literal65.tree;


                     helper.checkConstArg(ARG61); 

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
    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:190:1: literal : ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT );
    public final CtrlChecker.literal_return literal() throws RecognitionException {
        CtrlChecker.literal_return retval = new CtrlChecker.literal_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree set66=null;

        CtrlTree set66_tree=null;

        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:191:3: ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT )
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\CtrlChecker.g:
            {
            _last = (CtrlTree)input.LT(1);
            set66=(CtrlTree)input.LT(1);

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


 

    public static final BitSet FOLLOW_PROGRAM_in_program56 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_package_decl_in_program58 = new BitSet(new long[]{0x0010000020000000L});
    public static final BitSet FOLLOW_import_decl_in_program60 = new BitSet(new long[]{0x0010000020000000L});
    public static final BitSet FOLLOW_recipes_in_program63 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_functions_in_program65 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_block_in_program67 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PACKAGE_in_package_decl90 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_package_decl92 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IMPORT_in_import_decl125 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_import_decl127 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RECIPES_in_recipes157 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_recipe_in_recipes159 = new BitSet(new long[]{0x0008000000000008L});
    public static final BitSet FOLLOW_RECIPE_in_recipe176 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_recipe178 = new BitSet(new long[]{0x0000000080000800L});
    public static final BitSet FOLLOW_INT_LIT_in_recipe180 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_block_in_recipe200 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FUNCTIONS_in_functions231 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_function_in_functions233 = new BitSet(new long[]{0x0000000002000008L});
    public static final BitSet FOLLOW_FUNCTION_in_function256 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_function258 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_block_in_function277 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BLOCK_in_block315 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_block333 = new BitSet(new long[]{0xF20002001000C858L,0x0000000000000001L});
    public static final BitSet FOLLOW_block_in_stat363 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_decl_in_stat369 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALAP_in_stat376 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat378 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WHILE_in_stat387 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat396 = new BitSet(new long[]{0xF20002001000C850L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat414 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_UNTIL_in_stat438 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat447 = new BitSet(new long[]{0xF20002001000C850L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat465 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TRY_in_stat489 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat507 = new BitSet(new long[]{0xF20002001000C858L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat529 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IF_in_stat563 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat573 = new BitSet(new long[]{0xF20002001000C850L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat592 = new BitSet(new long[]{0xF20002001000C858L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat615 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CHOICE_in_stat649 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat667 = new BitSet(new long[]{0xF20002001000C858L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat690 = new BitSet(new long[]{0xF20002001000C858L,0x0000000000000001L});
    public static final BitSet FOLLOW_STAR_in_stat726 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat744 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_rule_in_stat766 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_stat772 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_stat784 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_stat796 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CALL_in_rule814 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rule818 = new BitSet(new long[]{0x0000000000000108L});
    public static final BitSet FOLLOW_ARGS_in_rule822 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_arg_in_rule824 = new BitSet(new long[]{0x0000000000000088L});
    public static final BitSet FOLLOW_VAR_in_var_decl843 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_type_in_var_decl845 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_var_decl862 = new BitSet(new long[]{0x0000000008000008L});
    public static final BitSet FOLLOW_NODE_in_type901 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_type911 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_type921 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_type929 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REAL_in_type940 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ARG_in_arg960 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_OUT_in_arg971 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_arg974 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DONT_CARE_in_arg986 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_literal_in_arg998 = new BitSet(new long[]{0x0000000000000008L});

}