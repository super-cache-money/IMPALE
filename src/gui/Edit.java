package gui;

import java.io.Serializable;

public abstract class Edit implements Runnable, Serializable{
	
	public Residue.ResidueType restype;
	public int respos, underlyingseqpos;
	boolean insert;
	boolean sticky;
	public int extraposinfo;
	public Residue.ResidueType extraresinfo;
	//0 is delete, 1 is add

	public abstract int getUnderlyingSeq();
	//public abstract Edit getInverse();
	
	
	

}
