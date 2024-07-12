import java.math.BigInteger;

public class VariableFactor implements Factor { // 变量项，包括了变量因子和常量因子（power=0），表示最底层的一个单元
    private BigInteger coefficient; // 系数
    private BigInteger power; // 幂次

    public VariableFactor(BigInteger coefficient, BigInteger power) {
        this.coefficient = coefficient;
        this.power = power;
    }

    @Override
    public VariableFactor clone() {
        return new VariableFactor(new BigInteger(this.coefficient.toByteArray()),
                new BigInteger(this.power.toByteArray()));
    }

    public BigInteger getCoefficient() {
        return coefficient;
    }

    public BigInteger getPower() {
        return power;
    }

    public void setCoefficient(BigInteger coefficient) {
        this.coefficient = coefficient;
    }

    public void setPower(BigInteger power) {
        this.power = power;
    }

    public VariableFactor multiply(VariableFactor factor) {
        return new VariableFactor(coefficient.multiply(factor.coefficient)
                , power.add(factor.power));
    }

    public VariableFactor reverseSign() {
        return new VariableFactor(coefficient.negate(), power);
    }

    public String toString() {
        String res = "";
        if (power.equals(BigInteger.ZERO)) {
            res += coefficient.toString();
        } else if (power.equals(BigInteger.ONE)) {
            res += coefficient.equals(BigInteger.ONE) ? "x" :
                    coefficient.equals(BigInteger.valueOf(-1)) ? "-x" : coefficient + "*x";
        } else {
            res += coefficient.equals(BigInteger.ONE) ? "x^" + power :
                    coefficient.equals(BigInteger.valueOf(-1)) ? "-x^" + power :
                            coefficient + "*x^" + power;
        }
        return res;
    }

    public int getSign() {
        return coefficient.signum();
    }
}
