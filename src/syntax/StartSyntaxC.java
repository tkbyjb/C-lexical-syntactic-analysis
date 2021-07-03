package syntax;

import java.io.File;

import lex.LexC;
import lex.StartLexC;

public class StartSyntaxC {
    //运行词法和语法分析
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        String inFile = "test/inFile.txt";
        String outFile = "test/outFile.txt";
        LexC.lex(new File(inFile), new File(outFile));
        long endTime = System.currentTimeMillis();
        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
        startTime = System.currentTimeMillis();
        SyntaxC.syntax(new File(outFile));
        endTime = System.currentTimeMillis();
        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");

    }

}
