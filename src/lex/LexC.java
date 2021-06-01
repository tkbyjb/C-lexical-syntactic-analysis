package lex;

import java.io.*;
import java.util.Map;
import java.util.HashMap;

public class LexC {
	private static Map<String,Integer>  type = new HashMap<>();
	static{
		type.put("��ʶ��", 1);
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
		type.put("����"	,70);	type.put("������",	71);	type.put("�ַ�"	,72);		type.put("�ַ���"	,73);
		type.put("ע��"	,76	);
	}
	private static FileReader in; 
	private static FileWriter out; 
	private static int nowInt; 			//��ǰ�ַ���Ӧ����ֵ
	private static char now; 			//��ǰ�ַ�
	private static String word=""; 		//����
	private static int lineNum=1;//��ǰ������������
	/*
	 * ��ʱ�и�����:�����һ��Ϊע����û�л��н��޷�ʶ��
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
				for( i=0;i<word.length();i++) {//ȥ��wordͷ���Ŀո�
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
				if(first == '_' || Character.isLetter(first)) { //��ʶ��
					getIdentifier();
				}
				else if(Character.isDigit(first)) {//����,������
					getIntOrFloat();
				}
				else if(first=='\'') { //�ַ�
					getChar();
				}
				else if(first=='"') { //�ַ���
					getString();
				}
				else if(type.get(Character.toString(first))!=null) { //�ֽ��,�����,ע��
					getSeparatorOrOperatorOrAnnotation();
				}
				else {
					throw new Exception(word+" ����ʶ��\n");
				}
			}
			in.close();
			out.close();
			System.out.println("�ʷ��������");
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
	
	
	private static void getIdentifier() throws Exception{ //��ʶ����ؼ����ж�
		if(Character.isDigit(now) || now == '_' || Character.isLetter(now)) {
			return; //������ȡ��һ���ַ�
		}
		else if(type.get(Character.toString(now))!=null || now==' '||now=='\n'||now=='\r'||now=='\t'){ 
			String temp=word.substring(0,word.length()-1);
			Integer value = type.get(temp);
			if(value != null) { //�ؼ���
				try {
					out.write(temp+"\t<"+temp+" : "+value+">	"+lineNum+"\n");
					word=""+now; 
				}
				catch(IOException e){
					e.getStackTrace();
				}
			}
			else { //��ʶ��
				try {
					out.write(temp+"\t<"+"��ʶ��"+" : "+type.get("��ʶ��")+">	"+lineNum+"\n");
					word=""+now; 
				}
				catch(IOException e){
					e.getStackTrace();
				}
			}
		}
		else {
			throw new Exception(word+"��ʶ������");
		}
	}
	
	
	private static void getIntOrFloat() throws Exception{ //�����򸡵���
		if(Character.isDigit(now) || now=='e' || now=='.' || now=='E') {//����Ĭ����5.���ָ�����д������ȷ��,.5�����Ǵ����
			return;
		}
		else if(type.get(Character.toString(now))!=null || now==' '||now=='\n'||now=='\r'||now=='f'||now=='F'||now=='l'||now=='L'){
			if(word.indexOf(".")==-1  && word.indexOf("f")==-1 && word.indexOf("F")==-1) {//����
				String temp=word.substring(0,word.length()-1);
				try {
					out.write(temp+"\t<"+"����"+" : "+type.get("����")+">	"+lineNum+"\n");
					word=""+now;
				}
				catch(IOException e){
					e.getStackTrace();
				}
			}
			else { //������
				if(word.charAt(0)!='e' && word.charAt(word.length()-1)!='e' && word.charAt(0)!='E' && word.charAt(word.length()-1)!='E') {//�ж��Ƿ���e,E�����˵Ĵ���д��
					if(now=='f' || now=='F') {
						try {
							out.write(word+"\t<"+word+" : "+type.get("������")+">	"+lineNum+"\n");
							word="";
						}
						catch(IOException e){
							e.getStackTrace();
						}
					}
					else {
						String temp=word.substring(0,word.length()-1);
						try {
							out.write(temp+"\t<"+word+" : "+type.get("������")+">	 "+lineNum+"\n");
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
			throw new Exception(word+"�����򸡵�������");
		}
	}
	
	private static void getChar() throws Exception{ //�ַ�
		if(now=='\'') {//�����ַ�type.get(Character.toString(now))!=null || now==' '||now=='\n'||now=='\r'
			if(word.length()==3) {
				try {
					out.write(word+"\t<"+"�ַ�"+" : "+type.get("�ַ�")+">	"+lineNum+"\n");
					word="";
					return;
				}
				catch(IOException e){
					e.getStackTrace();
				}
			}
			else if(word.length()==4 ) {
				if(word.charAt(1)!='\\') {
					throw new Exception(word+"�ַ�����");
				}		
				try {
					out.write(word+"\t<"+"�ַ�"+" : "+type.get("�ַ�")+">	"+lineNum+"\n");
					word="";
					return;
				}
				catch(IOException e){
					e.getStackTrace();
				}
			}
		}
		if(word.length()>4) {
			throw new Exception(word+"�ַ�����");
		}
	}
	
	private static void getString() {//�ַ���
		if(now=='"' && word.length()>1) {
			try {
				out.write(word+"\t<"+"�ַ���"+" : "+type.get("�ַ���")+">	"+lineNum+"\n");
				word="";
			}
			catch(IOException e){
				e.getStackTrace();
			}
		}
	}
	
	private static void getSeparatorOrOperatorOrAnnotation() throws Exception{//�����,�ֽ��,ע��
		if("{}[]();:.?,".indexOf(word.charAt(0))!=-1) { //����->�ķֽ��,->�������һ���ж�
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
			if(value!=null) { //2���ַ��������	
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
				if(temp2.equals("/*")) {//  /**/ע��
					if(word.substring(word.length()-2,word.length()).equals("*/")) {
						word="";
						/*����ע��
						try {
							out.write(word+"\t<"+"ע��"+" : "+type.get("ע��")+">	"+lineNum+"\n");
							word="";
						}
						catch(IOException e){
							e.getStackTrace();
						}*/
					}
				}
				else if(temp2.equals("//")) { // //ע��
					if(now=='\n') { 
						word="";
						/*����ע��
						try {
							out.write(word.substring(0,word.length()-1)+"\t<"+"ע��"+" : "+type.get("ע��")+">	"+lineNum+"\n");
							word="";
						}
						catch(IOException e){
							e.getStackTrace();
						}*/
					}
				}
				else { //1���ַ��������
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
