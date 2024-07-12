import java.util.Scanner;

public class MainClass {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        InputSolver inputSolver = new InputSolver(scanner);
        String input = scanner.nextLine();
        input = inputSolver.simplify(input);
        // System.err.println(input);
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);

        Expression expr = parser.parseExpr();

        System.out.println(expr.toString());

    }
}

//((((((x))^2))+(ar1)+(ar2)),(((x))^2),(((x))^2))