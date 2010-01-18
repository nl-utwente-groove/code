lexer grammar Config;

@header {
package groove.explore.chscenar.parser;
import groove.explore.chscenar.*;
}

T__7 : 'RULE' ;
T__8 : '::ALLOW' ;
T__9 : '::DENY' ;
T__10 : ':STRATEGY' ;
T__11 : ':RESULT' ;
T__12 : ':ACCEPTOR' ;
T__13 : ',' ;
T__14 : '.' ;

// $ANTLR src "../../src/groove/explore/chscenar/parser/Config.g" 136
ID  :	('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')* ;
// $ANTLR src "../../src/groove/explore/chscenar/parser/Config.g" 137
WS  :   (' '|'\t'| '\r'? '\n')+ {skip();} ;
// $ANTLR src "../../src/groove/explore/chscenar/parser/Config.g" 138
LINE_COMMENT
    : '//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
    ;
