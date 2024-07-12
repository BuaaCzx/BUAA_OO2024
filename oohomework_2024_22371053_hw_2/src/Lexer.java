public class Lexer {
    private final String input;
    private int pos;
    private String curToken;
    private boolean hasMore;

    public Lexer(String input) {
        this.input = input;
        pos = 0;
        nextToken();
    }

    private String getNumber() {
        StringBuilder sb = new StringBuilder();
        if (input.charAt(pos) == '+' || input.charAt(pos) == '-') {
            sb.append(input.charAt(pos++));
        }
        while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
            sb.append(input.charAt(pos));
            ++pos;
        }
        return sb.toString();
    }

    public void nextToken() { // 数字，乘号*，括号()，exp，fhg，正负号
        if (pos == input.length()) {
            hasMore = false;
            return;
        }
        char c = input.charAt(pos);
        if (Character.isDigit(c)) { // 数字
            curToken = getNumber();
        } else if (c == '+' || c == '-') {
            char nextChar = input.charAt(pos + 1);
            if (Character.isDigit(nextChar)) { // 数字
                curToken = getNumber();
            } else { // 别的，这个符号就作为整个 term 的符号（正负号）
                curToken = String.valueOf(c);
                pos++;
            }
        } else if (c == 'e') {
            pos += 3;
            curToken = "exp";
        } else {
            pos += 1;
            curToken = String.valueOf(c);
        }

        hasMore = true;
    }

    public String peek() {
        return curToken;
    }

    public boolean hasMore() {
        return hasMore;
    }

}
