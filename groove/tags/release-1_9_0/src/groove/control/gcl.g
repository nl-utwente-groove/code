///////////////////////////////////////////////////////////////////////////
// Parser for gcl files (groove control language)
///////////////////////////////////////////////////////////////////////////

header {
package groove.control;
}

class GCLParser extends Parser;

options {
        buildAST = true;
        k = 2;
		defaultErrorHandler = false;
}

tokens {
	ALAP = "alap";
	UNTIL = "until";
	TRY = "try";
	ELSE = "else";
	DO	 = "do";
	PROGRAM;
}

program
	: seq EOF!
	{ #program = #([PROGRAM,"program"], #program); };

body
	: LCURLY! seq RCURLY!;
	
expression	
	: seq;

basic
	: seq;

seq
	: or (SEMICOLON^ seq)?;
	
or
	: complex (OR^ or)?;

complex
    : ALAP^ atom
    | DO^ atom UNTIL! atom
    | TRY^ atom (options{greedy=true;}: ELSE atom)?
    | atom;

atom
	: IDENTIFIER
	| LPAREN! seq RPAREN!;


///////////////////////////////////////////////////////////////////////////
// Automaton Builder for gcl files (groove control language)
///////////////////////////////////////////////////////////////////////////

class GCLBuilder extends TreeParser;
options {
        buildAST = false;
		importVocab = GCLParser;
}

{
	private ControlAutomaton aut;
	
	public GCLBuilder(ControlAutomaton ca) {
		this.aut = ca;
	}
}

program { ControlState[] states; } 
	: 
	#(PROGRAM states=expression)
	{
		this.aut.setStartState(states[0]);
		this.aut.addFinalState(states[1]);
	}
	;

expression returns [ControlState[] states] { states = new ControlState[2]; ControlState[] first; ControlState[] second;}
	: #(SEMICOLON first=expression second=expression)
	{
		states[0] = first[0];
		states[1] = second[1];
		aut.addTransition(first[1],second[0], Control.LAMBDA_LABEL);
	}
	
	| #(OR first=expression second=expression)
	{
		states = first;
		aut.addTransition(states[0],second[0], Control.LAMBDA_LABEL);
		aut.addTransition(second[1],states[1], Control.LAMBDA_LABEL);		
	}	
	| #(ALAP first=expression)
	{
		states[0] = first[0];
		states[1] = aut.newState();
		aut.addTransition(states[0],states[1], Control.ELSE_LABEL);
		aut.addTransition(first[1], first[0], Control.LAMBDA_LABEL);
	}	
	| #(DO first=expression second=expression)
	{
		states = second;
		aut.addTransition(states[0], first[0], Control.ELSE_LABEL);
		aut.addTransition(first[1], states[0], Control.LAMBDA_LABEL);
	}
	| rule:IDENTIFIER
	{
		states[0] = aut.newState();
		states[1] = aut.newState();
		aut.addTransition(states[0],states[1], rule.toString() );
	}
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
SEMICOLON               : ';' ;

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
