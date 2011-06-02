/*
 * Generated on 6/3/11 12:59 AM
 */
package groove.prolog.util;

import java.io.*;
import javax.swing.text.Segment;

import org.fife.ui.rsyntaxtextarea.*;


/**
 * Token maker for syntax highlighting in Prolog.
Generated using TokenMakerMaker

 */
%%

%public
%class PrologTokenMaker
%extends AbstractJFlexTokenMaker
%unicode
/* Case sensitive */
%type org.fife.ui.rsyntaxtextarea.Token


%{


	/**
	 * Constructor.  This must be here because JFlex does not generate a
	 * no-parameter constructor.
	 */
	public PrologTokenMaker() {
	}


	/**
	 * Adds the token specified to the current linked list of tokens.
	 *
	 * @param tokenType The token's type.
	 * @see #addToken(int, int, int)
	 */
	private void addHyperlinkToken(int start, int end, int tokenType) {
		int so = start + offsetShift;
		addToken(zzBuffer, start,end, tokenType, so, true);
	}


	/**
	 * Adds the token specified to the current linked list of tokens.
	 *
	 * @param tokenType The token's type.
	 */
	private void addToken(int tokenType) {
		addToken(zzStartRead, zzMarkedPos-1, tokenType);
	}


	/**
	 * Adds the token specified to the current linked list of tokens.
	 *
	 * @param tokenType The token's type.
	 * @see #addHyperlinkToken(int, int, int)
	 */
	private void addToken(int start, int end, int tokenType) {
		int so = start + offsetShift;
		addToken(zzBuffer, start,end, tokenType, so, false);
	}


	/**
	 * Adds the token specified to the current linked list of tokens.
	 *
	 * @param array The character array.
	 * @param start The starting offset in the array.
	 * @param end The ending offset in the array.
	 * @param tokenType The token's type.
	 * @param startOffset The offset in the document at which this token
	 *        occurs.
	 * @param hyperlink Whether this token is a hyperlink.
	 */
	public void addToken(char[] array, int start, int end, int tokenType,
						int startOffset, boolean hyperlink) {
		super.addToken(array, start,end, tokenType, startOffset, hyperlink);
		zzStartRead = zzMarkedPos;
	}


	/**
	 * Returns the text to place at the beginning and end of a
	 * line to "comment" it in a this programming language.
	 *
	 * @return The start and end strings to add to a line to "comment"
	 *         it out.
	 */
	public String[] getLineCommentStartAndEnd() {
		return null;
	}


	/**
	 * Returns the first token in the linked list of tokens generated
	 * from <code>text</code>.  This method must be implemented by
	 * subclasses so they can correctly implement syntax highlighting.
	 *
	 * @param text The text from which to get tokens.
	 * @param initialTokenType The token type we should start with.
	 * @param startOffset The offset into the document at which
	 *        <code>text</code> starts.
	 * @return The first <code>Token</code> in a linked list representing
	 *         the syntax highlighted text.
	 */
	public Token getTokenList(Segment text, int initialTokenType, int startOffset) {

		resetTokenList();
		this.offsetShift = -text.offset + startOffset;

		// Start off in the proper state.
		int state = Token.NULL;
		switch (initialTokenType) {
						case Token.COMMENT_MULTILINE:
				state = MLC;
				start = text.offset;
				break;

			/* No documentation comments */
			default:
				state = Token.NULL;
		}

		s = text;
		try {
			yyreset(zzReader);
			yybegin(state);
			return yylex();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return new DefaultToken();
		}

	}


	/**
	 * Refills the input buffer.
	 *
	 * @return      <code>true</code> if EOF was reached, otherwise
	 *              <code>false</code>.
	 */
	private boolean zzRefill() {
		return zzCurrentPos>=s.offset+s.count;
	}


	/**
	 * Resets the scanner to read from a new input stream.
	 * Does not close the old reader.
	 *
	 * All internal variables are reset, the old input stream 
	 * <b>cannot</b> be reused (internal buffer is discarded and lost).
	 * Lexical state is set to <tt>YY_INITIAL</tt>.
	 *
	 * @param reader   the new input stream 
	 */
	public final void yyreset(java.io.Reader reader) {
		// 's' has been updated.
		zzBuffer = s.array;
		/*
		 * We replaced the line below with the two below it because zzRefill
		 * no longer "refills" the buffer (since the way we do it, it's always
		 * "full" the first time through, since it points to the segment's
		 * array).  So, we assign zzEndRead here.
		 */
		//zzStartRead = zzEndRead = s.offset;
		zzStartRead = s.offset;
		zzEndRead = zzStartRead + s.count - 1;
		zzCurrentPos = zzMarkedPos = zzPushbackPos = s.offset;
		zzLexicalState = YYINITIAL;
		zzReader = reader;
		zzAtBOL  = true;
		zzAtEOF  = false;
	}


%}

Letter							= [A-Za-z]
LetterOrUnderscore				= ({Letter}|"_")
NonzeroDigit						= [1-9]
Digit							= ("0"|{NonzeroDigit})
HexDigit							= ({Digit}|[A-Fa-f])
OctalDigit						= ([0-7])
AnyCharacterButApostropheOrBackSlash	= ([^\\'])
AnyCharacterButDoubleQuoteOrBackSlash	= ([^\\\"\n])
EscapedSourceCharacter				= ("u"{HexDigit}{HexDigit}{HexDigit}{HexDigit})
Escape							= ("\\"(([btnfr\"'\\])|([0123]{OctalDigit}?{OctalDigit}?)|({OctalDigit}{OctalDigit}?)|{EscapedSourceCharacter}))
NonSeparator						= ([^\t\f\r\n\ \(\)\{\}\[\]\;\,\.\=\>\<\!\~\?\:\+\-\*\/\&\|\^\%\"\']|"#"|"\\")
IdentifierStart					= ({LetterOrUnderscore}|"$")
IdentifierPart						= ({IdentifierStart}|{Digit}|("\\"{EscapedSourceCharacter}))

LineTerminator				= (\n)
WhiteSpace				= ([ \t\f]+)

CharLiteral	= ([\']({AnyCharacterButApostropheOrBackSlash}|{Escape})[\'])
UnclosedCharLiteral			= ([\'][^\'\n]*)
ErrorCharLiteral			= ({UnclosedCharLiteral}[\'])
StringLiteral				= ([\"]({AnyCharacterButDoubleQuoteOrBackSlash}|{Escape})*[\"])
UnclosedStringLiteral		= ([\"]([\\].|[^\\\"])*[^\"]?)
ErrorStringLiteral			= ({UnclosedStringLiteral}[\"])

MLCBegin					= "/*"
MLCEnd					= "*/"

/* No documentation comments */
LineCommentBegin			= "%"

IntegerHelper1				= (({NonzeroDigit}{Digit}*)|"0")
IntegerHelper2				= ("0"(([xX]{HexDigit}+)|({OctalDigit}*)))
IntegerLiteral				= ({IntegerHelper1}[lL]?)
HexLiteral				= ({IntegerHelper2}[lL]?)
FloatHelper1				= ([fFdD]?)
FloatHelper2				= ([eE][+-]?{Digit}+{FloatHelper1})
FloatLiteral1				= ({Digit}+"."({FloatHelper1}|{FloatHelper2}|{Digit}+({FloatHelper1}|{FloatHelper2})))
FloatLiteral2				= ("."{Digit}+({FloatHelper1}|{FloatHelper2}))
FloatLiteral3				= ({Digit}+{FloatHelper2})
FloatLiteral				= ({FloatLiteral1}|{FloatLiteral2}|{FloatLiteral3}|({Digit}+[fFdD]))
ErrorNumberFormat			= (({IntegerLiteral}|{HexLiteral}|{FloatLiteral}){NonSeparator}+)
BooleanLiteral				= ("true"|"false")

Separator					= ([\(\)\{\}\[\]])
Separator2				= ([\;,.])

Identifier				= ({IdentifierStart}{IdentifierPart}*)

URLGenDelim				= ([:\/\?#\[\]@])
URLSubDelim				= ([\!\$&'\(\)\*\+,;=])
URLUnreserved			= ({LetterOrUnderscore}|{Digit}|[\-\.\~])
URLCharacter			= ({URLGenDelim}|{URLSubDelim}|{URLUnreserved}|[%])
URLCharacters			= ({URLCharacter}*)
URLEndCharacter			= ([\/\$]|{Letter}|{Digit})
URL						= (((https?|f(tp|ile))"://"|"www.")({URLCharacters}{URLEndCharacter})?)


/* No string state */
/* No char state */
%state MLC
/* No documentation comment state */
%state EOL_COMMENT

%%

<YYINITIAL> {

	/* Keywords */
	"is"		{ addToken(Token.RESERVED_WORD); }

	/* Data types */
	/* No data types */

	/* Functions */
	"abolish" |
"active_ruleevent" |
"active_ruleevent" |
"active_state" |
"active_state" |
"append" |
"arg" |
"asserta" |
"assertz" |
"at_end_of_stream" |
"atom" |
"atom_chars" |
"atom_codes" |
"atom_concat" |
"atom_length" |
"atomic" |
"bagof" |
"call" |
"char_code" |
"char_conversion" |
"clause" |
"close" |
"closed_state" |
"closed_state" |
"compare" |
"composite_type_graph" |
"composite_type_graph" |
"compound" |
"confluent_rule" |
"confluent_rule" |
"confluent_rule_name" |
"confluent_rule_name" |
"convert_valuenode" |
"convert_valuenode" |
"copy_term" |
"current_char_conversion" |
"current_functor" |
"current_input" |
"current_op" |
"current_output" |
"current_predicate" |
"current_prolog_flag" |
"date_time_stamp" |
"date_time_value" |
"debugging" |
"dialog_confirm" |
"dialog_file_open" |
"dialog_file_save" |
"dialog_message" |
"dialog_prompt" |
"direct_subtype" |
"direct_subtype" |
"direct_subtype_label" |
"direct_subtype_label" |
"edge_label" |
"edge_label" |
"edge_role_binary" |
"edge_role_binary" |
"edge_role_flag" |
"edge_role_flag" |
"edge_role_node_type" |
"edge_role_node_type" |
"edge_source" |
"edge_source" |
"edge_target" |
"edge_target" |
"ensure_loaded" |
"final_state" |
"final_state" |
"final_state_set" |
"final_state_set" |
"findall" |
"float" |
"flush_output" |
"format_time" |
"functor" |
"get_byte" |
"get_char" |
"get_code" |
"get_time" |
"graph_binary" |
"graph_binary" |
"graph_edge" |
"graph_edge" |
"graph_edge_count" |
"graph_edge_count" |
"graph_edge_set" |
"graph_edge_set" |
"graph_flag" |
"graph_flag" |
"graph_node" |
"graph_node" |
"graph_node_count" |
"graph_node_count" |
"graph_node_set" |
"graph_node_set" |
"graph_node_type" |
"graph_node_type" |
"gts" |
"gts" |
"halt" |
"has_node_type" |
"has_node_type" |
"integer" |
"is_edge" |
"is_edge" |
"is_graph" |
"is_graph" |
"is_gts" |
"is_gts" |
"is_list" |
"is_node" |
"is_node" |
"is_rule" |
"is_rule" |
"is_ruleevent" |
"is_ruleevent" |
"is_rulematch" |
"is_rulematch" |
"is_state" |
"is_state" |
"is_transition" |
"is_transition" |
"is_valuenode" |
"is_valuenode" |
"java_classname" |
"java_object" |
"java_to_string" |
"label" |
"label" |
"label_edge" |
"label_edge" |
"label_edge_set" |
"label_edge_set" |
"length" |
"listing" |
"member" |
"msort" |
"nl" |
"node_edge" |
"node_edge" |
"node_edge_set" |
"node_edge_set" |
"node_number" |
"node_number" |
"node_out_edge" |
"node_out_edge" |
"node_out_edge_set" |
"node_out_edge_set" |
"node_path" |
"node_path" |
"node_self_edges" |
"node_self_edges" |
"node_self_edges_excl" |
"node_self_edges_excl" |
"node_with_attribute" |
"node_with_attribute" |
"nonvar" |
"nospy" |
"nospyall" |
"notrace" |
"number" |
"number_chars" |
"number_codes" |
"once" |
"op" |
"open" |
"parse_time" |
"peek_byte" |
"peek_char" |
"peek_code" |
"predsort" |
"put_byte" |
"put_char" |
"put_code" |
"read" |
"read_term" |
"repeat" |
"retract" |
"rule" |
"rule" |
"rule_confluent" |
"rule_confluent" |
"rule_lhs" |
"rule_lhs" |
"rule_name" |
"rule_name" |
"rule_priority" |
"rule_priority" |
"rule_rhs" |
"rule_rhs" |
"ruleevent" |
"ruleevent" |
"ruleevent_created_edge" |
"ruleevent_created_edge" |
"ruleevent_created_node" |
"ruleevent_created_node" |
"ruleevent_erased_edge" |
"ruleevent_erased_edge" |
"ruleevent_erased_node" |
"ruleevent_erased_node" |
"ruleevent_label" |
"ruleevent_label" |
"ruleevent_match" |
"ruleevent_match" |
"ruleevent_rule" |
"ruleevent_rule" |
"ruleevent_transpose" |
"ruleevent_transpose" |
"rulematch" |
"rulematch" |
"rulematch_edge" |
"rulematch_edge" |
"rulematch_node" |
"rulematch_node" |
"rulematch_rule" |
"rulematch_rule" |
"set_input" |
"set_output" |
"set_prolog_flag" |
"set_stream_position" |
"setof" |
"sort" |
"spy" |
"stacktrace" |
"stamp_date_time" |
"start_graph" |
"start_graph" |
"start_graph_name" |
"start_graph_name" |
"start_state" |
"start_state" |
"state" |
"state" |
"state_graph" |
"state_graph" |
"state_is_closed" |
"state_is_closed" |
"state_next" |
"state_next" |
"state_next_set" |
"state_next_set" |
"state_ruleevent" |
"state_ruleevent" |
"state_transition" |
"state_transition" |
"state_transition_set" |
"state_transition_set" |
"stream_property" |
"sub_atom" |
"subtype" |
"subtype" |
"subtype_label" |
"subtype_label" |
"trace" |
"tracing" |
"transition_event" |
"transition_event" |
"transition_match" |
"transition_match" |
"transition_source" |
"transition_source" |
"transition_target" |
"transition_target" |
"type_graph" |
"type_graph" |
"type_graph_name" |
"type_graph_name" |
"unify_with_occurs_check" |
"uuid" |
"uuid_compare" |
"uuid_variant" |
"uuid_version" |
"var" |
"write" |
"write_canonical" |
"write_term" |
"writeq"		{ addToken(Token.FUNCTION); }

	{LineTerminator}				{ addNullToken(); return firstToken; }

	{Identifier}					{ addToken(Token.IDENTIFIER); }

	{WhiteSpace}					{ addToken(Token.WHITESPACE); }

	/* String/Character literals. */
	{CharLiteral}				{ addToken(Token.LITERAL_CHAR); }
{UnclosedCharLiteral}		{ addToken(Token.ERROR_CHAR); addNullToken(); return firstToken; }
{ErrorCharLiteral}			{ addToken(Token.ERROR_CHAR); }
	{StringLiteral}				{ addToken(Token.LITERAL_STRING_DOUBLE_QUOTE); }
{UnclosedStringLiteral}		{ addToken(Token.ERROR_STRING_DOUBLE); addNullToken(); return firstToken; }
{ErrorStringLiteral}			{ addToken(Token.ERROR_STRING_DOUBLE); }

	/* Comment literals. */
	{MLCBegin}	{ start = zzMarkedPos-2; yybegin(MLC); }
	/* No documentation comments */
	{LineCommentBegin}			{ start = zzMarkedPos-1; yybegin(EOL_COMMENT); }

	/* Separators. */
	{Separator}					{ addToken(Token.SEPARATOR); }
	{Separator2}					{ addToken(Token.IDENTIFIER); }

	/* Operators. */
	" ==" |
"*" |
"**" |
"+" |
"-" |
"-->" |
"/" |
"//" |
"/\\" |
":-" |
"<" |
"<<" |
"=, =.." |
"=:=" |
"=<" |
"==" |
"=@=" |
"=\\=" |
">" |
">=" |
">>" |
"?-" |
"@<" |
"@=<" |
"@>" |
"@>=" |
"\\" |
"\\+" |
"\\/" |
"\\==" |
"\\=@=" |
"^"		{ addToken(Token.OPERATOR); }

	/* Numbers */
	{IntegerLiteral}				{ addToken(Token.LITERAL_NUMBER_DECIMAL_INT); }
	{HexLiteral}					{ addToken(Token.LITERAL_NUMBER_HEXADECIMAL); }
	{FloatLiteral}					{ addToken(Token.LITERAL_NUMBER_FLOAT); }
	{ErrorNumberFormat}				{ addToken(Token.ERROR_NUMBER_FORMAT); }

	/* Ended with a line not in a string or comment. */
	<<EOF>>						{ addNullToken(); return firstToken; }

	/* Catch any other (unhandled) characters. */
	.							{ addToken(Token.IDENTIFIER); }

}


/* No char state */

/* No string state */

<MLC> {

	[^hwf\n*]+				{}
	{URL}					{ int temp=zzStartRead; addToken(start,zzStartRead-1, Token.COMMENT_MULTILINE); addHyperlinkToken(temp,zzMarkedPos-1, Token.COMMENT_MULTILINE); start = zzMarkedPos; }
	[hwf]					{}

	\n						{ addToken(start,zzStartRead-1, Token.COMMENT_MULTILINE); return firstToken; }
	{MLCEnd}					{ yybegin(YYINITIAL); addToken(start,zzStartRead+2-1, Token.COMMENT_MULTILINE); }
	"*"						{}
	<<EOF>>					{ addToken(start,zzStartRead-1, Token.COMMENT_MULTILINE); return firstToken; }

}


/* No documentation comment state */

<EOL_COMMENT> {
	[^hwf\n]+				{}
	{URL}					{ int temp=zzStartRead; addToken(start,zzStartRead-1, Token.COMMENT_EOL); addHyperlinkToken(temp,zzMarkedPos-1, Token.COMMENT_EOL); start = zzMarkedPos; }
	[hwf]					{}
	\n						{ addToken(start,zzStartRead-1, Token.COMMENT_EOL); addNullToken(); return firstToken; }
	<<EOF>>					{ addToken(start,zzStartRead-1, Token.COMMENT_EOL); addNullToken(); return firstToken; }
}

