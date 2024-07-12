import java.util.ArrayList;

public class Term {

    // 一个 Term，是许多 Factor 相乘得到的结果。用一个容器把所有乘积项 Factor 储存起来即可。

    private ArrayList<Factor> factors = new ArrayList<Factor>();
    private int sign;

    public Term(int sign) {
        this.sign = sign;
    }

    public void addFactor(Factor f) {
        factors.add(f);
    }

    public Expression getExpression() { // 解析后得到的exp里的factor应全部是底层的variable
        Expression expression = new Expression();
        for (Factor f : factors) {
            if (expression.isEmpty()) { // 空，初始化
                if (f instanceof VariableFactor) {
                    expression.addFactor((VariableFactor) f);
                } else {
                    for (VariableFactor g : ((Expression) f).getFactors()) {
                        expression.addFactor(g);
                    }
                }
            } else { // 非空，做乘法
                if (f instanceof VariableFactor) {
                    for (int i = 0; i < expression.getFactors().size(); i++) {
                        expression.getFactors().set(i,
                                expression.getFactors().get(i).multiply((VariableFactor) f));
                    }
                } else {
                    expression = expression.multiply((Expression) f);
                }
            }
        }
        expression.merge();
        if (sign == -1) {
            expression.reverseSign();
        }
        return expression;
    }

}
