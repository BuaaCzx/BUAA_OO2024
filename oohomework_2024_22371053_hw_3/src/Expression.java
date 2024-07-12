import java.math.BigInteger;
import java.util.ArrayList;

public class Expression implements Factor {
    // 一堆 Term 相加，对 Term 先进行预处理，将Term转化为一堆 variableFactor 相加，进而exp就是一堆Factor相加

    private ArrayList<VariableFactor> factors;
    // 递归下降保证处理过程中所有的 expression 都是 variableFactor 相加的形式

    public Expression() {
        factors = new ArrayList<>();
    }

    @Override
    public Expression clone() {
        Expression expr = new Expression();
        for (VariableFactor factor : factors) {
            expr.addFactor(factor.clone());
        }
        return expr;
    }

    public void addFactor(Factor factor) {
        if (factor instanceof VariableFactor) {
            factors.add((VariableFactor) factor);
        } else if (factor instanceof Expression) {
            Expression expr = (Expression) factor;
            for (VariableFactor f : expr.getFactors()) {
                factors.add(f.clone());
            }
        }
    }

    public Expression addExpression(Expression other) { // 加法运算
        Expression res = this.clone();
        for (VariableFactor factor : other.getFactors()) {
            res.addFactor(factor);
        }
        res.merge();
        return res;
    }

    public boolean isEmpty() {
        return factors.isEmpty();
    }

    public ArrayList<VariableFactor> getFactors() {
        return factors;
    }

    public Expression multiply(Factor other) {
        if (other instanceof Expression) {
            return this.multiply((Expression) other);
        } else {
            return this.multiply((VariableFactor) other);
        }
    }

    public Expression multiply(Expression other) {
        Expression result = new Expression();
        for (VariableFactor factor : factors) {
            for (VariableFactor otherFactor : other.getFactors()) {
                result.addFactor(factor.multiply(otherFactor));
            }
        }
        return result;
    }

    public Expression multiply(VariableFactor factor) {
        Expression result = new Expression();
        for (VariableFactor f : factors) {
            result.addFactor(f.multiply(factor));
        }
        return result;
    }

    public Expression pow(int n) {
        Expression result = new Expression();
        result.addFactor(new VariableFactor(BigInteger.ONE, BigInteger.ZERO, new Expression()));
        Expression base = this;
        for (int i = 0; i < n; i++) {
            result = result.multiply(base);
        }
        result.merge();
        return result;
    }

    public boolean equals(Expression obj) {
        this.merge();
        obj.merge();
        if (this.factors.size() != obj.factors.size()) {
            return false;
        }
        ArrayList<VariableFactor> allFactors = new ArrayList<>();
        for (VariableFactor factor : this.factors) {
            boolean find = false;
            for (VariableFactor variableFactor : allFactors) {
                if (factor.equals(variableFactor)) {
                    find = true;
                    break;
                }
            }
            if (!find) {
                allFactors.add(factor);
            }
        }
        for (VariableFactor factor : obj.factors) {
            boolean find = false;
            for (VariableFactor variableFactor : allFactors) {
                if (factor.equals(variableFactor)) {
                    find = true;
                    break;
                }
            }
            if (!find) {
                allFactors.add(factor);
            }
        }
        return allFactors.size() == this.factors.size();
    }

    public void merge() {
        /* TODO: 需要重写merge方法。难点：expression判等。*/
        Expression res = new Expression();
        for (VariableFactor factor : this.factors) {
            boolean find = false;
            for (int i = 0; i < res.getFactors().size(); i++) {
                VariableFactor curFactor = res.getFactors().get(i);
                if (curFactor.canMerge(factor)) {
                    curFactor.setCoefficient(
                            curFactor.getCoefficient().add(factor.getCoefficient()));
                    find = true;
                    break;
                }
            }
            if (!find) {
                res.addFactor(factor);
            }
        }
        res.removeZeros();
        factors = res.getFactors();
    }

    private void removeZeros() {
        factors.removeIf(factor -> factor.getCoefficient().equals(BigInteger.ZERO));
    }

    public void reverseSign() {
        factors.replaceAll(VariableFactor::reverseSign);
    }

    public String toString() {
        if (factors.isEmpty()) {
            return "0";
        }
        // int first = getPositiveFactorIndex();
        StringBuilder sb = new StringBuilder();
        sb.append(factors.get(0).toString());
        for (int i = 1; i < factors.size(); i++) {
            if (factors.get(i).getSign() != -1) {
                sb.append("+");
            }
            sb.append(factors.get(i).toString());
        }
        return sb.toString();
    }

    private int getPositiveFactorIndex() {
        /*TODO*/
        return 0;
    }

    public boolean isSingleFactor() {
        if (this.isEmpty()) {
            return true;
        }
        if (factors.size() == 1) {
            String str = this.toString();
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) == '*') {
                    return false;
                }
            }
            return str.charAt(0) != '-' || Character.isDigit(str.charAt(1));
        }
        return false;
    }

    public Expression differentiate() {
        Expression res = new Expression();
        for (VariableFactor vf : factors) {
            res.addFactor(vf.differentiate());
        }
        return res;
    }
}

