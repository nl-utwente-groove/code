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
}

@lexer::header {
package groove.control.parse;
import groove.control.*;
}

@header {
package groove.control.parse;
import groove.control.*;
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
	| WHILE '(' condition ')' DO block -> ^(WHILE condition block)
	| DO block WHILE '(' condition ')' -> ^(DO block condition) 	
	| TRY block (ELSE block)? -> ^(TRY block+)
	| IF '(' condition ')' block (ELSE block)? -> ^(IF condition block+)
    | CHOICE block (CH_OR block)* -> ^(CHOICE block+)
	| expression ';' -> expression
    ;


conditionliteral
	: 'true' | rule ;

expression	
	: expression2 (OR^ expression)?
	;

expression2
    : expression_atom (PLUS^ | STAR^)?
    | SHARP^ expression_atom
    ;

expression_atom
	: rule
	| '('! expression ')'!
	| call
	; 

call
	: IDENTIFIER '(' ')' -> ^(CALL IDENTIFIER);

rule 	: IDENTIFIER;

// LEXER rules

ALAP 	:	'alap';
WHILE	:	'while';
DO	:	'do';
IF	:	'if';
ELSE	:	'else';
CHOICE	:	'choice';
CH_OR 	:	'or';
TRY	:	'try';
FUNCTION:	'function';


IDENTIFIER 	: ('a'..'z'|'A'..'Z') ('a'..'z'|'A'..'Z'|'0'..'9'|'-'|'_')*;

AND 	:	 '&';
COMMA 	:	 ',' ;
DOT 	:	 '.' ;
NOT 	:	 '!';
OR 	:	 '|';
SHARP 	:	 '#' ;
PLUS 	:	 '+' ;
STAR 	:	 '*' ;

WS  :   (   ' '
        |   '\t'
        |   '\r'
        |   '\n'
        )+
        { $channel=HIDDEN; }
    ;    
    
