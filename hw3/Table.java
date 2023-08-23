import java.util.ArrayList;
import java.util.Arrays;

class Symbol {
    String id;
    String value;
    String type;

    Symbol(String id, String value, String type) {
        this.id = id;
        this.value = value;
        this.type = type;
    }
}

public class Table {

    static ArrayList<String> stateList = new ArrayList<String>(Arrays.asList(        
        "<program>", "<block>", "<statements>",  "<statement>",  "<variable_declaration>", "<variables>", "<variable>", 
        "<variable_optional>", "<variable_init>", "<variable_type>", "<arithmetic_expression>", "<operation>", 
        "<binary_arithmetic_operation>", "<unary_arithmetic_operation>", "<term>", "<arithmetic_statement>", "<selection_statement>",
        "<if_statement>", "<elseif_statement>", "<else_statement>",
        "<loop_statement>", "<while_statement>", "<for-loop_statement>", "<keyword_statement>", "<control_keyword>", "<function_call>",
        "<functions>", "<parameter>", "<conditional_statement>", "<condition_operation>"
    ));

    static ArrayList<String> inputList = new ArrayList<String>(Arrays.asList(        
        "program", "IDENTIFIER", "program_begin", "program_end", "$", ";", ",", "=", "integer", 
        "+", "-", "*", "/", "%", "++", "--", "NUMBER", "if", "(", ")", "begin", "end", "elseif",
        "else", "while", "for", "break", "continue", "display", "STRING_LITERAL", "==", "!=", "<=", ">=", "<", ">"
    ));

    static ArrayList<String> bnfList = new ArrayList<String>(Arrays.asList(
        "", // error
        /* 1 */ "program IDENTIFIER program_begin <block> program_end",
        "<statements>",
        "<statement> <statements>",
        "<arithmetic_statement>",
        "<variable_declaration>",
        "<selection_statement>",
        "<loop_statement>",
        "<keyword_statement>",
        "<function_call>",
        /* 10 */"<variable_type> <variables> ;",
        "<variable> <variable_optional>",
        "IDENTIFIER <variable_init>",
        ", <variables>",
        "= <arithmetic_expression>",
        "integer",
        "<term> <operation>",
        "<binary_arithmetic_operation> <arithmetic_expression>",
        "<unary_arithmetic_operation>",
        "+",
        /* 20 */"-",
        "*",
        "/",
        "%",
        "++",
        "--",
        "IDENTIFIER",
        "NUMBER",
        "IDENTIFIER = <arithmetic_expression> ;",
        "<if_statement> <elseif_statement> <else_statement>",
        /* 30 */ "if ( <conditional_statement> ) begin <block> end",
        "elseif ( <conditional_statement> ) begin <block> end <elseif_statement>",
        "else begin <block> end",
        "<while_statement>",
        "<for-loop_statement>",
        "while ( <conditional_statement> ) begin <block> end",
        "for ( <variable_declaration> <conditional_statement> ; <arithmetic_expression> ) begin <block> end",
        "<control_keyword> ;",
        "break",
        "continue",
        /* 40 */ "<functions> ( <parameter> ) ;",
        "display",
        "STRING_LITERAL",
        "<arithmetic_expression> <condition_operation> <arithmetic_expression>",
        "==", 
        "!=", 
        "<=", 
        ">=", 
        "<", 
        ">",
        "" /* 50 */
    ));

    static ArrayList<String> errorMessageList = new ArrayList<String>(Arrays.asList(
        "keyword not matched", 
        "unavailable statement",
        "declaration keyword not matched", 
        "declaration keyword missing", 
        "begin keyword missing", 
        "end keyword missing",
        "conditional statement is bad"
    ));

    static int[][] parsingTable = {
        /* 
        program	| IDENTIFIER | program_begin | program_end | $ | ; | , | = | integer | + | -(10) | * | / | % | ++ | -- | NUMBER | if | ( | ) | begin(20) | end | elseif | else | while | for | break | continue | display | STRING_LITERAL | ==(30) | != | <= | >= | < | >
        */
        /*  <program> */                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        /* <block> */                   {-1, 2, -1, 2, -1, 0, -1, -1, 2, -1, -1, -1, -1, -1, -1, -1, -1, 2, -1, -1, -1, 2, -1, -1, 2, 2, 2, 2, 2, -1, -1, -1, -1, -1, -1, -1},
        /* <statements> */              {-1, 3, -1, 50, -1, -1, -1, -1, 3, -1, -1, -1, -1, -1, -1, -1, -1, 3, -1, -1, -1, 50, -1, -1, 3, 3, 3, 3, 3, -1, -1, -1, -1, -1, -1, -1},
        /* <statement> */               {-1, 4, -1, -1, -1, -1, -1, -1, 5, -1, -1, -1, -1, -1, -1, -1, -1, 6, -1, -1, -1, -1, -1, -1, 7, 7, 8, 8, 9, -1, -1, -1, -1, -1, -1, -1},
        /*  <variable_declaration> */   {0, 0, 0, 0, 0, 0, 0, 0, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        /*  <variables> */              {0, 11, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        /*  <variable> */               {0, 12, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        /*  <variable_optional> */      {0, 0, 0, 0, 0, 50, 13, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        /*  <variable_init> */          {0, 0, 0, 0, 0, 50, 50, 14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        /*  <variable_type> */          {0, 0, 0, 0, 0, 0, 0, 0, 15, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        /*  <arithmetic_expression> */  {0, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        /*  <operation> */              {0, 0, 0, 0, 0, 50, 50, 0, 0, 17, 17, 17, 17, 17, 18, 18, 0, 0, 0, 50, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 50, 50, 50, 50, 50, 50},
        /*  <bin_arith_op> */           {0, 0, 0, 0, 0, 0, 0, 0, 0, 19, 20, 21, 22, 23, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        /*  <unary_arith_op> */         {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 25, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        /*  <term> */                   {0, 26, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 27, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        /*  <arithmetic_statement> */   {0, 28, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        /*  <selection_statement> */    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 29, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        /*  <if_statement> */           {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 30, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        /*  <elseif_statement> */       {0, 50, 0, 50, 0, 0, 0, 0, 50, 0, 0, 0, 0, 0, 0, 0, 0, 50, 0, 0, 0, 50, 31, 50, 50, 50, 50, 50, 50, 0, 0, 0, 0, 0, 0, 0},
        /*  <else_statement> */         {0, 50, 0, 50, 0, 0, 0, 0, 50, 0, 0, 0, 0, 0, 0, 0, 0, 50, 0, 0, 0, 50, 0, 32, 50, 50, 50, 50, 50, 0, 0, 0, 0, 0, 0, 0},
        /*  <loop_statement> */         {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 33, 34, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        /*  <while_statement> */        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 35, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        /*  <for-loop_statement> */     {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 36, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        /*  <keyword_statement> */      {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 37, 37, 0, 0, 0, 0, 0, 0, 0, 0},
        /*  <control keyword> */        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 38, 39, 0, 0, 0, 0, 0, 0, 0, 0},
        /*  <function_call> */          {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 40, 0, 0, 0, 0, 0, 0, 0},
        /*  <functions> */              {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 41, 0, 0, 0, 0, 0, 0, 0},
        /*  <parameter> */              {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 50, 0, 0, 0, 0, 0, 0, 0, 0, 0, 42, 0, 0, 0, 0, 0, 0},
        /*  <conditional_stmt> */       {0, 43, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 43, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        /*  <condition_op> */           {0, 0, 0, 0, 0, -6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 44, 45, 46, 47, 48, 49}
    };

    public static ArrayList<Symbol> symbolList = new ArrayList<Symbol>(Arrays.asList(
        new Symbol("program", "program", "IDENTIFIER"), new Symbol("program_begin", "program_begin", "IDENTIFIER"), new Symbol("program_end", "program_end", "IDENTIFIER"),
        new Symbol("begin", "begin", "IDENTIFIER"), new Symbol("end", "end", "IDENTIFIER"), new Symbol("if", "if", "IDENTIFIER"),
        new Symbol("elseif", "elseif", "IDENTIFIER"), new Symbol("else", "else", "IDENTIFIER"), new Symbol("break", "break", "IDENTIFIER"),
        new Symbol("for", "for", "IDENTIFIER"), new Symbol("integer", "integer", "IDENTIFIER"), new Symbol("display", "display", "IDENTIFIER"), new Symbol("while", "while", "IDENTIFIER"),
        new Symbol("continue", "continue", "IDENTIFIER")
     ));
    
}
