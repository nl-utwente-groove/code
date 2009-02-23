grammar GCL;

options {
	output=AST;
	k=2;
}

tokens {
	PROGRAM;
	BLOCK;
	FUNCTIONS;
	FUNCTION;
	CALL;
	DO;
}

@lexer::header {
package groove.control.parse;
import groove.control.*;
import java.util.LinkedList;
}

@header {
package groove.control.parse;
import groove.control.*;
import java.util.LinkedList;

}


@members {
    private List<String> errors = new LinkedList<String>();
    public void displayRecognitionError(String[] tokenNames,
                                        RecognitionException e) {
        String hdr = getErrorHeader(e);
        String msg = getErrorMessage(e, tokenNames);
        errors.add(hdr + " " + msg);
    }
    public List<String> getErrors() {
        return errors;
    }
}

// PARSER rules

program : (function|statement)* -> ^(PROGRAM ^(FUNCTIONS function*) ^(BLOCK statement*));

block	: '{' statement*  '}' -> ^(BLOCK statement*);

function : FUNCTION IDENTIFIER '(' ')' block -> ^(FUNCTION IDENTIFIER block);

condition
	: conditionliteral (OR^ condition)?
	;
	
statement 
	: ALAP block -> ^(ALAP block)
	| WHILE '(' condition ')' DO? block -> ^(WHILE condition block)
	| UNTIL '(' condition ')' DO? block -> ^(UNTIL condition block)
	| DO block WHILE '(' condition ')' -> ^(DO block condition)
	| TRY block (ELSE block)? -> ^(TRY block+)
	| IF '(' condition ')' block (ELSE block)? -> ^(IF condition block+)
    | CHOICE block (CH_OR block)* -> ^(CHOICE block+)
	| expression ';' -> expression
    ;

conditionliteral
	: TRUE | rule ;

expression	
	: expression2 (OR^ expression)?
	;

expression2
    : expression_atom (PLUS^ | STAR^)?
    | SHARP^ expression_atom
    ;

expression_atom
	: rule
	| ANY
	| OTHER
	| '('! expression ')'!
	| call
	; 

call
	: IDENTIFIER '(' ')' -> ^(CALL IDENTIFIER);

rule 	: IDENTIFIER;

// LEXER rules

ALAP 	:	'alap';
WHILE	:	'while';
DO		:	'do';
UNTIL   :	'until';
IF		:	'if';
ELSE	:	'else';
CHOICE	:	'choice';
CH_OR 	:	'or';
TRY		:	'try';
FUNCTION:	'function';
TRUE	:	'true';
OTHER	:	'other';
ANY		:	'any';


IDENTIFIER 	: ('a'..'z'|'A'..'Z') ('a'..'z'|'A'..'Z'|'0'..'9'|'-'|'_')*;

AND 	:	 '&';
COMMA 	:	 ',' ;
DOT 	:	 '.' ;
NOT 	:	 '!';
OR 	:	 '|';
SHARP 	:	 '#' ;
PLUS 	:	 '+' ;
STAR 	:	 '*' ;

ML_COMMENT : '/*' ( options {greedy=false;} : . )* '*/' { $channel=HIDDEN; };
SL_COMMENT : '//' ( options {greedy=false;} : . )* '\n' { $channel=HIDDEN; };

WS  :   (   ' '
        |   '\t'
        |   '\r'
        |   '\n'
        )+
        { $channel=HIDDEN; }
    ;    
    
