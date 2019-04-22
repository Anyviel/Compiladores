public class Symbol {
    private String token;
    private String lexeme;
    private boolean isConst;

    Symbol(String token, String lexeme, boolean isConst) {
        this.token = token;
        this.lexeme = lexeme;
        this.isConst = isConst;
    }

    public boolean equals(Alphabet token) {
        return this.getToken().equals(token.getToken());
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLexeme() {
        return lexeme;
    }

    public void setLexeme(String lexeme) {
        this.lexeme = lexeme;
    }

    public boolean isConst() {
        return isConst;
    }

    public void setConst(boolean aConst) {
        isConst = aConst;
    }

    @Override
    public String toString() {
        return "Symbol{" +
                "token='" + token + '\'' +
                ", lexeme='" + lexeme + '\'' +
                ", isConst=" + isConst +
                '}';
    }
}
