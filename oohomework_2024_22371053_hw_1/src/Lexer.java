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

    public void nextToken() {
        if (pos == input.length()) {
            hasMore = false;
            return;
        }
        char c = input.charAt(pos);
        if (Character.isDigit(c)) { // 数字
            curToken = getNumber();
        } else if (c == '+' || c == '-') {
            char nxtc = input.charAt(pos + 1);
            if (Character.isDigit(nxtc)) {
                curToken = getNumber();
            } else {
                curToken = String.valueOf(c);
                pos++;
            }
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
