import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

class Parser {

    private Symbol symbol;
    private LexicalAnalyzer lexicalAnalyzer;

    Parser(String filename, String output) throws Exception {
        SymbolTable symbolTable = new SymbolTable();

        BufferedReader br = new BufferedReader(new FileReader(filename));

        lexicalAnalyzer = new LexicalAnalyzer(symbolTable, br);

        symbol = lexicalAnalyzer.analyze();

        symbolTable.print();
    }

    private void match(Alphabet expextedToken) throws Exception {
        if (symbol.equals(expextedToken)) {
            symbol = lexicalAnalyzer.analyze();
        } else if (lexicalAnalyzer.isEOF()) {
            throw new Exception("FIM DE ARQUIVO NÃO EXPERADO");
        } else {
            throw new Exception("TOKEN NÃO EXPERADO: " + symbol.getToken());
        }
    }

    /**
     * S -> { D }* { BLOCO }*
     */
    void S() throws Exception {
        while (symbol.equals(Alphabet.VAR)
            || symbol.equals(Alphabet.CONST)) {
            D();
        }

        while (symbol.equals(Alphabet.OPEN_KEYS)) {
            BLOCO();
        }
    }

    /**
     * D -> var { DTIPO }+ | const id = [ - | + ] valor;
     */
    private void D() throws Exception {
        if(symbol.equals(Alphabet.VAR)) {
            match(Alphabet.VAR);

            do {
                DTIPO();
            } while (
                    symbol.getToken().equals(Alphabet.CHAR.getToken())
                    || symbol.getToken().equals(Alphabet.CHAR.getToken())
            );
        }
    }

    /**
     * DTIPO -> (“integer” | “char”) id [ DVALOR ] { ,id [ DVALOR ] }*
     */
    private void DTIPO() throws Exception {
        if(symbol.equals(Alphabet.INTEGER)) {
            match(Alphabet.INTEGER);
        } else {
            match(Alphabet.CHAR);
        }

        match(Alphabet.ID);

        if(symbol.equals(Alphabet.ATTR)) {
            DVAL();
        } else if (symbol.equals(Alphabet.SEMICOLON)) {
            match(Alphabet.SEMICOLON);
            DTIPO();
        } else {
            while (symbol.equals(Alphabet.COMMA)) {
                match(Alphabet.COMMA);

                match(Alphabet.ID);

                if(symbol.equals(Alphabet.PLUS)
                        || symbol.equals(Alphabet.MINUS)) {
                    DVAL();
                }
            }
        }

    }

    /**
     * DVAL -> [ - | +] valor| “[” valor “]”
     */
    private void DVAL() throws Exception {
        if (symbol.equals(Alphabet.ATTR)) {
            match(Alphabet.ATTR);

            if (symbol.equals(Alphabet.PLUS)) {
                match(Alphabet.PLUS);
            } else if(symbol.equals(Alphabet.MINUS)) {
                match(Alphabet.MINUS);
            }

            match(Alphabet.VALUE);
        } else if(symbol.equals(Alphabet.OPEN_BRACKETS)) {
            match(Alphabet.OPEN_BRACKETS);

            match(Alphabet.ID);

            while (!symbol.equals(Alphabet.CLOSE_BRACKETS)) {
                match(Alphabet.COMMA);
                match(Alphabet.ID);
            }

            match(Alphabet.CLOSE_BRACKETS);
        }
    }

    /**
     * BLOCO -> “{” {C}* “}”
     */
    private void BLOCO() throws Exception {
        if(symbol.equals(Alphabet.OPEN_KEYS)) {
            match(Alphabet.OPEN_KEYS);

            C();

            match(Alphabet.CLOSE_KEYS);
        }
    }

    /**
     * C -> CA | CR | CC | CF | ;
     */
    private void C() throws Exception {
        if (symbol.equals(Alphabet.ID)) {
            CA();
        } else if (symbol.equals(Alphabet.FOR)) {
            CR();
        } else if (symbol.equals(Alphabet.IF)) {
            match(Alphabet.IF);
            CC();
        } else {
            if (symbol.equals(Alphabet.READLN)) {
                match(Alphabet.READLN);
            } else if (symbol.equals(Alphabet.WRITE)) {
                match(Alphabet.WRITE);
            } else if (symbol.equals(Alphabet.WRITELN)) {
                match(Alphabet.WRITELN);
            }
            CF();
        }
    }

    /**
     * CR -> for id = EXPR to EXPR [ step valor] do ( C | BLOCO )
     */
    private void CR() throws Exception {
        match(Alphabet.FOR);
        match(Alphabet.ID);
        match(Alphabet.ATTR);
        EXPR();
        match(Alphabet.TO);

        if (symbol.equals(Alphabet.STEP)) {
            match(Alphabet.STEP);
            match(Alphabet.VALUE);
        }

        match(Alphabet.DO);

        match(Alphabet.OPEN_KEYS);
        while (symbol.equals(Alphabet.ID)
            || symbol.equals(Alphabet.FOR)
            || symbol.equals(Alphabet.IF)
            || symbol.equals(Alphabet.READLN)
            || symbol.equals(Alphabet.WRITELN)
            || symbol.equals(Alphabet.WRITE)) {
            C();
        }
        match(Alphabet.CLOSE_KEYS);
    }

    /**
     * CC -> if EXPR then ( C | BLOCO ) [ else C | BLOCO ]
     */
    private void CC() throws Exception {
        match(Alphabet.IF);
        EXPR();
        match(Alphabet.THEN);

        match(Alphabet.OPEN_KEYS);

        while (symbol.equals(Alphabet.ID)
                || symbol.equals(Alphabet.FOR)
                || symbol.equals(Alphabet.IF)
                || symbol.equals(Alphabet.READLN)
                || symbol.equals(Alphabet.WRITELN)
                || symbol.equals(Alphabet.WRITE)) {
            C();
        }

        match(Alphabet.CLOSE_KEYS);

        if (symbol.equals(Alphabet.ELSE)) {
            match(Alphabet.OPEN_KEYS);

            while (symbol.equals(Alphabet.ID)
                    || symbol.equals(Alphabet.FOR)
                    || symbol.equals(Alphabet.IF)
                    || symbol.equals(Alphabet.READLN)
                    || symbol.equals(Alphabet.WRITELN)
                    || symbol.equals(Alphabet.WRITE)) {
                C();
            }

            match(Alphabet.CLOSE_KEYS);
        }
    }

    /**
     * CF -> readln “(” id “)”  |  ( write | writeln ) "(" id ")” ;
     */
    private void CF() throws Exception {
        if (symbol.equals(Alphabet.READLN)) {
            match(Alphabet.READLN);
        } else if (symbol.equals(Alphabet.WRITELN)) {
            match(Alphabet.WRITELN);
        } else {
            match(Alphabet.WRITE);
        }

        match(Alphabet.OPEN_PAR);
        match(Alphabet.ID);
        match(Alphabet.CLOSE_PAR);
        match(Alphabet.SEMICOLON);
    }

    /**
     * CA -> id = EXPR ;
     */
    private void CA() throws Exception {
        match(Alphabet.ID);
        match(Alphabet.ATTR);
        EXPR();
        match(Alphabet.SEMICOLON);
    }

    /**
     * EXPR -> EXPS [ ( < | <= | > | >= | == | <> ) EXPS ]
     */
    private void EXPR() throws Exception {
        EXPS();
        if (symbol.equals(Alphabet.LT)) {
            match(Alphabet.LT);
        } else if (symbol.equals(Alphabet.LTE)) {
            match(Alphabet.LTE);
        } else if (symbol.equals(Alphabet.GT)) {
            match(Alphabet.GT);
        } else if (symbol.equals(Alphabet.GTE)) {
            match(Alphabet.GTE);
        } else if ( symbol.equals(Alphabet.EQ)) {
            match(Alphabet.EQ);
        } else if (symbol.equals(Alphabet.NEQ)) {
            match(Alphabet.NEQ);
        }
        EXPS();
    }

    /**
     * EXPS -> [ - | + ] T { ( - | + | or ) T }*
     */
    private void EXPS() throws Exception {
        if(symbol.equals(Alphabet.MINUS)) {
            match(Alphabet.MINUS);
        } else if (symbol.equals(Alphabet.PLUS)) {
            match(Alphabet.PLUS);
        }
        T();

        while (symbol.equals(Alphabet.MINUS)
                || symbol.equals(Alphabet.PLUS)
                || symbol.equals(Alphabet.OR)) {
            if(symbol.equals(Alphabet.MINUS)) {
                match(Alphabet.MINUS);
            } else if (symbol.equals(Alphabet.PLUS)) {
                match(Alphabet.PLUS);
            } else if (symbol.equals(Alphabet.OR)) {
                match(Alphabet.OR);
            }
            T();
        }
    }

    /**
     * T -> F { ( * | / | and | % ) F }*
     */
    private void T() throws Exception {
        if(symbol.equals(Alphabet.TIMES)) {
            match(Alphabet.TIMES);
        } else if(symbol.equals(Alphabet.DIVIDE)) {
            match(Alphabet.PLUS);
        } else if(symbol.equals(Alphabet.AND)) {
            match(Alphabet.AND);
        }
        F();


        while (symbol.equals(Alphabet.TIMES)
                || symbol.equals(Alphabet.DIVIDE)
                || symbol.equals(Alphabet.AND)) {
            if(symbol.equals(Alphabet.TIMES)) {
                match(Alphabet.TIMES);
            } else if (symbol.equals(Alphabet.DIVIDE)) {
                match(Alphabet.DIVIDE);
            } else if (symbol.equals(Alphabet.AND)) {
                match(Alphabet.AND);
            }
            F();
        }
    }

    /**
     * F -> id [ “[” { ,EXPS } + “]” ] | valor | not F | “(” EXPS “)”
     */
    private void F() throws Exception {
        if(symbol.equals(Alphabet.ID)) {
            match(Alphabet.ID);

            if(symbol.equals(Alphabet.OPEN_BRACKETS)) {
                match(Alphabet.OPEN_BRACKETS);
                EXPS();

                while (!symbol.equals(Alphabet.CLOSE_BRACKETS)){
                    match(Alphabet.COMMA);
                    EXPS();
                }

                match(Alphabet.CLOSE_BRACKETS);
            }
            else if (symbol.equals(Alphabet.VALUE)) {
                match(Alphabet.VALUE);
            } else if (symbol.equals(Alphabet.NOT)) {
                match(Alphabet.NOT);
                F();
            } else if (symbol.equals(Alphabet.OPEN_PAR)) {
                match(Alphabet.OPEN_PAR);
                F();
                match(Alphabet.CLOSE_PAR);
            }
        }
    }

}
