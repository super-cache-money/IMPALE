package gui;

import gui.Residue.ResidueType;



public class DeleteNormalEdit extends Edit {

//	public int underlyingseqpos, respos;
//	Alignment al;

	public Residue.ResidueType res;
	public DeleteNormalEdit(int underlyingseqpos, int respos, Residue.ResidueType res) {
		super();
		this.underlyingseqpos = underlyingseqpos;
		this.respos = respos;
		this.res = res;
		
		
	}

	
	










	@Override
	public void run() {
		
		Alignment.al.insertResidueNormal(Alignment.al.inverseviewmap.get(underlyingseqpos), respos, new Residue(res));
		// TODO Auto-generated method stub

	}













	@Override
	public int getUnderlyingSeq() {
		return underlyingseqpos;
		// TODO Auto-generated method stub
		
	}

}
