///////////////////////////////////////////////////////////////////////////
// Parser for gcl files (groove control language)
///////////////////////////////////////////////////////////////////////////

header {
package groove.control.parse;
import groove.control.*;
}

class GCLParser extends Parser;

options {
        buildAST = true;
        k = 2;
		defaultErrorHandler = false;
}

tokens {
	ALAP = "alap";
	WHILE = "while";
	TRY = "try";
	ELSE = "else";
	DO	 = "do";
	IF = "if";
	CHOICE = "choice";
	OR = "or";
	PROC = "proc";  // not sure if i need a keyword for proc declarations
	PROCUSE;
	PROGRAM;
	BLOCK;
	TRUE = "true";
}

program
	: proclist EOF!
	{ #program = #([PROGRAM,"program"], #program); }
	;

block
	: LCURLY! statements  RCURLY!
	{ #block = #([BLOCK,"block"],#block); }
	;

proclist
	: procdef (proclist)?
	;

procdef
	: p:PROC^ i:IDENTIFIER LPAREN! RPAREN! block
	;

statements
	: statement (statements)?;

statement
	: ALAP^ block
	| WHILE^ LPAREN! condition RPAREN! DO! block
	| DO^ block WHILE! condition
    | TRY^ block (ELSE! block)?
	| IF^ LPAREN! condition RPAREN!
    | CHOICE^ block (OR! block)*
    | expression SEMICOLON!
    ;

condition
	: conditionliteral (OR^ condition)?
	;

conditionliteral
	: TRUE
	| rule
	;

expression	
	: expression_atom ((OR^ expression) | PLUS^ | STAR^)?
	| SHARP^ expression_atom
	;

expression_atom
	: rule
	| LPAREN! expression RPAREN!
	| procuse
	; 

procuse
	: IDENTIFIER LPAREN! RPAREN!
	{ #procuse = #([PROCUSE,"procuse"],#procuse); }
	;

rule
	: IDENTIFIER
	;

///////////////////////////////////////////////////////////////////////////
// Some scope operations and checks
///////////////////////////////////////////////////////////////////////////

class GCLChecker extends TreeParser;
options {
	buildAST = false;
	importVocab = GCLParser;
}

{
	private ControlAutomaton aut;
	
	public GCLChecker(ControlAutomaton ca) {
		this.aut = ca;
	}
	
    private Namespace namespace;
	public void setNamespace(Namespace namespace) {
		this.namespace = namespace;
	}
}

program 
  :  #(PROGRAM (proc)*)
  ;

proc
  : 
  #(p:PROC o:IDENTIFIER block)
  { namespace.store(o.getText(), p); }
  ;
  
block
  : #(BLOCK (statement)*)
  ;

statement
  : #(ALAP block)
  | #(WHILE condition block)
  | #(DO block condition)
  | #(TRY block (block)?)
  | #(IF condition block (block)?)
  | #(CHOICE (block)+)
  | expression
  ;

expression	
	: #(OR expression expression)
	| #(PLUS expression)
	| #(STAR expression)
	| #(SHARP expression)
	| #(PROCUSE i:IDENTIFIER)
	| rule
	; 

condition
  : #(OR condition condition)
  | rule
  | TRUE
  ;

rule
  : IDENTIFIER
  ;

///////////////////////////////////////////////////////////////////////////
// Lexer for Cps files
///////////////////////////////////////////////////////////////////////////

class GCLLexer extends Lexer;
options {
        k = 2;
   		testLiterals=false;
        charVocabulary = '\3'..'\377'; // just handle ASCII not Unicode
}

AND                     : '&';
COMMA                   : ',' ;
DOT                     : '.' ;
LCURLY                  : '{' ;
LPAREN		            : '(';
NOT                     : '!';
OR                      : '|';
RCURLY                  : '}' ;
RPAREN                  : ')';
RSQUARE                 : ']' ;
SHARP					: '#' ;
SEMICOLON               : ';' ;
PLUS					: '+' ;
STAR					: '*' ;

protected DIGIT         : '0'..'9' ;
protected LETTER        : 'a'..'z'|'A'..'Z' ;
protected NEWLINE       : (("\r\n") => "\r\n"           //DOS
                          | '\r'                        //Macintosh
                          | '\n'){newline();};          //Unix

// NEWLINE and WS.... you can combine those
WS                      : (NEWLINE) => NEWLINE { /*newline();*/ $setType(Token.SKIP);}
                          | (' ' | '\t' | '\f') { $setType(Token.SKIP); } ;

protected SPECIAL       : '_' | '-';
    
IDENTIFIER options {testLiterals=true;}
	: (LETTER | SPECIAL) (LETTER | DIGIT | SPECIAL)*;
