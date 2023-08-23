import java.util.Stack;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;

public class LR0parser {

    class Rule {
        String lhs = new String();
        ArrayList<String> rhs = new ArrayList<String>();

        Rule(String lhs, ArrayList<String> rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
        }
    }

    static int ok = -1;
    static Stack<String> parsingStack = new Stack<>();
    static ArrayList<String> inputList = new ArrayList<String>();
    static int inputIdx = 0;
    static final HashMap<String, Integer> inputMap = new HashMap<String, Integer>(){{
        put("(", 0);
        put("a", 1);
        put(")", 2);
        put("A", 3);
        put("$", 4);
    }};
    static final int[][] parsingTable = {
        {3, 2, -1, 1, -1},
        {-1, -1, -1, -1, -1}, 
        {-1, -1, -1, -1, -1}, 
        {3, 2, -1, 4, -1},
        {-1, -1, 5, -1, -1},
        {-1, -1, -1, -1, -1}
    };
    static final int stateAction[] = {0, 1, 1, 0, 0, 1}; // 0 : shift, 1 : reduce
    static HashMap<Integer, Rule> rules = new HashMap<Integer, Rule>();

    LR0parser() {
        rules.put(1, new Rule("S", new ArrayList<String>(Arrays.asList("A"))));
        rules.put(2, new Rule("A", new ArrayList<String>(Arrays.asList("a"))));
        rules.put(5, new Rule("A", new ArrayList<String>(Arrays.asList("(", "A", ")"))));
    }

    void doScan(String filename) {
        try {
            File file = new File(filename);
            FileReader filereader = new FileReader(file);
            int singleCh = 0;
            while(true) {
                singleCh = filereader.read();
                if(singleCh == -1) {
                    inputList.add("$");
                    break;
                }
                char c = (char) singleCh;
                if(c == '(' || c == ')' || c == 'a') {
                    inputList.add(Character.toString(c));
                } else {
                    System.out.println("Invalid input");
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
        parsingStack.push("$");
        parsingStack.push("0");

        while(!parsingStack.empty()) {
            System.out.println(parsingStack);

            String curInput = inputList.get(inputIdx);

            int curState = Integer.parseInt(parsingStack.peek());
            int curAction = stateAction[curState];

            if(curAction == 0) {
                parsingStack.push(curInput);
                inputIdx ++;
                int nextState = parsingTable[curState][inputMap.get(curInput)];
                if(nextState == -1) {
                    ok = 0;
                    break;
                } else {
                    parsingStack.push(Integer.toString(nextState));
                }
            } else {
                int popNum = 2 * rules.get(curState).rhs.size();
                String reduce = rules.get(curState).lhs;
                for(int i = 0; i < popNum; i ++) {
                    parsingStack.pop();
                }
                curState = Integer.parseInt(parsingStack.peek());
                if(curState == 0 && reduce.equals("S")) {
                    ok = 1;
                    while(!parsingStack.empty()) {
                        parsingStack.pop();
                    }
                    continue;
                } else if(reduce.equals("S")) {
                    ok = 0;
                    break;
                }
                parsingStack.push(reduce);
                int nextState = parsingTable[curState][inputMap.get(reduce)];
                if(nextState == -1) {
                    ok = 0;
                    break;
                } else {
                    parsingStack.push(Integer.toString(nextState));
                }
            }
        }

        if(inputIdx != inputList.size()-1) {
            ok = 0;
        }

        if(ok == 1) {
            System.out.println("Parsing OK");
        } else if(ok == 0) {
            System.out.println("Parsing Failed");
        } else {
            System.out.println("Parsing Undefined");
        }

    }

    static void run(String[] args) {
        LR0parser parser = new LR0parser();
        parser.doScan(args[0]);
        parser.doParse();
    }

    public static void main(String[] args) {
        LR0parser.run(args);
    }

} 