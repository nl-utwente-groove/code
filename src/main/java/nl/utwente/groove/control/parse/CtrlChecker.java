// $ANTLR 3.5.2 CtrlChecker.g 2025-03-26 17:57:06

package nl.utwente.groove.control.parse;
import nl.utwente.groove.control.*;
import nl.utwente.groove.algebra.AlgebraFamily;
import nl.utwente.groove.util.antlr.ParseTreeAdaptor;
import nl.utwente.groove.util.antlr.ParseInfo;
import java.util.LinkedList;
import java.util.Stack;
import java.util.HashSet;
import java.util.HashMap;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


@SuppressWarnings("all")
public class CtrlChecker extends TreeParser {
	public static final String[] tokenNames = new String[] {
		"<invalid>", "<EOR>", "<DOWN>", "<UP>", "ALAP", "AMP", "ANY", "ARGS", 
		"ARG_CALL", "ARG_ID", "ARG_LIT", "ARG_OP", "ARG_OUT", "ARG_WILD", "ASTERISK", 
		"ATOM", "BAR", "BECOMES", "BLOCK", "BOOL", "BQUOTE", "BSLASH", "CALL", 
		"CHOICE", "COLON", "COMMA", "DO", "DOT", "DO_UNTIL", "DO_WHILE", "Digit", 
		"ELSE", "EQ", "EscapeSequence", "FALSE", "FUNCTION", "FUNCTIONS", "GEQ", 
		"HALT", "ID", "IF", "IMPORT", "IMPORTS", "INT", "INT_LIT", "IntegerNumber", 
		"LANGLE", "LCURLY", "LEQ", "LPAR", "Letter", "MINUS", "ML_COMMENT", "NEQ", 
		"NODE", "NOT", "NonIntegerNumber", "OR", "OTHER", "OUT", "PACKAGE", "PAR", 
		"PARS", "PERCENT", "PLUS", "PRIORITY", "PROGRAM", "PosDigit", "QUOTE", 
		"RANGLE", "RCURLY", "REAL", "REAL_LIT", "RECIPE", "RECIPES", "RPAR", "SEMI", 
		"SHARP", "SLASH", "SL_COMMENT", "STAR", "STRING", "STRING_LIT", "TRUE", 
		"TRY", "UNDER", "UNTIL", "VAR", "WHILE", "WS"
	};
	public static final int EOF=-1;
	public static final int ALAP=4;
	public static final int AMP=5;
	public static final int ANY=6;
	public static final int ARGS=7;
	public static final int ARG_CALL=8;
	public static final int ARG_ID=9;
	public static final int ARG_LIT=10;
	public static final int ARG_OP=11;
	public static final int ARG_OUT=12;
	public static final int ARG_WILD=13;
	public static final int ASTERISK=14;
	public static final int ATOM=15;
	public static final int BAR=16;
	public static final int BECOMES=17;
	public static final int BLOCK=18;
	public static final int BOOL=19;
	public static final int BQUOTE=20;
	public static final int BSLASH=21;
	public static final int CALL=22;
	public static final int CHOICE=23;
	public static final int COLON=24;
	public static final int COMMA=25;
	public static final int DO=26;
	public static final int DOT=27;
	public static final int DO_UNTIL=28;
	public static final int DO_WHILE=29;
	public static final int Digit=30;
	public static final int ELSE=31;
	public static final int EQ=32;
	public static final int EscapeSequence=33;
	public static final int FALSE=34;
	public static final int FUNCTION=35;
	public static final int FUNCTIONS=36;
	public static final int GEQ=37;
	public static final int HALT=38;
	public static final int ID=39;
	public static final int IF=40;
	public static final int IMPORT=41;
	public static final int IMPORTS=42;
	public static final int INT=43;
	public static final int INT_LIT=44;
	public static final int IntegerNumber=45;
	public static final int LANGLE=46;
	public static final int LCURLY=47;
	public static final int LEQ=48;
	public static final int LPAR=49;
	public static final int Letter=50;
	public static final int MINUS=51;
	public static final int ML_COMMENT=52;
	public static final int NEQ=53;
	public static final int NODE=54;
	public static final int NOT=55;
	public static final int NonIntegerNumber=56;
	public static final int OR=57;
	public static final int OTHER=58;
	public static final int OUT=59;
	public static final int PACKAGE=60;
	public static final int PAR=61;
	public static final int PARS=62;
	public static final int PERCENT=63;
	public static final int PLUS=64;
	public static final int PRIORITY=65;
	public static final int PROGRAM=66;
	public static final int PosDigit=67;
	public static final int QUOTE=68;
	public static final int RANGLE=69;
	public static final int RCURLY=70;
	public static final int REAL=71;
	public static final int REAL_LIT=72;
	public static final int RECIPE=73;
	public static final int RECIPES=74;
	public static final int RPAR=75;
	public static final int SEMI=76;
	public static final int SHARP=77;
	public static final int SLASH=78;
	public static final int SL_COMMENT=79;
	public static final int STAR=80;
	public static final int STRING=81;
	public static final int STRING_LIT=82;
	public static final int TRUE=83;
	public static final int TRY=84;
	public static final int UNDER=85;
	public static final int UNTIL=86;
	public static final int VAR=87;
	public static final int WHILE=88;
	public static final int WS=89;

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
	@Override public String[] getTokenNames() { return CtrlChecker.tokenNames; }
	@Override public String getGrammarFileName() { return "CtrlChecker.g"; }


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
		@Override
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "program"
	// CtrlChecker.g:39:1: program : ^( PROGRAM package_decl imports functions recipes block ) ;
	public final CtrlChecker.program_return program() throws RecognitionException {
		CtrlChecker.program_return retval = new CtrlChecker.program_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		CtrlTree _first_0 = null;
		CtrlTree _last = null;


		CtrlTree PROGRAM1=null;
		TreeRuleReturnScope package_decl2 =null;
		TreeRuleReturnScope imports3 =null;
		TreeRuleReturnScope functions4 =null;
		TreeRuleReturnScope recipes5 =null;
		TreeRuleReturnScope block6 =null;

		CtrlTree PROGRAM1_tree=null;

		 helper.clearErrors(); 
		try {
			// CtrlChecker.g:41:3: ( ^( PROGRAM package_decl imports functions recipes block ) )
			// CtrlChecker.g:41:5: ^( PROGRAM package_decl imports functions recipes block )
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

			 
			if ( _first_1==null ) _first_1 = (CtrlTree)package_decl2.getTree();

			_last = (CtrlTree)input.LT(1);
			pushFollow(FOLLOW_imports_in_program65);
			imports3=imports();
			state._fsp--;

			 
			if ( _first_1==null ) _first_1 = (CtrlTree)imports3.getTree();

			_last = (CtrlTree)input.LT(1);
			pushFollow(FOLLOW_functions_in_program67);
			functions4=functions();
			state._fsp--;

			 
			if ( _first_1==null ) _first_1 = (CtrlTree)functions4.getTree();

			_last = (CtrlTree)input.LT(1);
			pushFollow(FOLLOW_recipes_in_program69);
			recipes5=recipes();
			state._fsp--;

			 
			if ( _first_1==null ) _first_1 = (CtrlTree)recipes5.getTree();

			_last = (CtrlTree)input.LT(1);
			pushFollow(FOLLOW_block_in_program71);
			block6=block();
			state._fsp--;

			 
			if ( _first_1==null ) _first_1 = (CtrlTree)block6.getTree();

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
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "package_decl"
	// CtrlChecker.g:44:1: package_decl : ^( PACKAGE qual_id SEMI ) ;
	public final CtrlChecker.package_decl_return package_decl() throws RecognitionException {
		CtrlChecker.package_decl_return retval = new CtrlChecker.package_decl_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		CtrlTree _first_0 = null;
		CtrlTree _last = null;


		CtrlTree PACKAGE7=null;
		CtrlTree SEMI9=null;
		TreeRuleReturnScope qual_id8 =null;

		CtrlTree PACKAGE7_tree=null;
		CtrlTree SEMI9_tree=null;

		try {
			// CtrlChecker.g:45:3: ( ^( PACKAGE qual_id SEMI ) )
			// CtrlChecker.g:45:5: ^( PACKAGE qual_id SEMI )
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

			 
			if ( _first_1==null ) _first_1 = (CtrlTree)qual_id8.getTree();

			_last = (CtrlTree)input.LT(1);
			SEMI9=(CtrlTree)match(input,SEMI,FOLLOW_SEMI_in_package_decl92); 
			 
			if ( _first_1==null ) _first_1 = SEMI9;

			 helper.checkPackage((qual_id8!=null?((CtrlTree)qual_id8.getTree()):null)); 
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
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "imports"
	// CtrlChecker.g:50:1: imports : ^( IMPORTS ( import_decl )* ) ;
	public final CtrlChecker.imports_return imports() throws RecognitionException {
		CtrlChecker.imports_return retval = new CtrlChecker.imports_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		CtrlTree _first_0 = null;
		CtrlTree _last = null;


		CtrlTree IMPORTS10=null;
		TreeRuleReturnScope import_decl11 =null;

		CtrlTree IMPORTS10_tree=null;

		try {
			// CtrlChecker.g:51:3: ( ^( IMPORTS ( import_decl )* ) )
			// CtrlChecker.g:51:5: ^( IMPORTS ( import_decl )* )
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
				// CtrlChecker.g:51:15: ( import_decl )*
				loop1:
				while (true) {
					int alt1=2;
					int LA1_0 = input.LA(1);
					if ( (LA1_0==IMPORT) ) {
						alt1=1;
					}

					switch (alt1) {
					case 1 :
						// CtrlChecker.g:51:15: import_decl
						{
						_last = (CtrlTree)input.LT(1);
						pushFollow(FOLLOW_import_decl_in_imports124);
						import_decl11=import_decl();
						state._fsp--;

						 
						if ( _first_1==null ) _first_1 = (CtrlTree)import_decl11.getTree();

						retval.tree = _first_0;
						if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
							retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

						}
						break;

					default :
						break loop1;
					}
				}

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
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "import_decl"
	// CtrlChecker.g:54:1: import_decl : ^( IMPORT qual_id SEMI ) ;
	public final CtrlChecker.import_decl_return import_decl() throws RecognitionException {
		CtrlChecker.import_decl_return retval = new CtrlChecker.import_decl_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		CtrlTree _first_0 = null;
		CtrlTree _last = null;


		CtrlTree IMPORT12=null;
		CtrlTree SEMI14=null;
		TreeRuleReturnScope qual_id13 =null;

		CtrlTree IMPORT12_tree=null;
		CtrlTree SEMI14_tree=null;

		try {
			// CtrlChecker.g:55:3: ( ^( IMPORT qual_id SEMI ) )
			// CtrlChecker.g:55:5: ^( IMPORT qual_id SEMI )
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

			 
			if ( _first_1==null ) _first_1 = (CtrlTree)qual_id13.getTree();

			_last = (CtrlTree)input.LT(1);
			SEMI14=(CtrlTree)match(input,SEMI,FOLLOW_SEMI_in_import_decl145); 
			 
			if ( _first_1==null ) _first_1 = SEMI14;

			 helper.checkImport((qual_id13!=null?((CtrlTree)qual_id13.getTree()):null)); 
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
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "recipes"
	// CtrlChecker.g:60:1: recipes : ^( RECIPES ( recipe )* ) ;
	public final CtrlChecker.recipes_return recipes() throws RecognitionException {
		CtrlChecker.recipes_return retval = new CtrlChecker.recipes_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		CtrlTree _first_0 = null;
		CtrlTree _last = null;


		CtrlTree RECIPES15=null;
		TreeRuleReturnScope recipe16 =null;

		CtrlTree RECIPES15_tree=null;

		try {
			// CtrlChecker.g:61:3: ( ^( RECIPES ( recipe )* ) )
			// CtrlChecker.g:61:5: ^( RECIPES ( recipe )* )
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
				// CtrlChecker.g:61:15: ( recipe )*
				loop2:
				while (true) {
					int alt2=2;
					int LA2_0 = input.LA(1);
					if ( (LA2_0==RECIPE) ) {
						alt2=1;
					}

					switch (alt2) {
					case 1 :
						// CtrlChecker.g:61:15: recipe
						{
						_last = (CtrlTree)input.LT(1);
						pushFollow(FOLLOW_recipe_in_recipes177);
						recipe16=recipe();
						state._fsp--;

						 
						if ( _first_1==null ) _first_1 = (CtrlTree)recipe16.getTree();

						retval.tree = _first_0;
						if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
							retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

						}
						break;

					default :
						break loop2;
					}
				}

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
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "recipe"
	// CtrlChecker.g:64:1: recipe : ^( RECIPE ID ^( PARS ( par_decl )* ) ( INT_LIT )? block ) ;
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
		TreeRuleReturnScope par_decl20 =null;
		TreeRuleReturnScope block22 =null;

		CtrlTree RECIPE17_tree=null;
		CtrlTree ID18_tree=null;
		CtrlTree PARS19_tree=null;
		CtrlTree INT_LIT21_tree=null;

		try {
			// CtrlChecker.g:65:3: ( ^( RECIPE ID ^( PARS ( par_decl )* ) ( INT_LIT )? block ) )
			// CtrlChecker.g:65:5: ^( RECIPE ID ^( PARS ( par_decl )* ) ( INT_LIT )? block )
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
				// CtrlChecker.g:67:18: ( par_decl )*
				loop3:
				while (true) {
					int alt3=2;
					int LA3_0 = input.LA(1);
					if ( (LA3_0==PAR) ) {
						alt3=1;
					}

					switch (alt3) {
					case 1 :
						// CtrlChecker.g:67:18: par_decl
						{
						_last = (CtrlTree)input.LT(1);
						pushFollow(FOLLOW_par_decl_in_recipe218);
						par_decl20=par_decl();
						state._fsp--;

						 
						if ( _first_2==null ) _first_2 = (CtrlTree)par_decl20.getTree();

						retval.tree = _first_0;
						if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
							retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

						}
						break;

					default :
						break loop3;
					}
				}

				match(input, Token.UP, null); 
			}
			_last = _save_last_2;
			}


			// CtrlChecker.g:67:29: ( INT_LIT )?
			int alt4=2;
			int LA4_0 = input.LA(1);
			if ( (LA4_0==INT_LIT) ) {
				alt4=1;
			}
			switch (alt4) {
				case 1 :
					// CtrlChecker.g:67:29: INT_LIT
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

			 
			if ( _first_1==null ) _first_1 = (CtrlTree)block22.getTree();

			 helper.endBody((block22!=null?((CtrlTree)block22.getTree()):null)); 
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
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "functions"
	// CtrlChecker.g:73:1: functions : ^( FUNCTIONS ( function )* ) ;
	public final CtrlChecker.functions_return functions() throws RecognitionException {
		CtrlChecker.functions_return retval = new CtrlChecker.functions_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		CtrlTree _first_0 = null;
		CtrlTree _last = null;


		CtrlTree FUNCTIONS23=null;
		TreeRuleReturnScope function24 =null;

		CtrlTree FUNCTIONS23_tree=null;

		try {
			// CtrlChecker.g:74:3: ( ^( FUNCTIONS ( function )* ) )
			// CtrlChecker.g:74:5: ^( FUNCTIONS ( function )* )
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
				// CtrlChecker.g:74:18: ( function )*
				loop5:
				while (true) {
					int alt5=2;
					int LA5_0 = input.LA(1);
					if ( (LA5_0==FUNCTION) ) {
						alt5=1;
					}

					switch (alt5) {
					case 1 :
						// CtrlChecker.g:74:18: function
						{
						_last = (CtrlTree)input.LT(1);
						pushFollow(FOLLOW_function_in_functions266);
						function24=function();
						state._fsp--;

						 
						if ( _first_1==null ) _first_1 = (CtrlTree)function24.getTree();

						retval.tree = _first_0;
						if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
							retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

						}
						break;

					default :
						break loop5;
					}
				}

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
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "function"
	// CtrlChecker.g:77:1: function : ^( FUNCTION ID ^( PARS ( par_decl )* ) block ) ;
	public final CtrlChecker.function_return function() throws RecognitionException {
		CtrlChecker.function_return retval = new CtrlChecker.function_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		CtrlTree _first_0 = null;
		CtrlTree _last = null;


		CtrlTree FUNCTION25=null;
		CtrlTree ID26=null;
		CtrlTree PARS27=null;
		TreeRuleReturnScope par_decl28 =null;
		TreeRuleReturnScope block29 =null;

		CtrlTree FUNCTION25_tree=null;
		CtrlTree ID26_tree=null;
		CtrlTree PARS27_tree=null;

		try {
			// CtrlChecker.g:78:3: ( ^( FUNCTION ID ^( PARS ( par_decl )* ) block ) )
			// CtrlChecker.g:78:5: ^( FUNCTION ID ^( PARS ( par_decl )* ) block )
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
				// CtrlChecker.g:80:18: ( par_decl )*
				loop6:
				while (true) {
					int alt6=2;
					int LA6_0 = input.LA(1);
					if ( (LA6_0==PAR) ) {
						alt6=1;
					}

					switch (alt6) {
					case 1 :
						// CtrlChecker.g:80:18: par_decl
						{
						_last = (CtrlTree)input.LT(1);
						pushFollow(FOLLOW_par_decl_in_function306);
						par_decl28=par_decl();
						state._fsp--;

						 
						if ( _first_2==null ) _first_2 = (CtrlTree)par_decl28.getTree();

						retval.tree = _first_0;
						if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
							retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

						}
						break;

					default :
						break loop6;
					}
				}

				match(input, Token.UP, null); 
			}
			_last = _save_last_2;
			}


			_last = (CtrlTree)input.LT(1);
			pushFollow(FOLLOW_block_in_function317);
			block29=block();
			state._fsp--;

			 
			if ( _first_1==null ) _first_1 = (CtrlTree)block29.getTree();

			 helper.endBody((block29!=null?((CtrlTree)block29.getTree()):null)); 
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
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "par_decl"
	// CtrlChecker.g:86:1: par_decl : ^( PAR ( OUT )? type ID ) ;
	public final CtrlChecker.par_decl_return par_decl() throws RecognitionException {
		CtrlChecker.par_decl_return retval = new CtrlChecker.par_decl_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		CtrlTree _first_0 = null;
		CtrlTree _last = null;


		CtrlTree PAR30=null;
		CtrlTree OUT31=null;
		CtrlTree ID33=null;
		TreeRuleReturnScope type32 =null;

		CtrlTree PAR30_tree=null;
		CtrlTree OUT31_tree=null;
		CtrlTree ID33_tree=null;

		try {
			// CtrlChecker.g:87:3: ( ^( PAR ( OUT )? type ID ) )
			// CtrlChecker.g:87:5: ^( PAR ( OUT )? type ID )
			{
			_last = (CtrlTree)input.LT(1);
			{
			CtrlTree _save_last_1 = _last;
			CtrlTree _first_1 = null;
			_last = (CtrlTree)input.LT(1);
			PAR30=(CtrlTree)match(input,PAR,FOLLOW_PAR_in_par_decl350); 

			if ( _first_0==null ) _first_0 = PAR30;
			match(input, Token.DOWN, null); 
			// CtrlChecker.g:87:11: ( OUT )?
			int alt7=2;
			int LA7_0 = input.LA(1);
			if ( (LA7_0==OUT) ) {
				alt7=1;
			}
			switch (alt7) {
				case 1 :
					// CtrlChecker.g:87:11: OUT
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

			 
			if ( _first_1==null ) _first_1 = (CtrlTree)type32.getTree();

			_last = (CtrlTree)input.LT(1);
			ID33=(CtrlTree)match(input,ID,FOLLOW_ID_in_par_decl357); 
			 
			if ( _first_1==null ) _first_1 = ID33;

			match(input, Token.UP, null); 
			_last = _save_last_1;
			}


			 helper.declarePar(ID33, (type32!=null?((CtrlTree)type32.getTree()):null), OUT31); 
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
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "block"
	// CtrlChecker.g:91:1: block : ^( BLOCK ( stat )* ) ;
	public final CtrlChecker.block_return block() throws RecognitionException {
		CtrlChecker.block_return retval = new CtrlChecker.block_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		CtrlTree _first_0 = null;
		CtrlTree _last = null;


		CtrlTree BLOCK34=null;
		TreeRuleReturnScope stat35 =null;

		CtrlTree BLOCK34_tree=null;

		try {
			// CtrlChecker.g:92:3: ( ^( BLOCK ( stat )* ) )
			// CtrlChecker.g:92:5: ^( BLOCK ( stat )* )
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
				// CtrlChecker.g:94:8: ( stat )*
				loop8:
				while (true) {
					int alt8=2;
					int LA8_0 = input.LA(1);
					if ( (LA8_0==ALAP||LA8_0==ATOM||(LA8_0 >= BECOMES && LA8_0 <= BLOCK)||(LA8_0 >= CALL && LA8_0 <= CHOICE)||LA8_0==HALT||LA8_0==IF||LA8_0==SEMI||LA8_0==STAR||(LA8_0 >= TRUE && LA8_0 <= TRY)||LA8_0==UNTIL||LA8_0==WHILE) ) {
						alt8=1;
					}

					switch (alt8) {
					case 1 :
						// CtrlChecker.g:94:8: stat
						{
						_last = (CtrlTree)input.LT(1);
						pushFollow(FOLLOW_stat_in_block399);
						stat35=stat();
						state._fsp--;

						 
						if ( _first_1==null ) _first_1 = (CtrlTree)stat35.getTree();

						retval.tree = _first_0;
						if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
							retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

						}
						break;

					default :
						break loop8;
					}
				}

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
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "stat"
	// CtrlChecker.g:99:1: stat : ( block | ^( SEMI var_decl ) | ^( SEMI stat ) | ^( ALAP stat ) | ^( ATOM stat ) | ^( WHILE stat stat ) | ^( UNTIL stat stat ) | ^( TRY stat ( stat )? ) | ^( IF stat stat ( stat )? ) | ^( CHOICE stat ( stat )* ) | HALT | ^( STAR stat ) | call | assign | TRUE );
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
		CtrlTree HALT61=null;
		CtrlTree STAR62=null;
		CtrlTree TRUE66=null;
		TreeRuleReturnScope block36 =null;
		TreeRuleReturnScope var_decl38 =null;
		TreeRuleReturnScope stat40 =null;
		TreeRuleReturnScope stat42 =null;
		TreeRuleReturnScope stat44 =null;
		TreeRuleReturnScope stat46 =null;
		TreeRuleReturnScope stat47 =null;
		TreeRuleReturnScope stat49 =null;
		TreeRuleReturnScope stat50 =null;
		TreeRuleReturnScope stat52 =null;
		TreeRuleReturnScope stat53 =null;
		TreeRuleReturnScope stat55 =null;
		TreeRuleReturnScope stat56 =null;
		TreeRuleReturnScope stat57 =null;
		TreeRuleReturnScope stat59 =null;
		TreeRuleReturnScope stat60 =null;
		TreeRuleReturnScope stat63 =null;
		TreeRuleReturnScope call64 =null;
		TreeRuleReturnScope assign65 =null;

		CtrlTree SEMI37_tree=null;
		CtrlTree SEMI39_tree=null;
		CtrlTree ALAP41_tree=null;
		CtrlTree ATOM43_tree=null;
		CtrlTree WHILE45_tree=null;
		CtrlTree UNTIL48_tree=null;
		CtrlTree TRY51_tree=null;
		CtrlTree IF54_tree=null;
		CtrlTree CHOICE58_tree=null;
		CtrlTree HALT61_tree=null;
		CtrlTree STAR62_tree=null;
		CtrlTree TRUE66_tree=null;

		try {
			// CtrlChecker.g:100:3: ( block | ^( SEMI var_decl ) | ^( SEMI stat ) | ^( ALAP stat ) | ^( ATOM stat ) | ^( WHILE stat stat ) | ^( UNTIL stat stat ) | ^( TRY stat ( stat )? ) | ^( IF stat stat ( stat )? ) | ^( CHOICE stat ( stat )* ) | HALT | ^( STAR stat ) | call | assign | TRUE )
			int alt12=15;
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
					int LA12_15 = input.LA(3);
					if ( (LA12_15==VAR) ) {
						alt12=2;
					}
					else if ( (LA12_15==ALAP||LA12_15==ATOM||(LA12_15 >= BECOMES && LA12_15 <= BLOCK)||(LA12_15 >= CALL && LA12_15 <= CHOICE)||LA12_15==HALT||LA12_15==IF||LA12_15==SEMI||LA12_15==STAR||(LA12_15 >= TRUE && LA12_15 <= TRY)||LA12_15==UNTIL||LA12_15==WHILE) ) {
						alt12=3;
					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 12, 15, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 12, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
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
			case HALT:
				{
				alt12=11;
				}
				break;
			case STAR:
				{
				alt12=12;
				}
				break;
			case CALL:
				{
				alt12=13;
				}
				break;
			case BECOMES:
				{
				alt12=14;
				}
				break;
			case TRUE:
				{
				alt12=15;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 12, 0, input);
				throw nvae;
			}
			switch (alt12) {
				case 1 :
					// CtrlChecker.g:100:5: block
					{
					_last = (CtrlTree)input.LT(1);
					pushFollow(FOLLOW_block_in_stat429);
					block36=block();
					state._fsp--;

					 
					if ( _first_0==null ) _first_0 = (CtrlTree)block36.getTree();

					retval.tree = _first_0;
					if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
						retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

					}
					break;
				case 2 :
					// CtrlChecker.g:101:5: ^( SEMI var_decl )
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

					 
					if ( _first_1==null ) _first_1 = (CtrlTree)var_decl38.getTree();

					match(input, Token.UP, null); 
					_last = _save_last_1;
					}


					retval.tree = _first_0;
					if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
						retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

					}
					break;
				case 3 :
					// CtrlChecker.g:102:5: ^( SEMI stat )
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

					 
					if ( _first_1==null ) _first_1 = (CtrlTree)stat40.getTree();

					match(input, Token.UP, null); 
					_last = _save_last_1;
					}


					retval.tree = _first_0;
					if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
						retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

					}
					break;
				case 4 :
					// CtrlChecker.g:103:5: ^( ALAP stat )
					{
					_last = (CtrlTree)input.LT(1);
					{
					CtrlTree _save_last_1 = _last;
					CtrlTree _first_1 = null;
					_last = (CtrlTree)input.LT(1);
					ALAP41=(CtrlTree)match(input,ALAP,FOLLOW_ALAP_in_stat457); 

					if ( _first_0==null ) _first_0 = ALAP41;
					 helper.startBranch(); 
					match(input, Token.DOWN, null); 
					_last = (CtrlTree)input.LT(1);
					pushFollow(FOLLOW_stat_in_stat476);
					stat42=stat();
					state._fsp--;

					 
					if ( _first_1==null ) _first_1 = (CtrlTree)stat42.getTree();

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
				case 5 :
					// CtrlChecker.g:111:5: ^( ATOM stat )
					{
					_last = (CtrlTree)input.LT(1);
					{
					CtrlTree _save_last_1 = _last;
					CtrlTree _first_1 = null;
					_last = (CtrlTree)input.LT(1);
					ATOM43=(CtrlTree)match(input,ATOM,FOLLOW_ATOM_in_stat524); 

					if ( _first_0==null ) _first_0 = ATOM43;
					match(input, Token.DOWN, null); 
					_last = (CtrlTree)input.LT(1);
					pushFollow(FOLLOW_stat_in_stat526);
					stat44=stat();
					state._fsp--;

					 
					if ( _first_1==null ) _first_1 = (CtrlTree)stat44.getTree();

					match(input, Token.UP, null); 
					_last = _save_last_1;
					}


					retval.tree = _first_0;
					if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
						retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

					}
					break;
				case 6 :
					// CtrlChecker.g:112:5: ^( WHILE stat stat )
					{
					_last = (CtrlTree)input.LT(1);
					{
					CtrlTree _save_last_1 = _last;
					CtrlTree _first_1 = null;
					_last = (CtrlTree)input.LT(1);
					WHILE45=(CtrlTree)match(input,WHILE,FOLLOW_WHILE_in_stat535); 

					if ( _first_0==null ) _first_0 = WHILE45;
					match(input, Token.DOWN, null); 
					_last = (CtrlTree)input.LT(1);
					pushFollow(FOLLOW_stat_in_stat544);
					stat46=stat();
					state._fsp--;

					 
					if ( _first_1==null ) _first_1 = (CtrlTree)stat46.getTree();

					 helper.startBranch(); 
					_last = (CtrlTree)input.LT(1);
					pushFollow(FOLLOW_stat_in_stat562);
					stat47=stat();
					state._fsp--;

					 
					if ( _first_1==null ) _first_1 = (CtrlTree)stat47.getTree();

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
					// CtrlChecker.g:121:5: ^( UNTIL stat stat )
					{
					_last = (CtrlTree)input.LT(1);
					{
					CtrlTree _save_last_1 = _last;
					CtrlTree _first_1 = null;
					_last = (CtrlTree)input.LT(1);
					UNTIL48=(CtrlTree)match(input,UNTIL,FOLLOW_UNTIL_in_stat611); 

					if ( _first_0==null ) _first_0 = UNTIL48;
					match(input, Token.DOWN, null); 
					_last = (CtrlTree)input.LT(1);
					pushFollow(FOLLOW_stat_in_stat620);
					stat49=stat();
					state._fsp--;

					 
					if ( _first_1==null ) _first_1 = (CtrlTree)stat49.getTree();

					 helper.startBranch(); 
					_last = (CtrlTree)input.LT(1);
					pushFollow(FOLLOW_stat_in_stat638);
					stat50=stat();
					state._fsp--;

					 
					if ( _first_1==null ) _first_1 = (CtrlTree)stat50.getTree();

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
					// CtrlChecker.g:130:5: ^( TRY stat ( stat )? )
					{
					_last = (CtrlTree)input.LT(1);
					{
					CtrlTree _save_last_1 = _last;
					CtrlTree _first_1 = null;
					_last = (CtrlTree)input.LT(1);
					TRY51=(CtrlTree)match(input,TRY,FOLLOW_TRY_in_stat687); 

					if ( _first_0==null ) _first_0 = TRY51;
					 helper.startBranch(); 
					match(input, Token.DOWN, null); 
					_last = (CtrlTree)input.LT(1);
					pushFollow(FOLLOW_stat_in_stat705);
					stat52=stat();
					state._fsp--;

					 
					if ( _first_1==null ) _first_1 = (CtrlTree)stat52.getTree();

					// CtrlChecker.g:133:8: ( stat )?
					int alt9=2;
					int LA9_0 = input.LA(1);
					if ( (LA9_0==ALAP||LA9_0==ATOM||(LA9_0 >= BECOMES && LA9_0 <= BLOCK)||(LA9_0 >= CALL && LA9_0 <= CHOICE)||LA9_0==HALT||LA9_0==IF||LA9_0==SEMI||LA9_0==STAR||(LA9_0 >= TRUE && LA9_0 <= TRY)||LA9_0==UNTIL||LA9_0==WHILE) ) {
						alt9=1;
					}
					switch (alt9) {
						case 1 :
							// CtrlChecker.g:133:10: stat
							{
							 helper.nextBranch(); 
							_last = (CtrlTree)input.LT(1);
							pushFollow(FOLLOW_stat_in_stat727);
							stat53=stat();
							state._fsp--;

							 
							if ( _first_1==null ) _first_1 = (CtrlTree)stat53.getTree();

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
					// CtrlChecker.g:138:5: ^( IF stat stat ( stat )? )
					{
					_last = (CtrlTree)input.LT(1);
					{
					CtrlTree _save_last_1 = _last;
					CtrlTree _first_1 = null;
					_last = (CtrlTree)input.LT(1);
					IF54=(CtrlTree)match(input,IF,FOLLOW_IF_in_stat761); 

					if ( _first_0==null ) _first_0 = IF54;
					 helper.startBranch(); 
					match(input, Token.DOWN, null); 
					_last = (CtrlTree)input.LT(1);
					pushFollow(FOLLOW_stat_in_stat780);
					stat55=stat();
					state._fsp--;

					 
					if ( _first_1==null ) _first_1 = (CtrlTree)stat55.getTree();

					_last = (CtrlTree)input.LT(1);
					pushFollow(FOLLOW_stat_in_stat790);
					stat56=stat();
					state._fsp--;

					 
					if ( _first_1==null ) _first_1 = (CtrlTree)stat56.getTree();

					 helper.nextBranch(); 
					// CtrlChecker.g:143:8: ( stat )?
					int alt10=2;
					int LA10_0 = input.LA(1);
					if ( (LA10_0==ALAP||LA10_0==ATOM||(LA10_0 >= BECOMES && LA10_0 <= BLOCK)||(LA10_0 >= CALL && LA10_0 <= CHOICE)||LA10_0==HALT||LA10_0==IF||LA10_0==SEMI||LA10_0==STAR||(LA10_0 >= TRUE && LA10_0 <= TRY)||LA10_0==UNTIL||LA10_0==WHILE) ) {
						alt10=1;
					}
					switch (alt10) {
						case 1 :
							// CtrlChecker.g:143:8: stat
							{
							_last = (CtrlTree)input.LT(1);
							pushFollow(FOLLOW_stat_in_stat808);
							stat57=stat();
							state._fsp--;

							 
							if ( _first_1==null ) _first_1 = (CtrlTree)stat57.getTree();

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
					// CtrlChecker.g:146:5: ^( CHOICE stat ( stat )* )
					{
					_last = (CtrlTree)input.LT(1);
					{
					CtrlTree _save_last_1 = _last;
					CtrlTree _first_1 = null;
					_last = (CtrlTree)input.LT(1);
					CHOICE58=(CtrlTree)match(input,CHOICE,FOLLOW_CHOICE_in_stat833); 

					if ( _first_0==null ) _first_0 = CHOICE58;
					 helper.startBranch(); 
					match(input, Token.DOWN, null); 
					_last = (CtrlTree)input.LT(1);
					pushFollow(FOLLOW_stat_in_stat851);
					stat59=stat();
					state._fsp--;

					 
					if ( _first_1==null ) _first_1 = (CtrlTree)stat59.getTree();

					// CtrlChecker.g:149:8: ( stat )*
					loop11:
					while (true) {
						int alt11=2;
						int LA11_0 = input.LA(1);
						if ( (LA11_0==ALAP||LA11_0==ATOM||(LA11_0 >= BECOMES && LA11_0 <= BLOCK)||(LA11_0 >= CALL && LA11_0 <= CHOICE)||LA11_0==HALT||LA11_0==IF||LA11_0==SEMI||LA11_0==STAR||(LA11_0 >= TRUE && LA11_0 <= TRY)||LA11_0==UNTIL||LA11_0==WHILE) ) {
							alt11=1;
						}

						switch (alt11) {
						case 1 :
							// CtrlChecker.g:149:10: stat
							{
							 helper.nextBranch(); 
							_last = (CtrlTree)input.LT(1);
							pushFollow(FOLLOW_stat_in_stat874);
							stat60=stat();
							state._fsp--;

							 
							if ( _first_1==null ) _first_1 = (CtrlTree)stat60.getTree();

							retval.tree = _first_0;
							if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
								retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

							}
							break;

						default :
							break loop11;
						}
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
				case 11 :
					// CtrlChecker.g:154:5: HALT
					{
					_last = (CtrlTree)input.LT(1);
					HALT61=(CtrlTree)match(input,HALT,FOLLOW_HALT_in_stat906); 
					 
					if ( _first_0==null ) _first_0 = HALT61;

					retval.tree = _first_0;
					if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
						retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

					}
					break;
				case 12 :
					// CtrlChecker.g:155:5: ^( STAR stat )
					{
					_last = (CtrlTree)input.LT(1);
					{
					CtrlTree _save_last_1 = _last;
					CtrlTree _first_1 = null;
					_last = (CtrlTree)input.LT(1);
					STAR62=(CtrlTree)match(input,STAR,FOLLOW_STAR_in_stat914); 

					if ( _first_0==null ) _first_0 = STAR62;
					 helper.startBranch(); 
					match(input, Token.DOWN, null); 
					_last = (CtrlTree)input.LT(1);
					pushFollow(FOLLOW_stat_in_stat932);
					stat63=stat();
					state._fsp--;

					 
					if ( _first_1==null ) _first_1 = (CtrlTree)stat63.getTree();

					 helper.endBranch(); 
					match(input, Token.UP, null); 
					_last = _save_last_1;
					}


					retval.tree = _first_0;
					if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
						retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

					}
					break;
				case 13 :
					// CtrlChecker.g:160:5: call
					{
					_last = (CtrlTree)input.LT(1);
					pushFollow(FOLLOW_call_in_stat954);
					call64=call();
					state._fsp--;

					 
					if ( _first_0==null ) _first_0 = (CtrlTree)call64.getTree();

					retval.tree = _first_0;
					if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
						retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

					}
					break;
				case 14 :
					// CtrlChecker.g:161:5: assign
					{
					_last = (CtrlTree)input.LT(1);
					pushFollow(FOLLOW_assign_in_stat960);
					assign65=assign();
					state._fsp--;

					 
					if ( _first_0==null ) _first_0 = (CtrlTree)assign65.getTree();

					retval.tree = _first_0;
					if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
						retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

					}
					break;
				case 15 :
					// CtrlChecker.g:162:5: TRUE
					{
					_last = (CtrlTree)input.LT(1);
					TRUE66=(CtrlTree)match(input,TRUE,FOLLOW_TRUE_in_stat966); 
					 
					if ( _first_0==null ) _first_0 = TRUE66;

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
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "call"
	// CtrlChecker.g:165:1: call : ^( CALL qual_id ( arg_list )? ) ;
	public final CtrlChecker.call_return call() throws RecognitionException {
		CtrlChecker.call_return retval = new CtrlChecker.call_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		CtrlTree _first_0 = null;
		CtrlTree _last = null;


		CtrlTree CALL67=null;
		TreeRuleReturnScope qual_id68 =null;
		TreeRuleReturnScope arg_list69 =null;

		CtrlTree CALL67_tree=null;

		try {
			// CtrlChecker.g:167:3: ( ^( CALL qual_id ( arg_list )? ) )
			// CtrlChecker.g:167:5: ^( CALL qual_id ( arg_list )? )
			{
			_last = (CtrlTree)input.LT(1);
			{
			CtrlTree _save_last_1 = _last;
			CtrlTree _first_1 = null;
			_last = (CtrlTree)input.LT(1);
			CALL67=(CtrlTree)match(input,CALL,FOLLOW_CALL_in_call984); 

			if ( _first_0==null ) _first_0 = CALL67;
			match(input, Token.DOWN, null); 
			_last = (CtrlTree)input.LT(1);
			pushFollow(FOLLOW_qual_id_in_call986);
			qual_id68=qual_id();
			state._fsp--;

			 
			if ( _first_1==null ) _first_1 = (CtrlTree)qual_id68.getTree();

			// CtrlChecker.g:167:20: ( arg_list )?
			int alt13=2;
			int LA13_0 = input.LA(1);
			if ( (LA13_0==ARGS) ) {
				alt13=1;
			}
			switch (alt13) {
				case 1 :
					// CtrlChecker.g:167:20: arg_list
					{
					_last = (CtrlTree)input.LT(1);
					pushFollow(FOLLOW_arg_list_in_call988);
					arg_list69=arg_list();
					state._fsp--;

					 
					if ( _first_1==null ) _first_1 = (CtrlTree)arg_list69.getTree();

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

			 helper.checkGroupCall(retval.tree); 
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
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "assign"
	// CtrlChecker.g:170:1: assign : ^( BECOMES ( var_decl | arg_list ) ^( CALL qual_id ( arg_list )? ) ) ;
	public final CtrlChecker.assign_return assign() throws RecognitionException {
		CtrlChecker.assign_return retval = new CtrlChecker.assign_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		CtrlTree _first_0 = null;
		CtrlTree _last = null;


		CtrlTree BECOMES70=null;
		CtrlTree CALL73=null;
		TreeRuleReturnScope var_decl71 =null;
		TreeRuleReturnScope arg_list72 =null;
		TreeRuleReturnScope qual_id74 =null;
		TreeRuleReturnScope arg_list75 =null;

		CtrlTree BECOMES70_tree=null;
		CtrlTree CALL73_tree=null;

		try {
			// CtrlChecker.g:172:3: ( ^( BECOMES ( var_decl | arg_list ) ^( CALL qual_id ( arg_list )? ) ) )
			// CtrlChecker.g:172:5: ^( BECOMES ( var_decl | arg_list ) ^( CALL qual_id ( arg_list )? ) )
			{
			_last = (CtrlTree)input.LT(1);
			{
			CtrlTree _save_last_1 = _last;
			CtrlTree _first_1 = null;
			_last = (CtrlTree)input.LT(1);
			BECOMES70=(CtrlTree)match(input,BECOMES,FOLLOW_BECOMES_in_assign1008); 

			if ( _first_0==null ) _first_0 = BECOMES70;
			match(input, Token.DOWN, null); 
			// CtrlChecker.g:172:15: ( var_decl | arg_list )
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
					// CtrlChecker.g:172:16: var_decl
					{
					_last = (CtrlTree)input.LT(1);
					pushFollow(FOLLOW_var_decl_in_assign1011);
					var_decl71=var_decl();
					state._fsp--;

					 
					if ( _first_1==null ) _first_1 = (CtrlTree)var_decl71.getTree();

					retval.tree = _first_0;
					if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
						retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

					}
					break;
				case 2 :
					// CtrlChecker.g:172:27: arg_list
					{
					_last = (CtrlTree)input.LT(1);
					pushFollow(FOLLOW_arg_list_in_assign1015);
					arg_list72=arg_list();
					state._fsp--;

					 
					if ( _first_1==null ) _first_1 = (CtrlTree)arg_list72.getTree();

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
			CALL73=(CtrlTree)match(input,CALL,FOLLOW_CALL_in_assign1019); 

			if ( _first_1==null ) _first_1 = CALL73;
			match(input, Token.DOWN, null); 
			_last = (CtrlTree)input.LT(1);
			pushFollow(FOLLOW_qual_id_in_assign1021);
			qual_id74=qual_id();
			state._fsp--;

			 
			if ( _first_2==null ) _first_2 = (CtrlTree)qual_id74.getTree();

			// CtrlChecker.g:172:52: ( arg_list )?
			int alt15=2;
			int LA15_0 = input.LA(1);
			if ( (LA15_0==ARGS) ) {
				alt15=1;
			}
			switch (alt15) {
				case 1 :
					// CtrlChecker.g:172:52: arg_list
					{
					_last = (CtrlTree)input.LT(1);
					pushFollow(FOLLOW_arg_list_in_assign1023);
					arg_list75=arg_list();
					state._fsp--;

					 
					if ( _first_2==null ) _first_2 = (CtrlTree)arg_list75.getTree();

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

			 helper.checkAssign(retval.tree); 
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
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "var_decl"
	// CtrlChecker.g:175:1: var_decl : ^( VAR type ( ID )+ ) ;
	public final CtrlChecker.var_decl_return var_decl() throws RecognitionException {
		CtrlChecker.var_decl_return retval = new CtrlChecker.var_decl_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		CtrlTree _first_0 = null;
		CtrlTree _last = null;


		CtrlTree VAR76=null;
		CtrlTree ID78=null;
		TreeRuleReturnScope type77 =null;

		CtrlTree VAR76_tree=null;
		CtrlTree ID78_tree=null;

		try {
			// CtrlChecker.g:176:2: ( ^( VAR type ( ID )+ ) )
			// CtrlChecker.g:176:4: ^( VAR type ( ID )+ )
			{
			_last = (CtrlTree)input.LT(1);
			{
			CtrlTree _save_last_1 = _last;
			CtrlTree _first_1 = null;
			_last = (CtrlTree)input.LT(1);
			VAR76=(CtrlTree)match(input,VAR,FOLLOW_VAR_in_var_decl1040); 

			if ( _first_0==null ) _first_0 = VAR76;
			match(input, Token.DOWN, null); 
			_last = (CtrlTree)input.LT(1);
			pushFollow(FOLLOW_type_in_var_decl1042);
			type77=type();
			state._fsp--;

			 
			if ( _first_1==null ) _first_1 = (CtrlTree)type77.getTree();

			// CtrlChecker.g:177:7: ( ID )+
			int cnt16=0;
			loop16:
			while (true) {
				int alt16=2;
				int LA16_0 = input.LA(1);
				if ( (LA16_0==ID) ) {
					alt16=1;
				}

				switch (alt16) {
				case 1 :
					// CtrlChecker.g:177:9: ID
					{
					_last = (CtrlTree)input.LT(1);
					ID78=(CtrlTree)match(input,ID,FOLLOW_ID_in_var_decl1052); 
					 
					if ( _first_1==null ) _first_1 = ID78;

					 helper.declareVar(ID78, (type77!=null?((CtrlTree)type77.getTree()):null)); 
					retval.tree = _first_0;
					if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
						retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

					}
					break;

				default :
					if ( cnt16 >= 1 ) break loop16;
					EarlyExitException eee = new EarlyExitException(16, input);
					throw eee;
				}
				cnt16++;
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
	// $ANTLR end "var_decl"


	public static class qual_id_return extends TreeRuleReturnScope {
		CtrlTree tree;
		@Override
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "qual_id"
	// CtrlChecker.g:183:1: qual_id : ^( ( ID | ANY | OTHER ) ID ) ;
	public final CtrlChecker.qual_id_return qual_id() throws RecognitionException {
		CtrlChecker.qual_id_return retval = new CtrlChecker.qual_id_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		CtrlTree _first_0 = null;
		CtrlTree _last = null;


		CtrlTree set79=null;
		CtrlTree ID80=null;

		CtrlTree set79_tree=null;
		CtrlTree ID80_tree=null;

		try {
			// CtrlChecker.g:184:3: ( ^( ( ID | ANY | OTHER ) ID ) )
			// CtrlChecker.g:184:5: ^( ( ID | ANY | OTHER ) ID )
			{
			_last = (CtrlTree)input.LT(1);
			{
			CtrlTree _save_last_1 = _last;
			CtrlTree _first_1 = null;
			set79=(CtrlTree)input.LT(1);
			if ( input.LA(1)==ANY||input.LA(1)==ID||input.LA(1)==OTHER ) {
				input.consume();
				state.errorRecovery=false;
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}

			if ( _first_0==null ) _first_0 = set79;
			match(input, Token.DOWN, null); 
			_last = (CtrlTree)input.LT(1);
			ID80=(CtrlTree)match(input,ID,FOLLOW_ID_in_qual_id1100); 
			 
			if ( _first_1==null ) _first_1 = ID80;

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
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "type"
	// CtrlChecker.g:189:1: type : ( NODE -> NODE | BOOL -> BOOL | STRING -> STRING | INT -> INT | REAL -> REAL );
	public final CtrlChecker.type_return type() throws RecognitionException {
		CtrlChecker.type_return retval = new CtrlChecker.type_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		CtrlTree _first_0 = null;
		CtrlTree _last = null;


		CtrlTree NODE81=null;
		CtrlTree BOOL82=null;
		CtrlTree STRING83=null;
		CtrlTree INT84=null;
		CtrlTree REAL85=null;

		CtrlTree NODE81_tree=null;
		CtrlTree BOOL82_tree=null;
		CtrlTree STRING83_tree=null;
		CtrlTree INT84_tree=null;
		CtrlTree REAL85_tree=null;
		RewriteRuleNodeStream stream_BOOL=new RewriteRuleNodeStream(adaptor,"token BOOL");
		RewriteRuleNodeStream stream_NODE=new RewriteRuleNodeStream(adaptor,"token NODE");
		RewriteRuleNodeStream stream_REAL=new RewriteRuleNodeStream(adaptor,"token REAL");
		RewriteRuleNodeStream stream_STRING=new RewriteRuleNodeStream(adaptor,"token STRING");
		RewriteRuleNodeStream stream_INT=new RewriteRuleNodeStream(adaptor,"token INT");

		try {
			// CtrlChecker.g:192:3: ( NODE -> NODE | BOOL -> BOOL | STRING -> STRING | INT -> INT | REAL -> REAL )
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
					// CtrlChecker.g:192:5: NODE
					{
					_last = (CtrlTree)input.LT(1);
					NODE81=(CtrlTree)match(input,NODE,FOLLOW_NODE_in_type1126); 
					 
					stream_NODE.add(NODE81);

					// AST REWRITE
					// elements: NODE
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CtrlTree)adaptor.nil();
					// 192:10: -> NODE
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
					// CtrlChecker.g:193:5: BOOL
					{
					_last = (CtrlTree)input.LT(1);
					BOOL82=(CtrlTree)match(input,BOOL,FOLLOW_BOOL_in_type1136); 
					 
					stream_BOOL.add(BOOL82);

					// AST REWRITE
					// elements: BOOL
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CtrlTree)adaptor.nil();
					// 193:10: -> BOOL
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
					// CtrlChecker.g:194:5: STRING
					{
					_last = (CtrlTree)input.LT(1);
					STRING83=(CtrlTree)match(input,STRING,FOLLOW_STRING_in_type1146); 
					 
					stream_STRING.add(STRING83);

					// AST REWRITE
					// elements: STRING
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CtrlTree)adaptor.nil();
					// 194:12: -> STRING
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
					// CtrlChecker.g:195:5: INT
					{
					_last = (CtrlTree)input.LT(1);
					INT84=(CtrlTree)match(input,INT,FOLLOW_INT_in_type1156); 
					 
					stream_INT.add(INT84);

					// AST REWRITE
					// elements: INT
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CtrlTree)adaptor.nil();
					// 195:9: -> INT
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
					// CtrlChecker.g:196:5: REAL
					{
					_last = (CtrlTree)input.LT(1);
					REAL85=(CtrlTree)match(input,REAL,FOLLOW_REAL_in_type1166); 
					 
					stream_REAL.add(REAL85);

					// AST REWRITE
					// elements: REAL
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CtrlTree)adaptor.nil();
					// 196:10: -> REAL
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
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "arg_list"
	// CtrlChecker.g:199:1: arg_list : ^( ARGS ( arg )* RPAR ) ;
	public final CtrlChecker.arg_list_return arg_list() throws RecognitionException {
		CtrlChecker.arg_list_return retval = new CtrlChecker.arg_list_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		CtrlTree _first_0 = null;
		CtrlTree _last = null;


		CtrlTree ARGS86=null;
		CtrlTree RPAR88=null;
		TreeRuleReturnScope arg87 =null;

		CtrlTree ARGS86_tree=null;
		CtrlTree RPAR88_tree=null;

		try {
			// CtrlChecker.g:200:3: ( ^( ARGS ( arg )* RPAR ) )
			// CtrlChecker.g:200:5: ^( ARGS ( arg )* RPAR )
			{
			_last = (CtrlTree)input.LT(1);
			{
			CtrlTree _save_last_1 = _last;
			CtrlTree _first_1 = null;
			_last = (CtrlTree)input.LT(1);
			ARGS86=(CtrlTree)match(input,ARGS,FOLLOW_ARGS_in_arg_list1184); 

			if ( _first_0==null ) _first_0 = ARGS86;
			match(input, Token.DOWN, null); 
			// CtrlChecker.g:200:12: ( arg )*
			loop18:
			while (true) {
				int alt18=2;
				int LA18_0 = input.LA(1);
				if ( ((LA18_0 >= ARG_CALL && LA18_0 <= ARG_WILD)) ) {
					alt18=1;
				}

				switch (alt18) {
				case 1 :
					// CtrlChecker.g:200:12: arg
					{
					_last = (CtrlTree)input.LT(1);
					pushFollow(FOLLOW_arg_in_arg_list1186);
					arg87=arg();
					state._fsp--;

					 
					if ( _first_1==null ) _first_1 = (CtrlTree)arg87.getTree();

					retval.tree = _first_0;
					if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
						retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

					}
					break;

				default :
					break loop18;
				}
			}

			_last = (CtrlTree)input.LT(1);
			RPAR88=(CtrlTree)match(input,RPAR,FOLLOW_RPAR_in_arg_list1189); 
			 
			if ( _first_1==null ) _first_1 = RPAR88;

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
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "arg"
	// CtrlChecker.g:203:1: arg : ( ^( ARG_OUT ID ) | ^( ARG_WILD ) | ^( ARG_ID ID ) | ^( ARG_LIT literal ) | ^( ARG_OP operator arg ( arg )? ) | ^( ARG_CALL ID arg_list ) );
	public final CtrlChecker.arg_return arg() throws RecognitionException {
		CtrlChecker.arg_return retval = new CtrlChecker.arg_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		CtrlTree _first_0 = null;
		CtrlTree _last = null;


		CtrlTree ARG_OUT89=null;
		CtrlTree ID90=null;
		CtrlTree ARG_WILD91=null;
		CtrlTree ARG_ID92=null;
		CtrlTree ID93=null;
		CtrlTree ARG_LIT94=null;
		CtrlTree ARG_OP96=null;
		CtrlTree ARG_CALL100=null;
		CtrlTree ID101=null;
		TreeRuleReturnScope literal95 =null;
		TreeRuleReturnScope operator97 =null;
		TreeRuleReturnScope arg98 =null;
		TreeRuleReturnScope arg99 =null;
		TreeRuleReturnScope arg_list102 =null;

		CtrlTree ARG_OUT89_tree=null;
		CtrlTree ID90_tree=null;
		CtrlTree ARG_WILD91_tree=null;
		CtrlTree ARG_ID92_tree=null;
		CtrlTree ID93_tree=null;
		CtrlTree ARG_LIT94_tree=null;
		CtrlTree ARG_OP96_tree=null;
		CtrlTree ARG_CALL100_tree=null;
		CtrlTree ID101_tree=null;

		try {
			// CtrlChecker.g:204:3: ( ^( ARG_OUT ID ) | ^( ARG_WILD ) | ^( ARG_ID ID ) | ^( ARG_LIT literal ) | ^( ARG_OP operator arg ( arg )? ) | ^( ARG_CALL ID arg_list ) )
			int alt20=6;
			switch ( input.LA(1) ) {
			case ARG_OUT:
				{
				alt20=1;
				}
				break;
			case ARG_WILD:
				{
				alt20=2;
				}
				break;
			case ARG_ID:
				{
				alt20=3;
				}
				break;
			case ARG_LIT:
				{
				alt20=4;
				}
				break;
			case ARG_OP:
				{
				alt20=5;
				}
				break;
			case ARG_CALL:
				{
				alt20=6;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 20, 0, input);
				throw nvae;
			}
			switch (alt20) {
				case 1 :
					// CtrlChecker.g:204:5: ^( ARG_OUT ID )
					{
					_last = (CtrlTree)input.LT(1);
					{
					CtrlTree _save_last_1 = _last;
					CtrlTree _first_1 = null;
					_last = (CtrlTree)input.LT(1);
					ARG_OUT89=(CtrlTree)match(input,ARG_OUT,FOLLOW_ARG_OUT_in_arg1205); 

					if ( _first_0==null ) _first_0 = ARG_OUT89;
					match(input, Token.DOWN, null); 
					_last = (CtrlTree)input.LT(1);
					ID90=(CtrlTree)match(input,ID,FOLLOW_ID_in_arg1207); 
					 
					if ( _first_1==null ) _first_1 = ID90;

					 helper.checkVarArg(ARG_OUT89); 
					match(input, Token.UP, null); 
					_last = _save_last_1;
					}


					retval.tree = _first_0;
					if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
						retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

					}
					break;
				case 2 :
					// CtrlChecker.g:205:5: ^( ARG_WILD )
					{
					_last = (CtrlTree)input.LT(1);
					{
					CtrlTree _save_last_1 = _last;
					CtrlTree _first_1 = null;
					_last = (CtrlTree)input.LT(1);
					ARG_WILD91=(CtrlTree)match(input,ARG_WILD,FOLLOW_ARG_WILD_in_arg1219); 

					if ( _first_0==null ) _first_0 = ARG_WILD91;
					 helper.checkDontCareArg(ARG_WILD91); 
					if ( input.LA(1)==Token.DOWN ) {
						match(input, Token.DOWN, null); 
						match(input, Token.UP, null); 
					}
					_last = _save_last_1;
					}


					retval.tree = _first_0;
					if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
						retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

					}
					break;
				case 3 :
					// CtrlChecker.g:206:5: ^( ARG_ID ID )
					{
					_last = (CtrlTree)input.LT(1);
					{
					CtrlTree _save_last_1 = _last;
					CtrlTree _first_1 = null;
					_last = (CtrlTree)input.LT(1);
					ARG_ID92=(CtrlTree)match(input,ARG_ID,FOLLOW_ARG_ID_in_arg1231); 

					if ( _first_0==null ) _first_0 = ARG_ID92;
					match(input, Token.DOWN, null); 
					_last = (CtrlTree)input.LT(1);
					ID93=(CtrlTree)match(input,ID,FOLLOW_ID_in_arg1233); 
					 
					if ( _first_1==null ) _first_1 = ID93;

					 helper.checkVarArg(ARG_ID92); 
					match(input, Token.UP, null); 
					_last = _save_last_1;
					}


					retval.tree = _first_0;
					if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
						retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

					}
					break;
				case 4 :
					// CtrlChecker.g:207:5: ^( ARG_LIT literal )
					{
					_last = (CtrlTree)input.LT(1);
					{
					CtrlTree _save_last_1 = _last;
					CtrlTree _first_1 = null;
					_last = (CtrlTree)input.LT(1);
					ARG_LIT94=(CtrlTree)match(input,ARG_LIT,FOLLOW_ARG_LIT_in_arg1245); 

					if ( _first_0==null ) _first_0 = ARG_LIT94;
					match(input, Token.DOWN, null); 
					_last = (CtrlTree)input.LT(1);
					pushFollow(FOLLOW_literal_in_arg1247);
					literal95=literal();
					state._fsp--;

					 
					if ( _first_1==null ) _first_1 = (CtrlTree)literal95.getTree();

					 helper.checkInArg(ARG_LIT94); 
					match(input, Token.UP, null); 
					_last = _save_last_1;
					}


					retval.tree = _first_0;
					if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
						retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

					}
					break;
				case 5 :
					// CtrlChecker.g:208:5: ^( ARG_OP operator arg ( arg )? )
					{
					_last = (CtrlTree)input.LT(1);
					{
					CtrlTree _save_last_1 = _last;
					CtrlTree _first_1 = null;
					_last = (CtrlTree)input.LT(1);
					ARG_OP96=(CtrlTree)match(input,ARG_OP,FOLLOW_ARG_OP_in_arg1259); 

					if ( _first_0==null ) _first_0 = ARG_OP96;
					match(input, Token.DOWN, null); 
					_last = (CtrlTree)input.LT(1);
					pushFollow(FOLLOW_operator_in_arg1261);
					operator97=operator();
					state._fsp--;

					 
					if ( _first_1==null ) _first_1 = (CtrlTree)operator97.getTree();

					_last = (CtrlTree)input.LT(1);
					pushFollow(FOLLOW_arg_in_arg1263);
					arg98=arg();
					state._fsp--;

					 
					if ( _first_1==null ) _first_1 = (CtrlTree)arg98.getTree();

					// CtrlChecker.g:208:28: ( arg )?
					int alt19=2;
					int LA19_0 = input.LA(1);
					if ( ((LA19_0 >= ARG_CALL && LA19_0 <= ARG_WILD)) ) {
						alt19=1;
					}
					switch (alt19) {
						case 1 :
							// CtrlChecker.g:208:28: arg
							{
							_last = (CtrlTree)input.LT(1);
							pushFollow(FOLLOW_arg_in_arg1265);
							arg99=arg();
							state._fsp--;

							 
							if ( _first_1==null ) _first_1 = (CtrlTree)arg99.getTree();

							retval.tree = _first_0;
							if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
								retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

							}
							break;

					}

					 helper.checkInArg(ARG_OP96); 
					match(input, Token.UP, null); 
					_last = _save_last_1;
					}


					retval.tree = _first_0;
					if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
						retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

					}
					break;
				case 6 :
					// CtrlChecker.g:209:5: ^( ARG_CALL ID arg_list )
					{
					_last = (CtrlTree)input.LT(1);
					{
					CtrlTree _save_last_1 = _last;
					CtrlTree _first_1 = null;
					_last = (CtrlTree)input.LT(1);
					ARG_CALL100=(CtrlTree)match(input,ARG_CALL,FOLLOW_ARG_CALL_in_arg1278); 

					if ( _first_0==null ) _first_0 = ARG_CALL100;
					match(input, Token.DOWN, null); 
					_last = (CtrlTree)input.LT(1);
					ID101=(CtrlTree)match(input,ID,FOLLOW_ID_in_arg1280); 
					 
					if ( _first_1==null ) _first_1 = ID101;

					_last = (CtrlTree)input.LT(1);
					pushFollow(FOLLOW_arg_list_in_arg1282);
					arg_list102=arg_list();
					state._fsp--;

					 
					if ( _first_1==null ) _first_1 = (CtrlTree)arg_list102.getTree();

					 helper.checkInArg(ARG_CALL100); 
					match(input, Token.UP, null); 
					_last = _save_last_1;
					}


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
	// $ANTLR end "arg"


	public static class literal_return extends TreeRuleReturnScope {
		CtrlTree tree;
		@Override
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "literal"
	// CtrlChecker.g:212:1: literal : ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT );
	public final CtrlChecker.literal_return literal() throws RecognitionException {
		CtrlChecker.literal_return retval = new CtrlChecker.literal_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		CtrlTree _first_0 = null;
		CtrlTree _last = null;


		CtrlTree set103=null;

		CtrlTree set103_tree=null;

		try {
			// CtrlChecker.g:213:3: ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT )
			// CtrlChecker.g:
			{
			_last = (CtrlTree)input.LT(1);
			set103=(CtrlTree)input.LT(1);
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


	public static class operator_return extends TreeRuleReturnScope {
		CtrlTree tree;
		@Override
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "operator"
	// CtrlChecker.g:217:1: operator : ( LANGLE | RANGLE | LEQ | GEQ | EQ | NEQ | PLUS | MINUS | PERCENT | ASTERISK | SLASH | AMP | BAR | NOT | LPAR );
	public final CtrlChecker.operator_return operator() throws RecognitionException {
		CtrlChecker.operator_return retval = new CtrlChecker.operator_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		CtrlTree _first_0 = null;
		CtrlTree _last = null;


		CtrlTree set104=null;

		CtrlTree set104_tree=null;

		try {
			// CtrlChecker.g:218:3: ( LANGLE | RANGLE | LEQ | GEQ | EQ | NEQ | PLUS | MINUS | PERCENT | ASTERISK | SLASH | AMP | BAR | NOT | LPAR )
			// CtrlChecker.g:
			{
			_last = (CtrlTree)input.LT(1);
			set104=(CtrlTree)input.LT(1);
			if ( input.LA(1)==AMP||input.LA(1)==ASTERISK||input.LA(1)==BAR||input.LA(1)==EQ||input.LA(1)==GEQ||input.LA(1)==LANGLE||(input.LA(1) >= LEQ && input.LA(1) <= LPAR)||input.LA(1)==MINUS||input.LA(1)==NEQ||input.LA(1)==NOT||(input.LA(1) >= PERCENT && input.LA(1) <= PLUS)||input.LA(1)==RANGLE||input.LA(1)==SLASH ) {
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
	// $ANTLR end "operator"

	// Delegated rules



	public static final BitSet FOLLOW_PROGRAM_in_program61 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_package_decl_in_program63 = new BitSet(new long[]{0x0000040000000000L});
	public static final BitSet FOLLOW_imports_in_program65 = new BitSet(new long[]{0x0000001000000000L});
	public static final BitSet FOLLOW_functions_in_program67 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
	public static final BitSet FOLLOW_recipes_in_program69 = new BitSet(new long[]{0x0000000000040000L});
	public static final BitSet FOLLOW_block_in_program71 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_PACKAGE_in_package_decl88 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_qual_id_in_package_decl90 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
	public static final BitSet FOLLOW_SEMI_in_package_decl92 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_IMPORTS_in_imports122 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_import_decl_in_imports124 = new BitSet(new long[]{0x0000020000000008L});
	public static final BitSet FOLLOW_IMPORT_in_import_decl141 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_qual_id_in_import_decl143 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
	public static final BitSet FOLLOW_SEMI_in_import_decl145 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_RECIPES_in_recipes175 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_recipe_in_recipes177 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000200L});
	public static final BitSet FOLLOW_RECIPE_in_recipe194 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_recipe213 = new BitSet(new long[]{0x4000000000000000L});
	public static final BitSet FOLLOW_PARS_in_recipe216 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_par_decl_in_recipe218 = new BitSet(new long[]{0x2000000000000008L});
	public static final BitSet FOLLOW_INT_LIT_in_recipe222 = new BitSet(new long[]{0x0000000000040000L});
	public static final BitSet FOLLOW_block_in_recipe232 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_FUNCTIONS_in_functions264 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_function_in_functions266 = new BitSet(new long[]{0x0000000800000008L});
	public static final BitSet FOLLOW_FUNCTION_in_function283 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_function301 = new BitSet(new long[]{0x4000000000000000L});
	public static final BitSet FOLLOW_PARS_in_function304 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_par_decl_in_function306 = new BitSet(new long[]{0x2000000000000008L});
	public static final BitSet FOLLOW_block_in_function317 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_PAR_in_par_decl350 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_OUT_in_par_decl352 = new BitSet(new long[]{0x0040080000080000L,0x0000000000020080L});
	public static final BitSet FOLLOW_type_in_par_decl355 = new BitSet(new long[]{0x0000008000000000L});
	public static final BitSet FOLLOW_ID_in_par_decl357 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_BLOCK_in_block381 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_stat_in_block399 = new BitSet(new long[]{0x0000014000C68018L,0x0000000001591000L});
	public static final BitSet FOLLOW_block_in_stat429 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_SEMI_in_stat436 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_var_decl_in_stat438 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_SEMI_in_stat446 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_stat_in_stat448 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_ALAP_in_stat457 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_stat_in_stat476 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_ATOM_in_stat524 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_stat_in_stat526 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_WHILE_in_stat535 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_stat_in_stat544 = new BitSet(new long[]{0x0000014000C68010L,0x0000000001591000L});
	public static final BitSet FOLLOW_stat_in_stat562 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_UNTIL_in_stat611 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_stat_in_stat620 = new BitSet(new long[]{0x0000014000C68010L,0x0000000001591000L});
	public static final BitSet FOLLOW_stat_in_stat638 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_TRY_in_stat687 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_stat_in_stat705 = new BitSet(new long[]{0x0000014000C68018L,0x0000000001591000L});
	public static final BitSet FOLLOW_stat_in_stat727 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_IF_in_stat761 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_stat_in_stat780 = new BitSet(new long[]{0x0000014000C68010L,0x0000000001591000L});
	public static final BitSet FOLLOW_stat_in_stat790 = new BitSet(new long[]{0x0000014000C68018L,0x0000000001591000L});
	public static final BitSet FOLLOW_stat_in_stat808 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_CHOICE_in_stat833 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_stat_in_stat851 = new BitSet(new long[]{0x0000014000C68018L,0x0000000001591000L});
	public static final BitSet FOLLOW_stat_in_stat874 = new BitSet(new long[]{0x0000014000C68018L,0x0000000001591000L});
	public static final BitSet FOLLOW_HALT_in_stat906 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_STAR_in_stat914 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_stat_in_stat932 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_call_in_stat954 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_assign_in_stat960 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TRUE_in_stat966 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_CALL_in_call984 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_qual_id_in_call986 = new BitSet(new long[]{0x0000000000000088L});
	public static final BitSet FOLLOW_arg_list_in_call988 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_BECOMES_in_assign1008 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_var_decl_in_assign1011 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_arg_list_in_assign1015 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_CALL_in_assign1019 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_qual_id_in_assign1021 = new BitSet(new long[]{0x0000000000000088L});
	public static final BitSet FOLLOW_arg_list_in_assign1023 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_VAR_in_var_decl1040 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_type_in_var_decl1042 = new BitSet(new long[]{0x0000008000000000L});
	public static final BitSet FOLLOW_ID_in_var_decl1052 = new BitSet(new long[]{0x0000008000000008L});
	public static final BitSet FOLLOW_set_in_qual_id1092 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_qual_id1100 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_NODE_in_type1126 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_BOOL_in_type1136 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_STRING_in_type1146 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_INT_in_type1156 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_REAL_in_type1166 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ARGS_in_arg_list1184 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_arg_in_arg_list1186 = new BitSet(new long[]{0x0000000000003F00L,0x0000000000000800L});
	public static final BitSet FOLLOW_RPAR_in_arg_list1189 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_ARG_OUT_in_arg1205 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_arg1207 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_ARG_WILD_in_arg1219 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ARG_ID_in_arg1231 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_arg1233 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_ARG_LIT_in_arg1245 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_literal_in_arg1247 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_ARG_OP_in_arg1259 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_operator_in_arg1261 = new BitSet(new long[]{0x0000000000003F00L});
	public static final BitSet FOLLOW_arg_in_arg1263 = new BitSet(new long[]{0x0000000000003F08L});
	public static final BitSet FOLLOW_arg_in_arg1265 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_ARG_CALL_in_arg1278 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_arg1280 = new BitSet(new long[]{0x0000000000000080L});
	public static final BitSet FOLLOW_arg_list_in_arg1282 = new BitSet(new long[]{0x0000000000000008L});
}
