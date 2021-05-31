package lex;
import java.io.*;
public class StartLexC {

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		String inFile="test/inFile.txt";
		String outFile="test/outFile.txt";

		LexC.lex(new File(inFile), new File(outFile));
		long endTime = System.currentTimeMillis();
		System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
	}

}
