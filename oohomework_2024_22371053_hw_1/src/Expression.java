import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

public class Expression implements Factor {
    // 一堆 Term 相加，对 Term 先进行预处理，将Term转化为一堆 variableFactor 相加，进而exp就是一堆Factor相加

    private ArrayList<VariableFactor> factors;
    // 递归下降保证处理过程中所有的 expression 都是 variableFactor 相加的形式

    public Expression() {
        factors = new ArrayList<>();
    }

    public void addFactor(VariableFactor factor) {
        factors.add(factor);
    }

    public void addExpression(Expression other) {
        for (VariableFactor factor : other.getFactors()) {
            addFactor(factor);
        }
    }

    public boolean isEmpty() {
        return factors.isEmpty();
    }

    public ArrayList<VariableFactor> getFactors() {
        return factors;
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

    public Expression pow(int n) {
        Expression result = new Expression();
        result.addFactor(new VariableFactor(BigInteger.ONE, BigInteger.ZERO));
        Expression base = this;
        for (int i = 0; i < n; i++) {
            result = result.multiply(base);
        }
        result.merge();
        return result;
    }

    public void merge() {
        ArrayList<VariableFactor> newFactors = new ArrayList<>();
        HashMap<BigInteger, BigInteger> powerCoeMap = new HashMap<>();
        for (VariableFactor factor : factors) {
            if (powerCoeMap.containsKey(factor.getPower())) {
                powerCoeMap.put(factor.getPower(),
                        powerCoeMap.get(factor.getPower()).add(factor.getCoefficient()));
            } else {
                powerCoeMap.put(factor.getPower(), factor.getCoefficient());
            }
        }
        for (HashMap.Entry<BigInteger, BigInteger> entry : powerCoeMap.entrySet()) {
            BigInteger power = entry.getKey();
            BigInteger coefficient = entry.getValue();
            if (!coefficient.equals(BigInteger.ZERO)) {
                newFactors.add(new VariableFactor(coefficient, power));
            }
        }
        if (newFactors.isEmpty()) {
            newFactors.add(new VariableFactor(BigInteger.ZERO, BigInteger.ZERO));
        }
        factors = newFactors;
    }

    public void reverseSign() {
        factors.replaceAll(VariableFactor::reverseSign);
    }

    public String toString() {
        if (factors.isEmpty()) {
            return "0";
        }
        int first = getPositiveFactorIndex();
        StringBuilder sb = new StringBuilder();
        sb.append(factors.get(first).toString());
        for (int i = 0; i < factors.size(); i++) {
            if (i == first) {
                continue;
            }
            if (factors.get(i).getSign() != -1) {
                sb.append("+");
            }
            sb.append(factors.get(i).toString());
        }
        return sb.toString();
    }

    private int getPositiveFactorIndex() {
        for (int i = 0; i < factors.size(); i++) {
            if (factors.get(i).getSign() == 1) {
                return i;
            }
        }
        return 0;
    }
}

