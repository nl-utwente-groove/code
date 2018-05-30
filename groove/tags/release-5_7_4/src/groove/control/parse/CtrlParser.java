// $ANTLR null E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g 2017-04-10 19:15:58

package groove.control.parse;
import groove.control.*;
import groove.util.antlr.*;
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
		"<invalid>", "<EOR>", "<DOWN>", "<UP>", "ALAP", "AMP", "ANY", "ARG", "ARGS", 
		"ASTERISK", "ATOM", "BAR", "BECOMES", "BLOCK", "BOOL", "BQUOTE", "BSLASH", 
		"CALL", "CHOICE", "COMMA", "DO", "DONT_CARE", "DOT", "DO_UNTIL", "DO_WHILE", 
		"ELSE", "EscapeSequence", "FALSE", "FUNCTION", "FUNCTIONS", "ID", "IF", 
		"IMPORT", "IMPORTS", "INT", "INT_LIT", "IntegerNumber", "LANGLE", "LCURLY", 
		"LPAR", "MINUS", "ML_COMMENT", "NODE", "NOT", "NonIntegerNumber", "OR", 
		"OTHER", "OUT", "PACKAGE", "PAR", "PARS", "PLUS", "PRIORITY", "PROGRAM", 
		"QUOTE", "RANGLE", "RCURLY", "REAL", "REAL_LIT", "RECIPE", "RECIPES", 
		"RPAR", "SEMI", "SHARP", "SL_COMMENT", "STAR", "STRING", "STRING_LIT", 
		"TRUE", "TRY", "UNTIL", "VAR", "WHILE", "WS"
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
	@Override public String getGrammarFileName() { return "E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g"; }


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
	// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:74:1: program : package_decl ( import_decl )* ( function | recipe | stat )* EOF -> ^( PROGRAM package_decl ^( IMPORTS ( import_decl )* ) ^( FUNCTIONS ( function )* ) ^( RECIPES ( recipe )* ) ^( BLOCK ( stat )* ) ) ;
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
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:77:3: ( package_decl ( import_decl )* ( function | recipe | stat )* EOF -> ^( PROGRAM package_decl ^( IMPORTS ( import_decl )* ) ^( FUNCTIONS ( function )* ) ^( RECIPES ( recipe )* ) ^( BLOCK ( stat )* ) ) )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:81:5: package_decl ( import_decl )* ( function | recipe | stat )* EOF
			{
			pushFollow(FOLLOW_package_decl_in_program166);
			package_decl1=package_decl();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_package_decl.add(package_decl1.getTree());
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:82:5: ( import_decl )*
			loop1:
			while (true) {
				int alt1=2;
				int LA1_0 = input.LA(1);
				if ( (LA1_0==IMPORT) ) {
					alt1=1;
				}

				switch (alt1) {
				case 1 :
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:82:5: import_decl
					{
					pushFollow(FOLLOW_import_decl_in_program172);
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

			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:83:5: ( function | recipe | stat )*
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
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:83:6: function
					{
					pushFollow(FOLLOW_function_in_program180);
					function3=function();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_function.add(function3.getTree());
					}
					break;
				case 2 :
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:83:15: recipe
					{
					pushFollow(FOLLOW_recipe_in_program182);
					recipe4=recipe();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_recipe.add(recipe4.getTree());
					}
					break;
				case 3 :
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:83:22: stat
					{
					pushFollow(FOLLOW_stat_in_program184);
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

			EOF6=(Token)match(input,EOF,FOLLOW_EOF_in_program188); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_EOF.add(EOF6);

			if ( state.backtracking==0 ) { helper.checkEOF(EOF6_tree); }
			// AST REWRITE
			// elements: package_decl, function, stat, recipe, import_decl
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CtrlTree)adaptor.nil();
			// 85:5: -> ^( PROGRAM package_decl ^( IMPORTS ( import_decl )* ) ^( FUNCTIONS ( function )* ) ^( RECIPES ( recipe )* ) ^( BLOCK ( stat )* ) )
			{
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:85:8: ^( PROGRAM package_decl ^( IMPORTS ( import_decl )* ) ^( FUNCTIONS ( function )* ) ^( RECIPES ( recipe )* ) ^( BLOCK ( stat )* ) )
				{
				CtrlTree root_1 = (CtrlTree)adaptor.nil();
				root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(PROGRAM, "PROGRAM"), root_1);
				adaptor.addChild(root_1, stream_package_decl.nextTree());
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:87:11: ^( IMPORTS ( import_decl )* )
				{
				CtrlTree root_2 = (CtrlTree)adaptor.nil();
				root_2 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(IMPORTS, "IMPORTS"), root_2);
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:87:21: ( import_decl )*
				while ( stream_import_decl.hasNext() ) {
					adaptor.addChild(root_2, stream_import_decl.nextTree());
				}
				stream_import_decl.reset();

				adaptor.addChild(root_1, root_2);
				}

				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:88:11: ^( FUNCTIONS ( function )* )
				{
				CtrlTree root_2 = (CtrlTree)adaptor.nil();
				root_2 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(FUNCTIONS, "FUNCTIONS"), root_2);
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:88:23: ( function )*
				while ( stream_function.hasNext() ) {
					adaptor.addChild(root_2, stream_function.nextTree());
				}
				stream_function.reset();

				adaptor.addChild(root_1, root_2);
				}

				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:89:11: ^( RECIPES ( recipe )* )
				{
				CtrlTree root_2 = (CtrlTree)adaptor.nil();
				root_2 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(RECIPES, "RECIPES"), root_2);
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:89:21: ( recipe )*
				while ( stream_recipe.hasNext() ) {
					adaptor.addChild(root_2, stream_recipe.nextTree());
				}
				stream_recipe.reset();

				adaptor.addChild(root_1, root_2);
				}

				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:90:11: ^( BLOCK ( stat )* )
				{
				CtrlTree root_2 = (CtrlTree)adaptor.nil();
				root_2 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(BLOCK, "BLOCK"), root_2);
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:90:19: ( stat )*
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
	// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:95:1: package_decl : (key= PACKAGE qual_name[false] close= SEMI -> ^( PACKAGE[$key] qual_name SEMI[$close] ) | ->) ;
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
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:96:3: ( (key= PACKAGE qual_name[false] close= SEMI -> ^( PACKAGE[$key] qual_name SEMI[$close] ) | ->) )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:98:5: (key= PACKAGE qual_name[false] close= SEMI -> ^( PACKAGE[$key] qual_name SEMI[$close] ) | ->)
			{
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:98:5: (key= PACKAGE qual_name[false] close= SEMI -> ^( PACKAGE[$key] qual_name SEMI[$close] ) | ->)
			int alt3=2;
			int LA3_0 = input.LA(1);
			if ( (LA3_0==PACKAGE) ) {
				alt3=1;
			}
			else if ( (LA3_0==EOF||LA3_0==ALAP||LA3_0==ANY||LA3_0==ASTERISK||LA3_0==BOOL||LA3_0==CHOICE||LA3_0==DO||LA3_0==FUNCTION||(LA3_0 >= ID && LA3_0 <= IMPORT)||LA3_0==INT||(LA3_0 >= LANGLE && LA3_0 <= LPAR)||LA3_0==NODE||LA3_0==OTHER||LA3_0==REAL||LA3_0==RECIPE||LA3_0==SHARP||LA3_0==STRING||(LA3_0 >= TRY && LA3_0 <= UNTIL)||LA3_0==WHILE) ) {
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
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:98:7: key= PACKAGE qual_name[false] close= SEMI
					{
					key=(Token)match(input,PACKAGE,FOLLOW_PACKAGE_in_package_decl325); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_PACKAGE.add(key);

					pushFollow(FOLLOW_qual_name_in_package_decl327);
					qual_name7=qual_name(false);
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_qual_name.add(qual_name7.getTree());
					close=(Token)match(input,SEMI,FOLLOW_SEMI_in_package_decl332); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_SEMI.add(close);

					if ( state.backtracking==0 ) { helper.setPackage((qual_name7!=null?((CtrlTree)qual_name7.getTree()):null)); }
					// AST REWRITE
					// elements: SEMI, PACKAGE, qual_name
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CtrlTree)adaptor.nil();
					// 100:7: -> ^( PACKAGE[$key] qual_name SEMI[$close] )
					{
						// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:100:10: ^( PACKAGE[$key] qual_name SEMI[$close] )
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
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:101:7: 
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
					// 101:7: ->
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
	// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:106:1: import_decl : IMPORT ^ qual_name[false] SEMI ;
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
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:107:3: ( IMPORT ^ qual_name[false] SEMI )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:109:5: IMPORT ^ qual_name[false] SEMI
			{
			root_0 = (CtrlTree)adaptor.nil();


			IMPORT8=(Token)match(input,IMPORT,FOLLOW_IMPORT_in_import_decl399); if (state.failed) return retval;
			if ( state.backtracking==0 ) {
			IMPORT8_tree = (CtrlTree)adaptor.create(IMPORT8);
			root_0 = (CtrlTree)adaptor.becomeRoot(IMPORT8_tree, root_0);
			}

			pushFollow(FOLLOW_qual_name_in_import_decl402);
			qual_name9=qual_name(false);
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) adaptor.addChild(root_0, qual_name9.getTree());

			SEMI10=(Token)match(input,SEMI,FOLLOW_SEMI_in_import_decl405); if (state.failed) return retval;
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
	// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:115:1: qual_name[boolean any] : ( ID ( DOT rest= qual_name[any] )? ->|{...}? ( ASTERISK DOT )? ( ANY ->| OTHER ->) );
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
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:116:3: ( ID ( DOT rest= qual_name[any] )? ->|{...}? ( ASTERISK DOT )? ( ANY ->| OTHER ->) )
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
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:120:5: ID ( DOT rest= qual_name[any] )?
					{
					ID11=(Token)match(input,ID,FOLLOW_ID_in_qual_name447); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_ID.add(ID11);

					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:120:8: ( DOT rest= qual_name[any] )?
					int alt4=2;
					int LA4_0 = input.LA(1);
					if ( (LA4_0==DOT) ) {
						alt4=1;
					}
					switch (alt4) {
						case 1 :
							// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:120:10: DOT rest= qual_name[any]
							{
							DOT12=(Token)match(input,DOT,FOLLOW_DOT_in_qual_name451); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_DOT.add(DOT12);

							pushFollow(FOLLOW_qual_name_in_qual_name455);
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
					// 121:22: ->
					{
						adaptor.addChild(root_0,  helper.toQualName(ID11, (rest!=null?((CtrlTree)rest.getTree()):null)) );
					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:122:5: {...}? ( ASTERISK DOT )? ( ANY ->| OTHER ->)
					{
					if ( !(( any )) ) {
						if (state.backtracking>0) {state.failed=true; return retval;}
						throw new FailedPredicateException(input, "qual_name", " any ");
					}
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:122:14: ( ASTERISK DOT )?
					int alt5=2;
					int LA5_0 = input.LA(1);
					if ( (LA5_0==ASTERISK) ) {
						alt5=1;
					}
					switch (alt5) {
						case 1 :
							// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:122:16: ASTERISK DOT
							{
							ASTERISK13=(Token)match(input,ASTERISK,FOLLOW_ASTERISK_in_qual_name494); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_ASTERISK.add(ASTERISK13);

							DOT14=(Token)match(input,DOT,FOLLOW_DOT_in_qual_name496); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_DOT.add(DOT14);

							}
							break;

					}

					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:123:14: ( ANY ->| OTHER ->)
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
							// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:123:16: ANY
							{
							ANY15=(Token)match(input,ANY,FOLLOW_ANY_in_qual_name516); if (state.failed) return retval; 
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
							// 123:22: ->
							{
								adaptor.addChild(root_0,  helper.toQualName(ASTERISK13, ANY15) );
							}


							retval.tree = root_0;
							}

							}
							break;
						case 2 :
							// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:124:16: OTHER
							{
							OTHER16=(Token)match(input,OTHER,FOLLOW_OTHER_in_qual_name539); if (state.failed) return retval; 
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
							// 124:22: ->
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
	// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:131:1: recipe : RECIPE ^ ID par_list ( PRIORITY ! INT_LIT )? block ;
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
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:132:3: ( RECIPE ^ ID par_list ( PRIORITY ! INT_LIT )? block )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:139:5: RECIPE ^ ID par_list ( PRIORITY ! INT_LIT )? block
			{
			root_0 = (CtrlTree)adaptor.nil();


			RECIPE17=(Token)match(input,RECIPE,FOLLOW_RECIPE_in_recipe608); if (state.failed) return retval;
			if ( state.backtracking==0 ) {
			RECIPE17_tree = (CtrlTree)adaptor.create(RECIPE17);
			root_0 = (CtrlTree)adaptor.becomeRoot(RECIPE17_tree, root_0);
			}

			ID18=(Token)match(input,ID,FOLLOW_ID_in_recipe611); if (state.failed) return retval;
			if ( state.backtracking==0 ) {
			ID18_tree = (CtrlTree)adaptor.create(ID18);
			adaptor.addChild(root_0, ID18_tree);
			}

			pushFollow(FOLLOW_par_list_in_recipe613);
			par_list19=par_list();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) adaptor.addChild(root_0, par_list19.getTree());

			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:139:25: ( PRIORITY ! INT_LIT )?
			int alt8=2;
			int LA8_0 = input.LA(1);
			if ( (LA8_0==PRIORITY) ) {
				alt8=1;
			}
			switch (alt8) {
				case 1 :
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:139:26: PRIORITY ! INT_LIT
					{
					PRIORITY20=(Token)match(input,PRIORITY,FOLLOW_PRIORITY_in_recipe616); if (state.failed) return retval;
					INT_LIT21=(Token)match(input,INT_LIT,FOLLOW_INT_LIT_in_recipe619); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					INT_LIT21_tree = (CtrlTree)adaptor.create(INT_LIT21);
					adaptor.addChild(root_0, INT_LIT21_tree);
					}

					}
					break;

			}

			if ( state.backtracking==0 ) { helper.setContext(RECIPE17_tree); }
			pushFollow(FOLLOW_block_in_recipe633);
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
	// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:150:1: function : FUNCTION ^ ID par_list block ;
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
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:151:3: ( FUNCTION ^ ID par_list block )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:156:5: FUNCTION ^ ID par_list block
			{
			root_0 = (CtrlTree)adaptor.nil();


			FUNCTION23=(Token)match(input,FUNCTION,FOLLOW_FUNCTION_in_function679); if (state.failed) return retval;
			if ( state.backtracking==0 ) {
			FUNCTION23_tree = (CtrlTree)adaptor.create(FUNCTION23);
			root_0 = (CtrlTree)adaptor.becomeRoot(FUNCTION23_tree, root_0);
			}

			ID24=(Token)match(input,ID,FOLLOW_ID_in_function682); if (state.failed) return retval;
			if ( state.backtracking==0 ) {
			ID24_tree = (CtrlTree)adaptor.create(ID24);
			adaptor.addChild(root_0, ID24_tree);
			}

			pushFollow(FOLLOW_par_list_in_function684);
			par_list25=par_list();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) adaptor.addChild(root_0, par_list25.getTree());

			if ( state.backtracking==0 ) { helper.setContext(FUNCTION23_tree); }
			pushFollow(FOLLOW_block_in_function697);
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
	// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:167:1: par_list : LPAR ( par ( COMMA par )* )? RPAR -> ^( PARS ( par )* ) ;
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
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:168:3: ( LPAR ( par ( COMMA par )* )? RPAR -> ^( PARS ( par )* ) )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:170:5: LPAR ( par ( COMMA par )* )? RPAR
			{
			LPAR27=(Token)match(input,LPAR,FOLLOW_LPAR_in_par_list728); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_LPAR.add(LPAR27);

			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:170:10: ( par ( COMMA par )* )?
			int alt10=2;
			int LA10_0 = input.LA(1);
			if ( (LA10_0==BOOL||LA10_0==INT||LA10_0==NODE||LA10_0==OUT||LA10_0==REAL||LA10_0==STRING) ) {
				alt10=1;
			}
			switch (alt10) {
				case 1 :
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:170:11: par ( COMMA par )*
					{
					pushFollow(FOLLOW_par_in_par_list731);
					par28=par();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_par.add(par28.getTree());
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:170:15: ( COMMA par )*
					loop9:
					while (true) {
						int alt9=2;
						int LA9_0 = input.LA(1);
						if ( (LA9_0==COMMA) ) {
							alt9=1;
						}

						switch (alt9) {
						case 1 :
							// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:170:16: COMMA par
							{
							COMMA29=(Token)match(input,COMMA,FOLLOW_COMMA_in_par_list734); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_COMMA.add(COMMA29);

							pushFollow(FOLLOW_par_in_par_list736);
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

			RPAR31=(Token)match(input,RPAR,FOLLOW_RPAR_in_par_list742); if (state.failed) return retval; 
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
			// 171:5: -> ^( PARS ( par )* )
			{
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:171:8: ^( PARS ( par )* )
				{
				CtrlTree root_1 = (CtrlTree)adaptor.nil();
				root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(PARS, "PARS"), root_1);
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:171:15: ( par )*
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
	// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:177:1: par : ( OUT var_type ID -> ^( PAR OUT var_type ID ) | var_type ID -> ^( PAR var_type ID ) );
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
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:178:3: ( OUT var_type ID -> ^( PAR OUT var_type ID ) | var_type ID -> ^( PAR var_type ID ) )
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
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:181:5: OUT var_type ID
					{
					OUT32=(Token)match(input,OUT,FOLLOW_OUT_in_par787); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_OUT.add(OUT32);

					pushFollow(FOLLOW_var_type_in_par789);
					var_type33=var_type();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_var_type.add(var_type33.getTree());
					ID34=(Token)match(input,ID,FOLLOW_ID_in_par791); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_ID.add(ID34);

					// AST REWRITE
					// elements: OUT, var_type, ID
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CtrlTree)adaptor.nil();
					// 181:21: -> ^( PAR OUT var_type ID )
					{
						// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:181:24: ^( PAR OUT var_type ID )
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
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:185:5: var_type ID
					{
					pushFollow(FOLLOW_var_type_in_par824);
					var_type35=var_type();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_var_type.add(var_type35.getTree());
					ID36=(Token)match(input,ID,FOLLOW_ID_in_par826); if (state.failed) return retval; 
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
					// 185:17: -> ^( PAR var_type ID )
					{
						// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:185:20: ^( PAR var_type ID )
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
	// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:189:1: block : open= LCURLY ( stat )* close= RCURLY -> ^( BLOCK[$open] ( stat )* TRUE[$close] ) ;
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
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:190:3: (open= LCURLY ( stat )* close= RCURLY -> ^( BLOCK[$open] ( stat )* TRUE[$close] ) )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:192:5: open= LCURLY ( stat )* close= RCURLY
			{
			open=(Token)match(input,LCURLY,FOLLOW_LCURLY_in_block865); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_LCURLY.add(open);

			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:192:17: ( stat )*
			loop12:
			while (true) {
				int alt12=2;
				int LA12_0 = input.LA(1);
				if ( (LA12_0==ALAP||LA12_0==ANY||LA12_0==ASTERISK||LA12_0==BOOL||LA12_0==CHOICE||LA12_0==DO||(LA12_0 >= ID && LA12_0 <= IF)||LA12_0==INT||(LA12_0 >= LANGLE && LA12_0 <= LPAR)||LA12_0==NODE||LA12_0==OTHER||LA12_0==REAL||LA12_0==SHARP||LA12_0==STRING||(LA12_0 >= TRY && LA12_0 <= UNTIL)||LA12_0==WHILE) ) {
					alt12=1;
				}

				switch (alt12) {
				case 1 :
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:192:17: stat
					{
					pushFollow(FOLLOW_stat_in_block867);
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

			close=(Token)match(input,RCURLY,FOLLOW_RCURLY_in_block872); if (state.failed) return retval; 
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
			// 193:5: -> ^( BLOCK[$open] ( stat )* TRUE[$close] )
			{
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:193:8: ^( BLOCK[$open] ( stat )* TRUE[$close] )
				{
				CtrlTree root_1 = (CtrlTree)adaptor.nil();
				root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(BLOCK, open), root_1);
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:193:23: ( stat )*
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
	// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:196:1: stat : ( var_decl SEMI ^| block | ALAP ^ stat |open= LANGLE ( stat )* close= RANGLE -> ^( ATOM[$open] ^( BLOCK ( stat )* TRUE[$close] ) ) | WHILE ^ LPAR ! cond RPAR ! stat | UNTIL ^ LPAR ! cond RPAR ! stat | DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) ) | IF ^ LPAR ! cond RPAR ! stat ( ( ELSE )=> ELSE ! stat )? | TRY ^ stat ( ( ELSE )=> ELSE ! stat )? | CHOICE ^ stat ( ( OR )=> OR ! stat )+ | expr SEMI ^);
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
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:197:3: ( var_decl SEMI ^| block | ALAP ^ stat |open= LANGLE ( stat )* close= RANGLE -> ^( ATOM[$open] ^( BLOCK ( stat )* TRUE[$close] ) ) | WHILE ^ LPAR ! cond RPAR ! stat | UNTIL ^ LPAR ! cond RPAR ! stat | DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) ) | IF ^ LPAR ! cond RPAR ! stat ( ( ELSE )=> ELSE ! stat )? | TRY ^ stat ( ( ELSE )=> ELSE ! stat )? | CHOICE ^ stat ( ( OR )=> OR ! stat )+ | expr SEMI ^)
			int alt18=11;
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
			default:
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 18, 0, input);
				throw nvae;
			}
			switch (alt18) {
				case 1 :
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:199:5: var_decl SEMI ^
					{
					root_0 = (CtrlTree)adaptor.nil();


					pushFollow(FOLLOW_var_decl_in_stat911);
					var_decl38=var_decl();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, var_decl38.getTree());

					SEMI39=(Token)match(input,SEMI,FOLLOW_SEMI_in_stat913); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					SEMI39_tree = (CtrlTree)adaptor.create(SEMI39);
					root_0 = (CtrlTree)adaptor.becomeRoot(SEMI39_tree, root_0);
					}

					}
					break;
				case 2 :
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:201:4: block
					{
					root_0 = (CtrlTree)adaptor.nil();


					pushFollow(FOLLOW_block_in_stat925);
					block40=block();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, block40.getTree());

					}
					break;
				case 3 :
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:205:4: ALAP ^ stat
					{
					root_0 = (CtrlTree)adaptor.nil();


					ALAP41=(Token)match(input,ALAP,FOLLOW_ALAP_in_stat942); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					ALAP41_tree = (CtrlTree)adaptor.create(ALAP41);
					root_0 = (CtrlTree)adaptor.becomeRoot(ALAP41_tree, root_0);
					}

					pushFollow(FOLLOW_stat_in_stat945);
					stat42=stat();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, stat42.getTree());

					}
					break;
				case 4 :
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:210:4: open= LANGLE ( stat )* close= RANGLE
					{
					open=(Token)match(input,LANGLE,FOLLOW_LANGLE_in_stat968); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_LANGLE.add(open);

					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:210:16: ( stat )*
					loop13:
					while (true) {
						int alt13=2;
						int LA13_0 = input.LA(1);
						if ( (LA13_0==ALAP||LA13_0==ANY||LA13_0==ASTERISK||LA13_0==BOOL||LA13_0==CHOICE||LA13_0==DO||(LA13_0 >= ID && LA13_0 <= IF)||LA13_0==INT||(LA13_0 >= LANGLE && LA13_0 <= LPAR)||LA13_0==NODE||LA13_0==OTHER||LA13_0==REAL||LA13_0==SHARP||LA13_0==STRING||(LA13_0 >= TRY && LA13_0 <= UNTIL)||LA13_0==WHILE) ) {
							alt13=1;
						}

						switch (alt13) {
						case 1 :
							// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:210:16: stat
							{
							pushFollow(FOLLOW_stat_in_stat970);
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

					close=(Token)match(input,RANGLE,FOLLOW_RANGLE_in_stat975); if (state.failed) return retval; 
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
					// 211:4: -> ^( ATOM[$open] ^( BLOCK ( stat )* TRUE[$close] ) )
					{
						// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:211:7: ^( ATOM[$open] ^( BLOCK ( stat )* TRUE[$close] ) )
						{
						CtrlTree root_1 = (CtrlTree)adaptor.nil();
						root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(ATOM, open), root_1);
						// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:211:21: ^( BLOCK ( stat )* TRUE[$close] )
						{
						CtrlTree root_2 = (CtrlTree)adaptor.nil();
						root_2 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(BLOCK, "BLOCK"), root_2);
						// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:211:29: ( stat )*
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
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:216:4: WHILE ^ LPAR ! cond RPAR ! stat
					{
					root_0 = (CtrlTree)adaptor.nil();


					WHILE44=(Token)match(input,WHILE,FOLLOW_WHILE_in_stat1016); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					WHILE44_tree = (CtrlTree)adaptor.create(WHILE44);
					root_0 = (CtrlTree)adaptor.becomeRoot(WHILE44_tree, root_0);
					}

					LPAR45=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat1019); if (state.failed) return retval;
					pushFollow(FOLLOW_cond_in_stat1022);
					cond46=cond();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, cond46.getTree());

					RPAR47=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat1024); if (state.failed) return retval;
					pushFollow(FOLLOW_stat_in_stat1027);
					stat48=stat();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, stat48.getTree());

					}
					break;
				case 6 :
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:220:5: UNTIL ^ LPAR ! cond RPAR ! stat
					{
					root_0 = (CtrlTree)adaptor.nil();


					UNTIL49=(Token)match(input,UNTIL,FOLLOW_UNTIL_in_stat1047); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					UNTIL49_tree = (CtrlTree)adaptor.create(UNTIL49);
					root_0 = (CtrlTree)adaptor.becomeRoot(UNTIL49_tree, root_0);
					}

					LPAR50=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat1050); if (state.failed) return retval;
					pushFollow(FOLLOW_cond_in_stat1053);
					cond51=cond();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, cond51.getTree());

					RPAR52=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat1055); if (state.failed) return retval;
					pushFollow(FOLLOW_stat_in_stat1058);
					stat53=stat();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, stat53.getTree());

					}
					break;
				case 7 :
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:221:4: DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) )
					{
					DO54=(Token)match(input,DO,FOLLOW_DO_in_stat1063); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_DO.add(DO54);

					pushFollow(FOLLOW_stat_in_stat1065);
					stat55=stat();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_stat.add(stat55.getTree());
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:222:4: ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) )
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
							// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:227:7: WHILE LPAR cond RPAR
							{
							WHILE56=(Token)match(input,WHILE,FOLLOW_WHILE_in_stat1108); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_WHILE.add(WHILE56);

							LPAR57=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat1110); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_LPAR.add(LPAR57);

							pushFollow(FOLLOW_cond_in_stat1112);
							cond58=cond();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_cond.add(cond58.getTree());
							RPAR59=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat1114); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_RPAR.add(RPAR59);

							// AST REWRITE
							// elements: WHILE, stat, stat, cond
							// token labels: 
							// rule labels: retval
							// token list labels: 
							// rule list labels: 
							// wildcard labels: 
							if ( state.backtracking==0 ) {
							retval.tree = root_0;
							RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

							root_0 = (CtrlTree)adaptor.nil();
							// 227:28: -> ^( BLOCK stat ^( WHILE cond stat ) )
							{
								// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:227:31: ^( BLOCK stat ^( WHILE cond stat ) )
								{
								CtrlTree root_1 = (CtrlTree)adaptor.nil();
								root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(BLOCK, "BLOCK"), root_1);
								adaptor.addChild(root_1, stream_stat.nextTree());
								// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:227:44: ^( WHILE cond stat )
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
							// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:234:5: UNTIL LPAR cond RPAR
							{
							UNTIL60=(Token)match(input,UNTIL,FOLLOW_UNTIL_in_stat1177); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_UNTIL.add(UNTIL60);

							LPAR61=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat1179); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_LPAR.add(LPAR61);

							pushFollow(FOLLOW_cond_in_stat1181);
							cond62=cond();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_cond.add(cond62.getTree());
							RPAR63=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat1183); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_RPAR.add(RPAR63);

							// AST REWRITE
							// elements: cond, stat, UNTIL, stat
							// token labels: 
							// rule labels: retval
							// token list labels: 
							// rule list labels: 
							// wildcard labels: 
							if ( state.backtracking==0 ) {
							retval.tree = root_0;
							RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

							root_0 = (CtrlTree)adaptor.nil();
							// 234:26: -> ^( BLOCK stat ^( UNTIL cond stat ) )
							{
								// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:234:29: ^( BLOCK stat ^( UNTIL cond stat ) )
								{
								CtrlTree root_1 = (CtrlTree)adaptor.nil();
								root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(BLOCK, "BLOCK"), root_1);
								adaptor.addChild(root_1, stream_stat.nextTree());
								// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:234:42: ^( UNTIL cond stat )
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
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:240:5: IF ^ LPAR ! cond RPAR ! stat ( ( ELSE )=> ELSE ! stat )?
					{
					root_0 = (CtrlTree)adaptor.nil();


					IF64=(Token)match(input,IF,FOLLOW_IF_in_stat1230); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					IF64_tree = (CtrlTree)adaptor.create(IF64);
					root_0 = (CtrlTree)adaptor.becomeRoot(IF64_tree, root_0);
					}

					LPAR65=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat1233); if (state.failed) return retval;
					pushFollow(FOLLOW_cond_in_stat1236);
					cond66=cond();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, cond66.getTree());

					RPAR67=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat1238); if (state.failed) return retval;
					pushFollow(FOLLOW_stat_in_stat1241);
					stat68=stat();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, stat68.getTree());

					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:240:31: ( ( ELSE )=> ELSE ! stat )?
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
							// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:240:33: ( ELSE )=> ELSE ! stat
							{
							ELSE69=(Token)match(input,ELSE,FOLLOW_ELSE_in_stat1251); if (state.failed) return retval;
							pushFollow(FOLLOW_stat_in_stat1254);
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
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:244:5: TRY ^ stat ( ( ELSE )=> ELSE ! stat )?
					{
					root_0 = (CtrlTree)adaptor.nil();


					TRY71=(Token)match(input,TRY,FOLLOW_TRY_in_stat1278); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					TRY71_tree = (CtrlTree)adaptor.create(TRY71);
					root_0 = (CtrlTree)adaptor.becomeRoot(TRY71_tree, root_0);
					}

					pushFollow(FOLLOW_stat_in_stat1281);
					stat72=stat();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, stat72.getTree());

					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:244:15: ( ( ELSE )=> ELSE ! stat )?
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
							// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:244:17: ( ELSE )=> ELSE ! stat
							{
							ELSE73=(Token)match(input,ELSE,FOLLOW_ELSE_in_stat1291); if (state.failed) return retval;
							pushFollow(FOLLOW_stat_in_stat1294);
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
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:247:5: CHOICE ^ stat ( ( OR )=> OR ! stat )+
					{
					root_0 = (CtrlTree)adaptor.nil();


					CHOICE75=(Token)match(input,CHOICE,FOLLOW_CHOICE_in_stat1313); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					CHOICE75_tree = (CtrlTree)adaptor.create(CHOICE75);
					root_0 = (CtrlTree)adaptor.becomeRoot(CHOICE75_tree, root_0);
					}

					pushFollow(FOLLOW_stat_in_stat1316);
					stat76=stat();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, stat76.getTree());

					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:247:18: ( ( OR )=> OR ! stat )+
					int cnt17=0;
					loop17:
					while (true) {
						int alt17=2;
						int LA17_0 = input.LA(1);
						if ( (LA17_0==OR) ) {
							int LA17_23 = input.LA(2);
							if ( (synpred3_Ctrl()) ) {
								alt17=1;
							}

						}

						switch (alt17) {
						case 1 :
							// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:247:20: ( OR )=> OR ! stat
							{
							OR77=(Token)match(input,OR,FOLLOW_OR_in_stat1326); if (state.failed) return retval;
							pushFollow(FOLLOW_stat_in_stat1329);
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
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:250:4: expr SEMI ^
					{
					root_0 = (CtrlTree)adaptor.nil();


					pushFollow(FOLLOW_expr_in_stat1344);
					expr79=expr();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, expr79.getTree());

					SEMI80=(Token)match(input,SEMI,FOLLOW_SEMI_in_stat1346); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					SEMI80_tree = (CtrlTree)adaptor.create(SEMI80);
					root_0 = (CtrlTree)adaptor.becomeRoot(SEMI80_tree, root_0);
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
	// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:254:1: var_decl : var_decl_pure ( -> var_decl_pure | BECOMES call -> ^( BECOMES var_decl_pure call ) ) ;
	public final CtrlParser.var_decl_return var_decl() throws RecognitionException {
		CtrlParser.var_decl_return retval = new CtrlParser.var_decl_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		Token BECOMES82=null;
		ParserRuleReturnScope var_decl_pure81 =null;
		ParserRuleReturnScope call83 =null;

		CtrlTree BECOMES82_tree=null;
		RewriteRuleTokenStream stream_BECOMES=new RewriteRuleTokenStream(adaptor,"token BECOMES");
		RewriteRuleSubtreeStream stream_call=new RewriteRuleSubtreeStream(adaptor,"rule call");
		RewriteRuleSubtreeStream stream_var_decl_pure=new RewriteRuleSubtreeStream(adaptor,"rule var_decl_pure");

		try {
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:255:3: ( var_decl_pure ( -> var_decl_pure | BECOMES call -> ^( BECOMES var_decl_pure call ) ) )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:258:5: var_decl_pure ( -> var_decl_pure | BECOMES call -> ^( BECOMES var_decl_pure call ) )
			{
			pushFollow(FOLLOW_var_decl_pure_in_var_decl1378);
			var_decl_pure81=var_decl_pure();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_var_decl_pure.add(var_decl_pure81.getTree());
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:259:5: ( -> var_decl_pure | BECOMES call -> ^( BECOMES var_decl_pure call ) )
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
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:259:7: 
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
					// 259:7: -> var_decl_pure
					{
						adaptor.addChild(root_0, stream_var_decl_pure.nextTree());
					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:260:7: BECOMES call
					{
					BECOMES82=(Token)match(input,BECOMES,FOLLOW_BECOMES_in_var_decl1396); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_BECOMES.add(BECOMES82);

					pushFollow(FOLLOW_call_in_var_decl1398);
					call83=call();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_call.add(call83.getTree());
					// AST REWRITE
					// elements: var_decl_pure, BECOMES, call
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CtrlTree)adaptor.nil();
					// 260:20: -> ^( BECOMES var_decl_pure call )
					{
						// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:260:23: ^( BECOMES var_decl_pure call )
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
	// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:264:1: var_decl_pure : var_type ID ( COMMA ID )* -> ^( VAR var_type ( ID )+ ) ;
	public final CtrlParser.var_decl_pure_return var_decl_pure() throws RecognitionException {
		CtrlParser.var_decl_pure_return retval = new CtrlParser.var_decl_pure_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		Token ID85=null;
		Token COMMA86=null;
		Token ID87=null;
		ParserRuleReturnScope var_type84 =null;

		CtrlTree ID85_tree=null;
		CtrlTree COMMA86_tree=null;
		CtrlTree ID87_tree=null;
		RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
		RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
		RewriteRuleSubtreeStream stream_var_type=new RewriteRuleSubtreeStream(adaptor,"rule var_type");

		try {
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:265:3: ( var_type ID ( COMMA ID )* -> ^( VAR var_type ( ID )+ ) )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:265:5: var_type ID ( COMMA ID )*
			{
			pushFollow(FOLLOW_var_type_in_var_decl_pure1428);
			var_type84=var_type();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_var_type.add(var_type84.getTree());
			ID85=(Token)match(input,ID,FOLLOW_ID_in_var_decl_pure1430); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_ID.add(ID85);

			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:265:17: ( COMMA ID )*
			loop20:
			while (true) {
				int alt20=2;
				int LA20_0 = input.LA(1);
				if ( (LA20_0==COMMA) ) {
					alt20=1;
				}

				switch (alt20) {
				case 1 :
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:265:18: COMMA ID
					{
					COMMA86=(Token)match(input,COMMA,FOLLOW_COMMA_in_var_decl_pure1433); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_COMMA.add(COMMA86);

					ID87=(Token)match(input,ID,FOLLOW_ID_in_var_decl_pure1435); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_ID.add(ID87);

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
			// 265:29: -> ^( VAR var_type ( ID )+ )
			{
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:265:32: ^( VAR var_type ( ID )+ )
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
	// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:269:1: cond : cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom ) ;
	public final CtrlParser.cond_return cond() throws RecognitionException {
		CtrlParser.cond_return retval = new CtrlParser.cond_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		Token BAR89=null;
		ParserRuleReturnScope cond_atom88 =null;
		ParserRuleReturnScope cond_atom90 =null;

		CtrlTree BAR89_tree=null;
		RewriteRuleTokenStream stream_BAR=new RewriteRuleTokenStream(adaptor,"token BAR");
		RewriteRuleSubtreeStream stream_cond_atom=new RewriteRuleSubtreeStream(adaptor,"rule cond_atom");

		try {
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:270:2: ( cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom ) )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:272:4: cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom )
			{
			pushFollow(FOLLOW_cond_atom_in_cond1471);
			cond_atom88=cond_atom();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_cond_atom.add(cond_atom88.getTree());
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:273:4: ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom )
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
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:273:6: ( BAR cond_atom )+
					{
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:273:6: ( BAR cond_atom )+
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
							// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:273:7: BAR cond_atom
							{
							BAR89=(Token)match(input,BAR,FOLLOW_BAR_in_cond1480); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_BAR.add(BAR89);

							pushFollow(FOLLOW_cond_atom_in_cond1482);
							cond_atom90=cond_atom();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_cond_atom.add(cond_atom90.getTree());
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
					// 273:23: -> ^( CHOICE cond_atom ( cond_atom )+ )
					{
						// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:273:26: ^( CHOICE cond_atom ( cond_atom )+ )
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
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:274:6: 
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
					// 274:6: -> cond_atom
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
	// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:278:1: cond_atom : ( TRUE | call );
	public final CtrlParser.cond_atom_return cond_atom() throws RecognitionException {
		CtrlParser.cond_atom_return retval = new CtrlParser.cond_atom_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		Token TRUE91=null;
		ParserRuleReturnScope call92 =null;

		CtrlTree TRUE91_tree=null;

		try {
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:279:2: ( TRUE | call )
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
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:281:4: TRUE
					{
					root_0 = (CtrlTree)adaptor.nil();


					TRUE91=(Token)match(input,TRUE,FOLLOW_TRUE_in_cond_atom1528); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					TRUE91_tree = (CtrlTree)adaptor.create(TRUE91);
					adaptor.addChild(root_0, TRUE91_tree);
					}

					}
					break;
				case 2 :
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:285:5: call
					{
					root_0 = (CtrlTree)adaptor.nil();


					pushFollow(FOLLOW_call_in_cond_atom1549);
					call92=call();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, call92.getTree());

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
	// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:288:1: expr : expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 ) ;
	public final CtrlParser.expr_return expr() throws RecognitionException {
		CtrlParser.expr_return retval = new CtrlParser.expr_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		Token BAR94=null;
		ParserRuleReturnScope expr293 =null;
		ParserRuleReturnScope expr295 =null;

		CtrlTree BAR94_tree=null;
		RewriteRuleTokenStream stream_BAR=new RewriteRuleTokenStream(adaptor,"token BAR");
		RewriteRuleSubtreeStream stream_expr2=new RewriteRuleSubtreeStream(adaptor,"rule expr2");

		try {
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:289:2: ( expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 ) )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:293:4: expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 )
			{
			pushFollow(FOLLOW_expr2_in_expr1579);
			expr293=expr2();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_expr2.add(expr293.getTree());
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:294:4: ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 )
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
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:294:6: ( BAR expr2 )+
					{
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:294:6: ( BAR expr2 )+
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
							// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:294:7: BAR expr2
							{
							BAR94=(Token)match(input,BAR,FOLLOW_BAR_in_expr1587); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_BAR.add(BAR94);

							pushFollow(FOLLOW_expr2_in_expr1589);
							expr295=expr2();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_expr2.add(expr295.getTree());
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
					// 294:19: -> ^( CHOICE expr2 ( expr2 )+ )
					{
						// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:294:22: ^( CHOICE expr2 ( expr2 )+ )
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
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:295:6: 
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
					// 295:6: -> expr2
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
	// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:299:1: expr2 : (e= expr_atom (plus= PLUS -> ^( BLOCK $e ^( STAR[$plus] $e) ) |ast= ASTERISK -> ^( STAR[$ast] $e) | -> $e) |op= SHARP expr_atom -> ^( ALAP[$op] expr_atom ) );
	public final CtrlParser.expr2_return expr2() throws RecognitionException {
		CtrlParser.expr2_return retval = new CtrlParser.expr2_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		Token plus=null;
		Token ast=null;
		Token op=null;
		ParserRuleReturnScope e =null;
		ParserRuleReturnScope expr_atom96 =null;

		CtrlTree plus_tree=null;
		CtrlTree ast_tree=null;
		CtrlTree op_tree=null;
		RewriteRuleTokenStream stream_ASTERISK=new RewriteRuleTokenStream(adaptor,"token ASTERISK");
		RewriteRuleTokenStream stream_SHARP=new RewriteRuleTokenStream(adaptor,"token SHARP");
		RewriteRuleTokenStream stream_PLUS=new RewriteRuleTokenStream(adaptor,"token PLUS");
		RewriteRuleSubtreeStream stream_expr_atom=new RewriteRuleSubtreeStream(adaptor,"rule expr_atom");

		try {
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:300:3: (e= expr_atom (plus= PLUS -> ^( BLOCK $e ^( STAR[$plus] $e) ) |ast= ASTERISK -> ^( STAR[$ast] $e) | -> $e) |op= SHARP expr_atom -> ^( ALAP[$op] expr_atom ) )
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
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:308:5: e= expr_atom (plus= PLUS -> ^( BLOCK $e ^( STAR[$plus] $e) ) |ast= ASTERISK -> ^( STAR[$ast] $e) | -> $e)
					{
					pushFollow(FOLLOW_expr_atom_in_expr21670);
					e=expr_atom();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_expr_atom.add(e.getTree());
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:309:5: (plus= PLUS -> ^( BLOCK $e ^( STAR[$plus] $e) ) |ast= ASTERISK -> ^( STAR[$ast] $e) | -> $e)
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
							// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:309:7: plus= PLUS
							{
							plus=(Token)match(input,PLUS,FOLLOW_PLUS_in_expr21680); if (state.failed) return retval; 
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
							// 309:17: -> ^( BLOCK $e ^( STAR[$plus] $e) )
							{
								// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:309:20: ^( BLOCK $e ^( STAR[$plus] $e) )
								{
								CtrlTree root_1 = (CtrlTree)adaptor.nil();
								root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(BLOCK, "BLOCK"), root_1);
								adaptor.addChild(root_1, stream_e.nextTree());
								// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:309:31: ^( STAR[$plus] $e)
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
							// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:310:7: ast= ASTERISK
							{
							ast=(Token)match(input,ASTERISK,FOLLOW_ASTERISK_in_expr21707); if (state.failed) return retval; 
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
							// 310:20: -> ^( STAR[$ast] $e)
							{
								// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:310:23: ^( STAR[$ast] $e)
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
							// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:311:7: 
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
							// 311:7: -> $e
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
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:317:5: op= SHARP expr_atom
					{
					op=(Token)match(input,SHARP,FOLLOW_SHARP_in_expr21762); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_SHARP.add(op);

					pushFollow(FOLLOW_expr_atom_in_expr21764);
					expr_atom96=expr_atom();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_expr_atom.add(expr_atom96.getTree());
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
					// 317:24: -> ^( ALAP[$op] expr_atom )
					{
						// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:317:27: ^( ALAP[$op] expr_atom )
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
	// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:320:1: expr_atom : (open= LPAR expr close= RPAR -> ^( BLOCK[$open] expr TRUE[$close] ) | assign | call );
	public final CtrlParser.expr_atom_return expr_atom() throws RecognitionException {
		CtrlParser.expr_atom_return retval = new CtrlParser.expr_atom_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		Token open=null;
		Token close=null;
		ParserRuleReturnScope expr97 =null;
		ParserRuleReturnScope assign98 =null;
		ParserRuleReturnScope call99 =null;

		CtrlTree open_tree=null;
		CtrlTree close_tree=null;
		RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
		RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
		RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");

		try {
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:321:2: (open= LPAR expr close= RPAR -> ^( BLOCK[$open] expr TRUE[$close] ) | assign | call )
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
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:323:4: open= LPAR expr close= RPAR
					{
					open=(Token)match(input,LPAR,FOLLOW_LPAR_in_expr_atom1795); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_LPAR.add(open);

					pushFollow(FOLLOW_expr_in_expr_atom1797);
					expr97=expr();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_expr.add(expr97.getTree());
					close=(Token)match(input,RPAR,FOLLOW_RPAR_in_expr_atom1801); if (state.failed) return retval; 
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
					// 324:4: -> ^( BLOCK[$open] expr TRUE[$close] )
					{
						// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:324:7: ^( BLOCK[$open] expr TRUE[$close] )
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
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:327:5: assign
					{
					root_0 = (CtrlTree)adaptor.nil();


					pushFollow(FOLLOW_assign_in_expr_atom1832);
					assign98=assign();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, assign98.getTree());

					}
					break;
				case 3 :
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:330:4: call
					{
					root_0 = (CtrlTree)adaptor.nil();


					pushFollow(FOLLOW_call_in_expr_atom1845);
					call99=call();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, call99.getTree());

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
	// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:334:1: assign : target ( COMMA target )* BECOMES call -> ^( BECOMES ^( ARGS ( target )+ RPAR ) call ) ;
	public final CtrlParser.assign_return assign() throws RecognitionException {
		CtrlParser.assign_return retval = new CtrlParser.assign_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		Token COMMA101=null;
		Token BECOMES103=null;
		ParserRuleReturnScope target100 =null;
		ParserRuleReturnScope target102 =null;
		ParserRuleReturnScope call104 =null;

		CtrlTree COMMA101_tree=null;
		CtrlTree BECOMES103_tree=null;
		RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
		RewriteRuleTokenStream stream_BECOMES=new RewriteRuleTokenStream(adaptor,"token BECOMES");
		RewriteRuleSubtreeStream stream_call=new RewriteRuleSubtreeStream(adaptor,"rule call");
		RewriteRuleSubtreeStream stream_target=new RewriteRuleSubtreeStream(adaptor,"rule target");

		try {
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:335:3: ( target ( COMMA target )* BECOMES call -> ^( BECOMES ^( ARGS ( target )+ RPAR ) call ) )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:342:5: target ( COMMA target )* BECOMES call
			{
			pushFollow(FOLLOW_target_in_assign1895);
			target100=target();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_target.add(target100.getTree());
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:342:12: ( COMMA target )*
			loop29:
			while (true) {
				int alt29=2;
				int LA29_0 = input.LA(1);
				if ( (LA29_0==COMMA) ) {
					alt29=1;
				}

				switch (alt29) {
				case 1 :
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:342:13: COMMA target
					{
					COMMA101=(Token)match(input,COMMA,FOLLOW_COMMA_in_assign1898); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_COMMA.add(COMMA101);

					pushFollow(FOLLOW_target_in_assign1900);
					target102=target();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_target.add(target102.getTree());
					}
					break;

				default :
					break loop29;
				}
			}

			BECOMES103=(Token)match(input,BECOMES,FOLLOW_BECOMES_in_assign1904); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_BECOMES.add(BECOMES103);

			pushFollow(FOLLOW_call_in_assign1906);
			call104=call();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_call.add(call104.getTree());
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
			// 343:5: -> ^( BECOMES ^( ARGS ( target )+ RPAR ) call )
			{
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:343:8: ^( BECOMES ^( ARGS ( target )+ RPAR ) call )
				{
				CtrlTree root_1 = (CtrlTree)adaptor.nil();
				root_1 = (CtrlTree)adaptor.becomeRoot(stream_BECOMES.nextNode(), root_1);
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:343:18: ^( ARGS ( target )+ RPAR )
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
	// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:346:1: target : ID -> ^( ARG OUT ID ) ;
	public final CtrlParser.target_return target() throws RecognitionException {
		CtrlParser.target_return retval = new CtrlParser.target_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		Token ID105=null;

		CtrlTree ID105_tree=null;
		RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

		try {
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:347:3: ( ID -> ^( ARG OUT ID ) )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:347:5: ID
			{
			ID105=(Token)match(input,ID,FOLLOW_ID_in_target1940); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_ID.add(ID105);

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
			// 347:8: -> ^( ARG OUT ID )
			{
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:347:11: ^( ARG OUT ID )
				{
				CtrlTree root_1 = (CtrlTree)adaptor.nil();
				root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(ARG, "ARG"), root_1);
				adaptor.addChild(root_1, (CtrlTree)adaptor.create(OUT, "OUT"));
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
	// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:351:1: call : rule_name ( arg_list )? -> ^( CALL[$rule_name.start] rule_name ( arg_list )? ) ;
	public final CtrlParser.call_return call() throws RecognitionException {
		CtrlParser.call_return retval = new CtrlParser.call_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		ParserRuleReturnScope rule_name106 =null;
		ParserRuleReturnScope arg_list107 =null;

		RewriteRuleSubtreeStream stream_rule_name=new RewriteRuleSubtreeStream(adaptor,"rule rule_name");
		RewriteRuleSubtreeStream stream_arg_list=new RewriteRuleSubtreeStream(adaptor,"rule arg_list");

		try {
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:352:2: ( rule_name ( arg_list )? -> ^( CALL[$rule_name.start] rule_name ( arg_list )? ) )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:356:4: rule_name ( arg_list )?
			{
			pushFollow(FOLLOW_rule_name_in_call1980);
			rule_name106=rule_name();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_rule_name.add(rule_name106.getTree());
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:356:14: ( arg_list )?
			int alt30=2;
			int LA30_0 = input.LA(1);
			if ( (LA30_0==LPAR) ) {
				alt30=1;
			}
			switch (alt30) {
				case 1 :
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:356:14: arg_list
					{
					pushFollow(FOLLOW_arg_list_in_call1982);
					arg_list107=arg_list();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_arg_list.add(arg_list107.getTree());
					}
					break;

			}

			if ( state.backtracking==0 ) { helper.registerCall((rule_name106!=null?((CtrlTree)rule_name106.getTree()):null)); }
			// AST REWRITE
			// elements: rule_name, arg_list
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CtrlTree)adaptor.nil();
			// 358:4: -> ^( CALL[$rule_name.start] rule_name ( arg_list )? )
			{
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:358:7: ^( CALL[$rule_name.start] rule_name ( arg_list )? )
				{
				CtrlTree root_1 = (CtrlTree)adaptor.nil();
				root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(CALL, (rule_name106!=null?(rule_name106.start):null)), root_1);
				adaptor.addChild(root_1, stream_rule_name.nextTree());
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:358:42: ( arg_list )?
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
	// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:362:1: rule_name : qual_name[true] ->;
	public final CtrlParser.rule_name_return rule_name() throws RecognitionException {
		CtrlParser.rule_name_return retval = new CtrlParser.rule_name_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		ParserRuleReturnScope qual_name108 =null;

		RewriteRuleSubtreeStream stream_qual_name=new RewriteRuleSubtreeStream(adaptor,"rule qual_name");

		try {
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:363:3: ( qual_name[true] ->)
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:373:5: qual_name[true]
			{
			pushFollow(FOLLOW_qual_name_in_rule_name2068);
			qual_name108=qual_name(true);
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_qual_name.add(qual_name108.getTree());
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
			// 374:5: ->
			{
				adaptor.addChild(root_0,  helper.qualify((qual_name108!=null?((CtrlTree)qual_name108.getTree()):null)) );
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
	// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:380:1: arg_list : open= LPAR ( arg ( COMMA arg )* )? close= RPAR -> ^( ARGS[$open] ( arg )* RPAR[$close] ) ;
	public final CtrlParser.arg_list_return arg_list() throws RecognitionException {
		CtrlParser.arg_list_return retval = new CtrlParser.arg_list_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		Token open=null;
		Token close=null;
		Token COMMA110=null;
		ParserRuleReturnScope arg109 =null;
		ParserRuleReturnScope arg111 =null;

		CtrlTree open_tree=null;
		CtrlTree close_tree=null;
		CtrlTree COMMA110_tree=null;
		RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
		RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
		RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
		RewriteRuleSubtreeStream stream_arg=new RewriteRuleSubtreeStream(adaptor,"rule arg");

		try {
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:381:3: (open= LPAR ( arg ( COMMA arg )* )? close= RPAR -> ^( ARGS[$open] ( arg )* RPAR[$close] ) )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:383:5: open= LPAR ( arg ( COMMA arg )* )? close= RPAR
			{
			open=(Token)match(input,LPAR,FOLLOW_LPAR_in_arg_list2104); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_LPAR.add(open);

			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:383:15: ( arg ( COMMA arg )* )?
			int alt32=2;
			int LA32_0 = input.LA(1);
			if ( (LA32_0==DONT_CARE||LA32_0==FALSE||LA32_0==ID||LA32_0==INT_LIT||LA32_0==OUT||LA32_0==REAL_LIT||(LA32_0 >= STRING_LIT && LA32_0 <= TRUE)) ) {
				alt32=1;
			}
			switch (alt32) {
				case 1 :
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:383:16: arg ( COMMA arg )*
					{
					pushFollow(FOLLOW_arg_in_arg_list2107);
					arg109=arg();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_arg.add(arg109.getTree());
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:383:20: ( COMMA arg )*
					loop31:
					while (true) {
						int alt31=2;
						int LA31_0 = input.LA(1);
						if ( (LA31_0==COMMA) ) {
							alt31=1;
						}

						switch (alt31) {
						case 1 :
							// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:383:21: COMMA arg
							{
							COMMA110=(Token)match(input,COMMA,FOLLOW_COMMA_in_arg_list2110); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_COMMA.add(COMMA110);

							pushFollow(FOLLOW_arg_in_arg_list2112);
							arg111=arg();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_arg.add(arg111.getTree());
							}
							break;

						default :
							break loop31;
						}
					}

					}
					break;

			}

			close=(Token)match(input,RPAR,FOLLOW_RPAR_in_arg_list2120); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_RPAR.add(close);

			// AST REWRITE
			// elements: RPAR, arg
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CtrlTree)adaptor.nil();
			// 384:5: -> ^( ARGS[$open] ( arg )* RPAR[$close] )
			{
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:384:8: ^( ARGS[$open] ( arg )* RPAR[$close] )
				{
				CtrlTree root_1 = (CtrlTree)adaptor.nil();
				root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(ARGS, open), root_1);
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:384:22: ( arg )*
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
	// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:390:1: arg : ( OUT ID -> ^( ARG OUT ID ) | ID -> ^( ARG ID ) | DONT_CARE -> ^( ARG DONT_CARE ) | literal -> ^( ARG literal ) );
	public final CtrlParser.arg_return arg() throws RecognitionException {
		CtrlParser.arg_return retval = new CtrlParser.arg_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		Token OUT112=null;
		Token ID113=null;
		Token ID114=null;
		Token DONT_CARE115=null;
		ParserRuleReturnScope literal116 =null;

		CtrlTree OUT112_tree=null;
		CtrlTree ID113_tree=null;
		CtrlTree ID114_tree=null;
		CtrlTree DONT_CARE115_tree=null;
		RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
		RewriteRuleTokenStream stream_DONT_CARE=new RewriteRuleTokenStream(adaptor,"token DONT_CARE");
		RewriteRuleTokenStream stream_OUT=new RewriteRuleTokenStream(adaptor,"token OUT");
		RewriteRuleSubtreeStream stream_literal=new RewriteRuleSubtreeStream(adaptor,"rule literal");

		try {
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:391:3: ( OUT ID -> ^( ARG OUT ID ) | ID -> ^( ARG ID ) | DONT_CARE -> ^( ARG DONT_CARE ) | literal -> ^( ARG literal ) )
			int alt33=4;
			switch ( input.LA(1) ) {
			case OUT:
				{
				alt33=1;
				}
				break;
			case ID:
				{
				alt33=2;
				}
				break;
			case DONT_CARE:
				{
				alt33=3;
				}
				break;
			case FALSE:
			case INT_LIT:
			case REAL_LIT:
			case STRING_LIT:
			case TRUE:
				{
				alt33=4;
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
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:394:5: OUT ID
					{
					OUT112=(Token)match(input,OUT,FOLLOW_OUT_in_arg2167); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_OUT.add(OUT112);

					ID113=(Token)match(input,ID,FOLLOW_ID_in_arg2169); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_ID.add(ID113);

					// AST REWRITE
					// elements: ID, OUT
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CtrlTree)adaptor.nil();
					// 394:12: -> ^( ARG OUT ID )
					{
						// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:394:15: ^( ARG OUT ID )
						{
						CtrlTree root_1 = (CtrlTree)adaptor.nil();
						root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(ARG, "ARG"), root_1);
						adaptor.addChild(root_1, stream_OUT.nextNode());
						adaptor.addChild(root_1, stream_ID.nextNode());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:398:5: ID
					{
					ID114=(Token)match(input,ID,FOLLOW_ID_in_arg2200); if (state.failed) return retval; 
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
					// 398:8: -> ^( ARG ID )
					{
						// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:398:11: ^( ARG ID )
						{
						CtrlTree root_1 = (CtrlTree)adaptor.nil();
						root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(ARG, "ARG"), root_1);
						adaptor.addChild(root_1, stream_ID.nextNode());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 3 :
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:402:5: DONT_CARE
					{
					DONT_CARE115=(Token)match(input,DONT_CARE,FOLLOW_DONT_CARE_in_arg2229); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_DONT_CARE.add(DONT_CARE115);

					// AST REWRITE
					// elements: DONT_CARE
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CtrlTree)adaptor.nil();
					// 402:15: -> ^( ARG DONT_CARE )
					{
						// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:402:18: ^( ARG DONT_CARE )
						{
						CtrlTree root_1 = (CtrlTree)adaptor.nil();
						root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(ARG, "ARG"), root_1);
						adaptor.addChild(root_1, stream_DONT_CARE.nextNode());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 4 :
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:403:5: literal
					{
					pushFollow(FOLLOW_literal_in_arg2246);
					literal116=literal();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_literal.add(literal116.getTree());
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
					// 403:13: -> ^( ARG literal )
					{
						// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:403:16: ^( ARG literal )
						{
						CtrlTree root_1 = (CtrlTree)adaptor.nil();
						root_1 = (CtrlTree)adaptor.becomeRoot((CtrlTree)adaptor.create(ARG, "ARG"), root_1);
						adaptor.addChild(root_1, stream_literal.nextTree());
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
	// $ANTLR end "arg"


	public static class literal_return extends ParserRuleReturnScope {
		CtrlTree tree;
		@Override
		public CtrlTree getTree() { return tree; }
	};


	// $ANTLR start "literal"
	// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:406:1: literal : ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT );
	public final CtrlParser.literal_return literal() throws RecognitionException {
		CtrlParser.literal_return retval = new CtrlParser.literal_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		Token set117=null;

		CtrlTree set117_tree=null;

		try {
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:407:3: ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:
			{
			root_0 = (CtrlTree)adaptor.nil();


			set117=input.LT(1);
			if ( input.LA(1)==FALSE||input.LA(1)==INT_LIT||input.LA(1)==REAL_LIT||(input.LA(1) >= STRING_LIT && input.LA(1) <= TRUE) ) {
				input.consume();
				if ( state.backtracking==0 ) adaptor.addChild(root_0, (CtrlTree)adaptor.create(set117));
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
	// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:425:1: var_type : ( NODE | BOOL | STRING | INT | REAL );
	public final CtrlParser.var_type_return var_type() throws RecognitionException {
		CtrlParser.var_type_return retval = new CtrlParser.var_type_return();
		retval.start = input.LT(1);

		CtrlTree root_0 = null;

		Token set118=null;

		CtrlTree set118_tree=null;

		try {
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:426:2: ( NODE | BOOL | STRING | INT | REAL )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:
			{
			root_0 = (CtrlTree)adaptor.nil();


			set118=input.LT(1);
			if ( input.LA(1)==BOOL||input.LA(1)==INT||input.LA(1)==NODE||input.LA(1)==REAL||input.LA(1)==STRING ) {
				input.consume();
				if ( state.backtracking==0 ) adaptor.addChild(root_0, (CtrlTree)adaptor.create(set118));
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
		// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:240:33: ( ELSE )
		// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:240:34: ELSE
		{
		match(input,ELSE,FOLLOW_ELSE_in_synpred1_Ctrl1246); if (state.failed) return;

		}

	}
	// $ANTLR end synpred1_Ctrl

	// $ANTLR start synpred2_Ctrl
	public final void synpred2_Ctrl_fragment() throws RecognitionException {
		// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:244:17: ( ELSE )
		// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:244:18: ELSE
		{
		match(input,ELSE,FOLLOW_ELSE_in_synpred2_Ctrl1286); if (state.failed) return;

		}

	}
	// $ANTLR end synpred2_Ctrl

	// $ANTLR start synpred3_Ctrl
	public final void synpred3_Ctrl_fragment() throws RecognitionException {
		// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:247:20: ( OR )
		// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:247:21: OR
		{
		match(input,OR,FOLLOW_OR_in_synpred3_Ctrl1321); if (state.failed) return;

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



	public static final BitSet FOLLOW_package_decl_in_program166 = new BitSet(new long[]{0x8A0044E5D0144250L,0x0000000000000164L});
	public static final BitSet FOLLOW_import_decl_in_program172 = new BitSet(new long[]{0x8A0044E5D0144250L,0x0000000000000164L});
	public static final BitSet FOLLOW_function_in_program180 = new BitSet(new long[]{0x8A0044E4D0144250L,0x0000000000000164L});
	public static final BitSet FOLLOW_recipe_in_program182 = new BitSet(new long[]{0x8A0044E4D0144250L,0x0000000000000164L});
	public static final BitSet FOLLOW_stat_in_program184 = new BitSet(new long[]{0x8A0044E4D0144250L,0x0000000000000164L});
	public static final BitSet FOLLOW_EOF_in_program188 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_PACKAGE_in_package_decl325 = new BitSet(new long[]{0x0000400040000240L});
	public static final BitSet FOLLOW_qual_name_in_package_decl327 = new BitSet(new long[]{0x4000000000000000L});
	public static final BitSet FOLLOW_SEMI_in_package_decl332 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_IMPORT_in_import_decl399 = new BitSet(new long[]{0x0000400040000240L});
	public static final BitSet FOLLOW_qual_name_in_import_decl402 = new BitSet(new long[]{0x4000000000000000L});
	public static final BitSet FOLLOW_SEMI_in_import_decl405 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_qual_name447 = new BitSet(new long[]{0x0000000000400002L});
	public static final BitSet FOLLOW_DOT_in_qual_name451 = new BitSet(new long[]{0x0000400040000240L});
	public static final BitSet FOLLOW_qual_name_in_qual_name455 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ASTERISK_in_qual_name494 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_DOT_in_qual_name496 = new BitSet(new long[]{0x0000400000000040L});
	public static final BitSet FOLLOW_ANY_in_qual_name516 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_OTHER_in_qual_name539 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_RECIPE_in_recipe608 = new BitSet(new long[]{0x0000000040000000L});
	public static final BitSet FOLLOW_ID_in_recipe611 = new BitSet(new long[]{0x0000008000000000L});
	public static final BitSet FOLLOW_par_list_in_recipe613 = new BitSet(new long[]{0x0010004000000000L});
	public static final BitSet FOLLOW_PRIORITY_in_recipe616 = new BitSet(new long[]{0x0000000800000000L});
	public static final BitSet FOLLOW_INT_LIT_in_recipe619 = new BitSet(new long[]{0x0000004000000000L});
	public static final BitSet FOLLOW_block_in_recipe633 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_FUNCTION_in_function679 = new BitSet(new long[]{0x0000000040000000L});
	public static final BitSet FOLLOW_ID_in_function682 = new BitSet(new long[]{0x0000008000000000L});
	public static final BitSet FOLLOW_par_list_in_function684 = new BitSet(new long[]{0x0000004000000000L});
	public static final BitSet FOLLOW_block_in_function697 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LPAR_in_par_list728 = new BitSet(new long[]{0x2200840400004000L,0x0000000000000004L});
	public static final BitSet FOLLOW_par_in_par_list731 = new BitSet(new long[]{0x2000000000080000L});
	public static final BitSet FOLLOW_COMMA_in_par_list734 = new BitSet(new long[]{0x0200840400004000L,0x0000000000000004L});
	public static final BitSet FOLLOW_par_in_par_list736 = new BitSet(new long[]{0x2000000000080000L});
	public static final BitSet FOLLOW_RPAR_in_par_list742 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_OUT_in_par787 = new BitSet(new long[]{0x0200040400004000L,0x0000000000000004L});
	public static final BitSet FOLLOW_var_type_in_par789 = new BitSet(new long[]{0x0000000040000000L});
	public static final BitSet FOLLOW_ID_in_par791 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_var_type_in_par824 = new BitSet(new long[]{0x0000000040000000L});
	public static final BitSet FOLLOW_ID_in_par826 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LCURLY_in_block865 = new BitSet(new long[]{0x830044E4C0144250L,0x0000000000000164L});
	public static final BitSet FOLLOW_stat_in_block867 = new BitSet(new long[]{0x830044E4C0144250L,0x0000000000000164L});
	public static final BitSet FOLLOW_RCURLY_in_block872 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_var_decl_in_stat911 = new BitSet(new long[]{0x4000000000000000L});
	public static final BitSet FOLLOW_SEMI_in_stat913 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_block_in_stat925 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ALAP_in_stat942 = new BitSet(new long[]{0x820044E4C0144250L,0x0000000000000164L});
	public static final BitSet FOLLOW_stat_in_stat945 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LANGLE_in_stat968 = new BitSet(new long[]{0x828044E4C0144250L,0x0000000000000164L});
	public static final BitSet FOLLOW_stat_in_stat970 = new BitSet(new long[]{0x828044E4C0144250L,0x0000000000000164L});
	public static final BitSet FOLLOW_RANGLE_in_stat975 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_WHILE_in_stat1016 = new BitSet(new long[]{0x0000008000000000L});
	public static final BitSet FOLLOW_LPAR_in_stat1019 = new BitSet(new long[]{0x0000400040000240L,0x0000000000000010L});
	public static final BitSet FOLLOW_cond_in_stat1022 = new BitSet(new long[]{0x2000000000000000L});
	public static final BitSet FOLLOW_RPAR_in_stat1024 = new BitSet(new long[]{0x820044E4C0144250L,0x0000000000000164L});
	public static final BitSet FOLLOW_stat_in_stat1027 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_UNTIL_in_stat1047 = new BitSet(new long[]{0x0000008000000000L});
	public static final BitSet FOLLOW_LPAR_in_stat1050 = new BitSet(new long[]{0x0000400040000240L,0x0000000000000010L});
	public static final BitSet FOLLOW_cond_in_stat1053 = new BitSet(new long[]{0x2000000000000000L});
	public static final BitSet FOLLOW_RPAR_in_stat1055 = new BitSet(new long[]{0x820044E4C0144250L,0x0000000000000164L});
	public static final BitSet FOLLOW_stat_in_stat1058 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DO_in_stat1063 = new BitSet(new long[]{0x820044E4C0144250L,0x0000000000000164L});
	public static final BitSet FOLLOW_stat_in_stat1065 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000140L});
	public static final BitSet FOLLOW_WHILE_in_stat1108 = new BitSet(new long[]{0x0000008000000000L});
	public static final BitSet FOLLOW_LPAR_in_stat1110 = new BitSet(new long[]{0x0000400040000240L,0x0000000000000010L});
	public static final BitSet FOLLOW_cond_in_stat1112 = new BitSet(new long[]{0x2000000000000000L});
	public static final BitSet FOLLOW_RPAR_in_stat1114 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_UNTIL_in_stat1177 = new BitSet(new long[]{0x0000008000000000L});
	public static final BitSet FOLLOW_LPAR_in_stat1179 = new BitSet(new long[]{0x0000400040000240L,0x0000000000000010L});
	public static final BitSet FOLLOW_cond_in_stat1181 = new BitSet(new long[]{0x2000000000000000L});
	public static final BitSet FOLLOW_RPAR_in_stat1183 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_IF_in_stat1230 = new BitSet(new long[]{0x0000008000000000L});
	public static final BitSet FOLLOW_LPAR_in_stat1233 = new BitSet(new long[]{0x0000400040000240L,0x0000000000000010L});
	public static final BitSet FOLLOW_cond_in_stat1236 = new BitSet(new long[]{0x2000000000000000L});
	public static final BitSet FOLLOW_RPAR_in_stat1238 = new BitSet(new long[]{0x820044E4C0144250L,0x0000000000000164L});
	public static final BitSet FOLLOW_stat_in_stat1241 = new BitSet(new long[]{0x0000000002000002L});
	public static final BitSet FOLLOW_ELSE_in_stat1251 = new BitSet(new long[]{0x820044E4C0144250L,0x0000000000000164L});
	public static final BitSet FOLLOW_stat_in_stat1254 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TRY_in_stat1278 = new BitSet(new long[]{0x820044E4C0144250L,0x0000000000000164L});
	public static final BitSet FOLLOW_stat_in_stat1281 = new BitSet(new long[]{0x0000000002000002L});
	public static final BitSet FOLLOW_ELSE_in_stat1291 = new BitSet(new long[]{0x820044E4C0144250L,0x0000000000000164L});
	public static final BitSet FOLLOW_stat_in_stat1294 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_CHOICE_in_stat1313 = new BitSet(new long[]{0x820044E4C0144250L,0x0000000000000164L});
	public static final BitSet FOLLOW_stat_in_stat1316 = new BitSet(new long[]{0x0000200000000000L});
	public static final BitSet FOLLOW_OR_in_stat1326 = new BitSet(new long[]{0x820044E4C0144250L,0x0000000000000164L});
	public static final BitSet FOLLOW_stat_in_stat1329 = new BitSet(new long[]{0x0000200000000002L});
	public static final BitSet FOLLOW_expr_in_stat1344 = new BitSet(new long[]{0x4000000000000000L});
	public static final BitSet FOLLOW_SEMI_in_stat1346 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_var_decl_pure_in_var_decl1378 = new BitSet(new long[]{0x0000000000001002L});
	public static final BitSet FOLLOW_BECOMES_in_var_decl1396 = new BitSet(new long[]{0x0000400040000240L});
	public static final BitSet FOLLOW_call_in_var_decl1398 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_var_type_in_var_decl_pure1428 = new BitSet(new long[]{0x0000000040000000L});
	public static final BitSet FOLLOW_ID_in_var_decl_pure1430 = new BitSet(new long[]{0x0000000000080002L});
	public static final BitSet FOLLOW_COMMA_in_var_decl_pure1433 = new BitSet(new long[]{0x0000000040000000L});
	public static final BitSet FOLLOW_ID_in_var_decl_pure1435 = new BitSet(new long[]{0x0000000000080002L});
	public static final BitSet FOLLOW_cond_atom_in_cond1471 = new BitSet(new long[]{0x0000000000000802L});
	public static final BitSet FOLLOW_BAR_in_cond1480 = new BitSet(new long[]{0x0000400040000240L,0x0000000000000010L});
	public static final BitSet FOLLOW_cond_atom_in_cond1482 = new BitSet(new long[]{0x0000000000000802L});
	public static final BitSet FOLLOW_TRUE_in_cond_atom1528 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_call_in_cond_atom1549 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expr2_in_expr1579 = new BitSet(new long[]{0x0000000000000802L});
	public static final BitSet FOLLOW_BAR_in_expr1587 = new BitSet(new long[]{0x8000408040000240L});
	public static final BitSet FOLLOW_expr2_in_expr1589 = new BitSet(new long[]{0x0000000000000802L});
	public static final BitSet FOLLOW_expr_atom_in_expr21670 = new BitSet(new long[]{0x0008000000000202L});
	public static final BitSet FOLLOW_PLUS_in_expr21680 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ASTERISK_in_expr21707 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_SHARP_in_expr21762 = new BitSet(new long[]{0x0000408040000240L});
	public static final BitSet FOLLOW_expr_atom_in_expr21764 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LPAR_in_expr_atom1795 = new BitSet(new long[]{0x8000408040000240L});
	public static final BitSet FOLLOW_expr_in_expr_atom1797 = new BitSet(new long[]{0x2000000000000000L});
	public static final BitSet FOLLOW_RPAR_in_expr_atom1801 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_assign_in_expr_atom1832 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_call_in_expr_atom1845 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_target_in_assign1895 = new BitSet(new long[]{0x0000000000081000L});
	public static final BitSet FOLLOW_COMMA_in_assign1898 = new BitSet(new long[]{0x0000000040000000L});
	public static final BitSet FOLLOW_target_in_assign1900 = new BitSet(new long[]{0x0000000000081000L});
	public static final BitSet FOLLOW_BECOMES_in_assign1904 = new BitSet(new long[]{0x0000400040000240L});
	public static final BitSet FOLLOW_call_in_assign1906 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_target1940 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_rule_name_in_call1980 = new BitSet(new long[]{0x0000008000000002L});
	public static final BitSet FOLLOW_arg_list_in_call1982 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_qual_name_in_rule_name2068 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LPAR_in_arg_list2104 = new BitSet(new long[]{0x2400800848200000L,0x0000000000000018L});
	public static final BitSet FOLLOW_arg_in_arg_list2107 = new BitSet(new long[]{0x2000000000080000L});
	public static final BitSet FOLLOW_COMMA_in_arg_list2110 = new BitSet(new long[]{0x0400800848200000L,0x0000000000000018L});
	public static final BitSet FOLLOW_arg_in_arg_list2112 = new BitSet(new long[]{0x2000000000080000L});
	public static final BitSet FOLLOW_RPAR_in_arg_list2120 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_OUT_in_arg2167 = new BitSet(new long[]{0x0000000040000000L});
	public static final BitSet FOLLOW_ID_in_arg2169 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_arg2200 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DONT_CARE_in_arg2229 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_literal_in_arg2246 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ELSE_in_synpred1_Ctrl1246 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ELSE_in_synpred2_Ctrl1286 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_OR_in_synpred3_Ctrl1321 = new BitSet(new long[]{0x0000000000000002L});
}
