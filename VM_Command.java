
public class VM_Command
{
	String comType, arg1;
	int arg2;
	
	public VM_Command(String comType, String arg1)
	{
		this.comType = comType;
		this.arg1 = arg1;
	}
	
	public VM_Command(String comType, String arg1, int arg2)
	{
		this.comType = comType;
		this.arg1 = arg1;
		this.arg2 = arg2;
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
