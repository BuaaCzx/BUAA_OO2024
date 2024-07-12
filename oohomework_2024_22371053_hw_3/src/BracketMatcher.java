import java.util.Stack;

public class BracketMatcher {
    private Stack<Character> stack;

    public BracketMatcher() {
        stack = new Stack<>();
    }

    public boolean isPaired() {
        return stack.isEmpty();
    }

    public void pushLeftBracket() {
        stack.push('(');
    }

    public void pushRightBracket() {
        if (stack.peek() == '(') {
            stack.pop();
        } else {
            stack.push(')');
        }
    }

}
