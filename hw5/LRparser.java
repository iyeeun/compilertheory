import java.util.Stack;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;

public class LRparser {

    static int ok = -1;
    static SmallScan sc = new SmallScan();
    static Stack<String> parsingStack = new Stack<>();
    static int inputIdx = 0;
    static ParsingTable parsingTable = new ParsingTable();
    static ArrayList<String> bao = new ArrayList<String>(Arrays.asList("+", "-", "*", "/", "%", "+="));
    static ArrayList<String> uao = new ArrayList<String>(Arrays.asList("++", "--"));
    static ArrayList<String> co = new ArrayList<String>(Arrays.asList("==", "!=", "<=", ">=", "<", ">"));

    void doScan(String filename) {
        int validInput = 0;
        try {
            File file = new File(filename);
            FileReader filereader = new FileReader(file);
            int singleCh = 0;
            while(true) {
                singleCh = filereader.read();
                validInput = sc.doScan(singleCh);
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
    }

    void doParse() {
        parsingStack.push("0");

        while(!parsingStack.empty()) {
            System.out.println(parsingStack);

            int curState = Integer.parseInt(parsingStack.peek());
            String curInput = sc.tokens.get(inputIdx);
            // System.out.println(curInput);
            // System.out.println();

            // pre-processing
            if(sc.getTokentypes().get(inputIdx).equals("IDENTIFIER")) {
                curInput = "IDENTIFIER";
            } else if(sc.getTokentypes().get(inputIdx).equals("STRING_LITERAL")) {
                curInput = "STRING_LITERAL";
            } else if(sc.getTokentypes().get(inputIdx).equals("NUMBER")) {
                curInput = "NUMBER";
            } else if(sc.getTokentypes().get(inputIdx).equals("EOF")) {
                curInput = "$";
            } else if(bao.contains(sc.getTokens().get(inputIdx))) {
                curInput = "BAO";
            }  else if(uao.contains(sc.getTokens().get(inputIdx))) {
                curInput = "UAO";
            }  else if(co.contains(sc.getTokens().get(inputIdx))) {
                curInput = "CO";
            } 

            int curAction = ParsingTable.table[curState][ParsingTable.inputList.indexOf(curInput)];

            if(curAction == 1000) {
                // ACCEPT
                ok = 1;
                System.out.println("Parsing OK");
                break;
            } else if(curAction <= -1000) {
                // ERROR
                if(curAction == ParsingTable.keywordError) {
                    System.out.println("Illegal keyword");
                } else if(curAction == ParsingTable.declareError) {
                    System.out.println("Declaration error");
                } else if(curAction == ParsingTable.missingError) {
                    System.out.println("Keyword missing error");
                } else if(curAction == ParsingTable.condError) {
                    System.out.println("Conditional syntax error");
                } else if(curAction == ParsingTable.terminatorError) {
                    System.out.println("Missing ; error");
                }
                ok = 0;
                break;
            } else if(curAction > 0) {
                // SHIFT
                parsingStack.push(curInput);
                inputIdx ++;
                parsingStack.push(Integer.toString(curAction));
            } else if(curAction < 0) {
                // REDUCTION
                int popNum = 2 * ParsingTable.ruleLength[-curAction];
                for(int i = 0; i < popNum; i ++) {
                    parsingStack.pop();
                }
                curState = Integer.parseInt(parsingStack.peek());
                String reduce = ParsingTable.reductList.get(-curAction);
                parsingStack.push(reduce);
                // GOTO
                int newState = ParsingTable.table[curState][ParsingTable.inputList.indexOf(reduce)];
                if(newState < 0) {
                    ok = 0;
                    System.out.println(curState + " " + reduce + " GOTO error");
                    break;
                } else {
                    parsingStack.push(Integer.toString(newState));
                }
            }
        }

        if(inputIdx != sc.tokens.size()-2 && !sc.tokens.get(inputIdx).equals("$")) {
            ok = 0;
        }

        if(ok == 1) {
            // System.out.println("Parsing OK");
        } else if(ok == 0) {
            System.out.println("Parsing Failed");
        } else {
            System.out.println("Parsing Undefined");
        }

    }

    static void run(String[] args) {
        LRparser parser = new LRparser();
        parser.doScan(args[0]);
        parser.doParse();
    }

    public static void main(String[] args) {
        LRparser.run(args);
    }

} 