package gui;

public  class MutableInt {
	
	public MutableInt(int num)
	{
		value = num;
	}
	public MutableInt() {
		// TODO Auto-generated constructor stub
	}
	public int value = 1;
	public void increment(){++value;}
	public void decerement(){--value;}
	public int get() {return value;}
}
