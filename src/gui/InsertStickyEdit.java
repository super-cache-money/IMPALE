package gui;

import gui.Residue.ResidueType;

public class InsertStickyEdit extends Edit {

//	Alignment al;
//	int respos, underlyingseq, deletepos;
	int deletepos;
	Residue.ResidueType insertres;

//	public InsertStickyEdit(int underlyingseq, int underlyingres, int deletePos, Residue.ResidueType insertres, Alignment al, boolean undo)
//	{
//		//this.underlyingres
//		super();
//	}


	public InsertStickyEdit(int underlyingseq,int underlyingres, 
			int deletepos, ResidueType insertres) {
		super();
//		this.al = al;
		this.respos = underlyingres;
		this.underlyingseqpos = underlyingseq;
		this.deletepos = deletepos;
		this.insertres = insertres;

	}
	

	@Override
	public void run() {
		
		
		Alignment.al.deleteResidueSticky(Alignment.al.inverseviewmap.get(underlyingseqpos), respos, deletepos -1);


		// TODO Auto-generated method stub
		
	}
	
	@Override
	public int getUnderlyingSeq() {
		return underlyingseqpos;
		// TODO Auto-generated method stub
		
	}

//	@Override
//	public Edit getInverse() {
//		// TODO Auto-generated method stub
//		return null;
//	}

}
