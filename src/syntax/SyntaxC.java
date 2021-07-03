package syntax;

import java.io.*;

public class SyntaxC {
    private static BufferedReader in; //词法分析结果文件缓冲流
    private static String line = null; //当前单词的信息串
    private static String wordType;  //当前单词类别
    private static int lineNum;  //当前单词所在源代码行数
    private static String word;  //当前单词

    private static boolean scan() {  //读取下一个单词
        try {
            line = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (line == null) {
            wordType = null;
            return false;
        } else {
            wordType = line.substring(line.indexOf("\t<") + 2, line.indexOf(" : "));
            lineNum = Integer.parseInt(line.substring(line.indexOf(">\t") + 2, line.length()));
            word = line.substring(0, line.indexOf("\t<"));
            return true;
        }
    }

    private static void error(String s) { //输出错误信息并中止程序
        System.out.println("error : 第" + lineNum + "行 : " + " " + word + s);
        System.exit(0);
    }

    private static void error() { //输出错误信息并中止程序
        System.out.println("error : 第" + lineNum + "行 : " + " " + word + "附近");
        System.exit(0);
    }

    private static void wordEquals(String s) { //终结符判断并读取
        if (wordType.equals(s)) scan();
        else error();
    }

    /*
     * 识别到第一个语法错误就停止运行,不能一次识别全部语法错误
     * 用中文方法名
     */
    public static void syntax(File inFile) {
        try {
            in = new BufferedReader(new FileReader(inFile));
            if (scan()) {
                外部语句列表();
                System.out.println("语法正确");
            } else {
                System.out.println("没有可分析数据");
            }
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void 外部语句列表() {
        外部语句();
        if (wordType != null) {
            外部语句列表();
        }
    }

    private static void 外部语句() {
        数据类型();
        函数定义和变量声明语句();
    }

    private static void 数据类型() {
        if (wordType.equals("char") || wordType.equals("int") ||
                wordType.equals("long") || wordType.equals("short") ||
                wordType.equals("float") || wordType.equals("double")) {
            scan();
        } else error();
    }

    private static void 函数定义和变量声明语句() {
        wordEquals("标识符");
        if (wordType.equals("(")) {
            scan();
            形参列表();
            wordEquals(")");
            复合语句();
        } else {
            变量声明部分();
        }
    }

    private static void 变量声明部分() {
        if (wordType.equals(";")) {
            scan();
        } else if (wordType.equals("=")) {
            scan();
            表达式();
            wordEquals(";");
        } else error();
    }

    /*
    private static void 变量声明语句() {
        数据类型();
        wordEquals("标识符");
        变量声明部分();
    }*/
    private static void 形参列表() {
        if (wordType.equals(")")) {//为空情况
            return;
        }
        数据类型();
        wordEquals("标识符");
        if (wordType.equals(",")) {
            scan();
            形参列表();
        }
    }

    private static void 复合语句() {
        wordEquals("{");
        内部语句列表();
        wordEquals("}");
    }

    private static void 表达式() {
        if (wordType.equals("标识符")) {
            scan();
            if (wordType.equals("(")) {//函数调用
                scan();
                表达式列表();
                wordEquals(")");
            }
        } else if (wordType.equals("整数") || wordType.equals("浮点数") || wordType.equals("字符") || wordType.equals("字符串")) {
            scan();
        } else if (wordType.equals("!") || wordType.equals("-") || wordType.equals("&") || wordType.equals("--") ||
                wordType.equals("++")) {
            scan();
            表达式();
        } else if (wordType.equals("(")) {
            scan();
            表达式();
            wordEquals(")");
        } else error();
        if (wordType.equals("+") || wordType.equals("-") || wordType.equals("*") || wordType.equals("/") ||
                wordType.equals("<<") || wordType.equals(">>") || wordType.equals(">") ||
                wordType.equals("<") || wordType.equals(">=") || wordType.equals("<=") ||
                wordType.equals("==") || wordType.equals("!=") || wordType.equals("&") ||
                wordType.equals("&") || wordType.equals("&&") || wordType.equals("||") || wordType.equals("=") ||
                wordType.equals("+=") || wordType.equals("%")) {
            scan();
            表达式();
        }
		/*
		else {
			表达式();
			if(wordType.equals("+")||wordType.equals("-")||wordType.equals("*")||wordType.equals("/")||
					wordType.equals("<<")||wordType.equals(">>")||wordType.equals(">")||
					wordType.equals("<")||wordType.equals(">=")||wordType.equals("<=")||
					wordType.equals("==")||wordType.equals("!=")||wordType.equals("&")||
					wordType.equals("&")||wordType.equals("&&")||wordType.equals("||")) {
				scan();
			}
			else {System.out.println("aa");error();}
			表达式();
		}*/
    }

    private static void 内部语句列表() {
        内部语句();
        if (wordType.equals("}") == false) {
            内部语句列表();
        }
    }

    private static void 内部语句() {
        if (wordType.equals("if") || wordType.equals("while")) {
            scan();
            while语句();
            if (wordType.equals("else")) {
                wordEquals("else");
                内部语句();
            }
        } else if (wordType.equals("do")) {
            scan();
            dowhile语句();
        } else if (wordType.equals("for")) {
            scan();
            for语句();
        } else if (wordType.equals("continue") || wordType.equals("break")) {
            scan();
            wordEquals(";");
        } else if (wordType.equals("return")) {
            scan();
            if (wordType.equals(";")) {
                scan();
            } else {
                表达式();
                wordEquals(";");
            }
        } else if (wordType.equals("{")) {
            复合语句();
        } else if (wordType.equals("标识符")) {
            scan();
            if (wordType.equals("(")) {
                表达式();
                wordEquals(";");
            } else {
                赋值语句();
            }
        } else {
            外部语句();
        }
    }

    private static void 表达式列表() {
        表达式();
        if (wordType.equals(",")) {
            表达式列表();
        }
    }

    private static void while语句() {
        wordEquals("(");
        表达式();
        wordEquals(")");
        内部语句();
    }

    private static void dowhile语句() {
        内部语句();
        wordEquals("while");
        wordEquals("(");
        表达式();
        wordEquals(")");
        wordEquals(";");
    }

    private static void for语句() {
        wordEquals("(");
        if (wordType.equals(";") == false) {
            表达式();
        }
        wordEquals(";");
        if (wordType.equals(";") == false) {
            表达式();
        }
        wordEquals(";");
        if (wordType.equals(")") == false) {
            表达式();
        }
        wordEquals(")");
        内部语句();
    }

    public static void 赋值语句() {
        //wordEquals("标识符");System.out.println(wordType);
        if (wordType.equals("=") || wordType.equals("+=") || wordType.equals("-=") || wordType.equals("/=") ||
                wordType.equals("*=") || wordType.equals("%=")) {
            scan();
            表达式();
            wordEquals(";");
        } else error();
    }

    public static void 表达语句() {
        表达式();
        wordEquals(";");
    }
}
