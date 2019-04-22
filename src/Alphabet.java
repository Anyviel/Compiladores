public enum Alphabet {
    IF("if"),
    FOR("for"),
    INTEGER("integer"),
    CHAR("char"),
    OPEN_KEYS("{"),
    CLOSE_KEYS("}"),
    LT("<"),
    GT(">"),
    GTE(">="),
    LTE("<="),
    EQ("=="),
    NEQ("<>"),
    NOT("not"),
    OR("or"),
    AND("and"),
    TO("to"),
    MOD("%"),
    STEP("step"),
    SEMICOLON(";"),
    ELSE("else"),
    OPEN_PAR("("),
    CLOSE_PAR(")"),
    OPEN_BRACKETS("["),
    CLOSE_BRACKETS("]"),
    ATTR("="),
    CONST("const"),
    PLUS("+"),
    MINUS("-"),
    TIMES("*"),
    DIVIDE("/"),
    THEN("then"),
    ID("id"),
    VALUE(""),
    VAR("var"),
    COMMA(","),
    READLN("readln"),
    WRITELN("writeln"),
    WRITE("write"),
    DO("do"),
    ;

    private String letter;

    Alphabet(String letter) {
        this.letter = letter;
    }

    public String getToken() {
        return this.letter;
    }
}