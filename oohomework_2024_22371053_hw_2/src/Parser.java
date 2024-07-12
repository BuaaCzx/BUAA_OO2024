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
            expr = expr.addExpression(term.getExpression());
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
            return parseExpressionFactor();
        } else if (lexer.peek().equals("x")) { // 变量因子：幂函数
            return parseVariableFactor();
        } else if (lexer.peek().equals("exp")) { // 变量因子：指数函数
            return parseExponentFactor();
        } else { // 常数因子：数字
            // System.err.println(lexer.peek() + " " + lexer.hasMore());
            return parseConstantFactor();
        }
    }

    private int parsePower() {
        if (lexer.peek().equals("^")) {
            lexer.nextToken();
            int power = Integer.parseInt(lexer.peek());
            lexer.nextToken();
            return power;
        } else {
            return 1;
        }
    }

    private Expression parseExpressionFactor() {
        lexer.nextToken();
        Expression expr = parseExpr();
        lexer.nextToken();
        return expr.pow(parsePower());
    }

    private VariableFactor parseVariableFactor() {
        VariableFactor variableFactor = new VariableFactor(
                BigInteger.ONE,
                BigInteger.ONE,
                new Expression()
        );
        lexer.nextToken();
        variableFactor.setPower(BigInteger.valueOf(parsePower()));
        return variableFactor;
    }

    private VariableFactor parseExponentFactor() {
        lexer.nextToken(); // 此时 curToken 指向 exp()的左括号(
        lexer.nextToken(); // 此时 curToken 指向 exp(something)中 something 的第一个token
        Factor factor = parseFactor();
        Expression expr = new Expression();
        expr.addFactor(factor);
        lexer.nextToken(); // 跳出 exp 的右括号，指向 exp 后的 token
        int pow = parsePower();
        expr = expr.multiply(new VariableFactor(
                BigInteger.valueOf(pow),
                BigInteger.ZERO,
                new Expression()
        ));
        return new VariableFactor(BigInteger.ONE, BigInteger.ZERO, expr);
    }

    private VariableFactor parseConstantFactor() {
        BigInteger num = new BigInteger(lexer.peek());
        lexer.nextToken();
        return new VariableFactor(num, BigInteger.ZERO, new Expression());
    }
}
