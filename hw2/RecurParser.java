import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.Arrays;
import java.util.ArrayList;

public class RecurParser {

    static SmallScan scanner = new SmallScan();
    int tokenidx;
    int maxtokenidx;
    String token;
    String tokentype;

    static ArrayList<String> datatype = new ArrayList<String>(Arrays.asList("integer", "float", "double", "long"));
    static ArrayList<String> controlKeywords = new ArrayList<String>(Arrays.asList("break", "continue"));
    static ArrayList<String> conditionOperations = new ArrayList<String>(Arrays.asList("==", "!=", "<", ">", "<=", ">="));
    static ArrayList<String> arithmeticOperations = new ArrayList<String>(Arrays.asList("+", "-", "*", "/", "%"));
    static ArrayList<String> functions = new ArrayList<String>(Arrays.asList("display"));
    
    void getToken() {
        if(tokenidx < maxtokenidx) {
            tokenidx ++;
            token = scanner.tokens.get(tokenidx);
            tokentype = scanner.tokentypes.get(tokenidx);
        }
    }

    void printError(int type) {
        // System.out.println(tokenidx + " " + token);
        if(type == 0) {
            System.out.println("Parsing Error : Wrong Keyword");
        } else if(type == 1) {
            System.out.println("Parsing Error : Wrong identifier");
        } else if(type == 2) {
            System.out.println("Parsing Error : Wrong parenthesis");
        } else if(type == 3) {
            System.out.println("Parsing Error : Missing keyword");
        } else if(type == 4) {
            System.out.println("Parsing Error : Missing statement terminator");
        } else if(type == 5) {
            System.out.println("Parsing Error : Wrong expression");
        } else if(type == 6) {
            System.out.println("Parsing Error : Wrong operation");
        }
    }

    public Boolean match(String expected) {
        if(scanner.tokens.get(tokenidx).equals(expected)) {
            getToken();
            return true;
        } else {
            return false;
        }
    }

    public Boolean checkProgram() {
        if(!match("program")) {
            printError(0); 
            return false;
        }
        if(tokentype.equals("IDENTIFIER")) {
            getToken();
        } else {
            printError(1);
            return false;
        }
        if(!match("program_begin")) {
            printError(0);
            return false;
        }
        if(!checkBlocks("program_begin")) {
           return false;
        }
        if(!match("program_end")) {
            printError(0);
            return false;
        }
        return true;
    }

    public Boolean checkTerm() {
        if(tokentype.equals("NUMBER")) return true;
        if(tokentype.equals("IDENTIFIER")) return true;
        return false;
    }

    public Boolean checkArithmeticExpression() {
        if(!checkTerm()) {
            printError(5);
            return false;
        }
        getToken();
        while(arithmeticOperations.contains(token)) {
            match(token);
            if(!checkTerm()) {
                printError(5);
                return false;
            }
            getToken();
        }
        return true;
    }

    public Boolean checkCondition() {
        if(!checkArithmeticExpression()) {
            printError(5);
            return false;
        }
        if(conditionOperations.contains(token)) {
            match(token);
        } else {
            printError(6);
            return false;
        }
        if(!checkArithmeticExpression()) {
            printError(5);
            return false;
        }
        return true;
    }

    public Boolean checkArithmeticStatement() {
        if(tokentype.equals("IDENTIFIER")) {
            match(token);
        }
        if(!match("=")) {
            printError(6);
            return false;
        }
        if(!checkArithmeticExpression()) {
            printError(5);
            return false;
        }
        if(!tokentype.equals("STATEMENT_TERMINATOR")) {
            if(token.equals(",")) {
                printError(3);
            } else {
                printError(4);
            }
            return false;
        }
        getToken();
        return true;
    }

    public Boolean checkVariable() {
        if(!tokentype.equals("IDENTIFIER")) {
            printError(1);
            return false;
        }
        match(token);
        if(token.equals("=")) {
            match("=");
            if(!checkArithmeticExpression()) {
                printError(5);
                return false;
            }
        }
        return true;
    }

    public Boolean checkIdentifierDeclaration() {
        if(datatype.contains(token)) {
            match(token);
        }
        if(!checkVariable()) {
            printError(5);
            return false;
        }
        while(token.equals(",")) {
            getToken();
            if(!checkVariable()) {
                printError(5);
                return false;
            }
        }
        if(!tokentype.equals("STATEMENT_TERMINATOR")) {
            printError(4);
            return false;
        }
        getToken();
        return true;
    }

    public Boolean checkBlocks(String start) {
        String end;
        if(start.equals("program_begin")) {
            end = "program_end";
        } else if(start.equals("begin")) {
            end = "end";
        } else {
            System.out.println("Wrong input for checkBlocks");
            return false;
        }

        while(!token.equals(end)) {
            if(tokenidx >= maxtokenidx) {
                printError(3);
                return false;
            }

            if(tokentype.equals("IDENTIFIER")) {
                // arithmetic
                if(!checkArithmeticStatement()) {
                    return false;
                }
            } else if(token.equals("if")) {
                // selection statement
                if(!checkIf()) {
                    return false;
                }
            } else if(token.equals("while")) {
                // while statement
                if(!checkWhile()) {
                    return false;
                }
            } else if(datatype.contains(token)) {
                // declaration
                if(!checkIdentifierDeclaration()) {
                    return false;
                }
            } else if(controlKeywords.contains(token)) {
                // keywords
                match(token);
                if(!tokentype.equals("STATEMENT_TERMINATOR")) {
                    printError(4);
                    return false;
                }
                getToken();
            } else if(functions.contains(token)) {
                // function call
                if(!checkFunctionCall()) return false;
            } else {
                printError(3);
                return false;
            }
        }
        return true;
    }

    public Boolean checkConditionalStatement(String in, Boolean cond) {
        match(in);
        if(cond) {
            if(!match("(")) {
                printError(2);
                return false;
            }
            if(!checkCondition()) {
                return false;
            }
            if(!match(")")) {
                printError(2);
                return false;
            }
        }
        if(!match("begin")) {
            printError(0);
            return false;
        }
        if(!checkBlocks("begin")) {
            return false;
        }
        if(!match("end")) {
            return false;
        }
        return true;
    }

    public Boolean checkIf() {
        if(!checkConditionalStatement("if", true)) return false;
        while(token.equals("elseif")) {
            if(!checkConditionalStatement("elseif", true)) return false;
        }
        if(token.equals("else")) {
            if(!checkConditionalStatement("else", false)) return false;
        }
        return true;
    }

    public Boolean checkWhile() {
        return checkConditionalStatement("while", true);
    }

    public Boolean checkFunctionCall() {
        String func = token;
        Boolean res = false;
        getToken();
        if(!match("(")) {
            printError(2);
            return false;
        }
        if(func.equals("display")) {
            while(tokenidx < maxtokenidx) {
                if(token.equals(")")) {
                    res = true;
                    getToken();
                    break;
                } else if(!tokentype.equals("STRING_LITERAL")) {
                    return false;
                }
                getToken();
            }
            if(!tokentype.equals("STATEMENT_TERMINATOR")) {
                printError(4);
                return false;
            }
            getToken();
        }
        return res;
    }

    public void doRecurParse(String filename) {
        
        int validInput;

        try {
            File file = new File(filename);
            FileReader filereader = new FileReader(file);
            int singleCh = 0;
            while(true) {
                singleCh = filereader.read();
                validInput = scanner.doScan(singleCh);
                if(validInput == 0) break;
                else if(validInput == -1) {
                    System.out.println("SPELLING ERROR");
                    break;
                }
            }
            filereader.close();
        } catch (FileNotFoundException e) {
            System.out.println(e);
        } catch(IOException e){
            System.out.println(e);
        }


        tokenidx = 0;
        maxtokenidx = scanner.tokens.size();
        token = scanner.tokens.get(tokenidx);
        tokentype = scanner.tokentypes.get(tokenidx);
        // System.out.println("TOTAL " + maxtokenidx + " TOKENS");
        checkProgram();
    }

    public static void main(String[] args) {
        String filename = args[0];
        RecurParser rp = new RecurParser();
        rp.doRecurParse(filename);

    }

}