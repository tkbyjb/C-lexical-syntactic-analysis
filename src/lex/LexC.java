package lex;

import java.io.*;
import java.util.Map;
import java.util.HashMap;

public class LexC {
	private static Map<String,Integer>  type = new HashMap<>();
	static{
		type.put("标识符", 1);
		type.put("auto",2); 	type.put(	"unsigned",	9);	type.put("do",	16);		type.put(	"const",23);	type.put("for",	30);
		type.put("int",	3);		type.put("extern",	10);	type.put(	"goto",	17	);	type.put("char",	24);
		type.put("break",	31);
		type.put("float",	4);	type.put("volatile" ,11);	type.put("if"	,18);		type.put("typedef"	,25);	type.put("case"	,32);
		type.put("double",	5);	type.put("while"	,12);	type.put("else"	,19);		type.put("sizeof"	,26);	type.put("enum"	,33);
		type.put("short",	6);	type.put("switch"	,13);	type.put("return"	,20);	type.put("static"	,27	);
		type.put("long",	7);	type.put("continue"	,14);	type.put("void"	,21);		type.put("default"	,28	);	
		type.put("signed",	8);	type.put("register"	,15);	type.put("struct"	,22);	type.put("union"	,29	);	
		type.put("{",	34);	type.put("("	,36);		type.put("["	,38);		type.put(";"	,40);		type.put(":"	,42);	type.put("."	,74);
		type.put("}",	35);	type.put(")"	,37);		type.put("]"	,39);		type.put(",",41);			type.put("?"	,43);	type.put("->"	,75);
		type.put("+",	44);	type.put("-"	,50);		type.put("*"	,56);		type.put("/"	,60);		type.put("%"	,64);	type.put("!"	,68);
		type.put("++"	,45);	type.put("--"	,51);		type.put("*="	,57);		type.put("/="	,61);		type.put("%="	,65);	type.put("!="	,69);
		type.put("+="	,46);	type.put("-="	,52	);							
		type.put(">"	,47);	type.put("<"	,53);		type.put("&"	,58);		type.put("|"	,62);		type.put("="	,66	);
		type.put(">>"	,48);	type.put("<<"	,54);		type.put("&&"	,59);		type.put("||"	,63);		type.put("=="	,67	);
		type.put(">="	,49);	type.put("<="	,55	);							
		type.put("整数"	,70);	type.put("浮点数",	71);	type.put("字符"	,72);		type.put("字符串"	,73);
		type.put("注释"	,76	);
	}
	private static FileReader in; 
	private static FileWriter out; 
	private static int nowInt; 			//当前字符对应的数值
	private static char now; 			//当前字符
	private static String word=""; 		//单词
	private static int lineNum=1;//当前单词所在行数
	/*
	 * 暂时有个问题:若最后一行为注释且没有换行将无法识别
	 */
	public static void lex(File inFile,File outFile) {
		try {
			LexC.in = new FileReader(inFile);
			LexC.out = new FileWriter(outFile);
			char first;
			while((nowInt=in.read()) != -1) {
				now=(char)nowInt;
				if(now=='\n')lineNum+=1;
				word+=now;
				int i;
				for( i=0;i<word.length();i++) {//去掉word头部的空格
					if(word.charAt(i)!=' '&&word.charAt(i)!='\n'&&word.charAt(i)!='\t'&&word.charAt(i)!='\r') {
						break;
					}
				}
				word=word.substring(i);
				if(word.length()==0) {
					continue;
				}
				
				//System.out.println(now+"	"+word.length());
				//System.out.println("$$$$$"+word+"$$$$$$");
				
				first=word.charAt(0);
				if(first == '_' || Character.isLetter(first)) { //标识符
					getIdentifier();
				}
				else if(Character.isDigit(first)) {//整数,浮点数
					getIntOrFloat();
				}
				else if(first=='\'') { //字符
					getChar();
				}
				else if(first=='"') { //字符串
					getString();
				}
				else if(type.get(Character.toString(first))!=null) { //分界符,运算符,注释
					getSeparatorOrOperatorOrAnnotation();
				}
				else {
					throw new Exception(word+" 不可识别\n");
				}
			}
			in.close();
			out.close();
			System.out.println("词法分析完成");
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				in.close();
				out.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private static void getIdentifier() throws Exception{ //标识符或关键字判断
		if(Character.isDigit(now) || now == '_' || Character.isLetter(now)) {
			return; //继续读取下一个字符
		}
		else if(type.get(Character.toString(now))!=null || now==' '||now=='\n'||now=='\r'||now=='\t'){ 
			String temp=word.substring(0,word.length()-1);
			Integer value = type.get(temp);
			if(value != null) { //关键字
				try {
					out.write(temp+"\t<"+temp+" : "+value+">	"+lineNum+"\n");
					word=""+now; 
				}
				catch(IOException e){
					e.getStackTrace();
				}
			}
			else { //标识符
				try {
					out.write(temp+"\t<"+"标识符"+" : "+type.get("标识符")+">	"+lineNum+"\n");
					word=""+now; 
				}
				catch(IOException e){
					e.getStackTrace();
				}
			}
		}
		else {
			throw new Exception(word+"标识符错误");
		}
	}
	
	
	private static void getIntOrFloat() throws Exception{ //整数或浮点数
		if(Character.isDigit(now) || now=='e' || now=='.' || now=='E') {//这里默认了5.这种浮点数写法是正确的,.5这种是错误的
			return;
		}
		else if(type.get(Character.toString(now))!=null || now==' '||now=='\n'||now=='\r'||now=='f'||now=='F'||now=='l'||now=='L'){
			if(word.indexOf(".")==-1  && word.indexOf("f")==-1 && word.indexOf("F")==-1) {//整数
				String temp=word.substring(0,word.length()-1);
				try {
					out.write(temp+"\t<"+"整数"+" : "+type.get("整数")+">	"+lineNum+"\n");
					word=""+now;
				}
				catch(IOException e){
					e.getStackTrace();
				}
			}
			else { //浮点数
				if(word.charAt(0)!='e' && word.charAt(word.length()-1)!='e' && word.charAt(0)!='E' && word.charAt(word.length()-1)!='E') {//判断是否有e,E在两端的错误写法
					if(now=='f' || now=='F') {
						try {
							out.write(word+"\t<"+word+" : "+type.get("浮点数")+">	"+lineNum+"\n");
							word="";
						}
						catch(IOException e){
							e.getStackTrace();
						}
					}
					else {
						String temp=word.substring(0,word.length()-1);
						try {
							out.write(temp+"\t<"+word+" : "+type.get("浮点数")+">	 "+lineNum+"\n");
							word=""+now;
						}
						catch(IOException e){
							e.getStackTrace();
						}	
					}
				}
			}
		}
		else {
			throw new Exception(word+"整数或浮点数错误");
		}
	}
	
	private static void getChar() throws Exception{ //字符
		if(now=='\'') {//单个字符type.get(Character.toString(now))!=null || now==' '||now=='\n'||now=='\r'
			if(word.length()==3) {
				try {
					out.write(word+"\t<"+"字符"+" : "+type.get("字符")+">	"+lineNum+"\n");
					word="";
					return;
				}
				catch(IOException e){
					e.getStackTrace();
				}
			}
			else if(word.length()==4 ) {
				if(word.charAt(1)!='\\') {
					throw new Exception(word+"字符错误");
				}		
				try {
					out.write(word+"\t<"+"字符"+" : "+type.get("字符")+">	"+lineNum+"\n");
					word="";
					return;
				}
				catch(IOException e){
					e.getStackTrace();
				}
			}
		}
		if(word.length()>4) {
			throw new Exception(word+"字符错误");
		}
	}
	
	private static void getString() {//字符串
		if(now=='"' && word.length()>1) {
			try {
				out.write(word+"\t<"+"字符串"+" : "+type.get("字符串")+">	"+lineNum+"\n");
				word="";
			}
			catch(IOException e){
				e.getStackTrace();
			}
		}
	}
	
	private static void getSeparatorOrOperatorOrAnnotation() throws Exception{//运算符,分界符,注释
		if("{}[]();:.?,".indexOf(word.charAt(0))!=-1) { //不含->的分界符,->和运算符一起判断
			try {
				out.write(word.charAt(0)+"\t<"+word.charAt(0)+" : "+type.get(Character.toString(word.charAt(0)))+">	"+lineNum+"\n");
				word=word.substring(1);
				return;
			}
			catch(IOException e){
				e.getStackTrace();
			}
		}
		else {
			if(word.length()<2)return; 
			Integer value=type.get(word);
			if(value!=null) { //2个字符的运算符	
				try {
					out.write(word+"\t<"+word+" : "+value+">	"+lineNum+"\n");
					word="";
				}
				catch(IOException e){
					e.getStackTrace();
				}
			}
			else {
				String temp2=word.substring(0,2);
				if(temp2.equals("/*")) {//  /**/注释
					if(word.substring(word.length()-2,word.length()).equals("*/")) {
						word="";
						/*忽略注释
						try {
							out.write(word+"\t<"+"注释"+" : "+type.get("注释")+">	"+lineNum+"\n");
							word="";
						}
						catch(IOException e){
							e.getStackTrace();
						}*/
					}
				}
				else if(temp2.equals("//")) { // //注释
					if(now=='\n') { 
						word="";
						/*忽略注释
						try {
							out.write(word.substring(0,word.length()-1)+"\t<"+"注释"+" : "+type.get("注释")+">	"+lineNum+"\n");
							word="";
						}
						catch(IOException e){
							e.getStackTrace();
						}*/
					}
				}
				else { //1个字符的运算符
					String temp=word.substring(0,word.length()-1);
					try {
						out.write(temp+"\t<"+temp+" : "+type.get(temp)+">	"+lineNum+"\n");
						word=""+now;
					}
					catch(IOException e){
						e.getStackTrace();
					}	
				}	
			}
		}
	}

}
