import java.util.Stack;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class LLparser {

    static SmallScan sc = new SmallScan();
    static Stack<String> parsingStack = new Stack<>();
    static int tokenidx = 0;

    void scan(String filename) {
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

    public void LLparse() {

        boolean parsingOK = true;
        
        parsingStack.push("$");
        parsingStack.push("<program>"); // Starting symbol
        /* non-terminal : <example_state>, else terminal */
        String parsingStackTop;
        String nextInputToken;

        while(true) {
            parsingStackTop = parsingStack.peek();
            nextInputToken = sc.getTokens().get(tokenidx);

            if(sc.getTokentypes().get(tokenidx).equals("IDENTIFIER")) {
                nextInputToken = "IDENTIFIER";
            } else if(sc.getTokentypes().get(tokenidx).equals("STRING_LITERAL")) {
                nextInputToken = "STRING_LITERAL";
            } else if(sc.getTokentypes().get(tokenidx).equals("NUMBER")) {
                nextInputToken = "NUMBER";
            } else if(sc.getTokentypes().get(tokenidx).equals("EOF")) {
                nextInputToken = "$";
            }

            // System.out.println(parsingStackTop + " " + nextInputToken);

            if(parsingStackTop.equals("$") || nextInputToken == "$") {
                break;
            }

            boolean isNonTerminal = (parsingStackTop.startsWith("<") && parsingStackTop.length() > 2);

            if(!isNonTerminal && nextInputToken.equals(parsingStackTop)) {
                tokenidx ++;
                parsingStack.pop();
                System.out.println("[MATCH] " + parsingStackTop);
            } else {
                if(!isNonTerminal) {
                    if(nextInputToken.equals("IDENTIFIER")) {
                        String errorMessage = Table.errorMessageList.get(2);
                        System.out.println("ERROR : " + errorMessage);
                        System.out.println("Parsing Failed");
                    } else if(parsingStackTop.equals(";") && nextInputToken.equals(",")) {
                        String errorMessage = Table.errorMessageList.get(3);
                        System.out.println("ERROR : " + errorMessage);
                        System.out.println("Parsing Failed");
                    } else if(parsingStackTop.equals("begin")) {
                        String errorMessage = Table.errorMessageList.get(4);
                        System.out.println("ERROR : " + errorMessage);
                        System.out.println("Parsing Failed");
                    } else if(parsingStackTop.equals("end")) {
                        String errorMessage = Table.errorMessageList.get(5);
                        System.out.println("ERROR : " + errorMessage);
                        System.out.println("Parsing Failed");
                    } else {
                        System.out.println("ERROR : " + parsingStackTop + " missing");
                        System.out.println("Parsing Failed");
                    }
                    parsingOK = false;
                    break;
                }

                int stateNo = Table.stateList.indexOf(parsingStackTop);
                int inputNo = Table.inputList.indexOf(nextInputToken);
                if(stateNo < 0 || inputNo < 0) {
                    System.out.println("Invalid state or input \'" + parsingStackTop + "\' \'" + nextInputToken + "\'");
                    parsingOK = false;
                    break;
                }

                if(isNonTerminal && Table.parsingTable[stateNo][inputNo] > 0) {
                    parsingStack.pop();
                    String derivation = Table.bnfList.get(Table.parsingTable[stateNo][inputNo]);
                    if(derivation.equals("")) continue;
                    String[] tokens = derivation.split(" ");
                    for(int i = tokens.length-1; i >= 0; i --) {
                        parsingStack.push(tokens[i]);
                    }
                } else {
                    String errorMessage = Table.errorMessageList.get(-Table.parsingTable[stateNo][inputNo]);
                    System.out.println("ERROR : " + errorMessage);
                    System.out.println("Parsing Failed");
                    // System.out.println(parsingStackTop + " " + nextInputToken);
                    parsingOK = false;
                    break;
                }
            }
            System.out.println(Arrays.toString(parsingStack.toArray()));
        }

        if(parsingOK && parsingStack.peek().equals("$") && sc.getTokentypes().get(tokenidx).equals("EOF")) {
            System.out.println("Parsing OK");
        } else if(!sc.getTokentypes().get(tokenidx).equals("EOF")) {
        } else if (!parsingStack.peek().equals("$")) {
            String errorMessage = Table.errorMessageList.get(0);
            System.out.println("ERROR : " + errorMessage);
            System.out.println("Parsing Failed");
        } else {
            System.out.println("Parsing Failed");
        }
        // if(!parsingOK) {
        //     System.out.println(parsingStackTop + " " + nextInputToken);
        // }

    }

    public static void main(String[] args) {
        LLparser parser = new LLparser();
        parser.scan(args[0]);
        parser.LLparse();
    }
    
}
