import java.util.Scanner;

public class MainClass {

    public static String removeSpacesAndTabs(String input) {
        return input.replaceAll("[ \t]", "");
    }

    public static String addPlus(String input) {
        if (input.length() < 2
                || (input.charAt(0) != '+' && input.charAt(0) != '-')
                || (input.charAt(1) != '+' && input.charAt(1) != '-')) {
            return "+" + input;
        }
        return input;
    }

    public static String replaceConsecutiveSigns(String input) {
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

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        input = removeSpacesAndTabs(input);
        input = addPlus(input);
        input = replaceConsecutiveSigns(input);
        // System.out.println(input);
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);

        Expression expr = parser.parseExpr();

        System.out.println(expr.toString());

    }
}
