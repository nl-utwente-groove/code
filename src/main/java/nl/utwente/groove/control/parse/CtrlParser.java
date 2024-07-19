// $ANTLR 3.5.3 .\\Ctrl.g 2024-07-19 14:27:00

package nl.utwente.groove.control.parse;
import nl.utwente.groove.control.*;
import nl.utwente.groove.util.antlr.*;
import java.util.LinkedList;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.antlr.runtime.tree.*;


@SuppressWarnings("all")
public class CtrlParser extends Parser {
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
	public Parser[] getDelegates() {
		return new Parser[] {};
	}

	// delegators


	public CtrlParser(TokenStream input) {
		this(input, new RecognizerSharedState());
	}
	public CtrlParser(TokenStream input, RecognizerSharedState state) {
		super(input, state);
	}

	protected TreeAdaptor adaptor = new CommonTreeAdaptor();

	public void setTreeAdaptor(TreeAdaptor adaptor) {
		this.adaptor = adaptor;
	}
	public TreeAdaptor getTreeAdaptor() {
		return adaptor;
	}
	@Override public String[] getTokenNames() { return CtrlParser.tokenNames; }
	@Override public String getGrammarFileName() { return ".\\Ctrl.g"; }


	    /** Helper class to convert AST trees to namespace. */
	    private CtrlHelper helper;
	    
	    public void displayRecognitionError(String[] tokenNames,
	            RecognitionException e) {
	        String hdr = getErrorHeader(e);
	        String msg = getErrorMessage(e, tokenNames);
	        this.helper.addError(hdr + " " + msg, e.line, e.charPositionInLine);
	    }

	    public void initialise(ParseInfo namespace) {
	        this.helper = new CtrlHelper((Namespace) namespace);
	    }


	public static class program_return extends ParserRuleReturnScope {
		CtrlTree tree;
		@Override
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "program"
	// .\\Ctrl.g:79:1: program : package_decl ( import_decl )* ( function | recipe | stat )* EOF -> ^( PROGRAM package_decl ^( IMPORTS ( import_decl )* ) ^( FUNCTIONS ( function )* ) ^( RECIPES ( recipe )* ) ^( BLOCK ( stat )* ) ) ;
	public final CtrlParser.program_return program() throws RecognitionException {
		CtrlParser.program_return retval = new CtrlParser.program_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		Token EOF6=null;
		ParserRuleReturnScope package_decl1 =null;
		ParserRuleReturnScope import_decl2 =null;
		ParserRuleReturnScope function3 =null;
		ParserRuleReturnScope recipe4 =null;
		ParserRuleReturnScope stat5 =null;

		CtrlTree EOF6_tree=null;
		RewriteRuleTokenStream stream_EOF=new RewriteRuleTokenStream(adaptor,"token EOF");
		RewriteRuleSubtreeStream stream_stat=new RewriteRuleSubtreeStream(adaptor,"rule stat");
		RewriteRuleSubtreeStream stream_import_decl=new RewriteRuleSubtreeStream(adaptor,"rule import_decl");
		RewriteRuleSubtreeStream stream_function=new RewriteRuleSubtreeStream(adaptor,"rule function");
		RewriteRuleSubtreeStream stream_recipe=new RewriteRuleSubtreeStream(adaptor,"rule recipe");
		RewriteRuleSubtreeStream stream_package_decl=new RewriteRuleSubtreeStream(adaptor,"rule package_decl");

		 helper.clearErrors(); 
		try {
			// .\\Ctrl.g:82:3: ( package_decl ( import_decl )* ( function | recipe | stat )* EOF -> ^( PROGRAM package_decl ^( IMPORTS ( import_decl )* ) ^( FUNCTIONS ( function )* ) ^( RECIPES ( recipe )* ) ^( BLOCK ( stat )* ) ) )
			// .\\Ctrl.g:86:5: package_decl ( import_decl )* ( function | recipe | stat )* EOF
			{
			pushFollow(FOLLOW_package_decl_in_program191);
			package_decl1=package_decl();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_package_decl.add(package_decl1.getTree());
			// .\\Ctrl.g:87:5: ( import_decl )*
			loop1:
			while (true) {
				int alt1=2;
				int LA1_0 = input.LA(1);
				if ( (LA1_0==IMPORT) ) {
					alt1=1;
				}

				switch (alt1) {
				case 1 :
					// .\\Ctrl.g:87:5: import_decl
					{
					pushFollow(FOLLOW_import_decl_in_program197);
					import_decl2=import_decl();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_import_decl.add(import_decl2.getTree());
					}
					break;

				default :
					break loop1;
				}
			}

			// .\\Ctrl.g:88:5: ( function | recipe | stat )*
			loop2:
			while (true) {
				int alt2=4;
				switch ( input.LA(1) ) {
				case FUNCTION:
					{
					alt2=1;
					}
					break;
				case RECIPE:
					{
					alt2=2;
					}
					break;
				case ALAP:
				case ANY:
				case ASTERISK:
				case BOOL:
				case CHOICE:
				case DO:
				case HALT:
				case ID:
				case IF:
				case INT:
				case LANGLE:
				case LCURLY:
				case LPAR:
				case NODE:
				case OTHER:
				case REAL:
				case SHARP:
				case STRING:
				case TRY:
				case UNTIL:
				case WHILE:
					{
					alt2=3;
					}
					break;
				}
				switch (alt2) {
				case 1 :
					// .\\Ctrl.g:88:6: function
					{
					pushFollow(FOLLOW_function_in_program205);
					function3=function();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_function.add(function3.getTree());
					}
					break;
				case 2 :
					// .\\Ctrl.g:88:15: recipe
					{
					pushFollow(FOLLOW_recipe_in_program207);
					recipe4=recipe();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_recipe.add(recipe4.getTree());
					}
					break;
				case 3 :
					// .\\Ctrl.g:88:22: stat
					{
					pushFollow(FOLLOW_stat_in_program209);
					stat5=stat();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_stat.add(stat5.getTree());
					}
					break;

				default :
					break loop2;
				}
			}

			EOF6=(Token)match(input,EOF,FOLLOW_EOF_in_program213); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_EOF.add(EOF6);

			if ( state.backtracking==0 ) { helper.checkEOF(EOF6_tree); }

			// AST REWRITE
			// elements: function, recipe, stat, package_decl, import_decl
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CtrlTree)adaptor.nil();
			// 90:5: -> ^( PROGRAM package_decl ^( IMPORTS ( import_decl )* ) ^( FUNCTIONS ( function )* ) ^( RECIPES ( recipe )* ) ^( BLOCK ( stat )* ) )
			{
				// .\\Ctrl.g:90:8: ^( PROGRAM package_decl ^( IMPORTS ( import_decl )* ) ^( FUNCTIONS ( function )* ) ^( RECIPES ( recipe )* ) ^( BLOCK ( stat )* ) )
				{
				CtrlTree root_1 = (CtrlTree)adaptor.nil();
				root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(PROGRAM, "PROGRAM"), root_1);
				adaptor.addChild(root_1, stream_package_decl.nextTree());
				// .\\Ctrl.g:92:11: ^( IMPORTS ( import_decl )* )
				{
				CtrlTree root_2 = (CtrlTree)adaptor.nil();
				root_2 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(IMPORTS, "IMPORTS"), root_2);
				// .\\Ctrl.g:92:21: ( import_decl )*
				while ( stream_import_decl.hasNext() ) {
					adaptor.addChild(root_2, stream_import_decl.nextTree());
				}
				stream_import_decl.reset();

				adaptor.addChild(root_1, root_2);
				}

				// .\\Ctrl.g:93:11: ^( FUNCTIONS ( function )* )
				{
				CtrlTree root_2 = (CtrlTree)adaptor.nil();
				root_2 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(FUNCTIONS, "FUNCTIONS"), root_2);
				// .\\Ctrl.g:93:23: ( function )*
				while ( stream_function.hasNext() ) {
					adaptor.addChild(root_2, stream_function.nextTree());
				}
				stream_function.reset();

				adaptor.addChild(root_1, root_2);
				}

				// .\\Ctrl.g:94:11: ^( RECIPES ( recipe )* )
				{
				CtrlTree root_2 = (CtrlTree)adaptor.nil();
				root_2 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(RECIPES, "RECIPES"), root_2);
				// .\\Ctrl.g:94:21: ( recipe )*
				while ( stream_recipe.hasNext() ) {
					adaptor.addChild(root_2, stream_recipe.nextTree());
				}
				stream_recipe.reset();

				adaptor.addChild(root_1, root_2);
				}

				// .\\Ctrl.g:95:11: ^( BLOCK ( stat )* )
				{
				CtrlTree root_2 = (CtrlTree)adaptor.nil();
				root_2 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(BLOCK, "BLOCK"), root_2);
				// .\\Ctrl.g:95:19: ( stat )*
				while ( stream_stat.hasNext() ) {
					adaptor.addChild(root_2, stream_stat.nextTree());
				}
				stream_stat.reset();

				adaptor.addChild(root_1, root_2);
				}

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
			if ( state.backtracking==0 ) { helper.declareProgram(retval.tree); }
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "program"


	public static class package_decl_return extends ParserRuleReturnScope {
		CtrlTree tree;
		@Override
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "package_decl"
	// .\\Ctrl.g:100:1: package_decl : (key= PACKAGE qual_name[false] close= SEMI -> ^( PACKAGE[$key] qual_name SEMI[$close] ) | ->) ;
	public final CtrlParser.package_decl_return package_decl() throws RecognitionException {
		CtrlParser.package_decl_return retval = new CtrlParser.package_decl_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		Token key=null;
		Token close=null;
		ParserRuleReturnScope qual_name7 =null;

		CtrlTree key_tree=null;
		CtrlTree close_tree=null;
		RewriteRuleTokenStream stream_PACKAGE=new RewriteRuleTokenStream(adaptor,"token PACKAGE");
		RewriteRuleTokenStream stream_SEMI=new RewriteRuleTokenStream(adaptor,"token SEMI");
		RewriteRuleSubtreeStream stream_qual_name=new RewriteRuleSubtreeStream(adaptor,"rule qual_name");

		try {
			// .\\Ctrl.g:101:3: ( (key= PACKAGE qual_name[false] close= SEMI -> ^( PACKAGE[$key] qual_name SEMI[$close] ) | ->) )
			// .\\Ctrl.g:103:5: (key= PACKAGE qual_name[false] close= SEMI -> ^( PACKAGE[$key] qual_name SEMI[$close] ) | ->)
			{
			// .\\Ctrl.g:103:5: (key= PACKAGE qual_name[false] close= SEMI -> ^( PACKAGE[$key] qual_name SEMI[$close] ) | ->)
			int alt3=2;
			int LA3_0 = input.LA(1);
			if ( (LA3_0==PACKAGE) ) {
				alt3=1;
			}
			else if ( (LA3_0==EOF||LA3_0==ALAP||LA3_0==ANY||LA3_0==ASTERISK||LA3_0==BOOL||LA3_0==CHOICE||LA3_0==DO||LA3_0==FUNCTION||(LA3_0 >= HALT && LA3_0 <= IMPORT)||LA3_0==INT||(LA3_0 >= LANGLE && LA3_0 <= LCURLY)||LA3_0==LPAR||LA3_0==NODE||LA3_0==OTHER||LA3_0==REAL||LA3_0==RECIPE||LA3_0==SHARP||LA3_0==STRING||LA3_0==TRY||LA3_0==UNTIL||LA3_0==WHILE) ) {
				alt3=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 3, 0, input);
				throw nvae;
			}

			switch (alt3) {
				case 1 :
					// .\\Ctrl.g:103:7: key= PACKAGE qual_name[false] close= SEMI
					{
					key=(Token)match(input,PACKAGE,FOLLOW_PACKAGE_in_package_decl350); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_PACKAGE.add(key);

					pushFollow(FOLLOW_qual_name_in_package_decl352);
					qual_name7=qual_name(false);
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_qual_name.add(qual_name7.getTree());
					close=(Token)match(input,SEMI,FOLLOW_SEMI_in_package_decl357); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_SEMI.add(close);

					if ( state.backtracking==0 ) { helper.setPackage((qual_name7!=null?((CtrlTree)qual_name7.getTree()):null)); }

					// AST REWRITE
					// elements: qual_name, SEMI, PACKAGE
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CtrlTree)adaptor.nil();
					// 105:7: -> ^( PACKAGE[$key] qual_name SEMI[$close] )
					{
						// .\\Ctrl.g:105:10: ^( PACKAGE[$key] qual_name SEMI[$close] )
						{
						CtrlTree root_1 = (CtrlTree)adaptor.nil();
						root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(PACKAGE, key), root_1);
						adaptor.addChild(root_1, stream_qual_name.nextTree());
						adaptor.addChild(root_1, (CtrlTree)adaptor.create(SEMI, close));
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// .\\Ctrl.g:106:7: 
					{

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CtrlTree)adaptor.nil();
					// 106:7: ->
					{
						adaptor.addChild(root_0,  helper.emptyPackage() );
					}


					retval.tree = root_0;
					}

					}
					break;

			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "package_decl"


	public static class import_decl_return extends ParserRuleReturnScope {
		CtrlTree tree;
		@Override
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "import_decl"
	// .\\Ctrl.g:111:1: import_decl : IMPORT ^ qual_name[false] SEMI ;
	public final CtrlParser.import_decl_return import_decl() throws RecognitionException {
		CtrlParser.import_decl_return retval = new CtrlParser.import_decl_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		Token IMPORT8=null;
		Token SEMI10=null;
		ParserRuleReturnScope qual_name9 =null;

		CtrlTree IMPORT8_tree=null;
		CtrlTree SEMI10_tree=null;

		try {
			// .\\Ctrl.g:112:3: ( IMPORT ^ qual_name[false] SEMI )
			// .\\Ctrl.g:114:5: IMPORT ^ qual_name[false] SEMI
			{
			root_0 = (CtrlTree)adaptor.nil();


			IMPORT8=(Token)match(input,IMPORT,FOLLOW_IMPORT_in_import_decl424); if (state.failed) return retval;
			if ( state.backtracking==0 ) {
			IMPORT8_tree = (CtrlTree)adaptor.create(IMPORT8);
			root_0 = (CtrlTree)adaptor.becomeRoot(IMPORT8_tree, root_0);
			}

			pushFollow(FOLLOW_qual_name_in_import_decl427);
			qual_name9=qual_name(false);
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) adaptor.addChild(root_0, qual_name9.getTree());

			SEMI10=(Token)match(input,SEMI,FOLLOW_SEMI_in_import_decl430); if (state.failed) return retval;
			if ( state.backtracking==0 ) {
			SEMI10_tree = (CtrlTree)adaptor.create(SEMI10);
			adaptor.addChild(root_0, SEMI10_tree);
			}

			if ( state.backtracking==0 ) { helper.addImport((qual_name9!=null?((CtrlTree)qual_name9.getTree()):null));
			    }
			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "import_decl"


	public static class qual_name_return extends ParserRuleReturnScope {
		CtrlTree tree;
		@Override
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "qual_name"
	// .\\Ctrl.g:120:1: qual_name[boolean any] : ( ID ( DOT rest= qual_name[any] )? ->|{...}? ( ASTERISK DOT )? ( ANY ->| OTHER ->) );
	public final CtrlParser.qual_name_return qual_name(boolean any) throws RecognitionException {
		CtrlParser.qual_name_return retval = new CtrlParser.qual_name_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		Token ID11=null;
		Token DOT12=null;
		Token ASTERISK13=null;
		Token DOT14=null;
		Token ANY15=null;
		Token OTHER16=null;
		ParserRuleReturnScope rest =null;

		CtrlTree ID11_tree=null;
		CtrlTree DOT12_tree=null;
		CtrlTree ASTERISK13_tree=null;
		CtrlTree DOT14_tree=null;
		CtrlTree ANY15_tree=null;
		CtrlTree OTHER16_tree=null;
		RewriteRuleTokenStream stream_OTHER=new RewriteRuleTokenStream(adaptor,"token OTHER");
		RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
		RewriteRuleTokenStream stream_ASTERISK=new RewriteRuleTokenStream(adaptor,"token ASTERISK");
		RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
		RewriteRuleTokenStream stream_ANY=new RewriteRuleTokenStream(adaptor,"token ANY");
		RewriteRuleSubtreeStream stream_qual_name=new RewriteRuleSubtreeStream(adaptor,"rule qual_name");

		try {
			// .\\Ctrl.g:121:3: ( ID ( DOT rest= qual_name[any] )? ->|{...}? ( ASTERISK DOT )? ( ANY ->| OTHER ->) )
			int alt7=2;
			int LA7_0 = input.LA(1);
			if ( (LA7_0==ID) ) {
				alt7=1;
			}
			else if ( (LA7_0==ANY||LA7_0==ASTERISK||LA7_0==OTHER) ) {
				alt7=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 7, 0, input);
				throw nvae;
			}

			switch (alt7) {
				case 1 :
					// .\\Ctrl.g:125:5: ID ( DOT rest= qual_name[any] )?
					{
					ID11=(Token)match(input,ID,FOLLOW_ID_in_qual_name472); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_ID.add(ID11);

					// .\\Ctrl.g:125:8: ( DOT rest= qual_name[any] )?
					int alt4=2;
					int LA4_0 = input.LA(1);
					if ( (LA4_0==DOT) ) {
						alt4=1;
					}
					switch (alt4) {
						case 1 :
							// .\\Ctrl.g:125:10: DOT rest= qual_name[any]
							{
							DOT12=(Token)match(input,DOT,FOLLOW_DOT_in_qual_name476); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_DOT.add(DOT12);

							pushFollow(FOLLOW_qual_name_in_qual_name480);
							rest=qual_name(any);
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_qual_name.add(rest.getTree());
							}
							break;

					}


					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CtrlTree)adaptor.nil();
					// 126:22: ->
					{
						adaptor.addChild(root_0,  helper.toQualName(ID11, (rest!=null?((CtrlTree)rest.getTree()):null)) );
					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// .\\Ctrl.g:127:5: {...}? ( ASTERISK DOT )? ( ANY ->| OTHER ->)
					{
					if ( !(( any )) ) {
						if (state.backtracking>0) {state.failed=true; return retval;}
						throw new FailedPredicateException(input, "qual_name", " any ");
					}
					// .\\Ctrl.g:127:14: ( ASTERISK DOT )?
					int alt5=2;
					int LA5_0 = input.LA(1);
					if ( (LA5_0==ASTERISK) ) {
						alt5=1;
					}
					switch (alt5) {
						case 1 :
							// .\\Ctrl.g:127:16: ASTERISK DOT
							{
							ASTERISK13=(Token)match(input,ASTERISK,FOLLOW_ASTERISK_in_qual_name519); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_ASTERISK.add(ASTERISK13);

							DOT14=(Token)match(input,DOT,FOLLOW_DOT_in_qual_name521); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_DOT.add(DOT14);

							}
							break;

					}

					// .\\Ctrl.g:128:14: ( ANY ->| OTHER ->)
					int alt6=2;
					int LA6_0 = input.LA(1);
					if ( (LA6_0==ANY) ) {
						alt6=1;
					}
					else if ( (LA6_0==OTHER) ) {
						alt6=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						NoViableAltException nvae =
							new NoViableAltException("", 6, 0, input);
						throw nvae;
					}

					switch (alt6) {
						case 1 :
							// .\\Ctrl.g:128:16: ANY
							{
							ANY15=(Token)match(input,ANY,FOLLOW_ANY_in_qual_name541); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_ANY.add(ANY15);


							// AST REWRITE
							// elements: 
							// token labels: 
							// rule labels: retval
							// token list labels: 
							// rule list labels: 
							// wildcard labels: 
							if ( state.backtracking==0 ) {
							retval.tree = root_0;
							RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

							root_0 = (CtrlTree)adaptor.nil();
							// 128:22: ->
							{
								adaptor.addChild(root_0,  helper.toQualName(ASTERISK13, ANY15) );
							}


							retval.tree = root_0;
							}

							}
							break;
						case 2 :
							// .\\Ctrl.g:129:16: OTHER
							{
							OTHER16=(Token)match(input,OTHER,FOLLOW_OTHER_in_qual_name564); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_OTHER.add(OTHER16);


							// AST REWRITE
							// elements: 
							// token labels: 
							// rule labels: retval
							// token list labels: 
							// rule list labels: 
							// wildcard labels: 
							if ( state.backtracking==0 ) {
							retval.tree = root_0;
							RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

							root_0 = (CtrlTree)adaptor.nil();
							// 129:22: ->
							{
								adaptor.addChild(root_0,  helper.toQualName(ASTERISK13, OTHER16) );
							}


							retval.tree = root_0;
							}

							}
							break;

					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "qual_name"


	public static class recipe_return extends ParserRuleReturnScope {
		CtrlTree tree;
		@Override
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "recipe"
	// .\\Ctrl.g:136:1: recipe : RECIPE ^ ID par_list ( PRIORITY ! INT_LIT )? block ;
	public final CtrlParser.recipe_return recipe() throws RecognitionException {
		CtrlParser.recipe_return retval = new CtrlParser.recipe_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		Token RECIPE17=null;
		Token ID18=null;
		Token PRIORITY20=null;
		Token INT_LIT21=null;
		ParserRuleReturnScope par_list19 =null;
		ParserRuleReturnScope block22 =null;

		CtrlTree RECIPE17_tree=null;
		CtrlTree ID18_tree=null;
		CtrlTree PRIORITY20_tree=null;
		CtrlTree INT_LIT21_tree=null;

		try {
			// .\\Ctrl.g:137:3: ( RECIPE ^ ID par_list ( PRIORITY ! INT_LIT )? block )
			// .\\Ctrl.g:144:5: RECIPE ^ ID par_list ( PRIORITY ! INT_LIT )? block
			{
			root_0 = (CtrlTree)adaptor.nil();


			RECIPE17=(Token)match(input,RECIPE,FOLLOW_RECIPE_in_recipe633); if (state.failed) return retval;
			if ( state.backtracking==0 ) {
			RECIPE17_tree = (CtrlTree)adaptor.create(RECIPE17);
			root_0 = (CtrlTree)adaptor.becomeRoot(RECIPE17_tree, root_0);
			}

			ID18=(Token)match(input,ID,FOLLOW_ID_in_recipe636); if (state.failed) return retval;
			if ( state.backtracking==0 ) {
			ID18_tree = (CtrlTree)adaptor.create(ID18);
			adaptor.addChild(root_0, ID18_tree);
			}

			pushFollow(FOLLOW_par_list_in_recipe638);
			par_list19=par_list();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) adaptor.addChild(root_0, par_list19.getTree());

			// .\\Ctrl.g:144:25: ( PRIORITY ! INT_LIT )?
			int alt8=2;
			int LA8_0 = input.LA(1);
			if ( (LA8_0==PRIORITY) ) {
				alt8=1;
			}
			switch (alt8) {
				case 1 :
					// .\\Ctrl.g:144:26: PRIORITY ! INT_LIT
					{
					PRIORITY20=(Token)match(input,PRIORITY,FOLLOW_PRIORITY_in_recipe641); if (state.failed) return retval;
					INT_LIT21=(Token)match(input,INT_LIT,FOLLOW_INT_LIT_in_recipe644); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					INT_LIT21_tree = (CtrlTree)adaptor.create(INT_LIT21);
					adaptor.addChild(root_0, INT_LIT21_tree);
					}

					}
					break;

			}

			if ( state.backtracking==0 ) { helper.setContext(RECIPE17_tree); }
			pushFollow(FOLLOW_block_in_recipe658);
			block22=block();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) adaptor.addChild(root_0, block22.getTree());

			if ( state.backtracking==0 ) { helper.resetContext();
			      helper.declareCtrlUnit(RECIPE17_tree);
			    }
			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "recipe"


	public static class function_return extends ParserRuleReturnScope {
		CtrlTree tree;
		@Override
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "function"
	// .\\Ctrl.g:155:1: function : FUNCTION ^ ID par_list block ;
	public final CtrlParser.function_return function() throws RecognitionException {
		CtrlParser.function_return retval = new CtrlParser.function_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		Token FUNCTION23=null;
		Token ID24=null;
		ParserRuleReturnScope par_list25 =null;
		ParserRuleReturnScope block26 =null;

		CtrlTree FUNCTION23_tree=null;
		CtrlTree ID24_tree=null;

		try {
			// .\\Ctrl.g:156:3: ( FUNCTION ^ ID par_list block )
			// .\\Ctrl.g:161:5: FUNCTION ^ ID par_list block
			{
			root_0 = (CtrlTree)adaptor.nil();


			FUNCTION23=(Token)match(input,FUNCTION,FOLLOW_FUNCTION_in_function704); if (state.failed) return retval;
			if ( state.backtracking==0 ) {
			FUNCTION23_tree = (CtrlTree)adaptor.create(FUNCTION23);
			root_0 = (CtrlTree)adaptor.becomeRoot(FUNCTION23_tree, root_0);
			}

			ID24=(Token)match(input,ID,FOLLOW_ID_in_function707); if (state.failed) return retval;
			if ( state.backtracking==0 ) {
			ID24_tree = (CtrlTree)adaptor.create(ID24);
			adaptor.addChild(root_0, ID24_tree);
			}

			pushFollow(FOLLOW_par_list_in_function709);
			par_list25=par_list();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) adaptor.addChild(root_0, par_list25.getTree());

			if ( state.backtracking==0 ) { helper.setContext(FUNCTION23_tree); }
			pushFollow(FOLLOW_block_in_function722);
			block26=block();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) adaptor.addChild(root_0, block26.getTree());

			if ( state.backtracking==0 ) { helper.resetContext();
			      helper.declareCtrlUnit(FUNCTION23_tree);
			    }
			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "function"


	public static class par_list_return extends ParserRuleReturnScope {
		CtrlTree tree;
		@Override
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "par_list"
	// .\\Ctrl.g:172:1: par_list : LPAR ( par ( COMMA par )* )? RPAR -> ^( PARS ( par )* ) ;
	public final CtrlParser.par_list_return par_list() throws RecognitionException {
		CtrlParser.par_list_return retval = new CtrlParser.par_list_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		Token LPAR27=null;
		Token COMMA29=null;
		Token RPAR31=null;
		ParserRuleReturnScope par28 =null;
		ParserRuleReturnScope par30 =null;

		CtrlTree LPAR27_tree=null;
		CtrlTree COMMA29_tree=null;
		CtrlTree RPAR31_tree=null;
		RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
		RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
		RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
		RewriteRuleSubtreeStream stream_par=new RewriteRuleSubtreeStream(adaptor,"rule par");

		try {
			// .\\Ctrl.g:173:3: ( LPAR ( par ( COMMA par )* )? RPAR -> ^( PARS ( par )* ) )
			// .\\Ctrl.g:175:5: LPAR ( par ( COMMA par )* )? RPAR
			{
			LPAR27=(Token)match(input,LPAR,FOLLOW_LPAR_in_par_list753); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_LPAR.add(LPAR27);

			// .\\Ctrl.g:175:10: ( par ( COMMA par )* )?
			int alt10=2;
			int LA10_0 = input.LA(1);
			if ( (LA10_0==BOOL||LA10_0==INT||LA10_0==NODE||LA10_0==OUT||LA10_0==REAL||LA10_0==STRING) ) {
				alt10=1;
			}
			switch (alt10) {
				case 1 :
					// .\\Ctrl.g:175:11: par ( COMMA par )*
					{
					pushFollow(FOLLOW_par_in_par_list756);
					par28=par();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_par.add(par28.getTree());
					// .\\Ctrl.g:175:15: ( COMMA par )*
					loop9:
					while (true) {
						int alt9=2;
						int LA9_0 = input.LA(1);
						if ( (LA9_0==COMMA) ) {
							alt9=1;
						}

						switch (alt9) {
						case 1 :
							// .\\Ctrl.g:175:16: COMMA par
							{
							COMMA29=(Token)match(input,COMMA,FOLLOW_COMMA_in_par_list759); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_COMMA.add(COMMA29);

							pushFollow(FOLLOW_par_in_par_list761);
							par30=par();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_par.add(par30.getTree());
							}
							break;

						default :
							break loop9;
						}
					}

					}
					break;

			}

			RPAR31=(Token)match(input,RPAR,FOLLOW_RPAR_in_par_list767); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_RPAR.add(RPAR31);


			// AST REWRITE
			// elements: par
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CtrlTree)adaptor.nil();
			// 176:5: -> ^( PARS ( par )* )
			{
				// .\\Ctrl.g:176:8: ^( PARS ( par )* )
				{
				CtrlTree root_1 = (CtrlTree)adaptor.nil();
				root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(PARS, "PARS"), root_1);
				// .\\Ctrl.g:176:15: ( par )*
				while ( stream_par.hasNext() ) {
					adaptor.addChild(root_1, stream_par.nextTree());
				}
				stream_par.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "par_list"


	public static class par_return extends ParserRuleReturnScope {
		CtrlTree tree;
		@Override
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "par"
	// .\\Ctrl.g:182:1: par : ( OUT var_type ID -> ^( PAR OUT var_type ID ) | var_type ID -> ^( PAR var_type ID ) );
	public final CtrlParser.par_return par() throws RecognitionException {
		CtrlParser.par_return retval = new CtrlParser.par_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		Token OUT32=null;
		Token ID34=null;
		Token ID36=null;
		ParserRuleReturnScope var_type33 =null;
		ParserRuleReturnScope var_type35 =null;

		CtrlTree OUT32_tree=null;
		CtrlTree ID34_tree=null;
		CtrlTree ID36_tree=null;
		RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
		RewriteRuleTokenStream stream_OUT=new RewriteRuleTokenStream(adaptor,"token OUT");
		RewriteRuleSubtreeStream stream_var_type=new RewriteRuleSubtreeStream(adaptor,"rule var_type");

		try {
			// .\\Ctrl.g:183:3: ( OUT var_type ID -> ^( PAR OUT var_type ID ) | var_type ID -> ^( PAR var_type ID ) )
			int alt11=2;
			int LA11_0 = input.LA(1);
			if ( (LA11_0==OUT) ) {
				alt11=1;
			}
			else if ( (LA11_0==BOOL||LA11_0==INT||LA11_0==NODE||LA11_0==REAL||LA11_0==STRING) ) {
				alt11=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 11, 0, input);
				throw nvae;
			}

			switch (alt11) {
				case 1 :
					// .\\Ctrl.g:186:5: OUT var_type ID
					{
					OUT32=(Token)match(input,OUT,FOLLOW_OUT_in_par812); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_OUT.add(OUT32);

					pushFollow(FOLLOW_var_type_in_par814);
					var_type33=var_type();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_var_type.add(var_type33.getTree());
					ID34=(Token)match(input,ID,FOLLOW_ID_in_par816); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_ID.add(ID34);


					// AST REWRITE
					// elements: ID, OUT, var_type
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CtrlTree)adaptor.nil();
					// 186:21: -> ^( PAR OUT var_type ID )
					{
						// .\\Ctrl.g:186:24: ^( PAR OUT var_type ID )
						{
						CtrlTree root_1 = (CtrlTree)adaptor.nil();
						root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(PAR, "PAR"), root_1);
						adaptor.addChild(root_1, stream_OUT.nextNode());
						adaptor.addChild(root_1, stream_var_type.nextTree());
						adaptor.addChild(root_1, stream_ID.nextNode());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// .\\Ctrl.g:190:5: var_type ID
					{
					pushFollow(FOLLOW_var_type_in_par849);
					var_type35=var_type();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_var_type.add(var_type35.getTree());
					ID36=(Token)match(input,ID,FOLLOW_ID_in_par851); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_ID.add(ID36);


					// AST REWRITE
					// elements: ID, var_type
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CtrlTree)adaptor.nil();
					// 190:17: -> ^( PAR var_type ID )
					{
						// .\\Ctrl.g:190:20: ^( PAR var_type ID )
						{
						CtrlTree root_1 = (CtrlTree)adaptor.nil();
						root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(PAR, "PAR"), root_1);
						adaptor.addChild(root_1, stream_var_type.nextTree());
						adaptor.addChild(root_1, stream_ID.nextNode());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "par"


	public static class block_return extends ParserRuleReturnScope {
		CtrlTree tree;
		@Override
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "block"
	// .\\Ctrl.g:194:1: block : open= LCURLY ( stat )* close= RCURLY -> ^( BLOCK[$open] ( stat )* TRUE[$close] ) ;
	public final CtrlParser.block_return block() throws RecognitionException {
		CtrlParser.block_return retval = new CtrlParser.block_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		Token open=null;
		Token close=null;
		ParserRuleReturnScope stat37 =null;

		CtrlTree open_tree=null;
		CtrlTree close_tree=null;
		RewriteRuleTokenStream stream_LCURLY=new RewriteRuleTokenStream(adaptor,"token LCURLY");
		RewriteRuleTokenStream stream_RCURLY=new RewriteRuleTokenStream(adaptor,"token RCURLY");
		RewriteRuleSubtreeStream stream_stat=new RewriteRuleSubtreeStream(adaptor,"rule stat");

		try {
			// .\\Ctrl.g:195:3: (open= LCURLY ( stat )* close= RCURLY -> ^( BLOCK[$open] ( stat )* TRUE[$close] ) )
			// .\\Ctrl.g:197:5: open= LCURLY ( stat )* close= RCURLY
			{
			open=(Token)match(input,LCURLY,FOLLOW_LCURLY_in_block890); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_LCURLY.add(open);

			// .\\Ctrl.g:197:17: ( stat )*
			loop12:
			while (true) {
				int alt12=2;
				int LA12_0 = input.LA(1);
				if ( (LA12_0==ALAP||LA12_0==ANY||LA12_0==ASTERISK||LA12_0==BOOL||LA12_0==CHOICE||LA12_0==DO||(LA12_0 >= HALT && LA12_0 <= IF)||LA12_0==INT||(LA12_0 >= LANGLE && LA12_0 <= LCURLY)||LA12_0==LPAR||LA12_0==NODE||LA12_0==OTHER||LA12_0==REAL||LA12_0==SHARP||LA12_0==STRING||LA12_0==TRY||LA12_0==UNTIL||LA12_0==WHILE) ) {
					alt12=1;
				}

				switch (alt12) {
				case 1 :
					// .\\Ctrl.g:197:17: stat
					{
					pushFollow(FOLLOW_stat_in_block892);
					stat37=stat();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_stat.add(stat37.getTree());
					}
					break;

				default :
					break loop12;
				}
			}

			close=(Token)match(input,RCURLY,FOLLOW_RCURLY_in_block897); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_RCURLY.add(close);


			// AST REWRITE
			// elements: stat
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CtrlTree)adaptor.nil();
			// 198:5: -> ^( BLOCK[$open] ( stat )* TRUE[$close] )
			{
				// .\\Ctrl.g:198:8: ^( BLOCK[$open] ( stat )* TRUE[$close] )
				{
				CtrlTree root_1 = (CtrlTree)adaptor.nil();
				root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(BLOCK, open), root_1);
				// .\\Ctrl.g:198:23: ( stat )*
				while ( stream_stat.hasNext() ) {
					adaptor.addChild(root_1, stream_stat.nextTree());
				}
				stream_stat.reset();

				adaptor.addChild(root_1, (CtrlTree)adaptor.create(TRUE, close));
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "block"


	public static class stat_return extends ParserRuleReturnScope {
		CtrlTree tree;
		@Override
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "stat"
	// .\\Ctrl.g:201:1: stat : ( var_decl SEMI ^| block | ALAP ^ stat |open= LANGLE ( stat )* close= RANGLE -> ^( ATOM[$open] ^( BLOCK ( stat )* TRUE[$close] ) ) | WHILE ^ LPAR ! cond RPAR ! stat | UNTIL ^ LPAR ! cond RPAR ! stat | DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) ) | IF ^ LPAR ! cond RPAR ! stat ( ( ELSE )=> ELSE ! stat )? | TRY ^ stat ( ( ELSE )=> ELSE ! stat )? | CHOICE ^ stat ( ( OR )=> OR ! stat )+ | expr SEMI ^| HALT );
	public final CtrlParser.stat_return stat() throws RecognitionException {
		CtrlParser.stat_return retval = new CtrlParser.stat_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		Token open=null;
		Token close=null;
		Token SEMI39=null;
		Token ALAP41=null;
		Token WHILE44=null;
		Token LPAR45=null;
		Token RPAR47=null;
		Token UNTIL49=null;
		Token LPAR50=null;
		Token RPAR52=null;
		Token DO54=null;
		Token WHILE56=null;
		Token LPAR57=null;
		Token RPAR59=null;
		Token UNTIL60=null;
		Token LPAR61=null;
		Token RPAR63=null;
		Token IF64=null;
		Token LPAR65=null;
		Token RPAR67=null;
		Token ELSE69=null;
		Token TRY71=null;
		Token ELSE73=null;
		Token CHOICE75=null;
		Token OR77=null;
		Token SEMI80=null;
		Token HALT81=null;
		ParserRuleReturnScope var_decl38 =null;
		ParserRuleReturnScope block40 =null;
		ParserRuleReturnScope stat42 =null;
		ParserRuleReturnScope stat43 =null;
		ParserRuleReturnScope cond46 =null;
		ParserRuleReturnScope stat48 =null;
		ParserRuleReturnScope cond51 =null;
		ParserRuleReturnScope stat53 =null;
		ParserRuleReturnScope stat55 =null;
		ParserRuleReturnScope cond58 =null;
		ParserRuleReturnScope cond62 =null;
		ParserRuleReturnScope cond66 =null;
		ParserRuleReturnScope stat68 =null;
		ParserRuleReturnScope stat70 =null;
		ParserRuleReturnScope stat72 =null;
		ParserRuleReturnScope stat74 =null;
		ParserRuleReturnScope stat76 =null;
		ParserRuleReturnScope stat78 =null;
		ParserRuleReturnScope expr79 =null;

		CtrlTree open_tree=null;
		CtrlTree close_tree=null;
		CtrlTree SEMI39_tree=null;
		CtrlTree ALAP41_tree=null;
		CtrlTree WHILE44_tree=null;
		CtrlTree LPAR45_tree=null;
		CtrlTree RPAR47_tree=null;
		CtrlTree UNTIL49_tree=null;
		CtrlTree LPAR50_tree=null;
		CtrlTree RPAR52_tree=null;
		CtrlTree DO54_tree=null;
		CtrlTree WHILE56_tree=null;
		CtrlTree LPAR57_tree=null;
		CtrlTree RPAR59_tree=null;
		CtrlTree UNTIL60_tree=null;
		CtrlTree LPAR61_tree=null;
		CtrlTree RPAR63_tree=null;
		CtrlTree IF64_tree=null;
		CtrlTree LPAR65_tree=null;
		CtrlTree RPAR67_tree=null;
		CtrlTree ELSE69_tree=null;
		CtrlTree TRY71_tree=null;
		CtrlTree ELSE73_tree=null;
		CtrlTree CHOICE75_tree=null;
		CtrlTree OR77_tree=null;
		CtrlTree SEMI80_tree=null;
		CtrlTree HALT81_tree=null;
		RewriteRuleTokenStream stream_RANGLE=new RewriteRuleTokenStream(adaptor,"token RANGLE");
		RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
		RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
		RewriteRuleTokenStream stream_WHILE=new RewriteRuleTokenStream(adaptor,"token WHILE");
		RewriteRuleTokenStream stream_DO=new RewriteRuleTokenStream(adaptor,"token DO");
		RewriteRuleTokenStream stream_UNTIL=new RewriteRuleTokenStream(adaptor,"token UNTIL");
		RewriteRuleTokenStream stream_LANGLE=new RewriteRuleTokenStream(adaptor,"token LANGLE");
		RewriteRuleSubtreeStream stream_stat=new RewriteRuleSubtreeStream(adaptor,"rule stat");
		RewriteRuleSubtreeStream stream_cond=new RewriteRuleSubtreeStream(adaptor,"rule cond");

		try {
			// .\\Ctrl.g:202:3: ( var_decl SEMI ^| block | ALAP ^ stat |open= LANGLE ( stat )* close= RANGLE -> ^( ATOM[$open] ^( BLOCK ( stat )* TRUE[$close] ) ) | WHILE ^ LPAR ! cond RPAR ! stat | UNTIL ^ LPAR ! cond RPAR ! stat | DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) ) | IF ^ LPAR ! cond RPAR ! stat ( ( ELSE )=> ELSE ! stat )? | TRY ^ stat ( ( ELSE )=> ELSE ! stat )? | CHOICE ^ stat ( ( OR )=> OR ! stat )+ | expr SEMI ^| HALT )
			int alt18=12;
			switch ( input.LA(1) ) {
			case BOOL:
			case INT:
			case NODE:
			case REAL:
			case STRING:
				{
				alt18=1;
				}
				break;
			case LCURLY:
				{
				alt18=2;
				}
				break;
			case ALAP:
				{
				alt18=3;
				}
				break;
			case LANGLE:
				{
				alt18=4;
				}
				break;
			case WHILE:
				{
				alt18=5;
				}
				break;
			case UNTIL:
				{
				alt18=6;
				}
				break;
			case DO:
				{
				alt18=7;
				}
				break;
			case IF:
				{
				alt18=8;
				}
				break;
			case TRY:
				{
				alt18=9;
				}
				break;
			case CHOICE:
				{
				alt18=10;
				}
				break;
			case ANY:
			case ASTERISK:
			case ID:
			case LPAR:
			case OTHER:
			case SHARP:
				{
				alt18=11;
				}
				break;
			case HALT:
				{
				alt18=12;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 18, 0, input);
				throw nvae;
			}
			switch (alt18) {
				case 1 :
					// .\\Ctrl.g:204:5: var_decl SEMI ^
					{
					root_0 = (CtrlTree)adaptor.nil();


					pushFollow(FOLLOW_var_decl_in_stat936);
					var_decl38=var_decl();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, var_decl38.getTree());

					SEMI39=(Token)match(input,SEMI,FOLLOW_SEMI_in_stat938); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					SEMI39_tree = (CtrlTree)adaptor.create(SEMI39);
					root_0 = (CtrlTree)adaptor.becomeRoot(SEMI39_tree, root_0);
					}

					}
					break;
				case 2 :
					// .\\Ctrl.g:206:4: block
					{
					root_0 = (CtrlTree)adaptor.nil();


					pushFollow(FOLLOW_block_in_stat950);
					block40=block();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, block40.getTree());

					}
					break;
				case 3 :
					// .\\Ctrl.g:210:4: ALAP ^ stat
					{
					root_0 = (CtrlTree)adaptor.nil();


					ALAP41=(Token)match(input,ALAP,FOLLOW_ALAP_in_stat967); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					ALAP41_tree = (CtrlTree)adaptor.create(ALAP41);
					root_0 = (CtrlTree)adaptor.becomeRoot(ALAP41_tree, root_0);
					}

					pushFollow(FOLLOW_stat_in_stat970);
					stat42=stat();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, stat42.getTree());

					}
					break;
				case 4 :
					// .\\Ctrl.g:215:4: open= LANGLE ( stat )* close= RANGLE
					{
					open=(Token)match(input,LANGLE,FOLLOW_LANGLE_in_stat993); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_LANGLE.add(open);

					// .\\Ctrl.g:215:16: ( stat )*
					loop13:
					while (true) {
						int alt13=2;
						int LA13_0 = input.LA(1);
						if ( (LA13_0==ALAP||LA13_0==ANY||LA13_0==ASTERISK||LA13_0==BOOL||LA13_0==CHOICE||LA13_0==DO||(LA13_0 >= HALT && LA13_0 <= IF)||LA13_0==INT||(LA13_0 >= LANGLE && LA13_0 <= LCURLY)||LA13_0==LPAR||LA13_0==NODE||LA13_0==OTHER||LA13_0==REAL||LA13_0==SHARP||LA13_0==STRING||LA13_0==TRY||LA13_0==UNTIL||LA13_0==WHILE) ) {
							alt13=1;
						}

						switch (alt13) {
						case 1 :
							// .\\Ctrl.g:215:16: stat
							{
							pushFollow(FOLLOW_stat_in_stat995);
							stat43=stat();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_stat.add(stat43.getTree());
							}
							break;

						default :
							break loop13;
						}
					}

					close=(Token)match(input,RANGLE,FOLLOW_RANGLE_in_stat1000); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_RANGLE.add(close);


					// AST REWRITE
					// elements: stat
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CtrlTree)adaptor.nil();
					// 216:4: -> ^( ATOM[$open] ^( BLOCK ( stat )* TRUE[$close] ) )
					{
						// .\\Ctrl.g:216:7: ^( ATOM[$open] ^( BLOCK ( stat )* TRUE[$close] ) )
						{
						CtrlTree root_1 = (CtrlTree)adaptor.nil();
						root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(ATOM, open), root_1);
						// .\\Ctrl.g:216:21: ^( BLOCK ( stat )* TRUE[$close] )
						{
						CtrlTree root_2 = (CtrlTree)adaptor.nil();
						root_2 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(BLOCK, "BLOCK"), root_2);
						// .\\Ctrl.g:216:29: ( stat )*
						while ( stream_stat.hasNext() ) {
							adaptor.addChild(root_2, stream_stat.nextTree());
						}
						stream_stat.reset();

						adaptor.addChild(root_2, (CtrlTree)adaptor.create(TRUE, close));
						adaptor.addChild(root_1, root_2);
						}

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 5 :
					// .\\Ctrl.g:221:4: WHILE ^ LPAR ! cond RPAR ! stat
					{
					root_0 = (CtrlTree)adaptor.nil();


					WHILE44=(Token)match(input,WHILE,FOLLOW_WHILE_in_stat1041); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					WHILE44_tree = (CtrlTree)adaptor.create(WHILE44);
					root_0 = (CtrlTree)adaptor.becomeRoot(WHILE44_tree, root_0);
					}

					LPAR45=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat1044); if (state.failed) return retval;
					pushFollow(FOLLOW_cond_in_stat1047);
					cond46=cond();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, cond46.getTree());

					RPAR47=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat1049); if (state.failed) return retval;
					pushFollow(FOLLOW_stat_in_stat1052);
					stat48=stat();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, stat48.getTree());

					}
					break;
				case 6 :
					// .\\Ctrl.g:225:5: UNTIL ^ LPAR ! cond RPAR ! stat
					{
					root_0 = (CtrlTree)adaptor.nil();


					UNTIL49=(Token)match(input,UNTIL,FOLLOW_UNTIL_in_stat1072); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					UNTIL49_tree = (CtrlTree)adaptor.create(UNTIL49);
					root_0 = (CtrlTree)adaptor.becomeRoot(UNTIL49_tree, root_0);
					}

					LPAR50=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat1075); if (state.failed) return retval;
					pushFollow(FOLLOW_cond_in_stat1078);
					cond51=cond();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, cond51.getTree());

					RPAR52=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat1080); if (state.failed) return retval;
					pushFollow(FOLLOW_stat_in_stat1083);
					stat53=stat();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, stat53.getTree());

					}
					break;
				case 7 :
					// .\\Ctrl.g:226:4: DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) )
					{
					DO54=(Token)match(input,DO,FOLLOW_DO_in_stat1088); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_DO.add(DO54);

					pushFollow(FOLLOW_stat_in_stat1090);
					stat55=stat();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_stat.add(stat55.getTree());
					// .\\Ctrl.g:227:4: ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) )
					int alt14=2;
					int LA14_0 = input.LA(1);
					if ( (LA14_0==WHILE) ) {
						alt14=1;
					}
					else if ( (LA14_0==UNTIL) ) {
						alt14=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						NoViableAltException nvae =
							new NoViableAltException("", 14, 0, input);
						throw nvae;
					}

					switch (alt14) {
						case 1 :
							// .\\Ctrl.g:232:7: WHILE LPAR cond RPAR
							{
							WHILE56=(Token)match(input,WHILE,FOLLOW_WHILE_in_stat1133); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_WHILE.add(WHILE56);

							LPAR57=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat1135); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_LPAR.add(LPAR57);

							pushFollow(FOLLOW_cond_in_stat1137);
							cond58=cond();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_cond.add(cond58.getTree());
							RPAR59=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat1139); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_RPAR.add(RPAR59);


							// AST REWRITE
							// elements: cond, stat, stat, WHILE
							// token labels: 
							// rule labels: retval
							// token list labels: 
							// rule list labels: 
							// wildcard labels: 
							if ( state.backtracking==0 ) {
							retval.tree = root_0;
							RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

							root_0 = (CtrlTree)adaptor.nil();
							// 232:28: -> ^( BLOCK stat ^( WHILE cond stat ) )
							{
								// .\\Ctrl.g:232:31: ^( BLOCK stat ^( WHILE cond stat ) )
								{
								CtrlTree root_1 = (CtrlTree)adaptor.nil();
								root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(BLOCK, "BLOCK"), root_1);
								adaptor.addChild(root_1, stream_stat.nextTree());
								// .\\Ctrl.g:232:44: ^( WHILE cond stat )
								{
								CtrlTree root_2 = (CtrlTree)adaptor.nil();
								root_2 = (CtrlTree)adaptor.becomeRoot(stream_WHILE.nextNode(), root_2);
								adaptor.addChild(root_2, stream_cond.nextTree());
								adaptor.addChild(root_2, stream_stat.nextTree());
								adaptor.addChild(root_1, root_2);
								}

								adaptor.addChild(root_0, root_1);
								}

							}


							retval.tree = root_0;
							}

							}
							break;
						case 2 :
							// .\\Ctrl.g:239:5: UNTIL LPAR cond RPAR
							{
							UNTIL60=(Token)match(input,UNTIL,FOLLOW_UNTIL_in_stat1202); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_UNTIL.add(UNTIL60);

							LPAR61=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat1204); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_LPAR.add(LPAR61);

							pushFollow(FOLLOW_cond_in_stat1206);
							cond62=cond();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_cond.add(cond62.getTree());
							RPAR63=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat1208); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_RPAR.add(RPAR63);


							// AST REWRITE
							// elements: stat, cond, stat, UNTIL
							// token labels: 
							// rule labels: retval
							// token list labels: 
							// rule list labels: 
							// wildcard labels: 
							if ( state.backtracking==0 ) {
							retval.tree = root_0;
							RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

							root_0 = (CtrlTree)adaptor.nil();
							// 239:26: -> ^( BLOCK stat ^( UNTIL cond stat ) )
							{
								// .\\Ctrl.g:239:29: ^( BLOCK stat ^( UNTIL cond stat ) )
								{
								CtrlTree root_1 = (CtrlTree)adaptor.nil();
								root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(BLOCK, "BLOCK"), root_1);
								adaptor.addChild(root_1, stream_stat.nextTree());
								// .\\Ctrl.g:239:42: ^( UNTIL cond stat )
								{
								CtrlTree root_2 = (CtrlTree)adaptor.nil();
								root_2 = (CtrlTree)adaptor.becomeRoot(stream_UNTIL.nextNode(), root_2);
								adaptor.addChild(root_2, stream_cond.nextTree());
								adaptor.addChild(root_2, stream_stat.nextTree());
								adaptor.addChild(root_1, root_2);
								}

								adaptor.addChild(root_0, root_1);
								}

							}


							retval.tree = root_0;
							}

							}
							break;

					}

					}
					break;
				case 8 :
					// .\\Ctrl.g:245:5: IF ^ LPAR ! cond RPAR ! stat ( ( ELSE )=> ELSE ! stat )?
					{
					root_0 = (CtrlTree)adaptor.nil();


					IF64=(Token)match(input,IF,FOLLOW_IF_in_stat1255); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					IF64_tree = (CtrlTree)adaptor.create(IF64);
					root_0 = (CtrlTree)adaptor.becomeRoot(IF64_tree, root_0);
					}

					LPAR65=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat1258); if (state.failed) return retval;
					pushFollow(FOLLOW_cond_in_stat1261);
					cond66=cond();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, cond66.getTree());

					RPAR67=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat1263); if (state.failed) return retval;
					pushFollow(FOLLOW_stat_in_stat1266);
					stat68=stat();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, stat68.getTree());

					// .\\Ctrl.g:245:31: ( ( ELSE )=> ELSE ! stat )?
					int alt15=2;
					int LA15_0 = input.LA(1);
					if ( (LA15_0==ELSE) ) {
						int LA15_1 = input.LA(2);
						if ( (synpred1_Ctrl()) ) {
							alt15=1;
						}
					}
					switch (alt15) {
						case 1 :
							// .\\Ctrl.g:245:33: ( ELSE )=> ELSE ! stat
							{
							ELSE69=(Token)match(input,ELSE,FOLLOW_ELSE_in_stat1276); if (state.failed) return retval;
							pushFollow(FOLLOW_stat_in_stat1279);
							stat70=stat();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) adaptor.addChild(root_0, stat70.getTree());

							}
							break;

					}

					}
					break;
				case 9 :
					// .\\Ctrl.g:249:5: TRY ^ stat ( ( ELSE )=> ELSE ! stat )?
					{
					root_0 = (CtrlTree)adaptor.nil();


					TRY71=(Token)match(input,TRY,FOLLOW_TRY_in_stat1303); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					TRY71_tree = (CtrlTree)adaptor.create(TRY71);
					root_0 = (CtrlTree)adaptor.becomeRoot(TRY71_tree, root_0);
					}

					pushFollow(FOLLOW_stat_in_stat1306);
					stat72=stat();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, stat72.getTree());

					// .\\Ctrl.g:249:15: ( ( ELSE )=> ELSE ! stat )?
					int alt16=2;
					int LA16_0 = input.LA(1);
					if ( (LA16_0==ELSE) ) {
						int LA16_1 = input.LA(2);
						if ( (synpred2_Ctrl()) ) {
							alt16=1;
						}
					}
					switch (alt16) {
						case 1 :
							// .\\Ctrl.g:249:17: ( ELSE )=> ELSE ! stat
							{
							ELSE73=(Token)match(input,ELSE,FOLLOW_ELSE_in_stat1316); if (state.failed) return retval;
							pushFollow(FOLLOW_stat_in_stat1319);
							stat74=stat();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) adaptor.addChild(root_0, stat74.getTree());

							}
							break;

					}

					}
					break;
				case 10 :
					// .\\Ctrl.g:252:5: CHOICE ^ stat ( ( OR )=> OR ! stat )+
					{
					root_0 = (CtrlTree)adaptor.nil();


					CHOICE75=(Token)match(input,CHOICE,FOLLOW_CHOICE_in_stat1338); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					CHOICE75_tree = (CtrlTree)adaptor.create(CHOICE75);
					root_0 = (CtrlTree)adaptor.becomeRoot(CHOICE75_tree, root_0);
					}

					pushFollow(FOLLOW_stat_in_stat1341);
					stat76=stat();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, stat76.getTree());

					// .\\Ctrl.g:252:18: ( ( OR )=> OR ! stat )+
					int cnt17=0;
					loop17:
					while (true) {
						int alt17=2;
						int LA17_0 = input.LA(1);
						if ( (LA17_0==OR) ) {
							int LA17_24 = input.LA(2);
							if ( (synpred3_Ctrl()) ) {
								alt17=1;
							}

						}

						switch (alt17) {
						case 1 :
							// .\\Ctrl.g:252:20: ( OR )=> OR ! stat
							{
							OR77=(Token)match(input,OR,FOLLOW_OR_in_stat1351); if (state.failed) return retval;
							pushFollow(FOLLOW_stat_in_stat1354);
							stat78=stat();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) adaptor.addChild(root_0, stat78.getTree());

							}
							break;

						default :
							if ( cnt17 >= 1 ) break loop17;
							if (state.backtracking>0) {state.failed=true; return retval;}
							EarlyExitException eee = new EarlyExitException(17, input);
							throw eee;
						}
						cnt17++;
					}

					}
					break;
				case 11 :
					// .\\Ctrl.g:255:4: expr SEMI ^
					{
					root_0 = (CtrlTree)adaptor.nil();


					pushFollow(FOLLOW_expr_in_stat1369);
					expr79=expr();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, expr79.getTree());

					SEMI80=(Token)match(input,SEMI,FOLLOW_SEMI_in_stat1371); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					SEMI80_tree = (CtrlTree)adaptor.create(SEMI80);
					root_0 = (CtrlTree)adaptor.becomeRoot(SEMI80_tree, root_0);
					}

					}
					break;
				case 12 :
					// .\\Ctrl.g:258:5: HALT
					{
					root_0 = (CtrlTree)adaptor.nil();


					HALT81=(Token)match(input,HALT,FOLLOW_HALT_in_stat1389); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					HALT81_tree = (CtrlTree)adaptor.create(HALT81);
					adaptor.addChild(root_0, HALT81_tree);
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "stat"


	public static class var_decl_return extends ParserRuleReturnScope {
		CtrlTree tree;
		@Override
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "var_decl"
	// .\\Ctrl.g:262:1: var_decl : var_decl_pure ( -> var_decl_pure | BECOMES call -> ^( BECOMES var_decl_pure call ) ) ;
	public final CtrlParser.var_decl_return var_decl() throws RecognitionException {
		CtrlParser.var_decl_return retval = new CtrlParser.var_decl_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		Token BECOMES83=null;
		ParserRuleReturnScope var_decl_pure82 =null;
		ParserRuleReturnScope call84 =null;

		CtrlTree BECOMES83_tree=null;
		RewriteRuleTokenStream stream_BECOMES=new RewriteRuleTokenStream(adaptor,"token BECOMES");
		RewriteRuleSubtreeStream stream_call=new RewriteRuleSubtreeStream(adaptor,"rule call");
		RewriteRuleSubtreeStream stream_var_decl_pure=new RewriteRuleSubtreeStream(adaptor,"rule var_decl_pure");

		try {
			// .\\Ctrl.g:263:3: ( var_decl_pure ( -> var_decl_pure | BECOMES call -> ^( BECOMES var_decl_pure call ) ) )
			// .\\Ctrl.g:266:5: var_decl_pure ( -> var_decl_pure | BECOMES call -> ^( BECOMES var_decl_pure call ) )
			{
			pushFollow(FOLLOW_var_decl_pure_in_var_decl1419);
			var_decl_pure82=var_decl_pure();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_var_decl_pure.add(var_decl_pure82.getTree());
			// .\\Ctrl.g:267:5: ( -> var_decl_pure | BECOMES call -> ^( BECOMES var_decl_pure call ) )
			int alt19=2;
			int LA19_0 = input.LA(1);
			if ( (LA19_0==SEMI) ) {
				alt19=1;
			}
			else if ( (LA19_0==BECOMES) ) {
				alt19=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 19, 0, input);
				throw nvae;
			}

			switch (alt19) {
				case 1 :
					// .\\Ctrl.g:267:7: 
					{

					// AST REWRITE
					// elements: var_decl_pure
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CtrlTree)adaptor.nil();
					// 267:7: -> var_decl_pure
					{
						adaptor.addChild(root_0, stream_var_decl_pure.nextTree());
					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// .\\Ctrl.g:268:7: BECOMES call
					{
					BECOMES83=(Token)match(input,BECOMES,FOLLOW_BECOMES_in_var_decl1437); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_BECOMES.add(BECOMES83);

					pushFollow(FOLLOW_call_in_var_decl1439);
					call84=call();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_call.add(call84.getTree());

					// AST REWRITE
					// elements: call, var_decl_pure, BECOMES
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CtrlTree)adaptor.nil();
					// 268:20: -> ^( BECOMES var_decl_pure call )
					{
						// .\\Ctrl.g:268:23: ^( BECOMES var_decl_pure call )
						{
						CtrlTree root_1 = (CtrlTree)adaptor.nil();
						root_1 = (CtrlTree)adaptor.becomeRoot(stream_BECOMES.nextNode(), root_1);
						adaptor.addChild(root_1, stream_var_decl_pure.nextTree());
						adaptor.addChild(root_1, stream_call.nextTree());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;

			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "var_decl"


	public static class var_decl_pure_return extends ParserRuleReturnScope {
		CtrlTree tree;
		@Override
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "var_decl_pure"
	// .\\Ctrl.g:272:1: var_decl_pure : var_type ID ( COMMA ID )* -> ^( VAR var_type ( ID )+ ) ;
	public final CtrlParser.var_decl_pure_return var_decl_pure() throws RecognitionException {
		CtrlParser.var_decl_pure_return retval = new CtrlParser.var_decl_pure_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		Token ID86=null;
		Token COMMA87=null;
		Token ID88=null;
		ParserRuleReturnScope var_type85 =null;

		CtrlTree ID86_tree=null;
		CtrlTree COMMA87_tree=null;
		CtrlTree ID88_tree=null;
		RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
		RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
		RewriteRuleSubtreeStream stream_var_type=new RewriteRuleSubtreeStream(adaptor,"rule var_type");

		try {
			// .\\Ctrl.g:273:3: ( var_type ID ( COMMA ID )* -> ^( VAR var_type ( ID )+ ) )
			// .\\Ctrl.g:273:5: var_type ID ( COMMA ID )*
			{
			pushFollow(FOLLOW_var_type_in_var_decl_pure1469);
			var_type85=var_type();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_var_type.add(var_type85.getTree());
			ID86=(Token)match(input,ID,FOLLOW_ID_in_var_decl_pure1471); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_ID.add(ID86);

			// .\\Ctrl.g:273:17: ( COMMA ID )*
			loop20:
			while (true) {
				int alt20=2;
				int LA20_0 = input.LA(1);
				if ( (LA20_0==COMMA) ) {
					alt20=1;
				}

				switch (alt20) {
				case 1 :
					// .\\Ctrl.g:273:18: COMMA ID
					{
					COMMA87=(Token)match(input,COMMA,FOLLOW_COMMA_in_var_decl_pure1474); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_COMMA.add(COMMA87);

					ID88=(Token)match(input,ID,FOLLOW_ID_in_var_decl_pure1476); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_ID.add(ID88);

					}
					break;

				default :
					break loop20;
				}
			}


			// AST REWRITE
			// elements: var_type, ID
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CtrlTree)adaptor.nil();
			// 273:29: -> ^( VAR var_type ( ID )+ )
			{
				// .\\Ctrl.g:273:32: ^( VAR var_type ( ID )+ )
				{
				CtrlTree root_1 = (CtrlTree)adaptor.nil();
				root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(VAR, "VAR"), root_1);
				adaptor.addChild(root_1, stream_var_type.nextTree());
				if ( !(stream_ID.hasNext()) ) {
					throw new RewriteEarlyExitException();
				}
				while ( stream_ID.hasNext() ) {
					adaptor.addChild(root_1, stream_ID.nextNode());
				}
				stream_ID.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "var_decl_pure"


	public static class cond_return extends ParserRuleReturnScope {
		CtrlTree tree;
		@Override
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "cond"
	// .\\Ctrl.g:277:1: cond : cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom ) ;
	public final CtrlParser.cond_return cond() throws RecognitionException {
		CtrlParser.cond_return retval = new CtrlParser.cond_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		Token BAR90=null;
		ParserRuleReturnScope cond_atom89 =null;
		ParserRuleReturnScope cond_atom91 =null;

		CtrlTree BAR90_tree=null;
		RewriteRuleTokenStream stream_BAR=new RewriteRuleTokenStream(adaptor,"token BAR");
		RewriteRuleSubtreeStream stream_cond_atom=new RewriteRuleSubtreeStream(adaptor,"rule cond_atom");

		try {
			// .\\Ctrl.g:278:2: ( cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom ) )
			// .\\Ctrl.g:280:4: cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom )
			{
			pushFollow(FOLLOW_cond_atom_in_cond1512);
			cond_atom89=cond_atom();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_cond_atom.add(cond_atom89.getTree());
			// .\\Ctrl.g:281:4: ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom )
			int alt22=2;
			int LA22_0 = input.LA(1);
			if ( (LA22_0==BAR) ) {
				alt22=1;
			}
			else if ( (LA22_0==RPAR) ) {
				alt22=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 22, 0, input);
				throw nvae;
			}

			switch (alt22) {
				case 1 :
					// .\\Ctrl.g:281:6: ( BAR cond_atom )+
					{
					// .\\Ctrl.g:281:6: ( BAR cond_atom )+
					int cnt21=0;
					loop21:
					while (true) {
						int alt21=2;
						int LA21_0 = input.LA(1);
						if ( (LA21_0==BAR) ) {
							alt21=1;
						}

						switch (alt21) {
						case 1 :
							// .\\Ctrl.g:281:7: BAR cond_atom
							{
							BAR90=(Token)match(input,BAR,FOLLOW_BAR_in_cond1521); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_BAR.add(BAR90);

							pushFollow(FOLLOW_cond_atom_in_cond1523);
							cond_atom91=cond_atom();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_cond_atom.add(cond_atom91.getTree());
							}
							break;

						default :
							if ( cnt21 >= 1 ) break loop21;
							if (state.backtracking>0) {state.failed=true; return retval;}
							EarlyExitException eee = new EarlyExitException(21, input);
							throw eee;
						}
						cnt21++;
					}


					// AST REWRITE
					// elements: cond_atom, cond_atom
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CtrlTree)adaptor.nil();
					// 281:23: -> ^( CHOICE cond_atom ( cond_atom )+ )
					{
						// .\\Ctrl.g:281:26: ^( CHOICE cond_atom ( cond_atom )+ )
						{
						CtrlTree root_1 = (CtrlTree)adaptor.nil();
						root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(CHOICE, "CHOICE"), root_1);
						adaptor.addChild(root_1, stream_cond_atom.nextTree());
						if ( !(stream_cond_atom.hasNext()) ) {
							throw new RewriteEarlyExitException();
						}
						while ( stream_cond_atom.hasNext() ) {
							adaptor.addChild(root_1, stream_cond_atom.nextTree());
						}
						stream_cond_atom.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// .\\Ctrl.g:282:6: 
					{

					// AST REWRITE
					// elements: cond_atom
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CtrlTree)adaptor.nil();
					// 282:6: -> cond_atom
					{
						adaptor.addChild(root_0, stream_cond_atom.nextTree());
					}


					retval.tree = root_0;
					}

					}
					break;

			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "cond"


	public static class cond_atom_return extends ParserRuleReturnScope {
		CtrlTree tree;
		@Override
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "cond_atom"
	// .\\Ctrl.g:286:1: cond_atom : ( TRUE | call );
	public final CtrlParser.cond_atom_return cond_atom() throws RecognitionException {
		CtrlParser.cond_atom_return retval = new CtrlParser.cond_atom_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		Token TRUE92=null;
		ParserRuleReturnScope call93 =null;

		CtrlTree TRUE92_tree=null;

		try {
			// .\\Ctrl.g:287:2: ( TRUE | call )
			int alt23=2;
			int LA23_0 = input.LA(1);
			if ( (LA23_0==TRUE) ) {
				alt23=1;
			}
			else if ( (LA23_0==ANY||LA23_0==ASTERISK||LA23_0==ID||LA23_0==OTHER) ) {
				alt23=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 23, 0, input);
				throw nvae;
			}

			switch (alt23) {
				case 1 :
					// .\\Ctrl.g:289:4: TRUE
					{
					root_0 = (CtrlTree)adaptor.nil();


					TRUE92=(Token)match(input,TRUE,FOLLOW_TRUE_in_cond_atom1569); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					TRUE92_tree = (CtrlTree)adaptor.create(TRUE92);
					adaptor.addChild(root_0, TRUE92_tree);
					}

					}
					break;
				case 2 :
					// .\\Ctrl.g:293:5: call
					{
					root_0 = (CtrlTree)adaptor.nil();


					pushFollow(FOLLOW_call_in_cond_atom1590);
					call93=call();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, call93.getTree());

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "cond_atom"


	public static class expr_return extends ParserRuleReturnScope {
		CtrlTree tree;
		@Override
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "expr"
	// .\\Ctrl.g:296:1: expr : expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 ) ;
	public final CtrlParser.expr_return expr() throws RecognitionException {
		CtrlParser.expr_return retval = new CtrlParser.expr_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		Token BAR95=null;
		ParserRuleReturnScope expr294 =null;
		ParserRuleReturnScope expr296 =null;

		CtrlTree BAR95_tree=null;
		RewriteRuleTokenStream stream_BAR=new RewriteRuleTokenStream(adaptor,"token BAR");
		RewriteRuleSubtreeStream stream_expr2=new RewriteRuleSubtreeStream(adaptor,"rule expr2");

		try {
			// .\\Ctrl.g:297:2: ( expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 ) )
			// .\\Ctrl.g:301:4: expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 )
			{
			pushFollow(FOLLOW_expr2_in_expr1620);
			expr294=expr2();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_expr2.add(expr294.getTree());
			// .\\Ctrl.g:302:4: ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 )
			int alt25=2;
			int LA25_0 = input.LA(1);
			if ( (LA25_0==BAR) ) {
				alt25=1;
			}
			else if ( ((LA25_0 >= RPAR && LA25_0 <= SEMI)) ) {
				alt25=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 25, 0, input);
				throw nvae;
			}

			switch (alt25) {
				case 1 :
					// .\\Ctrl.g:302:6: ( BAR expr2 )+
					{
					// .\\Ctrl.g:302:6: ( BAR expr2 )+
					int cnt24=0;
					loop24:
					while (true) {
						int alt24=2;
						int LA24_0 = input.LA(1);
						if ( (LA24_0==BAR) ) {
							alt24=1;
						}

						switch (alt24) {
						case 1 :
							// .\\Ctrl.g:302:7: BAR expr2
							{
							BAR95=(Token)match(input,BAR,FOLLOW_BAR_in_expr1628); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_BAR.add(BAR95);

							pushFollow(FOLLOW_expr2_in_expr1630);
							expr296=expr2();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_expr2.add(expr296.getTree());
							}
							break;

						default :
							if ( cnt24 >= 1 ) break loop24;
							if (state.backtracking>0) {state.failed=true; return retval;}
							EarlyExitException eee = new EarlyExitException(24, input);
							throw eee;
						}
						cnt24++;
					}


					// AST REWRITE
					// elements: expr2, expr2
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CtrlTree)adaptor.nil();
					// 302:19: -> ^( CHOICE expr2 ( expr2 )+ )
					{
						// .\\Ctrl.g:302:22: ^( CHOICE expr2 ( expr2 )+ )
						{
						CtrlTree root_1 = (CtrlTree)adaptor.nil();
						root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(CHOICE, "CHOICE"), root_1);
						adaptor.addChild(root_1, stream_expr2.nextTree());
						if ( !(stream_expr2.hasNext()) ) {
							throw new RewriteEarlyExitException();
						}
						while ( stream_expr2.hasNext() ) {
							adaptor.addChild(root_1, stream_expr2.nextTree());
						}
						stream_expr2.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// .\\Ctrl.g:303:6: 
					{

					// AST REWRITE
					// elements: expr2
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CtrlTree)adaptor.nil();
					// 303:6: -> expr2
					{
						adaptor.addChild(root_0, stream_expr2.nextTree());
					}


					retval.tree = root_0;
					}

					}
					break;

			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "expr"


	public static class expr2_return extends ParserRuleReturnScope {
		CtrlTree tree;
		@Override
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "expr2"
	// .\\Ctrl.g:307:1: expr2 : (e= expr_atom (plus= PLUS -> ^( BLOCK $e ^( STAR[$plus] $e) ) |ast= ASTERISK -> ^( STAR[$ast] $e) | -> $e) |op= SHARP expr_atom -> ^( ALAP[$op] expr_atom ) );
	public final CtrlParser.expr2_return expr2() throws RecognitionException {
		CtrlParser.expr2_return retval = new CtrlParser.expr2_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		Token plus=null;
		Token ast=null;
		Token op=null;
		ParserRuleReturnScope e =null;
		ParserRuleReturnScope expr_atom97 =null;

		CtrlTree plus_tree=null;
		CtrlTree ast_tree=null;
		CtrlTree op_tree=null;
		RewriteRuleTokenStream stream_ASTERISK=new RewriteRuleTokenStream(adaptor,"token ASTERISK");
		RewriteRuleTokenStream stream_SHARP=new RewriteRuleTokenStream(adaptor,"token SHARP");
		RewriteRuleTokenStream stream_PLUS=new RewriteRuleTokenStream(adaptor,"token PLUS");
		RewriteRuleSubtreeStream stream_expr_atom=new RewriteRuleSubtreeStream(adaptor,"rule expr_atom");

		try {
			// .\\Ctrl.g:308:3: (e= expr_atom (plus= PLUS -> ^( BLOCK $e ^( STAR[$plus] $e) ) |ast= ASTERISK -> ^( STAR[$ast] $e) | -> $e) |op= SHARP expr_atom -> ^( ALAP[$op] expr_atom ) )
			int alt27=2;
			int LA27_0 = input.LA(1);
			if ( (LA27_0==ANY||LA27_0==ASTERISK||LA27_0==ID||LA27_0==LPAR||LA27_0==OTHER) ) {
				alt27=1;
			}
			else if ( (LA27_0==SHARP) ) {
				alt27=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 27, 0, input);
				throw nvae;
			}

			switch (alt27) {
				case 1 :
					// .\\Ctrl.g:316:5: e= expr_atom (plus= PLUS -> ^( BLOCK $e ^( STAR[$plus] $e) ) |ast= ASTERISK -> ^( STAR[$ast] $e) | -> $e)
					{
					pushFollow(FOLLOW_expr_atom_in_expr21711);
					e=expr_atom();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_expr_atom.add(e.getTree());
					// .\\Ctrl.g:317:5: (plus= PLUS -> ^( BLOCK $e ^( STAR[$plus] $e) ) |ast= ASTERISK -> ^( STAR[$ast] $e) | -> $e)
					int alt26=3;
					switch ( input.LA(1) ) {
					case PLUS:
						{
						alt26=1;
						}
						break;
					case ASTERISK:
						{
						alt26=2;
						}
						break;
					case BAR:
					case RPAR:
					case SEMI:
						{
						alt26=3;
						}
						break;
					default:
						if (state.backtracking>0) {state.failed=true; return retval;}
						NoViableAltException nvae =
							new NoViableAltException("", 26, 0, input);
						throw nvae;
					}
					switch (alt26) {
						case 1 :
							// .\\Ctrl.g:317:7: plus= PLUS
							{
							plus=(Token)match(input,PLUS,FOLLOW_PLUS_in_expr21721); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_PLUS.add(plus);


							// AST REWRITE
							// elements: e, e
							// token labels: 
							// rule labels: e, retval
							// token list labels: 
							// rule list labels: 
							// wildcard labels: 
							if ( state.backtracking==0 ) {
							retval.tree = root_0;
							RewriteRuleSubtreeStream stream_e=new RewriteRuleSubtreeStream(adaptor,"rule e",e!=null?e.getTree():null);
							RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

							root_0 = (CtrlTree)adaptor.nil();
							// 317:17: -> ^( BLOCK $e ^( STAR[$plus] $e) )
							{
								// .\\Ctrl.g:317:20: ^( BLOCK $e ^( STAR[$plus] $e) )
								{
								CtrlTree root_1 = (CtrlTree)adaptor.nil();
								root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(BLOCK, "BLOCK"), root_1);
								adaptor.addChild(root_1, stream_e.nextTree());
								// .\\Ctrl.g:317:31: ^( STAR[$plus] $e)
								{
								CtrlTree root_2 = (CtrlTree)adaptor.nil();
								root_2 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(STAR, plus), root_2);
								adaptor.addChild(root_2, stream_e.nextTree());
								adaptor.addChild(root_1, root_2);
								}

								adaptor.addChild(root_0, root_1);
								}

							}


							retval.tree = root_0;
							}

							}
							break;
						case 2 :
							// .\\Ctrl.g:318:7: ast= ASTERISK
							{
							ast=(Token)match(input,ASTERISK,FOLLOW_ASTERISK_in_expr21748); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_ASTERISK.add(ast);


							// AST REWRITE
							// elements: e
							// token labels: 
							// rule labels: e, retval
							// token list labels: 
							// rule list labels: 
							// wildcard labels: 
							if ( state.backtracking==0 ) {
							retval.tree = root_0;
							RewriteRuleSubtreeStream stream_e=new RewriteRuleSubtreeStream(adaptor,"rule e",e!=null?e.getTree():null);
							RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

							root_0 = (CtrlTree)adaptor.nil();
							// 318:20: -> ^( STAR[$ast] $e)
							{
								// .\\Ctrl.g:318:23: ^( STAR[$ast] $e)
								{
								CtrlTree root_1 = (CtrlTree)adaptor.nil();
								root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(STAR, ast), root_1);
								adaptor.addChild(root_1, stream_e.nextTree());
								adaptor.addChild(root_0, root_1);
								}

							}


							retval.tree = root_0;
							}

							}
							break;
						case 3 :
							// .\\Ctrl.g:319:7: 
							{

							// AST REWRITE
							// elements: e
							// token labels: 
							// rule labels: e, retval
							// token list labels: 
							// rule list labels: 
							// wildcard labels: 
							if ( state.backtracking==0 ) {
							retval.tree = root_0;
							RewriteRuleSubtreeStream stream_e=new RewriteRuleSubtreeStream(adaptor,"rule e",e!=null?e.getTree():null);
							RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

							root_0 = (CtrlTree)adaptor.nil();
							// 319:7: -> $e
							{
								adaptor.addChild(root_0, stream_e.nextTree());
							}


							retval.tree = root_0;
							}

							}
							break;

					}

					}
					break;
				case 2 :
					// .\\Ctrl.g:325:5: op= SHARP expr_atom
					{
					op=(Token)match(input,SHARP,FOLLOW_SHARP_in_expr21803); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_SHARP.add(op);

					pushFollow(FOLLOW_expr_atom_in_expr21805);
					expr_atom97=expr_atom();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_expr_atom.add(expr_atom97.getTree());

					// AST REWRITE
					// elements: expr_atom
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CtrlTree)adaptor.nil();
					// 325:24: -> ^( ALAP[$op] expr_atom )
					{
						// .\\Ctrl.g:325:27: ^( ALAP[$op] expr_atom )
						{
						CtrlTree root_1 = (CtrlTree)adaptor.nil();
						root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(ALAP, op), root_1);
						adaptor.addChild(root_1, stream_expr_atom.nextTree());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "expr2"


	public static class expr_atom_return extends ParserRuleReturnScope {
		CtrlTree tree;
		@Override
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "expr_atom"
	// .\\Ctrl.g:328:1: expr_atom : (open= LPAR expr close= RPAR -> ^( BLOCK[$open] expr TRUE[$close] ) | assign | call );
	public final CtrlParser.expr_atom_return expr_atom() throws RecognitionException {
		CtrlParser.expr_atom_return retval = new CtrlParser.expr_atom_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		Token open=null;
		Token close=null;
		ParserRuleReturnScope expr98 =null;
		ParserRuleReturnScope assign99 =null;
		ParserRuleReturnScope call100 =null;

		CtrlTree open_tree=null;
		CtrlTree close_tree=null;
		RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
		RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
		RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");

		try {
			// .\\Ctrl.g:329:2: (open= LPAR expr close= RPAR -> ^( BLOCK[$open] expr TRUE[$close] ) | assign | call )
			int alt28=3;
			switch ( input.LA(1) ) {
			case LPAR:
				{
				alt28=1;
				}
				break;
			case ID:
				{
				int LA28_2 = input.LA(2);
				if ( (LA28_2==BECOMES||LA28_2==COMMA) ) {
					alt28=2;
				}
				else if ( (LA28_2==ASTERISK||LA28_2==BAR||LA28_2==DOT||LA28_2==LPAR||LA28_2==PLUS||(LA28_2 >= RPAR && LA28_2 <= SEMI)) ) {
					alt28=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 28, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case ANY:
			case ASTERISK:
			case OTHER:
				{
				alt28=3;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 28, 0, input);
				throw nvae;
			}
			switch (alt28) {
				case 1 :
					// .\\Ctrl.g:331:4: open= LPAR expr close= RPAR
					{
					open=(Token)match(input,LPAR,FOLLOW_LPAR_in_expr_atom1836); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_LPAR.add(open);

					pushFollow(FOLLOW_expr_in_expr_atom1838);
					expr98=expr();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_expr.add(expr98.getTree());
					close=(Token)match(input,RPAR,FOLLOW_RPAR_in_expr_atom1842); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_RPAR.add(close);


					// AST REWRITE
					// elements: expr
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CtrlTree)adaptor.nil();
					// 332:4: -> ^( BLOCK[$open] expr TRUE[$close] )
					{
						// .\\Ctrl.g:332:7: ^( BLOCK[$open] expr TRUE[$close] )
						{
						CtrlTree root_1 = (CtrlTree)adaptor.nil();
						root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(BLOCK, open), root_1);
						adaptor.addChild(root_1, stream_expr.nextTree());
						adaptor.addChild(root_1, (CtrlTree)adaptor.create(TRUE, close));
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// .\\Ctrl.g:335:5: assign
					{
					root_0 = (CtrlTree)adaptor.nil();


					pushFollow(FOLLOW_assign_in_expr_atom1873);
					assign99=assign();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, assign99.getTree());

					}
					break;
				case 3 :
					// .\\Ctrl.g:338:4: call
					{
					root_0 = (CtrlTree)adaptor.nil();


					pushFollow(FOLLOW_call_in_expr_atom1886);
					call100=call();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, call100.getTree());

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "expr_atom"


	public static class assign_return extends ParserRuleReturnScope {
		CtrlTree tree;
		@Override
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "assign"
	// .\\Ctrl.g:342:1: assign : target ( COMMA target )* BECOMES call -> ^( BECOMES ^( ARGS ( target )+ RPAR ) call ) ;
	public final CtrlParser.assign_return assign() throws RecognitionException {
		CtrlParser.assign_return retval = new CtrlParser.assign_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		Token COMMA102=null;
		Token BECOMES104=null;
		ParserRuleReturnScope target101 =null;
		ParserRuleReturnScope target103 =null;
		ParserRuleReturnScope call105 =null;

		CtrlTree COMMA102_tree=null;
		CtrlTree BECOMES104_tree=null;
		RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
		RewriteRuleTokenStream stream_BECOMES=new RewriteRuleTokenStream(adaptor,"token BECOMES");
		RewriteRuleSubtreeStream stream_call=new RewriteRuleSubtreeStream(adaptor,"rule call");
		RewriteRuleSubtreeStream stream_target=new RewriteRuleSubtreeStream(adaptor,"rule target");

		try {
			// .\\Ctrl.g:343:3: ( target ( COMMA target )* BECOMES call -> ^( BECOMES ^( ARGS ( target )+ RPAR ) call ) )
			// .\\Ctrl.g:350:5: target ( COMMA target )* BECOMES call
			{
			pushFollow(FOLLOW_target_in_assign1936);
			target101=target();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_target.add(target101.getTree());
			// .\\Ctrl.g:350:12: ( COMMA target )*
			loop29:
			while (true) {
				int alt29=2;
				int LA29_0 = input.LA(1);
				if ( (LA29_0==COMMA) ) {
					alt29=1;
				}

				switch (alt29) {
				case 1 :
					// .\\Ctrl.g:350:13: COMMA target
					{
					COMMA102=(Token)match(input,COMMA,FOLLOW_COMMA_in_assign1939); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_COMMA.add(COMMA102);

					pushFollow(FOLLOW_target_in_assign1941);
					target103=target();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_target.add(target103.getTree());
					}
					break;

				default :
					break loop29;
				}
			}

			BECOMES104=(Token)match(input,BECOMES,FOLLOW_BECOMES_in_assign1945); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_BECOMES.add(BECOMES104);

			pushFollow(FOLLOW_call_in_assign1947);
			call105=call();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_call.add(call105.getTree());

			// AST REWRITE
			// elements: BECOMES, target, call
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CtrlTree)adaptor.nil();
			// 351:5: -> ^( BECOMES ^( ARGS ( target )+ RPAR ) call )
			{
				// .\\Ctrl.g:351:8: ^( BECOMES ^( ARGS ( target )+ RPAR ) call )
				{
				CtrlTree root_1 = (CtrlTree)adaptor.nil();
				root_1 = (CtrlTree)adaptor.becomeRoot(stream_BECOMES.nextNode(), root_1);
				// .\\Ctrl.g:351:18: ^( ARGS ( target )+ RPAR )
				{
				CtrlTree root_2 = (CtrlTree)adaptor.nil();
				root_2 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(ARGS, "ARGS"), root_2);
				if ( !(stream_target.hasNext()) ) {
					throw new RewriteEarlyExitException();
				}
				while ( stream_target.hasNext() ) {
					adaptor.addChild(root_2, stream_target.nextTree());
				}
				stream_target.reset();

				adaptor.addChild(root_2, (CtrlTree)adaptor.create(RPAR, "RPAR"));
				adaptor.addChild(root_1, root_2);
				}

				adaptor.addChild(root_1, stream_call.nextTree());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "assign"


	public static class target_return extends ParserRuleReturnScope {
		CtrlTree tree;
		@Override
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "target"
	// .\\Ctrl.g:354:1: target : ID -> ^( ARG_OUT ID ) ;
	public final CtrlParser.target_return target() throws RecognitionException {
		CtrlParser.target_return retval = new CtrlParser.target_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		Token ID106=null;

		CtrlTree ID106_tree=null;
		RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

		try {
			// .\\Ctrl.g:355:3: ( ID -> ^( ARG_OUT ID ) )
			// .\\Ctrl.g:355:5: ID
			{
			ID106=(Token)match(input,ID,FOLLOW_ID_in_target1981); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_ID.add(ID106);


			// AST REWRITE
			// elements: ID
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CtrlTree)adaptor.nil();
			// 355:8: -> ^( ARG_OUT ID )
			{
				// .\\Ctrl.g:355:11: ^( ARG_OUT ID )
				{
				CtrlTree root_1 = (CtrlTree)adaptor.nil();
				root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(ARG_OUT, "ARG_OUT"), root_1);
				adaptor.addChild(root_1, stream_ID.nextNode());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "target"


	public static class call_return extends ParserRuleReturnScope {
		CtrlTree tree;
		@Override
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "call"
	// .\\Ctrl.g:359:1: call : rule_name ( arg_list[true] )? -> ^( CALL[$rule_name.start] rule_name ( arg_list )? ) ;
	public final CtrlParser.call_return call() throws RecognitionException {
		CtrlParser.call_return retval = new CtrlParser.call_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		ParserRuleReturnScope rule_name107 =null;
		ParserRuleReturnScope arg_list108 =null;

		RewriteRuleSubtreeStream stream_rule_name=new RewriteRuleSubtreeStream(adaptor,"rule rule_name");
		RewriteRuleSubtreeStream stream_arg_list=new RewriteRuleSubtreeStream(adaptor,"rule arg_list");

		try {
			// .\\Ctrl.g:360:2: ( rule_name ( arg_list[true] )? -> ^( CALL[$rule_name.start] rule_name ( arg_list )? ) )
			// .\\Ctrl.g:364:4: rule_name ( arg_list[true] )?
			{
			pushFollow(FOLLOW_rule_name_in_call2019);
			rule_name107=rule_name();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_rule_name.add(rule_name107.getTree());
			// .\\Ctrl.g:364:14: ( arg_list[true] )?
			int alt30=2;
			int LA30_0 = input.LA(1);
			if ( (LA30_0==LPAR) ) {
				alt30=1;
			}
			switch (alt30) {
				case 1 :
					// .\\Ctrl.g:364:14: arg_list[true]
					{
					pushFollow(FOLLOW_arg_list_in_call2021);
					arg_list108=arg_list(true);
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_arg_list.add(arg_list108.getTree());
					}
					break;

			}

			if ( state.backtracking==0 ) { helper.registerCall((rule_name107!=null?((CtrlTree)rule_name107.getTree()):null)); }

			// AST REWRITE
			// elements: arg_list, rule_name
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CtrlTree)adaptor.nil();
			// 366:4: -> ^( CALL[$rule_name.start] rule_name ( arg_list )? )
			{
				// .\\Ctrl.g:366:7: ^( CALL[$rule_name.start] rule_name ( arg_list )? )
				{
				CtrlTree root_1 = (CtrlTree)adaptor.nil();
				root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(CALL, (rule_name107!=null?(rule_name107.start):null)), root_1);
				adaptor.addChild(root_1, stream_rule_name.nextTree());
				// .\\Ctrl.g:366:42: ( arg_list )?
				if ( stream_arg_list.hasNext() ) {
					adaptor.addChild(root_1, stream_arg_list.nextTree());
				}
				stream_arg_list.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "call"


	public static class rule_name_return extends ParserRuleReturnScope {
		CtrlTree tree;
		@Override
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "rule_name"
	// .\\Ctrl.g:370:1: rule_name : qual_name[true] ->;
	public final CtrlParser.rule_name_return rule_name() throws RecognitionException {
		CtrlParser.rule_name_return retval = new CtrlParser.rule_name_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		ParserRuleReturnScope qual_name109 =null;

		RewriteRuleSubtreeStream stream_qual_name=new RewriteRuleSubtreeStream(adaptor,"rule qual_name");

		try {
			// .\\Ctrl.g:371:3: ( qual_name[true] ->)
			// .\\Ctrl.g:381:5: qual_name[true]
			{
			pushFollow(FOLLOW_qual_name_in_rule_name2108);
			qual_name109=qual_name(true);
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_qual_name.add(qual_name109.getTree());

			// AST REWRITE
			// elements: 
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CtrlTree)adaptor.nil();
			// 382:5: ->
			{
				adaptor.addChild(root_0,  helper.qualify((qual_name109!=null?((CtrlTree)qual_name109.getTree()):null)) );
			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "rule_name"


	public static class arg_list_return extends ParserRuleReturnScope {
		CtrlTree tree;
		@Override
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "arg_list"
	// .\\Ctrl.g:388:1: arg_list[boolean out] : open= LPAR ( arg[out] ( COMMA arg[out] )* )? close= RPAR -> ^( ARGS[$open] ( arg )* RPAR[$close] ) ;
	public final CtrlParser.arg_list_return arg_list(boolean out) throws RecognitionException {
		CtrlParser.arg_list_return retval = new CtrlParser.arg_list_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		Token open=null;
		Token close=null;
		Token COMMA111=null;
		ParserRuleReturnScope arg110 =null;
		ParserRuleReturnScope arg112 =null;

		CtrlTree open_tree=null;
		CtrlTree close_tree=null;
		CtrlTree COMMA111_tree=null;
		RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
		RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
		RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
		RewriteRuleSubtreeStream stream_arg=new RewriteRuleSubtreeStream(adaptor,"rule arg");

		try {
			// .\\Ctrl.g:389:3: (open= LPAR ( arg[out] ( COMMA arg[out] )* )? close= RPAR -> ^( ARGS[$open] ( arg )* RPAR[$close] ) )
			// .\\Ctrl.g:391:5: open= LPAR ( arg[out] ( COMMA arg[out] )* )? close= RPAR
			{
			open=(Token)match(input,LPAR,FOLLOW_LPAR_in_arg_list2145); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_LPAR.add(open);

			// .\\Ctrl.g:391:15: ( arg[out] ( COMMA arg[out] )* )?
			int alt32=2;
			int LA32_0 = input.LA(1);
			if ( (LA32_0==FALSE||LA32_0==ID||LA32_0==INT_LIT||LA32_0==LPAR||LA32_0==MINUS||LA32_0==NOT||LA32_0==OUT||LA32_0==REAL_LIT||(LA32_0 >= STRING_LIT && LA32_0 <= TRUE)||LA32_0==UNDER) ) {
				alt32=1;
			}
			switch (alt32) {
				case 1 :
					// .\\Ctrl.g:391:16: arg[out] ( COMMA arg[out] )*
					{
					pushFollow(FOLLOW_arg_in_arg_list2148);
					arg110=arg(out);
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_arg.add(arg110.getTree());
					// .\\Ctrl.g:391:25: ( COMMA arg[out] )*
					loop31:
					while (true) {
						int alt31=2;
						int LA31_0 = input.LA(1);
						if ( (LA31_0==COMMA) ) {
							alt31=1;
						}

						switch (alt31) {
						case 1 :
							// .\\Ctrl.g:391:26: COMMA arg[out]
							{
							COMMA111=(Token)match(input,COMMA,FOLLOW_COMMA_in_arg_list2152); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_COMMA.add(COMMA111);

							pushFollow(FOLLOW_arg_in_arg_list2154);
							arg112=arg(out);
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_arg.add(arg112.getTree());
							}
							break;

						default :
							break loop31;
						}
					}

					}
					break;

			}

			close=(Token)match(input,RPAR,FOLLOW_RPAR_in_arg_list2163); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_RPAR.add(close);


			// AST REWRITE
			// elements: arg, RPAR
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CtrlTree)adaptor.nil();
			// 392:5: -> ^( ARGS[$open] ( arg )* RPAR[$close] )
			{
				// .\\Ctrl.g:392:8: ^( ARGS[$open] ( arg )* RPAR[$close] )
				{
				CtrlTree root_1 = (CtrlTree)adaptor.nil();
				root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(ARGS, open), root_1);
				// .\\Ctrl.g:392:22: ( arg )*
				while ( stream_arg.hasNext() ) {
					adaptor.addChild(root_1, stream_arg.nextTree());
				}
				stream_arg.reset();

				adaptor.addChild(root_1, (CtrlTree)adaptor.create(RPAR, close));
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "arg_list"


	public static class arg_return extends ParserRuleReturnScope {
		CtrlTree tree;
		@Override
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "arg"
	// .\\Ctrl.g:398:1: arg[boolean out] : ({...}? OUT ID -> ^( ARG_OUT ID ) |{...}? UNDER -> ^( ARG_WILD ) | in_arg );
	public final CtrlParser.arg_return arg(boolean out) throws RecognitionException {
		CtrlParser.arg_return retval = new CtrlParser.arg_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		Token OUT113=null;
		Token ID114=null;
		Token UNDER115=null;
		ParserRuleReturnScope in_arg116 =null;

		CtrlTree OUT113_tree=null;
		CtrlTree ID114_tree=null;
		CtrlTree UNDER115_tree=null;
		RewriteRuleTokenStream stream_UNDER=new RewriteRuleTokenStream(adaptor,"token UNDER");
		RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
		RewriteRuleTokenStream stream_OUT=new RewriteRuleTokenStream(adaptor,"token OUT");

		try {
			// .\\Ctrl.g:399:3: ({...}? OUT ID -> ^( ARG_OUT ID ) |{...}? UNDER -> ^( ARG_WILD ) | in_arg )
			int alt33=3;
			switch ( input.LA(1) ) {
			case OUT:
				{
				alt33=1;
				}
				break;
			case UNDER:
				{
				alt33=2;
				}
				break;
			case FALSE:
			case ID:
			case INT_LIT:
			case LPAR:
			case MINUS:
			case NOT:
			case REAL_LIT:
			case STRING_LIT:
			case TRUE:
				{
				alt33=3;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 33, 0, input);
				throw nvae;
			}
			switch (alt33) {
				case 1 :
					// .\\Ctrl.g:402:5: {...}? OUT ID
					{
					if ( !(( out )) ) {
						if (state.backtracking>0) {state.failed=true; return retval;}
						throw new FailedPredicateException(input, "arg", " out ");
					}
					OUT113=(Token)match(input,OUT,FOLLOW_OUT_in_arg2213); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_OUT.add(OUT113);

					ID114=(Token)match(input,ID,FOLLOW_ID_in_arg2215); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_ID.add(ID114);


					// AST REWRITE
					// elements: ID
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CtrlTree)adaptor.nil();
					// 402:21: -> ^( ARG_OUT ID )
					{
						// .\\Ctrl.g:402:24: ^( ARG_OUT ID )
						{
						CtrlTree root_1 = (CtrlTree)adaptor.nil();
						root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(ARG_OUT, "ARG_OUT"), root_1);
						adaptor.addChild(root_1, stream_ID.nextNode());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// .\\Ctrl.g:406:5: {...}? UNDER
					{
					if ( !(( out )) ) {
						if (state.backtracking>0) {state.failed=true; return retval;}
						throw new FailedPredicateException(input, "arg", " out ");
					}
					UNDER115=(Token)match(input,UNDER,FOLLOW_UNDER_in_arg2246); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_UNDER.add(UNDER115);


					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CtrlTree)adaptor.nil();
					// 406:20: -> ^( ARG_WILD )
					{
						// .\\Ctrl.g:406:23: ^( ARG_WILD )
						{
						CtrlTree root_1 = (CtrlTree)adaptor.nil();
						root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(ARG_WILD, "ARG_WILD"), root_1);
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 3 :
					// .\\Ctrl.g:407:5: in_arg
					{
					root_0 = (CtrlTree)adaptor.nil();


					pushFollow(FOLLOW_in_arg_in_arg2261);
					in_arg116=in_arg();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, in_arg116.getTree());

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "arg"


	public static class in_arg_return extends ParserRuleReturnScope {
		CtrlTree tree;
		@Override
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "in_arg"
	// .\\Ctrl.g:413:1: in_arg : ( op1 in_arg -> ^( ARG_OP op1 in_arg ) | in_atom ( op2 in_arg -> ^( ARG_OP op2 in_atom in_arg ) | -> in_atom ) );
	public final CtrlParser.in_arg_return in_arg() throws RecognitionException {
		CtrlParser.in_arg_return retval = new CtrlParser.in_arg_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		ParserRuleReturnScope op1117 =null;
		ParserRuleReturnScope in_arg118 =null;
		ParserRuleReturnScope in_atom119 =null;
		ParserRuleReturnScope op2120 =null;
		ParserRuleReturnScope in_arg121 =null;

		RewriteRuleSubtreeStream stream_in_atom=new RewriteRuleSubtreeStream(adaptor,"rule in_atom");
		RewriteRuleSubtreeStream stream_op2=new RewriteRuleSubtreeStream(adaptor,"rule op2");
		RewriteRuleSubtreeStream stream_op1=new RewriteRuleSubtreeStream(adaptor,"rule op1");
		RewriteRuleSubtreeStream stream_in_arg=new RewriteRuleSubtreeStream(adaptor,"rule in_arg");

		try {
			// .\\Ctrl.g:414:3: ( op1 in_arg -> ^( ARG_OP op1 in_arg ) | in_atom ( op2 in_arg -> ^( ARG_OP op2 in_atom in_arg ) | -> in_atom ) )
			int alt35=2;
			int LA35_0 = input.LA(1);
			if ( (LA35_0==MINUS||LA35_0==NOT) ) {
				alt35=1;
			}
			else if ( (LA35_0==FALSE||LA35_0==ID||LA35_0==INT_LIT||LA35_0==LPAR||LA35_0==REAL_LIT||(LA35_0 >= STRING_LIT && LA35_0 <= TRUE)) ) {
				alt35=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 35, 0, input);
				throw nvae;
			}

			switch (alt35) {
				case 1 :
					// .\\Ctrl.g:419:5: op1 in_arg
					{
					pushFollow(FOLLOW_op1_in_in_arg2301);
					op1117=op1();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_op1.add(op1117.getTree());
					pushFollow(FOLLOW_in_arg_in_in_arg2303);
					in_arg118=in_arg();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_in_arg.add(in_arg118.getTree());

					// AST REWRITE
					// elements: op1, in_arg
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CtrlTree)adaptor.nil();
					// 419:16: -> ^( ARG_OP op1 in_arg )
					{
						// .\\Ctrl.g:419:19: ^( ARG_OP op1 in_arg )
						{
						CtrlTree root_1 = (CtrlTree)adaptor.nil();
						root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(ARG_OP, "ARG_OP"), root_1);
						adaptor.addChild(root_1, stream_op1.nextTree());
						adaptor.addChild(root_1, stream_in_arg.nextTree());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// .\\Ctrl.g:426:5: in_atom ( op2 in_arg -> ^( ARG_OP op2 in_atom in_arg ) | -> in_atom )
					{
					pushFollow(FOLLOW_in_atom_in_in_arg2349);
					in_atom119=in_atom();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_in_atom.add(in_atom119.getTree());
					// .\\Ctrl.g:426:13: ( op2 in_arg -> ^( ARG_OP op2 in_atom in_arg ) | -> in_atom )
					int alt34=2;
					int LA34_0 = input.LA(1);
					if ( (LA34_0==AMP||LA34_0==ASTERISK||LA34_0==BAR||LA34_0==EQ||LA34_0==GEQ||LA34_0==LANGLE||LA34_0==LEQ||LA34_0==MINUS||LA34_0==NEQ||LA34_0==NOT||(LA34_0 >= PERCENT && LA34_0 <= PLUS)||LA34_0==RANGLE||LA34_0==SLASH) ) {
						alt34=1;
					}
					else if ( (LA34_0==COMMA||LA34_0==RPAR) ) {
						alt34=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						NoViableAltException nvae =
							new NoViableAltException("", 34, 0, input);
						throw nvae;
					}

					switch (alt34) {
						case 1 :
							// .\\Ctrl.g:426:15: op2 in_arg
							{
							pushFollow(FOLLOW_op2_in_in_arg2353);
							op2120=op2();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_op2.add(op2120.getTree());
							pushFollow(FOLLOW_in_arg_in_in_arg2355);
							in_arg121=in_arg();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_in_arg.add(in_arg121.getTree());

							// AST REWRITE
							// elements: op2, in_arg, in_atom
							// token labels: 
							// rule labels: retval
							// token list labels: 
							// rule list labels: 
							// wildcard labels: 
							if ( state.backtracking==0 ) {
							retval.tree = root_0;
							RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

							root_0 = (CtrlTree)adaptor.nil();
							// 426:26: -> ^( ARG_OP op2 in_atom in_arg )
							{
								// .\\Ctrl.g:426:29: ^( ARG_OP op2 in_atom in_arg )
								{
								CtrlTree root_1 = (CtrlTree)adaptor.nil();
								root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(ARG_OP, "ARG_OP"), root_1);
								adaptor.addChild(root_1, stream_op2.nextTree());
								adaptor.addChild(root_1, stream_in_atom.nextTree());
								adaptor.addChild(root_1, stream_in_arg.nextTree());
								adaptor.addChild(root_0, root_1);
								}

							}


							retval.tree = root_0;
							}

							}
							break;
						case 2 :
							// .\\Ctrl.g:427:15: 
							{

							// AST REWRITE
							// elements: in_atom
							// token labels: 
							// rule labels: retval
							// token list labels: 
							// rule list labels: 
							// wildcard labels: 
							if ( state.backtracking==0 ) {
							retval.tree = root_0;
							RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

							root_0 = (CtrlTree)adaptor.nil();
							// 427:15: -> in_atom
							{
								adaptor.addChild(root_0, stream_in_atom.nextTree());
							}


							retval.tree = root_0;
							}

							}
							break;

					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "in_arg"


	public static class in_atom_return extends ParserRuleReturnScope {
		CtrlTree tree;
		@Override
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "in_atom"
	// .\\Ctrl.g:431:1: in_atom : ( ID ( arg_list[false] -> ^( ARG_CALL ID arg_list ) | -> ^( ARG_ID ID ) ) | literal -> ^( ARG_LIT literal ) | LPAR ( in_arg RPAR -> in_arg | ( REAL | INT | STRING ) RPAR in_arg -> ^( ARG_OP LPAR in_arg ) ) );
	public final CtrlParser.in_atom_return in_atom() throws RecognitionException {
		CtrlParser.in_atom_return retval = new CtrlParser.in_atom_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		Token ID122=null;
		Token LPAR125=null;
		Token RPAR127=null;
		Token REAL128=null;
		Token INT129=null;
		Token STRING130=null;
		Token RPAR131=null;
		ParserRuleReturnScope arg_list123 =null;
		ParserRuleReturnScope literal124 =null;
		ParserRuleReturnScope in_arg126 =null;
		ParserRuleReturnScope in_arg132 =null;

		CtrlTree ID122_tree=null;
		CtrlTree LPAR125_tree=null;
		CtrlTree RPAR127_tree=null;
		CtrlTree REAL128_tree=null;
		CtrlTree INT129_tree=null;
		CtrlTree STRING130_tree=null;
		CtrlTree RPAR131_tree=null;
		RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
		RewriteRuleTokenStream stream_REAL=new RewriteRuleTokenStream(adaptor,"token REAL");
		RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
		RewriteRuleTokenStream stream_STRING=new RewriteRuleTokenStream(adaptor,"token STRING");
		RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
		RewriteRuleTokenStream stream_INT=new RewriteRuleTokenStream(adaptor,"token INT");
		RewriteRuleSubtreeStream stream_in_arg=new RewriteRuleSubtreeStream(adaptor,"rule in_arg");
		RewriteRuleSubtreeStream stream_arg_list=new RewriteRuleSubtreeStream(adaptor,"rule arg_list");
		RewriteRuleSubtreeStream stream_literal=new RewriteRuleSubtreeStream(adaptor,"rule literal");

		try {
			// .\\Ctrl.g:432:3: ( ID ( arg_list[false] -> ^( ARG_CALL ID arg_list ) | -> ^( ARG_ID ID ) ) | literal -> ^( ARG_LIT literal ) | LPAR ( in_arg RPAR -> in_arg | ( REAL | INT | STRING ) RPAR in_arg -> ^( ARG_OP LPAR in_arg ) ) )
			int alt39=3;
			switch ( input.LA(1) ) {
			case ID:
				{
				alt39=1;
				}
				break;
			case FALSE:
			case INT_LIT:
			case REAL_LIT:
			case STRING_LIT:
			case TRUE:
				{
				alt39=2;
				}
				break;
			case LPAR:
				{
				alt39=3;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 39, 0, input);
				throw nvae;
			}
			switch (alt39) {
				case 1 :
					// .\\Ctrl.g:441:5: ID ( arg_list[false] -> ^( ARG_CALL ID arg_list ) | -> ^( ARG_ID ID ) )
					{
					ID122=(Token)match(input,ID,FOLLOW_ID_in_in_atom2457); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_ID.add(ID122);

					// .\\Ctrl.g:441:8: ( arg_list[false] -> ^( ARG_CALL ID arg_list ) | -> ^( ARG_ID ID ) )
					int alt36=2;
					int LA36_0 = input.LA(1);
					if ( (LA36_0==LPAR) ) {
						alt36=1;
					}
					else if ( (LA36_0==AMP||LA36_0==ASTERISK||LA36_0==BAR||LA36_0==COMMA||LA36_0==EQ||LA36_0==GEQ||LA36_0==LANGLE||LA36_0==LEQ||LA36_0==MINUS||LA36_0==NEQ||LA36_0==NOT||(LA36_0 >= PERCENT && LA36_0 <= PLUS)||LA36_0==RANGLE||LA36_0==RPAR||LA36_0==SLASH) ) {
						alt36=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						NoViableAltException nvae =
							new NoViableAltException("", 36, 0, input);
						throw nvae;
					}

					switch (alt36) {
						case 1 :
							// .\\Ctrl.g:441:10: arg_list[false]
							{
							pushFollow(FOLLOW_arg_list_in_in_atom2461);
							arg_list123=arg_list(false);
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_arg_list.add(arg_list123.getTree());

							// AST REWRITE
							// elements: arg_list, ID
							// token labels: 
							// rule labels: retval
							// token list labels: 
							// rule list labels: 
							// wildcard labels: 
							if ( state.backtracking==0 ) {
							retval.tree = root_0;
							RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

							root_0 = (CtrlTree)adaptor.nil();
							// 441:26: -> ^( ARG_CALL ID arg_list )
							{
								// .\\Ctrl.g:441:29: ^( ARG_CALL ID arg_list )
								{
								CtrlTree root_1 = (CtrlTree)adaptor.nil();
								root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(ARG_CALL, "ARG_CALL"), root_1);
								adaptor.addChild(root_1, stream_ID.nextNode());
								adaptor.addChild(root_1, stream_arg_list.nextTree());
								adaptor.addChild(root_0, root_1);
								}

							}


							retval.tree = root_0;
							}

							}
							break;
						case 2 :
							// .\\Ctrl.g:442:10: 
							{

							// AST REWRITE
							// elements: ID
							// token labels: 
							// rule labels: retval
							// token list labels: 
							// rule list labels: 
							// wildcard labels: 
							if ( state.backtracking==0 ) {
							retval.tree = root_0;
							RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

							root_0 = (CtrlTree)adaptor.nil();
							// 442:10: -> ^( ARG_ID ID )
							{
								// .\\Ctrl.g:442:13: ^( ARG_ID ID )
								{
								CtrlTree root_1 = (CtrlTree)adaptor.nil();
								root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(ARG_ID, "ARG_ID"), root_1);
								adaptor.addChild(root_1, stream_ID.nextNode());
								adaptor.addChild(root_0, root_1);
								}

							}


							retval.tree = root_0;
							}

							}
							break;

					}

					}
					break;
				case 2 :
					// .\\Ctrl.g:444:5: literal
					{
					pushFollow(FOLLOW_literal_in_in_atom2504);
					literal124=literal();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_literal.add(literal124.getTree());

					// AST REWRITE
					// elements: literal
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CtrlTree)adaptor.nil();
					// 444:13: -> ^( ARG_LIT literal )
					{
						// .\\Ctrl.g:444:16: ^( ARG_LIT literal )
						{
						CtrlTree root_1 = (CtrlTree)adaptor.nil();
						root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(ARG_LIT, "ARG_LIT"), root_1);
						adaptor.addChild(root_1, stream_literal.nextTree());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 3 :
					// .\\Ctrl.g:447:5: LPAR ( in_arg RPAR -> in_arg | ( REAL | INT | STRING ) RPAR in_arg -> ^( ARG_OP LPAR in_arg ) )
					{
					LPAR125=(Token)match(input,LPAR,FOLLOW_LPAR_in_in_atom2528); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_LPAR.add(LPAR125);

					// .\\Ctrl.g:447:10: ( in_arg RPAR -> in_arg | ( REAL | INT | STRING ) RPAR in_arg -> ^( ARG_OP LPAR in_arg ) )
					int alt38=2;
					int LA38_0 = input.LA(1);
					if ( (LA38_0==FALSE||LA38_0==ID||LA38_0==INT_LIT||LA38_0==LPAR||LA38_0==MINUS||LA38_0==NOT||LA38_0==REAL_LIT||(LA38_0 >= STRING_LIT && LA38_0 <= TRUE)) ) {
						alt38=1;
					}
					else if ( (LA38_0==INT||LA38_0==REAL||LA38_0==STRING) ) {
						alt38=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						NoViableAltException nvae =
							new NoViableAltException("", 38, 0, input);
						throw nvae;
					}

					switch (alt38) {
						case 1 :
							// .\\Ctrl.g:448:12: in_arg RPAR
							{
							pushFollow(FOLLOW_in_arg_in_in_atom2544);
							in_arg126=in_arg();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_in_arg.add(in_arg126.getTree());
							RPAR127=(Token)match(input,RPAR,FOLLOW_RPAR_in_in_atom2546); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_RPAR.add(RPAR127);


							// AST REWRITE
							// elements: in_arg
							// token labels: 
							// rule labels: retval
							// token list labels: 
							// rule list labels: 
							// wildcard labels: 
							if ( state.backtracking==0 ) {
							retval.tree = root_0;
							RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

							root_0 = (CtrlTree)adaptor.nil();
							// 448:24: -> in_arg
							{
								adaptor.addChild(root_0, stream_in_arg.nextTree());
							}


							retval.tree = root_0;
							}

							}
							break;
						case 2 :
							// .\\Ctrl.g:450:12: ( REAL | INT | STRING ) RPAR in_arg
							{
							// .\\Ctrl.g:450:12: ( REAL | INT | STRING )
							int alt37=3;
							switch ( input.LA(1) ) {
							case REAL:
								{
								alt37=1;
								}
								break;
							case INT:
								{
								alt37=2;
								}
								break;
							case STRING:
								{
								alt37=3;
								}
								break;
							default:
								if (state.backtracking>0) {state.failed=true; return retval;}
								NoViableAltException nvae =
									new NoViableAltException("", 37, 0, input);
								throw nvae;
							}
							switch (alt37) {
								case 1 :
									// .\\Ctrl.g:450:13: REAL
									{
									REAL128=(Token)match(input,REAL,FOLLOW_REAL_in_in_atom2576); if (state.failed) return retval; 
									if ( state.backtracking==0 ) stream_REAL.add(REAL128);

									}
									break;
								case 2 :
									// .\\Ctrl.g:450:20: INT
									{
									INT129=(Token)match(input,INT,FOLLOW_INT_in_in_atom2580); if (state.failed) return retval; 
									if ( state.backtracking==0 ) stream_INT.add(INT129);

									}
									break;
								case 3 :
									// .\\Ctrl.g:450:26: STRING
									{
									STRING130=(Token)match(input,STRING,FOLLOW_STRING_in_in_atom2584); if (state.failed) return retval; 
									if ( state.backtracking==0 ) stream_STRING.add(STRING130);

									}
									break;

							}

							RPAR131=(Token)match(input,RPAR,FOLLOW_RPAR_in_in_atom2587); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_RPAR.add(RPAR131);

							pushFollow(FOLLOW_in_arg_in_in_atom2589);
							in_arg132=in_arg();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_in_arg.add(in_arg132.getTree());

							// AST REWRITE
							// elements: in_arg, LPAR
							// token labels: 
							// rule labels: retval
							// token list labels: 
							// rule list labels: 
							// wildcard labels: 
							if ( state.backtracking==0 ) {
							retval.tree = root_0;
							RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

							root_0 = (CtrlTree)adaptor.nil();
							// 450:46: -> ^( ARG_OP LPAR in_arg )
							{
								// .\\Ctrl.g:450:49: ^( ARG_OP LPAR in_arg )
								{
								CtrlTree root_1 = (CtrlTree)adaptor.nil();
								root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(ARG_OP, "ARG_OP"), root_1);
								adaptor.addChild(root_1, stream_LPAR.nextNode());
								adaptor.addChild(root_1, stream_in_arg.nextTree());
								adaptor.addChild(root_0, root_1);
								}

							}


							retval.tree = root_0;
							}

							}
							break;

					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "in_atom"


	public static class op1_return extends ParserRuleReturnScope {
		CtrlTree tree;
		@Override
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "op1"
	// .\\Ctrl.g:455:1: op1 : ( MINUS | NOT );
	public final CtrlParser.op1_return op1() throws RecognitionException {
		CtrlParser.op1_return retval = new CtrlParser.op1_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		Token set133=null;

		CtrlTree set133_tree=null;

		try {
			// .\\Ctrl.g:456:3: ( MINUS | NOT )
			// .\\Ctrl.g:
			{
			root_0 = (CtrlTree)adaptor.nil();


			set133=input.LT(1);
			if ( input.LA(1)==MINUS||input.LA(1)==NOT ) {
				input.consume();
				if ( state.backtracking==0 ) adaptor.addChild(root_0, (CtrlTree)adaptor.create(set133));
				state.errorRecovery=false;
				state.failed=false;
			}
			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}
			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "op1"


	public static class op2_return extends ParserRuleReturnScope {
		CtrlTree tree;
		@Override
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "op2"
	// .\\Ctrl.g:461:1: op2 : ( LANGLE | RANGLE | LEQ | GEQ | EQ | NEQ | PLUS | MINUS | PERCENT | ASTERISK | SLASH | AMP | BAR | NOT );
	public final CtrlParser.op2_return op2() throws RecognitionException {
		CtrlParser.op2_return retval = new CtrlParser.op2_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		Token set134=null;

		CtrlTree set134_tree=null;

		try {
			// .\\Ctrl.g:462:3: ( LANGLE | RANGLE | LEQ | GEQ | EQ | NEQ | PLUS | MINUS | PERCENT | ASTERISK | SLASH | AMP | BAR | NOT )
			// .\\Ctrl.g:
			{
			root_0 = (CtrlTree)adaptor.nil();


			set134=input.LT(1);
			if ( input.LA(1)==AMP||input.LA(1)==ASTERISK||input.LA(1)==BAR||input.LA(1)==EQ||input.LA(1)==GEQ||input.LA(1)==LANGLE||input.LA(1)==LEQ||input.LA(1)==MINUS||input.LA(1)==NEQ||input.LA(1)==NOT||(input.LA(1) >= PERCENT && input.LA(1) <= PLUS)||input.LA(1)==RANGLE||input.LA(1)==SLASH ) {
				input.consume();
				if ( state.backtracking==0 ) adaptor.addChild(root_0, (CtrlTree)adaptor.create(set134));
				state.errorRecovery=false;
				state.failed=false;
			}
			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}
			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "op2"


	public static class literal_return extends ParserRuleReturnScope {
		CtrlTree tree;
		@Override
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "literal"
	// .\\Ctrl.g:465:1: literal : ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT );
	public final CtrlParser.literal_return literal() throws RecognitionException {
		CtrlParser.literal_return retval = new CtrlParser.literal_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		Token set135=null;

		CtrlTree set135_tree=null;

		try {
			// .\\Ctrl.g:466:3: ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT )
			// .\\Ctrl.g:
			{
			root_0 = (CtrlTree)adaptor.nil();


			set135=input.LT(1);
			if ( input.LA(1)==FALSE||input.LA(1)==INT_LIT||input.LA(1)==REAL_LIT||(input.LA(1) >= STRING_LIT && input.LA(1) <= TRUE) ) {
				input.consume();
				if ( state.backtracking==0 ) adaptor.addChild(root_0, (CtrlTree)adaptor.create(set135));
				state.errorRecovery=false;
				state.failed=false;
			}
			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}
			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "literal"


	public static class var_type_return extends ParserRuleReturnScope {
		CtrlTree tree;
		@Override
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "var_type"
	// .\\Ctrl.g:484:1: var_type : ( NODE | BOOL | STRING | INT | REAL );
	public final CtrlParser.var_type_return var_type() throws RecognitionException {
		CtrlParser.var_type_return retval = new CtrlParser.var_type_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		Token set136=null;

		CtrlTree set136_tree=null;

		try {
			// .\\Ctrl.g:485:2: ( NODE | BOOL | STRING | INT | REAL )
			// .\\Ctrl.g:
			{
			root_0 = (CtrlTree)adaptor.nil();


			set136=input.LT(1);
			if ( input.LA(1)==BOOL||input.LA(1)==INT||input.LA(1)==NODE||input.LA(1)==REAL||input.LA(1)==STRING ) {
				input.consume();
				if ( state.backtracking==0 ) adaptor.addChild(root_0, (CtrlTree)adaptor.create(set136));
				state.errorRecovery=false;
				state.failed=false;
			}
			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}
			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "var_type"

	// $ANTLR start synpred1_Ctrl
	public final void synpred1_Ctrl_fragment() throws RecognitionException {
		// .\\Ctrl.g:245:33: ( ELSE )
		// .\\Ctrl.g:245:34: ELSE
		{
		match(input,ELSE,FOLLOW_ELSE_in_synpred1_Ctrl1271); if (state.failed) return;

		}

	}
	// $ANTLR end synpred1_Ctrl

	// $ANTLR start synpred2_Ctrl
	public final void synpred2_Ctrl_fragment() throws RecognitionException {
		// .\\Ctrl.g:249:17: ( ELSE )
		// .\\Ctrl.g:249:18: ELSE
		{
		match(input,ELSE,FOLLOW_ELSE_in_synpred2_Ctrl1311); if (state.failed) return;

		}

	}
	// $ANTLR end synpred2_Ctrl

	// $ANTLR start synpred3_Ctrl
	public final void synpred3_Ctrl_fragment() throws RecognitionException {
		// .\\Ctrl.g:252:20: ( OR )
		// .\\Ctrl.g:252:21: OR
		{
		match(input,OR,FOLLOW_OR_in_synpred3_Ctrl1346); if (state.failed) return;

		}

	}
	// $ANTLR end synpred3_Ctrl

	// Delegated rules

	public final boolean synpred1_Ctrl() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred1_Ctrl_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred2_Ctrl() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred2_Ctrl_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred3_Ctrl() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred3_Ctrl_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}



	public static final BitSet FOLLOW_package_decl_in_program191 = new BitSet(new long[]{0x0442CBC804884050L,0x0000000001522280L});
	public static final BitSet FOLLOW_import_decl_in_program197 = new BitSet(new long[]{0x0442CBC804884050L,0x0000000001522280L});
	public static final BitSet FOLLOW_function_in_program205 = new BitSet(new long[]{0x0442C9C804884050L,0x0000000001522280L});
	public static final BitSet FOLLOW_recipe_in_program207 = new BitSet(new long[]{0x0442C9C804884050L,0x0000000001522280L});
	public static final BitSet FOLLOW_stat_in_program209 = new BitSet(new long[]{0x0442C9C804884050L,0x0000000001522280L});
	public static final BitSet FOLLOW_EOF_in_program213 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_PACKAGE_in_package_decl350 = new BitSet(new long[]{0x0400008000004040L});
	public static final BitSet FOLLOW_qual_name_in_package_decl352 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
	public static final BitSet FOLLOW_SEMI_in_package_decl357 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_IMPORT_in_import_decl424 = new BitSet(new long[]{0x0400008000004040L});
	public static final BitSet FOLLOW_qual_name_in_import_decl427 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
	public static final BitSet FOLLOW_SEMI_in_import_decl430 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_qual_name472 = new BitSet(new long[]{0x0000000008000002L});
	public static final BitSet FOLLOW_DOT_in_qual_name476 = new BitSet(new long[]{0x0400008000004040L});
	public static final BitSet FOLLOW_qual_name_in_qual_name480 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ASTERISK_in_qual_name519 = new BitSet(new long[]{0x0000000008000000L});
	public static final BitSet FOLLOW_DOT_in_qual_name521 = new BitSet(new long[]{0x0400000000000040L});
	public static final BitSet FOLLOW_ANY_in_qual_name541 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_OTHER_in_qual_name564 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_RECIPE_in_recipe633 = new BitSet(new long[]{0x0000008000000000L});
	public static final BitSet FOLLOW_ID_in_recipe636 = new BitSet(new long[]{0x0002000000000000L});
	public static final BitSet FOLLOW_par_list_in_recipe638 = new BitSet(new long[]{0x0000800000000000L,0x0000000000000002L});
	public static final BitSet FOLLOW_PRIORITY_in_recipe641 = new BitSet(new long[]{0x0000100000000000L});
	public static final BitSet FOLLOW_INT_LIT_in_recipe644 = new BitSet(new long[]{0x0000800000000000L});
	public static final BitSet FOLLOW_block_in_recipe658 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_FUNCTION_in_function704 = new BitSet(new long[]{0x0000008000000000L});
	public static final BitSet FOLLOW_ID_in_function707 = new BitSet(new long[]{0x0002000000000000L});
	public static final BitSet FOLLOW_par_list_in_function709 = new BitSet(new long[]{0x0000800000000000L});
	public static final BitSet FOLLOW_block_in_function722 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LPAR_in_par_list753 = new BitSet(new long[]{0x0840080000080000L,0x0000000000020880L});
	public static final BitSet FOLLOW_par_in_par_list756 = new BitSet(new long[]{0x0000000002000000L,0x0000000000000800L});
	public static final BitSet FOLLOW_COMMA_in_par_list759 = new BitSet(new long[]{0x0840080000080000L,0x0000000000020080L});
	public static final BitSet FOLLOW_par_in_par_list761 = new BitSet(new long[]{0x0000000002000000L,0x0000000000000800L});
	public static final BitSet FOLLOW_RPAR_in_par_list767 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_OUT_in_par812 = new BitSet(new long[]{0x0040080000080000L,0x0000000000020080L});
	public static final BitSet FOLLOW_var_type_in_par814 = new BitSet(new long[]{0x0000008000000000L});
	public static final BitSet FOLLOW_ID_in_par816 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_var_type_in_par849 = new BitSet(new long[]{0x0000008000000000L});
	public static final BitSet FOLLOW_ID_in_par851 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LCURLY_in_block890 = new BitSet(new long[]{0x0442C9C004884050L,0x00000000015220C0L});
	public static final BitSet FOLLOW_stat_in_block892 = new BitSet(new long[]{0x0442C9C004884050L,0x00000000015220C0L});
	public static final BitSet FOLLOW_RCURLY_in_block897 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_var_decl_in_stat936 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
	public static final BitSet FOLLOW_SEMI_in_stat938 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_block_in_stat950 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ALAP_in_stat967 = new BitSet(new long[]{0x0442C9C004884050L,0x0000000001522080L});
	public static final BitSet FOLLOW_stat_in_stat970 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LANGLE_in_stat993 = new BitSet(new long[]{0x0442C9C004884050L,0x00000000015220A0L});
	public static final BitSet FOLLOW_stat_in_stat995 = new BitSet(new long[]{0x0442C9C004884050L,0x00000000015220A0L});
	public static final BitSet FOLLOW_RANGLE_in_stat1000 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_WHILE_in_stat1041 = new BitSet(new long[]{0x0002000000000000L});
	public static final BitSet FOLLOW_LPAR_in_stat1044 = new BitSet(new long[]{0x0400008000004040L,0x0000000000080000L});
	public static final BitSet FOLLOW_cond_in_stat1047 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
	public static final BitSet FOLLOW_RPAR_in_stat1049 = new BitSet(new long[]{0x0442C9C004884050L,0x0000000001522080L});
	public static final BitSet FOLLOW_stat_in_stat1052 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_UNTIL_in_stat1072 = new BitSet(new long[]{0x0002000000000000L});
	public static final BitSet FOLLOW_LPAR_in_stat1075 = new BitSet(new long[]{0x0400008000004040L,0x0000000000080000L});
	public static final BitSet FOLLOW_cond_in_stat1078 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
	public static final BitSet FOLLOW_RPAR_in_stat1080 = new BitSet(new long[]{0x0442C9C004884050L,0x0000000001522080L});
	public static final BitSet FOLLOW_stat_in_stat1083 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DO_in_stat1088 = new BitSet(new long[]{0x0442C9C004884050L,0x0000000001522080L});
	public static final BitSet FOLLOW_stat_in_stat1090 = new BitSet(new long[]{0x0000000000000000L,0x0000000001400000L});
	public static final BitSet FOLLOW_WHILE_in_stat1133 = new BitSet(new long[]{0x0002000000000000L});
	public static final BitSet FOLLOW_LPAR_in_stat1135 = new BitSet(new long[]{0x0400008000004040L,0x0000000000080000L});
	public static final BitSet FOLLOW_cond_in_stat1137 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
	public static final BitSet FOLLOW_RPAR_in_stat1139 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_UNTIL_in_stat1202 = new BitSet(new long[]{0x0002000000000000L});
	public static final BitSet FOLLOW_LPAR_in_stat1204 = new BitSet(new long[]{0x0400008000004040L,0x0000000000080000L});
	public static final BitSet FOLLOW_cond_in_stat1206 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
	public static final BitSet FOLLOW_RPAR_in_stat1208 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_IF_in_stat1255 = new BitSet(new long[]{0x0002000000000000L});
	public static final BitSet FOLLOW_LPAR_in_stat1258 = new BitSet(new long[]{0x0400008000004040L,0x0000000000080000L});
	public static final BitSet FOLLOW_cond_in_stat1261 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
	public static final BitSet FOLLOW_RPAR_in_stat1263 = new BitSet(new long[]{0x0442C9C004884050L,0x0000000001522080L});
	public static final BitSet FOLLOW_stat_in_stat1266 = new BitSet(new long[]{0x0000000080000002L});
	public static final BitSet FOLLOW_ELSE_in_stat1276 = new BitSet(new long[]{0x0442C9C004884050L,0x0000000001522080L});
	public static final BitSet FOLLOW_stat_in_stat1279 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TRY_in_stat1303 = new BitSet(new long[]{0x0442C9C004884050L,0x0000000001522080L});
	public static final BitSet FOLLOW_stat_in_stat1306 = new BitSet(new long[]{0x0000000080000002L});
	public static final BitSet FOLLOW_ELSE_in_stat1316 = new BitSet(new long[]{0x0442C9C004884050L,0x0000000001522080L});
	public static final BitSet FOLLOW_stat_in_stat1319 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_CHOICE_in_stat1338 = new BitSet(new long[]{0x0442C9C004884050L,0x0000000001522080L});
	public static final BitSet FOLLOW_stat_in_stat1341 = new BitSet(new long[]{0x0200000000000000L});
	public static final BitSet FOLLOW_OR_in_stat1351 = new BitSet(new long[]{0x0442C9C004884050L,0x0000000001522080L});
	public static final BitSet FOLLOW_stat_in_stat1354 = new BitSet(new long[]{0x0200000000000002L});
	public static final BitSet FOLLOW_expr_in_stat1369 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
	public static final BitSet FOLLOW_SEMI_in_stat1371 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_HALT_in_stat1389 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_var_decl_pure_in_var_decl1419 = new BitSet(new long[]{0x0000000000020002L});
	public static final BitSet FOLLOW_BECOMES_in_var_decl1437 = new BitSet(new long[]{0x0400008000004040L});
	public static final BitSet FOLLOW_call_in_var_decl1439 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_var_type_in_var_decl_pure1469 = new BitSet(new long[]{0x0000008000000000L});
	public static final BitSet FOLLOW_ID_in_var_decl_pure1471 = new BitSet(new long[]{0x0000000002000002L});
	public static final BitSet FOLLOW_COMMA_in_var_decl_pure1474 = new BitSet(new long[]{0x0000008000000000L});
	public static final BitSet FOLLOW_ID_in_var_decl_pure1476 = new BitSet(new long[]{0x0000000002000002L});
	public static final BitSet FOLLOW_cond_atom_in_cond1512 = new BitSet(new long[]{0x0000000000010002L});
	public static final BitSet FOLLOW_BAR_in_cond1521 = new BitSet(new long[]{0x0400008000004040L,0x0000000000080000L});
	public static final BitSet FOLLOW_cond_atom_in_cond1523 = new BitSet(new long[]{0x0000000000010002L});
	public static final BitSet FOLLOW_TRUE_in_cond_atom1569 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_call_in_cond_atom1590 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expr2_in_expr1620 = new BitSet(new long[]{0x0000000000010002L});
	public static final BitSet FOLLOW_BAR_in_expr1628 = new BitSet(new long[]{0x0402008000004040L,0x0000000000002000L});
	public static final BitSet FOLLOW_expr2_in_expr1630 = new BitSet(new long[]{0x0000000000010002L});
	public static final BitSet FOLLOW_expr_atom_in_expr21711 = new BitSet(new long[]{0x0000000000004002L,0x0000000000000001L});
	public static final BitSet FOLLOW_PLUS_in_expr21721 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ASTERISK_in_expr21748 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_SHARP_in_expr21803 = new BitSet(new long[]{0x0402008000004040L});
	public static final BitSet FOLLOW_expr_atom_in_expr21805 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LPAR_in_expr_atom1836 = new BitSet(new long[]{0x0402008000004040L,0x0000000000002000L});
	public static final BitSet FOLLOW_expr_in_expr_atom1838 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
	public static final BitSet FOLLOW_RPAR_in_expr_atom1842 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_assign_in_expr_atom1873 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_call_in_expr_atom1886 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_target_in_assign1936 = new BitSet(new long[]{0x0000000002020000L});
	public static final BitSet FOLLOW_COMMA_in_assign1939 = new BitSet(new long[]{0x0000008000000000L});
	public static final BitSet FOLLOW_target_in_assign1941 = new BitSet(new long[]{0x0000000002020000L});
	public static final BitSet FOLLOW_BECOMES_in_assign1945 = new BitSet(new long[]{0x0400008000004040L});
	public static final BitSet FOLLOW_call_in_assign1947 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_target1981 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_rule_name_in_call2019 = new BitSet(new long[]{0x0002000000000002L});
	public static final BitSet FOLLOW_arg_list_in_call2021 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_qual_name_in_rule_name2108 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LPAR_in_arg_list2145 = new BitSet(new long[]{0x088A108400000000L,0x00000000002C0900L});
	public static final BitSet FOLLOW_arg_in_arg_list2148 = new BitSet(new long[]{0x0000000002000000L,0x0000000000000800L});
	public static final BitSet FOLLOW_COMMA_in_arg_list2152 = new BitSet(new long[]{0x088A108400000000L,0x00000000002C0100L});
	public static final BitSet FOLLOW_arg_in_arg_list2154 = new BitSet(new long[]{0x0000000002000000L,0x0000000000000800L});
	public static final BitSet FOLLOW_RPAR_in_arg_list2163 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_OUT_in_arg2213 = new BitSet(new long[]{0x0000008000000000L});
	public static final BitSet FOLLOW_ID_in_arg2215 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_UNDER_in_arg2246 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_in_arg_in_arg2261 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_op1_in_in_arg2301 = new BitSet(new long[]{0x008A108400000000L,0x00000000000C0100L});
	public static final BitSet FOLLOW_in_arg_in_in_arg2303 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_in_atom_in_in_arg2349 = new BitSet(new long[]{0x80A9402100014022L,0x0000000000004021L});
	public static final BitSet FOLLOW_op2_in_in_arg2353 = new BitSet(new long[]{0x008A108400000000L,0x00000000000C0100L});
	public static final BitSet FOLLOW_in_arg_in_in_arg2355 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_in_atom2457 = new BitSet(new long[]{0x0002000000000002L});
	public static final BitSet FOLLOW_arg_list_in_in_atom2461 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_literal_in_in_atom2504 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LPAR_in_in_atom2528 = new BitSet(new long[]{0x008A188400000000L,0x00000000000E0180L});
	public static final BitSet FOLLOW_in_arg_in_in_atom2544 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
	public static final BitSet FOLLOW_RPAR_in_in_atom2546 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_REAL_in_in_atom2576 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
	public static final BitSet FOLLOW_INT_in_in_atom2580 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
	public static final BitSet FOLLOW_STRING_in_in_atom2584 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
	public static final BitSet FOLLOW_RPAR_in_in_atom2587 = new BitSet(new long[]{0x008A108400000000L,0x00000000000C0100L});
	public static final BitSet FOLLOW_in_arg_in_in_atom2589 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ELSE_in_synpred1_Ctrl1271 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ELSE_in_synpred2_Ctrl1311 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_OR_in_synpred3_Ctrl1346 = new BitSet(new long[]{0x0000000000000002L});
}
