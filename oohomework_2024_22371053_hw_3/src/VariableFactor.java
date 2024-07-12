import java.math.BigInteger;

public class VariableFactor implements Factor {
    // 变量项，表示最底层的一个单元，形式为 coe*x^power*exp(exponentExpr)

    private BigInteger coefficient; // 系数
    private BigInteger power; // 幂次
    private Expression exponentExpr; // e^(?) 认为 Expression 为空即为 0.

    public VariableFactor(BigInteger coefficient, BigInteger power, Expression expr) {
        this.coefficient = coefficient;
        this.power = power;
        exponentExpr = expr;
    }

    public boolean equals(VariableFactor other) {
        return this.coefficient.equals(other.coefficient)
                && this.power.equals(other.power)
                && this.exponentExpr.equals(other.exponentExpr);
    }

    public boolean canMerge(VariableFactor other) {
        return this.power.equals(other.power) && this.exponentExpr.equals(other.exponentExpr);
    }

    @Override
    public VariableFactor clone() {
        return new VariableFactor(coefficient, power, exponentExpr.clone()); // BigInt浅拷贝？是否存在bug未知
    }

    public BigInteger getCoefficient() {
        return coefficient;
    }

    public BigInteger getPower() {
        return power;
    }

    public Expression getExponentExpr() {
        return exponentExpr;
    }

    public void setCoefficient(BigInteger coefficient) {
        this.coefficient = coefficient;
    }

    public void setPower(BigInteger power) {
        this.power = power;
    }

    public VariableFactor multiply(VariableFactor factor) {
        return new VariableFactor(coefficient.multiply(factor.coefficient),
                power.add(factor.power), exponentExpr.addExpression(factor.getExponentExpr()));
    }

    public VariableFactor reverseSign() {
        return new VariableFactor(coefficient.negate(), power, exponentExpr);
    }

    public String toString() {
        String exponent = exponentExpr.isSingleFactor() ?
                "*exp(" + exponentExpr.toString() + ")" :
                "*exp((" + exponentExpr.toString() + "))";
        if (exponent.equals("*exp(0)")) {
            exponent = "";
        }
        // String exponent = "*exp((" + exponentExpr.toString() + "))";
        String res = "";
        if (coefficient.equals(BigInteger.ZERO)) {
            return "0";
        } else if (coefficient.equals(BigInteger.ONE)) {
            if (power.equals(BigInteger.ZERO)) {
                res = exponent.isEmpty() ? "1" : exponent.substring(1);
            } else if (power.equals(BigInteger.ONE)) {
                return "x" + exponent;
            } else {
                return "x^" + power + exponent;
            }
        } else if (coefficient.equals(BigInteger.ONE.negate())) {
            if (power.equals(BigInteger.ZERO)) {
                res = exponent.isEmpty() ? "-1" : "-" + exponent.substring(1);
            } else if (power.equals(BigInteger.ONE)) {
                return "-x" + exponent;
            } else {
                return "-x^" + power + exponent;
            }
        } else {
            res += coefficient;
            if (power.equals(BigInteger.ZERO)) {
                res += exponent;
            } else if (power.equals(BigInteger.ONE)) {
                res += "*x" + exponent;
            } else {
                res += "*x^" + power + exponent;
            }
        }
        return res.isEmpty() ? "0" : res;
    }

    public int getSign() {
        return coefficient.signum();
    }

    // 求导
    // 求导：power == 0 && exponentExpr == ""
    // power == 0, coe*dx(exponentExpr)*exp(exponentExpr)
    // exponentExpr = "", coe*power*x^(power-1)
    // else, coe*power*x^(power-1)*exp(exponentExpr)+coe*x^power*dx(exponentExpr)*exp(exponentExpr)
    public Expression differentiate() {
        // System.err.println(this);
        if (power.equals(BigInteger.ZERO) && exponentExpr.isEmpty()) {
            // System.err.println("type0");
            return new Expression();
        } else if (power.equals(BigInteger.ZERO)) {
            // System.err.println("type1");
            Expression difExpr = this.exponentExpr.differentiate();
            // System.err.println(difExpr.toString());
            return difExpr.multiply(new VariableFactor(
                    this.coefficient,
                    BigInteger.ZERO,
                    exponentExpr.clone()
            ));
        } else if (exponentExpr.isEmpty()) {
            // System.err.println("type2");
            Expression res = new Expression();
            res.addFactor(new VariableFactor(
                    this.coefficient.multiply(this.power),
                    this.power.subtract(BigInteger.ONE),
                    new Expression()
            ));
            return res;
        } else {
            Expression res = new Expression();
            res.addFactor(new VariableFactor(
                    this.coefficient.multiply(this.power),
                    this.power.subtract(BigInteger.ONE),
                    this.exponentExpr.clone()
            ));
            Expression difExpr = this.exponentExpr.differentiate();
            res.addFactor(difExpr.multiply(new VariableFactor(
                    this.coefficient,
                    this.power,
                    this.exponentExpr.clone()
            )));
            return res;
        }
    }

}
