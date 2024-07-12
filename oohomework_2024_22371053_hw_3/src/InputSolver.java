import java.util.ArrayList;
import java.util.Scanner;

public class InputSolver {

    private ArrayList<Function> functions;

    public InputSolver(Scanner scanner) {
        solveFunction(scanner);
    }

    public void solveFunction(Scanner scanner) {
        this.functions = inputFunction(scanner);
    }

    public String removeFunction(String input) {
        String res = input;
        for (int i = 0; i < res.length(); i++) {
            char c = res.charAt(i);
            if (Character.isAlphabetic(c) && isFunction(c)) {
                Function function = getFunction(c);
                res = function.expandFunction(this, res, i);
                // System.err.println("After expandFunc : " + res);
            }
        }
        return res;
    }

    public boolean isFunction(char c) {
        for (Function function : functions) {
            if (function.getFunctionName() == c) {
                return true;
            }
        }
        return false;
    }

    public Function getFunction(char c) {
        for (Function function : functions) {
            if (function.getFunctionName() == c) {
                return function;
            }
        }
        return null;
    }

    private ArrayList<Function> inputFunction(Scanner scanner) {
        ArrayList<Function> functions = new ArrayList<>();
        int n = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character
        for (int i = 0; i < n; i++) {
            String function = scanner.nextLine();
            function = initFunction(function);
            functions.add(new Function(function));
        }
        return functions;
    }

    public String simplify(String inputstr) {
        String input = inputstr;
        input = removeSpacesAndTabs(input);
        input = removeFunction(input);
        input = addPlus(input);
        input = replaceConsecutiveSigns(input);
        return input;
    }

    public static String removeSpacesAndTabs(String input) {
        return input.replaceAll("[ \t]", "");
    }

    public String addPlus(String input) {
        if (input.length() < 2 ||
                (input.charAt(0) != '+' && input.charAt(0) != '-') ||
                (input.charAt(1) != '+' && input.charAt(1) != '-')) {
            return "+" + input;
        }
        return input;
    }

    public String replaceConsecutiveSigns(String input) {
        StringBuilder sb = new StringBuilder(input);
        int count = 0;
        for (int i = 0; i < sb.length(); i++) {
            char currentChar = sb.charAt(i);
            if (currentChar == '+' || currentChar == '-') {
                count++;
                if (count == 2) {
                    char replaceChar = sb.charAt(i - 1) == sb.charAt(i) ? '+' : '-';
                    sb.replace(i - 1, i + 1, String.valueOf(replaceChar));
                    count = 1;
                    i--;
                }
            } else {
                count = 0;
            }
        }
        return sb.toString();
    }

    private static String initFunction(String inputstr) {
        String input = inputstr;
        input = InputSolver.removeSpacesAndTabs(input);
        input = input.replaceAll("x", "a");
        input = input.replaceAll("eap", "exp");
        return input;
    }
}
