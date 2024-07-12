import java.math.BigInteger;

public class Parser {
    private final Lexer lexer;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public Expression parseExpr() {
        Expression expr = new Expression();
        while (!lexer.peek().equals(")") && lexer.hasMore()) {
            int sign = 1;
            if (lexer.peek().equals("-") || lexer.peek().equals("+")) {
                sign = lexer.peek().equals("+") ? 1 : -1;
                lexer.nextToken();
            }
            // System.err.println("Start parseTerm: " + lexer.peek() + " " + lexer.hasMore());
            Term term = parseTerm(sign);
            expr.addExpression(term.getExpression());
            expr.merge();
        }
        return expr;
    }

    public Term parseTerm(int sign) {
        Term term = new Term(sign);
        term.addFactor(parseFactor());
        while (lexer.peek().equals("*")) {
            lexer.nextToken();
            term.addFactor(parseFactor());
        }
        return term;
    }

    public Factor parseFactor() {
        if (lexer.peek().equals("(")) { // 表达式因子，递归求解
            lexer.nextToken();
            Expression expr = parseExpr();
            lexer.nextToken();
            if (lexer.peek().equals("^")) {
                lexer.nextToken();
                int power = Integer.parseInt(lexer.peek());
                lexer.nextToken();
                expr = expr.pow(power);
            }
            return expr;
        } else if (lexer.peek().equals("x")) {
            VariableFactor variableFactor = new VariableFactor(BigInteger.ONE, BigInteger.ONE);
            lexer.nextToken();
            if (lexer.peek().equals("^")) {
                lexer.nextToken();
                int power = Integer.parseInt(lexer.peek());
                lexer.nextToken();
                variableFactor.setPower(BigInteger.valueOf(power));
            }
            return variableFactor;
        } else {
            // System.err.println(lexer.peek() + " " + lexer.hasMore());
            BigInteger num = new BigInteger(lexer.peek());
            lexer.nextToken();
            return new VariableFactor(num, BigInteger.ZERO);
        }
    }
}
