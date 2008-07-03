lexer grammar GCL;
@header {
package groove.control.parse;
import groove.control.*;
}

T27 : '{' ;
T28 : '}' ;
T29 : '(' ;
T30 : ')' ;
T31 : ';' ;
T32 : 'true' ;

// $ANTLR src "GCL.g" 70
ALAP 	:	'alap';
// $ANTLR src "GCL.g" 71
WHILE	:	'while';
// $ANTLR src "GCL.g" 72
DO	:	'do';
// $ANTLR src "GCL.g" 73
IF	:	'if';
// $ANTLR src "GCL.g" 74
ELSE	:	'else';
// $ANTLR src "GCL.g" 75
CHOICE	:	'choice';
// $ANTLR src "GCL.g" 76
CH_OR 	:	'or';
// $ANTLR src "GCL.g" 77
TRY	:	'try';
// $ANTLR src "GCL.g" 78
FUNCTION:	'function';


// $ANTLR src "GCL.g" 81
IDENTIFIER 	: ('a'..'z'|'A'..'Z') ('a'..'z'|'A'..'Z'|'0'..'9'|'-'|'_')*;

// $ANTLR src "GCL.g" 83
AND 	:	 '&';
// $ANTLR src "GCL.g" 84
COMMA 	:	 ',' ;
// $ANTLR src "GCL.g" 85
DOT 	:	 '.' ;
// $ANTLR src "GCL.g" 86
NOT 	:	 '!';
// $ANTLR src "GCL.g" 87
OR 	:	 '|';
// $ANTLR src "GCL.g" 88
SHARP 	:	 '#' ;
// $ANTLR src "GCL.g" 89
PLUS 	:	 '+' ;
// $ANTLR src "GCL.g" 90
STAR 	:	 '*' ;

// $ANTLR src "GCL.g" 92
WS  :   (   ' '
        |   '\t'
        |   '\r'
        |   '\n'
        )+
        { $channel=HIDDEN; }
    ;    
    
