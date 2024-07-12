import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Function {
    private final char functionName;
    private String expression;
    private final String func;

    public Function(String expression) {
        this.functionName = expression.charAt(0);
        String[] parts = expression.split("=");
        this.func = parts[0].trim();
        this.expression = "(" + parts[1].trim() + ")";
        setArg();
        // System.err.println("Input to build : " + expression);
        // System.err.println("Func : " + this.expression);
    }

    private void setArg() {
        String regex = "([a-zA-Z]+)\\((.*?)\\)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(func);

        if (matcher.matches()) {
            String parameters = matcher.group(2);
            ArrayList<String> params = new ArrayList<>();
            for (String param : parameters.split(",")) {
                // System.err.println("Parameter: " + param.trim());
                params.add(param.trim());
            }
            changeArgName(params);
            // System.err.println("Expression: " + expression);
        }
    }

    private void changeArgName(ArrayList<String> params) {
        for (int i = 0; i < params.size(); i++) {
            // System.err.println("Param: " + params.get(i) + ", Before : " + expression);
            expression = expression.replaceAll(params.get(i), "(pr" + i + ")");
            // System.err.println("Param: " + params.get(i) + ", After : " + expression);
        }
    }

    public char getFunctionName() {
        return functionName;
    }

    public String expandFunction(InputSolver inputSolver, String inputstr, int pos) {
        BracketMatcher bracketMatcher = new BracketMatcher();
        bracketMatcher.pushLeftBracket();
        ArrayList<String> args = new ArrayList<>();
        StringBuilder arg = new StringBuilder();
        String input = inputstr;
        for (int i = pos + 2; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isAlphabetic(c) && inputSolver.isFunction(c)) {
                // System.err.println("In function: " + c);
                Function function = inputSolver.getFunction(c);
                input = function.expandFunction(inputSolver, input, i);
                // System.err.println(input);
            }
            c = input.charAt(i);
            if (c == ')') {
                bracketMatcher.pushRightBracket();
            } else if (c == '(') {
                bracketMatcher.pushLeftBracket();
            }
            if (bracketMatcher.isPaired()) {
                args.add(arg.toString());
                return exchangeExpr(input, pos, i, args);
            } else if (c == ',') {
                args.add(arg.toString());
                arg = new StringBuilder();
            } else {
                arg.append(input.charAt(i));
            }
        }
        return "error";
    }

    private String exchangeExpr(String input, int start, int end, ArrayList<String> args) {
        String expr = expression;
        // System.err.println("Before: " + input + " " + expression);
        for (int i = 0; i < args.size(); i++) {
            expr = expr.replace("pr" + i, "(" + args.get(i) + ")");
            // System.err.println("Replacing arg" + i + " with " + "(" + args.get(i) + ")");
        }
        // System.err.println("Expr : " + expr);
        // System.err.println(input.substring(0, start) + expr + input.substring(end + 1));
        return input.substring(0, start) + expr + input.substring(end + 1);
    }
}

//((((x)+(((x)+1-((x)))^2))