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

program : (procdef|statement)* -> ^(PROGRAM procdef* statement* );

block	: '{'! statement*  '}'!;

procdef : FUNCTION IDENTIFIER '('! ')'! block;

condition
	: conditionliteral (OR^ condition)?
	;
statement 
	: ALAP block
	| WHILE '('! condition ')'! DO block
	| DO block WHILE '('! condition ')'!	
	| TRY block ('else' block)?
	| IF '('! condition ')'! block (ELSE block)?
    	| 'choice' block (CH_OR! block)*
	| expression ';'!
    ;


conditionliteral
	: 'true' | rule ;

expression	
	: expression_atom ( (OR^ expression) | PLUS^ | STAR^)?
	| SHARP expression_atom
	;

expression_atom
	: rule
	| '('! expression ')'!
	| procuse
	; 

procuse
	: IDENTIFIER '(' ')';

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
    
