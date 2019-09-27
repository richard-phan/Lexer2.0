import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/*
 * ADD SECOND VECTOR TO ID THE ENDING STATE
 */

public class Lexer {

    private int[][] table;      // FSM table

    public Lexer() {

        /*
         * 0.  alpha
         * 1.  numeric
         * 2.  $
         * 3.  \
         * 4.  (
         * 5.  )
         * 6.  {
         * 7.  }
         * 8.  [
         * 9.  ]
         * 10. ,
         * 11. .
         * 12. :
         * 13. ;
         * 14. !
         * 15. space
         * 15. \t
         * 15. \r
         * 15. \n
         * 16. *
         * 17. +
         * 18. -
         * 19. =
         * 20. /
         * 21. >
         * 22. <
         * 23. %
         */

        table = new int[][]{
               /*    FSM Table    */
               //                               1  1  1  1  1  1  1  1  1  1  2  2  2  2
               // 0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  6  7  8  9  0  1  2  3
                { 1, 3, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 5, 0, 7, 7, 7, 7, 7, 7, 7, 7 }, //  0     starting state
                { 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 }, //  1     in identifier
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // [2]    end identifier
                { 4, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 8, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4 }, //  3     in number
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // [4]    end real number
                { 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 5, 5, 5, 5, 5, 5, 5, 5, 5 }, //  5     in comment
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // [6]    end comment
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // [7]    is symbol
                { 9, 8, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9 }, //  8     in float
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }  // [9]    end float
        };
    }

    public Vector[] getTokens(FileReader fr) throws IOException {
        Vector<String> tokens = new Vector<>();
        Vector<Integer> states = new Vector<>();

        int i;                      // character ascii value
        int state = 0;              // current state
        int col;                    // column of the character
        String token = "";          // token

        while ((i = fr.read()) != -1) {     // reads the file character by character
            char c = (char) i;              // converts the ascii value to a character

            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {      // removes whitespace /r
                    token += c;                                         // adds characters to the token
            }

            col = charToCol(c);             // converts the character to a column on the FSM table
            state = table[state][col];      // changes the state based on the current state and col

            if (state == 2 || state == 4 || state == 7 || state == 9) {     // checks for final state
                char lc = token.charAt(token.length() - 1);                 // gets last character
                if (token.length() > 1 && charToCol(lc) > 2) {              // checks if the token is valid
                    String id = token.substring(0, token.length() - 1);     // gets main token
                    String symbol = token.substring(token.length() - 1);    // gets ending symbol
                    tokens.add(id);                                         // adds main token to vector
                    tokens.add(symbol);                                     // adds ending symbol to vector
                    states.add(state);                                      // add id state
                    states.add(7);                                          // add symbol state
                    token = "";                                             // reset the token
                    state = 0;                                              // reset the state
                }
                else {
                    tokens.add(token);      // add token to vector
                    states.add(state);      // add state to vector
                    token = "";             // reset the token
                    state = 0;              // reset the state
                }
            }
            else if (state == 6) {          // check if the token is a comment
                token = "";                 // reset the token
                state = 0;                  // reset the state
            }
        }

        if (!token.equals("")) {                            // checks if a token exists without an ending signifier
            tokens.add(token);                              // add token to vectors
            state += 1;                                     // makes the state into a final state
            states.add(state);                              // add state to vector
        }

        return new Vector[]{tokens, states};
    }

    private int charToCol(char c) {         // returns a column value based on the character type
        if (Character.isAlphabetic(c)) {
            return 0;
        } else if (Character.isDigit(c)) {
            return 1;
        }

        switch(c) {
            case '$': return 2;
            case '\'': return 3;
            case '(': return 4;
            case ')': return 5;
            case '{': return 6;
            case '}': return 7;
            case '[': return 8;
            case ']': return 9;
            case ',': return 10;
            case '.': return 11;
            case ':': return 12;
            case ';': return 13;
            case '!': return 14;
            case ' ': return 15;
            case '\t': return 15;
            case '\r': return 15;
            case '\n': return 15;
            case '*': return 16;
            case '+': return 17;
            case '-': return 18;
            case '=': return 19;
            case '/': return 20;
            case '>': return 21;
            case '<': return 22;
            case '%': return 23;
        }
        return -1;  // return -1 if character is unknown
    }

    public Vector[] matchTokens(Vector[] list) {
        Map<String, String> map = new HashMap();    // Hashmap for lexeme
        map.put("int", "keyword");
        map.put("float", "keyword");
        map.put("bool", "keyword");
        map.put("if", "keyword");
        map.put("else", "keyword");
        map.put("then", "keyword");
        map.put("endif", "keyword");
        map.put("while", "keyword");
        map.put("whileend", "keyword");
        map.put("do", "keyword");
        map.put("doend", "keyword");
        map.put("for", "keyword");
        map.put("forend", "keyword");
        map.put("input", "keyword");
        map.put("output", "keyword");
        map.put("and", "keyword");
        map.put("or", "keyword");
        map.put("function", "keyword");
        map.put("'", "separator");
        map.put("(", "separator");
        map.put(")", "separator");
        map.put("{", "separator");
        map.put("}", "separator");
        map.put("[", "separator");
        map.put("]", "separator");
        map.put(",", "separator");
        map.put(".", "separator");
        map.put(":", "separator");
        map.put(";", "separator");
        map.put("!", "separator");
        map.put("*", "separator");
        map.put("+", "operator");
        map.put("-", "operator");
        map.put("=", "operator");
        map.put("/", "operator");
        map.put(">", "operator");
        map.put("<", "operator");
        map.put("%", "operator");

        Vector token = list[0];             // token vector
        Vector states = list[1];            // states vector
        String id;                          // possible identifier/keyword
        Vector<String> lexeme = new Vector<>();            // lexeme vector

        for (int i = 0; i < list[1].size(); i++) {
            int state = (int) states.get(i);

            if (state == 2 || state == 7) {                       // sorts possible keywords and identifiers and separators
                id = (String) token.get(i);
                lexeme.add(map.getOrDefault(id, "identifier"));          // assigns the map lexeme or defaults to "identifier"
            } else if (state == 4) {
                lexeme.add("real");                 // labels real number
            } else if (state == 9) {
                lexeme.add("float");                // labels float
            } else {
                lexeme.add("UNKNOWN");              // labels unknown states
            }
        }

        return new Vector[]{lexeme, token};         // returns the table
    }
}
