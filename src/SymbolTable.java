import java.util.Arrays;
import java.util.HashMap;

public class SymbolTable {
    private HashMap<String, Symbol> table;

    SymbolTable() {
        this.table = new HashMap<>();
        for(Alphabet a: Alphabet.values()) {
            table.put(a.getToken(), new Symbol(a.getToken(), a.getToken(), true));
        }
    }

    public Symbol searchLexeme(Alphabet symbol) {
        return searchLexeme(symbol.getToken());
    }

    Symbol searchLexeme(String lexeme) {
        return table.get(lexeme);
    }

    public Symbol insertId(String lexeme) {
        return this.insertValue(Alphabet.ID, lexeme, false);
    }

    public Symbol insertConst(String lexeme) {
        return this.insertValue(Alphabet.VALUE, lexeme, true);
    }

    private Symbol insertValue(Alphabet token, String lexeme, boolean isConst) {
        Symbol symbol = new Symbol(token.getToken(), lexeme, isConst);
        table.put(lexeme, symbol);
        return symbol;
    }

    void print() {
        System.out.println(Arrays.toString(table.entrySet().toArray()));
    }
}
