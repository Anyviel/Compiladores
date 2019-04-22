import java.io.BufferedReader;

public class LexicalAnalyzer {
    private BufferedReader br;
    private SymbolTable table;
    private boolean EOF;
    private char actualChar;
    private String lexema = "";
    private final int FINAL_STATE = 99;
    private final int ERROR = -99;
    private boolean returnValue;

    LexicalAnalyzer(SymbolTable table, BufferedReader br) {
        this.table = table;
        this.br = br;
        this.EOF = false;
    }

    private void readChar() {
        try {
            if (returnValue) {
                returnValue = false;
            } else {
                actualChar = (char) br.read();

                // ignora \r\n
                if (actualChar == '\r') {
                    actualChar = (char) br.read();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    Symbol analyze() throws Exception {
        lexema = "";
        int state = 0;
        while (state != FINAL_STATE) {
            if (actualChar != '\n' && actualChar != ' ' && actualChar != '\0') {
                lexema += actualChar;
            }

            switch (state) {
                case 0: state = state0(); break; //
                case 1: state = state1(); break;
                case 2: state = state2(); break;
                case 3: state = state3(); break;
                case 4: state = state4(); break; // ID
                case 5: state = state5(); break; // ID
                case 6: state = state6(); break; // DIGITO
                case 7: state = state7(); break; // nao usado
                case 8: state = state8(); break; // HEXA
                case 9: state = state9(); break; // HEXA
                case 10: state = state10(); break;
                case 11: state = state11(); break; // >
                case 12: state = state12(); break; // <
                case 13: state = state13(); break; // =
                case ERROR: error();
            }
        }



        Symbol symbol = table.searchLexeme(lexema);

        if (symbol == null) {
            symbol = table.insertId(lexema);
        }

        return symbol;
    }

    private void error() throws Exception {
        throw new Exception("TOKEN NÃO EXPERADO: " + lexema);
    }


    private int state0() {
        if (!returnValue) {
            readChar();
        } else {
            returnValue = false;
        }

        // inicio de comentario
        if (actualChar == '/') {
            return 1;
        }

        // pular espacos vazios
        while (actualChar == ' ' || actualChar == '\n') {
            readChar();
        }

        if (actualChar == '_') {
            return 5;
        }

        if (actualChar == '0') {
            return  8;
        } else if (isDigit(actualChar)) {
            return 6;
        }

        if (actualChar == '>') {
            return 11;
        }

        if (actualChar == '<') {
            return 12;
        }

        if (isSpecial(actualChar)) {
            return FINAL_STATE;
        }


        if (isLetter(actualChar)) {
            return 4;
        }

        return ERROR;
    }

    /**
     * identifica inicio de comentario
     */
    private int state1() {
        readChar();
        if (actualChar == '*') {
            return 2;
        }
        return ERROR;
    }

    /**
     * identifica fechamento de comentario
     */
    private int state2() {
        readChar();
        while (actualChar != '*') {
            readChar();
        }
        return 3;
    }

    /**
     * fecha o comentario
     */
    private int state3() {
        readChar();
        if (actualChar == '/') {
            return 0;
        } else {
            // encontrou algo diferente de / e nao fechou o comentario
            return 2;
        }
    }

    private int state4() {
        readChar();
        if (isLetter(actualChar)
            || isDigit(actualChar)
            || actualChar == '_') {
            return 4;
        } else {
            this.returnValue = true;
            return FINAL_STATE;
        }
    }

    private int state5() {
        if (isLetter(actualChar)
                || isDigit(actualChar)
                || actualChar == '_') {
            return 4;
        } else {
            this.returnValue = true;
            return FINAL_STATE;
        }
    }

    /**
     * números
     */
    private int state6() {
        readChar();
        if (isDigit(actualChar)) {
            return 6;
        }

        return ERROR;
    }

    // nao usado
    private short state7() {
        return 0;
    }

    private int state8() {
        readChar();
        if (isDigit(actualChar)) {
            return 6;
        } else if (actualChar == 'x' || actualChar == 'X') {
            return 9;
        }
        return ERROR;
    }

    private int state9() {
        readChar();
        if (isDigit(actualChar) || isHexa(actualChar)) {
            return 10;
        }
        return ERROR;
    }

    private int state10() {
        readChar();
        if (isDigit(actualChar) || isHexa(actualChar)) {
            return FINAL_STATE;
        }
        return ERROR;
    }

    private int state11() {
        readChar();
        if (actualChar == '=') {
            return FINAL_STATE;
        } else {
            return FINAL_STATE;
        }
    }

    private int state12() {
        readChar();
        if (actualChar == '>' || actualChar == '=') {
            return FINAL_STATE;
        } else  {
            return FINAL_STATE;
        }
    }


    private int state13() {
        readChar();
        if (actualChar == '=') {
            return  FINAL_STATE;
        } else {
            return FINAL_STATE;
        }
    }

    public boolean isEOF() {
        return EOF;
    }

    public void setEOF(boolean EOF) {
        this.EOF = EOF;
    }

    private boolean isLetter(char symbol) {
        return (""+symbol).matches("[a-zA-Z]");
    }
    private boolean isDigit(char symbol) {
        return (""+symbol).matches("[0-9]");
    }

    private boolean isHexa(char symbol) {
        return (""+symbol).matches("[a-fA-F]");
    }

    private boolean isSpecial(char symbol) {
        return symbol == '(' || symbol == ')' || symbol == '[' || symbol == ']' || symbol == '{' || symbol == '}' || symbol == ',' || symbol == ';';
    }

}
