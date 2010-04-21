grammar labels;

options {
  language = Java;
  output = AST;
}

label :
  graphLabel | ruleLabel ;

graphLabel :
  prefix* actualGraphLabel;
 
prefix
 : (char\{COLON,EQUALS})+ [ EQUALS (char\{COLON})* ] COLON ;

actualGraphLabel
   : COLON char*
   | REM COLON Char*
   | valueLabel
   | nodeLabel
   | (Char\{COLON})+ ;
   
valueLabel
   : INT COLON DIGIT+
   | REAL COLON (DIGIT+ [DOT DIGIT*] | DOT DIGIT+)
   | STRING COLON dQuotedText
   | BOOL COLON (TRUE | FALSE) ;
   
nodeLabel
   : TYPE COLON Ident
   | FLAG COLON Ident

ruleLabel
   : prefix* actualRuleLabel ;
   
actualRuleLabel
   : COLON Char*
   | REM COLON Char*
   | PAR COLON
   | PATH COLON [PLING] RegExpr
   | valueLabel
   | nodeLabel
   | attrLabel
   | negLabel
   | posLabel ;
   
attrLabel
   : INT COLON [Ident]
   | REAL COLON [Ident]
   | STRING COLON [Ident]
   | BOOL COLON [Ident]
   | ATTR COLON
   | PROD COLON
   | ARG COLON DIGIT+ ;

negLabel
   : PLING posLabel ;
   
posLabel
   : wildcard
   | EQUALS
   | LCURLY regExpr RCURLY
   | SQUOTE sQuotedText SQUOTE
   | (Char\{SQUOTE,LCURLY,RCURLY,BSLASH,COLON})* ;
   
regExpr
   : wildcard
   | EQUALS
   | atom
   | sequence
   | choice
   | star
   | plus
   | inverse
   | LPAR regExpr RPAR ;

wildcard
   : QUERY [IDENT] [constraint] ;

constraint
   : LSQUARE [HAT] atom (COMMA atom)* RSQUARE ;

sequence
   : regExpr (DOT regExpr)+ ;

choice
   : regExpr (BAR regExpr)+ ;

star
   : regExpr STAR ;

plus
   : regExpr PLUS ;

inverse
   : MINUS regExpr ;

atom
   : sQuotedText
   | identChar* ;

sQuotedText
   : SQUOTE (char\{SQUOTE,BSLASH} | BSLASH (BSLASH|SQUOTE))* SQUOTE ;

dQuotedText
   : DQUOTE (char\{DQUOTE,BSLASH} | BSLASH (BSLASH|DQUOTE))* DQUOTE ;

ident
   : LETTER identChar* ;

identChar
   : LETTER | DIGIT | DOLLAR | UNDER;

NEW    : 'new';
DEL    : 'del';
CNEW   : 'cnew';
NOT    : 'not';
USE    : 'use';
REM    : 'rem';

FORALL : 'forall';
FORALLX : 'forallx';
EXISTS : 'exists';
NESTED : 'nested';

PAR : 'par';

ATTR   : 'attr';
PROD   : 'prod';
ARG    : 'arg';
INT    : 'int';
REAL   : 'real';
STRING : 'string';
BOOL   : 'bool';

TYPE   : 'type';
FLAG   : 'flag';
PATH   : 'path';

TRUE   : 'true';
FALSE  : 'false';

IDENTIFIER : LETTER (LETTER | DIGIT | DOLLAR | UNDER )* ;

MINUS  : '-';
STAR   : '*';
PLUS   : '+';
DOT    : '.';
BAR    : '|';
HAT    : '^';
EQUALS : '=';
LBRACE : '{';
RBRACE : '}';
LPAR   : '(';
RPAR   : ')';
LSQUARE : '[';
RSQUARE : ']';
PLING  : '!';
QUERY  : '?'; 
LETTER : 'a'..'z'|'A'..'Z' ;
DIGIT  : '0'..'9' ;
COLON  : ':' ;
COMMA  : ',' ;
SQUOTE : '\'' ;
DQUOTE : '"' ;
DOLLAR : '$';
UNDER  : '_';
BSLASH : '\\' ;