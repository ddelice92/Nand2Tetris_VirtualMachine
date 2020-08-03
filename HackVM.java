import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

public class HackVM
{
	static int jmpCount = 1;
	static String className;
	static String statVar, labelVar;
	static int statCount = 0;
	static Hashtable<String, Integer> hash = new Hashtable<String, Integer>();
	
	public static void main(String[] args) throws IOException
	{
		String codeFinal = "";
		String strfile;
		File file = new File(args[0]);
		
		BufferedReader in;
		strfile = file.toString();
		if(file.isDirectory())
		{
			String[] dirContent = file.list();
			for(String d : dirContent)
			{
				d = file.toString() + "\\" + d;
				if(d.substring(d.length() - 3, d.length()).equals(".vm"))
				{
					in = new BufferedReader(new FileReader(new File(d)));
					strfile = file.toString();
					className = d.substring(d.lastIndexOf("\\") + 1, d.length() - 3);
					if(codeFinal.isEmpty())
						codeFinal = coder(parser(in));
					else
						codeFinal = codeFinal.concat(coder(parser(in)));
				}
			}
		}
		else
		{
			if(strfile.substring(strfile.length() - 3, strfile.length()).equals(".vm"))
			{
				in = new BufferedReader(new FileReader(file));
				strfile = file.toString();
				className = strfile.substring(strfile.lastIndexOf("\\") + 1, strfile.length() - 3);
				
				if(codeFinal.isEmpty())
					codeFinal = coder(parser(in));
				else
					codeFinal = codeFinal.concat(coder(parser(in)));
			}
		}
		
		strfile = strfile.concat(strfile.substring(strfile.lastIndexOf("\\"))) + ".asm";
		System.out.println(strfile);
		File fileOut = new File(strfile);
		//check if file already exists
		if(fileOut.exists())
		{
			fileOut.delete();
			fileOut.createNewFile();
		}
		else
			fileOut.createNewFile();
		BufferedWriter out = new BufferedWriter(new FileWriter(fileOut));
		
		System.out.println(codeFinal);
		out.write(codeFinal);
		
		/*for(VM_Command c : comArrProp)
		{
			if(c.getType().equals("C_PUSH") || c.getType().equals("C_POP"))
			{
				strTemp = c.getType() + " : " + c.getArg1() + " : " + c.getArg2();
				System.out.println(strTemp);
				out.write(strTemp);
			}
			else if(c.getType().equals("C_ARITHMETIC"))
			{
				strTemp = c.getType() + " : " + c.getArg1();
				System.out.println(strTemp);
				out.write(strTemp);
			}
			else
			{
				strTemp = "UNSPECIFIED TYPE : " + c.getType() + " : " + c.getArg1();
				System.out.println(strTemp);
				out.write(strTemp);
			}
		}*/
		
		out.close();
	}
	
	public static VM_Command[] parser(BufferedReader read) throws IOException
	{
		//using array list at first because we do not yet know the amount of
		//functions to be parsed
		ArrayList<VM_Command> comlist = new ArrayList<VM_Command>();
		String strCurrent = read.readLine();
		String[] ppArr = new String[3], lgiArr = new String[2];
		VM_Command comTemp;
		VM_Command[] comArray;
		
		//stop reading at end of file
		while(strCurrent != null)
		{
			//ignore blank lines
			if(!strCurrent.isBlank())
			{
				//ignore comment lines
				if(!strCurrent.substring(0,2).equals("//"))
				{
					//ADD CONDITIONAL FOR COMMENTS ON COMMAND LINE
					strCurrent = strCurrent.trim();
					System.out.println(strCurrent + "<---white space");
					
					if(strCurrent.contains("push") || strCurrent.contains("pop"))
					{
						ppArr = strCurrent.split(" ");
						comTemp = new VM_Command("C_" + ppArr[0].toUpperCase(), ppArr[1], Integer.parseInt(ppArr[2].trim()));
						
						/*if(comTemp.getArg1().equals("static") && !hash.containsKey(statVar))
							hash.put(statVar, comTemp.getArg2());*/
					}
					else if(strCurrent.split(" ")[0].equals("label"))
					{
						lgiArr = strCurrent.split(" ");
						comTemp = new VM_Command("C_" + lgiArr[0].toUpperCase(), lgiArr[1]);
					}
					else if(strCurrent.split(" ")[0].equals("goto"))
					{
						lgiArr = strCurrent.split(" ");
						comTemp = new VM_Command("C_" + lgiArr[0].toUpperCase(), lgiArr[1]);
					}
					else if(strCurrent.split(" ")[0].equals("if-goto"))
					{
						lgiArr = strCurrent.split(" ");
						comTemp = new VM_Command("C_IF", lgiArr[1]);
					}
					//create object for arithmetic functions
					else
						comTemp = new VM_Command("C_ARITHMETIC", strCurrent);
					
					comlist.add(comTemp);
				}
			}
			
			//read next line
			strCurrent = read.readLine();
		}
		
		//this block changes arraylist of vmcommand objects to regular array
		comlist.trimToSize();
		comArray = new VM_Command[comlist.size()];
		for(int i = 0; i < comlist.size(); i++)
			comArray[i] = comlist.get(i);
		
		return comArray;
	}
	
	public static String coder(VM_Command[] comlist)
	{
		String asmTemp, asmFinal = null;
		for(int i = 0; i < Array.getLength(comlist); i++)
		{
			if(comlist[i].getType().equals("C_PUSH"))
			{
				statVar = className + "." + Integer.toString(comlist[i].getArg2());
				asmTemp = push(comlist[i].getArg1(), comlist[i].getArg2());
			}
			else if(comlist[i].getType().equals("C_POP"))
			{
				statVar = className + "." + Integer.toString(comlist[i].getArg2());
				asmTemp = pop(comlist[i].getArg1(), comlist[i].getArg2());
			}
			else if(comlist[i].getType().equals("C_LABEL"))
			{
				labelVar = className + "$" + comlist[i].getArg1();
				asmTemp = label(comlist[i].getArg1());
			}
			else if(comlist[i].getType().equals("C_GOTO"))
			{
				labelVar = className + "$" + comlist[i].getArg1();
				asmTemp = goTo(comlist[i].getArg1());
			}
			else if(comlist[i].getType().equals("C_IF"))
			{
				labelVar = className + "$" + comlist[i].getArg1();
				asmTemp = if_goTo(comlist[i].getArg1());
			}
			else
				asmTemp = arith(comlist[i].getArg1());
			if(i == 0)
				asmFinal = asmTemp;
			else
				asmFinal = asmFinal.concat(asmTemp);
		}
		
		return asmFinal;
	}
	
	public static String arith(String string)
	{
		String asm;
		
		switch(string)
		{
		case "add":
			asm = 	"//ADD\n" +
					"@SP\n" + 
					"AM=M-1\n" + 
					"D=M\n" + 
					"@SP\n" + 
					"AM=M-1\n" + 
					"M=D+M\n" + 
					"@SP\n" + 
					"M=M+1\n";
			break;
		case "sub":
			asm = 	"//SUB\n" +
					"@SP\n" + 
					"AM=M-1\n" + 
					"D=M\n" + 
					"@SP\n" + 
					"AM=M-1\n" + 
					"M=M-D\n" + 
					"@SP\n" + 
					"M=M+1\n";
			break;
		case "neg":
			asm = 	"//NEG\n" +
					"@SP\n" + 
					"AM=M-1\n" + 
					"M=-M\n" + 
					"@SP\n" + 
					"M=M+1\n";
			break;
		case "eq":
			asm = 	"//EQ\n" +
					"@SP\n" + 
					"AM=M-1\n" + 
					"D=M\n" + 
					"@SP\n" + 
					"AM=M-1\n" + 
					"D=D-M\n" + 
					"@J" + jmpCount + "\n" + 
					"D;JEQ\n" + 
					"@SP\n" +
					"A=M\n" +
					"M=0\n" + 
					"@J" + (jmpCount + 1) + "\n" + 
					"0;JMP\n" + 
					"(J" + jmpCount + ")\n" + 
					"@SP\n" + 
					"A=M\n" + 
					"M=-1\n" + 
					"(J" + (jmpCount + 1) + ")\n" +
					"@SP\n" +
					"M=M+1\n";
			jmpCount = jmpCount +2;
			break;
		case "gt":
			asm = 	"//GT\n" +
					"@SP\r\n" + 
					"AM=M-1\n" + 
					"D=M\n" + 
					"@SP\n" + 
					"AM=M-1\n" + 
					"D=M-D\n" + 
					"@J" + jmpCount + "\n" + 
					"D;JGT\n" + 
					"@SP\n" +
					"A=M\n" +
					"M=0\n" + 
					"@J" + (jmpCount + 1) + "\n" + 
					"0;JMP\n" + 
					"(J" + jmpCount + ")\n" + 
					"@SP\n" + 
					"A=M\n" + 
					"M=-1\n" + 
					"(J" + (jmpCount + 1) + ")\n" + 
					"@SP\n" + 
					"M=M+1\n";
			jmpCount = jmpCount +2;
			break;
		case "lt":
			asm = 	"//LT\n" +
					"@SP\n" + 
					"AM=M-1\n" + 
					"D=M\n" + 
					"@SP\n" + 
					"AM=M-1\n" + 
					"D=M-D\n" + 
					"@J" + jmpCount + "\n" + 
					"D;JLT\n" + 
					"@SP\n" +
					"A=M\n" +
					"M=0\n" + 
					"@J" + (jmpCount + 1) + "\n" + 
					"0;JMP\n" + 
					"(J" + jmpCount + ")\n" + 
					"@SP\n" + 
					"A=M\n" + 
					"M=-1\n" + 
					"(J" + (jmpCount + 1) + ")\n" + 
					"@SP\n" + 
					"M=M+1\n";
			jmpCount = jmpCount +2;
			break;
		case "and":
			asm = 	"//AND\n" +
					"@SP\n" + 
					"AM=M-1\n" + 
					"D=M\n" + 
					"@SP\n" + 
					"AM=M-1\n" + 
					"M=D&M\n" + 
					"@SP\n" + 
					"M=M+1\n";
			break;
		case "or":
			asm = 	"//OR\n" +
					"@SP\n" + 
					"AM=M-1\n" + 
					"D=M\n" + 
					"@SP\n" + 
					"AM=M-1\n" + 
					"M=D|M\n" + 
					"@SP\n" + 
					"M=M+1\n";
			break;
		case "not":
			asm = 	"//NOT\n" +
					"@SP\n" + 
					"AM=M-1\n" + 
					"M=!M\n" + 
					"@SP\n" + 
					"M=M+1\n";
			break;
		default:
			asm = "INVALID FUNCTION\n";
			break;
		}
		
		return asm;
	}
	
	public static String push(String string, int i)
	{
		String asm;
		
		switch(string)
		{
		case "constant":
			asm = 	"//PUSH CONSTANT\n" +
					"@" + i + "\n" +
					"D=A\n" +
					"@SP\n" +
					"A=M\n" +
					"M=D\n" +
					"@SP\n" +
					"M=M+1\n";
			break;
		case "local":
			asm = 	"//PUSH LOCAL\n" +
					"@" + i + "\n" +
					"D=A\n" +
					"@LCL\n" +
					"A=D+M\n" +
					"D=M\n" +
					"@SP\n" +
					"A=M\n" +
					"M=D\n" +
					"@SP\n" +
					"M=M+1\n";
			break;
		case "argument":
			asm = 	"//PUSH ARGUMENT\n" +
					"@" + i + "\n" +
					"D=A\n" +
					"@ARG\n" +
					"A=D+M\n" +
					"D=M\n" +
					"@SP\n" +
					"A=M\n" +
					"M=D\n" +
					"@SP\n" +
					"M=M+1\n";
			break;
		case "this":
			asm = 	"//PUSH THIS\n" +
					"@" + i + "\n" +
					"D=A\n" +
					"@THIS\n" +
					"A=D+M\n" +
					"D=M\n" +
					"@SP\n" +
					"A=M\n" +
					"M=D\n" +
					"@SP\n" +
					"M=M+1\n";
			break;
		case "that":
			asm = 	"//PUSH THAT\n" +
					"@" + i + "\n" +
					"D=A\n" +
					"@THAT\n" +
					"A=D+M\n" +
					"D=M\n" +
					"@SP\n" +
					"A=M\n" +
					"M=D\n" +
					"@SP\n" +
					"M=M+1\n";
			break;
		case "pointer":
			asm = 	"//PUSH POINTER\n" +
					"@" + i + "\n" +
					"D=A\n" +
					"@3\n" +
					"A=D+A\n" +
					"D=M\n" +
					"@SP\n" +
					"A=M\n" +
					"M=D\n" +
					"@SP\n" +
					"M=M+1\n";
			break;
		case "temp":
			asm = 	"//PUSH TEMP\n" +
					"@" + i + "\n" +
					"D=A\n" +
					"@5\n" +
					"A=D+A\n" +
					"D=M\n" +
					"@SP\n" +
					"A=M\n" +
					"M=D\n" +
					"@SP\n" +
					"M=M+1\n";
			break;
		//CHANGE NAMING CONVENTION OF STATIC VARIABLES
		case "static":
			asm = 	"//PUSH STATIC\n" +
					"@" + statVar + "\n" +
					"D=M\n" +
					"@SP\n" +
					"A=M\n" +
					"M=D\n" +
					"@SP\n" +
					"M=M+1\n";
			break;
		default:
			asm = "INVALID SEGMENT\n";
			break;
		}
		
		return asm;
	}
	
	public static String pop(String string, int i)
	{
		String asm;
		int point = 3 + i, temp = 5 + i;
		
		switch(string)
		{
		case "local":
			asm = 	"//POP LOCAL\n" +
					"@" + i + "\n" + 
					"D=A\n" + 
					"@LCL\n" + 
					"M=M+D\n" + 
					"@SP\n" + 
					"M=M-1\n" + 
					"A=M\n" + 
					"D=M\n" + 
					"@LCL\n" + 
					"A=M\n" + 
					"M=D\n" + 
					"@" + i + "\n" + 
					"D=A\n" + 
					"@LCL\n" + 
					"M=M-D\n";
			break;
		case "argument":
			asm = 	"//POP ARGUMENT\n" +
					"@" + i + "\n" + 
					"D=A\n" + 
					"@ARG\n" + 
					"M=M+D\n" + 
					"@SP\n" + 
					"M=M-1\n" + 
					"A=M\n" + 
					"D=M\n" + 
					"@ARG\n" + 
					"A=M\n" + 
					"M=D\n" + 
					"@" + i + "\n" + 
					"D=A\n" + 
					"@ARG\n" + 
					"M=M-D\n";
			break;
		case "this":
			asm = 	"//POP THIS\n" +
					"@" + i + "\n" + 
					"D=A\n" + 
					"@THIS\n" + 
					"M=M+D\n" + 
					"@SP\n" + 
					"M=M-1\n" + 
					"A=M\n" + 
					"D=M\n" + 
					"@THIS\n" + 
					"A=M\n" + 
					"M=D\n" + 
					"@" + i + "\n" + 
					"D=A\n" + 
					"@THIS\n" + 
					"M=M-D\n";
			break;
		case "that":
			asm = 	"//POP THAT\n" +
					"@" + i + "\n" + 
					"D=A\n" + 
					"@THAT\n" + 
					"M=M+D\n" + 
					"@SP\n" + 
					"M=M-1\n" + 
					"A=M\n" + 
					"D=M\n" + 
					"@THAT\n" + 
					"A=M\n" + 
					"M=D\n" + 
					"@" + i + "\n" + 
					"D=A\n" + 
					"@THAT\n" + 
					"M=M-D\n";
			break;
		case "pointer":
			asm = 	"//POP POINTER\n" +
					"@SP\n" +
					"AM=M-1\n" +
					"D=M\n" +
					"@" + point + "\n" +
					"M=D\n";
			break;
		case "temp":
			asm = 	"//POP TEMP\n" +
					"@SP\n" +
					"AM=M-1\n" +
					"D=M\n" +
					"@" + temp + "\n" +
					"M=D\n";
			break;
		case "static":
			asm = 	"//POP STATIC\n" +
					"@SP\n" +
					"AM=M-1\n" +
					"D=M\n" +
					"@" + statVar + "\n" +
					"M=D\n";
			break;
		default:
			asm = "INVALID SEGMENT\n";
			break;
		}
		
		return asm;
	}
	
	public static String label(String string)
	{
		String asm;
		asm = 	"//LABEL\n" +
				"(" + string + ")\n";
		
		return asm;
	}
	
	public static String goTo(String string)
	{
		String asm;
		asm = 	"//GOTO\n" +
				"@" + string + "\n" +
				"0;JMP\n";
		
		return asm;
	}
	
	public static String if_goTo(String string)
	{
		String asm;
		asm = 	"//IF_GOTO\n" +
				"@SP\n" +
				"AM=M-1\n" +
				"D=M\n" +
				"@" + string + "\n" +
				"D;JNE\n";
		
		return asm;
	}
}
