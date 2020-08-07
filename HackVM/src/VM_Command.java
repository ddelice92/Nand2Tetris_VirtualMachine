
public class VM_Command
{
	String comType = null, arg1 = null;
	int arg2 = 0;
	boolean threeVar = false, twoVar = false;
	
	public VM_Command(String comType, String arg1)
	{
		this.comType = comType;
		this.arg1 = arg1;
		twoVar = true;
	}
	
	public VM_Command(String comType, String arg1, int arg2)
	{
		this.comType = comType;
		this.arg1 = arg1;
		this.arg2 = arg2;
		threeVar = true;
	}
	
	public VM_Command(String comType)
	{
		this.comType = comType;
	}
	
	public String toString()
	{
		String string;
		
		if(twoVar == true)
			string = comType + " " + arg1;
		else if(threeVar == true)
			string = comType + " " + arg1 + " " + arg2;
		else
			string = comType;
		
		return string;
	}
	
	public String getType()
	{
		return comType;
	}
	
	public String getArg1()
	{
		return arg1;
	}
	
	public int getArg2()
	{
		return arg2;
	}
}
