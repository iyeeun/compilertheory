import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.Arrays;
import java.util.ArrayList;

public class SmallScan {
    
    static final int START = 0;
    static final int IDENTIFIER = 1;
    static final int NUMBER = 2;
    static final int OPERATOR = 3;
    static final int STRING_LITERAL_OPEN = 4;
    static final int STRING_LITERAL_CLOSE = 5;
    static final int SPECIAL_CHAR = 6;
    static final int STATEMENT_TERMINATOR = 7;
    static final int ERROR = -1;

    int state = 0;
    String token = "";
    ArrayList<String> tokens = new ArrayList<String>();
    ArrayList<String> tokentypes = new ArrayList<String>();

    static int[][] dfatable = {
        {START, IDENTIFIER, NUMBER, STRING_LITERAL_OPEN, ERROR, SPECIAL_CHAR, STATEMENT_TERMINATOR, OPERATOR, ERROR},
        {START, IDENTIFIER, IDENTIFIER, ERROR, IDENTIFIER, SPECIAL_CHAR, STATEMENT_TERMINATOR, OPERATOR, ERROR},
        {START, ERROR, NUMBER, ERROR, ERROR, SPECIAL_CHAR, STATEMENT_TERMINATOR, OPERATOR, ERROR},
        {START, IDENTIFIER, NUMBER, STRING_LITERAL_OPEN, ERROR, SPECIAL_CHAR, ERROR, OPERATOR, ERROR},
        {STRING_LITERAL_OPEN, STRING_LITERAL_OPEN, STRING_LITERAL_OPEN, STRING_LITERAL_CLOSE, STRING_LITERAL_OPEN, STRING_LITERAL_OPEN, STRING_LITERAL_OPEN, STRING_LITERAL_OPEN, STRING_LITERAL_OPEN},
        {START, ERROR, ERROR, ERROR, ERROR, SPECIAL_CHAR, STATEMENT_TERMINATOR, OPERATOR, ERROR},
        {START, IDENTIFIER, NUMBER, STRING_LITERAL_OPEN, ERROR, SPECIAL_CHAR, STATEMENT_TERMINATOR, ERROR, ERROR},
        {START, IDENTIFIER, NUMBER, STRING_LITERAL_OPEN, ERROR, SPECIAL_CHAR, ERROR, ERROR, ERROR}
    };

    // 0 : NO UPDATE, 1 : update token, 2 : push, new token start, 3 : push, token init
    static int[][] accepttable = {
        {0, 1, 1, 1, 0, 1, 1, 1, 0},
        {3, 1, 1, 0, 1, 2, 2, 2, 0},
        {3, 0, 1, 0, 0, 2, 2, 2, 0},
        {3, 2, 2, 2, 0, 2, 0, 1, 0},
        {1, 1, 1, 1, 1, 1, 1, 1, 1},
        {3, 0, 0, 0, 0, 2, 2, 2, 0},
        {3, 2, 2, 2, 0, 2, 2, 0, 0},
        {3, 2, 2, 2, 0, 2, 0, 0, 0}
    };
    

    static ArrayList<String> keyword = new ArrayList<String>(Arrays.asList(
        "program", "program_begin", "program_end", 
        "begin", "end", "if", "elseif", "else", "break", "for",
        "integer", "display", "while", "continue"
    ));

    static ArrayList<String> operator = new ArrayList<String>(Arrays.asList("=", "<", ">", "*", "+", "-", "/", "%"));
    
    static ArrayList<String> special_char = new ArrayList<String>(Arrays.asList(",", "(", ")"));
    
    public ArrayList<String> getTokens() {
        return tokens;
    }

    public ArrayList<String> getTokentypes() {
        return tokentypes;
    }

    public int getInputType(char c) {
        if(Character.toString(c).isBlank()) { // whitespace
            return 0;
        } else if((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) { // Letter
            return 1;
        } else if(c >= '0' && c <= '9') {
            return 2;
        } else if(c == '"') {
            return 3;
        } else if(c == '_') {
            return 4;
        } else if(special_char.contains(Character.toString(c))) {
            return 5;
        } else if(c == ';') {
            return 6;
        } else if(operator.contains(Character.toString(c))) {
            return 7;
        } else if(c == '.') {
            return 8;
        } else {
            return 9;
        }
    }

    public void addSymbol(int type) {
        tokens.add(token);
        if(type == IDENTIFIER) {
            if(keyword.contains(token)) {
                tokentypes.add("KEYWORD");
            } else {
                tokentypes.add("IDENTIFIER");
            }
        } else if(type == NUMBER) {
            tokentypes.add("NUMBER");
        } else if(type == OPERATOR) {
            tokentypes.add("OPERATOR");
        } else if(type == STRING_LITERAL_CLOSE) {
            tokentypes.add("STRING_LITERAL");
        } else if(type == SPECIAL_CHAR) {
            tokentypes.add("SPECIAL_CHAR");
        } else if(type == STATEMENT_TERMINATOR) {
            tokentypes.add("STATEMENT_TERMINATOR");
        } else if(type == -1) {
            tokentypes.add("EOF");
        }
    }

    public void printTable() {
        int size = tokentypes.size();
        for(int i = 0; i < size; i ++) {
            System.out.println(tokentypes.get(i) + " : " + tokens.get(i));
        }
    }

    public int doScan(int ch) {
        if(ch == -1) {
            addSymbol(state);
            token = "$";
            addSymbol(-1);
            return 0;
        }
        char c = (char) ch;

        int inputType = getInputType(c);
        if(inputType == 9) {
            System.out.println("not valid input : " + c);
            return -1;
        }
        int newState = dfatable[state][inputType];
        if(newState == ERROR) {
            System.out.println("Spelling error : " + token + c);
            return -1;
        }
        int acceptable = accepttable[state][inputType];

        if(acceptable == 1) {
            token += c;
        } else if(acceptable == 2) {
            addSymbol(state);
            token = Character.toString(c);
        } else if(acceptable == 3) {
            addSymbol(state);
            token = "";
        } else {
            acceptable = 4;
        }
        
        state = newState;
        return acceptable;
    }

    public static void run(String[] args) {

        String filename = args[0];
        SmallScan ss = new SmallScan();
        int validInput;

        try {
            File file = new File(filename);
            FileReader filereader = new FileReader(file);
            int singleCh = 0;
            while(true) {
                singleCh = filereader.read();
                validInput = ss.doScan(singleCh);
                if(validInput == 0) {
                    break;
                } else if(validInput == -1) {
                    System.out.println("ERROR");
                    break;
                }
            }
            filereader.close();

            if(validInput >= 0) {
                ss.printTable();
            }

        } catch (FileNotFoundException e) {
            System.out.println(e);
        } catch(IOException e){
            System.out.println(e);
        }
    }

}
