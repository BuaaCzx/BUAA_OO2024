import java.util.ArrayList;

public class Term {

    // 一个 Term，是许多 Factor 相乘得到的结果。用一个容器把所有乘积项 Factor 储存起来即可。

    private ArrayList<Factor> factors = new ArrayList<>();
    private int sign;

    public Term(int sign) {
        this.sign = sign;
    }

    public void addFactor(Factor f) {
        factors.add(f);
    }

    public Expression getExpression() { // 解析后得到的exp里的factor应全部是底层的variable
        Expression expression = new Expression();
        if (factors.isEmpty()) {
            return expression;
        }
        expression.addFactor(factors.get(0));
        for (int i = 1; i < factors.size(); i++) {
            Factor f = factors.get(i);
            expression = expression.multiply(f);
        }
        expression.merge();
        if (sign == -1) {
            expression.reverseSign();
        }
        return expression;
    }

}
