// $ANTLR 3.0.1 GCL.g 2008-07-03 14:35:49

package groove.control.parse;
import groove.control.*;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class GCLLexer extends Lexer {
    public static final int FUNCTION=7;
    public static final int STAR=19;
    public static final int SHARP=20;
    public static final int FUNCTIONS=6;
    public static final int WHILE=12;
    public static final int ELSE=16;
    public static final int DO=13;
    public static final int T29=29;
    public static final int T28=28;
    public static final int T27=27;
    public static final int NOT=25;
    public static final int ALAP=11;
    public static final int AND=22;
    public static final int Tokens=33;
    public static final int EOF=-1;
    public static final int TRY=14;
    public static final int IF=15;
    public static final int WS=26;
    public static final int COMMA=23;
    public static final int IDENTIFIER=9;
    public static final int BLOCK=5;
    public static final int OR=10;
    public static final int CH_OR=17;
    public static final int PLUS=18;
    public static final int PROGRAM=4;
    public static final int CALL=8;
    public static final int DOT=24;
    public static final int T30=30;
    public static final int CHOICE=21;
    public static final int T32=32;
    public static final int T31=31;
    public GCLLexer() {;} 
    public GCLLexer(CharStream input) {
        super(input);
    }
    public String getGrammarFileName() { return "GCL.g"; }

    // $ANTLR start T27
    public final void mT27() throws RecognitionException {
        try {
            int _type = T27;
            // GCL.g:7:5: ( '{' )
            // GCL.g:7:7: '{'
            {
            match('{'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T27

    // $ANTLR start T28
    public final void mT28() throws RecognitionException {
        try {
            int _type = T28;
            // GCL.g:8:5: ( '}' )
            // GCL.g:8:7: '}'
            {
            match('}'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T28

    // $ANTLR start T29
    public final void mT29() throws RecognitionException {
        try {
            int _type = T29;
            // GCL.g:9:5: ( '(' )
            // GCL.g:9:7: '('
            {
            match('('); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T29

    // $ANTLR start T30
    public final void mT30() throws RecognitionException {
        try {
            int _type = T30;
            // GCL.g:10:5: ( ')' )
            // GCL.g:10:7: ')'
            {
            match(')'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T30

    // $ANTLR start T31
    public final void mT31() throws RecognitionException {
        try {
            int _type = T31;
            // GCL.g:11:5: ( ';' )
            // GCL.g:11:7: ';'
            {
            match(';'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T31

    // $ANTLR start T32
    public final void mT32() throws RecognitionException {
        try {
            int _type = T32;
            // GCL.g:12:5: ( 'true' )
            // GCL.g:12:7: 'true'
            {
            match("true"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T32

    // $ANTLR start ALAP
    public final void mALAP() throws RecognitionException {
        try {
            int _type = ALAP;
            // GCL.g:70:7: ( 'alap' )
            // GCL.g:70:9: 'alap'
            {
            match("alap"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ALAP

    // $ANTLR start WHILE
    public final void mWHILE() throws RecognitionException {
        try {
            int _type = WHILE;
            // GCL.g:71:7: ( 'while' )
            // GCL.g:71:9: 'while'
            {
            match("while"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end WHILE

    // $ANTLR start DO
    public final void mDO() throws RecognitionException {
        try {
            int _type = DO;
            // GCL.g:72:4: ( 'do' )
            // GCL.g:72:6: 'do'
            {
            match("do"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DO

    // $ANTLR start IF
    public final void mIF() throws RecognitionException {
        try {
            int _type = IF;
            // GCL.g:73:4: ( 'if' )
            // GCL.g:73:6: 'if'
            {
            match("if"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end IF

    // $ANTLR start ELSE
    public final void mELSE() throws RecognitionException {
        try {
            int _type = ELSE;
            // GCL.g:74:6: ( 'else' )
            // GCL.g:74:8: 'else'
            {
            match("else"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ELSE

    // $ANTLR start CHOICE
    public final void mCHOICE() throws RecognitionException {
        try {
            int _type = CHOICE;
            // GCL.g:75:8: ( 'choice' )
            // GCL.g:75:10: 'choice'
            {
            match("choice"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end CHOICE

    // $ANTLR start CH_OR
    public final void mCH_OR() throws RecognitionException {
        try {
            int _type = CH_OR;
            // GCL.g:76:8: ( 'or' )
            // GCL.g:76:10: 'or'
            {
            match("or"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end CH_OR

    // $ANTLR start TRY
    public final void mTRY() throws RecognitionException {
        try {
            int _type = TRY;
            // GCL.g:77:5: ( 'try' )
            // GCL.g:77:7: 'try'
            {
            match("try"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TRY

    // $ANTLR start FUNCTION
    public final void mFUNCTION() throws RecognitionException {
        try {
            int _type = FUNCTION;
            // GCL.g:78:9: ( 'function' )
            // GCL.g:78:11: 'function'
            {
            match("function"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end FUNCTION

    // $ANTLR start IDENTIFIER
    public final void mIDENTIFIER() throws RecognitionException {
        try {
            int _type = IDENTIFIER;
            // GCL.g:81:13: ( ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '-' | '_' )* )
            // GCL.g:81:15: ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '-' | '_' )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }

            // GCL.g:81:35: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '-' | '_' )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0=='-'||(LA1_0>='0' && LA1_0<='9')||(LA1_0>='A' && LA1_0<='Z')||LA1_0=='_'||(LA1_0>='a' && LA1_0<='z')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // GCL.g:
            	    {
            	    if ( input.LA(1)=='-'||(input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end IDENTIFIER

    // $ANTLR start AND
    public final void mAND() throws RecognitionException {
        try {
            int _type = AND;
            // GCL.g:83:6: ( '&' )
            // GCL.g:83:9: '&'
            {
            match('&'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end AND

    // $ANTLR start COMMA
    public final void mCOMMA() throws RecognitionException {
        try {
            int _type = COMMA;
            // GCL.g:84:8: ( ',' )
            // GCL.g:84:11: ','
            {
            match(','); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end COMMA

    // $ANTLR start DOT
    public final void mDOT() throws RecognitionException {
        try {
            int _type = DOT;
            // GCL.g:85:6: ( '.' )
            // GCL.g:85:9: '.'
            {
            match('.'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DOT

    // $ANTLR start NOT
    public final void mNOT() throws RecognitionException {
        try {
            int _type = NOT;
            // GCL.g:86:6: ( '!' )
            // GCL.g:86:9: '!'
            {
            match('!'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end NOT

    // $ANTLR start OR
    public final void mOR() throws RecognitionException {
        try {
            int _type = OR;
            // GCL.g:87:5: ( '|' )
            // GCL.g:87:8: '|'
            {
            match('|'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end OR

    // $ANTLR start SHARP
    public final void mSHARP() throws RecognitionException {
        try {
            int _type = SHARP;
            // GCL.g:88:8: ( '#' )
            // GCL.g:88:11: '#'
            {
            match('#'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SHARP

    // $ANTLR start PLUS
    public final void mPLUS() throws RecognitionException {
        try {
            int _type = PLUS;
            // GCL.g:89:7: ( '+' )
            // GCL.g:89:10: '+'
            {
            match('+'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end PLUS

    // $ANTLR start STAR
    public final void mSTAR() throws RecognitionException {
        try {
            int _type = STAR;
            // GCL.g:90:7: ( '*' )
            // GCL.g:90:10: '*'
            {
            match('*'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end STAR

    // $ANTLR start WS
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            // GCL.g:92:5: ( ( ' ' | '\\t' | '\\r' | '\\n' )+ )
            // GCL.g:92:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
            {
            // GCL.g:92:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
            int cnt2=0;
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>='\t' && LA2_0<='\n')||LA2_0=='\r'||LA2_0==' ') ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // GCL.g:
            	    {
            	    if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt2 >= 1 ) break loop2;
                        EarlyExitException eee =
                            new EarlyExitException(2, input);
                        throw eee;
                }
                cnt2++;
            } while (true);

             channel=HIDDEN; 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end WS

    public void mTokens() throws RecognitionException {
        // GCL.g:1:8: ( T27 | T28 | T29 | T30 | T31 | T32 | ALAP | WHILE | DO | IF | ELSE | CHOICE | CH_OR | TRY | FUNCTION | IDENTIFIER | AND | COMMA | DOT | NOT | OR | SHARP | PLUS | STAR | WS )
        int alt3=25;
        switch ( input.LA(1) ) {
        case '{':
            {
            alt3=1;
            }
            break;
        case '}':
            {
            alt3=2;
            }
            break;
        case '(':
            {
            alt3=3;
            }
            break;
        case ')':
            {
            alt3=4;
            }
            break;
        case ';':
            {
            alt3=5;
            }
            break;
        case 't':
            {
            int LA3_6 = input.LA(2);

            if ( (LA3_6=='r') ) {
                switch ( input.LA(3) ) {
                case 'u':
                    {
                    int LA3_34 = input.LA(4);

                    if ( (LA3_34=='e') ) {
                        int LA3_44 = input.LA(5);

                        if ( (LA3_44=='-'||(LA3_44>='0' && LA3_44<='9')||(LA3_44>='A' && LA3_44<='Z')||LA3_44=='_'||(LA3_44>='a' && LA3_44<='z')) ) {
                            alt3=16;
                        }
                        else {
                            alt3=6;}
                    }
                    else {
                        alt3=16;}
                    }
                    break;
                case 'y':
                    {
                    int LA3_35 = input.LA(4);

                    if ( (LA3_35=='-'||(LA3_35>='0' && LA3_35<='9')||(LA3_35>='A' && LA3_35<='Z')||LA3_35=='_'||(LA3_35>='a' && LA3_35<='z')) ) {
                        alt3=16;
                    }
                    else {
                        alt3=14;}
                    }
                    break;
                default:
                    alt3=16;}

            }
            else {
                alt3=16;}
            }
            break;
        case 'a':
            {
            int LA3_7 = input.LA(2);

            if ( (LA3_7=='l') ) {
                int LA3_26 = input.LA(3);

                if ( (LA3_26=='a') ) {
                    int LA3_36 = input.LA(4);

                    if ( (LA3_36=='p') ) {
                        int LA3_46 = input.LA(5);

                        if ( (LA3_46=='-'||(LA3_46>='0' && LA3_46<='9')||(LA3_46>='A' && LA3_46<='Z')||LA3_46=='_'||(LA3_46>='a' && LA3_46<='z')) ) {
                            alt3=16;
                        }
                        else {
                            alt3=7;}
                    }
                    else {
                        alt3=16;}
                }
                else {
                    alt3=16;}
            }
            else {
                alt3=16;}
            }
            break;
        case 'w':
            {
            int LA3_8 = input.LA(2);

            if ( (LA3_8=='h') ) {
                int LA3_27 = input.LA(3);

                if ( (LA3_27=='i') ) {
                    int LA3_37 = input.LA(4);

                    if ( (LA3_37=='l') ) {
                        int LA3_47 = input.LA(5);

                        if ( (LA3_47=='e') ) {
                            int LA3_53 = input.LA(6);

                            if ( (LA3_53=='-'||(LA3_53>='0' && LA3_53<='9')||(LA3_53>='A' && LA3_53<='Z')||LA3_53=='_'||(LA3_53>='a' && LA3_53<='z')) ) {
                                alt3=16;
                            }
                            else {
                                alt3=8;}
                        }
                        else {
                            alt3=16;}
                    }
                    else {
                        alt3=16;}
                }
                else {
                    alt3=16;}
            }
            else {
                alt3=16;}
            }
            break;
        case 'd':
            {
            int LA3_9 = input.LA(2);

            if ( (LA3_9=='o') ) {
                int LA3_28 = input.LA(3);

                if ( (LA3_28=='-'||(LA3_28>='0' && LA3_28<='9')||(LA3_28>='A' && LA3_28<='Z')||LA3_28=='_'||(LA3_28>='a' && LA3_28<='z')) ) {
                    alt3=16;
                }
                else {
                    alt3=9;}
            }
            else {
                alt3=16;}
            }
            break;
        case 'i':
            {
            int LA3_10 = input.LA(2);

            if ( (LA3_10=='f') ) {
                int LA3_29 = input.LA(3);

                if ( (LA3_29=='-'||(LA3_29>='0' && LA3_29<='9')||(LA3_29>='A' && LA3_29<='Z')||LA3_29=='_'||(LA3_29>='a' && LA3_29<='z')) ) {
                    alt3=16;
                }
                else {
                    alt3=10;}
            }
            else {
                alt3=16;}
            }
            break;
        case 'e':
            {
            int LA3_11 = input.LA(2);

            if ( (LA3_11=='l') ) {
                int LA3_30 = input.LA(3);

                if ( (LA3_30=='s') ) {
                    int LA3_40 = input.LA(4);

                    if ( (LA3_40=='e') ) {
                        int LA3_48 = input.LA(5);

                        if ( (LA3_48=='-'||(LA3_48>='0' && LA3_48<='9')||(LA3_48>='A' && LA3_48<='Z')||LA3_48=='_'||(LA3_48>='a' && LA3_48<='z')) ) {
                            alt3=16;
                        }
                        else {
                            alt3=11;}
                    }
                    else {
                        alt3=16;}
                }
                else {
                    alt3=16;}
            }
            else {
                alt3=16;}
            }
            break;
        case 'c':
            {
            int LA3_12 = input.LA(2);

            if ( (LA3_12=='h') ) {
                int LA3_31 = input.LA(3);

                if ( (LA3_31=='o') ) {
                    int LA3_41 = input.LA(4);

                    if ( (LA3_41=='i') ) {
                        int LA3_49 = input.LA(5);

                        if ( (LA3_49=='c') ) {
                            int LA3_55 = input.LA(6);

                            if ( (LA3_55=='e') ) {
                                int LA3_58 = input.LA(7);

                                if ( (LA3_58=='-'||(LA3_58>='0' && LA3_58<='9')||(LA3_58>='A' && LA3_58<='Z')||LA3_58=='_'||(LA3_58>='a' && LA3_58<='z')) ) {
                                    alt3=16;
                                }
                                else {
                                    alt3=12;}
                            }
                            else {
                                alt3=16;}
                        }
                        else {
                            alt3=16;}
                    }
                    else {
                        alt3=16;}
                }
                else {
                    alt3=16;}
            }
            else {
                alt3=16;}
            }
            break;
        case 'o':
            {
            int LA3_13 = input.LA(2);

            if ( (LA3_13=='r') ) {
                int LA3_32 = input.LA(3);

                if ( (LA3_32=='-'||(LA3_32>='0' && LA3_32<='9')||(LA3_32>='A' && LA3_32<='Z')||LA3_32=='_'||(LA3_32>='a' && LA3_32<='z')) ) {
                    alt3=16;
                }
                else {
                    alt3=13;}
            }
            else {
                alt3=16;}
            }
            break;
        case 'f':
            {
            int LA3_14 = input.LA(2);

            if ( (LA3_14=='u') ) {
                int LA3_33 = input.LA(3);

                if ( (LA3_33=='n') ) {
                    int LA3_43 = input.LA(4);

                    if ( (LA3_43=='c') ) {
                        int LA3_50 = input.LA(5);

                        if ( (LA3_50=='t') ) {
                            int LA3_56 = input.LA(6);

                            if ( (LA3_56=='i') ) {
                                int LA3_59 = input.LA(7);

                                if ( (LA3_59=='o') ) {
                                    int LA3_61 = input.LA(8);

                                    if ( (LA3_61=='n') ) {
                                        int LA3_62 = input.LA(9);

                                        if ( (LA3_62=='-'||(LA3_62>='0' && LA3_62<='9')||(LA3_62>='A' && LA3_62<='Z')||LA3_62=='_'||(LA3_62>='a' && LA3_62<='z')) ) {
                                            alt3=16;
                                        }
                                        else {
                                            alt3=15;}
                                    }
                                    else {
                                        alt3=16;}
                                }
                                else {
                                    alt3=16;}
                            }
                            else {
                                alt3=16;}
                        }
                        else {
                            alt3=16;}
                    }
                    else {
                        alt3=16;}
                }
                else {
                    alt3=16;}
            }
            else {
                alt3=16;}
            }
            break;
        case 'A':
        case 'B':
        case 'C':
        case 'D':
        case 'E':
        case 'F':
        case 'G':
        case 'H':
        case 'I':
        case 'J':
        case 'K':
        case 'L':
        case 'M':
        case 'N':
        case 'O':
        case 'P':
        case 'Q':
        case 'R':
        case 'S':
        case 'T':
        case 'U':
        case 'V':
        case 'W':
        case 'X':
        case 'Y':
        case 'Z':
        case 'b':
        case 'g':
        case 'h':
        case 'j':
        case 'k':
        case 'l':
        case 'm':
        case 'n':
        case 'p':
        case 'q':
        case 'r':
        case 's':
        case 'u':
        case 'v':
        case 'x':
        case 'y':
        case 'z':
            {
            alt3=16;
            }
            break;
        case '&':
            {
            alt3=17;
            }
            break;
        case ',':
            {
            alt3=18;
            }
            break;
        case '.':
            {
            alt3=19;
            }
            break;
        case '!':
            {
            alt3=20;
            }
            break;
        case '|':
            {
            alt3=21;
            }
            break;
        case '#':
            {
            alt3=22;
            }
            break;
        case '+':
            {
            alt3=23;
            }
            break;
        case '*':
            {
            alt3=24;
            }
            break;
        case '\t':
        case '\n':
        case '\r':
        case ' ':
            {
            alt3=25;
            }
            break;
        default:
            NoViableAltException nvae =
                new NoViableAltException("1:1: Tokens : ( T27 | T28 | T29 | T30 | T31 | T32 | ALAP | WHILE | DO | IF | ELSE | CHOICE | CH_OR | TRY | FUNCTION | IDENTIFIER | AND | COMMA | DOT | NOT | OR | SHARP | PLUS | STAR | WS );", 3, 0, input);

            throw nvae;
        }

        switch (alt3) {
            case 1 :
                // GCL.g:1:10: T27
                {
                mT27(); 

                }
                break;
            case 2 :
                // GCL.g:1:14: T28
                {
                mT28(); 

                }
                break;
            case 3 :
                // GCL.g:1:18: T29
                {
                mT29(); 

                }
                break;
            case 4 :
                // GCL.g:1:22: T30
                {
                mT30(); 

                }
                break;
            case 5 :
                // GCL.g:1:26: T31
                {
                mT31(); 

                }
                break;
            case 6 :
                // GCL.g:1:30: T32
                {
                mT32(); 

                }
                break;
            case 7 :
                // GCL.g:1:34: ALAP
                {
                mALAP(); 

                }
                break;
            case 8 :
                // GCL.g:1:39: WHILE
                {
                mWHILE(); 

                }
                break;
            case 9 :
                // GCL.g:1:45: DO
                {
                mDO(); 

                }
                break;
            case 10 :
                // GCL.g:1:48: IF
                {
                mIF(); 

                }
                break;
            case 11 :
                // GCL.g:1:51: ELSE
                {
                mELSE(); 

                }
                break;
            case 12 :
                // GCL.g:1:56: CHOICE
                {
                mCHOICE(); 

                }
                break;
            case 13 :
                // GCL.g:1:63: CH_OR
                {
                mCH_OR(); 

                }
                break;
            case 14 :
                // GCL.g:1:69: TRY
                {
                mTRY(); 

                }
                break;
            case 15 :
                // GCL.g:1:73: FUNCTION
                {
                mFUNCTION(); 

                }
                break;
            case 16 :
                // GCL.g:1:82: IDENTIFIER
                {
                mIDENTIFIER(); 

                }
                break;
            case 17 :
                // GCL.g:1:93: AND
                {
                mAND(); 

                }
                break;
            case 18 :
                // GCL.g:1:97: COMMA
                {
                mCOMMA(); 

                }
                break;
            case 19 :
                // GCL.g:1:103: DOT
                {
                mDOT(); 

                }
                break;
            case 20 :
                // GCL.g:1:107: NOT
                {
                mNOT(); 

                }
                break;
            case 21 :
                // GCL.g:1:111: OR
                {
                mOR(); 

                }
                break;
            case 22 :
                // GCL.g:1:114: SHARP
                {
                mSHARP(); 

                }
                break;
            case 23 :
                // GCL.g:1:120: PLUS
                {
                mPLUS(); 

                }
                break;
            case 24 :
                // GCL.g:1:125: STAR
                {
                mSTAR(); 

                }
                break;
            case 25 :
                // GCL.g:1:130: WS
                {
                mWS(); 

                }
                break;

        }

    }


 

}